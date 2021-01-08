package mercury.steps.portal.fundingRequests;

import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.SHORT;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.helpers.Globalisation.SHORT_TIME;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static mercury.helpers.StringHelper.trimZeros;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.database.dao.FundingRequestDao;
import mercury.database.models.FundingRequest;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.portal.fundingrequests.FundingRequestQueriesPage;
import mercury.pageobject.web.portal.fundingrequests.InitialFundingRequestsApprovalPage;
import mercury.pageobject.web.portal.quotes.SubmitQuoteJobRecommendModalPage;
import mercury.runtime.RuntimeState;

public class FundingRequestsAwaitingApprovalSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private FundingRequestDao fundingRequestDao;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TzHelper tzHelper;

    @And("^the response to the query is submitted$")
    public void the_response_to_the_query_is_submitted() throws Throwable {
        String response = "Test response entered on " + DateHelper.dateAsString(new Date());
        testData.put("response", response);
        if (runtimeState.fundingRequestsAwaitingApprovalPage.isSubmitQuoteJobRecommendationModalDisplayed()) {
            runtimeState.submitQuoteJobRecommendModalPage = new SubmitQuoteJobRecommendModalPage(getWebDriver()).get();
            runtimeState.submitQuoteJobRecommendModalPage.setInternalNotes(response);
            runtimeState.submitQuoteJobRecommendModalPage.saveRecommendation();
            runtimeState.fundingRequestQueriesPage = new FundingRequestQueriesPage(getWebDriver()).get();
        } else {
            runtimeState.fundingRequestsAwaitingApprovalPage.enterResponse(response);
            runtimeState.fundingRequestsAwaitingApprovalPage.clickSendResponseButton();
        }
        runtimeState.scenario.write("Submitted response to funding request query: " + response);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ContinueNextStepsOnException
    @Then("^the internal job notes or queries are displayed$")
    public void the_internal_job_notes_or_queries_are_displayed() throws Throwable {
        runtimeState.scenario.write("Asserting the details displayed in the Internal Job Notes/Queries section ");
        List<Map<String, Object>> dbData = dbHelperQuotes.getQuoteJobInternalNotesOrQueries(testData.getInt("jobReference"));
        Iterator iterator = dbData.iterator();
        for (int i = 0; i < dbData.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) iterator.next();
            if (!testData.getBoolean("skipCheckDates")) {
                String name = map.get("Name").toString();
                String onDate = map.get("OnDate").toString();
                onDate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), onDate, DB_DATE_FORMAT);
                String atDate  = DateHelper.convert(onDate, DB_DATE_FORMAT, SHORT_DATE);
                String atTime  = DateHelper.convert(onDate, DB_DATE_FORMAT, SHORT_TIME);
                String expected = String.format("%s on %s at %s", name, atDate, atTime);
                String actual = runtimeState.fundingRequestsAwaitingApprovalPage.getInternalJobNoteOrQueryLabel(i);
                assertThat("Unexpected label. Expected:  " + expected, expected, IsEqualIgnoringCase.equalToIgnoringCase(actual));
            }
            assertEquals("Unexpected Notes ", map.get("Notes").toString().replaceAll("\\s+", ""), runtimeState.fundingRequestsAwaitingApprovalPage.getInternalJobNoteOrQuery(i).replaceAll("\\s+", ""));
            assertTrue("Unexpected user icon ", runtimeState.fundingRequestsAwaitingApprovalPage.isInternalJobNoteOrQueryUserDisplayed(i));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the internal job notes or queries are displayed without dates$")
    public void the_internal_job_notes_or_queries_are_displayed_without_dates() throws Throwable {
        testData.put("skipCheckDates", false);
        the_internal_job_notes_or_queries_are_displayed();
    }

    @ContinueNextStepsOnException
    @Then("^the current budget route is displayed$")
    public void the_current_budget_route_is_displayed() throws Throwable {
        String expectedBudgetRoute = dbHelperQuotes.getBudgetRoute(testData.getInt("jobReference"));
        // currently 'Insurance' funding route option is removed from UKRB.
        // Older jobs which were created using Insurance as funding route are asking users to select a funding route again on portal
        expectedBudgetRoute = expectedBudgetRoute.equals("Insurance") && LOCALE.equals("en-GB") ? "-- Please select budget route --" : expectedBudgetRoute;
        runtimeState.scenario.write("Asserting budget route is: " + expectedBudgetRoute);
        assertEquals("Unexpected Budget Route ", expectedBudgetRoute, runtimeState.fundingRequestsAwaitingApprovalPage.getBudgetRoute());
    }

    @And("^the query reason is selected$")
    public void the_query_reason_is_selected() throws Throwable {
        String queryReason = runtimeState.queryInitialApproverModal.selectRandomQueryReason();
        testData.put("queryReason", queryReason);
    }

    @And("^the query notes are entered$")
    public void the_query_notes_are_entered() throws Throwable {
        String queryNotes = "Test query notes entered on " + DateHelper.dateAsString(new Date());
        runtimeState.queryInitialApproverModal.enterQueryNotes(queryNotes);
        testData.put("queryNotes", queryNotes);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void assertDataInFundingRequestsGrid(String page, Grid grid, List<Map<String, Object>> dbData, String compareField) throws Exception {

        // Compare both sets of data are the same size
        assertEquals("Unexpected number of records", dbData.size(), grid.getRows().size());

        // Sorting the data in ascending order of compareField
        List<LinkedHashMap<String, Object>> gridRows = GridHelper.getGridAsMapList(grid);
        Collections.sort(gridRows, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                return ((String) o1.get(compareField)).compareTo((String) o2.get(compareField));
            }
        });
        Collections.sort(dbData, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                return ((String) o1.get(compareField)).compareTo((String) o2.get(compareField));
            }
        });

        Iterator it = dbData.iterator();
        for (int i = 0; i < gridRows.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) it.next();
            switch (page) {
            case "Senior Manager Funding Request Decision":
            case "Funding Request Query Response Required":
            case "Funding Request Rejected":
                String resource = gridRows.get(i).get("Resource").toString();
                logger.debug("Quote Resource:" + resource);
                assertEquals("Unexpected Quote Ref for " + resource, normalize(map.get("Quote Ref").toString()), normalize(gridRows.get(i).get("Quote Ref").toString()));
                if (!testData.getBoolean("skipCheckDates")) {
                    String quoteDate = map.get("Quote Date").toString();
                    quoteDate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), quoteDate, DB_DATE_FORMAT);
                    quoteDate = DateHelper.convert(quoteDate, DB_DATE_FORMAT, SHORT_DATE);
                    assertEquals("Unexpected Quote Date for " + resource, quoteDate, gridRows.get(i).get("Quote Date"));
                }
                assertEquals("Unexpected " + localize("Quote Value ($)") + " for " + resource, trimZeros(map.get(localize("Quote Value ($)")).toString().replaceAll(",", "")), trimZeros(gridRows.get(i).get(localize("Quote Value ($)")).toString().replaceAll(",", "")));
                assertEquals("Unexpected Resource", normalize(map.get("Resource").toString()), normalize(gridRows.get(i).get("Resource").toString()));
                assertEquals("Unexpected Queried for " + resource, map.get("Queried"), gridRows.get(i).get("Queried"));
                assertEquals("Unexpected Quote Document for " + resource, map.get("Quote Document"), gridRows.get(i).get("Quote Document"));
                assertEquals("Unexpected Supporting Documents for " + resource, map.get("Supporting Documents"), gridRows.get(i).get("Supporting Documents"));
                assertEquals("Unexpected Status for " + resource, map.get("Status"), gridRows.get(i).get("Status"));
                break;
            case "Funding Requests Pending Query Response":
                logger.debug("Job Ref: " + map.get("Job Ref"));
                assertEquals("Unexpected Job Ref ", map.get("Job Ref"), gridRows.get(i).get("Job Ref"));
                assertEquals("Unexpected Site ", map.get("Site"), gridRows.get(i).get("Site"));
                assertEquals("Unexpected Division ", map.get("Division"), gridRows.get(i).get("Division"));
                if (!testData.getBoolean("skipCheckDates")) {
                    String loggedDate = map.get("Logged Date").toString();
                    loggedDate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), loggedDate, DB_DATE_FORMAT);
                    loggedDate = DateHelper.convert(loggedDate, DB_DATE_FORMAT, SHORT_DATE);
                    assertEquals("Unexpected Logged Date ", loggedDate, gridRows.get(i).get("Logged Date"));
                }
                assertEquals("Unexpected Location ", map.get("Location"), gridRows.get(i).get("Location"));
                assertEquals("Unexpected Subtype/Classification ", map.get("Subtype/Classification"), gridRows.get(i).get("Subtype/Classification"));
                assertEquals("Unexpected Priority  ", map.get("Priority"), gridRows.get(i).get("Priority"));
                assertEquals("Unexpected No. of Quotes  ", map.get("No. of Quotes"), gridRows.get(i).get("No. of Quotes"));
                break;
            case "Funding Requests Awaiting Approval":
                logger.debug("Job Ref: " + map.get("Description"));
                assertEquals("Unexpected Description ", normalize(map.get("Description").toString()), normalize(gridRows.get(i).get("Description").toString()));
                assertEquals("Unexpected Type ", normalize(map.get("Type").toString()), normalize(gridRows.get(i).get("Type").toString()));
                assertEquals("Unexpected Quantity ", map.get("Quantity").toString().substring(0, map.get("Quantity").toString().indexOf(".")), gridRows.get(i).get("Quantity"));
                assertEquals("Unexpected Unit price ", map.get("UnitPrice").toString().replaceAll(",", ""), gridRows.get(i).get(localize("Unit price (£)")).toString().replaceAll(",", ""));
                assertEquals("Unexpected Value ", map.get("Value").toString().replaceAll(",", ""), gridRows.get(i).get(localize("Value (£)")).toString().replaceAll(",", ""));
                break;
            default:
                throw new Exception("Cannot find " + page);
            }
        }
        outputHelper.takeScreenshots();
    }

    @Then("^the RFM will also have the ability to Query the quote with the contractor if required$")
    public void the_rfm_will_also_have_the_ability_to_query_the_quote_with_the_contractor_if_required() throws Throwable {
        int numberOfQuotes = runtimeState.fundingRequestsAwaitingApprovalPage.getResourceQuotesGridRows();
        runtimeState.scenario.write("Asserting Query button is displayed on each quote");
        for ( int i=1; i <= numberOfQuotes; i++ ) {
            runtimeState.fundingRequestsAwaitingApprovalPage.selectResourceQuote(i);
            runtimeState.fundingRequestsAwaitingApprovalPage.isQueryQuoteButtonDisplayed();
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the funding request details are displayed$")
    public void the_funding_request_details_are_displayed() throws Throwable {
        int jobReference = testData.getInt("jobReference");
        runtimeState.scenario.write("Asserting Details in Funding Request Details section: ");
        FundingRequest fundingRequest = fundingRequestDao.getByJobReference(jobReference).get(0);

        if (runtimeState.initialFundingRequestsApprovalPage == null) {
            runtimeState.initialFundingRequestsApprovalPage = new InitialFundingRequestsApprovalPage(getWebDriver()).get();
        }

        String resource = dbHelperResources.getResourceNameForResourceAssignmentId(fundingRequest.getResourceAssignmentId());
        runtimeState.scenario.write("Asserting Resource is : " + resource);
        assertEquals("Unexpected Resource ", resource.replaceAll("\\s+", ""), runtimeState.initialFundingRequestsApprovalPage.getFundingRequestFieldDetail("Resource").replaceAll("\\s+", ""));

        String expectedCreatedDate = DateHelper.dateAsString(fundingRequestDao.getByJobReferenceResourceNameInitialFunding(jobReference, resource, "1").getCreatedOn(), SHORT).toUpperCase();
        expectedCreatedDate = tzHelper.adjustTimeForJobReference(jobReference, expectedCreatedDate, SHORT);
        runtimeState.scenario.write("Asserting Created Date is : " + expectedCreatedDate);
        assertEquals("Unexpected Created Date ", expectedCreatedDate, runtimeState.initialFundingRequestsApprovalPage.getFundingRequestFieldDetail("Date Created"));

        runtimeState.scenario.write("Asserting Description is : " + fundingRequest.getDescription());
        assertEquals("Unexpected Description ", fundingRequest.getDescription(), runtimeState.initialFundingRequestsApprovalPage.getFundingRequestFieldDetail("Description"));
    }

    @ContinueNextStepsOnException
    @And("^all resource assignment details are displayed$")
    public void all_resource_assignment_details_are_displayed() throws Throwable {
        runtimeState.scenario.write("Asserting Details in All Resource Assignments section: ");
        FundingRequest fundingRequest = fundingRequestDao.getByJobReference(testData.getInt("jobReference")).get(0);

        String resource = dbHelperResources.getResourceNameForResourceAssignmentId(fundingRequest.getResourceAssignmentId());
        runtimeState.scenario.write("Asserting Resource is : " + resource);
        assertEquals("Unexpected Resource ", resource.replaceAll("\\s+", ""), runtimeState.initialFundingRequestsApprovalPage.getResourceAssignmentsFieldDetail("Resource").replaceAll("\\s+", ""));

        runtimeState.scenario.write("Asserting Status is : Awaiting Funding Authorisation");
        assertEquals("Unexpected Status ", "Awaiting Funding Authorisation", runtimeState.initialFundingRequestsApprovalPage.getResourceAssignmentsFieldDetail("Status"));

        runtimeState.scenario.write("Asserting Cost is : " + CURRENCY_SYMBOL + "0");
        assertEquals("Unexpected Cost ", CURRENCY_SYMBOL + "0", runtimeState.initialFundingRequestsApprovalPage.getResourceAssignmentsFieldDetail("Cost"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ContinueNextStepsOnException
    @Then("^the Quote Job Rejection Summary is displayed$")
    public void the_quote_job_rejection_summary_is_displayed() throws Throwable {
        runtimeState.scenario.write("Asserting the details displayed in the Quote Job Rejection Summary section ");
        List<Map<String, Object>> dbData = dbHelperQuotes.getQuoteJobRejectionSummary(testData.getInt("jobReference"));
        Iterator iterator = dbData.iterator();
        for (int i = 0; i < dbData.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) iterator.next();
            runtimeState.scenario.write("Asserting ResourceName is: " + map.get("ResourceName"));
            assertEquals("Unexpected label ", normalize(map.get("ResourceName").toString()), normalize(runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteJobRejectionSummaryResourceName(i)));
            runtimeState.scenario.write("Asserting Rejection Notes is: " + map.get("Notes"));
            assertEquals("Unexpected Notes ", normalize(map.get("Notes").toString()), normalize(runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteJobRejectionSummaryRejectionNotes(i)));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the user is unable to interact with the jobs on this screen$")
    public void the_user_is_unable_to_interact_with_the_jobs_on_this_screen() throws Throwable {
        assertFalse("User is able to interact with the jobs on the screen! ", runtimeState.fundingRequestsPendingQueryResponsePage.isGridInteractive());
    }

    @ContinueNextStepsOnException
    @Then("^the Multi-Quote Bypass section is displayed$")
    public void the_multiquote_bypass_section_is_displayed() throws Throwable {
        Map<String, Object> dbData = dbHelperQuotes.getMultiQuoteByPassReasonAndNotes(testData.getInt("jobReference"));
        runtimeState.scenario.write("Asserting Multi-Quote By pass reason is: " + dbData.get("MultiQuoteBypassReasonName"));
        assertEquals("Multi-Quote By pass reason ", normalize(dbData.get("MultiQuoteBypassReasonName").toString()), normalize(runtimeState.multiQuoteAwaitingBypassReviewPage.getMultiQuoteByPassFieldValue("Reason")));
        runtimeState.scenario.write("Asserting Multi-Quote By pass notes is: " + dbData.get("MultiQuoteBypassNote"));
        assertEquals("Multi-Quote By pass reason ", normalize(dbData.get("MultiQuoteBypassNote").toString()), normalize(runtimeState.multiQuoteAwaitingBypassReviewPage.getMultiQuoteByPassFieldValue("Notes")));
    }

    @ContinueNextStepsOnException
    @And("^the Quote details recommended by RFM are displayed$")
    public void the_quote_details_recommended_by_rfm_are_displayed() throws Throwable {
        Integer numberOfQuotes = runtimeState.fundingRequestsAwaitingApprovalPage.getResourceQuotesGridRows();
        int jobReference = testData.getInt("jobReference");
        String jobState = testData.getString("jobState");
        for (int i = 1; i <= numberOfQuotes; i++) {
            runtimeState.fundingRequestsAwaitingApprovalPage.selectResourceQuote(i);
            String lozengeText = testData.getString("originalProfileName").equals("RFM") ? "RECOMMENDED by you" : "RECOMMENDED by " + dbHelperQuotes.getJobQuoteManagerName(testData.getInt("jobReference"));
            runtimeState.scenario.write("Asserting Lozenge displays text: " + lozengeText);
            assertEquals("Incorrect Lozenge text ", normalize(lozengeText), normalize(runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteDetailsDecisionLozengeText()));

            String resourceName = runtimeState.fundingRequestsAwaitingApprovalPage.getResourceQuotesGrid().getRows().get(i - 1).getCell("Resource").getText();
            String decisionNotes = dbHelperQuotes.getDecisionNotesForQuoteResource(jobReference, resourceName, jobState);
            runtimeState.scenario.write("Asserting decision notes is : " + decisionNotes);
            assertEquals("Incorrect decision notes ", normalize(decisionNotes), normalize(runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteDetailsDecisionNotes()));

            Map<String, Object> quoteDetails = dbHelperQuotes.getQuoteDetailsForResource(jobReference, resourceName, jobState);
            runtimeState.scenario.write("Asserting Description of works is " + quoteDetails.get("DescriptionOfWorks"));
            assertEquals("Incorrect Description Of Works ", normalize(quoteDetails.get("DescriptionOfWorks").toString()), normalize(runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteDetailsFieldValue("Description of works")));
            runtimeState.scenario.write("Asserting Proposed working times is " + quoteDetails.get("ProposedWorkingTimes"));
            assertEquals("Incorrect Proposed working times ", normalize(quoteDetails.get("ProposedWorkingTimes").toString()), normalize(runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteDetailsFieldValue("Proposed working times")));
            runtimeState.scenario.write("Asserting Is High Risk Work is " + quoteDetails.get("IsHighRiskWork"));
            assertEquals("Incorrect Is High Risk Work ", quoteDetails.get("IsHighRiskWork"), runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteDetailsFieldValue("Is High Risk Work"));
            if (quoteDetails.get("IsHighRiskWork").equals("Yes")) {
                String highRiskWorkTypes = StringUtils.join(dbHelperQuotes.getHighRiskWorkTypesForResource(jobReference, resourceName, jobState), ',');
                runtimeState.scenario.write("Asserting High Risk Work types is " + highRiskWorkTypes);
                assertEquals("Incorrect High Risk Work types ", highRiskWorkTypes, runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteDetailsFieldValue("High Risk Work types"));
            }

            List<Map<String, Object>> dbData = dbHelperQuotes.getJobQuotesDetailsForResource(jobReference, resourceName, jobState);

            assertDataInFundingRequestsGrid("Funding Requests Awaiting Approval", runtimeState.fundingRequestsAwaitingApprovalPage.getQuoteDetailsGrid(), dbData, "Description");

        }
    }
}
