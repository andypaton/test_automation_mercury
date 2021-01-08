package mercury.steps.portal.jobs;

import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.applicationinsights.web.dependencies.apachecommons.lang3.StringUtils;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.JobViewDao;
import mercury.database.models.JobView;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.portal.jobs.FeedbackPage;
import mercury.pageobject.web.portal.jobs.FeedbackResponsePage;
import mercury.runtime.RuntimeState;

public class PortalFeedbackSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private OutputHelper outputHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;


    @When("^the job awaiting feedback response in the \"([^\"]*)\" table is searched for and opened$")
    public void the_job_awaiting_feedback_response_in_the_table_is_searched_for_and_opened(String tableName) throws Throwable {
        runtimeState.feedbackPage = new FeedbackPage(getWebDriver()).get();

        if (tableName.equals("Combined Feedback")) {
            testData.put("feedbackItemsCount", runtimeState.feedbackPage.getCombinedFeedbackCount());
        }

        Row row = null;
        for (int attempts = 0; attempts < 5; attempts++) {
            try {
                runtimeState.feedbackPage.searchJobAwaitingFeedback(testData.getString("jobReference"));
                outputHelper.takeScreenshot();

                Grid grid = runtimeState.feedbackPage.getGrid(tableName);
                GridHelper.waitForRowCount(grid.getGridXpath(), 1);

                runtimeState.scenario.write("Number of rows in grid after search: " + grid.getRows().size());


                row = grid.getRows().get(0);
                assertEquals(testData.getString("jobReference"), row.getCell(0).getText());

                String cssSelector = row.getCell(0).getCssSelector();
                getWebDriver().findElement(By.cssSelector(cssSelector)).click();
                runtimeState.scenario.write("Selected row for job ref: " + testData.getString("jobReference"));

                POHelper.waitForAngularRequestsToFinish();

                runtimeState.feedbackResponsePage = new FeedbackResponsePage(getWebDriver()).get();
                break;

            } catch (AssertionError | StaleElementReferenceException e) {
                runtimeState.scenario.write("Failed to select row for job ref: " + testData.getString("jobReference"));
                runtimeState.scenario.write("Row 1 Job Ref: " + row.getCell(0).getText());
            }
        }

        if ( !runtimeState.feedbackResponsePage.isJobTimelineDisplayed() ) {
            logger.debug("Job Timeline not displayed. Refreshing page!");
            POHelper.refreshPage();
        }
    }

    /**
     * @param section - In this method, job and feedback details displayed in the Feedback Response page are asserted.
     */
    @ContinueNextStepsOnException
    @Then("^the feedback response page is displayed with ((?:job|feedback)) details$")
    public void the_feedback_response_page_is_displayed_with_details(String section) throws Throwable {
        // Asserting the fields and their values displayed in the 'Job Details' section
        if ("JOB".equalsIgnoreCase(section)) {
            JobView jobView = jobViewDao.getByJobReference(testData.getInt("jobReference"));
            runtimeState.scenario.write("Asserting Job Details in Feedback response page: ");

            runtimeState.scenario.write("Asserting Site is: " + jobView.getName());
            assertEquals("Unexpected Site ", jobView.getName(), runtimeState.feedbackResponsePage.getJobDetailsFieldData("Site"));

            runtimeState.scenario.write("Asserting Location is: " + jobView.getSubLocationName());
            assertEquals("Unexpected Location ", jobView.getSubLocationName().replaceAll("\\s+", ""), runtimeState.feedbackResponsePage.getJobDetailsFieldData("Location").replaceAll("\\s+", ""));

            runtimeState.scenario.write("Asserting Subtype / Classification is: " + jobView.getAssetSubTypeName() + jobView.getAssetClassificationName());
            assertEquals("Unexpected Subtype", (jobView.getAssetSubTypeName() + jobView.getAssetClassificationName()).replaceAll("\\s+", ""), runtimeState.feedbackResponsePage.getJobDetailsFieldData("Subtype / Classification").replaceAll(" > |\\s+", ""));

            runtimeState.scenario.write("Asserting Fault Type is: " + jobView.getFaultType());
            assertEquals("Unexpected Fault Type ", jobView.getFaultType(), runtimeState.feedbackResponsePage.getJobDetailsFieldData("Fault Type"));

            runtimeState.scenario.write("Asserting Description is: " + jobView.getDescription());
            assertEquals("Unexpected Description ", jobView.getDescription().replaceAll("\\s+", ""), runtimeState.feedbackResponsePage.getJobDetailsFieldData("Description").replaceAll("\\s+", ""));

            runtimeState.scenario.write("Asserting Priority is: " + jobView.getFaultPriority());
            assertTrue("Unexpected Priority ", jobView.getFaultPriority().equals(runtimeState.feedbackResponsePage.getJobDetailsFieldData("Priority")) || runtimeState.feedbackResponsePage.getJobDetailsFieldData("Priority").contains(jobView.getPriority().toString()));

        } else if ("FEEDBACK".equalsIgnoreCase(section)) {
            // Asserting the field values and feedback image face displayed in the 'Feedback Details' section
            Map<String, Object> feedbackDetails = dbHelperJobs.getFeedBackDetailsForJob(testData.getInt("jobReference"));
            runtimeState.scenario.write("Asserting Feedback Details in Feedback response page: ");

            runtimeState.scenario.write("Asserting Feedback left by is: " + feedbackDetails.get("FeedbackLeftBy"));
            assertEquals("Unexpected FeedbackName", feedbackDetails.get("FeedbackLeftBy").toString().replaceAll("\\s+", ""), runtimeState.feedbackResponsePage.getFeedbackDetailsFieldData("Feedback left by").replaceAll("\\s+", ""));

            String expectedSupportingMessage = null;
            if (feedbackDetails.get("SupportingMessage") == null || feedbackDetails.get("SupportingMessage").toString().isEmpty()) {
                expectedSupportingMessage = "No supporting information was provided";
            } else {
                expectedSupportingMessage = feedbackDetails.get("SupportingMessage").toString();
            }
            runtimeState.scenario.write("Asserting Supporting message is: " + expectedSupportingMessage);
            if (runtimeState.feedbackResponsePage.getFeedbackDetailsFieldData("Supporting message").contains("View full details")) {
                runtimeState.feedbackDetailsModal = runtimeState.feedbackResponsePage.clickViewFullDetails();
                assertEquals("Unexpected Supporting message ", expectedSupportingMessage.replaceAll("\\s+", ""), runtimeState.feedbackDetailsModal.getSupportingMessage().replaceAll("\\s+", ""));
                outputHelper.takeScreenshots();
                runtimeState.feedbackResponsePage = runtimeState.feedbackDetailsModal.closeFeedbackDetails();
            } else {
                assertEquals("Unexpected SupportingMessage", expectedSupportingMessage.replaceAll("\\s+", ""), runtimeState.feedbackResponsePage.getFeedbackDetailsFieldData("Supporting message").replaceAll("\\s+", ""));
            }

            String feedbackFace = feedbackDetails.get("Rating").equals("Negative") ? "sad" : "happy";
            runtimeState.scenario.write("Asserting feedback Face is: " + feedbackFace);
            assertEquals("Unexpected Feed back Face", feedbackFace, runtimeState.feedbackResponsePage.getFeedbackFace().toLowerCase());

            String feedbackImageSource = runtimeState.feedbackResponsePage.getFeedbackImageSource();
            runtimeState.scenario.write("Asserting feedback Image source is: " + feedbackFace + ".svg");
            assertTrue("Unexpected Feedback Image ", feedbackImageSource.contains(feedbackFace + ".svg"));
        }
        outputHelper.takeScreenshots();
    }

    @And("^the feedback response is given through the ((?:Reply to Store|Spoke to Store)) action$")
    public void the_feedback_response_is_given_through_the_action(String action) throws Throwable {
        testData.put("action", action);
        String feedbackResponse = DataGenerator.generateRandomSentence();
        testData.put("feedbackResponse", feedbackResponse);
        if ("REPLY TO STORE".equalsIgnoreCase(action)) {
            runtimeState.replyToStoreModal.enterResponseText(feedbackResponse);
            runtimeState.scenario.write("Added feedback response: " + feedbackResponse);
            outputHelper.takeScreenshots();
            runtimeState.replyToStoreModal.send();
        } else if ("SPOKE TO STORE".equalsIgnoreCase(action)) {
            runtimeState.spokeToStoreModal.enterResponseText(feedbackResponse);
            String storeContactName = dbHelperResources.getRandomActiveResourceName();
            runtimeState.spokeToStoreModal.enterStoreContactName(storeContactName);
            runtimeState.scenario.write("Added feedback response: " + feedbackResponse + " and store contact name: " + storeContactName);
            testData.put("storeContactName", runtimeState.spokeToStoreModal.getStoreContactName().trim());
            outputHelper.takeScreenshots();
            runtimeState.spokeToStoreModal.send();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" event details are displayed in the Job Timeline table$")
    public void the_event_details_are_displayed_in_the_job_timeline_table(String event) throws Throwable {
        runtimeState.feedbackResponsePage.get();
        Grid grid = runtimeState.feedbackResponsePage.getJobTimeLineGrid();
        logger.debug("Grid retrieved:" + grid.getHeaders().toString() + " (" + grid.getRows().size() + " rows)");

        runtimeState.scenario.write("Job Timeline post-feedback: " + grid.getColumnText("Event").toString());

        // Get all rows across all pages
        List<Row> gridRows = GridHelper.getAllRows(grid);
        Row row = gridRows.get(gridRows.size() - 1);

        runtimeState.scenario.write("Asserting event is: " + event);
        assertEquals("Unexpected event ", event, row.getCell("Event").getText());

        String feedbackResponseTime = dbHelperJobs.getFeedBackResponseTime(testData.getInt("jobReference"));
        runtimeState.scenario.write("Asserting Time is: " + feedbackResponseTime);
        assertEquals("Unexpected Time", feedbackResponseTime, row.getCell("Time").getText());

        //Should use the Portal resource name
        String resourceName = dbHelperResources.getPortalResourceName(testData.getInt("resourceId")).trim();
        runtimeState.scenario.write("Asserting Resource is: " + resourceName);
        assertEquals("Unexpected Resource ", resourceName, row.getCell("Resource").getText().trim());

        runtimeState.scenario.write("Asserting " + localize("Cancelled") + " is: No");
        assertEquals("Unexpected Cancelled status ", "No", row.getCell(localize("Cancelled")).getText());

        String note = null;
        if ("REPLY TO STORE".equalsIgnoreCase(testData.getString("action"))) {
            note = "Response Type: Response through App, RFM Comments: " + testData.getString("feedbackResponse");
        } else {
            String contactName = testData.getString("storeContactName");
            note = "Response Type: Spoke to store, Store Contact Name: " + contactName + ", RFM Comments: " + testData.getString("feedbackResponse");
        }
        runtimeState.scenario.write("Asserting Note is: " + note);
        String expected = normalize(note);
        String actual = normalize(row.getCell("Note").getText());
        assertEquals("Expected: " + expected + ", Actual: " + actual, expected, actual);
    }

    @And("^a tick symbol is displayed on the \"([^\"]*)\" button$")
    public void a_tick_symbol_is_displayed_on_the_button(String buttonName) throws Throwable {
        runtimeState.scenario.write("Asserting tick symbol is displayed on the " + buttonName + " button");
        assertTrue("No tick symbol on button: " + buttonName, runtimeState.feedbackResponsePage.isTickSymbolDisplayedOnButton(buttonName));
    }

    @ContinueNextStepsOnException
    @Then("^the job reference is not in the Combined Feedback table$")
    public void the_job_reference_is_not_in_the_table() {
        runtimeState.feedbackPage.searchJobAwaitingFeedback(testData.getString("jobReference"));
        Grid grid = runtimeState.feedbackPage.getGrid("Combined Feedback");
        GridHelper.waitForRowCount(grid.getGridXpath(), 0);
        assertTrue("Unexpected job found: " + testData.getString("jobReference"), GridHelper.getNumberOfDisplayedRows() == 0);
    }

    @ContinueNextStepsOnException
    @Then("^the items count in the \"([^\"]*)\" table is reduced by \"([^\"]*)\"$")
    public void the_items_count_in_the_table_is_reduced_by(String tableName, String count) throws Throwable {
        Grid grid = runtimeState.feedbackPage.getGrid(tableName);
        int previousCount = testData.getInt("feedbackItemsCount");

        String label = GridHelper.getLabel(grid.getGridXpath());
        int newFeedbackItemsCount = label.equals("No items to display") ? 0 : Integer.valueOf(StringUtils.substringBetween(label, "of", "items").trim());

        runtimeState.scenario.write(String.format("Asserting items count, %d, in the feedback table is reduced by %d", previousCount, Integer.valueOf(count)));
        assertEquals(String.format("Items count in the feedback table is not reduced, was %d, expected %d, but is now %d", previousCount, previousCount-1, newFeedbackItemsCount) , Integer.parseInt(count), previousCount - newFeedbackItemsCount);
    }

    /**
     * Clicking on 'Split RFMs' button and checking if the combined feedback table is split into number of tables equal to
     * the number of RFMs whose absence is covered by the user provided the absent RFM has jobs awaiting feedback response
     * assigned to him. If the user has any jobs awaiting feedback response assigned to him apart from the ones assigned to
     * the RFMs covered by him, then a table will be displayed under his name too.
     */
    @ContinueNextStepsOnException
    @Then("^the Combined Feedback table is split according to the RFMs names whose absence is covered by the user$")
    public void the_combined_feedback_table_is_split_according_to_the_rfms_names_whose_absence_is_covered_by_the_user() throws Throwable {
        // Getting the RFM Names whose absence is covered by the user.
        List<String> rfmNames = dbHelperResources.getRFMNamesWhoseAbsenceIsCoveredBy(testData.getInt("resourceId"));
        String resourceName = dbHelperResources.getResourceName(testData.getInt("resourceId"));
        int combinedFeedbackCountOfUser = dbHelperJobs.getCountOfJobsAwaitingFeedbackResponseAssignedToRFM(resourceName);

        // If there are RFMs whose absence is covered by the user, then getting the number of jobs awaiting feedback response
        // assigned to them.
        if (!rfmNames.isEmpty()) {
            List<Integer> feedbackItemCount = new ArrayList<Integer>();
            int combinedFeedbackCountOfRFMs = 0;
            for (String rfmName : rfmNames) {
                int feedbackItemCountForRFM = dbHelperJobs.getCountOfJobsAwaitingFeedbackResponseAssignedToRFM(rfmName);
                feedbackItemCount.add(feedbackItemCountForRFM);
                logger.debug("rfmName: " + rfmName + " feedbackItemCountForRFM: " + feedbackItemCountForRFM);
                combinedFeedbackCountOfRFMs = combinedFeedbackCountOfRFMs + feedbackItemCountForRFM;
            }
            // Checking if the logged in user has jobs awaiting feedback response assigned to him separately apart from the ones
            // assigned to the RFMs covered by him.
            if (combinedFeedbackCountOfRFMs < combinedFeedbackCountOfUser) {
                rfmNames.add(resourceName);
                feedbackItemCount.add(combinedFeedbackCountOfUser - combinedFeedbackCountOfRFMs);
            }
            int index = 0;
            for (String rfmName : rfmNames) {
                if (feedbackItemCount.get(index) != 0) {
                    int expectedFeedbackItemCount = feedbackItemCount.get(index);
                    // Asserting RFM Names and the number of jobs awaiting feedback response assigned to them.
                    runtimeState.scenario.write("Asserting feedback grid is displayed for the RFM: " + rfmName + " and the item count in the grid is: " + feedbackItemCount.get(index));
                    int actualFeedbackItemCount = runtimeState.feedbackPage.getGrid(rfmName).getRows().size();
                    assertEquals("Unexpected Grid items count for: " + rfmName, expectedFeedbackItemCount, actualFeedbackItemCount);
                }
                index++;
            }
        } else {
            Grid grid = runtimeState.feedbackPage.getGrid(resourceName);
            String label = GridHelper.getLabel(grid.getGridXpath());
            int actualFeedbackItemCount = Integer.valueOf(StringUtils.substringBetween(label, "of", "items").trim());

            // When the user is not covering for any RFM, asserting the user name is displayed on the feedback table along with
            // number of feedback awaiting response jobs assigned to him.
            runtimeState.scenario.write("User is not covering for any RFM's absence.");
            runtimeState.scenario.write("Asserting the feedback table name changed to: " + resourceName + " and the item count in the feedback table is: " + actualFeedbackItemCount);
            assertEquals("Unexpected Grid items count for: " + resourceName, combinedFeedbackCountOfUser, actualFeedbackItemCount);
        }
    }
}
