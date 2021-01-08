package mercury.steps.helpdesk.incidents;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.runtime.RuntimeState;
import static mercury.helpers.StringHelper.normalize;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HelpdeskIncidentsRecentlyViewedSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;


    @When("^a search is run for \"([^\"]*)\" incidents$")
    public void a_search_is_run_for_incidents(Integer count) throws Throwable {

        List<Integer> incidentReferences = dbHelperIncidents.getRandomIncidentReferences(count);
        runtimeState.scenario.write("Searching for incident references: "+ incidentReferences.toString());
        testData.put("incidentReferences", incidentReferences);
        for (int incidentReference : incidentReferences) {
            runtimeState.helpdeskHomePage.selectHomeTab();
            runtimeState.helpdeskSearchBar.searchForIncident(incidentReference);
            runtimeState.helpdeskHomePage.closeActiveTab();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Last Viewed list displays (?:upto |)the last \"([^\"]*)\" incident numbers with store names$")
    public void the_Last_Viewed_list_displays_the_last_incident_numbers_are_displayed_with_store_names(Integer count) throws Throwable {
        runtimeState.helpdeskHomePage.selectHomeTab();
        List<Integer> lastViewedIncidents = runtimeState.helpdeskHomePage.getLastViewedIncidents();
        List<String> lastViewedIncidentSiteNames = runtimeState.helpdeskHomePage.getLastViewedSites();
        List<Integer> incidentReferences = testData.getIntList("incidentReferences");

        for (int i = 1; i <= count; i++) {

            int incidentReference = incidentReferences.get(incidentReferences.size() - i);
            String siteName = normalize(dbHelperSites.getSiteNameForIncidentRef(incidentReference));
            runtimeState.scenario.write("Asserting presence of : " + incidentReference + " (" + siteName + ")");

            assertTrue("Expected list to contain incident: " + incidentReference, lastViewedIncidents.contains(incidentReference));
            assertTrue("Expected list to contain site name: " + siteName, lastViewedIncidentSiteNames.contains(siteName));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Last Viewed incident list is updated immediately$")
    public void the_Last_Viewed_list_is_updated_immediately() throws Throwable {
        runtimeState.helpdeskHomePage.selectHomeTab();
        List<Integer> lastViewedIncidents = runtimeState.helpdeskHomePage.getLastViewedIncidents();
        List<String> lastViewedWhen = runtimeState.helpdeskHomePage.getLastViewedWhen();
        List<Integer> incidentReferences = testData.getIntList("incidentReferences");

        int incidentReference = incidentReferences.get(incidentReferences.size() - 1);
        assertTrue("Expected list to contain incident: " + incidentReference, lastViewedIncidents.get(0) == incidentReference);
        assertThat("Expected 'updated' to be: a few seconds ago " , lastViewedWhen.get(0), anyOf(equalTo("a few seconds ago"), equalTo("a minute ago"), equalTo("2 minutes ago"))) ;
    }

    @When("^an incident is selected from the Last Viewed list$")
    public void an_incident_is_selected_from_the_Last_Viewed_list() throws Throwable {

        List<Integer> lastViewedIncidents = runtimeState.helpdeskHomePage.getLastViewedIncidents();
        List<String> lastViewedSites = runtimeState.helpdeskHomePage.getLastViewedSites();

        if (lastViewedIncidents.isEmpty()) {
            // first ensure the Last Viewed list has entries
            a_search_is_run_for_incidents(1);
            lastViewedIncidents = runtimeState.helpdeskHomePage.getLastViewedIncidents();
            lastViewedSites = runtimeState.helpdeskHomePage.getLastViewedSites();
        }
        outputHelper.takeScreenshots();
        testData.addIntegerTag("incidentReference", lastViewedIncidents.get(0));
        testData.addStringTag("siteName", lastViewedSites.get(0));
        runtimeState.scenario.write("Last Viewed items: " + lastViewedIncidents.toString());
        runtimeState.scenario.write("Selecting Last Viewed item: " + lastViewedIncidents.get(0));
        runtimeState.helpdeskHomePage.selectLastViewedItem(lastViewedIncidents.get(0));
    }
}
