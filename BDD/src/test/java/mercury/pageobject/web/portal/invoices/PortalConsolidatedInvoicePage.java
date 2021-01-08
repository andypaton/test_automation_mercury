package mercury.pageobject.web.portal.invoices;

import java.util.ArrayList;
import java.util.List;

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

public class PortalConsolidatedInvoicePage extends Base_Page<PortalConsolidatedInvoicePage> {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Invoice";

    //Main Content
    private static final String INVOICE_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_TITLE_XPATH = INVOICE_CONTAINER_XPATH + "//h1[contains(text(), 'Invoice')]";
    private static final String INVOICE_DETAILS_HEADER_XPATH = INVOICE_CONTAINER_XPATH + "//h3[contains(text(), 'Invoice Details')]";

    //Page elements
    private static final String INVOICE_VALIDATION_SUMMARY_XPATH = INVOICE_CONTAINER_XPATH + "//div[contains(@class,'validation-summary-errors default-top-margin')]";
    private static final String INVOICE_VALIDATION_SUMMARY_MESSAGE_XPATH = INVOICE_VALIDATION_SUMMARY_XPATH + "//ul";

    //tables
    private static final String INVOICE_DOCUMENT_TYPE_TABLE_XPATH = INVOICE_CONTAINER_XPATH + "//table[(@id='upload-docs')]";
    private static final String INVOICE_DOCUMENT_TYPE_TABLE_ROWS_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_XPATH + "//tbody/tr[@data-missing-text='Upload Required']";
    private static final String INVOICE_DOCUMENT_TYPE_INVOICE_ROW_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_ROWS_XPATH + "//td[contains(text(),'Invoice')]";

    private static final String INVOICE_DOCUMENT_TYPE_INVOICE_INACTIVE_TICK_XPATH = INVOICE_DOCUMENT_TYPE_INVOICE_ROW_XPATH + "/..//span[contains(@class,'doc-missing')]";
    private static final String INVOICE_DOCUMENT_TYPE_INVOICE_ACTIVE_TICK_XPATH = INVOICE_DOCUMENT_TYPE_INVOICE_ROW_XPATH + "/..//span[contains(@class,'doc-uploaded')]";

    private static final String INVOICE_LINES_TOTAL_NET_XPATH = INVOICE_CONTAINER_XPATH + "//span[@id='TotalLineNetValue']";

    //Grid
    private static final String INVOICE_HEADER_GRID_XPATH = INVOICE_CONTAINER_XPATH + "//table[@class='default-top-margin nonclickable'][1]";
    private static final String CONSOLIDATED_INVOICE_LINES_GRID_XPATH = INVOICE_CONTAINER_XPATH + "//table[@id='consolidated-invoice-lines']";
    private static final String INVOICE_MANAGE_LINES_GRID_XPATH = INVOICE_CONTAINER_XPATH + "//table[@id='manage-lines']";
    private static final String PPM_ORDER_LINE_XPATH = CONSOLIDATED_INVOICE_LINES_GRID_XPATH + "//td[contains(text(), '%s')]";

    //Buttons
    private static final String INVOICE_SUBMIT_INVOICE_BUTTON_XPATH = INVOICE_CONTAINER_XPATH + "//button[contains(@id, 'submit')]";
    private static final String INVOICE_BACK_BUTTON_XPATH = INVOICE_CONTAINER_XPATH + "//input[@id='back']";
    private static final String DELETE_INVOICE_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_XPATH + "//a[@data-title='Delete Invoice']";
    private static final String DELETE_JOBSHEET_XPATH = CONSOLIDATED_INVOICE_LINES_GRID_XPATH + "//td[contains(text(), '%s')]/following-sibling::td//button[contains(., 'Delete')]]";
    private static final String UPLOAD_INVOICE_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_ROWS_XPATH + "/td[contains(text(),'Invoice')]/..//td/a[contains(text(),'Upload')]";
    private static final String UPLOAD_JOBSHEET_XPATH = CONSOLIDATED_INVOICE_LINES_GRID_XPATH + "//td[contains(text(), '%s')]/following-sibling::td//button[contains(., 'Upload')]";
    private static final String EDIT_CONSOLIDATED_INVOICE_XPATH = INVOICE_CONTAINER_XPATH + "//a[contains(@class,'edit-consolidated-invoice')]";
    private static final String INVOICE_MANAGE_LINES_BUTTON_XPATH = INVOICE_MANAGE_LINES_GRID_XPATH + "//descendant::button[@id = 'consolidatedInvoiceManageLineLink']";


    @FindBy(xpath = INVOICE_LINES_TOTAL_NET_XPATH)
    private WebElement totalNetAmount;

    @FindBy(xpath = INVOICE_DETAILS_HEADER_XPATH)
    private WebElement enterInvoiceDetailsHeader;

    @FindBy(xpath = INVOICE_TITLE_XPATH)
    private WebElement invoiceTitle;

