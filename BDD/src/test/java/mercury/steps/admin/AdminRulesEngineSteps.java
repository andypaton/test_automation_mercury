package mercury.steps.admin;

import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.LoginLogoutHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.gridV3.Grid;
import mercury.pageobject.web.admin.AdminQuestionPage;
import mercury.runtime.RuntimeState;
import mercury.steps.LoginSteps;
import mercury.steps.helpdesk.monitors.TileSteps;

public class AdminRulesEngineSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private LoginSteps loginSteps;
    @Autowired private TileSteps tileSteps;
    @Autowired private TestData testData;
    @Autowired private LoginLogoutHelper loginLogoutHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private OutputHelper outputHelper;

    @Given("^the configuration for job question: \"([^\"]*)\" is checked$")
    public void theConfigurationForJobQuestionIsChecked(String jobQuestion) throws Throwable {
        loginSteps.a_user_with_role_has_logged_in("Mercury_Admin_Rule_Engine");
        runtimeState.adminHomePage = runtimeState.helpdeskNavBar.OpenAdminApp();
        tileSteps.a_random_tile_is_selected("Rules Engine");
        runtimeState.adminRulesEnginePage.selectQuestionListOption();
        runtimeState.adminRulesEnginePage.editQuestionFile(jobQuestion);
        runtimeState.adminQuestionPage = new AdminQuestionPage(getWebDriver()).get();
        String questionType = runtimeState.adminQuestionPage.getQuestionType();
        testData.put("questionType", normalize(questionType));
        loginLogoutHelper.logout();
    }

    @When("^add new rule is selected$")
    public void addNewRuleIsSelected() throws Throwable {
        runtimeState.adminRulePage = runtimeState.adminRulesEnginePage.clickAddNewRuleButton();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^a new rule can be added$")
    public void aNewRuleCanBeAdded() throws Throwable {
        runtimeState.adminRulePage.selectRuleType("Job Questions");
        String rule = "TestAuto" + System.currentTimeMillis();
        runtimeState.adminRulePage.enterRuleDescription(rule);
        testData.put("ruleDescription", rule);
        runtimeState.adminRulePage.setActiveFromDate();
        runtimeState.adminRulePage.setActiveToDate();
        List<String> listOfQuestions = dbHelper.getListOfJobQuestions();

        // Removing any extra spaces in each question so we don't have to update them manually on DB after every refresh
        List<String> questionsList = new ArrayList<String>();
        for (String question : listOfQuestions) {
            question = normalize(question);
            questionsList.add(question);
        }
        runtimeState.scenario.write("Removed extra spaces from questions list. ");

        List<String> jobQuestions = runtimeState.adminRulePage.getJobQuestions();
        questionsList = questionsList.stream().map(String::toLowerCase).collect(Collectors.toList());
        jobQuestions = jobQuestions.stream().map(String::toLowerCase).collect(Collectors.toList());
        assertThat("List equality without order", questionsList, containsInAnyOrder(jobQuestions.toArray()));
        String[] fields = { "Asset SubType", "Asset Type", "Asset classification", "Asset tag", "Brand", "Fault type", "Location", "Site" };
        String field = fields[new Random().nextInt(fields.length)];
        testData.put("field", field);
        runtimeState.adminRulePage.selectFieldDropdown(field);
        String[] comparisonOperators = { "=", "<>" };
        String comparisonOperator = comparisonOperators[new Random().nextInt(comparisonOperators.length)];
        testData.put("comparisonOperator", comparisonOperator);
        runtimeState.adminRulePage.selectComparisonDropdown(comparisonOperator);

        String value = null;
        switch(testData.get("field").toString().toUpperCase()) {
        case "ASSET SUBTYPE":
            value = dbHelper.getRandomAssetSubTypeName();
            break;
        case "ASSET TYPE":
            value = dbHelper.getRandomAssetTypeName();
            break;
        case "ASSET CLASSIFICATION":
            value = dbHelper.getRandomAssetClassificationName();
            break;
        case "ASSET TAG":
            value = dbHelper.getRandomAssetTag();
            break;
        case "BRAND":
            value = dbHelper.getRandomBrand();
            break;
        case "FAULT TYPE":
            value = dbHelper.getRandomFaultType();
            break;
        case "LOCATION":
            value = dbHelper.getRandomLocationName();
            break;
        case "SITE":
            value = dbHelper.getRandomSiteName();
            break;
        }

        testData.put("value", value);
        runtimeState.adminRulePage.enterValueDropdown(value);
        outputHelper.takeScreenshots();

        assertTrue("Button is not enabled", runtimeState.adminRulePage.isAddNewConditionButtonEnabled());
        runtimeState.adminRulePage.clickSaveButton();
    }

    @ContinueNextStepsOnException
    @Then("^a new question can be added$")
    public void aNewQuestionCanBeAdded() throws Throwable {
        runtimeState.adminRulesEnginePage.addNewQuestion();
        runtimeState.adminQuestionPage = new AdminQuestionPage(getWebDriver()).get();
        assertFalse("Button is enabled", runtimeState.adminQuestionPage.isButtonEnabled("Save"));
        runtimeState.adminQuestionPage.selectTemplateDropdownOption("Job Questions");
        testData.put("questionTemplate", "Job Questions");
        String questionType = dbHelper.getRandomQuestionType();
        runtimeState.adminQuestionPage.selectQuestionTypeDropdownOption(questionType);
        testData.put("questionType", questionType);
        String question = "TestAuto" + System.currentTimeMillis();
        runtimeState.adminQuestionPage.enterQuestionName(question);
        testData.put("questionName", question);
        runtimeState.adminQuestionPage.enterQuestionText(question);
        runtimeState.adminQuestionPage.selectRequiredNo();
        runtimeState.adminQuestionPage.selectActiveNo();
        if ("Radio Buttons".equals(questionType) || "Drop Down List".equals(questionType)) {
            runtimeState.adminQuestionPage.clickWarningAddOutcomeButton();
            runtimeState.adminQuestionPage.clickPrimaryAddOutcomeButton();
            runtimeState.adminQuestionPage.addOutcomeText(0, "Yes");
            runtimeState.adminQuestionPage.addOutcomeText(1, "No");
        }
        assertTrue("Button is not enabled", runtimeState.adminQuestionPage.isButtonEnabled("Cancel"));
        assertTrue("Button is not enabled", runtimeState.adminQuestionPage.isButtonEnabled("Save"));
        outputHelper.takeScreenshots();
        runtimeState.adminQuestionPage.buttonIsClicked("Save");
    }

    @ContinueNextStepsOnException
    @Then("^the question has been added to the database$")
    public void theQuestionHasBeenAddedToTheDatabase() throws Throwable {
        Map<String, Object> databaseEntry = dbHelper.isQuestionInDatabase();

        assertEquals("question does not appear in database", String.valueOf(testData.get("questionName")), databaseEntry.get("QuestionName"));
        assertEquals("question does not appear in database", String.valueOf(testData.get("questionType")), databaseEntry.get("QuestionType"));
        assertEquals("question does not appear in database", String.valueOf(testData.get("questionTemplate")), databaseEntry.get("QuestionTemplate"));

        // delete the added question
        if ("Radio Buttons".equals(testData.getString("questionType")) || "Drop Down List".equals(testData.getString("questionType"))) {
            dbHelper.deleteQuestionOptionAddedByAutoTest(testData.getString("questionName"));
        }

        dbHelper.deleteQuestionAddedByAutoTest(testData.getString("questionName"));
    }

    @ContinueNextStepsOnException
    @Then("^the question is updated on the database$")
    public void the_question_is_updated_on_the_database() throws Throwable {
        Map<String, Object> databaseEntry = dbHelper.getQuestionDetails(testData.getString("questionName"));
        runtimeState.scenario.write("Edited question details: " + databaseEntry);
        assertEquals("question does not appear in database", String.valueOf(testData.get("questionName")), databaseEntry.get("QuestionName"));
        assertEquals("active column is not updated in database", testData.getBoolean("questionActive"), databaseEntry.get("Active"));

        // delete the added question
        if ("Radio Buttons".equals(testData.getString("questionType")) || "Drop Down List".equals(testData.getString("questionType"))) {
            dbHelper.deleteQuestionOptionAddedByAutoTest(testData.getString("questionName"));
        }

        dbHelper.deleteQuestionAddedByAutoTest(testData.getString("questionName"));
    }

    @ContinueNextStepsOnException
    @Then("^the rule has been added to the database$")
    public void theRuleHasBeenAddedToTheDatabase() throws Throwable {
        assertTrue("Rule does not appear in database", dbHelper.isRuleInDatabase(testData.getString("ruleDescription")));

        // deleting the added rule from database
        dbHelper.deleteRulesEngineLookUpConditionAddedByAutoTest(testData.getString("ruleDescription"));
        dbHelper.deleteRuleAddedByAutoTest(testData.getString("ruleDescription"));
    }

    @ContinueNextStepsOnException
    @Then("^the selected question is edited$")
    public void theSelectedQuestionCanBeEdited() throws Throwable {
        aNewQuestionCanBeAdded();
        Map<String, Object> databaseEntry = dbHelper.getQuestionDetails(testData.getString("questionName"));
        runtimeState.scenario.write("New question details: " + databaseEntry);
        String question = testData.getString("questionName");
        runtimeState.adminRulesEnginePage.searchNameColumn(question);
        runtimeState.adminRulesEnginePage.clickEditLink();
        runtimeState.scenario.write("Editing the question to be Active. ");
        runtimeState.adminQuestionPage.selectActiveYes();
        testData.put("questionActive", true);
        outputHelper.takeScreenshots();
        runtimeState.adminQuestionPage.buttonIsClicked("Save");
        runtimeState.adminRulesEnginePage.searchNameColumn(question);
        Grid grid = runtimeState.adminRulesEnginePage.getGrid();
        assertNotNull("Grid is empty ", grid);
        runtimeState.scenario.write("Asserting that the question has been edited. ");
        assertTrue("Question has not been edited! ", grid.getRows().get(0).getCell("Active").getText().equalsIgnoreCase("Yes"));
        outputHelper.takeScreenshots();
    }

}
