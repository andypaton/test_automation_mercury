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

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class OpenQuoteRequestsPage extends Base_Page<OpenQuoteRequestsPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Open Quote Requests";

    //Content
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

    //Table rows
    private static final String TABLE_BODY_XPATH = GRID_XPATH + "//table/tbody";

    private static final String TABLE_ROW_JOB_XPATH =  TABLE_BODY_XPATH + "/descendant::tr[contains(@data-fault-id,'%s')]";
    private static final String TABLE_ROW_JOB_REJECT_BUTTON = TABLE_ROW_JOB_XPATH + "//button[contains(@class, 'cancel')]";
    private static final String TABLE_ROW_JOB_APPROVE_BUTTON = TABLE_ROW_JOB_XPATH + "//a[contains(text(), 'Approve')]";
    private static final String TABLE_ROW_JOB_LINK_XPATH = TABLE_BODY_XPATH + "/descendant::tr[contains(@data-fault-id,'%s') and contains(@class,'qas-cannot-click')]";


    @FindBy(css = GRID_SEARCH_BOX_CSS)
    private WebElement searchQuery;

    public OpenQuoteRequestsPage(WebDriver driver) {
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
    public OpenQuoteRequestsPage searchJobs(String searchQuery) {
        waitForAnimation();
        this.searchQuery.sendKeys(searchQuery);
        this.searchQuery.sendKeys(Keys.RETURN);
        waitForAnimation();
        return this;
    }


    public String getTopJobReference() {
        waitForAnimation();
        By by = By.xpath(GRID_XPATH + "/descendant::td[1]"); // /div[2]/table/tbody/tr[3]/
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

    public QuoteJobEdit jobAwaitingQuote(Integer jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_JOB_APPROVE_BUTTON, jobReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        return PageFactory.initElements(driver, QuoteJobEdit.class).get();
    }

    public RejectQuoteRequest rejectQuoteRequest(Integer jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_JOB_REJECT_BUTTON, jobReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        return PageFactory.initElements(driver, RejectQuoteRequest.class).get();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(GRID_XPATH, 1);
    }

    public Boolean isSearchBoxDisplayed() {
        return isElementVisible(By.cssSelector(GRID_SEARCH_BOX_CSS));
    }

    public OpenQuoteRequestsPage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        GridHelper.tableSort(GRID_XPATH, header, isAscending);
        return this;
    }

    public boolean isAscending(String column) {
        return GridHelper.isAscending(GRID_XPATH, column);
    }

    public boolean IsclickFunctinalityAvailable(Integer jobReference) {
        return isElementPresent(By.xpath(String.format(TABLE_ROW_JOB_LINK_XPATH, jobReference)));
    }
}
