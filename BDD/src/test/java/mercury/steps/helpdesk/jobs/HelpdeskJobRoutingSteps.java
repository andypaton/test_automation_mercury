package mercury.steps.helpdesk.jobs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.RandomUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.api.models.resource.Classification;
import mercury.api.models.resource.ContractorSite;
import mercury.api.models.resource.PhoneNumber;
import mercury.api.models.resource.Resource;
import mercury.api.models.site.SiteResponse;
import mercury.database.dao.AssetClassificationDao;
import mercury.database.dao.DeferralWorkingHoursDao;
import mercury.database.dao.FaultPriorityMappingDao;
import mercury.database.models.AssetClassification;
import mercury.database.models.FaultPriorityMapping;
import mercury.databuilders.TestData;
import mercury.helpers.JavaBeanCopier;
import mercury.helpers.LogJobHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.SiteCreationHelper;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperOnCallScheduler;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperWorkingHours;
import mercury.runtime.RuntimeState;

public class HelpdeskJobRoutingSteps {

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperOnCallScheduler dbHelperOnCallScheduler;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperWorkingHours dbHelperWorkingHours;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private OutputHelper outputHelper;
    @Autowired private SiteCreationHelper siteCreationHelper;
    @Autowired private ApiHelperResources apiHelperResources;
    @Autowired private PropertyHelper propertyHelper;

    @Autowired private FaultPriorityMappingDao faultPriorityMappingDao;
    @Autowired private DeferralWorkingHoursDao deferralWorkingHoursDao;
    @Autowired private AssetClassificationDao assetClassificationDao;

    private static final Logger logger = LogManager.getLogger();

    private static final String SITE_NAME = "TestAutomationSite";
    private static final String CONTRACTOR = "automationContractor";
    private static final String MST_PERMIE = "automationMstPermie";
    private static final String MST_ON_CALL = "automationMstOnCall";
    private static final String RHVAC_PERMIE = "automationRhvacPermie";
    private static final String RHVAC_ON_CALL = "automationRhvacOnCall";
    private static final String RHVAC_TECHNICIAN = "RHVAC Technician";
    private static final String MST = "MST";

    private static final String IN_HOURS = "IN HOURS";
    private static final String OUT_OF_HOURS = "OUT OF HOURS";


    @When("^a new job for a \"([^\"]*)\" fault is being logged$")
    public void a_new_job_for_a_fault_is_being_logged(String priority) throws Throwable {
        capturePriority(priority);
        searchForSiteAndOpenLogJobForm();
    }

    @When("^a new job for a \"([^\"]*)\" fault is being logged \"([^\"]*)\"$")
    public void a_new_job_for_a_fault_is_being_logged(String priority, String hours) throws Throwable {
        capturePriority(priority);
        setOnCallSchedulerHours(hours);
        if (IN_HOURS.equals(hours)) {
            setDeferralTime("outwith");
        }
        searchForSiteAndOpenLogJobForm();
    }

    @When("^a new job for a \"([^\"]*)\" fault is being logged \"([^\"]*)\" the brands Deferral time$")
    public void a_new_job_for_a_fault_is_being_logged_the_brands_Deferral_time(String priority, String when) throws Throwable {
        capturePriority(priority);
        setDeferralTime(when);

        FaultPriorityMapping data = faultPriorityMappingDao.getRandomForSite(testData.getInt("siteId"), testData.getString("priority"));

        if (data == null) {
            throw new PendingException("no suitable test data found");
        }

        runtimeState.scenario.write("Logging job for: " + data.toString());

        captureTestData(data);

        searchForSite(testData.getInt("siteId"));

        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();
    }


    @When("^a new job for a \"([^\"]*)\" fault is being logged \"([^\"]*)\" and \"([^\"]*)\" the brands Deferral time$")

