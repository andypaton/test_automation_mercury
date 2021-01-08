package mercury.helpers;

import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.databuilders.TestData;
import mercury.databuilders.Toggles;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.gridV3.Grid;
import mercury.pageobject.web.admin.companies.AdminEditCompanyPage;
import mercury.pageobject.web.helpdesk.HelpdeskTimelineTab;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentTimelineTab;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskViewIncidentPage;
import mercury.pageobject.web.helpdesk.jobs.AddJobNotesModal;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskLinkedIncidentsModal;
import mercury.pageobject.web.portal.creditnotes.CreditNoteApprovalPage;
import mercury.pageobject.web.portal.creditnotes.EnterCreditNoteDetailsPage;
import mercury.pageobject.web.portal.invoices.InvoiceFinalApprovalPage;
import mercury.pageobject.web.portal.invoices.InvoiceSubmittedPage;
import mercury.pageobject.web.portal.jobs.AllJobsPage_OpsDirector;
import mercury.pageobject.web.portal.jobs.AsbestosModal;
import mercury.pageobject.web.portal.jobs.PortalJobsForSitePage;
import mercury.pageobject.web.portal.jobs.UpdateJobPage;
import mercury.runtime.RuntimeState;

@Component
public class ButtonHelper {

    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private OutputHelper outputHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private Toggles toggles;
    @Autowired private TzHelper tzHelper;


