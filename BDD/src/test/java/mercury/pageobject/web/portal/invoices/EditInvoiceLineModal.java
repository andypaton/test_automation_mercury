package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class EditInvoiceLineModal extends Base_Page<EditInvoiceLineModal> {

    private static final Logger logger = LogManager.getLogger();

    //Main Content
    private static final String EDIT_INVOICE_LINE_MODAL_CSS = "#EditLineModal";
    private static final String EDIT_INVOICE_LINE_MODAL_HEADER_CSS = EDIT_INVOICE_LINE_MODAL_CSS + " div.modal-header";
    private static final String EDIT_INVOICE_LINE_MODAL_FOOTER_CSS = EDIT_INVOICE_LINE_MODAL_CSS + " div.modal-footer";
    private static final String MODAL_TITLE_CSS = EDIT_INVOICE_LINE_MODAL_HEADER_CSS + " #NewLineLabel";

    //Buttons
    private static final String DELETE_INVOICE_LINE_BUTTON_CSS = EDIT_INVOICE_LINE_MODAL_FOOTER_CSS + " #DeleteLine";
    private static final String SAVE_INVOICE_LINE_BUTTON_CSS = EDIT_INVOICE_LINE_MODAL_FOOTER_CSS + "#EditLine";
    private static final String CLOSE_INVOICE_LINE_BUTTON_CSS = EDIT_INVOICE_LINE_MODAL_FOOTER_CSS + " button.btn.btn-secondary.btn-neutral";

    @FindBy(css = DELETE_INVOICE_LINE_BUTTON_CSS)
    private WebElement deleteInvoiceLineButton;

    @FindBy(css = SAVE_INVOICE_LINE_BUTTON_CSS)
    private WebElement saveInvoiceLineButton;

    @FindBy(css = CLOSE_INVOICE_LINE_BUTTON_CSS)
    private WebElement closeInvoiceLineButton;

    public EditInvoiceLineModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(MODAL_TITLE_CSS + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(MODAL_TITLE_CSS));
            logger.info(MODAL_TITLE_CSS + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(MODAL_TITLE_CSS + "isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(MODAL_TITLE_CSS + " Load");
    }

    public void selectDelete(){
        deleteInvoiceLineButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectSave() {
        waitForAngularRequestsToFinish();
        saveInvoiceLineButton.click();
    }

    public void selectClose() {
        waitForAngularRequestsToFinish();
        closeInvoiceLineButton.click();
    }

}
