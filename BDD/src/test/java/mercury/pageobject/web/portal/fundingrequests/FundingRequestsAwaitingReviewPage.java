package mercury.pageobject.web.portal.fundingrequests;

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

public class FundingRequestsAwaitingReviewPage extends Base_Page<FundingRequestsAwaitingReviewPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Funding Requests Awaiting Review";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CSS = "div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CSS + " div.main-content";
    private static final String PAGE_MAIN_CONTENT_XPATH = "//div[contains(@class,'main-content')]";
    private static final String PAGE_HEADER_XPATH = "//div[contains(@class,'body-content')]//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='datatable-default_wrapper']";
    private static final String GRID_CSS = " #datatable-default_wrapper";
    private static final String GRID_FILTER_CSS  = GRID_CSS + " #datatable-default_filter";
    private static final String GRID_SEARCH_BOX_CSS = GRID_FILTER_CSS + " input[type='search']";

    //Table rows
    private static final String TABLE_ROW_TYPE_XPATH_SUFFIX =  "/descendant::td[contains(text(),'%s')]";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + "a.btn.btn-secondary.btn-neutral";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = GRID_SEARCH_BOX_CSS)
    private WebElement searchBox;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;

    // Page methods
    public FundingRequestsAwaitingReviewPage(WebDriver driver) {
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
    public FundingRequestsAwaitingReviewPage searchJobs(String searchQuery) {
        POHelper.sendKeys(searchBox, searchQuery);
        waitForLoadingToComplete();
        waitForAnimation();
        return this;
    }

    public FundingRequestsAwaitingApprovalPage OpenJob(String jobReference) throws InterruptedException {
        By by = By.xpath(GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, jobReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        POHelper.clickJavascript(by);
        return PageFactory.initElements(driver, FundingRequestsAwaitingApprovalPage.class).get();
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

    public Boolean isSearchBoxDisplayed() {
        return isElementVisible(By.cssSelector(GRID_SEARCH_BOX_CSS));
    }

    public FundingRequestsAwaitingReviewPage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        GridHelper.tableSort(GRID_XPATH, header, isAscending);
        return this;
    }

    public boolean isAscending(String column) {
        return GridHelper.isAscending(GRID_XPATH, column);
    }
}
