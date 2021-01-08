package mercury.steps.helpdesk.resources;

import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.THREE_MINUTES;
import static mercury.helpers.DateHelper.isValidDate;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Duration.FIVE_SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.database.dao.JobDao;
import mercury.database.models.Job;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.asserter.dbassertions.AssertJobCreated;
import mercury.helpers.asserter.dbassertions.AssertResourceStatus;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.pageobject.web.helpdesk.HelpdeskCoreDetails;
import mercury.pageobject.web.helpdesk.HelpdeskTimelineTab;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.pageobject.web.helpdesk.resources.HelpdeskManageResourcesPanel;
import mercury.runtime.RuntimeState;

public class HelpdeskManageResourceAssertions {

    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private OutputHelper outputHelper;
    @Autowired AssertionFactory assertionFactory;
    @Autowired private NewJob job;
    @Autowired private TestData testData;
    @Autowired private JobDao jobDao;

    private static final String[] CITY_TECHNICIANS = {"RHVAC Technician", "RHVAC Supervisor", "MST"};


    @ContinueNextStepsOnException
    @Then("^the Manage Resources panel is displayed$")
    public void manage_resources_panel_is_displayed() throws Throwable {
        runtimeState.helpdeskManageResourcesPanel = new HelpdeskManageResourcesPanel(getWebDriver()).get();
        outputHelper.takeScreenshots();
        assertTrue(runtimeState.helpdeskManageResourcesPanel.isDisplayed());

        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
        runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
    }

    @ContinueNextStepsOnException
    @Then("^the Manage Resources page is displayed$")
    public void manage_resources_displayed() throws Throwable {

        runtimeState.helpdeskManageResourcesPanel = new HelpdeskManageResourcesPanel(getWebDriver()).get();
        runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
        runtimeState.helpdeskCoreDetails = new HelpdeskCoreDetails(getWebDriver()).get();
        outputHelper.takeScreenshots();

        Integer jobReference = Integer.valueOf(runtimeState.helpdeskManageResourcesPanel.getHeadline());

        assertTrue(runtimeState.helpdeskManageResourcesPanel.isDisplayed());

        assertEquals(testData.getString("siteName"), runtimeState.helpdeskCoreDetails.getCoreValue("Site"));
        assertTrue(runtimeState.helpdeskCoreDetails.getCoreValue("Caller").contains(testData.getString("caller")));
        assertTrue("expected location: " + job.getLocation(), runtimeState.helpdeskCoreDetails.getCoreValue("Location").contains(testData.getString("location")));
        assertEquals(testData.getString("subtypeClassification"), runtimeState.helpdeskCoreDetails.getCoreValue("Subtype/Classification"));

        String expectedAssetTag = dbHelper.getAssetTag(jobReference);
        if (expectedAssetTag != null) {
            assertEquals(expectedAssetTag, runtimeState.helpdeskCoreDetails.getCoreValue("Asset Tag"));
        }

        assertEquals(testData.getString("fault"), runtimeState.helpdeskCoreDetails.getCoreValue("Fault"));
        assertEquals(testData.getString("description"), runtimeState.helpdeskCoreDetails.getCoreValue("Description"));
        assertEquals(testData.getString("priority"), runtimeState.helpdeskCoreDetails.getCoreValue("Priority"));
    }

    /**
     * Assert BOTH the manage resources panel AND the job view.
     * To assert ONLY the job view then use step: 'the job resource status is .....'
     * @param resourceStatus
     */
    @ContinueNextStepsOnException
    @Then("^the resource status is (?:still |now |)\"([^\"]*)\"$")
    public void the_resource_status_is(String resourceStatus) {

        if (getWebDriver().getCurrentUrl().contains("Helpdesk")) {
            // assert manage resource panel
            with().pollInterval(FIVE_SECONDS).await().atMost(THREE_MINUTES, SECONDS).until(runtimeState.helpdeskManageResourcesPanel.assertResourceStatusContains(resourceStatus));

            // assert job view
            with().pollInterval(FIVE_SECONDS).await().atMost(THREE_MINUTES, SECONDS).until(runtimeState.helpdeskJobPage.assertResourceStatusContains(resourceStatus));

        } else {
            AssertResourceStatus assertResourceStatus = new AssertResourceStatus(dbHelperJobs, testData.getInt("jobReference"), resourceStatus);
            assertionFactory.performAssertion(assertResourceStatus);
        }
        testData.put("resourceStatus", resourceStatus);
    }

    @And("^the resource status is (?:still |now |)one of \"([^\"]*)\"$")
    public void the_resource_status_is_one_of(String resourceStatus) {
        // assert manage resource panel
        with().pollInterval(FIVE_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskManageResourcesPanel.assertResourceStatusContainsOneOf(resourceStatus));

        // assert job view
        with().pollInterval(FIVE_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskJobPage.assertResourceStatusContainsOneOf(resourceStatus));

        testData.put("resourceStatus", resourceStatus);
    }

    @ContinueNextStepsOnException
    @Then("^a new \"([^\"]*)\" job is saved to the database with \"([^\"]*)\" status$")
    public void the_job_generated(String expectedJobType, String expectedStatus) throws Throwable {

        AssertJobCreated assertJobCreated = new AssertJobCreated(expectedJobType, testData.getString("description"), expectedStatus, jobDao);
        assertionFactory.performAssertion(assertJobCreated);

        Job job = assertJobCreated.getJob();
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("New job reference: " + job.getJobReference());
    }

