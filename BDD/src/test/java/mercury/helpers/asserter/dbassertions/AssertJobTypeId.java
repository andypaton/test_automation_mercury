package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertTrue;

import mercury.database.dao.JobDao;
import mercury.helpers.asserter.common.AssertTask;

public class AssertJobTypeId implements AssertTask {
    
	private String failureMessage;
	
	private int jobReference;	
	private int jobTypeId;
	private JobDao jobDao;

    public AssertJobTypeId(int jobReference, int jobTypeId, JobDao jobDao) {
        this.jobReference = jobReference;
        this.jobTypeId = jobTypeId;
        this.jobDao = jobDao;
    }

    @Override
    public boolean execute() {
        try {

    		int actual = jobDao.getByJobReference(jobReference).getJobTypeId();
    		
    	    assertTrue("Expected JobTypeId = " + jobTypeId + " but got " + actual, jobTypeId == actual);

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

