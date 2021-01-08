package mercury.steps.admin;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.dbhelper.DbHelperOrganisation;
import mercury.pageobject.web.admin.AdminOrganisationPage;
import mercury.runtime.RuntimeState;

public class AdminOrganisationSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperOrganisation dbHelperOrganisation;

    @ContinueNextStepsOnException
    @Then("^the user is taken to the Organisational Structure Home Page$")
    public void the_user_is_taken_to_the_Organisational_Structure_Home_Page() throws Throwable {
        String currentURL = getWebDriver().getCurrentUrl();
        assertTrue("Page is not loaded! ", currentURL.contains("organisation"));
    }

    @And("^the system displays a key explaining the colour coding$")
    public void the_system_displays_a_key_explaining_the_colour_coding() throws Throwable {
        runtimeState.scenario.write("Asserting that the colour key is displayed. ");
        assertTrue("Colour key is not displayed! ", runtimeState.adminOrganisationPage.isColourKeyDisplayed());
    }

    @And("^the current active hierarchies are displayed$")
    public void the_current_active_hierarchies_are_displayed() throws Throwable {
        assertTrue("There are no hierarchy options displayed. ", runtimeState.adminOrganisationPage.isActiveHierarchiesDisplayed());
        List<String> hierarchyOptions = runtimeState.adminOrganisationPage.getHierarchyOptions();
        runtimeState.scenario.write("Hierarchy options found: " + hierarchyOptions);
        outputHelper.takeScreenshots();
    }

    @When("^the user clicks on the \"([^\"]*)\" hierarchy$")
    public void the_user_clicks_on_the_hierarchy(String option) throws Throwable {
        runtimeState.adminOrganisationPage.selectHierarchyOption(option);
        runtimeState.scenario.write("Asserting that the hierarchy option selected is " + option);
    }

    @ContinueNextStepsOnException
    @Then("^the system displays correct divisions$")
    public void the_system_displays_correct_divisions() throws Throwable {
        assertTrue("There are no hierarchy divisions displayed. ", runtimeState.adminOrganisationPage.isHierarchyDivisionsDisplayed());
        List<String> hierarchyDivisions = runtimeState.adminOrganisationPage.getHierarchyDivisions();
        runtimeState.scenario.write("Hierarchy divisions found: " + hierarchyDivisions);
        outputHelper.takeScreenshots();
    }

    @When("^the user clicks on the \"([^\"]*)\" option$")
    public void the_user_clicks_on_the_option(String option) throws Throwable {
        runtimeState.adminOrganisationPage.selectOption(option);
        runtimeState.scenario.write("Asserting that the option selected is " + option);
    }

    @When("^the user clicks on the \"([^\"]*)\" tech position$")
    public void the_user_clicks_on_the_tech_position(String option) throws Throwable {
        runtimeState.adminOrganisationPage.selectTechPosition(option);
        runtimeState.scenario.write("Asserting that the tech position selected is " + option);
    }

    @When("^a random tech position is selected$")
    public void a_random_tech_position_is_selected() throws Throwable {
        Map<String, Object> orgStructureForProfile = dbHelperOrganisation.getOrgStructureForTechPosition();
        testData.put("orgStructureForProfile", orgStructureForProfile.get("Breadcrumb"));
        String orgStructure = testData.getString("orgStructureForProfile");
        runtimeState.scenario.write("Organisation structure for the chosen tech position: " + orgStructure);
        String[] orgStructureArray = orgStructure.split(">");
        int size = orgStructureArray.length;
        String firstOption = orgStructureArray[0].trim();
        the_user_clicks_on_the_hierarchy(firstOption);
        for (int i = 1; i < size - 1; i++) {
            String option = orgStructureArray[i].trim();
            the_user_clicks_on_the_option(option);
        }
        String lastOption = orgStructureArray[size - 1].trim();
        the_user_clicks_on_the_tech_position(lastOption);
    }

    @When("^a random location to be moved is selected$")
    public void a_random_location_to_be_moved_is_selected() throws Throwable {
        a_manager_is_available();
        a_site_is_available();
        String siteLocation = runtimeState.adminOrganisationPage.getSiteName();
        assertNotNull("There are no sites available for this tech position. ", siteLocation);
        runtimeState.scenario.write("Site Location selected is " + siteLocation);
        testData.put("siteLocation", siteLocation);
        runtimeState.adminOrganisationPage.selectRandomLocation(siteLocation);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the manager is added to the site$")
    public void the_manager_is_added_to_the_site() throws Throwable {
        the_user_is_navigated_to_the_organisation_structure_page();
        String manager = testData.getString("siteManager");
        List<String> availableManagers = runtimeState.adminOrganisationPage.getAvailableManagers();
        assertTrue("The manager " + manager + " has not been added to the site!", availableManagers.contains(manager));
        runtimeState.scenario.write("Asserting that the site location " + manager + " is still available for this tech position. ");
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the manager is not added to the site$")
    public void the_manager_is_not_added_to_the_site() throws Throwable {
        the_user_is_navigated_to_the_organisation_structure_page();
        String manager = testData.getString("siteManager");
        List<String> availableManagers = runtimeState.adminOrganisationPage.getAvailableManagers();
        assertFalse("The manager " + manager + " has been added to the site!", availableManagers.contains(manager));
        runtimeState.scenario.write("Asserting that the site location " + manager + " is still available for this tech position. ");
        outputHelper.takeScreenshots();
    }

    @When("^the alert message is displayed$")
    public void the_alert_message_is_displayed() throws Throwable {
        runtimeState.adminOrganisationPage.isAlertMessageDisplayed();
        String alertMessage = runtimeState.adminOrganisationPage.getAlertMessage();
        runtimeState.scenario.write("Alert message displayed is '" + alertMessage + "'");
    }

    @And("^a manager is available$")
    public void a_manager_is_available() throws Throwable {
        runtimeState.scenario.write("Asserting that there is a manager available. ");
        List<String> availableManagers = runtimeState.adminOrganisationPage.getAvailableManagers();
        if (availableManagers.isEmpty()) {
            runtimeState.scenario.write("There is no manager available. Searching for another tech position.");
            POHelper.refreshPage();
            a_random_tech_position_is_selected();
            a_random_location_to_be_moved_is_selected();
        } else {
            runtimeState.scenario.write("Managers found: " + availableManagers);
        }
    }

    @And("^a site is available$")
    public void a_site_is_available() throws Throwable {
        runtimeState.scenario.write("Asserting that there is a site available. ");
        List<String> availableSites = runtimeState.adminOrganisationPage.getAvailableSites();
        if (availableSites.isEmpty()) {
            runtimeState.scenario.write("There is no site available. Searching for another tech position.");
            a_random_tech_position_is_selected();
            a_random_location_to_be_moved_is_selected();
        } else {
            runtimeState.scenario.write("Sites found: " + availableSites);
        }
    }

    @When("^a random manager is selected$")
    public void a_random_manager_is_selected() throws Throwable {
        String manager = runtimeState.adminOrganisationPage.selectRandomManager();
        testData.put("siteManager", manager);
        runtimeState.scenario.write("Manager selected is " + manager);
        outputHelper.takeScreenshots();
    }

    @When("^the \"([^\"]*)\" button on the \"([^\"]*)\" is clicked$")
    public void the_button_on_the_something_is_clicked(String buttonName, String page) throws Throwable {
        runtimeState.adminOrganisationPage.clickSaveOrCancelButton(buttonName);
        outputHelper.takeScreenshots();
    }

    @When("^the \"([^\"]*)\" manager button is clicked$")
    public void the_button_is_clicked(String buttonName) throws Throwable {
        runtimeState.adminOrganisationPage.clickAddOrRemoveButton(buttonName);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the user is navigated to the organisation structure page$")
    public void the_user_is_navigated_to_the_organisation_structure_page() throws Throwable {
        runtimeState.adminOrganisationPage = new AdminOrganisationPage(getWebDriver()).get();
    }

    @ContinueNextStepsOnException
    @Then("^the site location is available for the tech position$")
    public void the_site_location_is_available_for_the_tech_position() throws Throwable {
        String location = testData.getString("siteLocation");
        List<String> availableSites = runtimeState.adminOrganisationPage.getAvailableSites();
        runtimeState.scenario.write("Asserting that the site location '" + location + "' is still available for this tech position. ");
        assertTrue("The site location " + location + " is not available!", availableSites.contains(location));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the site location is not available for the tech position$")
    public void the_site_location_is_not_available_for_the_tech_position() throws Throwable {
        String location = testData.getString("siteLocation");
        List<String> availableSites = runtimeState.adminOrganisationPage.getAvailableSites();
        if (availableSites.isEmpty()) {
            runtimeState.scenario.write("There are no sites available for this tech position. ");
        }
        runtimeState.scenario.write("Available sites" + availableSites);
        runtimeState.scenario.write("Asserting that the site location '" + location + "' is no longer available for this tech position. ");
        assertFalse("The site location " + location + " is still available!", availableSites.contains(location));
        outputHelper.takeScreenshots();
    }
}
