package mercury.pageobject.web.portal.ppm;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class PPMJobDetailsPage extends Base_Page<PPMJobDetailsPage> {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "PPM Details";

    // Main content
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div h1";
    private static final String JOB_DETAIL_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String UPDATE_JOB_FORM_XPATH = "//form[@name='PPMShutDownForm']";

    // Core
    private static final String ETA_DATE_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'ETA Date')]/.." + DROPDOWN_CLOCK_XPATH;
    private static final String TRAVEL_TIME_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH+"//label[contains(text(), 'Travel Time')]/.." + DROPDOWN_CLOCK_XPATH;
    private static final String STATUS_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Status')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String SUB_STATUS_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Sub')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String WORK_START_TIME_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Work Start')]/.." + DROPDOWN_CLOCK_XPATH;
    private static final String WORK_START_DATE_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Work Start')]/.." + DROPDOWN_CALENDAR_XPATH ;
    private static final String WORK_END_TIME_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Work End')]/.." + DROPDOWN_CLOCK_XPATH;

    private static final String PPM_DETAILS_TITLE_XPATH = ".//div[contains(@class,'resource-detail__container has-cols read-only wide')]/h3";
    private static final String PPM_TYPE_LABEL_XPATH = "//label[contains(text(), 'PPM Type')]";
    private static final String PPM_REMEDIAL_LABEL_XPATH = "//label[contains(text(), 'Do you want to raise a PPM remedial Job?')]";
    private static final String PPM_REQUEST_QUOTE_LABEL_XPATH = "//label[contains(text(), 'Request Quote')]";
    private static final String ACTION_BUTTONS_XPATH = "//div[contains(@class, 'form-group bottom-buttons')]";
    private static final String PPM_SAVE_BUTTON_XPATH = ACTION_BUTTONS_XPATH + "//button[contains(@class,'btn btn-primary') and contains(text(), 'Save')]";
    private static final String PPM_CANCEL_BUTTON_XPATH = ACTION_BUTTONS_XPATH + "//button[contains(@class,'btn btn-destructive btn-secondary') and contains(text(), 'Cancel')]";
    private static final String NOTES_TEXTBOX_PPM_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Notes')]/..//textarea";

    private static final String GSAN_YES_BUTTON_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Yes')]";
    private static final String GSAN_NO_BUTTON_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'No')]";
    private static final String ADD_GSAN_BUTTON_XPATH = UPDATE_JOB_FORM_XPATH + "//div[@class='row btn btn-primary']//div[@class='col-md-12']//div";
    private static final String ADD_NEW_CERTIFICATE_XPATH = UPDATE_JOB_FORM_XPATH + "//button[contains(text(), 'Add new certificate')]";


    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeader;

    @FindBy(xpath = PPM_DETAILS_TITLE_XPATH)
    private WebElement ppmDetailsTitle;

    @FindBy(xpath = PPM_TYPE_LABEL_XPATH)
    private WebElement ppmTypeLabel;

    @FindBy(xpath = PPM_REMEDIAL_LABEL_XPATH)
    private WebElement ppmRemedialLabel;

    @FindBy(xpath = TRAVEL_TIME_DROPDOWN_XPATH)
    private WebElement travelTimeDropDown;

    @FindBy(xpath = WORK_START_DATE_XPATH)
    private WebElement workStartDate;

    @FindBy(xpath = WORK_START_TIME_XPATH)
    private WebElement workStartTime;

    @FindBy(xpath = WORK_END_TIME_XPATH)
    private WebElement workEndTime;

    @FindBy(xpath = STATUS_DROPDOWN_XPATH)
    private WebElement status;

    @FindBy(xpath = SUB_STATUS_DROPDOWN_XPATH)
    private WebElement subStatus;

    @FindBy(xpath = NOTES_TEXTBOX_PPM_XPATH)
    private WebElement notesTextBox;

    @FindBy(xpath = PPM_SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = PPM_CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = PPM_REQUEST_QUOTE_LABEL_XPATH)
    private WebElement ppmRequestQuoteLabel;

    @FindBy(xpath = ETA_DATE_DROPDOWN_XPATH)
    private WebElement etaDateTime;

    @FindBy(xpath = GSAN_YES_BUTTON_XPATH)
    private WebElement gsanYes;

    @FindBy(xpath = GSAN_NO_BUTTON_XPATH)
    private WebElement gsanNo;

    @FindBy(xpath = ADD_GSAN_BUTTON_XPATH)
    private WebElement addGsan;

    @FindBy(xpath = ADD_NEW_CERTIFICATE_XPATH)
    private WebElement addCertificate;

    public PPMJobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(JOB_DETAIL_CONTAINER_XPATH));
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

    public Boolean isPageLoaded() {
        waitForElement(By.xpath(PPM_CANCEL_BUTTON_XPATH),ELEMENT_IS_VISIBLE);
        return this.isElementClickable(By.xpath(PPM_CANCEL_BUTTON_XPATH));
    }

    // Page interactions
    public String getPageHeader() {
        return pageHeader.getText();
    }

    public String getPpmDetailsTitle() {
        return ppmDetailsTitle.getText();
    }

    public String getPpmTypeLabel() {
        return ppmTypeLabel.getText();
    }

    public boolean isPpmRemedialLabelDisplayed() {
        return isElementVisible(By.xpath(PPM_REMEDIAL_LABEL_XPATH));
    }

    public void selectTravelTime(){
        travelTimeDropDown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectWorkStartDateAndTime(){
        workStartTime.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectWorkEndTimeAndDate(){
        POHelper.scrollToElement(workEndTime);
        workEndTime.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectStatus(String option){
        status.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(option);
    }

    public void selectRandomSubStatus(){
        subStatus.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void enterNote(String note){
        notesTextBox.sendKeys(note);
    }

    public void saveButton(){
        saveButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectETATimeAndDate() throws InterruptedException {
        POHelper.scrollToElement(etaDateTime);
        etaDateTime.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
    }

    public void selectRequestRemedialJob(){
        ppmRemedialLabel.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRequestQuote(){
        ppmRequestQuoteLabel.click();
        waitForAngularRequestsToFinish();
    }

    public void selectYesToGsanQuestion() {
        gsanYes.click();
        waitForAngularRequestsToFinish();
    }

    public void selectNoToGsanQuestion() {
        gsanNo.click();
        waitForAngularRequestsToFinish();
    }

    public void addGsan() {
        addGsan.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isAddNewCertificateButtonDisplayed() {
        return this.isElementVisible(By.xpath(ADD_NEW_CERTIFICATE_XPATH));
    }

    public void addNewCertificate() {
        addCertificate.click();
        waitForAngularRequestsToFinish();
    }

}

