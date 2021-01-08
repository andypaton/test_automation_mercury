package mercury.pageobject.web.portal.creditnotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class CreditNoteApprovalPage extends Base_Page<CreditNoteApprovalPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Credit Note Approval";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    // Table
    private static final String CREDIT_NOTE_DETAILS_TABLE_XPATH = PAGE_MAIN_CONTENT_XPATH + "//table[@id='creditnote-table']";
    private static final String CREDIT_NOTE_DETAILS_TABLE_DATA_XPATH = CREDIT_NOTE_DETAILS_TABLE_XPATH + "//tbody//tr//th[text() = '%s']//following-sibling::td";

    // Credit Note Document Iframe
    private static final String CREDIT_NOTE_DOCUMENT_IFRAME_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@class='doc-view']//iframe[contains(@src,'Portal/CreditNote/CreditNoteDocument')]";

    // Buttons
    private static final String APPROVE_BUTTON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@type='submit']";
    private static final String REJECT_BUTTON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@id='RejectCreditNote']";
    private static final String CREDIT_NOTE_LINES_BUTTON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@id='ViewCreditNoteLines']";


    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderText;

    @FindBy(xpath = APPROVE_BUTTON_XPATH)
    private WebElement approve;

    @FindBy(xpath = REJECT_BUTTON_XPATH)
    private WebElement reject;

    @FindBy(xpath = CREDIT_NOTE_LINES_BUTTON_XPATH)
    private WebElement creditNoteLines;

    @FindBy(xpath = CREDIT_NOTE_DOCUMENT_IFRAME_XPATH)
    private WebElement creditNoteIframe;


    // Page methods
    public CreditNoteApprovalPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
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
        return pageHeaderText.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }

    public String getCreditNoteTableData(String creditNoteRowHeader) {
        By by = By.xpath(String.format(CREDIT_NOTE_DETAILS_TABLE_DATA_XPATH, creditNoteRowHeader));
        return driver.findElement(by).getText();
    }

    public String getPdfSrcFromIframe() {
        return creditNoteIframe.getAttribute("src");
    }

    public Boolean isApproveButtonDisplayed() {
        return isElementVisible(By.xpath(APPROVE_BUTTON_XPATH));
    }

    public Boolean isRejectButtonDisplayed() {
        return isElementVisible(By.xpath(REJECT_BUTTON_XPATH));
    }

    public CreditNoteLinesModal clickCreditNoteLinesButton() {
        creditNoteLines.click();
        return PageFactory.initElements(driver, CreditNoteLinesModal.class).get();
    }

    public void clickApproveButton() {
        waitForAngularRequestsToFinish();
        approve.click();
        waitForAngularRequestsToFinish();
    }

    public RejectCreditNoteModal clickRejectButton() {
        waitForAngularRequestsToFinish();
        reject.click();
        return PageFactory.initElements(driver, RejectCreditNoteModal.class).get();
    }
}