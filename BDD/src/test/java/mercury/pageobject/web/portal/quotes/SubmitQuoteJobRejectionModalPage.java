package mercury.pageobject.web.portal.quotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class SubmitQuoteJobRejectionModalPage extends Base_Page<SubmitQuoteJobRejectionModalPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Submit quote job rejection";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_MODAL_CSS = " div[class='modal fade in']";
    private static final String PAGE_MODAL_HEADER_CSS = PAGE_MODAL_CSS + " div.modal-header";
    private static final String PAGE_MODAL_BODY_CSS = PAGE_MODAL_CSS + " div.modal-body";
    private static final String PAGE_MODAL_FOOTER_CSS = PAGE_MODAL_CSS + " div.modal-footer";

    // Core
    private static final String REJECTION_ACTION_CSS = PAGE_MODAL_BODY_CSS + " select";
    private static final String CLOSE_REJECTION_CSS = PAGE_MODAL_FOOTER_CSS + " button.btn.btn-secondary.btn-neutral";
    private static final String SAVE_REJECTION_CSS = PAGE_MODAL_FOOTER_CSS + " #submit-quote-job";

    private static final String NUMBER_OF_QUOTES_REQUIRED_CSS = PAGE_MODAL_BODY_CSS + " #numberOfQuotesRequired";
    private static final String RESOURCE_TYPE_PICKER_CSS = PAGE_MODAL_BODY_CSS + " #quoteResourceTypePicker";
    private static final String QUOTE_RESOURCE_PICKER_CSS = PAGE_MODAL_BODY_CSS + " #quoteResourcePicker";
    private static final String SHOW_ALL_RESOURCES_CSS = PAGE_MODAL_BODY_CSS + " label[for=showAllResources_0]";
    private static final String SHOW_ALL_RESOURCES_XPATH = "//div[@id='quoteSubmissionModal']//div[@class='invitations-to-quote']//input[contains(@id,'showAllResources')]";

    // WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = REJECTION_ACTION_CSS)
    private WebElement rejectionAction;

    @FindBy(css = CLOSE_REJECTION_CSS)
    private WebElement closeRejection;

    @FindBy(css = SAVE_REJECTION_CSS)
    private WebElement saveRejection;

    @FindBy(css= NUMBER_OF_QUOTES_REQUIRED_CSS)
    private WebElement numberOfQuotes;

    @FindBy(css = RESOURCE_TYPE_PICKER_CSS)
    private WebElement resourceType;

    @FindBy(css = QUOTE_RESOURCE_PICKER_CSS)
    private WebElement resourcePicker;

    @FindBy(xpath = SHOW_ALL_RESOURCES_XPATH)
    private WebElement showAllResouces;

    public SubmitQuoteJobRejectionModalPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(PAGE_MODAL_HEADER_CSS));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public String getPageHeaderText() {
        return pageHeaderTest.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.cssSelector(CLOSE_REJECTION_CSS));
    }

    // Page interactions
    public void selectRandomRejectionAction() {
        selectRandomOptionFromSelect(rejectionAction);
    }

    public void selectRejectionAction(String action) {
        this.selectOptionFromSelect(rejectionAction, action);
    }

    public void selectRandomResourceType() {
        selectRandomOptionFromSelect(resourceType);
        waitForAngularRequestsToFinish();
    }

    public void showAllResources() {
        showAllResouces.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRandomResource() {
        selectRandomOptionFromSelect(resourcePicker);
        waitForAngularRequestsToFinish();
    }

    public void saveRejection() {
        this.waitForAngularRequestsToFinish();
        this.saveRejection.click();
    }

    // Get PAge values
    public String getSelectedResource() {
        return  getSelectSelectedText(resourcePicker);
    }
}
