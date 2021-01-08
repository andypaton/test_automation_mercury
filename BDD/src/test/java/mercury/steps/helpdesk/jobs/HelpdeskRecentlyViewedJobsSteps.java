package mercury.steps.helpdesk.jobs;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static mercury.helpers.StringHelper.normalize;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.PopupAlert;
import mercury.runtime.RuntimeState;
import mercury.steps.LoginSteps;

public class HelpdeskRecentlyViewedJobsSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;
    @Autowired private LoginSteps loginSteps;


    @When("^a search is run for \"([^\"]*)\" jobs?$")
    public void a_search_is_run_for_jobs(Integer count) throws Throwable {

        List<Integer> jobReferences = dbHelperJobs.getRandomJobReferences(count);
        runtimeState.scenario.write("Searching for job references: "+ jobReferences.toString());
        testData.put("jobReferences", jobReferences);
        for (int jobReference : jobReferences) {
            runtimeState.helpdeskHomePage.selectHomeTab();
            runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(jobReference).get();
            runtimeState.helpdeskHomePage.closeActiveTab();
            if (runtimeState.helpdeskHomePage.isAlertVisible()) {
                runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
                runtimeState.popupAlert.ok();            }
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Last Viewed list displays (?:upto |)the last \"([^\"]*)\" job numbers? with store names$")
    public void the_Last_Viewed_list_displays_the_last_job_numbers_are_displayed_with_store_names(Integer count) throws Throwable {
        runtimeState.helpdeskHomePage.selectHomeTab();
        List<Integer> lastViewedJobs = runtimeState.helpdeskHomePage.getLastViewedJobs();
        List<String> lastViewedJobSiteNames = runtimeState.helpdeskHomePage.getLastViewedSites();
        List<Integer> jobReferences = testData.getIntList("jobReferences");

        for (int i = 1; i <= count; i++) {
            int jobReference = jobReferences.get(jobReferences.size() - i);
            String siteName = dbHelperSites.getSiteNameForJobRef(jobReference);
            runtimeState.scenario.write("Asserting presence of : " + jobReference + " (" + siteName + ")");

            assertTrue("Expected list to contain job: " + jobReference, lastViewedJobs.contains(jobReference));
            assertTrue("Expected list to contain site name: " + siteName, lastViewedJobSiteNames.contains(normalize(siteName)));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Last Viewed list displays the last \"([^\"]*)\" job number with store names on subsequent logins$")
    public void the_Last_Viewed_list_displays_the_last_job_number_with_store_names_on_subsequent_logins(Integer count) throws Throwable {
        // due to failures that only occur when running on Jenkins we need to try this test a few times before giving up!
        int attempts = 1;
        boolean success = false;
        String errorMessage = "";
        do {
            loginSteps.the_user_logs_out_and_then_back_in();
            try {
                the_Last_Viewed_list_displays_the_last_job_numbers_are_displayed_with_store_names(count);
                success = true;
            } catch (Exception e) {
                attempts++;
                errorMessage = e.getStackTrace().toString();
            }
        } while (attempts < 10 && !success);
        assertTrue(errorMessage, success);
        runtimeState.scenario.write("Step took " + attempts + " attempts before passing");
    }

    @ContinueNextStepsOnException
    @Then("^the Last Viewed job list is updated immediately$")
    public void the_Last_Viewed_list_is_updated_immediately() throws Throwable {
        runtimeState.helpdeskHomePage.selectHomeTab();
        List<Integer> lastViewedJobs = runtimeState.helpdeskHomePage.getLastViewedJobs();
        List<String> lastViewedWhen = runtimeState.helpdeskHomePage.getLastViewedWhen();
        List<Integer> jobReferences = testData.getIntList("jobReferences");

        int jobReference = jobReferences.get(jobReferences.size() - 1);
        assertTrue("Expected list to contain job: " + jobReference, lastViewedJobs.get(0) == jobReference);
        assertEquals("Expected 'updated' to be: a few seconds ago", "a few seconds ago", lastViewedWhen.get(0));
    }

    @When("^a job is selected from the Last Viewed list$")
    public void a_job_is_selected_from_the_Last_Viewed_list() throws Throwable {

        List<Integer> lastViewedJobs = runtimeState.helpdeskHomePage.getLastViewedJobs();

        if (lastViewedJobs.isEmpty()) {
            // first ensure the Last Viewed list has entries
            a_search_is_run_for_jobs(1);
            lastViewedJobs = runtimeState.helpdeskHomePage.getLastViewedJobs();
        }
        outputHelper.takeScreenshots();
        testData.addIntegerTag("jobReference", lastViewedJobs.get(0));
        runtimeState.scenario.write("Last Viewed items: " + lastViewedJobs.toString());
        runtimeState.scenario.write("Selecting Last Viewed item: " + lastViewedJobs.get(0));
        runtimeState.helpdeskHomePage.selectLastViewedItem(lastViewedJobs.get(0));
    }
}
