package mercury.pageobject.web.portal.quotes;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class RegisterQuotePage   extends Base_Page<RegisterQuotePage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Regiserer Quote";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CSS = "div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CSS + " div.main-content";
    private static final String PAGE_MAIN_CONTENT_XPATH = "//div[contains(@class,'main-content')]";

    // Core
    private static final String QUOTE_LINES_BODY_CSS = " #QuoteLinesBody";
    private static final String QUOTE_DETAIL_LINES_CSS = QUOTE_LINES_BODY_CSS + " #detail-lines";
    private static final String QUOTE_ADD_LINE_CSS = QUOTE_DETAIL_LINES_CSS + " a.add-detail-line";
    private static final String SUBMIT_QUOTE_CSS = PAGE_MAIN_CONTENT + " input.btn.btn-primary";

    private static final String QUOTE_DOCUMENTS_TABLE_XPATH = "//*[@id='upload-docs']";
    private static final String UPLOAD_QUOTE_DOCUMENT_XPATH = QUOTE_DOCUMENTS_TABLE_XPATH + "//td[contains(text(),'Quote')]/..//a";
    private static final String UPLOAD_SUPPORTING_DOCUMENT_XPATH = QUOTE_DOCUMENTS_TABLE_XPATH + "//td[contains(text(),'Supporting Documents')]/..//a";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='QuoteLinesBody']";
    private static final String GRID_ROW_XPATH = "//td[contains(text(),'%s')]";
    private static final String GRID_ROW_DELETE_XPATH = GRID_ROW_XPATH + "/following-sibling::td//a[contains(@class,'delete-detail-line')]";
    private static final String GRID_ROW_EDIT_XPATH = GRID_ROW_XPATH + "/following-sibling::td//a[contains(@class,'edit-detail-line')]";

    private static final String QUOTE_EDIT_BUTTON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//a[@id='QuoteHeaderTable']/following-sibling::table//td[@class='center-text' ]//a[text()='Edit']";
    private static final String UPDATE_SUBMITTED_QUOTE_BUTTON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form//p//input[@value='Update Submitted Quote']";


    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = QUOTE_ADD_LINE_CSS)
    private WebElement addLinesToQuote;

    @FindBy(css  = SUBMIT_QUOTE_CSS)
    private WebElement submiteQuote;

    @FindBy(xpath = UPLOAD_QUOTE_DOCUMENT_XPATH)
    private WebElement uploadQuoteDocument;

    @FindBy(xpath = UPLOAD_SUPPORTING_DOCUMENT_XPATH)
    private WebElement uploadSupportingDocument;

    @FindBy(xpath = QUOTE_EDIT_BUTTON_XPATH)
    private WebElement quoteEditButton;

    @FindBy(xpath = UPDATE_SUBMITTED_QUOTE_BUTTON_XPATH)
    private WebElement updateSubmittedQuoteButton;

    public RegisterQuotePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(QUOTE_ADD_LINE_CSS));
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
        return this.isElementClickable(By.cssSelector(QUOTE_ADD_LINE_CSS));
    }


    // Page interactions

    public AddQuoteLinePage addLine() {
        waitForElement(By.cssSelector(QUOTE_ADD_LINE_CSS), State.ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(addLinesToQuote);
        this.waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AddQuoteLinePage.class).get();
    }

    public DeleteQuoteLinePage deleteLine(String lineDescription) {
        WebElement delete = waitForElement(By.xpath(String.format(GRID_ROW_DELETE_XPATH, lineDescription)), ELEMENT_IS_CLICKABLE);
        POHelper.scrollToElement(delete);
        delete.click();
        this.waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, DeleteQuoteLinePage.class).get();
    }

    public void  editLine(String lineDescription) {
        By by = By.xpath(String.format(GRID_ROW_EDIT_XPATH, lineDescription));
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
    }
    public UploadQuotePage uploadQuoteDocument() {
        waitForElement(By.xpath(UPLOAD_QUOTE_DOCUMENT_XPATH), State.ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(uploadQuoteDocument);
        this.waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, UploadQuotePage.class).get();
    }

    public void submitQuote() {
        waitForElement(By.cssSelector(SUBMIT_QUOTE_CSS), State.ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(submiteQuote);
        waitForAngularRequestsToFinish();
    }

    public Boolean isSubmitQuoteJobDisplayed() {
        return this.isElementPresent(By.cssSelector(SUBMIT_QUOTE_CSS));
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public Grid getGrid() {
        waitForModalToFadeOut();
        return GridHelper.getGrid(GRID_XPATH);
    }

    public EditQuoteDetailsPage clickEditQuoteButton() {
        quoteEditButton.click();
        return PageFactory.initElements(driver, EditQuoteDetailsPage.class).get();
    }

    public QuoteQueryPage clickUpdateSubmittedQuoteButton() {
        updateSubmittedQuoteButton.click();
        return PageFactory.initElements(driver, QuoteQueryPage.class).get();
    }
}
