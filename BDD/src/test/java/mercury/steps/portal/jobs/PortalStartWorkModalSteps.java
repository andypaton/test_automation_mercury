package mercury.steps.portal.jobs;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;
import mercury.steps.portal.PortalSteps;

public class PortalStartWorkModalSteps {

    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;
    @Autowired private PortalSteps portalSteps;
    @Autowired private RuntimeState runtimeState;


    @ContinueNextStepsOnException
    @Then("^start work message is displayed$")
    public void start_work_message_is_displayed() throws Throwable {
        portalSteps.the_asbestor_register_been_checked("has");
        
        String modalMessage = testData.getString("modalMessage");

        String expectedStartWorkMessage = "";
        switch (testData.getString("profileName")) {
        case "Contractor Technician":
            expectedStartWorkMessage = "You have started work on Job %d.\nYou must log back in before you leave site to stop work on this job.";
            break;

        default: throw new Exception("Unknown user profile: " + testData.getString("profileName"));
        }
        expectedStartWorkMessage = String.format(expectedStartWorkMessage, testData.getInt("jobReference"));

        runtimeState.scenario.write("Asserting message displayed: " + expectedStartWorkMessage);
        assertTrue("Message displayed: " + modalMessage + "\nExpected: " + expectedStartWorkMessage, modalMessage.contains(expectedStartWorkMessage));
        outputHelper.takeScreenshots();
    }

}
