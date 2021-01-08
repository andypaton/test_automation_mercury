package mercury.helpers;

import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.PendingException;
import mercury.database.dao.CallerDetailsDao;
import mercury.database.models.CallerDetails;
import mercury.database.models.LogJobData;
import mercury.databuilders.CallerContact;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskJobPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskNewSiteContactModal;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskQuotesPanel;
import mercury.pageobject.web.portal.JobQuestions;
import mercury.runtime.RuntimeState;

@Component
public class LogJobHelper {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private CallerDetailsDao callerDetailsDao;
    @Autowired private DbHelperResources dbHelperResources;

    private static final Logger logger = LogManager.getLogger();


    public static void fillInJobForm(RuntimeState runtimeState, TestData testData) throws Throwable {
        enterCaller(runtimeState, testData);
        enterAsset(runtimeState, testData);
        enterLocation(runtimeState, testData);
        enterDescription(runtimeState, testData);
        enterFault(runtimeState, testData);
        answerDeferralQuestions(runtimeState, testData);
        answerResourceQuestions(runtimeState, testData);
        answerJobQuestions(runtimeState, testData);
        selectJobContactSameAsCaller(runtimeState);
    }

    public static void enterCaller(RuntimeState runtimeState, TestData testData) throws Throwable {
        String caller = testData.getString("caller") == null ? "" : testData.getString("caller");
        logger.debug("entering caller: " + caller);
        caller = runtimeState.helpdeskLogJobPage.selectCaller(caller);
        if (caller == null) {
            addNewCaller(runtimeState);
        }
        testData.put("caller", runtimeState.helpdeskLogJobPage.getCaller());
        runtimeState.scenario.write("caller: " + testData.getString("caller"));
    }

    private static String addNewCaller(RuntimeState runtimeState) throws InterruptedException {
        CallerContact callerContact = new CallerContact.Builder().build();
        callerContact.setCallerType("Client");
        runtimeState.newCallerPage = runtimeState.helpdeskLogJobPage.clickAddNewCaller();
        runtimeState.newCallerPage.enterName(callerContact.getName());
        runtimeState.newCallerPage.enterJobRole(callerContact.getJobTitle());
        runtimeState.newCallerPage.enterDepartment(callerContact.getDepartment());
        runtimeState.newCallerPage.enterTelephone(callerContact.getTelephone());
        runtimeState.newCallerPage.enterExtension(callerContact.getExtension());

        runtimeState.helpdeskLogJobPage = runtimeState.newCallerPage.save();
        return callerContact.getName();
    }

    public static void enterAsset(RuntimeState runtimeState, TestData testData) {
        if (testData.getString("subtypeClassification") != null) {
            logger.debug("entering subtypeClassification: " + testData.getString("subtypeClassification"));
            runtimeState.helpdeskLogJobPage.addSubtypeClassification(testData.getString("subtypeClassification"));

            testData.put("asset", runtimeState.helpdeskLogJobPage.getAsset());
            testData.put("subtypeClassification", runtimeState.helpdeskLogJobPage.getClassification().replace("\n", " > "));
            runtimeState.scenario.write("asset: " + testData.getString("asset"));
            runtimeState.scenario.write("subtypeClassification: " + testData.getString("subtypeClassification"));
        } else {
            logger.debug("entering random asset");
            selectRandomAsset(runtimeState, testData);
        }
    }

    public static void selectRandomAsset(RuntimeState runtimeState, TestData testData) {

        if ( ! runtimeState.helpdeskLogJobPage.getAssets().isEmpty() ) {
            runtimeState.helpdeskLogJobPage.selectRandomAsset();
        }

        String classification = runtimeState.helpdeskLogJobPage.getClassification().replace("\n", " > ");

        if (classification.isEmpty()) {
            classification = runtimeState.helpdeskLogJobPage.selectRandomClassification();
        }

        testData.put("subtypeClassification", classification);
        testData.put("asset", runtimeState.helpdeskLogJobPage.getAsset());
        runtimeState.scenario.write("asset: " + testData.getString("asset"));
        runtimeState.scenario.write("subtypeClassification: " + testData.getString("subtypeClassification"));
        if ("None".equalsIgnoreCase(testData.getString("asset")) && classification.isEmpty()){
            throw new PendingException("No Asset and subtype classification found for the site");
        }
    }


