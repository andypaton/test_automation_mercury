package mercury.steps.helpdesk.resources;

import org.springframework.beans.factory.annotation.Autowired;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskAdviseEtaSteps {
	@Autowired private RuntimeState runtimeState;	
	@Autowired private OutputHelper outputHelper;

	@And("^the ETA panel is displayed$")
	public void the_eta_panel_is_displayed() throws Throwable {
		runtimeState.helpdeskAdviseEtaPanel = runtimeState.helpdeskJobPage.selectAdviseEtaAction();
		outputHelper.takeScreenshots();
	}

	@When("^the details are saved$")
	public void the_details_saved() throws Throwable {
	    outputHelper.takeScreenshots();
		runtimeState.helpdeskAdviseEtaPanel.save();
	}

}
