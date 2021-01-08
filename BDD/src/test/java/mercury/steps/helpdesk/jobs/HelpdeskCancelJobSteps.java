package mercury.steps.helpdesk.jobs;

import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.JobDao;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelper;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobCancelPanel;
import mercury.runtime.RuntimeState;
import mercury.steps.helpdesk.resources.HelpdeskManageResourcesSteps;


public class HelpdeskCancelJobSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelper dbHelper;
    @Autowired private JobDao jobDao;
    @Autowired private NewJob job;
    @Autowired private TestData testData;
    @Autowired private HelpdeskManageResourcesSteps helpdeskManageResourcesSteps;


    @When("^Notes are entered$")
    public void cancel_Notes_are_entered() throws Throwable {
        testData.put("notes", "updated by automated test");
        runtimeState.helpdeskJobCancelPanel.enterNotes(testData.getString("notes"));
    }

    @ContinueNextStepsOnException
    @Then("^the job status is (?:still|now) \"([^\"]*)\"$")
    public void client_Status_is(String name) throws Throwable {
        Integer jobReference = testData.getInt("jobReference") != null ? testData.getInt("jobReference") : job.getJobReference();
        int expectedStatusId = dbHelper.getJobStatusId(name);
        await().atMost(MAX_TIMEOUT, SECONDS).until( () -> jobDao.getByJobReference(jobReference).getJobStatusId(), equalTo(expectedStatusId) );
    }

    @ContinueNextStepsOnException
    @Then("^a original job number is saved to the database$")
    public void a_original_job_number_is_saved_to_the_database() {
        int duplicateJobId = jobDao.getByJobReference(job.getJobReference()).getDuplicateJobId();
        int originalJobReference = Integer.valueOf(testData.getString("originalJob").split(" ")[0]);
        assertTrue(duplicateJobId == jobDao.getByJobReference(originalJobReference).getId());
    }

    @ContinueNextStepsOnException
    @Then("^still no resource is assigned$")
    public void still_no_resource_is_assigned() throws Throwable {
        throw new PendingException();
    }

    @ContinueNextStepsOnException
    @Then("the job (?:still|now) displays Resource status \"([^\"]*)\" and Client status \"([^\"]*)\"$")
    public void the_job_displays_Resource_status(String resourecStatus, String clientStatus) {
        assertEquals(resourecStatus, runtimeState.helpdeskJobPage.getResourceStatus());
        assertEquals(clientStatus, runtimeState.helpdeskJobPage.getClientStatus());
    }

    @When("^all cancelation details are entered$")
    public void all_cancelation_details_are_entered() throws Throwable {
        testData.put("reason", "Work No Longer Required");
        testData.put("notes", "updated by automated test");

        runtimeState.helpdeskJobCancelPanel.enterRequestedBy(testData.getString("requestedBy"));
        runtimeState.helpdeskJobCancelPanel.selectReason(testData.getString("reason"));
        runtimeState.helpdeskJobCancelPanel.enterNotes(testData.getString("notes"));
        runtimeState.scenario.write("RequestedBy: " + testData.getString("requestedBy") + ", Reason: " + testData.getString("reason") + ", Notes: " + testData.getString("notes"));
    }

    @When("^job is cancelled$")
    public void job_is_cancelled() throws Throwable {
        helpdeskManageResourcesSteps.the_assigned_resource_is_removed_from_manage_resources_section();
        helpdeskManageResourcesSteps.the_advise_removal_is_confirmed();
        helpdeskManageResourcesSteps.any_additional_resources_are_removed();
        helpdeskManageResourcesSteps.the_additional_resource_required_section_is_closed_and_something("Cancelled");
    }

    @When("^the name of person canceling the job is entered$")
    public void the_name_of_person_canceling_the_job_is_entered() throws Throwable {
        runtimeState.helpdeskJobCancelPanel.enterRequestedBy(testData.getString("loginUser"));
    }

    @When("^Reason is set to \"([^\"]*)\"$")
    public void cancel_reason_is_set_to(String arg1) throws Throwable {
        testData.put("reason", arg1);
        runtimeState.helpdeskJobCancelPanel.selectReason(testData.getString("reason"));
    }

    @When("^the original job number is entered$")
    public void the_original_job_number_is_entered() throws Throwable {
        String originalJob = runtimeState.helpdeskJobCancelPanel.selectRandomOriginalJobOption();
        testData.put("originalJob", originalJob);
        runtimeState.scenario.write("Original job: " + originalJob);
    }

    @When("^Other Reason is entered$")
    public void other_Reason_is_set_to() throws Throwable {
        String otherReason = "fat fingers";
        testData.put("reason", "Other - " + otherReason);
        runtimeState.helpdeskJobCancelPanel.enterOtherReason(otherReason);
    }

    @When("^all cancelation details are entered for reason \"(.*)\"$")
    public void all_cancelation_details_are_entered(String option) throws Throwable {
        testData.put("reason", option);
        testData.put("notes", "updated by automated test");

        runtimeState.helpdeskJobCancelPanel.enterRequestedBy(testData.getString("requestedBy"));
        runtimeState.helpdeskJobCancelPanel.selectReason(testData.getString("reason"));
        runtimeState.helpdeskJobCancelPanel.enterNotes(testData.getString("notes"));
        runtimeState.scenario.write("RequestedBy: " + testData.getString("requestedBy") + ", Reason: " + testData.getString("reason") + ", Notes: " + testData.getString("notes"));
    }

    @ContinueNextStepsOnException
    @Then("^the Cancellation panel is displayed for the job$")
    public void the_cancellation_panel_is_displayed() {
        runtimeState.helpdeskJobCancelPanel = new HelpdeskJobCancelPanel(getWebDriver()).get();
        assertEquals("Cancellation request for job: " + testData.getString("jobReference"), runtimeState.helpdeskJobCancelPanel.getHeader().trim());
    }

    @And("^the job cancellation details are retrieved$")
    public void the_job_cancellation_details_are_retrieved() {
        List<Map<String, Object>> dbData = dbHelper.getJobCancellationAndResourceRemovalDetails(testData.getInt("jobReference"));
        testData.put("reason", dbData.get(0).get("JobCancellationReason"));
        testData.put("ResourceRemovalReason", dbData.get(0).get("ResourceRemovalReason"));
        testData.put("ResourceRemovalNotes", dbData.get(0).get("ResourceRemovalNotes"));
    }
}
