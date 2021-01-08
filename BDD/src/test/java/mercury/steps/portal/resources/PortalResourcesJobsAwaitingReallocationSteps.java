package mercury.steps.portal.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.runtime.RuntimeState;

public class PortalResourcesJobsAwaitingReallocationSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TestData testData;


    @And("^the job is reallocated to other city resource ((?:with|without)) ipad$")
    public void the_job_is_reallocated_to_other_resource_ipad_phone(String ipad) throws Throwable {
        String status = dbHelperResources.getResourceAssignmentStatus(testData.getInt("jobReference"), testData.getInt("jobResourceId")).equals("Awaiting Parts") ? "Work transferred with parts" : "Work transferred without parts";
        testData.put("status", status);

        String jobResourceName = dbHelperResources.getResourceName(testData.getInt("jobResourceId"));
        testData.put("jobResourceName", jobResourceName);

        boolean withIpad = ipad.equalsIgnoreCase("with") ? true : false;

        Integer reallocatedResourceId = null;
        String reallocatedResourceName = null;
        for (int attempts = 0; attempts < 5; attempts++) {
            Map<String, Object> dbData = dbHelperResources.getRandomCityTechWithIpad(withIpad);
            reallocatedResourceId = Integer.valueOf(dbData.get("Id").toString());
            reallocatedResourceName = dbData.get("Name").toString().trim();
            try {
                runtimeState.jobsAwaitingReallocationPage.selectACoveringResource(reallocatedResourceName);
                break;
            } catch (NoSuchElementException nse) {
                // alternative resource not found in drop down - possibly been updated by other scenario running in parallel
                // try next attempt!
            }
        }

        testData.put("alternativeResource", reallocatedResourceName);
        testData.put("reallocatedResourceId", reallocatedResourceId);
        testData.put("reallocatedResourceName", reallocatedResourceName);
        testData.put("reallocatedResourceType", "City Resource");
        runtimeState.scenario.write("Reallocating to resource: " + reallocatedResourceName);
        outputHelper.takeScreenshots();
        runtimeState.jobsAwaitingReallocationPage.clickReallocateButton();
    }

    @ContinueNextStepsOnException
    @Then("^the 'Reallocate' button in the 'Jobs Awaiting Reallocation for Resource' page is disabled until a covering resource is selected$")
    public void the_Reallocate_button_in_the_Jobs_Awaiting_Reallocation_for_Resource_page_is_disabled_until_a_covering_resource_is_selected() {
        runtimeState.scenario.write("Asserting 'Reallocate' button is disabled");
        assertFalse("Reallocate is enabled in the 'Jobs Awaiting Reallocation' page. ", runtimeState.jobsAwaitingReallocationForResourcePage.isReAllocateButtonEnabled());

        runtimeState.scenario.write("Selecting random covering resource");
        runtimeState.jobsAwaitingReallocationForResourcePage.selectRandomCoveringResource();

        runtimeState.scenario.write("Asserting 'Reallocate' button is enabled");
        assertTrue("Reallocate is enabled in the 'Jobs Awaiting Reallocation' page. ", runtimeState.jobsAwaitingReallocationForResourcePage.isReAllocateButtonEnabled());
    }

    @ContinueNextStepsOnException
    @Then("^the 'Reallocate' button in the 'Jobs Awaiting Reallocation' page is disabled until a covering resource is selected$")
    public void the_Reallocate_button_in_the_Jobs_Awaiting_Reallocation_page_is_disabled_until_a_covering_resource_is_selected() {
        runtimeState.scenario.write("Asserting 'Reallocate' button is disabled");
        assertFalse("Reallocate is enabled in the 'Jobs Awaiting Reallocation' page. ", runtimeState.jobsAwaitingReallocationPage.isReAllocateButtonEnabled());

        runtimeState.scenario.write("Selecting random covering resource");
        runtimeState.jobsAwaitingReallocationPage.selectRandomCoveringResource();

        runtimeState.scenario.write("Asserting 'Reallocate' button is enabled");
        assertTrue("Reallocate is enabled in the 'Jobs Awaiting Reallocation' page. ", runtimeState.jobsAwaitingReallocationPage.isReAllocateButtonEnabled());
    }

    @ContinueNextStepsOnException
    @Then("^covering resources include \"([^\"]*)\"$")
    public void covering_resources_include(String arg) {
        String[] resources = arg.split(",");
        try {
            // check if dropdown headers contains requested string
            List<String> dropdownHeaders = runtimeState.jobsAwaitingReallocationPage.getCoveringResourceHeaders();

            for (String resource : Arrays.asList(resources)) {
                boolean found = false;
                for (String header : dropdownHeaders) {
                    if (header.contains(resource.trim())) {
                        found = true;
                        runtimeState.scenario.write("Covering resources contains: " + header);
                    }
                }
                assertTrue("Not displayed in drop down list: " + resource, found);
            }

        } catch (AssertionError e) {
            // check if dropdown resources contains requested string
            List<String> coveringResources = runtimeState.jobsAwaitingReallocationPage.getCoveringResources();
            outputHelper.takeScreenshot();

            for (String resource : Arrays.asList(resources)) {
                boolean found = false;
                for (String coveringResource : coveringResources) {
                    if (coveringResource.contains(resource.trim())) {
                        found = true;
                        runtimeState.scenario.write("Covering resources contains: " + coveringResource);
                    }
                }
                assertTrue("Not displayed in drop down list: " + resource, found);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^only available resources are included in covering resources list$")
    public void only_available_resources_are_included_in_covering_resources_list() {
        List<String> coveringResources = runtimeState.jobsAwaitingReallocationPage.getCoveringResources();
        runtimeState.scenario.write("Verifying all displayed covering resources are available: " + coveringResources.toString());

        // verify all displayed resources are available
        for (String coveringResource : coveringResources) {
            if ( !coveringResource.isEmpty() && !coveringResource.toLowerCase().contains("resource") ) {
                int pos = coveringResource.lastIndexOf("(");
                String jobTitle = coveringResource.substring(pos, coveringResource.length());
                String name = coveringResource.replace(jobTitle, "").trim();
                assertTrue("Covering Resource is not available and should not be displayed: " + name, dbHelperResources.isResourceAvailable(name));
            }
        }

        // verify an unavailable resource is not displayed
        Map<String, Object> absentResource = dbHelperResources.getAbsentResource(true);
        String resourceName = (String) absentResource.get("Name");
        runtimeState.scenario.write("Verifying absent resource is not on the list: " + resourceName + ", StartAt: " + absentResource.get("StartAt") + ", EndAt: " + absentResource.get("EndAt"));
        assertFalse("Covering Resource is not available and should not be displayed: " + resourceName, coveringResources.toString().contains(resourceName.trim()));
    }

    @When("^Show Contractors is selected$")
    public void show_Contractors_is_selected() throws Throwable {
        runtimeState.jobsAwaitingReallocationPage.showContractors();
    }
}
