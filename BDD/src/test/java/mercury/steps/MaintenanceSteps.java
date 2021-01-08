package mercury.steps;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import com.opencsv.CSVReader;

import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.AssetFaultMappingDao;
import mercury.database.dao.JobDao;
import mercury.database.models.AssetFaultMapping;
import mercury.database.models.Job;
import mercury.databuilders.TestData;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.apihelper.ApiHelper;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperMaintenance;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.runtime.RuntimeState;

public class MaintenanceSteps {

    @Autowired private TestData testData;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperMaintenance dbHelperMaintenance;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private RuntimeState runtimeState;
    @Autowired private AssetFaultMappingDao assetFaultMappingDao;
    @Autowired private DbHelperIncidents dbHelperIncidents;
    @Autowired private ApiHelper apiHelper;
    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private OutputHelper outputHelper;
    @Autowired private JobDao jobDao;

    private static String invoiceState;

    @Given("^a resource file for view \"([^\"]*)\"$")
    public void a_resource_file_for_view(String view) throws Throwable {
        testData.put("view", view);
    }

    @When("^view is created$")
    public void view_is_created() throws Throwable {

        switch (testData.getString("view")) {

        case "CityTechStores":
            dbHelperMaintenance.createViewCityTechStores();
            break;

        case "Resources":
            dbHelperMaintenance.createViewResources();
            break;

        case "Rota":
            dbHelperMaintenance.createViewRota();
            break;

        case "VendorStores":
            dbHelperMaintenance.createViewVendorStores();
            break;

        case "JobSingleResource":
            dbHelperMaintenance.createViewJobsWithSingleActiveResource();
            break;

        case "QuoteUserScenario":
            dbHelperMaintenance.createViewQuoteApprovalScenarioUsers();
            break;

        default:
            throw new Exception("unexpected view: " + testData.getString("view"));
        }
    }

    @ContinueNextStepsOnException
    @Then("^the view returns results$")
    public void the_view_returns_results() throws Throwable {

        String viewName;
        switch (testData.getString("view")) {

        case "CityTechStores":
            viewName = "Staging.uvw_CityTechStores";
            break;

        case "Resources":
            viewName = "Staging.uvw_Resources";
            break;

        case "Rota":
            viewName = "Staging.uvw_Rota";
            break;

        case "VendorStores":
            viewName = "Staging.uvw_VendorStores";
            break;

        case "JobSingleResource":
            viewName = "Staging.uvw_JobsWithSingleActiveResource";
            break;

        case "QuoteUserScenario":
            viewName = "Staging.uvw_QuoteApprovalScenariosUsers";
            break;

        default:
            throw new Exception("unexpected view: " + testData.getString("view"));
        }

        assertTrue(dbHelper.getRowCount(viewName) > 0);
    }

    @Given("^a resource file for function \"([^\"]*)\"$")
    public void a_resource_file_for_function(String function) throws Throwable {
        testData.put("function", function);
    }

    @When("^function is created$")
    public void function_is_created() throws Throwable {

        String[] validFunctions = {"GetTimeAtSite", "GetTimeAtHomeOffice"};

        if ( ! Arrays.asList(validFunctions).contains(testData.getString("function"))) {
            throw new Exception("unexpected function: " + testData.getString("function"));
        }

        dbHelperMaintenance.createFunction(testData.getString("function"));
    }

    @When("^procedure is created$")
    public void procedure_is_created() throws Throwable {
        String[] validProcedure = {"UpdateUploadedInvoiceTemplate"};
        if ( ! Arrays.asList(validProcedure).contains(testData.getString("procedure"))) {
            throw new Exception("unexpected function: " + testData.getString("procedure"));
        }
        dbHelperMaintenance.createProcedure(testData.getString("procedure"));
    }

    @ContinueNextStepsOnException
    @Then("^the procedure returns results$")
    public void the_procedure_returns_results() throws Throwable {
        String procedureName;
        switch (testData.getString("procedure")) {

        case "UpdateUploadedInvoiceTemplate":
            procedureName = "Staging.usp_Update_InvoiceTemplateCoordinates";
            break;
        }
    }

    @Given("^a resource file for procedure \"([^\"]*)\"$")
    public void a_resource_file_for_procedure(String procedure) throws Throwable {
        testData.put("procedure", procedure);
    }

    @Given("^database mappings for all assets to all faults$")
    public void all_assets_all_faults_combinations(){
        List<AssetFaultMapping> assetFaultMappings = assetFaultMappingDao.getAllAssetsToAllFaults();
        storeTestData(assetFaultMappings);
    }

    @Given("^database mappings for the following asset classifications to all faults:$")
    public void database_mappings_for_the_following_assets_classifications_to_all_faults(List<String> dataTable){
        String classifications = "";
        for (String classification : dataTable) {
            String quotedClassification = "'" + classification + "'";
            classifications = classifications.isEmpty() ? quotedClassification : classifications + ", " + quotedClassification;
        }
        List<AssetFaultMapping> assetFaultMappings = assetFaultMappingDao.getAssetsToAllFaults(classifications);
        storeTestData(assetFaultMappings);
    }

