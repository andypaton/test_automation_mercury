package mercury.steps.performance;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.opencsv.CSVWriter;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.ApplicationUserDao;
import mercury.database.models.ApplicationUser;
import mercury.databuilders.TestData;
import mercury.helpers.AzureStorageHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.portal.GetPortalUserDataSteps;
import mercury.steps.portal.jobs.PortalUserWithJobSteps;

public class PerformanceDataSetupSteps {

    @Autowired private TestData testData;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private PortalUserWithJobSteps portalUserWithJobSteps;
    @Autowired private ApplicationUserDao applicationUserDao;
    @Autowired private GetPortalUserDataSteps getPortalUserDataSteps;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private AzureStorageHelper azureStorageHelper;

    private static List<String> blobsUploaded = new ArrayList<>();


    @Given("a system property for \"([^\"]*)\"")
    public void a_system_property_for(String property) throws Exception {
        if (System.getProperty(property) == null) throw new Exception("Property not set: " + property);
        runtimeState.scenario.write(property + " = " + System.getProperty(property));
    }

    @Given("the job is to be assigned to a \"([^\"]*)\"$")
    public void the_job_is_to_be_assigned_to_a(String userProfile) {
        testData.put("profileName", userProfile);
    }

    @When("the list of resourceIds are identified$")
    public void the_list_of_resourceIds_are_identified() throws Exception {
        String profileName = testData.getString("profileName");

        if (System.getProperty("assignToSiteIds") != null) {
            String[] parts = System.getProperty("assignToSiteIds").split(",");
            for (String part : parts) {
                int siteId = Integer.valueOf(part);
                testData.addToList("assignToSiteIds", siteId);
                Map<String, Object> rfm = dbHelperSites.getRfmForSite(siteId);
                testData.addToList("RFMs", rfm.get("ResourceId"));

                if (profileName.equals("Contractor Admin") || profileName.equals("Contractor Technician")) {
                    Map<String, Object> contractor = dbHelperResources.getResource(profileName, 1, siteId);
                    testData.addToList("assignToResourceIds", contractor.get("ResourceId"));
                    testData.addToList("resourceNames", contractor.get("ResourceName"));

                } else if (profileName.equals("City Resource")) {
                    List<Map<String, Object>> resources = dbHelperResources.getAvailableCityResource(siteId);
                    testData.addToList("assignToResourceIds", resources.get(0).get("ResourceId"));
                    testData.addToList("resourceNames", resources.get(0).get("ResourceName"));

                } else {
                    throw new Exception("Unexpected profile: " + profileName);
                }
            }

            runtimeState.scenario.write("SiteIds: " + testData.getList("assignToSiteIds", String.class));

            runtimeState.scenario.write("RFM resourceIds: " + testData.getList("RFMs", String.class));
            if (profileName.equals("Contractor Admin") || profileName.equals("Contractor Technician")) {
                runtimeState.scenario.write("Contractor resourceIds: " + testData.getList("assignToResourceIds", String.class));

            } else if (profileName.equals("City Resource")) {
                runtimeState.scenario.write("City Tech resourceIds: " + testData.getList("assignToResourceIds", String.class));
            }

        } else if (System.getProperty("assignToResourceIds") != null){

            String ids = System.getProperty("assignToResourceIds").toString();
            String userProfile = testData.getString("profileName");
            List<Map<String, Object>> dbData = dbHelperResources.getResources(ids, userProfile);
            CommonSteps.assertDataFound("No data found in database!!!", dbData);
            outputHelper.writeMapList(dbData);
            testData.put("assignees", dbData);

        } else if (System.getProperty("assignToNumResources") != null) {
            int count = Integer.valueOf(System.getProperty("assignToNumResources").toString());
            String userProfile = testData.getString("profileName");
            List<Map<String, Object>> dbData = dbHelperResources.getNumOfResources(count, userProfile);
            CommonSteps.assertDataFound("No data found in database!!!", dbData);
            outputHelper.writeMapList(dbData);
            testData.put("assignees", dbData);

        } else {
            throw new Exception("oops - this bit not coded yet!");
        }

    }

