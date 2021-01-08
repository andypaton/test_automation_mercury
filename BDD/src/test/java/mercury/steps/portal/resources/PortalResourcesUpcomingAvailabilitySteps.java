package mercury.steps.portal.resources;

import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.helpers.StringHelper.normalize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.runtime.RuntimeState;


public class PortalResourcesUpcomingAvailabilitySteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;

    @ContinueNextStepsOnException
    @Then("^the resource unavailability will appear on the Line Managers Resource Unavailability screen$")
    public void the_resource_unavailability_will_appear_on_the_resource_unavailability_screen() throws Throwable {
        Date today = new Date();
        Date todayPlusTwoWeeks = DateHelper.addDays(today, 14);
        runtimeState.resourceAvailabilityPage.searchResources(testData.getString("resourceName"));
        outputHelper.takeScreenshots();
        Grid grid;
        grid = runtimeState.resourceAvailabilityPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        for (Row row : grid.getRows()) {
            Date startDate = DateHelper.stringAsDate(row.getCell("Start Date").getText(), SHORT_DATE);
            Date endDate = DateHelper.stringAsDate(row.getCell("End Date").getText(), SHORT_DATE);
            assertEquals("Resource name does not match", row.getCell("Name").getText(), normalize(testData.getString("resourceName")));
            assertTrue("invalid Dates", (((startDate.after(today) || startDate.equals(today)) & startDate.before(todayPlusTwoWeeks)) || (startDate.before(today) & (endDate.after(today) || endDate.equals(today)))));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the resource unavailability will not appear on the Line Managers Resource Unavailability screen$")
    public void the_resource_unavailability_will_not_appear_on_the_Line_Managers_resource_unavailability_screen() throws Throwable {
        Grid grid;
        grid = runtimeState.resourceAvailabilityPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        runtimeState.resourceAvailabilityPage.searchResources(testData.getString("resourceName"));
        runtimeState.scenario.write(
                "Resource availability table should not display the row for resource: " + testData.getString("resourceName")
                + " with absence start date: " + testData.getString("startDate")
                + " and end date: " + testData.getString("endDate"));
        if (grid.getRows().size() >= 1) {
            for (Row row : grid.getRows()) {
                if (row.getCell("Name").getText().equals(testData.getString("resourceName"))) {
                    assertNotEquals("Start Date cannot be same", testData.getString("startDate"), row.getCell("Start Date").getText());
                    assertNotEquals("End Date cannot be same", testData.getString("endDate"), row.getCell("End Date").getText());
                }
            }
            runtimeState.scenario.write("Record for the Resource having back up resource assigned during the absence period is not displayed in the Resource Availability page.");
        }
        outputHelper.takeScreenshots();
    }

    @When("^a backup resource is assigned$")
    public void a_backup_resource_is_assigned() throws Throwable {

        String startDate = testData.getString("startDate");
        String endDate = testData.getString("endDate");
        runtimeState.resourceAvailabilityPage.searchResources(testData.getString("resourceName"));

        Grid grid = runtimeState.resourceAvailabilityPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        if (grid.getRows().size() >= 1) {
            int index = 0;
            for (Row row : grid.getRows()) {
                if (startDate.equals(row.getCell("Start Date").getText()) & endDate.equals(row.getCell("End Date").getText())) {
                    runtimeState.resourceAvailabilityPage.clickCoveringResourceButton(index);
                    String backup = runtimeState.resourceAvailabilityPage.selectFirstCoveringResource();

                    runtimeState.scenario.write("Backup selected: " + backup);
                    outputHelper.takeScreenshots();
                    runtimeState.resourceAvailabilityPage.clickConfirmButton(index);
                    break;
                }
                index++;
            }
        }
    }

    @When("^the resource name is selected from the Resource Availability grid$")
    public void the_resource_name_is_selected_from_the_Resource_Availability_grid() throws Throwable {
        runtimeState.resourceAvailabilityPage.searchResources(testData.getString("resourceName"));
        runtimeState.jobsAwaitingReallocationForResourcePage = runtimeState.resourceAvailabilityPage.selectFirstResource();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table for that resource is displayed$")
    public void the_table_for_that_resource_is_displayed(String table) throws Throwable {
        switch (table) {
        case "Jobs Awaiting Reallocation":
            runtimeState.scenario.write("Page displayed: " +  runtimeState.jobsAwaitingReallocationForResourcePage.getPageTitle());
            String jobReference = testData.getString("jobReference");
            runtimeState.scenario.write("Asserting table contains job reference: " +  jobReference);
            runtimeState.jobsAwaitingReallocationForResourcePage.searchJobs(jobReference);
            assertTrue(runtimeState.jobsAwaitingReallocationForResourcePage.getGridAsString().get(0).contains(jobReference));
            break;

        default:
            throw new Exception ("Unknown table: " + table);
        }
    }

}
