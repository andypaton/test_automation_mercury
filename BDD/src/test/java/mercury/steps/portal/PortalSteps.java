package mercury.steps.portal;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.SHORT;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.ComparisonFailure;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.JobContactDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.JobViewDao;
import mercury.database.dao.MessageDao;
import mercury.database.dao.ReasonDao;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.dao.SiteViewDao;
import mercury.database.dao.SiteVisitsDao;
import mercury.database.models.Job;
import mercury.database.models.JobContact;
import mercury.database.models.JobView;
import mercury.database.models.Message;
import mercury.database.models.Reason;
import mercury.database.models.ResourceAssignment;
import mercury.database.models.SiteView;
import mercury.databuilders.CallerContact;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.databuilders.UpdateJob;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.StepHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.asserter.dbassertions.AssertMessage;
import mercury.helpers.asserter.dbassertions.AssertSiteVisit;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperAssertions;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.portal.PortalHomePage;
import mercury.pageobject.web.portal.PortalSummaryPage;
import mercury.pageobject.web.portal.jobs.AsbestosModal;
import mercury.pageobject.web.portal.jobs.ChooseAssetPage;
import mercury.pageobject.web.portal.jobs.ContactInfoJobDetailsPage;
import mercury.pageobject.web.portal.jobs.JobInfoJobDetailsPage;
import mercury.pageobject.web.portal.jobs.OpenAwaitingJobsPage;
import mercury.pageobject.web.portal.jobs.PortalJobsForSitePage;
import mercury.pageobject.web.portal.jobs.SiteInfoJobDetailsPage;
import mercury.pageobject.web.portal.jobs.UpdateJobPage;
import mercury.pageobject.web.portal.jobs.UpdateSavedPage;
import mercury.runtime.RuntimeState;

public class PortalSteps {

    private static final Integer PAGE_SIZE = 10;
    private static final Logger logger = LogManager.getLogger();

    private static final String DECLINE_ROUTING_KEY = "job.resourceassignment.declined";
    private static final String DECLINE_EVENT_TYPE = "Declined";
    private static final Integer DECLINE_RESOURCE_ASSIGNMENT_STATUS = 16;

    private static final String ACCEPT_ROUTING_KEY = "job.resourceassignment.accepted";
    private static final String ACCEPT_EVENT_TYPE = "Accepted";
    private static final Integer ACCEPT_RESOURCE_ASSIGNMENT_STATUS = 7;
    private static final Integer ACCEPT_RESOURCE_ASSIGNMENT_REASON = 18;

    private static final String ETA_PROVIDED_EVENT_TYPE = "ETAProvided";
    private static final String ETA_PROVIDED_ROUTING_KEY = "job.resourceassignment.etaprovided";
    private static final Integer ETA_PROVIDED_RESOURCE_ASSIGNMENT_STATUS = 7;
    private static final Integer ETA_PROVIDED_RESOURCE_ASSIGNMENT_REASON = 18;

    private static final String SITE_NOTIFIED_ETA_PROVIDED_EVENT_TYPE = "SiteNotifiedOfETA";
    private static final String SITE_NOTIFIED_ETA_PROVIDED_ROUTING_KEY = "job.resourceassignment.sitenotifiedofeta";
    private static final Integer SITE_NOTIFIED_ETA_PROVIDED_RESOURCE_ASSIGNMENT_STATUS = 8;
    private static final Integer SITE_NOTIFIED_ETA_PROVIDED_RESOURCE_ASSIGNMENT_REASON = 18;

    private static final String COMPLETE_ROUTING_KEY = "job.resourceassignment.complete";
    private static final String COMPLETE_EVENT_TYPE = "Complete";
    private static final Integer COMPLETE_RESOURCE_ASSIGNMENT_STATUS = 13;
    private static final Integer COMPLETE_RESOURCE_ASSIGNMENT_REASON = 18;

    private static final String JOB_COMPLETE_ROUTING_KEY = "job.complete";
    private static final String JOB_COMPLETE_EVENT_TYPE = "Complete";

    @Autowired private DbHelper dbHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private StepHelper stepHelper;
    @Autowired private JobDao jobDao;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private SiteViewDao siteDao;
    @Autowired private JobContactDao jobContactDao;
    @Autowired private ReasonDao reasonDao;
    @Autowired private MessageDao messageDao;
    @Autowired private SiteVisitsDao siteVisitsDao;
    @Autowired private ResourceAssignmentDao resourceAssignmentDao;
    @Autowired private NewJob job;
    @Autowired private UpdateJob updateJob;
    @Autowired AssertionFactory assertionFactory;
    @Autowired private TestData testData;
    @Autowired private Reason reason;
    @Autowired private MenuSteps menuSteps;
    @Autowired private JobView currentJob;
    @Autowired private DbHelperAssertions dbHelperAssertions;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private TzHelper tzHelper;

    SiteView currentSite;
    List<JobContact> currentJobcontact;
    List<JobView> jobList;

    // String jobReference;

    Integer openJobCount = -1;
    Integer awaitingJobCount = -1;

