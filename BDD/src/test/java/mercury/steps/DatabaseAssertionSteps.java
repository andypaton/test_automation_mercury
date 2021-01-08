package mercury.steps;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.MEDIUM_DATE;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.Globalisation.setWeightLabel;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import mercury.database.dao.EventSummaryDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.JobTimelineEventDao;
import mercury.database.dao.JobViewDao;
import mercury.database.dao.MessageDao;
import mercury.database.dao.PPMJobDao;
import mercury.database.dao.QuoteDao;
import mercury.database.dao.ReasonDao;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.dao.SiteVisitCylinderDetailsDao;
import mercury.database.dao.SiteVisitGasDetailsDao;
import mercury.database.dao.SiteVisitGasLeakSiteCheckDao;
import mercury.database.dao.SiteVisitsDao;
import mercury.database.dao.SiteVisitsGasUsageDao;
import mercury.database.models.EventSummary;
import mercury.database.models.ResourceAssignment;
import mercury.database.models.SiteVisitCylinderDetails;
import mercury.database.models.SiteVisitsGasUsage;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.FgasPortalHelper;
import mercury.helpers.JsonHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.asserter.dbassertions.AssertDatabaseCommon;
import mercury.helpers.asserter.dbassertions.AssertEventSummary;
import mercury.helpers.asserter.dbassertions.AssertJobStatus;
import mercury.helpers.asserter.dbassertions.AssertJobTimelineEvent;
import mercury.helpers.asserter.dbassertions.AssertPPMJobCompletionStatus;
import mercury.helpers.asserter.dbassertions.AssertPPMJobId;
import mercury.helpers.asserter.dbassertions.AssertPPMResourceStatus;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperAssertions;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.runtime.RuntimeState;

public class DatabaseAssertionSteps {

    private static final Logger logger = LogManager.getLogger();
    private static final Integer waitTime = 120000;

    @Autowired private AssertionFactory assertionFactory;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperAssertions dbHelperAssertions;
    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private DbHelperResources dbHelperResource;
    @Autowired private EventSummaryDao eventSummaryDao;
    @Autowired private FgasPortalHelper fgasPortalHelper;
    @Autowired private JobDao jobDao;
    @Autowired private JobTimelineEventDao jobTimelineEventDao;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private PPMJobDao ppmJobDao;
    @Autowired private MessageDao messageDao;
    @Autowired private QuoteDao quoteDao;
    @Autowired private ReasonDao reasonDao;
    @Autowired private ResourceAssignmentDao resourceAssignmentDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private SiteVisitsDao siteVisitsDao;
    @Autowired private SiteVisitsGasUsageDao siteVisitsGasUsageDao;
    @Autowired private SiteVisitGasDetailsDao siteVisitGasDetailsDao;
    @Autowired private SiteVisitGasLeakSiteCheckDao siteVisitGasLeakSiteCheckDao;
    @Autowired private SiteVisitCylinderDetailsDao siteVisitCylinderDetailsDao;
    @Autowired private TestData testData;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private OutputHelper outputHelper;

    private static final Integer RETURNING_RESOURCE_ASSIGNMENT_STATUS = 21;
    private static final Integer RETURNING_RESOURCE_ASSIGNMENT_REASON = 18;

    private static final Integer FUNDINGROUTE_ID_OPEX = 17;
    private static final Integer FUNDINGROUTE_ID_CAPEX = 16;
    private static final Integer FUNDINGROUTE_ID_BMI = 20;

    private static final String FULL_DATE_FORMAT_STRING = "dd MMMM yyyy";

