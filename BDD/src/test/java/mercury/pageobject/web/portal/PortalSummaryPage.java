package mercury.pageobject.web.portal;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.portal.jobs.OpenAwaitingJobsPage;

public class PortalSummaryPage extends Base_Page<PortalSummaryPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Portal Summary Page";

    private static final String NO_COUNT_MESSAGE_XPATH = "//div[contains(text(), 'To improve your login experience, we have removed the counts from your home page.')]";

    //Summary Table
    private static final String SUMMARY_TABLE_ID = "summary";
    private static final String SUMMARY_TABLE_XPATH = ".//table[@id='summary']";
    private static final String SUMMARY_PAGE_XPATH = "//div//h1[text()='Home']";
    
    //Dashbord Area
    private static final String DASHBOARD_AREA_JOBS_AWAITING_ACCEPTANCE_TILE_XPATH = "//div[contains(@class,'dashboard-area')]//a//span[contains(text(),'Jobs Awaiting Acceptance')]";
    private static final String SUMMARY_TILES_XPATH =  "//div[@class='dashboard-area']//a//span";
    private static final String SUMMARY_TILE_LINK_XPATH = SUMMARY_TILES_XPATH + "[contains(text(),'%s')]";
  
    //Summary rows
    private static final String SUMMARY_TOTAL_ROWS_TYPE_XPATH = SUMMARY_TABLE_XPATH + "//tbody//tr//td//a";
    private static final String SUMMARY_ROW_TYPE_XPATH_SUFFIX =  "/descendant::a[contains(text(),'%s')]";
    private static final String SUMMARY_ROW_COUNT_XPATH_SUFFIX =  "/../../td[2]";
    private static final String SUMMARY_ROW_TYPE_TEXT_XPATH = SUMMARY_TOTAL_ROWS_TYPE_XPATH + "[contains(text(),'%s')]";
    
    // Jobs Tile
    @FindBy(xpath=SUMMARY_TABLE_XPATH)
    private WebElement summaryTable;

    @FindBy(xpath = SUMMARY_TOTAL_ROWS_TYPE_XPATH)
    private List<WebElement> totalTypeRows;

    @FindBy(xpath = SUMMARY_TILES_XPATH)
    private List<WebElement> summaryTiles;

    public PortalSummaryPage(WebDriver driver) {
        super(driver);
    }
    
    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {

            POHelper.isLoaded()
            .isFluentElementIsVisible(By.xpath(SUMMARY_PAGE_XPATH));
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
        return this.isElementPresent(By.xpath(SUMMARY_PAGE_XPATH));
    }


    public boolean isSummaryRowDisplayed(String row) {
        POHelper.isLoaded().isAngularFinishedProcessing();
        Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(SUMMARY_TABLE_XPATH + String.format(SUMMARY_ROW_TYPE_XPATH_SUFFIX, row))).isDisplayed());
        return driver.findElement(By.xpath(SUMMARY_TABLE_XPATH + String.format(SUMMARY_ROW_TYPE_XPATH_SUFFIX, row))).isDisplayed();
    }


    public OpenAwaitingJobsPage openAwaitingJobs(String row) {
        POHelper.isLoaded()
        .isAngularFinishedProcessing()
        .isFluentElementIsVisible(By.xpath(SUMMARY_TABLE_XPATH + String.format(SUMMARY_ROW_TYPE_XPATH_SUFFIX, row)));
        System.out.print("okokok");
        driver.findElement(By.xpath(SUMMARY_TABLE_XPATH + String.format(SUMMARY_ROW_TYPE_XPATH_SUFFIX, row))).click();

        return PageFactory.initElements(driver, OpenAwaitingJobsPage.class).get();
    }


    public Integer getCountForType(String jobType) {
        //if visible return value otherwise return 0
        By by = By.xpath(SUMMARY_TABLE_XPATH + String.format(SUMMARY_ROW_TYPE_XPATH_SUFFIX, jobType) + SUMMARY_ROW_COUNT_XPATH_SUFFIX);
        if(this.isElementPresent(by)) {
            return  Integer.valueOf(driver.findElement(by).getText());
        } else {
            return 0;
        }
    }

    public List<String> getTypeRows() {
        List<String> rows = new ArrayList<String>();
        for (WebElement row : totalTypeRows) {
            rows.add(row.getAttribute("innerText"));
        }
        return rows;
    }
    
    public List<String> getTiles() {
        List<String> rows = new ArrayList<String>();
        for (WebElement row : summaryTiles) {
            rows.add(row.getAttribute("innerText"));
        }
        return rows;
    }

    public Grid getGrid() {
        return GridHelper.getGrid(SUMMARY_TABLE_XPATH);
    }

    public List<String> getGridAsString(){
        return GridHelper.getRowsAsString(SUMMARY_TABLE_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(SUMMARY_TABLE_XPATH, 1);
    }

    public boolean isNoCountMessageDisplayed() {
        return isElementPresent(By.xpath(NO_COUNT_MESSAGE_XPATH));
    }

    public void clickSummaryTypeLink(String type) {
        WebElement element = driver.findElement(By.xpath(String.format(SUMMARY_ROW_TYPE_TEXT_XPATH, type)));
        element.click();
        waitForAngularRequestsToFinish();
    }
    
    public void clickSummaryLinkFromTile(String type) {
        WebElement element = driver.findElement(By.xpath(String.format(SUMMARY_TILE_LINK_XPATH, type)));
        element.click();
        waitForAngularRequestsToFinish();
    }
}
