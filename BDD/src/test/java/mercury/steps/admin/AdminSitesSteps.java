package mercury.steps.admin;

import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.SiteViewDao;
import mercury.database.models.SiteView;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.admin.sites.AdminSiteTypesPage;
import mercury.pageobject.web.admin.sites.AdminSitesPage;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;

public class AdminSitesSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private SiteViewDao siteViewDao;
    @Autowired private OutputHelper outputHelper;
    @Autowired private CommonSteps commonSteps;
    @Autowired private DbHelper dbHelper;

    private void searchForSiteInAdminSitesPage(String name, String code) {
        runtimeState.adminSitesPage.setSiteNameFilter(name);
        runtimeState.adminSitesPage.setSiteCodeFilter(code);
    }

    @When("^user searches for an \"([^\"]*)\" site$")
    public void user_searches_for_an_site(String status) throws Throwable {
        List<Map<String, Object>> dbData = dbHelperSites.getRandomSiteNameAndSiteCodeInStatus(status);
        testData.put("status", status);

        if (status.equalsIgnoreCase("Inactive")) {
            if (dbData == null || dbData.isEmpty()) {
                // Add new Inactive site
                runtimeState.scenario.write("Adding a new Inactive site.");
                testData.put("siteStatus", status);
                the_new_site_information_is_entered();
                runtimeState.adminEditSitePage.clickSaveButton();
                commonSteps.the_popup_alert_is_confirmed("Site Administration");
                dbData = dbHelperSites.getSiteNameAndSiteCode(testData.getString("siteName"));
            }
            runtimeState.adminSitesPage.clickTab(status);
        }

        String name = dbData.get(0).get("Name").toString();
        String code = dbData.get(0).get("Site Code").toString();
        testData.put("siteName", name);
        testData.put("siteCode", code);
        testData.putListMap("dbData", dbData);
        searchForSiteInAdminSitesPage(name, code);
    }

    public void assertDataInSitesGrid(Grid grid, String page) throws Exception {

        assertNotNull("There is no data on the grid table. ", grid);

        //Verify the data on the grid table is displayed in the correct columns
        if(grid.getRows().size() > 0) {
            for (Row row : grid.getRows()) {
                if (page.equals("Site Type")) {
                    runtimeState.scenario.write("Asserting that the data on the grid is displayed in the correct colums: ");

                    runtimeState.scenario.write("Asserting the Icon displayed is: " + testData.get("iconPath"));
                    assertTrue("Unexpected Icon ", row.getCell("Icon").getWebElement().getAttribute("src").contains((CharSequence) testData.get("iconPath")));

                    runtimeState.scenario.write("Asserting the Area is: " + testData.get("area"));
                    assertEquals("Unexpected Area ", normalize(testData.get("area").toString()), row.getCell("Area").getText());

                    runtimeState.scenario.write("Asserting the Location is: " + testData.get("locationName"));
                    assertEquals("Unexpected Location ", normalize(testData.get("locationName").toString()), row.getCell("Location").getText());

                    runtimeState.scenario.write("Asserting the Sub Location is: " + testData.get("subLocationName"));
                    assertEquals("Unexpected Sub Location ", normalize(testData.get("subLocationName").toString()), row.getCell("Sub Location").getText());

                    runtimeState.scenario.write("Asserting 'All' Checkbox value is: " + testData.get("allCheckbox"));
                    assertEquals("Unexpected 'All' Check box value ", testData.get("allCheckbox"), row.getCell("All").isCheckBoxChecked());

                    runtimeState.scenario.write("Asserting 'Optional' Checkbox value is: " + testData.get("optionalCheckbox"));
                    assertEquals("Unexpected 'Optional' Check box value ", testData.get("optionalCheckbox"), row.getCell("Optional").isCheckBoxChecked());
                } else {
                    assertTrue("Unexpected Site Type ", row.getCell("Type").getText().contains(testData.get("SiteType").toString()));
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void assertDataInSitesGrid(Grid grid, List<Map<String, Object>> dbData, String page) throws Exception {

        // Compare both sets of data are the same size
        assertEquals("Unexpected number of records", dbData.size(), grid.getRows().size());

        // Verify both datasets are the same
        Iterator it = dbData.iterator();
        if (dbData.size() > 0) {
            for (Row row : grid.getRows()) {
                Map<String, Object> map = (Map<String, Object>) it.next();

                if (page.equals("Sites")) {
                    assertTrue("Unexpected Name ", row.getCell("Name").getText().contains(normalize(map.get("Name").toString())));
                    assertTrue("Unexpected Site Code ", row.getCell("Site Code").getText().contains(normalize(map.get("Site Code").toString())));
                } else {
                    assertTrue("Unexpected Site Type ", row.getCell("Type").getText().contains(map.get("SiteType").toString()));
                }
            }
        }
    }

    private void fillCoreDetailsForm() {
        String siteName = "TestAuto" + System.currentTimeMillis();
        testData.put("siteName", siteName);
        runtimeState.adminEditSitePage.enterValueInCoreDetailsFormTextBox("Name", siteName);
        runtimeState.adminEditSitePage.enterValueInCoreDetailsFormTextBox("Code", siteName);
        testData.put("siteCode", siteName);
        String siteType = dbHelperSites.getRandomSiteType();
        String siteTypeModified = siteType.contains("'") ? siteType.split("'")[0] + "''" + siteType.split("'")[1] : siteType;
        testData.put("siteType", siteTypeModified);
        runtimeState.adminEditSitePage.selectOptionFromCoreDetailsFormDropdown("Type", siteType);
        String brand = dbHelperSites.getRandomSiteBrand();
        String brandModified = brand.contains("'") ? brand.split("'")[0] + "''" + brand.split("'")[1] : brand;
        testData.put("brand", brandModified);
        runtimeState.adminEditSitePage.selectOptionFromCoreDetailsFormDropdown("Brand", brand);
        String siteStatus = (testData.getString("siteStatus") != null && testData.getString("siteStatus").equals("Inactive")) ? "Sold" : "Unoccupied";
        runtimeState.adminEditSitePage.selectOptionFromCoreDetailsFormDropdown("Status", siteStatus);
        testData.put("siteStatus", siteStatus);
        runtimeState.adminEditSitePage.clearCoreDetailsFormDates();
    }

    private void fillAddressForm() {
        runtimeState.adminEditSitePage.openAddressForm();
        String town = DataGenerator.generateRandomWord();
        testData.put("town", town);
        runtimeState.adminEditSitePage.enterValueInAddressFormTextBox("Town", town);
        String country = dbHelperSites.getRandomCountry();
        testData.put("country", country);
        runtimeState.adminEditSitePage.selectOptionFromAddressFormDropdown("Country", country);
        String postcodeTextBox = LOCALE.equals("en-GB") ? "postcode" : "zipcode";
        String postcode = DataGenerator.GenerateRandomString(6, 6, 0, 3, 3, 0);
        testData.put("postcode", postcode);
        runtimeState.adminEditSitePage.enterValueInAddressFormTextBox(postcodeTextBox, postcode);
    }

    private void fillGeographicalInformationForm() {
        runtimeState.adminEditSitePage.openGeographicalInformationForm();
        String latitude = String.valueOf(DataGenerator.GenerateRandomDouble(0.00, 100.00));
        runtimeState.adminEditSitePage.enterValueInGeographicalInformationFormTextBox("Latitude", latitude);
        String longitude = String.valueOf(DataGenerator.GenerateRandomDouble(0.00, 100.00));
        runtimeState.adminEditSitePage.enterValueInGeographicalInformationFormTextBox("Longitude", longitude);
        runtimeState.adminEditSitePage.selectRandomOptionFromGeographicalInformationFormDropdown("Timezone");
    }

    private void fillOpeningHoursForm() {
        runtimeState.adminEditSitePage.openOpeningHoursForm();
        runtimeState.adminEditSitePage.selectAll24HrsCheckboxesInOpeningHoursForm();
    }

    private void enableSaveButtonInEditSitePage() {
        if (!runtimeState.adminEditSitePage.isSaveButtonEnabled()) {
            if (runtimeState.adminEditSitePage.isAddressFormCollapsed()) {
                runtimeState.adminEditSitePage.openAddressForm();
            }
            if (runtimeState.adminEditSitePage.isPhoneNumberErrorDisplayed()) {
                runtimeState.adminEditSitePage.removeContactInfo();
            }
        }
    }

    @When("^the new site information is entered$")
    public void the_new_site_information_is_entered() throws Throwable {
        runtimeState.adminEditSitePage = runtimeState.adminSitesPage.clickAddNewSiteButton();
        fillCoreDetailsForm();
        fillAddressForm();
        fillGeographicalInformationForm();
        fillOpeningHoursForm();
    }

    @ContinueNextStepsOnException
    @Then("^the site \"([^\"]*)\" (?:added|saved) to the database$")
    public void the_site_added_to_the_database(String added) throws Throwable {
        boolean expectedResult = added.equals("is") ? true : false;
        boolean actualResult = dbHelperSites.isSitePresent(testData.getString("siteName"), testData.getString("siteCode"), testData.getString("siteType"), testData.getString("brand"), testData.getString("siteStatus"), testData.getString("town"), testData.getString("country"), testData.getString("postcode"));
        String excpetion = added.equals("is") ? " is not" : " is";
        assertEquals("Site: " + testData.getString("siteName") + excpetion + " present ! ", expectedResult, actualResult);
    }

    @ContinueNextStepsOnException
    @Then("^the site \"([^\"]*)\" searchable in the Sites page$")
    public void the_site_searchable_in_the_sites_page(String searchable) throws Throwable {
        runtimeState.adminSitesPage = new AdminSitesPage(getWebDriver()).get();
        if (testData.getString("editSite") != null && testData.getString("editSite").equals("edited")) {
            runtimeState.adminSitesPage.clickTab("Inactive");
            testData.put("siteStatus", "Inactive");
        }

        searchForSiteInAdminSitesPage(testData.getString("siteName"), testData.getString("siteCode"));
        int expectedRows = searchable.equals("is") ? 1 : 0;
        assertEquals("Unexpected Rows! ", expectedRows, runtimeState.adminSitesPage.getGrid().getRows().size());

        // Delete site added by auto test
        if (searchable.equals("is")) {
            if (testData.getString("editSite") == null || !testData.getString("editSite").equals("edited")) {
                dbHelperSites.deleteSiteLocationMappingAddedByAutoTest(testData.getString("siteName"));
                dbHelperSites.deleteSiteOpeningHoursAddedByAutoTest(testData.getString("siteName"));
                dbHelperSites.deleteSiteAddedByAutoTest(testData.getString("siteName"));
            }
        }
    }

    @When("^a site is ((?:edited|edited with already existing site name))$")
    public void a_site_is_edited_with_already_existing_site_name(String editSite) throws Throwable {
        testData.put("editSite", editSite);
        if (editSite.equals("edited with already existing site name")) {
            user_searches_for_an_site("Active");
            runtimeState.adminEditSitePage = runtimeState.adminSitesPage.clickEditSiteLink();

            String siteName = dbHelperSites.getRandomSiteName();
            runtimeState.adminEditSitePage.enterValueInCoreDetailsFormTextBox("Name", siteName);
        } else {
            testData.put("siteStatus", "Inactive");
            the_new_site_information_is_entered();
            runtimeState.adminEditSitePage.clickSaveButton();
            commonSteps.the_popup_alert_is_confirmed("Site Administration");
            POHelper.refreshPage();
            runtimeState.adminSitesPage.clickTab("Inactive");
            searchForSiteInAdminSitesPage(testData.getString("siteName"), testData.getString("siteName"));
            runtimeState.adminEditSitePage = runtimeState.adminSitesPage.clickEditSiteLink();
            runtimeState.adminEditSitePage.enterValueInCoreDetailsFormTextBox("Code", "123");
            testData.put("siteCode", "123");
        }

        enableSaveButtonInEditSitePage();
    }

    @When("^a site is made Inactive$")
    public void a_site_is_made_inactive() throws Throwable {
        user_searches_for_an_site("Active");
        runtimeState.adminEditSitePage = runtimeState.adminSitesPage.clickEditSiteLink();

        SiteView siteView = siteViewDao.getSiteByName(testData.getString("siteName"));
        Integer originalStatusId = siteView.getSiteStatusId();
        testData.put("originalStatusId", originalStatusId);
        Integer siteId = siteView.getId();
        testData.put("siteId", siteId);

        String[] siteStatuses = { "Sold", "Demolished" };
        String siteStatus = siteStatuses[new Random().nextInt(siteStatuses.length)];
        runtimeState.adminEditSitePage.selectOptionFromCoreDetailsFormDropdown("Status", siteStatus);
        enableSaveButtonInEditSitePage();
        outputHelper.takeScreenshot();
        runtimeState.adminEditSitePage.clickSaveButton();

    }

    @ContinueNextStepsOnException
    @Then("^the site now appears in the Inactive tab$")
    public void the_site_now_appears_in_the_inactive_tab() throws Throwable {
        runtimeState.adminSitesPage = new AdminSitesPage(getWebDriver()).get();
        runtimeState.adminSitesPage.clickTab("Inactive");
        searchForSiteInAdminSitesPage(testData.getString("siteName"), testData.getString("siteCode"));
        assertEquals("Unexpected Rows! ", 1, runtimeState.adminSitesPage.getGrid().getRows().size());

        // Reset site status to original
        dbHelperSites.updateSiteStatusAndActiveColumns(testData.getInt("siteId"), testData.getInt("originalStatusId"), 1);
    }

    @When("^the user clicks on the Edit link of a SiteType$")
    public void the_user_clicks_on_the_edit_link_of_a_sitetype() throws Throwable {
        runtimeState.adminSiteTypesPage = new AdminSiteTypesPage(getWebDriver()).get();
        String siteType = dbHelperSites.getSiteTypeMappedtoMinimumNumberOfLocations();
        String siteTypeModified = siteType.contains("'") ? siteType.split("'")[0] + "''" + siteType.split("'")[1] : siteType;
        testData.put("siteTypeModified", siteTypeModified);
        testData.put("siteType", siteType);
        runtimeState.scenario.write("Searching for Site Type: " + siteType);
        runtimeState.adminSiteTypesPage.searchForSiteType(siteType);
        runtimeState.adminEditSiteTypePage = runtimeState.adminSiteTypesPage.clickEditLink();
    }

    @When("^searches for a location with a linked icon$")
    public void searches_for_a_location_with_a_linked_icon() throws Throwable {
        Map<String, Object> siteTypesLocations = dbHelperSites.getRandomLocationWithLinkedIcon(testData.getString("siteTypeModified"));
        String iconPath = siteTypesLocations.get("Icon").toString();
        String area = dbHelperSites.getAreaName();
        String locationName = siteTypesLocations.get("Location").toString();
        int locationId = (int) siteTypesLocations.get("Id");
        String subLocationName = dbHelperSites.getSubLocationName(locationId);
        testData.put("iconPath", iconPath);
        testData.put("area", area);
        testData.put("locationName", locationName);
        if (subLocationName == null) {
            testData.put("subLocationName", "N/A");
        } else{
            testData.put("subLocationName", subLocationName);
        }

        String checkbox = siteTypesLocations.get("IsOptional").toString().equals("1") ? "Optional" : "All";
        if (checkbox.equalsIgnoreCase("All")) {
            testData.put("allCheckbox", true);
            testData.put("optionalCheckbox", false);
        } else {
            testData.put("allCheckbox", false);
            testData.put("optionalCheckbox", true);
        }
        runtimeState.scenario.write("Searching for location: " + locationName);
        runtimeState.adminEditSiteTypePage.setColumnHeaderFilter("Location", "Is equal to", locationName);
    }

    @When("^searches for a ((?:location|location with linked icon)) ((?:mapped|not mapped)) to the site type$")
    public void searches_for_a_location_with_linked_icon_mapped_to_the_site_type(String location, String mapping) throws Throwable {
        boolean mapped = mapping.equals("mapped") ? true : false;
        boolean hasIcon = location.equals("location with linked icon") ? true : false;
        Map<String, Object> siteTypesLocations = dbHelperSites.getSiteTypesLocationsMapping(testData.getString("siteTypeModified"), mapped, hasIcon);
        String locationName = siteTypesLocations.get("Location").toString();
        testData.put("locationName", locationName);
        String checkbox = siteTypesLocations.get("IsOptional").toString().equals("1") ? "Optional" : "All";
        testData.put("checkbox", checkbox);
        if (location.equals("location with linked icon")) {
            String iconPath = siteTypesLocations.get("Icon").toString();
            testData.put("iconPath", iconPath);
        }
        runtimeState.scenario.write("Searching for location: " + locationName);
        runtimeState.adminEditSiteTypePage.setColumnHeaderFilter("Location", "Is equal to", locationName);
    }

    @ContinueNextStepsOnException
    @Then("^the picture linked to the location is displayed$")
    public void the_picture_linked_to_the_location_is_displayed() throws Throwable {
        assertTrue("Picture linked to location is not displayed! ", runtimeState.adminEditSiteTypePage.isIconPictureDisplayed(testData.getString("iconPath")));
    }

    @ContinueNextStepsOnException
    @Then("^the relevant checkbox is ticked$")
    public void the_relevant_checkbox_is_ticked() throws Throwable {
        String checkbox = testData.getString("checkbox");
        runtimeState.scenario.write("Asserting '" + checkbox + "' checkbox is ticked for Location: " + testData.getString("locationName"));
        Row row = runtimeState.adminEditSiteTypePage.getGrid().getRows().get(0);
        row.getCell(checkbox).isCheckBoxChecked();
    }

    @And("^\"([^\"]*)\" checkbox is (?:ticked|unticked)$")
    public void checkbox_is_ticked(String checkbox) throws Throwable {
        testData.put("sites", checkbox);
        Row row = runtimeState.adminEditSiteTypePage.getGrid().getRows().get(0);
        row.getCell(checkbox).clickCheckbox("");
    }

    private void removeSiteTypeLocationMappingAddedByAutoTest() {
        int locationId = dbHelper.getLocationId(testData.getString("locationName"));
        int siteTypeId = dbHelperSites.getSiteTypeId(testData.getString("siteTypeModified"));
        dbHelperSites.deleteSiteTypeLocationMappingAddedByAutoTest(locationId, siteTypeId);
    }

    @ContinueNextStepsOnException
    @Then("^\"([^\"]*)\" popup alert is displayed with text \"([^\"]*)\" for the specific site type$")
    public void _popup_alert_is_displayed_with_text_for_the_specific_site_type(String title, String description) throws Throwable {
        description = testData.getString("siteType") + " " + description;
        commonSteps.popup_alert_is_displayed_with_text(title, description);

        removeSiteTypeLocationMappingAddedByAutoTest();
    }

    @ContinueNextStepsOnException
    @Then("^the Site Type Location Mapping \"([^\"]*)\" ((?:still saved|saved)) to the database$")
    public void the_site_type_location_mapping_saved_to_the_database(String saved, String stillSaved) throws Throwable {
        int isOptional = testData.getString("sites").equals("All") ? 0 : 1;
        boolean expectedResult = saved.equals("is") ? true : false;
        String exception = saved.equals("is") ? " is not" : " is";
        assertEquals("Site Type Location mapping " + exception + " present in the database !", expectedResult, dbHelperSites.isSiteTypeLocationMappingPresent(testData.getString("siteTypeModified"), testData.getString("locationName"), isOptional));

        if (saved.equals("is") && !stillSaved.equals("still saved")) {
            removeSiteTypeLocationMappingAddedByAutoTest();
        }
    }
}



