package mercury.pageobject.web.helpdesk.caller;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class EditCallerModal extends Base_Page<EditCallerModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_CALLER_MODAL_XPATH = "//div[@id='manageCallerModal2' and contains(@style, 'display: block')]";

    private static final String NAME_XPATH = EDIT_CALLER_MODAL_XPATH + "//input[@id='CallerName']";
    private static final String JOB_ROLE_XPATH = EDIT_CALLER_MODAL_XPATH + "//input[@name='role']";
    private static final String DEPARTMENT_XPATH = EDIT_CALLER_MODAL_XPATH + "//input[@name='department']";
    private static final String TELEPHONE_XPATH = EDIT_CALLER_MODAL_XPATH + "//input[@name='telephone']";
    private static final String EXTENSION_XPATH = EDIT_CALLER_MODAL_XPATH + "//input[@name='extension']";

    private static final String CANCEL_XPATH = EDIT_CALLER_MODAL_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String UPDATE_CALLER_XPATH = EDIT_CALLER_MODAL_XPATH + "//button[contains(text(), 'Update and identify as caller')]";

    @FindBy(xpath = NAME_XPATH)
    private WebElement name;

    @FindBy(xpath = JOB_ROLE_XPATH)
    private WebElement jobRole;

    @FindBy(xpath = DEPARTMENT_XPATH)
    private WebElement department;

    @FindBy(xpath = TELEPHONE_XPATH)
    private WebElement telephone;

    @FindBy(xpath = EXTENSION_XPATH)
    private WebElement extension;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancel;

    @FindBy(xpath = UPDATE_CALLER_XPATH)
    private WebElement update;

    public EditCallerModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            assertTrue("Edit Caller modal not loaded!", driver.findElement(By.xpath(EDIT_CALLER_MODAL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getName() {
        return name.getAttribute("value");
    }

    public String getJobRole() {
        return jobRole.getAttribute("value");
    }

    public String getDepartment() {
        return department.getAttribute("value");
    }

    public String getTelephone() {
        return telephone.getAttribute("value");
    }

    public String getExtension() {
        return extension.getAttribute("value");
    }

    public void enterName(String str) {
        name.clear();
        name.sendKeys(str);
    }

    public void enterJobRole(String str) {
        jobRole.clear();
        jobRole.sendKeys(str);
    }

    public void enterDepartment(String str) {
        department.clear();
        department.sendKeys(str);
    }

    public void enterTelephone(String str) {
        telephone.clear();
        telephone.sendKeys(str);
    }

    public void enterExtension(String str) {
        extension.clear();
        extension.sendKeys(str);
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isCancelButtonDisplayed() {
        return isElementPresent(By.xpath(CANCEL_XPATH));
    }

    public boolean isUpdateButtonDisplayed() {
        return isElementPresent(By.xpath(UPDATE_CALLER_XPATH));
    }

    public void update() {
        update.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isUpdateEnabled() {
        return update.isEnabled();
    }

    public boolean isDisplayed() {
        return driver.findElement(By.xpath(EDIT_CALLER_MODAL_XPATH)).isDisplayed();
    }
}
