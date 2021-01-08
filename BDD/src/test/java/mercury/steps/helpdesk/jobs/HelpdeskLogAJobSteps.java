package mercury.steps.helpdesk.jobs;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import mercury.database.dao.CallerDetailsDao;
import mercury.database.dao.FaultPriorityMappingDao;
import mercury.database.dao.JobDao;
import mercury.database.dao.SiteViewDao;
import mercury.database.models.FaultPriorityMapping;
import mercury.database.models.SiteContractorAsset;
import mercury.database.models.SiteView;
import mercury.databuilders.CallerContact;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.HelpdeskLogJobForm;
import mercury.helpers.HelpdeskSearchHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.LogJobHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.apihelper.ApiHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.asserter.dbassertions.AssertJobNotCreated;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperJobs;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.pageobject.web.helpdesk.cards.ActiveCallerCard;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskQuotesPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskManageResourcesPanel;
import mercury.runtime.RuntimeState;
import mercury.steps.CommonSteps;
import mercury.steps.LoginSteps;

public class HelpdeskLogAJobSteps {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private ApiHelper apiHelper;

    @Autowired private CallerContact callerContact;
    @Autowired private SiteView siteView;
    @Autowired private NewJob job;
    @Autowired private TestData testData;

    @Autowired private CallerDetailsDao callerDetailsDao;
    @Autowired private FaultPriorityMappingDao faultPriorityMappingDao;
    @Autowired private SiteContractorAsset siteContractorAsset;
    @Autowired private JobDao jobDao;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private SiteViewDao siteViewDao;

    @Autowired private AssertionFactory assertionFactory;

    @Autowired private CommonSteps commonSteps;
    @Autowired private DbHelperJobs dbHelperJobs;
    @Autowired private LoginSteps loginSteps;
    @Autowired private DbHelperQuotes dbHelperQuotes;

    @Autowired HelpdeskSearchHelper helpdeskSearchHelper;
    @Autowired private PropertyHelper propertyHelper;


    @When("^Subtype/Classification \"([^\"]*)\" is entered$")
    public void subtype_Classification_is_entered(String subtype) throws Throwable {
        runtimeState.helpdeskLogJobPage.addSubtypeClassification(subtype);
        job.setClassification(runtimeState.helpdeskLogJobPage.getClassification());
    }

    @When("^Core Details are entered$")
    public void core_details_are_entered() throws Throwable {
        an_existing_Caller_is_entered();
        subtype_Classification_is_entered("Water");
        location_entered("Pharmacy");
        outputHelper.takeScreenshots();
    }

    @When("^a Job Description is entered$")
    public void job_details_are_entered() throws Throwable {
        runtimeState.helpdeskLogJobPage.addJobDetails("the Dilithium crystals are barely holding out!");
        outputHelper.takeScreenshots();
    }

    @When("^Job Description \"([^\"]*)\" is entered$")
    public void job_description_entered(String description) throws Throwable {
        runtimeState.helpdeskLogJobPage.addJobDetails(description);
        job.setDescription(description);
    }

    @When("^all mandatory fields for a new caller are entered$")
    public void new_caller_details_are_added() throws Throwable {

        callerContact.copy(new CallerContact.Builder().build());
        callerContact.setCallerType("Client");
        job.setCaller(callerContact);

        if (getWebDriver().getCurrentUrl().contains("search/addcaller")) {

            runtimeState.helpdeskAddCallerPage.enterName(callerContact.getName());
            runtimeState.helpdeskAddCallerPage.enterJobRole(callerContact.getJobTitle());
            runtimeState.helpdeskAddCallerPage.enterDepartment(callerContact.getDepartment());
            runtimeState.helpdeskAddCallerPage.enterTelephone(callerContact.getTelephone());
            runtimeState.helpdeskAddCallerPage.enterExtension(callerContact.getExtension());

            testData.put("addCaller", true);

        } else {

            runtimeState.newCallerPage.enterName(callerContact.getName());
            runtimeState.newCallerPage.enterJobRole(callerContact.getJobTitle());
            runtimeState.newCallerPage.enterDepartment(callerContact.getDepartment());
            runtimeState.newCallerPage.enterTelephone(callerContact.getTelephone());
            runtimeState.newCallerPage.enterExtension(callerContact.getExtension());

            testData.put("newCaller", true);
        }
    }

    @When("^the New Caller form is saved$")
    public void new_caller_details_are_saved() throws Throwable {
        outputHelper.takeScreenshots();
        if ( testData.getBoolean("addCaller") ) {
            runtimeState.helpdeskAddCallerPage = runtimeState.helpdeskAddCallerPage.clickSaveAndIdentifyAsCaller();
        } else {
            runtimeState.helpdeskLogJobPage = runtimeState.newCallerPage.save();
        }
    }

    @When("^user selects Add New Caller$")
    public void add_new_caller_clicked() throws Throwable {
        runtimeState.newCallerPage = runtimeState.helpdeskLogJobPage.clickAddNewCaller();
    }

    @When("^the new caller can be added to the core details$")
    public void the_new_caller_can_be_added_to_the_core_details() throws Throwable {
        runtimeState.helpdeskLogJobPage.selectCaller(callerContact.getName());
        outputHelper.takeScreenshots();
    }

