package mercury.pageobject.web.admin.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class OnCallSchedulerAdminPage extends Base_Page<OnCallSchedulerAdminPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "On Call Scheduler Admin";

    private static final String ADMIN_ACTION_XPATH = "//div[@class='admin-action']";

    private static final String ADMIN_ACTION_CONTENT_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__content']";

    private static final String ON_CALL_SCHEDULER_ADMIN_HEADER_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[@class='admin-action__sub-header']//h2[contains(text(),'" + PAGE_TITLE + "')]";

    private static final String ADMIN_ACTION_SUB_CONTENT_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[@class='admin-action__sub-content']";

    private static final String SUB_CONTENT_SECTION_XPATH = ADMIN_ACTION_SUB_CONTENT_XPATH + "//h2[contains(text(), '%s')]";
    private static final String START_OF_THE_WEEK_DROP_DOWN_XPATH = ADMIN_ACTION_SUB_CONTENT_XPATH + "//label[contains(text(), 'Start Of The Week')]/..//span[@role='listbox']";
    private static final String START_OF_THE_WEEK_DROP_DOWN_OPTIONS_XPATH = "//ul[@id='on-call-scheduler-week-start_listbox']/li";
    private static final String START_OF_THE_WEEK_DEFAULT_OPTION_XPATH = START_OF_THE_WEEK_DROP_DOWN_XPATH + "//span[contains(@class, 'k-input')]";
    private static final String SAVE_CHANGES_BUTTON_XPATH = ADMIN_ACTION_SUB_CONTENT_XPATH + "//button/span";
    private static final String OUT_OF_HOURS_TIMES_GRID_XPATH = ADMIN_ACTION_SUB_CONTENT_XPATH + "//div[@id='days-of-week-grid']";
    private static final String OUT_OF_HOURS_EDIT_BUTTON_FOR_THE_DAY_XPATH = OUT_OF_HOURS_TIMES_GRID_XPATH + "//td/span[contains(text(), '%s')]/../following-sibling::td/a[contains(@class, 'button')]";
    private static final String OUT_OF_HOURS_UPDATE_BUTTON_FOR_THE_DAY_XPATH = OUT_OF_HOURS_TIMES_GRID_XPATH + "//td/span[contains(text(), '%s')]/../following-sibling::td/a[contains(@class, 'update')]";

    private static final String OUT_OF_HOURS_START_TIME_TIME_PICKER_XPATH = OUT_OF_HOURS_TIMES_GRID_XPATH + "//td/span[contains(text(), '%s')]/../following-sibling::td[@data-container-for='startTime']//span[contains(@class, 'k-i-clock')]";
    private static final String OUT_OF_HOURS_END_TIME_TIME_PICKER_XPATH = OUT_OF_HOURS_TIMES_GRID_XPATH + "//td/span[contains(text(), '%s')]/../following-sibling::td[@data-container-for='endTime']//span[contains(@class, 'k-i-clock')]";


    @FindBy(xpath = START_OF_THE_WEEK_DROP_DOWN_XPATH)
    private WebElement startOfTheWeekDropdown;

    @FindBy(xpath = SAVE_CHANGES_BUTTON_XPATH)
    private WebElement saveChanges;

    public OnCallSchedulerAdminPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ON_CALL_SCHEDULER_ADMIN_HEADER_XPATH));
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isSectionDisplayed(String section) {
        return isElementPresent(By.xpath(String.format(SUB_CONTENT_SECTION_XPATH, section)));
    }

    public String getStartOfTheWeekDropdownSelectedOption() {
        return driver.findElement(By.xpath(START_OF_THE_WEEK_DEFAULT_OPTION_XPATH)).getText();
    }

    public List<String> getStartOfTheDayDropdownOptions() {
        startOfTheWeekDropdown.click();
        waitForAngularRequestsToFinish();
        List<String> dropDownOptions = new ArrayList<>();
        List<WebElement> options = driver.findElements(By.xpath(START_OF_THE_WEEK_DROP_DOWN_OPTIONS_XPATH));
        for (WebElement option : options) {
            dropDownOptions.add(option.getText());
        }
        return dropDownOptions;

    }

    public boolean isSaveChangesButtonDisplayed() {
        return isElementPresent(By.xpath(SAVE_CHANGES_BUTTON_XPATH));
    }

    public Grid getGrid() {
        return GridHelper.getGrid(OUT_OF_HOURS_TIMES_GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(OUT_OF_HOURS_TIMES_GRID_XPATH, 1);
    }

    public boolean isSaveChangesEnabled() {
        return "enabled".equals(saveChanges.getAttribute("disabled"));
    }

    public void clickEditButtonForTheDay(String day) {
        WebElement editButton = driver.findElement(By.xpath(String.format(OUT_OF_HOURS_EDIT_BUTTON_FOR_THE_DAY_XPATH, day)));
        editButton.click();
        waitForAngularRequestsToFinish();
    }

    public void searchAndSelectRandomStartOfTheWeekDay(String day) {
        startOfTheWeekDropdown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectExactVisibleDropdownOption(day);
    }

    public void clickSaveChangesButton() {
        saveChanges.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isStartTimeDropdownDisplayedForTheDay(String day) {
        return isElementPresent(By.xpath(String.format(OUT_OF_HOURS_START_TIME_TIME_PICKER_XPATH, day)));
    }

    public boolean isEndTimeDropdownDisplayedForTheDay(String day) {
        return isElementPresent(By.xpath(String.format(OUT_OF_HOURS_END_TIME_TIME_PICKER_XPATH, day)));
    }

    public String selectRandomStartTime(String day) {
        WebElement startTime = driver.findElement(By.xpath(String.format(OUT_OF_HOURS_START_TIME_TIME_PICKER_XPATH, day)));
        startTime.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String selectRandomEndTime(String day) {
        WebElement endTime = driver.findElement(By.xpath(String.format(OUT_OF_HOURS_END_TIME_TIME_PICKER_XPATH, day)));
        endTime.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public void clickUpdateButtonForTheDay(String day) {
        WebElement updateButton = driver.findElement(By.xpath(String.format(OUT_OF_HOURS_UPDATE_BUTTON_FOR_THE_DAY_XPATH, day)));
        updateButton.click();
        waitForAngularRequestsToFinish();
    }

}