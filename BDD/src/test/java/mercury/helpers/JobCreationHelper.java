package mercury.helpers;

import static mercury.helpers.Globalisation.LOCALE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.AUTOMATION_USER;
import static mercury.helpers.Constants.City2019;
import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.PA55W0RD;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Constants.THREE_MINUTES;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.opencsv.CSVReader;

import cucumber.api.PendingException;
import mercury.api.models.job.Job;
import mercury.api.models.portal.job.updateFGas2019.Update;
import mercury.api.models.resourceAssignmentFunding.Authorise;
import mercury.api.models.web.api.ResourceAction.AcceptJob;
import mercury.database.dao.ApplicationUserDao;
import mercury.database.dao.CallerDetailsDao;
import mercury.database.dao.FaultPriorityMappingDao;
import mercury.database.dao.FundingRequestDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.LogJobDataDao;
import mercury.database.dao.PartCodeDao;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.dao.ResourceDao;
import mercury.database.dao.SiteContractorAssetDao;
import mercury.database.dao.SiteViewDao;
import mercury.database.dao.SiteVisitsDao;
import mercury.database.dao.UserJobDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.CallerDetails;
import mercury.database.models.FaultPriorityMapping;
import mercury.database.models.FundingRequest;
import mercury.database.models.LogJobData;
import mercury.database.models.PartCode;
import mercury.database.models.ResourceAssignment;
import mercury.database.models.SiteContractorAsset;
import mercury.database.models.SiteView;
import mercury.database.models.UserJob;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.databuilders.UpdateJobBuilder;
import mercury.helpers.apihelper.ApiHelper;
import mercury.helpers.apihelper.ApiHelperHangfire;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.apihelper.ApiHelperParts;
import mercury.helpers.apihelper.ApiHelperResourceAssignmentFunding;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.apihelper.ApiHelperStorePortal;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperAssertions;
import mercury.helpers.dbhelper.DbHelperFundingRequests;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobData;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperParts;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.dbhelper.DbHelperTestAutomationUsers;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;

public class JobCreationHelper {

    private static final Logger logger = LogManager.getLogger();

    private static final String REACTIVE_JOBTYPE = "reactive";

    @Autowired private ApiHelper apiHelper;
    @Autowired private ApiHelperStorePortal apiHelperStorePortal;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private ApiHelperParts apiHelperParts;
    @Autowired private ApiHelperResources apiHelperResources;
    @Autowired private ApiHelperResourceAssignmentFunding apiHelperResourceAssignmentFunding;
    @Autowired private ApiHelperHangfire apiHelperHangfire;
    @Autowired private CallerDetailsDao callerDetailsDao;
    @Autowired private FaultPriorityMappingDao faultPriorityMappingDao;
    @Autowired private FundingRequestDao fundingRequestDao;
    @Autowired private DbHelperAssertions dbHelperAssertions;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private DbHelperFundingRequests dbHelperFundingRequests;
    @Autowired private DbHelperParts dbHelperParts;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;
    @Autowired private DbHelper dbHelper;
    @Autowired private InvoiceCreationHelper invoiceCreationHelper;
    @Autowired private LogJobDataDao logJobDataDao;
    @Autowired private JobDao jobDao;
    @Autowired private PartCodeDao partCodeDao;
    @Autowired protected PropertyHelper propertyHelper;
    @Autowired private ResourceAssignmentDao resourceAssignmentDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private SiteViewDao siteViewDao;
    @Autowired private SiteContractorAssetDao siteContractorAssetDao;
    @Autowired private DbHelperTestAutomationUsers dbHelperTestAutomationUsers;
    @Autowired private TestData testData;
    @Autowired private ToggleHelper toggleHelper;
    @Autowired private TzHelper tzHelper;
    @Autowired private UpdateJobBuilder updateJobBuilder;
    @Autowired private ResourceDao resourceDao;
    @Autowired private DbHelperJobData dbHelperJobData;
    @Autowired private SiteVisitsDao siteVisitsDao;
    @Autowired private UserJobDao userJobDao;
    @Autowired private ApplicationUserDao applicationUserDao;
    @Autowired private LoginLogoutHelper loginLogoutHelper;

    UserJob userJob;

    public ApplicationUser getCurrentUserForJobCreation() {
        runtimeState.domainUser = System.getProperty("user.name").toLowerCase();
        ApplicationUser applicationUser = applicationUserDao.getByUsername(runtimeState.domainUser);
        if (applicationUser == null) {
            Properties props = loginLogoutHelper.initializeUserProperties("automation_users.properties");
            String alias = props.getProperty(runtimeState.domainUser.toLowerCase());
            applicationUser = applicationUserDao.getByUsername(alias);
        }
        return applicationUser;
    }

    /**
     * Start the given job impersonating the given resource ID.  Job must be accepted.
     * @param jobReference
     * @param resourceId
     * @throws Exception
     */
    public void startContractorJob(int jobReference, int resourceId, String jobType) throws Exception {
        String resourceUserId = dbHelper.getApplicationUserIdForResourceId(resourceId, "Contractor Technician");

        for (Map<String, Object> dbData : dbHelperTestAutomationUsers.getITUsers()) {
            String userName = dbData.get("UserName").toString();
            String password = userName.equalsIgnoreCase(runtimeState.domainUser) ? City2019 : PA55W0RD;
            try {
                apiHelper.setupImpersonationCookiesAppUser(propertyHelper.getMercuryUrl(), userName, password, resourceUserId);
                break;
            } catch (AssertionError e) {
                logger.debug("Login failed for " + userName + ". Trying next user ...");
            }
        }

        Date startTime = DateUtils.truncate(tzHelper.getCurrentTimeAtSite(testData.getInt("siteId")), Calendar.MINUTE);
        testData.put("Start time", startTime);
        apiHelperJobs.startJob(jobReference, jobType);
    }

    private String formatFormData(String formData) {
        formData = formData.replaceAll("%5BgasLeakSites%5D%5B0%5D%5B", ".gasLeakSites%5B0%5D.");
        formData = formData.replaceAll("%5BgasCylinders%5D%5B0%5D%5B", ".gasCylinders%5B0%5D.");
        formData = formData.replaceAll("gasDetails%5B", "gasDetails.");
        formData = formData.replaceAll("%5D=", "=");
        formData = formData.replaceAll("JobDetails%5B", "JobDetails.");
        return formData;
    }

    public void updateJobWithSiteVisit(Integer jobId, Integer jobReference, Integer resourceId, Integer siteId, String statusOnDeparture) throws Exception {

        String resourceTypeName = dbHelperResources.getResourceType(resourceId);

        String applicationUserId = testData.getString("applicationUserId") != null ? testData.getString("applicationUserId") : null;
        if (applicationUserId == null) {
            applicationUserId = resourceTypeName.equalsIgnoreCase("Contractor") ? dbHelper.getApplicationUserIdForResourceId(resourceId, "Contractor Technician") : dbHelper.getApplicationUserIdForResourceId(Integer.valueOf(resourceId));
        }

        for (Map<String, Object> dbData : dbHelperTestAutomationUsers.getITUsers()) {
            String userName = dbData.get("UserName").toString();
            String password = userName.equalsIgnoreCase(runtimeState.domainUser) ? City2019 : PA55W0RD;
            try {
                apiHelper.setupImpersonationCookiesAppUser(propertyHelper.getMercuryUrl(), userName, password, applicationUserId);
                break;
            } catch (AssertionError e) {
                logger.debug("Login failed for " + userName + ". Trying next user ...");
            }
        }

        Date startTime = DateUtils.truncate(tzHelper.getCurrentTimeAtSite(siteId), Calendar.MINUTE);
        testData.put("Start time", startTime);

        Integer resourceAssignmentId = dbHelperJobs.getResourceAssignmentID(jobReference, resourceId);

        // need to do https://test-uswm.mercury.software/Portal/Job/Update/{resourceAssignmentId} and extract requestVerificationToken
        String response = apiHelperJobs.getUpdateJobPage(resourceAssignmentId);
        String requestVerificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");

        if (dbHelperGas.gasSiteVisitExists(jobReference) || testData.getBoolean("usesGas")) {
            // Now create the update payload
            // Need to determine what type of visit to create
            boolean gasUsage = testData.tagExist("gasUsage") ? testData.getBoolean("gasUsage") : false;
            boolean leakCheck = testData.tagExist("leakCheck") ? testData.getBoolean("leakCheck") : false;
            String assetType = testData.tagExist("assetType") ? testData.getString("assetType") : "Unknown";

            Update update = updateJobBuilder.createUpdateGasJob(jobId, jobReference, siteId, resourceTypeName, statusOnDeparture, gasUsage, leakCheck, assetType);
            update.setRequestVerificationToken(requestVerificationToken);
            update.setResourceAssignmentId(resourceAssignmentId.toString());

            logger.debug("Update job json payload : " + update.toJsonString());
            String payload = formatFormData(JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), update.toJsonString()));

