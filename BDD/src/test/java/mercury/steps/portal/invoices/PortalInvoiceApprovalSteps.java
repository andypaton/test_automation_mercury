package mercury.steps.portal.invoices;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.applicationinsights.web.dependencies.apachecommons.lang3.RandomUtils;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.LoginLogoutHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.gridV3.Grid;
import mercury.pageobject.web.portal.invoices.InvoiceApprovalPage;
import mercury.pageobject.web.portal.invoices.InvoiceFinalApprovalPage;
import mercury.pageobject.web.portal.invoices.InvoiceLinesModal;
import mercury.pageobject.web.portal.invoices.InvoiceTab;
import mercury.pageobject.web.portal.invoices.RejectInvoiceModalPage;
import mercury.pageobject.web.portal.invoices.UpliftsTab;
import mercury.runtime.RuntimeState;
import mercury.steps.LoginSteps;
import mercury.steps.portal.MenuSteps;
import mercury.steps.portal.PortalCommon;

public class PortalInvoiceApprovalSteps {
    private static final Logger logger = LogManager.getLogger();

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TzHelper tzHelper;
    @Autowired private LoginSteps loginSteps;
    @Autowired private LoginLogoutHelper loginLogoutHelper;
    @Autowired private MenuSteps menuSteps;
    @Autowired private PortalCommon portalCommon;


    @ContinueNextStepsOnException
    @Then("^the user is taken to the invoice approval page$")
    public void the_user_is_taken_to_the_invoice_approval_page() throws Throwable {
        runtimeState.invoiceApprovalPage = new InvoiceApprovalPage(getWebDriver()).get();
        assertEquals("Unexpected Page Title", "Invoice Approval", runtimeState.invoiceApprovalPage.getInvoiceApprovalHeader());
        assertEquals("Unexpected Page Sub Header", "Documents to Check", runtimeState.invoiceApprovalPage.getDocumentsToCheckHeader());
        assertTrue("Document view not visible", runtimeState.invoiceApprovalPage.isDocumentViewVisible());
        outputHelper.takeScreenshots();

        String budgetType = runtimeState.invoiceApprovalPage.getInvoiceBudget();
        testData.addStringTag("budgetType", budgetType);
    }

    @And("^the user rejects an invoice$")
    public void the_user_is_able_to_reject_the_invoice() throws Throwable {
        the_user_selects_reject_on_the_invoice();
        the_reject_invoice_modal_is_displayed();
        the_user_has_option_to_cancel_the_rejection();
        runtimeState.rejectInvoiceModalPage.selectRejectReasonListBox();
        outputHelper.takeScreenshots();
        String notes = "Please enter some notes to verify the database update " + DateHelper.dateAsString(new Date());
        runtimeState.rejectInvoiceModalPage.enterRandomRejectionNote(notes);
        outputHelper.takeScreenshots();
        runtimeState.rejectInvoiceModalPage.selectSaveButton();
        outputHelper.takeScreenshots();
    }

    @And("^the user selects reject on the invoice$")
    public void the_user_selects_reject_on_the_invoice() throws Throwable {
        runtimeState.invoiceApprovalPage.selectReject();
        outputHelper.takeScreenshots();
    }

    @And("^the user selects reject on the invoice awaiting final approval$")
    public void the_user_selects_reject_on_the_invoice_awaiting_final_approval() throws Throwable {
        runtimeState.invoiceFinalApprovalPage.reject();
        outputHelper.takeScreenshots();
    }

    @And("^the Invoice Final Approval is rejected$")
    public void the_Invoice_Final_Approval_is_rejected() throws Throwable {
        if (runtimeState.invoiceFinalApprovalPage == null) runtimeState.invoiceFinalApprovalPage = new InvoiceFinalApprovalPage(getWebDriver()).get();
        runtimeState.invoiceFinalApprovalPage.reject();
        outputHelper.takeScreenshots();

        the_reject_invoice_modal_is_displayed();
        the_rejection_reason_list_is_validated();
        runtimeState.rejectInvoiceModalPage.selectRejectReasonListBox();
        outputHelper.takeScreenshots();
        String notes = "Please enter some notes to verify the database update " + DateHelper.dateAsString(new Date());
        runtimeState.rejectInvoiceModalPage.enterRandomRejectionNote(notes);
        outputHelper.takeScreenshots();
        runtimeState.rejectInvoiceModalPage.selectSaveButton();
        outputHelper.takeScreenshots();
    }

