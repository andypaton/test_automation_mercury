package mercury.steps.portal.quotes;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.When;
import mercury.database.models.QuoteApprovalScenarios;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.gridV3.Grid;
import mercury.pageobject.web.portal.quotes.FundingRouteModal;
import mercury.runtime.RuntimeState;

public class PortalQuotesApprovalSteps {


    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private QuoteApprovalScenarios quoteApprovalScenarios;

    List<String> resources = new ArrayList<>(); // Used to store Resources Invited to Quote;


    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void storeQuoteResources() {
        // Now grab the resources to assert on
        List<Map<String, Object>> quoteResources = dbHelperQuotes.getQuoteResources(testData.getInt("jobReference"), null);
        Iterator it = quoteResources.iterator();

        for (int i = 1; i <= quoteResources.size()  ; i++) {
            Map<String, Object> map = (Map<String, Object>) it.next();
            resources.add(map.get("ResourceName").toString());
        }
        this.testData.putArray("resources", resources);
        testData.putListMap("resources", quoteResources);

    }

    /**
     * If the quote rejection reason is alternative funding route then this must be
     * selected.
     */
    private void postProcessingRejectionReason() {
        String rejectionReason = runtimeState.quotesManagersDecisionPage.getRejectReasonValue();
        if ("Alternative Funding Route".equalsIgnoreCase(rejectionReason)) {
            runtimeState.fundingRouteModal = new FundingRouteModal(getWebDriver()).get();
            String altFundingRoute = runtimeState.fundingRouteModal.selectRandomAltFundingRoute();
            runtimeState.fundingRouteModal.save();
            runtimeState.scenario.write("Quote rejection Alternative Funding Route :" + altFundingRoute);
        }
    }

    /**
     * Reject the currently selected quote with a random reason and note
     *
     * @throws Throwable
     */
    private void rejectQuoteApproval() throws Throwable {
        runtimeState.quotesManagersDecisionPage.rejectQuote();
        outputHelper.takeScreenshots();

        runtimeState.quotesManagersDecisionPage.selectRejectReason();
        // if alternative funding route selected now need to select that
        postProcessingRejectionReason();
        outputHelper.takeScreenshots();
        runtimeState.scenario.write("selected reason" + runtimeState.quotesManagersDecisionPage.getRejectReasonValue() + " : " + runtimeState.quotesManagersDecisionPage.getRejectReasonText());

        String rejectApproveNotes = "Notes for Quote rejected, entered on  " + DateHelper.dateAsString(new Date());
        runtimeState.quotesManagersDecisionPage.setRejectApproveNotes(rejectApproveNotes);
        runtimeState.scenario.write("Quote rejection notes :" + rejectApproveNotes);
    }

    /**
     * Accept or recommend the current quote based on the funding route
     * @throws Throwable
     */
    private void accpetQuoteApproval() throws Throwable {
        runtimeState.quotesManagersDecisionPage.recommendQuote();
        outputHelper.takeScreenshots();
        // Only CAPEX, BMI requires notes -- if they update the ID then this test is screwed
        boolean result = dbHelperQuotes.isApproverFinal(quoteApprovalScenarios.getFaultId(), quoteApprovalScenarios.getFundingRouteID(), testData.getInt("userProfileId"));

        if (!result) {
            String rejectApproveNotes = "Notes for Quote accepted, entered on  " + DateHelper.dateAsString(new Date());
            runtimeState.quotesManagersDecisionPage.setRejectApproveNotes(rejectApproveNotes);
            runtimeState.scenario.write("Quote approval notes : " + rejectApproveNotes);
        }
    }


    /**
     * Queries the current quote
     * @throws Throwable
     */
    private void queryQuoteApproval() throws Throwable {
        runtimeState.submitQuoteQueryModalPage = runtimeState.quotesManagersDecisionPage.queryQuote();
        runtimeState.submitQuoteQueryModalPage.selectRandomQueryReason();
        runtimeState.submitQuoteQueryModalPage.setQueryNotes(testData.getString("quoteQueryNotes"));
        outputHelper.takeScreenshots();
        runtimeState.submitQuoteQueryModalPage.saveQuery();
    }


