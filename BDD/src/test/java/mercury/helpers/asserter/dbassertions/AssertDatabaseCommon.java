package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import mercury.database.dao.JobTimelineEventDao;
import mercury.database.dao.MessageDao;
import mercury.database.dao.ProjectQuoteSummaryDao;
import mercury.database.dao.QuoteDao;
import mercury.database.dao.QuoteLineDao;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.helpers.asserter.common.AssertTask;

public class AssertDatabaseCommon implements AssertTask {

    private Object daoObject;
    private Integer jobReference;
    private Map<String, Object> queryMap;
    private String failureMessage;
    private Integer expectedRecordCount = 1;
    private String createdOn;

    public AssertDatabaseCommon(Object daoObject, Integer jobReference, Map<String, Object> queryMap, String createdOn) {
        this.daoObject = daoObject;
        this.jobReference = jobReference;
        this.queryMap = queryMap;
        this.createdOn = createdOn;
    }

    public AssertDatabaseCommon(Object daoObject, Integer jobReference, Map<String, Object> queryMap, Integer expectedRecordCount, String createdOn) {
        this.daoObject = daoObject;
        this.jobReference = jobReference;
        this.queryMap = queryMap;
        this.expectedRecordCount = expectedRecordCount;
        this.createdOn = createdOn;
    }

    @Override
    public boolean execute() {
        Object recordSet = null;
        try {
            if (daoObject instanceof JobTimelineEventDao) {
                recordSet = ((JobTimelineEventDao) daoObject).getRecordCount(jobReference, queryMap, createdOn);
                assertEquals("Unexpected number of records returned", expectedRecordCount, recordSet);
            }

            if (daoObject instanceof QuoteLineDao) {
                recordSet = ((QuoteLineDao) daoObject).getRecordCount(jobReference, queryMap, createdOn);
                assertEquals("Unexpected number of records returned", expectedRecordCount, recordSet);
            }

            if (daoObject instanceof ProjectQuoteSummaryDao) {
                recordSet = ((ProjectQuoteSummaryDao) daoObject).getRecordCount(jobReference, queryMap, createdOn);
                assertEquals("Unexpected number of records returned", expectedRecordCount, recordSet);
            }

            if (daoObject instanceof MessageDao) {
                recordSet = ((MessageDao) daoObject).getRecordCount(jobReference, queryMap, createdOn);
                assertEquals("Unexpected number of records returned", expectedRecordCount, recordSet);
            }

            if (daoObject instanceof ResourceAssignmentDao) {
                recordSet = ((ResourceAssignmentDao) daoObject).getRecordCount(jobReference, queryMap, createdOn);
                assertEquals("Unexpected number of records returned", expectedRecordCount, recordSet);
            }

            if (daoObject instanceof QuoteDao) {
                recordSet = ((QuoteDao) daoObject).getRecordCount(jobReference, queryMap, createdOn);
                assertEquals("Unexpected number of records returned", expectedRecordCount, recordSet);
            }

            assertNotNull("Unexpected null record set", recordSet);

        } catch (Throwable t) {
            failureMessage = t.getMessage();
            return false;
        }

        return true;
    }

    @Override
    public String getTaskName() {
        return this.getClass().getName();
    }

    @Override
    public String getTaskFailureMessage() {
        return failureMessage;
    }
}
