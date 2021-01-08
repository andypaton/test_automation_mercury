package mercury.steps.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.helpers.POHelper;
import mercury.runtime.RuntimeState;


public class HelpdeskQuestionsTabSteps {

    @Autowired private RuntimeState runtimeState;

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" tab in the newly created job displays the \"([^\"]*)\" question along with user selected \"([^\"]*)\" answer$")
    public void questions_tab_in_the_newly_created_job_displays_the_question_along_with_user_selected_answer(String questionsTab, String question, String answer) throws Throwable {
        POHelper.refreshPage();
        runtimeState.helpdeskQuestionsTab = runtimeState.helpdeskJobPage.selectQuestionsTab();
        String actualQuestion=runtimeState.helpdeskQuestionsTab.getQuestion(question);
        String actualAnswer=runtimeState.helpdeskQuestionsTab.getAnswer(question);
        assertTrue("Job Question is not displayed in Questions tab !", actualQuestion.contains(question));
        assertTrue("Job Questions' Answer is displayed incorrectly in Questions tab! ", actualAnswer.contains(answer));
        runtimeState.scenario.write(" The job Question: " +"\'" +actualQuestion +"\'"+ " and user selected answer: "+"\'" +actualAnswer+"\'"+" is displayed in Questions tab.");
    }

}
