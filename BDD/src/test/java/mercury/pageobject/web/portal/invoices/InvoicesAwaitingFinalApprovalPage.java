package mercury.pageobject.web.portal.invoices;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.POHelper.scrollTo;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class InvoicesAwaitingFinalApprovalPage extends Base_Page<InvoicesAwaitingFinalApprovalPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Invoices Awaiting Final Approval";

    // Page elements
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@id, 'invoice-awaiting-final-approval_wrapper')]";
    private static final String GRID_CSS = PAGE_MAIN_CONTENT + " #invoice-awaiting-final-approval_wrapper";
    private static final String GRID_FILTER_CSS = GRID_CSS + " #invoice-awaiting-final-approval_filter";
    private static final String GRID_SEARCH_BOX_CSS = GRID_FILTER_CSS + " input[type='search']";

    private static final String EMPTY_GRID_CSS = PAGE_MAIN_CONTENT + " #NoData";

    //Table Header Row
    private static final String TABLE_HEADER_ROW_XPATH = GRID_XPATH + "//th[text()='%s']";

    //Table rows
    private static final String TABLE_DATA_XPATH = GRID_XPATH + "//td[contains(text(), '%s')]";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + " a.btn.btn-secondary.btn-neutral";

    //WebElements
    @FindBy(css = GRID_SEARCH_BOX_CSS)
    private WebElement searchBox;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;

    @FindBy(css = EMPTY_GRID_CSS)
    private WebElement emptyGrid;

    @FindBy(xpath = PAGE_HEADER_XPATH)
    private WebElement pageHeader;

    // Page methods

    public InvoicesAwaitingFinalApprovalPage(WebDriver driver) {
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

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }

    public Boolean isSearchBoxDisplayed() {
        return isElementVisible(By.cssSelector(GRID_SEARCH_BOX_CSS));
    }

    // Page Interactions
    public InvoicesAwaitingFinalApprovalPage searchInvoices(String searchQuery) {
        searchBox.clear();
        waitForAngularRequestsToFinish();
        searchBox.sendKeys(searchQuery);
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

    public InvoicesAwaitingFinalApprovalPage tableSort(String columnDataField) {
        scrollToTopOfPage();
        By by = By.xpath(String.format(TABLE_HEADER_ROW_XPATH, columnDataField));
        driver.findElement(by).click();
        return this;
    }

    public InvoicesAwaitingFinalApprovalPage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        GridHelper.tableSort(GRID_XPATH, header, isAscending);
        return this;
    }

    public boolean isAscending(String column) {
        return GridHelper.isAscending(GRID_XPATH , column);
    }

    public InvoiceFinalApprovalPage openInvoiceAwaitingFinalApproval(String invoiceReference) throws InterruptedException {
        By by = By.xpath(String.format(TABLE_DATA_XPATH, invoiceReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceFinalApprovalPage.class).get();
    }

    public boolean isEmptyGrid() {
        boolean found;
        getWebDriver().manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        try {
            driver.findElement(By.xpath("//td[contains(text(), 'There are no invoices awaiting final approval')]"));
            found = true;
        } catch (NoSuchElementException e) {
            found = false;
        }
        getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return found;
    }

    public boolean isInvoiceDisplayed(String orderReference) {
        return isElementPresent(By.xpath(String.format(TABLE_DATA_XPATH, orderReference)));
    }

}
