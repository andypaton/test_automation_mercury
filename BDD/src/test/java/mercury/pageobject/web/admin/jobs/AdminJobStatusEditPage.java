package mercury.pageobject.web.admin.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminJobStatusEditPage extends Base_Page<AdminJobStatusEditPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Jobs Admin";

    private static final String ADMIN_ACTION_XPATH = "//div[@class='admin-action']";

    private static final String ADMIN_ACTION_HEADER_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__header']//h1[contains(text(),'" + PAGE_TITLE + "')]";
    private static final String ADMIN_ACTION_CONTENT_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__content']";

    private static final String WARNING_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//h1";

    private static final String JOB_STATUS_EDIT_FORM_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//form";
    private static final String JOB_REFERENCE_XPATH = JOB_STATUS_EDIT_FORM_XPATH + "//label[contains(text(), 'Job Reference')]/following-sibling::div";
    private static final String CURRENT_JOB_STATUS_XPATH = JOB_STATUS_EDIT_FORM_XPATH + "//label[contains(text(), 'Current Job Status')]/following-sibling::div";
    private static final String TARGET_STATUS_DROPDOWN_XPATH = JOB_STATUS_EDIT_FORM_XPATH + "//label[contains(text(), 'Target Status')]/following-sibling::div/select";
    private static final String SAVE_BUTTON_XPATH = JOB_STATUS_EDIT_FORM_XPATH + "//button[contains(text(), 'Save')]";


    @FindBy(xpath = WARNING_XPATH)
    private WebElement warning;

    @FindBy(xpath = JOB_REFERENCE_XPATH)
    private WebElement jobReference;

    @FindBy(xpath = CURRENT_JOB_STATUS_XPATH)
    private WebElement currentJobStatus;

    @FindBy(xpath = TARGET_STATUS_DROPDOWN_XPATH)
    private WebElement targetStatusDropdown;


    public AdminJobStatusEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(WARNING_XPATH));
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getWarning() {
        return warning.getText();
    }

    public String getJobReference() {
        return jobReference.getText();
    }

    public String getCurrentJobStatus() {
        return currentJobStatus.getText();
    }

    public String getTargetStatus() {
        return getSelectSelectedText(targetStatusDropdown);
    }

    public boolean isSaveButtonDisplayed() {
        return isElementPresent(By.xpath(SAVE_BUTTON_XPATH));
    }

}