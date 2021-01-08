package mercury.pageobject.web.portal.jobs;

import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.MAX_TIMEOUT;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;
import mercury.helpers.State;

public class AllJobsPage_OpsDirector extends Base_Page<AllJobsPage_OpsDirector>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "All Jobs";

    // Page elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTENT_XPATH + "//h1[contains(text(),'" + PAGE_TITLE + "')]";

    private static final String SPLIT_RFMS_BUTTON_XPATH = "//button[contains(., 'Split RFM')]";
    private static final String SPLIT_RFMS_XPATH = "//h3[contains(., 'Jobs for ')]";

    // DatePickers
    private static final String DATEPICKER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@id='datePickers']";
    private static final String FROM_DATEPICKER_XPATH = DATEPICKER_XPATH + "//input[@id='fromdatepicker']";
    private static final String TO_DATEPICKER_XPATH = DATEPICKER_XPATH + "//input[@id='todatepicker']";
    private static final String FIND_BUTTON_XPATH = DATEPICKER_XPATH + "//button[text()='Find']";

    // Search Box
    private static final String ALL_JOBS_SEARCH_XPATH = PAGE_BODY_CONTAINER_XPATH + "//input[@id='SingleGridSearchQuery']";

    // Table
    private static final String ALL_JOBS_GRID_TABLE_XPATH = "//div[@id='SingleJobsGrid']";

    //Table Rows
    private static final String TABLE_ROW_TYPE_XPATH_SUFFIX =  "//td[contains(text(),'%s')]";

    @FindBy(xpath = FROM_DATEPICKER_XPATH)
    private WebElement fromDate;

    @FindBy(xpath = TO_DATEPICKER_XPATH)
    private WebElement toDate;

    @FindBy(xpath = FIND_BUTTON_XPATH)
    private WebElement findButton;

    @FindBy(xpath = ALL_JOBS_SEARCH_XPATH)
    private WebElement searchBox;

    @FindBy(xpath = SPLIT_RFMS_BUTTON_XPATH)
    private WebElement splitRFMsButton;

    @FindBy(xpath = SPLIT_RFMS_XPATH)
    private List<WebElement> splitRFMs;

    public AllJobsPage_OpsDirector(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public AllJobsPage_OpsDirector searchJobs(String searchQuery) {
        searchBox.clear();
        POHelper.sendKeys(searchBox, searchQuery);
        if (searchQuery.isEmpty()) searchBox.sendKeys(Keys.RETURN);
        waitForLoadingToComplete();
        waitForAngularRequestsToFinish(MAX_SYNC_TIMEOUT);
        return this;
    }

    public void waitUntilFirstRowOfGridContains(String text) {
        driver.manage().timeouts().implicitlyWait(MAX_SYNC_TIMEOUT, TimeUnit.SECONDS);
        GridHelper.waitUntilFirstRowOfGridContains(text);
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
    }

    public void openJob(String jobReference) {
        waitForLoadingToComplete();
        List<WebElement> we = driver.findElements(By.xpath(ALL_JOBS_GRID_TABLE_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, jobReference)));
        we.get(0).click();
        waitForAngularRequestsToFinish(MAX_SYNC_TIMEOUT);
    }

    public boolean isFromDatePickerVisible() {
        return fromDate.isDisplayed();
    }

    public boolean isToDatePickerVisible() {
        return toDate.isDisplayed();
    }

    public boolean isFindButtonVisible() {
        return findButton.isDisplayed();
    }

    public String getFromDate() {
        return fromDate.getAttribute("value");
    }

    public void setFromDate(String date) {
        fromDate.clear();
        fromDate.sendKeys(date);
    }

    public void setToDate(String date) {
        toDate.clear();
        toDate.sendKeys(date);
    }

    public String getToDate() {
        return toDate.getAttribute("value");
    }

    public boolean isFindButtonEnabled() {
        return findButton.isEnabled();
    }

    public void clearFromDate() {
        fromDate.sendKeys(Keys.CONTROL + "a");
        fromDate.sendKeys(Keys.DELETE);
    }

    public void clearToDate() {
        toDate.sendKeys(Keys.CONTROL + "a");
        toDate.sendKeys(Keys.DELETE);
    }

    public void clickFindButton() {
        findButton.click();
        waitForAngularRequestsToFinish(MAX_SYNC_TIMEOUT);
    }

    public Grid getGrid() {
        waitForElement(By.xpath(ALL_JOBS_GRID_TABLE_XPATH), State.ELEMENT_IS_VISIBLE);
        return GridHelper.getGrid(ALL_JOBS_GRID_TABLE_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(ALL_JOBS_GRID_TABLE_XPATH, 1);
    }

    public Grid getGrid(int maxNumRows) {
        waitForAngularRequestsToFinish(MAX_SYNC_TIMEOUT);
        return GridHelper.getGrid(ALL_JOBS_GRID_TABLE_XPATH, maxNumRows);
    }

    public boolean isAscending(String header) {
        return GridHelper.isAscending(ALL_JOBS_GRID_TABLE_XPATH, header);
    }

    public AllJobsPage_OpsDirector tableSort(String header, boolean isAscending) {
        GridHelper.tableSort(ALL_JOBS_GRID_TABLE_XPATH, header);
        waitForAngularRequestsToFinish(MAX_SYNC_TIMEOUT);
        if (isAscending != GridHelper.isAscending(ALL_JOBS_GRID_TABLE_XPATH, header)) {
            GridHelper.tableSort(ALL_JOBS_GRID_TABLE_XPATH, header);
            waitForAngularRequestsToFinish(MAX_SYNC_TIMEOUT);
        }
        POHelper.waitForStability();
        return this;
    }

    public void clickSplitRFMs() {
        splitRFMsButton.click();
        waitForAngularRequestsToFinish(MAX_SYNC_TIMEOUT);
    }

    public List<String> getListOfSplitRFMs() {
        List<String> result = new ArrayList<>();
        for (WebElement we : splitRFMs) {
            result.add(we.getText());
        }
        return result;
    }
}