    public static void enterLocation(RuntimeState runtimeState, TestData testData) {
        if (testData.getString("location") != null) {
            logger.debug("entering location: " + testData.getString("location"));
            runtimeState.helpdeskLogJobPage.selectLocation(testData.getString("location"));
        } else if (runtimeState.helpdeskLogJobPage.getLocation().isEmpty()) {
            logger.debug("entering random location");
            runtimeState.helpdeskLogJobPage.selectRandomLocation();
        }
        testData.put("location", runtimeState.helpdeskLogJobPage.getLocation());
        runtimeState.scenario.write("location: " + testData.getString("location"));
    }

    public static void enterDescription(RuntimeState runtimeState, TestData testData) {
        String description = DateHelper.dateAsString(new Date()) + ". Scenario: " + runtimeState.scenario.getName().replaceAll("\\[.*\\]", "").trim();
        logger.debug("entering description: " + description);
        runtimeState.helpdeskLogJobPage.addJobDetails(description);
        testData.put("description", description);
    }

    public static void enterFault(RuntimeState runtimeState, TestData testData) throws InterruptedException {
        if (testData.getString("fault") != null) {
            logger.debug("entering fault: " + testData.getString("fault"));
            runtimeState.helpdeskLogJobPage.selectFault(testData.getString("fault"));
        } else {
            logger.debug("entering random fault");
            runtimeState.helpdeskLogJobPage.selectRandomFault();
        }
        testData.put("fault", runtimeState.helpdeskLogJobPage.getFaultType());
        testData.put("priority", runtimeState.helpdeskLogJobPage.getPriority());
        runtimeState.scenario.write("fault: " + testData.getString("fault"));
    }

    public static void answerDeferralQuestions(RuntimeState runtimeState, TestData testData) throws Exception {

        if (runtimeState.helpdeskLogJobPage.isDeferralQuestionAsked()) {
            String deferUntil = testData.getString("deferUntil");
            if (deferUntil == null) {
                runtimeState.scenario.write("OK to defer?: Yes");
                runtimeState.helpdeskLogJobPage.setOkToDefer();
            } else {
                runtimeState.scenario.write("OK to defer?: No");
                runtimeState.helpdeskLogJobPage.setNotOkToDefer();
                String reason = runtimeState.helpdeskLogJobPage.selectRandomDeferralReason();
                runtimeState.scenario.write("Deferral reason: : " + reason);
                runtimeState.scenario.write("Deferral until: : " + deferUntil);

                switch (deferUntil) {
                case "Now" : runtimeState.helpdeskLogJobPage.deferUntilNow(); break;
                case "Today" :
                    String today = DateHelper.getToday();
                    runtimeState.helpdeskLogJobPage.deferUntil(today);
                    break;
                case "Tomorrow" :
                    String tomorrow = DateHelper.getTomorrow();
                    runtimeState.helpdeskLogJobPage.deferUntil(tomorrow);
                    break;
                case "first available date" : runtimeState.helpdeskLogJobPage.deferUntilFirstDate(); break;
                case "Later Date" : runtimeState.helpdeskLogJobPage.deferUntilLaterDate(); break;
                default: throw new Exception("Unexpected defer until: " + deferUntil);
                }
                runtimeState.helpdeskLogJobPage.enterDeferralNote(DataGenerator.generateRandomSentence());
            }
        }
    }

