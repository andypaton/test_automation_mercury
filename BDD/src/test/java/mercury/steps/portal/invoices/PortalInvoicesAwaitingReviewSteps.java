package mercury.steps.portal.invoices;


import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.gridV3.Grid;
import mercury.runtime.RuntimeState;

public class PortalInvoicesAwaitingReviewSteps {
    private static final Logger logger = LogManager.getLogger();

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    /**
     * Invoices with 2 types of action (1.Values Changed, 2.No Approver) will be awaiting 'Accounts payable' user review. Scenario and code for invoices awaiting review with 'No Approver' action is pending.
     * @param action
     * @throws Throwable
     */
    @And("^the user opens an invoice awaiting review with \"((?:Values Changed|No Approver))\" action$")
    public void the_user_opens_an_invoice_awaiting_review_with_action(String action) throws Throwable {
        logger.debug("Opening Job Reference: " + testData.getInt("jobReference"));

        String orderRef = null;
        if ("VALUES CHANGED".equalsIgnoreCase(action)) {
            orderRef = dbHelperInvoices.getRandomAPReviewOrderRefNumber("Values Changed");
            if (orderRef == null) {
                throw new PendingException("No test data found");
            }
            runtimeState.invoicesAndCreditsAwaitingReviewPage.searchInvoices(orderRef);
            runtimeState.invoicePendingReviewPage = runtimeState.invoicesAndCreditsAwaitingReviewPage.openInvoiceAwaitingReview(orderRef);
        } else {
            // No Approver
            runtimeState.invoicesAndCreditsAwaitingReviewPage.searchInvoices("No Approver");
            Grid grid = runtimeState.invoicesAndCreditsAwaitingReviewPage.getGrid();
            if (grid.getRows().get(0).getCell(0).getText().equals("No awaiting review invoices or credits found")) {
                throw new PendingException("No test data found");
            }
            orderRef = grid.getRows().get(0).getCell("Order Ref").getText();
            runtimeState.invoicesAndCreditsAwaitingReviewPage.searchInvoices(orderRef);
            runtimeState.invoiceReassignApproverPage = runtimeState.invoicesAndCreditsAwaitingReviewPage.openInvoiceWithNoApprover(orderRef);
        }
        runtimeState.scenario.write("order ref: " + orderRef);
        testData.addStringTag("newOrderRef", orderRef);
        outputHelper.takeScreenshots();
    }

    @When("^the user approves the invoice pending review$")
    public void the_user_approves_the_invoice_pending_review() throws Throwable {
        runtimeState.invoicePendingReviewPage.clickApprove();
        outputHelper.takeScreenshots();
    }

    @When("^the invoice is re-assigned to a new approver$")
    public void the_invoice_is_reassigned_to_a_new_approver() throws Throwable {
        String approver = runtimeState.invoiceReassignApproverPage.selectRandomApprover();
        runtimeState.scenario.write("Assigned to: " + approver);
        outputHelper.takeScreenshots();
        runtimeState.invoiceReassignApproverPage.assign();
    }

    @When("^the user rejects the invoice pending review$")
    public void the_user_rejects_the_invoice_pending_review() throws Throwable {
        runtimeState.rejectInvoicePendingReviewModal = runtimeState.invoicePendingReviewPage.clickReject();
        outputHelper.takeScreenshots();
        runtimeState.rejectInvoicePendingReviewModal.selectRandomRejectionReason();
        outputHelper.takeScreenshots();
        String notes = "Please enter some notes to verify the database update " + DateHelper.dateAsString(new Date());
        runtimeState.rejectInvoicePendingReviewModal.enterNotes(notes);
        outputHelper.takeScreenshots();
        runtimeState.invoicePendingReviewPage = runtimeState.rejectInvoicePendingReviewModal.selectSave();
    }
}
