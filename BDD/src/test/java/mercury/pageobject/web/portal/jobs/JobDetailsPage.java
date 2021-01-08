package mercury.pageobject.web.portal.jobs;

import java.util.List;

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
import mercury.pageobject.web.Base_Page;

public class JobDetailsPage extends Base_Page<JobDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Job Details";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    // Main content
    // Accept or decline form
    private static final String ACCEPT_DECLINE_FORM_ID = "acceptOrDeclineForm";
    private static final String ACCEPT_DECLINE_FORM_XPATH = ".//form[@id='acceptOrDeclineForm']";

    private static final String ACCEPT_BUTTON_XPATH = ".//*[@id='accepted']/following-sibling::label[contains(text(),'Accept Job')]";
    private static final String DECLINE_BUTTON_XPATH = ".//*[@id='declined']/following-sibling::label[contains(text(),'Decline Job')]";
    private static final String EXACT_DECLINE_REASON_OPTIONS_XPATH =  "/descendant::li[contains(text(),'%s')]";

    //*[@id="acceptOrDeclineForm"]/div[2]/div[1]/div[1]/span[1]/span/span
    // ETA form
    private static final String ETA_DATE_ID = "etaPicker";
    private static final String ETA_DATE_PICKER_CSS = "span.k-widget.k-datepicker.k-header.ng-pristine.ng-untouched.ng-valid.ng-empty > span > span";
    private static final String ETA_DATE_PICKER_CONTAINER_CSS = "div.k-calendar-container";
    private static final String ETA_TIMEBOX_LIST_XPATH_SUFFIX = "/descendant::span[@aria-owns='EtaWindowId_listbox']";
    private static final String CONTACTOR_REFERENCE_ID = "ContractorReference";

    private static final String ETA_DROPDOWN_XPATH = ACCEPT_DECLINE_FORM_XPATH + "//label[contains(text(), 'ETA')]/.." + DROPDOWN_CALENDAR_XPATH;

    private static final String ETA_ADVISED_QUESTION_YES_XPATH = "//label[contains(text(),'%s')]/..//label[text()='Yes']";
    private static final String ETA_ADVISED_QUESTION_NO_XPATH = "//label[contains(text(),'%s')]/..//label[text()='No']";

    private static final String ETA_ADVISED_TO_CSS = "#advisedTo";

    // Decline form
    private static final String DECLINE_REASON_XPATH = ACCEPT_DECLINE_FORM_XPATH + "/descendant::span[@aria-owns='reasonId_listbox']";
    private static final String DECLINE_REASON_OPTIONS_XPATH = ".//*[@id='reasonId_listbox']";
    private static final String DECLINE_REASON_SEARCH_ARROW_XPATH = ACCEPT_DECLINE_FORM_XPATH + "//label[contains(text(), 'Reason')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String DECLINE_TEXT_XPATH = "//Label[contains(text(),'Reason')]/following-sibling::div/descendant::span[contains(@class,'k-input')]";
    private static final String NOTES_DECLINE_ID = "notes";

    private static final String SUGGESTED_RESOURCE_PROFILE_XPATH = ACCEPT_DECLINE_FORM_XPATH + "//label[contains(text(), 'Suggested Resource Profile')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String UPDATE_JOB_BUTTON_XPATH = "//div[@class='form-buttons']//input[@value='Update Job']";

    private static final String TRAVEL_TIME_DROPDOWN_XPATH = "//label[contains(text(), 'Travel Time')]/.." + DROPDOWN_CLOCK_XPATH;
    private static final String OPERATIONAL_ON_ARRIVAL_YES_XPATH = "//label[@for='operationalOnArrivalTrue']";
    private static final String OPERATIONAL_ON_DEPARTURE_YES_XPATH = "//label[@for='statusOnDepartureTrue']";
    private static final String STATUS_ON_DEPARTUE_DROPDOWN_XPATH = "//label[contains(text(), 'Status on Departure')]/following-sibling::div//span[@class='k-select']";
    private static final String STATUS_ON_DEPARTUE_DROPDOWN_OPTION_XPATH = "//li[contains(text(), 'Complete')]";
    private static final String ASSET_CONDITION_DROPDOWN_XPATH = "//label[contains(text(), 'Asset Condition')]/following-sibling::div//span[@class='k-select']";
    private static final String ASSET_CONDITION_DROPDOWN_OPTION_XPATH = "//li[contains(text(), 'Green')]";
    private static final String ROOT_CAUSE_CATEGORY_DROPDOWN_XPATH = "//label[contains(text(), 'Root cause category')]/following-sibling::span//span[@class='k-select']";
    private static final String ROOT_CAUSE_DROPDOWN_XPATH = "//label[text()='Root cause']/following-sibling::span//span[@class='k-select']";
    private static final String DETAILS_TEXTBOX_XPATH = "//textarea[@id='AdditionalNotes']";
    private static final String LABEL_XPATH = "//label[contains(text(), '%s')]";
    private static final String REFRIGERANT_GAS_NO_XPATH = "//label[@for='usesGasFalse']";
    private static final String CHECK_OR_REPAIR_NO_XPATH = "//label[@for='performedLeakCheckFalse']";


    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderText;

    @FindBy(id = ACCEPT_DECLINE_FORM_ID)
    private WebElement acceptDeclineForm;

    @FindBy(xpath = ACCEPT_BUTTON_XPATH)
    private WebElement acceptJob;

    @FindBy(id = ETA_DATE_ID)
    private WebElement eTADate;

    @FindBy(css = ETA_DATE_PICKER_CSS)
    private WebElement eTADatePicker;

    @FindBy(xpath = ETA_DROPDOWN_XPATH)
    private WebElement etaDatePicker1;

    @FindBy(css = ETA_DATE_PICKER_CONTAINER_CSS)
    private WebElement eTADatePickerContainer;

    @FindBy(xpath = ACCEPT_DECLINE_FORM_XPATH + ETA_TIMEBOX_LIST_XPATH_SUFFIX)
    private WebElement eTAWindow;

    @FindBy(id = CONTACTOR_REFERENCE_ID)
    private WebElement contractorReferenceNumber;

    @FindBy(xpath = DECLINE_BUTTON_XPATH)
    private WebElement declineJob;

    @FindBy(xpath = DECLINE_REASON_XPATH)
    private WebElement declineReason;

    @FindBy(xpath = DECLINE_REASON_SEARCH_ARROW_XPATH)
    private WebElement declineReasonSearch;

    @FindBy(xpath = DECLINE_REASON_OPTIONS_XPATH)
    private WebElement declineReasonList;

    @FindBy(xpath = SUGGESTED_RESOURCE_PROFILE_XPATH)
    private WebElement suggestedResource;

    @FindBy(id = NOTES_DECLINE_ID)
    private WebElement declineNotes;

    @FindBy(xpath = TRAVEL_TIME_DROPDOWN_XPATH)
    private WebElement travelTime;

    @FindBy(xpath = OPERATIONAL_ON_ARRIVAL_YES_XPATH)
    private WebElement operationalOnArrivalYes;

    @FindBy(xpath = OPERATIONAL_ON_DEPARTURE_YES_XPATH)
    private WebElement operationalOnDepartureYes;

    @FindBy(xpath = REFRIGERANT_GAS_NO_XPATH)
    private WebElement refrigerantGasNo;

    @FindBy(xpath = CHECK_OR_REPAIR_NO_XPATH)
    private WebElement checkOrRepairNo;

    @FindBy(xpath = DETAILS_TEXTBOX_XPATH)
    private WebElement detailsTextbox;

    @FindBy(xpath = UPDATE_JOB_BUTTON_XPATH)
    private WebElement updateJob;

    @FindBy(css = ETA_ADVISED_TO_CSS)
    private WebElement advisedTo;

    @FindBy(xpath = ACCEPT_DECLINE_FORM_XPATH + DECLINE_TEXT_XPATH)
    private WebElement declineReasonText;

    public JobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            driver.findElement(By.xpath("//h3[contains(translate(text(), 'D', 'd'), 'Job details')]"));
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

    // Page interactions
    public JobDetailsPage setDeclineReason(String declineReason) {
        this.declineReason.click();
        By by = By.xpath(DECLINE_REASON_OPTIONS_XPATH + String.format(EXACT_DECLINE_REASON_OPTIONS_XPATH, declineReason));
        POHelper.isLoaded().isAngularFinishedProcessing()
        .isElementIsClickable(by)
        .isFluentElementIsClickable(by)
        .isFluentElementIsVisible(by);
        WebElement declineThis = waitForElement(by, State.ELEMENT_IS_CLICKABLE);
        declineThis.click();
        return this;
    }

    public void acceptJob() {
        acceptJob.click();
        waitForAngularRequestsToFinish();
    }

    public void declineJob() {
        declineJob.click();
        waitForAngularRequestsToFinish();
    }

    public void setRandomDeclineReason() throws InterruptedException {
        this.waitForAnimation();
        this.waitForAngularRequestsToFinish();
        declineReasonSearch.click();
        this.waitForModalToFadeOut();
        this.selectRandomVisibleDropdownOption();
    }

    public void setDeclineNotes(String declineNotes) {
        this.declineNotes.sendKeys(declineNotes);
    }

    public JobDetailsPage completeDeclineJobForm(String declineReason, String declineNotes) throws InterruptedException{
        declineJob.click();
        POHelper.isLoaded().isFluentElementIsVisible(By.xpath(DECLINE_REASON_XPATH));
        setDeclineReason(declineReason);
        return this;
    }

    public JobDetailsPage selectSuggestedResource(String suggestedResource) {
        waitForElement(By.xpath(SUGGESTED_RESOURCE_PROFILE_XPATH), State.ELEMENT_IS_VISIBLE);
        waitForElement(By.xpath(SUGGESTED_RESOURCE_PROFILE_XPATH), State.ELEMENT_IS_CLICKABLE);
        this.suggestedResource.click();
        selectVisibleDropdownOption(suggestedResource);
        return this;
    }

    public JobDetailsPage enterETADate(String etaDate) throws InterruptedException {
        //		POHelper.isLoaded().isAngularFinishedProcessing().isElementIsClickable(By.cssSelector(ETA_DATE_PICKER_CSS));
        //		eTADatePicker.click();
        this.waitForAngularRequestsToFinish();
        etaDatePicker1.click();
        List<WebElement> days = eTADatePickerContainer.findElements(By.tagName("td"));
        for (WebElement day : days)
        {
            if(day.getText().trim().length()>0) {
                List<WebElement> links = day.findElements(By.tagName("a"));

                for (WebElement alink : links)
                {
                    if( alink.getAttribute("title").equalsIgnoreCase(etaDate)){
                        alink.click();
                        break;
                    }
                }
            }

        }
        return this;
    }

    public AcceptJobPage  completeAcceptJobForm() throws InterruptedException{
        acceptJob.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AcceptJobPage.class).get();
    }

    public JobDetailsPage  setContractorReference(String contractorReferenceNumber) throws InterruptedException{
        this.contractorReferenceNumber.sendKeys(contractorReferenceNumber);
        return this;
    }


    public JobDetailsPage submitForm() {
        acceptDeclineForm.submit();
        return this;
    }

    public void clickYes(String question) {
        waitForAnimation();
        WebElement yes = waitForElement(By.xpath(String.format(ETA_ADVISED_QUESTION_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        yes.click();
    }

    public void clickNo(String question) {
        waitForAnimation();
        WebElement no = waitForElement(By.xpath(String.format(ETA_ADVISED_QUESTION_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        no.click();
    }

    public void enterAdvisedTo(String advisedTo) {
        this.advisedTo.sendKeys(advisedTo);
    }

    public String getReasonText () {
        return declineReasonText.getText();
    }

    public Boolean isDeclineJobDisplayed() {
        return this.isElementPresent(By.xpath(DECLINE_BUTTON_XPATH));
    }

    public Boolean isAcceptJobDisplayed() {
        return this.isElementPresent(By.xpath(ACCEPT_BUTTON_XPATH));
    }

    public Boolean isEtaDropdownDisplayed() {
        return this.isElementPresent(By.xpath(ETA_DROPDOWN_XPATH));
    }

    public Boolean isUpdateJobButtonDisplayed() {
        return this.isElementPresent(By.xpath(UPDATE_JOB_BUTTON_XPATH));
    }

    public void clickUpdateJob() {
        updateJob.click();
        waitForAngularRequestsToFinish();
    }

    public void selectTravelTime() {
        travelTime.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectOperationalOnArrivalYes() {
        operationalOnArrivalYes.click();
        waitForAngularRequestsToFinish();
    }

    public void selectOperationalOnDepartureYes() {
        operationalOnDepartureYes.click();
        waitForAngularRequestsToFinish();
    }

    public void selectStatusOnDeparture() {
        WebElement dropdown = driver.findElement(By.xpath(STATUS_ON_DEPARTUE_DROPDOWN_XPATH));
        dropdown.click();
        waitForAngularRequestsToFinish();
        WebElement option = driver.findElement(By.xpath(STATUS_ON_DEPARTUE_DROPDOWN_OPTION_XPATH));
        option.click();
        waitForAngularRequestsToFinish();
    }

    public void selectAssetCondition() {
        WebElement dropdown = driver.findElement(By.xpath(ASSET_CONDITION_DROPDOWN_XPATH));
        dropdown.click();
        waitForAngularRequestsToFinish();
        WebElement option = driver.findElement(By.xpath(ASSET_CONDITION_DROPDOWN_OPTION_XPATH));
        option.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRootCauseCategory() {
        WebElement dropdown = driver.findElement(By.xpath(ROOT_CAUSE_CATEGORY_DROPDOWN_XPATH));
        dropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectRootCause() {
        WebElement dropdown = driver.findElement(By.xpath(ROOT_CAUSE_DROPDOWN_XPATH));
        dropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void enterDetails(String details) {
        detailsTextbox.sendKeys(details);
    }

    public Boolean isLabelDisplayed(String labelName) {
        return this.isElementPresent(By.xpath(String.format(LABEL_XPATH, labelName)));
    }

    public void selectRefrigerantGasNo() {
        refrigerantGasNo.click();
        waitForAngularRequestsToFinish();
    }

    public void selectCheckOrRepairNo() {
        checkOrRepairNo.click();
        waitForAngularRequestsToFinish();
    }
}