    @And("^the reject invoice modal is displayed$")
    public void the_reject_invoice_modal_is_displayed() throws Throwable {
        runtimeState.rejectInvoiceModalPage = new RejectInvoiceModalPage(getWebDriver()).get();
        assertEquals("Unexpected modal header", "Reject Invoice", runtimeState.rejectInvoiceModalPage.getRejectInvoiceHeader());
        assertEquals("Unexpected modal label", "Reason", runtimeState.rejectInvoiceModalPage.getRejectReasonLabel());
        assertEquals("Unexpected Modal label", "Notes", runtimeState.rejectInvoiceModalPage.getRejectNotesLabel());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the invoice status is set to ((?:Rejected|Awaiting Approval))$")
    public void the_invoice_status_is_set_to_rejected(String status) throws Throwable {
        int invoiceStatusId = dbHelperInvoices.getInvoiceStateIdForInvoice(String.valueOf(testData.getString("invoiceNumber")));
        switch (status) {
        case "rejected":
            assertEquals("Unexpected Invoice status Id", 6, invoiceStatusId);
            break;
        case "Awaiting Approval":
            assertEquals("Unexpected Invoice status Id", 3, invoiceStatusId);
            break;
        }
    }

    @And("^the user checks the documents attached and selects approve$")
    public void the_user_checks_the_documents_attached_and_select_approve() throws Throwable {
        if (runtimeState.invoiceApprovalPage.isInvoiceDocumentLinkVisible() && runtimeState.invoiceApprovalPage.isJobSheetDocumentLinkVisible()) {
            if (runtimeState.invoiceApprovalPage.getInvoiceDocumentChecked().equalsIgnoreCase("Invoice")) {
                runtimeState.invoiceApprovalPage.selectJobSheetDocumentLink();
            } else if (runtimeState.invoiceApprovalPage.getInvoiceDocumentChecked().equalsIgnoreCase("Job Sheet")) {
                runtimeState.invoiceApprovalPage.selectInvoiceDocumentLink();
            } else {
                throw new Exception("Unexpected document");
            }
            outputHelper.takeScreenshots();
            runtimeState.approveInvoiceModalPage = runtimeState.invoiceApprovalPage.selectApprove();
            outputHelper.takeScreenshots();
        } else if (runtimeState.invoiceApprovalPage.isInvoiceDocumentLinkVisible() || runtimeState.invoiceApprovalPage.isJobSheetDocumentLinkVisible()) {
            runtimeState.approveInvoiceModalPage = runtimeState.invoiceApprovalPage.selectApprove();
            outputHelper.takeScreenshots();
        } else {
            throw new Exception("No documents to view");
        }
    }

    @And("^the user checks the documents attached and selects approve on the Invoice Final Approval page$")
    public void the_user_checks_the_documents_attached_and_selects_approve_on_the_Invoice_Final_Approval_page() throws Throwable {
        if (runtimeState.invoiceFinalApprovalPage.isInvoiceDocumentLinkVisible() && runtimeState.invoiceFinalApprovalPage.isJobSheetDocumentLinkVisible()) {
            if (runtimeState.invoiceFinalApprovalPage.getInvoiceDocumentChecked().equalsIgnoreCase("Invoice")) {
                runtimeState.invoiceFinalApprovalPage.selectJobSheetDocumentLink();
            } else if (runtimeState.invoiceFinalApprovalPage.getInvoiceDocumentChecked().equalsIgnoreCase("Job Sheet")) {
                runtimeState.invoiceFinalApprovalPage.selectInvoiceDocumentLink();
            } else {
                throw new Exception("Unexpected document");
            }
            outputHelper.takeScreenshots();
            runtimeState.approveInvoiceModalPage = runtimeState.invoiceFinalApprovalPage.selectApprove();
            outputHelper.takeScreenshots();
        } else if (runtimeState.invoiceFinalApprovalPage.isInvoiceDocumentLinkVisible() || runtimeState.invoiceFinalApprovalPage.isJobSheetDocumentLinkVisible()) {
            runtimeState.approveInvoiceModalPage = runtimeState.invoiceFinalApprovalPage.selectApprove();
            outputHelper.takeScreenshots();
        } else {
            throw new Exception("No documents to view");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the funding route is displayed at the top right hand corner of the grid$")
    public void the_budget_route_is_displayed_on_the_grid() throws Throwable {
        String invoiceNum = testData.getString("invoiceNumber");
        String expectedBudgetRoute = dbHelperInvoices.getBudgetRouteOfInvoiceAwaitingInitialApproval(invoiceNum);
        if (runtimeState.invoiceApprovalPage == null) runtimeState.invoiceApprovalPage = new InvoiceApprovalPage(getWebDriver()).get();
        String actualBudgetRoute = runtimeState.invoiceApprovalPage.getInvoiceBudget();
        try {
            assertEquals("Unexpected Budget Route! ", expectedBudgetRoute, actualBudgetRoute);
        } catch (AssertionError e) {
            assertEquals("Unexpected Budget Route! ", dbHelperInvoices.getAliasFundingRoute(expectedBudgetRoute), actualBudgetRoute);
        }
        runtimeState.scenario.write("Budget Route is displayed on the grid and is : " + expectedBudgetRoute);
    }

    @And("^the approve invoice modal is displayed$")
    public void the_approve_invoice_modal_is_displayed() throws Throwable {
        assertEquals("Unexpected modal header", "Approve Invoice", runtimeState.approveInvoiceModalPage.getApproveInvoiceModalHeader());
        assertEquals("Unexpected modal label", "Selected Budget", runtimeState.approveInvoiceModalPage.getSelectedBudgetLabel());
        assertEquals("Unexpected modal label", "Does this relate to extreme weather conditions?", runtimeState.approveInvoiceModalPage.getRelatedWeatherConditionLabel());
        outputHelper.takeScreenshots();
    }

    @And("^the user selects \"([^\"]*)\" to weather conditions$")
    public void the_user_selects_weather_condition_to_weather_conditions(String weatherCondition) throws Throwable {
        if ("Extreme".equalsIgnoreCase(weatherCondition)) {
            runtimeState.approveInvoiceModalPage.selectYesRadioRelatedWeather();
        } else if ("Non Extreme".equalsIgnoreCase(weatherCondition)) {
            runtimeState.approveInvoiceModalPage.selectNoRadioRelatedWeather();
        } else {
            throw new Exception("Unexpected input");
        }
        outputHelper.takeScreenshots();
    }

    @And("^the invoice funding approval Confirm button is clicked$")
    public void the_user_selects_the_confirm_button() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.approveInvoiceModalPage.confirm();
    }

    @And("^the invoice funding approval Back button is clicked$")
    public void the_user_selects_the_back_button() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.approveInvoiceModalPage.back();
    }

    @And("^the invoice status is set to approved$")
    public void the_invoice_status_is_set_to_approved() throws Throwable {
        runtimeState.scenario.write("Asserting that the order reference '" + testData.getString("orderRef") + "' is not present on the Invoices Awaiting Approval table.");
        int invoiceStatusId = dbHelperInvoices.getInvoiceStateIdForInvoice(testData.getString("invoiceNumber"));
        try {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> invoiceStatusId, anyOf(equalTo(5), equalTo(10)));
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new Exception("Unexpected invoice status Id - " + invoiceStatusId + " for invoice number: " + testData.getString("invoiceNumber"));
        }
    }

