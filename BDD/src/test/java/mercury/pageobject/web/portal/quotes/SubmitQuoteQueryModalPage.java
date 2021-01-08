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

public class SubmitQuoteQueryModalPage extends Base_Page<SubmitQuoteQueryModalPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Send query to";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_MODAL_CSS = " #queryResourceQuoteModal";
    private static final String PAGE_MODAL_HEADER_CSS = PAGE_MODAL_CSS + " div.modal-header";
    private static final String PAGE_MODAL_BODY_CSS = PAGE_MODAL_CSS + " div.modal-body";
    private static final String PAGE_MODAL_FOOTER_CSS = PAGE_MODAL_CSS + " div.modal-footer";

    // Core
    private static final String QUERY_REASON_CSS = PAGE_MODAL_BODY_CSS + " select";
    private static final String QUERY_NOTES_CSS = PAGE_MODAL_BODY_CSS + " textarea[name=queryNotes]";
    private static final String CLOSE_CSS = PAGE_MODAL_FOOTER_CSS + " button.btn.btn-secondary.btn-neutral";
    private static final String SAVE_CSS = PAGE_MODAL_FOOTER_CSS + " #submit-quote-query";

    // WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = QUERY_REASON_CSS)
    private WebElement queryReason;

    @FindBy(css = QUERY_NOTES_CSS)
    private WebElement queryNotes;

    @FindBy(css = CLOSE_CSS)
    private WebElement closeQuery;

    @FindBy(css = SAVE_CSS)
    private WebElement saveQuery;

    // Page Methods
    public SubmitQuoteQueryModalPage(WebDriver driver) {
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

    // Page interactions

    public void selectRandomQueryReason() {
        selectRandomOptionFromSelect(queryReason);
    }
    public void setQueryNotes(String notes) {
        queryNotes.sendKeys(notes);
    }

    public void saveQuery() {
        this.saveQuery.click();
        this.waitForAngularRequestsToFinish();
    }

    // Get Page values
}
