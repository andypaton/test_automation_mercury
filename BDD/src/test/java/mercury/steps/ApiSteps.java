package mercury.steps;

import static mercury.helpers.Constants.AUTOMATION_USER;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.localize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.api.models.resource.Resource;
import mercury.database.dao.JobDao;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.dao.ResourceDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.Job;
import mercury.database.models.ResourceAssignment;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.MonitorHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.ToggleHelper;
import mercury.helpers.apihelper.ApiHelperHangfire;
import mercury.helpers.apihelper.ApiHelperInvoices;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.apihelper.ApiHelperPPM;
import mercury.helpers.apihelper.ApiHelperQuotes;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperMaintenance;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.helpers.dbhelper.DbHelperPPM;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.runtime.RuntimeState;
import mercury.steps.helpdesk.monitors.HelpdeskMonitorJobsSteps;
import mercury.steps.portal.GetPortalUserDataSteps;

public class ApiSteps {

    @Autowired private ApiHelperResources apiHelperResources;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private ApiHelperHangfire apiHelperHangfire;
    @Autowired private JobDao jobDao;
    @Autowired private ResourceDao resourceDao;
    @Autowired private ResourceAssignmentDao resourceAssignmentDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private ToggleHelper toggleHelper;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperMaintenance dbHelperMaintenance;
    @Autowired private MonitorHelper monitorHelper;
    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private ApiHelperInvoices apiHelperInvoices;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private ApiHelperPPM apiHelperPPM;
    @Autowired private DbHelperPPM dbHelperPPM;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private HelpdeskMonitorJobsSteps helpdeskMonitorJobsSteps;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private GetPortalUserDataSteps getPortalUserDataSteps;
    @Autowired private ApiHelperQuotes apiHelperQuotes;

    private static final Logger logger = LogManager.getLogger();

    private List<Job> jobs; // Used internally to store all jobs to be cancelled
    private static Resource resource;
    private List<ResourceAssignment> resourceAssignment;

    int waitFor = 0;
    int numJobs = 0;


    @When("^resource details are requested via the API$")
    public void resource_details_are_requested_via_the_API() throws Throwable {
        mercury.database.models.Resource r = resourceDao.getRandomActiveResource();
        runtimeState.scenario.write("Resource Id: " + r.getId());
        resource = apiHelperResources.getResource(r.getId());
    }

    @ContinueNextStepsOnException
    @Then("^resource details are returned$")
    public void resource_details_are_returned() {
        runtimeState.scenario.write("resource: " + resource.toString());
        assertNotNull("Resource is null !!!", resource);
    }


    /**
     * Does what it says, loops through all jobs and cancels them.
     * @throws Exception
     */
    @When("^the historic jobs are cancelled via the api$")
    public void the_historic_jobs_are_cancelled_via_the_api() throws Exception {
        int i = 0;
        for (Job j : jobs) {
            i++;

            apiHelperJobs.cancelJob(j.getId(), AUTOMATION_USER);
            if ((i % 10) == 0)
                logger.debug(j.getId());

            if (numJobs > 0 && ((i+1) % numJobs) == 0 && (i < jobs.size()) ) {
                // apply throttle for Service Channel (ie. wait <waitFor> seconds after every <numJobs> jobs)
                System.err.println("Waiting for " + waitFor + " seconds");
                Thread.sleep(waitFor * 1000);
            }
        }
    }

    /**
     * Does what it says, loops through all jobs and cancels them.
     * @throws Exception
     */
    @When("^the selected jobs are cancelled via the api$")
    public void the_selected_jobs_are_cancelled_via_the_api() throws Exception {
        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");
        List<Integer> jobsTerminated = new ArrayList<>();

        int i = 0;
        for (Map<String, Object> jobForCancelation : selectedJobs) {
            int jobId = (Integer) jobForCancelation.get("Id");
            int jobReference = (Integer) jobForCancelation.get("JobReference");
            jobsTerminated.add(jobReference);

            apiHelperJobs.cancelJob(jobId, AUTOMATION_USER);

            if (numJobs > 0 && ((i+1) % numJobs) == 0 && (i < selectedJobs.size()) ) {
                // apply throttle for Service Channel (ie. wait <waitFor> seconds after every <numJobs> jobs)
                System.err.println("Waiting for " + waitFor + " seconds");
                Thread.sleep(waitFor * 1000);
            }
        }
        testData.put("jobsTerminated",  jobsTerminated);
        dbHelperMaintenance.removeOnSiteResourcesForTerminatedJobs();
    }

