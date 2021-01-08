package mercury.pageobject.web.portal.quotes;

import static mercury.helpers.POHelper.scrollTo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;

public class ManagerJobsAwaitingQuotePage extends Base_Page<ManagerJobsAwaitingQuotePage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Jobs Awaiting Quote";

    //Content
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='datatable-default_wrapper']";
    private static final String GRID_CSS = PAGE_MAIN_CONTENT + " #datatable-default_wrapper";
    private static final String GRID_FILTER_CSS  = GRID_CSS + " #datatable-default_filter";
    private static final String GRID_SEARCH_BOX_CSS = GRID_FILTER_CSS + " input[type='search']";
    
    private static final String SCOPE_OF_WORKS_XPATH = PAGE_BODY_CONTENT_XPATH + "//form[@id='EditQuoteJobForm']//textarea[@id='ScopeOfWorks']";

    //Table rows
    private static final String TABLE_BODY_XPATH = GRID_XPATH + "//table/tbody";

    private static final String TABLE_ROW_JOB_XPATH =  TABLE_BODY_XPATH + "/descendant::td[contains(text(),'%s')]/parent::tr";
    private static final String TABLE_ROW_JOB_EDIT_QUOTE_BUTTON = TABLE_ROW_JOB_XPATH + "//a[contains(text(), 'Edit')]";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + " a.btn.btn-secondary.btn-neutral";
    private static final String TABLE_ROW_JOB_LINK_XPATH = TABLE_BODY_XPATH + "/descendant::tr[contains(@data-fault-id,'%s') and contains(@class,'qas-can-click')]";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = GRID_SEARCH_BOX_CSS)
    private WebElement searchQuery;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;
    
    @FindBy(xpath = SCOPE_OF_WORKS_XPATH)
    private WebElement scopeOfWorks;

    public ManagerJobsAwaitingQuotePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {

            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(GRID_XPATH));
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
        return this.isElementClickable(By.xpath(GRID_XPATH));
    }

    // Page Interactions
    public ManagerJobsAwaitingQuotePage searchJobs(String searchQuery) {
        waitForAnimation();
        this.searchQuery.sendKeys(searchQuery);
        this.searchQuery.sendKeys(Keys.RETURN);
        waitForAnimation();
        return this;
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(GRID_XPATH, 1);
    }

    public QuoteJobEdit editQuoteJobRequest(Integer jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_JOB_EDIT_QUOTE_BUTTON, jobReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        return PageFactory.initElements(driver, QuoteJobEdit.class).get();
    }

    public Boolean isSearchBoxDisplayed() {
        return isElementVisible(By.cssSelector(GRID_SEARCH_BOX_CSS));
    }

    public ManagerJobsAwaitingQuotePage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        GridHelper.tableSort(GRID_XPATH, header, isAscending);
        return this;
    }

    public boolean isAscending(String column) {
        return GridHelper.isAscending(GRID_XPATH, column);
    }

    public boolean getScopeOfWorksEditState() {
        return scopeOfWorks.isEnabled();
    }

    public boolean IsclickFunctinalityAvailable(Integer jobReference) {
        return isElementPresent(By.xpath(String.format(TABLE_ROW_JOB_LINK_XPATH, jobReference)));   
}
}
