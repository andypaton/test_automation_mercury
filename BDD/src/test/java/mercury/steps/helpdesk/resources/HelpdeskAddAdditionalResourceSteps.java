package mercury.steps.helpdesk.resources;

import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.helpers.StringHelper.normalize;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAdditionalResourcePanel;
import mercury.runtime.RuntimeState;


public class HelpdeskAddAdditionalResourceSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private ApiHelperResources apiHelperResources;

    @And("^an additional \"([^\"]*)\" resource is added$")
    public void an_additional_resource_is_added(String resourceType) throws Throwable {
        POHelper.refreshPage();
        runtimeState.helpdeskAddAdditionalResourcePanel = runtimeState.helpdeskManageResourcesPanel.clickAddAdditionalResource();
        creation_reason_is_selected();
        description_is_entered();
        if (resourceType.equalsIgnoreCase("contractor")) {
            int contractorId = dbHelperResources.getAdditionalContractorWithConfiguredRate(testData.getInt("resourceId"));
            String resource = dbHelperResources.getResourceName(contractorId);
            try {
                runtimeState.helpdeskAddAdditionalResourcePanel.searchAndSelectRandomConfiguredResource(resource);
            } catch (Exception e) {
                // Searching for configured contractor resource when unable to find the resource name (having ') in the dropdown.
                runtimeState.helpdeskAddAdditionalResourcePanel.selectRandomConfiguredContractorResource();
            }
            String additionalResource = runtimeState.helpdeskAddAdditionalResourcePanel.getAdditionalResourceName();
            testData.put("additionalResourceName", additionalResource);
            runtimeState.scenario.write("Additional Resource is: " + testData.getString("additionalResourceName"));

        } else if (resourceType.equalsIgnoreCase("City Tech having phone number without ipad")) {
            String resourceName = dbHelperResources.getResourceName(testData.getInt("resourceId"));
            testData.put("resourceName", resourceName);
            String additionalResourceName = dbHelperResources.getAdditionalCityTechForJobWithoutIpadWithPhoneNumber(testData.getString("resourceName"));
            if (additionalResourceName == null) {
                String resourceProfileName = dbHelperResources.getResourceProfileName(testData.getInt("resourceId"));
                int additionalResourceId = dbHelperResources.getRandomActiveBackupResourceId(resourceProfileName, testData.getInt("resourceId"));
                apiHelperResources.configureResource(additionalResourceId, "mobile", "with", "no");
                additionalResourceName = dbHelperResources.getResourceName(additionalResourceId);
            }
            runtimeState.helpdeskAddAdditionalResourcePanel.searchAndSelectRandomConfiguredResource(additionalResourceName);
            testData.put("additionalResourceName", runtimeState.helpdeskAddAdditionalResourcePanel.getAdditionalResourceName());
            runtimeState.scenario.write("Additional Resource is: " + testData.getString("additionalResourceName"));

        } else if (resourceType.equalsIgnoreCase("Resource Profile")) {
            testData.addStringTag("profileName", "Resource Profile");
            runtimeState.helpdeskAddAdditionalResourcePanel.clickResourceProfileRadioButton();
            int siteId = dbHelperSites.getSiteIdForJobRef(testData.getInt("jobReference"));
            String availableResourceProfile = dbHelperResources.getAvailableResourceProfileForSite(siteId, testData.getString("resourceName"));
            String aliasForAvailableResourceProfile = dbHelperResources.getAliasForResourceProfile(availableResourceProfile);
            if (aliasForAvailableResourceProfile == null) {
                throw new Exception("No available resource found");
            }
            runtimeState.helpdeskAddAdditionalResourcePanel.selectResourceProfile(aliasForAvailableResourceProfile);

        } else {
            String configuredResource = runtimeState.helpdeskAddAdditionalResourcePanel.getConfiguredResource();
            if (configuredResource.contains("No matching configured resources")) {
                runtimeState.helpdeskAddAdditionalResourcePanel.searchAndSelectRandomConfiguredResource("Contractor");
            } else {
                runtimeState.helpdeskAddAdditionalResourcePanel.selectRandomConfiguredResource();
            }
            testData.put("additionalResourceName", runtimeState.helpdeskAddAdditionalResourcePanel.getAdditionalResourceName());
            runtimeState.scenario.write("Additional Resource is: " + testData.getString("additionalResourceName"));
        }

    }

    public void additional_resource_is_added(String resourceName) throws Throwable {
        runtimeState.helpdeskAddAdditionalResourcePanel = runtimeState.helpdeskManageResourcesPanel.clickAddAdditionalResource();
        creation_reason_is_selected();
        description_is_entered();
        runtimeState.helpdeskAddAdditionalResourcePanel.searchAndSelectRandomConfiguredResource(resourceName);
        testData.put("additionalResourceName", runtimeState.helpdeskAddAdditionalResourcePanel.getAdditionalResourceName());
        runtimeState.scenario.write("Additional Resource is: " + testData.getString("additionalResourceName"));
    }

    @And("^creation reason is selected$")
    public void creation_reason_is_selected() throws Throwable {
        runtimeState.helpdeskAddAdditionalResourcePanel.selectRandomCreationReason();
        String createdReason = runtimeState.helpdeskAddAdditionalResourcePanel.getCreationReason();
        testData.addStringTag("createdReason", createdReason);
    }

    @And("^description is entered$")
    public void description_is_entered() throws Throwable {
        String description = "Test description entered on " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskAddAdditionalResourcePanel.sendAdditionalRequestDescription(description);
        testData.addStringTag("description", description);
    }

    @ContinueNextStepsOnException
    @Then("^the additional resource panel section displays the details of added resource$")
    public void the_additional_resource_panel_section_displays_the_details_of_added_resource() throws Throwable {
        runtimeState.helpdeskAdditionalResourcePanel = new HelpdeskAdditionalResourcePanel(getWebDriver()).get();
        runtimeState.scenario.write("Asserting Additional Resource Name: " + testData.getString("additionalResourceName"));
        String actualAdditionalResource = runtimeState.helpdeskAdditionalResourcePanel.getAdditionalResourceDetails();
        String expectedAdditionalResource = normalize(testData.getString("additionalResourceName"));
        assertTrue("Actual ResourceName: " + actualAdditionalResource + "Expected ResourceName: " + expectedAdditionalResource, actualAdditionalResource.contains(expectedAdditionalResource));

        runtimeState.scenario.write("Asserting Created Reason: " + testData.getString("createdReason"));
        String actualCreatedReason = runtimeState.helpdeskAdditionalResourcePanel.getCreatedReason();
        assertTrue(
                "Actual CreatedReason: " + actualCreatedReason
                + "Expected CreatedReason: "
                + testData.getString("createdReason"),
                actualCreatedReason.equals(testData.getString("createdReason")));

        runtimeState.scenario.write("Asserting Notes: " + testData.getString("description"));
        String actualNotes = runtimeState.helpdeskAdditionalResourcePanel.getNotes();
        assertTrue(
                "Actual Notes: " + actualNotes
                + "Expected Notes: "
                + testData.getString("description"),
                actualNotes.equals(testData.getString("description")));

        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^I have \"([^\"]*)\" profile$")
    public void i_have_given_profile(String userProfile) throws Throwable {
        testData.addStringTag("userProfile", userProfile);
    }
}