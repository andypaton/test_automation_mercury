package mercury.steps.helpdesk.incidents;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;

public class HelpdeskIncidentAssertions {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private CommonSteps commonSteps;
    @Autowired private TestData testData;
    @Autowired private DbHelper dbHelper;

    @ContinueNextStepsOnException
    @Then("^user can view incident tab on site view$")
    public void user_can_view_incident_tab_on_site_view() throws Throwable {
        assertTrue("Oops look like the incident tab is not displayed",runtimeState.helpdeskSitePage.isIncidentsTabDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^user can not see incident tab on site view$")
    public void user_can_not_see_incident_tab_on_site_view() throws Throwable {
        assertFalse("Looks like the incident tab is displayed to the user with no permission ",runtimeState.helpdeskSitePage.isIncidentsTabDisplayed());
    }

    @When("^user is on helpdesk home page$")
    public void user_is_on_helpdesk_home_page() throws Throwable {
        if (getWebDriver().getCurrentUrl().contains("home")) {
            runtimeState.scenario.write("User is on home page");
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^user can not see incident monitor on helpdesk search page$")
    public void user_can_not_see_incident_monitor_on_helpdesk_search_page() throws Throwable {
        assertFalse("Looks like the incident monitor is displayed to the user with no permission ",runtimeState.helpdeskSearchBar.IsIncidentsMonitorDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^user can not see linked assets panel on job view$")
    public void user_can_not_see_linked_assets_panel_on_job_view() throws Throwable {
        assertEquals("Your account is not configured to view incidents. Contact a helpdesk supervisor if you should have access", runtimeState.helpdeskJobsLinkedIncidentsPanel.getLinkedMessageText());
    }

    @ContinueNextStepsOnException
    @Then("^the log an incident page is displayed where description is prepopulated and is same as job description$")
    public void the_log_an_incident_page_is_displayed_where_description_is_prepopulated_and_is_same_as_job_description() throws Throwable {
        runtimeState.scenario.write("Asserting Incident description is: " + testData.getString("description"));
        String incidentDescription = runtimeState.helpdeskLogAnIncidentPage.getDescription();
        assertEquals("Unexpected Incident Description", testData.getString("description"), incidentDescription);
    }

    @And("^the incident Resource Caller value is prepopulated and is same as job caller$")
    public void the_incident_resource_caller_value_is_prepopulated_and_is_same_as_job_caller() throws Throwable {
        runtimeState.scenario.write("Asserting Incident caller is: " + testData.getString("caller"));
        String incidentCaller = runtimeState.helpdeskLogAnIncidentPage.getSelectedCallerValue();
        assertEquals("Unexpected incident Caller", testData.getString("caller"), incidentCaller);
    }

    @And("^the Incident Type \"([^\"]*)\" is pre-selected$")
    public void the_incident_type_is_preselected(String incidentType) throws Throwable {
        runtimeState.scenario.write(
                "Asserting " + incidentType
                + "is selected");
        runtimeState.helpdeskLogAnIncidentPage.isIncidentTypeSelected(incidentType.toLowerCase());

        dbHelper.deleteFromLinkedIncidentCriterionTable(testData.getString("assetSubType"), testData.getString("faultType"), testData.getString("incidentType"));
    }

    @ContinueNextStepsOnException
    @Then("^the job is displayed in the incidents linked jobs modal$")
    public void the_job_is_displayed_in_the_incidents_linked_jobs_modal() throws Throwable {
        Grid grid = runtimeState.helpdeskIncidentsLinkedJobsModal.getGrid();
        logger.debug(
                "Grid retrieved:" + grid.getHeaders().toString()
                + " ("
                + grid.getRows().size()
                + " rows)");
        Row row = grid.getRows().get(0);
        runtimeState.scenario.write("Asserting Linked jobs modal contains the job : " + testData.getInt("jobReference"));
        assertEquals("Job is not displayed in the incident's linked jobs modal", String.valueOf(testData.getInt("jobReference")), row.getCell("Reference").getText());
        commonSteps.the_button_is_clicked("Close");
        outputHelper.takeScreenshots();
    }
}
