package mercury.steps.portal.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperOnCallScheduler;
import mercury.runtime.RuntimeState;

public class OnCallSchedulerSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperOnCallScheduler dbHelperOnCallScheduler;
    @Autowired private OutputHelper outputHelper;
    @Autowired
    private TestData testData;


    @ContinueNextStepsOnException
    @Then("^the current week view is displayed$")
    public void the_user_is_shown_the_current_week_view() throws Throwable {
        String startOfWeek = DateHelper.getStartOfWeek("E d", dbHelperOnCallScheduler.getFirstDayOfTheWeek());
        String tableHeader = runtimeState.onCallSchedulerPage.getSchedularTableDates();
        runtimeState.scenario.write("Table header: " + tableHeader.replaceAll("\n",  " : "));
        assertTrue("Expected to default to 7 days view", tableHeader.split("\n").length == 8);
        assertTrue("Expected table header to have start of week: " + startOfWeek, tableHeader.split("\\n")[1].contains(startOfWeek));
    }

    @ContinueNextStepsOnException
    @Then("^resource profiles are available from the dropdown$")
    public void the_user_is_shown_the_resource_profile_dropdown_holding_MST_or_RHVAC_Technician() throws Throwable {
        List<String> resources = runtimeState.onCallSchedulerPage.getResourceProfiles();
        String resource = runtimeState.onCallSchedulerPage.getSelectedResourceProfile();
        runtimeState.scenario.write("Selected resource: " + resource);
        runtimeState.scenario.write("Available resources: " + resources);
        assertFalse("Resource dropdown list is empty", resources.isEmpty());
    }

    @ContinueNextStepsOnException
    @Then("^the region which is prefilled$")
    public void the_user_is_shown_the_region_which_is_prefilled_with_the_area() throws Throwable {
        String region = runtimeState.onCallSchedulerPage.getSelectedRegion();
        runtimeState.scenario.write("Selected region: " + region);
        assertFalse("Region has not been prefilled", region.isEmpty());
    }

    @ContinueNextStepsOnException
    @Then("^time and dates from oncall starting to finishing are displayed$")
    public void the_user_is_shown_the_dates_from_oncall_starting_to_when_it_finishes() throws Throwable {
        String currentlyViewing = runtimeState.onCallSchedulerPage.getCurrentlyViewing();
        runtimeState.scenario.write("Currently viewing: " + currentlyViewing.replaceAll("\n", " "));
        assertFalse("Region has not been prefilled", currentlyViewing.isEmpty());
    }

    @ContinueNextStepsOnException
    @Then("^the current week displayed can be updated$")
    public void the_current_week_displayed_can_be_updated() {
        String firstViewing = runtimeState.onCallSchedulerPage.getCurrentlyViewing();
        runtimeState.scenario.write("Currently viewing: " + firstViewing.replaceAll("\n", " "));
        outputHelper.takeScreenshot();

        // update using 'This week' back and forward arrows
        runtimeState.onCallSchedulerPage.previousWeek();
        String nowViewing = runtimeState.onCallSchedulerPage.getCurrentlyViewing();
        runtimeState.scenario.write("Updated (using back arrow) to: " + nowViewing.replaceAll("\n", " "));
        outputHelper.takeScreenshot();
        assertNotEquals(nowViewing, firstViewing);

        // jump forward 2 weeks
        runtimeState.onCallSchedulerPage.followingWeek();
        runtimeState.onCallSchedulerPage.followingWeek();
        nowViewing = runtimeState.onCallSchedulerPage.getCurrentlyViewing();
        runtimeState.scenario.write("Updated (using forward arrow) to: " + nowViewing.replaceAll("\n", " "));
        outputHelper.takeScreenshot();
        assertNotEquals(nowViewing, firstViewing);

        // update using calendar
        String newDate = DateHelper.getNowDatePlusOffset(48, "d MMM yy");
        runtimeState.onCallSchedulerPage.updateViewingFrom(newDate);
        nowViewing = runtimeState.onCallSchedulerPage.getCurrentlyViewing();
        runtimeState.scenario.write("Updated (using calendar) to: " + nowViewing.replaceAll("\n", " "));
        outputHelper.takeScreenshot();
        assertNotEquals(nowViewing, firstViewing);
    }

    @ContinueNextStepsOnException
    @Then("^Tech Positions are in a collapsed state$")
    public void Tech_Positions_are_in_a_collapsed_state() {
        int numExpandedTechPositions = runtimeState.onCallSchedulerPage.getNumExpandedTechPositions();
        assertTrue("Number of expended Tech Positions: " + numExpandedTechPositions, numExpandedTechPositions == 0);
    }

    @ContinueNextStepsOnException
    @Then("^the status for Tech Supervisors is verified for the displayed week$")
    public void the_status_for_Tech_Supervisors_is_verified_for_the_displayed_week() {
        List<String> statusRows = runtimeState.onCallSchedulerPage.getTechPositionsStatus();
        for (String row : statusRows) {
            runtimeState.scenario.write("Displayed Tech Position row: " + row);

            String[] parts = row.split(",");
            String techPosition = parts[0];

            runtimeState.onCallSchedulerPage.expandTechPosition(techPosition);

            List<String> sites = runtimeState.onCallSchedulerPage.getSitesForTechPosition(techPosition);
            runtimeState.scenario.write("Displayed Sites for Tech Position " + techPosition + ": " + sites.toString());

            for (int day = 1; day < 8; day++) {
                int scheduledRotaCount = runtimeState.onCallSchedulerPage.getScheduledRotaCountForDay(techPosition, day);
                int emptyRotaCount = runtimeState.onCallSchedulerPage.getEmptyRotaCountForDay(techPosition, day);

                runtimeState.scenario.write("Asserting Tech Position " + techPosition + parts[day] + " : " + emptyRotaCount + " empty rota entries : " + scheduledRotaCount + " scheduled rota entries");
                if (emptyRotaCount > 0) {
                    assertTrue("Expected Tech Position to be INCOMPLETE", parts[day].contains("INCOMPLETE"));
                } else {
                    assertFalse("Expected Tech Position to be COMPLETE", parts[day].contains("INCOMPLETE"));
                }
            }
        }
    }

    @When("^Rota Entries are updated for a site$")
    public void rota_Entries_are_updated_for_a_site() throws Throwable {
        List<String> statusRows = runtimeState.onCallSchedulerPage.getTechPositionsStatus();
        String firstTechPosition = statusRows.get(0);

        String[] parts = firstTechPosition.split(",");
        String techPosition = parts[0];
        runtimeState.onCallSchedulerPage.expandTechPosition(techPosition);

        List<String> sites = runtimeState.onCallSchedulerPage.getSitesForTechPosition(techPosition);
        String site = sites.get(0);

        int scheduledRotaCount = runtimeState.onCallSchedulerPage.getScheduledRotaCountForSite(techPosition, site);

        String tableHeader = runtimeState.onCallSchedulerPage.getSchedularTableDates();
        String[] headerParts = tableHeader.split("\n");

        runtimeState.scenario.write("All 7 days of the OCS week should be configured for the store count to reduce");
        if (scheduledRotaCount < 7) {
            for (int day = 1; day < 8; day++) {
                if (runtimeState.onCallSchedulerPage.isEmptyRotaEntry(techPosition, site, day)) {
                    runtimeState.scenario.write("adding rota entry for : " + techPosition + " > " + site + " > " + headerParts[day]);
                    runtimeState.onCallSchedulerPage.selectCell(techPosition, site, day);
                    addResource();
                }
            }

        } else {
            // only need to delete one entry for count on home page to be updated
            int day = 7; // ie. we are going to remove rota entry for the last day displayed
            for (int attempt = 0; attempt < 6; attempt++) {
                runtimeState.scenario.write("removing rota entry for : " + techPosition + " > " + site + " > " + headerParts[day]);
                outputHelper.takeScreenshot();
                runtimeState.onCallSchedulerPage.selectDelete(techPosition, site, day);
                outputHelper.takeScreenshot();
                runtimeState.onCallSchedulerPage.confirmDelete();
                runtimeState.onCallSchedulerPage.waitUntilEmptyRotaEntry(techPosition, site, day);
                if (runtimeState.onCallSchedulerPage.isEmptyRotaEntry(techPosition, site, day))
                    break;
            }
        }
    }

    private void addResource() {
        runtimeState.onCallSchedulerPage.selectAddRandomResource("RHVAC Technician");
        runtimeState.onCallSchedulerPage.saveNewResource();
    }

    @ContinueNextStepsOnException
    @Then("^the outstanding activities count on the Home page is updated$")
    public void the_count_on_the_Home_page_is_updated() throws Throwable {
        outputHelper.takeScreenshot();
        runtimeState.portalNavBar.clickTopLevelMenu("Home");

        String activity = testData.getString("activity");
        for (String row : runtimeState.portalSummaryPage.getGridAsString()) {
            if (row.contains(activity)) {
                runtimeState.scenario.write(row);
                String originalCount = testData.getString("activityCount");
                String countString = row.replace(activity, "").trim();
                assertNotEquals("Expected outstanding activities count to be updated", originalCount, countString);
            }
        }
    }

}
