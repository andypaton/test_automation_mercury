package mercury.steps.portal;

import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.MEDIUM_DATE;
import static mercury.helpers.Globalisation.MS_SHORT_DATE;
import static mercury.helpers.Globalisation.SHORT;
import static mercury.helpers.Globalisation.SHORT2;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.POHelper.scrollTo;
import static mercury.helpers.StringHelper.isNumeric;
import static mercury.helpers.StringHelper.normalize;
import static mercury.helpers.StringHelper.quote;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.QuoteApprovalScenariosDao;
import mercury.database.models.JobView;
import mercury.database.models.QuoteApprovalScenarios;
import mercury.databuilders.TestData;
import mercury.databuilders.UpdateJob;
import mercury.helpers.ComparisonHelper;
import mercury.helpers.DateHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperFundingRequests;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperOnCallScheduler;
import mercury.helpers.dbhelper.DbHelperOrganisation;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.gridV3.Cell;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.PopupAlert;
import mercury.pageobject.web.admin.sites.AdminSiteTypesPage;
import mercury.pageobject.web.portal.PortalSummaryPage;
import mercury.pageobject.web.portal.fundingrequests.FundingRequestsAwaitingResponseReviewPage;
import mercury.pageobject.web.portal.jobs.AsbestosModal;
import mercury.pageobject.web.portal.jobs.JobDetailsPage;
import mercury.runtime.RuntimeState;
import mercury.steps.admin.AdminJobsSteps;
import mercury.steps.admin.AdminSitesSteps;
import mercury.steps.portal.fundingRequests.FundingRequestsAwaitingApprovalSteps;
import mercury.steps.portal.jobs.PortalAllJobsSteps;

public class PortalCommon {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private TzHelper tzHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private OutputHelper outputHelper;
    @Autowired private UpdateJob updateJob;
    @Autowired private JobView currentJob;
    @Autowired private TestData testData;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private PortalAllJobsSteps portalAllJobsSteps;
    @Autowired private QuoteApprovalScenarios quoteApprovalScenarios;
    @Autowired private QuoteApprovalScenariosDao quoteApprovalScenariosDao;
    @Autowired private ComparisonHelper comparisonHelper;
    @Autowired private FundingRequestsAwaitingApprovalSteps fundingRequestsAwaitingApprovalSteps;
    @Autowired private DbHelperFundingRequests dbHelperFundingRequests;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperOrganisation dbHelperOrganisation;
    @Autowired private DbHelperOnCallScheduler dbHelperOnCallScheduler;
    @Autowired private AdminJobsSteps adminJobsSteps;
    @Autowired private AdminSitesSteps adminSitesSteps;
    @Autowired private MenuSteps menuSteps;
    @Autowired private DbHelperSites dbHelperSites;

    @When("^an? \"([^\"]*)\" quote is requested$")
    public void I_request_a_quote(String fundingRoute) throws Exception {
        // Need to get the alias for the quote type
        String alias = dbHelperQuotes.getFundingRouteAlias(fundingRoute);
        runtimeState.scenario.write("Alias for funding route " + fundingRoute + ": " + alias);
        if (getWebDriver().getCurrentUrl().contains("Portal/Job/Update/")) {
            // Portal Update a Job
            runtimeState.updateJobPage.selectQuoteType(alias);

        } else if (getWebDriver().getCurrentUrl().contains("Portal/QuoteJobEdit/Edit")) {
            switch (alias) {
            case "OPEX":
                runtimeState.quoteJobEdit.clickOpex(alias);
                break;
            case "CAPEX":
                runtimeState.quoteJobEdit.clickCapex();
                break;
            default:
                throw new Exception("Invalid quote type" + alias);
            }

        } else if (getWebDriver().getCurrentUrl().contains("Portal/Job/Log")) {
            // Portal Log a Job
            runtimeState.portalLogAJobPage.requestQuote();
            runtimeState.portalLogAJobPage.clickAnswer("Please select Quote Type", alias);
        }
    }

    private void completeInvitationsToQuote() {
        if (runtimeState.portalLogAJobPage.isInvitationsToQuoteDisplayed()) {
            try {
                runtimeState.portalLogAJobPage.getNumberOfQuotesRequired();
            } catch (NumberFormatException e) {
                runtimeState.portalLogAJobPage.selectNumberOfQuotesRequired(2);
            }

            for (int i = 1; i <= runtimeState.portalLogAJobPage.getNumberOfQuotesRequired(); i++){
                String resourceType = runtimeState.portalLogAJobPage.selectRandomResourceTypeToQuote(i);
                runtimeState.portalLogAJobPage.showAllResourcesToQuote(i);
                String resource = runtimeState.portalLogAJobPage.selectRandomResourceToQuote(i);
                runtimeState.scenario.write("Invitation To Quote selected : " + resourceType +  " : " + resource);
            }
        }
    }

    @When("^a random Quote Priority is selected$")
    public void a_random_quote_priority_is_selected() throws Exception {
        if (getWebDriver().getCurrentUrl().contains("Portal/Job/Update/")) {
            // Portal Update a Job
            runtimeState.updateJobPage.selectRandomQuotePriority();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Job/Log")) {
            // Portal Log a Job
            runtimeState.portalLogAJobPage.selectRandomQuotePriority();

            if (runtimeState.portalLogAJobPage.isQuestionDisplayed("Is this Quote a Potential Insurance Quote?")){
                runtimeState.portalLogAJobPage.selectPotentialInsuranceQuote(false);
            }

            completeInvitationsToQuote();
        }
    }

    @When("^Quote Priority is set to \"([^\"]*)\"$")
    public void quote_priority_is_set_to(String quotePriority) throws Exception {
        if (getWebDriver().getCurrentUrl().contains("Portal/Job/Update/")) {
            // Portal Update a Job
            runtimeState.updateJobPage.selectQuotePriority(quotePriority);
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Job/Log")) {
            // Portal Log a Job
            runtimeState.portalLogAJobPage.selectQuotePriority(quotePriority);
        }
    }

    @And("^I calculate the Job end date and duration$")
    public void i_calculate_the_job_enddate_and_duration() throws Throwable {
        Date siteDateTime = tzHelper.getCurrentTimeAtSite(currentJob.getSiteId());

        Timestamp createdOn = dbHelperResources.getResourceAssignmentCreationDate(testData.getInt("jobReference"), testData.getInt("resourceId"));
        logger.debug(DateHelper.getDifferenceToNow(createdOn));

        if (DateHelper.getDifferenceToNow(createdOn) < 2) {
            dbHelperResources.updateResourceAssignmentCreationDate(testData.getInt("jobReference"));
        }

        String workStart = DateHelper.getNowDatePlusOffset(-24, SHORT);
        String startTime = DateHelper.getNowDatePlusOffset(-24, SHORT).replaceAll("^.+? ", "");
        testData.put("workStart", workStart);

        long diffmins = Math.abs(DateHelper.getTimeDifferenceBetweenTwoDatesInMinutes(new Date(), siteDateTime));
        diffmins = diffmins > 1425 ? 1425 : diffmins;
        String randomDuration = DateHelper.getRandomDuration(diffmins);

        testData.put("startTime", startTime);
        testData.put("timeSpent", randomDuration);
    }

    @And("^the Work Start Date is selected$")
    public void the_work_start_date_is_selected() throws Throwable {
        i_calculate_the_job_enddate_and_duration();
        String etaDate = DateHelper.getNowDatePlusOffset(-24, SHORT_DATE);
        runtimeState.updateJobPage.enterWorkStartDate(etaDate + " 00:00");
    }

    @And("^a Work Start date and time is entered$")
    public void work_start_date_and_time_is_entered() throws Throwable {
        i_calculate_the_job_enddate_and_duration();
        runtimeState.updateJobPage.enterWorkStartDate(testData.getString("workStart"));
        String workStartTime = runtimeState.updateJobPage.getWorkStartTime();
        updateJob.setWorkStart(workStartTime);
        testData.put("Start time", DateHelper.stringAsDate(workStartTime, SHORT2));
    }

    @And("^a random Work Start Time is selected$")
    public void a_random_work_start_time_is_selected() throws Throwable {
        if (getWebDriver().getCurrentUrl().contains("Portal/Routed#!/ppm/")) {
            runtimeState.ppmJobDetailsPage.selectWorkStartDateAndTime();
            outputHelper.takeScreenshots();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Job/Update/")) {
            runtimeState.updateJobPage.selecWorkStartTime(testData.getString("startTime"));

            String workstartTime = runtimeState.updateJobPage.getWorkStartTime();
            updateJob.setWorkStart(workstartTime);
            runtimeState.scenario.write("Work start time : " + workstartTime);

            testData.put("Start time", DateHelper.stringAsDate(workstartTime, SHORT2));

            runtimeState.scenario.write("Work start time : " + runtimeState.updateJobPage.getWorkStartTime());

            outputHelper.takeScreenshots();
        } else {
            throw new Exception("Unable to locate page");
        }
    }

    @And("^a random Work End time is selected$")
    public void a_random_work_end_time_is_selected() throws Throwable {
        if (getWebDriver().getCurrentUrl().contains("Portal/Routed#!/ppm/")) {
            runtimeState.ppmJobDetailsPage.selectWorkEndTimeAndDate();
        } else if (getWebDriver().getCurrentUrl().contains("Portal/Job/Update/")) {
            runtimeState.updateJobPage.selectRandomWorkEndTime();
        } else {
            throw new Exception("Unable to locate page");
        }
    }

