package mercury.pageobject.web.portal.invoices;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class ConsolidatedInvoicesInProgressPage extends Base_Page<ConsolidatedInvoicesInProgressPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Consolidated Invoices In Progress";

    //Main content
    private static final String CONSOLIDATED_INVOICES_DETAIL_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String CONSOLIDATED_INVOICES_IN_PROGRESS_HEADER_XPATH = CONSOLIDATED_INVOICES_DETAIL_CONTAINER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";
    private static final String SEARCHBOX_XPATH = ".//label[contains(text(), 'Search')]//input";


    //Table
    private static final String CONSOLIDATED_INVOICES_GRID_XPATH = ".//table[@id='consolidated-invoice-in-progress']";

    //Table rows
    private static final String TABLE_ROW_TYPE_XPATH_SUFFIX =  "//td[contains(text(),'%s')]";

    //Grid
    private static final String GRID_XPATH = ".//div[@id='consolidated-invoice-in-progress_wrapper']";
    private static final String TABLE_FOOTER_XPATH = GRID_XPATH + "//div[@class='table-footer']";
    private static final String TABLE_INFO_XPATH = CONSOLIDATED_INVOICES_DETAIL_CONTAINER_XPATH + "//div[@class='dataTables_info' and contains(@id, 'invoice-in-progress_info')]";

    //Buttons
    private static final String BACK_BUTTON_XPATH = "//input[@type='button' and @id='back']";


    @FindBy(xpath = SEARCHBOX_XPATH)
    private WebElement searchBox;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement backButton;

    @FindBy(xpath = TABLE_INFO_XPATH)
    private WebElement tableInfo;

    @FindBy(xpath = TABLE_FOOTER_XPATH)
    private WebElement tableFooter;

    public ConsolidatedInvoicesInProgressPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(CONSOLIDATED_INVOICES_IN_PROGRESS_HEADER_XPATH));
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
        return this.isElementPresent(By.xpath(CONSOLIDATED_INVOICES_IN_PROGRESS_HEADER_XPATH));
    }

    public ConsolidatedInvoicesInProgressPage searchInvoices(String searchQuery) {
        this.searchBox.sendKeys(searchQuery);
        waitForAngularRequestsToFinish();
        return this;
    }

    public void openConsolidatedInvoiceInProgress(String invoiceNumber) throws InterruptedException {
        By by = By.xpath(CONSOLIDATED_INVOICES_GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, invoiceNumber));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Integer getTotalRows(int index) {
        ArrayList<Integer> pageInfo = new ArrayList<Integer>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(tableInfo.getText().replace(",", ""));
        while (m.find()) {
            pageInfo.add(Integer.valueOf(m.group()));
        }
        return pageInfo.get(index);
    }

    public Boolean isBackButtonDisplayed() {
        return isElementPresent(By.xpath(BACK_BUTTON_XPATH));
    }
}
