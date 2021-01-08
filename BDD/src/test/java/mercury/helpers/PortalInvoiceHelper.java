package mercury.helpers;

import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.SHORT2;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.database.dao.JobViewDao;
import mercury.database.dao.PartCodeDao;
import mercury.database.models.PartCode;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.PopupAlert;
import mercury.runtime.RuntimeState;

@Component
public class PortalInvoiceHelper {

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private PartCodeDao partCodeDao;
    @Autowired private OutputHelper outputHelper;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private TzHelper tzHelper;

    public String getDescription() throws Throwable {
        String description = "Description for test automation " + DateHelper.dateAsString(new Date());
        testData.addStringTag("description", description);
        return description;
    }

    public Integer getQuantity() {
        Integer quantity = DataGenerator.randBetween(1, 10);
        testData.put("quantity", quantity);
        runtimeState.scenario.write("The quantity entered is : " + quantity);
        return quantity;
    }

    public Float getUnitPrice() {
        Float unitPrice = BigDecimal.valueOf(DataGenerator.GenerateRandomDouble(1.00, 1000.00)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        testData.put("unitPrice", unitPrice);
        runtimeState.scenario.write("Unit price entered in the Materials invoice line is: " + testData.getFloat("unitPrice"));
        return unitPrice;
    }

    public Float getRate() {
        Float rate = BigDecimal.valueOf(DataGenerator.GenerateRandomDouble(100.00, 1000.00)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        testData.put("rate", rate);
        runtimeState.scenario.write("The rate entered in the Labor invoice line is: " + testData.getFloat("rate"));
        return rate;
    }

    public Float getUnitTax(String lineType, String tax, String percent) {
        int percentage = 0;
        if ("LESS".equalsIgnoreCase(tax)) {
            percentage = DataGenerator.randBetween(1, Integer.parseInt(percent));
        } else if ("GREATER".equalsIgnoreCase(tax)) {
            percentage = DataGenerator.randBetween((Integer.parseInt(percent) + 1), 100);
        } else if ("EITHER LESS OR GREATER".equalsIgnoreCase(tax)) {
            percentage = DataGenerator.randBetween(1, 100);
        }

        if ("Materials Line".equalsIgnoreCase(lineType)) {
            Float materialsLineTaxAmount = BigDecimal.valueOf(testData.getFloat("unitPrice") * percentage / 100).setScale(2, BigDecimal.ROUND_DOWN).floatValue();
            testData.put("materialsLineTaxAmount", materialsLineTaxAmount);
            runtimeState.scenario.write("Tax amount entered in the " + lineType + " invoice line is: " + testData.getFloat("materialsLineTaxAmount"));
            return materialsLineTaxAmount;
        } else {
            Float laborLineTaxAmount = BigDecimal.valueOf(testData.getFloat("rate") * percentage / 100).setScale(2, BigDecimal.ROUND_DOWN).floatValue();
            testData.put("laborLineTaxAmount", laborLineTaxAmount);
            runtimeState.scenario.write("Tax amount entered in the " + lineType + " invoice line is: " + testData.getFloat("laborLineTaxAmount"));
            return laborLineTaxAmount;
        }
    }

    public Integer getHours() {
        Integer hour = DataGenerator.randBetween(1, 12);
        testData.addIntegerTag("hour", hour);
        runtimeState.scenario.write("The hours entered in the labor invoice line is: " + testData.getInt("hour"));
        return hour;
    }

    public void enterMaterialsLineDetails(String type, String tax, String percent, String lineType) throws Throwable {
        testData.addStringTag("typeValue", type);

        int rowSize = testData.getInt("invoiceLinesRowSize");
        Grid grid = runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid();
        Row materialsLineRow = grid.getRows().get(rowSize);

        GridHelper.click(materialsLineRow.getCell(0).getCssSelector());
        runtimeState.manageInvoiceLinesModal.selectMaterialsLinesType(rowSize, type);

        PartCode materialsLineDetails = partCodeDao.getRandomPartCode();
        String partCode = materialsLineDetails.getPartCode();
        testData.addStringTag("partCode", partCode);
        if ("Parts".equalsIgnoreCase(type)) {
            materialsLineRow.getCell(1).sendText(partCode);
            runtimeState.scenario.write("The part code entered is : " + partCode);
        }

        String description = normalize(materialsLineDetails.getDescription());
        if ("Parts".equalsIgnoreCase(type)) {
            materialsLineRow.getCell(2).sendText(description);
            testData.addStringTag("description", description);
            runtimeState.scenario.write("The description entered is : " + description);
        } else {
            materialsLineRow.getCell(2).sendText(getDescription());
        }

        Integer quantity = getQuantity();
        materialsLineRow.getCell(3).sendText(String.valueOf(quantity));

        Float unitPrice = BigDecimal.valueOf(materialsLineDetails.getUnitPrice()).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
        if ("Parts".equalsIgnoreCase(type)) {
            materialsLineRow.getCell(4).sendText(String.valueOf(unitPrice));
            testData.put("unitPrice", unitPrice);
            runtimeState.scenario.write("Unit price entered in the Materials invoice line is: " + testData.getFloat("unitPrice"));
        } else {
            materialsLineRow.getCell(4).sendText(String.valueOf(getUnitPrice()));
        }

        Float unitTax = getUnitTax(lineType, tax, percent);
        materialsLineRow.getCell(5).sendText(String.valueOf(unitTax));

        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }

    public void enterLaborLineDetails(String type, String tax, String percent, String lineType) throws Throwable {
        testData.addStringTag("typeValue", localize(type));
        int rowSize = testData.getInt("invoiceLinesRowSize");

        Grid grid = runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid();
        Row laborLineRow = grid.getRows().get(rowSize);

        GridHelper.click(laborLineRow.getCell(0).getCssSelector());
        runtimeState.manageInvoiceLinesModal.selectLaborLinesType(rowSize, localize(type));
        laborLineRow.getCell(1).sendText(getDescription());
        laborLineRow.getCell(2).sendText(String.valueOf(getHours()));
        laborLineRow.getCell(3).sendText(String.valueOf(getRate()));
        laborLineRow.getCell(4).sendText(String.valueOf(getUnitTax(lineType, tax, percent)));

        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }

    public void deleteMaterialLine(String lineType) throws Throwable {
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        String[] laborTypes = {dbHelperInvoices.getLabourLineType(), "Travel", "Overtime"};

        for (int index = 0; index < rows.size(); index++) {
            if (!rows.get(index).getCell("Action").getText().isEmpty()) {
                String typeValue = runtimeState.manageInvoiceLinesModal.getMaterialsLineType(index);
                if (!Arrays.stream(laborTypes).anyMatch(typeValue::equals)) {
                    String newLine = System.lineSeparator();
                    String description = rows.get(index).getCell(2).getText();
                    runtimeState.scenario.write("Deleted the Materials line from Materials Related Invoice Lines table.");
                    runtimeState.scenario.write("The deleted row details are: " + newLine +
                            "Type: " + typeValue + newLine +
                            "Description: " + description + newLine +
                            "Quantity: " + rows.get(index).getCell(3).getValue() + newLine +
                            "Unit Price: " + rows.get(index).getCell(4).getValue());
                    rows.get(index).getCell("Action").clickButton("Delete");
                    runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
                    runtimeState.popupAlert.confirm();
                    break;
                }
            }
        }
        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }

    public void deleteLaborLine(String lineType) throws Throwable {
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        String[] laborTypes = {dbHelperInvoices.getLabourLineType(), "Travel", "Overtime"};

        for (int index = 0; index < rows.size(); index++) {
            if (!rows.get(index).getCell("Action").getText().isEmpty()) {
                String typeValue = runtimeState.manageInvoiceLinesModal.getLaborLineType(index);
                if (Arrays.stream(laborTypes).anyMatch(typeValue::equals)) {
                    String newLine = System.lineSeparator();
                    String description = rows.get(index).getCell(1).getText();
                    runtimeState.scenario.write("Deleted the Labor line from Labor Related Invoice Lines table.");
                    runtimeState.scenario.write("The deleted row details are: " + newLine +
                            "Type: " + typeValue + newLine +
                            "Description: " + description + newLine +
                            "Rate: " + rows.get(index).getCell(3).getValue());
                    rows.get(index).getCell("Action").clickButton("Delete");
                    runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
                    runtimeState.popupAlert.confirm();
                    break;
                }
            }
        }
        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }

    public boolean isTypeDisabled(Grid grid) throws Exception {
        outputHelper.takeScreenshots();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        for (int index = 0; index < rows.size(); index++) {
            if (rows.get(index).getCell("Action").getText().isEmpty()) {
                runtimeState.scenario.write("The " + testData.getString("lineType") + " type is disabled in row " + new Integer(index + 1) + ".");
                return rows.get(index).getCell("Type").isCellDisabled();
            }
        }
        return false;
    }

    public boolean isAllLinesDisabled(Grid grid) {
        outputHelper.takeScreenshots();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        Boolean[] linesDisabled = new Boolean[rows.size()];
        for (int index = 0; index < rows.size(); index++) {
            if (rows.get(index).getCell("Action").getText().isEmpty()) {
                linesDisabled[index] = rows.get(index).getCell("Type").isCellDisabled();
            }
        }
        return Arrays.asList(linesDisabled).contains(true);
    }

    public void editMaterialsLineDetails(String lineType) throws Throwable {
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        String[] laborTypes = {dbHelperInvoices.getLabourLineType(), "Travel", "Overtime"};

        for (int index = 0; index < rows.size(); index++) {
            if (!rows.get(index).getCell("Action").getText().isEmpty()) {
                String typeValue = runtimeState.manageInvoiceLinesModal.getMaterialsLineType(index);
                if (!Arrays.stream(laborTypes).anyMatch(typeValue::equals)) {
                    Integer quantity = Integer.valueOf(rows.get(index).getCell(3).getValue()) + 1;
                    testData.put("quantity", quantity + ".00");
                    rows.get(index).getCell(3).sendText(String.valueOf(quantity));
                    runtimeState.scenario.write("The updated quantity is : " + quantity);
                    testData.put("description", normalize(rows.get(index).getCell(2).getValue()));
                    testData.put("unitPrice", rows.get(index).getCell(4).getValue());
                    testData.put("unitTax", rows.get(index).getCell(5).getValue());
                    break;
                }
            }
        }
        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }

    public void editLaborLineDetails(String lineType) throws Throwable {
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        String[] laborTypes = {dbHelperInvoices.getLabourLineType(), "Travel", "Overtime"};

        for (int index = 0; index < rows.size(); index++) {
            if (!rows.get(index).getCell("Action").getText().isEmpty()) {
                String typeValue = runtimeState.manageInvoiceLinesModal.getLaborLineType(index);
                if (Arrays.stream(laborTypes).anyMatch(typeValue::equals)) {
                    String description = getDescription();
                    testData.addStringTag("description", description);
                    rows.get(index).getCell(1).sendText(description);
                    runtimeState.scenario.write("The updated description is : " + description);
                    break;
                }
            }
        }
        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }

    public boolean clickNotApplicableCheckBox(Grid grid) throws Throwable {
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        for (int index = 0; index < rows.size(); index++) {
            if (rows.get(index).getCell("Action").getText().isEmpty()) {
                if (!rows.get(index).getCell("N/A").isCheckBoxChecked()) {
                    rows.get(index).getCell("N/A").clickCheckbox("");
                    runtimeState.scenario.write("The " + testData.getString("lineType") + " N/A checkbox is clicked in row " + new Integer(index + 1) + ".");
                    return true;
                }
            }
        }
        return false;
    }

    public void enterDescription(Grid grid, boolean selected, String lineType) throws Throwable {
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        for (int index = 0; index < rows.size(); index++) {
            if (rows.get(index).getCell("Action").getText().isEmpty() && selected == true) {
                String description = getDescription();
                rows.get(index).getCell("Description").sendText(description);
                runtimeState.scenario.write("The " + testData.getString("lineType") + " description is entered in row " + new Integer(index + 1) + ".");
                if (lineType.equalsIgnoreCase("Materials Line")) {
                    testData.put("quantity", rows.get(index).getCell("Quantity").getText() + ".00");
                    testData.put("unitPrice", rows.get(index).getCell(localize("Unit Price ($)")).getValue());
                    testData.put("unitTax", rows.get(index).getCell("Unit "+ localize("Vat") +" ("+ localize("$") +")").getValue());
                }
                break;
            }
        }
        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }

    public void completeMandatoryLines(Grid grid) throws Throwable {
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        for (int index = 0; index < rows.size(); index++) {
            if (rows.get(index).getCell("Action").getText().isEmpty() && rows.get(index).getCell("Type").isCellDisabled()) {
                if (!rows.get(index).getCell("N/A").isCheckBoxChecked()) {
                    rows.get(index).getCell("N/A").clickCheckbox("");
                }
                String description = getDescription();
                rows.get(index).getCell("Description").sendText(description);
            }
        }
        String lineType = grid.getGridXpath().contains("material") ? "materials" : "labour";
        runtimeState.scenario.write("The mandatory "+ lineType +" lines are marked as N/A.");
    }

    public void assertInvoiceTab(List<Map<String, Object>> dbData) throws Exception {
        runtimeState.scenario.write("Asserting details displayed in the Invoice tab: ");

        runtimeState.scenario.write("Approver 1: " + dbData.get(0).get("Approver 1"));
        assertEquals("Unexpected Approver 1 ", dbData.get(0).get("Approver 1"), runtimeState.invoiceTabPage.getInvoiceValue("Approver 1"));

        runtimeState.scenario.write("Original Budget: " + dbData.get(0).get("Original Budget"));
        assertEquals("Unexpected Original Budget", dbData.get(0).get("Original Budget"), runtimeState.invoiceTabPage.getInvoiceValue("Original Budget"));

        // If there is no budget change, budget change reason is not displayed.
        if (dbData.get(0).get("BudgetChange").toString().equalsIgnoreCase("Yes")) {
            runtimeState.scenario.write("Budget Change Reason: " + dbData.get(0).get("Budget Change Reason"));
            assertEquals("Unexpected Budget Change Reason", normalize(dbData.get(0).get("Budget Change Reason").toString()), runtimeState.invoiceTabPage.getInvoiceValue("Budget Change Reason"));
        }

        runtimeState.scenario.write("Extreme Weather: " + dbData.get(0).get("Extreme Weather"));
        assertEquals("Unexpected Extreme Weather ", dbData.get(0).get("Extreme Weather"), runtimeState.invoiceTabPage.getInvoiceValue("Extreme Weather"));

        runtimeState.scenario.write("Order Ref: " + dbData.get(0).get("Order Ref"));
        assertEquals("Unexpected Order Ref ", normalize(dbData.get(0).get("Order Ref").toString()), runtimeState.invoiceTabPage.getInvoiceValue("Order Ref"));

        String expectedOrderValue = String.format("%.2f", dbData.get(0).get("Order Value"));
        runtimeState.scenario.write("Order Value: " + expectedOrderValue);
        assertEquals("Unexpected Order Value ", expectedOrderValue, runtimeState.invoiceTabPage.getInvoiceValueForHeaderContaining("Order Value ("));

        runtimeState.scenario.write("Job Ref: " + dbData.get(0).get("Job Ref"));
        assertEquals("Unexpected Job Ref ", dbData.get(0).get("Job Ref").toString(), runtimeState.invoiceTabPage.getInvoiceValue("Job Ref"));

        int jobReference = Integer.valueOf(dbData.get(0).get("Job Ref").toString());
        String loggedDate = dbData.get(0).get("Logged Date").toString();
        runtimeState.scenario.write("Logged Date: " + loggedDate);
        loggedDate = tzHelper.adjustTimeForJobReference(jobReference, loggedDate, DB_DATE_FORMAT);
        runtimeState.scenario.write("Logged Date (Store Time): " + loggedDate);
        loggedDate = DateHelper.convert(loggedDate, DB_DATE_FORMAT, SHORT_DATE);
        assertEquals("Unexpected Logged Date ", loggedDate, runtimeState.invoiceTabPage.getInvoiceValue("Logged Date"));

        runtimeState.scenario.write("Site: " + dbData.get(0).get("Site"));
        assertEquals("Unexpected Site ", dbData.get(0).get("Site").toString(), runtimeState.invoiceTabPage.getInvoiceValue("Site"));

        runtimeState.scenario.write("Inv Num: " + dbData.get(0).get("Inv Num"));
        assertEquals("Unexpected Inv Num ", normalize(dbData.get(0).get("Inv Num").toString()), runtimeState.invoiceTabPage.getInvoiceValue("Inv Num"));

        String invDate = dbData.get(0).get("Inv Date").toString();
        invDate = DateHelper.convert(invDate, DB_DATE_FORMAT, SHORT_DATE);
        runtimeState.scenario.write("Inv Date: " + invDate);
        assertEquals("Unexpected Inv Date ", invDate, runtimeState.invoiceTabPage.getInvoiceValue("Inv Date"));

        runtimeState.scenario.write("Supplier: " + dbData.get(0).get("Supplier"));
        assertEquals("Unexpected Supplier ", normalize(dbData.get(0).get("Supplier").toString()), runtimeState.invoiceTabPage.getInvoiceValue("Supplier"));

        runtimeState.scenario.write("Net (" + CURRENCY_SYMBOL + "): " + dbData.get(0).get("Net"));
        assertEquals("Unexpected Net (" + CURRENCY_SYMBOL + ") ", dbData.get(0).get("Net").toString(), runtimeState.invoiceTabPage.getInvoiceValueForHeaderContaining("Net ("));

        runtimeState.scenario.write("Tax (" + CURRENCY_SYMBOL + "): " + dbData.get(0).get("Tax"));
        assertEquals("Unexpected Tax (" + CURRENCY_SYMBOL + ") ", dbData.get(0).get("Tax").toString(), runtimeState.invoiceTabPage.getInvoiceValueForHeaderContaining("Tax ("));

        runtimeState.scenario.write("Gross (" + CURRENCY_SYMBOL + "): " + dbData.get(0).get("Gross"));
        assertEquals("Unexpected Gross (" + CURRENCY_SYMBOL + ") ", dbData.get(0).get("Gross").toString(), runtimeState.invoiceTabPage.getInvoiceValueForHeaderContaining("Gross ("));

        runtimeState.scenario.write("Total Job Cost (" + CURRENCY_SYMBOL + "): " + dbData.get(0).get("Total Job Cost"));
        assertEquals("Unexpected Total Job Cost (" + CURRENCY_SYMBOL + ") ", dbData.get(0).get("Total Job Cost").toString(), runtimeState.invoiceTabPage.getInvoiceValueForHeaderContaining("Total Job Cost ("));
    }

    public void assertJobNotesTab() throws Exception {
        List<String> headers = runtimeState.jobNotesTab.getJobNotesHeaders();
        String[] expectedHeaders = {"Job Description", "Job Closedown"};
        for (String expectedHeader : expectedHeaders) {
            assertTrue(headers.contains(expectedHeader));
        }

        runtimeState.scenario.write("Asserting details displayed in the Job Notes tab: ");

        String description = jobViewDao.getByJobReference(testData.getInt("jobReference")).getDescription();
        runtimeState.scenario.write("Job Description: " + description);
        assertEquals("Unexpected Job Description ", normalize(description), runtimeState.jobNotesTab.getJobNotes("Job Description"));

        List<Map<String, Object>> dbData = dbHelperJobs.getJobClosedownDetails(testData.getInt("jobReference"));
        String jobClosedownNotes = dbData.isEmpty() ? "" : dbData.get(0).get("Job Closedown").toString();
        runtimeState.scenario.write("Job Closedown: " + jobClosedownNotes);
        assertEquals("Unexpected Job Closedown ", normalize(jobClosedownNotes), runtimeState.jobNotesTab.getJobNotes("Job Closedown"));
    }

    public void assertClosedownTab() throws Exception {
        runtimeState.scenario.write("Asserting details displayed in the Closedown tab: ");
        outputHelper.takeScreenshot();

        int jobReference = testData.getInt("jobReference");

        List<Map<String, Object>> dbData = dbHelperJobs.getJobClosedownDetails(testData.getInt("jobReference"));
        String jobType = dbData.isEmpty() ? "" : dbData.get(0).get("JobType").toString();

        String firstArrivalDate = null;
        String jobCompletedDate = null;
        Date from, to;
        long totalDiff = 0;
        for (int i = 0 ; i < dbData.size() ;i++) {
            String workStartTime = tzHelper.adjustTimeForJobReference(jobReference, dbData.get(i).get("WorkStartTime").toString(), DB_DATE_FORMAT);
            String workEndTime = tzHelper.adjustTimeForJobReference(jobReference, dbData.get(i).get("WorkEndTime").toString(), DB_DATE_FORMAT);

            String arrivalDate = DateHelper.convert(workStartTime, DB_DATE_FORMAT, SHORT2);
            firstArrivalDate = firstArrivalDate == null ? arrivalDate : firstArrivalDate;
            from = DateHelper.stringAsDate(arrivalDate, SHORT2);

            //            to = DateHelper.toNextWholeMinute(DateHelper.stringAsDate(workEndTime, DB_DATE_FORMAT)); // round up minutes
            to = DateHelper.stringAsDate(workEndTime, DB_DATE_FORMAT);
            jobCompletedDate = DateHelper.dateAsString(to, SHORT2);

            long diff = DateHelper.getDifferenceBetweenTwoTimes(from, to);
            totalDiff = totalDiff + diff;
            long minutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) % 60;
            long hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
            String onSite =  String.format("%d:%02d", hours, minutes);

            runtimeState.scenario.write("Arrival Date: " + arrivalDate);
            assertEquals(arrivalDate, runtimeState.closedownTab.getArrivalDate(i+1));
            runtimeState.scenario.write("Onsite Time: " + onSite);
            assertEquals(onSite, runtimeState.closedownTab.getOnsiteHours(i+1));
        }

        if (dbData.isEmpty()) {
            assertEquals("Reactive", runtimeState.closedownTab.getJobType());
            assertEquals("Not Set", runtimeState.closedownTab.getFirstArrivalDate());
            assertEquals("Not Set", runtimeState.closedownTab.getJobCompletedDate());
            assertEquals("0:00", runtimeState.closedownTab.getTotalOnsiteHours());

        } else {
            runtimeState.scenario.write("Job Type: " + jobType);
            assertEquals(jobType, runtimeState.closedownTab.getJobType());
            runtimeState.scenario.write("First Arrival Date: " + jobType);
            assertEquals(firstArrivalDate, runtimeState.closedownTab.getFirstArrivalDate());
            runtimeState.scenario.write("Job Completed Date: " + jobType);
            assertEquals(jobCompletedDate, runtimeState.closedownTab.getJobCompletedDate());

            long minutes = TimeUnit.MINUTES.convert(totalDiff, TimeUnit.MILLISECONDS) % 60;
            long hours = TimeUnit.HOURS.convert(totalDiff, TimeUnit.MILLISECONDS);
            String onSite =  String.format("%d:%02d", hours, minutes);
            runtimeState.scenario.write("Total Onsite Time: " + onSite);
            assertEquals(onSite, runtimeState.closedownTab.getTotalOnsiteHours());
            //            runtimeState.scenario.write("Job Type: " + jobType);
            assertEquals("", runtimeState.closedownTab.getSupplierNotes());
        }

    }

    public void enterMaterialsLineDetailsForSingleQuantity(String value) throws Throwable {
        Grid grid = runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid();
        Row materialsLineRow = grid.getRows().get(0);
        materialsLineRow.getCell(3).sendText("1");
        materialsLineRow.getCell(4).sendText(value);
        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }
    
    public void enterMaterialLineDescription() {
        Grid grid = runtimeState.manageInvoiceLinesModal.getMaterialsRelatedGrid();
        Row materialsLineRow = grid.getRows().get(0);
        materialsLineRow.getCell(1).sendText("1");
        materialsLineRow.getCell(2).sendText("test Automation");
    }
    
    public void enterLabourLineDescription() {
        Grid grid = runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid();
        Row labourLineRow = grid.getRows().get(0);
        labourLineRow.getCell(1).sendText("test Automation");
    }
    
    public void enterLabourLineDetailsForSingleQuantity() throws Throwable {
        Grid grid = runtimeState.manageInvoiceLinesModal.getLaborRelatedGrid();
        Row labourLineRow = grid.getRows().get(0);
        labourLineRow.getCell(2).sendText("1");
        labourLineRow.getCell(3).sendText(testData.getString("orderValue"));
        outputHelper.takeScreenshots();
        runtimeState.manageInvoiceLinesModal.clickSave();
    }
}