    private void acceptFundingRequest() throws Throwable {
        runtimeState.fundingRequestsAwaitingApprovalPage.clickApproveRadioButton();
        outputHelper.takeScreenshots();
    }

    private void rejectFundingRequest() throws Throwable {
        runtimeState.fundingRequestsAwaitingApprovalPage.clickRejectRadioButton();

        String rejectReason = runtimeState.fundingRequestsAwaitingApprovalPage.selectRejectReason();
        runtimeState.scenario.write("selected reason: " + rejectReason);

        String rejectNotes = "Notes for Quote rejected, entered on  " + DateHelper.dateAsString(new Date());
        runtimeState.fundingRequestsAwaitingApprovalPage.enterRejectionNotes(rejectNotes);
    }

    private void recommendFundingRequest() throws Throwable {
        runtimeState.fundingRequestsAwaitingApprovalPage.clickRecommendRadioButton();
        String notes = "Test recommendation notes entered on " + DateHelper.dateAsString(new Date());
        runtimeState.fundingRequestsAwaitingApprovalPage.enterRecommendationNotesToSeniorManager(notes);
    }

    @When("^the \"([^\"]*)\" is \"([^\"]*)\"$")
    public void the_job_type_is_actioned(String jobType, String action) throws Throwable {
        switch(jobType) {
        case "Quote Approval":

            if ("Rejected".equalsIgnoreCase(action)) {
                rejectQuoteApproval();

                runtimeState.submitQuoteJobRejectionModalPage = runtimeState.quotesManagersDecisionPage.submitReject();
                outputHelper.takeScreenshots();
            }
            if ("Accepted".equalsIgnoreCase(action)) {
                accpetQuoteApproval();
                outputHelper.takeScreenshots();
            }
            if ("Queried".equalsIgnoreCase(action)) {
                String queryNotes = "Notes for Quote query, entered on  " + DateHelper.dateAsString(new Date());
                testData.addStringTag("quoteQueryNotes" , queryNotes);
                queryQuoteApproval();
                outputHelper.takeScreenshots();
                runtimeState.scenario.write(runtimeState.quotesManagersDecisionPage.getPageAlert());
            }
            // Now grab the resources to assert on
            storeQuoteResources();
            break;
        case "Funding Request":
            if ("rejected".equalsIgnoreCase(action)) {
                rejectFundingRequest();
                String rejectApproveNotes = "Notes for Quote rejected, entered on  " + DateHelper.dateAsString(new Date());
                runtimeState.submitFundingRequestModalPage = runtimeState.fundingRequestsAwaitingApprovalPage.clickSubmitButton();
                outputHelper.takeScreenshots();
                runtimeState.submitFundingRequestModalPage.setInternalNotes(rejectApproveNotes);
                outputHelper.takeScreenshots();
                runtimeState.submitFundingRequestModalPage.save();
                outputHelper.takeScreenshots();
            }
            if ("Accepted".equalsIgnoreCase(action)) {
                acceptFundingRequest();
            }
            break;
        default :
            throw new Exception("Cannot find "  + jobType);

        }
    }

    @When("^the Rejection action \"([^\"]*)\" is selected$")
    public void the_rejection_action_is_selected(String action) throws Throwable {
        runtimeState.submitQuoteJobRejectionModalPage.selectRejectionAction(action);
        outputHelper.takeScreenshots();
        //        runtimeState.submitQuoteJobRejectionModalPage.saveRejection();
        //        outputHelper.takeScreenshotIfConfigured();
    }