    /**
     * Correctly remove the resource which will then cancel any intial funding requests.
     * @throws Throwable
     */
    @When("^the resources are removed from the historic jobs via the api$")
    public void the_resources_are_removed_from_the_historic_jobs_via_the_api() throws Throwable {
        for (Job j : jobs) {
            resourceAssignment = resourceAssignmentDao.getByJobReference(j.getJobReference());
            for (ResourceAssignment ra : resourceAssignment) {
                if (ra.getResourceAssignmentStatusId() == 23) {
                    // Work Transferred
                    apiHelperJobs.declineJob(j.getJobReference());

                } else {
                    if (ra.getResourceAssignmentStatusId() != 14) apiHelperResources.removeResource(ra.getId());
                    if (ra.getResourceAssignmentStatusId() == 14) apiHelperResources.removeResourceAdvise(ra.getId(), AUTOMATION_USER);
                }
            }
        }
    }

    /**
     * Correctly remove the resource which will then cancel any intial funding requests.
     * @throws Throwable
     */
    @When("^the resources are removed from the selected jobs via the api$")
    public void the_resources_are_removed_from_the_seleced_jobs_via_the_api() throws Throwable {
        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");
        String exceptions = "";

        for (Map<String, Object> jobForCancelation : selectedJobs) {
            int jobReference = (Integer) jobForCancelation.get("JobReference");
            resourceAssignment = resourceAssignmentDao.getByJobReference(jobReference);
            for (ResourceAssignment ra : resourceAssignment) {
                if (ra.getResourceAssignmentStatusId() == 23) {
                    // Work Transferred
                    apiHelperJobs.declineJob(jobReference);

                } else {
                    try {
                        if (ra.getResourceAssignmentStatusId() != 14) apiHelperResources.removeResource(ra.getId());
                        if (ra.getResourceAssignmentStatusId() == 14) apiHelperResources.removeResourceAdvise(ra.getId(), AUTOMATION_USER);
                    } catch (AssertionError e) {
                        exceptions = exceptions + "\n\n" + e.getMessage();
                    }
                }
            }
        }
        testData.put("exceptions", exceptions);
    }

    /**
     * Pulls back a list of all jobs older than the given days Only jobs in status ( 1, 2, 3, 12, 22) will be pulled back
     * and has no resource or resources only allocated.  Excludes all jobs with active resources.
     * @param daysOld
     * @throws Exception
     */
    @And("^historic jobs older than \"([^\"]*)\" days and inactive resources$")
    public void i_have_historic_jobs(Integer daysOld) throws Exception {
        jobs = jobDao.getHistoricJobs(daysOld);
        logger.debug("Total number of record selected: " +jobs.size());
    }

    /**
     * Pulls back a list of all jobs older than the given days Only jobs
     * and has no resource or resources only allocated.  Excludes all jobs with active resources.
     * @param daysOld
     * @throws Exception
     */
    @And("^historic jobs \"([^\"]*)\" older than \"([^\"]*)\" days$")
    public void historic_jobs_older_than(String jobStatus, Integer daysOld) throws Exception {

        switch(jobStatus) {
        case "Awaiting Assignment":
            jobs = jobDao.getHistoricJobsAwaitingAssignment(daysOld);
            break;

        case "Awaiting Acceptance":
            jobs = jobDao.getHistoricJobsAwaitingAcceptance(daysOld);
            break;

        default:
            throw new Exception("Unexpected job status: " + jobStatus);
        }

        for (Job j : jobs) {
            testData.addToList("jobReferences", j.getJobReference());
        }

        runtimeState.scenario.write("Total number of record selected: " + jobs.size());
    }

