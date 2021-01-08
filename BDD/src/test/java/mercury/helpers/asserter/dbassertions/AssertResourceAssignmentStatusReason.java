package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertNotNull;

import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.models.ResourceAssignment;
import mercury.helpers.asserter.common.AssertTask;

public class AssertResourceAssignmentStatusReason implements AssertTask {

	private ResourceAssignmentDao resourceAssignmentDao;
	private Integer jobReference;
	private Integer resourceId;
	private Integer resourceAssignmentStatus;
	private Integer resourceAssignmentReason;

	private String failureMessage;
		
    public AssertResourceAssignmentStatusReason(ResourceAssignmentDao resourceAssignmentDao, Integer  jobReference, Integer resourceId, Integer resourceAssignmentStatus, Integer resourceAssignmentReason) {
    	this.resourceAssignmentDao = resourceAssignmentDao;
    	this.jobReference = jobReference;
        this.resourceId = resourceId;
        this.resourceAssignmentStatus = resourceAssignmentStatus;
        this.resourceAssignmentReason = resourceAssignmentReason;
    }

    @Override
    public boolean execute() {
    	ResourceAssignment resourceAssignment;	
        try {        		
        	resourceAssignment = resourceAssignmentDao.getAssigmentByStatusReason(jobReference, resourceId, resourceAssignmentStatus, resourceAssignmentReason);		
    		assertNotNull("Unexpected null record set", resourceAssignment);    	
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
