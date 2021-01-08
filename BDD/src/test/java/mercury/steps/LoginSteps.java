package mercury.steps;

import static mercury.helpers.StringHelper.normalize;
import static mercury.helpers.TimeoutHelper.setTimeout;
import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.steps.CommonSteps.assertDataFound;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.applicationinsights.web.dependencies.apachecommons.lang3.StringUtils;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.ApplicationUserDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.UserJob;
import mercury.databuilders.TestData;
import mercury.databuilders.User;
import mercury.helpers.DateHelper;
import mercury.helpers.EmailHelper;
import mercury.helpers.LoginLogoutHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.WebDriverHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperOrganisation;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperTimeZone;
import mercury.helpers.dbhelper.DbHelperUsers;
import mercury.pageobject.web.LoginPage;
import mercury.pageobject.web.ResetPasswordPage;
import mercury.runtime.RuntimeState;
import mercury.runtime.ThreadManager;

public class LoginSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;
    @Autowired private DbHelperUsers dbHelperUsers;
    @Autowired private DbHelperOrganisation dbHelperOrganisation;
    @Autowired private EmailHelper emailHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private User user;
    @Autowired private ApplicationUserDao applicationUserDao;
    @Autowired private TestData testData;
    @Autowired private LoginLogoutHelper loginLogoutHelper;

    ApplicationUser appUser;
    UserJob userJob;


    /**
     * @param portal: one of : Helpdesk, Resource, Customer, Field Management, Admin, Portal, Reports
     * @param role: any from AspNetRoles
     * @throws Exception
     */
    @Given("^a \"([^\"]*)\" user with \"([^\"]*)\" role$")
    public void a_user_with_role(String portal, String role) throws Exception {
        appUser = applicationUserDao.getUserWithRole(portal, role);
        if (appUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
    }

    /**
     * @param portal: one of : Helpdesk, Resource, Customer, Field Management, Admin, Portal, Reports
     * @param role: any from AspNetRoles
     * @throws Exception
     */
    @Given("^a user with \"([^\"]*)\" profile and \"([^\"]*)\" role$")
    public void a_user_profile_with_role(String userProfile, String role) throws Exception {
        appUser = applicationUserDao.getUserWithProfileAndRole(userProfile, role);
        assertDataFound(testData.getString("sql"), appUser);
        testData.put("applicationUser", appUser);
    }

    /**
     * login as user with default portal and with role
     * @param portal: one of : Helpdesk, Resource, Customer, Field Management, Admin, Portal, Reports
     * @param role: any from AspNetRoles
     * @throws Exception
     */
    @Given("^a \"([^\"]*)\" user with \"([^\"]*)\" role has logged in$")
    public void a_user_with_role_has_logged_in(String portal, String role) throws Exception {
        a_user_with_role(portal, role);
        loginLogoutHelper.login(appUser);
    }

    /**
     * login as user with role
     * @param role: any from AspNetRoles
     * @throws Exception
     */
    @Given("^a user with \"([^\"]*)\" role has logged in$")
    public void a_user_with_role_has_logged_in(String role) throws Exception {
        a_user_with_role("Helpdesk", role);
        loginLogoutHelper.login(appUser);
    }

    @Given("a helpdesk user without \"([^\"]*)\" role")
    public void a_user_without_role_has_logged_in(String role) throws Exception {
        ApplicationUser applicationUser = applicationUserDao.getAdminUserWithoutRole(role);
        if (applicationUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        testData.put("userName", applicationUser.getUserName());
        testData.put("applicationUser", applicationUser);
    }

    @Given("an? (?:Helpdesk|IT) user has logged in")
    public void a_IT_user_has_logged_in() throws Exception {
        loginLogoutHelper.loginAsTestAutomationUser();
        outputHelper.takeScreenshots();

        if (propertyHelper.getEnv().equalsIgnoreCase("dev_uswm")) {
            setTimeout(300);
        }
    }

    @Given("^an? \"([^\"]*)\"(?: user|) has logged in$")
    public void user_with_profile_has_been_impersonated(String profileName) throws Throwable {
        ApplicationUser applicationUser;
        if (testData.getBoolean("dataset")) {
            applicationUser = applicationUserDao.getByUsername(testData.getString("userName"));
        } else {
            applicationUser = applicationUserDao.getForUserProfile(profileName);
        }

        CommonSteps.assertDataFound(applicationUser);
        loginLogoutHelper.loginAndImpersonate(applicationUser);
    }

    @Given("^an application url has been opened$")
    public void an_application_url_has_been_opened() throws Throwable {
        loginLogoutHelper.openUrl();
    }

    @Given("^notifications url has been opened$")
    public void an_notifications_url_has_been_opened() throws Throwable {
        loginLogoutHelper.openNotificationUrl();
    }

    @Given("^system waits for \"([^\"]*)\" minute$")
    public void system_waits_for_minutes(Integer maxTime) throws Throwable {
        runtimeState.loginPage.waitForCertainTimeInMinutes(maxTime);
    }

    @Given("^login page is displayed correctly$")
    public void login_page_displayed_correctly() throws Throwable {
        outputHelper.takeScreenshot();
        assertTrue("Login page is not displayed correctly ", runtimeState.loginPage.isDisplayed());
    }

    @Given("^notification page is displayed correctly$")
    public void notification_page_displayed_correctly() throws Throwable {
        assertTrue("Notification page is not displayed correctly ", runtimeState.notificationPage.isActivityMonitorDisplayed());
    }

    @When("^the user logs out and then back in$")
    public void the_user_logs_out_and_then_back_in() throws Throwable {
        getWebDriver().manage().deleteAllCookies();
        getWebDriver().get(propertyHelper.getMercuryUrl() +  "/Account/Logoff");
        POHelper.waitForAngularRequestsToFinish();
        outputHelper.takeScreenshots();

        getWebDriver().close();
        getWebDriver().quit();

        WebDriverHelper webDriverHelper = new WebDriverHelper();
        ThreadManager.webDriver.set(webDriverHelper.getNewWebDriver());
        logger.debug("WebDriver instance created : " + getWebDriver().hashCode() + " [JVM process name : " + ManagementFactory.getRuntimeMXBean().getName() + "]");

        loginLogoutHelper.login(testData.getString("loginUserName"), testData.getString("loginUserPassword"));

        if (testData.getString("impersonatedUserId") != null) {
            loginLogoutHelper.impersonateUserViaAPI(testData.getString("impersonatedUserId"));
        }
    }

    @Given("^a \"([^\"]*)\"(?: user|) has logged in \"([^\"]*)\"$")
    public void user_with_profile_logged_in(String profileName, String when) throws Exception {

        boolean loginOutOfHours = true;
        if ("InHours".equalsIgnoreCase(when) || "In Hours".equalsIgnoreCase(when)) {
            loginOutOfHours = false;
        }

        boolean isHelpdeskOutOfHours = dbHelperTimeZone.isHelpdeskOutOfHours();
        if (loginOutOfHours != isHelpdeskOutOfHours) {
            throw new PendingException("Current time is not Helpdesk " + when);
        }

        user_with_profile(profileName);
        user_logs_in();

    }

    @Given("^a \"([^\"]*)\" portal user$")
    public void a_portal_user(String resourceType) throws Exception {
        testData.put("resourceTypeName", resourceType);
        appUser = applicationUserDao.getPortalUserWithResourceType(resourceType);

        testData.put("impersonatedResourceName", String.format(appUser.getFirstName() + " " + appUser.getLastName()).trim());
        testData.put("resourceType", resourceType);

        runtimeState.scenario.write("userName: " + appUser.getUserName());
        runtimeState.scenario.write("firstname lastname: " + appUser.getFirstName() + " " + appUser.getLastName());
        runtimeState.scenario.write("resourceId: " + appUser.getResourceId());
    }

    @Given("^an \"((?:Active Directory|Non Active Directory))\" portal user$")
    public void AD_portal_user(String adUser) throws Throwable {
        if (adUser.equalsIgnoreCase("Active Directory")) {
            appUser = applicationUserDao.getActiveDirectoryPortalUser(true);
        } else {
            appUser = applicationUserDao.getActiveDirectoryPortalUser(false);
        }

        if (appUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }

        testData.addStringTag("userName", appUser.getUserName());
        testData.addIntegerTag("accessFailedCount", appUser.getAccessFailedCount());
        testData.addTimestampTag("lockoutDate", appUser.getLockoutEndDateUtc());
    }

    @Given("^a (?:portal |)user with a \"([^\"]*)\" profile and a \"([^\"]*)\" role$")
    public void user_with_profile_with_permission(String profileName, String roleName) throws Exception {
        switch (profileName) {
        case "City Resource" :
        case "Operational Manager" :
        case "Contractor" :
            testData.addBooleanTag("useResourceTypeName", true);
            testData.addStringTag("resourceTypeName", profileName);
            testData.addStringTag("profileName", "NA");
            testData.addStringTag("roleName", roleName);
            break;
        default :
            testData.addBooleanTag("useResourceTypeName", false);
            testData.addStringTag("resourceTypeName", "NA");
            testData.addStringTag("profileName", profileName);
            testData.addStringTag("roleName", roleName);
            break;
        }

        appUser = applicationUserDao.getUserWithResourceProfileAndRole(testData.getString("profileName"), testData.getString("resourceTypeName"), testData.getString("roleName"), testData.getBoolean("useResourceTypeName"));

        if (appUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
    }

    @Given("^an \"([^\"]*)\" portal user with profile \"([^\"]*)\"$")
    public void AD_portal_user_with_profile(String adUser, String profileName) throws Throwable {

        profileName = "RHVAC Supervisor".equals(profileName) && propertyHelper.getEnv().contains("UKRB") ? "HVAC Supervisor" : profileName;

        if (adUser.equalsIgnoreCase("Active Directory")) {
            appUser = applicationUserDao.getActiveDirectoryUserWithMatchingResource(profileName);
            testData.addStringTag("profileName", profileName);
            testData.addStringTag("userName", appUser.getUserName());
            testData.addIntegerTag("accessFailedCount", appUser.getAccessFailedCount());
            testData.addTimestampTag("lockoutDate", appUser.getLockoutEndDateUtc());
            if (appUser == null) {
                throw new PendingException("Cannot find suitable data for test");
            }
        } else {
            getNonADUserWithProfile(profileName);
        }
    }

    public void getUserWithProfile(String profileName) {
        appUser = applicationUserDao.getForUserProfile(profileName);

        if (appUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        runtimeState.scenario.write("Application Username: " + appUser.getUserName() + ", Id: " + appUser.getId());
        testData.put("userName", appUser.getUserName());
        testData.put("accessFailedCount", appUser.getAccessFailedCount());
        testData.put("lockoutDate", String.valueOf(appUser.getLockoutEndDateUtc()));
    }

    public void getUserProfileWithSubmittedInvoicesAndCredits(String profileName) {
        appUser = applicationUserDao.getUserWithSubmittedInvoicesAndCredits(profileName);

        if (appUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        runtimeState.scenario.write("Application Username: " + appUser.getUserName() + ", Id: " + appUser.getId());
        testData.put("userName", appUser.getUserName());
        testData.put("accessFailedCount", appUser.getAccessFailedCount());
        testData.put("lockoutDate", String.valueOf(appUser.getLockoutEndDateUtc()));
    }

    public void getNonADUserWithProfile(String profileName) {
        appUser = applicationUserDao.getNonADUserWithFailedLoginAttempts(profileName, null);

        if (appUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        runtimeState.scenario.write("Application Username: " + appUser.getUserName() + ", Id: " + appUser.getId());
        testData.put("userName", appUser.getUserName());
        testData.put("accessFailedCount", appUser.getAccessFailedCount());
        testData.put("lockoutDate", String.valueOf(appUser.getLockoutEndDateUtc()));
    }

    public void useResource(Map<String, Object> resource) {
        int resourceId = (Integer) resource.get("Id");
        String username = (String) resource.get("UserName");
        String resourceProfile = (String) resource.get("ResourceProfile");
        runtimeState.scenario.write("resourceId: " + resourceId + ", username: " + username + ", resourceProfile: " + resourceProfile);
        testData.put("resourceId", resourceId);
        testData.put("userName", username);
        testData.put("profileName", resourceProfile);
    }

    @Given("^an? \"([^\"]*)\" with access to the \"([^\"]*)\" menu$")
    public void a_with_access_to_the_menu(String profileName, String menu) throws Exception {
        boolean found = false;
        for (Map<String, Object> resource : dbHelperResources.getResourceWithMenu(menu)) {
            String resourceProfile = (String) resource.get("ResourceProfile");

            if ("Supervisor".equals(profileName)) {
                if (resourceProfile.equals("HVAC Supervisor") || resourceProfile.equals("Refrigeration Supervisor") || resourceProfile.equals("RHVAC Supervisor")) {
                    useResource(resource);
                    found = true;
                    break;
                }
            } else if ("City Tech".equals(profileName)) {
                if (resourceProfile.equals("MST") || resourceProfile.equals("HVAC Technician") || resourceProfile.equals("Refrigeration Technician") || resourceProfile.equals("RHVAC Technician")) {
                    useResource(resource);
                    found = true;
                    break;
                }
            } else if (resourceProfile.equals(profileName)) {
                useResource(resource);
                found = true;
                break;
            }
        }
        if (!found ) throw new PendingException("No suitable resource found");
    }

    @Given("^a (?:portal |)user with profile \"([^\"]*)\"$")
    public void user_with_profile(String profileName) throws Exception {
        testData.put("profileName", profileName);

        switch (profileName) {
        case "City Resource":
        case "Operational Manager":
        case "Contractor":
            testData.put("useResourceTypeName", true);
            testData.put("resourceTypeName", profileName);
            testData.put("profileName", "NA");
            break;

        case "Contractor Admin":
            testData.put("useResourceTypeName", false);
            testData.put("resourceTypeName", "Contractor");
            getUserWithProfile(profileName);
            break;

        case "Contractor Technician":
            testData.put("useResourceTypeName", false);
            testData.put("resourceTypeName", "Contractor");
            break;

        case "Operations Director":
        case "RFM":
            List<Map<String, Object>> orgs = dbHelperOrganisation.getResourcesWithProfile(profileName);
            testData.put("resourceId", orgs.get(0).get("ResourceId"));
            testData.put("userName", orgs.get(0).get("UserName"));   // store for login step
            break;

        default :
            testData.put("useResourceTypeName", false);
            testData.put("resourceTypeName", "NA");
            getUserWithProfile(profileName);
        }
    }

    @Given("^a user with profile \"([^\"]*)\" and with Submitted Invoices and Credits$")
    public void a_user_with_profile_and_with_Submitted_Invoices_and_Credits(String profileName) throws Throwable {
        testData.addStringTag("profileName", profileName);
        getUserProfileWithSubmittedInvoicesAndCredits(profileName);
    }

    @Given("^a \"([^\"]*)\" from a team$")
    public void a_profileName_from_a_team(String profileName) throws Throwable {
        String team = dbHelper.getRandomTeamNameWithProfileName(profileName);

        testData.addStringTag("profileName", profileName);
        testData.addStringTag("team", team);

        appUser = applicationUserDao.getHelpdeskOperatorUserInTeam(profileName, team);
        runtimeState.scenario.write(appUser.getFirstName() + " " + appUser.getLastName() + " from " + team + " team.");
        testData.put("applicationUser", appUser);
        if(appUser==null){
            throw new PendingException("Cannot find suitable data for test");
        }
    }

    @Given("^an? \"([^\"]*)\"(?: user|) from a team has logged in$")
    public void user_with_profile_from_team_has_been_impersonated(String profileName) throws Throwable {
        String team = dbHelper.getRandomTeamNameWithProfileName(profileName);

        testData.addStringTag("profileName", profileName);
        testData.addStringTag("team", team);

        appUser = applicationUserDao.getHelpdeskOperatorUserInTeam(profileName, team);
        runtimeState.scenario.write(appUser.getFirstName() + " " + appUser.getLastName() + " from " + team + " team.");

        if (appUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        loginLogoutHelper.loginAndImpersonate(appUser);
    }

    public void loginWithUserName(String userName) throws Exception {
        appUser = this.applicationUserDao.getByUsername(userName);
        loginLogoutHelper.login(appUser);
    }

    @Given("^the user logs in$")
    public void user_logs_in() throws Exception {
        if (testData.getBoolean("dataset")) {
            appUser = applicationUserDao.getByUsername(testData.getString("userName"));

        } else if (testData.getString("applicationUser") != null) {
            appUser = new ApplicationUser();
            appUser = (ApplicationUser) testData.get("applicationUser");

        } else if (user.getUsername() != null) {
            appUser = applicationUserDao.getByUsername(user.getUsername());

        } else if (testData.getString("userName") != null) {
            appUser = applicationUserDao.getByUsername(testData.getString("userName"));
        }

        testData.put("userName", appUser.getUserName());
        loginLogoutHelper.login(appUser);
    }

    @Given("^the Contractor Admin logs in$")
    public void contractor_admin_logs_in() throws Exception {

        Map<String, Object> dbData = dbHelperUsers.getContractorAdminForJob(testData.getInt("jobReference"));
        testData.put("userName", dbData.get("UserName"));
        testData.put("userProfileName", dbData.get("UserProfileName"));

        user_logs_in();
    }

    @When("^the user attempts to login with an incorrect ((?:username|password))$")
    public void user_attempts_login_with_incorrect_details(String detail) throws Exception {

        getWebDriver().get(propertyHelper.getMercuryUrl());
        if (runtimeState.loginPage == null) {
            runtimeState.loginPage = new LoginPage(getWebDriver()).get();
        } else {
            runtimeState.loginPage.get();
        }

        String username, password;
        if ("username".equalsIgnoreCase(detail)) {
            username = "incorrect.username";
            password = "Password1";
        } else {
            username = appUser.getUserName();
            password = "incorrectPassword";
        }
        runtimeState.scenario.write("Username: " + username);
        runtimeState.scenario.write("Password: " + password);
        runtimeState.loginPage.login(new User(username, password));
        outputHelper.takeScreenshots();
    }

    @And("the user can re-enter details")
    public void the_user_can_reenter_details() throws Exception{
        user_attempts_login_with_incorrect_details("password");
    }

    @Given("^the user is on the mercury homepage$")
    public void the_user_is_on_mercury_homepage() {
        getWebDriver().get(propertyHelper.getMercuryUrl());
        if (runtimeState.loginPage == null) {
            runtimeState.loginPage = new LoginPage(getWebDriver()).get();
        } else {
            runtimeState.loginPage.get();
        }
    }

    @And("^users failed login count \"([^\"]*)\"$")
    public void users_failed_login_count(String detail) {
        appUser = this.applicationUserDao.getByUsername(testData.getString("userName"));
        int expectedFailCountValue = testData.getInt("accessFailedCount");
        int actualFailCountValue = appUser.getAccessFailedCount();
        if (detail.equalsIgnoreCase("increases")) {
            if (testData.getInt("accessFailedCount") == 2) {
                expectedFailCountValue = 0;
                assertTrue("LockoutEndDate not correct", appUser.getLockoutEndDateUtc().after(DateHelper.getTimestampPlusHours(22)));
            } else {
                expectedFailCountValue++;
                if(testData.getString("lockoutDate") == null) {
                    assertNull("Incorrect LockoutEndDate ", appUser.getLockoutEndDateUtc());
                } else {
                    assertEquals("LockoutEndDate not correct", testData.getString("lockoutDate"), appUser.getLockoutEndDateUtc().toString());
                }
            }
        }
        assertEquals("Login Fail Count Not correct", expectedFailCountValue, actualFailCountValue);
    }

    @And("^users failed login count resets to 0$")
    public void users_failed_login_count_resets_to_zero() {
        appUser = this.applicationUserDao.getByUsername(testData.getString("userName"));
        assertEquals("Login Fail Count did not reset to zero", 0, (int) appUser.getAccessFailedCount());
    }

    @Given("^a user with ((?:1|2)) failed login attempt and profile \"([^\"]*)\"$")
    public void user_with_failed_login_attempts(int failCount, String profileName) throws Throwable {

        appUser = applicationUserDao.getNonADUserWithFailedLoginAttempts(profileName, failCount);
        if (appUser == null) {
            appUser = applicationUserDao.getUserWithMatchingResourceAndPasswordExpiryDate(profileName);
            for (int i = 0; i < failCount; i++) {
                getWebDriver().get(propertyHelper.getMercuryUrl());
                if (runtimeState.loginPage == null) {
                    runtimeState.loginPage = new LoginPage(getWebDriver()).get();
                } else {
                    runtimeState.loginPage.get();
                }
                runtimeState.loginPage.login(new User(appUser.getUserName(), "incorrectPassword"));
                getWebDriver().manage().deleteAllCookies();
                getWebDriver().get(propertyHelper.getMercuryUrl() + "/Account/Logoff");
            }
        }


        testData.addStringTag("profileName", profileName);
        testData.addStringTag("userName", appUser.getUserName());
        testData.addIntegerTag("accessFailedCount", appUser.getAccessFailedCount());
        testData.addTimestampTag("lockoutDate", appUser.getLockoutEndDateUtc());
    }

    @Given("^I am on the landing page")
    public void chooseBrowser() throws Exception {
        getWebDriver().get("https://test.mercury.software");
        runtimeState.loginPage = new LoginPage(getWebDriver());
    }

    @When("^\"([^\"]*)\" logs in with password \"([^\"]*)\"$")
    public void user_login(String userName, String password) throws Exception {
        loginLogoutHelper.login(userName, password);
    }

    @ContinueNextStepsOnException
    @Then("^I will see the home tab$")
    public void i_will_see_the_home_tab() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    public void updateUserJobToScenario() {
        StringBuilder builder = new StringBuilder();
        builder.append("Test Data: ")
        .append(System.lineSeparator())
        .append("Username is ").append( userJob.getUserName())
        .append(System.lineSeparator())
        .append("Job Reference is ").append( userJob.getJobReference())
        .append(System.lineSeparator())
        .append("Resource Id is ").append( userJob.getResourceId());
        runtimeState.scenario.write(builder.toString());

    }

    @Given("^an \"([^\"]*)\" with username \"([^\"]*)\" and password \"([^\"]*)\" or encrypted password \"([^\"]*)\"$")
    public void an_with_username_and_password(String type, String username, String password, String encryptedPassword) throws Throwable {
        testData.put("username", username);
        testData.put("password", password);
        testData.put("encryptedPassword", encryptedPassword);
        testData.put("type", type);
    }

    @Given("^username \"([^\"]*)\" and password \"([^\"]*)\" or encrypted password \"([^\"]*)\"$")
    public void username_and_password(String username, String password, String encryptedPassword) throws Throwable {
        testData.put("username", username);
        testData.put("password", password);
        testData.put("encryptedPassword", encryptedPassword);
    }

    @Given("^username \"([^\"]*)\" is updated to be \"([^\"]*)\"$")
    public void username_is_updated_to_be(String username, String active) throws Throwable {
        testData.put("username", username);

        if ("ACTIVE".equalsIgnoreCase(active)) {
            testData.put("active", true);
        } else {
            testData.put("active", false);
        }
    }

    @When("^the user selects 'Forgotten Password\\?'$")
    public void the_user_selects_Forgotten_Password() throws Throwable {
        getWebDriver().get(propertyHelper.getMercuryUrl());
        runtimeState.loginPage = new LoginPage(getWebDriver()).get();
        outputHelper.takeScreenshot();
        runtimeState.passwordResetRequestPage = runtimeState.loginPage.selectForgottenPassword();
    }

    @When("^they request an Email Link on the Password Reset Request page$")
    public void they_request_an_Email_Link_on_the_Password_Reset_Request_page() throws Throwable {
        getUserWithProfile(testData.getString("profileName"));
        runtimeState.passwordResetRequestPage.enterUserName(testData.getString("userName"));
        outputHelper.takeScreenshot();
        runtimeState.passwordResetRequestPage.emailLink();
    }

    @Then("a Password Reset Request Confirmation is displayed")
    public void a_Password_Reset_Request_Confirmation_is_displayed() {
        outputHelper.takeScreenshot();
        assertTrue(runtimeState.passwordResetRequestPage.isPasswordResetRequestConfirmationDisplayed());
    }

    @Then("^an email is sent containing password reset link$")
    public void an_email_is_sent_containing_password_reset_link() throws Throwable {
        String body = emailHelper.getCommunicatorEmail("Password Reset");
        assertNotNull("Unexpected empty email", body);
        assertTrue(normalize(body).matches(".*https:.*/Account/ResetPassword.*"));
    }

    @Given("^the Reset Password link is selected on the email$")
    public void the_password_reset_link_is_selected_on_the_email() throws Throwable {
        String body = emailHelper.getCommunicatorEmail("Password Reset");
        String resetLink = "https" + StringUtils.substringBetween(normalize(body), "https", "\"");
        getWebDriver().get(resetLink);
        runtimeState.resetPasswordPage = new ResetPasswordPage(getWebDriver()).get();
        outputHelper.takeScreenshot();
    }

    @When("^the password is reset on the Reset Password page$")
    public void the_password_is_reset_on_the_Password_Reset_page() throws Throwable {
        String userName = testData.getString("userName");
        assertEquals("Not continuing because would not be able to resore password!!!", userName, appUser.getUserName());
        testData.put("appUser",  appUser);  // store so we can reset the password on teardown

        runtimeState.resetPasswordPage.enterUserName(userName);

        String newPassword = "newPassword123";
        runtimeState.scenario.write("Username: " + userName);
        runtimeState.scenario.write("New password: " + newPassword);
        testData.put("newPassword", newPassword);

        runtimeState.resetPasswordPage.enterPassword(newPassword);
        runtimeState.resetPasswordPage.enterPasswordConfirmation(newPassword);

        outputHelper.takeScreenshot();
        runtimeState.resetPasswordPage.reset();
    }

    @Then("^the user can log in with the new password$")
    public void the_user_can_log_in_with_the_new_password() throws Throwable {
        String userName = testData.getString("userName");
        String password = testData.getString("newPassword");
        runtimeState.scenario.write("Username: " + userName);
        runtimeState.scenario.write("New password: " + password);

        if (dbHelper.getDefaultPortal(userName).equals("Helpdesk")) {
            user_login(testData.getString("userName"), password);
        } else {
            getWebDriver().get(propertyHelper.getMercuryUrl());
            runtimeState.loginPage = new LoginPage(getWebDriver()).get();
            runtimeState.loginPage.loginPortal(new User(userName, password));
        }
    }
}