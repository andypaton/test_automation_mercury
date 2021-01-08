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

public class PortalOrdersInvoicePage extends Base_Page<PortalOrdersInvoicePage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Invoice";

    //Main Content
    private static final String INVOICE_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_TITLE_XPATH = INVOICE_CONTAINER_XPATH + "//h1[contains(text(), 'Invoice')]";
    private static final String ENTER_INVOICE_DETAILS_HEADER_XPATH = INVOICE_CONTAINER_XPATH + "//h3[contains(text(), 'Enter Invoice Details')]";

    //Page elements
    private static final String INVOICE_ADD_NEW_LINE_LINK_XPATH = INVOICE_CONTAINER_XPATH + "//a[contains(@class, 'add-detail-line')]";
    private static final String INVOICE_ADD_ORDER_LINE_XPATH = INVOICE_CONTAINER_XPATH + "//a[contains(@class, 'add-order-transaction-lines')]";
    private static final String INVOICE_VALIDATION_SUMMARY_XPATH = INVOICE_CONTAINER_XPATH + "//div[contains(@class,'validation-summary-errors default-top-margin')]";
    private static final String INVOICE_VALIDATION_SUMMARY_MESSAGE_XPATH = INVOICE_VALIDATION_SUMMARY_XPATH + "//ul";

    //tables
    private static final String INVOICE_DOCUMENT_TYPE_TABLE_XPATH = INVOICE_CONTAINER_XPATH + "//table[(@id='upload-docs')]";
    private static final String INVOICE_DOCUMENT_TYPE_TABLE_ROWS_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_XPATH + "//tbody/tr[@data-missing-text='Upload Required']";
    private static final String INVOICE_DOCUMENT_TYPE_INVOICE_ROW_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_ROWS_XPATH + "//td[contains(text(),'Invoice')]";
    private static final String INVOICE_DOCUMENT_TYPE_JOBSHEET_ROW_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_ROWS_XPATH + "//td[contains(text(),'Job Sheet')]";

    private static final String INVOICE_DOCUMENT_TYPE_INVOICE_INACTIVE_TICK_XPATH = INVOICE_DOCUMENT_TYPE_INVOICE_ROW_XPATH + "/..//span[contains(@class,'doc-missing')]";
    private static final String INVOICE_DOCUMENT_TYPE_INVOICE_ACTIVE_TICK_XPATH = INVOICE_DOCUMENT_TYPE_INVOICE_ROW_XPATH + "/..//span[contains(@class,'doc-uploaded')]";
    private static final String INVOICE_DOCUMENT_TYPE_JOBSHEET_INACTIVE_TICK_XPATH = INVOICE_DOCUMENT_TYPE_JOBSHEET_ROW_XPATH + "/..//span[contains(@class,'doc-missing')]";
    private static final String INVOICE_DOCUMENT_TYPE_JOBSHEET_ACTIVE_TICK_XPATH = INVOICE_DOCUMENT_TYPE_JOBSHEET_ROW_XPATH + "/..//span[contains(@class,'doc-uploaded')]";

    private static final String INVOICE_LINES_TOTAL_NET_XPATH = INVOICE_CONTAINER_XPATH + "//span[@id='TotalLineNetValue']";
    private static final String INVOICE_LINES_TOTAL_TAX_XPATH = INVOICE_CONTAINER_XPATH + "//span[@id='TotalLineTaxValue']";

    //Grid
    private static final String INVOICE_HEADER_GRID_XPATH = INVOICE_CONTAINER_XPATH + "//a[@id='InvoiceHeaderTable']//following-sibling::table";
    private static final String INVOICE_DETAIL_LINES_GRID_XPATH = INVOICE_CONTAINER_XPATH + "//table[@id='detail-lines']";
    private static final String INVOICE_MANAGE_LINES_GRID_XPATH = INVOICE_CONTAINER_XPATH + "//table[@id='manage-lines']";
    private static final String INVOICE_MATERIALS_LINES_GRID_XPATH = INVOICE_CONTAINER_XPATH + "//table[@id='material-lines']";
    private static final String INVOICE_LABOUR_LINES_GRID_XPATH = INVOICE_CONTAINER_XPATH + "//table[@id='labor-lines']";

    //Buttons
    private static final String INVOICE_SUBMIT_INVOICE_BUTTON_XPATH = INVOICE_CONTAINER_XPATH + "//input[contains(@type, 'submit')]";
    private static final String INVOICE_BACK_BUTTON_XPATH = INVOICE_CONTAINER_XPATH + "//input[@id='back']";
    private static final String DELETE_INVOICE_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_XPATH + "//a[@data-title='Delete Invoice']";
    private static final String DELETE_JOBSHEET_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_XPATH + "//a[@data-title='Delete Job Sheet']";
    private static final String UPLOAD_INVOICE_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_ROWS_XPATH + "/td[contains(text(),'Invoice')]/..//td/a[contains(text(),'Upload')]";
    private static final String UPLOAD_JOBSHEET_XPATH = INVOICE_DOCUMENT_TYPE_TABLE_ROWS_XPATH + "/td[contains(text(),'Job Sheet')]/..//td/a[contains(text(),'Upload')]";
    private static final String INVOICE_EDIT_ORDERLINE_XPATH = INVOICE_CONTAINER_XPATH + "//a[contains(@class,'edit-detail-line')]";
    private static final String INVOICE_LINES_ACTION_EDIT_LINK_CSS = "a[class='edit-invoice']";
    private static final String INVOICE_ORDER_LINE_EDIT_BUTTON_CSS = "edit-order-line-button > button";
    private static final String INVOICE_MANAGE_LINES_BUTTON_XPATH = INVOICE_MANAGE_LINES_GRID_XPATH + "//descendant::button[@id = 'manageLineLink']";
    
    //Consolidated Invoicing Elements 
    private static final String CONSOLIDATED_INVOICING_EDIT_LINK_XPATH = INVOICE_TITLE_XPATH + "/..//div//table//td//a[@class='edit-consolidated-invoice']";


    @FindBy(xpath = INVOICE_LINES_TOTAL_NET_XPATH)
    private WebElement totalNetAmount;

    @FindBy(xpath = INVOICE_LINES_TOTAL_TAX_XPATH)
    private WebElement totalTaxAmount;

    @FindBy(xpath = ENTER_INVOICE_DETAILS_HEADER_XPATH)
    private WebElement enterInvoiceDetailsHeader;

    @FindBy(xpath = INVOICE_TITLE_XPATH)
    private WebElement invoiceTitle;

    @FindBy(xpath = INVOICE_SUBMIT_INVOICE_BUTTON_XPATH)
    private WebElement submitInvoiceButton;

    @FindBy(xpath = INVOICE_BACK_BUTTON_XPATH)
    private WebElement backButton;
    @FindBy(xpath = INVOICE_ADD_NEW_LINE_LINK_XPATH)
    private WebElement addNewInvoiceLineLink;

    @FindBy(xpath = INVOICE_ADD_ORDER_LINE_XPATH)
    private WebElement addNewOrderLineLink;

    @FindBy(xpath = INVOICE_VALIDATION_SUMMARY_MESSAGE_XPATH)
    private WebElement invoiceErrorMessage;

    @FindBy(xpath = DELETE_INVOICE_XPATH)
    private WebElement deleteInvoiceLink;

    @FindBy(xpath = DELETE_JOBSHEET_XPATH)
    private WebElement deleteJobSheetLink;

    @FindBy(xpath = UPLOAD_INVOICE_XPATH)
    private WebElement uploadInvoice;

    @FindBy(xpath = UPLOAD_JOBSHEET_XPATH)
    private WebElement uploadJobsheet;

    @FindBy(xpath = INVOICE_EDIT_ORDERLINE_XPATH)
    private WebElement editOrderLineLink;

    @FindBy(xpath = INVOICE_MANAGE_LINES_BUTTON_XPATH)
    private WebElement manageLines;
    
    @FindBy(xpath = CONSOLIDATED_INVOICING_EDIT_LINK_XPATH)
    private WebElement editLink;

    public PortalOrdersInvoicePage(WebDriver driver) {
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

    public Grid getDetailLinesGrid() {
        return GridHelper.getGrid(INVOICE_DETAIL_LINES_GRID_XPATH);
    }

    public Grid getManageLinesGrid() {
        return GridHelper.getGrid(INVOICE_MANAGE_LINES_GRID_XPATH);
    }

    public Grid getMaterialsLinesGrid() {
        return GridHelper.getGrid(INVOICE_MATERIALS_LINES_GRID_XPATH);
    }

    public Grid getLaborLinesGrid() {
        return GridHelper.getGrid(INVOICE_LABOUR_LINES_GRID_XPATH);
    }

    public String getEditLink() {
        return INVOICE_LINES_ACTION_EDIT_LINK_CSS;
    }
    
    public void clickEditLink() {
        editLink.click();
        waitForAngularRequestsToFinish();
    }

    public String getEditOrderLineButton() {
        return INVOICE_ORDER_LINE_EDIT_BUTTON_CSS;
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

    public InvoiceSubmittedPage submitInvoice() {
        submitInvoiceButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceSubmittedPage.class);
    }

    public AddNewInvoiceLineModal addNewInvoiceLine() {
        addNewInvoiceLineLink.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AddNewInvoiceLineModal.class);
    }

    public AddOrderLineToInvoiceModal addNewOrderLine() {
        POHelper.clickJavascript(addNewOrderLineLink);
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AddOrderLineToInvoiceModal.class);
    }

    @Override
    public Boolean isValidationSummaryDisplayed() {
        return this.isElementPresent(By.xpath(INVOICE_VALIDATION_SUMMARY_XPATH));
    }

    public String getInvoiceErrorMessage() {
        return invoiceErrorMessage.getText();
    }

    public OrdersAwaitingInvoicePage clickBackButton() {
        waitForAngularRequestsToFinish();
        backButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, OrdersAwaitingInvoicePage.class);
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

    public DeleteInvoiceDocModal deleteInvoice() {
        deleteInvoiceLink.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, DeleteInvoiceDocModal.class);

    }

    public DeleteInvoiceDocModal deleteJobSheet() {
        deleteJobSheetLink.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, DeleteInvoiceDocModal.class);
    }

    public Boolean isDeleteInvoiceDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(DELETE_INVOICE_XPATH));
    }

    public Boolean isDeleteJobSheetDisplayed() {
        waitForAngularRequestsToFinish();
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


    public UploadInvoiceDocumentsPage clickUploadJobSheet() {
        uploadJobsheet.click();
        return PageFactory.initElements(driver, UploadInvoiceDocumentsPage.class);
    }

    public UploadInvoiceDocumentPage selectUploadJobSheet() {
        uploadJobsheet.click();
        return PageFactory.initElements(driver, UploadInvoiceDocumentPage.class);
    }

    public Boolean isUploadInvoiceDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(UPLOAD_INVOICE_XPATH));
    }

    public Boolean isUploadJobSheetDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(UPLOAD_JOBSHEET_XPATH));
    }

    public Boolean isInactiveInvoiceTickDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_TYPE_INVOICE_INACTIVE_TICK_XPATH));
    }

    public Boolean isActiveInvoiceTickDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_TYPE_INVOICE_ACTIVE_TICK_XPATH));
    }

    public Boolean isInactiveJobSheetTickDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_TYPE_JOBSHEET_INACTIVE_TICK_XPATH));
    }

    public Boolean isActiveJobSheetTickDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_TYPE_JOBSHEET_ACTIVE_TICK_XPATH));
    }

    public Boolean isEditOrderLineLinkDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(INVOICE_EDIT_ORDERLINE_XPATH));
    }

    public EditInvoiceLineModal editInvoiceOrderLine() {
        editOrderLineLink.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, EditInvoiceLineModal.class);
    }

    public String getTotalNetAmount() {
        return totalNetAmount.getText();
    }

    public String getTotalTaxAmount() {
        return totalTaxAmount.getText();
    }

    public Boolean isManageLinesButtonDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(INVOICE_MANAGE_LINES_BUTTON_XPATH));
    }

    public void clickManageLines() {
        manageLines.click();
        waitForAngularRequestsToFinish();
    }
}
