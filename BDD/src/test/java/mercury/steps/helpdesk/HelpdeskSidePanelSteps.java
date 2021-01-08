package mercury.steps.helpdesk;

import static mercury.helpers.Globalisation.SITE_TIME;
import static mercury.helpers.Globalisation.assertDateTimeFormat;
import static mercury.helpers.Globalisation.assertTerminology;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.database.dao.IncidentDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.ResourceWorkingHoursDao;
import mercury.database.models.Incident;
import mercury.database.models.Job;
import mercury.database.models.ResourceWorkingHours;
import mercury.database.models.SiteView;
import mercury.databuilders.CallerContact;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.apihelper.ApiHelperHangfire;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.helpdesk.cards.ActiveCallerCard;
import mercury.pageobject.web.helpdesk.cards.CallerCard;
import mercury.pageobject.web.helpdesk.cards.JobCard;
import mercury.pageobject.web.helpdesk.cards.ResourceCard;
import mercury.pageobject.web.helpdesk.cards.SiteCard;
import mercury.runtime.RuntimeState;

public class HelpdeskSidePanelSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private ApiHelperHangfire apiHelperHangfire;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TzHelper tzHelper;
    @Autowired private OutputHelper outputHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;

    @Autowired private CallerContact callerContact;
    @Autowired private SiteView siteView;

    @Autowired private JobDao jobDao;
    @Autowired private IncidentDao incidentDao;
    @Autowired private ResourceWorkingHoursDao resourceWorkingHoursDao;


    @And("^the site card is selected$")
    public void select_site_card() throws IOException {
        SiteCard siteCard = new SiteCard(getWebDriver()).get();
        runtimeState.helpdeskSitePage = siteCard.selectCard();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^a site card is displayed$")
    public void the_site_card_is_displayed() throws Exception {

        apiHelperHangfire.rebuildAllMonitorCountData();

        SiteCard siteCard = new SiteCard(getWebDriver()).get();
        assertTrue(siteCard.isCardDisplayed());

        // assert headers
        assertEquals("Expected site name: " + siteView.getName(), normalize(siteView.getName()), normalize(siteCard.getSiteName()));

        String expected = siteView.getSiteType() + " " + siteView.getSiteCode();
        assertEquals("Expected site type code: " + expected, expected, siteCard.getSiteTypeAndCode());

        int siteId = siteView.getId();

        Date t1 = tzHelper.getCurrentTimeAtSite(siteId);
        String timeAtSite = DateHelper.dateAsString(t1, SITE_TIME);

        String displayedTimeAtSite = siteCard.getSiteTime();
        assertDateTimeFormat(SITE_TIME, displayedTimeAtSite);
        long gap = Math.abs(DateHelper.getDifferenceBetweenTwoTimes(displayedTimeAtSite, timeAtSite, SITE_TIME));
        assertTrue("Site time not withing last 2 minutes. Expected: " + timeAtSite + ", but was: " + displayedTimeAtSite, gap < 3);

        Date t2 = tzHelper.getCurrentTimeAtHomeOffice(siteId);
        String timeAtHomeOffice = DateHelper.dateAsString(t2, SITE_TIME);

        String displayedTimeAtHomeOffice = siteCard.getHomeOfficeTime();
        assertDateTimeFormat(SITE_TIME, displayedTimeAtHomeOffice);
        gap = Math.abs(DateHelper.getDifferenceBetweenTwoTimes(displayedTimeAtHomeOffice, timeAtHomeOffice, SITE_TIME));
        assertTrue("Home Office time expected: " + timeAtHomeOffice + "\nActual: " + displayedTimeAtHomeOffice + "\nDifference (minutes): " + gap, gap < 2);

        assertEquals("GSM: " + dbHelperSites.getGsmForSite(siteView.getId()), siteCard.getSubHeaderLeft());

        // assert address
        String expectedAddress = siteView.getAddress1();
        expectedAddress = StringUtils.isEmpty(siteView.getAddress2()) ? expectedAddress : appendToAddress(expectedAddress, siteView.getAddress2());
        expectedAddress = StringUtils.isEmpty(siteView.getAddress3()) ? expectedAddress : appendToAddress(expectedAddress, siteView.getAddress3());
        expectedAddress = StringUtils.isEmpty(siteView.getTown()) ? expectedAddress : appendToAddress(expectedAddress, siteView.getTown());
        expectedAddress = StringUtils.isEmpty(siteView.getCounty()) ? expectedAddress : appendToAddress(expectedAddress, siteView.getCounty());
        expectedAddress = StringUtils.isEmpty(siteView.getPostcode()) ? expectedAddress : appendToAddress(expectedAddress, siteView.getPostcode());

        String phoneNumber = StringUtils.isEmpty(siteView.getPhoneNumber()) ? "No Phone Number" : siteView.getPhoneNumber();
        expectedAddress = appendToAddress(expectedAddress, phoneNumber);
        assertEquals(normalize(expectedAddress), normalize(siteCard.getContent()));

        // assert icons
        List<Job> openJobs = jobDao.getOpenJobs(siteView.getId());
        int jobCount = siteCard.getWrenchCount();
        assertTrue("Expected number of open jobs to be " + openJobs.size(), openJobs.size() == jobCount);

        List<Incident> loggedIncidents = incidentDao.getLoggedIncidents(siteView.getId());
        int incidentCount = siteCard.getExclamationTriangleCount();
        assertTrue("Expected number of logged incidents to be " + loggedIncidents.size(), loggedIncidents.size() == incidentCount);
    }

    private String appendToAddress(String address, String newLine) {
        return newLine == null ? address : address + "\n" + newLine;
    }

    @ContinueNextStepsOnException
    @And("^a job card is displayed$")
    public void the_job_card_is_displayed() throws Exception {

        JobCard jobCard = new JobCard(getWebDriver()).get();

        logger.debug("Job Card Header: " + jobCard.getHeader());
        logger.debug("Job Card Sub Header (left): " + jobCard.getSubHeaderLeft());
        logger.debug("Job Card Sub Header (right): " + jobCard.getSubHeaderRight());
        logger.debug("Job Card Content: " + jobCard.getContent());

        assertTrue(jobCard.isCardDisplayed());

        if (testData.getString("jobReference") != null) {
            String expectedState = dbHelperJobs.getJobStatus(testData.getString("jobReference"));
            runtimeState.scenario.write("Asserting state is " + localize(expectedState));
            assertEquals(localize(expectedState), jobCard.getSubHeaderRight());
        }
    }

    @And("^a resource card is displayed$")
    public void the_resource_card_is_displayed() throws Exception {

        ResourceCard resourceCard = new ResourceCard(getWebDriver()).get();

        logger.debug("Job Card Header: " + resourceCard.getHeader());
        logger.debug("Job Card Sub Header (left): " + resourceCard.getSubHeaderLeft());
        logger.debug("Job Card Sub Header (right): " + resourceCard.getSubHeaderRight());
        logger.debug("Job Card Content: " + resourceCard.getContent());

        assertTrue(resourceCard.isCardDisplayed());

        // assert Resource Name
        assertTrue("Unexpected resource name", resourceCard.getHeader().contains(testData.getString("resourceName")));

        // assert line manager
        String lineManager = dbHelperResources.getLineManager(testData.getInt("resourceId"));
        String expectedLineManager ="Line Manager: ";
        expectedLineManager = lineManager == null ? expectedLineManager + "Not configured" : expectedLineManager + lineManager;
        runtimeState.scenario.write("Asserting: " + expectedLineManager);
        assertTrue("Not displayed: " + expectedLineManager, resourceCard.getContent().contains(expectedLineManager));

        // assert availability, 3 possibilities:
        // 1. availability presented as (from - to)
        // 2. availability is no work scheduled
        // 3. line not displayed
        // No BA, developer or tester has been able to instruct me on the logic behind each case above, hence only the format of the from - to is checked IF it is present!

        String displayedAvailability = resourceCard.getContent();
        if ( displayedAvailability.contains(")") ) {
            ResourceWorkingHours rwh = resourceWorkingHoursDao.getWorkingHoursForToday(testData.getInt("resourceId"));
            String startAt = rwh.getStartAt().replaceAll("\\..*", "");
            String endAt = rwh.getEndAt().replaceAll("\\..*", "");
            String expectedAvailability = "Availability: Available (" + startAt + " - " + endAt;
            runtimeState.scenario.write("Asserting availability is: " + expectedAvailability);
            assertTrue("Not displayed: " + expectedAvailability, resourceCard.getContent().contains(expectedAvailability));
        }

    }

    @ContinueNextStepsOnException
    @Then("^a caller card is displayed$")
    public void the_caller_card_is_displayed() throws Exception {
        CallerCard callerCard = new CallerCard(getWebDriver()).get();
        assertTrue(callerCard.isCardDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^a site card is not displayed$")
    public void the_site_card_is_not_displayed() throws Exception {
        SiteCard card = new SiteCard(getWebDriver());
        assertFalse(card.isCardDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^a job card is not displayed$")
    public void the_job_card_is_not_displayed() throws Exception {
        JobCard card = new JobCard(getWebDriver());
        assertFalse(card.isCardDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the icon tool tips are displayed$")
    public void the_icon_tips_are_displayed() throws Exception {
        List<String> iconToolTips = dbHelperSites.getIconToolTips(testData.getInt("siteId"));
        for (String icon : iconToolTips) {
            runtimeState.scenario.write("Asserting presence of icon tool tip: " + icon);
            assertTerminology(icon);
            runtimeState.helpdeskSitePage.hoverOverIcon(icon); // this asserts if the icon tool tip is present
            outputHelper.takeScreenshot();
        }
    }

    @ContinueNextStepsOnException
    @Then("^a caller card is not displayed$")
    public void the_caller_card_is_not_displayed() throws Exception {
        CallerCard card = new CallerCard(getWebDriver());
        assertFalse(card.isCardDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^a caller card is displayed showing call duration$")
    public void a_caller_card_is_displayed_showing_call_duration() throws Exception {

        ActiveCallerCard activeCallerCard = new ActiveCallerCard(getWebDriver()).get();

        assertTrue(activeCallerCard.isCardDisplayed());
        assertTrue(activeCallerCard.isTimerDisplayed());

        // assert timer in correct format and incrementing
        String time1 = activeCallerCard.getTimer();
        assertTrue(time1.matches("\\d+:\\d{2}"));
        Thread.sleep(2000); // do not delete this line - we must wait for the caller clock to progress
        // second
        String time2 = activeCallerCard.getTimer();
        assertTrue("Time is in unexpected format: " + time2, time2.matches("\\d+:\\d{2}"));
        assertTrue("Expected " + time2 + " to be >= 1 " + time1 + " + 1s", Integer.valueOf(time2.replaceAll(":", "")) - Integer.valueOf(time1.replaceAll(":", "")) >= 1);

        logger.debug("header: " + activeCallerCard.getHeader());
        logger.debug("sub header left: " + activeCallerCard.getSubHeaderLeft());
        logger.debug("sub header right: " + activeCallerCard.getSubHeaderRight());
        logger.debug("content: " + activeCallerCard.getContent());
        logger.debug("header: " + activeCallerCard.getPageTitle());

        String callerNameAndSite = null;
        String callerJobTitle = null;

        if (getWebDriver().getCurrentUrl().contains("Helpdesk#!/logjob")) {
            //cannot use normalize method on callerNameAndSite as it removes the new line
            List<Map<String, Object>> expectedCaller = dbHelperJobs.getCallerNameAndJobTitle(testData.getInt("jobreference"));
            callerNameAndSite = expectedCaller.get(0).get("Name").toString().trim().replaceAll("\\s{2,}", " ") + "\n"
                    + expectedCaller.get(0).get("SiteName").toString().trim().replaceAll("\\s{2,}", " ");
            callerJobTitle = expectedCaller.get(0).get("JobTitle").toString();
            assertEquals("unexpected card left sub header", normalize(callerJobTitle), activeCallerCard.getSubHeaderLeft().trim());
        } else {
            callerNameAndSite = normalize(callerContact.getName()) + "\n" + normalize(testData.getString("siteName"));
        }

        assertEquals("unexpected card header", callerNameAndSite, activeCallerCard.getHeader());
    }

    @And("^the caller card displays the number of active jobs$")
    public void the_caller_card_displays_the_number_of_active_jobs() throws Exception {
        ActiveCallerCard activeCallerCard = new ActiveCallerCard(getWebDriver()).get();
        int activeJobCount = Integer.valueOf(activeCallerCard.getContent().substring(0, activeCallerCard.getContent().indexOf(" ")));
        int expectedNumberOfActiveJobs = jobDao.getActiveJobs(siteView.getId()).size();

        try {
            assertTrue("Expected " + expectedNumberOfActiveJobs + " active jobs" , activeJobCount == expectedNumberOfActiveJobs);
        } catch (AssertionError e) {
            List<String> statuses = dbHelper.getJobStatuses(siteView.getId());
            runtimeState.scenario.write(statuses.toString().replaceAll(",", "\n").replaceAll("^\\[", " ").replaceAll("\\]$", ""));
            throw e;
        }
    }

    @And("^the caller card has been updated for the (?:new |)caller$")
    public void the_caller_card_has_been_updated_for_the_new_caller() throws Exception {
        CallerCard callerCard = new CallerCard(getWebDriver()).get();
        if (testData.getString("caller") != null) {
            runtimeState.scenario.write("Asserting caller is " + testData.getString("caller"));
            assertTrue( callerCard.getHeader().contains(testData.getString("caller").split("\\(")[0].trim()) );
        }
        if (testData.getString("jobRole") != null) {
            runtimeState.scenario.write("Asserting job role is " + testData.getString("jobRole"));
            assertTrue( callerCard.getHeader().contains(testData.getString("jobRole")) );
        }
        if (testData.getString("telephone") != null) {
            runtimeState.scenario.write("Asserting telephone is " + testData.getString("telephone"));
            assertTrue( callerCard.getContent().contains(testData.getString("telephone")) );
        }
        if (testData.getString("extension") != null) {
            runtimeState.scenario.write("Asserting extension is " + testData.getString("extension"));
            assertTrue( callerCard.getContent().contains("ext " + testData.getString("extension")) );
        }
    }
}
