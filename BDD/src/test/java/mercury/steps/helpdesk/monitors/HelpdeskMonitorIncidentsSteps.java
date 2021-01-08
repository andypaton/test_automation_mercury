package mercury.steps.helpdesk.monitors;

import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.helpers.Globalisation.localize;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.MonitorHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskViewIncidentPage;
import mercury.pageobject.web.helpdesk.monitors.MonitorGrid;
import mercury.pageobject.web.helpdesk.monitors.MonitorsForInfo;
import mercury.pageobject.web.helpdesk.monitors.MonitorsSettings;
import mercury.pageobject.web.helpdesk.monitors.MonitorsToDo;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.helpdesk.incidents.HelpdeskIncidentActionDropdownSteps;
import mercury.steps.helpdesk.incidents.HelpdeskIncidentEscalationSteps;
import mercury.steps.helpdesk.incidents.HelpdeskIncidentsFollowUpSteps;
import mercury.steps.helpdesk.incidents.HelpdeskLogAnIncidentSteps;
import mercury.steps.helpdesk.incidents.HelpdeskViewIncidentSteps;
import mercury.steps.helpdesk.search.HelpdeskSearchForIncidentSteps;
import mercury.steps.helpdesk.search.HelpdeskSearchForSiteSteps;

public class HelpdeskMonitorIncidentsSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private MonitorHelper monitorHelper;
    @Autowired private HelpdeskViewIncidentSteps helpdeskViewIncidentSteps;
    @Autowired private CommonSteps commonsteps;
    @Autowired private HelpdeskIncidentsFollowUpSteps helpdeskIncidentsFollowUpSteps;
    @Autowired private HelpdeskIncidentActionDropdownSteps helpdeskIncidentActionDropdownSteps;
    @Autowired private HelpdeskIncidentEscalationSteps helpdeskIncidentEscalationSteps;
    @Autowired private HelpdeskLogAnIncidentSteps helpdeskLogAnIncidentSteps;
    @Autowired private HelpdeskSearchForSiteSteps helpdeskSearchForSiteSteps;
    @Autowired private HelpdeskMonitorSteps helpdeskMonitorSteps;
    @Autowired private HelpdeskSearchForIncidentSteps helpdeskSearchForIncidentSteps;


    @And("^the incident reference number for incident type \"([^\"]*)\" with status \"([^\"]*)\" is clicked from \"([^\"]*)\" monitor$")
    public void the_incident_reference_number_with_status_is_clicked_from_monitor(String incidentType, String status, String monitor) throws Throwable {

        switch (monitor) {

        case "Pending To Do":
            if (status.equalsIgnoreCase("Incident Followup")) {
                the_incident_reference_number_with_status_incident_follow_up_with_next_follow_up_required_criteria_is_clicked(incidentType);
                break;
            } else if(status.equalsIgnoreCase("Incident Initial Review,Incident Followup")) {
                the_incident_reference_number_with_status_incident_initial_review_and_follow_up_is_clicked(incidentType);
                break;
            }

        case "Follow ups":
            if ( status.equalsIgnoreCase("Incident Followup")) {
                the_incident_reference_number_with_status_incident_follow_up_with_next_follow_up_required_criteria_is_clicked(incidentType);
                break;
            }

        case "Reviews":
            if (status.equalsIgnoreCase("Incident Initial Review")) {
                the_incident_reference_number_with_status_initial_review_with_no_escalation_is_clicked(incidentType);
                break;
            }

        default:
            throw new Exception("Unexpected monitor: " + monitor);
        }
    }

    @And("^the incident reference number with status \"([^\"]*)\" is searched via a search bar$")
    public void the_incident_reference_number_with_status_is_searched_via_a_search_bar(String status) throws Throwable {
        Integer incidentReference = null;

        if (status.equalsIgnoreCase("Incident Initial Review")) {

            incidentReference = dbHelperIncidents.getIncidentReferenceNumberInitialReviewWithNoEscalation("Refrigeration Outage");
            testData.addStringTag("incidentType", "Refrigeration Outage");

            if (incidentReference == null) {
                helpdeskSearchForSiteSteps.a_search_is_run_for_City_Tech_store_with_caller("a search is run for an", "Occupied");
                testData.addStringTag("escalationCriteria", "No");
                helpdeskLogAnIncidentSteps.user_can_log_an_incident();
                runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();
                incidentReference = runtimeState.helpdeskViewIncidentPage.getIncidenceReference();
                testData.addIntegerTag("incidentReference", incidentReference);
                runtimeState.helpdeskHomePage.selectHomeTab();
            }
            testData.put("incidentReference", incidentReference);
            runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
            helpdeskSearchForIncidentSteps.a_search_is_for_an_incident_reference_number_via_searchbar(incidentReference);
        }
    }

    @And("^the incident reference number for incident type \"([^\"]*)\" with status incident follow up with next follow up required criteria is clicked$")
    public void the_incident_reference_number_with_status_incident_follow_up_with_next_follow_up_required_criteria_is_clicked(String incidentType) throws Throwable {
        testData.addStringTag("incidentType", incidentType);
        Integer incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumberFollowUpWithNextFollowupRequiredCriteria(incidentType);

        if (incidentReference != null) {
            testData.put("incidentReference", Integer.valueOf(incidentReference));
            runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
            the_incident_reference_number_is_searched_through_incident_monitor_filter(incidentReference);
        } else {
            runtimeState.helpdeskIncidentMonitorPage.clickPendingToDoLink();
            the_incident_reference_number_with_escalation_criteria_is_clicked();
            helpdeskViewIncidentSteps.the_incident_is_reviewed();
            commonsteps.the_button_is_clicked("Save");

            helpdeskIncidentsFollowUpSteps.the_user_has_updated_incident_for_further_follow_up_criteria();
            helpdeskIncidentActionDropdownSteps.the_option_is_clicked_from_the_actions_dropdown("Escalate");
            helpdeskIncidentEscalationSteps.the_message_is_entered_on_escalation_screen();
            commonsteps.the_button_is_clicked("Next");
            commonsteps.the_button_is_clicked("Send Escalations");

            incidentReference = runtimeState.helpdeskViewIncidentPage.getIncidenceReference();
            runtimeState.helpdeskHomePage.selectHomeTab();
            runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
        }
    }

    @And("^the incident reference number for incident type \"([^\"]*)\" with status incident initial review with no escalation criteria is clicked$")
    public void the_incident_reference_number_with_status_initial_review_with_no_escalation_is_clicked(String incidentType) throws Throwable {
        if(incidentType.equalsIgnoreCase("Refrigeration Outage")) {
            Integer incidentReference = dbHelperIncidents.getIncidentReferenceNumberInitialReviewWithNoEscalation(incidentType);
            if (incidentReference == null) {
                testData.addStringTag("incidentType", incidentType);
                runtimeState.helpdeskHomePage.closeActiveTab();
                helpdeskSearchForSiteSteps.a_search_is_run_for_City_Tech_store_with_caller("a search is run for an", "Occupied");
                helpdeskLogAnIncidentSteps.a_new_incident_is_logged_with_escalation_ctiteria_option("No");
                incidentReference = testData.getInt("incidentReference");
            }
            testData.put("incidentReference", incidentReference);
            runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
            the_incident_reference_number_is_searched_through_incident_monitor_filter(incidentReference);
        }
    }

    @And("^the incident reference number with escalation criteria is clicked$")
    public void the_incident_reference_number_with_escalation_criteria_is_clicked() throws Throwable {
        Integer incidentReference = dbHelperIncidents.getIncidentReferenceNumberEscalationCriteria(testData.getString("incidentType"));
        if (incidentReference == null) {
            runtimeState.helpdeskHomePage.closeActiveTab();
            helpdeskSearchForSiteSteps.a_search_is_run_for_City_Tech_store_with_caller("a search is run for an", "Occupied");
            helpdeskLogAnIncidentSteps.a_new_incident_is_logged_with_escalation_ctiteria_option("Yes");
            incidentReference = testData.getInt("incidentReference");
        }
        testData.put("incidentReference", incidentReference);
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        the_incident_reference_number_is_searched_through_incident_monitor_filter(incidentReference);
    }

    public void the_incident_reference_number_is_searched_through_incident_monitor_filter(int incidentReference) throws Throwable {
        runtimeState.monitorsSettings = new MonitorsSettings(getWebDriver()).get();

        runtimeState.monitorsSettings.expandSettings();
        helpdeskMonitorSteps.All_teams_are_selected_from_Settings();
        monitorHelper.setFilter("Incident Reference", String.valueOf(incidentReference));
        runtimeState.scenario.write("Incident Reference number is entered into an filter");
        outputHelper.takeScreenshot();
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskIncidentMonitorPage.clickIncidentReference(incidentReference);
    }

    public void the_incident_reference_number_with_status_incident_initial_review_and_follow_up_is_clicked(String incidentType) throws Throwable {
        Integer incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumberWithStatusInitialReviewAndFollowUp(incidentType);
        if (incidentReference==null) {
            throw new PendingException("No test data found");
        }
        testData.put("incidentReference", incidentReference);
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        the_incident_reference_number_is_searched_through_incident_monitor_filter(incidentReference);
    }

    @ContinueNextStepsOnException
    @Then("^the Pending To Do monitor ((?:will|will not)) display the incidents with below status$")
    public void the_monitor_displays_incidents_with_below_status(String action, DataTable statusDataTable) throws Throwable {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();
        runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver()).get();
        testData.addStringTag("monitor", "Pending To Do");
        testData.addStringTag("section", "To Do");
        if (action.equalsIgnoreCase("will")) {

            for (String status : statusDataTable.asList(String.class)) {

                if (runtimeState.monitorsSettings.isSettingsCollapsed()) {
                    runtimeState.monitorsSettings.expandSettings();
                    helpdeskMonitorSteps.All_teams_are_selected_from_Settings();
                }

                runtimeState.monitorGrid.clickFilterIcon("ToDo");
                runtimeState.monitorGrid.enterTextInFilterbox(status);
                runtimeState.monitorGrid.clickFilterButton();

                Integer incidentReference = dbHelperMonitors.getIncidentReferenceNumberFromMonitor("Pending To Do", status);
                if (incidentReference == null) {
                    runtimeState.scenario.write("Assertion no incidents displayed in grid because none found in DB with status: " + status);
                    assertTrue("Unexpected rows displayed in grid", runtimeState.monitorGrid.getGrid().getRows().size() == 0);
                } else {
                    monitorHelper.searchThroughTheFilter("Incident Reference", incidentReference, "will display");
                }
                getWebDriver().navigate().refresh();
            }
        } else {
            for (String status : statusDataTable.asList(String.class)) {
                Integer incidentReference = null;
                switch (status) {

                case "Incident has been reviewed":
                case "Incident has no escalations required":
                case "Incident has no follow ups required":
                case "Escalation call back is not required":
                    incidentReference = dbHelperIncidents.getRandomReviewedIncidentWithNoEscalationNoFollowUpAndWithLoggedStatus();
                    runtimeState.scenario.write("incident Reference is: " + incidentReference);

                    if (incidentReference == null) {
                        throw new PendingException("No Incident found with status: " + status);
                    }
                    break;

                default:
                    throw new Exception("Unexpected incident status");
                }
                monitorHelper.searchThroughTheFilter("Incident Reference", incidentReference, "will not display");
                getWebDriver().navigate().refresh();
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor ((?:will|will not)) display the ((?:incidents|jobs)) with status \"([^\"]*)\"$")
    public void the_monitor_display_reference_no_with_status(String monitor, String action, String reference, String status) throws Throwable {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();
        runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver()).get();
        testData.addStringTag("monitor", monitor);
        runtimeState.monitorsToDo.selectMonitor(monitor);

        if (action.equals("will")) {
            Integer referenceNumber = dbHelperMonitors.getIncidentReferenceNumberFromMonitor(monitor, null);
            String header = monitor.equals("Jobs For Review") ? "Job No" : "Incident Reference";
            monitorHelper.removeAllTheFiltersFromSettings();
            monitorHelper.searchThroughTheFilter(header, referenceNumber, "will display");
        } else {
            monitorHelper.assertIncidentNotDisplayedWithStatus(localize(status));
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will display the incidents with \"([^\"]*)\" status$")
    public void the_monitor_display_incidents_with_multiple_status(String monitor, String status) throws Throwable {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();
        runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver()).get();

        testData.addStringTag("incidentMonitor", monitor);
        runtimeState.monitorsToDo.selectMonitor(monitor);

        if (status.contains(",")) {
            String[] allStatus = status.split(",");
            for (String incidentStatus : allStatus) {

                monitorHelper.setFilter("ToDo", incidentStatus.trim());
                Integer incidentReference = dbHelperMonitors.getIncidentReferenceNumberFromMonitor(monitor, incidentStatus.trim());

                if (incidentReference == null) {
                    runtimeState.scenario.write("Assertion no incidents displayed in grid because none found in DB with status: " + status);
                    outputHelper.takeScreenshots();
                    assertTrue("Unexpected rows displayed in grid", runtimeState.monitorGrid.getGrid().getRows().size() == 0);
                } else {
                    monitorHelper.searchThroughTheFilter("Incident Reference", incidentReference, "will display");
                }

                monitorHelper.clearFilter("ToDo");
            }

        } else {
            the_monitor_display_reference_no_with_status(monitor, "will", "incidents", status);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Jobs For Review monitor ((?:will|will not)) display the jobs with Forced Incident criteria with ((?:linked|no linked)) incidents$")
    public void the_monitor_will_display_the_jobs_with_Forced_Incident_criteria_but_has_no_linked_incident(String action, String status) throws Throwable {

        testData.addStringTag("incidentMonitor", "Jobs For Review");

        if (action.equals("will") && status.equals("no linked")) {
            the_monitor_display_reference_no_with_status("Jobs For Review", action, "jobs", "Jobs with no linked Incidents");

        } else if (action.equals("will not") && status.equals("linked")) {
            Integer jobReference = dbHelperJobs.getJobReferenceNumberWithLinkedIncidents();
            runtimeState.monitorsToDo.selectMonitor(testData.getString("monitor"));
            monitorHelper.searchThroughTheFilter("Job No", jobReference, "will not display");
            getWebDriver().navigate().refresh();
        }
    }

    @And("^the user has incidents with upcoming follow up$")
    public void the_user_has_incidents_with_upcoming_follow_up() throws Throwable {

        Integer incidentReference = dbHelperIncidents.getIncidentReferenceNumberEscalationCriteria("Refrigeration Outage");
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
        helpdeskViewIncidentSteps.the_incident_is_reviewed();
        commonsteps.the_button_is_clicked("Save");

        helpdeskIncidentsFollowUpSteps.the_user_has_updated_incident_for_further_follow_up_criteria();
        helpdeskIncidentActionDropdownSteps.the_option_is_clicked_from_the_actions_dropdown("Escalate");
        helpdeskIncidentEscalationSteps.the_message_is_entered_on_escalation_screen();
        commonsteps.the_button_is_clicked("Next");

        helpdeskIncidentsFollowUpSteps.the_next_follow_up_time_is_entered();
        commonsteps.the_button_is_clicked("Send Escalations");
        runtimeState.helpdeskHomePage.closeActiveTab();
        runtimeState.helpdeskHomePage.selectHomeTab();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" monitor will display only the incidents where the follow up time is greater than \"([^\"]*)\" minutes$")
    public void the_monitor_will_display_only_the_incidents_where_the_follow_up_time_is_greater_than_minutes(String monitor, String time) throws Throwable {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();
        runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver()).get();
        runtimeState.monitorsForInfo.selectMonitor(monitor);
        outputHelper.takeScreenshots();
        monitorHelper.removeAllTheFiltersFromSettings();
        Integer incidentReference = dbHelperMonitors.getIncidentReferenceNumberFromMonitor(monitor, null);
        monitorHelper.searchThroughTheFilter("Incident Reference", incidentReference, "will display");
    }
}