    @Given("^the assets listed in excel filename \"([^\"]*)\"$")
    public void the_assets_listed_in_excel_filename_and_worksheet(String pathname) throws Exception{
        String pathToCsv = "src/test/resources/spreadsheets/" + pathname;

        File csvFile = new File(pathToCsv);
        if (csvFile.isFile()) {
            List<List<String>> records = new ArrayList<List<String>>();
            CSVReader csvReader = new CSVReader(new FileReader(pathToCsv));
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            csvReader.close();

            List<String> uniqueMappings = new ArrayList<>();

            for (int i = 1; i < records.size(); i++) {
                // read the spreadsheet and record unique mappings
                String mapping = records.get(i).get(0) + "¦" + records.get(i).get(1) + "¦" + records.get(i).get(2) + "¦" + records.get(i).get(3) + "¦" + records.get(i).get(8);
                if ( !uniqueMappings.contains(mapping) && !mapping.equals("¦¦¦¦")) {
                    uniqueMappings.add(mapping);
                }
            }

            for (String mapping : uniqueMappings) {
                String assetType = getPart(mapping, 0);
                String assetSubType = getPart(mapping, 1);
                String classification = getPart(mapping, 2).equalsIgnoreCase("NULL") ? "" : getPart(mapping, 2);
                String faultType = getPart(mapping, 3);
                String siteType = getPart(mapping, 4);

                testData.addToList("assetTypeName", assetType);
                testData.addToList("assetSubTypeName", assetSubType);
                testData.addToList("assetClassificationName", classification);
                testData.addToList("faultTypeName", faultType);
                testData.addToList("siteTypes", siteType);

                int assetTypeId = dbHelper.getAssetTypeId(assetType);
                testData.addToList("assetTypeId", assetTypeId);
                int assetSubTypeId = dbHelper.getAssetSubTypeId(assetTypeId, assetSubType);
                testData.addToList("assetSubTypeId", assetSubTypeId);
                testData.addToList("assetClassificationId", dbHelper.getClassificationId(classification, assetSubTypeId));
                testData.addToList("faultTypeId", dbHelper.getFaultTypeId(faultType));
            }

        } else {
            throw new Exception("*** File NOT Found! ***");
        }
    }

    private String getPart(String line, int i) {
        String[] parts = line.split("¦");
        try {
            return parts[i];
        } catch (Exception e) {
            return "";
        }
    }

    @Given("^database mappings for all assets to a random fault$")
    public void all_assets_random_faults_combinations(){
        List<AssetFaultMapping> assetFaultMappings = assetFaultMappingDao.getAllAssetsToRandomFault();
        storeTestData(assetFaultMappings);
    }

    @Given("^database mappings for all non RHVAC assets to a random fault$")
    public void all_non_RHVAC_assets_random_faults_combinations(){
        List<AssetFaultMapping> assetFaultMappings = assetFaultMappingDao.getAllNonRhvacAssetsToRandomFault();
        storeTestData(assetFaultMappings);
    }

    private void storeTestData(List<AssetFaultMapping> assetFaultMappings) {
        for (AssetFaultMapping assetFaultMapping : assetFaultMappings) {
            testData.addToList("assetTypeId", assetFaultMapping.getAssetTypeId());
            testData.addToList("assetTypeName", assetFaultMapping.getAssetTypeName());
            testData.addToList("assetSubTypeId", assetFaultMapping.getAssetSubTypeId());
            testData.addToList("assetSubTypeName", assetFaultMapping.getAssetSubTypeName());
            testData.addToList("assetClassificationId", assetFaultMapping.getAssetClassificationId());
            testData.addToList("assetClassificationName", assetFaultMapping.getAssetClassificationName());
            testData.addToList("faultTypeId", assetFaultMapping.getFaultTypeId());
            testData.addToList("faultTypeName", assetFaultMapping.getFaultTypeName());
        }
    }

    @Given("^asset/fault mappings: \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    public void job_arguments(int assetTypeId, String assetTypeName, int assetSubTypeId, String assetSubTypeName, int assetClassificationId, String assetClassificationName, int faultTypeId, String faultTypeName, String location) throws Throwable {
        testData.addToList("assetTypeId", assetTypeId);
        testData.addToList("assetTypeName", assetTypeName);
        testData.addToList("assetSubTypeId", assetSubTypeId);
        testData.addToList("assetSubTypeName", assetSubTypeName);
        testData.addToList("assetClassificationId", assetClassificationId);
        testData.addToList("assetClassificationName", assetClassificationName);
        testData.addToList("faultTypeId", faultTypeId);
        testData.addToList("faultTypeName", faultTypeName);
        testData.addToList("locations", location);
    }

    @Given("^for siteId \"([^\"]*)\"$")
    public void for_siteId(int siteId) throws Throwable {
        testData.put("siteId", siteId);
    }