    @When("^an existing Caller is entered$")
    public void an_existing_Caller_is_entered() throws Throwable {
        callerContact.copy(callerDetailsDao.getRandomClientCallerForSite(siteView.getName()));
        String departmentName = callerContact.getDepartment();
        String caller;
        if (departmentName.isEmpty() || departmentName == null) {
            caller = callerContact.getName();
        } else {
            caller = callerContact.getName() + " (" + callerContact.getDepartment() + ")";
        }
        runtimeState.helpdeskLogJobPage.selectCaller(caller);
        job.setCaller(callerContact);
    }

    @When("^a City Tech caller is entered$")
    public void a_City_Tech_caller_is_entered() throws Throwable {
        String siteName = dbHelperSites.getSiteName(testData.getInt("siteId"));
        String caller = callerDetailsDao.getRandomResourceCallerForSite(siteName).getName();
        runtimeState.helpdeskLogJobPage.selectCaller(caller);
    }

    @When("^the existing Caller is entered$")
    public void the_existing_Caller_is_entered() throws Throwable {
        String caller;
        if (callerContact.getDepartment().isEmpty()) {
            caller = callerContact.getName();
        } else {
            caller = callerContact.getName() + " (" + callerContact.getDepartment() + ")";
        }
        runtimeState.scenario.write("caller: " + caller);
        runtimeState.helpdeskLogJobPage.selectCaller(caller);
        job.setCaller(callerContact);
    }

    @When("^all other mandatory fields are entered$")
    public void all_other_mandatory_fields_are_entered() throws Throwable {
        LogJobHelper.enterAsset(runtimeState, testData);
        LogJobHelper.enterLocation(runtimeState, testData);
        LogJobHelper.enterDescription(runtimeState, testData);
        LogJobHelper.enterFault(runtimeState, testData);
        LogJobHelper.answerDeferralQuestions(runtimeState, testData);
        LogJobHelper.answerResourceQuestions(runtimeState, testData);
        LogJobHelper.answerJobQuestions(runtimeState, testData);
        LogJobHelper.selectJobContactSameAsCaller(runtimeState);
    }

    @When("^job is saved and edit job is selected$")
    public void job_is_saved_and_edit_job_is_selected() throws Throwable {
        runtimeState.helpdeskLogJobPage.save();
        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
        int jobRef = runtimeState.helpdeskJobPage.getJobReference();
        testData.put("jobreference", jobRef);
        commonSteps.the_action_is_selected("Edit");
    }

    @When("^an existing Caller for an occupied site$")
    public void an_existing_Caller_for_an_occupied_site() throws Throwable {
        callerContact.copy(callerDetailsDao.getRandomClientCallerWithSite("occupied"));
        String caller = callerContact.getName() + " (" + callerContact.getDepartment() + ")";
        runtimeState.helpdeskLogJobPage.selectCaller(caller);
        job.setCaller(callerContact);
    }

    @When("^Caller \"([^\"]*)\" is entered$")
    public void caller_entered(String name) throws Throwable {

        runtimeState.helpdeskLogJobPage.selectCaller(name);

        CallerContact callerContact = new CallerContact();
        callerContact.setName(StringUtils.split(runtimeState.helpdeskLogJobPage.getCaller(), "(")[0].trim());
        String department = StringUtils.substringBetween(runtimeState.helpdeskLogJobPage.getCaller(), "(", ")");
        callerContact.setName(callerContact.getName());
        callerContact.setCallerType(runtimeState.helpdeskLogJobPage.getCallerType());
        callerContact.setDepartment(department);
        callerContact.setExtension(runtimeState.helpdeskLogJobPage.getExtension());
        callerContact.setTelephone(runtimeState.helpdeskLogJobPage.getPhoneNo());

        job.setCaller(callerContact);
    }

    @When("^Location within Site is set to \"([^\"]*)\"$")
    public void location_entered(String location) {
        runtimeState.helpdeskLogJobPage.selectLocation(location);
        job.setLocation(runtimeState.helpdeskLogJobPage.getLocation());
    }

    @When("^Fault Type \"([^\"]*)\" is entered$")
    public void fault_Type_is_entered(String fault) throws Throwable {
        runtimeState.helpdeskLogJobPage.selectFault(fault);
        job.setFault(fault);
        job.setPriority(runtimeState.helpdeskLogJobPage.getPriority());
    }

    @When("^the answer to \"([^\"]*)\" is set to Yes$")
    public void the_answer_to_is_set_to_Yes(String question) throws Throwable {
        runtimeState.helpdeskLogJobPage.resourceQuestionSelectYes(question);
    }

    @When("^the answer to \"([^\"]*)\" is set to No$")
    public void the_answer_to_is_set_to_No(String question) throws Throwable {
        runtimeState.helpdeskLogJobPage.resourceQuestionSelectNo(question);
    }

    @When("^the Job Contact is the same as caller$")
    public void the_Job_Contact_is_the_same_as_caller() throws Throwable {
        runtimeState.helpdeskLogJobPage.clickSameAsCaller();
    }

