package mercury.pageobject.web.helpdesk.jobs;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskCallJobContactModal extends Base_Page<HelpdeskCallJobContactModal>{
    private static final Logger logger = LogManager.getLogger();


    // Page title
    private static final String PAGE_TITLE = "Call Job Contact";

    // Page elements
    private static final String PAGE_BODY_CSS = " body.modal-open";
    private static final String PAGE_MODAL_CONTENT_CSS = PAGE_BODY_CSS + " div.modal-content";
    private static final String PAGE_CALL_CONTACT_FORM_CSS = PAGE_MODAL_CONTENT_CSS + " div.contact-call";
    private static final String PAGE_MODAL_HEADER_CSS = PAGE_CALL_CONTACT_FORM_CSS + " div.modal-header";
    private static final String PAGE_MODAL_BODY_CSS = PAGE_CALL_CONTACT_FORM_CSS + " div.modal-body";
    private static final String PAGE_MODAL_FOOTER_CSS = PAGE_MODAL_CONTENT_CSS + " div.modal-footer";

    // Core
    private static final String CALL_CONTACT_CONTAIAINER_CSS = PAGE_MODAL_BODY_CSS + " div.contact-call__container";
    private static final String CALL_RADIO_SECTION_CSS = CALL_CONTACT_CONTAIAINER_CSS + " div.call-radio-section";
    private static final String CALL_ANSWERED_SECTION_CSS = CALL_CONTACT_CONTAIAINER_CSS + " div.contact-call__callAnswered";


    // Buttons and text boxes
    private static final String CALL_CONTACT_WRAPPER_LABEL_CSS = CALL_RADIO_SECTION_CSS + " label";
    private static final String CALL_CONTACT_CSS = CALL_CONTACT_WRAPPER_LABEL_CSS + " span.label-header";

    private static final String PRIMARY_PHONE_NUMBER_CSS = CALL_RADIO_SECTION_CSS + " label[for=primary]";
    private static final String SPEAKING_WITH_TEXTBOX_CSS = CALL_CONTACT_CONTAIAINER_CSS + " div.contact-call__speakingWith input";
    private static final String NOTES_TEXTBOX_CSS = CALL_CONTACT_CONTAIAINER_CSS + " div.contact-call__noAnswerNotes textarea";
    private static final String CALL_ANSWERED_YES_BUTTON_CSS = CALL_ANSWERED_SECTION_CSS + " label[for=Yes]";
    private static final String CALL_ANSWERED_NO_BUTTON_CSS = CALL_ANSWERED_SECTION_CSS + " label[for=No]";
    private static final String SAVE_CSS = PAGE_MODAL_FOOTER_CSS + " button.btn.btn-primary";

    // WebElements

    @FindBy(css = PAGE_MODAL_HEADER_CSS)
    private WebElement modalHeader;

    @FindBy(css = CALL_RADIO_SECTION_CSS)
    private WebElement callRadioButton;

    @FindBy(css = PRIMARY_PHONE_NUMBER_CSS)
    private WebElement primaryPhoneNumber;

    @FindBy(css = CALL_CONTACT_CSS)
    private WebElement callContact;

    @FindBy(css = CALL_CONTACT_WRAPPER_LABEL_CSS)
    private WebElement callContactWrapper;

    @FindBy(css = CALL_ANSWERED_YES_BUTTON_CSS)
    private WebElement callAnsweredYesButton;

    @FindBy(css = CALL_ANSWERED_NO_BUTTON_CSS)
    private WebElement callAnsweredNoButton;

    @FindBy(css = SPEAKING_WITH_TEXTBOX_CSS)
    private WebElement speakWithTextbox;

    @FindBy(css = NOTES_TEXTBOX_CSS)
    private WebElement notesTextbox;

    @FindBy(css = SAVE_CSS)
    private WebElement saveContactDetails;

    public HelpdeskCallJobContactModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(PAGE_MODAL_HEADER_CSS));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }


    public void clickPrimaryPhoneNumber() {
        primaryPhoneNumber.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCallcontact() {
        callContactWrapper.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCallAnsweredYes() {
        callAnsweredYesButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCallAnsweredNo() {
        callAnsweredNoButton.click();
        waitForAngularRequestsToFinish();
    }

    public void saveContactDetails() {
        this.saveContactDetails.click();
        waitForAngularRequestsToFinish();
    }

    public void setSpeakingWith(String note) {
        speakWithTextbox.clear();
        speakWithTextbox.sendKeys(note);
    }

    public void setNotes(String note) {
        notesTextbox.sendKeys(note);
    }

    public String getModalHeader() {
        return modalHeader.getText();
    }

    public String getCallContact() {
        return callContact.getText();
    }

    public String getSpeakingWith() {
        waitForElement(By.cssSelector(SPEAKING_WITH_TEXTBOX_CSS), State.ELEMENT_IS_VISIBLE);
        return speakWithTextbox.getAttribute("value");
    }

    public void clickCallRadioButton() {
        waitForElement(By.cssSelector(CALL_RADIO_SECTION_CSS), State.ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(callRadioButton);
    }

}
