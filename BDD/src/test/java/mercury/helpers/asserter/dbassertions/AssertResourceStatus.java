package mercury.helpers.asserter.dbassertions;

import java.util.Map;

import mercury.helpers.asserter.common.AssertTask;
import mercury.helpers.dbhelper.DbHelperJobs;

public class AssertResourceStatus implements AssertTask {

    private DbHelperJobs dbHelperJobs;
    private Integer jobReference;
    private String jobStatus;

    private String failureMessage = "Resource Statuses found:  ";

    public AssertResourceStatus(DbHelperJobs dbHelperJobs, Integer jobReference, String jobStatus) {
        this.dbHelperJobs = dbHelperJobs;
        this.jobReference = jobReference;
        this.jobStatus = jobStatus;
    }

    @Override
    public boolean execute() {

        try {
            for (Map<String, Object> jobDetails : dbHelperJobs.getResourceStatus(jobReference)) {
                if (jobDetails.get("ResourceStatus").equals(jobStatus)) {
                    return true;
                } else {
                    failureMessage = failureMessage + jobDetails.get("ResourceStatus") + ", ";
                }
            }

        } catch (Throwable t) {
            failureMessage = t.getMessage();
            return false;
        }

        failureMessage = failureMessage.replaceAll(", $", "");  // remove training comma
        return false;
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
