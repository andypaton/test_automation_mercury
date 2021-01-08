package mercury.pageobject.web.portal.quotes;

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

public class QuoteQueryPage extends Base_Page<QuoteQueryPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Quote Query";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='quote-query-pending_wrapper']";

    //Table rows
    private static final String TABLE_BODY_XPATH = GRID_XPATH + "//table/tbody";
    private static final String TABLE_ROWS_XPATH = TABLE_BODY_XPATH + "//tr";
    private static final String TABLE_ROW_TYPE_XPATH_SUFFIX = TABLE_ROWS_XPATH + "/descendant::td[contains(text(),'%s')]";

    //Quote query response form
    private static final String QUOTE_QURY_FORM = PAGE_MAIN_CONTENT_XPATH + "//form";
    private static final String QUOTE_QUERY_FIELDSET = QUOTE_QURY_FORM + "//fieldset[contains(@class,'quote-query')]";
    private static final String QUOTE_QUERY_RESPONSE_FIELD = QUOTE_QUERY_FIELDSET + "//textarea[@id='Response']"; //div[contains(@class,'editor-field')]
    
    private static final String REASON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div//form//h3[contains(text(),'Reason')]";
    private static final String QUERY_TEXT_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div//form//p[@class='query-text']";

    private static final String QUOTE_QUERY_BUTTONS = QUOTE_QUERY_FIELDSET + "//p[contains(@class,'buttons')]";
    private static final String QUOTE_QUERY_SEND_RESPONSE = QUOTE_QUERY_BUTTONS + "//input[contains(@class, 'btn') and contains(@class, 'btn-primary')]";
    private static final String QUOTE_QUERY_EDIT_QUOTE = QUOTE_QUERY_BUTTONS + "//a[contains(@class, 'btn') and contains(@class, 'btn-secondary') and contains(text(), 'Edit Quote')]";


    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + "a.btn.btn-secondary.btn-neutral";


    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;


    @FindBy(xpath = QUOTE_QUERY_RESPONSE_FIELD)
    private WebElement response;

    @FindBy(xpath = QUOTE_QUERY_SEND_RESPONSE)
    private WebElement sendResponse;

    @FindBy(xpath = QUOTE_QUERY_EDIT_QUOTE)
    private WebElement editQuote;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;
    
    @FindBy(xpath = REASON_XPATH)
    private WebElement reason;
    
    @FindBy(xpath = QUERY_TEXT_XPATH)
    private WebElement queryText;

    // Page methods
    public QuoteQueryPage(WebDriver driver) {
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
        return pageHeaderTest.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }

    // Page Interactions

    public QuotesManagersDecisionPage OpenJob(String jobReference) throws InterruptedException {
        waitForAnimation();
        By by = By.xpath(GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, jobReference));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAnimation();
        return PageFactory.initElements(driver, QuotesManagersDecisionPage.class).get();
    }

    public void setQuoteQueryResponse(String response) {
        this.response.sendKeys(response);
    }

    public QuotesWithQueryPendingPage submitResponse() {
        this.sendResponse.click();
        return PageFactory.initElements(driver, QuotesWithQueryPendingPage.class).get();
    }
    
    public void clickSendResponseButton() {
        sendResponse.click();
    }

    public Boolean isSendResponseButtonDisplayed() {
        return this.isElementPresent(By.xpath(QUOTE_QUERY_SEND_RESPONSE));
    }
    
    public String getReasonText() {
        return reason.getText();
    }
    
    public String getQueryText() {
        return queryText.getText();
    }

    public RegisterQuotePage clickEditQuoteButton() {
        editQuote.click();
        return PageFactory.initElements(driver, RegisterQuotePage.class).get();
    }
}
