package mercury.steps.helpdesk.resources;

import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DeadlockLoserDataAccessException;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.ResourceDao;
import mercury.database.models.Resource;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.apihelper.ApiHelperHangfire;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.HelpdeskCoreDetails;
import mercury.pageobject.web.helpdesk.HelpdeskTimelineTab;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskFundingRequestsPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAdditionalResourcePanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAdditionalResourcesRequiredPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskManageResourcesPanel;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.helpdesk.jobs.HelpdeskAcceptJobSteps;

public class HelpdeskManageResourcesSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private ResourceDao resourceDao;
    @Autowired private CommonSteps commonSteps;
    @Autowired private HelpdeskAcceptJobSteps helpdeskAcceptJobSteps;
    @Autowired private HelpdeskAdviseEtaSteps helpdeskAdviseEtaSteps;
    @Autowired private HelpdeskAddAdditionalResourceSteps helpdeskAddAdditionalResourceSteps;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private HelpdeskAdviseRemovalSteps helpdeskAdviseRemovalSteps;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private ApiHelperHangfire apiHelperHangfire;


    @When("^the Manage Resources panel is viewed$")
    public void the_Manage_Resources_panel_is_viewed() throws Throwable {
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference")).get();
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
        runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
        runtimeState.helpdeskCoreDetails = new HelpdeskCoreDetails(getWebDriver()).get();

        outputHelper.takeScreenshots();
    }

    @And("^add an additional \"([^\"]*)\" resource$")
    public void add_an_additional_resource(String resourceType) throws Throwable {
        runtimeState.helpdeskAddAdditionalResourcePanel = runtimeState.helpdeskManageResourcesPanel.clickAddAdditionalResource();
        runtimeState.helpdeskAddAdditionalResourcePanel.selectCreationReason("Other");
        runtimeState.helpdeskAddAdditionalResourcePanel.sendAdditionalRequestDescription("Updated additional request notes by test automation");
        if (resourceType.equalsIgnoreCase("contractor")) {
            runtimeState.helpdeskAddAdditionalResourcePanel.selectRandomConfiguredContractorResource();
        } else {
            runtimeState.helpdeskAddAdditionalResourcePanel.selectRandomConfiguredResource();
        }
        testData.put("additionalResourceName", runtimeState.helpdeskAddAdditionalResourcePanel.getAdditionalResourceName());
        outputHelper.takeScreenshots();
    }

    @And("^the callout rate is increased$")
    public void the_callout_rate_is_increased() throws Throwable {
        runtimeState.helpdeskAddAdditionalResourcePanel.clickIncreaseCallOut();
        int callOutAmount = DataGenerator.randBetween(200, 500);
        runtimeState.helpdeskAddAdditionalResourcePanel.sendIncreasedCalloutAmount(String.valueOf(callOutAmount));
        testData.addIntegerTag("callOutAmount", callOutAmount);
        runtimeState.helpdeskAddAdditionalResourcePanel.selectIncreaseCalloutReason("Capital Urgent Critical Request");
        runtimeState.helpdeskAddAdditionalResourcePanel.sendIncreaseCalloutNotes("Increase callout charge by test automation");

        runtimeState.scenario.write("Amount: " + callOutAmount);
        runtimeState.scenario.write("Reason: Capital Urgent Critical Request");
        runtimeState.scenario.write("Notes: Increase callout charge by test automation");

        runtimeState.helpdeskManageResourcesPanel.save();
    }

    @When("^a Contractor is selected and saved$")
    public void a_Contractor_is_selected_and_saved() throws IOException {

        runtimeState.helpdeskManageResourcesPanel.selectContractor();

        if (runtimeState.helpdeskManageResourcesPanel.isOverrideRecommendedResourceDisplayed()) {
            runtimeState.helpdeskManageResourcesPanel.enterOverrideRecommendedResourceRequestedBy("Dougle");
            runtimeState.helpdeskManageResourcesPanel.selectRandomOverrideRecommendedResourceReason();
            runtimeState.helpdeskManageResourcesPanel.enterOverrideRecommendedResourceNote("overridden on " + DateHelper.dateAsString(new Date()));
        }

        outputHelper.takeScreenshots();
        runtimeState.helpdeskManageResourcesPanel.save();
    }

    @When("^the manage resource details are saved$")
    public void the_resource_details_are_saved() throws IOException {
        outputHelper.takeScreenshots();
        runtimeState.helpdeskManageResourcesPanel.save();
        if (testData.getString("profileName").equals("Resource Profile")) {
            String additionalResource = dbHelperResources.getResourceNameForJobReference(testData.getInt("jobReference"));
            if (additionalResource == null) {
                throw new PendingException("No Resource found");
            }
            testData.addStringTag("additionalResourceName", additionalResource);
            runtimeState.scenario.write("Additional Resource is: " + testData.getString("additionalResourceName"));
        }
    }

    @When("^an available City Tech resource is selected$")
    public void a_CityTech_is_selected_and_saved() {
        Resource resource = resourceDao.getRandomAvailableCityTech(getTimezone());
        if (resource == null) {
            throw new PendingException("No City Tech found who is currently in hours!");
        }
        runtimeState.helpdeskManageResourcesPanel.selectResource(resource.getName());
        logger.debug("resource: " + resource.getName());
    }

    private String getTimezone() {
        return runtimeState.timezone;
    }

    @When("^Override Recommended Resource details are entered$")
    public void Override_Recommended_Resource_details_are_entered() {
        runtimeState.helpdeskManageResourcesPanel.enterOverrideRecommendedResourceRequestedBy("Dougle");
        runtimeState.helpdeskManageResourcesPanel.selectRandomOverrideRecommendedResourceReason();
        runtimeState.helpdeskManageResourcesPanel.enterOverrideRecommendedResourceNote("overridden on " + DateHelper.dateAsString(new Date()));
    }

    @And("^the resource is removed$")
    public void the_resource_is_removed() throws Throwable {
        String requestedBy = runtimeState.helpdeskRemoveResourcePanel.getRequestedBy();
        String randomText = DataGenerator.GenerateRandomString(7, 10, 3, 3, 0, 0);
        if (requestedBy.isEmpty()) {
            runtimeState.helpdeskRemoveResourcePanel.setRequestedBy(randomText);
        }
        String resourceToBeRemoved = runtimeState.helpdeskRemoveResourcePanel.getResourceNameToBeRemoved();
        String resourceProfile = runtimeState.helpdeskRemoveResourcePanel.getResourceProfile(resourceToBeRemoved.trim());
        if (resourceProfile.contains("(Contractor)")) {
            runtimeState.helpdeskRemoveResourcePanel.selectRandomReason();
        } else if (resourceProfile.contains("(Supply only)")) {
            runtimeState.helpdeskRemoveResourcePanel.selectExactReason("No available resource");
        } else {
            runtimeState.helpdeskRemoveResourcePanel.selectExactReason("Incorrect resource");
        }

        if (testData.getString("resourceRemovalAction") != null && testData.getString("resourceRemovalAction").equals("Cancel")) {
            runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskRemoveResourcePanel.cancel();
        } else {
            runtimeState.scenario.write("Removed resource from job by entering random test data");
            runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskRemoveResourcePanel.save();
        }

        outputHelper.takeScreenshots();
    }

    @And("^the assigned resource is removed from manage resources section$")
    public void the_assigned_resource_is_removed_from_manage_resources_section() throws Throwable {
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
        runtimeState.helpdeskRemoveResourcePanel = runtimeState.helpdeskManageResourcesPanel.selectRemoveResourceAction();
        the_resource_is_removed();
    }

    public void the_assigned_resource_is_removed_from_manage_resources_section_with_resource() throws Throwable {
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
        runtimeState.helpdeskRemoveResourcePanel = runtimeState.helpdeskManageResourcesPanel.selectRemoveResourceActionForResource(testData.getString("additionalResourceName"));
        the_resource_is_removed();
    }

    @And("^the advise removal is confirmed$")
    public void the_advise_removal_is_confirmed() throws Throwable {
        commonSteps.the_action_is_selected("Advise Removal");
        helpdeskAdviseRemovalSteps.removal_confirmed_option_is_selected("is");
        commonSteps.the_button_is_clicked("Save");
    }

    @And("^any additional resources are removed$")
    public void any_additional_resources_are_removed() throws Throwable {

        runtimeState.helpdeskManageResourcesPanel = new HelpdeskManageResourcesPanel(getWebDriver()).get();

        while (runtimeState.helpdeskManageResourcesPanel.actionsButtonIsDisplayed()) {
            runtimeState.helpdeskManageResourcesPanel.selectRemoveResourceAction();
            the_resource_is_removed();
            the_advise_removal_is_confirmed();
        }
    }

    @When("^the additional resource required section is closed and \"([^\"]*)\"$")
    public void the_additional_resource_required_section_is_closed_and_something(String status) throws Throwable {
        if (runtimeState.helpdeskAdditionalResourcesRequiredPanel == null) {
            runtimeState.helpdeskAdditionalResourcesRequiredPanel = new HelpdeskAdditionalResourcesRequiredPanel(getWebDriver()).get();
        }
        runtimeState.helpdeskAdditionalResourcesRequiredPanel.closeAdditionalResource();
        String randomText = DataGenerator.GenerateRandomString(7, 10, 3, 3, 0, 0);

        switch(status) {
        case "Parked":
            runtimeState.helpdeskAdditionalResourcesRequiredPanel.removeAdditionalResourceAndPark(randomText);
            runtimeState.scenario.write("Park Reason is : " + runtimeState.helpdeskAdditionalResourcesRequiredPanel.selectRandomParkReason());
            runtimeState.helpdeskAdditionalResourcesRequiredPanel.selectParkClock();
            runtimeState.scenario.write("Date to Unpark is : " + runtimeState.helpdeskAdditionalResourcesRequiredPanel.getParkDateAndTime());

            while(runtimeState.helpdeskAdditionalResourcesRequiredPanel.closeAdditionalResourceButtonIsDisplayed()) {
                int number = 2;
                runtimeState.helpdeskAdditionalResourcesRequiredPanel.closeAdditionalResource();
                runtimeState.helpdeskAdditionalResourcesRequiredPanel.removeNextAdditionalResourceAndPark(randomText, number);
                runtimeState.scenario.write("Park Reason is : " + runtimeState.helpdeskAdditionalResourcesRequiredPanel.selectNextRandomParkReason(number));
                runtimeState.helpdeskAdditionalResourcesRequiredPanel.selectNextParkClock(number);
                runtimeState.scenario.write("Date to Unpark is : " + runtimeState.helpdeskAdditionalResourcesRequiredPanel.getNextParkDateAndTime(number));
                number ++;
            }
            outputHelper.takeScreenshots();
            runtimeState.helpdeskAdditionalResourcesRequiredPanel.save();
            break;
        case "Cancelled":
            runtimeState.helpdeskAdditionalResourcesRequiredPanel.removeAdditionalResourceAndCancel(randomText);
            testData.put("reason", "Other");

            while(runtimeState.helpdeskAdditionalResourcesRequiredPanel.closeAdditionalResourceButtonIsDisplayed()) {
                int number = 2;
                runtimeState.helpdeskAdditionalResourcesRequiredPanel.closeAdditionalResource();
                runtimeState.helpdeskAdditionalResourcesRequiredPanel.removeNextAdditionalResourceAndCancel(randomText, number);
                number ++;
            }
            outputHelper.takeScreenshots();
            runtimeState.helpdeskAdditionalResourcesRequiredPanel.save();
            break;
        }
        outputHelper.takeScreenshots();
    }

    @And("^Funding requests are displayed$")
    public void funding_requests_are_displayed() throws Throwable {

        if (runtimeState.helpdeskManageResourcesPanel == null || !runtimeState.helpdeskManageResourcesPanel.isDisplayed() ) {
            runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
        }

        if (runtimeState.helpdeskFundingRequestsPanel == null || !runtimeState.helpdeskFundingRequestsPanel.isDisplayed() ) {
            runtimeState.helpdeskFundingRequestsPanel = runtimeState.helpdeskManageResourcesPanel.selectFundingRequestAction();
        }
    }

    @And("^the funding request is authorised$")
    public void the_funding_request_is_authorised() throws Throwable {
        runtimeState.helpdeskFundingRequestsPanel.authorize();
        if (runtimeState.helpdeskFundingRequestsPanel.isReasonDropdownVisible()) {
            runtimeState.helpdeskFundingRequestsPanel.selectRandomReason();
        }
        runtimeState.helpdeskFundingRequestsPanel.selectRandomFunding();
        if (dbHelperSystemToggles.getSystemFeatureToggle("Budget Review") == 1) {
            runtimeState.helpdeskFundingRequestsPanel.selectRandomAnswerForIsPotentialInsuranceQuestion();
        }
        runtimeState.helpdeskFundingRequestsPanel.enterAuthoriseNotes("Updated callout notes by test automation");
        outputHelper.takeScreenshots();
        runtimeState.helpdeskFundingRequestsPanel.save();
    }

    @And("^the job is accepted and an additional resource added$")
    public void the_job_is_accepted_and_an_additional_resource_added() throws Throwable {
        commonSteps.the_action_is_selected("Manage Resources");
        commonSteps.the_action_is_selected("Accept job");
        helpdeskAcceptJobSteps.eta_populated_advised("is not");
        int additionalResourceId = dbHelperResources.getChargeableContractor(true, "without");
        String additionalResourceName = dbHelperResources.getResourceName(additionalResourceId);
        helpdeskAddAdditionalResourceSteps.additional_resource_is_added(additionalResourceName);
        the_resource_details_are_saved();
        runtimeState.helpdeskAdditionalResourcePanel = new HelpdeskAdditionalResourcePanel(getWebDriver()).get();
        runtimeState.helpdeskAdditionalResourcePanel.selectFundingRequestAction();
        runtimeState.helpdeskFundingRequestsPanel = new HelpdeskFundingRequestsPanel(getWebDriver()).get();
        runtimeState.helpdeskFundingRequestsPanel.authorize();
        runtimeState.helpdeskFundingRequestsPanel.selectRandomFunding();
        runtimeState.helpdeskFundingRequestsPanel.enterAuthoriseNotes(DataGenerator.GenerateRandomString(7, 10, 3, 3, 0, 0));
        runtimeState.helpdeskFundingRequestsPanel.save();
        commonSteps.the_action_is_selected("Additional resource accepts the job");
        helpdeskAcceptJobSteps.eta_populated_advised("is not");
        helpdeskAdviseEtaSteps.the_eta_panel_is_displayed();
    }

    @And("^an additional \"([^\"]*)\" resource accepts the job$")
    public void an_additional_resource_accepts_the_job(String resourceType) throws Throwable {
        helpdeskAddAdditionalResourceSteps.an_additional_resource_is_added(resourceType);
        outputHelper.takeScreenshots();
        the_resource_details_are_saved();

        testData.put("additionalResourceStatus", runtimeState.helpdeskManageResourcesPanel.getAdditionalResourceStatus(normalize(testData.getString("additionalResourceName"))));

        if (testData.getString("additionalResourceStatus").equalsIgnoreCase(localize("Awaiting Funding Authorisation")) || testData.getString("additionalResourceStatus").equalsIgnoreCase("Awaiting Funding Authorisation")) {
            commonSteps.the_action_is_selected("Additional resource funding requests");
            the_funding_request_is_authorised();
        }

        if(runtimeState.helpdeskFundingRequestsPanel != null && runtimeState.helpdeskFundingRequestsPanel.isSaveButtonDisplayed()) {
            runtimeState.helpdeskFundingRequestsPanel.save();
        }

        commonSteps.the_action_is_selected("Additional resource accepts the job");
        helpdeskAcceptJobSteps.eta_populated_advised("is not");
        helpdeskAdviseEtaSteps.the_eta_panel_is_displayed();
    }

    @And("^an additional contractor resource accepts the job$")
    public void an_additional_contractor_resource_accepts_the_job() throws Throwable {
        helpdeskAddAdditionalResourceSteps.an_additional_resource_is_added("Contractor");
        outputHelper.takeScreenshots();
        the_resource_details_are_saved();

        try {
            runtimeState.helpdeskFundingRequestsPanel=runtimeState.helpdeskManageResourcesPanel.selectAdditionalResourceFundingRequestAction(testData.getString("additionalResourceName"));
        } catch (Exception e) {
            runtimeState.helpdeskFundingRequestsPanel=runtimeState.helpdeskManageResourcesPanel.selectAdditionalResourceFundingRequestAction(testData.getString("additionalResourceName").split(" ")[0].trim());
        }
        the_funding_request_is_authorised();

        if (runtimeState.helpdeskFundingRequestsPanel != null && runtimeState.helpdeskFundingRequestsPanel.isSaveButtonDisplayed()) {
            runtimeState.helpdeskFundingRequestsPanel.save();
        }

        commonSteps.the_action_is_selected("Additional resource accepts the job");
        helpdeskAcceptJobSteps.eta_populated_advised("is not");
        helpdeskAdviseEtaSteps.the_eta_panel_is_displayed();
    }

    @ContinueNextStepsOnException
    @Then("^the PO document is attached to the job$")
    public void po_document_is_attached_to_the_job() throws Throwable {

        apiHelperHangfire.processPurchaseOrderDocuments();

        // Refresh the page until the attachment count is displayed
        Instant start = Instant.now();
        do {
            POHelper.refreshPage();
            runtimeState.scenario.write("*****Waiting (upto 8 minutes) until the purchase order document is attached to the job*****");
        } while (runtimeState.helpdeskJobPage.getAttachmentCount() != 1 && Duration.between(start, Instant.now()).toMinutes() < 9);

        Map<String, Object> dbData = dbHelperJobs.getJobAttachmentDetails(testData.getInt("jobReference"));

        // Now click the attachment and check the attachment details
        runtimeState.helpdeskAddAttachmentsModal = runtimeState.helpdeskJobPage.clickAttachmentsIcon();
        Grid grid = runtimeState.helpdeskAddAttachmentsModal.getGrid();
        Row row = grid.getRows().get(0);
        outputHelper.takeScreenshot();
        runtimeState.scenario.write("Asserting that the document details are shown correctly for job: " + testData.getInt("jobReference").toString());

        testData.addStringTag("PONumber", dbData.get("PONumber").toString());

        assertTrue("The File Name is not displayed as " + dbData.get("PONumber").toString()+ ".pdf" , row.getCell("File Name").getText().contains(dbData.get("PONumber").toString()));
        assertEquals("The File Type is not displayed as " +  dbData.get("FileType").toString(), dbData.get("FileType").toString(), row.getCell("File Type").getText());
        assertEquals("The Note is not displayed as " +  dbData.get("Note").toString(), dbData.get("Note").toString(), row.getCell("Note").getText());
        assertTrue("The Download button is not displayed", runtimeState.helpdeskAddAttachmentsModal.isDownloadButtonDisplayed());
        runtimeState.helpdeskAddAttachmentsModal.clickCloseButton();
    }

    @When("^the contractor resource status for the job is updated to \"([^\"]*)\"$")
    public void the_resource_status_is_updated_to(String rasStatus) throws Exception {
        int jobId = dbHelperJobs.getJobId(testData.getInt("jobReference"));
        switch (rasStatus) {
        case "on site":
            try {
                dbHelperResources.updateResourceAssignmentStatus(jobId, testData.getString("additionalResourceName"), 9);
            } catch (DeadlockLoserDataAccessException e) {
                // try one more time
                System.err.print("Deadlock occurred when updating resource assignment status. Trying one more time!");
                dbHelperResources.updateResourceAssignmentStatus(jobId, testData.getString("additionalResourceName"), 9);
            }
            dbHelperJobs.updateJobStatus(testData.getInt("jobReference"), 3);
            break;
        default:
            throw new Exception("Unexpected resource assignment status: " + rasStatus);
        }
        runtimeState.helpdeskManageResourcesPanel.refreshPage();
        outputHelper.takeScreenshots();
    }

    @When("^the additional resource is removed$")
    public void the_additional_resource_is_removed() throws Throwable {
        commonSteps.the_action_is_selected("Remove Additional Resource");
        fillAndSaveRemoveResourceDetails();
        commonSteps.the_action_is_selected("Additional Resource Advise Removal");
        fillAndSaveAdviseRemovalDetails();
        runtimeState.helpdeskManageResourcesPanel.clickCloseResourcesPanel();
        outputHelper.takeScreenshots();
    }

    public void fillAndSaveRemoveResourceDetails() throws Throwable {
        String randomText = DataGenerator.GenerateRandomString(7, 10, 3, 3, 0, 0);
        String removeResourceNotes = "Resource removed by test automation: " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskRemoveResourcePanel.setRequestedBy(randomText);
        runtimeState.helpdeskRemoveResourcePanel.selectRandomReason();
        runtimeState.helpdeskRemoveResourcePanel.setNotes(removeResourceNotes);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskRemoveResourcePanel.save();
    }

    public void fillAndSaveAdviseRemovalDetails() throws Throwable {
        String adviseRemovalNotes = "Resource removal advised by test automation: " + DateHelper.dateAsString(new Date());
        runtimeState.helpdeskAdviseRemovalPanel.selectRemovalConfirmed("Yes");
        runtimeState.helpdeskAdviseRemovalPanel.setNotes(adviseRemovalNotes);
        outputHelper.takeScreenshots();
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskAdviseRemovalPanel.save();
    }

    @When("^the removal form is filled and the cancel button is clicked$")
    public void the_removal_form_is_filled_and_cancel_button_is_clicked() throws Throwable {
        testData.addStringTag("resourceRemovalAction", "Cancel");
        the_assigned_resource_is_removed_from_manage_resources_section();
    }
}
