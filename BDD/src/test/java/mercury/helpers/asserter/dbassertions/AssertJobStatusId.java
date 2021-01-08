package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertTrue;

import mercury.database.dao.JobDao;
import mercury.helpers.asserter.common.AssertTask;

public class AssertJobStatusId implements AssertTask {
    
	private String failureMessage;
	
	private int jobReference;	
	private int statusId;
	private JobDao jobDao;

    public AssertJobStatusId(int jobReference, int statusId, JobDao jobDao) {
        this.jobReference = jobReference;
        this.statusId = statusId;
        this.jobDao = jobDao;
    }

    @Override
    public boolean execute() {
        try {

    		int actual = jobDao.getByJobReference(jobReference).getJobStatusId();
    		
    	    assertTrue("Expected JobStatusId = " + statusId + " but got " + actual, statusId == actual);

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