    public void a_new_job_for_a_fault_is_being_logged_the_brands_Deferral_time(String priority, String hours, String deferralTime) throws Throwable {
        capturePriority(priority);

        setOnCallSchedulerHours(hours);
        setDeferralTime(deferralTime);

        if (OUT_OF_HOURS.equals(hours)) {
            testData.put("resourceWithinWorkingHours", false);
        }

        FaultPriorityMapping data = faultPriorityMappingDao.getRandomForSite(testData.getInt("siteId"), testData.getString("priority"));

        if (data == null) {
            throw new PendingException("no suitable test data found");
        }

        runtimeState.scenario.write("Logging job for: " + data.toString());

        captureTestData(data);

        searchForSite(testData.getInt("siteId"));

        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();
    }


    private void capturePriority(String priority) {
        if ("P2/P3".equals(priority)) {
            testData.put("priority", RandomUtils.nextInt(2, 3));
        } else {
            testData.put("priority", Integer.valueOf(priority.replace("P", "")));
        }
    }

    @When("^a new job for a \"([^\"]*)\" fault \"([^\"]*)\" Immediate Callout is being logged \"([^\"]*)\" \"([^\"]*)\" the brands Deferral time$")
    public void a_new_job_for_a_fault_with_immediate_callout_is_being_logged(String priority, String immediateCallout, String hours, String deferralTime) throws Throwable {
        capturePriority(priority);

        setOnCallSchedulerHours(hours);
        setDeferralTime(deferralTime);

        boolean callout = "WITH".equalsIgnoreCase(immediateCallout) ? true : false;
        testData.put("immediateCallout", callout);

        FaultPriorityMapping data = faultPriorityMappingDao.getRandomForSiteAndImmediateCallout(testData.getInt("siteId"), testData.getString("priority"), callout);

        if (data == null) {
            throw new PendingException("no suitable test data found");
        }

        runtimeState.scenario.write("Logging job for: " + data.toString());

        captureTestData(data);

        searchForSite(testData.getInt("siteId"));

        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();
    }

    @When("^a new job for a \"([^\"]*)\" fault \"([^\"]*)\" Immediate Callout is being logged \"([^\"]*)\" the brands Deferral time$")
    public void a_new_job_for_a_fault_with_immediate_callout_is_being_logged(String priority, String immediateCallout, String deferralTime) throws Throwable {
        capturePriority(priority);

        setDeferralTime(deferralTime);

        boolean callout = "WITH".equalsIgnoreCase(immediateCallout) ? true : false;
        testData.put("immediateCallout", callout);

        FaultPriorityMapping data = faultPriorityMappingDao.getRandomForSiteAndImmediateCallout(testData.getInt("siteId"), testData.getString("priority"), callout);

        if (data == null) {
            throw new PendingException("no suitable test data found");
        }

        runtimeState.scenario.write("Logging job for: " + data.toString());

        captureTestData(data);

        searchForSite(testData.getInt("siteId"));

        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();
    }

    private void setDeferralTime(String when) throws Throwable {
        int siteId = testData.getInt("siteId");
        if ("during".equals(when)) {
            dbHelperWorkingHours.updateDeferralWorkingHoursForSite(siteId, "00:01", "00:02");
            runtimeState.scenario.write("Deferral Time: StartAt [00:01], EndAt [00:02]");
        } else if ("outwith".equals(when)) {
            dbHelperWorkingHours.updateDeferralWorkingHoursForSite(siteId, "00:01", "23:59");
            runtimeState.scenario.write("Deferral Time: StartAt [00:01], EndAt [23:59]");
        } else {
            throw new Exception("Expected time: " + when);
        }

        deferralWorkingHoursDao.getDeferralWorkingHoursForSiteId(siteId);
    }

    private void setOnCallSchedulerHours(String when) throws Throwable {
        if (IN_HOURS.equalsIgnoreCase(when)) {
            dbHelperOnCallScheduler.updateOnCallSchedulerOutOfHours("23:59:59", "00:00:01");
            runtimeState.scenario.write("On Call Scheduler: StartTime [23:59:59], EndTime [00:00:01] ");
        } else {
            dbHelperOnCallScheduler.updateOnCallSchedulerOutOfHours("00:00:01", "23:59:59");
            runtimeState.scenario.write("On Call Scheduler: StartTime [00:00:01], EndTime [23:59:59] ");
        }
    }

