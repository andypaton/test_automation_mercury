package mercury.steps.helpdesk.resources;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.runtime.RuntimeState;

public class HelpdeskTransferWorkSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperResources dbHelperResources;


    @And("^the new \"([^\"]*)\" resource to transfer work to is selected$")
    public void the_new_resource_to_transfer_work_to_is_selected(String resourceProfile) throws Throwable {
        int resourceId = dbHelperResources.getResourceId(testData.getString("oldResource"));
        int additionalResourceId = dbHelperResources.getRandomActiveBackupResourceId(resourceProfile, resourceId);
        String additionalResource = dbHelperResources.getResourceName(additionalResourceId);
        runtimeState.helpdeskTransferWorkPanel.searchAndSelectAdditionalResource(additionalResource);
        testData.put("newResource", additionalResource);
    }

    @And("^the reason for work transfer is selected$")
    public void the_reason_for_work_transfer_is_selected() throws Throwable {
        runtimeState.helpdeskTransferWorkPanel.selectReason();
        String reason = runtimeState.helpdeskTransferWorkPanel.getSelectedReason();
        testData.put("reason", reason);
    }

    @And("^the work transfer notes are entered$")
    public void the_work_transfer_notes_are_entered() throws Throwable {
        String notes = DataGenerator.generateRandomSentence();
        runtimeState.helpdeskTransferWorkPanel.enterNotes(notes);
        testData.put("notes", notes);
    }

    @And("^the transfer work is saved$")
    public void the_transfer_work_is_saved() throws Throwable {
        runtimeState.helpdeskTransferWorkPanel.clickSaveButton();
    }

    @And("^the work is transferred to another \"([^\"]*)\" resource$")
    public void the_work_is_transferred_to_another_resource(String resourceProfile) throws Throwable {
        the_new_resource_to_transfer_work_to_is_selected(resourceProfile);
        the_reason_for_work_transfer_is_selected();
        the_work_transfer_notes_are_entered();
        outputHelper.takeScreenshots();
        the_transfer_work_is_saved();
    }

}


