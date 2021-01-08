package mercury.steps.portal.jobs;

import static mercury.helpers.Globalisation.localiseMap;
import static mercury.helpers.Globalisation.setWeightLabel;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.FgasAdminHelper;
import mercury.helpers.FgasPortalHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.admin.refrigerantGasUsage.AdminLeakSiteInformationModal;
import mercury.pageobject.web.pageHelpers.QuestionHelper;
import mercury.pageobject.web.portal.jobs.refrigerant.PortalAppliancePartial;
import mercury.pageobject.web.portal.jobs.refrigerant.PortalLeakCheckAppliancePartial;
import mercury.pageobject.web.portal.jobs.refrigerant.PortalLeakSiteInformationModal;
import mercury.pageobject.web.portal.jobs.refrigerant.PortalLeakSiteInformationPartial;
import mercury.pageobject.web.portal.jobs.refrigerant.PortalRefrigerantMaxChargePartial;
import mercury.pageobject.web.portal.jobs.refrigerant.PortalRefrigerantSourceModal;
import mercury.pageobject.web.portal.jobs.refrigerant.PortalRefrigerantSourcePartial;
import mercury.runtime.RuntimeState;

public class PortalGasSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private FgasPortalHelper fgasPortalHelper;
    @Autowired private FgasAdminHelper fgasAdminHelper;

    @Given("^gas questions for \"([^\"]*)\" gas type$")
    public void gas_questions_for_gas_type(String gasType) {
        testData.put("gasQuestionSet", gasType);
    }

    @Given("^an asset ((?:with|without)) gas$")
    public void a_non_gas_asset(String gas) {
        Boolean value = gas.equalsIgnoreCase("with") ? true : false;
        testData.put("inPlant", value);
        testData.put("asset", value);
    }

    /**
     * Runs any actions before answering the sub section with questions, for example clicking a button to launch a modal
     * Also initialises the correct questionHelper partial page object relevant to the sub section.
     *
     * @param subSection
     * @throws Throwable
     */
    private void preAnswerChecks(String subSection) throws Throwable {
        switch (subSection) {
        case "FGAS Appliance":
            runtimeState.portalAppliancePartial = new PortalAppliancePartial(getWebDriver()).get();
            runtimeState.questionHelper = runtimeState.portalAppliancePartial.getQuestionHelper();
            break;

        case "Refrigerant Source":
            runtimeState.portalRefrigerantSourcePartial = new PortalRefrigerantSourcePartial(getWebDriver()).get();
            runtimeState.portalRefrigerantSourcePartial.addRefrigerantSource();
            runtimeState.portalRefrigerantSourceModal = new PortalRefrigerantSourceModal(getWebDriver()).get();
            runtimeState.questionHelper = runtimeState.portalRefrigerantSourceModal.getQuestionHelper();
            break;

        case "Maximum Charge":
            runtimeState.portalRefrigerantMaxChargePartial = new PortalRefrigerantMaxChargePartial(getWebDriver()).get();
            runtimeState.questionHelper = runtimeState.portalRefrigerantMaxChargePartial.getQuestionHelper();
            break;

        case "Leak Check Questions":
            runtimeState.portalLeakCheckAppliancePartial = new PortalLeakCheckAppliancePartial(getWebDriver()).get();
            runtimeState.questionHelper = runtimeState.portalLeakCheckAppliancePartial.getQuestionHelper();
            break;

        case "Leak Site Information":
            runtimeState.portalLeakSiteInformationModal = new PortalLeakSiteInformationModal(getWebDriver());
            if (!runtimeState.portalLeakSiteInformationModal.isPopupVisible()) {
                runtimeState.portalLeakSiteInformationPartial = new PortalLeakSiteInformationPartial(getWebDriver()).get();
                runtimeState.portalLeakSiteInformationPartial.addLeakSiteInformation();
                runtimeState.portalLeakSiteInformationModal = new PortalLeakSiteInformationModal(getWebDriver()).get();
            }
            runtimeState.questionHelper = runtimeState.portalLeakSiteInformationModal.getQuestionHelper();
            break;

        default:
            throw new Exception("Doh the type of question cannot be found, this can be down to a number of things, 1: the develop is an idiot, 2: someone has changed the feature file and not the code, 3: both. ");
        }

    }

    /**
     * Runs any actions after answering the sub section with questions, for example clicking a button to save the question
     * answers
     *
     * @param subSection
     * @throws Throwable
     */
    private void checkPostButtonClicks(String subSection) throws Throwable {
        Map<String, Object> map = new HashMap<String, Object>();
        switch (subSection) {
        case "FGAS Appliance":
            break;

        case "Refrigerant Source":
            // Store the data in test data and save the form
            map = testData.getMap("Refrigerant Source");

            if (map.get("gasCylinderTypeId") != null && map.get("gasPoundsInCylinderID") != null) {
                testData.addToMap(subSection, setWeightLabel("Total (%s)"), "Full".equalsIgnoreCase(map.get("gasCylinderTypeId").toString()) ? runtimeState.portalRefrigerantSourceModal.getTotal() : map.get("gasPoundsInCylinderID").toString());
                testData.addToMap(subSection, "Source", map.get("refrigerantSourceID"));
            }

            runtimeState.portalRefrigerantSourceModal.addSource();

            // Collate the data to testData for use in validation checks
            map = new HashMap<String, Object>();

            Map<String, Object> tempMap = testData.getMap("Refrigerant Source");
            if (tempMap != null) {
                map = Stream.concat(map.entrySet().stream(), tempMap.entrySet().stream()).filter(entry -> entry.getValue() != null).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

                testData.putMap("refrigerantGasUsage", map);

                // Now tidy up test data
                testData.removeTag("Refrigerant Source");
                testData.removeTag("Refrigerant Installed");
                testData.removeTag("Refrigerant Surplus");
            }

            break;
        case "Maximum Charge":
            break;

        case "Leak Check Questions":
            break;

        case "Leak Site Information":
            // Store the data in test data and save the form
            map = testData.getMap("Leak Site Information");
            map.put("Leak Location", map.get("gasLeakLocationId"));
            map.put("Primary Component Information", map.get("primaryComponentInformation"));
            map.put("Leak Sub Location", map.get("gasLeakSubLocationId"));
            map.put("Leak Site Status", map.get("gasLeakSiteStatusId"));
            map.put("Initial Verification Test", map.get("gasLeakInitialTestId"));
            map.put("Follow Up Verification Test", map.get("gasLeakFollowUpTestId"));

            testData.addMap("leakSiteInformation", map);
            testData.removeTag("Leak Site Information");

            if (runtimeState.portalLeakSiteInformationModal != null && runtimeState.portalLeakSiteInformationModal.isAddButtonDisplayed()) {
                runtimeState.portalLeakSiteInformationModal.addLeakSiteInformation();
            }
            break;

        default:
            throw new Exception("Doh the type of question cannot be found, this can be down to a number of things, 1: the develop is an idiot, 2: someone has changed the feature file and not the code, 3: both. ");
        }
    }

    @When("^the Gas \"([^\"]*)\" sub section questions are answered$")
    public void the_gas_sub_secion_questions_are_answered(String subSection) throws Throwable {

        preAnswerChecks(subSection);

        if (runtimeState.questionHelper == null) {
            if (getWebDriver().getCurrentUrl().contains("Portal")) {
                runtimeState.questionHelper = runtimeState.updateJobPage.getQuestionHelper();
            } else {
                runtimeState.questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
            }
        }

        if (subSection.equals("FGAS Appliance")) {
            fgasAdminHelper.answerFgasApplianceQuestions(runtimeState.questionHelper);

        } else if (subSection.equals("Refrigerant Source")) {
            fgasAdminHelper.updateCylinder(runtimeState.questionHelper);
        }

        outputHelper.takeScreenshots();
        runtimeState.scenario.write(subSection + " data: " + testData.getString(subSection));

        checkPostButtonClicks(subSection);


        if (subSection.equals("Refrigerant Source")) {
            if (runtimeState.portalRefrigerantMaxChargePartial == null) runtimeState.portalRefrigerantMaxChargePartial = new PortalRefrigerantMaxChargePartial(getWebDriver());
            runtimeState.questionHelper = runtimeState.portalRefrigerantMaxChargePartial.getQuestionHelper();

            if (runtimeState.questionHelper.isQuestionVisible("Please enter appliance maximum charge")) {
                String newMaxCharge = "999";
                runtimeState.questionHelper.updateAnswer("Please enter appliance maximum charge", newMaxCharge);
                testData.addToMap("Maximum Charge", "NewAssetMaximumCharge", newMaxCharge);
            }

            if (runtimeState.questionHelper.isQuestionVisible("Please provide notes to explain reason for changing appliance maximum charge")) {
                String reason = DataGenerator.generateRandomSentence();
                runtimeState.questionHelper.updateAnswer("Please provide notes to explain reason for changing appliance maximum charge", reason);
                testData.addToMap("Maximum Charge", "ReasonForChangingMaximumCharge", reason);
            }
        }
        runtimeState.scenario.write(testData.getTestData());
    }

    @When("^the Gas Leak Check Questions sub section questions are answered with$")
    public void the_gas_Leak_Check_Questions_questions_are_answered_with(DataTable preAnswered) throws Throwable {
        Map<String, String> preAnsweredMap = localiseMap(preAnswered.asMap(String.class, String.class));
        preAnsweredMap.remove("Question"); // remove datatable header
        preAnsweredMap.forEach((question, answer) -> testData.put(question, answer));

        fgasPortalHelper.updateLeakInspectionAndRepair();

        outputHelper.takeScreenshots();
    }

    @When("^the Leak Check questions are answered forcing the resource to return$")
    public void the_Leak_Check_questions_are_answered_forcing_the_resource_to_return() throws Throwable {

        String valuesInArray[]= {"leakCheckStatus", "leakCheckResultType", "leakSiteStatus", "gasLeakFollowUpTest"};
        List<String> returnQuestions = new ArrayList<>(Arrays.asList(valuesInArray));

        // get random answers that would force a return
        String leakCheckStatusForcingReturn = dbHelperGas.getRandomLeakCheckStatus(true);
        String leakCheckResultTypeForcingReturn = dbHelperGas.getRandomLeakCheckResultType(true);
        String leakSiteStatusForcingReturn = dbHelperGas.getRandomLeakSiteStatus(true);
        String gasLeakFollowUpTestForcingReturn = dbHelperGas.getRandomGasLeakFollowUpTest(true);

        // if a random answer was found then determine the questions for that answer
        if (leakCheckStatusForcingReturn == null) returnQuestions.remove(returnQuestions.indexOf("leakCheckStatus"));
        if (leakCheckResultTypeForcingReturn == null) returnQuestions.remove(returnQuestions.indexOf("leakCheckResultType"));
        if (leakSiteStatusForcingReturn == null) returnQuestions.remove(returnQuestions.indexOf("leakSiteStatus"));
        if (gasLeakFollowUpTestForcingReturn == null) returnQuestions.remove(returnQuestions.indexOf("gasLeakFollowUpTest"));

        // no answers found that would force a return
        if (returnQuestions.size() == 0) throw new PendingException("No data found in DB that would force a return");

        // choose one of the return questions to trigger the return
        String questionToForceReturn = returnQuestions.get(RandomUtils.nextInt(0, returnQuestions.size() - 1));

        String answerToForceReturn = "";
        if (questionToForceReturn.equals("leakCheckStatus")) answerToForceReturn = leakCheckStatusForcingReturn;
        if (questionToForceReturn.equals("leakCheckResultType")) answerToForceReturn = leakCheckResultTypeForcingReturn;
        if (questionToForceReturn.equals("leakSiteStatus")) answerToForceReturn = leakSiteStatusForcingReturn;
        if (questionToForceReturn.equals("gasLeakFollowUpTest")) answerToForceReturn = gasLeakFollowUpTestForcingReturn;

        runtimeState.scenario.write("Forcing resource to return by setting " + questionToForceReturn + " to: " + answerToForceReturn);

        // now answer the questions ...
        runtimeState.updateJobPage.scrollToGasLeakCheckQuestions();
        QuestionHelper questionHelper = runtimeState.updateJobPage.getQuestionHelper();

        String answer = questionToForceReturn.equals("leakCheckStatus") ? leakCheckStatusForcingReturn : dbHelperGas.getRandomLeakCheckStatus(false);
        questionHelper.updateAnswer("Leak Check Status", answer);
        testData.put("GasLeakCheckStatusId", answer);
        testData.addToMap("Leak Check Questions", "GasLeakCheckStatusId", answer);

        if (questionHelper.isQuestionVisible("Leak Check Method")) {
            answer = questionHelper.updateAnswer("Leak Check Method");
            testData.put("GasLeakCheckMethodId", answer);
            testData.addToMap("Leak Check Questions", "GasLeakCheckMethodId", answer);

            answer = questionToForceReturn.equals("leakCheckResultType") ? leakCheckResultTypeForcingReturn : dbHelperGas.getRandomLeakCheckResultType(false);
            questionHelper.updateAnswer("Leak Check Result Type", answer);
            testData.put("GasLeakCheckResultTypeId", answer);
            testData.addToMap("Leak Check Questions", "GasLeakCheckResultTypeId", answer);

            runtimeState.updateJobPage.addLeakSiteDetails();

            answer = questionToForceReturn.equals("gasLeakFollowUpTest") ? gasLeakFollowUpTestForcingReturn : dbHelperGas.getRandomGasLeakFollowUpTest(false);
            testData.put("followUpVerificationTestAnswer", answer);

            answer = questionToForceReturn.equals("leakSiteStatus") ? leakSiteStatusForcingReturn : dbHelperGas.getRandomLeakSiteStatus(false);
            testData.put("leakSiteStatusAnswer", answer);

            fgasAdminHelper.enterLeakDetails();
            checkPostButtonClicks("Leak Site Information");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" answers are displayed on update job page$")
    public void the_gas_sub_section_answers_are_displayed(String subSection) throws Throwable {
        Grid grid = null;
        switch (subSection) {
        case "FGAS Appliance":
            break;

        case "Refrigerant Source":
            runtimeState.portalRefrigerantSourcePartial = new PortalRefrigerantSourcePartial(getWebDriver()).get();
            grid = runtimeState.portalRefrigerantSourcePartial.getGasGrid();

            // need to refactor this
            for (Row row : grid.getRows()) {
                assertEquals("Unexpected Supplier column data", testData.getString("refrigerantSourceID"), row.getCell("Source"));
                assertEquals("Unexpected Cylinder Type column data", testData.getString("cylinderTypeId"), row.getCell("Full/Partial"));
                assertEquals("Unexpected Pounds in Cylinder column data", testData.getString("gasPoundsInCylinderID"), row.getCell(setWeightLabel("Total (%s)")));
            }
            break;

        case "Leak Site Information":
            runtimeState.portalLeakSiteInformationPartial = new PortalLeakSiteInformationPartial(getWebDriver()).get();
            grid = runtimeState.portalLeakSiteInformationPartial.getGasGrid();
            assertNotNull("Unexpected empty " + subSection + " grid", grid);
            List<LinkedHashMap<String, Object>> gridAsHashMap = GridHelper.getGridAsMapList(grid);

            List<LinkedHashMap<String, Object>> expectdGrid = testData.getLinkedListMap("leakSiteInformation");
            assertEquals("Unexpected differences in expected grid compared to actual grid", expectdGrid, gridAsHashMap);
            break;

        default:
            throw new Exception("Doh the type of question cannot be found, this can be down to a number of things, 1: the develop is an idiot, 2: someone has changed the feature file and not the code, 3: both. ");
        }
    }

    @When("^the \"([^\"]*)\" questions are configured$")
    public void the_refrigerant_gas_questions_are_configured(String subSection, DataTable questions) throws Throwable {

        // Initialise the correct question helper
        preAnswerChecks(subSection);

        // Now validate the questions
        for (Map<String, String> row : questions.asMaps(String.class, String.class)) {
            String questionText = row.get("Question");
            String state = row.get("State");
            boolean visible;
            if (state.equalsIgnoreCase("Invisible") || state.equalsIgnoreCase("Visible")) {
                visible = runtimeState.questionHelper.isQuestionVisible(questionText);
                assertEquals("Unexpected visible state for " + questionText, (state.equalsIgnoreCase("Invisible") ? false : true), visible);
            }
        }
    }

    /**
     * This needs to be refactored and moved to a more common step file. Will be refactored to include the questionHelper
     * object. UpdateJob page does not use the questionHelper.
     *
     * @param form
     * @param question
     * @param answer
     * @throws Throwable
     */
    @ContinueNextStepsOnException
    @Then("^on the form \"([^\"]*)\" the question \"([^\"]*)\" is answered with \"([^\"]*)\"$")
    public void question_is_answered_with(String form, String question, String answer) throws Throwable {

        switch (form) {
        case "Update Job":
            String pageAnswer = runtimeState.updateJobPage.getStatusOnDeparture();
            assertEquals("Unexpected answer for question", answer, pageAnswer);
            break;
        default:
            throw new Exception("Doh the form cannot be found, this can be down to a number of things, 1: the develop is an idiot, 2: someone has changed the feature file and not the code, 3: both.");

        }
    }

    @ContinueNextStepsOnException
    @Then("^the following information message is displayed: \"([^\"]*)\"$")
    public void the_following_information_message_is_displayed(String message) {
        assertTrue(runtimeState.updateJobPage.isInformationMessageDisplayed(message));
    }

    @ContinueNextStepsOnException
    @Then("^a Follow Up button is displayed for the previous Leak Site Check$")
    public void for_previous_Leak_Site_Check_a_Follow_Up_button_is_displayed() throws Throwable {
        if (runtimeState.adminRefrigerantGasUsageEditPageV2 != null) {
            assertTrue("Button is not displayed", runtimeState.adminRefrigerantGasUsageEditPageV2.isButtonDisplayed("Follow Up"));
        } else {
            assertTrue("Button is not displayed", runtimeState.updateJobPage.isButtonDisplayed("Follow Up"));
        }
    }

    @ContinueNextStepsOnException
    @Then("^FGAS \"([^\"]*)\" are mandatory$")
    public void refrigerant_details_are_mandatory(String field) throws Throwable {
        if (field.equals("ETA Date")) {
            assertFalse(runtimeState.updateJobPage.getETADate().isEmpty());
        } else {

            fgasPortalHelper.captureAllFgasUsQuestions();
            runtimeState.updateJobPage.updateJob();

            String mandatoryAlert = "";
            switch (field) {
            case "Refrigerant Details": mandatoryAlert = "Please add at least one gas source"; break;
            case "Reason for Returning": mandatoryAlert = "Please select a reason for returning"; break;
            case "ETA Date": mandatoryAlert = "ETA date cannot be in the past"; break;
            default: throw new Exception("Unknown field: " + field);
            }
            runtimeState.scenario.write("Asserting alert is displayed: " + mandatoryAlert);
            assertTrue("Expected alert: " + mandatoryAlert, runtimeState.updateJobPage.getAlerts().contains(mandatoryAlert));
        }
    }

    @When("^the Leak Site Check 'Follow Up' button is clicked$")
    public void the_Leak_Site_Check_Follow_Up_button_is_clicked() throws Throwable {
        runtimeState.adminLeakSiteInformationModal = runtimeState.updateJobPage.followUp();
    }

    @ContinueNextStepsOnException
    @Then("^Add Leak Site Details is \"([^\"]*)\"$")
    public void Add_Leak_Site_Details_is(String mandatory) throws Throwable {
        QuestionHelper questionHelper = runtimeState.updateJobPage.getQuestionHelper();

        // this ensures that when 'Update Job' is selected that we wont leave the page
        if (questionHelper.isQuestionVisible("Reason for Returning")) {
            questionHelper.updateAnswer("Reason for Returning", "");
        } else {
            if ( !questionHelper.getAnswer("Status on Departure").equals("Please select a status") ) {
                questionHelper.updateAnswer("Status on Departure", "");
            }
        }

        fgasPortalHelper.captureAllFgasUsQuestions();
        runtimeState.updateJobPage.updateJob();

        String alert = "Please add the details of at least one leak check";
        if (mandatory.equals("mandatory")) {
            runtimeState.scenario.write("Asserting alert is displayed: " + alert);
            assertTrue("Expected alert: " + alert, runtimeState.updateJobPage.getAlerts().contains(alert));
        } else {
            runtimeState.scenario.write("Asserting alert is NOT displayed: " + alert);
            assertFalse("Unexpected alert: " + alert, runtimeState.updateJobPage.getAlerts().contains(alert));
        }
    }

    @When("^Add Leak Site Details is selected$")
    public void Add_Leak_Site_Details_is_selected() {
        runtimeState.updateJobPage.addLeakSiteDetails();
    }

    private void assertLeakSiteSiteInformationWarning(String warning) {
        runtimeState.scenario.write("Asserting " + warning);
        assertTrue("Expected: " + warning, runtimeState.adminLeakSiteInformationModal.getErrors().contains(warning));
    }

    private void assertMandatoryLeakSiteSiteInformation() throws Exception {
        runtimeState.adminLeakSiteInformationModal.add();

        assertLeakSiteSiteInformationWarning("Primary Component is required");
        assertLeakSiteSiteInformationWarning("Primary Component Information is required");
        assertLeakSiteSiteInformationWarning("Site Status is required");
        assertLeakSiteSiteInformationWarning("Initial Verification Test is required");
        assertLeakSiteSiteInformationWarning("Follow Up Verification Test is required");

        QuestionHelper questionHelper = runtimeState.adminLeakSiteInformationModal.getQuestionHelper();
        questionHelper.updateAnswer("Primary Component Information", "123456789_123456789");
        String expectedWarning = "Primary Component Information must be at least 20 characters";
        runtimeState.scenario.write("Asserting: " +  expectedWarning);
        outputHelper.takeScreenshots();
        assertTrue("Expected: " + expectedWarning + " characters", runtimeState.adminLeakSiteInformationModal.getErrors().contains(expectedWarning));
    }

    private void populate_Leak_Site_Information(DataTable dataTable) throws Exception {
        if (runtimeState.adminLeakSiteInformationModal == null) runtimeState.adminLeakSiteInformationModal = new AdminLeakSiteInformationModal(getWebDriver()).get();

        QuestionHelper questionHelper = runtimeState.adminLeakSiteInformationModal.getQuestionHelper();
        List<Map<String, String>> data = dataTable == null ? null :dataTable.asMaps(String.class, String.class);

        assertMandatoryLeakSiteSiteInformation();

        List<String> questions = questionHelper.getAllQuestions();
        int index = 0;
        while (questions.size() > index) {
            String question = questions.get(index);
            String answer = "";
            if (data != null) { // datatable provided
                for (Map<String, String> qa : data) {                   // for all QA's supplied in scenario
                    if (qa.get("Question").equals(question)) {          // if modal question = scenario question
                        if ("dropdown".equals(questionHelper.getAnswerType(question))) {
                            List<String> options = questionHelper.getDropdownOptions(question);
                            for (String option : options) {
                                if (option.toLowerCase().contains(qa.get("Answer").toLowerCase())) {     // if modal question is a dropdown then choose suitable option
                                    answer = option;
                                    break;
                                }
                            }
                        } else {
                            answer = qa.get("Answer");                      // not a dropdown, so enter provided answer
                        }
                        assertFalse("Invalid answer: " + qa.get("Question"), answer.isEmpty());
                        questionHelper.updateAnswer(question, answer);
                        break;
                    }
                }
            }

            if (answer.isEmpty()) {
                // question / answer not provided in scenario datatable
                if (question.contains("Primary Component Information")) {
                    String warning = "Primary Component Information must be at least 20 characters";
                    answer = "text length 20 chars";
                    questionHelper.updateAnswer("Primary Component Information", answer);
                    assertFalse("Unexpected: " + warning, runtimeState.adminLeakSiteInformationModal.getErrors().contains(warning));

                } else {
                    answer = questionHelper.updateAnswer(question);
                }
            }
            runtimeState.scenario.write(question + " set to: " + answer);
            questions = questionHelper.getAllQuestions();
            index++;
        }
    }

    @When("^Leak Site Details are added with$")
    public void Leak_Site_Details_added(DataTable dataTable) throws Throwable {
        runtimeState.updateJobPage.addLeakSiteDetails();
        populate_Leak_Site_Information(dataTable);
        outputHelper.takeScreenshot();
        runtimeState.adminLeakSiteInformationModal.add();
    }

    @When("^Leak Site Details are entered then cancelled$")
    public void Leak_Site_Details_cancelled() throws Exception {
        runtimeState.updateJobPage.addLeakSiteDetails();
        populate_Leak_Site_Information(null);
        outputHelper.takeScreenshot();
        runtimeState.adminLeakSiteInformationModal.cancel();
    }

    @When("^Leak Site Information section is added with$")
    public void Leak_Site_Information_is_added(DataTable dataTable) throws Exception {
        populate_Leak_Site_Information(dataTable);
        outputHelper.takeScreenshot();
        runtimeState.adminLeakSiteInformationModal.add();
    }

    @When("^an additional Leak Site Check is added$")
    public void additional_Leak_Site_Checks_can_be_added() throws Throwable {
        runtimeState.updateJobPage.addAdditionalLeakSiteDetails();
        populate_Leak_Site_Information(null);
        runtimeState.adminLeakSiteInformationModal.add();
    }

    @ContinueNextStepsOnException
    @Then("^no Leak Site Checks are listed$")
    public void no_leak_Site_Checks_are_listed() throws Throwable {
        List<String> displayedLeakSiteChecks = runtimeState.updateJobPage.getLeakSiteChecks();
        assertNull("Unexpected Leak Site Check displayed", displayedLeakSiteChecks);
    }

    @ContinueNextStepsOnException
    @Then("^the following Leak Site Checks are listed \"([^\"]*)\"$")
    public void leak_Site_Checks_are_listed_with_a_reference_number(String leakSiteChecks) throws Throwable {
        String[] references = leakSiteChecks.split(",");
        List<String> displayedLeakSiteChecks = runtimeState.updateJobPage.getLeakSiteChecks();
        for (String reference : references) {
            assertTrue("Leak Site Check not displayed: " + reference, displayedLeakSiteChecks.contains(reference.trim()));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Status on Departure can be set to \"([^\"]*)\"$")
    public void job_Status_can_be_set_to(String jobStatuses) throws Throwable {
        QuestionHelper questionHelper = runtimeState.updateJobPage.getQuestionHelper();
        List<String> departureStatuses = questionHelper.getDropdownOptions("Status on Departure");

        String[] statuses = jobStatuses.split(",");
        for (String status : statuses) {
            assertTrue("Departure Status not displayed: " + status, departureStatuses.contains(status.trim()));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Status on Departure has been set to \"([^\"]*)\"$")
    public void job_Status_has_been_set_to(String jobStatus) throws Throwable {
        QuestionHelper questionHelper = runtimeState.updateJobPage.getQuestionHelper();
        String departureStatus = questionHelper.getAnswer("Status on Departure");
        assertTrue("Departure Status not displayed: " + jobStatus, departureStatus.contains(jobStatus.trim()));
    }

    @When("^the Status on Departure is set to \"([^\"]*)\"$")
    public void job_Status_is_set_to(String jobStatus) throws Throwable {
        QuestionHelper questionHelper = runtimeState.updateJobPage.getQuestionHelper();
        if (!questionHelper.getAnswer("Status on Departure").equals(jobStatus)) {
            questionHelper.updateAnswer("Status on Departure", jobStatus);
        }
    }

}