    @When("^a City resource \"((?:is|is not))\" configured for the site$")
    public void a_City_resource_configured_for_the_site(String cityResourceConfigured) throws Throwable {
        if ("is".equals(cityResourceConfigured)) {
            testData.put("cityResourceConfigured", true);
        } else {
            testData.put("cityResourceConfigured", false);
            testData.put("onCall", false);
            an_On_Call_resource_is_not_available();
        }
    }

    @When("^a Contractor \"((?:is|is not))\" configured for the site$")
    public void a_Contractor_configured_for_the_site(String contractorConfigured) throws Throwable {
        if ("is".equals(contractorConfigured)) {
            testData.put("contractorConfigured", true);
        } else {
            testData.put("contractorConfigured", false);
        }
    }

    @When("^there \"((?:is|is not))\" a City Tech resource available$")
    public void the_City_Tech_resource_is_working_hours(String workingHours) throws Throwable {
        if ("is".equals(workingHours)) {
            testData.put("resourceWithinWorkingHours", true);
        } else {
            testData.put("resourceWithinWorkingHours", false);
        }
    }

    @When("^the City Tech \"([^\"]*)\" an application user with iPad$")
    public void the_City_Tech_resource_has_ipad(String ipad) throws Throwable {
        if ("is".equals(ipad)) {
            testData.put("ipad", true);
        } else {
            testData.put("ipad", false);
        }
    }

    private void outputTestDataConfig() throws ClientProtocolException, JSONException, IOException, TransformerException, ParseException {
        runtimeState.scenario.write("Site name: " + dbHelperSites.getSiteName(testData.getInt("siteId")) + " [" + testData.getInt("siteId") + "]");

        int cityTechResourceId = testData.getInt("cityTechResourceId");
        Resource resource = apiHelperResources.getResource(cityTechResourceId);
        runtimeState.scenario.write("City Tech Resource: " + resource.getName() + " [" + cityTechResourceId + "], Active ["  + resource.getActive() + "], Job logged during working hours [" + dbHelperWorkingHours.isResourceInHours(cityTechResourceId, runtimeState.timezone) + "]");

        int onCallResourceId = testData.getInt("onCallResourceId");
        resource = apiHelperResources.getResource(onCallResourceId);
        runtimeState.scenario.write("On Call Resource: " + resource.getName() + " [" + onCallResourceId + "], On Call [" + dbHelperOnCallScheduler.isOnCallSchedulerActive() + "]");

        int contractorResourceId = testData.getInt("contractorResourceId");
        resource = apiHelperResources.getResource(contractorResourceId);
        runtimeState.scenario.write("Contractor Resource: " + resource.getName() + " [" + contractorResourceId + "], Active [" + resource.getActive() + "]");
    }

    @When("^the job is logged$")
    public void the_job_is_logged() throws Throwable {

        reconfigureSiteResources();

        LogJobHelper.fillInJobForm(runtimeState, testData);

        LogJobHelper.saveJob(runtimeState, testData, outputHelper);
    }

    private void searchForSiteAndOpenLogJobForm() throws Exception {
        FaultPriorityMapping data = faultPriorityMappingDao.get(testData.getString("priority"), testData.getInt("siteId"), testData.getInt("contractorResourceId"), testData.getInt("cityTechProfileId"));

        if (data == null) {
            throw new PendingException("no suitable test data found");
        }

        runtimeState.scenario.write("Logging job for: " + data.toString());

        captureTestData(data);

        searchForSite(testData.getInt("siteId"));

        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();
    }

    @When("^the job is logged with deferred until \"([^\"]*)\"$")
    public void the_job_is_logged_with_no_deferral(String deferUntil) throws Throwable {
        testData.put("deferUntil", deferUntil);
        the_job_is_logged();
    }

