package mercury.pageobject.web.helpdesk.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskNewSiteContactModal extends Base_Page<HelpdeskNewSiteContactModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String MODAL_XPATH = "//div[@class='modal-content']";
    private static final String MODAL_HEADER_XPATH = MODAL_XPATH + "//h3[contains(text(),'New Site Contact')]";
    private static final String MODAL_BODY_XPATH = MODAL_XPATH + "//form[@name='newCallerForm']";
    private static final String PAGE_FOOTER_XPATH = MODAL_BODY_XPATH + "//div[@class='modal-footer']";

    // Core
    private static final String CALLER_NAME_INPUT_XPATH = MODAL_BODY_XPATH + "//input[@name='name']";
    private static final String JOB_TITLE_INPUT_XPATH = MODAL_BODY_XPATH + "//input[@name='jobTitle']";
    private static final String SITE_DROPDOWN_XPATH = MODAL_BODY_XPATH + "//select[@name='site']";
    private static final String DEPARTMENT_INPUT_XPATH = MODAL_BODY_XPATH + "//input[@name='department']";
    private static final String TELEPHONE_INPUT_XPATH = MODAL_BODY_XPATH + "//input[@name='phoneNumber']";
    private static final String EXTENSION_INPUT_XPATH = MODAL_BODY_XPATH + "//input[@name='extension']";

    // Buttons
    private static final String BUTTON_XPATH = PAGE_FOOTER_XPATH + "//button[text()='%s']";

    @FindBy(xpath = CALLER_NAME_INPUT_XPATH)
    private WebElement callerName;

    @FindBy(xpath = JOB_TITLE_INPUT_XPATH)
    private WebElement jobTitle;

    @FindBy(xpath = SITE_DROPDOWN_XPATH)
    private WebElement siteDropdown;

    @FindBy(xpath = DEPARTMENT_INPUT_XPATH)
    private WebElement department;

    @FindBy(xpath = TELEPHONE_INPUT_XPATH)
    private WebElement telephone;

    @FindBy(xpath = EXTENSION_INPUT_XPATH)
    private WebElement extension;

    public HelpdeskNewSiteContactModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(MODAL_HEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (Exception e) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterName(String name) {
        callerName.sendKeys(name);
    }

    public void enterJobTitle(String title) {
        jobTitle.sendKeys(title);
    }

    public void enterDepartment(String departmentName) {
        department.sendKeys(departmentName);
    }

    public void enterTelephone(String value) {
        telephone.sendKeys(value);
    }

    public void clickButton(String button) {
        WebElement element = waitForElement(By.xpath(String.format(BUTTON_XPATH, button)), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
    }

    public String getName() {
        return callerName.getAttribute("value");
    }

}