    @Given("^for a \"([^\"]*)\" site$")
    public void for_a_site(String siteType) throws Throwable {
        int siteId = dbHelperSites.getRandomSiteIdForType(siteType);
        testData.put("siteId", siteId);
    }

    @ContinueNextStepsOnException
    @Then("^the create job results are output$")
    public void the_create_job_results_are_output() throws Throwable {

        outputCsvToReport();

        boolean failed = false;
        for (int i = 0; i < testData.getIntList("faultTypeId").size(); i++) {
            int jobReference = testData.getIntList("jobReferences").get(i);

            if (jobReference > 0) {
                dbHelper.insertAudit("log job", jobReference, createJsonAudit(i), "PASS");
            } else {
                dbHelper.insertAudit("log job", null, createJsonAudit(i), "FAIL");
                failed = true;
            }
        }
        if (failed) {
            throw new Exception("At least one failure was captured. Check TestAutomation_Audit table for results!");
        }
    }

    private void outputCsvToReport() throws Exception {
        String from = runtimeState.timestamp;
        String to = dbHelper.getDbTimestamp();
        List<String> jobs = dbHelperJobs.getJobsCreatedBetween("job created from test automation api call", from, to);
        if (jobs.size() > 0) {
            runtimeState.scenario.write("JobReference,SiteName,SiteType,Asset,SubType,Classification,FaultType,Priority,Created");
        }
        for (String job : jobs) {
            runtimeState.scenario.write(job);
        }
    }

    private String createJsonAudit(int i) {

        String input = "";
        if ( testData.getIntList("siteTypes") != null) {
            input = "{'siteId':%d, 'siteType':'%s', 'assetTypeId':%d, 'assetTypeName':'%s', 'assetSubTypeId':%d, 'assetSubTypeName':'%s', 'assetClassificationId':%d, 'assetClassificationName':'%s', 'faultTypeId':%d, 'faultTypeName':'%s', 'location':'%s'}";
            input = input.replaceAll("'",  "\"");
            return String.format(input
                    , testData.getIntList("siteIds").get(i)
                    , testData.getArray("siteTypes").get(i)
                    , testData.getIntList("assetTypeId").get(i)
                    , testData.getArray("assetTypeName").get(i)
                    , testData.getIntList("assetSubTypeId").get(i)
                    , testData.getArray("assetSubTypeName").get(i)
                    , testData.getIntList("assetClassificationId").get(i)
                    , testData.getArray("assetClassificationName").get(i)
                    , testData.getIntList("faultTypeId").get(i)
                    , testData.getArray("faultTypeName").get(i)
                    , testData.getArray("locations").get(i));

        } else {
            input = "{'siteId':%d, 'assetTypeId':%d, 'assetTypeName':'%s', 'assetSubTypeId':%d, 'assetSubTypeName':'%s', 'assetClassificationId':%d, 'assetClassificationName':'%s', 'faultTypeId':%d, 'faultTypeName':'%s', 'location':'%s'}";
            input = input.replaceAll("'",  "\"");
            return String.format(input
                    , testData.getIntList("siteIds").get(i)
                    , testData.getIntList("assetTypeId").get(i)
                    , testData.getArray("assetTypeName").get(i)
                    , testData.getIntList("assetSubTypeId").get(i)
                    , testData.getArray("assetSubTypeName").get(i)
                    , testData.getIntList("assetClassificationId").get(i)
                    , testData.getArray("assetClassificationName").get(i)
                    , testData.getIntList("faultTypeId").get(i)
                    , testData.getArray("faultTypeName").get(i)
                    , testData.getArray("locations").get(i));
        }
    }

    @Given("^failed jobs that were recorded in the TestAutomation_Audit table and created between \"([^\"]*)\" and \"([^\"]*)\"$")
    public void failed_jobs_that_were_recorded_in_the_TestAutomation_Audit_table_and_created_between_and(String from, String to) throws Throwable {
        List<String> failedJobs = dbHelperMaintenance.getFailedJobs(from, to);
        for (String failedJob : failedJobs) {

            failedJob = failedJob.replaceAll("\"siteType\":.+?, ", "");
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(failedJob);
            testData.addToList("siteIds", getInt(json, "siteId"));
            testData.addToList("assetTypeId", getInt(json, "assetTypeId"));
            testData.addToList("assetTypeName", json.get("assetTypeName"));
            testData.addToList("assetSubTypeId", getInt(json, "assetSubTypeId"));
            testData.addToList("assetSubTypeName", json.get("assetSubTypeName"));
            testData.addToList("assetClassificationId", getInt(json, "assetClassificationId"));
            testData.addToList("assetClassificationName", json.get("assetClassificationName"));
            testData.addToList("faultTypeId", getInt(json, "faultTypeId"));
            testData.addToList("faultTypeName", json.get("faultTypeName"));
            testData.addToList("locations", json.get("location"));
        }
    }

