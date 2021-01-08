package mercury.pageobject.web.helpdesk.jobs;

import static mercury.helpers.POHelper.scrollTo;
import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.caller.EditCallerModal;
import mercury.pageobject.web.helpdesk.caller.NewCallerPage;

public class HelpdeskLogJobPage extends Base_Page<HelpdeskLogJobPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_CSS = "#main-panel > div.tab-content > div.tab-pane.active";
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String LOG_A_JOB_CSS = ACTIVE_WORKSPACE_CSS + " div.log-a-job-workspace";
    private static final String LOG_A_JOB_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'log-a-job-workspace')]";

    // HEADERS
    private static final String HEADLINE_CSS = LOG_A_JOB_CSS + " div.view-header__headline";
    private static final String SUBHEAD_CSS = LOG_A_JOB_CSS + " div.view-header__subhead";

    // BUTTON BAR
    private static final String BUTTONS_BAR_CSS = LOG_A_JOB_CSS + " div.view-button-bar__buttons-container";
    private static final String BUTTON_XPATH = "//button[text() = '%s']";
    private static final String REQUEST_QUOTE_XPATH = LOG_A_JOB_XPATH +"//button/span[contains(text(), 'Request Quote')]";
    private static final String LINKED_JOBS_CSS = LOG_A_JOB_CSS + " a.btn.btn-primary[title='Linked Jobs']";
    private static final String LINKED_INCIDENTS_CSS = LOG_A_JOB_CSS + " a.btn.btn-primary[title='Linked Incidents']";
    private static final String ADD_NOTES_CSS = LOG_A_JOB_CSS + " a.btn.btn-primary[title='Add Notes']";
    private static final String ADD_ATTACHMENT_CSS = LOG_A_JOB_CSS + " a.btn.btn-primary[title='Add Attachment']";
    private static final String ADD_ATTACHMENT_BADGE_CSS = ADD_ATTACHMENT_CSS + " > div.badge";
    private static final String ADD_NOTES_BUTTON_CSS = LOG_A_JOB_CSS + " div.add-note-button-container > button";
    private static final String ADD_NOTES_BUTTON_BADGE_CSS = LOG_A_JOB_CSS  + " a.btn.btn-primary[title='Add Notes'] > div.badge";

    // CONTENT
    private static final String CALLER_LISTBOX_CSS = LOG_A_JOB_CSS + " span[aria-owns='callerSelect_listbox']";
    private static final String CALLER_OPTIONS_XPATH = "(//ul[@id='callerSelect_listbox'])[1]/li";
    private static final String ADD_NEW_CALLER_XPATH = LOG_A_JOB_XPATH + "//button[@title='Add New Caller']";

    private static final String PHONE_NO_CSS = LOG_A_JOB_CSS + " input[name='PhoneNo']";
    private static final String EXTENSION_CSS = LOG_A_JOB_CSS + " input[name='Extension']";
    private static final String CALLER_TYPE_CSS = LOG_A_JOB_CSS + " input[name='CallerType']";

    private static final String SUBTYPE_CLASSIFICATION_XPATH = LOG_A_JOB_XPATH + "//ph-core-details-classification//span[contains(@class, 'k-input')]";
    private static final String SUBTYPE_CLASSIFICATION_SELECTED_VALUE_XPATH = LOG_A_JOB_XPATH + "//ph-combined-lookup[@name='classification']";
    private static final String SUBTYPE_CLASSIFICATION_DROPDOWN_CSS = LOG_A_JOB_CSS + " .combined-lookup__dropdown>ul";

    private static final String ASSET_DROPDOWN_XPATH = LOG_A_JOB_XPATH + "//label[@for='Asset']/..//span[contains(@class, 'k-input')]";

    private static final String LOCATION_DROPDOWN_XPATH = LOG_A_JOB_XPATH + "//label[contains(text(), 'Location')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String LOCATION_LISTBOX_CSS = LOG_A_JOB_CSS + " span[aria-owns='Locations_listbox']";
    private static final String LOCATION_SELECTED_VALUE_CSS = LOG_A_JOB_CSS + " span[aria-owns='Locations_listbox'] .k-input";
    private static final String LOCATION_LISTBOX_SEARCH_XPATH = "//div[@id='Locations-list' and contains(@style, 'display: block')]/span/input[@aria-owns='Locations_listbox']";
    private static final String LOCATION_LISTBOX_SELECTION_XPATH = "//ul[@aria-hidden='false']/li[contains(text(), '%s')]";

    private static final String ERROR_CSS = LOG_A_JOB_CSS + " label.error";

    private static final String CONTRACTOR_TO_QUOTE_XPATH = LOG_A_JOB_XPATH + "//label[@for='ContractorToQuoteId']";
    private static final String CONTRACTOR_TO_QUOTE_VALUE_XPATH = CONTRACTOR_TO_QUOTE_XPATH + "/..//span[contains(@class, 'k-input')]";
    private static final String CONTRACTOR_TO_QUOTE_NONE_XPATH = CONTRACTOR_TO_QUOTE_XPATH + "/..//span[@class='k-input' and text()='None']";
    private static final String CONTRACTOR_TO_QUOTE_DROPDOWN_XPATH = CONTRACTOR_TO_QUOTE_XPATH + "/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ERROR_XPATH =  "/following-sibling::label[@class='error']";

    // JOB DETAILS
    private static final String JOB_DESCRIPTION_CSS = LOG_A_JOB_CSS + " #Description";
    private static final String QUOTE_TYPE_XPATH = LOG_A_JOB_XPATH + "//label[contains(text(), 'Please select Quote Type')]/following-sibling::div";
    private static final String QUOTE_TYPE_LABELS_XPATH = QUOTE_TYPE_XPATH + "//label";
    private static final String RANDOM_QUOTE_TYPE_LABEL_XPATH = QUOTE_TYPE_XPATH + "//label[%s]";
    private static final String QUOTE_TYPE_LABEL_XPATH = QUOTE_TYPE_XPATH + "//label[contains(text(), '%s')]";
    private static final String SELECTED_QUOTE_TYPE_LABEL_XPATH = QUOTE_TYPE_XPATH + "//input[contains(@class, 'ng-valid-parse')]/following-sibling::label[1]";
    private static final String JOB_DETAIL_PRIORITY_DROPDOWN_XPATH = LOG_A_JOB_XPATH + "//span[@aria-owns='quotePriorityId_listbox']//span[contains(@class, 'k-i-arrow-s')]";
    private static final String POTENTIAL_INSURANCE_QUOTE_XPATH = LOG_A_JOB_XPATH + "//label[contains(text(), 'Potential Insurance Quote?')]/..//div[contains(@class, 'yesNoRadio')]//input";
    private static final String POTENTIAL_INSURANCE_QUOTE_LABELS_XPATH = LOG_A_JOB_XPATH + "//div[contains(@class, 'yesNoRadio')]//label[contains(@for, '%s')]";

    // FAULT DETAILS
    private static final String FAULT_DROPDOWN_XPATH = LOG_A_JOB_XPATH + "//label[contains(text(), 'Fault Type')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String FAULT_TYPE_SELECTED_VALUE_XPATH = LOG_A_JOB_XPATH + "//label[@for='FaultType']/following-sibling::span//span[contains(@class, 'k-input')]";
    private static final String PRIORITY_CSS = LOG_A_JOB_CSS + " input[name='Priority']";

    // DEFERRAL QUESTIONS
    private static final String DEFERRAL_SECTION_XPATH = LOG_A_JOB_XPATH + "//ph-job-deferral[contains(@aria-hidden, 'false')]";
    private static final String OK_TO_DEFER_XPATH = DEFERRAL_SECTION_XPATH + "//input[@id='Yes']";
    private static final String NOT_OK_TO_DEFER_XPATH = DEFERRAL_SECTION_XPATH + "//input[@id='No']";
    private static final String DEFERRAL_REASON_XPATH = DEFERRAL_SECTION_XPATH + "//label[contains(text(), 'Reason')]/following-sibling::span[contains(@class, 'k-dropdown')]";
    private static final String DEFER_UNTIL_NOW_XPATH = DEFERRAL_SECTION_XPATH + "//input[@id='AlternativeDeferralTimeNow']";
    private static final String DEFER_UNTIL_FIRST_DATE_XPATH = DEFERRAL_SECTION_XPATH + "//input[@id='AlternativeDeferralTime0']";
    private static final String DEFER_UNTIL_SECOND_DATE_XPATH = DEFERRAL_SECTION_XPATH + "//input[@id='AlternativeDeferralTime1']";
    private static final String DEFER_UNTIL_XPATH = DEFERRAL_SECTION_XPATH + "//label[contains(@class, 'job-deferral__radio') and contains(text(), '%s')]";
    private static final String DEFER_UNTIL_LATER_DATE_XPATH = DEFERRAL_SECTION_XPATH + "//input[@id='AlternativeDeferralTimeLater']";
    private static final String DEFERRAL_NOTE_XPATH = DEFERRAL_SECTION_XPATH + "//textarea[@name='deferralNote']";

    // RESOURCE QUESTIONS
    private static final String RESOURCE_QUESTIONS_XPATH = LOG_A_JOB_XPATH + "//div[@ng-if='faultCtrl.showResourceQuestion']//label[@class='control-label']";
    private static final String RESOURCE_QUESTION_XPATH = "//label[contains(text(),'%s')]";
    private static final String RESOURCE_QUESTION_YES_XPATH = RESOURCE_QUESTION_XPATH + "/..//label[text()='Yes']";
    private static final String RESOURCE_QUESTION_NO_XPATH = RESOURCE_QUESTION_XPATH + "/..//label[text()='No']";
    private static final String RESOURCE_QUESTION_DROPDOWN_ARROW_XPATH = RESOURCE_QUESTION_XPATH + "/../..//*[@ng-model='faultCtrl.jobmodel.specificResourceRequestId']//span[contains(@class, 'k-i-arrow-s')]";
    private static final String RESOURCE_QUESTION_ANSWERED_XPATH = RESOURCE_QUESTION_XPATH + "/..//input[@aria-invalid='true']";
    private static final String ADVISED_RESOURCE = "//label[contains(text(), 'Have you been advised to assign a specific resource to this job?')]/../../../div[@class = 'row']//span[contains(@class, 'k-dropdown')]//span[contains(@class, 'k-input')]";

    // JOB QUESTIONS
    private static final String JOB_QUESTIONS_XPATH = LOG_A_JOB_XPATH + "//div[@ng-if='jl.showJobQuestions']//label[contains(@ng-class, 'question.isRequired')]";
    private static final String JOB_QUESTION_XPATH = LOG_A_JOB_XPATH + "//div[@ng-if='jl.showJobQuestions']//label[contains(@ng-class, 'question.isRequired') and contains(text(),'%s')]";
    private static final String JOB_QUESTION_TYPE_XPATH = JOB_QUESTION_XPATH + "/../div/following-sibling::*";
    private static final String JOB_QUESTION_INPUT_XPATH = JOB_QUESTION_XPATH + "/..//input[contains(@ng-if, 'ctrl.questionTypes')]";
    private static final String JOB_QUESTION_NONDECIMAL_XPATH = JOB_QUESTION_XPATH + "/..//span/input";
    private static final String JOB_QUESTION_MULTIPLELINETEXT_XPATH = JOB_QUESTION_XPATH + "/..//textarea";
    private static final String JOB_QUESTION_DROPDOWN_XPATH = JOB_QUESTION_XPATH + "/..//select";
    private static final String JOB_QUESTION_DROPDOWN_OPTION_XPATH = JOB_QUESTION_DROPDOWN_XPATH + "/option[contains(text(), '%s')]";
    private static final String JOB_QUESTION_YES_XPATH = JOB_QUESTION_XPATH + "/..//label[contains(text(), 'Yes')]";
    private static final String JOB_QUESTION_NO_XPATH = JOB_QUESTION_XPATH + "/..//label[contains(text(), 'No')]";
    private static final String JOB_QUESTION_CALENDAR_XPATH = JOB_QUESTION_XPATH + "/.." + DROPDOWN_CALENDAR_XPATH;
    private static final String JOB_QUESTION_CALENDAR_OPTION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]//a[@title='%s']";
    private static final String JOB_QUESTION_CLOCK_XPATH = JOB_QUESTION_XPATH + "/.." + DROPDOWN_CLOCK_XPATH;

    // JOB CONTACTS
    private static final String JOB_CONTACTS_CSS = LOG_A_JOB_CSS + " div[name='Job Contacts']";
    private static final String SITE_CONTACT_CSS = JOB_CONTACTS_CSS + " span[aria-owns='callerSelect_listbox']";
    private static final String SITE_CONTACT_NEW_XPATH = "(//div[contains(@class, 'log-a-job-workspace')]//select[@id='callerSelect'])[last()]/option";
    private static final String JOB_CONTACTS_XPATH = LOG_A_JOB_XPATH + "//div[@name='Job Contacts']";
    private static final String SAME_AS_CALLER_CSS = LOG_A_JOB_CSS + " button[name='JobContactSameAsCaller']";
    private static final String ADD_JOB_CONTACT_XPATH = JOB_CONTACTS_XPATH + "//button[contains(text(), 'Add job contact')]";
    private static final String ADD_NEW_JOB_CONTACT_XPATH = JOB_CONTACTS_XPATH + "//button[contains(@class, 'btn-primary') and contains(@class, 'btn-compact')]/i";
    private static final String SITE_CONTACT_DROPDOWN_XPATH = LOG_A_JOB_XPATH + "//label[contains(text(), 'Site Contact')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String SITE_CONTACT_XPATH = LOG_A_JOB_XPATH + "//label[contains(text(), 'Site Contact')]/..//select[contains(@class, 'js-contact-name')]/option";
    private static final String STORED_SITE_CONTACT_XPATH = LOG_A_JOB_XPATH + "//label[contains(text(), 'Site Contact')]/..//span[contains(@class, 'k-input')]";
    private static final String NEW_SITE_CONTACT_XPATH = "(//div[contains(@class, 'log-a-job-workspace')]//select[@id='callerSelect'])[last()]/option";
    private static final String NEW_SITE_CONTACT_DROPDOWN_XPATH = "(//div[contains(@class, 'log-a-job-workspace')]//label[contains(text(), 'Site Contact')])[last()]/following-sibling::span//span[@class='k-select']";
    private static final String ALTERNATIVE_NUMBER_CSS = JOB_CONTACTS_CSS + " #alternativeNumber";
    private static final String NOTES_CSS = JOB_CONTACTS_CSS + " textarea[ng-model='vmJobContact.jobContact.notes']";
    private static final String ALT_NUMBER_XPATH = JOB_CONTACTS_XPATH + "//div[@ng-if='vmJobContact.jobContact.alternativeNumber' and contains(text(), '%s')]";
    private static final String ACTIVE_CONTACT_XPATH = JOB_CONTACTS_XPATH + "//span[contains(@class, 'js-contact-name')]//span[normalize-space()='%s']/ancestor::div[contains(@class, 'job-contact')]//input[@type = 'checkbox']";
    private static final String ACTIVE_CONTACT_CLICKABLE_XPATH = JOB_CONTACTS_XPATH + "//span[contains(@class, 'js-contact-name')]//span[contains(text(), '%s')]/ancestor::div[contains(@class, 'job-contact')]//div[@class='pull-right']";
    private static final String EDIT_JOB_NEW_CONTACT_XPATH = JOB_CONTACTS_XPATH + "//div[text()='New Contact']/following-sibling::button";
    private static final String EDIT_JOB_JOB_ROLE_XPATH = JOB_CONTACTS_XPATH + "//div[@class='job-contact__info']//div[contains(text(),'Job Role')]/following-sibling::div[1]";
    private static final String EDIT_JOB_TEL_XPATH = JOB_CONTACTS_XPATH + "//div[@class='job-contact__info']//div[contains(text(),'Tel')]/following-sibling::div[1]";


    // DUPLICATE JOBS
    private static final String DUPLICATE_JOBS_XPATH = ACTIVE_WORKSPACE_XPATH + "//job-potential-duplicate//div[@class = 'card job inline-card']";
    private static final String DUPLICATE_JOB_REFERENCE_XPATH = "//div[contains(@class, 'card-header__headline')]";
    private static final String DUPLICATE_JOB_CARD_XPATH = DUPLICATE_JOB_REFERENCE_XPATH +"//*[contains(text(), '%d')]/ancestor::div[@class = 'card job inline-card']";
    private static final String DUPLICATE_SITE_NAME_XPATH = "//div[contains(@class, 'card-header__subhead')]";
    private static final String DUPLICATE_STATUS_XPATH = "//div[contains(@class, 'card-subheader__left')]";
    private static final String DUPLICATE_CREATED_XPATH = "//div[contains(@class, 'card-subheader__right')]";
    private static final String DUPLICATE_CONTENT_XPATH = "//div[contains(@class, 'card-content')]//div[text()='%s']/following-sibling::div[@class='col-md-7']";

    // BUTTON ACTIONS
    private static final String BUTTONS_XPATH = LOG_A_JOB_XPATH + "//div[contains(@class,'form-overlay-buttons')]";
    private static final String CANCEL_XPATH = BUTTONS_XPATH + "//button[text() = 'Cancel']";
    private static final String SAVE_XPATH = BUTTONS_XPATH + "//button[contains(.,'Save')]";
    private static final String EDIT_CALLER_XPATH = LOG_A_JOB_XPATH + "//button[@title = 'Edit Caller']";

    // ALERTS
    private static final String CAPEX_URGENT_CRITICAL_ALERT_XPATH = "//div[contains(@class, 'showSweetAlert') and contains(@class, 'visible') and contains(@class, 'hd-capex-urgentcritical')]";

    // MODAL
    private static final String ASSET_NOT_SELECTED_MODAL_XPATH = "//div[@class='title' and contains(text(), 'Asset not selected')]/ancestor::body[@class='modal-open']";
    private static final String ASSET_NOT_SELECTED_REASON_XPATH = ASSET_NOT_SELECTED_MODAL_XPATH + "//span[@class='k-input' and contains(text(), 'Please choose a reason ...')]";
    private static final String ASSET_NOT_SELECTED_CONTINUE_XPATH = ASSET_NOT_SELECTED_MODAL_XPATH + "//button[contains(text(), 'Continue')]";

    //CONFIRM JOB CHANGES MODAL
    private static final String CONFIRM_JOB_CHANGES_MODAL_XPATH = "//body[@class='modal-open']//div[@modal-render='true']//div[contains(@class, 'job-edit__reason-modal')]/..";

    @FindBy(css = LOG_A_JOB_CSS)
    private WebElement page;

    @FindBy(css = HEADLINE_CSS)
    private WebElement headline;

    @FindBy(css = SUBHEAD_CSS)
    private WebElement subHeadline;

    @FindBy(css = BUTTONS_BAR_CSS)
    private WebElement buttonBar;

    @FindBy(css = JOB_DESCRIPTION_CSS)
    private WebElement jobDescription;

    @FindBy(css = JOB_CONTACTS_CSS)
    private WebElement jobContacts;

    @FindBy(css = ALTERNATIVE_NUMBER_CSS)
    private WebElement alternativeNumber;

    @FindBy(css = NOTES_CSS)
    private WebElement notes;

    @FindBy(xpath = ALT_NUMBER_XPATH)
    private WebElement altNumber;

    @FindBy(xpath = REQUEST_QUOTE_XPATH)
    private WebElement requestQuoteButton;

    @FindBy(css = LINKED_JOBS_CSS)
    private WebElement linkedJobs;

    @FindBy(css = LINKED_INCIDENTS_CSS)
    private WebElement linkedIncidents;

    @FindBy(css = ADD_NOTES_CSS)
    private WebElement addNotes;

    @FindBy(css = ADD_ATTACHMENT_CSS)
    private WebElement addAttachment;

    @FindBy(xpath = QUOTE_TYPE_LABELS_XPATH)
    private List<WebElement> quoteTypes;

    @FindBy(xpath = SELECTED_QUOTE_TYPE_LABEL_XPATH)
    private WebElement selectedQuoteType;

    @FindBy(xpath = JOB_DETAIL_PRIORITY_DROPDOWN_XPATH)
    private WebElement jobDetailPriority;

    @FindBy(css = CALLER_LISTBOX_CSS)
    private WebElement callerListbox;

    @FindBy(css = PHONE_NO_CSS)
    private WebElement phoneNo;

    @FindBy(css = EXTENSION_CSS)
    private WebElement extension;

    @FindBy(css = CALLER_TYPE_CSS)
    private WebElement callerType;

    @FindBy(xpath = ADD_NEW_CALLER_XPATH)
    private WebElement addNewCaller;

    @FindBy(xpath = SUBTYPE_CLASSIFICATION_XPATH)
    private WebElement subtypeClassification;

    @FindBy(css = SUBTYPE_CLASSIFICATION_DROPDOWN_CSS)
    private WebElement subtypeClassificationDropdown;

    @FindBy(xpath = SUBTYPE_CLASSIFICATION_SELECTED_VALUE_XPATH)
    private WebElement subtypeClassificationSelectedValue;

    @FindBy(xpath = ASSET_DROPDOWN_XPATH)
    private WebElement assetDropdown;

    @FindBy(css = LOCATION_LISTBOX_CSS)
    private WebElement locationListbox;

    @FindBy(xpath = LOCATION_DROPDOWN_XPATH)
    private WebElement locationDropdown;

    @FindBy(css = LOCATION_SELECTED_VALUE_CSS)
    private WebElement locationSelectedValue;

    @FindBy(xpath = FAULT_DROPDOWN_XPATH)
    private WebElement faultTypeDropdown;

    @FindBy(xpath = FAULT_TYPE_SELECTED_VALUE_XPATH)
    private WebElement faultTypeSelectedValue;

    @FindBy(css = SAME_AS_CALLER_CSS)
    private WebElement sameAsCaller;

    @FindBy(xpath = ADD_JOB_CONTACT_XPATH)
    private WebElement addJobContact;

    @FindBy(xpath = ADD_NEW_JOB_CONTACT_XPATH)
    private WebElement addNewJobContact;

    @FindBy(xpath = SITE_CONTACT_DROPDOWN_XPATH)
    private WebElement siteContactDropdown;

    @FindBy(xpath = NEW_SITE_CONTACT_DROPDOWN_XPATH)
    private WebElement siteContactDropdownNew;

    @FindBy(xpath = SITE_CONTACT_DROPDOWN_XPATH)
    private List<WebElement> siteContactsDropdown;

    @FindBy(xpath = STORED_SITE_CONTACT_XPATH)
    private List<WebElement> storedSiteContact;

    @FindBy(xpath = SITE_CONTACT_XPATH)
    private List<WebElement> siteContacts;

    @FindBy(xpath = NEW_SITE_CONTACT_XPATH)
    private List<WebElement> siteContactsNew;

    @FindBy(xpath = EDIT_JOB_NEW_CONTACT_XPATH)
    private WebElement editJobNewContactButton;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancel;

    @FindBy(xpath = SAVE_XPATH)
    private WebElement save;

    @FindBy(xpath = EDIT_CALLER_XPATH)
    private WebElement editCaller;

    @FindBy(css = ERROR_CSS)
    private WebElement error;

    @FindBy(xpath = CONTRACTOR_TO_QUOTE_XPATH)
    private WebElement contractorToQuote;

    @FindBy(xpath = CONTRACTOR_TO_QUOTE_DROPDOWN_XPATH)
    private WebElement contractorToQuoteDropdown;

    @FindBy(xpath = CONTRACTOR_TO_QUOTE_VALUE_XPATH)
    private WebElement contractorToQuoteValue;

    @FindBy(xpath = ASSET_NOT_SELECTED_MODAL_XPATH)
    private WebElement assetNotSelectedModal;

    @FindBy(xpath = ASSET_NOT_SELECTED_REASON_XPATH)
    private WebElement assetNotSelectedReason;

    @FindBy(xpath = ASSET_NOT_SELECTED_CONTINUE_XPATH)
    private WebElement assetNotSelectedContinue;

    @FindBy(xpath = OK_TO_DEFER_XPATH)
    private WebElement okToDefer;

    @FindBy(xpath = NOT_OK_TO_DEFER_XPATH)
    private WebElement notOkToDefer;

    @FindBy(xpath = DEFERRAL_REASON_XPATH)
    private WebElement deferralReason;

    @FindBy(xpath = DEFER_UNTIL_NOW_XPATH)
    private WebElement deferUntilNow;

    @FindBy(xpath = DEFER_UNTIL_FIRST_DATE_XPATH)
    private WebElement deferUntilFirstDate;

    @FindBy(xpath = DEFER_UNTIL_SECOND_DATE_XPATH)
    private WebElement deferUntilSecondDate;

    @FindBy(xpath = DEFER_UNTIL_LATER_DATE_XPATH)
    private WebElement deferUntilLaterDate;

    @FindBy(xpath = DEFERRAL_NOTE_XPATH)
    private WebElement deferralNote;

    @FindBy(css = ADD_NOTES_BUTTON_CSS)
    private WebElement addNotesButton;

    @FindBy(css = ADD_NOTES_BUTTON_BADGE_CSS)
    private WebElement addNotesButtonBadge;

    @FindBy(css = ADD_ATTACHMENT_BADGE_CSS)
    private WebElement addAttachmentButtonBadge;

    @FindBy(xpath = EDIT_JOB_JOB_ROLE_XPATH)
    private WebElement siteContactJobRole;

    @FindBy(xpath = EDIT_JOB_TEL_XPATH)
    private WebElement siteContactTelephone;

    @FindBy(xpath = ADVISED_RESOURCE)
    private WebElement advisedResource;

    @FindBy(css = SITE_CONTACT_CSS)
    private WebElement siteContact;

    @FindBy(xpath = SITE_CONTACT_NEW_XPATH)
    private WebElement siteContactNew;

    public HelpdeskLogJobPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(LOG_A_JOB_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isPageLoaded() {
        try {
            return driver.findElement(By.xpath(LOG_A_JOB_XPATH)).isDisplayed();
        } catch(Exception e) {
            return false;
        }

    }

    private WebElement getButton(String button) {
        return buttonBar.findElement(By.xpath(String.format(BUTTON_XPATH, button)));
    }

    public String getHeadline() {
        return headline.getText();
    }

    public String getSubHeadline() {
        return subHeadline.getText();
    }

    public boolean isButtonDisplayed(String button) {
        return getButton(button).isDisplayed();
    }

    public boolean isButtonEnabled(String button) {
        return getButton(button).isEnabled();
    }

    public boolean isButtonDisabled(String button) {
        return "true".equals(getButton(button).getAttribute("disabled"));
    }

    public boolean isSaveButtonEnabled() {
        String disabled = save.getAttribute("disabled");
        return disabled == null || !"true".equals(disabled);
    }

    public void clickRequestQuote() {
        POHelper.clickJavascript(requestQuoteButton);
    }

    public HelpdeskLinkedJobsModal clickLinkedJob() {
        POHelper.scrollToElement(linkedJobs);
        linkedJobs.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLinkedJobsModal.class).get();
    }

    public void clickLinkedIncidents() {
        linkedIncidents.click();
        waitForAngularRequestsToFinish();
    }

    public void clickAddNotes() {
        POHelper.scrollToElement(addNotes);
        addNotes.click();
        waitForAngularRequestsToFinish();
    }

    public void clickAddAttachment() {
        addAttachment.click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskLogJobPage addJobDetails(String details) {
        jobDescription.clear();
        jobDescription.sendKeys(details);
        waitForAngularRequestsToFinish();
        return this;
    }

    public HelpdeskLogJobPage enterScopeOfWork(String scopeOfWorks) {
        jobDescription.sendKeys(scopeOfWorks);
        return this;
    }

    public String getScopeOfWork() {
        return jobDescription.getAttribute("value");
    }

    public HelpdeskLogJobPage selectQuoteType(String quoteType) {
        WebElement we = driver.findElement(By.xpath(String.format(QUOTE_TYPE_LABEL_XPATH, quoteType)));
        POHelper.scrollToElement(we);
        we.click();
        waitForAngularRequestsToFinish();
        return this;
    }

    public HelpdeskLogJobPage selectRandomQuoteType(){
        int randomNumber = RandomUtils.nextInt(1, quoteTypes.size());
        WebElement we = driver.findElement(By.xpath(String.format(RANDOM_QUOTE_TYPE_LABEL_XPATH, randomNumber)));
        POHelper.scrollToElement(we);
        we.click();
        waitForAngularRequestsToFinish();
        return this;
    }

    public String getQuoteType(){
        return selectedQuoteType.getText();
    }

    public HelpdeskLogJobPage addSubtypeClassification(String subtype) {
        checkForAndConfirmAlert();
        subtypeClassification.click();
        waitForAngularRequestsToFinish();
        checkForAndConfirmAlert();
        searchAndSelectExactVisibleDropdownOption(subtype);
        checkForAndConfirmAlert();
        return this;
    }

    public HelpdeskLogJobPage selectJobDetailPriority(String priority){
        jobDetailPriority.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(priority);
        return this;
    }

    public void waitForContractorToQuoteToBeSet() {
        waitUntilElementNotDisplayed(By.xpath(CONTRACTOR_TO_QUOTE_NONE_XPATH));
    }

    public HelpdeskLogJobPage selectRandomJobDetailPriority(){
        jobDetailPriority.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
        return this;
    }

    public List<String> getJobDetailPriorities(){
        jobDetailPriority.click();
        waitForAngularRequestsToFinish();
        List<String> priorities =  getVisibleDropdownOptions();
        jobDetailPriority.click();
        waitForAngularRequestsToFinish();
        return priorities;
    }

    public String getClassification() {
        return subtypeClassification.getText();
    }

    public NewCallerPage clickAddNewCaller() {
        POHelper.scrollToElement(addNewCaller);
        addNewCaller.click();
        waitForAngularRequestsToFinish();
        return new NewCallerPage(driver).get();
    }

    public String selectRandomCaller() {
        callerListbox.click();
        waitForAngularRequestsToFinish();
        String searchStr = selectRandomVisibleDropdownOption();
        if (searchStr == null) {
            callerListbox.click();
            waitForAngularRequestsToFinish();
        }
        return searchStr;
    }

    public String selectCaller(String searchStr) throws InterruptedException {
        if (searchStr.isEmpty()) {
            return selectRandomCaller();
        } else {
            callerListbox.click();
            waitForAngularRequestsToFinish();
            searchAndSelectVisibleDropdownOption(searchStr);
            return searchStr;
        }
    }

    public String getCaller() {
        return callerListbox.getText().replace("\nselect", "");
    }

    public String getSiteContact() {
        return siteContact.getText().replace("\nselect", "");
    }

    public String getNewSiteContact() {
        return siteContactNew.getText().replace("\nselect", "");
    }

    public String getPhoneNo() {
        return phoneNo.getAttribute("value");
    }

    public String getExtension() {
        return extension.getAttribute("value");
    }

    public String getCallerType() {
        return callerType.getAttribute("value");
    }

    public String getAsset() {
        return assetDropdown.getText();
    }

    public void selectAsset(String asset) {
        assetDropdown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(asset);
    }

    public void removeAsset(String asset) {
        assetDropdown.click();
        waitForAngularRequestsToFinish();
        checkForAndConfirmAlert();
        removeVisibleDropdownOption(asset);
        waitForAngularRequestsToFinish();
        checkForAndConfirmAlert();
    }

    public void selectLocation(String location) {
        locationDropdown.click();
        waitForAngularRequestsToFinish();
        WebElement locationListboxSearch = waitForElement(By.xpath(LOCATION_LISTBOX_SEARCH_XPATH), ELEMENT_IS_VISIBLE);
        locationListboxSearch.sendKeys(location);

        driver.findElement(By.xpath(String.format(LOCATION_LISTBOX_SELECTION_XPATH, location))).click();
    }

    public String getLocation() {
        return locationSelectedValue.getText();
    }

    public void selectFault(String fault) throws InterruptedException {
        faultTypeDropdown.click();
        waitForAngularRequestsToFinish();
        checkForAndConfirmAlert();
        selectVisibleDropdownOption(fault);
        waitForAngularRequestsToFinish();
        checkForAndConfirmAlert();
    }

    public String getFaultType() {
        return faultTypeSelectedValue.getText();
    }

    public List<String> getFaultTypes() {
        faultTypeDropdown.click();
        waitForAngularRequestsToFinish();
        List<String> faultTypes = getVisibleDropdownOptions();
        faultTypeDropdown.click();
        waitForAngularRequestsToFinish();
        return faultTypes;
    }

    public String getPriority() {
        return driver.findElement(By.cssSelector(PRIORITY_CSS)).getAttribute("value");
    }

    public void resourceQuestionSelectYes(String question) {
        WebElement yes = waitForElement(By.xpath(String.format(RESOURCE_QUESTION_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        yes.click();
        waitForAngularRequestsToFinish();
    }

    public void resourceQuestionSelectNo(String question) {
        WebElement no = waitForElement(By.xpath(String.format(RESOURCE_QUESTION_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        no.click();
        waitForAngularRequestsToFinish();
    }

    public void selectJobQuestionDropdown(String question, String answer) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_DROPDOWN_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        jobQuestion.click();
        WebElement jobQuestionAnswer = waitForElement(By.xpath(String.format(JOB_QUESTION_DROPDOWN_OPTION_XPATH, question, answer)), State.ELEMENT_IS_CLICKABLE);
        jobQuestionAnswer.click();
    }

    public void jobQuestionSelectYes(String question) {
        WebElement yes = waitForElement(By.xpath(String.format(JOB_QUESTION_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(yes);
    }

    public void jobQuestionSelectNo(String question) {
        WebElement no = waitForElement(By.xpath(String.format(JOB_QUESTION_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(no);
    }

    public void jobQuestionEnterText(String question, String text) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_INPUT_XPATH, question)), ELEMENT_IS_VISIBLE);
        jobQuestion.sendKeys(text);
    }

    public void jobQuestionEnterMultipleLineText(String question, String text) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_MULTIPLELINETEXT_XPATH, question)), ELEMENT_IS_VISIBLE);
        jobQuestion.sendKeys(text);
    }

    public void jobQuestionEnterNonDecimal(String question, int text) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_NONDECIMAL_XPATH, question)), ELEMENT_IS_VISIBLE);
        jobQuestion.sendKeys(Integer.toString(text));
    }

    public String jobQuestionSelectRandomOption(String question) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_DROPDOWN_XPATH, question)), ELEMENT_IS_CLICKABLE);
        jobQuestion.click();
        waitForAngularRequestsToFinish();
        String selection = selectRandomOptionFromSelect(jobQuestion);
        return selection;
    }

    public void jobQuestionSelectRandomDate(String question, String date) {
        WebElement jobQuestionCalendarIcon = waitForElement(By.xpath(String.format(JOB_QUESTION_CALENDAR_XPATH, question)), ELEMENT_IS_CLICKABLE);
        jobQuestionCalendarIcon.click();
        waitForAngularRequestsToFinish();
        POHelper.clickJavascript(jobQuestionCalendarIcon);
        WebElement jobQuestionCalendarOption =  waitForElement(By.xpath(String.format(JOB_QUESTION_CALENDAR_OPTION_XPATH, date)), ELEMENT_IS_CLICKABLE);
        jobQuestionCalendarOption.click();
        waitForAngularRequestsToFinish();
    }

    public String jobQuestionSelectRandomTime(String question) {
        WebElement jobQuestionClockIcon = waitForElement(By.xpath(String.format(JOB_QUESTION_CLOCK_XPATH, question)), ELEMENT_IS_CLICKABLE);
        jobQuestionClockIcon.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public void clickSameAsCaller() {
        POHelper.scrollToElement(sameAsCaller);
        sameAsCaller.click();
        waitForAngularRequestsToFinish();
    }

    public void clickAddJobContact() {
        addJobContact.click();
        waitForAngularRequestsToFinish();
    }

    public void clickAddNewJobContact() {
        addNewJobContact.click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getCallers() {
        List<String> callers = new ArrayList<>();
        List<WebElement> callerOptions = driver.findElements(By.xpath(CALLER_OPTIONS_XPATH));
        for (WebElement caller : callerOptions) {
            callers.add(caller.getAttribute("innerText"));
        }
        return callers;
    }

    public List<String> getSiteContacts() {
        waitForAngularRequestsToFinish();
        List<String> contacts = new ArrayList<>();
        for (WebElement contact : siteContacts) {
            contacts.add(contact.getAttribute("innerText"));
        }
        return contacts;
    }

    public List<String> getSiteContactsFromNewSiteContactDropdown() {
        waitForAngularRequestsToFinish();
        List<String> contacts = new ArrayList<>();
        for (WebElement contact : siteContactsNew) {
            contacts.add(contact.getAttribute("innerText"));
        }
        return contacts;
    }

    public String getStoredSiteContact(int index) {
        return storedSiteContact.get(index).getAttribute("innerText");
    }

    public String getSiteContact(int index) {
        return siteContacts.get(index).getAttribute("innerText");
    }

    public void selectRandomSiteContact() {
        siteContactDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomSiteContactFromNewSiteContactDropdown() {
        siteContactDropdownNew.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectSiteContactFromNewSiteContactDropdown(String option) {
        siteContactDropdownNew.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(option);
    }

    public String selectRandomSiteContact(int index) {
        siteContactsDropdown.get(index).click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public void addJobContact() {
        siteContactDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomLocation() {
        POHelper.scrollToElement(locationDropdown);
        POHelper.clickJavascript(locationDropdown);
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectSiteContact(String option) {
        siteContactDropdown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(option);
    }

    public void setSiteContactInactive(String contactName) throws Exception {
        WebElement siteContactStates = driver.findElement(By.xpath(String.format(ACTIVE_CONTACT_XPATH, contactName)));

        if (siteContactStates.isSelected()) {
            siteContactStates.click();
            waitForAngularRequestsToFinish();
        }
    }

    public List<Integer> getDuplicateJobReferences(){
        List<Integer> jobRefs = new ArrayList<>();
        for(String s : getDuplicateContent(DUPLICATE_JOB_REFERENCE_XPATH)) {
            jobRefs.add(Integer.valueOf(s));
        }
        return jobRefs;
    }

    public List<String> getDuplicateSiteNames(){
        return getDuplicateContent(DUPLICATE_SITE_NAME_XPATH);
    }

    public List<String> getDuplicateStatuses(){
        return getDuplicateContent(DUPLICATE_STATUS_XPATH);
    }

    public List<String> getDuplicateCreatedDates(){
        return getDuplicateContent(DUPLICATE_CREATED_XPATH);
    }

    public List<String> getDuplicateAstClassifications(){
        return getDuplicateContent(String.format(DUPLICATE_CONTENT_XPATH, "Subtype / Classification"));
    }

    public List<String> getDuplicateLocations(){
        return getDuplicateContent(String.format(DUPLICATE_CONTENT_XPATH, "Location"));
    }

    public List<String> getDuplicateAssets(){
        return getDuplicateContent(String.format(DUPLICATE_CONTENT_XPATH, "Asset"));
    }

    public List<String> getDuplicateFaultTypes(){
        return getDuplicateContent(String.format(DUPLICATE_CONTENT_XPATH, "Fault Type"));
    }

    public List<String> getDuplicateDescriptions(){
        return getDuplicateContent(String.format(DUPLICATE_CONTENT_XPATH, "Description"));
    }

    private List<String> getDuplicateContent(String xpath){
        waitForElement(By.xpath(DUPLICATE_JOBS_XPATH), ELEMENT_IS_VISIBLE);
        List<String> content = new ArrayList<>();
        for (WebElement we : driver.findElements(By.xpath(DUPLICATE_JOBS_XPATH + xpath))) {
            content.add(we.getText());
        }
        return content;
    }

    public void getSelectPotentialDuplicateJob(Integer jobReference) {
        String xpath = String.format(DUPLICATE_JOB_CARD_XPATH, jobReference);
        WebElement card = driver.findElement(By.xpath(xpath));
        card.click();
        waitForAngularRequestsToFinish();
    }

    public CancelChangesAlert cancel() {
        POHelper.clickJavascript(cancel);
        return new CancelChangesAlert(driver).get();
    }

    public void save() {
        POHelper.clickJavascript(save);
        waitForAngularRequestsToFinish();
    }

    public void saveRequestedQuote() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public void save_BeforeAllMandatoryFieldsPopulated() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public String getError() {
        return error.getText();
    }

    public String getContractorToQuoteError() {
        WebElement error = driver.findElement(By.xpath(CONTRACTOR_TO_QUOTE_XPATH + ERROR_XPATH));
        return error.getText();
    }

    public String getContractorToQuote() {
        return contractorToQuoteValue.getText();
    }

    public void setContractorToQuote(String contractor) {
        contractorToQuoteDropdown.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(contractor);
    }

    public Boolean isContractorToQuoteRequested() {
        if (isElementPresent(By.xpath(CONTRACTOR_TO_QUOTE_XPATH))) {
            return true;
        }
        return false;
    }

    public String setRandomContractorToQuote() {
        contractorToQuoteDropdown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public List<String> getContractorToQuoteOptions() {
        POHelper.clickJavascript(contractorToQuoteDropdown);
        return getVisibleDropdownOptions();
    }

    public Boolean isContractorToQuoteSelectable() {
        return !contractorToQuoteDropdown.getAttribute("unselectable").equals("on");
    }

    public String getSelectedContractorToQuote() {
        return contractorToQuoteValue.getText();
    }

    public boolean isDisplayed() {
        return page.isDisplayed();
    }

    public void enterContactAlternativeNumber(String str) throws InterruptedException {
        alternativeNumber.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        alternativeNumber.sendKeys(Keys.DELETE);
        alternativeNumber.sendKeys(str);
        alternativeNumber.sendKeys(Keys.TAB);
        waitForAngularRequestsToFinish();
    }

    public void enterContactNotes(String str) {
        notes.clear();
        notes.sendKeys(str);
    }

    public List<String> getResourceQuestions(){
        List<String> questions = new ArrayList<>();
        if (isElementPresent(By.xpath(RESOURCE_QUESTIONS_XPATH))) {
            for (WebElement resourceQuestion : driver.findElements(By.xpath(RESOURCE_QUESTIONS_XPATH))){
                questions.add(resourceQuestion.getText());
            }
        }
        return questions;
    }

    public List<String> getResourceQuestionOptions(String question) {

        String xpath = String.format(RESOURCE_QUESTION_DROPDOWN_ARROW_XPATH, question);
        driver.findElement(By.xpath(xpath)).click();
        waitForAngularRequestsToFinish();

        return getVisibleDropdownOptions();
    }

    public void enterResourceQuestionAnswer(String question, String answer) throws InterruptedException {
        String xpath = String.format(RESOURCE_QUESTION_DROPDOWN_ARROW_XPATH, question);
        WebElement dropdown = driver.findElement(By.xpath(xpath));
        POHelper.scrollToElement(dropdown);
        POHelper.clickJavascript(dropdown);
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(answer);
    }

    public List<String> getJobQuestions(){
        List<String> questions = new ArrayList<>();
        if (isElementPresent(By.xpath(JOB_QUESTIONS_XPATH))) {
            for (WebElement jobQuestion : driver.findElements(By.xpath(JOB_QUESTIONS_XPATH))){
                questions.add(jobQuestion.getText());
            }
        }
        return questions;
    }

    public String getJobQuestionTagName(String question) {
        WebElement element = driver.findElement(By.xpath(String.format(JOB_QUESTION_TYPE_XPATH, question)));
        return element.getTagName();
    }

    public String getJobQuestionAttribute(String question, String attributeValue) {
        WebElement element = driver.findElement(By.xpath(String.format(JOB_QUESTION_TYPE_XPATH, question)));
        return element.getAttribute(attributeValue);
    }

    public void selectResourceQuestionOption(String question, String option) {
        String xpath = String.format(RESOURCE_QUESTION_DROPDOWN_ARROW_XPATH, question);
        driver.findElement(By.xpath(xpath)).click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(option);
    }

    public boolean isResourceQuestionAnswered(String question) {
        String xpath = String.format(RESOURCE_QUESTION_ANSWERED_XPATH, question);
        return !isElementPresent(By.xpath(xpath));
    }

    public String getSelectedResource() {
        return advisedResource.getText();
    }

    public String selectRandomResourceQuestionOption(String question) {
        String xpath = String.format(RESOURCE_QUESTION_DROPDOWN_ARROW_XPATH, question);
        driver.findElement(By.xpath(xpath)).click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public boolean isCapexUrgentCriticalAlertDisplayed() {
        WebElement capexUrgentCriticalAlert	= waitForElement(By.xpath(CAPEX_URGENT_CRITICAL_ALERT_XPATH), State.ELEMENT_IS_VISIBLE);
        return capexUrgentCriticalAlert != null;
    }

    public List<String> getAssets() {
        assetDropdown.click();
        waitForAngularRequestsToFinish();
        waitForKendoLoadingToComplete();
        List<String> assets = getVisibleDropdownOptions();
        assetDropdown.click(); // close the dropdown
        waitForAngularRequestsToFinish();
        return assets;
    }

    public String selectRandomAsset() {
        assetDropdown.click();
        waitForAngularRequestsToFinish();
        waitForKendoLoadingToComplete();
        return selectRandomVisibleDropdownOption();
    }

    public String selectRandomClassification() {
        subtypeClassification.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String selectRandomFault() {
        faultTypeDropdown.click();
        waitForAngularRequestsToFinish();
        waitForKendoLoadingToComplete();
        return selectRandomVisibleDropdownOption();
    }

    public Boolean isAssetNotSelectedModalDisplayed() {
        return isElementPresent(By.xpath(ASSET_NOT_SELECTED_MODAL_XPATH));
    }

    public void selectRandomAssetNotSelectedReason() {
        assetNotSelectedReason.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void assetNotSelectedContinue() {
        assetNotSelectedContinue.click();
        waitForAngularRequestsToFinish();
    }

    public EditCallerModal selectEditCaller() {
        editCaller.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, EditCallerModal.class).get();
    }

    public Boolean isConfirmJobChangesModalDisplayed() {
        return isElementPresent(By.xpath(CONFIRM_JOB_CHANGES_MODAL_XPATH));
    }

    public Boolean isAddJobContactButtonDisplayed() {
        return isElementPresent(By.xpath(ADD_JOB_CONTACT_XPATH));
    }

    public Boolean isSameAsCallerButtonVisible() {
        return isElementVisible(By.cssSelector(SAME_AS_CALLER_CSS));
    }

    public Boolean isDeferralQuestionAsked() {
        return isElementVisible(By.xpath(DEFERRAL_SECTION_XPATH));
    }

    public void setOkToDefer() {
        int pos = Integer.valueOf(okToDefer.getLocation().getY());
        scrollTo(pos);
        POHelper.clickJavascript(okToDefer);
    }

    public void setNotOkToDefer() {
        int pos = Integer.valueOf(notOkToDefer.getLocation().getY());
        scrollTo(pos);
        POHelper.clickJavascript(notOkToDefer);
    }

    public String selectRandomDeferralReason() {
        deferralReason.click();
        waitForAngularRequestsToFinish();
        String selection = selectRandomVisibleDropdownOption();
        return selection;
    }

    public void deferUntilNow() {
        POHelper.clickJavascript(deferUntilNow);
    }

    public void deferUntil(String day) {
        String xpath = String.format(DEFER_UNTIL_XPATH, day);
        WebElement we = driver.findElement(By.xpath(xpath));
        POHelper.clickJavascript(we);
    }

    public void deferUntilFirstDate() {
        POHelper.clickJavascript(deferUntilFirstDate);
    }

    public void deferUntilSecondDate() {
        POHelper.clickJavascript(deferUntilSecondDate);
    }

    public void deferUntilLaterDate() {
        POHelper.clickJavascript(deferUntilLaterDate);
    }

    public void enterDeferralNote(String note){
        deferralNote.sendKeys(note);
    }

    public boolean isDeferralQuestionDisplayed() {
        return isElementVisible(By.xpath(DEFERRAL_SECTION_XPATH));
    }

    public void addNotes(){
        addNotesButton.click();
        waitForAngularRequestsToFinish();
    }

    public int getAddNoteButtonBadgeNumber() {
        waitForElement(By.cssSelector(ADD_NOTES_BUTTON_BADGE_CSS), State.ELEMENT_IS_VISIBLE);
        int badgeCount = Integer.parseInt(addNotesButtonBadge.getText());
        return badgeCount;
    }

    public HelpdeskAddAttachmentsModal clickAttachmentsIcon() {
        POHelper.scrollToElement(addAttachment);
        addAttachment.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAddAttachmentsModal.class).get();
    }

    public int getAttachmentButtonBadgeNumber() {
        int badgeCount = new Integer(0);
        if (isElementPresent(By.cssSelector(ADD_ATTACHMENT_BADGE_CSS))) {
            badgeCount = Integer.parseInt(addAttachmentButtonBadge.getText());
        }
        return badgeCount;
    }

    public Boolean isCallerListBoxEnabled() {
        return callerListbox.isEnabled();
    }

    public Boolean isSubTypeClassificationListBoxEnabled() {
        return subtypeClassification.isEnabled();
    }

    public Boolean isLocationWithinSiteListBoxEnabled() {
        return locationListbox.isEnabled();
    }

    public Boolean isJobDescriptionEditBoxEnabled() {
        return jobDescription.isEnabled();
    }

    public Boolean isFaultTypeListBoxEnabled() {
        return faultTypeDropdown.isEnabled();
    }

    public Boolean isSiteContactListBoxEnabled() {
        return siteContactDropdown.isEnabled();
    }

    public HelpdeskNewSiteContactModal clickNewContactButton() {
        editJobNewContactButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskNewSiteContactModal.class).get();
    }

    public String getSiteContactJobRole() {
        return siteContactJobRole.getAttribute("innerText");
    }

    public String getSiteContactTelephone() {
        return siteContactTelephone.getAttribute("innerText");
    }

    public boolean isActiveCheckboxClickable(String newCaller) {
        String xpath = String.format(ACTIVE_CONTACT_CLICKABLE_XPATH, newCaller);
        return isElementClickable(By.xpath(xpath));
    }

    public Boolean isPotentialInsuranceQuoteQuestionDisplayed() {
        return isElementPresent(By.xpath(POTENTIAL_INSURANCE_QUOTE_XPATH));
    }

    public void selectRandomAnswerForIsPotentialInsuranceQuote() {
        List<WebElement> potentialInsurance = driver.findElements(By.xpath(POTENTIAL_INSURANCE_QUOTE_XPATH));
        int randomSelection = RandomUtils.nextInt(0, potentialInsurance.size());
        String id = potentialInsurance.get(randomSelection).getAttribute("id");
        WebElement potentialInsuranceQuote = driver.findElement(By.xpath(String.format(POTENTIAL_INSURANCE_QUOTE_LABELS_XPATH, id)));
        potentialInsuranceQuote.click();
    }
}
