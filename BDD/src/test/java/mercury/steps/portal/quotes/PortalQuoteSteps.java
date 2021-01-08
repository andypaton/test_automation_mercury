package mercury.steps.portal.quotes;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MEDIUM_DATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.DataTable;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.JobViewDao;
import mercury.database.dao.PartCodeDao;
import mercury.database.dao.ProjectQuoteSummaryDao;
import mercury.database.dao.QuoteDao;
import mercury.database.dao.QuoteLineDao;
import mercury.database.dao.QuotePriorityDao;
import mercury.database.dao.ResourceDao;
import mercury.database.models.JobView;
import mercury.database.models.PartCode;
import mercury.database.models.Quote;
import mercury.database.models.QuoteApprovalScenarios;
import mercury.database.models.QuotePriority;
import mercury.database.models.Resource;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.PartBuilder;
import mercury.databuilders.TestData;
import mercury.helpers.AddLineItemForm;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.StepHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.asserter.dbassertions.AssertDatabaseCommon;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperAssertions;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.portal.PortalPopupPage;
import mercury.pageobject.web.portal.multiquote.MultiQuoteAwaitingQuotePage;
import mercury.pageobject.web.portal.quotes.QuoteJobDetailsPage;
import mercury.runtime.RuntimeState;
import mercury.steps.portal.PortalCommon;


