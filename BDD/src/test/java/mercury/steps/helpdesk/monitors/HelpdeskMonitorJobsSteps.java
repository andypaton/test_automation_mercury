package mercury.steps.helpdesk.monitors;


import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.THREE_MINUTES;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import mercury.api.models.job.Job;
import mercury.databuilders.TestData;
import mercury.databuilders.User;
import mercury.helpers.DateHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.MonitorHelper;
import mercury.helpers.POHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.QuoteCreationHelper;
import mercury.helpers.apihelper.ApiHelperHangfire;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperTimeZone;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.monitors.MonitorGrid;
import mercury.pageobject.web.helpdesk.monitors.MonitorsForInfo;
import mercury.pageobject.web.helpdesk.monitors.MonitorsToDo;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.LoginSteps;
import mercury.steps.helpdesk.jobs.HelpdeskLogAJobSteps;
import mercury.steps.helpdesk.resources.HelpdeskManageResourceAssertions;
import mercury.steps.helpdesk.resources.HelpdeskManageResourcesSteps;
import mercury.steps.helpdesk.resources.HelpdeskScheduleCallBackSteps;
import mercury.steps.helpdesk.search.HelpdeskSearchForJobSteps;
import mercury.steps.helpdesk.search.HelpdeskSearchForLogJobSteps;
import mercury.steps.portal.GetPortalUserDataSteps;
import mercury.steps.portal.MenuSteps;
import mercury.steps.portal.PortalCommon;
import mercury.steps.portal.quotes.PortalQuoteSteps;
import mercury.steps.portal.quotes.PortalQuotesApprovalSteps;

public class HelpdeskMonitorJobsSteps {

    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private RuntimeState runtimeState;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private TestData testData;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private HelpdeskSearchForJobSteps helpdeskSearchForJobSteps;
    @Autowired private CommonSteps commonSteps;
    @Autowired private HelpdeskManageResourcesSteps helpdeskManageResourcesSteps;
    @Autowired private GetPortalUserDataSteps getPortalUserDataSteps;
    @Autowired private LoginSteps loginSteps;
    @Autowired private MenuSteps menuSteps;
    @Autowired private PortalCommon portalCommon;
    @Autowired private PortalQuotesApprovalSteps portalQuotesApprovalSteps;
    @Autowired private PortalQuoteSteps portalQuoteSteps;
    @Autowired private User user;
    @Autowired private MonitorHelper monitorHelper;
    @Autowired private HelpdeskScheduleCallBackSteps helpdeskScheduleCallBackSteps;
    @Autowired private HelpdeskSearchForLogJobSteps helpdeskSearchForLogJobSteps;
    @Autowired private HelpdeskLogAJobSteps helpdeskLogAJobSteps;
    @Autowired private HelpdeskManageResourceAssertions helpdeskManageResourceAssertions;
    @Autowired private QuoteCreationHelper quoteCreationHelper;
    @Autowired private ApiHelperHangfire apiHelperHangfire;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private DbHelperQuotes dbHelperQuotes;


