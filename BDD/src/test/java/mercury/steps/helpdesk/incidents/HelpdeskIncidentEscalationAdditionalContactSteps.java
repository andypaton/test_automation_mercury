package mercury.steps.helpdesk.incidents;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentEscalationAdditionalContact;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentEscalationAdditionalContactSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;


    @ContinueNextStepsOnException
    @Then("^an additional user is added on Escalation page$")
    public void an_additional_user_is_added_on_Escalation_page(String contactPreferenceMethod) throws Throwable {
        runtimeState.helpdeskIncidentEscalation.clickContactTab("Additional");
        runtimeState.helpdeskIncidentEscalationAdditionalContact = new HelpdeskIncidentEscalationAdditionalContact(getWebDriver()).get();
        String additonalContactName = DataGenerator.generateRandomWord();
        runtimeState.helpdeskIncidentEscalationAdditionalContact.enterName(additonalContactName);
        testData.addStringTag("addionalContactName", additonalContactName);
        String additonalContactTelephone = DataGenerator.generatePhoneNumber();
        runtimeState.helpdeskIncidentEscalationAdditionalContact.enterTelephone(additonalContactTelephone);
        testData.addStringTag("addionalContactTelephone", additonalContactTelephone);

        switch (contactPreferenceMethod) {

        case "Call":
            runtimeState.helpdeskIncidentEscalationAdditionalContact.clickContactByRadioButton("Call");
            break;

        case "Email":
            runtimeState.helpdeskIncidentEscalationAdditionalContact.clickContactByRadioButton("Email");
            break;

        case "Text":
            runtimeState.helpdeskIncidentEscalationAdditionalContact.clickContactByRadioButton("SMS");
            break;

        default:
            throw new Exception("Unexpected preference method " + contactPreferenceMethod);
        }
        runtimeState.helpdeskIncidentEscalationAdditionalContact.clickAddButton();
        outputHelper.takeScreenshots();
    }

    @When("^user adds an additonal contact with contact preference$")
    public void user_adds_an_additonal_contact_with_contact_preference() throws Throwable {
        String[] preferences = { "Call", "Email", "Text" };
        Random random = new Random();
        String contactPreferenceMethod = preferences[random.nextInt(preferences.length)];
        testData.addStringTag("contactPreferenceMethod", contactPreferenceMethod);
        an_additional_user_is_added_on_Escalation_page(contactPreferenceMethod);
    }

    @ContinueNextStepsOnException
    @Then("^the added contact is displayed in additional tab$")
    public void the_added_contact_is_displayed_in_additional_tab() throws Throwable {
        Grid grid = runtimeState.helpdeskIncidentEscalationAdditionalContact.getGrid();
        List<Row> rows = grid.getRows();

        String expectedContact = testData.getString("addionalContactName").trim();
        String expectedTelephone = testData.getString("addionalContactTelephone").trim();

        boolean foundName = false;
        boolean foundTelephone = false;

        for (Row row : rows) {

            if (row.getCell("Name").getSubText().contains(expectedContact)) {
                foundName = true;
            }
            if (row.getCell("Name").getText().contains(expectedTelephone)) {
                foundTelephone = true;
            }
        }
        assertTrue("Additional contact name is not displayed: " + expectedContact, foundName);
        assertTrue("Additional Contact telephone is not displayed: " + expectedTelephone, foundTelephone);
    }
}