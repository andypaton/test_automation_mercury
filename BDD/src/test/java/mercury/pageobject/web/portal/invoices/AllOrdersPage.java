package mercury.pageobject.web.portal.invoices;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import mercury.pageobject.web.portal.PortalSummaryPage;

public class AllOrdersPage extends Base_Page<AllOrdersPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "All Orders";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT_CSS = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    //Grid
    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@id, 'all-orders_wrapper') and @class='dataTables_wrapper no-footer']";
    private static final String SHOW_ENTRIES_DROPDOWN_XPATH = GRID_XPATH + "//div[contains(@class, 'dataTables_length')]//select";
    private static final String GRID_SEARCH_BOX_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@type='search']";
    private static final String EMPTY_GRID_CSS = PAGE_MAIN_CONTENT_CSS + " #NoData";
    private static final String VIEW_PURCHASE_ORDER_BUTTON_XPATH = GRID_XPATH + "//td//button[text()='View Purchase Order']";

    //Table
    private static final String TABLE_HEADER_ROW_XPATH = GRID_XPATH + "//th[text()='%s']";
    private static final String TABLE_FOOTER_XPATH = GRID_XPATH + "//div[@class='table-footer']";
    private static final String PAGINATION_FIRST_BUTTON_XPATH = TABLE_FOOTER_XPATH + "//div[@class='paginators']//a[contains(@class, 'paginate_button first')]";
    private static final String PAGINATION_PREVIOUS_BUTTON_XPATH = TABLE_FOOTER_XPATH + "//div[@class='paginators']//a[contains(@class, 'paginate_button previous')]";
    private static final String PAGINATION_NEXT_BUTTON_XPATH = TABLE_FOOTER_XPATH + "//div[@class='paginators']//a[contains(@class,'paginate_button next')]";
    private static final String PAGINATION_LAST_BUTTON_XPATH = TABLE_FOOTER_XPATH + "//div[@class='paginators']//a[contains(@class,'paginate_button last')]";
    private static final String ALL_ORDERS_INFO_XPATH = TABLE_FOOTER_XPATH + "//div[contains(@id, 'all-orders_info')]";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT_CSS + " #back";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;

    @FindBy(css = EMPTY_GRID_CSS)
    private WebElement emptyGrid;

    @FindBy(xpath = GRID_SEARCH_BOX_XPATH)
    private WebElement searchBox;

    @FindBy(xpath = VIEW_PURCHASE_ORDER_BUTTON_XPATH)
    private WebElement viewPurchaseOrderButton;

    @FindBy(xpath = SHOW_ENTRIES_DROPDOWN_XPATH)
    private WebElement showEntriesDropdown;

    @FindBy(xpath = PAGINATION_FIRST_BUTTON_XPATH)
    private WebElement paginationFirst;

    @FindBy(xpath = PAGINATION_PREVIOUS_BUTTON_XPATH)
    private WebElement paginationPrevious;

    @FindBy(xpath = PAGINATION_NEXT_BUTTON_XPATH)
    private WebElement paginationNext;

    @FindBy(xpath = PAGINATION_LAST_BUTTON_XPATH)
    private WebElement paginationLast;

    @FindBy(xpath = ALL_ORDERS_INFO_XPATH)
    private WebElement allOrdersInfo;

    // Page methods
    public AllOrdersPage(WebDriver driver) {
        super(driver);
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

    public Boolean isSearchBoxDisplayed() {
        return isElementVisible(By.xpath(GRID_SEARCH_BOX_XPATH));
    }

    public Boolean isViewPurchaseOrderButtonDisplayed() {
        return viewPurchaseOrderButton.isEnabled();
    }

    public Boolean isShowEntriesDropdownDisplayed() {
        return isElementVisible(By.xpath(SHOW_ENTRIES_DROPDOWN_XPATH));
    }

    public Boolean isFirstPageButtonDisplayed() {
        return isElementVisible(By.xpath(PAGINATION_FIRST_BUTTON_XPATH));
    }

    public Boolean isPreviousPageButtonDisplayed() {
        return isElementVisible(By.xpath(PAGINATION_PREVIOUS_BUTTON_XPATH));
    }

    public Boolean isNextPageButtonDisplayed() {
        return isElementVisible(By.xpath(PAGINATION_NEXT_BUTTON_XPATH));
    }

    public Boolean isLastPageButtonDisplayed() {
        return isElementVisible(By.xpath(PAGINATION_LAST_BUTTON_XPATH));
    }

    public PortalSummaryPage clickBackButton() {
        backButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PortalSummaryPage.class).get();
    }

    // Page Interactions
    public AllOrdersPage searchJobs(String searchQuery) {
        this.searchBox.clear();
        POHelper.sendKeys(searchBox, searchQuery);
        searchBox.sendKeys(Keys.ENTER);
        waitForLoadingToComplete();
        waitForAnimation();
        return this;
    }

    public void search(String text) {
        waitForElement(By.xpath(GRID_SEARCH_BOX_XPATH), ELEMENT_IS_VISIBLE);
        searchBox.sendKeys(text);
        searchBox.sendKeys(Keys.ENTER);
        waitForAngularRequestsToFinish();
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public List<Integer> numberOfEntriesInSubmittedInvoicesAndCreditsInfo() {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(allOrdersInfo.getText());
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo;
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

    public String getSelectedShowEntriesDropdownValue() {
        WebElement showEntriesDropdown = driver.findElement(By.xpath(String.format(SHOW_ENTRIES_DROPDOWN_XPATH)));
        return getSelectSelectedText(showEntriesDropdown).trim();
    }

    public AllOrdersPage tableSort(String columnDataField) {
        By by = By.xpath(String.format(TABLE_HEADER_ROW_XPATH, columnDataField));
        driver.findElement(by).click();
        return this;
    }

    public Integer getTotalRows(int index) {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(allOrdersInfo.getText().replace(",", ""));
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo.get(index);
    }
}