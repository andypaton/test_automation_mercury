package mercury.pageobject.web.helpdesk.caller;

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

public class HelpdeskAddCallerPage extends Base_Page<HelpdeskAddCallerPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String MAIN_CONTENT_XPATH = "//div[@class='view-main-content']";

    private static final String ACTIVE_WORKSPACE_CSS = "#main-panel > div.tab-content > div.tab-pane.active div.view.caller";
    protected static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[@class='view caller']";

    // HEADERS
    private static final String HEADLINE_CSS = ACTIVE_WORKSPACE_CSS + " div.view-header__headline";
    private static final String SUBHEAD_CSS = ACTIVE_WORKSPACE_CSS + " div.view-header__subhead";
    private static final String SUBHEAD_LEFT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__left";
    private static final String SUBHEAD_RIGHT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__right";

    // BUTTONS
    private static final String BUTTONS_BAR_CSS = ACTIVE_WORKSPACE_CSS + " div.footer-button-bar__buttons-container";

    // CONTENT
    protected static final String DESC_TITLE_XPATH = "//dt[contains(text(),'%s')]";
    protected static final String DESC_VALUE_XPATH = "//dt[contains(text(),'%s')]/following-sibling::dd[1]";

    // ERRORS
    private static final String CALLER_ERROR_CSS = ACTIVE_WORKSPACE_CSS + " #CallerName-error";
    private static final String JOB_ROLE_ERROR_CSS = ACTIVE_WORKSPACE_CSS + " #role-error";
    private static final String DEPARTMENT_ERROR_CSS = ACTIVE_WORKSPACE_CSS + " #department-error";
    private static final String TELEPHONE_ERROR_CSS = ACTIVE_WORKSPACE_CSS + " #telephone-error";
    private static final String EXTENSION_ERROR_CSS = ACTIVE_WORKSPACE_CSS + " #extension-error";

    @FindBy(xpath = ACTIVE_WORKSPACE_XPATH + MAIN_CONTENT_XPATH)
    WebElement mainContent;

    @FindBy(css = HEADLINE_CSS)
    WebElement headline;

    @FindBy(css = SUBHEAD_CSS)
    WebElement subHeadline;

    @FindBy(css = SUBHEAD_LEFT_CSS)
    WebElement subHeadline_left;

    @FindBy(css = SUBHEAD_RIGHT_CSS)
    WebElement subHeadline_right;

    @FindBy(css = BUTTONS_BAR_CSS)
    WebElement buttonBar;

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

    // FIELDS
    private static final String NAME_CSS = ACTIVE_WORKSPACE_CSS + " #CallerName";
    private static final String JOB_ROLE_CSS = ACTIVE_WORKSPACE_CSS + " input[name='role']";
    private static final String DEPARTMENT_CSS = ACTIVE_WORKSPACE_CSS + " input[name='department']";
    private static final String TELEPHONE_CSS = ACTIVE_WORKSPACE_CSS + " input[name='telephone']";
    private static final String EXTENSION_CSS = ACTIVE_WORKSPACE_CSS + " input[name='extension']";

    // BUTTONS
    private static final String CANCEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//button[text()='Cancel']";
    private static final String SAVE_XPATH = ACTIVE_WORKSPACE_XPATH + "//button[text()='Save and identify as caller']";

    @FindBy(css = ACTIVE_WORKSPACE_XPATH)
    private static WebElement addCaller;

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

    public HelpdeskAddCallerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForElement(By.cssSelector(ACTIVE_WORKSPACE_CSS), ELEMENT_IS_VISIBLE);
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ACTIVE_WORKSPACE_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
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

    public boolean isContentTitleDisplayed(String title) {
        return mainContent.findElement(By.xpath(String.format(DESC_TITLE_XPATH, title))).isDisplayed();
    }

    public String getContentValue(String title) {
        return mainContent.findElement(By.xpath(String.format(DESC_VALUE_XPATH, title))).getText();
    }

    public HelpdeskAddCallerPage clickSaveAndIdentifyAsCaller() throws InterruptedException {
        save.click();
        waitUntilElementNotDisplayed(By.xpath(SAVE_XPATH));
        return PageFactory.initElements(driver, HelpdeskAddCallerPage.class).get();
    }

    public HelpdeskAddCallerPage clickSaveAndIdentifyAsCaller_ErrorExpected() throws InterruptedException {
        save.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAddCallerPage.class).get();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
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

    public boolean isExtensionErrorDisplayed() {
        return isElementPresent(By.cssSelector(EXTENSION_ERROR_CSS));
    }

    public boolean isSaveEnabled() {
        return !"true".equals(save.getAttribute("disabled"));
    }
}
