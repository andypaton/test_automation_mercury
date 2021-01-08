package mercury.steps.helpdesk;

import static mercury.helpers.Globalisation.MEDIUM_DATE;
import static mercury.helpers.Globalisation.SHORT_TIME;
import static mercury.helpers.Globalisation.assertDateTimeFormat;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.database.dao.EventSummaryDao;
import mercury.database.models.EventSummary;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.JsonHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.helpdesk.HelpdeskTimelineTab;
import mercury.runtime.RuntimeState;


public class HelpdeskJobTimelineSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private TzHelper tzHelper;
    @Autowired private EventSummaryDao eventSummaryDao;
    @Autowired private OutputHelper outputHelper;
    @Autowired private PropertyHelper propertyHelper;


    private static final List<String> GAS_TIMELINE_EVENTS = Arrays.asList("Complete", "Resource returning", "Resource Awaiting Parts");

    private static boolean isNotesTimeline = false;

    @ContinueNextStepsOnException
    @Then("^the timeline displays an? \"([^\"]*)\" event for \"([^\"]*)\" with additional \"([^\"]*)\" details$")
    public void the_timeline_displays_event_including(String event, String amount, String details) throws Throwable {
        String notes = testData.getString("notes").replace(localize("$500"), localize(amount));
        testData.put("notes", notes);
        the_timeline_displays_event_including(event, details);
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays an? \"([^\"]*)\" event with additional details:$")
    public void the_timeline_displays_event_including(String event, Map<String, String> dataTable) throws Throwable {

        the_timeline_displays_event("displays", event);

        for (String key : dataTable.keySet()) {
            switch (key.toUpperCase().replace(" ", "")) {
            case "REQUESTEDBY":
                assertEquals("Expected RequestedBy: " + dataTable.get(key), dataTable.get(key), runtimeState.helpdeskTimelineTab.getEventDetails(event, "Requested By"));
                break;
            case "REASON":
                assertEquals("Expected Reason: " + dataTable.get(key), dataTable.get(key), runtimeState.helpdeskTimelineTab.getEventDetails(event, "Reason"));
                break;
            case "NOTES":
                String actualNotes = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Notes");
                assertTrue("Expected Notes to contain: " + dataTable.get(key) + ". Actual Notes  : " + actualNotes, actualNotes.contains(dataTable.get(key)));
                break;
            default:
                throw new Exception("Unexpected argument: " + dataTable.get(key));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays an? \"([^\"]*)\" event with \"([^\"]*)\"$")
    public void the_timeline_displays_event_including(String event, String details) throws Throwable {
        POHelper.refreshPage();
        the_timeline_displays_event("displays", event);
        assertEventDetails(normalize(event), details);
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays an? \"([^\"]*)\" event for the uplift with \"([^\"]*)\"$")
    public void the_timeline_displays_uplift_event_including(String event, String details) throws Throwable {
        testData.put("amount", testData.get("upliftAmount"));
        testData.put("notes", testData.get("upliftNotes"));
        the_timeline_displays_event("displays", event);
        assertEventDetails(localize(normalize(event)), details);
    }

    private void checkEvents(String event, String key, String expectedStr) throws Exception{
        runtimeState.scenario.write("Asserting " + key + " is " + expectedStr);
        boolean found = false;
        for (int i = 0; i < runtimeState.helpdeskTimelineTab.getNumberOfMatchingEvents(event); i++) {
            String actual = normalize(runtimeState.helpdeskTimelineTab.getEventDetails(event, i, key));
            if (actual.contains(expectedStr)) {
                found = true;
                break;
            }
        }
        assertTrue("Not found: " + expectedStr, found);
    }

    private void checkEventsNotNull(String event, String key) throws Exception{
        boolean found = false;
        for (int i = 0; i < runtimeState.helpdeskTimelineTab.getNumberOfMatchingEvents(event); i++) {
            String actual = normalize(runtimeState.helpdeskTimelineTab.getEventDetails(event, i, key));
            if (!actual.isEmpty()) {
                found = true;
                break;
            }
        }
        assertTrue("Not found: " + key, found);
    }

    private void assertEventDetails(String event, String details) throws Exception {
        // Remove whitespace and split by comma
        List<String> fields = Arrays.asList(details.replace(" and ", ", ").split(","));

        for (String field : fields) {
            switch (field.trim()) {
            case "Advised To":
                runtimeState.scenario.write("Asserting Advised To is " + testData.getString("advisedTo"));
                String actualAdvisedTo = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Advised To");
                assertEquals("Expected advisedTo: " + testData.getString("advisedTo") + ". Actual advisedTo : " + actualAdvisedTo, testData.getString("advisedTo"), actualAdvisedTo);
                break;
            case "Change":
                runtimeState.scenario.write("Asserting Change is " + testData.getString("change"));
                String actualChange = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Change");
                assertEquals("Expected Change: " + testData.getString("change") + ". Actual Change : " + actualChange, testData.getString("change"), actualChange);
                break;
            case "Due at":
                runtimeState.scenario.write("Asserting Due at is " + testData.getString("dueAt").toLowerCase());
                String actualDueAt = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Due at");
                assertEquals("Expected dueAt: " + testData.getString("dueAt").toLowerCase() + ". Actual dueAt : " + actualDueAt, testData.getString("dueAt").toLowerCase(), actualDueAt.toLowerCase());
                break;
            case "ETA":
                runtimeState.scenario.write("Asserting ETA is " + testData.getString("eta"));
                String actualEta = runtimeState.helpdeskTimelineTab.getEventDetails(event, "ETA");
                assertEquals("Expected ETA: " + testData.getString("eta") + ". Actual ETA : " + actualEta, testData.getString("eta"), actualEta);
                break;
            case "Info":
                runtimeState.scenario.write("Asserting Info is " + testData.getString("info"));
                String actualInfo = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Info");
                assertEquals("Expected Info: " + testData.getString("info") + ". Actual Info : " + actualInfo, testData.getString("info"), actualInfo);
                break;
            case "New resource":
                runtimeState.scenario.write("Asserting New resource is " + testData.getString("newResource"));
                String actualNewResource = runtimeState.helpdeskTimelineTab.getEventDetails(event, "New resource");
                assertEquals("Expected New resource: " + testData.getString("newResource") + ". Actual New resource : " + actualNewResource, testData.getString("newResource"), actualNewResource);
                break;
            case "Notes":
                String expectedNotes = localize(normalize(testData.getString("notes")));
                runtimeState.scenario.write("Asserting notes is " + expectedNotes);

                if (runtimeState.helpdeskTimelineTab.getNumberOfMatchingEvents(event) == 1) {
                    String actualNotes = normalize(runtimeState.helpdeskTimelineTab.getEventDetails(event, "Notes"));
                    assertTrue("\nExpected Notes: " + expectedNotes + "\nActual Notes  : " + actualNotes + "\n", actualNotes.contains(expectedNotes));

                } else {
                    checkEvents(event, "Notes", expectedNotes);
                }
                break;
            case "Old resource":
                String expectedOldResource = normalize(testData.getString("oldResource"));
                runtimeState.scenario.write("Asserting Old resource is " + expectedOldResource);
                String actualOldResource = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Old resource");
                assertEquals("Expected old resource: '" + expectedOldResource + "'. Actual Old resource : '" + actualOldResource +"'", expectedOldResource, actualOldResource);
                break;
            case "Parked until":
                runtimeState.scenario.write("Asserting Parked until is " + testData.getString("parkedUntil"));
                String actualParkedUntil = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Parked until");
                assertThat("Expected parkedUntil: " + testData.getString("parkedUntil") + ". Actual parkedUntil : " + actualParkedUntil, actualParkedUntil, IsEqualIgnoringCase.equalToIgnoringCase(testData.getString("parkedUntil")));
                break;
            case "Phone No":
                runtimeState.scenario.write("Asserting Phone No is " + testData.getString("phoneNo"));
                String actualPhoneNo = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Phone No");
                assertEquals("Expected phoneNo: " + testData.getString("phoneNo") + ". Actual phoneNo : " + actualPhoneNo, testData.getString("phoneNo"), actualPhoneNo);
                break;
            case "Quote request approver":
                runtimeState.scenario.write("Asserting Quote request approver is " + normalize(testData.getString("quoteRequestApprover")));
                String actualQuoteRequestApprover = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Quote request approver");
                assertEquals("Expected quoteRequestApprover: " + normalize(testData.getString("quoteRequestApprover")) + ". Actual quoteRequestApprover : " + actualQuoteRequestApprover, normalize(testData.getString("quoteRequestApprover")), actualQuoteRequestApprover);
                break;
            case "Reason":
                checkEvents(event, "Reason", testData.getString("reason"));
                break;
            case "Requested By":
                runtimeState.scenario.write("Asserting requestedBy is " + testData.getString("requestedBy") + " or " + testData.getString("loginUser"));
                String actualRequestedBy = normalize(runtimeState.helpdeskTimelineTab.getEventDetails(event, "Requested By"));
                assertTrue(actualRequestedBy.equals(normalize(testData.getString("requestedBy"))) || actualRequestedBy.equals(normalize(testData.getString("loginUser"))));
                break;
            case "Speaking With":
                runtimeState.scenario.write("Asserting Speaking With is " + testData.getString("speakingWith"));
                String actualSpeakingWith = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Speaking With");
                assertEquals("Expected Speaking With: " + testData.getString("speakingWith") + ". Actual Speaking With : " + actualSpeakingWith, normalize(testData.getString("speakingWith")), normalize(actualSpeakingWith));
                break;
            case "Time Spent":
                runtimeState.scenario.write("Asserting timeSpent is " + testData.getString("timeSpent"));
                String actualTimeSpent = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Time Spent");
                assertEquals("Expected Time Spent: " + testData.getString("timeSpent") + ". Actual Time Spent  : " + actualTimeSpent, testData.getString("timeSpent"), actualTimeSpent);
                break;
            case "Uplift cancellation authorized by":
                runtimeState.scenario.write("Asserting " + localize(field.trim()) + " is " + normalize(testData.getString("upliftCancellationAuthorizedBy")));
                String actualUpliftCancellationAuthorizedBy = normalize(runtimeState.helpdeskTimelineTab.getEventDetails(event, localize(field.trim())));
                assertEquals("Expected " + localize(field.trim()) + " : " + normalize(testData.getString("upliftCancellationAuthorizedBy")) + ". Actual " + localize(field.trim()) + " : " + actualUpliftCancellationAuthorizedBy, normalize(testData.getString("upliftCancellationAuthorizedBy")), actualUpliftCancellationAuthorizedBy);
                break;
            default:
                //                assertNotNull(runtimeState.helpdeskTimelineTab.getEventDetails(event, field.trim()));
                checkEventsNotNull(event, field.trim());
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the timeline (.*) an? \"([^\"]*)\" event logged by \"([^\"]*)\"$")
    public void the_timeline_displays_event(String displays, String event, String loggedBy) throws Throwable {
        testData.put("loggedBy", loggedBy);
        the_timeline_displays_event(displays, event);
    }

    public String getExpectedIcon(String event) {
        String icon = "wrench"; // default

        if (testData.getString("icon") != null) {
            icon = testData.getString("icon");
        } else {

            switch (event) {
            case "Awaiting Funding Authorisation":
            case "ETA Advised to Site":
            case "ETA Acknowledged":
            case "Funding Approved":
            case "Job completed":
            case "Job Provided With ETA":
            case "Job reopened":
            case "Resource Removal Rejected":
            case "Resource Removed":
            case "Confirmed Funding Request Canceled":
            case "Confirmed Funding Request Cancelled":
            case "notified by email of removal from job":
            case "Resource Removal Request":
            case "Resource Added":
            case "Job Parked":
            case "Additonal Resource Not Required":
            case "Job Un-Parked":
            case "Declined Job":
            case "Job Type changed to Quote":
            case "Job canceled":
            case "Work transferred":
                icon = "wrench";
                break;

            case "Email notification sent":
            case "Notification":
            case "Notification and text message sent":
            case "Notification sent, No text message sent as no number configured":
            case "Resource Notified by SMS, No IPad notification was sent - Not configured":
                icon = "tablet";
                break;

            case "Complete":
            case "Job logged":
            case "Job in Technical Bureau triage":
            case "Tech-Bureau assigning resources":
            case "Tech-Bureau marked job for cancellation":
            case "Tech-Bureau marked job for completion":
            case "Tech-Bureau triage update":
                icon = "book";
                break;

            case "Chase ETA Chase":
            case "Chase Manager Chase":
            case "Outbound call successful":
            case "Outbound call unsuccessful":
            case "Update - ETA Chase":
            case "Callback scheduled for Resource":
                icon = "phone";
                break;

            case "Job cancellation requested":
                icon = "times";
                break;

            case "Quote Request Approver Set":
                icon = "user";
                break;

            case "Quote Request Raised":
                icon = "file";
                break;

            case "Public note added":
            case "Private note added":
                icon = "comment";
                break;
            }
        }
        return icon;
    }

    @ContinueNextStepsOnException
    @Then("^the timeline (.*) an? \"([^\"]*)\" event$")
    public void the_timeline_displays_event(String displays, String event) throws Throwable {

        event = localize(event);

        if ("EMAIL NOTIFICATION SENT".equalsIgnoreCase(event)) {
            if (!testData.getString("resourceStatus").contains("New Job Notification Sent")) {
                displays = "does not display";
            }
        }

        String icon = getExpectedIcon(event);

        String[] parts = event.split(",");
        for (int i = 0; i < parts.length; i++) {
            event = parts[i].trim();

            if ("DOES NOT DISPLAY".equalsIgnoreCase(displays)) {

                assertEventNotDisplayed(event);

            } else {
                assertEventDisplayed(event, icon, false);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the (?:timeline|job timeline) displays a new \"([^\"]*)\" event$")
    public void the_timeline_displays_new_event(String event) throws Throwable {
        String icon = getExpectedIcon(event);
        assertEventDisplayed(event, icon, true);
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays a new \"([^\"]*)\" event - ios$")
    public void the_timeline_displays_new_event_ios(String event) throws Throwable {
        //iOS notifications are only available in UAT and Live environments
        String environment = propertyHelper.getEnv();
        if (environment.contains("UAT")) {
            String icon = getExpectedIcon(event);
            assertEventDisplayed(event, icon, true);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the notes timeline displays a new \"([^\"]*)\" event$")
    public void the_notes_timeline_displays_new_event(String event) throws Throwable {
        isNotesTimeline = true;
        the_timeline_displays_new_event(event);
    }

    private void assertEventDisplayed(String event, String icon, Boolean loggedNow) throws Throwable {

        if (runtimeState.helpdeskTimelineTab == null) {
            runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
        }

        // if the requested event contains commas then treat the split it and assert each part
        String[] parts = event.split(",");
        for (int i = 0; i < parts.length; i++) {
            event = parts[i].trim();

            if ( !isNotesTimeline ) {
                runtimeState.helpdeskTimelineTab.search(event);
            }

            event = normalize(event);

            runtimeState.helpdeskTimelineTab.expandAll();
            runtimeState.helpdeskTimelineTab.takeScreenshotOfEvent(outputHelper, event);

            if (loggedNow) {
                // assert if the event was logged now
                assertEventLoggedNow(event, icon);

            } else {
                String message = "Asserting timeline: event name is " + event + ", event icon is " + icon;
                runtimeState.scenario.write(message);

                switch (event.toUpperCase()) {
                case "JOB CANCELED": // US and UK english have different cases
                case "JOB CANCELLED":
                case "NOTIFICATION":
                    // ignore case
                    assertTrue("timeline event not found: " + event, runtimeState.helpdeskTimelineTab.timelineContainsLowercaseText(event));
                    break;

                default:
                    try {
                        assertTrue("timeline event not found: " + event, runtimeState.helpdeskTimelineTab.timelineContains(event));
                    } catch (Exception e) {
                        // refresh page and try once more
                        POHelper.refreshPage();
                        runtimeState.helpdeskTimelineTab.search(event);
                        runtimeState.helpdeskTimelineTab.expandAll();
                        assertTrue("timeline event not found: " + event, runtimeState.helpdeskTimelineTab.timelineContains(event));
                    }
                }

                assertIcon(event, icon);
            }

            if ("Resource Removed".equals(event)) {
                assertResourceRemovedDetails();
            }
        }
    }

    /**
     * assert event dates and user details
     * @param event
     * @param icon
     * @throws Exception
     */
    private void assertEventLoggedNow(String event, String icon) throws Exception {
        int jobReference = testData.getInt("jobReference");
        int siteId = dbHelperSites.getSiteIdForJobRef(jobReference);

        Date homeOfficeDate = tzHelper.getCurrentTimeAtHomeOffice(siteId);
        String homeOfficeDay = DateHelper.dateAsString(homeOfficeDate, MEDIUM_DATE);
        int homeOfficeHour = Integer.valueOf(DateHelper.dateAsString(homeOfficeDate, "HH"));
        int homeOfficeMinute = Integer.valueOf(DateHelper.dateAsString(homeOfficeDate, "mm"));

        String expectedDay = homeOfficeDay;
        if (homeOfficeHour == 23 && homeOfficeMinute > 58) {
            expectedDay = expectedDay + " or " + DateHelper.dateAsString(DateHelper.addDays(new Date(), -1), MEDIUM_DATE);
        }

        String loggedBy;
        if (runtimeState.scenario.getSourceTagNames().contains("@loggedByApi")) {
            loggedBy = testData.getString("apiUser");
        } else if (testData.getString("loggedBy") != null) {
            loggedBy = testData.getString("loggedBy");
        } else {
            loggedBy = testData.getString("loginUser");
        }

        String message = "Asserting timeline:";
        message += " event name is " + event;
        message += ", event icon is " + icon;
        message += ", event date is " + expectedDay;
        message += ", event time is within last minute (Home Office time: " + homeOfficeDate.toString() + ")";
        message += ", event logged by " + loggedBy;
        runtimeState.scenario.write(message);

        assertTrue("Expected date: " + expectedDay, expectedDay.equals(runtimeState.helpdeskTimelineTab.getDate(event)));

        if (runtimeState.helpdeskTimelineTab.isStoreTimeDisplayed(event)) {
            // Store Time displayed for Site

            String eventStoreTime = runtimeState.helpdeskTimelineTab.getStoreTime(event).replace("ST -", "").trim();
            assertDateTimeFormat(SHORT_TIME, eventStoreTime);

            Date storeDate = tzHelper.getCurrentTimeAtSite(siteId);
            String now = DateHelper.dateAsString(storeDate, SHORT_TIME);
            long differenceInMinutes = DateHelper.getDifferenceBetweenTwoTimes(eventStoreTime, now, SHORT_TIME);
            assertTrue("Expected:" +  now + "\nto be after Store Time: " + eventStoreTime + "\nDifference (minutes): " + differenceInMinutes, differenceInMinutes >= 0);
        }

        String eventHomeOfficeTime = runtimeState.helpdeskTimelineTab.getHomeOfficeTime(event).replace("HO -", "").trim();
        assertDateTimeFormat(SHORT_TIME, eventHomeOfficeTime);

        String now = DateHelper.dateAsString(homeOfficeDate, SHORT_TIME);
        long differenceInMinutes = DateHelper.getDifferenceBetweenTwoTimes(eventHomeOfficeTime, now, SHORT_TIME);
        assertTrue("Expected:" +  now + "\nto be after Home Office Time: " + eventHomeOfficeTime + "\nDifference (minutes): " + differenceInMinutes, differenceInMinutes >= 0);

        assertEquals("Expected LoggedBy: " + normalize(loggedBy), normalize(loggedBy), runtimeState.helpdeskTimelineTab.getLoggedBy(event));

        if (runtimeState.helpdeskTimelineTab.isEventDetailListed(event, "Impersonating")) {
            runtimeState.scenario.write("asserting timeline event contains Impersonating: " + testData.getString("impersonatedResourceName"));
            assertEquals("Expected Impersonating: " + testData.getString("impersonatedResourceName"), testData.getString("impersonatedResourceName"),
                    runtimeState.helpdeskTimelineTab.getEventDetails(event, "Impersonating"));
        }
    }

    private void assertIcon(String event, String icon) throws Exception {
        switch (icon) {
        case "tablet":
            assertTrue("Expected tablet icon", runtimeState.helpdeskTimelineTab.isTabletIcon(event));
            break;
        case "book":
            assertTrue("Expected times icon", runtimeState.helpdeskTimelineTab.isBookIcon(event));
            break;
        case "times":
            assertTrue("Expected times icon", runtimeState.helpdeskTimelineTab.isTimesIcon(event));
            break;
        case "wrench":
            assertTrue("Expected wrench icon", runtimeState.helpdeskTimelineTab.isWrenchIcon(event));
            break;
        case "phone":
            assertTrue("Expected phone icon", runtimeState.helpdeskTimelineTab.isPhoneIcon(event));
            break;
        case "user":
            assertTrue("Expected user icon", runtimeState.helpdeskTimelineTab.isUserIcon(event));
            break;
        case "file":
            assertTrue("Expected file icon", runtimeState.helpdeskTimelineTab.isFileIcon(event));
            break;
        case "comment":
            assertTrue("Expected comment icon", runtimeState.helpdeskTimelineTab.isCommentIcon(event));
            break;
        default:
            throw new Exception("Icon not asserted: " + icon);
        }
    }

    private void assertEventNotDisplayed(String event) throws Exception {

        if (runtimeState.helpdeskTimelineTab == null) {
            runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
        }

        runtimeState.helpdeskTimelineTab.search(event);
        runtimeState.helpdeskTimelineTab.takeScreenshotOfEvent(outputHelper, event);

        // when asserting a notification has been sent - it could either be on its own
        // or sent with a text message
        if ("Notification".equals(event)) {
            assertTrue("timeline event found: " + event, runtimeState.helpdeskTimelineTab.timelineDoesNotContainLowercaseText("Notification"));
        } else {
            assertTrue("timeline event found: " + event, runtimeState.helpdeskTimelineTab.timelineDoesNotContain(event));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays a \"([^\"]*)\" event with note \"([^\"]*)\"$")
    public void the_timeline_displays_a_event_that_has_Amount_authorised_automatically(String event, String note) throws Throwable {
        runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
        assertTrue("timeline event not found: " + event, runtimeState.helpdeskTimelineTab.timelineContains(event));
        String actualNotes = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Notes");
        assertTrue("Expected " + actualNotes + " to contain " + note, actualNotes.contains(note));
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays \"([^\"]*)\" \"([^\"]*)\" events with \"([^\"]*)\" icon and with \"([^\"]*)\" details$")
    public void the_timeline_displays_events_with_icon_and_with_details(String eventCount, String event, String icon, String details) throws Throwable {
        POHelper.refreshPage();
        if (runtimeState.helpdeskTimelineTab == null) {
            runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
        }
        List<String> events = runtimeState.helpdeskTimelineTab.getMatchingEvents(event);
        runtimeState.scenario.write("Asserting event count is: " + eventCount);
        assertEquals("Event count is not matching!", Integer.parseInt(eventCount), events.size());
        String resourceName = "";
        for( String e : events) {
            if (e.contains(normalize(testData.getString("resourceName")))) {
                testData.addStringTag("phoneNo", testData.getString("phoneNoInitial"));
                testData.addStringTag("notes", testData.getString("notesInitial"));
                testData.addStringTag("speakingWith", testData.getString("speakingWithInitial"));
                testData.addStringTag("eta", testData.getString("etaInitial"));
                testData.addStringTag("reason", testData.getString("reasonInitial"));
                resourceName = testData.getString("resourceName");

            } else if (e.contains(normalize(testData.getString("additionalResourceName")))) {
                testData.addStringTag("phoneNo", testData.getString("phoneNoAdditional"));
                testData.addStringTag("notes", testData.getString("notesAdditional"));
                testData.addStringTag("speakingWith", testData.getString("speakingWithAdditional"));
                testData.addStringTag("eta", testData.getString("etaAdditional"));
                testData.addStringTag("reason", testData.getString("reasonAdditional"));
                resourceName = testData.getString("additionalResourceName");
            }

            testData.addStringTag("icon", icon);
            String eventName = event.equals("Job Provided With ETA") ? event + " For " : event + " - ";
            the_timeline_displays_event_including(eventName + resourceName, details);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays an? \"([^\"]*)\" event with updated and original questions and answers$")
    public void the_timeline_displays_a_event_with_updated_and_original_questions_and_answers(String event) throws Throwable {

        the_timeline_displays_event("displays", event);

        Map<String, Object> responses = testData.getMap("questions");
        if (responses != null) {
            for (String question : responses.keySet()) {
                String answer = (String) responses.get(question);
                String actualAnswer = runtimeState.helpdeskTimelineTab.getEventDetails(event, question);
                runtimeState.scenario.write("Asserting: " + question + "\nis: " + answer);
                assertEquals("Expected " + question + " : " + answer + " but got " + actualAnswer, answer, actualAnswer);
            }
        }

        responses = testData.getMap("originalQuestions");
        if (responses != null) {
            for (String question : responses.keySet()) {
                String answer = (String) responses.get(question);
                String actualAnswer = runtimeState.helpdeskTimelineTab.getEventDetails(event, question);
                runtimeState.scenario.write("Asserting: " + question + "\nis: " + answer);
                assertEquals("Expected " + question + " : " + answer + " but got " + actualAnswer, answer, actualAnswer);
            }
        }
        throw new PendingException("still to code");
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays a \"([^\"]*)\" event with all questions asked and answers$")
    public void the_timeline_displays_a_event_with_all_questions_asked_and_answers(String event) throws Throwable {
        the_timeline_displays_event("displays", event);

        List<EventSummary> events = eventSummaryDao.getEventSummaryForJobReference(testData.getInt("jobReference"));
        for (EventSummary eventSummary : events) {
            if (eventSummary.getTitle().matches(event + ".*")) {
                // this is the latest event of the requested event type in the event summary

                if ("2019".equals(testData.getString("fgasRegulations"))) {
                    assertQuestions(event, eventSummary.getDetail1());
                    assertQuestions(event, eventSummary.getDetail2());

                } else {
                    assertUkGasQuestions(event, eventSummary.getDetail1());
                    assertUkGasQuestions(event, eventSummary.getDetail2());
                }

                if (eventSummary.getNotes() != null) {
                    Map<String, Object> notes = JsonHelper.toMap(eventSummary.getNotes());

                    runtimeState.scenario.write("Asserting Notes are: " + notes.get("Notes"));
                    String actualNotes = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Notes");
                    assertEquals("Expected Notes:" + normalize(notes.get("Notes").toString().replaceAll(" +", " ").replace("\r", "")), normalize(notes.get("Notes").toString().replaceAll(" +", " ").replace("\r", "")), normalize(actualNotes));
                }
                break;
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the FGAS questions and answers are not displayed on the \"([^\"]*)\" event$")
    public void the_FGAS_questions_and_answers_are_not_displayed_on_the_event(String event) throws Throwable {
        the_timeline_displays_event("displays", event);
        assertFalse(runtimeState.helpdeskTimelineTab.isEventDetailListed(event, "Gas Type"));
        assertFalse(runtimeState.helpdeskTimelineTab.isEventDetailListed(event, "Gas Usage"));
    }

    public static JSONArray extractJsonArray(String json, String element) throws Exception {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray msg = (JSONArray) jsonObject.get(element);
        return msg;
    }

    private void assertGasSubSection(String event, String jsonString, String section) throws Exception {
        JSONArray jsonArray = extractJsonArray(jsonString, section);
        if (jsonArray == null) return;

        List<String> subHeaders = runtimeState.helpdeskTimelineTab.getEventSubHeadings(event, section);
        List<String> subValues = runtimeState.helpdeskTimelineTab.getEventSubValues(event, section);

        for(int i = 0; i < jsonArray.size(); i++){
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) jsonArray.get(i);
            for (String key : map.keySet()) {
                if ( JsonHelper.isJSONValid(map.get(key).toString()) ) {
                    JSONObject jsonObject = (JSONObject) map.get(key);

                    String title = (String) jsonObject.get("Title");
                    String value = jsonObject.get("Value") == null ? "" : String.valueOf(jsonObject.get("Value"));
                    boolean isBold = (Long) jsonObject.get("Type") == 1;
                    String fontWeight = isBold ? "(Bold) " : "";

                    runtimeState.scenario.write("Asserting " + fontWeight + title + " is: " + normalize(value));
                    assertTrue("Header not found: " + title, subHeaders.contains(title));
                    assertTrue("Value not found: " + value, subValues.contains(normalize(value)));

                    if (isBold) {
                        assertTrue("Expected bold text: " + isBold, isBold == runtimeState.helpdeskTimelineTab.isBold(event, section, title, value));
                    }
                } else {
                    if (!"|".equals(key.trim())) {
                        String value = normalize(map.get(key).toString());
                        runtimeState.scenario.write("Asserting " + key + " is: " + value);
                        assertTrue("Header not found: " + key, subHeaders.contains(key));
                        assertTrue("Value not found: " + value, subValues.contains(value));
                    }
                }
            }
        }
    }

    public void assertQuestions(String event, String jsonQuestionAnswers) throws Exception {

        if (jsonQuestionAnswers == null) return;

        Map<String, Object> map = JsonHelper.toMap(jsonQuestionAnswers);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String question = entry.getKey();
            String answer = String.valueOf(entry.getValue());

            if (JsonHelper.isJSONValid(answer)) {
                assertGasSubSection(event, jsonQuestionAnswers, question);

            } else {
                runtimeState.scenario.write("Asserting " + question + " is: " + answer);
                String actualAnswer = runtimeState.helpdeskTimelineTab.getEventDetails(event, question);
                assertEquals("Expected " + question + " = " + answer, normalize(answer), normalize(actualAnswer));
            }
        }
    }

    public void assertUkGasQuestions(String event, String jsonQuestionAnswers) throws Exception {

        if (jsonQuestionAnswers == null) return;

        // fix bad data
        jsonQuestionAnswers = jsonQuestionAnswers.replaceAll("\\\\\"", "\"");                               // replace all \" for "
        jsonQuestionAnswers = jsonQuestionAnswers.replaceAll("\"\\[", "\\[").replaceAll("\\]\"", "\\]");    // replace all "[ for [, and ]" for ]

        Map<String, Object> map = JsonHelper.toMap(jsonQuestionAnswers);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String question = entry.getKey();
            String answer = String.valueOf(entry.getValue());

            if (JsonHelper.isJSONValid(answer)) {
                // if the expected answer is a json list then assert each header value combo

                runtimeState.scenario.write(question + " :");

                List<String> subHeaders = runtimeState.helpdeskTimelineTab.getEventSubHeadings(event, question);
                List<String> subValues = runtimeState.helpdeskTimelineTab.getEventSubValues(event, question);

                Map<String, Object> subMap = JsonHelper.toMap(answer);
                for (Map.Entry<String, Object> sub : subMap.entrySet()) {

                    if (sub.getValue() instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> valueTitleMap = (Map<String,Object>) sub.getValue();
                        String header = (String) valueTitleMap.get("Title");
                        String value = String.valueOf(valueTitleMap.get("Value"));

                        runtimeState.scenario.write(" - Asserting " + header + " is: " + value);
                        assertTrue("Header not found: " + header, subHeaders.contains(header));
                        assertTrue("Value not found: " + value, subValues.contains(value));
                    }
                }

            } else {

                runtimeState.scenario.write("Asserting " + question + " is: " + answer);
                String actualAnswer = runtimeState.helpdeskTimelineTab.getEventDetails(event, question);
                assertEquals("Expected " + question + " = " + answer, normalize(answer), normalize(actualAnswer));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays an event listing all gas site visit questions answered$")
    public void the_timeline_displays_an_event_listing_all_gas_site_visit_questions_answered() throws Throwable {

        List<EventSummary> events = eventSummaryDao.getEventSummaryForJobReference(testData.getInt("jobReference"));
        for (EventSummary eventSummary : events) {

            String event = eventSummary.getTitle().split("-")[0].trim();

            if (GAS_TIMELINE_EVENTS.contains(event)) {
                the_timeline_displays_event("displays", event);

                // this is the latest event of the requested event type in the event summary
                assertQuestions(event, eventSummary.getDetail1());
                assertQuestions(event, eventSummary.getDetail2());

                if (eventSummary.getNotes() != null) {
                    Map<String, Object> notes = JsonHelper.toMap(eventSummary.getNotes());

                    runtimeState.scenario.write("Asserting Notes are: " + notes.get("Notes"));
                    String actualNotes = runtimeState.helpdeskTimelineTab.getEventDetails(event, "Notes");
                    assertEquals("Expected Notes:" + notes.get("Notes"), notes.get("Notes").toString().trim().replaceAll(" +", " "), actualNotes.trim());
                }
                break;
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays an event for resource removal$")
    public void the_timeline_displays_an_event_for_resource_removal() throws Throwable {

        String[] reason = testData.getString("ResourceRemovalReason").split(":");
        String RemovalReason = reason[1].replaceAll("\"", "").replace("}", "");
        testData.addStringTag("reason", RemovalReason);

        String resourceRemovalNotes = testData.getString("ResourceRemovalNotes");
        if (resourceRemovalNotes != null) {
            String[] notes = testData.getString("ResourceRemovalNotes").split(":");
            String RemovalNotes = notes[1].replaceAll("\"", "").replace("}", "");
            testData.addStringTag("notes", RemovalNotes);
            the_timeline_displays_event_including("Resource Removed", "Reason, Notes");
        } else {
            the_timeline_displays_event_including("Resource Removed", "Reason");
        }
    }

    @ContinueNextStepsOnException
    @Then("^each jobs timeline displays an event with the questions asked and answers$")
    public void each_jobs_timeline_displays_an_event_with_the_questions_asked_and_answers() throws Throwable {

        List<String> fgasQuestions = testData.getArray("fgasQuestions");
        for (String fgasQuestion : fgasQuestions) {
            String[] parts = fgasQuestion.split("¦");
            String jobReference = parts[0];
            String event = parts[1];
            String question = parts[2];
            String answer = parts[3];

            // open a tab in the helpdesk for each job
            runtimeState.helpdeskHomePage.selectTab(jobReference);
            runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
            runtimeState.scenario.write("Asserting " + question + " is: " + answer);
            String actualAnswer = runtimeState.helpdeskTimelineTab.getEventDetails(event, question);
            assertEquals("Expected " + question + " = " + answer, normalize(answer), normalize(actualAnswer));
        }
    }

    public void assertResourceRemovedDetails() throws Throwable {
        if (testData.getString("ResourceRemovalReason") != null) {
            String[] reason = testData.getString("ResourceRemovalReason").split(":");
            String RemovalReason = reason[1].replaceAll("\"", "").replace("}", "");
            testData.addStringTag("reason", RemovalReason);

            String resourceRemovalNotes = testData.getString("ResourceRemovalNotes");
            if (resourceRemovalNotes != null) {
                String[] notes = testData.getString("ResourceRemovalNotes").split(":");
                String RemovalNotes = notes[1].replaceAll("\"", "").replace("}", "");
                testData.addStringTag("notes", RemovalNotes);
                assertEventDetails("Resource Removed", "Reason, Notes");
            } else {
                assertEventDetails("Resource Removed", "Reason");
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the timeline displays a \"([^\"]*)\" event with Notes \"([^\"]*)\"$")
    public void the_timeline_displays__a_event_with_Notes(String event, String notes) throws Throwable {
        if (notes.contains("$")) notes = localize(notes);
        testData.put("notes", notes);
        the_timeline_displays_event_including(event, "Notes");
    }

}