    public static void answerResourceQuestions(RuntimeState runtimeState, TestData testData) throws InterruptedException {
        // all resource question answers defaulted to No
        for (String question : runtimeState.helpdeskLogJobPage.getResourceQuestions()) {

            if ("Have you been advised to assign a specific resource to this job?".equals(question)) {
                if (testData.getString("resourceName") != null) {
                    runtimeState.scenario.write("Question: " + question + "\nAnswer: Yes - " + testData.getString("resourceName"));
                    runtimeState.helpdeskLogJobPage.resourceQuestionSelectYes(question);
                    runtimeState.helpdeskLogJobPage.enterResourceQuestionAnswer(question, testData.getString("resourceName"));
                } else {
                    runtimeState.scenario.write("Question: " + question + "\nAnswer: No");
                    runtimeState.helpdeskLogJobPage.resourceQuestionSelectNo(question);
                }
            } else {
                runtimeState.scenario.write("Question: " + question + "\nAnswer: No");
                runtimeState.helpdeskLogJobPage.resourceQuestionSelectNo(question);
            }
        }
    }

    public static void answerJobQuestions(RuntimeState runtimeState, TestData testData) throws ParseException {
        runtimeState.jobQuestions = new JobQuestions(getWebDriver());
        List<String> questions = runtimeState.jobQuestions.getJobQuestions();
        for (int index = 0; index < questions.size(); index++) {
            String question = questions.get(index);
            String answer = "";
            String questionTagName = runtimeState.jobQuestions.getJobQuestionTagName(question);
            switch(questionTagName){
            case "label":
                answer = "No";
                runtimeState.jobQuestions.jobQuestionSelectNo(question);
                break;
            case "textarea":
                answer = DataGenerator.randomAlphaNumericWords(1, 10);
                runtimeState.jobQuestions.jobQuestionEnterMultipleLineText(question, answer);
                break;
            case "ph-date-time":
                answer = DateHelper.getNowDatePlusOffset(24, FULL_DATE);
                runtimeState.jobQuestions.jobQuestionSelectRandomDate(question, DateHelper.getNowDatePlusOffset(24, FULL_DATE));
                break;
            case "select":
                answer = runtimeState.jobQuestions.jobQuestionSelectRandomOption(question);
                break;
            case "input":
                // TODO : this may need some additional work to determine if it is a numeric input required
                answer = RandomStringUtils.randomAlphanumeric(5, 16);
                runtimeState.jobQuestions.jobQuestionEnterText(question, answer);
                break;
            case "span":
                String attributeValue = runtimeState.jobQuestions.getJobQuestionAttribute(question, "class");
                if (attributeValue.contains("k-datepicker")) {
                    answer = DateHelper.getNowDatePlusOffset(24, FULL_DATE);
                    runtimeState.jobQuestions.jobQuestionSelectRandomDate(question, answer);
                } else if (attributeValue.contains("k-timepicker")) {
                    answer = runtimeState.jobQuestions.jobQuestionSelectRandomTime(question);
                } else if (attributeValue.contains("k-numerictextbox")) {
                    int numeric = RandomUtils.nextInt(1, 9);
                    runtimeState.jobQuestions.jobQuestionEnterNonDecimal(question, numeric);
                    answer = String.valueOf(numeric);
                }
                break;
            }
            testData.put(question, answer);
            runtimeState.scenario.write("Question: " + question);
            runtimeState.scenario.write("Answer: " + answer);

            questions = runtimeState.jobQuestions.getJobQuestions();
        }
    }

    public static void selectJobContactSameAsCaller(RuntimeState runtimeState) {
        runtimeState.helpdeskLogJobPage.clickSameAsCaller();
        runtimeState.scenario.write("Job Contact same as Caller");
    }

