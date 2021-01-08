package mercury.steps.portal.invoices;


import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.MS_SHORT_DATE;
import static mercury.helpers.Globalisation.TAX_RATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.helpers.ResourceHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;


public class CreateStandAloneCreditNoteSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired
    private CommonSteps commonSteps;
    @Autowired
    private DbHelperInvoices dbHelperInvoices;
    @Autowired
    private ResourceHelper resourceHelper;


    @And("^a supplier or vendor to create credit note for is selected$")
    public void a_supplier_or_vendor_to_create_credit_note_for_is_selected() throws Throwable {
        testData.put("supplier", runtimeState.createCreditNotePage.selectRandomSupplierOrVendor());
        runtimeState.scenario.write("Selected Supplier is: " + testData.getString("supplier"));
    }

    @ContinueNextStepsOnException
    @Then("^the Credit Note Date is autopopulated with current date$")
    public void the_credit_note_date_is_autopopulated_with_current_date() throws Throwable {
        String currentDate = DateHelper.dateAsString(new Date(), MS_SHORT_DATE);
        String actualCreditNoteDate = runtimeState.creditNotePage.getCreditNoteDate();
        assertEquals(" Unexpected Credit Note Date: ", currentDate, actualCreditNoteDate);
        runtimeState.scenario.write("Credit Note Date is autopopulated and is: " + currentDate);
    }

    @When("^the Credit Note Number is entered$")
    public void the_credit_note_number_is_entered() throws Throwable {
        String creditNoteNumber = String.valueOf(DataGenerator.randBetween(100000000, 999999999));
        testData.put("creditNoteNumber", creditNoteNumber);
        runtimeState.scenario.write("Credit note number entered is: " + creditNoteNumber);
        runtimeState.creditNotePage.enterCreditNoteNumber(creditNoteNumber);
    }

    @And("^the SUN contract code is selected$")
    public void the_sun_contract_code_is_selected() {
        String option = runtimeState.creditNotePage.selectSunContractCode();
        testData.put("sunContractCode", option);
        runtimeState.scenario.write("Selected SUN contract code is: " + option);
    }

    @And("^the SUN store code is selected$")
    public void the_sun_store_code_is_selected() {
        String option = runtimeState.creditNotePage.selectSunStoreCode();
        testData.put("sunStoreCode", option);
        runtimeState.scenario.write("Selected SUN store code is: " + option);
    }

    @And("^the SUN nominal code is selected$")
    public void the_sun_nominal_code_is_selected() {
        String option = runtimeState.creditNotePage.selectSunNominalCode();
        testData.put("sunNominalCode", option);
        runtimeState.scenario.write("Selected SUN nominal code is: " + option);
    }

    @And("^the SUN discipline code is selected$")
    public void the_sun_discipline_code_is_selected() {
        String option = runtimeState.creditNotePage.selectSunDisciplineCode();
        testData.put("sunDisciplineCode", option);
        runtimeState.scenario.write("Selected SUN discipline code is: " + option);
    }

    @And("^the SUN tax code is selected$")
    public void the_sun_tax_code_is_selected() {
        String option = runtimeState.creditNotePage.selectSunTaxCode();
        testData.put("sunTaxCode", option);
        runtimeState.scenario.write("Selected SUN tax code is: " + option);
    }

    @And("^the SUN area code is selected$")
    public void the_sun_area_code_is_selected() {
        String option = runtimeState.creditNotePage.selectSunAreaCode();
        testData.put("sunAreaCode", option);
        runtimeState.scenario.write("Selected SUN area code is: " + option);
    }

    @And("^the SUN Reference is entered$")
    public void the_sun_reference_is_entered() throws Throwable {
        String sunReference = DataGenerator.randomAlphaNumericWords(2, 3);
        testData.put("sunReference", sunReference);
        runtimeState.creditNotePage.enterSunReference(sunReference);
        runtimeState.scenario.write("SUN Reference entered is : " + sunReference);
    }

    @And("^all the SUN codes are selected$")
    public void all_the_sun_codes_are_selected() throws Throwable {
        the_sun_contract_code_is_selected();
        the_sun_store_code_is_selected();
        the_sun_nominal_code_is_selected();
        the_sun_discipline_code_is_selected();
        the_sun_tax_code_is_selected();
        the_sun_area_code_is_selected();
    }

    @And("^the Net Amount is entered$")
    public void the_net_amount_is_entered() throws Throwable {
        DecimalFormat df = new DecimalFormat("#0.00");
        String netAmount = df.format(DataGenerator.GenerateRandomDouble(1.00, 1000.00));
        testData.put("netAmount", netAmount);
        runtimeState.creditNotePage.enterNetAmount(String.valueOf(netAmount));
        runtimeState.scenario.write("Net amount entered is: " + netAmount);
    }

    @And("^the Tax amount ((?:less|greater|either less or greater)) than the allowed percent of the net amount is entered$")
    public void the_tax_amount_greater_than_percent_of_the_net_amount_is_entered(String tax) throws Throwable {
        Double percentage = 0.00;
        DecimalFormat df = new DecimalFormat("#0.00");

        if ("LESS".equalsIgnoreCase(tax)) {
            percentage = DataGenerator.GenerateRandomDouble(1.00, TAX_RATE);
        } else if ("GREATER".equalsIgnoreCase(tax)) {
            percentage = DataGenerator.GenerateRandomDouble((TAX_RATE + 1), 100.00);
        } else if ("EITHER LESS OR GREATER".equalsIgnoreCase(tax)) {
            percentage = DataGenerator.GenerateRandomDouble(1.0, 100.00);
        }
        String taxAmount = df.format(Double.parseDouble(testData.getString("netAmount")) * percentage / 100.00);
        testData.put("taxAmount", taxAmount);
        runtimeState.scenario.write("Tax amount entered is: " + taxAmount);
        runtimeState.creditNotePage.enterTaxAmount(taxAmount);
        Double grossAmount = new BigDecimal(Double.parseDouble(testData.getString("netAmount")) + Double.parseDouble(taxAmount)).setScale(2, RoundingMode.HALF_UP).doubleValue();
        String expectedGrossAmount = df.format(grossAmount);
        testData.put("grossAmount", expectedGrossAmount);
    }

    @ContinueNextStepsOnException
    @Then("^the Gross Amount is autocalculated by system$")
    public void the_gross_amount_is_autocalculated_by_system() throws Throwable {
        String actualGrossAmount = runtimeState.creditNotePage.getGrossAmount().replaceAll("\\s+","");
        assertEquals("Unexpected Gross Amount. Expected Gross Amount: " + testData.getString("grossAmount") + ". Actual Gross Amount: " + actualGrossAmount, testData.getString("grossAmount"), actualGrossAmount);
        runtimeState.scenario.write("The gross amount is autocalculated and auto-populated by the system and is: " + testData.getString("grossAmount"));
    }

    @And("^the user fills in all mandatory fields in the credit note page$")
    public void the_user_fills_in_all_mandatory_fields_in_the_credit_note_page() throws Throwable {
        the_credit_note_number_is_entered();
        the_net_amount_is_entered();
        the_sun_reference_is_entered();
        all_the_sun_codes_are_selected();
    }

    @ContinueNextStepsOnException
    @Then("^the credit note details entered by the user are displayed in the top row of the credit note details table$")
    public void the_credit_note_details_entered_by_the_user_are_displayed_in_the_top_row_of_the_credit_note_details_table() throws Throwable {
        Grid grid = runtimeState.enterCreditNoteDetailsPage.getCreditNoteDetailsTable();
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");
        List<Row> row = grid.getRows();
        runtimeState.scenario.write("Asserting Credit Note Number in the table is: " + testData.getString("creditNoteNumber"));
        assertEquals("Unexpected Credit Note Number. ", testData.getString("creditNoteNumber"), row.get(0).getCell("Credit Note Number").getText());
        runtimeState.scenario.write("Asserting Credit Note Date in the table is: " + DateHelper.dateAsString(new Date(), MS_SHORT_DATE));
        assertEquals("Unexpected Credit Note Date. ", DateHelper.dateAsString(new Date(), MS_SHORT_DATE), row.get(0).getCell("Credit Note Date").getText());
        runtimeState.scenario.write("Asserting Net Amount in the table is: " + testData.getString("netAmount"));
        assertEquals("Unexpected Net Amount. ", testData.getString("netAmount"), row.get(0).getCell(localize("Net Amount ($)")).getText());
        runtimeState.scenario.write("Asserting "+ localize("Vat") +" Amount in the table is: " + testData.getString("taxAmount"));
        assertEquals("Unexpected Tax Amount. ", testData.getString("taxAmount"), row.get(0).getCell(""+ localize("Vat") +" Amount ("+ localize("$") +")").getText());
        runtimeState.scenario.write("Asserting Gross Amount in the table is: " + testData.getString("grossAmount"));
        assertEquals("Unexpected Gross Amount. ", testData.getString("grossAmount"), row.get(0).getCell(localize("Gross Amount ($)")).getText());
    }

    @And("^the credit note number is displayed as reference number in the top row of the credit note document table$")
    public void the_credit_note_number_is_displayed_as_reference_number_in_the_top_row_of_the_credit_note_document_table() throws Throwable {
        Grid grid = runtimeState.enterCreditNoteDetailsPage.getCreditNoteDocumentTable();
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");
        List<Row> row = grid.getRows();
        runtimeState.scenario.write("Asserting Reference Number in the credit note document table is: " + testData.getString("creditNoteNumber"));
        assertEquals("Unexpected Reference Number. ", testData.getString("creditNoteNumber"), row.get(0).getCell("Reference Number").getText());
    }


    @And("^the user selects add new line to credit note$")
    public void the_user_selects_add_new_line_to_credit_note() throws Throwable {
        runtimeState.addNewCreditNoteLineModal = runtimeState.enterCreditNoteDetailsPage.addNewLineToCreditNote();
    }

    public void the_part_number_is_entered() {
        String partNumber = String.valueOf(DataGenerator.randBetween(100000, 999999));
        testData.put("partNumber", partNumber);
        runtimeState.addNewCreditNoteLineModal.enterPartNumber(partNumber);
        runtimeState.scenario.write("Part Number entered is: " + partNumber);
    }

    @And("^the credit note line type is selected$")
    public void the_credit_note_line_type_is_selected() {
        runtimeState.addNewCreditNoteLineModal.selectCreditNotelineType();
        String type = runtimeState.addNewCreditNoteLineModal.getCreditNotelineType();
        if ("Parts".equalsIgnoreCase(type)) {
            the_part_number_is_entered();
            type = type + " ("
                    + testData.getString("partNumber")
                    + ")";
        }
        testData.put("type", type);
        runtimeState.scenario.write("Selected Credit note line type is: " + type);
    }

    @And("^the description is entered$")
    public void the_description_is_entered() throws ParseException {
        String description = DataGenerator.generateRandomSentence();
        testData.put("description", description);
        runtimeState.addNewCreditNoteLineModal.enterDescription(description);
        runtimeState.scenario.write("Entered test description: " + description);
    }

    public void the_quantity_is_entered() {
        String quantity = "1";
        testData.put("quantity", quantity);
        runtimeState.addNewCreditNoteLineModal.enterQuantity(quantity);
        runtimeState.scenario.write("Quantity entered in the credit note line is: " + quantity);
    }

    public void the_unit_price_is_entered() {
        runtimeState.addNewCreditNoteLineModal.enterUnitPrice(String.valueOf(testData.getString("netAmount")));
        runtimeState.scenario.write("Unit price entered in the credit note line is: " + testData.getString("netAmount"));
    }

    public void the_unit_tax_amount_is_entered() {
        runtimeState.addNewCreditNoteLineModal.enterUnitTaxAmount(String.valueOf(testData.getString("taxAmount")));
        runtimeState.scenario.write("Tax amount entered in the credit note line is: " + testData.getString("taxAmount"));
    }

    @And("^the user adds new line to credit note$")
    public void the_user_adds_new_line_to_credit_note() throws Throwable {
        the_user_selects_add_new_line_to_credit_note();
        the_credit_note_line_type_is_selected();
        the_description_is_entered();
        the_quantity_is_entered();
        the_unit_price_is_entered();
        the_unit_tax_amount_is_entered();
        commonSteps.the_button_is_clicked("Add");
    }

    @ContinueNextStepsOnException
    @Then("^the credit note line details entered by the user are displayed in the top row of the credit note line details table$")
    public void the_credit_note_line_details_entered_by_the_user_are_displayed_in_the_top_row_of_the_credit_note_line_details_table() throws Throwable {
        Grid grid = runtimeState.enterCreditNoteDetailsPage.getCreditNoteLineDetailsTable();
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");
        List<Row> row = grid.getRows();

        runtimeState.scenario.write("Asserting Credit Note Line Description in the table is: " + testData.getString("description"));
        assertEquals("Unexpected Credit Note Line Description. ", testData.getString("description"), row.get(0).getCell("Description").getText());

        runtimeState.scenario.write("Asserting Credit Note Line Type in the table is: " + testData.getString("type"));
        assertEquals("Unexpected Credit Note Line Type. ", testData.getString("type"), row.get(0).getCell("Type").getText());

        runtimeState.scenario.write("Asserting Credit Note Line Quantity in the table is: " + testData.getString("quantity") + ".00");
        assertEquals("Unexpected Credit Note Line Quantity. ", testData.getString("quantity") + ".00", row.get(0).getCell("Quantity").getText());

        runtimeState.scenario.write("Asserting Line Net Amount in the table is: " + testData.getString("netAmount"));
        assertEquals("Unexpected Line Net Amount. ", testData.getString("netAmount"), row.get(0).getCell(localize("Line Net ($)")).getText());

        runtimeState.scenario.write("Asserting Line "+ localize("Vat") +" Amount in the table is: " + testData.getString("taxAmount"));
        assertEquals("Unexpected Line "+ localize("Vat") +" Amount. ", testData.getString("taxAmount"), row.get(0).getCell("Line "+ localize("Vat") +" ("+ localize("$") +")").getText());

        runtimeState.scenario.write("Asserting Line Gross Amount in the table is: " + testData.getString("grossAmount"));
        assertEquals("Unexpected Line Gross Amount. ", testData.getString("grossAmount"), row.get(0).getCell(localize("Line Gross ($)")).getText());
    }

    @And("^the standalone credit note is created with the tax amount ((?:less|greater)) than the allowed percent of the net amount$")
    public void the_standalone_credit_note_is_created_with_the_tax_amount_less_than_something_percent_of_the_net_amount(String tax) throws Throwable {
        a_supplier_or_vendor_to_create_credit_note_for_is_selected();
        commonSteps.the_button_is_clicked("Next");
        the_user_fills_in_all_mandatory_fields_in_the_credit_note_page();
        the_tax_amount_greater_than_percent_of_the_net_amount_is_entered(tax);
        commonSteps.the_button_is_clicked("Next");
    }

    @And("^the user selects upload credit note document$")
    public void the_user_selects_upload_credit_note_document() throws Throwable {
        runtimeState.uploadCreditNoteDocumentPage = runtimeState.enterCreditNoteDetailsPage.clickUploadLink();
    }

    @And("^uploads a credit note document$")
    public void uploads_a_credit_note_document() throws Throwable {
        the_user_selects_upload_credit_note_document();
        runtimeState.uploadCreditNoteDocumentPage.setDocumentFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\credit-note-sample.pdf");
        runtimeState.scenario.write("Credit note document is uploaded");
        commonSteps.the_button_is_clicked("Upload File");
    }

    /**
     * This method checks if the credit note pdf and the data added while creating
     * the credit note are displayed correctly in the credit note approval page
     */
    /**
     * @throws Throwable
     */
    @ContinueNextStepsOnException
    @Then("^the credit note approval page is displayed with credit note details and embed credit note document$")
    public void the_credit_note_approval_page_is_displayed_with_credit_note_details_and_embed_credit_note_document() throws Throwable {
        // Testing the data in 'Credit Note' table displayed at the right of 'Credit
        // Note Approval' page
        runtimeState.scenario.write("Asserting data in credit note details table: ");

        runtimeState.scenario.write("Asserting Credit Note Ref is: " + testData.getString("creditNoteNumber"));
        assertEquals("Unexpected Credit Note Ref ", testData.getString("creditNoteNumber"), runtimeState.creditNoteApprovalPage.getCreditNoteTableData("Credit Note Ref"));
        
        runtimeState.scenario.write("Asserting Credit Note Date is: " + DateHelper.dateAsString(new Date(), MS_SHORT_DATE));
        assertEquals("Unexpected Credit Note Date ", DateHelper.dateAsString(new Date(), MS_SHORT_DATE), runtimeState.creditNoteApprovalPage.getCreditNoteTableData("Credit Note Date"));
        
        runtimeState.scenario.write("Asserting Credit Note Type is: Standalone");
        assertEquals("Unexpected Credit Note Type ", "Standalone", runtimeState.creditNoteApprovalPage.getCreditNoteTableData("Credit Note Type"));
        
        runtimeState.scenario.write("Asserting Supplier is: " + testData.getString("supplier"));
        assertEquals("Unexpected Supplier ", normalize(testData.getString("supplier")), runtimeState.creditNoteApprovalPage.getCreditNoteTableData("Supplier"));
       
        runtimeState.scenario.write("Asserting Net (" + CURRENCY_SYMBOL + ") is: " + testData.getString("netAmount"));
        assertEquals("Unexpected Net (" + CURRENCY_SYMBOL + ")", testData.getString("netAmount"), runtimeState.creditNoteApprovalPage.getCreditNoteTableData("Net (" + CURRENCY_SYMBOL + ")"));
        
        runtimeState.scenario.write("Asserting " + localize("Vat") + " (" + CURRENCY_SYMBOL + ") is: " + testData.getString("taxAmount"));
        assertEquals("Unexpected " + localize("Vat") + " (" + CURRENCY_SYMBOL + ")", testData.getString("taxAmount"), runtimeState.creditNoteApprovalPage.getCreditNoteTableData("" + localize("Vat") + " (" + CURRENCY_SYMBOL + ")"));
        
        runtimeState.scenario.write("Asserting Gross (" + CURRENCY_SYMBOL + ") is: " + testData.getString("grossAmount"));
        assertEquals("Unexpected Gross (" + CURRENCY_SYMBOL + ")", testData.getString("grossAmount"), runtimeState.creditNoteApprovalPage.getCreditNoteTableData("Gross (" + CURRENCY_SYMBOL + ")"));

        // Testing if the credit note pdf is displayed in the iframe. For this, checking
        // if the credit note url given in the iframe is valid and returns 200 response
        runtimeState.scenario.write("Asserting credit note document is displayed in the credit note approval page.");
        String pdfUrl = runtimeState.creditNoteApprovalPage.getPdfSrcFromIframe();
        logger.debug("pdfUrl: " + pdfUrl);
        resourceHelper.getDocumentAsResource(pdfUrl, testData.getString("resourceId"));
    }


    @And("^the Approve and Reject buttons are displayed in the credit note approval page$")
    public void the_approve_and_reject_buttons_are_displayed_in_the_credit_note_approval_page() throws Throwable {
        runtimeState.scenario.write("Asserting Approve and Reject buttons are displayed");
        assertTrue("Approve button is not displayed", runtimeState.creditNoteApprovalPage.isApproveButtonDisplayed());
        assertTrue("Reject button is not displayed", runtimeState.creditNoteApprovalPage.isRejectButtonDisplayed());

    }

    /**
     * Credit note lines modal is displayed on clicking 'Credit Note Lines' button
     * in credit note approval page. In this method, details in the 'Credit Note
     * Lines' modal are checked.
     */
    @ContinueNextStepsOnException
    @Then("^the credit note lines modal is displayed with credit note line details$")
    public void the_credit_note_lines_modal_is_displayed_with_credit_note_line_details() throws Throwable {
        // Checking the credit lines data displayed in the credit note lines modal
        runtimeState.scenario.write("Clicked on Credit Note Lines button to open Credit Note Lines modal and assert data in it");

        Grid grid = runtimeState.creditNoteLinesModal.getCreditNoteLinesTable();
        logger.debug(
                "Grid retrieved:" + grid.getHeaders().toString()
                + " ("
                + grid.getRows().size()
                + " rows)");
        List<Row> rows = grid.getRows();
        runtimeState.scenario.write("Asserting Description is: " + testData.getString("description"));
        assertEquals("Unexpected Description ", testData.getString("description"), rows.get(0).getCell("Description").getText());
        runtimeState.scenario.write("Asserting Type is: " + testData.getString("type"));
        assertEquals("Unexpected Type ", testData.getString("type"), rows.get(0).getCell("Type").getText());
        runtimeState.scenario.write("Asserting Quantity is: " + testData.getString("quantity"));
        assertEquals("Unexpected Quantity ", testData.getString("quantity"), rows.get(0).getCell("Quantity").getText());
        runtimeState.scenario.write("Asserting Unit Price (" + CURRENCY_SYMBOL + ") is: " + testData.getString("netAmount"));
        assertEquals("Unexpected Unit Price (" + CURRENCY_SYMBOL + ") ", testData.getString("netAmount"), rows.get(0).getCell(localize("Unit Price ($)")).getText());
        runtimeState.scenario.write("Asserting Unit Tax (" + CURRENCY_SYMBOL + ") is: " + testData.getString("taxAmount"));
        assertEquals("Unexpected Unit "+ localize("Vat") +" (" + CURRENCY_SYMBOL + ") ", testData.getString("taxAmount"), rows.get(0).getCell("Unit "+ localize("Vat") +" ("+ localize("$") +")").getText());

        runtimeState.scenario.write("Asserting Line Net (" + CURRENCY_SYMBOL + ") is: " + testData.getString("netAmount"));
        assertEquals("Unexpected Line Net (" + CURRENCY_SYMBOL + ") ", testData.getString("netAmount"), rows.get(0).getCell(localize("Line Net ($)")).getText());
        runtimeState.scenario.write("Asserting Line Tax (" + CURRENCY_SYMBOL + ") is: " + testData.getString("taxAmount"));
        assertEquals("Unexpected Line Tax (" + CURRENCY_SYMBOL + ") ", testData.getString("taxAmount"), rows.get(0).getCell("Line "+ localize("Vat") +" ("+ localize("$") +")").getText());
        runtimeState.scenario.write("Asserting Line Gross (" + CURRENCY_SYMBOL + ") is: " + testData.getString("grossAmount"));
        assertEquals("Unexpected Line Gross (" + CURRENCY_SYMBOL + ") ", testData.getString("grossAmount"), rows.get(0).getCell(localize("Line Gross ($)")).getText());
    }

    @And("^the credit note status in the database is \"([^\"]*)\"$")
    public void the_credit_note_status_in_the_database_is(String creditNoteStatus) throws Throwable {
        runtimeState.scenario.write("Asserting Credit Note status is: " + creditNoteStatus);
        assertEquals("Unexpected status ", creditNoteStatus, dbHelperInvoices.getCreditNoteStatus(testData.getString("creditNoteNumber")));
    }

    @And("^the rejection reason is selected$")
    public void the_rejection_reason_is_selected() throws Throwable {
        runtimeState.rejectCreditNoteModal.selectRandomRejectionReason();
        String rejectionReason = runtimeState.rejectCreditNoteModal.getRejectionReason();
        testData.put("rejectionReason", rejectionReason);
        runtimeState.scenario.write("Selected Rejection reason is: " + rejectionReason);
    }

    @And("^the rejection notes are entered$")
    public void the_rejection_notes_are_entered() throws Throwable {
        String rejectionNotes = DataGenerator.generateRandomSentence();
        runtimeState.rejectCreditNoteModal.enterRejectionNotes(rejectionNotes);
        testData.put("rejectionNotes", rejectionNotes);
        runtimeState.scenario.write("Test Rejection notes: " + rejectionNotes);
    }

    @And("^the user rejects the standalone credit note with reason and notes$")
    public void the_user_rejects_the_standalone_credit_note_with_reason_and_notes() throws Throwable {
        the_rejection_reason_is_selected();
        the_rejection_notes_are_entered();
        commonSteps.the_button_is_clicked("Save");
    }

    @And("^the rejection reason and notes are stored in the database$")
    public void the_rejection_reason_and_notes_are_stored_in_the_database() throws Throwable {
        Map<String, Object> dbData = dbHelperInvoices.getStandaloneCreditNoteRejectionReasonAndNotes(testData.getString("creditNoteNumber"));

        runtimeState.scenario.write("Asserting rejection reason stored in the database is: " + testData.getString("rejectionReason"));
        assertEquals("Unexpected Rejection Reason ", testData.getString("rejectionReason"), dbData.get("RejectionReason"));

        runtimeState.scenario.write("Asserting rejection notes stored in the database is: " + testData.getString("rejectionNotes"));
        assertEquals("Unexpected Rejection Notes ", testData.getString("rejectionNotes"), dbData.get("RejectionNotes"));
    }
}
