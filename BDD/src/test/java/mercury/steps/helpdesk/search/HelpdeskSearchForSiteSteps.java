package mercury.steps.helpdesk.search;

import static mercury.helpers.Globalisation.SHORT;
import static mercury.helpers.Globalisation.assertDateTimeFormat;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.BrandWorkingHoursDao;
import mercury.database.dao.CallerDetailsDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.SiteViewDao;
import mercury.database.models.BrandWorkingHours;
import mercury.database.models.Job;
import mercury.database.models.SiteView;
import mercury.databuilders.CallerContact;
import mercury.databuilders.TestData;
import mercury.helpers.HelpdeskSearchHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.asserter.dbassertions.AssertBrandWorkingHours;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperWorkingHours;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.HelpdeskCoreDetails;
import mercury.runtime.RuntimeState;

public class HelpdeskSearchForSiteSteps {

    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private SiteView siteView;
    @Autowired private JobDao jobDao;
    @Autowired private SiteViewDao siteViewDao;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperWorkingHours dbHelperWorkingHours;
    @Autowired private BrandWorkingHoursDao brandWorkingHoursDao;
    @Autowired private CallerContact callerContact;
    @Autowired private CallerDetailsDao callerDetailsDao;
    @Autowired private AssertionFactory assertionFactory;
    @Autowired private HelpdeskSearchHelper helpdeskSearchHelper;
    @Autowired private PropertyHelper propertyHelper;

    private static final Logger logger = LogManager.getLogger();


    @When("^a search is run for site \"([^\"]*)\"$")
    public void a_search_is_run(String searchStr) throws Exception {

        siteView.copy(siteViewDao.getSiteByName(searchStr));

        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(searchStr);
        outputHelper.takeScreenshots();
    }

    @When("^a search is run for site \"([^\"]*)\" ((?:in|out of)) brand hours$")
    public void a_search_is_run_in_brand_hours(String searchStr, String hours) throws Throwable {

        siteView.copy(siteViewDao.getSiteByName(searchStr));

        if ("in".equals(hours)) {
            updateStoresBrandHours(siteView.getId(), "00:00:01", "23:59:59");
        } else {
            updateStoresBrandHours(siteView.getId(), "00:00:01", "00:00:02");
        }

        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(searchStr);
        outputHelper.takeScreenshots();
    }