    public static void saveJob(RuntimeState runtimeState, TestData testData, OutputHelper outputHelper) throws IOException {
        if (runtimeState.helpdeskLogJobPage.isContractorToQuoteRequested()) {
            runtimeState.helpdeskLogJobPage.setRandomContractorToQuote();
            testData.put("description", runtimeState.helpdeskLogJobPage.getScopeOfWork());
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

    public static void captureTestData(LogJobData logJobData, TestData testData) {
        testData.put("siteId", logJobData.getSiteId());

        String subtypeClassification = logJobData.getAssetSubTypeName();
        if (logJobData.getAssetClassificationName() != null && !logJobData.getAssetClassificationName().isEmpty()) {
            subtypeClassification = subtypeClassification + " > " + logJobData.getAssetClassificationName();
        }
        if (logJobData.getAssetTypeName() != null && !logJobData.getAssetTypeName().isEmpty()) {
            subtypeClassification = logJobData.getAssetTypeName() + " > " + subtypeClassification;
        }
        testData.put("subtypeClassification", subtypeClassification);
        testData.put("fault", logJobData.getFaultTypeName());
    }

    public static String selectCityTechCaller(RuntimeState runtimeState) throws InterruptedException {
        List<String> callers = runtimeState.helpdeskLogJobPage.getCallers();
        for (String caller : callers) {
            if (caller.contains("RHVAC") || caller.contains("MST")) {
                runtimeState.helpdeskLogJobPage.selectCaller(caller);
                return caller;
            }
        }
        return null;
    }

    public void jobWithPreviousSiteContact() throws Throwable {
        runtimeState.helpdeskLogJobPage.clickAddNewJobContact();
        List<String> contacts = runtimeState.helpdeskLogJobPage.getSiteContactsFromNewSiteContactDropdown();
        runtimeState.scenario.write("Existing contacts: " + contacts.toString());
        List<CallerDetails> oldContacts = callerDetailsDao.getCallerNamesForJob(testData.getString("jobReference"));
        List<String> contactsTrimmed = new ArrayList<String>();
        for (String name : contacts) {
            contactsTrimmed.add(name.replaceAll("\\(.*?\\) ?", "").trim());
        }
        for (CallerDetails contact : oldContacts) {
            if (!contactsTrimmed.contains(contact.getName())) {
                runtimeState.helpdeskLogJobPage.selectSiteContactFromNewSiteContactDropdown(contact.getName());
                testData.put("contactName", contact.getName());
                outputHelper.takeScreenshots();
                break;
            }
        }
        if (runtimeState.helpdeskLogJobPage.getNewSiteContact().equals("")) {
            runtimeState.helpdeskLogJobPage.clickNewContactButton();

            runtimeState.helpdeskNewSiteContactModal = new HelpdeskNewSiteContactModal(getWebDriver());
            String createdContact = createNewSiteContact();
            getWebDriver().navigate().refresh();
            runtimeState.helpdeskLogJobPage.clickAddNewJobContact();
            runtimeState.helpdeskLogJobPage.selectSiteContactFromNewSiteContactDropdown(createdContact);
            outputHelper.takeScreenshots();
            testData.put("contactName", createdContact);
        }
    }

    public String createNewSiteContact() {
        runtimeState.helpdeskNewSiteContactModal.enterName(DataGenerator.generateRandomName());
        String createdContact = runtimeState.helpdeskNewSiteContactModal.getName();
        String randomResourceProfile = dbHelperResources.getRandomResourceProfile();
        runtimeState.helpdeskNewSiteContactModal.enterJobTitle(randomResourceProfile);
        runtimeState.helpdeskNewSiteContactModal.enterDepartment(DataGenerator.generateRandomDepartment());
        runtimeState.helpdeskNewSiteContactModal.enterTelephone(DataGenerator.generatePhoneNumber());
        outputHelper.takeScreenshots();
        runtimeState.helpdeskNewSiteContactModal.clickButton("Add");
        return createdContact;
    }

    public void jobWithNoPreviousSiteContact() throws Throwable {
        runtimeState.helpdeskLogJobPage.clickAddJobContact();
        List<String> contacts = runtimeState.helpdeskLogJobPage.getSiteContacts();
        runtimeState.scenario.write("Existing contacts: " + contacts.toString());
        int index = contacts.size() -1;
        String oldContact = runtimeState.helpdeskLogJobPage.getCaller();
        runtimeState.helpdeskLogJobPage.selectRandomSiteContact();
        String newContact = runtimeState.helpdeskLogJobPage.getSiteContact();
        int cnt = 0;
        while (oldContact.equalsIgnoreCase(newContact) && cnt < index) {
            runtimeState.helpdeskLogJobPage.selectRandomSiteContact();
            newContact = runtimeState.helpdeskLogJobPage.getSiteContact();
            cnt++;
        }
        outputHelper.takeScreenshots();
        runtimeState.scenario.write("New contact: " + newContact);
        testData.put("contactName", newContact);
    }
}
