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

public class RejectInvoicePendingReviewModal extends Base_Page<RejectInvoicePendingReviewModal>{
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice Pending Review";

    //Main Content
    private static final String REJECT_INVOICE_PAGE_MODAL_CSS = "#RejectInvoiceModal";
    private static final String REJECT_INVOICE_PAGE_MODAL_FOOTER_CSS = REJECT_INVOICE_PAGE_MODAL_CSS + " div.modal-footer";

    //Main Content
    private static final String REJECT_INVOICE_PAGE_MODAL_XPATH = "//*[@id='RejectInvoiceModal']";
    private static final String MODAL_TITLE_XPATH = REJECT_INVOICE_PAGE_MODAL_XPATH + "//h4";
    private static final String REJECT_INVOICE_MODAL_REASON_FORM_GROUP_XPATH = REJECT_INVOICE_PAGE_MODAL_XPATH + "//div[contains(@class, 'form-group')]";

    //Buttons
    private static final String REJECT_INVOICE_SAVE_BUTTON_CSS = REJECT_INVOICE_PAGE_MODAL_FOOTER_CSS + " button.btn.btn-primary";
    private static final String REJECT_INVOICE_CLOSE_BUTTON_CSS = REJECT_INVOICE_PAGE_MODAL_FOOTER_CSS + " button.btn.btn-secondary.btn-neutral";

    //List box
    private static final String REJECT_INVOICE_REASON_LIST_BOX_XPATH = REJECT_INVOICE_MODAL_REASON_FORM_GROUP_XPATH  + "//select";

    //Notes Box
    private static final String REJECT_INVOICE_REASON_NOTES_BOX_XPATH = REJECT_INVOICE_PAGE_MODAL_XPATH + "//textarea[@id='RejectionNotes']";

    @FindBy(xpath = REJECT_INVOICE_REASON_LIST_BOX_XPATH)
    private WebElement reasonListtBox;

    @FindBy(xpath = REJECT_INVOICE_REASON_NOTES_BOX_XPATH)
    private WebElement notesBox;

    @FindBy(css = REJECT_INVOICE_SAVE_BUTTON_CSS)
    private WebElement saveButton;

    @FindBy(css = REJECT_INVOICE_CLOSE_BUTTON_CSS)
    private WebElement closeButton;

    public RejectInvoicePendingReviewModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(MODAL_TITLE_XPATH + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(MODAL_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(MODAL_TITLE_XPATH));
    }

    public void selectRandomRejectionReason() {
        waitForAnimation();
        selectRandomOptionFromSelect(reasonListtBox);
    }

    public void enterNotes(String notes) {
        waitForAnimation();
        notesBox.sendKeys(notes);
    }

    public InvoicePendingReviewPage selectSave() {
        waitForAnimation();
        saveButton.click();
        return PageFactory.initElements(driver, InvoicePendingReviewPage.class);
    }

}
