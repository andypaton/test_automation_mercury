package mercury.steps.portal.invoices;

import static mercury.helpers.Globalisation.SHORT;
import static mercury.helpers.Globalisation.TAX_RATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.pdfhelper.PdfHelperOCRInvoice;
import mercury.runtime.RuntimeState;

public class PortalUploadTemplateSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private TestData testData;
    @Autowired private PdfHelperOCRInvoice pdfHelperOCR;


    // TODO : change this.  The asserts are wrong
    // should be runtimeState.invoiceTemplatesPage.isPageLoaded()
    // Not sure why the if statement is in this method test should know what page should load.  Get an invoice without a template!!!
    @And("^the Invoice Templates page is displayed$")
    public void the_invoice_templates_page_is_displayed() throws Throwable {
        if(!getWebDriver().getCurrentUrl().contains("Portal/Supplier/InvoiceTemplateUpload")) {
            the_user_deletes_an_invoice_template();
        } else {
            assertEquals("Unexpected Page Title", "Invoice Templates", runtimeState.invoiceTemplatesPage.getInvoiceTemplatesTitle());
            assertEquals("Unexpected Page Header", "Upload Invoice Templates", runtimeState.invoiceTemplatesPage.getInvoiceUploadTemplateHeader());
        }
        outputHelper.takeScreenshots();

    }

    @And("^the user uploads an invoice template$")
    public void the_user_uploads_an_invoice_template() throws Throwable {
        runtimeState.invoiceTemplatesPage.setInvoiceTemplateFileName(System.getProperty("user.dir")  + "\\src\\test\\resources\\portalfiles\\OCRinvoiceTemplate.pdf");
        outputHelper.takeScreenshots();
        runtimeState.invoiceTemplatePage = runtimeState.invoiceTemplatesPage.uploadInvoiceTemplate();
        outputHelper.takeScreenshots();
    }

    @And("^the invoice template is updated with the information fields ((?:with|without)) tax amount$")
    public void the_invoice_template_is_updated_with_the_information_fields_except_tax_amount(String withOrWithout) throws Throwable {
        int uploadedInvoiceTemplateId = dbHelperInvoices.getUploadedInvoiceTemplateId(testData.getInt("jobReference"));
        dbHelperInvoices.updateInvoiceTemplateCoordinates(uploadedInvoiceTemplateId, localize("VAT"));
        if ("with".equalsIgnoreCase(withOrWithout)) {
            the_user_saves_the_invoice_template(true);
        } else {
            runtimeState.invoiceTemplatePage.excludeTaxAmount(localize("Vat"));
            the_user_saves_the_invoice_template(false);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Save Template button is displayed$")
    public void the_invoice_template_can_be_saved() {
        assertTrue("Save Template button is not displayed", runtimeState.invoiceTemplatePage.isSaveButtonDisplayed());
    }

    @And("^Legal Entity details are selected$")
    public void Legal_Entity_details_are_selected() {
        runtimeState.invoiceTemplatePage.selectLegalEntity();
        runtimeState.invoiceTemplatePage.highlightLegalEntity();
        outputHelper.takeScreenshot();
    }

    @And("^Invoice Number details are selected$")
    public void Invoice_Number_details_are_selected() {
        runtimeState.invoiceTemplatePage.selectInvoiceNumber();
        runtimeState.invoiceTemplatePage.highlightInvoiceNumber();
        outputHelper.takeScreenshot();
    }

    @And("^Invoice Date details are selected$")
    public void Invoice_Date_details_are_selected() {
        runtimeState.invoiceTemplatePage.selectInvoiceDate();
        runtimeState.invoiceTemplatePage.highlightInvoiceDate();
        outputHelper.takeScreenshot();
    }

    @And("^Net Amount details are selected$")
    public void Net_Amount_details_are_selected() {
        runtimeState.invoiceTemplatePage.selectNetAmount();
        runtimeState.invoiceTemplatePage.highlightNetAmount();
        outputHelper.takeScreenshot();
    }

    @And("^Tax Amount details are selected$")
    public void VAT_Amount_details_are_selected() {
        runtimeState.invoiceTemplatePage.selectTaxAmount(localize("Vat"));
        runtimeState.invoiceTemplatePage.highlightTaxAmount();
        outputHelper.takeScreenshot();
    }

    @And("Tax Amount details are exempt$")
    public void Tax_Amount_details_are_exempt() {
        runtimeState.invoiceTemplatePage.selectTaxAmount(localize("Vat"));
        runtimeState.invoiceTemplatePage.excludeTaxAmount();
    }

    @And("^Gross Amount details are selected$")
    public void Gross_Amount_details_are_selected() {
        runtimeState.invoiceTemplatePage.selectGrossAmount();
        runtimeState.invoiceTemplatePage.highlightGrossAmount();
        outputHelper.takeScreenshot();
    }

    @And("^the invoice template is updated with the information fields$")
    public void the_invoice_template_is_updated_with_the_information_fields() throws Throwable {
        int uploadedInvoiceTemplateId = dbHelperInvoices.getUploadedInvoiceTemplateId(testData.getInt("jobReference"));
        dbHelperInvoices.updateInvoiceTemplateCoordinatesInvoicing(uploadedInvoiceTemplateId, localize("VAT"));
    }

    @And("^the user deletes an invoice template$")
    public void the_user_deletes_an_invoice_template() throws Throwable {
        runtimeState.invoiceDeleteInvoiceTemplateModal = runtimeState.invoiceTemplatePage.deleteTemplate();
        runtimeState.invoiceTemplatesPage = runtimeState.invoiceDeleteInvoiceTemplateModal.delete();
        outputHelper.takeScreenshots();
    }

    public void the_user_saves_the_invoice_template(Boolean isTaxAmountSpecified) throws Throwable {
        runtimeState.invoiceTemplatePage.selectSaveTemplate(isTaxAmountSpecified);
        assertTrue("Unexpected alert message", runtimeState.invoiceTemplatePage.getSavedTemplateAlert().contains("Saved Successfully"));
    }

    @And("^the Invoice Template is saved$")
    public void the_invoice_template_is_saved() throws Throwable {
        runtimeState.invoiceTemplatePage.saveTemplate();
        assertTrue("Unexpected alert message", runtimeState.invoiceTemplatePage.getSavedTemplateAlert().contains("Saved Successfully"));
    }

    @When("^an OCR invoice template is uploaded$")
    public void an_OCR_invoice_template_is_uploaded() throws Throwable {
        the_invoice_templates_page_is_displayed();
        the_user_uploads_an_invoice_template();
    }


    @And("^an OCR invoice template is created and uploaded for the assigned company$")
    public void an_ocr_invoice_template_is_created_and_uploaded_for_the_assigned_company() throws Throwable {
        the_invoice_templates_page_is_displayed();
        String companyName = null;
        Double netValue = null;
        String invoiceDate = DateHelper.getDateInFormat(new Date(), SHORT);
        String invoiceNumber = testData.getInt("jobReference").toString() + DataGenerator.GenerateRandomString(3, 3, 0, 0, 0, 0);

        switch (testData.getString("jobStatus")) {

        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice":
        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice and Documents":
            String ppmPONumber = dbHelperInvoices.getPONumberfromPPM(testData.getInt("jobReference")).toString();
            companyName = dbHelperInvoices.getAssignedCompanyName(ppmPONumber);
            netValue = dbHelperInvoices.getOrderCost(ppmPONumber);
            break;

        default:
            companyName = dbHelperInvoices.getAssignedCompanyNameFromJobReference(testData.getInt("jobReference"));
            netValue = dbHelperInvoices.getOrderValue(testData.getInt("jobReference"), testData.getString("jobStatus"));
            break;
        }

        pdfHelperOCR.createSimpleOCRInvoice(companyName, invoiceNumber, invoiceDate, String.valueOf(netValue), String.valueOf(Math.round(netValue) * TAX_RATE));

        runtimeState.invoiceTemplatesPage.setInvoiceTemplateFileName(System.getProperty("user.dir")  + testData.getString("pdfPath"));
        runtimeState.invoiceTemplatePage = runtimeState.invoiceTemplatesPage.uploadInvoiceTemplate();
        outputHelper.takeScreenshots();

    }

    @ContinueNextStepsOnException
    @Then("^the user is able to delete the uploaded template$")
    public void the_user_is_able_to_delete_the_uploaded_template() throws Throwable {
        the_user_deletes_an_invoice_template();
    }

    @ContinueNextStepsOnException
    @Then("^the save template button is disabled$")
    public void the_save_template_button_is_disabled() throws Throwable {
        assertFalse("Unexpected element displayed",runtimeState.invoiceTemplatePage.isSaveButtonDisplayed());
        outputHelper.takeScreenshots();
    }
}
