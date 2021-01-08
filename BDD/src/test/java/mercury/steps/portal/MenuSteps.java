package mercury.steps.portal;

import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.DataTable;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.pageobject.web.portal.PortalNavBar;
import mercury.pageobject.web.portal.PortalPopupPage;
import mercury.pageobject.web.portal.admin.ManageDeptHeadUserAssignmentPage;
import mercury.pageobject.web.portal.creditnotes.AllCreditNotesAwaitingApprovalPage;
import mercury.pageobject.web.portal.creditnotes.CreateCreditNotePage;
import mercury.pageobject.web.portal.fundingrequests.FundingRequestQueriesPage;
import mercury.pageobject.web.portal.fundingrequests.FundingRequestsAwaitingResponseReviewPage;
import mercury.pageobject.web.portal.fundingrequests.FundingRequestsAwaitingReviewPage;
import mercury.pageobject.web.portal.fundingrequests.FundingRequestsPendingQueryResponsePage;
import mercury.pageobject.web.portal.fundingrequests.FundingRequestsRejectedPage;
import mercury.pageobject.web.portal.fundingrequests.InitialFundingRequestsPage;
import mercury.pageobject.web.portal.invoices.AllInvoicesAwaitingApprovalPage;
import mercury.pageobject.web.portal.invoices.AllOrdersPage;
import mercury.pageobject.web.portal.invoices.ConsolidatedInvoicesInProgressPage;
import mercury.pageobject.web.portal.invoices.InvoiceTemplatePage;
import mercury.pageobject.web.portal.invoices.InvoiceTemplatesPage;
import mercury.pageobject.web.portal.invoices.InvoicesAndCreditsAwaitingReviewPage;
import mercury.pageobject.web.portal.invoices.InvoicesAwaitingApprovalPage;
import mercury.pageobject.web.portal.invoices.InvoicesAwaitingFinalApprovalPage;
import mercury.pageobject.web.portal.invoices.InvoicesAwaitingReviewPage;
import mercury.pageobject.web.portal.invoices.OrdersAwaitingInvoicePage;
import mercury.pageobject.web.portal.invoices.SubmittedInvoicesAndCreditsPage;
import mercury.pageobject.web.portal.invoices.UploadInvoiceDocumentsPage;
import mercury.pageobject.web.portal.jobs.AllJobsPage;
import mercury.pageobject.web.portal.jobs.FeedbackPage;
import mercury.pageobject.web.portal.jobs.OpenAwaitingJobsPage;
import mercury.pageobject.web.portal.jobs.PortalLogAJobPage;
import mercury.pageobject.web.portal.jobs.PortalOpenJobsBySitePage;
import mercury.pageobject.web.portal.multiquote.MultiQuoteAwaitingBypassApprovalPage;
import mercury.pageobject.web.portal.multiquote.MultiQuoteAwaitingBypassReviewPage;
import mercury.pageobject.web.portal.multiquote.MultiQuoteAwaitingQuotePage;
import mercury.pageobject.web.portal.multiquote.MultiQuoteAwaitingResourceSelectionPage;
import mercury.pageobject.web.portal.multiquote.MultiQuoteAwaitingReviewPage;
import mercury.pageobject.web.portal.parts.PartsRequestsAwaitingApprovalPage;
import mercury.pageobject.web.portal.quotes.JobsAwaitingQuotePage;
import mercury.pageobject.web.portal.quotes.JobsAwaitingResourceSelectionPage;
import mercury.pageobject.web.portal.quotes.ManagerJobsAwaitingQuotePage;
import mercury.pageobject.web.portal.quotes.OpenQuoteRequestsPage;
import mercury.pageobject.web.portal.quotes.QuotesAwaitingReviewPage;
import mercury.pageobject.web.portal.quotes.QuotesInQueryPage;
import mercury.pageobject.web.portal.quotes.QuotesWithQueryPendingPage;
import mercury.pageobject.web.portal.quotes.QuotesWithQueryResponsePage;
import mercury.pageobject.web.portal.resources.JobsAwaitingReallocationPage;
import mercury.pageobject.web.portal.resources.OnCallSchedulerPage;
import mercury.pageobject.web.portal.resources.OnCallSummaryPage;
import mercury.pageobject.web.portal.resources.ResourceAvailabilityPage;
import mercury.runtime.RuntimeState;

