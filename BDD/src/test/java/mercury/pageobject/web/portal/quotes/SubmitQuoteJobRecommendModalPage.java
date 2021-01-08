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

public class SubmitQuoteJobRecommendModalPage extends Base_Page<SubmitQuoteJobRecommendModalPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Submit quote job recommendation";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_MODAL_CSS = " #quoteSubmissionModalContent";
    private static final String PAGE_MODAL_HEADER_CSS = PAGE_MODAL_CSS + " div.modal-header";
    private static final String PAGE_MODAL_BODY_CSS = PAGE_MODAL_CSS + " div.modal-body";
    private static final String PAGE_MODAL_FOOTER_CSS = PAGE_MODAL_CSS + " div.modal-footer";

    // Core
    private static final String INTERNAL_NOTES_LABEL_XPATH = "//label[@for='internalNote']";
    private static final String INTERNAL_NOTES_CSS = PAGE_MODAL_BODY_CSS + " textarea[name=internalNote]";
    private static final String CLOSE_RECOMMEND_CSS = PAGE_MODAL_FOOTER_CSS + " button.btn.btn-secondary.btn-neutral";
    private static final String SAVE_RECOMMEND_CSS = PAGE_MODAL_FOOTER_CSS + " #submit-quote-job";

    // WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = INTERNAL_NOTES_CSS)
    private WebElement internalNotes;

    @FindBy(css = CLOSE_RECOMMEND_CSS)
    private WebElement closeRecommend;

    @FindBy(css = SAVE_RECOMMEND_CSS)
    private WebElement saveRecommend;

    @FindBy(xpath = INTERNAL_NOTES_LABEL_XPATH)
    private WebElement internalNotesLabel;

    // Page Methods
    public SubmitQuoteJobRecommendModalPage(WebDriver driver) {
        super(driver);
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

    // Page interactions

    public void setInternalNotes(String notes) {
        internalNotes.sendKeys(notes);
    }

    public void saveRecommendation() {
        this.waitForAngularRequestsToFinish();
        this.saveRecommend.click();
    }

    public boolean isInternalNotesLabelDisplayed() {
        return isElementClickable(By.xpath(INTERNAL_NOTES_LABEL_XPATH));
    }

    // Get Page values
}
