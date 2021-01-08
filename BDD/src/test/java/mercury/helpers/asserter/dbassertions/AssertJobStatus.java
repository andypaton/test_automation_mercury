package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertNotNull;

import mercury.database.dao.JobViewDao;
import mercury.database.models.JobView;
import mercury.helpers.asserter.common.AssertTask;

public class AssertJobStatus implements AssertTask {

    //@Autowired
    private JobViewDao jobViewDao;

    private Integer jobReference;
    private String jobStatus;

    private String failureMessage;

    public AssertJobStatus(JobViewDao jobViewDao, Integer jobReference, String jobStatus) {
        this.jobViewDao = jobViewDao;
        this.jobReference = jobReference;
        this.jobStatus = jobStatus;
    }

    @Override
    public boolean execute() {

        try {

            JobView currentJob = jobViewDao.getByJobReference(jobReference, jobStatus);
            assertNotNull("Unexpected Job Status found", currentJob);

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
