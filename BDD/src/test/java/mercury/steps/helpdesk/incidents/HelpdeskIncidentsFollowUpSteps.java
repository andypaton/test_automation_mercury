package mercury.steps.helpdesk.incidents;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentEscalation;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentsFollowUpSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;

    @And("^the follow up question \"([^\"]*)\" is answered with option \"([^\"]*)\"$")
    public void the_follow_up_question_is_answered_with_option(String question, String answer) throws Throwable {
        runtimeState.helpdeskIncidentFollowUpPage.selectQuestionAnswer(question,answer);
    }

    @And("^the update notes are entered on follow up page$")
    public void update_notes_are_entered_on_follow_up_page() throws Throwable {
        String notes = "Test notes entered on " + DateHelper.dateAsString(new Date());
        testData.put("notes", notes);
        runtimeState.helpdeskIncidentFollowUpPage.enterUpdateNotes(notes);
    }

    @When("^the follow up questions are answered:$")
    public void the_follow_up_questions_are_answered(DataTable questionAnswer) throws Throwable {
        for (Map<String, String> row : questionAnswer.asMaps(String.class, String.class)) {
            String question = row.get("question");
            String answer = row.get("answer");
            runtimeState.helpdeskIncidentFollowUpPage.selectQuestionAnswer(question,answer);
        }
    }

    public void answerFollowupQuestion(String action, String question) throws Exception {
        String answer;
        String questionTagName = runtimeState.helpdeskIncidentFollowUpPage.getIncidentQuestionTagName(question);
        switch(questionTagName){
        case "ph-incident-question-type-list-selection":
        case "ph-incident-question-type-yes-no":
            if (action.equalsIgnoreCase("No Further Follow up")) {
                runtimeState.helpdeskIncidentFollowUpPage.selectAnswer(question, "No");
                answer = "No";
            } else if (action.equalsIgnoreCase("Further Follow up")) {
                runtimeState.helpdeskIncidentFollowUpPage.selectAnswer(question, "Yes");
                answer = "Yes";
            } else {
                answer = runtimeState.helpdeskIncidentFollowUpPage.questionSelectRandomOption(question);
            }
            break;
        case "ph-incident-question-type-text-single-line":
            answer = DataGenerator.generateRandomSentence();
            runtimeState.helpdeskIncidentFollowUpPage.questionEnterText(question, answer);
            break;
        case "ph-incident-question-type-date":
            Date now = new Date();
            String dateTime = DateHelper.dateAsString(now, "M/d/yyyy h:mm a");
            runtimeState.helpdeskIncidentFollowUpPage.enterDateTime(question, dateTime);
            answer = DateHelper.dateAsString(now, "d MMM yyyy h:mma");
            break;
        default:
            throw new Exception ("Unexpected tag name (" + questionTagName +"). Please update code");
        }
        testData.addToMap("questions", question, answer);
        runtimeState.scenario.write("Question: " + question);
        runtimeState.scenario.write("Answer: " + answer);
    }

    @When("^all follow up questions are answered for \"([^\"]*)\"$")
    public void all_incident_questions_are_answered(String action) throws Throwable {
        List<String> questions = runtimeState.helpdeskIncidentFollowUpPage.getIncidentQuestions();
        for(int i=0; i<questions.size(); i++) {
            answerFollowupQuestion(action,questions.get(i));
            questions = runtimeState.helpdeskIncidentFollowUpPage.getIncidentQuestions();
        }
    }

    /**
     * This method is to send the escalation first and then answer the follow up
     * questions to meet the further follow up criteria
     */

    @And("^the user has updated incident for further follow up criteria$")
    public void the_user_has_updated_incident_for_further_follow_up_criteria() throws Throwable {

        // Enter a message on Escalation page and send escalation
        runtimeState.helpdeskIncidentEscalation = new HelpdeskIncidentEscalation(getWebDriver()).get();
        String message = "Test message entered on " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskIncidentEscalation.enterTextMessage(message);
        runtimeState.helpdeskIncidentSendEscalation = runtimeState.helpdeskIncidentEscalation.clickNextButton();
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskIncidentSendEscalation.clickSendEscalationsButton();

        // Answer the follow up questions to meet further follow up
        runtimeState.helpdeskIncidentFollowUpPage = runtimeState.helpdeskIncidentActionsDropdown.selectFollowUp();
        all_incident_questions_are_answered("Further follow up");
        update_notes_are_entered_on_follow_up_page();
        runtimeState.helpdeskIncidentEscalation = runtimeState.helpdeskIncidentFollowUpPage.clickUpdateFollowUpButton();
        runtimeState.helpdeskIncidentEscalation.enterTextMessage(message);
        runtimeState.helpdeskIncidentSendEscalation = runtimeState.helpdeskIncidentEscalation.clickNextButton();

        // Enter the next follow up date and send escalation
        runtimeState.helpdeskIncidentSendEscalation.selectNextFollowUpDate();
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskIncidentSendEscalation.clickSendEscalationsButton();
    }

    @And("^the next follow up time is entered$")
    public void the_next_follow_up_time_is_entered() throws Throwable {
        Date date = new Date();
        Date FollowUpTime = DateHelper.getDatePlusOffsetInHours(date, 24);
        String nextFollowUpTime = DateHelper.dateAsString(FollowUpTime, "MM/dd/yyyy hh:mm a");
        runtimeState.helpdeskIncidentSendEscalation.enterFollowUpTime(nextFollowUpTime);
    }
}