    @Given("^failed API requests that were recorded in the TestAutomation_Audit table and created between \"([^\"]*)\" and \"([^\"]*)\"$")
    public void failed_API_requests_that_were_recorded_in_the_TestAutomation_Audit_table_and_created_between_and(String from, String to) throws Throwable {
        List<String> failedApiRequests = dbHelperMaintenance.getFailedApiRequests(from, to);
        testData.put("failedApiRequests", failedApiRequests);
    }

    /**
     * get integer value from json
     * @param jsonObject
     * @param field
     * @return
     */
    private static Integer getInt(JSONObject jsonObject, String field) {
        return  ((Long) jsonObject.get(field)).intValue();
    }

    @Given("^all except the latest \"([^\"]*)\" incidents on the \"([^\"]*)\" monitor are selected for cancellation$")
    public void all_except_the_latest_incidents_on_the_monitor_are_selected_for_cancellation(int keepCount, String monitor) throws Exception{

        String view;
        switch (monitor) {
        case "Pending To Do": view = "uvw_MonitorIncidentPendingToDo"; break;
        case "Initial Escalations": view = "uvw_MonitorIncidentInitialEscalations"; break;
        case "Reviews": view = "uvw_MonitorIncidentReview"; break;
        case "Follow ups": view = "uvw_MonitorIncidentFollowUp"; break;
        default: throw new Exception("Unknown monitor: " + monitor);
        }

        List<Integer> incidentsForCancellation = dbHelperIncidents.getIncidentsForCancellation(keepCount, view);
        testData.put("incidentsForCancellation", incidentsForCancellation);
        runtimeState.scenario.write(incidentsForCancellation.size() + " incidents selected for cancellation");
    }

    @When("^the selected incidents are cancelled via the api$")
    public void the_selected_incidents_are_cancelled_via_the_api() throws Throwable {
        apiHelper.cancelIncidents(testData.getIntList("incidentsForCancellation"));
    }

    @ContinueNextStepsOnException
    @Then("^the selected incidents shall no longer exist on the Incidents \"([^\"]*)\" monitor$")
    public void the_selected_incidents_shall_no_longer_exist_on_the_Incidents_monitor(String monitor) throws Throwable {

        String view;
        switch (monitor) {
        case "Pending To Do": view = "uvw_MonitorIncidentPendingToDo"; break;
        case "Initial Escalations": view = "uvw_MonitorIncidentInitialEscalations"; break;
        case "Reviews": view = "uvw_MonitorIncidentReview"; break;
        case "Follow ups": view = "uvw_MonitorIncidentFollowUp"; break;
        default: throw new Exception("Unknown monitor: " + monitor);
        }

        String incidentIds = testData.getIntList("incidentsForCancellation").toString().replace("[", "").replace("]", "");
        if (!incidentIds.isEmpty()) {
            int numNotDeleted = dbHelperMonitors.countJobsOnMonitor(view, incidentIds);
            assertTrue(numNotDeleted + " selected incidents have not been cancelled", numNotDeleted == 0);
        } else {
            runtimeState.scenario.write("No incidents selected");
        }
    }

    @Given("^all except the latest \"([^\"]*)\" jobs on the \"([^\"]*)\" monitor are selected for cancelation$")
    public void all_except_the_latest_jobs_on_the_monitor_are_selected_for_cancelation(int keepCount, String monitor) throws Exception{
        String view;
        switch (monitor) {
        case "Awaiting Assignment": view = "uvw_MonitorHelpdeskJobAwaitingAssignment"; break;
        case "Awaiting Acceptance": view = "uvw_MonitorJobAwaitingAcceptance"; break;
        default: throw new Exception("Unknown monitor: " + monitor);
        }

        List<Map<String, Object>> selectedJobs = dbHelperJobs.getJobsForCancelation(keepCount, view);
        testData.put("selectedJobs", selectedJobs);
        runtimeState.scenario.write(selectedJobs.size() + " jobs selected for cancelation");
    }

    @Given("^all except the latest \"([^\"]*)\" jobs on the \"([^\"]*)\" monitor are selected for completion$")
    public void all_except_the_latest_jobs_on_the_monitor_are_selected_for_completion(int keepCount, String monitor) throws Exception{
        String view;
        switch (monitor) {
        case "ETA Expired": view = "uvw_MonitorFocusJobETAExpired"; break;
        case "SLA Near/Missed": view = "uvw_MonitorFocusJobRepairTargetMissed"; break;
        case "ETA greater": view = "uvw_MonitorJobEtaExceedsSlaBase"; break;
        default: throw new Exception("Unknown monitor: " + monitor);
        }

        List<Map<String, Object>> selectedJobs = dbHelperJobs.getJobsForCompletion(keepCount, view);
        testData.put("selectedJobs", selectedJobs);
        runtimeState.scenario.write(selectedJobs.size() + " jobs selected for completion");
    }

