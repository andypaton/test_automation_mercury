package mercury.steps.portal;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import mercury.api.models.ppm.PPMJob;
import mercury.database.dao.UserJobDao;
import mercury.database.models.UserJob;
import mercury.databuilders.TestData;
import mercury.databuilders.User;
import mercury.helpers.PPMJobCreationHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.runtime.RuntimeState;

public class GetPortalPPMUserDataSteps {

    @Autowired private TestData testData;
    @Autowired private UserJobDao userJobDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private User user;
    @Autowired private GetPortalUserDataSteps getPortalUserDataSteps;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private PPMJobCreationHelper ppmJobCreationHelper;

    UserJob userJob;

    /**
     * Creates the PPM Job(s) with the required user details.
     * @param jobStatus
     * @throws Exception
     */
    private void createPPMUserJob(String jobStatus) throws Exception {

        PPMJob ppmJob = ppmJobCreationHelper.createPPMJobData(jobStatus);

        userJob = userJobDao.getPPMJobWithInvoice(testData.getString("profileName"), ppmJob.getPPMTypeName());
        user.setUsername(userJob.getUserName());
        user.setResourceId(userJob.getResourceId());
        user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));

        int activeFlag = dbHelperInvoices.getOnlineInvoicingActiveFlagForUser(userJob.getResourceId());

        if (activeFlag == 0) {
            dbHelperInvoices.updateOnlineInvoicingActiveFlagForUser(userJob.getResourceId());
        }

        testData.put("jobReference", userJob.getJobReference());
        testData.put("resourceId", userJob.getResourceId());
        testData.put("siteId", userJob.getSiteId());
    }

    @Given("^a portal user with a \"([^\"]*)\" (?:profile|permission) and with \"([^\"]*)\" PPM Jobs$")
    public void portal_user_with_profile_and_ppm_jobs_with_status(String profileName, String jobStatus) throws Throwable {
        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("jobType", "PPM");

        try {
            //            getPortalUserDataSteps.getUserJob(queryMap);  // commenting out to avoid bad data
            throw new PendingException("Cannot find suitable test data");
        } catch (PendingException e) {
            createPPMUserJob(jobStatus);

            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }
    }
}