    @When("^a random Resource is selected to Quote$")
    public void a_random_Resource_is_selected_to_Quote() throws Throwable {
        runtimeState.submitQuoteJobRejectionModalPage.selectRandomResourceType();
        runtimeState.submitQuoteJobRejectionModalPage.showAllResources();
        runtimeState.submitQuoteJobRejectionModalPage.selectRandomResource();

        // Save the resource for assertion later.
        String resourceName = runtimeState.submitQuoteJobRejectionModalPage.getSelectedResource();

        Map<String, Object> newResource = new HashMap<>();
        newResource.put("ApprovalStatusName", "ItqAwaitingAcceptance");
        newResource.put("ResourceName", resourceName);
        this.testData.addMap("resources", newResource);

    }

    @When("^the quote rejection is saved$")
    public void the_quote_rejection_is_saved() throws Throwable {
        runtimeState.submitQuoteJobRejectionModalPage.saveRejection();
        List<Map<String, Object>> quoteResources = dbHelperQuotes.getQuoteResources(testData.getInt("jobReference"), null);
        testData.addAllMap("resources", quoteResources);

        outputHelper.takeScreenshots();
    }

    @When("^the \"([^\"]*)\" notes are added$")
    public void the_notes_are_added(String jobType) throws Throwable {
        switch (jobType)  {
        case "Quote Query" :
            String quoteQueryResponseNotes = "Notes for Quote Query response, entered on  " + DateHelper.dateAsString(new Date());
            runtimeState.quoteQueryPage.setQuoteQueryResponse(quoteQueryResponseNotes);
            runtimeState.scenario.write(quoteQueryResponseNotes);
            break;
        case "was this ever called" :
            String rejectApproveNotes = "Notes for Quote recommendation, entered on  " + DateHelper.dateAsString(new Date());
            runtimeState.quotesManagersDecisionPage.setRejectApproveNotes(rejectApproveNotes);
            runtimeState.scenario.write(rejectApproveNotes);
            break;
        default :
            throw new Exception("Cannot find "  + jobType);

        }
    }

    @When("^the \"([^\"]*)\" is submitted$")
    public void the_is_submitted(String jobType) throws Throwable {
        switch(jobType) {
        case "Quote Approval" :
            runtimeState.submitQuoteJobRecommendModalPage = runtimeState.quotesManagersDecisionPage.submitRecommend();
            break;
        case "Funding Request" :
            runtimeState.submitFundingRequestModalPage = runtimeState.fundingRequestsAwaitingApprovalPage.clickSubmitButton();
            break;
        case "Quote Query" :
            runtimeState.quotesWithQueryPendingPage = runtimeState.quoteQueryPage.submitResponse();
            break;
        default :
            throw new Exception("Cannot find "  + jobType);
        }
        outputHelper.takeScreenshots();
    }

    @When("^the \"([^\"]*)\" RFM Approval notes are added$")
    public void the_rfm_approval_notes_are_added(String jobType) throws Throwable {
        String rejectApproveNotes = "Notes for Quote recommendation, entered on  " + DateHelper.dateAsString(new Date());
        testData.addStringTag("RFMApprovalNotes", rejectApproveNotes);
        runtimeState.quotesManagersDecisionPage.setRejectApproveNotes(rejectApproveNotes);
        runtimeState.scenario.write(rejectApproveNotes);
    }

    @When("^the \"([^\"]*)\" Senior Manager notes are added$")
    public void the_senior_manager_notes_are_added(String jobType) throws Throwable {
        boolean result = dbHelperQuotes.isApproverFinal(quoteApprovalScenarios.getFaultId(), quoteApprovalScenarios.getFundingRouteID(), testData.getInt("userProfileId"));
        if (!result) {
            String rejectApproveNotes = "Notes for Quote recommendation, entered on  " + DateHelper.dateAsString(new Date());
            testData.addStringTag("SeniorManagerNotes", rejectApproveNotes);
            runtimeState.submitQuoteJobRecommendModalPage.setInternalNotes(rejectApproveNotes);
        }
        outputHelper.takeScreenshots();
    }

