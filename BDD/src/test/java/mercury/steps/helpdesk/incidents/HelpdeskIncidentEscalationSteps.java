package mercury.steps.helpdesk.incidents;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.database.dao.EscalationContactsDao;
import mercury.database.models.EscalationContacts;
import mercury.databuilders.CallerContact;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentEscalation;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentEscalationAdditionalContact;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentEscalationSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private CallerContact callerContact;
    @Autowired private TestData testData;
    @Autowired private EscalationContactsDao escalationContactsDao;

    @ContinueNextStepsOnException
    @Then("^the De-Escalation page is displayed$")
    public void the_De_Escalation_page_is_displayed() throws Throwable {
        assertEquals(" De-escalation for incident: "+ testData.getString("incidentReference")+"", runtimeState.helpdeskIncidentEscalation.getIncidentHeader());
    }

    @ContinueNextStepsOnException
    @Then("^the escalation email address is displayed$")
    public void the_escalation_email_address_is_displayed() throws Throwable {

        EscalationContacts escalationContacts = escalationContactsDao.getByIncidentReferenceNumber(testData.getInt("incidentReference"));
        String expectedEmail = escalationContacts.getRecipientEmailAddress();
        String getName = escalationContacts.getName();
        String Name[] = getName.split(" ");
        String name = Name[0];
        String actualEmail = runtimeState.helpdeskIncidentEscalation.getEscalationEmail(name);
        runtimeState.scenario.write("Asserting Escalation Email address for " +getName + " is: " + expectedEmail );
        assertEquals("Escalation Email address "+ expectedEmail +" is not displayed in the Email/SMS tab",expectedEmail,actualEmail);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^user can add additional user on Escalation page$")
    public void additional_user_is_added_on_Escalation_page() throws Throwable {
        runtimeState.helpdeskIncidentEscalation.clickContactTab("Additional");
        runtimeState.helpdeskIncidentEscalationAdditionalContact = new HelpdeskIncidentEscalationAdditionalContact(getWebDriver()).get();
        callerContact.copy(new CallerContact.Builder().build());
        callerContact.setCallerType("Client");
        runtimeState.helpdeskIncidentEscalationAdditionalContact.enterName(callerContact.getName());
        runtimeState.helpdeskIncidentEscalationAdditionalContact.enterTelephone(callerContact.getTelephone());
        runtimeState.helpdeskIncidentEscalationAdditionalContact.clickContactByRadioButton("Call");
        runtimeState.helpdeskIncidentEscalationAdditionalContact.clickAddButton();
        outputHelper.takeScreenshots();
    }

    @And("^the message is entered on incident escalation page$")
    public void the_message_is_entered_on_escalation_screen() throws Throwable {
        runtimeState.helpdeskIncidentEscalation = new HelpdeskIncidentEscalation(getWebDriver()).get();
        String message = "Test message entered on " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskIncidentEscalation.enterTextMessage(message);
        testData.addStringTag("message", message);
    }

    @ContinueNextStepsOnException
    @Then("^the Emails text is displayed correctly on incident escalation page$")
    public void the_Emails_text_is_displayed_correctly_on_incident_escalation_page() throws Throwable {
        assertEquals("Emails text is not displayed correctly on incident escalation page",testData.getString("notes"), runtimeState.helpdeskIncidentEscalation.getPreviouslySentEmailsText());
        assertEquals("Emails text is not displayed correctly on incident escalation page",testData.getString("notes"), runtimeState.helpdeskIncidentEscalation.getWhatWillBeSentEmailsText());
        runtimeState.scenario.write("The Emails text is: "+runtimeState.helpdeskIncidentEscalation.getWhatWillBeSentEmailsText());
    }

    @ContinueNextStepsOnException
    @Then("^the user has \"([^\"]*)\" Incident type$")
    public void the_user_has_incident_type(String incidentType) throws Throwable {
        testData.addStringTag("incidentType", incidentType);
    }
}
