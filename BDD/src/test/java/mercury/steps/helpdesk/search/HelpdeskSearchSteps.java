package mercury.steps.helpdesk.search;

import static com.google.common.base.Strings.nullToEmpty;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.steps.CommonSteps.assertDataFound;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.api.models.resource.Resource;
import mercury.database.dao.CallerDetailsDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.SiteContractorAssetDao;
import mercury.database.dao.SiteViewDao;
import mercury.database.models.Job;
import mercury.database.models.SiteContractorAsset;
import mercury.database.models.SiteView;
import mercury.databuilders.CallerContact;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.helpers.OutputHelper;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.pageobject.web.helpdesk.HelpdeskCoreDetails;
import mercury.runtime.RuntimeState;

public class HelpdeskSearchSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private CallerDetailsDao callerDetailsDao;
    @Autowired private SiteContractorAssetDao siteContractorAssetDao;
    @Autowired private SiteViewDao siteViewDao;
    @Autowired private SiteView siteView;
    @Autowired private NewJob job;
    @Autowired private JobDao jobDao;
    @Autowired private CallerContact callerContact;
    @Autowired private TestData testData;
    @Autowired private HelpdeskSearchForSiteSteps helpdeskSearchForSiteSteps;
    @Autowired private ApiHelperResources apiHelperResources;
    @Autowired private DbHelperJobs dbHelperJobs;

    private static final String[] SITE_BUTTONS = { "Log an incident", "Log a job" };


    @When("^a search is run for an? \"([^\"]*)\" resource$")
    public void a_search_is_run_for_a_resource(String state) throws Exception {
        String searchStr;
        if ("ACTIVE".equalsIgnoreCase(state)) {
            Map<String, Object> dbData = dbHelperResources.getRandomActiveResource();
            searchStr = (String) dbData.get("Summary");
            testData.put("resourceId", dbData.get("Id"));
            runtimeState.scenario.write("Active resourceId: " + dbData.get("Id"));
        } else {
            searchStr = dbHelperResources.getRandomInactiveResourceName();

            if (searchStr == null) {
                // update an active resource to be inactive
                Map<String, Object> dbData = dbHelperResources.getRandomActiveResource();
                searchStr = (String) dbData.get("Summary");
                int resourceId = (Integer) dbData.get("Id");
                Resource resource = apiHelperResources.getResource(resourceId);
                resource.setActive(false);
                apiHelperResources.createOrUpdateResource(resource);
            }
        }

        assertDataFound(searchStr);

        String name = searchStr.contains("-") ? normalize(searchStr.substring(0, searchStr.lastIndexOf(" -")).split("\\(")[0].replaceAll("( +)", " ")) : searchStr;
        callerContact.setName(name);

        runtimeState.scenario.write("Searching for: " + searchStr);

        if ("active".equals(state)) {
            runtimeState.helpdeskResourcePage = runtimeState.helpdeskSearchBar.searchForResource(searchStr).get();
        } else {
            runtimeState.helpdeskSearchBar.searchFor("user", searchStr, 1);
        }
        outputHelper.takeScreenshots();
        testData.put("searchStr", searchStr);
        testData.put("resourceName", name);
    }

    @When("^a search is run for a client caller$")
    public void a_search_is_run_for_a_client_caller() throws Exception {
        String searchStr;
        callerContact.copy(callerDetailsDao.getRandomClientCallerWithSite("occupied"));

        // search and select

        if (getWebDriver().getCurrentUrl().contains("search/site")) {
            searchStr = callerContact.getName() + " - " + callerContact.getDepartment();
            runtimeState.scenario.write("Searching for: " + searchStr);

            runtimeState.helpdeskResourcePage = runtimeState.helpdeskSearchBar.searchForResource(searchStr);
        } else {
            searchStr = callerContact.getName() + " (" + callerContact.getDepartment() + " - " + callerContact.getSiteName() + ")";
            runtimeState.scenario.write("Searching for: " + searchStr);

            runtimeState.helpdeskCallerPage = runtimeState.helpdeskSearchBar.searchForCaller(searchStr);
            siteView.copy(siteViewDao.getSiteByName(runtimeState.helpdeskCallerPage.getSubHeadline()));
        }
        outputHelper.takeScreenshots();
        testData.put("searchStr", searchStr);
    }

    @When("^a search is run for an existing caller$")
    public void a_search_is_run_for_existing_client_caller() throws Exception {

        callerContact.copy(callerDetailsDao.getRandomClientCallerForSite(siteView.getName()));

        if (getWebDriver().getCurrentUrl().contains("search/site")) {

            String searchStr = "";
            if ("Resource".equals(callerContact.getCallerType())) {
                searchStr = callerContact.getName() + " - " + callerContact.getDepartment();
            } else {
                searchStr = callerContact.getName() + " (" + callerContact.getDepartment() + " - " + callerContact.getSiteName() + ")";
            }

            runtimeState.scenario.write("Searching for: " + searchStr);

            if (callerDetailsDao.getClientCallerByName(callerContact.getName()) != null) {
                runtimeState.helpdeskCallerPage = runtimeState.helpdeskSearchBar.searchForCaller(searchStr);
            } else {
                runtimeState.helpdeskResourcePage = runtimeState.helpdeskSearchBar.searchForResource(searchStr);
            }
            testData.put("searchStr", searchStr);
        }
        outputHelper.takeScreenshots();
    }

    @When("^a search is run for an existing contractor$")
    public void a_search_is_run_for_an_existing_contact() throws Throwable {

        SiteContractorAsset sca = siteContractorAssetDao.getRandomMappingForSite(siteView.getId());

        if (getWebDriver().getCurrentUrl().contains("search/site")) {
            String searchStr = sca.getResourceName();
            runtimeState.scenario.write("Searching for: " + searchStr);
            runtimeState.helpdeskResourcePage = runtimeState.helpdeskSearchBar.searchForResource(searchStr);
            testData.put("searchStr", searchStr);
        }
        outputHelper.takeScreenshots();
    }

    @When("^a search is initiated for a caller$")
    public void a_search_is_initiated_for_a_caller() throws Exception {
        runtimeState.helpdeskSearchBar.enterSearchString("newCaller");
        outputHelper.takeScreenshots();
    }

    @When("^a search is run for a client caller with an \"([^\"]*)\" site$")
    public void a_search_is_run_for_a_resource_with_site(String siteState) throws Exception {
        callerContact.copy(callerDetailsDao.getRandomClientCallerWithSite(siteState));

        String searchStr = callerContact.getName() + " (" + callerContact.getDepartment() + " - " + callerContact.getSiteName() + ")";
        runtimeState.scenario.write("Searching for: " + searchStr);

        runtimeState.helpdeskCallerPage = runtimeState.helpdeskSearchBar.searchForCaller(searchStr);
        outputHelper.takeScreenshots();
        siteView.copy(siteViewDao.getSiteByName(callerContact.getSiteName()));
        testData.put("searchStr", searchStr);
        testData.put("siteId", siteView.getId());
    }

    @When("^a search is run for an asset$")
    public void a_search_is_run_for_an_asset() throws Exception {

        String searchStr = dbHelper.getRandomAsset();
        runtimeState.scenario.write("Searching for: " + searchStr);

        runtimeState.helpdeskSearchBar = runtimeState.helpdeskSearchBar.search(searchStr).get();
        outputHelper.takeScreenshots();
        testData.put("searchStr", searchStr);
    }

    @When("^a search is run for an asset subtype$")
    public void a_search_is_run_for_an_asset_subtype() throws Exception {
        String searchStr = dbHelper.getRandomAssetSubType();
        runtimeState.scenario.write("Searching for: " + searchStr);
        runtimeState.helpdeskAssetPage = runtimeState.helpdeskSearchBar.searchForAssetSubtype(searchStr).get();
        outputHelper.takeScreenshots();
        testData.put("searchStr", searchStr);
    }

    @ContinueNextStepsOnException
    @Then("^(?:the |)\"(.*)\" button(?:s are| is) enabled$")
    public void buttons_are_enabled(String listOfButtons) throws Throwable {

        // Remove whitespace and split by comma
        List<String> buttons = Arrays.asList(listOfButtons.replaceAll("\" and \"", ",").split("\\s*,\\s*"));

        for (String button : buttons) {
            if (Arrays.asList(SITE_BUTTONS).contains(button)) {
                assertTrue("Expected button (" + button + ") to be enabled", runtimeState.helpdeskSitePage.isButtonEnabled(button));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^\"(.*)\" button(?:s are| is) disabled$")
    public void buttons_are_disabled(String listOfButtons) throws Throwable {

        // Remove whitespace and split by comma
        List<String> buttons = Arrays.asList(listOfButtons.replaceAll("\" and \"", ",").split("\\s*,\\s*"));

        for (String button : buttons) {
            if (Arrays.asList(SITE_BUTTONS).contains(button)) {
                assertTrue("Expected button (" + button + ") to be disabled", runtimeState.helpdeskSitePage.isButtonDisabled(button));
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^a new tab is opened with the resource details$")
    public void a_new_tab_is_opened_with_the_resource_details() throws Exception {

        runtimeState.helpdeskCoreDetails = new HelpdeskCoreDetails(getWebDriver()).get();

        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Line Manager"));
        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Availability"));

        String contactName = runtimeState.helpdeskResourcePage.getHeadline();
        if (contactName.contains("(")) {
            contactName = contactName.substring(0, contactName.indexOf("("));
        }

        assertEquals(callerContact.getName(), normalize(contactName));
    }

    @ContinueNextStepsOnException
    @Then("^caller details are displayed$")
    public void a_new_tab_is_opened_with_the_caller_details() throws Exception {

        assertTrue(runtimeState.helpdeskCallerPage.isButtonDisplayed("Edit Caller"));
        assertTrue(runtimeState.helpdeskCallerPage.isButtonDisplayed("Identify Caller"));

        assertTrue(runtimeState.helpdeskCallerPage.isContentTitleDisplayed("Name"));
        assertTrue(runtimeState.helpdeskCallerPage.isContentTitleDisplayed("Job role"));
        assertTrue(runtimeState.helpdeskCallerPage.isContentTitleDisplayed("Site"));
        assertTrue(runtimeState.helpdeskCallerPage.isContentTitleDisplayed("Phone"));
        assertTrue(runtimeState.helpdeskCallerPage.isContentTitleDisplayed("Extension"));
        assertTrue(runtimeState.helpdeskCallerPage.isContentTitleDisplayed("Department"));

        assertTrue("Unexpected heading contact name",  normalize(runtimeState.helpdeskCallerPage.getHeadline()).contains(normalize(callerContact.getName())) );
        assertEquals("Unexpected heading site name", callerContact.getSiteName(), runtimeState.helpdeskCallerPage.getSubHeadline());
        assertEquals("Unexpected contact name", normalize(callerContact.getName()), normalize(runtimeState.helpdeskCallerPage.getContentValue("Name")));
        assertEquals("Unexpected job title", normalize(callerContact.getJobTitle()).toLowerCase(), runtimeState.helpdeskCallerPage.getContentValue("Job role").toLowerCase());
        assertEquals("Unexpected site name", normalize(callerContact.getSiteName()), normalize(runtimeState.helpdeskCallerPage.getContentValue("Site")));
        assertEquals("Unexpected phone number", callerContact.getTelephone(), runtimeState.helpdeskCallerPage.getContentValue("Phone"));
        assertEquals("Unexpected extension", nullToEmpty(callerContact.getExtension()), runtimeState.helpdeskCallerPage.getContentValue("Extension").trim());
        assertEquals("Unexpected department", callerContact.getDepartment(), runtimeState.helpdeskCallerPage.getContentValue("Department"));
    }

    @ContinueNextStepsOnException
    @Then("^a new tab is opened with the asset details$")
    public void a_new_tab_is_opened_with_the_asset_details() throws Exception {
        String searchStr = testData.getString("searchStr");
        if (searchStr.contains(">")) {
            String[] parts = searchStr.split(">");
            assertEquals(normalize(parts[1].split("\\(")[0].trim()), normalize(runtimeState.helpdeskAssetPage.getHeadline()));
            assertEquals(normalize(parts[0].split("\\(")[0].trim()), normalize(runtimeState.helpdeskAssetPage.getSubHeadline()));
        } else {
            assertEquals(searchStr, runtimeState.helpdeskAssetPage.getHeadline());
        }
        testData.put("searchStr", searchStr);
    }

    @ContinueNextStepsOnException
    @Then("^a new tab is opened with the asset subtype details$")
    public void a_new_tab_is_opened_with_the_asset_subtype_details() throws Exception {

        String assetSubType = testData.getString("assetSubType");
        String subType = assetSubType.split(" > ")[0];
        String name = assetSubType.split(" > ")[1];

        assertEquals(name, runtimeState.helpdeskAssetPage.getHeadline());
        assertEquals(subType, runtimeState.helpdeskAssetPage.getSubHeadline());
    }

    @ContinueNextStepsOnException
    @Then("^no matching results are found$")
    public void no_matching_results_are_found() throws Exception {
        if ( runtimeState.helpdeskSearchBar.isNoResultsFound() ) {
            return; // success
        }
        // some results have been returned - check the exact match is not found
        for (String result : runtimeState.helpdeskSearchBar.getSearchResults()) {
            assertFalse(result.equals(testData.getString("searchStr")));
        }
    }

    @ContinueNextStepsOnException
    @Then("^no matching \"([^\"]*)\" is found$")
    public void no_matching_results_are_found(String type) throws Exception {
        if ( runtimeState.helpdeskSearchBar.isNoResultsFound() ) {
            return; // success
        }
        // some results have been returned - check the exact match is not found
        for (String result : runtimeState.helpdeskSearchBar.getSearchResults(type)) {
            assertFalse(result.equals(testData.getString("searchStr")));
        }
    }

    @ContinueNextStepsOnException
    @Then("^a new tab is opened with the job details$")
    public void a_new_tab_is_opened_with_the_job_details() throws Exception {

        runtimeState.helpdeskCoreDetails = new HelpdeskCoreDetails(getWebDriver()).get();

        String jobType = dbHelperJobs.getJobType(testData.getInt("jobReference"));

        if (jobType.equals("PPM") && testData.getInt("assetId") != null) {
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("PPM"));
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Location"));
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Subtype/Classification"));
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Description"));
        } else if (jobType.equals("PPM")) {
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("PPM"));
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Main Type"));
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Description"));
        } else {
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Caller"));
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Location"));
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Subtype/Classification"));
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Fault"));
        }

        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Site"));
        assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Asset Tag"));

        Job job = jobDao.getByJobReference(testData.getInt("jobReference"));
        boolean quoteRequested = job.getJobTypeId() == 2 ? true : false;
        if (quoteRequested) {
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Scope of Works"));
        } else {
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Description"));
        }
        if (job.getJobTypeId() != 3) {
            assertTrue(runtimeState.helpdeskCoreDetails.isCoreTitleDisplayed("Priority"));
        }


    }

    @Given("^they are logging a new job$")
    public void a_Helpdesk_Operator_logging_a_new_job() throws Throwable {
        helpdeskSearchForSiteSteps.a_search_is_run_for_a_site("occupied");
        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();
        testData.put("siteName", siteView.getName());
        testData.put("isQuoteRequested", false);
    }

    @Given("^the caller is identified$")
    public void the_caller_is_identified() throws Throwable {

        if (getWebDriver().getCurrentUrl().contains("search/caller")) {
            runtimeState.helpdeskCallerPage.clickIdentifyCallerButton();
        } else {
            runtimeState.helpdeskResourcePage.clickIdentifyCallerButton();
        }
        job.setCaller(callerContact);
    }

    @When("^a search is run for an active resource with outstanding absences$")
    public void a_search_is_run_for_an_active_resource_with_outstanding_absences() throws Throwable {
        Map<String, Object> dbData = dbHelperResources.getRandomActiveResourceWithOutstandingAbsences();
        String resource = (String) dbData.get("Summary");
        if (resource == null) {
            resource = dbHelperResources.getRandomActiveResourceName();
            String resourceName = resource.split(" - ")[0].split("\\(")[0].replaceAll("( +)", " ").trim();
            int resourceId = dbHelperResources.getResourceId(resourceName);
            testData.put("resourceId", resourceId);
            apiHelperResources.createAbsenceForResource(resourceId, null);
        } else {
            testData.put("resourceId", dbData.get("Id"));
        }

        callerContact.setName(resource.split(" - ")[0].split("\\(")[0].replaceAll("( +)", " ").trim());
        runtimeState.scenario.write("Searching for: " + resource);
        runtimeState.helpdeskResourcePage = runtimeState.helpdeskSearchBar.searchForResource(resource).get();
        testData.put("resource", resource);
    }

}