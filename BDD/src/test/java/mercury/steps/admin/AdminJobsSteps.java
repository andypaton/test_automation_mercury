package mercury.steps.admin;

import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperOnCallScheduler;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.admin.jobs.OnCallSchedulerAdminPage;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;

public class AdminJobsSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperOnCallScheduler dbHelperOnCallScheduler;
    @Autowired private CommonSteps commonSteps;

    @ContinueNextStepsOnException
    @Then("^the note \"([^\"]*)\" is displayed above the search box$")
    public void the_note_is_displayed_above_the_search_box(String note) throws Throwable {

        runtimeState.scenario.write("Asserting '" + note + "' is displayed above the search box");
        assertTrue("Note is not displayed ", runtimeState.adminJobsPage.isSearchNoteDisplayed(note));
    }

    @When("^the user enters first few numberical characters of job number and selects search$")
    public void the_user_enters_first_few_numberical_characters_of_job_number_and_selects_search() throws Throwable {

        String jobReference = dbHelperJobs.getRandomJobReferences(1).get(0).toString();
        String jobReferenceForSearch = jobReference.substring(0, jobReference.length() - 1);
        testData.put("jobReferenceForSearch", jobReferenceForSearch);

        runtimeState.adminJobsPage.enterJobReference(testData.getInt("jobReferenceForSearch"));
        runtimeState.adminJobsPage.search();
    }

    @When("^the admin user searches and selects a job$")
    public void the_admin_user_searches_and_selects_a_job() throws Throwable {
        int jobReference = dbHelperJobs.getRandomJobReferences(1).get(0);
        testData.put("jobReference", jobReference);
        runtimeState.adminJobsPage.enterJobReference(jobReference);
        runtimeState.adminJobsPage.search();
        runtimeState.scenario.write("Opening Job Reference : " + jobReference);
        runtimeState.adminJobStatusEditPage = runtimeState.adminJobsPage.OpenJob(String.valueOf(jobReference));
    }

    @ContinueNextStepsOnException
    @Then("^the Admin Job Status Edit page will be displayed with warning \"([^\"]*)\", job details and save button$")
    public void the_admin_job_status_edit_page_will_be_displayed_with_warning_job_details_and_save_button(String warning) throws Throwable {
        runtimeState.scenario.write("Asserting '" + warning + "' is displayed");
        assertEquals("Warning is not displayed", normalize(warning), normalize(runtimeState.adminJobStatusEditPage.getWarning()));

        runtimeState.scenario.write("Asserting Job Reference is: " + testData.getString("jobReference"));
        assertEquals("Unexpected Job Reference: ", testData.getString("jobReference"), runtimeState.adminJobStatusEditPage.getJobReference());

        String jobStatus = dbHelperJobs.getJobStatus(testData.getString("jobReference"));
        runtimeState.scenario.write("Asserting Current Job Status is : " + jobStatus);
        assertEquals("Unexpected Current Job Status: ", jobStatus, runtimeState.adminJobStatusEditPage.getCurrentJobStatus());

        runtimeState.scenario.write("Asserting Target Status is : " + jobStatus);
        assertEquals("Unexpected Target Status: ", jobStatus, runtimeState.adminJobStatusEditPage.getTargetStatus());

        runtimeState.scenario.write("Asserting save button is displayed");
        assertTrue("Save button is not displayed! ", runtimeState.adminJobStatusEditPage.isSaveButtonDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" sections are dipslayed on the On Call Scheduler Admin page$")
    public void the_sections_are_dipslayed_on_on_call_scheduler_admin_page(String sections) throws Throwable {
        runtimeState.onCallSchedulerAdminPage = new OnCallSchedulerAdminPage(getWebDriver()).get();

        List<String> sectionsList = Arrays.asList(sections.split("\\s*,\\s*"));
        for (String section : sectionsList) {
            runtimeState.scenario.write("Asserting section:  '" + section + "' is displayed");
            assertTrue("Section is not displayed", runtimeState.onCallSchedulerAdminPage.isSectionDisplayed(section));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Start of the Week drop-down is displayed with each day of the week available for selection$")
    public void the_start_of_the_week_dropdown_is_displayed_with_each_day_of_the_week_available_for_selection() throws Throwable {
        String startDayOfWeek = dbHelperOnCallScheduler.getStartDayOfTheWeek();
        runtimeState.scenario.write(" Asserting default selected Start Day Of the week is : " + startDayOfWeek);
        assertEquals("Unexpected Start Day Of The Week ", startDayOfWeek, runtimeState.onCallSchedulerAdminPage.getStartOfTheWeekDropdownSelectedOption());

        List<String> startOfTheWeekDropdownOptions = runtimeState.onCallSchedulerAdminPage.getStartOfTheDayDropdownOptions();
        String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        runtimeState.scenario.write("Asserting Start Of The Week dropdown has all days of the week as its options ");
        assertTrue("Unexpected Start Of The Week dropdown options: ", Arrays.asList(daysOfTheWeek).containsAll(startOfTheWeekDropdownOptions));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void assertDataInOnCallSchedulerAdminGrid(Grid grid, List<Map<String, Object>> dbData) throws Exception {

        // Compare both sets of data are the same size
        assertEquals("Unexpected number of records", dbData.size(), GridHelper.getAllRows(grid).size());

        // Verify both datasets are the same
        Iterator it = dbData.iterator();
        if (dbData.size() > 0) {
            for (Row row : grid.getRows()) {
                Map<String, Object> map = (Map<String, Object>) it.next();

                String weekDay = row.getCell("Week Day").getText();
                logger.debug(weekDay);
                assertEquals("Unexpected Week Day " + weekDay, normalize(map.get("Week Day").toString()), row.getCell("Week Day").getText());
                assertEquals("Unexpected Start Time for " + weekDay, normalize(DateHelper.convert(map.get("Start Time").toString(), "h:mma", "hh:mm a").toUpperCase()), normalize(row.getCell("Start Time").getText().toUpperCase()));
                assertEquals("Unexpected End Time (Next Day) for " + weekDay, normalize(DateHelper.convert(map.get("End Time (Next Day)").toString(), "h:mma", "hh:mm a").toUpperCase()), normalize(row.getCell("End Time (Next Day)").getText().toUpperCase()));
            }
        }
    }

    @When("^the user edits Start Of The Week Day$")
    public void the_user_edits_start_of_the_week_day() throws Throwable {
        runtimeState.onCallSchedulerAdminPage = new OnCallSchedulerAdminPage(getWebDriver()).get();

        String newStartOftheWeekDay = dbHelperOnCallScheduler.getRandomDayOfTheWeekThatIsNotStartDay();
        runtimeState.onCallSchedulerAdminPage.searchAndSelectRandomStartOfTheWeekDay(newStartOftheWeekDay);
        testData.put("newStartOftheWeekDay", newStartOftheWeekDay);
        runtimeState.scenario.write("User selected Start Of The Week Day is : " + newStartOftheWeekDay);
    }

    @ContinueNextStepsOnException
    @Then("^the new Start Of The Week Day is saved to the database$")
    public void the_new_start_of_the_week_day_is_saved_to_the_database() throws Throwable {
        commonSteps.the_popup_alert_is_confirmed("Success");

        runtimeState.scenario.write(" Asserting new Start Of the Week Day in Database is : " + testData.getString("newStartOftheWeekDay"));
        assertEquals("Unexpected New Start Day Of The Week ", testData.getString("newStartOftheWeekDay"), dbHelperOnCallScheduler.getStartDayOfTheWeek());
    }

    @When("^the user clicks Edit button for the day they wish to amend$")
    public void the_user_clicks_edit_button_for_the_day_they_wish_to_amend() throws Throwable {
        runtimeState.onCallSchedulerAdminPage = new OnCallSchedulerAdminPage(getWebDriver()).get();

        String dayOfTheWeek = dbHelperOnCallScheduler.getRandomDayOfTheWeekThatIsNotStartDay();
        testData.put("dayOfTheWeek", dayOfTheWeek);
        runtimeState.scenario.write("Clicking Edit button in the Out Of Hours Times for the day: " + dayOfTheWeek);
        runtimeState.onCallSchedulerAdminPage.clickEditButtonForTheDay(dayOfTheWeek);
    }

    @ContinueNextStepsOnException
    @Then("^the Start Time and End Time Fields for user selected day are available for editing$")
    public void the_start_time_and_end_time_fields_for_user_selected_day_are_available_for_editing() throws Throwable {
        runtimeState.scenario.write("Asserting Start Time for " + testData.getString("dayOfTheWeek") + " is available for editing");
        assertTrue("Start Time is not editable! ", runtimeState.onCallSchedulerAdminPage.isStartTimeDropdownDisplayedForTheDay(testData.getString("dayOfTheWeek")));
        runtimeState.scenario.write("Asserting End Time for " + testData.getString("dayOfTheWeek") + " is available for editing");
        assertTrue("Start Time is not editable! ", runtimeState.onCallSchedulerAdminPage.isEndTimeDropdownDisplayedForTheDay(testData.getString("dayOfTheWeek")));
    }

    @ContinueNextStepsOnException
    @Then("^the Start Time and End Time Fields for other days are not available for editing$")
    public void the_start_time_and_end_time_fields_for_other_days_are_not_available_for_editing() throws Throwable {
        String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        for (String dayOfTheWeek : daysOfTheWeek) {
            if (!dayOfTheWeek.equals(testData.getString("dayOfTheWeek"))) {
                runtimeState.scenario.write("Asserting Start Time for " + dayOfTheWeek + " is not available for editing");
                assertFalse("Start Time is editable! ", runtimeState.onCallSchedulerAdminPage.isStartTimeDropdownDisplayedForTheDay(dayOfTheWeek));
                runtimeState.scenario.write("Asserting End Time for " + dayOfTheWeek + " is not available for editing");
                assertFalse("Start Time is editable! ", runtimeState.onCallSchedulerAdminPage.isEndTimeDropdownDisplayedForTheDay(dayOfTheWeek));
            }
        }
    }

    @When("^selects new Start Time and End Time$")
    public void selects_new_start_time_and_end_time() throws Throwable {
        String newstartTime = runtimeState.onCallSchedulerAdminPage.selectRandomStartTime(testData.getString("dayOfTheWeek"));
        runtimeState.scenario.write("Selected Start Time: " + newstartTime);
        testData.put("newstartTime", newstartTime);

        String newEndTime = runtimeState.onCallSchedulerAdminPage.selectRandomEndTime(testData.getString("dayOfTheWeek"));
        runtimeState.scenario.write("Selected End Time: " + newEndTime);
        testData.put("newEndTime", newEndTime);
    }

    @Then("^the new Start Time and End Time is updated in the database$")
    public void the_new_start_time_and_end_time_is_updated_in_the_database() throws Throwable {

        int dayOfTheWeek = dbHelperOnCallScheduler.getIntegerValueOfWeekDay(testData.getString("dayOfTheWeek"));
        Map<String, Object> times = dbHelperOnCallScheduler.getStartTimeEndTimeForDay(dayOfTheWeek);

        runtimeState.scenario.write("Asserting new Start Time for " + testData.getString("dayOfTheWeek") + " in the database is: " + testData.getString("newstartTime"));
        assertEquals("Unexpected Start Time ", normalize(testData.getString("newstartTime")), normalize(DateHelper.convert(times.get("Start Time").toString(), "h:mma", "hh:mm a").toUpperCase()));
        runtimeState.scenario.write("Asserting new End Time for " + testData.getString("dayOfTheWeek") + " in the database is: " + testData.getString("newEndTime"));
        assertEquals("Unexpected End Time (Next Day) ", normalize(testData.getString("newEndTime")), normalize(DateHelper.convert(times.get("End Time (Next Day)").toString(), "h:mma", "hh:mm a").toUpperCase()));

        // reset the out of Hours Times
        dbHelperOnCallScheduler.resetOnCallScheduler();
    }

}