    @Then("^the number of jobs created is verified$")
    public void the_number_of_jobs_created_is_verified() {
        int expected = Integer.valueOf(System.getProperty("numberOfJobsRequired"));
        int actual = testData.getInt("numJobsCreated");
        assertTrue("Expected: " + expected + ", Actual: " + actual, expected == actual);
    }

    @When("^a list of \"([^\"]*)\"$")
    public void list_of_contractor_admins(String typeOfList) {
        if (typeOfList.equalsIgnoreCase("Contractor Admins")) {
            //change to list of contractor admins once received
            Map<String, Object> contractor = dbHelperResources.getContractorWithProfile("Contractor Admin");
            testData.put("contractorAdminName", contractor.get("ResourceName"));
            testData.put("assignToResourceId", contractor.get("ResourceId"));

        } else if (typeOfList.equalsIgnoreCase("Contractor Techs")) {
            //change to list of contractor techs once received
            ApplicationUser appUser = applicationUserDao.getForUserProfile("Contractor Technician");
            testData.put("useResourceTypeName", true);
            testData.put("assignToResourceId", appUser.getResourceId());
            testData.put("contractorTechName", (appUser.getFirstName() + " " + appUser.getLastName()).trim());

        } else if (typeOfList.equalsIgnoreCase("Contractor Admins without Techs")) {
            //change to list of contractor admins without techs once received
            //using specific contractor as db query is finding incorrect results at the minute
            testData.put("contractorAdminName", "Midwest Security Products");
            testData.put("assignToResourceId", 13059);
        }
    }

    @When("^the user profiles are setup$")
    public void the_user_profiles_are_setup() {
        list_of_contractor_admins(System.getProperty("userProfile"));
    }

    @Then("^\"([^\"]*)\" jobs are created for the number requested$")
    public void requested_num_jobs_are_created(String jobType) throws Throwable {
        int numberOfJobs = Integer.valueOf(System.getProperty("numberOfJobsRequired"));
        testData.put("useResourceTypeName", true);
        jobs_are_created(jobType, numberOfJobs);
    }

    private void setAssignToResourceId(int position) {
        if (testData.tagExist("assignees")) {
            List<Map<String, Object>> assignees = testData.getListMap("assignees");
            int index = (position + 1) % assignees.size();
            String resourceId = assignees.get(index).get("resourceId").toString();
            String resourceName = assignees.get(index).get("resourceName").toString();
            testData.put("assignToResourceId", resourceId);
            testData.put("contractorAdminName", resourceName);

        } else if (testData.tagExist("assignToResourceIds")) {
            int index = (position + 1) % testData.getIntList("assignToResourceIds").size();
            String resourceId = testData.getIntList("assignToResourceIds").get(index).toString();
            String resourceName = testData.getArray("resourceNames").get(index);
            String siteId = testData.getIntList("assignToSiteIds").get(index).toString();

            testData.put("assignToResourceId", resourceId);
            if (testData.getString("profileName").equals("City Resource")) {
                testData.put("resourceName", resourceName);
            } else {
                testData.put("contractorAdminName", resourceName);
            }
            testData.put("assignToSiteId", siteId);
        }
    }

