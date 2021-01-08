package mercury.steps.storeportal;

import static mercury.runtime.ThreadManager.getWebDriver;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import mercury.database.models.ApplicationUser;
import mercury.database.models.UserJob;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.storeportal.StorePortalLoginPage;
import mercury.runtime.RuntimeState;

public class StorePortalLoginSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;

    ApplicationUser appUser;
    UserJob userJob;

    @Given("^an active Store Portal user$")
    public void active_store_portal_user() throws Exception {
        String userName = dbHelperSites.getRandomSiteForStorePortal();
        testData.put("storePortalUserName", userName);
        String password = "Password1";
        testData.put("storePortalPassword", password);
        runtimeState.scenario.write("Username: " + userName + ", Password: " + password);
    }

    @When("^the user has logged in$")
    public void user_has_logged_in() throws Exception {
        runtimeState.storePortalLoginPage = new StorePortalLoginPage(getWebDriver()).get();
        runtimeState.storePortalHomePage = runtimeState.storePortalLoginPage.login(testData.getString("storePortalUserName"), testData.getString("storePortalPassword"));
        outputHelper.takeScreenshots();
    }

}