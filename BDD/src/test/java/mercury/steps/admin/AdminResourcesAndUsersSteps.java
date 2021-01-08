package mercury.steps.admin;

import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.applicationinsights.web.dependencies.apachecommons.lang3.text.WordUtils;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.api.models.job.Job;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalRetrieval.JobDetails;
import mercury.database.dao.ApplicationUserDao;
import mercury.database.dao.CallerDetailsDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.CallerDetails;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.QuoteCreationHelper;
import mercury.helpers.ResourceHelper;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperUsers;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.LoginPage;
import mercury.pageobject.web.admin.AdminResourcesAndUsersPage;
import mercury.pageobject.web.portal.invoices.InvoiceDetailsPage;
import mercury.pageobject.web.portal.invoices.OrdersAwaitingInvoicePage;
import mercury.pageobject.web.portal.invoices.PortalOrdersInvoicePage;
import mercury.pageobject.web.portal.invoices.UploadInvoiceDocumentsPage;
import mercury.pageobject.web.portal.jobs.JobDetailsPage;
import mercury.pageobject.web.portal.jobs.PortalJobsForSitePage;
import mercury.pageobject.web.portal.quotes.CreateQuotePage;
import mercury.pageobject.web.portal.quotes.JobsAwaitingQuotePage;
import mercury.pageobject.web.portal.quotes.QuoteQueryPage;
import mercury.pageobject.web.portal.quotes.QuotesWithQueryPendingPage;
import mercury.pageobject.web.portal.quotes.RegisterQuotePage;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.LoginSteps;
import mercury.steps.helpdesk.HelpdeskHomePageSteps;
import mercury.steps.helpdesk.monitors.TileSteps;
import mercury.steps.portal.MenuSteps;
import mercury.steps.portal.invoices.PortalOrdersAwaitingInvoiceSteps;
import mercury.steps.portal.quotes.PortalQuoteSteps;

public class AdminResourcesAndUsersSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private ApplicationUserDao applicationUserDao;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperUsers dbHelperUsers;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private QuoteCreationHelper quoteCreationHelper;
    @Autowired private ApiHelperResources apiHelperResources;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private ResourceHelper resourceHelper;
    @Autowired private CallerDetailsDao callerDetailsDao;
    @Autowired private MenuSteps menuSteps;
    @Autowired private LoginSteps loginSteps;
    @Autowired private HelpdeskHomePageSteps helpdeskHomePageSteps;
    @Autowired private TileSteps tileSteps;
    @Autowired private PortalQuoteSteps portalQuoteSteps;
    @Autowired private PortalOrdersAwaitingInvoiceSteps portalOrdersAwaitingInvoiceSteps;
    @Autowired private CommonSteps commonSteps;
    @Autowired private PropertyHelper propertyHelper;

    public void insertPayrollCode() {
        String code = "TEST:" + DataGenerator.GenerateRandomString(6,10,0,0,6,0);
        testData.put("payrollCode", code);
        String name = DataGenerator.generateRandomName();
        name = name.replaceAll("'", "");
        testData.put("payrollName", name);
        if (LOCALE.equals("en-US")) {
            dbHelper.deleteAllFromSunTCode5Table();
            dbHelperResources.insertIntoPayrollTable5(code, name);
        } else {
            dbHelper.deleteAllFromSunTCode6Table();
            dbHelperResources.insertIntoPayrollTable6(code, name);
        }
        runtimeState.scenario.write("Payroll Code: " + code);
        runtimeState.scenario.write("Payroll Name: " + name);
    }

    public void insertSupplierCode() {
        String code = "TEST:" + DataGenerator.GenerateRandomString(6,10,0,0,6,0);
        testData.put("supplierCode", code);
        String name = DataGenerator.generateRandomName();
        name = name.replaceAll("'", "");
        testData.put("supplierName", name);
        dbHelperResources.insertIntoTCodeSuppliersTable(code, name);
        runtimeState.scenario.write("Supplier Code: " + code);
        runtimeState.scenario.write("Supplier Name: " + name);
    }

    @Given("^a job is logged and assigned to a \"([^\"]*)\"$")
    public void job_is_logged_and_assigned_to_resource(String resourceType) throws Throwable {
        Map<String, Object> dbData = dbHelperResources.getUserToImpersonate(resourceType);
        testData.put("username", dbData.get("UserName"));
        testData.put("resourceId", dbData.get("ResourceId"));
        testData.put("assignToResourceId",  dbData.get("ResourceId"));
        testData.put("resourceName", dbData.get("ResourceName"));

        Job job = null;
        if (resourceType.equalsIgnoreCase("Landlord")) {
            job = jobCreationHelper.createJobInStatus("Logged");
            testData.put("jobReference", job.getJobReference());
        }

        runtimeState.scenario.write(String.format("Job reference %d assigned to '%s'", job.getJobReference(), dbData.get("ResourceName")));
    }

    @When("^the user is impersonated$")
    public void the_user_is_impersonated() throws Throwable {
        String userName = testData.getString("username");
        runtimeState.scenario.write("Impersonating username: " + userName);
        runtimeState.adminResourcesAndUsersPage.selectImpersonateUser(userName);
        outputHelper.takeScreenshots();
        runtimeState.adminResourcesAndUsersPage.confirmImpersonateUser();
        outputHelper.takeScreenshots();
    }

    public void the_user_is_impersonated(String username) throws Throwable {
        runtimeState.adminResourcesAndUsersPage.selectImpersonateUser(username);
        outputHelper.takeScreenshots();
        runtimeState.adminResourcesAndUsersPage.confirmImpersonateUser();
        outputHelper.takeScreenshots();
    }

    @When("^a user with \"([^\"]*)\" role is impersonated$")
    public void a_user_is_impersonated(String role) throws Throwable {
        ApplicationUser applicationUser = applicationUserDao.getUserWithRole("Helpdesk", role);
        testData.put("impersonatedResourceName", normalize(applicationUser.getFirstName() + " " + applicationUser.getLastName()));
        testData.put("username", applicationUser.getUserName());
        runtimeState.scenario.write("Impersonated Resource Name: " + testData.getString("impersonatedResourceName"));

        runtimeState.adminHomePage = runtimeState.helpdeskNavBar.OpenAdminApp();
        outputHelper.takeScreenshots();
        runtimeState.adminHomePage.selectTile("Resources & Users");
        runtimeState.adminResourcesAndUsersPage = new AdminResourcesAndUsersPage(getWebDriver()).get();
        outputHelper.takeScreenshots();
        runtimeState.adminResourcesAndUsersPage.selectImpersonateUser(applicationUser.getUserName());
        outputHelper.takeScreenshots();
        runtimeState.adminResourcesAndUsersPage.confirmImpersonateUser();
    }

    @When("^a new \"([^\"]*)\" User is created$")
    public void a_new_user_is_created(String resourceType) throws Throwable {
        runtimeState.adminAddNewUserPage = runtimeState.adminResourcesAndUsersPage.addNewUser();

        String resource = null;

        if (resourceType.equalsIgnoreCase("Contractor")) {
            resource = dbHelperResources.getRandomContractorResource();
        } else if (resourceType.equalsIgnoreCase("Landlord")) {
            resource = dbHelperResources.getRandomLandlordResource();
        } else {
            resource = dbHelperResources.getRandomContractorOrLandlordResource();
        }
        runtimeState.adminAddNewUserPage.selectResource(resource);
        testData.put("resourceName", resource);

        String username = "TestAuto" + System.currentTimeMillis();
        runtimeState.adminAddNewUserPage.enterUsername(username);
        testData.put("username", username);

        String email = DataGenerator.generateRandomEmailAddress();
        runtimeState.adminAddNewUserPage.enterEmailAddress(email);
        testData.put("email", email);

        String phoneNumber = DataGenerator.generatePhoneNumber();
        runtimeState.adminAddNewUserPage.enterPhoneNumber(phoneNumber);
        testData.put("phoneNumber", phoneNumber);

        String userProfile = dbHelperResources.getRandomActiveUserProfile();
        runtimeState.adminAddNewUserPage.selectUserProfile(userProfile);
        testData.put("userProfile", userProfile);

        String[] isActive = {"Active", "Inactive"};
        Random random = new Random();
        int randomIndex = random.nextInt(isActive.length);
        if (randomIndex == 0) {
            runtimeState.adminAddNewUserPage.makeUserActive();
            testData.put("userStatus", "Active");
        } else {
            runtimeState.adminAddNewUserPage.makeUserInactive();
            testData.put("userStatus", "Inactive");
        }
        outputHelper.takeScreenshots();
        runtimeState.adminAddNewUserPage.createUser();
    }

    @When("^the User is edited$")
    public void user_is_edited() throws Throwable {
        if (testData.get("userStatus").equals("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectActiveTabUsers();
        } else {
            runtimeState.adminResourcesAndUsersPage.selectInactiveTabUsers();
        }

        runtimeState.adminResourcesAndUsersPage.searchForUserName(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.searchForUserUsername(testData.getString("username"));
        String resourceProfileName = dbHelperResources.getResourceProfileName(testData.getString("username"));
        runtimeState.adminResourcesAndUsersPage.searchForUserResourceProfile(resourceProfileName);
        outputHelper.takeScreenshots();
        runtimeState.adminEditUserPage = runtimeState.adminResourcesAndUsersPage.editUser(testData.getString("resourceName"));

        if (testData.get("userStatus").equals("Active")) {
            runtimeState.adminEditUserPage.makeUserInactive();
            testData.put("editedStatus", "Inactive");
        } else {
            runtimeState.adminEditUserPage.makeUserActive();
            testData.put("editedStatus", "Active");
        }

        outputHelper.takeScreenshots();
        runtimeState.adminEditUserPage.saveChanges();
    }

    @When("^a duplicate User is created$")
    public void duplicate_user_is_created() throws Throwable {
        runtimeState.adminAddNewUserPage = runtimeState.adminResourcesAndUsersPage.addNewUser();

        runtimeState.adminAddNewUserPage.selectResource(testData.getString("resourceName"));
        runtimeState.adminAddNewUserPage.enterUsername(testData.getString("username"));
        runtimeState.adminAddNewUserPage.enterEmailAddress(testData.getString("email"));
        runtimeState.adminAddNewUserPage.enterPhoneNumber(testData.getString("phoneNumber"));
        runtimeState.adminAddNewUserPage.selectUserProfile(testData.getString("userProfile"));

        if (testData.get("userStatus").equals("Active")) {
            runtimeState.adminAddNewUserPage.makeUserActive();
        } else {
            runtimeState.adminAddNewUserPage.makeUserInactive();
        }
        runtimeState.adminAddNewUserPage.createUser();
    }

    @When("^a new User Profile is created$")
    public void new_user_profile_is_created() throws Throwable {
        runtimeState.adminAddNewUserProfilePage = runtimeState.adminResourcesAndUsersPage.addNewUserProfile();

        String userProfileName = "TestAuto" + System.currentTimeMillis();
        runtimeState.adminAddNewUserProfilePage.enterUserProfileName(userProfileName);
        testData.put("userProfileName", userProfileName);

        runtimeState.adminAddNewUserProfilePage.selectDefaultPortal("Resource");
        Random randomGenerator = new Random();
        int randomNumnberOfPermissions = randomGenerator.nextInt(5) + 1;
        List<String> permissions = dbHelper.getRandomPermissions(randomNumnberOfPermissions);
        for (String permission : permissions) {
            runtimeState.scenario.write("Selecting permission: " + permission);
            runtimeState.adminAddNewUserProfilePage.selectPermission(permission);
        }
        int randomNumnberOfMonitorAreas = randomGenerator.nextInt(3) + 1;
        List<String> monitorAreas = dbHelper.getRandomMonitorAreas(randomNumnberOfMonitorAreas);
        for (String monitorArea : monitorAreas) {
            runtimeState.scenario.write("Selecting monitorArea: " + monitorArea);
            runtimeState.adminAddNewUserProfilePage.selectMonitorTile(monitorArea);
        }
        runtimeState.adminAddNewUserProfilePage.makeProfileActive();
        outputHelper.takeScreenshots();
        runtimeState.adminResourcesAndUsersPage = runtimeState.adminAddNewUserProfilePage.createProfile();
    }

    @When("^an active Resource is associated to this User Profile$")
    public void active_resource_is_associated_to_user_profile() throws Throwable {
        insertPayrollCode();

        runtimeState.adminMenuPage.selectAdminMenu("Resources");
        outputHelper.takeScreenshots();

        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        String resourceProfile = "Accounts Payable";
        runtimeState.adminAddNewResourcePage.selectResourceProfile(resourceProfile);
        testData.put("resourceProfile", resourceProfile);

        String resourceName = "TestAuto" + System.currentTimeMillis();
        resourceName = resourceName.replaceAll("'", "");
        runtimeState.adminAddNewResourcePage.enterResourceName(resourceName);
        testData.put("resourceName", resourceName);

        runtimeState.adminAddNewResourcePage.selectPayrollCodeDropdownOption(testData.getString("payrollCode") + " " + testData.getString("payrollName"));

        resourceHelper.answerResourceQuestions();

        String userName = "TestAuto" + System.currentTimeMillis();
        runtimeState.adminAddNewResourcePage.enterUsername(userName);
        testData.put("userName", userName);
        runtimeState.adminAddNewResourcePage.selectUserPermission(testData.getString("userProfileName"));

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Make resource active?", "Yes");
        outputHelper.takeScreenshots();
        runtimeState.adminAddNewResourcePage.completeResourceSetup();
    }

    @When("^a new \"([^\"]*)\" User Profile is created$")
    public void new_active_user_profile_is_created(String isActive) throws Throwable {
        runtimeState.adminAddNewUserProfilePage = runtimeState.adminResourcesAndUsersPage.addNewUserProfile();

        String userProfileName = "TestAuto" + System.currentTimeMillis();
        userProfileName = userProfileName.replaceAll("'", "");
        runtimeState.adminAddNewUserProfilePage.enterUserProfileName(userProfileName);
        testData.put("userProfileName", userProfileName);
        String portal = dbHelper.getRandomPortal();
        runtimeState.adminAddNewUserProfilePage.selectDefaultPortal(portal);
        Random randomGenerator = new Random();
        int randomNumnberOfPermissions = randomGenerator.nextInt(5) + 1;
        List<String> permissions = dbHelper.getRandomPermissions(randomNumnberOfPermissions);
        for (String permission : permissions) {
            runtimeState.scenario.write("Selecting permission: " + permission);
            runtimeState.adminAddNewUserProfilePage.selectPermission(permission);
        }
        int randomNumnberOfMonitorAreas = randomGenerator.nextInt(3) + 1;
        List<String> monitorAreas = dbHelper.getRandomMonitorAreas(randomNumnberOfMonitorAreas);
        for (String monitorArea : monitorAreas) {
            runtimeState.scenario.write("Selecting monitorArea: " + monitorArea);
            runtimeState.adminAddNewUserProfilePage.selectMonitorTile(monitorArea);
        }
        if(isActive.equals("Active")) {
            runtimeState.adminAddNewUserProfilePage.makeProfileActive();
        } else {
            runtimeState.adminAddNewUserProfilePage.makeProfileInactive();
        }
        outputHelper.takeScreenshots();
        runtimeState.adminResourcesAndUsersPage = runtimeState.adminAddNewUserProfilePage.createProfile();
    }

    @When("^the working hours are edited$")
    public void the_working_hours_are_edited() throws Throwable {

        //Add new shift for Saturday
        runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Saturday", "06:00");
        runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Saturday", "09:00");
        runtimeState.scenario.write("Working hours updated to: Saturday 06:00 - 09:00" );

        //Make Wednesday overnight shift that spans 2 days
        runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Wednesday", "08:00");
        runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Wednesday", "06:00");
        runtimeState.scenario.write("Working hours updated to: Wednesday 08:00 - 06:00" );

        //Delete Mondays shift
        runtimeState.adminAddNewResourcePage.deleteWorkingHoursStartTime("Monday");
        runtimeState.adminAddNewResourcePage.deleteWorkingHoursEndTime("Monday");
        runtimeState.scenario.write("Working hours deleted: Monday" );

        outputHelper.takeScreenshots();
        runtimeState.adminAddNewResourcePage.saveProgressOfApplication();
    }

    @When("^the Resource Profile working hours are edited$")
    public void the_resource_profile_working_hours_are_edited() throws Throwable {
        if (testData.get("resourceProfileStatus").equals("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectActiveTabResourceProfiles();
        } else {
            runtimeState.adminResourcesAndUsersPage.selectInactiveTabResourceProfiles();
        }

        String resourceProfile = testData.getString("resourceProfileAlias") + " ( "
                + testData.getString("resourceProfileName") + " )";
        runtimeState.adminResourcesAndUsersPage.searchForResourceProfile(resourceProfile);
        runtimeState.adminResourcesAndUsersPage.searchForResourceType(testData.getString("resourceType"));
        outputHelper.takeScreenshots();

        runtimeState.adminEditResourceProfilePage = runtimeState.adminResourcesAndUsersPage.editResourceProfile(resourceProfile);

        //Delete Monday and Saturday shifts
        runtimeState.adminEditResourceProfilePage.deleteWorkingHoursStartTime("Monday");
        runtimeState.adminEditResourceProfilePage.deleteWorkingHoursEndTime("Monday");
        runtimeState.scenario.write("Working hours deleted: Monday" );
        runtimeState.adminEditResourceProfilePage.deleteWorkingHoursStartTime("Saturday");
        runtimeState.adminEditResourceProfilePage.deleteWorkingHoursEndTime("Saturday");
        runtimeState.scenario.write("Working hours deleted: Saturday" );

        //Add new shift for Saturday
        runtimeState.adminEditResourceProfilePage.enterWorkingHoursStartTime("Saturday", "06:00");
        runtimeState.adminEditResourceProfilePage.enterWorkingHoursEndTime("Saturday", "09:00");
        runtimeState.scenario.write("Working hours updated to: Saturday 06:00 - 09:00" );

        //Make Wednesday overnight shift that spans 2 days
        runtimeState.adminEditResourceProfilePage.enterWorkingHoursStartTime("Wednesday", "08:00");
        runtimeState.adminEditResourceProfilePage.enterWorkingHoursEndTime("Wednesday", "06:00");
        runtimeState.scenario.write("Working hours updated to: Wednesday 08:00 - 06:00" );

        outputHelper.takeScreenshots();
    }

    @When("^the add new resource profile button is clicked$")
    public void add_new_resource_profile_is_clicked() throws Throwable {
        runtimeState.adminAddNewResourceProfilePage = runtimeState.adminResourcesAndUsersPage.addNewResourceProfile();
        outputHelper.takeScreenshots();
    }

    @When("^the Resource Profile is de-activated$")
    public void resource_profile_is_deactivated() throws Throwable {
        if (testData.get("resourceProfileStatus").equals("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectActiveTabResourceProfiles();
        } else {
            runtimeState.adminResourcesAndUsersPage.selectInactiveTabResourceProfiles();
        }

        String resourceProfile = testData.getString("resourceProfileAlias") + " ( "
                + testData.getString("resourceProfileName") + " )";
        runtimeState.adminResourcesAndUsersPage.searchForResourceProfile(resourceProfile);
        runtimeState.adminResourcesAndUsersPage.searchForResourceType(testData.getString("resourceType"));
        outputHelper.takeScreenshots();

        runtimeState.adminEditResourceProfilePage = runtimeState.adminResourcesAndUsersPage.editResourceProfile(resourceProfile);

        runtimeState.adminEditResourceProfilePage.deactivateResourceProfile();
    }

    @When("^a new \"([^\"]*)\" Resource Profile is added$")
    public void new_resource_profile_is_added(String resourceType) throws Throwable {
        runtimeState.adminAddNewResourceProfilePage = runtimeState.adminResourcesAndUsersPage.addNewResourceProfile();

        if (resourceType.equalsIgnoreCase("random")) {
            resourceType = dbHelperResources.getRandomResourceType();
            runtimeState.adminAddNewResourceProfilePage.selectResourceType(resourceType);
            testData.put("resourceType", resourceType);
        } else {
            runtimeState.adminAddNewResourceProfilePage.selectResourceType(resourceType);
            testData.put("resourceType", resourceType);
        }

        String resourceProfileName = "TestAuto" + System.currentTimeMillis();
        resourceProfileName = resourceProfileName.replaceAll("'", "");
        testData.put("resourceProfileName", resourceProfileName);
        String resourceProfileAlias = DataGenerator.generateRandomName();
        resourceProfileAlias = resourceProfileAlias.replaceAll("'", "");
        testData.put("resourceProfileAlias", resourceProfileAlias);
        runtimeState.adminAddNewResourceProfilePage.enterResourceProfileName(resourceProfileName);
        runtimeState.adminAddNewResourceProfilePage.enterResourceProfileAlias(resourceProfileAlias);

        resourceHelper.answerResourceProfileQuestions();

        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursStartTime("Monday", "09:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursEndTime("Monday", "16:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursStartTime("Tuesday", "09:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursEndTime("Tuesday", "16:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursStartTime("Wednesday", "09:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursEndTime("Wednesday", "16:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursStartTime("Thursday", "09:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursEndTime("Thursday", "16:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursStartTime("Friday", "09:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursEndTime("Friday", "16:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursStartTime("Saturday", "09:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursEndTime("Saturday", "16:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursStartTime("Sunday", "09:00");
        runtimeState.adminAddNewResourceProfilePage.enterWorkingHoursEndTime("Sunday", "16:00");

        if (resourceType.equalsIgnoreCase("City Resource")) {
            String rate = DataGenerator.GenerateRandomString(4, 4, 0, 0, 4, 0);
            if(testData.getBoolean("labourRatesByState")) {
                if (propertyHelper.getEnv().contains("USWM")) {
                    String state = dbHelper.getUsState();
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Out of Hours", 1, rate, state);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Standard", 2, rate, state);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Travel", 3, rate, state);

                } else if (propertyHelper.getEnv().contains("USAD")) {
                    String state = dbHelper.getUsState();
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Out of Hours", 1, rate, state);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Standard", 2, rate, state);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Weekend", 3, rate, state);

                } else if (propertyHelper.getEnv().contains("UKRB")) {
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Out of Hours", 1, rate, "United Kingdom");
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Standard", 2, rate, "United Kingdom");
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithState("Weekend", 3, rate, "United Kingdom");
                }
            } else {
                if (propertyHelper.getEnv().contains("USWM")) {
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Out of Hours", 1, rate);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Standard", 2, rate);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Travel", 3, rate);

                } else if (propertyHelper.getEnv().contains("USAD")) {
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Out of Hours", 1, rate);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Standard", 2, rate);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Weekend", 3, rate);

                } else if (propertyHelper.getEnv().contains("UKRB")) {
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Out of Hours", 1, rate);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Standard", 2, rate);
                    runtimeState.adminAddNewResourceProfilePage.enterLabourRateWithoutState("Weekend", 3, rate);
                }
            }
        }

        String[] isActive = {"Active", "Inactive"};
        Random random = new Random();
        int randomIndex = random.nextInt(isActive.length);
        if (randomIndex == 0) {
            runtimeState.adminAddNewResourceProfilePage.clickActiveYes();
            runtimeState.scenario.write("Profile is active");
            testData.put("resourceProfileStatus", "Active");
        } else {
            runtimeState.adminAddNewResourceProfilePage.clickActiveNo();
            runtimeState.scenario.write("Profile is inactive");
            testData.put("resourceProfileStatus", "Inactive");
        }
        outputHelper.takeScreenshots();
        if(testData.get("resourceProfileStatus").equals("Active")) {
            runtimeState.adminAddNewResourceProfilePage.createResourceProfile();
        } else {
            runtimeState.adminAddNewResourceProfilePage.saveProgress();
        }
    }

    @When("^the User Profile is de-activated$")
    public void the_user_profile_is_deactivated() throws Throwable {
        runtimeState.adminMenuPage.selectAdminMenu("User profiles");
        outputHelper.takeScreenshots();

        runtimeState.adminResourcesAndUsersPage.searchForUserProfile(testData.getString("userProfileName"));
        runtimeState.adminEditUserProfilePage = runtimeState.adminResourcesAndUsersPage.editUserProfile(testData.getString("userProfileName"));

        runtimeState.adminEditUserProfilePage.makeProfileInactive();
        outputHelper.takeScreenshots();
    }

    @When("^the profile is de-activated$")
    public void the_profile_is_deactivated() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.searchForUserProfile(testData.getString("userProfileName"));
        runtimeState.adminEditUserProfilePage = runtimeState.adminResourcesAndUsersPage.editUserProfile(testData.getString("userProfileName"));

        runtimeState.adminEditUserProfilePage.makeProfileInactive();
        outputHelper.takeScreenshots();
        runtimeState.adminEditUserProfilePage.saveProfile();
    }

    @When("^the profile is activated$")
    public void the_profile_is_activated() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.selectInactiveTabUserProfiles();
        runtimeState.adminResourcesAndUsersPage.searchForUserProfile(testData.getString("userProfileName"));
        runtimeState.adminEditUserProfilePage = runtimeState.adminResourcesAndUsersPage.editUserProfile(testData.getString("userProfileName"));

        runtimeState.adminEditUserProfilePage.makeProfileActive();
        outputHelper.takeScreenshots();
        runtimeState.adminEditUserProfilePage.saveProfile();
    }

    @When("^a new resource is added and the resource profile is selected$")
    public void resource_added_and_resource_profile_selected() throws Throwable {
        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        Map<String, Object> resourceProfileAndAlias = dbHelperResources.getRandomResourceProfileAliasWithWorkingHours();
        String resourceProfile = null;
        if (resourceProfileAndAlias.get("Name").equals(resourceProfileAndAlias.get("Alias"))) {
            resourceProfile = resourceProfileAndAlias.get("Alias").toString().trim();
        } else {
            resourceProfile = resourceProfileAndAlias.get("Alias").toString().trim() + " ( "
                    + resourceProfileAndAlias.get("Name").toString().trim() + " )";
        }
        runtimeState.adminAddNewResourcePage.selectResourceProfile(resourceProfile);
        testData.put("resourceProfile", resourceProfile);
        outputHelper.takeScreenshots();
    }

    @When("^a new resource is added with a site assigned$")
    public void resource_is_added_with_site_assigned() throws Throwable {
        insertPayrollCode();

        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        String resourceProfile = null;
        if (LOCALE.equals("en-GB")) {
            resourceProfile = "Refrigeration Technician";
        } else if(LOCALE.equals("en-US")) {
            resourceProfile = "RHVAC Technician";
        }

        runtimeState.adminAddNewResourcePage.selectResourceProfile(resourceProfile);
        testData.put("resourceProfile", resourceProfile);

        String resourceName = "TestAuto" + System.currentTimeMillis();
        resourceName = resourceName.replaceAll("'", "");
        runtimeState.adminAddNewResourcePage.enterResourceName(resourceName);
        testData.put("resourceName", resourceName);

        if (runtimeState.adminAddNewResourcePage.isPayrollDisplayed()) {
            runtimeState.adminAddNewResourcePage.selectPayrollCodeDropdownOption(testData.getString("payrollCode") + " " + testData.getString("payrollName"));
        }

        if (runtimeState.adminAddNewResourcePage.isHomeStoreDisplayed()) {
            String homeStore = dbHelperSites.getRandomSiteName();
            runtimeState.adminAddNewResourcePage.selectHomeStore(homeStore);
            testData.put("homeStore", homeStore);
        }

        if (runtimeState.adminAddNewResourcePage.isEmailTextBoxDisplayed()) {
            String email = DataGenerator.generateRandomEmailAddress();
            runtimeState.adminAddNewResourcePage.enterFirstEmail(email);
            testData.put("email", email);
        }

        resourceHelper.answerResourceQuestions();

        String siteName = dbHelperSites.getRandomSiteName();
        runtimeState.adminAddNewResourcePage.selectPermanentSite(siteName);
        testData.put("siteName", siteName);

        if (runtimeState.adminAddNewResourcePage.isReplaceBarDisplayed()) {
            runtimeState.adminAddNewResourcePage.clickReplaceBar();
        }

        if (runtimeState.adminAddNewResourcePage.isLabelDisplayed("Users") || runtimeState.adminAddNewResourcePage.isHeaderDisplayed("Users")) {
            String userName = "TestAuto" + System.currentTimeMillis();
            runtimeState.adminAddNewResourcePage.enterUsername(userName);
            testData.put("userName", userName);
            runtimeState.adminAddNewResourcePage.selectRandomUserPermission();
        }

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Make resource active?", "Yes");
        outputHelper.takeScreenshots();
        runtimeState.adminAddNewResourcePage.completeResourceSetup();
    }

    @When("^a new resource who can be assigned to a site is added$")
    public void resource_who_can_be_assigned_to_a_site_is_added() throws Throwable {
        insertPayrollCode();

        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        String resourceProfileForQuery = null;
        if (LOCALE.equals("en-GB")) {
            resourceProfileForQuery = "Refrigeration Technician";
        } else if(LOCALE.equals("en-US")) {
            resourceProfileForQuery = "RHVAC Technician";
        }

        Map<String, Object> resourceNameProfileAndAlias = dbHelperResources.getResourceAndResourceProfileAssignedToSite(resourceProfileForQuery);
        String resourceProfile = null;
        if (resourceNameProfileAndAlias.get("ResourceProfileName").equals(resourceNameProfileAndAlias.get("ResourceProfileAlias"))) {
            resourceProfile = resourceNameProfileAndAlias.get("ResourceProfileAlias").toString().trim();
        } else {
            resourceProfile = resourceNameProfileAndAlias.get("ResourceProfileAlias").toString().trim() + " ( "
                    + resourceNameProfileAndAlias.get("ResourceProfileName").toString().trim() + " )";
        }
        runtimeState.adminAddNewResourcePage.selectResourceProfile(resourceProfile);
        testData.put("resourceProfile", resourceProfile);
        testData.put("resourceProfileName", resourceNameProfileAndAlias.get("ResourceProfileName").toString());
        testData.put("resourceProfileAlias", resourceNameProfileAndAlias.get("ResourceProfileAlias").toString());
        testData.put("resourceName", resourceNameProfileAndAlias.get("ResourceName").toString());
        testData.put("siteName", resourceNameProfileAndAlias.get("SiteName").toString());
        outputHelper.takeScreenshots();

        String resourceName = "TestAuto" + System.currentTimeMillis();
        resourceName = resourceName.replaceAll("'", "");
        runtimeState.adminAddNewResourcePage.enterResourceName(resourceName);
        testData.put("resourceName", resourceName);

        if (runtimeState.adminAddNewResourcePage.isPayrollDisplayed()) {
            runtimeState.adminAddNewResourcePage.selectPayrollCodeDropdownOption(testData.getString("payrollCode") + " " + testData.getString("payrollName"));
        }

        if (runtimeState.adminAddNewResourcePage.isHomeStoreDisplayed()) {
            String homeStore = dbHelperSites.getRandomSiteName();
            runtimeState.adminAddNewResourcePage.selectHomeStore(homeStore);
            testData.put("homeStore", homeStore);
        }

        if (runtimeState.adminAddNewResourcePage.isEmailTextBoxDisplayed()) {
            String email = DataGenerator.generateRandomEmailAddress();
            runtimeState.adminAddNewResourcePage.enterFirstEmail(email);
            testData.put("email", email);
        }

        resourceHelper.answerResourceQuestions();

        if (runtimeState.adminAddNewResourcePage.isLabelDisplayed("Users") || runtimeState.adminAddNewResourcePage.isHeaderDisplayed("Users")) {
            String userName = "TestAuto" + System.currentTimeMillis();
            runtimeState.adminAddNewResourcePage.enterUsername(userName);
            testData.put("userName", userName);
            runtimeState.adminAddNewResourcePage.selectRandomUserPermission();
        }

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Make resource active?", "Yes");
        outputHelper.takeScreenshots();
        runtimeState.adminAddNewResourcePage.completeResourceSetup();
    }

    @When("^the resource is edited and a site which already has resource assigned is added$")
    public void resource_is_edited_and_site_which_has_resource_assigned_added() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.selectResourcesActiveTab();

        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        outputHelper.takeScreenshots();

        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));

        if (runtimeState.adminEditResourcePage.isSiteConfigurationDisplayed()) {
            runtimeState.adminEditResourcePage.selectSiteForConfiguration(testData.getString("siteName"));
        } else if (runtimeState.adminEditResourcePage.isPermanentSiteDisplayed()) {
            runtimeState.adminEditResourcePage.selectPermanentSiteForConfiguration(testData.getString("siteName"));
        }
    }

    @When("^the resource is edited and another site is added$")
    public void resource_is_edited_and_another_site_is_added() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.selectResourcesActiveTab();

        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        outputHelper.takeScreenshots();

        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));

        String siteName = dbHelperSites.getRandomSiteName();
        if (runtimeState.adminEditResourcePage.isSiteConfigurationDisplayed()) {
            runtimeState.adminEditResourcePage.selectSiteForConfiguration(siteName);
        } else if (runtimeState.adminEditResourcePage.isPermanentSiteDisplayed()) {
            runtimeState.adminEditResourcePage.selectPermanentSiteForConfiguration(siteName);
        }
        testData.put("secondSiteName", siteName);
    }

    @When("^the resource is edited$")
    public void resource_is_edited() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.selectResourcesActiveTab();

        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        outputHelper.takeScreenshots();

        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));
    }

    @When("^a \"([^\"]*)\" exception is added$")
    public void exception_is_added(String calloutType) throws Throwable {
        runtimeState.adminConfigureExceptionModal = runtimeState.adminEditResourcePage.addCalloutException();
        runtimeState.adminConfigureExceptionModal.selectExceptionDropdownOption(testData.getString("firstSiteName"));
        runtimeState.adminConfigureExceptionModal.selectCalloutType(calloutType);
        outputHelper.takeScreenshots();
        runtimeState.adminConfigureExceptionModal.clickButton("Add");
    }

    @When("^the site is removed$")
    public void site_is_removed() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.selectResourcesActiveTab();

        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        outputHelper.takeScreenshots();

        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));

        runtimeState.adminEditResourcePage.removeSite(testData.getString("siteName"));
    }

    @When("^an Active Contractor Resource is created with Geo-Fencing \"([^\"]*)\"$")
    public void active_contractor_resource_is_created_with_geo_fencing(String geoFencing) throws Throwable {
        insertSupplierCode();

        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        runtimeState.adminAddNewResourcePage.selectResourceProfile("Contractor");
        testData.put("resourceProfile", "Contractor");

        String resourceName = "TestAuto" + System.currentTimeMillis();
        resourceName = resourceName.replaceAll("'", "");
        runtimeState.adminAddNewResourcePage.enterResourceName(resourceName);
        testData.put("resourceName", resourceName);

        runtimeState.adminAddNewResourcePage.selectSunSupplierTcodeDropdownOption(testData.getString("supplierCode"));

        String email = DataGenerator.generateRandomEmailAddress();
        runtimeState.adminAddNewResourcePage.enterFirstEmail(email);
        testData.put("email", email);

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is this resource always chargeable?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Can this resource be auto-assigned to reactive jobs?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is a reference required?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Can this resource attach assets?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is this resource a VIP?", "No");
        if (geoFencing.equalsIgnoreCase("Enabled")) {
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Does this Contractor use Technicians?", "Yes");
        } else {
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Does this Contractor use Technicians?", "No");
        }
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Invoicing and credit Notes active?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Will this resource have Purchase Order Imports?", "No");

        String siteName = dbHelperSites.getRandomSiteName();
        runtimeState.adminAddNewResourcePage.selectSiteForConfiguration(siteName);
        testData.put("firstSiteName", siteName);
        runtimeState.adminEditSiteConfigurationModal = runtimeState.adminAddNewResourcePage.editSiteForConfiguration();
        runtimeState.adminEditSiteConfigurationModal.selectClassification();
        if (runtimeState.adminEditSiteConfigurationModal.isPriorityDropdownDisplayedForSelectedClassification()) {
            runtimeState.adminEditSiteConfigurationModal.selectClassificationPriority("Medium");
        }
        outputHelper.takeScreenshots();
        runtimeState.adminEditSiteConfigurationModal.clickButton("Update");

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Make resource active?", "Yes");
        testData.put("statusBeforeEditing", "Active");
        outputHelper.takeScreenshots();
        runtimeState.adminAddNewResourcePage.completeResourceSetup();
    }

    @When("^a Landlord resource is added$")
    public void landlord_resource_is_added() throws Throwable {
        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        runtimeState.adminAddNewResourcePage.selectResourceProfile("Landlord");
        testData.put("resourceProfile", "Landlord");

        String resourceName = "TestAuto" + System.currentTimeMillis();
        resourceName = resourceName.replaceAll("'", "");
        runtimeState.adminAddNewResourcePage.enterResourceName(resourceName);
        testData.put("resourceName", resourceName);

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Can this resource attach assets?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is this resource a VIP?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Does this resource receive update e-mails?", "No");

        String siteName = dbHelperSites.getRandomSiteName();
        runtimeState.adminAddNewResourcePage.selectSiteForConfiguration(siteName);
        testData.put("firstSiteName", siteName);
        runtimeState.adminAddNewResourcePage.clickResourceRequiresSiteVisitNotificationCheckbox();

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Make resource active?", "Yes");
        testData.put("statusBeforeEditing", "Active");
        outputHelper.takeScreenshots();
        runtimeState.adminAddNewResourcePage.completeResourceSetup();
    }

    @When("^a job is logged against this resource with ETA provided$")
    public void job_is_logged_against_resource_with_ETA_provided() throws Throwable {
        int siteId = dbHelperSites.getSiteId(testData.getString("firstSiteName"));
        Map<String, Object> faultDetails = dbHelper.getFaultDetailsForSite(siteId);
        int faultTypeId = (int) faultDetails.get("FaultTypeId");
        int faultPriorityId = (int) faultDetails.get("FaultPriorityId");
        int assetClassificationId = (int) faultDetails.get("AssetClassificationId");
        CallerDetails cd = callerDetailsDao.getRandomCallerForSite(dbHelperSites.getSiteName(siteId));
        int callerId = cd.getId();
        Integer locationId = dbHelper.getRandomLocation(siteId);
        int resourceId = dbHelperResources.getResourceId(testData.getString("resourceName"));

        ApplicationUser applicationUser = jobCreationHelper.getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        Job job = apiHelperJobs.createNewJobForResource(1, siteId, faultTypeId, faultPriorityId, "Test Automation", callerId, assetClassificationId, locationId, resourceId, null, createdBy);
        runtimeState.scenario.write("jobReference: " + job.getJobReference());
        testData.put("jobReference", job.getJobReference());

        String jobStatus = "Allocated / ETA Provided";
        int assignedResourceId = dbHelperResources.getAssignedResources(job.getJobReference()).get(0);
        String resourceType = dbHelperResources.getResourceType(assignedResourceId);
        testData.put("resourceTypeName", resourceType);
        jobCreationHelper.acceptJob(job.getJobReference(), job.getId(), resourceType, jobStatus);
    }

    @When("^a job is assigned to the city resource$")
    public void job_is_assigned_to_the_city_resource() throws Throwable {
        int siteId = dbHelperSites.getSiteId(testData.getString("siteName"));
        Map<String, Object> faultDetails = dbHelper.getFaultDetailsForSite(siteId);
        int faultTypeId = (int) faultDetails.get("FaultTypeId");
        int faultPriorityId = (int) faultDetails.get("ResponsePriorityId");
        int assetClassificationId = (int) faultDetails.get("AssetClassificationId");
        CallerDetails cd = callerDetailsDao.getRandomCallerForSite(dbHelperSites.getSiteName(siteId));
        int callerId = cd.getId();
        Integer locationId = dbHelper.getRandomLocation(siteId);
        int resourceId = dbHelperResources.getResourceId(testData.getString("resourceName"));

        ApplicationUser applicationUser = jobCreationHelper.getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        Job job = apiHelperJobs.createNewJobForResource(1, siteId, faultTypeId, faultPriorityId, "Test Automation", callerId, assetClassificationId, locationId, resourceId, null, createdBy);
        runtimeState.scenario.write("jobReference: " + job.getJobReference());
        testData.put("jobReference", job.getJobReference());

        String jobStatus = "Allocated / ETA Provided";
        int assignedResourceId = dbHelperResources.getAssignedResources(job.getJobReference()).get(0);
        String resourceType = dbHelperResources.getResourceType(assignedResourceId);
        testData.put("resourceTypeName", resourceType);
        testData.put("resourceId", resourceId);
        jobCreationHelper.acceptJob(job.getJobReference(), job.getId(), resourceType, jobStatus);
    }

    @When("^a new resource who works 5 days or less is added and the resource profile is selected$")
    public void resource_works_5_days_added_and_resource_profile_selected() throws Throwable {
        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        Map<String, Object> resourceProfileAndAlias = dbHelperResources.getRandomResourceProfileAliasWhoWorksFiveDaysOrLess();
        String resourceProfile = null;
        if (resourceProfileAndAlias.get("Name").equals(resourceProfileAndAlias.get("Alias"))) {
            resourceProfile = resourceProfileAndAlias.get("Alias").toString().trim();
        } else {
            resourceProfile = resourceProfileAndAlias.get("Alias").toString().trim() + " ( "
                    + resourceProfileAndAlias.get("Name").toString().trim() + " )";
        }
        runtimeState.adminAddNewResourcePage.selectResourceProfile(resourceProfile);
        testData.put("resourceProfile", resourceProfile);

        String resourceName = "TestAuto" + System.currentTimeMillis();
        resourceName = resourceName.replaceAll("'", "");
        runtimeState.adminAddNewResourcePage.enterResourceName(resourceName);
        testData.put("resourceName", resourceName);
        outputHelper.takeScreenshots();
    }

    @When("^an Active Contractor Resource is created$")
    public void active_contractor_resource_is_created() throws Throwable {
        insertSupplierCode();

        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        runtimeState.adminAddNewResourcePage.selectResourceProfile("Contractor");
        testData.put("resourceProfile", "Contractor");

        String resourceName = "TestAuto" + System.currentTimeMillis();
        resourceName = resourceName.replaceAll("'", "");
        runtimeState.adminAddNewResourcePage.enterResourceName(resourceName);
        testData.put("resourceName", resourceName);
        runtimeState.scenario.write("Resource Name: " + resourceName);

        runtimeState.adminAddNewResourcePage.selectSunSupplierTcodeDropdownOption(testData.getString("supplierCode"));

        String email = DataGenerator.generateRandomEmailAddress();
        runtimeState.adminAddNewResourcePage.enterFirstEmail(email);
        testData.put("email", email);

        String[] phoneType = {"Mobile", "Landline", "Fax", "Other"};
        Random random = new Random();
        int randomIndex;
        runtimeState.adminAddNewResourcePage.addPhoneNumber();
        String phoneNumber = DataGenerator.generatePhoneNumber();
        String notes = DataGenerator.generateRandomSentence();
        randomIndex = random.nextInt(phoneType.length);
        runtimeState.adminAddNewResourcePage.addFirstPhoneNumber(phoneNumber, notes, phoneType[randomIndex]);

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is this resource always chargeable?", "Yes");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Can this resource be auto-assigned to reactive jobs?", "Yes");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is a reference required?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Can this resource attach assets?", "Yes");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is this resource a VIP?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Does this Contractor use Technicians?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Invoicing and credit Notes active?", "No");
        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Will this resource have Purchase Order Imports?", "No");

        String siteName = dbHelperSites.getRandomSiteName();
        runtimeState.adminAddNewResourcePage.selectSiteForConfiguration(siteName);
        testData.put("firstSiteName", siteName);
        runtimeState.adminEditSiteConfigurationModal = runtimeState.adminAddNewResourcePage.editSiteForConfiguration();
        runtimeState.adminEditSiteConfigurationModal.selectClassification();
        if (runtimeState.adminEditSiteConfigurationModal.isPriorityDropdownDisplayedForSelectedClassification()) {
            runtimeState.adminEditSiteConfigurationModal.selectClassificationPriority("Medium");
        }
        outputHelper.takeScreenshots();
        runtimeState.adminEditSiteConfigurationModal.clickButton("Update");

        runtimeState.adminAddNewResourcePage.answerResourceQuestions("Make resource active?", "Yes");
        testData.put("statusBeforeEditing", "Active");
        outputHelper.takeScreenshots();
        runtimeState.adminAddNewResourcePage.completeResourceSetup();
    }

    @When("^a Job is assigned to the resource$")
    public void job_is_assigned_to_the_resource() throws Throwable {
        String resourceName = testData.getString("resourceName");
        int resourceId = dbHelperResources.getResourceId(resourceName);
        Job job = jobCreationHelper.createJobInStatus("Logged / Removed");
        testData.put("resourceId", resourceId);
        testData.put("resourceName", resourceName);
        apiHelperResources.addAdditionalResource_resourceId(job.getId(), resourceId);
    }

    @When("^the resource is removed from the job$")
    public void resource_is_removed_from_the_job() throws Throwable {
        int resourceAssignmentId = dbHelperResources.getResourceAssignmentId(testData.getInt("resourceId"));
        apiHelperResources.removeResource(resourceAssignmentId);
        apiHelperResources.removeResourceAdvise(resourceAssignmentId, "TEST.AUTOMATION");
    }

    @When("^the City Resource is de-activated$")
    public void city_resource_is_deactivated() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));

        runtimeState.adminEditResourcePage.changeResourceStatus("No");
        permanent_site_allocations_warning_message_is_displayed();
        confirmation_message_is_displayed();
        runtimeState.adminEditResourcePage.deactivateResourceConfirmation();
        outputHelper.takeScreenshots();
        runtimeState.adminEditResourcePage.deactivateResource();
    }

    @When("^the Resource is de-activated$")
    public void resource_is_deactivated() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));

        runtimeState.adminEditResourcePage.changeResourceStatus("No");
        confirmation_message_is_displayed();
        runtimeState.adminEditResourcePage.deactivateResourceConfirmation();

        outputHelper.takeScreenshots();
        runtimeState.adminEditResourcePage.deactivateResource();
    }

    @When("^the \"([^\"]*)\" resource is edited$")
    public void resource_is_edited(String status) throws Throwable {
        if (status.equalsIgnoreCase("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectResourcesActiveTab();
        } else if(status.equalsIgnoreCase("Inactive")) {
            runtimeState.adminResourcesAndUsersPage.selectResourcesInactiveTab();
        }

        testData.put("statusBeforeEditing", status);
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        outputHelper.takeScreenshots();

        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));

        if (status.equalsIgnoreCase("Active")) {
            runtimeState.adminEditResourcePage.changeResourceStatus("No");
            runtimeState.adminEditResourcePage.deactivateResourceConfirmation();
            outputHelper.takeScreenshots();
            runtimeState.adminEditResourcePage.deactivateResource();
        } else {
            runtimeState.adminEditResourcePage.changeResourceStatus("Yes");
            outputHelper.takeScreenshots();
            runtimeState.adminEditResourcePage.saveChanges();
        }
    }

    @When("^a \"([^\"]*)\" resource is added$")
    public void resource_is_added(String status) throws Throwable {
        insertPayrollCode();
        insertSupplierCode();

        runtimeState.adminAddNewResourcePage = runtimeState.adminResourcesAndUsersPage.clickAddNewResource();

        Map<String, Object> resourceProfileAndAlias = dbHelperResources.getRandomResourceProfileAlias();
        String resourceProfile = null;
        if (resourceProfileAndAlias.get("Name").equals(resourceProfileAndAlias.get("Alias"))) {
            resourceProfile = resourceProfileAndAlias.get("Alias").toString().trim();
        } else {
            resourceProfile = resourceProfileAndAlias.get("Alias").toString().trim() + " ( "
                    + resourceProfileAndAlias.get("Name").toString().trim() + " )";
        }
        runtimeState.adminAddNewResourcePage.selectResourceProfile(resourceProfile);
        testData.put("resourceProfile", resourceProfile);

        String resourceName = "TestAuto" + System.currentTimeMillis();
        resourceName = resourceName.replaceAll("'", "");
        runtimeState.adminAddNewResourcePage.enterResourceName(resourceName);
        testData.put("resourceName", resourceName);

        if (runtimeState.adminAddNewResourcePage.isPayrollDisplayed()) {
            runtimeState.adminAddNewResourcePage.selectPayrollCodeDropdownOption(testData.getString("payrollCode") + " " + testData.getString("payrollName"));
        }

        if (runtimeState.adminAddNewResourcePage.isSupplierTCodeDisplayed()) {
            runtimeState.adminAddNewResourcePage.selectSunSupplierTcodeDropdownOption(testData.getString("supplierCode"));
        }

        if (runtimeState.adminAddNewResourcePage.isHomeStoreDisplayed()) {
            String homeStore = dbHelperSites.getRandomSiteName();
            runtimeState.adminAddNewResourcePage.selectHomeStore(normalize(homeStore));
            testData.put("homeStore", homeStore);
        }

        if (!runtimeState.adminAddNewResourcePage.isEmailTextBoxDisplayed()) {
            runtimeState.adminAddNewResourcePage.addEmailAddress();
        }
        String firstEmail = DataGenerator.generateRandomEmailAddress();
        runtimeState.adminAddNewResourcePage.enterFirstEmail(firstEmail);
        testData.put("firstEmail", firstEmail);
        runtimeState.adminAddNewResourcePage.addEmailAddress();
        String secondEmail = DataGenerator.generateRandomEmailAddress();
        runtimeState.adminAddNewResourcePage.enterSecondEmail(secondEmail);
        testData.put("secondEmail", secondEmail);
        runtimeState.adminAddNewResourcePage.makeSecondEmailPrimaryEmail();

        String[] phoneType = {"Mobile", "Landline", "Fax", "Other"};
        Random random = new Random();
        int randomIndex;
        runtimeState.adminAddNewResourcePage.addPhoneNumber();
        String firstPhoneNumber = DataGenerator.generatePhoneNumber();
        String notes = DataGenerator.generateRandomSentence();
        randomIndex = random.nextInt(phoneType.length);
        runtimeState.adminAddNewResourcePage.addFirstPhoneNumber(firstPhoneNumber, notes, phoneType[randomIndex]);
        runtimeState.adminAddNewResourcePage.addPhoneNumber();
        String secondPhoneNumber = DataGenerator.generatePhoneNumber();
        randomIndex = random.nextInt(phoneType.length);
        runtimeState.adminAddNewResourcePage.addSecondPhoneNumber(secondPhoneNumber, notes, phoneType[randomIndex]);
        runtimeState.adminAddNewResourcePage.makeSecondPhoneNumberPrimaryPhone();

        resourceHelper.answerResourceQuestions();

        resourceHelper.enterResourceWorkingHours();

        if (runtimeState.adminAddNewResourcePage.isLabelDisplayed("Users") || runtimeState.adminAddNewResourcePage.isHeaderDisplayed("Users")) {
            String userName = "TestAuto" + System.currentTimeMillis();
            runtimeState.adminAddNewResourcePage.enterUsername(userName);
            testData.put("userName", userName);
            runtimeState.adminAddNewResourcePage.selectRandomUserPermission();
        }

        testData.put("resourceStatus", status);

        if (status.equalsIgnoreCase("Active")) {
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Make resource active?", "Yes");
            outputHelper.takeScreenshots();
            runtimeState.adminAddNewResourcePage.completeResourceSetup();
        } else if(status.equalsIgnoreCase("Inactive")) {
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Make resource active?", "No");
            outputHelper.takeScreenshots();
            runtimeState.adminAddNewResourcePage.completeResourceSetup();
        } else if(status.equalsIgnoreCase("Incomplete")) {
            outputHelper.takeScreenshots();
            runtimeState.adminAddNewResourcePage.saveProgressOfApplication();
        }
    }

    @And("^the permanent site allocations warning message is displayed$")
    public void permanent_site_allocations_warning_message_is_displayed() throws Throwable {
        String expectedMessage = "Permanent site allocations will be removed.";
        String actualMessage = runtimeState.adminEditResourcePage.getPermSiteAllocationsMessage();
        runtimeState.scenario.write("Asserting Permanent Site Allocations warning message: " + actualMessage);
        assertEquals("Expected: " + expectedMessage + ", but got: " + actualMessage, expectedMessage, actualMessage);
    }

    @And("^the deactivate resource confirmation message is displayed$")
    public void confirmation_message_is_displayed() throws Throwable {
        String expectedMessage = "Tick to confirm this resource has been removed from all active jobs";
        String actualMessage = runtimeState.adminEditResourcePage.getConfirmationMessage();
        runtimeState.scenario.write("Asserting Confirmation message: " + actualMessage);
        assertEquals("Expected: " + expectedMessage + ", but got: " + actualMessage, expectedMessage, actualMessage);
    }

    @ContinueNextStepsOnException
    @Then("^the classifications are displayed as expected$")
    public void classifications_displayed_as_expected() throws Throwable {
        assertFalse("Save button is enabled", runtimeState.adminEditResourcePage.isSaveEnabled());

        runtimeState.adminEditResourcePage.editSiteConfig(testData.getString("secondSiteName"));

        List<String> siteClassifications = runtimeState.adminEditSiteConfigurationModal.getSiteClassifications();
        assertNotNull("There are no Site Classifications displayed", siteClassifications);

        runtimeState.adminEditSiteConfigurationModal.copyFromPreviousClassification(testData.getString("firstSiteName"));
        assertTrue("Classification has not been selected", runtimeState.adminEditSiteConfigurationModal.isClassificationSelected());

        assertTrue("Filter box is not displayed", runtimeState.adminEditSiteConfigurationModal.isFilterBoxDisplayed());
        assertTrue("Only show selected filter is not displayed", runtimeState.adminEditSiteConfigurationModal.isOnlyShowSelectedFilterDisplayed());

        resourceHelper.deleteContractorResource();
    }

    @ContinueNextStepsOnException
    @Then("^a rota entry is generated$")
    public void rota_entry_is_generated() throws Throwable {
        int resourceId = dbHelperResources.getResourceId(testData.getString("resourceName"));
        int resourceProfileId = dbHelperResources.getResourceProfileId(resourceId);
        int rotaEntryTypeId = 1; //1 for Permanent Site

        Map<String, Object> rotaEntry = dbHelperResources.getRotaEntryForResource(resourceId, resourceProfileId, rotaEntryTypeId);
        assertNotNull("Rota Entry not generated for Resource", rotaEntry);

        resourceHelper.deleteCityResourceWithSite();
        resourceHelper.deletePayrollCode();
    }

    @ContinueNextStepsOnException
    @Then("^the warning message is displayed$")
    public void warning_message_is_displayed() throws Throwable {
        outputHelper.takeScreenshots();
        assertTrue("Warning message is not displayed", runtimeState.adminEditResourcePage.isRemoveSiteAlertDisplayed());

        resourceHelper.deleteCityResourceWithSite();
        resourceHelper.deletePayrollCode();
    }

    @ContinueNextStepsOnException
    @Then("^a new rate is added for Site with \"([^\"]*)\" type$")
    public void new_rate_is_added(String calloutType) throws Throwable {

        runtimeState.scenario.write("Asserting that correct Site has been added");
        String site = runtimeState.adminEditResourcePage.getExceptionSite();
        site = StringUtils.substringAfter(site, "SITE LEVEL").trim();
        assertEquals("Expected: " + normalize(testData.getString("firstSiteName")) + " but was: " + site,
                normalize(testData.getString("firstSiteName")), site);

        runtimeState.scenario.write("Asserting that the input box is displayed for the correct callout rate");
        switch (calloutType) {
        case "Standard":
            assertFalse("Standard rate box is not displayed", runtimeState.adminEditResourcePage.isStandardRateBoxDisplayed());
            assertTrue("Recall rate box is displayed", runtimeState.adminEditResourcePage.isRecallRateBoxDisplayed());
            assertTrue("Out of Hours rate box is displayed", runtimeState.adminEditResourcePage.isOutOfHoursRateBoxDisplayed());
            assertTrue("Subsequent rate box is displayed", runtimeState.adminEditResourcePage.isSubsequentRateBoxDisplayed());
            break;
        case "Recall":
            assertTrue("Standard rate box is displayed", runtimeState.adminEditResourcePage.isStandardRateBoxDisplayed());
            assertFalse("Recall rate box is not displayed", runtimeState.adminEditResourcePage.isRecallRateBoxDisplayed());
            assertTrue("Out of Hours rate box is displayed", runtimeState.adminEditResourcePage.isOutOfHoursRateBoxDisplayed());
            assertTrue("Subsequent rate box is displayed", runtimeState.adminEditResourcePage.isSubsequentRateBoxDisplayed());
            break;
        case "Out of Hours":
            assertTrue("Standard rate box is displayed", runtimeState.adminEditResourcePage.isStandardRateBoxDisplayed());
            assertTrue("Recall rate box is displayed", runtimeState.adminEditResourcePage.isRecallRateBoxDisplayed());
            assertFalse("Out of Hours rate box is not displayed", runtimeState.adminEditResourcePage.isOutOfHoursRateBoxDisplayed());
            assertTrue("Subsequent rate box is displayed", runtimeState.adminEditResourcePage.isSubsequentRateBoxDisplayed());
            break;
        case "Subsequent":
            assertTrue("Standard rate box is displayed", runtimeState.adminEditResourcePage.isStandardRateBoxDisplayed());
            assertTrue("Recall rate box is displayed", runtimeState.adminEditResourcePage.isRecallRateBoxDisplayed());
            assertTrue("Out of Hours rate box is displayed", runtimeState.adminEditResourcePage.isOutOfHoursRateBoxDisplayed());
            assertFalse("Subsequent rate box is not displayed", runtimeState.adminEditResourcePage.isSubsequentRateBoxDisplayed());
            break;
        }

        resourceHelper.deleteContractorResource();
    }

    @ContinueNextStepsOnException
    @Then("^Geo-Fencing is set to \"([^\"]*)\"$")
    public void geo_fencing_is_set_to(String geoFencing) throws Throwable {
        if (geoFencing.equalsIgnoreCase("Enabled")) {
            assertTrue("Resource is not Geo-Fenced", dbHelperResources.isResourceGeoFenced(testData.getString("resourceName")));
        } else {
            assertFalse("Resource is Geo-Fenced", dbHelperResources.isResourceGeoFenced(testData.getString("resourceName")));
        }

        resourceHelper.deleteContractorResource();
    }

    @ContinueNextStepsOnException
    @Then("^the classification is displayed as expected$")
    public void classification_displayed_as_expected() throws Throwable {
        String numberOfClassifications = runtimeState.adminEditResourcePage.getNumberOfClassificationsForSite(testData.getString("firstSiteName"));
        List<Map<String, Object>> sitesAssignedToContractor = dbHelperResources.getSitesAssignedToContractor(testData.getString("resourceName"));
        int numberOfSites = sitesAssignedToContractor.size();
        assertEquals("Number of sites doesn't match", numberOfSites, Integer.parseInt(numberOfClassifications));

        runtimeState.adminEditResourcePage.editSiteConfig(testData.getString("firstSiteName"));

        assertTrue("Filter box is not displayed", runtimeState.adminEditSiteConfigurationModal.isFilterBoxDisplayed());
        assertTrue("Only show selected filter is not displayed", runtimeState.adminEditSiteConfigurationModal.isOnlyShowSelectedFilterDisplayed());
        assertTrue("Classification has not been selected", runtimeState.adminEditSiteConfigurationModal.isClassificationSelected());
        assertTrue("Priority dropdown is not displayed for selected classification", runtimeState.adminEditSiteConfigurationModal.isPriorityDropdownDisplayedForSelectedClassification());

        runtimeState.adminEditSiteConfigurationModal.removeSelectedClassification();
        assertFalse("Classification has not been removed", runtimeState.adminEditSiteConfigurationModal.isClassificationSelected());

        runtimeState.adminEditSiteConfigurationModal.clickButton("Update");

        numberOfClassifications = runtimeState.adminEditResourcePage.getNumberOfClassificationsForSite(testData.getString("firstSiteName"));
        assertEquals("Number of sites doesn't match", numberOfSites -1, Integer.parseInt(numberOfClassifications));

        assertFalse("Save button is enabled", runtimeState.adminEditResourcePage.isSaveEnabled());

        resourceHelper.deleteContractorResource();
    }

    @ContinueNextStepsOnException
    @Then("^the error \"([^\"]*)\" is displayed$")
    public void error_is_displayed(String expectedErrorMessage) throws Throwable {
        String actualErrorMessage = runtimeState.adminEditResourcePage.getErrorMessage();
        assertEquals("Expected: " + expectedErrorMessage + ", but got: " + actualErrorMessage, expectedErrorMessage, actualErrorMessage);

        if (testData.getString("resourceProfile").equalsIgnoreCase("Contractor")) {
            resourceHelper.deleteResource();
        } else {
            resourceHelper.deleteCityResourceWithJobAssigned();
        }
        resourceHelper.deletePayrollCode();
    }

    @ContinueNextStepsOnException
    @Then("^the edited Resource has been added into the correct table$")
    public void edited_resource_is_in_correct_table() throws Throwable {
        if (testData.get("statusBeforeEditing").toString().equalsIgnoreCase("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectResourcesInactiveTab();
            runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
            runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        } else if(testData.get("statusBeforeEditing").toString().equalsIgnoreCase("Inactive")) {
            runtimeState.adminResourcesAndUsersPage.selectResourcesActiveTab();
            runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
            runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        }

        Grid grid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();
        outputHelper.takeScreenshots();
        assertNotNull("Unexpected Null Grid", grid);

        assertEquals("Incorrect result in Resource column", StringUtils.substringBefore(grid.getRows().get(0).getCell("Resource").getText(), " Edit"), testData.getString("resourceName"));
        assertEquals("Incorrect result in Resource Profile column", grid.getRows().get(0).getCell("Resource profile").getText(), StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));

        resourceHelper.deleteCityResource();
        resourceHelper.deletePayrollCode();
    }

    @ContinueNextStepsOnException
    @Then("^the contractor sites are still displayed$")
    public void contractor_sites_are_displayed() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.selectResourcesInactiveTab();
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));

        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));
        assertTrue("Site Configuration table is not displayed", runtimeState.adminEditResourcePage.isSiteConfigTableDisplayed());
        Grid grid = runtimeState.adminEditResourcePage.getSiteConfigTableGrid();
        assertFalse("Site Configuration table is empty! ", grid.getRows().isEmpty());

        outputHelper.takeScreenshots();
        resourceHelper.deleteContractorResourceWithJobAssigned();
    }

    @ContinueNextStepsOnException
    @Then("^all permanent sites have been removed$")
    public void all_permanent_sites_have_been_removed() throws Throwable {
        runtimeState.adminResourcesAndUsersPage.selectResourcesInactiveTab();
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));

        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));
        assertTrue("Permanent Sites table is not displayed", runtimeState.adminEditResourcePage.isPermSiteTableDisplayed());
        Grid grid = runtimeState.adminEditResourcePage.getPermSiteTableGrid();
        assertTrue("Permanent Sites table is not empty! ", grid.getRows().isEmpty());

        outputHelper.takeScreenshots();
        resourceHelper.deleteCityResourceWithJobAssigned();
        resourceHelper.deletePayrollCode();
    }

    @ContinueNextStepsOnException
    @Then("^the Resource has been added into the \"([^\"]*)\" table$")
    public void resource_is_in_correct_table(String status) throws Throwable {
        if (status.equalsIgnoreCase("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectResourcesActiveTab();
            runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
            runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        } else if(status.equalsIgnoreCase("Inactive")) {
            runtimeState.adminResourcesAndUsersPage.selectResourcesInactiveTab();
            runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceFilter(testData.getString("resourceName"));
            runtimeState.adminResourcesAndUsersPage.setActiveInactiveResourceProfileFilter(StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));
        } else if(status.equalsIgnoreCase("Incomplete")) {
            runtimeState.adminResourcesAndUsersPage.selectResourcesIncompleteTab();
            runtimeState.adminResourcesAndUsersPage.setIncompleteResourceFilter(testData.getString("resourceName"));
            runtimeState.adminResourcesAndUsersPage.setIncompleteResourceProfileFilter(testData.getString("resourceProfile"));
        }

        Grid grid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();
        outputHelper.takeScreenshots();
        assertNotNull("Unexpected Null Grid", grid);

        if (status.equalsIgnoreCase("Active") || status.equalsIgnoreCase("Inactive")) {
            assertEquals("Incorrect result in Resource column", StringUtils.substringBefore(grid.getRows().get(0).getCell("Resource").getText(), " Edit"), testData.getString("resourceName"));
            assertEquals("Incorrect result in Resource Profile column", grid.getRows().get(0).getCell("Resource profile").getText(), StringUtils.substringBefore(testData.getString("resourceProfile"), " ("));

            resourceHelper.deleteCityResource();
            resourceHelper.deletePayrollCode();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Resource working hours are populated as expected$")
    public void resource_working_hours_populated_as_expected() throws ParseException {
        String resourceProfile = StringUtils.substringBefore(testData.getString("resourceProfile"), " (");
        List<Map<String, Object>> workingHours = dbHelperResources.getResourceWorkingHours(resourceProfile);
        List<String> dailyShifts = runtimeState.adminAddNewResourcePage.getWorkingHoursShiftSummary();
        List<String> workingHoursEdited = new ArrayList<>();

        if (workingHours.size() != 0) {
            for (int i = 0; i < workingHours.size(); i ++) {
                long differenceInMinutes = DateHelper.getDifferenceBetweenTwoTimes(workingHours.get(i).get("StartTime").toString(), workingHours.get(i).get("EndTime").toString(), "hh:mm");

                String differenceInHoursAndMinutes = Duration.ofMinutes(differenceInMinutes).toString();

                String editedDay = workingHours.get(i).get("DayOfTheWeek").toString() + " "
                        + workingHours.get(i).get("StartTime").toString().substring(1) + " to "
                        + workingHours.get(i).get("EndTime").toString() + " ("
                        + StringUtils.substringBetween(differenceInHoursAndMinutes, "PT", "H") + "h ";

                if (differenceInHoursAndMinutes.contains("M")) {
                    editedDay = editedDay + StringUtils.substringBetween(differenceInHoursAndMinutes, "H", "M") +"m)";
                } else {
                    editedDay = editedDay + "0m)";
                }

                workingHoursEdited.add(editedDay);
            }
            assertEquals("Expected: " + workingHoursEdited + ", but got: " + dailyShifts, workingHoursEdited, dailyShifts);
        } else {
            assertFalse("Shift summary is displayed even though there are no working hours available", runtimeState.adminAddNewResourcePage.isShiftSummaryDisplayed());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the de-activate profile modal is displayed as expected$")
    public void deactivate_profile_modal_is_displayed_as_expected() {
        String expectedWarningMessage = "Setting this User Profile as inactive will affect the users currently configured with this User Profile."
                + " Please move these users to another User Profile or set them as InActive.";
        String actualWarningMessage = runtimeState.adminEditUserProfilePage.getDeactivateProfileModalText();
        actualWarningMessage = StringUtils.substringBefore(actualWarningMessage, "This User Profile").trim();
        assertEquals("Warning message expected: " + expectedWarningMessage + " But was: " + actualWarningMessage,
                expectedWarningMessage, actualWarningMessage);

        assertFalse("Save button is enabled", runtimeState.adminEditUserProfilePage.isSaveProfileButtonEnabled());

        String resourceNameAndProfile = testData.getString("resourceName")
                + " (" + testData.getString("userName") + ")";
        String assignedResource = runtimeState.adminEditUserProfilePage.getAssignedResource(resourceNameAndProfile);
        assertEquals("Assigned Resource is not displayed", resourceNameAndProfile, assignedResource);

        assertFalse("Change Profile button is enabled", runtimeState.adminEditUserProfilePage.isChangeProfileButtonEnabled());
        runtimeState.adminEditUserProfilePage.selectRandomProfile();
        assertTrue("Change Profile button is not enabled", runtimeState.adminEditUserProfilePage.isChangeProfileButtonEnabled());

        runtimeState.adminEditUserProfilePage.clickChangeProfile();
        runtimeState.adminEditUserProfilePage.saveProfile();
    }

    @ContinueNextStepsOnException
    @Then("^the User Profile is displayed in the \"([^\"]*)\" table$")
    public void user_profile_is_displayed_in_table(String isActive) {

        if (isActive.equals("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectActiveTabUserProfiles();
        } else {
            runtimeState.adminResourcesAndUsersPage.selectInactiveTabUserProfiles();
        }

        runtimeState.adminResourcesAndUsersPage.searchForUserProfile(testData.getString("userProfileName"));
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();

        String expected = testData.getString("userProfileName");
        String actual = normalize(substringBefore(grid.getRows().get(0).getCells().get(0).getText(), "Edit"));
        assertEquals(expected, actual);

        resourceHelper.deleteUserProfile();
        if(testData.get("resourceName") != null) {
            resourceHelper.deleteCityResource();
        }
        resourceHelper.deletePayrollCode();
    }

    @ContinueNextStepsOnException
    @Then("^the Resources page is displayed as expected$")
    public void resources_page_is_displayed_as_expected() {
        assertTrue("Button is not displayed", runtimeState.adminResourcesAndUsersPage.isAddNewResourceButtonDisplayed());

        Grid activeGrid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();
        assertNotNull("Unexpected Null Grid", activeGrid);

        //selects a random number between 2-10 as this reflects the pages shown on screen
        //1 is ignored as this is the default page selected
        Random random = new Random();
        int randomPageNumber = random.nextInt(9) + 2;
        runtimeState.adminResourcesAndUsersPage.selectPage(randomPageNumber);
        int currentPageSelected = runtimeState.adminResourcesAndUsersPage.getCurrentPageNumber();
        assertEquals("Current page does not match page selected", randomPageNumber, currentPageSelected);
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the Resource Profiles page is displayed as expected$")
    public void resource_profiles_page_is_displayed_as_expected() {
        assertTrue("Button is not displayed", runtimeState.adminResourcesAndUsersPage.isAddNewResourceProfileButtonDisplayed());

        Grid activeGrid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();
        assertNotNull("Unexpected Null Grid", activeGrid);

        assertEquals("Active tab is not selected", runtimeState.adminResourcesAndUsersPage.getSelectedTabNameResourceProfiles(), "ACTIVE");
        runtimeState.adminResourcesAndUsersPage.selectInactiveTabResourceProfiles();
        assertEquals("Inactive tab is not selected", runtimeState.adminResourcesAndUsersPage.getSelectedTabNameResourceProfiles(), "INACTIVE");
        runtimeState.adminResourcesAndUsersPage.selectIncompleteTabResourceProfiles();
        assertEquals("Incomplete tab is not selected", runtimeState.adminResourcesAndUsersPage.getSelectedTabNameResourceProfiles(), "INCOMPLETE");
    }

    @ContinueNextStepsOnException
    @Then("^a \"([^\"]*)\" error message is displayed$")
    public void error_message_is_displayed(String expectedErrorTitle) {
        String actualErrorTitle = runtimeState.adminAddNewUserPage.getCreateUserErrorTitle();
        String actualErrorMessage = runtimeState.adminAddNewUserPage.getCreateUserErrorMessage();
        String expectedErrorMessage = "A user already exists with the username " + testData.getString("username");

        assertEquals("Incorrect error title", expectedErrorTitle, actualErrorTitle);
        assertEquals("Incorrect error message", expectedErrorMessage, actualErrorMessage);

        resourceHelper.deleteUser();
    }

    @Then("^the Impersonate User page has the correct column names$")
    public void impersonate_user_page_has_correct_column_names() {
        List<String> expectedHeaders = Arrays.asList("Name", "User Name", "Resource Profile", "Resource Type");
        runtimeState.scenario.write("Expected column names: " + expectedHeaders.toString());
        Grid grid = runtimeState.adminResourcesAndUsersPage.getImpersonateUserGrid();
        List<String> actualHeaders = grid.getHeaders();
        boolean isEqual = expectedHeaders.equals(actualHeaders);
        assertTrue("Expected: " + expectedHeaders + " But got: " + actualHeaders, isEqual);
    }

    @Then("^the \"([^\"]*)\" can perform the expected actions$")
    public void user_can_perform_the_expected_actions(String resourceType) throws Throwable {
        if (resourceType.equalsIgnoreCase("Landlord")) {
            menuSteps.sub_menu_is_selected_from_the_top_menu("Jobs Awaiting Acceptance", "Jobs");
            runtimeState.openAwaitingJobsPage.searchJobsNoWait(testData.getString("jobReference"));
            outputHelper.takeScreenshots();
            runtimeState.jobDetailsPage = runtimeState.openAwaitingJobsPage.openJob(String.valueOf(testData.get("jobReference")));
            outputHelper.takeScreenshots();

            assertTrue("Landlord cannot Decline Job", runtimeState.jobDetailsPage.isDeclineJobDisplayed());
            assertTrue("Landlord cannot Accept Job", runtimeState.jobDetailsPage.isAcceptJobDisplayed());

            runtimeState.jobDetailsPage.acceptJob();
            outputHelper.takeScreenshots();
            assertTrue("Landlord cannot uptdate ETA", runtimeState.jobDetailsPage.isEtaDropdownDisplayed());

        } else if(resourceType.equalsIgnoreCase("Contractor Tech")) {
            runtimeState.portalJobsForSitePage = new PortalJobsForSitePage(getWebDriver()).get();
            runtimeState.portalJobsForSitePage.searchJobs(String.valueOf(testData.get("jobReference")));
            outputHelper.takeScreenshots();

            assertTrue("Contractor Tech cannot Start Work on Job", runtimeState.portalJobsForSitePage.isStartWorkButtonDisplayed(String.valueOf(testData.get("jobReference"))));
            runtimeState.portalJobsForSitePage.clickStartWorkButton(String.valueOf(testData.get("jobReference")));

            //Need to log in again after start work is clicked
            runtimeState.loginPage = new LoginPage(getWebDriver()).get();
            loginSteps.a_user_with_role_has_logged_in("Mercury_Admin_Core");
            helpdeskHomePageSteps.Admin_is_selected_from_the_Mercury_navigation_menu("Admin");
            tileSteps.a_random_tile_is_selected("Resources & Users");
            the_user_is_impersonated();

            outputHelper.takeScreenshots();
            runtimeState.portalJobsForSitePage.searchJobs(String.valueOf(testData.get("jobReference")));
            assertTrue("Contractor Tech cannot Stop Work on Job", runtimeState.portalJobsForSitePage.isStopWorkButtonDisplayed(String.valueOf(testData.get("jobReference"))));
            runtimeState.portalJobsForSitePage.clickStopWorkButton(String.valueOf(testData.get("jobReference")));
            runtimeState.jobDetailsPage = new JobDetailsPage(getWebDriver()).get();
            outputHelper.takeScreenshots();
            assertTrue("Contractor Tech cannot Update Job", runtimeState.jobDetailsPage.isUpdateJobButtonDisplayed());

        } else if(resourceType.equalsIgnoreCase("Contractor Admin")) {
            menuSteps.sub_menu_is_selected_from_the_top_menu("Jobs Awaiting Acceptance", "Jobs");
            runtimeState.openAwaitingJobsPage.searchJobsNoWait(String.valueOf(testData.get("contractorAdminJobReference")));
            outputHelper.takeScreenshots();
            runtimeState.jobDetailsPage = runtimeState.openAwaitingJobsPage.openJob(String.valueOf(testData.get("contractorAdminJobReference")));
            outputHelper.takeScreenshots();

            assertTrue("Contractor Admin cannot Decline Job", runtimeState.jobDetailsPage.isDeclineJobDisplayed());
            assertTrue("Contractor Admin cannot Accept Job", runtimeState.jobDetailsPage.isAcceptJobDisplayed());

            runtimeState.jobDetailsPage.acceptJob();
            outputHelper.takeScreenshots();
            assertTrue("Landlord cannot uptdate ETA", runtimeState.jobDetailsPage.isEtaDropdownDisplayed());

            menuSteps.sub_menu_is_selected_from_the_top_menu("Jobs Awaiting Quote", "Quotes");
            runtimeState.jobsAwaitingQuotePage = new JobsAwaitingQuotePage(getWebDriver()).get();
            runtimeState.jobsAwaitingQuotePage.searchJobs(String.valueOf(testData.get("contractorAdminQuoteJobReference")));
            outputHelper.takeScreenshots();

            assertTrue("Contractor Admin cannot Decline Quote Job", runtimeState.jobsAwaitingQuotePage.isDeclineQuoteJobDisplayed(String.valueOf(testData.get("contractorAdminQuoteJobReference"))));
            assertTrue("Contractor Admin cannot Accept Quote Job", runtimeState.jobsAwaitingQuotePage.isAcceptQuoteJobDisplayed(String.valueOf(testData.get("contractorAdminQuoteJobReference"))));
            runtimeState.jobsAwaitingQuotePage.acceptQuoteJobRequest(testData.getInt("contractorAdminQuoteJobReference"));
            outputHelper.takeScreenshots();
            assertTrue("Contractor Admin cannot Create Quote Job", runtimeState.jobsAwaitingQuotePage.isCreateQuoteJobDisplayed(String.valueOf(testData.get("contractorAdminQuoteJobReference"))));
            runtimeState.jobsAwaitingQuotePage.createQuoteJobRequest(testData.getInt("contractorAdminQuoteJobReference"));
            outputHelper.takeScreenshots();

            runtimeState.createQuotePage = new CreateQuotePage(getWebDriver()).get();
            runtimeState.createQuotePage.setQuoteReference(DataGenerator.GenerateRandomString(20, 20, 20, 0, 0, 0));
            runtimeState.createQuotePage.setDescriptionOfWorks(DataGenerator.GenerateRandomString(25, 25, 25, 0, 0, 0));
            runtimeState.createQuotePage.setProposedWorkingTimes(DataGenerator.GenerateRandomString(20, 20, 20, 0, 0, 0));
            outputHelper.takeScreenshots();
            runtimeState.createQuotePage.startQuote();

            runtimeState.registerQuotePage = new RegisterQuotePage(getWebDriver()).get();
            outputHelper.takeScreenshots();
            assertTrue("Contractor Admin cannot Submit Quote Job", runtimeState.registerQuotePage.isSubmitQuoteJobDisplayed());

            portalQuoteSteps.a_new_line_is_added_to_the_quote_breakdown();
            portalQuoteSteps.the_line_item_type_is_entered("Labour");
            portalQuoteSteps.the_line_item_description_is_entered("something");
            portalQuoteSteps.the_line_item_quantity_is_entered(1);
            portalQuoteSteps.the_line_item_unit_price_is_entered((float) 2499.99);
            portalQuoteSteps.the_line_is_added_to_the_quote();
            portalQuoteSteps.and_uploads_the_quote_document();
            runtimeState.registerQuotePage.submitQuote();

            Map<String, Object> rfmDetailsForJob = dbHelperResources.getResourceForJob("RFM", testData.getInt("contractorAdminQuoteJobReference"));
            JobDetails quoteJobDetails = quoteCreationHelper.getQuoteJobDetails(testData.getInt("contractorAdminQuoteJobReference"), (int) rfmDetailsForJob.get("ResourceId"));
            quoteCreationHelper.createQuoteQuery(testData.getInt("contractorAdminQuoteJobReference"), quoteJobDetails.getResourceQuotes().get(0).getId(), (int) rfmDetailsForJob.get("ResourceId"));

            menuSteps.sub_menu_is_selected_from_the_top_menu("Quotes with Query Pending", "Quotes");
            runtimeState.quotesWithQueryPendingPage = new QuotesWithQueryPendingPage(getWebDriver()).get();
            runtimeState.quotesWithQueryPendingPage.searchJobs(String.valueOf(testData.get("contractorAdminQuoteJobReference")));
            outputHelper.takeScreenshots();

            runtimeState.quotesWithQueryPendingPage.OpenJob(String.valueOf(testData.get("contractorAdminQuoteJobReference")));
            outputHelper.takeScreenshots();
            runtimeState.quoteQueryPage = new QuoteQueryPage(getWebDriver()).get();
            assertTrue("Contractor Admin cannot Send Quote Query Response", runtimeState.quoteQueryPage.isSendResponseButtonDisplayed());

            menuSteps.sub_menu_is_selected_from_the_top_menu("Orders Awaiting Invoice", "Invoices and Credits");
            runtimeState.ordersAwaitingInvoicePage = new OrdersAwaitingInvoicePage(getWebDriver()).get();
            runtimeState.ordersAwaitingInvoicePage.searchOrders(String.valueOf(testData.get("contractorAdminInvoiceJobReference")));
            outputHelper.takeScreenshots();

            runtimeState.ordersAwaitingInvoicePage.openOrderAwaitingInvoice(String.valueOf(testData.get("contractorAdminInvoiceJobReference")));
            runtimeState.uploadInvoiceDocumentsPage = new UploadInvoiceDocumentsPage(getWebDriver()).get();
            runtimeState.uploadInvoiceDocumentsPage.setInvoiceFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
            runtimeState.uploadInvoiceDocumentsPage.setJobSheetFileName(System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf");
            outputHelper.takeScreenshots();
            runtimeState.uploadInvoiceDocumentsPage.clickUploadFiles();

            runtimeState.invoiceDetailsPage = new InvoiceDetailsPage(getWebDriver()).get();
            portalOrdersAwaitingInvoiceSteps.an_invoice_number_is_entered();
            portalOrdersAwaitingInvoiceSteps.an_invoice_date_is_entered_which_is_after_the_job_date();
            runtimeState.invoiceDetailsPage.setInvoiceNetAmount("100");
            runtimeState.invoiceDetailsPage.setInvoiceTaxAmount("10");
            runtimeState.invoiceDetailsPage.setLegalEntity("City FM");
            outputHelper.takeScreenshots();
            commonSteps.the_button_is_clicked("Save");

            runtimeState.portalOrdersInvoicePage = new PortalOrdersInvoicePage(getWebDriver()).get();
            assertTrue("Contractor Admin cannot Submit Invoice", runtimeState.portalOrdersInvoicePage.isSubmitInvoiceButtonDisplayed());
        }
    }

    @ContinueNextStepsOnException
    @Then("^each column filter defaults to \"([^\"]*)\"$")
    public void column_filter_default(String contains) {
        String filterText = null;

        //1 and 2 denotes the 1st and 2nd dropdowns
        runtimeState.adminResourcesAndUsersPage.selectImpersonateGridFilter("Name");
        filterText = runtimeState.adminResourcesAndUsersPage.getTextOfFilterDropdown(1);
        assertEquals("Filter is not defaulted to Contains", filterText, contains);
        filterText = runtimeState.adminResourcesAndUsersPage.getTextOfFilterDropdown(2);
        assertEquals("Filter is not defaulted to Contains", filterText, contains);

        runtimeState.adminResourcesAndUsersPage.selectImpersonateGridFilter("User Name");
        filterText = runtimeState.adminResourcesAndUsersPage.getTextOfFilterDropdown(1);
        assertEquals("Filter is not defaulted to Contains", filterText, contains);
        filterText = runtimeState.adminResourcesAndUsersPage.getTextOfFilterDropdown(2);
        assertEquals("Filter is not defaulted to Contains", filterText, contains);

        runtimeState.adminResourcesAndUsersPage.selectImpersonateGridFilter("Resource Profile");
        filterText = runtimeState.adminResourcesAndUsersPage.getTextOfFilterDropdown(1);
        assertEquals("Filter is not defaulted to Contains", filterText, contains);
        filterText = runtimeState.adminResourcesAndUsersPage.getTextOfFilterDropdown(2);
        assertEquals("Filter is not defaulted to Contains", filterText, contains);
    }

    @ContinueNextStepsOnException
    @Then("^the \"([^\"]*)\" columns can be filtered$")
    public void columns_can_be_filtered(String columns) throws Throwable {
        String[] parts = columns.split(",");
        Grid grid = runtimeState.adminResourcesAndUsersPage.getImpersonateUserGrid();
        List<Row> rows = grid.getRows();

        for (String column : parts) {
            column = column.trim();
            int randomSelector = RandomUtils.nextInt(0, rows.size() - 1);
            String filter = rows.get(randomSelector).getCell(column).getText();
            runtimeState.scenario.write("Filtering on: " + filter);
            GridHelper.filterIsEqualTo(grid.getGridXpath(), column, filter);

            List<String> gridStr = runtimeState.adminResourcesAndUsersPage.getImpersonateUserGridAsString();
            for (String row : gridStr) {
                assertTrue(row.contains(filter));
            }
            outputHelper.takeScreenshot();
            GridHelper.filterClear(grid.getGridXpath(), column);
        }
    }

    @When("^an? \"([^\"]*)\" is selected from Resource Profiles$")
    public void column_is_filtered_on(String filter) throws Throwable {
        switch(filter) {
        case "Contractor Admin":
            runtimeState.scenario.write("Filtering on: 'Resource Profile' = 'Contractor' AND 'User Name' does not contain '_tech'");
            runtimeState.adminResourcesAndUsersPage.filterImpersonatorGridIsEqualTo("Resource Profile", "Contractor");
            runtimeState.adminResourcesAndUsersPage.filterImpersonatorGridDoesNotContain("User Name", "_tech");
            break;

        case "Contractor Technician":
            runtimeState.scenario.write("Filtering on: 'Resource Profile' = 'Contractor' AND 'User Name' contains '_tech'");
            runtimeState.adminResourcesAndUsersPage.filterImpersonatorGridIsEqualTo("Resource Profile", "Contractor");
            runtimeState.adminResourcesAndUsersPage.filterImpersonatorGridContains("User Name", "_tech");
            break;

        default:
            runtimeState.scenario.write(String.format("Filtering on: 'Resource Profile' = '%s'", filter));
            runtimeState.adminResourcesAndUsersPage.filterImpersonatorGridIsEqualTo("Resource Profile", filter);
            break;
        }
        outputHelper.takeScreenshot();

        Grid grid = runtimeState.adminResourcesAndUsersPage.getImpersonateUserGridFirstRow();
        testData.put("username", grid.getRows().get(0).getCell("User Name").getText());

        runtimeState.adminResourcesAndUsersPage.selectFirstRowToImpersonate();
        runtimeState.adminResourcesAndUsersPage.confirmImpersonateUser();
    }

    @ContinueNextStepsOnException
    @Then("^the Users page is displayed as expected$")
    public void users_page_is_displayed_as_expected() {
        assertTrue("Button is not displayed", runtimeState.adminResourcesAndUsersPage.isAddNewUserButtonDisplayed());

        assertTrue("Name filter is not displayed", runtimeState.adminResourcesAndUsersPage.isNamefilterDisplayedUsers());
        assertTrue("Username filter is not displayed", runtimeState.adminResourcesAndUsersPage.isUsernameFilterDisplayedUsers());
        assertTrue("Resource Profile filter is not displayed", runtimeState.adminResourcesAndUsersPage.isResourceProfileDisplayedUsers());

        Grid activeGrid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();
        assertNotNull("Unexpected Null Grid", activeGrid);
        String selectedTab = runtimeState.adminResourcesAndUsersPage.getSelectedTabNameUsers();
        assertEquals("Active tab is not selected", selectedTab, "ACTIVE");
        outputHelper.takeScreenshots();

        runtimeState.adminResourcesAndUsersPage.selectInactiveTabUsers();
        Grid inactiveGrid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();
        assertNotNull("Unexpected Null Grid", inactiveGrid);
        selectedTab = runtimeState.adminResourcesAndUsersPage.getSelectedTabNameUsers();
        assertEquals("Active tab is not selected", selectedTab, "INACTIVE");
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the User Profiles page is displayed as expected$")
    public void user_profiles_page_is_displayed_as_expected() {
        assertTrue("Button is not displayed", runtimeState.adminResourcesAndUsersPage.isAddNewUserProfileButtonDisplayed());

        Grid activeGrid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();
        assertNotNull("Unexpected Null Grid", activeGrid);
        String selectedTab = runtimeState.adminResourcesAndUsersPage.getSelectedTabNameUserProfiles();
        assertEquals("Active tab is not selected", selectedTab, "ACTIVE");
        outputHelper.takeScreenshots();

        runtimeState.adminResourcesAndUsersPage.selectInactiveTabUserProfiles();
        Grid inactiveGrid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();
        assertNotNull("Unexpected Null Grid", inactiveGrid);
        selectedTab = runtimeState.adminResourcesAndUsersPage.getSelectedTabNameUserProfiles();
        assertEquals("Active tab is not selected", selectedTab, "INACTIVE");
        outputHelper.takeScreenshots();
    }

    @SuppressWarnings("deprecation")
    @ContinueNextStepsOnException
    @Then("^the user is presented with an option to replace the existing resource$")
    public void user_presented_with_option_to_replace_existing_resource() {
        assertFalse("Save button is enabled", runtimeState.adminEditResourcePage.isSaveEnabled());
        assertTrue("Replace resource bar is not available", runtimeState.adminEditResourcePage.isReplaceResourceSiteBarDisplayed());

        runtimeState.adminEditResourcePage.clickReplaceResourceSiteBar();
        outputHelper.takeScreenshots();
        assertTrue("Save button is not enabled", runtimeState.adminEditResourcePage.isSaveEnabled());

        String replaceResourceText = runtimeState.adminEditResourcePage.getReplaceResourceText();
        String replaceResourceName = StringUtils.substringAfter(replaceResourceText, "Replace ").toLowerCase();
        replaceResourceName = WordUtils.capitalize(replaceResourceName);
        if (replaceResourceName.contains(",")) {
            replaceResourceName = StringUtils.substringBefore(replaceResourceName, ",");
        }

        int replaceResourceId = dbHelperResources.getResourceId(replaceResourceName);
        int replaceSiteId = dbHelperSites.getSiteId(testData.getString("siteName"));
        dbHelper.deleteFromSiteResourceTable(replaceResourceId, replaceSiteId);

        runtimeState.adminEditResourcePage.saveChanges();
        resourceHelper.deleteCityResourceWithSite();
        resourceHelper.deletePayrollCode();
    }

    @ContinueNextStepsOnException
    @Then("^the impersonated users home screen is displayed$")
    public void the_impersonated_users_home_screen_is_displayed() {
        Map<String, Object> dbData = dbHelperUsers.getApplicationUserFromUserName(testData.getString("username"));
        String fullName = normalize(dbData.get("FirstName") + " " + dbData.get("LastName"));

        runtimeState.scenario.write("Asserting navbar displays user: " + fullName);

        assertTrue(normalize(runtimeState.helpdeskNavBar.getUserName()).contains(fullName));
    }

    @ContinueNextStepsOnException
    @Then("^all the resource types are displayed in the dropdown$")
    public void resource_types_are_displayed_in_dropdown() {
        List<String> expectedResourceTypes = Arrays.asList("City Resource", "Operational Manager", "Head Office",
                "Client Contact", "Contractor", "Directors", "System", "Landlord", "Team (Distribution Group)");

        List<String> actualResourceTypes = runtimeState.adminAddNewResourceProfilePage.getListOfResourceTypes();

        for (int i = 0; i < expectedResourceTypes.size(); i++) {
            assertEquals(expectedResourceTypes.get(i).toString(), actualResourceTypes.get(i +1).toString());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the confirm removal checkbox is displayed$")
    public void confirm_removal_checkbox_is_displayed() {
        assertFalse("Save button is enabled", runtimeState.adminEditResourceProfilePage.isSaveButtonClickable());
        assertTrue("Confirm removal checkbox is not displayed", runtimeState.adminEditResourceProfilePage.isConfirmRemovalCheckboxDisplayed());

        runtimeState.adminEditResourceProfilePage.clickConfirmRemovalCheckbox();
        assertTrue("Save button is not enabled", runtimeState.adminEditResourceProfilePage.isSaveButtonClickable());

        resourceHelper.deleteResourceProfile();
    }

    @ContinueNextStepsOnException
    @Then("^the Resource Profile is displayed in the correct table$")
    public void resource_profile_displayed_in_correct_table() {
        if(testData.get("resourceProfileStatus").equals("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectActiveTabResourceProfiles();
        } else {
            runtimeState.adminResourcesAndUsersPage.selectInactiveTabResourceProfiles();
        }

        runtimeState.adminResourcesAndUsersPage.searchForResourceProfile(testData.getString("resourceProfileName"));
        runtimeState.adminResourcesAndUsersPage.searchForResourceType(testData.getString("resourceType"));
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();

        String expectedResourceProfileName = testData.getString("resourceProfileAlias")
                + " ( " + testData.getString("resourceProfileName") + " )";
        String actualResourceProfileName = normalize(substringBefore(grid.getRows().get(0).getCells().get(0).getText(), "Edit"));
        assertEquals(expectedResourceProfileName, actualResourceProfileName);

        resourceHelper.deleteResourceProfile();
    }

    @ContinueNextStepsOnException
    @Then("^the User is displayed in the correct table$")
    public void user_displayed_in_correct_table() {
        if(testData.get("userStatus").equals("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectActiveTabUsers();
        } else {
            runtimeState.adminResourcesAndUsersPage.selectInactiveTabUsers();
        }

        runtimeState.adminResourcesAndUsersPage.searchForUserName(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.searchForUserUsername(testData.getString("username"));
        String resourceProfileName = dbHelperResources.getResourceProfileName(testData.getString("username"));
        runtimeState.adminResourcesAndUsersPage.searchForUserResourceProfile(resourceProfileName);
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();

        String expectedName = normalize(testData.getString("resourceName"));
        String actualName = normalize(substringBefore(grid.getRows().get(0).getCells().get(0).getText(), "Edit"));
        assertEquals(expectedName, actualName);

        String expectedUserName = normalize(testData.getString("username"));
        String actualUserName = normalize(grid.getRows().get(0).getCells().get(1).getText());
        assertEquals(expectedUserName, actualUserName);

        String expectedResourceProfileName = resourceProfileName;
        String actualResourceProfileName = normalize(grid.getRows().get(0).getCells().get(2).getText());
        assertEquals(expectedResourceProfileName, actualResourceProfileName);

        resourceHelper.deleteUser();
    }

    @ContinueNextStepsOnException
    @Then("^the edited User is displayed in the correct table$")
    public void edited_user_displayed_in_correct_table() {
        if (testData.get("editedStatus").equals("Active")) {
            runtimeState.adminResourcesAndUsersPage.selectActiveTabUsers();
        } else {
            runtimeState.adminResourcesAndUsersPage.selectInactiveTabUsers();
        }

        runtimeState.adminResourcesAndUsersPage.searchForUserName(testData.getString("resourceName"));
        runtimeState.adminResourcesAndUsersPage.searchForUserUsername(testData.getString("username"));
        String resourceProfileName = dbHelperResources.getResourceProfileName(testData.getString("username"));
        runtimeState.adminResourcesAndUsersPage.searchForUserResourceProfile(resourceProfileName);
        outputHelper.takeScreenshots();
        Grid grid = runtimeState.adminResourcesAndUsersPage.getActiveUserGrid();

        String expectedName = normalize(testData.getString("resourceName"));
        String actualName = normalize(substringBefore(grid.getRows().get(0).getCells().get(0).getText(), "Edit"));
        assertEquals(expectedName, actualName);

        String expectedUserName = testData.getString("username");
        String actualUserName = normalize(grid.getRows().get(0).getCells().get(1).getText());
        assertEquals(expectedUserName, actualUserName);

        String expectedResourceProfileName = resourceProfileName;
        String actualResourceProfileName = normalize(grid.getRows().get(0).getCells().get(2).getText());
        assertEquals(expectedResourceProfileName, actualResourceProfileName);

        resourceHelper.deleteUser();
    }

    @ContinueNextStepsOnException
    @Then("^the Create Resource Profile button is unavailable$")
    public void create_resource_profile_button_unavailable() {
        assertTrue("Cancel button is not displayed", runtimeState.adminAddNewResourceProfilePage.isCancelButtonDisplayed());
        assertTrue("Save Progress button is not displayed", runtimeState.adminAddNewResourceProfilePage.isSaveProgressButtonDisplayed());
        assertFalse("Create Resource Profile button is displayed", runtimeState.adminAddNewResourceProfilePage.isCreateResourceProfileButtonDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the Resources & Users main page is displayed as expected$")
    public void resources_and_users_main_page_displayed_as_expected() throws Throwable {
        assertTrue("Resources menu is not displayed", runtimeState.adminResourcesAndUsersPage.isMenuItemDisplayed("Resources"));
        assertTrue("Resource profiles menu is not displayed", runtimeState.adminResourcesAndUsersPage.isMenuItemDisplayed("Resource profiles"));
        assertTrue("User profiles menu is not displayed", runtimeState.adminResourcesAndUsersPage.isMenuItemDisplayed("User profiles"));
        assertTrue("Users menu is not displayed", runtimeState.adminResourcesAndUsersPage.isMenuItemDisplayed("Users"));
        assertTrue("Impersonate user menu is not displayed", runtimeState.adminResourcesAndUsersPage.isMenuItemDisplayed("Impersonate user"));
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the shift summary has been updated with the edited values$")
    public void shift_summary_has_been_updated() {
        runtimeState.adminResourcesAndUsersPage.selectResourcesIncompleteTab();
        runtimeState.adminResourcesAndUsersPage.setIncompleteResourceFilter(testData.getString("resourceName"));
        String resourceProfile = testData.getString("resourceProfile");
        resourceProfile = resourceProfile.contains("(") ? resourceProfile.split("\\(")[0].trim() : resourceProfile;
        runtimeState.adminResourcesAndUsersPage.setIncompleteResourceProfileFilter(resourceProfile);
        runtimeState.adminEditResourcePage = runtimeState.adminResourcesAndUsersPage.editResource(testData.getString("resourceName"));

        assertFalse("Monday's shift has not been removed from the shift summary", runtimeState.adminEditResourcePage.isShiftDisplayed("Monday"));
        assertFalse("Monday's shift overnight has not been removed from the shift summary", runtimeState.adminEditResourcePage.isShiftOvernightDisplayed("Monday"));

        String saturdayShiftSummary = "Saturday 6:00 to 9:00 (3h 0m)";
        assertTrue("Saturday's shift has not been added to the shift summary", runtimeState.adminEditResourcePage.isShiftDisplayed("Saturday"));
        assertEquals("Saturday's shift summary not displayed as expected.", saturdayShiftSummary, runtimeState.adminEditResourcePage.getShiftSummaryText("Saturday"));
        assertEquals("Saturday's shift is displayed as overnight when it shouldn't be", "No", runtimeState.adminEditResourcePage.getShiftOvernightText("Saturday"));

        String wednesdayShiftSummary = "Wednesday 8:00 to Thursday 6:00 (22h 0m)";
        assertTrue("Wednesday's shift is not displayed on the shift summary", runtimeState.adminEditResourcePage.isShiftDisplayed("Wednesday"));
        assertEquals("Wednesday's shift summary not displayed as expected.", wednesdayShiftSummary, runtimeState.adminEditResourcePage.getShiftSummaryText("Wednesday"));
        assertEquals("Wednesday's shift isn't displayed as overnight when it should be", "Yes", runtimeState.adminEditResourcePage.getShiftOvernightText("Wednesday"));
    }

    @ContinueNextStepsOnException
    @Then("^the Resource Profile shift summary has been updated with the edited values$")
    public void resource_profile_shift_summary_has_been_updated() {
        assertFalse("Monday's shift has not been removed from the shift summary", runtimeState.adminEditResourceProfilePage.isShiftDisplayed("Monday"));
        assertFalse("Monday's shift overnight has not been removed from the shift summary", runtimeState.adminEditResourceProfilePage.isShiftOvernightDisplayed("Monday"));

        String saturdayShiftSummary = "Saturday 6:00 to 9:00 (3h 0m)";
        assertTrue("Saturday's shift has not been added to the shift summary", runtimeState.adminEditResourceProfilePage.isShiftDisplayed("Saturday"));
        assertEquals("Saturday's shift summary not displayed as expected.", saturdayShiftSummary, runtimeState.adminEditResourceProfilePage.getShiftSummaryText("Saturday"));
        assertEquals("Saturday's shift is displayed as overnight when it shouldn't be", "No", runtimeState.adminEditResourceProfilePage.getShiftOvernightText("Saturday"));

        String wednesdayShiftSummary = "Wednesday 8:00 to Thursday 6:00 (22h 0m)";
        assertTrue("Wednesday's shift is not displayed on the shift summary", runtimeState.adminEditResourceProfilePage.isShiftDisplayed("Wednesday"));
        assertEquals("Wednesday's shift summary not displayed as expected.", wednesdayShiftSummary, runtimeState.adminEditResourceProfilePage.getShiftSummaryText("Wednesday"));
        assertEquals("Wednesday's shift isn't displayed as overnight when it should be", "Yes", runtimeState.adminEditResourceProfilePage.getShiftOvernightText("Wednesday"));

        resourceHelper.deleteResourceProfile();
    }
}
