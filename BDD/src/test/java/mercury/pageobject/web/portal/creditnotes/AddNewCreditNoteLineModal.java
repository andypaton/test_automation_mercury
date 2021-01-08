package mercury.pageobject.web.portal.creditnotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.portal.invoices.PortalOrdersInvoicePage;

public class AddNewCreditNoteLineModal extends Base_Page<AddNewCreditNoteLineModal> {
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Add New Line to the Credit Note";

    //Main Content
    private static final String ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_CSS = "#NewLineModal ";
    private static final String ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FOOTER_CSS = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_CSS + "div.modal-footer ";
    private static final String ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_TITLE_CSS = "h4#NewLineLabel ";

    private static final String ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_XPATH = "//*[@id='NewLineModal']";
    private static final String ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_BODY_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_XPATH + "//div[contains(@class,'modal-body')]";
    private static final String ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_BODY_XPATH + "//form[@id='CreditNoteLineForm']";

    // Fields
    private static final String CREDIT_NOTE_TYPE_DROPDOWN_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH + "//select[@id='CreditNoteLineTypeId']";
    private static final String CREDIT_NOTE_DESCRIPTION_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH + "//textarea[@id='Description']";
    private static final String CREDIT_NOTE_PART_NUMBER_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH + "//input[@id='PartNumber']";
    private static final String CREDIT_NOTE_QUANTITY_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH + "//input[@id='Quantity']";
    private static final String CREDIT_NOTE_UNIT_PRICE_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH + "//input[@id='UnitPrice']";
    private static final String CREDIT_NOTE_UNIT_TAX_AMOUNT_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH + "//input[@id='UnitTaxAmount']";
    private static final String CREDIT_NOTE_LINE_NET_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH + "//span[@id='LineNetValue']";
    private static final String CREDIT_NOTE_LINE_TAX_XPATH = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH + "//span[@id='LineTaxValue']";

    //Buttons
    private static final String CREDIT_NOTE_LINE_ADD_BUTTON = ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FOOTER_CSS + " #AddNewLine";


    @FindBy(xpath = CREDIT_NOTE_TYPE_DROPDOWN_XPATH)
    private WebElement creditNoteType;

    @FindBy(xpath = CREDIT_NOTE_PART_NUMBER_XPATH)
    private WebElement creditNotePartNumber;

    @FindBy(xpath = CREDIT_NOTE_DESCRIPTION_XPATH)
    private WebElement description;

    @FindBy(xpath = CREDIT_NOTE_UNIT_PRICE_XPATH)
    private WebElement unitPrice;

    @FindBy(xpath = CREDIT_NOTE_QUANTITY_XPATH)
    private WebElement quantity;

    @FindBy(xpath = CREDIT_NOTE_UNIT_TAX_AMOUNT_XPATH)
    private WebElement unitTaxAmount;

    @FindBy(xpath = CREDIT_NOTE_LINE_NET_XPATH)
    private WebElement lineNetAmount;

    @FindBy(xpath = CREDIT_NOTE_LINE_TAX_XPATH)
    private WebElement lineTaxAmount;

    @FindBy(css = CREDIT_NOTE_LINE_ADD_BUTTON)
    private WebElement addbutton;


    public AddNewCreditNoteLineModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_TITLE_CSS + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_TITLE_CSS));
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

    public void selectCreditNotelineType() {
        selectRandomOptionFromSelect(creditNoteType);
    }

    public String getCreditNotelineType() {
        waitForAngularRequestsToFinish();
        return new Select(creditNoteType).getFirstSelectedOption().getText();
    }

    public Boolean isPartNumberDisplayed(){
        return this.isElementPresent(By.xpath(ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_FORM_XPATH));
    }

    public void enterPartNumber(String partNumber) {
        waitForAngularRequestsToFinish();
        creditNotePartNumber.sendKeys(partNumber);
    }

    public void enterDescription(String text) {
        description.sendKeys(text);
    }

    public void enterUnitPrice(String price) {
        unitPrice.clear();
        unitPrice.sendKeys(price);
    }

    public void enterQuantity(String amount) {
        quantity.clear();
        quantity.sendKeys(amount);
    }

    public PortalOrdersInvoicePage selectAdd() {
        waitForAngularRequestsToFinish();
        addbutton.click();
        return PageFactory.initElements(driver, PortalOrdersInvoicePage.class);
    }

    public void enterUnitTaxAmount(String taxAmount) {
        unitTaxAmount.clear();
        unitTaxAmount.sendKeys(taxAmount);
    }

    public void clickAddButton() {
        addbutton.click();
        waitForAngularRequestsToFinish();
    }

}
