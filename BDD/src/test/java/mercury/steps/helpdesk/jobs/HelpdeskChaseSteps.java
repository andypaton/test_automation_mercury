package mercury.steps.helpdesk.jobs;

import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.StringHelper.normalize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.runtime.RuntimeState;

public class HelpdeskChaseSteps {
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private TzHelper tzHelper;

    private static final Logger logger = LogManager.getLogger();
    private int actionsBadgeCount = 0;

    @When("^an \"([^\"]*)\" chase is created for the job$")
    public void a_chase_is_created_for_the_job(String chaseType) throws Throwable {
        runtimeState.helpdeskCreateJobChasePanel = runtimeState.helpdeskJobChasePanel.clickAddChase();
        setChaseDetails(chaseType);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskCreateJobChasePanel.clickCreateChaseButton();
    }

    @When("^the \"([^\"]*)\" chase details are entered$")
    public void the_chase_details_are_entered(String chaseType) {
        setChaseDetails(chaseType);
    }

    @When("^the Chase panel is displayed$")
    public void the_Chase_panel_is_displayed() throws Throwable {
        testData.addIntegerTag("jobReference", runtimeState.helpdeskJobPage.getJobReference());
        runtimeState.helpdeskJobChasePanel = runtimeState.helpdeskJobPage.selectChaseAction();
        outputHelper.takeScreenshots();
    }

    @When("^the view/update button is selected$")
    public void the_view_update_button_is_selected() throws Throwable {
        runtimeState.helpdeskUpdateJobChasePanel = runtimeState.helpdeskJobChasePanel.selectETAChaseForUpdate();
    }

    @ContinueNextStepsOnException
    @Then("^An \"([^\"]*)\" chase of type \"([^\"]*)\" is displayed$")
    public void an_chase_of_type_displayed(String state, String type) throws Throwable {
        Grid grid = runtimeState.helpdeskJobChasePanel.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");
        List<Row> row = grid.getRows();
        assertTrue("Invalid chase type", (row.get(0).getCell("Chase type").getText().equalsIgnoreCase(type)));
        assertTrue("Invalid chase status", (row.get(0).getCell("Status").getText().equalsIgnoreCase(state)));
    }

