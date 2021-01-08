package mercury.steps;

import static mercury.helpers.Constants.City2019;
import static mercury.helpers.Constants.PA55W0RD;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import atu.testrecorder.ATUTestRecorder;
import cucumber.api.PendingException;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import mercury.config.CucumberConfig;
import mercury.database.dao.SystemFeatureToggleDao;
import mercury.database.dao.SystemSubFeatureToggleDao;
import mercury.database.dao.TestAutomationLockDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.SystemFeatureToggle;
import mercury.database.models.SystemSubFeatureToggle;
import mercury.database.models.TestAutomationLock;
import mercury.databuilders.TestData;
import mercury.databuilders.Toggles;
import mercury.helpers.ErrorCollector;
import mercury.helpers.LoginLogoutHelper;
import mercury.helpers.MonitorHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.ToggleHelper;
import mercury.helpers.WebDriverHelper;
import mercury.helpers.apihelper.ApiHelper;
import mercury.helpers.apihelper.ApiHelperAdmin;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperCompanies;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperMaintenance;
import mercury.helpers.dbhelper.DbHelperOnCallScheduler;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperTestAutomationUsers;
import mercury.helpers.dbhelper.DbHelperTestData;
import mercury.helpers.dbhelper.DbHelperTimeZone;
import mercury.helpers.dbhelper.DbHelperUsers;
import mercury.helpers.dbhelper.DbHelperWorkingHours;
import mercury.runtime.RuntimeState;
import mercury.runtime.ThreadManager;

