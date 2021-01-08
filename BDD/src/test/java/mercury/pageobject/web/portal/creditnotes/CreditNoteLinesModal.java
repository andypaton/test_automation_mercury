package mercury.pageobject.web.portal.creditnotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;

public class CreditNoteLinesModal extends Base_Page<CreditNoteLinesModal> {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Credit Note Lines";

    //Main Content
    private static final String CREDIT_NOTE_LINES_MODAL_XPATH = "//*[@id='CreditNoteLinesModal']";
    private static final String CREDIT_NOTE_LINES_MODAL_FOOTER_XPATH = CREDIT_NOTE_LINES_MODAL_XPATH + "//div[contains(@class,'modal-footer')]";
    private static final String CREDIT_NOTE_LINES_MODAL_HEADER_XPATH = CREDIT_NOTE_LINES_MODAL_XPATH + "//div[contains(@class,'modal-header')]";
    private static final String CREDIT_NOTE_LINES_MODAL_BODY_XPATH = CREDIT_NOTE_LINES_MODAL_XPATH + "//div[contains(@class,'modal-body')]";

    // Table
    private static final String CREDIT_NOTE_LINES_TABLE_XPATH = CREDIT_NOTE_LINES_MODAL_BODY_XPATH + "//table";

    // Buttons
    private static final String CLOSE_BUTTON_XPATH = CREDIT_NOTE_LINES_MODAL_FOOTER_XPATH + "//button[contains(text(), 'Close')]";


    // Web Elements
    @FindBy(xpath = CLOSE_BUTTON_XPATH)
    private WebElement close;


    // Page Methods
    public CreditNoteLinesModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(CREDIT_NOTE_LINES_MODAL_HEADER_XPATH));
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

    public Grid getCreditNoteLinesTable() {
        return GridHelper.getGrid(CREDIT_NOTE_LINES_TABLE_XPATH);
    }

    public void close() {
        close.click();
    }
}