    public void findAllJobs() {
        if (testData.getString("profileName").equals("Operations Director")) {
            if (runtimeState.allJobsPage_OpsDirector == null) runtimeState.allJobsPage_OpsDirector = new AllJobsPage_OpsDirector(getWebDriver()).get();

            // Operations Director's have so many jobs that they take forever to be displayed!
            getWebDriver().manage().timeouts().implicitlyWait(MAX_SYNC_TIMEOUT, TimeUnit.SECONDS);
            runtimeState.allJobsPage_OpsDirector.clickFindButton();
            getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);

        } else {
            runtimeState.allJobsPage.clickFindButton();
        }
    }

    public void add() {
        if (getWebDriver().getCurrentUrl().contains("Portal/Job/Update") && runtimeState.portalRefrigerantSourceModal.isDisplayed()) {
            runtimeState.portalRefrigerantSourceModal.save();
        } else {
            runtimeState.addNewCreditNoteLineModal.clickAddButton();
        }
    }

    public void addNewCaller() {
        if (getWebDriver().getCurrentUrl().contains("search/site")) {
            runtimeState.helpdeskAddCallerPage = runtimeState.helpdeskSitePage.clickAddNewCaller();
        } else if (getWebDriver().getCurrentUrl().contains("logjob")) {
            runtimeState.newCallerPage = runtimeState.helpdeskLogJobPage.clickAddNewCaller();
        }
    }

    public void addNote() throws ParseException {
        if (getWebDriver().getCurrentUrl().contains("logjob")) {
            runtimeState.helpdeskLogJobPage.clickAddNotes();
            runtimeState.addJobNotesModal = new AddJobNotesModal(getWebDriver()).get();
        } else if ((getWebDriver().getCurrentUrl().contains("incident"))) {
            runtimeState.helpdeskIncidentTimelineTab = new HelpdeskIncidentTimelineTab(getWebDriver()).get();
            int siteId = dbHelperSites.getSiteId(runtimeState.helpdeskViewIncidentPage.getSiteValue());
            testData.put("storeTime", tzHelper.getCurrentTimeAtSite(siteId));
            runtimeState.helpdeskIncidentUpdate = runtimeState.helpdeskIncidentTimelineTab.clickAddNote();
        } else {
            runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
            runtimeState.helpdeskTimelineTab.clickAddNotes();
        }
    }

    public void approve() throws ParseException {
        if (getWebDriver().getCurrentUrl().contains("/Portal/InvoiceFinalApproval/Approval")) {
            if (runtimeState.invoiceFinalApprovalPage == null) runtimeState.invoiceFinalApprovalPage = new InvoiceFinalApprovalPage(getWebDriver()).get();

            if ( !runtimeState.invoiceFinalApprovalPage.isApprovable() ) {
                runtimeState.invoiceFinalApprovalPage.clickNext();
            }

            runtimeState.approveInvoiceModalPage = runtimeState.invoiceFinalApprovalPage.approve();

        } else {
            runtimeState.creditNoteApprovalPage.clickApproveButton();
        }
    }

    public void attach() throws ParseException {
        int siteId = dbHelperSites.getSiteIdForJobRef(testData.getInt("jobReference"));

        Date date = tzHelper.getCurrentTimeAtHomeOffice(siteId);
        String homeOfficeTime = DateHelper.dateAsString(date, MEDIUM);
        testData.addStringTag("dateCreated", "HO - " + homeOfficeTime);

        runtimeState.helpdeskAddAttachmentsModal.clickAttachButton();
    }

    public void attachment() {
        if (getWebDriver().getCurrentUrl().contains("Helpdesk#!/logjob/")) {
            testData.addIntegerTag("attachmentsCount", runtimeState.helpdeskLogJobPage.getAttachmentButtonBadgeNumber());
            runtimeState.helpdeskAddAttachmentsModal = runtimeState.helpdeskLogJobPage.clickAttachmentsIcon();
        } else {
            testData.addIntegerTag("attachmentsCount", runtimeState.helpdeskJobPage.getAttachmentCount());
            runtimeState.helpdeskAddAttachmentsModal = runtimeState.helpdeskJobPage.clickAttachmentsIcon();
        }
    }


    public void back() {
        if (getWebDriver().getCurrentUrl().contains("Helpdesk#!/logjob") && runtimeState.confirmJobChangesModal.isDisplayed()) {
            runtimeState.helpdeskLogJobPage = runtimeState.confirmJobChangesModal.back();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Feedback/JobDetail")) {
            runtimeState.feedbackPage = runtimeState.feedbackResponsePage.clickBackButton();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/AllOrders")) {
            runtimeState.portalSummaryPage = runtimeState.allOrdersPage.clickBackButton();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/AccountsPayable/SubmittedInvoicesAndCredits")) {
            runtimeState.portalSummaryPage = runtimeState.submittedInvoicesAndCreditsPage.clickBackButton();
        }
    }

    public void call() {
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
        runtimeState.helpdeskCallJobContactModal = runtimeState.helpdeskManageResourcesPanel.callResource();
    }

    public void callJobContact() {
        runtimeState.helpdeskCallJobContactModal = runtimeState.helpdeskAdviseEtaPanel.clickCallJobContact();
        outputHelper.takeScreenshots();
        runtimeState.helpdeskCallJobContactModal.clickCallcontact();
    }

    public void cancel() {
        if (getWebDriver().getCurrentUrl().contains("Portal/SupplierTechnician/JobsForStore")) {
            int asbestosNotification = dbHelperSystemToggles.getSystemFeatureToggle("AsbestosNotification");
            // If asbestos turned on the we should get an asbestos warning
            if (asbestosNotification == 1) {
                String answer = RandomUtils.nextBoolean() ? "Yes" :"No";
                runtimeState.scenario.write("Confirming 'Asbestos register has been checked': " + answer);
                runtimeState.asbestosModal = new AsbestosModal(getWebDriver()).get();
                runtimeState.asbestosModal.clickAnswer(answer);
                runtimeState.asbestosModal.clickCancel();
            } else {
                // Else we just see the confirmation modal
                runtimeState.portalJobsForSitePage = runtimeState.portalStartWorkModalPage.clickCancelButton();
            }
        } else if (getWebDriver().getCurrentUrl().contains("quotes")) {
            runtimeState.helpdeskJobPage = runtimeState.helpdeskJobActionsPanel.cancel();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/ConsolidatedInvoice")) {
            runtimeState.manageInvoiceLinesModal.clickCancelButton();
        } else if (getWebDriver().getCurrentUrl().contains("Site#")) {
            runtimeState.adminSitesPage = runtimeState.adminEditSitePage.clickCancelButton();
        } else if (getWebDriver().getCurrentUrl().contains("SiteType#")) {
            runtimeState.adminEditSiteTypePage.clickCancelButton();
        } else {
            runtimeState.cancelChangesAlert = runtimeState.helpdeskLogJobPage.cancel();
        }
    }

    public void cancelJob() {
        runtimeState.helpdeskTimelineTab = runtimeState.helpdeskJobCancelPanel.cancelJob();
        runtimeState.helpdeskJobPage.get();
    }

    public void close() {
        if (getWebDriver().getCurrentUrl().contains("job")) {
            runtimeState.helpdeskLinkedIncidentsModal.closeLinkedIncidentsModal();
        } else if (getWebDriver().getCurrentUrl().contains("incident")) {
            runtimeState.helpdeskIncidentsLinkedJobsModal.closeLinkedJobsModal();
        }
    }

    public void confirmStartWork() {
        int asbestosNotification = dbHelperSystemToggles.getSystemFeatureToggle("AsbestosNotification");
        // If asbestos turned on the we should get an asbestos warning
        if (asbestosNotification == 1) {
            String answer = RandomUtils.nextBoolean() ? "Yes" :"No";
            runtimeState.scenario.write("Confirming 'Asbestos register has been checked': " + answer);
            runtimeState.asbestosModal = new AsbestosModal(getWebDriver()).get();
            runtimeState.asbestosModal.clickAnswer(answer);
            runtimeState.asbestosModal.clickOk();
        } else {
            // Else we just see the confirmation modal
            runtimeState.portalStartWorkModalPage.clickConfirmButton();
        }
    }

    public void convertToQuoteJob() {
        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskJobActionsPanel.convertToQuoteJob();
        testData.put("description", runtimeState.helpdeskLogJobPage.getScopeOfWork());
    }

    public void createLinkedQuoteJob() {
        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskJobActionsPanel.createLinkedQuoteJob();
        testData.put("description", runtimeState.helpdeskLogJobPage.getScopeOfWork());
        testData.put("linkQuoteJob", true);
    }

    public void createNewIncident() {
        runtimeState.helpdeskLinkedIncidentsModal = new HelpdeskLinkedIncidentsModal(getWebDriver()).get();
        runtimeState.helpdeskLogAnIncidentPage = runtimeState.helpdeskLinkedIncidentsModal.clickCreateNewIncidentButton();
    }


    public void editQuoteJobRecommendations() {
        runtimeState.fundingRequestsAwaitingApprovalPage.clickEditQuoteJobRecommendationsRadioButton();
    }

    public void confirmQuoteJobRejection() {
        runtimeState.submitQuoteJobRejectionModalPage = runtimeState.fundingRequestsAwaitingApprovalPage.clickConfirmQuoteJobRejectionRadioButton();
    }

    public void link() {
        runtimeState.helpdeskLinkedIncidentsModal = new HelpdeskLinkedIncidentsModal(getWebDriver()).get();
        runtimeState.helpdeskLinkedIncidentsModal.clickLinkButton();
    }


    public void linkedIncidents() throws Throwable {
        POHelper.refreshPage();
        runtimeState.helpdeskJobsLinkedIncidentsPanel = runtimeState.helpdeskJobPage.clickLinkedIncidentsIcon();
    }

    public void linkedJobs() throws Throwable {
        runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();
        runtimeState.helpdeskIncidentsLinkedJobsModal = runtimeState.helpdeskViewIncidentPage.clickLinkedJob();
    }

    public void next() {
        if (getWebDriver().getCurrentUrl().contains("escalation")) {
            runtimeState.helpdeskIncidentSendEscalation = runtimeState.helpdeskIncidentEscalation.clickNextButton();
        } else if (getWebDriver().getCurrentUrl().contains("CreateCreditNote")) {
            runtimeState.creditNotePage = runtimeState.createCreditNotePage.clickNextButton();
        } else if(getWebDriver().getCurrentUrl().contains("EditCreditNoteHeader")) {
            runtimeState.creditNotePage.clickNextButton();
            if (getWebDriver().getCurrentUrl().contains("Portal/CreditNote/CreditNote?creditNoteHeaderId=")) {
                runtimeState.enterCreditNoteDetailsPage = new EnterCreditNoteDetailsPage(getWebDriver()).get();
            }
        }
    }

    public void requestQuote() {
        runtimeState.helpdeskLogJobPage.clickRequestQuote();
        testData.put("isQuoteRequested", true);
    }

    public void respondToQuery() {
        runtimeState.fundingRequestsAwaitingApprovalPage.clickRespondToQueryRadioButton();
    }

    public void save() throws Throwable {
        if (runtimeState.helpdeskIncidentUpdate != null && runtimeState.helpdeskIncidentUpdate.isPageDisplayed()) {
            runtimeState.helpdeskIncidentUpdate.clickSaveButton();
        } else if (getWebDriver().getCurrentUrl().contains("SystemFeatureToggle")) {
            toggles.put("haveTogglesBeenUpdated", true);
            runtimeState.adminSystemFeatureTogglePage.save();
        } else if (getWebDriver().getCurrentUrl().contains("EditGasUsage#!/editeparegulations2019")) {
            if (runtimeState.adminRefrigerantSourceModal != null && runtimeState.adminRefrigerantSourceModal.isDisplayed()) {
                runtimeState.adminRefrigerantSourceModal.save();
            } else {
                runtimeState.adminRefrigerantGasUsageEditPageV2.save();
            }
        } else if (getWebDriver().getCurrentUrl().contains("EditGasUsage")) {
            runtimeState.popupAlert = runtimeState.adminRefrigerantGasUsageEditPage.save();
        } else if (getWebDriver().getCurrentUrl().contains("logjob") && runtimeState.addJobNotesModal.isDisplayed()) {
            runtimeState.addJobNotesModal.clickSaveButton();
        } else if (getWebDriver().getCurrentUrl().contains("Helpdesk#!")) {
            if (runtimeState.helpdeskAdviseRemovalPanel != null && runtimeState.helpdeskAdviseRemovalPanel.isPageDisplayed()) {
                runtimeState.helpdeskAdviseRemovalPanel.save();
            } else {
                runtimeState.helpdeskTimelineTab.clickSaveButton();
            }
        } else if (getWebDriver().getCurrentUrl().contains("InvoiceLine#!")) {
            if (runtimeState.adminFinancePage.isSaveButtonEnabled() == true) {
                runtimeState.popupAlert = runtimeState.adminFinancePage.selectSave();
                outputHelper.takeScreenshots();
                runtimeState.popupAlert.confirm();
                outputHelper.takeScreenshots();
                runtimeState.popupAlert.ok();
            }
        } else if (getWebDriver().getCurrentUrl().contains("Portal/CreditNoteApproval")) {
            runtimeState.rejectCreditNoteModal.save();
        } else if  (getWebDriver().getCurrentUrl().contains("ConfigureCompany#!/company")) {
            runtimeState.adminEditCompanyPage = new AdminEditCompanyPage(getWebDriver()).get();
            runtimeState.adminEditCompanyPage.clickSave();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/EditInvoiceHeader")) {
            runtimeState.invoiceDetailsPage.selectSave();
        } else if (getWebDriver().getCurrentUrl().contains("Companies/ConfigureAssignmentRule")) {
            if (runtimeState.adminCompaniesEditAssignmentRuleModal != null) {
                runtimeState.adminCompaniesEditAssignmentRuleModal.clickSave();
            } else {
                runtimeState.adminCompaniesAssignmentRuleEditPage.clickSave();
            }
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/Invoice")) {
            runtimeState.newInvoicingEditInvoiceLineModal.clickSave();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/ConsolidatedInvoice")) {
            runtimeState.manageInvoiceLinesModal.clickSave();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Routed#!/ppm/")) {
            runtimeState.ppmJobDetailsPage.saveButton();
        } else if (getWebDriver().getCurrentUrl().contains("Site#")) {
            runtimeState.adminEditSitePage.clickSaveButton();
            if (testData.getString("editSite") != null && testData.getString("editSite").equals("edited")) {
                runtimeState.adminEditSitePage.checkAndConfirmAlert();
            }
        } else if (getWebDriver().getCurrentUrl().contains("SiteType#")) {
            runtimeState.adminEditSiteTypePage.clickSaveButton();
        } else if ((getWebDriver().getCurrentUrl().contains("incident") && runtimeState.helpdeskIncidentUpdate == null) || (getWebDriver().getCurrentUrl().contains("Helpdesk#!/details/job/"))) {
            runtimeState.helpdeskLogAnIncidentPage.saveButtonIsClicked();
        }
    }

    public void saveChanges() throws Throwable {
        if (getWebDriver().getCurrentUrl().contains("OnCallSchedulerAdmin#")) {
            runtimeState.onCallSchedulerAdminPage.clickSaveChangesButton();
        } else {
            runtimeState.invoiceDetailsPage.clickSave();
        }
    }

    public void sendResponse() {

        if (getWebDriver().getCurrentUrl().contains("Portal/QuoteCreateEdit/Query")) {
            runtimeState.quoteQueryPage.clickSendResponseButton();
        } else {
            runtimeState.fundingRequestQueriesPage = runtimeState.fundingRequestsAwaitingApprovalPage.clickSendResponseButton();
        }
    }

    public void splitRFMs() {
        if (getWebDriver().getCurrentUrl().contains("/Portal/JobList/AllJobs")) {
            if (runtimeState.allJobsPage_OpsDirector == null) runtimeState.allJobsPage_OpsDirector = new AllJobsPage_OpsDirector(getWebDriver()).get();
            runtimeState.allJobsPage_OpsDirector.clickSplitRFMs();
        } else {
            runtimeState.feedbackPage.clickSplitRFMsButton();
        }
    }

    public void startWork() throws ParseException {
        testData.put("Start time", tzHelper.getCurrentTimeAtSite(testData.getInt("siteId")));
        runtimeState.portalJobsForSitePage = new PortalJobsForSitePage(getWebDriver()).get();
        runtimeState.portalJobsForSitePage.clickStartWorkButton(testData.getString("jobReference"));

        if ( runtimeState.portalJobsForSitePage.isModalDisplayed() ) {
            testData.put("modalMessage", runtimeState.portalJobsForSitePage.getModalText());
        }
    }

    public void stopWork() {
        runtimeState.portalJobsForSitePage = new PortalJobsForSitePage(getWebDriver()).get();
        runtimeState.portalJobsForSitePage.clickStopWorkButton(testData.getString("jobReference"));
        runtimeState.updateJobPage = new UpdateJobPage(getWebDriver()).get();
    }

    public void submit() {
        outputHelper.takeScreenshots();
        runtimeState.initialFundingRequestsApprovalPage.submit();
    }

    public void submitCreditNote() {
        runtimeState.enterCreditNoteDetailsPage.clickSubmitCreditNoteButton();
        if (getWebDriver().getCurrentUrl().contains("Portal/CreditNoteApproval/Approval")) {
            runtimeState.creditNoteApprovalPage = new CreditNoteApprovalPage(getWebDriver()).get();
        }
    }

    public void submitInvoice() {
        runtimeState.portalOrdersInvoicePage.submitInvoice();
        if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/InvoiceSubmitted")) {
            runtimeState.invoiceSubmittedPage = new InvoiceSubmittedPage(getWebDriver()).get();
        }
    }

    public void uploadFile() {
        if (getWebDriver().getCurrentUrl().contains("UploadCreditNoteDocument")) {
            runtimeState.uploadCreditNoteDocumentPage.clickUploadFile();
        }
    }

    public void view() {
        runtimeState.helpdeskIncidentTimelineTab = new HelpdeskIncidentTimelineTab(getWebDriver()).get();
        runtimeState.helpdeskIncidentUpdate = runtimeState.helpdeskIncidentTimelineTab.clickViewButton();
    }

    public void spokeToStore() throws Throwable {
        if (runtimeState.feedbackResponsePage.isJobTimelineDisplayed()) {
            Grid grid = runtimeState.feedbackResponsePage.getJobTimeLineGrid();
            runtimeState.scenario.write("Job Timeline pre-feedback: " + grid.getColumnText("Event").toString());
        } else {
            runtimeState.scenario.write("Job Timeline pre-feedback: Not displayed");
        }
        runtimeState.spokeToStoreModal = runtimeState.feedbackResponsePage.clickSpokeToStoreButton("Spoke to Store");
    }

    public void replyToStore() throws Throwable {
        if (runtimeState.feedbackResponsePage.isJobTimelineDisplayed()) {
            Grid grid = runtimeState.feedbackResponsePage.getJobTimeLineGrid();
            runtimeState.scenario.write("Job Timeline pre-feedback: " + grid.getColumnText("Event").toString());
        } else {
            runtimeState.scenario.write("Job Timeline pre-feedback: Not displayed");
        }
        runtimeState.replyToStoreModal = runtimeState.feedbackResponsePage.clickReplyToStoreButton("Reply to Store");
    }

    public void clickEditQuoteButton() {
        runtimeState.registerQuotePage = runtimeState.quoteQueryPage.clickEditQuoteButton();
    }

    public void clickEditButton() {
        if (getWebDriver().getCurrentUrl().contains("Portal/QuoteCreateEdit/Register")) {
            runtimeState.editQuoteDetailsPage = runtimeState.registerQuotePage.clickEditQuoteButton();
        }
    }

    public void clickUpdateSubmittedQuoteButton() {
        if (getWebDriver().getCurrentUrl().contains("Portal/QuoteCreateEdit/Register")) {
            runtimeState.quoteQueryPage = runtimeState.registerQuotePage.clickUpdateSubmittedQuoteButton();
        }
    }
}