@ContextConfiguration(classes = CucumberConfig.class)
@DirtiesContext()
public class SetupTeardownSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperOnCallScheduler dbHelperOnCallScheduler;
    @Autowired private DbHelperWorkingHours dbHelperWorkingHours;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private DbHelperCompanies dbHelperCompanies;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TestData testData;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private ApiHelper apiHelper;
    @Autowired private DbHelperTestAutomationUsers dbHelperTestAutomationUsers;
    @Autowired private TestAutomationLockDao testAutomationLockDao;
    @Autowired private DbHelperTestData dbHelperTestData;
    @Autowired private SystemFeatureToggleDao systemFeatureToggleDao;
    @Autowired private SystemSubFeatureToggleDao systemSubFeatureToggleDao;
    @Autowired private Toggles toggles;
    @Autowired private ToggleHelper toggleHelper;
    @Autowired private MonitorHelper monitorHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private ApiHelperAdmin apiHelperAdmin;
    @Autowired private DbHelperUsers dbHelperUsers;
    @Autowired private LoginLogoutHelper loginLogoutHelper;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private DbHelperMaintenance dbHelperMaintenance;


    private ATUTestRecorder recorder;
    protected WebDriver driver;

    /*
     * Scenarios can be tagged:
     *     @mcp : scenario should run for all customers
     *         OR
     *     @cust1 @cust2 @cust3 .... : list of customers
     */
    private void assertCustomerTags() {
        String[] customers = {"@ukrb", "@uswm", "@usah", "@usad"};  // Rainbow (ie. M&S), Walmart, Ahold, Advocate (ie. 7Eleven)
        String tags = runtimeState.scenario.getSourceTagNames().toString();
        boolean isCustomerTagged = Arrays.stream(customers).anyMatch(tags::contains);
        assertTrue("Scenario MUST contain EITHER @mcp OR one of: " + Arrays.toString(customers), tags.contains("@adhoc") || tags.contains("@maintenance") || tags.contains("@mcp") || tags.contains("@serviceChannel") || isCustomerTagged);
        assertFalse("Scenario cannot contain BOTH @mcp AND any of: " + Arrays.toString(customers), tags.contains("@mcp") && isCustomerTagged);
    }

    private void closeAndQuitWebDriver() {
        try {
            getWebDriver().close();
            getWebDriver().quit();
        } catch (Exception e) {
            // ignore
        }
    }

    private void newWebDriver() {
        closeAndQuitWebDriver();
        WebDriverHelper webDriverHelper = new WebDriverHelper();
        ThreadManager.webDriver.set(webDriverHelper.getNewWebDriver());
    }

    private void newWebDriver(String name) {
        String originalWebDriver = testData.tagExist("originalWebDriver") ? testData.getString("originalWebDriver") : System.getProperty("web.driver");
        testData.put("originalWebDriver", originalWebDriver);
        System.setProperty("web.driver", name);
        newWebDriver();
    }

    @Before
    public void setup(Scenario newScenario) throws Exception {
        runtimeState.scenario = newScenario;
        runtimeState.timestamp = dbHelper.getDbTimestamp();

        TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) ); // this should ensure dates and timestamps return the results as when run locally on SQL Server

        logger.debug("******** Scenario: " + runtimeState.scenario.getName() + " ********");

        runtimeState.domainUser = System.getProperty("user.name").toLowerCase();
        logger.debug("Domain user: " + runtimeState.domainUser);

        if (runtimeState.scenario.getSourceTagNames().contains("@geolocation") && !System.getProperty("web.driver").equals("Chrome")) {
            // chromeheadless driver does not work on Jenkins for geolocation tests - use chrome
            newWebDriver("Chrome");

        } else if (runtimeState.scenario.getSourceTagNames().contains("@internetexplorer")) {
            newWebDriver("IE");

        } else if (runtimeState.scenario.getSourceTagNames().contains("@chromeBrowser")) {
            newWebDriver("Chrome");
        }

        try {
            // Delete all cookies at the start of each scenario to avoid shared state between tests
            logger.debug("Deleting all cookies [" + getWebDriver().hashCode() + "]");

            getWebDriver().manage().deleteAllCookies();
        } catch (Exception e) {
            WebDriverHelper webDriverHelper = new WebDriverHelper();
            ThreadManager.webDriver.set(webDriverHelper.getNewWebDriver());
        }

        testData.reset();

        ErrorCollector.reset();

        runtimeState.timezone = getTimezone();

        runtimeState.originalResources.clear();

        dbHelper.deleteJobLocksOlderThan(30);

        if (!runtimeState.scenario.getSourceTagNames().contains("@setupTestUsers")) {
            int count = 3;
            do {
                try {
                    setupCookiesForApiHelper();
                    count = 0;
                } catch (TimeoutException te) {
                    count = count - 1;
                    newWebDriver();
                }
            } while (count > 0);
        }

        toggles.put("originalSystemFeatureToggles", systemFeatureToggleDao.getAll());
        toggles.put("originalSystemSubFeatureToggles", systemSubFeatureToggleDao.getAll());

        assertCustomerTags();

        if (runtimeState.scenario.getSourceTagNames().contains("@video")) {
            String scenarioName = runtimeState.scenario.getName().replaceAll("\\[.*\\]", "").replaceAll("-", "").trim().replaceAll("\\s+", "_");
            recorder = new ATUTestRecorder(System.getProperty("user.dir") + "\\target\\", "TestVideo-" + scenarioName, false);
            //To start video recording.
            recorder.start();
        }

    }

    private String getTimezone() {
        int offset = Integer.valueOf(dbHelperTimeZone.getHeadOfficeTimeDifference());
        return ((offset < 0) ? "-" : "") + String.format("%02d:00", Math.abs(offset));
    }

    private void setupCookiesForApiHelper() throws Exception {

        String url = null;
        if (runtimeState.scenario.getSourceTagNames().contains("@store_portal")) {
            url = propertyHelper.getStorePortalUrl();
        } else {
            url = propertyHelper.getMercuryUrl();
        }

        List<Map<String, Object>> testAutomationUsers = dbHelperTestAutomationUsers.getITUsers();

        for (Map<String, Object> dbData: testAutomationUsers) {
            String userName = dbData.get("UserName").toString();
            String firstnameLastname = dbData.get("Name").toString().trim();
            String password = userName.equalsIgnoreCase(runtimeState.domainUser) ? City2019 : PA55W0RD;
            try {
                logger.debug("apiUser: " + firstnameLastname + " (username: " + userName + ", password: " + password + ")");
                apiHelper.setupCookies(url, userName, password);
                testData.put("apiUser", firstnameLastname);
                break;

            } catch (AssertionError e) {
                logger.debug("Failed to login as: " + firstnameLastname + ", username: " + userName + ", password: " + password);
                // ignore login failure and move on to next attempt
            }
        }

        testData.copy(testData);
    }

    private void writeConsoleLogs () {
        LogEntries logs = getWebDriver().manage().logs().get("browser");
        for (LogEntry entry : logs) {
            // dont write WARNING's and INFO's
            if ( !entry.getLevel().toString().equals("WARNING")
                    && !entry.getLevel().toString().equals("INFO")
                    && !entry.getLevel().toString().equals("FINE")) {
                runtimeState.scenario.write("Console log: " + entry.toString());
            }
        }
    }

    /**
     * The scenario has failed.  If a job reference exists in test data then lock the job to prevent further tests using it
     */
    private void lockFailedJob() {
        if (testData.getString("jobReference") != null) {
            TestAutomationLock lock =  new TestAutomationLock();
            lock.setReference((int) testData.getInt("jobReference"));
            lock.setType("job");
            lock.setReason("Failed Test");
            lock.setDetail(runtimeState.scenario.getName());
            testAutomationLockDao.create(lock);
        }
    }

    /**
     * Output the before and after feature toggle values, only outputs before if changed.
     * Useful for debugging failed tests.
     */
    private void outputSystemToggles() {
        String originalSystemFeatureToggles = toggles.getList("originalSystemFeatureToggles", SystemFeatureToggle.class).toString();
        String originalSystemSubFeatureToggles = toggles.getList("originalSystemSubFeatureToggles", SystemSubFeatureToggle.class).toString();

        String afterSystemFeatureToggles = systemFeatureToggleDao.getAll().toString();
        String afterSystemSubFeatureToggles = systemSubFeatureToggleDao.getAll().toString();

        runtimeState.scenario.write("System Feature Toggles (BEFORE): " + originalSystemFeatureToggles
                + "\nSystem Feature Toggles (AFTER) : " + afterSystemFeatureToggles
                + "\n\nSystem Sub Feature Toggles (BEFORE): " + originalSystemSubFeatureToggles
                + "\nSystem Sub Feature Toggles (AFTER) : " + afterSystemSubFeatureToggles);
    }

    private void outputScenarioFailReport() {
        try {
            // Always want to output the console errors
            writeConsoleLogs();
        }  catch (Throwable e) {
            // ignore
        }

        try {
            for (int i = 0; i < ErrorCollector.errors.size(); i++) {
                runtimeState.scenario.write("Error found on URL: " + ErrorCollector.urls.get(i));
                runtimeState.scenario.write(ErrorCollector.errors.get(i).getMessage());
                // runtimeState.scenario.embed(ErrorCollector.screenshots.get(i), "image/png");
            }
            if (ErrorCollector.errors.size() > 0) {
                throw new PendingException("Warning: non terminal errors captured earlier in scenario");
            }
        } catch (Throwable e) {
            // ignore
        }

        try {
            if (runtimeState.scenario.isFailed()) {
                try {
                    runtimeState.scenario.write("Scenario Failed:\nBrowser: " + outputHelper.getBrowserInfo() + "\nDate   : " + new Date() + "\nURL    : " + getWebDriver().getCurrentUrl());
                } catch (Throwable e) {
                    runtimeState.scenario.write("Scenario Failed:\nBrowser: " + outputHelper.getBrowserInfo() + "\nDate   : " + new Date() + "\nURL    : ");
                }

                runtimeState.scenario.write("Test Data: " + testData.getTestData());

                try {
                    if (runtimeState.helpdeskTimelineTab != null) {
                        runtimeState.helpdeskTimelineTab.clearSearch(); // clearing the search box will redisplay all events on the timeline!
                    }
                } catch (Throwable e) {
                    //do nothing
                }

                try {
                    lockFailedJob();
                } catch (Throwable e) {
                    runtimeState.scenario.write("Failed to lock job test failure on tear down.");
                }

                try {
                    outputSystemToggles();
                } catch (Throwable e) {
                    runtimeState.scenario.write("Failed to output toggles on tear down.");
                }

                try {
                    outputHelper.takeScreenshots(true); // note: sometimes selenium can throw an exception while taking the snapshot
                } catch (Throwable e) {
                    runtimeState.scenario.write("Failed to create screenshot on tear down");
                }
            }
        } catch (Throwable e) {
            System.err.print("Failed to output Scenario Fail Report : " + e.getMessage());
        }
    }

    private void resetPassword() {
        if (testData.getString("newPassword") != null) {
            // password has been updated during test - so reset it!
            ApplicationUser appUser = (ApplicationUser) testData.get("appUser");
            dbHelperUsers.resetPassword(appUser.getId(), appUser.getPasswordHash());
        }
    }

    private void resetSystemToggles() throws Exception {
        try {    // surround with try-catch so that following teardown steps will always be run

            if ( runtimeState.scenario.getSourceTagNames().contains("@admin_toggles")) {
                toggleHelper.resetTogglesViaAPI();

            } else {

                if ( toggles.getBoolean("haveTogglesBeenUpdated") ) {
                    Map<String, Object> updatedFeatureToggles = toggles.getMap("updatedFeatureToggles");
                    Map<String, Object> updatedSubFeatureToggles = toggles.getMap("updatedSubFeatureToggles");

                    if (updatedFeatureToggles != null) {
                        List<SystemFeatureToggle> originalFeatureToggles = toggles.getList("originalSystemFeatureToggles", SystemFeatureToggle.class);
                        for (String updatedFeatureId : updatedFeatureToggles.keySet()) {
                            for (SystemFeatureToggle originalFeatureToggle : originalFeatureToggles) {
                                if (Integer.valueOf(updatedFeatureId) == originalFeatureToggle.getId()) {
                                    // reset only the updated Feature toggles
                                    int active = originalFeatureToggle.getActive() ? 1 : 0;
                                    toggles.addToMap("systemFeatureToggles", originalFeatureToggle.getFeature(), active);
                                    runtimeState.scenario.write("Resetting Feature toggle" + originalFeatureToggle.getFeature() + " to: " + originalFeatureToggle.getActive());
                                }
                            }
                        }
                    }

                    if (updatedSubFeatureToggles != null) {
                        List<SystemSubFeatureToggle> originalSubFeatureToggles = toggles.getList("originalSystemSubFeatureToggles", SystemSubFeatureToggle.class);
                        for (String updatedSubFeatureId : updatedSubFeatureToggles.keySet()) {
                            for (SystemSubFeatureToggle originalSubFeatureToggle : originalSubFeatureToggles) {
                                if (Integer.valueOf(updatedSubFeatureId) == originalSubFeatureToggle.getId()) {
                                    // reset only the updated Sub Feature toggles
                                    int active = originalSubFeatureToggle.getActive() ? 1 : 0;
                                    toggles.addToMap("systemSubFeatureToggles", originalSubFeatureToggle.getSubFeature(), active);
                                    runtimeState.scenario.write("Resetting Sub-Feature toggle" + originalSubFeatureToggle.getSubFeature() + " to: " + originalSubFeatureToggle.getActive());
                                }
                            }
                        }
                    }

                    apiHelperAdmin.setSystemFeatureToggles();
                }
            }
        } catch (Throwable t) {
            System.err.println("Ignoring thrown exception: " + t.getMessage());
        }
    }

    private void resetActivatedMonitor() {
        try {
            if (testData.getString("activatedMonitor") != null) {
                monitorHelper.disableMonitor();
            }
        } catch (Exception e) {
            System.err.print("Failed to reset Activated Monitor : " + e.getMessage());
        }
    }

    private void resetCoreProductValues() {
        // Reset core product values - These should be replaced by API calls!
        try {
            dbHelperWorkingHours.resetBrandWorkingHours();
            dbHelperWorkingHours.resetDeferralWorkingHours();

            if (testData.getBoolean("resetOnCallScheduler")) {
                // reset In Hours - Out Of Hours
                dbHelperOnCallScheduler.resetOnCallScheduler();
            }

            if (testData.getString("origLocation") != null) {
                String origLocation = testData.getString("origLocation");
                String[] parts = origLocation.split(",");
                dbHelperSites.updateSiteLocation(Integer.valueOf(parts[0]), Double.valueOf(parts[1]), Double.valueOf(parts[2]));
            }

            if (testData.getInt("originalSiteStatusId") != null) {
                logger.debug(
                        "Updating siteId " + testData.getInt("siteId")
                        + " to status "
                        + dbHelperSites.getSiteStatusName(testData.getInt("originalSiteStatusId")));
                dbHelperSites.updateSiteStatus(testData.getInt("siteId"), testData.getInt("originalSiteStatusId"));
            }

            if (testData.getString("primaryCompanyName") != null) {
                logger.debug("Updating primary company to its original value ");
                if (testData.getString("primaryCompanyName").equalsIgnoreCase(testData.getString("companyName"))) {
                    dbHelperCompanies.resetToPrimaryCompany(testData.getString("primaryCompanyName"));
                } else {
                    dbHelperCompanies.resetToPrimaryCompany(testData.getString("primaryCompanyName"));
                    dbHelperCompanies.resetToNonPrimaryCompany(testData.getString("companyName"));
                }
            }

            if (testData.getFloat("origRefrigerantCharge") != null) {
                dbHelperGas.updateRefrigerantCharge(testData.getInt("assetId"), testData.getFloat("origRefrigerantCharge"));
            }

            if (testData.getString("pdfPath") != null) {
                File file = new File(System.getProperty("user.dir")  + testData.getString("pdfPath"));
                file.delete();
            }

            cleanupTestAutomationSite();

        } catch (Exception e) {
            System.err.print("Failed tear down when resetting core product values : " + e.getMessage());
        }
    }


    private void resetAutomationLocks() {
        try {
            testData.removeTag("jobReference");
        } catch (Exception e) {
            System.err.print("Failed tear down when removing automation locks : " + e.getMessage());
        }
    }

    private void resetInvoiceLineTypes() {
        List<Map<String, Object>> existingLineTypeSettings = testData.getListMap("existingLineTypeSettings");
        if (testData.getBoolean("haveLineTypeSettingsBeenUpdated")) {
            int index = 0;
            for (Map<String, Object> map : existingLineTypeSettings) {
                if (map.get("Description").equals(testData.getList("activeInvoiceLineTypes", Object.class).get(index))) {
                    String description = map.get("Description").toString();
                    boolean isMandatory = (boolean) map.get("IsMandatory");
                    boolean isLabor = (boolean) map.get("IsLabor");
                    dbHelperInvoices.resetInvoiceLineTypes(description, isMandatory, isLabor);
                    index++;
                }
            }
        }
    }

    public void cleanupTestData() throws ClientProtocolException, IOException {
        if (testData.getBoolean("dataset")) {
            if (testData.getInt("jobReference") != null) {
                apiHelperJobs.cancelJob(testData.getInt("jobReference"));
            }
            dbHelperMaintenance.removeOnSiteResourcesForTerminatedJobs();
        }
    }

    private void saveTestData() {
        // first remove anything that wont be needed in future runs
        testData.removeTag("grid");

        dbHelperTestData.saveTestData();
    }

    private void deleteResourceProfile() {
        List<Integer> resourceProfileIdList = dbHelperResources.getResourceProfileIdsCreatedByAutoTest();
        if (!resourceProfileIdList.isEmpty()) {
            String resourceProfileIds = resourceProfileIdList.toString();
            resourceProfileIds = resourceProfileIds.substring(1, resourceProfileIds.length()-1);
            dbHelper.deleteResourceProfileWorkingHoursAddedByAutoTest(resourceProfileIds);
            dbHelper.deleteResourceProfileLaborRateAddedByAutoTest(resourceProfileIds);
            dbHelper.deleteResourceProfileAddedByAutoTest(resourceProfileIds);
        }
    }

    private void deleteUser() {
        List<String> userIdList = dbHelperResources.getUserIdsCreatedByAutoTest();
        if (!userIdList.isEmpty()) {
            String userIds = userIdList.stream().map(s -> "'" + s + "'").collect(Collectors.joining(", "));
            dbHelper.deleteUsersFromUserImpersonationTable(userIds);
            dbHelper.deleteUsersFromLockAuditTable(userIds);
            dbHelper.deleteUsersFromIncidentLockTable(userIds);
            dbHelper.deleteAllApplicationUsersAddedByAutoTestUser();
        }
    }

    private void deleteUserProfile() {
        List<Integer> userProfileIdList = dbHelperResources.getUserProfileIdsCreatedByAutoTest();
        if (!userProfileIdList.isEmpty()) {
            String userProfileIds = userProfileIdList.toString();
            userProfileIds = userProfileIds.substring(1, userProfileIds.length()-1);
            dbHelper.deleteFromPermissionTable(userProfileIds);
            dbHelper.deleteFromUserProfileToMonitorAreaTable(userProfileIds);
            dbHelper.deleteApplicationUserAddedByAutoTestUserProfile(userProfileIds);
            dbHelper.deleteAllUserProfilesAddedByAutoTest();
        }
    }

    private void deleteResource() {
        List<Integer> resourceIdList = dbHelperResources.getResourceIdsCreatedByAutoTest();
        if (!resourceIdList.isEmpty()) {
            String resourceIds = resourceIdList.toString();
            resourceIds = resourceIds.substring(1, resourceIds.length()-1);
            dbHelper.deleteResourceCallersAddedByAutoTest(resourceIds);
            dbHelper.deleteResourceEmailAddressesAddedByAutoTest(resourceIds);
            dbHelper.deleteResourcePhoneNumbersAddedByAutoTest(resourceIds);
            dbHelper.deleteApplicationUsersAddedByAutoTest(resourceIds);
            dbHelper.deleteResourceNotificationMethodsAddedByAutoTest(resourceIds);
            dbHelper.deleteAssetClassificationSiteContractorMappingAddedByAutoTest(resourceIds);
            dbHelper.deleteFromSiteResourceTable(resourceIds);
            dbHelper.deleteFromSiteResourceSyncTable(resourceIds);
            dbHelper.deleteFromRotaEntryTable(resourceIds);
            dbHelper.deleteResourceWorkingHoursAddedByAutoTest(resourceIds);

            List<Integer> resourceAssignmentIdList = dbHelperResources.getResourceAssignmentIdsCreatedByAutoTest(resourceIds);
            String resourceAssignmentIds = null;
            List<Integer> resourceAssignmentEventIdList = null;

            if (!resourceAssignmentIdList.isEmpty()) {
                resourceAssignmentIds = resourceAssignmentIdList.toString();
                resourceAssignmentIds = resourceAssignmentIds.substring(1, resourceAssignmentIds.length()-1);
                resourceAssignmentEventIdList = dbHelperResources.getResourceAssignmentEventIds(resourceAssignmentIds);
            }

            if (resourceAssignmentEventIdList != null) {
                String resourceAssignmentEventIds = resourceAssignmentEventIdList.toString();
                resourceAssignmentEventIds = resourceAssignmentEventIds.substring(1, resourceAssignmentEventIds.length()-1);
                dbHelper.deleteResourceAssignmentStatusChangeAddedByAutoTest(resourceAssignmentEventIds);
            }

            if (!resourceAssignmentIdList.isEmpty()) {
                resourceAssignmentIds = resourceAssignmentIdList.toString();
                resourceAssignmentIds = resourceAssignmentIds.substring(1, resourceAssignmentIds.length()-1);
                dbHelper.deleteResourceAssignmentEventsAddedByAutoTest(resourceAssignmentIds);
                dbHelper.deleteOriginatingResourceAssignmentsAddedByAutoTest(resourceAssignmentIds);
                dbHelper.deleteSiteVisitsAddedByAutoTest(resourceAssignmentIds);
                dbHelper.deleteAdditionalResourceRequirementAddedByAutoTest(resourceAssignmentIds);
                dbHelper.deleteFundingRequestAddedByAutoTest(resourceAssignmentIds);
            }
            dbHelper.deleteResourceAssignmentAddedByAutoTest(resourceIds);
            dbHelper.deleteCoveringResourceAvailabilityAddedByAutoTest(resourceIds);
            dbHelper.deleteResourceAvailabilityAddedByAutoTest(resourceIds);
            dbHelper.deleteResourcesAddedByAutoTest();
        }
    }


    private void deleteSite() {
        List<Integer> siteIdList = dbHelperSites.getSiteIdsCreatedByAutoTest();
        if (!siteIdList.isEmpty()) {
            String siteIds = siteIdList.toString();
            siteIds = siteIds.substring(1, siteIds.length()-1);
            dbHelperSites.deleteSiteLocationMappingsAddedByAutoTest(siteIds);
            dbHelperSites.deleteSitesOpeningHoursAddedByAutoTest(siteIds);
            dbHelperSites.deleteLockJobsForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteJobAnswerForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteJobContactForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteJobRepairResponseForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteJobTimelineEventForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteJobsForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteRotaEntryForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteOrgStructureForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteTeamMappingForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteAssetClassificationContractorMappingForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteAssetClassificationForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteResourceCallerForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteResourceEmailAddressForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteResourceNotificationMethodForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteResourcePhoneNumberForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteTeamUserMappingForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteApplicationUserForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteResourceWorkingHoursForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteResourceSyncForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteResourceForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deletePhoneNumberForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteAdditionalInfoForSiteAddedByAutoTest(siteIds);
            dbHelperSites.deleteAllSitesAddedByAutoTest();
        }
    }

    private void deleteAsset() {
        List<Integer> assetIdList = dbHelper.getAssetIdsCreatedByAutoTest();
        if (!assetIdList.isEmpty()) {
            String assetIds = assetIdList.toString();
            assetIds = assetIds.substring(1, assetIds.length()-1);
            dbHelper.deleteFromAssetNoteTable(assetIds);
            dbHelper.deleteFromAssetTimelineEventTable(assetIds);
            dbHelper.deleteAllFromAssetTable();
        }
    }

    private void resetResourceAvailability() {
        if (testData.getString("absentResourceId") != null) {
            logger.debug("Deleting absence for resource created by auto test.");
            dbHelperResources.deleteAbsences(testData.getInt("absentResourceId"));
        }
    }

    private void cleanupAdmin() {
        dbHelper.deleteAllFromSunTCode5Table();
        dbHelper.deleteAllFromSunTCode6Table();
        dbHelper.deleteAllFromTCodeSuppliersTable();
        deleteResourceProfile();
        deleteUser();
        deleteUserProfile();
        deleteResource();
        deleteSite();
        deleteAsset();
        dbHelper.deleteAllQuestionOptionAddedByAutoTest();
        dbHelper.deleteAllQuestionAddedByAutoTest();
        dbHelper.deleteAllRulesEngineLookUpConditionAddedByAutoTest();
        dbHelper.deleteAllRulesAddedByAutoTest();
    }

    @After
    public void teardown() throws Exception {
        cleanupAdmin();

        saveTestData();

        outputScenarioFailReport();

        cleanupTestData();

        resetResourceAvailability();

        resetSystemToggles();

        resetActivatedMonitor();

        resetCoreProductValues();

        resetAutomationLocks();

        resetInvoiceLineTypes();

        resetPassword();

        if (!runtimeState.scenario.getSourceTagNames().contains("@maintenance")) {
            outputHelper.takeScreenshots();
        }

        logger.debug("******** Completed Scenario: " + runtimeState.scenario.getName() + " ********");

        loginLogoutHelper.logout();

        if (runtimeState.scenario.getSourceTagNames().contains("@video")) {
            recorder.stop();
        }

    }

    private void cleanupTestAutomationSite() {
        // over time the RotaEntry table will build up excessive rows for the test resource - so remove old rows
        if (testData.getInt("cityTechResourceId") != null) {
            dbHelperResources.cleaupRotaEntries(testData.getInt("siteId"), testData.getInt("cityTechResourceId"));
        }
    }

}
