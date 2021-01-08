package mercury.pageobject.web.portal.invoices;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class InvoiceDetailsPage extends Base_Page<InvoiceDetailsPage> {

    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice Details";

    //Main Content
    private static final String INVOICE_DETAILS_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_DETAILS_TITLE_XPATH = INVOICE_DETAILS_CONTAINER_XPATH + "//h2[contains(text(), '" + PAGE_TITLE + "')]";

    private static final String INVOICE_DOCUMENT_VIEW_XPATH = ".//div[contains(@class, 'doc-view')]";

    //Table
    private static final String INVOICE_DETAILS_TABLE_XPATH = "//table[(@id='work-order-summary')]";
    private static final String INVOICE_JOB_REF_CELL_XPATH = INVOICE_DETAILS_TABLE_XPATH + "//tr//td[2]";
    private static final String INVOICE_JOB_DATE_CELL_XPATH = INVOICE_DETAILS_TABLE_XPATH + "//tr//td[4]";
    private static final String SUPPLIER_INVOICE_JOB_REF_CELL_XPATH = INVOICE_DETAILS_TABLE_XPATH + "//tr//td";

    //Form
    private static final String INVOICE_DETAILS_FORM_XPATH = "//form[(@class = 'input-form')]";

    //Form Elements
    private static final String INVOICE_FORM_GROUP_XPATH =  INVOICE_DETAILS_FORM_XPATH + "//div[contains(@class, 'form-group')]";
    //Labels
    private static final String INVOICE_FORM_INVOICE_LABEL_TO_CONTROL_XPATH = INVOICE_FORM_GROUP_XPATH + "//strong[contains(text(), '%s')]//following-sibling::input";
    private static final String INVOICE_MISSING_INFO_ERROR_MESSAGE_XPATH = INVOICE_DETAILS_FORM_XPATH + "//div[@class='validation-summary-errors']//ul//li[contains(text(),'be greater than')]";

    //Text Boxes
    private static final String INVOICE_FORM_INVOICE_NUMBER_TEXTBOX_XPATH = INVOICE_FORM_GROUP_XPATH + "//input[(@id='InvoiceNumber')]";
    private static final String INVOICE_FORM_INVOICE_DATE_TEXTBOX_XPATH = INVOICE_FORM_GROUP_XPATH + "//input[(@id='InvoiceDate')]";
    private static final String INVOICE_FORM_LEGAL_ENTITY_TEXTBOX_XPATH = INVOICE_FORM_GROUP_XPATH + "//input[(@id='LegalEntity')]";
    private static final String INVOICE_FORM_NET_AMOUNT_TEXTBOX_XPATH = INVOICE_FORM_GROUP_XPATH + "//input[(@id='NetAmount')]";
    private static final String INVOICE_FORM_TAX_AMOUNT_TEXTBOX_XPATH = INVOICE_FORM_GROUP_XPATH + "//input[(@id='TaxAmount')]";
    private static final String INVOICE_FORM_LEGAL_ENTITY_DROP_DOWN_XPATH = INVOICE_FORM_GROUP_XPATH + "//div//button[@data-id='SelectedCompanyId']//span[contains(@class,'filter-option')]";
    private static final String INVOICE_FORM_LEGAL_ENTITY_TEXT_BOX_XPATH = INVOICE_FORM_LEGAL_ENTITY_DROP_DOWN_XPATH + "/../..//div//input[@type='text']";
    private static final String INVOICE_FORM_INVOICE_DATE_PICKER_XPATH = "//div[@id='ui-datepicker-div']//table//td//a[contains(@class,'highlight')]";
    private static final String INVOICE_FORM_LEGAL_ENTITY_DROPDOWN_OPTIONS_XPATH = "//div//button[@data-id='SelectedCompanyId']//span[contains(@class,'filter-option')]/..//..//div//ul//a";
    private static final String INVOICE_FORM_LEGAL_ENTITY_LABEL_XPATH = "//div//button[@data-id='SelectedCompanyId']//span[contains(@class,'filter-option')]/..//..//button";
    //Buttons
    private static final String INVOICE_DETAILS_SAVE_BUTTON_XPATH = INVOICE_DETAILS_FORM_XPATH + "//input[contains(@value, 'Save')]";

    //Invoice Upload Error Message
    private static final String UPLOAD_INVOICE_ERROR_MESSAGE_XPATH = "//div[contains(@class, 'validation-summary-errors')]//span[contains(text(), 'error when uploading')]";

    @FindBy(xpath = INVOICE_DOCUMENT_VIEW_XPATH)
    private WebElement invoiceDocument;

    @FindBy(xpath = INVOICE_DETAILS_TITLE_XPATH)
    private WebElement invoiceDetailsTitle;

    @FindBy(xpath = INVOICE_DETAILS_TABLE_XPATH)
    private WebElement invoiceDetailsTable;

    @FindBy(xpath = INVOICE_JOB_REF_CELL_XPATH)
    private WebElement invoiceJobRefCell;

    @FindBy(xpath = SUPPLIER_INVOICE_JOB_REF_CELL_XPATH)
    private WebElement supplierInvoiceJobRefCell;

    @FindBy(xpath = INVOICE_JOB_DATE_CELL_XPATH)
    private WebElement invoiceJobDateCell;

    @FindBy(xpath = INVOICE_FORM_INVOICE_NUMBER_TEXTBOX_XPATH)
    private WebElement invoiceNumberTextBox;

    @FindBy(xpath = INVOICE_FORM_INVOICE_DATE_TEXTBOX_XPATH)
    private WebElement invoiceDateTextBox;

    @FindBy(xpath = INVOICE_FORM_LEGAL_ENTITY_TEXTBOX_XPATH)
    private WebElement invoiceLegalEntityTextBox;

    @FindBy(xpath = INVOICE_FORM_NET_AMOUNT_TEXTBOX_XPATH)
    private WebElement invoiceNetAmountTextBox;

    @FindBy(xpath = INVOICE_FORM_TAX_AMOUNT_TEXTBOX_XPATH)
    private WebElement invoiceTaxAmountTextBox;

    @FindBy(xpath = INVOICE_DETAILS_SAVE_BUTTON_XPATH)
    private WebElement invoiceSaveButton;

    @FindBy(xpath = INVOICE_MISSING_INFO_ERROR_MESSAGE_XPATH)
    private WebElement invoiceErrorMessage;
    
    @FindBy(xpath = INVOICE_FORM_LEGAL_ENTITY_DROP_DOWN_XPATH)
    private WebElement legalEntityDropDown;
    
    @FindBy(xpath = INVOICE_FORM_INVOICE_DATE_PICKER_XPATH)
    private WebElement datePicker;
    
    @FindBy(xpath = INVOICE_FORM_LEGAL_ENTITY_TEXT_BOX_XPATH)
    private WebElement legalEntityTextBox;
    
    @FindBy(xpath = INVOICE_FORM_LEGAL_ENTITY_LABEL_XPATH)
    private WebElement legalEntityLabel;
    
    public InvoiceDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_DETAILS_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(INVOICE_DETAILS_TITLE_XPATH));
    }

    public boolean isInvoiceDocumentViewVisible() {
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_VIEW_XPATH));
    }

    public String getInvoiceDetailsTitle() {
        return invoiceDetailsTitle.getText();
    }

    public Boolean isInvoiceTableVisible() {
        return this.isElementPresent(By.xpath(INVOICE_DETAILS_TABLE_XPATH));
    }

    public boolean isUploadInvoiceErrorMessageDisplayed() {
        return this.isElementPresent(By.xpath(UPLOAD_INVOICE_ERROR_MESSAGE_XPATH));
    }

    public Integer getInvoiceJobRef() {
        return Integer.valueOf(invoiceJobRefCell.getText());
    }

    public Integer getSupplierInvoiceJobRef() {
        return Integer.valueOf(supplierInvoiceJobRefCell.getText());
    }

    public String getInvoiceNumber() {
        return invoiceNumberTextBox.getAttribute("value");
    }

    public void setInvoiceNumber(String invoiceNumber) {
        invoiceNumberTextBox.clear();
        invoiceNumberTextBox.sendKeys(invoiceNumber);
    }

    public String getInvoiceDate() {
        return invoiceDateTextBox.getAttribute("value");
    }

    public void setInvoiceDate(String invoiceDate) {
        invoiceDateTextBox.clear();
        invoiceDateTextBox.sendKeys(invoiceDate);
        // need to click off the cal control
        POHelper.clickJavascript(invoiceNumberTextBox);
        // TODO : update to interact with the cal modal popup
    }
    
    public void selectDate() {
        POHelper.clickJavascript(datePicker);
    }

    public void setInvoiceNetAmount(String amount) {
        invoiceNetAmountTextBox.clear();
        invoiceNetAmountTextBox.sendKeys(amount);
    }

    public Boolean isFieldEditable(String fieldName) {
        return this.isElementClickable(By.xpath(String.format(INVOICE_FORM_INVOICE_LABEL_TO_CONTROL_XPATH, fieldName)));
    }

    public String getInvoiceJobDate() {
        return  invoiceJobDateCell.getText();
    }

    public void selectSave() {
        invoiceSaveButton.click();
        waitForAngularRequestsToFinish();
    }

    public PortalOrdersInvoicePage clickSave() {
        invoiceSaveButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PortalOrdersInvoicePage.class);
    }

    public void setInvoiceTaxAmount(String amount) {
        invoiceTaxAmountTextBox.clear();
        invoiceTaxAmountTextBox.sendKeys(amount);
    }

    public String getLegalEntityPlaceHolderText() {
        return invoiceLegalEntityTextBox.getAttribute("placeholder");
    }

    public String getLegalEntity() {
        return invoiceLegalEntityTextBox.getAttribute("value");
    }
    
    public String getLegalEntityLabelText() {
        return legalEntityLabel.getAttribute("title");
    }

    public void setLegalEntity(String legalEntity) {
        invoiceLegalEntityTextBox.clear();
        invoiceLegalEntityTextBox.sendKeys(legalEntity);
    }
    
    public void selectLegalEntity() {
        legalEntityDropDown.click();
        selectRandomVisibleDropdownOption(0, INVOICE_FORM_LEGAL_ENTITY_DROPDOWN_OPTIONS_XPATH);
    }
    
    public void enterLegalEntity(String input) {
        legalEntityDropDown.click();
        legalEntityTextBox.sendKeys(input);
    }

    public String getNetAmount() {
        return invoiceNetAmountTextBox.getAttribute("value");
    }

    public boolean isInvoiceErrorMessageDisplayed() {
        return isElementPresent(By.xpath(INVOICE_MISSING_INFO_ERROR_MESSAGE_XPATH));
    }
}