    @When("^the quote budget route is updated$")
    public void the_quote_budget_route_is_updated(String budget) throws Throwable {
        runtimeState.quotesManagersDecisionPage = runtimeState.quotesManagersDecisionPage.get();
        runtimeState.quotesManagersDecisionPage.selectBudgetRoute(budget);
        runtimeState.quotesManagersDecisionPage.clickConfirmationAlert();
        outputHelper.takeScreenshots();
    }

    @When("^all \"([^\"]*)\" are \"([^\"]*)\"$")
    public void all_are(String jobType, String action)  throws Throwable {
        Grid grid;

        switch (jobType) {
        case "Quote Approval" :
            grid = runtimeState.quotesManagersDecisionPage.getGrid();
            assertNotNull("Unexpected Null Grid", grid);
            Integer numberOfQuotes;
            numberOfQuotes = runtimeState.quotesManagersDecisionPage.getNumberOfAwaitingQuotesRows();
            if ("Rejected".equalsIgnoreCase(action)) {
                for ( int i=1; i <= numberOfQuotes; i++ ) {
                    runtimeState.quotesManagersDecisionPage.selectQuote(i);
                    // When the "Quote Approval" is "Rejected"
                    outputHelper.takeScreenshots();
                    rejectQuoteApproval();
                    outputHelper.takeScreenshots();
                }
                runtimeState.submitQuoteJobRejectionModalPage = runtimeState.quotesManagersDecisionPage.submitReject();
                outputHelper.takeScreenshots();

            }

            if ("Queried".equalsIgnoreCase(action)) {
                String queryNotes = "Notes for Quote query, entered on  " + DateHelper.dateAsString(new Date());
                testData.addStringTag("quoteQueryNotes" , queryNotes);
                for ( int i=1; i <= numberOfQuotes; i++ ) {
                    runtimeState.quotesManagersDecisionPage.selectQuote(i);
                    // When the "Quote Approval" is "Queried"
                    outputHelper.takeScreenshots();
                    queryQuoteApproval();
                    outputHelper.takeScreenshots();
                }
                outputHelper.takeScreenshots();
            }

            if ("Accepted".equalsIgnoreCase(action)) {

                for ( int i=1; i <= numberOfQuotes; i++ ) {
                    runtimeState.quotesManagersDecisionPage.selectQuote(i);
                    // When the "Quote Approval" is "Accepted"
                    outputHelper.takeScreenshots();
                    accpetQuoteApproval();
                    outputHelper.takeScreenshots();
                }
                outputHelper.takeScreenshots();
            }
            // Now grab the resources to assert on
            storeQuoteResources();
            break;
        case "Resource Quotes":
            grid = runtimeState.fundingRequestsAwaitingApprovalPage.getResourceQuotesGrid();
            numberOfQuotes = runtimeState.fundingRequestsAwaitingApprovalPage.getResourceQuotesGridRows();
            for ( int i=1; i <= numberOfQuotes; i++ ) {
                runtimeState.fundingRequestsAwaitingApprovalPage.selectResourceQuote(i);
                if ("Accepted".equalsIgnoreCase(action)) {
                    // When the "Funding Request" is "Accepted"
                    acceptFundingRequest();
                    outputHelper.takeScreenshots();
                } else if ("Rejected".equalsIgnoreCase(action)) {
                    // When the "Funding Request" is "Rejected"
                    rejectFundingRequest();
                    outputHelper.takeScreenshots();
                } else if ("Recommended".equalsIgnoreCase(action)) {
                    // When the "Funding Request" is "Recommended"
                    recommendFundingRequest();
                    outputHelper.takeScreenshots();
                }
            }
            outputHelper.takeScreenshots();
            runtimeState.submitFundingRequestModalPage = runtimeState.fundingRequestsAwaitingApprovalPage.clickSubmitButton();
            // Now grab the resources to assert on
            storeQuoteResources();
            break;
        default :
            throw new Exception("Cannot find expected job type : "  + jobType);
        }
    }
}

