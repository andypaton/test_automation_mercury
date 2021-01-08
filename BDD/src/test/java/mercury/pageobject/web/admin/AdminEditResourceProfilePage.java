package mercury.pageobject.web.admin;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import static mercury.runtime.ThreadManager.getWebDriver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditResourceProfilePage extends Base_Page<AdminEditResourceProfilePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_RESOURCE_PROFILE_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String EDIT_RESOURCE_PROFILE_CONTENT_XPATH = EDIT_RESOURCE_PROFILE_PAGE_XPATH + "//div[@class='admin-action__sub-content']";
    private static final String EDIT_RESOURCE_PROFILE_FOOTER_XPATH = EDIT_RESOURCE_PROFILE_PAGE_XPATH + "//div[@class='footer-button-bar']";

    private static final String WORKING_HOURS_SHIFT_SUMMARY_XPATH = EDIT_RESOURCE_PROFILE_PAGE_XPATH + "//div[contains(@class, 'opening-hours__summary')]";
    private static final String WORKING_HOURS_SHIFT_SUMMARY_OPTIONS_XPATH = EDIT_RESOURCE_PROFILE_PAGE_XPATH + "//div[contains(@class, 'opening-hours__summary')]//div";
    private static final String WORKING_HOURS_START_AT_TIME_XPATH = EDIT_RESOURCE_PROFILE_PAGE_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//input[contains(@name, 'startAt')]";
    private static final String WORKING_HOURS_END_AT_TIME_XPATH = EDIT_RESOURCE_PROFILE_PAGE_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//input[contains(@name, 'endAt')]";
    private static final String WORKING_HOURS_OVERNIGHT_XPATH = EDIT_RESOURCE_PROFILE_PAGE_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//span[@ng-if='day.overnight!==null']";
    private static final String WORKING_HOURS_SHIFT_DAY_XPATH = EDIT_RESOURCE_PROFILE_PAGE_XPATH + "//div[contains(text(), '%s')]";

    private static final String ACTIVE_YES_XPATH = EDIT_RESOURCE_PROFILE_CONTENT_XPATH + "//label[@for='active_Yes']";
    private static final String ACTIVE_NO_XPATH = EDIT_RESOURCE_PROFILE_CONTENT_XPATH + "//label[@for='active_No']";
    private static final String CONFIRM_REMOVAL_CHECKBOX_XPATH = EDIT_RESOURCE_PROFILE_CONTENT_XPATH + "//label[@for='ConfirmRemoved']";

    private static final String CANCEL_BUTTON_XPATH = EDIT_RESOURCE_PROFILE_FOOTER_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String SAVE_BUTTON_XPATH = EDIT_RESOURCE_PROFILE_FOOTER_XPATH + "//span[contains(text(), 'Save')]/..";


    @FindBy(xpath = ACTIVE_YES_XPATH)
    private WebElement activeYes;

    @FindBy(xpath = ACTIVE_NO_XPATH)
    private WebElement activeNo;

    @FindBy(xpath = CONFIRM_REMOVAL_CHECKBOX_XPATH)
    private WebElement confirmRemovalCheckbox;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;


    public AdminEditResourceProfilePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(EDIT_RESOURCE_PROFILE_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void deleteWorkingHoursStartTime(String day) {
        WebElement startTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_START_AT_TIME_XPATH, day)));
        startTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
        startTime.sendKeys(Keys.ARROW_RIGHT);
        waitForAngularRequestsToFinish();
        startTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
        //To remove the AM that appears when run in Jenkins
        startTime.sendKeys(Keys.ARROW_RIGHT);
        waitForAngularRequestsToFinish();
        startTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
    }

    public void deleteWorkingHoursEndTime(String day) {
        WebElement endTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_END_AT_TIME_XPATH, day)));
        endTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
        endTime.sendKeys(Keys.ARROW_RIGHT);
        waitForAngularRequestsToFinish();
        endTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
        //To remove the PM that appears when run in Jenkins
        endTime.sendKeys(Keys.ARROW_RIGHT);
        waitForAngularRequestsToFinish();
        endTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
    }

    public void enterWorkingHoursStartTime(String day, String timePart) {
        WebElement startTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_START_AT_TIME_XPATH, day)));

        // set element to 'text' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'text');", startTime);

        startTime.clear();
        startTime.sendKeys(timePart);

        // reset element to 'time' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'time');", startTime);

        // re-focus
        new Actions(driver).moveToElement(startTime.findElement(By.xpath("//ph-edit-working-hours"))).click().perform();
    }

    public void enterWorkingHoursEndTime(String day, String timePart) throws InterruptedException {
        WebElement endTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_END_AT_TIME_XPATH, day)));

        // set element to 'text' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'text');", endTime);

        endTime.clear();
        endTime.sendKeys(timePart);

        // reset element to 'time' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'time');", endTime);

        // re-focus
        new Actions(driver).moveToElement(endTime.findElement(By.xpath("//ph-edit-working-hours"))).click().perform();

        //Wait for the Overnight to be populated
        waitForElement(By.xpath(String.format(WORKING_HOURS_OVERNIGHT_XPATH, day)), ELEMENT_IS_VISIBLE);
    }

    public Boolean isShiftDisplayed(String shift) {
        return this.isElementPresent(By.xpath(String.format(WORKING_HOURS_SHIFT_DAY_XPATH, shift)));
    }

    public Boolean isShiftOvernightDisplayed(String shift) {
        return this.isElementPresent(By.xpath(String.format(WORKING_HOURS_OVERNIGHT_XPATH, shift)));
    }

    public String getShiftSummaryText(String shift) {
        WebElement shiftSummary = driver.findElement(By.xpath(String.format(WORKING_HOURS_SHIFT_DAY_XPATH, shift)));
        return shiftSummary.getText();
    }

    public String getShiftOvernightText(String shift) {
        WebElement shiftOvernight = driver.findElement(By.xpath(String.format(WORKING_HOURS_OVERNIGHT_XPATH, shift)));
        return shiftOvernight.getAttribute("innerHTML");
    }

    public void deactivateResourceProfile() {
        activeNo.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isConfirmRemovalCheckboxDisplayed() {
        try {
            return this.isElementPresent(By.xpath(CONFIRM_REMOVAL_CHECKBOX_XPATH));
        } catch(Exception e) {
            return false;
        }
    }

    public void clickConfirmRemovalCheckbox() {
        confirmRemovalCheckbox.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isSaveButtonClickable() {
        return this.isElementClickable(By.xpath(SAVE_BUTTON_XPATH));
    }

}
