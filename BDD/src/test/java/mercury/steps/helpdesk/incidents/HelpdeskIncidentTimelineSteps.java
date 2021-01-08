package mercury.steps.helpdesk.incidents;

import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.MEDIUM_DATE;
import static mercury.helpers.Globalisation.SHORT_TIME;
import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentTimelineTab;
import mercury.helpers.IncidentHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.runtime.RuntimeState;

public class HelpdeskIncidentTimelineSteps {

    private static final Logger logger = LogManager.getLogger();
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TzHelper tzHelper;
    @Autowired private IncidentHelper incidentHelper;
    @Autowired private OutputHelper outputHelper;

    @ContinueNextStepsOnException
    @Then("^the Incident Timeline contains a new row for Description \"([^\"]*)\" and Type \"([^\"]*)\"$")
    public void the_Incident_timeline_contains_a_row_for_description_as_and_type_as(String expectedDescription, String expectedType) throws Throwable {

        runtimeState.helpdeskIncidentTimelineTab = new HelpdeskIncidentTimelineTab(getWebDriver()).get();
        String expectedUser = testData.getString("loginUser");

        the_Incident_timeline_displays("Description", expectedDescription);
        the_Incident_timeline_displays("User", expectedUser);
        the_Incident_timeline_displays("Type", expectedType);

        // To assert the time
        runtimeState.helpdeskIncidentTimelineTab.tableSort("Time");
        Grid grid = runtimeState.helpdeskIncidentTimelineTab.getGrid();
        List<Row> row = grid.getRows();
        String actualTime = row.get(0).getCell("Time").getText();
        String[] time = actualTime.split("ST");
        String eventActualTime = time[0];
        Date eventActualTimeAsDate = DateHelper.stringAsDate(eventActualTime, MEDIUM);
        String actualEventTime = DateHelper.dateAsString(eventActualTimeAsDate, SHORT_TIME);
        String actualEventDate =  DateHelper.dateAsString(eventActualTimeAsDate, MEDIUM_DATE);
        int siteId = dbHelperSites.getSiteIdForIncidentRef(runtimeState.helpdeskViewIncidentPage.getIncidenceReference());
        Date storeDate = tzHelper.getCurrentTimeAtSite(siteId);
        String expectedCurrentTime = DateHelper.dateAsString(storeDate, SHORT_TIME);
        String expectedCurrentDate = DateHelper.dateAsString(storeDate, MEDIUM_DATE);
        long diff = DateHelper.getDifferenceBetweenTwoTimes(eventActualTimeAsDate, storeDate);
        long differenceInMinutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) % 60;
        runtimeState.scenario.write("Asserting Date and Time is: " + expectedCurrentDate + " " + expectedCurrentTime);
        assertEquals("Looks like " + expectedCurrentDate + "is not dispalyed in Timeline tab ",expectedCurrentDate, actualEventDate);
        assertTrue("current time (" + expectedCurrentTime + ") - Store event time (" + actualEventTime + ") > 1 minute", differenceInMinutes < 3);
    }

    @ContinueNextStepsOnException
    @Then("^the Incident timeline displays \"([^\"]*)\" as \"([^\"]*)\"$")
    public void the_Incident_timeline_displays(String header, String expectedValue) throws Throwable {
        runtimeState.helpdeskIncidentTimelineTab = new HelpdeskIncidentTimelineTab(getWebDriver());

        Grid grid = runtimeState.helpdeskIncidentTimelineTab.getGrid();
        List<Row> rows = grid.getRows();
        Date storeDateAndTime = testData.getDate("storeTime");
        Boolean found = false;
        expectedValue = localize(expectedValue);
        switch (header) {

        case "Description":
            runtimeState.scenario.write("Asserting Description: " + expectedValue);
            for (Row row : rows) {
                incidentHelper.outputIncidentTimelineRow(row);
                String actualStoreTime = row.getCell("Time").getText();
                String[] time = actualStoreTime.split("ST");
                String actualTime = time[0].trim();
                Date eventActualTimeAsDate = DateHelper.stringAsDate(actualTime, MEDIUM);
                long diff = DateHelper.getDifferenceBetweenTwoTimes(eventActualTimeAsDate, storeDateAndTime);
                long differenceInMinutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) % 60;
                if (expectedValue.equals(row.getCell("Description").getText()) && (differenceInMinutes < 2)) {
                    found=true;
                    break;
                }
            }
            assertTrue("Expected Description [" + expectedValue + "] is not displayed in the Timeline tab", found);
            break;

        case "User":
            runtimeState.scenario.write("Asserting User: " + expectedValue);
            for (Row row : rows) {
                incidentHelper.outputIncidentTimelineRow(row);
                String actualStoreTime = row.getCell("Time").getText();
                String[] time = actualStoreTime.split("ST");
                String actualTime = time[0];
                Date eventActualTimeAsDate = DateHelper.stringAsDate(actualTime, MEDIUM);
                long diff = DateHelper.getDifferenceBetweenTwoTimes(eventActualTimeAsDate, storeDateAndTime);
                long differenceInMinutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) % 60;
                if (expectedValue.contains(row.getCell("User").getText()) && (differenceInMinutes < 2)) {
                    found=true;
                    break;
                }
            }
            assertTrue("Looks like expected User: " + expectedValue + " is not displayed in the Timeline tab", found);
            break;

        case "Type":
            runtimeState.scenario.write("Asserting Type: " + expectedValue);
            for (Row row : rows) {
                incidentHelper.outputIncidentTimelineRow(row);
                String actualStoreTime = row.getCell("Time").getText();
                String[] time = actualStoreTime.split("ST");
                String actualTime = time[0];
                Date eventActualTimeAsDate = DateHelper.stringAsDate(actualTime, MEDIUM);
                long diff = DateHelper.getDifferenceBetweenTwoTimes(eventActualTimeAsDate, storeDateAndTime);
                long differenceInMinutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) % 60;
                if (expectedValue.equals(row.getCell("Type").getText()) && (differenceInMinutes < 2)) {
                    found=true;
                    break;
                }
            }
            assertTrue("Looks like expected Type: " + expectedValue + " is not displayed in the Timeline tab", found);
            break;
        }
        outputHelper.takeScreenshots();
    }

    @And("^the Timeline tab is refreshed$")
    public void the_timeline_tab_is_refreshed() throws Throwable {
        POHelper.refreshPage();
        runtimeState.helpdeskIncidentTimelineTab.clickRefresh();
    }

    @ContinueNextStepsOnException
    @Then("^the Incident timeline displays the updated core details$")
    public void the_Incident_timeline_displays_the_updated_core_details() throws Throwable {

        runtimeState.helpdeskIncidentTimelineTab = new HelpdeskIncidentTimelineTab(getWebDriver());
        Grid grid = runtimeState.helpdeskIncidentTimelineTab.getGrid();
        List<Row> row = grid.getRows();

        String caller[] = testData.getString("caller").split(" ");
        String expectedCaller = caller[0];
        String expectedSiteValue = testData.getString("siteClosedValue");
        String expectedDepartmentValue = testData.getString("departmentClosedValue");

        for (int i=0;i<row.size();i++) {
            String actualDescription = row.get(i).getCell("Description").getText();
            runtimeState.scenario.write(actualDescription);
            if (actualDescription.contains(expectedSiteValue) && actualDescription.contains(expectedDepartmentValue)) {
                logger.info(actualDescription);
                break;
            } else if (i==row.size()-1 && !(actualDescription.contains(expectedCaller) && actualDescription.contains(expectedSiteValue) && actualDescription.contains(expectedDepartmentValue))){
                throw new PendingException("The core details are not shown correctly in the Incident Timeline");
            }
        }

    }

}


