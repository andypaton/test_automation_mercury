package mercury.helpers.asserter.dbassertions;

import mercury.database.dao.JobDao;
import mercury.helpers.asserter.common.AssertTask;

import static org.junit.Assert.assertTrue;

public class AssertPPMJobId implements AssertTask{
    private String failureMessage;

    private int jobReference;
    private int ppmJobId;
    private JobDao jobDao;

    public AssertPPMJobId(JobDao jobDao, int jobReference, int ppmJobId) {
        this.jobReference = jobReference;
        this.ppmJobId = ppmJobId;
        this.jobDao = jobDao;
    }

    @Override
    public boolean execute() {
        try {

            int actual = jobDao.getByJobReference(jobReference).getPpmJobId();

            assertTrue("Expected PPMJobId = " + ppmJobId + " but got " + actual, ppmJobId == actual);

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