    @Then("^the \"([^\"]*)\" table contains the \"([^\"]*)\" section$")
    public void the_table_contains_the_section(String table, String section) throws Throwable {

        List<String> typeRows = getWebDriver().getCurrentUrl().contains("uswm") ? runtimeState.portalSummaryPage.getTypeRows() : runtimeState.portalSummaryPage.getTiles();
        if (!typeRows.isEmpty()) {
            runtimeState.scenario.write("Tiles found: " + typeRows);
            assertTrue("The " + section + " section is not displayed on the Summary page", typeRows.contains(section));
        } else {
            runtimeState.scenario.write("This user has no outstanding quotes with status: " + section);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" form (?:displays correctly|is displayed)$")
    public void the_form_displays_correctly(String pageName) throws Throwable {
        switch (pageName) {
        case "Jobs Awaiting Quote":
            assertTrue("Unexpected Page not found :", runtimeState.jobsAwaitingQuotePage.isPageLoaded());
            break;
        case "Quotes With Query Pending":
            assertTrue("Unexpected Page not found :", runtimeState.quotesWithQueryPendingPage.isPageLoaded());
            break;
        case "Quotes Awaiting Review":
            assertTrue("Unexpected Page not found :", runtimeState.quotesAwaitingReviewPage.isPageLoaded());
            break;
        case "Decline Invitation To Quote":
            assertTrue("Unexpected Page not found :", runtimeState.declineInvitationToQuotePage.isPageLoaded());
            break;
        case "Create Quote":
            assertTrue("Unexpected Page not found :", runtimeState.createQuotePage.isPageLoaded());
            break;
        case "Register Quote":
            assertTrue("Unexpected Page not found :", runtimeState.registerQuotePage.isPageLoaded());
            break;
        case "Quote Managers Decision":
            assertTrue("Unexpected Page not found :", runtimeState.quotesManagersDecisionPage.isPageLoaded());
            break;
        case "Senior Manager Funding Request Decision":
            assertTrue("Unexpected Page not found :", runtimeState.fundingRequestsAwaitingApprovalPage.isPageLoaded());
            break;
        case "Quote Query":
            assertTrue("Unexpected Page not found :", runtimeState.quoteQueryPage.isPageLoaded());
            break;
        case "On Call Scheduler":
            assertTrue("Unexpected Page not found :", runtimeState.onCallSchedulerPage.isPageLoaded());
            break;
        case "On Call Summary":
            assertTrue("Unexpected Page not found :", runtimeState.onCallSummaryPage.isPageLoaded());
            break;
        case "Jobs Awaiting Reallocation":
            assertTrue("Unexpected Page not found :", runtimeState.jobsAwaitingReallocationPage.isPageLoaded());
            break;
        case "Job Awaiting Quote":
        case "Job Awaiting Resource Assignment":
        case "Multi-Quote Awaiting Resource":
        case "Multi-Quote Awaiting Quote":
        case "Multi-Quote Awaiting Review":
            assertTrue("Unexpected Page not found :", runtimeState.quoteJobEdit.isPageLoaded());
            break;
        case "Multi-Quotes Awaiting Quote":
            assertTrue("Unexpected Page not found :", runtimeState.multiQuoteAwaitingQuotePage.isPageLoaded());
            break;
        case "Multi-Quotes Awaiting Review":
            assertTrue("Unexpected Page not found :", runtimeState.multiQuoteAwaitingReviewPage.isPageLoaded());
            break;
        case "Multi-Quotes Awaiting Resource":
            assertTrue("Unexpected Page not found :", runtimeState.multiQuoteAwaitingResourceSelectionPage.isPageLoaded());
            break;
        case "Multi-Quotes Awaiting Bypass Approval":
            assertTrue("Unexpected Page not found :", runtimeState.multiQuoteAwaitingBypassApprovalPage.isPageLoaded());
            break;
        case "Multi-Quotes Awaiting Bypass Review":
            assertTrue("Unexpected Page not found :", runtimeState.multiQuoteAwaitingBypassReviewPage.isPageLoaded());
            break;
        case "All Invoices Awaiting Approval":
            assertTrue("Unexpected Page not found :", runtimeState.allInvoicesAwaitingApprovalPage.isPageLoaded());
            break;
        case "All Credit Notes Awaiting Approval":
            assertTrue("Unexpected Page not found :", runtimeState.allCreditNotesAwaitingApprovalPage.isPageLoaded());
            break;
        case "Invoices & Credits Awaiting Review":
            assertTrue("Unexpected Page not found :", runtimeState.invoicesAndCreditsAwaitingReviewPage.isPageLoaded());
            break;
        case "Invoices Awaiting Review":
            assertTrue("Unexpected Page not found :", runtimeState.invoicesAwaitingReviewPage.isPageLoaded());
            break;
        case "Submitted Invoices and Credits":
            assertTrue("Unexpected Page not found :", runtimeState.submittedInvoicesAndCreditsPage.isPageLoaded());
            break;
        case "All Jobs":
            portalAllJobsSteps.allJobsFormDisplaysCorrectly();
            break;
        default:
            throw new Exception("Cannot find switch case for  " + pageName);
        }
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" table displays headers \"([^\"]*)\"$")
    public void table_displays_headers(String tableName, String headers) throws Exception {
        Grid grid = GridHelper.getGridForTableName(tableName);
        GridHelper.assertGridHeaders(grid.getHeaders(), headers.split(","));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" table displays sub-headers \"([^\"]*)\"$")
    public void table_displays_sub_headers(String tableName, String headers) throws Exception {
        Grid grid = GridHelper.getGridForTableName(tableName);
        GridHelper.assertGridHeaders(grid.getSubHeaders(), headers.split(","));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table is displayed$")
    public void the_table_is_displayed(String table) throws Exception {
        Grid grid;
        String[] expectedHeaders = null;

        switch (table) {
        case "Invoices Awaiting Approval":
            grid = runtimeState.invoicesAwaitingApprovalPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);
            String[] headersInvoicesAwaitingApproval = { "Invoice Ref", "Invoice Date", "Order Ref", "Budget", "Supplier", "Site", "Net (" + localize("£") + ")", localize("Vat") + " (" + localize("£") + ")", "Gross (" + localize("£") + ")" };
            expectedHeaders = headersInvoicesAwaitingApproval;
            break;

        case "Invoices Awaiting Final Approval":
            grid = runtimeState.invoicesAwaitingFinalApprovalPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);
            String[] headersInvoicesAwaitingFinalApproval = { "Invoice Ref", "Invoice Date", "Order Ref", "Initial Approver", "Budget", "Supplier", "Site", "Division", "Net (" + localize("£") + ")", localize("Vat") + " (" + localize("£") + ")", "Gross (" + localize("£") + ")" };
            expectedHeaders = headersInvoicesAwaitingFinalApproval;
            break;

        case "All Jobs":
            grid = runtimeState.allJobsPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);
            String[] headersAllJobs;
            if (getWebDriver().getCurrentUrl().contains("ukrb")) {
                headersAllJobs = new String[] { "Job reference", "Assignment status", "Site", "Asset subtype / classification", "Serial No", "Days outstanding", "Reference", "ETA" };
            } else {
                headersAllJobs = new String[] { "Job reference", "Status", "Asset subtype / classification", "Asset No", "Priority", "Priority", "Days outstanding", "Reference", "ETA" };
            }
            expectedHeaders = headersAllJobs;
            break;
        case "Jobs Awaiting Acceptance":
            grid = runtimeState.openAwaitingJobsPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);
            String[] headersJobsAwaitingAcceptance;
            if (getWebDriver().getCurrentUrl().contains("ukrb")) {
                headersJobsAwaitingAcceptance = new String[] { "Job reference", "Assignment status", "Site", "Asset subtype/classification", "Serial No", "Days outstanding" };
            } else {
                headersJobsAwaitingAcceptance = new String[] { "Job Reference", "Status", "Asset subtype/classification", "Asset No", "Priority", "Priority", "Days outstanding" };
            }
            expectedHeaders = headersJobsAwaitingAcceptance;
            break;
        case "Question List":
            grid = runtimeState.adminRulesEnginePage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);
            String[] headersQuestionList = { "Name", "Question", "Template", "Active", "" };
            expectedHeaders = headersQuestionList;
            break;
        default:
            throw new Exception("Cannot find table on page");
        }
        GridHelper.assertGridHeaders(grid.getHeaders(), expectedHeaders);
        runtimeState.scenario.write("Expected headers: " + Arrays.toString(expectedHeaders));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" table can be searched$")
    public void table_can_be_searched(String tableName) throws Exception {
        verifyTableSearching(tableName);
    }

    @ContinueNextStepsOnException
    @Then("^a search can be run on the \"([^\"]*)\" table$")
    public void the_table_can_be_searched(String table) throws Exception {
        switch (table) {
        case "All Jobs":
            searchAllJobs();
            break;

        case "Jobs Awaiting Reallocation":
            searchJobsAwaitingReallocation();
            break;

        case "Invoices Awaiting Approval":
            searchInvoicesAwaitingApproval();
            break;

        case "Invoices Awaiting Final Approval":
            searchInvoicesAwaitingFinalApproval();
            break;

        case "Open Jobs":
            String headersAllJobs;
            if (getWebDriver().getCurrentUrl().contains("ukrb")) {
                headersAllJobs = "Job reference, Assignment status, Site, Asset subtype / classification, Serial No, Assigned Date, Reference, ETA";
            } else {
                headersAllJobs = "Job reference, Status, Asset subtype / classification, Priority, Reference, ETA";
            }
            searchOpenJobs(headersAllJobs);
            break;

        case "Question List":
            searchQuestionList();
            break;

        default:
            throw new Exception("Cannot find table on page");
        }
    }

    private void searchQuestionList() throws Exception {
        //        String headersQuestionList = "Name, Question, Template, Active";  // Removing search on Active column due to bug MCP-13769
        String headersQuestionList = "Name, Question, Template";
        String[] allHeaders = headersQuestionList.split(" ?,\\s");

        for (String header : allHeaders) {
            switch (header) {
            case "Name":
            case "Question":
            case "Template":
            case "Active":
                assertSearchQuestionList(header);
                break;
            }
        }
    }

    private void verifyTableSearching(String tableName) throws Exception {
        GridHelper.searchTable(tableName, ""); // remove any filters

        Grid grid = GridHelper.getGridForTableName(tableName, 1); // get first row from grid
        assertNotNull("Unexpected Null Grid", grid);

        for (String header : grid.getHeaders()) {
            List<String> column = grid.getHeaders().contains(header) ? grid.getColumnText(header) : grid.getColumnSubText(header);
            String filter = column.get(RandomUtils.nextInt(0, column.size() - 1));

            if (!filter.isEmpty()) {
                GridHelper.searchTable(tableName, filter);
                POHelper.waitForStability();
                outputHelper.takeScreenshots();

                List<String> rows = GridHelper.getRowsAsString();
                for (String row : rows) {
                    String errorMessage = String.format("Search string '%s' not found in row: %s", filter, row);
                    assertTrue(errorMessage, row.contains(filter));
                }
            }
        }
    }

    private void searchAllJobs_OpsDirector() throws Exception {
        String headersAllJobs = "Job reference, Assignment status, Site, Asset subtype / classification, Assigned Date";
        String[] allHeaders = headersAllJobs.split(" ?,\\s");

        runtimeState.allJobsPage_OpsDirector.searchJobs("");              // remove any filters
        Grid grid = runtimeState.allJobsPage_OpsDirector.getGrid(1);      // get first row from grid
        assertNotNull("Unexpected Null Grid", grid);

        for (String header : allHeaders) {
            List<String> column = grid.getHeaders().contains(header) ? grid.getColumnText(header) : grid.getColumnSubText(header);
            String filter = column.get(RandomUtils.nextInt(0, column.size() - 1));

            if (!filter.isEmpty()) {
                runtimeState.allJobsPage_OpsDirector.searchJobs(filter);
                POHelper.waitForStability();
                outputHelper.takeScreenshots();

                List<String> rows = GridHelper.getRowsAsString();
                for (String row : rows) {
                    String errorMessage = String.format("Search string '%s' not found in row: %s", filter, row);
                    assertTrue(errorMessage, row.contains(filter));
                }
            }
        }
    }

    private void searchAllJobs() throws Exception {
        if (testData.getString("profileName").equals("Operations Director")) {
            searchAllJobs_OpsDirector();
        } else {
            String headersAllJobs = "Job reference, Assignment status, Site, Asset subtype / classification, Assigned Date";
            String[] allHeaders = headersAllJobs.split(" ?,\\s");

            runtimeState.allJobsPage.searchJobs("");              // remove any filters
            Grid grid = runtimeState.allJobsPage.getGrid(1);      // get first row from grid
            assertNotNull("Unexpected Null Grid", grid);

            for (String header : allHeaders) {
                List<String> column = grid.getHeaders().contains(header) ? grid.getColumnText(header) : grid.getColumnSubText(header);
                String filter = column.get(RandomUtils.nextInt(0, column.size() - 1));

                if (!filter.isEmpty()) {
                    runtimeState.allJobsPage.searchJobs(filter);
                    POHelper.waitForStability();
                    outputHelper.takeScreenshots();

                    List<String> rows = GridHelper.getRowsAsString();
                    for (String row : rows) {
                        String errorMessage = String.format("Search string '%s' not found in row: %s", filter, row);
                        assertTrue(errorMessage, row.contains(filter));
                    }
                }
            }
        }
    }

    private void searchAndVerify(String filter) {
        runtimeState.jobsAwaitingReallocationPage.searchJobs(filter);
        POHelper.waitForStability();
        outputHelper.takeScreenshots();

        filter = filter.trim();
        if (filter.matches("^\".*\"$")) {
            // if filter is surrounded by quotes then remove them
            filter = filter.replaceFirst("^\"", "").replaceAll("\"$", "");
        }

        List<String> rows = GridHelper.getRowsAsString();
        for (String row : rows) {
            String errorMessage = String.format("Search string '%s' not found in row: %s", filter, row);
            assertTrue(errorMessage, row.contains(filter));
        }
    }

    private void searchJobsAwaitingReallocation() throws Exception {
        String headersAllJobs = "Job Ref, Site, Resource, Resource Profile, Logged, Subtype/Classification, Description, Priority";
        String[] allHeaders = headersAllJobs.split(" ?,\\s");

        runtimeState.jobsAwaitingReallocationPage.searchJobs("");                   // remove any filters
        Grid grid = runtimeState.jobsAwaitingReallocationPage.getGridFirstRow(1);    // get first row from grid
        assertNotNull("Unexpected Null Grid", grid);

        for (String header : allHeaders) {
            List<String> column = grid.getHeaders().contains(header) ? grid.getColumnText(header) : grid.getColumnSubText(header);
            String filter = column.get(RandomUtils.nextInt(0, column.size() - 1));
            filter = filter.length() > 50 ? filter.substring(0, 49) : filter;   // restrict to first 50 chars

            if (!filter.isEmpty()) {
                try {
                    searchAndVerify(filter);
                } catch (AssertionError e) {
                    searchAndVerify("\"" + filter + "\"");
                }
            }
        }
    }

    private void searchOpenJobs(String headersAllJobs) throws Exception {
        String[] allHeaders = headersAllJobs.split(" ?,\\s");

        runtimeState.openAwaitingJobsPage.searchJobs("");              // remove any filters
        Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid(1);  // get first row from grid
        assertNotNull("Unexpected Null Grid", grid);

        for (String header : allHeaders) {
            List<String> column = grid.getHeaders().contains(header) ? grid.getColumnText(header) : grid.getColumnSubText(header);
            String filter = column.get(RandomUtils.nextInt(0, column.size() - 1));

            if (header.contains("ETA")) {
                filter = filter.split(" ")[0] + " " + filter.split(" ")[1] + " " + filter.split(" ")[2];
            }

            if (!filter.isEmpty()) {
                runtimeState.scenario.write("Searching for: " + filter);
                runtimeState.openAwaitingJobsPage.searchJobs(filter);
                POHelper.waitForStability();
                outputHelper.takeScreenshots();

                List<String> rows = GridHelper.getRowsAsString();
                for (String row : rows) {
                    String errorMessage = String.format("Search string '%s' not found in row: %s", filter, row);
                    assertTrue(errorMessage, row.toLowerCase().contains(filter.toLowerCase()));
                }
            }
        }
    }

    public void assertSearchQuestionList(String header) throws Exception {
        Grid grid = runtimeState.adminRulesEnginePage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<String> column = grid.getColumnText(header);
        String random = column.get(RandomUtils.nextInt(0, column.size() - 1));

        switch (header) {
        case "Name":
            runtimeState.adminRulesEnginePage.searchNameColumn(random);
            break;
        case "Question":
            runtimeState.adminRulesEnginePage.searchQuestionColumn(random);
            break;
        case "Template":
            runtimeState.adminRulesEnginePage.searchTemplateColumn(random);
            break;
        case "Active":
            runtimeState.adminRulesEnginePage.searchActiveColumn(random);
            break;
        }

        outputHelper.takeScreenshots();
        grid = runtimeState.adminRulesEnginePage.getGrid();
        assertTrue("Expected at least 1 row to be displayed. Search string: " + random, grid.getRows().size() > 0);
        testData.put("searchQuery", random);
        for (Row row : grid.getRows()) {
            boolean found = false;
            for (Cell cell : row.getCells()) {
                if (cell.getText() != null && cell.getText().toLowerCase().contains(random.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            assertTrue("Search string not found in row: " + random, found);
        }
        runtimeState.adminRulesEnginePage.clearActiveSearchColumn();
    }

    private void searchInvoicesAwaitingApproval() throws Exception {
        String[] headersInvoicesAwaitingApproval = { "Invoice Ref", "Invoice Date", "Order Ref", "Budget", "Supplier", "Site", "Net (" + localize("£") + ")", localize("Vat") + " (" + localize("£") + ")", "Gross (" + localize("£") + ")" };

        for (String header : headersInvoicesAwaitingApproval) {
            switch (header) {
            case "Invoice Ref":
                assertSearchInvoiceRefColumn(header);
                break;
            case "Invoice Date":
            case "Order Ref":
            case "Budget":
            case "Supplier":
            case "Site":
                assertSearchInvoicesAwaitingApproval(header);
                break;
            case "Net (£)":
            case "Vat (£)":
            case "Gross (£)":
                if(header.equals("Tax ($)") && LOCALE.equals("en-GB")) {
                    header = header.replace("Tax ($)", "Vat (£)");
                }
                assertSearchInvoicesAwaitingApproval(localize(header));
                break;
            }
        }
    }

    private void searchInvoicesAwaitingFinalApproval() throws Exception {
        String[] headersInvoicesAwaitingFinalApproval = { "Invoice Ref", "Invoice Date", "Order Ref", "Initial Approver", "Budget", "Supplier", "Site", "Division", "Net (" + localize("£") + ")", localize("Vat") + " (" + localize("£") + ")", "Gross (" + localize("£") + ")" };

        for (String header : headersInvoicesAwaitingFinalApproval) {
            switch (header) {
            case "Invoice Ref":
                assertSearchInvoiceRef(header);
                break;
            case "Invoice Date":
            case "Order Ref":
            case "Initial Approver":
            case "Budget":
            case "Supplier":
            case "Site":
            case "Division":
                assertSearchInvoicesAwaitingFinalApproval(header);
                break;
            case "Net (£)":
            case "Vat (£)":
            case "Gross (£)":
                if(header.equals("Tax ($)") && LOCALE.equals("en-GB")) {
                    header = header.replace("Tax ($)", "Vat (£)");
                }
                assertSearchInvoicesAwaitingApproval(localize(header));
                break;
            }
        }
    }

    public void assertSearchInvoiceRefColumn(String header) throws Exception {
        Grid grid;
        grid = runtimeState.invoicesAwaitingApprovalPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<String> column = grid.getColumnText(header);
        String random = column.get(RandomUtils.nextInt(0, column.size() - 1));
        runtimeState.invoicesAwaitingApprovalPage.searchInvoices(random);
        outputHelper.takeScreenshots();
        grid = runtimeState.invoicesAwaitingApprovalPage.getGrid();
        for (Row row : grid.getRows()) {
            assertTrue("Cell does not equal: " + random, row.getCell(header).getText().equals(random));
        }
        runtimeState.invoicesAwaitingApprovalPage.searchInvoices("");
    }

    public void assertSearchInvoiceRef(String header) throws Exception {
        Grid grid;
        grid = runtimeState.invoicesAwaitingFinalApprovalPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<String> column = grid.getColumnText(header);
        String random = column.get(RandomUtils.nextInt(0, column.size() - 1));
        runtimeState.invoicesAwaitingFinalApprovalPage.searchInvoices(random);
        outputHelper.takeScreenshots();
        grid = runtimeState.invoicesAwaitingFinalApprovalPage.getGrid();
        for (Row row : grid.getRows()) {
            assertTrue("Cell does not equal: " + random, row.getCell(header).getText().equals(random));
        }
        runtimeState.invoicesAwaitingFinalApprovalPage.searchInvoices("");
    }


    public void assertSearchInvoicesAwaitingApproval(String header) throws Exception {
        Grid grid;
        grid = runtimeState.invoicesAwaitingApprovalPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<String> column = grid.getColumnText(header);
        String random = column.get(RandomUtils.nextInt(0, column.size() - 1));
        runtimeState.invoicesAwaitingApprovalPage.searchInvoices(random);
        outputHelper.takeScreenshots();
        grid = runtimeState.invoicesAwaitingApprovalPage.getGrid();
        for (Row row : grid.getRows()) {
            Cell cell = row.getCell(header);
            assertNotNull("Unexpected empty cell when searching: " + header + " -> " + random, cell);

            if (header.equals("Net (£)")) {
                assertTrue("Cell does not equal: " + random, cell.getText().contains(random) || row.getCell("Vat (£)").getText().contains(random) || row.getCell("Gross (£)").getText().contains(random));
            } else if (header.equals("Vat (£)")) {
                assertTrue("Cell does not equal: " + random, cell.getText().contains(random) || row.getCell("Net (£)").getText().contains(random) || row.getCell("Gross (£)").getText().contains(random));
            } else
                assertTrue("Cell does not equal: " + random, cell.getText().contains(random));
        }
        runtimeState.invoicesAwaitingApprovalPage.searchInvoices("");
    }

    public void assertSearchInvoicesAwaitingFinalApproval(String header) throws Exception {
        Grid grid;
        grid = runtimeState.invoicesAwaitingFinalApprovalPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        List<String> column = grid.getColumnText(header);
        String random = column.get(RandomUtils.nextInt(0, column.size() - 1));
        runtimeState.invoicesAwaitingFinalApprovalPage.searchInvoices(random);
        outputHelper.takeScreenshots();
        grid = runtimeState.invoicesAwaitingFinalApprovalPage.getGrid();
        for (Row row : grid.getRows()) {

            if (header.equals("Net (£)")) {
                assertTrue("Cell does not equal: " + random, row.getCell(header).getText().contains(random) || row.getCell("Vat (£)").getText().contains(random) || row.getCell("Gross (£)").getText().contains(random));
            } else if (header.equals("Vat (£)")) {
                assertTrue("Cell does not equal: " + random, row.getCell(header).getText().contains(random) || row.getCell("Net (£)").getText().contains(random) || row.getCell("Gross (£)").getText().contains(random));
            } else
                assertTrue("Cell does not equal: " + random, row.getCell(header).getText().contains(random));
        }
        runtimeState.invoicesAwaitingFinalApprovalPage.searchInvoices("");
    }

    private void verifyGridSorting(Grid grid) throws Exception {
        String gridXpath = grid.getGridXpath();

        for (String header : grid.getHeaders()) {
            GridHelper.tableSort(gridXpath, header, RandomUtils.nextBoolean());

            outputHelper.takeScreenshots();

            grid = GridHelper.getGrid(gridXpath);

            boolean isAscending = GridHelper.isAscending(gridXpath, localize(header));
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + localize(header) + " column is sorted in " + order + " order");

            if (header.contains("Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else if (header.contains("$") || header.contains("£")) {
                GridHelper.assertSortedNumerics(grid, localize(header), isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" table can be sorted on all columns$")
    public void table_can_be_sorted_on_columns(String tableName) throws Throwable {
        Grid grid = GridHelper.getGridForTableName(tableName);
        verifyGridSorting(grid);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table can be sorted on all columns$")
    public void the_table_can_be_sorted_on_columns(String table) throws Throwable {
        switch (table) {
        case "Invoices Awaiting Approval":
            sortInvoicesAwaitingApproval();
            break;

        case "Invoices Awaiting Final Approval":
            sortInvoicesAwaitingFinalApproval();
            break;

        case "All Jobs":
            if (testData.getString("profileName").equals("Operations Director")) {
                runtimeState.allJobsPage_OpsDirector.searchJobs("");
                sortAllJobs_OpsDirector();
            } else {
                runtimeState.allJobsPage.searchJobs("");
                sortAllJobs();
            }
            break;

        case "Open Jobs":
            runtimeState.openAwaitingJobsPage.searchJobs("");   // ensure search filter has not been left on by previous (failing) step
            String[] headersAllJobs;
            if (getWebDriver().getCurrentUrl().contains("ukrb")) {
                headersAllJobs = new String[] { "Job reference", "Assignment status", "Site", "Asset subtype / classification", "Serial No", "Assigned Date", "Reference", "ETA" };
            } else {
                headersAllJobs = new String[] { "Job reference", "Status", "Asset subtype / classification", "Priority", "Days outstanding", "Reference", "ETA" };
            }
            sortOpenJobs(headersAllJobs);
            break;

        case "On Call Summary":
            // Get list of alias of resource profiles from the database
            List<Map<String, Object>> resourceProfiles = dbHelperResources.getResourceProfileUnderRFMWithAlias(testData.getInt("resourceId"));

            // Now build the expected headers for the grid
            String[] expectedHeadersOnCallSummary = new String[resourceProfiles.size() + 2];
            expectedHeadersOnCallSummary[0] = "From";
            expectedHeadersOnCallSummary[1] = "To";
            for (int j = 0; j < resourceProfiles.size(); j++) {
                expectedHeadersOnCallSummary[j + 2] = resourceProfiles.get(j).get("alias").toString().concat(" Schedules Complete");
            }

            String[] columnNames = expectedHeadersOnCallSummary;
            sortOnCallSummary(columnNames);
            break;

        case "Open Quote Requests":
            sortOpenQuoteRequests();
            break;

        case "Funding Requests Awaiting Review":
            sortFundingRequestsAwaitingReview();
            break;

        case "Funding Request Query":
            sortFundingRequestsQueries();
            break;

        case "Funding Requests Pending Query Response":
            sortFundingRequestsPendingQueryResponse();
            break;

        case "Funding Requests Awaiting Response Review":
            sortFundingRequestsAwaitingResponseReview();
            break;

        case "Multi Quotes Awaiting Bypass Review":
            sortFundingRequestsAwaitingBypassReview();
            break;

        case "Jobs Awaiting Initial Funding Request Approval":
            sortInitialFundingRequests();
            break;

        case "My Parts Orders Approval":
            sortPartsAwaitingApproval();
            break;

        case "Jobs Awaiting Resource Selection":
            sortJobsAwaitingResourceSelection();
            break;

        case "Jobs Awaiting Quote":
            sortJobsAwaitingQuote();
            break;

        case "Quotes Awaiting Review":
            sortQuotesAwaitingReviewPage();
            break;

        case "Quotes with Query Response":
            sortQuotesWithQueryResponse();
            break;

        case "Combined Feedback":
            sortFeedback();
            break;

        case "Jobs Awaiting Acceptance":
            String[] headersJobsAwaitingAcceptance;
            if(getWebDriver().getCurrentUrl().contains("ukrb")) {
                headersJobsAwaitingAcceptance = new String[] { "Job reference", "Assignment status", "Site", "Asset subtype/classification", "Serial No", "Days outstanding" };
            } else {
                headersJobsAwaitingAcceptance = new String[] { "Job Reference", "Status", "Asset subtype/classification", "Asset No", "Priority", "Days outstanding" };
            }
            sortJobsAwaitingAcceptance(headersJobsAwaitingAcceptance);
            break;

        case "Question List":
            sortQuestionList();
            break;

        case "Quotes With Funding Request Rejected":
            sortQuotesWithFundingRequestRejected();
            break;

        case "My Multi-Quotes - Awaiting Quote":
            sortMyMultiQuotesAwaitingQuote();
            break;

        case "My Multi-Quotes - Awaiting Review":
            sortMyMultiQuotesAwaitingReview();
            break;

        default:
            throw new Exception("Cannot find table on page");
        }
    }

    private void sortOnCallSummary(String[] columnNames) throws Exception {
        Grid grid;
        for (String header : columnNames) {
            scrollTo(0);
            runtimeState.onCallSummaryPage.tableSort(header, RandomUtils.nextBoolean());
            boolean isAscending = runtimeState.onCallSummaryPage.isAscending(header);
            outputHelper.takeScreenshots();
            grid = runtimeState.onCallSummaryPage.getGrid();
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.contains("Schedules Complete")) {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            } else {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);
            }
        }
    }

    private void sortAllJobs_OpsDirector() throws Exception {
        String[] headersAllJobs = { "Job reference", "Assignment status", "Site", "Asset subtype / classification", "Serial No", "Assigned Date", "Reference", "ETA" };
        for (String header : headersAllJobs) {
            scrollTo(0);
            runtimeState.allJobsPage_OpsDirector.tableSort(header, RandomUtils.nextBoolean());
            assertAllJobsOpsDirectorSortedColumn(header);
        }
    }

    private void sortAllJobs() throws Exception {
        String[] headersAllJobs = { "Job reference", "Assignment status", "Site", "Asset subtype / classification", "Serial No",  "Assigned Date", "Reference", "ETA" };
        for (String header : headersAllJobs) {
            scrollTo(0);
            runtimeState.allJobsPage.tableSort(header, RandomUtils.nextBoolean());
            assertAllJobsSortedColumn(header);
        }
    }

    private void assertAllJobsOpsDirectorSortedColumn(String header) throws Exception {
        boolean isAscending = runtimeState.allJobsPage_OpsDirector.isAscending(header);
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.allJobsPage_OpsDirector.getGrid();
        String order = isAscending ? "ascending" : "descending";
        runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

        switch (header) {
        case "Site":
            if (isNumeric(grid.getColumnText(header).get(0))) {
                GridHelper.assertSortedNumerics(grid, header, isAscending);
            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
            break;

        case "ETA":
        case "Assigned Date":
            GridHelper.assertSortedDates(grid, header, MEDIUM_DATE, isAscending);
            break;

        case "Job reference":
        case "Assignment status":
        case "Asset subtype / classification":
        case "Serial No":
        case "Reference":
        default:
            GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            break;
        }
    }

    private void assertAllJobsSortedColumn(String header) throws Exception {
        boolean isAscending = runtimeState.allJobsPage.isAscending(header);
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.allJobsPage.getGrid();
        String order = isAscending ? "ascending" : "descending";
        runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

        switch (header) {
        case "Site":
            if (isNumeric(grid.getColumnText(header).get(0))) {
                GridHelper.assertSortedNumerics(grid, header, isAscending);
            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
            break;

        case "ETA":
        case "Assigned Date":
            GridHelper.assertSortedDates(grid, header, MEDIUM_DATE, isAscending);
            break;

        case "Job reference":
        case "Assignment status":
        case "Asset subtype / classification":
        case "Serial No":
        case "Reference":
        default:
            GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            break;
        }
    }

    private void sortOpenJobs(String[] headersAllJobs) throws Exception {
        for (String header : headersAllJobs) {
            scrollTo(0);
            runtimeState.openAwaitingJobsPage.tableSort(header, RandomUtils.nextBoolean());
            assertOpenJobsSortedColumn(header);
        }
    }

    private void assertOpenJobsSortedColumn(String header) throws Exception {
        boolean isAscending = runtimeState.openAwaitingJobsPage.isAscending(header);
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid();
        String order = isAscending ? "ascending" : "descending";
        runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

        switch (header) {
        case "Site":
            if (isNumeric(grid.getColumnText(header).get(0))) {
                GridHelper.assertSortedNumerics(grid, header, isAscending);
            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
            break;

        case "ETA":
        case "Assigned Date":
            GridHelper.assertSortedDates(grid, header, MEDIUM_DATE, isAscending);
            break;

        case "Days outstanding":
            // just check its sorted - not worried if ascending or descending
            try {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            } catch (AssertionError e) {
                GridHelper.assertSortedAlphaNumerics(grid, header, !isAscending, String.CASE_INSENSITIVE_ORDER);
            }
            break;

        case "Job reference":
        case "Assignment status":
        case "Asset subtype / classification":
        case "Serial No":
        case "Asset No":
        case "Reference":
        default:
            GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            break;
        }
    }

    private void sortInvoicesAwaitingApproval() throws Exception {
        Grid grid;
        String[] headersInvoicesAwaitingApproval = { "Invoice Ref", "Invoice Date", "Order Ref", "Budget", "Supplier", "Site", "Net (" + localize("£") + ")", localize("Vat") + " (" + localize("£") + ")", "Gross (" + localize("£") + ")" };

        for (String header : headersInvoicesAwaitingApproval) {
            runtimeState.invoicesAwaitingApprovalPage.tableSort(localize(header), RandomUtils.nextBoolean());
            outputHelper.takeScreenshots();

            grid = runtimeState.invoicesAwaitingApprovalPage.getGrid();
            boolean isAscending = runtimeState.invoicesAwaitingApprovalPage.isAscending(localize(header));
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + localize(header) + " column is sorted in " + order + " order");

            if (header.equals("Invoice Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);
            } else if ( header.contains("Net (") || header.contains(localize("Vat") + " (") || header.contains("Gross (") ) {
                GridHelper.assertSortedNumerics(grid, localize(header), isAscending);
            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortInvoicesAwaitingFinalApproval() throws Exception {
        Grid grid;
        String[] headersInvoicesAwaitingFinalApproval = { "Invoice Ref", "Invoice Date", "Order Ref", "Budget", "Supplier", "Site", "Net (" + localize("£") + ")", localize("Vat") + " (" + localize("£") + ")", "Gross (" + localize("£") + ")" };

        for (String header : headersInvoicesAwaitingFinalApproval) {
            runtimeState.invoicesAwaitingFinalApprovalPage.tableSort(localize(header), RandomUtils.nextBoolean());
            outputHelper.takeScreenshots();

            grid = runtimeState.invoicesAwaitingFinalApprovalPage.getGrid();
            boolean isAscending = runtimeState.invoicesAwaitingFinalApprovalPage.isAscending(localize(header));
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + localize(header) + " column is sorted in " + order + " order");

            if (header.equals("Invoice Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);
            } else if ( header.contains("Net (") || header.contains(localize("Vat") + " (") || header.contains("Gross (") ) {
                GridHelper.assertSortedNumerics(grid, localize(header), isAscending);
            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortOpenQuoteRequests() throws Exception {
        Grid grid;
        String[] headersOpenQuoteRequests = { "Job Ref", "Site", "Location", "Subtype/Classification", "Logged", "Requested By", "Notes", "Action" };

        for (String header : headersOpenQuoteRequests) {

            runtimeState.openQuoteRequestsPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.openQuoteRequestsPage.getGrid();
            boolean isAscending = runtimeState.openQuoteRequestsPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortFundingRequestsAwaitingReview() throws Exception {
        Grid grid;
        String[] headersFundingRequestsAwaitingReview = { "Job Ref", "Site", "Division", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };

        for (String header : headersFundingRequestsAwaitingReview) {

            runtimeState.fundingRequestsAwaitingReviewPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.fundingRequestsAwaitingReviewPage.getGrid();
            boolean isAscending = runtimeState.fundingRequestsAwaitingReviewPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortFundingRequestsQueries() throws Exception {
        Grid grid;
        String[] headersFundingRequestsQueries = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "No. of Quotes" };

        for (String header : headersFundingRequestsQueries) {

            runtimeState.fundingRequestQueriesPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.fundingRequestQueriesPage.getGrid();
            boolean isAscending = runtimeState.fundingRequestQueriesPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortFundingRequestsPendingQueryResponse() throws Exception {
        Grid grid;
        String[] headersFundingRequestsPendingQueryResponse = { "Job Ref", "Site", "Division", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };

        for (String header : headersFundingRequestsPendingQueryResponse) {

            runtimeState.fundingRequestsPendingQueryResponsePage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.fundingRequestsPendingQueryResponsePage.getGrid();
            boolean isAscending = runtimeState.fundingRequestsPendingQueryResponsePage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortFundingRequestsAwaitingResponseReview() throws Exception {
        Grid grid;
        String[] headersFundingRequestsAwaitingResponseReview = { "Job Ref", "Site", "Division", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };

        for (String header : headersFundingRequestsAwaitingResponseReview) {

            runtimeState.fundingRequestsAwaitingResponseReviewPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.fundingRequestsAwaitingResponseReviewPage.getGrid();
            boolean isAscending = runtimeState.fundingRequestsAwaitingResponseReviewPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortFundingRequestsAwaitingBypassReview() throws Exception {
        Grid grid;
        String[] headersFundingRequestsAwaitingResponseReview = { "Job Ref", "Site", "Division", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "Bypass Reason" };

        for (String header : headersFundingRequestsAwaitingResponseReview) {

            runtimeState.multiQuoteAwaitingBypassReviewPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.multiQuoteAwaitingBypassReviewPage.getGrid();
            boolean isAscending = runtimeState.multiQuoteAwaitingBypassReviewPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortInitialFundingRequests() throws Exception {
        Grid grid;
        String[] headersInitialFundingRequests = { "Job Ref", "Site", "Resource", "Description", "Logged Date" };

        for (String header : headersInitialFundingRequests) {

            runtimeState.initialFundingRequestsPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.initialFundingRequestsPage.getGrid();
            boolean isAscending = runtimeState.initialFundingRequestsPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, MEDIUM, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortPartsAwaitingApproval() throws Exception {
        Grid grid;
        String[] headersPartsAwaitingApproval = { "PO Number", "Job/PPM Number", "Job Type", "Site", "Date Raised", "Required By", "Supplier", localize("Cost ($)") };

        for (String header : headersPartsAwaitingApproval) {

            runtimeState.partsRequestsAwaitingApprovalPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.partsRequestsAwaitingApprovalPage.getGrid();
            boolean isAscending = runtimeState.partsRequestsAwaitingApprovalPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Date Raised") || header.equals("Required By")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else if (header.equals(localize("Cost ($)"))) {
                GridHelper.assertSortedNumerics(grid, localize(header), isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortJobsAwaitingResourceSelection() throws Exception {
        Grid grid;
        String[] headersJobsAwaitingResourceSelection = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "Action" };

        for (String header : headersJobsAwaitingResourceSelection) {

            runtimeState.jobsAwaitingResourceSelectionPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.jobsAwaitingResourceSelectionPage.getGrid();
            boolean isAscending = runtimeState.jobsAwaitingResourceSelectionPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortJobsAwaitingQuote() throws Exception {
        Grid grid;
        String[] headersJobsAwaitingQuote = { "Job Ref", "Site", "Requested", "Scope of Works", "Location", "Subtype/Classification", "Resource", "Priority", "Due", "Status", "Action" };

        for (String header : headersJobsAwaitingQuote) {

            runtimeState.managerJobsAwaitingQuotePage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.managerJobsAwaitingQuotePage.getGrid();
            boolean isAscending = runtimeState.managerJobsAwaitingQuotePage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Requested") || header.equals("Due")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortQuotesAwaitingReviewPage() throws Exception {
        Grid grid;
        String[] headersQuoteAwaitingReview = { "Job Ref", "Site", "Requested", "Scope of Works", "Value ("+ CURRENCY_SYMBOL +")", "Location", "Subtype/Classification", "Resource", "Queried", "Priority" };

        for (String header : headersQuoteAwaitingReview) {

            runtimeState.quotesAwaitingReviewPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.quotesAwaitingReviewPage.getGrid();
            boolean isAscending = runtimeState.quotesAwaitingReviewPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Requested")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortQuotesWithQueryResponse() throws Exception {
        Grid grid;
        String[] headersQuotesWithQueryResponse = { "Job Ref", "Site", "Date Responded", "Scope of Works", "Value ("+ CURRENCY_SYMBOL +")", "Resource", "Query", "Query Reason", "Query Response" };

        for (String header : headersQuotesWithQueryResponse) {

            runtimeState.quotesWithQueryResponsePage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.quotesWithQueryResponsePage.getGrid();
            boolean isAscending = runtimeState.quotesWithQueryResponsePage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Date Responded")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else if (header.equals(localize("Value ("+ CURRENCY_SYMBOL +")"))) {
                GridHelper.assertSortedNumerics(grid, localize(header), isAscending);
            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortFeedback() throws Exception {
        Grid grid;
        String[] headersFeedback = { "Job Ref", "Site", "Store Rating", "Store Feedback", "Subtype / Classification", "Fixed Date" };

        for (String header : headersFeedback) {

            runtimeState.feedbackPage.tableSort(localize(header), RandomUtils.nextBoolean());
            grid = runtimeState.feedbackPage.getCombinedFeedbackGrid();
            boolean isAscending = runtimeState.feedbackPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Fixed Date")) {
                GridHelper.assertSortedDates(grid, header, MEDIUM, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortJobsAwaitingAcceptance(String[] headersJobsAwaitingAcceptance) throws Exception {
        for (String header : headersJobsAwaitingAcceptance) {
            runtimeState.openAwaitingJobsPage.tableSort(header, RandomUtils.nextBoolean());
            Boolean isAscending = runtimeState.openAwaitingJobsPage.isAscending(header);
            if (isAscending == null) {
                throw new Exception("Column not sorted: " + header);
            }

            Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid();
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Days outstanding")) {
                // this column is sorted in reverse
                isAscending = !isAscending;
            }
            GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
        }
    }

    private void sortQuestionList() throws Exception {
        //  Removing sort on Question and Active columns due to bugs MCP-13769 and MCP-20612
        //        String[] headersQuestionList = { "Name", "Question", "Template", "Active", "" };
        String[] headersQuestionList = { "Name", "Template", "" };
        Boolean isAscending;

        for (String header : headersQuestionList) {
            if(header.equalsIgnoreCase("")) {
                runtimeState.adminRulesEnginePage.tableSortBlank(header, RandomUtils.nextBoolean());
                isAscending = runtimeState.adminRulesEnginePage.isAscendingBlank();
            } else {
                runtimeState.adminRulesEnginePage.tableSort(header, RandomUtils.nextBoolean());
                isAscending = runtimeState.adminRulesEnginePage.isAscending(header);
            }

            if (isAscending == null) {
                throw new Exception("Column not sorted: " + header);
            }

            Grid grid = runtimeState.adminRulesEnginePage.getGrid();
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            outputHelper.takeScreenshots();
        }
    }

    private void sortQuotesWithFundingRequestRejected() throws Exception {
        String[] headersQuotesWithFundingRequestRejected = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };

        for (String header : headersQuotesWithFundingRequestRejected) {

            runtimeState.fundingRequestsRejectedPage.tableSort(localize(header), RandomUtils.nextBoolean());
            Grid grid = runtimeState.fundingRequestsRejectedPage.getGrid();
            boolean isAscending = runtimeState.fundingRequestsRejectedPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortMyMultiQuotesAwaitingQuote() throws Exception {
        String[] headersQuotesWithFundingRequestRejected = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };

        for (String header : headersQuotesWithFundingRequestRejected) {

            runtimeState.multiQuoteAwaitingQuotePage.tableSort(localize(header), RandomUtils.nextBoolean());
            Grid grid = runtimeState.multiQuoteAwaitingQuotePage.getGrid();
            boolean isAscending = runtimeState.multiQuoteAwaitingQuotePage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    private void sortMyMultiQuotesAwaitingReview() throws Exception {
        String[] headersQuotesWithFundingRequestRejected = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };

        for (String header : headersQuotesWithFundingRequestRejected) {

            runtimeState.multiQuoteAwaitingReviewPage.tableSort(localize(header), RandomUtils.nextBoolean());
            Grid grid = runtimeState.multiQuoteAwaitingReviewPage.getGrid();
            boolean isAscending = runtimeState.multiQuoteAwaitingReviewPage.isAscending(header);
            String order = isAscending ? "ascending" : "descending";
            runtimeState.scenario.write("Asserting " + header + " column is sorted in " + order + " order");

            if (header.equals("Logged Date")) {
                GridHelper.assertSortedDates(grid, header, SHORT_DATE, isAscending);

            } else {
                GridHelper.assertSortedAlphaNumerics(grid, header, isAscending, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the show entries dropdown is displayed on the \"([^\"]*)\" page$")
    public void the_show_entries_dropdown_is_displayed_on_the_page(String page) throws Throwable {
        String defaultValue = null;
        switch (page) {
        case "All Orders":
            assertTrue("The 'Show Entries' dropdown is not displayed!", runtimeState.allOrdersPage.isShowEntriesDropdownDisplayed());
            defaultValue = runtimeState.allOrdersPage.getSelectedShowEntriesDropdownValue();
            runtimeState.scenario.write("The 'Show entries' dropdown default value is: " + defaultValue);
            break;
        case "Submitted Invoices and Credits":
            assertTrue("The 'Show Entries' dropdown is not displayed!", runtimeState.submittedInvoicesAndCreditsPage.isShowEntriesDropdownDisplayed());
            defaultValue = runtimeState.submittedInvoicesAndCreditsPage.getSelectedShowEntriesDropdownValue();
            runtimeState.scenario.write("The 'Show entries' dropdown default value is: " + defaultValue);
            break;
        default:
            outputHelper.takeScreenshots();
            throw new Exception("Cannot find the dropdown on page");
        }
    }

    @And("^the grid page navigation buttons are displayed on the \"([^\"]*)\" page$")
    public void the_grid_page_navigation_buttons_are_displayed_on_the_page(String page) throws Throwable {
        switch (page) {
        case "All Orders":
            assertTrue("The First page navigation button is not displayed! ", runtimeState.allOrdersPage.isFirstPageButtonDisplayed());
            assertTrue("The Previous page navigation button is not displayed! ", runtimeState.allOrdersPage.isPreviousPageButtonDisplayed());
            assertTrue("The Next page navigation button is not displayed! ", runtimeState.allOrdersPage.isNextPageButtonDisplayed());
            assertTrue("The Last page navigation button is not displayed! ", runtimeState.allOrdersPage.isLastPageButtonDisplayed());
            runtimeState.scenario.write("All page navigation buttons are displayed on the grid ");
            outputHelper.takeScreenshots();
            break;

        case "Submitted Invoices and Credits":
            assertTrue("The First page navigation button is not displayed! ", runtimeState.submittedInvoicesAndCreditsPage.isFirstPageButtonDisplayed());
            assertTrue("The Previous page navigation button is not displayed! ", runtimeState.submittedInvoicesAndCreditsPage.isPreviousPageButtonDisplayed());
            assertTrue("The Next page navigation button is not displayed! ", runtimeState.submittedInvoicesAndCreditsPage.isNextPageButtonDisplayed());
            assertTrue("The Last page navigation button is not displayed! ", runtimeState.submittedInvoicesAndCreditsPage.isLastPageButtonDisplayed());
            runtimeState.scenario.write("All page navigation buttons are displayed on the grid ");
            outputHelper.takeScreenshots();
            break;

        default:
            outputHelper.takeScreenshots();
            throw new Exception("Cannot find page navigation buttons on the grid");
        }
    }

    @ContinueNextStepsOnException
    @Then("^the portal \"([^\"]*)\" page displays correctly$")
    public void the_portal_page_displays_correctly(String page) throws Throwable {
        runtimeState.portalSummaryPage = new PortalSummaryPage(getWebDriver()).get();
        assertTrue("The Home page is not loaded correctly", runtimeState.portalSummaryPage.isPageLoaded());
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table on the \"([^\"]*)\" page displays (?:correctly|expected headers)$")
    public void the_table_on_page_displays_correctly(String table, String page) throws Throwable {
        Grid grid;
        String[] expectedHeaders = null;
        switch (page) {
        case "Quotes Awaiting Review":
            grid = runtimeState.quotesAwaitingReviewPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersQuote = { "Job Ref", "Site", "Requested", "Scope of Works", "Value ("+ CURRENCY_SYMBOL +")", "Location", "Subtype/Classification", "Resource", "Queried", "Priority" };
            expectedHeaders = expectedHeadersQuote;
            break;
        case "Funding Requests Awaiting Review":
            grid = runtimeState.fundingRequestsAwaitingReviewPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersFunding = { "Job Ref", "Site", "Division", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };
            expectedHeaders = expectedHeadersFunding;
            break;
        case "Quotes in Query":
            grid = runtimeState.quotesInQueryPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersQuotesInQuery = { "Job Ref", "Site", "Date Queried", "Scope of Works", "Value ("+ CURRENCY_SYMBOL +")", "Resource", "Query", "Query Reason" };
            expectedHeaders = expectedHeadersQuotesInQuery;
            break;
        case "Parts Awaiting Approval":
            grid = runtimeState.partsRequestsAwaitingApprovalPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersPartsAwaitingApproval = { "PO Number", "Job/PPM Number", "Job Type", "Site", "Date Raised", "Required By", "Supplier", localize("Cost ($)") };
            expectedHeaders = expectedHeadersPartsAwaitingApproval;
            break;
        case "Parts Order Awaiting Approval":
            grid = runtimeState.partsOrderAwaitingApprovalPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersPartsOrderAwaitingApproval = { "Description", "Part Number", "Funding Route", "Order Qty", "Reduce To Qty", "Unit Price", "Cost", "Action" };
            expectedHeaders = expectedHeadersPartsOrderAwaitingApproval;
            break;
        case "Quotes with Query Pending":
            grid = runtimeState.quotesWithQueryPendingPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersQuoteswithQueryPending = { "Job Ref", "Site", "Quote Date", "Scope of Works", "Value ("+ CURRENCY_SYMBOL +")", "Quote Ref" };
            expectedHeaders = expectedHeadersQuoteswithQueryPending;
            break;
        case "Quotes with Query Response":
            grid = runtimeState.quotesWithQueryResponsePage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersQuoteswithQueryResponse = { "Job Ref", "Site", "Date Responded", "Scope of Works", "Value ("+ CURRENCY_SYMBOL +")", "Resource", "Query", "Query Reason", "Query Response" };
            expectedHeaders = expectedHeadersQuoteswithQueryResponse;
            break;
        case "Jobs Awaiting Resource Selection":
            grid = runtimeState.jobsAwaitingResourceSelectionPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersQuotesawaitingResourceSelection = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "Action" };
            expectedHeaders = expectedHeadersQuotesawaitingResourceSelection;
            break;
        case "Job Awaiting Resource Assignment":
            grid = runtimeState.quoteJobEdit.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersQuotesAwaitingResourceAssignment = { "Resource",
                    "Reason",
            "Note" };
            expectedHeaders = expectedHeadersQuotesAwaitingResourceAssignment;
            break;
        case "Resources with upcoming unavailability":
            grid = runtimeState.resourceAvailabilityPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            if ("My Resources".equals(table)) {
                String[] expectedHeadersMyResources = { "Name", "Reason", "Start Date", "End Date", "Jobs", "Covering Resource", "Show Contractors", "Action" };
                expectedHeaders = expectedHeadersMyResources;
            } else {
                String[] expectedHeadersResourceAvailability = { "Name", "Reason", "Start Date", "End Date", "Covering Resource", "Action" };
                expectedHeaders = expectedHeadersResourceAvailability;
            }
            break;
        case "On Call Summary":
            grid = runtimeState.onCallSummaryPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            // Get list of alias of resource profiles from the database
            List<Map<String, Object>> resourceProfiles = dbHelperResources.getResourceProfileUnderRFMWithAlias(testData.getInt("resourceId"));

            // Now build the expected headers for the grid
            String[] expectedHeadersOnCallSummary = new String[resourceProfiles.size() + 2];
            expectedHeadersOnCallSummary[0] = "From";
            expectedHeadersOnCallSummary[1] = "To";
            for (int j = 0; j < resourceProfiles.size(); j++) {
                expectedHeadersOnCallSummary[j + 2] = resourceProfiles.get(j).get("alias").toString().concat(" Schedules Complete");
            }

            expectedHeaders = expectedHeadersOnCallSummary;
            break;
        case "Jobs Awaiting Reallocation":
            grid = runtimeState.jobsAwaitingReallocationPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersMyJobs = { "Job Ref", "Site", "Resource", "Resource Profile", "Logged", "Subtype/Classification", "Description", "Priority", "Reallocate to Resource", "Show Contractors", "Action" };
            expectedHeaders = expectedHeadersMyJobs;
            break;
        case "Jobs Awaiting Quote":
            grid = runtimeState.managerJobsAwaitingQuotePage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersJobsAwaitingQuote = { "Job Ref", "Site", "Requested", "Scope of Works", "Location", "Subtype/Classification", "Resource", "Priority", "Due", "Status", "Action" };
            expectedHeaders = expectedHeadersJobsAwaitingQuote;
            break;
        case "Awaiting Quote":
            grid = runtimeState.multiQuoteAwaitingQuotePage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersAwaitingQuote = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes", "Action" };
            expectedHeaders = expectedHeadersAwaitingQuote;
            break;
        case "Awaiting Review":
            grid = runtimeState.multiQuoteAwaitingReviewPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersAwaitingReview = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes", "Action" };
            expectedHeaders = expectedHeadersAwaitingReview;
            break;
        case "Awaiting Resource Selection":
            grid = runtimeState.multiQuoteAwaitingResourceSelectionPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersAwaitingResourceSelection = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "Action" };
            expectedHeaders = expectedHeadersAwaitingResourceSelection;
            break;
        case "Awaiting Bypass Approval":
            grid = runtimeState.multiQuoteAwaitingBypassApprovalPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersAwaitingBypassApproval = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "Bypass Reason", "Action" };
            expectedHeaders = expectedHeadersAwaitingBypassApproval;
            break;
        case "All Invoices Awaiting Approval":
            grid = runtimeState.allInvoicesAwaitingApprovalPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedAllInvoicesAwaitingApproval = { "Inv Num", "Supplier", "Order Ref", "Site", "Date Submitted", "Inv Date", "Inv Net ("+ CURRENCY_SYMBOL +")", "Inv "+ localize("Vat") +" ("+ CURRENCY_SYMBOL +")", "Approver" };
            expectedHeaders = expectedAllInvoicesAwaitingApproval;
            break;
        case "Invoices & Credits Awaiting Review":
            grid = runtimeState.invoicesAndCreditsAwaitingReviewPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedInvoicesAndCreditNoteHeaders = { "Supplier Ref", "Type", "Order Ref", "Supplier", "Date", "Net (" + CURRENCY_SYMBOL + ")", localize("Vat") + " (" + CURRENCY_SYMBOL + ")", "Gross (" + CURRENCY_SYMBOL + ")", "Action" };
            expectedHeaders = expectedInvoicesAndCreditNoteHeaders;
            break;
        case "Submitted Invoices and Credits":
            grid = runtimeState.submittedInvoicesAndCreditsPage.getGrid();
            assertNotNull("Unexpected Null Grid", grid);
            if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/")) {
                if (dbHelperInvoices.getCountOfSubmittedCreditsWithinLast30DaysForSupplier(testData.getString("userName")) == 0) {
                    String[] expectedSubmittedInvoicesAndCredits = { "Type", "Ref Num", "Order Ref", "Date", "Net ("+ CURRENCY_SYMBOL +")", ""+ localize("Vat") +" ("+ CURRENCY_SYMBOL +")", "Gross ("+ CURRENCY_SYMBOL +")", "Status" };
                    expectedHeaders = expectedSubmittedInvoicesAndCredits;
                } else {
                    String[] expectedSubmittedInvoicesAndCredits = { "Type", "Ref Num", "Order Ref", "Date", "Net ("+ CURRENCY_SYMBOL +")", ""+ localize("Vat") +" ("+ CURRENCY_SYMBOL +")", "Gross ("+ CURRENCY_SYMBOL +")", "Status", "Has Credit" };
                    expectedHeaders = expectedSubmittedInvoicesAndCredits;
                }
            } else if (dbHelperInvoices.getCountOfSubmittedCreditsWithInLast30Days() == 0) {
                String[] expectedSubmittedInvoicesAndCredits = { "Type", "Supplier", "Ref Num", "Order Ref", "Job Ref", "Site", "Date", "Net ("+ CURRENCY_SYMBOL +")", ""+ localize("Vat") +" ("+ CURRENCY_SYMBOL +")", "Gross ("+ CURRENCY_SYMBOL +")", "Status" };
                expectedHeaders = expectedSubmittedInvoicesAndCredits;
            } else {
                String[] expectedSubmittedInvoicesAndCredits = { "Type", "Supplier", "Ref Num", "Order Ref", "Job Ref", "Site", "Date", "Net ("+ CURRENCY_SYMBOL +")", ""+ localize("Vat") +" ("+ CURRENCY_SYMBOL +")", "Gross ("+ CURRENCY_SYMBOL +")", "Status", "Has Credit" };
                expectedHeaders = expectedSubmittedInvoicesAndCredits;
            }
            break;

        case "All Orders":
            grid = runtimeState.allOrdersPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedAllOrdersHeaders = { "Order Ref", "Order Date", "Order Value ("+ CURRENCY_SYMBOL +")", "Action" };
            expectedHeaders = expectedAllOrdersHeaders;
            break;

        case "Home":
            runtimeState.portalNavBar.clickTopLevelMenu("Home");
            if (runtimeState.portalNavBar.isAlertVisible()) {
                runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
                outputHelper.takeScreenshots();
                runtimeState.popupAlert.ok();
            }
            outputHelper.takeScreenshots();

            runtimeState.portalSummaryPage = new PortalSummaryPage(getWebDriver()).get();
            grid = runtimeState.portalSummaryPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            if ("Summary".equals(table)) {
                String[] expectedHeadersHome = { "Type", "Count" };
                if (runtimeState.portalSummaryPage.isNoCountMessageDisplayed()) {
                    expectedHeadersHome = ArrayUtils.removeElement(expectedHeadersHome, "Count");
                }
                expectedHeaders = expectedHeadersHome;
            } else {
                throw new Exception("Unexpected table: " + table);
            }
            break;

        case "Feedback":
            grid = runtimeState.feedbackPage.getGridFirstRow(table);
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedFeedbackHeaders = { "Job Ref", "Site", "Store Rating", "Store Feedback", "Subtype / Classification", "Fixed Date" };
            expectedHeaders = expectedFeedbackHeaders;
            break;

        case "Feedback Response":
            grid = runtimeState.feedbackResponsePage.getJobTimeLineGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedJobTimelineHeaders = { "Event", "Time", "Resource", "Status", "Cancelled", "Note" };
            expectedHeaders = expectedJobTimelineHeaders;
            break;

        case "Funding Request Query":
            grid = runtimeState.fundingRequestQueriesPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedFundingRequestQueryHeaders = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "No. of Quotes" };
            expectedHeaders = expectedFundingRequestQueryHeaders;
            break;

        case "Jobs Awaiting Initial Funding Request Approval":
            grid = runtimeState.initialFundingRequestsPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedInitialFundingRequestsHeaders = { "Job Ref", "Site", "Resource", "Description", "Logged Date" };
            expectedHeaders = expectedInitialFundingRequestsHeaders;
            break;

        case "Funding Requests Awaiting Response Review":
            grid = runtimeState.fundingRequestsAwaitingResponseReviewPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedFundingRequestsAwaitingResponseReviewHeaders = { "Job Ref", "Site", "Division", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };
            expectedHeaders = expectedFundingRequestsAwaitingResponseReviewHeaders;
            break;

        case "Funding Requests Pending Query Response":
            grid = runtimeState.fundingRequestsPendingQueryResponsePage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedFundingRequestsPendingQueryResponseHeaders = { "Job Ref", "Site", "Division", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };
            expectedHeaders = expectedFundingRequestsPendingQueryResponseHeaders;
            break;

        case "Invoices Awaiting Approval":
            grid = runtimeState.invoicesAwaitingApprovalPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);
            String[] expectedInvoicesAwaitingApproval = { "Invoice Ref", "Invoice Date", "Order Ref", "Budget", "Supplier", "Site", localize("Net ($)"), localize("Vat ($)"), localize("Gross ($)") };
            expectedHeaders = expectedInvoicesAwaitingApproval;
            break;

        case "Invoices Awaiting Final Approval":
            grid = runtimeState.invoicesAwaitingFinalApprovalPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedInvoicesAwaitingFinalApproval = { "Invoice Ref", "Invoice Date", "Order Ref", "Initial Approver", "Budget", "Supplier", "Site", "Division", localize("Net ($)"), localize("Tax ($)"), localize("Gross ($)") };
            expectedHeaders = expectedInvoicesAwaitingFinalApproval;
            break;

        case "Multi Quotes Awaiting Bypass Review":
            grid = runtimeState.multiQuoteAwaitingBypassReviewPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedMultiQuotesAwaitingBypassReview = { "Job Ref", "Site", "Division", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "Bypass Reason" };
            expectedHeaders = expectedMultiQuotesAwaitingBypassReview;
            break;

        case "Open Quote Requests":
            grid = runtimeState.openQuoteRequestsPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedOpenQuoteRequests = { "Job Ref", "Site", "Location", "Subtype/Classification", "Logged", "Requested By", "Notes", "Action" };
            expectedHeaders = expectedOpenQuoteRequests;
            break;

        case "Quote Managers Decision":
            grid = runtimeState.quotesManagersDecisionPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedQuoteManagerdicisionHeaders = { "Quote Ref", "Quote Date", "Quote Value (" + CURRENCY_SYMBOL + ")", "Resource", "Queried", "Quote Document", "Supporting Documents", "Status"};
            expectedHeaders = expectedQuoteManagerdicisionHeaders;
            break;

        case "Funding Request Rejected":
            grid = runtimeState.fundingRequestsRejectedPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedQuotesWithFundingRequestRejectedHeaders = { "Job Ref", "Site", "Logged Date", "Scope of Works", "Location", "Subtype/Classification", "Priority", "No. of Quotes" };
            expectedHeaders = expectedQuotesWithFundingRequestRejectedHeaders;
            break;

        case "Jobs":
            grid = runtimeState.adminJobsPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedAdminJobsHeaders = { "Job Ref", "JobStatus" };
            expectedHeaders = expectedAdminJobsHeaders;
            break;

        case "On Call Scheduler Admin":
            grid = runtimeState.onCallSchedulerAdminPage.getGridFirstRow();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedOutOfHoursTimesHeaders = {"Week Day", "Start Time", "End Time (Next Day)" };
            expectedHeaders = expectedOutOfHoursTimesHeaders;
            break;

        case "Site Type":
            grid = runtimeState.adminEditSiteTypePage.getGrid();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedSiteTypesLocationsMappingHeaders = { "Icon", "Area", "Location", "Sub Location", "All", "Optional" };
            expectedHeaders = expectedSiteTypesLocationsMappingHeaders;
            break;

        default:
            outputHelper.takeScreenshots();
            throw new Exception("Cannot find table on page");
        }
        runtimeState.scenario.write("Expected headers: " + Arrays.toString(expectedHeaders));
        GridHelper.assertGridHeaders(grid.getHeaders(), expectedHeaders);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table displays expected headers$")
    public void the_table_displays_expected_headers(String table) throws Throwable {
        Grid grid;
        String[] expectedHeaders = null;
        switch (table) {
        case "Jobs for Site":
            grid = runtimeState.portalJobsForSitePage.getGrid();
            assertNotNull("Unexpected Null Grid", grid);

            String[] expectedHeadersQuote;
            if (getWebDriver().getCurrentUrl().contains("ukrb")) {
                expectedHeadersQuote = new String[] { "Job Type", "Job Reference", "Assignment Status", "Site", "Asset Subtype / Classification", "Serial No", "Fault Type", "Priority", "Description", "Assigned Date", "Days Outstanding", "Reference", "Action" };
            } else {
                expectedHeadersQuote = new String[] { "Job Type", "Job Reference", "Status", "Asset Subtype / Classification", "Fault Type", "Asset No Serial No", "Priority Response", "Priority Repair", "Reference", "Action" };
            }
            expectedHeaders = expectedHeadersQuote;
            break;

        default:
            outputHelper.takeScreenshots();
            throw new Exception("Cannot find table on page");
        }
        runtimeState.scenario.write("Expected headers: " + Arrays.toString(expectedHeaders));
        GridHelper.assertGridHeaders(grid.getHeaders(), expectedHeaders);
    }

    public void the_portal_user_views_on_page(String jobType) throws Throwable {
        switch (jobType) {

        case "Funding Requests Awaiting Bypass Review":
            runtimeState.multiQuoteAwaitingBypassReviewPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.fundingRequestsAwaitingApprovalPage = runtimeState.multiQuoteAwaitingBypassReviewPage.OpenJob(testData.getInt("jobReference").toString());
            break;
        case "Funding Request Awaiting Response Review":
            runtimeState.fundingRequestsAwaitingResponseReviewPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.fundingRequestsAwaitingApprovalPage = runtimeState.fundingRequestsAwaitingResponseReviewPage.openFundingRequestAwaitingResponseReview(testData.getInt("jobReference").toString());
            break;
        case "Funding Requests Awaiting Review":
            runtimeState.fundingRequestsAwaitingReviewPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.fundingRequestsAwaitingApprovalPage = runtimeState.fundingRequestsAwaitingReviewPage.OpenJob(testData.getInt("jobReference").toString());
            break;
        case "Funding Request Query":
            runtimeState.fundingRequestQueriesPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.fundingRequestsAwaitingApprovalPage = runtimeState.fundingRequestQueriesPage.openQuoteWithFundingRequestQuery(testData.getInt("jobReference").toString());
            break;
        case "Funding Request Rejected":
            runtimeState.fundingRequestsRejectedPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.fundingRequestsAwaitingApprovalPage = runtimeState.fundingRequestsRejectedPage.OpenJob(testData.getInt("jobReference").toString());
            break;

        case "Multi-Quote Awaiting Quote":
            runtimeState.multiQuoteAwaitingQuotePage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quoteJobEdit = runtimeState.multiQuoteAwaitingQuotePage.editQuoteJobRequest(testData.getInt("jobReference"));
            break;
        case "Multi-Quote Awaiting Resource":
            runtimeState.multiQuoteAwaitingResourceSelectionPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quoteJobEdit = runtimeState.multiQuoteAwaitingResourceSelectionPage.editQuoteJobRequest(testData.getInt("jobReference"));
            break;
        case "Multi-Quote Awaiting Review":
            runtimeState.multiQuoteAwaitingReviewPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quoteJobEdit = runtimeState.multiQuoteAwaitingReviewPage.editQuoteJobRequest(testData.getInt("jobReference"));
            break;

        case "Multi-Quote Bypass Approval":
            runtimeState.multiQuoteAwaitingBypassApprovalPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quoteJobEdit = runtimeState.multiQuoteAwaitingBypassApprovalPage.editQuoteJobRequest(testData.getInt("jobReference"));
            break;

        case "Multi-Quote Bypass Review":
            runtimeState.multiQuoteAwaitingBypassApprovalPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quoteJobEdit = runtimeState.multiQuoteAwaitingBypassApprovalPage.editQuoteJobRequest(testData.getInt("jobReference"));
            break;

        case "Parts Awaiting Approval":
            runtimeState.partsRequestsAwaitingApprovalPage.searchJobs(testData.getInt("jobReference").toString());
            outputHelper.takeScreenshot();
            runtimeState.partsOrderAwaitingApprovalPage = runtimeState.partsRequestsAwaitingApprovalPage.OpenJob(testData.getInt("jobReference").toString());
            testData.put("resourceId", testData.getInt("jobResourceId"));
            break;

        case "Invoice Awaiting Approval":
            if (!testData.tagExist("invoiceNumber")) {
                testData.put("invoiceNumber", dbHelperInvoices.getInvoiceNumberOrderReference(testData.getInt("jobReference"), jobType).get("InvoiceNumber"));
            }
            if (!testData.tagExist("orderRef")) {
                testData.put("orderRef", dbHelperInvoices.getInvoiceNumberOrderReference(testData.getInt("jobReference"), jobType).get("OrderReference"));
            }
            runtimeState.invoicesAwaitingApprovalPage.searchInvoices(testData.getString("orderRef"));
            runtimeState.scenario.write("Searching for Order Reference: " + testData.getString("orderRef"));
            outputHelper.takeScreenshots();
            runtimeState.invoiceApprovalPage = runtimeState.invoicesAwaitingApprovalPage.openInvoiceAwaitingApproval(testData.getString("orderRef"));
            String budgetType = runtimeState.invoiceApprovalPage.getInvoiceBudget();
            testData.put("budgetType", budgetType);
            break;
        case "Invoice Awaiting Final Approval":
        case "Supply Only Invoice Awaiting Final Approval":
        case "Non Supply Only Invoice Awaiting Final Approval":
            //Getting both invoice number and order reference.
            //Invoice number is used for assertions where are order reference is used to filter the grid and open the invoice as searching for invoice number with less digits is causing multiple rows to appear.
            testData.put("invoiceNumber", dbHelperInvoices.getInvoiceNumberOrderReference(testData.getInt("jobReference"), jobType).get("InvoiceNumber"));
            testData.put("orderRef", dbHelperInvoices.getInvoiceNumberOrderReference(testData.getInt("jobReference"), jobType).get("OrderReference"));
            runtimeState.invoicesAwaitingFinalApprovalPage.searchInvoices(testData.getString("orderRef"));
            runtimeState.scenario.write("Searching for Order Reference: " + testData.getString("orderRef"));
            outputHelper.takeScreenshots();
            runtimeState.invoiceFinalApprovalPage = runtimeState.invoicesAwaitingFinalApprovalPage.openInvoiceAwaitingFinalApproval(testData.getString("orderRef"));
            break;
        case "Initial Funding Request":
            runtimeState.initialFundingRequestsPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.initialFundingRequestsApprovalPage = runtimeState.initialFundingRequestsPage.openInitalFundingRequest(testData.getInt("jobReference").toString());
            break;

        case "Jobs Awaiting Quote":
            runtimeState.quoteJobEdit = runtimeState.managerJobsAwaitingQuotePage.editQuoteJobRequest(testData.getInt("jobReference"));
            break;
        case "Jobs Awaiting Resource Selection":
            runtimeState.jobsAwaitingResourceSelectionPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quoteJobEdit = runtimeState.jobsAwaitingResourceSelectionPage.editQuoteJobRequest(testData.getInt("jobReference"));
            break;

        case "Quote in Query":
            runtimeState.quotesInQueryPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quotesManagersDecisionPage = runtimeState.quotesInQueryPage.OpenJob(testData.getInt("jobReference").toString());
            break;
        case "Quote Managers Decision":
            runtimeState.quotesAwaitingReviewPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quotesManagersDecisionPage = runtimeState.quotesAwaitingReviewPage.OpenJob(testData.getInt("jobReference").toString());
            break;
        case "Quotes with Query Pending":
            runtimeState.quotesWithQueryPendingPage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quoteQueryPage = runtimeState.quotesWithQueryPendingPage.OpenJob(testData.getInt("jobReference").toString());
            break;
        case "Quotes with Query Response":
            runtimeState.quotesWithQueryResponsePage.searchJobs(testData.getInt("jobReference").toString());
            runtimeState.quotesManagersDecisionPage = runtimeState.quotesWithQueryResponsePage.OpenJob(testData.getInt("jobReference").toString());
            break;

        default:
            throw new Exception("Cannot find switch case for view " + jobType);
        }
    }

    @When("^the user \"([^\"]*)\" an? \"([^\"]*)\"$")
    public void the_portal_user_performs_action_on_page(String action, String jobType) throws Throwable {
        runtimeState.scenario.write("Action : " + action + " " + jobType + " is being performed on Job Reference: " + testData.getInt("jobReference"));
        switch (action) {
        case "Accepts":
            if ("Jobs Awaiting Quote".equalsIgnoreCase(jobType)) {
                runtimeState.jobsAwaitingQuotePage.searchJobs(testData.getInt("jobReference").toString());
                runtimeState.jobsAwaitingQuotePage.acceptQuoteJobRequest(testData.getInt("jobReference")).get();
            }
            break;

        case "Declines":
            if ("Jobs Awaiting Quote".equalsIgnoreCase(jobType)) {
                runtimeState.jobsAwaitingQuotePage.searchJobs(testData.getInt("jobReference").toString());
                runtimeState.declineInvitationToQuotePage = runtimeState.jobsAwaitingQuotePage.declineQuoteJobRequest(testData.getInt("jobReference")).get();
            }
            break;

        case "Creates":
            if ("Jobs Awaiting Quote".equalsIgnoreCase(jobType)) {
                runtimeState.createQuotePage = runtimeState.jobsAwaitingQuotePage.createQuoteJobRequest(testData.getInt("jobReference")).get();
            }
            break;

        case "Queries":
            if ("Funding Request Awaiting Response Review".equalsIgnoreCase(jobType) || "Funding Request Awaiting Review".equalsIgnoreCase(jobType)) {
                runtimeState.queryInitialApproverModal = runtimeState.fundingRequestsAwaitingApprovalPage.clickQueryButton();
                fundingRequestsAwaitingApprovalSteps.the_query_reason_is_selected();
                fundingRequestsAwaitingApprovalSteps.the_query_notes_are_entered();
                outputHelper.takeScreenshots();
                runtimeState.queryInitialApproverModal.save();
            }
            break;

        case "Registers":
            if ("Jobs Awaiting Quote".equalsIgnoreCase(jobType)) {
                runtimeState.jobsAwaitingQuotePage.searchJobs(testData.getInt("jobReference").toString());
                runtimeState.registerQuotePage = runtimeState.jobsAwaitingQuotePage.registerQuoteJobRequest(testData.getInt("jobReference")).get();
            }
            break;

        case "Reviews":
            if ("Multi-Quote Awaiting Review".equalsIgnoreCase(jobType)) {
                runtimeState.multiQuoteAwaitingReviewPage.searchJobs(testData.getInt("jobReference").toString());
                runtimeState.quotesManagersDecisionPage = runtimeState.multiQuoteAwaitingReviewPage.reviewQuoteJobRequest(testData.getInt("jobReference")).get();
            }
            break;

        case "Searches":
            if ("Job Awaiting Reallocation".equalsIgnoreCase(jobType)) {
                runtimeState.jobsAwaitingReallocationPage.searchJobs(testData.getInt("jobReference").toString());
                Grid grid = runtimeState.jobsAwaitingReallocationPage.getGrid();
                GridHelper.waitForRowCount(grid.getGridXpath(), 1);
            }
            break;

        case "Views":
            the_portal_user_views_on_page(jobType);
            break;

        default:
            throw new Exception("Cannot find switch case for " + action);
        }

        // Now we have the quote job opened lets grab it from the database
        QuoteApprovalScenarios quote = new QuoteApprovalScenarios();
        quote = quoteApprovalScenariosDao.getQuoteByFaultId(testData.getInt("jobReference"));
        if (quote != null) {
            quoteApprovalScenarios.copy(quote);
        }

        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^a search box is present on the \"([^\"]*)\" page$")
    public void a_search_box_is_present_on_the_page(String pageName) throws Throwable {
        switch (pageName) {
        case "All Invoices Awaiting Approval":
            assertTrue(
                    "Search Box is not present on the " + pageName
                    + " page. ",
                    runtimeState.allInvoicesAwaitingApprovalPage.isSearchBoxDisplayed());
            break;
        case "Invoices & Credits Awaiting Review":
            assertTrue(
                    "Search Box is not present on the " + pageName
                    + " page. ",
                    runtimeState.invoicesAndCreditsAwaitingReviewPage.isSearchBoxDisplayed());
            break;
        case "Submitted Invoices and Credits":
            assertTrue(
                    "Search Box is not present on the " + pageName
                    + " page. ",
                    runtimeState.submittedInvoicesAndCreditsPage.isSearchBoxDisplayed());
            break;

        case "All Orders":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.allOrdersPage.isSearchBoxDisplayed());
            break;

        case "On Call Summary":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.onCallSummaryPage.isSearchBoxDisplayed());
            break;
        case "Feedback":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.feedbackPage.isSearchBoxDisplayed());
            break;
        case "Funding Requests Pending Query Response":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.fundingRequestsPendingQueryResponsePage.isSearchBoxDisplayed());
            break;

        case "Open Quote Requests":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.openQuoteRequestsPage.isSearchBoxDisplayed());
            break;

        case "Funding Request Query":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.fundingRequestQueriesPage.isSearchJobDisplayed());
            break;

        case "Funding Requests Awaiting Review":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.fundingRequestsAwaitingReviewPage.isSearchBoxDisplayed());
            break;

        case "Funding Requests Awaiting Response Review":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.fundingRequestsAwaitingResponseReviewPage.isSearchJobDisplayed());
            break;

        case "Multi Quotes Awaiting Bypass Review":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.multiQuoteAwaitingBypassReviewPage.isSearchJobDisplayed());
            break;

        case "Jobs Awaiting Initial Funding Request Approval":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.initialFundingRequestsPage.isSearchJobDisplayed());
            break;

        case "Jobs Awaiting Resource Selection":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.jobsAwaitingResourceSelectionPage.isSearchBoxDisplayed());
            break;

        case "Jobs Awaiting Quote":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.managerJobsAwaitingQuotePage.isSearchBoxDisplayed());
            break;

        case "Quotes With Funding Request Rejected":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.fundingRequestsRejectedPage.isSearchBoxDisplayed());
            break;

        case "Jobs":
            assertTrue("Search Box is not present on the " + pageName + " page. ", runtimeState.adminJobsPage.isSearchBoxDisplayed());
            break;

        default:
            throw new Exception("Unexpected page " + pageName);
        }
        outputHelper.takeScreenshots();
    }

    public void verifyOnCallSummary() throws Exception {

        // get the org structure below the RFM / Supervisor
        List<Map<String, Object>> orgStructure = dbHelperOrganisation.getOrgStructureBelowResource(testData.getInt("resourceId"));

        Grid grid = runtimeState.onCallSummaryPage.getGrid();
        for (int r = 0; r < grid.getRows().size() && r < 5; r++) {  // restrict verification to first 5 rows (ie. 35 days * all the sites under the managers domain)
            Row row = grid.getRows().get(r);
            String from = row.getCell("From").getText();
            String to = row.getCell("To").getText();
            String complete = row.getCell(2).getText();

            String expectedTo = DateHelper.getDatePlusOffset(from, SHORT_DATE, 24 * 7, SHORT_DATE);
            assertEquals("Expected 'To' date incorrect for 'From': " + from, expectedTo, to);

            String expectedComplete = "Yes";
            for (int i = 0; i < 7; i++) {
                // for each of the next 7 days
                String startAt = DateHelper.getDatePlusOffset(from, SHORT_DATE, 24 * i, "yyyy-MM-dd 22:00");
                String endAt = DateHelper.getDatePlusOffset(from, SHORT_DATE, 24 * (i+1), "yyyy-MM-dd 06:00");

                for (Map<String, Object> org : orgStructure) {

                    String orgStructureType = (String) org.get("OrgStructureType");
                    if (orgStructureType.equals("Tech Position")) {
                        int orgStructureId = (Integer) org.get("OrganisationStructureId");

                        for (int siteId : dbHelperOrganisation.getSitesInOrganisationStructure(orgStructureId)) {
                            // for each site managed by the resourceId check if there is an on call resource on the given day
                            Integer onCallResourceId = dbHelperOrganisation.getOnCallResourceId(siteId, startAt, endAt); // get the On Call resource
                            if (onCallResourceId == null) {
                                expectedComplete = "No";        // no On Call resource configured
                                break;
                            }
                        }
                    }
                }
            }
            assertEquals("Expected 'Schedules Complete' incorrect for 'From': " + from + "\n" + testData.getString("sql"), expectedComplete, complete);
        }
    }

    public void updateToStoreTime(List<Map<String, Object>> dbData, String fieldName, String toFormat) throws ParseException {
        for (Map<String, Object> data : dbData) {
            String value = data.get(fieldName).toString();
            value = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), value, DB_DATE_FORMAT);
            value = DateHelper.convert(value, DB_DATE_FORMAT, toFormat);
            data.put(fieldName, value);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table on the \"([^\"]*)\" (?:page|tab) displays (?:each|the latest job) row correctly$")
    public void the_table_on_page_displays_each_row_correctly(String table, String page) throws Throwable {
        // Pull back data from page and database
        List<Map<String, Object>> dbData = null;
        Grid grid;
        String compareField = null;
        switch (page) {
        case "Awaiting Bypass Approval":
            grid = runtimeState.multiQuoteAwaitingBypassApprovalPage.getGrid();
            dbData = dbHelperQuotes.getMultiQuotesAwaitingBypassReviewForRFM(testData.getInt("resourceId"));
            updateToStoreTime(dbData, "Logged Date", SHORT_DATE);
            dbData = normalize(dbData);
            compareField = "Job Ref";
            break;

        case "Invoices & Credits Awaiting Review":
            dbData = dbHelperInvoices.getInvoicesAndCreditNotesAwaitingReviewData(MS_SHORT_DATE);
            runtimeState.invoicesAndCreditsAwaitingReviewPage.searchInvoices(dbData.get(0).get("OrderRef").toString());
            grid = runtimeState.invoicesAndCreditsAwaitingReviewPage.getGrid();
            assertDataInInvoicesGrid(page, grid, dbData);
            return;

        case "Awaiting Quote":
            grid = runtimeState.multiQuoteAwaitingQuotePage.getGrid();
            dbData = dbHelperQuotes.getMultiQuotesAwaitingQuoteForRFM(testData.getInt("resourceId"));
            dbData = normalize(dbData);
            compareField = "Job Ref";
            break;

        case "All Invoices Awaiting Approval":
            dbData = dbHelperInvoices.getAllInvoicesAwaitingApprovalData(MS_SHORT_DATE, LOCALE);
            runtimeState.allInvoicesAwaitingApprovalPage.searchJobs(dbData.get(0).get("OrderRef").toString());
            grid = runtimeState.allInvoicesAwaitingApprovalPage.getGrid();
            assertDataInInvoicesGrid(page, grid, dbData);
            return;

        case "Awaiting Resource Selection":
            grid = runtimeState.multiQuoteAwaitingResourceSelectionPage.getGrid();
            dbData = dbHelperQuotes.getMultiQuotesAwaitingResourceForRFM(testData.getInt("resourceId"));
            updateToStoreTime(dbData, "Logged Date", SHORT_DATE);
            dbData = normalize(dbData);
            compareField = "Job Ref";
            break;

        case "Awaiting Review":
            grid = runtimeState.multiQuoteAwaitingReviewPage.getGrid();
            dbData = dbHelperQuotes.getMultiQuotesAwaitingReviewForRFM(testData.getInt("resourceId"));
            compareField = "Job Ref";
            break;

        case "Funding Requests Awaiting Response Review":
            dbData = dbHelperQuotes.getFundingRequestsDataInPage(testData.getString("profileName"), page);
            updateToStoreTime(dbData, "Logged Date", SHORT_DATE);
            runtimeState.fundingRequestsAwaitingResponseReviewPage.searchJobs(dbData.get(0).get("Job Ref").toString());
            grid = runtimeState.fundingRequestsAwaitingResponseReviewPage.getGrid();
            compareField = "Job Ref";
            break;

        case "Funding Requests Awaiting Review":
            dbData = dbHelperQuotes.getFundingRequestsDataInPage(testData.getString("profileName"), page);
            updateToStoreTime(dbData, "Logged Date", SHORT_DATE);
            runtimeState.fundingRequestsAwaitingReviewPage.searchJobs(dbData.get(0).get("Job Ref").toString());
            grid = runtimeState.fundingRequestsAwaitingReviewPage.getGrid();
            compareField = "Job Ref";
            break;

        case "Funding Requests Pending Query Response":
            dbData = dbHelperQuotes.getFundingRequestsDataInPage(testData.getString("profileName"), page);
            runtimeState.fundingRequestsPendingQueryResponsePage.searchJobs(dbData.get(0).get("Job Ref").toString());
            grid = runtimeState.fundingRequestsPendingQueryResponsePage.getGrid();
            compareField = "Job Ref";
            fundingRequestsAwaitingApprovalSteps.assertDataInFundingRequestsGrid(page, grid, dbData, compareField);
            return;

        case "Funding Request Rejected":
            grid = runtimeState.fundingRequestsAwaitingApprovalPage.getResourceQuotesGrid();
            dbData = dbHelperQuotes.getJobQuotesForFundingRequestRejected(testData.getInt("jobReference"));
            compareField = "Resource";
            fundingRequestsAwaitingApprovalSteps.assertDataInFundingRequestsGrid(page, grid, dbData, compareField);
            return;

        case "Funding Request Query Response Required":
        case "Senior Manager Funding Request Decision":
            grid = runtimeState.fundingRequestsAwaitingApprovalPage.getResourceQuotesGrid();
            dbData = dbHelperQuotes.getJobQuotesInPage(testData.getInt("jobReference"), page, testData.getString("profileName"));
            compareField = "Resource";
            fundingRequestsAwaitingApprovalSteps.assertDataInFundingRequestsGrid(page, grid, dbData, compareField);
            return;

        case "Initial Funding Requests":
            dbData = dbHelperFundingRequests.getLatestInitialFundingRequestJob(testData.getString("userName"));
            updateToStoreTime(dbData, "Logged Date", MEDIUM);
            runtimeState.initialFundingRequestsPage.searchJobs(dbData.get(0).get("Job Ref").toString());
            grid = runtimeState.initialFundingRequestsPage.getGrid();
            compareField = "Job Ref";
            break;

        case "Invoices Awaiting Final Approval":
            dbData = dbHelperInvoices.getInvoicesAwaitingFinalApproval();
            runtimeState.invoicesAwaitingFinalApprovalPage.searchInvoices(dbData.get(0).get("Order Ref").toString());
            grid = runtimeState.invoicesAwaitingFinalApprovalPage.getGrid();
            assertDataInInvoicesGrid(page, grid, dbData);
            return;

        case "On Call Summary":
            verifyOnCallSummary();
            return;

        case "All Orders":
            dbData = dbHelperInvoices.getRandomOrderFromAllOrdersForSupplier(testData.getString("userName"), MS_SHORT_DATE, LOCALE);
            runtimeState.scenario.write("Searching for Order Reference: " + dbData.get(0).get("OrderRef").toString());
            runtimeState.allOrdersPage.search(dbData.get(0).get("OrderRef").toString());
            grid = runtimeState.allOrdersPage.getGrid();
            if (grid == null) {
                runtimeState.scenario.write("The Order Reference " + dbData.get(0).get("OrderRef").toString() + " was not found on the grid table ");
            }
            assertDataInInvoicesGrid(page, grid, dbData);
            return;

        case "Submitted Invoices and Credits":
            if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/")) {
                dbData = dbHelperInvoices.getSubmittedInvoicesForSupplyOnlyUser(testData.getString("userName"), MS_SHORT_DATE, LOCALE);
            } else {
                dbData = dbHelperInvoices.getRandomSupplierAndOrderRefFromSubmittedInvoicesAndCredits();
            }
            runtimeState.scenario.write("Searching for Order Reference: " + dbData.get(0).get("OrderRef").toString());
            runtimeState.submittedInvoicesAndCreditsPage.searchJobs(dbData.get(0).get("OrderRef").toString());
            grid = runtimeState.submittedInvoicesAndCreditsPage.getGrid();
            if (grid == null) {
                runtimeState.scenario.write("The Order Reference " + dbData.get(0).get("OrderRef").toString() + " was not found on the grid table ");
            }
            runtimeState.scenario.write("Invoice details: " + dbData.toString());
            assertDataInInvoicesGrid(page, grid, dbData);
            return;

        case "Job Status Admin":
            dbData = dbHelperJobs.getJobReferencesAndStatus(testData.getInt("jobReferenceForSearch"));
            grid = runtimeState.adminJobsPage.getGrid();
            compareField = "Job Ref";
            break;

        case "On Call Scheduler Admin":
            dbData = dbHelperOnCallScheduler.getOutOfHoursTimes();
            grid = runtimeState.onCallSchedulerAdminPage.getGrid();
            adminJobsSteps.assertDataInOnCallSchedulerAdminGrid(grid, dbData);
            return;

        case "Sites":
            dbData = testData.getListMap("dbData");
            grid = runtimeState.adminSitesPage.getGrid();
            adminSitesSteps.assertDataInSitesGrid(grid, dbData, page);
            return;

        case "Site Types":
            runtimeState.adminSiteTypesPage = new AdminSiteTypesPage(getWebDriver()).get();
            dbData = dbHelperSites.getSiteTypes();
            grid = runtimeState.adminSiteTypesPage.getGrid();
            adminSitesSteps.assertDataInSitesGrid(grid, dbData, page);
            return;

        case "Site Type":
            grid = runtimeState.adminSiteTypesPage.getGridFirstRow();
            adminSitesSteps.assertDataInSitesGrid(grid, page);
            return;

        default:
            throw new Exception("Cannot find " + page);
        }

        comparisonHelper.sortAndAssertDataInGridAndDatabase(grid, dbData, compareField);
    }

    public void assertMyMultiQuotes(Grid grid, Map<String, Object> dbData) throws ParseException {

        Row row = grid.getRows().get(0);

        runtimeState.scenario.write("Asserting Job Ref is: " + String.valueOf(dbData.get("FaultId")));
        assertEquals(String.valueOf(dbData.get("FaultId")), row.getCell("Job Ref").getText());

        runtimeState.scenario.write("Asserting Site is: " + dbData.get("Store"));
        assertEquals(dbData.get("Store"), row.getCell("Site").getText());

        String loggedDate = dbData.get("LoggedDate").toString();
        runtimeState.scenario.write("Logged Date: " + loggedDate);
        loggedDate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), loggedDate, DB_DATE_FORMAT);
        runtimeState.scenario.write("Logged Date (Store Time): " + loggedDate);
        String loggedDateToAssert = DateHelper.convert(loggedDate, DB_DATE_FORMAT, SHORT_DATE);
        assertEquals(loggedDateToAssert, row.getCell("Logged Date").getText());

        String scopeOfWorks = normalize((String) dbData.get("ScopeOfWorks"));
        runtimeState.scenario.write("Asserting Scope of Works is: " + scopeOfWorks);
        assertEquals(scopeOfWorks, row.getCell("Scope of Works").getText());

        runtimeState.scenario.write("Asserting Location is: " + dbData.get("Location"));
        assertEquals(dbData.get("Location"), row.getCell("Location").getText());

        runtimeState.scenario.write("Asserting Subtype/Classification is: " + dbData.get("SubTypeClassification"));
        assertEquals(dbData.get("SubTypeClassification"), row.getCell("Subtype/Classification").getText());

        runtimeState.scenario.write("Asserting Priority is: " + dbData.get("QuotePriority"));
        assertEquals(dbData.get("QuotePriority"), row.getCell("Priority").getText());

        String noOfQuotes = String.valueOf(dbData.get("NumQuotes")) + " of " + String.valueOf(dbData.get("NumberOfQuotesRequired"));
        runtimeState.scenario.write("Asserting No. of Quotes is: " + noOfQuotes);
        assertEquals(noOfQuotes, row.getCell("No. of Quotes").getText());

        runtimeState.scenario.write("Asserting Action is: " + dbData.get("Action"));
        assertEquals(dbData.get("Action"), row.getCell("Action").getText());
    }

    private void assertAwaitingQuote(int jobReference) throws ParseException {
        runtimeState.multiQuoteAwaitingQuotePage.searchJobs(String.valueOf(jobReference));

        Grid grid = runtimeState.multiQuoteAwaitingQuotePage.getGrid();
        Map<String, Object> dbData = dbHelperQuotes.getMultiQuotesAwaitingQuoteForJobReference(testData.getInt("jobReference"));

        assertMyMultiQuotes(grid, dbData);
    }

    private void assertAwaitingReview(int jobReference) throws ParseException {
        runtimeState.multiQuoteAwaitingReviewPage.searchJobs(String.valueOf(jobReference));

        Grid grid = runtimeState.multiQuoteAwaitingReviewPage.getGrid();
        Map<String, Object> dbData = dbHelperQuotes.getMultiQuotesAwaitingReviewForJobReference(testData.getInt("jobReference"));

        assertMyMultiQuotes(grid, dbData);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table on the \"([^\"]*)\" (?:page|tab) displays expected job details$")
    public void the_table_on_page_displays_expected_job_details(String table, String page) throws Throwable {
        switch (page) {
        case "Awaiting Quote":
            assertAwaitingQuote(testData.getInt("jobReference"));
            break;

        case "Awaiting Review":
            assertAwaitingReview(testData.getInt("jobReference"));
            break;

        default:
            throw new Exception("Cannot find " + page);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table on the \"([^\"]*)\" page displays each row correctly without dates$")
    public void the_table_on_page_displays_each_row_correctly_without_dates(String table, String page) throws Throwable {
        testData.put("skipCheckDates", true);
        the_table_on_page_displays_each_row_correctly(table, page);
    }

    @And("^the \"([^\"]*)\" table has data$")
    public void the_table_has_data(String table) throws Throwable {
        switch (table) {
        case "All Invoices Awaiting Approval":
            if (dbHelperInvoices.getCountOfAllInvoicesInStatus("Awaiting Approval") == 0) {
                throw new PendingException("There are no invoices awaiting approval");
            }
            break;
        case "Invoices & Credits Awaiting Review":
            if (dbHelperInvoices.getCountOfInvoicesAndCreditsInStatus("Awaiting AP Review", "Awaiting approval") == 0) {
                throw new PendingException("There are no invoices or credit notes for awaiting approval");
            }
            break;
        case "Submitted Invoices and Credits":
            if (dbHelperInvoices.getCountOfSubmittedInvoicesAndCreditsWithInLast30Days() == 0) {
                throw new PendingException("There are no submitted invoices and credits");
            }
            break;
        default:
            throw new Exception("Incorrect table: " + table);
        }
    }

    @And("^a search is run on \"([^\"]*)\" table for \"([^\"]*)\"$")
    public void a_search_is_run_on_table_for(String table, String columnName) throws Throwable {
        String orderRef;

        switch (table) {
        case "All Orders":
            orderRef = dbHelperInvoices.getRandomOrderRefFromAllOrdersForSupplier(testData.getString("userName")).toString();
            testData.put("orderRef", orderRef);
            runtimeState.allOrdersPage.search(orderRef);
            runtimeState.scenario.write("Searching for Order Reference: " + orderRef);
            break;
        case "Submitted Invoices and Credits":
            if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/")) {
                orderRef = dbHelperInvoices.getRandomOrderRefForSupplier(testData.getString("userName")).toString();
                testData.put("orderRef", orderRef);
                runtimeState.submittedInvoicesAndCreditsPage.search(orderRef);
                runtimeState.scenario.write("Searching for Order Reference: " + orderRef);
            } else if ("OrderRef".equalsIgnoreCase(columnName)) {
                List<Map<String, Object>> dbData = dbHelperInvoices.getRandomSupplierAndOrderRefFromSubmittedInvoicesAndCredits();
                testData.put("supplierName", dbData.get(0).get("Supplier").toString());
                testData.put("orderRef", dbData.get(0).get("OrderRef").toString());
                runtimeState.submittedInvoicesAndCreditsPage.search(dbData.get(0).get("OrderRef").toString());
                runtimeState.scenario.write("Searching for Order Ref: " + dbData.get(0).get("OrderRef").toString());
            }
            break;
        default:
            throw new Exception("Incorrect table " + table);
        }
        outputHelper.takeScreenshots();
    }

    @And("^\"([^\"]*)\" button is displayed on the \"([^\"]*)\" page$")
    public void button_is_displayed_on_the_page(String buttonName, String pageName) throws Throwable {
        switch (pageName) {
        case "Feedback":
            assertTrue(buttonName + " button is not present on the " + pageName + " page. ", runtimeState.feedbackPage.isSplitRFMsButtonDisplayed());
            break;
        default:
            throw new Exception("Unexpected page " + pageName);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" button in the \"([^\"]*)\" (?:page|modal) is disabled$")
    public void the_button_in_the_page_is_disabled(String buttonName, String pageName) throws Throwable {
        runtimeState.scenario.write("Asserting " + buttonName + " button is disabled in the " + pageName + " page");
        switch (pageName) {
        case "Reply to Store":
            assertFalse(buttonName + " is enabled in the " + pageName + " page. ", runtimeState.replyToStoreModal.isSendButtonEnabled());
            break;
        case "Spoke to Store":
            assertFalse(buttonName + " is enabled in the " + pageName + " page. ", runtimeState.spokeToStoreModal.isSendButtonEnabled());
            break;
        case "Feedback Response":
            assertFalse(buttonName + " is enabled in the " + pageName + " page. ", runtimeState.feedbackResponsePage.isButtonEnabled(buttonName));
            break;
        case "Funding Request Query Response":
            assertFalse(buttonName + " is enabled in the " + pageName + " page. ", runtimeState.fundingRequestsAwaitingApprovalPage.isSendResponseButtonEnabled());
            break;
        case "Jobs Awaiting Reallocation":
            assertFalse(buttonName + " is enabled in the " + pageName + " page. ", runtimeState.jobsAwaitingReallocationPage.isReAllocateButtonEnabled());
            break;
        default:
            throw new Exception("Unexpected page " + pageName);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the ((?:job|quote)) will not appear on the \"([^\"]*)\" screen$")
    public void the_something_will_not_appear_on_the_something_screen(String jobName, String pageName) throws Throwable {
        switch (pageName) {
        case "Quotes with Funding Request Queries":
            // If no search then job isn't displayed
            if (runtimeState.fundingRequestQueriesPage.isSearchJobDisplayed()) {
                runtimeState.fundingRequestQueriesPage.searchJobs(String.valueOf(testData.getInt("jobReference")));
                assertFalse("Unexpected Quote", runtimeState.fundingRequestQueriesPage.isQuoteDisplayed(String.valueOf(testData.getInt("jobReference"))));
            }
            break;

        case "Jobs Awaiting Reallocation In Your Region":
            if (runtimeState.jobsAwaitingReallocationPage.isSearchJobDisplayed()) {
                runtimeState.jobsAwaitingReallocationPage.searchJobs(String.valueOf(testData.getInt("jobReference")));
                assertFalse("Unexpected Job", runtimeState.jobsAwaitingReallocationPage.isJobDisplayed(String.valueOf(testData.getInt("jobReference"))));
            }
            break;

        case "Funding Request Awaiting Response Review":
            runtimeState.fundingRequestsAwaitingResponseReviewPage = new FundingRequestsAwaitingResponseReviewPage(getWebDriver()).get();
            if (runtimeState.fundingRequestsAwaitingResponseReviewPage.isSearchJobDisplayed()) {
                runtimeState.fundingRequestsAwaitingResponseReviewPage.searchJobs(String.valueOf(testData.getInt("jobReference")));
                assertFalse("Unexpected Job", runtimeState.fundingRequestsAwaitingResponseReviewPage.isJobDisplayed(String.valueOf(testData.getInt("jobReference"))));
            }
            break;

        case "Parts Requests Awaiting Approval":
            if (runtimeState.partsRequestsAwaitingApprovalPage.isSearchJobDisplayed()) {
                runtimeState.partsRequestsAwaitingApprovalPage.searchJobs(String.valueOf(testData.getInt("jobReference")));
                if ( runtimeState.partsRequestsAwaitingApprovalPage.isJobDisplayed(String.valueOf(testData.getInt("jobReference"))) ) {
                    if ( testData.getString("totalCost") != null ) {
                        Grid grid = runtimeState.partsRequestsAwaitingApprovalPage.getGrid();
                        for (Row row : grid.getRows()) {
                            String totalCost = testData.getString("totalCost");
                            assertFalse("Unexpected Job displayed: " + testData.getInt("jobReference") + ". Cost: " + totalCost, row.getCell(localize("Cost ($)")).equals(totalCost));
                        }
                    } else {
                        fail("Unexpected Job displayed: " + testData.getInt("jobReference"));
                    }
                }
            }
            break;

        case "Quotes With Funding Request Rejected":
            // If no search then job isn't displayed
            if (runtimeState.fundingRequestsRejectedPage.isSearchBoxDisplayed()) {
                runtimeState.fundingRequestsRejectedPage.searchJobs(testData.getInt("jobReference").toString());
                assertFalse("Unexpected Quote", runtimeState.fundingRequestsRejectedPage.isQuoteDisplayed(String.valueOf(testData.getInt("jobReference"))));
            }
            break;

        default:
            throw new Exception("Unexpected page " + pageName);
        }
        runtimeState.scenario.write("Job Reference: " + testData.getInt("jobReference") + " is not displayed on the grid. ");
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" table excludes jobs that are 'Deferred', 'Declined', 'Removed' 'Cancelled' and 'Fixed'$")
    public void the_grid_excludes_jobs_that_are_Deferred_Declined_Removed_Cancelled_and_Fixed(String tableName) throws Throwable {
        switch (tableName) {
        case "Open Jobs":
            List<Map<String, Object>> dbData = dbHelperJobs.getClosedJobs(testData.getInt("resourceId"));
            runtimeState.scenario.write("Resource has " + dbData.size() + " closed jobs");
            runtimeState.openAwaitingJobsPage.searchJobs("");              // remove any filters
            for (String row : GridHelper.getRowsAsString()) {
                String displayedJobReference = row.split("\n")[0].trim();
                for (Map<String, Object> closedJob : dbData) {
                    String jobReference = closedJob.get("JobReference").toString();
                    assertFalse("Unexpected jobReference " + jobReference + " found on row: " + row, displayedJobReference.equals(jobReference));
                }
            }
            break;

        default:
            throw new Exception("Unexpected table: " + tableName);
        }
    }

    @ContinueNextStepsOnException
    @Then("^clicking on a job on the \"([^\"]*)\" table will take the user to the 'Job Details' screen$")
    public void clicking_on_a_job_will_take_the_user_to_the_Job_Details_screen(String tableName) throws Throwable {
        switch (tableName) {
        case "Open Jobs":
            Grid grid = runtimeState.openAwaitingJobsPage.getJobsGrid(1);
            Row row = grid.getRows().get(0);
            String cssSelector = row.getCell(0).getCssSelector();
            getWebDriver().findElement(By.cssSelector(cssSelector)).click();
            POHelper.waitForAngularRequestsToFinish();

            runtimeState.asbestosModal = new AsbestosModal(getWebDriver());
            //Only execute if the modal is displayed - This is to prevent a number of tests being blocked by 1 bug
            if ( runtimeState.asbestosModal.isPageLoaded()) {
                String answer = "Yes";
                runtimeState.scenario.write("Confirming 'Asbestos register has been checked': " + answer);
                runtimeState.asbestosModal.clickAnswer(answer);
                outputHelper.takeScreenshots();
                runtimeState.asbestosModal.clickOk();
            }

            runtimeState.jobDetailsPage = new JobDetailsPage(getWebDriver()).get(); // this is the assertion - will throw exception if page not displayed!
            break;

        default:
            throw new Exception("Unexpected table: " + tableName);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the click functionality from the screen (?:is|is not) available on \"([^\"]*)\"$")
    public void the_click_functionality_from_the_screen(String pageName) throws Throwable {

        runtimeState.scenario.write("Asserting the click functionality on " + pageName + " page");

        switch (pageName) {
        case "Open Quote Requests":
            assertTrue("Click functionality is available on the page" + pageName + " page. ", runtimeState.openQuoteRequestsPage.IsclickFunctinalityAvailable(testData.getInt("jobReference")));
            break;

        case "Jobs Awaiting Resource Selection":
            assertTrue("Click functionality is available on the page" + pageName + " page. ", runtimeState.jobsAwaitingResourceSelectionPage.IsclickFunctinalityAvailable(testData.getInt("jobReference")));
            break;

        case "Jobs Awaiting Quote":
            assertFalse("Click functionality is available on the page" + pageName + " page. ", runtimeState.managerJobsAwaitingQuotePage.IsclickFunctinalityAvailable(testData.getInt("jobReference")));
            break;

        case "Quotes Awaiting Review":
            assertTrue("Click functionality is not available on the page" + pageName + " page. ", runtimeState.quotesAwaitingReviewPage.IsclickFunctinalityAvailable(testData.getInt("jobReference")));
            break;

        case "Quotes in Query":
            assertTrue("Click functionality is not available on the page" + pageName + " page. ", runtimeState.quotesInQueryPage.IsClickFunctinalityAvailable(testData.getInt("jobReference")));
            break;

        case "Quotes with Query Response":
            assertTrue("Click functionality is not available on the page" + pageName + " page. ", runtimeState.quotesWithQueryResponsePage.IsClickFunctinalityAvailable(testData.getInt("jobReference")));
            break;

        default:
            throw new Exception("Unexpected page " + pageName);
        }
    }

    @ContinueNextStepsOnException
    @Then("^selecting the browsers Back button returns to the \"([^\"]*)\" page$")
    public void selecting_the_browsers_Back_button_will_return_to_the_page(String pageName) {
        outputHelper.takeScreenshot();
        getWebDriver().navigate().back();
        POHelper.waitForAngularRequestsToFinish();
        String currentPage = runtimeState.loginPage.getPageTitle();
        assertEquals("Expected page name " + quote(pageName) + ", but was " + quote(currentPage), pageName, currentPage);
    }

    @ContinueNextStepsOnException
    @Then("^selecting \"([^\"]*)\" displays the \"([^\"]*)\" page$")
    public void selecting_the_browsers_Back_button_will_return_to_the_page(String menuItem, String pageName) throws Exception {
        if (menuItem.contains(">")) {
            String[] parts = menuItem.split(">");
            menuSteps.sub_menu_is_selected_from_the_top_menu(parts[1].trim(), parts[0].trim());
        } else {
            runtimeState.portalNavBar.expandTopLevelMenu(menuItem);
        }

        String currentPage = runtimeState.loginPage.getPageTitle();
        assertEquals("Expected page name " + quote(pageName) + ", but was " + quote(currentPage), pageName, currentPage);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void assertDataInInvoicesGrid(String page, Grid grid, List<Map<String, Object>> dbData) throws Exception {

        // Compare both sets of data are the same size
        if (dbData.size() == 0) {
            assertEquals("Unexpected number of records", 1, grid.getRows().size()); // one row displayed in Grid with warning that no rows found
        } else {
            assertEquals("Unexpected number of records", dbData.size(), GridHelper.getAllRows(grid).size());
        }

        // Verify both datasets are the same
        Iterator it = dbData.iterator();
        if (dbData.size() > 0) {
            for (Row row : grid.getRows()) {
                Map<String, Object> map = (Map<String, Object>) it.next();

                // Assert custom columns
                switch (page) {
                case "All Invoices Awaiting Approval":
                    String invoiceNumber = row.getCell("Inv Num").getText();
                    logger.debug(invoiceNumber);
                    assertEquals("Unexpected Inv Num for " + invoiceNumber, normalize(map.get("InvNum").toString()), row.getCell("Inv Num").getText());
                    assertEquals("Unexpected Supplier for " + invoiceNumber, normalize(map.get("Supplier").toString()), row.getCell("Supplier").getText());
                    assertEquals("Unexpected Order Ref for " + invoiceNumber, map.get("OrderRef"), row.getCell("Order Ref").getText());
                    assertEquals("Unexpected Site for " + invoiceNumber, map.get("Site"), row.getCell("Site").getText());
                    assertEquals("Unexpected Date Submitted for " + invoiceNumber, map.get("DateSubmitted"), row.getCell("Date Submitted").getText());
                    assertEquals("Unexpected Inv Date for " + invoiceNumber, map.get("InvDate"), row.getCell("Inv Date").getText());
                    assertEquals("Unexpected Inv Net (" + CURRENCY_SYMBOL + ") for " + invoiceNumber, String.valueOf(new BigDecimal(map.get("InvNet").toString()).setScale(2, BigDecimal.ROUND_DOWN)), row.getCell(localize("Inv Net ($)")).getText());
                    assertEquals("Unexpected Inv Tax (" + CURRENCY_SYMBOL + ") for " + invoiceNumber, String.valueOf(new BigDecimal(map.get("InvTax").toString()).setScale(2, BigDecimal.ROUND_DOWN)), row.getCell(localize("Inv Vat ($)")).getText());
                    // Commenting line out due to a potential bug around rfm1 covering rfm2 covering rfm3 covering frm1 which causes
                    // the wrong approver to show
                    // assertEquals("Unexpected Approver for " + invoiceNumber, row.getCell("Approver").getText(), map.get("Approver"));
                    break;

                case "Invoices & Credits Awaiting Review":
                    String supplierRef = row.getCell("Supplier Ref").getText();
                    String orderRef = map.get("OrderRef").toString().equals("N/A") ? map.get("OrderRef").toString().replace("N/A", "") : map.get("OrderRef").toString();

                    logger.debug("The Supplier Reference for the current order is: " + supplierRef);

                    assertTrue("Unexpected Supplier Reference for " + supplierRef, row.getCell("Supplier Ref").getText().trim().contains(map.get("SupplierRef").toString().trim()));
                    assertTrue("Unexpected Type for " + supplierRef, row.getCell("Type").getText().contains(map.get("Type").toString()));
                    assertEquals("Unexpected Order Ref. for " + supplierRef, orderRef, row.getCell("Order Ref").getText());
                    assertThat("Unexpected Supplier for " + supplierRef, row.getCell("Supplier").getText().trim(), anyOf(equalTo(map.get("Supplier").toString().trim()), equalTo(map.get("SupplierName").toString().trim())));
                    assertEquals("Unexpected Date for " + supplierRef, map.get("Date"), row.getCell("Date").getText());
                    assertEquals("Unexpected Net Amount (" + CURRENCY_SYMBOL + ") for " + supplierRef, String.valueOf(new BigDecimal(map.get("Net").toString()).setScale(2, BigDecimal.ROUND_DOWN)), row.getCell(localize("Net (£)")).getText());
                    assertEquals("Unexpected Vat Amount (" + CURRENCY_SYMBOL + ") for " + supplierRef, String.valueOf(new BigDecimal(map.get("Vat").toString()).setScale(2, BigDecimal.ROUND_DOWN)), row.getCell(localize("Vat (£)")).getText());
                    assertEquals("Unexpected Gross Amount (" + CURRENCY_SYMBOL + ") for " + supplierRef, String.valueOf(new BigDecimal(map.get("Gross").toString()).setScale(2, BigDecimal.ROUND_DOWN)), row.getCell(localize("Gross (£)")).getText());
                    assertEquals("Unexpected Action for " + supplierRef, map.get("Action").toString().trim(), row.getCell("Action").getText().trim());
                    break;

                case "All Orders":
                    String orderReference = row.getCell("Order Ref").getText();
                    logger.debug(orderReference);
                    assertEquals("Unexpected Order Ref for " + orderReference, map.get("OrderRef"), row.getCell("Order Ref").getText());
                    assertEquals("Unexpected Order Date for " + orderReference, DateHelper.convert(map.get("OrderDate").toString(), DB_DATE_FORMAT, SHORT_DATE), row.getCell("Order Date").getText());
                    assertTrue("Unexpected Order Value (" + CURRENCY_SYMBOL + ") for " + orderReference, String.valueOf(new BigDecimal(map.get("OrderValue").toString()).setScale(2, BigDecimal.ROUND_DOWN)).contains(row.getCell(localize("Order Value ($)")).getText()));
                    assertTrue("Unexpected Action for " + orderReference, row.getCell("Action").getWebElement().getText().contains("View Purchase Order"));
                    runtimeState.scenario.write("Asserting the 'View Purchase Order' button is displayed on the grid.");
                    assertTrue("The 'View Purchase Order' button is not displayed for " + orderReference, runtimeState.allOrdersPage.isViewPurchaseOrderButtonDisplayed());
                    break;

                case "Submitted Invoices and Credits":
                    String refNumber = row.getCell("Ref Num").getText();
                    logger.debug(refNumber);
                    if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/")) {
                        assertEquals("Unexpected Type for " + refNumber, map.get("Type"), row.getCell("Type").getText());
                        assertEquals("Unexpected Ref Num for " + refNumber, normalize(map.get("RefNum").toString()), row.getCell("Ref Num").getText());
                        assertEquals("Unexpected Order Ref for " + refNumber, map.get("OrderRef"), row.getCell("Order Ref").getText());
                        assertEquals("Unexpected Date for " + refNumber, map.get("Date"), row.getCell("Date").getText());
                        assertTrue("Unexpected Net (" + CURRENCY_SYMBOL + ") for " + refNumber, String.valueOf(new BigDecimal(map.get("Net").toString()).setScale(2, BigDecimal.ROUND_DOWN)).contains(row.getCell(localize("Net ($)")).getText()));
                        assertTrue("Unexpected Tax (" + CURRENCY_SYMBOL + ") for " + refNumber, String.valueOf(new BigDecimal(map.get("Tax").toString()).setScale(2, BigDecimal.ROUND_DOWN)).contains(row.getCell(localize("Vat") + " (" + localize("$") + ")").getText()));
                        assertTrue("Unexpected Gross (" + CURRENCY_SYMBOL + ") for " + refNumber, String.valueOf(new BigDecimal(map.get("Gross").toString()).setScale(2, BigDecimal.ROUND_DOWN)).contains(row.getCell(localize("Gross ($)")).getText()));
                        assertEquals("Unexpected Status for " + refNumber, map.get("Status"), row.getCell("Status").getText());
                        if (dbHelperInvoices.getCountOfSubmittedCreditsWithinLast30DaysForSupplier(testData.getString("userName")) > 0) {
                            assertEquals("Unexpected Has Credit for " + refNumber, map.get("HasCredit"), row.getCell("Has Credit").getText());
                        }
                    } else {
                        assertEquals("Unexpected Type for " + refNumber, map.get("Type"), row.getCell("Type").getText());
                        assertEquals("Unexpected Ref Num for " + refNumber, normalize(map.get("RefNum").toString()), row.getCell("Ref Num").getText());
                        assertEquals("Unexpected Supplier for " + refNumber, normalize(row.getCell("Supplier").getText()), normalize(map.get("Supplier").toString()));
                        assertEquals("Unexpected Order Ref for " + refNumber, map.get("OrderRef"), row.getCell("Order Ref").getText());
                        assertEquals("Unexpected Job Ref for " + refNumber, map.get("JobRef"), row.getCell("Job Ref").getText());
                        assertEquals("Unexpected Site for " + refNumber, normalize(map.get("Site").toString()), row.getCell("Site").getText());
                        assertTrue("Unexpected Date for " + refNumber, DateHelper.convert(map.get("Date").toString(), DB_DATE_FORMAT, SHORT_DATE).contains(row.getCell("Date").getText()) || map.get("Date").toString().contains(row.getCell("Date").getText()));
                        assertTrue("Unexpected Net (" + CURRENCY_SYMBOL + ") for " + refNumber, String.valueOf(new BigDecimal(map.get("Net").toString()).setScale(2, BigDecimal.ROUND_DOWN)).contains(row.getCell(localize("Net ($)")).getText()));
                        assertTrue("Unexpected Tax (" + CURRENCY_SYMBOL + ") for " + refNumber, String.valueOf(new BigDecimal(map.get("Tax").toString()).setScale(2, BigDecimal.ROUND_DOWN)).contains(row.getCell(localize("Vat") + " (" + localize("$") + ")").getText()));
                        assertTrue("Unexpected Gross (" + CURRENCY_SYMBOL + ") for " + refNumber, String.valueOf(new BigDecimal(map.get("Gross").toString()).setScale(2, BigDecimal.ROUND_DOWN)).contains(row.getCell(localize("Gross ($)")).getText()));
                        assertEquals("Unexpected Status for " + refNumber, map.get("Status"), row.getCell("Status").getText());
                        assertEquals("Unexpected Has Credit for " + refNumber, map.get("HasCredit"), row.getCell("Has Credit").getText());
                    }
                    break;
                case "Invoices Awaiting Final Approval":
                    String invoiceRef = row.getCell("Invoice Ref").getText();
                    logger.debug(invoiceRef);
                    assertEquals("Unexpected Invoice Ref for " + invoiceRef, normalize(map.get("Invoice Ref").toString()), row.getCell("Invoice Ref").getText());
                    assertEquals("Unexpected Invoice Date for " + invoiceRef, map.get("Invoice Date"), row.getCell("Invoice Date").getText());
                    assertEquals("Unexpected Order Ref for " + invoiceRef, map.get("Order Ref"), row.getCell("Order Ref").getText());
                    assertEquals("Unexpected Initial Approver for " + invoiceRef, map.get("Initial Approver"), row.getCell("Initial Approver").getText());
                    assertEquals("Unexpected Budget for " + invoiceRef, map.get("Budget"), row.getCell("Budget").getText());
                    assertEquals("Unexpected Supplier for " + invoiceRef, normalize(map.get("Supplier").toString()), row.getCell("Supplier").getText());
                    assertEquals("Unexpected Site for " + invoiceRef, map.get("Site"), row.getCell("Site").getText());
                    assertEquals("Unexpected Division for " + invoiceRef, map.get("Division"), row.getCell("Division").getText());
                    assertEquals("Unexpected Net (" + CURRENCY_SYMBOL + ") for " + invoiceRef, map.get("Net"), row.getCell(localize("Net ($)")).getText());
                    assertEquals("Unexpected Tax (" + CURRENCY_SYMBOL + ") for " + invoiceRef, map.get("Tax"), row.getCell(localize("Tax ($)")).getText());
                    assertEquals("Unexpected Gross (" + CURRENCY_SYMBOL + ") for " + invoiceRef, map.get("Gross"), row.getCell(localize("Gross ($)")).getText());
                    break;
                default:
                    throw new Exception("Cannot find " + page);
                }
            }
        }
        outputHelper.takeScreenshots();
    }
}
