package mercury.steps.portal.admin;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.gridV3.Grid;
import mercury.runtime.RuntimeState;

public class ManageDeptHeadUserAssignmentSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private OutputHelper outputHelper;


    @When("^the logged on user is selected as department head$")
    public void a_department_head_is_selected() throws Throwable {
        String deptHead = testData.getString("impersonatedResourceName");
        runtimeState.manageDeptHeadUserAssignmentPage.selectDepartmentHead(deptHead);
    }

    @When("^a random user is selected as department head$")
    public void a_random_department_head_is_selected() throws Throwable {
        String deptHead = runtimeState.manageDeptHeadUserAssignmentPage.selectRandomDepartmentHead();
        runtimeState.scenario.write("Department Head selected: " + deptHead);
        testData.put("deptHead", deptHead);
    }

    @When("^a colleague is selected to fall under their management$")
    public void a_colleague_is_selected_to_fall_under_their_management() throws Throwable {
        Grid grid = runtimeState.manageDeptHeadUserAssignmentPage.getColleaguesTable();
        // select a random colleague
        String colleague = grid.getColumnText("Name").get(RandomUtils.nextInt(0, grid.getRows().size() - 1));
        testData.put("colleague", colleague);
        runtimeState.scenario.write("Selecting: " + colleague);
        runtimeState.manageDeptHeadUserAssignmentPage.search(colleague);
        runtimeState.manageDeptHeadUserAssignmentPage.selectColleague(colleague);
    }

    @When("^the managed changes are saved$")
    public void the_managed_changes_are_saved() {
        outputHelper.takeScreenshots();
        runtimeState.manageDeptHeadUserAssignmentPage.saveChanges();
        runtimeState.manageDeptHeadUserAssignmentPage.confirmSavedChanges();
    }

    @ContinueNextStepsOnException
    @Then("^the updated resource relationship is stored in the database$")
    public void the_updated_resource_relationship_is_stored_in_the_database() {
        int parentId = dbHelperResources.getPortalResourceId(testData.getString("deptHead"));
        int childId = dbHelperResources.getPortalResourceId(testData.getString("colleague"));
        assertTrue(dbHelperResources.helpDeskResourceRelationshipExists(parentId, childId));

        // revert the change
        runtimeState.scenario.write("Reverting changes");
        runtimeState.manageDeptHeadUserAssignmentPage.search(testData.getString("colleague"));
        runtimeState.manageDeptHeadUserAssignmentPage.deselectColleague(testData.getString("colleague"));
        runtimeState.manageDeptHeadUserAssignmentPage.saveChanges();
        runtimeState.manageDeptHeadUserAssignmentPage.confirmSavedChanges();
    }

}