public class PortalQuoteSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private QuoteDao quoteDao;
    @Autowired private ResourceDao resourceDao;
    @Autowired private ProjectQuoteSummaryDao projectQuoteSummaryDao;
    @Autowired private QuoteLineDao quoteLineDao;
    @Autowired private QuotePriorityDao quotePriorityDao;
    @Autowired private PartCodeDao partCodeDao;
    @Autowired private PortalCommon portalCommon;
    @Autowired private AssertionFactory assertionFactory;
    @Autowired private TestData testData;
    @Autowired private StepHelper stepHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private DbHelperAssertions dbHelperAssertions;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private QuoteApprovalScenarios quoteApprovalScenarios;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;

    JobView currentJob;
    Quote currentQuote;


    PartBuilder partBuilder; //TODO change to autowired
    List<String> resources = new ArrayList<>(); // Used to store Resources Invited to Quote;
    List<AddLineItemForm> quoteLineItems = new ArrayList<>(); // used to store the quoteline items


    @When("^\"([^\"]*)\" is selected from the resource picker$")
    public void contractor_is_selected_from_the_resource_picker(String resourceType) throws Exception {
        if (getWebDriver().getCurrentUrl().contains("Portal/QuoteJobEdit/Edit")) {
            //Approve Quote Request Form
            runtimeState.quoteJobEdit.selectResourceType(1, resourceType);
        } else if (getWebDriver().getCurrentUrl().contains("Portal/JobsAwaitingQuoteRequestApproval")) {
            runtimeState.rejectQuoteRequest.selectResourceType(resourceType);
        }
    }

    @When("^show all \"([^\"]*)\" is selected$")
    public void show_all_resources_is_selected(String resourceType) throws Exception {
        switch (resourceType) {
        case "Contractor":
            runtimeState.quoteJobEdit.showAllContractors(1);
            break;
        case "Technician":
            runtimeState.quoteJobEdit.showAllTechnicians(1);
            break;
        default :
            throw new Exception("Cannot find Resource Type" + resourceType);
        }
    }

    //And the Rejection Reason "Fund as reactive" is selected
    @When("^the Rejection Reason \"([^\"]*)\" is selected$")
    public void the_rejection_reason_is_selected(String option) throws Exception {
        runtimeState.rejectQuoteRequest.selectRejectReason(option);
    }


    //Use alternative contractor is selected
    @When("^Use alternative contractor is selected$")
    public void use_alternative_contractor_is_selectefd() throws Exception {
        runtimeState.rejectQuoteRequest.showAllContractors();
    }

    @When("^a resource is selected$")
    public void a_resource_is_selected() throws Exception {

        runtimeState.rejectQuoteRequest.selectRandomResource();
        runtimeState.scenario.write(runtimeState.rejectQuoteRequest.getResourceValue() + ":" + runtimeState.rejectQuoteRequest.getResourceValueText());
    }

    @When("^the additional comments are entered$")
    public void the_portal_reject_job_the_additional_comments_are_entered() throws Exception {
        String additionalComments = "Additional comments reject quote request job " + DateHelper.dateAsString(new Date());
        runtimeState.rejectQuoteRequest.setAdditionalComments(additionalComments);
        runtimeState.scenario.write(additionalComments);
        outputHelper.takeScreenshots();
    }

    @When("^the Quote Request is rejected$")
    public void the_portal_reject_quote_request() throws Exception {
        outputHelper.takeScreenshots();
        runtimeState.rejectQuoteRequest.submitForm();
        outputHelper.takeScreenshots();
    }


    @ContinueNextStepsOnException
    @Then("^all \"([^\"]*)\" will be available$")
    public void all_resources_will_be_available(String resourceType) throws Exception {
        List<Resource> allDBResources = new ArrayList<>();
        List<String> allResources = null;

        allDBResources = resourceDao.getByResourceType(resourceType);
        runtimeState.scenario.write("Total count of Database resources " + allDBResources.size());

        if (getWebDriver().getCurrentUrl().contains("Portal/QuoteJobEdit/Edit")) {
            // Approve Quote Request form
            allResources = runtimeState.quoteJobEdit.getAllResources(1);

            assertNotNull("Unexpected empty list of resources ", allResources);
            runtimeState.scenario.write("Total count of Web resources " + allResources.size());

        } else if (getWebDriver().getCurrentUrl().contains("Portal/JobsAwaitingQuoteRequestApproval")) {
            // Reject Quote Request form
            allResources = runtimeState.rejectQuoteRequest.getAllResources();

            assertNotNull("Unexpected empty list of resources ", allResources);
            runtimeState.scenario.write("Total count of Web resources " + allResources.size());
        }

        assertEquals("Unexpected number of resources ", allResources.size(), allDBResources.size());

        for (Resource resource : allDBResources) {
            assertTrue("Unexpected Resource " + resource.getName(), allResources.toString().contains(normalize(resource.getName())));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Job Details on the Jobs Awaiting Quote Request Approval form displays correctly$")
    public void the_jobs_awaiting_quote_request_approval_form_displays_correctly() throws Exception {
        currentJob = jobViewDao.getByJobReference(testData.getInt("jobReference"));

        runtimeState.quoteJobDetailsPage = new QuoteJobDetailsPage(getWebDriver()).get();
        runtimeState.scenario.write(runtimeState.quoteJobDetailsPage.getJobRef());

        assertEquals("Unexpected Job Reference", currentJob.getJobReference(), runtimeState.quoteJobDetailsPage.getJobRef());
        assertEquals("Unexpected Site", currentJob.getName(), runtimeState.quoteJobDetailsPage.getSite());

        // Location is not checked only sub location so commenting this check out. If
        // the UI changes again re-enable
        // assertTrue("Unexpected Location",
        // runtimeState.quoteJobDetailsPage.getLocation().toLowerCase().contains(currentJob.getLocationName().toLowerCase()));
        assertTrue("Unexpected Location", runtimeState.quoteJobDetailsPage.getLocation().toLowerCase().contains(currentJob.getSubLocationName().toLowerCase()));

        String classification = currentJob.getAssetName() == null ? "" : currentJob.getAssetName();
        classification = classification.concat(currentJob.getAssetSubTypeName().length()  == 0 ? "" : (" > " + currentJob.getAssetSubTypeName()));
        classification = classification.concat(currentJob.getAssetClassificationName().length()  == 0 ? "" : (" > " + currentJob.getAssetClassificationName()));
        assertEquals("Unexpected Subtype and or classification", classification, runtimeState.quoteJobDetailsPage.getSubType());

        String loggedDate = DateHelper.dateAsString(currentJob.getCreatedOn(), MEDIUM_DATE);
        assertTrue("Unexpected Logged Date", runtimeState.quoteJobDetailsPage.getLogged().contains(loggedDate));

        // Verify that the job details displayed are read only
        assertTrue("The Job details are not view only", runtimeState.quoteJobDetailsPage.isJobDetailViewOnly());
    }

    @ContinueNextStepsOnException
    @Then("^the Approve Quote Request Form displays correctly$")
    public void the_approve_quote_request_form_displays_correctly() throws Exception {
        currentQuote = quoteDao.getByJobReference(testData.getInt("jobReference"));
        currentJob = jobViewDao.getByJobReference(testData.getInt("jobReference"));

        assertNotNull("Unexpected null record set for Quote", currentQuote);


        assertEquals("Unexpected Scope of Works", currentJob.getDescription(), runtimeState.quoteJobEdit.getScopeOfWorks());

        // Verify the correct quote type is highlighted
        assertEquals("Unexpected Quote Type ", currentQuote.getFundingRouteId(), Integer.valueOf(runtimeState.quoteJobEdit.getFundintRoute()));
        assertEquals("Unexpected Quote Priority", currentQuote.getQuotePriorityId(), Integer.valueOf(runtimeState.quoteJobEdit.getQuotePriorityValue()));

        /*
         * Need to output only the following if fully debugging
		runtimeState.scenario.write("getCapexValue " + runtimeState.approveQuoteRequest.getCapexValue());
		runtimeState.scenario.write("getNumberOfQuotesText " + runtimeState.approveQuoteRequest.getNumberOfQuotesText());
		runtimeState.scenario.write("getOpexValue " + runtimeState.approveQuoteRequest.getOpexValue());
		runtimeState.scenario.write("getQuotePriorityText " + runtimeState.approveQuoteRequest.getQuotePriorityText());
		runtimeState.scenario.write("getQuotePriorityValue " + runtimeState.approveQuoteRequest.getQuotePriorityValue());
		runtimeState.scenario.write("getScopeOfWorksgetScopeOfWorks " + runtimeState.approveQuoteRequest.getScopeOfWorks());
		runtimeState.scenario.write("getOpexChecked " + String.valueOf(runtimeState.approveQuoteRequest.getOpexChecked()));
		runtimeState.scenario.write("getCapexChecked " + String.valueOf(runtimeState.approveQuoteRequest.getCapexChecked()));
         */
    }


    @When("^the funding route is selected$")
    public void the_funding_route_is_selected() {
        runtimeState.initialFundingRequestsApprovalPage.setRandomQuoteFundingRoute();
    }

    @When("^the potential insurance question is answered$")
    public void the_potential_insurance_question_is_answered() throws Throwable {
        if (dbHelperSystemToggles.getSystemFeatureToggle("Budget Review") == 1) {
            if (getWebDriver().getCurrentUrl().contains("JobsAwaitingInitialFundingRequest")) {
                runtimeState.initialFundingRequestsApprovalPage.selectRandomAnswerForIsPotentialInsuranceQuote();
            } else {
                runtimeState.fundingRequestsAwaitingApprovalPage.selectRandomAnswerForIsPotentialInsuranceQuote();
            }
        }
    }

    @When("^the quote scope of works is entered$")
    public void the_quote_scope_of_works_is_entered() {
        String scopeOfWorks = " Scope of works  " + DateHelper.dateAsString(new Date());
        if (getWebDriver().getCurrentUrl().contains("Portal/JobsAwaitingInitialFundingRequest")) {
            runtimeState.initialFundingRequestsApprovalPage.setScopeOFWorks(scopeOfWorks);
        } else {
            runtimeState.quoteJobEdit.setScopeOFWorks(scopeOfWorks);
        }
    }

    /**
     * Selects a random quote priority if and only if none was selected.
     * @throws Exception
     */
    @When("^the quote priority is selected$")
    public void the_quote_priority_is_selected() throws Exception {
        if (getWebDriver().getCurrentUrl().contains("Portal/JobsAwaitingInitialFundingRequest")) {
            runtimeState.initialFundingRequestsApprovalPage.selectRandomQuotePriortyByText();
        } else {
            String quotePriority = runtimeState.quoteJobEdit.getQuotePriorityText();
            if (quotePriority.isEmpty()) {
                runtimeState.quoteJobEdit.selectRandomQuotePriortyByText();
            }
        }
    }

    /**
     * Selects a random new quote priority
     * @throws Exception
     */
    @When("^a new quote priority is selected$")
    public void a_new_quote_priority_is_selected() throws Exception {
        String quotePriority = runtimeState.quoteJobEdit.getQuotePriorityText();
        do {
            runtimeState.quoteJobEdit.selectRandomQuotePriortyByText();
        } while (runtimeState.quoteJobEdit.getQuotePriorityText().equalsIgnoreCase(quotePriority));

        testData.addStringTag("newQuotePriority", runtimeState.quoteJobEdit.getQuotePriorityText());
        outputHelper.takeScreenshots();
    }

    @When("^a new quote type is selected$")
    public void a_new_quote_type_is_selected() throws Exception {
        if ( runtimeState.quoteJobEdit.getCapexChecked()) {
            String quoteType = LOCALE.equals("en-US") ? "OPEX" : "REVEX";
            runtimeState.quoteJobEdit.clickOpex(quoteType);
            testData.addIntegerTag("newQuoteType", Integer.parseInt(runtimeState.quoteJobEdit.getOpexValue(quoteType)));
        } else {
            runtimeState.quoteJobEdit.clickCapex();
            testData.addIntegerTag("newQuoteType", Integer.parseInt(runtimeState.quoteJobEdit.getCapexValue()));
        }

        if (runtimeState.quoteJobEdit.isBudgetWarningMessageDisplayed()) {
            runtimeState.quoteJobEdit.confirmBudgetWarningMessage();
        }
        outputHelper.takeScreenshots();
    }

    @When("^a new quote resource is selected$")
    public void a_new_quote_resource_is_selected() throws Exception {
        String quoteResource = runtimeState.quoteJobEdit.getResource(1);
        if (runtimeState.quoteJobEdit.getResourceType(1).equalsIgnoreCase("Contractor")) {
            if (!runtimeState.quoteJobEdit.getAllContactorsChecked(1)) {
                runtimeState.quoteJobEdit.showAllContractors(1);
            }
        } else {
            if (!runtimeState.quoteJobEdit.getAllTechniciansChecked(1)) {
                runtimeState.quoteJobEdit.showAllTechnicians(1);
            }
        }


        do {
            runtimeState.quoteJobEdit.selectRandomResource(String.valueOf(1));
        } while (runtimeState.quoteJobEdit.getResource(1).equalsIgnoreCase(quoteResource));

        testData.addStringTag("newQuoteResource", runtimeState.quoteJobEdit.getResource(1));
        runtimeState.scenario.write("New quote resource : " + testData.getString("newQuoteResource"));
    }

    @When("^the number of quotes required is ((?:increased|decreased))")
    public void the_number_of_quotes_requied_is_modified(String change) throws Exception {
        Integer numberOfQuotesRequired = Integer.valueOf(runtimeState.quoteJobEdit.getNumberOfQuotesText());
        List<Integer> allOptions = runtimeState.quoteJobEdit.getNumberOfQuotesRequiredOptions();
        Integer minIndex = allOptions.indexOf(Collections.min(allOptions));
        Integer maxIndex = allOptions.indexOf(Collections.max(allOptions));
        Integer newNumberOfQuotes = null;

        if ("decreased".equalsIgnoreCase(change)) {
            newNumberOfQuotes = ((minIndex != numberOfQuotesRequired ) ?   numberOfQuotesRequired-- :  numberOfQuotesRequired);
        } else if ("increased".equalsIgnoreCase(change)) {
            newNumberOfQuotes = ((maxIndex != numberOfQuotesRequired ) ?   numberOfQuotesRequired++ :  numberOfQuotesRequired);
        } else {
            newNumberOfQuotes = numberOfQuotesRequired;
        }
        runtimeState.quoteJobEdit.selectNumberOfQuotesByText(newNumberOfQuotes.toString());
    }

    @When("^the number of quotes required is set to Funding Route minimum")
    public void the_number_of_quotes_required_is_set_to_funding_route_minumum() throws Exception {
        if (getWebDriver().getCurrentUrl().contains("Portal/JobsAwaitingInitialFundingRequest")) {
            runtimeState.initialFundingRequestsApprovalPage.selectMinumumNumberOfQuotes();
        } else {
            Integer fundingRouteid = Integer.valueOf(runtimeState.quoteJobEdit.getFundintRoute());
            Integer quotePriorityId = Integer.valueOf(runtimeState.quoteJobEdit.getQuotePriorityValue());
            QuotePriority quotePriority = quotePriorityDao.getQuotePriorityByFundingRouteId(fundingRouteid, quotePriorityId);

            runtimeState.quoteJobEdit.selectNumberOfQuotesByText(quotePriority.getMinimumQuotesRequired().toString());
        }
    }


    private void populateQuoteEditQuoteResources() throws IOException {
        // Get the number of quotes required and the number of quotes already submitted.
        Integer numberOfQuotesRequired = Integer.valueOf(runtimeState.quoteJobEdit.getNumberOfQuotesText());
        Integer numberOfQuotesSubmitted = dbHelperQuotes.getNumberofQuotesAwaitingApproval(testData.getInt("jobReference"));
        Integer quotesToPopulate = numberOfQuotesRequired - numberOfQuotesSubmitted;

        // Now populate the resources to quote
        for (int i = 1; i <= quotesToPopulate  ; i++) {
            runtimeState.quoteJobEdit.selectRandomResourceType(i);
            if (runtimeState.quoteJobEdit.getNumberOfResources(i) < 2){
                if (runtimeState.quoteJobEdit.getResourceType(i).equalsIgnoreCase("Contractor")) {
                    runtimeState.quoteJobEdit.showAllContractors(i);
                } else {
                    runtimeState.quoteJobEdit.showAllTechnicians(i);
                }
            }
            runtimeState.quoteJobEdit.selectRandomResource(String.valueOf(i));
            runtimeState.scenario.write("Quote Resource " + String.valueOf(i) + " : " + runtimeState.quoteJobEdit.getResource(i));

            String resourceName = runtimeState.quoteJobEdit.getResource(i).trim();

            Map<String, Object> newResource = new HashMap<>();
            newResource.put("ApprovalStatusName", "ItqAwaitingAcceptance");
            newResource.put("ResourceName", resourceName);
            this.testData.addMap("resources", newResource);
        }
    }

    private void populateInitialFundingRouteQuoteResources() throws IOException {
        // Get the number of quotes required and the number of quotes already submitted.
        Integer numberOfQuotesRequired = Integer.valueOf(runtimeState.initialFundingRequestsApprovalPage.getNumberOfQuotesText());
        Integer numberOfQuotesSubmitted = dbHelperQuotes.getNumberofQuotesAwaitingApproval(testData.getInt("jobReference"));
        Integer quotesToPopulate = numberOfQuotesRequired - numberOfQuotesSubmitted;

        // Now populate the resources to quote
        for (int i = 1; i <= quotesToPopulate  ; i++) {
            runtimeState.initialFundingRequestsApprovalPage.selectRandomResourceType(i);
            if (runtimeState.initialFundingRequestsApprovalPage.getNumberOfResources(i) < 2){
                if (runtimeState.initialFundingRequestsApprovalPage.getResourceType(i).equalsIgnoreCase("Contractor")) {
                    runtimeState.initialFundingRequestsApprovalPage.showAllContractors(i);
                } else {
                    runtimeState.initialFundingRequestsApprovalPage.showAllTechnicians(i);
                }
            }
            runtimeState.initialFundingRequestsApprovalPage.selectRandomResource(String.valueOf(i));
            runtimeState.scenario.write("Quote Resource " + String.valueOf(i) + " : " + runtimeState.initialFundingRequestsApprovalPage.getResource(i));

            String resourceName = runtimeState.initialFundingRequestsApprovalPage.getResource(i).trim();

            Map<String, Object> newResource = new HashMap<>();
            newResource.put("ApprovalStatusName", "ItqAwaitingAcceptance");
            newResource.put("ResourceName", resourceName);
            this.testData.addMap("resources", newResource);
        }
    }

    @When("^the quote resources are populated$")
    public void the_quote_resources_are_populated() throws Exception {
        testData.put("jobStatusBeforeSaved", dbHelperJobs.getJobStatus(testData.getString("jobReference")));
        if (getWebDriver().getCurrentUrl().contains("Portal/JobsAwaitingInitialFundingRequest")) {
            populateInitialFundingRouteQuoteResources();
        } else {
            populateQuoteEditQuoteResources();
        }
    }

    @When("^the user \"([^\"]*)\" a Multi-Quote Awaiting Quote$")
    public void the_user_views_or_edits(String viewOrEdit) throws Exception {
        runtimeState.multiQuoteAwaitingQuotePage.searchJobs(testData.getInt("jobReference").toString());
        if(viewOrEdit.equalsIgnoreCase("Edits")) {
            runtimeState.quoteJobEdit = runtimeState.multiQuoteAwaitingQuotePage.editQuoteJobRequest(testData.getInt("jobReference"));
        } else {
            runtimeState.quoteAwaitingResourceQuotePage = runtimeState.multiQuoteAwaitingQuotePage.viewQuoteJobRequest(testData.getInt("jobReference"));
        }
    }

    @When("^the \"([^\"]*)\" form is saved$")
    public void the_form_is_saved(String formName) throws Exception {
        outputHelper.takeScreenshots();
        switch (formName) {
        case "Awaiting Quote Request Approval" :
        case "Job Awaiting Quote" :
        case "Multi-Quote Awaiting Bypass Review" :
        case "Multi-Quote Awaiting Resource" :
        case "Multi-Quote Awaiting Review" :
            runtimeState.quoteJobEdit.submitForm();
            break;
        case "Multi-Quote Awaiting Quote" :
            runtimeState.quoteJobEdit.submitForm();
            runtimeState.multiQuoteAwaitingQuotePage = new MultiQuoteAwaitingQuotePage(getWebDriver()).get();
            break;
        case "Job Awaiting Resource Assignment" :
            runtimeState.quoteJobEdit.submitForm();
            break;
        default :
            throw new Exception("Cannot find form " + formName);
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Quote \"([^\"]*)\" is updated$")
    public void the_quote_field_is_updated(String quoteField) throws Throwable {
        switch(quoteField) {
        case "Quote Priority" :
            dbHelperAssertions.quoteWithQuotePriority(testData.getInt("jobReference"), testData.getString("newQuotePriority"));
            break;
        case "Quote Type" :
            dbHelperAssertions.quoteWithFundingRoute(testData.getInt("jobReference"), testData.getInt("newQuoteType"));
            break;
        case "Quote Resource" :
            dbHelperAssertions.quoteWithResource(testData.getInt("jobReference"), testData.getString("newQuoteResource"));
            break;
        default :
            throw new Exception("Cannot find switch case for  " + quoteField);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the decline reason list is validated$")
    public void the_decline_reason_list_is_validated() throws Throwable {

        if (getWebDriver().getCurrentUrl().contains("DeclineInvitation")) {
            List<Map<String, Object>> allDbRejectionReasons =  new ArrayList<>();
            List<String> allReasons = runtimeState.declineInvitationToQuotePage.getAllRejectionsReasons(1).stream().sorted().collect(Collectors.toList());
            assertNotNull("Unexpected empty list of reasons ", allReasons);

            //remove list instruction text from list
            allReasons.remove(3);
            runtimeState.scenario.write("Total count of Web resources " + allReasons.size());
            allDbRejectionReasons = dbHelperQuotes.getQuotesRejectionReasons();
            runtimeState.scenario.write("Total count of Database resources " + allDbRejectionReasons.size());
            assertEquals("Unexpected number of resources ", allReasons.size(), allDbRejectionReasons.size());
            assertNotNull("Unexpected empty list of reasons", allDbRejectionReasons);

            //sort list alphabetically - not case sensitive
            allReasons.sort(String::compareToIgnoreCase);

            //compare contents of both lists
            for (int i=0; i<allReasons.size(); i++) {
                runtimeState.scenario.write("Asserting decline reason: " + allDbRejectionReasons.get(i).get("RejectionReason"));
                assertEquals("The decline reason" + allDbRejectionReasons.get(i).get("RejectionReason") + " is not displayed correctly ", allDbRejectionReasons.get(i).get("RejectionReason"),allReasons.get(i));
            }

        } else {
            throw new Exception("Unexpected URL");
        }
    }

    @When("^the Reason to decline is selected$")
    public void the_Reason_to_decline_is_selected() throws Throwable {
        runtimeState.declineInvitationToQuotePage.selectRandomReason();
        runtimeState.scenario.write("Decline Reason : " + runtimeState.declineInvitationToQuotePage.getReasonValue() +  ", " + runtimeState.declineInvitationToQuotePage.getReasonText());
        outputHelper.takeScreenshots();
    }

    @When("^the notes for the decline are entered$")
    public void the_notes_for_the_decline_are_entered() throws Throwable {
        String declineDescription = "Please enter a decline reason for use to verify the database update " + DateHelper.dateAsString(new Date());
        runtimeState.declineInvitationToQuotePage.setDescriptiveReason(declineDescription);
        runtimeState.scenario.write(declineDescription);
        outputHelper.takeScreenshots();
    }

    @When("^the \"([^\"]*)\" is saved$")
    public void the_is_saved(String jobType) throws Throwable {
        outputHelper.takeScreenshots();
        switch(jobType) {
        case "Quote Approval" :
            boolean result = dbHelperQuotes.isApproverFinal(quoteApprovalScenarios.getFaultId(), quoteApprovalScenarios.getFundingRouteID(), testData.getInt("userProfileId"));
            if (!result) {
                String rejectApproveNotes = "Notes for Quote recommendation, entered on  " + DateHelper.dateAsString(new Date());
                testData.addStringTag("SeniorManagerNotes", rejectApproveNotes);
                runtimeState.submitQuoteJobRecommendModalPage.setInternalNotes(rejectApproveNotes);
            }
            //Current Page URL is : https://test.mercury.software/Portal/QuotesAwaitingApproval/ManagerDecision?faultId=10023965&caller=QuotesAwaitingApproval
            runtimeState.submitQuoteJobRecommendModalPage.saveRecommendation();
            break;
        case "Funding Request" :
            if (runtimeState.submitFundingRequestModalPage.getPageTitle().equalsIgnoreCase("Submit funding request rejection")) {
                String internalNotes = "Notes for Quote rejected, entered on  " + DateHelper.dateAsString(new Date());
                runtimeState.submitFundingRequestModalPage.setInternalNotes(internalNotes);
            } else if (runtimeState.submitFundingRequestModalPage.getPageTitle().equalsIgnoreCase("Submit quote job recommendation")) {
                String internalNotes = "Notes for Quote recommendation, entered on  " + DateHelper.dateAsString(new Date());
                runtimeState.submitFundingRequestModalPage.setInternalNotes(internalNotes);
            }
            outputHelper.takeScreenshots();
            runtimeState.submitFundingRequestModalPage.save();
            break;
        case "Decline Invitation To Quote" :
            runtimeState.declineInvitationToQuotePage.saveClick();
            break;
        default :
            throw new Exception("Cannot find "  + jobType);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the minimum number of quotes required is \"([^\"]*)\"$")
    public void minimum_number_of_quotes_required(int minimumQuotesRequired) throws Throwable {
        List<Integer> numberOfQuotesRequired = runtimeState.quoteJobEdit.getNumberOfQuotesRequiredOptions();
        int lowestNumberOfQuotesRequired = numberOfQuotesRequired.get(0);
        assertEquals("Minimum number of quotes required isn't 2", minimumQuotesRequired, lowestNumberOfQuotesRequired);
    }

    @ContinueNextStepsOnException
    @Then("^the Quote Awaiting Resource Quote page is displayed$")
    public void the_Quote_Awaiting_Resource_Quote_page_is_displayed() throws Throwable {
        outputHelper.takeScreenshots();
        assertTrue("Job Ref is not displayed", runtimeState.quoteAwaitingResourceQuotePage.isFieldDisplayed("Job Ref"));
        assertTrue("Site is not displayed", runtimeState.quoteAwaitingResourceQuotePage.isFieldDisplayed("Site"));
        assertTrue("Location is not displayed", runtimeState.quoteAwaitingResourceQuotePage.isFieldDisplayed("Location"));
        assertTrue("SubType/Classification is not displayed", runtimeState.quoteAwaitingResourceQuotePage.isFieldDisplayed("SubType/Classification"));
        assertTrue("Date is not displayed", runtimeState.quoteAwaitingResourceQuotePage.isFieldDisplayed("Date"));
        assertTrue("Quote Priority is not displayed", runtimeState.quoteAwaitingResourceQuotePage.isFieldDisplayed("Quote Priority"));
        assertTrue("Scope of works is not displayed", runtimeState.quoteAwaitingResourceQuotePage.isFieldDisplayed("Scope of works"));
    }

    @ContinueNextStepsOnException
    @Then("^the Quote Reference is entered$")
    public void the_Quote_Reference_is_entered() throws Throwable {
        String quoteReference = testData.getInt("jobReference") + "ACME-QR-" + DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0);
        testData.addStringTag("quoteReference", quoteReference);
        runtimeState.scenario.write(quoteReference);
        runtimeState.createQuotePage.setQuoteReference(quoteReference);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Description of works is entered$")
    public void the_Description_of_works_is_entered() throws Throwable {
        String quoteDescription = "Quote description for job " + this.testData.getInt("jobReference") + " : " +  DateHelper.dateAsString(new Date());
        testData.addStringTag("quoteDescription", quoteDescription);
        runtimeState.scenario.write(quoteDescription);
        runtimeState.createQuotePage.setDescriptionOfWorks(quoteDescription);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Description of works is entered with less than \"([^\"]*)\" characters$")
    public void the_Description_of_works_is_entered_with_insufficient_characters(Integer length) throws Throwable {
        Integer descriptionLength = Integer.valueOf(length) - 2 ;
        String quoteDescription = "Quote description for job " + this.testData.getInt("jobReference") + " : " +  DateHelper.dateAsString(new Date());
        quoteDescription = quoteDescription.substring(0, descriptionLength);
        testData.addStringTag("quoteDescription", quoteDescription);
        runtimeState.scenario.write(quoteDescription);
        runtimeState.createQuotePage.setDescriptionOfWorks(quoteDescription);
        outputHelper.takeScreenshots();
    }

    @When("^the work is High Risk with random risks$")
    public void the_work_is_High_Risk_with_random_risks() throws Throwable {
        runtimeState.createQuotePage.setHighRiskWorksYes();
        List<String> risks = dbHelper.getRandomHighRiskWorkTypes();

        for(String risk : risks){
            runtimeState.scenario.write("Adding risk : " + risk);
            runtimeState.createQuotePage.selectHighRisk(risk);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Proposed Working times is entered$")
    public void the_Proposed_Working_times_is_entered() throws Throwable {
        String proposedWorkTime = "Proposed worktime for  " + this.testData.getInt("jobReference") + " : " +  DateHelper.dateAsString(new Date());
        testData.addStringTag("proposedWorkTime", proposedWorkTime);
        runtimeState.scenario.write(proposedWorkTime);
        runtimeState.createQuotePage.setProposedWorkingTimes(proposedWorkTime);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^and the user starts the quote process$")
    public void and_the_user_starts_the_quote_process() throws Throwable {
        runtimeState.registerQuotePage = runtimeState.createQuotePage.startQuote().get();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^and the user clicks Start Quote$")
    public void and_the_user_clicks_start_quote() throws Throwable {
        runtimeState.createQuotePage.clickStartQuote();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Quote summary information has been updated$")
    public void the_the_QuoteHeader_table_has_been_updated() throws Throwable {
        String createdOn = runtimeState.timestamp;
        AssertDatabaseCommon assertDatabaseCommon;
        Map<String, Object> queryMap;

        queryMap = new HashMap<String, Object>();
        if(this.testData.getString("profileName").contains("Contract")) {
            queryMap.put("pq.fld_str_QuoteRef", testData.getString("quoteReference"));
        }
        queryMap.put("qh.fld_str_DescriptionOfWorks",testData.getString("quoteDescription"));
        queryMap.put("qh.fld_str_proposedWorkingTimes", testData.getString("proposedWorkTime"));
        queryMap.put("r.Id", testData.getInt("resourceId"));
        assertDatabaseCommon = new AssertDatabaseCommon(projectQuoteSummaryDao, testData.getInt("jobReference"), queryMap, createdOn);
        assertionFactory.performAssertion(assertDatabaseCommon);
    }


    @ContinueNextStepsOnException
    @Then("^the Quote Line information has been updated$")
    public void the_Quote_Line_information_has_been_updated() throws Throwable {
        // need to loop through quoteLineItems and verify each item is in the database as expected.
        String createdOn = runtimeState.timestamp;

        AssertDatabaseCommon assertDatabaseCommon;
        Map<String, Object> queryMap;
        //		for (int i = 0; i < quoteLineItems.size(); i++) {
        for (AddLineItemForm line : quoteLineItems) {
            queryMap = new HashMap<String, Object>();
            queryMap.put("ql.fld_str_PartCode", localize(line.getType()));
            queryMap.put("ql.fld_str_PartDescription", line.getDescription());
            // queryMap.put("ql.fld_str_PartCodeDetail", quoteLineItems.get(i));
            queryMap.put("fld_cur_UnitPrice", line.getUnitPrice());
            queryMap.put("ql.fld_dec_Quantity", line.getQuantity());
            assertDatabaseCommon = new AssertDatabaseCommon(quoteLineDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Quote line grid is empty$")
    public void the_Quote_line_grid_is_empty() throws Throwable {
        Grid grid = runtimeState.registerQuotePage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);

        String[] expectedHeaders = { "Description", "Type", "Quantity", localize("Unit Price ($)"), localize("Value ($)"), ""};

        for (String header : grid.getHeaders()) {
            assertTrue("Expected " + expectedHeaders.toString() + " to contain " + header, ArrayUtils.contains(expectedHeaders, header.trim()));
        }
        assertTrue("Unexpected number of headers returned, found : " + grid.getHeaders().size() + " expected : " + expectedHeaders.length, expectedHeaders.length == grid.getHeaders().size());
        assertEquals("Unexpected number of rows found", Integer.valueOf(1) , runtimeState.registerQuotePage.getNumberOfDisplayedRows());
        assertEquals("Unexpected row found", "There are no Quote Lines.", grid.getRows().get(0).getCell("Description").getText());
    }

    @ContinueNextStepsOnException
    @Then("^the Quote line grid is updated$")
    public void the_Quote_line_grid_is_updated() throws Throwable {
        Grid grid = runtimeState.registerQuotePage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);

        String[] expectedHeaders = { "Description", "Type", "Quantity", localize("Unit Price ($)"), localize("Value ($)"),  ""};

        for (String header : grid.getHeaders()) {
            assertTrue("Expected " + expectedHeaders.toString() + " to contain " + header, ArrayUtils.contains(expectedHeaders, header.trim()));
        }
        assertTrue("Unexpected number of headers returned, found : " + grid.getHeaders().size() + " expected : " + expectedHeaders.length, expectedHeaders.length == grid.getHeaders().size());
        assertEquals("Unexpected number of rows found", Integer.valueOf(1) , runtimeState.registerQuotePage.getNumberOfDisplayedRows());

        for ( Row row : grid.getRows()) {
            assertEquals("Unexpected Description column data", quoteLineItems.get(0).getDescription().toUpperCase(), row.getCell("Description").getText().toUpperCase());
            assertEquals("Unexpected Type column data", quoteLineItems.get(0).getType().toUpperCase(), row.getCell("Type").getText().toUpperCase());
            assertEquals("Unexpected Quantity column data", String.valueOf(quoteLineItems.get(0).getQuantity()), row.getCell("Quantity").getText());
            assertEquals("Unexpected Unit Price column data", (quoteLineItems.get(0).getUnitPrice() == null) ? "N/A" : String.valueOf(quoteLineItems.get(0).getUnitPrice()), row.getCell(localize("Unit Price ($)")).getText());
            assertEquals("Unexpected Line Value column data", (quoteLineItems.get(0).getLineValue() == null) ? "N/A" : String.valueOf(quoteLineItems.get(0).getLineValue()), row.getCell(localize("Value ($)")).getText());
        }

        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the error message \"([^\"]*)\" for \"([^\"]*)\" will be displayed$")
    public void the_error_message_for_will_be_displayed(String expectedErrorMessage, String errorType) throws Throwable {
        Boolean isErrorDisplayed = null;
        String displayedErrorMEssage;
        switch(errorType) {
        case "Line type":
            isErrorDisplayed = runtimeState.addQuoteLinePage.isLineTypeErrorDisplayed();
            displayedErrorMEssage = runtimeState.addQuoteLinePage.getLineTypeErrorMessage();
            break;
        case "Line description":
            isErrorDisplayed = runtimeState.addQuoteLinePage.isLineDescriptionErrorDisplayed();
            displayedErrorMEssage = runtimeState.addQuoteLinePage.getLineDescriptionErrorMessage();
            break;
        case "Line quantity":
            isErrorDisplayed = runtimeState.addQuoteLinePage.isLineQuantityErrorDisplayed();
            displayedErrorMEssage = runtimeState.addQuoteLinePage.getLineQuantityErrorMessage();
            break;
        case "Line unit price":
            isErrorDisplayed = runtimeState.addQuoteLinePage.isLineUnitPriceErrorDisplayed();
            displayedErrorMEssage = runtimeState.addQuoteLinePage.getLineUnitPriceErrorMessage();
            break;
        case "Part Number":
            isErrorDisplayed = runtimeState.addQuoteLinePage.isPartNumberErrorDisplayed();
            displayedErrorMEssage = runtimeState.addQuoteLinePage.getLinePartNumberErrorMessage();
            break;
        case "Line supplier":
            isErrorDisplayed = runtimeState.addQuoteLinePage.isLineSupplierErrorDisplayed();
            displayedErrorMEssage = runtimeState.addQuoteLinePage.getLineSupplierErrorMessage();
            break;
        default :
            throw new Exception("Cannot find error types " + errorType);
        }
        assertTrue("Unexpected error display", isErrorDisplayed);
        assertEquals("Unexpected error message displayed", expectedErrorMessage, displayedErrorMEssage);
    }


    @ContinueNextStepsOnException
    @Then("^the Quote Request form is displayed$")
    public void the_portal_update_job_quote_request_form_is_displayed() throws Exception {
        assertEquals("Unexpected Page displayed: Missing Quote Request form", runtimeState.updateJobPage.getQuoteFormVisible(), true);
    }

    @ContinueNextStepsOnException
    @Then("^the Quote Request form is populated correctly$")
    public void the_quote_request_form_is_populated_correctly() throws Exception {

    }

    @Given("^the \"([^\"]*)\" is created as a \"([^\"]*)\" profile$")
    public void the_is_created(String itemToCreate, String profile) throws Throwable {
        if(profile.contains("Contractor")) {
            the_Quote_Reference_is_entered();
        }
        the_Description_of_works_is_entered();
        the_Proposed_Working_times_is_entered();
        outputHelper.takeScreenshots();
    }

    @When("^a new line is added to the quote breakdown$")
    public void a_new_line_is_added_to_the_quote_breakdown() throws Throwable {
        runtimeState.addQuoteLinePage = runtimeState.registerQuotePage.addLine();
        outputHelper.takeScreenshots();
    }

    @When("^the line item type \"([^\"]*)\" is entered$")
    public void the_line_item_type_is_entered(String type) throws Throwable {
        type = localize(type);
        runtimeState.addQuoteLinePage.setLineTypeByText(type);
    }

    @When("^the line item part number \"([^\"]*)\" is entered$")
    public void the_line_item_part_number_is_entered(String partNumber) throws Throwable {
        runtimeState.addQuoteLinePage.setPartNumber(partNumber);
    }

    @When("^the line item part code is selected from the list$")
    public void the_line_item_part_code_is_selected() throws Throwable {
        PartCode partCode = partCodeDao.getRandomPartCode();
        partBuilder = new PartBuilder();
        partBuilder.setPartNumber(partCode.getPartCode().trim());
        partBuilder.setPartDescription(partCode.getDescription().trim());
        partBuilder.setManufacturerRef(partCode.getManufacturerRef().trim());
        partBuilder.setNewPart(false);
        partBuilder.setUnitPrice(Double.valueOf(partCode.getUnitPrice().toString()));
        partBuilder.setSupplierCode(partCode.getSupplierCode());
        StringBuilder builder = new StringBuilder();
        builder.append("Requesting Part: ")
        .append(System.lineSeparator())
        .append("Part Code is ").append(partCode.getPartCode()).append(", ")
        .append(System.lineSeparator())
        .append("Part Description is ").append(partCode.getDescription()).append(", ")
        .append(System.lineSeparator())
        .append("Manufacturer Ref is ").append(partCode.getManufacturerRef()).append(", ")
        .append(System.lineSeparator())
        .append("Supplier Code is ").append(partCode.getSupplierCode().trim()).append(", ")
        .append(System.lineSeparator())
        .append("Unit Price is ").append(partCode.getUnitPrice());
        runtimeState.addQuoteLinePage.setPartCode(partCode.getPartCode(), partCode.getDescription());
    }

    @When("^the line item part code is not on the list$")
    public void the_line_item_part_code_is_not_on_the_list() throws Throwable {
        runtimeState.addQuoteLinePage.setPartNotInList();
    }

    @When("^the line item part number is entered$")
    public void the_line_item_part_number_is_entered() throws Throwable {
        partBuilder = new PartBuilder.Builder().build();
        runtimeState.addQuoteLinePage.setPartNumber(partBuilder.getPartNumber());
    }

    @When("^the line item Resource Profile is selected$")
    public void the_line_item_Resource_Profile_is_entered() throws Throwable {
        Map<String, Object> resourceProfileAndLabourType = dbHelperResources.getLabourTypeForResourceProfile();

        String labourType = resourceProfileAndLabourType.get("LabourType").toString();
        String resourceProfile = resourceProfileAndLabourType.get("Alias").toString();

        testData.put("labourType", labourType);
        testData.put("resourceProfile", resourceProfile);
        runtimeState.scenario.write("Selecting Resource Profile: " + resourceProfile);
        runtimeState.addQuoteLinePage.selectResourceType(resourceProfile);
    }

    @When("^the line item Labour Type is selected$")
    public void the_line_item_Labor_Type_is_entered() throws Throwable {
        runtimeState.scenario.write("Selecting Labour Type: " + testData.getString("labourType"));
        runtimeState.addQuoteLinePage.selectLabourType(testData.getString("labourType"));
    }

    @When("^the line item Supplier is selected$")
    public void the_line_item_Supplier_is_selected() throws Throwable {
        String supplier = runtimeState.addQuoteLinePage.selectRandomSupplier();
        runtimeState.scenario.write(String.format("Selected %s as a random supplier" , supplier));
    }

    @When("^the line item description \"([^\"]*)\" is entered$")
    public void the_line_item_description_is_entered(String description) throws Throwable {
        // For some reason the description doesnt always enter.
        // Retrying for MAX_TIMEOUT until its entered successfully
        try {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> runtimeState.addQuoteLinePage.setLineDescription(description), notNullValue());
        } catch (Exception e) {
            throw new Exception("Cannot enter the quote line description");
        }
    }

    @When("^the line item quantity <([0-9]+)> is entered$")
    public void the_line_item_quantity_is_entered(Integer quantity) throws Throwable {
        runtimeState.addQuoteLinePage.setQuantity(quantity);
    }

    @When("^the line item quantity \"([^\"]*)\" is entered$")
    public void the_line_item_quantity_is_entered(String quantity) throws Throwable {
        runtimeState.addQuoteLinePage.setQuantity(quantity);
        outputHelper.takeScreenshots();
    }

    @When("^the line item unit price <([0-9.]+)> is entered$")
    public void the_line_item_unit_price_is_entered(Float unitPrice) throws Throwable {
        runtimeState.addQuoteLinePage.setUnitPrice(unitPrice);
    }

    @When("^the line item unit price \"([^\"]*)\" is entered$")
    public void the_line_item_unit_price_is_entered(String unitPrice) throws Throwable {
        runtimeState.addQuoteLinePage.setUnitPrice(unitPrice);
    }

    @When("^the line is not added to the quote$")
    public void the_line_is_not_added_to_the_quote() throws Throwable {
        runtimeState.addQuoteLinePage.cancelLine();
    }

    @When("^the line is added to the quote$")
    public void the_line_is_added_to_the_quote() throws Throwable {
        if(!testData.getString("profileName").contains("Contract") || testData.getString("profileName").equals("Contractor Admin")) {
            AddLineItemForm form = new AddLineItemForm();
            form.setType(runtimeState.addQuoteLinePage.getLineType());

            switch(form.getType().toLowerCase()) {
            case "parts":
                form.setType("Parts (" + runtimeState.addQuoteLinePage.getLinePartNumber() + ")");
                if (!runtimeState.addQuoteLinePage.getLineDescription().contentEquals("") && !runtimeState.addQuoteLinePage.getLineDescription().contentEquals("invalid")) {
                    form.setDescription(runtimeState.addQuoteLinePage.getLineDescription());
                }
                if (!runtimeState.addQuoteLinePage.getLineQuantity().contentEquals("") && !runtimeState.addQuoteLinePage.getLineQuantity().contentEquals("invalid")) {
                    form.setQuantity(Integer.valueOf(runtimeState.addQuoteLinePage.getLineQuantity()));
                }
                if (!runtimeState.addQuoteLinePage.getLineUnitPrice().contentEquals("") && !runtimeState.addQuoteLinePage.getLineUnitPrice().contentEquals("invalid")) {
                    form.setUnitPrice(Float.valueOf(runtimeState.addQuoteLinePage.getLineUnitPrice()));
                }
                if (!runtimeState.addQuoteLinePage.getLineValue().contentEquals("") && !runtimeState.addQuoteLinePage.getLineValue().contentEquals("invalid")) {
                    form.setLineValue(Float.valueOf(runtimeState.addQuoteLinePage.getLineValue()));
                }
                break;
            case "labor":
            case "labour":
            case "hire/access equipment":
            case "travel":
                if (!runtimeState.addQuoteLinePage.getLineDescription().contentEquals("") && !runtimeState.addQuoteLinePage.getLineDescription().contentEquals("invalid")) {
                    form.setDescription(runtimeState.addQuoteLinePage.getLineDescription());
                }
                if (!runtimeState.addQuoteLinePage.getLineQuantity().contentEquals("") && !runtimeState.addQuoteLinePage.getLineQuantity().contentEquals("invalid")) {
                    form.setQuantity(Integer.valueOf(runtimeState.addQuoteLinePage.getLineQuantity()));
                }
                if (!runtimeState.addQuoteLinePage.getLineUnitPrice().contentEquals("") && !runtimeState.addQuoteLinePage.getLineUnitPrice().contentEquals("invalid")) {
                    form.setUnitPrice(Float.valueOf(runtimeState.addQuoteLinePage.getLineUnitPrice()));
                }
                if (!runtimeState.addQuoteLinePage.getLineValue().contentEquals("") && !runtimeState.addQuoteLinePage.getLineValue().contentEquals("invalid")) {
                    form.setLineValue(Float.valueOf(runtimeState.addQuoteLinePage.getLineValue()));
                }
                break;
            default:
                if (!runtimeState.addQuoteLinePage.getLineDescription().contentEquals("") && !runtimeState.addQuoteLinePage.getLineDescription().contentEquals("invalid")) {
                    form.setDescription(runtimeState.addQuoteLinePage.getLineDescription());
                }
                if (!runtimeState.addQuoteLinePage.getLineQuantity().contentEquals("") && !runtimeState.addQuoteLinePage.getLineQuantity().contentEquals("invalid")) {
                    form.setQuantity(Integer.valueOf(runtimeState.addQuoteLinePage.getLineQuantity()));
                }
                if (!runtimeState.addQuoteLinePage.getLineUnitPrice().contentEquals("") && !runtimeState.addQuoteLinePage.getLineUnitPrice().contentEquals("invalid")) {
                    form.setUnitPrice(Float.valueOf(runtimeState.addQuoteLinePage.getLineUnitPrice()));
                }
                if (!runtimeState.addQuoteLinePage.getLineValue().contentEquals("") && !runtimeState.addQuoteLinePage.getLineValue().contentEquals("invalid")) {
                    form.setLineValue(Float.valueOf(runtimeState.addQuoteLinePage.getLineValue()));
                }
                break;
            }
            try {
                quoteLineItems.add(form);
            } catch (Exception e){
            }

        }
        outputHelper.takeScreenshots();
        runtimeState.addQuoteLinePage.addLine();
        runtimeState.scenario.write("Line added");
    }

    @When("^the line with description \"([^\"]*)\" is selected for delete$")
    public void the_line_with_description_is_selected_for_delete(String lineDescription) throws Throwable {
        runtimeState.deleteQuoteLinePage = runtimeState.registerQuotePage.deleteLine(lineDescription);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Quote line grid is updated with the delete$")
    public void the_Quote_line_grid_is_updated_with_the_delete() throws Throwable {
        the_Quote_line_grid_is_empty();
    }


    @When("^the line is deleted$")
    public void the_line_is_deleted() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.registerQuotePage = runtimeState.deleteQuoteLinePage.deleteLine();
    }

    @When("^the line is not deleted$")
    public void the_line_is_not_deleted() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.registerQuotePage = runtimeState.deleteQuoteLinePage.cancelDelete();
    }

    @When("^a line with <type>, <description>, <quantity>, <unitPrice>, <minUnitPrice>, <maxUnitPrice> is added$")
    public void the_a_line_with_type_description_quantity_price_is_added(List<AddLineItemForm> form) throws Throwable {
        quoteLineItems = form;
        for (AddLineItemForm line : quoteLineItems) {
            a_new_line_is_added_to_the_quote_breakdown();
            the_line_item_type_is_entered(line.getType());
            if ("Parts".equalsIgnoreCase(line.getType())) {
                String partCode = "ACME-PN-" + DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0);
                line.setPartCode(partCode);
                the_line_item_part_number_is_entered(partCode);
            }
            the_line_item_description_is_entered(line.getDescription());
            the_line_item_quantity_is_entered(line.getQuantity());
            the_line_item_unit_price_is_entered(line.getUnitPrice());
            the_line_is_added_to_the_quote();
        }
    }

    @When("^a line with <type>, <description>, <quantity>, <unitPrice> is entered$")
    public void a_line_with_type_description_quantity_unitPrice_is_entered(DataTable quoteLine) throws Throwable {

        List<Map<String, String>> list = quoteLine.asMaps(String.class, String.class);
        a_new_line_is_added_to_the_quote_breakdown();
        the_line_item_type_is_entered(list.get(0).get("type"));
        the_line_item_description_is_entered(list.get(0).get("description"));
        the_line_item_quantity_is_entered(list.get(0).get("quantity"));
        the_line_item_unit_price_is_entered(list.get(0).get("unitPrice"));
    }

    @When("^uploads the quote document$")
    public void and_uploads_the_quote_document() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.uploadQuotePage = runtimeState.registerQuotePage.uploadQuoteDocument();
        runtimeState.uploadQuotePage.setFileName(System.getProperty("user.dir")  + "\\src\\test\\resources\\portalfiles\\smallquote.pdf");
        outputHelper.takeScreenshots();
        runtimeState.uploadQuotePage.uploadFile();
    }

    @When("^uploads the quote document which is too large$")
    public void and_uploads_the_quote_document_which_is_too_large() throws Throwable {
        runtimeState.uploadQuotePage = runtimeState.registerQuotePage.uploadQuoteDocument();
        runtimeState.uploadQuotePage.setFileName(System.getProperty("user.dir")  + "\\src\\test\\resources\\portalfiles\\largequote.pdf");
        outputHelper.takeScreenshots();
        runtimeState.uploadQuotePage.uploadFile();
    }

    @When("^the user submits the quote$")
    public void the_user_submits_the_quote() throws Throwable {
        runtimeState.registerQuotePage.submitQuote();
        // Now store details of who submitted the quote
        String resourceName = dbHelperResources.getResourceName(testData.getInt("resourceId"));
        Map<String, Object> newResource = new HashMap<>();
        newResource.put("ApprovalStatusName", "AwaitingApproval");
        newResource.put("ResourceName", resourceName);
        this.testData.addMap("resources", newResource);
        testData.addMap("resources", newResource);

        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the line value is calculated correctly$")
    public void the_line_value_is_calculated_correctly() throws Throwable {
        String actualLineValue = runtimeState.addQuoteLinePage.getLineValue();
        Double quantity =  Double.valueOf(runtimeState.addQuoteLinePage.getLineQuantity());
        Double unitPrice = Double.valueOf(runtimeState.addQuoteLinePage.getLineUnitPrice());
        Double expectedLineValue = quantity * unitPrice;
        runtimeState.scenario.write("Calculated value" + actualLineValue);
        runtimeState.scenario.write("Expected value" + expectedLineValue);
        assertEquals("Unexpected line value", actualLineValue, stepHelper.getMoneyString(expectedLineValue));
    }

    private void completeInvitationsToQuote() {
        if (runtimeState.portalLogAJobPage.isInvitationsToQuoteDisplayed()) {
            for (int i = 1; i <= runtimeState.portalLogAJobPage.getNumberOfQuotesRequired(); i++){
                String resourceType = runtimeState.portalLogAJobPage.selectRandomResourceTypeToQuote(i);
                runtimeState.portalLogAJobPage.showAllResourcesToQuote(i);
                String resource = runtimeState.portalLogAJobPage.selectRandomResourceToQuote(i);
                runtimeState.scenario.write("Invitation To Quote selected : " + resourceType +  " : " + resource);
            }

        }
    }

    @When("^a ((?:urgent|non-urgent)) quote with a \"([^\"]*)\" funding route is requested$")
    public void a_quote_with_urgency_and_funding_route_is_requested(String urgency, String fundingRoute) throws Throwable{
        // Determine if quote is immediate or not and convert to boolean
        Boolean immediate;
        if ("urgent".equalsIgnoreCase(urgency)) {
            immediate = true;
        } else {
            immediate = false;
        }

        // Obtain a random value from the database as per step requirements
        QuotePriority quotePriority = quotePriorityDao.getQuotePriorityByUrgencyAndFundingRoute(immediate, fundingRoute);

        portalCommon.I_request_a_quote(fundingRoute);
        portalCommon.quote_priority_is_set_to(quotePriority.getName());

        if (getWebDriver().getCurrentUrl().contains("Portal/Job/Update/")) {
            // Portal Update a Job
            if (runtimeState.updateJobPage.isQuestionDisplayed("Is this Quote a Potential Insurance Quote?")) {
                runtimeState.updateJobPage.selectPotentialInsuranceQuote(false);
            }

        } else if (getWebDriver().getCurrentUrl().contains("Portal/Job/Log")) {
            // Portal Log a Job
            if (runtimeState.portalLogAJobPage.isQuestionDisplayed("Is this Quote a Potential Insurance Quote?")){
                runtimeState.portalLogAJobPage.selectPotentialInsuranceQuote(false);
            }

            completeInvitationsToQuote();
        }
    }

    @When("^show Quote Query history is clicked$")
    public void show_hide_quote_query_history() throws Throwable {
        runtimeState.quotesManagersDecisionPage.showQuoteHistory();
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the query request and response timeline will be displayed correctly$")
    public void the_query_request_and_response_timeline_will_be_displayed() throws Throwable {
        Grid grid = runtimeState.quotesManagersDecisionPage.getHistoryGrid();
        assertNotNull("Unexpected Null Grid", grid);

        String[] expectedHeaders = { "Query/Response Date", "Query/Response From", "Query/Response"};

        for (String header : grid.getHeaders()) {
            assertTrue("Expected " + expectedHeaders.toString() + " to contain " + header, ArrayUtils.contains(expectedHeaders, header));
        }
        assertTrue("Unexpected number of headers returned, found : " + grid.getHeaders().size() + " expected : " + expectedHeaders.length, expectedHeaders.length == grid.getHeaders().size());

        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" warning ((?:is|is not)) displayed$")
    public void the_portal_warming_is_displayed(String warning, String displayed) throws Throwable {

        boolean alertDisplayed = "is".equalsIgnoreCase(displayed) ? true : false;

        switch(warning) {
        case "Scope of Works" :
            assertEquals("Unexpected scope of works alert", alertDisplayed, runtimeState.quoteJobEdit.isScopeOfWorksAlertDisplayed());
            break;
        case "Quote Priority" :
            assertEquals("Unexpected Quote Priority alert", alertDisplayed, runtimeState.quoteJobEdit.isQuotePriorityAlertDisplayed());
            break;
        default :
            throw new Exception("Cannot find expected warning");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" button ((?:is|is not)) displayed$")
    public void the_portal_button_is_displayed(String button, String displayed) throws Throwable {
        boolean buttonDisplayed = "is".equalsIgnoreCase(displayed) ? true : false;

        switch(button) {
        case "Bypass Multi-Quote" :
            assertEquals("Unexpected Bypass Multi-Quote", buttonDisplayed, runtimeState.quoteJobEdit.isBypassMultiQuoteButtonDisplayed());
            break;
        default :
            throw new Exception("Cannot find expected button");
        }
    }


    @When("^Bypass Multi-Quote is clicked$")
    public void bypass_Multi_Quote_is_clicked() throws Throwable {
        runtimeState.quoteJobEdit.byPassMultiQuote();
    }

    @When("^a Multi-Quote Bypass Reason is selected$")
    public void a_Multi_Quote_Bypass_Reason_is_selected() throws Throwable {
        runtimeState.quoteJobEdit.selectRandomMultiQuoteByPassReason();
    }

    @When("^a Multi-Quote Bypass Note is entered$")
    public void a_Multi_Quote_Bypass_Note_is_entered() throws Throwable {
        String notes = "Notes for Multi-Quote Bypass, entered on  " + DateHelper.dateAsString(new Date());
        testData.addStringTag("NotesforMultiQuoteBypass", notes);
        runtimeState.quoteJobEdit.enterMultiQuoteByPassNotes(notes);
    }

    @When("^the confirmation for recommending the quote is given$")
    public void the_confirmation_for_recommending_the_quote_is_given() throws Throwable {
        runtimeState.quoteJobEdit.confirmRecommendQuoteForApproval();
    }

    @When("^the Quote Recommendation note is entered$")
    public void the_Quote_Recommendation_note_is_entered() throws Throwable {
        String notes = "Notes for Quote Recommendation, entered on  " + DateHelper.dateAsString(new Date());
        runtimeState.quoteJobEdit.enterConfirmRecommendationNotes(notes);
    };

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" ((?:is|is not)) editable$")
    public void the_is_not_editable(String element, String editable) throws Throwable {

        Boolean canEdit = (editable.equalsIgnoreCase("is not")) ? false : true;

        switch(element) {
        case "Scope of Works" :
            assertEquals("Unexpected control state for Scope of Works", canEdit, runtimeState.quoteJobEdit.getScopeOfWorksEditState());
            break;
        case "Number of Quotes Required" :
            assertEquals("Unexpected control state for Number of Quotes Required", canEdit, runtimeState.quoteJobEdit.getNumberOfQuotesEditState());
            break;
        case "Bypass information":
            assertEquals("Unexpected control state for MultiQyote Bypass Reason", canEdit, runtimeState.quoteJobEdit.getByPassReasonEditState());
            assertEquals("Unexpected control state for Multi-Quote Bypass Note", canEdit, runtimeState.quoteJobEdit.getMultiQuoteByPassNotesEditState());
            assertEquals("Unexpected control state for Confirm that you are recommending this quote for approval", canEdit, runtimeState.quoteJobEdit.getConfirmRecommendQuoteForApprovalEditState());
            assertEquals("Unexpected control state for Quote Recommendation Note", canEdit, runtimeState.quoteJobEdit.getConfirmRecommendationNotesEditState());
            break;
        case "Budget Route":
            assertEquals("Unexpected control state for Budget Route", canEdit, runtimeState.fundingRequestsAwaitingApprovalPage.getBudgetRouteDropdownEditState());
            break;
        case "Budget":
            assertEquals("Unexpected control state for Budget ", canEdit, runtimeState.partsOrderAwaitingApprovalPage.getBudgetDropdownEditState());
            break;
        default :
            throw new Exception("Cannot find element to check in switch");
        }

    }

    @When("^the \"([^\"]*)\" is increased$")
    public void the_is_increased(String element) throws Throwable {
        if ("Number of Quotes Required".equalsIgnoreCase(element)) {
            String numberOfQuotesValue = runtimeState.quoteJobEdit.getNumberOfQuotesValue();
            Integer numberOfQuotes = null;
            if (StringUtils.isNumeric(numberOfQuotesValue)) {
                numberOfQuotes = Integer.valueOf(numberOfQuotesValue) + 1;
                runtimeState.quoteJobEdit.selectNumberOfQuotesByValue(numberOfQuotes.toString());

                // Need to check for the alert and dismiss it
                PortalPopupPage popup = new PortalPopupPage(getWebDriver());
                if(popup.isPopupVisible()) {
                    outputHelper.takeScreenshots();
                    popup.clickYes();
                }
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the job details displayed is view only$")
    public void the_job_details_is_view_only() throws Throwable {
        assertTrue("The Job details are not view only", runtimeState.quoteJobEdit.isJobDetailViewOnly());
    }

    @Then("^the job details displayed on \"([^\"]*)\" is view only$")
    public void the_job_details_section_on_is_view_only(String page) throws Throwable {

        switch(page) {

        case "Quote Approval Decision":
        case "Quote Managers Decision":
            assertTrue("The Job details are not view only", runtimeState.quotesManagersDecisionPage.isJobDetailViewOnly());
            break;

        default:
            throw new Exception("Cannot find the given page");
        }
    }

    @Then("^the Resource Quote details are displayed correctly$")
    public void the_Resource_Quote_details_are_displayed_correctly() throws Throwable {
        portalCommon.the_table_on_page_displays_correctly("Resource Quotes", "Quote Managers Decision");

        Grid grid = runtimeState.quotesManagersDecisionPage.getQuoteDetailsGrid();
        List<Row> row = grid.getRows();

        List<Map<String, Object>> dbData = dbHelperQuotes.getQuoteDetails(testData.getString("jobReference"));

        String expectedUnitPrice = dbData.get(0).get("Unit Price").toString();
        String expectedValue = dbData.get(0).get("Value").toString();

        String actualUP = row.get(0).getCell("Unit price ("+ CURRENCY_SYMBOL +")").getText().replace(",", "");
        String actualVal = row.get(0).getCell("Value ("+ CURRENCY_SYMBOL +")").getText().replace(",", "");

        assertEquals("The Description of work is not shown correctly", dbData.get(0).get("Description Of Work").toString(), runtimeState.quotesManagersDecisionPage.getDescriptionOfWorksValue());
        assertEquals("The Proposed working times is not shown correctly", dbData.get(0).get("Proposed working times").toString(), runtimeState.quotesManagersDecisionPage.getProposedWorkingTime());
        assertEquals("The Quote Details description is not shown correctly", dbData.get(0).get("Description").toString() , row.get(0).getCell("Description").getText());
        assertEquals("The Quote Details Type is not shown correctly", dbData.get(0).get("Type").toString() , row.get(0).getCell("Type").getText());
        assertEquals("The Quote Quantity is not shown correctly", dbData.get(0).get("Quantity").toString() , row.get(0).getCell("Quantity").getText());
        assertTrue("The Quote Unit Price is not shown correctly", actualUP.contains(expectedUnitPrice));
        assertTrue("The Quote Value is not shown correctly", actualVal.contains(expectedValue));
    }

    @Then("^the Resource status will be None Assigned$")
    public void the_Resource_status_will_be_none_assigned() throws Throwable {
        assertTrue("The Resource status is not None Assigned", dbHelperQuotes.getResourceCountAssignedToTheJob(testData.getInt("jobReference")) == 0);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" is editable on page \"([^\"]*)\"$")
    public void the_element_is_editable_on_page(String element, String page) throws Throwable {

        if (element.equalsIgnoreCase("Scope Of Works")) {
            switch (page) {
            case "Approve Quote Request":
                assertTrue("Unexpected control state for Scope of Works", runtimeState.quoteJobEdit.getScopeOfWorksEditState());
                break;

            case "Jobs Awaiting Resource Selection":
                assertTrue("Unexpected control state for Scope of Works", runtimeState.jobsAwaitingResourceSelectionPage.getScopeOfWorksEditState());
                break;

            case "Jobs Awaiting Quote":
                assertTrue("Unexpected control state for Scope of Works", runtimeState.managerJobsAwaitingQuotePage.getScopeOfWorksEditState());
                break;

            default:
                throw new Exception("Cannot find page to check in switch");
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the user can view the query reason and comments input by the initial approver$")
    public void the_user_can_view_the_query_reason_and_comments_input_by_the_initial_approver() throws Throwable {

        List<Map<String, Object>> dbData = dbHelperQuotes.getQueryReasonAndComments(testData.getInt("jobReference"));

        assertTrue("The Query Reason is not shown correctly", runtimeState.quoteQueryPage.getReasonText().contains(dbData.get(0).get("Reason").toString()));
        assertEquals("The Query text is not shown correctly", dbData.get(0).get("Comments").toString(), runtimeState.quoteQueryPage.getQueryText());
    }

    @ContinueNextStepsOnException
    @Then("^all the elements on page \"([^\"]*)\" are editable$")
    public void all_the_elements_on_page_are_editable(String page) throws Throwable {

        switch (page) {
        case "Edit Quote Details":
            assertTrue("Unexpected control state for Description of Works edit box", runtimeState.editQuoteDetailsPage.getDescriptionOfWorksEditState());
            assertTrue("Unexpected control state for High Risk Yes Radio button", runtimeState.editQuoteDetailsPage.getYesRadioButtonEditState());
            assertTrue("Unexpected control state for High Risk No Radio button", runtimeState.editQuoteDetailsPage.getNoRadioButtonEditState());
            assertTrue("Unexpected control state for Proposed Working Times edit box", runtimeState.editQuoteDetailsPage.getProposedWorkingTimesEditState());
            assertTrue("Unexpected control state for Save Changes button", runtimeState.editQuoteDetailsPage.getSaveChangesButtonEditState());
            break;

        default:
            throw new Exception("Cannot find a page");
        }
    }

    @When("^the description of works is updated$")
    public void the_description_of_works_is_updated() throws Throwable {
        String descriptionOfWorks = "Description of works midified on  " + DateHelper.dateAsString(new Date());
        runtimeState.editQuoteDetailsPage.enterDescriptionOfWorks(descriptionOfWorks);
        runtimeState.scenario.write(descriptionOfWorks);
        runtimeState.editQuoteDetailsPage.clickSaveChangesButton();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" notification has been updated$")
    public void the_notification_has_been_updated(String notification) throws Throwable {
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> dbHelperQuotes.getRecordCount(testData.getInt("jobReference"), notification) != 0);
    }
}