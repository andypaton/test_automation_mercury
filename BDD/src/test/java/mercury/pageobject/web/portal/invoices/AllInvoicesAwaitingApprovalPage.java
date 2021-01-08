package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class AllInvoicesAwaitingApprovalPage extends Base_Page<AllInvoicesAwaitingApprovalPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "All Invoices Awaiting Approval";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class,'wrapper')]//table[@role='grid' and contains(@id,'nvoice')]";
    private static final String GRID_CSS = PAGE_MAIN_CONTENT + " #all-invoices-awaiting-approval_wrapper";
    private static final String GRID_FILTER_CSS  = GRID_CSS + " #all-invoices-awaiting-approval_filter";
    private static final String GRID_SEARCH_BOX_XPATH = PAGE_MAIN_CONTENT_XPATH + "//label[contains(text(),'Search')]//input";

    private static final String EMPTY_GRID_CSS = PAGE_MAIN_CONTENT + " #NoData";

    //Table Header Row
    private static final String TABLE_HEADER_ROW_XPATH = GRID_XPATH + "//th[text()='%s']";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + " a.btn.btn-secondary.btn-neutral";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = GRID_SEARCH_BOX_XPATH)
    private WebElement searchBox;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;

    @FindBy(css = EMPTY_GRID_CSS)
    private WebElement emptyGrid;

    @FindBy(xpath = PAGE_HEADER_XPATH)
    private WebElement pageHeader;

    // Page methods

    public AllInvoicesAwaitingApprovalPage(WebDriver driver) {
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

    private void scrollToTopOfPage() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pageHeader);
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

    public Boolean isSearchBoxDisplayed() {
        return isElementPresent(By.xpath(GRID_SEARCH_BOX_XPATH));
    }

    // Page Interactions
    public AllInvoicesAwaitingApprovalPage searchJobs(String searchQuery) {
        POHelper.sendKeys(searchBox, searchQuery);
        searchBox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
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

    public String getEmptyGridMessage() {
        return emptyGrid.getText();
    }

    public AllInvoicesAwaitingApprovalPage tableSort(String columnDataField) {
        scrollToTopOfPage();
        By by = By.xpath(String.format(TABLE_HEADER_ROW_XPATH, columnDataField));
        driver.findElement(by).click();
        return this;
    }
}
