package mercury.pageobject.web.helpdesk.caller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskLogJobPage;

public class NewCallerPage extends Base_Page<NewCallerPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_CSS = "#main-panel > div.tab-content > div.tab-pane.active";
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String NEW_CALLER_FORM_CSS = ACTIVE_WORKSPACE_CSS + " ph-manage-caller-modal[modal-name='newCallerModal']";
    private static final String NEW_CALLER_FORM_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-manage-caller-modal[@modal-name='newCallerModal']";

    // FIELDS
    private static final String TITLE_CSS = NEW_CALLER_FORM_CSS + " .modal-title";
    private static final String NAME_CSS = NEW_CALLER_FORM_CSS + " #CallerName";
    private static final String JOB_ROLE_CSS = NEW_CALLER_FORM_CSS + " input[name='role']";
    private static final String DEPARTMENT_CSS = NEW_CALLER_FORM_CSS + " input[name='department']";
    private static final String TELEPHONE_CSS = NEW_CALLER_FORM_CSS + " input[name='telephone']";
    private static final String EXTENSION_CSS = NEW_CALLER_FORM_CSS + " input[name='extension']";

    // BUTTONS
    private static final String CANCEL_XPATH = NEW_CALLER_FORM_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String SAVE_XPATH = NEW_CALLER_FORM_XPATH + "//button[contains(text(), 'Save and identify as caller')]";

    // ERRORS
    private static final String CALLER_ERROR_CSS = NEW_CALLER_FORM_CSS + " #CallerName-error";
    private static final String JOB_ROLE_ERROR_CSS = NEW_CALLER_FORM_CSS + " #role-error";
    private static final String DEPARTMENT_ERROR_CSS = NEW_CALLER_FORM_CSS + " #department-error";
    private static final String TELEPHONE_ERROR_CSS = NEW_CALLER_FORM_CSS + " #telephone-error";
    private static final String EXTENSION_ERROR_CSS = NEW_CALLER_FORM_CSS + " #extension-error";

    @FindBy(css = NEW_CALLER_FORM_CSS)
    private static WebElement newCaller;

    @FindBy(css = TITLE_CSS)
    private static WebElement title;

    @FindBy(css = NAME_CSS)
    private static WebElement name;

    @FindBy(css = JOB_ROLE_CSS)
    private static WebElement jobRole;

    @FindBy(css = DEPARTMENT_CSS)
    private static WebElement department;

    @FindBy(css = TELEPHONE_CSS)
    private static WebElement telephone;

    @FindBy(css = EXTENSION_CSS)
    private static WebElement extension;

    @FindBy(xpath = CANCEL_XPATH)
    private static WebElement cancel;

    @FindBy(xpath = SAVE_XPATH)
    private static WebElement save;

    @FindBy(css = CALLER_ERROR_CSS)
    private static WebElement nameError;

    @FindBy(css = JOB_ROLE_ERROR_CSS)
    private static WebElement jobRoleError;

    @FindBy(css = DEPARTMENT_ERROR_CSS)
    private static WebElement departmentError;

    @FindBy(css = TELEPHONE_ERROR_CSS)
    private static WebElement telephoneError;

    @FindBy(css = EXTENSION_ERROR_CSS)
    private static WebElement extensionError;

    public NewCallerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForElement(By.xpath(CANCEL_XPATH), State.ELEMENT_IS_CLICKABLE);
            Assert.assertTrue("Page is not displayed", newCaller.isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getTitle() {
        return title.getText();
    }

    public void enterName(String text) {
        name.clear();
        name.sendKeys(text);
    }

    public void enterJobRole(String text) {
        jobRole.clear();
        jobRole.sendKeys(text);
    }

    public void enterDepartment(String text) {
        department.clear();
        department.sendKeys(text);
    }

    public void enterTelephone(String text) {
        telephone.clear();
        telephone.sendKeys(text);
    }

    public void enterExtension(String text) {
        extension.clear();
        extension.sendKeys(text);
    }

    public HelpdeskLogJobPage save() throws InterruptedException {
        save.click();
        waitUntilElementNotDisplayed(By.cssSelector(NAME_CSS));
        return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
    }

    public HelpdeskLogJobPage save_ErrorExpected() throws InterruptedException {
        save.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
    }

    public HelpdeskLogJobPage cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
    }

    public String getCallerError() {
        return nameError.getText();
    }

    public String getJobRoleError() {
        return jobRoleError.getText();
    }

    public String getDepartmentError() {
        return departmentError.getText();
    }

    public String getTelephoneError() {
        return telephoneError.getText();
    }

    public String getExtensionError() {
        return telephoneError.getText();
    }

    public boolean isCallerErrorDisplayed() {
        return isElementPresent(By.cssSelector(CALLER_ERROR_CSS));
    }

    public boolean isJobRoleErrorDisplayed() {
        return isElementPresent(By.cssSelector(JOB_ROLE_ERROR_CSS));
    }

    public boolean isDepartmentErrorDisplayed() {
        return isElementPresent(By.cssSelector(DEPARTMENT_ERROR_CSS));
    }

    public boolean isTelephoneErrorDisplayed() {
        return isElementPresent(By.cssSelector(TELEPHONE_ERROR_CSS));
    }

    public boolean isExtensionErrorDisplayed() {
        return isElementPresent(By.cssSelector(EXTENSION_ERROR_CSS));
    }

    public boolean isSaveEnabled() {
        return !"true".equals(save.getAttribute("disabled"));
    }
}
