package mercury.pageobject.web.helpdesk.incidents;

import static mercury.helpers.POHelper.scrollTo;
import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskLogAnIncidentPage extends Base_Page<HelpdeskLogAnIncidentPage>{

    private static final Logger logger = LogManager.getLogger();

    //WORKSPACE
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String LOG_AN_INCIDENT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='incident view incident-form']";
    private static final String INCIDENT_PAGE_XPATH = "//div[@class='incident view incident-form']";
    private static final String INCIDENT_SITE_QUESTIONS = INCIDENT_PAGE_XPATH + "//div[@name = 'Site questions']";

    // HEADERS
    private static final String HEADER_XPATH = LOG_AN_INCIDENT_XPATH + "//div[contains(@class, 'view-header__headline')]";
    private static final String SUBHEADER_XPATH = LOG_AN_INCIDENT_XPATH + "//div[contains(@class, 'view-header__subhead')]";

    //DESCRIPTION
    private static final String DESCRIPTION_TEXTBOX_XPATH = LOG_AN_INCIDENT_XPATH + "//form[@name='descriptionForm']//div/textarea[contains(@id,'Description')]";

    // CORE DETAILS
    private static final String RESOURCE_CALLER_DROPDOWN_XPATH = "//div[contains(@class,'logging-core-details')]//span[@class='k-icon k-i-arrow-s']";
    private static final String RESOURCE_CALLER_TEXTBOX_XPATH = "//div[contains(@class,'logging-core-details')]//label[text()='Resource Caller']/.." + DROPDOWN_SEARCH_XPATH;
    private static final String CORE_DETAILS_XPATH = LOG_AN_INCIDENT_XPATH + "//label[text()='%s']/following-sibling::input";

    //SITE QUESTIONS RADIO BUTTONS
    private static final String YES_SITE_CLOSED_RADIO_BUTTON = LOG_AN_INCIDENT_XPATH + "//input[@type='radio' and (@id='SiteClosedYes') ]";
    private static final String YES_NO_RADIO_BUTTON = "//div[@class='yesNoRadio']";
    private static final String NO_SITE_CLOSED_RADIO_BUTTON = LOG_AN_INCIDENT_XPATH + YES_NO_RADIO_BUTTON + "//input[@id='SiteClosedNo']";
    private static final String YES_SITE_REOPENED_RADIO_BUTTON = LOG_AN_INCIDENT_XPATH + YES_NO_RADIO_BUTTON + "//input[@id='SiteReopenedYes']";
    private static final String NO_SITE_REOPENED_RADIO_BUTTON = LOG_AN_INCIDENT_XPATH + YES_NO_RADIO_BUTTON + "//input[@id='SiteReopenedNo']";
    private static final String YES_DEPARTMENT_CLOSED_RADIO_BUTTON = LOG_AN_INCIDENT_XPATH + YES_NO_RADIO_BUTTON + "//input[@id='DepartmentClosedYes']";
    private static final String NO_DEPARTMENT_CLOSED_RADIO_BUTTON = LOG_AN_INCIDENT_XPATH + YES_NO_RADIO_BUTTON + "//input[@id='DepartmentClosedNo']";
    private static final String YES_DEPARTMENT_REOPENED_RADIO_BUTTON = LOG_AN_INCIDENT_XPATH + YES_NO_RADIO_BUTTON + "//input[@id='DepartmentReopenedYes']";
    private static final String NO_DEPARTMENT_REOPENED_RADIO_BUTTON = LOG_AN_INCIDENT_XPATH + YES_NO_RADIO_BUTTON + "//input[@id='DepartmentReopenedNo']";

    //DATE PICKER
    private static final String DATE_AND_TIME_OF_INCIDENT_CLOCK = INCIDENT_PAGE_XPATH + "//div[contains (@class,'job-deferral__deferralDate')]" + DROPDOWN_CLOCK_XPATH;
    private static final String SITE_CLOSED_XPATH = "//label[contains(text(), 'Site closed?')]/../..//label[contains(text(),'Closed at')]/..";
    private static final String SITE_CLOSED_CLOCK_XPATH = INCIDENT_SITE_QUESTIONS + SITE_CLOSED_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String SITE_REOPENED_XPATH = "//label[contains(text(), 'Site Re-opened?')]/../..//label[contains(text(),'Re-opened at')]/..";
    private static final String SITE_LIKELY_TO_REOPEN_XPATH = "//label[contains(text(), 'Site Re-opened?')]/../..//label[contains(text(),'Likely to re-open at')]/..";
    private static final String SITE_REOPENED_CLOCK_XPATH = INCIDENT_SITE_QUESTIONS + SITE_REOPENED_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String SITE__LIKELY_TO_REOPEN_CLOCK_XPATH = INCIDENT_SITE_QUESTIONS + SITE_LIKELY_TO_REOPEN_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String DEPARTMENT_CLOSED_XPATH = "//label[contains(text(), 'Department closed') or contains(text(),'Location closed')]/../..//label[contains(text(),'Closed at')]/..";
    private static final String DEPARTMENT_CLOSED_CLOCK_XPATH = INCIDENT_SITE_QUESTIONS + DEPARTMENT_CLOSED_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String DEPARTMENT_REOPENED_XPATH = "//label[contains(text(), 'Department re-opened') or contains(text(),'Location re-opened')]/../..//label[contains(text(),'Re-opened at')]/..";
    private static final String DEPARTMENT_REOPENED_CLOCK_XPATH = INCIDENT_SITE_QUESTIONS + DEPARTMENT_REOPENED_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String DEPARTMENT_LIKELY_TO_REOPEN_XPATH = "//label[contains(text(), 'Department re-opened') or contains(text(),'Location re-opened')]/../..//label[contains(text(),'Likely to be open at')]/..";
    private static final String DEPARTMENT_LIKELY_TO_REOPEN_CLOCK_XPATH = INCIDENT_SITE_QUESTIONS + DEPARTMENT_LIKELY_TO_REOPEN_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String DEPARTMENT_CLOSED_AT_INPUT_XPATH = INCIDENT_SITE_QUESTIONS + DEPARTMENT_CLOSED_XPATH + DROPDOWN_PICK_WRAPPER_XPATH + "//input";
    private static final String DEPARTMENT_LIKELY_TO_BE_OPEN_AT_INPUT_XPATH = INCIDENT_SITE_QUESTIONS + DEPARTMENT_LIKELY_TO_REOPEN_XPATH + DROPDOWN_PICK_WRAPPER_XPATH + "//input";
    private static final String DEPARTMENT_REOPENED_AT_INPUT_XPATH = INCIDENT_SITE_QUESTIONS + DEPARTMENT_REOPENED_XPATH + DROPDOWN_PICK_WRAPPER_XPATH + "//input";

    //INCIDENT TYPES
    private static final String INCIDENT_TYPES_GRID = LOG_AN_INCIDENT_XPATH + "//div[contains(@class,'view-main-content') and @name = 'Incident Types']";
    private static final String INCIDENT_TYPE_XPATH = LOG_AN_INCIDENT_XPATH + "//div[@class='row incident-types']//label[" + LOWERCASE_TITLE + "= '%s']";

    private static final String INCIDENT_TYPE_FILTER_TEXT_BOX = LOG_AN_INCIDENT_XPATH + "//form[@name = 'incidentTypeForm']//input[contains(@class,'pull-right ng')]";

    //REFRIGERATION OUTAGE
    private static final String DROPDOWN_INCIDENT_QUESTION = LOG_AN_INCIDENT_XPATH + "//div[@class='incident-questions__row']//label[contains(text(), '%s')]/following-sibling::span[@class='incident-questions__input-container']//select";
    private static final String TEXTBOX_STOCK_LOSS_DETAILS = LOG_AN_INCIDENT_XPATH + "//div[@class='incident-questions__row']//label[contains(text(), 'Please give details of the stock loss')]/following-sibling::span[@class='incident-questions__input-container']//input";

    // QUESTIONS
    private static final String INCIDENT_QUESTIONS_XPATH = LOG_AN_INCIDENT_XPATH + "//div[@name='Questions']//label[@class='incident-questions__label']";
    private static final String INCIDENT_QUESTION_XPATH = LOG_AN_INCIDENT_XPATH + "//div[@name='Questions']//label[@class='incident-questions__label' and contains(text(),\"%s\")]";
    private static final String INCIDENT_QUESTION_TYPE_XPATH = INCIDENT_QUESTION_XPATH + "/..//*[@question='vmIncidentQuestionsActive.question']";
    private static final String INCIDENT_QUESTION_DROPDOWN_XPATH = INCIDENT_QUESTION_XPATH + "/..//select";
    private static final String INCIDENT_QUESTION_CALENDAR_INPUT_XPATH = INCIDENT_QUESTION_XPATH + "/..//input[@type='text'][1]";
    private static final String INCIDENT_QUESTION_INPUT_TEXT_XPATH = INCIDENT_QUESTION_XPATH + "/..//input[@type='text']";

    //BUTTON ACTIONS
    private static final String SAVE_BUTTON_XPATH = LOG_AN_INCIDENT_XPATH + "//button[contains(@class,'btn-primary')][contains(text(),'Save')]";
    private static final String CANCEL_BUTTON_XPATH = LOG_AN_INCIDENT_XPATH + "//div[contains(@class,'view-button-bar')]//button[contains(text(),'Cancel')]";

    private static final String COMPLETE_REVIEW_CHECKBOX = LOG_AN_INCIDENT_XPATH + "//div[@class='todo-banner']//input[@type='checkbox']";
    private static final String SAVE_ONLY_BUTTON = LOG_AN_INCIDENT_XPATH + "//button[contains(@class,'btn-primary')][contains(text(),'Save only')]";

    @FindBy(xpath = ACTIVE_WORKSPACE_XPATH)
    private WebElement activeWorkspace;

    @FindBy(xpath = LOG_AN_INCIDENT_XPATH)
    private WebElement logAnIncident;

    @FindBy(xpath = HEADER_XPATH)
    private WebElement headline;

    @FindBy(xpath = SUBHEADER_XPATH)
    private WebElement subHeadline;

    @FindBy(xpath = DESCRIPTION_TEXTBOX_XPATH)
    private WebElement descriptionText;

    @FindBy(xpath = RESOURCE_CALLER_DROPDOWN_XPATH)
    private WebElement callerDropdown;

    @FindBy(xpath = YES_SITE_CLOSED_RADIO_BUTTON)
    private WebElement yesSiteClosedRadio;

    @FindBy(xpath = NO_SITE_CLOSED_RADIO_BUTTON)
    private WebElement noSiteClosedRadio;

    @FindBy(xpath = YES_SITE_REOPENED_RADIO_BUTTON)
    private WebElement yesSiteReopenedRadio;

    @FindBy(xpath = NO_SITE_REOPENED_RADIO_BUTTON)
    private WebElement noSiteReopenedRadio;

    @FindBy(xpath = YES_DEPARTMENT_CLOSED_RADIO_BUTTON)
    private WebElement yesDepartmentClosedRadio;

    @FindBy(xpath = NO_DEPARTMENT_CLOSED_RADIO_BUTTON)
    private WebElement noDepartmentClosedRadio;

    @FindBy(xpath = YES_DEPARTMENT_REOPENED_RADIO_BUTTON)
    private WebElement yesDepartmentReopenedRadio;

    @FindBy(xpath = NO_DEPARTMENT_REOPENED_RADIO_BUTTON)
    private WebElement noDepartmentReopenedRadio;

    @FindBy(xpath = DATE_AND_TIME_OF_INCIDENT_CLOCK)
    private WebElement dateAndTimeClock;

    @FindBy(xpath = SITE_CLOSED_CLOCK_XPATH)
    private WebElement siteClosedClock;

    @FindBy(xpath = SITE_REOPENED_CLOCK_XPATH)
    private WebElement siteReopenedClock;

    @FindBy(xpath = SITE__LIKELY_TO_REOPEN_CLOCK_XPATH)
    private WebElement siteLikelyToReopenClock;

    @FindBy(xpath = DEPARTMENT_CLOSED_CLOCK_XPATH)
    private WebElement departmentClosedClock;

    @FindBy(xpath = DEPARTMENT_CLOSED_AT_INPUT_XPATH)
    private WebElement departmentClosedDatePicker;

    @FindBy(xpath = DEPARTMENT_LIKELY_TO_BE_OPEN_AT_INPUT_XPATH)
    private WebElement departmentLikelyToReopenDatePicker;

    @FindBy(xpath = DEPARTMENT_REOPENED_AT_INPUT_XPATH)
    private WebElement departmentReopenDatePicker;

    @FindBy(xpath = DEPARTMENT_REOPENED_CLOCK_XPATH)
    private WebElement departmentReopenedClock;

    @FindBy(xpath = DEPARTMENT_LIKELY_TO_REOPEN_CLOCK_XPATH)
    private WebElement departmentLikelyToReopenClock;

    @FindBy(xpath = INCIDENT_TYPES_GRID)
    private WebElement incidentTypeGrid;

    @FindBy(xpath = INCIDENT_TYPE_FILTER_TEXT_BOX)
    private WebElement filterTypeTextBox;

    @FindBy(xpath = TEXTBOX_STOCK_LOSS_DETAILS)
    private WebElement stockLossTextbox;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = COMPLETE_REVIEW_CHECKBOX)
    private WebElement completeReviewCheckbox;

    @FindBy(xpath = SAVE_ONLY_BUTTON)
    private WebElement saveOnlyButton;

    @FindBy(xpath = RESOURCE_CALLER_TEXTBOX_XPATH)
    private WebElement callerTextbox;

    public HelpdeskLogAnIncidentPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", logAnIncident.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isIncidentPageLoaded() {
        return isElementPresent(By.xpath(LOG_AN_INCIDENT_XPATH));
    }

    public boolean isIncidentHeaderDisplayed() {
        return isElementPresent(By.xpath(HEADER_XPATH));
    }

    public boolean isIncidentSubHeaderDisplayed() {
        return isElementPresent(By.xpath(SUBHEADER_XPATH));
    }

    public HelpdeskLogAnIncidentPage addIncidentDetails(String incidentDescription) {
        descriptionText.sendKeys(incidentDescription);
        return this;
    }

    public void selectCaller(String caller) throws InterruptedException {
        callerDropdown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(caller);
    }

    public String getCaller() {
        return callerDropdown.getText().replace("\nselect", "");
    }

    public void selectIncidentClock() {
        dateAndTimeClock.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public String getHeadLine() {
        return headline.getText();
    }

    public String getSubheadLine() {
        return subHeadline.getText();
    }

    public void clickSiteClosedYesRadio() {
        POHelper.clickJavascript(yesSiteClosedRadio);
    }

    public void clickSiteClosedNoRadio() {
        POHelper.clickJavascript(noSiteClosedRadio);
    }

    public void selectSiteClosedDate() {
        siteClosedClock.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectSiteReopenedDate(){
        siteReopenedClock.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectSiteLikelyToReopenDate(){
        siteLikelyToReopenClock.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectDepartmentClosedDate(){
        departmentClosedClock.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectDepartmentReopenedDate(){
        departmentReopenedClock.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectDepartmentLikelyToReopenDate(){
        departmentLikelyToReopenClock.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void clickDepartmentClosedNoRadio() {
        POHelper.clickJavascript(noDepartmentClosedRadio);
    }

    public void clickDepartmentClosedYesRadio() {
        POHelper.clickJavascript(yesDepartmentClosedRadio);
    }

    public void clickSiteReopenedNoRadio() {
        POHelper.clickJavascript(noSiteReopenedRadio);
    }

    public void clickSiteReopenedYesRadio() {
        POHelper.clickJavascript(yesSiteReopenedRadio);
    }

    public void clickDepartmentReopenedNoRadio() {
        POHelper.clickJavascript(noDepartmentReopenedRadio);
    }

    public void clickIncidentType(String incidentTypeValue) throws Throwable{
        WebElement incidentType = driver.findElement(By.xpath(String.format(INCIDENT_TYPE_XPATH, incidentTypeValue)));
        incidentType.click();
        //waitForAngularRequestsToFinish();
    }

    public void clickDepartmentReopenedYesRadio() {
        POHelper.clickJavascript(yesDepartmentReopenedRadio);
    }

    public void addStockLossDetails(String details) throws Throwable{
        stockLossTextbox.sendKeys(details);
        stockLossTextbox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public String getStockLossDetails() throws Throwable{
        return stockLossTextbox.getAttribute("value");
    }


    public void saveButtonIsClicked() throws Throwable {
        saveButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectQuestionAnswer(String question, String answer) {
        WebElement element = waitForElement(By.xpath(String.format(DROPDOWN_INCIDENT_QUESTION,question)),State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
        selectOptionFromSelect(element, answer);
        WebElement dropdown = driver.switchTo().activeElement();
        dropdown.sendKeys(Keys.ESCAPE);
        waitForAngularRequestsToFinish();
    }

    public void clickCompleteReviewCheckbox() {
        POHelper.clickJavascript(completeReviewCheckbox);
    }

    public HelpdeskViewIncidentPage clickSaveOnlyButton() {
        saveOnlyButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskViewIncidentPage.class).get();
    }

    public String selectCaller() throws Throwable {
        POHelper.clickJavascript(callerDropdown);
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption(1);
    }

    public String getCallerValue() throws Throwable {
        return callerTextbox.getAttribute("value");
    }

    public String getDepartmentClosedDateValue() throws Throwable{
        return departmentClosedDatePicker.getAttribute("value");
    }

    public String getDepartmentLikelyToReopenedDateValue() throws Throwable{
        return departmentLikelyToReopenDatePicker.getAttribute("value");
    }

    public String getSelectedCallerValue() {
        return callerTextbox.getAttribute("innerText");
    }

    public boolean IsDescriptionTextboxEnabled() {
        return descriptionText.isEnabled();
    }

    public boolean IsSiteClosedYesRadioEnabled() {
        return yesSiteClosedRadio.isEnabled();
    }

    public boolean IsSiteClosedNoRadioEnabled() {
        return noSiteClosedRadio.isEnabled();
    }

    public boolean IsAnswerFieldEnabled(String questionText) {
        WebElement element = driver.findElement(By.xpath(String.format(DROPDOWN_INCIDENT_QUESTION,questionText)));
        return element.isEnabled();
    }

    public String getDepartmentReopenedDateValue() {
        return departmentReopenDatePicker.getAttribute("value");
    }

    public String getPhoneNo() {
        WebElement element = driver.findElement(By.xpath(String.format(CORE_DETAILS_XPATH,"Phone No")));
        return element.getAttribute("value");
    }

    public String getExtension() {
        WebElement element = driver.findElement(By.xpath(String.format(CORE_DETAILS_XPATH,"Extension")));
        return element.getAttribute("value");
    }

    public String getCallerType() {
        WebElement element = driver.findElement(By.xpath(String.format(CORE_DETAILS_XPATH,"Caller Type")));
        return element.getAttribute("value");
    }

    public List<String> getIncidentQuestions() {
        List<String> questions = new ArrayList<>();
        if (isElementPresent(By.xpath(INCIDENT_QUESTIONS_XPATH))) {
            for (WebElement jobQuestion : driver.findElements(By.xpath(INCIDENT_QUESTIONS_XPATH))) {
                questions.add(jobQuestion.getText());
            }
        }
        return questions;
    }

    public String getIncidentQuestionTagName(String question) {
        WebElement element = driver.findElement(By.xpath(String.format(INCIDENT_QUESTION_TYPE_XPATH, question)));
        return element.getTagName();
    }

    public String questionSelectRandomOption(String question) {
        //WebElement dropdown = waitForElement(By.xpath(String.format(INCIDENT_QUESTION_DROPDOWN_XPATH, question)), ELEMENT_IS_CLICKABLE);
        WebElement dropdown = driver.findElement(By.xpath(String.format(INCIDENT_QUESTION_DROPDOWN_XPATH, question)));
        String selection = selectRandomOptionFromSelect(dropdown);
        return selection;
    }

    public void questionSelectOption(String question, String option) {
        WebElement dropdown = waitForElement(By.xpath(String.format(INCIDENT_QUESTION_DROPDOWN_XPATH, question)), ELEMENT_IS_CLICKABLE);
        int pos = Integer.valueOf(dropdown.getLocation().getY());
        scrollTo(pos);
        dropdown.click();
        waitForAngularRequestsToFinish();
        selectOptionFromSelect(dropdown, option);
    }

    public void questionEnterText(String question, String text) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(INCIDENT_QUESTION_INPUT_TEXT_XPATH, question)), ELEMENT_IS_VISIBLE);
        jobQuestion.clear();
        jobQuestion.sendKeys(text);
        jobQuestion.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    /**
     *
     * @param question
     * @param dateTime : eg. '7/25/2018 2:30 AM'
     */
    public void enterDateTime(String question, String dateTime) {
        WebElement cal = waitForElement(By.xpath(String.format(INCIDENT_QUESTION_CALENDAR_INPUT_XPATH, question)), State.ELEMENT_IS_VISIBLE);
        cal.clear();
        cal.sendKeys(dateTime);
    }

    public String getDescription() {
        waitForAngularRequestsToFinish();
        return descriptionText.getAttribute("value");
    }

    public Boolean isIncidentTypeSelected(String incidentType) {
        return driver.findElement(By.xpath(String.format(INCIDENT_TYPE_XPATH, incidentType))).isSelected();
    }

}