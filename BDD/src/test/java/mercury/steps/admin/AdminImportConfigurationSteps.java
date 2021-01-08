package mercury.steps.admin;

import static mercury.helpers.FileHelper.mkFolder;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opencsv.CSVWriter;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.apihelper.ApiHelperPPM;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperPPM;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.runtime.RuntimeState;

public class AdminImportConfigurationSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private ApiHelperPPM apiHelperPPM;
    @Autowired private DbHelperPPM dbHelperPPM;
    @Autowired private PropertyHelper propertyHelper;

    @When("^a \"([^\"]*)\" file is created \"([^\"]*)\"$")
    public void file_is_created(String fileType, String status) throws Exception {
        if (fileType.equalsIgnoreCase("Purchase Order Configuration")) {
            String supplierCode = null;
            if (propertyHelper.getEnv().contains("USAD")) {
                supplierCode = dbHelper.getSOSupplierCodesAdvocate(1).get(0);
            } else {
                supplierCode = dbHelper.getSOSupplierCodes(1).get(0);
            }
            createPurchaseOrderConfigFile(supplierCode, 1);
        } else {
            createPpmConfigFile(status);
        }
    }

    @When("purchase order CSV files are created")
    public void purchase_order_CSV_files_are_created() throws ParseException {
        int numPurchaseOrdersToImport = Integer.valueOf(System.getProperty("numPurchaseOrdersToImport"));
        List<String> supplierCodes = dbHelper.getSuppliersNotUsingPortalForInvoicing(Integer.valueOf(System.getProperty("numSuppliers")));
        int numSupplierCodes = supplierCodes.size();
        int numOrdersPerFile = numSupplierCodes > 1 ? numPurchaseOrdersToImport / (numSupplierCodes - 1) : numPurchaseOrdersToImport;
        numOrdersPerFile = numOrdersPerFile > 0 ? numOrdersPerFile : 1;
        int numFiles = numPurchaseOrdersToImport < numSupplierCodes ? numPurchaseOrdersToImport : numSupplierCodes;

        int remaining = numPurchaseOrdersToImport;
        int total = 0;
        for (int i = 1; i < numFiles; i++) {
            int numToImport = remaining > numOrdersPerFile ? numOrdersPerFile : remaining;
            String supplierCode = supplierCodes.get(i).trim();
            runtimeState.scenario.write("Supplier: " + supplierCode + ", Number of orders to import: " + numToImport);
            createPurchaseOrderConfigFile(supplierCode, numToImport);
            remaining = remaining - numToImport;
            total = total + numToImport;
        }

        if (remaining > 0) {
            String supplierCode = supplierCodes.get(0).trim();
            runtimeState.scenario.write("Supplier: " + supplierCode + ", Number of orders to import: " + remaining);
            createPurchaseOrderConfigFile(supplierCode, remaining);
            total = total + remaining;
        }

        runtimeState.scenario.write("Total number purchase orders to import: " + total);
    }

    public void createPpmConfigFile(String status) throws Exception {
        String site = dbHelperSites.getRandomSiteName();

        String resourceProfile = "Contractor";
        String ppmType = "Test_" + resourceProfile + "_" + System.currentTimeMillis();
        int resourceProfileId = dbHelperResources.getResourceProfileId(resourceProfile);
        String assetTypeId = dbHelper.getRandomActiveAssetId();
        apiHelperPPM.createPpmType(ppmType, "1", assetTypeId, 1, String.valueOf(resourceProfileId), null, "2", 52, "No Override", "1");

        LocalDate today = LocalDate.now();
        LocalDate nextWeekendDate = today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        String nextWeekend = nextWeekendDate.format(DateTimeFormatter.ofPattern(SHORT_DATE));

        String todaysDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = "PPM Configuration " + todaysDate + ".csv";
        String folder = System.getProperty("user.dir") + "\\target\\PurchaseOrderImports\\";
        mkFolder(folder);
        String filePath = folder + fileName;
        testData.put("filePath", filePath);
        File file = new File(filePath);
        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, System.getProperty("line.separator"));

            String[] columnHeaders = { "Store Name", "PPM Type", "Weekend Date" };
            writer.writeNext(columnHeaders);

            if (status.equalsIgnoreCase("Successfully")) {
                String[] data = { site, ppmType, nextWeekend };
                writer.writeNext(data);
            } else {
                String[] data = { "test", "test", "test" };
                writer.writeNext(data);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createPurchaseOrderConfigFile(String supplierCode, int numOrders) throws ParseException {
        Map<String, Object> importDetails = dbHelper.getPurchaseOrderDetailsForImport(supplierCode);

        Map<String, Object> supplierCodeDetails = null;
        String supplierName = null;
        String prefix = null;
        if (dbHelper.isSupplierSetUpWithCode(importDetails.get("Supplier").toString())) {
            supplierCodeDetails = dbHelper.getSupplierCodeDetails(importDetails.get("Supplier").toString());
        } else {
            supplierName = dbHelper.getSupplierName(importDetails.get("Supplier").toString());
            prefix = supplierName.substring(0, 2).toUpperCase();
            dbHelper.setUpSupplierWithcode(importDetails.get("Supplier").toString(), supplierName, prefix);
        }
        String orderPrefix = (supplierCodeDetails != null ? supplierCodeDetails.get("OrderPrefix").toString() : prefix);
        int seed = RandomUtils.nextInt(10000000, 88888888);

        String orderDate = importDetails.get("OrderDate").toString().substring(0, 10);
        String dateFormat = LOCALE.equals("en-US") ? "MM/dd/yyyy" : "dd/MM/yyyy";
        orderDate = DateHelper.convert(orderDate, "yyyy-MM-dd", dateFormat);
        testData.put("orderDate", orderDate);

        String poType = importDetails.get("POType").toString();
        testData.put("poType", poType);
        String jobNumber = importDetails.get("Job").toString();
        testData.put("jobNumber", jobNumber);
        String partNumber = "PARTS";
        testData.put("partNumber", partNumber);
        String partDescription = "GENERIC PART";
        testData.put("partDescription", partDescription);
        String unitPrice = "99.99";
        testData.put("unitPrice", unitPrice);
        String jobCode = "JOB0000" + jobNumber;
        testData.put("jobCode", jobCode);
        String retrospective = null;
        if (importDetails.get("Retrospective") == null) {
            retrospective = "0";
        } else {
            retrospective = importDetails.get("Retrospective").toString();
        }
        testData.put("retrospective", retrospective);
        String budget = importDetails.get("Budget").toString();
        testData.put("budget", budget);

        String todaysDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = importDetails.get("Supplier").toString().replaceAll(":", "") + todaysDate + ".csv";

        String folder = System.getProperty("user.dir") + "\\target\\PurchaseOrderImports\\";
        mkFolder(folder);
        String filePath = folder + fileName;

        testData.addToList("poImports", filePath);

        File file = new File(filePath);
        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, "," + System.getProperty("line.separator"));

            String[] columnHeaders = { "Order Number", "Order Date", "PO Type", "Job", "Project", "Asda Order Number", "Part No", "Part Description", "Quantity", "Unit Price", "T Code 1", "T Code 2", "T Code 3", "T Code 4", "T Code 5", "T Code 6", "T Code 7", "T Code 8", "T Code 9", "T Code 10", "Retrospective", "Budget" };

            writer.writeNext(columnHeaders);

            for (int i = 0; i < numOrders; i++) {
                String orderNumber = orderPrefix + String.valueOf(seed + i);
                testData.put("orderNumber", orderNumber);

                if (LOCALE.equals("en-US")) {
                    String[] dataWalmart = { orderNumber, orderDate, poType, jobNumber, "123", "123", partNumber, partDescription, "1", unitPrice, "#", "#", "#", "#", "#", "#", jobCode, "#", "#", "#", retrospective, budget};
                    writer.writeNext(dataWalmart);
                } else {
                    String[] dataRainbow = { orderNumber, orderDate, poType, jobNumber, "123", "123", partNumber, partDescription, "1", unitPrice, "#", "#", "#", "#", "#", "#", "#", jobCode, "#", "#", retrospective, budget};
                    writer.writeNext(dataRainbow);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @When("^the \"([^\"]*)\" \"([^\"]*)\" file is uploaded and processed$")
    public void file_is_uploaded_and_processed(String status, String fileType) {
        if (fileType.equalsIgnoreCase("Purchase Order Configuration")) {
            runtimeState.adminImportConfigurationPage.uploadFile(testData.getArray("poImports").get(0));
            runtimeState.adminImportConfigurationPage.processFiles();
        } else {
            runtimeState.adminImportConfigurationPage.uploadPpmFile(testData.getString("filePath"));
            if (status.equalsIgnoreCase("Successful")) {
                runtimeState.adminImportConfigurationPage.processPpmFiles();
            }
        }
    }

    @When("^the Purchase Order CSV files are uploaded, processed and imported$")
    public void the_Purchase_Order_CSV_files_are_uploaded_and_processed() {
        for (String filename : testData.getArray("poImports")) {
            runtimeState.adminImportConfigurationPage.uploadFile(filename);
            runtimeState.adminImportConfigurationPage.processFiles();
            runtimeState.adminImportConfigurationPage.importPos();
        }
    }

    @Then("^the imported purchase orders are in the database$")
    public void the_imported_purchase_orders_are_in_the_database() {
        for (String pathname : testData.getArray("poImports")) {
            String[] parts = pathname.split("\\\\");
            String filename = parts[parts.length - 1];
            assertTrue("Not imported: " + filename, dbHelper.isPOImported(filename));
        }
    }

    @When("^the file is imported \"([^\"]*)\"$")
    public void file_is_imported(String importSuccess) {
        runtimeState.adminEditOrderWidget = runtimeState.adminImportConfigurationPage.editOrder();
        checkAndEnterValuesIntoTextBox();

        if (importSuccess.equalsIgnoreCase("Unsuccessfully")) {
            runtimeState.adminEditOrderWidget.removeValueFromTextBox("Budget");
        }

        runtimeState.adminEditOrderWidget.clickUpdateButton();
        runtimeState.adminImportConfigurationPage.get();
        runtimeState.adminImportConfigurationPage.importPos();
    }

    public void checkAndEnterValuesIntoTextBox() {
        String orderNumber = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Order Number");
        if (orderNumber.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Order Number", testData.getString("orderNumber"));
        }

        String orderDate = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Order Date");
        if (orderDate.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Order Date", testData.getString("orderDate"));
        }

        String poType = runtimeState.adminEditOrderWidget.getValueInsideTextBox("PO Type");
        if (poType.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("PO Type", testData.getString("poType"));
        }

        String jobNumber = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Job");
        if (jobNumber.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Job", testData.getString("jobNumber"));
        }

        String partNumber = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Part No");
        if (partNumber.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Part No", testData.getString("partNumber"));
        }

        String partDescription = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Part Description");
        if (partDescription.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Part Description", testData.getString("partDescription"));
        }

        String quantity = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Quantity");
        if (quantity.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Quantity", "1");
        }

        String unitPrice = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Unit Price");
        if (unitPrice.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Unit Price", testData.getString("unitPrice"));
        }

        String tCode1 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 1");
        if (tCode1.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 1", "#");
        }

        String tCode2 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 2");
        if (tCode2.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 2", "#");
        }

        String tCode3 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 3");
        if (tCode3.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 3", "#");
        }

        String tCode4 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 4");
        if (tCode4.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 4", "#");
        }

        String tCode5 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 5");
        if (tCode5.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 5", "#");
        }

        String tCode6 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 6");
        if (tCode6.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 6", "#");
        }

        if (LOCALE.equals("en-US")) {
            String tCode7 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 7");
            if (tCode7.isEmpty()) {
                runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 7", testData.getString("jobCode"));
            }

            String tCode8 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 8");
            if (tCode8.isEmpty()) {
                runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 8", "#");
            }
        } else {
            String tCode7 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 7");
            if (tCode7.isEmpty()) {
                runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 7", "#");
            }

            String tCode8 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 8");
            if (tCode8.isEmpty()) {
                runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 8", testData.getString("jobCode"));
            }
        }

        String tCode9 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 9");
        if (tCode9.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 9", "#");
        }

        String tCode10 = runtimeState.adminEditOrderWidget.getValueInsideTextBox("T Code 10");
        if (tCode10.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("T Code 10", "#");
        }

        String retrospective = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Retrospective");
        if (retrospective.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Retrospective", testData.getString("retrospective"));
        }

        String budget = runtimeState.adminEditOrderWidget.getValueInsideTextBox("Budget");
        if (budget.isEmpty()) {
            runtimeState.adminEditOrderWidget.enterValueIntoTextBox("Budget", testData.getString("budget"));
        }

    }

    @ContinueNextStepsOnException
    @Then("^a \"([^\"]*)\" \"([^\"]*)\" file has been imported and relevant message is correct$")
    public void file_imported_and_message_displayed_correctly(String importStatus, String fileType) {
        runtimeState.scenario.write("Asserting that the correct message is displayed after a " + importStatus + " import");
        if (fileType.equalsIgnoreCase("Purchase Order Configuration")) {
            if (importStatus.equalsIgnoreCase("Successful")) {
                String successMessages = "Import Successful, All POs imported";
                String actualMessage = runtimeState.adminImportConfigurationPage.getImportSuccessfulMessage();
                assertTrue("Successful Import message not displayed correctly", successMessages.contains(actualMessage));

            } else {
                String expectedMessage = "One or more PO is invalid. The import has been rolled back, please correct the PO in the grid and import again.";
                String actualMessage = runtimeState.adminImportConfigurationPage.getImportUnsuccessfulMessage();
                assertTrue("Unsuccessful Import message not displayed correctly", actualMessage.contains(expectedMessage));
            }

        } else {
            if (importStatus.equalsIgnoreCase("Successful")) {
                String expectedMessage = "PPM Configuration Schedule successfully imported.";
                String actualMessage = runtimeState.adminImportConfigurationPage.getPpmImportSuccessfulMessage().trim();
                assertEquals("Successful PPM Import message not displayed correctly", expectedMessage, actualMessage);

            } else {
                String expectedMessage = "There were validation errors. Review input file.";
                String actualMessage = runtimeState.adminImportConfigurationPage.getPpmImportUnsuccessfulMessage().trim();
                assertEquals("Unsuccessful PPM Import message not displayed correctly", expectedMessage, actualMessage);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Import Configuration screen is displayed as expected$")
    public void import_configuration_screen_displayed_as_expected() {
        runtimeState.scenario.write("Asserting that all menu items are displayed");
        assertTrue("Purchase Order Configuration menu is not displayed", runtimeState.adminImportConfigurationPage.isMenuItemDisplayed("Purchase Order Configuration"));
        assertTrue("PPM Configuration menu is not displayed", runtimeState.adminImportConfigurationPage.isMenuItemDisplayed("PPM Configuration"));
        assertTrue("Finance Recode Import menu is not displayed", runtimeState.adminImportConfigurationPage.isMenuItemDisplayed("Finance Recode Import"));
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" page is displayed as expected$")
    public void page_displayed_as_expected(String pageName) {
        if (pageName.contains("PPM")) {
            runtimeState.scenario.write("Asserting that 'Choose files to add' label is displayed");
            String labelText = runtimeState.adminImportConfigurationPage.getChooseFilesLabelText();
            assertTrue("Label not displayed as expected", labelText.contentEquals("Choose file to add:"));

            runtimeState.scenario.write("Asserting that Upload button is displayed");
            assertTrue("Upload button is not displayed", runtimeState.adminImportConfigurationPage.isPpmUploadButtonDisplayed());
        } else {
            runtimeState.scenario.write("Asserting that 'Choose files to add' label is displayed");
            String labelText = runtimeState.adminImportConfigurationPage.getChooseFilesLabelText();
            assertTrue("Label not displayed as expected", labelText.contentEquals("Choose files to add"));

            runtimeState.scenario.write("Asserting that Upload button is displayed");
            assertTrue("Upload button is not displayed", runtimeState.adminImportConfigurationPage.isUploadButtonDisplayed());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" menu is displayed by default$")
    public void default_menu_displayed(String expectedMenuItem) {
        runtimeState.scenario.write("Asserting that correct menu item is displayed as default");
        String selectedMenuItem = runtimeState.adminImportConfigurationPage.getSelectedMenuItem();
        assertEquals("Expected: " + expectedMenuItem + " but was: " + selectedMenuItem,
                expectedMenuItem, selectedMenuItem);
    }

}
