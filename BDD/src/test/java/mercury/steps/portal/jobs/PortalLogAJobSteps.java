package mercury.steps.portal.jobs;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.JobViewDao;
import mercury.database.dao.SiteAssetDao;
import mercury.database.models.JobView;
import mercury.database.models.SiteAsset;
import mercury.databuilders.CallerContact;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.LogJobHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.gridV3.Grid;
import mercury.pageobject.web.portal.NewSiteContactPage;
import mercury.pageobject.web.portal.jobs.JobLoggedConfigmationPage;
import mercury.pageobject.web.portal.jobs.PortalLogAJobPage;
import mercury.runtime.RuntimeState;

public class PortalLogAJobSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private JobViewDao jobViewDao;
    @Autowired private SiteAssetDao siteAssetDao;
    @Autowired private NewJob job;
    @Autowired private TestData testData;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperJobs dbHelperJobs;

    SiteAsset siteAsset;
    JobView currentJob;
    List<JobView> jobList;

    public void the_log_a_job_form_is_completed(Boolean asset, Boolean randomLocation) throws Exception {
        StringBuilder output = new StringBuilder();
        output.append("Site Name: " + siteAsset.getName());
        output.append(", Classification : " + siteAsset.getAssetTypeName() + " > " + siteAsset.getAssetSubTypeName());
        output.append(", Asset Location : " + siteAsset.getSubLocationName());
        output.append(", Fault Type : " + siteAsset.getFaultTypeName());
        runtimeState.scenario.write(output.toString());

        // get page object and enter site
        runtimeState.portalLogAJobPage = new PortalLogAJobPage(getWebDriver()).get();
        runtimeState.scenario.write("Setting site name as : " + siteAsset.getName());
        runtimeState.portalLogAJobPage.selectSite(siteAsset.getName());
        testData.put("siteName", siteAsset.getName());
        // If asset
        if (asset) {
            // Search for a partial match of the expected asset name in drop down - fixed in MCP-140
            String assetPartialTest = siteAsset.getAssetTypeName() + " > " + siteAsset.getAssetSubTypeName();

            assetPartialTest = assetPartialTest.concat((siteAsset.getAssetClassificationName() == null || siteAsset.getAssetClassificationName().isEmpty()) ? "" : (" > " + siteAsset.getAssetClassificationName()));
            assetPartialTest = assetPartialTest.concat((siteAsset.getSerialNo() == null || siteAsset.getSerialNo().isEmpty()) ? "" : (" > " + siteAsset.getSerialNo()));
            assetPartialTest = assetPartialTest.concat(siteAsset.getAssetTag() == null ? "" : (" > " + siteAsset.getAssetTag()));
            assetPartialTest = assetPartialTest.concat((siteAsset.getLocalIdentifier() == null || siteAsset.getLocalIdentifier().isEmpty()) ? "" : (" > " + siteAsset.getLocalIdentifier()));

            runtimeState.scenario.write("Setting Asset Sub Name as : " + assetPartialTest);
            runtimeState.portalLogAJobPage.selectAsset(assetPartialTest);

        } else {
            // else enter classification and location
            String classification = siteAsset.getAssetTypeName() == null ? "" : siteAsset.getAssetTypeName();

            classification = classification.concat(siteAsset.getAssetSubTypeName() == null ? "" : " > " + siteAsset.getAssetSubTypeName());
            classification = classification.concat( (siteAsset.getAssetClassificationName() == null || siteAsset.getAssetClassificationName().isEmpty()) ? "" : (" > " + siteAsset.getAssetClassificationName()));

            runtimeState.scenario.write("Setting Classification as : " + classification);
            runtimeState.portalLogAJobPage.selectClassification(classification);
            if (randomLocation) {
                runtimeState.scenario.write("Setting Random Location");
                runtimeState.portalLogAJobPage.selectRandomAssetLocation();
            } else {
                runtimeState.scenario.write("Setting Location Name as : " + siteAsset.getSubLocationName());
                runtimeState.portalLogAJobPage.selectAssetLocation(siteAsset.getSubLocationName());
            }

        }

        // Enter fault type and description
        runtimeState.scenario.write("Setting Fault Type as : " + siteAsset.getFaultTypeName());
        runtimeState.portalLogAJobPage.selectFaultType(siteAsset.getFaultTypeName());

        String description = runtimeState.scenario.getName().replaceAll("\\[.*\\]", "").trim();
        testData.put("description", description);
        runtimeState.scenario.write("Setting Description as : " + description);
        runtimeState.portalLogAJobPage.enterFaultDescription(description);

        if (runtimeState.portalLogAJobPage.SiteHasContacts()) {
            runtimeState.portalLogAJobPage.selectRandomSiteContact();
        }

        LogJobHelper.answerJobQuestions(runtimeState, testData);
    }


    @When("^the form is completed with a \"([^\"]*)\" priority non tagged asset$")
    public void get_non_tagged_asset(String priority) throws Exception {
        siteAsset = siteAssetDao.getRandomSiteAsset_CTE(priority);
        if (siteAsset == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        job.setLocation(siteAsset.getSubLocationName());
        job.setAssetTag(siteAsset.getAssetTag());
        job.setPriority(siteAsset.getFaultPriority());
        job.setSite(siteAsset.getName());
        job.setSubtype(siteAsset.getAssetSubTypeName());
        job.setDescription("The thing is broken, please help me obi wan kenobi you're my only hope. " + DateHelper.dateAsString(new Date()) );
        job.setFault(siteAsset.getFaultTypeName());

        the_log_a_job_form_is_completed(true, true);
    }

    @When("^the form is completed with a non tagged asset$")
    public void get_non_tagged_asset() throws Exception {
        siteAsset = siteAssetDao.getRandomSiteAsset_CTE();
        if (siteAsset == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        job.setLocation(siteAsset.getSubLocationName());
        job.setAssetTag(siteAsset.getAssetTag());
        job.setPriority(siteAsset.getFaultPriority());
        job.setSite(siteAsset.getName());
        job.setSubtype(siteAsset.getAssetSubTypeName());
        job.setDescription("The thing is broken, please help me obi wan kenobi you're my only hope. " + DateHelper.dateAsString(new Date()) );
        job.setFault(siteAsset.getFaultTypeName());

        the_log_a_job_form_is_completed(true, true);
    }

    @When("^the form is completed with a tagged asset$")
    public void get_tagged_asset() throws Exception {
        siteAsset = siteAssetDao.getRandomSiteTaggedAsset_CTE();

        if (siteAsset == null) {
            throw new PendingException("Cannot find suitable data for test");
        }

        job.setLocation(siteAsset.getSubLocationName());
        job.setAssetTag(siteAsset.getAssetTag());
        job.setPriority(siteAsset.getFaultPriority());
        job.setSite(siteAsset.getName());
        job.setSubtype(siteAsset.getAssetSubTypeName());
        job.setDescription("The thing is broken, please help me obi wan kenobi you're my only hope. " + DateHelper.dateAsString(new Date()) );
        job.setFault(siteAsset.getFaultTypeName());

        the_log_a_job_form_is_completed(true, true);
    }


    @When("^the form is completed with a \"([^\"]*)\" priority \"([^\"]*)\" asset$")
    public void get_priority_asset(String priority, String asset) throws Exception {
        Boolean tagged = ("tagged".equalsIgnoreCase(asset) == true ? true : false);
        siteAsset = siteAssetDao.getRandomSiteAsset_CTE(priority, tagged);
        if (siteAsset == null) {
            throw new PendingException("Cannot find suitable data for test");
        }

        job.setLocation(siteAsset.getSubLocationName());
        job.setAssetTag(siteAsset.getAssetTag());
        job.setPriority(siteAsset.getFaultPriority());
        job.setSite(siteAsset.getName());
        job.setSubtype(siteAsset.getAssetSubTypeName());
        job.setDescription("The thing is broken, please help me obi wan kenobi you're my only hope. " + DateHelper.dateAsString(new Date()) );
        job.setFault(siteAsset.getFaultTypeName());


        the_log_a_job_form_is_completed(true, true);
    }

    @When("^the form is completed with a \"([^\"]*)\" priority \"([^\"]*)\" asset ((?:with|without)) serial number$")
    public void get_priority_asset_tagged_serial(String priority, String asset, String serial) throws Exception {
        Boolean tagged = ("tagged".equalsIgnoreCase(asset) == true ? true : false);
        Boolean serialNo = ("with".equalsIgnoreCase(serial) == true ? true : false);
        siteAsset = siteAssetDao.getRandomSiteAsset_CTE(priority, tagged, serialNo);
        if (siteAsset == null) {
            throw new PendingException("Cannot find suitable data for test");
        }

        job.setLocation(siteAsset.getSubLocationName());
        job.setAssetTag(siteAsset.getAssetTag());
        job.setPriority(siteAsset.getFaultPriority());
        job.setSite(siteAsset.getName());
        job.setSubtype(siteAsset.getAssetSubTypeName());
        job.setDescription("The thing is broken, please help me obi wan kenobi you're my only hope. " + DateHelper.dateAsString(new Date()) );
        job.setFault(siteAsset.getFaultTypeName());

        the_log_a_job_form_is_completed(true, true);
    }

    @When("^the form is completed ((?:with|without)) tagged asset$")
    public void the_form_is_completed_with_asset_tagged_serial(String tagged) throws Exception {
        Boolean asset = ("with".equalsIgnoreCase(tagged) == true ? true : false);

        runtimeState.portalLogAJobPage = new PortalLogAJobPage(getWebDriver()).get();
       
        siteAsset = siteAssetDao.getRandomSiteAsset_CTE("P1", asset, false);
        if (siteAsset == null) {
            throw new PendingException("Cannot find suitable data for test");
        }
        runtimeState.portalLogAJobPage.selectSite(siteAsset.getName());
        
        if (asset) {
            runtimeState.portalLogAJobPage.selectRandomAsset();

        } else {
            runtimeState.portalLogAJobPage.selectRandomClassification();
        }
        runtimeState.scenario.write("Setting Random Location");   
        runtimeState.portalLogAJobPage.selectRandomAssetLocation();

        // Enter fault type and description
        runtimeState.portalLogAJobPage.selectRandomFaultType();

        String description = runtimeState.scenario.getName().replaceAll("\\[.*\\]", "").trim();
        testData.put("description", description);
        runtimeState.scenario.write("Setting Description as : " + description);
        runtimeState.portalLogAJobPage.enterFaultDescription(description);

        runtimeState.portalLogAJobPage.selectRandomSiteContact();
        LogJobHelper.answerJobQuestions(runtimeState, testData);
    }

    @When("^the form is completed with a \"([^\"]*)\" priority and no asset$")
    public void the_form_is_completed_without_asset(String priority) throws Exception {
        siteAsset = siteAssetDao.getRandomSiteAsset_CTE(priority);
        if (null == siteAsset) {
            throw new PendingException("Cannot find suitable data for test");
        }

        job.setLocation(siteAsset.getSubLocationName());
        job.setAssetTag(siteAsset.getAssetTag());
        job.setClassification(siteAsset.getAssetClassificationName());
        job.setPriority(siteAsset.getFaultPriority());
        job.setSite(siteAsset.getName());
        job.setSubtype(siteAsset.getAssetSubTypeName());
        job.setDescription("The thing is broken, please help me obi wan kenobi you're my only hope. " + DateHelper.dateAsString(new Date()) );
        job.setFault(siteAsset.getFaultTypeName());

        the_log_a_job_form_is_completed(false, true);
    }

    @When("^the form is completed with a \"([^\"]*)\" priority and no asset to be assigned to technician$")
    public void the_form_is_completed_without_asset1(String priority) throws Exception {
        siteAsset = siteAssetDao.getRandomSiteAsset_CTE(priority);
        if (null == siteAsset) {
            throw new PendingException("Cannot find suitable data for test");
        }

        job.setLocation(siteAsset.getSubLocationName());
        job.setAssetTag(siteAsset.getAssetTag());
        job.setPriority(siteAsset.getFaultPriority());
        job.setSite(siteAsset.getName());
        job.setSubtype(siteAsset.getAssetSubTypeName());
        job.setDescription("The thing is broken, please help me obi wan kenobi you're my only hope. " + DateHelper.dateAsString(new Date()) );
        job.setFault(siteAsset.getFaultTypeName());

        the_log_a_job_form_is_completed(false, true);
    }

    @ContinueNextStepsOnException
    @Then("^the user cannot log a job$")
    public void the_user_cannot_log_a_job() throws Exception {
        boolean exceptionCaught = false;
        try {
            the_form_is_completed_without_asset1("P1");
        } catch (Exception e) {
            exceptionCaught = true;
        }
        assertTrue("User IS able to log a job!!!", exceptionCaught);
        runtimeState.scenario.write("Successfully unable to complete and save log job form");
    }

    @When("^a new contact is added for the site$")
    public void I_add_a_new_contact_for_the_site() throws Exception {
        CallerContact cc = new CallerContact.Builder().build();
        runtimeState.scenario.write("Adding a new contact: " + cc.toString());

        NewSiteContactPage newSiteContactPage = runtimeState.portalLogAJobPage.addNewContact();
        newSiteContactPage.enterName(cc.getName());
        newSiteContactPage.enterJobTitle(cc.getJobTitle());
        newSiteContactPage.enterDepartment(cc.getDepartment());
        newSiteContactPage.enterTelephone(cc.getTelephone());
        job.setCaller(cc);
        runtimeState.portalLogAJobPage = newSiteContactPage.save();
    }

    @When("^an? \"([^\"]*)\" contact is added for the site$")
    public void I_add_a_contact_for_the_site(String contact) throws Exception {
        Boolean siteHasContacts = runtimeState.portalLogAJobPage.SiteHasContacts();
        if (contact.equalsIgnoreCase("new") || !siteHasContacts){
            I_add_a_new_contact_for_the_site();
        } else {
            runtimeState.portalLogAJobPage.selectRandomSiteContact();
        }
    }

    @When("^the \"([^\"]*)\" resource is assigned to the job$")
    public void the_resource_is_assigned_to_the_job(String resourceProfile) throws Exception {
        if (!resourceProfile.equalsIgnoreCase("me")){
            // Need to get a list of possible resources for the site
            runtimeState.portalLogAJobPage.clickNo("Assign to me?");
            resourceProfile = resourceProfile.equalsIgnoreCase("City Resource") ? dbHelperResources.getResourceProfileNameForSite(testData.getString("siteName")) : resourceProfile;
            runtimeState.portalLogAJobPage.selectResource(resourceProfile);
        } else {
            String newEtaDate = DateHelper.getNowDatePlusOffset(24, FULL_DATE);
            runtimeState.portalLogAJobPage.selectETADate(newEtaDate);
            runtimeState.portalLogAJobPage.selectRandomETAWindow();
            Random rand = new Random();

            // Randomly advise site of ETA
            boolean siteAdvised = rand.nextBoolean();

            if (siteAdvised) {
                CallerContact cc = new CallerContact.Builder().build();

                runtimeState.portalLogAJobPage.clickYes("ETA advised to site");
                runtimeState.portalLogAJobPage.enterAdvisedTo(cc.getName());

                runtimeState.scenario.write("ETA is advised to " + cc.getName());
            } else {
                runtimeState.portalLogAJobPage.clickNo("ETA advised to site");
            }

        }
        outputHelper.takeScreenshots();
    }

    @When("^the log job form is saved$")
    public void click_save_to_log_the_job() throws Exception {
        outputHelper.takeScreenshots();
        
        if (runtimeState.portalLogAJobPage.isSiteContactErrorMessageDisplayed()) {
            runtimeState.portalLogAJobPage.selectRandomSiteContact();
        }
        
        try {
            runtimeState.jobLoggedConfigmationPage = runtimeState.portalLogAJobPage.submitFormSuccess();
        } catch (Exception e) {
            outputHelper.takeScreenshots();
            runtimeState.scenario.write("It took a bit longer to save a job");
            runtimeState.jobLoggedConfigmationPage = new JobLoggedConfigmationPage(getWebDriver()).get();
        }
    }

    @When("^the Cancel button is clicked on the Log Job form$")
    public void click_cancel_to_abort_the_log_a_job() throws Exception {
        outputHelper.takeScreenshots();
        runtimeState.portalLogAJobPage.submitFormCancel();
    }

    @When("^the form is complete with duplicate information$")
    public void the_form_is_complete_with_duplicate_information() throws Exception {
        Map<String, Object> dbData = dbHelperJobs.getRandomJobForDuplication();
        runtimeState.scenario.write("original job reference: " + dbData.get("JobReference"));

        siteAsset = new SiteAsset();
        siteAsset.setSiteCode(dbData.get("SiteCode").toString());
        siteAsset.setName(dbData.get("SiteName").toString());
        siteAsset.setAssetSubTypeName(dbData.get("AssetSubTypeName").toString());
        siteAsset.setAssetTypeName(dbData.get("AssetTypeName").toString());
        siteAsset.setSubLocationName(dbData.get("LocationName").toString());
        siteAsset.setFaultTypeName(dbData.get("FaultTypeName").toString());
        siteAsset.setAssetClassificationName(dbData.get("AssetClassificationName").toString());

        the_log_a_job_form_is_completed(false, false);
    }

    @When("^a quote is requested$")
    public void I_request_a_Quote() throws Exception {
        runtimeState.portalLogAJobPage.requestQuote();
    }


    @ContinueNextStepsOnException
    @Then("^the potential duplicate Jobs grid will be displayed$")
    public void the_potential_duplicate_Jobs_grid_will_be_displayed() throws Exception {
        Grid grid = runtimeState.portalLogAJobPage.getGrid();
        assertNotNull("Unexpected Null Grid", grid);
        assertTrue("Unepected null row count", grid.getRows().size()>0);
        // TODO : check each row
    }

    @ContinueNextStepsOnException
    @Then("^a confirmation that the job has been logged with job number is displayed$")
    public void Then_I_will_be_advised_of_the_job_reference() throws Exception {
        int jobReference = Integer.valueOf(runtimeState.jobLoggedConfigmationPage.getJobReference());
        job.setJobReference(jobReference);
        testData.put("jobReference", jobReference);
        logger.debug(runtimeState.jobLoggedConfigmationPage.getJobReference());
        runtimeState.scenario.write(String.format("New job logged %d", jobReference));
    }

    @ContinueNextStepsOnException
    @Then("^the Job will exist in the database$")
    public void the_Job_will_exist_in_the_database() throws Exception {
        currentJob = jobViewDao.getByJobReference(testData.getInt("jobReference"));
        assertNotNull(currentJob);
        assertEquals("Unexpected Site Code found", siteAsset.getSiteCode(), currentJob.getSiteCode());
        assertEquals("Unexpected Asset Name found", siteAsset.getAssetSubTypeName(), currentJob.getAssetSubTypeName());
        //		assertEquals("unexpected Location found", siteAsset.getSubLocationName(), currentJob.getSubLocationName());
        assertTrue("Unexpected Fault Type found", currentJob.getFaultType().equals(siteAsset.getFaultTypeName()) || currentJob.getFaultType().contains(siteAsset.getFaultTypeName()));

        String expectedDescription = testData.get("description") != null ? testData.getString("description") : job.getDescription();
        assertEquals("Unexpected Description found", expectedDescription, currentJob.getDescription());
    }

    @ContinueNextStepsOnException
    @Then("^a popup is displayed requesting Cancel confirmation$")
    public void i_will_be_prompted() throws Exception {
        assertTrue("Confirmation to Cancel is not displayed", runtimeState.portalLogAJobPage.isLeavingPageDisplayed());
    }

}