    @ContinueNextStepsOnException
    @Then("^the Deferral Question \"([^\"]*)\" presented$")
    public void the_Deferral_Question_presented(String presented) throws Throwable {

        LogJobHelper.enterCaller(runtimeState, testData);
        LogJobHelper.enterAsset(runtimeState, testData);
        LogJobHelper.enterLocation(runtimeState, testData);
        LogJobHelper.enterDescription(runtimeState, testData);
        LogJobHelper.enterFault(runtimeState, testData);

        if ("IS".equalsIgnoreCase(presented)) {
            assertTrue("Expected Deferral Question to be presented", runtimeState.helpdeskLogJobPage.isDeferralQuestionDisplayed());
        } else {
            assertFalse("Expected Deferral Question NOT to be presented", runtimeState.helpdeskLogJobPage.isDeferralQuestionDisplayed());
        }
    }

    @Given("^an On Call City Tech resource is available$")
    public void an_On_Call_resource_is_available() throws Throwable {
        setOnCallSchedulerHours(OUT_OF_HOURS);
        dbHelperResources.updateRotaEntryToActive(testData.getInt("onCallResourceId"), testData.getInt("siteId"));
    }

    @Given("^an On Call resource is not available$")
    public void an_On_Call_resource_is_not_available() throws Throwable {
        dbHelperResources.updateRotaEntryToInactive(testData.getInt("onCallResourceId"), testData.getInt("siteId"));
    }

    @Given("^the On Call Scheduler is \"([^\"]*)\"$")
    public void the_On_Call_Scheduler_is(String when) throws Throwable {
        setOnCallSchedulerHours(when);
    }

    private void storeOriginalResource(Resource resource) {
        Resource originalResource = (Resource) JavaBeanCopier.copy(resource);
        runtimeState.originalResources.add(originalResource);
    }

    private void configureCityTechResource() throws Exception {
        int cityTechResourceId = testData.getInt("cityTechResourceId");
        if (testData.getBooleanTrueFalseOrNull("cityResourceConfigured") != null && !testData.getBoolean("cityResourceConfigured")) {
            dbHelperResources.setActive(cityTechResourceId, false);
        } else {
            dbHelperResources.setActive(cityTechResourceId, true);
        }

        if (testData.getBooleanTrueFalseOrNull("resourceWithinWorkingHours") != null) {
            if ( testData.getBoolean("resourceWithinWorkingHours") ) {
                dbHelperWorkingHours.updateResourceWorkingHours(cityTechResourceId, "00:01", "23:59");
                runtimeState.scenario.write("City Tech Resource Working Hours: StartAt [00:01], EndAt [23:59]");
            } else {
                dbHelperWorkingHours.updateResourceWorkingHours(cityTechResourceId, "00:01", "00:02");
                runtimeState.scenario.write("City Tech Resource Working Hours: StartAt [00:01], EndAt [00:02]");
            }
        }
    }

    private void configureContractor() throws Exception {
        if (testData.getBooleanTrueFalseOrNull("contractorConfigured") != null) {
            int contractorResourceId = testData.getInt("contractorResourceId");
            Resource contractorResource = apiHelperResources.getResource(contractorResourceId);
            storeOriginalResource(contractorResource);
            if (!testData.getBoolean("contractorConfigured")) {
                contractorResource.setActive(false);
            } else {
                contractorResource.setActive(true);
                String url = propertyHelper.getMercuryUrl();
                boolean alwaysChargeable = url.contains("-uswm") || url.contains("-usad") ? true : false;
                contractorResource.setAlwaysChargeable(alwaysChargeable);  // true: monitor = Funding Requests,        false: monitor = Awaiting Acceptance
            }
            apiHelperResources.createOrUpdateResource(contractorResource);
        }
    }

    private void reconfigureSiteResources() throws Exception {
        configureContractor();
        configureCityTechResource();
        outputTestDataConfig();
    }

