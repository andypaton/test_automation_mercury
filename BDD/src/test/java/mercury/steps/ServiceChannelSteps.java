package mercury.steps;

import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static org.awaitility.Awaitility.await;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.ResourceDao;
import mercury.database.models.Resource;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.LogJobHelper;
import mercury.helpers.LoginLogoutHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.apihelper.ApiHelperServiceChannel;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperServiceChannel;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.servicechannel.ServiceChannelLoginPage;
import mercury.runtime.RuntimeState;
import mercury.steps.helpdesk.HelpdeskCallSteps;
import mercury.steps.helpdesk.incidents.HelpdeskLogAnIncidentSteps;
import mercury.steps.helpdesk.jobs.HelpdeskAcceptJobSteps;
import mercury.steps.helpdesk.jobs.HelpdeskCancelJobSteps;
import mercury.steps.helpdesk.jobs.HelpdeskChaseSteps;
import mercury.steps.helpdesk.jobs.HelpdeskDeclineJobSteps;
import mercury.steps.helpdesk.jobs.HelpdeskEditJobSteps;
import mercury.steps.helpdesk.jobs.HelpdeskLogAJobSteps;
import mercury.steps.helpdesk.jobs.HelpdeskParkJobSteps;
import mercury.steps.helpdesk.jobs.HelpdeskReopenJobSteps;
import mercury.steps.helpdesk.monitors.HelpdeskMonitorJobsSteps;
import mercury.steps.helpdesk.resources.HelpdeskAcknowledgeEtaSteps;
import mercury.steps.helpdesk.resources.HelpdeskAdviseRemovalSteps;
import mercury.steps.helpdesk.resources.HelpdeskFundingRequestsSteps;
import mercury.steps.helpdesk.resources.HelpdeskManageResourcesSteps;
import mercury.steps.helpdesk.resources.HelpdeskRemoveSuggestedResourceSteps;
import mercury.steps.helpdesk.resources.HelpdeskScheduleCallBackSteps;
import mercury.steps.helpdesk.resources.HelpdeskTransferWorkSteps;
import mercury.steps.helpdesk.search.HelpdeskSearchForJobSteps;
import mercury.steps.helpdesk.search.HelpdeskSearchForLogJobSteps;
import mercury.steps.portal.GetPortalUserDataSteps;
import mercury.steps.portal.MenuSteps;
import mercury.steps.portal.PortalCommon;
import mercury.steps.portal.PortalSteps;
import mercury.steps.portal.invoices.PortalInvoiceApprovalSteps;
import mercury.steps.portal.jobs.PortalUpdateJobSteps;
import mercury.steps.portal.parts.PortalPartsRequestsAwaitingApprovalSteps;
import mercury.steps.portal.quotes.PortalQuoteSteps;
import mercury.steps.portal.quotes.PortalQuotesApprovalSteps;

public class ServiceChannelSteps {

    @Autowired private PropertyHelper propertyHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperServiceChannel dbHelperServiceChannel;
    @Autowired private ApiHelperServiceChannel apiHelperServiceChannel;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private OutputHelper outputHelper;
    @Autowired private HelpdeskLogAJobSteps helpdeskLogAJobSteps;
    @Autowired private HelpdeskCancelJobSteps helpdeskCancelJobSteps;
    @Autowired private CommonSteps commonSteps;
    @Autowired private HelpdeskReopenJobSteps helpdeskReopenJobSteps;
    @Autowired private HelpdeskEditJobSteps helpdeskEditJobSteps;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private ResourceDao resourceDao;
    @Autowired private HelpdeskSearchForLogJobSteps helpdeskSearchForLogJobSteps;
    @Autowired private HelpdeskManageResourcesSteps helpdeskManageResourcesSteps;
    @Autowired private HelpdeskMonitorJobsSteps helpdeskMonitorJobsSteps;
    @Autowired private DbHelper dbHelper;
    @Autowired private HelpdeskChaseSteps helpdeskChaseSteps;
    @Autowired private HelpdeskSearchForJobSteps helpdeskSearchForJobSteps;
    @Autowired private HelpdeskFundingRequestsSteps helpdeskFundingRequestsSteps;
    @Autowired private HelpdeskScheduleCallBackSteps helpdeskScheduleCallBackSteps;
    @Autowired private GetPortalUserDataSteps getPortalUserDataSteps;
    @Autowired private LoginSteps loginSteps;
    @Autowired private LoginLogoutHelper loginLogoutHelper;
    @Autowired private MenuSteps menuSteps;
    @Autowired private PortalSteps portalSteps;
    @Autowired private PortalQuoteSteps portalQuoteSteps;
    @Autowired private PortalCommon portalCommon;
    @Autowired private PortalInvoiceApprovalSteps portalInvoiceApprovalSteps;
    @Autowired private PortalQuotesApprovalSteps portalQuotesApprovalSteps;
    @Autowired private PortalUpdateJobSteps portalUpdateJobSteps;
    @Autowired private PortalPartsRequestsAwaitingApprovalSteps portalPartsRequestsAwaitingApprovalSteps;
    @Autowired private HelpdeskLogAnIncidentSteps helpdeskLogAnIncidentSteps;
    @Autowired private HelpdeskDeclineJobSteps helpdeskDeclineJobSteps;
    @Autowired private HelpdeskRemoveSuggestedResourceSteps helpdeskRemoveSuggestedResourceSteps;
    @Autowired private HelpdeskAcceptJobSteps helpdeskAcceptJobSteps;
    @Autowired private HelpdeskAdviseRemovalSteps helpdeskAdviseRemovalSteps;
    @Autowired private HelpdeskParkJobSteps helpdeskParkJobSteps;
    @Autowired private HelpdeskAcknowledgeEtaSteps helpdeskAcknowledgeEtaSteps;
    @Autowired private HelpdeskCallSteps helpdeskCallSteps;
    @Autowired private HelpdeskTransferWorkSteps helpdeskTransferWorkSteps;

    @Given("^all test city database tables have been purged$")
    public void allTestCityDatabaseTablesHaveBeenPurged() {
        dbHelperServiceChannel.purgeJobChannelEventTable();
        dbHelperServiceChannel.purgeSctServiceChannelEventTable();
        dbHelperServiceChannel.purgeSctFaultTable();
        dbHelperServiceChannel.purgeSclServiceChannelEventTable();
        dbHelperServiceChannel.purgeSclFaultTable();
    }

    @Given("^a list of jobs without a Work Order on Service Channel$")
    public void aListOfJobsWithoutAWorkOrderOnServiceChannel() {
        List<Integer> jobRefs = dbHelperServiceChannel.getJobsWithoutWorkOrderOnSC();
        for (int i = 0; i < jobRefs.size(); i++) {
            int jobReference = jobRefs.get(i);
            int jobId = dbHelperJobs.getJobId(jobReference);
            testData.addToList("jobIds", jobId);
            testData.addToList("jobRefs", jobReference);
        }
    }