    @And("^a job with an active \"([^\"]*)\" chase and \"([^\"]*)\" creation route$")
    public void a_job_with_an_active_chase_and_creation_route(String ChaseType, String creationRoute) throws Throwable {
        int jobReference;
        switch (creationRoute) {
        case "Helpdesk" :
            testData.addIntegerTag("chaseCreationRoute", 1);
            jobReference = dbHelperJobs.getRandomJobWithActiveChase(ChaseType, testData.getInt("chaseCreationRoute"));
            if (jobReference == 0) {
                jobCreationHelper.createJobInStatus("Logged / Helpdesk Chase");
            } else {
                testData.put("jobReference", jobReference);
                dbHelperJobs.unlockJob(jobReference);
            }
            break;
        case "nonHelpdesk" :
            testData.addIntegerTag("chaseCreationRoute", 0);
            jobReference = dbHelperJobs.getRandomJobWithActiveChase(ChaseType, testData.getInt("chaseCreationRoute"));
            if (jobReference == 0) {
                jobCreationHelper.createJobInStatus("Logged / Non Helpdesk Manager Chase");
            } else {
                testData.put("jobReference", jobReference);
                dbHelperJobs.unlockJob(jobReference);
            }
            break;
        case "Helpdesk / With Chase Update" :
            testData.addIntegerTag("chaseCreationRoute", 1);
            jobReference = dbHelperJobs.getRandomJobWithActiveChaseAndUpdate(ChaseType, testData.getInt("chaseCreationRoute"));
            if (jobReference == 0) {
                jobCreationHelper.createJobInStatus("Logged / Helpdesk Chase");
            } else {
                testData.put("jobReference", jobReference);
                dbHelperJobs.unlockJob(jobReference);
            }
            break;
        case "Helpdesk / Only 1 Previous Chase" :
            testData.addIntegerTag("chaseCreationRoute", 1);
            jobReference = dbHelperJobs.getRandomJobWithOnlyOneActiveChase(ChaseType, testData.getInt("chaseCreationRoute"));
            if (jobReference == 0) {
                jobCreationHelper.createJobInStatus("Logged / Helpdesk Chase");
            } else {
                testData.put("jobReference", jobReference);
                dbHelperJobs.unlockJob(jobReference);
            }
            break;
        default :
            throw new PendingException("cannot find creation route for: " + creationRoute);
        }
        testData.put("creationRoute", creationRoute);
        runtimeState.scenario.write("test data selected: jobReference = " + testData.getInt("jobReference"));
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(testData.getInt("jobReference")));
        actionsBadgeCount = runtimeState.helpdeskJobPage.getActionsBadgeCount();
        outputHelper.takeScreenshots();
    }

    @When("^an update is added to an unresolved chase$")
    public void the_chase_is_updated() throws Throwable {
        String chaseNotes = "Chase updated by test automation";
        testData.addStringTag("chaseNotes", chaseNotes);
        if (testData.getString("chaseType") != null) {
            runtimeState.helpdeskUpdateJobChasePanel = runtimeState.helpdeskJobChasePanel.selectChaseTypeForUpdate(testData.getString("chaseType"));
        } else if (testData.getInt("chaseCreationRoute") == 1) {
            runtimeState.helpdeskUpdateJobChasePanel = runtimeState.helpdeskJobChasePanel.selectETAChaseForUpdate();
        } else {
            runtimeState.helpdeskUpdateJobChasePanel = runtimeState.helpdeskJobChasePanel.selectManagerChaseForUpdate();
        }
        String chaseNumber = runtimeState.helpdeskUpdateJobChasePanel.getChaseNumber();
        testData.addStringTag("chaseNumber", chaseNumber);
        runtimeState.helpdeskUpdateJobChasePanel.clickUpdateChase();
        runtimeState.helpdeskUpdateJobChasePanel.setUpdateChaseNotes(chaseNotes);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskJobChasePanel = runtimeState.helpdeskUpdateJobChasePanel.clickSubmitUpdateChase();
    }

    @ContinueNextStepsOnException
    @Then("^the Actions button number flag count has not changed$")
    public void the_Actions_button_number_flag_count_has_not_changed() throws Throwable {
        assertEquals("actions badge count different", actionsBadgeCount, runtimeState.helpdeskJobPage.getActionsBadgeCount());
    }

    @And("^the chase notes show the \"([^\"]*)\"$")
    public void the_chase_notes_have_been_updated(String action) throws Throwable {
        String chaseNumber = testData.getString("chaseNumber");
        if (action.equalsIgnoreCase("update")){
            runtimeState.helpdeskJobChasePanel.clickViewChase(chaseNumber);
            assertEquals("Unexpected chase notes", testData.getString("chaseNotes"), runtimeState.helpdeskUpdateJobChasePanel.getChaseNotes());
        }
        else if (action.equalsIgnoreCase("resolvedChase")){
            runtimeState.helpdeskJobChasePanel.clickViewChase(chaseNumber);
            assertEquals("Unexpected chase notes", testData.getString("chaseNotes"), runtimeState.helpdeskUpdateJobChasePanel.getChaseNotes());
        }
        else {
            runtimeState.helpdeskJobChasePanel.clickViewChase(chaseNumber);
            assertEquals("Unexpected chase notes", testData.getString("chaseNotes"), runtimeState.helpdeskJobChasePanel.getChaseNotes());
        }
        outputHelper.takeScreenshots();
    }

    @When("^the chase is updated to resolved$")
    public void the_chase_is_updated_to_resolved() throws Throwable {
        String chaseNotes = "Chase resolved by test automation";
        String chaseStoreNotes = "Store feedback provided by test automation" + DateHelper.dateAsString(new Date());
        testData.addStringTag("chaseNotes", chaseNotes);
        testData.addStringTag("chaseStoreNotes", chaseStoreNotes);
        if (testData.getInt("chaseCreationRoute") == 1) {
            runtimeState.helpdeskUpdateJobChasePanel = runtimeState.helpdeskJobChasePanel.selectETAChaseForUpdate();
        } else {
            runtimeState.helpdeskUpdateJobChasePanel = runtimeState.helpdeskJobChasePanel.selectManagerChaseForUpdate();
        }

        String chaseNumber = runtimeState.helpdeskUpdateJobChasePanel.getChaseNumber();
        testData.addStringTag("chaseNumber", chaseNumber);
        runtimeState.helpdeskUpdateJobChasePanel.clickResolveChase();
        runtimeState.helpdeskUpdateJobChasePanel.setUpdateChaseNotes(chaseNotes);
        if (testData.getInt("chaseCreationRoute") == 0) {
            runtimeState.helpdeskUpdateJobChasePanel.setChaseStoreResolutionFeedback(chaseStoreNotes);
        }
        outputHelper.takeScreenshots();
        runtimeState.helpdeskJobChasePanel = runtimeState.helpdeskUpdateJobChasePanel.clickSubmitResolveChase();
    }

    @ContinueNextStepsOnException
    @Then("^the Actions button number flag will be reduced or cleared$")
    public void the_Actions_button_number_flag_will_be_reduced_or_cleared() throws Throwable {
        if (actionsBadgeCount == 1) {
            assertTrue("actions badge count has not been reduced", runtimeState.helpdeskJobPage.badgeCountCleared());
        } else {
            assertEquals("actions badge count has not been reduced", actionsBadgeCount-1, runtimeState.helpdeskJobPage.getActionsBadgeCount());
        }
    }

    @When("^the chase is updated to cancelled$")
    public void the_chase_is_updated_to_cancelled() throws Throwable {
        String reason = "Chase raised in error by operator";
        String chaseNotes = "Chase updated by test automation";
        testData.addStringTag("chaseNotes", chaseNotes);
        runtimeState.helpdeskCancelJobChasePanel = runtimeState.helpdeskJobChasePanel.clickCancelChase();
        String chaseNumber = runtimeState.helpdeskCancelJobChasePanel.getChaseNumber();
        testData.addStringTag("chaseNumber", chaseNumber);
        runtimeState.helpdeskCancelJobChasePanel.setCancellationReason(reason);
        runtimeState.helpdeskCancelJobChasePanel.setCancelChaseNotes(chaseNotes);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskJobChasePanel = runtimeState.helpdeskCancelJobChasePanel.clickSubmitCancelChase();
    }

    @ContinueNextStepsOnException
    @Then("^the Actions button number flag is increased$")
    public void the_Actions_button_number_flag_is_increased() throws Throwable {
        assertEquals("actions badge count has not been increased", actionsBadgeCount+1, runtimeState.helpdeskJobPage.getActionsBadgeCount());
    }

    @When("^a chase is created with no details entered$")
    public void a_chase_is_created_with_no_details_entered() throws Throwable {
        runtimeState.helpdeskCreateJobChasePanel = runtimeState.helpdeskJobChasePanel.clickAddChase();
        selectMandatoryFields();
        runtimeState.helpdeskCreateJobChasePanel.clickCreateChaseButton();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^mandatory field error messages are displayed$")
    public void mandatory_field_error_messages_are_displayed() throws Throwable {
        assertTrue("Mandatory description field error message not displayed",runtimeState.helpdeskCreateJobChasePanel.verifyMandatoryFieldMessageDisplayed("description"));
        assertTrue("Mandatory chaseType field error message not displayed",runtimeState.helpdeskCreateJobChasePanel.verifyMandatoryFieldMessageDisplayed("chaseType"));
        assertTrue("Mandatory callerName field error message not displayed",runtimeState.helpdeskCreateJobChasePanel.verifyMandatoryFieldMessageDisplayed("callerName"));
        assertTrue("Mandatory telephoneNumber field error message not displayed",runtimeState.helpdeskCreateJobChasePanel.verifyMandatoryFieldMessageDisplayed("telephoneNumber"));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^view/update panel is displayed correctly$")
    public void view_update_panel_is_displayed_correctly() throws Throwable {
        List<String> chaseUpdates = runtimeState.helpdeskUpdateJobChasePanel.getAvailableUpdates();

        List<Map<String, Object>> chaseUpdatesDb = dbHelperJobs.getChaseUpdates(testData.getInt("jobReference"));
        int i = 0;
        for (Map<String, Object> dbData : chaseUpdatesDb) {
            String actual = normalize(chaseUpdates.get(i).toString());

            String chaseUpdate = dbData.get("ChaseUpdate").toString();
            String createdOn = dbData.get("CreatedOn").toString();
            createdOn = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), createdOn, DB_DATE_FORMAT);
            createdOn = DateHelper.convert(createdOn, DB_DATE_FORMAT, MEDIUM);
            createdOn = createdOn.replace("am", "AM").replace("pm", "PM");
            String name = dbData.get("Name").toString();
            String expected = normalize(String.format("%s %s (%s)", chaseUpdate, createdOn, name));
            assertEquals("Row " + i + " doesn't match. Expected: " + expected + " Actual: " + actual, expected, actual);
            i++;
        }
        String notes = DataGenerator.GenerateRandomString(1, 5, 1, 1, 0, 0);

        assertTrue("Update button is not displayed", runtimeState.helpdeskUpdateJobChasePanel.isUpdateRadioButtonDisplayed());
        assertTrue("Resolve button is not displayed", runtimeState.helpdeskUpdateJobChasePanel.isResolveRadioButtonDisplayed());

        assertFalse("Update Chase button can be clicked", runtimeState.helpdeskUpdateJobChasePanel.isUpdateChaseButtonClickable());
        runtimeState.helpdeskUpdateJobChasePanel.setUpdateChaseNotes(notes);
        assertTrue("Update Chase button cannot be clicked", runtimeState.helpdeskUpdateJobChasePanel.isUpdateChaseButtonClickable());
        runtimeState.helpdeskUpdateJobChasePanel.clearUpdateChaseNotes();
        runtimeState.helpdeskUpdateJobChasePanel.clickResolveChase();
        assertFalse("Resolve Chase button can be clicked", runtimeState.helpdeskUpdateJobChasePanel.isResolveChaseButtonClickable());
        runtimeState.helpdeskUpdateJobChasePanel.setUpdateChaseNotes(notes);
        assertTrue("Resolve Chase button cannot be clicked", runtimeState.helpdeskUpdateJobChasePanel.isResolveChaseButtonClickable());
    }

    @ContinueNextStepsOnException
    @Then("^chase panel is displayed correctly$")
    public void chase_panel_is_displayed_correctly() throws Throwable {
        actionsBadgeCount = runtimeState.helpdeskJobPage.getActionsBadgeCount();
        Map<String, Object> jobChase = dbHelperJobs.getChaseForJobReference(testData.getInt("jobReference"));
        Grid grid = runtimeState.helpdeskJobChasePanel.getGrid();
        String jobChaseResource = null;
        assertTrue("Actions badge count is not displayed", actionsBadgeCount != 0);
        assertTrue("New Chase button is not displayed", runtimeState.helpdeskJobChasePanel.isNewChaseButtonDisplayed());

        assertEquals("Expected Description: " + normalize(jobChase.get("Description").toString()) + " Actual Description: " + normalize(grid.getRows().get(0).getCell(0).getText()),
                normalize(jobChase.get("Description").toString()), normalize(grid.getRows().get(0).getCell(0).getText()));

        assertEquals("Expected Chase Type: " + jobChase.get("ChaseType") + " Actual Chase Type: " + grid.getRows().get(0).getCell(1).getText(),
                jobChase.get("ChaseType"), grid.getRows().get(0).getCell(1).getText());
        if(jobChase.get("Resource") == null) {
            jobChaseResource = "";
        } else {
            jobChaseResource = jobChase.get("Resource").toString();
        }
        assertEquals("Expected Resource: " + normalize(jobChaseResource) + " Actual Resource: " + normalize(grid.getRows().get(0).getCell(2).getText()),
                normalize(jobChaseResource), normalize(grid.getRows().get(0).getCell(2).getText()));
        assertEquals("Expected Status: " + jobChase.get("Status") + " Actual Status: " + grid.getRows().get(0).getCell(3).getText(),
                jobChase.get("Status"), grid.getRows().get(0).getCell(3).getText());

        String createdOn = jobChase.get("CreatedOn").toString();
        createdOn = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), createdOn, DB_DATE_FORMAT);
        createdOn = DateHelper.convert(createdOn, DB_DATE_FORMAT, MEDIUM);
        createdOn = createdOn.replace("am", "AM").replace("pm", "PM");
        assertEquals("Expected Created On: " + createdOn + " Actual: " + grid.getRows().get(0).getCell(4).getText(), createdOn, grid.getRows().get(0).getCell(4).getText());

        assertTrue("View/Update button is not displayed", runtimeState.helpdeskJobChasePanel.isViewUpdateButtonDisplayed());
        assertTrue("Cancel Chase button is not displayed", runtimeState.helpdeskJobChasePanel.isCancelChaseButtonDisplayed());
    }

    public void setChaseDetails(String chaseType) {
        String chaseDescription = "Updated by test automation";
        runtimeState.helpdeskCreateJobChasePanel.setChaseDescription(chaseDescription);
        setChaseRandomTelephoneNumber();
        runtimeState.helpdeskCreateJobChasePanel.setChaseType(chaseType);
        runtimeState.helpdeskCreateJobChasePanel.setResource();
        runtimeState.helpdeskCreateJobChasePanel.setChaseCallerName("Test Automation");
        setChaseRandomTelephoneNumberExtension();
        testData.put("newChase", true);
    }

    public void selectMandatoryFields(){
        runtimeState.helpdeskCreateJobChasePanel.clearChaseTelephoneNumber();
        runtimeState.helpdeskCreateJobChasePanel.clickChaseDescription();
        runtimeState.helpdeskCreateJobChasePanel.clickChaseTypeDropdown();
        runtimeState.helpdeskCreateJobChasePanel.clearCallerName();
        //Caller Name won't display error message until user has clicked away from this field
        runtimeState.helpdeskCreateJobChasePanel.clearChaseExtension();
        runtimeState.helpdeskCreateJobChasePanel.clickCreateChaseButton();

    }

    public void setChaseRandomTelephoneNumber() {
        runtimeState.helpdeskCreateJobChasePanel.clearChaseTelephoneNumber();
        String telephoneNumber = DataGenerator.generatePhoneNumber();
        runtimeState.helpdeskCreateJobChasePanel.setChaseRandomTelephoneNumber(telephoneNumber);
    }

    public void setChaseRandomTelephoneNumberExtension() {
        String telephoneExtension = DataGenerator.GenerateRandomString(4,4,0,0,4,0);
        runtimeState.helpdeskCreateJobChasePanel.setChaseRandomTelephoneNumberExtension(telephoneExtension);
    }
}