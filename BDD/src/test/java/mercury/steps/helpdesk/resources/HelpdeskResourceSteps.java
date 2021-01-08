package mercury.steps.helpdesk.resources;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.SHORT;
import static mercury.helpers.StringHelper.normalize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.runtime.RuntimeState;

public class HelpdeskResourceSteps {
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TzHelper tzHelper;

    private static final Logger logger = LogManager.getLogger();

    public void verifyAbsenceHeaders() throws Throwable {
        Grid grid = runtimeState.helpdeskAddOrOverrideAbsencePanel.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        String[] expectedHeaders = { "From", "To", "Type", "Backup Resource", "Notes", "Status", ""};
        for (String header : grid.getHeaders()) {
            assertTrue("Expected " + expectedHeaders.toString() + " to contain " + header, ArrayUtils.contains(expectedHeaders, header));
        }
        assertTrue("Unexpected number of headers returned, found : " + grid.getHeaders().size() + " expected : " + expectedHeaders.length, expectedHeaders.length == grid.getHeaders().size());
        outputHelper.takeScreenshots();
    }

    public void verifyAbsenceRow() throws Throwable {
        Grid grid = runtimeState.helpdeskAddOrOverrideAbsencePanel.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        boolean found = false;
        for (Row row : grid.getRows()) {
            if (row.getCell("From").getText().contains(testData.getString("fromDate")) && row.getCell("To").getText().contains(testData.getString("toDate"))) {
                assertTrue("incorrect Absence type? ", testData.getString("absenceType").equalsIgnoreCase(row.getCell("Type").getText()));
                if (testData.getString("backup").equalsIgnoreCase("BACKUP")) {
                    assertTrue("Incorrect backup resource found? ", testData.getString("backupResourceName").equalsIgnoreCase(row.getCell("Backup Resource").getText()));
                } else {
                    assertTrue("Incorrect backup resource", row.getCell("Backup Resource").getText().isEmpty());
                }
                assertTrue("Incorrect Notes? ", row.getCell("Notes").getText().contains("Notes added by test automation:"));
                assertTrue("Incorrect Status? ", row.getCell("Status").getText().equalsIgnoreCase("Active"));
                found = true;
            }
        }
        assertTrue("Absence not found", found);
        outputHelper.takeScreenshots();
    }

    public void setupToDate() throws Throwable{
        String toDateStr = DateHelper.getNowDatePlusOffset(48, FULL_DATE);
        runtimeState.helpdeskAddOrOverrideAbsencePanel.selectToDate(toDateStr);
        String toDate = DateHelper.convert(runtimeState.helpdeskAddOrOverrideAbsencePanel.getToDate(), SHORT, MEDIUM);
        testData.put("toDate", toDate);
    }

    public void setupFromDate() throws Throwable{
        String fromDateStr = DateHelper.getNowDatePlusOffset(24, FULL_DATE);
        runtimeState.helpdeskAddOrOverrideAbsencePanel.selectFromDate(fromDateStr);
        String fromDate = DateHelper.convert(runtimeState.helpdeskAddOrOverrideAbsencePanel.getFromDate(), SHORT, MEDIUM);
        testData.put("fromDate", fromDate);    }

    public void createAbsence(String addBackupResource) throws Throwable {
        String absenceNotes = "Notes added by test automation:  " + DateHelper.dateAsString(new Date());
        testData.put("resourceName", runtimeState.helpdeskAddOrOverrideAbsencePanel.getResourceName());
        runtimeState.helpdeskAddOrOverrideAbsencePanel.clickAddAbsence();
        setupToDate();
        setupFromDate();
        runtimeState.helpdeskAddOrOverrideAbsencePanel.selectRandomAbsenceType();
        testData.put("absenceType", runtimeState.helpdeskAddOrOverrideAbsencePanel.getSelectedAbsenceType());
        runtimeState.helpdeskAddOrOverrideAbsencePanel.setAbsenceNotes(absenceNotes);
        if (addBackupResource.equalsIgnoreCase("BACKUP")) {
            runtimeState.helpdeskAddOrOverrideAbsencePanel.selectBackupResource();
            testData.put("backupResourceName", runtimeState.helpdeskAddOrOverrideAbsencePanel.getSelectedBackupResource());
        }
        outputHelper.takeScreenshots();
        runtimeState.helpdeskAddOrOverrideAbsencePanel.saveAbsence();
    }

    public int numberOfAbsencesDisplayed() {
        Grid grid = runtimeState.helpdeskAddOrOverrideAbsencePanel.getGrid();
        return grid.getRows().size();
    }


    @When("^an absence is added with \"([^\"]*)\" cover$")
    public void an_absence_is_added_with_cover(String backup) throws Throwable {
        runtimeState.helpdeskAddOrOverrideAbsencePanel = runtimeState.helpdeskResourcePage.selectAddOrOverrideAbsenceAction();
        createAbsence(backup);
        testData.put("backup", backup);
        testData.addIntegerTag("numberOfAbsencesDisplayed", numberOfAbsencesDisplayed());
    }

    @When("^a new Absence is created with ((?:backup|no backup)) resource assigned that overlaps an existing absence$")
    public void a_new_absence_is_created_with_backup_resource_assigned_that_overlaps_an_existing_absence(String backup) throws Throwable {
        createAbsence(backup);
    }

    @And("^the absence details are displayed/recorded correctly")
    public void the_absence_details_are_displayed() throws Throwable {
        verifyAbsenceHeaders();
        verifyAbsenceRow();
    }

