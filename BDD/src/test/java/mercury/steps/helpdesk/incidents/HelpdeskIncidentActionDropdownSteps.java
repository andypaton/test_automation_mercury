package mercury.steps.helpdesk.incidents;

import static mercury.runtime.ThreadManager.getWebDriver;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentActionsDropdown;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentActionDropdownSteps {

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TzHelper tzHelper;


    @When("^\"([^\"]*)\" is clicked from the Actions dropdown$")
    public void the_option_is_clicked_from_the_actions_dropdown(String option) throws Throwable {

        runtimeState.helpdeskIncidentActionsDropdown = new HelpdeskIncidentActionsDropdown(getWebDriver());

        switch (option.toUpperCase()) {

        case "CANCEL INCIDENT":
            int siteId = dbHelperSites.getSiteId(runtimeState.helpdeskViewIncidentPage.getSiteValue());
            testData.put("storeTime", tzHelper.getCurrentTimeAtSite(siteId));
            runtimeState.helpdeskCancelIncident = runtimeState.helpdeskIncidentActionsDropdown.selectCancelIncident();
            runtimeState.scenario.write("Cancel Incident is clicked from the Actions dropdown");
            break;

        case "EDIT":
            runtimeState.helpdeskLogAnIncidentPage = runtimeState.helpdeskIncidentActionsDropdown.selectEditIncident();
            break;

        case "FOLLOW UP":
            runtimeState.helpdeskIncidentFollowUpPage = runtimeState.helpdeskIncidentActionsDropdown.selectFollowUp();
            runtimeState.scenario.write("Follow up link is clicked from the Actions dropdown");
            break;

        case "ESCALATE":
            runtimeState.helpdeskIncidentEscalation = runtimeState.helpdeskIncidentActionsDropdown.selectEscalate();
            runtimeState.scenario.write("Escalate link is clicked from the Actions dropdown");
            break;

        case "REVIEW":
            runtimeState.helpdeskLogAnIncidentPage = runtimeState.helpdeskIncidentActionsDropdown.reviewIncident();
            runtimeState.scenario.write("Review link is clicked from the Actions dropdown");
            break;

        case "ESCALATION CALLS":
            runtimeState.helpdeskIncidentSendEscalation = runtimeState.helpdeskIncidentActionsDropdown.selectEscalationCalls();
            runtimeState.scenario.write("Escalation Calls is selected from the Actions dropdown");
            break;

        default:
            throw new Exception("Unexpected option: " + option);
        }

    }

}
