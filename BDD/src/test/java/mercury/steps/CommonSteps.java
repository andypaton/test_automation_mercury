package mercury.steps;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Constants.THREE_MINUTES;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.toCurrency;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.PortalNotificationsQueueDao;
import mercury.database.models.PortalNotificationsQueue;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.ButtonHelper;
import mercury.helpers.EmailHelper;
import mercury.helpers.ErrorHelper;
import mercury.helpers.FgasAdminHelper;
import mercury.helpers.FundingRequestHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.apihelper.ApiHelperHangfire;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.PopupAlert;
import mercury.pageobject.web.admin.AdminMenuPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.pageobject.web.portal.PortalSummaryPage;
import mercury.pageobject.web.portal.invoices.AllOrdersPage;
import mercury.pageobject.web.portal.invoices.InvoiceApprovalPage;
import mercury.pageobject.web.portal.invoices.InvoiceFinalApprovalPage;
import mercury.pageobject.web.portal.invoices.InvoicesAndCreditsAwaitingReviewPage;
import mercury.pageobject.web.portal.invoices.ManageInvoiceLinesModal;
import mercury.pageobject.web.portal.invoices.PortalOrdersInvoicePage;
import mercury.pageobject.web.portal.invoices.UploadInvoiceDocumentsPage;
import mercury.pageobject.web.portal.quotes.QuotesAwaitingReviewPage;
import mercury.runtime.RuntimeState;

public class CommonSteps {

    @Autowired private ApiHelperHangfire apiHelperHangfire;
    @Autowired private ButtonHelper buttonHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private EmailHelper emailHelper;
    @Autowired private ErrorHelper errorHelper;
    @Autowired private OutputHelper outputHelper;
    @Autowired private PortalNotificationsQueueDao portalNotificationsQueueDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private FgasAdminHelper fgasAdminHelper;
    @Autowired private FundingRequestHelper fundingRequestHelper;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private DbHelperSites dbHelperSites;

    private static final String QUOTE_APPROVAL_QUERY_REQUESTED_ALERT = "Please note that this quote is awaiting a resource query response. Passing an approval or raising a funding request will not be possible until all queries are returned.";