    private void displayAssertionMessage(Map<String, Object> queryMap) {
        runtimeState.scenario.write("Asserting database has the following values:");
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            runtimeState.scenario.write(entry.getKey().toString() + " has the value " + entry.getValue().toString());
        }
        runtimeState.scenario.write("UpdatedOn is after " + runtimeState.timestamp);
    }

    @ContinueNextStepsOnException
    @Then("^the Job is updated with a \"([^\"]*)\" status$")
    public void the_job_is_updated_with_a_status(String jobStatus) throws Exception {
        if ("PPM Job".equalsIgnoreCase(testData.getString("jobStatus"))) {
            AssertPPMJobCompletionStatus assertPPMJobCompletionStatus = new AssertPPMJobCompletionStatus(ppmJobDao, testData.getInt("jobReference"), jobStatus);
            assertionFactory.performAssertion(assertPPMJobCompletionStatus);
        } else {
            if ("Awaiting Approval - Funding Request Rejected".equalsIgnoreCase(testData.getString("jobStatusBeforeSaved"))) {
                AssertJobStatus assertJobStatus = new AssertJobStatus(jobViewDao, testData.getInt("jobReference"), localize("Awaiting Approval - Funding Request Rejected"));
                assertionFactory.performAssertion(assertJobStatus, waitTime);
            } else {
                AssertJobStatus assertJobStatus = new AssertJobStatus(jobViewDao, testData.getInt("jobReference"), localize(jobStatus));
                assertionFactory.performAssertion(assertJobStatus, waitTime);
            }
        }
    }

    // The above checks for the callout status for ppm jobs that are awaiting part or the completion status for ppm jobs or the status of a open job.

    @ContinueNextStepsOnException
    @Then("^the Message table has been updated with \"([^\"]*)\"$")
    public void the_Message_table_has_been_updated_with(String eventType) throws Exception {
        String createdOn = runtimeState.timestamp;
        Map<String, Object> queryMap;
        switch (eventType) {
        case "Complete":
            queryMap = new HashMap<String, Object>();
            queryMap.put("m.Entity", "Job");
            queryMap.put("m.RoutingKey", "job.resourceassignment.complete");
            queryMap.put("m.EventType", eventType);
            queryMap.put("m.SubEntity", "ResourceAssignment");
            queryMap.put("m.ResourceAssignmentStatusId", 13);
            queryMap.put("m.ResourceAssignmentReasonId", 18);
            break;
        case "DeclinedInvitationToQuote":
            queryMap = new HashMap<String, Object>();
            queryMap.put("m.Entity", "Job");
            queryMap.put("m.RoutingKey", "job.declinedinvitationtoquote.resources");
            queryMap.put("m.EventType", "Resources");
            queryMap.put("m.SubEntity", eventType);
            break;
        case "ETACreated":
            queryMap = new HashMap<String, Object>();
            queryMap.put("m.Entity", "Job");
            queryMap.put("m.RoutingKey", "job.resourceassignment.etacreated");
            queryMap.put("m.EventType", eventType);
            queryMap.put("m.SubEntity", "ResourceAssignment");
            queryMap.put("m.ResourceAssignmentReasonId", 18);
            queryMap.put("m.ResourceId", this.testData.getInt("resourceId"));
            break;
        case "Returning":
            queryMap = new HashMap<String, Object>();
            queryMap.put("m.Entity", "Job");
            queryMap.put("m.RoutingKey", "job.resourceassignment.returning");
            queryMap.put("m.EventType", eventType);
            queryMap.put("m.SubEntity", "ResourceAssignment");
            queryMap.put("m.ResourceAssignmentStatusId", 21);
            queryMap.put("m.ResourceAssignmentReasonId", 18);
            queryMap.put("m.ResourceId", this.testData.getInt("resourceId"));
            break;
        case "ResourceAwaitingParts":
            queryMap = new HashMap<String, Object>();
            queryMap.put("m.Entity", "Job");
            queryMap.put("m.RoutingKey", "job.resourceassignment.resourceawaitingparts");
            queryMap.put("m.EventType", eventType);
            queryMap.put("m.SubEntity", "ResourceAssignment");
            queryMap.put("m.ResourceAssignmentStatusId", 10);
            queryMap.put("m.ResourceAssignmentReasonId", 18);
            queryMap.put("m.ResourceId", this.testData.getInt("resourceId"));
            break;

        default:
            throw new Exception("Cannot find message type " + eventType);
        }

        displayAssertionMessage(queryMap);

        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> messageDao.getRecordCount(testData.getInt("jobReference"), queryMap, createdOn), equalTo(1));
    }

    @ContinueNextStepsOnException
    @Then("^the JobTimelineEvent table has been updated with \"([^\"]*)\"$")
    public void the_timeline_data_is_updated(String eventType) throws Exception {
        // dont run the notification tests if before the 18th
        Date now = new Date();
        Date runDate = DateHelper.stringAsDate("20/12/2018", "dd/MM/yyyy");
        if (eventType.contains("Notification") && now.compareTo(runDate) < 0) {
            return;
        }

        // This should either be context sensitive or know where its being called from and run the necessary tests
        AssertJobTimelineEvent assertJobTimelineEvent;
        AssertDatabaseCommon assertDatabaseCommon;
        Map<String, Object> queryMap;
        List<String> resources;
        List<Map<String, Object>> resourceMap;
        Integer numberOfQuotes = 1;
        String createdOn = runtimeState.timestamp;
        switch (eventType) {
        case "Alternative Quote Requested":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 108);
            queryMap.put("jtle.Title", eventType + "%");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Funding Request Declined Notification":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", eventType + "%");
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"A funding request from % has been declined\"}");
            queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Funding Request Notification":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", eventType + "%");
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"A funding request from % has been submitted for approval\"}");
            queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertJobTimelineEvent = new AssertJobTimelineEvent(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertJobTimelineEvent, waitTime);
            break;

        case "In Query":
            resources = new ArrayList<>();
            resources = testData.getArray("resources");
            numberOfQuotes = resources.size();
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 75);
            queryMap.put("jtle.Title", eventType + "%");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, 1, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Initial Approver Responded to Funding Request Query":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 112);
            queryMap.put("jtle.Title", eventType);
            queryMap.put("jtle.Detail1", "{\"Reason\":\"%\",\"Notes\":\"" + testData.getString("response") + "\"}");
            queryMap.put("jtle.Detail2", "{\"Impersonating\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Invitation to Quote Accepted":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 129);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Invitation To Quote Declined Notification":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", eventType);
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"% has declined the invitation to quote\"}");
            queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Invitation To Quote Notification":
            List<Map<String, Object>> resourcesITQNotif = testData.getListMap("resources");
            for (int i = 0; i < resourcesITQNotif.size(); i++) {
                if ("ItqAwaitingAcceptance".equalsIgnoreCase(resourcesITQNotif.get(i).get("ApprovalStatusName").toString())) {
                    String resourceName = resourcesITQNotif.get(i).get("ResourceName").toString().split("\\(")[0].trim();
                    queryMap = new HashMap<String, Object>();
                    queryMap.put("jtle.JobEventTypeId", 69);
                    queryMap.put("jtle.Title", eventType + "%");
                    queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"% has requested %" + resourceName + "%to proceed to quote%\"}");
                    queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
                    displayAssertionMessage(queryMap);
                    assertJobTimelineEvent = new AssertJobTimelineEvent(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                    assertionFactory.performAssertion(assertJobTimelineEvent, waitTime);
                }
            }
            break;

        case "Invitation To Quote Query Notification":
            resources = new ArrayList<>();
            resources = testData.getArray("resources");
            numberOfQuotes = resources.size();
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", eventType + "%");
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"% has queried the quote provided by % - Query: %" + testData.getString("quoteQueryNotes") + "%\"}");
            queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, numberOfQuotes, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Job cancellation requested":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.Detail1", "{\"Requested By\":\"%\"}");
            queryMap.put("jtle.Reason", "{\"Reason\":\"%\"}");
            queryMap.put("jtle.JobEventTypeId", 33);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Job canceled":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 9);
            queryMap.put("jtle.Title", localize(eventType));
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Job deferred":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 3);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Job Type changed":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.Detail1", "{\"Change\":\"Job type changed from Quote to Reactive\",\"Notes\":\"%\"}");
            queryMap.put("jtle.Detail2", "{\"Reason\":\"Fund as reactive%\"%}");
            queryMap.put("jtle.JobEventTypeId", 61);
            queryMap.put("jtle.Title", eventType + "%");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Multi Quote bypass requested":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 127);
            queryMap.put("jtle.Title", eventType);
            queryMap.put("jtle.Detail1", "{\"Notes\":\"" + testData.getString("NotesforMultiQuoteBypass") + "\"}");
            queryMap.put("jtle.Detail2", "{\"Impersonating\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Parts Order Approved":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 132);
            queryMap.put("jtle.Title", eventType);
            queryMap.put("jtle.Detail1", "{\"Approved By\":\"%\",\"Supplier\":\"%\",\"Total Value\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Parts Order Issued":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.Detail1", "{\"Purchase Order Notification\":\"A parts order has been issued to %\"}");
            queryMap.put("jtle.JobEventTypeId", 134);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Parts Order Rejected":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 133);
            queryMap.put("jtle.Title", eventType);
            queryMap.put("jtle.Detail1", "{\"Rejected By\":\"%\",\"Supplier\":\"%\",\"Total Value\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Parts Requested":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 135);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Approved":
            List<Map<String, Object>> resourcesQuoteApproved = dbHelperQuotes.getQuoteResources(testData.getInt("jobReference"), "Approved");
            for (int i = 0; i < resourcesQuoteApproved.size(); i++) {
                String resourceName = resourcesQuoteApproved.get(i).get("ResourceName").toString().split("\\(")[0].trim();
                queryMap = new HashMap<String, Object>();
                queryMap.put("jtle.JobEventTypeId", 63);
                queryMap.put("jtle.Detail1", "{\"Quote approved for \":\"" + resourceName + "%\"}");
                queryMap.put("jtle.Title", eventType + " - " + resourceName + "%");
                displayAssertionMessage(queryMap);
                assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            }
            return;

        case "Quote Approval Request Notification":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"A Quote Job has been raised by % and has been sent to % for approval\"}");
            queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Awaiting Approval":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.Detail1", "{\"% Resource\":\"% \"}");
            queryMap.put("jtle.JobEventTypeId", 76);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Awaiting Resource Quote":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.Detail1", "{\"% quote invitation\":\"% \"}");
            queryMap.put("jtle.JobEventTypeId", 72); // Job Event Title = Resources Invited to Quote
            queryMap.put("jtle.Title", "Resources Invited to Quote");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Approval Declined Notification":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"% has declined the request to proceed to quote\"}");
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Funding Request Rejected":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 113);
            queryMap.put("jtle.Title", eventType + "%");
            queryMap.put("jtle.Detail1", "{\"Notes\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Funding Request Queried with Initial Approver":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 111);
            queryMap.put("jtle.Title", eventType + "");
            queryMap.put("jtle.Detail1", "{\"Reason\":\"" + testData.getString("queryReason") + "\",\"Notes\":\"" + testData.getString("queryNotes") + "\"}");
            if (null != testData.getString("impersonatedResourceName")) {
                queryMap.put("jtle.Detail2", "{\"Impersonating\":\"" + testData.getString("impersonatedResourceName") + "\"}");
            }
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Job Approved Notification Caller - Bypass Review":
            resourceMap = testData.getListMap("resources");
            for (int i = 0; i < resourceMap.size(); i++) {
                if ("Approved".equalsIgnoreCase(resourceMap.get(i).get("ApprovalStatusName").toString())) {
                    queryMap = new HashMap<String, Object>();
                    queryMap.put("jtle.JobEventTypeId", 69);
                    queryMap.put("jtle.Title", "Quote Job Approved Notification%");
                    queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"Caller has been notified of the approved quote job\"}");
                    queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
                    displayAssertionMessage(queryMap);
                    assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                    assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
                }
            }
            return;

        case "Quote Job Approved Notification Manager - Bypass Review":
            resourceMap = testData.getListMap("resources");
            for (int i = 0; i < resourceMap.size(); i++) {
                if ("Approved".equalsIgnoreCase(resourceMap.get(i).get("ApprovalStatusName").toString())) {
                    queryMap = new HashMap<String, Object>();
                    queryMap.put("jtle.JobEventTypeId", 69);
                    queryMap.put("jtle.Title", "Quote Job Approved Notification%");
                    queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"Manager has been notified of the approved quote job\"}");
                    queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
                    displayAssertionMessage(queryMap);
                    assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                    assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
                }
            }
            return;

        case "Quote Job Approved Notification Senior Manager - Bypass Review":
            resourceMap = testData.getListMap("resources");
            for (int i = 0; i < resourceMap.size(); i++) {
                if ("Approved".equalsIgnoreCase(resourceMap.get(i).get("ApprovalStatusName").toString())) {
                    queryMap = new HashMap<String, Object>();
                    queryMap.put("jtle.JobEventTypeId", 69);
                    queryMap.put("jtle.Title", "Quote Job Approved Notification%");
                    queryMap.put("jtle.Detail1", "{\"Job resource notified\":\" SeniorManager has been notified of the approved quote job\"}");
                    queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
                    displayAssertionMessage(queryMap);
                    assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                    assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
                }
            }
            return;

        case "Quote Job Approved Notification Caller":
            resources = new ArrayList<>();
            resources = testData.getArray("resources");
            numberOfQuotes = resources.size();
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", "Quote Job Approved Notification%");
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"Caller has been notified of the approved quote job\"}");
            queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, numberOfQuotes, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Job Approved Notification Manager":
            resourceMap = dbHelperQuotes.getQuoteResources(testData.getInt("jobReference"), "Approved");
            numberOfQuotes = resourceMap.size();
            for (int i = 0; i < resourceMap.size(); i++) {
                queryMap = new HashMap<String, Object>();
                queryMap.put("jtle.JobEventTypeId", 69);
                queryMap.put("jtle.Title", "Quote Job Approved Notification%");
                queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"Manager has been notified of the approved quote job\"}");
                queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
                displayAssertionMessage(queryMap);
                assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, numberOfQuotes, createdOn);
                assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            }
            return;

        case "Quote Job Approved Notification Senior Manager":
            resourceMap = dbHelperQuotes.getQuoteResources(testData.getInt("jobReference"), "Approved");
            numberOfQuotes = resourceMap.size();
            for (int i = 0; i < resourceMap.size(); i++) {
                queryMap = new HashMap<String, Object>();
                queryMap.put("jtle.JobEventTypeId", 69);
                queryMap.put("jtle.Title", "Quote Job Approved Notification%");
                queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"Senior Manager has been notified of the approved quote job\"}");
                queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
                displayAssertionMessage(queryMap);
                assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, numberOfQuotes, createdOn);
                assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            }
            return;

        case "Quote Query Response Notification":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", eventType + "%");
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"A quote query response from % has been submitted for review - Response: %\"}");
            queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Requires Final Approval":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 106);
            queryMap.put("jtle.Title", eventType + "%");
            queryMap.put("jtle.Detail1", "{\"Quote final approver\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Rejected":
            List<Map<String, Object>> resourcesReject = testData.getListMap("resources");
            for (int i = 0; i < resourcesReject.size(); i++) {
                if ("Rejected".equalsIgnoreCase(resourcesReject.get(i).get("ApprovalStatusName").toString())) {
                    String resourceName = resourcesReject.get(i).get("ResourceName").toString().split("\\(")[0].trim();
                    queryMap = new HashMap<String, Object>();
                    queryMap.put("jtle.JobEventTypeId", 107);
                    queryMap.put("jtle.Detail1", "{\"Quote rejected for \":\"" + resourceName + "%" + "\"}");
                    queryMap.put("jtle.Detail2", "{\"Quote rejection reason - %\"}");
                    queryMap.put("jtle.Title", eventType + "%");
                    displayAssertionMessage(queryMap);
                    assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                    assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
                }
            }
            break;

        case "Quote Rejection email sent to":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 123);
            queryMap.put("jtle.Detail1", "{\"Email sent to \":\"%\"}");
            queryMap.put("jtle.Title", eventType + "%");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Quote Rejected Notification":
            resources = new ArrayList<>();
            resources = testData.getArray("resources");
            List<Map<String, Object>> resourcesRejectMotif = testData.getListMap("resources");
            for (int i = 0; i < resources.size(); i++) {
                if ("Rejected".equalsIgnoreCase(resourcesRejectMotif.get(i).get("ApprovalStatusName").toString())) {
                    String resourceName = resourcesRejectMotif.get(i).get("ResourceName").toString().split("\\(")[0].trim();
                    queryMap = new HashMap<String, Object>();
                    queryMap.put("jtle.JobEventTypeId", 69);
                    queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"A quote from " + resourceName + "%" + " has been rejected\"}");
                    queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
                    queryMap.put("jtle.Title", eventType + "%");
                    displayAssertionMessage(queryMap);
                    assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                    assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
                }
            }
            break;

        case "Quote Submitted Notification":
            // AwaitingApproval
            List<Map<String, Object>> resourcesSubmittedNotif = testData.getListMap("resources");
            for (int i = 0; i < resourcesSubmittedNotif.size(); i++) {
                if ("AwaitingApproval".equalsIgnoreCase(resourcesSubmittedNotif.get(i).get("ApprovalStatusName").toString())) {
                    queryMap = new HashMap<String, Object>();
                    queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"A quote from % has been submitted for approval\"}");
                    queryMap.put("jtle.JobEventTypeId", 69);
                    queryMap.put("jtle.Title", eventType);
                    displayAssertionMessage(queryMap);
                    assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                    assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
                }
            }
            break;

        case "Resource declined invitation to quote":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 73);
            queryMap.put("jtle.Title", eventType);
            queryMap.put("jtle.Detail1", "{\"Resource\":\"%\",\"Reason\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Resources Invited to Quote":
            List<Map<String, Object>> resourcesITQ = testData.getListMap("resources");
            for (int i = 0; i < resourcesITQ.size(); i++) {
                if ("ItqAwaitingAcceptance".equalsIgnoreCase(resourcesITQ.get(i).get("ApprovalStatusName").toString())) {
                    String resourceName = resourcesITQ.get(i).get("ResourceName").toString().split("\\(")[0].trim();
                    queryMap = new HashMap<String, Object>();
                    queryMap.put("jtle.JobEventTypeId", 72);
                    queryMap.put("jtle.Title", eventType);
                    queryMap.put("jtle.Detail1", "{\"Job quote invitation\":\"A Quote Job has been raised by % to be sent to %" + resourceName + "%\"}");
                    displayAssertionMessage(queryMap);
                    assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
                    assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
                }
            }
            break;

        case "Resource responded to query":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 77);
            queryMap.put("jtle.Title", eventType + "%");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Update Returning ETA Notification":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 69);
            queryMap.put("jtle.Title", eventType);
            queryMap.put("jtle.Detail1", "{\"Job resource notified\":\"A notification has been issued to % requesting their ETA to be updated after their parts request was rejected.\"}");
            queryMap.put("jtle.Detail2", "{\"Recipient(s)\":\"%\"}");
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Job Completed":
        case "Job linked to Quote Job":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        case "Works Order Issued":
            queryMap = new HashMap<String, Object>();
            queryMap.put("jtle.JobEventTypeId", 134);
            queryMap.put("jtle.Title", eventType);
            displayAssertionMessage(queryMap);
            assertDatabaseCommon = new AssertDatabaseCommon(jobTimelineEventDao, testData.getInt("jobReference"), queryMap, createdOn);
            assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
            break;

        default:
            throw new Exception("Unexpected time line event type " + eventType);
        }

    }

    @ContinueNextStepsOnException
    @Then("^the Resource Assignment table has been updated with the status \"([^\"]*)\"$")
    public void the_ResourceAssignment_table_has_been_updated_with_status(String status) throws Exception {
        String createdOn = runtimeState.timestamp;
        Map<String, Object> queryMap;
        switch (status) {
        case "Awaiting Parts":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 10);
            queryMap.put("ra.ReasonId", 18);
            queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;
        case "Awaiting Parts Review":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 22);
            queryMap.put("ra.ReasonId", 18);
            queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;

        case "Call Required":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceId", testData.getInt("reallocatedResourceId"));
            queryMap.put("ra.ResourceAssignmentStatusId", 5);
            break;

        case "Call Required - Notified by Email":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceId", testData.getInt("reallocatedResourceId"));
            queryMap.put("ra.ResourceAssignmentStatusId", 5);
            queryMap.put("ra.ReasonId", 87);
            break;

        case "Call Required - Notified by SMS":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceId", testData.getInt("reallocatedResourceId"));
            queryMap.put("ra.ResourceAssignmentStatusId", 5);
            queryMap.put("ra.ReasonId", 77);
            break;

        case "Complete":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 13);
            queryMap.put("ra.ReasonId", 18);
            queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;

        case "Declined":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 16);
            queryMap.put("ra.ReasonId", testData.getInt("declineReasonId"));
            queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;
        case "DeclinedInvitationToQuote":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 15);
            queryMap.put("ra.ReasonId", 18);
            queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;

        case "ETA Advised To Site":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 8);
            queryMap.put("ra.ReasonId", 18);
            queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;
        case "ETA Provided":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 7);
            queryMap.put("ra.ReasonId", 18);
            queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;

        case "Funding Request Rejected":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 20);
            queryMap.put("ra.ReasonId", 15);
            break;

        case "Funding Request Rejected - Request a Quote":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 20);
            queryMap.put("ra.ReasonId", 30);
            break;

        case "New Job Notification Sent":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceId", testData.getInt("reallocatedResourceId"));
            queryMap.put("ra.ResourceAssignmentStatusId", 4);
            // queryMap.put("ra.ReasonId", 31); // dont care about method that notification was sent (ipad / email / sms)
            break;

        case "New Job Notification Sent - Initial Funding Request":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", 4);
            queryMap.put("ra.ReasonId", 87);
            // queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;

        case "New Job Notification Sent - Multiple":
            queryMap = new HashMap<String, Object>();
            List<Map<String, Object>> resources = dbHelperQuotes.getQuoteResources(testData.getInt("jobReference"), "Approved");
            int reasonId = 0;
            for (int i = 0; i < resources.size(); i++) {
                int resourceId = dbHelperResource.getHelpdeskResourceId(Integer.valueOf(resources.get(i).get("ResourceId").toString()));
                queryMap.put("ra.ResourceId", resourceId);
                // reasonIds : 24 - Push Notification, 31 - SMS and Push Notification, 77 - SMS notification, 87 - Email Notification
                // For contractor resource, resourceassignment status would be 'New job Notification Sent'
                if (dbHelperResource.getResourceProfileName(resourceId).equalsIgnoreCase("Contractor")) {
                    queryMap.put("ra.ResourceAssignmentStatusId", 4);
                    reasonId = 87;
                } else {
                    // If the resource has ipad, then resourceassignment status would be 'New job Notification Sent' else it would be 'Call Required'
                    if (dbHelperResource.getResourceNotificationMethodType(resourceId) == null) {
                        reasonId = dbHelperResource.resourceHasMobilePhone(resourceId) ? 77 : 87;
                        queryMap.put("ra.ResourceAssignmentStatusId", 5);
                    } else {
                        reasonId = dbHelperResource.resourceHasMobilePhone(resourceId) ? 31 : 24;
                        queryMap.put("ra.ResourceAssignmentStatusId", 4);
                    }
                }
                if (propertyHelper.getEnv().contains("UAT")) {
                    queryMap.put("ra.ReasonId", reasonId);
                }
                displayAssertionMessage(queryMap);
                await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> resourceAssignmentDao.getRecordCount(testData.getInt("jobReference"), queryMap, createdOn), equalTo(1));
            }
            return;

        case "New Job Notification Sent - Bypass Review":
            return;

        case "Returning":
            queryMap = new HashMap<String, Object>();
            queryMap.put("ra.ResourceAssignmentStatusId", RETURNING_RESOURCE_ASSIGNMENT_STATUS);
            queryMap.put("ra.ReasonId", RETURNING_RESOURCE_ASSIGNMENT_REASON);
            queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
            break;

        default:
            throw new Exception("Cannot find resource status " + status);
        }

        displayAssertionMessage(queryMap);

        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> resourceAssignmentDao.getRecordCount(testData.getInt("jobReference"), queryMap, createdOn), equalTo(1));
    }

    @ContinueNextStepsOnException
    @Then("^the Quote tables have been updated with the \"([^\"]*)\"$")
    public void the_quote_tables_have_been_updated_with_the(String status) throws Throwable {
        String createdOn = runtimeState.timestamp;
        Map<String, Object> queryMap;
        switch (status) {
        case "RFM Approval Notes":
            queryMap = new HashMap<String, Object>();
            queryMap.put("qr.fld_str_Notes", testData.getString("RFMApprovalNotes"));
            break;
        case "Senior Manager Notes":
            queryMap = new HashMap<String, Object>();
            queryMap.put("qry.fld_str_QueryText", testData.getString("SeniorManagerNotes"));
            break;
        default:
            throw new Exception("Cannot find resource status " + status);
        }

        displayAssertionMessage(queryMap);

        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> quoteDao.getRecordCount(testData.getInt("jobReference"), queryMap, createdOn), equalTo(1));
    }

    // the Resource has been marked as "Complete" for the job
    // the Job has been marked as "Fixed"
    // Not modified as part of MTA-117 but needs cleaned up
    @ContinueNextStepsOnException
    @Then("^the Resource has been marked as \"([^\"]*)\" for the job$")
    public void the_job_has_been_marked_as(String resourceStatus) throws Exception {
        ResourceAssignment resourceAssignment;
        switch (resourceStatus) {
        case "Complete":
            // assertMessageRecord(job.getJobReference(), COMPLETE_ROUTING_KEY, COMPLETE_EVENT_TYPE, COMPLETE_RESOURCE_ASSIGNMENT_STATUS, COMPLETE_RESOURCE_ASSIGNMENT_REASON);
            break;
        case "Returning":
            // assertMessageRecord(job.getJobReference(), RETURNING_ROUTING_KEY, RETURNING_EVENT_TYPE, RETURNING_RESOURCE_ASSIGNMENT_STATUS, RETURNING_RESOURCE_ASSIGNMENT_REASON);
            resourceAssignment = resourceAssignmentDao.getAssigmentByStatusReason(testData.getInt("jobReference"), testData.getInt("resourceId"), RETURNING_RESOURCE_ASSIGNMENT_STATUS, RETURNING_RESOURCE_ASSIGNMENT_REASON);
            assertNotNull("Unexpected null record set", resourceAssignment);
            break;
        case "DeclinedInvitationToQuote":
            resourceAssignment = resourceAssignmentDao.getAssigmentByJobReference(testData.getInt("jobReference"));
            assertNull("Unexpected record set", resourceAssignment);
            break;
        default:
            throw new Exception("Cannot find resource status " + resourceStatus);
        }
    }

    // TODO : Fix this
    /**
     * This method needs to be refactored. ppmJobDao is too specific for this step.
     *
     * @param resourceStatus
     * @throws Throwable
     */
    @ContinueNextStepsOnException
    @Then("^the Job is updated with a \"([^\"]*)\" resource status$")
    public void the_job_is_updated_with_a_resource_status(String resourceStatus) throws Throwable {
        AssertPPMResourceStatus assertJobStatus = new AssertPPMResourceStatus(ppmJobDao, testData.getInt("jobReference"), resourceStatus);
        assertionFactory.performAssertion(assertJobStatus);
    }

    @ContinueNextStepsOnException
    @Then("^a reactive job is created which is linked to the PPM job$")
    public void a_reactive_job_is_created_which_is_linked_to_the_ppm_job() throws Throwable {
        AssertPPMJobId assertPPMJobId = new AssertPPMJobId(jobDao, testData.getInt("linkedJobReference"), testData.getInt("jobReference"));
        assertionFactory.performAssertion(assertPPMJobId);
    }

    @ContinueNextStepsOnException
    @Then("^the access failed count is updated$")
    public void access_failed_count_is_updated() throws Throwable {
        runtimeState.scenario.write("Asserting lockout count for : " + testData.getString("userName") + " = " + testData.getInt("accessFailedCount"));
        dbHelperAssertions.applicatonUserLockout(testData.getString("userName"), testData.getInt("accessFailedCount"));
    }

    // TODO : Fix this
    /**
     * Reactive job?? More like quote job
     *
     * @param quoteType
     * @throws Throwable
     */
    @ContinueNextStepsOnException
    @Then("^the reactive job created is a \"([^\"]*)\" job$")
    public void the_reactive_job_created_is_a_job(String quoteType) throws Throwable {
        logger.debug("Get the job");
        logger.debug(testData.getInt("jobReference"));
        Integer fundingRouteId = Integer.valueOf(dbHelper.getJobFundingRouteId(testData.getInt("linkedJobReference")));
        switch (quoteType) {
        case "OPEX":
            assertEquals(fundingRouteId, FUNDINGROUTE_ID_OPEX);
            break;
        case "CAPEX":
            assertEquals(fundingRouteId, FUNDINGROUTE_ID_CAPEX);
            break;
        case "BMI":
            assertEquals(fundingRouteId, FUNDINGROUTE_ID_BMI);
            break;
        default:
            throw new Exception("Cannot find quote type " + quoteType);
        }
    }

    @ContinueNextStepsOnException
    @Then("^the reactive job has a \"([^\"]*)\" assignment status$")
    public void the_reactive_job_has_a_assignment_status(String Assignment) throws Throwable {
        AssertDatabaseCommon assertDatabaseCommon;
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("ra.ResourceId", testData.getInt("resourceId"));
        Integer expectedCount = ("Assigned To Me".equalsIgnoreCase(Assignment)) ? 1 : 0;
        String createdOn = runtimeState.timestamp;
        assertDatabaseCommon = new AssertDatabaseCommon(resourceAssignmentDao, testData.getInt("linkedJobReference"), queryMap, expectedCount, createdOn);
        assertionFactory.performAssertion(assertDatabaseCommon, waitTime);
    }

    public void the_quote_is_in_stage(String quoteJobApprovalStatus, String approvalStatus) throws Throwable {
        dbHelperAssertions.quoteIsInStage(testData.getInt("jobReference"), quoteJobApprovalStatus, approvalStatus);
    }

    public void the_quote_is_in_stage(Integer quoteJobApprovalStatusId, Integer approvalStatusId) throws Throwable {
        dbHelperAssertions.quoteIsInStage(testData.getInt("jobReference"), quoteJobApprovalStatusId, approvalStatusId);
    }

    private void gasUsageVersion1Recorded() throws Throwable {
        int jobReference = testData.getInt("jobReference");

        if (testData.getString("gasType") != null) {
            Integer expectedGasType = dbHelperGas.getGasTypeIdForName(testData.getString("gasType"));
            runtimeState.scenario.write("Asserting gastType is: " + testData.getString("gasType"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasTypeId(), is(expectedGasType));
        }

        if (testData.getString("gasLeakageCode") != null) {
            Integer expectedLeakCode = dbHelperGas.getGasLeakageCodeForName(testData.getString("gasLeakageCode"));
            runtimeState.scenario.write("Asserting gasLeakageCode is: " + testData.getString("gasLeakageCode"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasLeakageCodeId(), is(expectedLeakCode));
        }

        if (testData.getString("gasLeakageCheckMethod") != null) {
            Integer expectedLeakageCheckMethodCode = dbHelperGas.getGasLeakageCheckMethodForName(testData.getString("gasLeakageCheckMethod"));
            runtimeState.scenario.write("Asserting gasLeakageCheckMethod is: " + testData.getString("gasLeakageCheckMethod") + " (" + expectedLeakageCheckMethodCode + ")");
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasLeakageCheckMethodId(), is(expectedLeakageCheckMethodCode));
        }

        if (testData.getString("gasLeakLocation") != null) {
            Integer expectedLeakLocationCode = dbHelperGas.getGasLeakLocationForName(testData.getString("gasLeakLocation"));
            runtimeState.scenario.write("Asserting gasLeakLocation is: " + testData.getString("gasLeakLocation") + " (" + expectedLeakLocationCode + ")");
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasLeakLocationId(), is(expectedLeakLocationCode));
        }

        if (testData.getString("gasAction") != null) {
            Integer expectedGasActionCode = reasonDao.getReasonByname(testData.getString("gasAction"), "UsesGasAction").getId();
            runtimeState.scenario.write("Asserting gasAction is: " + testData.getString("gasAction") + " (" + expectedGasActionCode + ")");
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasActionReasonId(), is(expectedGasActionCode));
        }

        if (testData.getString("gasFaultCode") != null) {
            Integer expectedGasFaultCode = reasonDao.getReasonByname(testData.getString("gasFaultCode"), "UsesGasFaultCode").getId();
            runtimeState.scenario.write("Asserting gasFaultCode is: " + testData.getString("gasFaultCode") + " (" + expectedGasFaultCode + ")");
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasFaultCodeReasonId(), is(expectedGasFaultCode));
        }

        if (testData.getMap("gasAmountUsed") != null) {
            List<SiteVisitsGasUsage> gasUsages = siteVisitsGasUsageDao.getForJobReference(testData.getInt("jobReference"));

            Map<String, Object> gasAmounts = testData.getMap("gasAmountUsed");
            for (String bottleNumber : gasAmounts.keySet()) {
                float bottleQty = (float) gasAmounts.get(bottleNumber);
                runtimeState.scenario.write("Asserting Bottle: " + bottleNumber + " = " + bottleQty);

                boolean found = false;
                for (SiteVisitsGasUsage gasUsage : gasUsages) {
                    if (gasUsage.getBottleNumber().equals(bottleNumber) && gasUsage.getBottleQuantity() == bottleQty) {
                        found = true;
                    }
                }
                assertTrue("Expected SiteVisitsGasUsage to have bottleNumber  " + bottleNumber + " and bottleQuantity " + bottleQty, found);
            }
        }

        if (testData.getString("gasTypeId") != null) {
            String expectedGasTypeName = ("Other".equalsIgnoreCase(testData.getString("gasTypeId"))) ? testData.getString("gasTypeOtherId") : testData.getString("gasTypeId");
            Integer expectedGasType = dbHelperGas.getGasTypeIdForName(expectedGasTypeName);
            runtimeState.scenario.write("Asserting gasTypeId is: ".concat(expectedGasTypeName));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasTypeId(), is(expectedGasType));
        }

        if (testData.getString("gasLeakageCodeId") != null) {
            Integer expectedLeakCode = dbHelperGas.getGasLeakageCodeForName(testData.getString("gasLeakageCodeId"));
            runtimeState.scenario.write("Asserting gasLeakageCodeId is: " + testData.getString("gasLeakageCodeId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasLeakageCodeId(), is(expectedLeakCode));
        }

        if (testData.getString("gasLeakageCheckMethodId") != null) {
            Integer expectedLeakageCheckMethodCode = dbHelperGas.getGasLeakageCheckMethodForName(testData.getString("gasLeakageCheckMethodId"));
            runtimeState.scenario.write("Asserting gasLeakageCheckMethodId is: " + testData.getString("gasLeakageCheckMethodId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasLeakageCheckMethodId(), is(expectedLeakageCheckMethodCode));
        }

        if (testData.getString("gasLeakLocationId") != null) {
            Integer expectedLeakLocationCode = dbHelperGas.getGasLeakLocationForName(testData.getString("gasLeakLocationId"));
            runtimeState.scenario.write("Asserting gasLeakLocationId is: " + testData.getString("gasLeakLocationId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasLeakLocationId(), is(expectedLeakLocationCode));
        }

        if (testData.getString("gasActionId") != null) {
            Integer expectedGasActionCode = reasonDao.getReasonByname(testData.getString("gasActionId"), "UsesGasAction").getId();
            runtimeState.scenario.write("Asserting gasActionId is: " + testData.getString("gasActionId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasActionReasonId(), is(expectedGasActionCode));
        }

        if (testData.getString("gasFaultCodeId") != null) {
            Integer expectedGasFaultCode = reasonDao.getReasonByname(testData.getString("gasFaultCodeId"), "UsesGasFaultCode").getId();
            runtimeState.scenario.write("Asserting gasFaultCodeId is: " + testData.getString("gasFaultCodeId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitsDao.getLatestSiteVisitForJobReference(jobReference).getGasFaultCodeReasonId(), is(expectedGasFaultCode));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Gas Leak Check Questions have been recorded$")
    public void the_leak_check_questions_has_been_recorded() throws Throwable {
        int jobReference = testData.getInt("jobReference");
        String createdOn = runtimeState.timestamp;

        if (testData.getString("GasLeakCheckMethodId") != null) {
            Integer expectedGasLeakCheckMethodCode = dbHelperGas.getGasLeakCheckMethodForName(testData.getString("GasLeakCheckMethodId"));
            runtimeState.scenario.write("Asserting gasLeakCheckMethodId is: " + testData.getString("GasLeakCheckMethodId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getGasLeakCheckMethodId(), is(expectedGasLeakCheckMethodCode));
        }

        if (testData.getString("GasLeakCheckResultTypeId") != null) {
            Integer expectedGasLeakCheckResultCode = dbHelperGas.getGasLeakCheckResultForName(testData.getString("GasLeakCheckResultTypeId"));
            runtimeState.scenario.write("Asserting gasLeakCheckResultTypeId is: " + testData.getString("GasLeakCheckResultTypeId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getGasLeakCheckResultTypeId(), is(expectedGasLeakCheckResultCode));
        }

        if (testData.getString("GasLeakCheckStatusId") != null) {
            Integer expectedGasLeakCheckStatusCode = dbHelperGas.getGasLeakCheckStatusForName(testData.getString("GasLeakCheckStatusId"));
            runtimeState.scenario.write("Asserting gasLeakCheckStatusId is: " + testData.getString("GasLeakCheckStatusId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getGasLeakCheckStatusId(), is(expectedGasLeakCheckStatusCode));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Leak Site Information Questions have been recorded$")
    public void the_leak_site_information_questions_has_been_recorded() throws Throwable {
        int jobReference = testData.getInt("jobReference");

        // leak site checks
        List<String> primaryComponents = testData.getList("FGAS_primaryComponents", String.class);
        List<String> primaryComponentsInfo = testData.getList("FGAS_primaryComponentsInfo", String.class);
        List<String> subComponents = testData.getList("FGAS_subComponents", String.class);
        List<String> leakSiteStatuses = testData.getList("FGAS_leakSiteStatuses", String.class);
        List<String> initialTests = testData.getList("FGAS_initialTests", String.class);
        List<String> followUpTests = testData.getList("FGAS_followUpTests", String.class);

        for (int index = 0; index < primaryComponents.size(); index++) {
            String gasLeakLocationName = primaryComponents.get(index);
            String gasLeakSubLocationName = subComponents.get(index);
            String gasLeakSiteStatusName = leakSiteStatuses.get(index);
            String gasLeakInitialTestName = initialTests.get(index);
            String gasLeakFollowUpTestName = followUpTests.get(index);
            String primaryComponentInformation = primaryComponentsInfo.get(index);

            runtimeState.scenario.write("Asserting Leak Location is: " + gasLeakLocationName);
            runtimeState.scenario.write("Asserting Leak Sub Location is: " + gasLeakSubLocationName);
            runtimeState.scenario.write("Asserting Leak Site Status is: " + gasLeakSiteStatusName);
            runtimeState.scenario.write("Asserting Initial Verification Test is: " + gasLeakInitialTestName);
            runtimeState.scenario.write("Asserting Follow Up Verification Test is: " + gasLeakFollowUpTestName);
            runtimeState.scenario.write("Asserting Primary Component Information is: " + primaryComponentInformation);

            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(
                    () -> siteVisitGasLeakSiteCheckDao
                    .getRecordCount(jobReference, gasLeakLocationName, gasLeakSubLocationName, gasLeakSiteStatusName, gasLeakInitialTestName, gasLeakFollowUpTestName, primaryComponentInformation),
                    equalTo(1));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Gas Appliance Questions have been recorded$")
    public void the_gas_appliance_questions_have_been_recorded() throws Throwable {
        int jobReference = testData.getInt("jobReference");
        String createdOn = runtimeState.timestamp;

        if (testData.getString("applianceTypeId") != null) {
            Integer expectedApplianceTypeId = dbHelperGas.getApplianceTypeForName(testData.getString("applianceTypeId"));
            runtimeState.scenario.write("Asserting Appliance Type applianceTypeId is: " + testData.getString("applianceTypeId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getGasApplianceTypeId(), is(expectedApplianceTypeId));
        }

        if (testData.getString("applianceIdentificationId") != null) {
            Integer expectedApplianceIdentificationId = dbHelperGas.getAssetId(testData.getString("applianceIdentificationId"), testData.getString("siteName"));
            runtimeState.scenario.write("Asserting Appliance Identification applianceTypeId is: " + testData.getString("applianceIdentificationId"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getAssetId(), is(expectedApplianceIdentificationId));
        }

        if (testData.getString("ApplianceInformation") != null) {
            // Integer expectedApplianceTypeId = dbHelperGas.getGasLeakCheckMethodForName(testData.getString("applianceTypeId"));
            runtimeState.scenario.write("Asserting Appliance Type applianceTypeId is: " + testData.getString("ApplianceInformation"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getApplianceInformation(), is(testData.getString("ApplianceInformation")));
        }

        if (testData.getString("ReceiverLevelRecorded") != null) {
            Boolean ReceiverLevelRecorded = testData.getString("ReceiverLevelRecorded").equalsIgnoreCase("Yes") ? true : false;
            runtimeState.scenario.write("Asserting Receiver Level Recorded is: " + testData.getString("ReceiverLevelRecorded"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).isReceiverLevelRecorded(), is(ReceiverLevelRecorded));
        }

        if (testData.getString("QuantityOfBallsFloating") != null) {
            // Integer expectedApplianceTypeId = dbHelperGas.getGasLeakCheckMethodForName(testData.getString("applianceTypeId"));
            runtimeState.scenario.write("Asserting QuantityOfBallsFloating is: " + testData.getString("QuantityOfBallsFloating"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getQuantityOfBallsFloating(), is(testData.getString("QuantityOfBallsFloating")));
        }

        if (testData.getString("LevelIndicator") != null) {
            // Integer expectedApplianceTypeId = dbHelperGas.getGasLeakCheckMethodForName(testData.getString("applianceTypeId"));
            runtimeState.scenario.write("Asserting LevelIndicator is: " + testData.getString("LevelIndicator"));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getLevelIndicator(), is(testData.getString("LevelIndicator")));
        }

        if (testData.getString("gasTypeId") != null) {
            String expectedGasTypeName = ("Incorrect refrigerant type displayed".equalsIgnoreCase(testData.getString("gasTypeId"))) ? testData.getString("gasTypeOtherId") : testData.getString("gasTypeId");
            Integer expectedGasType = dbHelperGas.getGasTypeIdForName(expectedGasTypeName);
            runtimeState.scenario.write("Asserting gasTypeId is: ".concat(expectedGasTypeName));
            await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitGasDetailsDao.getSiteVisitGasDetailsForJobReference(jobReference, createdOn).getGasTypeId(), is(expectedGasType));
        }
    }

    public void the_refrigerant_gas_usage_has_been_saved_check_row(int jobReference, Map<String, Object> map) throws Throwable {

        boolean type1 = map.get("lbsInstalled") != null ? true : false;

        Object refrigerantSourceID = type1 ? map.get("refrigerantSourceID") : map.get("Refrigerant Source");
        Object lbsInstalled = type1 ? map.get("lbsInstalled") : map.get(setWeightLabel("Gas Installed (%s)"));
        Object gasCylinderTypeId = type1 ? map.get("gasCylinderTypeId") : map.get("Type of Cylinder");
        Object refrigerantSourceLocation = type1 ? map.get("RefrigerantSourceLocation") : map.get("Refrigerant Source Location");
        Object fullOrPartialCylinder = type1 ? map.get("IsPartialCylinder") : map.get("Full or Partial Cylinder");
        Object gasCylinderSerialNo = type1 ? map.get("gasCylinderSerialNo") : map.get("Cylinder Serial No");
        Object gasPoundsInCylinderID = type1 ? map.get("gasPoundsInCylinderID") : map.get(setWeightLabel("Cylinder Available (%s)"));

        Integer expectedrefrigerantSourceID = dbHelperGas.getGasSourceTypeIdForName(refrigerantSourceID.toString());
        BigDecimal bottleQuantity = new BigDecimal(lbsInstalled.toString());
        Integer expectedGasCylinderTypeId = dbHelperGas.getGasCylinderTypeIdForName(gasCylinderTypeId.toString());
        String createdOn = runtimeState.timestamp;

        await().atMost(MAX_TIMEOUT, SECONDS).until(() -> siteVisitCylinderDetailsDao.getSiteVisitsCylinderDetailsForJobReference(jobReference), notNullValue());

        SiteVisitCylinderDetails siteVisitCylinderDetails = siteVisitCylinderDetailsDao.getSiteVisitsCylinderDetailsForJobReference(jobReference, expectedrefrigerantSourceID, expectedGasCylinderTypeId, bottleQuantity, createdOn);
        assertNotNull("Unexpected null siteVisitCylinderDetails object : Cannot find cylinder details in the database", siteVisitCylinderDetails);

        if (refrigerantSourceID != null) {
            Integer expectedRefrigerantSourceId = dbHelperGas.getGasSourceTypeIdForName(refrigerantSourceID.toString());
            runtimeState.scenario.write("Asserting Refrigerant : ".concat(expectedRefrigerantSourceId.toString()).concat(" : ").concat(refrigerantSourceID.toString()));
            assertEquals("Unexpected Refrigerant Source. ", expectedRefrigerantSourceId, siteVisitCylinderDetails.getGasSourceTypeId());
        }

        if (refrigerantSourceLocation != null) {
            runtimeState.scenario.write("Asserting Refrigerant Source Location is: " + refrigerantSourceLocation);
            assertEquals("Unexpected Refrigerant Source Location / Off-Site Location. ", refrigerantSourceLocation, siteVisitCylinderDetails.getRefrigerantSourceLocation());
        }

        if (fullOrPartialCylinder != null) {
            Boolean isPartialCylinder = fullOrPartialCylinder.toString().equalsIgnoreCase("Partial");
            runtimeState.scenario.write("Asserting Full or Partial Cylinder is: ".concat(fullOrPartialCylinder.toString()));
            assertEquals("Unexpected IsPartialCylinder. ", isPartialCylinder, siteVisitCylinderDetails.isPartialCylinder());
        }

        if (gasCylinderTypeId != null) {
            runtimeState.scenario.write("Asserting Type of Cylinder is: ".concat(expectedGasCylinderTypeId.toString()).concat(" : ").concat(gasCylinderTypeId.toString()));
            assertEquals("Unexpected Type of Cylinder. ", expectedGasCylinderTypeId, siteVisitCylinderDetails.getGasCylinderTypeId());
        }

        if (gasCylinderSerialNo != null) {
            runtimeState.scenario.write("Asserting Cylinder serial number is: " + gasCylinderSerialNo);
            assertEquals("Unexpected Cylinder Serial number. ", gasCylinderSerialNo, siteVisitCylinderDetails.getBottleNumber());
        }

        if (gasPoundsInCylinderID != null) {
            runtimeState.scenario.write("Asserting Pounds in Cylinder is: " + gasPoundsInCylinderID);
            int scale = "en-US".equals(LOCALE) ? 2 : 1; // UKRB Stores to 1 decimal place, USWM 2
            Double intialQuantity = new BigDecimal(gasPoundsInCylinderID.toString()).setScale(scale, RoundingMode.CEILING).doubleValue();
            assertEquals("Unexpected Pounds in Cylinder. ", intialQuantity, siteVisitCylinderDetails.getInitialQuantity());
        }

        if (lbsInstalled != null) {
            runtimeState.scenario.write("Asserting lbsInstalled is: " + lbsInstalled);
            int scale = "en-US".equals(LOCALE) ? 2 : 1; // UKRB Stores to 1 decimal place, USWM 2
            Double bottleQuantityInstalled = new BigDecimal(lbsInstalled.toString()).setScale(scale, RoundingMode.CEILING).doubleValue();
            assertEquals("Unexpected lbs Installed. ", bottleQuantityInstalled, siteVisitCylinderDetails.getBottleQuantity());
        }

        // Check Surplus information if required
        boolean isSurplus = map.get("GasSurplusDestinationId") != null || map.get("Surplus Type") != null ? true : false;
        if (isSurplus) {
            Object gasSurplusDestinationId = type1 ? map.get("GasSurplusDestinationId") : map.get("Destination");
            Object gasSurplusTypeId = type1 ? map.get("GasSurplusTypeId") : map.get("Surplus Type");
            Object returnedTo = type1 ? map.get("ReturnedTo") : map.get("Returned To");

            if (gasSurplusDestinationId != null) {
                Integer expectedGasSurplusDestinationId = dbHelperGas.getGasSurplusDestinationIdForName(gasSurplusDestinationId.toString());
                runtimeState.scenario.write("Asserting GasSurplusDestinationId is: ".concat(expectedGasSurplusDestinationId.toString()));
                assertEquals("Unexpected GasSurplusDestinationId. ", expectedGasSurplusDestinationId, siteVisitCylinderDetails.getGasSurplusDestinationId());
            }

            if (gasSurplusTypeId != null) {
                Integer expectedGasSurplusTypeId = dbHelperGas.getGasSurplusTypeIdForName(gasSurplusTypeId.toString());
                runtimeState.scenario.write("Asserting GasSurplusTypeId is: ".concat(expectedGasSurplusTypeId.toString()));
                assertEquals("Unexpected GasSurplusDestinationId. ", expectedGasSurplusTypeId, siteVisitCylinderDetails.getGasSurplusTypeId());
            }

            if (returnedTo != null) {
                runtimeState.scenario.write("Asserting Returned To is: " + returnedTo);
                assertEquals("Unexpected GasSurplusDestinationId. ", returnedTo, siteVisitCylinderDetails.getReturnedTo());
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ContinueNextStepsOnException
    @Then("^the Refrigerant Gas Usage has been recorded$")
    public void the_refrigerant_gas_usage_has_been_saved() throws Throwable {
        int jobReference = testData.getInt("jobReference");

        List<LinkedHashMap<String, Object>> leakSiteInformation = testData.getLinkedListMap("refrigerantGasUsage");

        if (leakSiteInformation != null) {
            Iterator it = leakSiteInformation.iterator();

            while (it.hasNext()) {
                Map<String, Object> map = (Map<String, Object>) it.next();

                the_refrigerant_gas_usage_has_been_saved_check_row(jobReference, map);
            }
        }
    }

    /**
     * Run all the database verification checks to ensure the database has been updated correctly
     *
     * @throws Throwable
     */
    private void gasUsageEPA2019Recorded() throws Throwable {
        the_refrigerant_gas_usage_has_been_saved();
        the_leak_site_information_questions_has_been_recorded();
        the_gas_appliance_questions_have_been_recorded();
        the_leak_check_questions_has_been_recorded();
    }

    @ContinueNextStepsOnException
    @Then("^the Site Visit Gas Usage and Leak Checks have been recorded$")
    public void the_gas_usage_has_been_recorded() throws Throwable {
        // check what toggles are set then click the appropriate answer
        String fgasRegulations = testData.getString("fgasRegulations");
        boolean epa2019;
        if (fgasRegulations != null) {
            // if the site visit has been updated on the Admin page then it doesn't matter what the toggles are set to - the original site visit drives the regulations
            epa2019 = "2019".equals(fgasRegulations) ? true : false;
        } else {
            epa2019 = true;
        }

        if (epa2019) {
            gasUsageEPA2019Recorded();

        } else {
            gasUsageVersion1Recorded();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Timeline Event Summary has been updated with \"([^\"]*)\"$")
    public void the_timeline_event_summery_is_updated(String eventType) throws Throwable {
        Map<String, Object> queryMap;
        logger.debug(testData.toString());
        String details1 = null;
        Boolean hasDetails2 = false;
        String loggedAt = runtimeState.timestamp;
        String impersonatedResourceName = testData.getString("impersonatedResourceName");
        String etaDate = null;
        List<Map<String, Object>> resources;
        boolean sqlLike = false;

        outputHelper.writeEventSumary(testData.getInt("jobReference"));;

        switch (eventType) {
        case "Awaiting Funding Authorization":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 21);
            queryMap.put("es.Title", localize(eventType) + " - " + testData.get("alternativeResource"));
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.ResourceName", testData.get("alternativeResource"));
            break;

        case "Complete":
            hasDetails2 = true;
            details1 = fgasPortalHelper.getFGASDetails1();
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 5);
            queryMap.put("es.Detail1", details1);
            break;

        case "Declined":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 9);
            if (null == impersonatedResourceName) {
                String resourceName = dbHelperResource.getResourceName(testData.getInt("resourceId"));
                queryMap.put("es.Title", "Declined Job - " + resourceName);
                queryMap.put("es.ResourceName", resourceName);
            } else {
                queryMap.put("es.Title", "Declined Job - " + testData.getString("loginUserName"));
                queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
                queryMap.put("es.ResourceName", impersonatedResourceName);
            }
            queryMap.put("es.Reason", "{\"Reason\":\"" + testData.getString("declineReason") + "\"}");
            queryMap.put("es.Notes", "{\"Notes\":\"" + testData.getString("declineNotes") + "\"}");
            break;

        case "Email notification sent to":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 20);
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.Title", "Email notification sent to " + dbHelperResource.getResourceEmailAddresses(testData.getInt("reallocatedResourceId")).get(0) + ". - " + testData.get("reallocatedResourceName"));
            queryMap.put("es.ResourceName", testData.get("reallocatedResourceName"));
            break;

        case "ETA advised to site":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.Title", eventType);
            queryMap.put("es.ResourceAssignmentEventTypeId", 28);
            etaDate = DateHelper.convert(testData.getString("etaDate"), FULL_DATE_FORMAT_STRING, MEDIUM_DATE);

            if (null == impersonatedResourceName) {
                queryMap.put("es.Detail2", "{\"ETA\":\"ST - " + etaDate + " between " + testData.getString("etaWindow") + "\"}");
                queryMap.put("es.ResourceName", testData.getString("resourceName"));
            } else {
                queryMap.put("es.Detail2", "{\"ETA\":\"ST - " + etaDate + " between " + testData.getString("etaWindow") + "\",\"Impersonating\":\"" + impersonatedResourceName + "\"}");
                queryMap.put("es.ResourceName", impersonatedResourceName);
                queryMap.put("es.LoggedBy", testData.get("loginUser") + "%");
                sqlLike = true;
            }
            if (null != testData.getString("advisedTo")) {
                queryMap.put("es.Detail1", "{\"Advised To\":\"" + testData.getString("advisedTo") + "\"}");
            }
            break;

        case "ETA Updated":
            sqlLike = true;
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.Title", eventType + "%");
            queryMap.put("es.ResourceAssignmentEventTypeId", 25);
            break;

        case "Funding Approved":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 23);
            queryMap.put("es.Title", "Funding Approved - " + testData.get("loginUserName"));
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            break;

        case "Funding Declined":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 24);
            queryMap.put("es.Title", "Funding Declined - " + testData.get("loginUserName"));
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            break;

        case "Invoice Approved":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.JobEventTypeId", 130);
            queryMap.put("es.Title", eventType);
            queryMap.put("es.Detail1", "{\"Invoice\":\"" + testData.getString("invoiceNumber") + "\",\"ApprovedBy\":\"" + testData.getString("userName") + "\"%\"}");
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.LoggedBy", testData.get("loginUser") + "%");
            sqlLike = true;
            break;

        case "Job linked to Quote Job":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.JobEventTypeId", 70);
            queryMap.put("es.Title", eventType);
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.LoggedBy", testData.get("loginUser") + "%");
            sqlLike = true;
            break;

        case "Job Provided With ETA For":
            impersonatedResourceName = testData.getString("impersonatedResourceName");
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 4);
            queryMap.put("es.Title", "Job Provided With ETA For " + testData.getString("impersonatedResourceName"));

            etaDate = DateHelper.convert(testData.getString("etaDate"), FULL_DATE_FORMAT_STRING, MEDIUM_DATE);

            if (null == impersonatedResourceName) {
                queryMap.put("es.Detail2", "{\"ETA\":\"ST - " + etaDate + " between " + testData.getString("etaWindow") + "\"}");
                queryMap.put("es.ResourceName", testData.getString("resourceName"));
            } else {
                queryMap.put("es.Detail2", "{\"ETA\":\"ST - " + etaDate + " between " + testData.getString("etaWindow") + "\",\"Impersonating\":\"" + impersonatedResourceName + "\"}");
                queryMap.put("es.ResourceName", impersonatedResourceName);
                queryMap.put("es.LoggedBy", testData.get("loginUser") + "%");
                sqlLike = true;
            }
            break;

        case "Job Type changed to Quote":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.JobEventTypeId", 61);
            queryMap.put("es.Title", eventType);
            if (testData.getString("jobType") != null) {
                queryMap.put("es.Detail1", "{\"Change\":\"Job type changed from " + testData.getString("jobType") + " to Quote\",\"Notes\":null}");
            }
            queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.LoggedBy", testData.get("loginUser") + "%");
            sqlLike = true;
            break;

        case "notification sent":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 20);
            queryMap.put("es.Title", "%notification%sent to%" + testData.getString("reallocatedResourceName") + "%");
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.ResourceName", testData.getString("reallocatedResourceName"));
            sqlLike = true;
            break;

        case "Notification and text message sent to":
            String title = null;
            String phoneNumber = dbHelperResource.getResourcePhoneNumber(testData.getInt("reallocatedResourceId"));
            title = (phoneNumber.isEmpty()) ? "Notification and text message sent to  - " : "Notification and text message sent to " + phoneNumber + " - ";
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 20);
            queryMap.put("es.Title", title + testData.getString("reallocatedResourceName"));
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.ResourceName", testData.getString("reallocatedResourceName"));
            break;

        case "Notification and text message sent to - Bypass Review": // Resources added as the result of a multiquote bypass review by ops director
            List<Map<String, Object>> notifications = testData.getListMap("resources");
            for (int i = 0; i < notifications.size(); i++) {
                if ("Approved".equalsIgnoreCase(notifications.get(i).get("ApprovalStatusName").toString())) {
                    String resourceName = notifications.get(i).get("ResourceName").toString().split("\\(")[0].trim();
                    final Map<String, Object> queryMapBP = new HashMap<String, Object>();
                    queryMapBP.put("es.ResourceAssignmentEventTypeId", 20);
                    queryMapBP.put("es.Title", "Notification and text message sent to - " + resourceName);
                    if (impersonatedResourceName != null) queryMapBP.put("es", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
                    queryMapBP.put("es.IconIdentifier", "tablet");
                    displayAssertionMessage(queryMapBP);
                    await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(30, SECONDS).until(() -> eventSummaryDao.getEventSummary(testData.getInt("jobReference"), queryMapBP, loggedAt), notNullValue());
                }
            }
            return;

        case "Notification and text message sent to - Multiple":
            if (propertyHelper.getEnv().contains("UAT")) {
                resources = dbHelperQuotes.getQuoteResources(testData.getInt("jobReference"), "Approved");
                for (int i = 0; i < resources.size(); i++) {
                    int resourceId = dbHelperResource.getHelpdeskResourceId(Integer.valueOf(resources.get(i).get("ResourceId").toString()));
                    // Notification and text message will be sent only when resource has ipad
                    if (dbHelperResource.getResourceNotificationMethodType(resourceId) != null) {
                        final Map<String, Object> queryMapN = new HashMap<String, Object>();
                        queryMapN.put("es.ResourceAssignmentEventTypeId", 20);
                        title = dbHelperResource.resourceHasMobilePhone(resourceId) ? "Notification and text message sent to%" : "Notification sent to . No text message sent as no number configured%";
                        queryMapN.put("es.Title", title + " - " + resources.get(i).get("ResourceName").toString().trim() + "%");
                        if (impersonatedResourceName != null) queryMapN.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
                        queryMapN.put("es.ResourceName", resources.get(i).get("ResourceName").toString().trim() + "%");
                        displayAssertionMessage(queryMapN);
                        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(30, SECONDS).until(() -> eventSummaryDao.getEventSummaryLike(testData.getInt("jobReference"), queryMapN, loggedAt), notNullValue());
                    }
                }
            }
            return;

        case "On Site":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 6);
            queryMap.put("es.Title", "On Site%");
            sqlLike = true;
            break;

        case "Quote Request Approver Set":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.JobEventTypeId", 68);
            queryMap.put("es.Title", eventType);
            if (impersonatedResourceName != null) {
                queryMap.put("es.Detail1", "{\"Quote request approver\":\"" + impersonatedResourceName + "\"}");
                queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            }
            queryMap.put("es.LoggedBy", testData.get("loginUser") + "%");
            sqlLike = true;
            break;

        case "Quote Request Raised":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.JobEventTypeId", 67);
            queryMap.put("es.Title", eventType);
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.LoggedBy", testData.get("loginUser") + "%");
            sqlLike = true;
            break;

        case "Refrigerant Gas Used":
            hasDetails2 = true;
            details1 = fgasPortalHelper.getFGASDetails1();
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 47);
            queryMap.put("es.Detail1", details1);
            break;

        case "Resource Added - Awaiting Funding Authorization":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 1);
            queryMap.put("es.Title", "Resource Added - " + testData.get("alternativeResource"));
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.Notes", "{\"Notes\":\"Status: Awaiting Funding Authorisation\"}");
            queryMap.put("es.ResourceName", testData.get("alternativeResource"));
            break;

        case "Resource Added - Bypass Review": // Resources added as the result of a multiquote bypass review by ops director
            List<Map<String, Object>> resourcesAdded = testData.getListMap("resources");
            for (int i = 0; i < resourcesAdded.size(); i++) {
                if ("Approved".equalsIgnoreCase(resourcesAdded.get(i).get("ApprovalStatusName").toString())) {
                    String resourceName = resourcesAdded.get(i).get("ResourceName").toString().split("\\(")[0].trim();
                    final Map<String, Object> queryMapRA = new HashMap<String, Object>();
                    queryMapRA.put("es.JobEventTypeId", 63);
                    queryMapRA.put("es.Title", eventType + " - " + resourceName);
                    if (impersonatedResourceName != null) queryMapRA.put("es", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
                    queryMapRA.put("es.Notes", "{\"Notes\":\"Status: New Job Notification Sent\"}");
                    queryMapRA.put("es.IconIdentifier", "wrench");
                    displayAssertionMessage(queryMapRA);
                    await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(30, SECONDS).until(() -> eventSummaryDao.getEventSummary(testData.getInt("jobReference"), queryMapRA, loggedAt), notNullValue());
                }
            }
            return;

        case "Resource Added - New Job Notification Sent":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 1);
            queryMap.put("es.Title", "Resource Added - " + testData.get("alternativeResource") + "%");
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            if ("City Resource".equalsIgnoreCase(testData.getString("reallocatedResourceType")) && dbHelper.isResourceInActiveDirectory(testData.getInt("reallocatedResourceId"))) {
                queryMap.put("es.Notes", "{\"Notes\":\"Status: New Job Notification Sent\"}");
            }
            queryMap.put("es.ResourceName", testData.get("alternativeResource") + "%");
            sqlLike = true;
            break;

        case "Resource Added - Call Required":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 1);
            queryMap.put("es.Title", "Resource Added - " + testData.get("alternativeResource"));
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.Notes", "{\"Notes\":\"Status: Call Required\"}");
            queryMap.put("es.ResourceName", testData.get("alternativeResource"));
            break;

        case "Resource Added - New Job Notification Sent - Multiple":
            queryMap = new HashMap<String, Object>();
            resources = dbHelperQuotes.getQuoteResources(testData.getInt("jobReference"), "Approved");
            for (int i = 0; i < resources.size(); i++) {
                int resourceId = dbHelperResource.getHelpdeskResourceId(Integer.valueOf(resources.get(i).get("ResourceId").toString()));
                // If the resource has ipad, then resourceassignment status would be 'New job Notification Sent' else it would be 'Call Required'
                if (dbHelperResource.getResourceNotificationMethodType(resourceId) == null && !dbHelperResource.getResourceProfileName(resourceId).equalsIgnoreCase("Contractor")) {
                    queryMap.put("es.Notes", "{\"Notes\":\"Status: Call Required\"}");
                } else {
                    queryMap.put("es.Notes", "{\"Notes\":\"Status: New Job Notification Sent\"}");
                }
                queryMap.put("es.ResourceAssignmentEventTypeId", 1);
                queryMap.put("es.Title", "Resource Added% - " + resources.get(i).get("ResourceName").toString().trim() + "%");
                if (null != impersonatedResourceName) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
                queryMap.put("es.ResourceName", resources.get(i).get("ResourceName").toString().trim() + "%");
                displayAssertionMessage(queryMap);
                await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(30, SECONDS).until(() -> eventSummaryDao.getEventSummaryLike(testData.getInt("jobReference"), queryMap, loggedAt), notNullValue());
            }
            return;

        case "Resource Awaiting Parts":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 30);

            if (testData.getString("gasQuestionSet") != null ){
                hasDetails2 = true;
                details1 = fgasPortalHelper.getFGASDetails1();
                queryMap.put("es.Detail1", details1);
            }
            break;

        case "Resource Notified by SMS":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 20);
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.Title", "Resource Notified by SMS to " + dbHelperResource.getResourcePhoneNumber(testData.getInt("reallocatedResourceId")) + ". No IPad notification was sent - Not configured - " + testData.get("reallocatedResourceName"));
            queryMap.put("es.ResourceName", testData.get("reallocatedResourceName"));
            break;

        case "Resource Removed":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 15);
            // To Do : To uncomment after MCP-7288 is resolved
            // queryMap.put("es.Title", "Resource Removed - " + testData.get("jobResourceName"));
            if (impersonatedResourceName != null) queryMap.put("es.Detail2", "{\"Impersonating\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.Notes", "{\"Notes\":\"Replacing absent resource\"}");
            queryMap.put("es.ResourceName", testData.get("jobResourceName"));
            break;

        case "Resource Returning - Awaiting Parts Review":
            String resourceName = dbHelperResource.getResourceName(testData.getInt("jobResourceId"));
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.Title", "Resource Returning - " + resourceName);
            queryMap.put("es.ResourceAssignmentEventTypeId", 36);
            queryMap.put("es.Detail1", "{\"Status From\":\"Awaiting Parts Review\"}");
            queryMap.put("es.Detail2", "{\"Status To\":\"Returning\"}");
            queryMap.put("es.ResourceName", resourceName);
            break;

        case "Resource returning":
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 36);
            if (testData.getString("gasQuestionSet") != null ){
                hasDetails2 = true;
                details1 = fgasPortalHelper.getFGASDetails1();
                queryMap.put("es.Detail1", details1);
            }
            break;

        case "Work Transferred":
            // Note: Works transfer will only work if the resource status is Returning or Awaiting Parts
            queryMap = new HashMap<String, Object>();
            queryMap.put("es.ResourceAssignmentEventTypeId", 45);
            queryMap.put("es.Title", testData.getString("status") + " - " + testData.getString("reallocatedResourceName"));
            if (impersonatedResourceName != null) queryMap.put("es.Detail1", "{\"Requested By\":\"" + impersonatedResourceName + "\"}");
            queryMap.put("es.Reason", "{\"Reason\":\"" + "Not in work today (Holiday/Absence)" + "\"}");
            break;

        default:
            throw new Exception("Cannot find resource status " + eventType);
        }

        displayAssertionMessage(queryMap);

        try {
            if (sqlLike) {
                await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(30, SECONDS).until(() -> eventSummaryDao.getEventSummaryLike(testData.getInt("jobReference"), queryMap, loggedAt), notNullValue());
            } else {
                await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(30, SECONDS).until(() -> eventSummaryDao.getEventSummary(testData.getInt("jobReference"), queryMap, loggedAt), notNullValue());
            }
        } catch (ConditionTimeoutException e) {
            fail("SQL failed: " + testData.getString("sql"));
        }

        if (hasDetails2) {
            EventSummary eventSummary = eventSummaryDao.getEventSummaryForJobReference(testData.getInt("jobReference"), queryMap, loggedAt, sqlLike);
            dbHelperAssertions.assertEventSummaryTimes(JsonHelper.toMap(eventSummary.getDetail2()));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Timeline Event Summary has been updated with \"([^\"]*)\" or \"([^\"]*)\"$")
    public void the_timeline_event_summery_is_updated(String eventType1, String eventType2) throws Throwable {
        try {
            the_timeline_event_summery_is_updated(eventType1.trim());
        } catch (Exception e) {
            the_timeline_event_summery_is_updated(eventType2.trim());

        }
    }

    @ContinueNextStepsOnException
    @Then("^a Works Order has been written to the database for the uplift amount$")
    public void the_invoice_line_types_are_stored_in_the_database() throws Throwable {
        runtimeState.scenario.write("Asserting DB for Works Order with amount " + testData.getFloat("upliftAmount"));
        BigDecimal expected = BigDecimal.valueOf(testData.getFloat("upliftAmount"));
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(60, SECONDS).until(() -> dbHelperInvoices.getWorksOrderAmount(testData.getInt("jobReference")), comparesEqualTo(expected));
    }

    @ContinueNextStepsOnException
    @Then("^the job event summary has been updated with \"([^\"]*)\"$")
    public void the_job_event_summary_has_been_updated_with(String event) {
        AssertEventSummary assertEventSummary = new AssertEventSummary(eventSummaryDao, testData.getInt("jobReference"), event, testData.getString("impersonatedResourceName"));
        boolean result = true;
        try {
            assertionFactory.performAssertion(assertEventSummary);
        } catch (AssertionError e) {
            result = false;
        }
        runtimeState.scenario.write(assertEventSummary.timeline);
        assertTrue(result);
    }

    @ContinueNextStepsOnException
    @Then("^the Job is updated with one of \"([^\"]*)\" status$")
    public void the_job_is_updated_with_one_of_status(String jobStatus) throws Throwable {
        List<Integer> stIds = new ArrayList<>();
        int actualJobStatusId = dbHelperJobs.getJobStatusId(testData.getInt("jobReference"));
        testData.addIntegerTag("actualJobStatusId", actualJobStatusId);
        if (jobStatus.contains("/")) {
            String[] status = jobStatus.split("/");
            for (String stat : status) {
                int expectedStatusId = dbHelper.getJobStatusId(stat);
                stIds.add(expectedStatusId);
            }
            assertTrue("Incorrect Status Id found : " + actualJobStatusId + " ", stIds.contains(actualJobStatusId));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the JobTimelineEvent table has been updated with one of \"([^\"]*)\"$")
    public void the_timeline_data_is_updated_with_one_of(String eventType) throws Exception {
        if (eventType.contains("/")) {
            String[] types = eventType.split("/");

            if (testData.getInt("actualJobStatusId") == 17) {
                // Job status = 'Awaiting Resource Quote'
                the_timeline_data_is_updated("Quote Submitted Notification");
                assertEquals("The job status is not shown as " + types[1], dbHelperJobs.getJobStatus(testData.getString("jobReference")), types[1]);

            } else {
                the_timeline_data_is_updated(types[0].trim());
            }
        }
    }

    @Then("^the impersonating and impersonated users are recorded on the job event timeline$")
    public void the_impersonating_and_impersonated_users_are_recorded_on_the_job_event_timeline() throws Throwable {
        outputHelper.writeEventSumary(testData.getInt("jobReference"));;

        List<EventSummary> events = eventSummaryDao.getEventSummaryForJobReference(testData.getInt("jobReference"));
        String detail1 = events.get(0).getDetail1() == null ? "" : events.get(0).getDetail1();
        String detail2 = events.get(0).getDetail2() == null ? "" : events.get(0).getDetail2();
        String detail = detail1 + detail2;

        assertTrue("Impersonating user not recorded on job event timeline", detail.contains("Impersonating") && detail.contains(testData.getString("resourceName")));
        assertTrue("Login user not recorded on job event timeline", events.get(0).getLoggedBy() != null);
    }

}