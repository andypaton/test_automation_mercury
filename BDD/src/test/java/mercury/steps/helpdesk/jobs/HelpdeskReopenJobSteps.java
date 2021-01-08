package mercury.steps.helpdesk.jobs;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskReopenJobSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;


    @When("^all reopen details requested are entered$")
    public void all_reopen_details_requested_are_are_entered() throws Throwable {
        String reason = "Site advised works incomplete";
        runtimeState.reopenJobModal.selectReason(reason);
        String notes = DataGenerator.generateRandomSentence();
        runtimeState.reopenJobModal.enterNotes(notes);;

        testData.put("reason", reason);
        testData.put("notes", notes);

        if ("Engineer completed in error".equals(reason)) {
            String existingResource = runtimeState.reopenJobModal.selectRandomExistingResource();
            testData.put("existingresource", existingResource);
        }
    }

    @When("^reopen reason \"([^\"]*)\" and notes are entered$")
    public void the_reopen_reason_and_notes_are_entered(String reason) throws Throwable {
        runtimeState.reopenJobModal.selectReason(reason);
        String notes = DataGenerator.generateRandomSentence();
        runtimeState.reopenJobModal.enterNotes(notes);;

        testData.put("reason", reason);
        testData.put("notes", notes);
    }

    @When("^reopen reason \"([^\"]*)\", notes and existing resource are entered$")
    public void the_reopen_reason_notes_and_existing_resource_are_entered(String reason) throws Throwable {
        the_reopen_reason_and_notes_are_entered(reason);
        String existingResource = runtimeState.reopenJobModal.selectRandomExistingResource();
        testData.put("existingresource", existingResource);
    }

    @When("^Re-Open is selected$")
    public void reopen_is_selected() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.reopenJobModal.reopen();
    }

    @When("^Re-Open pop up is canceled$")
    public void reopen_pop_up_is_canceled() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.reopenJobModal.cancel();
    }
}
