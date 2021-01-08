package mercury.steps.helpdesk.incidents;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentUpdateSteps {

	@Autowired private RuntimeState runtimeState;
	@Autowired private OutputHelper outputHelper;
	@Autowired private TestData testData;

	@When("^the incident update page is displayed$")
	public void the_incident_update_page_is_displayed() throws Throwable {
		assertEquals("Incident update page is not displayed", "Incident Update", runtimeState.helpdeskIncidentUpdate.getHeader());
		outputHelper.takeScreenshots();
	}

	@When("^the notes are entered$")
	public void the_notes_are_entered() throws Throwable {
		String notes = "Test notes entered on " + DateHelper.dateAsString(new Date());
		runtimeState.helpdeskIncidentUpdate.addnotes(notes);
		testData.put("notes", notes);
	}
}
