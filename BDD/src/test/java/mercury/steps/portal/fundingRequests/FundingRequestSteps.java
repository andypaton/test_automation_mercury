package mercury.steps.portal.fundingRequests;

import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.DataTable;
import cucumber.api.java.en.When;
import mercury.database.dao.JobViewDao;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.runtime.RuntimeState;

public class FundingRequestSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private ApiHelperResources apiHelperResources;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;

    @When("^the funding request is approved with random funding route$")
    public void the_funding_request_is_approved_with_random_funding_route() throws Exception {
        runtimeState.initialFundingRequestsApprovalPage.approve();
        String fundingRoute = runtimeState.initialFundingRequestsApprovalPage.selectRandomFundingRoute();
        String approvalNotes = "Approval notes for Initial Funding Requests Automation testing  : " +  DateHelper.dateAsString(new Date());

        String amount = runtimeState.initialFundingRequestsApprovalPage.getAmount();
        if (amount.isEmpty()) {
            assertFalse("Amount is not populated even when contractor has pricebook entry! ", dbHelperResources.contractorHasPricebookEntry(testData.getInt("resourceId")));
            int amountValue = DataGenerator.randBetween(100, 500);
            runtimeState.initialFundingRequestsApprovalPage.enterAmount(String.valueOf(amountValue));
        }
        if (dbHelperSystemToggles.getSystemFeatureToggle("Budget Review") == 1) {
            runtimeState.initialFundingRequestsApprovalPage.selectRandomAnswerForIsPotentialInsuranceQuote();
        }

        runtimeState.initialFundingRequestsApprovalPage.addApprovalNotes(approvalNotes);

        runtimeState.scenario.write("Selected funding route : " + fundingRoute);
        runtimeState.scenario.write("Added approval notes :  " + approvalNotes);
        testData.addStringTag("fundingRoute", fundingRoute);
        testData.addStringTag("approvalNotes", approvalNotes);
    }


    /**
     * Complete the reject funding request common elements
     * @param rejectionReason
     */
    private void rejectFundingRequestCommon(String rejectionReason) {
        runtimeState.initialFundingRequestsApprovalPage.reject();
        runtimeState.initialFundingRequestsApprovalPage.selectExactRejectionReason(rejectionReason);
        String rejectionNotes = "Rejection notes for Initial Funding Requests Automation testing  : " +  DateHelper.dateAsString(new Date());

        runtimeState.initialFundingRequestsApprovalPage.addRejectionNotes(rejectionNotes);

        runtimeState.scenario.write("Selected rejection reason : " + rejectionReason);
        runtimeState.scenario.write("Added rejection notes :  " + rejectionNotes);
        testData.addStringTag("rejectionReason", rejectionReason);
        testData.addStringTag("rejectionNotes", rejectionNotes);
    }

    @When("^the funding request is rejected with a random reason$")
    public void the_funding_request_is_rejected_with_a_random_reason(DataTable rejectionReasons) throws Exception {
        List<String> reasons = rejectionReasons.asList(String.class);
        Random rand = new Random();
        String rejectionReason = reasons.get(rand.nextInt(reasons.size()));

        rejectFundingRequestCommon(rejectionReason);
    }

    @When("^the funding request is rejected with \"([^\"]*)\"$")
    public void the_funding_request_is_rejected_with_a_random_reason(String rejectionReason) throws Exception {
        String jobType = jobViewDao.getByJobReference(testData.getInt("jobReference")).getJobTypeName();
        testData.put("jobType", jobType);
        rejectFundingRequestCommon(rejectionReason);

        outputHelper.takeScreenshots();
        runtimeState.initialFundingRequestsApprovalPage.submit();
    }

    @When("^an alternative resource is selected$")
    public void an_alternative_resource_is_selected() throws Exception {
        String alternativeResourceType = runtimeState.initialFundingRequestsApprovalPage.selectRandomAlternativeResourceType();

        runtimeState.initialFundingRequestsApprovalPage.showAllResources();
        String alternativeResource = runtimeState.initialFundingRequestsApprovalPage.selectAlternativeResource();

        runtimeState.scenario.write("Alternative Resource type :  " + alternativeResourceType);
        testData.addStringTag("alternativeResourceType", alternativeResourceType);
        runtimeState.scenario.write("Alternative Resource :  " + alternativeResource);
        testData.addStringTag("alternativeResource", alternativeResource);
    }


    @When("^a \"([^\"]*)\" is selected as an alternative resource$")
    public void a_specific_resource_is_selected_as_an_alternative_resource(String resource) throws Exception {
        String alternativeResource = null;
        Integer resourceId = null;
        if ("City Technician".equalsIgnoreCase(resource)) {
            runtimeState.initialFundingRequestsApprovalPage.selectExactAlternativeResourceType(resource);
            runtimeState.initialFundingRequestsApprovalPage.showAllResources();
            resourceId = dbHelperResources.getRandomCityResource("mobile", "with", "with");
            if (resourceId == null) {
                resourceId = apiHelperResources.configureRandomActiveResource("City Resource", "mobile", "with", "with");
            }

            alternativeResource = dbHelperResources.getResourceName(resourceId);
            // ToDo Once MCP code is deployed to Walmart, modify sql query to use COALESCE(Alias, Name) such that only one db call would suffice to get the correct value.
            String alternativeResourceProfile = dbHelperResources.getResourceProfileName(resourceId);
            // RDATA-40 ticket is deployed to Rainbow
            alternativeResourceProfile = dbHelperResources.getAliasForResourceProfile(alternativeResourceProfile);
            String alternativeResourceName = alternativeResource + " (" + alternativeResourceProfile + ")";
            runtimeState.initialFundingRequestsApprovalPage.selectExactAlternativeResource(alternativeResourceName);
        } else {
            runtimeState.initialFundingRequestsApprovalPage.selectExactAlternativeResourceType("Contractor");
            runtimeState.initialFundingRequestsApprovalPage.showAllResources();

            if ("Contractor who is non-chargeable".equalsIgnoreCase(resource)) {
                resourceId = dbHelperResources.getChargeableContractor(false, null);
                if (resourceId == null) {
                    resourceId = dbHelperResources.getContractorWithConfiguredRate();
                    dbHelperResources.updateAlwaysChargeableValueOfResource(false, resourceId);
                }
            } else if ("Contractor who is always chargeable and without zero price in the pricebook".equalsIgnoreCase(resource)) {
                resourceId = dbHelperResources.getChargeableContractor(true, "without");
            } else if ("Contractor who is always chargeable and with zero price in the pricebook".equalsIgnoreCase(resource)) {
                resourceId = dbHelperResources.getChargeableContractor(true, "with");
                if (resourceId == null) {
                    resourceId = dbHelperResources.getChargeableContractor(false, "with");
                    dbHelperResources.updateAlwaysChargeableValueOfResource(true, resourceId);
                }
            }
            alternativeResource = dbHelperResources.getResourceName(resourceId);
            runtimeState.initialFundingRequestsApprovalPage.selectExactAlternativeResource(alternativeResource);

            String resourceType = dbHelperResources.getResourceType(resourceId);
            testData.put("reallocatedResourceType", resourceType);

            testData.put("reallocatedResourceId", resourceId);
            testData.put("reallocatedResourceName", alternativeResource);
        }
        runtimeState.scenario.write("Alternative Resource type : " + resource);
        testData.addStringTag("alternativeResourceType", resource);
        runtimeState.scenario.write("Alternative Resource :  " + alternativeResource);
        testData.addStringTag("alternativeResource", alternativeResource);
    }
}