    @When("^an \"([^\"]*)\" is keyed but cancelled before being saved$")
    public void an_absence_is_keyed_but_cancelled_before_being_saved(String arg1) throws Throwable {
        runtimeState.helpdeskAddOrOverrideAbsencePanel = runtimeState.helpdeskResourcePage.selectAddOrOverrideAbsenceAction();
        int numberOfRowsDisplayed = 0;
        if (runtimeState.helpdeskAddOrOverrideAbsencePanel.existingAbsencesDisplayed()) {
            Grid grid = runtimeState.helpdeskAddOrOverrideAbsencePanel.getGrid();
            assertNotNull("Unexpected Null Grid", grid);
            numberOfRowsDisplayed = grid.getRows().size();
        }
        testData.addIntegerTag("numberOfRowsDisplayed", numberOfRowsDisplayed);
        runtimeState.helpdeskAddOrOverrideAbsencePanel.clickAddAbsence();
        setupToDate();
        setupFromDate();
        runtimeState.helpdeskAddOrOverrideAbsencePanel.selectAbsenceType("Absence");
        outputHelper.takeScreenshots();
        runtimeState.helpdeskAddOrOverrideAbsencePanel.clickCancelAddOrOverride();
    }

    @ContinueNextStepsOnException
    @Then("^the absence will not be displayed in the table view$")
    public void the_absence_will_not_be_displayed_in_the_table_view() {
        int numberOfRowsDisplayedNow = 0;
        if (runtimeState.helpdeskAddOrOverrideAbsencePanel.existingAbsencesDisplayed()) {
            Grid grid = runtimeState.helpdeskAddOrOverrideAbsencePanel.getGrid();
            assertNotNull("Unexpected Null Grid", grid);
            numberOfRowsDisplayedNow = grid.getRows().size();
        }
        int originalNumberOfRowsDisplayed = testData.getInt("numberOfRowsDisplayed");
        logger.debug("Original number of rows displayed: " + originalNumberOfRowsDisplayed + "..........Number of rows displayed now: " + numberOfRowsDisplayedNow);
        assertEquals("The number of rows has changed", originalNumberOfRowsDisplayed, numberOfRowsDisplayedNow);
    }

    @And("^an alert is displayed and the new absence is not added/saved")
    public void an_alert_is_displayed_and_the_absence_is_not_added() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.helpdeskAddOrOverrideAbsencePanel.clickOverlappingAbenceAlert();
        runtimeState.helpdeskAddOrOverrideAbsencePanel.clickCancelAddOrOverride();
        assertTrue("incorrect number of Absences displayed", testData.getInt("numberOfAbsencesDisplayed") == numberOfAbsencesDisplayed());
    }

    /**
     * Asserting the table data in the 'Add or override absence' panel
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ContinueNextStepsOnException
    @Then("^the previously added absences still outstanding will show in a table on the Add or override absence panel$")
    public void the_previously_added_absences_still_outstanding_will_show_in_a_table_on_the_add_or_override_absence_panel() throws Throwable {
        List<Map<String, Object>> dbData = dbHelperResources.getOutstandingAbsencesForResource(testData.getString("resource"));
        Grid grid = runtimeState.helpdeskAddOrOverrideAbsencePanel.getGrid();
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");
        // Compare both sets of data are of the same size
        assertEquals("Unexpected number of records", grid.getRows().size(), dbData.size());

        // Verify both data sets are the same
        runtimeState.scenario.write("Asserting the table data displayed in the 'Add or override absence' panel");
        Iterator it = dbData.iterator();
        for (Row row : grid.getRows()) {
            Map<String, Object> map = (Map<String, Object>) it.next();
            logger.debug(row.getCell("From").getText());

            String expectedFromDate = DateHelper.convert(map.get("FromDate").toString(), "d MMM yyyy h:mma", MEDIUM);
            String expectedToDate = DateHelper.convert(map.get("ToDate").toString(), "d MMM yyyy h:mma", MEDIUM);

            //Resource Absences in mercury application are displayed in Eastern Standard Time in Walmart and London Time in Rainbow.(Stored in UTC in DB)
            String ianaCode = LOCALE.equals("en-GB") ? "Europe/London" : "America/New_York";
            expectedFromDate = tzHelper.adjustTimeForIanaCode(ianaCode, expectedFromDate, MEDIUM).replaceAll("am", "AM").replaceAll("pm", "PM");
            expectedToDate = tzHelper.adjustTimeForIanaCode(ianaCode, expectedToDate, MEDIUM).replaceAll("am", "AM").replaceAll("pm", "PM");

            assertEquals("Unexpected From", expectedFromDate, row.getCell("From").getText());
            assertEquals("Unexpected To", expectedToDate, row.getCell("To").getText());
            assertEquals("Unexpected Type", map.get("Type"), row.getCell("Type").getText());
            assertEquals("Unexpected Backup Resource", map.get("BackupResource"), row.getCell("Backup Resource").getText());
            assertEquals("Unexpected Notes", normalize(map.get("Notes").toString()), row.getCell("Notes").getText());
            assertEquals("Unexpected Status", map.get("Status"), row.getCell("Status").getText());
        }
    }

    @Given("^City Resource can be assigned an ipad$")
    public void city_resource_can_be_assigned_ipad() {
        Integer adLoginState = dbHelperResources.getCityResourceAdLoginState();
        testData.put("adLoginState", adLoginState);
        if (adLoginState.equals(0)) {
            dbHelperResources.updateCityResourceSoTheyCanHaveIpad(1);
        }
    }

    @Then("^resource type table restored to default value$")
    public void resource_type_table_restored_to_default_value() {
        if (testData.get("adLoginState").equals(0)) {
            dbHelperResources.updateCityResourceSoTheyCanHaveIpad(0);
        }
    }

}