    @Given("^all except the latest \"([^\"]*)\" \"([^\"]*)\" jobs with \"([^\"]*)\" resource are selected$")
    public void all_except_the_latest_jobs_with_resource_are_selected(int keepCount, String jobStatus, String resourceStatus) throws Throwable {
        List<Map<String, Object>> selectedJobs = dbHelperMaintenance.getJobsForTermination(keepCount, jobStatus, resourceStatus);
        testData.put("selectedJobs", selectedJobs);
        runtimeState.scenario.write(selectedJobs.size() + " jobs selected");
    }

    @Given("^the jobs to be cancelled are identified$")
    public void the_jobs_to_be_cancelled_are_identified() throws Throwable {
        int keepCount = Integer.valueOf(System.getProperty("maxNumberOfOpenJobs"));
        String keepResourceIds = System.getProperty("keepResourceIds");
        String source = System.getProperty("cancelJob");

        assertNotNull("Property not set: cancelJob", source);
        String jobStatus;
        String resourceAssignmentStatus = null;
        if (source.contains(">")) {
            String[] parts = source.split(">");
            jobStatus = parts[0].trim();
            resourceAssignmentStatus = parts[1].trim();
        } else {
            jobStatus = source.trim();
        }

        List<Map<String, Object>> selectedJobs = dbHelperMaintenance.getJobsForTermination(keepCount, jobStatus, resourceAssignmentStatus, keepResourceIds);
        testData.put("selectedJobs", selectedJobs);
        runtimeState.scenario.write(selectedJobs.size() + " jobs selected");
    }

    @Given("^all except the latest \"([^\"]*)\" chase jobs with \"([^\"]*)\" chase type are selected$")
    public void all_except_the_latest_chase_jobs_with_chase_type_are_selected(int keepCount, String chaseType) throws Throwable {
        List<Map<String, Object>> selectedJobs = dbHelperMaintenance.getChaseJobsForCancellation(keepCount, chaseType);
        testData.put("selectedJobs", selectedJobs);
        runtimeState.scenario.write(selectedJobs.size() + " chase jobs selected for cancellation");
    }

    @When("^the selected chase jobs are cancelled by DB update$")
    public void the_selected_chase_jobs_are_cancelled_by_DB_update() throws Throwable {
        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");
        List<Integer> chaseJobsCancelled = new ArrayList<>();
        String exceptions = "";
        for (Map<String, Object> job : selectedJobs) {
            try {
                int id = (Integer) job.get("ChaseId");
                int jobReference = (Integer) job.get("JobReference");
                dbHelperMaintenance.updateJobChaseToCancelled(id);
                chaseJobsCancelled.add(jobReference);
            } catch (Exception e) {
                exceptions = exceptions + "\n\n" + e.getMessage();
            }
        }
        testData.put("chaseJobsCancelled", chaseJobsCancelled);
        testData.put("exceptions", exceptions);
    }

    @When("^the selected chase jobs are cancelled via the API$")
    public void the_selected_chase_jobs_are_cancelled_via_API() throws Throwable {
        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");
        List<Integer> chaseJobsCancelled = new ArrayList<>();
        String exceptions = "";
        for (Map<String, Object> job : selectedJobs) {
            try {
                int id = (Integer) job.get("ChaseId");
                int jobReference = (Integer) job.get("JobReference");
                apiHelperJobs.cancelChase(id);
                chaseJobsCancelled.add(jobReference);
            } catch (Exception e) {
                exceptions = exceptions + "\n\n" + e.getMessage();
            }
        }
        testData.put("chaseJobsCancelled", chaseJobsCancelled);
        testData.put("exceptions", exceptions);
        dbHelperMaintenance.removeOnSiteResourcesForTerminatedJobs();
    }

    @Then("^the selected chase jobs will have been cancelled$")
    public void the_selected_chase_jobs_will_have_been_cancelled() throws Throwable {
        runtimeState.scenario.write("Cancelled chase jobs: " + testData.getIntList("chaseJobsCancelled").toString().replace("[", "").replace("]", ""));

        runtimeState.scenario.write("Remaining chase jobs: \n");
        outputHelper.writeMapList(dbHelperMaintenance.getChaseJobTotals());

        assertTrue(testData.getString("exceptions"), testData.getString("exceptions") == null || testData.getString("exceptions").isEmpty());
    }

    @Given("^all except the latest \"([^\"]*)\" quote jobs with \"([^\"]*)\" approval status are selected$")
    public void all_except_the_latest_quote_jobs_with_approval_status_are_selected(int keepCount, String approvalStatus) throws Throwable {
        List<Map<String, Object>> selectedJobs = dbHelperMaintenance.getQuoteJobsForCancellation(keepCount, approvalStatus);
        testData.put("selectedJobs", selectedJobs);
        runtimeState.scenario.write(selectedJobs.size() + " quote jobs selected for cancellation");
    }

