package mercury.steps.portal.jobs;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.When;
import mercury.api.models.job.Job;
import mercury.databuilders.TestData;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperUsers;

public class PortalUserWithJobSteps {

    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperUsers dbHelperUsers;
    @Autowired AssertionFactory assertionFactory;
    @Autowired private TestData testData;


    /**
     * Create job via API and assign to either Contractor Technician or Contractor Admin (if he does NOT use engineers)
     */
    @When("^a Contractor with a job awaiting acceptance$")
    public void a_Contractor_with_a_job_awaiting_acceptance() throws Throwable {
        testData.put("profileName", "Contractor");
        testData.put("jobType", "reactive");

        Job job = jobCreationHelper.createJobInStatus("Logged / New Notification Sent");

        testData.put("jobReference", job.getJobReference());
        testData.put("siteId", job.getSiteId());
        testData.put("siteName", dbHelperSites.getSiteName(job.getSiteId()));
        testData.put("resourceId", testData.getInt("resourceId"));

        Map<String, Object> dbData = dbHelperUsers.getLoginUserForJob(job.getJobReference());
        testData.put("userName", dbData.get("UserName"));
        testData.put("userProfileName", dbData.get("UserProfileName"));
    }

}


