package mercury.helpers.asserter.dbassertions;

import mercury.database.dao.PPMJobDao;
import mercury.helpers.asserter.common.AssertTask;

import static org.junit.Assert.assertTrue;

public class AssertPPMResourceStatus implements AssertTask {

    private PPMJobDao PPMJobDao;

    private String jobStatus;
    private Integer jobReference;


    private String failureMessage;

    public AssertPPMResourceStatus(PPMJobDao PPMJobDao, Integer jobReference, String jobStatus) {
        this.PPMJobDao = PPMJobDao;
        this.jobReference = jobReference;
        this.jobStatus = jobStatus;
    }

    @Override
    public boolean execute() {
     try {
        String actual = PPMJobDao.getPPMJobStatusByPpmId(jobReference).getCalloutStatus();

        assertTrue("Expected JobStatus = " + jobStatus + " but got " + actual, actual.equals(jobStatus));

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