public class MenuSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TestData testData;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;


    private void dismissWarningAlert() {
        PortalPopupPage popup = new PortalPopupPage(getWebDriver());
        if (popup.isPopupVisible()) {
            popup.clickYes();
        }
    }

    private void clickMenu(String topMenu, String subMenu) throws Exception {
        outputHelper.takeScreenshot();
        if(runtimeState.portalNavBar == null) {
            runtimeState.portalNavBar = new PortalNavBar(getWebDriver()).get();
        }
        runtimeState.portalNavBar.clickTopLevelMenu(topMenu);
        runtimeState.portalNavBar.clickSubLevelMenu(subMenu);
    }

    private void resourcesMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "On Call Scheduler":
            OnCallSchedulerPage onCallSchedulerPage = new OnCallSchedulerPage(getWebDriver());
            if (!onCallSchedulerPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }
            runtimeState.onCallSchedulerPage = new OnCallSchedulerPage(getWebDriver()).get();
            break;

        case "On Call Summary":
            OnCallSummaryPage onCallSummaryPage = new OnCallSummaryPage(getWebDriver());
            if (!onCallSummaryPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }
            runtimeState.onCallSummaryPage = new OnCallSummaryPage(getWebDriver()).get();
            break;

        case "Jobs Awaiting Reallocation":
            JobsAwaitingReallocationPage jobsAwaitingReallocationPage = new JobsAwaitingReallocationPage(getWebDriver());
            if (!jobsAwaitingReallocationPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }
            runtimeState.jobsAwaitingReallocationPage = new JobsAwaitingReallocationPage(getWebDriver()).get();
            break;

        case "Resources with upcoming unavailability":
            if (getWebDriver().getCurrentUrl().contains("usad") && testData.getString("profileName").equalsIgnoreCase("Divisional Manager")) {
                runtimeState.portalNavBar.clickTopLevelMenu(topMenu);
                runtimeState.portalNavBar.clickSeniorManagerResourceAvailabilitySubMenu(subMenu);
            } else {
                ResourceAvailabilityPage resourceAvailabilityPage = new ResourceAvailabilityPage(getWebDriver());
                if (!resourceAvailabilityPage.isPageLoaded()) {
                    clickMenu(topMenu, subMenu);
                    dismissWarningAlert();
                }
            }
            runtimeState.resourceAvailabilityPage = new ResourceAvailabilityPage(getWebDriver()).get();
            break;

        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    private void adminMainMenu(String subMenu, String topMenu) throws Exception {
        clickMenu(topMenu, subMenu);

        switch (subMenu) {
        case "Manage Department Head User Assignment":
            runtimeState.manageDeptHeadUserAssignmentPage = new ManageDeptHeadUserAssignmentPage(getWebDriver()).get();
            break;
        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    private void jobMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "Log Job":
            PortalLogAJobPage logJob = new PortalLogAJobPage(getWebDriver());
            if (!logJob.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }
            runtimeState.portalLogAJobPage = new PortalLogAJobPage(getWebDriver()).get();
            break;
        case "Jobs Awaiting Acceptance":
            OpenAwaitingJobsPage awaitingJobs = new OpenAwaitingJobsPage(getWebDriver());
            if (!awaitingJobs.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }
            runtimeState.openAwaitingJobsPage = new OpenAwaitingJobsPage(getWebDriver()).get();
            break;
        case "Open Jobs":
            OpenAwaitingJobsPage openJobs = new OpenAwaitingJobsPage(getWebDriver());
            if (!openJobs.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }
            runtimeState.openAwaitingJobsPage = new OpenAwaitingJobsPage(getWebDriver()).get();
            break;
        case "Open Jobs By Site":
            clickMenu(topMenu, subMenu);
            runtimeState.portalOpenJobsBySitePage = new PortalOpenJobsBySitePage(getWebDriver()).get();
            break;
        case "All Jobs":
            clickMenu(topMenu, subMenu);
            runtimeState.allJobsPage = new AllJobsPage(getWebDriver()).get();
            break;
        case "Feedback":
            clickMenu(topMenu, subMenu);
            runtimeState.feedbackPage = new FeedbackPage(getWebDriver()).get();
            break;
        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    private void quotesMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "Jobs Awaiting Quote":
            JobsAwaitingQuotePage jobsAwaitingQuote = new JobsAwaitingQuotePage(getWebDriver());
            if (!jobsAwaitingQuote.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }

            if (getWebDriver().getCurrentUrl().contains("Portal/QuoteList/JobsAwaitingQuote")) {
                runtimeState.jobsAwaitingQuotePage = new JobsAwaitingQuotePage(getWebDriver()).get();
            } else if (getWebDriver().getCurrentUrl().contains("/Portal/JobsAwaitingQuote")) {
                runtimeState.managerJobsAwaitingQuotePage = new ManagerJobsAwaitingQuotePage(getWebDriver()).get();
            }
            break;

        case "Jobs Awaiting Resource Selection":
            JobsAwaitingResourceSelectionPage jobsAwaitingResourceSelectionPage = new JobsAwaitingResourceSelectionPage(getWebDriver());
            if (!jobsAwaitingResourceSelectionPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.jobsAwaitingResourceSelectionPage = new JobsAwaitingResourceSelectionPage(getWebDriver()).get();
            break;

        case "Open Quote Requests":
            runtimeState.portalNavBar.clickTopLevelMenu(topMenu);
            runtimeState.portalNavBar.clickSubLevelMenu(subMenu);

            runtimeState.openQuoteRequestsPage = new OpenQuoteRequestsPage(getWebDriver()).get();
            break;

        case "Quotes Awaiting Review":
            QuotesAwaitingReviewPage quotesAwaitingReviewPage = new QuotesAwaitingReviewPage(getWebDriver());
            if (!quotesAwaitingReviewPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }

            runtimeState.quotesAwaitingReviewPage = new QuotesAwaitingReviewPage(getWebDriver()).get();
            break;

        case "Quotes in Query":
            QuotesInQueryPage quotesInQueryPage = new QuotesInQueryPage(getWebDriver());
            if (!quotesInQueryPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.quotesInQueryPage = new QuotesInQueryPage(getWebDriver()).get();
            break;

        case "Quotes with Query Pending":
            QuotesWithQueryPendingPage quotesWithQueryPendingPage = new QuotesWithQueryPendingPage(getWebDriver());
            if (!quotesWithQueryPendingPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
                dismissWarningAlert();
            }
            runtimeState.quotesWithQueryPendingPage = new QuotesWithQueryPendingPage(getWebDriver()).get();
            break;

        case "Quotes with Query Response":
            QuotesWithQueryResponsePage quotesWithQueryResponsePage = new QuotesWithQueryResponsePage(getWebDriver());
            if (!quotesWithQueryResponsePage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.quotesWithQueryResponsePage = new QuotesWithQueryResponsePage(getWebDriver()).get();
            break;

        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    private void multiQuotesMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "Awaiting Bypass Review":
            MultiQuoteAwaitingBypassApprovalPage multiQuoteAwaitingBypassApprovalPage = new MultiQuoteAwaitingBypassApprovalPage(getWebDriver());
            if (!multiQuoteAwaitingBypassApprovalPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.multiQuoteAwaitingBypassApprovalPage = new MultiQuoteAwaitingBypassApprovalPage(getWebDriver()).get();
            break;

        case "Awaiting Quote":
            MultiQuoteAwaitingQuotePage multiQuoteAwaitingQuotePage = new MultiQuoteAwaitingQuotePage(getWebDriver());
            if (!multiQuoteAwaitingQuotePage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.multiQuoteAwaitingQuotePage = new MultiQuoteAwaitingQuotePage(getWebDriver()).get();
            break;

        case "Awaiting Resource Selection":
            MultiQuoteAwaitingResourceSelectionPage multiQuoteAwaitingResourceSelectionPage = new MultiQuoteAwaitingResourceSelectionPage(getWebDriver());
            if (!multiQuoteAwaitingResourceSelectionPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.multiQuoteAwaitingResourceSelectionPage = new MultiQuoteAwaitingResourceSelectionPage(getWebDriver()).get();
            break;

        case "Awaiting Review":
            MultiQuoteAwaitingReviewPage multiQuoteAwaitingReviewPage = new MultiQuoteAwaitingReviewPage(getWebDriver());
            if (!multiQuoteAwaitingReviewPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.multiQuoteAwaitingReviewPage = new MultiQuoteAwaitingReviewPage(getWebDriver()).get();
            break;

        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }

    }

    public void invoicesMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "Invoices Awaiting Approval":
            InvoicesAwaitingApprovalPage invoicesAwaitingApprovalPage = new InvoicesAwaitingApprovalPage(getWebDriver());
            if (!invoicesAwaitingApprovalPage.isPageLoaded()) {
                runtimeState.portalNavBar.clickInvoicesMenu(topMenu);
                runtimeState.portalNavBar.clickSubLevelMenu(subMenu);
            }
            runtimeState.invoicesAwaitingApprovalPage = new InvoicesAwaitingApprovalPage(getWebDriver()).get();
            break;
        case "Invoices Awaiting Final Approval":
            InvoicesAwaitingFinalApprovalPage invoicesAwaitingFinalApprovalPage = new InvoicesAwaitingFinalApprovalPage(getWebDriver());
            if (!invoicesAwaitingFinalApprovalPage.isPageLoaded()) {
                runtimeState.portalNavBar.clickInvoicesMenu(topMenu);
                runtimeState.portalNavBar.clickSubLevelMenu(subMenu);
            }
            runtimeState.invoicesAwaitingFinalApprovalPage = new InvoicesAwaitingFinalApprovalPage(getWebDriver()).get();
            break;
        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    private void invoicesCreditsMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "Orders Awaiting Invoice":
            OrdersAwaitingInvoicePage awaitingInvoiceJobs = new OrdersAwaitingInvoicePage(getWebDriver());
            if (!awaitingInvoiceJobs.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.ordersAwaitingInvoicePage = new OrdersAwaitingInvoicePage(getWebDriver()).get();
            break;
        case "Invoices Template":
            InvoiceTemplatesPage invoiceTemplates = new InvoiceTemplatesPage(getWebDriver());
            if (!invoiceTemplates.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            if (getWebDriver().getCurrentUrl().contains("Portal/Supplier/InvoiceTemplateUpload")) {
                runtimeState.invoiceTemplatesPage = new InvoiceTemplatesPage(getWebDriver()).get();
                break;
            } else {
                runtimeState.invoiceTemplatePage = new InvoiceTemplatePage(getWebDriver()).get();
                break;
            }
        case "Invoices & Credits Awaiting Review":
            InvoicesAndCreditsAwaitingReviewPage invoicesAndCreditsAwaitingReviewPage = new InvoicesAndCreditsAwaitingReviewPage(getWebDriver());
            if (!invoicesAndCreditsAwaitingReviewPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.invoicesAndCreditsAwaitingReviewPage = new InvoicesAndCreditsAwaitingReviewPage(getWebDriver()).get();
            break;
        case "Invoices Awaiting Review":
            InvoicesAwaitingReviewPage invoicesAwaitingReviewPage = new InvoicesAwaitingReviewPage(getWebDriver());
            if (!invoicesAwaitingReviewPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.invoicesAwaitingReviewPage = new InvoicesAwaitingReviewPage(getWebDriver()).get();
            break;
        case "All Invoices Awaiting Approval":
            AllInvoicesAwaitingApprovalPage allInvoicesAwaitingApprovalPage = new AllInvoicesAwaitingApprovalPage(getWebDriver());
            if (!allInvoicesAwaitingApprovalPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.allInvoicesAwaitingApprovalPage = new AllInvoicesAwaitingApprovalPage(getWebDriver()).get();
            break;
        case "All Credit Notes Awaiting Approval":
            AllCreditNotesAwaitingApprovalPage allCreditNotesAwaitingApprovalPage = new AllCreditNotesAwaitingApprovalPage(getWebDriver());
            if (!allCreditNotesAwaitingApprovalPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.allCreditNotesAwaitingApprovalPage = new AllCreditNotesAwaitingApprovalPage(getWebDriver()).get();
            break;
        case "Submitted Invoices and Credits":
            SubmittedInvoicesAndCreditsPage submittedInvoicesAndCreditsPage = new SubmittedInvoicesAndCreditsPage(getWebDriver());
            runtimeState.scenario.write("Checking if the page has been loaded");
            outputHelper.takeScreenshot();
            if (!submittedInvoicesAndCreditsPage.isPageLoaded()) {
                outputHelper.takeScreenshot();
                clickMenu(topMenu, subMenu);
            }
            runtimeState.submittedInvoicesAndCreditsPage = new SubmittedInvoicesAndCreditsPage(getWebDriver()).get();
            break;
        case "Create Standalone Credit Note":
            CreateCreditNotePage createCreditNotePage = new CreateCreditNotePage(getWebDriver());
            if (!createCreditNotePage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.createCreditNotePage = new CreateCreditNotePage(getWebDriver()).get();
            break;
        case "Create Consolidated Invoice":
            UploadInvoiceDocumentsPage uploadInvoiceDocumentsPage = new UploadInvoiceDocumentsPage(getWebDriver());
            if (!uploadInvoiceDocumentsPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.uploadInvoiceDocumentsPage = new UploadInvoiceDocumentsPage(getWebDriver()).get();
            break;
        case "Consolidated Invoices In Progress":
            ConsolidatedInvoicesInProgressPage consolidatedInvoicesInProgressPage = new ConsolidatedInvoicesInProgressPage(getWebDriver());
            if (!consolidatedInvoicesInProgressPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.consolidatedInvoicesInProgressPage = new ConsolidatedInvoicesInProgressPage(getWebDriver()).get();
            break;
        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    private void partsMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "Parts Awaiting Approval":
            PartsRequestsAwaitingApprovalPage partsRequestsAwaitingApprovalPage = new PartsRequestsAwaitingApprovalPage(getWebDriver());
            if (!partsRequestsAwaitingApprovalPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.partsRequestsAwaitingApprovalPage = new PartsRequestsAwaitingApprovalPage(getWebDriver()).get();
            break;
        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    private void ordersMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "All Orders":
            AllOrdersPage allOrdersPage = new AllOrdersPage(getWebDriver());
            if (!allOrdersPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.allOrdersPage = new AllOrdersPage(getWebDriver()).get();
            break;
        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    private void fundingRequestMainMenu(String subMenu, String topMenu) throws Exception {
        switch (subMenu) {
        case "Awaiting Bypass Review":
            MultiQuoteAwaitingBypassReviewPage multiQuoteAwaitingBypassReviewPage = new MultiQuoteAwaitingBypassReviewPage(getWebDriver());
            if (!multiQuoteAwaitingBypassReviewPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.multiQuoteAwaitingBypassReviewPage = new MultiQuoteAwaitingBypassReviewPage(getWebDriver()).get();
            break;

        case "Awaiting Response Review":
            FundingRequestsAwaitingResponseReviewPage fundingRequestsAwaitingResponseReviewPage = new FundingRequestsAwaitingResponseReviewPage(getWebDriver());
            if (!fundingRequestsAwaitingResponseReviewPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }

            runtimeState.fundingRequestsAwaitingResponseReviewPage = new FundingRequestsAwaitingResponseReviewPage(getWebDriver()).get();
            break;

        case "Awaiting Review":
            FundingRequestsAwaitingReviewPage fundingRequestsAwaitingReviewPage = new FundingRequestsAwaitingReviewPage(getWebDriver());
            if (!fundingRequestsAwaitingReviewPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }

            runtimeState.fundingRequestsAwaitingReviewPage = new FundingRequestsAwaitingReviewPage(getWebDriver()).get();
            break;

        case "Funding Request Query":
            FundingRequestQueriesPage fundingRequestQueriesPage = new FundingRequestQueriesPage(getWebDriver());
            if (!fundingRequestQueriesPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.fundingRequestQueriesPage = new FundingRequestQueriesPage(getWebDriver()).get();
            break;

        case "Funding Request Rejected":
            FundingRequestsRejectedPage fundingRequestsRejectedPage = new FundingRequestsRejectedPage(getWebDriver());
            if (!fundingRequestsRejectedPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.fundingRequestsRejectedPage = new FundingRequestsRejectedPage(getWebDriver()).get();
            break;

        case "Initial Funding Requests":
            InitialFundingRequestsPage initialFundingRequestsPage = new InitialFundingRequestsPage(getWebDriver());
            if (!initialFundingRequestsPage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.initialFundingRequestsPage = new InitialFundingRequestsPage(getWebDriver()).get();
            break;


        case "Pending Query Response":
            FundingRequestsPendingQueryResponsePage fundingRequestsPendingQueryResponsePage = new FundingRequestsPendingQueryResponsePage(getWebDriver());
            if (!fundingRequestsPendingQueryResponsePage.isPageLoaded()) {
                clickMenu(topMenu, subMenu);
            }
            runtimeState.fundingRequestsPendingQueryResponsePage = new FundingRequestsPendingQueryResponsePage(getWebDriver()).get();
            break;

        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    @Given("^the \"([^\"]*)\" sub menu is selected from the \"([^\"]*)\" top menu$")
    public void sub_menu_is_selected_from_the_top_menu(String subMenu, String topMenu) throws Exception {
        // ToDo make this code more generic and not specific to the log a job page
        switch (topMenu) {
        case "Admin":
            adminMainMenu(subMenu, topMenu);
            break;
        case "Jobs":
            jobMainMenu(subMenu, topMenu);
            break;
        case "Quotes":
            quotesMainMenu(subMenu, topMenu);
            break;
        case "Funding Requests":
            fundingRequestMainMenu(subMenu, topMenu);
            break;
        case "Invoices":
            invoicesMainMenu(subMenu, topMenu);
            break;
        case "Invoices and Credits":
            invoicesCreditsMainMenu(subMenu, topMenu);
            break;
        case "Resources":
            resourcesMainMenu(subMenu, topMenu);
            break;
        case "Multi-Quotes":
            multiQuotesMainMenu(subMenu, topMenu);
            break;
        case "Parts":
            partsMainMenu(subMenu, topMenu);
            break;
        case "Orders":
            ordersMainMenu(subMenu, topMenu);
            break;

        default:
            throw new Exception("Cannot find menu item " + subMenu);
        }
    }

    @And("menu item \"([^\"]*)\" is selected")
    public void menu_item_is_selected(String menuItem) throws Exception {
        if (menuItem.contains(">")) {
            String[] parts = menuItem.split(">");
            sub_menu_is_selected_from_the_top_menu(parts[1].trim(), parts[0].trim());
        } else {
            runtimeState.portalNavBar.expandTopLevelMenu(menuItem);
        }
    }

    @And("^the \"([^\"]*)\" top menu contains sub menus? \"([^\"]*)\"$")
    public void the_top_menu_contains_sub_menus(String topMenu, String subMenus) {
        runtimeState.portalNavBar.expandTopLevelMenu(topMenu);
        outputHelper.takeScreenshots();
        String[] subMenu = subMenus.split(",");
        for (String option : subMenu) {
            if (!option.isEmpty()) {
                assertTrue("Oops!Looks like " + option + " sub menu is not displayed under " + topMenu + " menu", runtimeState.portalNavBar.isSubMenuDisplayed(option.trim()));
            }
        }
    }

    @And("^the sub menus are visible$")
    public void the_sub_menus_are_visible(DataTable subMenus) throws Throwable {
        for (Map<String, String> row : subMenus.asMaps(String.class, String.class)) {
            String topLevel = row.get("topMenu");
            String subLevel = row.get("subMenu");
            the_top_menu_contains_sub_menus(topLevel, subLevel);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" top menu contains following sub menu options$")
    public void the_something_top_menu_contains_following_sub_menu_options(String topMenu, DataTable subMenus) throws Throwable {
        String subMenu = subMenus.asList(String.class).toString();
        subMenu = subMenu.substring(1, subMenu.length() - 1);
        the_top_menu_contains_sub_menus(topMenu, subMenu);
    }

    @ContinueNextStepsOnException
    @Then("^all their configured menus items are displayed$")
    public void all_their_configured_menus_items_are_displayed() throws Throwable {
        List<Map<String, Object>> dbData = dbHelperResources.getMenuItems(testData.getInt("resourceId"), testData.getString("resourceTypeName"));

        List<String> menuItems = new ArrayList<>();
        for (int i = 0; i < dbData.size(); i++) {
            String menuItem = (String) dbData.get(i).get("Menu");
            if ( !menuItems.contains(menuItem) ) {
                menuItems.add(menuItem);
            }
        }

        runtimeState.scenario.write("Configured menu items: \n" + menuItems.toString().replaceAll("[\\[\\]]","").replaceAll(", ", "\n"));

        for (String menuItem : menuItems) {
            if ( !menuItem.contains(" > ")) {
                assertTrue(runtimeState.portalNavBar.isMenuDisplayed(menuItem));

            } else {
                String parts[] = menuItem.split(" > ");
                String topLevel = parts[0];
                String subLevel = parts[1];

                if ("Snow Management".equals(subLevel) && dbHelperSystemToggles.getSystemFeatureToggle("Snow Management") == 0) {
                    // toggle is set to OFF - ignore it!

                } else if (subLevel.contains("Funding Requests") && dbHelperSystemToggles.getSystemFeatureToggle("FundingRequestAvailableForCityResource") == 0 && "City Resource".equals(testData.getString("resourceType"))) {
                    // toggle is set to OFF - ignore it!

                } else if ((subLevel.contains("Certificates") || subLevel.equals("Remedials/Recommendations Awaiting Review")) && dbHelperSystemToggles.getSystemFeatureToggle("Compliance") == 0) {
                    // toggle is set to OFF - ignore it!

                } else if (subLevel.contains("Consolidated") && dbHelperSystemToggles.getSystemFeatureToggle("Invoicing") == 0) {
                    // toggle is set to OFF - ignore it!

                } else {
                    the_top_menu_contains_sub_menus(topLevel, subLevel);
                }
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^all configured menus are displayed including:$")
    public void all_their_configured_menus_items_are_displayed(List<String> menuItems) throws Throwable {
        all_their_configured_menus_items_are_displayed();

        for (String menuItem : menuItems) {
            String[] parts = menuItem.split(">");
            String topLevel = parts[0].trim();
            String subLevel = parts.length > 1 ? parts[1].trim() : "";
            the_top_menu_contains_sub_menus(topLevel, subLevel);
        }
    }

    @ContinueNextStepsOnException
    @Then("^a user menu displays their username and a logout option$")
    public void a_user_menu_displays_their_username_and_a_logout_option() throws Throwable {
        List<String> options = runtimeState.portalNavBar.getUserMenuOptions();
        String username = testData.getString("impersonatedResourceName");
        assertTrue("Menu does not contain: " + normalize(username), options.stream().anyMatch(normalize(username)::equalsIgnoreCase));
        assertTrue("Menu does not contain: Log Out", options.contains("Log Out"));
    }

}
