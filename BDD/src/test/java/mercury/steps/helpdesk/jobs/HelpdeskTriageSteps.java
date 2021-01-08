package mercury.steps.helpdesk.jobs;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobCancelPanel;
import mercury.runtime.RuntimeState;

public class HelpdeskTriageSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;


    @ContinueNextStepsOnException
    @Then("^hours, minutes, notes.* outcome reason \"([^\"]*)\" .*are saved$")
    public void hours_minutes_notes_outcome_saved(String outcome) throws IOException {

        String hour = runtimeState.helpdeskTriagePanel.selectRandomHour();
        String minute = runtimeState.helpdeskTriagePanel.selectRandomMinute();
        testData.put("timeSpent", hour + " hours " + minute + " minutes");
        String notes = "Triage job picked up at " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskTriagePanel.addNotes(notes);
        testData.put("notes", notes);
        runtimeState.helpdeskTriagePanel.selectOutcome(outcome);

        outputHelper.takeScreenshots();

        if ("Complete Job".equals(outcome)) {
            String rootCauseCategory = runtimeState.helpdeskTriagePanel.selectRandomRootCauseCategory();
            String rootCause = runtimeState.helpdeskTriagePanel.selectRandomRootCause();
            testData.put("reason", rootCauseCategory + " > " + rootCause);
        }

        runtimeState.helpdeskTriagePanel.save();

        if ("Cancel Job".equals(outcome)) {
            runtimeState.helpdeskJobCancelPanel = new HelpdeskJobCancelPanel(getWebDriver()).get();
        }
    }

}