    @When("^the selected quote jobs are cancelled by DB update$")
    public void the_selected_quote_jobs_are_cancelled_by_DB_update() throws Throwable {
        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");
        List<Integer> quoteJobsCancelled = new ArrayList<>();
        String exceptions = "";
        for (Map<String, Object> job : selectedJobs) {
            try {
                int id = (Integer) job.get("HelpDeskFaultTimeId");
                int jobReference = (Integer) job.get("HelpDeskFaultID");
                dbHelperMaintenance.updateHelpDeskFaultTimeToCancelled(id);
                quoteJobsCancelled.add(jobReference);
            } catch (Exception e) {
                exceptions = exceptions + "\n\n" + e.getMessage();
            }
        }
        testData.put("quoteJobsCancelled", quoteJobsCancelled);
        testData.put("exceptions", exceptions);
        dbHelperMaintenance.removeOnSiteResourcesForTerminatedJobs();
    }

    @Then("^the selected quote jobs will have been cancelled$")
    public void the_selected_quote_jobs_will_have_been_cancelled() throws Throwable {
        runtimeState.scenario.write("Cancelled quote jobs: " + testData.getIntList("quoteJobsCancelled").toString().replace("[", "").replace("]", ""));

        runtimeState.scenario.write("Remaining quote jobs: \n");
        outputHelper.writeMapList(dbHelperMaintenance.getQuoteJobTotals());

        assertTrue(testData.getString("exceptions"), testData.getString("exceptions") == null || testData.getString("exceptions").isEmpty());
    }

    @When("^the selected jobs are completed via the api$")
    public void the_selected_jobs_are_completed_via_the_api() throws Throwable {
        // Need to add these as the step requires this
        testData.addBooleanTag("gasUsage", false);
        testData.addBooleanTag("leakCheck", false);
        testData.addStringTag("profileName", "cityResource");

        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");

        List<Integer> jobsTerminated = new ArrayList<>();

        selectedJobs.stream().forEach(mapsData->{
            int id = (int) mapsData.get("id");
            int jobReference = (int) mapsData.get("JobReference");
            int resourceId = (int) mapsData.get("ResourceId");
            int siteId = (int) mapsData.get("siteId");
            try {
                jobCreationHelper.updateJobWithSiteVisit(id, jobReference, resourceId, siteId, "Complete");
                jobsTerminated.add(jobReference);
            } catch (Exception | AssertionError e) {
                try {
                    apiHelperJobs.fixJob(jobReference);
                } catch (IOException e1) {
                    runtimeState.scenario.write("Failed to complete job : " + jobReference + "\n" + e1.getMessage());
                }
            }
        });

        testData.put("jobsTerminated", jobsTerminated);
        dbHelperMaintenance.removeOnSiteResourcesForTerminatedJobs();
    }

