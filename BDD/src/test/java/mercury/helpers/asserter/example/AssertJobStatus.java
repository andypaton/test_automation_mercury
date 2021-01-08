package mercury.helpers.asserter.example;

import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import mercury.helpers.asserter.common.AssertTask;
import mercury.helpers.dbhelper.DbHelperJobs;

public class AssertJobStatus implements AssertTask {
    
	@Autowired private DbHelperJobs dbHelperJobs;

	private String arg;
	
    private String failureMessage;

    public AssertJobStatus(String arg) {
        this.arg = arg;
    }

    @Override
    public boolean execute() {
        
        try {
                            
           assertTrue("Something is not true", dbHelperJobs.isJobAwaitingAcceptance(arg));

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

