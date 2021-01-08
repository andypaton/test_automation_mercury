package mercury.steps;

import static org.junit.Assert.assertNotNull;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.opencsv.CSVReader;

import cucumber.api.java.en.Given;
import mercury.api.models.job.Job;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.models.UserJob;
import mercury.databuilders.TestData;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.QuoteCreationHelper;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperTestData;
import mercury.runtime.RuntimeState;

public class TestDataSteps {

    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperTestData dbHelperTestData;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private ResourceAssignmentDao resourceAssignmentDao;
    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private QuoteCreationHelper quoteCreationHelper;
    @Autowired private OutputHelper outputHelper;
    @Autowired private PropertyHelper propertyHelper;



    @Given("^a job exists (?:where|that has been) \"([^\"]*)\"$")
    public void jobs_exist_where(String type) throws Throwable {
        Job job;
        switch (type) {
        case "no resource assigned":
            if (dbHelperMonitors.getJobReferenceWithNoResourceIsAssigned() == null) {
                jobCreationHelper.createJobInStatus("Logged / Awaiting Assignment");
            }
            break;

        case "the resource has declined the job and suggested a resource profile of Contractor":
            if (dbHelperMonitors.getJobReferenceWithDeclinedJob("single", 115, 0) == null) {
                job = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");
                apiHelperJobs.declineJob(job.getJobReference());
                int additionalResourceRequirementId = dbHelper.getMaxAdditionalResourceRequirementId(job.getJobReference());
                apiHelperJobs.updateResourceProfile(additionalResourceRequirementId, 115);  // Contractor
            }
            break;

        case "the resource has declined the job and suggested a resource profile that can't be mapped to the site":
            if (dbHelperMonitors.getJobReferenceWithDeclinedJob("single", -1, 0) == null) {
                job = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");

                // decline job
                apiHelperJobs.declineJob(job.getJobReference());

                // suggest resource that can't be mapped to site
                int resourceProfileId = dbHelperResources.getResourceProfileIdThatCantBeMappedToSite(job.getSiteId());
                int arrId = dbHelper.getMaxAdditionalResourceRequirementId(job.getJobReference());
                apiHelperJobs.updateResourceProfile(arrId, resourceProfileId);
            }
            break;

        case "declined twice by a resource":
            if (dbHelperMonitors.getJobReferenceWithDeclinedJob("multiple", 0, 2) == null) {
                job = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");

                // decline first time
                apiHelperJobs.declineJob(job.getJobReference());

                // assign to any city tech
                int resourceId = dbHelperResources.getCityTechWithIpad();
                apiHelperJobs.assignAdditionalResource(job.getJobReference(), resourceId);

                // decline second time
                apiHelperJobs.declineJob(job.getJobReference());

            }
            break;

        case "re-opened with no assignment":
            if (dbHelperMonitors.getJobReferenceWhereReopenedAndNoResourceAssigned() == null) {
                job = jobCreationHelper.createJobInStatus("Logged / Awaiting Assignment");
                apiHelperJobs.cancelJob(job.getId(), "TestAutomation");
                apiHelperJobs.reopenJob(job.getId());
            }
            break;

        case "unparked":
            if (dbHelperMonitors.getJobReferenceWhichHasBecomeUnparked() == null) {
                job = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");
                int resourceAssignmentId = resourceAssignmentDao.getAssigmentByJobReference(job.getJobReference()).getId();
                apiHelperJobs.removeResource(resourceAssignmentId);
                apiHelperJobs.removeResourceAdvise(resourceAssignmentId);
                apiHelperJobs.parkJob(job.getId());
                apiHelperJobs.unParkJob(job.getId());
            }
            break;


        default:
            throw new Exception("Unexpected type: " + type);
        }
    }

    @Given("^all job types exist for the \"([^\"]*)\" monitor$")
    public void all_job_types_exist_for_the_monitor(String monitor) throws Throwable {
        switch(monitor) {
        case "Awaiting Assignment":
            jobs_exist_where("no resource assigned");
            jobs_exist_where("the resource has declined the job and suggested a resource profile of Contractor");
            jobs_exist_where("the resource has declined the job and suggested a resource profile that can't be mapped to the site");
            jobs_exist_where("declined twice by a resource");
            
            if (!getWebDriver().getCurrentUrl().contains("usad")) {
                jobs_exist_where("re-opened with no assignment");
                jobs_exist_where("unparked"); 
            }
            
            break;
        default: throw new Exception("Unexpected monitor: "+ monitor);
        }
    }

