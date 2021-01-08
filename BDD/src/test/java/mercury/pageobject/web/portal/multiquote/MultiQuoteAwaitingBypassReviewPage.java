package mercury.pageobject.web.portal.multiquote;

import static mercury.helpers.POHelper.scrollTo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import mercury.pageobject.web.portal.fundingrequests.FundingRequestsAwaitingApprovalPage;
import mercury.pageobject.web.portal.quotes.QuoteJobEdit;

public class MultiQuoteAwaitingBypassReviewPage extends Base_Page<MultiQuoteAwaitingBypassReviewPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Multi Quotes Awaiting Bypass Review";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String MULTI_QUOTE_BYPASS_SECTION_XPATH = "//h3[contains(text(), 'Multi-Quote Bypass')]/..//div[@class='resource-quote-detail__resource-quote-info']";
    private static final String MULTI_QUOTE_BYPASS_FIELD_XPATH = "//label[contains(text(), '%s')]/following-sibling::div";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='datatable-default_wrapper']";
    private static final String GRID_CSS = PAGE_MAIN_CONTENT + " #datatable-default_wrapper";
    private static final String GRID_FILTER_CSS  = GRID_CSS + " #datatable-default_filter";
    private static final String GRID_SEARCH_BOX_CSS = GRID_FILTER_CSS + " input[type='search']";

    //Table rows
    private static final String TABLE_BODY_XPATH = GRID_XPATH + "//table/tbody";

    private static final String TABLE_ROW_JOB_XPATH =  TABLE_BODY_XPATH + "/descendant::tr[contains(@data-fault-id,'%s')]";
    private static final String TABLE_ROW_JOB_EDIT_QUOTE_BUTTON = TABLE_ROW_JOB_XPATH + "//a[contains(text(), 'Edit Job')]";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + " a.btn.btn-secondary.btn-neutral";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = GRID_SEARCH_BOX_CSS)
    private WebElement searchBox;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;

    // Page methods
    public MultiQuoteAwaitingBypassReviewPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
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

    public String getPageHeaderText() {
        return pageHeaderTest.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }

    // Page Interactions
    public MultiQuoteAwaitingBypassReviewPage searchJobs(String searchQuery) {
        POHelper.sendKeys(searchBox, searchQuery);
        waitForLoadingToComplete();
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

    public FundingRequestsAwaitingApprovalPage OpenJob(String jobReference) throws InterruptedException {
        By by = By.xpath(String.format(TABLE_ROW_JOB_XPATH, jobReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        POHelper.clickJavascript(by);
        return PageFactory.initElements(driver, FundingRequestsAwaitingApprovalPage.class).get();
    }

    public boolean isSearchJobDisplayed() {
        return isElementPresent(By.cssSelector(GRID_SEARCH_BOX_CSS));
    }

    public MultiQuoteAwaitingBypassReviewPage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        GridHelper.tableSort(GRID_XPATH, header, isAscending);
        return this;
    }

    public boolean isAscending(String column) {
        return GridHelper.isAscending(GRID_XPATH, column);
    }

    public String getMultiQuoteByPassFieldValue(String fieldName) {
        WebElement field = driver.findElement(By.xpath(String.format(MULTI_QUOTE_BYPASS_FIELD_XPATH, fieldName)));
        return field.getText();
    }
}
