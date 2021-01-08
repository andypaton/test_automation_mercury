package mercury.steps.helpdesk.monitors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Constants.THREE_MINUTES;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.DataTable;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperTimeZone;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.monitors.MonitorGrid;
import mercury.pageobject.web.helpdesk.monitors.MonitorsForInfo;
import mercury.pageobject.web.helpdesk.monitors.MonitorsSettings;
import mercury.pageobject.web.helpdesk.monitors.MonitorsToDo;
import mercury.runtime.RuntimeState;

public class HelpdeskMonitorSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;
    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private TileSteps tileSteps;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelper dbHelper;

    private static final Logger logger = LogManager.getLogger();


    @Given("^the user clicks an incidents monitor$")
    public void the_user_clicks_an_incidents_monitor() throws Throwable {
        runtimeState.helpdeskIncidentMonitorPage = runtimeState.helpdeskSearchBar.clickIncidentMonitor();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" page is displayed with the user profiles configured monitor tiles$")
    public void the_page_is_displayed_with_the_user_profiles_configured_monitor_tiles(String arg1) throws Throwable {
        List<String> monitors = dbHelperMonitors.getMonitors(testData.getInt("userProfileId"));
        testData.put("monitors", monitors.toString().replace("[","").replace("]","") );
        runtimeState.scenario.write("Configured monitors for userProfileId " + testData.getString("userProfileId") + ": " + monitors.toString());
        for (String tile : monitors) {
            logger.debug("Asserting " + tile);
            assertTrue(runtimeState.helpdeskHomePage.isTileDisplayed(tile));
        }
        assertTrue("Unexpected number of moditors displayed", monitors.size() == runtimeState.helpdeskHomePage.getNumberOfMonitorTiles());
    }

    @ContinueNextStepsOnException
    @Then("^if Helpdesk is IN HOURS then the job is now sitting in the \"([^\"]*)\" monitor, else the \"([^\"]*)\" monitor$")
    public void the_new_job_is(String inHoursMonitor, String outOfHoursMonitor) throws Exception {
        int jobReference = testData.getInt("jobReference");
        if (dbHelperTimeZone.isHelpdeskOutOfHours()) {
            runtimeState.scenario.write("Helpdesk is Out Of Hours");
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobEtaExceedsSlaOutOfHours", jobReference));
        } else {
            runtimeState.scenario.write("Helpdesk is In Hours");
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperJobs.isJobOnMonitor("dbo.uvw_MonitorJobAwaitingAcceptance", jobReference));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the job is (?:still |now |)sitting in the \"([^\"]*)\" monitor$")
    public void the_new_job_is(String jobMonitor) throws Exception {

        if ( !dbHelperMonitors.isMonitorActive(jobMonitor, "Job") ) {
            runtimeState.scenario.write("Assertion skipped - monitor is NOT active: " + jobMonitor);
        } else {

            int jobReference = testData.getInt("jobReference");
            String actualMonitors = null;
            try {
                if (jobMonitor.contains("/")) {
                    String[] monitors = jobMonitor.split("/");
                    if (monitors.length == 2) {
                        actualMonitors = await().atMost(THREE_MINUTES, SECONDS).until( () -> dbHelperJobs.getJobMonitor(jobReference), anyOf(containsString(monitors[0].trim()), containsString(monitors[1].trim())) );
                    } else if (monitors.length == 3) {
                        actualMonitors = await().atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperJobs.getJobMonitor(jobReference), anyOf(containsString(monitors[0].trim()), containsString(monitors[1].trim()), containsString(monitors[2].trim())));
                    } else {
                        actualMonitors = await().atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperJobs.getJobMonitor(jobReference), anyOf(containsString(monitors[0].trim()), containsString(monitors[1].trim()), containsString(monitors[2].trim()), containsString(monitors[3].trim())));
                    }
                    runtimeState.scenario.write("Job " + jobReference + " is in the " +  dbHelperJobs.getJobMonitor(jobReference) + " monitor");
                } else {
                    actualMonitors = await().atMost(THREE_MINUTES, SECONDS).pollInterval(POLLING_INTERVAL, MILLISECONDS).until( () -> dbHelperJobs.getJobMonitor(jobReference), containsString(jobMonitor));
                }
                runtimeState.scenario.write("Job found in monitors: " + actualMonitors);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^each tile contains totals for MY TEAM and NATIONAL:$")
    public void each_tile_contains_totals_for_MY_TEAM_and_NATIONAL(Map<String, String> dataTable) throws Throwable {
        for (String tile : dataTable.keySet()) {
            Integer myTeamToDo = null;
            Integer nationalToDo = null;
            Integer myTeamForInfo = null;
            Integer nationalForInfo = null;

            if (dataTable.get(tile).contains("To Do")) {
                myTeamToDo = runtimeState.helpdeskHomePage.getMyTeamToDoCount(tile);
                nationalToDo = runtimeState.helpdeskHomePage.getNationalToDoCount(tile);
            }
            if (dataTable.get(tile).contains("For Info")) {
                myTeamForInfo = runtimeState.helpdeskHomePage.getMyTeamForInfoCount(tile);
                nationalForInfo = runtimeState.helpdeskHomePage.getNationalForInfoCount(tile);
            }

            if (dataTable.get(tile).contains("To Do")) {
                assertNotNull("My Team To Do count not displayed: " + tile, myTeamToDo);
                assertNotNull("National To Do count not displayed: " + tile, nationalToDo);
            } else {
                assertNull("My Team For Info count displayed: " + tile, myTeamToDo);
                assertNull("National For Info count displayed: " + tile, nationalToDo);
            }

            if (dataTable.get(tile).contains("For Info")) {
                assertNotNull("My Team For Info count not displayed: " + tile, myTeamForInfo);
                assertNotNull("National For Info count not displayed: ", nationalForInfo);
            } else {
                assertNull("My Team For Info count displayed: " + tile, myTeamForInfo);
                assertNull("National For Info count displayed: " + tile, nationalForInfo);
            }
        }
    }

    @Then("^the My List and National total counts for each tile matches with the counts on each monitor tab$")
    public void the_my_list_and_national_total_counts_on_home_tile_matches_with_counts_on_monitor_tab() throws Throwable {

        List<String> monitors = Arrays.asList(testData.getString("monitors").split("\\s*,\\s*"));

        for (String homeMonitor : monitors) {

            runtimeState.scenario.write("*************************************");
            runtimeState.scenario.write("Asserting " + homeMonitor + " monitor");
            runtimeState.scenario.write("*************************************");

            Integer myTeamToDo = null;
            Integer nationalToDo = null;
            Integer myTeamForInfo = null;
            Integer nationalForInfo = null;
            Integer myListCount = null;
            Integer teamListCount = null;

            List<String> monitorSections = dbHelperMonitors.getMonitorSectionNames(homeMonitor);
            if (monitorSections != null && !monitorSections.isEmpty()) {
                for (String sectionName : monitorSections) {

                    runtimeState.scenario.write("Asserting " + sectionName + " section for " + homeMonitor + " monitor");
                    List<String> monitornames = dbHelperMonitors.getMonitornames(homeMonitor, sectionName);

                    Integer myListTotalCount = 0;
                    Integer nationalTotalCount = 0;

                    if (sectionName.contains("To Do")) {
                        myTeamToDo = runtimeState.helpdeskHomePage.getMyTeamToDoCount(homeMonitor);
                        nationalToDo = runtimeState.helpdeskHomePage.getNationalToDoCount(homeMonitor);
                    } else {
                        myTeamForInfo = runtimeState.helpdeskHomePage.getMyTeamForInfoCount(homeMonitor);
                        nationalForInfo = runtimeState.helpdeskHomePage.getNationalForInfoCount(homeMonitor);
                    }

                    tileSteps.a_random_tile_is_selected(homeMonitor);

                    runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver());
                    runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver());

                    for (String monitor : monitornames) {


                        if (sectionName.contains("To Do")) {
                            myListCount = runtimeState.monitorsToDo.getMyListCount(monitor);
                            teamListCount = runtimeState.monitorsToDo.getTeamListCount(monitor);
                        } else {
                            myListCount = runtimeState.monitorsForInfo.getMyListCount(monitor);
                            teamListCount = runtimeState.monitorsForInfo.getTeamListCount(monitor);
                        }
                        runtimeState.scenario.write("Total My List Count: " + myListTotalCount + "+" + myListCount + "(added " + monitor + " monitor count for My list)");
                        runtimeState.scenario.write("Total Team List Count: " + nationalTotalCount + "+" + teamListCount + "(added " + monitor + " monitor count for Team List)");

                        myListTotalCount = myListTotalCount + myListCount;
                        nationalTotalCount = nationalTotalCount + teamListCount;
                    }

                    if (sectionName.contains("To Do")) {
                        assertEquals("The My Team count is not matched", myTeamToDo, myListTotalCount);
                        assertEquals("The National count is not matched", nationalToDo, nationalTotalCount);
                    } else {
                        assertEquals("The My Team count is not matched", myTeamForInfo, myListTotalCount);
                        assertEquals("The National count is not matched", nationalForInfo, nationalTotalCount);
                    }
                    runtimeState.helpdeskHomePage.closeActiveTab();
                }
            }
        }

    }

    @ContinueNextStepsOnException
    @Then("^each tile contains TO DO and FOR INFO totals for MY TEAM and NATIONAL$")
    public void each_tile_contains_TO_DO_and_FOR_INFO_totals_for_MY_TEAM_and_NATIONAL() throws Throwable {

        // Remove whitespace and split by comma
        List<String> monitors = Arrays.asList(testData.getString("monitors").split("\\s*,\\s*"));

        for (String monitor : monitors) {
            Integer myTeamToDo = null;
            Integer nationalToDo = null;
            Integer myTeamForInfo = null;
            Integer nationalForInfo = null;

            runtimeState.scenario.write("Asserting TO DO and FOR INFO counts are displayed for monitor: " + monitor);
            if (!monitor.equalsIgnoreCase("Admin") && !monitor.equalsIgnoreCase("Focus")) {
                myTeamToDo = runtimeState.helpdeskHomePage.getMyTeamToDoCount(monitor);
                nationalToDo = runtimeState.helpdeskHomePage.getNationalToDoCount(monitor);
                assertNotNull("My Team To Do count not displayed", myTeamToDo);
                assertNotNull("National To Do count not displayed", nationalToDo);
            }

            if (!monitor.equalsIgnoreCase("Focus") && !monitor.equalsIgnoreCase("Bureau")) {
                myTeamForInfo = runtimeState.helpdeskHomePage.getMyTeamForInfoCount(monitor);
                nationalForInfo = runtimeState.helpdeskHomePage.getNationalForInfoCount(monitor);
                assertNotNull("My Team For Info count not displayed", myTeamForInfo);
                assertNotNull("National For Info count not displayed", nationalForInfo);
            }
        }
    }

    @When("^a random tile is selected$")
    public void a_random_tile_is_selected() throws Throwable {
        // select random tile where ToDo or ForInfo > 0
        List<String> tiles = runtimeState.helpdeskHomePage.getTiles();
        Collections.shuffle(tiles);

        String[] toDoOnly = {"Bureau", "Focus"};
        String[] forInfoOnly = {"Admin"};

        for (String tile : tiles) {

            if (!tile.equalsIgnoreCase("Focus")) {
                int nationalToDoCount = Arrays.asList(forInfoOnly).contains(tile) ? 0 : runtimeState.helpdeskHomePage.getNationalToDoCount(tile);        // Admin does not display ToDo counts
                int nationalForInfoCount = Arrays.asList(toDoOnly).contains(tile) ? 0 : runtimeState.helpdeskHomePage.getNationalForInfoCount(tile); // Bureau does not display ForInfo counts

                if (nationalToDoCount > 0 || nationalForInfoCount > 0) {
                    runtimeState.scenario.write("Selecting " + tile + " tile.");
                    testData.put("tile", tile);
                    runtimeState.helpdeskHomePage.selectTile(tile);
                    break;
                }
            }
        }
        runtimeState.monitorsSettings = new MonitorsSettings(getWebDriver());
        runtimeState.monitorsSettings.expandSettings();
    }

    @ContinueNextStepsOnException
    @Then("^the selected tile's monitors tab has focus$")
    public void the_selected_tiles_monitor_is_displayed() throws Throwable {
        String monitor = testData.getString("tile") + " Monitors";
        assertTrue("Expected " + monitor + " tab to have focus", runtimeState.helpdeskHomePage.isTabActive(monitor));
    }

    @ContinueNextStepsOnException
    @Then("^only one tab for the selected tile can be opened at any time$")
    public void only_one_tab_for_the_selected_tile_can_be_opened_at_any_time() throws Throwable {
        String tile = testData.getString("tile");
        runtimeState.scenario.write("Selecting " + tile + " tile from the Home tab again!");
        runtimeState.helpdeskHomePage.selectHomeTab();
        runtimeState.helpdeskHomePage.selectTile(tile);

        // get number of tabs for our monitor
        long numberOfTabs =  runtimeState.helpdeskHomePage.getTabs()
                .stream()
                .filter(p -> p.equals(tile + " Monitors"))
                .count();

        runtimeState.scenario.write("Asserting only 1 " + tile + " Monitors tab is open");
        assertTrue(numberOfTabs == 1);
    }

    @ContinueNextStepsOnException
    @Then("^the first monitor in the \"([^\"]*)\" section is displayed$")
    public void the_first_monitor_in_the_section_is_displayed(String section) throws Throwable {
        List<String> monitors;
        if ("To Do".equals(section)) {
            runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver());
            monitors = runtimeState.monitorsToDo.getMonitors();
        } else {
            runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver());
            monitors = runtimeState.monitorsForInfo.getMonitors();
        }
        runtimeState.scenario.write("First monitor in " + section + " list is " + monitors.get(0));

        runtimeState.monitorGrid = new MonitorGrid(getWebDriver());
        runtimeState.scenario.write("Asserting " + monitors.get(0) + " title is displayed");
        testData.addStringTag("firstMonitor", monitors.get(0));
        assertEquals("Expected " + monitors.get(0) + " monitor to be displayed", monitors.get(0).trim(), runtimeState.monitorGrid.getMonitorName().trim());
    }

    @ContinueNextStepsOnException
    @Then("^the first monitor in the \"([^\"]*)\" section is displayed by default$")
    public void the_first_monitor_in_the_section_is_displayed_by_default(String section) throws Throwable {
        the_first_monitor_in_the_section_is_displayed(section);
        String activeMonitorName = runtimeState.monitorGrid.getActiveMonitor();
        assertEquals("Expected " + testData.getString("firstMonitor") + " monitor to be displayed", testData.getString("firstMonitor"), activeMonitorName);
    }

    @ContinueNextStepsOnException
    @Then("^the monitor displays \"([^\"]*)\" sections$")
    public void the_monitor_displays_sections(String sections) throws Throwable {
        String[] parts = sections.split(",");
        for (String part : parts) {
            switch (part.trim()) {
            case "Settings":
                runtimeState.monitorsSettings = new MonitorsSettings(getWebDriver());
                assertTrue("Settings section not displayed", runtimeState.monitorsSettings.isDisplayed());
                break;
            case "To Do":
                runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver());
                assertTrue("To Do section not displayed", runtimeState.monitorsToDo.isDisplayed());
                break;
            case "For Info":
                runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver());
                assertTrue("For Info section not displayed", runtimeState.monitorsForInfo.isDisplayed());
                break;
            default:
                throw new Exception("Unknown section: " + part);
            }
        }
    }

    private Grid getMonitorGrid() {
        if (testData.get("grid") == null) {
            testData.put("grid", runtimeState.monitorGrid.getGrid());
        }
        return runtimeState.monitorGrid.getGrid();
    }

    @ContinueNextStepsOnException
    @Then("^the My List \"([^\"]*)\" count matches the number of rows displayed$")
    public void the_My_List_count_matches_the_number_of_rows_displayed(String section) throws Throwable {
        String monitor = runtimeState.monitorGrid.getMonitorName();
        Grid grid = getMonitorGrid();
        int numRowsDisplayed = grid.getRows().size();
        runtimeState.scenario.write("Number of rows in grid = " + numRowsDisplayed);

        int myListCount;
        if ("For Info".equals(section)) {
            runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver());
            myListCount = runtimeState.monitorsForInfo.getMyListCount(monitor);
        } else {
            runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver());
            myListCount = runtimeState.monitorsToDo.getMyListCount(monitor);
        }
        assertTrue("My List To Do = " + myListCount + ", Number of Rows displayed = " + numRowsDisplayed, myListCount == numRowsDisplayed);
    }

    @ContinueNextStepsOnException
    @Then("^the users settings are defaulted to include logged in user's team and all Asset Types$")
    public void the_users_settings_are_defaulted_to_include_logged_in_users_team_and_all_asset_types() throws Throwable {
        String teamName = testData.getString("team");
        List<String> teams = runtimeState.monitorsSettings.getTeams();
        assertTrue("Expected Team (" + teams.toString() + ") to include: " + teamName, teams.toString().contains(teamName));

        List<String> assetTypes = runtimeState.monitorsSettings.getAssetTypes();
        assertTrue("Expected exactly 1 Asset Type to be in Settings", assetTypes.size() == 1);
        assertEquals("Expected Asset Type to be: All", "[All]", assetTypes.get(0));
    }

    @ContinueNextStepsOnException
    @Then("the monitors only display rows matching the settings")
    public void the_monitors_only_displays_rows_matching_the_settings() {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver());

        List<String> organisation = dbHelperMonitors.getOrganisation();

        List<String> teams = runtimeState.monitorsSettings.getTeams();
        List<String> sites = dbHelperSites.getSitesInTeams(teams);

        Grid grid = getMonitorGrid();
        for (Row row : grid.getRows()) {
            if (grid.getHeaders().contains("Division")) {
                String division = row.getCell("Division").getText();
                if ( ! isDivisionInTeam(teams, division, organisation) ) {
                    runtimeState.scenario.write("Row: " + row.getCells().toString());
                    fail("Division (" + division + ") is not in Team (" + teams.toString() + ")");
                }
            }

            if (grid.getHeaders().contains("Site")) {
                String site = row.getCell("Site").getText();
                if ( ! sites.contains(site) ) {
                    runtimeState.scenario.write("Row: " + row.getCells().toString());
                    fail("Site (" + site + ") is not in Team " + teams.toString());
                }
            }
        }

        if (!"Incidents".contains(testData.getString("tile"))) {
            List<String> assetTypes = runtimeState.monitorsSettings.getAssetTypes();
            if (!assetTypes.contains("[All]")) {
                for (Row row : runtimeState.monitorGrid.getGrid().getRows()) {
                    String assetType = row.getCell("Asset Type").getText();
                    if ( !assetTypes.contains(assetType) && !assetType.isEmpty()) {
                        runtimeState.scenario.write("Row: " + row.getCells().toString());
                        fail("Expected Asset Type to be one of: " + assetTypes.toString());
                    }
                }
            }
        }
    }

    private Boolean isDivisionInTeam(List<String> teams, String division, List<String> organisation) {
        if (!teams.toString().contains(division)) {
            boolean found = false;
            for (String org : organisation) {
                for (String team : teams) {
                    if (org.matches(team.trim() + ",.*" + division + ".*")) {
                        found = true;
                        break; // the division IS in one of the filtered teams
                    }
                }
            }

            if ( !found ) {
                return false;
            }
        }
        return true;
    }


    private String listToString(List<String> myList) {
        return myList.toString().replace("[", "").replace("]", "");
    }

    @ContinueNextStepsOnException
    @Then("the monitors (?:display|only display) rows (?:for|for the) \"([^\"]*)\" teams?")
    public void the_monitors_display_rows_for_teams(String teamNames) throws Exception {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver());

        List<String> expectedTeams = Arrays.asList(teamNames.split(","));

        if ("both".equals(teamNames)) {
            expectedTeams = Arrays.asList(testData.getString("team"), testData.getString("additionalTeam"));
        } else if ("newly added".equals(teamNames)) {
            expectedTeams = Arrays.asList(testData.getString("additionalTeam"));
        }

        if (!"All".equals(teamNames)) {
            List<String> organisation = dbHelperMonitors.getOrganisation();
            List<String> sites = dbHelperSites.getSitesInTeams(expectedTeams);

            Grid grid = getMonitorGrid();

            List<Row> rows = grid.getRows();
            for (Row row : rows) {

                if (grid.getHeaders().contains("Site")) {
                    String site = row.getCell("Site").getText();
                    if ( ! sites.contains(site) ) {
                        runtimeState.scenario.write("Row: " + row.getCells().toString());
                        fail("Site (" + site + ") is not in Team (" + expectedTeams + ")");
                    }

                } else if (grid.getHeaders().contains("Division")) {
                    String division = row.getCell("Division").getText();
                    if ( ! isDivisionInTeam(expectedTeams, division, organisation) ) {
                        runtimeState.scenario.write("Row: " + row.getCells().toString());
                        fail("Division (" + division + ") is not in Team (" + expectedTeams + ")");
                    }
                }

            }
        }

        List<String> filteredTeams = runtimeState.monitorsSettings.getTeams();
        for (String expectedTeam : expectedTeams) {
            assertTrue("Expected team (" + expectedTeam + ") to be in the filtered settings", filteredTeams.toString().contains(expectedTeam));
        }

    }

    @And("^(?:a random|another) team is added to Settings$")
    public void another_team_is_added_to_settings() throws Throwable {
        String teamName = dbHelper.getRandomTeamNameWhichIsNot(testData.getString("team"));
        testData.put("additionalTeam", teamName);
        if ( ! runtimeState.monitorsSettings.getTeams().contains(teamName) ) {
            runtimeState.monitorsSettings.addTeam(teamName);
        }
        runtimeState.scenario.write("Added " + teamName + " team to settings");
    }

    @Given("All teams are selected from Settings")
    public void All_teams_are_selected_from_Settings() {
        // All teams are selected by removing all filtered teams
        List<String> teams = runtimeState.monitorsSettings.getTeams();
        for (String team : teams) {
            if (!"[All]".equals(team)) {
                runtimeState.monitorsSettings.removeTeam(team);
            }
        }
    }

    @And("^the default team is removed from Settings$")
    public void the_default_team_is_removed_from_settings() throws Throwable {
        runtimeState.monitorsSettings.removeTeam(testData.getString("team"));
        runtimeState.scenario.write(testData.getString("team") + " team is removed from settings.");
    }

    @Given("an Asset Type from the displayed monitor is added to Settings")
    public void an_Asset_Type_from_the_displayed_monitor_is_added_to_Settings() {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver());
        String assetType = getMonitorGrid().getRows().get(0).getCell("Asset Type").getText();
        runtimeState.monitorsSettings.addAssetType(assetType);
    }

    @Given("multiple Asset Types from the displayed monitor are added to Settings")
    public void multiple_Asset_Types_from_the_displayed_monitor_are_added_to_Settings() {
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver());
        List<Row> rows = runtimeState.monitorGrid.getGrid().getRows();
        List<String> assetTypes = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            String assetType = rows.get(i).getCell("Asset Type").getText();

            if ( ! assetTypes.contains(assetType)) {
                assetTypes.add(assetType);
                runtimeState.monitorsSettings.addAssetType(assetType);
            }

            if (assetTypes.size() > 1) {
                runtimeState.scenario.write("Filtering on asset types: " + assetTypes.toString());
                break;
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("the monitors only displays rows for the matching Asset Types?")
    public void the_monitors_only_displays_rows_for_the_matching_Asset_Type() throws Exception {
        List<String> assetTypes = runtimeState.monitorsSettings.getAssetTypes();
        the_monitors_display_rows_for_asset_Types(listToString(assetTypes));
    }

    @Given("all Asset Types are removed from Settings")
    public void all_Asset_Types_are_removed_from_Settings() {
        List<String> assetTypes = runtimeState.monitorsSettings.getAssetTypes();
        for (String assetType : assetTypes) {
            if ( ! "All".equals(assetType) ) {
                runtimeState.monitorsSettings.removeAssetType(assetType);
            }
        }
    }

    @Given("an Asset Type is removed from Settings")
    public void an_Asset_Type_is_removed_from_Settings() {
        List<String> assetTypes = runtimeState.monitorsSettings.getAssetTypes();
        String assetType = assetTypes.get(RandomUtils.nextInt(0, assetTypes.size()-1));
        runtimeState.scenario.write("Removing asset type: " + assetType);
        runtimeState.monitorsSettings.removeAssetType(assetType);
    }

    @ContinueNextStepsOnException
    @Then("the monitors display rows for \"([^\"]*)\" Asset Types?")
    public void the_monitors_display_rows_for_asset_Types(String assetTypes) throws Exception {

        List<String> expectedAssetTypes = Arrays.asList(assetTypes.split(","));

        if ( !assetTypes.contains("All") ) {
            for (Row row : getMonitorGrid().getRows()) {
                String assetType = row.getCell("Asset Type").getText();
                if ( ! assetTypes.contains(assetType) ) {
                    runtimeState.scenario.write("Row: " + row.getCells().toString());
                    fail("Expected Asset Type to be one of: " + assetTypes);
                }
            }
        }

        List<String> filteredAssetTypes = runtimeState.monitorsSettings.getAssetTypes();
        for (String expectedAssetType : expectedAssetTypes) {
            assertTrue("Expected asset type (" + expectedAssetType + ") to be in the filtered settings", filteredAssetTypes.toString().contains(expectedAssetType));
        }
    }

    @ContinueNextStepsOnException
    @Then("the My List count for the active monitor matches the number of rows in the monitor table")
    public void the_My_List_count_for_the_active_monitor_matches_the_number_of_rows_in_the_monitor_table() {
        Grid grid = getMonitorGrid();

        int numRowsDisplayed = grid.getRows().size();
        runtimeState.scenario.write("Number of rows in grid = " + numRowsDisplayed);

        // commented this assertion until MCP-11455 gets resolved

        // String monitorName = runtimeState.monitorGrid.getActiveMonitor();
        // int myListCount = runtimeState.monitorGrid.getActiveMonitorMyListCount();
        // assertTrue(monitorName + " My List = " + myListCount + ", Number of Rows displayed = " + numRowsDisplayed,
        // myListCount == numRowsDisplayed);
    }

    @When("^the \"([^\"]*)\" monitor is selected from \"([^\"]*)\" section$")
    public void the_monitor_is_selected_from_section(String monitor, String section) throws Throwable {
        String helpdeskHours = dbHelperTimeZone.isHelpdeskOutOfHours() ? "Out Of Hours" : "In Hours";
        runtimeState.scenario.write("Helpdesk is currently: " + helpdeskHours);

        testData.addStringTag("monitor", monitor);
        testData.addStringTag("section", section);

        if (section.equals("To Do")) {
            runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver()).get();
            runtimeState.monitorsToDo.selectMonitor(monitor);

        } else if (section.equals("For Info")) {
            runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver()).get();
            runtimeState.monitorsForInfo.selectMonitor(monitor);
        }
    }

    @When("^the \"([^\"]*)\" are collapsable$")
    public void the_sections_are_collapsable(String sections) throws Throwable {
        String[] parts = sections.split(",");

        for (String part : parts) {
            switch (part.trim()) {

            case "To Do":
                runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver());
                assertTrue("The To Do section is not collapsable", runtimeState.monitorsToDo.collapse());
                break;

            case "For Info":
                runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver());
                assertTrue("The For Info section is not collapsable", runtimeState.monitorsForInfo.collapse());
                break;

            default:
                throw new Exception("Unknown section: " + part);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^for each monitor the following sections are collapsable$")
    public void the_each_monitor_the_following_sections_are_collapsable(DataTable dataTable) throws Throwable {

        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            String monitor = row.get("monitor");
            String sections = row.get("sections");
            tileSteps.a_random_tile_is_selected(monitor);
            the_sections_are_collapsable(sections);
            runtimeState.helpdeskHomePage.closeActiveTab();
        }
    }

    @When("^the \"([^\"]*)\" monitor displays My List and Team List heading for the \"([^\"]*)\" sections$")
    public void the_tile_displays_my_list_and_team_list_headings_for_the_sections(String monitor, String sections) throws Throwable {
        String[] parts = sections.split(",");

        for (String part : parts) {
            switch (part.trim()) {

            case "To Do":
                runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver());
                assertTrue("The heading My List is not displayed in To Do section of " + monitor + " monitor ", runtimeState.monitorsToDo.isMyListDisplayed());
                assertTrue("The heading Team List is not displayed in To Do section of " + monitor + " monitor ", runtimeState.monitorsToDo.isTeamListDisplayed());
                break;

            case "For Info":
                runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver());
                assertTrue("The heading My List is not displayed in For Info section of " + monitor + " monitor", runtimeState.monitorsForInfo.isMyListDisplayed());
                assertTrue("The heading Team List is not displayed in For Info section of " + monitor + " monitor", runtimeState.monitorsForInfo.isTeamListDisplayed());
                break;

            default:
                throw new Exception("Unknown section: " + part);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the each monitor displays My List and Team List heading for the below sections$")
    public void the_each_monitor_displays_my_list_and_team_list_heading_for_the_below_sections(DataTable dataTable) throws Throwable {

        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            String monitor = row.get("monitor");
            String sections = row.get("sections");
            tileSteps.a_random_tile_is_selected(monitor);
            the_tile_displays_my_list_and_team_list_headings_for_the_sections(monitor, sections);
            runtimeState.helpdeskHomePage.closeActiveTab();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" \"([^\"]*)\" team count reflects the count as per the monitor grid and filters added by user$")
    public void the_team_count_reflects_the_count_as_per_the_monitor_grid_and_filters_added_by_user(String section, String team) throws Throwable {
        Integer jobReference = null;
        // Asserting the Team Count
        runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();

        /*
         * Commenting out the code to assert the count until the bug MCP-7255 is fixed
         *
         * int count = monitorHelper.getTeamCountBasedOnTheSection(section, team);
         * Grid grid = runtimeState.monitorGrid.getGrid(); List<Row> row =
         * grid.getRows(); runtimeState.scenario.write("Asserting the " + team +
         * " count"); monitorHelper.assertGridCount(count, row.size());
         */

        outputHelper.takeScreenshots();

        // Remove all the Teams and assert the count
        if (team.equals("My List")) {
            All_teams_are_selected_from_Settings();
            runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();

            /*
             * Commenting out the code to assert the count until the bug MCP-7255 is fixed
             *
             * int count = monitorHelper.getTeamCountBasedOnTheSection(section, team);
             * Grid gridAfterRemovingAllTeams = runtimeState.monitorGrid.getGrid();
             * List<Row> rowsAfterRemovingAllTeams = gridAfterRemovingAllTeams.getRows();
             * runtimeState.scenario.write("Asserting the " + team +
             * " count after removing all the teams"); monitorHelper.assertGridCount(count,
             * rowsAfterRemovingAllTeams.size());
             */
            outputHelper.takeScreenshots();
        } else {
            All_teams_are_selected_from_Settings();
        }

        // Asserting the grid count after adding an filter
        if (testData.getString("tile").equals("One Stop Shop") || testData.getString("tile").equals("Managed Contracts")) {
            String monitor = testData.getString("section").equals("To Do") ? testData.getString("monitor") + " To Do"
                    : testData.getString("monitor") + " For Info";
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor(monitor, null, null);
        } else {
            jobReference = dbHelperMonitors.getJobReferenceFromMonitor(testData.getString("monitor"), null, null);
        }
        runtimeState.monitorGrid.clickFilterIcon("Job ID");
        runtimeState.monitorGrid.enterTextInFilterbox(String.valueOf(jobReference));
        runtimeState.monitorGrid.clickFilterButton();

        /*
         * Commenting out the code to assert the count until the bug MCP-7255 is fixed
         *
         * Grid gridNew = runtimeState.monitorGrid.getGrid(); List<Row> rows =
         * gridNew.getRows();
         * runtimeState.scenario.write("Asserting the grid rows after adding an filter"
         * ); assertEquals("The count mismatch for filters added by user", rows.size(),
         * 1);
         */
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" section displays all configured active monitors for \"([^\"]*)\"$")
    public void the_section_displays_all_configured_active_monitors_for(String category, String area) {
        List<String> activeMonitors = dbHelperMonitors.getActiveMonitors(area, category);
        runtimeState.scenario.write("Configured active monitors: " + activeMonitors.toString());
        for (String monitor : activeMonitors) {
            if (category.equalsIgnoreCase("To Do")) {
                runtimeState.monitorsToDo = new MonitorsToDo(getWebDriver()).get();
                assertTrue("The monitor " + monitor + " is not displayed in " + category + " section", runtimeState.monitorsToDo.isMonitorDisplayed(monitor));
            } else {
                runtimeState.monitorsForInfo = new MonitorsForInfo(getWebDriver()).get();
                assertTrue("The monitor " + monitor + " is not displayed in " + category + " section", runtimeState.monitorsForInfo.isMonitorDisplayed(monitor));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the monitor key is displayed$")
    public void the_monitor_key_is_displayed() {
        assertTrue("The monitor key is not displayed for " + testData.getString("tile") + " monitor", runtimeState.monitorGrid.isMonitorKeyDisplayed());
    }
}
