package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import mercury.database.dao.JobTimelineEventDao;
import mercury.database.models.JobTimelineEvent;
import mercury.helpers.asserter.common.AssertTask;

public class AssertJobTimelineEvent implements AssertTask {

    private JobTimelineEventDao jobTimelineEventDao;

    private Integer jobReference;
    private Map<String, Object> queryMap;
    private String createdOn;

    private String failureMessage;

    public AssertJobTimelineEvent(JobTimelineEventDao jobTimelineEventDao, Integer jobReference, Map<String, Object> queryMap, String createdOn) {
        this.jobTimelineEventDao = jobTimelineEventDao;
        this.jobReference = jobReference;
        this.queryMap = queryMap;
        this.createdOn = createdOn;
    }

    @Override
    public boolean execute() {
        JobTimelineEvent JobTimelineEvent;
        try {
            JobTimelineEvent = jobTimelineEventDao.getByJobReferenceMap(jobReference, queryMap, createdOn);
            assertNotNull("Unexpected null record set", JobTimelineEvent);
            //assertFalse("Unexpected empty record set", JobTimelineEvent.isEmpty());

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
