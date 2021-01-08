package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;

public class AddOrderLineToInvoiceModal extends Base_Page<AddOrderLineToInvoiceModal> {
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice";

    //Main Content
    private static final String ADD_ORDER_LINE_TO_INVOICE_MODAL_TITLE_CSS = "h4#AddOrderTransactionLinesToInvoiceLabel ";

    private static final String ADD_ORDER_LINE_TO_INVOICE_MODAL_XPATH = "//*[@id='AddOrderTransactionLinesToInvoiceModal']";


    //Input
    private static final String ADD_ORDER_LINE_INVOICE_QUANTITY_INPUT_XPATH = "//*[@id='InvoiceLineModels_0__Quantity']";
    private static final String ADD_ORDER_LINE_UNIT_PRICE_INPUT_XPATH = "//*[@id='InvoiceLineModels_0__UnitPrice']";
    private static final String ADD_ORDER_LINE_UNIT_TAX_AMOUNT_INPUT_XPATH = "//*[@id='InvoiceLineModels_0__UnitTaxAmount']";
    private static final String ADD_ORDER_LINE_TICK_ADD_INPUT_XPATH = "//input[@id='InvoiceLineModels0__AutoPopulate']";

    //Label
    private static final String ADD_ORDER_LINE_UNIT_TAX_ERROR_LABEL_XPATH = ADD_ORDER_LINE_TO_INVOICE_MODAL_XPATH + "//div[@id='AddOrderTransactionLinesToInvoiceError']";

    //Grid
    private static final String GRID_XPATH = ".//form[@id='AddOrderTransactionLinesForm']//div[@class='row table-responsive']";

    //Cell Control
    private static final String LINE_FULLFILLED_TICK_BOX_CSS = "div.col-sm-12 > table > tbody > tr > td > div  > input[class='chk_line_fulfilled']";
    private static final String TICK_TO_ADD_TICK_BOX_CSS = "div.col-sm-12 > table > tbody > tr > td > div  > input[class='chk_auto_populate']";

    //Buttons
    private static final String ADD_ORDER_LINE_ADD_LINES_BUTTON_XPATH = ADD_ORDER_LINE_TO_INVOICE_MODAL_XPATH + "//*[@id='AddOrderTransactionLinesToInvoice']";
    private static final String CLOSE_XPATH = ADD_ORDER_LINE_TO_INVOICE_MODAL_XPATH + "//button[contains(text(), 'Close')]";

    @FindBy(xpath = ADD_ORDER_LINE_INVOICE_QUANTITY_INPUT_XPATH)
    private WebElement quantityInputBox;

    @FindBy(xpath = ADD_ORDER_LINE_UNIT_PRICE_INPUT_XPATH)
    private WebElement unitPriceInputBox;

    @FindBy(xpath = ADD_ORDER_LINE_UNIT_TAX_AMOUNT_INPUT_XPATH)
    private WebElement unitTaxAmountInputBox;

    @FindBy(xpath = ADD_ORDER_LINE_TICK_ADD_INPUT_XPATH)
    private WebElement tickToAddCheckBox;

    @FindBy(xpath = ADD_ORDER_LINE_ADD_LINES_BUTTON_XPATH)
    private WebElement addLinesButton;

    @FindBy(xpath = CLOSE_XPATH)
    private WebElement close;

    @FindBy(xpath = ADD_ORDER_LINE_UNIT_TAX_ERROR_LABEL_XPATH)
    private WebElement unitTaxError;

    public AddOrderLineToInvoiceModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(ADD_ORDER_LINE_TO_INVOICE_MODAL_TITLE_CSS + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(ADD_ORDER_LINE_TO_INVOICE_MODAL_TITLE_CSS));
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
        return this.isElementPresent(By.cssSelector(ADD_ORDER_LINE_TO_INVOICE_MODAL_TITLE_CSS));
    }

    public PortalOrdersInvoicePage selectAddLines() {
        addLinesButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PortalOrdersInvoicePage.class);
    }

    public PortalOrdersInvoicePage close() {
        close.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PortalOrdersInvoicePage.class);
    }

    public void setQuantity(String quantity) {
        quantityInputBox.click();
        quantityInputBox.sendKeys(quantity);
    }

    public void setUnitPrice(String price) {
        unitPriceInputBox.clear();
        unitPriceInputBox.sendKeys(price);
    }

    public void setUnitTaxAmount(String price) {
        unitTaxAmountInputBox.clear();
        unitTaxAmountInputBox.sendKeys(price);
    }

    public void selectAddTickBox() {
        POHelper.clickJavascript(tickToAddCheckBox);
    }

    public Grid getGrid() {
        waitForAngularRequestsToFinish();
        return GridHelper.getGrid(GRID_XPATH);
    }


    public String getLineFullFilledControl() {
        return LINE_FULLFILLED_TICK_BOX_CSS;
    }

    public String getTickToAddControl() {
        return TICK_TO_ADD_TICK_BOX_CSS;
    }

}
