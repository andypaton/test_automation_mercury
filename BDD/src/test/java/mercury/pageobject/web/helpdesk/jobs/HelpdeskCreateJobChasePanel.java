package mercury.pageobject.web.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class HelpdeskCreateJobChasePanel extends Base_Page<HelpdeskCreateJobChasePanel>{

    private static final Logger logger = LogManager.getLogger();

    // Workspace, panel and form
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String CHASE_PANEL_CONTENT_XPATH = ACTIVE_WORKSPACE_XPATH +  "//div[contains(@class,'job-action-panel') and contains(@class,'job-chase')]//div[@class='job-action-panel__content']";
    private static final String CREATE_CHASE_FORM_XPATH = CHASE_PANEL_CONTENT_XPATH + "//form[@id='chaseFormNew']";
    private static final String CREATE_CHASE_FORM_DETAILS_XPATH = CREATE_CHASE_FORM_XPATH + "//div[contains(@class,'job-chase__details')]";
    private static final String CREATE_CHASE_FORM_FOOTER_XPATH = CREATE_CHASE_FORM_DETAILS_XPATH + "//div[contains(@class,'footer-button-bar--wide')]";


    // Buttons
    private static final String CANCEL_CHASE_BUTTON_XPATH = CREATE_CHASE_FORM_FOOTER_XPATH + "//button[contains(text(),'Cancel')]";
    private static final String CREATE_CHASE_BUTTON_XPATH = CREATE_CHASE_FORM_FOOTER_XPATH + "//button[contains(text(),'Create Chase')]";


    // Core
    private static final String CHASE_DESCRIPTION_XPATH = CREATE_CHASE_FORM_DETAILS_XPATH + "//textarea[@id='chaseDescription']";
    private static final String CHASE_CALLER_NAME_XPATH = CREATE_CHASE_FORM_DETAILS_XPATH + "//input[@id='callerName']";
    private static final String CHASE_TYPE_DROPDOWN_ARROW_XPATH = CREATE_CHASE_FORM_DETAILS_XPATH + "//label[contains(text(), 'Chase Type')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String RESOURCE_DROPDOWN_ARROW_XPATH = CREATE_CHASE_FORM_DETAILS_XPATH + "//label[contains(text(), 'Resource')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String CALLER_NAME_XPATH = CREATE_CHASE_FORM_DETAILS_XPATH + "//input[@id='callerName']";
    private static final String TELEPHONE_NO_XPATH = CREATE_CHASE_FORM_DETAILS_XPATH + "//input[@id='callerTelephone']";
    private static final String TELEPHONE_NO_EXTENSION_XPATH = CREATE_CHASE_FORM_DETAILS_XPATH + "//input[@id='callerExtension']";


    // Errors
    private static final String ERROR_MESSAGE_XPATH = "/..//following-sibling::div[contains(@class, 'error')]";

    private static final String CHASE_DESCRIPTION_ERROR_TEXT_XPATH = CHASE_DESCRIPTION_XPATH + ERROR_MESSAGE_XPATH;
    private static final String CHASE_CALLER_NAME_ERROR_TEXT_XPATH = CALLER_NAME_XPATH + ERROR_MESSAGE_XPATH;
    private static final String CHASE_CALLER_TELEPHONE_ERROR_TEXT_XPATH = TELEPHONE_NO_XPATH + ERROR_MESSAGE_XPATH;
    private static final String CHASE_TYPE_ERROR_TEXT_XPATH = CHASE_TYPE_DROPDOWN_ARROW_XPATH + "//ancestor::span" + ERROR_MESSAGE_XPATH;


    public HelpdeskCreateJobChasePanel(WebDriver driver) {
        super(driver);
    }


    @FindBy(xpath = CHASE_DESCRIPTION_XPATH)
    private WebElement chaseDescription;

    @FindBy(xpath = CHASE_CALLER_NAME_XPATH)
    private WebElement chaseCallerName;

    @FindBy(xpath = TELEPHONE_NO_XPATH)
    private WebElement chaseTelephoneNumber;

    @FindBy(xpath = TELEPHONE_NO_EXTENSION_XPATH)
    private WebElement chaseTelephoneNumberExtension;

    @FindBy(xpath = CALLER_NAME_XPATH)
    private WebElement callerName;

    @FindBy(xpath = CHASE_TYPE_DROPDOWN_ARROW_XPATH)
    private WebElement chaseTypeDropdown;

    @FindBy(xpath = CHASE_DESCRIPTION_ERROR_TEXT_XPATH)
    private WebElement descriptionFieldError;

    @FindBy(xpath = CHASE_TYPE_ERROR_TEXT_XPATH)
    private WebElement chaseTypeFieldError;

    @FindBy(xpath = CHASE_CALLER_NAME_ERROR_TEXT_XPATH)
    private WebElement callerNameFieldError;

    @FindBy(xpath = CHASE_CALLER_TELEPHONE_ERROR_TEXT_XPATH)
    private WebElement telephoneNumberFieldError;

    @FindBy(xpath = CREATE_CHASE_BUTTON_XPATH)
    private WebElement createChaseButton;

    @FindBy(xpath = CANCEL_CHASE_BUTTON_XPATH)
    private WebElement cancelChaseButton;

    @FindBy(xpath = RESOURCE_DROPDOWN_ARROW_XPATH)
    private WebElement resourceDropdown;


    @Override
    protected void isLoaded() throws Error {
        try{
            assertTrue("Chase panel not loaded!", driver.findElement(By.xpath(CREATE_CHASE_FORM_DETAILS_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void setChaseCallerName(String notes) {
        chaseCallerName.clear();
        chaseCallerName.sendKeys(notes);
    }

    public String getChaseCallerName() {
        return chaseCallerName.getText();
    }

    public void setChaseDescription(String notes) {
        chaseDescription.sendKeys(notes);
    }

    public void setChaseRandomTelephoneNumber(String telephoneNumber) {
        chaseTelephoneNumber.clear();
        chaseTelephoneNumber.sendKeys(telephoneNumber);
    }

    public void setChaseRandomTelephoneNumberExtension(String telephoneExtension) {
        chaseTelephoneNumberExtension.sendKeys(telephoneExtension);
    }

    public void setChaseType(String chaseType) {
        chaseTypeDropdown.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(chaseType);
    }

    public void setResource() {
        resourceDropdown.click();
        waitForAngularRequestsToFinish();
        if (!getVisibleDropdownOptions().isEmpty()) {
            selectRandomVisibleDropdownOption();
        }
    }

    public void clickCreateChaseButton() {
        createChaseButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCancelChaseButton() {
        cancelChaseButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clearChaseTelephoneNumber() {
        chaseTelephoneNumber.clear();
        waitForAngularRequestsToFinish();
    }

    public void clearChaseExtension() {
        chaseTelephoneNumberExtension.clear();
        waitForAngularRequestsToFinish();
    }

    public void clickChaseDescription() {
        chaseDescription.click();
        waitForAngularRequestsToFinish();
    }

    public void clickChaseTypeDropdown() {
        chaseTypeDropdown.click();
        waitForAngularRequestsToFinish();
    }

    public void clearCallerName() {
        callerName.clear();
    }

    public boolean verifyMandatoryFieldMessageDisplayed(String field){
        switch (field.toUpperCase()) {
        case "DESCRIPTION":
            return descriptionFieldError.isDisplayed();
        case "CHASETYPE":
            return chaseTypeFieldError.isDisplayed();
        case "CALLERNAME":
            return callerNameFieldError.isDisplayed();
        case "TELEPHONENUMBER":
            return telephoneNumberFieldError.isDisplayed();
        default:
            return false;
        }
    }

    public boolean isCreateChaseEnabled() {
        return createChaseButton.isEnabled();
    }

}
