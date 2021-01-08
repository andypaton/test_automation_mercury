package mercury.steps.portal.invoices;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.MS_SHORT_DATE;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.helpers.Globalisation.TAX_RATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.Globalisation.toCurrency;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
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
import mercury.helpers.PortalInvoiceHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.portal.invoices.AddOrderLinesToInvoiceConfirmationModal;
import mercury.pageobject.web.portal.invoices.EditInvoiceLineConfirmationModal;
import mercury.pageobject.web.portal.invoices.InvoiceDetailsPage;
import mercury.pageobject.web.portal.invoices.ManageInvoiceLinesModal;
import mercury.pageobject.web.portal.invoices.NewInvoicingEditInvoiceLineModal;
import mercury.pageobject.web.portal.invoices.PortalOrdersInvoicePage;
import mercury.pageobject.web.portal.invoices.UploadInvoiceDocumentPage;
import mercury.pageobject.web.portal.invoices.UploadInvoiceDocumentsPage;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;

public class PortalOrdersAwaitingInvoiceSteps {
    private static final Logger logger = LogManager.getLogger();

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private CommonSteps commonSteps;
    @Autowired private PortalInvoiceHelper portalInvoiceHelper;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private TzHelper tzHelper;
    @Autowired private DbHelperJobs dbHelperJobs;

    @And("^the order awaiting invoice is searched for and opened$")
    public void the_order_awaiting_invoice_is_searched_for_and_opened() throws Throwable {
        // Searches for an order awaiting invoice upload and if it is a contractor admin it looks at the job date to input
        // into invoice details and if it is supplier then it uses order date
        // Search for a job
        Grid grid;

        logger.debug("Opening Job Reference: " + testData.getInt("jobReference"));

        //  outputHelper.takeScreenshots();
        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName").trim())) {
            runtimeState.ordersAwaitingInvoicePage.searchOrders(testData.getInt("jobReference").toString());
            // use grid to get order value
            grid = runtimeState.ordersAwaitingInvoicePage.getGrid();
            String orderValue = grid.getRows().get(0).getCell("Order Value (" + CURRENCY_SYMBOL + ")").getText();
            String orderRef = grid.getRows().get(0).getCell("Order Ref").getText();
            String orderDate = grid.getRows().get(0).getCell("Order Date").getText();
            String jobDate = grid.getRows().get(0).getCell("Job Date").getText();
            testData.addStringTag("orderValue", orderValue);
            testData.addIntegerTag("orderRef", Integer.valueOf(orderRef));
            testData.addStringTag("orderDate", orderDate);
            testData.addStringTag("jobDate", jobDate);
            runtimeState.ordersAwaitingInvoicePage.openOrderAwaitingInvoice(testData.getInt("jobReference").toString());
        } else {
            // Need to get the PO number from the job reference so it can be opened
            Integer poNumber = dbHelperInvoices.getOrderRefNumber(testData.getInt("jobReference"), testData.getString("userName"));
            testData.put("poNumber", poNumber);
            runtimeState.ordersAwaitingInvoicePage.searchOrders(poNumber.toString());
            //use grid to get supplier order value
            grid = runtimeState.ordersAwaitingInvoicePage.getSupplyOnlyGrid();
            String orderValue = grid.getRows().get(0).getCell("Order Value (" + CURRENCY_SYMBOL + ")").getText();
            String orderRef = grid.getRows().get(0).getCell("Order Ref").getText();
            String orderDate = grid.getRows().get(0).getCell("Order Date").getText();
            testData.addStringTag("orderValue", orderValue);
            testData.addIntegerTag("orderRef", Integer.valueOf(orderRef));
            testData.addStringTag("orderDate", orderDate);
            runtimeState.ordersAwaitingInvoicePage.openSupplierOrderAwaitingInvoice(poNumber.toString());
        }