    @When("^the user approves an invoice with \"([^\"]*)\" weather conditions$")
    public void the_user_is_able_to_approve_an_invoice_with_weather_conditions(String weatherCondition) throws Throwable {
        the_user_checks_the_documents_attached_and_select_approve();
        the_approve_invoice_modal_is_displayed();
        the_user_selects_weather_condition_to_weather_conditions(weatherCondition);
        the_user_selects_the_confirm_button();
    }

    @When("^the invoice is approved$")
    public void the_invoice_is_approved() throws Throwable {
        the_budget_route_is_displayed_on_the_grid();
        the_user_checks_the_documents_attached_and_select_approve();
        the_approve_invoice_modal_is_displayed();
        the_Extreme_Weather_question_is_answered();
        the_user_selects_the_confirm_button();
        the_invoice_status_is_set_to_approved();
    }

    @When("^the invoice funding route is changed to \"([^\"]*)\" and the invoice is approved$")
    public void the_invoice_funding_route_is_changed_to_and_the_invoice_is_approved(String fundingRoute) throws Throwable {
        the_user_checks_the_documents_attached_and_select_approve();
        the_approve_invoice_modal_is_displayed();
        the_invoice_funding_route_is_changed_to(fundingRoute);
        the_reason_for_funding_route_change_is_entered();
        the_Extreme_Weather_question_is_answered();
        the_user_selects_the_confirm_button();
        the_invoice_status_is_set_to_approved();
    }

