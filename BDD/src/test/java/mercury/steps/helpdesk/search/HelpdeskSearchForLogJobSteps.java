package mercury.steps.helpdesk.search;

import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Globalisation.toCurrency;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Duration.TWO_SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import mercury.api.models.resource.Resource;
import mercury.database.dao.BrandWorkingHoursDao;
import mercury.database.dao.DeferredJobDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.LogJobDataDao;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.dao.ResourceWorkingHoursDao;
import mercury.database.dao.RotaEntryDao;
import mercury.database.dao.SiteContractorAssetDao;
import mercury.database.dao.SiteViewDao;
import mercury.database.models.BrandWorkingHours;
import mercury.database.models.DeferredJob;
import mercury.database.models.LogJobData;
import mercury.database.models.ResourceWorkingHours;
import mercury.database.models.RotaEntry;
import mercury.database.models.SiteContractorAsset;
import mercury.database.models.SiteView;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.LogJobHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.asserter.dbassertions.AssertResourceWorkingHours;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperTimeZone;
import mercury.helpers.dbhelper.DbHelperWorkingHours;
import mercury.pageobject.web.helpdesk.HelpdeskCoreDetails;
import mercury.pageobject.web.helpdesk.HelpdeskTimelineTab;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.runtime.RuntimeState;
import mercury.steps.helpdesk.jobs.HelpdeskAcceptJobSteps;
import mercury.steps.helpdesk.jobs.HelpdeskLogAJobSteps;

public class HelpdeskSearchForLogJobSteps {
    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperWorkingHours dbHelperWorkingHours;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private ApiHelperResources apiHelperResources;

    @Autowired private DeferredJobDao deferredJobDao;
    @Autowired private JobDao jobDao;
    @Autowired private NewJob job;
    @Autowired private SiteContractorAssetDao siteContractorAssetDao;
    @Autowired private ResourceWorkingHoursDao resourceWorkingHoursDao;
    @Autowired private SiteViewDao siteViewDao;
    @Autowired private ResourceAssignmentDao resourceAssignmentDao;

    @Autowired private SiteView siteView;
    @Autowired private RotaEntryDao rotaEntryDao;
    @Autowired private SiteContractorAsset siteContractorAsset;
    @Autowired private TestData testData;

    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private LogJobDataDao logJobDataDao;
    @Autowired private BrandWorkingHoursDao brandWorkingHoursDao;

    @Autowired private AssertionFactory assertionFactory;

    @Autowired private HelpdeskSearchForSiteSteps helpdeskSearchForSiteSteps;
    @Autowired private HelpdeskLogAJobSteps helpdeskLogAJobSteps;
    @Autowired private HelpdeskAcceptJobSteps helpdeskAcceptJobSteps;

    private static DeferredJob deferredJob;
    private static final String PERMANENT = "Permanent";
    private static final String ON_CALL = "On Call Scheduler";

    private static List<String> rota = new ArrayList<>();

    @Before
    public void setup() {
        rota.clear();
    }


    @And("^the helpdesk is \"([^\"]*)\"$")
    public void the_helpdesk_is(String flag) {
        switch (flag.toUpperCase()) {
        case "OUT OF HOURS":
            assertTrue(dbHelperTimeZone.isHelpdeskOutOfHours());
            break;
        case "IN HOURS":
            assertFalse(dbHelperTimeZone.isHelpdeskOutOfHours());
            break;
        }
    }

    @When("^a search is run for that site$")
    public void a_search_is_run_for_that_site() throws Throwable {
        getSiteContractorAssetMapping();
        runSearch();
    }

    @When("^a search is run for a site with a resource capable of working on an asset$")
    public void search_for_a_site_with_an_available_contractor_for_asset_types() throws Throwable {
        testData.put("storeType", "CITY_TECH");
        testData.put("resourceInHours", true);
        testData.put("contractorConfigured", true);

        LogJobData logJobData = logJobDataDao.cityTechResource("1,2,3");

        if (logJobData == null) {
            throw new PendingException("Cannot find suitable test data");
        }

        LogJobHelper.captureTestData(logJobData, testData);

        siteView.copy(siteViewDao.get(logJobData.getSiteId()));

        runtimeState.scenario.write("TEST DATA: " + logJobData.toString());

        runSearch();
    }

