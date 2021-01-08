package mercury.steps.portal.invoices;


import static org.junit.Assert.assertTrue;


import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.runtime.RuntimeState;


public class PortalSubmittedInvoicesAndCreditsSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\", \"([^\"]*)\" calendar buttons, \"([^\"]*)\" dropdown and \"([^\"]*)\" button are displayed$")
    public void the_calendar_buttons_dropdown_and_buttons_are_displayed(String dateFrom, String dateUntil, String supplierDropdown, String findButton) throws Throwable {
        assertTrue(dateFrom + "is not displayed", runtimeState.submittedInvoicesAndCreditsPage.isDateFromDisplayed());
        assertTrue(dateFrom + "is not displayed", runtimeState.submittedInvoicesAndCreditsPage.isDateUntilDisplayed());
        assertTrue(supplierDropdown + "is not displayed", runtimeState.submittedInvoicesAndCreditsPage.isSupplierDropdownDisplayed());
        assertTrue(findButton + "is not displayed", runtimeState.submittedInvoicesAndCreditsPage.isFindButtonDisplayed());

        outputHelper.takeScreenshots();
    }
    
    @When("^the user selects an invoice from the Submitted Invoices and Credits grid view$")
    public void the_user_selects_an_invoice_from_the_Submitted_Invoices_and_Credits_grid_view() throws Throwable {
        String orderRef = dbHelperInvoices.getRandomOrderRefWithInvoicePdfdDocument();
        runtimeState.submittedInvoicesAndCreditsPage.search(orderRef);
        runtimeState.invoiceViewPage = runtimeState.submittedInvoicesAndCreditsPage.clickOrderRef(orderRef);
    }

    @ContinueNextStepsOnException
    @Then("^the PDF view of invoice is displayed$")
    public void the_PDF_view_of_invoice_is_displayed() throws Throwable {
        assertTrue("PDF is not displayed", runtimeState.invoiceViewPage.isPDFDisplayed());
        outputHelper.takeScreenshotOfDesktop();
       // String link = runtimeState.invoiceViewPage.getPdfLinkAddress();
       // runtimeState.invoiceViewPage.openPdfInNewTab(link);
       // outputHelper.takeScreenshotOfDesktop();
    }
}