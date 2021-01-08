package mercury.steps.admin;

import static mercury.helpers.FgasAdminHelper.AFTER;
import static mercury.helpers.FgasAdminHelper.BEFORE;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.Globalisation.setWeightLabel;
import static mercury.helpers.Constants.LABEL_WEIGHT_UK;
import static mercury.helpers.Constants.LABEL_WEIGHT_US;
import static mercury.helpers.StringHelper.randomAscii;
import static mercury.helpers.StringHelper.trimZeros;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static java.lang.Math.floor;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.JobTimelineEventDao;
import mercury.database.dao.SiteVisitGasLeakSiteCheckDao;
import mercury.database.dao.SiteVisitsDao;
import mercury.database.models.JobTimelineEvent;
import mercury.database.models.SiteVisitGasLeakSiteCheck;
import mercury.database.models.SiteVisits;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.FgasAdminHelper;
import mercury.helpers.FgasQuestions;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.pageobject.web.PopupAlert;
import mercury.pageobject.web.admin.refrigerantGasUsage.AdminRefrigerantGasUsageEditPage;
import mercury.pageobject.web.admin.refrigerantGasUsage.AdminRefrigerantGasUsageEditPageV2;
import mercury.pageobject.web.pageHelpers.QuestionHelper;
import mercury.runtime.RuntimeState;

public class AdminRefrigerantGasUsageSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private FgasAdminHelper fgasAdminHelper;
    @Autowired private TzHelper tzHelper;
    @Autowired private JobTimelineEventDao jobTimelineEventDao;
    @Autowired private SiteVisitGasLeakSiteCheckDao siteVisitGasLeakSiteCheckDao;
    @Autowired private SiteVisitsDao siteVisitsDao;


    @When("^the Refrigerant Gas Usage visit is viewed$")
    public void the_Refrigerant_Gas_Usage_visit_is_viewed() throws ParseException {
        SiteVisits siteVisit = new SiteVisits();
        siteVisit = (SiteVisits) testData.get("siteVisit");
        String workEndTime = DateHelper.getTimestampInFormat(siteVisit.getWorkEndTime(), MEDIUM);
        runtimeState.scenario.write("Work End Time returned from database: " + workEndTime);

        testData.put("workEndTime", tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), workEndTime, MEDIUM));

        runtimeState.scenario.write("Site Visit date completed: " + testData.getString("workEndTime"));

        if (runtimeState.adminRefrigerantGasUsageSearchPage.multipleVisitsDisplayed()) {
            outputHelper.takeScreenshots();
            runtimeState.adminRefrigerantGasUsageSearchPage.viewVisit(testData.getString("workEndTime"));
        }

        if ("2019".equals(testData.getString("fgasRegulations"))) {
            runtimeState.adminRefrigerantGasUsageEditPageV2 = new AdminRefrigerantGasUsageEditPageV2(getWebDriver()).get();
        } else {
            runtimeState.adminRefrigerantGasUsageEditPage = new AdminRefrigerantGasUsageEditPage(getWebDriver()).get();
        }
    }

    @When("^the following gas usage answers are updated:$")
    public void the_following_gas_usage_answers_are_updated(List<String> answers) throws Exception {
        fgasAdminHelper.updateUkFgasQuestions(answers);
    }

    @When("^the Amount Used is updated to \"([^\"]*)\" the maximum charge allowed$")
    public void the_Amount_Used_is_updated_to_the_maximum_charge_allowed(String amount) throws Exception {

        float maxCharge = testData.getFloat("maxGasCharge");
        maxCharge = !"2019".equals(testData.getString("fgasRegulations")) && maxCharge == 0 ? maxCharge = 1200 : maxCharge;    // pre-2019 Regulations hard coded to 1200 by default
        runtimeState.scenario.write("Max refrigerant charge allowed for asset = " + maxCharge);
        float qty = "EXCEED".equalsIgnoreCase(amount) ? maxCharge + 1 : Math.round(RandomUtils.nextFloat(0, maxCharge) * 100) / 100; // round to 2 decimal places

        String prevQty = runtimeState.adminRefrigerantGasUsageEditPage.getAmountsUsed().get(0);
        testData.put("origGasBottleQty", prevQty);

        runtimeState.adminRefrigerantGasUsageEditPage.editBottle();

        runtimeState.scenario.write("Updating amount used from " + prevQty + " to " + qty);

        runtimeState.adminRefrigerantGasUsageEditPage.enterAmountUsed(String.valueOf(qty));

        runtimeState.adminRefrigerantGasUsageEditPage.takeScreenshotOfPopup(outputHelper);
        runtimeState.adminRefrigerantGasUsageEditPage.update();

        String bottleNumber = runtimeState.adminRefrigerantGasUsageEditPage.getBottleNumbers().get(0);

        testData.addToMap("gasAmountUsed", bottleNumber, qty);
        testData.put("gasBottleQty", runtimeState.adminRefrigerantGasUsageEditPage.getAmountsUsed().get(0));
    }

    @When("^all gas answers are updated$")
    public void all_gas_answers_are_updated() throws Throwable {
        if ("2019".equals(testData.getString("fgasRegulations"))) {
            fgasAdminHelper.captureAllFgasUsQuestions(BEFORE);

            fgasAdminHelper.updateAllFgasUsQuestions();

            fgasAdminHelper.captureAllFgasUsQuestions(AFTER);

            outputHelper.takeScreenshots();
            runtimeState.adminRefrigerantGasUsageEditPageV2.save(); // click the save button
        } else {
            fgasAdminHelper.updateAllFgasUkQuestions();
        }
    }

    @ContinueNextStepsOnException
    @Then("^2019 FGas Refrigerant Gas questions completed by the engineer are displayed$")
    public void US_FGas_Refrigerant_Gas_questions_completed_by_the_engineer_are_displayed() throws Throwable {
        fgasAdminHelper.assertSiteVisitGasDetails();
        fgasAdminHelper.assertSiteVisitCylinderDetails();
    }

    @ContinueNextStepsOnException
    @Then("^Leak Inspection and Repair questions (?:completed by the engineer|previously entered) are displayed$")
    public void US_FGas_Leak_Check_and_Repair_questions_completed_by_the_engineer_are_displayed() throws Throwable {
        fgasAdminHelper.assertSiteVisitGasLeakSiteCheck();
    }

    @ContinueNextStepsOnException
    @Then("^UK Regulation questions completed by the engineer are displayed$")
    public void UK_Regulation_questions_completed_by_the_engineer_are_displayed() throws Throwable {
        fgasAdminHelper.assertUkRefrigerantGasQuestions();
    }

    @When("^a Cylinder is deleted$")
    public void a_cylinder_is_deleted() throws Throwable {
        List<String> serialNumbers = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers();
        if (serialNumbers.size() > 0) {
            String serialNumber = serialNumbers.get(0);
            runtimeState.adminRefrigerantGasUsageEditPageV2.deleteCylinder(serialNumber);
            testData.put("deletedCylinder", serialNumber);
        }
        testData.put("numberOfCylinders", serialNumbers.size());
    }

    @When("^the Edit button is clicked for the first Cylinder$")
    public void a_cylinder_is_edited() throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        fgasAdminHelper.captureQuestion(questionHelper, "Refrigerant Type Used");
        fgasAdminHelper.captureQuestion(questionHelper, "New Refrigerant Type Used");

        List<String> serialNumbers = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers();
        if (serialNumbers.size() > 0) {
            String serialNumber = serialNumbers.get(0);
            runtimeState.adminRefrigerantSourceModal = runtimeState.adminRefrigerantGasUsageEditPageV2.editCylinder(serialNumber);
            testData.put("cylinder", serialNumber);
        }
    }

    @When("^the Edit button is clicked for the \"([^\"]*)\" partial Cylinder$")
    public void the_Edit_button_is_clicked_for_the_Cylinder(String qty) throws Throwable {
        String expected = qty.equals("Fully Used") ? "Yes" : "No";

        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        fgasAdminHelper.captureQuestion(questionHelper, "Refrigerant Type Used");
        fgasAdminHelper.captureQuestion(questionHelper, "New Refrigerant Type Used");

        List<String> cylindersFullPartial = runtimeState.adminRefrigerantGasUsageEditPageV2.getFullPartial();
        List<String> serialNumbers = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers();
        for (int i = 1; i <= serialNumbers.size(); i++) {
            if ("PARTIAL".equalsIgnoreCase(cylindersFullPartial.get(i-1))) {
                runtimeState.adminRefrigerantSourceModal = runtimeState.adminRefrigerantGasUsageEditPageV2.editCylinder(i);
                questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();

                if (questionHelper.isQuestionVisible("Fully Used") && questionHelper.getAnswer("Fully Used").equals(expected)) {
                    testData.put("cylinder", serialNumbers.get(i-1));
                    break;
                }
                runtimeState.adminRefrigerantSourceModal.cancel();
            }
        }
    }

    @When("^a Cylinder is edited and the updates are cancelled$")
    public void a_Cylinder_is_edited_and_the_updates_are_cancelled() throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();

        fgasAdminHelper.captureRefrigerantDetails(questionHelper, BEFORE);

        a_cylinder_is_edited();
        fgasAdminHelper.updateCylinder(runtimeState.adminRefrigerantSourceModal.getQuestionHelper());
        runtimeState.adminRefrigerantSourceModal.cancel();

        fgasAdminHelper.captureRefrigerantDetails(questionHelper, AFTER);
    }

    @ContinueNextStepsOnException
    @Then("the Cylinder has not been (?:updated|added)")
    public void the_Cylinder_has_not_been_updated() {
        List<String> beforeCylinderSerialNumbers = testData.getList(BEFORE + "cylinderSerialNumbers", String.class);
        List<String> beforeCylinderSources = testData.getList(BEFORE + "cylinderSources", String.class);
        List<String> beforeCylinderFullPartial = testData.getList(BEFORE + "cylinderFullPartial", String.class);
        List<String> beforeCylinderTypes = testData.getList(BEFORE + "cylinderTypes", String.class);
        List<String> beforeCylinderGasAvailable = testData.getList(BEFORE + "cylinderGasAvailable", String.class);
        List<String> beforeCylinderGasInstalled = testData.getList(BEFORE + "cylinderGasInstalled", String.class);
        List<String> beforeCylinderSurplus = testData.getList(BEFORE + "cylinderSurplus", String.class);
        List<String> beforeCylinderDestinations = testData.getList(BEFORE + "cylinderDestinations", String.class);
        List<String> beforeCylinderSurplusTypes = testData.getList(BEFORE + "cylinderSurplusTypes", String.class);

        List<String> afterCylinderSerialNumbers = testData.getList(AFTER + "cylinderSerialNumbers", String.class);
        List<String> afterCylinderSources = testData.getList(AFTER + "cylinderSources", String.class);
        List<String> afterCylinderFullPartial = testData.getList(AFTER + "cylinderFullPartial", String.class);
        List<String> afterCylinderTypes = testData.getList(AFTER + "cylinderTypes", String.class);
        List<String> afterCylinderGasAvailable = testData.getList(AFTER + "cylinderGasAvailable", String.class);
        List<String> afterCylinderGasInstalled = testData.getList(AFTER + "cylinderGasInstalled", String.class);
        List<String> afterCylinderSurplus = testData.getList(AFTER + "cylinderSurplus", String.class);
        List<String> afterCylinderDestinations = testData.getList(AFTER + "cylinderDestinations", String.class);
        List<String> afterCylinderSurplusTypes = testData.getList(AFTER + "cylinderSurplusTypes", String.class);

        assertTrue("Expected Serial Numbers: " + beforeCylinderSerialNumbers.toString(), beforeCylinderSerialNumbers.containsAll(afterCylinderSerialNumbers));
        assertTrue("Expected Sources: " + beforeCylinderSources.toString(), beforeCylinderSources.containsAll(afterCylinderSources));
        assertTrue("Expected Full/Partial: " + beforeCylinderFullPartial.toString(), beforeCylinderFullPartial.containsAll(afterCylinderFullPartial));
        assertTrue("Expected Cylinder Types: " + beforeCylinderTypes.toString(), beforeCylinderTypes.containsAll(afterCylinderTypes));
        assertTrue("Expected Gas Available: " + beforeCylinderGasAvailable.toString(), beforeCylinderGasAvailable.containsAll(afterCylinderGasAvailable));
        assertTrue("Expected Gas Installed: " + beforeCylinderGasInstalled.toString(), beforeCylinderGasInstalled.containsAll(afterCylinderGasInstalled));
        assertTrue("Expected Surplus: " + beforeCylinderSurplus.toString(), beforeCylinderSurplus.containsAll(afterCylinderSurplus));
        assertTrue("Expected Destinations: " + beforeCylinderDestinations.toString(), beforeCylinderDestinations.containsAll(afterCylinderDestinations));
        assertTrue("Expected Surplus Types: " + beforeCylinderSurplusTypes.toString(), beforeCylinderSurplusTypes.containsAll(afterCylinderSurplusTypes));
    }

    @When("^a new Cylinder is added$")
    public void a_new_cylinder_is_added() throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        if(questionHelper.isQuestionVisible("Appliance Identification") && questionHelper.getAnswer("Appliance Identification").equals("Please select an appliance identification")) {
            fgasAdminHelper.updateApplianceInformationSection(questionHelper);
        }

        fgasAdminHelper.addCylinder();
    }

    @When("^a new Cylinder is added then cancelled$")
    public void a_new_cylinder_is_added_then_cancelled() throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();

        fgasAdminHelper.captureRefrigerantDetails(questionHelper, BEFORE);
        fgasAdminHelper.captureQuestion(questionHelper, "Refrigerant Type Used");
        fgasAdminHelper.captureQuestion(questionHelper, "New Refrigerant Type Used");

        runtimeState.adminRefrigerantSourceModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addCylinder();
        fgasAdminHelper.updateCylinder(runtimeState.adminRefrigerantSourceModal.getQuestionHelper());
        runtimeState.adminRefrigerantSourceModal.cancel();

        fgasAdminHelper.captureRefrigerantDetails(questionHelper, AFTER);
    }

    @ContinueNextStepsOnException
    @Then("^the Cylinder details are deleted$")
    public void the_Cylinder_details_are_deleted() throws Throwable {
        List<String> serialNumbers = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers();
        if (testData.getInt("numberOfCylinders") > 0) {
            assertTrue("Cylinder has not been deleted: " + testData.getString("deletedCylinder"), serialNumbers.size() == testData.getInt("numberOfCylinders") - 1);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Cylinder details are not deleted$")
    public void the_Cylinder_details_are_not_deleted() throws Throwable {
        List<String> serialNumbers = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers();
        assertTrue("Unexpected number of cylinders displayed", serialNumbers.size() == testData.getInt("numberOfCylinders"));
    }

    @ContinueNextStepsOnException
    @Then("^Refrigerant Gas Usage question \"([^\"]*)\" is read only$")
    public void Refrigerant_Gas_Usage_question_is_read_only(String question) {
        QuestionHelper questionHelper;
        if (runtimeState.adminRefrigerantGasUsageEditPageV2 != null) {
            questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        } else {
            questionHelper = runtimeState.updateJobPage.getQuestionHelper();
        }
        runtimeState.scenario.write("Asserting question is READ-ONLY: " + question);
        assertTrue("Expected quetion to be read-only: " + question, questionHelper.isAnswerReadOnly(question));
    }

    @ContinueNextStepsOnException
    @Then("^Appliance Details are read only$")
    public void Appliance_Details_are_read_only() {
        Refrigerant_Gas_Usage_question_is_read_only("Appliance Type");
        Refrigerant_Gas_Usage_question_is_read_only("Appliance Identification");
        Refrigerant_Gas_Usage_question_is_read_only("Has receiver level been recorded?");
        Refrigerant_Gas_Usage_question_is_read_only("Refrigerant Type Used");
    }

    @ContinueNextStepsOnException
    @Then("^all answers except Appliance Type can be updated in the Appliance Information section$")
    public void all_answers_except_Appliance_Type_can_be_updated_in_the_Appliance_Information_section() throws Throwable {
        List<String> sectionQuestions = FgasQuestions.getGasQuestionsForSection("FGAS Appliance");
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        for (String question : questionHelper.getAllQuestions()) {
            if (sectionQuestions.contains(question)) {
                if ("Appliance Type".equals(question)) {
                    runtimeState.scenario.write("Asserting answer to question is read-only: " + question);
                    assertTrue("Expected answer to be read-only: " + question, questionHelper.isAnswerReadOnly(question));

                } else {
                    runtimeState.scenario.write("Asserting answer to question is editable: " + question);
                    assertFalse("Expected answer to be editable: " + question, questionHelper.isAnswerReadOnly(question));
                }
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^only the following Refrigerant Source fields can be updated: Cylinder Serial No \\(full and partial\\), Gas Installed \\(partial only\\)$")
    public void only_the_following_fields_can_be_updated_serial_number_full_and_partial_lbs_in_cylinder_partial_only() throws Throwable {
        List<String> sectionQuestions = FgasQuestions.getGasQuestionsForSection("Refrigerant Source");
        QuestionHelper questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();
        for (String question : questionHelper.getAllQuestions()) {
            if (sectionQuestions.contains(question)) {
                if ("Cylinder Serial No".equals(question) || "Gas Installed".equals(question)) {
                    runtimeState.scenario.write("Asserting answer to question is editable: " + question);
                    assertFalse("Expected answer to be editable: " + question, questionHelper.isAnswerReadOnly(question));

                } else {
                    runtimeState.scenario.write("Asserting answer to question is read-only: " + question);
                    assertTrue("Expected answer to be read-only: " + question, questionHelper.isAnswerReadOnly(question));
                }
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the gas usage \"([^\"]*)\" is updated$")
    public void the_gas_usage_is_updated(String question) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        String originalAnswer = questionHelper.getAnswer(question);
        boolean updated = false;
        for (int i = 0; i < 5; i++) {
            String newAnswer = questionHelper.updateAnswer(question);
            if (!newAnswer.equals(originalAnswer)) {
                updated = true;
                break;
            }
        }
        assertTrue("Question has not been updated: " + question, updated);
    }

    @ContinueNextStepsOnException
    @Then("^the gas usage Appliance Identification is updated so that a change in Refrigerant Type will be required$")
    public void the_gas_usage_Appliance_Identification_is_updated_so_that_a_change_in_Refrigerant_Type_will_be_required() throws Throwable {
        outputHelper.takeScreenshots();
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        String answer = questionHelper.getAnswer("Appliance Identification");

        if ("Appliance not on list".equals(answer)) {
            Optional<String> newAnswer = questionHelper.getDropdownOptions("Appliance Identification")
                    .stream()
                    .filter(option -> !option.equals(answer))
                    .findAny();

            questionHelper.updateAnswer("Appliance Identification", newAnswer.get());
        } else {
            questionHelper.updateAnswer("Appliance Identification", "Appliance not on list");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the gas usage \"([^\"]*)\" is updated to \"([^\"]*)\"$")
    public void the_gas_usage_is_updated_to(String question, String answer) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        questionHelper.updateAnswer(question, answer);
    }

    @ContinueNextStepsOnException
    @Then("^Refrigerant Type Used is updated to \"([^\"]*)\"$")
    public void Refrigerant_Type_Used_is_updated_to(String answer) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        questionHelper.updateAnswer("Refrigerant Type Used", answer);

        if (runtimeState.adminRefrigerantGasUsageEditPageV2.isAlertVisible()) {
            runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
            runtimeState.popupAlert.ok();
        }
    }

    @ContinueNextStepsOnException
    @Then("^Appliance Identification is updated to \"([^\"]*)\"$")
    public void Appliance_Identification_is_updated_to(String answer) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        questionHelper.updateAnswer("Appliance Identification", answer);

        if (runtimeState.adminRefrigerantGasUsageEditPageV2.isAlertVisible()) {
            runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
            runtimeState.popupAlert.ok();
        }
    }

    @ContinueNextStepsOnException
    @Then("^Appliance Identification is not \"([^\"]*)\"$")
    public void appliance_Identification_is_not(String answer) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        String origAnswer = questionHelper.getAnswer("Appliance Identification");

        if (answer.equals(origAnswer)) {

            List<String> options = questionHelper.getDropdownOptions("Appliance Identification");

            if (options.size() > 2) {
                Optional<String> newAnswer = questionHelper.getDropdownOptions("Appliance Identification")
                        .stream()
                        .filter(option -> !option.equals(answer))
                        .findAny();

                questionHelper.updateAnswer("Appliance Identification", newAnswer.get());
            }
        }

        if (runtimeState.adminRefrigerantGasUsageEditPageV2.isAlertVisible()) {
            runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
            runtimeState.popupAlert.ok();
        }
    }

    @ContinueNextStepsOnException
    @Then("^Appliance Identification is updated to a valid alternative$")
    public void appliance_Identification_is_updated_to_valid_alternative() throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        String answer = questionHelper.getAnswer("Appliance Identification");
        appliance_Identification_is_not(answer);
    }

    @ContinueNextStepsOnException
    @Then("^the Refrigerant Source and Refrigerant Installed sections ((?:are|are not)) cleared$")
    public void the_Refrigerant_Source_and_Refrigerant_Installed_sections_are_cleared(String cleared) throws Throwable {
        boolean expected = "are".equals(cleared) ? false : true
                ;
        assertTrue("Refrigerant Source is displayed", runtimeState.adminRefrigerantGasUsageEditPageV2.isSubSectionDispayed("Refrigerant Source") == expected);
        assertTrue("Refrigerant Installed is displayed", runtimeState.adminRefrigerantGasUsageEditPageV2.isSubSectionDispayed("Refrigerant Installed") == expected);
    }

    @ContinueNextStepsOnException
    @Then("^the Refrigerant Installed section is cleared$")
    public void the_Refrigerant_Installed_section_is_cleared() throws Throwable {
        assertFalse("Refrigerant Installed is displayed", runtimeState.adminRefrigerantSourceModal.isRefrigerantInstalledDispayed());
    }

    @ContinueNextStepsOnException
    @Then("^the Refrigerant Source is updated$")
    public void the_Refrigerant_Source_is_updated() throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();
        String originalAnswer = questionHelper.getAnswer("Refrigerant Source");
        boolean updated = false;
        for (int i = 0; i < 5; i++) {
            String newAnswer = questionHelper.updateAnswer("Refrigerant Source");
            if (!newAnswer.equals(originalAnswer)) {
                updated = true;
                break;
            }
        }
        assertTrue("Question has not been updated: Refrigerant Source", updated);
    }

    @ContinueNextStepsOnException
    @Then("^alerts are displayed for unanswered Refrigerant Source questions$")
    public void alerts_are_displayed_for_unanswered_Refrigerant_Source_questions() throws Throwable {
        assertTrue("Alerts not displayed", runtimeState.adminRefrigerantSourceModal.getErrors().size() > 0);
    }

    @ContinueNextStepsOnException
    @Then("^the Refrigerant Details now display the new cylinder details$")
    public void the_Refrigerant_Details_now_display_the_new_cylinder_details() throws Throwable {
        Map<String, Object> questions = testData.getMap("Refrigerant Source");

        int index = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers().size() - 1;

        String question = "Cylinder Serial No";
        fgasAdminHelper.assertAnswer(question, (String) questions.get(question), runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers().get(index));

        question = "Refrigerant Source";
        fgasAdminHelper.assertAnswer(question, (String) questions.get(question), runtimeState.adminRefrigerantGasUsageEditPageV2.getSources().get(index));

        question = "Full or Partial Cylinder";
        fgasAdminHelper.assertAnswer(question, (String) questions.get(question), runtimeState.adminRefrigerantGasUsageEditPageV2.getFullPartial().get(index));

        question = "Type of Cylinder";
        fgasAdminHelper.assertAnswer(question, String.valueOf(questions.get(question)), runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderTypes().get(index));

        question = setWeightLabel("Gas Available (%s)");
        fgasAdminHelper.assertAnswerDouble(question, String.valueOf(questions.get(question)), runtimeState.adminRefrigerantGasUsageEditPageV2.getGasAvailable().get(index));

        question = setWeightLabel("Gas Installed (%s)");
        fgasAdminHelper.assertAnswerDouble(question, String.valueOf(questions.get(question)), runtimeState.adminRefrigerantGasUsageEditPageV2.getGasInstalled().get(index));

        question = "Surplus";
        fgasAdminHelper.assertAnswerDouble(question, String.valueOf(questions.get(question)), runtimeState.adminRefrigerantGasUsageEditPageV2.getSurplus().get(index));
    }

    @When("^updates are made resulting in unused gas within the cylinder$")
    public void updates_are_made_resulting_in_unused_gas_within_the_cylinder() throws Throwable {
        runtimeState.adminRefrigerantSourceModal.setFullyUsed(false);

        String label = setWeightLabel("Gas in Cylinder (%s)");
        if (runtimeState.adminRefrigerantSourceModal.getQuestionHelper().isQuestionVisible(label)) {
            QuestionHelper questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();
            String maxValue = questionHelper.getAnswer(setWeightLabel("Cylinder Capacity (%s)"));
            questionHelper.updateAnswer(label, maxValue);
        }
        lbs_Installed_is_entered();
    }

    @When("^Fully Used is \"([^\"]*)\"$")
    public void fully_Used_is(String checked) throws Throwable {
        boolean current =  runtimeState.adminRefrigerantSourceModal.isFullyUsed();
        boolean expected = "checked".equals(checked);
        if (current == expected) {
            runtimeState.adminRefrigerantSourceModal.setFullyUsed(!expected);
        }
        runtimeState.adminRefrigerantSourceModal.setFullyUsed(expected);
    }

    @When("^Gas in Cylinder is updated to be greater than a cylinders capacity$")
    public void Pounds_in_Cylinder_is_updated_to_be_greater_than_a_cylinders_capacity() {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();

        if (!getWebDriver().getCurrentUrl().contains("usad")) {
            if ( !questionHelper.getAnswer("Type of Cylinder").equals("Deposit") ) {
                questionHelper.updateAnswer("Refrigerant Source", "On Site Inventory");
                questionHelper.updateAnswer("Full or Partial Cylinder", "Partial");
                questionHelper.updateAnswer("Type of Cylinder", "Deposit");
            }
        }

        String label = setWeightLabel("Cylinder Capacity (%s)");
        float maxCapacity = Float.valueOf(questionHelper.updateAnswer(label));
        testData.put("maxCapacity", maxCapacity);
        runtimeState.scenario.write("Max cylinder capacity: " + maxCapacity);
        int newCapacity = Math.round(maxCapacity) + 1;
        runtimeState.scenario.write("Updating Gas in Cylinder to: " + newCapacity);

        label = setWeightLabel("Gas in Cylinder (%s)");
        questionHelper.updateAnswer(label, String.valueOf(newCapacity));
    }

    @When("^Gas in Cylinder is updated to be less than Gas Installed$")
    public void Pounds_in_Cylinder_is_updated_to_be_less_than_Lbs_Installed() {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();
        String label = setWeightLabel("Gas Installed (%s)");
        Double gasInstalled = Double.valueOf(questionHelper.getAnswer(label));
        Double gasInCylinder = gasInstalled.compareTo(floor(gasInstalled)) == 0 ? floor(gasInstalled - 1) : floor(gasInstalled);
        outputHelper.takeScreenshots();
        runtimeState.scenario.write("updating Gas in Cylinder to: " + gasInCylinder);

        label = setWeightLabel("Gas in Cylinder (%s)");
        questionHelper.updateAnswer(label, trimZeros(String.valueOf(gasInCylinder)));
    }

    @ContinueNextStepsOnException
    @Then("^Gas Installed is updated to equal the Gas in Cylinder$")
    public void lbs_Installed_is_updated_to_equal_the_Pounds_in_Cylinder() throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();

        String label = setWeightLabel("Gas in Cylinder (%s)");
        int gasInCylinder = Integer.valueOf(questionHelper.getAnswer(label));

        label = setWeightLabel("Gas Installed (%s)");
        int gasInstalled = Integer.valueOf(questionHelper.getAnswer(label));
        assertTrue("Expected Gas in Cylinder (" +  gasInCylinder + ") == Gas Installed (" + gasInstalled + ")", gasInCylinder == gasInstalled);
    }

    @ContinueNextStepsOnException
    @Then("^Gas Installed is updated to match Gas Available$")
    public void lbs_Installed_is_updated_to_match_Lbs_Available() throws Throwable {
        Double gasAvailable = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasAvailable());
        Double gasInstalled = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasInstalled());
        assertTrue("Expected Gas Available (" +  gasAvailable + ") == Gas Installed (" + gasInstalled + ")", gasAvailable.equals(gasInstalled));
    }

    @When("^Gas Installed is entered$")
    public void lbs_Installed_is_entered() throws Throwable {
        Double gasAvailable = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasAvailable());
        Double gasInstalled = gasAvailable - 1;
        runtimeState.adminRefrigerantSourceModal.enterGasInstalled(gasInstalled);
    }

    @When("^Gas Installed is entered to exceed Gas Available$")
    public void lbs_Installed_is_entered_to_exceed_lbs_available() throws Throwable {
        Double gasAvailable = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasAvailable());
        runtimeState.adminRefrigerantSourceModal.enterGasInstalled(gasAvailable + 1);
    }

    @When("^Gas Installed is set to \"([^\"]*)\"$")
    public void Lbs_Installed_is_set_to(Double gasAvailable) throws Throwable {
        runtimeState.adminRefrigerantSourceModal.enterGasInstalled(gasAvailable);
    }

    @ContinueNextStepsOnException
    @Then("^the Surplus is displayed$")
    public void the_Surplus_is_displayed() throws Throwable {
        Double gasAvailable = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasAvailable());
        Double gasInstalled = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasInstalled());
        Double surplus = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getSurplus());
        runtimeState.scenario.write("Asserting gasAvailable (" + gasAvailable + ") - gasInstalled (" + gasInstalled + ") = surplus (" + surplus + ")");
        Double gasDiff = gasAvailable - gasInstalled;
        assertTrue("Expected gasAvailable (" + gasAvailable + ") - gasInstalled (" + gasInstalled + ") = surplus (" + surplus + ")", gasDiff.equals(surplus));
    }

    @ContinueNextStepsOnException
    @Then("^FGAS question \"([^\"]*)\" is mandatory$")
    public void FGAS_question_is_mandatory(String question) throws Throwable {
        QuestionHelper questionHelper;
        if (getWebDriver().getCurrentUrl().contains("Portal")) {
            questionHelper = runtimeState.updateJobPage.getQuestionHelper();
            questionHelper.updateAnswer(question, "");

            runtimeState.updateJobPage.updateJob();
            assertTrue("Expected Edit Gas Usage page to be displayed", runtimeState.updateJobPage.isDisplayed());

        } else {
            questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
            questionHelper.updateAnswer(question, "");

            runtimeState.adminRefrigerantGasUsageEditPageV2.save();
            runtimeState.adminRefrigerantGasUsageEditPageV2.takeScreenshotRefrigerantInstalledForm(outputHelper);

            assertTrue("Expected Edit Gas Usage page to be displayed", runtimeState.adminRefrigerantGasUsageEditPageV2.isDisplayed());
        }

        String warning = "";
        switch(question) {
        case "Asset Condition":
            warning = "Please select an asset condition from the list";
            break;

        case "Leak Check Method":
            warning = "Please select the Leak Check Method";
            break;

        case "Leak Check Result Type":
            warning = "Please select the Leak Check Result Type";
            break;

        case "Please enter appliance maximum charge":
            warning = "Please enter appliance maximum charge";
            break;

        case "Please describe works carried out and any parts fitted":
            warning = "Details are required";
            break;

        case "Please provide notes to explain reason for changing appliance maximum charge":
            warning = "Please enter appliance maximum charge";
            assertTrue("Expected warning: Maximum charge notes must be at least 20 characters", runtimeState.loginPage.getAlerts().contains("Maximum charge notes must be at least 20 characters"));
            break;

        case "Reason for Returning":
            warning = "Please select a reason for returning";
            break;

        case "Root cause":
            // only displays if 'Root cause category' selected
            runtimeState.updateJobPage.selectRandomRootCauseCategory();
            runtimeState.updateJobPage.updateJob();
            warning = "Root cause is required when completing a job";
            break;

        case "Root cause category":
            warning = "Root cause category is required when completing a job";
            break;
        }
        assertTrue("Expected warning: " + warning, runtimeState.loginPage.getAlerts().contains(warning));
    }

    @ContinueNextStepsOnException
    @Then("^FGAS question \"([^\"]*)\" is requested$")
    public void FGAS_question_is_requested(String question) throws Throwable {
        QuestionHelper questionHelper;
        if (getWebDriver().getCurrentUrl().contains("Portal")) {
            questionHelper = runtimeState.updateJobPage.getQuestionHelper();
        } else {
            questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        }

        assertTrue("Not displayed: " + question, questionHelper.isQuestionVisible(question));
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" (?:is|are) requested$")
    public void appliance_details_are_requested(String question) throws Throwable {
        assertTrue(question + "is not requested", runtimeState.adminRefrigerantGasUsageEditPageV2.isQuestionVisible(question));
    }

    @ContinueNextStepsOnException
    @Then("^either \"Quantity of Floating Balls\" or \"Level Indicator %\" is mandatory$")
    public void either_Quantity_of_Floating_Balls_or_Level_Indicator_is_mandatory() throws Throwable {
        appliance_details_are_requested("Quantity of Balls Floating");
        appliance_details_are_requested("Provide Level Indicator %");

        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        String origNumBalls = questionHelper.getAnswer("Quantity of Balls Floating");
        String origLevel = questionHelper.getAnswer("Provide Level Indicator %");

        questionHelper.updateAnswer("Quantity of Balls Floating", "");
        questionHelper.updateAnswer("Provide Level Indicator %", "");
        runtimeState.adminRefrigerantGasUsageEditPageV2.save();

        runtimeState.adminRefrigerantGasUsageEditPageV2.takeScreenshotApplianceInformationForm(outputHelper);
        String expected = "Please enter quantity of floating balls OR level indicator %";
        assertTrue("Expected: " + expected, runtimeState.adminRefrigerantGasUsageEditPageV2.getAlerts().contains(expected));

        // undo changes
        questionHelper.updateAnswer("Quantity of Balls Floating", origNumBalls);
        questionHelper.updateAnswer("Provide Level Indicator %", origLevel);
    }

    @ContinueNextStepsOnException
    @Then("^Quantity of Balls Floating must be a whole number between \"([^\"]*)\" and \"([^\"]*)\"$")
    public void Quantity_of_Balls_Floating_mus_be_a_whole_number_between(Integer from, Integer to) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();

        assertTrue("Quantity of Balls Floating is not requested", questionHelper.isQuestionVisible("Quantity of Balls Floating"));

        String rangeWarning = "Please enter a number between " + from + " and " + to;
        String wholeNumWarning = "Please enter a whole number";

        // set to less than min allowed
        questionHelper.updateAnswer("Quantity of Balls Floating", String.valueOf(from - 1));
        questionHelper.tab();
        runtimeState.adminRefrigerantGasUsageEditPageV2.takeScreenshotApplianceInformationForm(outputHelper);
        assertTrue("Expected: " + rangeWarning, runtimeState.adminRefrigerantGasUsageEditPageV2.getAlerts().contains(rangeWarning));

        // set to more than max allowed
        questionHelper.updateAnswer("Quantity of Balls Floating", String.valueOf(to + 1));
        questionHelper.tab();
        runtimeState.adminRefrigerantGasUsageEditPageV2.takeScreenshotApplianceInformationForm(outputHelper);
        assertTrue("Expected: " + rangeWarning, runtimeState.adminRefrigerantGasUsageEditPageV2.getAlerts().contains(rangeWarning));

        // set to decimal
        questionHelper.updateAnswer("Quantity of Balls Floating", String.valueOf(RandomUtils.nextDouble(from, to)));
        questionHelper.tab();
        runtimeState.adminRefrigerantGasUsageEditPageV2.takeScreenshotApplianceInformationForm(outputHelper);
        assertTrue("Expected: " + wholeNumWarning, runtimeState.adminRefrigerantGasUsageEditPageV2.getErrors().contains(wholeNumWarning));

        // set to min allowed
        questionHelper.updateAnswer("Quantity of Balls Floating", String.valueOf(from));
        questionHelper.tab();
        runtimeState.adminRefrigerantGasUsageEditPageV2.takeScreenshotApplianceInformationForm(outputHelper);
        assertFalse("Not expected: " + rangeWarning, runtimeState.loginPage.getAlerts().contains(rangeWarning));
        assertFalse("Not expected: " + wholeNumWarning, runtimeState.loginPage.getErrors().contains(wholeNumWarning));

        // set to max allowed
        questionHelper.updateAnswer("Quantity of Balls Floating", String.valueOf(to));
        questionHelper.tab();
        runtimeState.adminRefrigerantGasUsageEditPageV2.takeScreenshotApplianceInformationForm(outputHelper);
        assertFalse("Not expected: " + rangeWarning, runtimeState.loginPage.getAlerts().contains(rangeWarning));
        assertFalse("Not expected: " + wholeNumWarning, runtimeState.loginPage.getErrors().contains(wholeNumWarning));
    }

    @ContinueNextStepsOnException
    @Then("^Returned To has a minimum field length of \"([^\"]*)\"$")
    public void Return_To_has_a_minimum_field_length(Integer minAllowed) throws Throwable {
        QuestionHelper questionHelper =  runtimeState.adminRefrigerantSourceModal.getQuestionHelper();
        if (!getWebDriver().getCurrentUrl().contains("usad")) {
            questionHelper.updateAnswer("Refrigerant Source", "Recovered Gas");
        }
        questionHelper.updateAnswer("Refrigerant Source Location");
        questionHelper.updateAnswer("Full or Partial Cylinder", "Full");
        if (getWebDriver().getCurrentUrl().contains("usad")) {
            questionHelper.updateAnswer("Type of Cylinder", "Disposable");
        } else {
            questionHelper.updateAnswer("Type of Cylinder", "Deposit");
        }
        questionHelper.updateAnswer("Cylinder Serial No");

        String label = setWeightLabel("Cylinder Capacity (%s)");
        questionHelper.updateAnswer(label);

        questionHelper.updateAnswer("Fully Used", "No");
        lbs_Installed_is_entered();
        List<String> options = questionHelper.getDropdownOptions("Destination");
        String answer = options.contains("Returned to Supplier") ? "Returned to Supplier" : "Returned to Source";
        questionHelper.updateAnswer("Destination", answer);
        questionHelper.updateAnswer("Surplus Type");

        answer = randomAscii(minAllowed - 1);
        questionHelper.updateAnswer("Returned To", answer);
        String expectedWarning = "Details entered must be at least ";
        outputHelper.takeScreenshots();
        runtimeState.scenario.write("Asserting Returned To text (" + answer.length() + " chars) displays warning: " + answer);
        assertTrue("Expected: " + expectedWarning + minAllowed + " characters", runtimeState.adminRefrigerantSourceModal.getErrors().contains(expectedWarning + minAllowed + " characters"));

        answer = randomAscii(minAllowed);
        questionHelper.updateAnswer("Returned To", answer);
        runtimeState.scenario.write("Asserting Returned To text (" + answer.length() + " chars) does not display warning: " + answer);
        assertFalse("Not Expected: " + expectedWarning, runtimeState.adminRefrigerantSourceModal.getErrors().toString().contains(expectedWarning));
    }

    @ContinueNextStepsOnException
    @Then("^Primary Component Information is requested with a minimum field length \"([^\"]*)\"$")
    public void Primary_Component_Information_is_requested_with_a_minimum_field_length(Integer minAllowed) throws Throwable {
        String testString = RandomStringUtils.randomAscii(minAllowed - 1);
        runtimeState.scenario.write("Asserting " + (minAllowed - 1) + " characters displays warning: '" + testString + "'");
        runtimeState.adminLeakSiteInformationModal.getQuestionHelper().updateAnswer("Primary Component Information", testString);
        String expectedWarning = "Primary Component Information must be at least ";
        outputHelper.takeScreenshots();
        assertTrue("Expected: " + expectedWarning + minAllowed + " characters", runtimeState.adminLeakSiteInformationModal.getErrors().contains(expectedWarning + minAllowed + " characters"));

        testString = RandomStringUtils.randomAscii(minAllowed);
        runtimeState.scenario.write("Asserting " + minAllowed + " characters does NOT display warning: '" + testString + "'");
        runtimeState.adminLeakSiteInformationModal.getQuestionHelper().updateAnswer("Primary Component Information", testString);
        assertFalse("Not Expected: " + expectedWarning, runtimeState.adminLeakSiteInformationModal.getErrors().toString().contains(expectedWarning));
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" (?:is|are) not requested$")
    public void Appliance_Details_are_not_requested(String question) throws Throwable {
        assertFalse(question + "is requested", runtimeState.adminRefrigerantGasUsageEditPageV2.isQuestionVisible(question));
    }

    @ContinueNextStepsOnException
    @Then("^Refrigerant Source \"([^\"]*)\" ((?:is|is not)) requested$")
    public void Refrigerant_Source_are_not_requested(String question, String requested) throws Throwable {
        boolean expected = "is not".equals(requested) ? false : true;
        QuestionHelper questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();

        if (questionHelper.getAnswer("Type of Cylinder").equals("Please select a gas cylinder type")) {
            String cylinderType = fgasAdminHelper.getRandomCylinderType();
            questionHelper.updateAnswer("Type of Cylinder", cylinderType);
        }

        String label = setWeightLabel("Cylinder Capacity (%s)");
        if (questionHelper.getAnswer(label).equals("Please select a gas cylinder capacity")) {
            questionHelper.updateAnswer(label);
        }

        List<String> questionsDisplayed = questionHelper.getAllQuestions();
        if (question.contains("lbs")) {
            question = question.replace("lbs", setWeightLabel("%s"));
        } else {
            question = localize(question);
        }
        assertTrue(question + " is requested: " + questionsDisplayed.contains(question), questionsDisplayed.contains(question) == expected);
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" can be set to \"([^\"]*)\"$")
    public void can_be_set_to(String question, String answer) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        List<String> options = questionHelper.getDropdownOptions(question);
        assertTrue(answer + " not found in dropdown list: " + options, options.contains(answer));
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" can not be set to \"([^\"]*)\"$")
    public void can_not_be_set_to(String question, String answer) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        List<String> options = questionHelper.getDropdownOptions(question);
        runtimeState.scenario.write("Dropdown contains: " + options.toString());
        assertFalse(answer + " found in dropdown list: " + options, options.contains(answer));
    }

    @ContinueNextStepsOnException
    @Then("^Refrigerant Type Used can be set to any active refrigerant type$")
    public void Refrigerant_Type_Used_can_be_set_to_any_active_refrigerant_type() throws Throwable {
        List<String> activeGasTypes = dbHelperGas.getActiveGasTypes();

        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        List<String> options = questionHelper.getDropdownOptions("Refrigerant Type Used");

        runtimeState.scenario.write("Active Gas Types: " + activeGasTypes.toString());
        runtimeState.scenario.write("Dropdown contains: " + options.toString());
        assertTrue("Expected dropdown to contain " + activeGasTypes.toString() + " but actually contained " + options.toString() , options.containsAll(activeGasTypes));
    }

    @When("^a new Fully Used Cylinder is added$")
    public void a_new_fully_used_Cylinder_is_added() throws Throwable {
        testData.put("fullyUsed", true);
        a_new_cylinder_is_added();
    }

    @ContinueNextStepsOnException
    @Then("^Leak Site Check questions? \"([^\"]*)\" ((?:can|can not)) be updated$")
    public void the_Leak_Site_Check_question_can_not_be_updated(String question, String updated) throws Throwable {
        QuestionHelper questionHelper;
        if (getWebDriver().getCurrentUrl().contains("Portal")) {
            questionHelper = runtimeState.adminLeakSiteInformationModal.getQuestionHelper();

        } else {
            if (!runtimeState.adminRefrigerantGasUsageEditPageV2.isModalDisplayed()) {
                if (runtimeState.adminRefrigerantGasUsageEditPageV2.isLeakSiteCheckEditDisplayed()) {
                    runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.editLeakSiteCheck(1);
                }
            }

            questionHelper = runtimeState.adminLeakSiteInformationModal.getQuestionHelper();
        }

        boolean expected = "can not".equals(updated);
        String parts[] = question.split(",");
        for (int i = 0 ; i < parts.length; i++) {
            question = parts[i].trim();
            assertTrue("Expected question to be read-only: " + question, questionHelper.isAnswerReadOnly(question) == expected);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Leak Site Check \"([^\"]*)\" button ((?:is|is not)) displayed$")
    public void the_Leak_Site_Check_button_is_displayed(String button, String displayed) throws Throwable {
        boolean expected = "is not".equals(displayed) ? false : true;

        switch (button) {
        case "Edit":
            assertTrue(runtimeState.adminRefrigerantGasUsageEditPageV2.isLeakSiteCheckEditDisplayed() == expected);
            break;

        case "Delete":
            assertTrue(runtimeState.adminRefrigerantGasUsageEditPageV2.isLeakSiteCheckDeleteDisplayed() == expected);
            break;
        }
    }

    @ContinueNextStepsOnException
    @Then("^a New Refrigerant Type Used is selected$")
    public void a_New_Refrigerant_Type_Used_is_selected() throws Throwable {
        String question = "New Refrigerant Type Used";
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        String originalAnswer = questionHelper.getAnswer(question);
        String newAnswer;
        do {
            newAnswer = questionHelper.updateAnswer(question);
        } while (newAnswer.equals(originalAnswer));
    }

    @ContinueNextStepsOnException
    @Then("^the Total gas used is set to zero$")
    public void the_Total_gas_used_is_set_to_zero() throws Throwable {
        int totalGasUsed = Integer.valueOf(runtimeState.adminRefrigerantGasUsageEditPageV2.getTotalGasUsed().replace(" " + LABEL_WEIGHT_US, "").replace(" " + LABEL_WEIGHT_UK, ""));
        assertTrue(totalGasUsed == 0);
    }

    @When("^it is a \"([^\"]*)\" cylinder$")
    public void it_is_a_cylinder(String cylinderType) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();

        // workaround for old bad data in test databases
        if (questionHelper.getAnswer("Refrigerant Source").equals("Please select a gas source")) {
            questionHelper.updateAnswer("Refrigerant Source",  "On Site Inventory");
        }

        List<String> questionsDisplayed = questionHelper.getAllQuestions();
        if ( !questionsDisplayed.contains("Full or Partial Cylinder") ) {
            questionHelper.updateAnswer("Refrigerant Source", "On Site Inventory");
        }
        questionHelper.updateAnswer("Full or Partial Cylinder", cylinderType);
    }

    @ContinueNextStepsOnException
    @Then("^all Leak Site Check questions can be updated$")
    public void all_Leak_Site_Check_questions_can_be_updated() throws Throwable {
        if (!runtimeState.adminRefrigerantGasUsageEditPageV2.isModalDisplayed()) {
            if (runtimeState.adminRefrigerantGasUsageEditPageV2.isLeakSiteCheckEditDisplayed()) {
                runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.editLeakSiteCheck(1);
            }
        }

        QuestionHelper questionHelper = runtimeState.adminLeakSiteInformationModal.getQuestionHelper();
        for (String question : questionHelper.getAllQuestions()) {
            question = question.replaceAll("\n.*", "");
            assertFalse("Question should be updateable: " + question, questionHelper.isAnswerReadOnly(question));
        }
    }

    @When("^the last Leak Site Check is deleted$")
    public void the_last_Leak_Site_Check_is_deleted() throws Throwable {

        outputHelper.takeScreenshots();
        List<String> primaryComponentsInfo = runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponentsInfo();

        testData.put("primaryComponentsInfo", primaryComponentsInfo);
        runtimeState.popupAlert = runtimeState.adminRefrigerantGasUsageEditPageV2.deleteLastLeakSiteCheck();

        testData.put("deletedPrimaryComponentInfo", primaryComponentsInfo.get(primaryComponentsInfo.size() - 1));
        outputHelper.takeScreenshot();
        runtimeState.popupAlert.ok();

        assert_the_Leak_Site_Check_is_deleted();
    }

    @When("^\"([^\"]*)\" Leak Site Check is cancelled$")
    public void a_Leak_Site_Check_Delete_is_cancelled(String action) throws ParseException {

        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        fgasAdminHelper.captureLeakInspectionsAndRepairs(questionHelper, BEFORE);

        if ("Delete".equals(action)) {
            List<String> primaryComponentsInfo = runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponentsInfo();
            testData.put("deletedPrimaryComponentInfo", primaryComponentsInfo.get(primaryComponentsInfo.size() - 1));

            runtimeState.popupAlert = runtimeState.adminRefrigerantGasUsageEditPageV2.deleteLastLeakSiteCheck();
            outputHelper.takeScreenshots();
            runtimeState.popupAlert.cancel();

        } else {
            if ("Edit".equals(action)) {
                runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.editLeakSiteCheck(1);

            } else if ("Add Additional".equals(action)) {
                runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addAdditionalLeakSiteDetails();
            }

            fgasAdminHelper.updateLeakSiteCheck();
            outputHelper.takeScreenshots();
            runtimeState.adminLeakSiteInformationModal.cancel();
        }

        fgasAdminHelper.captureLeakInspectionsAndRepairs(questionHelper, AFTER);
    }

    @When("^a leak site check is edited$")
    public void a_leak_site_check_is_edited() {
        runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.editLeakSiteCheck(1);
    }

    @When("^the Leak Site Check is deleted$")
    public void assert_the_Leak_Site_Check_is_deleted() throws Throwable {
        List<String> originalPrimaryComponentsInfo = testData.getArray("primaryComponentsInfo");
        List<String> primaryComponentsInfo = runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponentsInfo();
        assertTrue("Leak Site Check not deleted", originalPrimaryComponentsInfo.size()-1 == primaryComponentsInfo.size());
    }

    @When("^the Leak Site Check \"([^\"]*)\" soft deleted from the database$")
    public void the_Leak_Site_Check_is_soft_deleted_from_the_database(String deleted) {
        boolean isDeleted = deleted.equals("is");
        String deletedPrimaryComponentInfo = testData.getString("deletedPrimaryComponentInfo");
        SiteVisits siteVisit = new SiteVisits();
        siteVisit = (SiteVisits) testData.get("siteVisit");
        List<SiteVisitGasLeakSiteCheck> siteVisitGasLeakSiteChecks = siteVisitGasLeakSiteCheckDao.getAllForSiteVisitId(siteVisit.getId());
        boolean found = false;
        for (SiteVisitGasLeakSiteCheck gasLeakSiteCheck : siteVisitGasLeakSiteChecks) {
            if (gasLeakSiteCheck.getPrimaryComponentInformation().equals(deletedPrimaryComponentInfo)) {
                assertFalse("Unexpected state: Leak Site Check", gasLeakSiteCheck.isActive() == isDeleted);
                found = true;
                break;
            }
        }
        assertTrue("Primary Component Info not found in DB: " + deletedPrimaryComponentInfo, found);
    }

    @When("^the Leak Site Checks are not updated$")
    public void the_Leak_Site_Checks_are_not_updated() throws Throwable {
        List<String> beforePrimaryComponents =  testData.getList(BEFORE + "primaryComponents", String.class);
        List<String> beforeSubComponents = testData.getList(BEFORE + "subComponents", String.class);
        List<String> beforePrimaryComponentsInfo = testData.getList(BEFORE + "primaryComponentsInfo", String.class);
        List<String> beforeLeakSiteStatuses = testData.getList(BEFORE + "leakSiteStatuses", String.class);
        List<String> beforeInitialTests = testData.getList(BEFORE + "initialTests", String.class);
        List<String> beforeFollowUpTests = testData.getList(BEFORE + "followUpTests", String.class);

        List<String> afterPrimaryComponents =  testData.getList(AFTER + "primaryComponents", String.class);
        List<String> afterSubComponents = testData.getList(AFTER + "subComponents", String.class);
        List<String> afterPrimaryComponentsInfo = testData.getList(AFTER + "primaryComponentsInfo", String.class);
        List<String> afterLeakSiteStatuses = testData.getList(AFTER + "leakSiteStatuses", String.class);
        List<String> afterInitialTests = testData.getList(AFTER + "initialTests", String.class);
        List<String> afterFollowUpTests = testData.getList(AFTER + "followUpTests", String.class);

        assertTrue("Expected Primary Components: " + beforePrimaryComponents.toString(), beforePrimaryComponents.containsAll(afterPrimaryComponents));
        assertTrue("Expected Sub Components: " + beforeSubComponents.toString(), beforeSubComponents.containsAll(afterSubComponents));
        assertTrue("Expected Primary Components Info: " + beforePrimaryComponentsInfo.toString(), beforePrimaryComponentsInfo.containsAll(afterPrimaryComponentsInfo));
        assertTrue("Expected Leak Site Statuses: " + beforeLeakSiteStatuses.toString(), beforeLeakSiteStatuses.containsAll(afterLeakSiteStatuses));
        assertTrue("Expected Initial Tests: " + beforeInitialTests.toString(), beforeInitialTests.containsAll(afterInitialTests));
        assertTrue("Expected Follow Up Tests: " + beforeFollowUpTests.toString(), beforeFollowUpTests.containsAll(afterFollowUpTests));
    }

    @When("^Add Additional Leak Site Check is selected$")
    public void Add_Additional_Leak_Site_Check_is_selected() throws Throwable {
        runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addAdditionalLeakSiteDetails();
    }

    @When("^an Additional Leak Site Check is added$")
    public void an_Additional_Leak_Site_Check_is_added() throws Throwable {
        runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addAdditionalLeakSiteDetails();
        fgasAdminHelper.enterLeakDetails();
    }

    @ContinueNextStepsOnException
    @Then("^the new Leak Site Check is added to the Refrigerant Leak Inspection & Repair section$")
    public void the_new_Leak_Site_Check_is_added_to_the_Refrigerant_Leak_Inspection_Repair_section() throws Throwable {
        List<String> originalPrimaryComponentsInfo = testData.getArray("primaryComponentsInfo");
        List<String> primaryComponentsInfo = runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponentsInfo();
        if (originalPrimaryComponentsInfo != null) {
            assertTrue("Primary Component Info not added", originalPrimaryComponentsInfo.size() + 1 == primaryComponentsInfo.size());
        }

        List<String> primaryComponents = runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponents();
        List<String> subComponents = runtimeState.adminRefrigerantGasUsageEditPageV2.getSubComponents();
        List<String> statuses = runtimeState.adminRefrigerantGasUsageEditPageV2.getLeakSiteStatuses();
        List<String> initialTests = runtimeState.adminRefrigerantGasUsageEditPageV2.getInitialTests();
        List<String> followUpTests = runtimeState.adminRefrigerantGasUsageEditPageV2.getFollowUpTests();

        fgasAdminHelper.assertAnswer("Primary Component", (String) testData.getMap("questions").get("Primary Component"), primaryComponents.get(primaryComponents.size()-1));
        fgasAdminHelper.assertAnswer("Sub-Component", (String) testData.getMap("questions").get("Sub-Component"), subComponents.get(subComponents.size()-1));
        fgasAdminHelper.assertAnswer("Primary Component Information", (String) testData.getMap("questions").get("Primary Component Information"), primaryComponentsInfo.get(primaryComponentsInfo.size()-1));
        fgasAdminHelper.assertAnswer("Leak Site Status", (String) testData.getMap("questions").get("Leak Site Status"), statuses.get(statuses.size()-1));
        fgasAdminHelper.assertAnswer("Initial Test", (String) testData.getMap("questions").get("Initial Verification Test"), initialTests.get(initialTests.size()-1));
        fgasAdminHelper.assertAnswer("Follow Up Test", (String) testData.getMap("questions").get("Follow Up Verification Test"), followUpTests.get(followUpTests.size()-1));

        if (siteVisitsDao.getSiteVisitWithLeakSiteChecks() == null) {
            // there isn't a site visit with multiple leak site checks in the DB. Save it so we can use for subsequent tests
            outputHelper.takeScreenshot();
            runtimeState.scenario.write("Saving site visit updates");
            runtimeState.adminRefrigerantGasUsageEditPageV2.save();
        }
    }

    @When("^the Add button is selected on the Leak Site Information popup$")
    public void the_Add_button_is_selected_on_the_Leak_Site_Information_popup() throws Throwable {
        runtimeState.adminLeakSiteInformationModal.takeScreenshot(outputHelper);
        runtimeState.adminLeakSiteInformationModal.add();
    }

    @ContinueNextStepsOnException
    @Then("^warnings are displayed for unanswered mandatory Leak Site Information questions$")
    public void warnings_are_displayed_for_unanswered_mandatory_Leak_Site_Information_questions() throws Throwable {
        List<String> errors = runtimeState.adminLeakSiteInformationModal.getErrors();
        runtimeState.scenario.write("Displayed warnings: " + errors.toString());
        assertTrue("Missing error: Primary Component is required", errors.contains("Primary Component is required"));
        assertTrue("Missing error: Primary Component Information is required", errors.contains("Primary Component Information is required"));
        assertTrue("Missing error: Site Status is required", errors.contains("Site Status is required"));
        assertTrue("Missing error: Initial Verification Test is required", errors.contains("Initial Verification Test is required"));
        assertTrue("Missing error: Follow Up Verification Test is required", errors.contains("Follow Up Verification Test is required"));
    }

    @When("^a change is made to the leak site information that would cause the job status to be updated to returning$")
    public void a_change_is_made_to_the_leak_site_information_that_would_cause_the_job_status_to_be_updated_to_returning() throws Throwable {
        outputHelper.takeScreenshots();
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();

        List<String> options = questionHelper.getDropdownOptions("Leak Check Status");
        if (options.contains("Partial leak check performed")) {
            questionHelper.updateAnswer("Leak Check Status", "Partial leak check performed");
        } else {
            questionHelper.updateAnswer("Leak Check Result Type", "Inconclusive - Follow-up required");
        }
    }

    @ContinueNextStepsOnException
    @Then("^a warning is displayed stating the user shall need to contact the Helpdesk to create a new job$")
    public void a_warning_is_displayed_stating_the_user_shall_need_to_contact_the_Helpdesk_to_create_a_new_job() throws Throwable {
        String error = "As you have made a change to the refrigerant usage/leak site information that would have resulted in this job moving to a returning status, you will need to contact the Helpdesk to re-open this job and assign to the resource who entered the refrigerant gas. Please ensure job details are updated to inform the technician/vendor that this job is being reassigned to them due to a refrigerant gas/leak site test not being complete on the previous visit.";
        runtimeState.scenario.write("Asserting presence of error: " + error);
        assertTrue("Expected: " + error, runtimeState.adminRefrigerantGasUsageEditPageV2.getErrors().contains(error));
    }

    @ContinueNextStepsOnException
    @Then("^the Refrigerant Gas Audit History has been updated$")
    public void the_Audit_History_has_been_updated() throws Exception {
        runtimeState.adminRefrigerantGasUsageSearchPage.enterJobReference(testData.getInt("jobReference"));
        runtimeState.adminRefrigerantGasUsageEditPage = runtimeState.adminRefrigerantGasUsageSearchPage.search();
        if (runtimeState.adminRefrigerantGasUsageSearchPage.multipleVisitsDisplayed()) {
            outputHelper.takeScreenshots();
            runtimeState.adminRefrigerantGasUsageSearchPage.viewVisit(testData.getString("workEndTime"));
        }

        if ("2019".equals(testData.getString("fgasRegulations"))) {
            fgasAdminHelper.assertAuditHistoryUsGas();

        } else {
            fgasAdminHelper.assertAuditHistoryUkGas();
        }
    }

    @ContinueNextStepsOnException
    @Then("^a timeline \"([^\"]*)\" event is created in the database with the updated FGAS questions and answers$")
    public void the_timeline_displays_an_event_with_the_updated_FGAS_questions_and_answers(String event) throws Throwable {
        int jobReference = testData.getInt("jobReference");
        JobTimelineEvent timelineEvent = jobTimelineEventDao.getLatest(jobReference, event);

        runtimeState.scenario.write("Asserting content: " + timelineEvent.getDetail1());

        Map<String, Object> afterMap = testData.getMap(AFTER + "questions");
        for (String title : afterMap.keySet()) {
            if (afterMap.get(title) != null) {
                switch (title) {
                case "Refrigerant Type Used":
                    if ("Incorrect refrigerant type displayed".equals(afterMap.get(title))) {
                        assertGasTimelineDetail(timelineEvent.getDetail1(), "Refrigerant Type Used", (String) afterMap.get("New Refrigerant Type Used"));
                    } else {
                        assertGasTimelineDetail(timelineEvent.getDetail1(), "Refrigerant Type Used", (String) afterMap.get(title));
                    }
                    break;

                case "New Refrigerant Type Used":
                    assertGasTimelineDetail(timelineEvent.getDetail1(), "Refrigerant Type Used", (String) afterMap.get(title));
                    break;

                case "Appliance Identification":
                    if ("Appliance not on list".equals(afterMap.get(title))) {
                        assertGasTimelineDetail(timelineEvent.getDetail1(), "Appliance Information", (String) afterMap.get("Please provide appliance details"));
                    } else {
                        assertGasTimelineDetail(timelineEvent.getDetail1(), "Appliance Identification", (String) afterMap.get(title));
                    }
                    break;

                case "Please provide appliance details":
                    assertGasTimelineDetail(timelineEvent.getDetail1(), "Appliance Information", (String) afterMap.get(title));
                    break;

                case "Has receiver level been recorded?":
                    assertGasTimelineDetail(timelineEvent.getDetail1(), "Receiver Level Recorded", (String) afterMap.get(title));
                    break;

                case "Provide Level Indicator %":
                    assertGasTimelineDetail(timelineEvent.getDetail1(), "Provided Level Indicator %", (String) afterMap.get(title));
                    break;

                case "Please enter appliance maximum charge":
                    assertGasTimelineDetail(timelineEvent.getDetail1(), "New Appliance Maximum Charge", (String) afterMap.get(title));
                    break;

                case "Please provide notes to explain reason for changing appliance maximum charge":
                    assertGasTimelineDetail(timelineEvent.getDetail1(), "Reason For Changing Maximum Charge", (String) afterMap.get(title));
                    break;

                default:
                    assertGasTimelineDetail(timelineEvent.getDetail1(), title, (String) afterMap.get(title));
                }
            }
        }

        // cylinders
        List<String> afterCylinderSerialNumbers = testData.getList(AFTER + "cylinderSerialNumbers", String.class);
        List<String> afterCylinderSources = testData.getList(AFTER + "cylinderSources", String.class);
        List<String> afterCylinderFullPartial = testData.getList(AFTER + "cylinderFullPartial", String.class);
        List<String> afterCylinderTypes = testData.getList(AFTER + "cylinderTypes", String.class);
        List<String> afterCylinderGasAvailable = testData.getList(AFTER + "cylinderGasAvailable", String.class);
        List<String> afterCylinderGasInstalled = testData.getList(AFTER + "cylinderGasInstalled", String.class);
        List<String> afterCylinderSurplus = testData.getList(AFTER + "cylinderSurplus", String.class);
        List<String> afterCylinderDestinations = testData.getList(AFTER + "cylinderDestinations", String.class);
        List<String> afterCylinderSurplusTypes = testData.getList(AFTER + "cylinderSurplusTypes", String.class);

        for (int index = 0; index < afterCylinderSerialNumbers.size(); index++) {
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Cylinder Serial No", afterCylinderSerialNumbers.get(index));
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Refrigerant Source", afterCylinderSources.get(index));
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Full or Partial Cylinder", afterCylinderFullPartial.get(index));
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Type of Cylinder", afterCylinderTypes.get(index));

            String label = LOCALE.equals("en-GB") ? "Gas Available (kg)" : "Gas Available (lbs)";
            assertGasTimelineDetail(timelineEvent.getDetail1(), label, afterCylinderGasAvailable.get(index));

            label = LOCALE.equals("en-GB") ? "Gas Installed (kg)" : "Gas Installed (lbs)";
            assertGasTimelineDetail(timelineEvent.getDetail1(), label, afterCylinderGasInstalled.get(index));

            assertGasTimelineDetail(timelineEvent.getDetail1(), "Surplus", afterCylinderSurplus.get(index));
            if (afterCylinderDestinations.get(index) != null && !afterCylinderDestinations.get(index).isEmpty()) {
                assertGasTimelineDetail(timelineEvent.getDetail1(), "Surplus Destination", afterCylinderDestinations.get(index));
            }
            if (afterCylinderSurplusTypes.get(index) != null && !afterCylinderSurplusTypes.get(index).isEmpty()) {
                assertGasTimelineDetail(timelineEvent.getDetail1(), "Surplus Type", afterCylinderSurplusTypes.get(index));
            }
        }

        // leak site checks
        List<String> afterPrimaryComponents =  testData.getList(AFTER + "primaryComponents", String.class);
        List<String> afterPrimaryComponentsInfo = testData.getList(AFTER + "primaryComponentsInfo", String.class);
        List<String> afterSubComponents = testData.getList(AFTER + "subComponents", String.class);
        List<String> afterLeakSiteStatuses = testData.getList(AFTER + "leakSiteStatuses", String.class);
        List<String> afterInitialTests = testData.getList(AFTER + "initialTests", String.class);
        List<String> afterFollowUpTests = testData.getList(AFTER + "followUpTests", String.class);

        for (int index = 0; index < afterPrimaryComponents.size(); index++) {
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Primary Component", afterPrimaryComponents.get(index));
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Primary Component Information", afterPrimaryComponentsInfo.get(index));
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Sub-Component", afterSubComponents.get(index));
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Leak Site Status", afterLeakSiteStatuses.get(index));
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Initial Verification Test", afterInitialTests.get(index));
            assertGasTimelineDetail(timelineEvent.getDetail1(), "Follow Up Verification Test", afterFollowUpTests.get(index));
        }
    }

    private void assertGasTimelineDetail(String detail, String title, String value) {
        String template = "Title\":\"%s\",\"Value\":\"%s";
        String expected = String.format(template, title, value);
        assertTrue("Not found: Title: " + title + ", Value: " + value, detail.contains(expected));
    }

    @ContinueNextStepsOnException
    @Then("^user is returned to initial job reference entry screen$")
    public void user_is_returned_to_initial_job_reference_entry_screen() {
        assertTrue("Job reference entry screen not displayed", runtimeState.adminRefrigerantGasUsageSearchPage.isDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" cannot be set to \"([^\"]*)\"$")
    public void question_cannot_be_set_to(String question, String answer) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        List<String> options = questionHelper.getDropdownOptions(question);
        runtimeState.scenario.write(question + " (available options): " + options.toString());
        assertFalse("Unexpected option available: " + answer, options.contains(answer));
    }

    @ContinueNextStepsOnException
    @Then("^Leak Site Checks are not displayed$")
    public void leak_Site_Checks_are_not_displayed() throws Throwable {
        int numberLeakSiteChecksDisplayed = runtimeState.adminRefrigerantGasUsageEditPageV2.getNumberOfLeakSiteChecks();
        assertTrue("Unexpected number of Leak Site Checks displayed: " + numberLeakSiteChecksDisplayed, numberLeakSiteChecksDisplayed == 0);
    }

    @ContinueNextStepsOnException
    @Then("^gas usage button \"([^\"]*)\" is displayed$")
    public void gas_usage_button_is_not_displayed(String button) throws Throwable {
        assertTrue("Button is not displayed: " + button, runtimeState.adminRefrigerantGasUsageEditPageV2.isButtonDisplayed(button));
    }

    @ContinueNextStepsOnException
    @Then("^Adding Leak Site Details is optional$")
    public void Adding_Leak_Site_Details_is_optional() {
        runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addLeakSiteDetails();
        outputHelper.takeScreenshots();
        runtimeState.adminLeakSiteInformationModal.cancel();
        runtimeState.scenario.write("Saving form with NO leak details added");
        runtimeState.adminRefrigerantGasUsageEditPageV2.save();
        assertTrue("Job reference entry screen not displayed", runtimeState.adminRefrigerantGasUsageSearchPage.isDisplayed());
        assertTrue("Expected: Gas details updated successfully", runtimeState.adminRefrigerantGasUsageSearchPage.getToastMessage().equals("Gas details updated successfully"));
    }

    @ContinueNextStepsOnException
    @Then("^Adding Leak Site Details is mandatory$")
    public void Adding_Leak_Site_Details_is_mandatory() {
        outputHelper.takeScreenshot();
        runtimeState.adminRefrigerantGasUsageEditPageV2.save();
        assertFalse("Job reference entry screen not displayed", runtimeState.adminRefrigerantGasUsageSearchPage.isDisplayed());
        String alert = "Please add the details of at least one leak check";
        assertTrue("Alert expected: " + alert, runtimeState.adminRefrigerantGasUsageEditPageV2.getAlerts().contains(alert));
    }

    @When("^Add Leak Details is selected$")
    public void add_Leak_Details_is_selected() throws Throwable {
        runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addLeakSiteDetails();
    }

    @When("^leak details are entered$")
    public void leak_details_are_entered() throws Throwable {
        fgasAdminHelper.enterLeakDetails();
    }

    @ContinueNextStepsOnException
    @Then("^Leak Check Result Type is updated$")
    public void Leak_Check_Result_Type_is_updated() {
        String question = "Leak Check Result Type";
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        String currentAnswer = questionHelper.getAnswer(question);
        List<String> options = questionHelper.getDropdownOptions(question);
        runtimeState.scenario.write("Leak Check Result Type current answer: " + currentAnswer);
        runtimeState.scenario.write("Leak Check Result Type options: " + options.toString());
        int index = options.indexOf(currentAnswer);
        options.remove(index);
        String newAnswer = options.get(RandomUtils.nextInt(0, options.size()-1));
        runtimeState.scenario.write("Updating " + question + " from [" + currentAnswer + "] to [" + newAnswer + "]");
        questionHelper.updateAnswer(question, newAnswer);
    }

}