    @When("^a search is run for an? \"([^\"]*)\" site$")
    public void a_search_is_run_for_a_site(String state) throws Exception {

        SiteView site = siteViewDao.getSiteByState(state);
        if (site == null) {
            site = siteViewDao.getRandomSite();
            logger.debug("Updating siteId " + site.getId() + " from " + site.getSiteStatus() + " to " + state);
            testData.put("originalSiteStatusId", site.getSiteStatusId());
            testData.put("siteId", site.getId());
            Integer siteStatusId = dbHelperSites.getSiteStatusId(state);
            dbHelperSites.updateSiteStatus(site.getId(), siteStatusId);
        }

        siteView.copy(site);
        testData.put("siteId", siteView.getId());
        testData.put("siteName", normalize(siteView.getName()));
        runtimeState.scenario.write("Searching for: " + normalize(siteView.getName()));

        testData.put("siteName", siteView.getName());
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteView.getName());
        outputHelper.takeScreenshots();
    }

    @When("^a search is run for an? \"([^\"]*)\" contract store with an existing caller$")
    public void a_search_is_run_for_a_site_with_caller(String state) throws Exception {

        siteView.copy(siteViewDao.getRandomContractStoreWithCaller(state));

        runtimeState.scenario.write("Searching for: " + siteView.getName());

        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteView.getName());
        outputHelper.takeScreenshots();
    }

    @When("^((?:an|a search is run for an)) \"([^\"]*)\" City Tech store with an existing caller$")
    public void a_search_is_run_for_City_Tech_store_with_caller(String action, String state) throws Exception {

        siteView.copy(siteViewDao.getRandomCityTechStoreWithCaller(state));

        testData.put("siteName", siteView.getName());
        testData.put("siteId", siteView.getId());

        if ("A SEARCH IS RUN FOR AN".equalsIgnoreCase(action)) {
            runtimeState.scenario.write("Searching for: " + siteView.getName());

            runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteView.getName());
            outputHelper.takeScreenshots();
        }
    }

    @When("^((?:an|a search is run for an)) \"([^\"]*)\" City Tech store with an existing City Tech caller$")
    public void a_search_is_run_for_City_Tech_store_with_City_Tech_caller(String action, String state) throws Exception {

        siteView.copy(siteViewDao.getRandomCityTechStoreWithCityTechCaller(state));

        testData.put("siteName", siteView.getName());
        testData.put("siteId", siteView.getId());

        if ("A SEARCH IS RUN FOR AN".equalsIgnoreCase(action)) {
            runtimeState.scenario.write("Searching for: " + siteView.getName());

            runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteView.getName());
            outputHelper.takeScreenshots();
        }
    }

    @When("^a search is run for a contract store$")
    public void a_search_is_run_for_a_contract_store() throws Exception {

        siteView.copy(siteViewDao.getContractStores().get(0));

        runtimeState.scenario.write("Searching for: " + siteView.getName());

        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteView.getName());
        outputHelper.takeScreenshots();
    }

    @When("^a search is run for an occupied site and an existing caller$")
    public void a_search_is_run_for_a_site_and_an_existing_caller() throws Throwable {
        callerContact.copy(callerDetailsDao.getRandomClientCallerWithSite("occupied"));

        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(callerContact.getSiteName());

        if (getWebDriver().getCurrentUrl().contains("search/site")) {

            String searchStr = "";
            if ("Resource".equals(callerContact.getCallerType())) {
                searchStr = callerContact.getName() + " - " + callerContact.getDepartment();
            } else {
                searchStr = callerContact.getName() + " (" + callerContact.getDepartment() + " - " + callerContact.getSiteName() + ")";
            }

            runtimeState.scenario.write("Searching for: " + searchStr);

            if (callerDetailsDao.getClientCallerByName(callerContact.getName()) != null) {
                runtimeState.helpdeskCallerPage = runtimeState.helpdeskSearchBar.searchForCaller(searchStr);
            } else {
                runtimeState.helpdeskResourcePage = runtimeState.helpdeskSearchBar.searchForResource(searchStr);
            }

        }
        outputHelper.takeScreenshots();
    }

    @When("^a search is run for an occupied site with an existing caller$")
    public void a_search_is_run_for_a_site_with_an_existing_caller() throws Throwable {
        callerContact.copy(callerDetailsDao.getRandomClientCallerWithSite("occupied"));
        runtimeState.scenario.write("Callername : " + callerContact.getName());
        testData.put("siteName", callerContact.getSiteName());
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(callerContact.getSiteName());
        outputHelper.takeScreenshots();
    }

    public void updateStoresBrandHours(int siteId, String startAt, String endAt) throws Exception {
        BrandWorkingHours bwh = brandWorkingHoursDao.getBrandWorkingHoursForSiteId(siteId);
        dbHelperWorkingHours.updateBrandWorkingHours(bwh.getId(), startAt, endAt);

        // wait
        AssertBrandWorkingHours assertBrandWorkingHours = new AssertBrandWorkingHours(bwh.getId(), startAt, endAt, brandWorkingHoursDao);
        assertionFactory.performAssertion(assertBrandWorkingHours);

        runtimeState.scenario.write(String.format("Updated BrandWorkingHours [id=%d, dayOfTheWeek=%d, startAt=%s, endAt=%s]", bwh.getId(), bwh.getDayOfTheWeek(), startAt, endAt));
    }

    @ContinueNextStepsOnException
    @Then("^a new tab is opened with the site details$")
    public void a_new_tab_is_opened_with_the_site_details() throws Exception {

        runtimeState.helpdeskCoreDetails = new HelpdeskCoreDetails(getWebDriver()).get();

        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Site Type"));
        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Site Code"));
        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Status"));

        SiteView site = siteViewDao.get(testData.getInt("siteId"));
        assertEquals(site.getSiteCode(), runtimeState.helpdeskCoreDetails.getCoreValue("Site Code"));
        assertEquals(dbHelperSites.getSiteStatusName(site.getSiteStatusId()), runtimeState.helpdeskCoreDetails.getCoreValue("Status"));
    }

    @ContinueNextStepsOnException
    @Then("^site details are displayed$")
    public void site_details_are_displayed() {
        SiteView site = siteViewDao.get(testData.getInt("siteId"));

        assertTrue(runtimeState.helpdeskSitePage.getHeadline().contains(siteView.getName()));
        assertEquals(siteView.getSiteCode(), runtimeState.helpdeskSitePage.getSubHeadline());
        assertEquals("Site Status: " + site.getSiteStatus(), runtimeState.helpdeskSitePage.getSubHeadlineRight());

        runtimeState.helpdeskCoreDetails = new HelpdeskCoreDetails(getWebDriver()).get();

        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Site Type"));
        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Site Code"));
        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Status"));

        assertEquals(site.getSiteType(), runtimeState.helpdeskCoreDetails.getCoreValue("Site Type"));
        assertEquals(site.getSiteCode(), runtimeState.helpdeskCoreDetails.getCoreValue("Site Code"));
        assertEquals(site.getSiteStatus(), runtimeState.helpdeskCoreDetails.getCoreValue("Status"));
    }

    @And("^the search bar contains a lozenge with site name and code$")
    public void the_search_bar_contains_a_lozenge_with_site_name_and_code() {

        String title = siteView.getName() + " - " + siteView.getSiteCode();
        assertTrue("Expected lozinge to be displayed: " + title, runtimeState.helpdeskSearchBar.isLozengeDisplayed(title));
    }

    @And("^the search bar contains lozenges for site and caller$")
    public void the_search_bar_contains_lozenges_for_site_and_caller() {

        String title = siteView.getName() + " - " + siteView.getSiteCode();
        assertTrue("Expected lozinge to be displayed: " + title, runtimeState.helpdeskSearchBar.isLozengeDisplayed(title));

        String str = callerContact.getName() + " (" + callerContact.getDepartment() + " - " + callerContact.getSiteName() + ")";
        assertTrue("Expected lozinge to be displayed: " + str, runtimeState.helpdeskSearchBar.isLozengeDisplayed(str));
    }

    @ContinueNextStepsOnException
    @Then("^open jobs for the site are displayed$")
    public void open_jobs_for_the_site_are_displayed() throws Throwable {
        runtimeState.helpdeskOpenJobsTab = runtimeState.helpdeskSitePage.getOpenJobs();
        Grid grid = runtimeState.helpdeskOpenJobsTab.getGrid();
        String[] expectedHeaders = null;

        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");

        if (propertyHelper.getMercuryUrl().contains("-usad")) {
            String[] expectedOpenJobHeaders = { "Logged", "Job Ref", "Job Type", "Subtype/Classification", "Serial Number", "Priority", "Resources Assigned", "Date assigned", "Status" };
            expectedHeaders = expectedOpenJobHeaders;
        } else {
            String[] expectedOpenJobHeaders = { "Logged", "Job Ref", "Job Type", "Subtype/Classification", "Serial Number", "Priority", "Resources Assigned", "Date assigned", "Status" };
            expectedHeaders = expectedOpenJobHeaders;
        }

        for (String header : grid.getHeaders()) {
            assertTrue("Expected " + expectedHeaders.toString() + " to contain " + header, ArrayUtils.contains(expectedHeaders, header));
        }
        assertTrue("Unexpected number of headers returned", expectedHeaders.length == grid.getHeaders().size());

        List<Job> jobList = jobDao.getOpenJobs(siteView.getId());
        int displayedNumberOfItems = runtimeState.helpdeskOpenJobsTab.getNumberOfItems();
        assertTrue("Expected " + jobList.size() + " items to be listed, but " + displayedNumberOfItems + " displayed", jobList.size() == displayedNumberOfItems);

        for (Row row : grid.getRows()) {
            assertDateTimeFormat(SHORT, row.getCell("Logged").getText());
        }

    }

    @When("^a search is run for an? \"([^\"]*)\" site having logged incidents in last 30 days$")
    public void a_search_is_run_for_a_site_having_logged_incidents_in_last_30_days(String state) throws Exception {
        helpdeskSearchHelper.searchForSiteHavingLoggedIncidentsInLast30_Days(state);
    }

    @When("^a search is run for job reference \"([^\"]*)\"$")
    public void a_search_is_run_for_job_reference(String jobReference) throws Exception {
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(jobReference);
        outputHelper.takeScreenshots();
    }
}
