package mercury.steps.admin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.Toggles;
import mercury.helpers.ToggleHelper;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.runtime.RuntimeState;

public class AdminSystemFeatureToggleSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private ToggleHelper toggleHelper;
    @Autowired private Toggles toggles;

    @When("^the following System Feature Toggles are set to \"([^\"]*)\":$")
    public void the_following_System_Feature_Toggles_are_set_to(String value, List<String> featureToggles) {

        if (!Boolean.valueOf(System.getProperty("setSystemToggles"))) {

            for (String toggle : featureToggles) {
                toggles.addToList("featureToggles", toggle);
                if ("ON".equalsIgnoreCase(value)) {
                    runtimeState.adminSystemFeatureTogglePage.toggleOnFeature(toggle);
                } else {
                    runtimeState.adminSystemFeatureTogglePage.toggleOffFeature(toggle);
                }
            }

        } else {
            throw new PendingException("System configured not to allow toggle changes, please re-run enabling setting of System Toggles");
        }
    }

    @When("^the following System Sub Feature Toggles are set to \"([^\"]*)\":$")
    public void the_following_Sub_System_Feature_Toggles_are_set_to(String value, List<String> featureToggles) {

        if (!Boolean.valueOf(System.getProperty("setSystemToggles"))) {
            for (String toggle : featureToggles) {
                toggles.addToList("subFeatureToggles", toggle);
                if ("ON".equalsIgnoreCase(value)) {
                    String parent = runtimeState.adminSystemFeatureTogglePage.getParentToggle(toggle);
                    if ( ! runtimeState.adminSystemFeatureTogglePage.isToggleOnFeature(parent) ) {
                        // the sub features parent toggle must be set to ON
                        runtimeState.adminSystemFeatureTogglePage.toggleOnFeature(parent);
                    }
                    runtimeState.adminSystemFeatureTogglePage.toggleOnSubFeature(toggle);
                } else {
                    runtimeState.adminSystemFeatureTogglePage.toggleOffSubFeature(toggle);
                }
            }

        } else {
            throw new PendingException("System configured not to allow toggle changes, please re-run enabling setting of System Toggles");
        }
    }

    @When("^the \"([^\"]*)\" System Feature Toggle is set to \"([^\"]*)\"$")
    public void the_System_Feature_Toggle_is_set_to(String toggle, String value) throws Throwable {
        if (Boolean.valueOf(System.getProperty("setSystemToggles"))) {

            toggles.addToList("featureToggles", toggle);
            if ("ON".equalsIgnoreCase(value)) {
                runtimeState.adminSystemFeatureTogglePage.toggleOnFeature(toggle);
            } else {
                runtimeState.adminSystemFeatureTogglePage.toggleOffFeature(toggle);
            }

        } else {
            throw new PendingException("System configured not to allow toggle changes, please re-run enabling setting of System Toggles");
        }
    }

    @When("^the \"([^\"]*)\" System Sub Feature Toggle is set to \"([^\"]*)\"$")
    public void the_System_Sub_Feature_Toggle_is_set_to(String toggle, String value) throws Throwable {
        if (Boolean.valueOf(System.getProperty("setSystemToggles"))) {

            toggles.addToList("subFeatureToggles", toggle);
            if ("ON".equalsIgnoreCase(value)) {
                runtimeState.adminSystemFeatureTogglePage.toggleOnSubFeature(toggle);
            } else {
                runtimeState.adminSystemFeatureTogglePage.toggleOffSubFeature(toggle);
            }

        } else {
            throw new PendingException("System configured not to allow toggle changes, please re-run enabling setting of System Toggles");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the toggled features are stored as \"([^\"]*)\" in the database$")
    public void the_following_System_Features_are_stored_as_in_the_database(String value) throws Throwable {
        for (String toggle : toggles.getList("featureToggles")) {
            runtimeState.scenario.write("Asserting DB for " + toggle + " = " + value);
            if ("active".equalsIgnoreCase(value)) {
                assertTrue("Expected " + toggle + " to equal 1", dbHelperSystemToggles.getSystemFeatureToggle(toggle) == 1);
            } else {
                assertTrue("Expected " + toggle + " to equal 0", dbHelperSystemToggles.getSystemFeatureToggle(toggle) == 0);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the toggled sub features are stored as \"([^\"]*)\" in the database$")
    public void the_following_System_Sub_Features_are_stored_as_in_the_database(String value) throws Throwable {
        if (toggles.getList("subFeatureToggles") != null ) {
            for (String toggle : toggles.getList("subFeatureToggles")) {
                runtimeState.scenario.write("Asserting DB for " + toggle + " = " + value);
                if ("active".equalsIgnoreCase(value)) {
                    assertTrue("Expected " + toggle + " to equal 1", dbHelperSystemToggles.getSystemSubFeatureToggle(toggle) == 1);
                } else {
                    assertTrue("Expected " + toggle + " to equal 0", dbHelperSystemToggles.getSystemSubFeatureToggle(toggle) == 0);
                }
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the following System Features are stored as \"([^\"]*)\" in the database:$")
    public void the_following_System_Features_are_stored_as_in_the_database(String value, List<String> toggles) throws Throwable {
        for (String toggle : toggles) {
            runtimeState.scenario.write("Asserting DB for " + toggle + " = " + value);
            if ("active".equalsIgnoreCase(value)) {
                assertTrue("Expected " + toggle + " to equal 1", dbHelperSystemToggles.getSystemFeatureToggle(toggle) == 1);
            } else {
                assertTrue("Expected " + toggle + " to equal 0", dbHelperSystemToggles.getSystemFeatureToggle(toggle) == 0);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the following System Sub Features are stored as \"([^\"]*)\" in the database:$")
    public void the_following_Sub_System_Features_are_stored_as_in_the_database(String value, List<String> toggles) throws Throwable {
        for (String toggle : toggles) {
            runtimeState.scenario.write("Asserting DB for " + toggle + " = " + value);
            if ("active".equalsIgnoreCase(value)) {
                assertTrue("Expected " + toggle + " to equal 1", dbHelperSystemToggles.getSystemSubFeatureToggle(toggle) == 1);
            } else {
                assertTrue("Expected " + toggle + " to equal 0", dbHelperSystemToggles.getSystemSubFeatureToggle(toggle) == 0);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("the toggled features have their Sub Features automatically set to OFF")
    public void the_toggled_features_have_their_Sub_Features_automatically_set_to_OFF() throws Exception {
        for (String feature : toggles.getList("featureToggles")) {
            for (String subFeature : runtimeState.adminSystemFeatureTogglePage.getSubFeatures(feature)) {
                toggles.addToList("subFeatureToggles", subFeature);
                assertFalse("Expected " + subFeature + " to be toggled OFF", runtimeState.adminSystemFeatureTogglePage.isToggleOnSubFeature(subFeature));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" System Sub Feature Toggle is now set to \"([^\"]*)\"$")
    public void the_System_Sub_Feature_Toggle_is_now_set_to(String subFeature, String toggle) throws Throwable {
        if ("ON".equalsIgnoreCase(toggle)) {
            assertTrue("Expected " + subFeature + " to be toggled ON", runtimeState.adminSystemFeatureTogglePage.isToggleOnSubFeature(subFeature));
        } else {
            assertFalse("Expected " + subFeature + " to be toggled OFF", runtimeState.adminSystemFeatureTogglePage.isToggleOnSubFeature(subFeature));
        }
    }

    @When("^the toggles are set to the environment defaults$")
    public void the_toggles_are_set_to_the_environment_defaults() throws Throwable {
        toggleHelper.resetTogglesViaUI();
    }

    @ContinueNextStepsOnException
    @Then("^the default toggle values are stored in the database$")
    public void the_default_toggle_values_are_stored_in_the_database() throws Throwable {
        toggleHelper.assertDefaultToggles();
    }
}