    @Then("^\"([^\"]*)\" jobs are created \"([^\"]*)\" times$")
    public void jobs_are_created(String jobType, int numberOfJobs) throws Throwable {
        String todaysDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = runtimeState.scenario.getName().replaceAll(" ", "_").replaceAll("[,-]", "") + todaysDate + ".csv";
        String outputDir = "target/performanceDataFiles";

        new File(outputDir).mkdir();
        String filePath = outputDir + "/" + fileName;
        File file = new File(filePath);
        FileWriter outputfile = new FileWriter(file);
        CSVWriter writer = new CSVWriter(outputfile);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        int created = 0;
        if (jobType.equalsIgnoreCase("View, Accept and ETA - Contractor Admin")) {
            String[] columnHeaders = { "Job Reference", "Contractor Admin Name", "Resource Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                try {
                    portalUserWithJobSteps.a_Contractor_with_a_job_awaiting_acceptance();
                    String[] data = { testData.getString("jobReference"), testData.getString("contractorAdminName"), testData.getString("assignToResourceId") };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("Contractor Admin Name", testData.getString("contractorAdminName"));
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Start Work - Contractor Tech")) {
            String[] columnHeaders = { "Job Reference", "Site Id", "Contractor Tech Name", "Resource Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                try {
                    getPortalUserDataSteps.createJobData("Allocated");
                    String[] data = { testData.getString("jobReference"), testData.getString("siteId"), testData.getString("contractorTechName"), testData.getString("assignToResourceId") };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("Site Id", testData.getString("siteId"));
                    map.put("Contractor Tech Name", testData.getString("contractorTechName"));
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Update Job - Contractor Admin No Tech")) {
            String[] columnHeaders = { "Job Reference", "Contractor Admin Name", "Resource Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                try {
                    jobCreationHelper.createJobInStatus("Allocated / ETA Provided");
                    String[] data = { testData.getString("jobReference"), testData.getString("contractorAdminName"), testData.getString("assignToResourceId") };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("Contractor Admin Name", testData.getString("contractorAdminName"));
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Funding Request Awaiting Approval - RFM")) {
            String[] columnHeaders = { "Job Reference", "Contractor Admin Name", "Job assigned to Resource Id", "RFM Name", "RFM Resource Id", "Site Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                testData.addStringTag("profileName", "RFM");
                try {
                    jobCreationHelper.createJobInStatus("Logged / Awaiting Funding Authorisation");
                    // get the RFM for the new job and authorise funding request for contractor
                    int siteId = testData.getInt("siteId");
                    Map<String, Object> dbData = dbHelperSites.getRfmForSite(siteId);
                    int rfmResourceId = Integer.valueOf(dbData.get("ResourceId").toString());
                    String rfmName = dbHelperResources.getResourceName(rfmResourceId);
                    String[] data = { testData.getString("jobReference"), testData.getString("contractorAdminName"), testData.getString("assignToResourceId"), rfmName, String.valueOf(rfmResourceId), String.valueOf(siteId) };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("Contractor Admin Name", testData.getString("contractorAdminName"));
                    map.put("RFM Name", rfmName);
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Approved Funding Request - RFM")) {
            String[] columnHeaders = { "Job Reference", "Contractor Admin Name", "Job assigned to Resource Id", "RFM Name", "RFM Resource Id", "Site Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                testData.addStringTag("profileName", "RFM");
                try {
                    jobCreationHelper.createJobInStatus("Logged / Awaiting Funding Authorisation");

                    // get the RFM for the new job and authorise funding request for contractor
                    int siteId = testData.getInt("siteId");
                    int jobRef = testData.getInt("jobReference");
                    jobCreationHelper.approveContractorFunding(jobRef, siteId);
                    Map<String, Object> dbData = dbHelperSites.getRfmForSite(siteId);
                    int rfmResourceId = Integer.valueOf(dbData.get("ResourceId").toString());
                    String rfmName = dbHelperResources.getResourceName(rfmResourceId);
                    String[] data = { testData.getString("jobReference"), testData.getString("contractorAdminName"), testData.getString("assignToResourceId"), rfmName, String.valueOf(rfmResourceId), String.valueOf(siteId) };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("Contractor Admin Name", testData.getString("contractorAdminName"));
                    map.put("RFM Name", rfmName);
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Close Job - Contractor Admin")) {
            String[] columnHeaders = { "Job Reference", "Site Id", "Contractor Tech Name", "Resource Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                try {
                    getPortalUserDataSteps.createJobData("In Progress");
                    String[] data = { testData.getString("jobReference"), testData.getString("siteId"), testData.getString("contractorTechName"), testData.getString("assignToResourceId") };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("Site Id", testData.getString("siteId"));
                    map.put("Contractor Tech Name", testData.getString("contractorTechName"));
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Complete / Orders Awaiting Invoice With No Invoice")) {
            String[] columnHeaders = { "Job Reference", "Contractor Admin Name", "Job assigned to Resource Id", "RFM Name", "RFM Resource Id", "Site Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                testData.addStringTag("profileName", "RFM");
                try {
                    jobCreationHelper.createJobInStatus(jobType);
                    // get the RFM for the new job
                    int siteId = testData.getInt("siteId");
                    Map<String, Object> dbData = dbHelperSites.getRfmForSite(siteId);
                    int rfmResourceId = Integer.valueOf(dbData.get("ResourceId").toString());
                    String rfmName = dbHelperResources.getResourceName(rfmResourceId);
                    String[] data = { testData.getString("jobReference"), testData.getString("contractorAdminName"), testData.getString("assignToResourceId"), rfmName, String.valueOf(rfmResourceId), String.valueOf(siteId) };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("Contractor Admin Name", testData.getString("contractorAdminName"));
                    map.put("RFM Name", rfmName);
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Update Job - City Tech")) {
            String[] columnHeaders = { "Job Reference", "City Tech Name", "Resource Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                try {
                    jobCreationHelper.createJobInStatus("Allocated / ETA Provided");
                    String[] data = { testData.getString("jobReference"), testData.getString("contractorAdminName"), testData.getString("assignToResourceId") };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("City Tech Name", testData.getString("contractorAdminName"));
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Quotes Awaiting Review")) {
            String[] columnHeaders = { "Job Reference", "Site Id", "City Tech Name", "Resource Id", "RFM Name", "RFM Resource Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                try {
                    int siteId = testData.getInt("assignToSiteId");
                    Map<String, Object> dbData = dbHelperSites.getRfmForSite(siteId);
                    int rfmResourceId = (Integer) dbData.get("ResourceId");
                    String rfmName = dbHelperResources.getResourceName(rfmResourceId);
                    getPortalUserDataSteps.a_with_a_in_state_with_a_funding_route_with_a_budget("RFM", "single", "Quote", "Quotes Awaiting Review", "OPEX", "less");
                    String[] data = { testData.getString("jobReference"), testData.getString("assignToSiteId"), testData.getString("resourceName"), testData.getString("assignToResourceId"), rfmName,  String.valueOf(rfmResourceId)};
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("Site Id", siteId);
                    map.put("City Tech Name", testData.getString("resourceName"));
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    map.put("RFM Name", rfmName);
                    map.put("RFM Resource Id", String.valueOf(rfmResourceId));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else if (jobType.equalsIgnoreCase("Parts Awaiting Approval")) {
            String[] columnHeaders = { "Job Reference", "City Tech Name", "Resource Id" };
            writer.writeNext(columnHeaders);

            for (int i = 0; i < numberOfJobs; i++) {
                setAssignToResourceId(i);
                try {
                    jobCreationHelper.createJobInStatus("Parts Awaiting Approval");
                    String[] data = { testData.getString("jobReference"), testData.getString("contractorAdminName"), testData.getString("assignToResourceId") };
                    writer.writeNext(data);
                    map = new HashMap<String, Object>();
                    map.put("Job Reference", testData.getString("jobReference"));
                    map.put("City Tech Name", testData.getString("resourceName"));
                    map.put("Resource Id", testData.getString("assignToResourceId"));
                    list.add(map);
                    created = created + 1;
                } catch (Throwable t) {
                    //skip
                }
                testData.put("numJobsCreated", created);
            }
            writer.close();
            outputHelper.writeMapList(list);

        } else {
            writer.close();
            throw new Exception("Unexpected job type: " + jobType);
        }
    }

    @When("^the invoice PDFs are uploaded to the carousel$")
    public void the_invoice_PDFs_are_uploaded_to_the_carousel() throws Throwable {
        String connectionString = propertyHelper.getAccountsPayableCarousel();
        String localPathname = System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\Invoice_10005051.pdf";

        int numInvoicesToUpload = Integer.valueOf(System.getProperty("numInvoicesToUpload"));
        for (int i = 1; i <= numInvoicesToUpload; i++) {
            String remoteFileName = String.format("Uploads/InvoiceShareAP/1/Invoice_%03d.pdf", i);
            azureStorageHelper.uploadBlob(connectionString, localPathname, "city-holdings", remoteFileName);
            blobsUploaded.add(remoteFileName);
        }
    }

    @Then("^all uploaded invoice PDFs are on the carousel$")
    public void all_uploaded_invoice_PDFs_are_on_the_carousel() throws Throwable {
        String connectionString = propertyHelper.getAccountsPayableCarousel();
        List<String> files = azureStorageHelper.getBlobs(connectionString, "city-holdings", "Uploads/InvoiceShareAP/1");
        runtimeState.scenario.write("Accounts Payable Carousel: " + files.toString());

        for (String blob : blobsUploaded) {
            assertTrue("Blob not uploaded" + blob, files.toString().contains(blob));
        }
    }
}