    /**
     * Pulls back a list of all resource assignments older than the given days Only jobs in status Cancellation Requested
     * and resource assignment status 14 will be pulled back
     * @param daysOld
     * @throws Exception
     */
    @And("^historic cancellation requested jobs older than \"([^\"]*)\" days$")
    public void i_have_historic_cancellation_requested_jobs_older_than(Integer daysOld) throws Exception {
        resourceAssignment = resourceAssignmentDao.getRemovalRequestedJobs(daysOld);
        runtimeState.scenario.write("Total number of records selected :" + resourceAssignment.size());
    }

    @When("^the resources are removed via the api$")
    public void the_resources_are_removed_via_the_api() throws Exception {
        String exceptions = "";
        for (ResourceAssignment ra : resourceAssignment) {
            try {
                if (ra.getResourceAssignmentStatusId() != 14) apiHelperResources.removeResource(ra.getId());
                if (ra.getResourceAssignmentStatusId() == 14) apiHelperResources.removeResourceAdvise(ra.getId(), AUTOMATION_USER);

                testData.addToList("jobsTerminated", dbHelperJobs.getJobReferenceForResourceAssignmentId(ra.getId()));
            } catch (AssertionError e) {
                exceptions = exceptions + "\n\n" + e.getMessage();
            }
        }
        testData.put("exceptions", exceptions);
    }

    @When("^the \"([^\"]*)\" job runs$")
    public void the_hangfire_job_runs(String job) throws Exception {

        switch(job) {
        case "Process Deferred Jobs" :
            apiHelperHangfire.processDeferredJobs();
            break;
        case "Process Deferred Jobs With No Resources" :
            apiHelperHangfire.processDeferredJobsWithNoResources();
            break;
        case "Process Portal Notifications Queue" :
            apiHelperHangfire.processPortalNotificationsQueue();
            break;
        case "Rebuild All Monitor Count Data" :
            apiHelperHangfire.rebuildAllMonitorCountData();
            break;
        case "Process Purchase Order Documents" :
            apiHelperHangfire.processPurchaseOrderDocuments();
            break;
        case "Export Job Updates" :
            apiHelperHangfire.exportJobUpdates();
            break;
        default:
            throw new Exception("unknown hanfire job: " + job);
        }
    }

    @ContinueNextStepsOnException
    @Then("^some historic jobs older than \"([^\"]*)\" days will have been cancelled$")
    public void some_historic_jobs_will_have_been_cancelled(Integer daysOld) throws Exception {
        Integer jobsToBeCancelled = jobDao.getHistoricJobs(daysOld).size();
        assertTrue("No jobs have been cancelled", jobsToBeCancelled <= jobs.size());
    }

    @Then("^the selected jobs have had resources removed$")
    public void the_selected_resources_have_been_removed() throws Exception {
        int cnt = 0;
        if (testData.getIntList("jobsTerminated") != null) {
            for (int jobReference : testData.getIntList("jobsTerminated")) {
                if (dbHelper.getResourceWithResourceAssignmentStatusId(jobReference, 14) != null) {
                    runtimeState.scenario.write("Job " + jobReference + " still has resources with removal requested");
                } else {
                    cnt++;
                }
            }
        }
        runtimeState.scenario.write(cnt + " jobs have had all requested removals completed");
    }

    @ContinueNextStepsOnException
    @Then("^some historic cancellation requested jobs older than \"([^\"]*)\" days will have been cancelled$")
    public void some_historic_cancellation_requested_jobs_will_have_been_cancelled(Integer daysOld) throws Exception {
        Integer resourcesRemoved = resourceAssignmentDao.getRemovalRequestedJobs(daysOld).size();
        assertTrue("No jobs have been cancelled", resourcesRemoved <= resourceAssignment.size());
    }

