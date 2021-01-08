package mercury.helpers;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.databuilders.TestData;
import mercury.helpers.apihelper.ApiHelperAdmin;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.helpdesk.monitors.MonitorGrid;
import mercury.runtime.RuntimeState;

@Component
public class MonitorHelper {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private ApiHelperAdmin apiHelperAdmin;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private JobCreationHelper jobCreationHelper;

    public void all_teams_are_selected() {
        // All teams are selected by removing all filtered teams
        List<String> teams = runtimeState.monitorsSettings.getTeams();
        for (String team : teams) {
            if (!"[All]".equals(team)) {
                runtimeState.monitorsSettings.removeTeam(team);
            }
        }
    }

    public void removeAllTheFiltersFromSettings() {
        if (runtimeState.monitorsSettings.isSettingsCollapsed()) {
            runtimeState.monitorsSettings.expandSettings();
        }
        all_teams_are_selected();
    }

    public void setFilter(String header, String value) {
        if (runtimeState.monitorGrid == null) {
            runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();
        }

        runtimeState.monitorGrid.clickFilterIcon(header);
        runtimeState.monitorGrid.enterTextInFilterbox(value);
        runtimeState.monitorGrid.clickFilterButton();
    }

    public void clearFilter(String header) {
        runtimeState.monitorGrid.clickFilterIcon(header);
        runtimeState.monitorGrid.clearFilter();
    }

    public void searchThroughTheFilter(String header, Integer referenceNumber, String action) throws InterruptedException {
        if (runtimeState.monitorGrid == null) {
            runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();
        }

        if (testData.getString("section").equals("To Do")) {
            runtimeState.monitorsToDo.selectMonitor(testData.getString("monitor"));
        } else {
            runtimeState.monitorsForInfo.selectMonitor(testData.getString("monitor"));
        }
        setFilter(header, String.valueOf(referenceNumber));
        Thread.sleep(1000);

        if (testData.getString("monitor").equalsIgnoreCase("Watched Jobs") && testData.getString("assertWatchedIcon").equals("True")) {
            assertTrue("The watched icon is not displayed", runtimeState.monitorGrid.isWatchedIconDisplayed(referenceNumber));
        }

        if (action.equals("will display")) {
            runtimeState.scenario.write("Asserting that the " + header + " " + referenceNumber + " is present in the grid");
            assertTrue(header + ": " + referenceNumber + " is not displayed in the monitor", runtimeState.monitorGrid.isReferenceNumberDisplayed(referenceNumber));
        } else {
            runtimeState.scenario.write("Asserting that the " + header + " " + referenceNumber + " is not present in the grid");
            assertFalse(header + ": " + referenceNumber + " is displayed in the monitor", runtimeState.monitorGrid.isReferenceNumberDisplayed(referenceNumber));
        }
        clearFilter(header);
    }
    
    public boolean isJobReferenceDisplayed(String header, Integer referenceNumber) throws Throwable {
       
        removeAllTheFiltersFromSettings();
        
        if (testData.getString("section").equals("To Do")) {
            runtimeState.monitorsToDo.selectMonitor(testData.getString("monitor"));
        } else {
            runtimeState.monitorsForInfo.selectMonitor(testData.getString("monitor"));
        }
        setFilter(header, String.valueOf(referenceNumber));
        return runtimeState.monitorGrid.isReferenceNumberDisplayed(referenceNumber);
    }

    public void assertGridCount(Integer expectedGridCount, Integer actualGridCount) {
        assertEquals("The grid count is not matched", expectedGridCount, actualGridCount);
    }

    public int getTeamCountBasedOnTheSection(String section, String team) {
        Integer count = null;

        switch (section) {

        case "To Do":
            count = team.equals("My List") ? runtimeState.monitorsToDo.getMyListCount(testData.getString("monitor"))
                    : runtimeState.monitorsToDo.getTeamListCount(testData.getString("monitor"));
            break;

        case "For Info":
            count = team.equals("My List") ? runtimeState.monitorsForInfo.getMyListCount(testData.getString("monitor"))
                    : runtimeState.monitorsForInfo.getTeamListCount(testData.getString("monitor"));
            break;
        }
        return count;
    }

    public Integer getIncidentReferenceWithStatus(String status) {
        Integer incidentReference = null;
        switch (status) {

        case "Logged":
            incidentReference = dbHelperIncidents.getRandomReviewedIncidentWithNoEscalationNoFollowUpAndWithLoggedStatus();
            break;

        case "Incident Followup":
            incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumberFollowUpWithNextFollowupRequiredCriteria("Refrigeration Outage");
            break;

        case "Cancelled":
            incidentReference = dbHelperIncidents.getIncidentReferenceWithStatusId(2);
            break;
        }
        return incidentReference;
    }