    private void setBrandHours() throws Exception {

        BrandWorkingHours bwh = brandWorkingHoursDao.getBrandWorkingHoursToday(siteView.getBrandId());

        if (testData.getBoolean("brandInHours")) {
            // brand should be in-hours
            if (bwh == null) {
                throw new PendingException("Cannot find suitable test data: BrandId " + siteView.getBrandId() + " has no working hours today!");
            }

            if (!isInHours(bwh.getStartAt(), bwh.getEndAt())) {
                helpdeskSearchForSiteSteps.updateStoresBrandHours(siteView.getId(), "00:00:01", "23:59:59");
            }

        } else if (testData.getBooleanTrueFalseOrNull("brandInHours") != null) {
            // brand should be out-of-hours
            if (bwh != null && isInHours(bwh.getStartAt(), bwh.getEndAt())) {
                helpdeskSearchForSiteSteps.updateStoresBrandHours(siteView.getId(), "00:00:01", "00:00:02");
            }
        }

        bwh = brandWorkingHoursDao.getBrandWorkingHoursToday(siteView.getBrandId());
        if (bwh == null) {
            runtimeState.scenario.write("Brand working hours: none configured for today");
        } else {
            runtimeState.scenario.write("Brand working hours: " + bwh.getStartAt().replaceAll("\\.\\\\d+", "") + " to " + bwh.getEndAt());
        }
    }

    private void setResourceWorkingHours() throws Exception {
        if (testData.getBoolean("resourceInHours")) {
            // setResourceInHours(siteContractorAsset.getResourceId()); // using api
            setResourceInHours(); // using sql
        } else if (testData.getBooleanTrueFalseOrNull("resourceInHours") != null) {
            setResourceOutOfHours(); // using sql
        }

        ResourceWorkingHours rwh = resourceWorkingHoursDao.getResourceWorkingHoursForToday(siteContractorAsset.getResourceId());
        if (rwh == null) {
            runtimeState.scenario.write(String.format("Resource Working Hours not set for resourceId" + siteContractorAsset.getResourceId()));
        } else {
            runtimeState.scenario.write(String.format("Resource Working Hours: startAt=%s, endAt=%s", rwh.getStartAt(), rwh.getEndAt()));
        }
    }

    private void setResourceInHours() throws Exception {
        String startAt = siteContractorAsset.getStartAt();
        String endAt = siteContractorAsset.getEndAt();

        if (!isInHours(startAt, endAt)) {
            setResourceAvailable(siteContractorAsset.getResourceId());
        }
    }

