package mercury.pageobject.web.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskFundingRequestsPanel extends Base_Page<HelpdeskFundingRequestsPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String FUNDING_REQUEST_FORM_XPATH = "//form[@name='fundingRequestForm']//div[contains(@class, 'resource-action__content')]";

    private static final String FUNDING_REQUEST_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class,'resource-funding-request__action-panel')]";
    private static final String REQUESTED_AMOUNT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'col-xs-2')]/div";

    // ACTIONS
    private static final String AUTHORIZE_XPATH = FUNDING_REQUEST_FORM_XPATH + "//input[@type='radio' and @id='fr_authorise']/following-sibling::label";
    private static final String REJECT_XPATH = FUNDING_REQUEST_FORM_XPATH + "//input[@type='radio' and @id='fr_reject']/following-sibling::label";

    //Create Uplift Request
    private static final String AMOUNT_FUNDING_REQUEST_XPATH= FUNDING_REQUEST_FORM_XPATH + "//input[@name='calloutFee']";
    private static final String DESCRIPTION_FUNDING_REQUEST_XPATH= FUNDING_REQUEST_FORM_XPATH + "//textarea[@name='description']";
    private static final String DONT_KNOW_THE_AMOUNT_XPATH = FUNDING_REQUEST_FORM_XPATH + "//a[contains(text(), \"Don't know the amount?\")]";
    private static final String DROPDOWN_REASON_FOR_NO_AMOUNT = FUNDING_REQUEST_FORM_XPATH + "//label[contains(text(), 'Reason for no amount')]/following-sibling::span" + DROPDOWN_SEARCH_XPATH;

    //Uplift Request
    private static final String DROPDOWN_REJECT_REASON_XPATH = FUNDING_REQUEST_PANEL_XPATH + "//label[contains(text(), 'Reason')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String DROPDOWN_REJECTED_BY_XPATH = FUNDING_REQUEST_PANEL_XPATH + "//label[contains(text(), 'Rejected By')]/../following-sibling::div//ph-foreign-key" + DROPDOWN_SEARCH_XPATH;
    private static final String DROPDOWN_AUTHORIZED_BY_XPATH = FUNDING_REQUEST_PANEL_XPATH + "//ph-foreign-key[@name='authorisedBy0']" + DROPDOWN_SEARCH_XPATH;
    private static final String AMOUNT_XPATH = FUNDING_REQUEST_PANEL_XPATH + "//label[contains(text(),'Amount')]/../following-sibling::div//input[@name='authoriseAmount0']";
    private static final String DROPDOWN_REASON_FOR_FUNDING_REQUEST_XPATH = FUNDING_REQUEST_PANEL_XPATH + "//label[contains(text(), 'Reason for funding request')]/.." + DROPDOWN_SEARCH_XPATH;
    private static final String DROPDOWN_FUNDING_XPATH = FUNDING_REQUEST_FORM_XPATH + "//select[(@name='fundingRoute0')]";
    private static final String POTENTIAL_INSURANCE_RADIO_BUTTON_XPATH = FUNDING_REQUEST_FORM_XPATH + "//label[contains(text(), 'Is this a potential insurance job?')]/../..//div[@class='yesNoRadio']//input";
    private static final String POTENTIAL_INSURANCE_LABELS_XPATH = FUNDING_REQUEST_FORM_XPATH + "//div[contains(@class, 'yesNoRadio')]//label[contains(@for, '%s')]";
    private static final String REJECT_NOTES_XPATH = FUNDING_REQUEST_PANEL_XPATH + "//textarea[@name='rejectionNote0']";
    private static final String AUTHORISE_NOTES_XPATH = FUNDING_REQUEST_PANEL_XPATH + "//textarea[@name='authoriseNotes0']";

    //BUTTONS
    private static final String BUTTON_CONTAINER_XPATH = FUNDING_REQUEST_FORM_XPATH + "//div[@class='resource-action__button-container']";
    private static final String SAVE_BUTTON_XPATH = BUTTON_CONTAINER_XPATH + "//button[@class='btn btn-primary']";
    private static final String CANCEL_BUTTON_XPATH = BUTTON_CONTAINER_XPATH + "//button[contains(text(), 'Cancel')]";

    @FindBy(xpath = FUNDING_REQUEST_PANEL_XPATH)
    private WebElement fundingRequestPanel;

    @FindBy(xpath = REQUESTED_AMOUNT_XPATH)
    private WebElement requestedAmount;

    @FindBy(xpath = AMOUNT_FUNDING_REQUEST_XPATH)
    private WebElement amountFundingRequest;

    @FindBy(xpath = DESCRIPTION_FUNDING_REQUEST_XPATH)
    private WebElement descriptionFundingRequest;

    @FindBy(xpath = DROPDOWN_REJECT_REASON_XPATH)
    private WebElement rejectReasonDropdown;

    @FindBy(xpath = DROPDOWN_FUNDING_XPATH)
    private WebElement fundingDropdown;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = REJECT_NOTES_XPATH)
    private WebElement rejectNotes;

    @FindBy(xpath = AUTHORISE_NOTES_XPATH)
    private WebElement authoriseNotes;

    @FindBy(xpath = AUTHORIZE_XPATH)
    private WebElement authorize;

    @FindBy(xpath = REJECT_XPATH)
    private WebElement reject;

    @FindBy(xpath = DROPDOWN_REJECTED_BY_XPATH)
    private WebElement rejectedByDropdown;

    @FindBy(xpath = DROPDOWN_AUTHORIZED_BY_XPATH)
    private WebElement authorizedByDropdown;

    @FindBy(xpath = DONT_KNOW_THE_AMOUNT_XPATH)
    private WebElement dontKnowTheAmount;

    @FindBy(xpath = DROPDOWN_REASON_FOR_NO_AMOUNT)
    private WebElement reasonForNoAmountDropdown;

    @FindBy(xpath = AMOUNT_XPATH)
    private WebElement amount;

    @FindBy(xpath = DROPDOWN_REASON_FOR_FUNDING_REQUEST_XPATH)
    private WebElement reasonForFundingRequestDropdown;


    public HelpdeskFundingRequestsPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try{
            waitForAngularRequestsToFinish();
            assertTrue("Funding Requests panel not loaded!", driver.findElement(By.xpath(FUNDING_REQUEST_FORM_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectReason(String reason) {
        rejectReasonDropdown.click();
        waitForAngularRequestsToFinish();
        selectExactVisibleDropdownOption(reason);
    }

    public void enterRejectNotes(String notes) {
        rejectNotes.sendKeys(notes);
    }

    public String getAmountInFundingRequest() {
        return amountFundingRequest.getText();
    }

    public String getAmount() {
        return requestedAmount.getText();
    }

    public String getDescriptionInFundingRequest() {
        return descriptionFundingRequest.getText();
    }

    public void save() {
        saveButton.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isSaveButtonDisplayed() {
        return isElementPresent(By.xpath(SAVE_BUTTON_XPATH));
    }

    public void setAmountInFundingRequest(String amount) {
        POHelper.sendKeys(amountFundingRequest, amount);
    }

    public void setDescriptionInFundingRequest(String description) {
        descriptionFundingRequest.sendKeys(description);
    }

    public void authorize() {
        authorize.click();
        waitForAngularRequestsToFinish();
    }

    public void reject() {
        reject.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRandomReason() {
        rejectReasonDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRandomFunding() {
        selectRandomOptionFromSelect(fundingDropdown);
        checkForAndConfirmAlert();
    }

    public void selectFundingRoute(String fundingRoute) {
        selectOptionFromSelect(fundingDropdown, fundingRoute);
        checkForAndConfirmAlert();
    }

    public void enterAuthoriseNotes(String notes) {
        authoriseNotes.sendKeys(notes);
    }

    public String getRejectedBy() {
        return rejectedByDropdown.getText();
    }

    public String selectRandomRejectedBy() {
        rejectedByDropdown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public boolean isReasonDropdownVisible() {
        return isElementClickable(By.xpath(DROPDOWN_REJECT_REASON_XPATH));
    }

    public String getAuthorizedBy() {
        return authorizedByDropdown.getText();
    }

    public String selectRandomAuthorizedBy() {
        authorizedByDropdown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String selectRandomReasonForFundingRequest() {
        reasonForFundingRequestDropdown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String getReasonForFundingRequest() {
        return reasonForFundingRequestDropdown.getText();
    }

    public String getFunding() {
        return getSelectSelectedText(fundingDropdown);
    }

    public boolean isFundingEnabled() {
        return isElementClickable(By.xpath(DROPDOWN_FUNDING_XPATH));
    }

    public void clickDontKnowTheAmount() {
        dontKnowTheAmount.click();
        waitForAngularRequestsToFinish();
    }

    public String selectReasonForNoAmount() {
        reasonForNoAmountDropdown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public void setAmount(String text) {
        amount.sendKeys(text);
    }

    public boolean isAmountVisible() {
        return isElementVisible(By.xpath(AMOUNT_XPATH));
    }

    public boolean isDescriptionFundingRequestDisplayed() {
        return isElementPresent(By.xpath(DESCRIPTION_FUNDING_REQUEST_XPATH));
    }

    public boolean isAmountFundingRequestDisplayed() {
        return isElementPresent(By.xpath(AMOUNT_FUNDING_REQUEST_XPATH));
    }

    public boolean isSaveButtonClickable() {
        return isElementClickable(By.xpath(SAVE_BUTTON_XPATH));
    }

    public boolean isCancelButtonClickable() {
        return isElementClickable(By.xpath(CANCEL_BUTTON_XPATH));
    }

    public boolean isDisplayed() {
        return fundingRequestPanel.isDisplayed();
    }

    public boolean isFundingRequestSelectable() {
        return fundingDropdown.getAttribute("disabled") == null;
    }

    public void selectRandomAnswerForIsPotentialInsuranceQuestion() {
        List<WebElement> potentialInsurance = driver.findElements(By.xpath(POTENTIAL_INSURANCE_RADIO_BUTTON_XPATH));
        int randomSelection = RandomUtils.nextInt(0, potentialInsurance.size());
        String id = potentialInsurance.get(randomSelection).getAttribute("id");
        WebElement potentialInsuranceQuote = driver.findElement(By.xpath(String.format(POTENTIAL_INSURANCE_LABELS_XPATH, id)));
        potentialInsuranceQuote.click();
        waitForAngularRequestsToFinish();
    }
}