    @Given("^the BudgetReview toggle is enabled for Rainbow")
    public void BudgetReview_toggle_is_enabled() throws Exception {
        if(LOCALE.equalsIgnoreCase("en-GB")) {
            toggleHelper.enableSystemFeatureToggle("BudgetReview");
        }
    }

    @Given("^the system feature toggle \"([^\"]*)\" is \"([^\"]*)\"$")
    public void the_system_feature_toggle_is_configured(String feature, String state) throws Exception {
        int currentState = dbHelperSystemToggles.getSystemFeatureToggle(feature);
        boolean enabled = "disabled".equals(state) ? false : true;
        if (enabled && currentState == 0) {
            toggleHelper.enableSystemFeatureToggle(feature);
            assertEquals("SystemFeatureToggle not updated", dbHelperSystemToggles.getSystemFeatureToggle(feature), 1);
        } else if ( !enabled && currentState == 1) {
            toggleHelper.disableSystemFeatureToggle(feature);
            assertEquals("SystemFeatureToggle not updated", dbHelperSystemToggles.getSystemFeatureToggle(feature), 0);
        }
    }

    @Given("^the system sub feature toggle \"([^\"]*)\" is \"([^\"]*)\"$")
    public void the_system_sub_feature_toggle_is_configured(String feature, String state) throws Exception {
        boolean toggle = "disabled".equals(state) ? false : true;
        setSubFeatureToggle(feature, toggle);
    }

    private void setSubFeatureToggle(String feature, boolean enabled) throws Exception {
        int currentState = dbHelperSystemToggles.getSystemSubFeatureToggle(feature);
        if (enabled && currentState == 0) {
            toggleHelper.enableSystemSubFeatureToggle(feature);
            assertEquals("SystemSubFeatureToggle not updated", dbHelperSystemToggles.getSystemSubFeatureToggle(feature), 1);
        } else if ( !enabled && currentState == 1) {
            toggleHelper.disableSystemSubFeatureToggle(feature);
            assertEquals("SystemSubFeatureToggle not updated", dbHelperSystemToggles.getSystemSubFeatureToggle(feature), 0);
        }
    }

    @When("^the Service Channel throttle is set to a \"([^\"]*)\" second wait after every \"([^\"]*)\" jobs$")
    public void api_requests_are_made_to_create_jobs_with_second_wait_after_every_created(int waitFor, int numJobs) throws Throwable {
        this.waitFor = waitFor;
        this.numJobs = numJobs;
    }

    @When("^the failed API requests are resubmitted$")
    public void the_failed_API_requests_are_resubmitted() throws Throwable {
        for (String jsonPayload : testData.getArray("failedApiRequests")) {
            try {
                apiHelperJobs.rerunJob(jsonPayload);
            } catch (Exception e) {
                System.err.println("Exception caught while making API request: " + jsonPayload);
            }
        }
    }

    @When("^api requests are made to create jobs$")
    public void api_requests_are_made_to_create_jobs() throws Throwable {
        int maxSize = testData.getIntList("faultTypeId").size();
        for (int i = 0; i < maxSize; i++) {

            int siteId;
            if (testData.getArray("siteIds") != null) {
                siteId = testData.getIntList("siteIds").get(i);
                testData.addToList("siteTypes", dbHelperSites.getSiteTypeId(siteId));

            } else {
                if (testData.getArray("siteTypes") != null) {
                    String siteType = testData.getArray("siteTypes").get(i);
                    siteId = dbHelperSites.getRandomSiteIdForType(siteType);
                } else {
                    siteId = testData.getInt("siteId");
                    testData.addToList("siteTypes", dbHelperSites.getSiteTypeId(siteId));
                }
                testData.addToList("siteIds", siteId);
            }

            int faultTypeId = testData.getIntList("faultTypeId").get(i);
            int assetClassificationId = testData.getIntList("assetClassificationId").get(i);
            int assetSubTypeId = testData.getIntList("assetSubTypeId").get(i);

            if (testData.getArray("locations") == null || (testData.getArray("locations").size() - 1) < i) {
                testData.addToList("locations", dbHelper.getRandomLocationName(siteId));
            }

            String location = testData.getArray("locations").get(i);

            try {
                int siteTypeId = dbHelperSites.getSiteTypeId(siteId);
                int faultPriorityId = dbHelper.getFaultPriorityId(siteTypeId, assetSubTypeId, assetClassificationId, faultTypeId);

                String description = "job created from test automation api call";
                int callerId = dbHelper.getRandomCaller("Resource");

                int locationId = dbHelper.getLocationId(location);

                ApplicationUser applicationUser = jobCreationHelper.getCurrentUserForJobCreation();
                String createdBy = applicationUser.getId();

                mercury.api.models.job.Job job = apiHelperJobs.createNewJob(1, siteId, faultTypeId, faultPriorityId, description, callerId, assetClassificationId, locationId, createdBy);

                testData.addToList("jobReferences", job.getJobReference());

            } catch (Exception e) {
                testData.addToList("jobReferences", 0);
            }

            if (numJobs > 0 && ((i+1) % numJobs) == 0 && (i < (maxSize - 1)) ) {
                // apply throttle for Service Channel (ie. wait <waitFor> seconds after every <numJobs> jobs)
                System.err.println("Waiting for " + waitFor + " seconds");
                Thread.sleep(waitFor * 1000);
            }
        }
    }