    @FindBy(xpath = INVOICE_SUBMIT_INVOICE_BUTTON_XPATH)
    private WebElement submitInvoiceButton;

    @FindBy(xpath = INVOICE_BACK_BUTTON_XPATH)
    private WebElement backButton;

    @FindBy(xpath = INVOICE_VALIDATION_SUMMARY_MESSAGE_XPATH)
    private WebElement invoiceErrorMessage;

    @FindBy(xpath = DELETE_INVOICE_XPATH)
    private WebElement deleteInvoiceLink;

    @FindBy(xpath = UPLOAD_INVOICE_XPATH)
    private WebElement uploadInvoice;

    @FindBy(xpath = EDIT_CONSOLIDATED_INVOICE_XPATH)
    private WebElement editConsolidatedInvoiceLink;

    @FindBy(xpath = INVOICE_MANAGE_LINES_BUTTON_XPATH)
    private WebElement manageLinesLink;


    public PortalConsolidatedInvoicePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(INVOICE_TITLE_XPATH));
    }

    public Grid getInvoiceHeaderGrid() {
        return GridHelper.getGrid(INVOICE_HEADER_GRID_XPATH);
    }

    public Grid getConsolidatedInvoiceLinesGrid() {
        return GridHelper.getGrid(CONSOLIDATED_INVOICE_LINES_GRID_XPATH);
    }

    public Grid getManageLinesGrid() {
        return GridHelper.getGrid(INVOICE_MANAGE_LINES_GRID_XPATH);
    }

    public String getInvoiceDetailsHeader() {
        return enterInvoiceDetailsHeader.getText();
    }

    public String getInvoiceTitle() {
        return invoiceTitle.getText();
    }

    public Boolean isSubmitInvoiceButtonDisplayed() {
        return this.isElementPresent(By.xpath(INVOICE_SUBMIT_INVOICE_BUTTON_XPATH));
    }

    public Boolean isPpmOrderDisplayed(String searchQuery) {
        WebElement we = driver.findElement(By.xpath(String.format(PPM_ORDER_LINE_XPATH, searchQuery)));
        return we.isDisplayed();
    }

    public InvoiceSubmittedPage submitInvoice() {
        submitInvoiceButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceSubmittedPage.class);
    }

    public void clickUploadJobSheetButton(String searchQuery) {
        WebElement we = driver.findElement(By.xpath(String.format(UPLOAD_JOBSHEET_XPATH, searchQuery)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void clickDeleteJobSheetButton(String searchQuery) {
        WebElement we = driver.findElement(By.xpath(String.format(DELETE_JOBSHEET_XPATH, searchQuery)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    @Override
    public Boolean isValidationSummaryDisplayed() {
        return this.isElementPresent(By.xpath(INVOICE_VALIDATION_SUMMARY_XPATH));
    }

    public String getInvoiceErrorMessage() {
        return invoiceErrorMessage.getText();
    }

    public ConsolidatedInvoicesInProgressPage clickBackButton() {
        backButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, ConsolidatedInvoicesInProgressPage.class);
    }

    public List<String> getInvoiceErrors() {
        List<String> errors = new ArrayList<>();
        List<WebElement> allErrors = driver.findElements(By.xpath(INVOICE_VALIDATION_SUMMARY_MESSAGE_XPATH));
        for (WebElement error : allErrors) {
            errors.add(error.getAttribute("innerText"));
        }
        logger.debug("visibleOptions = " + errors.toString());
        return errors;
    }

    public Boolean isDeleteJobSheetDisplayed() {
        return this.isElementPresent(By.xpath(DELETE_JOBSHEET_XPATH));
    }

    public UploadInvoiceDocumentPage selectUploadInvoice() {
        uploadInvoice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, UploadInvoiceDocumentPage.class);
    }

    public UploadInvoiceDocumentsPage clickUploadInvoice() {
        uploadInvoice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, UploadInvoiceDocumentsPage.class);
    }

    public Boolean isUploadInvoiceDisplayed() {
        return this.isElementPresent(By.xpath(UPLOAD_INVOICE_XPATH));
    }

    public Boolean isUploadJobSheetDisplayed() {
        return this.isElementPresent(By.xpath(UPLOAD_JOBSHEET_XPATH));
    }

    public Boolean isInactiveInvoiceTickDisplayed() {
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_TYPE_INVOICE_INACTIVE_TICK_XPATH));
    }

    public Boolean isActiveInvoiceTickDisplayed() {
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_TYPE_INVOICE_ACTIVE_TICK_XPATH));
    }

    public String getTotalNetAmount() {
        return totalNetAmount.getText();
    }

    public Boolean isManageLinesButtonDisplayed() {
        return this.isElementPresent(By.xpath(INVOICE_MANAGE_LINES_BUTTON_XPATH));
    }

    public void clickManageLinesLink() {
        manageLinesLink.click();
        waitForAngularRequestsToFinish();
    }
}
