package mercury.steps.helpdesk.search;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.AUTOMATION_USER;
import static mercury.helpers.Constants.DB_POLLING_INTERVAL;
import static mercury.helpers.Constants.TWO_MINUTES;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.toCurrency;
import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.steps.CommonSteps.assertDataFound;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import mercury.database.dao.FundingRequestDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.LogJobDataDao;
import mercury.database.dao.SiteVisitCylinderDetailsDao;
import mercury.database.dao.SiteVisitsDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.Job;
import mercury.database.models.LogJobData;
import mercury.database.models.SiteVisitCylinderDetails;
import mercury.database.models.SiteVisits;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.LogJobHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.QuoteCreationHelper;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.pageobject.web.PopupAlert;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.helpdesk.jobs.HelpdeskAcceptJobSteps;
import mercury.steps.helpdesk.jobs.HelpdeskLogAJobSteps;
import mercury.steps.helpdesk.resources.HelpdeskFundingRequestsSteps;
import mercury.steps.helpdesk.resources.HelpdeskManageResourcesSteps;
import mercury.steps.portal.GetPortalUserDataSteps;

public class HelpdeskSearchForJobSteps {

    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private OutputHelper outputHelper;
    @Autowired private JobDao jobDao;
    @Autowired private SiteVisitsDao siteVisitsDao;
    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private NewJob job;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private HelpdeskSearchForSiteSteps helpdeskSearchForSiteSteps;
    @Autowired private HelpdeskAcceptJobSteps helpdeskAcceptJobSteps;
    @Autowired private HelpdeskFundingRequestsSteps helpdeskFundingRequestsSteps;
    @Autowired private HelpdeskManageResourcesSteps helpdeskManageResourcesSteps;
    @Autowired private HelpdeskLogAJobSteps helpdeskLogAJobSteps;
    @Autowired private CommonSteps commonSteps;
    @Autowired private SiteVisitCylinderDetailsDao siteVisitCylinderDetailsDao;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private FundingRequestDao fundingRequestDao;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private ApiHelperResources apiHelperResources;
    @Autowired private GetPortalUserDataSteps getPortalUserDataSteps;
    @Autowired private QuoteCreationHelper quoteCreationHelper;
    @Autowired private LogJobDataDao logJobDataDao;

    private String searchStr;


    @When("^a search is run for a random job$")
    public void a_search_is_run_for_a_job() throws Throwable {

        Job job = jobDao.getRandomJob();
        if (job == null) {
            throw new PendingException("No test data found");
        }

        testData.put("jobStatusId", job.getJobStatusId());
        testData.put("jobReference", job.getJobReference());
        if (job.getAssetId() != null) {
            testData.put("assetId", job.getAssetId());
        }

        boolean quoteRequested = job.getJobTypeId() == 2 ? true : false;
        testData.put("isQuoteRequested", quoteRequested);

        searchStr = String.valueOf(job.getJobReference());
        runtimeState.scenario.write("Searching for: " + searchStr);

        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(searchStr).get();
        outputHelper.takeScreenshots();
    }

