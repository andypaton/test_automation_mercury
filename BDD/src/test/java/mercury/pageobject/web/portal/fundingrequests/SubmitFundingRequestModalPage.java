package mercury.pageobject.web.portal.fundingrequests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class SubmitFundingRequestModalPage extends Base_Page<SubmitFundingRequestModalPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page elements
    private static final String PAGE_MODAL_CSS = " #quoteSubmissionModal";
    private static final String PAGE_MODAL_HEADER_CSS = PAGE_MODAL_CSS + " div.modal-header";
    private static final String PAGE_MODAL_TITLE_CSS = PAGE_MODAL_HEADER_CSS + " h4";
    private static final String PAGE_MODAL_BODY_CSS = PAGE_MODAL_CSS + " div.modal-body";
    private static final String PAGE_MODAL_FOOTER_CSS = PAGE_MODAL_CSS + " div.modal-footer";

    private static final String REJECTION_ACTION_CSS = PAGE_MODAL_BODY_CSS + " select";
    private static final String INTERNAL_NOTES_CSS = PAGE_MODAL_BODY_CSS + " textarea[name=internalNote]";
    private static final String CLOSE_BUTTON_CSS = PAGE_MODAL_FOOTER_CSS + " button.btn.btn-secondary.btn-neutral";
    private static final String SAVE_BUTTON_CSS = PAGE_MODAL_FOOTER_CSS + " #submit-quote-job";

    private static final String INVITATION_TO_QUOTE_CSS = PAGE_MODAL_BODY_CSS + " div.invitations-to-quote.ng-scope";
    private static final String NUMBER_OF_QUOTES_REQUIRED_CSS = INVITATION_TO_QUOTE_CSS + " #numberOfQuotesRequired";
    private static final String RESOURCE_TYPE_PICKER_CSS = INVITATION_TO_QUOTE_CSS + " #quoteResourceTypePicker";
    private static final String QUOTE_RESOURCE_PICKER_CSS = INVITATION_TO_QUOTE_CSS + " #quoteResourcePicker";
    private static final String SHOW_ALL_RESOURCES_CSS = INVITATION_TO_QUOTE_CSS + " label[for=showAllResources_0]";

    // WebElements
    @FindBy(css = PAGE_MODAL_TITLE_CSS)
    private WebElement pageTitle;

    @FindBy(css = INTERNAL_NOTES_CSS)
    private WebElement internalNotes;

    @FindBy(css = REJECTION_ACTION_CSS)
    private WebElement rejectionAction;

    @FindBy(css = CLOSE_BUTTON_CSS)
    private WebElement closeButton;

    @FindBy(css = SAVE_BUTTON_CSS)
    private WebElement saveButton;

    @FindBy(css= NUMBER_OF_QUOTES_REQUIRED_CSS)
    private WebElement numberOfQuotes;

    @FindBy(css = RESOURCE_TYPE_PICKER_CSS)
    private WebElement resourceType;

    @FindBy(css = QUOTE_RESOURCE_PICKER_CSS)
    private WebElement resourcePicker;

    @FindBy(css = SHOW_ALL_RESOURCES_CSS)
    private WebElement showAllResouces;

    public SubmitFundingRequestModalPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(PAGE_MODAL_HEADER_CSS));
            logger.info(PAGE_MODAL_HEADER_CSS + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_MODAL_HEADER_CSS + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_MODAL_HEADER_CSS + " Load");
    }

    @Override
    public String getPageTitle() {
        return pageTitle.getText();
    }

    public void setInternalNotes(String notes) {
        internalNotes.sendKeys(notes);
    }

    public void save() {
        saveButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRandomRejectionAction() {
        selectRandomOptionFromSelect(rejectionAction);
    }

    public void selectRejectionAction(String action) {
        this.selectOptionFromSelect(rejectionAction, action);
    }

    public void selectRandomResourceType() {
        selectRandomOptionFromSelect(resourceType);
    }

    public void showAllResources() {
        showAllResouces.click();
    }

    public String selectRandomResource() {
        return selectRandomOptionFromSelect(resourcePicker);
    }

}