    public void assertIncidentNotDisplayedWithStatus(String status) throws InterruptedException {
        Integer incidentReference;
        if (status.contains(",")) {
            String[] parts = status.split(",");
            for (String incidentStatus : parts) {
                incidentReference = getIncidentReferenceWithStatus(incidentStatus.trim());
                searchThroughTheFilter("Incident Reference", incidentReference, "will not display");
            }
        } else {
            incidentReference = getIncidentReferenceWithStatus(status);
            searchThroughTheFilter("Incident Reference", incidentReference, "will not display");
        }
    }

    public void assertMonitorCount(String monitor, Integer expectedDbCount) {
        Integer monitorMyListCount = monitor.equals("Upcoming To Do") ? runtimeState.monitorsForInfo.getMyListCount(monitor) : runtimeState.monitorsToDo.getMyListCount(monitor);
        assertEquals("Count mismatch for " + monitor + " My List count", expectedDbCount, monitorMyListCount);
    }

    public void assertGridCount(String monitor, Integer expectedDbCount) {
        Integer actualRowCount = GridHelper.getNumberOfDisplayedRows(runtimeState.monitorGrid.getGridXpath());
        runtimeState.scenario.write("Asserting rows for " + monitor + " monitor.Expected count is: " + expectedDbCount);
        assertEquals("Count mismatch for the " + monitor + " monitor ", expectedDbCount, actualRowCount);
    }

    public Integer getCountFromDatabase(String status) throws Exception {
        Integer count = null;

        switch (status) {

        case "Incident Initial Review":
            count = dbHelperMonitors.getMonitorCount("Reviews", null, null, null);
            break;

        case "Incident Followup":
            count = dbHelperMonitors.getMonitorCount("Follow ups", null, null, null);
            break;

        case "Jobs with no linked Incidents":
            count = dbHelperMonitors.getMonitorCount("Jobs For Review", null, null, null);
            break;

        case "Immediate Escalation":
        case "Incident Telephone Escalation Callback":
        case "Pending To Do":
        case "Upcoming To Do":
            count = dbHelperMonitors.getMonitorCount(status, null, null, null);
            break;

        default:
            throw new Exception("Unexpected incident status");
        }
        return count;
    }

    public void enterTextAndClickFilterButton(String filterIcon, String text) {
        runtimeState.monitorGrid.clickFilterIcon(filterIcon);
        runtimeState.monitorGrid.enterTextInFilterbox(text);
        runtimeState.monitorGrid.clickFilterButton();
    }

    public void enableMonitor() throws Exception {
        apiHelperAdmin.updateMonitors(testData.getListMap("monitorUpdate"), "true");
    }

    public void disableMonitor() throws Exception {
        apiHelperAdmin.updateMonitors(testData.getListMap("monitorUpdate"), "false");
    }

    public int getClientStatusId(String clientStatus) {
        int clientStatusId = 0;

        switch(clientStatus) {

        case "Logged":
            clientStatusId = 1;
            break;

        case "Allocated":
            clientStatusId = 2;
            break;

        case "Fixed":
            clientStatusId = 9;
            break;

        case "Cancelled":
            clientStatusId = 10;
            break;
        }
        return clientStatusId;
    }

    public Integer getDeferredJobWithStatus(String status) throws Throwable {
        Integer jobReference = dbHelperMonitors.getJobsWithActiveDeferralEvents(status);
        if (jobReference == null) {
            testData.put("resourceId", dbHelperResources.getRandomCityTechId("City Resource"));
            mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Logged / Job Advise Deferred");
            jobReference = job.getJobReference();
        }
        return jobReference;
    }

    public void searchJobRef(Integer jobReference, boolean displayAction) throws Throwable {
        int flag = 0;

        Instant start = Instant.now();

        // Refresh the page until the job appears/disappears in the grid
        do {
            POHelper.refreshPage();

            if (testData.getString("section").equals("To Do")) {
                runtimeState.monitorsToDo.selectMonitor(testData.getString("monitor"));
            } else {
                runtimeState.monitorsForInfo.selectMonitor(testData.getString("monitor"));
            }

            removeAllTheFiltersFromSettings();
            setFilter("Job ID", String.valueOf(jobReference));

            if (displayAction) {
                flag = runtimeState.monitorGrid.isReferenceNumberDisplayed(jobReference) == true ? 1 : 0;
            } else {
                flag = runtimeState.monitorGrid.isReferenceNumberDisplayed(jobReference) == false ? 1 : 0;
            }
        } while (flag != 1 && Duration.between(start, Instant.now()).toMinutes() < 3);
    }
}