    @When("^the selected jobs are \"([^\"]*)\" via the admin api$")
    public void the_selected_jobs_are_fixed_via_the_api(String status) throws Throwable {
        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");
        List<Integer> jobsTerminated = new ArrayList<>();

        selectedJobs.stream().forEach(mapsData->{
            int jobReference = (int) mapsData.get("JobReference");
            try {
                switch (status) {
                case "fixed" : apiHelperJobs.fixJob(jobReference); break;
                case "cancelled" : apiHelperJobs.cancelJob(jobReference); break;
                default : throw new Exception("unexpected: " + status);
                }
                dbHelperMaintenance.cancelPortalJob(jobReference);
                jobsTerminated.add(jobReference);
            } catch (IOException e1) {
                runtimeState.scenario.write("Failed to terminate job : " + jobReference + "\n" + e1.getMessage());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        testData.put("jobsTerminated", jobsTerminated);
        dbHelperMaintenance.removeOnSiteResourcesForTerminatedJobs();
    }

    @ContinueNextStepsOnException
    @Then("^the selected jobs shall no longer exist on the Jobs \"([^\"]*)\" monitor$")
    public void the_selected_jobs_shall_no_longer_exist_on_the_Jobs_monitor(String monitor) throws Throwable {
        String view;
        switch (monitor) {
        case "ETA Expired": view = "uvw_MonitorFocusJobETAExpired"; break;
        case "SLA Near/Missed": view = "uvw_MonitorFocusJobRepairTargetMissed"; break;
        case "ETA greater": view = "uvw_MonitorFocusJobEtaExceedsSla"; break;
        case "Removal Requests": view = "uvw_MonitorHelpdeskJobRemovalRequests"; break;
        case "Funding Requests": view = "uvw_MonitorHelpdeskJobFundingRequests"; break;
        default: throw new Exception("Unknown monitor: " + monitor);
        }

        String jobIds = testData.getIntList("jobsTerminated").toString().replace("[", "").replace("]", "");
        if (!jobIds.isEmpty()) {
            int numNotDeleted = dbHelperMonitors.countJobsOnMonitor(view, jobIds);
            assertTrue(numNotDeleted + " selected jobs have not been cancelled", numNotDeleted == 0);
        } else {
            runtimeState.scenario.write("No jobs selected");
        }
    }

    @Given("^all except the latest \"([^\"]*)\" jobs on the \"([^\"]*)\" monitor are selected for resource removal$")
    public void all_except_the_latest_jobs_on_the_monitor_are_selected_for_resource_removal(int keepCount, String monitor) throws Exception{
        String view;
        switch (monitor) {
        case "Removal Requests": view = "uvw_MonitorHelpdeskJobRemovalRequests"; break;
        default: throw new Exception("Unknown monitor: " + monitor);
        }

        List<Map<String, Object>> selectedJobs = dbHelperJobs.getJobsForResourceRemoval(keepCount, view);
        testData.put("selectedJobs", selectedJobs);
        runtimeState.scenario.write(selectedJobs.size() + " jobs selected");
    }

    @When("^the resources are removed from the jobs via the api$")
    public void the_resources_are_removed_from_the_jobs_the_api() throws Throwable {
        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");
        List<Integer> jobsTerminated = new ArrayList<>();

        selectedJobs.stream().forEach(mapsData->{
            int id = (int) mapsData.get("id");
            Integer resourceAssignmentId = (Integer) mapsData.get("resourceAssignmentId");
            if (resourceAssignmentId != null) {
                int jobReference = (int) mapsData.get("JobReference");
                try {
                    apiHelperJobs.removeResourceAdvise(resourceAssignmentId);
                    for (int arrId : dbHelperJobs.getARRID(id)) {
                        apiHelperJobs.cancelAdditionalResource(arrId);
                    }
                    jobsTerminated.add(jobReference);

                } catch (Exception | AssertionError e) {
                    runtimeState.scenario.write("Failed to remove resource from job : " + jobReference + "\n" + e.getMessage());
                }
            }
        });
        testData.put("jobsTerminated", jobsTerminated);
    }

    @Given("^all except the latest \"([^\"]*)\" jobs on the \"([^\"]*)\" monitor have the funding request rejected$")
    public void all_except_the_latest_jobs_on_the_monitor_have_the_funding_request_rejected(int keepCount, String monitor) throws Exception{
        String view;
        switch (monitor) {
        case "Funding Requests": view = "uvw_MonitorHelpdeskJobFundingRequests"; break;
        default: throw new Exception("Unknown monitor: " + monitor);
        }

        List<Map<String, Object>> selectedJobs = dbHelperJobs.getJobsForFundingRequestRejection(keepCount, view);
        testData.put("selectedJobs", selectedJobs);
        runtimeState.scenario.write(selectedJobs.size() + " jobs selected for Funding Request rejection");
    }

    @When("^the funding requests are rejected for the jobs via the api$")
    public void the_funding_requests_are_rejected_for_the_jobs_the_api() throws Throwable {
        List<Map<String, Object>> selectedJobs = testData.getListMap("selectedJobs");

        List<Integer> jobsTerminated = new ArrayList<>();

        selectedJobs.stream().forEach(mapsData->{
            int id = (int) mapsData.get("id");
            int jobReference = (int) mapsData.get("JobReference");
            int fundingRequestId = (int) mapsData.get("fundingRequestId");
            int initialRequestApproverId = (int) mapsData.get("InitialRequestApproverId");
            try {
                apiHelper.rejectFundingRequest(fundingRequestId, initialRequestApproverId);
                jobsTerminated.add(id);
            } catch (Exception e) {
                runtimeState.scenario.write("Failed to remove resource from job : " + jobReference + "\n" + e.getMessage());
            }
        });

        testData.put("jobsTerminated", jobsTerminated);
        dbHelperMaintenance.removeOnSiteResourcesForTerminatedJobs();
    }

    @Given("^the properties are set up correctly$")
    public void properties_set_up_correctly() throws Throwable{
        String environment = System.getProperty("Environment");
        assertNotNull("Missing Environment system property", environment);

        String summary = "PPM jobs will be created on Environment - %s";
        summary = String.format(summary, environment);
        runtimeState.scenario.write(summary);

        testData.put("environment", environment);
    }


    @Given("^jobs have to be created$")
    public void the_requirements() throws Throwable{
        testData.addStringTag("jobType", "reactive");
        String environment = System.getProperty("Environment");
        String resourceType = System.getProperty("ResourceType");
        String jobResourceStatus = System.getProperty("JobResourceStatus");
        String assetType = System.getProperty("AssetType");
        Integer numberOfJobs = Integer.valueOf(System.getProperty("NumberOfJobs"));
        String priority = System.getProperty("Priority");

        assertNotNull("Missing environment system property", environment);
        assertNotNull("Missing resourceType system property", resourceType);
        assertNotNull("Missing jobResourceStatus system property", jobResourceStatus);
        assertNotNull("Missing assetType system property", assetType);
        assertNotNull("Missing numberOfJobs system property", numberOfJobs);
        assertNotNull("Missing numberOfJobs system property", System.getProperty("NumberOfJobs"));
        assertNotNull("Missing priority system property", priority);

        String summary = "Jobs with the following properties will be created, Environment: %s, Resource Type: %s, jobResourceStatus: %s, AssetType: %s, numberOfJobs: %d, Priority: %s";
        summary = String.format(summary,environment, resourceType, jobResourceStatus, assetType, numberOfJobs, priority);
        runtimeState.scenario.write(summary);

        Boolean inPlant = "refrigeration".equalsIgnoreCase(assetType) ? true : false;
        Boolean usesGas = "Non gas".equalsIgnoreCase(assetType) ? false : true;

        testData.addBooleanTag("asset", "Unknown".equalsIgnoreCase(assetType) ? false : true);
        testData.addStringTag("jobType", "reactive");
        testData.addStringTag("resourceTypeName", resourceType);
        testData.addStringTag("jobResourceStatus", jobResourceStatus);
        testData.addStringTag("profileName", resourceType);
        testData.put("inPlant",inPlant);
        testData.put("usesGas", usesGas);
        testData.addStringTag("assetType", assetType);
        testData.put("numberOfJobs", numberOfJobs);
        testData.put("priority", priority);
    }

    @Given("^\"([^\"]*)\" job has to be created for \"([^\"]*)\" for \"([^\"]*)\" and \"([^\"]*)\" priority$")
    public void job_has_to_be_created_for_for_and_priority(String jobResourceStatus, String resourceType, String assetType, String priority) throws Throwable {
        testData.addStringTag("jobType", "reactive");
        Integer numberOfJobs = 1;

        String summary = "Jobs with the following properties will be created, Resource Type: %s, jobResourceStatus: %s, AssetType: %s, numberOfJobs: %d, Priority: %s";
        summary = String.format(summary, resourceType, jobResourceStatus, assetType, numberOfJobs, priority);
        runtimeState.scenario.write(summary);

        Boolean inPlant = "refrigeration".equalsIgnoreCase(assetType) ? true : false;
        Boolean usesGas = "Non gas".equalsIgnoreCase(assetType) ? false : true;

        testData.addBooleanTag("asset", "Unknown".equalsIgnoreCase(assetType) ? false : true);
        testData.addStringTag("jobType", "reactive");
        testData.addStringTag("resourceTypeName", resourceType);
        testData.addStringTag("jobResourceStatus", jobResourceStatus);
        testData.put("inPlant",inPlant);
        testData.put("usesGas", usesGas);
        testData.addStringTag("assetType", assetType);
        testData.put("numberOfJobs", numberOfJobs);
        testData.put("priority", priority);
    }

    @ContinueNextStepsOnException
    @Then("^the create job results are saved$")
    public void the_create_job_results_are_saved() throws Throwable {
        for (int i = 0; i < testData.getIntList("createdJobs").size(); i++) {
            int jobReference = testData.getIntList("createdJobs").get(i);
            runtimeState.scenario.write(String.valueOf(jobReference));
        }
    }

    @Given("^all invoices at state \"([^\"]*)\"$")
    public void all_invoices_except_the_latest_for_each_supplier(String state) throws Throwable {
        invoiceState = state;
    }

    @When("^all invoices except the latest \"([^\"]*)\" for each supplier are updated to rejected$")
    public void the_selected_invoices_are_updated_in_the_DB_to_rejected(int count) throws Throwable {
        dbHelperInvoices.rejectInvoices(invoiceState, count);
    }

    @Given("^system property \"([^\"]*)\" is \"([^\"]*)\"$")
    public void system_property_is(String key, String value) {
        System.setProperty(key, value);
    }

    @Then("^the selected jobs will have been (?:cancelled|fixed|rejected)$")
    public void the_selected_jobs_will_have_been_closed() throws Exception {
        if (testData.getIntList("jobsTerminated") != null) {
            String failedJobs = "";
            String fixedJobs = "";
            String cancelledJobs = "";
            int numCancelled = 0;
            int numFixed = 0;

            for (int jobReference : testData.getIntList("jobsTerminated")) {
                Job job = jobDao.getByJobReference(jobReference);

                if (job.getJobStatusId() == 9) {
                    // fixed
                    numFixed = numFixed + 1;
                    fixedJobs = fixedJobs + "\n" + jobReference;

                } else if (job.getJobStatusId() != 10) {
                    // job not cancelled!
                    failedJobs = failedJobs + "\n" + jobReference + " status now " + dbHelper.getJobStatusName(job.getJobStatusId());

                } else {
                    numCancelled = numCancelled + 1;
                    cancelledJobs = cancelledJobs + "\n" + jobReference;
                }
            }

            if (numCancelled > 0) runtimeState.scenario.write(numCancelled + " jobs have been cancelled: " + cancelledJobs);
            if (numFixed > 0) runtimeState.scenario.write(numFixed + " jobs have been fixed: " + fixedJobs);
            if ( !failedJobs.isEmpty() ) runtimeState.scenario.write("Jobs NOT terminated: " + failedJobs);

        } else {
            runtimeState.scenario.write("No jobs selected for termination");
        }

        runtimeState.scenario.write("Remaining jobs: \n");
        outputHelper.writeMapList(dbHelperMaintenance.getJobTotals());

        assertTrue(testData.getString("exceptions"), testData.getString("exceptions") == null || testData.getString("exceptions").isEmpty());
    }

}
