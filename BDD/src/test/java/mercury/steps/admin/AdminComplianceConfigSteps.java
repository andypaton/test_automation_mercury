package mercury.steps.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperUsers;
import mercury.runtime.RuntimeState;

public class AdminComplianceConfigSteps {

    @Autowired
    private RuntimeState runtimeState;
    @Autowired
    private TestData testData;
    @Autowired
    private OutputHelper outputHelper;
    @Autowired
    private DbHelperUsers dbHelperUsers;

    @When("^the user profile is edited and compliance \"([^\"]*)\" selected and save the changes$")
    public void the_user_profile_is_edited_and_compliance_selected_and_save_the_changes(String permission_name) throws Throwable {
        runtimeState.adminResourcesAndUsersPage.searchForUserProfile(testData.getString("userProfileName"));
        runtimeState.adminEditUserProfilePage = runtimeState.adminResourcesAndUsersPage.editUserProfile(testData.getString("userProfileName"));

        runtimeState.adminAddNewUserProfilePage.selectPermission(permission_name);
        outputHelper.takeScreenshots();
        runtimeState.adminEditUserProfilePage.saveProfile();
        runtimeState.adminResourcesAndUsersPage.isAddNewUserProfileButtonDisplayed();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" is saved to the database$")
    public void the_is_saved_to_the_database(String permissionName) throws Throwable {
        runtimeState.adminResourcesAndUsersPage.searchForUserProfile(testData.getString("userProfileName"));
        outputHelper.takeScreenshots();

        List<String> actualUserProfile = dbHelperUsers.getComplianceConfigResourceProfile(testData.getString("userProfileName"));
        String isPermissionPresent = actualUserProfile.stream().filter(result -> result.equals(permissionName)).collect(Collectors.toList()).get(0);
        runtimeState.scenario.write("Permission Details are: " + permissionName);
        assertEquals(permissionName, isPermissionPresent);   
    }
    
    @ContinueNextStepsOnException
    @Then("^the user is returned to the user profiles main screen$")
    public void the_user_is_returned_to_the_user_profiles_main_screen() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.isAddNewUserProfileButtonDisplayed();
    }

    @ContinueNextStepsOnException
    @Then("^certificate configuration screen is displayed as expected$")
    public void certificate_configuration_screen_is_displayed_as_expected() throws Throwable {
        runtimeState.adminComplianceConfigPage.isAddNewCertificateTypeButtonDisplayed();
        runtimeState.scenario.write("Asserting that all menu items are displayed");
        assertTrue("Certificate Configuration menu is not displayed", runtimeState.adminComplianceConfigPage.isMenuItemDisplayed("Certificate Types"));
    }
}