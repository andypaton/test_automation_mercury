package mercury.steps.portal.invoices;

import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.pageobject.web.portal.invoices.InvoiceDetailsPage;
import mercury.pageobject.web.portal.invoices.ManageInvoiceLinesModal;
import mercury.pageobject.web.portal.invoices.PortalConsolidatedInvoicePage;
import mercury.pageobject.web.portal.invoices.PortalOrdersInvoicePage;
import mercury.pageobject.web.portal.invoices.UploadInvoiceDocumentsPage;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;

public class PortalConsolidatedInvoicesSteps {
    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;
    @Autowired private PortalOrdersAwaitingInvoiceSteps portalOrdersAwaitingInvoiceSteps;
    @Autowired private CommonSteps commonSteps;

    @When("^the user clicks on manage lines link$")
    public void the_user_clicks_on_manage_lines_link() {
        runtimeState.portalConsolidatedInvoicePage = new PortalConsolidatedInvoicePage(getWebDriver()).get();
        runtimeState.portalConsolidatedInvoicePage.clickManageLinesLink();
    }

    @ContinueNextStepsOnException
    @Then("^the Manage Lines link is displayed on the page$")
    public void the_manage_lines_link_is_displayed_on_the_page() {
        runtimeState.portalConsolidatedInvoicePage = new PortalConsolidatedInvoicePage(getWebDriver()).get();
        assertTrue("The Manage Lines link is not present", runtimeState.portalConsolidatedInvoicePage.isManageLinesButtonDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the Save button is disabled$")
    public void the_save_button_is_disabled() {
        assertFalse("Save button is enabled! ", runtimeState.manageInvoiceLinesModal.isSaveButtonEnabled());
    }

    // TO-DO: Work in progress!!!
    @ContinueNextStepsOnException
    @Then("^the ppm orders ((?:are|are not)) displayed on the consolidated invoice$")
    public void the_ppm_orders_are_displayed_on_the_consolidated_invoice(String displayed) {
        boolean orderDisplayed = "are".equalsIgnoreCase(displayed) ? true : false;

        Grid grid = runtimeState.portalConsolidatedInvoicePage.getManageLinesGrid();

        List<Integer> ppmOrders = testData.getIntList("createdJobs");
        for (Integer ppm : ppmOrders) {
            assertEquals("Unexpected order", orderDisplayed, runtimeState.portalConsolidatedInvoicePage.isPpmOrderDisplayed(ppm.toString()));
        }
        outputHelper.takeScreenshots();
    }

    @And("^ppm order lines are added to the invoice$")
    public void ppm_order_lines_are_added_to_the_invoice() throws Throwable {
        the_user_clicks_on_manage_lines_link();
        the_user_clicks_include_in_invoice_on_the_ppm_order_line();
        outputHelper.takeScreenshots();
        commonSteps.the_button_is_clicked("Save");
    }

    @And("^an order line is deleted from the invoice$")
    public void order_line_is_deleted_from_the_invoice() throws Throwable {
        the_user_clicks_on_manage_lines_link();
        the_order_line_is_deleted_from_the_consolidated_invoice();
        outputHelper.takeScreenshots();
        commonSteps.the_button_is_clicked("Save");
    }

    @And("^the user clicks Include In Invoice on the ppm order line$")
    public void the_user_clicks_include_in_invoice_on_the_ppm_order_line() throws Throwable {
        // Get list of ppm jobs created
        List<Integer> ppmIds = testData.getIntList("createdJobs");
        runtimeState.scenario.write("PPM Ids to be added to the consolidated invoice: " + ppmIds);
        runtimeState.manageInvoiceLinesModal = new ManageInvoiceLinesModal(getWebDriver()).get();

        // Search for ppmId in table and add to invoice
        if (!ppmIds.isEmpty()) {
            for (Integer ppmId : ppmIds) {
                runtimeState.manageInvoiceLinesModal.searchPpmOrders(ppmId.toString());
                runtimeState.manageInvoiceLinesModal.clickPpmOrderCheckbox(ppmId.toString());
            }
        }
        outputHelper.takeScreenshots();
    }

    // TO DO: Add ppmId to testData!
    @When("^an order line is deleted from the consolidated invoice$")
    public void the_order_line_is_deleted_from_the_consolidated_invoice() throws Throwable {
        // Get list of ppm jobs created
        String ppmId = testData.getString("ppmId");
        runtimeState.manageInvoiceLinesModal = new ManageInvoiceLinesModal(getWebDriver()).get();
        runtimeState.manageInvoiceLinesModal.searchPpmOrders(ppmId.toString());
        runtimeState.manageInvoiceLinesModal.clickPpmOrderCheckbox(ppmId.toString());

        outputHelper.takeScreenshots();
    }

    // TO-DO: Work in progress!!!
    @ContinueNextStepsOnException
    @Then("^the order ((?:is|is not)) displayed on the consolidated invoice$")
    public void the_order_is_displayed_on_the_consolidated_invoice(String displayed) throws Throwable {
        boolean orderDisplayed = "is".equalsIgnoreCase(displayed) ? true : false;

        Grid grid = runtimeState.portalConsolidatedInvoicePage.getManageLinesGrid();

        List<Integer> ppmOrders = testData.getIntList("createdJobs");
        for (Integer ppm : ppmOrders) {
            assertEquals("Unexpected order", orderDisplayed, runtimeState.portalConsolidatedInvoicePage.isPpmOrderDisplayed(ppm.toString()));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" button ((?:is|is not)) displayed on the page$")
    public void the_portal_button_is_displayed_on_page(String button, String displayed) throws Throwable {
        boolean buttonDisplayed = "is".equalsIgnoreCase(displayed) ? true : false;

        switch(button) {
        case "Back" :
            assertEquals("Unexpected button", buttonDisplayed, runtimeState.consolidatedInvoicesInProgressPage.isBackButtonDisplayed());
            break;
        default :
            throw new Exception("Cannot find expected button: " + button);
        }
    }

    @ContinueNextStepsOnException
    @Then("^an invoice document is uploaded$")
    public void an_invoice_document_is_uploaded() throws Throwable {
        runtimeState.uploadInvoiceDocumentsPage = new UploadInvoiceDocumentsPage(getWebDriver()).get();
        runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
        outputHelper.takeScreenshots();
        POHelper.waitForAngularRequestsToFinish();
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentsPage.selectUploadFiles();
    }

    @ContinueNextStepsOnException
    @Then("^the user enters the invoice details with ((?:correct|incorrect)) legal entity$")
    public void the_user_enters_the_invoice_details_with_correct_legal_entity(String legalEntity) throws Throwable {
        portalOrdersAwaitingInvoiceSteps.an_invoice_number_is_entered();

        runtimeState.invoiceDetailsPage.setInvoiceDate(DateHelper.getDateInFormat(new Date(), SHORT_DATE));
        runtimeState.invoiceDetailsPage.selectDate();

        runtimeState.invoiceDetailsPage.setInvoiceNetAmount(testData.getString("callOutRate"));
        runtimeState.scenario.write("The invoice net amount is entered: " + testData.getString("callOutRate"));

        if (legalEntity.equalsIgnoreCase("correct")) {
            runtimeState.invoiceDetailsPage.selectLegalEntity();
        } else {
            runtimeState.invoiceDetailsPage.enterLegalEntity(DataGenerator.GenerateRandomString(13, 13, 3, 0, 10, 0));
        }

        outputHelper.takeScreenshots();
        commonSteps.the_button_is_clicked("Save");
    }

    @When("^the user edits an invoice with an incorrect legal entity$")
    public void the_user_edits_the_invoice_with_incorret_legal_entity() throws Throwable {
        runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();
        runtimeState.portalOrdersInvoicePage.clickEditLink();
        runtimeState.invoiceDetailsPage = new InvoiceDetailsPage(getWebDriver()).get();
        the_user_enters_the_invoice_details_with_correct_legal_entity("incorrect");
    }

    @When("^the legal entity section shows \"([^\"]*)\"$")
    public void the_legal_entity_section_shows(String text) {
        assertEquals("Unexpected legal entity text", text, runtimeState.invoiceDetailsPage.getLegalEntityLabelText());
    }

    @When("^the Invoice page is displayed$")
    public void the_invoice_page_is_displayed() {
        assertEquals("Unexpected Page Title", "Invoice Details", runtimeState.invoiceDetailsPage.getInvoiceDetailsTitle());
    }
}