    @When("^jobs are created via api calls$")
    public void api_requests_are_made_to_create_jobs1() throws Throwable {
        mercury.api.models.job.Job job = null;
        int numberOfJobs = testData.getInt("numberOfJobs");
        String jobResourceStatus = testData.getString("jobResourceStatus");

        for (int i = 1; i <= numberOfJobs; i++) {
            job = jobCreationHelper.createJobInStatus(jobResourceStatus);
            String message = String.format("Created Job %s, ",job.getJobReference() );
            runtimeState.scenario.write(message);
            testData.addToList("createdJobs", job.getJobReference());

        }
    }

    @When("^\"([^\"]*)\" jobs are created in Mercury$")
    public void jobs_are_created_in_mercury(String jobType) throws Throwable {
        Integer numberOfJobs = Integer.valueOf(System.getProperty("NumberOfJobs"));
        for (int i = 1; i <= numberOfJobs; i++) {
            if (jobType.equalsIgnoreCase("Reactive")) {
                mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Logged");
                runtimeState.scenario.write(jobType + " job - " + job.getJobReference() + " has been created");
                testData.addToList("createdJobs", job.getJobReference());

            } else if (jobType.equalsIgnoreCase("Quote")) {
                String fundingRoute = dbHelperQuotes.getRandomFundingRouteSingleQuote();

                //needs reworked when there is time, so that full process can be automated via api requests
                //                getPortalUserDataSteps.a_with_a_in_state_with_a_funding_route_with_budget("RFM", "single", jobType, "Quotes Awaiting Review", fundingRoute, "less");
                //                List<Map<String, Object>> quoteDetails = dbHelperQuotes.getQuoteDetails(testData.getInt("jobReference"));
                //                BigDecimal quoteValueBD = (BigDecimal) quoteDetails.get(0).get("QuoteValue");
                //                int quoteValue = quoteValueBD.intValue();
                //                apiHelperQuotes.approveQuoteAwaitingReview((int) quoteDetails.get(0).get("QuoteApprovalScenarioId"), (int) quoteDetails.get(0).get("ResourceId"), (int) quoteDetails.get(0).get("PortalResourceId"), testData.getInt("jobReference"), (int) quoteDetails.get(0).get("FundingRouteID"), quoteValue);

                helpdeskMonitorJobsSteps.a_user_has_jobs_with_status1(jobType, "New Job Notification Sent", fundingRoute, "single");
                runtimeState.scenario.write(jobType + " job - " + testData.getInt("jobReference") + " has been created");
                testData.addToList("createdJobs", testData.getInt("jobReference"));

            } else if (jobType.equalsIgnoreCase("Multi-Quote")) {
                String fundingRoute = dbHelperQuotes.getRandomFundingRouteMultiQuote();
                helpdeskMonitorJobsSteps.a_user_has_jobs_with_status1(jobType, "New Job Notification Sent", fundingRoute, "Multi");
                runtimeState.scenario.write(jobType + " job - " + testData.getInt("jobReference") + " has been created");
                testData.addToList("createdJobs", testData.getInt("jobReference"));

            } else if (jobType.equalsIgnoreCase("PPM")) {
                String resourceProfile = dbHelperResources.getRandomResourceProfileForPpmType();
                String ppmType = dbHelperPPM.getPpmTypeForResourceProfile(resourceProfile);
                int resourceProfileId = dbHelperResources.getResourceProfileId(resourceProfile);

                Random random = new Random();

                if (ppmType == null) {
                    String name = "Test_" + resourceProfile + "_" + System.currentTimeMillis();
                    String assetTypeId = dbHelper.getRandomActiveAssetId();
                    ppmType = apiHelperPPM.createPpmType(name, "1", assetTypeId, 1, String.valueOf(resourceProfileId), null, "2", 52, "No Override", "1");
                }

                int callOutRate = random.nextInt(100);
                String siteId = String.valueOf(dbHelperSites.getRandomSiteId());
                String dueDate = DateHelper.getNowDatePlusOffset(48, "yyyy/MM/dd").replaceAll("/", "-");
                dueDate += "T23:00:00.000Z";
                int resourceId = dbHelperResources.getRandomResourceWithResourceProfile(resourceProfile);
                String ppmJobRef = apiHelperPPM.createPpmJobWithResource(ppmType, callOutRate, siteId, String.valueOf(resourceProfileId), String.valueOf(resourceId), dueDate);
                int ppmJob = Integer.parseInt(ppmJobRef);
                testData.addToList("createdJobs", ppmJob);

                apiHelperHangfire.processPPMJobs();
                Thread.sleep(60000);
            }

        }
    }