        if (getWebDriver().getTitle().equalsIgnoreCase("Invoice")) {
            runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();
        }
    }

    @And("^the invoice in progress is searched for and opened$")
    public void the_invoice_is_searched_for_and_opened() throws Throwable {
        Grid grid;

        runtimeState.scenario.write("Opening Invoice Number: " + testData.getString("invoiceNumber"));
        outputHelper.takeScreenshots();

        runtimeState.consolidatedInvoicesInProgressPage.searchInvoices(testData.getString("invoiceNumber"));
        // use grid to get invoice value
        grid = runtimeState.consolidatedInvoicesInProgressPage.getGrid();
        String invoiceNumber = grid.getRows().get(0).getCell("Invoice Number").getText();
        String invoiceDate = grid.getRows().get(0).getCell("Invoice Date").getText();
        String numberOfOrders = grid.getRows().get(0).getCell("Number of Orders").getText();
        String netAmount = grid.getRows().get(0).getCell("Net Amount").getText();
        String vatAmount = grid.getRows().get(0).getCell("Vat Amount").getText();
        String grossAmount = grid.getRows().get(0).getCell("Gross Amount").getText();
        testData.put("netAmount", netAmount);
        testData.put("grossAmount", grossAmount);
        testData.put("numberOfOrders", Integer.valueOf(numberOfOrders));
        testData.put("invoiceDate", invoiceDate);
        testData.put("vatAmount", vatAmount);
        runtimeState.consolidatedInvoicesInProgressPage.openConsolidatedInvoiceInProgress(invoiceNumber);
        outputHelper.takeScreenshots();
    }

    @And("^the \"([^\"]*)\" is searched for and opened$")
    public void the_order_awaiting_invoice_is_searched_for_and_opened(String invoiceType) throws Exception {
        // Searches for an order awaiting invoice and if it is a contractor admin it looks at the job date to input
        // into invoice details and if it is supplier then it uses order date
        // Search for a job
        Grid grid;
        logger.debug("Opening Job Reference: " + testData.getInt("jobReference"));
        if ("Supplier Orders Awaiting Invoice".equalsIgnoreCase(invoiceType)) {
            // Get order reference from the job reference provided
            testData.addIntegerTag("orderRef", dbHelperInvoices.getOrderRefNumberForNewSupplierInvoice(testData.getInt("jobReference"), testData.getInt("resourceId"), testData.getString("jobStatus")));
            runtimeState.ordersAwaitingInvoicePage.searchOrders(testData.getString("orderRef"));
        } else {
            runtimeState.ordersAwaitingInvoicePage.searchOrders(testData.getString("jobReference"));
        }
        outputHelper.takeScreenshots();

        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName").trim())) {
            // use grid to get order value
            grid = runtimeState.ordersAwaitingInvoicePage.getGrid();
            String orderValue = grid.getRows().get(0).getCell("Order Value (" + CURRENCY_SYMBOL + ")").getText();
            String orderRef = grid.getRows().get(0).getCell("Order Ref").getText();
            String orderDate = grid.getRows().get(0).getCell("Order Date").getText();
            String jobDate = grid.getRows().get(0).getCell("Job Date").getText();
            testData.addStringTag("orderValue", orderValue);
            testData.addIntegerTag("orderRef", Integer.valueOf(orderRef));
            testData.addStringTag("orderDate", orderDate);
            testData.addStringTag("jobDate", jobDate);
            runtimeState.ordersAwaitingInvoicePage.openOrderAwaitingInvoice(testData.getInt("jobReference").toString());

        } else {
            // use grid to get order value
            grid = runtimeState.ordersAwaitingInvoicePage.getSupplyOnlyGrid();
            String orderValue = grid.getRows().get(0).getCell("Order Value (" + CURRENCY_SYMBOL + ")").getText();
            String orderRef = grid.getRows().get(0).getCell("Order Ref").getText();
            String orderDate = grid.getRows().get(0).getCell("Order Date").getText();
            testData.addStringTag("orderValue", orderValue);
            testData.addIntegerTag("orderRef", Integer.valueOf(orderRef));
            testData.addStringTag("orderDate", orderDate);
            runtimeState.ordersAwaitingInvoicePage.openSupplierOrderAwaitingInvoice(testData.getInt("orderRef").toString());
        }

        if (getWebDriver().getTitle().equalsIgnoreCase("Invoice")) {
            runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the user is taken to the invoice page$")
    public void the_user_is_taken_to_the_invoice_page() throws Throwable {
        // User is taken to the invoice page which shows the documents uploaded and the invoice lines
        logger.debug("Get the job");
        logger.debug(testData.getInt("resourceId"));
        runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();
        assertEquals("Unexpected Page Heading", "Enter Invoice Details", runtimeState.portalOrdersInvoicePage.getInvoiceDetailsHeader());
        assertEquals("Unexpected Page Title", "Invoice", runtimeState.portalOrdersInvoicePage.getInvoiceTitle());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Manage Lines link is displayed$")
    public void the_manage_lines_link_is_displayed() {
        assertTrue("The Manage Lines link is not present", runtimeState.portalOrdersInvoicePage.isManageLinesButtonDisplayed());
    }

    @When("^the user clicks on manage lines$")
    public void the_user_clicks_on_manage_lines() {
        // TODO: Use the grid helper when the devs have fixed the html for the button (not used the common steps either)
        runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();
        Grid materialLinesGrid = runtimeState.portalOrdersInvoicePage.getMaterialsLinesGrid();
        testData.addIntegerTag("materialLinesGridSize", materialLinesGrid.getRows().size());
        Grid laborLinesGrid = runtimeState.portalOrdersInvoicePage.getLaborLinesGrid();
        testData.addIntegerTag("laborLinesGridSize", laborLinesGrid.getRows().size());
        runtimeState.portalOrdersInvoicePage.clickManageLines();
    }

    @When("^a \"([^\"]*)\" is added$")
    public void a_line_is_added(String lineType) throws Throwable {
        int rowSize;
        commonSteps.the_modal_is_displayed("Manage Lines");
        outputHelper.takeScreenshots();

        portalInvoiceHelper.completeMandatoryLines(runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid());
        portalInvoiceHelper.completeMandatoryLines(runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid());

        if("Materials Line".equalsIgnoreCase(localize(lineType))) {
            Grid grid = runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid();
            assertNotNull("Unexpected Null Grid", grid);
            rowSize = grid.getRows().size();
            commonSteps.the_button_is_clicked("Add Materials Lines");
        } else {
            Grid grid = runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid();
            assertNotNull("Unexpected Null Grid", grid);
            rowSize = grid.getRows().size();
            commonSteps.the_button_is_clicked("Add " + localize(lineType).substring(0, localize(lineType).indexOf(' ')) + " Lines");
        }
        testData.addIntegerTag("invoiceLinesRowSize", rowSize);
    }

    @When("^a new line with \"([^\"]*)\" and tax ((?:less|greater|either less or greater)) than \"([^\"]*)\" percent is added to the \"([^\"]*)\"$")
    public void a_new_line_with_type_is_added_to_the_linetype(String type, String tax, String percent, String lineType) throws Throwable {

        switch (localize(lineType)) {
        case "Materials Line":
            portalInvoiceHelper.enterMaterialsLineDetails(type, tax, percent, localize(lineType));
            break;

        case "Labor Line":
        case "Labour Line":
            portalInvoiceHelper.enterLaborLineDetails(type, tax, percent, localize(lineType));
            break;

        default:
            throw new Exception("Unexpected line type: " +localize(lineType));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" type is disabled$")
    public void the_linetype_type_is_disabled(String lineType) throws Throwable {
        commonSteps.the_modal_is_displayed("Manage Lines");
        testData.addStringTag("lineType", localize(lineType));
        switch (localize(lineType)) {
        case "Materials Line":
            assertTrue("None of the " + localize(lineType) + " type row(s) are disabled.", portalInvoiceHelper.isTypeDisabled(runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid()));
            break;

        case "Labor Line":
        case "Labour Line":
            assertTrue("None of the " + localize(lineType) + " type row(s) are disabled.", portalInvoiceHelper.isTypeDisabled(runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid()));
            break;

        default:
            throw new Exception("Unexpected line type: " +localize(lineType));
        }
    }

    @ContinueNextStepsOnException
    @Then("^a line is added to the \"([^\"]*)\" invoice lines$")
    public void a_line_is_added_to_the_linetype_invoice_lines(String lineType) throws Throwable {
        Grid grid;
        int rowSize;
        switch (localize(lineType)) {
        case "Materials Line":
            grid = runtimeState.portalOrdersInvoicePage.getMaterialsLinesGrid();
            rowSize = grid.getRows().size();
            assertNotNull("Unexpected Null Grid", grid);
            Row materialsLineRow = grid.getRows().get(rowSize - 1);
            String materialsLineTax = BigDecimal.valueOf(Float.valueOf(testData.getString("quantity")) * testData.getFloat("materialsLineTaxAmount")).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
            assertEquals("Unexpected type value in line", testData.getString("typeValue") + " (" + testData.getString("partCode") + ")", materialsLineRow.getCell("Type").getText());
            assertEquals("Unexpected description value in line", testData.getString("description"), materialsLineRow.getCell("Description").getText());
            assertEquals("Unexpected quantity in line", testData.getString("quantity") + ".00", materialsLineRow.getCell("Quantity").getText());
            assertEquals("Unexpected unit price in line", toCurrency(new BigDecimal(testData.getString("unitPrice"))), toCurrency(new BigDecimal(materialsLineRow.getCell(localize("Unit Net ($)")).getText().replace(",", ""))));
            assertEquals("Unexpected tax in line", toCurrency(new BigDecimal(materialsLineTax)), toCurrency(new BigDecimal(materialsLineRow.getCell("Line "+ localize("Vat") +" ("+ localize("$") +")").getText().replace(",", ""))));
            break;

        case "Labor Line":
        case "Labour Line":
            grid = runtimeState.portalOrdersInvoicePage.getLaborLinesGrid();
            rowSize = grid.getRows().size();
            assertNotNull("Unexpected Null Grid", grid);
            Row laborLineRow = grid.getRows().get(rowSize - 1);
            String laborLineTax = BigDecimal.valueOf(Float.valueOf(testData.getString("hour")) * testData.getFloat("laborLineTaxAmount")).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
            assertEquals("Unexpected type value in line", testData.getString("typeValue"), laborLineRow.getCell("Type").getText());
            assertEquals("Unexpected description value in line", testData.getString("description"), laborLineRow.getCell("Description").getText());
            assertEquals("Unexpected hours in line", testData.getString("hour") + ".00", laborLineRow.getCell("Hours").getText());
            assertEquals("Unexpected unit price in line", toCurrency(new BigDecimal(testData.getString("rate"))), toCurrency(new BigDecimal(laborLineRow.getCell(localize("Unit Net ($)")).getText().replace(",", ""))));
            assertEquals("Unexpected tax in line", toCurrency(new BigDecimal(laborLineTax)), toCurrency(new BigDecimal(laborLineRow.getCell("Line "+ localize("Vat") +" ("+ localize("$") +")").getText().replace(",", ""))));
            break;

        default:
            throw new Exception("Unexpected line type: " + localize(lineType));
        }
    }

    @When("^the user edits the \"([^\"]*)\"$")
    public void the_user_edits_the_linetype(String lineType) throws Throwable {
        commonSteps.the_modal_is_displayed("Manage Lines");
        switch (localize(lineType)) {
        case "Materials Line":
            if (portalInvoiceHelper.isAllLinesDisabled(runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid()) == true) {
                a_line_is_added(localize(lineType));
                a_new_line_with_type_is_added_to_the_linetype(dbHelperInvoices.getRandomMaterialsLineType(), "less", String.valueOf(TAX_RATE.intValue()), localize(lineType));
                the_user_clicks_on_manage_lines();
            }
            portalInvoiceHelper.editMaterialsLineDetails(localize(lineType));
            break;

        case "Labor Line":
        case "Labour Line":
            if (portalInvoiceHelper.isAllLinesDisabled(runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid()) == true) {
                a_line_is_added(localize(lineType));
                a_new_line_with_type_is_added_to_the_linetype(dbHelperInvoices.getRandomLaborLineType(), "less", String.valueOf(TAX_RATE.intValue()), localize(lineType));
                the_user_clicks_on_manage_lines();
            }
            portalInvoiceHelper.editLaborLineDetails(localize(lineType));
            break;

        default:
            throw new Exception("Unexpected line type: " + localize(lineType));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" line is updated$")
    public void the_linetype_line_is_updated(String lineType) throws Throwable {
        Grid grid;
        switch (localize(lineType)) {
        case "Materials Line":
            grid = runtimeState.portalOrdersInvoicePage.getMaterialsLinesGrid();
            assertNotNull("Unexpected Null Grid", grid);
            BigDecimal materialsLineNet = BigDecimal.valueOf(Float.valueOf(testData.getString("quantity")) * Float.valueOf(testData.getString("unitPrice"))).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal materialsLineTax = BigDecimal.valueOf(Float.valueOf(testData.getString("quantity")) * Float.valueOf(testData.getString("unitTax"))).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal materialsLineGross = materialsLineNet.add(materialsLineTax);
            List<Row> materialsLineRows = grid.getRows();

            for (int index = 0; index < materialsLineRows.size(); index++) {
                if (testData.getString("quantity").equalsIgnoreCase(materialsLineRows.get(index).getCell(2).getText()) && materialsLineTax.equals(new BigDecimal(materialsLineRows.get(index).getCell(4).getText()))) {
                    assertEquals("Unexpected description in line", testData.getString("description"), materialsLineRows.get(index).getCell("Description").getText());
                    assertEquals("Unexpected quantity in line", testData.getString("quantity"), materialsLineRows.get(index).getCell("Quantity").getText());
                    assertEquals("Unexpected tax in line", toCurrency(materialsLineTax), toCurrency(new BigDecimal(materialsLineRows.get(index).getCell("Line "+ localize("Vat") +" ("+ localize("$") +")").getText().replace(",", ""))));
                    assertEquals("Unexpected gross in line", toCurrency(materialsLineGross), toCurrency(new BigDecimal(materialsLineRows.get(index).getCell(localize("Line Gross ($)")).getText().replace(",", ""))));
                    break;
                }
            }
            break;

        case "Labor Line":
        case "Labour Line":
            grid = runtimeState.portalOrdersInvoicePage.getLaborLinesGrid();
            assertNotNull("Unexpected Null Grid", grid);
            List<Row> laborLineRows = grid.getRows();
            for (int index = 0; index < laborLineRows.size(); index++) {
                if (testData.getString("description").equalsIgnoreCase(laborLineRows.get(index).getCell(1).getText())) {
                    assertEquals("Unexpected description in line", testData.getString("description"), laborLineRows.get(index).getCell("Description").getText());
                    break;
                }
            }
            break;

        default:
            throw new Exception("Unexpected line type: " + localize(lineType));
        }
    }

    @And("^the \"([^\"]*)\" N/A checkbox is clicked and description entered$")
    public void the_linetype_na_checkbox_is_selected(String lineType) throws Throwable {
        testData.addStringTag("lineType", localize(lineType));
        commonSteps.the_modal_is_displayed("Manage Lines");
        boolean selected;
        switch (localize(lineType)) {
        case "Materials Line":
            selected = portalInvoiceHelper.clickNotApplicableCheckBox(runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid());
            portalInvoiceHelper.enterDescription(runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid(), selected, localize(lineType));
            break;

        case "Labor Line":
        case "Labour Line":
            selected = portalInvoiceHelper.clickNotApplicableCheckBox(runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid());
            portalInvoiceHelper.enterDescription(runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid(), selected, localize(lineType));
            break;

        default:
            throw new Exception("Unexpected line type: " + localize(lineType));
        }
    }

    @When("^the user deletes a \"([^\"]*)\"$")
    public void the_user_delete_the_linetype(String lineType) throws Throwable {
        commonSteps.the_modal_is_displayed("Manage Lines");
        switch (localize(lineType)) {
        case "Materials Line":
            //Adding the line first then performing the deletion
            a_line_is_added(localize(lineType));
            a_new_line_with_type_is_added_to_the_linetype(dbHelperInvoices.getRandomMaterialsLineType(), "less", "14" , localize(lineType));
            the_user_clicks_on_manage_lines();
            portalInvoiceHelper.deleteMaterialLine(localize(lineType));
            break;

        case "Labor Line":
        case "Labour Line":
            //Adding the line first then performing the deletion
            a_line_is_added(localize(lineType));
            a_new_line_with_type_is_added_to_the_linetype(dbHelperInvoices.getRandomLaborLineType(), "less", "14" , localize(lineType));
            the_user_clicks_on_manage_lines();
            portalInvoiceHelper.deleteLaborLine(localize(lineType));
            break;

        default:
            throw new Exception("Unexpected line type: " + localize(lineType));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" line is deleted$")
    public void the_linetype_line_is_deleted(String lineType) throws Exception {
        Grid grid;
        runtimeState.scenario.write("Verifying " + localize(lineType) + " only has " + (testData.getInt("materialLinesGridSize") - 1) + " rows ");
        switch (localize(lineType)) {
        case "Materials Line":
            grid = runtimeState.portalOrdersInvoicePage.getMaterialsLinesGrid();
            assertNotNull("Unexpected Null Grid", grid);
            if (testData.getInt("materialLinesGridSize") == 1) {
                assertEquals("There are no Materials Related Invoice Lines.", grid.getRows().get(0).getCell(0).getText());
            } else {
                assertEquals("There are no Materials Related Invoice Lines.", testData.getInt("materialLinesGridSize") - 1, grid.getRows().size());
            }
            break;

        case "Labor Line":
        case "Labour Line":
            grid = runtimeState.portalOrdersInvoicePage.getLaborLinesGrid();
            assertNotNull("Unexpected Null Grid", grid);
            if (testData.getInt("laborLinesGridSize") == 1) {
                assertEquals("There are no Labor Related Invoice Lines.", grid.getRows().get(0).getCell(0).getText());
            } else {
                assertEquals("There are no Labor Related Invoice Lines.", testData.getInt("laborLinesGridSize") - 1, grid.getRows().size());
            }
            break;

        default:
            throw new Exception("Unexpected line type: " + localize(lineType));
        }
    }

    @ContinueNextStepsOnException
    @Then("^a new editable line is added to the \"([^\"]*)\" table$")
    public void a_new_editable_line_is_added_to_the_table(String lineType) throws Throwable {
        int rowSize;
        if("Materials Line".equalsIgnoreCase(localize(lineType))) {
            Grid grid = runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid();
            assertNotNull("Unexpected Null Grid", grid);
            rowSize = grid.getRows().size();
        } else {
            Grid grid = runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid();
            assertNotNull("Unexpected Null Grid", grid);
            rowSize = grid.getRows().size();
        }
        assertEquals("Unexpected row size", testData.getInt("invoiceLinesRowSize") + 1, rowSize);
    }

    @ContinueNextStepsOnException
    @Then("^the user is taken to the upload documents page$")
    public void the_user_is_taken_to_the_upload_documents_page() throws Throwable {
        // This page is the page the user is taken to when both the job sheet and invoice is not already uploaded
        logger.debug("Get the job");
        logger.debug(testData.getInt("resourceId"));
        runtimeState.uploadInvoiceDocumentsPage = new UploadInvoiceDocumentsPage(getWebDriver()).get();
        assertEquals("Unexpected Page Title", "Upload Invoice Documents", runtimeState.uploadInvoiceDocumentsPage.getUploadInvoiceDocumentsTitle());
        assertEquals("Unexpected Page Heading", "Upload Invoice Files", runtimeState.uploadInvoiceDocumentsPage.getUploadInvoiceFileHeading());
        assertEquals("Unexpected Sub Page Heading", "Invoice File", runtimeState.uploadInvoiceDocumentsPage.getInvoiceFileHeader());
        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName"))) {
            assertEquals("Unexpected page Sub heading", "Job Sheet File", runtimeState.uploadInvoiceDocumentsPage.getJobSheetFile());
        }
    }

    @And("^the user uploads invoice documents$")
    public void the_user_uploads_invoice_documents() throws Throwable {
        // This is when no documents are present on the invoice and if it is a supplier uploading the doc then only an invoice
        // is uploaded
        // If it is a contractor then a job sheet is also uploaded
        runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName"))) {
            runtimeState.uploadInvoiceDocumentsPage.setJobSheetFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
        }
        outputHelper.takeScreenshots();
        POHelper.waitForAngularRequestsToFinish();
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentsPage.selectUploadFiles();
    }

    @And("^the user uploads consolidated invoice document$")
    public void the_user_uploads_consolidated_invoice_document() throws Throwable {
        // Only an invoice file is uploaded for consolidated invoicing
        runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
        outputHelper.takeScreenshots();
        POHelper.waitForAngularRequestsToFinish();
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentsPage.selectUploadFiles();
    }

    @And("^the user uploads valid invoice template documents$")
    public void the_user_uploads_valid_invoice_template_documents() throws Throwable {
        // This is when no documents are present on the invoice and if it is a supplier uploading the doc then only an ocr
        // template invoice is uploaded


        // If it is a contractor then a job sheet is also uploaded
        runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\OCRinvoiceTemplate.pdf");
        outputHelper.takeScreenshots();
        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName"))) {
            runtimeState.uploadInvoiceDocumentsPage.setJobSheetFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\OCRinvoiceTemplate.pdf");
            outputHelper.takeScreenshots();
        }
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentsPage.selectUploadFiles();
        outputHelper.takeScreenshots();
    }

    @And("^the Invoice Details page is displayed$")
    public void the_invoice_details_page_is_displayed() throws Throwable {
        // This is the page which allows the user to make update the invoice details upon upload
        assertEquals("Unexpected Page Title", "Invoice Details", runtimeState.invoiceDetailsPage.getInvoiceDetailsTitle());
        assertTrue("Unexpected View ", runtimeState.invoiceDetailsPage.isInvoiceDocumentViewVisible());
        assertTrue("Unexpected table", runtimeState.invoiceDetailsPage.isInvoiceTableVisible());
        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName"))) {
            assertEquals("Unexpected Job Reference", testData.getInt("jobReference"), runtimeState.invoiceDetailsPage.getInvoiceJobRef());
        } else {
            assertEquals("Unexpected Job Reference", testData.getInt("orderRef"), runtimeState.invoiceDetailsPage.getSupplierInvoiceJobRef());
        }
    }

    @And("^the Consolidated Invoice Details page is displayed$")
    public void the_consolidated_invoice_details_page_is_displayed() throws Throwable {
        // This is the page which allows the user to update the invoice details upon upload
        assertEquals("Unexpected Page Title", "Invoice Details", runtimeState.invoiceDetailsPage.getInvoiceDetailsTitle());
        assertTrue("Unexpected View ", runtimeState.invoiceDetailsPage.isInvoiceDocumentViewVisible());
        assertTrue("Unexpected table", runtimeState.invoiceDetailsPage.isInvoiceTableVisible());
    }

    @ContinueNextStepsOnException
    @Then("^the legal entity text box shows \"([^\"]*)\"$")
    public void the_legal_entity_text_box_shows(String text) {
        assertEquals("Unexpected placeholder text", text, runtimeState.invoiceDetailsPage.getLegalEntityPlaceHolderText());
    }

    @And("^an invoice number is entered$")
    public void an_invoice_number_is_entered() throws Throwable {
        // Invoice number is created with the job ref and then a random string to make the number unique
        String randomInvoiceNumber = testData.getInt("jobReference") + DataGenerator.GenerateRandomString(13, 13, 3, 0, 10, 0);
        runtimeState.invoiceDetailsPage.setInvoiceNumber(String.valueOf(randomInvoiceNumber));
        testData.addStringTag("newInvoiceNumber", (randomInvoiceNumber));
    }

    @And("^an Invoice date is entered which is after the Job date$")
    public void an_invoice_date_is_entered_which_is_after_the_job_date() throws Throwable {
        // This uses the job date for the invoice date for a contractor and the order date for a supplier
        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName").trim())) {
            runtimeState.invoiceDetailsPage.setInvoiceDate(DateHelper.getDateInFormat(new Date(), SHORT_DATE));
        } else {
            runtimeState.invoiceDetailsPage.setInvoiceDate(DateHelper.getDateInFormat(new Date(), SHORT_DATE));
        }
    }

    @And("^a net amount is entered$")
    public void a_net_amount_is_entered() throws Throwable {
        // The net amount is taken from the order value on the orders awaiting invoice page
        runtimeState.invoiceDetailsPage.setInvoiceNetAmount(testData.getString("orderValue"));
        runtimeState.scenario.write("The invoice net amount is entered: " + testData.getString("orderValue"));
    }

    @And("^a new invoice is added to the invoice page$")
    public void a_new_invoice_is_added_to_the_invoice_Page() throws Throwable {
        assertTrue(dbHelperInvoices.isNewInvoiceCreated(testData.getString("newInvoiceNumber")));
    }

    @And("^the user select add new line to invoice$")
    public void the_user_selects_add_new_line_to_invoice() throws Throwable {
        runtimeState.addNewInvoiceLineModal = runtimeState.portalOrdersInvoicePage.addNewInvoiceLine();
    }

    @And("^the user selects \"([^\"]*)\" type for the invoice line$")
    public void the_user_selects_type_for_the_invoice_line(String type) throws Throwable {
        // Invoice number is created with the job ref and then a random string to make the number unique
        String randomInvoiceNumber = DataGenerator.GenerateRandomString(8, 8, 2, 0, 9, 0);
        if (type.equalsIgnoreCase("random")) {
            type = dbHelperInvoices.getRandomSupplierInvoiceLineType();
        }
        runtimeState.addNewInvoiceLineModal.selectAnInvoiceType(type);
        if ("Parts".equalsIgnoreCase(type)) {
            runtimeState.addNewInvoiceLineModal.enterPartNumber(randomInvoiceNumber);
        }
    }

    @And("^the user enters a description to the invoice line$")
    public void the_user_enters_a_description_to_the_invoice_line() throws Throwable {
        // A random note is added to the description
        String notes = "Please enter some notes to verify the database update " + DateHelper.dateAsString(new Date());
        testData.addStringTag("notes", notes);
        runtimeState.scenario.write("Adding Notes: " + notes);
        runtimeState.addNewInvoiceLineModal.enterDescription(notes);
    }

    @And("^the user enters a quantity to the invoice line$")
    public void the_user_enters_a_quantity_to_the_invoice_line() throws Throwable {
        runtimeState.addNewInvoiceLineModal.enterQuantity("1");
    }

    @And("^the user enters a unit price to the invoice line$")
    public void the_user_enters_a_unit_price_to_the_invoice_line() throws Throwable {
        runtimeState.addNewInvoiceLineModal.enterUnitPrice(testData.getString("orderValue"));
    }

    @And("^the user enters a unit price equal to the net value to the invoice line$")
    public void the_user_enters_a_unit_price_equal_to_net_value_to_the_invoice_line() throws Throwable {
        DecimalFormat df = new DecimalFormat("#.00");
        runtimeState.addNewInvoiceLineModal.enterUnitPrice(df.format(Double.valueOf(testData.getString("invoiceNetValue"))));
        outputHelper.takeScreenshots();
    }

    @And("^the user enters a unit price less than net value to the invoice line$")
    public void the_user_enters_a_unit_price_less_than_net_value_to_the_invoice_line() throws Throwable {
        DecimalFormat df = new DecimalFormat("#.00");
        Double randomSelection = RandomUtils.nextDouble(0, Double.valueOf(testData.getString("orderValue")) - 1);
        runtimeState.addNewInvoiceLineModal.enterUnitPrice(df.format(randomSelection));
        outputHelper.takeScreenshots();
    }

    @And("^the user enters a unit price less than net value to the order line$")
    public void the_user_enters_a_unit_price_less_than_net_value_to_the_order_line() throws Throwable {
        Grid grid = runtimeState.addOrderLineToInvoiceModal.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");
        String unitPrice = grid.getRows().get(0).getCell("Unit Price (" + CURRENCY_SYMBOL + ")").getValue();

        DecimalFormat df = new DecimalFormat("#.00");
        Double randomSelection = RandomUtils.nextDouble(0, Double.valueOf(unitPrice) - 0.01);
        runtimeState.addOrderLineToInvoiceModal.setUnitPrice(df.format(randomSelection));
        outputHelper.takeScreenshots();
    }

    @And("^the user is able to add the invoice line$")
    public void the_user_is_able_to_add_the_invoice_line() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.portalOrdersInvoicePage = runtimeState.addNewInvoiceLineModal.addLine();
    }

    @ContinueNextStepsOnException
    @Then("^a new line of \"([^\"]*)\" type is added to the invoice$")
    public void a_new_line_of_type_is_added_to_the_invoice(String type) throws Throwable {
        the_user_selects_add_new_line_to_invoice();
        the_user_selects_type_for_the_invoice_line(type);
        the_user_enters_a_description_to_the_invoice_line();
        the_user_enters_a_quantity_to_the_invoice_line();
        the_user_enters_a_unit_price_to_the_invoice_line();
        the_user_is_able_to_add_the_invoice_line();
    }

    @And("^the user submits the invoice$")
    public void the_user_is_able_to_submit_the_invoice() throws Throwable {
        int invoicingLineFulfilled = dbHelperSystemToggles.getSystemSubFeatureToggle("invoicingLineFulfilled");
        int consolidatedInvoicing = dbHelperSystemToggles.getSystemSubFeatureToggle("consolidatedInvoicing");

        if (invoicingLineFulfilled == 1 || consolidatedInvoicing == 1) {
            the_user_updates_the_invoice_net_and_tax_amounts();

            runtimeState.invoiceDetailsPage = new InvoiceDetailsPage(getWebDriver()).get();
            if (runtimeState.invoiceDetailsPage.getInvoiceNumber().isEmpty()) {
                runtimeState.invoiceDetailsPage.setInvoiceNumber(String.valueOf(RandomUtils.nextInt(100000, 999999)));
            }
            if (runtimeState.invoiceDetailsPage.getInvoiceDate().isEmpty()) {
                runtimeState.invoiceDetailsPage.setInvoiceDate(DateHelper.getDateInFormat(new Date(), SHORT_DATE));
            }
            if (runtimeState.invoiceDetailsPage.getLegalEntity().isEmpty()) {
                runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
            }
            runtimeState.invoiceDetailsPage.clickSave();

            if (runtimeState.invoiceDetailsPage != null && runtimeState.invoiceDetailsPage.isInvoiceErrorMessageDisplayed()) {
                String netAmount = runtimeState.invoiceDetailsPage.getNetAmount();
                dbHelperJobs.updateOrderValue(testData.getInt("jobReference"), netAmount);
                runtimeState.invoiceDetailsPage.clickSave();
            }
        }
        runtimeState.invoiceSubmittedPage = runtimeState.portalOrdersInvoicePage.submitInvoice();

        outputHelper.takeScreenshots();
    }

    @When("^the total net value of the order lines equals the invoice net value$")
    public void the_total_net_value_of_the_order_lines_equals_the_invoice_net_value() throws Throwable {
        Grid grid = runtimeState.portalOrdersInvoicePage.getInvoiceHeaderGrid();
        assertNotNull("Unexpected Null Grid", grid);
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");
        String invoiceNetValue = grid.getRows().get(0).getCell("Net (" + CURRENCY_SYMBOL + ")").getText();

        if (invoiceNetValue !=  runtimeState.portalOrdersInvoicePage.getTotalNetAmount()) {
            runtimeState.scenario.write("Updating invoice value to match with total order line net value");
            the_user_updates_the_invoice_net_and_tax_amounts();
            runtimeState.invoiceDetailsPage.clickSave();

            if (runtimeState.invoiceDetailsPage != null && runtimeState.invoiceDetailsPage.isInvoiceErrorMessageDisplayed()) {
                String netAmount = runtimeState.invoiceDetailsPage.getNetAmount();
                dbHelperJobs.updateOrderValue(testData.getInt("jobReference"), netAmount);
                runtimeState.invoiceDetailsPage.clickSave();
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the user will be presented with the submitted invoice details$")
    public void the_user_will_be_presented_with_the_submitted_invoice_details() throws Exception {

        List<Map<String, Object>> dbData = dbHelperInvoices.getAllInvoicesDetails(testData.getString("orderRef"));

        int jobReference = testData.getInt("jobReference");

        String invDate = dbData.get(0).get("InvDate").toString();
        String invDateToAssert = DateHelper.convert(invDate, DB_DATE_FORMAT, SHORT_DATE);

        String invoiceDate = dbData.get(0).get("InvoiceDate").toString();
        String invoiceDateToAssert = DateHelper.convert(invoiceDate, DB_DATE_FORMAT, SHORT_DATE);

        String dateSubmitted = dbData.get(0).get("DateSubmitted").toString();
        dateSubmitted = tzHelper.adjustTimeForJobReference(jobReference, invoiceDate, DB_DATE_FORMAT);
        String invoiceSubmittedDateToAssert = DateHelper.convert(dateSubmitted, DB_DATE_FORMAT, SHORT_DATE);

        String submissionDate = dbData.get(0).get("SubmissionDate").toString();
        submissionDate = tzHelper.adjustTimeForJobReference(jobReference, submissionDate, DB_DATE_FORMAT);
        String submissionDateToAssert = DateHelper.convert(submissionDate, DB_DATE_FORMAT, SHORT_DATE);

        BigDecimal taxVal = (BigDecimal) dbData.get(0).get("Tax");
        BigDecimal grossVal = (BigDecimal) dbData.get(0).get("Gross");

        if (testData.getString("profileName").equalsIgnoreCase("Contractor Admin")) {

            assertEquals("The Job Ref is not shown correctly: ", testData.getInt("jobReference").toString(), runtimeState.invoiceSubmittedPage.getJobRef());
            assertTrue("The Site is not shown correctly: ", runtimeState.invoiceSubmittedPage.getSiteValue().contains(dbData.get(0).get("Site").toString()));
        }
        assertEquals("The invoice submitted message is not shown correctly: ", "Invoice Submitted Successfully", runtimeState.invoiceSubmittedPage.getInvoiceSubmittedMessage());
        assertEquals("The Order Ref is not shown correctly: ", testData.getString("orderRef"), runtimeState.invoiceSubmittedPage.getOrderRef());
        assertEquals("The Invoice Number is not shown correctly: ", dbData.get(0).get("InvNum").toString(), runtimeState.invoiceSubmittedPage.getInvoiceNumber());
        assertThat("The Invoice Date is not shown correctly " + runtimeState.invoiceSubmittedPage.getInvoiceDate(), runtimeState.invoiceSubmittedPage.getInvoiceDate(), anyOf(equalTo(invDateToAssert), equalTo(invoiceDateToAssert)));
        assertTrue("The Net Amount is not shown correctly: ", runtimeState.invoiceSubmittedPage.getNetAmount(localize("£")).contains( testData.getString("orderValue")));
        assertEquals("The Tax Value is not shown correctly: ", taxVal.setScale(2, RoundingMode.HALF_UP).toString(), runtimeState.invoiceSubmittedPage.getTaxAmount(localize("Vat"), localize("£")));
        assertEquals("The Gross Value is not shown correctly: ", grossVal.setScale(2, RoundingMode.HALF_UP).toString(), runtimeState.invoiceSubmittedPage.getGrossAmount(localize("£")));
        assertThat("The Date submitted is not shown correctly: " + runtimeState.invoiceSubmittedPage.getDateSubmitted(), runtimeState.invoiceSubmittedPage.getDateSubmitted(), anyOf(equalTo(invoiceSubmittedDateToAssert), equalTo(submissionDateToAssert)));
    }

    @When("^the user selects the submit button$")
    public void the_user_selects_the_submit_button() {
        runtimeState.portalOrdersInvoicePage.submitInvoice();
        outputHelper.takeScreenshots();
    }

    @And("^the user clicks the back button without submitting the invoice$")
    public void user_clicks_back_button_without_submitting_order() {
        runtimeState.ordersAwaitingInvoicePage = runtimeState.portalOrdersInvoicePage.clickBackButton();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the orders awaiting invoice page is displayed$")
    public void the_orders_awaiting_invoice_page_is_displayed() {
        assertTrue("Page is not loaded ", runtimeState.ordersAwaitingInvoicePage.isPageLoaded());
        assertEquals("Unexpected Page Title", "Orders Awaiting Invoice", runtimeState.ordersAwaitingInvoicePage.getPageTitle());
    }

    @And("^the user has deleted an invoice$")
    public void the_user_has_deleted_an_invoice() throws Throwable {
        // this looks to check if an invoice document exists, if so it then deletes the invoice, if not it uploads one and then
        // deletes it


        if (runtimeState.portalOrdersInvoicePage.isDeleteInvoiceDisplayed()) {
            the_user_deletes_an_invoice();
        } else {
            the_user_uploads_an_invoice_from_the_invoice_page();
            the_user_deletes_an_invoice();
        }

    }

    @And("^the user has deleted a job sheet$")
    public void the_user_has_deleted_a_job_sheet() throws Throwable {
        // this looks to check if an jobsheet document exists, if so it then deletes the jobsheet, if not it uploads one and
        // then deletes it
        // if a invoice document is present then the user should be taken to the upload document page and if both dont exist
        // then invoice documents page

        if (runtimeState.portalOrdersInvoicePage.isUploadInvoiceDisplayed() && runtimeState.portalOrdersInvoicePage.isUploadJobSheetDisplayed()) {
            runtimeState.uploadInvoiceDocumentsPage = runtimeState.portalOrdersInvoicePage.clickUploadJobSheet();
            the_user_uploads_a_jobsheet_from_the_invoice_page();
        } else if (runtimeState.portalOrdersInvoicePage.isUploadJobSheetDisplayed() && runtimeState.portalOrdersInvoicePage.isDeleteInvoiceDisplayed()) {
            the_user_uploads_a_job_sheet();
        }

        the_user_deletes_a_jobSheet();
    }

    @And("^the user uploads an invoice from the invoice page$")
    public void the_user_uploads_an_invoice_from_the_invoice_page() throws Throwable {
        // If both documents don't exist then user is taken to the upload documents page if the job sheet exists then invoice
        // document page

        // This then goes on to complete the invoice details page
        if (runtimeState.portalOrdersInvoicePage.isUploadInvoiceDisplayed() && runtimeState.portalOrdersInvoicePage.isUploadJobSheetDisplayed()) {
            runtimeState.uploadInvoiceDocumentsPage = runtimeState.portalOrdersInvoicePage.clickUploadInvoice();
            the_user_uploads_invoice_documents();

        } else if (runtimeState.portalOrdersInvoicePage.isUploadInvoiceDisplayed() && runtimeState.portalOrdersInvoicePage.isDeleteJobSheetDisplayed()) {
            the_user_uploads_an_invoice();
        }

        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();

        if (runtimeState.invoiceDetailsPage.getLegalEntity().isEmpty()) {
            runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        }
        a_net_amount_is_entered();
        commonSteps.the_button_is_clicked("Save");
        the_user_is_taken_to_the_invoice_page();
    }

    @And("^the user uploads a jobsheet from the invoice page$")
    public void the_user_uploads_a_jobsheet_from_the_invoice_page() throws Throwable {
        // uploads a job sheet and invoice if not already present and completes the invoice details page
        the_user_uploads_invoice_documents();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();

        if (runtimeState.invoiceDetailsPage.getLegalEntity().isEmpty()) {
            runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        }
        a_net_amount_is_entered();
        commonSteps.the_button_is_clicked("Save");
        the_user_is_taken_to_the_invoice_page();
    }

    @And("^the user deletes a job sheet$")
    public void the_user_deletes_a_jobSheet() throws Throwable {
        runtimeState.deleteInvoiceDocModal = runtimeState.portalOrdersInvoicePage.deleteJobSheet();
        outputHelper.takeScreenshots();
        runtimeState.portalOrdersInvoicePage = runtimeState.deleteInvoiceDocModal.selectDelete();
        outputHelper.takeScreenshots();
    }

    @And("^the user deletes an invoice$")
    public void the_user_deletes_an_invoice() throws Throwable {
        runtimeState.deleteInvoiceDocModal = runtimeState.portalOrdersInvoicePage.deleteInvoice();
        outputHelper.takeScreenshots();
        runtimeState.portalOrdersInvoicePage = runtimeState.deleteInvoiceDocModal.selectDelete();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^there is no invoice present on the invoice page$")
    public void there_is_no_invoice_present_on_the_invoice_page() throws Throwable {
        assertTrue("Upload Invoice not displayed", runtimeState.portalOrdersInvoicePage.isUploadInvoiceDisplayed());
        assertTrue("Inactive invoice tick not displayed", runtimeState.portalOrdersInvoicePage.isInactiveInvoiceTickDisplayed());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^there is no job sheet present on the invoice page$")
    public void there_is_no_job_sheet_present_on_the_invoice_page() throws Throwable {
        assertTrue("Uploaded job sheet not displayed", runtimeState.portalOrdersInvoicePage.isUploadJobSheetDisplayed());
        assertTrue("Inactive job sheet tick not displayed", runtimeState.portalOrdersInvoicePage.isInactiveJobSheetTickDisplayed());
        outputHelper.takeScreenshots();
    }

    @When("^a user attempts to add a new line of \"([^\"]*)\" type with a tax amount greater than standard rate$")
    public void a_user_attempts_to_add_a__new_line_of_type_with_a_tax_amount_greater_than_standard_rate(String type) throws Throwable {
        // This adds a new line to the invoice and passes in the percentage given via the feature file to create the tax amount
        // in accordance to the net amount
        the_user_selects_add_new_line_to_invoice();
        the_user_selects_type_for_the_invoice_line(type);
        the_user_enters_a_description_to_the_invoice_line();
        the_user_enters_a_quantity_to_the_invoice_line();
        the_user_enters_a_unit_price_to_the_invoice_line();
        the_user_enters_a_tax_amount_which_is_percent_of_the_unit_price((TAX_RATE + 1));
        the_user_is_able_to_add_the_invoice_line();
    }

    @When("^a user attempts to add a material line of \"([^\"]*)\" type with a tax amount greater than standard rate$")
    public void a_user_attempts_to_add_a_material_of_type_with_a_tax_amount_greater_than_standard_rate(String type) throws Throwable {

        the_user_clicks_on_manage_lines();
        runtimeState.manageInvoiceLinesModal = new ManageInvoiceLinesModal(getWebDriver()).get();

        if (!runtimeState.manageInvoiceLinesModal.isMaterialLinePresent()) {
            runtimeState.manageInvoiceLinesModal.clickMaterialsLines();
        }

        if (!runtimeState.manageInvoiceLinesModal.isLabourLinePresent()) {
            runtimeState.manageInvoiceLinesModal.clickLaborLines();
        }

        portalInvoiceHelper.completeMandatoryLines(runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid());
        portalInvoiceHelper.completeMandatoryLines(runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid());

        Grid grid = runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid();
        Row materialsLineRow = grid.getRows().get(0);
        materialsLineRow.getCell(3).sendText("1");
        materialsLineRow.getCell(4).sendText(testData.getString("orderValue"));

        double taxAmount = (Math.ceil(Double.parseDouble(testData.getString("orderValue")) / 100) * TAX_RATE + 1);
        materialsLineRow.getCell(5).sendText(String.valueOf(taxAmount));

        if (runtimeState.manageInvoiceLinesModal.isDeleteLaboursLinePresent()) {
            runtimeState.manageInvoiceLinesModal.clickDeleteLabourLines();
            runtimeState.manageInvoiceLinesModal.clickConfirmationAlert();
            portalInvoiceHelper.enterMaterialLineDescription();
        }

        runtimeState.manageInvoiceLinesModal.clickSave();
    }

    @And("^the user enters a tax amount which is \"([^\"]*)\" percent of the unit price$")
    public void the_user_enters_a_tax_amount_which_is_percent_of_the_unit_price(double percent) throws Throwable {
        double taxAmount = (Math.ceil(Double.parseDouble(testData.getString("orderValue")) / 100) * percent);
        runtimeState.addNewInvoiceLineModal.enterUnitTaxAmount(String.valueOf(taxAmount), localize("Vat"));
    }

    @ContinueNextStepsOnException
    @Then("^the user is not able to add the invoice line without completing the compulsory fields$")
    public void the_user_is_not_able_to_add_the_invoice_line_without_completing_the_compulsory_fields() throws Throwable {
        the_user_is_able_to_add_the_invoice_line();
        commonSteps.the_error_is_displayed("Please select your type");
        commonSteps.the_error_is_displayed("Please enter your description");
        commonSteps.the_error_is_displayed("This field is required.");
    }

    @ContinueNextStepsOnException
    @Then("^an upload invoice error message is displayed$")
    public void an_upload_invoice_error_message_is_displayed() throws Throwable {
        List<Map<String, Object>> invoiceDocuments = dbHelperInvoices.getInvoiceDocumentsFilePath(testData.getInt("orderRef"));
        if (invoiceDocuments.get(0).get("jobSheetFilePath") == null && invoiceDocuments.get(0).get("invoiceFilePath") != null) {
            commonSteps.the_error_is_displayed("Please select a Job Sheet to upload");
            outputHelper.takeScreenshots();
        } else if (invoiceDocuments.get(0).get("invoiceFilePath") == null && invoiceDocuments.get(0).get("jobSheetFilePath") != null) {
            commonSteps.the_error_is_displayed("Please specify the file to be uploaded");
            outputHelper.takeScreenshots();
        } else if (invoiceDocuments.get(0).get("invoiceFilePath") == null && invoiceDocuments.get(0).get("jobSheetFilePath") == null){
            commonSteps.the_error_is_displayed("Please select a Job Sheet to upload");
            commonSteps.the_error_is_displayed("Please select an Invoice to upload");
            outputHelper.takeScreenshots();
        }
    }

    @And("^the user uploads an invoice$")
    public void the_user_uploads_an_invoice() throws Throwable {
        runtimeState.uploadInvoiceDocumentPage = runtimeState.portalOrdersInvoicePage.selectUploadInvoice();
        outputHelper.takeScreenshots();
        runtimeState.uploadInvoiceDocumentPage.setDocumentFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
        outputHelper.takeScreenshots();
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentPage.selectUploadDocument();
        outputHelper.takeScreenshots();
    }

    @And("^the user uploads a job sheet$")
    public void the_user_uploads_a_job_sheet() throws Throwable {
        runtimeState.uploadInvoiceDocumentPage = runtimeState.portalOrdersInvoicePage.selectUploadJobSheet();
        outputHelper.takeScreenshots();
        runtimeState.uploadInvoiceDocumentPage.setDocumentFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
        outputHelper.takeScreenshots();
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentPage.selectUploadDocument();
        outputHelper.takeScreenshots();
    }

    @And("^the user tries to upload an invoice without specifying the documents$")
    public void the_user_tries_to_upload_an_invoice_without_specifying_the_documents() throws Throwable {
        runtimeState.uploadInvoiceDocumentsPage = runtimeState.portalOrdersInvoicePage.clickUploadInvoice();
        outputHelper.takeScreenshots();
        runtimeState.uploadInvoiceDocumentsPage.clickUploadFiles();
        outputHelper.takeScreenshots();
    }

    @And("^the user tries to upload a jobsheet without specifying the documents$")
    public void the_user_tries_to_upload_a_jobsheet_without_specifying_the_documents() throws Throwable {
        runtimeState.uploadInvoiceDocumentsPage = runtimeState.portalOrdersInvoicePage.clickUploadJobSheet();
        outputHelper.takeScreenshots();
        runtimeState.uploadInvoiceDocumentsPage.clickUploadFiles();
        outputHelper.takeScreenshots();
    }

    @And("^the user tries to upload an invoice without specifying the location$")
    public void the_user_tries_to_upload_an_invoice_without_specifying_the_location() throws Throwable {
        runtimeState.uploadInvoiceDocumentPage = runtimeState.portalOrdersInvoicePage.selectUploadInvoice();
        outputHelper.takeScreenshots();
        runtimeState.uploadInvoiceDocumentPage.clickUploadFile();
        outputHelper.takeScreenshots();
    }

    @And("^the user tries to upload a job sheet without specifying the location$")
    public void the_user_tries_to_upload_a_job_sheet_without_specifying_the_location() throws Throwable {
        runtimeState.uploadInvoiceDocumentPage = runtimeState.portalOrdersInvoicePage.selectUploadJobSheet();
        outputHelper.takeScreenshots();
        runtimeState.uploadInvoiceDocumentPage.clickUploadFile();
        outputHelper.takeScreenshots();
    }


    @When("^the user uploads a job sheet without a document$")
    public void the_user_uploads_a_job_sheet_without_a_document() throws Throwable {
        // Checks if a jobsheet exists, if not it uploads one
        List<Map<String, Object>> invoiceDocuments = dbHelperInvoices.getInvoiceDocumentsFilePath(testData.getInt("orderRef"));
        if (invoiceDocuments.get(0).get("jobSheetFilePath") == null && invoiceDocuments.get(0).get("invoiceFilePath") == null) {
            the_user_tries_to_upload_a_jobsheet_without_specifying_the_documents();
        } else {
            the_user_tries_to_upload_a_job_sheet_without_specifying_the_location();
        }
    }

    @When("^the user uploads an invoice without a document$")
    public void the_user_uploads_an_invoice_without_a_document() throws Throwable {
        // Checks if an invoice document exists, if not it uploads one
        List<Map<String, Object>> invoiceDocuments = dbHelperInvoices.getInvoiceDocumentsFilePath(testData.getInt("orderRef"));
        if (invoiceDocuments.get(0).get("invoiceFilePath") == null && invoiceDocuments.get(0).get("jobSheetFilePath") == null) {
            the_user_tries_to_upload_an_invoice_without_specifying_the_documents();
        } else {
            the_user_tries_to_upload_an_invoice_without_specifying_the_location();
        }
    }

    @And("^the user deletes a job sheet if it exists$")
    public void the_user_deletes_a_job_sheet_if_it_exists() throws Throwable {
        // Checks if an jobsheet document exists, if so it deletes it
        List<Map<String, Object>> invoiceDocuments = dbHelperInvoices.getInvoiceDocumentsFilePath(testData.getInt("orderRef"));
        if (invoiceDocuments.get(0).get("jobSheetFilePath") != null) {
            the_user_deletes_a_jobSheet();
        }
    }

    @And("^the user deletes an invoice if it exists$")
    public void the_user_deletes_an_invoice_if_it_exists() throws Throwable {
        // Checks if an invoice document exists, if so it deletes it
        List<Map<String, Object>> invoiceDocuments = dbHelperInvoices.getInvoiceDocumentsFilePath(testData.getInt("orderRef"));
        if (invoiceDocuments.get(0).get("invoiceFilePath") != null) {
            the_user_deletes_an_invoice();
        }
    }

    @And("^the user deletes the invoice documents$")
    public void the_user_deletes_the_invoice_documents() throws Throwable {
        the_user_is_taken_to_the_invoice_page();
        the_user_deletes_an_invoice_if_it_exists();
        the_user_deletes_a_job_sheet_if_it_exists();
    }

    @And("^the user deletes all invoice lines$")
    public void the_user_deletes_all_invoice_lines() throws Throwable {
        while (runtimeState.portalOrdersInvoicePage.isEditOrderLineLinkDisplayed()) {
            runtimeState.editInvoiceLineModal = runtimeState.portalOrdersInvoicePage.editInvoiceOrderLine();
            runtimeState.editInvoiceLineModal.selectDelete();
            outputHelper.takeScreenshots();
        }
    }

    @And("^the user uploads an incorrect OCR invoice$")
    public void the_user_uploads_an_incorrect_OCR_invoice() throws Throwable {
        runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\IncorrectOCRInvoice.pdf");
        outputHelper.takeScreenshots();
        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName"))) {
            runtimeState.uploadInvoiceDocumentsPage.setJobSheetFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
            outputHelper.takeScreenshots();
        }
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentsPage.selectUploadFiles();
        outputHelper.takeScreenshots();
    }

    @And("^the user uploads an incorrect OCR invoice from the invoice details screen$")
    public void the_user_uploads_an_incorrect_OCR_invoice_from_the_invoice_details_screen() throws Throwable {
        runtimeState.uploadInvoiceDocumentPage = runtimeState.portalOrdersInvoicePage.selectUploadInvoice();
        runtimeState.uploadInvoiceDocumentPage = new UploadInvoiceDocumentPage(getWebDriver()).get();
        outputHelper.takeScreenshots();
        runtimeState.uploadInvoiceDocumentPage.setDocumentFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\IncorrectOCRInvoice.pdf");
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentPage.selectUploadDocument();
        outputHelper.takeScreenshots();
    }

    @When("^the user uploads incorrect OCR Documents$")
    public void the_user_uploads_incorrect_OCR_documents() throws Throwable {
        // uploads an ocr which is not correct
        the_order_awaiting_invoice_is_searched_for_and_opened();
        the_user_is_taken_to_the_upload_documents_page();
        the_user_uploads_an_incorrect_OCR_invoice();
        the_invoice_details_page_is_displayed();
    }

    @ContinueNextStepsOnException
    @Then("^the invoice details \"([^\"]*)\" corrected$")
    public void the_invoice_details_can_be_corrected(String canBeOrAre) throws Throwable {
        if ("can be".equalsIgnoreCase(canBeOrAre)) {
            assertTrue("Invoice Number field is not editable", runtimeState.invoiceDetailsPage.isFieldEditable("Invoice Number"));
            assertTrue("Invoice Date field is not editable", runtimeState.invoiceDetailsPage.isFieldEditable("Invoice Date"));
            assertTrue("Legal Entity field is not editable", runtimeState.invoiceDetailsPage.isFieldEditable("Legal Entity"));
            assertTrue("Net Amount field is not editable", runtimeState.invoiceDetailsPage.isFieldEditable("Net Amount"));
            assertTrue("Tax Amount field is not editable", runtimeState.invoiceDetailsPage.isFieldEditable(localize("Vat")));
        } else if ("are".equalsIgnoreCase(canBeOrAre)) {
            an_invoice_number_is_entered();
            an_invoice_date_is_entered_which_is_after_the_job_date();
            runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getString("orderRef")));
            runtimeState.invoiceDetailsPage.setInvoiceTaxAmount("0.00");
        }
    }

    @When("^the user uploads a valid invoice$")
    public void the_user_uploads_a_valid_invoice() throws Throwable {
        // Uploads a valid invoice, if a job sheet does not already exists it also uploads that too
        List<Map<String, Object>> invoiceDocuments = dbHelperInvoices.getInvoiceDocumentsFilePath(testData.getInt("orderRef"));
        if (invoiceDocuments.get(0).get("invoiceFilePath") == null && invoiceDocuments.get(0).get("jobSheetFilePath") == null) {
            runtimeState.uploadInvoiceDocumentsPage = runtimeState.portalOrdersInvoicePage.clickUploadInvoice();
            the_user_uploads_invoice_documents();
        } else {
            the_user_uploads_an_invoice();
        }
    }

    @And("^the user attempts to add a tax amount greater than standard rate$")
    public void the_user_attempts_to_add_a_tax_amount_greater_than_standard_rate() throws Throwable {
        // A Tax amount is set by taking the percentage given via the feature and multiplying it by the order value over 100
        String randomInvoiceNumber = testData.getInt("jobReference") + DataGenerator.GenerateRandomString(13, 13, 3, 0, 10, 0);
        runtimeState.invoiceDetailsPage.setInvoiceNumber(randomInvoiceNumber);
        runtimeState.invoiceDetailsPage.setInvoiceNetAmount(testData.getString("orderValue"));
        runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getString("orderRef")));
        outputHelper.takeScreenshots();
        DecimalFormat df = new DecimalFormat("0.##");
        String taxAmount = df.format((Double.parseDouble(testData.getString("orderValue")) / 100d) * (TAX_RATE + 1));
        testData.addStringTag("invoiceTaxAmount", String.valueOf((taxAmount)));
        runtimeState.invoiceDetailsPage.setInvoiceTaxAmount(String.valueOf(taxAmount));
        String invoiceDate = DateHelper.getDateInFormat(new Date(), MS_SHORT_DATE);
        runtimeState.invoiceDetailsPage.setInvoiceDate(invoiceDate);
        commonSteps.the_button_is_clicked("Save");
    }

    @And("^the user clicks add order line to invoice$")
    public void the_user_clicks_add_order_line_to_invoice() throws Throwable {
        runtimeState.addOrderLineToInvoiceModal = runtimeState.portalOrdersInvoicePage.addNewOrderLine();
    }


    @When("^the user is able to upload an invoice to the order$")
    public void the_user_is_able_to_upload_an_invoice_to_the_order() throws Throwable {
        the_user_uploads_invoice_documents();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()), not(isEmptyOrNullString()));
        runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        a_net_amount_is_entered();
        commonSteps.the_button_is_clicked("Save");
        the_user_is_taken_to_the_invoice_page();
    }

    @When("^the user uploads a \"([^\"]*)\" ocr invoice to the order$")
    public void the_user_uploads_an_ocr_invoice_to_the_order(String type) throws Throwable {
        runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir") + testData.getString("pdfPath"));
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentsPage.selectUploadFiles();
        the_invoice_details_page_is_displayed();
        commonSteps.the_button_is_clicked("Save");
    }

    @When("^the user upload an invoice to an order with an incorrect legal entity$")
    public void the_user_uploads_an_invoice_to_an_order_with_incorret_legal_entity() throws Throwable {
        the_user_uploads_invoice_documents();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        a_net_amount_is_entered();
        a_random_legal_entity_is_entered();
        commonSteps.the_button_is_clicked("Save");
    }

    @And("^the user clicks on edit invoice$")
    public void the_user_clicks_on_edit_invoice() throws Exception {
        // Searches for the edit link on the grid
        // adds the edit button action to the row.

        Grid grid = runtimeState.portalOrdersInvoicePage.getInvoiceHeaderGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> row = grid.getRows();
        row.get(0).getCell("Action").clickButton("Edit");
    }

    @When("^the user edits the invoice with an incorrect legal entity$")
    public void the_user_edits_the_invoice_with_incorret_legal_entity() throws Throwable {
        the_user_clicks_on_edit_invoice();
        runtimeState.invoiceDetailsPage = new InvoiceDetailsPage(getWebDriver()).get();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        a_net_amount_is_entered();
        a_random_legal_entity_is_entered();
        commonSteps.the_button_is_clicked("Save");
    }

    @When("^the user edits the invoice with a correct legal entity against the \"([^\"]*)\" name$")
    public void the_user_edits_the_invoice_with_a_correct_legal_entity_against_the_name(String entityNameType) throws Throwable {
        the_user_clicks_on_edit_invoice();
        runtimeState.invoiceDetailsPage = new InvoiceDetailsPage(getWebDriver()).get();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        a_net_amount_is_entered();
        // Checks the type of legal entity that is requested
        // Sets the legal entity name with the correct legal entity
        if ("Company".equalsIgnoreCase(entityNameType)) {
            runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        } else {
            runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedAliasNames(testData.getInt("orderRef").toString()));
        }
        outputHelper.takeScreenshots();
        commonSteps.the_button_is_clicked("Save");
    }

    @And("^the user is able to upload an invoice to the order with a \"([^\"]*)\" legal entity$")
    public void the_user_is_able_to_upload_an_invoice_to_the_order_with_a_something_legal_entity(String entityType) throws Throwable {
        the_user_uploads_invoice_documents();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        a_net_amount_is_entered();
        if ("Company".equalsIgnoreCase(entityType)) {
            runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        } else {
            runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedAliasNames(testData.getInt("orderRef").toString()));
        }
        commonSteps.the_button_is_clicked("Save");
        the_user_is_taken_to_the_invoice_page();
    }

    @And("^the invoice amount is \"([^\"]*)\" the authorised amount$")
    public void the_invoice_amount_is_less_than_or_equal_to_the_authorised_amount(String invoiceAmount) {
        DecimalFormat df = new DecimalFormat("#.00");
        Double randomSelection = RandomUtils.nextDouble(0, Double.valueOf(testData.getString("orderValue")));
        if (invoiceAmount.equalsIgnoreCase("less than")) {
            runtimeState.invoiceDetailsPage.setInvoiceNetAmount(df.format(randomSelection));
            testData.addStringTag("invoiceNetValue", String.valueOf(randomSelection));
        } else if (invoiceAmount.equalsIgnoreCase("equal to")) {
            runtimeState.invoiceDetailsPage.setInvoiceNetAmount(testData.getString("orderValue"));
            testData.addStringTag("invoiceNetValue", testData.getString("orderValue"));
        } else if (invoiceAmount.equalsIgnoreCase("greater than")) {
            Double overAmount = Double.valueOf(testData.getString("orderValue")) + randomSelection;
            runtimeState.invoiceDetailsPage.setInvoiceNetAmount(df.format(overAmount));
            testData.addStringTag("invoiceNetValue", String.valueOf(df.format(overAmount)));
        }
        runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();

        outputHelper.takeScreenshots();
    }

    @And("^the user clicks Tick to Add on the order line$")
    public void the_user_clicks_tick_to_add_on_the_order_line() throws Throwable {
        runtimeState.scenario.write("Line Fulfilled and Tick to Add are checked for every row in the list");
        List<Map<String, Object>> invoiceLines = dbHelperInvoices.getOrderLinesToInvoice(Integer.toString(testData.getInt("orderRef")));
        Grid grid = runtimeState.addOrderLineToInvoiceModal.getGrid();
        List<Row> rows = grid.getRows();

        // check what toggles are set then click the appropriate answer
        int invoicingLineFulfilled = dbHelperSystemToggles.getSystemSubFeatureToggle("invoicingLineFulfilled");
        int consolidatedInvoicing = dbHelperSystemToggles.getSystemSubFeatureToggle("consolidatedInvoicing");
        if (invoicingLineFulfilled == 1 || consolidatedInvoicing == 1 & !invoiceLines.isEmpty()) {
            // Iterates through the grid and adds cell control for line fulfilled
            for (int i = 0; i < rows.size(); i++) {

                // Iterates through the list and compares the Part Number against the grid Part Numbers
                if (rows.get(i).getCell("Tick to Add").getText().isEmpty() && invoiceLines != null && !invoiceLines.isEmpty()) {

                    for (int j = 0; j < invoiceLines.size(); j++) {
                        if (rows.get(i).getCell("Description").getText().equalsIgnoreCase(invoiceLines.get(j).get("Description").toString().trim())) {

                            // Gets to invoice quantity and only Fulfils line if the quantity is greater than 1
                            int toInvoiceQuantity = Math.round(Float.parseFloat(invoiceLines.get(j).get("Quantity").toString()));
                            if (toInvoiceQuantity >= 1 && !rows.get(i).getCell("Line Fulfilled ?").isCheckBoxChecked()) {
                                rows.get(i).getCell("To Invoice Qty").sendText(String.valueOf(toInvoiceQuantity - 1));
                                rows.get(i).getCell("Line Fulfilled ?").clickCheckbox("");
                                testData.addBooleanTag("lineFullfilled", true);
                            }
                        }
                    }
                    if (rows.get(i).getCell("Line Fulfilled ?").getText().equals("Yes")) {
                        testData.addBooleanTag("lineFullfilled", true);
                    }

                    rows.get(i).getCell("Tick to Add").clickCheckbox("");
                }
            }
        } else {
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).getCell("Tick to Add").getText().isEmpty()) {
                    rows.get(i).getCell("Tick to Add").clickCheckbox("");
                }
            }
        }
        outputHelper.takeScreenshots();
    }

    @And("^the to invoice quantity is set to be greater than the order quantity$")
    public void the_to_invoice_quantity_is_est_to_be_greater_than_the_order_quantity() throws Exception {
        runtimeState.scenario.write("Invoice quantity is changed to be greater than the order quantity on a line");
        Grid grid = runtimeState.addOrderLineToInvoiceModal.getGrid();
        List<Row> row = grid.getRows();
        row.get(0).getCell("To Invoice Qty").sendText("1");
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the invoice line is shown as Fullfilled$")
    public void the_invoice_line_is_shown_as_fullfilled() throws Exception {
        // Asserts the line Fullfilled is shown as yes
        List<Map<String, Object>> invoiceLineFullfilled = dbHelperInvoices.getInvoiceLineFullfilledStatus(Integer.toString(testData.getInt("orderRef")));
        assertNotNull("Unexpected empty list of invoice line fulfilled status", invoiceLineFullfilled);
        Grid grid = runtimeState.addOrderLineToInvoiceModal.getGrid();
        List<Row> row = grid.getRows();
        // Iterates through the grid
        for (int i = 0; i < row.size(); i++) {
            // Iterates through the invoice fullfilled list to find the matching description
            for (int j = 0; j < invoiceLineFullfilled.size(); j++) {
                if (row.get(i).getCell("Description").getText().equalsIgnoreCase(invoiceLineFullfilled.get(j).get("Description").toString().trim())) {
                    if (invoiceLineFullfilled.get(j).get("lineFullfilled").toString().equalsIgnoreCase("True")) {
                        assertEquals("unexpected line fullfillment status", row.get(i).getCell("Line Fulfilled ?").getText(), "Yes");
                    }
                }
            }
        }
    }

    @And("^the user clicks add lines to order$")
    public void the_user_clicks_add_lines_to_order() throws Throwable {
        Grid grid = runtimeState.addOrderLineToInvoiceModal.getGrid();
        boolean lineToAdd = false;
        for (Row row : grid.getRows()) {
            if (row.getCell("Tick to Add").getWebElements() != null) {
                lineToAdd = true;
            }
        }

        if ( !lineToAdd ) {
            // no lines to add
            runtimeState.addOrderLineToInvoiceModal.close();

        } else {
            runtimeState.scenario.write("Select Add Lines and if the confirmation modal appears then select yes");
            runtimeState.addOrderLineToInvoiceModal.selectAddLines();
            outputHelper.takeScreenshots();

            if(testData.getBoolean("lineFullfilled") == true) {
                runtimeState.addOrderLinesToInvoiceConfirmationModal = new AddOrderLinesToInvoiceConfirmationModal(getWebDriver()).get();

                assertThat("Unexpected confirmation message", runtimeState.addOrderLinesToInvoiceConfirmationModal.getConfirmationMessage(),
                        containsString("The remaining balance on those order lines will be cancelled and will not be available for invoicing. Are you sure you want to proceed?"));
                runtimeState.addOrderLinesToInvoiceConfirmationModal.clickYes();
                outputHelper.takeScreenshots();
            }
        }
    }

    @And("^an order line is added to the uploaded invoice and submitted$")
    public void an_order_line_is_added_to_the_uploaded_invoice_and_submitted() throws Throwable {
        a_new_invoice_is_added_to_the_invoice_Page();
        the_user_clicks_add_order_line_to_invoice();
        the_user_clicks_tick_to_add_on_the_order_line();
        the_user_clicks_add_lines_to_order();

        // Update the invoice amount to match the invoice line value
        the_user_updates_the_invoice_net_and_tax_amounts();
        runtimeState.invoiceDetailsPage.clickSave();

        if (runtimeState.invoiceDetailsPage != null && runtimeState.invoiceDetailsPage.isInvoiceErrorMessageDisplayed()) {
            String netAmount = runtimeState.invoiceDetailsPage.getNetAmount();
            dbHelperJobs.updateOrderValue(testData.getInt("jobReference"), netAmount);
            runtimeState.invoiceDetailsPage.clickSave();
        }

        the_user_is_able_to_submit_the_invoice();
    }

    @And("^the user updates the invoice net and tax amounts$")
    public void the_user_updates_the_invoice_net_and_tax_amounts() throws Exception{
        testData.addStringTag("totalNetAmount", runtimeState.portalOrdersInvoicePage.getTotalNetAmount());
        testData.addStringTag("totalTaxAmount", runtimeState.portalOrdersInvoicePage.getTotalTaxAmount());
        Grid grid = runtimeState.portalOrdersInvoicePage.getInvoiceHeaderGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> row = grid.getRows();
        row.get(0).getCell("Action").clickButton("Edit");
        runtimeState.invoiceDetailsPage = new InvoiceDetailsPage(getWebDriver()).get();
        runtimeState.invoiceDetailsPage.setInvoiceNetAmount(testData.getString("totalNetAmount").replace(",", ""));
        runtimeState.invoiceDetailsPage.setInvoiceTaxAmount(testData.getString("totalTaxAmount").replace(",", ""));
        outputHelper.takeScreenshots();
    }

    @When("^the order line is added to the invoice$")
    public void the_order_line_is_added_to_the_invoice() throws Throwable {
        the_user_clicks_add_order_line_to_invoice();
        the_user_clicks_tick_to_add_on_the_order_line();
        the_user_clicks_add_lines_to_order();
    }

    @When("^an invoice is uploaded and an order line is added$")
    public void invoice_is_uploaded_and_order_line_is_added() throws Throwable {
        the_user_is_taken_to_the_upload_documents_page();
        the_user_uploads_a_correct_OCR_invoice_to_the_order_with_a_tax_amount("0.00");
        the_order_line_is_added_to_the_invoice();
    }

    @And("^the user clicks edit on an order line$")
    public void the_user_clicks_edit_on_an_order_line() {
        Grid grid = runtimeState.portalOrdersInvoicePage.getDetailLinesGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> row = grid.getRows();
        row.get(0).getCell(6).clickButton("Edit");
    }

    @ContinueNextStepsOnException
    @Then("^the edit invoice line modal has the correct order$")
    public void the_edit_invoice_line_has_the_correct_order() throws Exception {
        runtimeState.newInvoicingEditInvoiceLineModal = new NewInvoicingEditInvoiceLineModal(getWebDriver()).get();
        List<Map<String, Object>> invoiceLines = dbHelperInvoices.getAddedOrderLinesToInvoice(Integer.toString(testData.getInt("orderRef")));
        Grid grid = runtimeState.newInvoicingEditInvoiceLineModal.getGrid();
        List<Row> row = grid.getRows();
        for (int i = 0; i < row.size(); i++) {
            // Iterates through the invoice fullfilled list to find the matching part number
            // Asserts that the description from the cell contains part of the description from the db
            for (int j = 0; j < invoiceLines.size(); j++) {
                if (row.get(i).getCell("Part Number").getText().contains(invoiceLines.get(j).get("PartNumber").toString().trim()) || row.get(i).getCell("Part Number").getText().contains(invoiceLines.get(j).get("SuppPartNumber").toString().trim())) {
                    assertEquals("Unexpected Description", row.get(i).getCell("Description").getText(), normalize(invoiceLines.get(j).get("Description").toString()));
                    if (invoiceLines.get(j).get("lineFulfilled").equals(true)) {
                        assertTrue("Invoice submitted header not displayed", row.get(i).getCell("Line Fulfilled ?").isCheckBoxChecked());
                    }
                }
            }
        }
    }

    @And("^the user edits the invoice line fulfillment status$")
    public void the_user_edits_the_invoice_line_fulfillment_status() throws Throwable  {
        runtimeState.newInvoicingEditInvoiceLineModal = new NewInvoicingEditInvoiceLineModal(getWebDriver()).get();
        List<Map<String, Object>> invoiceLines = dbHelperInvoices.getAddedOrderLinesToInvoice(Integer.toString(testData.getInt("orderRef")));
        Grid grid = runtimeState.newInvoicingEditInvoiceLineModal.getGrid();
        List<Row> row = grid.getRows();
        for (int i = 0; i < row.size(); i++) {
            // Iterates through the invoice fulfilled list to find the matching part number
            // Asserts that the description from the cell contains part of the description from the db
            for (int j = 0; j < invoiceLines.size(); j++) {
                if (row.get(i).getCell("Part Number").getText().contains(invoiceLines.get(j).get("PartNumber").toString().trim()) || row.get(i).getCell("Part Number").getText().contains(invoiceLines.get(j).get("SuppPartNumber").toString().trim())) {
                    testData.addIntegerTag("invoiceLineId", (Integer) invoiceLines.get(j).get("invoiceLineId"));
                    if (invoiceLines.get(j).get("lineFulfilled").equals(true)) {
                        testData.addBooleanTag("initialFulfillmentStatus", true);
                        row.get(i).getCell("Line Fulfilled ?").clickCheckbox("");
                    } else {
                        testData.addBooleanTag("initialFulfillmentStatus", false);
                        row.get(i).getCell("Line Fulfilled ?").clickCheckbox("");
                    }
                }
            }
        }

        // Save the changes and if the fulfillment status is set to fulfilled then accept the confirmation message
        commonSteps.the_button_is_clicked("Save");
        if (runtimeState.newInvoicingEditInvoiceLineModal.isConfirmationMessageDisplayed()) {
            runtimeState.editInvoiceLineConfirmationModal = new EditInvoiceLineConfirmationModal(getWebDriver()).get();
            assertThat("Unexpected Description",runtimeState.editInvoiceLineConfirmationModal.getConfirmationMessage(),
                    containsString("You have advised that some line numbers have been fully fulfilled. The remaining balance on those order lines will be cancelled and will not be available for invoicing."));
            runtimeState.editInvoiceLineConfirmationModal.clickConfirm();
        }
    }

    @And("^the user updates the order with an incorrect invoice quantity$")
    public void the_user_updates_the_order_with_an_incorrect_invoice_quantity() throws Throwable {
        runtimeState.newInvoicingEditInvoiceLineModal = new NewInvoicingEditInvoiceLineModal(getWebDriver()).get();
        List<Map<String, Object>> invoiceLines = dbHelperInvoices.getAddedOrderLinesToInvoice(Integer.toString(testData.getInt("orderRef")));
        Grid grid = runtimeState.newInvoicingEditInvoiceLineModal.getGrid();
        List<Row> row = grid.getRows();
        for (int i = 0; i < row.size(); i++) {
            // Iterates through the invoice fulfilled list to find the matching part number
            // Asserts that the description from the cell contains part of the description from the db
            for (int j = 0; j < invoiceLines.size(); j++) {
                if (row.get(i).getCell("Part Number").getText().contains(invoiceLines.get(j).get("PartNumber").toString().trim()) || row.get(i).getCell("Part Number").getText().contains(invoiceLines.get(j).get("SuppPartNumber").toString().trim())) {
                    int toInvoiceQuantity = Math.round(Float.parseFloat(invoiceLines.get(j).get("Quantity").toString()));
                    testData.addIntegerTag("toInvoiceQuantity", toInvoiceQuantity);
                    row.get(i).getCell("To Invoice Qty").sendText(String.valueOf(toInvoiceQuantity + 2));

                    // Line fulfilled box should not be enabled
                    assertFalse("Line fulfilled box is enabled", row.get(i).getCell("Line Fulfilled ?").isCheckBoxEnabled());
                }
            }
        }
        commonSteps.the_button_is_clicked("Save");
    }

    @ContinueNextStepsOnException
    @Then("^the line fulfillment status is updated$")
    public void the_line_fulfillment_status_is_updated() {
        int invoiceLineId = testData.getInt("invoiceLineId");
        if (testData.getBoolean("initialFulfillmentStatus") == true) {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> dbHelperInvoices.isLineFulfilled(invoiceLineId), equalTo(false));
        } else {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> dbHelperInvoices.isLineFulfilled(invoiceLineId), equalTo(true));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the user is taken to the invoice submitted page$")
    public void the_user_is_taken_to_the_invoice_submitted_page() throws Throwable {
        assertTrue("Invoice submitted header not displayed", runtimeState.invoiceSubmittedPage.isInvoiceSubmittedHeaderDisplayed());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the invoice is not displayed on the \"([^\"]*)\" table$")
    public void the_invoice_is_not_displayed_on_the_table(String table) throws Throwable {
        if ("Invoices & Credits Awaiting Review".equals(table)) {
            String orderRef = testData.getString("newOrderRef");
            runtimeState.invoicesAndCreditsAwaitingReviewPage.searchInvoices(orderRef);
            assertFalse("Invoice is displayed on the grid!", runtimeState.invoicesAndCreditsAwaitingReviewPage.isInvoiceDisplayed(orderRef));

        }
    }

    @ContinueNextStepsOnException
    @Then("^the invoice status is (?:set to|still) (?:|one of )\"([^\"]*)\"$")
    public void the_invoice_status_is_set_to(String stateName) throws Throwable {
        if (stateName.contains("/")) {
            String[] status = stateName.split("/");
            List<Integer> invoiceStatusIds = new ArrayList<Integer>();
            for (String invoiceStatus : status) {
                invoiceStatusIds.add(dbHelperInvoices.getInvoiceStateIdForName(invoiceStatus));
            }
            assertTrue(invoiceStatusIds.contains(dbHelperInvoices.getInvoiceStateIdForInvoice(testData.getString("newInvoiceNumber"))));

        } else {
            Integer invoiceStatusId = dbHelperInvoices.getInvoiceStateIdForName(stateName);
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> dbHelperInvoices.getInvoiceStateIdForInvoice(testData.getString("newInvoiceNumber")), equalTo(invoiceStatusId));
            outputHelper.takeScreenshots();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the invoice status is set to \"([^\"]*)\" status$")
    public void the_invoice_status_is_set_to_status(String stateName) throws Throwable {
        Integer invoiceStatusId = dbHelperInvoices.getInvoiceStateIdForName(stateName);
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> dbHelperInvoices.getInvoiceStateIdForOrderRef(testData.getString("newOrderRef")), equalTo(invoiceStatusId));
        outputHelper.takeScreenshots();
    }

    @And("^the user uploads an OCR invoice$")
    public void the_user_uploads_an_OCR_invoice() throws Throwable {
        the_order_awaiting_invoice_is_searched_for_and_opened();
        the_user_is_taken_to_the_upload_documents_page();
        the_user_uploads_valid_invoice_template_documents();
        the_invoice_details_page_is_displayed();
    }

    @And("^the user selects delete on the supplier invoice$")
    public void the_user_selects_delete_on_the_supplier_invoice() throws Throwable {
        runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();
        runtimeState.portalOrdersInvoicePage.isDeleteInvoiceDisplayed();
        runtimeState.portalOrdersInvoicePage.deleteInvoice();
    }

    @When("^the user has deleted a supplier invoice$")
    public void the_user_has_deleted_a_supplier_invoice() throws Throwable {
        if (runtimeState.portalOrdersInvoicePage.isDeleteInvoiceDisplayed()) {
            the_user_deletes_an_invoice();
        } else {
            the_user_uploads_a_supplier_invoice_from_the_invoice_page();
            the_user_deletes_an_invoice();
        }
    }

    @And("^the user uploads a supplier invoice from the invoice page$")
    public void the_user_uploads_a_supplier_invoice_from_the_invoice_page() throws Throwable {
        runtimeState.portalOrdersInvoicePage.selectUploadInvoice();
        outputHelper.takeScreenshots();
        the_user_is_taken_to_the_upload_documents_page();
        the_user_uploads_valid_invoice_template_documents();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        a_net_amount_is_entered();
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()), not(isEmptyOrNullString()));
        runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        runtimeState.invoiceDetailsPage.selectSave();
        outputHelper.takeScreenshots();
    }

    @When("^an order line with a tax amount greater than standard rate is added$")
    public void an_order_line_with_a_tax_amount_greater_than_standard_rate_is_added() throws Throwable {
        a_new_invoice_is_added_to_the_invoice_Page();
        the_user_clicks_add_order_line_to_invoice();
        Grid grid = runtimeState.addOrderLineToInvoiceModal.getGrid();

        String orderDescription = grid.getRows().get(0).getCell("Description").getText();
        String orderPartNumber = grid.getRows().get(0).getCell("Part Number").getText();
        testData.addStringTag("orderDescription", orderDescription);
        testData.addStringTag("orderPartNumber", orderPartNumber);

        DecimalFormat df = new DecimalFormat("#0.00");
        String taxAmount = df.format((Double.parseDouble(testData.getString("orderValue")) / 100d) * (TAX_RATE + 1));
        testData.addStringTag("unitTaxAmount", taxAmount);

        // Get unit price from the grid
        String unitPrice = grid.getRows().get(0).getCell("Unit Price (" + CURRENCY_SYMBOL + ")").getValue();
        testData.addStringTag("partUnitPrice", unitPrice);

        runtimeState.addOrderLineToInvoiceModal.setUnitTaxAmount(String.valueOf(taxAmount));
        the_user_clicks_tick_to_add_on_the_order_line();
        the_user_clicks_add_lines_to_order();
    }

    @And("^the user deletes an order line$")
    public void the_user_deletes_an_order_line() throws Throwable {
        if (!runtimeState.portalOrdersInvoicePage.isEditOrderLineLinkDisplayed()) {
            the_user_clicks_add_order_line_to_invoice();
            the_user_clicks_tick_to_add_on_the_order_line();
            the_user_clicks_add_lines_to_order();
        }

        while (runtimeState.portalOrdersInvoicePage.isEditOrderLineLinkDisplayed()) {
            runtimeState.editInvoiceLineModal = runtimeState.portalOrdersInvoicePage.editInvoiceOrderLine();
            runtimeState.editInvoiceLineModal.selectDelete();
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^there is no order line present on the invoice$")
    public void there_is_no_order_line_present_on_the_invoice() throws Throwable {
        assertTrue("AN order line is present on the invoice", !runtimeState.portalOrdersInvoicePage.isEditOrderLineLinkDisplayed());
        outputHelper.takeScreenshots();
    }

    @When("^the user uploads and saves an OCR invoice that has a tax amount \"([^\"]*)\"$")
    public void the_user_uploads_and_saves_an_OCR_invoice_that_has_a_tax_amount(String taxAmount) throws Throwable {
        the_order_awaiting_invoice_is_searched_for_and_opened();
        the_user_is_taken_to_the_upload_documents_page();
        the_user_uploads_valid_invoice_template_documents();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        runtimeState.invoiceDetailsPage.setInvoiceNetAmount(testData.getString("orderValue"));
        runtimeState.invoiceDetailsPage.setInvoiceTaxAmount(taxAmount);
        runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        outputHelper.takeScreenshots();
        runtimeState.portalOrdersInvoicePage = runtimeState.invoiceDetailsPage.clickSave();
    }

    @When("^the user uploads and saves an OCR invoice$")
    public void the_user_uploads_and_saves_an_ocr_invoice() throws Throwable {
        the_user_is_taken_to_the_upload_documents_page();
        runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir")  + testData.getString("pdfPath"));
        outputHelper.takeScreenshots();
        if ("Contractor Admin".equalsIgnoreCase(testData.getString("profileName"))) {
            runtimeState.uploadInvoiceDocumentsPage.setJobSheetFileName(System.getProperty("user.dir")  + testData.getString("pdfPath"));
            outputHelper.takeScreenshots();
        }
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentsPage.selectUploadFiles();
        outputHelper.takeScreenshots();

        the_invoice_details_page_is_displayed();

        if (runtimeState.invoiceDetailsPage.getInvoiceNumber().isEmpty()) {
            runtimeState.invoiceDetailsPage.setInvoiceNumber(String.valueOf(RandomUtils.nextInt(100000, 999999)));
        }
        if (runtimeState.invoiceDetailsPage.getInvoiceDate().isEmpty()) {
            runtimeState.invoiceDetailsPage.setInvoiceDate(DateHelper.getDateInFormat(new Date(), SHORT_DATE));
        }
        if (runtimeState.invoiceDetailsPage.getLegalEntity().isEmpty()) {
            runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        }
        runtimeState.invoiceDetailsPage.clickSave();

        if (runtimeState.invoiceDetailsPage != null && runtimeState.invoiceDetailsPage.isInvoiceErrorMessageDisplayed()) {
            String netAmount = runtimeState.invoiceDetailsPage.getNetAmount();
            dbHelperJobs.updateOrderValue(testData.getInt("jobReference"), netAmount);
            runtimeState.invoiceDetailsPage.clickSave();
        }
    }

    @And("^the user uploads a correct OCR invoice to the order with a tax amount \"([^\"]*)\"$")
    public void the_user_uploads_a_correct_OCR_invoice_to_the_order_with_a_tax_amount(String taxAmount) throws Throwable {
        runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\OCRinvoiceTemplate.pdf");
        outputHelper.takeScreenshots();
        runtimeState.invoiceDetailsPage = runtimeState.uploadInvoiceDocumentsPage.selectUploadFiles();
        outputHelper.takeScreenshots();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()), not(isEmptyOrNullString()));
        runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        a_net_amount_is_entered();
        runtimeState.invoiceDetailsPage.setInvoiceTaxAmount(taxAmount);
        commonSteps.the_button_is_clicked("Save");
        the_user_is_taken_to_the_invoice_page();
    }

    @ContinueNextStepsOnException
    @Then("^an invoice is uploaded with job sheet to the order with tax amount \"([^\"]*)\"$")
    public void the_user_is_able_to_upload_an_invoice_and_job_sheet_to_the_order_with_tax_amount(String taxAmount) throws Throwable {
        the_user_uploads_invoice_documents();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        a_net_amount_is_entered();
        runtimeState.invoiceDetailsPage.setInvoiceTaxAmount(taxAmount);
        runtimeState.invoiceDetailsPage.setLegalEntity(dbHelperInvoices.getAssignedCompanyName(testData.getInt("orderRef").toString()));
        outputHelper.takeScreenshots();
        commonSteps.the_button_is_clicked("Save");
        the_user_is_taken_to_the_invoice_page();
    }

    @When("^the user is able to upload an invoice and job sheet to the order with an incorrect legal entity$")
    public void the_user_is_able_to_upload_an_invoice_and_job_sheet_to_the_order_with_incorrect_legal_entity() throws Throwable {
        the_user_uploads_invoice_documents();
        the_invoice_details_page_is_displayed();
        an_invoice_number_is_entered();
        an_invoice_date_is_entered_which_is_after_the_job_date();
        a_net_amount_is_entered();
        a_random_legal_entity_is_entered();
        commonSteps.the_button_is_clicked("Save");
    }

    @And("^a new line type with net value equal to order value is added to the invoice$")
    public void an_new_line_is_added_to_the_uploaded_invoice_and_submitted() throws Throwable {
        the_user_selects_add_new_line_to_invoice();
        the_user_selects_type_for_the_invoice_line("Parts");
        the_user_enters_a_description_to_the_invoice_line();
        the_user_enters_a_quantity_to_the_invoice_line();
        the_user_enters_a_unit_price_equal_to_net_value_to_the_invoice_line();
        the_user_is_able_to_add_the_invoice_line();
    }

    @And("^a new line type with net value less than order value is added to the invoice$")
    public void an_new_line_with_value_less_than_order_value_is_added_to_the_uploaded_invoice_and_submitted() throws Throwable {
        the_user_selects_add_new_line_to_invoice();
        the_user_selects_type_for_the_invoice_line("Parts");
        the_user_enters_a_description_to_the_invoice_line();
        the_user_enters_a_quantity_to_the_invoice_line();
        the_user_enters_a_unit_price_less_than_net_value_to_the_invoice_line();
        the_user_is_able_to_add_the_invoice_line();
    }

    @And("^a new materials line type with net value less than order value is added to the invoice$")
    public void a_new_materials_line_with_value_less_than_order_value_is_added_to_the_uploaded_invoice_and_submitted() throws Throwable {
        the_user_clicks_on_manage_lines();
        runtimeState.manageInvoiceLinesModal = new ManageInvoiceLinesModal(getWebDriver()).get();

        if (!runtimeState.manageInvoiceLinesModal.isMaterialLinePresent()) {
            runtimeState.manageInvoiceLinesModal.clickMaterialsLines();
        }

        if (!runtimeState.manageInvoiceLinesModal.isLabourLinePresent()) {
            runtimeState.manageInvoiceLinesModal.clickLaborLines();
        }

        portalInvoiceHelper.completeMandatoryLines(runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid());
        portalInvoiceHelper.completeMandatoryLines(runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid());

        DecimalFormat df = new DecimalFormat("#.00");
        Double value = RandomUtils.nextDouble(0, Double.valueOf(testData.getString("orderValue")) - 1);

        if (getWebDriver().getCurrentUrl().contains("usad")) {

            if (runtimeState.manageInvoiceLinesModal.isDeleteLaboursLinePresent()) {
                runtimeState.manageInvoiceLinesModal.clickDeleteLabourLines();
                runtimeState.manageInvoiceLinesModal.clickConfirmationAlert();
            }
            portalInvoiceHelper.enterMaterialLineDescription();
        }

        portalInvoiceHelper.enterMaterialsLineDetailsForSingleQuantity(df.format(value));
        outputHelper.takeScreenshots();

    }

    @When("^an order line with net value less than order value is added to the invoice$")
    public void an_order_line_with_net_value_less_than_order_value_is_added_to_the_invoice() throws Throwable {
        the_user_clicks_add_order_line_to_invoice();
        the_user_enters_a_unit_price_less_than_net_value_to_the_order_line();
        the_user_clicks_tick_to_add_on_the_order_line();
        the_user_clicks_add_lines_to_order();
    }

    @And("^a random Legal entity is entered$")
    public void a_random_legal_entity_is_entered() {
        // Random string is entered into the legal entity text box
        runtimeState.invoiceDetailsPage.setLegalEntity(DataGenerator.GenerateRandomString(13, 13, 3, 0, 10, 0));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^a \"([^\"]*)\" line is added to the invoice$")
    public void invoice_line_added(String type) throws Throwable {

        the_user_clicks_on_manage_lines();
        runtimeState.manageInvoiceLinesModal = new ManageInvoiceLinesModal(getWebDriver()).get();

        if (!runtimeState.manageInvoiceLinesModal.isMaterialLinePresent()) {
            runtimeState.manageInvoiceLinesModal.clickMaterialsLines();
        }

        if (!runtimeState.manageInvoiceLinesModal.isLabourLinePresent()) {
            runtimeState.manageInvoiceLinesModal.clickLaborLines();
        }

        portalInvoiceHelper.completeMandatoryLines(runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid());
        portalInvoiceHelper.completeMandatoryLines(runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid());

        if (type.equalsIgnoreCase("Materials") && getWebDriver().getCurrentUrl().contains("usad")) {

            if (runtimeState.manageInvoiceLinesModal.isDeleteLaboursLinePresent()) {
                runtimeState.manageInvoiceLinesModal.clickDeleteLabourLines();
                runtimeState.manageInvoiceLinesModal.clickConfirmationAlert();
            }
            portalInvoiceHelper.enterMaterialLineDescription();
        }

        if (type.equalsIgnoreCase("Labour") && runtimeState.manageInvoiceLinesModal.isDeleteMaterialsLinePresent()) {
            runtimeState.manageInvoiceLinesModal.clickDeleteMaterialsLines();
            runtimeState.manageInvoiceLinesModal.clickConfirmationAlert();
            portalInvoiceHelper.enterLabourLineDescription();
        }


        if (type.equalsIgnoreCase("Materials")) {
            portalInvoiceHelper.enterMaterialsLineDetailsForSingleQuantity(testData.getString("orderValue"));
        } else if (type.equalsIgnoreCase("Labour")) {
            portalInvoiceHelper.enterLabourLineDetailsForSingleQuantity();
        }
    }
}