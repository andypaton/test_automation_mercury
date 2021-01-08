package mercury.pageobject.web.portal.creditnotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class RejectCreditNoteModal extends Base_Page<RejectCreditNoteModal> {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Reject Credit Note";

    //Main Content
    private static final String REJECT_CREDIT_NOTE_MODAL_XPATH = "//*[@id='RejectCreditNoteModal']";
    private static final String REJECT_CREDIT_NOTE_MODAL_FOOTER_XPATH = REJECT_CREDIT_NOTE_MODAL_XPATH + "//div[contains(@class,'modal-footer')]";
    private static final String REJECT_CREDIT_NOTE_MODAL_HEADER_XPATH = REJECT_CREDIT_NOTE_MODAL_XPATH + "//div[contains(@class,'modal-header')]";
    private static final String REJECT_CREDIT_NOTE_MODAL_BODY_XPATH = REJECT_CREDIT_NOTE_MODAL_XPATH + "//div[contains(@class,'modal-body')]";

    // Elements
    private static final String REASON_DROPDOWN_XPATH = REJECT_CREDIT_NOTE_MODAL_BODY_XPATH + "//select";
    private static final String REJECTION_NOTES_XPATH = REJECT_CREDIT_NOTE_MODAL_BODY_XPATH + "//textarea";

    // Buttons
    private static final String SAVE_BUTTON_XPATH = REJECT_CREDIT_NOTE_MODAL_FOOTER_XPATH + "//button[contains(text(), 'Save')]";


    // Web Elements
    @FindBy(xpath = SAVE_BUTTON_XPATH) private WebElement save;

    @FindBy(xpath = REASON_DROPDOWN_XPATH) private WebElement reasonDropdown;

    @FindBy(xpath = REJECTION_NOTES_XPATH) private WebElement rejectionNotes;


    // Page Methods
    public RejectCreditNoteModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(REJECT_CREDIT_NOTE_MODAL_HEADER_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + "isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public void selectRandomRejectionReason() {
        waitForAngularRequestsToFinish();
        selectRandomOptionFromSelect(reasonDropdown);
    }

    public String getRejectionReason() {
        waitForAngularRequestsToFinish();
        return getSelectSelectedText(reasonDropdown);
    }

    public void enterRejectionNotes(String text) {
        rejectionNotes.sendKeys(text);
    }

    public void save() {
        save.click();
        waitForAngularRequestsToFinish();
    }
}