    @Given("^an invalid alternative number is entered$")
    public void an_invalid_alternative_number_is_entered() throws Throwable {
        String phoneNumber = DataGenerator.generateInvalidPhoneNumber();
        runtimeState.helpdeskLogJobPage.enterContactAlternativeNumber(phoneNumber);
    }

    @When("^the Job Contact \"([^\"]*)\" a City resource$")
    public void the_Job_Contact_a_City_resource(String flag) throws Throwable {

        runtimeState.helpdeskLogJobPage.clickAddJobContact();

        runtimeState.helpdeskLogJobPage.enterContactNotes(getRandomNote());

        List<String> siteContacts = runtimeState.helpdeskLogJobPage.getSiteContacts();

        for (String contact : siteContacts) {
            String name = contact.split("\\(")[0].split(" - ")[0].trim();

            String resourceType = dbHelperResources.getResourceType(name);

            if (!resourceType.isEmpty()) {
                if ("IS".equalsIgnoreCase(flag) && resourceType.equals("City Resource")) {
                    runtimeState.helpdeskLogJobPage.selectSiteContact(contact);
                    break;
                } else if ( !"IS".equalsIgnoreCase(flag) && !resourceType.equals("City Resource")) {
                    runtimeState.helpdeskLogJobPage.selectSiteContact(contact);
                    break;
                }
            }
        }

        callerContact.setName(runtimeState.helpdeskLogJobPage.getSiteContacts().get(0));

        int tel_code = RandomUtils.nextInt(1000);
        int tel_mid = RandomUtils.nextInt(1000);
        int tel_end = RandomUtils.nextInt(10000);
        runtimeState.helpdeskLogJobPage.enterContactAlternativeNumber(String.format("0%03d %03d %04d", tel_code, tel_mid, tel_end));
    }

    @When("^the job is saved$")
    public void the_job_is_saved() throws Throwable {
        // all resource questions are defaulted to No and answering job questions if there are any
        for (String question : runtimeState.helpdeskLogJobPage.getResourceQuestions()) {
            if ( !runtimeState.helpdeskLogJobPage.isResourceQuestionAnswered(question) ) {
                runtimeState.scenario.write("Question: " + question + "\nAnswer: No");
                runtimeState.helpdeskLogJobPage.resourceQuestionSelectNo(question);
                assertFalse(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());
            }
        }
        LogJobHelper.answerJobQuestions(runtimeState, testData);

        if (runtimeState.helpdeskLogJobPage.getLocation().isEmpty()) {
            runtimeState.helpdeskLogJobPage.selectRandomLocation();
        }

        if (runtimeState.helpdeskLogJobPage.getScopeOfWork().length() < 20) {
            runtimeState.helpdeskLogJobPage.enterScopeOfWork(DataGenerator.generateRandomSentence());
        }

        if (runtimeState.helpdeskLogJobPage.isContractorToQuoteRequested()) {
            runtimeState.helpdeskLogJobPage.setRandomContractorToQuote();
            job.setDescription(runtimeState.helpdeskLogJobPage.getScopeOfWork());
        }

        outputHelper.takeScreenshots();
        if (testData.getBoolean("isQuoteRequested")) {
            runtimeState.helpdeskLogJobPage.saveRequestedQuote();
        } else {
            runtimeState.helpdeskLogJobPage.save();
        }

        if (runtimeState.helpdeskLogJobPage.isAssetNotSelectedModalDisplayed()) {
            runtimeState.helpdeskLogJobPage.selectRandomAssetNotSelectedReason();
            outputHelper.takeScreenshots();
            runtimeState.helpdeskLogJobPage.assetNotSelectedContinue();
        }

        if (testData.getBoolean("isQuoteRequested")) {
            runtimeState.helpdeskQuotesPanel = new HelpdeskQuotesPanel(getWebDriver()).get();
        }

        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
    }

    @When("^the save button is clicked$")
    public void the_save_button_is_clicked() throws Throwable {
        runtimeState.helpdeskLogJobPage.save();
        if (getWebDriver().getCurrentUrl().contains("Helpdesk#!/details/job")) {
            runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
        }
    }

    @When("^the job is not saved$")
    public void the_job_is_not_saved() throws Throwable {
        AssertJobNotCreated assertJobNotCreated = new AssertJobNotCreated(job.getDescription(), jobDao);
        assertionFactory.performAssertion(assertJobNotCreated);
    }

    @When("^the \"([^\"]*)\" job is not saved$")
    public void the_job_is_not_saved(String jobType) throws Throwable {
        AssertJobNotCreated assertJobNotCreated = new AssertJobNotCreated(jobType,job.getDescription(), jobDao);
        assertionFactory.performAssertion(assertJobNotCreated);
    }

