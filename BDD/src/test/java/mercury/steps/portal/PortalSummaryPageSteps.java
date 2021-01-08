package mercury.steps.portal;

import static mercury.helpers.Globalisation.LOCALE;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.pageobject.web.portal.PortalSummaryPage;
import mercury.pageobject.web.portal.invoices.InvoicesAndCreditsAllOrders;
import mercury.pageobject.web.portal.invoices.OrdersAwaitingInvoicePage;
import mercury.pageobject.web.portal.invoices.SubmittedInvoicesAndCreditsPage;
import mercury.pageobject.web.portal.quotes.JobsAwaitingQuotePage;
import mercury.pageobject.web.portal.quotes.QuotesAwaitingReviewPage;
import mercury.pageobject.web.portal.quotes.QuotesWithQueryPendingPage;
import mercury.pageobject.web.portal.resources.OnCallSchedulerPage;
import mercury.runtime.RuntimeState;

public class PortalSummaryPageSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;

    /**
     * This method asserts that count present on the portal summary page matches the
     * count on the sub menus under "Invoices and Credits" tab
     *
     * @throws Throwable
     */

    @And("^the count on the Summary page for \"([^\"]*)\" matches with the count for the following sections$")
    public void the_count_on_the_Summary_page_matches_with_the_count_for_the_following_sections(String topLevel, DataTable section) throws Throwable {
        if (runtimeState.portalSummaryPage.isNoCountMessageDisplayed()) {
            runtimeState.scenario.write("Removed Counts from home page currently!");
        } else {

            // Get all the Type rows and counts on the summary page
            POHelper.refreshPage();
            List<String> typeRows = runtimeState.portalSummaryPage.getTypeRows();
            Map<String, Object> typeRowsAndCount = new HashMap<String, Object>();
            if (!typeRows.isEmpty()) {
                for (String row : typeRows) {
                    runtimeState.scenario.write("Current Type is: " + row);
                    int countForEachRow = runtimeState.portalSummaryPage.getCountForType(row);
                    typeRowsAndCount.put(row, countForEachRow);
                }
                outputHelper.takeScreenshots();
            } else {
                runtimeState.scenario.write("This user has no outstanding activities ");
            }

            if (!typeRows.isEmpty()) {

                for (String row : typeRows) {
                    String changedTopLevel = (row.equals("All Orders") && LOCALE.equals("en-GB")) ? "Orders" : topLevel;
                    runtimeState.portalNavBar.clickTopLevelMenu(changedTopLevel);
                    runtimeState.portalNavBar.clickSubLevelMenu(row);
                    int storedCount = (int) typeRowsAndCount.get(row);
                    int actualCount = 0;

                    if (row.equals("All Orders")) {
                        runtimeState.invoicesAndCreditsAllOrders = new InvoicesAndCreditsAllOrders(getWebDriver()).get();
                        actualCount = runtimeState.invoicesAndCreditsAllOrders.getTotalRows(2);
                    }

                    if (row.equals("Orders Awaiting Invoice")) {
                        runtimeState.ordersAwaitingInvoicePage = new OrdersAwaitingInvoicePage(getWebDriver()).get();
                        actualCount = runtimeState.ordersAwaitingInvoicePage.getTotalRows(2);
                    }

                    if (row.equals("Submitted Invoices and Credits")) {
                        runtimeState.submittedInvoicesAndCreditsPage = new SubmittedInvoicesAndCreditsPage(getWebDriver()).get();
                        actualCount = runtimeState.submittedInvoicesAndCreditsPage.getTotalRows(2);
                    }

                    assertEquals("The count does not match for " + row + " page", storedCount, actualCount);
                    outputHelper.takeScreenshots();
                }
            }
        }
    }

    @When("^\"([^\"]*)\" is selected from outstanding activities$")
    public void the_section_is_selected_from_the_outstanding_activities(String activity) throws Throwable {
        runtimeState.portalSummaryPage = new PortalSummaryPage(getWebDriver()).get();
        
        if (getWebDriver().getCurrentUrl().contains("uswm")) {
            for (String row : runtimeState.portalSummaryPage.getGridAsString()) {
                if (row.contains(activity)) {
                    runtimeState.scenario.write(row);
                    testData.put("activity", activity);
                    testData.put("activityCount", row.replace(activity, "").trim());
                }
            }
            runtimeState.portalSummaryPage.clickSummaryTypeLink(activity);
        } else {
            for (String row : runtimeState.portalSummaryPage.getTiles()) {
                if (row.contains(activity)) {
                    runtimeState.scenario.write(row);
                    testData.put("activity", activity);
                    testData.put("activityCount", row.replace(activity, "").trim());
                }
            }
            runtimeState.portalSummaryPage.clickSummaryLinkFromTile(activity);
        }

        outputHelper.takeScreenshot();
        

        switch(activity) {
        case "Jobs Awaiting Quote":
            runtimeState.jobsAwaitingQuotePage = new JobsAwaitingQuotePage(getWebDriver()).get();
            break;

        case "On Call Schedule RHVAC Technician":
            runtimeState.onCallSchedulerPage = new OnCallSchedulerPage(getWebDriver()).get();
            break;

        case "Quotes Awaiting Review":
            runtimeState.quotesAwaitingReviewPage = new QuotesAwaitingReviewPage(getWebDriver()).get();
            break;

        case "Quotes With Query Pending":
            runtimeState.quotesWithQueryPendingPage = new QuotesWithQueryPendingPage(getWebDriver()).get();
            break;
        }

    }

}
