package mercury.steps.helpdesk.incidents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentSendEscalationSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperIncidents dbHelperIncidents;

    @When("^the message to de-escalate with no further follow-ups is displayed$")
    public void the_message_to_de_escalate_with_no_further_follow_ups_is_displayed() throws Throwable {
        assertEquals("Incident will de-escalate with no further follow-ups", runtimeState.helpdeskIncidentSendEscalation.getFollowUpText());
        outputHelper.takeScreenshots();
    }

    @And("^user clicks the called checkbox on incident send escalation page$")
    public void user_clicks_the_called_checkbox_on_incident_escalation_page() throws Throwable {
        runtimeState.helpdeskIncidentSendEscalation.clickCalledCheckbox();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Email text is displayed correctly on send escalation page$")
    public void the_email_text_is_displayed_correctly_on_incident_send_escalation_page() throws Throwable {
        assertEquals("Emails text is not displayed correctly", testData.getString("notes"),runtimeState.helpdeskIncidentSendEscalation.getInformationEmailText());
    }

    @ContinueNextStepsOnException
    @Then("^the text message is displayed correctly on send escalation page$")
    public void the_text_message_is_displayed_correctly_on_send_escalation_page() throws Throwable {

        Integer incidentReference = testData.getInt("incidentReference");
        String siteName = dbHelperIncidents.getSiteNameForIncidentReferenceNumber(incidentReference);
        String messageText = "Incident id: " + incidentReference + "-" + siteName + ": " + testData.getString("message");
        testData.put("messageText", messageText);
        assertEquals("Text Message is not displayed correctly", messageText,runtimeState.helpdeskIncidentSendEscalation.getInformationMessageText());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the next follow up time is displayed$")
    public void the_next_follow_up_time_is_prepulated() throws Throwable {
        assertTrue("The Next Follow up time is not displayed", runtimeState.helpdeskIncidentSendEscalation.isNextFollowUpTimeDisplayed());
        runtimeState.scenario.write("The pre populated next follow up date is: " + runtimeState.helpdeskIncidentSendEscalation.getNextFollowUpPrepopulatedTime());
    }

    @ContinueNextStepsOnException
    @Then("^user can change the next follow up time$")
    public void user_can_change_the_next_follow_up_time() throws Throwable {
        runtimeState.helpdeskIncidentSendEscalation.selectNextFollowUpDate();
        runtimeState.scenario.write("The next follow up date is: " +runtimeState.helpdeskIncidentSendEscalation.getNextFollowUpPrepopulatedTime());
    }

}
