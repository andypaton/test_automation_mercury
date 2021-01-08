package mercury.steps.helpdesk.jobs;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;


public class HelpdeskDeclineJobSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;

    @When("^user declines job on behalf of ((?:initial resource|additional resource|resource))$")
    public void user_declines_job_on_behalf_of_resource(String resource) throws Throwable {
        if (runtimeState.helpdeskManageResourcesPanel == null) {
            runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
        }

        if ("ADDITIONAL RESOURCE".equalsIgnoreCase(resource)) {

            runtimeState.helpdeskDeclineJobPanel = runtimeState.helpdeskManageResourcesPanel.additionalResourceSelectDeclineJobAction(testData.getString("additionalResourceName"));
            the_reason_for_declining_the_job_reason_is_selected();
            the_notes_for_declining_the_job_are_entered();
            the_save_button_in_the_decline_job_panel_is_clicked();
            testData.put("reasonAdditional", testData.getString("reason"));
            testData.put("notesAdditional", testData.getString("notes"));
        } else {
            runtimeState.helpdeskDeclineJobPanel = runtimeState.helpdeskManageResourcesPanel.selectDeclineJobAction();
            the_reason_for_declining_the_job_reason_is_selected();
            the_notes_for_declining_the_job_are_entered();
            the_save_button_in_the_decline_job_panel_is_clicked();
            testData.put("reasonInitial", testData.getString("reason"));
            testData.put("notesInitial", testData.getString("notes"));

        }
        runtimeState.scenario.write("Declining the job with Reason: " + testData.getString("reason") + ", Notes: " + testData.getString("notes"));
        outputHelper.takeScreenshots();
    }

    @And("^the reason for declining the job reason is selected$")
    public void the_reason_for_declining_the_job_reason_is_selected() throws Throwable {
        runtimeState.helpdeskDeclineJobPanel.selectRandomReason();
        String reason = runtimeState.helpdeskDeclineJobPanel.getReason();
        testData.addStringTag("reason", reason);

    }

    @And("^the notes for declining the job are entered$")
    public void the_notes_for_declining_the_job_are_entered() throws Throwable {
        String notes = "Test Notes entered on " + DateHelper.dateAsString(new Date());
        testData.addStringTag("notes", notes);
        runtimeState.helpdeskDeclineJobPanel.setNotes(notes);
    }

    @And("^the save button in the decline job panel is clicked $")
    public void the_save_button_in_the_decline_job_panel_is_clicked() throws Throwable {
        runtimeState.helpdeskAdditionalResourcesRequiredPanel = runtimeState.helpdeskDeclineJobPanel.save();
    }

}

