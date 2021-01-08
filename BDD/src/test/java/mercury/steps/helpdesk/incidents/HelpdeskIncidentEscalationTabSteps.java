package mercury.steps.helpdesk.incidents;

import static mercury.helpers.Globalisation.MEDIUM;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentEscalationTabSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TzHelper tzHelper;

    @ContinueNextStepsOnException
    @Then("^the Escalation grid contains \"([^\"]*)\" as \"([^\"]*)\"$")
    public void the_escalation_grid_contains(String header, String expectedValue) throws Throwable {

        runtimeState.helpdeskIncidentEscalationTab = runtimeState.helpdeskViewIncidentPage.clickEscalationsTab();
        Grid grid = runtimeState.helpdeskIncidentEscalationTab.getGrid();

        List<Row> row = grid.getRows();

        switch (header) {

        case "Update":
            assertEquals(expectedValue + " is not displayed in Escalation tab ", expectedValue, row.get(0).getCell("Update").getText());
            break;

        case "SMS":
            assertEquals(expectedValue + " is not displayed in Escalation tab ", expectedValue, row.get(0).getCell("SMS").getText());
            break;
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Escalation tab displays the correct SMS text$")
    public void the_Escalation_tab_displays_the_correct_description() throws Throwable {
        the_escalation_grid_contains("SMS",testData.getString("messageText"));
    }

    @ContinueNextStepsOnException
    @Then("^the Incident Escalations tab contains a row for \"([^\"]*)\"$")
    public void the_Incident_Escalations_tab_contains_a_row_for(String expectedUpdate) throws Throwable {
        runtimeState.helpdeskIncidentEscalationTab = runtimeState.helpdeskViewIncidentPage.clickEscalationsTab();

        runtimeState.helpdeskIncidentEscalationTab.tableSort("On");
        Grid grid = runtimeState.helpdeskIncidentEscalationTab.getGrid();
        List<Row> row = grid.getRows();

        String actualTime = row.get(0).getCell("On").getText().replaceAll(" ST", "");
        String actualUpdate = row.get(0).getCell("Update").getText();
        String actualSMS = row.get(0).getCell("SMS").getText();
        String actualDescription = row.get(0).getCell("Description").getText();
        String actualUser = row.get(0).getCell("By").getText();

        Date eventActualTimeAsDate = DateHelper.stringAsDate(actualTime, MEDIUM);
        String actualEventTime = DateHelper.dateAsString(eventActualTimeAsDate, "hh:mm a");
        String actualEventDate =  DateHelper.dateAsString(eventActualTimeAsDate, "dd MMM yyyy");

        int siteId = dbHelperSites.getSiteId(runtimeState.helpdeskViewIncidentPage.getSiteValue());
        Date storeDate = tzHelper.getCurrentTimeAtSite(siteId);
        String expectedCurrentTime = DateHelper.dateAsString(storeDate, "hh:mm a");
        String expectedCurrentDate = DateHelper.dateAsString(storeDate, "dd MMM yyyy");

        String expectedSMS = testData.getString("messageText");
        String expectedDescription = runtimeState.helpdeskViewIncidentPage.getDescriptionValue();
        String expectedUser = testData.getString("loginUser");

        long diff = DateHelper.getDifferenceBetweenTwoTimes(actualEventTime, expectedCurrentTime);
        long differenceInMinutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) % 60;

        runtimeState.scenario.write("Asserting Date and Time is: " + expectedCurrentDate + " " + expectedCurrentTime);
        assertEquals("Looks like " + expectedCurrentDate + "is not dispalyed in Escalation tab ",expectedCurrentDate, actualEventDate);
        assertTrue("current time (" + expectedCurrentTime + ") - Store event time (" + actualEventTime + ") > 1 minute", differenceInMinutes < 2);

        runtimeState.scenario.write("Asserting update is: " + expectedUpdate);
        assertEquals("Looks like "+expectedUpdate +" is not dispalyed in Escalation tab ",expectedUpdate, actualUpdate);

        runtimeState.scenario.write("Asserting SMS is: " + expectedSMS);
        assertEquals("Looks like "+expectedSMS +" is not dispalyed in Escalation tab ",expectedSMS, actualSMS);

        runtimeState.scenario.write("Asserting Description is: " + expectedDescription);
        assertEquals("Looks like "+expectedDescription +" is not dispalyed in Escalation tab ", expectedDescription, actualDescription);

        runtimeState.scenario.write("Asserting User is: " + expectedUser);
        assertTrue("Looks like "+expectedUser +" is not dispalyed in Escalation tab ", expectedUser.contains(actualUser));

        outputHelper.takeScreenshots();

    }
}