    @ContinueNextStepsOnException
    @Then("^the job \"([^\"]*)\" red flagged for immediate callout$")
    public void the_job_is_marked_for_callout(String flag) throws Throwable {
        switch (flag.toUpperCase()){
        case "IS" : assertTrue("Red flag immediate callout expected", runtimeState.helpdeskManageResourcesPanel.isImmediateCallout()); break;
        case "IS NOT" : assertTrue("Red flag immediate callout not expected", runtimeState.helpdeskManageResourcesPanel.isNotImmediateCallout()); break;
        default : throw new Exception ("Unexpected flag: " + flag);
        }
    }

    @ContinueNextStepsOnException
    @Then("^a blue banner with \"([^\"]*)\" is displayed$")
    public void job_blue_banner(String title) throws Throwable {
        assertEquals(title, runtimeState.helpdeskJobPage.getAlertText());
    }

    @ContinueNextStepsOnException
    @Then("^the job has been assigned to a ([^\"]*)$")
    public void the_job_has_been_assigned_to(String who) throws Throwable {
        assertFalse("No resource name displayed", runtimeState.helpdeskManageResourcesPanel.getResourceName().isEmpty());
        String resourceProfileDisplayed = runtimeState.helpdeskManageResourcesPanel.getResourceProfile().replace("(", "").replace(")", "");

        if ("CITY TECHNICIAN".equalsIgnoreCase(who)) {
            assertTrue("Unexpected profile displayed: " + resourceProfileDisplayed, Arrays.asList(CITY_TECHNICIANS).contains(resourceProfileDisplayed) );
        } else if ("CONTRACTOR".equalsIgnoreCase(who)) {
            assertTrue("Unexpected profile displayed: " + resourceProfileDisplayed, "Contractor".equals(resourceProfileDisplayed));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the job has not been assigned$")
    public void the_job_is_not_assigned() throws Throwable {
        assertFalse(runtimeState.helpdeskManageResourcesPanel.isResourceNamePresent());
    }

    @ContinueNextStepsOnException
    @Then("^a banner shows Deferred Until date$")
    public void a_banner_shows_Deferred_Until_date() {
        String headlineComment = runtimeState.helpdeskManageResourcesPanel.getHeadlineComment();
        assertTrue(headlineComment.contains("Deferred until"));

        String deferredDate = headlineComment.replace("Deferred until ", "");
        assertTrue("invalid date format: " + deferredDate, isValidDate(deferredDate, "d MMM yyyy"));
    }

    @ContinueNextStepsOnException
    @Then("^the resource panel is blank$")
    public void the_resource_panel_is_blank() {
        assertEquals("expected 'Configured resources' dropdown to be displayed", "Configured resources", runtimeState.helpdeskManageResourcesPanel.getConfiguredResource());
    }

    @ContinueNextStepsOnException
    @Then("^the resource panel displays \"([^\"]*)\"$")
    public void the_resource_panel_displays(String value) {
        assertTrue("expected " + value + " to be displayed", runtimeState.helpdeskManageResourcesPanel.getConfiguredResource().contains(value));
    }

    @ContinueNextStepsOnException
    @Then("^the jobs are linked$")
    public void the_jobs_are_linked() throws Throwable {
        assertTrue(dbHelperJobs.areJobsLinked(testData.getInt("jobReference"), job.getJobReference()));
    }

    @ContinueNextStepsOnException
    @Then("^a new \"([^\"]*)\" job is saved to the database$")
    public void the_job_generated(String expectedJobType) throws Throwable {

        String description = testData.getString("description") != null ? testData.getString("description") : job.getDescription();

        AssertJobCreated assertJobCreated = new AssertJobCreated(expectedJobType, description, null, jobDao);
        assertionFactory.performAssertion(assertJobCreated);

        Job job = assertJobCreated.getJob();
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("New job reference: " + job.getJobReference());
    }

    @ContinueNextStepsOnException
    @Then("^a new job is saved to the database with \"([^\"]*)\" status$")
    public void a_new_job_is_saved_to_the_database_with_status(String expectedStatus) throws Throwable {
        AssertJobCreated assertJobCreated = new AssertJobCreated(null, testData.getString("description"), expectedStatus, jobDao);
        assertionFactory.performAssertion(assertJobCreated);

        Job job = assertJobCreated.getJob();
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("New job reference: " + job.getJobReference());
    }

    @ContinueNextStepsOnException
    @Then("^each resource status is now \"([^\"]*)\"$")
    public void each_resource_status_is_now(String status) throws Throwable {
        assertTrue("resource status incorrect",runtimeState.helpdeskManageResourcesPanel.getResourceStatus().contains(status));
        assertTrue("resource status incorrect",runtimeState.helpdeskManageResourcesPanel.getAdditionalResourceStatus(normalize(testData.getString("additionalResourceName"))).contains(status));
    }

    @ContinueNextStepsOnException
    @Then("^the ETA date and time provided are displayed for the resource$")
    public void the_ETA_date_and_time_are_displayed_for_the_resource() {
        String expected = "ETA Provided - " + testData.getString("eta");
        String actual = runtimeState.helpdeskManageResourcesPanel.getResourceStatus();
        DateHelper.assertEquals("Expected: " + expected + " but was: " + actual, expected, actual);
    }

}
