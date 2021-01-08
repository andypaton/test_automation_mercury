package mercury.steps.helpdesk.incidents;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentEscalationContactEmailSmsTab;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentEscalationContactEmailSmsTabSteps {

    @Autowired
    private RuntimeState runtimeState;
    @Autowired
    private OutputHelper outputHelper;
    @Autowired
    private TestData testData;
    @Autowired
    private DbHelperIncidents dbHelperIncidents;


    @When("^the communication method for an escalation contact is changed to \"([^\"]*)\"$")
    public void the_communication_method_for_an_escalation_contact_is_changed(String communicationMethod) throws Throwable {
        String escalationContactName = dbHelperIncidents.getEscalationContactName(testData.getInt("incidentReference"));
        testData.addStringTag("escalationContactName", escalationContactName);
        runtimeState.scenario.write("Escalation Contact Name: " + escalationContactName);
        runtimeState.helpdeskIncidentEscalationContactEmailSmsTab = new HelpdeskIncidentEscalationContactEmailSmsTab(getWebDriver()).get();
        runtimeState.helpdeskIncidentEscalationContactEmailSmsTab.clickRadioButton(escalationContactName, communicationMethod);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the communication method for an escalation contact is updated$")
    public void the_communication_method_for_an_escalation_contact_is_updated() throws Throwable {
        int emailNotificationStatus = dbHelperIncidents.getEmailNotificationStatus(testData.getString("escalationContactName"), testData.getInt("incidentReference"));
        assertEquals("Looks like the Communication method is not updated", 0, emailNotificationStatus);
    }

    @And("^system displays the escalation contacts$")
    public void system_displays_an_escalation_contacts() throws Throwable {

        runtimeState.helpdeskIncidentEscalationContactEmailSmsTab = new HelpdeskIncidentEscalationContactEmailSmsTab(getWebDriver()).get();
        // Assert the page icons
        assertTrue("The Escalation Contact Header is not displayed", runtimeState.helpdeskIncidentEscalationContactEmailSmsTab.IsEscalationContactHeaderDisplayed());
        assertTrue("The Call Icon is not displayed", runtimeState.helpdeskIncidentEscalationContactEmailSmsTab.IsIconDisplayed("Call"));
        assertTrue("The Email Icon is not displayed", runtimeState.helpdeskIncidentEscalationContactEmailSmsTab.IsIconDisplayed("Email"));
        assertTrue("The Text Message Icon is not displayed", runtimeState.helpdeskIncidentEscalationContactEmailSmsTab.IsIconDisplayed("Text Message"));
        assertTrue("The Skip Icon is not displayed", runtimeState.helpdeskIncidentEscalationContactEmailSmsTab.IsIconDisplayed("Skip"));

        // Assert the escalation contacts
        List<String> dbEscalationContacts = dbHelperIncidents.getEscalationContactNames(testData.getInt("incidentReference"));
        List<String> escalationContacts = runtimeState.helpdeskIncidentEscalationContactEmailSmsTab.getEscalationContacts();

        for (int i = 0; i < escalationContacts.size(); i++) {
            runtimeState.scenario.write("Asserting contact is: " + escalationContacts.get(i).toString());
            assertTrue("Looks like an escalation contacts are not matched ", dbEscalationContacts.toString().trim().contains(escalationContacts.get(i).toString().trim()));
        }
        outputHelper.takeScreenshots();
    }
}
