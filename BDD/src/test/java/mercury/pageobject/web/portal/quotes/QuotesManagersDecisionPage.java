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
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class QuotesManagersDecisionPage  extends Base_Page<QuotesManagersDecisionPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Quote Managers Decision";

    // This page is soooo ugly from an html point of view
    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CSS = "div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";
    private static final String PAGE_HEADER_XPATH = "//div[contains(@class,'body-content')]//h1";

    // core
    private static final String PAGE_ALERT_CSS = PAGE_MAIN_CONTENT + " div:not(.ng-hide).quote-alert div.alert-text";
    private static final String QUOTE_QUERY_REPONSE_ALERT_CSS = PAGE_MAIN_CONTENT + " div[ng-show*=quoteJobIsInQuery] div.alert-text";

    private static final String QUOTE_APPROVAL_FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//mc-quote-approval";
    private static final String BUDGET_ROUTE_CSS = PAGE_MAIN_CONTENT + " div.resource-quote-detail__container.has-cols.read-only select";
    private static final String AWAITING_QUOTES_GRID_XPATH = QUOTE_APPROVAL_FORM_XPATH + "//div//mc-resource-quotes/div";
    private static final String AWAITING_QUOTES_ROW_XPATH =  AWAITING_QUOTES_GRID_XPATH + "//table/tbody/tr[%s]";


    private static final String APPROVE_REJECT_CSS = PAGE_MAIN_CONTENT + " div.resource-quote-detail__resource-quote-input div.yesNoRadio";
    private static final String REJECT_QUOTE_CSS = APPROVE_REJECT_CSS + " label[for=rejectQuote]";
    private static final String RECOMMEND_QUOTE_CSS = APPROVE_REJECT_CSS + " label[for=approveRecommendQuote]";

    private static final String RECOMMEND_QUOTE_NOTES_XPATH = PAGE_MAIN_CONTENT_XPATH + "//textarea[@id='decisionNotes']";
    private static final String PROCEED_TO_NEXT_QUOTE_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@id='SaveDecision']";

    private static final String QUOTE_DETAILS_CSS = PAGE_MAIN_CONTENT + " mc-resource-quote-details div.resource-quote-detail__container.has-cols.read-only";
    private static final String QUERY_QUOTE_CSS = QUOTE_DETAILS_CSS + " button.btn.btn-primary";
    private static final String FORM_BUTTONS_CSS = "div.row.form-buttons";
    private static final String CANCEL_CHANGES_CSS = FORM_BUTTONS_CSS + " a.btn.btn-secondary.btn-destructive";
    private static final String SUBMIT_CHANGES_CSS = FORM_BUTTONS_CSS + " #Submit.btn.btn-primary";
    private static final String REJECT_RECOMMEND_FORM_CSS = "ng-form[name=decisionInput]";
    private static final String REJECT_REASON_CSS = REJECT_RECOMMEND_FORM_CSS + " select";
    private static final String REJECT_APPROVE_REASON_NOTES_CSS = REJECT_RECOMMEND_FORM_CSS +  " #decisionNotes";
    private static final String QUOTE_QUERY_HISTORY_XPATH = "//mc-resource-quote-query-history";

    private static final String QUOTE_DETAILS_XPATH = "//h3[text()='Quote Details']/..";
    private static final String DESCRIPTION_OF_WORKS_VALUE_XPATH = QUOTE_DETAILS_XPATH + "//div//label[contains(text(),'Description of works')]/following-sibling::div";
    private static final String PROPOSED_WORKING_TIMES_VALUE_XPATH = QUOTE_DETAILS_XPATH + "//div//label[contains(text(),'Proposed working times')]/following-sibling::div[1]";
    private static final String QUOTE_DETAILS_GRID_XPATH = QUOTE_DETAILS_XPATH + "//table[@class='header-margin']";

    //Alert
    private static final String CONFIRMATION_ALERT_XPATH = "//div[@class='sweet-alert showSweetAlert visible']";
    private static final String CONFIRM_ALERT_BUTTON_XPATH = CONFIRMATION_ALERT_XPATH + "//button[@class='confirm']";

    // Following are the best that can be done on the page!!!
    private static final String SHOW_QUOTE_QUERY_HISTORY_XPATH = QUOTE_QUERY_HISTORY_XPATH + "//a[contains(text(), 'Show quote query history')]";
    private static final String HIDE_QUOTE_QUERY_HISTORY_XPATH = QUOTE_QUERY_HISTORY_XPATH + "//a[contains(text(), 'Hide quote query history')]";
    private static final String QUOTE_QUERY_HISTORY_GRID_XPATH = QUOTE_QUERY_HISTORY_XPATH + "//div[contains(@class,'resource-quote-detail__container') and contains(@class,'has-cols') and contains(@class,'read-only')]";

    private static final String MODAL_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='queryResourceQuoteModal' and contains(@class,'modal') and contains(@class,'fade') and contains(@class,'in')]";
    private static final String JOB_DETAILS_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'resource-quote-detail__container') and contains(@class,'read-only')]";

    // WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = PAGE_ALERT_CSS)
    private WebElement pageAlert;

    @FindBy(css = QUOTE_QUERY_REPONSE_ALERT_CSS)
    private WebElement quoteQueryAlert;

    @FindBy(css = BUDGET_ROUTE_CSS)
    private WebElement budgetRoute;

    @FindBy(xpath = CONFIRM_ALERT_BUTTON_XPATH)
    private WebElement confirmAlert;

    @FindBy(css = REJECT_QUOTE_CSS)
    private WebElement rejectQuote;

    @FindBy(css = REJECT_REASON_CSS)
    private WebElement rejectReason;

    @FindBy(css = REJECT_APPROVE_REASON_NOTES_CSS)
    private WebElement reasonNotes;

    @FindBy(css = RECOMMEND_QUOTE_CSS)
    private WebElement recommendQuote;

    @FindBy(css = CANCEL_CHANGES_CSS)
    private WebElement cancelChanges;

    @FindBy(css = SUBMIT_CHANGES_CSS)
    private WebElement submitChanges;

    @FindBy(css = QUERY_QUOTE_CSS)
    private WebElement queryQuote;

    @FindBy(xpath = SHOW_QUOTE_QUERY_HISTORY_XPATH)
    private WebElement showQueryHistory;

    @FindBy(xpath = HIDE_QUOTE_QUERY_HISTORY_XPATH)
    private WebElement hideQueryHistory;

    @FindBy(xpath = QUOTE_QUERY_HISTORY_GRID_XPATH)
    private WebElement queryHistory;

    @FindBy(xpath = JOB_DETAILS_XPATH)
    private WebElement jobDetails;

    @FindBy(xpath = DESCRIPTION_OF_WORKS_VALUE_XPATH)
    private WebElement descriptionOfWorks;

    @FindBy(xpath = PROPOSED_WORKING_TIMES_VALUE_XPATH)
    private WebElement proposedWorkingTime;

    @FindBy(xpath = RECOMMEND_QUOTE_NOTES_XPATH)
    private WebElement recommendQuoteNotes;

    @FindBy(xpath = PROCEED_TO_NEXT_QUOTE_XPATH)
    private WebElement proceedToNextQuote;

    // Page Methods
    public QuotesManagersDecisionPage(WebDriver driver) {
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

    // Page interactions
    public void rejectQuote() throws InterruptedException {
        this.waitForAngularRequestsToFinish();
        WebElement rejectLabel = waitForElement(By.cssSelector(REJECT_QUOTE_CSS), ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(rejectLabel);
    }

    public void selectRejectReason() {
        waitForKendoLoadingToComplete();
        waitForAnimation();
        waitForAngularRequestsToFinish();
        selectRandomOptionFromSelect(rejectReason);
    }

    public void setRejectApproveNotes(String notes)	 {
        reasonNotes.sendKeys(notes);

    }

    public void recommendQuote() throws Throwable {
        this.waitForAngularRequestsToFinish();
        WebElement recommendLabel = waitForElement(By.cssSelector(RECOMMEND_QUOTE_CSS), ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(recommendLabel);
    }

    public SubmitQuoteJobRejectionModalPage submitReject() {
        this.waitForAngularRequestsToFinish();
        this.submitChanges.click();
        return PageFactory.initElements(driver, SubmitQuoteJobRejectionModalPage.class).get();
    }

    public SubmitQuoteJobRecommendModalPage submitRecommend() {
        this.submitChanges.click();
        return PageFactory.initElements(driver, SubmitQuoteJobRecommendModalPage.class).get();
    }

    public SubmitQuoteQueryModalPage queryQuote() {
        queryQuote.click();
        return PageFactory.initElements(driver, SubmitQuoteQueryModalPage.class).get();
    }

    public void showQuoteHistory() {
        showQueryHistory.click();
        this.waitForAngularRequestsToFinish();
    }

    public void hideQuoteHistory() {
        hideQueryHistory.click();
    }

    public void selectBudgetRoute(String budget) {
        budgetRoute.click();
        selectOptionFromSelect(budgetRoute, budget);
    }

    public boolean isConfirmationAlertDisplayed() {
        return isElementPresent(By.xpath(CONFIRMATION_ALERT_XPATH));
    }

    public void clickConfirmationAlert() {
        confirmAlert.click();
    }

    public void selectQuote(Integer quoteRow) {
        waitUntilElementCountToBe(By.xpath(MODAL_XPATH), 0);

        By by = By.xpath(String.format(AWAITING_QUOTES_ROW_XPATH, quoteRow));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        waitForAngularRequestsToFinish();
        driver.findElement(by).click();
    }

    public void enterRecommendQuoteNotes(String notes) {
        recommendQuoteNotes.sendKeys(notes);
    }

    public void proceedToNextQuote() {
        proceedToNextQuote.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isProceedToNextQuoteButtonVisible() {
        return isElementClickable(By.xpath(PROCEED_TO_NEXT_QUOTE_XPATH));
    }

    // Page get values
    public String getPageAlert() {
        return pageAlert.getText();
    }

    public Boolean isQuoteQueryResponseAlertDisplayed() {
        return quoteQueryAlert.isDisplayed();
    }

    public String getQueryResponseAlert() {
        return quoteQueryAlert.getText();
    }

    public String getRejectReasonValue() {
        return getSelectSelectedValue(rejectReason);
    }

    public String getRejectReasonText() {
        return getSelectSelectedText(rejectReason);
    }

    public Integer getRejectReasonCount() {
        return this.getSelectOptionsCount(rejectReason);
    }

    public Integer getNumberOfQueryHistorRows() {
        return GridHelper.getNumberOfDisplayedRows(QUOTE_QUERY_HISTORY_GRID_XPATH);
    }

    public Grid getHistoryGrid() {
        this.waitForAngularRequestsToFinish();
        return GridHelper.getGrid(QUOTE_QUERY_HISTORY_GRID_XPATH);
    }

    public Integer getNumberOfAwaitingQuotesRows() {
        return GridHelper.getNumberOfDisplayedRows(AWAITING_QUOTES_GRID_XPATH);
    }

    public Grid getGrid() {
        this.waitForAngularRequestsToFinish();
        return GridHelper.getGrid(AWAITING_QUOTES_GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(AWAITING_QUOTES_GRID_XPATH, 1);
    }

    public boolean isJobDetailViewOnly() {
        return jobDetails.isDisplayed();
    }

    public String getDescriptionOfWorksValue() {
        return descriptionOfWorks.getText();
    }

    public String getProposedWorkingTime() {
        return proposedWorkingTime.getText();
    }

    public Grid getQuoteDetailsGrid() {
        return GridHelper.getGrid(QUOTE_DETAILS_GRID_XPATH);
    }
}
