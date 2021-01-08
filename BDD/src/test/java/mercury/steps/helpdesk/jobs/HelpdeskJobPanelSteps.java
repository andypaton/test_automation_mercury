package mercury.steps.helpdesk.jobs;

import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Duration.TWO_SECONDS;
import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.database.models.SiteView;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.helpdesk.HelpdeskTimelineTab;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobActionsPanel;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskQuotesPanel;
import mercury.runtime.RuntimeState;

public class HelpdeskJobPanelSteps {

    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private OutputHelper outputHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private SiteView siteView;
    @Autowired private TestData testData;


    @ContinueNextStepsOnException
    @Then("^the quote panel is displayed with approver$")
    public void the_quote_panel_is_displayed_with_approver() throws Throwable {
        int siteId = testData.getInt("siteId") != null ? testData.getInt("siteId") : siteView.getId();
        String expectedApprover = dbHelperSites.getRfmForSite(siteId).get("Name").toString().trim();
        runtimeState.helpdeskQuotesPanel = new HelpdeskQuotesPanel(getWebDriver()).get();
        String actualApprover = runtimeState.helpdeskQuotesPanel.getApprover();
        runtimeState.scenario.write("Asserting that expected approver (" + expectedApprover + ") and actual approver (" + actualApprover + ") are the same");
        assertEquals("Expected approver " + expectedApprover, expectedApprover, actualApprover);
    }

    @ContinueNextStepsOnException
    @Then("^the Job Details page is displayed$")
    public void manage_resources_displayed() throws Throwable {
        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
        runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
        outputHelper.takeScreenshots();
    }

    @And("^the job resource status is (?:still |now |)\"([^\"]*)\"$")
    public void the_job_resource_status_contains(String resourceStatus) {
        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskJobPage.assertResourceStatusContains(resourceStatus));
        testData.put("resourceStatus", resourceStatus);
    }

    @ContinueNextStepsOnException
    @Then("^the [Cc]lient [Ss]tatus is (?:still |now |)\"([^\"]*)\"$")
    public void the_client_status_contains(String clientStatus) {
        clientStatus = localize(clientStatus);
        runtimeState.scenario.write("Asserting client status is : " + clientStatus);
        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskJobPage.assertClientStatusEquals(clientStatus));
    }

    @ContinueNextStepsOnException
    @Then("^the Resource Status and Client Status are not updated$")
    public void the_Resource_Status_and_Client_Status_are_not_updated() {
        assertEquals("Unexpected resource status", testData.getString("origResourceStatus"), runtimeState.helpdeskJobPage.getResourceStatus());
        assertEquals("Unexpected client status", testData.getString("origClientStatus"), runtimeState.helpdeskJobPage.getClientStatus());
    }

    @ContinueNextStepsOnException
    @Then("^the Resource Status is not updated$")
    public void the_Resource_Status_is_not_updated() {
        assertEquals("Unexpected resource status", testData.getString("origResourceStatus"), runtimeState.helpdeskJobPage.getResourceStatus());
    }

    @ContinueNextStepsOnException
    @Then("^the job displays Resource status \"([^\"]*)\" and Client status \"([^\"]*)\"$")
    public void the_job_displays_Resource_status_and_Client_status(String resourceStatus, String clientStatus) throws Throwable {
        clientStatus = localize(clientStatus);
        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskJobPage.assertResourceStatusContains(resourceStatus));
        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskJobPage.assertClientStatusEquals(clientStatus));
    }

    @ContinueNextStepsOnException
    @Then("^the client status will change back to the status before the job was cancelled$")
    public void the_Client_status_will_change_back_to_the_status_before_the_job_was_cancelled() throws Throwable {
        String expectedClientStatus = testData.getString("clientStatusPriorToCancellation");
        //		expectedClientStatus = "Tech Bureau Triage".equals(expectedClientStatus) ? "Logged" : expectedClientStatus;   // see MCP-2307
        runtimeState.scenario.write("Expected client status: " + expectedClientStatus);
        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskJobPage.assertClientStatusEquals(expectedClientStatus));
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" text is shown on the convert to quote job screen$")
    public void text_is_shown_on_the_convert_to_quote_job_screen(String expectedText) throws Throwable {
        runtimeState.helpdeskJobActionsPanel = new HelpdeskJobActionsPanel(getWebDriver()).get();
        String actualText = runtimeState.helpdeskJobActionsPanel.getContentText();
        runtimeState.scenario.write("Asserting text: " + expectedText +  " is displayed");
        assertEquals("Convert to quote job text is not displayed", expectedText, actualText);
        outputHelper.takeScreenshots();
    }

    @And("^the job resource status is one of \"([^\"]*)\"$")
    public void the_job_resource_status_contains_one_of(String resourceStatus) {
        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskJobPage.assertResourceStatusContainsOneOf(resourceStatus));
        testData.put("resourceStatus", runtimeState.helpdeskJobPage.getResourceStatus());
    }

}
