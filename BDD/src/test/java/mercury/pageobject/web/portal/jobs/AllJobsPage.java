package mercury.pageobject.web.portal.jobs;

import java.util.List;
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

public class AllJobsPage extends Base_Page<AllJobsPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "All Jobs";

    // Page elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTENT_XPATH + "//h1[contains(text(),'" + PAGE_TITLE + "')]";

    // DatePickers
    private static final String DATEPICKER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@id='datePickers']";
    private static final String FROM_DATEPICKER_XPATH = DATEPICKER_XPATH + "//input[@id='fromdatepicker']";
    private static final String TO_DATEPICKER_XPATH = DATEPICKER_XPATH + "//input[@id='todatepicker']";
    private static final String FIND_BUTTON_XPATH = DATEPICKER_XPATH + "//button[text()='Find']";

    // Search Box
    private static final String ALL_JOBS_SEARCH_XPATH = PAGE_BODY_CONTAINER_XPATH + "//label[contains(text(), 'Search')]/..//input";

    // Table
    private static final String ALL_JOBS_GRID_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@id='JobsGrid']";
    private static final String ALL_JOBS_GRID_TABLE_XPATH = ALL_JOBS_GRID_XPATH + "//table[@role='grid']";

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

    public AllJobsPage(WebDriver driver) {
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

    public AllJobsPage searchJobs(String searchQuery) {
        searchBox.clear();
        POHelper.sendKeys(searchBox, searchQuery);
        if (searchQuery.isEmpty()) searchBox.sendKeys(Keys.RETURN);
        waitForLoadingToComplete();
        waitForAngularRequestsToFinish();
        return this;
    }

    public void openJob(String jobReference) {
        waitForLoadingToComplete();
        List<WebElement> we = driver.findElements(By.xpath(ALL_JOBS_GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, jobReference )));
        we.get(0).click();
        waitForAngularRequestsToFinish();
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
        waitForAngularRequestsToFinish();
    }

    public Grid getGrid() {
        waitForElement(By.xpath(ALL_JOBS_GRID_TABLE_XPATH), State.ELEMENT_IS_VISIBLE);
        return GridHelper.getGrid(ALL_JOBS_GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(ALL_JOBS_GRID_TABLE_XPATH, 1);
    }

    public Grid getGrid(int maxNumRows) {
        waitForAngularRequestsToFinish();
        return GridHelper.getGrid(ALL_JOBS_GRID_XPATH, maxNumRows);
    }

    public boolean isAscending(String header) {
        return GridHelper.isAscending(ALL_JOBS_GRID_XPATH, header);
    }

    public AllJobsPage tableSort(String header, boolean isAscending) {
        GridHelper.tableSort(ALL_JOBS_GRID_XPATH, header);
        waitForAngularRequestsToFinish();
        if (isAscending != GridHelper.isAscending(ALL_JOBS_GRID_XPATH, header)) {
            GridHelper.tableSort(ALL_JOBS_GRID_XPATH, header);
            waitForAngularRequestsToFinish();
        }
        POHelper.waitForStability();
        return this;
    }

}