    @ContinueNextStepsOnException
    @Then("^a \"([^\"]*)\" alert is displayed$")
    public void a_alert_is_displayed(String alert) throws Throwable {
        if (getWebDriver().getCurrentUrl().contains("Helpdesk#!")) {
            if (alert.equalsIgnoreCase("Thank you, the file has been uploaded.")) {
                assertTrue("Alert message not displayed", runtimeState.helpdeskAddAttachmentsModal.isAlertTextDisplayed(alert));
            } else {
                assertTrue("Error message not displayed", runtimeState.helpdeskAddAttachmentsModal.isErrorTextDisplayed(alert));
            }
        } else if (getWebDriver().getCurrentUrl().contains("Account/Login")) {
            assertTrue("Alert message not displayed", runtimeState.loginPage.isIncorrectUsernameOrPassword());
        } else {
            switch (alert) {

            case "CAPEX - Urgent Critical":
                assertTrue("Expected alert is not displayed", runtimeState.helpdeskLogJobPage.isCapexUrgentCriticalAlertDisplayed());
                break;

            case "Quote - Query Response":
                assertTrue("Expected alert is not displayed", runtimeState.quotesManagersDecisionPage.isQuoteQueryResponseAlertDisplayed());
                assertEquals("Expected alert text", QUOTE_APPROVAL_QUERY_REQUESTED_ALERT, runtimeState.quotesManagersDecisionPage.getPageAlert());
                break;

            default:
                throw new Exception("Unexpected alert: " + alert);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" page is displayed$")
    public void the_page_is_displayed(String page) throws Throwable {
        switch (page) {

        case "invoice":
            runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();
            assertTrue("Unexpected page displayed", runtimeState.portalOrdersInvoicePage.isPageLoaded());
            break;
        case "Upload Invoice Documents":
            runtimeState.uploadInvoiceDocumentsPage = new UploadInvoiceDocumentsPage(getWebDriver()).get();
            assertTrue("Unexpected page displayed", runtimeState.uploadInvoiceDocumentsPage.isPageLoaded());
            break;

        case "Quotes Awaiting Review":
            runtimeState.quotesAwaitingReviewPage = new QuotesAwaitingReviewPage(getWebDriver()).get();
            assertTrue("Unexpected page displayed", runtimeState.quotesAwaitingReviewPage.isPageLoaded());
            break;

        case "Invoices & Credits Awaiting Review":
            runtimeState.invoicesAndCreditsAwaitingReviewPage = new InvoicesAndCreditsAwaitingReviewPage(getWebDriver()).get();
            assertTrue("Unexpected page displayed", runtimeState.invoicesAndCreditsAwaitingReviewPage.isPageLoaded());
            break;

        case "All Orders":
            runtimeState.allOrdersPage = new AllOrdersPage(getWebDriver()).get();
            assertTrue("Unexpected page displayed", runtimeState.allOrdersPage.isPageLoaded());
            break;

        case "Home":
            runtimeState.portalSummaryPage = new PortalSummaryPage(getWebDriver()).get();
            assertTrue("Unexpected page displayed", runtimeState.portalSummaryPage.isPageLoaded());
            break;

        default:
            throw new Exception("Unexpected page: " + page);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" modal is displayed$")
    public void the_modal_is_displayed(String modal) throws Throwable {
        switch (modal) {

        case "Manage Lines":
            runtimeState.manageInvoiceLinesModal = new ManageInvoiceLinesModal(getWebDriver()).get();
            assertTrue("Unexpected page displayed", runtimeState.manageInvoiceLinesModal.isPageLoaded());
            break;

        default:
            throw new Exception("Unexpected modal: " + modal);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" button is visible$")
    public void the_button_is_visible(String button) throws Throwable {
        switch (button) {

        case "consolidated invoice":
            // Check the consolidated invoice button is visible on the page
            assertTrue("Unexpected View ", runtimeState.ordersAwaitingInvoicePage.isConsolidatedInvoiceButtonVisible());
            break;

        default:
            throw new Exception("Unexpected button: " + button);
        }
    }

    @And("^the \"([^\"]*)\" button is clicked$")
    public void the_button_is_clicked(String button) throws Throwable {

        outputHelper.takeScreenshots();

        switch (button){

        case "Add": buttonHelper.add(); break;
        case "Add Labor Lines":
        case "Add Labour Lines": runtimeState.manageInvoiceLinesModal.clickLaborLines(); break;
        case "Add Materials Lines": runtimeState.manageInvoiceLinesModal.clickMaterialsLines(); break;
        case "Add new caller": buttonHelper.addNewCaller(); break;
        case "Add note": buttonHelper.addNote(); break;
        case "Approve": buttonHelper.approve(); break;
        case "Attach": buttonHelper.attach(); break;
        case "Attachment": buttonHelper.attachment(); break;

        case "Back": buttonHelper.back(); break;

        case "Call": buttonHelper.call(); break;
        case "Call Job Contact": buttonHelper.callJobContact(); break;
        case "Cancel": buttonHelper.cancel(); break;
        case "Cancel Job": buttonHelper.cancelJob(); break;
        case "Close": buttonHelper.close(); break;
        case "Confirm": buttonHelper.confirmStartWork(); break;
        case "Consolidated Invoice" : runtimeState.ordersAwaitingInvoicePage.clickConsolidatedInvoice(); break;
        case "Convert to quote job": buttonHelper.convertToQuoteJob(); break;
        case "Create linked quote job": buttonHelper.createLinkedQuoteJob(); break;
        case "Create New Incident": buttonHelper.createNewIncident(); break;
        case "Credit Note Lines": runtimeState.creditNoteLinesModal = runtimeState.creditNoteApprovalPage.clickCreditNoteLinesButton(); break;

        case "Delete": runtimeState.helpdeskAddAttachmentsModal.clickDeleteButton(); break;
        case "Don't Cancel": runtimeState.helpdeskTimelineTab = runtimeState.helpdeskJobCancelPanel.dontCancelJob(); break;

        case "Edit": buttonHelper.clickEditButton(); break;
        case "Edit Quote": buttonHelper.clickEditQuoteButton(); break;

        case "Find": buttonHelper.findAllJobs(); break;

        case "Link": buttonHelper.link(); break;
        case "Linked Incidents": buttonHelper.linkedIncidents(); break;
        case "Linked Jobs": buttonHelper.linkedJobs(); break;
        case "Log a job": runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton(); break;
        case "Log an incident": runtimeState.helpdeskLogAnIncidentPage = runtimeState.helpdeskSitePage.clickLogAnIncidentButton(); break;

        case "Next": buttonHelper.next(); break;
        case "New Chase": runtimeState.helpdeskCreateJobChasePanel = runtimeState.helpdeskJobChasePanel.clickAddChase(); break;

        case "Request Quote": buttonHelper.requestQuote(); break;
        case "Reject": runtimeState.rejectCreditNoteModal = runtimeState.creditNoteApprovalPage.clickRejectButton(); break;
        case "Reply to Store": buttonHelper.replyToStore(); break;

        case "Save": buttonHelper.save(); break;
        case "Save Changes" : buttonHelper.saveChanges(); break;
        case "Save only": runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskLogAnIncidentPage.clickSaveOnlyButton(); break;
        case "Send Escalations": runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskIncidentSendEscalation.clickSendEscalationsButton(); break;
        case "Send response": buttonHelper.sendResponse(); break;
        case "Split RFMs": buttonHelper.splitRFMs(); break;
        case "Spoke to Store": buttonHelper.spokeToStore(); break;
        case "Start Work": buttonHelper.startWork(); break;
        case "Stop Work": buttonHelper.stopWork(); break;
        case "Submit": buttonHelper.submit(); break;
        case "Submit Credit Note": buttonHelper.submitCreditNote(); break;
        case "Submit Invoice": buttonHelper.submitInvoice(); break;

        case "Update Follow Up": runtimeState.helpdeskIncidentEscalation = runtimeState.helpdeskIncidentFollowUpPage.clickUpdateFollowUpButton(); break;
        case "Upload File": buttonHelper.uploadFile(); break;
        case "Upload Invoice": runtimeState.uploadInvoiceDocumentsPage = runtimeState.portalOrdersInvoicePage.clickUploadInvoice(); break;
        case "Update": runtimeState.onCallSchedulerAdminPage.clickUpdateButtonForTheDay(testData.getString("dayOfTheWeek")); break;
        case "Update Submitted Quote": buttonHelper.clickUpdateSubmittedQuoteButton(); break;

        case "View": buttonHelper.view(); break;

        default:
            throw new Exception("Unexpected button: " + button);
        }
    }

    @And("^the \"([^\"]*)\" action is selected")
    public void the_action_is_selected(String action) throws Throwable {

        outputHelper.takeScreenshots();

        if (!getWebDriver().getCurrentUrl().contains("search/resource")) {
            // "Add or override absence" does not need the helpdesk job page.
            runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
        }

        switch(action) {

        case "Accept job":
            runtimeState.helpdeskAcceptJobPanel = runtimeState.helpdeskManageResourcesPanel.selectAcceptJobAction();
            break;

        case "Additional resource accepts the job":
            runtimeState.helpdeskAcceptJobPanel = runtimeState.helpdeskManageResourcesPanel.additionalResourceSelectAcceptJobAction(normalize(testData.getString("additionalResourceName")));
            break;

        case "Additional resource funding requests":
            runtimeState.helpdeskFundingRequestsPanel=runtimeState.helpdeskManageResourcesPanel.selectAdditionalResourceFundingRequestAction(testData.getString("additionalResourceName"));
            break;

        case "Advise Eta":
            runtimeState.helpdeskAdviseEtaPanel = runtimeState.helpdeskJobPage.selectAdviseEtaAction();
            break;

        case "Advise Removal":
            runtimeState.helpdeskAdviseRemovalPanel = runtimeState.helpdeskManageResourcesPanel.selectAdviseRemovalAction();
            break;

        case "Additional Resource Advise Removal":
            runtimeState.helpdeskAdviseRemovalPanel = runtimeState.helpdeskManageResourcesPanel.additionalResourceSelectAdviseRemovalAction(testData.getString("additionalResourceName"));
            break;

        case "Cancel Job":
            testData.put("origResourceStatus", runtimeState.helpdeskJobPage.getResourceStatus());
            testData.put("origClientStatus", runtimeState.helpdeskJobPage.getClientStatus());
            runtimeState.helpdeskJobCancelPanel = runtimeState.helpdeskJobPage.selectCancelJobAction();
            break;

        case "Chase":
            runtimeState.helpdeskJobChasePanel = runtimeState.helpdeskJobPage.selectChaseAction();
            break;

        case "Edit":
            runtimeState.helpdeskLogJobPage = runtimeState.helpdeskJobPage.selectEditAction();
            break;

        case "ETA":
            String resourceName = runtimeState.helpdeskManageResourcesPanel.getResourceNameWithActions();
            runtimeState.helpdeskResourceETAPanel = runtimeState.helpdeskManageResourcesPanel.selectEtaAction(resourceName);
            break;

        case "Funding requests":
            POHelper.refreshPage();
            runtimeState.helpdeskFundingRequestsPanel=runtimeState.helpdeskManageResourcesPanel.selectFundingRequestAction();
            break;

        case "Manage Resources":
            POHelper.refreshPage();
            runtimeState.helpdeskManageResourcesPanel= runtimeState.helpdeskJobPage.selectManageResourcesAction();
            break;

        case "Park Job":
            runtimeState.helpdeskParkJobPanel = runtimeState.helpdeskJobPage.selectParkJobAction();
            break;

        case "Quotes":
            runtimeState.helpdeskJobActionsPanel = runtimeState.helpdeskJobPage.selectQuotesAction();
            break;

        case "Remove Resource":
            runtimeState.helpdeskRemoveResourcePanel = runtimeState.helpdeskManageResourcesPanel.selectRemoveResourceAction();
            break;

        case "Remove Additional Resource":
            runtimeState.helpdeskRemoveResourcePanel = runtimeState.helpdeskManageResourcesPanel.selectRemoveAdditionalResourceAction(testData.getString("additionalResourceName"));
            break;

        case "Reopen Job":
            runtimeState.reopenJobModal = runtimeState.helpdeskJobPage.selectReopenJob();
            break;

        case "Transfer Work":
            runtimeState.helpdeskTransferWorkPanel = runtimeState.helpdeskManageResourcesPanel.selectTransferWorkAction();
            break;

        case "UnPark Job":
            runtimeState.helpdeskJobPage.selectUnParkJobAction();
            break;

        case "Add or override absence":
            runtimeState.helpdeskAddOrOverrideAbsencePanel = runtimeState.helpdeskResourcePage.selectAddOrOverrideAbsenceAction();
            break;
            
        case "Confirm Warranty":
            runtimeState.confirmWarrantyPanel = runtimeState.helpdeskJobPage.selectConfirmWarranty();
            break;

        default:
            throw new Exception("Unexpected action: " + action);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" action (?:is not now|should not be) available$")
    public void the_action_should_not_be_available(String action) throws Exception {
        switch (action) {
        case "Acknowledge ETA" :
            assertFalse("ETA Acknowledged button is available", runtimeState.helpdeskManageResourcesPanel.isETAAcknowledgedButtonEnabled() );
            break;
        case "Remove Resource" :
            assertFalse("Remove Resource button is available", runtimeState.helpdeskManageResourcesPanel.isRemoveResourceButtonEnabled(testData.getString("additionalResourceName")) );
            break;
        case "Manage Resources" :
            assertTrue("Manage Resources action is available", runtimeState.helpdeskJobPage.isJobActionDisabled(action));
            break;
        case "Funding requests":
            assertTrue("Funding requests action is available", runtimeState.helpdeskManageResourcesPanel.isActionUnavailable(action));
            break;
        default :
            throw new Exception("Action is not available " + action);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" action should (?:be|now be) available$")
    public void the_action_should_be_available(String action) throws Exception {
        switch (action) {
        case "Acknowledge ETA" :
            assertTrue("ETA Acknowledged button is not available", runtimeState.helpdeskManageResourcesPanel.isETAAcknowledgedButtonEnabled() );
            break;
        default :
            throw new Exception("Action is not available " + action);
        }
    }

    @Then("^the convert to quote screen panel will not be visible$")
    public void the_convert_to_quote_screen_panel_will_not_be_visible() throws Throwable {
        runtimeState.scenario.write("Asserting that Convert to quote screen panel is not visible");
        assertFalse("Convert to quote screen panel is displayed!", runtimeState.helpdeskJobActionsPanel.isJobActionsPanelDisplayed());
        outputHelper.takeScreenshots();
    }

    @Then("^the convert to quote job button is not displayed$")
    public void the_convert_to_quote_job_button_is_not_displayed() throws Throwable {
        runtimeState.scenario.write("Asserting that Convert to quote job button is not displayed");
        assertFalse("Convert to quote job button is displayed!", runtimeState.helpdeskJobActionsPanel.isConvertToQuoteJobButtonDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the Job \"([^\"]*)\" added to the \"([^\"]*)\" monitor$")
    public void the_job_is_added_to_the_monitor(String is, String monitor) throws Exception {
        apiHelperHangfire.rebuildAllMonitorCountData();
        boolean isAdded = is.equals("is");
        int jobReference = testData.getInt("jobReference");

        switch(monitor) {
        case "ETA greater than response/repair time":
            if (isAdded) {
                await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobEtaExceedsSlaInHours", jobReference));
                assertTrue("Job is not on the " + monitor + " monitor", dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobEtaExceedsSlaInHours", jobReference));
            } else {
                assertFalse("Job is not on the " + monitor + " monitor", dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobEtaExceedsSlaInHours", jobReference));
            }
            break;
        case "Chase":
            if (isAdded) {
                await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobChaseLoggedBase", jobReference));
                assertTrue("Job is not on the " + monitor + " monitor", dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobChaseLoggedBase", jobReference));
            } else {
                assertFalse("Job is not on the " + monitor + " monitor", dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobChaseLoggedBase", jobReference));
            }
            break;
        default:
            throw new Exception("Unexpected action: " + monitor);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Job (?:is|has been) removed from the \"([^\"]*)\" monitor$")
    public void the_job_is_removed_from_the_monitor(String monitor) throws Exception {
        apiHelperHangfire.rebuildAllMonitorCountData();
        int jobReference = testData.getInt("jobReference");

        switch(monitor) {
        case "ETA greater than response/repair time":
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> !dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobEtaExceedsSlaInHours", jobReference));
            assertTrue("Job is on the " + monitor + " monitor", !dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobEtaExceedsSlaInHours", jobReference));
            break;
        case "ETA expired":
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> !dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorFocusJobETAExpired", jobReference));
            assertTrue("Job is on the " + monitor + " monitor", !dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorFocusJobETAExpired", jobReference));
            break;
        case "ETA not Advised to site":
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> !dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobETANotAdvised", jobReference));
            assertTrue("Job is on the " + monitor + " monitor", !dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobETANotAdvised", jobReference));
            break;
        default:
            throw new Exception("Unexpected action: " + monitor);
        }
    }

    @ContinueNextStepsOnException
    @Then("^an email is sent for \"([^\"]*)\"$")
    public void an_email_is_sent_for(String title) throws Throwable {

        int jobReference = testData.getInt("jobReference");

        // to be fleshed out - assert more fields!
        switch(title) {
        case "Awaiting Parts Notification":
            title = title + " - Job Number " + jobReference;
            emailHelper.verifyAwaitingPartsEmail(title, jobReference);
            break;

        case "Appliance Information Is Missing":
        case "Full Charge is Different from Asset":
        case "Full Charge and Gas Type is Different from Asset":
        case "Refrigerant Type is Different from Asset":
            emailHelper.verifyFGasEmail(title, jobReference);
            break;

        case "City Help Desk - Job Cost Exceeds":
        case "City Help Desk":
            String application = "Mercury2";
            if (LOCALE.equals("en-GB")) {
                String environment = propertyHelper.getEnv().contains("STG") ? "Staging" : propertyHelper.getEnv().contains("UAT") ? "UAT" : "Test";
                application = "Rainbow" + environment;
            }

            if ("City Help Desk".equalsIgnoreCase(title)) {
                title = application + " - " + title + " - " + jobReference + ", (" + dbHelperSites.getSiteName(testData.getInt("siteId")) + ")";
            } else {
                title = application + " - " + title + " " + toCurrency(testData.getBigDecimal("financeNotificationValue"));
            }
            emailHelper.verifyUpliftEmail(title, jobReference);
            break;
        case "Mercury Helpdesk - Parts Request Rejected":
            title = title + " - Job Number " + jobReference;
            emailHelper.verifyPartsRejectedEmail(title, jobReference);
            break;

        case "Resource Returning Notification - Awaiting Parts":
            title = title + " - Job Number " + jobReference;
            emailHelper.verifyReturningAwaitingPartsEmail(title, jobReference);
            break;

        default:
            List<PortalNotificationsQueue> emails = portalNotificationsQueueDao.getEmailsByJobReference(jobReference, title);

            assertNotNull("No email notifications found for jobReference: " + jobReference, emails);
        }
    }

    @And("the Save is confirmed")
    public void the_Save_is_confirmed() {
        runtimeState.popupAlert.ok();
    }

    @ContinueNextStepsOnException
    @Then("^the following error is displayed: \"([^\"]*)\"$")
    public void the_error_is_displayed(String message) throws Exception {
        // although using the login page to retrieve - these will return if visible from any page!
        message = errorHelper.generateErrorMessage(message);
        runtimeState.scenario.write("Asserting error is displayed: " + message);
        assertThat(runtimeState.loginPage.getErrors(), hasItem(containsString(message)));
    }

    @Then("^the following invoice tax error message is displayed: \"([^\"]*)\"$")
    public void the_following_invoice_tax_error_message_is_displayed(String message) throws Exception {
        message = errorHelper.generateErrorMessage(message);
        runtimeState.scenario.write("Asserting error is displayed: " + message);
        List<String> errorMessageList = new ArrayList<String>();
        errorMessageList = runtimeState.loginPage.getErrors();
        for (String i : errorMessageList) {
            assertTrue("Expected: " + message, i.contains(message));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the following toast message is displayed: \"([^\"]*)\"$")
    public void the_toast_message_is_displayed(String message) throws Exception {
        String toast = runtimeState.loginPage.getToastMessage();
        runtimeState.scenario.write("Toast: " + toast);
        assertTrue("Expected: " + message, toast.equals(message));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the message \"([^\"]*)\" is displayed$")
    public void the_information_message_is_displayed(String message) throws Exception {
        runtimeState.scenario.write("Asserting message is displayed: " + message);
        assertThat(runtimeState.loginPage.getInformationMessage(), containsString(message));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the following alert is displayed: \"([^\"]*)\"$")
    public void the_alert_is_displayed(String message) throws Exception {
        // although using the login page to retrieve - these will return if visible from any page!
        List<String> alerts = runtimeState.loginPage.getAlerts();
        runtimeState.scenario.write("Alerts: " + alerts.toString());
        assertThat(alerts, hasItem(containsString(message)));
    }

    @ContinueNextStepsOnException
    @Then("^(?:the|a) \"([^\"]*)\" popup alert is displayed$")
    public void the_following_alert_is_displayed(String expected) throws Exception {
        runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
        assertTrue("Expected pop up to display: " + expected, runtimeState.popupAlert.containsText(expected));
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" popup alert is displayed with text \"([^\"]*)\"$")
    public void popup_alert_is_displayed_with_text(String title, String description) throws Exception {
        runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
        assertTrue("Expected pop up to display: " + title + "\n" + description, runtimeState.popupAlert.containsText(title, description));
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" popup alert is confirmed$")
    public void the_popup_alert_is_confirmed(String expected) throws Exception {
        the_following_alert_is_displayed(expected);
        runtimeState.popupAlert.ok();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" popup alert is cancelled$")
    public void the_error_message_is_cancelled(String expected) throws Exception {
        the_following_alert_is_displayed(expected);
        runtimeState.popupAlert.cancel();
    }

    @And("^the \"([^\"]*)\" radio button is clicked$")
    public void the_radio_button_is_clicked(String radionButton) throws Throwable {

        outputHelper.takeScreenshots();

        switch (radionButton) {
        case "Respond to query": buttonHelper.respondToQuery(); break;
        case "Edit Quote Job Recommendations": buttonHelper.editQuoteJobRecommendations(); break;
        case "Confirm Quote Job Rejection" : buttonHelper.confirmQuoteJobRejection();break;

        default:
            throw new Exception("Unexpected radio button: " + radionButton);
        }
    }

    /**
     * @param timeoutName : This should be the name of the global constant
     * @param time : long in seconds
     * @throws Throwable
     */
    @Given("^the \"([^\"]*)\" timeout is set to \"([^\"]*)\" seconds$")
    public void the_timeout_is_set_to_seconds(String timeoutName, long time) throws Throwable {
        switch (timeoutName) {
        case "PAGELOAD_TIMEOUT": getWebDriver().manage().timeouts().pageLoadTimeout(time, TimeUnit.SECONDS); break;
        case "IMPLICIT_WAIT":  getWebDriver().manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS); break;
        case "SCRIPT_TIMEOUT":  getWebDriver().manage().timeouts().setScriptTimeout(time, TimeUnit.SECONDS); break;
        default:
            throw new PendingException("Cannot find expected wait");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the job details are displayed$")
    public void the_job_details_are_displayed() throws Throwable {
        String currentURL = getWebDriver().getCurrentUrl();

        if (currentURL.contains("QuotesAwaitingApproval") || currentURL.contains("JobsAwaitingInitialFundingRequest")) {
            fundingRequestHelper.assertJobDetailsDisplayed();

        } else if (currentURL.contains("RefrigerantGasUsage/EditGasUsage")){

            // Refrigerant Gas
            if ("2019".equals(testData.getString("fgasRegulations"))) {
                fgasAdminHelper.jobDetailsDisplayedForUsRegulations();
            } else {
                fgasAdminHelper.jobDetailsDisplayedForUkRegulations();
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the job details are displayed without dates$")
    public void the_job_details_are_displayed_without_dates() throws Throwable {
        testData.put("skipCheckDates", true);
        the_job_details_are_displayed();
    }

    @Then("^Documents to Check are displayed$")
    public void Documents_to_Check_are_displayed() {
        List<String> documentsToCheck = null;
        if (getWebDriver().getCurrentUrl().contains("/Portal/InvoiceFinalApproval/Approval")) {
            documentsToCheck = runtimeState.invoiceFinalApprovalPage.getDocumentsToCheck();
        } else {
            documentsToCheck = runtimeState.invoiceApprovalPage.getDocumentsToCheck();
        }
        assertNotNull("Documents to Check not displayed", documentsToCheck);
        runtimeState.scenario.write("Documents to Check: " + documentsToCheck.toString());
    }

    @When("^the View Invoice Lines button is clicked$")
    public void the_View_Invoice_Lines_button_on_the_page_is_clicked() throws Throwable {
        if (getWebDriver().getCurrentUrl().contains("/Portal/InvoiceFinalApproval/Approval")) {
            runtimeState.invoiceFinalApprovalPage.selectViewInvoiceLines();
        } else {
            runtimeState.invoiceApprovalPage.selectViewInvoiceLines();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Approve button is not visible until all documents have been checked$")
    public void the_Approve_button_is_not_visible_until_all_documents_have_been_checked() throws Throwable {
        List<String> uncheckedDocuments = null;
        if (getWebDriver().getCurrentUrl().contains("/Portal/InvoiceApproval/Approval")) {
            if (runtimeState.invoiceApprovalPage == null) runtimeState.invoiceApprovalPage = new InvoiceApprovalPage(getWebDriver()).get();
            uncheckedDocuments = runtimeState.invoiceApprovalPage.getUncheckedDocuments();
            if (uncheckedDocuments.size() > 0) {
                runtimeState.scenario.write("Unchecked documents: " + uncheckedDocuments.toString());
                runtimeState.scenario.write("Verifying Approve button is disabled");
                assertFalse("Unexpected button visible: Approve", runtimeState.invoiceApprovalPage.isApprovable());

                for (String doc : uncheckedDocuments) {
                    runtimeState.invoiceApprovalPage.selectDoc(doc);
                    getWebDriver().navigate().back();
                    POHelper.waitForAngularRequestsToFinish();
                }
            }
            runtimeState.scenario.write("All documents checked. Verifying Approve button is enabled");
            assertTrue("Unexpected button NOT visible: Approve", runtimeState.invoiceApprovalPage.isApprovable());

        } else {
            if (runtimeState.invoiceFinalApprovalPage == null) runtimeState.invoiceFinalApprovalPage = new InvoiceFinalApprovalPage(getWebDriver()).get();
            uncheckedDocuments = runtimeState.invoiceFinalApprovalPage.getUncheckedDocuments();
            if (uncheckedDocuments.size() > 0) {
                runtimeState.scenario.write("Unchecked documents: " + uncheckedDocuments.toString());
                runtimeState.scenario.write("Verifying Approve button is disabled");
                assertFalse("Unexpected button visible: Approve", runtimeState.invoiceFinalApprovalPage.isApprovable());

                for (String doc : uncheckedDocuments) {
                    runtimeState.invoiceFinalApprovalPage.selectDoc(doc);
                    getWebDriver().navigate().back();
                    POHelper.waitForAngularRequestsToFinish();
                }
            }
            runtimeState.scenario.write("All documents checked. Verifying Approve button is enabled");
            assertTrue("Unexpected button NOT visible: Approve", runtimeState.invoiceFinalApprovalPage.isApprovable());
        }
        outputHelper.takeScreenshots();
    }

    @Then("^the Approve button is not visible until 'Next >>' is clicked$")
    public void the_Approve_button_is_not_visible_until_Next_is_clicked() {
        if (getWebDriver().getCurrentUrl().contains("/Portal/InvoiceFinalApproval/Approval")) {
            assertFalse("Unexpected button visible: Approve", runtimeState.invoiceFinalApprovalPage.isApprovable());
            runtimeState.invoiceFinalApprovalPage.clickNext();
            assertTrue("Unexpected button NOT visible: Approve", runtimeState.invoiceFinalApprovalPage.isApprovable());
        } else {
            assertFalse("Unexpected button visible: Approve", runtimeState.invoiceApprovalPage.isApprovable());
            runtimeState.invoiceApprovalPage.clickNext();
            assertTrue("Unexpected button NOT visible: Approve", runtimeState.invoiceApprovalPage.isApprovable());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" button is disabled$")
    public void the_button_is_disabled(String button) throws Exception {
        switch (button) {
        case "Create Chase":
            assertFalse("Button is enabled", runtimeState.helpdeskCreateJobChasePanel.isCreateChaseEnabled());
            break;

        case "Save":
            assertTrue(runtimeState.helpdeskLogJobPage.isButtonDisabled("Save"));
            break;

        case "Save and identify as caller":
            if ( testData.getBoolean("addCaller") ) {
                runtimeState.helpdeskAddCallerPage.isSaveEnabled();

            } else if ( testData.getBoolean("newCaller")) {
                assertFalse("Button is enabled", runtimeState.newCallerPage.isSaveEnabled());
            }
            break;

        case "Update and identify as caller":
            assertFalse("Button is enabled", runtimeState.editCallerModal.isUpdateEnabled());
            break;

        case "Save Changes":
            assertFalse("Button is enabled", runtimeState.onCallSchedulerAdminPage.isSaveChangesEnabled());
            break;

        default:
            throw new Exception("Unknown button: " + button);
        }
    }

    @When("^an invalid telephone number is entered$")
    public void an_invalid_telephone_number_is_entered() {
        String phoneNumber = DataGenerator.generateInvalidPhoneNumber();
        runtimeState.scenario.write("phone number: " + phoneNumber);

        if ( testData.getBoolean("addCaller") ) {
            runtimeState.helpdeskAddCallerPage.enterTelephone(phoneNumber);

        } else if ( testData.getBoolean("newCaller")) {
            runtimeState.newCallerPage.enterTelephone(phoneNumber);

        } else if ( testData.getBoolean("editCaller")) {
            runtimeState.editCallerModal.enterTelephone(phoneNumber);

        } else if ( testData.getBoolean("newChase")) {
            runtimeState.helpdeskCreateJobChasePanel.setChaseRandomTelephoneNumber(phoneNumber);
        }
    }

    public static void assertDataFound(Object o) {
        if (o == null) {
            throw new PendingException("No test data found");
        }
    }

    public static void assertDataFound(String message, Object o) {
        if (o == null) {
            throw new PendingException(message);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the mercury navigation menu in the admin \"([^\"]*)\" page displays options correctly$")
    public void the_mercury_navigation_menu_in_the_admin_page_displays_options_correctly(String adminPage) throws Throwable {
        runtimeState.adminMenuPage = new AdminMenuPage(getWebDriver()).get();
        String[] expectedNavigationMenuOptions = null;
        List<String> actualNavigationMenuOptions = runtimeState.adminMenuPage.getNavigationMenuOptions();

        switch (adminPage) {
        case "Jobs":
            String expectedJobPageNavigationMenuOptions[] = { "Job Status Admin", "On Call Scheduler" };
            expectedNavigationMenuOptions = expectedJobPageNavigationMenuOptions;
            break;

        case "Sites":
            String expectedSitesPageNavigationMenuOptions[] = { "Sites", "Locations", "Site Types/Locations Mapping" };
            expectedNavigationMenuOptions = expectedSitesPageNavigationMenuOptions;
            break;

        default:
            runtimeState.scenario.write("Unexpected admin page : " + adminPage);
            break;
        }

        List<String> navigationMenuOptions = Arrays.asList(expectedNavigationMenuOptions);
        runtimeState.scenario.write("Asserting Navigation Menu options in the " + adminPage + " page : " + Arrays.toString(expectedNavigationMenuOptions));
        assertTrue("Unexpected Navigation menu options, expected " + Arrays.toString(expectedNavigationMenuOptions) + " but found " + actualNavigationMenuOptions, actualNavigationMenuOptions.containsAll(navigationMenuOptions));
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" menu option in the admin \"([^\"]*)\" page is highlighted$")
    public void the_menu_option_in_the_admin_page_is_highlighted(String menuOption, String adminPage) throws Throwable {
        runtimeState.scenario.write("Asseting '" + menuOption + "' menu item is highlighted by default in " + adminPage + " page.");
        assertEquals(menuOption + " is not highlighted ", menuOption, runtimeState.adminMenuPage.getHighlightedNavigationMenuOption());
    }

}
