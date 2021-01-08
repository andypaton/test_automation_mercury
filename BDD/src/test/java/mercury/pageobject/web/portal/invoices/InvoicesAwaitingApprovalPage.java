package mercury.pageobject.web.portal.invoices;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.POHelper.scrollTo;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.concurrent.TimeUnit;

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

public class InvoicesAwaitingApprovalPage extends Base_Page<InvoicesAwaitingApprovalPage> {

    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoices Awaiting Approval";

    //Main content
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTENT_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    //Table
    private static final String GRID_SEARCH_XPATH = PAGE_MAIN_CONTENT_XPATH + "//label[contains(text(), 'Search')]//input";
    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//h3[contains(text(), 'My Invoices')]/../..//div[@class='table-responsive']";

    //Table rows
    private static final String TABLE_ROW_TYPE_XPATH_SUFFIX =  GRID_XPATH + "/descendant::td[contains(text(),'%s')]";


    @FindBy(xpath = GRID_SEARCH_XPATH)
    private WebElement searchBox;


    public InvoicesAwaitingApprovalPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
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
        return this.isElementPresent(By.xpath(PAGE_HEADER_XPATH));
    }

    public InvoicesAwaitingApprovalPage searchInvoices(String searchQuery) {
        searchBox.clear();
        searchBox.sendKeys(searchQuery);
        searchBox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
        return this;
    }

    public InvoiceApprovalPage openInvoiceAwaitingApproval(String resourceId) throws InterruptedException {
        By by = By.xpath(String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, resourceId ));
        retryingClick(by);
        return PageFactory.initElements(driver, InvoiceApprovalPage.class);
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(GRID_XPATH, 1);
    }

    public InvoicesAwaitingApprovalPage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        GridHelper.tableSort(GRID_XPATH, header, isAscending);
        return this;
    }

    public boolean isAscending(String column) {
        return GridHelper.isAscending(GRID_XPATH , column);
    }

    /**
     * returns true if the first row is a warning (ie. red)
     * @return
     */
    public boolean isWarningRow() {
        getWebDriver().manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        boolean result = driver.findElements(By.xpath("//table//tbody/tr[contains(@class, 'warning')]")).size() > 0;
        getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return result;
    }

    public boolean isInvoiceDisplayed(String orderReference) {
        return isElementPresent(By.xpath(String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, orderReference)));
    }

}
