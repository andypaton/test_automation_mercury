package mercury.steps.helpdesk.incidents;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentAnswersSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;

    @ContinueNextStepsOnException
    @Then("^the Answers tab displays the question \"([^\"]*)\" along with answer \"([^\"]*)\"$")
    public void the_answer_to_is_displayed_as(String question, String answer) throws Throwable {
        runtimeState.helpdeskIncidentAnswersTab = runtimeState.helpdeskViewIncidentPage.selectAnswersTab();
        String actualAnswer = runtimeState.helpdeskIncidentAnswersTab.getAnswer(question);
        assertEquals("Expected " + answer + " but got " + actualAnswer, answer, actualAnswer);
    }

    @ContinueNextStepsOnException
    @Then("^the Answers tab displays the all incident questions asked with answers$")
    public void the_Answers_tab_displays_the_all_incident_questions_asked_with_answers() throws Throwable {

        runtimeState.helpdeskIncidentAnswersTab = runtimeState.helpdeskViewIncidentPage.selectAnswersTab();

        Map<String, Object> responses = testData.getMap("questions");
        if (responses != null) {
            for (String question : responses.keySet()) {
                String answer = (String) responses.get(question);
                runtimeState.scenario.write("Asserting: " + question + "\nis: " + answer);
                String actualAnswer = runtimeState.helpdeskIncidentAnswersTab.getAnswer(question);
                assertThat("Expected " + question + " : " + answer + " but got ", actualAnswer, is(equalToIgnoringCase(answer)));
            }
        }
    }
}