    private void runSearch() throws Exception {

        runtimeState.scenario.write("Current local time: " + DateHelper.dateAsString(new Date()));
        int offsetHours = Integer.valueOf(dbHelperTimeZone.getHeadOfficeTimeDifference());
        if (offsetHours != 0) {
            runtimeState.scenario.write("Current Head Office time: " + dbHelperTimeZone.getHeadOfficeTimezone());
        }

        if (Boolean.valueOf(System.getProperty("updateWorkingHours"))) {
            setBrandHours();

            if (siteContractorAsset.getResourceId() != null) {
                setResourceWorkingHours();
            }
        }

        BrandWorkingHours bwh = brandWorkingHoursDao.getBrandWorkingHoursToday(siteView.getBrandId());
        if (bwh == null) {
            runtimeState.scenario.write("Brand working hours: none configured for today");
        } else {
            runtimeState.scenario.write("Brand working hours: " + bwh.getStartAt().replaceAll("\\.\\\\d+", "") + " to " + bwh.getEndAt());
        }

        switch (testData.getString("storeType")) {
        case "CONTRACT":
            testData.put("notes", "Authorized By: Unknown \nStandard Callout Amount Authorized: " + toCurrency(0) + " \nAmount authorized automatically");
            break;
        case "CITY_TECH":
            testData.put("notes", "Status: New Job Notification Sent");
            break;
        case "VENDOR":
            testData.put("notes", "Authorized By: Unknown \nStandard Callout \nAmount Authorized: " + toCurrency(500) + " \nUplift Funded by: Unspecified \nAmount authorized automatically");
        }

        if (!rota.isEmpty()) {
            // RotaEntry rotaEntry =
            // rotaEntryDao.getRotaEntryOnCall(siteContractorAsset.getResourceId(),
            // siteContractorAsset.getSiteId());
            List<RotaEntry> rotaEntries = rotaEntryDao.getRotaEntriesForSite(siteContractorAsset.getSiteId(), runtimeState.timezone);
            if (rotaEntries != null) {
                for (RotaEntry re : rotaEntries) {
                    runtimeState.scenario.write(re.toString());
                }
            } else {
                runtimeState.scenario.write("Site has no active rota entries");
            }
        }

        runtimeState.scenario.write("Searching for: " + siteView.getName());
        testData.put("siteName", siteView.getName());
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteView.getName());
        outputHelper.takeScreenshots();
    }

    @And("^a resource is available$")
    public void a_resource_is_available() throws Exception {

        int assetClassificationId = dbHelper.getClassificationIdForAssetSubType(job.getClassification());

        // set resource in hours
        int resourceId = dbHelperResources.getSuitableResourcesAllocatedToSite(siteView.getId(), assetClassificationId).get(0);
        setResourceAvailable(resourceId);
    }

    public void getSiteContractorAssetMappingForDebugging() {

        SiteContractorAsset sca = new SiteContractorAsset();

        sca.setSiteId(669);
        sca.setResourceId(1);
        sca.setResourceName("Aaron Farmery");
        sca.setResourceProfileName("GSM ");
        sca.setAssetClassificationSiteId(14506);
        sca.setAssetClassificationId(18);
        sca.setAssetSubTypeName("Compressor (HVAC)");
        sca.setFaultTypeName("P2 chased out of hours Escalated by District Manager");
        sca.setPriority(1);
        sca.setStartAt("07:00:00.0000000");
        sca.setEndAt("16:00:00.0000000");
        sca.setStartDayOfTheWeek(DateHelper.getDayOfWeek());
        sca.setEndDayOfTheWeek(DateHelper.getDayOfWeek());

        siteContractorAsset.copy(sca);

        siteView.copy(siteViewDao.get(siteContractorAsset.getSiteId()));

        String testData = siteContractorAsset.toString().replace("siteId=", "siteName=" + siteView.getName() + ", siteId=");
        runtimeState.scenario.write("TEST DATA: " + testData);
    }

    private void getSiteContractorAssetMapping() throws Exception {

        SiteContractorAsset randomsiteContractorAsset = null;

        Integer faultPriority = testData.getInt("faultPriority");
        Integer brandInHours = testData.getBooleanTrueFalseOrNull("brandInHours") == null ? 0 : testData.getBoolean("brandInHours") ? 1 : -1;
        Integer resourceInHours = testData.getBooleanTrueFalseOrNull("resourceInHours") == null ? 0 : testData.getBoolean("resourceInHours") ? 1 : -1;
        Boolean contractorConfigured = testData.getBoolean("contractorConfigured");
        Boolean storeWalkround = testData.getBoolean("storeWalkround");
        String capability = testData.getString("capability");

        switch (testData.getString("storeType")) {

        case "CONTRACT":
            randomsiteContractorAsset = siteContractorAssetDao.getRandomForContractStore(faultPriority);
            break;

        case "CITY_TECH":
            randomsiteContractorAsset = siteContractorAssetDao.getRandomForCityTechStore(faultPriority, brandInHours, resourceInHours, rota, runtimeState.timezone, contractorConfigured, storeWalkround, capability);
            break;

        case "VENDOR":
            randomsiteContractorAsset = siteContractorAssetDao.getRandomForVendorStore(faultPriority, brandInHours, contractorConfigured, rota, runtimeState.timezone, storeWalkround, 0);
        }

        if (randomsiteContractorAsset == null) {
            throw new PendingException("Cannot find suitable test data: contractor site > contractor > asset mapping for priority " + faultPriority);
        }

        siteContractorAsset.copy(randomsiteContractorAsset);

        siteView.copy(siteViewDao.get(siteContractorAsset.getSiteId()));

        String testData = siteContractorAsset.toString().replace("siteId=", "siteName=" + siteView.getName() + ", siteId=");
        runtimeState.scenario.write("TEST DATA: " + testData);
        captureTestData(siteContractorAsset);
    }

    @Given("^a contract store with an available resource to work on asset types with priority \"([^\"]*)\"$")
    public void contract_store_with_priority(String priority) throws Throwable {
        testData.put("storeType", "CONTRACT");
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
    }

    private boolean isInHours(String startAt, String endAt) {
        int offsetHours = Integer.valueOf(dbHelperTimeZone.getHeadOfficeTimeDifference());
        Date dateAtHeadOfficeTimezone = DateHelper.getDatePlusOffsetInHours(new Date(), offsetHours);
        String now = DateHelper.dateAsString(dateAtHeadOfficeTimezone, "HH:mm:ss") + ".000000";

        if (StringUtils.compare(startAt, now) < 0 && StringUtils.compare(now, endAt) < 0) {
            return true;
        }
        return false;
    }

    private void setResourceOutOfHours() throws Exception {
        String startAt = siteContractorAsset.getStartAt();
        String endAt = siteContractorAsset.getEndAt();

        if (isInHours(startAt, endAt)) {
            setResourceUnavailable(siteContractorAsset.getResourceId());
        }
    }

    private void setResourceAvailable(int resourceId) throws Exception {
        String startAt = "00:00:01";
        String endAt = "23:59:59";
        updateResourceWorkingHours(resourceId, startAt, endAt);
    }

    private void setResourceUnavailable(int resourceId) throws Exception {
        String startAt = "00:00:01";
        String endAt = "00:00:02";
        updateResourceWorkingHours(resourceId, startAt, endAt);
    }

    private void updateResourceWorkingHours(Integer resourceId, String startAt, String endAt) throws Exception {

        dbHelperWorkingHours.updateResourceWorkingHours(resourceId, startAt, endAt);

        // wait
        AssertResourceWorkingHours assertResourceWorkingHours = new AssertResourceWorkingHours(resourceId, startAt, endAt, resourceWorkingHoursDao);
        assertionFactory.performAssertion(assertResourceWorkingHours);

        runtimeState.scenario.write(String.format("Updated ResourceWorkingHours [resourceId=%d, startAt=%s, endAt=%s]", resourceId, startAt, endAt));
    }

    @When("^both a City Tech and a Contractor are capable of fixing the fault$")
    public void both_a_City_Tech_and_a_Contractor_are_capable_of_fixing_the_fault() throws Throwable {
        testData.put("capability", "CityTech, Contractor");
    }

    @Given("^the call is outside the store brand hours$")
    public void the_store_is_outside_hours() throws Throwable {
        testData.put("brandInHours", false);
    }

    @Given("^the call is within the store brand hours$")
    public void the_store_is_within_hours() throws Throwable {
        testData.put("brandInHours", true);
    }

    @Given("^the call is \"([^\"]*)\" the store brand hours$")
    public void the_store_is(String flag) throws Throwable {
        if ("within".equals(flag)) {
            the_store_is_within_hours();
        } else {
            the_store_is_outside_hours();
        }
    }

    @When("^a \"([^\"]*)\" fault is (logged|entered) against a City Tech store within the brand and resource working hours$")
    public void a_P_fault_is_logged_within_the_bwh_and_rwh(String priority, String action) throws Throwable {
        testData.put("storeType", "CITY_TECH");
        rota.add(PERMANENT);
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
        testData.put("resourceInHours", true);
        testData.put("brandInHours", true);
        testData.put("absent", false);
        testData.put("contractorConfigured", true);

        if ("logged".equals(action)) {
            enterAndSaveJob();
        }
    }

    @When("^a \"([^\"]*)\" fault is (logged|entered) against a City Tech store outwith the brand working hours$")
    public void a_P_fault_is_logged_outwith_the_bwh(String priority, String action) throws Throwable {
        testData.put("storeType", "CITY_TECH");
        rota.add(PERMANENT);
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
        testData.put("brandInHours", false);
        testData.put("contractorConfigured", true);

        if ("logged".equals(action)) {
            enterAndSaveJob();
        }
    }

    private void enterAndSaveJob() throws Throwable {

        a_search_is_run_for_that_site();
        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();

        testData.put("siteName", siteView.getName());
        testData.put("isQuoteRequested", false);

        String description = "Meltdown on " + DateHelper.dateAsString(new Date());
        testData.put("description", description);

        LogJobHelper.fillInJobForm(runtimeState, testData);

        outputHelper.takeScreenshots();

        LogJobHelper.saveJob(runtimeState, testData, outputHelper);

        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
    }

    @When("^a \"([^\"]*)\" fault is (logged|entered) against a City Tech store outwith the resource working hours but with City Tech on call$")
    public void a_P_fault_is_logged_outwith_rwh_and_on_call(String priority, String action) throws Throwable {
        testData.put("storeType", "CITY_TECH");
        rota.add(ON_CALL);
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
        testData.put("resourceInHours", false);
        testData.put("absent", false);
        testData.put("contractorConfigured", true);

        if ("logged".equals(action)) {
            enterAndSaveJob();
        }
    }

    @When("^a \"([^\"]*)\" fault is (logged|entered) against a City Tech store outwith the resource working hours$")
    public void a_P_fault_is_logged_outwith_rwh(String priority, String action) throws Throwable {
        testData.put("storeType", "CITY_TECH");
        rota.add(PERMANENT);
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
        testData.put("resourceInHours", false);
        testData.put("absent", false);
        testData.put("contractorConfigured", true);

        if ("logged".equals(action)) {
            enterAndSaveJob();
        }
    }

    @When("^a \"([^\"]*)\" fault is (logged|entered) against a City Tech store within the resource working hours$")
    public void a_P_fault_is_logged_for_cityTechStore_within_the_resource_working_hours(String priority, String action) throws Throwable {
        testData.put("storeType", "CITY_TECH");
        rota.add(PERMANENT);
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
        testData.put("resourceInHours", true);
        testData.put("absent", false);
        testData.put("contractorConfigured", true);

        if ("logged".equals(action)) {
            enterAndSaveJob();
        }
    }

    @When("^a \"([^\"]*)\" fault is (logged|entered) within the resource working hours$")
    public void a_P_fault_is_logged_within_the_resource_working_hours(String priority, String action) throws Throwable {
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
        testData.put("resourceInHours", true);
        testData.put("absent", false);
        if ("logged".equals(action)) {
            enterAndSaveJob();
        }
    }

    @When("^a \"([^\"]*)\" fault is (logged|entered) for a City Tech store outwith the resource working hours$")
    public void a_P_fault_is_logged_agan_within_the_resource_not_available(String priority, String action) throws Throwable {
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
        testData.put("resourceInHours", false);
        testData.put("absent", false);
        testData.put("contractorConfigured", true);

        if ("logged".equals(action)) {
            enterAndSaveJob();
        }
    }

    @Given("^a City Tech store with no City Tech on call$")
    public void a_City_Tech_store_with_no_configured_contractor_and_no_City_Tech_on_call() {
        testData.put("storeType", "CITY_TECH");
        rota.add(PERMANENT);
    }

    @Given("^a City Tech store with a City Tech on call$")
    public void a_City_Tech_store_with_a_City_Tech_on_call() {
        testData.put("storeType", "CITY_TECH");
        rota.add(PERMANENT);
        rota.add(ON_CALL);
    }

    @Given("^a City Tech store$")
    public void a_City_Tech_store() {
        testData.put("storeType", "CITY_TECH");
        rota.add(PERMANENT);
    }

    @When("^the job form is saved$")
    public void the_job_form_is_saved() throws Throwable {
        enterAndSaveJob();
    }

    @Given("^a contractor is not configured to the store$")
    public void a_contractor_is_not_configured_to_the_store() {
        testData.put("contractorConfigured", false);
    }

    @Given("^the resource has a phone number configured$")
    public void the_resource_has_a_phone_number_configured() {
        testData.put("phoneConfigured", true);
    }

    @Given("^no rota entries are active$")
    public void no_rota_entries_setup() {
        rota.clear();
    }

    @Given("^a rota entry is active for a \"([^\"]*)\"$")
    public void rota_city_tech(String resourceProfileName) {
        rota.add(ON_CALL);
        testData.put("rotaResourceProfileName", resourceProfileName);
    }

    @Given("^the store does not have a city tech on call$")
    public void no_city_tech_on_call() {
        rota.add(PERMANENT);
    }

    @Given("^the store has a city tech on call$")
    public void city_tech_on_call() {
        rota.add(ON_CALL);
    }

    @Given("^the store does not have a city tech on a permanent assignment or on call$")
    public void no_city_tech_with_a_permanent_assignment() {
        rota.clear();
    }

    @When("^the fault type is not Store Walkround$")
    public void the_fault_type_is_not_Store_Walkround() {
        testData.put("storeWalkround", false);
    }

    @When("the caller has not been advised to asign the job")
    public void the_caller_has_not_been_advised_to_asign_the_job() {
        testData.put("advisedToAssign", false);
    }

    @Given("^a \"([^\"]*)\" job with status \"([^\"]*)\"$")
    public void a_job_with_status(String jobType, String status) throws Throwable {
        testData.put("jobTypeName", jobType);
        testData.put("jobStatusName", status);
        testData.put("jobTypeChanged", "is not");
    }

    @Given("^a \"([^\"]*)\" job with \"([^\"]*)\" resource and status \"([^\"]*)\"$")
    public void a_job_with_status_and_resource(String jobType, String resourceAssigned, String status) throws Throwable {
        testData.put("jobTypeName", jobType);
        if (resourceAssigned.equalsIgnoreCase("no")) {
            testData.put("resourceAssigned", false);
        } else {
            testData.put("resourceAssigned", true);
        }
        testData.put("jobStatusName", status);
        testData.put("jobTypeChanged", "is not");
    }

    @Given("^a duplicate \"([^\"]*)\" job with \"([^\"]*)\" resource and status \"([^\"]*)\"$")
    public void a_duplicate_job_with_status(String jobType, String resourceAssigned, String status) throws Throwable {
        testData.put("duplicate", true);
        testData.put("jobTypeName", jobType);
        if (resourceAssigned.equalsIgnoreCase("no")) {
            testData.put("resourceAssigned", false);
        } else {
            testData.put("resourceAssigned", true);
        }
        testData.put("jobStatusName", status);
        testData.put("jobTypeChanged", "is not");
    }

    @Given("^a duplicate \"([^\"]*)\" job with status \"([^\"]*)\"$")
    public void a_duplicate_job_with_status_and_resource(String jobType, String status) throws Throwable {
        testData.put("duplicate", true);
        testData.put("jobTypeName", jobType);
        testData.put("jobStatusName", status);
    }

    @Given("^the Resource Assignment Status is \"([^\"]*)\"$")
    public void the_Resource_Status_is(String status) throws Throwable {
        testData.put("resourceAssignmentStatusName", status);
    }

    @Given("^a \"([^\"]*)\" resource is assigned$")
    public void a_resource_is_assigned(String resourceType) throws Throwable {
        testData.put("resourceTypeName", resourceType);
        testData.put("resourceAssignmentStatusName", "New Job Notification Sent");
    }

    @Given("^no resource is assigned$")
    public void no_resource_is_assigned() throws Throwable {
        testData.put("resourceAssignmentStatusName", "NONE");
    }

    @And("^the job type ((?:is|is not)) changed$")
    public void the_job_type_is_changed(String jobTypeChanged) throws Throwable {
        testData.put("jobTypeChanged", jobTypeChanged);
    }

    @Given("^a deferred job assigned to a City Tech resource \"([^\"]*)\" an iPad$")
    public void a_deferred_job_assigned_to_a_City_Tech_resource_with_an_iPad(String ipad) throws Throwable {

        boolean withIpad = "with".equals(ipad) ? true : false;

        Integer resourceId = dbHelperResources.getResourceIdWithIpad("City Tech", withIpad);
        if (resourceId == null) {
            resourceId = dbHelperResources.getResourceIdWithIpad("City Tech", !withIpad);
            Resource resource = apiHelperResources.getResource(resourceId);
            resource.setHasIPad(withIpad);
            apiHelperResources.createOrUpdateResource(resource);
        }

        if (resourceId == null) {
            throw new PendingException("Cannot find suitable test data: no suitable resource found!");
        }
        testData.put("resourceId", resourceId);

        mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Logged / Job Advise Deferred");

        deferredJob = deferredJobDao.getDeferredJob(job.getId());
        if (deferredJob == null) {
            throw new PendingException("Cannot find suitable test data: no suitable deferred jobs found!");
        }
        runtimeState.scenario.write("Job Reference: " + job.getJobReference());
        logger.debug("test data : " + deferredJob.toString());
        captureTestData(job);
    }

    @Given("^a deferred job assigned to a Contractor resource \"([^\"]*)\" an email address$")
    public void a_deferred_job_assigned_to_a_Contractor_resource_with_an_email_address(String email) throws Throwable {

        boolean withEmail = "with".equals(email) ? true : false;

        int resourceId = dbHelperResources.getResourceIdWithEmail("Contractor", withEmail);
        testData.put("resourceId", resourceId);

        mercury.api.models.job.Job job = jobCreationHelper.createJobInStatus("Logged / Job Advise Deferred");

        deferredJob = deferredJobDao.getDeferredJob(job.getId());
        if (deferredJob == null) {
            throw new PendingException("Cannot find suitable test data: no suitable deferred jobs found!");
        }
        logger.debug("test data : " + deferredJob.toString());
        captureTestData(job);
    }

    @Given("^they are accepting a job for a resource with no iPad$")
    public void the_are_accepting_a_job_for_a_resource_with_no_iPad() throws Throwable {
        helpdeskLogAJobSteps.a_new_job_is_logged_and_assigned_to_a_city_resource_with_phone_email_and_ipad("mobile", "with", "no");
        helpdeskLogAJobSteps.the_job_is_viewed();
        outputHelper.takeScreenshots();
        runtimeState.helpdeskAcceptJobPanel = runtimeState.helpdeskManageResourcesPanel.selectAcceptJobAction();
        helpdeskAcceptJobSteps.eta_populated_advised("is not");
    }

    @Given("^a job in Triage$")
    public void a_job_in_triage() throws Throwable {
        mercury.api.models.job.Job job = jobCreationHelper.createJobInTriage();
        captureTestData(job);
    }

    @Given("^the job deferral time is ((?:in|out of)) hours$")
    public void the_job_deferral_time_is_in_hours(String toggle) throws Throwable {
        if ("in".equals(toggle)) {
            testData.put("JobDeferralsHours", 1);
        }else {
            testData.put("JobDeferralsHours", -1);
        }
    }

    @Given("^a job in Triage awaiting callout resource assignment$")
    public void a_job_in_Triage_awaiting_callout_resource_assignment() throws Throwable {
        mercury.api.models.job.Job job = jobCreationHelper.createJobInTriage();
        captureTestData(job);
        apiHelperJobs.triageCallout(job.getId());
    }

    @When("^the deferral date passes$")
    public void the_deferral_date_passes() throws Throwable {

        String originalDeferredDate = runtimeState.helpdeskJobPage.getDeferralDate();

        dbHelperResources.updateResourceAssignmentDeferralDateToYesterday(deferredJob.getId());
        dbHelperJobs.updateJobDeferralDateToYesterday(deferredJob.getId());

        runtimeState.scenario.write("Resource Assignment deferral date updated to: " + resourceAssignmentDao.getAssigmentByJobId(deferredJob.getId()).getDeferralDate());
        runtimeState.scenario.write("Job deferral date updated to: " + jobDao.get(deferredJob.getId()).getDeferralDate());

        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).until(runtimeState.helpdeskJobPage.assertDeferralDateDoesNotContain(originalDeferredDate));
        outputHelper.takeScreenshots();
    }

    @When("^the Technical Bureau Triage panel is viewed$")
    public void the_Triage_panel_is_viewed() throws Throwable {
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getString("jobReference")).get();
        runtimeState.helpdeskTriagePanel = runtimeState.helpdeskJobPage.selectTriageAction();

        runtimeState.helpdeskTimelineTab = new HelpdeskTimelineTab(getWebDriver()).get();
        runtimeState.helpdeskCoreDetails = new HelpdeskCoreDetails(getWebDriver()).get();
    }

    @When("^the page is refreshed$")
    public void page_refresh() throws Throwable {
        POHelper.refreshPage();
    }

    @When("^the resources working hours commence$")
    public void the_resources_In_Hours_working_times_commence() throws Throwable {
        if (deferredJob.getRwhId() == null) {
            dbHelperWorkingHours.addResourceWorkingHours(deferredJob.getResourceId(), "00:00:01", "23:59:59");
        } else {
            dbHelperWorkingHours.updateResourceWorkingHours(deferredJob.getResourceId(), "00:00:01", "23:59:59");
        }

        ResourceWorkingHours rwh = resourceWorkingHoursDao.getResourceWorkingHoursForToday(deferredJob.getResourceId());
        runtimeState.scenario.write("Resource Working Hours updated: " + rwh.toString());
    }

    @Given("^the store has a city tech on a permanent assignment or on call$")
    public void with_city_tech_with_a_permanent_assignment() {
        rota.add(PERMANENT);
        rota.add(ON_CALL);
    }

    @Given("^the (?:time|call) is outwith the resource working hours$")
    public void the_time_is_outwith_the_resource_working_hours() {
        testData.put("resourceInHours", false);
    }

    @Given("^a vendor store with a priority \"([^\"]*)\" fault$")
    public void vendor_with_priority_fault(String priority) throws Throwable {
        testData.put("storeType", "VENDOR");
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
    }

    @Given("^a City Tech store with an available resource to work on asset types with priority \"([^\"]*)\"$")
    public void non_contract_store_with_city_tech_and_priority(String priority) throws Throwable {
        testData.put("storeType", "CITY_TECH");
        testData.put("absent", false);
        testData.put("resourceInHours", true);
        testData.put("faultPriority", priority.startsWith("P") ? Integer.valueOf(priority.replaceAll("^P", "")) : Integer.valueOf(priority));
        testData.put("contractorConfigured", true);
    }

    @Given("^the (?:time|call) is within the resource working hours$")
    public void the_time_is_within_the_resource_working_hours() {
        testData.put("resourceInHours", true);
    }

    @Given("^the resource is not absent$")
    public void the_resource_is_not_absent() {
        testData.put("absent", false);
    }

    @Given("^a contractor is configured to the store$")
    public void a_contractor_is_configured_to_the_store() {
        testData.put("contractorConfigured", true);
    }

    @Given("^a site with a resource capable of working on asset types$")
    public void a_site_with_an_available_contractor_for_asset_types() throws Throwable {

        siteContractorAsset.copy(siteContractorAssetDao.getRandom());

        siteView.copy(siteViewDao.get(siteContractorAsset.getSiteId()));

        String testData = siteContractorAsset.toString().replace("siteId=", "siteName=" + siteView.getName() + ", siteId=");
        runtimeState.scenario.write("TEST DATA: " + testData);
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

    private void captureTestData(SiteContractorAsset sca) {
        testData.put("assetClassificationId", sca.getAssetClassificationId());
        testData.put("assetClassificationName", sca.getAssetClassificationName());
        testData.put("assetClassificationSiteId", sca.getAssetClassificationSiteId());
        testData.put("assetSubTypeName", sca.getAssetSubTypeName());
        testData.put("faultTypeId", sca.getFaultTypeId());
        testData.put("fault", sca.getFaultTypeName());
        testData.put("priority", sca.getPriority());
        testData.put("resourceId", sca.getResourceId());
        testData.put("resourceName", sca.getResourceName());
        testData.put("resourceProfileName", sca.getResourceProfileName());
        testData.put("siteId", sca.getSiteId());
    }

    @And("^a job where the ETA is greater than the SLA$")
    public void a_job_where_the_eta_is_greater_than_the_sla() throws Throwable {
        int etaJobReference = dbHelperJobs.getRandomJobWhereETAExceedsSLA();
        if (etaJobReference == 0) {
            throw new PendingException("No test data found");
        }
        runtimeState.scenario.write("test data selected: jobReference = " + etaJobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(etaJobReference));
        testData.put("jobReference", etaJobReference);
        outputHelper.takeScreenshots();
    }

    @And("^a job where the ETA is within the SLA and the ETA has expired$")
    public void a_job_where_the_eta_is_within_the_sla_and_the_eta_has_expired() throws Throwable {
        int etaJobReference = dbHelperJobs.getRandomP1JobWhereETAExpiredAndWithinSLA();
        if (etaJobReference == 0) {
            throw new PendingException("No test data found");
        }
        runtimeState.scenario.write("test data selected: jobReference = " + etaJobReference);
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(String.valueOf(etaJobReference));
        testData.put("jobReference", etaJobReference);
        outputHelper.takeScreenshots();
    }

}