    @When("^PPM jobs are created for site$")
    public void ppm_jobs_are_created_for_site() throws Throwable {
        String env = propertyHelper.getEnv().toLowerCase();
        String pathName = null;
        runtimeState.domainUser = System.getProperty("user.name").toLowerCase();

        if (runtimeState.domainUser.contains("jenkins")) {
            pathName = "E:\\Jenkins\\workspace\\(environment)\\NamedCityResourceLive.xlsx";
        } else {
            pathName = "Y:\\workspace\\(environment)\\NamedCityResourceLive.xlsx";
        }

        if (env.contains("ukrb")) {
            pathName = pathName.replace("(environment)", "CreatePPMJob_Rainbow");
        } else if (env.contains("uswm")) {
            pathName = pathName.replace("(environment)", "CreatePPMJob_Walmart");
        } else if (env.contains("usad")) {
            pathName = pathName.replace("(environment)", "CreatePPMJob_Advocate");
        }

        File file = new File(pathName);
        FileInputStream ExcelFile = new FileInputStream(file);
        XSSFWorkbook ExcelWBook = new XSSFWorkbook(ExcelFile);
        int numberOfSheets = ExcelWBook.getNumberOfSheets();

        for (int i = 0; i < numberOfSheets; i++) {
            XSSFSheet ExcelWSheet = ExcelWBook.getSheetAt(i);

            assertTrue("Worksheet Not Found", ExcelWSheet != null);
            int numberOfRows = ExcelWSheet.getLastRowNum();

            for (int j = 1; j <= numberOfRows; j++) {
                List<String> headers = new ArrayList<>();
                XSSFRow row = ExcelWSheet.getRow(0);

                for (int h = 0; h < row.getLastCellNum(); h++) {
                    headers.add(row.getCell(h).getStringCellValue());
                }

                int pos = headers.indexOf("Username");
                assertTrue("Username header not found", pos != -1);
                XSSFCell Cell = ExcelWSheet.getRow(j).getCell(pos);
                String username = Cell.getStringCellValue();

                pos = headers.indexOf("Site");
                assertTrue("Site header not found", pos != -1);
                Cell = ExcelWSheet.getRow(j).getCell(pos);
                String siteCode = null;
                try {
                    siteCode = Cell.getStringCellValue();
                } catch (Exception e) {
                    int site = (int) Cell.getNumericCellValue();
                    siteCode = String.valueOf(site);
                }

                //Atm Walmart code is behind and does not have the new PPM changes as Advocate
                //So when creating a PPM for a specific resource, the resource needs to be added to the PPM Type
                //For Advocate, the resource is added to the PPM

                if (dbHelperResources.isUserNamePresentInDb(username)) {

                    if (dbHelperSites.isSitePresentInDb(Integer.valueOf(siteCode))) {

                        String resourceProfile = dbHelperResources.getResourceProfileName(username);
                        int resourceProfileId = dbHelperResources.getResourceProfileId(resourceProfile);

                        Random random = new Random();

                        String name = "Test_" + resourceProfile + "_" + System.currentTimeMillis();
                        String assetTypeId = dbHelper.getRandomActiveAssetId();
                        int resourceId = dbHelperResources.getResourceIdFromUserName(username);
                        String ppmType = apiHelperPPM.createPpmType(name, "1", assetTypeId, 1, String.valueOf(resourceProfileId), String.valueOf(resourceId), "3", 12, "No Override", "1");

                        if (!ppmType.equals("Fail")) {
                            int callOutRate = random.nextInt(100);
                            int siteId = dbHelperSites.getSiteIdFromSiteCode(siteCode);
                            String dueDate = DateHelper.getNowDatePlusOffset(48, "yyyy/MM/dd").replaceAll("/", "-");
                            dueDate += "T23:00:00.000Z";
                            String ppmJobRef = apiHelperPPM.createPpmJob(ppmType, callOutRate, String.valueOf(siteId), dueDate);
                            if (!ppmJobRef.equals("Fail")) {
                                runtimeState.scenario.write("PPM Job - " + ppmJobRef + " has been created for Username - " + username + " and Site - " + siteCode);
                                testData.addToList("createdJobs", Integer.valueOf(ppmJobRef));
                            }
                        }

                    } else {
                        runtimeState.scenario.write("PPM Job has not been created as Site - " + siteCode + " does not exist in the database");
                    }

                } else {
                    runtimeState.scenario.write("PPM Job has not been created as Username - " + username + " does not exist in the database");
                }
            }
        }
    }

