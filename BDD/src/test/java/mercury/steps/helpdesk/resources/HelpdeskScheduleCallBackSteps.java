package mercury.steps.helpdesk.resources;

import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.SHORT2;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class HelpdeskScheduleCallBackSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;

    @When("^user selects Schedule Callback action for the resource$")
    public void user_selects_schedule_callback_action_for_the_resource() throws Throwable {
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
        runtimeState.helpdeskScheduleCallBackPanel = runtimeState.helpdeskManageResourcesPanel.selectScheduleCallBackAction();
        runtimeState.scenario.write("Schedule Callback action is selected");
        outputHelper.takeScreenshots();
    }

    @And("^the Schedule callback time is selected$")
    public void the_schedule_callback_time_is_selected() throws Throwable {
        runtimeState.helpdeskScheduleCallBackPanel.clickRandomCallBackTimeButton();
    }

    @And("^the Schedule callback time is entered$")
    public void the_schedule_callback_time_is_entered() throws Throwable {
        Date callBackTime = DateHelper.getDatePlusOffsetInDays(10);
        String sceduledCallBackTime = DateHelper.dateAsString(callBackTime, SHORT2);
        runtimeState.helpdeskScheduleCallBackPanel.clickRandomCallBackTimeButton();
        the_schedule_callback_notes_is_entered();
        runtimeState.helpdeskScheduleCallBackPanel.setTime(sceduledCallBackTime);
        runtimeState.scenario.write("Schedule Callback time is entered: " + sceduledCallBackTime);
        the_schedule_callback_is_saved();
    }

    @And("^the Schedule callback notes is entered$")
    public void the_schedule_callback_notes_is_entered() throws Throwable {
        String notes = "Test notes entered on: " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskScheduleCallBackPanel.setNotes(notes);
        testData.addStringTag("notes", notes);
    }

    @And("^the Schedule callback is saved$")
    public void the_schedule_callback_is_saved() throws Throwable {
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskScheduleCallBackPanel.save();
    }

    @And("^selects call back time, enters notes and saves it$")
    public void selects_call_back_time_enters_notes_and_saves_it() throws Throwable {
        the_schedule_callback_time_is_selected();
        the_schedule_callback_notes_is_entered();

        String callBackTime = runtimeState.helpdeskScheduleCallBackPanel.getCallBackTime();
        String dueAt = DateHelper.convert(callBackTime, SHORT2, MEDIUM);
        testData.addStringTag("dueAt", dueAt);

        runtimeState.scenario.write("Entered Call back time: " + callBackTime + ", Notes: " + testData.getString("notes"));
        outputHelper.takeScreenshots();

        the_schedule_callback_is_saved();
    }

    @ContinueNextStepsOnException
    @Then("^telephone icon along with date and time of call back is displayed against the resource who requires a call back$")
    public void telephone_icon_along_with_date_and_time_of_call_back_is_displayed_against_the_resource_who_requires_a_call_back() throws Throwable {
        assertTrue("Telephone icon is not displayed", runtimeState.helpdeskManageResourcesPanel.isCallBackIconDisplayed());
        String actualCallBack = runtimeState.helpdeskManageResourcesPanel.getResourceCallBackText();
        String expectedCallBackText = testData.getString("dueAt");
        assertTrue("Date and time of Callback is displayed incorrectly", actualCallBack.equalsIgnoreCase(expectedCallBackText));
        runtimeState.scenario.write("Telephone icon, date and time of callback: " + expectedCallBackText + " is displayed against the resource.");
        outputHelper.takeScreenshots();
    }
}

