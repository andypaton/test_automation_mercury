package mercury.pageobject.web.portal.ppm;

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
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.portal.jobs.UpdateSavedPage;

public class PPMRemedialJobPage extends Base_Page<PPMRemedialJobPage> {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "PPM Details";

    // Main content
    private static final String UPDATE_JOB_FORM_XPATH = "//form[@name='PPMShutDownForm']";
    private static final String REMEDIAL_JOB_FORM_XPATH = "//div[@ng-if='$ctrl.showRemedialJobQuestions']";

    private static final String ASSIGN_TO_ME_BUTTON_XPATH = "//div[@class = 'yesNoRadio']//label[contains (text() , 'Yes')]";
    private static final String NOT_ASSIGN_TO_ME_BUTTON_XPATH = "//div[@class = 'yesNoRadio']//label[contains (text() , 'No')]";
    private static final String ASSET_MAIN_TYPE_DROPDOWN_XPATH = "//*[@id='assetTypeId']" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ASSET_SUB_TYPE_DROPDOWN_XPATH = "//*[@id='assetSubTypeId']" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String PPM_REMEDIAL_LOCATION_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Location')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String PPM_REMEDIAL_FAULT_TYPE_DROPDOWN_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Fault Type')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String DESCRIPTION_TEXTBOX_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(), 'Provide a description of the remedial works required')]/..//textarea";
    private static final String ACTION_BUTTONS_XPATH = "//div[contains(@class, 'form-group bottom-buttons')]";
    private static final String PPM_SAVE_BUTTON_XPATH = ACTION_BUTTONS_XPATH + "//button[contains(@class,'btn btn-primary') and contains(text(), 'Save')]";


    @FindBy(xpath = ASSIGN_TO_ME_BUTTON_XPATH)
    private WebElement assignToMeButton;

    @FindBy(xpath = NOT_ASSIGN_TO_ME_BUTTON_XPATH)
    private WebElement noAssignToMeButton;

    @FindBy(xpath = ASSET_MAIN_TYPE_DROPDOWN_XPATH)
    private WebElement assetMainTypeDropDown;

    @FindBy(xpath = ASSET_SUB_TYPE_DROPDOWN_XPATH)
    private WebElement assetSubTypeDropDown;

    @FindBy(xpath = PPM_REMEDIAL_LOCATION_DROPDOWN_XPATH)
    private WebElement locationDropDown;

    @FindBy(xpath = PPM_REMEDIAL_FAULT_TYPE_DROPDOWN_XPATH)
    private WebElement faultTypeDropDown;

    @FindBy(xpath = DESCRIPTION_TEXTBOX_XPATH)
    private WebElement descriptionTextBox;

    @FindBy(xpath = PPM_SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    public PPMRemedialJobPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(REMEDIAL_JOB_FORM_XPATH));
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

    public UpdateSavedPage saveButton(){
        saveButton.click();
        return PageFactory.initElements(driver, UpdateSavedPage.class);
    }

    public void selectAssignToMe(){
        assignToMeButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectNotAssignedToMe(){
        noAssignToMeButton.click();
        waitForAngularRequestsToFinish();
    }

    public String selectRandomAssetMainType(){
        assetMainTypeDropDown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String selectRandomAssetSubType(){
        assetSubTypeDropDown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public List<String> getAssetMainTypes(){
        POHelper.scrollToElement(assetMainTypeDropDown);
        assetMainTypeDropDown.click();
        waitForAngularRequestsToFinish();
        List<String> options = getVisibleDropdownOptions();
        assetMainTypeDropDown.click();  // collapse dropdown
        waitForAngularRequestsToFinish();
        return options;
    }

    public List<String> getAssetSubTypes(){
        assetSubTypeDropDown.click();
        waitForAngularRequestsToFinish();
        List<String> options = getVisibleDropdownOptions();
        assetSubTypeDropDown.click();  // collapse dropdown
        waitForAngularRequestsToFinish();
        return options;
    }

    public void selectAssetMainType(String option) {
        assetMainTypeDropDown.click();
        selectVisibleDropdownOption(option);
    }

    public void selectRandomLocation(){
        locationDropDown.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
    }

    public void enterRandomDescription(String description){
        descriptionTextBox.sendKeys(description);
    }

    public void selectRandomFaultType(){
        faultTypeDropDown.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
    }

}
