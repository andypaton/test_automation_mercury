package mercury.steps.helpdesk.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import static mercury.runtime.ThreadManager.getWebDriver;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.runtime.RuntimeState;

public class HelpdeskDocumentsTabSteps {

        @Autowired private RuntimeState runtimeState;
        @Autowired private TestData testData;
        @Autowired private OutputHelper outputHelper;

        @ContinueNextStepsOnException
        @Then("the purchase order details are shown in Documents Tab$")
        public void the_purchase_order_details_are_shown_in_documents_tab() throws Throwable {
            
            runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();

            runtimeState.helpdeskDocumentsTab = runtimeState.helpdeskJobPage.selectDocumentsTab();
            Grid grid = runtimeState.helpdeskDocumentsTab.getGrid();
            Row row = grid.getRows().get(0);
            
            outputHelper.takeScreenshot();
            runtimeState.scenario.write("Asserting Documents Tab for purchase order details");
            assertEquals("The PO Reference is not displayed as " +  testData.getString("PONumber"),  testData.getString("PONumber"), row.getCell("PO Reference").getText());
            assertTrue("The download button is not displayed in Documents tab", runtimeState.helpdeskDocumentsTab.isDownloadButtonDisplayed());
        }

    }
