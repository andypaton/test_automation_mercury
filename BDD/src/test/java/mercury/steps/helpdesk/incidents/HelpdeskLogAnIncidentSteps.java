package mercury.steps.helpdesk.incidents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static mercury.helpers.Globalisation.SHORT2;
import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.helpers.Globalisation.MEDIUM;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.CallerContact;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.pageobject.web.helpdesk.HelpdeskHomePage;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskViewIncidentPage;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.helpdesk.search.HelpdeskSearchForSiteSteps;

public class HelpdeskLogAnIncidentSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private CallerContact callerContact;
    @Autowired AssertionFactory assertionFactory;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private CommonSteps commonSteps;
    @Autowired private HelpdeskViewIncidentSteps helpdeskViewIncidentSteps;
    @Autowired private HelpdeskIncidentTimelineSteps helpdeskIncidentTimelineSteps;
    @Autowired private HelpdeskIncidentAnswersSteps helpdeskIncidentAnswersSteps;
    @Autowired private HelpdeskSearchForSiteSteps helpdeskSearchForSiteSteps;
    

    @And("^the Incident description is entered$")
    public void incident_description_entered() throws Throwable {
        String description = "Test description entered on " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskLogAnIncidentPage.addIncidentDetails(description);
        testData.addStringTag("description", description);
    }

    @When("^an existing Caller is entered on incident page$")
    public void an_existing_Caller_is_entered() throws Throwable {
        String selectedCaller;
        selectedCaller = runtimeState.helpdeskLogAnIncidentPage.selectCaller();
        if (selectedCaller == null) {
            selectedCaller = runtimeState.helpdeskLogAnIncidentPage.selectCaller();
        }
        String name[] = selectedCaller.split(" ");
        testData.addStringTag("caller", name[0]);
    }

    @When("^an existing caller is entered$")
    public void an_existing_caller_is_entered() throws Throwable {
        String caller;
        String callerName;
        String departmentName = callerContact.getDepartment();
        if(departmentName.isEmpty() || departmentName == null) {
            caller = callerContact.getName();
            callerName = caller.contains("'") ? caller.split("'")[0].trim() : caller;
        } else {
            caller = callerContact.getName() + " (" + callerContact.getDepartment() + ")";
            callerName = caller.contains("'") ? caller.split("'")[0].trim() : caller;
        }
        runtimeState.scenario.write("Caller Name is: " + caller);
        runtimeState.helpdeskLogAnIncidentPage.selectCaller(callerName);
    }

    @ContinueNextStepsOnException
    @Then("^the phone number, extension and caller type are auto completed$")
    public void caller_details_are_auto_completed() {
        String expectedExtension = callerContact.getExtension() == null ? "" : callerContact.getExtension();
        assertEquals("Unexpected Phone Number found", callerContact.getTelephone(), runtimeState.helpdeskLogAnIncidentPage.getPhoneNo());
        assertEquals("Unexpected Extension found", expectedExtension, runtimeState.helpdeskLogAnIncidentPage.getExtension());
        assertEquals("Unexpected Caller Type found", callerContact.getCallerType(), runtimeState.helpdeskLogAnIncidentPage.getCallerType());
    }

    @ContinueNextStepsOnException
    @Then("^Date and Time of an incident is entered$")
    public void date_and_time_of_an_incident_is_entered() throws Throwable {
        runtimeState.helpdeskLogAnIncidentPage.selectIncidentClock();
    }

    @And("^the Core details are entered$")
    public void core_details_are_entered() throws Throwable {
        an_existing_Caller_is_entered();
        date_and_time_of_an_incident_is_entered();
    }

    @And("^the site questions are answered when the site is closed$")
    public void the_site_questions_are_answered_when_the_site_is_closed() throws Throwable  {
        the_site_closed_radio_button_and_site_closed_date_is_selected();
        the_site_reopened_no_radio_button_and_site_reopened_date_is_selected();
    }

    @And("^the site closed yes radio button and site closed date is selected$")
    public void the_site_closed_radio_button_and_site_closed_date_is_selected() throws Throwable  {
        runtimeState.helpdeskLogAnIncidentPage.clickSiteClosedYesRadio();
        testData.addStringTag("siteClosedValue", "Yes");
        runtimeState.helpdeskLogAnIncidentPage.selectSiteClosedDate();
    }

    @And("^the site reopened no radio button and site likely to reopen date is selected$")
    public void the_site_reopened_no_radio_button_and_site_reopened_date_is_selected() throws Throwable  {
        runtimeState.helpdeskLogAnIncidentPage.clickSiteReopenedNoRadio();
        testData.addStringTag("siteClosedValue", "No");
        runtimeState.helpdeskLogAnIncidentPage.selectSiteLikelyToReopenDate();
    }

    @And("^the site questions are answered when the site is closed but has re-opened$")
    public void the_site_questions_are_answered_when_the_site_is_closed_but_has_reopened() throws Throwable  {
        the_site_closed_radio_button_and_site_closed_date_is_selected();
        the_site_reopened_yes_radio_button_and_site_reopened_date_is_selected();
        runtimeState.helpdeskLogAnIncidentPage.clickDepartmentClosedNoRadio();
    }

    @And("^the site reopened yes radio button and site reopen date is selected$")
    public void the_site_reopened_yes_radio_button_and_site_reopened_date_is_selected() throws Throwable  {
        runtimeState.helpdeskLogAnIncidentPage.clickSiteReopenedYesRadio();
        testData.addStringTag("siteReopenedValue", "Yes");
        runtimeState.helpdeskLogAnIncidentPage.selectSiteReopenedDate();
    }

    @And("^the site questions are answered when the department is closed$")
    public void the_site_questions_are_answered_with_department_closed() throws Throwable  {
        the_site_closed_radio_button_and_site_closed_date_is_selected();
        the_site_reopened_yes_radio_button_and_site_reopened_date_is_selected();
        the_department_closed_radio_button_and_department_closed_date_is_selected();
        runtimeState.helpdeskLogAnIncidentPage.clickDepartmentReopenedNoRadio();
    }

    @And("^the site questions are answered when the department is closed but has reopened$")
    public void the_site_questions_are_answered_when_department_is_closed_but_has_reopened() throws Throwable  {
        the_site_closed_radio_button_and_site_closed_date_is_selected();
        the_site_reopened_yes_radio_button_and_site_reopened_date_is_selected();
        the_department_closed_radio_button_and_department_closed_date_is_selected();
        the_department_reopened_radio_button_and_department_reopened_date_is_selected();
    }

    @And("^the site questions are answered when nothing is closed$")
    public void the_site_questions_are_answered_when_nothing_is_closed() throws Throwable  {
        runtimeState.helpdeskLogAnIncidentPage.clickSiteClosedNoRadio();
        runtimeState.helpdeskLogAnIncidentPage.clickDepartmentClosedNoRadio();
    }

    @And("^the department closed yes radio button and department closed date is selected$")
    public void the_department_closed_radio_button_and_department_closed_date_is_selected() throws Throwable  {
        runtimeState.helpdeskLogAnIncidentPage.clickDepartmentClosedYesRadio();
        testData.addStringTag("departmentClosedValue", "Yes");
        runtimeState.helpdeskLogAnIncidentPage.selectDepartmentClosedDate();
        testData.addStringTag("departmentClosedDate", runtimeState.helpdeskLogAnIncidentPage.getDepartmentClosedDateValue());
    }

    @And("^the department reopened yes radio button and department reopened date is selected$")
    public void the_department_reopened_radio_button_and_department_reopened_date_is_selected() throws Throwable  {
        runtimeState.helpdeskLogAnIncidentPage.clickDepartmentReopenedYesRadio();
        testData.addStringTag("departmentReopenedValue", "Yes");
        runtimeState.helpdeskLogAnIncidentPage.selectDepartmentReopenedDate();
    }

    @And("^the Incident Type \"([^\"]*)\" is selected$")
    public void the_Incident_Type_is_selected(String incidentType) throws Throwable {

        if (dbHelperIncidents.isIncidentTypeActive(incidentType.toLowerCase())) {
            testData.put("incidentType", incidentType);
            runtimeState.scenario.write("test data selected: incident Type = " + incidentType);
            runtimeState.helpdeskLogAnIncidentPage.clickIncidentType(incidentType.toLowerCase());
        } else {
            throw new Error("Incident type " + incidentType + " is not active for logging");
        }
    }

    @When("^the question \"([^\"]*)\" is answered with option \"([^\"]*)\"$")
    public void the_question_is_answered_with_option(String question, String answer) throws Throwable {
        runtimeState.helpdeskLogAnIncidentPage.selectQuestionAnswer(question,answer);
    }

    @When("^all incident questions are answered$")
    public void all_incident_questions_are_answered() throws Throwable {
        List<String> questions = runtimeState.helpdeskLogAnIncidentPage.getIncidentQuestions();
        int index = 0;
        while (questions.size() > index) {
            answerIncidentQuestion(questions.get(index), null);
            index++;
            questions = runtimeState.helpdeskLogAnIncidentPage.getIncidentQuestions();
        }
    }

    public void answerIncidentQuestion(String question, String escalationCriteria) throws Exception {
        String answer;
        String questionTagName = runtimeState.helpdeskLogAnIncidentPage.getIncidentQuestionTagName(question);
        switch(questionTagName){
        case "ph-incident-question-type-list-selection":
            answer = runtimeState.helpdeskLogAnIncidentPage.questionSelectRandomOption(question);
            break;

        case "ph-incident-question-type-yes-no":
            if (escalationCriteria != null) {
                answer = escalationCriteria;
                runtimeState.helpdeskLogAnIncidentPage.questionSelectOption(question, escalationCriteria);
            } else {
                answer = runtimeState.helpdeskLogAnIncidentPage.questionSelectRandomOption(question);
            }
            break;
        case "ph-incident-question-type-text-single-line":
            answer = DataGenerator.generateRandomSentence();
            runtimeState.helpdeskLogAnIncidentPage.questionEnterText(question, answer);
            break;
        case "ph-incident-question-type-date-time":
            Date now = new Date();
            String dateTime = DateHelper.dateAsString(now, SHORT2);
            runtimeState.helpdeskLogAnIncidentPage.enterDateTime(question, dateTime);
            answer = DateHelper.dateAsString(now, MEDIUM);
            break;
        default:
            throw new Exception ("Unexpected tag name (" + questionTagName +"). Please update code");
        }
        testData.addToMap("questions", question, answer);
        runtimeState.scenario.write("Question: " + question);
        runtimeState.scenario.write("Answer: " + answer);
    }

    @When("^all incident questions are answered with escalation criteria option$")
    public void all_incident_questions_are_answered_with_escalation_criteria_option() throws Throwable {
        List<String> questions = runtimeState.helpdeskLogAnIncidentPage.getIncidentQuestions();
        int index = 0;
        while (questions.size() > index) {
            String escalationCritera = testData.getString("escalationCriteria");
            answerIncidentQuestion(questions.get(index), escalationCritera);
            index++;
            questions = runtimeState.helpdeskLogAnIncidentPage.getIncidentQuestions();
        }
    }

    @And("^the Stock loss details \"([^\"]*)\" are entered$")
    public void Stock_Loss_details_are_entered(String details) throws Throwable {
        runtimeState.helpdeskLogAnIncidentPage.addStockLossDetails(details);
        runtimeState.scenario.write("Stock loss details: " + runtimeState.helpdeskLogAnIncidentPage.getStockLossDetails());
    }

    @And("^the Incident description is updated as \"([^\"]*)\"$")
    public void incident_description_updated_as(String description) throws Throwable {
        runtimeState.helpdeskLogAnIncidentPage.addIncidentDetails(description);
        testData.addStringTag("description", description);
    }

    @And("^the core details and site questions are updated$")
    public void the_core_details_and_site_questions_are_updated() throws Throwable {
        an_existing_Caller_is_entered();
        runtimeState.helpdeskLogAnIncidentPage.clickSiteClosedNoRadio();
        testData.addStringTag("siteClosedValue", "No");
        runtimeState.helpdeskLogAnIncidentPage.clickDepartmentClosedNoRadio();
        testData.addStringTag("departmentClosedValue", "No");
    }

    @And("^the site questions are answered when the site is \"([^\"]*)\" and department is \"([^\"]*)\"$")
    public void the_site_questions_are_answered_when_the_site_is_open_and_department_closed(String siteValue, String departmentValue) throws Throwable  {

        if (siteValue.equals("open")) {
            runtimeState.helpdeskLogAnIncidentPage.clickSiteClosedNoRadio();
            testData.addStringTag("siteClosedValue", "No");
        }

        if (departmentValue.equals("closed")) {
            the_department_closed_radio_button_and_department_closed_date_is_selected();
            runtimeState.helpdeskLogAnIncidentPage.clickDepartmentReopenedNoRadio();
            testData.addStringTag("departmentReopenedValue", "No");
            runtimeState.helpdeskLogAnIncidentPage.selectDepartmentLikelyToReopenDate();
            testData.addStringTag("departmentLikelyToReopenDate", runtimeState.helpdeskLogAnIncidentPage.getDepartmentLikelyToReopenedDateValue());
        } else if (departmentValue.equals("open")) {
            the_department_closed_radio_button_and_department_closed_date_is_selected();
            the_department_reopened_radio_button_and_department_reopened_date_is_selected();
            testData.addStringTag("departmentReopenedDate", runtimeState.helpdeskLogAnIncidentPage.getDepartmentReopenedDateValue());
        }
    }

    @ContinueNextStepsOnException
    @Then("^user can log an incident$")
    public void user_can_log_an_incident() throws Throwable {
        runtimeState.helpdeskLogAnIncidentPage = runtimeState.helpdeskSitePage.clickLogAnIncidentButton();
        incident_description_entered();
        core_details_are_entered();
        the_site_questions_are_answered_with_department_closed();

        if (testData.getString("incidentType") != null) {
            the_Incident_Type_is_selected(testData.getString("incidentType"));
        } else {
            the_Incident_Type_is_selected(dbHelperIncidents.getRandomIncidentType());
        }
        all_incident_questions_are_answered_with_escalation_criteria_option();
        outputHelper.takeScreenshots();
        runtimeState.helpdeskLogAnIncidentPage.saveButtonIsClicked();
    }

    @ContinueNextStepsOnException
    @Then("^a new incident is logged with escalation criteria option \"([^\"]*)\"$")
    public void a_new_incident_is_logged_with_escalation_ctiteria_option(String option) throws Throwable {
        runtimeState.helpdeskHomePage.closeActiveTab();
        testData.addStringTag("escalationCriteria", option);
        helpdeskSearchForSiteSteps.a_search_is_run_for_City_Tech_store_with_caller("a search is run for an", "Occupied");
        user_can_log_an_incident();
        runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();
        int incidentReference = runtimeState.helpdeskViewIncidentPage.getIncidenceReference();
        testData.addIntegerTag("incidentReference", incidentReference);
        runtimeState.helpdeskHomePage.closeActiveTab();
        runtimeState.helpdeskHomePage.closeActiveTab();
        runtimeState.helpdeskHomePage.selectTile("Incidents");
        
    }

    @And("^user can edit description, site questions and answer fields$")
    public void user_can_edit_description_site_questions_and_answer_fields() throws Throwable {
        assertTrue("User can not edit Incident Description ",runtimeState.helpdeskLogAnIncidentPage.IsDescriptionTextboxEnabled());
        assertTrue("User can not edit Incident Site questions : Site Closed Yes button is disbled ",runtimeState.helpdeskLogAnIncidentPage.IsSiteClosedYesRadioEnabled());
        assertTrue("User can not edit Incident Site questions : Site Closed No button is disabled ",runtimeState.helpdeskLogAnIncidentPage.IsSiteClosedNoRadioEnabled());

        String question = dbHelperIncidents.getIncidentQuestion(testData.getInt("incidentReference"));
        String questionText = question.contains("'") ? question.split("\\'")[1].trim() : question;
        assertTrue("User can not edit Incident answer field",runtimeState.helpdeskLogAnIncidentPage.IsAnswerFieldEnabled(questionText));
    }

    @And("^user clicks the complete review checkbox$")
    public void user_clicks_the_complete_review_checkbox() throws Throwable {
        runtimeState.helpdeskLogAnIncidentPage.clickCompleteReviewCheckbox();
        runtimeState.scenario.write("Complete review checkbox is clicked ");
    }
    
    @And("^an incident is saved$")
    public void incident_is_saved() throws Throwable {
        runtimeState.helpdeskLogAnIncidentPage.saveButtonIsClicked();
    }

    @And("^a new incident is logged$")
    public void a_new_incident_is_logged() throws Throwable {
        date_and_time_of_an_incident_is_entered();
        the_site_questions_are_answered_when_the_site_is_closed();
        all_incident_questions_are_answered();
        incident_is_saved();
    }
    
    @And("^an incident is logged$")
    public void an_incident_is_logged() throws Throwable {
        date_and_time_of_an_incident_is_entered();
        the_site_questions_are_answered_when_the_site_is_closed();
        the_Incident_Type_is_selected(dbHelperIncidents.getRandomIncidentType());
        all_incident_questions_are_answered();
        incident_is_saved();
    }

    @ContinueNextStepsOnException
    @Then("^user can log all types of incidents$")
    public void user_can_log_all_types_of_incidents() throws Throwable {

        List<String> incidentTypes = dbHelperIncidents.getAllIncidentTypes();

        for (int i = 0; i < incidentTypes.size(); i++) {

            // Enter all the details and then log an incident
            runtimeState.helpdeskLogAnIncidentPage = runtimeState.helpdeskSitePage.clickLogAnIncidentButton();
            incident_description_entered();
            core_details_are_entered();
            the_site_questions_are_answered_when_nothing_is_closed();
            the_Incident_Type_is_selected(incidentTypes.get(i).toString());
            testData.put("questions", null);
            all_incident_questions_are_answered_with_escalation_criteria_option();
            outputHelper.takeScreenshot();
            runtimeState.helpdeskLogAnIncidentPage.saveButtonIsClicked();
            runtimeState.scenario.write("The incident type " + incidentTypes.get(i).toString() + " is logged");

            // Validate that all the details are shown correctly
            helpdeskViewIncidentSteps.incident_summary_page_is_displayed();
            helpdeskIncidentTimelineSteps.the_Incident_timeline_contains_a_row_for_description_as_and_type_as("Incident was logged", "Incident logged");
            helpdeskIncidentAnswersSteps.the_Answers_tab_displays_the_all_incident_questions_asked_with_answers();
            outputHelper.takeScreenshots();
            runtimeState.helpdeskHomePage.closeActiveTab();
        }
    }
}


