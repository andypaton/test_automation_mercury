package mercury.steps.portal.resources;

import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.helpers.OutputHelper;
import mercury.runtime.RuntimeState;

public class PortalResourcesOnCallSummarySteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;


    @ContinueNextStepsOnException
    @Then("^the user will have visibility of the next 52 weeks oncall summary records$")
    public void the_user_will_have_visibility_of_the_next_52_weeks_oncall_summary_records() {
        // will always be 53 rows, as 365 days, divided by 7 days a week = 52.1428571,
        // so round up to 53
        Grid grid = runtimeState.onCallSummaryPage.getGrid();
        assertEquals("Incorrect Number of Rows in table ", 53, grid.getRows().size());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^entering search criteria filters the table correctly$")
    public void entering_search_criteria_filters_the_table_correctly() throws Throwable {
        Grid grid = runtimeState.onCallSummaryPage.getGrid();
        Random randomizer = new Random();
        int randomRow = randomizer.nextInt(grid.getRows().size());
        Row row = grid.getRows().get(randomRow);

        runtimeState.onCallSummaryPage.searchJobs(row.getCell("From").getText());

        Grid searchResult = runtimeState.onCallSummaryPage.getGrid();
        for (Row searchRow : searchResult.getRows()) {
            assertThat("Search did not return correct result", row.getCell("From").getText(), isOneOf(searchRow.getCell("From").getText(), searchRow.getCell("To").getText()));
        }
        outputHelper.takeScreenshots();
    }
}