    private Integer setupTestSite(String siteName) throws Exception {
        Integer siteId = dbHelperSites.getSiteId(siteName);
        if (siteId == null) {
            // create the site if it doesnt already exist
            SiteResponse site = siteCreationHelper.createNewSite(siteName);
            siteId = site.getId();
        }

        if (dbHelperSites.getOrganisationStructureSiteId(siteId) == null) {
            siteCreationHelper.addSiteToOrganisationStructure(siteId, siteName);
            dbHelper.addAssetsToSite(siteId);
        }

        logger.debug("Site name : " + siteName + " [" + siteId +"]");
        testData.put("siteId", siteId);
        return siteId;
    }

    private List<Classification> getClassifications(){
        List<Classification> classifications = new ArrayList<>();
        for (AssetClassification assetClassification : assetClassificationDao.getRandomAssetClassificationList()) {
            Classification classification = new Classification();
            classification.setClassificationId(assetClassification.getAssetClassificationId());
            classification.setPriority(assetClassification.getPriority());
            classifications.add(classification);
        }
        return classifications;
    }

    private void addContractorToSite(int siteId, String siteName) throws Exception {
        int resourceProfileId = dbHelperResources.getResourceProfileId("Contractor");
        testData.put("contractorProfileId", resourceProfileId);
        if (dbHelperResources.getContractorResources(siteId).size() == 0) {
            Integer contractorResourceId = dbHelperResources.getResourceId(CONTRACTOR);
            if (contractorResourceId == null) {
                dbHelper.insertSupplierTcode();
                apiHelperResources.createContractorResource(testData.getString("siteId"));
            } else {
                Resource contractorResource = apiHelperResources.getResource(contractorResourceId);
                List<ContractorSite> sites = contractorResource.getContractorSites();
                ContractorSite newSite = new ContractorSite();
                newSite.setSiteId(siteId);
                newSite.setClassifications(getClassifications());
                newSite.setSiteName(siteName);
                sites.add(newSite);
                apiHelperResources.createOrUpdateResource(contractorResource);
            }
        }
        List<Integer> contractorResourceIds = dbHelperResources.getContractorResources(siteId);
        assertTrue("Expected exactly one Contractor resource configured to site, but found contractor Id's: " + contractorResourceIds.toString(), contractorResourceIds.size() == 1);
        testData.put("contractorResourceId", contractorResourceIds.get(0));
        runtimeState.scenario.write("Contractor resourceId: " + testData.getInt("contractorResourceId"));
    }

    private void addCityTechToSite(int siteId, String siteName) throws Exception {
        String url = propertyHelper.getMercuryUrl();
        String resourceProfileName = url.contains("-uswm") || url.contains("-usad") ? MST : RHVAC_TECHNICIAN;
        int resourceProfileId = dbHelperResources.getResourceProfileId(resourceProfileName);
        testData.put("cityTechProfileId", resourceProfileId);
        if (dbHelperResources.getCityTechResources(siteId).size() == 0) {
            String resourceName = url.contains("-uswm") || url.contains("-usad") ? MST_PERMIE : RHVAC_PERMIE;
            Integer cityTechResourceId = dbHelperResources.getResourceId(resourceName);
            if (cityTechResourceId == null) {
                String payrollCode = dbHelper.getAvailablePayrollCode();
                apiHelperResources.createPermieResource(resourceName, resourceProfileId, siteId, payrollCode);
            } else {
                testData.put("cityTechResourceId", cityTechResourceId);
                configureCityTechResource();
            }
        }
        List<Integer> cityTechResourceIds = dbHelperResources.getCityTechResources(siteId);
        assertTrue("Expected exactly one City Tech resource configured to site", cityTechResourceIds.size() == 1);

        int cityTechResourceId = cityTechResourceIds.get(0);
        //        setupMobileLogin(cityTechResourceId); // user must have logged in to iPad at least once for it to receive notifications
        setResourceToActiveHasIPadAndMobile(cityTechResourceId);

        testData.put("cityTechResourceId", cityTechResourceId);
        runtimeState.scenario.write("City Tech resourceId: " + cityTechResourceId);
    }

