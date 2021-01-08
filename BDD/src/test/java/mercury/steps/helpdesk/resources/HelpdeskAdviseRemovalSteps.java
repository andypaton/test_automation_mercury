package mercury.steps.helpdesk.resources;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import mercury.databuilders.DataGenerator;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskAdviseRemovalSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;



    @And("^Advise removal ((?:is|is not)) confirmed$")
    public void removal_confirmed_option_is_selected(String option) throws Throwable {

        if ("IS NOT".equalsIgnoreCase(option)) {
            runtimeState.helpdeskAdviseRemovalPanel.selectRemovalConfirmed("No");
            runtimeState.helpdeskAdviseRemovalPanel.selectRandomReason();
            runtimeState.scenario.write("'No' option is selected in Advise Removal" );
        } else {
            runtimeState.helpdeskAdviseRemovalPanel.selectRemovalConfirmed("Yes");
            runtimeState.scenario.write("'Yes' option is selected in Advise Removal" );
        }
        String randomNote = DataGenerator.GenerateRandomString(9, 10, 5, 3, 0, 0);
        runtimeState.helpdeskAdviseRemovalPanel.setNotes(randomNote);
        outputHelper.takeScreenshots();
    }

    @And("^the Advise removal form is saved$")
    public void the_button_is_clicked() throws Throwable {
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskAdviseRemovalPanel.save();
    }

}

