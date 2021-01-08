package mercury.helpers.dbhelper;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.DB_POLLING_INTERVAL;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Constants.POLLING_INTERVAL_LONG;
import static mercury.helpers.Constants.TWO_MINUTES;
import static mercury.helpers.Globalisation.MEDIUM3;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mercury.database.dao.ApplicationUserDao;
import mercury.database.dao.HelpdeskFaultDao;
import mercury.database.dao.JobViewDao;
import mercury.database.dao.QuoteApprovalScenariosDao;
import mercury.database.models.ApplicationUser;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.runtime.RuntimeState;

public class DbHelperAssertions {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private QuoteApprovalScenariosDao quoteApprovalScenariosDao;
    @Autowired private HelpdeskFaultDao helpdeskFaultDao;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private ApplicationUserDao applicationUserDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperQuotes dbHelperQuotes;


    public void quoteIsInStage(Integer jobReference, String quoteJobApprovalStatus, String approvalStatus) throws Exception {
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("QuoteJobApprovalStatusName", quoteJobApprovalStatus);
        queryMap.put("ApprovalStatusName", approvalStatus);
        try {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> dbHelperQuotes.getQuoteApprovalScenariosRecordCount(jobReference, queryMap), greaterThanOrEqualTo(1));
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new Exception("Cannot find the Quote " + jobReference + " in the correct state, this is usually down to the database sync.");
        }
    }

    public void quoteIsInStage(Integer jobReference, Integer quoteJobApprovalStatusId, Integer approvalStatusId) throws Exception {
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("QuoteJobApprovalStatusId", quoteJobApprovalStatusId);
        queryMap.put("ApprovalStatusId", approvalStatusId);
        try {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> quoteApprovalScenariosDao.getRecordCount(jobReference, queryMap), greaterThanOrEqualTo(1));
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new Exception("Cannot find the Quote " + jobReference + " in the correct state, this is usually down to the database sync.");
        }
    }

    public void quoteWithFundingRoute(Integer jobReference, Integer FundingRouteId) throws Exception {
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("FundingRouteId", FundingRouteId);
        try {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> quoteApprovalScenariosDao.getRecordCount(jobReference, queryMap), greaterThanOrEqualTo(1));
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new Exception("Cannot find the Quote " + jobReference + " in the correct state, this is usually down to the database sync or the save hasn't worked.");
        }

    }

    public void quoteWithQuotePriority(Integer jobReference, String QuotePriority) throws Exception {
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("QuotePriority", QuotePriority);
        try {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> quoteApprovalScenariosDao.getRecordCount(jobReference, queryMap), greaterThanOrEqualTo(1));
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new Exception("Cannot find the Quote " + jobReference + " in the correct state, this is usually down to the database sync or the save hasn't worked.");
        }

    }

    public void jobInHelpdeskFault(Integer jobReference) throws Exception {
        try {
            await().pollInterval(POLLING_INTERVAL_LONG, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> helpdeskFaultDao.getJobInHelpdeskFault(jobReference), notNullValue());
        } catch (Exception e) {
            throw new Exception("Cannot find the Job " + jobReference + " in the correct state, this is usually down to the database sync.");
        }
    }

    public void jobIsInStatus(Integer jobReference, String jobStatus) throws Exception {
        try {
            await().pollInterval(DB_POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> jobViewDao.getByJobReference(jobReference, jobStatus), notNullValue());
        } catch (Exception e) {
            throw new Exception("Cannot find the Job " + jobReference + " in the correct state, this is usually down to the database sync.");
        }
    }

    public void quoteWithResource(Integer jobReference, String resourceName) throws Exception {
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("ResourceName", resourceName);
        try {
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> quoteApprovalScenariosDao.getQuoteByFaultId(jobReference).getResourceName().trim(), equalToIgnoringCase(resourceName.split("\\(")[0].trim()));
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new Exception("Cannot find the Quote " + jobReference + " in the correct state, expected resource " + resourceName + ". This is usually down to the database sync or the save hasn't worked.");
        }
    }

    public void applicatonUserLockout(String userName, int accessFailedCount) throws Exception {
        ApplicationUser loginUser = applicationUserDao.getByUsername(userName);
        assertEquals("Username is not the same", loginUser.getUserName(), userName);
        accessFailedCount++;

        try {
            int innerAccessFailedCount = accessFailedCount;
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> applicationUserDao.getByUsername(userName).getAccessFailedCount().equals(innerAccessFailedCount));
        } catch (Exception e) {
            throw new Exception("UserName " + userName + " does not have the expected log on fail count");
        }
    }

    private void dateBeforeOrEqual(Date expected, String actual, String format) throws ParseException {
        Date actualStartTime = DateHelper.stringAsDate(actual, format);
        runtimeState.scenario.write("Asserting time is  " + expected.toString() + " before or equal to " + actualStartTime.toString());
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(15, SECONDS).until(() -> expected.compareTo(actualStartTime), lessThanOrEqualTo(0));
    }

    public void assertEventSummaryTimes(Map<String, Object> details2) throws ParseException {
        runtimeState.scenario.write("Asserting Start time");
        dateBeforeOrEqual(testData.getDate("Start time"), details2.get("Start time").toString(), MEDIUM3);

        runtimeState.scenario.write("Asserting Off site time");
        dateBeforeOrEqual(testData.getDate("Off site time"), details2.get("Off site time").toString(), MEDIUM3);

        runtimeState.scenario.write("Asserting Time on site");
        long duration = testData.getDate("Off site time").getTime() - testData.getDate("Start time").getTime();
        long actualDuration = DateHelper.getTimeInMilliseconds(DateHelper.stringAsDate(details2.get("Time on site").toString(), "HH:mm"));
        int tolerance = 60000;
        long min = duration > tolerance ? duration - tolerance : 0;
        long max = duration + tolerance;
        assertTrue(min <= actualDuration && actualDuration <= max);
    }
}