    @When("^the save button is disabled until all mandatory fields are entered$")
    public void the_save_button_is_disabled_until_all_mandatory_fields_are_entered() throws Throwable {
        assertFalse(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());
        LogJobHelper.enterCaller(runtimeState, testData);
        assertFalse(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());
        runtimeState.helpdeskLogJobPage.selectRandomAsset();
        assertFalse(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());
        runtimeState.helpdeskLogJobPage.addJobDetails("Job created for test automation");
        assertFalse(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());
        runtimeState.helpdeskLogJobPage.selectRandomFault();
        // all resource question answers defaulted to No
        for (String question : runtimeState.helpdeskLogJobPage.getResourceQuestions()) {
            runtimeState.scenario.write("Question: " + question + "\nAnswer: No");
            runtimeState.helpdeskLogJobPage.resourceQuestionSelectNo(question);
            assertFalse(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());
        }

        //answer all mandatory job questions
        LogJobHelper.answerJobQuestions(runtimeState, testData);
        assertFalse(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());

        outputHelper.takeScreenshots();
        runtimeState.helpdeskLogJobPage.clickSameAsCaller();
        assertTrue(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());
    }

    @When("^all mandatory fields are entered$")
    public void all_mandatory_fields_are_entered() throws Throwable {
        LogJobHelper.fillInJobForm(runtimeState, testData);
    }

    @When("^the job is cancelled$")
    public void the_job_is_cancelled() throws Throwable {
        runtimeState.helpdeskLogJobPage.cancel();
        outputHelper.takeScreenshots();
    }

    @When("^the Job Contact is an existing contact$")
    public void the_Job_Contact_is_an_existing_contact() throws Throwable {

        runtimeState.helpdeskLogJobPage.clickAddJobContact();

        runtimeState.helpdeskLogJobPage.enterContactNotes(getRandomNote());

        runtimeState.helpdeskLogJobPage.selectRandomSiteContact();

        callerContact.setName(runtimeState.helpdeskLogJobPage.getSiteContacts().get(0));

        String phoneNumber = DataGenerator.generatePhoneNumber();
        runtimeState.helpdeskLogJobPage.enterContactAlternativeNumber(phoneNumber);
    }

    private String getRandomNote() throws ParseException {
        final String[] status = {"available ", "not available ", "out of office ", "busy ", "free "};
        final String[] when = {"on ", "before ", "until ", "after "};

        Random rand = new Random();
        return status[rand.nextInt(status.length)] + when[rand.nextInt(when.length)] + DateHelper.getNowDatePlusOffset(24, FULL_DATE);
    }

    @When("^an asset, description and fault type are entered$")
    public void an_asset_description_and_fault_type_entered() throws Exception {
        if (testData.getBoolean("dataset")) {
            int faultTypeId = testData.getInt("faultTypeId");
            int assetClassificationId = testData.getInt("assetClassificationId");
            Integer siteTypeId = dbHelperSites.getSiteTypeId(testData.getInt("siteId"));

            Map<String, Object> assetDetails = dbHelper.getAssetClassificationFaultDetail(assetClassificationId, faultTypeId, siteTypeId);
            String assetTypeName = assetDetails.get("AssetTypeName").toString().trim();
            String subtype = assetDetails.get("AssetSubTypeName").toString().trim();
            String subTypeClassification = assetTypeName.concat(" > " + subtype);
            String fault = assetDetails.get("FaultTypeName").toString().trim();

            testData.put("subtypeClassification", subTypeClassification);
            testData.put("fault", fault);
        }
        LogJobHelper.enterAsset(runtimeState, testData);
        if (runtimeState.helpdeskLogJobPage.getLocation().isEmpty()) {
            LogJobHelper.enterLocation(runtimeState, testData);
        }
        LogJobHelper.enterDescription(runtimeState, testData);
        LogJobHelper.enterFault(runtimeState, testData);
        LogJobHelper.answerResourceQuestions(runtimeState, testData);
    }

    @When("^caller, asset, location description, fault type and any resource or job questions are entered$")
    public void caller_asset_location_description_fault_type_and_any_resource_or_job_questions_are_entered() throws Throwable {
        LogJobHelper.enterCaller(runtimeState, testData);
        LogJobHelper.enterAsset(runtimeState, testData);
        LogJobHelper.enterLocation(runtimeState, testData);
        LogJobHelper.enterDescription(runtimeState, testData);
        LogJobHelper.enterFault(runtimeState, testData);
        LogJobHelper.answerResourceQuestions(runtimeState, testData);
        LogJobHelper.answerJobQuestions(runtimeState, testData);
    }

    private void enter_new_job_form_data(String subtypeClassification, String faultTypeName) throws InterruptedException {

        // enter asset subtype classification
        runtimeState.helpdeskLogJobPage.addSubtypeClassification(subtypeClassification);
        job.setClassification(runtimeState.helpdeskLogJobPage.getClassification().replace("\n", " > "));

        // enter location
        runtimeState.helpdeskLogJobPage.selectRandomLocation();
        job.setLocation(runtimeState.helpdeskLogJobPage.getLocation());
        job.setAssetTag(runtimeState.helpdeskLogJobPage.getAsset());

        // enter job description
        String description;
        description = "Fault discovered on " + DateHelper.dateAsString(new Date());
        testData.put("description", description);

        runtimeState.helpdeskLogJobPage.addJobDetails(description);
        job.setDescription(description);

        // enter fault
        runtimeState.helpdeskLogJobPage.selectFault(faultTypeName);
        job.setFault(faultTypeName);
        job.setPriority(runtimeState.helpdeskLogJobPage.getPriority());

        // all resource question answers defaulted to No
        for (String question : runtimeState.helpdeskLogJobPage.getResourceQuestions()) {
            runtimeState.scenario.write("Question: " + question + "\nAnswer: No");
            runtimeState.helpdeskLogJobPage.resourceQuestionSelectNo(question);
        }
    }

