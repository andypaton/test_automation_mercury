package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminNewIncidentCriteriaPage extends Base_Page<AdminNewIncidentCriteriaPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String INCIDENT_CRITERIA_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String INCIDENT_CRITERIA_CONTENT_XPATH = INCIDENT_CRITERIA_PAGE_XPATH + "//div[@class='admin-action__content']";

    private static final String DROPDOWN_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//label[contains(text(),'%s')]/following::select";

    private static final String ACTIVE_INACTIVE_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//label[contains(text(), '%s')]";
    private static final String SAVE_BUTTON_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//button[@type='submit']";
    private static final String CANCEL_BUTTON_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//button[contains(text(), 'Cancel')]";

    private static final String ERROR_MESSAGE_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//span[contains(text(), '%s')]";


    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;


    public AdminNewIncidentCriteriaPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INCIDENT_CRITERIA_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Boolean isErrorMessageDisplayed(String errorMessage) {
        WebElement error = driver.findElement(By.xpath(String.format(ERROR_MESSAGE_XPATH, errorMessage)));
        return error.isDisplayed();
    }

    public void clickTabKeyOnDropdown(String dropdownName) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)));
        dropdown.sendKeys(Keys.TAB);
        waitForAngularRequestsToFinish();
    }

    public void selectDropdownOption(String dropdownName, String option) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)));
        Select select = new Select(dropdown);
        select.selectByVisibleText(option);
        waitForAngularRequestsToFinish();
    }

    public String selectRandomDropdownOption(String dropdownName) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)));
        String option = selectRandomOptionFromSelect(dropdown);
        waitForAngularRequestsToFinish();
        dropdown.sendKeys(Keys.TAB);
        return option;
    }

    public void selectActiveOrInactive(String isCriteriaActive) {
        WebElement activeInactive = driver.findElement(By.xpath(String.format(ACTIVE_INACTIVE_XPATH, isCriteriaActive)));
        activeInactive.click();
        waitForAngularRequestsToFinish();
    }

    public void save() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }


}