            logger.debug("Update job payload : " + payload);

            apiHelperJobs.updateFgasJob(resourceAssignmentId, payload);

        } else {
            Update update = updateJobBuilder.createUpdateJob(jobId, jobReference, siteId, resourceTypeName, statusOnDeparture);
            update.setRequestVerificationToken(requestVerificationToken);
            update.setResourceAssignmentId(resourceAssignmentId.toString());

            logger.debug("Update job json payload : " + update.toJsonString());
            String payload = formatFormData(JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), update.toJsonString()));

            logger.debug("Update job payload : " + payload);
            apiHelperJobs.updateJob(resourceAssignmentId, payload);
        }
    }

    public void acceptJob(Integer jobReference, Integer jobId, String resourceType, String jobStatus) throws Exception {
        ResourceAssignment resourceAssignment = resourceAssignmentDao.getAssigmentByJobReference(jobReference);

        // Create random ETA data string
        String etaDate = DateHelper.getRandomETATime() + "T07:00:00.378Z";
        // Create acceptance
        AcceptJob acceptjob = new AcceptJob().withEta(etaDate).withEtaAdvisedToSite(false).withEtaWindowId(8).withJobId(jobId).withResourceAssignmentId(resourceAssignment.getId()).withResourceId(testData.getInt("resourceId")).withUseSlaAsEta(false);
        if (resourceType.contains("Contractor")) {
            acceptjob.setContractorReference(jobReference + " Contractor Ref");
        }
        runtimeState.scenario.write(acceptjob.toJsonString());
        apiHelperJobs.acceptJob(acceptjob);

        // Need the following line to ensure portal actions can take place on the job
        dbHelperResources.updateResourceAssignmentCreationDate(jobReference);
    }

    private void cancelJob(int jobId) throws Exception {
        apiHelperJobs.cancelJob(jobId, AUTOMATION_USER);
    }

    private void removeResource(Integer jobReference) throws Exception {
        Integer resourceAssignmentId = dbHelperJobs.getResourceAssignmentID(jobReference, testData.getInt("resourceId"));
        apiHelperResources.removeResource(resourceAssignmentId);
        apiHelperResources.removeResourceAdvise(resourceAssignmentId);
    }

    public void startJob(Integer jobReference) throws Exception {
        startContractorJob(jobReference, testData.getInt("resourceId"), REACTIVE_JOBTYPE);
    }

    public void createHelpdeskChaseForjob(int jobId) throws Exception {
        String description = DataGenerator.GenerateRandomString(20, 20, 3, 0, 10, 0);
        String telephoneNumber = DataGenerator.generatePhoneNumber();
        String caller = dbHelperJobs.getCallerNameForJobId(jobId);
        apiHelperJobs.createHelpdeskChase(jobId, description, telephoneNumber, 1, true, caller);
    }

    public void createNonHelpdeskManagerChaseForjob(int jobRef) throws Exception {
        String description = DataGenerator.GenerateRandomString(20, 20, 3, 0, 10, 0);
        String contactName = DataGenerator.generateRandomName();
        String telephoneNumber = DataGenerator.generatePhoneNumber().replace("-", "").replace("(", "").replace(")", "");
        apiHelperStorePortal.createNonHelpdeskManagerChase(jobRef, description, contactName, telephoneNumber);
    }

    public Job createJobInStorePortal() throws Exception {
        apiHelperStorePortal.logInToStorePortal();
        Map<String, Object> data = dbHelper.getRandomAssetTypeFromSiteCode(testData.getString("storePortalUserName"));
        Integer locationId = (int) data.get("LocationId");
        Integer assetClassificationId = (int) data.get("AssetClassificationId");
        Integer siteId = (int) data.get("SiteId");
        Integer assetId = (int) data.get("AssetTypeId");
        Integer faultTypeId = (int) data.get("FaultTypeId");
        String description = DataGenerator.GenerateRandomString(20, 20, 3, 0, 10, 0);
        String siteContact = DataGenerator.generateRandomName();
        String jobTitle = DataGenerator.generateRandomJobTitle();
        String telephoneNumber = DataGenerator.generatePhoneNumber().replace("-", "").replace("(", "").replace(")", "");
        Job job = apiHelperStorePortal.createJobInStorePortal(assetClassificationId, assetId, faultTypeId, description, jobTitle, locationId, telephoneNumber, siteContact, siteId);
        mercury.database.models.Job jobData = jobDao.getByJobReference(testData.getInt("jobReference"));
        Integer faultPriorityId = (int) jobData.getFaultPriorityId();
        Integer callerId = (int) jobData.getCallerId();
        job.setFaultPriorityId(faultPriorityId);
        job.setCallerId(callerId);
        return job;
    }

    public void approveContractorFunding(Integer jobReference, Integer siteID) throws Exception {
        List<FundingRequest> fundingRequest = fundingRequestDao.getByJobReference(jobReference);
        /*
         *  Contractors may have %portaldb.tblPricebook.fld_cur_CostEach = 0 for fld_str_PartCode = 'Standard Callout'
         *  which result in no funding requests to approve.
         *  The may also have %helpdeskdb.Resource.AlwaysChargeable = 1 which will also result in no funding requests to approve.
         *  Both of these will result in the query returning nothing.  If this causes issues then we might have to introduce a a query similar to the following:
         *
         *  SELECT * FROM Resource r
         *  LEFT JOIN %portaldb.tblPricebook pb ON pb.fld_int_ResourceId = r.EpochId
         *  WHERE r.Id = 19631
         *  AND (pb.fld_str_PartCode = 'Standard Callout' AND pb.fld_cur_CostEach > 0)
         *  OR r.AlwaysChargeable = 1
         */
        if (fundingRequest != null && !fundingRequest.isEmpty()) {
            assertNotNull("Cannot pull back funding request for job", fundingRequest);
            int rfmid = (Integer) dbHelperSites.getRfmForSite(siteID).get("ResourceId");
            Authorise authorise;
            int currentState = dbHelperSystemToggles.getSystemFeatureToggle("Budget Review");

            String fundingRoute = testData.getString("fundingRoute");
            int fundingRouteId = fundingRoute == null ? 17 : dbHelperFundingRequests.getFundingRouteId(fundingRoute);
            testData.put("fundingRouteId", fundingRouteId);

            runtimeState.scenario.write("Funding Route: " + fundingRoute + " Funding Route Id: " + fundingRouteId);

            if(currentState == 1) {
                authorise = new Authorise().withId(fundingRequest.get(0).getId()).withAuthorisedByResourceId(rfmid).withFundingReasonId(63).withFundingRouteId(fundingRouteId).withNotes("Approved by Auto test").withAuthorisedAmount(500).withPotentialInsuranceValue("true");
            } else {
                authorise = new Authorise().withId(fundingRequest.get(0).getId()).withAuthorisedByResourceId(rfmid).withFundingReasonId(63).withFundingRouteId(fundingRouteId).withNotes("Approved by Auto test").withAuthorisedAmount(500);
            }

            apiHelperResourceAssignmentFunding.authorise(fundingRequest.get(0).getId(), authorise);
        }
    }

    private Job createDeferredJob() throws Exception {

        dbHelperSites.updateSiteTypeToAutomaticallyDefer();

        SiteContractorAsset siteContractorAsset = siteContractorAssetDao.getForCityTechStoreAndDeferrable();

        CommonSteps.assertDataFound(testData.getString("sql"), siteContractorAsset);

        testData.put("isDeferred", true);

        int siteId = siteContractorAsset.getSiteId();
        int faultTypeId = siteContractorAsset.getFaultTypeId();
        int assetClassificationId = siteContractorAsset.getAssetClassificationId();
        int faultPriorityId = siteContractorAsset.getFaultPriorityId();

        int callerId = dbHelper.getRandomCaller("Resource");
        int locationId = dbHelper.getRandomLocation(siteId, assetClassificationId);

        ApplicationUser applicationUser = getCurrentUserForJobCreation();

        Job jobTemplate = new Job();
        jobTemplate.setJobTypeId(1);                // Reactive
        jobTemplate.setFaultTypeId(faultTypeId);
        jobTemplate.setFaultPriorityId(faultPriorityId);
        jobTemplate.setDescription("Created for test automation");
        jobTemplate.setCallerId(callerId);
        jobTemplate.setAssetClassificationId(assetClassificationId);
        jobTemplate.setIsDeferred(true);
        jobTemplate.setDeferralNote("Deferred for automation test");
        jobTemplate.setDeferralTypeId(5);
        Date plusTwoDays = DateHelper.getDatePlusOffsetInHours(new Date(), 48);
        jobTemplate.setDeferralDate(DateHelper.dateAsString(plusTwoDays, "yyyy-MM-dd'T'08:00:00+00:00"));
        jobTemplate.setLocationId(locationId);
        jobTemplate.setSiteId(siteId);
        jobTemplate.setJobStatusId(1);
        jobTemplate.setReasonNotLoggedAgainstAssetId(10);
        jobTemplate.setCreatedBy(applicationUser.getId());
        if (testData.getInt("resourceId") != null) {
            jobTemplate.setSpecificResourceRequestId(testData.getInt("resourceId"));
        }

        Job job = apiHelperJobs.createNewJob(jobTemplate);

        logger.debug("Created deferred job: " + job.getJobReference());

        apiHelperJobs.putContact(callerId, job.getId());

        apiHelperHangfire.processRefreshMonitorDataAndCounts();

        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperJobs.getJobMonitor(job.getJobReference()), containsString("Deferred jobs"));

        return job;
    }

    public Job createJobInTriage() throws Exception {

        // set system feature toggles to allow job to go to triage
        toggleHelper.disableSystemFeatureToggle("JobDeferrals");
        toggleHelper.enableSystemFeatureToggle("NewRoutingRules");
        toggleHelper.enableSystemFeatureToggle("AutoAssign");
        toggleHelper.enableSystemFeatureToggle("AutoApproveContractorFundingRequests");

        // P2 or P3 + store without city resource + contractor assigned + no city tech on rota

        LogJobData data = logJobDataDao.noCityTechResourceContractorConfigured("2, 3");

        if (data == null) {
            throw new PendingException("No test data found");
        }

        LogJobHelper.captureTestData(data, testData);

        int callerId = dbHelper.getRandomCaller("Resource");
        Integer locationId = data.getLocationId();
        if (locationId == null) {
            locationId = dbHelper.getRandomLocation(data.getSiteId());
        }

        ApplicationUser applicationUser = getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        Job job = apiHelperJobs.createNewJob(1, data.getSiteId(), data.getFaultTypeId(), 2, "Created for test automation", callerId, data.getAssetClassificationId(), locationId, createdBy);
        apiHelperJobs.putContact(callerId, job.getId());

        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(THREE_MINUTES, SECONDS).until(() -> dbHelperJobs.getJobMonitor(job.getJobReference()), containsString("Triage"));

        logger.debug("createJobInTriage: " + job.getJobReference());
        return job;
    }

    // TODO this should be createJobInStatus("Logged / Awaiting Funding Authorisation");
    public Job createJobForFundingRequests() throws Exception {
        // vendor store + contractor assigned

        int priority = testData.getInt("priority");
        SiteContractorAsset siteContractorAsset = siteContractorAssetDao.getRandomForVendorStore(priority);

        if (siteContractorAsset == null) {
            throw new PendingException("No test data found");
        }

        int siteId = siteContractorAsset.getSiteId();
        int faultTypeId = siteContractorAsset.getFaultTypeId();
        int assetClassificationId = siteContractorAsset.getAssetClassificationId();

        CallerDetails cd = callerDetailsDao.getRandomCallerForSite(dbHelperSites.getSiteName(siteId));
        if (cd == null) {
            throw new PendingException("No suitable caller found");
        }
        int callerId = cd.getId();
        Integer locationId = dbHelper.getRandomLocation(siteId, assetClassificationId);
        if (locationId == null) {
            locationId = dbHelper.getRandomLocation(siteId);
        }
        int resourceId = testData.getInt("resourceId");

        int responsePriorityId = dbHelperJobs.getFaultPriorityForJobCreationHelper(siteId, assetClassificationId, faultTypeId);

        ApplicationUser applicationUser = getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        Job job = apiHelperJobs.createNewJobForResource(1, siteId, faultTypeId, responsePriorityId, "Created for test automation", callerId, assetClassificationId, locationId, resourceId, null, createdBy);
        apiHelperJobs.putContact(callerId, job.getId());

        logger.debug("createJobForFundingRequestsWithContractorResource: " + job.getJobReference());

        testData.put("siteId", siteId);

        return job;
    }

    // TODO this should be createJobInStatus("Logged / Allocated / Awaiting Funding Authorisation");
    public Job createJobForFundingRequestsWithMultipleContractorResources() throws Exception {
        // P1 + vendor store + contractor assigned
        testData.put("priority", 1);
        Integer resourceId = dbHelperResources.getContractorWithConfiguredRate();
        if (resourceId == null) {
            throw new PendingException("No contractors with pre-configured rate were found");
        }
        testData.addIntegerTag("resourceId", resourceId);
        return createJobForFundingRequests();
    }

    public Job createJobForFundingRequestsForContractor(String contractor) throws Exception {
        // P1 + vendor store + contractor assigned
        testData.put("priority", 1);
        Integer resourceId = null;
        if ("chargeable contractor with zero price".equalsIgnoreCase(contractor)) {
            resourceId = dbHelperResources.getChargeableContractor(true, "with");
        } else if ("non-chargeable contractor with or without zero price".equalsIgnoreCase(contractor)) {
            resourceId = dbHelperResources.getChargeableContractor(false, null);
        }
        if (resourceId == null) {
            throw new PendingException("No " + contractor + " found");
        }
        testData.addIntegerTag("resourceId", resourceId);
        return createJobForFundingRequests();
    }

    public Job createJobForFundingRequestsWithContractorWithNoConfiguredAmount() throws Throwable {
        // P1 + vendor store + contractor assigned with no pre-approved amount
        testData.put("priority", 1);
        Integer resourceId = dbHelperResources.getContractorWithNoConfiguredRate();
        if (resourceId == null) {
            String resourceName = "AutomationContractor_NoAgreedRate";
            resourceId = dbHelperResources.getResourceId(resourceName);
            if (resourceId == null) {
                apiHelperResources.createOrUpdateContractorResource(resourceName);
                resourceId = dbHelperResources.getResourceId(resourceName);
            }
            dbHelperResources.deletePricebookEntryForContractor(resourceId);
        }
        testData.addIntegerTag("resourceId", resourceId);
        return createJobForFundingRequests();
    }

    private Job processLogJobData(LogJobData data, boolean withAsset, String createdBy) throws Exception {
        runtimeState.scenario.write("TEST DATA: " + data.toString());

        LogJobHelper.captureTestData(data, testData);

        int callerId = dbHelper.getRandomCaller("Resource");
        Integer locationId = data.getLocationId();
        if (locationId == null) {
            locationId = dbHelper.getRandomLocation(data.getSiteId());
        }

        testData.addStringTag("resourceName", data.getResourceName());
        testData.addIntegerTag("resourceId", data.getResourceId());

        Integer assetId = null;
        if (withAsset) {
            assetId = dbHelperSites.getSiteAssetId(data.getSiteId(), data.getAssetClassificationId());
            testData.put("specificResourceRequestId", data.getResourceId());
            testData.put("assetId", assetId);
        }

        Job job = apiHelperJobs.createNewJobForResource(1, data.getSiteId(), data.getFaultTypeId(), data.getPriority(), "Created for test automation", callerId, data.getAssetClassificationId(), locationId, data.getResourceId(), assetId, createdBy);

        return job;
    }

    /**
     * Attempts to create a job for the given input.  If priority is set to 0 it starts at P1 and works its way to P6, returning a job with the first priority which matches
     * the other inputs.
     *
     * @param resourceType
     * @param phone
     * @param email
     * @param ipad
     * @param faultPriority
     * @param gasUsage
     * @param leakCheck
     * @param inPlant
     * @param withAsset
     * @return
     * @throws Exception
     */
    private Job createJobResourcePhoneEmailIpadPriority(String resourceType, String phone, String email, String ipad, Integer faultPriority, boolean gasUsage, boolean leakCheck, Boolean inPlant, Boolean withAsset, String createdBy) throws Exception {
        boolean isContractor = "Contractor".equals(resourceType);
        boolean hasIpad = "with".equals(ipad);
        String phoneType = "no".equals(phone) ? null : phone;
        boolean hasEmail = "with".equals(email);

        String searchFaultPriority = String.valueOf(faultPriority);
        searchFaultPriority = faultPriority == 0 ? "1, 2, 3, 4, 5, 6" : faultPriority.toString();
        LogJobData data = null ;

        // If we try to query all fault priorities at once we get timeouts, so do one at a time
        String faultPriority1[] = searchFaultPriority.split(",");
        for (int i = 0; i < faultPriority1.length; i++) {
            data = logJobDataDao.getLogJobData_CTE(faultPriority1[i], isContractor, hasIpad, phoneType, hasEmail);
            if (data != null) break;
        }

        if (data == null) {
            throw new PendingException("No suitable test data located");
        }

        Job job = processLogJobData(data, withAsset, createdBy);

        return job;
    }

    /**
     * Attempts to create a job for the given input.  If priority is set to 0 it starts at P1 and works its way to P6, returning a job with the first priority which matches
     * the other inputs.
     *
     * @param faultPriority
     * @param gasUsage
     * @param leakCheck
     * @param inPlant
     * @param withAsset
     * @return
     * @throws Exception
     */
    private Job createJobForContractor(Integer faultPriority, boolean gasUsage, boolean leakCheck, Boolean inPlant, Boolean withAsset, String createdBy) throws Exception {

        String searchFaultPriority = String.valueOf(faultPriority);
        searchFaultPriority = faultPriority == 0 ? "1, 2, 3, 4, 5, 6" : faultPriority.toString();
        LogJobData data = null ;

        // If we try to query all fault priorities at once we get timeouts, so do one at a time
        String faultPriority1[] = searchFaultPriority.split(",");
        for (int i = 0; i < faultPriority1.length; i++) {
            if ("Contractor Technician".equals(testData.getString("profileName"))) {
                data = logJobDataDao.getLogJobDataForContractorTechnician(faultPriority1[i]);
            } else {
                data = logJobDataDao.getLogJobDataForContractor(faultPriority1[i]);
            }
            if (data != null) break;
        }

        if (data == null) {
            throw new PendingException("No suitable test data located");
        }

        Job job = processLogJobData(data, withAsset, createdBy);

        return job;
    }

    public void createJobResourceTypePhoneEmailIpad(String resourceType, String phone, String email, String ipad) throws Throwable {
        int faultPriority = 0;
        int responsePriorityId = dbHelperJobData.getResoponsePriorityId(faultPriority);

        Boolean withIpad = "no".equals(ipad) ? false : true;
        Boolean withEmail = "with".equals(email) ? true : false;
        Boolean withMobile = "mobile".equals(phone) ? true : false;

        Map<String, Object> dbData = dbHelperJobData.getJobData(withIpad, withEmail, withMobile);

        if (dbData == null) {
            dbData = dbHelperJobData.getJobData(null, null, null);
            CommonSteps.assertDataFound(dbData);
            apiHelperResources.configureResource((Integer) dbData.get("ResourceId"), phone, email, ipad);
        }

        int cityTechResourceId = (Integer) dbData.get("ResourceId");
        int siteId = (Integer) dbData.get("SiteId");
        testData.put("siteId", siteId);
        int assetClassificationId = (Integer) dbData.get("AssetClassificationId");
        int faultTypeId = (Integer) dbData.get("FaultTypeId");

        Integer resourceId = "CONTRACTOR".equalsIgnoreCase(resourceType) ? dbHelperResources.getContractorResourceIdForSite(siteId, assetClassificationId) : cityTechResourceId;
        testData.put("resourceId", resourceId);

        if ("CONTRACTOR".equalsIgnoreCase(resourceType) && email.equals("with")) {
            // on the test environments a lot of the data is pre-loaded by the Dev's ... they often do not load data to the ResourceNotificationMethod table for contractors
            // this is a datafix for this missing data
            dbHelperResources.insertResourceNotificationMethod(resourceId, "Email");
        }

        Job job = createJobForResource(siteId, resourceId, faultTypeId, responsePriorityId, assetClassificationId);

        runtimeState.scenario.write("Created job: " + job.toString().replaceAll(".*\\[", "").replace("]", ""));
        testData.put("jobReference", job.getJobReference());
    }

    public void createJobForResourceForPriority(int faultPriority, String value, String phone, String email, String ipad) throws Throwable {

        Boolean withIpad = "no".equals(ipad) ? false : true;
        Boolean withEmail = "with".equals(email) ? true : false;
        Boolean withMobile = "mobile".equals(phone) ? true : false;

        Map<String, Object> dbData = dbHelperJobData.getJobDataForPriority(faultPriority, value, withIpad, withEmail, withMobile);

        int resourceId = (Integer) dbData.get("ResourceId");
        int siteId = (Integer) dbData.get("SiteId");
        testData.put("siteId", siteId);
        int assetClassificationId = (Integer) dbData.get("AssetClassificationId");
        int faultTypeId = (Integer) dbData.get("FaultTypeId");

        testData.put("resourceId", resourceId);
        Job job = createJobForResource(siteId, resourceId, faultTypeId, faultPriority, assetClassificationId);

        runtimeState.scenario.write("Created job: " + job.toString().replaceAll(".*\\[", "").replace("]", ""));
        testData.put("jobReference", job.getJobReference());
    }

    private Job createJobForResource(int siteId, int resourceId, int faultTypeId, int priority, int assetClassificationId) throws Exception {
        int callerId = 0;

        if (testData.getString("resource") != null && "resource same as caller".equalsIgnoreCase(testData.getString("resource"))) {
            callerId = dbHelperResources.getResourceCallerId(resourceId);
        } else {
            Map<String, Object> caller = null;
            caller = dbHelper.getRandomResourceCallerWithPhoneNumber("Resource");
            callerId = (Integer) caller.get("Id");
            logger.debug("Caller: " + caller.get("Name"));
        }

        Integer locationId = dbHelper.getRandomLocation(siteId);
        if (locationId == null) {
            throw new Exception(String.format("Cannot find a valid location for site %d and classification %d", siteId, assetClassificationId));
        }
        runtimeState.domainUser = System.getProperty("user.name").toLowerCase();

        ApplicationUser applicationUser = getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        Job job = apiHelperJobs.createNewJobForResource(1, siteId, faultTypeId, priority, "Created for test automation", callerId, assetClassificationId, locationId, resourceId, null, createdBy);

        return job;
    }

    /**
     * @param priority - if priority is not valid for the customer it defaults to 0
     * @return
     * @throws Exception
     */
    private Job createLoggedJob(String priority) throws Exception {
        int faultPriority;

        if (priority.equals("PE")) {
            faultPriority = 4;
        } else {
            faultPriority = Integer.valueOf(dbHelper.isFaultPriorityValid(priority) ? priority.replace("P", "") :"0");
        }

        String resourceTypeName = testData.getString("resourceTypeName");
        String profileName = testData.getString("profileName");

        ApplicationUser applicationUser = getCurrentUserForJobCreation();

        String createdBy = applicationUser.getId();

        Job job;
        if (testData.getBoolean("dataset")) {
            int siteId = testData.getInt("siteId");
            int faultTypeId = testData.getInt("faultTypeId");
            int faultPriorityId = testData.getInt("faultPriorityId");
            int responsePriorityId = dbHelperJobData.getResoponsePriorityId(0);
            int assetClassificationId = testData.getInt("assetClassificationId");
            int locationId = testData.getInt("locationId");
            int callerId = testData.getInt("callerId");
            int resourceId = testData.getInt("resourceId");

            Map<String, Object> assetDetails = dbHelper.getAssetSubTypeAndClassification(assetClassificationId);

            logger.debug("SiteName: " + dbHelperSites.getSiteName(siteId));
            logger.debug("ResourceName: " + dbHelperResources.getResourceName(resourceId));
            logger.debug("AssetClassificationName: " + assetDetails.get("AssetClassification"));
            logger.debug("AssetSubTypeName: " + assetDetails.get("AssetSubType"));
            logger.debug("Location: " + dbHelper.getLocationNameFromId(locationId));
            logger.debug("FaultTypeName: " + dbHelper.getFaultTypeNameFromId(faultTypeId));

            job = apiHelperJobs.createNewJobForResource(1, siteId, faultTypeId, responsePriorityId, "Test Automation", callerId, assetClassificationId, locationId, resourceId, null, createdBy);

        } else if (testData.getInt("assignToResourceId") != null) {
            // assign to specified resource
            int resourceId = testData.getInt("assignToResourceId");
            testData.put("resourceId", resourceId);
            Integer searchFaultPriority = faultPriority == 0 ? null : faultPriority;

            Map<String, Object> dbData;
            Integer siteId = testData.getInt("assignToSiteId");
            if (siteId != null) {
                dbData = dbHelperJobData.getJobDataForResourceAndSite(resourceId, siteId, searchFaultPriority);
                if (dbData == null) {
                    dbData = dbHelperJobData.getJobDataForSite(siteId, searchFaultPriority);
                }
            } else {
                dbData = dbHelperJobData.getJobDataForResource(resourceId, searchFaultPriority);
            }

            if (dbData == null) {
                dbData = dbHelperJobData.getJobDataForAnyResource();
            }

            siteId = (int) dbData.get("SiteId");
            int faultTypeId = testData.getInt("faultTypeId") != null ? testData.getInt("faultTypeId") : (int) dbData.get("FaultTypeId");
            int faultPriorityId = (int) dbData.get("FaultPriorityId");
            int assetClassificationId = testData.getInt("assetClassificationId") != null ? testData.getInt("assetClassificationId") : (int) dbData.get("AssetClassificationId");
            int locationId = testData.getInt("locationId") != null ? testData.getInt("locationId") : (int) dbData.get("LocationId");

            Integer callerId = null;
            if (testData.getInt("callerId") != null) {
                callerId =  testData.getInt("callerId");
            } else {
                Map<String, Object> caller = dbHelper.getRandomResourceCallerWithPhoneNumber("Resource");
                callerId = (Integer) caller.get("Id");
            }

            Map<String, Object> assetDetails = dbHelper.getAssetSubTypeAndClassification(assetClassificationId);

            logger.debug("SiteName: " + dbData.get("SiteName"));
            logger.debug("ResourceName: " + dbHelperResources.getResourceName(testData.getInt("resourceId")));
            logger.debug("AssetClassificationName: " + assetDetails.get("AssetClassification"));
            logger.debug("AssetSubTypeName: " + assetDetails.get("AssetSubType"));
            logger.debug("Location: " + dbHelper.getLocationNameFromId(locationId));
            logger.debug("FaultTypeName: " + dbHelper.getFaultTypeNameFromId(faultTypeId));

            job = apiHelperJobs.createNewJobForResource(1, siteId, faultTypeId, faultPriorityId, "Test Automation", callerId, assetClassificationId, locationId, resourceId, null, createdBy);

        } else if (testData.getBoolean("usesGas")) {
            // All FGAS jobs should come here!
            Map<String, Object> dbData = dbHelperJobData.getJobDataForFgas();

            CommonSteps.assertDataFound("No previous site visits with similar gas details found. Manually run the test, then retry!", dbData);

            int siteId = (int) dbData.get("SiteID");
            int faultTypeId = (int) dbData.get("FaultTypeId");
            int faultPriorityId = (int) dbData.get("ResponsePriorityId");
            int assetClassificationId = (int) dbData.get("AssetClassificationId");
            int locationId = (int) dbData.get("LocationId");
            int resourceId = (int) dbData.get("ResourceId");
            String userName = (String) dbData.get("UserName");
            String userProfileName = (String) dbData.get("UserProfileName");
            String applicationUserId = (String) dbData.get("ApplicationUserId");

            testData.put("resourceId", resourceId);
            testData.put("siteId", siteId);
            testData.put("userName", userName);
            testData.put("applicationUserId", applicationUserId);
            testData.put("userProfileName", userProfileName);

            Map<String, Object> caller = dbHelper.getRandomResourceCallerWithPhoneNumber("Resource");
            int callerId = (Integer) caller.get("Id");
            logger.debug("Caller: " + caller.get("Name"));

            job = apiHelperJobs.createNewJobForResource(1, siteId, faultTypeId, faultPriorityId, "Test Automation", callerId, assetClassificationId, locationId, resourceId, null, createdBy);

        } else if ((resourceTypeName != null && resourceTypeName.contains("Contractor")) || (profileName != null && profileName.contains("Contractor"))) {
            // Create the job for a Contractor
            job = createJobForContractor(faultPriority, testData.getBoolean("gasUsage"), testData.getBoolean("leakCheck"), testData.getBoolean("inPlant"), testData.getBoolean("asset"), createdBy);

        } else {
            testData.put("resourceTypeName", "City Resource");

            // Create the job
            job = createJobResourcePhoneEmailIpadPriority("City Resource", "Any", "with", "with", faultPriority, testData.getBoolean("gasUsage"), testData.getBoolean("leakCheck"), testData.getBoolean("inPlant"), testData.getBoolean("asset"), createdBy);
        }

        apiHelperJobs.putContact(job.getCallerId(), job.getId());   // set contact to same as caller

        runtimeState.scenario.write("Created job: " + job.toString().replaceAll(".*\\[", "").replace("]", ""));
        testData.put("jobReference", job.getJobReference());
        testData.put("jobId", job.getId());
        testData.put("siteId", job.getSiteId());

        dbHelperAssertions.jobInHelpdeskFault(job.getJobReference());

        return job;
    }

    private void createSupplierOrderAwaitingInvoice(int jobReference, int resourceId) throws Throwable {
        runtimeState.scenario.write("There is no supplier order with awaiting invoice, creating the test data.");
        // Getting the job assigned to city resource without Parts order from DB

        // Inserting the part order in ios.tblEpochNewPartsRequest & ios.tblEpochNewPartsRequestLine

        boolean onlineInvoicing = !testData.getBoolean("supplierNotUsingPortalForInvoicing");

        if (!onlineInvoicing) {
            Map<String, Object> dbData = dbHelperInvoices.getRandomSupplierNotUsingPortalForInvoicing();

            testData.put("supplierResourceId", dbData.get("ResourceId"));
            testData.put("supplierId", dbData.get("SupplierId"));
            testData.put("epochId", dbData.get("EpochId"));
        }

        int epochId = onlineInvoicing ? resourceDao.getByResourceId(resourceId).getEpochId() : testData.getInt("epochId");

        assertNotNull(epochId);
        dbHelperParts.insertInToNewPartsRequest(jobReference, epochId);
        Integer partRequestID = dbHelperParts.getPartRequestID(jobReference);
        PartCode partDetails = partCodeDao.getRandomPartCode();
        String partCode = partDetails.getPartCode();
        Integer quantity = DataGenerator.randBetween(2, 10);
        Float unitPrice = BigDecimal.valueOf(partDetails.getUnitPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        String partDescription = partDetails.getDescription();
        String notes = "Note for test automation " + DateHelper.dateAsString(new Date());

        String supplierId = onlineInvoicing ? partDetails.getSupplierCode() : testData.getString("supplierId");

        dbHelperParts.insertInToNewPartsRequestLine(partRequestID, partCode, quantity, supplierId, notes, unitPrice, partDescription, epochId);

        // Waiting for the request processing to complete
        dbHelperJobs.updateEpochImportStatus(jobReference);
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> dbHelperJobs.getJobEpochImportStatus(jobReference), comparesEqualTo(70));

        // Update the cost expected and change the PO status in tblPO.
        Float costExpected = quantity * unitPrice;
        dbHelperJobs.updateJobPOStatus(jobReference, costExpected);

        int supplierResourceId = onlineInvoicing ? dbHelperInvoices.getSupplierResourceId(supplierId) : testData.getInt("supplierResourceId");
        testData.put("supplierResourceId", supplierResourceId);
        runtimeState.scenario.write("Test data created for the job: " + jobReference + " with Supplier Code:" + supplierId);
    }

    /**
     * Create a job with the first available priority that matches the query input.
     *
     * @param jobStatus - P0, P1...P6
     * @return
     * @throws Throwable
     */
    public Job createJobInStatus(String jobStatus, String priority) throws Throwable {
        Job job;
        int resourceId;

        switch (jobStatus) {
        case "Logged":
        case "Open":
            job = createLoggedJob(priority);
            break;

        case "Logged / New Notification Sent":
            job = createJobInStatus("Logged", priority);

            String resourceTypeName = testData.getString("resourceTypeName");
            String profileName = testData.getString("profileName");
            if ((resourceTypeName != null && resourceTypeName.contains("Contractor")) || (profileName != null && profileName.contains("Contractor"))) {
                approveContractorFunding(job.getJobReference(), job.getSiteId());
            }
            break;

        case "Logged / Awaiting Acceptance":
            job = createJobInStatus("Logged / New Notification Sent", priority);
            break;

        case "Logged / Awaiting Assignment":
            job = createLoggedAwaitingAssignmentJob();
            break;

        case "Logged / Helpdesk Chase":
            job = createLoggedJob(priority);
            createHelpdeskChaseForjob(job.getId());
            break;

        case "Logged / Non Helpdesk Manager Chase":
            job = createJobInStorePortal();
            createNonHelpdeskManagerChaseForjob(job.getJobReference());
            break;

        case "Logged / Awaiting Assignment / Immediate Callout":
            job = createLoggedAwaitingAssignmentJobWithImmediateCallout();
            break;

        case "Logged / Awaiting Funding Authorisation":
            testData.put("resourceTypeName", "Contractor");
            job = createJobInStatus("Logged", priority);
            if (testData.getString("additionalContractor") != null && testData.getString("additionalContractor").equalsIgnoreCase("store with an additional contractor resource")) {
                resourceId = dbHelperResources.getContractorWithConfiguredRate();
                apiHelperResources.addAdditionalResource_resourceId(job.getId(), resourceId);
            }
            break;

        case "Logged / Job Advise Deferred":
            job = createDeferredJob();
            break;

        case "Cancelled":
            job = createJobInStatus("Logged", priority);
            cancelJob(job.getId());
            break;

        case "Allocated":
        case "Allocated / ETA Provided":
            jobStatus = "Allocated / ETA Provided";
            job = createJobInStatus("Logged / Awaiting Acceptance", priority);
            resourceId = dbHelperResources.getAssignedResources(job.getJobReference()).get(0);
            String resourceType = dbHelperResources.getResourceType(resourceId);
            testData.put("resourceTypeName", resourceType);
            testData.put("siteId", job.getSiteId());
            acceptJob(job.getJobReference(), job.getId(), resourceType, jobStatus);

            if (testData.getBoolean("usesGas")) testData.put("siteVisit",  siteVisitsDao.getSiteVisitsForJobReference(job.getJobReference()).get(0));
            break;

        case "Logged / Allocated / Awaiting Funding Authorisation":
            job = createJobInStatus("Allocated", priority);
            resourceId = dbHelperResources.getContractorWithConfiguredRate();
            apiHelperResources.addAdditionalResource_resourceId(job.getId(), resourceId);
            break;

        case "Logged / Removed":
            job = createJobInStatus("Logged", priority);
            removeResource(job.getJobReference());
            break;

        case "Parked":
            job = createJobInStatus("Logged / Removed", priority);
            apiHelperJobs.parkJob(job.getId());
            break;

        case "Logged / New Notification Sent / Awaiting Reallocation":
            job = createJobInStatus("Logged / New Notification Sent", priority);
            resourceId = testData.getInt("resourceId");
            List<Map<String, Object>> resourceAvailability = dbHelperResources.getResourceAvailability(resourceId);
            if (resourceAvailability == null || resourceAvailability.size() == 0) {
                apiHelperResources.createAbsenceForResource(resourceId, null);
            }
            break;

        case "In Progress / Returning / Awaiting Reallocation":
            job = createJobInStatus("In Progress / Returning", priority);
            resourceId = testData.getInt("resourceId");
            resourceAvailability = dbHelperResources.getResourceAvailability(resourceId);
            if (resourceAvailability == null || resourceAvailability.size() == 0) {
                apiHelperResources.createAbsenceForResource(resourceId, null);
            }
            break;

        case "In Progress":
            job = createJobInStatus("Allocated / ETA Provided", priority);
            if ("Contractor".equals(testData.getString("resourceTypeName"))) {
                startJob(job.getJobReference()) ;
            } else {
                updateJobWithSiteVisit(job.getId(), job.getJobReference(), testData.getInt("resourceId"), job.getSiteId(), "Returning");
            }

            //If resource status is returning we need to add a previous visit
            if ("Returning".equalsIgnoreCase(testData.getString("resourceAssignmentStatus"))) {
                updateJobWithSiteVisit(job.getId(), job.getJobReference(), testData.getInt("resourceId"),job.getSiteId(),"Returning");

                // Also need to start the new visit (Move this into the method above?)
                if (testData.getString("resourceTypeName").contains("Contractor")) {
                    // Need to set up the impersonation cookie for the Contractor Tech
                    startContractorJob(job.getJobReference(), testData.getInt("resourceId"), REACTIVE_JOBTYPE);
                }
            }
            break;

        case "In Progress / On Site":
            job = createJobInStatus("Allocated / ETA Provided", priority);
            if ("Contractor".equals(testData.getString("resourceTypeName"))) {
                startJob(job.getJobReference());
            } else {
                throw new Exception("Unexpected resource type [City Resource] never goes to this state");
            }
            break;

        case "In Progress / Awaiting Reallocation":
            job = createJobInStatus("Allocated / ETA Provided", priority);
            if ("Contractor".equals(testData.getString("resourceTypeName"))) {
                startJob(job.getJobReference());
            }
            resourceId = testData.getInt("resourceId");
            dbHelperResources.deleteAbsences(resourceId);
            apiHelperResources.createAbsenceForResource(resourceId, null);
            break;

        case "In Progress / Returning" :
            if ("Contractor".equals(testData.getString("resourceTypeName")) || testData.getString("profileName").contains("Contractor")) {
                job = createJobInStatus( "In Progress / On Site", priority);
            } else {
                job = createJobInStatus("Allocated / ETA Provided", priority);
            }

            updateJobWithSiteVisit(job.getId(), job.getJobReference(), testData.getInt("resourceId"), job.getSiteId(), "Returning");

            break;

        case "In Progress / Resource Awaiting Parts Review":
            job = createJobInStatus("Allocated / ETA Provided", priority);
            updateJobWithSiteVisit(job.getId(), job.getJobReference(), testData.getInt("resourceId"), job.getSiteId(), "Awaiting Parts");
            break;

        case "Parts Awaiting Approval":
        case "Capital Parts Awaiting Approval":
        case "Non Capital Parts Awaiting Approval":
        case "In Progress / Parts Requested":
        case "In Progress / Awaiting Parts":
        case "Parts Requested Offsite":
            String equipmentType = jobStatus.equalsIgnoreCase("Capital Parts Awaiting Approval") ? "Capital" : jobStatus.equalsIgnoreCase("Non Capital Parts Awaiting Approval") ? "Non-Capital" : "Random";
            boolean isCapitalEquipment = jobStatus.equalsIgnoreCase("Capital Parts Awaiting Approval");
            testData.put("equipmentType", equipmentType);
            testData.put("isCapitalEquipment", isCapitalEquipment);
            testData.put("originalJobStatus", jobStatus);
            jobStatus = jobStatus.contains("In Progress /") ? jobStatus : "In Progress / " + jobStatus;

            // Get mobile user from CSV file and assign to job - Advocate env
            if (LOCALE.equalsIgnoreCase("en-US")) {
                String pathToCsv;
                if (propertyHelper.getMercuryUrl().contains("usad")) {
                    pathToCsv = "src/test/resources/spreadsheets/usersMobileSync_USAD_techs.csv";
                } else {
                    pathToCsv = "src/test/resources/spreadsheets/usersMobileSync_USWM.csv";
                }

                File csvFile = new File(pathToCsv);
                if (csvFile.isFile()) {
                    List<List<String>> resources = new ArrayList<List<String>>();
                    CSVReader csvReader = new CSVReader(new FileReader(pathToCsv));
                    String[] values = null;
                    while ((values = csvReader.readNext()) != null) {
                        resources.add(Arrays.asList(values));
                    }
                    csvReader.close();

                    // select active city tech resource with encrypted username
                    List<String> cityTechs = new ArrayList<String>();
                    cityTechs = dbHelperResources.getUsernamesForCityTechsWithIpad();
                    boolean found = false;
                    for (List<String> resource : resources) {
                        if (cityTechs.contains(resource.get(0))) {
                            String username = resource.get(0).toString();
                            resourceId = dbHelperResources.getResourceIdFromUserName(username);
                            testData.put("resourceUserName", username);
                            testData.put("assignToResourceId", resourceId);
                            String encryptedUsername = StringUtils.substringBefore(resource.get(3).toString(), "=");
                            testData.put("encryptedUsername", encryptedUsername);
                            found = true;
                            break;
                        }
                        if (found) break;
                    }
                } else {
                    throw new Exception("*** File NOT Found! ***");
                }
            }

            job = createJobInStatus("In Progress / Resource Awaiting Parts Review", priority);

            String autoCompleteValue = dbHelper.getSettingValue("PoAutoCompleteValue");
            Double lowerRange, upperRange;
            if (testData.getBoolean("partLessThanAutoCompleteValue")) {
                lowerRange = (double) 0;
                upperRange = Double.valueOf(autoCompleteValue);
            } else {
                lowerRange = Double.valueOf(autoCompleteValue);
                upperRange = (double) 1000.00;
            }

            PartCode dbPartCode = equipmentType.equals("Capital") ? partCodeDao.getRandomCapitalPartCodeBetweenValues(lowerRange, upperRange) : equipmentType.equals("Non-Capital") ? partCodeDao.getRandomNonCapitalPartCodeBetweenValues(lowerRange, upperRange) : partCodeDao.getRandomPartCodeBetweenValues(lowerRange, upperRange);
            int quantity = 1;
            if (dbPartCode == null) {
                dbPartCode = equipmentType.equals("Capital") ? partCodeDao.getRandomCapitalPartCode() : equipmentType.equals("Non-Capital") ? partCodeDao.getRandomNonCapitalPartCode() : partCodeDao.getRandomPartCode();
                Float price = dbPartCode.getUnitPrice();
                quantity = (int) (lowerRange / price + 1);
            }
            String partCode = dbPartCode.getPartCode();
            String description = dbPartCode.getDescription();
            String supplierCode = dbPartCode.getSupplierCode();
            String supplierName = dbPartCode.getSupplierName();
            resourceId = testData.getInt("resourceId");

            // Create parts order using Portal (old method) on Rainbow.
            // If this breaks, change to use Mobile method in the 'else' statement as techs no longer use Portal.
            if (propertyHelper.getMercuryUrl().contains("ukrb")) {
                apiHelperParts.partsRequest(job.getJobReference(), partCode, supplierCode, supplierName, quantity);
            } else {
                // Create parts order using Mobile on Walmart and Advocate
                Float unitPrice = dbPartCode.getUnitPrice();
                Map<String, Object> geo = dbHelperSites.getGeoLocationForJobReference(job.getJobReference());
                String geolocation = geo.get("Latitude") + "," + geo.get("Longitude");
                int siteId = testData.getInt("siteId");
                String date = tzHelper.getCurrentTimeAtSite(testData.getInt("siteId")).toString();
                String oldFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
                String newFormat = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
                Date d = sdf.parse(date);
                sdf.applyPattern(newFormat);
                String newDate = sdf.format(d);
                int epochId = dbHelperResources.getEpochId(resourceId);  // the epochId is used for mobile parts requests

                apiHelperParts.getMobileAuthenticationToken(testData.getString("encryptedUsername"));
                apiHelperParts.partsRequestUsingMobile(job.getJobReference(), partCode, description, supplierCode, quantity, unitPrice, epochId, siteId, geolocation, newDate);
            }
            dbHelperParts.updatePoStatus(job.getJobReference(), "Awaiting Approval", isCapitalEquipment); // normally jobs with Capital parts go to status 'Awaiting OSS Approval' which must be progressed in Mercury Desktop. This step avoids that
            apiHelperHangfire.processPurchaseOrderDocuments();

            logger.debug("Waiting for Purchase Order document to appear in the database....");
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> dbHelperJobs.getPONumberCount(job.getJobReference()) != 0);
            break;

        case "Logged / Work Transferred":
            job = createJobInStatus("In Progress / Resource Awaiting Parts Review", priority);

            int resourceAssignmentId = resourceAssignmentDao.getAssigmentByJobReference(job.getJobReference()).getId();
            int newResourceId = dbHelperResources.getUnassignedResourceId(job.getJobReference());
            apiHelperResources.transferResource(resourceAssignmentId, newResourceId);
            testData.put("resourceId", newResourceId);
            break;

        case "Complete":
        case "Fixed / Complete":
            job = createJobInStatus("In Progress", priority);
            updateJobWithSiteVisit(job.getId(), job.getJobReference(), testData.getInt("resourceId"), job.getSiteId(), "Complete");
            dbHelperJobs.updateOrderValue(job.getJobReference(), "500.00");
            break;

        case "Awaiting Feedback Response":
            job =  createJobInStatus("Complete", priority);
            int feedbackRatingId = dbHelperJobs.getRandomFeedbackRatingId();
            String feedbackComments = "Test Feedback comments entered on " + DateHelper.dateAsString(new Date());
            dbHelperJobs.insertJobFeedback(feedbackRatingId, feedbackComments, job.getJobReference());
            break;

        case "In Progress / Parts Requested / Awaiting Invoice With No Invoice":
            job = createJobInStatus("In Progress");
            createSupplierOrderAwaitingInvoice(job.getJobReference(), testData.getInt("resourceId"));
            dbHelperJobs.updateOrderValue(job.getJobReference(), "1000.00");
            break;

        case "In Progress / Parts Requested / Awaiting Invoice With Existing Invoice":
            job = createJobInStatus("In Progress / Parts Requested / Awaiting Invoice With No Invoice", priority);
            invoiceCreationHelper.addInvoiceDocument(dbHelperInvoices.getOrderRefNumber(job.getJobReference()), testData.getInt("supplierResourceId"), job.getJobReference(), testData.getString("profileName"));
            break;

        case "Complete / Orders Awaiting Invoice With No Invoice":
            testData.addStringTag("profileName", "Contractor Admin");
            job = createJobInStatus("Complete", priority);
            break;

        case "Complete / Orders Awaiting Invoice With Existing Invoice":
            testData.addStringTag("profileName", "Contractor Admin");
            job = createJobInStatus("Complete", priority);
            invoiceCreationHelper.addInvoiceDocument(dbHelperInvoices.getOrderRefNumber(job.getJobReference()), testData.getInt("resourceId"), job.getJobReference(), testData.getString("profileName"));
            break;

        case "Complete / Orders Awaiting Invoice With Existing Invoice without documents":
            job = createJobInStatus("Complete / Orders Awaiting Invoice With Existing Invoice", priority);
            Integer orderReference = dbHelperInvoices.getOrderRefNumber(job.getJobReference());
            invoiceCreationHelper.deleteInvoiceDocuments(orderReference);
            break;

        case "Invoices Awaiting Approval":
            job = createJobInStatus("Complete / Orders Awaiting Invoice With Existing Invoice", priority);
            userJob = userJobDao.getUserForJobResource(job.getJobReference(), testData.getString("profileName"), testData.getBoolean("useResourceTypeName"), testData.getString("resourceTypeName"));
            Integer orderRef = dbHelperInvoices.getOrderRefNumber(job.getJobReference());
            invoiceCreationHelper.addLinesAndSubmitTheInvoice(orderRef, userJob.getResourceId(), job.getJobReference());
            dbHelperInvoices.updateInvoiceStatusForInvoiceApprover(3, orderRef.toString());
            testData.addStringTag("profileName", "Invoice Approver");
            break;

        case "Invoice Awaiting Final Approval":
            job = createJobInStatus("Invoices Awaiting Approval", priority);
            orderRef = dbHelperInvoices.getOrderRefNumber(job.getJobReference());

            Map<String, Object> dbData = dbHelperResources.getResourceForJob("RFM", job.getJobReference());
            int rfmResourceId = (Integer) dbData.get("EpochId");

            Integer nextInvoiceProcessStep = dbHelperInvoices.getNextProcessStepIdForFinalApproval(job.getJobReference());
            nextInvoiceProcessStep = nextInvoiceProcessStep == null ? 1 : nextInvoiceProcessStep; // default to 8

            dbHelperInvoices.approveInvoiceAwaitingFinalApproval(orderRef.toString(), rfmResourceId, nextInvoiceProcessStep);
            break;

        default:
            throw new Exception("Unexpected status: " + jobStatus);
        }

        logger.debug("JobReference: " + job.getJobReference() + " (" + jobStatus + ")");
        return job;
    }

    /**
     * Create a job with the first available priority that matches the query input.
     *
     * @param jobStatus
     * @return
     * @throws Throwable
     */
    public Job createJobInStatus(String jobStatus) throws Throwable {
        return createJobInStatus(jobStatus, "P0");
    }

    private Job createLoggedAwaitingAssignmentJob() throws Throwable {
        int origToggleSetting = dbHelperSystemToggles.getSystemFeatureToggle("AutoAssign");
        //   toggleHelper.disableSystemFeatureToggle("AutoAssign");

        Job job;
        ApplicationUser applicationUser;
        String createdBy = null;
        if (testData.getBoolean("dataset")) {
            int siteId = testData.getInt("siteId");
            int faultTypeId = testData.getInt("faultTypeId");
            int faultPriorityId = testData.getInt("faultPriorityId");
            int assetClassificationId = testData.getInt("assetClassificationId");
            int locationId = testData.getInt("locationId");
            int callerId = testData.getInt("callerId");

            Map<String, Object> assetDetails = dbHelper.getAssetSubTypeAndClassification(assetClassificationId);

            logger.debug("SiteName: " + dbHelperSites.getSiteName(siteId));
            logger.debug("AssetClassificationName: " + assetDetails.get("AssetClassification"));
            logger.debug("AssetSubTypeName: " + assetDetails.get("AssetSubType"));
            logger.debug("Location: " + dbHelper.getLocationNameFromId(locationId));
            logger.debug("FaultTypeName: " + dbHelper.getFaultTypeNameFromId(faultTypeId));

            applicationUser = getCurrentUserForJobCreation();
            createdBy = applicationUser.getId();

            job = apiHelperJobs.createNewJob(1, siteId, faultTypeId, faultPriorityId, "Test Automation", callerId, assetClassificationId, locationId, createdBy);
            String autoUserName = dbHelperResources.getNameFromApplicationUserId(job.getCreatedBy());
            testData.put("requestedBy", autoUserName);

        } else {

            LogJobData logJobData = logJobDataDao.getLogJobData(false, null, null);
            CommonSteps.assertDataFound(logJobData);
            LogJobHelper.captureTestData(logJobData, testData);

            int callerId = dbHelper.getRandomCaller("Resource");
            Integer locationId = logJobData.getLocationId();
            if (locationId == null) {
                locationId = dbHelper.getRandomLocation(logJobData.getSiteId());
            }

            applicationUser = getCurrentUserForJobCreation();
            createdBy = applicationUser.getId();

            job = apiHelperJobs.createNewJob(1, logJobData.getSiteId(), logJobData.getFaultTypeId(), logJobData.getPriority(), "Created for test automation", callerId, logJobData.getAssetClassificationId(), locationId, createdBy);

            runtimeState.scenario.write("Job created with: jobTypeId - " + 1 + " siteId - " + logJobData.getSiteId() + " faultTypeId - " + logJobData.getFaultTypeId()
            + " faultPriorityId - " + logJobData.getPriority() + " description - Created for test automation callerId - " + callerId
            + " assetClassificationId - " + logJobData.getAssetClassificationId() + " locationId - " + locationId);
        }

        //        Commenting out below steps as system is not allowed to change the toggles , but keeping it for future reference
        //        if (origToggleSetting == 1) {
        //             re-enable toggle if it was originally enabled
        //            toggleHelper.enableSystemFeatureToggle("AutoAssign");
        //        }
        return job;
    }

    private Job createLoggedAwaitingAssignmentJobWithImmediateCallout() throws Throwable {
        int origToggleSetting = dbHelperSystemToggles.getSystemFeatureToggle("AutoAssign");
        toggleHelper.disableSystemFeatureToggle("AutoAssign");

        LogJobData logJobData = logJobDataDao.getLogJobData(true, null, null);
        CommonSteps.assertDataFound(logJobData);
        LogJobHelper.captureTestData(logJobData, testData);

        int callerId = dbHelper.getRandomCaller("Resource");
        Integer locationId = logJobData.getLocationId();
        if (locationId == null) {
            locationId = dbHelper.getRandomLocation(logJobData.getSiteId());
        }

        ApplicationUser applicationUser = getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        Job job = apiHelperJobs.createNewJob(1, logJobData.getSiteId(), logJobData.getFaultTypeId(), logJobData.getPriority(), "Created for test automation", callerId, logJobData.getAssetClassificationId(), locationId, createdBy);

        if (origToggleSetting == 1) {
            // re-enable toggle if it was originally enabled
            toggleHelper.enableSystemFeatureToggle("AutoAssign");
        }
        return job;
    }

    public Job createJobWithPriority(String priority) throws Exception {
        priority = priority.replace("P", "");

        SiteView siteView = siteViewDao.getRandomSiteWithCityTech();
        FaultPriorityMapping fpm = faultPriorityMappingDao.getRandomForSite(siteView.getId(), priority);
        int callerId = dbHelper.getRandomCaller("Resource");
        int locationId = dbHelper.getRandomLocation(siteView.getId(), fpm.getAssetClassificationId());
        int resourceId = dbHelperResources.getResourceIdForSite(siteView.getId());

        ApplicationUser applicationUser = getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        mercury.api.models.job.Job job = apiHelperJobs.createNewJobForResource(1, siteView.getId(), fpm.getFaultTypeId(), Integer.valueOf(priority), "create for TestAutomation by API request", callerId, fpm.getAssetClassificationId(), locationId, resourceId, null, createdBy);

        runtimeState.scenario.write("jobReference: " + job.getJobReference());
        testData.put("jobReference", job.getJobReference());
        return job;
    }

    public Job createJobForDeferrablePriority() throws Exception {
        ApplicationUser applicationUser = getCurrentUserForJobCreation();

        String createdBy = applicationUser.getId();
        Job job = null;
        testData.put("resourceTypeName", "City Resource");

        // Create the job
        LogJobData data = null ;
        data = logJobDataDao.getLogJobData_DeferrableFaultPriorities("2", false, false, "Any", false);

        if (data == null) {
            throw new PendingException("No suitable test data located");
        }
        job = processLogJobData(data, testData.getBoolean("asset"), createdBy);
        apiHelperJobs.putContact(job.getCallerId(), job.getId());   // set contact to same as caller

        runtimeState.scenario.write("Created job: " + job.toString().replaceAll(".*\\[", "").replace("]", ""));
        testData.put("jobReference", job.getJobReference());
        testData.put("jobId", job.getId());
        testData.put("siteId", job.getSiteId());

        dbHelperAssertions.jobInHelpdeskFault(job.getJobReference());
        return job;
    }
}