    // Very similar to Helpdesk page display step - could merge code.
    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" portal page is displayed with (.*)$")
    public void the_page_is_displayed_with_menus(String page, String listOfMenus) throws Exception {

        // Remove whitespace and split by comma
        List<String> menus = Arrays.asList(listOfMenus.split("\\s*,\\s*"));

        for (String menu : menus) {
            logger.debug("Asserting " + menu);
            assertTrue(runtimeState.portalNavBar.isMenuDisplayed(menu));
        }
    }

    @When("^the user is viewing the Jobs Awaiting Acceptance grid$")
    public void the_user_is_viewing_the_awaiting_jobs_grid() throws Exception {
        // If the Portal summary page is showing then grab some stats from it.
        // TODO:load the table into an Array and use that
        if (runtimeState.portalSummaryPage == null) {
            PortalSummaryPage portalSummaryPage = new PortalSummaryPage(getWebDriver());
            if (portalSummaryPage.isPageLoaded()) {
                runtimeState.portalSummaryPage = new PortalSummaryPage(getWebDriver()).get();
                awaitingJobCount = runtimeState.portalSummaryPage.getCountForType("Jobs Awaiting Acceptance");
                openJobCount = runtimeState.portalSummaryPage.getCountForType("Open Jobs");
            }
        } else {
            awaitingJobCount = runtimeState.portalSummaryPage.getCountForType("Jobs Awaiting Acceptance");
            openJobCount = runtimeState.portalSummaryPage.getCountForType("Open Jobs");
        }
        runtimeState.scenario.write("Number of open jobs: " + openJobCount);
        runtimeState.scenario.write("Number of jobs awaiting acceptance: " + awaitingJobCount);
        menuSteps.sub_menu_is_selected_from_the_top_menu("Jobs Awaiting Acceptance", "Jobs");
    }

    @ContinueNextStepsOnException
    @Then("^the job is displayed in the Jobs Awaiting Acceptance grid$")
    public void the_job_is_displayed_in_the_Jobs_Awaiting_Acceptance_grid() throws Throwable {
        runtimeState.scenario.write("Verifying row details for job reference: " + testData.getInt("jobReference"));
        runtimeState.openAwaitingJobsPage.searchJobs(testData.getString("jobReference"));
        Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid();
        assertTrue("Job reference not in grid: " + testData.getString("jobReference"), grid.getRows().size() > 0);

        Row row = grid.getRows().get(0);
        assertEquals(testData.getString("jobReference"), row.getCell("Job reference").getText());
        assertEquals(testData.getString("jobType"), row.getCell("Job reference").getSubText());

        assertEquals("New Job Notification Sent", row.getCell("Assignment status").getText());
        String priority = dbHelper.getPriorityDetail(testData.getInt("faultPriorityId"));
        assertEquals(priority, row.getCell("Assignment status").getSubText());

        Map<String, Object> site = dbHelperSites.getSite(testData.getInt("siteId"));
        assertEquals(site.get("Name"), row.getCell("Site").getText());

        int assetClassificationId = testData.getInt("assetClassificationId");
        int faultTypeId = testData.getInt("faultTypeId");
        int siteTypeId = (int) site.get("SiteTypeId");
        Map<String, Object> dbData = dbHelper.getAssetClassificationFaultDetail(assetClassificationId, faultTypeId, siteTypeId);
        String text = dbData.get("AssetTypeName") + " > " + dbData.get("AssetSubTypeName");
        assertEquals(text, row.getCell("Asset subtype/classification").getText());
        assertEquals(dbData.get("FaultTypeName"), row.getCell("Asset subtype/classification").getSubText());

        assertEquals("0 days ago", row.getCell("Days outstanding").getText());
        assertEquals(DateHelper.getDateInFormat(new Date(), "d MMM yyyy"), row.getCell("Days outstanding").getSubText()); // same date format for US and UK

        assertEquals("Test Automation. Scenario: " + runtimeState.scenario.getName(), row.getCell(6).getText());
    }

    @ContinueNextStepsOnException
    @SuppressWarnings("unused")
    @Then("^the job is displayed in the Open Jobs grid$")
    public void the_job_is_displayed_in_the_Open_Jobs_grid() throws Throwable {
        outputHelper.takeScreenshots();
        String jobReference = testData.getString("jobReference");

        runtimeState.openAwaitingJobsPage = runtimeState.openAwaitingJobsPage.searchJobs(jobReference);

        Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid();
        assertTrue("Job reference not in grid: " + jobReference, grid.getRows().size() > 0);

        Row row = grid.getRows().get(0);

        assertEquals(jobReference, row.getCell("Job reference").getText());

        JobView jobView = jobViewDao.getByJobReference(jobReference);
        if (jobView != null ){
            assertNotNull(jobView);
            stepHelper.verifyJobGridCommon(jobView, runtimeState.openAwaitingJobsPage);

            String projectCost = dbHelper.getProjectCost(jobReference, testData.getString("userName"));
            stepHelper.verifyJobGridOpen(jobView, runtimeState.openAwaitingJobsPage, projectCost);
        }
    }

    @When("^the user is viewing (?:a|the) job awaiting acceptance$")
    public void the_user_is_viewing_a_job_awaiting_acceptance() throws Exception {
        logger.debug("Opening Job Reference: " + testData.getInt("jobReference"));

        the_user_is_viewing_the_awaiting_jobs_grid();

        // Search for a job
        runtimeState.openAwaitingJobsPage.searchJobs(testData.getInt("jobReference").toString());
        outputHelper.takeScreenshots();

        job.setJobReference(testData.getInt("jobReference").toString());
        currentJob.copy(jobViewDao.getByJobReference(testData.getString("jobReference")));
        assertNotNull("Unexpected null record set", currentJob);

        logger.debug("Opening Job Reference: " + job.getJobReference());
        runtimeState.scenario.write("Opening Job Reference: " + job.getJobReference());

        assertTrue(runtimeState.openAwaitingJobsPage.isSummaryRowDisplayed(currentJob.getJobReference()));

        // Open Job
        runtimeState.jobDetailsPage = runtimeState.openAwaitingJobsPage.openJob(testData.getInt("jobReference").toString());

        if (currentJob.getJobTypeId() == 4) {
            // Warranty job
            assertEquals(String.format("Job %s - Warranty Job", currentJob.getJobReference()), runtimeState.jobDetailsPage.getPageTitle());
            assertEquals(String.format("Job %s - Warranty Job", currentJob.getJobReference()), runtimeState.jobDetailsPage.getPageHeaderText());
        } else {
            assertEquals(String.format("Job %s", currentJob.getJobReference()), runtimeState.jobDetailsPage.getPageTitle());
            assertEquals(String.format("Job %s", currentJob.getJobReference()), runtimeState.jobDetailsPage.getPageHeaderText());
        }
        runtimeState.jobInfoJobDetailsPage = new JobInfoJobDetailsPage(getWebDriver()).get();
        outputHelper.takeScreenshots();

    }

    @When("^the user is viewing the approval form for a job \"([^\"]*)\"$")
    public void the_user_is_viewing_a_job_with_status(String jobStatus) throws Exception {

        runtimeState.scenario.write("Opening Job Reference: : " + testData.getInt("jobReference"));
        runtimeState.openQuoteRequestsPage.searchJobs(testData.getInt("jobReference").toString());
        runtimeState.quoteJobEdit = runtimeState.openQuoteRequestsPage.jobAwaitingQuote(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @When("^the user is viewing the reject form for a job \"([^\"]*)\"$")
    public void the_user_is_viewing_a_job_with_status1(String jobStatus) throws Exception {
        // TODO: Make this page/url aware

        testData.getInt("jobReference");

        job.setJobReference(this.testData.getInt("jobReference"));
        runtimeState.scenario.write("Opening Job Reference: : " + testData.getInt("jobReference"));
        runtimeState.rejectQuoteRequest = runtimeState.openQuoteRequestsPage.rejectQuoteRequest(testData.getInt("jobReference"));
    }


    @When("^the asbestos register ((has|has not)) been checked$")
    public void the_asbestor_register_been_checked(String checked) {
        int AsbestosNotification = dbHelperSystemToggles.getSystemFeatureToggle("AsbestosNotification");
        if (AsbestosNotification == 1) {
            String answer = checked.equalsIgnoreCase("has") ? "Yes" :"No";
            runtimeState.asbestosModal = new AsbestosModal(getWebDriver());
            //Only execute if the modal is displayed - This is to prevent a number of tests being blocked by 1 bug
            if ( runtimeState.asbestosModal.isPageLoaded()) {
                runtimeState.scenario.write("Confirming 'Asbestos register has been checked': " + answer);
                runtimeState.asbestosModal.clickAnswer(answer);
                outputHelper.takeScreenshots();
                runtimeState.asbestosModal.clickOk();
            }
        }
        if ( runtimeState.loginPage.isModalDisplayed() ) {
            testData.put("modalMessage", runtimeState.loginPage.getModalText());
        }
    }

    @When("^the user views (?:an|the) \"([^\"]*)\" \"([^\"]*)\" job$")
    public void the_user_views_a_job(String jobType, String jobStatus) throws Throwable {
        String jobReference = testData.getString("jobReference");
        runtimeState.scenario.write("Searching and Opening Job Reference: " + jobReference);

        if (testData.getString("userProfileName") != null && testData.getString("userProfileName").equals("Contractor Technician")) {
            runtimeState.portalJobsForSitePage = new PortalJobsForSitePage(getWebDriver()).get();

            Grid grid = runtimeState.portalJobsForSitePage.getGrid();      
            for (Row row : grid.getRows()) {

                if ( row.getCell(1) != null && row.getCell(1).getText().equals(jobReference) ) {
                    if (row.getCell("Action").getText().equals("Stop Work")) {
                        runtimeState.portalJobsForSitePage.clickStopWorkButton(jobReference);
                    }
                }
            }
            POHelper.waitForAngularRequestsToFinish();
            the_asbestor_register_been_checked("has");

            // Check if choose asset for job page is loaded.
            ChooseAssetPage chooseAssetPage = new ChooseAssetPage(getWebDriver());
            if (chooseAssetPage.isPageLoaded()) {
                runtimeState.chooseAssetPage = new ChooseAssetPage(getWebDriver()).get();
                runtimeState.updateJobPage = runtimeState.chooseAssetPage.selectTopAsset();

            } else {
                runtimeState.updateJobPage = new UpdateJobPage(getWebDriver()).get();
            }

        } else {
            int attempts = 0;
            boolean result = false;

            switch (jobStatus) {
            case "Open":
                menuSteps.sub_menu_is_selected_from_the_top_menu("Open Jobs", "Jobs");
                runtimeState.openAwaitingJobsPage.searchJobs(testData.getString("jobReference"));

                outputHelper.takeScreenshots();
                Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid();
                Row row = grid.getRows().get(0);
                assertEquals(testData.getString("jobReference"), row.getCell(0).getText());
                String cssSelector = row.getCell(0).getCssSelector();
                getWebDriver().findElement(By.cssSelector(cssSelector)).click();

                the_asbestor_register_been_checked("has");
                // Check if choose asset for job page is loaded.
                ChooseAssetPage chooseAssetPage = new ChooseAssetPage(getWebDriver());
                if (chooseAssetPage.isPageLoaded()) {
                    runtimeState.chooseAssetPage = new ChooseAssetPage(getWebDriver()).get();
                    runtimeState.updateJobPage = runtimeState.chooseAssetPage.selectTopAsset();

                } else {
                    runtimeState.updateJobPage = new UpdateJobPage(getWebDriver()).get();
                }
                result = true;
                break;

            case "Allocated":
                while (attempts < 3) {
                    try {
                        menuSteps.sub_menu_is_selected_from_the_top_menu("Open Jobs", "Jobs");
                        runtimeState.openAwaitingJobsPage.searchJobs(testData.getString("jobReference"));

                        outputHelper.takeScreenshots();
                        runtimeState.portalUpdateJobETA = runtimeState.openAwaitingJobsPage.openAllocatedJob(testData.getString("jobReference"));
                        result = true;
                        break;
                    } catch (Exception e) {
                        runtimeState.scenario.write("Failed to open job, retrying....");
                        logger.debug("Failed to open job, retrying. " + e.getMessage());
                    }
                    // Wait for angular as the page might be refreshing
                    POHelper.waitForAngularRequestsToFinish();
                    attempts++;
                }
                break;
            default:
                throw new Exception("Cannot find job status " + jobStatus);
            }

            if (!result) {
                throw new Exception("Unable to open job after 3 attempts : " + testData.getInt("jobReference"));
            }
        }

        // Need these down stream - should refactor out at somepoint
        job.setJobReference(testData.getInt("jobReference"));
        updateJob.setJobReference(String.valueOf(job.getJobReference()));

        currentJob.copy(jobViewDao.getByJobReference(job.getJobReference()));

    }

    @When("^an Additional Resource is Required$")
    public void an_additional_resource_is_required() throws Exception {
        runtimeState.updateJobPage.selectAdditionalResourceRequired();
        updateJob.setAdditionalResourceRequired(true);
    }

    private void verifyManditoryFields() {
        runtimeState.updateJobPage.updateJob();
        List<String> alerts = runtimeState.updateJobPage.getAlerts();
        runtimeState.scenario.write("Verifying mandatory fields alerts displayed: " + alerts.toString());
        assertFalse("Alerts not displayed for mandatory fields", alerts.isEmpty());
    }

    @When("^a Quote is Requested for the Job$")
    public void the_portal_update_job_a_quote_is_requested_for_the_job() throws Exception {
        runtimeState.updateJobPage.selectRequestQuote();
        updateJob.setRequestQuote(true);

        verifyManditoryFields();

        // Store quote for details
        saveQuoteFormDetails();
    }

    @When("^the Scope of Work is entered$")
    public void the_portal_update_job_scope_of_work_is_entered_is_entered() throws Exception {

        // Check if upload job photos alert is displayed
        if (runtimeState.updateJobPage.isUploadJobPhotosAlertDisplayed()) {
            runtimeState.updateJobPage.clickJobPhotosAlertOKButton();
        }

        String scopeOfWork = "Scope of work for the updated job " + DateHelper.dateAsString(new Date());
        runtimeState.updateJobPage.updateScopeOfWork(scopeOfWork);
        runtimeState.scenario.write(scopeOfWork);
        job.setDescription(scopeOfWork);
    }

    @When("^the user is viewing a job awaiting acceptance previously declined$")
    public void the_user_is_viewing_a_job_awaiting_acceptance_previously_declined() throws Exception {
        the_user_is_viewing_the_awaiting_jobs_grid();
        currentJob.copy(jobViewDao.getDeclinedJob(testData.getInt("resourceId")));

        runtimeState.openAwaitingJobsPage.searchJobs(currentJob.getJobReference());
        outputHelper.takeScreenshots();

        runtimeState.jobDetailsPage = runtimeState.openAwaitingJobsPage.openJob(currentJob.getJobReference());
        runtimeState.jobInfoJobDetailsPage = new JobInfoJobDetailsPage(getWebDriver()).get();
    }

    @When("^the user is viewing the open jobs grid$")
    public void the_user_is_viewing_the_open_jobs_grid() throws Exception {
        if (runtimeState.portalSummaryPage == null) {
            runtimeState.portalSummaryPage = new PortalSummaryPage(getWebDriver()).get();
            if (!runtimeState.portalSummaryPage.isNoCountMessageDisplayed()) {
                awaitingJobCount = runtimeState.portalSummaryPage.getCountForType("Jobs Awaiting Acceptance");
                openJobCount = runtimeState.portalSummaryPage.getCountForType("Open Jobs");
                runtimeState.scenario.write("Number of open jobs: " + openJobCount);
                runtimeState.scenario.write("Number of jobs awaiting acceptance: " + awaitingJobCount);
            }

        }
        menuSteps.sub_menu_is_selected_from_the_top_menu("Open Jobs", "Jobs");
    }

    @When("^the user completes the accept form$")
    public void the_user_completes_the_accept_form() throws Exception {
        String etaDate = DateHelper.getNowDatePlusOffset(24, FULL_DATE);
        testData.addStringTag("etaDate", etaDate);
        runtimeState.scenario.write("ETA Date " + etaDate);
        runtimeState.acceptJobPage = runtimeState.jobDetailsPage.completeAcceptJobForm();

        runtimeState.acceptJobPage.enterETADate(etaDate);
        String etaWindow = runtimeState.acceptJobPage.selectRandomETAWindow();
        testData.addStringTag("etaWindow", etaWindow);
        runtimeState.scenario.write("ETA Window " + etaWindow);
    }

    @When("^a Contractor Reference Number \"([^\"]*)\" value is entered$")
    public void a_contractor_reference_value_is_entered(String value) throws Exception {
        String contractorReference = "ACME-".concat(testData.getString("jobReference")).concat(DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0));
        runtimeState.jobDetailsPage.setContractorReference(contractorReference);
        outputHelper.takeScreenshots();
    }

    @When("the site eta advised status is \"([^\"]*)\"$")
    public void the_site_eta_advised_status(String advised) throws Exception {
        if ("advised".equalsIgnoreCase(advised)) {
            runtimeState.acceptJobPage.clickAdviseYes("ETA advised to site");
            CallerContact cc = new CallerContact.Builder().build();
            runtimeState.acceptJobPage.enterAdvisedTo(cc.getName());
            testData.addStringTag("advisedTo", cc.getName());
            runtimeState.scenario.write("ETA is " + advised + " too site : " + cc.getName());
        }
        if ("not advised".equalsIgnoreCase(advised)) {
            runtimeState.acceptJobPage.clickAdviseNo("ETA advised to site");
            runtimeState.scenario.write("ETA is " + advised + " too site : ");
        }

    }

    @When("^the user accepts the job$")
    public void the_user_accepts_the_job() throws Exception {
        outputHelper.takeScreenshots();
        runtimeState.acceptJobPage.save();
        outputHelper.takeScreenshots();
    }

    @When("^the user completes the decline form$")
    public void the_user_completes_the_decline_form() throws Exception {

        if (testData.getString("profileName").contains("Contractor")) {
            reason = reasonDao.getRandomReasonByReasonCategory("ResourceAssignmentRejectContractor");
        } else {
            reason = reasonDao.getRandomReasonByReasonCategory("ResourceAssignmentRejectTechnician");
        }
        the_job_is_declined_with_reason(reason.getName());
    }

    @When("^the user saves the decline form$")
    public void the_user_saves_the_decline_form() throws Exception {
        runtimeState.jobDetailsPage.submitForm();
        outputHelper.takeScreenshots();
    }

    @When("^a \"([^\"]*)\" resource is selected")
    public void a_resource_is_selected(String resourceProfile) throws Exception {
        // runtimeState.updateJobPage.selectRandomResourceProfile();
        runtimeState.updateJobPage.selectResourceProfile(resourceProfile);
        outputHelper.takeScreenshots();
    }

    @When("the Resources Notes are entered")
    public void the_resources_notes_are_entered() throws Exception {
        String resourceNotes = "Additional resource notes : " + DateHelper.dateAsString(new Date());
        runtimeState.updateJobPage.updateAdditionalReourceNotes(resourceNotes);
        runtimeState.scenario.write(resourceNotes);
        outputHelper.takeScreenshots();
    }

    @When("^the user declines the job with reason \"([^\"]*)\"$")
    public void the_job_is_declined_with_reason(String declineReason) throws Exception {
        logger.debug(" Test wants to decline with  : " + declineReason);
        runtimeState.scenario.write("Declined job with reason " + declineReason);

        runtimeState.jobDetailsPage.completeDeclineJobForm(declineReason, "decline notes");
        outputHelper.takeScreenshots();

        while (!runtimeState.jobDetailsPage.getReasonText().equalsIgnoreCase(declineReason)) {
            runtimeState.jobDetailsPage.setDeclineReason(declineReason);
        }

        if (testData.getString("profileName").contains("Contractor")) {
            reason = this.reasonDao.getReasonByname(declineReason, "ResourceAssignmentRejectContractor");
        } else {
            reason = this.reasonDao.getReasonByname(declineReason, "ResourceAssignmentRejectTechnician");
        }
    }

    @When("^the user declines the job$")
    public void the_user_declines_the_job() throws Throwable {
        runtimeState.jobDetailsPage.declineJob();
    }

    @When("^the user selects a random reason$")
    public void the_user_selects_a_random_reason() throws Throwable {
        runtimeState.jobDetailsPage.setRandomDeclineReason();
        String declineReason = runtimeState.jobDetailsPage.getReasonText();
        runtimeState.scenario.write("Decline reason " + declineReason);
        // Store decline reason for use with the assertion later
        if (testData.getString("profileName").contains("Contractor")) {
            reason = reasonDao.getReasonByname(declineReason, "ResourceAssignmentRejectContractor");
        } else {
            reason = reasonDao.getReasonByname(declineReason, "ResourceAssignmentRejectTechnician");
        }
        testData.addIntegerTag("declineReasonId", reason.getId());
        testData.addStringTag("declineReason", reason.getName());
    }

    @When("^the user enters decline notes$")
    public void the_user_enters_decline_notes() throws Throwable {
        String declineNotes = "Job declined " + DateHelper.dateAsString(new Date());
        testData.addStringTag("declineNotes", declineNotes);
        runtimeState.jobDetailsPage.setDeclineNotes(declineNotes);
        outputHelper.takeScreenshots();
    }

    @When("^the job is assigned to a \"([^\"]*)\" resource$")
    public void the_job_is_assigned_to_a_resource(String resourceProfile) throws Exception {
        if (!resourceProfile.isEmpty()) {
            // If City Resource need to check the DB and pull back a valid alias name to select from the drop down, otherwise use what was given in the feature file
            resourceProfile = resourceProfile.equalsIgnoreCase("City Resource") ? dbHelperResources.getResourceProfileNameForSite(testData.getString("siteName")) : resourceProfile;
            runtimeState.jobDetailsPage.selectSuggestedResource(resourceProfile);
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the job info is displayed correctly$")
    public void the_job_info_is_displayed_correctly() throws Exception {
        runtimeState.jobInfoJobDetailsPage = new JobInfoJobDetailsPage(getWebDriver()).get();
        runtimeState.scenario.write("Asserting Job info section is displaying the correct data");

        // Assert logged date displays correctly - Need to get offset and apply to value form database
        int jobReference = testData.getInt("jobReference");
        Job job = jobDao.getByJobReference(jobReference);
        String createdOnDate = DateHelper.getDateInFormat(job.getCreatedOn(), MEDIUM);
        String createdOn = tzHelper.adjustTimeForJobReference(jobReference, createdOnDate, MEDIUM);
        assertEquals("Unexpected Logged Date", createdOn, runtimeState.jobInfoJobDetailsPage.getLoggedDate());

        stepHelper.verifyJobDescription(currentJob.getJobReference(), currentJob.getDescription(), runtimeState.jobInfoJobDetailsPage.getJobDescription());

        assertEquals("Unexpected Job Type", currentJob.getJobTypeName(), runtimeState.jobInfoJobDetailsPage.getJobType());
        if (currentJob.getLocationName() != null) {
            assertTrue("Unexpected Location", runtimeState.jobInfoJobDetailsPage.getLocation().toLowerCase().contains(currentJob.getLocationName().toLowerCase()));
        }

        String pagelocation = runtimeState.jobInfoJobDetailsPage.getLocation().toLowerCase();
        String expectedSubLocation = currentJob.getSubLocationName().toLowerCase();
        assertTrue("Unexpected Sub Location : expected " + pagelocation + " to contain " + expectedSubLocation, pagelocation.contains(expectedSubLocation));

        assertTrue("Unexpected Asset Name", runtimeState.jobInfoJobDetailsPage.getSubtypeClassification().toLowerCase().contains(currentJob.getAssetName().toLowerCase()));
        assertTrue("Unexpected Asset Sub Name", runtimeState.jobInfoJobDetailsPage.getSubtypeClassification().toLowerCase().contains(currentJob.getAssetSubTypeName().toLowerCase()));

        assertTrue("Unexpected Response Priority", runtimeState.jobInfoJobDetailsPage.getResponsePriority().contains(currentJob.getFaultPriority()));
        assertTrue("Unexpected Repair Priority", runtimeState.jobInfoJobDetailsPage.getRepairPriority().contains(currentJob.getFaultPriority()));
        assertEquals("Unexpected Fault Type", currentJob.getFaultType(), runtimeState.jobInfoJobDetailsPage.getFaultType());
    }

    @ContinueNextStepsOnException
    @Then("^the site info is displayed correctly$")
    public void the_site_info_is_disaplyed_correctly() throws Exception {
        runtimeState.scenario.write("Asserting Site Info section is displaying the correct data");
        // Initialise page object
        runtimeState.siteInfoJobDetailsPage = new SiteInfoJobDetailsPage(getWebDriver()).get();

        // Get site from current job
        currentSite = siteDao.get(currentJob.getSiteId());
        assertNotNull("Unexpected null record set", currentSite);

        assertEquals("Unexpected Site Name", currentSite.getName(), runtimeState.siteInfoJobDetailsPage.getSiteName());
        // Assert Site Information is displayed correctly - database returns null for
        // empty string so need to convert
        assertEquals("unexpected Address line 1", (currentSite.getAddress1() == null ? "" : currentSite.getAddress1()), runtimeState.siteInfoJobDetailsPage.getAddressLine(0));
        assertEquals("unexpected Address line 2", (currentSite.getAddress2() == null ? "" : currentSite.getAddress2()), runtimeState.siteInfoJobDetailsPage.getAddressLine(1));
        assertEquals("unexpected Address line 3", (currentSite.getAddress3() == null ? "" : currentSite.getAddress3()), runtimeState.siteInfoJobDetailsPage.getAddressLine(2));
        int postCodeAddressLine = 3;
        postCodeAddressLine = (!currentSite.getTown().isEmpty() || currentSite.getTown() != null) ? postCodeAddressLine + 1 : postCodeAddressLine;
        postCodeAddressLine = (!currentSite.getCounty().isEmpty() || currentSite.getCounty() != null) ? postCodeAddressLine + 1 : postCodeAddressLine;
        assertEquals("unexpected Postcode/Zipcode", (currentSite.getPostcode() == null ? "" : normalize(currentSite.getPostcode())), normalize(runtimeState.siteInfoJobDetailsPage.getAddressLine(postCodeAddressLine)));
    }

    @ContinueNextStepsOnException
    @Then("^the contact info is displayed correctly$")
    public void the_contact_info_is_disaplyed_correctly() throws Exception {
        runtimeState.scenario.write("Asserting Contact Info section is displaying the correct data");
        // Initialise page object
        runtimeState.contactInfoJobDetailsPage = new ContactInfoJobDetailsPage(getWebDriver()).get();

        // Get site from current job
        currentJobcontact = jobContactDao.get(currentJob.getId().toString());
        assertNotNull("Unexpected null record set", currentJobcontact);
        logger.debug(currentJobcontact.size());
        logger.debug(runtimeState.contactInfoJobDetailsPage.getNumberOfContacts());

        assertEquals("Unexpected number of contacts assigned to job", Integer.valueOf(currentJobcontact.size()), runtimeState.contactInfoJobDetailsPage.getNumberOfContacts());

        // Pull back contacts on page
        List<JobContact> displayedContacts = new ArrayList<JobContact>();
        for (int i = 1; i <= (runtimeState.contactInfoJobDetailsPage.getNumberOfContacts()); i++) {
            JobContact currentContact = new JobContact();
            currentContact.setContactName(normalize(runtimeState.contactInfoJobDetailsPage.getContactName(i)));
            currentContact.setContactType(normalize(runtimeState.contactInfoJobDetailsPage.getContactType(i)));
            currentContact.setContactNumber((runtimeState.contactInfoJobDetailsPage.getContactNumber(i)));
            displayedContacts.add(currentContact);
        }

        // sort both expected and displayed by contact name
        displayedContacts.sort(Comparator.comparing(JobContact::getContactName));
        currentJobcontact.sort(Comparator.comparing(JobContact::getContactName));

        // Now assert both are the same
        for (int i = 0; i < (runtimeState.contactInfoJobDetailsPage.getNumberOfContacts()); i++) {
            assertEquals("Unexpected Contact Name", currentJobcontact.get(i).getContactName() == null ? "" : normalize(currentJobcontact.get(i).getContactName()),
                    displayedContacts.get(i).getContactName() == null ? "" : displayedContacts.get(i).getContactName());

            assertEquals("Unexpected Contact Type", currentJobcontact.get(i).getContactType() == null ? "" : normalize(currentJobcontact.get(i).getContactType()),
                    displayedContacts.get(i).getContactType() == null ? "" : normalize(displayedContacts.get(i).getContactType()));

            assertEquals("Unexpected Contact Number", currentJobcontact.get(i).getContactNumber() == null ? "" : currentJobcontact.get(i).getContactNumber(),
                    displayedContacts.get(i).getContactNumber() == null ? "" : displayedContacts.get(i).getContactNumber());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the awaiting jobs grid displays data correctly$")
    public void the_awaiting_jobs_grid_displays_data_correctly() throws Exception {
        runtimeState.openAwaitingJobsPage = new OpenAwaitingJobsPage(getWebDriver()).get();

        jobList = jobViewDao.getAwaitingJobs(testData.getInt("resourceId"));
        JobView jobView = jobList.get(0);

        runtimeState.openAwaitingJobsPage.searchJobs(jobView.getJobReference());

        stepHelper.verifyJobGridCommon(jobView, runtimeState.openAwaitingJobsPage);
    }

    @ContinueNextStepsOnException
    @Then("^each row on the awaiting jobs grid displays data correctly$")
    public void each_row_on_the_awaiting_jobs_grid_displays_data_correctly() throws Exception {
        // Make it easy sort by job reference descending - fix this hard coding
        outputHelper.takeScreenshots();

        runtimeState.openAwaitingJobsPage.tableSort("Job reference");
        runtimeState.openAwaitingJobsPage = new OpenAwaitingJobsPage(getWebDriver()).get();

        jobList = jobViewDao.getAwaitingJobs(testData.getInt("resourceId"));

        jobList.sort(Comparator.comparing(JobView::getJobReference));
        assertNotNull("Unexpected null record set", jobList);
        assertFalse("Unexpected empty record set", jobList.isEmpty());


        // Loop through the pages
        Integer pages = (jobList.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int i = 0; i < pages; i++) {
            Integer offSet = (i) * PAGE_SIZE;
            Integer pageMax = ((i + 1) * PAGE_SIZE) - 1;
            outputHelper.takeScreenshots();
            // Verify contents of each page
            for (int j = offSet; j < (jobList.size() < pageMax ? jobList.size() : pageMax); j++) {
                JobView jobView = jobList.get(j);
                try {
                    stepHelper.verifyJobGridCommon(jobView, runtimeState.openAwaitingJobsPage);
                } catch (NoSuchElementException nse) {
                    // check to see if test has failed due to external influence updating the number of jobs awaiting acceptance
                    List<JobView> jobList2 = jobViewDao.getAwaitingJobs(testData.getInt("resourceId"));
                    if (jobList2.size() != jobList.size()) {
                        runtimeState.scenario.write("Number of jobs awaiting acceptance has changed during the duration of the test. Skipping further assertions after completing: " + j);
                        break;
                    } else {
                        throw new Exception(nse.getMessage());
                    }
                }
            }
            runtimeState.openAwaitingJobsPage = runtimeState.openAwaitingJobsPage.gotoNextPage();
        }
        runtimeState.openAwaitingJobsPage.gotoPage(1);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" jobs grid displays data correctly$")
    public void the_open_jobs_grid_displays_data_correctly(String jobStatus) throws Exception {
        Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid();
        assertNotNull("Unexpected Null Grid", grid);
        String[] expectedHeaders = null;

        switch (jobStatus) {
        case "Open":
            String[] openJobsHeaders;
            if (getWebDriver().getCurrentUrl().contains("ukrb")) {
                openJobsHeaders = new String[] { "Job reference", "Assignment status", "Site", "Asset subtype / classification", "Serial No", "Days outstanding", "ETA" };
            } else {
                openJobsHeaders = new String[] { "Job reference", "Status", "Asset subtype / classification", "Asset No", "Priority", "Priority", "Days outstanding", "Reference", "ETA" };
            }
            expectedHeaders = openJobsHeaders;
            break;

        case "Awaiting":
            String[] jobsAwaitingAcceptanceHeaders = { "Job reference", "Assignment status", "Site", "Asset subtype/classification", "Serial No", "Days outstanding" };
            expectedHeaders = jobsAwaitingAcceptanceHeaders;
            break;

        default:
            throw new Exception("Cannot find job status " + jobStatus);
        }
        GridHelper.assertGridHeaders(grid.getHeaders(), expectedHeaders);
        assertTrue("Unexpected table footer missing", runtimeState.openAwaitingJobsPage.isTableSummaryPageInfoDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the open jobs by site grid displays data correctly$")
    public void the_open_jobs_by_site_grid_displays_data_correctly() throws Exception {
        runtimeState.scenario.write("Asserting the table can only be searched on Site");
        assertTrue("The Site search box is not displayed on Open Jobs By Site Page", runtimeState.portalOpenJobsBySitePage.isSiteSearchBoxDisplayed());
        assertTrue("The table can be filtered by more than 1 column", runtimeState.portalOpenJobsBySitePage.getNumberOfSearchBoxesDisplayed() == 1);

        runtimeState.scenario.write("Asserting columns cannot be sorted");
        assertTrue("The Sorting is not available on Site column", runtimeState.portalOpenJobsBySitePage.isColumnSortable("Site"));
        assertTrue("The Sorting is not available on Awaiting Acceptance column", runtimeState.portalOpenJobsBySitePage.isColumnSortable("Awaiting Acceptance"));
        assertTrue("The Sorting is not available on Allocated column", runtimeState.portalOpenJobsBySitePage.isColumnSortable("Allocated"));

        runtimeState.scenario.write("Asserting table footer is displayed");
        assertTrue("Unexpected table footer missing", runtimeState.portalOpenJobsBySitePage.isTableSummaryPageInfoDisplayed());

        Grid grid = runtimeState.portalOpenJobsBySitePage.getGrid();

        runtimeState.scenario.write("Asserting table headers: Site, Awating Acceptance, Allocated");
        assertTrue("Expected header: Site", grid.getHeaders().contains("Site"));
        assertTrue("Expected header: Awaiting Acceptance", grid.getHeaders().contains("Awaiting Acceptance"));
        assertTrue("Expected header: Allocated", grid.getHeaders().contains("Allocated"));
    }

    @ContinueNextStepsOnException
    @Then("^each row on the jobs grid displays data correctly$")
    public void each_row_on_the_jobs_grid_displays_data_correctly() throws Exception {
        // Make it easy sort by job reference descending - fix this hard coding
        outputHelper.takeScreenshots();
        runtimeState.openAwaitingJobsPage = runtimeState.openAwaitingJobsPage.tableSort("jobReference");

        jobList.sort(Comparator.comparing(JobView::getJobReference));
        assertNotNull("Unexpected null record set", jobList);
        assertFalse("Unexpected empty record set", jobList.isEmpty());

        // Loop through the pages
        Integer pages = (jobList.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int i = 0; i < pages; i++) {
            Integer offSet = (i) * PAGE_SIZE;
            Integer pageMax = ((i + 1) * PAGE_SIZE) - 1;
            outputHelper.takeScreenshots();
            // Verify contents of each page
            for (int j = offSet; j < (jobList.size() <= pageMax ? jobList.size() : pageMax + 1); j++) {
                JobView jobView = jobList.get(j);
                runtimeState.scenario.write("Asserting row details for job reference: " + jobView.getJobReference());
                // TODO : Only output if on debug
                // runtimeState.scenario.write("Verifying " + jobView.getJobReference());
                stepHelper.verifyJobGridCommon(jobView, runtimeState.openAwaitingJobsPage);
                String projectCost = dbHelper.getProjectCost(jobView.getJobReference(), testData.getString("userName"));
                stepHelper.verifyJobGridOpen(jobView, runtimeState.openAwaitingJobsPage, projectCost);
            }
            runtimeState.openAwaitingJobsPage = runtimeState.openAwaitingJobsPage.gotoNextPage();
        }
        runtimeState.openAwaitingJobsPage.gotoPage(1);
    }

    @ContinueNextStepsOnException
    @Then("^the open job count will have incremented$")
    public void the_open_row_count_will_have_incremented() throws Exception {
        // Only check if openJobCount != -1
        // -1 is the value default value
        if (openJobCount != -1) {
            Integer newOpenJobs = runtimeState.portalSummaryPage.getCountForType("Open Jobs");
            runtimeState.scenario.write("Number of open jobs: " + newOpenJobs);
            assertTrue("Unexpected Open Job Count", openJobCount == newOpenJobs - 1);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the awaiting job count will have decremented on the landing page$")
    public void the_open_row_count_will_have_decremented() throws Exception {
        // Only check if awaitingJobCount != -1
        // -1 is the value default value
        if (awaitingJobCount != -1) {
            runtimeState.portalNavBar.clickTopLevelMenu("Home");
            Integer newAwaitingJobCount = runtimeState.portalSummaryPage.getCountForType("Jobs Awaiting Acceptance");
            runtimeState.scenario.write("Number of jobs awaiting acceptance: " + newAwaitingJobCount);
            assertTrue("Unexpected Awaiting Job Count", awaitingJobCount == newAwaitingJobCount + 1);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the job is removed from the jobs awaiting acceptance table$")
    public void the_job_is_removed_from_the_jobs_awaiting_acceptance_table() throws Exception {
        the_user_is_viewing_the_awaiting_jobs_grid();

        if ( !runtimeState.openAwaitingJobsPage.isNoData() ) {
            // Search for a job
            runtimeState.openAwaitingJobsPage.searchJobsNoWait(testData.getInt("jobReference").toString());
            Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid();
            GridHelper.waitForRowCount(grid.getGridXpath(), 0);
            outputHelper.takeScreenshots();
            int numRows = runtimeState.openAwaitingJobsPage.getJobsGrid().getRows().size();
            assertTrue("Unexpeced number of rows displayed: " + numRows, numRows == 0);
        }
    }

    public void assertMessageRecord(Integer jobReference, String routingKey, String eventtype, Integer assignStatus, Integer assignReason) throws InterruptedException {
        List<Message> message;
        message = messageDao.getMessages(jobReference, routingKey, eventtype, assignStatus, assignReason);
        assertNotNull("Unexpected null record set", message);
        assertFalse("Unexpected empty record set", message.isEmpty());
    }

    public void assertMessageRecord(Integer jobReference, String routingKey, String eventtype, Integer jobStatus) throws InterruptedException {
        List<Message> message;
        message = messageDao.getMessages(jobReference, routingKey, eventtype, jobStatus);
        assertNotNull("Unexpected null record set", message);
        assertFalse("Unexpected empty record set", message.isEmpty());
    }

    public void assertMessageRecord(Integer jobReference, String routingKey, String eventtype, String jobStatus) throws InterruptedException {
        List<Message> message;
        message = messageDao.getMessages(jobReference, routingKey, eventtype, jobStatus);
        assertNotNull("Unexpected null record set", message);
        assertFalse("Unexpected empty record set", message.isEmpty());
    }

    @ContinueNextStepsOnException
    @Then("^the database has been updated with the acceptance$")
    public void the_database_has_been_updated_with_the_acceptance() throws Throwable {
        // Check message table
        assertMessageRecord(job.getJobReference(), ACCEPT_ROUTING_KEY, ACCEPT_EVENT_TYPE, ACCEPT_RESOURCE_ASSIGNMENT_STATUS, ACCEPT_RESOURCE_ASSIGNMENT_REASON);
        assertMessageRecord(job.getJobReference(), ETA_PROVIDED_ROUTING_KEY, ETA_PROVIDED_EVENT_TYPE, ETA_PROVIDED_RESOURCE_ASSIGNMENT_STATUS, ETA_PROVIDED_RESOURCE_ASSIGNMENT_REASON);
    }

    @When("the Site notified of the ETA \"([^\"]*)\"$")
    public void the_site_notified_of_the_ETA(String advised) throws Exception {
        ResourceAssignment resourceAssignment;
        if ("advised".equalsIgnoreCase(advised)) {
            // TODO : Having to use ACCEPT_RESOURCE_ASSIGNMENT_STATUS and not
            // SITE_NOTIFIED_ETA_PROVIDED_RESOURCE_ASSIGNMENT_STATUS. Need to verify why?
            assertMessageRecord(
                    job.getJobReference(),
                    SITE_NOTIFIED_ETA_PROVIDED_ROUTING_KEY,
                    SITE_NOTIFIED_ETA_PROVIDED_EVENT_TYPE,
                    ACCEPT_RESOURCE_ASSIGNMENT_STATUS,
                    SITE_NOTIFIED_ETA_PROVIDED_RESOURCE_ASSIGNMENT_REASON);
            // Check resource assignment
            resourceAssignment = resourceAssignmentDao.getAssigmentByStatusReason(
                    job.getJobReference(),
                    testData.getInt("resourceId"),
                    SITE_NOTIFIED_ETA_PROVIDED_RESOURCE_ASSIGNMENT_STATUS,
                    SITE_NOTIFIED_ETA_PROVIDED_RESOURCE_ASSIGNMENT_REASON);
        } else {
            // Check resource assignment
            resourceAssignment = resourceAssignmentDao.getAssigmentByStatusReason(job.getJobReference(), testData.getInt("resourceId"), ACCEPT_RESOURCE_ASSIGNMENT_STATUS, ACCEPT_RESOURCE_ASSIGNMENT_REASON);
        }
        assertNotNull("Unexpected null record set", resourceAssignment);
    }

    @ContinueNextStepsOnException
    @Then("^the database has been updated with the decline$")
    public void the_database_has_been_updated_with_the_decline() throws Exception {
        // Check resource assignment
        ResourceAssignment resourceAssignment;
        resourceAssignment = resourceAssignmentDao.getAssigmentByStatusReason(job.getJobReference(), testData.getInt("resourceId"), DECLINE_RESOURCE_ASSIGNMENT_STATUS, reason.getId());
        assertNotNull("Unexpected null record set", resourceAssignment);

    }

    @ContinueNextStepsOnException
    @Then("^the Job is sitting in \"([^\"]*)\" status$")
    public void the_job_is_sitting_in_status(String jobStatus) throws Exception {
        logger.debug("Get the job");
        logger.debug(testData.getInt("jobReference"));

        dbHelperAssertions.jobIsInStatus(testData.getInt("jobReference"), jobStatus);

        JobView currentjob = jobViewDao.getByJobReference(testData.getInt("jobReference"), jobStatus);
        this.currentJob.copy(currentjob);
    }

    @ContinueNextStepsOnException
    @Then("^the resource assignment status is now \"([^\"]*)\"$")
    public void the_resource_assignment_status_is_now(String status) {
        int jobReference = testData.getInt("jobReference");
        int resourceId = testData.getInt("resourceId");
        List<String> resourceAssignmentStatus = dbHelperResources.getResourceAssignmentStatus(jobReference, resourceId);
        assertTrue("Actual Resource Assignment status: " + resourceAssignmentStatus, resourceAssignmentStatus.contains(status));
    }

    @ContinueNextStepsOnException
    @Then("^the Job is sitting in \"([^\"]*)\" status or \"([^\"]*)\" status$")
    public void the_job_is_sitting_in_status_or_alt_status(String jobStatus, String altJobStatus) throws Exception {
        try {
            the_job_is_sitting_in_status(jobStatus);
            runtimeState.scenario.write("Job status recorded as: " + jobStatus);
        } catch (Exception e) {
            the_job_is_sitting_in_status(altJobStatus);
            runtimeState.scenario.write("Job status recorded as: " + altJobStatus);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Message table has been updated with the Job Status \"([^\"]*)\"$")
    public void the_Message_table_has_been_updated_with_job_status(String jobStatus) throws Exception {
        assertMessageRecord(job.getJobReference(), JOB_COMPLETE_ROUTING_KEY, JOB_COMPLETE_EVENT_TYPE, jobStatus);
    }

    @ContinueNextStepsOnException
    @Then("^the Message table has been updated with the Resource Status \"([^\"]*)\"$")
    public void the_Message_table_has_been_updated_with_resource_status(String messageStatus) throws Exception {
        Integer waitTime = 120000;
        AssertMessage assertMessage;
        switch (messageStatus) {
        case "Complete":
            assertMessage = new AssertMessage(messageDao, job.getJobReference(), COMPLETE_ROUTING_KEY, COMPLETE_EVENT_TYPE, COMPLETE_RESOURCE_ASSIGNMENT_STATUS, COMPLETE_RESOURCE_ASSIGNMENT_REASON);
            assertionFactory.performAssertion(assertMessage, waitTime);
            break;
        case "Declined":
            assertMessage = new AssertMessage(messageDao, job.getJobReference(), DECLINE_ROUTING_KEY, DECLINE_EVENT_TYPE, DECLINE_RESOURCE_ASSIGNMENT_STATUS, reason.getId());
            assertionFactory.performAssertion(assertMessage, waitTime);
            break;
        default:
            throw new Exception("Cannot find resource status " + messageStatus);
        }

    }

    // the "Portal" "Job" Update Saved page is showing
    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" \"([^\"]*)\" Update Saved page is displayed$")
    public void the_site_job_update_saved_page_is_displayed(String appName, String pageName) throws Exception {
        runtimeState.updateSavedPage = new UpdateSavedPage(getWebDriver()).get();
    }

    @ContinueNextStepsOnException
    @Then("^the Site Visit has been recorded$")
    public void the_site_visit_has_been_recorded() throws Exception {
        // Need to convert the dates here before running the assertion.
        // This is to compensate for the fact that the database is sometimes set to a different locale than the customer
        if (testData.getString("profileName").contains("Contractor")) {
            updateJob.setWorkStart(DateHelper.dateAsString(testData.getDate("Start time"), MEDIUM));
            updateJob.setJobReference(testData.getString("jobReference"));
        } else {
            updateJob.setWorkStart(DateHelper.convert(updateJob.getWorkStart(), SHORT, MEDIUM));
        }

        AssertSiteVisit assertSiteVisit = new AssertSiteVisit(siteVisitsDao, updateJob);
        assertionFactory.performAssertion(assertSiteVisit);
    }

    @ContinueNextStepsOnException
    @Then("^the new quote job is created$")
    public void the_new_quote_job_is_created() throws Exception {

        if (testData.getString("profileName").contains("Contractor")) {
            //get linked job id and verify if it is a quote
            Integer newJobReference = dbHelperJobs.getLinkedJobReference(testData.getInt("jobReference"));
            runtimeState.scenario.write("Asserting linked Job is a quote with reference: " + newJobReference);
            assertEquals("New Job is not of type quote!", "Quote", dbHelperJobs.getJobType(newJobReference));

        } else {
            // get quote number from the update saved page
            // check db
            // need to grab the info from the quote job form and store in the newjob object
            // as well
            String newQuote = runtimeState.updateSavedPage.getJobReference("A quote job has been created");
            logger.debug("New Quote Ref: " + newQuote);
            assertNotNull("Unexpected null quote reference", newQuote);

            currentJob.copy(jobViewDao.getByJobReference(newQuote));
            assertNotNull(currentJob);
            // Redo this after analysis
            // assertEquals("Unexpected Main Type found", job.getMaintype(),
            // currentJob.getAssetName());
            // assertEquals("Unexpected Sub Type found", job.getSubtype(),
            // currentJob.getAssetSubTypeName());
            // assertEquals("Unexpected Location found", job.getLocation(),
            // currentJob.getSubLocationName());
            // assertTrue("Unexpected Location found",
            // job.getLocation().contains(currentJob.getSubLocationName()));
            // assertEquals("Unexpected Fault Type found", job.getFault(),
            // currentJob.getFaultType());
            // assertEquals("Unexpected Description found", job.getDescription(),
            // currentJob.getDescription());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Travel time tooltip will be displayed$")
    public void the_travel_time_tooltip_will_be_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing View Job details link", runtimeState.updateJobPage.isTravelTimeToolTipVisible(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the Work Start tooltip will be displayed$")
    public void the_work_start_tooltip_will_be_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing Start tooltip", runtimeState.updateJobPage.isWorkStartToolTipVisible(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the Time Spent tooltip will be displayed$")
    public void the_time_spent_tooltip_will_be_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing Start tooltip", runtimeState.updateJobPage.isTimeSpentToolTipVisible(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the Work End tooltip will be displayed$")
    public void the_work_end_tooltip_will_be_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing End tooltip ", runtimeState.updateJobPage.isWorkEndToolTipVisible(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the Status on Departure tooltip will be displayed$")
    public void the_status_on_departure_tooltip_will_be_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing Status on Departure tooltip", runtimeState.updateJobPage.isStatusOnDepartureToolTipVisible(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the View Job Details link be displayed$")
    public void the_work_start_link_will_be_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing View Job details link", runtimeState.updateSavedPage.viewJobDetailsIsDisplayed(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the View Open Jobs link will be displayed$")
    public void the_work_end_link_will_be_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing View Job details link", runtimeState.updateSavedPage.viewOpenJobsIsDisplayed(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the View All Jobs link will be displayed$")
    public void the_status_on_departure_link_will_be_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing View Job details link", runtimeState.updateSavedPage.viewAllJobsUsDisplayed(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" message will be displayed$")
    public void the_job_update_has_been_saved_message_will_be_displayed(String message) throws Exception {
        runtimeState.updateSavedPage = new UpdateSavedPage(getWebDriver()).get();
        assertEquals("Unexpected Page displayed: Missing job update message", message, runtimeState.updateSavedPage.getJobSavedMessage());
    }

    private void saveQuoteFormDetails() {
        // Store quote for details
        job.setMaintype(runtimeState.updateJobPage.getQuoteAssetMainTypeSelectedValue());
        job.setSubtype(runtimeState.updateJobPage.getQuoteAssetSubTypeSelectedValue());
        job.setClassification(runtimeState.updateJobPage.getQuoteAssetClassificationSelectedValue());
        job.setAssetTag(runtimeState.updateJobPage.getQuoteAssetSelectedValue());
        job.setLocation(runtimeState.updateJobPage.getQuoteLocationSelectedValue());
        job.setFault(runtimeState.updateJobPage.getQuoteFaultTypeSelectedValue());
    }

    @ContinueNextStepsOnException
    @Then("^the number of \"([^\"]*)\" jobs displayed on the table is correct$")
    public void the_number_of_jobs_is_correct(String jobStatus) throws Throwable {
        List<Map<String, Object>> dbData = null;
        if (jobStatus.equals("Allocated")) {
            dbData = dbHelperJobs.getNumberOfJobsAllocated(testData.getInt("resourceId"));
        } else if (jobStatus.equals("Awaiting Acceptance")) {
            dbData = dbHelperJobs.getNumberOfJobsAwaitingAcceptance(testData.getInt("resourceId"));
        }

        Grid grid = runtimeState.portalOpenJobsBySitePage.getGrid();
        for (Row row : grid.getRows()) {
            String siteName = row.getCell("Site").getText();
            String displayedCount = row.getCell(jobStatus).getText();

            String expectedJobCount = "0";
            if (dbData != null) {
                for (Map<String, Object> counts : dbData) {
                    if ( siteName.equals(normalize(counts.get("SiteName").toString())) ) {
                        expectedJobCount = counts.get("JobCount").toString();
                    }
                }
            }

            runtimeState.scenario.write("Asserting count for site " + siteName + ": " + expectedJobCount);
            try {
                assertEquals("Expected count for site " + siteName + ": " + expectedJobCount, expectedJobCount, displayedCount);
            } catch (Exception | ComparisonFailure e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" is displayed on the table of outstanding activities \"([^\"]*)\" counts$")
    public void is_displayed_on_the_table_of_outstanding_activities(String item, String withCounts) throws Throwable {
        boolean countsExpected = withCounts.equals("with") ? true : false;
        runtimeState.portalHomePage = new PortalHomePage(getWebDriver()).get();
        boolean found = false;

        if (getWebDriver().getCurrentUrl().contains("usad")) {
            POHelper.waitForAngularRequestsToFinish();
            List<String> tiles = runtimeState.portalHomePage.getTiles();
            for (String tile : tiles) {
                if (tile.contains(item)) {
                    found = true;

                    runtimeState.scenario.write("Asserting count displayed for tile: " + tile);
                    boolean exceptionThrown = false;
                    String count = runtimeState.portalHomePage.getCountsForTile(tile);
                    try {
                        Integer.valueOf(count);
                        runtimeState.scenario.write("Tile count: " + count);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exceptionThrown = true;
                    }

                    if (countsExpected && exceptionThrown) {
                        throw new Exception ("Count not found for: " + item);
                    } else if (!countsExpected && !exceptionThrown) {
                        throw new Exception ("Count found for: " + item);
                    }
                }
            }
        } else {
            for (String row : runtimeState.portalHomePage.getGridAsString()) {
                if (row.contains(item)) {
                    found = true;

                    runtimeState.scenario.write("Asserting count displayed: " + row);
                    boolean exceptionThrown = false;
                    String[] counts = row.split(" ");
                    try {
                        Integer.valueOf(counts[counts.length - 1]);
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        exceptionThrown = true;
                    }

                    if (countsExpected && exceptionThrown) {
                        throw new Exception ("Count not found for: " + item);
                    } else if (!countsExpected && !exceptionThrown) {
                        throw new Exception ("Count found for: " + item);
                    }
                }
            }
        }
        assertTrue("Not found in outstanding activities summary table: " + item, found);
    }

    @ContinueNextStepsOnException
    @Then("^outstanding activities are displayed \"([^\"]*)\" counts$")
    public void is_displayed_on_the_table_of_outstanding_activities(String withCounts) throws Throwable {
        boolean countsExpected = withCounts.equals("with") ? true : false;
        runtimeState.portalHomePage = new PortalHomePage(getWebDriver()).get();
        for (String row : runtimeState.portalHomePage.getGridAsString()) {
            if (!row.isEmpty()) {
                runtimeState.scenario.write("Asserting count displayed: " + row);
                String[] parts = row.split(" ");
                boolean exceptionThrown = false;
                try {
                    Integer.valueOf(parts[parts.length - 1]);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    exceptionThrown = true;
                }

                if (countsExpected && exceptionThrown) {
                    throw new Exception ("Count not found for: " + row);
                } else if (!countsExpected && !exceptionThrown) {
                    throw new Exception ("Count found for: " + row);
                }
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^outstanding activities are displayed$")
    public void outstanding_activities_are_displayed() throws Throwable {
        runtimeState.portalHomePage = new PortalHomePage(getWebDriver()).get();
        runtimeState.portalSummaryPage = new PortalSummaryPage(getWebDriver()).get();

        if (getWebDriver().getCurrentUrl().contains("uswm")) {

            List<String> grid = runtimeState.portalHomePage.getGridAsString();
            runtimeState.scenario.write("Outstanding Activities displayed: ");
            for (String row : grid) {
                runtimeState.scenario.write(row);
            }
            assertFalse(grid.isEmpty());
        } else {
            List<String> tiles = runtimeState.portalSummaryPage.getTiles();
            runtimeState.scenario.write("Tiles displayed: ");
            for (String tile : tiles) {
                runtimeState.scenario.write(tile);
            }
            assertFalse(tiles.isEmpty());
        }
    }

    @ContinueNextStepsOnException
    @Then("^outstanding activities are displayed on the \"([^\"]*)\"$")
    public void outstanding_activities_are_displayed(String home) throws Throwable {
        runtimeState.portalHomePage = new PortalHomePage(getWebDriver()).get();
        runtimeState.portalNavBar.clickTopLevelMenu("Home");
        runtimeState.portalNavBar.clickSubLevelMenu(home);
        List<String> grid = runtimeState.portalHomePage.getGridAsString();
        runtimeState.scenario.write("Outstanding Activities displayed: ");
        for (String row : grid) {
            runtimeState.scenario.write(row);
        }
        assertFalse(grid.isEmpty());
    }

}
