package mercury.steps.storeportal;

import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MEDIUM_DATE;
import static mercury.helpers.Globalisation.MS_SHORT_TIME;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.api.models.job.Job;
import mercury.database.models.ApplicationUser;
import mercury.database.models.UserJob;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.FileHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.QuoteCreationHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobData;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.pageobject.web.storeportal.StorePortalCalendarPage;
import mercury.pageobject.web.storeportal.StorePortalContactUsMenuPage;
import mercury.pageobject.web.storeportal.StorePortalHomePage;
import mercury.pageobject.web.storeportal.StorePortalJobDetailsPage;
import mercury.pageobject.web.storeportal.StorePortalLogAJobPage;
import mercury.runtime.RuntimeState;

public class StorePortalSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperJobData dbHelperJobData;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TzHelper tzHelper;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private QuoteCreationHelper quoteCreationHelper;


    @Given("^a Watched Job$")
    public void a_watched_job() throws Throwable {
        Map<String, Object> watchedJob = dbHelperJobs.getWatchedJob();
        runtimeState.scenario.write("Watched Job details - " + watchedJob);
        testData.put("jobReference", watchedJob.get("jobReference"));
        testData.put("storePortalUserName", watchedJob.get("SiteCode"));
        testData.put("storePortalPassword", "Password1");
    }

    @Given("^a PPM Job with a due date for the current month$")
    public void ppm_job_with_due_date_for_current_month() throws Throwable {
        Map<String, Object> ppmDetails = dbHelperJobs.getPpmJobWithDueDateForCurrentMonth();
        runtimeState.scenario.write("PPM Job details - " + ppmDetails);
        testData.put("storePortalUserName", ppmDetails.get("SiteCode"));
        String password = "Password1";
        testData.put("storePortalPassword", password);
        testData.put("jobReference", ppmDetails.get("JobReference"));
        testData.put("jobDescription", ppmDetails.get("JobDescription"));
        testData.put("dayOfCurrentMonth", ppmDetails.get("DayOfCurrentMonth"));

        // Converting the ETA date to the local timezone
        //        String ETADate = DateHelper.dateAsString((Date) ppmDetails.get("ETA"), MEDIUM_DATE);
        //        ETADate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), ETADate, MEDIUM_DATE);
        //        testData.put("dayOfCurrentMonth", ETADate.split(" ")[0]);
    }

    @Given("^a Job with ETA for the current month$")
    public void job_with_eta_for_current_month() throws Throwable {
        Job job = jobCreationHelper.createJobInStatus("Allocated / ETA Provided");
        testData.put("jobReference", job.getJobReference());
        Map<String, Object> etaDetails = dbHelperJobs.getJobWithEtaForCurrentMonth(testData.getInt("jobReference"));
        runtimeState.scenario.write("ETA Job details - " + etaDetails);
        testData.put("storePortalUserName", etaDetails.get("SiteCode"));
        String password = "Password1";
        testData.put("storePortalPassword", password);
        testData.put("jobReference", etaDetails.get("JobReference"));
        testData.put("assetType", etaDetails.get("AssetType"));
        testData.put("location", etaDetails.get("Location"));
        testData.put("etaDetails", etaDetails.get("EtaDetails"));
        testData.put("dayOfCurrentMonth", etaDetails.get("DayOfCurrentMonth"));
    }

    @Given("^a Job is logged with Resource assigned$")
    public void job_is_logged_with_resource_assigned() throws Throwable {
        Job job = jobCreationHelper.createJobInStatus("Logged / New Notification Sent");
        testData.put("jobReference", job.getJobReference());
        int siteId = job.getSiteId();
        String siteCode = dbHelperSites.getSiteCodeFromSiteId(siteId);
        testData.put("storePortalUserName", siteCode);
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Job is logged with Resource accepted and provided ETA$")
    public void job_is_logged_with_resource_accepted_and_provided_eta() throws Throwable {
        Job job = jobCreationHelper.createJobInStatus("Allocated / ETA Provided");
        testData.put("jobReference", job.getJobReference());
        int siteId = job.getSiteId();
        String siteCode = dbHelperSites.getSiteCodeFromSiteId(siteId);
        testData.put("storePortalUserName", siteCode);
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Job with Feedback Response$")
    public void job_with_feedback_response() throws Throwable {
        Map<String, Object> job = dbHelperJobs.getJobWithFeedbackResponseWithinLast30Days();
        testData.put("jobReference", job.get("JobReference"));
        int siteId = (int) job.get("SiteId");
        String siteCode = dbHelperSites.getSiteCodeFromSiteId(siteId);
        testData.put("storePortalUserName", siteCode);
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Job is logged with Resource On Site$")
    public void job_is_logged_with_resource_on_site() throws Throwable {
        testData.put("resourceTypeName", "Contractor");
        Job job = jobCreationHelper.createJobInStatus("In Progress / On Site");
        testData.put("jobReference", job.getJobReference());
        int siteId = job.getSiteId();
        String siteCode = dbHelperSites.getSiteCodeFromSiteId(siteId);
        testData.put("storePortalUserName", siteCode);
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Job is logged with Resource Returning$")
    public void job_is_logged_with_resource_returning() throws Throwable {
        Job job = jobCreationHelper.createJobInStatus("Allocated / ETA Provided");
        jobCreationHelper.updateJobWithSiteVisit(job.getId(), job.getJobReference(), testData.getInt("resourceId"), job.getSiteId(), "Returning");
        testData.put("jobReference", job.getJobReference());
        int siteId = job.getSiteId();
        String siteCode = dbHelperSites.getSiteCodeFromSiteId(siteId);
        testData.put("storePortalUserName", siteCode);
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Job is logged with Resource Awaiting Parts$")
    public void job_is_logged_with_resource_awaiting_parts() throws Throwable {
        Job job = jobCreationHelper.createJobInStatus("In Progress / Resource Awaiting Parts Review");
        testData.put("jobReference", job.getJobReference());
        int siteId = job.getSiteId();
        String siteCode = dbHelperSites.getSiteCodeFromSiteId(siteId);
        testData.put("storePortalUserName", siteCode);
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Job is logged with Resource Complete$")
    public void job_is_logged_with_resource_complete() throws Throwable {
        Job job = jobCreationHelper.createJobInStatus("Complete");
        testData.put("jobReference", job.getJobReference());
        int siteId = job.getSiteId();
        String siteCode = dbHelperSites.getSiteCodeFromSiteId(siteId);
        testData.put("storePortalUserName", siteCode);
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Fixed Job with no previous feedback$")
    public void fixed_job_with_no_previous_feedback() throws Exception {
        Map<String, Object> jobDetails = dbHelperJobs.getFixedJobWithNoPreviousFeedback();
        testData.put("jobReference", jobDetails.get("JobReference"));
        testData.put("storePortalUserName", jobDetails.get("SiteCode"));
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^an active PPM Job$")
    public void active_ppm_job() throws Throwable {
        Map<String, Object> jobDetails = dbHelperJobs.getActivePpmJobWithDueDateForCurrentMonth();
        testData.put("jobReference", jobDetails.get("JobReference"));
        testData.put("storePortalUserName", jobDetails.get("SiteCode"));
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Quote Job has been logged$")
    public void quote_job_has_been_logged() throws Throwable {
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("quoteJobApprovalStatus", "ItqAwaitingAcceptance");
        queryMap.put("multiQuote", "single");
        queryMap.put("fundingRoute", "ignore");
        queryMap.put("profileName", "RFM");
        queryMap.put("useResourceTypeName", false);
        queryMap.put("resourceTypeName", "NA");
        queryMap.put("awaitingResourceSelectionBypass", "false");
        testData.put("originalProfileName", "RFM");

        runtimeState.scenario.write(Arrays.toString(queryMap.entrySet().toArray()));
        UserJob userJob = quoteCreationHelper.createQuote(queryMap);

        String siteCode = dbHelperSites.getSiteCodeFromSiteId(userJob.getSiteId());
        testData.put("storePortalUserName", siteCode);
        String password = "Password1";
        testData.put("storePortalPassword", password);
    }

    @Given("^a Job has been logged$")
    public void job_has_been_logged() throws Exception {
        String storeUser = dbHelperSites.getRandomSiteForStorePortal();
        testData.put("storePortalUserName", storeUser);
        String password = "Password1";
        testData.put("storePortalPassword", password);

        int siteId = dbHelperSites.getSiteIdFromSiteCode(storeUser);

        Map<String, Object> dbData = dbHelperJobData.getJobDataForResourceAndSite(null, siteId, null);

        int faultTypeId = (int) dbData.get("FaultTypeId");
        int faultPriorityId = (int) dbData.get("FaultPriorityId");
        int assetClassificationId = (int) dbData.get("AssetClassificationId");
        int locationId = (int) dbData.get("LocationId");

        Map<String, Object> caller = dbHelper.getRandomResourceCallerWithPhoneNumber("Resource");
        int callerId = (Integer) caller.get("Id");

        ApplicationUser applicationUser = jobCreationHelper.getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        Job job = apiHelperJobs.createNewJobForResource(1, siteId, faultTypeId, faultPriorityId, "Test Automation", callerId, assetClassificationId, locationId, null, null, createdBy);
        testData.put("jobReference", job.getJobReference());
    }

    @When("^\"([^\"]*)\" Tile is selected$")
    public void a_tile_is_selected(String tileName) {
        if (tileName.equalsIgnoreCase("Random")) {
            List<String> tileNames = Arrays.asList("Onsite", "Reactive", "PPM", "Quotes", "Closed", "Awaiting Feedback");
            for (int i = 0; i <= tileNames.size(); i ++) {
                String count = runtimeState.storePortalHomePage.getCountForTile(tileNames.get(i));
                if (!count.equals("0")) {
                    runtimeState.storePortalHomePage.viewJobsForTile(tileNames.get(i));
                    testData.put("countValue", count);
                    break;
                }
            }
        } else {
            runtimeState.storePortalHomePage.viewJobsForTile(tileName);
        }
        outputHelper.takeScreenshots();
    }

    @When("^the Job is searched for$")
    public void job_is_searched_for() {
        runtimeState.storePortalHomePage.searchForJob(testData.getString("jobReference"));
        outputHelper.takeScreenshots();
    }

    @When("^the Job is viewed$")
    public void job_is_viewed() {
        if (testData.getString("jobReference") != null) {
            runtimeState.storePortalHomePage.searchForJob(testData.getString("jobReference"));
        }
        String jobType = runtimeState.storePortalHomePage.getJobType();
        testData.put("jobType", jobType);
        runtimeState.storePortalJobDetailsPage = runtimeState.storePortalHomePage.selectFirstAvailableJob();
        outputHelper.takeScreenshots();
    }

    @When("^negative feedback is given on a fixed job$")
    public void negative_feedback_is_given() {
        a_tile_is_selected("Closed");
        job_is_viewed();

        POHelper.waitWhileBusy();
        if (dbHelperSystemToggles.getSystemSubFeatureToggle("ShowFullJobHistoryTimeline") == 1) {
            runtimeState.storePortalLeaveFeedbackModal = runtimeState.storePortalJobDetailsPage.clickLeaveFeedbackButtonToggleOn();
        } else {
            runtimeState.storePortalLeaveFeedbackModal = runtimeState.storePortalJobDetailsPage.clickLeaveFeedbackButtonToggleOff();
        }
        runtimeState.storePortalLeaveFeedbackModal.clickSadFeedback();
        String name = DataGenerator.generateRandomName();
        runtimeState.storePortalLeaveFeedbackModal.enterName(name);
        String additionalInformation = DataGenerator.GenerateRandomString(20, 20, 20, 0, 0, 0);
        runtimeState.storePortalLeaveFeedbackModal.enterAdditionalInformation(additionalInformation);
        outputHelper.takeScreenshots();
        runtimeState.storePortalLeaveFeedbackModal.clickSendButton();
        runtimeState.storePortalLeaveFeedbackModal.clickOkOnPopup();

        runtimeState.storePortalHomePage.selectMenuItem("Home");
    }

    @When("^a non watched Job is viewed$")
    public void non_watched_job_is_viewed() {
        runtimeState.storePortalJobDetailsPage = runtimeState.storePortalHomePage.selectFirstNotWatchedJob();
        outputHelper.takeScreenshots();
    }

    @When("^the Job is unselected to watch$")
    public void job_is_unselected_to_watch() {
        runtimeState.storePortalJobDetailsPage.watchOrUnwatchJob("Stop Watching");
        outputHelper.takeScreenshots();
        runtimeState.storePortalJobDetailsPage.selectMenuItem("Home");
    }

    @When("^the Job is selected to watch$")
    public void job_is_selected_to_watch() {
        String jobReference = runtimeState.storePortalJobDetailsPage.getJobReference();
        jobReference = StringUtils.substringAfter(jobReference, "Job ");
        testData.put("jobReference", jobReference);
        runtimeState.storePortalJobDetailsPage.watchOrUnwatchJob("Watch");
        outputHelper.takeScreenshots();
        runtimeState.storePortalJobDetailsPage.selectMenuItem("Home");
    }

    @When("^the Calendar menu is selected$")
    public void calendar_menu_is_selected() {
        runtimeState.storePortalHomePage.selectMenuItem("Calendar");
        runtimeState.storePortalCalendarPage = new StorePortalCalendarPage(getWebDriver()).get();
        outputHelper.takeScreenshots();
    }

    @When("^the Contact Us menu is selected$")
    public void contact_us_menu_is_selected() {
        runtimeState.storePortalHomePage.selectMenuItem("Contact Us");
        runtimeState.storePortalContactUsMenuPage = new StorePortalContactUsMenuPage(getWebDriver()).get();
        outputHelper.takeScreenshots();
    }

    @When("^a day with no scheduled jobs is selected$")
    public void day_with_no_scheduled_jobs_is_selected() {
        List<String> days = runtimeState.storePortalCalendarPage.getDaysWithNoEvents();
        Random random = new Random();
        int index = random.nextInt(days.size());
        String day = days.get(index);
        runtimeState.storePortalCalendarPage.selectCalendarDay(day);
        outputHelper.takeScreenshots();
    }

    @When("^the date of the Job is selected$")
    public void date_of_job_is_selected() {
        String dayOfMonth = testData.getString("dayOfCurrentMonth");
        runtimeState.storePortalCalendarPage.selectCalendarDay(dayOfMonth);
        outputHelper.takeScreenshots();
    }

    @When("^the Log a Job menu is selected$")
    public void log_a_job_menu_is_selected() {
        runtimeState.storePortalHomePage.selectMenuItem("Log a Job");
        runtimeState.storePortalLogAJobPage = new StorePortalLogAJobPage(getWebDriver()).get();
        outputHelper.takeScreenshots();
    }

    @When("^a Job is Logged$")
    public void job_is_logged() {
        String assetType = runtimeState.storePortalLogAJobPage.selectRandomOptionFromDropdown("Asset Type");
        testData.put("assetType", assetType);
        String location =  runtimeState.storePortalLogAJobPage.selectRandomOptionFromDropdown("Location");
        testData.put("location", location);
        String faultType =  runtimeState.storePortalLogAJobPage.selectRandomOptionFromDropdown("Fault Type");
        testData.put("faultType", faultType);
        String faultDescription = DataGenerator.GenerateRandomString(20, 20, 20, 0, 0, 0);
        runtimeState.storePortalLogAJobPage.enterTextIntoTextArea("Fault Description", faultDescription);
        testData.put("faultDescription", faultDescription);
        POHelper.waitWhileBusy();
        if (runtimeState.storePortalLogAJobPage.isJobQuestionsDisplayed()) {
            answerJobQuestions();
        }
        String siteContact = DataGenerator.generateRandomName();
        siteContact = siteContact.replaceAll("'", "");
        runtimeState.storePortalLogAJobPage.enterTextIntoTextBox("Site Contact", siteContact);
        testData.put("siteContact", siteContact);
        String jobTitle = DataGenerator.generateRandomJobTitle();
        runtimeState.storePortalLogAJobPage.enterTextIntoTextBox("Job Title", jobTitle);
        testData.put("jobTitle", jobTitle);
        String phoneNumber = DataGenerator.generatePhoneNumber();
        runtimeState.storePortalLogAJobPage.enterTextIntoTextBox("Phone Number", phoneNumber);
        testData.put("phoneNumber", phoneNumber);

        outputHelper.takeScreenshots();
        runtimeState.storePortalLogAJobPage.clickSaveButton();
    }

    @When("^a Duplicate Job is created$")
    public void duplicate_job_is_created() {
        runtimeState.storePortalJobDetailsPage = new StorePortalJobDetailsPage(getWebDriver()).get();
        String jobReference = runtimeState.storePortalJobDetailsPage.getJobReference();
        jobReference = StringUtils.substringAfter(jobReference, "Job ");
        testData.put("jobReference", jobReference);

        runtimeState.storePortalHomePage.selectMenuItem("Log a Job");

        runtimeState.storePortalLogAJobPage.selectOptionFromDropdown("Asset Type", testData.getString("assetType"));
        runtimeState.storePortalLogAJobPage.selectOptionFromDropdown("Location", testData.getString("location"));
        runtimeState.storePortalLogAJobPage.selectOptionFromDropdown("Fault Type", testData.getString("faultType"));
        POHelper.waitWhileBusy();
        outputHelper.takeScreenshots();
    }

    public void answerJobQuestions() {
        runtimeState.storePortalLogAJobPage.waitForJobQuestionsToBeDisplayed();

        List<String> questions = runtimeState.storePortalLogAJobPage.getJobQuestions();
        for (int index = 0; index < questions.size(); index++) {
            String question = questions.get(index);
            String answer = "";
            String[] yesNo = {"Yes", "No"};
            Random random = new Random();
            int randomIndex = 0;
            String questionTagName = runtimeState.storePortalLogAJobPage.getJobQuestionTagName(question);
            switch(questionTagName){
            case "cfm-radio-button":
                randomIndex = random.nextInt(yesNo.length);
                answer = yesNo[randomIndex];
                runtimeState.storePortalLogAJobPage.clickJobQuestionRadioAnswerButton(question, answer);
                break;
            case "cfm-text-input":
                answer = DataGenerator.GenerateRandomString(20, 20, 20, 0, 0, 0);
                runtimeState.storePortalLogAJobPage.enterTextIntoJobQuestionTextBox(question, answer);
                break;
            case "cfm-text-area":
                answer = DataGenerator.GenerateRandomString(20, 20, 20, 0, 0, 0);
                runtimeState.storePortalLogAJobPage.enterTextIntoJobQuestionTextArea(question, answer);
                break;
            case "cfm-drop-down-list":
                answer = runtimeState.storePortalLogAJobPage.selectRandomOptionFromJobQuestionDropdown(question);
                break;
            }
            testData.put(question, answer);
            runtimeState.scenario.write("Question: " + question);
            runtimeState.scenario.write("Answer: " + answer);

            questions = runtimeState.storePortalLogAJobPage.getJobQuestions();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Contact Us details are correct$")
    public void contact_us_details_are_correct() throws Exception {
        runtimeState.scenario.write("Asserting that the Contact Us section displays the correct Helpdesk Contact Number and the Contact name for each Role");
        int siteId = dbHelperSites.getSiteIdFromSiteCode(testData.getString("storePortalUserName"));
        if (LOCALE.equals("en-US")) {
            String expectedHelpdeskContactNumber = "(904) 512-1200";
            String actualHelpdeskContactNumber = runtimeState.storePortalContactUsMenuPage.getContactUsNumber();
            assertEquals("Expected: " + expectedHelpdeskContactNumber + "but was: " + actualHelpdeskContactNumber,
                    expectedHelpdeskContactNumber, actualHelpdeskContactNumber);

            String expectedDistrictDirector = dbHelperSites.getManagerNameForSiteAndPosition(siteId, "District Director");
            boolean isDistrictDirectorAbsent = checkIfResourceIsAbsent(expectedDistrictDirector);
            if (isDistrictDirectorAbsent == false) {
                String actualDistrictDirector = runtimeState.storePortalContactUsMenuPage.getContactUsRole("District Director");
                assertEquals("Expected: " + expectedDistrictDirector + "but was: " + actualDistrictDirector,
                        expectedDistrictDirector, actualDistrictDirector);
            }

            String expectedRfm = dbHelperSites.getManagerNameForSiteAndPosition(siteId, "RFM");
            boolean isRfmAbsent = checkIfResourceIsAbsent(expectedRfm);
            if (isRfmAbsent == false) {
                String actualRfm = runtimeState.storePortalContactUsMenuPage.getContactUsRole("RFM");
                assertEquals("Expected: " + expectedRfm + "but was: " + actualRfm,
                        expectedRfm, actualRfm);
            }

            String expectedRhvacSupervisor = dbHelperSites.getManagerNameForSiteAndPosition(siteId, "RHVAC Supervisor");
            boolean isRhvacSupervisorAbsent = checkIfResourceIsAbsent(expectedRhvacSupervisor);
            if (isRhvacSupervisorAbsent == false) {
                String actualRhvacSupervisor = runtimeState.storePortalContactUsMenuPage.getContactUsRole("RHVAC Supervisor");
                assertEquals("Expected: " + expectedRhvacSupervisor + "but was: " + actualRhvacSupervisor,
                        expectedRhvacSupervisor, actualRhvacSupervisor);
            }

            String expectedMstSupervisor = dbHelperSites.getManagerNameForSiteAndPosition(siteId, "MST Supervisor");
            boolean isMstSupervisorAbsent = checkIfResourceIsAbsent(expectedMstSupervisor);
            if (isMstSupervisorAbsent == false) {
                String actualMstSupervisor = runtimeState.storePortalContactUsMenuPage.getContactUsRole("MST Supervisor");
                assertEquals("Expected: " + expectedMstSupervisor + "but was: " + actualMstSupervisor,
                        expectedMstSupervisor, actualMstSupervisor);
            }
        } else {
            String expectedHelpdeskContactNumber = "0208 718 2182";
            String actualHelpdeskContactNumber = runtimeState.storePortalContactUsMenuPage.getContactUsNumber();
            assertEquals("Expected: " + expectedHelpdeskContactNumber + "but was: " + actualHelpdeskContactNumber,
                    expectedHelpdeskContactNumber, actualHelpdeskContactNumber);

            String expectedAmm = dbHelperSites.getManagerNameForSiteAndPosition(siteId, "AMM");
            boolean isAmmAbsent = checkIfResourceIsAbsent(expectedAmm);
            if (isAmmAbsent == false) {
                String actualAmm = runtimeState.storePortalContactUsMenuPage.getContactUsRole("AMM");
                assertEquals("Expected: " + expectedAmm + "but was: " + actualAmm,
                        expectedAmm, actualAmm);
            }

            String expectedDivisionalOpsManager = dbHelperSites.getManagerNameForSiteAndPosition(siteId, "Divisional Operations Manager");
            boolean isDivisionalOpsManagerAbsent = checkIfResourceIsAbsent(expectedDivisionalOpsManager);
            if (isDivisionalOpsManagerAbsent == false) {
                String actualDivisionalOpsManager = runtimeState.storePortalContactUsMenuPage.getContactUsRole("Divisional Operations Manager");
                assertEquals("Expected: " + expectedDivisionalOpsManager + "but was: " + actualDivisionalOpsManager,
                        expectedDivisionalOpsManager, actualDivisionalOpsManager);
            }
        }
    }

    public boolean checkIfResourceIsAbsent(String resourceName) {
        int resourceId = dbHelperResources.getResourceId(resourceName);
        boolean resourceIsAbsent = dbHelperResources.isResourceAbsent(resourceId);
        return resourceIsAbsent;
    }

    @ContinueNextStepsOnException
    @Then("^the Job \"([^\"]*)\" present in the Watched Job list$")
    public void job_present_in_the_watched_job_list(String presentInList) {
        runtimeState.storePortalHomePage.searchForJob(testData.getString("jobReference"));

        List<String> gridRows = runtimeState.storePortalHomePage.getGridRows();

        if (presentInList.equals("is")) {
            String actualJobReference = gridRows.get(0).substring(0, 8);
            runtimeState.scenario.write("Asserting that Job is present in list");
            assertEquals("Job is not present in list", testData.getString("jobReference"), actualJobReference);
        } else {
            runtimeState.scenario.write("Asserting that Job is not present in list");
            assertTrue("Grid is not empty", gridRows.isEmpty());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Job \"([^\"]*)\" on the Watched Jobs monitor$")
    public void job_on_the_watched_job_monitor(String presentOnMonitor) {
        if (presentOnMonitor.equals("is")) {
            runtimeState.scenario.write("Asserting that Job is on the Watched Job Monitor");
            assertTrue("Job is not on the monitor", dbHelperJobs.isJobOnMonitor("dbo.uvw_monitorHelpdeskJobWatched", testData.getInt("jobReference")));
        } else {
            runtimeState.scenario.write("Asserting that Job is not on the Watched Job Monitor");
            assertFalse("Job is on the monitor", dbHelperJobs.isJobOnMonitor("dbo.uvw_monitorHelpdeskJobWatched", testData.getInt("jobReference")));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Job is present in the Reactive list$")
    public void job_is_present_in_the_reactive_list() throws Throwable {
        runtimeState.storePortalJobDetailsPage = new StorePortalJobDetailsPage(getWebDriver()).get();
        String jobReference = runtimeState.storePortalJobDetailsPage.getJobReference();
        jobReference = StringUtils.substringAfter(jobReference, "Job ");

        runtimeState.storePortalJobDetailsPage.selectMenuItem("Home");
        runtimeState.storePortalHomePage = new StorePortalHomePage(getWebDriver()).get();
        runtimeState.storePortalHomePage.viewJobsForTile("Reactive");
        runtimeState.storePortalHomePage.searchForJob(jobReference);

        List<String> gridRows = runtimeState.storePortalHomePage.getGridRows();
        assertNotNull("Grid is empty.", gridRows);
        String actualJobReference = gridRows.get(0).substring(0, 8);

        runtimeState.scenario.write("Asserting that Job is present in list");
        assertEquals("Job is not present in list", jobReference, actualJobReference);
    }

    @ContinueNextStepsOnException
    @Then("^the Job details are displayed correctly$")
    public void job_details_displayed_correctly() throws Exception {
        String jobReference = runtimeState.storePortalJobDetailsPage.getJobReference();
        jobReference = StringUtils.substringAfter(jobReference, "Job ");
        Map<String, Object> jobDetails = null;

        if (testData.get("jobType").equals("Reactive") || testData.get("jobType").equals("Quote")) {
            assertReactiveAndQuoteJobDetails(jobDetails, jobReference);
        } else if (testData.get("jobType").equals("PPM")) {
            assertPPMJobDetails(jobDetails, jobReference);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" Job details are displayed correctly$")
    public void job_details_are_displayed_correctly(String jobType) throws Exception {
        if (jobType.equals("PPM")) {
            String description = testData.getString("jobDescription").substring(0, testData.getString("jobDescription").length() -4);
            runtimeState.scenario.write("Asserting that PPM Job has the correct details");
            assertTrue("PPM Job is not displayed", runtimeState.storePortalCalendarPage.isJobDetailsDisplayed(description));
        } else {
            String assetType = null;
            if (testData.get("assetType").toString().endsWith(" > ")) {
                assetType = testData.get("assetType").toString().substring(0, testData.get("assetType").toString().length() - 3);
            } else {
                assetType = testData.get("assetType").toString();
            }

            String etaTimeFull = StringUtils.substringBetween(testData.get("etaDetails").toString(), "between ", "\"");
            String etaTime1 = StringUtils.substringBefore(etaTimeFull, " -");
            String etaTime2 = StringUtils.substringAfter(etaTimeFull, "- ");

            DateFormat inputFormat = new SimpleDateFormat("HH:mm");
            DateFormat outputFormat = new SimpleDateFormat("h:mm a");
            etaTime1 = outputFormat.format(inputFormat.parse(etaTime1)).replace("am", "AM").replace("pm", "PM");
            etaTime2 = outputFormat.format(inputFormat.parse(etaTime2)).replace("am", "AM").replace("pm", "PM");

            runtimeState.scenario.write("Asserting that ETA Job has the correct details");
            assertTrue("PPM Job is not displayed", runtimeState.storePortalCalendarPage.isJobDetailsDisplayed(assetType));
            assertTrue("PPM Job is not displayed", runtimeState.storePortalCalendarPage.isJobDetailsDisplayed(testData.getString("location")));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the message \"([^\"]*)\" is displayed on the timeline$")
    public void message_displayed_on_timeline(String expectedMessage) throws Exception {
        String actualMessage = runtimeState.storePortalCalendarPage.getNoVisitsScheduledMessage();
        runtimeState.scenario.write("Asserting that No Visits Scheduled message is correct");
        assertEquals("Expected: " + expectedMessage + " but was: " + actualMessage, expectedMessage, actualMessage);
    }

    @ContinueNextStepsOnException
    @Then("^the Job details are displayed correctly for tile \"([^\"]*)\"$")
    public void job_details_from_table_displayed_correctly(String tileName) throws Exception {
        Map<String, Object> jobDetails = null;
        if (tileName.equals("PPM")) {
            jobDetails = dbHelperJobs.getPpmJobDetailsForStorePortalTables(testData.getInt("jobReference"));
        } else {
            jobDetails = dbHelperJobs.getJobDetailsForStorePortalTables(testData.getInt("jobReference"));
        }

        POHelper.waitForAngularRequestsToFinish();
        outputHelper.takeScreenshots();
        String expectedAssetType = null;
        if (jobDetails.get("AssetType").toString().endsWith(" > ")) {
            expectedAssetType = jobDetails.get("AssetType").toString().substring(0, jobDetails.get("AssetType").toString().length() - 3);
        } else {
            expectedAssetType = jobDetails.get("AssetType").toString();
        }

        String jobId = runtimeState.storePortalHomePage.getJobId();
        String assetType = null;
        String jobType = null;
        String loggedDate = null;
        String onSiteResource = null;
        String priority = null;
        String closedDate = null;
        String eta = null;
        String raisedBy = null;
        String feedbackRating = null;
        String comments = null;

        runtimeState.scenario.write("Asserting that Job Details are correct");
        assertEquals("Expected: " + jobDetails.get("JobId").toString() + " but was: " + jobId,
                jobDetails.get("JobId").toString(), jobId);

        if (!tileName.equals("Closed")) {
            loggedDate = runtimeState.storePortalHomePage.getLoggedDate();
            assertEquals("Expected: " + jobDetails.get("LoggedDate").toString() + " but was: " + loggedDate,
                    jobDetails.get("LoggedDate").toString(), loggedDate);
            // Converting the Logged Date to the local timezone
            //         runtimeState.scenario.write("Logged date before conversion: " + jobDetails.get("LoggedDate"));
            //         String jobLoggedDate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), jobDetails.get("LoggedDate").toString(), MEDIUM_DATE);
            //         runtimeState.scenario.write("Logged date after conversion: " + jobLoggedDate);
            //         assertEquals("Expected: " + jobLoggedDate + " but was: " + loggedDate,
            //                 jobLoggedDate, loggedDate);
        }

        if (!tileName.equals("Pending")) {
            jobType = runtimeState.storePortalHomePage.getJobType();
            assertEquals("Expected: " + jobDetails.get("JobType").toString() + " but was: " + jobType,
                    jobDetails.get("JobType").toString(), jobType);

            assetType = runtimeState.storePortalHomePage.getAssetType();
            assertEquals("Expected: " + normalize(expectedAssetType) + " but was: " + assetType,
                    normalize(expectedAssetType), assetType);
        }

        if (tileName.equals("Onsite")) {
            onSiteResource = runtimeState.storePortalHomePage.getOnSiteResource();
            assertEquals("Expected: " + normalize(jobDetails.get("OnSiteResource").toString()) + " but was: " + onSiteResource,
                    normalize(jobDetails.get("OnSiteResource").toString()), onSiteResource);
        }

        if (tileName.equals("Reactive")) {
            priority = runtimeState.storePortalHomePage.getPriority();
            assertTrue("Expected: " + jobDetails.get("Priority").toString() + " but was: " + priority,
                    jobDetails.get("Priority").toString().equals(priority) || priority.contains(jobDetails.get("FaultPriority").toString()));
        }

        if (tileName.equals("Closed")) {
            closedDate = runtimeState.storePortalHomePage.getClosedDate();
            assertEquals("Expected: " + jobDetails.get("ClosedDate").toString() + " but was: " + closedDate,
                    jobDetails.get("ClosedDate").toString(), closedDate);
            // Converting the Closed Date to the local timezone
            //         runtimeState.scenario.write("Closed date before conversion: " + jobDetails.get("ClosedDate"));
            //         String jobClosedDate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), jobDetails.get("ClosedDate").toString(), MEDIUM_DATE);
            //         runtimeState.scenario.write("Closed date after conversion: " + jobClosedDate);
            //         assertEquals("Expected: " + jobClosedDate + " but was: " + closedDate,
            //                 jobClosedDate, closedDate);
        }

        if (tileName.equals("PPM")) {
            eta = runtimeState.storePortalHomePage.getEta();

            // Converting the ETA Date to the local timezone
            runtimeState.scenario.write("ETA date before conversion: " + jobDetails.get("ETA"));
            String jobETA = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), jobDetails.get("ETA").toString(), "d MMM yyyy");
            runtimeState.scenario.write("ETA date after conversion: " + jobETA);
            assertEquals("Expected: " + jobETA + " but was: " + eta,
                    jobETA, eta);
        }

        if (tileName.equals("Pending")) {
            raisedBy = runtimeState.storePortalHomePage.getRaisedBy();
            assertEquals("Expected: " + jobDetails.get("RaisedBy").toString() + " but was: " + raisedBy,
                    jobDetails.get("RaisedBy").toString(), raisedBy);

            feedbackRating = runtimeState.storePortalHomePage.getFeedbackRating();
            assertEquals("Expected: " + jobDetails.get("FeedbackRating").toString() + " but was: " + feedbackRating,
                    jobDetails.get("FeedbackRating").toString(), feedbackRating);

            comments = runtimeState.storePortalHomePage.getComments();
            assertEquals("Expected: " + jobDetails.get("Comments").toString() + " but was: " + comments,
                    jobDetails.get("Comments").toString(), comments);
        }
    }

    public void assertReactiveAndQuoteJobDetails(Map<String, Object> jobDetails, String jobReference) throws NumberFormatException, Exception {
        jobDetails = dbHelperJobs.getJobDetailsForStorePortal((Integer.parseInt(jobReference)));
        String assetType = runtimeState.storePortalJobDetailsPage.getJobDetail("Asset Type");
        String expectedAssetType = null;
        if (jobDetails.get("AssetType").toString().endsWith(" > ")) {
            expectedAssetType = jobDetails.get("AssetType").toString().substring(0, jobDetails.get("AssetType").toString().length() - 3);
        } else {
            expectedAssetType = jobDetails.get("AssetType").toString();
        }
        String location = runtimeState.storePortalJobDetailsPage.getJobDetail("Location");
        String faultType = runtimeState.storePortalJobDetailsPage.getJobDetail("Fault Type");
        String faultPriority = runtimeState.storePortalJobDetailsPage.getJobDetail("Fault Priority");
        String description = runtimeState.storePortalJobDetailsPage.getJobDetail("Description");

        Map<String, Object> callerDetails = null;
        if (jobDetails.get("CallerTypeId").equals("1")) {
            callerDetails = dbHelperJobs.getClientCallerDetails((Integer.parseInt(jobReference)));
        }

        String siteContact = runtimeState.storePortalJobDetailsPage.getJobDetail("Site Contact");
        String jobRole = runtimeState.storePortalJobDetailsPage.getJobDetail("Job Role");

        runtimeState.scenario.write("Asserting that Job Details are correct");
        assertEquals("Expected: " + expectedAssetType + " but was: " + assetType,
                expectedAssetType, assetType);
        assertEquals("Expected: " + jobDetails.get("Location").toString() + " but was: " + location,
                jobDetails.get("Location").toString(), location);
        assertEquals("Expected: " + jobDetails.get("FaultType").toString() + " but was: " + faultType,
                jobDetails.get("FaultType").toString(), faultType);
        assertEquals("Expected: " + jobDetails.get("FaultPriority").toString() + " but was: " + faultPriority,
                jobDetails.get("FaultPriority").toString(), faultPriority);
        assertEquals("Expected: " + normalize(jobDetails.get("Description").toString()) + " but was: " + normalize(description),
                normalize(jobDetails.get("Description").toString()), normalize(description));
        if (jobDetails.get("CallerTypeId").equals("1")) {
            assertEquals("Expected: " + callerDetails.get("SiteContact").toString() + " but was: " + siteContact,
                    callerDetails.get("SiteContact").toString(), siteContact);
            assertEquals("Expected: " + callerDetails.get("JobRole").toString() + " but was: " + jobRole,
                    callerDetails.get("JobRole").toString(), jobRole);
        }
    }

    public void assertPPMJobDetails(Map<String, Object> jobDetails, String jobReference) throws NumberFormatException, Exception {
        jobDetails = dbHelperJobs.getPPMJobDetailsForStorePortal((Integer.parseInt(jobReference)));
        String description = runtimeState.storePortalJobDetailsPage.getJobDetail("Description");
        String expectedDescription = null;
        if (jobDetails.get("JobDescription").toString().endsWith("PPM")) {
            expectedDescription = jobDetails.get("JobDescription").toString().substring(0, jobDetails.get("JobDescription").toString().length() -4);
        } else {
            expectedDescription = jobDetails.get("JobDescription").toString();
        }

        runtimeState.scenario.write("Asserting that Job Details are correct");
        assertEquals("Expected: " + expectedDescription + " but was: " + description,
                expectedDescription, description);
    }

    @ContinueNextStepsOnException
    @Then("^the user can add \"([^\"]*)\" details and history is avaialable$")
    public void user_can_add_details(String details) throws Exception {
        if (details.equalsIgnoreCase("Contact Us")) {
            addContactUsDetails();
            assertContactUsDetails();
        } else if (details.equalsIgnoreCase("Feedback")) {
            addFeedbackDetails();
            assertFeedbackDetails();
        }
    }

    public void addContactUsDetails() {
        runtimeState.storePortalContactUsModal = runtimeState.storePortalJobDetailsPage.clickContactUsButton();
        String name = DataGenerator.generateRandomName();
        runtimeState.storePortalContactUsModal.enterName(name);
        String contactNumber = null;
        if (LOCALE.equals("en-US")) {
            contactNumber = DataGenerator.GenerateRandomString(10, 10, 0, 0, 10, 0);
        } else {
            contactNumber = DataGenerator.GenerateRandomString(11, 11, 0, 0, 11, 0);
        }
        runtimeState.storePortalContactUsModal.enterContactNumber(contactNumber);
        String[] yesNo = {"Yes", "No"};
        Random random = new Random();
        int randomIndex = random.nextInt(yesNo.length);
        if (yesNo[randomIndex].equals("Yes")) {
            runtimeState.storePortalContactUsModal.clickRadioYesButton();
        } else {
            runtimeState.storePortalContactUsModal.clickRadioNoButton();
        }
        String message = DataGenerator.GenerateRandomString(20, 20, 20, 0, 0, 0);
        runtimeState.storePortalContactUsModal.enterMessage(message);
        outputHelper.takeScreenshots();
        runtimeState.storePortalContactUsModal.clickSendButton();
    }

    public void assertContactUsDetails() throws Exception {
        runtimeState.storePortalJobDetailsPage.viewDetails();
        outputHelper.takeScreenshots();
        Map<String, Object> chaseDetails = dbHelperJobs.getJobChaseDetails(testData.getInt("jobReference"));
        String chaseSubmittedBy = runtimeState.storePortalJobDetailsPage.getSubmittedByToggleOff();
        String chaseContactNumber = runtimeState.storePortalJobDetailsPage.getContactNumber();
        String chaseDescription = runtimeState.storePortalJobDetailsPage.getDescriptionToggleOff();

        runtimeState.scenario.write("Asserting Contact Us details are correct");
        assertEquals("Expected: " + chaseDetails.get("SubmittedBy").toString() + " but was: " + chaseSubmittedBy,
                chaseDetails.get("SubmittedBy").toString(), chaseSubmittedBy);
        assertEquals("Expected: " + chaseDetails.get("ContactNumber").toString() + " but was: " + chaseContactNumber,
                chaseDetails.get("ContactNumber").toString(), chaseContactNumber);
        assertEquals("Expected: " + chaseDetails.get("Description").toString() + " but was: " + chaseDescription,
                chaseDetails.get("Description").toString(), chaseDescription);
    }

    public void addFeedbackDetails() {
        if (dbHelperSystemToggles.getSystemSubFeatureToggle("ShowFullJobHistoryTimeline") == 1) {
            runtimeState.storePortalLeaveFeedbackModal = runtimeState.storePortalJobDetailsPage.clickLeaveFeedbackButtonToggleOn();
        } else {
            runtimeState.storePortalLeaveFeedbackModal = runtimeState.storePortalJobDetailsPage.clickLeaveFeedbackButtonToggleOff();
        }
        String[] happySad = {"Happy", "Sad"};
        Random random = new Random();
        int randomIndex = random.nextInt(happySad.length);
        if (happySad[randomIndex].equals("Happy")) {
            runtimeState.storePortalLeaveFeedbackModal.clickHappyFeedback();
            testData.put("feedbackResponse", "Happy");
        } else {
            runtimeState.storePortalLeaveFeedbackModal.clickSadFeedback();
            testData.put("feedbackResponse", "Sad");
        }
        String name = DataGenerator.generateRandomName();
        runtimeState.storePortalLeaveFeedbackModal.enterName(name);
        String additionalInformation = DataGenerator.GenerateRandomString(20, 20, 20, 0, 0, 0);
        if (testData.get("feedbackResponse").equals("Happy")) {
            runtimeState.storePortalLeaveFeedbackModal.enterAdditionalFeedback(additionalInformation);
        } else {
            runtimeState.storePortalLeaveFeedbackModal.enterAdditionalInformation(additionalInformation);
        }
        outputHelper.takeScreenshots();
        runtimeState.storePortalLeaveFeedbackModal.clickSendButton();
        runtimeState.storePortalLeaveFeedbackModal.clickOkOnPopup();
    }

    public void assertFeedbackDetails() throws Exception {
        Map<String, Object> feedbackDetails = dbHelperJobs.getJobFeedbackDetails(testData.getInt("jobReference"));
        String feedbackSubmittedBy = null;
        String feedbackDescription = null;
        if (dbHelperSystemToggles.getSystemSubFeatureToggle("ShowFullJobHistoryTimeline") == 1) {
            outputHelper.takeScreenshots();
            feedbackSubmittedBy = runtimeState.storePortalJobDetailsPage.getSubmittedByToggleOn();
            feedbackDescription = runtimeState.storePortalJobDetailsPage.getDescriptionToggleOn();
        } else {
            runtimeState.storePortalJobDetailsPage.viewDetails();
            outputHelper.takeScreenshots();
            feedbackSubmittedBy = runtimeState.storePortalJobDetailsPage.getSubmittedByToggleOff();
            feedbackDescription = runtimeState.storePortalJobDetailsPage.getDescriptionToggleOff();
        }

        runtimeState.scenario.write("Asserting Feedback details are correct");
        if (testData.get("feedbackResponse").equals("Happy")) {
            assertTrue("Feedback rating does not match", feedbackDetails.get("JobFeedbackRatingId").equals(2));
        } else {
            assertTrue("Feedback rating does not match", feedbackDetails.get("JobFeedbackRatingId").equals(1));
        }

        assertEquals("Expected: " + feedbackDetails.get("SubmittedBy").toString() + " but was: " + feedbackSubmittedBy,
                feedbackDetails.get("SubmittedBy").toString(), feedbackSubmittedBy);
        assertEquals("Expected: " + feedbackDetails.get("FeedbackComments").toString() + " but was: " + feedbackDescription,
                feedbackDetails.get("FeedbackComments").toString(), feedbackDescription);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" Job contains the message: \"([^\"]*)\"$")
    public void Job_contains_message(String jobStatus, String expectedMessage) throws ParseException {
        String actualMessage = runtimeState.storePortalJobDetailsPage.getJobProgressStatus();

        if (jobStatus.equals("Allocated / ETA Provided")) {
            expectedMessage = updateExpectedMessageForResourceAcceptedAndProvidedEta(expectedMessage);

        } else if (jobStatus.equals("In Progress / On Site")) {
            expectedMessage = updateExpectedMessageForResourceOnSite(expectedMessage);

        } else if (jobStatus.equals("In Progress / Returning")) {
            expectedMessage = updateExpectedMessageForResourceReturning(expectedMessage);

        } else if (jobStatus.equals("In Progress / Awaiting Parts Review")) {
            expectedMessage = updateExpectedMessageForResourceAwaitingParts(expectedMessage);

        } else if (jobStatus.equals("Fixed / Complete")) {
            expectedMessage = updateExpectedMessageForResourceComplete(expectedMessage);
        }

        runtimeState.scenario.write("Asserting the Job Progress Status is correct");
        assertEquals("Expected: " + expectedMessage + " but was: " + actualMessage, expectedMessage, actualMessage);
    }

    public String updateExpectedMessageForResourceAcceptedAndProvidedEta(String expectedMessage) throws ParseException {
        Map<String, Object> etaDetails = dbHelperJobs.getResourceEtaTimeAndDate(testData.getInt("jobReference"), 4);
        String resourceName = normalize(etaDetails.get("ResourceName").toString());
        String etaDate = StringUtils.substringBetween(etaDetails.get("Detail2").toString(), "ST - " , " between");
        String etaTimeFull = StringUtils.substringBetween(etaDetails.get("Detail2").toString(), "between ", "\"");
        String etaTime1 = StringUtils.substringBefore(etaTimeFull, " -");
        String etaTime2 = StringUtils.substringAfter(etaTimeFull, "- ");

        Properties props = initializeProperties();
        if (LOCALE.equals("en-GB")) {
            DateFormat inputTimeFormat = new SimpleDateFormat(MS_SHORT_TIME);
            DateFormat outputTimeFormat = new SimpleDateFormat(props.getProperty("SHORT_TIME2"));
            etaTime1 = outputTimeFormat.format(inputTimeFormat.parse(etaTime1)).replace("am", "AM").replace("pm", "PM");
            etaTime2 = outputTimeFormat.format(inputTimeFormat.parse(etaTime2)).replace("am", "AM").replace("pm", "PM");
        } else {
            DateFormat inputDateFormat = new SimpleDateFormat(MEDIUM_DATE);
            DateFormat outputDateFormat = new SimpleDateFormat(props.getProperty("MS_MEDIUM_DATE"));
            etaDate = outputDateFormat.format(inputDateFormat.parse(etaDate));
        }

        String etaTime = etaTime1 + " - " + etaTime2;

        expectedMessage = expectedMessage.replace("@Resource", resourceName).replace("@EtaDate", etaDate).replace("@EtaTime", etaTime);

        return expectedMessage;
    }

    public String updateExpectedMessageForResourceOnSite(String expectedMessage) throws ParseException {
        List<String> assignedResources = dbHelperResources.getAssignedResourcesByName(testData.getInt("jobReference"));
        String resourceName = normalize(assignedResources.get(0).toString());

        expectedMessage = expectedMessage.replace("@Resource", resourceName);

        return expectedMessage;
    }

    public String updateExpectedMessageForResourceReturning(String expectedMessage) throws ParseException {
        Map<String, Object> etaDetails = dbHelperJobs.getResourceEtaTimeAndDate(testData.getInt("jobReference"), 32);
        String resourceName = normalize(etaDetails.get("ResourceName").toString());
        String etaDate = StringUtils.substringBetween(etaDetails.get("Detail2").toString(), "ST - " , " between");
        String etaTimeFull = StringUtils.substringBetween(etaDetails.get("Detail2").toString(), "between ", "\"");
        String etaTime1 = StringUtils.substringBefore(etaTimeFull, " -");
        String etaTime2 = StringUtils.substringAfter(etaTimeFull, "- ");

        String resourceStatus;
        if (LOCALE.equals("en-GB")) {
            resourceStatus = dbHelperResources.getResourceReturningStatus(testData.getInt("jobReference"));
            resourceStatus = "Returning - " + resourceStatus;
        } else {
            resourceStatus = "returning";
        }

        Properties props = initializeProperties();
        if (LOCALE.equals("en-GB")) {
            DateFormat inputTimeFormat = new SimpleDateFormat(MS_SHORT_TIME);
            DateFormat outputTimeFormat = new SimpleDateFormat(props.getProperty("SHORT_TIME2"));
            etaTime1 = outputTimeFormat.format(inputTimeFormat.parse(etaTime1)).replace("am", "AM").replace("pm", "PM");
            etaTime2 = outputTimeFormat.format(inputTimeFormat.parse(etaTime2)).replace("am", "AM").replace("pm", "PM");
        } else {
            DateFormat inputDateFormat = new SimpleDateFormat(MEDIUM_DATE);
            DateFormat outputDateFormat = new SimpleDateFormat(props.getProperty("MS_MEDIUM_DATE"));
            etaDate = outputDateFormat.format(inputDateFormat.parse(etaDate));
        }

        String etaTime = etaTime1 + " - " + etaTime2;

        expectedMessage = expectedMessage.replace("@Resource", resourceName).replace("@Status", resourceStatus).replace("@EtaDate", etaDate).replace("@EtaTime", etaTime);

        return expectedMessage;
    }

    public String updateExpectedMessageForResourceAwaitingParts(String expectedMessage) throws ParseException {
        Map<String, Object> etaDetails = dbHelperJobs.getResourceEtaTimeAndDate(testData.getInt("jobReference"), 32);
        String resourceName = normalize(etaDetails.get("ResourceName").toString());
        String etaDate = StringUtils.substringBetween(etaDetails.get("Detail2").toString(), "ST - " , " between");
        String etaTimeFull = StringUtils.substringBetween(etaDetails.get("Detail2").toString(), "between ", "\"");
        String etaTime1 = StringUtils.substringBefore(etaTimeFull, " -");
        String etaTime2 = StringUtils.substringAfter(etaTimeFull, "- ");

        String resourceStatus;
        if (LOCALE.equals("en-GB")) {
            resourceStatus = dbHelperResources.getResourceReturningAwaitingPartsStatus(testData.getInt("jobReference"));
            resourceStatus = "Returning - " + StringUtils.substringBetween(resourceStatus, "Resource ", " - ");
        } else {
            resourceStatus = "returning";
        }

        Properties props = initializeProperties();
        if (LOCALE.equals("en-GB")) {
            DateFormat inputTimeFormat = new SimpleDateFormat(MS_SHORT_TIME);
            DateFormat outputTimeFormat = new SimpleDateFormat(props.getProperty("SHORT_TIME2"));
            etaTime1 = outputTimeFormat.format(inputTimeFormat.parse(etaTime1)).replace("am", "AM").replace("pm", "PM");
            etaTime2 = outputTimeFormat.format(inputTimeFormat.parse(etaTime2)).replace("am", "AM").replace("pm", "PM");
        } else {
            DateFormat inputDateFormat = new SimpleDateFormat(MEDIUM_DATE);
            DateFormat outputDateFormat = new SimpleDateFormat(props.getProperty("MS_MEDIUM_DATE"));
            etaDate = outputDateFormat.format(inputDateFormat.parse(etaDate));
        }

        String etaTime = etaTime1 + " - " + etaTime2;

        expectedMessage = expectedMessage.replace("@Resource", resourceName).replace("@Status", resourceStatus).replace("@EtaDate", etaDate).replace("@EtaTime", etaTime);

        return expectedMessage;
    }

    public String updateExpectedMessageForResourceComplete(String expectedMessage) throws ParseException {
        Map<String, Object> jobCompletionDetails = dbHelperJobs.getResourceNameAndCompletionDate(testData.getInt("jobReference"));
        String resourceName = normalize(jobCompletionDetails.get("ResourceName").toString());
        String jobCompletionDateAndTime = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), jobCompletionDetails.get("CompletedDate").toString(), DB_DATE_FORMAT);

        String jobCompletionDate = StringUtils.substringBefore(jobCompletionDateAndTime, " ");
        DateFormat inputFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormatDate = new SimpleDateFormat("dd MMM yyyy");
        jobCompletionDate = outputFormatDate.format(inputFormatDate.parse(jobCompletionDate));

        String jobCompletionTime = jobCompletionDateAndTime.substring(11, 16);
        DateFormat inputFormatTime = new SimpleDateFormat("HH:mm");
        DateFormat outputFormatTime = new SimpleDateFormat("h:mm a");
        jobCompletionTime = outputFormatTime.format(inputFormatTime.parse(jobCompletionTime)).replace("am", "AM").replace("pm", "PM");

        expectedMessage = expectedMessage.replace("@Resource", resourceName).replace("@JobCompletionDate", jobCompletionDate).replace("@JobCompletionTime", jobCompletionTime);

        return expectedMessage;
    }

    public Properties initializeProperties() {
        try {
            Properties props = new Properties();
            ClassLoader classLoader = FileHelper.class.getClassLoader();
            URI uri  = classLoader.getResource("date_time.properties").toURI();
            InputStream stream = Files.newInputStream(Paths.get(uri));
            props.load(stream);

            return props;

        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ContinueNextStepsOnException
    @Then("^the Calendar function works as expected$")
    public void calendar_function_works_as_expected() {
        LocalDate currentDate = LocalDate.now();
        Month month = currentDate.getMonth();
        int year = currentDate.getYear();
        int dayOfMonth = currentDate.getDayOfMonth();
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

        String expectedCalendarMonth = StringUtils.capitalize(month.toString().toLowerCase()) + " " + String.valueOf(year);
        String actualCalendarMonth = runtimeState.storePortalCalendarPage.getCalendarMonth();
        runtimeState.scenario.write("Asserting Calendar displays the correct Month");
        assertEquals("Expected: " + expectedCalendarMonth + " but was: " + actualCalendarMonth, expectedCalendarMonth, actualCalendarMonth);

        String expectedCalendarDay = String.valueOf(dayOfMonth);
        String actualCalendarDay = runtimeState.storePortalCalendarPage.getCurrentCalendarDay();
        runtimeState.scenario.write("Asserting Calendar displays the correct Day");
        assertEquals("Expected: " + expectedCalendarDay + " but was: " + actualCalendarDay, expectedCalendarDay, actualCalendarDay);

        String expectedTimelineDate = StringUtils.capitalize(String.valueOf(dayOfWeek).toLowerCase()) + " "
                + StringUtils.capitalize(month.toString().toLowerCase()) + " " + dayOfMonth + ordinalAbbreviation(dayOfMonth);
        String jobDetailsDay = runtimeState.storePortalCalendarPage.getJobDetailsDay();
        String jobDetailsDate = runtimeState.storePortalCalendarPage.getJobDetailsDate();
        String actualTimelineDate = jobDetailsDay + " " + StringUtils.capitalize(jobDetailsDate.toLowerCase());
        runtimeState.scenario.write("Asserting Timeline displays the correct Date");
        assertEquals("Expected: " + expectedTimelineDate + " but was: " + actualTimelineDate, expectedTimelineDate, actualTimelineDate);
    }

    public static String ordinalAbbreviation(int number)  {
        String answer ="th";
        if (number % 100 / 10 == 1) {
            return answer;
        }
        switch (number % 10) {
        case 1: answer = "st";
        break;
        case 2: answer = "nd";
        break;
        case 3: answer = "rd";
        break;
        }
        return answer;
    }

    @ContinueNextStepsOnException
    @Then("^the Homepage is displayed as expected$")
    public void homepage_is_displayed_as_expected() {
        POHelper.waitWhileBusy();
        List<String> expectedTileNames = Arrays.asList("Watched", "Onsite", "Reactive", "PPM", "Quotes", "Notifications", "Closed", "Awaiting Feedback");
        List<String> alternateExpectedTileNames = Arrays.asList("Watched", "Onsite", "Reactive", "PPM", "Quotes", "Pending Review", "Notifications", "Closed");
        List<String> actualTileNames = runtimeState.storePortalHomePage.getTileNames();
        runtimeState.scenario.write("Asserting Menu item names are: " + expectedTileNames + ", or: " + alternateExpectedTileNames);
        assertThat(actualTileNames, Matchers.either(Matchers.is(expectedTileNames)).or(Matchers.is(alternateExpectedTileNames)));

        List<String> tileCounts = runtimeState.storePortalHomePage.getTileCounts();
        runtimeState.scenario.write("Asserting Tiles all have a Count value");
        assertTrue(!tileCounts.contains(null));

        List<String> expectedMenuNames = Arrays.asList("Home", "Log a Job", "Calendar", "Contact Us", "Logout");
        List<String> actualMenuNames = runtimeState.storePortalHomePage.getMenuNames();
        runtimeState.scenario.write("Asserting Tile names are: " + expectedMenuNames);
        assertEquals("Expected: " + expectedMenuNames + " but was: " + actualMenuNames, expectedMenuNames, actualMenuNames);
    }

    @ContinueNextStepsOnException
    @Then("^the first Job will be shown as a potential duplicate$")
    public void first_job_will_be_shown_as_potential_duplicate() {
        String actualJobReference = normalize(runtimeState.storePortalLogAJobPage.getPotentialDuplicateJobReference());
        String actualSubtypeClassification = normalize(runtimeState.storePortalLogAJobPage.getPotentialDuplicateItem("Subtype / Classification"));
        String actualLocation = normalize(runtimeState.storePortalLogAJobPage.getPotentialDuplicateItem("Location"));
        String actualFaultType = normalize(runtimeState.storePortalLogAJobPage.getPotentialDuplicateItem("Fault Type"));
        String actualDescription = normalize(runtimeState.storePortalLogAJobPage.getPotentialDuplicateItem("Description"));

        runtimeState.scenario.write("Asserting that Potential Duplicate Job is showing the correct details");
        assertEquals("Job Reference does not match with Duplicate Job", testData.get("jobReference"), actualJobReference);
        String subtypeClassification = StringUtils.substringAfter(testData.get("assetType").toString(), " > ");
        assertEquals("Subtype / Classification does not match with Duplicate Job", subtypeClassification, actualSubtypeClassification);
        assertEquals("Location does not match with Duplicate Job", testData.get("location"), actualLocation);
        assertEquals("Fault Type does not match with Duplicate Job", testData.get("faultType"), actualFaultType);
        assertEquals("Description does not match with Duplicate Job", testData.get("faultDescription"), actualDescription);
    }
}
