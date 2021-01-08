package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertNull;

import mercury.database.dao.JobDao;
import mercury.database.models.Job;
import mercury.helpers.asserter.common.AssertTask;

public class AssertJobNotCreated implements AssertTask {
    
	private String failureMessage;
	
	private String description;	
	private String jobType;	
	private JobDao jobDao;
	private Job job;

    public AssertJobNotCreated(String description, JobDao jobDao) {
        this.description = description;
        this.jobDao = jobDao;
    }
    
    public AssertJobNotCreated(String jobType, String description, JobDao jobDao) {
        this.description = description;
        this.jobType = jobType;
        this.jobDao = jobDao;
    }

    @Override
    public boolean execute() {
        try {
        	if (jobType == null) {
        		job = jobDao.getByDescription(description);
        	} else {
        		job = jobDao.getJob(jobType, description);
        	}
    	    assertNull(job);

        } catch (Throwable t) {
            failureMessage = t.getMessage();
            return false;
        }
        
        return true;
    }

    public Job getJob() {
    	return job;
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

