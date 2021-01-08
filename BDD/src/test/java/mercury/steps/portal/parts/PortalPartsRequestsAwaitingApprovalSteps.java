package mercury.steps.portal.parts;

import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.Globalisation.SHORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.database.dao.JobViewDao;
import mercury.database.models.JobView;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperParts;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.runtime.RuntimeState;


public class PortalPartsRequestsAwaitingApprovalSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private DbHelperParts dbHelperParts;
    @Autowired private TzHelper tzHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;


    @And("^the parts request is rejected$")
    public void the_parts_request_is_rejected() throws Exception {
        String siteName = dbHelperSites.getSiteNameForJobRef(testData.getInt("jobReference"));
        testData.put("siteName", siteName);

        runtimeState.partsOrderAwaitingApprovalPage.setRandomRejectReason();
        String totalCost = runtimeState.partsOrderAwaitingApprovalPage.getTotalCost();
        testData.put("totalCost",  totalCost);
        runtimeState.partsOrderAwaitingApprovalPage.setRejectNotes("Notes set by test automation");
        outputHelper.takeScreenshots();
        runtimeState.partsOrderAwaitingApprovalPage.reject();
        runtimeState.scenario.write("Rejected part with total cost of: " + totalCost);
    }

    @And("^the parts request is approved$")
    public void the_parts_request_is_approved() throws Exception {
        if (runtimeState.partsOrderAwaitingApprovalPage.getBudgetDropdownEditState()) {
            runtimeState.partsOrderAwaitingApprovalPage.setBudget();
        }

        if ( !runtimeState.partsOrderAwaitingApprovalPage.isApprovalEnabled() ) {
            runtimeState.partsOrderAwaitingApprovalPage.clickVerbalApproval();
        }

        if (dbHelperSystemToggles.getSystemFeatureToggle("Budget Review") == 1) {
            runtimeState.partsOrderAwaitingApprovalPage.selectRandomAnswerForIsPotentialInsuranceJob();
        }
        outputHelper.takeScreenshots();
        runtimeState.partsOrderAwaitingApprovalPage.approve();
    }

    @And("^an item is deleted$")
    public void an_item_is_deleted() throws Exception {
        runtimeState.partsOrderAwaitingApprovalPage.deletePartRequest();
        Grid grid;
        grid = runtimeState.partsOrderAwaitingApprovalPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> row = grid.getRows();
        int numRows = row.size();
        Row lastRow = row.get(numRows - 1);
        testData.put("totalCostAfterItemRemoved", new BigDecimal(lastRow.getCell("Cost").getText().replaceAll(",", "")));
        testData.put("totalNumberOfRowsDisplayedAfterDeletion", runtimeState.partsOrderAwaitingApprovalPage.getNumberOfDisplayedRows());
    }

    @And("^all items are deleted$")
    public void all_items_are_deleted() throws Throwable {
        POHelper.refreshPage();
        Grid grid;
        grid = runtimeState.partsOrderAwaitingApprovalPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<Row> rows = grid.getRows();
        runtimeState.scenario.write("Deleting all the items listed on the parts request");
        for (int i = rows.size(); i > 1; i--) {
            runtimeState.partsOrderAwaitingApprovalPage.deletePartRequest();
        }
        Grid newGrid = runtimeState.partsOrderAwaitingApprovalPage.getGrid();
        List<Row> row = newGrid.getRows();
        runtimeState.scenario.write("Total Cost After All Items Are Deleted: " + new BigDecimal(row.get(0).getCell("Cost").getText()));
    }

    @ContinueNextStepsOnException
    @Then("^rejecting the parts order is the only available option$")
    public void rejecting_parts_order_is_the_only_available_option() throws Exception {
        runtimeState.scenario.write("Asserting that the Approve button is disabled. ");
        assertFalse("Approve button is enabled! ", runtimeState.partsOrderAwaitingApprovalPage.isApprovalEnabled());

        runtimeState.scenario.write("Asserting that the Reject button is enabled. ");
        assertTrue("Reject button is not enabled! ", runtimeState.partsOrderAwaitingApprovalPage.isRejectButtonEnabled());
    }

    @And("^there is an item that can be deleted/amended$")
    public void there_is_an_item_that_can_be_deleted_Or_Amended() throws Exception {
        try {
            runtimeState.partsOrderAwaitingApprovalPage.isDeleteButtonEnabled();
        }
        catch(Exception e) {
            // This is not a pending exception. Should never get to this stage in the test and throw a pending
            //throw new PendingException("no part requests to delete");
            throw new Exception("No part requests to delete - an issue with the query in selecting data");
        }
        Grid grid;
        grid = runtimeState.partsOrderAwaitingApprovalPage.getGrid();
        int numRows = grid.getRows().size();
        int count = 0;
        assertNotNull("Unexpected Null Grid", grid);
        for (Row row : grid.getRows()) {
            if (count == 0) {//the first part in the table - the one to be deleted
                testData.put("costOfItem", new BigDecimal(row.getCell("Cost").getText().replaceAll(",", "")));
                testData.put("unitPriceOfItem", new BigDecimal(row.getCell("Unit Price").getText().replaceAll(",", "")));
                testData.put("orderQty", Double.parseDouble(row.getCell("Order Qty").getText()));
                count++;
            } else if (count == numRows - 1){//last row holds the total cost - has less TD elements than the rest of the table
                testData.put("totalCostBeforeItemRemoved", new BigDecimal(row.getCell("Cost").getText().replaceAll(",", "")));
            } else {
                count++;
            }
        }
        testData.put("reduceToQtyBefore", runtimeState.partsOrderAwaitingApprovalPage.getUnitQuantityReduced());
        testData.put("totalNumberOfRowsDisplayedBeforeDeletion", runtimeState.partsOrderAwaitingApprovalPage.getNumberOfDisplayedRows());
    }

    @ContinueNextStepsOnException
    @Then("^the reduced total cost is displayed$")
    public void the_reduced_total_cost_is_displayed() throws Exception {
        BigDecimal costOfItem = testData.getBigDecimal("costOfItem");
        BigDecimal totalBeforeItemRemoved = testData.getBigDecimal("totalCostBeforeItemRemoved");
        BigDecimal totalLessItemRemoved = totalBeforeItemRemoved.subtract(costOfItem).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newTotal = testData.getBigDecimal("totalCostAfterItemRemoved");
        runtimeState.scenario.write("Cost of Item Deleted: " + costOfItem + " total cost before item deleted: " + totalBeforeItemRemoved + " total cost after item deleted: " + newTotal);
        assertEquals("Incorrect total cost displayed!", totalLessItemRemoved, newTotal);
    }

    @And("^the correct number of parts requested rows are displayed$")
    public void the_correct_number_of_rows_is_displayed() throws Exception {
        int numberOfRowsBeforeDeletion = testData.getInt("totalNumberOfRowsDisplayedBeforeDeletion");
        int numberOfRowsAfterDeletion = testData.getInt("totalNumberOfRowsDisplayedAfterDeletion");
        runtimeState.scenario.write(
                "Number of rows before deletion: " + numberOfRowsBeforeDeletion
                + " .Number of rows after deletion: "
                + numberOfRowsAfterDeletion);
        assertEquals("Incorrect number of rows displayed!", (numberOfRowsBeforeDeletion - 1), numberOfRowsAfterDeletion);
    }

    @And("^the item quantity is reduced$")
    public void the_item_quantity_is_reduced() throws Exception {
        outputHelper.takeScreenshots();
        runtimeState.partsOrderAwaitingApprovalPage.reduceUnitQuantity();
        Grid grid;
        grid = runtimeState.partsOrderAwaitingApprovalPage.getGrid();
        int numRows = grid.getRows().size();
        int count = 0;
        assertNotNull("Unexpected Null Grid", grid);
        for (Row row : grid.getRows()) {
            if (count == 0) {//the first item in the table -
                testData.put("costOfItemAfterQtyReduced", new BigDecimal(row.getCell("Cost").getText().replaceAll(",", "")));
                count++;
            } else if (count == numRows - 1){//last row holds the total cost - has less TD elements than the rest of the table
                testData.put("totalCostAfterItemQtyReduced", new BigDecimal(row.getCell("Cost").getText().replaceAll(",", "")));
            } else {
                count++;
            }
        }
        testData.put("reduceToQtyAfter", runtimeState.partsOrderAwaitingApprovalPage.getUnitQuantityReduced());
    }


    @And("^the total item cost has been reduced$")
    public void the_item_cost_has_been_reduced() throws Exception {
        BigDecimal itemCostAfterQtyReduced = testData.getBigDecimal("costOfItemAfterQtyReduced");
        BigDecimal itemcostLessOneUnit = testData.getBigDecimal("costOfItem").subtract(testData.getBigDecimal("unitPriceOfItem"));
        assertEquals("Item cost is incorrect", itemCostAfterQtyReduced, itemcostLessOneUnit);

    }

    @And("^the total cost has been reduced$")
    public void the_total_cost_has_been_reduced() throws Exception {
        BigDecimal totalCostAfterQtyReduced = testData.getBigDecimal("totalCostAfterItemQtyReduced");
        BigDecimal itemcostLessOneUnit = testData.getBigDecimal("totalCostBeforeItemRemoved").subtract(testData.getBigDecimal("unitPriceOfItem"));
        assertEquals("Total cost is incorrect", totalCostAfterQtyReduced, itemcostLessOneUnit);
    }

    @And("^a message \"([^\"]*)\" is displayed below the Budget dropdown$")
    public void a_message_is_displayed_below_the_budget_dropdown(String expectedMessage) throws Throwable {
        String actualMessage = runtimeState.partsOrderAwaitingApprovalPage.getBudgetAlertMessage();
        assertEquals("Budget Alert message is not displayed ! ", expectedMessage, actualMessage);
        runtimeState.scenario.write("Budget alert message is displayed and is: " + expectedMessage);
    }

    @ContinueNextStepsOnException
    @Then("^the job details of parts order are ((?:displayed|displayed without dates))$")
    public void the_job_details_of_parts_order_are_displayed(String checkDates) throws Throwable {
        JobView jobView = jobViewDao.getByJobReference(testData.getInt("jobReference"));
        runtimeState.scenario.write("Asserting the Job Details: ");

        runtimeState.scenario.write("Asserting Job Reference is: " + jobView.getJobReference());
        assertEquals("Unexpected Job Reference ", jobView.getJobReference(), runtimeState.partsOrderAwaitingApprovalPage.getJobDetailsFieldData("Job Reference"));

        runtimeState.scenario.write("Asserting Site is: " + jobView.getName());
        assertEquals("Unexpected Site ", jobView.getName(), runtimeState.partsOrderAwaitingApprovalPage.getJobDetailsFieldData("Site"));

        runtimeState.scenario.write("Asserting Location within site is: " + jobView.getSubLocationName());
        assertEquals("Unexpected Location ", jobView.getSubLocationName().replaceAll("\\s+", ""), runtimeState.partsOrderAwaitingApprovalPage.getJobDetailsFieldData("Location within site").replaceAll("\\s+", ""));

        runtimeState.scenario.write("Asserting SubType/Classification is: " + jobView.getAssetSubTypeName() + " " + jobView.getAssetClassificationName());
        assertEquals("Unexpected SubType", (jobView.getAssetSubTypeName() + jobView.getAssetClassificationName()).replaceAll("\\s+", ""), runtimeState.partsOrderAwaitingApprovalPage.getJobDetailsFieldData("Subtype/Classification").replaceAll(" > |>|\\s+", ""));

        if (!checkDates.equalsIgnoreCase("displayed without dates")) {
            String expectedDate = DateHelper.dateAsString(jobView.getCreatedOn(), SHORT).toUpperCase();
            runtimeState.scenario.write("Asserting Logged is: " + expectedDate);
            assertEquals("Unexpected Logged", expectedDate, runtimeState.partsOrderAwaitingApprovalPage.getJobDetailsFieldData("Logged"));
        }

        runtimeState.scenario.write("Asserting Description is: " + jobView.getDescription());
        assertEquals("Unexpected Description ", jobView.getDescription().replaceAll("\\s+", ""), runtimeState.partsOrderAwaitingApprovalPage.getJobDetailsFieldData("Description").replaceAll("\\s+", ""));
    }

    @ContinueNextStepsOnException
    @And("^the parts order details are ((?:displayed|displayed without dates))$")
    public void the_parts_order_details_are_displayed(String checkDates) throws Throwable {
        Map<String, Object> partsOrderDetails = dbHelperParts.getPartsOrderDetails(testData.getInt("jobReference"));

        runtimeState.scenario.write("Asserting Purchase Order Number is: " + partsOrderDetails.get("PONumber"));
        assertEquals("Unexpected Purchase Order Number ", partsOrderDetails.get("PONumber"), runtimeState.partsOrderAwaitingApprovalPage.getPartsOrdersDetailsFieldData("Purchase Order Number"));

        runtimeState.scenario.write("Asserting Supplier is: " + partsOrderDetails.get("Supplier"));
        assertEquals("Unexpected Supplier ", partsOrderDetails.get("Supplier").toString().replaceAll("\\s+", ""), runtimeState.partsOrderAwaitingApprovalPage.getPartsOrdersDetailsFieldData("Supplier").replaceAll("\\s+", ""));

        runtimeState.scenario.write("Asserting Type: " + partsOrderDetails.get("Type"));
        assertEquals("Unexpected Type ", partsOrderDetails.get("Type"), runtimeState.partsOrderAwaitingApprovalPage.getPartsOrdersDetailsFieldData("Type"));

        runtimeState.scenario.write("Asserting Priority is: " + partsOrderDetails.get("Priority"));
        assertEquals("Unexpected Priority ", partsOrderDetails.get("Priority"), runtimeState.partsOrderAwaitingApprovalPage.getPartsOrdersDetailsFieldData("Priority"));

        runtimeState.scenario.write("Asserting Requestor Name is: " + partsOrderDetails.get("RaisedBy"));
        assertEquals("Unexpected Requestor Name ", partsOrderDetails.get("RaisedBy"), runtimeState.partsOrderAwaitingApprovalPage.getPartsOrdersDetailsFieldData("Requestor Name"));

        runtimeState.scenario.write("Asserting Funding Route is: " + partsOrderDetails.get("FundingRouteAlias"));
        assertEquals("Unexpected Funding Route ", partsOrderDetails.get("FundingRouteAlias"), runtimeState.partsOrderAwaitingApprovalPage.getPartsOrdersDetailsFieldData("Funding Route"));

        if (!checkDates.equalsIgnoreCase("displayed without dates")) {
            String dateRaised = partsOrderDetails.get("DateRaised").toString();
            dateRaised = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), dateRaised, DB_DATE_FORMAT);
            dateRaised = DateHelper.convert(dateRaised, DB_DATE_FORMAT, SHORT);
            runtimeState.scenario.write("Asserting Date Raised is: " + dateRaised);
            assertEquals("Unexpected Date Raised. Expected: ", dateRaised, runtimeState.partsOrderAwaitingApprovalPage.getPartsOrdersDetailsFieldData("Date Raised"));

            runtimeState.scenario.write("Asserting Required By is: " + partsOrderDetails.get("RequiredBy"));
            assertEquals("Unexpected Required By ", partsOrderDetails.get("RequiredBy"), runtimeState.partsOrderAwaitingApprovalPage.getPartsOrdersDetailsFieldData("Required By"));
        }
    }
}
