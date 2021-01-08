package mercury.pageobject.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

import static mercury.runtime.ThreadManager.getWebDriver;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;

public class AdminAddNewResourceProfilePage extends Base_Page<AdminAddNewResourceProfilePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADD_NEW_RESOURCE_PROFILE_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH = ADD_NEW_RESOURCE_PROFILE_PAGE_XPATH + "//div[@class='admin-action__sub-content']";
    private static final String ADD_NEW_RESOURCE_PROFILE_FOOTER_XPATH = ADD_NEW_RESOURCE_PROFILE_PAGE_XPATH + "//div[@class='footer-button-bar']";

    private static final String RESOURCE_TYPE_DROPDOWN_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//select[@ng-model='$ctrl.resourceProfile.resourceTypeId']";
    private static final String RESOURCE_TYPES_XPATH = RESOURCE_TYPE_DROPDOWN_XPATH + "//option";
    private static final String RESOURCE_PROFILE_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//input[@name='resourceProfileName']";
    private static final String RESOURCE_PROFILE_ALIAS_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//input[@name='resourceProfileAlias']";
    private static final String IS_LABEL_DISPLAYED_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//label[contains(text(), \"%s\")]";
    private static final String RESOURCE_PROFILE_QUESTION_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//label[contains(text(), \"%s\")]/../following-sibling::div//label[contains(text(), '%s')]";
    private static final String WORKING_HOURS_START_TIME_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//input[contains(@name, 'startAt')]";
    private static final String WORKING_HOURS_END_TIME_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//input[contains(@name, 'endAt')]";
    private static final String ADD_LABOUR_RATE_BUTTON_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//button[contains(text(), 'Add')]";
    private static final String LABOUR_RATE_STATE_DROPDOWN_XPATH = "(//select[@id='usState_'])[%d]";
    private static final String LABOUR_RATE_TYPE_DROPDOWN_XPATH = "(//select[@id='labourRateTypeName_'])[%d]";
    private static final String LABOUR_RATE_TEXT_FIELD_XPATH = "(//input[@name='hourlyRate_'])[%d]";
    private static final String ACTIVE_YES_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//label[@for='active_Yes']";
    private static final String ACTIVE_NO_XPATH = ADD_NEW_RESOURCE_PROFILE_CONTENT_XPATH + "//label[@for='active_No']";

    private static final String CANCEL_BUTTON_XPATH = ADD_NEW_RESOURCE_PROFILE_FOOTER_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String SAVE_PROGRESS_BUTTON_XPATH = ADD_NEW_RESOURCE_PROFILE_FOOTER_XPATH + "//span[contains(text(), 'Save Progress')]/..";
    private static final String CREATE_RESOURCE_PROFILE_BUTTON_XPATH = ADD_NEW_RESOURCE_PROFILE_FOOTER_XPATH + "//span[contains(text(), 'Create Resource Profile')]/..";


    @FindBy(xpath = RESOURCE_TYPE_DROPDOWN_XPATH)
    private WebElement resourceTypeDropdown;

    @FindBy(xpath = RESOURCE_TYPES_XPATH)
    private List<WebElement> resourceTypes;

    @FindBy(xpath = RESOURCE_PROFILE_XPATH)
    private WebElement resourceProfileNameBox;

    @FindBy(xpath = RESOURCE_PROFILE_ALIAS_XPATH)
    private WebElement resourceProfileAliasBox;

    @FindBy(xpath = ADD_LABOUR_RATE_BUTTON_XPATH)
    private WebElement addLabourRate;

    @FindBy(xpath = ACTIVE_YES_XPATH)
    private WebElement activeYes;

    @FindBy(xpath = ACTIVE_NO_XPATH)
    private WebElement activeNo;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    @FindBy(xpath = SAVE_PROGRESS_BUTTON_XPATH)
    private WebElement saveProgress;

    @FindBy(xpath = CREATE_RESOURCE_PROFILE_BUTTON_XPATH)
    private WebElement createResourceProfile;


    public AdminAddNewResourceProfilePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ADD_NEW_RESOURCE_PROFILE_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectResourceType(String resourceType) {
        selectOptionFromSelect(resourceTypeDropdown, resourceType);
    }

    public List<String> getListOfResourceTypes() {
        List<String> resourceTypeList = new ArrayList<>();
        for (WebElement resourceType : resourceTypes) {
            resourceTypeList.add(resourceType.getText());
        }
        return resourceTypeList;
    }

    public void enterResourceProfileName(String resourceProfileName) {
        resourceProfileNameBox.sendKeys(resourceProfileName);
    }

    public void enterResourceProfileAlias(String resourceProfileAlias) {
        resourceProfileAliasBox.sendKeys(resourceProfileAlias);
    }

    public Boolean isLabelDisplayed(String label) {
        return this.isElementPresent(By.xpath(String.format(IS_LABEL_DISPLAYED_XPATH, label)));
    }

    public void answerResourceProfileQuestions(String question, String answer) {
        WebElement resourceProfileQuestion = driver.findElement(By.xpath(String.format(RESOURCE_PROFILE_QUESTION_XPATH, question, answer)));
        resourceProfileQuestion.click();
        waitForAngularRequestsToFinish();
    }

    public void enterWorkingHoursStartTime(String day, String timePart) {
        WebElement startTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_START_TIME_XPATH, day)));

        // set element to 'text' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'text');", startTime);

        startTime.clear();
        startTime.sendKeys(timePart);

        // reset element to 'time' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'time');", startTime);

        // re-focus
        new Actions(driver).moveToElement(startTime.findElement(By.xpath("//ph-edit-working-hours"))).click().perform();
    }

    public void enterWorkingHoursEndTime(String day, String timePart) {
        WebElement endTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_END_TIME_XPATH, day)));

        // set element to 'text' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'text');", endTime);

        endTime.clear();
        endTime.sendKeys(timePart);

        // reset element to 'time' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'time');", endTime);

        // re-focus
        new Actions(driver).moveToElement(endTime.findElement(By.xpath("//ph-edit-working-hours"))).click().perform();
    }

    public void enterLabourRateWithState(String labourRateType, int number, String rate, String state) {
        addLabourRate.click();
        waitForAngularRequestsToFinish();
        WebElement labourRateStateDropdown = driver.findElement(By.xpath(String.format(LABOUR_RATE_STATE_DROPDOWN_XPATH, number)));
        selectOptionFromSelect(labourRateStateDropdown, state);
        WebElement labourRateTypeDropdown = driver.findElement(By.xpath(String.format(LABOUR_RATE_TYPE_DROPDOWN_XPATH, number)));
        selectOptionFromSelect(labourRateTypeDropdown, labourRateType);
        WebElement labourRateTextField = driver.findElement(By.xpath(String.format(LABOUR_RATE_TEXT_FIELD_XPATH, number)));
        labourRateTextField.sendKeys(rate);
    }

    public void enterLabourRateWithoutState(String labourRateType, int number, String rate) {
        addLabourRate.click();
        waitForAngularRequestsToFinish();
        WebElement labourRateTypeDropdown = driver.findElement(By.xpath(String.format(LABOUR_RATE_TYPE_DROPDOWN_XPATH, number)));
        selectOptionFromSelect(labourRateTypeDropdown, labourRateType);
        WebElement labourRateTextField = driver.findElement(By.xpath(String.format(LABOUR_RATE_TEXT_FIELD_XPATH, number)));
        labourRateTextField.sendKeys(rate);
    }

    public void clickActiveYes() {
        activeYes.click();
        waitForAngularRequestsToFinish();
    }

    public void clickActiveNo() {
        activeNo.click();
        waitForAngularRequestsToFinish();
    }

    public void cancelChanges() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isCancelButtonDisplayed() {
        try {
            return cancel.isDisplayed();
        } catch(Exception e) {
            return false;
        }
    }

    public void saveProgress() {
        saveProgress.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isSaveProgressButtonDisplayed() {
        try {
            return saveProgress.isDisplayed();
        } catch(Exception e) {
            return false;
        }
    }

    public void createResourceProfile() {
        createResourceProfile.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isCreateResourceProfileButtonDisplayed() {
        try {
            return createResourceProfile.isDisplayed();
        } catch(Exception e) {
            return false;
        }
    }
}
