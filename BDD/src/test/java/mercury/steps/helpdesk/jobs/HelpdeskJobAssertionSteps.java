package mercury.steps.helpdesk.jobs;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.StringHelper.normalize;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import mercury.database.models.SiteView;
import mercury.databuilders.CallerContact;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.ErrorHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.HelpdeskCoreDetails;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskLinkedIncidentsModal;
import mercury.runtime.RuntimeState;

public class HelpdeskJobAssertionSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private TestData testData;
    @Autowired private SiteView siteView;
    @Autowired private CallerContact callerContact;
    @Autowired private OutputHelper outputHelper;
    @Autowired private ErrorHelper errorHelper;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;


    @ContinueNextStepsOnException
    @Then("^a draft template is displayed to log a new job$")
    public void a_draft_template_is_displayed_to_log_a_new_job() throws Throwable {
        assertEquals("New Job", runtimeState.helpdeskLogJobPage.getHeadline());
        assertEquals(siteView.getName(), runtimeState.helpdeskLogJobPage.getSubHeadline());
    }

    @ContinueNextStepsOnException
    @Then("^the following New Caller fields are mandatory \"([^\"]*)\"$")
    public void new_caller_fields_are_mandatory(String fieldList) throws Throwable {

        if (getWebDriver().getCurrentUrl().contains("addcaller")) {
            assertMandatoryCallerFieldsOnAddCallerPage(fieldList);
        }else {
            assertMandatoryCallerFieldsOnLogJobPage(fieldList);
        }
    }

    public void assertMandatoryCallerFieldsOnLogJobPage(String fieldList) throws Throwable {

        // populate all fields
        runtimeState.newCallerPage.enterName("Elvis Presley");
        runtimeState.newCallerPage.enterJobRole("The King");
        runtimeState.newCallerPage.enterDepartment("Rock and Roll");
        runtimeState.newCallerPage.enterTelephone(DataGenerator.generatePhoneNumber());
        runtimeState.newCallerPage.enterExtension("0000");

        // verify Save button is enabled
        assertTrue(runtimeState.newCallerPage.isSaveEnabled());

        // for each mandatory field - set it to blank, assert Save is disabled, then re-populate the field

        //Remove whitespace and split by comma
        List<String> fields = Arrays.asList(fieldList.split("\\s*,\\s*"));

        for (String field : fields) {
            // String error;
            switch (field.toUpperCase()) {
            case "NAME":
                runtimeState.newCallerPage.enterName("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.newCallerPage.isSaveEnabled());
                runtimeState.newCallerPage.enterName("Elvis Presley");
                break;
            case "JOB ROLE":
                runtimeState.newCallerPage.enterJobRole("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.newCallerPage.isSaveEnabled());
                runtimeState.newCallerPage.enterJobRole("The King");
                break;
            case "DEPARTMENT":
                runtimeState.newCallerPage.enterDepartment("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.newCallerPage.isSaveEnabled());
                runtimeState.newCallerPage.enterDepartment("Rock and Roll");
                break;
            case "TELEPHONE":
                runtimeState.newCallerPage.enterTelephone("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.newCallerPage.isSaveEnabled());
                runtimeState.newCallerPage.enterTelephone("1901-332-0887");
                break;
            case "EXTENSION":
                runtimeState.newCallerPage.enterExtension("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.newCallerPage.isSaveEnabled());
                runtimeState.newCallerPage.enterExtension("0000");
                break;
            default:
                throw new Exception("Unexpected field");
            }
        }
    }

    public void assertMandatoryCallerFieldsOnAddCallerPage(String fieldList) throws Throwable {

        // populate all fields
        runtimeState.helpdeskAddCallerPage.enterName("Elvis Presley");
        runtimeState.helpdeskAddCallerPage.enterJobRole("The King");
        runtimeState.helpdeskAddCallerPage.enterDepartment("Rock and Roll");
        runtimeState.helpdeskAddCallerPage.enterTelephone(DataGenerator.generatePhoneNumber());
        runtimeState.helpdeskAddCallerPage.enterExtension("0000");

        // verify Save button is enabled
        assertTrue(runtimeState.helpdeskAddCallerPage.isSaveEnabled());

        //Remove whitespace and split by comma
        List<String> fields = Arrays.asList(fieldList.split("\\s*,\\s*"));

        runtimeState.helpdeskAddCallerPage.enterName("");
        runtimeState.helpdeskAddCallerPage.clickSaveAndIdentifyAsCaller_ErrorExpected();

        for (String field : fields) {
            switch (field.toUpperCase()) {
            case "NAME":
                runtimeState.helpdeskAddCallerPage.enterName("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.helpdeskAddCallerPage.isSaveEnabled());
                runtimeState.helpdeskAddCallerPage.enterName("Elvis Presley");
                break;
            case "JOB ROLE":
                runtimeState.helpdeskAddCallerPage.enterJobRole("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.helpdeskAddCallerPage.isSaveEnabled());
                runtimeState.helpdeskAddCallerPage.enterJobRole("The King");
                break;
            case "DEPARTMENT":
                runtimeState.helpdeskAddCallerPage.enterDepartment("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.helpdeskAddCallerPage.isSaveEnabled());
                runtimeState.helpdeskAddCallerPage.enterDepartment("Rock and Roll");
                break;
            case "TELEPHONE":
                runtimeState.helpdeskAddCallerPage.enterTelephone("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.helpdeskAddCallerPage.isSaveEnabled());
                runtimeState.helpdeskAddCallerPage.enterTelephone("1901-332-0887");
                break;
            case "EXTENSION":
                runtimeState.helpdeskAddCallerPage.enterExtension("");
                outputHelper.takeScreenshots();
                assertFalse(runtimeState.helpdeskAddCallerPage.isSaveEnabled());
                runtimeState.helpdeskAddCallerPage.enterExtension("0000");
                break;
            default:
                throw new Exception("Unexpected field");
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the following New Caller fields? (?:is|are) optional \"([^\"]*)\"$")
    public void new_caller_fields_are_optional(String fieldList) throws Throwable {
        if (getWebDriver().getCurrentUrl().contains("addcaller")) {
            assertOptionalCallerFieldsOnAddCallerPage(fieldList);
        }else {
            assertOptionalCallerFieldsOnLogJobPage(fieldList);
        }
    }

    private void assertOptionalCallerFieldsOnAddCallerPage(String fieldList) throws Throwable {

        // populate all fields
        runtimeState.helpdeskAddCallerPage.enterName("Elvis Presley");
        runtimeState.helpdeskAddCallerPage.enterJobRole("The King");
        runtimeState.helpdeskAddCallerPage.enterDepartment("Rock and Roll");
        runtimeState.helpdeskAddCallerPage.enterTelephone(DataGenerator.generatePhoneNumber());
        runtimeState.helpdeskAddCallerPage.enterExtension("0000");

        // verify Save button is enabled
        assertTrue(runtimeState.helpdeskAddCallerPage.isSaveEnabled());

        //Remove whitespace and split by comma
        List<String> fields = Arrays.asList(fieldList.split("\\s*,\\s*"));

        for (String field : fields) {
            switch (field.toUpperCase()) {
            case "EXTENSION":
                runtimeState.helpdeskAddCallerPage.enterExtension("");
                break;
            default:
                throw new Exception("Unexpected field");
            }
            assertTrue(runtimeState.helpdeskAddCallerPage.isSaveEnabled());
        }
    }

    private void assertOptionalCallerFieldsOnLogJobPage(String fieldList) throws Throwable {

        // populate all fields
        runtimeState.newCallerPage.enterName("Elvis Presley");
        runtimeState.newCallerPage.enterJobRole("The King");
        runtimeState.newCallerPage.enterDepartment("Rock and Roll");
        runtimeState.newCallerPage.enterTelephone(DataGenerator.generatePhoneNumber());
        runtimeState.newCallerPage.enterExtension("0000");

        // verify Save button is enabled
        assertTrue(runtimeState.newCallerPage.isSaveEnabled());

        //Remove whitespace and split by comma
        List<String> fields = Arrays.asList(fieldList.split("\\s*,\\s*"));

        for (String field : fields) {
            switch (field.toUpperCase()) {
            case "EXTENSION":
                runtimeState.newCallerPage.enterExtension("");
                break;
            default:
                throw new Exception("Unexpected field");
            }
            assertTrue(runtimeState.newCallerPage.isSaveEnabled());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the page is updated with pre-populated caller details$")
    public void the_new_caller_has_been_stored() throws Throwable {
        assertCallerDetails();
    }


    private void assertCallerDetails() {

        if (getWebDriver().getCurrentUrl().contains("addcaller")) {
            assertEquals("Unexpected Name found", callerContact.getName(), runtimeState.helpdeskAddCallerPage.getContentValue("Name"));
            assertEquals("Unexpected Job role found", nullToEmpty(callerContact.getJobTitle()), runtimeState.helpdeskAddCallerPage.getContentValue("Job role"));
            assertEquals("Unexpected Site found", nullToEmpty(siteView.getName()), runtimeState.helpdeskAddCallerPage.getContentValue("Site"));
            assertEquals("Unexpected Phone Number found", nullToEmpty(callerContact.getTelephone()), runtimeState.helpdeskAddCallerPage.getContentValue("Phone"));
            assertEquals("Unexpected Extension found", nullToEmpty(callerContact.getExtension()), runtimeState.helpdeskAddCallerPage.getContentValue("Extension"));
            assertEquals("Unexpected Department found", nullToEmpty(callerContact.getDepartment()), runtimeState.helpdeskAddCallerPage.getContentValue("Department"));

        } else {
            String caller = callerContact.getName() + " (" + callerContact.getDepartment() + ")";
            assertEquals("Unexpected callerName (department) found", caller, runtimeState.helpdeskLogJobPage.getCaller());
            assertEquals("Unexpected Phone Number found", nullToEmpty(callerContact.getTelephone()), runtimeState.helpdeskLogJobPage.getPhoneNo());
            assertEquals("Unexpected Extension found", nullToEmpty(callerContact.getExtension()), runtimeState.helpdeskLogJobPage.getExtension());
            assertEquals("Unexpected Caller Type found", nullToEmpty(callerContact.getCallerType()), runtimeState.helpdeskLogJobPage.getCallerType());
        }
    }

    @ContinueNextStepsOnException
    @Then("^the department, phone number, extension and caller type are auto completed$")
    public void caller_details_autocompleted() throws Exception {
        List<Map<String, Object>> expectedCaller = dbHelperJobs.getCallerNameAndJobTitle(testData.getInt("jobreference"));

        if ( !callerContact.getDepartment().isEmpty() ) {
            assertTrue("Unexpected Department found", runtimeState.helpdeskLogJobPage.getCaller().contains("(" + callerContact.getDepartment() +")"));
        }
        assertEquals("Unexpected Phone Number found", callerContact.getTelephone(), runtimeState.helpdeskLogJobPage.getPhoneNo());
        String expectedExt = null;
        if (expectedCaller.get(0).get("Extension") == null || expectedCaller.get(0).get("Extension").toString().isEmpty()) {
            expectedExt = "";
        } else {
            expectedExt = expectedCaller.get(0).get("Extension").toString();
        }
        assertEquals("Unexpected Extension found", expectedExt, runtimeState.helpdeskLogJobPage.getExtension());
        assertEquals("Unexpected Caller Type found", callerContact.getCallerType(), runtimeState.helpdeskLogJobPage.getCallerType());
    }

    @ContinueNextStepsOnException
    @Then("^Department is set to \"([^\"]*)\"$")
    public void department_is_set_to(String department) throws Throwable {
        assertEquals("Unexpected Department found", department, StringUtils.substringBetween(runtimeState.helpdeskLogJobPage.getCaller(), "(", ")"));
    }

    @ContinueNextStepsOnException
    @Then("^Phone Number is set to \"([^\"]*)\"$")
    public void phone_Number_is_set_to(String phoneNumber) throws Throwable {
        assertEquals("Unexpected Phone Number found", phoneNumber, runtimeState.helpdeskLogJobPage.getPhoneNo());
    }

    @ContinueNextStepsOnException
    @Then("^Extension is set to \"([^\"]*)\"$")
    public void extension_is_set_to(String extension) throws Throwable {
        assertEquals("Unexpected Extension found", extension, runtimeState.helpdeskLogJobPage.getExtension());
    }

    @ContinueNextStepsOnException
    @Then("^Caller Type is set to \"([^\"]*)\"$")
    public void caller_Type_is_set_to(String callerType) throws Throwable {
        assertEquals("Unexpected Caller Type found", callerType, runtimeState.helpdeskLogJobPage.getCallerType());
    }

    @ContinueNextStepsOnException
    @Then("^Priority is set to \"([^\"]*)\"$")
    public void priority_is_set_to(String priority) throws Throwable {
        outputHelper.takeScreenshots();
        assertEquals("Unexpected Priority found", priority, runtimeState.helpdeskLogJobPage.getPriority());
    }

    @ContinueNextStepsOnException
    @Then("^the fault priority auto completed based on site, asset subtype and fault type$")
    public void the_fault_priority_autocompleted() throws Throwable {
        String faultTypeName = runtimeState.helpdeskLogJobPage.getFaultType();
        String assetSubTypeName = runtimeState.helpdeskLogJobPage.getClassification().split("\n")[0];
        String siteName = testData.getString("siteName");
        String expectedPriority;
        if (assetSubTypeName.contains(">")) {
            String[] parts = assetSubTypeName.split(">");
            int assetSize = parts.length;
            if (assetSize == 2) {
                assetSubTypeName = parts[1].trim();
                expectedPriority = dbHelper.getFaultTypeDetail(faultTypeName, assetSubTypeName, siteName);
            } else {
                assetSubTypeName = parts[1].trim();
                String assetClassificationName = parts[2].trim();
                expectedPriority = dbHelper.getFaultTypeDetail(faultTypeName, assetSubTypeName, assetClassificationName, siteName);
            }
        } else {
            expectedPriority = dbHelper.getFaultTypeDetail(faultTypeName, assetSubTypeName, siteName);
        }
        runtimeState.scenario.write("expected priority to be " + expectedPriority + " for asset subtype [" + assetSubTypeName + "], fault type [" + faultTypeName + "] and site " + siteName);
        assertEquals("Unexpected Priority found", expectedPriority, runtimeState.helpdeskLogJobPage.getPriority());
    }

    @ContinueNextStepsOnException
    @Then("^a \"([^\"]*)\" warning displayed$")
    public void error_displayed(String error) {
        switch(error) {
        case "We could not find a site at your location":
            assertTrue(runtimeState.loginPage.getNoValidateMessage().contains(error));
            break;

        default:
            assertTrue(runtimeState.helpdeskLogJobPage.getError().equals(error));
        }
    }

    @ContinueNextStepsOnException
    @Then("^a \"([^\"]*)\" warning is NOT displayed$")
    public void error_not_displayed(String error) {
        getWebDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        switch(error) {
        case "We could not find a site at your location":
            assertFalse(runtimeState.loginPage.getNoValidateMessage().contains(error));
            break;

        default:
            assertFalse(runtimeState.helpdeskLogJobPage.getError().equals(error));
        }
        getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
    }

    @ContinueNextStepsOnException
    @Then("^a new job is not created$")
    public void the_job_is_not_created() throws Throwable {
        assertTrue(runtimeState.helpdeskLogJobPage.isDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^potential duplicate jobs are displayed with \"([^\"]*)\"$")
    public void potential_duplicate_jobs_are_displayed_with(String content) throws Throwable {

        outputHelper.takeScreenshots();

        String[] parts = content.split(",");
        for (int i = 0; i < parts.length; i++) {
            String field = parts[i].trim();

            switch (field.toUpperCase()) {
            case "JOB REFERENCE":
                List<Integer> actualJobReferences = runtimeState.helpdeskLogJobPage.getDuplicateJobReferences();
                int jobReference = testData.getInt("jobReference");
                assertTrue("Expected duplicate Job Reference not displayed: " + jobReference, actualJobReferences.contains(jobReference));
                break;
            case "SITE NAME":
                List<String> actualSiteNames = runtimeState.helpdeskLogJobPage.getDuplicateSiteNames();
                String siteName = testData.getString("siteName");
                assertTrue("Expected duplicate Site Name not displayed: " + siteName, actualSiteNames.contains(siteName));
                break;
            case "CREATED DATE":
                List<String> actualCreatedDates = runtimeState.helpdeskLogJobPage.getDuplicateCreatedDates();
                for (String date : actualCreatedDates) {
                    assertNotNull("Expected duplicate Created Date should be populated", date);
                    assertFalse("Expected duplicate Created Date should be populated", date.isEmpty());
                }
                break;
            case "STATUS":
                List<String> actualStatuses = runtimeState.helpdeskLogJobPage.getDuplicateStatuses();
                for (String status : actualStatuses) {
                    assertNotNull("Expected duplicate Status should be populated", status);
                    assertFalse("Expected duplicate Status should be populated", status.isEmpty());
                }
                break;
            case "AST/CLASS":
                List<String> actualAstClassifications = runtimeState.helpdeskLogJobPage.getDuplicateAstClassifications();
                String astClassification = testData.getString("subtypeClassification").replaceFirst(".*>", "").trim();
                assertTrue("Expected duplicate Classification not displayed: " + astClassification, actualAstClassifications.toString().contains(astClassification));
                break;
            case "LOCATION":
                List<String> actualLocations = runtimeState.helpdeskLogJobPage.getDuplicateLocations();
                String location = testData.getString("location");
                assertTrue("Expected duplicate Location displayed: " + location, actualLocations.toString().contains(location));
                break;
            case "FAULT TYPE":
                List<String> actualFaultTypes = runtimeState.helpdeskLogJobPage.getDuplicateFaultTypes();
                String faultType = testData.getString("fault");
                assertTrue("Expected duplicate Fault Type not displayed: " + faultType, actualFaultTypes.contains(faultType));
                break;
            case "DESCRIPTION":
                List<String> actualDescriptions = runtimeState.helpdeskLogJobPage.getDuplicateDescriptions();
                for (String description : actualDescriptions) {
                    assertNotNull("Expected duplicate Description should be populated", description);
                    assertFalse("Expected duplicate Description should be populated", description.isEmpty());
                }
                break;
            default:
                throw new Exception("Unexpected field: " + field);
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the Contractor to Quote field is marked as required$")
    public void the_Contractor_to_Quote_field_is_marked_as_required() throws Throwable {
        String error = runtimeState.helpdeskLogJobPage.getContractorToQuoteError();
        assertEquals("This field is required.", error);
    }

    @ContinueNextStepsOnException
    @Then("^the Contractor to Quote dropdown is unselectable$")
    public void the_Contractor_to_Quote_dropdown_is_unselectable() throws Throwable {
        assertEquals("None", runtimeState.helpdeskLogJobPage.getContractorToQuote());
        assertFalse(runtimeState.helpdeskLogJobPage.isContractorToQuoteSelectable());
    }

    @And("^the job closedown details are displayed$")
    public void a_new_tab_is_opened_with_the_job_closedown_details() throws Exception {
        assertTrue(runtimeState.helpdeskJobPage.isJobCloseDownSectionDisplayed());
        assertTrue(runtimeState.helpdeskJobPage.isJobCloseDownTitleDisplayed("Location"));
        assertTrue(runtimeState.helpdeskJobPage.isJobCloseDownTitleDisplayed("Subtype/Classification"));
        assertTrue(runtimeState.helpdeskJobPage.isJobCloseDownTitleDisplayed("Asset Tag"));
    }

    @And("^the job closedown details are not displayed$")
    public void a_new_tab_is_opened_with_the_job_closedown_not_details() throws Exception {
        assertFalse(runtimeState.helpdeskJobPage.isJobCloseDownSectionDisplayed());
    }

    @And("^the client status is correctly displayed$")
    public void job_status_correctly_displayed() throws Exception {
        String expectedClientStatus = dbHelper.getJobStatusName(testData.getInt("jobStatusId"));
        assertEquals(localize(expectedClientStatus), runtimeState.helpdeskJobPage.getClientStatus());
    }

    @ContinueNextStepsOnException
    @Then("the jobs Core Details display the new caller")
    public void the_jobs_Core_Details_display_the_new_caller() {
        runtimeState.helpdeskCoreDetails = new HelpdeskCoreDetails(getWebDriver()).get();
        assertTrue(runtimeState.helpdeskCoreDetails.getCoreValue("Caller").contains(testData.getString("caller")));
    }

    @ContinueNextStepsOnException
    @Then("^the available job actions includes \"([^\"]*)\"$")
    public void the_available_job_actions_includes(String listOfActions) throws Throwable {
        runtimeState.helpdeskJobPage.selectActions();
        outputHelper.takeScreenshots();
        List<String> availableActions = runtimeState.helpdeskJobPage.getAvailableActions();
        runtimeState.scenario.write("Enabled actions: " + availableActions);
        // Reopen Job option won't be available in Advocate as the required permissions are removed for this client
        listOfActions = propertyHelper.getMercuryUrl().contains("usad") ? listOfActions.replaceAll("Reopen Job,", "") : listOfActions;
        List<String> expectedActions = new ArrayList<String>(Arrays.asList(listOfActions.split("\\s*,\\s*")));
        //'Potential Insurance' job action would be available when 'Budget Review' toggle is set to ON. This toggle will always be on for M&S & off for everyone else
        //As part of this change, Insurance budget is disabled from front-end of portal and is replaced with this question which just sets a flag on the JobBudgetReview table
        if (dbHelperSystemToggles.getSystemFeatureToggle("Budget Review") == 1) {
            expectedActions.add("Potential Insurance");
        }
        Collections.sort(availableActions);
        Collections.sort(expectedActions);

        for(int i = 0; i < expectedActions.size(); i ++) {
            assertTrue(expectedActions.get(i) + " is not present in list", availableActions.contains(expectedActions.get(i)));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the draft log a job form tab remains open$")
    public void the_draft_log_a_job_form_tab_remains_open() throws IOException {
        int displayedJobreference = runtimeState.helpdeskJobPage.getJobReference();

        List<String> tabs = runtimeState.helpdeskHomePage.getTabs();
        assertTrue("Draft tab should be open", tabs.contains("Draft"));
        runtimeState.helpdeskHomePage.selectTab("Draft");
        outputHelper.takeScreenshots();
        runtimeState.helpdeskLogJobPage.get();
        runtimeState.scenario.write("Asserting Caller, AST/Class, Location, Description and Fault Type are populated");
        assertFalse("Caller should be populated", runtimeState.helpdeskLogJobPage.getCaller().isEmpty());
        assertFalse("Classification should be populated", runtimeState.helpdeskLogJobPage.getClassification().isEmpty());
        assertFalse("Location should be populated", runtimeState.helpdeskLogJobPage.getLocation().isEmpty());
        assertFalse("Description should be populated", runtimeState.helpdeskLogJobPage.getScopeOfWork().isEmpty());
        assertFalse("Fault Type should be populated", runtimeState.helpdeskLogJobPage.getFaultType().isEmpty());

        runtimeState.helpdeskHomePage.selectTab(String.valueOf(displayedJobreference));
    }

    @ContinueNextStepsOnException
    @Then("^the original job is opened on a new tab$")
    public void the_original_job_is_opened_on_a_new_tab() throws IOException {
        List<String> tabs = runtimeState.helpdeskHomePage.getTabs();
        int jobReference = testData.getInt("originalJobReference");
        assertTrue("Tab should be open for job " + jobReference, tabs.contains(String.valueOf(jobReference)));
        runtimeState.helpdeskJobPage.get();
        assertTrue(jobReference == runtimeState.helpdeskJobPage.getJobReference());
        outputHelper.takeScreenshots();
    }

    @ContinueNextStepsOnException
    @Then("^the user is able to add a chase to the original job$")
    public void the_user_is_able_to_add_a_chase_to_the_original_job() {
        runtimeState.scenario.write("Selecting Chase Action and asserting Chase panel is displayed");
        runtimeState.helpdeskJobChasePanel = runtimeState.helpdeskJobPage.selectChaseAction();   // assertion done within method to verify chase panel is displayed!
    }

    @ContinueNextStepsOnException
    @Then("^the Linked Incident modal is displayed with text \"([^\"]*)\" and \"([^\"]*)\"$")
    public void the_linked_incident_modal_is_displayed_with_text_something_and_something(String text1, String text2) throws Throwable {
        runtimeState.helpdeskLinkedIncidentsModal = new HelpdeskLinkedIncidentsModal(getWebDriver()).get();
        List<String> textMessages = new ArrayList<>();
        textMessages = runtimeState.helpdeskLinkedIncidentsModal.getTextMessagesOnLinkedIncidentModal();
        runtimeState.scenario.write("Asserting following message is displayed on Linked Incidents modal: " + text1);
        assertTrue("Unexpected Text Message", textMessages.contains(text1));
        runtimeState.scenario.write("Asserting following message is displayed on Linked Incidents modal: " + text2);
        assertTrue("Unexpected Text Message", textMessages.contains(text2));
    }

    /**
     * Checking the details of incidents displayed in the Linked Incidents modal
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @And("^the details of all the logged incidents for the site for the last 30 days are displayed in the Linked Incident Modal$")
    public void the_details_of_all_the_logged_incidents_for_the_site_for_the_last_30_days_are_displayed_in_the_linked_incident_modal() throws Throwable {
        // Get the count of incidents logged for the site in last 30 days
        int countOfLoggedIncidentsForSiteInLast30Days = dbHelperIncidents.getCountOfLoggedIncidentsForSiteInLast30Days(testData.getInt("siteId"));

        // If number of incidents logged for site is > 0, asserting they are displayed
        // in the Linked Incidents Modal displayed for job
        if (countOfLoggedIncidentsForSiteInLast30Days != 0) {
            runtimeState.scenario.write("Number of logged Incident(s) for the site in last 30 days is: " + countOfLoggedIncidentsForSiteInLast30Days);
            List<Map<String, Object>> dbData = dbHelperIncidents.getLoggedIncidentsForSiteInLast30Days(testData.getInt("siteId"));
            Grid grid = runtimeState.helpdeskLinkedIncidentsModal.getLinkedIncidentTable();
            logger.debug(
                    "Grid retrieved:" + grid.getHeaders().toString()
                    + " ("
                    + grid.getRows().size()
                    + " rows)");
            // Compare both sets of data are the same size
            assertEquals("Unexpected number of records", dbData.size(), grid.getRows().size());

            // Verify both data sets are the same
            runtimeState.scenario.write("Asserting the details of incidents displayed in Linked incidents modal");
            Iterator it = dbData.iterator();
            for (Row row : grid.getRows()) {
                Map<String, Object> map = (Map<String, Object>) it.next();
                logger.debug(row.getCell("Reference").getText());
                assertEquals("Unexpected Refrence", map.get("Reference").toString(), row.getCell("Reference").getText());
                assertEquals("Unexpected Site", map.get("Site"), row.getCell("Site").getText());
                assertEquals("Unexpected Incident Types", map.get("IncidentTypes"), row.getCell("Incident Types").getText());
                assertEquals("Unexpected Logged", map.get("Logged"), row.getCell("Logged").getText());
            }
        } else {
            runtimeState.scenario.write("No logged incidents found for site in previous 30 days");
        }
    }

    @And("^the close button in the Linked Incidents modal is ((?:enabled|disabled))$")
    public void the_close_button_in_the_linked_incidents_modal_is_disabled(String buttonStatus) throws Throwable {
        runtimeState.scenario.write("Asserting Close button in Linked Incidents modal is: " + buttonStatus);
        if ("DISABLED".equalsIgnoreCase(buttonStatus)) {
            assertFalse("Close button is enabled!", runtimeState.helpdeskLinkedIncidentsModal.isCloseButtonEnabled());
        } else {
            assertTrue("Close button is disabled!", runtimeState.helpdeskLinkedIncidentsModal.isCloseButtonEnabled());
        }
        dbHelper.deleteFromLinkedIncidentCriterionTable(testData.getString("assetSubType"), testData.getString("faultType"), testData.getString("incidentType"));
    }

    /**
     * Asserting the table data in the 'Linked incident for job' panel
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ContinueNextStepsOnException
    @Then("^the Linked incident for job panel is displayed with existing logged incidents in last 30 days$")
    public void the_linked_incident_for_job_panel_is_displayed_with_existing_logged_incidents_in_last_30_days() throws Throwable {
        List<Map<String, Object>> dbData = dbHelperIncidents.getLoggedIncidentsForSiteInLast30Days(testData.getInt("siteId"));
        Grid grid = runtimeState.helpdeskJobsLinkedIncidentsPanel.getGrid();
        logger.debug(
                "Grid retrieved:" + grid.getHeaders().toString()
                + " ("
                + grid.getRows().size()
                + " rows)");
        // Compare both sets of data are the same size
        assertEquals("Unexpected number of records", grid.getRows().size(), dbData.size());

        // Verify both data sets are the same
        runtimeState.scenario.write("Asserting the data displayed in the 'Linked incidents for job' table");
        Iterator it = dbData.iterator();
        for (Row row : grid.getRows()) {
            Map<String, Object> map = (Map<String, Object>) it.next();
            logger.debug(row.getCell("Incident Number").getText());
            assertEquals("Unexpected Incident Number", map.get("Reference").toString(), row.getCell("Incident Number").getText());
            assertEquals("Unexpected Site", map.get("Site"), row.getCell("Site").getText());
            String[] sortedIncidentTypes = row.getCell("Incident Type(s)").getText().split(",");
            Arrays.sort(sortedIncidentTypes);
            String convertedIncidentTypes = StringUtils.join(sortedIncidentTypes, ", ");
            assertEquals("Unexpected Incident Types", map.get("IncidentTypes"), normalize(convertedIncidentTypes));
            String expected = map.get("Description").toString();
            String actual = row.getCell("Description").getText();
            assertEquals("\nExpected: " + expected + "\nActual  : " + actual, normalize(expected), actual);
            assertEquals("Unexpected Logged", map.get("Logged"), row.getCell("Logged").getText());
        }
    }

    @And("^the \"([^\"]*)\" button is displayed beside the linked incident$")
    public void the_something_button_is_displayed_beside_the_linked_incident(String expectedButtonName) throws Throwable {
        String actualButtonName = runtimeState.helpdeskJobsLinkedIncidentsPanel.getButtonName();
        assertEquals("Unexpected button", expectedButtonName, actualButtonName);

        dbHelper.deleteFromLinkedIncidentCriterionTable(testData.getString("assetSubType"), testData.getString("faultType"), testData.getString("incidentType"));
    }

    @And("^the job remains open in another tab$")
    public void the_job_remains_open_in_another_tab() throws Throwable {
        runtimeState.scenario.write(
                "Asserting job: " + testData.getInt("jobReference")
                + " is open in another tab");
        List<String> tabs = runtimeState.helpdeskHomePage.getTabs();
        assertTrue("Job is not open ", tabs.contains(String.valueOf(testData.getInt("jobReference"))));

        dbHelper.deleteFromLinkedIncidentCriterionTable(testData.getString("assetSubType"), testData.getString("faultType"), testData.getString("incidentType"));
    }

    @ContinueNextStepsOnException
    @Then("^an error for invalid phone number format is displayed$")
    public void an_error_for_invalid_phone_number_format() throws Exception {
        String message = null;
        if (LOCALE.equals("en-US")) {
            message = errorHelper.generateErrorMessage("Phone numbers have to be in following format: (123)-123-1234 or 123-123-1234 or 1234-123-1234");
        } else {
            message = errorHelper.generateErrorMessage("Phone numbers must have 10 or more digits");
        }
        runtimeState.scenario.write("Asserting error is displayed: " + message);
        assertThat(runtimeState.loginPage.getErrors(), hasItem(containsString(message)));
    }

    @ContinueNextStepsOnException
    @Then("^the resource status changed to \"([^\"]*)\"$")
    public void the_resource_status_changed_to_status(String status) throws Exception {

        runtimeState.scenario.write("Asserting the resource status: " + status);
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> dbHelperJobs.getRecordCountForStatus(testData.getInt("jobReference"), status), equalTo(1));
    }

}
