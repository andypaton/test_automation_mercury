package mercury.steps.portal.parts;

import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.PartCodeDao;
import mercury.database.dao.PartsRequestSummaryDao;
import mercury.database.models.PartCode;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.PartBuilder;
import mercury.databuilders.PartRequestBuilder;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.asserter.dbassertions.AssertPartsRequestSummary;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.portal.jobs.PartsRequestPage;
import mercury.runtime.RuntimeState;

public class PortalPartsRequestSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired AssertionFactory assertionFactory;
    @Autowired private DbHelper dbHelper;
    @Autowired private OutputHelper outputHelper;
    @Autowired private PartCodeDao partCodeDao;
    @Autowired private PartsRequestSummaryDao partsRequestSummaryDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;

    PartBuilder partBuilder; //TODO change to autowired
    List<PartRequestBuilder> partRequests = new ArrayList<>(); //TODO change to autowired

    @Given("^the user has a new Part$")
    public void part_data_generator() throws Exception {
        partBuilder = new PartBuilder.Builder().build();
        StringBuilder builder = new StringBuilder();
        builder.append("Requesting Part: ")
        .append(System.lineSeparator())
        .append("Part Number is ").append( partBuilder.getPartNumber()).append(", ")
        .append(System.lineSeparator())
        .append("Part Description is ").append( partBuilder.getPartDescription()).append(", ")
        .append(System.lineSeparator())
        .append("Manufacture Ref is ").append( partBuilder.getManufacturerRef()).append(", ")
        .append(System.lineSeparator())
        .append("Model is ").append( partBuilder.getModel()).append(", ")
        .append(System.lineSeparator())
        .append("Serial Number is ").append( partBuilder.getSerialNumber()).append(", ")
        .append(System.lineSeparator())
        .append("New Part is ").append( partBuilder.getNewPart());
        partBuilder.setNewPart(true);
        runtimeState.scenario.write(builder.toString());
    }

    @When("^the \"([^\"]*)\" form will be displayed$")
    public void load_part_request_form(String formName) throws Exception {
        runtimeState.partsRequestPage = new PartsRequestPage(getWebDriver()).get();
    }

    @When("^the user selects Part Not In List$")
    public void the_portal_part_request_part_not_in_list() throws Exception {
        runtimeState.partsRequestPage.setPartNotInList();
        outputHelper.takeScreenshots();
    }

    @When("^the user selects Part In List$")
    public void the_portal_part_request_part_in_list() throws Exception {
        PartCode partCode = partCodeDao.getRandomPartCode();
        partBuilder = new PartBuilder();
        partBuilder.setPartNumber(partCode.getPartCode());
        partBuilder.setPartDescription(partCode.getDescription());
        partBuilder.setManufacturerRef(partCode.getManufacturerRef());
        partBuilder.setNewPart(false);
        partBuilder.setUnitPrice(Double.valueOf(partCode.getUnitPrice().toString()));
        partBuilder.setSupplierCode(partCode.getSupplierCode());
        StringBuilder builder = new StringBuilder();
        builder.append("Requesting Part: ")
        .append(System.lineSeparator())
        .append("Part Code is ").append(partCode.getPartCode()).append(", ")
        .append(System.lineSeparator())
        .append("Part Description is ").append(partCode.getDescription()).append(", ")
        .append(System.lineSeparator())
        .append("Manufacturer Ref is ").append(partCode.getManufacturerRef()).append(", ")
        .append(System.lineSeparator())
        .append("Supplier Code is ").append(partCode.getSupplierCode().trim()).append(", ")
        .append(System.lineSeparator())
        .append("Unit Price is ").append(partCode.getUnitPrice());

        runtimeState.scenario.write(builder.toString());

        // Cannot use this code as the stored proc and the UI does not display information to search by  part description
        // e.g exec sp_executesql N'EXEC mry.usp_Get_PartsBySearchOnPartCodeAndDescription @search',N'@search text',@search='Fluorescent Linear Lamp,T8,Cool,4100K'
        // returns 34 (at time of execution) parts and its impossible to select the correct one
        //exec sp_executesql N'EXEC mry.usp_Get_Suppliers_For_Part_Description @PartDescription',N'@PartDescription varchar(37)',@PartDescription='Fluorescent Linear Lamp,T8,Cool,4100K'
        //		String partialPartDescription = stepHelper.getPartialPartDescription(partCode.getDescription());
        //		runtimeState.scenario.write("Searching for partial part " + partialPartDescription);
        //		runtimeState.partsRequestPage.selectPart(partialPartDescription, partCode.getDescription().trim());


        runtimeState.scenario.write("Searching for partial code " + partBuilder.getPartNumber());
        runtimeState.partsRequestPage.selectPart(partCode.getPartCode(), partCode.getDescription().trim());
    }

    @When("^the user selects Part In List with Unit Price between <([0-9.]+)> and <([0-9.]+)>$")
    public void the_portal_part_request_part_in_list_with_unit_price_in_range(Double minUnitPrice, Double maxUnitPrice) throws Exception {
        PartCode partCode = partCodeDao.getRandomPartCodeBetweenValues(minUnitPrice, maxUnitPrice);
        partBuilder = new PartBuilder();
        partBuilder.setPartNumber(partCode.getPartCode());
        partBuilder.setPartDescription(partCode.getDescription());
        partBuilder.setManufacturerRef(partCode.getManufacturerRef());
        partBuilder.setNewPart(false);
        partBuilder.setUnitPrice(Double.valueOf(partCode.getUnitPrice().toString()));
        partBuilder.setSupplierCode(partCode.getSupplierCode());
        StringBuilder builder = new StringBuilder();
        builder.append("Requesting Part: ")
        .append(System.lineSeparator())
        .append("Part Code is ").append(partCode.getPartCode()).append(", ")
        .append(System.lineSeparator())
        .append("Part Description is ").append(partCode.getDescription()).append(", ")
        .append(System.lineSeparator())
        .append("Manufacturer Ref is ").append(partCode.getManufacturerRef()).append(", ")
        .append(System.lineSeparator())
        .append("Supplier Code is ").append(partCode.getSupplierCode()).append(", ")
        .append(System.lineSeparator())
        .append("Unit Price is ").append(partCode.getUnitPrice());

        runtimeState.scenario.write(builder.toString());

        runtimeState.partsRequestPage.selectPart(partCode.getDescription().trim());
    }

    @When("^the user selects Part In List with Unit Price ((?:less|greater)) than the auto complete value$")
    public void the_portal_part_request_part_in_list_with_unit_price_in_range(String autoComplete) throws Exception {
        String autoCompleteValue = dbHelper.getSettingValue("PoAutoCompleteValue");

        Double lowerRange = null;
        Double upperRange = null;
        if ("less".equalsIgnoreCase(autoComplete)) {
            lowerRange = (double) 0;
            upperRange = Double.valueOf(autoCompleteValue);
        } else {
            lowerRange = Double.valueOf(autoCompleteValue);
            upperRange = (double) 1000.00;
        }

        PartCode partCode = partCodeDao.getRandomPartCodeBetweenValues(lowerRange, upperRange);
        partBuilder = new PartBuilder();
        partBuilder.setPartNumber(partCode.getPartCode());
        partBuilder.setPartDescription(partCode.getDescription());
        partBuilder.setManufacturerRef(partCode.getManufacturerRef());
        partBuilder.setNewPart(false);
        partBuilder.setUnitPrice(Double.valueOf(partCode.getUnitPrice().toString()));
        partBuilder.setSupplierCode(partCode.getSupplierCode());
        StringBuilder builder = new StringBuilder();
        builder.append("Requesting Part: ")
        .append(System.lineSeparator())
        .append("Part Code is ").append(partCode.getPartCode()).append(", ")
        .append(System.lineSeparator())
        .append("Part Description is ").append(partCode.getDescription()).append(", ")
        .append(System.lineSeparator())
        .append("Manufacturer Ref is ").append(partCode.getManufacturerRef()).append(", ")
        .append(System.lineSeparator())
        .append("Supplier Code is ").append(partCode.getSupplierCode()).append(", ")
        .append(System.lineSeparator())
        .append("Unit Price is ").append(partCode.getUnitPrice());

        runtimeState.scenario.write(builder.toString());

        runtimeState.partsRequestPage.selectPart(partCode.getDescription().trim());
    }


    @When("^the user selects a random Supplier$")
    public void the_portal_part_request_the_user_select_a_random_supplier() throws Throwable {
        runtimeState.partsRequestPage.selectRandomSupplier();
        runtimeState.scenario.write("Selected Supplier value and text : " + runtimeState.partsRequestPage.getSupplierValue() + " : " + runtimeState.partsRequestPage.getSupplierText());
    }

    @When("^the user selects the Supplier for Part In List$")
    public void the_portal_part_request_the_user_select_a_supplier_for_part() throws Throwable {
        runtimeState.partsRequestPage.setSupplierByValue(partBuilder.getSupplierCode().trim());
        runtimeState.scenario.write("Selected Supplier value and text : " + runtimeState.partsRequestPage.getSupplierValue() + " : " + runtimeState.partsRequestPage.getSupplierText());
    }

    @When("^the user enters a new Part Number$")
    public void the_portal_part_request_the_user_enter_a_new_Part_Number() throws Throwable {
        runtimeState.partsRequestPage.setPartNumber(partBuilder.getPartNumber());
        runtimeState.scenario.write("Selected Part Number :" + runtimeState.partsRequestPage.getPartNumber());
    }

    @When("^the user enters a new Part Description$")
    public void the_portal_part_request_the_user_enter_a_new_Part_Description() throws Throwable {
        runtimeState.partsRequestPage.setPartDescription(partBuilder.getPartDescription());
        runtimeState.scenario.write("Selected Part Description : " + runtimeState.partsRequestPage.getPartDescription());
    }

    @When("^the user enters a new Manufacturer Ref$")
    public void the_portal_part_request_the_user_enter_a_new_Manufacturer_Ref() throws Throwable {
        runtimeState.partsRequestPage.setManufacturingRef(partBuilder.getManufacturerRef());
        runtimeState.scenario.write ("Selected Manufacturer Ref : " + runtimeState.partsRequestPage.getManufactuerRef());
    }

    @When("^the user enters a new Model$")
    public void the_portal_part_request_the_user_enter_a_new_model() throws Throwable {
        runtimeState.partsRequestPage.setModel(partBuilder.getModel());
        runtimeState.scenario.write("Selected Model : " + runtimeState.partsRequestPage.getModel());
    }

    @When("^the user enters a new Serial Number$")
    public void the_portal_part_request_the_user_enter_a_new_Serial_Number() throws Throwable {
        runtimeState.partsRequestPage.setSerialNumber(partBuilder.getSerialNumber());
        runtimeState.scenario.write("Selected Serial Number : " + runtimeState.partsRequestPage.getSerialNumber());
    }

    @When("^the user enters a new Unit Price$")
    public void the_portal_part_request_the_user_enter_a_new_Unit_Price() throws Throwable {
        runtimeState.partsRequestPage.setUnitPrice(String.valueOf(partBuilder.getUnitPrice()));
        runtimeState.scenario.write("Selected Unit Price : " + runtimeState.partsRequestPage.getUnitPrice());
    }


    @When("^the user enters a new Unit Price between <([0-9.]+)> and <([0-9.]+)>$")
    public void the_portal_part_request_the_user_enter_a_new_Unit_Price_betweeb_range(Double lowerRange, Double upperRange) throws Throwable {
        partBuilder.setUnitPrice(DataGenerator.GenerateRandomDouble(lowerRange, upperRange));

        runtimeState.partsRequestPage.setUnitPrice(String.valueOf(partBuilder.getUnitPrice()));
        runtimeState.scenario.write("Selected Unit Price : " + runtimeState.partsRequestPage.getUnitPrice());
        outputHelper.takeScreenshots();
    }

    @When("^the user enters a new Unit Price ((?:less|greater)) than the auto complete value$")
    public void the_portal_part_request_the_user_enter_a_new_Unit_Price_in_range(String autoComplete) throws Throwable {
        String autoCompleteValue = dbHelper.getSettingValue("PoAutoCompleteValue");

        Double lowerRange = null;
        Double upperRange = null;
        if ("less".equalsIgnoreCase(autoComplete)) {
            lowerRange = (double) 0;
            upperRange = Double.valueOf(autoCompleteValue);
        } else {
            lowerRange = Double.valueOf(autoCompleteValue);
            upperRange = (double) 1000.00;
        }

        partBuilder.setUnitPrice(DataGenerator.GenerateRandomDouble(lowerRange, upperRange));
        runtimeState.partsRequestPage.setUnitPrice(String.valueOf(partBuilder.getUnitPrice()));
        runtimeState.scenario.write("Selected Unit Price : " + runtimeState.partsRequestPage.getUnitPrice());
    }


    @When("^the Priority \"([^\"]*)\" is entered$")
    public void the_portal_part_request_the_Priority_is_entered(String priority) throws Throwable {
        runtimeState.partsRequestPage.setPriority(priority);
        runtimeState.scenario.write("Selected Priority value and text : " + runtimeState.partsRequestPage.getPriorityValue() + " : " + runtimeState.partsRequestPage.getPriorityText());
    }

    @When("^the quantity \"([^\"]*)\" is entered$")
    public void the_portal_part_request_the_quantity_is_entered(String quantity) throws Throwable {
        int j = Integer.valueOf(quantity);
        for(int i = 0; i < j; ++i) {
            runtimeState.partsRequestPage.increaseQuantity();
        }
        runtimeState.scenario.write("Selected Quantity : " + runtimeState.partsRequestPage.getQuantity().toString());
    }

    @When("^the Delivery Method \"([^\"]*)\" is entered$")
    public void the_portal_part_request_the_delivery_method_is_entered(String deliveryMethod) throws Throwable {
        runtimeState.partsRequestPage.setDeliveryMethod(deliveryMethod);
        runtimeState.scenario.write("Slected Delivery Method value and text : " + runtimeState.partsRequestPage.getDeliveryMethodValue() + " : "  + runtimeState.partsRequestPage.getDeliveryMethodText());
    }

    @When("^the Delivery Address \"([^\"]*)\" is entered$")
    public void the_portal_part_request_the_delivery_address_is_entered(String deliverAddress) throws Throwable {
        runtimeState.partsRequestPage.setDeliveryAddress(deliverAddress);
        runtimeState.scenario.write("Selected Address value and text : " + runtimeState.partsRequestPage.getDeliveryAddressValue() + " : "  + runtimeState.partsRequestPage.getDeliveryAddressText());
    }

    @When("^the Add to Request List is clicked$")
    public void the_portal_part_request_the_Add_to_Request_List_is_clicked() throws Throwable {
        PartRequestBuilder partRequest = new PartRequestBuilder();

        // Pull back all the data before saving to the Awaiting Parts Request grid
        partRequest.setSupplier(runtimeState.partsRequestPage.getSupplierText());
        partRequest.setSupplierCode(runtimeState.partsRequestPage.getSupplierValue());
        partRequest.setModel(partBuilder.getModel());
        partRequest.setManufacturerRef(partBuilder.getManufacturerRef());
        partRequest.setSerialNumber(partBuilder.getSerialNumber());
        partRequest.setPartNumber(partBuilder.getPartNumber());
        partRequest.setPartDescription(partBuilder.getPartDescription());
        partRequest.setPriority(runtimeState.partsRequestPage.getPriorityText());
        //partRequest.setQuantity(runtimeState.partsRequestPage.getQuantity());  // need to fix double to integer formatting
        partRequest.setQuantity(1);  // need to fix double to integer formatting
        partRequest.setUnitPrice(runtimeState.partsRequestPage.getUnitPrice());
        partRequest.setDeliveryMethod(runtimeState.partsRequestPage.getDeliveryMethodText());
        partRequest.setDeliveryAddress(runtimeState.partsRequestPage.getDeliveryAddressText());
        partRequest.setNewPart(partBuilder.getNewPart());
        partRequests.add(partRequest);
        logger.debug(partRequest.toString());
        runtimeState.partsRequestPage.addToRequestList();
        outputHelper.takeScreenshots();
    }

    @When("^the Save Request is clicked$")
    public void the_portal_part_request_the_save_request_is_clicked() throws Throwable {
        runtimeState.createPartRequestModalPage = runtimeState.partsRequestPage.saveRequest();
        outputHelper.takeScreenshots();
        runtimeState.createPartRequestModalPage.createRequest();
    }

    @ContinueNextStepsOnException
    @Then("^the Add to Request List button is diabled$")
    public void the_portal_part_request_the_Add_to_Request_List_button_is_diabled() throws Throwable {
        assertTrue("Unexpected Add to Request List button enablement", runtimeState.partsRequestPage.getAddToListEnabled() == false );
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Part Number is populated$")
    public void the_portal_part_request_the_part_number_is_populated() throws Throwable {
        assertEquals("Unexpected Part No data", partBuilder.getPartNumber().toLowerCase() ,runtimeState.partsRequestPage.getPartNumber().toLowerCase());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Part Description is populated$")
    public void the_portal_part_request_the_part_description_is_populated() throws Throwable {
        assertEquals("Unexpected Part Description data", partBuilder.getPartDescription().toLowerCase() ,runtimeState.partsRequestPage.getPartDescription().toLowerCase());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Existing Parts Request grid is updated$")
    public void the_portal_part_request_the_Existing_Parts_Request_is_updated() throws Throwable {
        Grid grid = runtimeState.partsRequestPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);

        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");

        String[] expectedHeaders = { "Supplier", "Part No", "Part Description", "Qty", "Delivery Method", "Priority", "Sup. Ref", "Man. Ref", "Model", "Serial Number", ""};

        for (String header : grid.getHeaders()) {
            assertTrue("Expected " + expectedHeaders.toString() + " to contain " + header, ArrayUtils.contains(expectedHeaders, header));
        }
        assertTrue("Unexpected number of headers returned, found : " + grid.getHeaders().size() + " expected : " + expectedHeaders.length, expectedHeaders.length == grid.getHeaders().size());

        assertTrue("Expected " +partRequests.size()+ " items to be listed, but " + grid.getRows().size() + " displayed", partRequests.size() == grid.getRows().size());

        // Now assert each row is correct
        for ( Row row : grid.getRows()) {

            if(partRequests.get(0).getNewPart() == true) {
                assertEquals("Unexpected Supplier column data", partRequests.get(0).getSupplier(), row.getCell("Supplier").getText());
                assertEquals("Unexpected Part No column data", partRequests.get(0).getPartNumber(), row.getCell("Sup. Ref").getText());
                assertEquals("Unexpected Part No column data", partRequests.get(0).getManufacturerRef(), row.getCell("Man. Ref").getText());
                assertEquals("Unexpected Part No column data", partRequests.get(0).getModel(), row.getCell("Model").getText());
                assertEquals("Unexpected Part No column data", partRequests.get(0).getSerialNumber(), row.getCell("Serial Number").getText());
                assertEquals("Unexpected Qty column data", 1, Integer.valueOf(row.getCell("Qty").getText()).intValue());
                assertTrue("Unexpected Delivery Method column data", row.getCell("Delivery Method").getText().contains(partRequests.get(0).getDeliveryMethod()));
                assertEquals("Unexpected Priority column data", partRequests.get(0).getPriority(), row.getCell("Priority").getText());
            }
            if(partRequests.get(0).getNewPart() == false) {
                assertEquals("Unexpected Supplier column data", partRequests.get(0).getSupplier(), row.getCell("Supplier").getText());
                assertEquals("Unexpected Part No column data", partRequests.get(0).getPartNumber(), row.getCell("Part No").getText());
                assertEquals("Unexpected Part Description column data", normalize(partRequests.get(0).getPartDescription().toLowerCase()), normalize(row.getCell("Part Description").getText().toLowerCase()));
                assertEquals("Unexpected Qty column data", 1, Integer.valueOf(row.getCell("Qty").getText()).intValue());
                assertTrue("Unexpected Delivery Method column data", row.getCell("Delivery Method").getText().contains(partRequests.get(0).getDeliveryMethod()));
                assertEquals("Unexpected Priority column data", partRequests.get(0).getPriority(), row.getCell("Priority").getText());
            }
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Parts Request has been recorded in the database$")
    public void the_portal_part_request_the_parts_request_has_been_recorded_in_the_database() throws Throwable {
        AssertPartsRequestSummary assertPartsRequestSummary = new AssertPartsRequestSummary(
                partsRequestSummaryDao,
                this.testData.getInt("jobReference"),
                partRequests.get(0).getSupplierCode(),
                partRequests.get(0).getPartNumber(),
                partRequests.get(0).getManufacturerRef(),
                partRequests.get(0).getModel(),
                partRequests.get(0).getPartDescription(),
                partRequests.get(0).getSerialNumber(),
                Float.valueOf(partRequests.get(0).getUnitPrice().toString()),
                partRequests.get(0).getQuantity(),
                partRequests.get(0).getNewPart());
        assertionFactory.performAssertion(assertPartsRequestSummary);
    }

    @And("^the Delivery Method \"([^\"]*)\" and Delivery Address \"([^\"]*)\" is entered$")
    public void the_delivery_method_and_delivery_address_is_entered(String deliveryMethod, String deliveryAddress) throws Throwable {
        the_portal_part_request_the_delivery_method_is_entered(deliveryMethod);
        the_portal_part_request_the_delivery_address_is_entered(deliveryAddress);
    }

    @And("^the parts request form is completed with a part, in list, priced between <([0-9.]+)> and <([0-9.]+)>$")
    public void the_parts_request_form_is_completed_with_a_part_in_list_priced_between(Double lowerRange, Double upperRange) throws Throwable {
        the_portal_part_request_part_in_list_with_unit_price_in_range(lowerRange, upperRange);
        the_parts_request_form_is_completed();
    }

    @And("^the parts request form is completed with a part, not in list, priced between <([0-9.]+)> and <([0-9.]+)>$")
    public void the_parts_request_form_is_completed_with_a_part_not_in_list_priced_between_and(Double lowerRange, Double upperRange) throws Throwable {
        the_portal_part_request_part_not_in_list();
        the_portal_part_request_the_user_select_a_random_supplier();
        the_portal_part_request_the_user_enter_a_new_Part_Number();
        the_portal_part_request_the_user_enter_a_new_Part_Description();
        the_portal_part_request_the_user_enter_a_new_Manufacturer_Ref();
        the_portal_part_request_the_user_enter_a_new_model();
        the_portal_part_request_the_user_enter_a_new_Serial_Number();
        the_portal_part_request_the_user_enter_a_new_Unit_Price_betweeb_range(lowerRange, upperRange);

        the_parts_request_form_is_completed();
    }

    @And("^the parts request form is completed$")
    public void the_parts_request_form_is_completed() throws Throwable {
        the_portal_part_request_the_user_select_a_random_supplier();
        the_portal_part_request_the_Priority_is_entered("Same Day");
        the_portal_part_request_the_quantity_is_entered("1");
        the_delivery_method_and_delivery_address_is_entered("Direct To Store", "Job Store");
    }

    @When("^\"([^\"]*)\" new part with a price between between ([0-9.]+) and ([0-9.]+) is ordered for \"([^\"]*)\" on the \"([^\"]*)\"$")
    public void a_new_part_with_a_price_between_low_and_high_is_ordered_for_delivery_method_on_the_priority(String quantity, Double lowerRange, Double upperRange, String deliveryMethod, String priority) throws Throwable {
        load_part_request_form("Parts Request");
        part_data_generator();
        the_portal_part_request_part_not_in_list();
        the_portal_part_request_the_user_select_a_random_supplier();
        the_portal_part_request_the_user_enter_a_new_Part_Number();
        the_portal_part_request_the_user_enter_a_new_Part_Description();
        the_portal_part_request_the_user_enter_a_new_Manufacturer_Ref();
        the_portal_part_request_the_user_enter_a_new_model();
        the_portal_part_request_the_user_enter_a_new_Serial_Number();
        the_portal_part_request_the_user_enter_a_new_Unit_Price_betweeb_range(lowerRange, upperRange);
        the_portal_part_request_the_Priority_is_entered(priority);
        the_portal_part_request_the_quantity_is_entered(quantity);
        the_portal_part_request_the_delivery_method_is_entered(deliveryMethod);
        the_portal_part_request_the_Add_to_Request_List_is_clicked();
        the_portal_part_request_the_save_request_is_clicked();
    }

}