    @When("^the invoice funding route is changed to \"([^\"]*)\" and mandatory fields are answered$")
    public void the_invoice_funding_route_is_changed_to_and_mandatory_fields_are_answered(String fundingRoute) throws Throwable {
        the_user_checks_the_documents_attached_and_select_approve();
        the_approve_invoice_modal_is_displayed();
        the_invoice_funding_route_is_changed_to(fundingRoute);
        the_reason_for_funding_route_change_is_entered();
        the_Extreme_Weather_question_is_answered();
    }

    @And("^the user selects a new budget$")
    public void the_user_selects_a_new_budget() throws Throwable {
        runtimeState.approveInvoiceModalPage.selectBudgetListBox();
        runtimeState.approveInvoiceModalPage.selectBudgetType(dbHelperInvoices.getUnusedBudgetName(testData.getInt("jobReference")));
        if (runtimeState.approveInvoiceModalPage.isBudgetWarningMessageDisplayed()) {
            runtimeState.approveInvoiceModalPage.confirmBudgetWarningMessage();
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the user is able to update the budget type and approve an invoice with \"([^\"]*)\" weather conditions$")
    public void the_user_is_able_to_update_and_approve_an_invoice_with_weather_conditions(String weatherCondition) throws Throwable {
        the_user_checks_the_documents_attached_and_select_approve();
        the_approve_invoice_modal_is_displayed();
        the_user_selects_a_new_budget();
        the_user_enters_a_random_funding_route_change_note();
        the_user_selects_weather_condition_to_weather_conditions(weatherCondition);
        the_user_selects_the_confirm_button();
    }

    @And("^the user enters a random funding route change note$")
    public void the_user_enters_a_random_funding_route_change_note() throws Throwable {
        String notes = "Please enter some notes to verify the database update " + DateHelper.dateAsString(new Date());
        runtimeState.approveInvoiceModalPage.enterReason(notes);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the invoice is ((?:still|not)) displayed on the Invoices Awaiting Approval table$")
    public void the_invoice_is_displayed_on_the_Invoices_Awaiting_Approval_table(String option) throws Throwable {
        String orderReference = testData.getString("orderRef");
        runtimeState.invoicesAwaitingApprovalPage.searchInvoices(orderReference);
        if ("not".equals(option)) {
            assertFalse("Invoice is displayed on the grid!", runtimeState.invoicesAwaitingApprovalPage.isInvoiceDisplayed(orderReference));
        } else {
            assertTrue("Invoice not displayed on the grid! ", runtimeState.invoicesAwaitingApprovalPage.isInvoiceDisplayed(orderReference));

        }
    }

    @ContinueNextStepsOnException
    @Then("^the mandatory fields for the rejection form are validated$")
    public void the_mandatory_fields_for_the_rejection_form_are_validated() throws Throwable {
        runtimeState.rejectInvoiceModalPage = new RejectInvoiceModalPage(getWebDriver()).get();
        runtimeState.rejectInvoiceModalPage.selectSaveButton();
        outputHelper.takeScreenshots();
        assertEquals("Unexpected validation message for reject reason", "This field is required.", runtimeState.rejectInvoiceModalPage.getRejectReasonValidationMessage());
        assertEquals("Unexpected validation message for notes", "This field is required.", runtimeState.rejectInvoiceModalPage.getRejectNotesValidationMessage());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the user has option to cancel the rejection$")
    public void the_user_has_option_to_cancel_the_rejection() throws Throwable {
        runtimeState.rejectInvoiceModalPage = new RejectInvoiceModalPage(getWebDriver()).get();
        runtimeState.scenario.write("Asserting that the user has the option to cancel the invoice rejection. ");
        assertTrue("The Close button is not displayed on the Reject Invoice modal", runtimeState.rejectInvoiceModalPage.isCloseButtonDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the Save button on the reject invoice modal is disabled$")
    public void the_Save_button_on_rejection_modal_is_disabled() throws Throwable {
        runtimeState.rejectInvoiceModalPage = new RejectInvoiceModalPage(getWebDriver()).get();
        runtimeState.scenario.write("Asserting that the Save button on the reject invoice modal is disabled.");
        assertFalse("Button is enabled", runtimeState.rejectInvoiceModalPage.isSaveButtonEnabled());
    }

    @ContinueNextStepsOnException
    @Then("^the rejection reason list is validated$")
    public void the_rejection_reason_list_is_validated() throws Throwable {
        runtimeState.rejectInvoiceModalPage = new RejectInvoiceModalPage(getWebDriver()).get();
        List<String> allReasons = runtimeState.rejectInvoiceModalPage.getAllRejectionsReasons(1).stream().sorted().collect(Collectors.toList());
        assertNotNull("Unexpected empty list of reasons ", allReasons);

        int index = allReasons.indexOf("Select a reason to Reject");
        allReasons.remove(index);

        List<Map<String, Object>> allDbRejectionReasons = new ArrayList<>();
        allDbRejectionReasons = dbHelperInvoices.invoiceRejectionReasons();
        runtimeState.scenario.write("Invoice rejection reasons: " + allDbRejectionReasons.toString());

        assertNotNull("Unexpected empty list of reasons", allDbRejectionReasons);
        assertEquals("Unexpected number of rejection reasons ", allReasons.size(), allDbRejectionReasons.size());

        //sort list alphabetically - not case sensitive
        allReasons.sort(String::compareToIgnoreCase);

        //compare contents of both lists
        for (int i = 1; i <= 10; i++) {
            assertEquals(allDbRejectionReasons.get(i).get("RejectionReason"), allReasons.get(i));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the following Invoice Approval tabs are displayed \"([^\"]*)\"$")
    public void the_invoice_approval_tabs_are_displayed(List<String> tabs) throws Throwable {
        if (runtimeState.invoiceApprovalPage == null) runtimeState.invoiceApprovalPage = new InvoiceApprovalPage(getWebDriver()).get();
        for (String tab : tabs) {
            assertTrue("Tab is not displayed !", runtimeState.invoiceApprovalPage.isTabDisplayed(tab));
            runtimeState.scenario.write(tab + " tab is displayed");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" tab displays \"([^\"]*)\"$")
    public void the_tab_displays(String tab, String listOfHeaders) throws Throwable {
        String[] list = listOfHeaders.split(",");
        if (runtimeState.invoiceApprovalPage == null) runtimeState.invoiceApprovalPage = new InvoiceApprovalPage(getWebDriver()).get();

        runtimeState.invoiceApprovalPage.clickTab(tab);
        outputHelper.takeScreenshot();

        if (tab.equals("Uplifts")) {
            if (runtimeState.upliftsTabPage == null) runtimeState.upliftsTabPage = new UpliftsTab(getWebDriver()).get();
            String uplifts = runtimeState.upliftsTabPage.getUplifts();
            runtimeState.scenario.write(uplifts);
            assertNotNull(uplifts);
        } else if (tab.equals("Invoice")) {
            String[] expectedHeaders =  { "Order Ref", "Order Value ("+ CURRENCY_SYMBOL +")", "Inv Num", "Inv Date", "Supplier", "Net ("+ CURRENCY_SYMBOL +")", localize("Vat") + " ("+ CURRENCY_SYMBOL +")", "Gross ("+ CURRENCY_SYMBOL +")", "Total Job Cost ("+ CURRENCY_SYMBOL +")" };
            List<String> headers = runtimeState.invoiceApprovalPage.getTableHeaders();
            for (String expected : expectedHeaders) {
                expected = expected.trim();
                assertTrue(expected + " not found in " + headers, headers.toString().contains(expected));
            }
            runtimeState.scenario.write("Invoice headers found: " + headers);
        } else {
            List<String> headers = runtimeState.invoiceApprovalPage.getTableHeaders();
            for (String expected : list) {
                expected = expected.trim();
                assertTrue(expected + " not found in " + headers, headers.toString().contains(expected));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the user will be presented with the uploaded invoice and invoice details$")
    public void the_user_will_be_presented_with_the_uploaded_invoice_and_invoice_details() throws Throwable {
        String invoiceURL = runtimeState.invoiceApprovalPage.getInvoiceUrl();
        assertNotNull("Invoice not displayed on left hand side of screen", invoiceURL);
        runtimeState.scenario.write("Invoice URL: " + invoiceURL);

        runtimeState.invoiceTabPage = new InvoiceTab(getWebDriver()).get();

        List<Map<String, Object>> dbData = dbHelperInvoices.getAllInvoicesDetails(testData.getString("orderRef"));
        String loggedDate = dbData.get(0).get("LoggedDate").toString();
        if (loggedDate != null) {
            runtimeState.scenario.write("Logged Date: " + loggedDate);
            loggedDate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), loggedDate, DB_DATE_FORMAT);
            runtimeState.scenario.write("Logged Date (Store Time): " + loggedDate);
            String loggedDateToAssert = DateHelper.convert(loggedDate, DB_DATE_FORMAT, SHORT_DATE);
            assertEquals("The Logged Date is not shown correctly", loggedDateToAssert, runtimeState.invoiceTabPage.getLoggedDate());
        }

        BigDecimal netVal = (BigDecimal) dbData.get(0).get("Net");
        BigDecimal taxVal = (BigDecimal) dbData.get(0).get("Tax");
        BigDecimal grossVal = (BigDecimal) dbData.get(0).get("Gross");

        String invDate = dbData.get(0).get("InvDate").toString();
        String invoiceDate = DateHelper.convert(invDate, DB_DATE_FORMAT, SHORT_DATE);

        String invDate2 = dbData.get(0).get("InvoiceDate").toString();
        String invoiceDate2 = DateHelper.convert(invDate2, DB_DATE_FORMAT, SHORT_DATE);

        List<String> dates = new ArrayList<>();
        dates.add(invoiceDate);
        dates.add(invoiceDate2);

        assertEquals("The Order Ref is not shown correctly", testData.getString("orderRef"), runtimeState.invoiceTabPage.getOrderRef());
        if (testData.getString("jobReference") != null) {
            assertEquals("The Job Ref is not shown correctly", testData.getString("jobReference"), runtimeState.invoiceTabPage.getJobRef());
            assertEquals("The Total Job Cost Value is not shown correctly", dbHelperInvoices.getInvoiceTotalCost(testData.getString("jobReference")), runtimeState.invoiceTabPage.getTotalJobCostValue(localize("£")));
        }
        if (dbData.get(0).get("Site") != null) {
            assertTrue("The Site is not shown correctly", runtimeState.invoiceTabPage.getSiteValue().contains(dbData.get(0).get("Site").toString()));
        }
        assertEquals("The Invoice Number is not shown correctly", testData.getString("invoiceNumber"), runtimeState.invoiceTabPage.getInvNum());
        assertTrue("The Invoice Date is not shown correctly", dates.contains(runtimeState.invoiceTabPage.getInvoiceDate()));
        assertEquals("The Supplier is not shown correctly", dbData.get(0).get("Supplier").toString().trim(), runtimeState.invoiceTabPage.getSupplierValue());
        assertEquals("The Net Value is not shown correctly", netVal.setScale(2, RoundingMode.HALF_UP).toString(), runtimeState.invoiceTabPage.getNetValue(localize("£")));
        assertEquals("The Tax Value is not shown correctly", taxVal.setScale(2, RoundingMode.HALF_UP).toString(), runtimeState.invoiceTabPage.getTaxValue(localize("Vat"), localize("£")));
        assertEquals("The Gross Value is not shown correctly", grossVal.setScale(2, RoundingMode.HALF_UP).toString(), runtimeState.invoiceTabPage.getGrossValue(localize("£")));
    }

    @ContinueNextStepsOnException
    @Then("^the invoice is displayed in red on the RFMs Invoice Approval table$")
    public void the_invoice_is_displayed_in_red_on_the_RFMs_Invoice_Approval_table() throws Exception {
        loginLogoutHelper.logout();
        Map<String, Object> dbData = dbHelperSites.getManagerInfoForSiteAndPosition(testData.getInt("siteId"), "RFM");
        String[] rfmName = dbData.get("ResourceName").toString().split(" ");
        String rfmFirstName = rfmName[0];
        String rfmLastName = rfmName[1];
        String rfmUserName = dbHelperResources.getUserNameFromFirstAndSecondNames(rfmFirstName, rfmLastName);
        testData.put("rfmUserName", rfmUserName);

        loginSteps.loginWithUserName(rfmUserName);
        menuSteps.invoicesMainMenu("Invoices Awaiting Approval", "Invoices");
        runtimeState.invoicesAwaitingApprovalPage.searchInvoices(testData.getString("orderRef"));

        outputHelper.takeScreenshot();
        assertTrue("Row is not red: " + testData.getString("orderRef"), runtimeState.invoicesAwaitingApprovalPage.isWarningRow());
    }

    @ContinueNextStepsOnException
    @Then("^the Invoice Approval page is displayed$")
    public void the_Invoice_Approval_is_displayed() throws Exception {
        try {
            assertFalse("Approve Invoice modal is displayed", runtimeState.approveInvoiceModalPage.isDisplayed());
            assertTrue("Invoice Approval page is not displayed", runtimeState.invoiceApprovalPage.isPageLoaded());
        } catch (AssertionError | Exception e) {
            throw new Exception("Invoice Approval page not displayed");
        }
    }

    private void assertMandatoryApproveInvoiceFields() {
        runtimeState.approveInvoiceModalPage.confirm();
        List<String> errors = runtimeState.approveInvoiceModalPage.getErrors();
        outputHelper.takeScreenshot();
        runtimeState.scenario.write("Verifying Reason for funding route change is mandatory");
        assertTrue("Reason for funding route change is NOT mandatory", errors.contains("Funding route note is required"));
    }

    @ContinueNextStepsOnException
    @Then("^the invoice funding route is changed to \"([^\"]*)\"$")
    public void the_invoice_funding_route_is_changed_to(String fundingRoute) {
        runtimeState.scenario.write("The original budget route is: " + testData.getString("fundingRoute"));
        runtimeState.scenario.write("The selected budget route is: " + runtimeState.approveInvoiceModalPage.getSelectedBudgetRoute());
        String budget = dbHelperInvoices.getAliasBudgetRoute(fundingRoute);
        runtimeState.approveInvoiceModalPage.selectBudgetType(budget);
        if (runtimeState.approveInvoiceModalPage.isBudgetWarningMessageDisplayed()) { runtimeState.approveInvoiceModalPage.confirmBudgetWarningMessage(); }
        runtimeState.scenario.write("The new budget route is: " + runtimeState.approveInvoiceModalPage.getSelectedBudgetRoute());
        assertMandatoryApproveInvoiceFields();
    }

    @ContinueNextStepsOnException
    @Then("^the reason for funding route change is entered$")
    public void the_reason_for_funding_route_change_is_entered() {
        String reason = "Funding route changed by automation test";
        runtimeState.scenario.write("Reason for change: " + reason);
        runtimeState.approveInvoiceModalPage.enterReason(reason);
    }

    @ContinueNextStepsOnException
    @Then("^the Extreme Weather question is answered$")
    public void the_Extreme_Weather_question_is_answered() {
        if (RandomUtils.nextBoolean()) {
            runtimeState.scenario.write("Extreme weather: Yes");
            runtimeState.approveInvoiceModalPage.selectYesRadioRelatedWeather();
        } else {
            runtimeState.scenario.write("Extreme weather: No");
            runtimeState.approveInvoiceModalPage.selectNoRadioRelatedWeather();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Invoice Lines table is displayed$")
    public void the_Invoice_Lines_table_is_displayed() throws Exception {
        if (runtimeState.invoiceLinesModal == null) runtimeState.invoiceLinesModal = new InvoiceLinesModal(getWebDriver()).get();
        Grid invoiceLines = runtimeState.invoiceLinesModal.getGrid();
        assertNotNull(invoiceLines);
    }

}
