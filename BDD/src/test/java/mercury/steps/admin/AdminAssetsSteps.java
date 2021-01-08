package mercury.steps.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.AssetHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.gridV3.Grid;
import mercury.runtime.RuntimeState;

public class AdminAssetsSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private AssetHelper assetHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private OutputHelper outputHelper;

    @When("^the Asset is edited$")
    public void asset_is_edited() {
        runtimeState.adminAssetsPage.filterOnAssetTag(testData.getString("assetTag"));
        runtimeState.adminEditAssetPage = runtimeState.adminAssetsPage.editAsset();
        String clientAssetReference = DataGenerator.GenerateRandomString(8, 8, 0, 0, 8, 0);
        runtimeState.adminEditAssetPage.enterClientAssetReference(clientAssetReference);
        testData.put("clientAssetReference", clientAssetReference);
        outputHelper.takeScreenshots();
        runtimeState.adminEditAssetPage.clickSaveButton();
    }

    @When("^a new Asset is added$")
    public void new_asset_is_added() throws ParseException {
        runtimeState.adminAddNewAssetPage = runtimeState.adminAssetsPage.addNewAsset();

        String assetTag = "TestAuto" + System.currentTimeMillis();
        runtimeState.adminAddNewAssetPage.enterAssetTag(assetTag);
        testData.put("assetTag", assetTag);

        Map<String, Object> assetDetails = dbHelper.getAssetDetails();
        runtimeState.scenario.write("Asset Details are: " + assetDetails);
        String assetType = assetDetails.get("AssetType").toString();
        runtimeState.adminAddNewAssetPage.selectAssetType(assetType);
        testData.put("assetType", assetType);
        String assetSubtype = assetDetails.get("AssetSubtype").toString();
        runtimeState.adminAddNewAssetPage.selectAssetSubtype(assetSubtype);
        testData.put("assetSubtype", assetSubtype);
        String assetClassification = runtimeState.adminAddNewAssetPage.selectRandomAssetClassification();
        if (assetClassification.equalsIgnoreCase("No classification")) {
            testData.put("assetClassification", "");
        } else {
            testData.put("assetClassification", assetClassification);
        }

        String site = runtimeState.adminAddNewAssetPage.selectRandomSite();
        testData.put("site", site);
        runtimeState.adminAddNewAssetPage.selectRandomArea();
        String location = runtimeState.adminAddNewAssetPage.selectRandomLocation();
        testData.put("location", location);
        runtimeState.adminAddNewAssetPage.selectRandomSubLocation();

        runtimeState.adminAddNewAssetPage.selectRandomManufacturer();
        String modelName = DataGenerator.GenerateRandomString(8, 8, 8, 0, 0, 0);
        runtimeState.adminAddNewAssetPage.enterModelName(modelName);
        testData.put("modelName", modelName);
        String modelNumber = DataGenerator.GenerateRandomString(8, 8, 0, 0, 8, 0);
        runtimeState.adminAddNewAssetPage.enterModelNumber(modelNumber);
        testData.put("modelNumber", modelNumber);
        String serialNumber = DataGenerator.GenerateRandomString(8, 8, 0, 0, 8, 0);
        runtimeState.adminAddNewAssetPage.enterSerialNumber(serialNumber);
        testData.put("serialNumber", serialNumber);

        Random random = new Random();
        int lifeCycleCosting = random.nextInt(101);
        runtimeState.adminAddNewAssetPage.enterLifeCycleCosting(String.valueOf(lifeCycleCosting));

        String note = DataGenerator.generateRandomSentence();
        runtimeState.adminAddNewAssetPage.enterNote(note);
        runtimeState.adminAddNewAssetPage.clickSaveNoteButton();

        outputHelper.takeScreenshots();
        runtimeState.adminAddNewAssetPage.clickSaveButton();
    }

    @ContinueNextStepsOnException
    @Then("^the Asset is present in the Asset Register$")
    public void asset_in_asset_register() throws Exception {
        // Adding a wait method to test if it will fix intermittent timeout issue on Advocate
        Thread.sleep(5000);
        runtimeState.adminAssetsPage.filterOnAssetTag(testData.getString("assetTag"));

        runtimeState.scenario.write("Asserting that the grid shows the correct Asset");
        Grid grid = runtimeState.adminAssetsPage.getAssetRegisterGrid();
        outputHelper.takeScreenshots();
        String delimiter = ",";

        List<String> expectedAssetTag = Arrays.asList(testData.getString("assetTag"));
        List<String> actualAssetTag = grid.getColumnTextAndSubText("Asset Tag");
        assertEquals("Expected: " + expectedAssetTag + " but was: " + actualAssetTag,
                expectedAssetTag, actualAssetTag);

        List<String> modelNameAndNumber = grid.getColumnTextAndSubText("Model Name");
        String expectedModelNameAndNumber = testData.getString("modelName") + ", " + testData.getString("modelNumber");
        String actualModelNameAndNumber = String.join(delimiter, modelNameAndNumber);
        assertEquals("Expected: " + expectedModelNameAndNumber + " but was: " + actualModelNameAndNumber,
                expectedModelNameAndNumber, actualModelNameAndNumber);

        List<String> expectedSerialNumber = Arrays.asList(testData.getString("serialNumber"));
        List<String> actualSerialNumber = grid.getColumnTextAndSubText("Serial Number");
        assertEquals("Expected: " + expectedSerialNumber + " but was: " + actualSerialNumber,
                expectedSerialNumber, actualSerialNumber);

        List<String> expectedAssetType = Arrays.asList(testData.getString("assetType"));
        List<String> actualAssetType = grid.getColumnTextAndSubText("Type");
        assertEquals("Expected: " + expectedAssetType + " but was: " + actualAssetType,
                expectedAssetType, actualAssetType);

        List<String> expectedAssetSubtype = Arrays.asList(testData.getString("assetSubtype"));
        List<String> actualAssetSubtype = grid.getColumnTextAndSubText("Subtype");
        assertEquals("Expected: " + expectedAssetSubtype + " but was: " + actualAssetSubtype,
                expectedAssetSubtype, actualAssetSubtype);

        List<String> expectedAssetClassification = Arrays.asList(testData.getString("assetClassification"));
        List<String> actualAssetClassification = grid.getColumnTextAndSubText("Classification");
        assertTrue("Expected: " + expectedAssetClassification + " but was: " + actualAssetClassification,
                actualAssetClassification.containsAll(expectedAssetClassification));

        List<String> siteAndLocation = grid.getColumnTextAndSubText("Site");
        String expectedSiteAndLocation = testData.getString("site") + ", " + testData.getString("location");
        String actualSiteAndLocation = String.join(delimiter, siteAndLocation);
        assertEquals("Expected: " + expectedSiteAndLocation + " but was: " + actualSiteAndLocation,
                expectedSiteAndLocation, actualSiteAndLocation);

        List<String> expectedStatus = Arrays.asList("Active");
        List<String> actualStatus = grid.getColumnTextAndSubText("Status");
        assertEquals("Expected: " + expectedStatus + " but was: " + actualStatus,
                expectedStatus, actualStatus);

        assetHelper.deleteAsset();
    }

    @ContinueNextStepsOnException
    @Then("^an Audit event has been created$")
    public void audit_event_has_been_created() {
        runtimeState.adminAssetsPage.filterOnAssetTag(testData.getString("assetTag"));
        runtimeState.adminEditAssetPage = runtimeState.adminAssetsPage.editAsset();
        runtimeState.adminEditAssetPage.expandAuditHistorySection();
        outputHelper.takeScreenshots();

        runtimeState.scenario.write("Asserting that Audit History Event is correct");
        String expectedAuditHistoryEvent = "Asset Client Asset reference was changed from 'null' to '" + testData.getString("clientAssetReference") + "'.";
        String actualAuditHistoryEvent = runtimeState.adminEditAssetPage.getAuditHistoryEvent();
        assertEquals("Expected: " + expectedAuditHistoryEvent + " but was: " + actualAuditHistoryEvent,
                expectedAuditHistoryEvent, actualAuditHistoryEvent);

        assetHelper.deleteAsset();
    }

    @ContinueNextStepsOnException
    @Then("^the Asset screen is displayed as expected$")
    public void import_configuration_screen_displayed_as_expected() {
        runtimeState.scenario.write("Asserting that all menu items are displayed");
        assertTrue("Purchase Order Configuration menu is not displayed", runtimeState.adminAssetsPage.isMenuItemDisplayed("Asset Configuration"));
        assertTrue("PPM Configuration menu is not displayed", runtimeState.adminAssetsPage.isMenuItemDisplayed("Fault Type"));
        assertTrue("Finance Recode Import menu is not displayed", runtimeState.adminAssetsPage.isMenuItemDisplayed("Manufacturers"));
        assertTrue("PPM Configuration menu is not displayed", runtimeState.adminAssetsPage.isMenuItemDisplayed("Asset Register"));
        assertTrue("Finance Recode Import menu is not displayed", runtimeState.adminAssetsPage.isMenuItemDisplayed("Site/Main Type Tagging"));
    }

    @ContinueNextStepsOnException
    @Then("^the Asset Register menu is displayed by default$")
    public void asset_register_menu_displayed() {
        runtimeState.scenario.write("Asserting that Asset Register is displayed as default");
        String selectedMenuItem = runtimeState.adminAssetsPage.getSelectedMenuItem();
        assertTrue(selectedMenuItem.equalsIgnoreCase("Asset Register"));
    }

    @ContinueNextStepsOnException
    @Then("^the Asset Register page is displayed as expected$")
    public void asset_register_page_displayed_as_expected() {
        runtimeState.scenario.write("Asserting that the 'Add new asset' button is displayed");
        assertTrue(runtimeState.adminAssetsPage.isAddNewAssetButtonDisplayed());

        runtimeState.scenario.write("Asserting that the grid is not empty");
        Grid grid = runtimeState.adminAssetsPage.getAssetRegisterGrid();
        assertNotNull("Unexpected Null Grid", grid);

        runtimeState.scenario.write("Asserting that the grid headers are correct");
        List<String> expectedGridHeaders = Arrays.asList("Asset Tag", "Model Name", "Serial Number", "Type", "Subtype", "Classification", "Site", "Status", "Edit");
        List<String> actualGridHeaders = grid.getHeaders();
        assertEquals("Expected: " + expectedGridHeaders + " but was: " + actualGridHeaders ,expectedGridHeaders, actualGridHeaders);
    }

}
