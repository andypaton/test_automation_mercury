package mercury.steps.helpdesk.search;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.runtime.RuntimeState;

public class HelpdeskSearchForIncidentSteps {

    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TzHelper tzHelper;

    @And("^a search is run for an Incident reference number$")
    public void a_search_is_for_an_incident_reference_number() throws Throwable {
        Integer incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumber();
        if (incidentReference == null) {
            throw new PendingException("No test data found");
        }
        testData.put("incidentReference", Integer.valueOf(incidentReference));
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        if (testData.getString("profileName").trim().equalsIgnoreCase("OSS Operator")) {
            runtimeState.helpdeskSearchBar.searchForIncident(incidentReference);
        } else {
            runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
        }
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for an Incident reference number with a note$")
    public void a_search_is_run_for_an_Incident_reference_number_with_note() throws Throwable {
        Integer incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumberWithNote();
        if (incidentReference == null) {
            throw new PendingException("No test data found");
        }
        testData.put("incidentReference", Integer.valueOf(incidentReference));
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
        int siteId = dbHelperSites.getSiteId(runtimeState.helpdeskViewIncidentPage.getSiteValue());
        Date storeTime = tzHelper.getCurrentTimeAtSite(siteId);
        testData.put("storeTime", storeTime);
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for an Incident reference number with site closed$")
    public void a_search_is_run_for_an_Incident_reference_number_with_site_closed() throws Throwable {
        Integer incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumberWithSiteClosedAndNoEscalationCriteria();
        if (incidentReference == null) {
            throw new PendingException("No test data found");
        }
        testData.put("incidentReference", Integer.valueOf(incidentReference));
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
        int siteId = dbHelperSites.getSiteId(runtimeState.helpdeskViewIncidentPage.getSiteValue());
        Date storeTime = tzHelper.getCurrentTimeAtSite(siteId);
        testData.put("storeTime", storeTime);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^user search for an incident reference number via search bar$")
    public void user_search_for_incidents_via_the_search_bar() throws Throwable {
        a_search_is_for_an_incident_reference_number();
    }

    @And("^a search is run for an Incident reference number with a linked job$")
    public void a_search_is_run_for_an_Incident_reference_number_with_a_linked_job() throws Throwable {
        Integer incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumberWithLinkedJob();
        if (incidentReference == null) {
            throw new PendingException("No test data found");
        }
        testData.put("incidentReference", Integer.valueOf(incidentReference));
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for an Incident reference number with status Incident Telephone Escalation Callback$")
    public void a_search_is_run_for_an_Incident_reference_number_with_status_Incident_Telephone_Escalation_Callback() throws Throwable {
        Integer incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumberWithStatusTelephoneEscalationCallback();
        if (incidentReference == null) {
            throw new PendingException("No test data found");
        }
        testData.put("incidentReference", Integer.valueOf(incidentReference));
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for an Incident reference number with an escalation contacts$")
    public void a_search_is_for_an_incident_reference_number_with_an_escalation_contacts() throws Throwable {
        Integer incidentReference = dbHelperIncidents.getRandomIncidentReferenceNumberWithEscalationContact(testData.getString("incidentType"));
        if (incidentReference == null) {
            throw new PendingException("No test data found");
        }
        testData.addIntegerTag("incidentReference", incidentReference);
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
        outputHelper.takeScreenshots();
    }
    
    @And("^a search is run for an Incident reference number via a search bar$")
    public void a_search_is_for_an_incident_reference_number_via_searchbar(Integer incidentReference) throws Throwable {
        testData.addIntegerTag("incidentReference", incidentReference);
        runtimeState.scenario.write("test data selected: incidentReference = " + incidentReference);
        runtimeState.helpdeskViewIncidentPage = runtimeState.helpdeskSearchBar.searchForAnIncident(incidentReference);
    }
}
