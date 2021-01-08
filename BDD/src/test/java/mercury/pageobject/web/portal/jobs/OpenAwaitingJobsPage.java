package mercury.pageobject.web.portal.jobs;

import static mercury.helpers.POHelper.clickJavascript;
import static mercury.helpers.POHelper.scrollTo;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.portal.ppm.PPMJobDetailsPage;

public class OpenAwaitingJobsPage extends Base_Page<OpenAwaitingJobsPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Open or Awaiting Jobs Grid";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";

    //Table
    protected static final String JOBS_GRID_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@id='JobsGrid']";
    private static final String JOBS_GRID_HEADER_XPATH = ".//*[@id='JobsGrid']/div[1]/div";

    //Table search
    private static final String SEARCH_QUERY_ID = "SearchQuery";

    //Table Header Row
    private static final String TABLE_HEADER_ROW_XPATH = "//th[contains(@data-title, '%s')]";

    //Table rows
    private static final String TABLE_ROW_XPATH = JOBS_GRID_XPATH +  "//tbody/tr/td[contains(.,'%s')]";
    private static final String TABLE_BODY = JOBS_GRID_XPATH + "/div[2]/table/tbody";
    private static final String TABLE_ROW_PPM_XPATH =  TABLE_ROW_XPATH + "/descendant::span[contains(text(),'PPM')]";

    private static final String CELL_SECOND_ROW_XPATH_SUFFIX = "/span[contains(@class,'second-row')]";

    private static final String UPDATE_ETA_BUTTON_XPATH = TABLE_ROW_XPATH +  "/..//button[contains(text(), 'Update ETA')]";

    //Table pager
    private static final String TABLE_PAGER_XPATH = ".//div[@id='JobsGrid']/div[3]";
    private static final String TABLE_SUMMARY_XPATH_SUFFIX = "/span";
    private static final String PAGE_LINK_XPATH_SUFFIX = "/ul/descendant::*[contains(text(),'%s')]";
    private static final String NEXT_PAGE_CSS = "#JobsGrid > div.k-pager-wrap.k-grid-pager.k-widget.k-floatwrap > a:nth-child(4) > span";

    private static final String NO_DATA_XPATH = JOBS_GRID_XPATH + "/following-sibling::div[@id = 'NoData']";


    @FindBy(xpath = JOBS_GRID_XPATH)
    private WebElement jobsGrid;

    @FindBy(xpath = TABLE_PAGER_XPATH + TABLE_SUMMARY_XPATH_SUFFIX)
    private WebElement tablePageSummary;

    @FindBy(xpath = TABLE_BODY )
    private WebElement tableBody;

    @FindBy(css = NEXT_PAGE_CSS)
    private WebElement nextPageButton;

    @FindBy(id = SEARCH_QUERY_ID)
    private WebElement searchBox;


    //JobsGrid
    public OpenAwaitingJobsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded();
            POHelper.isElementPresent(By.xpath(JOBS_GRID_XPATH));
            waitForAngularRequestsToFinish();
            logger.info(PAGE_TITLE + " isloaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(JOBS_GRID_HEADER_XPATH));
    }

    public boolean isSummaryRowDisplayed(String jobReference) {
        waitForLoadingToComplete();
        By by = By.xpath(String.format(TABLE_ROW_XPATH, jobReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        return driver.findElement(by).isDisplayed();
    }

    public JobDetailsPage openJob(String jobReference) throws InterruptedException {
        By by = By.xpath(String.format(TABLE_ROW_XPATH, jobReference));
        retryingClick(by);
        return PageFactory.initElements(driver, JobDetailsPage.class).get();
    }

    public PPMJobDetailsPage openPPMJob(String jobReference) throws InterruptedException {
        waitForLoadingToComplete();
        waitForAnimation();
        By by = By.xpath(String.format(TABLE_ROW_PPM_XPATH, jobReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsClickable(by);
        // For some reason the waits above do not capture this element still being clickable
        waitUntilElementNotDisplayed(By.xpath(".//*[@class='k-loading-image']"));
        driver.findElement(by).click();
        return PageFactory.initElements(driver, PPMJobDetailsPage.class).get();
    }

    public void openOpenJob(String jobReference) throws InterruptedException {
        By by = By.xpath(String.format(TABLE_ROW_XPATH, jobReference));
        WebElement we = driver.findElement(by);
        String pageSource = driver.getPageSource();
        we = driver.findElement(by);
        we.click();
        waitForAngularRequestsToFinish();
        waitUntilPageSourceUpdated(pageSource);
    }

    public PortalUpdateJobETA openAllocatedJob(String jobReference) throws InterruptedException {
        By by = By.xpath(String.format(UPDATE_ETA_BUTTON_XPATH, jobReference));
        retryingClick(by);
        return PageFactory.initElements(driver, PortalUpdateJobETA.class).get();
    }

    public Boolean isTableSummaryPageInfoDisplayed() {
        return tablePageSummary.isDisplayed();
    }

    public Integer minimumJobInPager() {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(tablePageSummary.getText());
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo.get(0);
    }

    public Integer maximumJobInPager() {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(tablePageSummary.getText());
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo.get(1);
    }

    public Integer numberOfJobsInPager() {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(tablePageSummary.getText());
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo.get(2);
    }

    public Integer getDisplayedRows() {
        List<WebElement> tableRows = tableBody.findElements(By.tagName("tr"));
        //List<WebElement> tableRows = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(tableBody, By.tagName("tr")));
        return tableRows.size();
    }

    public OpenAwaitingJobsPage tableSort(String columnDataField) {
        By by = By.xpath(String.format(TABLE_HEADER_ROW_XPATH, columnDataField));
        driver.findElement(by).click();
        return this;
    }

    public String getJobRowJobReference() {
        return null;
    }

    private String getGenericTopRowString(String jobReference, Integer columnNumber) {
        By by = By.xpath( String.format(TABLE_ROW_XPATH, jobReference) + "/following-sibling::td[" + columnNumber +"]");
        POHelper.isLoaded()
        .isAngularFinishedProcessing()
        .isFluentElementIsVisible(by);
        // getText() did not fully return the innerText if the first line was blank resulting in switching to innerText.
        String returnValue[] = driver.findElement(by).getAttribute("innerText").split("\n");
        if(returnValue.length>0) {
            return returnValue[0].trim();
        } else {
            return "";
        }
    }

    private String getGenericSecondRowString(String jobReference, Integer columnNumber) {
        By by = By.xpath( String.format(TABLE_ROW_XPATH, jobReference) + "/following-sibling::td[" + columnNumber +"]" + CELL_SECOND_ROW_XPATH_SUFFIX);
        POHelper.isLoaded()
        .isAngularFinishedProcessing()
        .isFluentElementIsVisible(by);
        return driver.findElement(by).getText();
    }

    public String getJobRowJobtype(String jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_XPATH, jobReference)+CELL_SECOND_ROW_XPATH_SUFFIX);
        return driver.findElement(by).getText();
    }

    public String getJobRowAssignmentStatus(String jobReference) {
        return getGenericTopRowString(jobReference,1);
    }

    public String getJobRowPriority(String jobReference) {
        return getGenericTopRowString(jobReference,4);
    }

    public String getJobRowSite(String jobReference)  {
        By by = By.xpath( String.format(TABLE_ROW_XPATH, jobReference) + "/../following-sibling::tr/td[2]/div");
        return driver.findElement(by).getText();
    }

    public String getJobRowAssetSubTypeClassification(String jobReference) {
        return getGenericTopRowString(jobReference,2);
    }

    public String getJobRowFaultType(String jobReference)  {
        return getGenericSecondRowString(jobReference, 2);
    }

    public String getJobRowDaysOutstanding(String jobReference) {
        return getGenericTopRowString(jobReference,6);
    }

    public String getJobRowLoggedDate(String jobReference) {
        return getGenericSecondRowString(jobReference, 6);
    }

    public String getJobRowReference(String jobReference) {
        return getGenericTopRowString(jobReference,7);
    }

    public String getJobRowReferenceValue(String jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_XPATH, jobReference) + "/following-sibling::td[7]" + CELL_SECOND_ROW_XPATH_SUFFIX);
        if (this.isElementPresent(by)) {
            return driver.findElement(by).getText();
        } else {
            return null;
        }
    }

    public String getjobRowETADate(String jobReference) {
        String etaDate;
        By by = By.xpath(String.format(TABLE_ROW_XPATH, jobReference) + "/following-sibling::td[8]" + "/span");
        if (this.isElementPresent(by)) {
            etaDate = getGenericTopRowString(jobReference,8);
            if ("update eta".equalsIgnoreCase(etaDate)) {
                return "";
            } else {
                return etaDate;
            }
        } else {
            return "";
        }
    }

    public String getjobRowETATime(String jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_XPATH, jobReference) + "/following-sibling::td[8]");
        return driver.findElement(by).getText().split("\n")[1].replace("Update ETA", "");
    }

    public String getJobRowDescription(String jobReference) {
        By by = By.xpath( String.format(TABLE_ROW_XPATH, jobReference) + "/../following-sibling::tr/td[2]/div");
        return driver.findElement(by).getText();
    }

    public OpenAwaitingJobsPage gotoPage(Integer pageNumber) {
        By by = By.xpath(TABLE_PAGER_XPATH + String.format(PAGE_LINK_XPATH_SUFFIX, pageNumber));
        if(this.isElementClickable(by)) {
            WebElement pageLink = driver.findElement(by);
            Actions action = new Actions(driver);
            action.moveToElement(pageLink).build().perform();
            driver.findElement(by).click();
        }
        return this;
    }

    public OpenAwaitingJobsPage gotoNextPage() {
        this.nextPageButton.click();
        isLoaded();
        return this;
    }

    public String getTopJobReference() {
        waitForAnimation();
        By by = By.xpath(JOBS_GRID_XPATH + "/descendant::td[2]"); // /div[2]/table/tbody/tr[3]/
        POHelper.isLoaded()
        .isAngularFinishedProcessing()
        .isFluentElementIsVisible(by);
        String returnValue[] = driver.findElement(by).getAttribute("innerText").split("\n");
        if(returnValue.length>0) {
            return returnValue[0].trim();
        } else {
            return "";
        }
    }

    public OpenAwaitingJobsPage searchJobs(String searchQuery) throws InterruptedException {
        searchBox.clear();
        POHelper.sendKeys(searchBox, searchQuery);
        if (!searchQuery.isEmpty()) {
            waitForElement(By.xpath(String.format(TABLE_ROW_XPATH, searchQuery)), State.ELEMENT_IS_VISIBLE);
        }
        POHelper.waitForStability();
        return this;
    }

    /**
     * Search for string but dont wait for it to be displayed
     * @param searchQuery
     * @return
     */
    public OpenAwaitingJobsPage searchJobsNoWait(String searchQuery) {
        searchBox.clear();

        POHelper.sendKeys(searchBox, searchQuery);
        searchBox.sendKeys(Keys.TAB);

        waitForAngularRequestsToFinish();
        POHelper.waitForKendoLoadingToComplete();
        return this;
    }

    public Grid getJobsGrid() {
        waitForAngularRequestsToFinish();
        return GridHelper.getGrid(JOBS_GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(JOBS_GRID_XPATH, 1);
    }

    public Grid getJobsGrid(int maxNumRows) {
        waitForAngularRequestsToFinish();
        return GridHelper.getGrid(JOBS_GRID_XPATH, maxNumRows);
    }

    public OpenAwaitingJobsPage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        clickJavascript(getWebDriver().findElement(By.xpath(String.format(JOBS_GRID_XPATH + TABLE_HEADER_ROW_XPATH, header))));
        if (isAscending != isAscending(header)) {
            clickJavascript(getWebDriver().findElement(By.xpath(String.format(JOBS_GRID_XPATH + TABLE_HEADER_ROW_XPATH, header))));
        }
        POHelper.waitForStability();
        POHelper.waitForKendoLoadingToComplete();
        return this;
    }

    /*
     * return:
     *      true if ascending
     *      false if descending
     *      null if not sorted
     */
    public Boolean isAscending(String column) {
        if (getWebDriver().findElement(By.xpath(String.format(JOBS_GRID_XPATH + TABLE_HEADER_ROW_XPATH, column))).getAttribute("aria-sort").contains("ascending")) {
            return true; // ascending
        } else if (getWebDriver().findElement(By.xpath(String.format(JOBS_GRID_XPATH + TABLE_HEADER_ROW_XPATH, column))).getAttribute("aria-sort").contains("descending")) {
            return false; // descending
        } else {
            return null; // not sorted
        }
    }

    public boolean isNoData() {
        return isElementPresent(By.xpath(NO_DATA_XPATH));
    }
}
