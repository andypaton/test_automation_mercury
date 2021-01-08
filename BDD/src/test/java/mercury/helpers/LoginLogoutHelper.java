package mercury.helpers;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.StringHelper.normalize;
import static mercury.helpers.TimeoutHelper.resetTimeout;
import static mercury.helpers.TimeoutHelper.setTimeout;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.database.dao.ApplicationUserDao;
import mercury.database.dao.ResourceDao;
import mercury.database.models.ApplicationUser;
import mercury.databuilders.TestData;
import mercury.databuilders.User;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperTestAutomationUsers;
import mercury.pageobject.web.LoginPage;
import mercury.pageobject.web.NotificationPage;
import mercury.pageobject.web.admin.AdminMenuPage;
import mercury.pageobject.web.admin.AdminResourcesAndUsersPage;
import mercury.pageobject.web.helpdesk.HelpdeskHomePage;
import mercury.pageobject.web.helpdesk.HelpdeskNavBar;
import mercury.pageobject.web.helpdesk.HelpdeskSearchBar;
import mercury.pageobject.web.portal.PortalNavBar;
import mercury.pageobject.web.storeportal.StorePortalHomePage;
import mercury.rest.RestAssuredHelper;
import mercury.runtime.RuntimeState;

@Component
public class LoginLogoutHelper {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private DbHelperTestAutomationUsers dbHelperTestAutomationUsers;
    @Autowired private ApplicationUserDao applicationUserDao;
    @Autowired private ResourceDao resourceDao;
    @Autowired private RestAssuredHelper restAssuredHelper;


    public void login(ApplicationUser applicationUser) throws Exception {
        try {
            loginAndImpersonate(applicationUser);

        } catch (Exception | AssertionError e) {
            // try one last time
            logout();
            loginAndImpersonate(applicationUser);
        }
    }

    public void logout() {
        try {
            // unlock job
            if ( runtimeState.helpdeskJobPage != null && runtimeState.helpdeskJobPage.isPageDisplayed() ) {
                dbHelperJobs.unlockJob(runtimeState.helpdeskJobPage.getJobReference());

            } else if (testData.getInt("jobReference") != null) {
                dbHelperJobs.unlockJob(testData.getInt("jobReference"));

            } else if (runtimeState.helpdeskViewIncidentPage != null && runtimeState.helpdeskViewIncidentPage.isPageDisplayed()) {
                // unlock incident
                dbHelperIncidents.unlockIncident(runtimeState.helpdeskViewIncidentPage.getIncidenceReference());
            }

            // Need to delete cookies first otherwise redirect to log off fails with too many failed redirect attempts
            getWebDriver().manage().deleteAllCookies();

            if (getWebDriver().getCurrentUrl().contains("azurewebsites.net")) {
                logoutStorePortal();
            } else {
                logoutViaRedirect();
            }

            // reset implicit wait period
            getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);

        } catch (Exception e) {
            closeAndQuitWebDriver();
        }

