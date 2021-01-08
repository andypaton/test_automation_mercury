package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertTrue;

import mercury.database.dao.BrandWorkingHoursDao;
import mercury.database.models.BrandWorkingHours;
import mercury.helpers.asserter.common.AssertTask;

public class AssertBrandWorkingHours implements AssertTask {
    
	private String failureMessage;
	
	private Integer brandId;	
	private String startAt;
	private String endAt;
	private BrandWorkingHoursDao brandWorkingHoursDao;

    public AssertBrandWorkingHours(Integer brandId, String startAt, String endAt, BrandWorkingHoursDao brandWorkingHoursDao) {
        this.brandId = brandId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.brandWorkingHoursDao = brandWorkingHoursDao;
    }

    @Override
    public boolean execute() {
        try {

        	BrandWorkingHours bwh = brandWorkingHoursDao.get(brandId);
        	assertTrue(bwh.getStartAt().contains(startAt));
        	assertTrue(bwh.getEndAt().contains(endAt));

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

