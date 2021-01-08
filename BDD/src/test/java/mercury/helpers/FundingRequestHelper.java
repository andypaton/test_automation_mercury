package mercury.helpers;

import static mercury.helpers.Globalisation.SHORT;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.database.dao.JobViewDao;
import mercury.database.models.JobView;
import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.pageobject.web.portal.fundingrequests.JobDetailsPartial;
import mercury.runtime.RuntimeState;

@Component
public class FundingRequestHelper {

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private TzHelper tzHelper;

    /**
     * Asserting the fields and their values displayed in the 'Job Details' partial page object section specific to Quote and Funding Request jobs
     * @param jobView
     * @throws Exception
     */
    private void assertJobDetailsForQuoteJobs(JobView jobView) throws Exception {
        if (!testData.getBoolean("skipCheckDates")) {
            //          expectedDate = tzHelper.adjustTimeForJobReference(testData.getInt("jobReference"), expectedDate, SHORT_DATE);
            String expectedDate = DateHelper.dateAsString(jobView.getCreatedOn(), SHORT_DATE);
            runtimeState.scenario.write("Asserting Date is: " + expectedDate);
            assertEquals("Unexpected Date ", expectedDate, runtimeState.jobDetailsPartial.getJobDetailsFieldData("Date"));
        }

        runtimeState.scenario.write("Asserting Quote Priority is: " + dbHelperQuotes.getQuotePriority(testData.getInt("jobReference")));
        assertEquals("Unexpected Quote Priority ", dbHelperQuotes.getQuotePriority(testData.getInt("jobReference")), runtimeState.jobDetailsPartial.getJobDetailsFieldData("Quote Priority"));

        runtimeState.scenario.write("Asserting Scope of Works is: " + jobView.getDescription());
        assertEquals("Unexpected Scope of Works ", jobView.getDescription().replaceAll("\\s+", ""), runtimeState.jobDetailsPartial.getJobDetailsFieldData("Scope of works").replaceAll("\\s+", ""));
    }

    /**
     * Asserting the fields and their values displayed in the Job Details' partial page object section specific to Quote and Funding Request
     * @param jobView
     * @throws Exception
     */
    private void assertJobDetailsForReactiveJobs(JobView jobView) throws Exception {
        int jobReference = Integer.valueOf(jobView.getJobReference());
        runtimeState.scenario.write("Asserting Job Ref is: " + jobView.getJobReference());
        assertEquals("Unexpected Job Reference ", jobView.getJobReference(), runtimeState.jobDetailsPartial.getJobDetailsFieldData("Job Ref"));

        runtimeState.scenario.write("Asserting Site is: " + jobView.getName());
        assertEquals("Unexpected Site ", normalize(jobView.getName()), normalize(runtimeState.jobDetailsPartial.getJobDetailsFieldData("Site")));

        String location = jobView.getLocationName().isEmpty() ? jobView.getSubLocationName().replaceAll("\\s+", "") : (jobView.getSubLocationName() + jobView.getLocationName()).replaceAll("\\s+", "");
        runtimeState.scenario.write("Asserting Location is: " + location);
        assertEquals("Unexpected Location ", location, runtimeState.jobDetailsPartial.getJobDetailsFieldData("Location").replaceAll(" > |\\s+", ""));

        runtimeState.scenario.write("Asserting SubType/Classification is: " + jobView.getAssetClassificationName() + jobView.getAssetSubTypeName());
        assertEquals("Unexpected SubType", (jobView.getAssetClassificationName() + jobView.getAssetSubTypeName()).replaceAll(" - |\\s+", ""), runtimeState.jobDetailsPartial.getJobDetailsFieldData("SubType/Classification").replaceAll(" - |\\s+", ""));

        if (!testData.getBoolean("skipCheckDates")) {
            String expectedDate = DateHelper.dateAsString(jobView.getCreatedOn(), SHORT).toUpperCase();
            expectedDate = tzHelper.adjustTimeForJobReference(jobReference, expectedDate, SHORT);
            runtimeState.scenario.write("Asserting Date Logged is: " + expectedDate);
            assertEquals("Unexpected Date Job Logged", expectedDate, runtimeState.jobDetailsPartial.getJobDetailsFieldData("Date Job Logged"));
        }

        runtimeState.scenario.write("Asserting Priority is: " + jobView.getFaultPriority());
        assertEquals("Unexpected Priority ", jobView.getFaultPriority(), runtimeState.jobDetailsPartial.getJobDetailsFieldData("Priority"));

        String caller = dbHelperJobs.getCallerNameForJob(testData.getInt("jobReference"));
        runtimeState.scenario.write("Asserting Caller is: " + caller);
        assertEquals("Unexpected Caller ", normalize(caller), normalize(runtimeState.jobDetailsPartial.getJobDetailsFieldData("Caller")));

        String jobStatus = dbHelperJobs.getJobStatus(testData.getString("jobReference"));
        runtimeState.scenario.write("Asserting Job Status is: " + jobStatus);
        assertEquals("Unexpected Job Status ", jobStatus, runtimeState.jobDetailsPartial.getJobDetailsFieldData("Job Status"));

        runtimeState.scenario.write("Asserting Description is: " + jobView.getDescription());
        assertEquals("Unexpected Description ", jobView.getDescription().replaceAll("\\s+", ""), runtimeState.jobDetailsPartial.getJobDetailsFieldData("Job Description").replaceAll("\\s+", ""));
    }

    /**
     *  Asserting the fields and their values displayed in the Job Details' partial page object section specific to Quote and Funding Request
     * @param jobView
     * @throws Exception
     */
    private void assertJobDetailsCommon(JobView jobView) throws Exception {
        runtimeState.scenario.write("Asserting Job Ref is: " + jobView.getJobReference());
        assertEquals("Unexpected Job Reference ", jobView.getJobReference(), runtimeState.jobDetailsPartial.getJobDetailsFieldData("Job Ref"));

        runtimeState.scenario.write("Asserting Site is: " + jobView.getName());
        assertEquals("Unexpected Site ", normalize(jobView.getName()), normalize(runtimeState.jobDetailsPartial.getJobDetailsFieldData("Site")));

        runtimeState.scenario.write("Asserting Location is: " + jobView.getSubLocationName());
        assertEquals("Unexpected Location ", jobView.getSubLocationName().replaceAll("\\s+", ""), runtimeState.jobDetailsPartial.getJobDetailsFieldData("Location").replaceAll("\\s+", ""));

        runtimeState.scenario.write("Asserting SubType/Classification is: " + jobView.getAssetSubTypeName() + jobView.getAssetClassificationName());
        assertEquals("Unexpected SubType", (jobView.getAssetName() + jobView.getAssetSubTypeName() + jobView.getAssetClassificationName()).replaceAll("\\s+", ""), runtimeState.jobDetailsPartial.getJobDetailsFieldData("SubType/Classification").replaceAll(" > |\\s+", ""));
    }

    public void assertJobDetailsDisplayed() throws Exception {
        // Get the partial page object Job Details
        runtimeState.jobDetailsPartial = new JobDetailsPartial(getWebDriver()).get();

        // Asserting the fields and their values displayed in the 'Job Details' section
        JobView jobView = jobViewDao.getByJobReference(testData.getInt("jobReference"));
        runtimeState.scenario.write("Asserting the Job Details: ");

        if (getWebDriver().getCurrentUrl().contains("Portal/JobsAwaitingInitialFundingRequest")) {
            assertJobDetailsForReactiveJobs(jobView);
        } else {
            assertJobDetailsCommon(jobView);
            assertJobDetailsForQuoteJobs(jobView);
        }
    }
}
