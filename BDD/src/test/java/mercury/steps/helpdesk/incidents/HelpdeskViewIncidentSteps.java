package mercury.steps.helpdesk.incidents;

import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.SHORT2;
import static mercury.helpers.Globalisation.localize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Duration.TWO_SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static mercury.helpers.StringHelper.normalize;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskIncidentActionsDropdown;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskViewIncidentPage;
import mercury.runtime.RuntimeState;

public class HelpdeskViewIncidentSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TzHelper tzHelper;

    @And("^the 'Confirm' button is clicked on Cancel Incident pop up$")
    public void the_yes_button_is_clicked_on_cancel_incident_pop_up() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.helpdeskIncidentTimelineTab = runtimeState.helpdeskCancelIncident.clickCancelIncidentConfirmButton();
        runtimeState.scenario.write("Yes button is clicked on Cancel Incident pop up");
    }

    @ContinueNextStepsOnException
    @Then("^the incident status is now \"([^\"]*)\"$")
    public void the_incident_status_is_now(String status) throws Throwable {
        POHelper.refreshPage();
        String expectedStatus = localize(status);
        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskViewIncidentPage.assertStatusEqualsIgnoreCase(expectedStatus));
    }

    @ContinueNextStepsOnException
    @Then("^the Incident Summary page is displayed$")
    public void incident_summary_page_is_displayed() throws Throwable {

        runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();
        int siteId = dbHelperSites.getSiteIdForIncidentRef(runtimeState.helpdeskViewIncidentPage.getIncidenceReference());

        Date storeTime = tzHelper.getCurrentTimeAtSite(siteId);
        testData.put("storeTime", storeTime);

        assertThat("Incident Type is not shown correctly on the left header of the View Incident page ", runtimeState.helpdeskViewIncidentPage.getIncidentType(), is(equalToIgnoringCase(testData.getString("incidentType"))));
        assertEquals("Incident Status is not shown correctly on the View Incident page ", "Incident Initial Review", runtimeState.helpdeskViewIncidentPage.getIncidentInitialStatus());
        assertEquals("Site Value is not shown correctly on the View Incident page", normalize(testData.getString("siteName")), normalize(runtimeState.helpdeskViewIncidentPage.getSiteValue()));
        assertEquals("Caller is not shown properly on the View Incident page", normalize(runtimeState.helpdeskViewIncidentPage.getCallerName()), normalize(testData.getString("caller")));
        assertEquals("The description is not shown correctly on the View Incident page", normalize(runtimeState.helpdeskViewIncidentPage.getDescriptionValue()), normalize(testData.getString("description")));
    }

    @ContinueNextStepsOnException
    @Then("^a new tab is opened with the incident details$")
    public void a_new_tab_is_opened_with_the_incident_details() throws Throwable {
        runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();
        assertTrue("Expected incident reference: " + testData.getInt("incidentReference"), testData.getInt("incidentReference") == runtimeState.helpdeskViewIncidentPage.getIncidenceReference());
        assertEquals("Expected site name: " + testData.getString("siteName"), testData.getString("siteName"), runtimeState.helpdeskViewIncidentPage.getSiteValue());
    }

    @ContinueNextStepsOnException
    @Then("^the View Incident page is displayed correctly$")
    public void view_incident_page_is_displayed() throws Throwable {

        runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();
        String siteClosed = runtimeState.helpdeskViewIncidentPage.getSiteClosedValue();
        String siteReopened = runtimeState.helpdeskViewIncidentPage.getSiteReopenedValue();
        String departmentClosed = runtimeState.helpdeskViewIncidentPage.getDepartmentClosedValue();
        String departmentReopened = runtimeState.helpdeskViewIncidentPage.getDepartmentReopenedValue();

        int siteId = dbHelperSites.getSiteId(runtimeState.helpdeskViewIncidentPage.getSiteValue());
        Date storeTime = tzHelper.getCurrentTimeAtSite(siteId);
        testData.put("storeTime", storeTime);

        assertEquals("Incident Type is not shown correctly on the left header of the View Incident page", testData.getString("incidentType"), runtimeState.helpdeskViewIncidentPage.getIncidentType());
        assertEquals("Incident Status is not shown correctly on the View Incident page ", "Incident Initial Review", runtimeState.helpdeskViewIncidentPage.getIncidentInitialStatus());
        assertEquals("Site Value is not shown correctly on the View Incident page", normalize(testData.getString("siteName")), normalize(runtimeState.helpdeskViewIncidentPage.getSiteValue()));
        assertEquals("Caller is not shown properly on the View Incident page", normalize(runtimeState.helpdeskViewIncidentPage.getCallerName()), normalize(testData.getString("caller")));
        assertTrue("Site Closed value is not shown correctly on the View Incident Page ", siteClosed.contains(testData.getString("siteClosedValue")));
        assertTrue("Site Reopened value is not shown correctly on the View Incident Page ", siteReopened.contains(testData.getString("siteReopenedValue")));
        assertTrue("Department Closed value is not shown correctly on the View Incident Page ", departmentClosed.contains(testData.getString("departmentClosedValue")));
        assertTrue("Department Reopened value is not shown correctly on the View Incident Page ", departmentReopened.contains(testData.getString("departmentReopenedValue")));
        assertEquals("The description is not shown correctly on the View Incident page", runtimeState.helpdeskViewIncidentPage.getDescriptionValue(), testData.getString("description"));
    }

    @ContinueNextStepsOnException
    @Then("^the site questions are displayed correctly when the site is \"([^\"]*)\" and department is \"([^\"]*)\"$")
    public void the_site_questions_are_displayed_correctly_for_site_option_and_department_option(String siteValue, String departmentValue) throws Throwable {
        runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();

        if (siteValue.equals("open")) {
            assertEquals("Site Value is not shown correctly on the View Incident Page ", testData.getString("siteClosedValue"), runtimeState.helpdeskViewIncidentPage.getSiteClosedValue());
        }

        String expectedDepartmentClosedValue = testData.getString("departmentClosedValue") + " - " + DateHelper.convert(testData.getString("departmentClosedDate"), SHORT2, MEDIUM);
        assertThat("Department Closed value is not shown correctly on the View Incident Page ", runtimeState.helpdeskViewIncidentPage.getDepartmentClosedValue(), is(equalToIgnoringCase(expectedDepartmentClosedValue)));

        String expectedValue = null;
        String actualValue;

        if(departmentValue.equals("closed")) {
            expectedValue = testData.getString("departmentReopenedValue") + " - estimated for " + DateHelper.convert(testData.getString("departmentLikelyToReopenDate"), SHORT2, MEDIUM);
        } else if (departmentValue.equals("open")) {
            expectedValue = testData.getString("departmentReopenedValue") + " - " + DateHelper.convert(testData.getString("departmentReopenedDate"), SHORT2, MEDIUM);
        }
        actualValue = runtimeState.helpdeskViewIncidentPage.getDepartmentReopenedValue();
        assertThat("Department Reopened value is not shown correctly on the View Incident Page ", actualValue, is(equalToIgnoringCase(expectedValue)));
    }

    @And("^the incident is reviewed$")
    public void the_incident_is_reviewed() throws Throwable {
        runtimeState.helpdeskIncidentActionsDropdown = new HelpdeskIncidentActionsDropdown(getWebDriver());
        runtimeState.helpdeskLogAnIncidentPage = runtimeState.helpdeskIncidentActionsDropdown.reviewIncident();
        runtimeState.helpdeskLogAnIncidentPage.clickCompleteReviewCheckbox();
    }

    @ContinueNextStepsOnException
    @Then("^the description is displayed correctly$")
    public void the_description_is_displayed_correctly() throws Throwable {
        assertTrue("The updated description is not shown correctly on the View Incident page",runtimeState.helpdeskViewIncidentPage.getDescriptionValue().contains(testData.getString("description")));
    }

    @ContinueNextStepsOnException
    @Then("^the core details and site questions are displayed correctly$")
    public void the_core_details_and_site_questions_are_displayed_correctly() throws Throwable {
        String caller[] = testData.getString("caller").split(" ");
        assertEquals("The Caller is not shown correctly on the View Incident page",caller[0],runtimeState.helpdeskViewIncidentPage.getCallerName());
        assertEquals("Site Closed value is not shown correctly on the View Incident Page ",runtimeState.helpdeskViewIncidentPage.getSiteClosedValue(),testData.getString("siteClosedValue"));
        assertEquals("Department Closed value is not shown correctly on the View Incident Page ",runtimeState.helpdeskViewIncidentPage.getDepartmentClosedValue(),testData.getString("departmentClosedValue"));
    }

    @ContinueNextStepsOnException
    @Then("^user can view an incident details$")
    public void user_can_view_an_incident_details() throws Throwable {
        runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();
    }

    @ContinueNextStepsOnException
    @Then("^user can not view incident details and an error message is displayed$")
    public void user_can_not_view_incident_details() throws Throwable {
        assertEquals("Your account is not configured to view incidents. Contact a helpdesk supervisor if you should have access",runtimeState.helpdeskSearchBar.getIncidentErrorMessage());
    }

    @ContinueNextStepsOnException
    @Then("^user can access linked assets panel on job view$")
    public void user_can_access_linked_assets_panel_on_job_view() throws Throwable {
        runtimeState.helpdeskIncidentsLinkedJobsModal = runtimeState.helpdeskViewIncidentPage.clickLinkedJob();
        outputHelper.takeScreenshots();
        Integer jobReferenceNumber = dbHelperIncidents.getJobReferenceNumber(testData.getInt("incidentReference"));
        if (jobReferenceNumber == null) {
            throw new PendingException("No test data found");
        }
        runtimeState.helpdeskJobPage = runtimeState.helpdeskIncidentsLinkedJobsModal.clickOpenButton(jobReferenceNumber);
        outputHelper.takeScreenshots();
    }

    @And("^the user clicks \"([^\"]*)\" link on Incidents monitor page$")
    public void user_clicks_link_on_incidents_monitor_page(String link) throws Throwable {

        switch (link) {

        case "Pending To Do":
            runtimeState.helpdeskIncidentMonitorPage = runtimeState.helpdeskSearchBar.clickIncidentMonitor();
            runtimeState.helpdeskIncidentMonitorPage.clickPendingToDoLink();
            break;

        case "Follow ups":
            runtimeState.helpdeskIncidentMonitorPage = runtimeState.helpdeskSearchBar.clickIncidentMonitor();
            runtimeState.helpdeskIncidentMonitorPage.clickFollowUpLink();
            break;

        case "Reviews":
            runtimeState.helpdeskIncidentMonitorPage = runtimeState.helpdeskSearchBar.clickIncidentMonitor();
            runtimeState.helpdeskIncidentMonitorPage.clickReviewsLink();
            break;

        default:
            throw new Exception("Unexpected option on Incident monitor page: " + link);
        }

    }

    @ContinueNextStepsOnException
    @Then("^the incident status is changed to (?:|one of )\"([^\"]*)\"$")
    public void the_incident_status_is_changed_to(String incidentStatus) throws Throwable {
        runtimeState.helpdeskViewIncidentPage = new HelpdeskViewIncidentPage(getWebDriver()).get();
        String actualStatus = runtimeState.helpdeskViewIncidentPage.getIncidentInitialStatus();
        if(incidentStatus.contains("/")) {
            String[] status = incidentStatus.split("/");
            assertTrue("Status not found. Status: " + status + " .Actualstatus: " + actualStatus, Arrays.stream(status).anyMatch(x -> x.equalsIgnoreCase(actualStatus)));
        } else {
            assertTrue("Status not found. Incident Status: " + incidentStatus + " . Actualstatus: " + actualStatus, incidentStatus.equalsIgnoreCase(actualStatus));
        }
    }
}