        // if original webdriver was Chromeheadless, and a Chrome webdriver has been created for current geolocation test then...
        String originalWebDriver = testData.getString("originalWebDriver");
        if ( originalWebDriver != null && !originalWebDriver.equals(System.getProperty("web.driver"))) {
            closeAndQuitWebDriver();
            System.setProperty("web.driver", originalWebDriver);
        }
    }

    private void logoutViaRedirect() {
        getWebDriver().get(propertyHelper.getMercuryUrl() +  "/Account/Logoff");
    }

    private void logoutStorePortal() throws IOException {
        runtimeState.storePortalHomePage = new StorePortalHomePage(getWebDriver()).get();
        runtimeState.storePortalHomePage.logout();
    }

    public Properties initializeUserProperties(String propertiesFileName) {
        try {
            Properties props = new Properties();
            ClassLoader classLoader = FileHelper.class.getClassLoader();
            URI uri  = classLoader.getResource(propertiesFileName).toURI();
            InputStream stream = Files.newInputStream(Paths.get(uri));
            props.load(stream);

            return props;

        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ApplicationUser loginAsTestAutomationUser() throws Exception {
        runtimeState.domainUser = System.getProperty("user.name").toLowerCase();
        ApplicationUser applicationUser = applicationUserDao.getByUsername(runtimeState.domainUser);
        if (applicationUser == null) {
            Properties props = initializeUserProperties("automation_users.properties");
            String alias = props.getProperty(runtimeState.domainUser.toLowerCase());
            applicationUser = applicationUserDao.getByUsername(alias);
        }

        assertNotNull("Unexpected user: " + runtimeState.domainUser + ". Update automation_users.properties", applicationUser);

        String userName = applicationUser.getUserName();
        String password = "City2019";
        login(userName, password);
        testData.put("userProfileId", applicationUser.getUserProfileId());

        testData.put("loginUser", normalize(applicationUser.getFirstName() + " " + applicationUser.getLastName()));
        testData.put("loginUserName", applicationUser.getUserName().trim());
        testData.put("loginUserPassword", password);

        runtimeState.scenario.write("login as username: " + userName + ", password: " + password);

        String profileName = dbHelper.getUserProfileName(applicationUser.getUserProfileId());
        testData.put("profileName", profileName);

        return applicationUser;
    }

    public void loginAndImpersonate(ApplicationUser applicationUser) throws Exception {
        testData.put("userName", applicationUser.getUserName());
        loginAsTestAutomationUser();

        String profileName = dbHelper.getUserProfileName(applicationUser.getUserProfileId());

        scenarioStartupInfo();

        // only need to impersonate if its NOT an IT user we are meant to be logging in as
        if (!profileName.equals("IT")) {
            runtimeState.scenario.write("Impersonate Username: " + applicationUser.getUserName() + ", profile: " + profileName + ", resourceId: " + applicationUser.getResourceId());

            setTimeout(60);

            if (propertyHelper.getEnv().equalsIgnoreCase("dev_uswm")) {
                setTimeout(300);
            }
            // Impersonate a user via the correct method
            if ("api".equalsIgnoreCase(System.getProperty("impersonationMethod"))) {
                impersonateUserViaAPI(applicationUser.getId());
                runtimeState.scenario.write("Impersonating via API");

            } else {
                impersonateUserViaGUI(applicationUser.getUserName());
                runtimeState.scenario.write("Impersonating via GUI");
            }
            resetTimeout();

            testData.put("impersonatedResourceName", resourceDao.getByResourceId(applicationUser.getResourceId()).getName());
            testData.put("impersonatedUserName", applicationUser.getUserName());
            testData.put("impersonatedUserId", applicationUser.getId());
        }

        testData.put("requestedBy", normalize(applicationUser.getFirstName() + " " + applicationUser.getLastName()));
        testData.put("profileName", profileName);
        testData.put("resourceId", applicationUser.getResourceId());
        testData.put("userName", applicationUser.getUserName());
        testData.put("userProfileId", applicationUser.getUserProfileId());

        try {
            setNavBar(applicationUser.getUserName());
        } catch (Error | Exception e) {
            // ignore
        }
    }


    public void loginAs(ApplicationUser applicationUser) throws Exception {
        String profileName = dbHelper.getUserProfileName(applicationUser.getUserProfileId());
        scenarioStartupInfo();

        String log = "Username: %s, Password: %s, profile: %s, resourceId: %d";
        runtimeState.scenario.write(String.format(log, applicationUser.getUserName(), "Password1", profileName, applicationUser.getResourceId()));

        testData.put("resourceId", applicationUser.getResourceId());
        login(applicationUser.getUserName(), "Password1");
    }

    public void openUrl() throws InterruptedException {
        runtimeState.loginPage = new LoginPage(getWebDriver()).get();
        //        runtimeState.loginPage.waitForCertainTimeInMinutes(1);
        getWebDriver().get(propertyHelper.getMercuryUrl());
    }

    public void openNotificationUrl() throws InterruptedException {
        //        runtimeState.loginPage.waitForCertainTimeInMinutes(1);
        getWebDriver().get(propertyHelper.getMercuryNotificationUrl());
        runtimeState.notificationPage = new NotificationPage(getWebDriver()).get();
    }

    public void login(String userName, String password) throws Exception {
        String defaultPortal = dbHelper.getDefaultPortal(userName);
        logger.debug("Logging on to " + defaultPortal +  " as user : " + userName);

        getWebDriver().get(propertyHelper.getMercuryUrl());
        runtimeState.loginPage = new LoginPage(getWebDriver()).get();

        runtimeState.deployedVersion = runtimeState.loginPage.getDeployedVersion();

        ApplicationUser applicationUser = applicationUserDao.getByUsername(userName);
        if (applicationUser.getPasswordExpiryDate() == null) {
            runtimeState.scenario.write("Updating PasswordExpiryDate to be future date for username: " + userName);
            dbHelperTestAutomationUsers.updatePasswordExpiryDate(userName);
        }

        if (dbHelper.getDefaultPortal(userName).equals("Helpdesk")) {
            // monitor tiles continually refresh - hence waiting time may max out
            setTimeout(1);
        }

        if (propertyHelper.getEnv().equalsIgnoreCase("dev_uswm")) {
            setTimeout(300);
        }

        runtimeState.loginPage.login(new User(userName, password));

        resetTimeout();

        restAssuredHelper.setCookies(getWebDriver());

        switch (defaultPortal) {
        case "Helpdesk" :
            runtimeState.helpdeskNavBar = new HelpdeskNavBar(getWebDriver()).get();
            runtimeState.helpdeskSearchBar = new HelpdeskSearchBar(getWebDriver()).get();
            runtimeState.helpdeskHomePage = new HelpdeskHomePage(getWebDriver()).get();
            break;

        default :
            runtimeState.portalNavBar =  new PortalNavBar(getWebDriver()).get();
            break;
        }
        outputHelper.takeScreenshots();
    }

    private void scenarioStartupInfo() {
        runtimeState.scenario.write("Scenario Starting:\nBrowser: " + outputHelper.getBrowserInfo() + "\nDate   : " + new Date() + "\nURL    : " + getWebDriver().getCurrentUrl());
    }

    private void setNavBar(String username) throws Exception {
        String defaultPortal = dbHelper.getDefaultPortal(username);
        switch (defaultPortal) {
        case "Helpdesk" :
            setTimeout(60);
            runtimeState.helpdeskNavBar = new HelpdeskNavBar(getWebDriver()).get();
            runtimeState.helpdeskSearchBar = new HelpdeskSearchBar(getWebDriver()).get();
            break;
        case "Portal" :
        case "Resource" :
        case "Field Management" :
            setTimeout(120);
            runtimeState.portalNavBar = new PortalNavBar(getWebDriver()).get();
            break;
        default :
            throw new Exception("Unexpected value: " + defaultPortal);
        }
    }

    public void impersonateUserViaGUI(String userName) throws Exception {
        runtimeState.adminHomePage = runtimeState.helpdeskNavBar.OpenAdminApp();
        runtimeState.adminHomePage.selectTile("Resources & Users");

        runtimeState.adminMenuPage = new AdminMenuPage(getWebDriver()).get();
        runtimeState.adminMenuPage.selectAdminMenu("Impersonate user");

        runtimeState.adminResourcesAndUsersPage = new AdminResourcesAndUsersPage(getWebDriver()).get();
        runtimeState.adminResourcesAndUsersPage.filterImpersonatorGridIsEqualTo("User Name", userName);

        runtimeState.adminResourcesAndUsersPage.selectFirstRowToImpersonate();
        runtimeState.adminResourcesAndUsersPage.confirmImpersonateUser();
    }

    public void impersonateUserViaAPI(String applicationUserId) throws ClientProtocolException, IOException, InterruptedException {
        logger.debug("Username before impersonation: " + POHelper.getCurrentUserName());
        postImpersonateUserApi(propertyHelper.getMercuryUrl(), applicationUserId);
        outputHelper.takeScreenshots();
        boolean success = POHelper.waitUntilConsoleLogContains("Impersonate return status: 200");
        assertTrue("Impersonate return status is NOT: 200", success);
        redirectHome();
    }

    public void redirectHome() throws InterruptedException {
        boolean success = false;
        int count = 0;
        do {
            logger.debug("Redirecting to Home ...");
            getWebDriver().get(propertyHelper.getMercuryUrl() + "/Account/RedirectHome");
            assertNotNull("Code requires loginUserName to be set before progressing!!!", testData.getString("loginUserName"));
            logger.debug("GET page completed, waiting on page to not contain username " + testData.getString("loginUserName") + " ...");
            success = POHelper.waitUntilPageSourceDoesNotContain("currentUsername = '" + testData.getString("loginUserName") + "'");
            if (!success) logger.debug("Failed to redirected!!!");
            count = count + 1;
        } while (!success && count < 4);
        if (success) logger.debug("Redirected to: " + getWebDriver().getCurrentUrl());
    }

    /**
     * Impersonates a user by using forcing a POST via javascript in the browser.
     * If we need to use a cookie then the following code should be used
     *  String fullcookie = "";
     *  for (org.openqa.selenium.Cookie cookie : getWebDriver().manage().getCookies()) {
     *      fullcookie = fullcookie + cookie.getName().concat("=").concat(cookie.getValue()).concat("; ");
     *  }
     *  logger.debug(fullcookie);
     *
     *  If we need to send more paramenters in the javascript post then the following would be used
     *  script = script.concat("xhr.setRequestHeader('Origin', '" + url +"');");
     *  script = script.concat("xhr.setRequestHeader('Referer', '"+ url + "/Resources/ImpersonateUser');");
     *  script = script.concat("xhr.setRequestHeader('Cookie', '"+ fullcookie +  "');");
     *  script = script.concat("xhr.setRequestHeader('Host', url);");

     * @param url - URL of the application
     * @param applicationUserId - user who should be impersonated
     * @throws InterruptedException
     */
    public static void postImpersonateUserApi(String url, String applicationUserId) throws InterruptedException {
        String script = "var xhr = new XMLHttpRequest();";
        script = script.concat("xhr.open('POST', '"+ url + "/Resources/Api/ImpersonateUserApi/Impersonate/" + applicationUserId + "', true);");
        script = script.concat("xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');");
        script = script.concat("xhr.onreadystatechange = function () { console.log(\"Impersonate return status: \" + xhr.status); };");
        script = script.concat("xhr.send();");
        logger.debug(script);
        ((JavascriptExecutor) getWebDriver()).executeScript(script);
    }

    private void closeAndQuitWebDriver() {
        try {
            getWebDriver().close();
            getWebDriver().quit();
        } catch (Exception e) {
            // ignore
        }
    }

}
