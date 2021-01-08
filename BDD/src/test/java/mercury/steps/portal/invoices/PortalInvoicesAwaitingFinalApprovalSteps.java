package mercury.steps.portal.invoices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.PortalInvoiceHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.runtime.RuntimeState;
import mercury.steps.portal.PortalCommon;

import mercury.pageobject.web.portal.invoices.InvoiceFinalApprovalPage;
import mercury.pageobject.web.portal.invoices.UpliftsTab;

public class PortalInvoicesAwaitingFinalApprovalSteps {

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private PortalCommon portalCommon;
    @Autowired private PortalInvoiceHelper portalInvoiceHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private OutputHelper outputHelper;

    @ContinueNextStepsOnException
    @Then("^the invoice funding route is displayed at the top right hand corner of the grid$")
    public void the_budget_route_is_displayed_on_the_grid() throws Throwable {
        String invoiceNum = testData.getString("invoiceNumber");
        String expectedBudgetRoute = dbHelperInvoices.getBudgetRouteOfInvoiceAwaitingFinalApproval(invoiceNum);
        expectedBudgetRoute = expectedBudgetRoute == null ? dbHelperInvoices.getBudgetRouteOfInvoiceAwaitingInitialApproval(invoiceNum) : expectedBudgetRoute;
        if (runtimeState.invoiceFinalApprovalPage == null) runtimeState.invoiceFinalApprovalPage = new InvoiceFinalApprovalPage(getWebDriver()).get();
        String actualBudgetRoute = runtimeState.invoiceFinalApprovalPage.getInvoiceBudget();
        try {
            assertEquals("Unexpected Budget Route! ", expectedBudgetRoute, actualBudgetRoute);
        } catch (AssertionError e) {
            assertEquals("Unexpected Budget Route! ", dbHelperInvoices.getAliasFundingRoute(expectedBudgetRoute), actualBudgetRoute);
        }
        runtimeState.scenario.write("Budget Route is displayed on the grid and is : " + expectedBudgetRoute);
    }

    @ContinueNextStepsOnException
    @Then("^the following Invoice Final Approval tabs are displayed \"([^\"]*)\"$")
    public void the_tabs_are_displayed(List<String> tabs) throws Throwable {
        if (runtimeState.invoiceFinalApprovalPage == null) runtimeState.invoiceFinalApprovalPage = new InvoiceFinalApprovalPage(getWebDriver()).get();
        for (String tab : tabs) {
            assertTrue("Tab is not displayed !", runtimeState.invoiceFinalApprovalPage.isTabDisplayed(tab));
            runtimeState.scenario.write(tab + " tab is displayed");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the number of uplifts attached to the job are displayed$")
    public void the_number_of_uplifts_attached_to_the_job_are_displayed() throws Throwable {
        runtimeState.upliftsTabPage = runtimeState.invoiceFinalApprovalPage.clickUpliftsTab("Uplifts");

        int count = dbHelperInvoices.getCountOfFundingRequests(testData.getString("invoiceNumber"));
        assertNotNull("Count of Funding Requests returned null ! " + count);
        String expectedUplifts = (count == 0 | count == 1) ? "There are no uplifts." : "There are " + count + " uplifts.";
        runtimeState.scenario.write("Uplifts tab displays: " + expectedUplifts);
        assertEquals("Unexpected Uplifts ", expectedUplifts, runtimeState.upliftsTabPage.getUplifts());
    }

    @ContinueNextStepsOnException
    @Then("^the Invoices Awaiting Final Approval table is displayed$")
    public void the_Invoices_Awaiting_Final_Approval_table_is_displayed() throws Throwable {
        portalCommon.the_table_on_page_displays_correctly("Invoices Awaiting Final Approval", "Invoices Awaiting Final Approval");
    }

    @ContinueNextStepsOnException
    @Then("^the invoice is ((?:still|not)) displayed on the Invoices Awaiting Final Approval table$")
    public void the_invoice_is_displayed_on_the_Invoices_Awaiting_Final_Approval_table(String option) throws Throwable {
        String orderReference = testData.getString("orderRef");
        runtimeState.invoicesAwaitingFinalApprovalPage.searchInvoices(orderReference);
        if ("not".equals(option)) {
            assertFalse("Invoice is displayed on the grid!", runtimeState.invoicesAwaitingFinalApprovalPage.isInvoiceDisplayed(orderReference));
        } else {
            assertTrue("Invoice not displayed on the grid!", runtimeState.invoicesAwaitingFinalApprovalPage.isInvoiceDisplayed(orderReference));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Invoice Final Approval page is displayed$")
    public void the_Invoice_Final_Approval_is_displayed() throws Exception {
        try {
            assertTrue("Invoice Final Approval page is not displayed", runtimeState.invoiceFinalApprovalPage.isPageLoaded());
        } catch (AssertionError | Exception e) {
            throw new Exception("Invoice Final Approval page not displayed");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" tab on the Invoice Final Approval page displays \"([^\"]*)\"$")
    public void the_tab_on_the_Invoice_Final_approval_page_displays(String tab, String listOfHeaders) throws Throwable {
        String[] list = listOfHeaders.split(",");
        if (runtimeState.invoiceFinalApprovalPage == null) runtimeState.invoiceFinalApprovalPage = new InvoiceFinalApprovalPage(getWebDriver()).get();

        runtimeState.invoiceFinalApprovalPage.clickTab(tab);
        outputHelper.takeScreenshot();

        if (tab.equals("Uplifts")) {
            if (runtimeState.upliftsTabPage == null) runtimeState.upliftsTabPage = new UpliftsTab(getWebDriver()).get();
            String uplifts = runtimeState.upliftsTabPage.getUplifts();
            runtimeState.scenario.write(uplifts);
            assertNotNull(uplifts);
        } else {
            List<String> headers = runtimeState.invoiceFinalApprovalPage.getTableHeaders();
            for (String expected : list) {
                expected = expected.trim();
                assertTrue(expected + " not found in " + headers, headers.toString().contains(expected));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Invoice Final Approval \"([^\"]*)\" tab displays expected fields$")
    public void the_Invoice_Final_Approval_tab_displays_expected_fields(String tab) throws Exception {
        List<Map<String, Object>> dbData = null;

        switch (tab) {
        case "Invoice":
            runtimeState.invoiceTabPage = runtimeState.invoiceFinalApprovalPage.clickInvoiceTab(tab);
            dbData = dbHelperInvoices.getInvoiceTabDetailsInInvoiceAwaitingFinalApproval(testData.getString("invoiceNumber"));
            portalInvoiceHelper.assertInvoiceTab(dbData);
            break;

        case "Job Notes":
            runtimeState.jobNotesTab = runtimeState.invoiceFinalApprovalPage.clickJobNotesTab(tab);
            portalInvoiceHelper.assertJobNotesTab();
            break;

        case "Closedown":
            runtimeState.closedownTab = runtimeState.invoiceFinalApprovalPage.clickClosedownTab(tab);
            dbData = dbHelperJobs.getJobClosedownDetails(testData.getInt("jobReference"));
            portalInvoiceHelper.assertClosedownTab();
            break;

        default:
            throw new Exception("Cannot find tab: " + tab);
        }
    }

}