    @Given("^a \"([^\"]*)\" job is created and searched for$")
    public void logged_job_is_created_and_searched_for(String status) throws Throwable {
        mercury.api.models.job.Job job = null;
        if (status.equalsIgnoreCase("Logged")) {
            job = jobCreationHelper.createJobInStatus("Logged");
        } else if (status.equalsIgnoreCase("Cancelled")) {
            job = jobCreationHelper.createJobInStatus("Cancelled");
        } else if (status.equalsIgnoreCase("Fixed")) {
            job = jobCreationHelper.createJobInStatus("Fixed / Complete");
        }

        Integer jobReference = job.getJobReference();
        testData.put("jobReference", jobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        outputHelper.takeScreenshots();
    }

    @Given("^a search is run for the job reference$")
    public void a_search_is_run_for_the_job_reference() throws Throwable {

        mercury.api.models.job.Job apiJob;
        Integer jobReference = null;

        if (testData.getBoolean("dataset")) {
            apiJob = jobCreationHelper.createJobInStatus("Logged / Awaiting Assignment");
            jobReference = apiJob.getJobReference();

        } else {

            String jobStatusName = testData.getString("jobStatusName");
            String jobTypeName = testData.getString("jobTypeName");
            String resourceAssignmentStatusName = testData.getString("resourceAssignmentStatusName");
            String resourceTypeName = testData.getString("resourceTypeName");
            String jobTypeChanged = testData.getString("jobTypeChanged");

            Job randomJob;

            if (testData.getString("resourceAssigned") != null && !testData.getBoolean("resourceAssigned")) {
                randomJob = jobDao.getRandomJob(jobStatusName, jobTypeName, "NONE", resourceTypeName);
            } else if (testData.getBoolean("duplicate")) {
                randomJob = jobDao.getRandomDuplicateJob(jobStatusName, jobTypeName, resourceAssignmentStatusName, resourceTypeName);

            } else if (testData.getInt("jobReference") != null) {
                randomJob = jobDao.getByJobReference(testData.getInt("jobReference"));

            } else if (testData.getString("jobTypeChanged") != null) {
                randomJob = jobDao.getRandomJob(jobStatusName, jobTypeName, resourceAssignmentStatusName, resourceTypeName, jobTypeChanged);

            } else {
                randomJob = jobDao.getRandomJob(jobStatusName, jobTypeName, resourceAssignmentStatusName, resourceTypeName);
            }

            if (randomJob == null) {
                if (testData.getString("jobTypeChanged") != null) {
                    if ("is not".equalsIgnoreCase(testData.getString("jobTypeChanged")) || "is".equalsIgnoreCase(testData.getString("jobTypeChanged"))) {
                        if (testData.get("resourceAssignmentStatusName") == null || testData.get("resourceAssignmentStatusName").equals("NONE")) {
                            apiJob = jobCreationHelper.createJobInStatus("Logged / Awaiting Assignment");
                        } else {
                            apiJob = jobCreationHelper.createJobInStatus("Logged");
                        }
                    } else {
                        Map<String, Object> queryMap = new HashMap<String, Object>();
                        queryMap.put("quoteJobApprovalStatus", "AwaitingQuoteRequestApproval");
                        queryMap.put("profileName", "RFM");
                        queryMap.put("fundingRoute", "ignore");
                        queryMap.put("multiQuote", "single");
                        apiJob = quoteCreationHelper.createAndRejectQuote(queryMap);
                    }
                    jobReference = apiJob.getJobReference();
                } else {
                    throw new PendingException(String.format("Cannot find suitable test data: no job found with jobStatusName=%s, jobTypeName=%s, resourceAssignmentStatusName=%s, resourceTypeName=%s, resourceTypeChanged=%s", jobStatusName, jobTypeName, resourceAssignmentStatusName, resourceTypeName, jobTypeChanged));
                }
            } else {
                jobReference = randomJob.getJobReference();
            }
        }

        testData.put("jobReference", jobReference);

        job.setJobReference(jobReference);
        runtimeState.scenario.write("test data selected: jobReference = " + jobReference);

        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a cancelled job$")
    public void a_search_is_run_for_a_cancelled_job() throws Throwable {
        Job job = null;
        if (testData.getString("resourceTypeName") != null && testData.getString("resourceTypeName").equals("single")) {
            job = jobDao.getRandomCancelledJobWithSingleResource();
        } else {
            job = jobDao.getRandomCancelledJob();
        }

        mercury.api.models.job.Job newJob = null;
        if (job == null) {
            newJob = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");
            apiHelperJobs.cancelJob(newJob.getId(), AUTOMATION_USER);
        }

        testData.put("jobReference", (job != null ? job.getJobReference() : newJob.getJobReference()));
        runtimeState.scenario.write("JobReference: " + testData.getString("jobReference"));

        String clientStatusPriorToCancellation = dbHelper.getStatusNamePriorToCancellation(testData.getInt("jobReference"));
        testData.put("clientStatusPriorToCancellation", clientStatusPriorToCancellation);
        runtimeState.scenario.write("client status prior to cancellation: " + clientStatusPriorToCancellation);

        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a cancelled \"([^\"]*)\" job$")
    public void a_search_is_run_for_a_cancelled_job_of_type(String jobType) throws Throwable {
        Job job = jobDao.getRandomCancelledJobOfType(jobType);
        if (job == null) {
            throw new PendingException("No test data found: unlocked Cancelled job");
        }
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("JobReference: " + job.getJobReference());

        String clientStatusPriorToCancellation = dbHelper.getStatusNamePriorToCancellation(testData.getInt("jobReference"));
        testData.put("clientStatusPriorToCancellation", clientStatusPriorToCancellation);
        runtimeState.scenario.write("client status prior to cancellation: " + clientStatusPriorToCancellation);

        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @When("^a search is run for a job with a \"([^\"]*)\" FGAS regulated site visit and \"([^\"]*)\"$")
    public void a_search_is_run_for_a_job_with_a_FGAS_regulated_site_visit_and(String version, String timelineEvent) throws Throwable {

        String resourceAssignmentEventType;
        String resourceAssignmentStatus;
        String jobStatus;
        String returning;
        switch (timelineEvent) {

        case "Resource returning":
            resourceAssignmentEventType = "Resource Returning";
            resourceAssignmentStatus = "Complete / Work Transferred";
            jobStatus = "In Progress / Returning";
            returning = "is";
            break;

        case "Resource Awaiting Parts":
            resourceAssignmentEventType = null;
            resourceAssignmentStatus = "Awaiting Parts";
            jobStatus = "In Progress / Awaiting Parts";
            returning = "is";
            break;

        case "Gas details updated":
            resourceAssignmentEventType = "Refrigerant Gas Used";
            resourceAssignmentStatus = "Awaiting Parts Review";
            jobStatus = "In Progress / Resource Awaiting Parts Review";
            returning = "is";
            break;

        case "Complete":
        default:
            resourceAssignmentEventType = "Assignment Completed";
            resourceAssignmentStatus = "Complete";
            jobStatus = "Fixed / Complete";
            returning = "is not";
            break;
        }


        SiteVisits siteVisit;
        if ("2019".equals(version)) {

            siteVisit = await().pollInterval(DB_POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> siteVisitsDao.getSiteVisitFor2019Regulations(resourceAssignmentEventType, resourceAssignmentStatus), notNullValue());

            if (siteVisit == null) {
                testData.addBooleanTag("useResourceTypeName", true);
                testData.addStringTag("resourceTypeName", "City Resource");
                testData.addStringTag("profileName", "NA");

                getPortalUserDataSteps.I_have_a_gas_job_with_possible_asset("reactive", jobStatus, "with", "without", "Refrigeration", returning);

                siteVisit = await().pollInterval(DB_POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> siteVisitsDao.getSiteVisitFor2019Regulations(resourceAssignmentEventType, resourceAssignmentStatus), notNullValue());
                CommonSteps.assertDataFound(siteVisit);
            }

        } else {
            siteVisit = siteVisitsDao.getSiteVisitForPre2019Regulations(resourceAssignmentEventType);
        }

        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", version);
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    @When("^a search is run for a job that has been edited by a Gas Refrigeration Administrator$")
    public void a_search_is_run_for_a_job_that_has_been_edited_by_a_Gas_Refrigeration_Administrator() {
        int jobReference = dbHelperGas.getRandomJobUpdatedByAdmin();
        searchForJob(jobReference);
    }

    @When("^a search is run for a job with a \"([^\"]*)\" FGAS regulated site visit$")
    public void a_search_is_run_for_a_job_with_gas_site_visit(String version) throws Throwable {
        SiteVisits siteVisit;
        if ("2019".equals(version)) {
            siteVisit = siteVisitsDao.getSiteVisitFor2019RegulationsWithRefrigerantDetails();
        }else {
            siteVisit = siteVisitsDao.getSiteVisitForPre2019Regulations();
        }

        assertDataFound("No site visit found for " + version + " regulations", siteVisit);
        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", version);
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    @When("^a search is run for a job with a 2019 FGAS regulated site visit with a \"([^\"]*)\" partial cylinder$")
    public void a_search_is_run_for_a_job_with_gas_site_visit_with_cylinder(String qty) throws Throwable {
        boolean fullyUsed = qty.equals("Fully Used");
        SiteVisits siteVisit = siteVisitsDao.getSiteVisitFor2019RegulationsWithCylinderQty(fullyUsed);

        if (siteVisit == null) {
            siteVisit = siteVisitsDao.getSiteVisitFor2019RegulationsWithCylinderQty(!fullyUsed);
            assertDataFound("No suitable site visit found", siteVisit);

            List<SiteVisitCylinderDetails> cylinders = siteVisitCylinderDetailsDao.getForSiteVisitId(siteVisit.getId());
            for (SiteVisitCylinderDetails cylinder : cylinders) {
                if (cylinder.isPartialCylinder()) {
                    Double initialQuantity = cylinder.getInitialQuantity();
                    if (fullyUsed) {
                        dbHelperGas.updateCylinderQtys(cylinder.getId(), initialQuantity, 0.0);
                    } else {
                        if (initialQuantity < 3) {
                            dbHelperGas.updateCylinderInitialQty(cylinder.getId(), 3.0, 1.0);
                            dbHelperGas.updateCylinderQtys(cylinder.getId(), 2.0, 1.0);
                        } else {
                            dbHelperGas.updateCylinderQtys(cylinder.getId(), initialQuantity - 1, 1.0);
                        }
                    }
                    break;
                }
            }
        }

        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    @When("^a search is run for a job with a 2019 FGAS regulated site visit with installed gas at max capacity$")
    public void a_search_is_run_for_a_job_with_gas_site_visit() throws Throwable {
        SiteVisits siteVisit = siteVisitsDao.getSiteVisitWithAsset();
        assertDataFound("No site visit found for 2019 regulations", siteVisit);
        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());
        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);
        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());

        // store original max refrigerant charge
        float maxCharge = testData.getFloat("maxGasCharge");
        int assetId = dbHelperGas.getAssetIdForSiteVisit(siteVisit.getId());
        testData.put("origRefrigerantCharge", maxCharge);
        testData.put("assetId", assetId);

        // update max refrigerant charge
        float totalGasUsed = dbHelperGas.getTotalGasUsedForSiteVisit(siteVisit.getId());
        dbHelperGas.updateRefrigerantCharge(assetId, totalGasUsed);
        testData.put("maxGasCharge", totalGasUsed);
        runtimeState.scenario.write("Max refrigerant charge for asset set to: " + totalGasUsed);

        searchForJob(jobReference);
    }

    @When("^a search is run for a job with a 2019 FGAS regulated site visit with a Remote System Appliance Type$")
    public void a_search_is_run_for_a_job_with_a_2019_FGAS_regulated_site_visit_with_a_Remote_System_Appliance_Type() throws Throwable {
        SiteVisits siteVisit = siteVisitsDao.getSiteVisitFor2019RegulationsWithRemoteSystemApplianceType();

        if (siteVisit == null) {

            testData.put("gasUsage", true);
            testData.put("jobType", "reactive");
            testData.put("jobStatus", "In Progress");
            testData.put("inPlant", false);
            testData.put("leakCheck", true);
            testData.put("isRequiredToReturn", false);
            testData.put("asset", true);

            jobCreationHelper.createJobInStatus("In Progress");

            siteVisit = siteVisitsDao.getSiteVisitFor2019RegulationsWithRemoteSystemApplianceType();
            assertDataFound("No site visit found for 2019 regulations", siteVisit);
        }

        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());
        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    @When("^a search is run for a job with multiple leak site checks, but NOT with multiple site visits requiring a return visit$")
    public void a_search_is_run_for_a_job_with_a_FGAS_regulated_site_visit_with_Leak_Site_Checks() throws Throwable {
        SiteVisits siteVisit = siteVisitsDao.getSiteVisitWithLeakSiteChecks();

        assertDataFound("No site visit found for 2019 regulated site visit with multiple Leak Site Checks", siteVisit);
        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    @When("^a search is run for a job with multiple 2019 FGAS regulated site visits with Leak Site Check failures$")
    public void a_search_is_run_for_a_job_with_multiple_FGAS_regulated_site_visit_with_Leak_Site_Check_failures() throws Throwable {
        SiteVisits siteVisit = siteVisitsDao.getSiteVisitWithLeakSiteCheckFailure(true);

        assertDataFound("No FGAS job found with multiple Leak Site Check failures", siteVisit);

        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    @When("^a search is run for a job with 2019 FGAS regulated site visits? \"([^\"]*)\" Leak Site Check failures$")
    public void a_search_is_run_for_a_job_with_a_FGAS_regulated_site_visit_without_a_Leak_Site_Check_failure(String failure) throws Throwable {
        boolean withFailure = "without".equals(failure) ? false: true;
        SiteVisits siteVisit = siteVisitsDao.getSiteVisitWithLeakSiteCheckFailure(withFailure);

        assertDataFound("No FGAS job found " + failure + " Leak Site Check failures", siteVisit);

        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    @When("^a search is run for a job with a completed 2019 FGAS regulated site visit ((?:with|without)) a Leak Site Check$")
    public void a_search_is_run_for_a_job_with_a_completed_FGAS_regulated_site_visit_with_a_Leak_Site_Check(String withLeakSiteCheck) throws Throwable {

        boolean withLSC = "with".equals(withLeakSiteCheck);
        SiteVisits siteVisit = siteVisitsDao.getSiteVisitWithLeakSiteCheck(withLSC);

        assertDataFound("No site visit found for 2019 regulated site visit " + withLeakSiteCheck + " a Leak Site Check", siteVisit);
        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    @When("^a search is run for a job with a 2019 FGAS regulated site visit without a Leak Check performed$")
    public void a_search_is_run_for_a_job_with_a_FGAS_regulated_site_visit_without_a_Leak_Check_performed() throws Throwable {

        SiteVisits siteVisit = siteVisitsDao.getSiteVisitWithoutLeakCheck();

        if (siteVisit == null) {
            String msg = propertyHelper.getEnv().toUpperCase().contains("UKRB") ? "Leak Check Not Performed: option is not available for UKRB" : "No data found";
            throw new PendingException(msg);
        }

        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }

    private void searchForJob(int jobReference) {
        testData.put("jobReference", jobReference);
        runtimeState.scenario.write("JobReference: " + jobReference);

        if (getWebDriver().getCurrentUrl().contains("EditGasUsage")) {

            // Admin > Refrigerant Gas Usage search
            runtimeState.adminRefrigerantGasUsageSearchPage.enterJobReference(jobReference);
            outputHelper.takeScreenshots();
            runtimeState.adminRefrigerantGasUsageEditPage = runtimeState.adminRefrigerantGasUsageSearchPage.search();

        } else {
            // Helpdesk search
            outputHelper.takeScreenshots();
            runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(jobReference);

            // if Job Locked popup is dislpayed then close it
            if (runtimeState.helpdeskJobPage.isAlertVisible()) {
                runtimeState.popupAlert = new PopupAlert(getWebDriver());
                runtimeState.popupAlert.ok();
            }
        }
    }

    private void saveSiteVisit(SiteVisits siteVisit) throws ParseException {
        testData.put("siteVisit", siteVisit);
        testData.put("resourceAssignmentId", siteVisit.getResourceAssignmentId());
        testData.put("totalGasUsed", dbHelperGas.getTotalGasUsedForSiteVisit(siteVisit.getId()));
        testData.put("maxGasCharge", dbHelperGas.getRefrigerantCharge(siteVisit.getId()));
    }

    @And("^a search is run for a fixed job$")
    public void a_search_is_run_for_a_fixed_job() throws Throwable {
        Job job = jobDao.getRandomFixedJob();
        if (job == null) {
            throw new PendingException("No test data found: unlocked Fixed job");
        }
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a logged job$")
    public void a_search_is_run_for_a_logged_job() throws Throwable {
        Job job = jobDao.getRandomLoggedJob();
        if (job == null) {
            throw new PendingException("No test data found");
        }
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a logged job \"([^\"]*)\"$")
    public void a_search_is_run_for_a_logged_job(String outstandingParts) throws Throwable {

        Job job = null;
        mercury.api.models.job.Job newJob = null;
        if(outstandingParts.equals("No outstanding Parts Order")) {
            job = jobDao.getLoggedJobWithoutOutstandingPartsOrder();
            if (job == null) {
                newJob = jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance");
            }
        } else if(outstandingParts.equals("With outstanding Parts Order")) {
            job = jobDao.getLoggedJobWithOutstandingPartsOrder();
            if (job == null) {
                newJob = jobCreationHelper.createJobInStatus("Parts Awaiting Approval");
            }
        }
        testData.put("jobReference", (job != null ? job.getJobReference() : newJob.getJobReference()));
        runtimeState.scenario.write("JobReference: " + testData.getString("jobReference"));
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a logged job with a single site contact$")
    public void a_search_is_run_for_a_logged_job_with_a_single_site_contact() throws Throwable {
        Job job = jobDao.getRandomLoggedJobWithSingleSiteContact();
        if (job == null) {
            throw new PendingException("No test data found");
        }
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a logged job with attachments$")
    public void a_search_is_run_for_a_logged_job_with_attachments() throws Throwable {
        Job job = jobDao.getRandomLoggedJobWithAttachment();
        if (job == null) {
            throw new PendingException("No test data found");
        }
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a logged job with a Client caller$")
    public void a_search_is_run_for_a_logged_job_with_a_client_caller() throws Throwable {
        Job job = jobDao.getRandomLoggedJobWithClientCaller();
        if (job == null) {
            throw new PendingException("No test data found");
        }
        testData.put("jobReference", job.getJobReference());
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a cancelled job assigned to a \"([^\"]*)\"$")
    public void a_search_is_run_for_a_cancelled_reactive_job(String resourceTypeName) throws Throwable {
        Job job = jobDao.getRandomCancelledJob(resourceTypeName);
        mercury.api.models.job.Job newJob = null;
        if (job == null) {
            newJob = jobCreationHelper.createJobInStatus("Cancelled");
        }
        testData.put("jobReference", (job != null ? job.getJobReference() : newJob.getJobReference()));
        runtimeState.scenario.write("JobReference: " + testData.getInt("jobReference"));

        if (job != null) {
            String clientStatusPriorToCancellation = dbHelper.getStatusNamePriorToCancellation(testData.getInt("jobReference"));
            testData.put("clientStatusPriorToCancellation", clientStatusPriorToCancellation);
        } else {
            testData.put("clientStatusPriorToCancellation", "Logged");
        }

        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    /**
     * @param contractor
     * @param store. Although this method has parameters, they don't drive the test
     *            and therefore no if else conditions have been written. The step
     *            definition has been changed for easier readability of gherkin
     *            steps by business team.
     */
    @And("^a job assigned to ((?:contractor with agreed call out rate|contractor)) at a vendor ((?:store|store with an additional contractor resource))$")
    public void a_job_assigned_to_contractor_at_a_vendor_store(String contractor, String store) throws Throwable {
        testData.put("resourceTypeName", "Contractor");
        testData.put("additionalContractor", store);
        mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Logged / Awaiting Funding Authorisation");
        int jobReference = job.getJobReference();
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
        if (dbHelperSystemToggles.getSystemFeatureToggle("AutoApproveContractorFundingRequests") == 1) {
            BigDecimal requestedAmount = fundingRequestDao.getByJobReference(testData.getInt("jobReference")).get(0).getRequestedAmount().setScale(2);
            String fundingRoute = getWebDriver().getCurrentUrl().contains("-ukrb") ? "REVEX" : "OPEX";
            String rfm = dbHelperSites.getRfmForSite(testData.getInt("siteId")).get("Name").toString().trim();
            rfm = "en-GB".equalsIgnoreCase(LOCALE) ? "Unknown" : rfm;
            testData.put("notes", "Authorized By: " + rfm + "\nStandard Callout\nAmount Authorized: " + toCurrency(requestedAmount).replaceAll(",", "") + "\nUplift Funded by: " + fundingRoute + "\nAmount authorized automatically");
        }
    }

    @And("^a job accepted by a contractor$")
    public void a_job_accepted_by_contractor() throws Throwable {
        testData.put("resourceTypeName", "Contractor");
        mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Allocated / ETA Provided");
        int jobReference = job.getJobReference();
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
    }

    @When("^a new job is logged and assigned to a contractor at a vendor store$")
    public void a_new_job_is_logged_and_assigned_to_contractor() throws Throwable {
        runtimeState.helpdeskHomePage.closeActiveTab();
        a_job_assigned_to_contractor_at_a_vendor_store("contractor", "store with an additional contractor resource");
        outputHelper.takeScreenshots();
        runtimeState.helpdeskHomePage.closeActiveTab();
        runtimeState.helpdeskHomePage.selectTile("Jobs");
    }

    @When("^a new job assigned to a contractor is logged and initial funding request is authorised$")
    public void a_new_job_assigned_to_contractor_is_logged_and_initial_funding_request_is_authorised() throws Throwable {
        runtimeState.helpdeskHomePage.closeActiveTab();
        a_job_assigned_to_contractor_at_a_vendor_store("contractor", "store with an additional contractor resource");
        commonSteps.the_action_is_selected("Manage Resources");
        helpdeskFundingRequestsSteps.the_funding_request_is_authorised_for("initial", "contractor");
    }

    @And("^the contractor accepts the job and a new uplift funding request is created with known amount$")
    public void contractor_accepts_job_and_uplift_funding_request_is_created() throws Throwable {
        helpdeskAcceptJobSteps.the_job_is_accepted_for_the_resource_and_an_eta_advised_to_site("contractor", "is not");
        helpdeskFundingRequestsSteps.a_new_uplift_funding_request_is_created("known");
        outputHelper.takeScreenshots();
        runtimeState.helpdeskHomePage.closeActiveTab();
        runtimeState.helpdeskHomePage.selectTile("Jobs");
    }

    @And("^a job assigned to ((?:chargeable contractor with zero price|non-chargeable contractor with or without zero price)) in the pricebook at a vendor store$")
    public void a_job_assigned_to_chargeable_contractor_with_nonzero_price_in_the_pricebook_at_a_vendor_store(String contractor) throws Throwable {
        mercury.api.models.job.Job job = jobCreationHelper.createJobForFundingRequestsForContractor(contractor);
        int jobReference = job.getJobReference();
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
        String fundingRoute = getWebDriver().getCurrentUrl().contains("-ukrb") ? "REVEX" : "OPEX";
        String rfm = dbHelperSites.getRfmForSite(testData.getInt("siteId")).get("Name").toString().trim();
        testData.put("notes", "Authorized By: " + rfm + "\nRecall\nAmount Authorized: " + toCurrency(0) + "\nUplift Funded by: " + fundingRoute + "\nAmount authorized automatically");
    }

    @Given("^a search is made for a Priority \"([^\"]*)\" job with contractor resource$")
    public void a_job_with_contractor_resource_and_given_priority(String priority) throws Throwable {
        testData.put("priority", priority);
        testData.addIntegerTag("resourceId", dbHelperResources.getContractorWithConfiguredRate());
        mercury.api.models.job.Job job = jobCreationHelper.createJobForFundingRequests();
        captureTestData(job);
        int jobReference = job.getJobReference();
        if(jobReference==0) {
            throw new PendingException("Job with suitable data is not found!");
        }
        job.setJobReference(jobReference);
        runtimeState.scenario.write("test data selected: jobReference = " + jobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
    }

    @And("^a search is run for a \"([^\"]*)\" job assigned to a City Tech resource$")
    public void a_search_is_run_for_a_job_assigned_to_a_city_tech_resource(String jobType) throws Throwable {
        int jobReference = 0; // Commenting out this method as its not giving the correct results - dbHelperJobs.getJobOfTypeAwaitingAcceptanceByCityTechResource(jobType);
        if (jobReference == 0) {
            jobCreationHelper.createJobResourceTypePhoneEmailIpad("City resource", "mobile", "with", "no");
            jobReference = testData.getInt("jobReference");
        }
        job.setJobReference(jobReference);
        runtimeState.scenario.write("test data selected: jobReference = " + jobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
    }

    @And("^a search is run for a \"([^\"]*)\" job assigned to a resource$")
    public void a_search_is_run_for_a_job_assigned_to_a_resource(String jobType) throws Throwable {
        int jobReference = 0;
        if (testData.getString("userProfile") != null && testData.getString("userProfile").equals("Resource")) {
            helpdeskLogAJobSteps.a_new_job_is_logged_and_assigned_to_any_resource_with_phone_email("no", "with");
            jobReference = testData.getInt("jobReference");
            String resourceName = dbHelperResources.getOtherAvailableActiveResourceForSite(testData.getInt("siteId"), testData.getInt("resourceId"));
            testData.addStringTag("resourceName", resourceName);


        } else {
            jobReference = dbHelperJobs.getRandomJobAssignedToAResource(jobType);
            if (jobReference == 0) {
                helpdeskSearchForSiteSteps.a_search_is_run_for_City_Tech_store_with_caller("an", "occupied");
                helpdeskLogAJobSteps.a_new_job_is_logged_and_assigned_to_any_resource_with_phone_email("no", "with");
                jobReference = testData.getInt("jobReference");
            }
        }
        job.setJobReference(jobReference);
        runtimeState.scenario.write("test data selected: jobReference = " + jobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
    }

    @And("^a search is run for a parked job that is not of type \"([^\"]*)\" with \"([^\"]*)\" fault priority$")
    public void a_search_is_run_for_a_parked_job_that_is_not_of_type_with_fault_priority( String jobType, String faultPriority) throws Throwable {
        String faultPriorityName = propertyHelper.getMercuryUrl().contains("uswm") ? "PE" : "P1";

        mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Parked", faultPriorityName);

        runtimeState.scenario.write("test data selected: jobReference = " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(job.getJobReference()));
        testData.put("jobReference", job.getJobReference());
    }

    @And("^a search is run for a job that is not of type \"([^\"]*)\" with \"([^\"]*)\" fault priority and ((?:is|is not)) assigned to a resource$")
    public void a_search_is_run_for_a_job_that_is_not_of_type_with_fault_priority_and_is_not_assigned_to_a_resource(String jobType, String faultPriority, String resourceAssignment) throws Throwable {
        int jobReference = 0;
        if ("IS".equalsIgnoreCase(resourceAssignment)) {
            jobReference = dbHelperJobs.getJobReferenceNotOfTypeWithDesiredFaultPriority(jobType, faultPriority);
            jobReference = jobReference == 0 ? jobCreationHelper.createJobInStatus("Logged / Awaiting Acceptance").getJobReference() : jobReference;
        } else if ("IS NOT".equalsIgnoreCase(resourceAssignment)) {
            jobReference = dbHelperJobs.getRandomJobNotAssignedToAResourceNotOfTypeWithDesiredFaultPriority(jobType, faultPriority);
            jobReference = jobReference == 0 ? jobCreationHelper.createJobInStatus("Logged / Removed").getJobReference() : jobReference;
        }
        testData.put("jobReference", jobReference);
        job.setJobReference(jobReference);
        runtimeState.scenario.write("jobReference = " + jobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(jobReference);
    }

    @And("^a job with ETA not advised to site is viewed$")
    public void a_job_with_ETA_not_advised_to_site_is_viewed() throws Throwable {
        int etaJobReference = dbHelperJobs.getRandomJobWithETANotAdvised();
        if (etaJobReference == 0) {
            throw new PendingException("No test data found");
        }
        runtimeState.scenario.write("test data selected: jobReference = " + etaJobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(etaJobReference));
        outputHelper.takeScreenshots();
    }

    @And("^a job with ETA not advised to site and \"([^\"]*)\" contacts? assigned is viewed$")
    public void a_job_with_ETA_not_advised_to_site_and_contacts_assigned_is_viewed(String numberOfContacts) throws Throwable {
        int etaJobReference = dbHelperJobs.getJobWithETANotAdvisedAndContactsAssigned(numberOfContacts);
        if (etaJobReference == 0) {
            mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Allocated / ETA Provided");
            apiHelperResources.addAdditionalResource_resourceProfileId(job.getId(), 1); // assign 2nd City resource to the job

            etaJobReference = dbHelperJobs.getJobWithETANotAdvisedAndContactsAssigned(numberOfContacts);
            CommonSteps.assertDataFound(etaJobReference);
        }
        runtimeState.scenario.write("test data selected: jobReference = " + etaJobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(etaJobReference));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a job assigned to a resource with phone number$")
    public void a_search_is_run_for_a_job_assigned_to_a_resource_with_phone_number() throws Throwable {
        int jobReference = dbHelperResources.getRandomJobWithResourceHavingPhoneNumber();
        testData.put("jobReference", jobReference);
        if (testData.getInt("jobReference") == 0) {
            testData.put("siteId", dbHelperSites.getRandomSiteIdWithCityTechAndContractor());
            helpdeskLogAJobSteps.a_new_job_is_logged_and_assigned_to_any_resource_with_phone_email("mobile", "with");
        }
        job.setJobReference(testData.getInt("jobReference"));
        runtimeState.scenario.write("test data selected: jobReference = " + testData.getInt("jobReference"));
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(testData.getInt("jobReference")));
    }

    @And("^a search is run for a job assigned to a single City resource with no iPad$")
    public void a_search_is_run_for_a_job_assigned_to_a_single_City_resource_with_no_ipad() throws Throwable {
        int jobReference = dbHelperJobs.getRandomJobAssignedToASingleCityResourceWithNoiPad();
        if (jobReference == 0) {
            throw new PendingException("No test data found");
        }
        job.setJobReference(jobReference);
        runtimeState.scenario.write("test data selected: jobReference = " + jobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a \"([^\"]*)\" job assigned to a City Tech ((?:resource|resource same as caller)) with phone number$")
    public void a_search_is_run_for_a_job_assigned_to_a_city_tech_resource_with_phone_number(String jobType, String resource) throws Throwable {
        testData.put("resource", resource);
        int jobReference = 0;
        if ("resource".equalsIgnoreCase(resource)) {
            jobReference = dbHelperResources.getJobOfRequiredTypeWithCityTechResourceHavingPhoneNumber(jobType);
            testData.put("jobReference", jobReference);
        }
        if (jobReference == 0 || "resource same as caller".equalsIgnoreCase(resource)) {
            helpdeskSearchForSiteSteps.a_search_is_run_for_City_Tech_store_with_caller("an", "occupied");
            helpdeskLogAJobSteps.a_new_job_is_logged_and_assigned_to_a_city_resource_with_phone_email_and_ipad("mobile", "with", "no");
        }
        job.setJobReference(testData.getInt("jobReference"));
        runtimeState.scenario.write("test data selected: jobReference = " + testData.getInt("jobReference"));
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(testData.getInt("jobReference")));
    }

    private void captureTestData(mercury.api.models.job.Job job) {
        testData.put("jobReference", job.getJobReference());
        testData.put("siteName", dbHelperSites.getSiteNameForJobRef(job.getJobReference()));
        this.job.setCaller(dbHelper.getCallerName(job.getCallerId()));
        this.job.setJobReference(job.getJobReference());
        this.job.setDescription(job.getDescription());
        this.job.setLocation(dbHelper.getLocationName(job.getLocationId()));
        this.job.setFault(dbHelper.getFaultTypeName(job.getFaultTypeId()));
        this.job.setClassification(dbHelper.getAssetSubTypeName(job.getAssetClassificationId()));
        this.job.setPriority("P" + job.getFaultPriorityId());
    }

    @And("^a search is run for a Job reference number with a linked incident$")
    public void a_search_is_run_for_a_job_reference_number_with_a_linked_incident() throws Throwable {
        int jobReference = dbHelperJobs.getJobReferenceNumberWithLinkedIncidents();
        if (jobReference == 0) {
            throw new PendingException("No test data found");
        }
        job.setJobReference(jobReference);
        runtimeState.scenario.write("test data selected: jobReference = " + jobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
    }

    @Given("^a search is run for an unallocated job with \"([^\"]*)\" callout$")
    public void a_search_is_run_for_an_unallocated_job(String callout) throws Throwable {
        ApplicationUser applicationUser;
        String createdBy = null;

        LogJobData logJobData;
        if (callout.equalsIgnoreCase("non-immediate")) {
            logJobData = logJobDataDao.getLogJobData(false, null, null);
        } else {
            logJobData = logJobDataDao.getLogJobData(true, null, null);
        }

        CommonSteps.assertDataFound(logJobData);
        LogJobHelper.captureTestData(logJobData, testData);

        int callerId = dbHelper.getRandomCaller("Resource");
        Integer locationId = logJobData.getLocationId();
        if (locationId == null) {
            locationId = dbHelper.getRandomLocation(logJobData.getSiteId());
        }

        applicationUser = jobCreationHelper.getCurrentUserForJobCreation();
        createdBy = applicationUser.getId();

        mercury.api.models.job.Job jobData = apiHelperJobs.createNewJob(1, logJobData.getSiteId(), logJobData.getFaultTypeId(), logJobData.getPriority(), "Created for test automation", callerId, logJobData.getAssetClassificationId(), locationId, createdBy);

        runtimeState.scenario.write("Job created with: jobTypeId - " + 1 + " siteId - " + logJobData.getSiteId() + " faultTypeId - " + logJobData.getFaultTypeId()
        + " faultPriorityId - " + logJobData.getPriority() + " description - Created for test automation callerId - " + callerId
        + " assetClassificationId - " + logJobData.getAssetClassificationId() + " locationId - " + locationId);

        testData.addIntegerTag("jobReference", jobData.getJobReference());
        testData.addIntegerTag("siteId", jobData.getSiteId());
        testData.addIntegerTag("assetClassificationId", jobData.getAssetClassificationId());
        runtimeState.scenario.write("JobReference: " + jobData.getJobReference());

        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @Given("^a search is run for a deferred job$")
    public void a_search_is_run_for_a_deferred_P2_or_P3_job() throws Throwable {
        int resourceId = dbHelperResources.getRandomCityTechId("City Resource");
        testData.put("resourceId", resourceId);
        mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Logged / Job Advise Deferred");
        testData.put("jobReference", job.getJobReference());
        testData.addIntegerTag("siteId", job.getSiteId());
        testData.addIntegerTag("assetClassificationId", job.getAssetClassificationId());
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a \"([^\"]*)\" job in \"([^\"]*)\" status assigned to a \"([^\"]*)\" resource$")
    public void a_search_is_run_for_a_job_in_status_assigned_to_a_resource(String jobType, String jobStatus, String resourceProfile) throws Throwable {
        int jobReference = dbHelperJobs.getJobOfTypeWithRequiredStatusAndResourceProfile(jobType, jobStatus, resourceProfile);
        if (jobReference == 0) {
            throw new PendingException("No test data found");
        }
        job.setJobReference(jobReference);
        runtimeState.scenario.write("test data selected: jobReference = " + jobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
    }

    @And("^a job assigned to contractor with no configured rate at a vendor store$")
    public void a_job_assigned_to_contractor_with_no_configured_rate_at_a_vendor_store() throws Throwable {
        mercury.api.models.job.Job job = jobCreationHelper.createJobForFundingRequestsWithContractorWithNoConfiguredAmount();
        int jobReference = job.getJobReference();
        runtimeState.scenario.write("JobReference: " + job.getJobReference());
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
    }

    @When("^a search is run for an invalid job reference$")
    public void a_search_is_run_for_an_invalid_job_reference() throws Throwable {

        int jobReference = RandomUtils.nextInt(20000000, 29999999);
        while (dbHelperJobs.getJobId(jobReference) != 0) {
            jobReference = RandomUtils.nextInt(20000000, 29999999);
        }
        runtimeState.scenario.write("JobReference: " + jobReference);
        runtimeState.adminRefrigerantGasUsageSearchPage.enterJobReference(jobReference);
        outputHelper.takeScreenshots();
        runtimeState.adminRefrigerantGasUsageEditPage = runtimeState.adminRefrigerantGasUsageSearchPage.search();
    }

    @When("^a search is run for job without a site visit$")
    public void a_search_is_run_for_job_without_site_visit() throws Throwable {
        int jobReference = dbHelperJobs.getJobReferenceWhereNoSiteVisit();
        runtimeState.scenario.write("JobReference: " + jobReference);
        runtimeState.adminRefrigerantGasUsageSearchPage.enterJobReference(jobReference);
        outputHelper.takeScreenshots();
        runtimeState.adminRefrigerantGasUsageEditPage = runtimeState.adminRefrigerantGasUsageSearchPage.search();
    }

    @When("^a search is run for job with a non gas site visit$")
    public void a_search_is_run_for_job_with_a_non_gas_site_visit() throws Throwable {
        int jobReference = dbHelperJobs.getJobReferenceWhereNonGasSiteVisit();
        runtimeState.scenario.write("JobReference: " + jobReference);
        runtimeState.adminRefrigerantGasUsageSearchPage.enterJobReference(jobReference);
        outputHelper.takeScreenshots();
        runtimeState.adminRefrigerantGasUsageEditPage = runtimeState.adminRefrigerantGasUsageSearchPage.search();
    }

    @And("^a search is run for a \"([^\"]*)\" job assigned to single city resource with resource assignment status \"([^\"]*)\" or \"([^\"]*)\"$")
    public void a_search_is_run_for_a_job_assigned_to_single_city_resource_with_resource_assignment_status(String jobType, String resourceAssignmentStatus, String resourceAssignmentStatusAlt) throws Throwable {
        int jobReference = 0;
        try {
            jobReference = dbHelperJobs.getJobReferenceOfTypeWithResourceAssignmentStatus(jobType, resourceAssignmentStatus);
        } catch (PendingException e) {
            try {
                jobReference = dbHelperJobs.getJobReferenceOfTypeWithResourceAssignmentStatus(jobType, resourceAssignmentStatusAlt);
            } catch (PendingException exception) {
                throw exception;
            }
        }
        String resourceName = dbHelperResources.getResourceNameForJobReference(jobReference);
        testData.put("oldResource", resourceName);

        runtimeState.scenario.write("Test data selected: \njobReference: " + jobReference + "\nresourceName: " + resourceName);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(jobReference));
        testData.put("jobReference", jobReference);
        outputHelper.takeScreenshots();
    }

    @And("^a search is run for a job with a 2019 FGAS regulated site visit with no leak found$")
    public void a_search_is_run_for_a_job_with_a_2019_FGAS_regulated_site_visit_with_no_leak_found() throws Throwable {

        SiteVisits siteVisit = siteVisitsDao.getSiteVisitWithNoLeakFound();

        if (siteVisit == null) {
            throw new PendingException("No test data found: No site visit found for 2019 regulated site visit with no leak found");
        }

        runtimeState.scenario.write("siteVisitId: " + siteVisit.getId());

        testData.put("fgasRegulations", "2019");
        saveSiteVisit(siteVisit);

        Integer jobReference = dbHelperJobs.getJobReferenceForSiteVisit(siteVisit.getId());
        searchForJob(jobReference);
    }
}
