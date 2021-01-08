package mercury.steps.portal.jobs;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.DataTable;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.portal.jobs.JobDetailsPage;
import mercury.pageobject.web.portal.jobs.JobHistoryPage;
import mercury.pageobject.web.portal.ppm.PPMJobDetailsPage;
import mercury.runtime.RuntimeState;

public class PortalAllJobsSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;

    @ContinueNextStepsOnException
    @Then("^the Find button will be disabled if there are no dates in fields$")
    public void the_find_button_is_diabled_when_no_dates_in_fields(DataTable fields) throws Throwable {
        String initialFromDate = runtimeState.allJobsPage.getFromDate();
        String initialToDate = runtimeState.allJobsPage.getToDate();

        for (String field : fields.asList(String.class)) {
            runtimeState.scenario.write("Asserting Find button disabled when " + field + " date(s) cleared");
            switch (field) {
            case "From":
                runtimeState.allJobsPage.clearFromDate();
                break;
            case "To":
                runtimeState.allJobsPage.clearToDate();
                break;
            case "From and To":
                runtimeState.allJobsPage.clearFromDate();
                runtimeState.allJobsPage.clearToDate();
                break;
            default:
                throw new Exception("Not a valid datefield option");
            }
            assertFalse("Find Button is enabled when " + field + " date(s) cleared", runtimeState.allJobsPage.isFindButtonEnabled());
            outputHelper.takeScreenshots();
            runtimeState.allJobsPage.setFromDate(initialFromDate);
            runtimeState.allJobsPage.setToDate(initialToDate);
        }
    }

    @When("^dates are entered$")
    public void user_enters_dates_in_datepicker() throws Throwable {
        Date createdDated = DateHelper.stringAsDate(runtimeState.timestamp, "yyyy-MM-dd HH:mm:ss");
        runtimeState.allJobsPage.setFromDate(DateHelper.getDatePlusOffsetInMonths(createdDated, -2));
        runtimeState.allJobsPage.setToDate(DateHelper.getDatePlusOffsetInMonths(createdDated, 0));
    }

    @When("^a \"([^\"]*)\" day date window is entered$")
    public void a_one_week_date_window_is_entered(int days) throws Throwable {
        Date createdDated = DateHelper.stringAsDate(runtimeState.timestamp, "yyyy-MM-dd HH:mm:ss");
        runtimeState.allJobsPage.setFromDate(DateHelper.getDatePlusOffsetInDays(createdDated, (0 - days)));
        runtimeState.allJobsPage.setToDate(DateHelper.getDatePlusOffsetInDays(createdDated, 0));
    }

    private void a_job_is_selected_from_the_grid_for_ops_director() throws Throwable {
        Grid grid = runtimeState.allJobsPage_OpsDirector.getGrid();

        List<String> jobList = grid.getColumnText("Job reference");
        List<String> jobTypeList = grid.getColumnSubText("Job reference");
        int rowNum = RandomUtils.nextInt(0, jobList.size() - 1);
        String jobReference = jobList.get(rowNum);
        String jobType = jobTypeList.get(rowNum);

        testData.put("jobReference", jobReference);
        testData.put("jobType", jobType);
        outputHelper.takeScreenshots();
        runtimeState.scenario.write("Opening Job Reference: " + jobReference);
        runtimeState.allJobsPage_OpsDirector.searchJobs(jobReference);
        runtimeState.allJobsPage_OpsDirector.waitUntilFirstRowOfGridContains(jobReference);
        runtimeState.allJobsPage_OpsDirector.openJob(jobReference);
    }

    @And("^a job is selected from the grid$")
    public void a_job_is_selected_from_the_grid() throws Throwable {
        if (testData.getString("profileName").equals("Operations Director")) {
            a_job_is_selected_from_the_grid_for_ops_director();

        } else {
            Grid grid = runtimeState.allJobsPage.getGrid();
            List<String> jobList = grid.getColumnText("Job reference");
            List<String> jobTypeList = grid.getColumnSubText("Job reference");
            int rowNum = RandomUtils.nextInt(0, jobList.size() - 1);
            String jobReference = jobList.get(rowNum);
            String jobType = jobTypeList.get(rowNum);
            testData.put("jobReference", jobReference);
            testData.put("jobType", jobType);
            outputHelper.takeScreenshots();
            runtimeState.scenario.write("Opening Job Reference: " + jobReference);
            runtimeState.allJobsPage.searchJobs(jobReference);
            GridHelper.waitForRowCount(grid.getGridXpath(), 1);
            runtimeState.allJobsPage.openJob(jobReference);
        }
    }

    @ContinueNextStepsOnException
    @Then("a table is displayed for each Split RFM$")
    public void a_table_is_displayed_for_each_Split_RFM() {
        List<String> splitRFMs = runtimeState.allJobsPage_OpsDirector.getListOfSplitRFMs();
        runtimeState.scenario.write("Tables displayed for Split RFMs: \n" + splitRFMs.toString().replace("[", "").replace("]", "").replaceAll(", ", "\n").replaceAll("Jobs for ", ""));
        assertNotNull("Split RFMs not displayed", splitRFMs);
    }

    @ContinueNextStepsOnException
    @Then("the job details page is displayed$")
    public void the_job_details_page_is_displayed() {
        if ("PPM".equalsIgnoreCase(testData.getString("jobType"))) {
            runtimeState.ppmJobDetailsPage = PageFactory.initElements(getWebDriver(), PPMJobDetailsPage.class).get();
            runtimeState.scenario.write("Asserting Job reference " + testData.getString("jobReference") + " is shown in page title for the PPM Job details Page");
            assertEquals("Job Details Page not shown", "Job " + testData.getString("jobReference"), runtimeState.ppmJobDetailsPage.getPageHeader());
        } else {
            runtimeState.jobDetailsPage = PageFactory.initElements(getWebDriver(), JobDetailsPage.class).get();
            runtimeState.scenario.write("Asserting Job reference " + testData.getString("jobReference") + " is shown in page title for Job details Page");
            assertTrue("Job Details Page not shown", runtimeState.jobDetailsPage.getPageTitle().contains(testData.getString("jobReference")));
        }
    }

    @ContinueNextStepsOnException
    @Then("the job history page is displayed$")
    public void the_job_history_page_is_displayed() {
        runtimeState.jobHistoryPage = PageFactory.initElements(getWebDriver(), JobHistoryPage.class).get();
        runtimeState.scenario.write("Asserting Job reference " + testData.getString("jobReference") + " is shown in page title for Job History Page");
        assertEquals("Job History Page not shown", "Job History", runtimeState.jobHistoryPage.getPageTitle());
    }

    public void allJobsFormDisplaysCorrectly() throws Throwable {
        String fromDateField = runtimeState.allJobsPage.getFromDate();
        String toDateField = runtimeState.allJobsPage.getToDate();
        String expectedFromDate = DateHelper.getDatePlusOffsetInMonths(-3);
        String expectedToDate = DateHelper.getDatePlusOffsetInMonths(0);

        runtimeState.scenario.write("Asserting From date is visible and has the value " + expectedFromDate);
        assertTrue("From Date Picker is not visible", runtimeState.allJobsPage.isFromDatePickerVisible());
        assertEquals("From Date is not 3 months before today", fromDateField, expectedFromDate);
        runtimeState.scenario.write("Asserting To date is visible and has the value " + expectedToDate);
        assertTrue("To Date Picker is not visible", runtimeState.allJobsPage.isToDatePickerVisible());
        assertEquals("To date not set to today", toDateField, expectedToDate);
        runtimeState.scenario.write("Asserting Find button is visible");
        assertTrue("Find Button is not visible", runtimeState.allJobsPage.isFindButtonVisible());
    }

}
