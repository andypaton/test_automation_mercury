package mercury.pageobject.web.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.State;

public class HelpdeskAddAttachmentsModal extends Base_Page<HelpdeskAddAttachmentsModal> {


    private static final Logger logger = LogManager.getLogger();

    // Page
    private static final String PAGE_BODY_XPATH = "//body[@class='modal-open']";

    // Main panel
    private static final String MODAL_MAIN_PANEL_XPATH = PAGE_BODY_XPATH + "//div[@id='main-panel']";

    // Modal Dialog
    private static final String MODAL_DIALOG_XPATH = MODAL_MAIN_PANEL_XPATH + "//div[contains(@class, 'modal-dialog') and contains(@class, 'modal-lg')]";
    private static final String MODAL_BODY_XPATH = MODAL_DIALOG_XPATH + "//div[@class='modal-body']";
    private static final String MODAL_UPLOAD_BUTTON_XPATH = MODAL_BODY_XPATH + "//div[contains(@class, 'fileUpload') and contains(@class, 'btn') and contains(@class, 'btn-neutral')]";
    private static final String MODAL_UPLOAD_FILE_INPUT_XPATH = MODAL_UPLOAD_BUTTON_XPATH + "//input[contains(@name, 'file') and contains(@name, 'uploadedFile')]";
    private static final String MODAL_ATTACHMENT_TYPE_DROPDOWN_XPATH = MODAL_BODY_XPATH + "//label[contains(text(), 'Attachment Type')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String MODAL_ATTACHMENT_NOTE_XPATH = MODAL_BODY_XPATH + "//textarea[@name='Note']";
    private static final String MODAL_ATTACH_BUTTON_XPATH = MODAL_BODY_XPATH + "//button[contains(@class, 'btn btn-default') and contains(@class, 'btn-primary') and contains(text(), 'Attach')]";
    private static final String MODAL_ALERT_TEXT_XPATH = MODAL_BODY_XPATH + "//div[@class = 'alert-text']";
    private static final String MODAL_ERROR_ALERT_TEXT_XPATH = MODAL_BODY_XPATH + "//div[contains(@class, 'error') and contains(text(), '%s')]";
    private static final String MODAL_CURRENT_ATTACHMENTS_GRID_XPATH = MODAL_BODY_XPATH + "//div[@name='documentGrid']";
    private static final String MODAL_ATTACHMENTS_GRID_DELETE_BUTTON_XPATH = MODAL_CURRENT_ATTACHMENTS_GRID_XPATH + "//a[contains(@class, 'k-button') and contains(text(), 'Delete')]";
    private static final String MODAL_ATTACHMENTS_GRID_DOWNLOAD_BUTTON_XPATH = MODAL_CURRENT_ATTACHMENTS_GRID_XPATH + "//tr//td//button[text()='Download']";
    // Footer
    private static final String MODAL_FOOTER_XPATH = MODAL_DIALOG_XPATH + "//div[@class='modal-footer']";
    private static final String MODAL_CLOSE_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Close')]";

    @FindBy(xpath = MODAL_DIALOG_XPATH)
    private WebElement modalDialog;

    @FindBy(xpath = MODAL_UPLOAD_BUTTON_XPATH)
    private WebElement uploadButton;

    @FindBy(xpath = MODAL_ATTACHMENT_TYPE_DROPDOWN_XPATH)
    private WebElement attachmentTypeDropdown;

    @FindBy(xpath = MODAL_ATTACHMENT_NOTE_XPATH)
    private WebElement attachmentNote;

    @FindBy(xpath = MODAL_ATTACH_BUTTON_XPATH)
    private WebElement attachButton;

    @FindBy(xpath = MODAL_UPLOAD_FILE_INPUT_XPATH)
    private WebElement uploadFileInput;

    @FindBy(xpath = MODAL_CLOSE_BUTTON_XPATH)
    private WebElement closeButton;

    @FindBy(xpath = MODAL_ALERT_TEXT_XPATH)
    private WebElement alertText;

    @FindBy(xpath = MODAL_ERROR_ALERT_TEXT_XPATH)
    private WebElement errorAlertText;

    @FindBy(xpath = MODAL_ATTACHMENTS_GRID_DELETE_BUTTON_XPATH)
    private WebElement deleteButton;
    
    @FindBy(xpath = MODAL_ATTACHMENTS_GRID_DOWNLOAD_BUTTON_XPATH)
    private WebElement downloadButton;

    public HelpdeskAddAttachmentsModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForElement(By.xpath(MODAL_UPLOAD_BUTTON_XPATH), State.ELEMENT_IS_VISIBLE);
            assertTrue("Attachments modal not loaded!", driver.findElement(By.xpath(MODAL_UPLOAD_BUTTON_XPATH)).isDisplayed());
            logger.info("Attachments Modal Dialog loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Attachments Model Dialog failed to load");
            throw new AssertionError();
        }
    }

    public boolean isDisplayed() {
        return modalDialog.isDisplayed();
    }

    public void setAttachmentFileName(String fileToUpload) {
        uploadFileInput.sendKeys(fileToUpload);
    }

    public void clickUploadButton() {
        uploadButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickAttachButton() {
        attachButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCloseButton() {
        closeButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickDeleteButton() {
        deleteButton.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isAlertTextDisplayed(String alertMessage) {
        WebElement alertText = driver.findElement(By.xpath(String.format(MODAL_ALERT_TEXT_XPATH, alertMessage)));
        return alertText.isDisplayed();
    }

    public Boolean isErrorTextDisplayed(String errorMessage) {
        WebElement errorText = driver.findElement(By.xpath(String.format(MODAL_ERROR_ALERT_TEXT_XPATH, errorMessage)));
        return errorText.isDisplayed();
    }

    public String getErrorAlertText() {
        return errorAlertText.getText();
    }

    public Grid getGrid() {
        waitForElement(By.xpath(MODAL_CURRENT_ATTACHMENTS_GRID_XPATH), State.ELEMENT_IS_VISIBLE);
        return GridHelper.getGrid(MODAL_CURRENT_ATTACHMENTS_GRID_XPATH);
    }

    public void selectAttachmentType(String attachmentType) {
        attachmentTypeDropdown.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(attachmentType);
    }
    
    public boolean isDownloadButtonDisplayed() {
        return downloadButton.isEnabled();
    }
     
}