    @When("^the New Job form is completed for a matching asset and priority$")
    public void the_New_Job_form_is_completed_for_a_matching_asset_and_priority() throws Throwable {

        runtimeState.helpdeskLogJobPage.selectCaller("");
        callerContact.setName(runtimeState.helpdeskLogJobPage.getCaller());
        job.setCaller(callerContact);

        if (siteContractorAsset.getFaultTypeName() != null) {
            // old way - to be phased out!!!
            String subtypeClassification = siteContractorAsset.getAssetSubTypeName();
            if (siteContractorAsset.getAssetClassificationName() != null) {
                subtypeClassification = subtypeClassification + ">" + siteContractorAsset.getAssetClassificationName();
            }
            enter_new_job_form_data(subtypeClassification, siteContractorAsset.getFaultTypeName());

        } else {
            LogJobHelper.enterCaller(runtimeState, testData);
            LogJobHelper.enterAsset(runtimeState, testData);
            LogJobHelper.enterLocation(runtimeState, testData);
            LogJobHelper.enterDescription(runtimeState, testData);
            LogJobHelper.enterFault(runtimeState, testData);
            LogJobHelper.answerResourceQuestions(runtimeState, testData);
            LogJobHelper.answerJobQuestions(runtimeState, testData);
        }
    }

    @When("^the quote type is(?:| set to) \"([^\"]*)\"$")
    public void quote_type_is(String quoteType) throws Throwable {
        if (quoteType.equalsIgnoreCase("BMI") && getWebDriver().getCurrentUrl().contains("-ukrb")) {
            quoteType = "INSURANCE";
        } else {
            String aliasFundingRoute = dbHelperQuotes.getFundingRouteAlias(quoteType);
            quoteType = (aliasFundingRoute == null) ? quoteType : aliasFundingRoute;
        }
        runtimeState.helpdeskLogJobPage.selectQuoteType(quoteType);
        testData.put("isQuoteRequested", true);
    }

    @When("^a random quote type and priority are selected$")
    public void a_random_quote_type_and_priority_are_selected() throws Throwable {
        testData.put("isQuoteRequested", true);
        runtimeState.helpdeskLogJobPage.selectRandomQuoteType();
        runtimeState.helpdeskLogJobPage.selectRandomJobDetailPriority();
    }

    @When("^a non CAPEX Urgent Critical quote are selected")
    public void a_non_CAPEX_Urgent_Critical_quote_are_selected() throws Throwable {
        testData.put("isQuoteRequested", true);

        runtimeState.helpdeskLogJobPage.selectRandomQuoteType();
        String quoteType = runtimeState.helpdeskLogJobPage.getQuoteType();

        if ("CAPEX".equals(quoteType)) {
            List<String> priorities = runtimeState.helpdeskLogJobPage.getJobDetailPriorities();
            int indx = priorities.indexOf("Urgent - Critical");
            if (indx > -1) {
                priorities.remove(indx);
            }

            indx = org.apache.commons.lang3.RandomUtils.nextInt(0, priorities.size()-1);
            String priority = priorities.get(indx);

            runtimeState.helpdeskLogJobPage.selectJobDetailPriority(priority);
        } else {
            runtimeState.helpdeskLogJobPage.selectRandomJobDetailPriority();
        }

        if (runtimeState.helpdeskLogJobPage.isPotentialInsuranceQuoteQuestionDisplayed()) {
            runtimeState.helpdeskLogJobPage.selectRandomAnswerForIsPotentialInsuranceQuote();
        }
    }

    @When("^the New Job form is completed for an? \"([^\"]*)\" quote$")
    public void job_details_are_completed_for_a_quote_type(String quoteType) throws Throwable {

        the_New_Job_form_is_completed_for_a_matching_asset_and_priority();

        quote_type_is(quoteType);
        the_job_detail_priority_is_set();
    }

    @When("^the New Job form is completed for an? \"([^\"]*)\" quote with \"([^\"]*)\" priority$")
    public void job_details_are_completed_for_a_quote_type_with_priority(String quoteType, String priority) throws Throwable {

        the_New_Job_form_is_completed_for_a_matching_asset_and_priority();

        quote_type_is(quoteType);
        the_job_detail_priority_is_set(priority);
    }

    @When("^the New Job form is completed for a non CAPEX Urgent Critical quote$")
    public void job_details_are_completed_for_a_non_CAPEX_UrgentCritical_quote() throws Throwable {
        the_New_Job_form_is_completed_for_a_matching_asset_and_priority();
        a_non_CAPEX_Urgent_Critical_quote_are_selected();
    }

    @When("^an? \"([^\"]*)\" quote with \"([^\"]*)\" priority is entered$")
    public void a_quote_type_with_priority_is_entered(String quoteType, String priority) throws Throwable {
        quote_type_is(quoteType);
        the_job_detail_priority_is_set(priority);
    }

    @When("^the job detail priority is set$")
    public void the_job_detail_priority_is_set() {
        runtimeState.helpdeskLogJobPage.selectRandomJobDetailPriority();
    }