    @When("^the \"([^\"]*)\" timeline event is triggered$")
    public void timelineEventIsTriggered(String timelineEvent) throws Throwable {
        switch (timelineEvent) {
        case "Logged":
            triggerLoggedTimelineEvent();
            break;
        case "Reopened":
            triggerReopenedTimelineEvent();
            break;
        case "Edited":
            triggerEditedTimelineEvent();
            break;
        case "JobParked":
            triggerJobParkedTimelineEvent();
            break;
        case "JobUnParked":
            triggerJobUnParkedTimelineEvent();
            break;
        case "PublicNoteAdded":
            triggerPublicNoteAddedTimelineEvent();
            break;
        case "PrivateNoteAdded":
            triggerPrivateNoteAddedTimelineEvent();
            break;
        case "JobQuoteApproved":
            triggerJobQuoteApprovedTimelineEvent();
            break;
        case "JobCancellationAdded":
            triggerJobCancellationAddedTimelineEvent();
            break;
        case "JobLinked":
            triggerJobLinkedTimelineEvent();
            break;
        case "JobUnlinked":
            triggerJobUnlinkedTimelineEvent();
            break;
        case "QuoteResourceQueried":
            triggerQuoteResourceQueriedTimelineEvent();
            break;
        case "ResourceQueryResponse":
            triggerQuoteResourceQueryResponseTimelineEvent();
            break;
        case "JobTypeChangedToWarranty":
            triggerJobTypeChangedToWarrantyTimelineEvent();
            break;
        case "JobTypeChangedToReactive":
            triggerJobTypeChangedToReactiveTimelineEvent();
            break;
        case "FundingApproved":
            triggerFundingApprovedTimelineEvent();
            break;
        case "FundingDeclined":
            triggerFundingDeclinedTimelineEvent();
            break;
        case "PartsOrderApproved":
            triggerPartsOrderApprovedTimelineEvent();
            break;
        case "PartsOrderRejected":
            triggerPartsOrderRejectedTimelineEvent();
            break;
        case "AttachmentAdded":
            triggerAttachmentAddedTimelineEvent();
            break;
        case "AttachmentRemoved":
            triggerAttachmentRemovedTimelineEvent();
            break;
        case "ExtremeWeatherInvoiceApproved":
            triggerExtremeWeatherInvoiceApprovedTimelineEvent();
            break;
        case "JobLinkedToIncident":
            triggerJobLinkedToIncidentEvent();
            break;
        case "JobUnlinkedFromIncident":
            triggerJobUnlinkedFromIncidentEvent();
            break;
        case "DeclinedJob":
            triggerDeclinedJobTimelineEvent();
            break;
        case "RemovalRequested":
            triggerRemovalRequestedTimelineEvent();
            break;
        case "RemovalRejected":
            triggerRemovalRejectedTimelineEvent();
            break;
        case "ResourceRemoved":
            triggerResourceRemovedTimelineEvent();
            break;
        case "JobLinkedToQuoteJob":
            triggerJobLinkedToQuoteJobTimelineEvent();
            break;
        case "JobUnlinkedFromQuoteJob":
            triggerJobUnlinkedFromQuoteJobTimelineEvent();
            break;
        case "ResourceQuoteRejected":
            triggerResourceQuoteRejectedTimelineEvent();
            break;
        case "QuoteFinalApproverRejected":
            triggerQuoteFinalApproverRejectedTimelineEvent();
            break;
        case "AdditionalResourceAssignmentClosed":
            triggerAdditionalResourceAssignmentclosedTimelineEvent();
            break;
        case "AdditionalResourceRequiredRemoved":
            triggerARRRemovedTimelineEvent();
            break;
        case "Cancelled":
            triggerCancelledJobTimelineEvent();
            break;
        case "OutboundCall":
            triggerOutboundCallTimelineEvent();
            break;
        case "ETAAcknowledged":
            triggerETAAcknowledgedTimelineEvent();
            break;
        case "WorkTransferred":
            triggerWorkTransferredTimelineEvent();
            break;
        case "UpdateETAChase":
            triggerUpdateETAChaseTimelineEvent();
            break;
        case "ETAChaseResolved":
            triggerETAChaseResolvedTimelineEvent();
            break;
        case "ChaseComplaint":
            triggerChaseComplaintTimelineEvent();
            break;
        case "ChaseWorksCompleteQuery":
            triggerChaseWorksCompleteQueryTimelineEvent();
            break;
        case "ChaseWorksheetQuery":
            triggerChaseWorksheetQueryTimelineEvent();
            break;
        case "ChasePartsEquipmentLeftOnSite":
            triggerChasePartsEquipmentLeftOnSiteTimelineEvent();
            break;
        case "ChaseManagerQuery":
            triggerChaseManagerQueryTimelineEvent();
            break;
        case "JobStatusChanged":
            triggerJobStatusChangedTimelineEvent();
            break;
        case "FundingRouteUpdated":
            triggerFundingRouteUpdatedTimelineEvent();
            break;
        case "CallbackRequired":
            triggerCallbackRequiredTimelineEvent();
            break;
        case "CallbackCompleted":
            triggerCallbackCompletedTimelineEvent();
            break;
        case "SiteNotifiedOfETA":
            triggerSiteNotifiedOfETATimelineEvent();
            break;
        }
    }