    @When("^PPM jobs are created$")
    public void ppm_jobs_are_created() throws Throwable {
        String env = propertyHelper.getEnv().toLowerCase();
        String pathName = null;
        runtimeState.domainUser = System.getProperty("user.name").toLowerCase();

        if (runtimeState.domainUser.contains("jenkins")) {
            pathName = "E:\\Jenkins\\workspace\\(environment)\\NamedCityResourceLive.xlsx";
        } else {
            pathName = "Y:\\workspace\\(environment)\\NamedCityResourceLive.xlsx";
        }

        if (env.contains("ukrb")) {
            pathName = pathName.replace("(environment)", "CreatePPMJob_Rainbow");
        } else if (env.contains("uswm")) {
            pathName = pathName.replace("(environment)", "CreatePPMJob_Walmart");
        } else if (env.contains("usad")) {
            pathName = pathName.replace("(environment)", "CreatePPMJob_Advocate");
        }

        File file = new File(pathName);
        FileInputStream ExcelFile = new FileInputStream(file);
        XSSFWorkbook ExcelWBook = new XSSFWorkbook(ExcelFile);
        int numberOfSheets = ExcelWBook.getNumberOfSheets();

        for (int i = 0; i < numberOfSheets; i++) {
            XSSFSheet ExcelWSheet = ExcelWBook.getSheetAt(i);

            assertTrue("Worksheet Not Found", ExcelWSheet != null);
            int numberOfRows = ExcelWSheet.getLastRowNum();

            for (int j = 1; j <= numberOfRows; j++) {
                List<String> headers = new ArrayList<>();
                XSSFRow row = ExcelWSheet.getRow(0);

                for (int h = 0; h < row.getLastCellNum(); h++) {
                    headers.add(row.getCell(h).getStringCellValue());
                }

                int pos = headers.indexOf("Username");
                assertTrue("Username header not found", pos != -1);
                XSSFCell Cell = ExcelWSheet.getRow(j).getCell(pos);
                String username = Cell.getStringCellValue();

                //Atm Walmart code is behind and does not have the new PPM changes as Advocate
                //So when creating a PPM for a specific resource, the resource needs to be added to the PPM Type
                //For Advocate, the resource is added to the PPM

                if (dbHelperResources.isUserNamePresentInDb(username)) {

                    String resourceProfile = dbHelperResources.getResourceProfileName(username);
                    String ppmType = dbHelperPPM.getPpmTypeForResourceProfile(resourceProfile);
                    int resourceProfileId = dbHelperResources.getResourceProfileId(resourceProfile);

                    Random random = new Random();

                    if (ppmType == null) {
                        String name = "Test_" + resourceProfile + "_" + System.currentTimeMillis();
                        String assetTypeId = dbHelper.getRandomActiveAssetId();
                        ppmType = apiHelperPPM.createPpmType(name, "1", assetTypeId, 1, String.valueOf(resourceProfileId), null, "2", 52, "No Override", "1");
                        runtimeState.scenario.write("PPM Type - " + ppmType + " has been created");
                    }

                    int callOutRate = random.nextInt(100);
                    String siteId = String.valueOf(dbHelperSites.getRandomSiteId());
                    String dueDate = DateHelper.getNowDatePlusOffset(48, "yyyy/MM/dd").replaceAll("/", "-");
                    dueDate += "T23:00:00.000Z";
                    int resourceId = dbHelperResources.getResourceIdFromUserName(username);
                    String ppmJobRef = apiHelperPPM.createPpmJobWithResource(ppmType, callOutRate, siteId, String.valueOf(resourceProfileId), String.valueOf(resourceId), dueDate);
                    runtimeState.scenario.write("PPM Job - " + ppmJobRef + " has been created for Username - " + username);
                    testData.addToList("createdJobs", Integer.valueOf(ppmJobRef));

                } else {
                    runtimeState.scenario.write("PPM Job has not been created as Username - " + username + " does not exist in the database");
                }
            }
        }
    }