    @When("^the job detail priority is set to \"([^\"]*)\"$")
    public void the_job_detail_priority_is_set(String priority) {
        List<String> priorities = runtimeState.helpdeskLogJobPage.getJobDetailPriorities();
        if (!priorities.contains(priority)) {
            runtimeState.scenario.write(priority + " not an available option: " + priorities.toString());
            throw new PendingException(priority + " not an available option: " + priorities.toString());
        }
        runtimeState.helpdeskLogJobPage.selectJobDetailPriority(priority);
    }

    @When("^the caller is a City resource$")
    public void the_caller_is_a_city_resource() throws Throwable {

        List<String> callers = runtimeState.helpdeskLogJobPage.getCallers();

        for (String caller : callers) {
            String name = caller.split("\\(")[0].split(" - ")[0].trim();

            String resourceType = dbHelperResources.getResourceType(name);

            if (!resourceType.isEmpty()) {
                if (resourceType.equals("City Resource")) {
                    runtimeState.helpdeskLogJobPage.selectSiteContact(caller);
                    break;
                }
            }
        }

        callerContact.setName(runtimeState.helpdeskLogJobPage.getCaller());
        job.setCaller(callerContact);
    }

    @When("^a (?:non City resource|contractor) is assigned to the job$")
    public void a_non_City_resource_is_assigned_to_the_job() throws InterruptedException {

        String question = "Have you been advised to assign a specific resource to this job?";

        runtimeState.helpdeskLogJobPage.resourceQuestionSelectYes(question);

        List<Integer> contractorIds = dbHelperResources.getContractorResources(testData.getInt("siteId"));
        String contractor;
        if (contractorIds.isEmpty()) {
            contractor = runtimeState.helpdeskLogJobPage.selectRandomResourceQuestionOption(question);
        } else {
            contractor = dbHelperResources.getResourceName(contractorIds.get(0));
            logger.debug("contractor: " + contractor);
            runtimeState.helpdeskLogJobPage.enterResourceQuestionAnswer(question, contractor);
        }
        runtimeState.scenario.write("Question: " + question + "\nAnswer: Yes - " + contractor);

    }

    @When("^the New Job form is completed with details:$")
    public void the_New_Job_form_is_completed_with_details(List<HelpdeskLogJobForm> form) throws Throwable {

        if (form.get(0).getCaller() == null) {
            an_existing_Caller_is_entered();
        } else {
            caller_entered(form.get(0).getCaller());
        }

        subtype_Classification_is_entered(form.get(0).getSubtype());
        location_entered(form.get(0).getLocation());

        if (form.get(0).getDescription() == null) {
            String description = "Meltdown on " + DateHelper.dateAsString(new Date());
            runtimeState.helpdeskLogJobPage.addJobDetails(description);
            job.setDescription(description);
        } else {
            job_description_entered(form.get(0).getDescription());
        }

        fault_Type_is_entered(form.get(0).getFault());

        for (String question : runtimeState.helpdeskLogJobPage.getResourceQuestions()) {
            if ("Have you been advised to assign a specific resource to this job?".equals(question) && form.get(0).getAssignTo() != null) {
                runtimeState.helpdeskLogJobPage.resourceQuestionSelectYes(question);
                runtimeState.helpdeskLogJobPage.enterResourceQuestionAnswer(question, form.get(0).getAssignTo());
            } else {
                runtimeState.helpdeskLogJobPage.resourceQuestionSelectNo(question);
            }
        }
    }

