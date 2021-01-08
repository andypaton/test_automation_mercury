package mercury.steps.helpdesk.resources;

import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;


public class HelpdeskRemoveSuggestedResourceSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;

    @And("^removes suggested resource field$")
    public void removes_suggested_resource_field() throws Throwable {
        runtimeState.scenario.write("Removing Suggested resource by clicking on X Icon");
        outputHelper.takeScreenshots();
        runtimeState.helpdeskAdditionalResourcesNotRequiredPanel = runtimeState.helpdeskAdditionalResourcesRequiredPanel.closeAdditionalResource();
    }

    @ContinueNextStepsOnException
    @Then("^a warning \"([^\"]*)\" is displayed$")
    public void a_warning_is_displayed(String expectedAlertText) throws Throwable {
        String actualAlertText = runtimeState.helpdeskAdditionalResourcesNotRequiredPanel.getAlertText();
        assertTrue("'This will mark the resource as no longer required' warning is not displayed!", actualAlertText.contains(expectedAlertText));
        runtimeState.scenario.write("The warning: " + expectedAlertText + " is displayed.");
        outputHelper.takeScreenshots();
    }
}

