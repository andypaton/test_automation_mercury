package mercury.steps.admin;

import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.FaultPriorityMappingDao;
import mercury.database.dao.SiteViewDao;
import mercury.database.models.FaultPriorityMapping;
import mercury.database.models.SiteView;
import mercury.databuilders.TestData;
import mercury.helpers.LoginLogoutHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.admin.AdminEditIncidentCriteriaPage;
import mercury.pageobject.web.admin.AdminNewIncidentCriteriaPage;
import mercury.runtime.RuntimeState;
import mercury.steps.LoginSteps;
import mercury.steps.helpdesk.monitors.TileSteps;

public class AdminIncidentConfigurationSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private SiteViewDao siteViewDao;
    @Autowired private FaultPriorityMappingDao faultPriorityMappingDao;
    @Autowired private LoginSteps loginSteps;
    @Autowired private TileSteps tileSteps;
    @Autowired private LoginLogoutHelper loginLogoutHelper;

    @Given("^the Linked Incident Criteria is set$")
    public void theLinkedIncidentCriteriaIsSet() throws Throwable {
        loginSteps.a_user_with_role_has_logged_in("Mercury_Admin_Incidents");
        runtimeState.adminHomePage = runtimeState.helpdeskNavBar.OpenAdminApp();
        tileSteps.a_random_tile_is_selected("Incident Configuration");
        testData.put("linkedIncidentType", "Forced");
        testData.put("siteBrand", "No");
        testData.put("siteType", "No");
        testData.put("activeCriteria", "Yes");
        newCriteriaIsAdded();
        loginLogoutHelper.logout();
    }

    @When("^a new criteria is added without completing mandatory fields$")
    public void newCriteriaIsAddedWithoutMandatoryFields() throws Throwable {
        runtimeState.adminIncidentConfigurationPage.addNewCriteria();
        runtimeState.adminNewIncidentCriteriaPage = new AdminNewIncidentCriteriaPage(getWebDriver()).get();
        runtimeState.adminNewIncidentCriteriaPage.clickTabKeyOnDropdown("Potential/Forced");
        runtimeState.adminNewIncidentCriteriaPage.clickTabKeyOnDropdown("Asset Sub Type");
        runtimeState.adminNewIncidentCriteriaPage.clickTabKeyOnDropdown("Incident Type");
        runtimeState.adminNewIncidentCriteriaPage.clickTabKeyOnDropdown("Fault Type");
        outputHelper.takeScreenshots();
    }

    @When("^a new criteria is added$")
    public void newCriteriaIsAdded() throws Throwable {
        runtimeState.adminIncidentConfigurationPage.addNewCriteria();
        runtimeState.adminNewIncidentCriteriaPage = new AdminNewIncidentCriteriaPage(getWebDriver()).get();

        String linkedIncidentType = null;
        if (testData.get("linkedIncidentType") != null && testData.get("linkedIncidentType").equals("Forced")) {
            linkedIncidentType = "Forced";
        } else {
            linkedIncidentType = dbHelperIncidents.getRandomLinkedIncidentType();
        }
        runtimeState.adminNewIncidentCriteriaPage.selectDropdownOption("Potential/Forced", linkedIncidentType);
        testData.put("linkedIncidentType", linkedIncidentType);
        runtimeState.scenario.write("linkedIncidentType: " + linkedIncidentType);

        if (testData.get("siteBrand") == null) {
            String siteBrand = dbHelperSites.getRandomSiteBrand();
            runtimeState.adminNewIncidentCriteriaPage.selectDropdownOption("Site Brand", siteBrand);
            testData.put("siteBrand", siteBrand);
            runtimeState.scenario.write("siteBrand: " + siteBrand);
        }

        if (testData.get("siteType") == null) {
            String siteType = dbHelperSites.getRandomSiteType();
            runtimeState.adminNewIncidentCriteriaPage.selectDropdownOption("Site Type", siteType);
            testData.put("siteType", siteType);
            runtimeState.scenario.write("siteType: " + siteType);
        }

        Map<String, Object> assetSubType = dbHelper.getRandomAssetSubTypeWithFormat();
        runtimeState.adminNewIncidentCriteriaPage.selectDropdownOption("Asset Sub Type", assetSubType.get("AssetSubType").toString());
        String subType = StringUtils.substringBefore(assetSubType.get("AssetSubType").toString(), "(");
        testData.put("assetSubType", subType);
        testData.put("assetSubTypeWithFormat", assetSubType);
        runtimeState.scenario.write("assetSubType: " + assetSubType);

        int assetSubTypeId = (int) assetSubType.get("AssetSubTypeId");
        String classification = dbHelper.getRandomAssetClassificationForAssetSubType(assetSubTypeId);
        if (classification.equalsIgnoreCase("")) {
            classification = "None";
        }
        runtimeState.adminNewIncidentCriteriaPage.selectDropdownOption("Classification", classification);
        testData.put("classification", classification);
        runtimeState.scenario.write("classification: " + classification);

        String incidentType = dbHelperIncidents.getRandomIncidentType();
        runtimeState.adminNewIncidentCriteriaPage.selectDropdownOption("Incident Type", incidentType);
        testData.put("incidentType", incidentType);
        runtimeState.scenario.write("incidentType: " + incidentType);

        //Selecting a random fault type as the DB value was not being found on the dropdown causing intermittent failure
        String faultType = runtimeState.adminNewIncidentCriteriaPage.selectRandomDropdownOption("Fault Type");
        testData.put("faultType", faultType);
        runtimeState.scenario.write("faultType: " + faultType);

        String isCriteriaActive = null;
        if (testData.get("activeCriteria") != null && testData.get("activeCriteria").equals("Yes")) {
            isCriteriaActive = "Yes";
        } else {
            isCriteriaActive = "No";
        }
        runtimeState.adminNewIncidentCriteriaPage.selectActiveOrInactive(isCriteriaActive);
        testData.put("isCriteriaActive", isCriteriaActive);
        runtimeState.scenario.write("Incident Criteria is " + isCriteriaActive);

        outputHelper.takeScreenshots();
        runtimeState.adminNewIncidentCriteriaPage.save();
    }

    @When("^a criteria is ((?:edited|edited to have empty mandatory fields))$")
    public void criteriaIsEdited(String editAction) throws Throwable {
        if (!dbHelperIncidents.isThereAnInactiveIncident()) {
            int linkedIncidentCriterionTypeId = dbHelperIncidents.getRandomLinkedIncidentCriterionTypeId();
            SiteView siteView = siteViewDao.getRandomSite();
            FaultPriorityMapping faultPriorityMapping = faultPriorityMappingDao.getFaultPriorityMappingForSite(siteView.getId(), 0);
            int incidentTypeId = dbHelperIncidents.getRandomIncidentTypeId();
            dbHelperIncidents.insertLinkedIncidentCriterion(linkedIncidentCriterionTypeId, siteView.getBrandId(), siteView.getSiteTypeId(), faultPriorityMapping.getAssetSubTypeId(), faultPriorityMapping.getAssetClassificationId(), faultPriorityMapping.getFaultTypeId(), incidentTypeId, 0);
        }

        POHelper.refreshPage();
        runtimeState.adminIncidentConfigurationPage.selectInactiveTab();

        runtimeState.adminEditIncidentCriteriaPage = runtimeState.adminIncidentConfigurationPage.editIncidentCriteria();

        if (editAction.equals("edited to have empty mandatory fields")) {
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Potential/Forced", "None");
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Asset Sub Type", "None");
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Incident Type", "None");
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Fault Type", "None");

            outputHelper.takeScreenshots();

        } else {

            String linkedIncidentType = dbHelperIncidents.getRandomLinkedIncidentType();
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Potential/Forced", linkedIncidentType);
            testData.put("linkedIncidentType", linkedIncidentType);
            runtimeState.scenario.write("linkedIncidentType: " + linkedIncidentType);

            String siteBrand = dbHelperSites.getRandomSiteBrand();
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Site Brand", siteBrand);
            testData.put("siteBrand", siteBrand);
            runtimeState.scenario.write("siteBrand: " + siteBrand);

            String siteType = dbHelperSites.getRandomSiteType();
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Site Type", siteType);
            testData.put("siteType", siteType);
            runtimeState.scenario.write("siteType: " + siteType);

            //Selecting a random asset sub type as the DB value was not being found on the dropdown causing intermittent failure
            String assetSubType = runtimeState.adminEditIncidentCriteriaPage.selectRandomDropdownOption("Asset Sub Type");
            String subType = StringUtils.substringBefore(assetSubType, "(");
            testData.put("assetSubType", subType);
            runtimeState.scenario.write("assetSubType: " + assetSubType);

            int assetSubTypeId = dbHelper.getAssetSubTypeId(subType);
            String classification = dbHelper.getRandomAssetClassificationForAssetSubType(assetSubTypeId);
            if(classification.equalsIgnoreCase("")) {
                classification = "None";
            }
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Classification", classification);
            testData.put("classification", classification);
            runtimeState.scenario.write("classification: " + classification);

            String incidentType = dbHelperIncidents.getRandomIncidentType();
            runtimeState.adminEditIncidentCriteriaPage.selectDropdownOption("Incident Type", incidentType);
            testData.put("incidentType", incidentType);
            runtimeState.scenario.write("incidentType: " + incidentType);

            //Selecting a random fault type as the DB value was not being found on the dropdown causing intermittent failure
            String faultType = runtimeState.adminEditIncidentCriteriaPage.selectRandomDropdownOption("Fault Type");
            testData.put("faultType", faultType);
            runtimeState.scenario.write("faultType: " + faultType);

            String isCriteriaActive = "No";
            runtimeState.adminEditIncidentCriteriaPage.selectActiveOrInactive(isCriteriaActive);
            testData.put("isCriteriaActive", isCriteriaActive);
            runtimeState.scenario.write("Incident Criteria is " + isCriteriaActive);

            outputHelper.takeScreenshots();
            runtimeState.adminEditIncidentCriteriaPage.save();
        }
    }

    @ContinueNextStepsOnException
    @Then("^the new criteria is added to the relevant table$")
    public void newCriteriaAddedToTable() throws Throwable {
        int active;
        POHelper.refreshPage();

        if(testData.get("isCriteriaActive").equals("Yes")) {
            runtimeState.adminIncidentConfigurationPage.selectActiveTab();
            active = 1;
        } else {
            runtimeState.adminIncidentConfigurationPage.selectInactiveTab();
            active = 0;
        }

        String potentialForced = normalize(testData.getString("linkedIncidentType"));
        String siteBrand = normalize(testData.getString("siteBrand"));
        String siteType = normalize(testData.getString("siteType"));
        String assetSubType = normalize(testData.getString("assetSubType"));
        String classification = normalize(testData.getString("classification"));
        String faultType = normalize(testData.getString("faultType"));
        String incidentType = normalize(testData.getString("incidentType"));

        List<Map<String, Object>> gridDb = dbHelper.getIncidentCriteriaGrid(active, potentialForced, siteBrand, siteType, assetSubType, classification, faultType, incidentType);

        runtimeState.adminIncidentConfigurationPage.setPotentialForcedFilter(potentialForced);
        runtimeState.adminIncidentConfigurationPage.setSiteBrandFilter(siteBrand);
        runtimeState.adminIncidentConfigurationPage.setSiteTypeFilter(siteType);
        runtimeState.adminIncidentConfigurationPage.setAssetSubTypeFilter(assetSubType);
        if(!classification.equals("None")) {
            runtimeState.adminIncidentConfigurationPage.setClassificationFilter(classification);
        }
        runtimeState.adminIncidentConfigurationPage.setFaultTypeFilter(faultType);
        runtimeState.adminIncidentConfigurationPage.setIncidentTypeFilter(incidentType);

        Grid grid = runtimeState.adminIncidentConfigurationPage.getGrid();
        outputHelper.takeScreenshots();
        assertNotNull("Unexpected Null Grid", grid);

        assertEquals("Incorrect result in Potential/Forced column", StringUtils.substringBefore(grid.getRows().get(0).getCell("Potential/Forced").getText(), " Edit"), gridDb.get(0).get("Potential/Forced"));
        assertEquals("Incorrect result in Site Brand column", grid.getRows().get(0).getCell("Site Brand").getText(), gridDb.get(0).get("Site Brand"));
        assertEquals("Incorrect result in Site type column", grid.getRows().get(0).getCell("Site type").getText(), gridDb.get(0).get("Site type"));
        assertEquals("Incorrect result in Asset Sub type column", normalize(grid.getRows().get(0).getCell("Asset Sub type").getText()), normalize(gridDb.get(0).get("Asset Sub type").toString()));
        assertEquals("Incorrect result in Classification column", grid.getRows().get(0).getCell("Classification").getText(), gridDb.get(0).get("Classification"));
        assertEquals("Incorrect result in Fault type column", grid.getRows().get(0).getCell("Fault type").getText(), gridDb.get(0).get("Fault type"));
        assertEquals("Incorrect result in Incident type column", grid.getRows().get(0).getCell("Incident type").getText(), gridDb.get(0).get("Incident type"));
    }

    @ContinueNextStepsOnException
    @Then("^an audit history is now present$")
    public void auditHistoryIsNowPresent() throws Throwable {
        runtimeState.adminIncidentConfigurationPage.editIncidentCriteria();
        runtimeState.adminEditIncidentCriteriaPage = new AdminEditIncidentCriteriaPage(getWebDriver()).get();

        runtimeState.adminEditIncidentCriteriaPage.expandAuditHistory();
        outputHelper.takeScreenshots();

        String updatedOn = runtimeState.adminEditIncidentCriteriaPage.getUpdatedOn();
        String updatedBy =  runtimeState.adminEditIncidentCriteriaPage.getUpdatedOn();
        List<String> descriptions = runtimeState.adminEditIncidentCriteriaPage.getDescriptions();

        assertNotNull("Updated On is not present", updatedOn);
        assertNotNull("Updated By is not present", updatedBy);
        assertNotNull("Description is not present", descriptions);

        dbHelper.deleteLinkedIncidentCriterionAddedByAutoTest();
    }

    @ContinueNextStepsOnException
    @Then("^the forced incidents home page is displayed as expected$")
    public void forcedIncidentsHomePageDisplayedAsExpected() throws Throwable {
        String[] expectedHeaders = { "Potential/Forced", "Site Brand", "Site type", "Asset Sub type", "Classification", "Fault type", "Incident type" };

        Grid activeGrid = runtimeState.adminIncidentConfigurationPage.getGrid();
        GridHelper.assertGridHeaders(activeGrid.getHeaders(), expectedHeaders);
        outputHelper.takeScreenshots();

        runtimeState.adminIncidentConfigurationPage.selectInactiveTab();

        Grid inactiveGrid = runtimeState.adminIncidentConfigurationPage.getGrid();
        GridHelper.assertGridHeaders(inactiveGrid.getHeaders(), expectedHeaders);
        outputHelper.takeScreenshots();
    }

}