    @When("^a search is run for a \"([^\"]*)\" site with configured job question: \"([^\"]*)\"$")
    public void a_search_is_run_for_a_site_with_configured_job_question(String siteStatus, String jobQuestion) throws Throwable {
        Integer siteId = dbHelperSites.getRandomSiteWithConfiguredJobQuestion(siteStatus, jobQuestion);
        if (siteId == null) {
            siteId = siteViewDao.getSiteByState(siteStatus).getId();
            apiHelper.createRuleJobQuestion(jobQuestion, siteId);
        }
        String siteName = dbHelperSites.getSiteName(siteId);
        runtimeState.scenario.write("Site is: " + siteName);
        testData.put("siteId", siteId);
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteName);
    }

    @When("^a job is logged$")
    public void a_job_is_logged() throws Throwable {
        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();

        LogJobHelper.fillInJobForm(runtimeState, testData);
        LogJobHelper.saveJob(runtimeState, testData, outputHelper);

        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
        testData.put("jobReference", runtimeState.helpdeskJobPage.getJobReference());
        runtimeState.scenario.write("Created job reference: " + testData.getInt("jobReference"));
    }

    @When("^a duplicate job is being logged$")
    public void a_duplicate_job_is_being_logged() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.helpdeskHomePage.closeAllTabs();

        ActiveCallerCard activeCallerCard = new ActiveCallerCard(getWebDriver());
        if (activeCallerCard.isCardDisplayed()) {
            activeCallerCard.endCall();
        }

        String siteName = testData.getString("siteName") != null ? testData.getString("siteName") : siteView.getName();
        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteName);

        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();

        if (testData.getBoolean("assignToContractor")) {
            LogJobHelper.selectCityTechCaller(runtimeState);
        } else {
            runtimeState.helpdeskLogJobPage.selectRandomCaller();
        }

        if ( ! "None".equals(testData.getString("asset")) ) {
            runtimeState.helpdeskLogJobPage.selectAsset(testData.getString("asset"));
        } else {
            runtimeState.helpdeskLogJobPage.addSubtypeClassification(testData.getString("subtypeClassification"));
        }

        if (runtimeState.helpdeskLogJobPage.getLocation().isEmpty()) {
            runtimeState.helpdeskLogJobPage.selectLocation(testData.getString("location"));
        }

        runtimeState.helpdeskLogJobPage.addJobDetails("Job created for test automation");
        runtimeState.helpdeskLogJobPage.selectFault(testData.getString("fault"));
        // all resource question answers defaulted to No

        for (String question : runtimeState.helpdeskLogJobPage.getResourceQuestions()) {

            if ("Have you been advised to assign a specific resource to this job?".equals(question) && testData.getBoolean("assignToContractor")) {
                a_non_City_resource_is_assigned_to_the_job();

            } else {
                runtimeState.scenario.write("Question: " + question + "\nAnswer: No");
                runtimeState.helpdeskLogJobPage.resourceQuestionSelectNo(question);
            }
        }

        LogJobHelper.answerJobQuestions(runtimeState, testData);
        runtimeState.helpdeskLogJobPage.clickSameAsCaller();
        outputHelper.takeScreenshots();
    }

    @When("^a duplicate job is created$")
    public void a_duplicate_job_is_created() throws Throwable {
        a_duplicate_job_is_being_logged();

        runtimeState.helpdeskLogJobPage.save();

        runtimeState.helpdeskJobPage = new HelpdeskJobPage(getWebDriver()).get();
        testData.put("jobReference", runtimeState.helpdeskJobPage.getJobReference());
        runtimeState.scenario.write("Created job reference: " + testData.getInt("jobReference"));

        runtimeState.helpdeskManageResourcesPanel = new HelpdeskManageResourcesPanel(getWebDriver()).get();
        testData.put("expectedFundingAmount", "0.00");
    }

    @When("^a new job is being logged$")
    public void a_new_job_is_being_logged() throws Throwable {

        FaultPriorityMapping data = faultPriorityMappingDao.getRandomForSite(testData.getInt("siteId"), "1,2,3");

        if (data == null) {
            throw new PendingException("no suitable test data found");
        }

        runtimeState.scenario.write("Logging job for: " + data.toString());

        runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();

        LogJobHelper.fillInJobForm(runtimeState, testData);
    }

    @When("^user selects \"([^\"]*)\" answer for the job question: \"([^\"]*)\"$")
    public void user_selects_answer_for_the_job_question(String answer, String question) throws Throwable {
        if(testData.get("questionType").equals("Drop Down List")) {
            runtimeState.helpdeskLogJobPage.selectJobQuestionDropdown(question, answer);
        } else if (testData.get("questionType").equals("Radio Buttons")) {
            if(answer.equals("Yes")) {
                runtimeState.helpdeskLogJobPage.jobQuestionSelectYes(question);
            } else {
                runtimeState.helpdeskLogJobPage.jobQuestionSelectNo(question);
            }
        }
        runtimeState.scenario.write("\'"+answer+"\'"+ "option is selected for the job question: " +"\'"+question +"\'");
    }

    @When("^any Job Questions are answered$")
    public void any_Job_Questions_are_answered() throws ParseException {
        LogJobHelper.answerJobQuestions(runtimeState, testData);
    }


    @When("^a new job is logged and assigned to a City resource with \"([^\"]*)\" phone, \"([^\"]*)\" email and \"([^\"]*)\" ipad$")
    public void a_new_job_is_logged_and_assigned_to_a_city_resource_with_phone_email_and_ipad(String phone, String email, String ipad) throws Throwable {
        jobCreationHelper.createJobResourceTypePhoneEmailIpad("City resource", phone, email, ipad);
    }

    @When("^a new job is logged and assigned to a City resource with \"([^\"]*)\" phone, \"([^\"]*)\" email and \"([^\"]*)\" ipad and \"([^\"]*)\" priority$")
    public void a_new_job_is_logged_and_assigned_to_a_city_resource_with_phone_email_and_ipad_priority(String phone, String email, String ipad, String priority) throws Throwable {
        int faultPriority = propertyHelper.getMercuryUrl().contains("uswm") ? 4 : 1; // In Walmart PE jobs (with FP = 4) will appear on Awaiting Acceptance monitor whereas in Rainbow it is P1 with FP=1
        String name = propertyHelper.getMercuryUrl().contains("uswm") ? "PE" : "P1";
        jobCreationHelper.createJobForResourceForPriority(faultPriority, name, phone, email, ipad);
    }

    @When("^a new job is logged and assigned to a Contractor with \"([^\"]*)\" phone and \"([^\"]*)\" email$")
    public void a_new_job_is_logged_and_assigned_to_a_contractor_with_phone_email(String phone, String email) throws Throwable {
        jobCreationHelper.createJobResourceTypePhoneEmailIpad("Contractor", phone, email, "no");
    }

    @When("^a new job is logged and assigned to any resource with \"([^\"]*)\" phone and \"([^\"]*)\" email$")
    public void a_new_job_is_logged_and_assigned_to_any_resource_with_phone_email(String phone, String email) throws Throwable {
        jobCreationHelper.createJobResourceTypePhoneEmailIpad("any", phone, email, "no");
    }

    @When("^the job is viewed$")
    public void the_job_is_viewed() throws Throwable {
        runtimeState.helpdeskJobPage = runtimeState.helpdeskSearchBar.searchForJob(testData.getInt("jobReference"));
        runtimeState.helpdeskManageResourcesPanel = runtimeState.helpdeskJobPage.selectManageResourcesAction();
    }

    @When("^a potential duplicate job is selected$")
    public void a_potential_duplicate_job_is_selected() {
        List<Integer> originalJobReferences = runtimeState.helpdeskLogJobPage.getDuplicateJobReferences();
        Integer originalJobReference =  originalJobReferences.get(0);
        runtimeState.scenario.write("Selecting potential duplicate job reference: " + originalJobReference);
        runtimeState.helpdeskLogJobPage.getSelectPotentialDuplicateJob(originalJobReference);
        testData.put("originalJobReference", originalJobReference);
    }

    private void logJobwithLinkedIncident() throws Throwable {
        commonSteps.the_button_is_clicked("Log a job");
        LogJobHelper.enterCaller(runtimeState, testData);
        String subType = StringUtils.substringBetween(testData.getString("assetSubTypeWithFormat"), "(", ")");
        subType = subType + " > " + StringUtils.substringBetween(testData.getString("assetSubTypeWithFormat"), "=", " (");
        if (!testData.getString("classification").equalsIgnoreCase("None")) {
            subType = subType + " > " + testData.getString("classification");
        }
        String faultType = testData.getString("faultType");
        enter_new_job_form_data(subType, faultType);
        the_Job_Contact_is_the_same_as_caller();
        LogJobHelper.answerJobQuestions(runtimeState, testData);
        testData.put("caller", runtimeState.helpdeskLogJobPage.getCaller());
        assertTrue(runtimeState.helpdeskLogJobPage.isSaveButtonEnabled());
        runtimeState.helpdeskLogJobPage.save();
        int jobId = Integer.parseInt(StringUtils.substringBetween(getWebDriver().getCurrentUrl(), "job/", "?"));
        testData.put("jobReference", dbHelperJobs.getJobReference(jobId));
    }

    @Given("^a reactive job is logged for a fault with matching linked incident criterion$")
    public void a_reactive_job_is_logged_for_a_fault_with_matching_linked_incident_criterion() throws Throwable {
        helpdeskSearchHelper.searchForSiteWithLinkedIncidentCriterion();
        logJobwithLinkedIncident();
    }

    @Given("^a reactive job is logged for a fault with matching linked incident criterion for a site having logged incidents in last 30 days$")
    public void a_reactive_job_is_logged_for_a_fault_with_matching_linked_incident_criterion_30_days() throws Throwable {
        helpdeskSearchHelper.searchForSiteHavingLoggedIncidentsInLast30_Days("Occupied");
        logJobwithLinkedIncident();
    }

    @When("^the top incident is linked to the job$")
    public void the_top_incident_is_linked_to_the_job() throws Throwable {
        commonSteps.the_button_is_clicked("Link");
        commonSteps.the_button_is_clicked("Close");
    }

    @When("^requests are made to create jobs via the Helpdesk$")
    public void requests_are_made_to_create_jobs_via_the_Helpdesk() throws Throwable {

        String siteName = dbHelperSites.getSiteName(testData.getInt("siteId"));

        loginSteps.user_with_profile_has_been_impersonated("Helpdesk Operator");

        runtimeState.helpdeskSitePage = runtimeState.helpdeskSearchBar.searchForSite(siteName);

        int maxSize = testData.getIntList("faultTypeId").size();
        for (int i = 0; i < maxSize; i++) {
            runtimeState.helpdeskLogJobPage = runtimeState.helpdeskSitePage.clickLogAJobButton();

            try {

                String subtypeClassification = testData.getArray("assetTypeName").get(i) + " > " + testData.getArray("assetSubTypeName").get(i);
                if ( ! testData.getArray("assetClassificationName").get(i).trim().isEmpty() ) {
                    subtypeClassification = subtypeClassification + " > " + testData.getArray("assetClassificationName").get(i);
                }

                testData.put("subtypeClassification", subtypeClassification);
                testData.put("fault", testData.getIntList("faultTypeName").get(i));

                if (testData.getArray("locations") == null || (testData.getArray("locations").size() - 1) < i) {
                    String location = dbHelper.getRandomLocationName(testData.getInt("siteId"));
                    testData.put("location", location);
                    testData.addToList("locations", location);
                }

                LogJobHelper.fillInJobForm(runtimeState, testData);
                LogJobHelper.saveJob(runtimeState, testData, outputHelper);

                testData.addToList("jobReferences", runtimeState.helpdeskJobPage.getJobReference());

            } catch (Exception e) {
                testData.addToList("jobReferences", 0);
            }
            runtimeState.helpdeskHomePage.closeActiveTab();
        }
    }

}

