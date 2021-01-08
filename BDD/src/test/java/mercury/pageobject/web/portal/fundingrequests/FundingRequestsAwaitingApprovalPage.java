package mercury.pageobject.web.portal.fundingrequests;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
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
import mercury.pageobject.web.portal.quotes.SubmitQuoteJobRejectionModalPage;

public class FundingRequestsAwaitingApprovalPage extends Base_Page<FundingRequestsAwaitingApprovalPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class, 'main-content')]";
    private static final String QUOTE_DETAIL_CONTAINER_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'resource-quote-detail__container')]";
    private static final String QUOTE_INPUT_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//div[@class='resource-quote-detail__resource-quote-input']";

    // Internal Job Notes/Queries
    private static final String INTERNAL_JOB_NOTES_OR_QUERIES_INFO_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//h3[contains(text(), 'Internal Job Notes/Queries')]/..//div[@class='resource-quote-detail__resource-quote-info']";
    private static final String INTERNAL_JOB_NOTES_OR_QUERIES_XPATH = INTERNAL_JOB_NOTES_OR_QUERIES_INFO_XPATH + "//div[contains(@class, 'internal-note-for-senior-manager')]//div[contains(@ng-repeat, 'note')]";
    private static final String INTERNAL_JOB_NOTE_OR_QUERY_LABEL_XPATH = INTERNAL_JOB_NOTES_OR_QUERIES_XPATH + "//label";
    private static final String INTERNAL_JOB_NOTE_OR_QUERY_XPATH = INTERNAL_JOB_NOTES_OR_QUERIES_XPATH + "//div";
    private static final String INTERNAL_JOB_NOTE_OR_QUERY_USER_XPATH = INTERNAL_JOB_NOTES_OR_QUERIES_XPATH + "//label//span[contains(@class, 'glyphicon-user')]";

    // Quote Job Rejection Summary
    private static final String QUOTE_JOB_REJECTION_SUMMARY_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//h3[contains(text(), 'Quote Job Rejection Summary')]/..//div[@class='resource-quote-detail__resource-quote-info']";
    private static final String QUOTE_JOB_REJECTION_SUMMARY_RESOURCE_NAME_XPATH = QUOTE_JOB_REJECTION_SUMMARY_XPATH + "//div[@class='quote-submit__resource-name']//p";
    private static final String QUOTE_JOB_REJECTION_SUMMARY_REJECTION_NOTES_XPATH = QUOTE_JOB_REJECTION_SUMMARY_XPATH + "//label[contains(text(), 'Rejection notes')]/following-sibling::div";

    // Resource Quotes
    private static final String RESOURCE_QUOTES_GRID_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//h3[contains(text(), 'Resource Quotes')]/..//table";
    private static final String RESOURCE_QUOTES_GRID_ROW_XPATH = RESOURCE_QUOTES_GRID_XPATH + "//tbody//tr[%s]";

    // Quote Details
    private static final String QUOTE_DETAILS_HEADER_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//h3[contains(text(), 'Quote Details')]/..";
    private static final String QUOTE_DETAILS_XPATH = QUOTE_DETAILS_HEADER_XPATH + "//div[@class='resource-quote-detail__resource-quote-info']";
    private static final String QUOTE_DETAILS_DECISION_LOZENGE_XPATH = QUOTE_DETAILS_XPATH + "//span[contains(@class, 'decision-lozenge')]";
    private static final String QUOTE_DETAILS_DECISION_NOTES_XPATH = QUOTE_DETAILS_XPATH + "//div[@class='note-content']/div";
    private static final String QUOTE_DETIALS_FIELD_XPATH = QUOTE_DETAILS_XPATH + "//div//label[contains(text(), '%s')]/following-sibling::div";
    private static final String QUOTE_DETAILS_GRID_XPATH = QUOTE_DETAILS_XPATH + "//table";

    // Radio Buttons
    private static final String RADIO_BUTTONS_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//div[@class='yesNoRadio']";
    private static final String RESPOND_TO_QUERY_RADIO_BUTTON_XPATH = RADIO_BUTTONS_XPATH + "//label[contains(text(), 'Respond to query')]";
    private static final String CONFIRM_QUOTE_JOB_REJECTION_RADIO_BUTTON_XPATH = RADIO_BUTTONS_XPATH + "//label[contains(text(), 'Confirm Quote Job Rejection')]";
    private static final String EDIT_QUOTE_JOB_RECOMMENDATIONS_RADIO_BUTTON_XPATH = RADIO_BUTTONS_XPATH + "//label[contains(text(), 'Edit Quote Job Recommendations')]";
    private static final String POTENTIAL_INSURANCE_QUOTE_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@name='isPotentialInsurance']";
    private static final String QUOTE_YES_OR_NO_LABELS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'yesNoRadio')]//label[contains(@for, '%s')]";
    private static final String REJECT_RADIO_BUTTON_XPATH = QUOTE_INPUT_XPATH + "//label[contains(text(), 'Reject')]";
    private static final String RECOMMEND_RADIO_BUTTON_XPATH = QUOTE_INPUT_XPATH + "//label[@for='approveRecommendQuote']";
    private static final String APPROVE_RADIO_BUTTON_XPATH = QUOTE_INPUT_XPATH + "//label[contains(text(), 'Approve')]";

    // Dropdowns
    private static final String DROPDOWN_BUDGET_ROUTE_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//h3[contains(text(), 'Budget Route')]/..//select";
    private static final String REJECT_REASON_DROPDOWN_XPATH = QUOTE_INPUT_XPATH + "//label[contains(text(), 'Rejection reason')]/following-sibling::select";

    // Textboxes
    private static final String RESPOND_TO_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//label[contains(text(), 'Respond to')]/following-sibling::textarea";
    private static final String RECOMMENDATION_NOTES_TO_SENIOR_MANAGER_XPATH = QUOTE_INPUT_XPATH + "//label[contains(text(), 'Recommendation notes to senior manager')]/following-sibling::textarea";
    private static final String REJECTION_NOTES_XPATH = QUOTE_INPUT_XPATH + "//label[contains(text(), 'Rejection notes')]/following-sibling::textarea";

    // Buttons
    private static final String SEND_RESPONSE_BUTTON_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//button[contains(text(), 'Send response')]";
    private static final String FORM_BUTTONS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'form-buttons')]";
    private static final String PROCEED_TO_NEXT_QUOTE_XPATH = FORM_BUTTONS_XPATH + "//input[@id='SaveDecision']";
    private static final String SUBMIT_BUTTON_XPATH = FORM_BUTTONS_XPATH + "//input[@id='Submit']";

    private static final String SUBMIT_QUOTE_JOB_RECOMMENDATION_MODAL_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='quoteSubmissionModal']";
    private static final String QUERY_BUTTON_XPATH = FORM_BUTTONS_XPATH + "//button[contains(text(), 'Query')]";
    private static final String QUERY_QUOTE_BUTTON_XPATH = QUOTE_DETAILS_HEADER_XPATH + "//button[contains(text(), 'Query')]";

    // Web Elements
    @FindBy(xpath = RESPOND_TO_QUERY_RADIO_BUTTON_XPATH)
    private WebElement respondToQueryRadioButton;

    @FindBy(xpath = CONFIRM_QUOTE_JOB_REJECTION_RADIO_BUTTON_XPATH)
    private WebElement confirmQuoteJobRejectionRadioButton;

    @FindBy(xpath = EDIT_QUOTE_JOB_RECOMMENDATIONS_RADIO_BUTTON_XPATH)
    private WebElement editQuoteJobRecommendationsRadioButton;

    @FindBy(xpath = DROPDOWN_BUDGET_ROUTE_XPATH)
    private WebElement budgetRouteDropdown;

    @FindBy(xpath = RESPOND_TO_XPATH)
    private WebElement respondTo;

    @FindBy(xpath = SEND_RESPONSE_BUTTON_XPATH)
    private WebElement sendResponseButton;

    @FindBy(xpath = REJECT_RADIO_BUTTON_XPATH)
    private WebElement rejectRadioButton;

    @FindBy(xpath = RECOMMEND_RADIO_BUTTON_XPATH)
    private WebElement recommendRadioButton;

    @FindBy(xpath = APPROVE_RADIO_BUTTON_XPATH)
    private WebElement approveRadioButton;

    @FindBy(xpath = RECOMMENDATION_NOTES_TO_SENIOR_MANAGER_XPATH)
    private WebElement recommendationNotesToSeniorManager;

    @FindBy(xpath = REJECT_REASON_DROPDOWN_XPATH)
    private WebElement rejectReason;

    @FindBy(xpath = REJECTION_NOTES_XPATH)
    private WebElement rejectionNotes;

    @FindBy(xpath = PROCEED_TO_NEXT_QUOTE_XPATH)
    private WebElement proceedToNextQuote;

    @FindBy(xpath = SUBMIT_BUTTON_XPATH)
    private WebElement submit;

    @FindBy(xpath = INTERNAL_JOB_NOTE_OR_QUERY_LABEL_XPATH)
    private List<WebElement> internalJobNoteOrQueryLabel;

    @FindBy(xpath = INTERNAL_JOB_NOTE_OR_QUERY_XPATH)
    private List<WebElement> internalJobOrQuery;

    @FindBy(xpath = INTERNAL_JOB_NOTE_OR_QUERY_USER_XPATH)
    private List<WebElement> internalJobOrQueryUser;

    @FindBy(xpath = QUOTE_JOB_REJECTION_SUMMARY_RESOURCE_NAME_XPATH)
    private List<WebElement> quoteJobRejectionSummaryResourceName;

    @FindBy(xpath = QUOTE_JOB_REJECTION_SUMMARY_REJECTION_NOTES_XPATH)
    private List<WebElement> quoteJobRejectionSummaryRejectionNotes;

    @FindBy(xpath = QUERY_BUTTON_XPATH)
    private WebElement queryButton;

    @FindBy(xpath = QUERY_QUOTE_BUTTON_XPATH)
    private WebElement queryQuoteButton;

    @FindBy(xpath = QUOTE_DETAILS_DECISION_LOZENGE_XPATH)
    private WebElement quoteDetailsDecisionLozenge;

    @FindBy(xpath = QUOTE_DETAILS_DECISION_NOTES_XPATH)
    private WebElement quoteDetailsDecisionNotes;


    // Methods
    public FundingRequestsAwaitingApprovalPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(QUOTE_DETAIL_CONTAINER_XPATH + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
            logger.info(QUOTE_DETAIL_CONTAINER_XPATH + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(QUOTE_DETAIL_CONTAINER_XPATH + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(QUOTE_DETAIL_CONTAINER_XPATH + " Load");
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }

    public void clickRespondToQueryRadioButton() {
        respondToQueryRadioButton.click();
        waitForAngularRequestsToFinish();
    }

    public SubmitQuoteJobRejectionModalPage clickConfirmQuoteJobRejectionRadioButton() {
        confirmQuoteJobRejectionRadioButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, SubmitQuoteJobRejectionModalPage.class).get();
    }

    public void clickEditQuoteJobRecommendationsRadioButton() {
        editQuoteJobRecommendationsRadioButton.click();
        waitForAngularRequestsToFinish();
    }

    public void enterResponse(String response) {
        respondTo.sendKeys(response);
    }

    public boolean isSendResponseButtonEnabled() {
        return isElementClickable(By.xpath(SEND_RESPONSE_BUTTON_XPATH));
    }

    public String getBudgetRoute() {
        return getSelectSelectedText(budgetRouteDropdown);
    }

    public boolean getBudgetRouteDropdownEditState() {
        return isElementClickable(By.xpath(DROPDOWN_BUDGET_ROUTE_XPATH));
    }

    public FundingRequestQueriesPage clickSendResponseButton() {
        sendResponseButton.click();
        waitForAnimation();
        return PageFactory.initElements(driver, FundingRequestQueriesPage.class).get();
    }

    public Grid getResourceQuotesGrid() {
        return GridHelper.getGrid(RESOURCE_QUOTES_GRID_XPATH);
    }

    public void clickRecommendRadioButton() {
        recommendRadioButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickApproveRadioButton() {
        approveRadioButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickRejectRadioButton() {
        rejectRadioButton.click();
        waitForAngularRequestsToFinish();
    }

    public void enterRecommendationNotesToSeniorManager(String notes) {
        recommendationNotesToSeniorManager.sendKeys(notes);
    }

    public void proceedToNextQuote() {
        proceedToNextQuote.click();
        waitForAngularRequestsToFinish();
    }

    public SubmitFundingRequestModalPage clickSubmitButton() {
        submit.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, SubmitFundingRequestModalPage.class).get();
    }

    public int getResourceQuotesGridRows() {
        return GridHelper.getNumberOfDisplayedRows(RESOURCE_QUOTES_GRID_XPATH);
    }

    public void selectResourceQuote(Integer quoteRow) {
        By by = By.xpath(String.format(RESOURCE_QUOTES_GRID_ROW_XPATH, quoteRow));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        POHelper.clickJavascript(by);
        waitForAngularRequestsToFinish();
    }

    public boolean isSubmitQuoteJobRecommendationModalDisplayed() {
        return driver.findElement(By.xpath(SUBMIT_QUOTE_JOB_RECOMMENDATION_MODAL_XPATH)).isDisplayed();
    }

    public String getInternalJobNoteOrQueryLabel(int index) {
        return internalJobNoteOrQueryLabel.get(index).getText();
    }

    public String selectRejectReason() {
        return selectRandomOptionFromSelect(rejectReason);
    }

    public void enterRejectionNotes(String text) {
        rejectionNotes.sendKeys(text);
    }

    public String getInternalJobNoteOrQuery(int index) {
        return internalJobOrQuery.get(index).getText();
    }

    public boolean isInternalJobNoteOrQueryUserDisplayed(int index) {
        return internalJobOrQueryUser.get(index).isDisplayed();
    }

    public String getQuoteJobRejectionSummaryResourceName(int index) {
        return quoteJobRejectionSummaryResourceName.get(index).getText();
    }

    public String getQuoteJobRejectionSummaryRejectionNotes(int index) {
        return quoteJobRejectionSummaryRejectionNotes.get(index).getText();
    }

    public QueryInitialApproverModal clickQueryButton() {
        queryButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, QueryInitialApproverModal.class).get();
    }

    public boolean isQueryQuoteButtonDisplayed() {
        return queryQuoteButton.isDisplayed();
    }

    public String getQuoteDetailsDecisionLozengeText() {
        return quoteDetailsDecisionLozenge.getText();
    }

    public String getQuoteDetailsDecisionNotes() {
        return quoteDetailsDecisionNotes.getText();
    }

    public String getQuoteDetailsFieldValue(String fieldName) {
        WebElement field = driver.findElement(By.xpath(String.format(QUOTE_DETIALS_FIELD_XPATH, fieldName)));
        return field.getText();
    }

    public Grid getQuoteDetailsGrid() {
        return GridHelper.getGrid(QUOTE_DETAILS_GRID_XPATH);
    }

    public void selectRandomAnswerForIsPotentialInsuranceQuote() {
        List<WebElement> potentialInsurance = driver.findElements(By.xpath(POTENTIAL_INSURANCE_QUOTE_XPATH));
        int randomSelection = RandomUtils.nextInt(0, potentialInsurance.size());
        String id = potentialInsurance.get(randomSelection).getAttribute("id");
        WebElement potentialInsuranceQuote = driver.findElement(By.xpath(String.format(QUOTE_YES_OR_NO_LABELS_XPATH, id)));
        potentialInsuranceQuote.click();
    }

    public boolean isProceedToNextQuoteButtonVisible() {
        return isElementClickable(By.xpath(PROCEED_TO_NEXT_QUOTE_XPATH));
    }

}
