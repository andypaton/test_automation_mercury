package mercury.pageobject.web.portal.jobs;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class PortalOpenJobsBySitePage extends Base_Page <PortalOpenJobsBySitePage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Open Jobs By Site";

    // Page elements
    private static final String HEADER_XPATH = "//h1[contains(text(),'Open Jobs By Site')]";
    private static final String OPEN_JOBS_GRID_XPATH = "//div[@class='job-list']//div[@id='jobsgrid']";
    private static final String OPEN_JOBS_GRID_HEADER_XPATH = "//*[@id='jobsgrid']/div[1]//div";
    private static final String FILTER_ROW_XPATH = OPEN_JOBS_GRID_XPATH + "//tr[@class='k-filter-row']";
    private static final String SITE_SEARCH_BAR_XPATH = FILTER_ROW_XPATH + "//input[contains(@data-text-field,'siteName')]";
    private static final String COLUMN_HEADER = OPEN_JOBS_GRID_XPATH + "//table//th[@data-title = '%s' and @data-role = 'columnsorter']";

    // Table pager
    private static final String TABLE_PAGER_XPATH = ".//div[@id='jobsgrid']//div[contains(@class,'pager')]";
    private static final String PAGE_SUMMARY_XPATH = TABLE_PAGER_XPATH + "//a[contains(@class,'refresh')]/following-sibling::span";
    private static final String NEXT_PAGE_XPATH = TABLE_PAGER_XPATH + "//a[@title='Go to the next page']//span";
    private static final String ITEMS_PER_PAGE_XPATH = TABLE_PAGER_XPATH + "//span[contains(@class,'pager-sizes')]" + DROPDOWN_SEARCH_ARROW_XPATH;

    @FindBy(xpath = HEADER_XPATH)
    private WebElement header;

    @FindBy(xpath = OPEN_JOBS_GRID_XPATH)
    private WebElement openJobsGrid;

    @FindBy(xpath = OPEN_JOBS_GRID_HEADER_XPATH)
    private WebElement gridHeader;

    @FindBy(xpath = FILTER_ROW_XPATH)
    private WebElement filterRow;

    @FindBy(xpath = SITE_SEARCH_BAR_XPATH)
    private WebElement siteSearchBox;

    @FindBy(xpath = PAGE_SUMMARY_XPATH)
    private WebElement tableSummary;

    @FindBy(xpath = NEXT_PAGE_XPATH)
    private WebElement nextPage;

    @FindBy(xpath = ITEMS_PER_PAGE_XPATH)
    private WebElement itemsPerPage;

    public PortalOpenJobsBySitePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded()
            .isFluentElementIsVisible(By.xpath(OPEN_JOBS_GRID_HEADER_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(OPEN_JOBS_GRID_HEADER_XPATH));
    }

    public Boolean isTableSummaryPageInfoDisplayed() {
        return tableSummary.isDisplayed();
    }

    public Grid getGrid() {
        waitForAngularRequestsToFinish();
        return GridHelper.getGrid(OPEN_JOBS_GRID_XPATH);
    }

    public boolean isSiteSearchBoxDisplayed() {
        return siteSearchBox.isDisplayed();
    }

    public int getNumberOfSearchBoxesDisplayed() {
        return filterRow.findElements(By.xpath("//input[@role = 'textbox']")).size();
    }

    public boolean isColumnSortable(String columnName) {
        WebElement column = driver.findElement(By.xpath(String.format(COLUMN_HEADER, columnName)));
        return column.isDisplayed();
    }

    public Integer numberOfSitesInPager(int index) {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(tableSummary.getText());
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo.get(index);
    }

    public void goToTheNextPage() {
        nextPage.click();
    }

    public void selectItemsPerPage(String option) {
        itemsPerPage.click();
        selectExactVisibleDropdownOption(option);
    }

}
