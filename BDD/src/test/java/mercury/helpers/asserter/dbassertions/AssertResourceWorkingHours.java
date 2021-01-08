package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertTrue;

import mercury.database.dao.ResourceWorkingHoursDao;
import mercury.database.models.ResourceWorkingHours;
import mercury.helpers.asserter.common.AssertTask;

public class AssertResourceWorkingHours implements AssertTask {
    
	private String failureMessage;
	
	private Integer resourceId;	
	private String startAt;
	private String endAt;
	private ResourceWorkingHoursDao resourceWorkingHoursDao;

    public AssertResourceWorkingHours(Integer resourceId, String startAt, String endAt, ResourceWorkingHoursDao resourceWorkingHoursDao) {
        this.resourceId = resourceId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.resourceWorkingHoursDao = resourceWorkingHoursDao;
    }

    @Override
    public boolean execute() {
        try {

        	ResourceWorkingHours rwh = resourceWorkingHoursDao.getResourceWorkingHoursForToday(resourceId);
        	assertTrue(rwh.getStartAt().contains(startAt));
        	assertTrue(rwh.getEndAt().contains(endAt));

        } catch (Throwable t) {
            failureMessage = "expected ResourceId " + resourceId + ": StartAt=" + startAt + ", EndAt=" + endAt;
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

