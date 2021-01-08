package mercury.steps.helpdesk.resources;

import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskFundingRequestsPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAcknowledgeETAPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAdditionalResourcePanel;
import mercury.runtime.RuntimeState;

public class HelpdeskAcknowledgeEtaSteps {
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;

    @When("^the ETA is acknowledged$")
    public void the_eta_is_acknowledged() throws Throwable {
        String acknowledgeETANotes = "Notes entered by Test Automation " + DateHelper.dateAsString(new Date());
        String acknowledgedBy = "Acknowledged by Test Automation ";


        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();

        if(runtimeState.helpdeskManageResourcesPanel.getLastResourceStatus().contains(localize("Awaiting Funding Authorisation"))) {
            runtimeState.helpdeskAdditionalResourcePanel = new HelpdeskAdditionalResourcePanel(getWebDriver()).get();
            runtimeState.helpdeskAdditionalResourcePanel.selectFundingRequestAction();
            runtimeState.helpdeskFundingRequestsPanel = new HelpdeskFundingRequestsPanel(getWebDriver()).get();
            runtimeState.helpdeskFundingRequestsPanel.authorize();
            runtimeState.helpdeskFundingRequestsPanel.selectRandomFunding();
            runtimeState.helpdeskFundingRequestsPanel.enterAuthoriseNotes(DataGenerator.GenerateRandomString(7, 10, 3, 3, 0, 0));
            runtimeState.helpdeskFundingRequestsPanel.save();
        }

        String resourceName = runtimeState.helpdeskManageResourcesPanel.getResourceNameWithActions();

        runtimeState.helpdeskManageResourcesPanel.selectAcknowledgeEtaAction(resourceName);
        runtimeState.helpdeskAcknowledgeETAPanel = new HelpdeskAcknowledgeETAPanel(getWebDriver()).get();

        runtimeState.helpdeskAcknowledgeETAPanel.setAcknowledgedBy(acknowledgedBy);
        runtimeState.helpdeskAcknowledgeETAPanel.setNotes(acknowledgeETANotes);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskAcknowledgeETAPanel.save();
        outputHelper.takeScreenshots();
    }
}