    public void triggerJobParkedTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_that_is_not_of_type_with_fault_priority_and_is_not_assigned_to_a_resource("Landlord", "P1", "is");
        helpdeskManageResourcesSteps.the_assigned_resource_is_removed_from_manage_resources_section();
        helpdeskManageResourcesSteps.the_advise_removal_is_confirmed();
        helpdeskParkJobSteps.the_job_is_parked();
    }

    public void triggerJobUnParkedTimelineEvent() throws Throwable {
        commonSteps.the_action_is_selected("UnPark Job");
    }

    public void triggerJobUnlinkedTimelineEvent() throws Throwable {
        runtimeState.helpdeskLinkedJobsModal = runtimeState.helpdeskLogJobPage.clickLinkedJob();
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Type");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Sub Type");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Classification");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Location");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Resource");
        runtimeState.helpdeskLinkedJobsModal.selectOptionFromDropdown("Site", testData.getString("site"));
        runtimeState.helpdeskLinkedJobsModal.searchForLinkedJob();
        runtimeState.helpdeskLinkedJobsModal.selectSecondJobOnGrid();
        runtimeState.helpdeskLinkedJobsModal.unlinkJob();
        runtimeState.helpdeskLinkedJobsModal.closeLinkedJobsModal();
    }

    public void triggerJobLinkedTimelineEvent() throws Throwable {
        commonSteps.the_action_is_selected("Edit");
        runtimeState.helpdeskLinkedJobsModal = runtimeState.helpdeskLogJobPage.clickLinkedJob();
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Type");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Sub Type");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Classification");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Location");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Resource");
        runtimeState.helpdeskLinkedJobsModal.selectOptionFromDropdown("Site", testData.getString("site"));
        runtimeState.helpdeskLinkedJobsModal.searchForLinkedJob();
        runtimeState.helpdeskLinkedJobsModal.selectFirstJobOnGrid();
        runtimeState.helpdeskLinkedJobsModal.linkJob();
        runtimeState.helpdeskLinkedJobsModal.closeLinkedJobsModal();
    }

    public void triggerEditedTimelineEvent() throws Throwable {
        testData.put("site", "0110");
        triggerLoggedTimelineEvent();
        commonSteps.the_action_is_selected("Edit");
        helpdeskEditJobSteps.the_job_contact_is_updated();
        helpdeskEditJobSteps.the_caller_is_shown_in_the_caller_field();
        helpdeskLogAJobSteps.the_job_is_saved();
        helpdeskEditJobSteps.the_reason_for_changes_is_entered_in_the_summary_pop_up();
        helpdeskEditJobSteps.the_changes_are_confirmed();
    }

    public void triggerJobCancellationAddedTimelineEvent() throws Throwable {
        //Using TestAutomationSite so that it will always be out of hours
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite("TestAutomationSite");
        Map<String, Object> assetAndFaultDetails = dbHelper.getAssetAndFaultDetailsForDeferred("TestAutomationSite");

        String subtypeClassification = assetAndFaultDetails.get("AssetType".toString()) + " > " + assetAndFaultDetails.get("AssetSubType").toString();
        if (!assetAndFaultDetails.get("AssetClassification").toString().isEmpty()  && assetAndFaultDetails.get("AssetClassification").toString().length() != 0 ) {
            subtypeClassification += " > " + assetAndFaultDetails.get("AssetClassification").toString();
        }
        testData.put("subtypeClassification", subtypeClassification);
        testData.put("fault", assetAndFaultDetails.get("FaultType").toString());
        helpdeskLogAJobSteps.a_job_is_logged();

        commonSteps.the_action_is_selected("Cancel Job");
        helpdeskCancelJobSteps.all_cancelation_details_are_entered();
        commonSteps.the_button_is_clicked("Cancel Job");
    }

    public void triggerPrivateNoteAddedTimelineEvent() throws Throwable {
        commonSteps.the_button_is_clicked("Add note");
        helpdeskEditJobSteps.notes_are_entered_on_the_page();
        helpdeskEditJobSteps.private_checkbox_is_clicked();
        commonSteps.the_button_is_clicked("Save");
    }

    public void triggerPublicNoteAddedTimelineEvent() throws Throwable {
        commonSteps.the_button_is_clicked("Add note");
        helpdeskEditJobSteps.notes_are_entered_on_the_page();
        commonSteps.the_button_is_clicked("Save");
    }

    public void triggerReopenedTimelineEvent() throws Throwable {
        if (!dbHelperResources.doesJobHaveResourceAssigned(testData.getInt("jobReference"))) {
            runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
            Resource resource = resourceDao.getRandomCityTechResource();
            runtimeState.helpdeskManageResourcesPanel.selectResource(resource.getName());
            if (runtimeState.helpdeskManageResourcesPanel.isOverrideRecommendedResourceDisplayed()) {
                helpdeskManageResourcesSteps.Override_Recommended_Resource_details_are_entered();
            }
            helpdeskManageResourcesSteps.the_resource_details_are_saved();
        }
        helpdeskCancelJobSteps.job_is_cancelled();
        commonSteps.the_action_is_selected("Reopen Job");
        helpdeskReopenJobSteps.the_reopen_reason_and_notes_are_entered("Site advised works incomplete");
        helpdeskReopenJobSteps.reopen_is_selected();
    }

    public void triggerLoggedTimelineEvent() throws Throwable {
        String site = null;
        if (testData.get("site") == null) {
            site = dbHelperSites.getRandomSiteName();
            testData.put("site", site);
        } else {
            site = testData.getString("site");
        }
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(site);
        helpdeskLogAJobSteps.a_job_is_logged();
    }

    public void triggerJobQuoteApprovedTimelineEvent() throws Throwable {
        // Below method will trigger the timeline events such as - QuoteRequestRaised, QuoteRequestApproverSet, ResourcesInvitedToQuote, ItqAccepted, AwaitingApproval, JobQuoteApproved
        helpdeskMonitorJobsSteps.a_user_has_jobs_with_status("Quote", "New Job Notification Sent");
    }

    public void triggerQuoteResourceQueriedTimelineEvent() throws Throwable {
        getPortalUserDataSteps.a_with_a_quote_job_in_state_with_a_funding_route("Contractor Admin", "single", "Quote", "Quotes with Query Pending", "ignore");
    }

    public void triggerQuoteResourceQueryResponseTimelineEvent() throws Throwable {
        loginSteps.user_logs_in();
        menuSteps.sub_menu_is_selected_from_the_top_menu("Quotes with Query Pending", "Quotes");
        portalCommon.the_portal_user_performs_action_on_page("Views", "Quotes with Query Pending");
        portalQuotesApprovalSteps.the_notes_are_added("Quote Query");
        portalQuotesApprovalSteps.the_is_submitted("Quote Query");
    }

    public void triggerJobTypeChangedToWarrantyTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_logged_job();
        commonSteps.the_action_is_selected("Confirm Warranty");
        runtimeState.confirmWarrantyPanel.selectRandomReason();
        runtimeState.confirmWarrantyPanel.enterNotes("Notes entered on " + DateHelper.dateAsString(new Date()));
        outputHelper.takeScreenshots();
        runtimeState.confirmWarrantyPanel.clickSaveButton();
    }

    public void triggerJobTypeChangedToReactiveTimelineEvent() throws Throwable {
        menuSteps.sub_menu_is_selected_from_the_top_menu("Open Quote Requests", "Quotes");
        portalSteps.the_user_is_viewing_a_job_with_status1("Awaiting Quote Request Approval");
        portalQuoteSteps.the_rejection_reason_is_selected("Fund as reactive");
        portalQuoteSteps.a_resource_is_selected();
        portalQuoteSteps.the_portal_reject_job_the_additional_comments_are_entered();
        portalQuoteSteps.the_portal_reject_quote_request();
    }

    public void triggerFundingApprovedTimelineEvent() throws Throwable {
        // This method will also trigger the 'Awaiting Funding Authorisation' timeline event
        helpdeskSearchForJobSteps.a_job_assigned_to_contractor_at_a_vendor_store("contractor", "store");
        commonSteps.the_action_is_selected("Manage Resources");
        helpdeskFundingRequestsSteps.the_funding_request_is_authorised_for("initial", "contractor");
    }

    public void triggerFundingDeclinedTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_job_assigned_to_contractor_at_a_vendor_store("contractor with agreed call out rate", "store");
        commonSteps.the_action_is_selected("Manage Resources");
        helpdeskFundingRequestsSteps.the_funding_request_is_rejected_for_resource_with_reason_and_notes("initial", "contractor", "Duplicate Job");
    }

    public void triggerPartsOrderApprovedTimelineEvent() throws Throwable {
        menuSteps.sub_menu_is_selected_from_the_top_menu("Parts Awaiting Approval", "Parts");
        portalCommon.the_portal_user_performs_action_on_page("Views", "Parts Awaiting Approval");
        portalPartsRequestsAwaitingApprovalSteps.the_parts_request_is_approved();
    }

    public void triggerPartsOrderRejectedTimelineEvent() throws Throwable {
        menuSteps.sub_menu_is_selected_from_the_top_menu("Parts Awaiting Approval", "Parts");
        portalCommon.the_portal_user_performs_action_on_page("Views", "Parts Awaiting Approval");
        portalPartsRequestsAwaitingApprovalSteps.the_parts_request_is_rejected();
    }

    public void triggerAttachmentAddedTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_logged_job();
        commonSteps.the_button_is_clicked("Attachment");
        helpdeskEditJobSteps.file_is_chosen_to_be_uploaded("Invoice");
        helpdeskEditJobSteps.the_attachment_type_is_set_to("Invoice");
        commonSteps.the_button_is_clicked("Attach");
    }

    public void triggerAttachmentRemovedTimelineEvent() throws Throwable {
        commonSteps.the_button_is_clicked("Delete");
        runtimeState.helpdeskAddAttachmentsModal.clickCloseButton();
        POHelper.refreshPage();
        outputHelper.takeScreenshots();
    }

    public void triggerExtremeWeatherInvoiceApprovedTimelineEvent() throws Throwable {
        menuSteps.sub_menu_is_selected_from_the_top_menu("Invoices Awaiting Approval", "Invoices");
        portalCommon.the_portal_user_performs_action_on_page("Views", "Invoice Awaiting Approval");
        portalInvoiceApprovalSteps.the_user_is_able_to_approve_an_invoice_with_weather_conditions("Extreme");
    }


    public void triggerJobLinkedToIncidentEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_logged_job();
        commonSteps.the_button_is_clicked("Linked Incidents");
        runtimeState.helpdeskLogAnIncidentPage = runtimeState.helpdeskJobsLinkedIncidentsPanel.clickCreateNewIncidentIcon();
        helpdeskLogAnIncidentSteps.an_incident_is_logged();
        runtimeState.helpdeskHomePage.selectHomeTab();
    }

    public void triggerJobUnlinkedFromIncidentEvent() throws Throwable {
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        commonSteps.the_button_is_clicked("Linked Incidents");
        runtimeState.helpdeskJobsLinkedIncidentsPanel.clickUnlinkButton();
        outputHelper.takeScreenshots();
    }

    public void triggerDeclinedJobTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_assigned_to_a_resource("reactive");
        helpdeskDeclineJobSteps.user_declines_job_on_behalf_of_resource("resource");
        helpdeskRemoveSuggestedResourceSteps.removes_suggested_resource_field();
    }

    public void triggerRemovalRequestedTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_assigned_to_a_city_tech_resource_with_phone_number("reactive", "resource");
        helpdeskAcceptJobSteps.the_job_is_accepted_for_the_resource_and_an_eta_advised_to_site("resource", "is not");
        helpdeskManageResourcesSteps.the_assigned_resource_is_removed_from_manage_resources_section();
    }

    public void triggerRemovalRejectedTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_assigned_to_a_city_tech_resource("reactive");
        helpdeskAcceptJobSteps.the_job_is_accepted_for_the_resource_and_an_eta_advised_to_site("resource", "is not");
        helpdeskManageResourcesSteps.the_assigned_resource_is_removed_from_manage_resources_section();
        commonSteps.the_action_is_selected("Advise Removal");
        helpdeskAdviseRemovalSteps.removal_confirmed_option_is_selected("is not");
        helpdeskAdviseRemovalSteps.the_button_is_clicked();
        loginLogoutHelper.logout();
    }

    public void triggerResourceRemovedTimelineEvent() throws Throwable {
        // This method will also trigger the 'Resource Notified' timeline event
        helpdeskSearchForLogJobSteps.the_are_accepting_a_job_for_a_resource_with_no_iPad();
        helpdeskManageResourcesSteps.an_additional_resource_accepts_the_job("Contractor");
        helpdeskManageResourcesSteps.the_additional_resource_is_removed();
    }

    public void triggerJobLinkedToQuoteJobTimelineEvent() throws Throwable {
        commonSteps.the_button_is_clicked("Stop Work");
        portalUpdateJobSteps.portal_job_form_job_details_are_updated_with_complete_status("non remote");
        portalSteps.the_portal_update_job_a_quote_is_requested_for_the_job();
        portalSteps.the_portal_update_job_scope_of_work_is_entered_is_entered();
        portalQuoteSteps.a_quote_with_urgency_and_funding_route_is_requested("non-urgent", "OPEX");
        portalUpdateJobSteps.portal_update_job_save_job();
        loginLogoutHelper.logout();
    }

    public void triggerJobUnlinkedFromQuoteJobTimelineEvent() throws Throwable {
        loginSteps.a_IT_user_has_logged_in();
        runtimeState.scenario.write("Searching for jobReference: " + testData.getString("jobReference"));
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getString("jobReference"));
        runtimeState.helpdeskLinkedJobsModal = runtimeState.helpdeskJobPage.clickLinkedJobsIcon();
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Type");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Sub Type");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Asset Classification");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Location");
        runtimeState.helpdeskLinkedJobsModal.selectBlankOptionFromDropdown("Resource");
        runtimeState.helpdeskLinkedJobsModal.searchForLinkedJob();
        runtimeState.helpdeskLinkedJobsModal.selectSecondJobOnGrid();
        runtimeState.helpdeskLinkedJobsModal.unlinkJob();
        runtimeState.helpdeskLinkedJobsModal.closeLinkedJobsModal();
        POHelper.refreshPage();
        outputHelper.takeScreenshots();
    }

    public void triggerResourceQuoteRejectedTimelineEvent() throws Throwable {
        // This will also trigger 'Alternative Quote Requested' and 'ResourceQuoteRejectionEmailSent' timeline events
        menuSteps.sub_menu_is_selected_from_the_top_menu("Quotes Awaiting Review", "Quotes");
        portalCommon.the_portal_user_performs_action_on_page("Views", "Quote Managers Decision");
        portalQuotesApprovalSteps.the_job_type_is_actioned("Quote Approval", "Rejected");
        portalQuotesApprovalSteps.the_rejection_action_is_selected("Request Alternative Quote");
        portalQuotesApprovalSteps.a_random_Resource_is_selected_to_Quote();
        portalQuotesApprovalSteps.the_quote_rejection_is_saved();
    }

    public void triggerQuoteFinalApproverRejectedTimelineEvent() throws Throwable {
        // This will also trigger 'MultiQuoteBypassRequested', 'QuoteRequiresFinalApproval' and 'ResourceDeclinedInvitationToQuote' timeline events
        menuSteps.sub_menu_is_selected_from_the_top_menu("Awaiting Bypass Review", "Funding Requests");
        portalCommon.the_portal_user_performs_action_on_page("Views", "Funding Requests Awaiting Bypass Review");
        portalQuoteSteps.the_potential_insurance_question_is_answered();
        portalQuotesApprovalSteps.all_are("Resource Quotes", "Rejected");
        portalQuoteSteps.the_is_saved("Funding Request");
    }

    public void triggerAdditionalResourceAssignmentclosedTimelineEvent() throws Throwable {
        loginSteps.a_IT_user_has_logged_in();
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_that_is_not_of_type_with_fault_priority_and_is_not_assigned_to_a_resource("Landlord", "P1", "is");
        helpdeskManageResourcesSteps.the_assigned_resource_is_removed_from_manage_resources_section();
        helpdeskManageResourcesSteps.the_advise_removal_is_confirmed();
        helpdeskParkJobSteps.the_additional_resource_section_is_closed();
        helpdeskParkJobSteps.the_details_for_why_no_additional_resource_is_required_is_entered();
        helpdeskParkJobSteps.user_selects_park_job_action_with_reason_and_date_to_unpark();
    }

    public void triggerARRRemovedTimelineEvent() throws Throwable {
        // This will trigger 'ARR Removed', 'Job Resource Notified' and 'Resource Funding Request Cancelled' timeline events
        helpdeskSearchForLogJobSteps.the_are_accepting_a_job_for_a_resource_with_no_iPad();
        helpdeskManageResourcesSteps.an_additional_resource_accepts_the_job("Contractor");
        helpdeskManageResourcesSteps.the_additional_resource_is_removed();
    }

    public void triggerCancelledJobTimelineEvent() throws Throwable {
        // This will trigger 'Cancelled Job' and 'Fault Canceled' Timeline events
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_assigned_to_a_resource("reactive");
        helpdeskDeclineJobSteps.user_declines_job_on_behalf_of_resource("resource");
        helpdeskRemoveSuggestedResourceSteps.removes_suggested_resource_field();
        helpdeskParkJobSteps.the_details_for_why_no_additional_resource_is_required_is_entered();
        helpdeskParkJobSteps.user_selects_cancel_job_action();
    }

    public void triggerOutboundCallTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_assigned_to_a_resource_with_phone_number();
        commonSteps.the_button_is_clicked("Call");
        helpdeskCallSteps.the_phone_number_to_call_is_selected();
        helpdeskCallSteps.the_call_is_answered("is");
    }

    public void triggerETAAcknowledgedTimelineEvent() throws Throwable {
        helpdeskSearchForLogJobSteps.a_job_where_the_eta_is_greater_than_the_sla();
        helpdeskAcknowledgeEtaSteps.the_eta_is_acknowledged();
    }

    public void triggerWorkTransferredTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_assigned_to_single_city_resource_with_resource_assignment_status("reactive", "Awaiting Parts", "Returning");
        commonSteps.the_action_is_selected("Manage Resources");
        commonSteps.the_action_is_selected("Transfer Work");
        helpdeskTransferWorkSteps.the_work_is_transferred_to_another_resource("City tech");
    }

    public void triggerUpdateETAChaseTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.logged_job_is_created_and_searched_for("Logged");
        helpdeskChaseSteps.the_Chase_panel_is_displayed();
        helpdeskChaseSteps.a_chase_is_created_for_the_job("ETA Chase");
        testData.put("chaseCreationRoute", 1);
        helpdeskChaseSteps.the_chase_is_updated();
    }

    public void triggerChaseComplaintTimelineEvent() throws Throwable {
        helpdeskChaseSteps.a_chase_is_created_for_the_job("Complaint");
        testData.put("chaseType", "Complaint");
        helpdeskChaseSteps.the_chase_is_updated();
    }

    public void triggerChaseWorksCompleteQueryTimelineEvent() throws Throwable {
        helpdeskChaseSteps.a_chase_is_created_for_the_job("Works Complete Query");
        testData.put("chaseType", "Works Complete Query");
        helpdeskChaseSteps.the_chase_is_updated();
    }

    public void triggerChaseWorksheetQueryTimelineEvent() throws Throwable {
        helpdeskChaseSteps.a_chase_is_created_for_the_job("Worksheet Query");
        testData.put("chaseType", "Worksheet Query");
        helpdeskChaseSteps.the_chase_is_updated();
    }

    public void triggerChasePartsEquipmentLeftOnSiteTimelineEvent() throws Throwable {
        helpdeskChaseSteps.a_chase_is_created_for_the_job("Parts/Equipment left on Site");
        testData.put("chaseType", "Parts/Equipment left on Site");
        helpdeskChaseSteps.the_chase_is_updated();
    }

    public void triggerChaseManagerQueryTimelineEvent() throws Throwable {
        helpdeskChaseSteps.a_chase_is_created_for_the_job("Manager Chase");
        testData.put("chaseType", "Manager Chase");
        helpdeskChaseSteps.the_chase_is_updated();
    }

    public void triggerETAChaseResolvedTimelineEvent() throws Throwable {
        helpdeskChaseSteps.the_chase_is_updated_to_resolved();
    }

    public void triggerJobStatusChangedTimelineEvent() throws Throwable {
        commonSteps.the_button_is_clicked("Stop Work");
        portalUpdateJobSteps.portal_job_form_job_details_are_updated_with_complete_status("remote");
        portalUpdateJobSteps.portal_update_job_save_job();
    }

    public void triggerFundingRouteUpdatedTimelineEvent() throws Throwable {
        menuSteps.sub_menu_is_selected_from_the_top_menu("Quotes Awaiting Review", "Quotes");
        portalCommon.the_portal_user_performs_action_on_page("Views", "Quote Managers Decision");
        runtimeState.scenario.write("Changing the budget route. ");
        portalQuotesApprovalSteps.the_quote_budget_route_is_updated("Out of Contract");
        portalQuotesApprovalSteps.all_are("Quote Approval", "Accepted");
        portalQuotesApprovalSteps.the_is_submitted("Quote Approval");
        portalQuoteSteps.the_is_saved("Quote Approval");
    }

    public void triggerCallbackRequiredTimelineEvent() throws Throwable {
        helpdeskSearchForJobSteps.a_search_is_run_for_a_job_assigned_to_a_resource_with_phone_number();
        helpdeskScheduleCallBackSteps.user_selects_schedule_callback_action_for_the_resource();
        helpdeskScheduleCallBackSteps.selects_call_back_time_enters_notes_and_saves_it();
    }

    public void triggerCallbackCompletedTimelineEvent() throws Throwable {
        runtimeState.helpdeskCallJobContactModal = runtimeState.helpdeskManageResourcesPanel.callResource();
        helpdeskCallSteps.the_phone_number_to_call_is_selected();
        helpdeskCallSteps.the_call_is_answered("answered");
    }

    public void triggerSiteNotifiedOfETATimelineEvent() throws Throwable {
        // This will trigger 'ETAUpdated' timeline event
        helpdeskAcceptJobSteps.the_job_is_accepted_for_the_resource_and_an_eta_advised_to_site("contractor", "is not");
        commonSteps.the_action_is_selected("ETA");
        helpdeskAcceptJobSteps.eta_populated_advised("is");
    }

    @When("^jobs are created using the combination mapping spreadsheet$")
    public void jobsAreCreatedUsingCombinationMappingSpreadsheet() throws Exception {
        String pathName = null;
        runtimeState.domainUser = System.getProperty("user.name").toLowerCase();

        if (runtimeState.domainUser.contains("jenkins")) {
            pathName = "E:\\Jenkins\\workspace\\ServiceChannel_CombinationMapping\\ServiceChannelCombinationMapping.xlsx";
        } else {
            pathName = "Y:\\workspace\\ServiceChannel_CombinationMapping\\ServiceChannelCombinationMapping.xlsx";
        }

        File file = new File(pathName);
        FileInputStream ExcelFile = new FileInputStream(file);
        XSSFWorkbook ExcelWBook = new XSSFWorkbook(ExcelFile);
        XSSFSheet ExcelWSheet = ExcelWBook.getSheet("Data - NonPPM");

        assertTrue("Worksheet Not Found", ExcelWSheet != null);
        int numberOfRows = ExcelWSheet.getLastRowNum();

        List<String> headers = new ArrayList<>();
        XSSFRow row = ExcelWSheet.getRow(1);

        for (int h = 0; h < row.getLastCellNum(); h ++) {
            headers.add(row.getCell(h).getStringCellValue());
        }

        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, String>> rowData = new ArrayList<Map<String, String>>();

        for (int r = 2; r <= numberOfRows; r ++) {
            row = ExcelWSheet.getRow(r);
            map.put("mercuryAssetType", row.getCell(0) != null && !row.getCell(0).getStringCellValue().isEmpty() && row.getCell(0).getStringCellValue().length() != 0 ? row.getCell(0).getStringCellValue() : "N/A");
            map.put("mercuryAssetSubType", row.getCell(1) != null && !row.getCell(1).getStringCellValue().isEmpty() && row.getCell(1).getStringCellValue().length() != 0 ? row.getCell(1).getStringCellValue() : "N/A");
            map.put("mercuryAssetClassification", row.getCell(2) != null && !row.getCell(2).getStringCellValue().isEmpty() && row.getCell(2).getStringCellValue().length() != 0 ? row.getCell(2).getStringCellValue() : "N/A");
            map.put("mercuryFaultType", row.getCell(3) != null && !row.getCell(3).getStringCellValue().isEmpty() && row.getCell(3).getStringCellValue().length() != 0 ? row.getCell(3).getStringCellValue() : "N/A");
            map.put("mercuryPriority", row.getCell(4) != null && !row.getCell(4).getStringCellValue().isEmpty() && row.getCell(4).getStringCellValue().length() != 0 ? row.getCell(4).getStringCellValue() : "N/A");
            map.put("mercuryLocation", row.getCell(5) != null && !row.getCell(5).getStringCellValue().isEmpty() && row.getCell(5).getStringCellValue().length() != 0 ? row.getCell(5).getStringCellValue() : "N/A");
            rowData.add(map);
            map = new HashMap<String, String>();
        }

        for (int i = 0; i <= rowData.size(); i ++) {
            //Selecting random site for now - can be changed later if needed
            String site = dbHelperSites.getRandomSiteName();
            runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(site);
            runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();
            runtimeState.helpdeskLogJobPage.selectRandomCaller();
            String asset = rowData.get(i).get("mercuryAssetType".toString()) + " > " + rowData.get(i).get("mercuryAssetSubType").toString();
            if (rowData.get(i).get("mercuryAssetClassification") != null && !rowData.get(i).get("mercuryAssetClassification").isEmpty()  && rowData.get(i).get("mercuryAssetClassification").length() != 0 ) {
                asset += " > " + rowData.get(i).get("mercuryAssetClassification").toString();
            }
            runtimeState.helpdeskLogJobPage.selectAsset(asset);
            runtimeState.helpdeskLogJobPage.selectLocation(rowData.get(i).get("mercuryLocation".toString()));
            runtimeState.helpdeskLogJobPage.addJobDetails(DataGenerator.GenerateRandomString(20, 25, 20, 0, 0, 0));
            runtimeState.helpdeskLogJobPage.selectFault(rowData.get(i).get("mercuryFaultType".toString()));
            LogJobHelper.answerDeferralQuestions(runtimeState, testData);
            LogJobHelper.answerResourceQuestions(runtimeState, testData);
            LogJobHelper.answerJobQuestions(runtimeState, testData);
            LogJobHelper.selectJobContactSameAsCaller(runtimeState);
            LogJobHelper.saveJob(runtimeState, testData, outputHelper);

            runtimeState.scenario.write("Job - " + runtimeState.helpdeskJobPage.getJobReference() + " has been created using the following mapping: "
                    + " Asset - " + asset + " Location - " + rowData.get(i).get("mercuryLocation".toString())
                    + " Fault Type - " + rowData.get(i).get("mercuryFaultType".toString()));
            testData.addToList("jobReferences", runtimeState.helpdeskJobPage.getJobReference());
        }
    }

    @When("^the job is sent to the job logged event injection API$")
    public void theJobIsSentToTheJobLoggedEventInjectionAPI() throws ClientProtocolException, IOException {
        apiHelperServiceChannel.getBearerToken();
        if (testData.getIntList("jobRefs") != null) {
            for (int i = 0; i < testData.getIntList("jobRefs").size(); i++) {
                runtimeState.scenario.write("Using job - " + testData.getIntList("jobRefs").get(i) + " to send API request");
                apiHelperServiceChannel.runJobLoggedEventInjectionAPI(testData.getIntList("jobRefs").get(i));
            }
        } else {
            for (int i = 0; i < testData.getIntList("createdJobs").size(); i++) {
                runtimeState.scenario.write("Using job - " + testData.getIntList("createdJobs").get(i) + " to send API request");
                apiHelperServiceChannel.runJobLoggedEventInjectionAPI(testData.getIntList("createdJobs").get(i));
            }
        }
    }

    @When("^the job is sent to the ppm logged event injection API$")
    public void theJobIsSentToThePpmLoggedEventInjectionAPI() throws ClientProtocolException, IOException {
        apiHelperServiceChannel.getBearerToken();
        if (testData.getIntList("jobRefs") != null) {
            for (int i = 0; i < testData.getIntList("jobRefs").size(); i++) {
                runtimeState.scenario.write("Using job - " + testData.getIntList("jobRefs").get(i) + " to send API request");
                apiHelperServiceChannel.runPPMJobLoggedEventInjectionAPI(testData.getIntList("jobRefs").get(i));
            }
        } else {
            for (int i = 0; i < testData.getIntList("createdJobs").size(); i++) {
                runtimeState.scenario.write("Using job - " + testData.getIntList("createdJobs").get(i) + " to send API request");
                apiHelperServiceChannel.runPPMJobLoggedEventInjectionAPI(testData.getIntList("createdJobs").get(i));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" action has been completed in Service Channel$")
    public void theActionHasBeenCompletedInServiceChannel(String action) throws InterruptedException {
        logInToServiceChannel();

        while (runtimeState.serviceChannelHomePage.isLoadingIconDisplayed()) {
            Thread.sleep(3000);
        }

        switch (action) {
        case "Create Work Order":
            workOrderHasBeenCreatedInServiceChannel();
            break;
        }
    }

    public void workOrderHasBeenCreatedInServiceChannel() throws InterruptedException {
        runtimeState.serviceChannelHomePage.selectSearchTypeDropdownOption("by Purchase Order #");
        runtimeState.serviceChannelHomePage.enterFilterText(testData.getString("jobReference"));
        runtimeState.serviceChannelSearchResultsPage = runtimeState.serviceChannelHomePage.clickSearchButton();

        while (runtimeState.serviceChannelSearchResultsPage.isLoadingIconDisplayed()) {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> runtimeState.serviceChannelSearchResultsPage.hasWorkOrderBeenCreated());
        }

        assertTrue("Work Order has not been created", runtimeState.serviceChannelSearchResultsPage.hasWorkOrderBeenCreated());
    }

    @ContinueNextStepsOnException
    @Then("^the mapping is displayed correctly for each job$")
    public void theMappingIsDisplayedCorrectlyForEachJob() throws IOException {
        String pathName = null;
        runtimeState.domainUser = System.getProperty("user.name").toLowerCase();

        if (runtimeState.domainUser.contains("jenkins")) {
            pathName = "E:\\Jenkins\\workspace\\ServiceChannel_CombinationMapping\\ServiceChannelCombinationMapping.xlsx";
        } else {
            pathName = "Y:\\workspace\\ServiceChannel_CombinationMapping\\ServiceChannelCombinationMapping.xlsx";
        }

        File file = new File(pathName);
        FileInputStream ExcelFile = new FileInputStream(file);
        XSSFWorkbook ExcelWBook = new XSSFWorkbook(ExcelFile);
        XSSFSheet ExcelWSheet = ExcelWBook.getSheet("Data - NonPPM");

        assertTrue("Worksheet Not Found", ExcelWSheet != null);
        int numberOfRows = ExcelWSheet.getLastRowNum();

        List<String> headers = new ArrayList<>();
        XSSFRow row = ExcelWSheet.getRow(1);

        for (int h = 0; h < row.getLastCellNum(); h ++) {
            headers.add(row.getCell(h).getStringCellValue());
        }

        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, String>> rowData = new ArrayList<Map<String, String>>();

        for (int r = 2; r <= numberOfRows; r ++) {
            row = ExcelWSheet.getRow(r);
            map.put("serviceChannelTrade", row.getCell(6) != null && !row.getCell(6).getStringCellValue().isEmpty() && row.getCell(6).getStringCellValue().length() != 0 ? row.getCell(6).getStringCellValue() : "N/A");
            map.put("serviceChannelEquipment", row.getCell(7) != null && !row.getCell(7).getStringCellValue().isEmpty() && row.getCell(7).getStringCellValue().length() != 0 ? row.getCell(7).getStringCellValue() : "N/A");
            map.put("serviceChannelProblemGroup", row.getCell(8) != null && !row.getCell(8).getStringCellValue().isEmpty() && row.getCell(8).getStringCellValue().length() != 0 ? row.getCell(8).getStringCellValue() : "N/A");
            map.put("serviceChannelProblemCode", row.getCell(9) != null && !row.getCell(9).getStringCellValue().isEmpty() && row.getCell(9).getStringCellValue().length() != 0 ? row.getCell(9).getStringCellValue() : "N/A");
            map.put("serviceChannelArea", row.getCell(10) != null && !row.getCell(10).getStringCellValue().isEmpty() && row.getCell(10).getStringCellValue().length() != 0 ? row.getCell(10).getStringCellValue() : "N/A");
            map.put("serviceChannelPriority", row.getCell(11) != null && !row.getCell(11).getStringCellValue().isEmpty() && row.getCell(11).getStringCellValue().length() != 0 ? row.getCell(11).getStringCellValue() : "N/A");
            rowData.add(map);
            map = new HashMap<String, String>();
        }

        for (int i = 0; i <= testData.getIntList("jobReferences").size(); i++) {
            runtimeState.scenario.write("Checking job - " + testData.getIntList("jobReferences").get(i) + " is mapped correctly in Service Channel");
            Map<String, Object> mercuryJobDetails = dbHelperServiceChannel.getMercuryJobDetails(testData.getIntList("jobReferences").get(i));

            assertTrade(mercuryJobDetails, rowData.get(i).get("serviceChannelTrade"));
            assertEquipment(mercuryJobDetails, rowData.get(i).get("serviceChannelEquipment"));
            //To insert assertion for problem group mapping if required
            assertProblemCode(mercuryJobDetails, Integer.valueOf(rowData.get(i).get("serviceChannelProblemCode")));
            assertArea(mercuryJobDetails, rowData.get(i).get("serviceChannelArea"));
            assertPriority(mercuryJobDetails, rowData.get(i).get("serviceChannelPriority"));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the database queue has been updated correctly$")
    public void theDatabaseQueueHasBeenUpdatedCorrectly() throws InterruptedException {
        Thread.sleep(60000);
        for (int i = 0; i < testData.getIntList("jobIds").size(); i++) {
            Map<String, Object> queueDetails = dbHelperServiceChannel.getWorkOrderQueueDetails(String.valueOf(testData.getIntList("jobIds").get(i)));

            eventAssertion(queueDetails, i);

            Boolean existInServiceChannel = (Boolean) queueDetails.get("AlreadyExistedInServiceChannel");

            if (existInServiceChannel != null) {
                if (existInServiceChannel) {
                    transformAssertion(queueDetails, i);
                    sentToServiceChannelTrueAssertion(queueDetails, i);
                    finalizedAtAssertion(queueDetails, i);
                    runtimeState.scenario.write("Work Order Id for job - " + testData.getIntList("jobIds").get(i) + " is - " + queueDetails.get("WorkOrderId"));

                } else if (!existInServiceChannel) {
                    transformAssertion(queueDetails, i);
                    sentToServiceChannelFalseAssertion(queueDetails, i);
                    finalizedAtAssertion(queueDetails, i);
                    runtimeState.scenario.write("Work Order Id for job - " + testData.getIntList("jobIds").get(i) + " is - " + queueDetails.get("WorkOrderId"));
                }

            } else if (existInServiceChannel == null) {
                transformExceptionAssertion(queueDetails, i);
                sentToServiceChannelFalseAssertion(queueDetails, i);
                finalizedAtNullAssertion(queueDetails, i);
                workOrderIdNullAssertion(queueDetails, i);
                runtimeState.scenario.write("Work Order Id for job - " + testData.getIntList("jobIds").get(i) + " is - " + queueDetails.get("WorkOrderId"));
            }
        }
    }

    public void workOrderIdNullAssertion(Map<String, Object> queueDetails, int i) {
        try {
            assertNull("WorkOrderId is not null", queueDetails.get("WorkOrderId"));
        } catch (AssertionError e){
            runtimeState.scenario.write("ERROR: WorkOrderId is not null for job - " + testData.getIntList("jobIds").get(i));
        }
    }

    public void finalizedAtNullAssertion(Map<String, Object> queueDetails, int i) {
        try {
            assertNull("FinalizedAt is not null", queueDetails.get("FinalizedAt"));
        } catch (AssertionError e){
            runtimeState.scenario.write("ERROR: Finalized event published for job - " + testData.getIntList("jobIds").get(i));
        }
    }

    public void finalizedAtAssertion(Map<String, Object> queueDetails, int i) {
        try {
            assertNotNull("FinalizedAt is null", queueDetails.get("FinalizedAt"));
        } catch (AssertionError e){
            runtimeState.scenario.write("ERROR: Finalized event not published for job - " + testData.getIntList("jobIds").get(i)
                    + "\r\n Load Exception is " + queueDetails.get("LoadException"));
        }
    }

    public void sentToServiceChannelFalseAssertion(Map<String, Object> queueDetails, int i) {
        try {
            assertNull("SentToServiceChannelAt is not null", queueDetails.get("SentToServiceChannelAt"));
        } catch (AssertionError e){
            runtimeState.scenario.write("ERROR: Job - " + testData.getIntList("jobIds").get(i) + " has been sent to Service Channel");
        }
    }

    public void sentToServiceChannelTrueAssertion(Map<String, Object> queueDetails, int i) {
        try {
            assertNotNull("SentToServiceChannelAt is null", queueDetails.get("SentToServiceChannelAt"));
        } catch (AssertionError e){
            runtimeState.scenario.write("ERROR: Job - " + testData.getIntList("jobIds").get(i) + " has not been sent to Service Channel");
        }
    }

    public void transformExceptionAssertion(Map<String, Object> queueDetails, int i) {
        try {
            assertNotNull("TransformException is null", queueDetails.get("TransformException"));
        } catch (AssertionError e){
            runtimeState.scenario.write("ERROR: Transform exception not shown for job - " + testData.getIntList("jobIds").get(i));
        }
    }

    public void transformAssertion(Map<String, Object> queueDetails, int i) {
        try {
            assertNotNull("TransformedEventJson is null", queueDetails.get("TransformedEventJson"));
            assertNotNull("TransformedPublishedAt is null", queueDetails.get("TransformedPublishedAt"));
        } catch (AssertionError e){
            runtimeState.scenario.write("ERROR: Transform event not published for job - " + testData.getIntList("jobIds").get(i)
                    + "\r\n Transform Exception is " + queueDetails.get("TransformException"));
        }
    }

    public void eventAssertion(Map<String, Object> queueDetails, int i) {
        try {
            assertNotNull("EventLoggedAt is null", queueDetails.get("EventLoggedAt"));
            assertNotNull("EventPublishedAt is null", queueDetails.get("EventLoggedAt"));
        } catch (AssertionError e){
            runtimeState.scenario.write("ERROR: Event not logged or published for job - " + testData.getIntList("jobIds").get(i));
        }
    }

    public void logInToServiceChannel() throws InterruptedException {
        getWebDriver().get(propertyHelper.getServiceChannelUrl());
        runtimeState.serviceChannelLoginPage = new ServiceChannelLoginPage(getWebDriver()).get();

        if (runtimeState.serviceChannelLoginPage.isAnnouncementModalDisplayed()) {
            runtimeState.serviceChannelLoginPage.closeAnnouncementModal();
        }

        runtimeState.serviceChannelHomePage = runtimeState.serviceChannelLoginPage.login("CityfmAPI", "cityfm2018");

        if (runtimeState.serviceChannelHomePage.isNavigationPopupDisplayed()) {
            runtimeState.serviceChannelHomePage.closeNavigationPopup();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" jobs are displayed correctly on Service Channel$")
    public void jobsAreDisplayedCorrectlyOnServiceChannel(String jobType) throws ParseException, InterruptedException {
        logInToServiceChannel();

        for (int i = 0; i < testData.getIntList("createdJobs").size(); i++) {
            int jobReference = testData.getIntList("createdJobs").get(i);
            while (runtimeState.serviceChannelHomePage.isLoadingIconDisplayed()) {
                Thread.sleep(3000);
            }
            runtimeState.serviceChannelHomePage.selectSearchTypeDropdownOption("by Purchase Order #");
            runtimeState.serviceChannelHomePage.enterFilterText(String.valueOf(jobReference));
            runtimeState.serviceChannelSearchResultsPage = runtimeState.serviceChannelHomePage.clickSearchButton();

            runtimeState.serviceChannelJobDetailsPage = runtimeState.serviceChannelSearchResultsPage.selectFirstResultFound();

            if (!jobType.equals("PPM")) {
                Map<String, Object> mercuryJobDetails = dbHelperServiceChannel.getMercuryJobDetails(jobReference);
                runtimeState.scenario.write("mercuryJobDetails - " + mercuryJobDetails);
                String faultType = normalize(String.valueOf(mercuryJobDetails.get("FaultType")).replaceAll("/", " > ").toUpperCase());
                assertStatus(mercuryJobDetails);
                assertFaultType(mercuryJobDetails, faultType);
                assertDescription(mercuryJobDetails, faultType);
                assertTrade(mercuryJobDetails);
                assertPriority(mercuryJobDetails);
                assertCategory(mercuryJobDetails, jobType);
                assertTrackingNumber(jobReference);
                assertPurchaseOrderNumber(jobReference);
                assertScheduledDate(mercuryJobDetails);
                assertSiteDetails(mercuryJobDetails);

            } else {
                Map<String, Object> mercuryPpmJobDetails = dbHelperServiceChannel.getMercuryPpmJobDetails(jobReference);
                assertPpmStatus(mercuryPpmJobDetails);
                assertPpmDescription(mercuryPpmJobDetails);
                assertPpmTrade(mercuryPpmJobDetails);
                assertPpmPriority();
                assertPpmCategory();
                assertTrackingNumber(jobReference);
                assertPurchaseOrderNumber(jobReference);
                assertPpmSiteDetails(mercuryPpmJobDetails);
            }
        }
    }

    public void assertPpmSiteDetails(Map<String, Object> mercuryPpmJobDetails) {
        String expectedSiteCode = String.valueOf(mercuryPpmJobDetails.get("SiteCode"));
        String expectedSiteAddress1 = String.valueOf(mercuryPpmJobDetails.get("Address1"));
        String expectedSiteAddress2 = String.valueOf(mercuryPpmJobDetails.get("Town")) + " "
                + String.valueOf(mercuryPpmJobDetails.get("County")) + " "
                + String.valueOf(mercuryPpmJobDetails.get("Postcode")) + " US";
        String expectedSitePhoneNumber = String.valueOf(mercuryPpmJobDetails.get("TelNo"));

        String actualSiteCode = runtimeState.serviceChannelJobDetailsPage.getSiteCode();
        String actualSiteAddress1 = runtimeState.serviceChannelJobDetailsPage.getSiteAddress1();
        String actualSiteAddress2 = runtimeState.serviceChannelJobDetailsPage.getSiteAddress2();
        String actualSitePhoneNumber = runtimeState.serviceChannelJobDetailsPage.getSitePhoneNumber();

        runtimeState.scenario.write("Asserting that Site details are correct");
        assertEquals("Expected: " + expectedSiteCode + " but was: " + actualSiteCode,
                expectedSiteCode, actualSiteCode);
        assertEquals("Expected: " + expectedSiteAddress1 + " but was: " + actualSiteAddress1,
                expectedSiteAddress1, actualSiteAddress1);
        assertEquals("Expected: " + expectedSiteAddress2 + " but was: " + actualSiteAddress2,
                expectedSiteAddress2, actualSiteAddress2);
        assertEquals("Expected: " + expectedSitePhoneNumber + " but was: " + actualSitePhoneNumber,
                expectedSitePhoneNumber, actualSitePhoneNumber);
    }

    public void assertSiteDetails(Map<String, Object> mercuryJobDetails) {
        String expectedSiteCode = String.valueOf(mercuryJobDetails.get("SiteCode"));
        String expectedSiteAddress1 = String.valueOf(mercuryJobDetails.get("Address1"));
        String expectedSiteAddress2 = String.valueOf(mercuryJobDetails.get("Address2")) + " "
                + String.valueOf(mercuryJobDetails.get("County")) + " "
                + String.valueOf(mercuryJobDetails.get("Postcode")) + " US";
        String expectedSitePhoneNumber = String.valueOf(mercuryJobDetails.get("TelNo"));

        String actualSiteCode = runtimeState.serviceChannelJobDetailsPage.getSiteCode();
        String actualSiteAddress1 = runtimeState.serviceChannelJobDetailsPage.getSiteAddress1();
        String actualSiteAddress2 = runtimeState.serviceChannelJobDetailsPage.getSiteAddress2();
        String actualSitePhoneNumber = runtimeState.serviceChannelJobDetailsPage.getSitePhoneNumber();

        runtimeState.scenario.write("Asserting that Site details are correct");
        assertEquals("Expected: " + expectedSiteCode + " but was: " + actualSiteCode,
                expectedSiteCode, actualSiteCode);
        assertEquals("Expected: " + expectedSiteAddress1 + " but was: " + actualSiteAddress1,
                expectedSiteAddress1, actualSiteAddress1);
        assertEquals("Expected: " + expectedSiteAddress2 + " but was: " + actualSiteAddress2,
                expectedSiteAddress2, actualSiteAddress2);
        assertEquals("Expected: " + expectedSitePhoneNumber + " but was: " + actualSitePhoneNumber,
                expectedSitePhoneNumber, actualSitePhoneNumber);
    }

    public void assertScheduledDate(Map<String, Object> mercuryJobDetails) throws ParseException {
        String scheduled = String.valueOf(mercuryJobDetails.get("RepairTargetDate"));
        scheduled = StringUtils.substring(scheduled, 0, 16);
        String expectedScheduledDateAndTime1 = DateHelper.convertAddHours(scheduled, "yyyy-MM-dd HH:mm", "MMM dd yyyy HH:mm", -4) + " EST";
        String expectedScheduledDateAndTime2 = DateHelper.convertAddHours(scheduled, "yyyy-MM-dd HH:mm", "MMM dd yyyy HH:mm", -5) + " EST";
        String scheduledDate = runtimeState.serviceChannelJobDetailsPage.getScheduledDate();
        String scheduledTime = runtimeState.serviceChannelJobDetailsPage.getScheduledTime();
        String actualScheduledDateAndTime = scheduledDate + " " + scheduledTime;

        runtimeState.scenario.write("Asserting that Scheduled Date is correct");
        assertThat(actualScheduledDateAndTime, isOneOf(expectedScheduledDateAndTime1, expectedScheduledDateAndTime2));
    }

    public void assertPurchaseOrderNumber(int jobReference) {
        String purchaseOrderNumber = runtimeState.serviceChannelJobDetailsPage.getPurchaseOrderNumber();
        runtimeState.scenario.write("purchaseOrderNumber - " + purchaseOrderNumber);
        runtimeState.scenario.write("Asserting that Purchase Order Number is correct");
        assertEquals("Expected: " + String.valueOf(jobReference) + " but was: " + purchaseOrderNumber,
                String.valueOf(jobReference), purchaseOrderNumber);
    }

    public void assertTrackingNumber(int jobReference) {
        int expectedTrackingNumber = dbHelperServiceChannel.getServiceChannelTrackingNumber(jobReference);
        runtimeState.scenario.write("expectedTrackingNumber - " + expectedTrackingNumber);
        String actualTrackingNumber = runtimeState.serviceChannelJobDetailsPage.getTrackingNumber();
        runtimeState.scenario.write("actualTrackingNumber - " + actualTrackingNumber);
        runtimeState.scenario.write("Asserting that Tracking Number is correct");
        assertEquals("Expected: " + String.valueOf(expectedTrackingNumber) + " but was: " + actualTrackingNumber,
                String.valueOf(expectedTrackingNumber), actualTrackingNumber);
    }

    public void assertPpmCategory() {
        String expectedCategory = dbHelperServiceChannel.getServiceChannelCategory("PPM", null).toUpperCase();
        String actualCategory = runtimeState.serviceChannelJobDetailsPage.getCategory();
        runtimeState.scenario.write("Asserting that Category is correct");
        assertEquals("Expected: " + expectedCategory + " but was: " + actualCategory,
                expectedCategory, actualCategory);
    }

    public void assertCategory(Map<String, Object> mercuryJobDetails, String jobType) {
        String expectedCategory = null;
        if (jobType.equalsIgnoreCase("Reactive")) {
            expectedCategory = dbHelperServiceChannel.getServiceChannelCategory(jobType, null).toUpperCase();
        } else if (jobType.equalsIgnoreCase("Quote")) {
            expectedCategory = dbHelperServiceChannel.getServiceChannelCategory(jobType, (int) mercuryJobDetails.get("FundingRouteID")).toUpperCase();
        } else {
            expectedCategory = dbHelperServiceChannel.getServiceChannelCategory("Quote", (int) mercuryJobDetails.get("FundingRouteID")).toUpperCase();
        }
        runtimeState.scenario.write("expectedCategory - " + expectedCategory);
        String actualCategory = runtimeState.serviceChannelJobDetailsPage.getCategory();
        runtimeState.scenario.write("actualCategory - " + actualCategory);
        runtimeState.scenario.write("Asserting that Category is correct");
        assertEquals("Expected: " + expectedCategory + " but was: " + actualCategory,
                expectedCategory, actualCategory);
    }

    public void assertPpmPriority() {
        String expectedPriority = dbHelperServiceChannel.getServiceChannelPpmPriority("PPM").toUpperCase();
        String actualPriority = runtimeState.serviceChannelJobDetailsPage.getPriority();
        runtimeState.scenario.write("Asserting that Priority is correct");
        assertEquals("Expected: " + expectedPriority + " but was: " + actualPriority,
                expectedPriority, actualPriority);
    }

    public void assertPriority(Map<String, Object> mercuryJobDetails) {
        String expectedPriority = dbHelperServiceChannel.getServiceChannelPriority((int) mercuryJobDetails.get("PriorityId")).toUpperCase();
        runtimeState.scenario.write("expectedPriority - " + expectedPriority);
        String actualPriority = runtimeState.serviceChannelJobDetailsPage.getPriority();
        runtimeState.scenario.write("actualPriority - " + actualPriority);
        runtimeState.scenario.write("Asserting that Priority is correct");
        assertEquals("Expected: " + expectedPriority + " but was: " + actualPriority,
                expectedPriority, actualPriority);
    }

    public void assertPriority(Map<String, Object> mercuryJobDetails, String priority) {
        String expectedPriority = dbHelperServiceChannel.getServiceChannelPriority((int) mercuryJobDetails.get("PriorityId")).toUpperCase();
        assertEquals("Expected: " + expectedPriority + " but was: " + priority,
                expectedPriority, priority);
    }

    public void assertPpmTrade(Map<String, Object> mercuryPpmJobDetails) {
        String expectedTradeName = dbHelperServiceChannel.getServiceChannelPpmTradeName((int) mercuryPpmJobDetails.get("PpmTypeId")).toUpperCase();
        String actualTradeName = runtimeState.serviceChannelJobDetailsPage.getTrade();
        runtimeState.scenario.write("Asserting that Trade is correct");
        assertEquals("Expected: " + expectedTradeName + " but was: " + actualTradeName,
                expectedTradeName, actualTradeName);
    }

    public void assertTrade(Map<String, Object> mercuryJobDetails) {
        String expectedTradeName = dbHelperServiceChannel.getServiceChannelTradeName((int) mercuryJobDetails.get("AssetTypeId"), (int) mercuryJobDetails.get("AssetSubTypeId"), (int) mercuryJobDetails.get("AssetClassificationId")).toUpperCase();
        runtimeState.scenario.write("expectedTradeName - " + expectedTradeName);
        String actualTradeName = runtimeState.serviceChannelJobDetailsPage.getTrade();
        runtimeState.scenario.write("actualTradeName - " + actualTradeName);
        runtimeState.scenario.write("Asserting that Trade is correct");
        assertEquals("Expected: " + expectedTradeName + " but was: " + actualTradeName,
                expectedTradeName, actualTradeName);
    }

    public void assertTrade(Map<String, Object> mercuryJobDetails, String tradeName) {
        String expectedTradeName = dbHelperServiceChannel.getServiceChannelTradeName((int) mercuryJobDetails.get("AssetTypeId"), (int) mercuryJobDetails.get("AssetSubTypeId"), (int) mercuryJobDetails.get("AssetClassificationId"));
        runtimeState.scenario.write("Asserting that Trade is correct");
        assertEquals("Expected: " + expectedTradeName + " but was: " + tradeName,
                expectedTradeName, tradeName);
    }

    public void assertEquipment(Map<String, Object> mercuryJobDetails, String equipmentName) {
        String expectedEquipmentName = dbHelperServiceChannel.getServiceChannelEquipmentName((int) mercuryJobDetails.get("AssetTypeId"), (int) mercuryJobDetails.get("AssetSubTypeId"), (int) mercuryJobDetails.get("AssetClassificationId"));
        runtimeState.scenario.write("Asserting that Equipment is correct");
        assertEquals("Expected: " + expectedEquipmentName + " but was: " + equipmentName,
                expectedEquipmentName, equipmentName);
    }

    public void assertProblemCode(Map<String, Object> mercuryJobDetails, int problemcode) {
        String expectedProblemCode = dbHelperServiceChannel.getServiceChannelProblemCode((int) mercuryJobDetails.get("FaultTypeId"));
        runtimeState.scenario.write("Asserting that Equipment is correct");
        assertEquals("Expected: " + expectedProblemCode + " but was: " + problemcode,
                expectedProblemCode, problemcode);
    }

    public void assertArea(Map<String, Object> mercuryJobDetails, String area) {
        String expectedArea = dbHelperServiceChannel.getServiceChannelArea((int) mercuryJobDetails.get("LocationId"));
        runtimeState.scenario.write("Asserting that Area is correct");
        assertEquals("Expected: " + expectedArea + " but was: " + area,
                expectedArea, area);
    }

    public void assertPpmDescription(Map<String, Object> mercuryPpmJobDetails) {
        String expectedDescription = String.valueOf(mercuryPpmJobDetails.get("PpmType"));
        int sizeOfScDescription = expectedDescription.length();

        if (sizeOfScDescription > 150) {
            expectedDescription = StringUtils.substring(expectedDescription, 0, 150) + "...";
        }
        String actualDescription = runtimeState.serviceChannelJobDetailsPage.getDescription();
        runtimeState.scenario.write("Asserting that Description is correct");
        assertEquals("Expected: " + expectedDescription + " but was: " + actualDescription,
                expectedDescription, actualDescription);
    }

    public void assertDescription(Map<String, Object> mercuryJobDetails, String faultType) {
        String location = String.valueOf(mercuryJobDetails.get("Location")).toUpperCase();
        runtimeState.scenario.write("location - " + location);
        String assetType = String.valueOf(mercuryJobDetails.get("AssetType")).toUpperCase();
        runtimeState.scenario.write("assetType - " + assetType);
        String assetSubType = String.valueOf(mercuryJobDetails.get("AssetSubType")).toUpperCase();
        runtimeState.scenario.write("assetSubType - " + assetSubType);
        String assetClassification = String.valueOf(mercuryJobDetails.get("AssetClassification")).toUpperCase();
        runtimeState.scenario.write("assetClassification - " + assetClassification);
        String description = String.valueOf(mercuryJobDetails.get("Description")).toUpperCase();
        runtimeState.scenario.write("description - " + description);
        String serviceChannelDescription = location + " / " + assetType + " / " + assetSubType
                + " / " + assetClassification + " / " + faultType + " / " + description;
        int sizeOfScDescription = serviceChannelDescription.length();

        if (sizeOfScDescription > 150) {
            serviceChannelDescription = StringUtils.substring(serviceChannelDescription, 0, 150) + "...";
        }
        runtimeState.scenario.write("serviceChannelDescription - " + serviceChannelDescription);
        String actualDescription = runtimeState.serviceChannelJobDetailsPage.getDescription();
        runtimeState.scenario.write("actualDescription - " + actualDescription);
        runtimeState.scenario.write("Asserting that Description is correct");
        assertEquals("Expected: " + serviceChannelDescription + " but was: " + actualDescription,
                serviceChannelDescription, actualDescription);
    }

    public void assertFaultType(Map<String, Object> mercuryJobDetails, String faultType) {
        String actualFaultType = runtimeState.serviceChannelJobDetailsPage.getFaultType();
        runtimeState.scenario.write("actualFaultType - " + actualFaultType);
        runtimeState.scenario.write("Asserting that Fault Type is correct");
        assertEquals("Expected: " + faultType + " but was: " + actualFaultType,
                faultType, actualFaultType);
    }

    public void assertPpmStatus(Map<String, Object> mercuryPpmJobDetails) {
        Map<String, Object> serviceChannelStatuses = dbHelperServiceChannel.getServiceChannelPpmStatus((int) mercuryPpmJobDetails.get("PpmStatusId"));
        runtimeState.scenario.write("serviceChannelStatuses - " + serviceChannelStatuses);
        String actualPrimaryStatus = runtimeState.serviceChannelJobDetailsPage.getPrimaryStatus();
        runtimeState.scenario.write("actualPrimaryStatus - " + actualPrimaryStatus);
        String actualExtendedStatus = runtimeState.serviceChannelJobDetailsPage.getExtendedStatus();
        runtimeState.scenario.write("actualExtendedStatus - " + actualExtendedStatus);
        runtimeState.scenario.write("Asserting that Status is correct");
        assertEquals("Expected: " + serviceChannelStatuses.get("ServiceChannelStatus") + "but was: " + actualPrimaryStatus,
                serviceChannelStatuses.get("ServiceChannelStatus"), actualPrimaryStatus);
        if ((!actualExtendedStatus.isEmpty()) && (actualExtendedStatus != null)) {
            assertEquals("Expected: " + serviceChannelStatuses.get("ServiceChannelExtendedStatus").toString() + " but was: " + actualExtendedStatus,
                    serviceChannelStatuses.get("ServiceChannelExtendedStatus").toString(), actualExtendedStatus);
        }
    }

    public void assertStatus(Map<String, Object> mercuryJobDetails) {
        Map<String, Object> serviceChannelStatuses = dbHelperServiceChannel.getServiceChannelStatus((int) mercuryJobDetails.get("JobTypeId"), String.valueOf(mercuryJobDetails.get("JobStatus")));
        runtimeState.scenario.write("serviceChannelStatuses - " + serviceChannelStatuses);
        String actualPrimaryStatus = runtimeState.serviceChannelJobDetailsPage.getPrimaryStatus();
        runtimeState.scenario.write("actualPrimaryStatus - " + actualPrimaryStatus);
        String actualExtendedStatus = runtimeState.serviceChannelJobDetailsPage.getExtendedStatus();
        runtimeState.scenario.write("actualExtendedStatus - " + actualExtendedStatus);
        runtimeState.scenario.write("Asserting that Status is correct");
        assertEquals("Expected: " + serviceChannelStatuses.get("ServiceChannelStatus") + "but was: " + actualPrimaryStatus,
                serviceChannelStatuses.get("ServiceChannelStatus"), actualPrimaryStatus);
        if ((!actualExtendedStatus.isEmpty()) && (actualExtendedStatus != null)) {
            assertEquals("Expected: " + serviceChannelStatuses.get("ServiceChannelExtendedStatus").toString() + " but was: " + actualExtendedStatus,
                    serviceChannelStatuses.get("ServiceChannelExtendedStatus").toString(), actualExtendedStatus);
        }
    }
}
