package mercury.steps.portal.jobs;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.helpers.Globalisation.SHORT2;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.applicationinsights.core.dependencies.apachecommons.lang3.RandomStringUtils;
import com.microsoft.applicationinsights.web.dependencies.apachecommons.lang3.RandomUtils;

import cucumber.api.DataTable;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.CallerContact;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.databuilders.UpdateJob;
import mercury.helpers.DateHelper;
import mercury.helpers.FgasPortalHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.pageobject.web.pageHelpers.QuestionHelper;
import mercury.pageobject.web.portal.jobs.AcceptJobPage;
import mercury.pageobject.web.portal.jobs.JobInfoJobDetailsPage;
import mercury.runtime.RuntimeState;
import mercury.steps.portal.PortalCommon;

public class PortalUpdateJobSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private UpdateJob updateJob;
    @Autowired AssertionFactory assertionFactory;
    @Autowired private TestData testData;
    @Autowired private PortalCommon portalCommon;
    @Autowired private TzHelper tzHelper;
    @Autowired private FgasPortalHelper fgasPortalHelper;

    @When("^the job is not the landlords responsibility$")
    public void the_job_is_not_the_landlords_responsibility() throws Exception {
        if (runtimeState.updateJobPage.isQuestionVisible("Is this job the landlords responsibility?")) {
            runtimeState.updateJobPage.clickNo("Is this job the landlords responsibility?");
        }
    }

    @When("^the job is a Remote Job$")
    public void portal_update_job_remote_job_is_entered() throws Exception {
        updateJob.setRemoteJob(true);
        runtimeState.updateJobPage.selectRemoteJob();
    }

    @ContinueNextStepsOnException
    @When("^the \"([^\"]*)\" job form details are updated with Complete status on departure$")
    public void portal_job_form_job_details_are_updated_with_complete_status(String job) throws Throwable {
        the_job_is_not_the_landlords_responsibility();
        if (job.equalsIgnoreCase("remote")) {
            portal_update_job_remote_job_is_entered();
        } else {
            portal_update_job_travel_time_is_entered();
        }
        portal_update_job_operational_arrival_is_entered("No");
        portal_update_job_operational_departure_is_entered("Yes");
        portal_update_job_status_on_departure_is_entered("Complete");
        portal_update_job_asset_condition_is_entered();
        portal_update_job_root_cause_category_is_entered();
        portal_update_job_root_cause_is_entered();
        the_root_cause_description_is_entered();
        portal_update_job_additional_notes_is_entered();
    }

    @ContinueNextStepsOnException
    @Then("^the remote job form details are updated with ((?:Returning|Awaiting Parts)) status on departure$")
    public void portal_remote_job_form_job_details_are_updated_with_status(String status) throws Throwable {
        the_job_is_not_the_landlords_responsibility();
        portal_update_job_remote_job_is_entered();
        portal_update_job_operational_arrival_is_entered("No");
        portal_update_job_operational_departure_is_entered("Yes");
        portal_update_job_status_on_departure_is_entered(status);
        if (status.equalsIgnoreCase("Returning")) {
            portal_update_job_reason_for_returning_is_entered();
        }
        portal_update_job_return_eta_is_entered();
        portal_update_job_eta_window_is_entered();
    }

    @When("^the Travel Time is entered$")
    public void portal_update_job_travel_time_is_entered() throws Exception {
        runtimeState.updateJobPage.selectRandomTravelTime();
        String travelTime = runtimeState.updateJobPage.getTravelTime();
        travelTime = DateHelper.dateAsString(DateHelper.stringAsDate(travelTime, "HH:mm"), "HH:mm:ss");
        testData.addStringTag("Travel time", travelTime);

        runtimeState.scenario.write("Travel time " + travelTime);
    }

    @When("^the Operational On Arrival is \"([^\"]*)\"$")
    public void portal_update_job_operational_arrival_is_entered(String option) throws Exception {
        switch (option) {
        case "Yes" :
            runtimeState.updateJobPage.clickYes("Operational On Arrival");
            updateJob.setOperationalOnArrival(true);
            break;
        case "No" :
            runtimeState.updateJobPage.clickNo("Operational On Arrival");
            updateJob.setOperationalOnArrival(false);
            break;
        default :
            throw new Exception("Cannot find Operational On Arrival " + option);
        }
    }

    @When("^the Operational On Departure is \"([^\"]*)\"$")
    public void portal_update_job_operational_departure_is_entered(String option) throws Exception {
        switch (option) {
        case "Yes" :
            runtimeState.updateJobPage.clickYes("Operational On Departure");
            updateJob.setOperationOnDeparture(true);
            break;
        case "No" :
            runtimeState.updateJobPage.clickNo("Operational On Departure");
            updateJob.setOperationOnDeparture(false);
            break;
        default :
            throw new Exception("Cannot find Operational On Arrival " + option);
        }
    }

    @When("^the Work Start is entered$")
    public void portal_update_job_work_start_is_entered() throws Exception {
        runtimeState.updateJobPage.selectRandomWorkStartTime();
    }

    @When("^the Time Spent is entered$")
    public void portal_update_job_time_spent_is_entered() throws Exception {
        runtimeState.updateJobPage.selectTimeSpent(testData.getString("timeSpent"));
        updateJob.setWorkEnd(runtimeState.updateJobPage.getWorkEndTime());

        String workEndTime = updateJob.getWorkEnd();
        testData.put("Off site time", DateHelper.stringAsDate(workEndTime, SHORT2));

        runtimeState.scenario.write("Off site time " + testData.getString("Off site time"));
        runtimeState.scenario.write("Work End time : " + workEndTime);
    }

    @When("^the \"([^\"]*)\" Status on Departure is entered$")
    public void portal_update_job_status_on_departure_is_entered(String option) throws Exception {
        runtimeState.updateJobPage.selectStatusOnDeparture(option);
        runtimeState.scenario.write(runtimeState.updateJobPage.getStatusOnDeparture());
        updateJob.setStatusOnDeparture(runtimeState.updateJobPage.getStatusOnDeparture());
    }

    @When("^the Asset Condition is entered$")
    public void portal_update_job_asset_condition_is_entered() throws Exception {
        runtimeState.updateJobPage.selectRandomAssetCondition();
        runtimeState.scenario.write(runtimeState.updateJobPage.getAssetCondition());
        updateJob.setAssetCondition(runtimeState.updateJobPage.getAssetCondition());
    }

    @When("^the Root Cause Category is entered$")
    public void portal_update_job_root_cause_category_is_entered() throws Exception {
        runtimeState.updateJobPage.selectRandomRootCauseCategory();
        runtimeState.scenario.write(runtimeState.updateJobPage.getRootCauseCategory());
        updateJob.setRootCauseCategory(runtimeState.updateJobPage.getRootCauseCategory());
    }

    @When("^the Root Cause is entered$")
    public void portal_update_job_root_cause_is_entered() throws Exception {
        Integer i = 0;
        while(runtimeState.updateJobPage.getRootCauseSize() == 0) {
            runtimeState.scenario.write("Selecting new Root Cause Category");
            runtimeState.updateJobPage.selectRandomRootCauseCategory();
            runtimeState.scenario.write(runtimeState.updateJobPage.getRootCauseCategory());
            i++;
            if(i==6) {
                // Only loop through 6 times until MCP-1150 fixed
                throw new Exception("The system does not have the correct data therefore its impossible to select a ROOT CAUSE");
            }
        }
        runtimeState.updateJobPage.selectRandomRootCause();
        runtimeState.scenario.write(runtimeState.updateJobPage.getRootCause());
        updateJob.setRootCause(runtimeState.updateJobPage.getRootCause());
    }

    @When("^the Root Cause Description is entered$")
    public void the_root_cause_description_is_entered() throws Exception {
        if(runtimeState.updateJobPage.getRootCauseDescriptionVisible()) {
            String notes = "This is the root cause description : "+ DateHelper.dateAsString(new Date());
            runtimeState.updateJobPage.updateRootCauseDescription(notes);
            runtimeState.scenario.write("Root Cause Description Notes :" + notes);
        }
    }

    @When("^the Notes are entered$")
    public void portal_update_job_additional_notes_is_entered() throws Exception {
        String notes = "The thing is fixed, this is not the job you are looking for. "+ DateHelper.dateAsString(new Date());
        runtimeState.updateJobPage.updateNotes(notes);
        runtimeState.scenario.write(notes);
        updateJob.setNotes(notes);
    }

    @When("the Reason for Returning is entered")
    public void portal_update_job_reason_for_returning_is_entered() throws Exception {
        runtimeState.updateJobPage.selectRandomReasonForReturning();
        runtimeState.scenario.write(runtimeState.updateJobPage.getReasonForReturningSelectedValue());
    }

    @When("the Return ETA Date is entered")
    public void portal_update_job_return_eta_is_entered() throws Exception {
        String currentETA = runtimeState.updateJobPage.getETADate();
        runtimeState.scenario.write("Current ETA date " + currentETA);
        String newETA;
        if(currentETA.isEmpty() || currentETA == null) {
            newETA = DateHelper.getNowDatePlusOffset(24, SHORT_DATE);
        } else {
            newETA = DateHelper.getNowOrDatePlusOffset(currentETA, SHORT_DATE, 24, SHORT_DATE);
        }
        runtimeState.scenario.write("New ETA date " + newETA);
        runtimeState.updateJobPage.enterETADate(newETA);
        runtimeState.scenario.write("Return ETA Date : " + runtimeState.updateJobPage.getETADate());
    }

    @When("the Return ETA Window is entered")
    public void portal_update_job_eta_window_is_entered() throws Exception {
        runtimeState.updateJobPage.selectRandomETAWindow();
        runtimeState.scenario.write("Return ETA Window : " + runtimeState.updateJobPage.getETAWindow());
    }

    @When("^the leak test repair has been checked$")
    public void the_leak_test_repair_has_been_checked() throws Throwable {
        if (runtimeState.updateJobPage.isLeakTestRepairPresent() && !testData.getBoolean("leakChecked")) {
            runtimeState.updateJobPage.selectLeakTestRepair("Yes");
            testData.addBooleanTag("leakChecked", true);
        }
    }

    /**
     * Step to use no gas for both UK and US question set.  Used as a hack to when updating jobs on the portal for US version for non gas tests.
     *
     * @throws Throwable
     */
    @When("^no refrigerant gas used or leak check was carried out$")
    public void no_refrigerant_gas_used_or_leak_check_was_carried_out() throws Throwable {
        if (testData.getString("gasQuestionSet") == null) {
            if (runtimeState.updateJobPage.isQuestionVisible("Refrigerant Gas used during this visit") ) {
                runtimeState.updateJobPage.clickButtonAnswer("Refrigerant Gas used during this visit", "No");
                runtimeState.updateJobPage.clickButtonAnswer("Leak Check or Repair (without a refrigerant usage)", "No");
            } else  if (runtimeState.updateJobPage.isQuestionVisible("Refrigerant Gas Use") ) {
                runtimeState.updateJobPage.clickButtonAnswer("Refrigerant Gas Use", "No");
            }
        }
    }

    @When("a GSAN ((?:is|is not)) issued$")
    public void a_gsan_was_or_not_issued(String gsanIssued) throws Throwable {
        String answer = gsanIssued.equalsIgnoreCase("is") ? "Yes" : "No";
        if (runtimeState.updateJobPage.isQuestionVisible("Did you issue a Gas Safety Advice Note (GSAN)")) {
            runtimeState.updateJobPage.clickButtonAnswer("Did you issue a Gas Safety Advice Note (GSAN)", answer);
        }
        if (runtimeState.updateJobPage.isQuestionVisible("GSAN Reference Number")) {
            String gsanReferenceNumber = "GSAN-" + DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0);
            runtimeState.updateJobPage.enterGSANReferenceNumber(gsanReferenceNumber);
            runtimeState.updateJobPage.selectRandomGSANType();
        }
    }

    @When("^the user updates the job$")
    public void portal_update_job_save_job() throws Throwable {
        the_leak_test_repair_has_been_checked();

        if (testData.getDate("Off site time") == null) {
            Date offSiteTime = DateUtils.truncate(tzHelper.getCurrentTimeAtSite(testData.getInt("siteId")), Calendar.MINUTE);
            testData.put("Off site time", offSiteTime);
        }

        a_gsan_was_or_not_issued("is not");
        no_refrigerant_gas_used_or_leak_check_was_carried_out();

        fgasPortalHelper.captureAllFgasUsQuestions();

        if (runtimeState.updateJobPage.isSelectJobPhotoButtonDisplayed()) {
            runtimeState.scenario.write("uploading job photos");
            runtimeState.updateJobPage.uploadJobPhotos(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\jobphotos.PNG");
        }

        outputHelper.takeScreenshots();
        try {
            runtimeState.updateJobPage.updateJob();
        } catch (Exception e){
            runtimeState.scenario.write("It took a bit longer to update the job");
        }
    }

    @When("^the user updates the gas job$")
    public void the_user_updates_the_gas_job() throws Throwable {
        the_leak_test_repair_has_been_checked();

        if (testData.getDate("Off site time") == null) {
            Date offSiteTime = DateUtils.truncate(tzHelper.getCurrentTimeAtSite(testData.getInt("siteId")), Calendar.MINUTE);
            testData.put("Off site time", offSiteTime);
        }

        a_gsan_was_or_not_issued("is");

        fgasPortalHelper.captureAllFgasUsQuestions();

        outputHelper.takeScreenshots();

        runtimeState.updateJobPage.updateJob();
    }

    @When("the ETA date is entered$")
    public void the_eta_date_is_entered() throws Throwable {
        String newEtaDate = DateHelper.getNowDatePlusOffset(24, FULL_DATE);
        runtimeState.portalUpdateJobETA.selectETADate(newEtaDate);
        runtimeState.scenario.write("New ETA Date : " + newEtaDate);
    }

    @When("the ETA Window is entered")
    public void the_eta_window_is_entered() throws Throwable {
        String etaWindow = runtimeState.portalUpdateJobETA.selectRandomETAWindow();
        runtimeState.scenario.write("New ETA Window : " + etaWindow);
    }

    @When("the ETA is updated$")
    public void the_eta_is_updated() throws Throwable {
        the_eta_date_is_entered();
        the_eta_window_is_entered();
    }

    @When("the ETA ((?:is|is not)) advised to site$")
    public void the_eta_is_updated_advised_status_of(String advised) throws Throwable {
        boolean siteAdvised = "is".equalsIgnoreCase(advised) ? true : false;

        if (siteAdvised) {
            CallerContact cc = new CallerContact.Builder().build();

            runtimeState.portalUpdateJobETA.clickYes("ETA advised to site");
            runtimeState.portalUpdateJobETA.enterAdvisedTo(cc.getName());

            runtimeState.scenario.write("ETA is advised to " + cc.getName());
        } else {
            runtimeState.portalUpdateJobETA.clickNo("ETA advised to site");
        }
    }

    private void reset_ETA_and_save() throws Throwable {
        while (runtimeState.portalUpdateJobETA.controlToolTipIsDisplayed("ETA Window")){
            the_eta_is_updated();
            runtimeState.portalUpdateJobETA.submitForm();
        };
    }

    @When("the ETA is saved$")
    public void the_eta_is_saved() throws Throwable {
        runtimeState.portalUpdateJobETA.submitForm();
        reset_ETA_and_save();
    }

    @When("^Refrigerant is selected$")
    public void portal_update_job_refrigerant_gas_usage() throws Throwable {
        runtimeState.updateJobPage.clickYes("Refrigerant Gas used during this visit");
    }

    @When("^Refrigerant gas ((?:is|is not)) used during this visit$")
    public void portal_update_job_refrigerant_gas_usage(String gasUsed) throws Throwable {
        String answer = "is".equalsIgnoreCase(gasUsed) ? "Yes" : "No";
        runtimeState.updateJobPage.clickButtonAnswer("Refrigerant Gas used during this visit", answer);
    }

    @When("^a Leak Check or Repair ((?:is|is not)) carried out$")
    public void portal_update_job_leak_check_repair(String leakCheck) throws Throwable {
        String answer = "is".equalsIgnoreCase(leakCheck) ? "Yes" : "No";
        runtimeState.updateJobPage.clickButtonAnswer("Leak Check or Repair (without a refrigerant usage)", answer);
    }

    @When("^Gas was added to a rack$")
    public void portal_update_job_gas_added_to_rack() throws Throwable {
        runtimeState.updateJobPage.clickYes("Was Gas Added to a Rack?");
    }

    @When("^the other asset plant notes are entered$")
    public void portal_update_job_other_asset_plant_notes() throws Throwable {
        String notes = "Other asset plant notes for automation. " + DateHelper.dateAsString(new Date());
        runtimeState.updateJobPage.enterOtherAssetPlantNotes(notes);
        runtimeState.scenario.write(notes);
    }

    @When("^the other gas type notes are entered$")
    public void portal_update_job_other_gas_type_notes() throws Throwable {
        String notes = "Other type notes for automation. " + DateHelper.dateAsString(new Date());
        runtimeState.updateJobPage.enterOtherGasTypeNotes(notes);
        runtimeState.scenario.write(notes);
    }

    @When("^the other asset notes are entered$")
    public void portal_update_job_other_assent_notes() throws Throwable {
        String notes = "Other asset notes for automation. " + DateHelper.dateAsString(new Date());
        runtimeState.updateJobPage.enterOtherAssetNotes(notes);
        runtimeState.scenario.write(notes);
    }

    @When("^a random gas rack is selected$")
    public void portal_update_job_select_a_rack() throws Throwable {
        runtimeState.updateJobPage.selectRandomGasSelectARack();
        testData.addStringTag("gasRack",  runtimeState.updateJobPage.getGasSelectARackSelectedValue());
    }

    @When("^a random asset is selected$")
    public void portal_update_job_select_an_asset() throws Throwable {
        runtimeState.updateJobPage.selectRandomSelectAnAsset();
        if (!"Asset not in this list".equalsIgnoreCase(runtimeState.updateJobPage.getSelectAnAssetSelectedValue())) {
            testData.addStringTag("gasAsset",  runtimeState.updateJobPage.getSelectAnAssetSelectedValue());
        }
    }

    @When("^a \"([^\"]*)\" asset is selected$")
    public void portal_update_job_select_an_asset(String asset) throws Throwable {
        runtimeState.updateJobPage.selecSelectAnAsset(asset);
        if (!"Asset not in this list".equalsIgnoreCase(runtimeState.updateJobPage.getSelectAnAssetSelectedValue())) {
            testData.addStringTag("gasAsset", runtimeState.updateJobPage.getSelectAnAssetSelectedValue());
        }
    }

    @When("^a random gas type is selected$")
    public void portal_update_job_select_gas_type() throws Throwable {
        runtimeState.updateJobPage.selectRandomGasType();
    }

    private void getSelectedGasType() {
        testData.addStringTag("gasType",  runtimeState.updateJobPage.getGasTypeSelectedValue());
    }

    @When("^a random gas other gas type is selected$")
    public void portal_update_job_select_other_gas_type() throws Throwable {
        runtimeState.updateJobPage.selectRandomOtherGasType();
        testData.addStringTag("gasOtherType",  runtimeState.updateJobPage.getAllGasTypeSelectedValue());
    }

    private void getOtherGasType() {
        testData.addStringTag("gasType",  runtimeState.updateJobPage.getAllGasTypeSelectedValue());
    }

    @When("^a random gas leakage code is selected$")
    public void portal_update_job_select_gas_leakage_code() throws Throwable {
        runtimeState.updateJobPage.selectRandomGasLeakageCode();
        testData.addStringTag("gasLeakageCode",  runtimeState.updateJobPage.getGasLeakageCodeSelectedValue());
    }

    @When("^a random gas leakage check method is selected$")
    public void portal_update_job_select_gas_leakage_check_method() throws Throwable {
        runtimeState.updateJobPage.selectRandomGasLeakageCheckMethod();
        testData.addStringTag("gasLeakageCheckMethod",  runtimeState.updateJobPage.getGasLeakageCheckMethodSelectedValue());
    }

    @When("^a random gas leak location is entered$")
    public void portal_update_job_select_gas_leak_location() throws Throwable {
        runtimeState.updateJobPage.selectRandomGasLeakLocation();
        testData.addStringTag("gasLeakLocation",  runtimeState.updateJobPage.getGasLeakLocationSelectedValue());
    }

    @When("^a random gas action is selected$")
    public void portal_update_job_select_action() throws Throwable {
        runtimeState.updateJobPage.selectRandomAction();
        testData.addStringTag("gasAction",  runtimeState.updateJobPage.getActionSelectedValue());
    }

    @When("^a random fault code is selected$")
    public void portal_update_job_select_fault_code() throws Throwable {
        runtimeState.updateJobPage.selectRandomFaultCode();
        testData.addStringTag("gasFaultCode",  runtimeState.updateJobPage.getFaultCodeSelectedValue());
    }

    @When("^gas bottle usage is entered$")
    public void portal_update_job_gas_bottle_usage_is_entered() throws Throwable {
        runtimeState.updateJobPage.enterGasUsageBottleNumber(0);
        runtimeState.updateJobPage.enterGasUsageBottleQuantity(0);
    }

    @When("^Refrigerant was used and ((?:was not|was)) added to the rack$")
    public void portal_update_job_refrigerant_usage_when_completing_a_job(String addedToRack) throws Throwable {
        boolean rack = ("was not".equalsIgnoreCase(addedToRack) ? false : true);
        runtimeState.jobInfoJobDetailsPage = new JobInfoJobDetailsPage(getWebDriver()).get();
        String asset = runtimeState.jobInfoJobDetailsPage.getDetail("Asset");
        boolean hasAsset = ("Job does not have an asset attached".equalsIgnoreCase(asset) ? false
                : true);

        logger.debug(addedToRack);

        portal_update_job_refrigerant_gas_usage();
        if (rack && !hasAsset) {
            if (!testData.getBoolean("inPlant")) {
                portal_update_job_gas_added_to_rack();
            }
            portal_update_job_select_a_rack();
            portal_update_job_other_asset_plant_notes();
        } else {
            if (!hasAsset) {
                portal_update_job_select_an_asset();
                if ("Asset not in this list".equalsIgnoreCase(runtimeState.updateJobPage.getSelectAnAssetSelectedValue())) {
                    portal_update_job_other_assent_notes();
                }
            }
        }

        portal_update_job_select_gas_type();

        if (rack) {
            if ("Other".equalsIgnoreCase(runtimeState.updateJobPage.getGasTypeSelectedValue())) {
                portal_update_job_select_other_gas_type();
                portal_update_job_other_gas_type_notes();
                getSelectedGasType();
                getOtherGasType();
            }
        } else {
            getOtherGasType();
            if ("Other".equalsIgnoreCase(runtimeState.updateJobPage.getAllGasTypeSelectedValue())) {
                getOtherGasType();

            }
        }

        portal_update_job_select_gas_leakage_code();
        portal_update_job_select_gas_leakage_check_method();
        portal_update_job_select_gas_leak_location();
        portal_update_job_select_action();
        portal_update_job_select_fault_code();
        portal_update_job_gas_bottle_usage_is_entered();
        logger.debug(testData.getTestData());
    }

    @When("^the following error messages are displayed:$")
    public void the_a_line_with_type_description_quantity_price_is_added(DataTable fieldErrors) throws Throwable {

        for (Map<String, String> row : fieldErrors.asMaps(String.class, String.class)) {
            String errorMessage = localize(row.get("errorMessage"));
            String question = localize(row.get("question"));
            boolean errorDisplayed = runtimeState.updateJobPage.theErrorMessageIsDisplayed(question, errorMessage);
            assertTrue(
                    "Unexpected error message not displayed " + question
                    + " : "
                    + errorMessage,
                    errorDisplayed);
        }
        outputHelper.takeScreenshots();
    }

    @Given("^the update Job form is complete with \"([^\"]*)\"$")
    public void portal_update_job_form_is_complete_with(String completionStatus) throws Throwable {
        the_job_is_not_the_landlords_responsibility();
        portal_update_job_travel_time_is_entered();
        portal_update_job_operational_arrival_is_entered("No");
        portal_update_job_operational_departure_is_entered("Yes");
        portalCommon.work_start_date_and_time_is_entered();
        portal_update_job_time_spent_is_entered();
        portal_update_job_status_on_departure_is_entered(completionStatus);
        do {
            portal_update_job_return_eta_is_entered();
            portal_update_job_eta_window_is_entered();
            portal_update_job_save_job();
        } while (runtimeState.updateJobPage.controlToolTipIsDisplayed("ETA "));
    }

    @Given("^the Update Job form is complete with basic information$")
    public void portal_update_job_form_is_complete_with_basic_information() throws Throwable {
        the_job_is_not_the_landlords_responsibility();
        portal_update_job_travel_time_is_entered();
        portal_update_job_operational_arrival_is_entered("No");
        portal_update_job_operational_departure_is_entered("Yes");

        // note 'Contractor Technician should be in userProfileName, but to support older code also check profileName
        if (!testData.getString("profileName").equals("Contractor Technician") && !testData.getString("userProfileName").equals("Contractor Technician")) {
            portalCommon.work_start_date_and_time_is_entered();
            portal_update_job_time_spent_is_entered();
        }

        QuestionHelper questionHelper = runtimeState.updateJobPage.getQuestionHelper();
        if (questionHelper.isQuestionVisible("Did you issue a Gas Safety Advice Note (GSAN)?")) {
            boolean gsanIssued = RandomUtils.nextBoolean();
            String answer = gsanIssued ? "Yes": "No";
            questionHelper.updateAnswer("Did you issue a Gas Safety Advice Note (GSAN)?", answer);
            if (gsanIssued) {
                questionHelper.updateAnswer("GSAN Reference Number", RandomStringUtils.randomAlphabetic(20));
                questionHelper.updateAnswer("Type");
            }
        }
    }

    @When("^the on Status on Departure section is completed with status \"([^\"]*)\"$")
    public void portal_update_job_status_on_departure_is_section(String option) throws Exception {
        portal_update_job_status_on_departure_is_entered(option);
        portal_update_job_asset_condition_is_entered();
        portal_update_job_root_cause_category_is_entered();
        portal_update_job_root_cause_is_entered();
        the_root_cause_description_is_entered();
        portal_update_job_additional_notes_is_entered();
    }

    @When("^the update Job form is completed with status on departure \"([^\"]*)\"$")
    public void the_update_job_form_is_completed_with_status_on_departue_complete(String status) throws Throwable {

        switch (status) {
        case "Complete":
            portal_update_job_status_on_departure_is_entered(status);

            portal_update_job_asset_condition_is_entered();
            portal_update_job_root_cause_category_is_entered();
            portal_update_job_root_cause_is_entered();
            the_root_cause_description_is_entered();
            portal_update_job_additional_notes_is_entered();
            portal_update_job_save_job();
            break;

        case "Awaiting Parts" :
            portal_update_job_status_on_departure_is_entered(status);

            do {
                portal_update_job_return_eta_is_entered();
                portal_update_job_eta_window_is_entered();
                portal_update_job_save_job();
            } while (runtimeState.updateJobPage.controlToolTipIsDisplayed("ETA Window"));
            break;

        case "Returning" :
            if (!runtimeState.updateJobPage.isStatusOnDepartureDisabled()) {
                portal_update_job_status_on_departure_is_entered(status);
            }
            portal_update_job_reason_for_returning_is_entered();
            portal_update_job_return_eta_is_entered();
            portal_update_job_eta_window_is_entered();
            portal_update_job_save_job();
            break;

        default:
            throw new Exception("Doh it seems like the status of ".concat(status).concat(" is not expected in this method.  Please check the code"));
        }
    }

    @When("^the update Job form is completed with \"([^\"]*)\" status on departure$")
    public void the_update_job_form_is_completed_with_returning_status_on_departue(String status) throws Throwable {

        if (status.equals("Returning")) {
            if (!runtimeState.updateJobPage.isStatusOnDepartureDisabled()) {
                portal_update_job_status_on_departure_is_entered("Returning");
            }
            portal_update_job_reason_for_returning_is_entered();
            portal_update_job_return_eta_is_entered();
            portal_update_job_eta_window_is_entered();

            if (testData.getDate("Off site time") == null) {
                Date offSiteTime = DateUtils.truncate(tzHelper.getCurrentTimeAtSite(testData.getInt("siteId")), Calendar.MINUTE);
                testData.put("Off site time", offSiteTime);
            }

        } else if (status.equals("Complete")) {
            portal_update_job_status_on_departure_is_entered("Complete");

        }

        outputHelper.takeScreenshots();
        fgasPortalHelper.captureAllFgasUsQuestions();

        runtimeState.updateJobPage.updateJob();
    }

    @When("^the Job Awaiting Acceptance is selected$")
    public void the_Job_Awaiting_Acceptance_is_selected() throws Throwable {
        runtimeState.openAwaitingJobsPage.searchJobsNoWait(testData.getString("jobReference"));
        outputHelper.takeScreenshots();
        runtimeState.jobDetailsPage = runtimeState.openAwaitingJobsPage.openJob(String.valueOf(testData.get("jobReference")));
    }

    @ContinueNextStepsOnException
    @Then("^the job can be Accepted$")
    public void the_job_can_be_Accepted() throws Throwable {
        assertTrue("Landlord cannot Accept Job", runtimeState.jobDetailsPage.isAcceptJobDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the job can be Declined$")
    public void the_job_can_be_Declined() throws Throwable {
        assertTrue("Landlord cannot Decline Job", runtimeState.jobDetailsPage.isDeclineJobDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the ETA can be updated$")
    public void the_ETA_can_be_updated() throws Exception {
        runtimeState.jobDetailsPage.acceptJob();
        outputHelper.takeScreenshots();
        assertTrue("Landlord cannot uptdate ETA", runtimeState.jobDetailsPage.isEtaDropdownDisplayed());

        runtimeState.acceptJobPage = new AcceptJobPage(getWebDriver()).get();

        String etaDate = DateHelper.getNowDatePlusOffset(24, FULL_DATE);
        runtimeState.acceptJobPage.enterETADate(etaDate);
        String etaWindow = runtimeState.acceptJobPage.selectRandomETAWindow();
        testData.addStringTag("etaWindow", etaWindow);
        runtimeState.scenario.write("ETA: " + etaDate + ", "+ etaWindow);

        outputHelper.takeScreenshots();
        runtimeState.acceptJobPage.save();
    }

}


