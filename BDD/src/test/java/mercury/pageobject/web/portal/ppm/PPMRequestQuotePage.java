package mercury.pageobject.web.portal.ppm;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.portal.jobs.UpdateSavedPage;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class PPMRequestQuotePage extends Base_Page<PPMRequestQuotePage> {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "PPM Details";

    // Main content
    private static final String UPDATE_JOB_FORM_XPATH = "//form[@name='PPMShutDownForm']";
    private static final String REQUEST_QUOTE_FORM_XPATH = "//div[@id='create_quote_form'] ";

    //Labels
    private static final String REQUEST_QUOTE_FAULT_TYPE_LABEL_XPATH = UPDATE_JOB_FORM_XPATH +"//label[contains(text(), 'Fault Type')]";
    private static final String REQUEST_QUOTE_LOCATION_LABEL_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Location')]";
    private static final String REQUEST_QUOTE_PRIORITY_LABEL_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Select Priority')]";

    //Drop downs
    private static final String QUOTE_ASSET_MAIN_TYPE_DROPDOWN_XPATH = "//*[@id='quoteAssetTypeId']" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String QUOTE_ASSET_SUB_TYPE_DROPDOWN_XPATH = "//*[@id='quoteAssetSubTypeId']" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String PPM_QUOTE_LOCATION_DROPDOWN_XPATH = REQUEST_QUOTE_LOCATION_LABEL_XPATH + "/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String QUOTE_PRIORITY_DROPDOWN_XPATH = REQUEST_QUOTE_PRIORITY_LABEL_XPATH + "/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String QUOTE_FAULT_TYPE_DROPDOWN_XPATH = REQUEST_QUOTE_FAULT_TYPE_LABEL_XPATH + "/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    //Text Box
    private static final String SCOPE_OF_WORK_TEXTBOX_PPM_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Scope Of Work')]/..//textarea";

    //Buttons
    private static final String QUOTE_TYPE_RADIO_BUTTONS_XPATH = "//div[@class = 'yesNoRadio']";
    private static final String QUOTE_TYPE_RADIO_BUTTON_ANSWER_XPATH = QUOTE_TYPE_RADIO_BUTTONS_XPATH + "/label[contains(text(), '%s')]";
    private static final String ACTION_BUTTONS_XPATH = "//div[contains(@class, 'form-group bottom-buttons')]";
    private static final String QUOTE_SAVE_BUTTON_XPATH = ACTION_BUTTONS_XPATH + "//button[contains(@class,'btn btn-primary') and contains(text(), 'Save')]";



    @FindBy(xpath = QUOTE_ASSET_MAIN_TYPE_DROPDOWN_XPATH)
    private WebElement quoteAssetMainTypeDropDown;

    @FindBy(xpath = QUOTE_ASSET_SUB_TYPE_DROPDOWN_XPATH)
    private WebElement quoteAssetSubTypeDropDown;

    @FindBy(xpath = PPM_QUOTE_LOCATION_DROPDOWN_XPATH)
    private WebElement locationDropDown;

    @FindBy(xpath = SCOPE_OF_WORK_TEXTBOX_PPM_XPATH)
    private WebElement scopeOfWorkTextBox;

    @FindBy(xpath = QUOTE_PRIORITY_DROPDOWN_XPATH)
    private WebElement quotePriorityDropDown;

    @FindBy(xpath = QUOTE_SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = QUOTE_FAULT_TYPE_DROPDOWN_XPATH)
    private WebElement faultTypeDropdown;


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(REQUEST_QUOTE_FORM_XPATH));
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


    public PPMRequestQuotePage(WebDriver driver) {
        super(driver);
    }

    public void selectRandomQuoteAssetMainType(){
        quoteAssetMainTypeDropDown.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
    }

    public void selectQuoteAssetMainType(String assetMainType){
        quoteAssetMainTypeDropDown.click();
        waitForAngularRequestsToFinish();
        this.selectExactVisibleDropdownOption(assetMainType);
    }

    public List<String> getQuoteAssetMainTypes(){
        quoteAssetMainTypeDropDown.click();
        waitForAngularRequestsToFinish();
        List<String> options = getVisibleDropdownOptions();
        quoteAssetMainTypeDropDown.click();
        waitForAngularRequestsToFinish();
        return options;
    }

    public UpdateSavedPage saveButton(){
        saveButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, UpdateSavedPage.class);
    }

    public void selectRandomQuoteAssetSubType(){
        quoteAssetSubTypeDropDown.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
    }

    public void selectRandomLocation(){
        locationDropDown.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
    }

    public void enterRandomScopeOfWork(String scope){
        waitForAnimation();
        scopeOfWorkTextBox.sendKeys(scope);
    }

    public void selectQuoteTypeAnswer(String quoteType){
        WebElement answer = waitForElement(By.xpath(String.format(QUOTE_TYPE_RADIO_BUTTON_ANSWER_XPATH, quoteType)), State.ELEMENT_IS_CLICKABLE);
        answer.click();
        waitForAngularRequestsToFinish();
    }

    public void selectQuotePriority(String priority){
        quotePriorityDropDown.click();
        waitForAngularRequestsToFinish();
        this.selectExactVisibleDropdownOption(priority);
    }

    public void selectRandomFaultType() {
        faultTypeDropdown.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
    }

}
