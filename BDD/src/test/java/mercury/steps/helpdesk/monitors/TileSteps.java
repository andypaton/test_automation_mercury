package mercury.steps.helpdesk.monitors;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertFalse;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.pageobject.web.admin.AdminAssetsPage;
import mercury.pageobject.web.admin.AdminComplianceConfigPage;
import mercury.pageobject.web.admin.AdminImportConfigurationPage;
import mercury.pageobject.web.admin.AdminIncidentConfigurationPage;
import mercury.pageobject.web.admin.AdminOrganisationPage;
import mercury.pageobject.web.admin.AdminPpmsPage;
import mercury.pageobject.web.admin.AdminResourcesAndUsersPage;
import mercury.pageobject.web.admin.AdminRulesEnginePage;
import mercury.pageobject.web.admin.AdminSystemFeatureTogglePage;
import mercury.pageobject.web.admin.companies.AdminCompaniesPage;
import mercury.pageobject.web.admin.finance.AdminFinancePage;
import mercury.pageobject.web.admin.jobs.AdminJobsPage;
import mercury.pageobject.web.admin.refrigerantGasUsage.AdminRefrigerantGasUsageSearchPage;
import mercury.pageobject.web.admin.sites.AdminSitesPage;
import mercury.pageobject.web.helpdesk.monitors.MonitorGrid;
import mercury.pageobject.web.helpdesk.monitors.MonitorsSettings;
import mercury.runtime.RuntimeState;

public class TileSteps {
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private OutputHelper outputHelper;


    public void initializeNextPage(String tile) throws Exception {
        switch(tile) {
        case "Refrigerant Gas Usage" :
            runtimeState.adminRefrigerantGasUsageSearchPage = new AdminRefrigerantGasUsageSearchPage(getWebDriver()).get();
            break;
        case "System Feature Toggle" :
            runtimeState.adminSystemFeatureTogglePage = new AdminSystemFeatureTogglePage(getWebDriver()).get();
            break;
        case "Finance" :
            runtimeState.adminFinancePage = new AdminFinancePage(getWebDriver()).get();
            break;
        case "Companies" :
            runtimeState.adminCompaniesPage = new AdminCompaniesPage(getWebDriver()).get();
            break;
        case "Rules Engine" :
            runtimeState.adminRulesEnginePage = new AdminRulesEnginePage(getWebDriver()).get();
            break;
        case "Incident Configuration" :
            runtimeState.adminIncidentConfigurationPage = new AdminIncidentConfigurationPage(getWebDriver()).get();
            break;
        case "Import Configuration" :
            runtimeState.adminImportConfigurationPage = new AdminImportConfigurationPage(getWebDriver()).get();
            break;
        case "Jobs":
            runtimeState.adminJobsPage = new AdminJobsPage(getWebDriver()).get();
            break;
        case "Resources & Users":
            runtimeState.adminResourcesAndUsersPage = new AdminResourcesAndUsersPage(getWebDriver()).get();
            break;
        case "Sites":
            runtimeState.adminSitesPage = new AdminSitesPage(getWebDriver()).get();
            break;
        case "Organisation":
            runtimeState.adminOrganisationPage = new AdminOrganisationPage(getWebDriver()).get();
            break;
        case "Assets":
            runtimeState.adminAssetsPage = new AdminAssetsPage(getWebDriver()).get();
            break;
        case "PPMs":
            runtimeState.adminPpmsPage = new AdminPpmsPage(getWebDriver()).get();
            break;
        case "Certificate Configuration":
            runtimeState.adminComplianceConfigPage = new AdminComplianceConfigPage(getWebDriver()).get();
            break;
        default :
            throw new Exception("Unexpected tile: " + tile);
        }
    }

    @When("^the \"([^\"]*)\" tile is selected$")
    public void a_random_tile_is_selected(String tile) throws Throwable {

        if (getWebDriver().getCurrentUrl().contains("ukrb") && tile.equalsIgnoreCase("One Stop Shop")) {
            tile = "Managed Contracts";
        }

        testData.put("tile", tile);
        outputHelper.takeScreenshots();

        if (getWebDriver().getCurrentUrl().contains("Admin")) {
            // Admin
            runtimeState.adminHomePage.selectTile(tile);
            initializeNextPage(tile);
            outputHelper.takeScreenshots();
        } else {
            // Helpdesk
            runtimeState.helpdeskHomePage.selectTile(tile);

            runtimeState.monitorsSettings = new MonitorsSettings(getWebDriver());
            runtimeState.monitorsSettings.expandSettings();

            runtimeState.monitorGrid = new MonitorGrid(getWebDriver()).get();
        }
    }

    @ContinueNextStepsOnException
    @Then("they cannot navigate to the Admin \"([^\"]*)\" tile$")
    public void a_random_tile_is_not_displayed(String tile) throws Throwable {
        String context;
        switch(tile) {
        case "Refrigerant Gas Usage" :
            context = "Admin";
            break;

        default:
            throw new Exception("Unexpected tile: " + tile);
        }

        outputHelper.takeScreenshots();
        if (context.contains("Admin") && runtimeState.helpdeskNavBar.getNavBarText().contains("Admin")) {
            // Admin
            runtimeState.adminHomePage = runtimeState.helpdeskNavBar.OpenAdminApp();
            assertFalse(runtimeState.adminHomePage.isTileDisplayed(tile));

        } else {
            // Helpdesk
            assertFalse(runtimeState.helpdeskHomePage.isTileDisplayed(tile));
        }
    }
}
