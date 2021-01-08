package mercury.pageobject.web.portal.creditnotes;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UploadCreditNoteDocumentPage extends Base_Page<UploadCreditNoteDocumentPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Upload credit note document";

    //Main Content
    private static final String UPLOAD_CREDIT_NOTE_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String UPLOAD_CREDIT_NOTE_MAIN_CONTENT_XPATH = UPLOAD_CREDIT_NOTE_CONTAINER_XPATH + "//div[@class='main-content']";
    private static final String UPLOAD_CREDIT_NOTE_DOCUMENT_TITLE_XPATH = UPLOAD_CREDIT_NOTE_CONTAINER_XPATH + "//h1[contains(text(), '"
            + PAGE_TITLE
            + "')]";

    //Form
    private static final String UPLOAD_CREDIT_NOTE_DOCUMENT_FORM_XPATH = UPLOAD_CREDIT_NOTE_MAIN_CONTENT_XPATH + "//form[@action='/Portal/CreditNote/UploadCreditNoteDocument']";

    // Text Box
    private static final String FILENAME_XPATH = UPLOAD_CREDIT_NOTE_DOCUMENT_FORM_XPATH + "//fieldset//input[(@id='FileToUpload')]";

    // Buttons
    private static final String UPLOAD_CREDIT_NOTE_DOCUMENT_UPLOAD_FILE_BUTTON_XPATH = UPLOAD_CREDIT_NOTE_DOCUMENT_FORM_XPATH + "//input[(@type='submit')]";


    @FindBy(xpath = UPLOAD_CREDIT_NOTE_DOCUMENT_TITLE_XPATH)
    private WebElement uploadCreditNoteDocumentTitle;

    @FindBy(xpath = UPLOAD_CREDIT_NOTE_DOCUMENT_UPLOAD_FILE_BUTTON_XPATH)
    private WebElement uploadFileButton;

    @FindBy(xpath = FILENAME_XPATH)
    private WebElement documentFileName;

    public UploadCreditNoteDocumentPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(UPLOAD_CREDIT_NOTE_DOCUMENT_TITLE_XPATH));
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

    public Boolean isPageLoaded() {
        return this.isElementPresent(By.xpath(UPLOAD_CREDIT_NOTE_DOCUMENT_TITLE_XPATH));
    }

    public void clickUploadFile() {
        uploadFileButton.click();
        waitForAngularRequestsToFinish();
    }

    public void setDocumentFileName(String keysToSend) {
        documentFileName.sendKeys(keysToSend);
    }

}
