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

public class JobsAwaitingQuotePage extends Base_Page<JobsAwaitingQuotePage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Jobs Awaiting Quote";

    //Content
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='awaiting-supplier-quote_wrapper']";
    private static final String GRID_CSS = "#awaiting-supplier-quote_wrapper";
    private static final String GRID_FILTER_CSS  = GRID_CSS + " #awaiting-supplier-quote_filter";
    private static final String GRID_SEARCH_BOX_CSS = GRID_FILTER_CSS + " input[type='search']";

    //Table rows
    private static final String TABLE_BODY_XPATH = GRID_XPATH + "//table/tbody";

    private static final String TABLE_ROW_JOB_XPATH =  TABLE_BODY_XPATH + "/descendant::td[contains(text(),'%s')]/parent::tr";

    private static final String TABLE_ROW_JOB_DECLINE_BUTTON = TABLE_ROW_JOB_XPATH + "//a[contains(@class, 'btn-decline')]";
    private static final String TABLE_ROW_JOB_ACCEPT_QUOTE_BUTTON = TABLE_ROW_JOB_XPATH + "//button[contains(@data-role, 'accept-quote') and not(contains(@class,'hidden'))]";
    private static final String TABLE_ROW_JOB_CREATE_QUOTE_BUTTON = TABLE_ROW_JOB_XPATH + "//a[contains(@data-role, 'create-quote')]";

    @FindBy(css = GRID_SEARCH_BOX_CSS)
    private WebElement searchBox;

    public JobsAwaitingQuotePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {

            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(GRID_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(GRID_XPATH));
    }

    // Page Interactions
    public JobsAwaitingQuotePage searchJobs(String searchQuery) {
        POHelper.sendKeys(searchBox, searchQuery);
        waitForLoadingToComplete();
        waitForAnimation();
        return this;
    }

    public Boolean isDeclineQuoteJobDisplayed(String jobReference) {
        return this.isElementPresent(By.xpath(String.format(TABLE_ROW_JOB_DECLINE_BUTTON, jobReference)));
    }

    public Boolean isAcceptQuoteJobDisplayed(String jobReference) {
        return this.isElementPresent(By.xpath(String.format(TABLE_ROW_JOB_ACCEPT_QUOTE_BUTTON, jobReference)));
    }

    public Boolean isCreateQuoteJobDisplayed(String jobReference) {
        return this.isElementPresent(By.xpath(String.format(TABLE_ROW_JOB_CREATE_QUOTE_BUTTON, jobReference)));
    }

    public JobsAwaitingQuotePage acceptQuoteJobRequest(Integer jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_JOB_ACCEPT_QUOTE_BUTTON, jobReference));
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return this;
    }

    public DeclineInvitationToQuotePage declineQuoteJobRequest(Integer jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_JOB_DECLINE_BUTTON, jobReference));
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, DeclineInvitationToQuotePage.class).get();
    }

    public CreateQuotePage createQuoteJobRequest(Integer jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_JOB_CREATE_QUOTE_BUTTON, jobReference));
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, CreateQuotePage.class).get();
    }

    public RegisterQuotePage registerQuoteJobRequest(Integer jobReference) {
        By by = By.xpath(String.format(TABLE_ROW_JOB_CREATE_QUOTE_BUTTON, jobReference));
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, RegisterQuotePage.class).get();
    }

}
