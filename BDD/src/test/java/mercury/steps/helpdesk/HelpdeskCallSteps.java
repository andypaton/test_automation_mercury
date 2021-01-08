package mercury.steps.helpdesk;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskCallSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;

    @And("^the phone number to call is selected$")
    public void the_phone_number_to_call_is_selected() throws Throwable {
        String modalHeader = runtimeState.helpdeskCallJobContactModal.getModalHeader();

        String phoneNo = runtimeState.helpdeskCallJobContactModal.getCallContact();
        testData.addStringTag("phoneNo", phoneNo);

        if (modalHeader.contains("Call")) {
            runtimeState.helpdeskCallJobContactModal.clickCallRadioButton();
        } else {
            runtimeState.helpdeskCallJobContactModal.clickCallcontact();
        }
        runtimeState.scenario.write("Selected Phone number: " + phoneNo);
    }

    @And("^the call ((?:is|is not)) answered$")
    public void the_call_is_answered(String option) throws Throwable {

        if ("IS NOT".equalsIgnoreCase(option)) {
            runtimeState.helpdeskCallJobContactModal.clickCallAnsweredNo();
            enters_notes_in_the_call_resource_card();
        } else {
            runtimeState.helpdeskCallJobContactModal.clickCallAnsweredYes();
            enters_speaking_with_name();
        }

        outputHelper.takeScreenshots();
        runtimeState.helpdeskCallJobContactModal.saveContactDetails();
    }

    @And("^enters speaking with name$")
    public void enters_speaking_with_name() throws Throwable {
        String speakingWith = runtimeState.helpdeskCallJobContactModal.getSpeakingWith();
        if (speakingWith.isEmpty()) {
            speakingWith = runtimeState.helpdeskCallJobContactModal.getModalHeader().replaceAll("Call ", "");
            runtimeState.helpdeskCallJobContactModal.setSpeakingWith(speakingWith);
        } else {
            runtimeState.helpdeskCallJobContactModal.setSpeakingWith(speakingWith);
        }
        testData.addStringTag("speakingWith", speakingWith);
        runtimeState.scenario.write("Speaking with: " + speakingWith);
    }

    @And("^enters notes in the call resource card$")
    public void enters_notes_in_the_call_resource_card() throws Throwable {
        String notes = "Call not answered notes entered on: " + DateHelper.dateAsString(new Date());
        testData.addStringTag("notes", notes);
        runtimeState.helpdeskCallJobContactModal.setNotes(notes);
        runtimeState.scenario.write(notes);
    }

    @When("^the call ((?:is|is not)) answered by ((?:initial|additional)) resource$")
    public void the_call_is_not_answered_by_resource(String option, String resource) throws Throwable {
        if ("INITIAL".equalsIgnoreCase(resource)) {
            runtimeState.helpdeskCallJobContactModal = runtimeState.helpdeskManageResourcesPanel.clickCallResourceButton(testData.getString("resourceName"));
            the_phone_number_to_call_is_selected();
            testData.addStringTag("phoneNoInitial", testData.getString("phoneNo"));
            the_call_is_answered(option);
            testData.addStringTag("speakingWithInitial", testData.getString("speakingWith"));
            testData.addStringTag("notesInitial", testData.getString("notes"));
        } else if ("ADDITIONAL".equalsIgnoreCase(resource)) {
            runtimeState.helpdeskCallJobContactModal = runtimeState.helpdeskManageResourcesPanel.clickCallResourceButton(testData.getString("additionalResourceName"));
            the_phone_number_to_call_is_selected();
            testData.addStringTag("phoneNoAdditional", testData.getString("phoneNo"));
            the_call_is_answered(option);
            testData.addStringTag("notesAdditional", testData.getString("notes"));
            testData.addStringTag("speakingWithAdditional", testData.getString("speakingWith"));
        }
        POHelper.refreshPage();
    }

}