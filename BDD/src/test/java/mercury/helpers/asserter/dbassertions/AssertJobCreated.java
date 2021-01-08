package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertNotNull;

import mercury.database.dao.JobDao;
import mercury.database.models.Job;
import mercury.helpers.asserter.common.AssertTask;

public class AssertJobCreated implements AssertTask {
    
	private String failureMessage;
	
	private String description;	
	private String status;	
	private String jobType;	
	private JobDao jobDao;
	private Job job;

    public AssertJobCreated(String description, JobDao jobDao) {
        this.description = description;
        this.jobDao = jobDao;
    }
    
    public AssertJobCreated(String jobType, String description, String status, JobDao jobDao) {
        this.description = description;
        this.status = status;
        this.jobType = jobType;
        this.jobDao = jobDao;
    }

    @Override
    public boolean execute() {
        try {
            assertNotNull(description);

        	if (status != null && jobType != null) {
        		job = jobDao.getJob(jobType, description, status);
        		
        	} else if (status != null) {
                job = jobDao.getByDescriptionAndStatus(description, status);
                
            } else {        		
        		job = jobDao.getByDescription(description);
        	}
        	assertNotNull(job);


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

