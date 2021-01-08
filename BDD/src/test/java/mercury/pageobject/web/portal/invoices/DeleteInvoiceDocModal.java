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

public class DeleteInvoiceDocModal extends Base_Page<DeleteInvoiceDocModal>{

    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice";

    //Main Content
    private static final String DELETE_INVOICE_PAGE_MODAL_CSS = "#DeleteInvoiceDocModal";
    private static final String DELETE_INVOICE_PAGE_MODAL_HEADER_CSS = DELETE_INVOICE_PAGE_MODAL_CSS + " div.modal-header";
    private static final String DELETE_INVOICE_PAGE_MODAL_FOOTER_CSS = DELETE_INVOICE_PAGE_MODAL_CSS + " div.modal-footer";
    private static final String MODAL_TITLE_CSS = DELETE_INVOICE_PAGE_MODAL_HEADER_CSS + " #DeleteInvoiceDocLabel";

    //Buttons
    private static final String INVOICE_DELETE_DOC_DELETE_BUTTON_CSS = DELETE_INVOICE_PAGE_MODAL_FOOTER_CSS + " #DeleteInvoiceDoc";
    private static final String INVOICE_DELETE_DOC_CANCEL_BUTTON_CSS = DELETE_INVOICE_PAGE_MODAL_FOOTER_CSS + " button.btn.btn-secondary.btn-destructive";

    @FindBy(css = INVOICE_DELETE_DOC_DELETE_BUTTON_CSS)
    private WebElement invoiceDeleteButton;

    @FindBy(css = INVOICE_DELETE_DOC_CANCEL_BUTTON_CSS)
    private WebElement cancelButton;

    public DeleteInvoiceDocModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(MODAL_TITLE_CSS + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(MODAL_TITLE_CSS));
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
        return this.isElementPresent(By.xpath(MODAL_TITLE_CSS));
    }

    public PortalOrdersInvoicePage selectDelete() {
        waitForAngularRequestsToFinish();
        invoiceDeleteButton.click();
        return PageFactory.initElements(driver, PortalOrdersInvoicePage.class);

    }

}
