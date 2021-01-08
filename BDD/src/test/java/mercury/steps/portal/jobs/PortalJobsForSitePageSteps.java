package mercury.steps.portal.jobs;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.WebDriverHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.LoginPage;
import mercury.pageobject.web.portal.jobs.PortalJobsForSitePage;
import mercury.runtime.RuntimeState;


public class PortalJobsForSitePageSteps {
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private OutputHelper outputHelper;


    @Given("^is within GEO radius$")
    public void is_within_GEO_radius() throws Throwable {
        int jobReference = testData.getInt("jobReference");
        Map<String, Object> dbData = dbHelperSites.getGeoLocationForJobReference(jobReference);
        int siteId = dbHelperSites.getSiteIdForJobRef(jobReference);
        testData.put("siteId", siteId);

        double latitude = (Double) dbData.get("Latitude");
        double longitude = (Double) dbData.get("Longitude");

        runtimeState.scenario.write("Latitude: " + latitude + ", Longitude: " + longitude);
        POHelper.setGeolocation(latitude, longitude);
    }

    public void is_within_GEO_radius(int jobReference) throws Throwable {
        Map<String, Object> dbData = dbHelperSites.getGeoLocationForJobReference(jobReference);
        int siteId = dbHelperSites.getSiteIdForJobRef(jobReference);
        testData.put("siteId", siteId);

        double latitude = (Double) dbData.get("Latitude");
        double longitude = (Double) dbData.get("Longitude");

        runtimeState.scenario.write("Latitude: " + latitude + ", Longitude: " + longitude);
        POHelper.setGeolocation(latitude, longitude);
    }

    @Given("^is on site and has started the job$")
    public void is_on_site_and_has_started_the_job() throws Throwable {
        is_within_GEO_radius();

        jobCreationHelper.startContractorJob(testData.getInt("jobReference"), testData.getInt("resourceId"), "reactive");
    }

    @Given("^is not within GEO radius$")
    public void not_within_GEO_Radius() throws Throwable {
        runtimeState.scenario.write("Latitude: 90.000, Longitude: 0.000");
        POHelper.setGeolocation(90, 0);     // North Pole
    }

    @Given("^I have found my geolocation$")
    public void i_have_found_my_geolocation() throws Throwable {
        String geoLocationURL = "https://www.geolocation.com/";
        WebDriverHelper webDriverHelper = new WebDriverHelper();
        WebDriver geoDriver = webDriverHelper.getNewWebDriver();
        geoDriver.get(geoLocationURL);
        String data = StringUtils.substringBetween(geoDriver.getPageSource(), "https://www.weatherdatasource.com/", "\" target");
        String [] geoData = data.split("/");
        testData.addStringTag("currentLatitude", geoData[0]);
        testData.addStringTag("currentLongitude", geoData[1]);
        runtimeState.scenario.write( "Current location Lat and Long are : " + data);
        geoDriver.close();
    }

    @ContinueNextStepsOnException
    @Then("^the user is returned to Jobs for site table$")
    public void the_user_is_returned_to_Jobs_for_site_table() throws Throwable {
        runtimeState.portalJobsForSitePage.isPageTitleDisplayed();
    }

    @ContinueNextStepsOnException
    @Then("^the Jobs for Site page is displayed$")
    public void the_jobs_for_site_page_is_displayed() throws Throwable {
        if (runtimeState.portalJobsForSitePage == null) runtimeState.portalJobsForSitePage = new PortalJobsForSitePage(getWebDriver()).get();
        assertTrue("Jobs for Site not displayed", runtimeState.portalJobsForSitePage.isPageTitleDisplayed());
    }


    @ContinueNextStepsOnException
    @Then("^the job In Progress can be stopped$")
    public void the_job_In_Progress_can_be_stopped() {
        assertTrue("Expected button to be displayed: Stop Work", runtimeState.portalJobsForSitePage.isStopWorkButtonDisplayed(testData.getString("jobInProgress")));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Allocated job can be started$")
    public void the_Allocated_job_can_be_started() {
        assertTrue("Expected button to be displayed: Start Work", runtimeState.portalJobsForSitePage.isStartWorkButtonDisplayed(testData.getString("jobAllocated")));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the updated job \"([^\"]*)\" displayed on the Jobs for Site page$")
    public void the_updated_job_is_not_displayed_on_Jobs_for_Site(String condition) {
        boolean isJobExpectedOnPage = !condition.equals("is not");
        if (isJobExpectedOnPage) {
            String jobReference = testData.getString("jobReference");
            assertTrue("Job not found on grid: " + jobReference, runtimeState.portalJobsForSitePage.isJobReferenceDisplayed(jobReference));
        } else {
            if (runtimeState.portalJobsForSitePage.isAnyJobsForSite()) {
                String jobReference = testData.getString("jobReference");
                assertFalse("Job found on grid: " + jobReference, runtimeState.portalJobsForSitePage.isJobReferenceDisplayed(jobReference));
            }
        }
    }

    @Given("^user is logged out of portal$")
    public void user_is_logged_out_of_portal() throws Throwable {
        runtimeState.loginPage = new LoginPage(getWebDriver()).get();
        assertTrue(runtimeState.loginPage.isDisplayed());
    }

    @Given("^has started the job$")
    public void i_have_started_the_job() throws Throwable {
        jobCreationHelper.startContractorJob(testData.getInt("jobReference"), testData.getInt("resourceId"), "reactive");
    }

    @ContinueNextStepsOnException
    @Then("^the open jobs can be started on multiple sites$")
    public void the_open_jobs_can_be_started_on_multiple_sites() {
        if (runtimeState.portalJobsForSitePage == null) runtimeState.portalJobsForSitePage = new PortalJobsForSitePage(getWebDriver()).get();
        for (int jobReference : testData.getIntList("jobReferences")) {
            runtimeState.portalJobsForSitePage.isJobReferenceOnGrid(jobReference);
        }
    }
}


