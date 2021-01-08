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
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class RejectInvoiceModalPage extends Base_Page<RejectInvoiceModalPage> {
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice Approval";

    // Main Content
    private static final String REJECT_INVOICE_MODAL = "//*[@id='RejectInvoiceModal']";
    private static final String MODAL_TITLE_XPATH = REJECT_INVOICE_MODAL + "//h4";
    private static final String REJECT_MODAL_FORM_GROUP_XPATH = "//div[contains(@class, 'form-group')]";

    private static final String REJECT_REASON_LABEL = REJECT_INVOICE_MODAL + REJECT_MODAL_FORM_GROUP_XPATH + "//label[contains(text(), 'Reason')]";
    private static final String REJECT_NOTES_LABEL = REJECT_INVOICE_MODAL + REJECT_MODAL_FORM_GROUP_XPATH + "//label[contains(text(), 'Notes')]";

    private static final String REJECT_REASON_LIST_BOX = REJECT_INVOICE_MODAL + "//select[(@id='SelectedRejectionReason')]";
    private static final String REJECT_NOTES_TEXT_BOX = REJECT_INVOICE_MODAL + "//textarea[(@id='RejectionNotes')]";

    private static final String REJECT_VALIDATION_SPAN_XPATH = "/../span[contains(@class, 'field-validation-error')]";
    private static final String REJECT_REASON_VALIDATION_MESSAGE_XPATH = REJECT_REASON_LIST_BOX + REJECT_VALIDATION_SPAN_XPATH;
    private static final String REJECT_NOTES_VALIDATION_MESSAGE_XPATH = REJECT_NOTES_TEXT_BOX + REJECT_VALIDATION_SPAN_XPATH;

    private static final String REJECT_INVOICE_SAVE_BUTTON = REJECT_INVOICE_MODAL + "//*[@id='SaveRejection']";
    private static final String REJECT_INVOICE_CLOSE_BUTTON = REJECT_INVOICE_MODAL + "//div//button[text()='Close']";


    @FindBy(xpath = MODAL_TITLE_XPATH)
    private WebElement rejectInvoiceHeader;

    @FindBy(xpath = REJECT_REASON_LIST_BOX)
    private WebElement rejectReasonListBox;

    @FindBy(xpath = REJECT_NOTES_TEXT_BOX)
    private WebElement rejectNotesTextBox;

    @FindBy(xpath = REJECT_INVOICE_SAVE_BUTTON)
    private WebElement rejectInvoiceSaveButton;

    @FindBy(xpath = REJECT_INVOICE_CLOSE_BUTTON)
    private WebElement rejectInvoiceCloseButton;

    @FindBy(xpath = REJECT_REASON_LABEL)
    private WebElement rejectReasonLabel;

    @FindBy(xpath = REJECT_NOTES_LABEL)
    private WebElement rejectNotesLabel;

    @FindBy(xpath = REJECT_REASON_VALIDATION_MESSAGE_XPATH)
    private WebElement rejectReasonValidationMessage;

    @FindBy(xpath = REJECT_NOTES_VALIDATION_MESSAGE_XPATH)
    private WebElement rejectNotesValidationMessage;

    public RejectInvoiceModalPage(WebDriver driver){
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

    public String getRejectInvoiceHeader() {
        return rejectInvoiceHeader.getText();
    }

    public void selectRejectReasonListBox() {
        selectRandomOptionFromSelect(rejectReasonListBox);
        waitForAngularRequestsToFinish();
    }

    public void enterRandomRejectionNote(String note) {
        waitForAnimation();
        rejectNotesTextBox.sendKeys(note);
    }

    public void selectSaveButton() {
        rejectInvoiceSaveButton.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isCloseButtonDisplayed() {
        return rejectInvoiceCloseButton.isDisplayed();
    }

    public Boolean isSaveButtonEnabled() {
        return rejectInvoiceSaveButton.isEnabled();
    }

    public String getRejectReasonLabel() {
        return rejectReasonLabel.getText();
    }

    public String getRejectNotesLabel() {
        return rejectNotesLabel.getText();
    }

    public String getRejectReasonValidationMessage() {
        return rejectReasonValidationMessage.getText();
    }

    public String getRejectNotesValidationMessage() {
        return rejectNotesValidationMessage.getText();
    }

    public List<String> getAllRejectionsReasons(Integer reasonPicker) {
        List<String> allRejectionReasons = new ArrayList<>();
        Select reasonSelect = new Select(driver.findElement(By.xpath(String.format(REJECT_REASON_LIST_BOX, String.valueOf(reasonPicker)))));
        List<WebElement> visibleOptions = reasonSelect.getOptions();
        for (WebElement option : visibleOptions)
        {
            String optionText = option.getText();
            if(optionText.length()>0) {
                allRejectionReasons.add(option.getText().trim());
            }
        }
        return allRejectionReasons;
    }

}