    @Given("^a \"([^\"]*)\" user with a \"([^\"]*)\" job \"([^\"]*)\"$")
    public void a_user_with_a_job(String profile, String jobType, String resourceStatus) throws Throwable {
        testData.put("jobType", jobType);
        Map<String, Object> dbData = null;
        if (profile.toUpperCase().contains("CONTRACTOR")) {
            dbData = dbHelperResources.getContractorWithProfile(profile);
            testData.put("profileName", "Contractor");
        }
        CommonSteps.assertDataFound(dbData);

        testData.put("resourceId", dbData.get("ResourceId"));
        testData.put("assignToResourceId", dbData.get("ResourceId"));
        testData.put("userName", dbData.get("UserName"));

        runtimeState.scenario.write("Username: " + dbData.get("UserName") + ", ResourceId: " + dbData.get("ResourceId") + ", ResourceName: " + dbData.get("ResourceName"));

        if ("Awaiting Acceptance".equals(resourceStatus)) {
            int jobReference;
            switch(jobType) {
            case "Reactive" :
                Job job = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");
                jobReference = job.getJobReference();
                testData.put("faultPriorityId", job.getFaultPriorityId());
                testData.put("faultTypeId", job.getFaultTypeId());
                testData.put("siteId", job.getSiteId());
                testData.put("assetClassificationId", job.getAssetClassificationId());
                break;

            case "Quote" :
                Map<String, Object> queryMap;
                queryMap = new HashMap<String, Object>();
                queryMap.put("jobType", jobType);
                queryMap.put("profileName", "RFM");
                queryMap.put("useResourceTypeName", "False");
                queryMap.put("resourceTypeName", "NA");
                queryMap.put("quoteJobApprovalStatus", "ItqAwaitingAcceptance");
                queryMap.put("quoteJobApprovalStatusId", "18");
                queryMap.put("approvalStatus", "ItqAwaitingAcceptance");
                queryMap.put("approvalStatusId", "2");
                queryMap.put("quotesSubmitted", null);
                queryMap.put("aboveThreshhold", null);
                queryMap.put("fundingRoute", "ignore");
                queryMap.put("multiQuote", "single");
                queryMap.put("assignToResourceId", dbData.get("ResourceId"));

                UserJob userJob = quoteCreationHelper.createQuote(queryMap);
                jobReference = userJob.getJobReference();
                break;

            case "PPM" :
                jobReference = 0;
                break;

            default:
                throw new Exception("Unknown job type: " + jobType);
            }
            testData.put("jobReference", jobReference);
            runtimeState.scenario.write("Jobreference: " + jobReference);

        } else {
            throw new Exception("Unexpected resource status: " + resourceStatus);
        }
    }

    @Given("^resource id \"([^\"]*)\"$")
    public void resource_id(int resourceId) throws Exception {
        Map<String, Object> dbData = dbHelperResources.getResource(resourceId);

        outputHelper.writeMapList(dbData);

        testData.put("resourceId", resourceId);
        testData.put("specificResourceRequestId", resourceId);
        testData.put("userName", dbData.get("UserName"));
        testData.put("profileName", dbData.get("ResourceProfileName"));
    }

    @Given("^using dataset \"([^\"]*)\"$")
    public void using_dataset(String datasetId) throws Exception {
        String env = propertyHelper.getEnv().toLowerCase();

        String pathToCsv = "src/test/resources/spreadsheets/";
        if (env.equals("test_ukrb")) {
            pathToCsv = pathToCsv + "DataSet_Test_UKRB.csv";
        } else if (env.equals("test_uswm")) {
            pathToCsv = pathToCsv + "DataSet_Test_USWM.csv";
        } else if (env.equals("test_usad")) {
            pathToCsv = pathToCsv + "DataSet_Test_USAD.csv";
        } else if (env.equals("uat_uswm")) {
            pathToCsv = pathToCsv + "DataSet_UAT_USWM.csv";
        } else if (env.equals("uat_ukrb")) {
            pathToCsv = pathToCsv + "DataSet_UAT_UKRB.csv";
        } else if (env.equals("uat_usad")) {
            pathToCsv = pathToCsv + "DataSet_UAT_USAD.csv";
        } else if (env.equals("dev_usad")) {
            pathToCsv = pathToCsv + "DataSet_DEV_USAD.csv";
        }

        File csvFile = new File(pathToCsv);
        if (csvFile.isFile()) {
            List<List<String>> records = new ArrayList<List<String>>();
            CSVReader csvReader = new CSVReader(new FileReader(pathToCsv));
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            csvReader.close();

            boolean datasetFound = false;
            List<String> headers = records.get(0);
            for(int i = 1; i < records.size(); i++) {
                if (records.get(i).get(1).equals(datasetId) && records.get(i).get(0).equalsIgnoreCase("Y")) {
                    datasetFound = true;
                    for (int j = 1; j <= headers.size() -1; j++) {
                        if (!headers.get(j).isEmpty()) {
                            testData.put(headers.get(j), records.get(i).get(j));
                        }
                    }
                }
            }

            if (datasetFound) {
                runtimeState.scenario.write("*** Using data: " + testData.toString() + " ***");
                testData.put("dataset", true);

                Map<String, Object> dbData = dbHelperResources.getResourceFromUserName(testData.getString("userName"));
                testData.put("userProfileName", dbData.get("UserProfileName"));
                testData.put("resourceTypeName", dbData.get("ResourceTypeName"));
                testData.put("resourceProfileName", dbData.get("ResourceProfileName"));

            } else {
                runtimeState.scenario.write("*** Dataset NOT Found: " + datasetId + " ***");
                testData.put("dataset", false);
            }

        } else {
            throw new Exception("*** File NOT Found! ***");
        }
    }

    @Given("^using test data from a previous \"([^\"]*)\" run$")
    public void using_test_data_from_previous_run(String status) throws Exception {
        Object prevTestData;
        Map<String, Object> dbData = dbHelperTestData.getTestData();
        assertNotNull("Previous run not found!", dbData);

        if (status.equalsIgnoreCase("PASSED")) {
            prevTestData = dbData.get("TestData");

        } else if (status.equalsIgnoreCase("FAILED")) {
            prevTestData = dbData.get("FailedTestData");

        } else {
            throw new Exception("Invalid status: " + status);
        }

        assertNotNull("Previous " + status + " run not found!", prevTestData);
        testData.set(prevTestData.toString());
        runtimeState.scenario.write("*** Using data: " + testData.toString() + " ***");
        testData.put("dataset", true);
    }
}
