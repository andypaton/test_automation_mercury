package mercury.steps.helpdesk.jobs;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import mercury.database.dao.JobViewDao;
import mercury.database.models.JobView;
import mercury.databuilders.TestData;
import mercury.runtime.RuntimeState;

public class HelpdeskJobSearchSteps {

    @Autowired private JobViewDao jobViewDao;
    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;


    @Given("^there is a completed P1 job within last (\\d+) days assigned to a contractor$")
    public void there_is_a_completed_job_within_last_days_assigned_to_a_contractor(int arg1) throws Throwable {
        JobView jobView = jobViewDao.getCompletedContractorP1JobWithinLastFortnight();
        if (jobView == null) {
            throw new PendingException("Test data not found");
        }

        runtimeState.scenario.write("Original job reference: " + jobView.getJobReference());

        testData.put("originalJobReference", jobView.getJobReference());
        testData.put("siteName", jobView.getName());
        testData.put("location", jobView.getSubLocationName());
        testData.put("fault", jobView.getFaultType());

        String subtypeClassification = jobView.getAssetName() + " > " + jobView.getAssetSubTypeName();
        subtypeClassification = jobView.getAssetClassificationName().isEmpty() ? subtypeClassification : subtypeClassification + " > " + jobView.getAssetClassificationName();
        testData.put("subtypeClassification", subtypeClassification);
        testData.put("asset", "None");
    }

}