    @Given("^the monitor \"([^\"]*)\" from \"([^\"]*)\" area is enabled$")
    public void the_monitor_from_area_is(String monitor, String area) throws Throwable {
        List<Map<String, Object>> dbData = dbHelperMonitors.getMonitorSettingsDetails(monitor, area);
        testData.addAllMap("monitorUpdate", dbData);
        testData.addStringTag("monitorDisplayArea", area);
        boolean activeFlag = (boolean) dbData.get(0).get("Active");

        if (!activeFlag) {
            testData.addStringTag("activatedMonitor", monitor);
            monitorHelper.enableMonitor();
        }
    }

    @Given("^the mandatory invoice line type \"([^\"]*)\" is enabled$")
    public void the_mandatory_invoice_line_type_linetype_is_enabled(String lineType) throws Throwable {
        Map<String, Object> dbData = dbHelperInvoices.getInvoiceLineType(localize(lineType));

        if (!(dbData.get("Description").equals(localize(lineType)) && dbData.get("IsMandatory").equals(true))) {
            apiHelperInvoices.updateLineType(dbData, "true", localize(lineType));
        }
    }

    @And("^the invoice line type \"([^\"]*)\" is set as labour$")
    public void the_invoice_line_type_linetype_is_set_as_labour(String lineType) throws Throwable {
        the_mandatory_invoice_line_type_linetype_is_enabled(localize(lineType));
        Map<String, Object> dbData = dbHelperInvoices.getInvoiceLineType(localize(lineType));

        if (!(dbData.get("Description").equals(localize(lineType)) && dbData.get("IsLabor").equals(true))) {
            apiHelperInvoices.updateLineType(dbData, "true", localize(lineType));
        }
    }
}