    private void setResourceToActiveHasIPadAndMobile(int resourceId) throws Exception {
        Resource resource = apiHelperResources.getResource(resourceId);
        boolean changed = false;
        if (resource.getActive()) {
            resource.setActive(true);
            changed = true;
        }
        if ( !resource.getHasIPad() ) {
            resource.setHasIPad(true);
            changed = true;
        }

        List<PhoneNumber> phoneNumbers = resource.getPhoneNumbers();
        if (phoneNumbers.isEmpty()) {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setPhoneNumber("0794-643-8364");
            phoneNumber.setPhoneNumberTypeId(1);
            phoneNumbers = new ArrayList<>();
            phoneNumbers.add(phoneNumber);
            resource.setPhoneNumbers(phoneNumbers);
            changed = true;
        } else {
            if (phoneNumbers.get(0).getPhoneNumberTypeId() != 1) {
                phoneNumbers.get(0).setPhoneNumberTypeId(1); // set to Mobile
                changed = true;
            }
        }

        if ( !resource.getHasIPad() ) {
            resource.setHasIPad(true);
            changed = true;
        }

        if (changed) {
            apiHelperResources.createOrUpdateResource(resource);
        }
    }

    private void addOnCallResourceToSite(int siteId) throws Exception {
        if (dbHelperResources.getOnCallResources(siteId).isEmpty()) {
            String url = propertyHelper.getMercuryUrl();
            String resourceName = url.contains("-uswm") || url.contains("-usad") ? MST_ON_CALL : RHVAC_ON_CALL;
            Integer onCallResourceId = dbHelperResources.getResourceId(resourceName);
            String resourceProfileName = url.contains("-uswm") || url.contains("-usad") ? MST : RHVAC_TECHNICIAN;
            int resourceProfileId = dbHelperResources.getResourceProfileId(resourceProfileName);
            if (onCallResourceId == null) {
                String payrollCode = dbHelper.getAvailablePayrollCode();
                apiHelperResources.createOnCallResource(resourceName, resourceProfileId, siteId, payrollCode);
            }
            apiHelperResources.createOnCallSchedule(onCallResourceId, resourceProfileId, siteId);
        }
        List<Integer> onCallResourceIds = dbHelperResources.getOnCallResources(siteId);
        assertTrue("Expected exactly one ON CALL resource configured to site", onCallResourceIds.size() == 1);

        int onCallResourceId = onCallResourceIds.get(0);
        //        setupMobileLogin(onCallResourceId); // user must have logged in to iPad at least once for it to receive notifications
        setResourceToActiveHasIPadAndMobile(onCallResourceId);

        testData.put("onCallResourceId", onCallResourceId);
        runtimeState.scenario.write("On Call resourceId: " + onCallResourceId);
    }

    @Given("^the TestAutomationSite exists$")
    public void TestAutomationSite_exists() throws Exception {
        int siteId = setupTestSite(SITE_NAME);
        addContractorToSite(siteId, SITE_NAME);
        addCityTechToSite(siteId, SITE_NAME);
        addOnCallResourceToSite(siteId);
    }

    private void searchForSite(Integer siteId) {
        String siteName = dbHelperSites.getSiteName(siteId);
        testData.put("siteName", siteName);
        searchForSite(siteName);
    }

    private void searchForSite(String siteName) {
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteName);
    }

    private void captureTestData(FaultPriorityMapping fpm) {
        if (fpm.getAssetClassificationName() != null) {
            testData.put("subtypeClassification", fpm.getAssetSubTypeName() + " > " + fpm.getAssetClassificationName() );
        } else {
            testData.put("subtypeClassification", fpm.getAssetSubTypeName());
        }
        testData.put("fault", fpm.getFaultTypeName());
    }

    //    /**
    //     * user must have logged in to iPad at least once for it to receive notifications
    //     * @param resourceId
    //     */
    //    public void setupMobileLogin(int resourceId) {
    //        if ( !dbHelperResources.ipadUserHasLogin(resourceId) ) {
    //            dbHelperResources.insertNewRowApplicationUserMobileApplication(resourceId);
    //        }
    //
    //    }

}