    @ContinueNextStepsOnException
    @Then("^the Awaiting Assignment monitor only displays jobs where primary or requested additional resource is unassigned$")
    public void the_Awaiting_Assignment_monitor_only_displays_jobs_where_primary_or_requested_additional_resource_is_unassigned() throws Throwable {
        Integer jobReference = dbHelperMonitors.getJobReferenceFromMonitor("Awaiting Assignment", null, null);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) the jobs with resource status \"([^\"]*)\"$")
    public void the_monitor_will_display_the_jobs_with_resource_status(String monitor, String action, String status) throws Throwable {
        Integer jobReference;
        if (status.contains(",")) {
            String[] resourceStatus = status.split(",");
            for (String currentStatus : resourceStatus) {
                jobReference = dbHelperMonitors.getJobReferenceFromMonitorWithResourceStatus(monitor, currentStatus.trim());
                monitorHelper.removeAllTheFiltersFromSettings();
                monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
            }
        } else {
            jobReference = (monitor.contains("Deferred") && action.equals("will display"))
                    ? monitorHelper.getDeferredJobWithStatus(status)
                            : dbHelperJobs.getNonDeferredJobReferenceOfResourceStatus(status);

                    monitorHelper.removeAllTheFiltersFromSettings();
                    monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
        }
    }

    @And("^there is a job with status \"([^\"]*)\"$")
    public void there_is_a_job_with_status(String status) throws Throwable {
        Integer jobReference = dbHelperJobs.getNonDeferredJobReferenceOfResourceStatus(status);
        Job job;
        if (jobReference == null) {
            job = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");
            int resourceAssignmentId = dbHelper.getResourceIdForJob(job.getJobReference());
            apiHelperJobs.removeResource(resourceAssignmentId);
        }
    }

    @And("^the Removal Requests monitor will display the jobs with resource status \"([^\"]*)\"$")
    public void the_removal_requests_monitor_will_display_jobs_with_resource_status(String status) throws Throwable {
        Integer jobReference = dbHelperJobs.getNonDeferredJobReferenceOfResourceStatus(status);
        boolean flag;
        Instant start = Instant.now();
        do {
            POHelper.refreshPage();
            runtimeState.scenario.write("*****Waiting (upto 2 minutes) until the job is present in the monitor*****");
            flag = monitorHelper.isJobReferenceDisplayed("Job ID", jobReference);
        } while ((flag != true) && Duration.between(start, Instant.now()).toMinutes() < 3);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) jobs with status \"([^\"]*)\"$")
    public void the_monitor_will_display_the_deferred_jobs_with_resource_status(String monitor, String action, String status) throws Throwable {
        if (dbHelperTimeZone.isHelpdeskOutOfHours()) {
            Integer jobReference = dbHelperMonitors.getJobsWithActiveDeferralEvents(status);
            if (jobReference == null) {
                testData.put("resourceId", dbHelperResources.getRandomCityTechId("City Resource"));
                mercury.api.models.job.Job job = jobCreationHelper.createJobForDeferrablePriority();
                jobReference = job.getJobReference();
            }
            boolean flag;
            Instant start = Instant.now();
            do {
                POHelper.refreshPage();
                runtimeState.scenario.write("*****Waiting (upto 2 minutes) until the job is present in the monitor*****");
                flag = monitorHelper.isJobReferenceDisplayed("Job ID", jobReference);
            } while ((flag != true) && Duration.between(start, Instant.now()).toMinutes() < 3);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) the jobs ((?:with|without)) initial funding request authorised$")
    public void the_monitor_the_jobs_initial_funding_request_authorised(String monitor, String action, String fundingAuthorised) throws Throwable {
        Integer jobReference = null;
        if (fundingAuthorised.equals("with")) {
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor(monitor, null, null);
        } else {
            monitorHelper.removeAllTheFiltersFromSettings();
            jobReference = dbHelperJobs.getNonDeferredJobReferenceOfResourceStatus("Awaiting Funding Authorisation");
        }
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
    }

    @ContinueNextStepsOnException
    @Then("^the Chase monitor ((?:will display|will not display)) the jobs with \"([^\"]*)\" chase event$")
    public void the_Chase_monitor_from_tile_will_display_the_jobs_with_chase_event(String action, String chase) throws Throwable {
        Integer jobReference = null;

        String monitor = testData.getString("tile").equals("Jobs") ? "Chase" : "Focus Chase";

        if (chase.equals("active")) {
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor(monitor, null, null);

        } else if (chase.equals("multiple active")) {
            monitorHelper.removeAllTheFiltersFromSettings();
            jobReference = dbHelperJobs.getJobReferenceWhereChaseIsResolvedOrActive("multiple", 0);

        } else {
            monitorHelper.removeAllTheFiltersFromSettings();
            jobReference = dbHelperJobs.getJobReferenceWhereChaseIsResolvedOrActive("single", 1);
        }
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) the jobs for which deferral time has been passed and notification period has been missed$")
    public void the_monitor_will_display_the_jobs_for_which_deferral_time_has_been_passed_and_notification_period_has_been_missed(String monitor, String action) throws Throwable {

        Integer jobReference = dbHelperMonitors.getJobReferenceWithNotificationPeriodHasPassed(monitor, "Yes");
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) the P2 or P3 \"([^\"]*)\" jobs$")
    public void the_monitor_display_the_jobs(String monitor, String action, String hours) throws Throwable {
        Integer jobReference = null;

        if (hours.equals("Out Of Hours")) {
            jobReference = dbHelperMonitors.getOutOfHoursJob();
            if (jobReference != null) {
                monitorHelper.removeAllTheFiltersFromSettings();
                monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
            }
        } else {
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor("Awaiting Acceptance", null, null);
            monitorHelper.removeAllTheFiltersFromSettings();
            monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Deferred jobs monitor will display the jobs with active deferral event$")
    public void the_Deferred_monitor_will_display_the_jobs_with_active_deferral_events() throws Throwable {

        if (dbHelperTimeZone.isHelpdeskOutOfHours()) {
            Integer jobReference = dbHelperMonitors.getJobsWithActiveDeferralEvents(null);
            if (jobReference == null) {
                testData.put("resourceId", dbHelperResources.getRandomCityTechId("City Resource"));
                mercury.api.models.job.Job job = jobCreationHelper.createJobForDeferrablePriority();
                jobReference = job.getJobReference();
            }
            boolean flag;
            Instant start = Instant.now();
            do {
                POHelper.refreshPage();
                runtimeState.scenario.write("*****Waiting (upto 2 minutes) until the job is present in the monitor*****");
                flag = monitorHelper.isJobReferenceDisplayed("Job ID", jobReference);
            } while ((flag != true) && Duration.between(start, Instant.now()).toMinutes() < 3);
        }
    }

    @ContinueNextStepsOnException
    @Then("^there is a recently created quote job$")
    public void i_have_the_quote_jobs_created_recently() throws Throwable {
        helpdeskSearchForLogJobSteps.search_for_a_site_with_an_available_contractor_for_asset_types();
        commonSteps.the_button_is_clicked("Log a job");
        commonSteps.the_button_is_clicked("Request Quote");
        helpdeskLogAJobSteps.job_details_are_completed_for_a_non_CAPEX_UrgentCritical_quote();
        helpdeskLogAJobSteps.the_Job_Contact_is_the_same_as_caller();
        helpdeskLogAJobSteps.the_job_is_saved();
        helpdeskManageResourceAssertions.the_job_generated("Quote", "Awaiting Quote Request Review");
        runtimeState.helpdeskHomePage.selectHomeTab();
    }

    @Given("^a user has \"([^\"]*)\" jobs with status \"([^\"]*)\"$")
    public void a_user_has_jobs_with_status(String jobType, String status) throws Throwable {

        if (jobType.equals("Quote") && status.equals("New Job Notification Sent")) {
            getPortalUserDataSteps.a_with_a_in_state_with_a_funding_route_with_budget("RFM", "single", jobType, "Quotes Awaiting Review", "OPEX", "less");
            loginSteps.user_logs_in();
            menuSteps.sub_menu_is_selected_from_the_top_menu("Quotes Awaiting Review", "Quotes");
            portalCommon.the_portal_user_performs_action_on_page("Views", "Quote Managers Decision");
            portalQuotesApprovalSteps.the_job_type_is_actioned("Quote Approval", "Accepted");
            portalQuotesApprovalSteps.the_is_submitted("Quote Approval");
            portalQuoteSteps.the_is_saved("Quote Approval");
            user.setUsername(null);
        }
        getWebDriver().manage().deleteAllCookies();
        getWebDriver().get(propertyHelper.getMercuryUrl() + "/Account/Logoff");
    }

    public void a_user_has_jobs_with_status1(String jobType, String status, String fundingRoute, String multiQuote) throws Throwable {

        if (jobType.equals("Quote") && status.equals("New Job Notification Sent")) {
            quoteApprovedByRfm(multiQuote, jobType, fundingRoute);

        } else if (jobType.equals("Multi-Quote") && status.equals("New Job Notification Sent")) {
            multiQuoteApprovedByRfm(multiQuote, jobType, fundingRoute);

            String jobStatus = dbHelperJobs.getJobStatus(String.valueOf(testData.getInt("jobReference")));
            if (jobStatus.equalsIgnoreCase("Awaiting Final Approval")) {
                multiQuoteApprovedByFinalApprover();
            }
            user.setUsername(null);
        }
        getWebDriver().manage().deleteAllCookies();
        getWebDriver().get(propertyHelper.getMercuryUrl() + "/Account/Logoff");
    }

    public void quoteApprovedByRfm(String multiQuote, String jobType, String fundingRoute) throws Throwable {
        getPortalUserDataSteps.a_with_a_in_state_with_a_funding_route_with_budget("RFM", multiQuote, jobType, "Quotes Awaiting Review", fundingRoute, "less");
        loginSteps.user_logs_in();
        menuSteps.sub_menu_is_selected_from_the_top_menu("Quotes Awaiting Review", "Quotes");
        portalCommon.the_portal_user_performs_action_on_page("Views", "Quote Managers Decision");
        portalQuotesApprovalSteps.the_job_type_is_actioned("Quote Approval", "Accepted");
        portalQuotesApprovalSteps.the_is_submitted("Quote Approval");
        portalQuoteSteps.the_is_saved("Quote Approval");
        user.setUsername(null);
    }

    public void multiQuoteApprovedByRfm(String multiQuote, String jobType, String fundingRoute) throws Throwable {
        getPortalUserDataSteps.a_with_a_in_state_with_a_funding_route_with_budget("RFM", multiQuote, jobType, "Quotes Awaiting Review", fundingRoute, "less");
        loginSteps.user_logs_in();
        menuSteps.sub_menu_is_selected_from_the_top_menu("Awaiting Review", "Multi-Quotes");
        portalCommon.the_portal_user_performs_action_on_page("Reviews", "Multi-Quote Awaiting Review");
        runtimeState.quotesManagersDecisionPage.selectQuote(1);
        runtimeState.quotesManagersDecisionPage.recommendQuote();
        if (fundingRoute.equalsIgnoreCase("CAPEX") || fundingRoute.equalsIgnoreCase("BMI")) {
            runtimeState.quotesManagersDecisionPage.enterRecommendQuoteNotes("abc");
        }
        while (runtimeState.quotesManagersDecisionPage.isProceedToNextQuoteButtonVisible()) {
            runtimeState.quotesManagersDecisionPage.proceedToNextQuote();
            runtimeState.quotesManagersDecisionPage.recommendQuote();
            if (fundingRoute.equalsIgnoreCase("CAPEX") || fundingRoute.equalsIgnoreCase("BMI")) {
                runtimeState.quotesManagersDecisionPage.enterRecommendQuoteNotes("abc");
            }
        }
        runtimeState.submitQuoteJobRecommendModalPage = runtimeState.quotesManagersDecisionPage.submitRecommend();
        if (runtimeState.submitQuoteJobRecommendModalPage.isInternalNotesLabelDisplayed()) {
            runtimeState.submitQuoteJobRecommendModalPage.setInternalNotes("abc");
        }
        runtimeState.submitQuoteJobRecommendModalPage.saveRecommendation();
    }

    public void multiQuoteApprovedByFinalApprover() throws Exception {
        getWebDriver().manage().deleteAllCookies();
        getWebDriver().get(propertyHelper.getMercuryUrl() + "/Account/Logoff");
        List<Map<String, Object>> quoteDetails = dbHelperQuotes.getQuoteDetails(testData.getInt("jobReference"));
        Map<String, Object> seniorManagerDetails = dbHelperResources.getResourceWithEpochId((int) quoteDetails.get(0).get("SeniorManagerApproverID"));
        user.setUsername(String.valueOf(seniorManagerDetails.get("UserName")));
        loginSteps.user_logs_in();
        menuSteps.sub_menu_is_selected_from_the_top_menu("Awaiting Review", "Funding Requests");
        runtimeState.fundingRequestsAwaitingReviewPage.searchJobs(String.valueOf(testData.getInt("jobReference")));
        runtimeState.fundingRequestsAwaitingApprovalPage = runtimeState.fundingRequestsAwaitingReviewPage.OpenJob(String.valueOf(testData.getInt("jobReference")));

        runtimeState.fundingRequestsAwaitingApprovalPage.selectResourceQuote(1);
        runtimeState.fundingRequestsAwaitingApprovalPage.clickApproveRadioButton();

        while (runtimeState.fundingRequestsAwaitingApprovalPage.isProceedToNextQuoteButtonVisible()) {
            runtimeState.fundingRequestsAwaitingApprovalPage.proceedToNextQuote();
            runtimeState.fundingRequestsAwaitingApprovalPage.clickApproveRadioButton();
        }
        runtimeState.submitFundingRequestModalPage = runtimeState.fundingRequestsAwaitingApprovalPage.clickSubmitButton();
        runtimeState.submitFundingRequestModalPage.save();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will display the jobs with \"([^\"]*)\" ((?:resource|client)) status$")
    public void the_monitor_will_display_the_jobs_with_status(String monitor, String statusName, String status) throws Throwable {
        Integer jobReference = null;

        if (statusName == null && status == null) {
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor(monitor, null, null);

        } else if (status.equalsIgnoreCase("Resource")) {

            await().atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperMonitors.getJobReferenceFromMonitor(monitor, null, null) != null);
            jobReference = dbHelperMonitors.getJobReferenceFromMonitorWithResourceStatus(monitor, statusName);

        } else {
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor(monitor, statusName, null);
        }
        if (jobReference == null) {
            throw new PendingException("No Jobs found for monitor: " + monitor);
        }
        monitorHelper.searchJobRef(jobReference, true);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) the jobs for which notification period \"([^\"]*)\" been exceeded$")
    public void the_monitor_will_display_the_jobs_for_which_notification_period_been_exceeded(String monitor, String action, String exceedStatus) throws Throwable {
        String notificationPeriod = exceedStatus.equals("has") ? "Yes" : "No";
        String monitorToPass = action.equals("will display") ? monitor : "Awaiting Acceptance";
        Integer jobReference = dbHelperMonitors.getJobReferenceWithNotificationPeriodHasPassed(monitorToPass, notificationPeriod);
        boolean displayAction = action.equalsIgnoreCase("will display") ? true : false;
        monitorHelper.searchJobRef(jobReference, displayAction);
    }

    @ContinueNextStepsOnException
    @Then("^the Parked Jobs monitor will display the jobs with parked event and for which parked date and time is not expired$")
    public void the_Parked_Jobs_monitor_will_display_the_jobs_with_parked_event_and_for_which_parked_date_and_time_is_not_expired() throws Throwable {
        Integer jobReference = dbHelperMonitors.getJobReferenceFromMonitor("Parked Jobs", null, null);
        if (jobReference == null) {
            mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");
            runtimeState.helpdeskHomePage.closeActiveTab();
            runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(job.getJobReference());
            jobReference = job.getJobReference();
            helpdeskManageResourcesSteps.the_assigned_resource_is_removed_from_manage_resources_section();
            commonSteps.the_action_is_selected("Advise Removal");
            runtimeState.helpdeskAdviseRemovalPanel.selectRemovalConfirmed("Yes");
            runtimeState.helpdeskAdviseRemovalPanel.save();
            commonSteps.the_action_is_selected("Park Job");
            runtimeState.helpdeskParkJobPanel.selectRandomReason();
            runtimeState.helpdeskParkJobPanel.save();
        }
        runtimeState.helpdeskHomePage.closeActiveTab();
        runtimeState.helpdeskHomePage.selectTile("Jobs");
        monitorHelper.searchJobRef(jobReference, true);
    }

    @ContinueNextStepsOnException
    @Then("^the Parked Jobs monitor will not display the jobs for which parked date and time has expired$")
    public void the_Parked_Jobs_monitor_will_not_display_the_jobs_for_which_parked_date_and_time_has_expired() throws Throwable {
        Integer jobReference = dbHelperMonitors.getJobReferenceWithParkedDateAndTimeExpired();
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will not display");
    }

    @ContinueNextStepsOnException
    @Then("^the Parked Jobs monitor will not display the job which has reverted back to Logged status$")
    public void the_Parked_Jobs_monitor_will_not_display_the_job_which_has_reverted_back_to_Logged_status() throws Throwable {

        // Search for a Parked Job and then unpark it
        runtimeState.helpdeskHomePage.closeActiveTab();

        helpdeskSearchForJobSteps.a_search_is_run_for_a_parked_job_that_is_not_of_type_with_fault_priority("Landlord", "P1");
        runtimeState.scenario.write("Unparking the job : " + testData.getInt("jobReference"));
        commonSteps.the_action_is_selected("UnPark Job");

        apiHelperHangfire.processParkedJobs();

        // Assert that the unparked job is removed from the Parked Jobs monitor
        runtimeState.helpdeskHomePage.closeActiveTab();
        runtimeState.helpdeskHomePage.selectTile("Jobs");

        monitorHelper.searchJobRef(testData.getInt("jobReference"), false);
    }

    @ContinueNextStepsOnException
    @Then("^the Awaiting Assignment monitor will display the correct jobs$")
    public void the_awaiting_assignment_monitor_will_display_the_correct_jobs() throws Throwable {
        Integer jobReference;

        runtimeState.scenario.write("Asserting the jobs where there is no resource assigned");
        jobReference = dbHelperMonitors.getJobReferenceWithNoResourceIsAssigned();
        CommonSteps.assertDataFound("No Jobs found where there is no resource assigned", jobReference);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");

        runtimeState.scenario.write("Asserting the jobs where resource has declined the job and suggested a resource that can't be mapped to the site");
        jobReference = dbHelperMonitors.getJobReferenceWithDeclinedJob("single", -1, 0);
        CommonSteps.assertDataFound("No Jobs found where resource has declined the job and suggested a resource that can't be mapped to the site", jobReference);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");

        runtimeState.scenario.write("Asserting the jobs where resource has declined the job and suggested a resource profile of Contractor");
        jobReference = dbHelperMonitors.getJobReferenceWithDeclinedJob("single", 115, 0);
        CommonSteps.assertDataFound("No Jobs found where resource has declined the job and suggested a resource profile of Contractor", jobReference);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");

        runtimeState.scenario.write("Asserting the jobs where job has been declined twice by a resource");
        jobReference = dbHelperMonitors.getJobReferenceWithDeclinedJob("multiple", 0, 2);
        CommonSteps.assertDataFound("No Jobs found where a job has been declined twice by a resource", jobReference);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");

        if (!getWebDriver().getCurrentUrl().contains("usad")) {
            runtimeState.scenario.write("Asserting the jobs where it has been re-opened with no assignment");
            jobReference = dbHelperMonitors.getJobReferenceWhereReopenedAndNoResourceAssigned();
            CommonSteps.assertDataFound("No Jobs found where it has been re-opened with no assignment", jobReference);
            monitorHelper.removeAllTheFiltersFromSettings();
            monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");
        }
        
        runtimeState.scenario.write("Asserting the jobs where it has become unparked ");
        jobReference = dbHelperMonitors.getJobReferenceWhichHasBecomeUnparked();
        CommonSteps.assertDataFound("No unparked jobs found for the test", jobReference);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");
    }

    @ContinueNextStepsOnException
    @Then("^the user has jobs with a scheduled callback event$")
    public void the_user_has_jobs_with_scheduled_callback_event() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_assigned_to_a_resource_with_phone_number();
        helpdeskScheduleCallBackSteps.user_selects_schedule_callback_action_for_the_resource();
        helpdeskScheduleCallBackSteps.the_schedule_callback_time_is_entered();
        runtimeState.helpdeskHomePage.closeActiveTab();
        runtimeState.helpdeskHomePage.selectHomeTab();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will display the jobs with scheduled callback event$")
    public void the_monitor_will_display_the_jobs_with_scheduled_callback_event(String monitor) throws Throwable {
        the_monitor_will_display_the_jobs_with_status(monitor, null, null);
    }

    @ContinueNextStepsOnException
    @Then("^the Scheduled Call-Backs monitor will not display the jobs for which scheduled call back date and time has passed$")
    public void the_Scheduled_Callbacks_monitor_will_not_display_the_jobs_for_which_scheduled_call_back_date_and_time_has_passed() throws Throwable {
        Integer jobReference = dbHelperMonitors.getJobReferenceWithScheduleCallBackTimeExpired();
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will not display");
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will display the jobs where provided ETA date and time has been elapsed$")
    public void the_monitor_will_display_the_jobs_where_provided_ETA_date_and_time_has_elapsed(String monitor) throws Throwable {
        the_monitor_will_display_the_jobs_with_status(monitor, null, null);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) the jobs where priority and response date ((?:has|has not)) been elapsed$")
    public void the_monitor_will_display_the_jobs_where_priority_and_response_date_has_been_elapsed(String monitor, String action, String flag) throws Throwable {
        Integer jobReference = null;
        String section = testData.getString("section");

        if (flag.equals("has") && section.equals("To Do") || (flag.equals("has not") && section.equals("For Info"))) {
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor(monitor, null, null);

        } else {
            String sectionValue = section.equalsIgnoreCase("To Do") ? " For Info" : " To Do";
            String monitorValue = testData.getString("monitor") + sectionValue;
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor(monitorValue, null, null);
        }
        boolean displayAction = action.equalsIgnoreCase("will display") ? true : false;
        monitorHelper.searchJobRef(jobReference, displayAction);

    }

    @Given("^there is a recently created \"([^\"]*)\" job with client status \"([^\"]*)\"$")
    public void there_is_a_recently_created_job_with_client_status(String jobType, String jobState) throws Throwable {

        String quoteJobApprovalStatus = null;
        String approvalStatus = null;
        String quoteJobApprovalStatusId = null;
        String approvalStatusId = null;
        String quotesSubmitted = null;
        String aboveThreshhold = null;
        String awaitingResourceSelectionBypass = "false";

        switch (jobState) {

        case "Awaiting Quote Request Review":
            quoteJobApprovalStatus = "AwaitingQuoteRequestApproval";
            approvalStatus = "None";
            quoteJobApprovalStatusId = "1";
            approvalStatusId = "1";
            break;

        case "ITQ Awaiting Acceptance":
            quoteJobApprovalStatus = "ItqAwaitingAcceptance";
            approvalStatus = "ItqAwaitingAcceptance";
            quoteJobApprovalStatusId = "18";
            approvalStatusId = "2";
            break;

        case "Awaiting Approval":
            quoteJobApprovalStatus = "AwaitingManagerApproval";
            approvalStatus = "QueryResourceAnswered";
            quoteJobApprovalStatusId = "5";
            approvalStatusId = "6";
            quotesSubmitted = "1";
            aboveThreshhold = "false";
            break;

        default:
            throw new Exception("Cannot find " + jobState);
        }

        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobType", jobType);
        queryMap.put("profileName", "RFM");
        queryMap.put("useResourceTypeName", "False");
        queryMap.put("resourceTypeName", "NA");
        queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus);
        queryMap.put("quoteJobApprovalStatusId", quoteJobApprovalStatusId);
        queryMap.put("approvalStatus", approvalStatus);
        queryMap.put("approvalStatusId", approvalStatusId);
        queryMap.put("quotesSubmitted", quotesSubmitted);
        queryMap.put("aboveThreshhold", aboveThreshhold);
        queryMap.put("fundingRoute", "ignore");
        queryMap.put("multiQuote", "single");
        queryMap.put("awaitingResourceSelectionBypass", awaitingResourceSelectionBypass);

        testData.put("originalProfileName", testData.get("profileName"));
        quoteCreationHelper.createQuote(queryMap);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) the jobs where time ((?:has|has not)) been elapsed$")
    public void the_monitor_will_display_the_jobs_where_time_has_been_elapsed(String monitor, String action, String flag) throws Throwable {
        the_monitor_will_display_the_jobs_where_priority_and_response_date_has_been_elapsed(monitor, action, flag);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will display the watched jobs with watched icon$")
    public void the_monitor_will_display_the_watched_jobs_with_watched_icon(String monitor) throws Throwable {
        testData.addStringTag("assertWatchedIcon", "True");
        the_monitor_will_display_the_jobs_with_status(monitor, null, null);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will not display any job that is not being watched$")
    public void the_monitor_will_not_display_any_job_that_is_not_being_watched(String monitor) throws Throwable {
        Integer jobReference = dbHelperMonitors.getJobReferenceNotBeingWatched();
        monitorHelper.removeAllTheFiltersFromSettings();
        testData.addStringTag("assertWatchedIcon", "False");
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will not display");
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will not display any \"([^\"]*)\" jobs$")
    public void the_monitor_will_not_display_any_closed_jobs(String monitor, String jobStatus) throws Throwable {
        testData.addStringTag("assertWatchedIcon", "False");
        int clientStatusId = monitorHelper.getClientStatusId(jobStatus);
        Integer jobReference = dbHelperJobs.getJobReferenceOfClientStatusWithFaultPriority(clientStatusId, 0);
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will not display");
    }

    @ContinueNextStepsOnException
    @Then("^the monitor will display the jobs sorted by priority for site \"([^\"]*)\"$")
    public void the_monitor_will_display_the_jobs_sorted_by_priority(String site, String priorityColumnHeader) throws Throwable {

        monitorHelper.enterTextAndClickFilterButton("Site", site);
        runtimeState.scenario.write("Asserting that the jobs are sorted by Priorities for site: " + site);
        runtimeState.monitorGrid.tableSort(priorityColumnHeader);
        assertTrue("The jobs are not sorted by priority", runtimeState.monitorGrid.isAscending(priorityColumnHeader));
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will display the jobs sorted by priority and then Response/Repair Due time$")
    public void the_monitor_will_display_the_jobs_sorted_by_priority_and_then_response_repair_due_time(String monitor) throws Throwable {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();

        String priorityColumnHeader = LOCALE.equals("en-US") ? "Priority" : "P";

        // Get the site from the monitor with fault priorities P1, P2, P3
        String site = dbHelperMonitors.getSiteWithAllFaultPriorities(monitor);

        // If the site with all fault priorities is not present, get any site from the
        // monitor
        if (site == null) {
            site = dbHelperMonitors.getSiteWithFaultPriority(monitor);
        }
        the_monitor_will_display_the_jobs_sorted_by_priority(site, priorityColumnHeader);
        testData.addStringTag("site", site);

        List<String> faultPriorities = dbHelperMonitors.getAllFaultPriorities(monitor, site);

        for (String priority : faultPriorities) {

            runtimeState.scenario.write("Asserting that the jobs are sorted by Response/Repair due time for priority: " + priority);
            monitorHelper.enterTextAndClickFilterButton(priorityColumnHeader, priority);

            Grid gridBasedOnPriority = runtimeState.monitorGrid.getGrid();
            List<Row> rowsBasedOnPriority = gridBasedOnPriority.getRows();

            for (int i = 0, j = 1; j <= rowsBasedOnPriority.size() - 1; i++, j++) {
                String responseTime1 = rowsBasedOnPriority.get(i).getCell("Response/Repair Due - HO").getText();
                String responseTime2 = rowsBasedOnPriority.get(j).getCell("Response/Repair Due - HO").getText();
                Date responseTimeAsDate1 = DateHelper.stringAsDate(responseTime1, "dd MMM yyyy hh:mma");
                Date responseTimeAsDate2 = DateHelper.stringAsDate(responseTime2, "dd MMM yyyy hh:mma");
                assertTrue(
                        "The date " + responseTimeAsDate2 + " is not after " + responseTimeAsDate1 + " for priority " + priority + " at row " + i,
                        responseTimeAsDate2.after(responseTimeAsDate1) || responseTimeAsDate2.equals(responseTimeAsDate1));
            }
            getWebDriver().navigate().refresh();
            POHelper.waitForAngularRequestsToFinish();
            monitorHelper.removeAllTheFiltersFromSettings();
            runtimeState.monitorsToDo.selectMonitor(testData.getString("monitor"));
            monitorHelper.enterTextAndClickFilterButton("Site", site);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will display|will not display)) the jobs based on the priority$")
    public void the_monitor_displays_the_jobs_based_on_the_priority(String monitor, String action) throws Throwable {
        List<String> priority = dbHelperMonitors.getAllFaultPriorities(monitor, testData.getString("site"));

        getWebDriver().navigate().refresh();
        POHelper.waitForAngularRequestsToFinish();
        runtimeState.monitorsToDo.selectMonitor(testData.getString("monitor"));

        for (String priorityValue : priority) {
            runtimeState.scenario.write("Asserting the jobs for priority : " + priorityValue);
            Integer jobReference = dbHelperMonitors.getJobReferenceFromMonitor(monitor, null, priorityValue);
            monitorHelper.removeAllTheFiltersFromSettings();
            monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
        }
    }

    @ContinueNextStepsOnException
    @Then("^only jobs Awaiting Assignment with \"([^\"]*)\" priorities will be displayed when the helpdesk is Out Of Hours$")
    public void only_jobs_Awaiting_Assignment_with_priorities_will_be_displayed_when_the_helpdesk_is_Out_Of_Hours(String priorities) throws Throwable {
        if (dbHelperTimeZone.isHelpdeskOutOfHours()) {
            Grid grid = runtimeState.monitorGrid.getGrid();
            String jobId = null;
            for (Row row : grid.getRows()) {
                if (!priorities.contains(row.getCell("P").getText())) {
                    jobId = row.getCell("Job ID").getText();
                    break;
                }
            }
            assertNull("Found a non-immediate callout job: " + jobId, jobId);
        }
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" jobs Awaiting Assignment will always be displayed$")
    public void jobs_Awaiting_Assignment_will_always_be_displayed(String priorities) throws Throwable {
        Integer jobReference = dbHelperMonitors.getJobReferenceFromMonitor("Awaiting Assignment", null, priorities);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" jobs Awaiting Assignment will only be displayed when the helpdesk is In Hours Monday to Sunday$")
    public void jobs_Awaiting_Assignment_will_only_be_displayed_when_the_helpdesk_is_In_Hours_Monday_to_Sunday(String priorities) throws Throwable {
        if ( dbHelperTimeZone.isHelpdeskOutOfHours() ) {
            Integer jobReference = dbHelperMonitors.getInHoursJobReferenceWithPriority(priorities);
            monitorHelper.removeAllTheFiltersFromSettings();
            monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will not display");
        } else {
            jobs_Awaiting_Assignment_will_always_be_displayed(priorities);
        }
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" jobs Awaiting Assignment will only be displayed when the helpdesk is In Hours Monday to Friday$")
    public void jobs_Awaiting_Assignment_will_only_be_displayed_when_the_helpdesk_is_In_Hours_Monday_to_Friday(String priorities) throws Throwable {
        
        if ( dbHelperTimeZone.isHelpdeskOutOfHours() || "Saturday, Sunday".contains(DateHelper.getToday()) ) {
            Integer jobReference = dbHelperMonitors.getInHoursJobReferenceWithPriority(priorities);
            monitorHelper.removeAllTheFiltersFromSettings();
            monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will not display");
        } else {
            jobs_Awaiting_Assignment_will_always_be_displayed(priorities);
        }
    }

    @ContinueNextStepsOnException
    @Then("^all jobs Awaiting Assignment will be displayed when the helpdesk is In Hours$")
    public void all_jobs_Awaiting_Assignment_will_be_displayed_when_the_helpdesk_is_In_Hours() throws Throwable {
        if (!dbHelperTimeZone.isHelpdeskOutOfHours() ) {
            List<String> priorities = dbHelper.getNonImmediateCalloutPriorities();
            Grid grid = runtimeState.monitorGrid.getGrid();
            boolean found = false;
            for (Row row : grid.getRows()) {
                if (priorities.contains(row.getCell("P").getText())) {
                    found = true;
                    break;
                }
            }
            assertTrue("Not found any non-immediate callout jobs", found);
        }
    }

    @And("^the count for \"([^\"]*)\" monitor from \"([^\"]*)\" section has been saved$")
    public void the_monitor_count_has_been_saved(String monitor, String section) throws Throwable {
        runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver());
        runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver());
        monitorHelper. removeAllTheFiltersFromSettings();
        Integer myListCount = runtimeState.monitorsToDo.getMyListCount(monitor);
        testData.addIntegerTag("originalCount", myListCount);
        testData.addStringTag("monitor", monitor);
        testData.addStringTag("section", section);
    }

    @And("^the monitor count has been increased$")
    public void the_monitor_count_has_been_increased() throws Throwable {


        Integer jobRefrence = testData.getString("tile").equalsIgnoreCase("Jobs") ? testData.getInt("jobReference") : testData.getInt("incidentReference");
        String columnName = testData.getString("tile").equalsIgnoreCase("Jobs") ? "Job ID" : "Incident Reference";
        String monitor = testData.getString("monitor");
        Integer countToCheck = 0;
        boolean flag;
        Integer originalCount = testData.getInt("originalCount");
        Instant start = Instant.now();

        // Checking whether the newly created job present in the monitor
        do {
            POHelper.refreshPage();
            runtimeState.scenario.write("*****Waiting (upto 2 minutes) until the job is present in the monitor*****");
            flag = monitorHelper.isJobReferenceDisplayed(columnName, jobRefrence);
        } while ((flag != true) && Duration.between(start, Instant.now()).toMinutes() < 4);

        assertTrue("The job is not present in monitor", monitorHelper.isJobReferenceDisplayed(columnName, jobRefrence));

        //Now check the count for monitor
        do {
            POHelper.refreshPage();
            monitorHelper. removeAllTheFiltersFromSettings();
            countToCheck = testData.getString("section").equalsIgnoreCase("To Do") ? runtimeState.monitorsToDo.getMyListCount(monitor)
                    : runtimeState.monitorsForInfo.getMyListCount(monitor);

            runtimeState.scenario.write("*****Waiting (upto 2 minutes) until the monitor count grid has been refreshed*****");
            runtimeState.scenario.write("Original count:" + testData.getInt("originalCount"));
            runtimeState.scenario.write("Count To Check:" + countToCheck);

        } while ( !(countToCheck > originalCount) && Duration.between(start, Instant.now()).toMinutes() < 3);

        assertTrue("The count has not been increased", countToCheck > originalCount);
    }
}