package mercury.steps.portal;

import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.SHORT_DATE;
import static mercury.runtime.ThreadManager.getWebDriver;
import static mercury.steps.CommonSteps.assertDataFound;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.applicationinsights.core.dependencies.apachecommons.lang3.RandomUtils;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import mercury.api.models.job.Job;
import mercury.database.dao.ApplicationUserDao;
import mercury.database.dao.JobViewDao;
import mercury.database.dao.PPMJobDao;
import mercury.database.dao.UserJobDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.JobView;
import mercury.database.models.PPMJob;
import mercury.database.models.UserJob;
import mercury.databuilders.TestData;
import mercury.databuilders.User;
import mercury.helpers.DateHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.QuoteCreationHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.apihelper.ApiHelperResources;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperOrganisation;
import mercury.helpers.dbhelper.DbHelperPPM;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.DatabaseAssertionSteps;
import microsoft.sql.DateTimeOffset;

public class GetPortalUserDataSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private RuntimeState runtimeState;
    @Autowired private ApplicationUserDao applicationUserDao;
    @Autowired private UserJobDao userJobDao;
    @Autowired private TestData testData;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperPPM dbHelperPPM;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperOrganisation dbHelperOrganisation;
    @Autowired private QuoteCreationHelper quoteCreationHelper;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private DatabaseAssertionSteps databaseAssertionSteps;
    @Autowired private User user;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private PPMJobDao ppmJobDao;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private TzHelper tzHelper;
    @Autowired private ApiHelperResources apiHelperResources;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;

    ApplicationUser appUser;
    UserJob userJob;

    /**
     * Sets the testdata profile and resource type based on the profile name set in
     * the feature file. Used to determine what type of data to pull back
     *
     * @param profileName
     */
    private void setResourceUserProfileType(String profileName) {
        switch (profileName) {
        case "City Resource":
        case "Operational Manager":
        case "Contractor":

            testData.addBooleanTag("useResourceTypeName", true);
            testData.addStringTag("resourceTypeName", profileName);
            testData.addStringTag("profileName", "NA");

            break;
        case "RFM":
        case "Contractor Admin":
            testData.addBooleanTag("useResourceTypeName", false);
            testData.addStringTag("resourceTypeName", "NA");
            testData.addStringTag("profileName", profileName);

            break;
        default:
            testData.addStringTag("profileName", profileName);
            testData.addBooleanTag("useResourceTypeName", false);
            testData.addStringTag("resourceTypeName", "NA");
        }
    }

    /**
     * @param testData - initial test data to be stored
     * @param queryMap - Query map to used to filter the database
     * @throws Exception
     */
    public void getUserJob(Map<String, Object> queryMap) throws Exception {
        userJob = userJobDao.getUserJob(queryMap);

        if (userJob == null) {
            throw new PendingException("Cannot find suitable test data");
        }

        // TODO : Remove this and use the test data
        User newUser = new User.Builder("Portal")
                .withProfile(queryMap.get("profileName").toString())
                .withUserName(userJob.getUserName())
                .withPassword("Password1")
                .withResourceId(userJob.getResourceId())
                .build();

        // Uncomment and modify code to use a particular resource
        //userJob.setUserName("daniel.przekop");
        //userJob.setJobReference(20125602);
        //userJob.setResourceId(11477);
        //userJob.setSiteId(4581);

        user.copy(newUser);

        outputUserAndJobInfo();

        user.setUsername(userJob.getUserName());
        user.setResourceId(userJob.getResourceId());
        user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));
        testData.addIntegerTag("jobReference", userJob.getJobReference());
        testData.addIntegerTag("resourceId", userJob.getResourceId());
        testData.addIntegerTag("jobResourceId", userJob.getResourceId());
        testData.put("userName", userJob.getUserName());

        if (!queryMap.get("jobType").toString().contains("PPM")) {
            JobView jobView = jobViewDao.getByJobReference(userJob.getJobReference());
            testData.addStringTag("siteName", jobView.getName());
            testData.addIntegerTag("siteId", jobView.getSiteId());
        } else {
            PPMJob ppmJob = ppmJobDao.getPPMJobCompletionStatusByPpmId(userJob.getJobReference());
            testData.addStringTag("status", ppmJob.getStatus());
        }
    }

    /**
     * @param jobState - State the quote should be in
     * @throws Throwable
     */
    public void createQuoteUserJob(Map<String, Object> queryMap) throws Throwable {

        userJob = quoteCreationHelper.createQuote(queryMap);

        if (userJob == null) {
            throw new PendingException("Cannot create suitable test data, this is usually down to the database sync.");
        }

        runtimeState.scenario.write("Created Quote Job " + userJob.getJobReference());

        // TODO : Remove this and use the test data
        User newUser = new User.Builder("Portal")
                .withProfile(dbHelperResources.getResourceProfileName(userJob.getUserName()))
                .withUserName(userJob.getUserName())
                .withPassword("Password1")
                .withResourceId(userJob.getResourceId())
                .build();
        user.copy(newUser);

        outputUserAndJobInfo();

        user.setUsername(userJob.getUserName());
        user.setResourceId(userJob.getResourceId());
        user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));
        testData.addIntegerTag("jobReference", userJob.getJobReference());
        testData.addIntegerTag("resourceId", userJob.getResourceId());
        try {
            databaseAssertionSteps.the_quote_is_in_stage(Integer.valueOf(queryMap.get("quoteJobApprovalStatusId").toString()), Integer.valueOf(queryMap.get("approvalStatusId").toString()));
        } catch (Exception e) {
            throw new PendingException("Cannot create suitable test data, this is usually down to the database sync.");
        }
    }

    @Given("^an RFM with a Multi Quote in state Jobs Awaiting Quote with \"([^\"]*)\" quote submitted")
    public void an_RFM_with_a_Multi_Quote_in_state_Jobs_Awaiting_Approval_with_number_of_quotes_submitted(String numberOfQuotesSubmitted) throws Throwable {
        Map<String, Object> loginDetails = dbHelperQuotes.getJobsWithQuotesSubmitted(numberOfQuotesSubmitted);

        if(loginDetails == null) {

            Map<String, Object> queryMap;
            queryMap = new HashMap<String, Object>();
            queryMap.put("jobType", "Quote");
            queryMap.put("profileName", "RFM");
            queryMap.put("quoteJobApprovalStatus", "AwaitingQuote");
            queryMap.put("quoteJobApprovalStatusId", 18);
            queryMap.put("approvalStatus", "AwaitingQuote");
            queryMap.put("approvalStatusId", 2);
            queryMap.put("fundingRoute", "CAPEX");
            queryMap.put("budget", "greater");
            queryMap.put("multiQuote", "Multi");
            queryMap.put("resourceTypeName", "City Resource");
            queryMap.put("useResourceTypeName", false);
            queryMap.put("quotesSubmitted", numberOfQuotesSubmitted);
            queryMap.put("aboveThreshhold", "true");
            queryMap.put("awaitingResourceSelectionBypass", "false");

            testData.put("originalProfileName", "RFM");

            createQuoteUserJob(queryMap);
        } else {
            String managerApproverName = (String) loginDetails.get("ManagerApproverName");
            String[] splitName = managerApproverName.split(" ");
            String userName = dbHelperResources.getUserNameFromFirstAndSecondNames(splitName[0], splitName[1]);
            testData.put("userName", userName);
            testData.put("jobReference", loginDetails.get("FaultReference"));
        }
    }

    @Given("^an RFM with a Multi Quote in state Awaiting Resource Selection with Bypass with a \"([^\"]*)\" funding route greater than budget")
    public void an_RFM_with_a_Multi_Quote_in_state_Awaiting_Resource_Selection_with_Bypass_with_funding_route_greater_than_budget(String fundingRoute) throws Throwable {
        if(fundingRoute.equalsIgnoreCase("BMI/INSURANCE")) {
            String[] splitFundingRoute = fundingRoute.split("/");
            if(LOCALE.equals("en-US")) {
                fundingRoute = splitFundingRoute[0];
            } else {
                fundingRoute = splitFundingRoute[1];
            }
        }
        testData.put("isSingleQuote", false);
        testData.put("fundingRoute", fundingRoute);
        testData.put("originalProfileName", "RFM");
        Map<String, Object> loginDetails = dbHelperQuotes.getJobsWithMultiQuotesAwaitingReviewWithBypass(fundingRoute);

        if(loginDetails == null) {

            Map<String, Object> queryMap;
            queryMap = new HashMap<String, Object>();
            queryMap.put("jobType", "Quote");
            queryMap.put("profileName", "RFM");
            queryMap.put("quoteJobApprovalStatus", "AwaitingResourceAssignment");
            queryMap.put("quoteJobApprovalStatusId", 3);
            queryMap.put("approvalStatus", "AwaitingApproval");
            queryMap.put("approvalStatusId", 7);
            queryMap.put("fundingRoute", fundingRoute);
            queryMap.put("budget", "greater");
            queryMap.put("multiQuote", "Multi");
            queryMap.put("resourceTypeName", "City Resource");
            queryMap.put("useResourceTypeName", false);
            queryMap.put("quotesSubmitted", 1);
            queryMap.put("aboveThreshhold", "true");
            queryMap.put("awaitingResourceSelectionBypass", "true");

            createQuoteUserJob(queryMap);
        } else {
            String managerApproverName = (String) loginDetails.get("ManagerApproverName");
            String[] splitName = managerApproverName.split(" ");
            String userName = dbHelperResources.getUserNameFromFirstAndSecondNames(splitName[0], splitName[1]);
            testData.put("userName", userName);
            testData.put("jobReference", loginDetails.get("FaultReference"));
        }
    }

    @Given("^an Invoice Approver with \"([^\"]*)\" Jobs$")
    public void an_invoice_approver_with_invoice_awaiting_approval_jobs(String jobStatus) throws Throwable {

        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", "");
        queryMap.put("jobType", jobStatus);
        queryMap.put("fundingRoute", "");

        try {
            getUserJob(queryMap);
        } catch (PendingException e) {
            createJobData(jobStatus);

            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }
    }

    @Given("^a supplier who does not use the portal for invoicing$")
    public void a_supplier_who_does_not_use_the_portal_for_invoicing() {
        testData.put("supplierNotUsingPortalForInvoicing", true);
        testData.put("resourceTypeName", "Contractor");
        testData.put("profileName", "Supply Only");
    }

    @Given("^a portal user with a \"([^\"]*)\" (?:profile|permission) and with \"([^\"]*)\" Jobs$")
    public void portal_user_with_profile_and_jobs_with_status(String profileName, String jobStatus) throws Throwable {

        // Check if its an additional approver
        if("Additional Approver".equalsIgnoreCase(profileName) || "Additional Final Approver".equalsIgnoreCase(profileName) ) {
            //go away and get the profile
            profileName = dbHelperQuotes.getApprover(profileName);
        }

        testData.put("profileName", profileName);
        testData.put("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("jobType", jobStatus);
        queryMap.put("fundingRoute", "N/A");

        try {
            // getUserJob(queryMap); // Commenting out to avoid bad data
            throw new PendingException();

        } catch (Exception e) {
            createJobData(jobStatus);

            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }

        if (!dbHelperResources.getResourceProfileName(testData.getInt("resourceId")).equals(profileName)) {
            for (Map<String, Object> resource : dbHelperOrganisation.getOrganisationForSite(testData.getInt("siteId"))) {
                String resourceProfileName = (String) resource.get("ResourceProfileName");
                if (resourceProfileName.equals(profileName)) {
                    testData.put("resourceId", resource.get("ResourceId"));
                    testData.put("userName", resource.get("UserName"));
                }
            }
        }
    }

    @Given("^a final approver and with an invoice awaiting final approval for \"([^\"]*)\" funding route$")
    public void a_final_approver_and_with_an_invoice_awaiting_final_approval_for_funding_route(String fundingRoute) throws Throwable {
        String jobStatus = "Invoice Awaiting Final Approval";
        String profileName = "Head Of Finance";

        testData.put("profileName", profileName);
        testData.put("jobStatus", jobStatus);
        testData.put("fundingRoute", fundingRoute);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("jobType", jobStatus);
        queryMap.put("fundingRoute", fundingRoute);

        try {
            getUserJob(queryMap);

        } catch (Exception e) {
            createJobData(jobStatus);
            getUserJob(queryMap);

            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }
    }

    @Given("^a \"([^\"]*)\" user profile with an invoice awaiting approval for \"([^\"]*)\" funding route$")
    public void a_user_profile_with_an_invoice_awaiting_approval_for_funding_route(String profileName, String fundingRoute) throws Throwable {
        String jobStatus = "Invoices Awaiting Approval";

        testData.put("profileName", profileName);
        testData.put("jobStatus", jobStatus);
        testData.put("fundingRoute", fundingRoute);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("jobType", jobStatus);
        queryMap.put("fundingRoute", fundingRoute);

        try {
            createJobData(jobStatus);

        } catch (Exception e) {
            getUserJob(queryMap);

            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }
    }

    @Given("^a \"([^\"]*)\" with a job Awaiting Feedback Response$")
    public void a_RFM_with_a_job_Awaiting_Feedback_Response(String resourceType) throws Throwable {
        Job job = jobCreationHelper.createJobInStatus("Awaiting Feedback Response");
        int jobReference = job.getJobReference();

        Map<String, Object> rfm = dbHelperResources.getResourceForJob(resourceType, jobReference);
        CommonSteps.assertDataFound(rfm);

        String username = (String) rfm.get("UserName");

        runtimeState.scenario.write("Job Reference: " + jobReference);
        runtimeState.scenario.write(resourceType + ": " + username);

        // save data for later use
        testData.put("profileName", resourceType);
        testData.put("jobReference", jobReference);
        testData.put("userName", username);
        testData.put("resourceId", rfm.get("ResourceId"));
        testData.put("siteId", job.getSiteId());

        // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
        runtimeState.timestamp = dbHelper.getDbTimestamp();

        if (resourceType.equals("Operations Director")) {
            getWebDriver().manage().timeouts().implicitlyWait(MAX_SYNC_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    @Given("^a Head of Finance with \"([^\"]*)\"$")
    public void a__resourceProfile_with_jobs(String jobType) throws Throwable {
        Map<String, Object> dbData = dbHelperResources.getResourceWithMenuAndRole("Portal_Department_Head_Section_Access", "Head of Finance");
        int epochId = dbHelperResources.getEpochId((Integer) dbData.get("Id"));

        List<Map<String, Object>> invoices = dbHelperInvoices.getContractorInvoicesAwaitingApproval();

        String orderRef = null;
        String invoiceNumber = null;
        int jobRef = 0;

        boolean found = false;
        for (Map<String, Object> invoice : invoices) {
            if ((Integer) invoice.get("EpochId") == epochId) {
                found = true;
                orderRef = (String) invoice.get("OrderRef");
                invoiceNumber = (String) invoice.get("InvNum");
                jobRef = (int) invoice.get("Job Ref");
                break;
            }
        }

        if (!found) {
            orderRef = (String) invoices.get(0).get("OrderRef");
            Integer invoiceID = (Integer) invoices.get(0).get("Id");
            invoiceNumber = (String) invoices.get(0).get("InvNum");
            jobRef = (int) invoices.get(0).get("Job Ref");
            dbHelperInvoices.updateToGeneralOrder(orderRef);
            dbHelperInvoices.assignToEpochId(invoiceID, epochId);
        }

        testData.put("orderRef", orderRef);
        testData.put("invoiceNumber", invoiceNumber);
        testData.put("jobReference", jobRef);
        testData.put("userName", dbData.get("UserName"));
        testData.put("resourceId", dbData.get("Id"));
        testData.put("profileName", dbData.get("UserProfileName"));

        user.setUsername(null); // login using testdata username
    }

    @Given("^a final approver with invoices awaiting final approval$")
    public void a_final_approver_with_invoices_awaiting_final_approval() throws Throwable {
        Map<String, Object> dbData = dbHelperResources.getResourceWithMenuAndRole("Portal_Invoice_Stage_2_Approver", "Head of Finance");

        Map<String, Object> invoice = dbHelperInvoices.getContractorInvoiceAwaitingFinalApproval();
        testData.put("jobReference", invoice.get("Job Ref"));
        testData.put("impersonatedResourceName", dbData.get("Name"));
        testData.put("userName", dbData.get("UserName"));
        testData.put("resourceId", dbData.get("Id"));
        testData.put("profileName", dbData.get("UserProfileName"));
        testData.put("initialApprover", invoice.get("Initial Approver"));

        String[] initialApprover = invoice.get("Initial Approver").toString().split(" ");
        String initialApproverFirstName = initialApprover[0].trim();
        String initialApproverLastName = initialApprover[1].trim();
        testData.put("initialApproverFirstName", initialApproverFirstName);
        testData.put("initialApproverLastName", initialApproverLastName);

        runtimeState.scenario.write("Username: " + dbData.get("UserName"));
        runtimeState.scenario.write("Resource Name: " + dbData.get("Name"));
        runtimeState.scenario.write("resourceId: " + dbData.get("Id"));

        user.setUsername(null); // login using testdata username
    }

    @Given("^a \"([^\"]*)\" with jobs \"([^\"]*)\"$")
    public void a_Line_Manager_with_jobs(String resourceProfile, String jobType) throws Throwable {
        String menuItem = testData.getString("menuItem");
        if (menuItem == null) {
            switch (jobType) {
            case "Awaiting Reallocation":
                menuItem = "Resources > Jobs Awaiting Reallocation";
                break;

            case "Invoices Awaiting Approval":
                menuItem = "Invoices > Invoices Awaiting Approval";
                break;
            default:
                menuItem = jobType;
            }
        }

        // get manager
        Integer managerResourceId = null;
        List<Map<String, Object>> lineManagers = dbHelperOrganisation.getResourcesWithProfile(resourceProfile);
        for (Map<String, Object> manager : lineManagers) {
            List<Map<String, Object>> menuItems = dbHelperResources.getMenuItems((Integer) manager.get("ResourceId"), resourceProfile);

            if (menuItems.toString().contains(menuItem)) {
                managerResourceId = (Integer) manager.get("ResourceId");
                testData.put("userName", manager.get("UserName"));   // store for login step
                runtimeState.scenario.write("Operational Manager : " + manager.get("ResourceName") + " (" + manager.get("ResourceProfileName") + ")");
                break;
            }
        }
        assertNotNull(resourceProfile + " not found with menu: " + menuItem, managerResourceId);

        // get city tech
        List<Map<String, Object>> org = dbHelperOrganisation.getOrgStructureBelowResource(managerResourceId);
        for (Map<String, Object> resource : org) {
            if (resource.get("OrgStructureType").equals("Tech Position")) {
                testData.put("assignToResourceId", resource.get("ResourceId"));
                testData.put("resourceName", resource.get("ResourceName"));
                break;
            }
        }

        createJobData(jobType);

        user.setUsername(null);    // override login user, set below!
        testData.removeTag("applicationUser");

        Integer unavailableResourceId = testData.getInt("jobResourceId");

        List<Map<String, Object>> resourceAvailabilityList = dbHelperResources.getResourceAvailability(unavailableResourceId);
        CommonSteps.assertDataFound(resourceAvailabilityList);
        runtimeState.scenario.write("Absent resource  : " + resourceAvailabilityList.get(0).get("Name"));
        runtimeState.scenario.write("Absent resourceId: " + unavailableResourceId);
        for (Map<String, Object> resourceAvailability : resourceAvailabilityList) {
            runtimeState.scenario.write("Absence starts at: " + resourceAvailability.get("StartAt"));
            runtimeState.scenario.write("Absence ends at  : " + resourceAvailability.get("EndAt"));
            runtimeState.scenario.write("Covering resource: " + resourceAvailability.get("CoveringResourceName"));
        }

        testData.put("resourceId", managerResourceId);
        runtimeState.scenario.write("Job reference       : " + testData.getInt("jobReference"));
    }


    @Given("^a RFM with a completed job whose resource is absent$")
    public void a_RFM_with_a_completed_job_whose_resource_is_absent() throws Throwable {
        //        Map<String, Object> dbData = null;
        Integer unavailableResourceId;
        try {

            Map<String, Object> dbData = dbHelperJobs.getCompletedJobWithAbsentResource();

            testData.put("profileName", "RFM");

            CommonSteps.assertDataFound(dbData);

            unavailableResourceId = (Integer) dbData.get("ResourceId");
            testData.put("jobResourceId", unavailableResourceId);
            testData.put("jobReference", dbData.get("JobReference"));

        } catch (PendingException e) {
            createJobData("Complete");

            user.setUsername(null);    // override login user, set below!
            testData.removeTag("applicationUser");

            unavailableResourceId = testData.getInt("jobResourceId");
        }

        List<Map<String, Object>> resourceAvailabilityList = dbHelperResources.getResourceAvailability(unavailableResourceId);
        CommonSteps.assertDataFound(resourceAvailabilityList);
        runtimeState.scenario.write("Absent resource  : " + resourceAvailabilityList.get(0).get("Name"));
        runtimeState.scenario.write("Absent resourceId: " + unavailableResourceId);
        for (Map<String, Object> resourceAvailability : resourceAvailabilityList) {
            runtimeState.scenario.write("Absence starts at: " + resourceAvailability.get("StartAt"));
            runtimeState.scenario.write("Absence ends at  : " + resourceAvailability.get("EndAt"));
            runtimeState.scenario.write("Covering resource: " + resourceAvailability.get("CoveringResourceName"));
        }

        // now find the jobs line manager (could be an RFM or a Supervisor)
        Map<String, Object> lineManager = dbHelperOrganisation.getLineManager(unavailableResourceId);
        if (lineManager == null) lineManager = dbHelperOrganisation.getResourceWithProfile("RFM", unavailableResourceId).get(0);

        testData.put("resourceId", lineManager.get("ResourceId"));
        testData.put("userName", lineManager.get("UserName"));   // store for login step

        runtimeState.scenario.write("Line Manager : " + lineManager.get("ResourceName") + " (" + lineManager.get("ResourceProfileName") + ")");
        runtimeState.scenario.write("Job reference: " + testData.getInt("jobReference"));
    }

    @Given("^a \"([^\"]*)\" with jobs \"([^\"]*)\" and ((?:no|with)) covering resource$")
    public void a_Line_Manager_with_jobs_and_no_covering_resource(String resourceProfileName, String jobStatus, String coveringResource) throws Throwable {
        testData.put("withCoveringResource", coveringResource.equals("with") ? true : false);
        testData.put("menuItem", "Resources > Resources with upcoming unavailability");
        a_Line_Manager_with_jobs(resourceProfileName, jobStatus);
    }


    @Given("^a portal user with a \"([^\"]*)\" (?:profile|permission) and with \"([^\"]*)\" Quote Jobs$")
    public void portal_user_with_profile_and_quote_jobs_with_status(String profileName, String jobStatus) throws Throwable {

        testData.put("originalProfileName", profileName);

        // Check if its an additional approver
        if("Additional Approver".equalsIgnoreCase(profileName) || "Additional Final Approver".equalsIgnoreCase(profileName) ) {
            //go away and get the profile
            profileName = dbHelperQuotes.getApprover(profileName);
        }

        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobStatus", jobStatus);
        testData.put("jobState", jobStatus);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("jobType", jobStatus);

        try {
            getUserJob(queryMap);

        } catch (PendingException e) {
            getQuoteJobWithCriteria(profileName, "any", "Quote", jobStatus, "ignore", "ignore", "ignore", "greater");

            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }
    }

    @Given("^a portal user with a \"([^\"]*)\" permission and with \"([^\"]*)\" Jobs ((?:with|with out)) additional resources$")
    public void portal_user_with_permission_and_jobs_with_status_with_resources(String profileName, String jobStatus, String additionalResources) throws Throwable {
        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobStatus", jobStatus);
        testData.put("additionalResources", additionalResources);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("jobType", jobStatus);
        queryMap.put("additionalResources", additionalResources);

        try {
            getUserJob(queryMap);

        } catch (PendingException e) {
            createJobData(jobStatus);

            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }
    }

    @Given("^a portal user with a \"([^\"]*)\" profile and with \"([^\"]*)\" Jobs and a (.*) Quote$")
    public void portal_user_with_profile_and_jobs_with_status_and_a_quote(String profileName, String jobStatus, String quoteType) throws Exception {
        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("quoteType", quoteType);
        queryMap.put("multiQuote", "ignore");

        getUserJob(queryMap);
    }

    @Given("^a portal user with a \"([^\"]*)\" profile and with \"([^\"]*)\" Jobs and a (.*) Quote ((?:less|greater)) than budget$")
    public void portal_user_with_profile_and_jobs_with_status_and_a_quote(String profileName, String jobStatus, String quoteType, String budget) throws Exception {
        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobStatus", jobStatus);
        testData.addStringTag("fundingRoute", quoteType);
        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("quoteType", quoteType);
        queryMap.put("budget", budget);
        queryMap.put("multiQuote", "ignore");

        getUserJob(queryMap);
    }

    @Given("^a portal user with a \"([^\"]*)\" profile and a \"([^\"]*)\" job with quote approval status \"([^\"]*)\" and approval status \"([^\"]*)\" with a \"([^\"]*)\" funding route$")
    public void a_portal_user_with_a_profile_and_a_job_with_quote_approval_status_and_approval_status_with_a_funding_route(String profileName, String jobType, String quoteJobApprovalStatus,
            String approvalStatus, String fundingRoute) throws Throwable {
        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobType", jobType);
        // user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobType", jobType);
        queryMap.put("profileName", profileName);
        queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus);
        queryMap.put("approvalStatus", approvalStatus);
        queryMap.put("fundingRoute", fundingRoute);
        queryMap.put("budget", "ignore");
        queryMap.put("multiQuote", "ignore");

        getUserJob(queryMap);
    }

    @Given("^a portal user with a \"([^\"]*)\" profile and a \"([^\"]*)\" job with quote approval status \"([^\"]*)\" and approval status \"([^\"]*)\" with a \"([^\"]*)\" funding route ((?:less|greater)) than budget$")
    public void a_portal_user_with_a_profile_and_a_job_with_quote_approval_status_and_approval_status_with_a_funding_route_and_budget(String profileName, String jobType, String quoteJobApprovalStatus,
            String approvalStatus, String fundingRoute, String budget) throws Throwable {
        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobType", jobType);
        // user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobType", jobType);
        queryMap.put("profileName", profileName);
        queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus);
        queryMap.put("approvalStatus", approvalStatus);
        queryMap.put("fundingRoute", fundingRoute);
        queryMap.put("budget", budget);
        queryMap.put("multiQuote", "false");

        getUserJob(queryMap);
    }

    @Given("^a portal user with a \"([^\"]*)\" profile and with \"([^\"]*)\" \"([^\"]*)\" Jobs$")
    public void portal_user_with_profile_and_jobs_with_status(String profileName, String jobType, String jobStatus) throws Throwable {
        setResourceUserProfileType(profileName);

        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("profileName", profileName);
        queryMap.put("jobType", jobType);
        queryMap.put("multiQuote", "ignore");
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));

        try {
            getUserJob(queryMap);

        } catch (PendingException e) {
            createJobData(jobStatus);

            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }
    }

    /**
     * @param profileName - RFM, BTFM
     * @param multiQuote - Single, Multi or n/a
     * @param jobType - Quote only at the moment
     * @param jobState : Open Quote Requests, Jobs Awaiting Quote, Jobs Awaiting
     *            Resource Selection, Quotes Awaiting Review, Quotes in Query,
     *            Quotes with Query Response
     * @throws Throwable
     */
    @Given("^a \"([^\"]*)\" with a \"([^\"]*)\" \"([^\"]*)\" in state \"([^\"]*)\"$")
    public void a_with_a_quote_job_in_state(String profileName, String multiQuote, String jobType, String jobState) throws Throwable {
        a_with_a_quote_job_in_state_with_a_funding_route(profileName, multiQuote, jobType, jobState, "ignore");
    }

    /**
     * @param profileName - RFM, BTFM
     * @param multiQuote - Single, Multi or n/a
     * @param jobType - Quote only at the moment
     * @param jobState : Open Quote Requests, Jobs Awaiting Quote, Jobs Awaiting
     *            Resource Selection, Quotes Awaiting Review, Quotes in Query,
     *            Quotes with Query Response
     * @param quotesSubmitted : Number of quotes to have been submitted
     * @throws Throwable
     */
    @Given("^a \"([^\"]*)\" with a \"([^\"]*)\" \"([^\"]*)\" in state \"([^\"]*)\" with \"([^\"]*)\" quote submitted$")
    public void a_with_a_quote_job_in_state_with_quotes(String profileName, String multiQuote, String jobType, String jobState, String quotesSubmitted) throws Throwable {
        getQuoteJobWithCriteria(profileName, multiQuote, jobType, jobState, "ignore", "ignore", quotesSubmitted, "ignore");
    }

    /**
     * @param profileName - RFM, BTFM
     * @param multiQuote - Single, Multi or n/a
     * @param jobType - Quote only at the moment
     * @param jobState : Open Quote Requests, Jobs Awaiting Quote, Jobs Awaiting
     *            Resource Selection, Quotes Awaiting Review, Quotes in Query,
     *            Quotes with Query Response
     * @param fundingRoute - CAPEX, OPEX, ignore
     * @throws Throwable
     */
    @Given("^a \"([^\"]*)\" with a \"([^\"]*)\" \"([^\"]*)\" in state \"([^\"]*)\" with a \"([^\"]*)\" funding route$")
    public void a_with_a_quote_job_in_state_with_a_funding_route(String profileName, String multiQuote, String jobType, String jobState, String fundingRoute) throws Throwable {
        if(fundingRoute == null) {
            fundingRoute = "ignore";
        }
        a_with_a_in_state_with_a_funding_route_with_budget(profileName, multiQuote, jobType, jobState, fundingRoute, "ignore");
    }

    private String setMultiQuote(String multiquote) {
        // Set up value for multi quote
        switch (multiquote.toLowerCase()) {
        case "multi":
            return "multi";
        case "single":
            return "single";
        default:
            return "ignore";
        }
    }

    /**
     * @param profileName - RFM, BTFM
     * @param multiQuote - Single, Multi or n/a
     * @param jobType - Quote only at the moment
     * @param jobState : Open Quote Requests, Jobs Awaiting Quote, Jobs Awaiting
     *            Resource Selection, Quotes Awaiting Review, Quotes in Query,
     *            Quotes with Query Response
     * @param fundingRoute - CAPEX, OPEX, ignore
     * @param budget - greater, less, ignore
     * @throws Throwable
     */
    @Given("^a \"([^\"]*)\" with a \"([^\"]*)\" \"([^\"]*)\" in state \"([^\"]*)\" with a \"([^\"]*)\" funding route ((?:less|greater)) than budget$")
    public void a_with_a_in_state_with_a_funding_route_with_a_budget(String profileName, String multiQuote, String jobType, String jobState, String fundingRoute, String budget) throws Throwable {
        if(fundingRoute.equalsIgnoreCase("BMI/INSURANCE")) {
            String[] splitFundingRoute = fundingRoute.split("/");
            if(LOCALE.equals("en-US")) {
                fundingRoute = splitFundingRoute[0];
            } else {
                fundingRoute = splitFundingRoute[1];
            }
        }
        String aboveThreshhold = ("greater".equalsIgnoreCase(budget)) ? "true"
                : (("less".equalsIgnoreCase(budget)) ? "false"
                        : "ignore");
        fundingRoute = "any".equalsIgnoreCase(fundingRoute) ? "ignore" : fundingRoute;
        getQuoteJobWithCriteria(profileName, multiQuote, jobType, jobState, fundingRoute, budget, "ignore", aboveThreshhold);
    }

    @Given("^a \"([^\"]*)\" with a \"([^\"]*)\" \"([^\"]*)\" in state \"([^\"]*)\" with \"([^\"]*)\" funding route ((?:less|greater)) than budget$")
    public void a_with_a_in_state_with_a_funding_route_with_budget(String profileName, String multiQuote, String jobType, String jobState, String fundingRoute, String budget) throws Throwable {
        a_with_a_in_state_with_a_funding_route_with_a_budget(profileName, multiQuote, jobType, jobState, fundingRoute, budget);
    }

    /**
     * @param profileName - RFM, BTFM
     * @param multiQuote - Single, Multi or n/a
     * @param jobType - Quote only at the moment
     * @param jobState : Open Quote Requests, Jobs Awaiting Quote, Jobs Awaiting
     *            Resource Selection, Quotes Awaiting Review, Quotes in Query,
     *            Quotes with Query Response
     * @param fundingRoute - CAPEX, OPEX, ignore
     * @param budget - greater, less, ignore
     * @throws Throwable
     */
    public void getQuoteJobWithCriteria(String profileName, String multiQuote, String jobType, String jobState, String fundingRoute, String budget, String quotesSubmitted, String aboveThreshhold) throws Throwable {
        if (testData.getString("originalProfileName") == null) {
            testData.put("originalProfileName", profileName);
        }

        // Check if its an additional approver
        if("Additional Approver".equalsIgnoreCase(profileName) || "Additional Final Approver".equalsIgnoreCase(profileName) ) {
            //go away and get the profile
            profileName = dbHelperQuotes.getApprover(profileName);
        }

        setResourceUserProfileType(profileName);

        // testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobType", jobType);
        testData.addStringTag("fundingRoute", fundingRoute);
        testData.put("jobState", jobState);

        String quoteJobApprovalStatus = null;
        String approvalStatus = null;
        String quoteJobApprovalStatusId = null;
        String approvalStatusId = null;
        String awaitingResourceSelectionBypass = "false";

        // Define quoteJobApprovalStatus, quoteJobApprovalStatusID and AwaitingApproval,
        // AwaitingApprovalID base on jobState
        // There might not be a 1 to 1 mapping. The id's are used in the query to pull
        // back data so can allow scenarios like 1,2,3
        switch (jobState) {
        case "Awaiting Quote Request Review":
            quoteJobApprovalStatus = "AwaitingQuoteRequestApproval";
            approvalStatus = "None";
            quoteJobApprovalStatusId = "1";
            approvalStatusId = "1";
            break;
        case "Quotes Awaiting Review":
        case "Awaiting Review":
            quoteJobApprovalStatus = "AwaitingManagerApproval";
            approvalStatus = "AwaitingApproval";
            quoteJobApprovalStatusId = "5";
            approvalStatusId = "7";
            quotesSubmitted = "all";
            break;
        case "Funding Requests Awaiting Review":
            quoteJobApprovalStatus = "AwaitingSeniorManagerApproval";
            approvalStatus = "AwaitingApproval";
            quoteJobApprovalStatusId = "7";
            approvalStatusId = "7";
            break;
        case "Quotes in Query":
        case "Quotes with Query Pending":
        case "Quotes With Query Pending":
            quoteJobApprovalStatus = "InResourceQuery";
            approvalStatus = "QueryResourcePending";
            quoteJobApprovalStatusId = "4";
            approvalStatusId = "5";
            quotesSubmitted = "1";
            break;
        case "Quotes with Query Response":
            quoteJobApprovalStatus = "QueryResourceAnswered"; // Should really be "AwaitingManagerApproval";
            approvalStatus = "QueryResourceAnswered";
            quoteJobApprovalStatusId = "5";
            approvalStatusId = "6";
            quotesSubmitted = "1";
            break;
        case "Awaiting Resource Selection":
            quoteJobApprovalStatus = "AwaitingResourceAssignment";
            approvalStatus = "AwaitingApproval";
            quoteJobApprovalStatusId = "3";
            approvalStatusId = "7";
            quotesSubmitted = "1";
            aboveThreshhold = "true";
            break;
        case "Awaiting Resource Selection with Bypass":
            quoteJobApprovalStatus = "AwaitingResourceAssignment";
            approvalStatus = "AwaitingApproval";
            quoteJobApprovalStatusId = "3";
            approvalStatusId = "7";
            quotesSubmitted = "1";
            aboveThreshhold = "true";
            awaitingResourceSelectionBypass = "true";
            break;
        case "Jobs Awaiting Resource Selection":
            quoteJobApprovalStatus = "AwaitingResourceAssignment";
            approvalStatus = "AwaitingResourceAssignment";
            quoteJobApprovalStatusId = "3";
            approvalStatusId = "4";
            break;
        case "Jobs Awaiting Quote":
        case "Awaiting Quote":
            quoteJobApprovalStatus = multiQuote.equalsIgnoreCase("single") ? "AwaitingQuote" : "ItqAwaitingAcceptance";
            approvalStatus = "AwaitingQuote";
            quoteJobApprovalStatusId = "18";
            approvalStatusId = "2";
            break;
        case "ITQ Awaiting Acceptance":
            quoteJobApprovalStatus = "ItqAwaitingAcceptance";
            approvalStatus = "ItqAwaitingAcceptance";
            quoteJobApprovalStatusId = "18";
            approvalStatusId = "2";
            break;
        case "Awaiting Bypass Approval":
        case "AwaitingBypassApproval":
            quoteJobApprovalStatus = "AwaitingBypassApproval";
            approvalStatus = "AwaitingApproval";
            quoteJobApprovalStatusId = "7";
            approvalStatusId = "7";
            quotesSubmitted = "1";
            aboveThreshhold = "true";
            awaitingResourceSelectionBypass = "true";
            break;
        case "Funding Requests Pending Query Response":
        case "Funding Request Queries":
            quoteJobApprovalStatus = "QueryManagerPending";
            approvalStatus = "AwaitingApproval";
            quoteJobApprovalStatusId = "8";
            approvalStatusId = "7";
            quotesSubmitted = "all";
            aboveThreshhold = "Additional Final Approver".equalsIgnoreCase(testData.getString("originalProfileName")) || "RFM".equalsIgnoreCase(testData.getString("originalProfileName")) ? "true" : "false";
            multiQuote = "ignore";
            break;
        case "Funding Requests Awaiting Response Review":
            quoteJobApprovalStatus = "QueryManagerAnswered"; // Is this correct?
            approvalStatus = "AwaitingApproval";
            quoteJobApprovalStatusId = "9";
            approvalStatusId = "7";
            quotesSubmitted = "all";
            aboveThreshhold = "Additional Final Approver".equalsIgnoreCase(testData.getString("originalProfileName")) ? "true" : "false";
            multiQuote = "ignore";
            break;

        case "Funding Requests Awaiting Approval":
        case "AwaitingSeniorManagerApproval":
            quoteJobApprovalStatus = "AwaitingSeniorManagerApproval";
            approvalStatus = "AwaitingApproval";
            quoteJobApprovalStatusId = "6";
            approvalStatusId = "7";
            quotesSubmitted = "all";
            aboveThreshhold = "Additional Final Approver".equalsIgnoreCase(testData.getString("originalProfileName")) ? "true" : "false";
            multiQuote = "false";
            break;

        case "Funding Request Rejected":
            quoteJobApprovalStatus = "SeniorManagerRejected";
            approvalStatus = "AwaitingResourceAssignment";
            quoteJobApprovalStatusId = "12";
            approvalStatusId = "4";
            quotesSubmitted = "all";
            aboveThreshhold = "true";
            multiQuote = "ignore";
            break;

        default:
            throw new Exception("Cannot find " + jobState);
        }

        multiQuote = setMultiQuote(multiQuote);

        // Build map and get a user and job
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobType", jobType);
        queryMap.put("profileName", profileName);
        queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus);
        queryMap.put("quoteJobApprovalStatusId", quoteJobApprovalStatusId);
        queryMap.put("approvalStatus", approvalStatus);
        queryMap.put("approvalStatusId", approvalStatusId);
        queryMap.put("fundingRoute", fundingRoute);
        queryMap.put("budget", budget);
        queryMap.put("multiQuote", multiQuote);
        queryMap.put("profileName", testData.getString("profileName"));
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));
        queryMap.put("quotesSubmitted", quotesSubmitted);
        queryMap.put("aboveThreshhold", aboveThreshhold);
        queryMap.put("awaitingResourceSelectionBypass", awaitingResourceSelectionBypass);

        runtimeState.scenario.write(Arrays.toString(queryMap.entrySet().toArray()));
        try {
            // getUserJob(queryMap); //Commenting out to avoid bad data
            throw new PendingException();

        } catch (PendingException e) {
            createQuoteUserJob(queryMap);
            // Need to reset the time when the scenario actually starts due to timeline events created during test data setup
            runtimeState.timestamp = dbHelper.getDbTimestamp();
        }
    }

    @Given("^a portal user with a \"([^\"]*)\" profile and with \"([^\"]*)\" Jobs in \"([^\"]*)\" for a \"([^\"]*)\" quote$")
    public void portal_user_with_profile_and_jobs_with_status_and_sub_status_and_quote_type(String profileName, String jobStatus, String jobSubStatus, String quoteType) throws Throwable {
        a_with_a_quote_job_in_state_with_a_funding_route(profileName, quoteType, "Quote", jobSubStatus, "ignore");
    }

    // @Deprecated
    // @Given("^a portal user with a \"([^\"]*)\" profile and with \"([^\"]*)\" Jobs in
    // \"([^\"]*)\" for a \"([^\"]*)\" quote$")
    public void portal_user_with_profile_and_jobs_with_status_and_sub_status_and_quote_type_old(String profileName, String jobStatus, String jobSubStatus, String quoteType) throws Throwable {
        setResourceUserProfileType(profileName);

        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobStatus", jobStatus);
        testData.addStringTag("quoteType", quoteType);

        // Need to tidy this up
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("jobType", jobSubStatus);
        //
        queryMap.put("profileName", testData.getString("profileName"));
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));
        queryMap.put("quoteType", quoteType);

        user.setJobStatus(jobStatus);
        userJob = userJobDao.getUserJob(queryMap);
        if (userJob == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        testData.addIntegerTag("jobReference", userJob.getJobReference());
        User newUser = new User.Builder("Portal").withProfile(profileName).withUserName(userJob.getUserName()).withPassword("Password1").withResourceId(userJob.getResourceId()).build();
        user.copy(newUser);

        outputUserAndJobInfo();

        user.setUsername(userJob.getUserName());
        user.setResourceId(userJob.getResourceId());
        user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));
        testData.addIntegerTag("jobReference", userJob.getJobReference());
        testData.addIntegerTag("resourceId", userJob.getResourceId());
    }

    @Given("^a portal user with a \"([^\"]*)\" profile and with \"([^\"]*)\" Jobs in \"([^\"]*)\"$")
    public void portal_user_with_profile_and_jobs_with_status_and_sub_status(String profileName, String jobStatus, String jobSubStatus, String quoteType) throws Exception {
        testData.addStringTag("profileName", profileName);
        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        userJob = userJobDao.getUserJob(jobStatus, profileName, jobSubStatus, quoteType);
        if (userJob == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        testData.addIntegerTag("jobReference", userJob.getJobReference());
        User newUser = new User.Builder("Portal").withProfile(profileName).withUserName(userJob.getUserName()).withPassword("Password1").withResourceId(userJob.getResourceId()).build();
        user.copy(newUser);

        outputUserAndJobInfo();

        user.setUsername(userJob.getUserName());
        user.setResourceId(userJob.getResourceId());
        testData.addIntegerTag("jobReference", userJob.getJobReference());
        testData.addIntegerTag("resourceId", userJob.getResourceId());
    }

    @Given("^has \"([^\"]*)\" jobs$")
    public void I_have_open_jobs(String jobStatus) throws Exception {
        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        userJob = userJobDao.getUserJob(jobStatus, testData.getString("profileName"), "", "");
        if (userJob == null) {
            throw new PendingException("Cannot find suitable data for test");
        }

        user.setUsername(userJob.getUserName());
        user.setResourceId(userJob.getResourceId());
        testData.addStringTag("userNameToLogin", userJob.getUserName());
        testData.addIntegerTag("jobReference", userJob.getJobReference());
        testData.addIntegerTag("resourceId", userJob.getResourceId());

        outputUserAndJobInfo();
    }

    @Given("^a \"([^\"]*)\" with \"([^\"]*)\" jobs$")
    public void a_with_jobs(String profileName, String jobStatus) throws Exception {

        testData.put("profileName", profileName);
        testData.put("jobStatus", jobStatus);
        testData.put("useResourceTypeName", false);
        testData.put("resourceTypeName", "NA");

        userJob = userJobDao.getUserJob(jobStatus, profileName, "", "");
        if (userJob == null) {
            throw new PendingException("Cannot find suitable data for test");
        }

        user.setJobStatus(jobStatus);
        user.setUsername(userJob.getUserName());
        user.setResourceId(userJob.getResourceId());

        testData.put("userName", userJob.getUserName());
        testData.put("userNameToLogin", userJob.getUserName());
        testData.put("jobReference", userJob.getJobReference());
        testData.put("resourceId", userJob.getResourceId());

        outputUserAndJobInfo();
    }

    @Given("^a \"([^\"]*)\" with an? \"([^\"]*)\" job$")
    public void a_with_a_job(String profileName, String jobStatus) throws Exception {
        a_with_jobs(profileName, jobStatus);
    }

    @Given("^a Contractor with an? \"([^\"]*)\" job$")
    public void a_Contractor_with_an_job(String jobType) throws Throwable {
        Map<String, Object> dbData = jobType.equals("PPM") ? dbHelperPPM.getContractorWithOpenPpmJob() : dbHelperJobs.getContractorWithOpenJobType(jobType);
        CommonSteps.assertDataFound(dbData);

        testData.put("userName", dbData.get("UserName"));
        testData.put("resourceId", dbData.get("ResourceId"));
        testData.put("jobReference", dbData.get("JobReference"));

        outputHelper.writeMapList(dbData);
    }

    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs only assigned to me$")
    @Deprecated
    public void I_have_jobs_of_type_only_assigned_to_me(String jobType, String jobStatus) throws Exception {
        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        userJob = userJobDao.getUserJob(jobStatus, testData.getString("profileName"), jobType, "");
        if (userJob == null) {
            throw new PendingException("Cannot find suitable data for test");
        }

        user.setUsername(userJob.getUserName());
        user.setResourceId(userJob.getResourceId());
        testData.addIntegerTag("jobReference", userJob.getJobReference());
        testData.addIntegerTag("resourceId", userJob.getResourceId());

        outputUserAndJobInfo();
    }

    /**
     * @param jobType - reactive, quote
     * @param jobStatus - In Progress, Allocated
     * @param resourceCount - single, multiple
     * @param resourceAssignmentStatus - taken from the ResourceAssignmentTable
     * @throws Exception
     */
    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs only assigned to \"([^\"]*)\" resource with status \"([^\"]*)\" ((?:with|with out)) Gas ((?:in|not in)) Plant ((?:with|with out)) Asset$")
    public void I_have_jobs_of_type_only_assigned_to_me(String jobType, String jobStatus, String resourceCount, String resourceAssignmentStatus, String gasUsage, String inPlant, String withAsset)
            throws Exception {
        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobStatus);
        testData.addStringTag("resourceAssignmentStatus", resourceAssignmentStatus);
        Boolean gas = "with out".equalsIgnoreCase(gasUsage) ? false
                : true;
        Boolean plant = "not in".equalsIgnoreCase(inPlant) ? false
                : true;
        Boolean asset = "with out".equalsIgnoreCase(withAsset) ? false
                : true;
        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("jobType", jobType);
        queryMap.put("resourceCount", resourceCount);
        queryMap.put("resourceAssignmentStatus", resourceAssignmentStatus);
        queryMap.put("gasUsage", gas);
        queryMap.put("inPlant", plant);
        queryMap.put("asset", asset);
        queryMap.put("profileName", testData.getString("profileName"));
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));

        getUserJob(queryMap);
    }

    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs only assigned to \"([^\"]*)\" resource with status \"([^\"]*)\" ((?:with|without)) Gas used and ((?:with|without)) a Leak Check and ((?:is|is not)) required to return$")
    public void I_have_a_gas_job_with_previous_(String jobType, String jobStatus, String resourceCount, String resourceAssignmentStatus, String gasUsage, String leakCheck, String returning) throws Throwable {
        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobStatus);
        testData.addStringTag("resourceAssignmentStatus", resourceAssignmentStatus);
        Boolean gas = "without".equalsIgnoreCase(gasUsage) ? false : true;
        Boolean leak = "without".equalsIgnoreCase(leakCheck) ? false : true;
        Boolean isRequiredToReturn = "is not".equalsIgnoreCase(returning) ? false : true;

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("jobType", jobType);
        queryMap.put("resourceCount", resourceCount);
        queryMap.put("resourceAssignmentStatus", resourceAssignmentStatus);
        queryMap.put("gasUsage", gas);
        queryMap.put("leakCheck", leak);
        queryMap.put("isRequiredToReturn", isRequiredToReturn);
        queryMap.put("profileName", testData.getString("profileName"));
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));

        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobStatus);
        testData.addBooleanTag("inPlant", testData.tagExist("inPlant") ? testData.getBoolean("inPlant") : true);
        testData.addBooleanTag("gasUsage", gas);
        testData.addBooleanTag("leakCheck", leak);
        testData.addBooleanTag("isRequiredToReturn", isRequiredToReturn);
        testData.addBooleanTag("asset", testData.tagExist("asset") ? testData.getBoolean("inPlant") : true);
        testData.addStringTag("resourceAssignmentStatus", resourceAssignmentStatus);

        try {
            getUserJob(queryMap);

        } catch (PendingException e) {
            createJobData(jobStatus);
        }
    }

    /**
     * Finds or creates a job in the given status and data requirements
     *
     * @param jobType - currently only reactive is supported
     * @param jobResourceStatus - list from JobCreationHelper.createJobInStatus (Only specific case values will create the site visit)
     * @param gasUsage - Has the site visit to use gas, only valid if assetType = HVAC|Refrigeration|Unknown
     * @param leakCheck - Has the site visit to use a leak check, only valid if assetType = HVAC|Refrigeration|Unknown
     * @param assetType - Determines the time of asset to be selected when creating the job and updating the job - HVAC(Gas)|Refrigeration(Gas)|Unknown(Gas)|Non Gas(self explanatory)
     * @param returning - Should the leak check for a returning.  Only valid for assetType in HVAC|Refrigeration|Unknown and leakCheck = with
     * @throws Throwable
     */
    @Given("^a \"([^\"]*)\" with a job for an asset that uses gas and a forced returning status$")
    public void a_with_a_job_for_an_asset_that_uses_gas_and_a_forced_return_status(String userProfileName) throws Throwable {
        testData.put("profileName", userProfileName);

        testData.put("usesGas", true);      // asset type uses gas
        testData.put("gasUsage", true);     // gas cylinder used
        testData.put("leakCheck", true);
        testData.put("isRequiredToReturn", true);

        List<String> gasAssetTypes = dbHelperGas.getGasAssetTypes();
        String assetType = gasAssetTypes.get(RandomUtils.nextInt(0, gasAssetTypes.size()-1));
        testData.put("assetType", assetType);

        Boolean inPlant = "Refrigeration".equals(assetType) ? true : false;
        testData.put("inPlant",inPlant);

        testData.put("asset", true);    // true for HVAC or Refrigeration asset types

        createJobData("In Progress / Returning");
    }

    /**
     * Finds or creates a job in the given status and data requirements
     *
     * @param jobType - currently only reactive is supported
     * @param jobResourceStatus - list from JobCreationHelper.createJobInStatus (Only specific case values will create the site visit)
     * @param gasUsage - Has the site visit to use gas, only valid if assetType = HVAC|Refrigeration|Unknown
     * @param leakCheck - Has the site visit to use a leak check, only valid if assetType = HVAC|Refrigeration|Unknown
     * @param assetType - Determines the time of asset to be selected when creating the job and updating the job - HVAC(Gas)|Refrigeration(Gas)|Unknown(Gas)|Non Gas(self explanatory)
     * @param returning - Should the leak check for a returning.  Only valid for assetType in HVAC|Refrigeration|Unknown and leakCheck = with
     * @throws Throwable
     */
    @Given("^a \"([^\"]*)\" with a job for an asset that uses gas$")
    public void a_with_a_job_for_an_asset_that_uses_gas(String userProfileName) throws Throwable {
        testData.put("usesGas", true);      // asset type uses gas
        testData.put("gasUsage", true);     // gas cylinder used
        testData.put("leakCheck", true);
        testData.put("isRequiredToReturn", false);
        testData.put("profileName", userProfileName);
        testData.put("asset", true);    // true for HVAC or Refrigeration asset types

        List<String> gasAssetTypes = dbHelperGas.getGasAssetTypes();
        String assetType = gasAssetTypes.get(RandomUtils.nextInt(0, gasAssetTypes.size()-1));
        testData.put("assetType", assetType);

        Boolean inPlant = "Refrigeration".equals(assetType) ? true : false;
        testData.put("inPlant", inPlant);

        createJobData("In Progress");
    }

    /**
     * Finds or creates a job in the given status and data requirements
     *
     * @param jobType - currently only reactive is supported
     * @param jobResourceStatus - list from JobCreationHelper.createJobInStatus (Only specific case values will create the site visit)
     * @param gasUsage - Has the site visit to use gas, only valid if assetType = HVAC|Refrigeration|Unknown
     * @param leakCheck - Has the site visit to use a leak check, only valid if assetType = HVAC|Refrigeration|Unknown
     * @param assetType - Determines the time of asset to be selected when creating the job and updating the job - HVAC(Gas)|Refrigeration(Gas)|Unknown(Gas)|Non Gas(self explanatory)
     * @param returning - Should the leak check for a returning.  Only valid for assetType in HVAC|Refrigeration|Unknown and leakCheck = with
     * @throws Throwable
     */
    @Given("^has \"([^\"]*)\" \"([^\"]*)\" job ((?:with|without)) Gas used and ((?:with|without)) a Leak Check and a ((?:HVAC|Refrigeration|Unknown|Non gas)) asset and ((?:is|is not)) required to return$")
    public void I_have_a_gas_job_with_possible_asset(String jobType, String jobResourceStatus, String gasUsage, String leakCheck, String assetType, String returning) throws Throwable {
        String jobStatus = jobResourceStatus.split("\\/")[0].trim();
        String resourceAssignmentStatus = jobResourceStatus.split("\\/")[1].trim();

        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobResourceStatus.split("\\/")[0]);
        testData.addStringTag("resourceAssignmentStatus", jobResourceStatus.split("\\/")[1]);

        Boolean gas = "without".equalsIgnoreCase(gasUsage) ? false : true;
        Boolean leak = "without".equalsIgnoreCase(leakCheck) ? false : true;
        Boolean isRequiredToReturn = "is not".equalsIgnoreCase(returning) ? false : true;
        Boolean inPlant = "refrigeration".equalsIgnoreCase(assetType) ? true : false;
        Boolean usesGas = "Non gas".equalsIgnoreCase(assetType) ? false : true;

        testData.put("inPlant",inPlant);
        testData.put("usesGas", usesGas);


        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("jobType", jobType);
        queryMap.put("resourceCount", 1);
        queryMap.put("resourceAssignmentStatus", resourceAssignmentStatus);
        queryMap.put("gasUsage", gas);
        queryMap.put("leakCheck", leak);
        queryMap.put("usesGas", usesGas);
        queryMap.put("inPlant", inPlant);
        queryMap.put("asset", "Unknown".equalsIgnoreCase(assetType) ? false : true);
        queryMap.put("isRequiredToReturn", isRequiredToReturn);
        queryMap.put("profileName", testData.getString("profileName"));
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));

        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobStatus);
        testData.addBooleanTag("gasUsage", gas);
        testData.addBooleanTag("leakCheck", leak);
        testData.addBooleanTag("isRequiredToReturn", isRequiredToReturn);
        testData.addBooleanTag("asset", "Unknown".equalsIgnoreCase(assetType) ? false : true);
        testData.addStringTag("resourceAssignmentStatus", resourceAssignmentStatus);
        testData.addStringTag("assetType", assetType);

        try {
            getUserJob(queryMap);

        } catch (PendingException e) {
            createJobData(jobResourceStatus);
        }
    }

    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs only assigned to \"([^\"]*)\" resource with status \"([^\"]*)\" ((?:with|without)) Gas used and ((?:with|without)) a Leak Check$")
    public void I_have_a_gas_job_with_previous_(String jobType, String jobStatus, String resourceCount, String resourceAssignmentStatus, String gasUsage, String leakCheck) throws Exception {
        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobStatus);
        testData.addStringTag("resourceAssignmentStatus", resourceAssignmentStatus);
        Boolean gas = "without".equalsIgnoreCase(gasUsage) ? false
                : true;
        Boolean leak = "without".equalsIgnoreCase(leakCheck) ? false
                : true;

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("jobType", jobType);
        queryMap.put("resourceCount", resourceCount);
        queryMap.put("resourceAssignmentStatus", resourceAssignmentStatus);
        queryMap.put("gasUsage", gas);
        queryMap.put("leakCheck", leak);
        queryMap.put("profileName", testData.getString("profileName"));
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));

        getUserJob(queryMap);
    }

    /**
     * @param jobType - reactive, quote
     * @param jobStatus - In Progress, Allocated
     * @param resourceCount - single, multiple
     * @param resourceAssignmentStatus - taken from the ResourceAssignmentTable
     * @throws Exception
     */
    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs only assigned to \"([^\"]*)\" resource with status \"([^\"]*)\"$")
    public void I_have_jobs_of_type_only_assigned_to_me(String jobType, String jobStatus, String resourceCount, String resourceAssignmentStatus) throws Exception {
        testData.put("jobType", jobType);
        testData.put("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("jobType", jobType);
        queryMap.put("resourceCount", resourceCount);
        queryMap.put("resourceAssignmentStatus", resourceAssignmentStatus);
        queryMap.put("profileName", testData.getString("profileName"));
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));
        queryMap.put("transferJob", "ignore");

        getUserJob(queryMap);
    }

    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs only assigned to \"([^\"]*)\" resource with status \"([^\"]*)\" ((?:with|without)) transfer$")
    public void I_have_jobs_of_type_only_assigned_to_me_transfer(String jobType, String jobStatus, String resourceCount, String resourceAssignmentStatus, String transferJob) throws Throwable {
        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        Map<String, Object> queryMap;
        queryMap = new HashMap<String, Object>();
        queryMap.put("jobStatus", jobStatus);
        queryMap.put("jobType", jobType);
        queryMap.put("resourceCount", resourceCount);
        queryMap.put("resourceAssignmentStatus", resourceAssignmentStatus);
        queryMap.put("profileName", testData.getString("profileName"));
        queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
        queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));
        queryMap.put("transferJob", transferJob);

        try {
            getUserJob(queryMap);

        } catch (Exception e) {
            jobStatus = (transferJob.equalsIgnoreCase("With" )? "Logged / Work Transferred" : "Logged");
            createJobData(jobStatus);
        }
    }

    public void createJobData(String jobStatus) throws Throwable {
        runtimeState.scenario.write("Creating job in status: " + jobStatus);

        Job job;
        switch (jobStatus) {
        case "Awaiting Reallocation" :
            if (testData.get("faultPriorityId") != null) {
                String faultPriority = dbHelper.getFaultPriorityFromId(testData.getInt("faultPriorityId"));
                job = jobCreationHelper.createJobInStatus("In Progress / Awaiting Reallocation", faultPriority);
            } else {
                job = jobCreationHelper.createJobInStatus("In Progress / Awaiting Reallocation");
            }
            testData.put("useResourceTypeName", true);
            break;

        case "Awaiting Feedback Response":
            throw new Exception("Use alternative method!");

        default:
            if (testData.get("faultPriorityId") != null) {
                String faultPriority = dbHelper.getFaultPriorityFromId(testData.getInt("faultPriorityId"));
                job = jobCreationHelper.createJobInStatus(jobStatus, faultPriority);
            } else {
                job = jobCreationHelper.createJobInStatus(jobStatus);
            }

            // override with debug data ...
            //            job.setJobReference(20143336);
            //            job.setSiteId(539);
            //            job.setSpecificResourceRequestId(19583);
            //            testData.put("jobReference", 20143336);
            //            testData.put("resourceId", 19583);
            //            testData.put("userName", "C000010_tech");
        }

        testData.put("jobReference", job.getJobReference());
        testData.put("siteId", job.getSiteId());
        testData.put("siteName", dbHelperSites.getSiteName(job.getSiteId()));
        testData.put("jobResourceId", testData.getInt("resourceId"));
        String profileName = testData.getString("profileName");
        if (testData.getInt("resourceId") != null && (profileName == null || profileName.equalsIgnoreCase("NA"))) {
            profileName = dbHelperResources.getResourceUserProfileName(testData.getInt("resourceId"));
            runtimeState.scenario.write("profileName: " + profileName);
            if (profileName != null) {
                testData.put("profileName", profileName);
            }
        }

        if ("RFM".equals(profileName)) {
            // get the RFM for the new job
            Map<String, Object> dbData = dbHelperSites.getRfmForSite(testData.getInt("siteId"));
            int rfmResourceId = Integer.valueOf(dbData.get("ResourceId").toString());
            testData.put("applicationUser", applicationUserDao.getByResourceId(rfmResourceId));

            user.setUsername(dbHelperResources.getResourceName(rfmResourceId));
            user.setResourceId(rfmResourceId);
            testData.put("resourceId", rfmResourceId);

        } else if ("Supply Only".equals(profileName)) {
            userJob = userJobDao.getSupplierWithNoInvoice(job.getJobReference());
            user.setUsername(userJob.getUserName());
            user.setResourceId(userJob.getResourceId());
            testData.put("resourceId", userJob.getResourceId());
            testData.put("userName", userJob.getUserName());
            int activeFlag = dbHelperInvoices.getOnlineInvoicingActiveFlagForUser(userJob.getResourceId());

            if (activeFlag == 0) {
                dbHelperInvoices.updateOnlineInvoicingActiveFlagForUser(userJob.getResourceId());
            }

            user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));

        } else if ("Invoice Approver".equals(profileName)) {
            userJob = userJobDao.getInvoiceApproverWithInvoicesAwaitingApprovalJobs(job.getJobReference());
            user.setUsername(userJob.getUserName());
            user.setResourceId(userJob.getResourceId());
            testData.put("resourceId", userJob.getResourceId());
            testData.put("userName", userJob.getUserName());
            user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));

        } else if (testData.getBoolean("usesGas")) {
            // FGAS jobs should have all the test data setup by now - so nothing left to do!

        } else {
            if ( "Contractor Admin".equals(profileName) ) {
                testData.put("useResourceTypeName", false);
            } else if (testData.getBooleanTrueFalseOrNull("useResourceTypeName") == null && testData.getString("resourceTypeName") != null) {
                testData.put("useResourceTypeName", true);
            }
            userJob = userJobDao.getUserForJobResource(job.getJobReference(), testData.getString("profileName"), testData.getBoolean("useResourceTypeName"), testData.getString("resourceTypeName"));
            assertDataFound("Cannot create suitable test data, this is usually down to the database sync.", userJob);

            user.setUsername(userJob.getUserName());
            user.setResourceId(userJob.getResourceId());
            testData.put("resourceId", userJob.getResourceId());
            user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));
        }
    }

    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs only assigned to \"([^\"]*)\" resource with status \"([^\"]*)\" or \"([^\"]*)\" ((?:with|with out)) Gas ((?:in|not in)) Plant ((?:with|with out)) Asset$")
    public void I_have_jobs_of_type_only_assigned_to_me_gas(String jobType, String jobStatus, String resourceCount, String resourceAssignmentStatus, String resourceAssignmentStatusAlt, String gasUsage, String inPlant, String withAsset) throws Throwable {
        Boolean gas = "with out".equalsIgnoreCase(gasUsage) ? false : true;
        Boolean plant = "not in".equalsIgnoreCase(inPlant) ? false : true;
        Boolean asset = "with out".equalsIgnoreCase(withAsset) ? false : true;

        testData.put("jobType", jobType);
        testData.put("jobStatus", jobStatus);
        testData.put("inPlant", plant);
        testData.put("gasUsage", gas);
        testData.put("asset", asset);
        testData.put("resourceAssignmentStatus", resourceAssignmentStatus);

        try {
            String status = resourceAssignmentStatus + ", " + resourceAssignmentStatusAlt;
            I_have_jobs_of_type_only_assigned_to_me(jobType, jobStatus, resourceCount, status, gasUsage, inPlant, withAsset);

        } catch (PendingException e) {
            createJobData("In Progress");
        }
    }

    /**
     * @param jobType - reactive, quote
     * @param jobStatus - In Progress, Allocated
     * @param resourceCount - single, multiple
     * @param resourceAssignmentStatus - taken from the ResourceAssignmentTable
     * @param resourceAssignmentStatusAlt - taken from the ResourceAssignmentTable -
     *            Alternative if not found
     * @throws Throwable
     */
    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs only assigned to \"([^\"]*)\" resource with status \"([^\"]*)\" or \"([^\"]*)\"$")
    public void I_have_jobs_of_type_only_assigned_to_me(String jobType, String jobStatus, String resourceCount, String resourceAssignmentStatus, String resourceAssignmentStatusAlt) throws Throwable {
        testData.put("jobType", jobType);
        testData.put("jobStatus", jobStatus);
        String userProfileName = testData.getString("profileName");
        try {
            String status = resourceAssignmentStatus + ", " + resourceAssignmentStatusAlt;
            Map<String, Object> dbData = dbHelper.getJobWithResourceCount(userProfileName, jobType, jobStatus, resourceCount, status);

            testData.put("userName", dbData.get("UserName"));
            testData.put("resourceId", dbData.get("ResourceId"));
            testData.put("jobReference", dbData.get("JobReference"));

        } catch (Exception e) {
            if (jobStatus.equals("Logged") && (resourceAssignmentStatus.equals("Call Required") || resourceAssignmentStatus.equals("New Job Notification Sent"))) {
                jobStatus = "Logged / New Notification Sent";
            }
            createJobData(jobStatus);
        }
    }

    @Given("^has \"([^\"]*)\" \"([^\"]*)\" jobs assigned to \"([^\"]*)\" resource$")
    public void I_have_jobs_of_type_assigned_to(String jobType, String jobStatus, String resourceCount) throws Throwable {
        testData.addStringTag("jobType", jobType);
        testData.addStringTag("jobStatus", jobStatus);
        try {
            testData.put("jobType", jobType);
            testData.put("jobStatus", jobStatus);

            user.setJobStatus(jobStatus);
            Map<String, Object> queryMap;
            queryMap = new HashMap<String, Object>();
            queryMap.put("jobStatus", jobStatus);
            queryMap.put("jobType", jobType);
            queryMap.put("resourceCount", resourceCount);
            queryMap.put("profileName", testData.getString("profileName"));
            queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
            queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));
            queryMap.put("transferJob", "ignore");

            getUserJob(queryMap);

        } catch (PendingException e) {
            createJobData(jobStatus);
        }
    }

    @Given("^has \"([^\"]*)\" jobs previously declined$")
    public void I_have_jobs_previously_declined(String jobStatus) throws Exception {
        testData.addStringTag("jobStatus", jobStatus);

        user.setJobStatus(jobStatus);
        switch (jobStatus) {
        case "Open":
            appUser = applicationUserDao.getUserWithOpenJobs(testData.getString("profileName"));
            break;
        case "Awaiting Acceptance":
            appUser = applicationUserDao.getUserWithAwaitingJobsPreviouslyDeclined(testData.getString("profileName"));
            break;
        default:
            throw new Exception("Cannot find user with jobs " + jobStatus);
        }
        if (appUser == null) {
            throw new PendingException("Cannot find suitable data for test");
        }

        user.setUsername(appUser.getUserName());
        user.setResourceId(appUser.getResourceId());
        outputUserAndJobInfo();
    }

    private void outputUserAndJobInfo() {
        String msg = "Username: %s\nJob Reference: %d\nResource Id: %d\n";
        runtimeState.scenario.write(String.format(msg, userJob.getUserName(), userJob.getJobReference(), userJob.getResourceId()));
    }

    private String getOrgTypeForResource(String resourceType) {
        switch (resourceType) {
        case "City Tech": return "Tech Position";
        case "RFM": return "Region";
        case "Divisional Manager": return "Division";
        default:
            return resourceType;
        }
    }

    @Given("^a \"([^\"]*)\" with a \"([^\"]*)\" with upcoming unavailability ((?:with|without)) a covering resource assigned$")
    public void Line_Manager_and_cover_assigned_status(String targetManager, String resourceType, String cover) throws Exception {
        Integer managerResourceId = null;
        String managerName = null;
        String managerUserName = null;
        String managerUserProfile = null;
        Integer resourceId = null;
        String resourceName = "";
        String resourceProfileName = "";
        String coveringName = "";

        List<Map<String, Object>> absences = null;

        boolean found = false;
        // get a manager with the correct menu enabled
        for (Map<String, Object> manager : dbHelperResources.getResourceWithMenu("Resources > Resources with upcoming unavailability")) {
            managerResourceId = (Integer) manager.get("Id");
            managerName = (String) manager.get("Name");
            managerUserName = (String) manager.get("UserName");
            managerUserProfile = (String) manager.get("UserProfileName");

            if (managerUserProfile.equals(targetManager) || targetManager.equals("Line Manager")) {
                // get everyone who is under the managers org structure
                List<Map<String, Object>> orgForLineManager = dbHelperOrganisation.getOrgStructureBelowResource(managerResourceId);

                // get the resource from the org structure who will be absent
                String expectedOrgType = getOrgTypeForResource(resourceType);

                for (Map<String, Object> resource : orgForLineManager) {
                    String orgStructureType = (String) resource.get("OrgStructureType");
                    if (orgStructureType.equals(expectedOrgType)) {
                        // get a resource who we will make absent
                        resourceId = (Integer) resource.get("ResourceId");
                        resourceName = (String) resource.get("ResourceName");
                        resourceProfileName = (String) resource.get("ResourceProfileName");

                        // delete all previous absences
                        dbHelperResources.deleteAbsences(resourceId);

                        found = true;
                        break;
                    }
                }
            }
            if (found) break;
        }

        Integer coveringResourceId = null;
        if (cover.equals("with")) {
            // get a covering resource thats part of an organisation, thats not the absentResource
            List<Map<String, Object>> orgs = dbHelperOrganisation.getResourcesWithProfile(resourceType);
            for (int i = 1; i < orgs.size(); i++) {
                Map<String, Object> org = orgs.get(i);
                coveringResourceId = (Integer) org.get("ResourceId");
                coveringName = (String) org.get("ResourceName");
                if (coveringResourceId != resourceId) break;
            }
        }

        // create absence
        apiHelperResources.createAbsenceForResource(resourceId, coveringResourceId);

        absences = dbHelperResources.getResourceAvailability(resourceId);

        // save all test data for later steps
        testData.put("resourceId", managerResourceId);
        testData.put("absentResourceId", resourceId);
        testData.put("userName", managerUserName);   // store for login step

        String timestamp = ((DateTimeOffset) absences.get(0).get("StartAt")).getTimestamp().toString();
        String startTime = tzHelper.adjustTimeForResourceId(resourceId, timestamp, "yyyy-MM-dd HH:mm:ss.S");
        startTime = DateHelper.convert(startTime, "yyyy-MM-dd HH:mm:ss.S", SHORT_DATE);
        testData.put("startDate", startTime);

        timestamp = ((DateTimeOffset) absences.get(0).get("EndAt")).getTimestamp().toString();
        String endTime = tzHelper.adjustTimeForResourceId(resourceId, timestamp, "yyyy-MM-dd HH:mm:ss.S");
        endTime = DateHelper.convert(endTime, "yyyy-MM-dd HH:mm:ss.S", SHORT_DATE);
        testData.put("endDate", endTime);

        runtimeState.scenario.write("Absence     : " + resourceName + " (" + resourceProfileName + "), Dates: " + startTime + " - " + endTime);
        runtimeState.scenario.write("Line Manager: " + managerName + " (" + managerUserProfile + ")");
        runtimeState.scenario.write("Cover       : " + coveringName);

        testData.put("manager", managerUserName);

        testData.put("resourceName", resourceName);
        testData.put("coveringResourceName", coveringName);
    }

    @And("^has allocated job to \"([^\"]*)\" the work$")
    public void i_have_job_with_status(String action) throws Throwable {
        if(action.equals("start")) {
            userJob = userJobDao.getUserWithAllocatedJobsToStartWork(testData.getString("profileName"));
        }
        if(userJob == null) {
            //throw new PendingException("Cannot find suitable data for test");
            createJobData("Allocated");
        } else {
            user.setUsername(userJob.getUserName());
            user.setResourceId(userJob.getResourceId());
            user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));

            testData.put("jobReference", userJob.getJobReference());
            testData.put("resourceId", userJob.getResourceId());
            testData.put("siteId", userJob.getSiteId());

            outputUserAndJobInfo();
        }
    }

    @And("^a user with profile \"([^\"]*)\" and has an allocated job$")
    public void a_user_with_profile_and_has_an_allocated_job(String userProfile) throws Throwable {

        testData.put("profileName", userProfile);

        if (testData.getBoolean("dataset")) {
            createJobData("Allocated");

        } else {

            userJob = userJobDao.getUserWithAllocatedJobsToStartWork(testData.getString("profileName"));

            if (userJob == null) {
                if (userProfile.contains("Contractor")) {
                    appUser = applicationUserDao.getContractor(userProfile);

                } else {
                    // City Resource
                    appUser = applicationUserDao.getForUserProfile(userProfile);
                }

                testData.put("useResourceTypeName", true);
                testData.put("assignToResourceId", appUser.getResourceId());

                createJobData("Allocated");

            } else {
                user.setUsername(userJob.getUserName());
                user.setResourceId(userJob.getResourceId());
                user.setProfileName(dbHelperResources.getResourceProfileName(userJob.getUserName()));

                testData.put("jobReference", userJob.getJobReference());
                testData.put("resourceId", userJob.getResourceId());
                testData.put("userName", userJob.getUserName());
                testData.put("resourceTypeName", dbHelperResources.getResourceType(userJob.getResourceId()));
                testData.put("siteId", userJob.getSiteId());

                outputUserAndJobInfo();
            }
        }
    }

    @And("^a user with profile \"([^\"]*)\" and has started work on one job and has another allocated job to the same site$")
    public void a_user_with_profile_and_has_started_work_on_one_job_and_has_another_allocated_job(String userProfile) throws Throwable {

        testData.put("profileName", userProfile);

        if (userProfile.contains("Contractor")) {
            appUser = applicationUserDao.getContractor(userProfile);

        } else {
            // City Resource
            appUser = applicationUserDao.getForUserProfile(userProfile);
        }

        testData.put("useResourceTypeName", true);
        testData.put("assignToResourceId", appUser.getResourceId());

        // create first job and start work
        createJobData("In Progress");
        testData.put("jobInProgress", testData.get("jobReference"));

        // create second job (allocated) at same site
        testData.put("assignToSiteId", testData.getInt("siteId"));
        createJobData("Allocated");
        testData.put("jobAllocated", testData.get("jobReference"));
    }

    @Given("^a Contractor Technician within a 750m GEO radius of multiple sites$")
    public void a_Contractor_Technician_within_a_m_GEO_radius_of_multiple_sites() throws Throwable {
        Map<String, Object> dbData = dbHelperResources.getContractorTechWithMultipleSites();

        int site1Id = (Integer) dbData.get("Site1Id");
        int site2Id = (Integer) dbData.get("Site2Id");
        Double latitude = (Double) dbData.get("Site2Latitude");
        Double longitude = (Double) dbData.get("Site2Longitude");
        testData.put("origLocation", String.format("%d,%f,%f", site2Id, latitude, longitude));
        logger.debug("Original siteId: " + site2Id + ", lat: " + latitude + ", long: " + longitude);

        Double newLat = (Double) dbData.get("Site1Latitude") + 0.004;
        Double newLong = (Double) dbData.get("Site1Longitude") + 0.004;

        dbHelperSites.updateSiteLocation(site2Id, newLat, newLong);

        Map<String, Object> myMap = new LinkedHashMap<>();
        myMap.put("Site1Id", site1Id);
        myMap.put("Site1Latitude", dbData.get("Site1Latitude"));
        myMap.put("Site1Longitude", dbData.get("Site1Longitude"));
        myMap.put("Site2Id", site2Id);
        myMap.put("Site2Latitude", newLat);
        myMap.put("Site2Longitude", newLong);
        myMap.put("ResourceId", dbData.get("ResourceId"));
        myMap.put("ResourceName", dbData.get("ResourceName"));
        myMap.put("UserName", dbData.get("UserName"));
        myMap.put("Metres",dbHelperSites.getDistanceBetweenSites(site1Id, site2Id));
        outputHelper.writeMapList(myMap);

        testData.put("myMap", myMap);
        testData.put("userName", dbData.get("UserName"));
        testData.put("profileName", "Contractor Technician");
    }

    @Given("^with open jobs on each site$")
    public void with_open_jobs_on_each_site() throws Throwable {
        Map<String, Object> dbData = testData.getMap("myMap");
        List<Integer> jobReferences = new ArrayList<>();

        testData.put("assignToResourceId", dbData.get("ResourceId"));

        testData.put("assignToSiteId", dbData.get("Site1Id"));
        createJobData("Allocated");
        jobReferences.add(testData.getInt("jobReference"));

        testData.put("assignToSiteId", dbData.get("Site2Id"));
        createJobData("Allocated");
        jobReferences.add(testData.getInt("jobReference"));
        testData.put("jobReferences", jobReferences);
    }
}