package mercury.pageobject.web.portal.invoices;

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

public class InvoicesAndCreditsAwaitingReviewPage extends Base_Page<InvoicesAndCreditsAwaitingReviewPage> {

    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoices and Credits Awaiting Review";

    //Main content
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";

    private static final String INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_MAIN_CONTENT_XPATH = INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";
    private static final String INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_HEADER_TITLE_XPATH = INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    //Grid
    private static final String GRID_XPATH = INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_MAIN_CONTENT_XPATH + "//div[@id='ap-submitted-invoices-and-credits_wrapper']";

    //Table rows
    private static final String TABLE_ROW_TYPE_XPATH_SUFFIX = "//table//tr//td[contains(text(),'%s')]";

    // Table Header Row
    private static final String TABLE_HEADER_ROW_XPATH = GRID_XPATH + "//th[text()='%s']";

    private static final String SEARCHBOX_XPATH = INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_MAIN_CONTENT_XPATH + "//label[contains(text(), 'Search')]//input";


    @FindBy(xpath = SEARCHBOX_XPATH)
    private WebElement searchBox;

    public InvoicesAndCreditsAwaitingReviewPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_HEADER_TITLE_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + "isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public Boolean isPageLoaded() {
        return this.isElementPresent(By.xpath(INVOICE_AND_CREDIT_AWAITING_REVIEW_PAGE_HEADER_TITLE_XPATH));
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(GRID_XPATH, 1);
    }

    public InvoicesAndCreditsAwaitingReviewPage searchInvoices(String searchQuery) {
        searchBox.clear();
        POHelper.sendKeys(searchBox, searchQuery);
        searchBox.sendKeys(Keys.ENTER);
        waitForAngularRequestsToFinish();
        return this;
    }

    public InvoicePendingReviewPage openInvoiceAwaitingReview(String invoiceNumber) throws InterruptedException {
        By by = By.xpath(GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, invoiceNumber));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoicePendingReviewPage.class);
    }

    public InvoiceReassignApproverPage openInvoiceWithNoApprover(String invoiceNumber) throws InterruptedException {
        By by = By.xpath(GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, invoiceNumber));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceReassignApproverPage.class);
    }

    public Boolean isSearchBoxDisplayed() {
        return isElementVisible(By.xpath(SEARCHBOX_XPATH));
    }

    public Boolean isInvoiceDisplayed(String invoiceNumber) {
        return isElementPresent(By.xpath(String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, invoiceNumber)));
    }

    public InvoicesAndCreditsAwaitingReviewPage tableSort(String columnDataField) {
        By by = By.xpath(String.format(TABLE_HEADER_ROW_XPATH, columnDataField));
        driver.findElement(by).click();
        return this;
    }
}