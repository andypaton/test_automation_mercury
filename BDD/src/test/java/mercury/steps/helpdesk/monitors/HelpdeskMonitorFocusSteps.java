package mercury.steps.helpdesk.monitors;

import static org.junit.Assert.assertTrue;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.MonitorHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.helpers.gridV3.Grid;
import mercury.runtime.RuntimeState;

public class HelpdeskMonitorFocusSteps {

    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private RuntimeState runtimeState;
    @Autowired private MonitorHelper monitorHelper;
    @Autowired private TestData testData;

    @ContinueNextStepsOnException
    @Then("^the SLA Near/Missed monitor ((?:will display|will not display)) the invalid jobs$")
    public void the_monitor_will_not_display_the_invalid_jobs(String action) throws Throwable {
        Integer jobReference;

        runtimeState.scenario.write("Asserting that the monitor does not display any jobs which are not assigned to a resource");
        jobReference = dbHelperJobs.getRandomJobNotAssignedToAResourceNotOfTypeWithDesiredFaultPriority("reactive", "P1");
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);

        runtimeState.scenario.write("Asserting that the monitor does not display any P3 jobs which are at a status of 'Fixed' ");
        jobReference = dbHelperJobs.getJobReferenceOfClientStatusWithFaultPriority(9, 3);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);

        runtimeState.scenario.write("Asserting that the monitor does not display any Cancelled jobs ");
        jobReference = dbHelperJobs.getJobReferenceOfClientStatusWithFaultPriority(10, 0);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);

        runtimeState.scenario.write("Asserting that the monitor does not display any Quote jobs ");
        jobReference = dbHelperMonitors.getJobReferenceFromMonitor("Awaiting Quote Request Review To Do", null, null);
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
    }

    @ContinueNextStepsOnException
    @Then("^the ETA greater than response/repair time monitor will display the jobs where the ETA is outwith the configured response time$")
    public void the_monitor_will_display_the_jobs_where_the_ETA_provided_by_the_resource_is_outwith_the_configured_response_time() throws Throwable {
        Integer jobReference = dbHelperMonitors.getJobReferenceFromMonitor("ETA greater than response/repair time", null, null);
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, "will display");
    }

    @ContinueNextStepsOnException
    @Then("^the ETA greater than response/repair time monitor ((?:will display|will not display)) the \"([^\"]*)\" jobs$")
    public void the_ETA_greater_than_response_repair_time_monitor_will_not_display_the_jobs(String action, String jobs) throws Throwable {
        Integer jobReference = null;

        switch (jobs) {

        case "P3":
            jobReference = dbHelperJobs.getJobReferenceOfClientStatusWithFaultPriority(1, 3);
            break;

        case "Quote":
            jobReference = dbHelperJobs.getJobReferenceOfJobType("Quote");
            break;

        case "Onsite Event":
            jobReference = dbHelperJobs.getJobReferenceOfTypeWithResourceAssignmentStatus("Reactive", "On Site");
            break;
        }
        monitorHelper.removeAllTheFiltersFromSettings();
        monitorHelper.searchThroughTheFilter("Job ID", jobReference, action);
    }
    
    @When("^the jobs with multiple active chase event has been searched$")
    public void the_jobs_with_multiple_active_chase_event_has_been_searched() throws Throwable {
        Map<String, Object> dbData = null;
        monitorHelper.removeAllTheFiltersFromSettings();
        dbData = dbHelperJobs.getJobReferenceAndCountWithMultipleActiveChases();
        Integer jobReference = Integer.valueOf(dbData.get("JobReference").toString());
        testData.addIntegerTag("chaseCount", Integer.valueOf(dbData.get("CHASECOUNT").toString()));
        runtimeState.monitorsToDo.selectMonitor(testData.getString("monitor"));
        monitorHelper.setFilter("Job ID", String.valueOf(jobReference));
        assertTrue("Job ID" + ": " + jobReference + " is not displayed in the monitor", runtimeState.monitorGrid.isReferenceNumberDisplayed(jobReference));
    }
    
    @Then("^the job will display as many times as there are active chases$")
    public void the_job_will_display_as_many_times_as_there_are_active_chases() throws Throwable {
        Integer chaseCount = testData.getInt("chaseCount");
        Grid grid = runtimeState.monitorGrid.getGrid();
        int numRowsDisplayed = grid.getRows().size();
        runtimeState.scenario.write("Number of rows in grid = " + numRowsDisplayed);
        assertTrue("Chanse Count = " + chaseCount + ", Number of Rows displayed = " + numRowsDisplayed, chaseCount == numRowsDisplayed);
    }
}
