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

public class QueryInitialApproverModal extends Base_Page<QueryInitialApproverModal> {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Send query to";

    //Main Content
    private static final String QUERY_INITIAL_APPROVER_MODAL_XPATH = "//*[@id='queryInitialApproverQuoteModal']";
    private static final String QUERY_INITIAL_APPROVER_MODAL_FOOTER_XPATH = QUERY_INITIAL_APPROVER_MODAL_XPATH + "//div[contains(@class,'modal-footer')]";
    private static final String QUERY_INITIAL_APPROVER_MODAL_HEADER_XPATH = QUERY_INITIAL_APPROVER_MODAL_XPATH + "//div[contains(@class,'modal-header')]";
    private static final String QUERY_INITIAL_APPROVER_MODAL_BODY_XPATH = QUERY_INITIAL_APPROVER_MODAL_XPATH + "//div[contains(@class,'modal-body')]";

    // Elements
    private static final String QUERY_REASON_DROPDOWN_XPATH = QUERY_INITIAL_APPROVER_MODAL_BODY_XPATH + "//select";
    private static final String QUERY_NOTES_XPATH = QUERY_INITIAL_APPROVER_MODAL_BODY_XPATH + "//textarea";

    // Buttons
    private static final String SAVE_BUTTON_XPATH = QUERY_INITIAL_APPROVER_MODAL_FOOTER_XPATH + "//button[contains(text(), 'Save')]";


    // Web Elements
    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = QUERY_REASON_DROPDOWN_XPATH)
    private WebElement queryReasonDropdown;

    @FindBy(xpath = QUERY_NOTES_XPATH)
    private WebElement queryNotes;


    // Page Methods
    public QueryInitialApproverModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(QUERY_INITIAL_APPROVER_MODAL_HEADER_XPATH));
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

    public String selectRandomQueryReason() {
        return selectRandomOptionFromSelect(queryReasonDropdown);
    }

    public void enterQueryNotes(String text) {
        queryNotes.sendKeys(text);
    }

    public void save() {
        save.click();
        waitForAnimation();
    }
}
