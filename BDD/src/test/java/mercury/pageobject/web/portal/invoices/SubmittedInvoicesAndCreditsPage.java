package mercury.pageobject.web.portal.invoices;

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

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

public class SubmittedInvoicesAndCreditsPage extends Base_Page<SubmittedInvoicesAndCreditsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Submitted Invoices and Credits";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    //Form Elements
    private static final String FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form[@action='/Portal/AccountsPayable/SubmittedInvoicesAndCredits']";
    private static final String DATE_FROM_XPATH = FORM_XPATH + "//label[contains(text(), 'Date From')]/following-sibling::input";
    private static final String DATE_UNTIL_XPATH = FORM_XPATH + "//label[contains(text(), 'Date Until')]/following-sibling::input";
    private static final String DROPDOWN_SUPPLIER_XPATH = FORM_XPATH + "//label[contains(text(), 'Supplier')]/following-sibling::select[@id='SupplierId']";
    private static final String FIND_BUTTON_XPATH = FORM_XPATH + "//input[@id='ReloadInvoices']";

    //Grid
    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@id, 'submitted-invoices-and-credits_wrapper') and @class='dataTables_wrapper no-footer']";
    private static final String SHOW_ENTRIES_DROPDOWN_XPATH = GRID_XPATH + "//div[contains(@class, 'dataTables_length')]//select";
    private static final String GRID_CSS = PAGE_MAIN_CONTENT + " #ap-submitted-invoices-and-credits_wrapper";
    private static final String GRID_FILTER_CSS  = GRID_CSS + " #ap-submitted-invoices-and-credits_filter";
    private static final String GRID_SEARCH_BOX_XPATH = PAGE_MAIN_CONTENT_XPATH + " //input[@type='search']";

    private static final String EMPTY_GRID_CSS = PAGE_MAIN_CONTENT + " #NoData";
    private static final String GRID_ORDER_REF_XPATH = GRID_XPATH + "//tr[@class='clickable odd']//td[text()='%s']";

    //Table Header Row
    private static final String TABLE_HEADER_ROW_XPATH = GRID_XPATH + "//th[text()='%s']";

    private static final String TABLE_FOOTER_XPATH = GRID_XPATH + "//div[@class='table-footer']";
    private static final String SUBMITTED_INVOICES_AND_CREDITS_INFO_XPATH = TABLE_FOOTER_XPATH + "//div[@id='ap-submitted-invoices-and-credits_info']";

    private static final String PAGINATION_BUTTON_PREVIOUS_XPATH = TABLE_FOOTER_XPATH + "//div[@class='paginators']//a[contains(@class,'paginate_button previous')]";
    private static final String PAGINATION_BUTTON_FIRST_XPATH = TABLE_FOOTER_XPATH + "//div[@class='paginators']//a[contains(@class,'paginate_button first')]";
    private static final String PAGINATION_NEXT_BUTTON_XPATH = TABLE_FOOTER_XPATH + "//div[@class='paginators']//a[contains(@class,'paginate_button next')]";
    private static final String PAGINATION_BUTTON_LAST_XPATH = TABLE_FOOTER_XPATH + "//div[@class='paginators']//a[contains(@class,'paginate_button last')]";
    private static final String TABLE_INFO_SUPPLY_ONLY_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@id, 'submitted-invoices-and-credits_info')]";

    private static final String BACK_BUTTON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div//input[@type='button' and @id='back']";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement backButton;

    @FindBy(css = EMPTY_GRID_CSS)
    private WebElement emptyGrid;

    @FindBy(xpath = GRID_SEARCH_BOX_XPATH)
    private WebElement searchBox;

    @FindBy (xpath = SUBMITTED_INVOICES_AND_CREDITS_INFO_XPATH)
    private WebElement submittedInvoicesAndCreditsInfo;

    @FindBy(xpath = PAGINATION_BUTTON_PREVIOUS_XPATH)
    private WebElement paginationPrevious;

    @FindBy(xpath = PAGINATION_BUTTON_FIRST_XPATH)
    private WebElement paginationFirst;

    @FindBy(xpath = PAGINATION_NEXT_BUTTON_XPATH)
    private WebElement paginationNext;

    @FindBy(xpath = PAGINATION_BUTTON_LAST_XPATH)
    private WebElement paginationLast;

    @FindBy(xpath = TABLE_INFO_SUPPLY_ONLY_XPATH)
    private WebElement tableInfoSupplyOnly;

    // Page methods

    public SubmittedInvoicesAndCreditsPage(WebDriver driver) {
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
        return this.isElementVisible(By.xpath(PAGE_HEADER_XPATH));
    }

    public Boolean isSearchBoxDisplayed() {
        return isElementVisible(By.xpath(GRID_SEARCH_BOX_XPATH));
    }

    // Page Interactions
    public SubmittedInvoicesAndCreditsPage searchJobs(String searchQuery) {
        this.searchBox.clear();
        searchBox.sendKeys(searchQuery);
        searchBox.sendKeys(Keys.ENTER);
        waitForAngularRequestsToFinish();
        return this;
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public List<Integer> numberOfEntriesInSubmittedInvoicesAndCreditsInfo() {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(submittedInvoicesAndCreditsInfo.getText());
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

    public SubmittedInvoicesAndCreditsPage tableSort(String columnDataField) {
        By by = By.xpath(String.format(TABLE_HEADER_ROW_XPATH, columnDataField));
        driver.findElement(by).click();
        return this;
    }

    public Boolean isDateFromDisplayed() {
        return isElementVisible(By.xpath(DATE_FROM_XPATH));
    }

    public Boolean isDateUntilDisplayed() {
        return isElementVisible(By.xpath(DATE_UNTIL_XPATH));
    }

    public Boolean isSupplierDropdownDisplayed() {
        return isElementVisible(By.xpath(DROPDOWN_SUPPLIER_XPATH));
    }

    public Boolean isFindButtonDisplayed() {
        return isElementVisible(By.xpath(FIND_BUTTON_XPATH));
    }

    public void search(String text) {
        waitForElement(By.xpath(GRID_SEARCH_BOX_XPATH), ELEMENT_IS_VISIBLE);
        searchBox.sendKeys(text);
        searchBox.sendKeys(Keys.ENTER);
        waitForAngularRequestsToFinish();
    }

    public Integer getTotalRows(int index) {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(tableInfoSupplyOnly.getText().replace(",", ""));
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo.get(index);
    }
    public Boolean isShowEntriesDropdownDisplayed() {
        return isElementVisible(By.xpath(SHOW_ENTRIES_DROPDOWN_XPATH));
    }

    public boolean isFirstPageButtonDisplayed() {
        return paginationFirst.isDisplayed();
    }

    public boolean isPreviousPageButtonDisplayed() {
        return paginationPrevious.isDisplayed();
    }

    public boolean isNextPageButtonDisplayed() {
        return paginationNext.isDisplayed();
    }

    public boolean isLastPageButtonDisplayed() {
        return paginationLast.isDisplayed();
    }

    public String getSelectedShowEntriesDropdownValue() {
        WebElement showEntriesDropdown = driver.findElement(By.xpath(String.format(SHOW_ENTRIES_DROPDOWN_XPATH)));
        return getSelectSelectedText(showEntriesDropdown).trim();
    }

    public PortalSummaryPage clickBackButton() {
        backButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PortalSummaryPage.class).get();
    }

    public InvoiceViewPage clickOrderRef(String orderRef) {
        By by = By.xpath(String.format(GRID_ORDER_REF_XPATH, orderRef));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceViewPage.class).get();

    }
}
