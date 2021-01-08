package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AddNewInvoiceLineModal extends Base_Page<AddNewInvoiceLineModal> {
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice";

    //Main Content
    private static final String ADD_NEW_LINE_TO_INVOICE_MODAL_CSS = "#NewLineModal ";
    private static final String ADD_NEW_LINE_TO_INVOICE_MODAL_FOOTER_CSS = ADD_NEW_LINE_TO_INVOICE_MODAL_CSS + "div.modal-footer ";
    private static final String ADD_NEW_LINE_TO_INVOICE_MODAL_TITLE_CSS = "h4#NewLineLabel ";

    private static final String ADD_NEW_LINE_TO_INVOICE_MODAL_XPATH = "//*[@id='NewLineModal']";
    private static final String ADD_NEW_LINE_TO_INVOICE_MODAL_BODY_XPATH = ADD_NEW_LINE_TO_INVOICE_MODAL_XPATH + "//div[contains(@class,'modal-body')]";

    //Labels
    private static final String INVOICE_TYPE_LABEL_XPATH = ADD_NEW_LINE_TO_INVOICE_MODAL_BODY_XPATH +"//strong[contains(text(),'Type')]";
    private static final String INVOICE_PART_NUMBER_LABEL_XPATH = ADD_NEW_LINE_TO_INVOICE_MODAL_BODY_XPATH +"//strong[contains(text(),'Part Number')]";
    private static final String INVOICE_DESCRIPTION_LABEL_XPATH = ADD_NEW_LINE_TO_INVOICE_MODAL_BODY_XPATH +"//strong[contains(text(),'Description')]";
    private static final String INVOICE_QUANTITY_LABEL_XPATH = ADD_NEW_LINE_TO_INVOICE_MODAL_BODY_XPATH +"//strong[contains(text(),'Quantity')]";
    private static final String INVOICE_UNIT_PRICE_LABEL_XPATH = ADD_NEW_LINE_TO_INVOICE_MODAL_BODY_XPATH +"//strong[contains(text(),'Unit Price')]";
    private static final String INVOICE_UNIT_TAX_AMOUNT_LABEL_XPATH = ADD_NEW_LINE_TO_INVOICE_MODAL_BODY_XPATH +"//strong[contains(text(),'Unit %s Amount')]";

    //List Box
    private static final String INVOICE_TYPE_LIST_DROPDOWN_XPATH = INVOICE_TYPE_LABEL_XPATH + "/..//select[(@id='InvoiceLineTypeId')]";

    //Text Box
    private static final String INVOICE_DESCRIPTION_TEXTBOX_XPATH = INVOICE_DESCRIPTION_LABEL_XPATH + "/..//textarea[(@id='Description')]";
    private static final String INVOICE_PART_NUMBER_TEXTBOX_XPATH = INVOICE_PART_NUMBER_LABEL_XPATH + "/..//input[(@id='PartNumber')]";
    private static final String INVOICE_QUANTITY_TEXTBOX_XPATH = INVOICE_QUANTITY_LABEL_XPATH + "/..//input[(@id='Quantity')]";
    private static final String INVOICE_UNIT_PRICE_TEXTBOX_XPATH = INVOICE_UNIT_PRICE_LABEL_XPATH + "/../input[(@id='UnitPrice')]";
    private static final String INVOICE_UNIT_TAX_AMOUNT_TEXTBOX_XPATH = INVOICE_UNIT_TAX_AMOUNT_LABEL_XPATH + "/../input[(@id='UnitTaxAmount')]";

    //Buttons
    private static final String INVOICE_LINE_ADD_BUTTON = ADD_NEW_LINE_TO_INVOICE_MODAL_FOOTER_CSS + " #AddNewLine";

    //Error labels
    private static final String INVOICE_TAX_ERROR_MESSAGE_XPATH = ADD_NEW_LINE_TO_INVOICE_MODAL_XPATH + "//div[(@id='InvoiceLineError')]";
    private static final String INVOICE_TYPE_ERROR_MESSAGE_XPATH = INVOICE_TYPE_LABEL_XPATH + "/..//label[(@id='InvoiceLineTypeId-error')]";
    private static final String INVOICE_DESCRIPTION_ERROR_MESSAGE_XPATH = INVOICE_DESCRIPTION_LABEL_XPATH + "/..//label[(@id='Description-error')]";
    private static final String INVOICE_QUANTITY_ERROR_MESSAGE_XPATH = INVOICE_QUANTITY_LABEL_XPATH + "/..//label[(@id='Quantity-error')]";


    @FindBy(xpath = INVOICE_TYPE_LIST_DROPDOWN_XPATH)
    private WebElement invoiceTypeListBox;

    @FindBy(xpath = INVOICE_PART_NUMBER_TEXTBOX_XPATH)
    private WebElement invoicePartNumberTextBox;

    @FindBy(xpath = INVOICE_DESCRIPTION_TEXTBOX_XPATH)
    private WebElement descriptionTextBox;

    @FindBy(xpath = INVOICE_UNIT_PRICE_TEXTBOX_XPATH)
    private WebElement unitPriceTextBox;

    @FindBy(xpath = INVOICE_QUANTITY_TEXTBOX_XPATH)
    private WebElement quantityTextBox;

    @FindBy(xpath = INVOICE_TAX_ERROR_MESSAGE_XPATH)
    private WebElement taxAmountError;

    @FindBy(xpath = INVOICE_TYPE_ERROR_MESSAGE_XPATH)
    private WebElement typeError;

    @FindBy(xpath = INVOICE_DESCRIPTION_ERROR_MESSAGE_XPATH)
    private WebElement descriptionError;

    @FindBy(xpath = INVOICE_QUANTITY_ERROR_MESSAGE_XPATH)
    private WebElement quantityError;

    @FindBy(css = INVOICE_LINE_ADD_BUTTON)
    private WebElement addbutton;



    public AddNewInvoiceLineModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(ADD_NEW_LINE_TO_INVOICE_MODAL_TITLE_CSS + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(ADD_NEW_LINE_TO_INVOICE_MODAL_TITLE_CSS));
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
        return this.isElementPresent(By.cssSelector(ADD_NEW_LINE_TO_INVOICE_MODAL_TITLE_CSS));
    }

    public void selectAnInvoiceType(String type) {
        selectOptionFromSelect(invoiceTypeListBox, type);
    }

    public Boolean isPartNumberDisplayed(){
        return this.isElementPresent(By.xpath(INVOICE_PART_NUMBER_LABEL_XPATH));
    }

    public void enterPartNumber(String partNumber) {
        invoicePartNumberTextBox.sendKeys(partNumber);
    }

    public void enterDescription(String description) {
        descriptionTextBox.sendKeys(description);
    }

    public void enterUnitPrice(String unitPrice) {
        unitPriceTextBox.clear();
        unitPriceTextBox.sendKeys(unitPrice);
    }

    public void enterQuantity(String quantity) {
        quantityTextBox.clear();
        quantityTextBox.sendKeys(quantity);
    }

    public PortalOrdersInvoicePage addLine() {
        addbutton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PortalOrdersInvoicePage.class);
    }

    public void enterUnitTaxAmount(String taxAmount, String taxlabel) {
        By by = By.xpath(String.format(INVOICE_UNIT_TAX_AMOUNT_TEXTBOX_XPATH, taxlabel));
        driver.findElement(by).clear();
        driver.findElement(by).sendKeys(taxAmount);
    }

    public String getTaxAmountErrorMessage() {
        return taxAmountError.getText();
    }

    public String getTypeErrorMessage() {
        waitForAnimation();
        return typeError.getText();
    }

    public String getDescriptionErrorMessage() {
        waitForAnimation();
        return descriptionError.getText();
    }

    public String getQuantityErrorMessage() {
        waitForAnimation();
        return quantityError.getText();
    }
}
