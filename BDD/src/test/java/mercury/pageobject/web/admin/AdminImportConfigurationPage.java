package mercury.pageobject.web.admin;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class AdminImportConfigurationPage extends Base_Page<AdminImportConfigurationPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String IMPORT_CONFIGURATION_XPATH = "//div[@class='admin-action__header']//h1[text()='Import Configuration admin']//ancestor::div[@class='admin-action']";

    private static final String LEFT_MENU_XPATH = IMPORT_CONFIGURATION_XPATH + "//a[contains(text(), '%s')]";
    private static final String SELECTED_MENU_ITEM = IMPORT_CONFIGURATION_XPATH + "//a[@class='admin-highlighted-link']";

    //Purchase Order Configuration
    private static final String CHOOSE_FILES_LABEL_XPATH = IMPORT_CONFIGURATION_XPATH + "//label[@for='file']";
    private static final String UPLOAD_BUTTON_XPATH = IMPORT_CONFIGURATION_XPATH + "//div[contains(@class, 'po-fileUpload')]";
    private static final String FILE_UPLOAD_XPATH = UPLOAD_BUTTON_XPATH + "//input";
    private static final String PROCESS_FILES_BUTTON_XPATH = IMPORT_CONFIGURATION_XPATH + "//button[@ng-click='poCtrl.processFiles()']";
    private static final String EDIT_ORDER_BUTTON_XPATH = IMPORT_CONFIGURATION_XPATH + "//a[contains(@class, 'k-grid-edit')]";
    private static final String IMPORT_POS_BUTTON_XPATH = IMPORT_CONFIGURATION_XPATH + "//button[@ng-click='poCtrl.importPOData()']";
    private static final String IMPORT_UNSUCCESSFUL_MESSAGE_XPATH = IMPORT_CONFIGURATION_XPATH + "//div[@ng-show='poCtrl.errorOnImport']//div";
    private static final String IMPORT_SUCCESSFUL_MESSAGE_XPATH = IMPORT_CONFIGURATION_XPATH + "//div[@ng-show='poCtrl.savedOnImport']";
    private static final String IMPORT_POS_SPINNER_XPATH = IMPORT_CONFIGURATION_XPATH + "//i[@class='icons__circle-notch spin']";

    //PPM Configuration
    private static final String PPM_UPLOAD_BUTTON_XPATH = IMPORT_CONFIGURATION_XPATH + "//div[contains(@class, 'ppm-file-upload')]";
    private static final String PPM_SCHEDULE_DROPDOWN_XPATH = IMPORT_CONFIGURATION_XPATH + "//select[@id='ppmSchedule']";
    private static final String PPM_FILE_UPLOAD_XPATH = PPM_UPLOAD_BUTTON_XPATH + "//input";
    private static final String PPM_PROCESS_FILES_BUTTON_XPATH = IMPORT_CONFIGURATION_XPATH + "//button[@ng-click='ImportPPMConfigurationFormCtrl.importFile()']";
    private static final String PPM_IMPORT_SUCCESSFUL_MESSAGE_XPATH = IMPORT_CONFIGURATION_XPATH + "//h4[contains(@class, 'success-text-md')]";
    private static final String PPM_IMPORT_UNSUCCESSFUL_MESSAGE_XPATH = IMPORT_CONFIGURATION_XPATH + "//h4[contains(@class, 'warning-text-md')]";


    @FindBy(xpath = SELECTED_MENU_ITEM)
    private WebElement selectedMenuItem;

    @FindBy(xpath = CHOOSE_FILES_LABEL_XPATH)
    private WebElement chooseFilesLabel;

    @FindBy(xpath = UPLOAD_BUTTON_XPATH)
    private WebElement uploadButton;

    @FindBy(xpath = PPM_UPLOAD_BUTTON_XPATH)
    private WebElement uploadButtonPpm;

    @FindBy(xpath = PPM_SCHEDULE_DROPDOWN_XPATH)
    private WebElement scheduleDropdownPpm;

    @FindBy(xpath = FILE_UPLOAD_XPATH)
    private WebElement fileUpload;

    @FindBy(xpath = PPM_FILE_UPLOAD_XPATH)
    private WebElement ppmFileUpload;

    @FindBy(xpath = PROCESS_FILES_BUTTON_XPATH)
    private WebElement processFiles;

    @FindBy(xpath = PPM_PROCESS_FILES_BUTTON_XPATH)
    private WebElement processFilesPpm;

    @FindBy(xpath = EDIT_ORDER_BUTTON_XPATH)
    private WebElement editOrder;

    @FindBy(xpath = IMPORT_POS_BUTTON_XPATH)
    private WebElement importPosButton;

    @FindBy(xpath = IMPORT_UNSUCCESSFUL_MESSAGE_XPATH)
    private WebElement importUnsuccessfulMessage;

    @FindBy(xpath = IMPORT_SUCCESSFUL_MESSAGE_XPATH)
    private WebElement importSuccessfulMessage;

    @FindBy(xpath = PPM_IMPORT_SUCCESSFUL_MESSAGE_XPATH)
    private WebElement importSuccessfulMessagePpm;

    @FindBy(xpath = PPM_IMPORT_UNSUCCESSFUL_MESSAGE_XPATH)
    private WebElement importUnsuccessfulMessagePpm;


    public AdminImportConfigurationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitWhileBusy();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(IMPORT_CONFIGURATION_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isMenuItemDisplayed(String menuName) {
        WebElement menuItem = driver.findElement(By.xpath(String.format(LEFT_MENU_XPATH, menuName)));
        return menuItem.isDisplayed();
    }

    public String getSelectedMenuItem() {
        return selectedMenuItem.getText();
    }

    public String getChooseFilesLabelText() {
        return chooseFilesLabel.getText();
    }

    public boolean isUploadButtonDisplayed() {
        return uploadButton.isDisplayed();
    }

    public boolean isPpmUploadButtonDisplayed() {
        return uploadButtonPpm.isDisplayed();
    }

    public boolean isPpmScheduleDropdownDisplayed() {
        return scheduleDropdownPpm.isDisplayed();
    }

    public void uploadFile(String filePath) {
        fileUpload.sendKeys(filePath);
    }

    public void uploadPpmFile(String filePath) {
        ppmFileUpload.sendKeys(filePath);
    }

    public void processFiles() {
        waitForElement(By.xpath(PROCESS_FILES_BUTTON_XPATH), ELEMENT_IS_VISIBLE);
        processFiles.click();
        waitForAngularRequestsToFinish();
    }

    public void processPpmFiles() {
        waitForElement(By.xpath(PPM_PROCESS_FILES_BUTTON_XPATH), ELEMENT_IS_VISIBLE);
        processFilesPpm.click();
        waitForAngularRequestsToFinish();
    }

    public AdminEditOrderWidget editOrder() {
        editOrder.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditOrderWidget.class).get();
    }

    public void importPos() {
        importPosButton.click();
        waitForAngularRequestsToFinish();
        waitUntilElementNotDisplayed(By.xpath(IMPORT_POS_SPINNER_XPATH));
    }

    public String getImportUnsuccessfulMessage() {
        return importUnsuccessfulMessage.getText();
    }

    public String getImportSuccessfulMessage() {
        return importSuccessfulMessage.getText();
    }

    public String getPpmImportSuccessfulMessage() {
        return importSuccessfulMessagePpm.getText();
    }

    public String getPpmImportUnsuccessfulMessage() {
        waitForElement(By.xpath(PPM_IMPORT_UNSUCCESSFUL_MESSAGE_XPATH), ELEMENT_IS_VISIBLE);
        return importUnsuccessfulMessagePpm.getText();
    }

    public void selectRandomSchedule() {
        selectRandomOptionFromSelect(scheduleDropdownPpm);
    }

}
