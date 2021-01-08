package mercury.steps.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.gridV3.Grid;
import mercury.runtime.RuntimeState;

public class AdminPpmSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelper dbHelper;
    @Autowired private TestData testData;

    @When("^an Asset is added to the PPM Type$")
    public void asset_is_added_to_ppm_type() {
        runtimeState.adminPpmTypeAssetMappingPage = runtimeState.adminPpmTypeAssetPage.addNewPpmTypeAsset();

    }

    @When("^the Add a new PPM Type at Asset Level$")
    public void add_new_ppm_type_at_asset_level() {
        runtimeState.adminAddNewPpmTypePage = runtimeState.adminPpmsPage.addNewPpmType();

        String ppmTypeName = "TestAuto" + System.currentTimeMillis();
        runtimeState.adminAddNewPpmTypePage.enterTextIntoField("PPM Type Name", ppmTypeName);
        testData.put("ppmTypeName", ppmTypeName);
        runtimeState.adminAddNewPpmTypePage.selectOptionFromDropdown("PPM Type Category", "PPM");
        String assetTypeName = dbHelper.getRandomAssetTypeName();
        runtimeState.adminAddNewPpmTypePage.selectOptionFromDropdown("Main Type", assetTypeName);
        testData.put("assetTypeName", assetTypeName);
        String scheduleAdvanceWeeks = "1";
        runtimeState.adminAddNewPpmTypePage.enterTextIntoField("Schedule Advance Weeks", scheduleAdvanceWeeks);
        String[] frequency = {"Day", "Week", "Month", "Year", "Bi-Monthly"};
        Random random = new Random();
        int randomIndex = random.nextInt(frequency.length);
        runtimeState.adminAddNewPpmTypePage.selectOptionFromDropdown("Frequency Increment", frequency[randomIndex]);
        testData.put("frequency", frequency[randomIndex]);
        runtimeState.adminAddNewPpmTypePage.save();

        runtimeState.adminPpmTypeAssetPage = runtimeState.adminAddNewPpmTypePage.ppmTypeAtAssetLevel();
    }

    @When("^the Add a new PPM Type button is pressed$")
    public void add_new_ppm_type_button_pressed() {
        runtimeState.adminAddNewPpmTypePage = runtimeState.adminPpmsPage.addNewPpmType();
    }

    @ContinueNextStepsOnException
    @Then("^the Add a new PPM Type page is displayed as expected$")
    public void add_new_ppm_type_page_displayed_as_expected() {
        runtimeState.scenario.write("Asserting that the system displays the correct text");
        String expectedText = "All fields are required unless marked optional";
        String actualText = runtimeState.adminAddNewPpmTypePage.getFormInstructionsText();
        assertEquals("Expected: " + expectedText + " but was: " + actualText, expectedText, actualText);

        runtimeState.scenario.write("Asserting that all fields are displayed");
        assertTrue("PPM Type Name field is not displayed", runtimeState.adminAddNewPpmTypePage.isQuestionTextboxDisplayed("PPM Type Name"));
        assertTrue("PPM Type Category field is not displayed", runtimeState.adminAddNewPpmTypePage.isQuestionDropdownDisplayed("PPM Type Category"));
        assertTrue("Main Type field is not displayed", runtimeState.adminAddNewPpmTypePage.isQuestionDropdownDisplayed("Main Type"));
        assertTrue("Schedule Advance Weeks field is not displayed", runtimeState.adminAddNewPpmTypePage.isQuestionTextboxDisplayed("Schedule Advance Weeks"));
        assertTrue("Frequency Increment field is not displayed", runtimeState.adminAddNewPpmTypePage.isQuestionDropdownDisplayed("Frequency Increment"));
        assertTrue("Number of Annual Occurrences field is not displayed", runtimeState.adminAddNewPpmTypePage.isQuestionTextboxDisplayed("Number of Annual Occurrences"));
    }

    @ContinueNextStepsOnException
    @Then("^the PPM Types page is displayed as expected$")
    public void ppm_types_page_displayed_as_expected() {
        runtimeState.scenario.write("Asserting that all filters are displayed");
        assertTrue("Name filter is not displayed", runtimeState.adminPpmsPage.isNameFilterDisplayed());
        assertTrue("Classification filter is not displayed", runtimeState.adminPpmsPage.isClassificationFilterDisplayed());
        assertTrue("Asset Type filter is not displayed", runtimeState.adminPpmsPage.isAssetTypeFilterDisplayed());
        assertTrue("Callout Advance Weeks filter is not displayed", runtimeState.adminPpmsPage.isCalloutAdvanceWeeksFilterDisplayed());

        runtimeState.scenario.write("Asserting that 'Clear Filter' and 'Search' buttons are displayed");
        assertTrue("Clear Filter button is not displayed", runtimeState.adminPpmsPage.isClearFilterButtonDisplayed());
        assertTrue("Search button is not displayed", runtimeState.adminPpmsPage.isSearchButtonDisplayed());

        runtimeState.scenario.write("Asserting that the grid is not empty");
        Grid grid = runtimeState.adminPpmsPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);

        runtimeState.scenario.write("Asserting that the grid headers are correct");
        List<String> expectedGridHeaders = Arrays.asList("Name", "Classification", "Asset Type", "Callout Advance Weeks", "Certificate at Asset Level", "Actions");
        List<String> actualGridHeaders = grid.getHeaders();
        assertEquals("Expected: " + expectedGridHeaders + " but was: " + actualGridHeaders, expectedGridHeaders, actualGridHeaders);

        runtimeState.scenario.write("Asserting that the records per page defaults as 10");
        String recordsPerPage = runtimeState.adminPpmsPage.getRecordsPerPage();
        assertEquals("Expected: 10 but was: " + recordsPerPage, "10", recordsPerPage);
    }

    @ContinueNextStepsOnException
    @Then("^the PPM screen is displayed as expected$")
    public void ppm_screen_displayed_as_expected() {
        runtimeState.scenario.write("Asserting that all menu items are displayed");
        assertTrue("Purchase Order Configuration menu is not displayed", runtimeState.adminPpmsPage.isMenuItemDisplayed("PPM Types"));
        assertTrue("PPM Configuration menu is not displayed", runtimeState.adminPpmsPage.isMenuItemDisplayed("PPMs"));
    }

    @ContinueNextStepsOnException
    @Then("^the PPM Types menu is displayed by default$")
    public void ppm_types_menu_displayed() {
        runtimeState.scenario.write("Asserting that PPM Types is displayed as default");
        String selectedMenuItem = runtimeState.adminPpmsPage.getSelectedMenuItem();
        assertTrue(selectedMenuItem.equalsIgnoreCase("PPM Types"));
    }

}
