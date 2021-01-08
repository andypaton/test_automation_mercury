package mercury.pageobject.web.admin;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminAddNewResourcePage extends Base_Page<AdminAddNewResourcePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADD_NEW_RESOURCE_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String ADD_NEW_RESOURCE_CONTENT_XPATH = ADD_NEW_RESOURCE_PAGE_XPATH + "//div[@class='admin-action__content']";

    private static final String RESOURCE_PROFILE_DROPDOWN_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//select[@id='ResourceProfile']";
    private static final String RESOURCE_NAME_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//input[@id='Name']";
    private static final String PAYROLL_CODE_DROPDOWN_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//span[@aria-owns='PayrollTCode_listbox']//span";
    private static final String PAYROLL_CODE_OPTION_XPATH = "//li[contains(text(), '%s')]";
    private static final String PAYROLL_CODE_SELECTED_OPTION_XPATH = PAYROLL_CODE_DROPDOWN_XPATH + "//span";
    private static final String SUN_SUPPLIER_TCODE_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//select[@id='SupplierTCode']";
    private static final String SUN_SUPPLIER_TCODE_OPTION_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//option[@value='string:%s']";
    private static final String HOME_STORE_LABEL_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[@for='HomeStoreId']";
    private static final String HOME_STORE_DROPDOWN_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//select[@id='HomeStoreId']";
    private static final String FIRST_EMAIL_TEXTBOX_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//input[@type='email']";
    private static final String SECOND_EMAIL_TEXTBOX_XPATH = "(//input[@type='email'])[2]";
    private static final String ADD_EMAIL_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//button[@ng-click='$ctrl.addEmail()']";
    private static final String EMAIL_UP_CHEVRON_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[contains(text(), 'Email')]/following-sibling::div//i[contains(@class, 'fa-chevron-up')]";
    private static final String ADD_PHONE_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//button[@ng-click='$ctrl.addPhone()']";
    private static final String FIRST_PHONE_TEXTBOX_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//input[@name='PhoneNumber_0']";
    private static final String FIRST_PHONE_NOTES_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//textarea[@ng-model='phoneNumber.notes']";
    private static final String FIRST_PHONE_TYPE_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//select[@name='PhoneNumberType_0']";
    private static final String SECOND_PHONE_TEXTBOX_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//input[@name='PhoneNumber_1']";
    private static final String SECOND_PHONE_NOTES_XPATH = "(//textarea[@ng-model='phoneNumber.notes'])[2]";
    private static final String SECOND_PHONE_TYPE_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//select[@name='PhoneNumberType_1']";
    private static final String PHONE_UP_CHEVRON_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[contains(text(), 'Phone number')]/following-sibling::div//i[contains(@class, 'fa-chevron-up')]";
    private static final String RESOURCE_QUESTION_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[contains(text(), '%s')]/following-sibling::div//label[contains(text(), '%s')]";
    private static final String IS_LABEL_DISPLAYED_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[contains(text(), '%s')]";
    private static final String IS_PAYROLL_DISPLAYED_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//div[@aria-hidden='false']//label[contains(text(), 'Payroll T Code')]";
    private static final String IS_HEADER_DISPLAYED_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//h2[contains(text(), '%s')]";
    private static final String USERNAME_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//input[@ng-model='$ctrl.resource.username']";
    private static final String USER_PERMISSION_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//select[@name='UserPermissions']";
    private static final String SITE_CONFIGURATION_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[contains(text(), 'Site configuration')]//following-sibling::div//input";
    private static final String EDIT_SITE_CONFIGURATION_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//button[contains(text(), 'Edit')]";
    private static final String PERMANENT_SITE_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[contains(text(), 'Permanent sites')]//following-sibling::div//input";
    private static final String SITE_REPLACE_USER_BAR_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[@ng-if='$ctrl.requiresOverride(selectedSite)']";
    private static final String RESOURCE_REQUIRES_SITE_VISIT_NOTIFICATION_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//label[@for='requiresNotification0']";

    private static final String WORKING_HOURS_SHIFT_SUMMARY_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//div[contains(@class, 'opening-hours__summary')]";
    private static final String WORKING_HOURS_SHIFT_SUMMARY_OPTIONS_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//div[contains(@class, 'opening-hours__summary')]//div";
    private static final String WORKING_HOURS_START_AT_TIME_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//input[contains(@name, 'startAt')]";
    private static final String WORKING_HOURS_END_AT_TIME_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//input[contains(@name, 'endAt')]";
    private static final String WORKING_HOURS_OVERNIGHT_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//span[@ng-if='day.overnight!==null']";
    private static final String WORKING_HOURS_EMPTY_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//td[text()='%s']/following-sibling::td[%d]/input[contains(@class, 'ng-empty')]";

    private static final String CANCEL_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String COMPLETE_RESOURCE_SETUP_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//span[contains(text(), 'Complete Resource Setup')]/..";
    private static final String SAVE_PROGRESS_XPATH = ADD_NEW_RESOURCE_CONTENT_XPATH + "//button[contains(text(), 'Save Progress')]";


    @FindBy(xpath = RESOURCE_NAME_XPATH)
    private WebElement resourceName;

    @FindBy(xpath = PAYROLL_CODE_DROPDOWN_XPATH)
    private WebElement payrollCode;

    @FindBy(xpath = PAYROLL_CODE_SELECTED_OPTION_XPATH)
    private WebElement payrollCodeSelectedOption;

    @FindBy(xpath = SUN_SUPPLIER_TCODE_XPATH)
    private WebElement sunSupplierTcode;

    @FindBy(xpath = HOME_STORE_LABEL_XPATH)
    private WebElement homeStoreLabel;

    @FindBy(xpath = FIRST_EMAIL_TEXTBOX_XPATH)
    private WebElement firstEmailTextBox;

    @FindBy(xpath = SECOND_EMAIL_TEXTBOX_XPATH)
    private WebElement secondEmailTextBox;

    @FindBy(xpath = ADD_EMAIL_XPATH)
    private WebElement addEmail;

    @FindBy(xpath = EMAIL_UP_CHEVRON_XPATH)
    private WebElement emailUpChevron;

    @FindBy(xpath = ADD_PHONE_XPATH)
    private WebElement addPhone;

    @FindBy(xpath = FIRST_PHONE_TEXTBOX_XPATH)
    private WebElement firstPhoneNumber;

    @FindBy(xpath = FIRST_PHONE_NOTES_XPATH)
    private WebElement firstPhoneNotes;

    @FindBy(xpath = FIRST_PHONE_TYPE_XPATH)
    private WebElement firstPhoneType;

    @FindBy(xpath = SECOND_PHONE_TEXTBOX_XPATH)
    private WebElement secondPhoneNumber;

    @FindBy(xpath = SECOND_PHONE_NOTES_XPATH)
    private WebElement secondPhoneNotes;

    @FindBy(xpath = SECOND_PHONE_TYPE_XPATH)
    private WebElement secondPhoneType;

    @FindBy(xpath = PHONE_UP_CHEVRON_XPATH)
    private WebElement phoneUpChevron;

    @FindBy(xpath = USERNAME_XPATH)
    private WebElement username;

    @FindBy(xpath = USER_PERMISSION_XPATH)
    private WebElement userPermissions;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancel;

    @FindBy(xpath = COMPLETE_RESOURCE_SETUP_XPATH)
    private WebElement completeResourceSetup;

    @FindBy(xpath = SAVE_PROGRESS_XPATH)
    private WebElement saveProgress;

    @FindBy(xpath = SITE_CONFIGURATION_XPATH)
    private WebElement siteConfig;

    @FindBy(xpath = EDIT_SITE_CONFIGURATION_XPATH)
    private WebElement editSiteConfig;

    @FindBy(xpath = WORKING_HOURS_SHIFT_SUMMARY_OPTIONS_XPATH)
    private List<WebElement> workingHoursShiftSummaryOptions;

    @FindBy(xpath = WORKING_HOURS_SHIFT_SUMMARY_XPATH)
    private WebElement shiftSummary;

    @FindBy(xpath = PERMANENT_SITE_XPATH)
    private WebElement permanentSite;

    @FindBy(xpath = SITE_REPLACE_USER_BAR_XPATH)
    private WebElement replaceBar;


    public AdminAddNewResourcePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ADD_NEW_RESOURCE_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectResourceProfile(String resourceProfile) {
        WebElement dropdown = driver.findElement(By.xpath(RESOURCE_PROFILE_DROPDOWN_XPATH));
        selectOptionFromSelect(dropdown, resourceProfile);
    }

    public void enterResourceName(String name) {
        resourceName.sendKeys(name);
    }

    public void selectPayrollCodeDropdownOption(String code) {
        payrollCode.click();
        waitForAngularRequestsToFinish();
        WebElement option = driver.findElement(By.xpath(String.format(PAYROLL_CODE_OPTION_XPATH, code)));
        option.click();
        waitForAngularRequestsToFinish();
    }

    public void selectSunSupplierTcodeDropdownOption(String code) {
        sunSupplierTcode.click();
        waitForAngularRequestsToFinish();
        WebElement option = driver.findElement(By.xpath(String.format(SUN_SUPPLIER_TCODE_OPTION_XPATH, code)));
        option.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isHomeStoreDisplayed() {
        return this.isElementPresent(By.xpath(HOME_STORE_LABEL_XPATH));
    }

    public void selectHomeStore(String homeStore) {
        WebElement dropdown = driver.findElement(By.xpath(HOME_STORE_DROPDOWN_XPATH));
        selectOptionFromSelect(dropdown, homeStore);
    }

    public Boolean isEmailTextBoxDisplayed() {
        return this.isElementPresent(By.xpath(FIRST_EMAIL_TEXTBOX_XPATH));
    }

    public void addEmailAddress() {
        addEmail.click();
        waitForAngularRequestsToFinish();
    }

    public void enterFirstEmail(String email) {
        firstEmailTextBox.sendKeys(email);
    }

    public void enterSecondEmail(String email) {
        secondEmailTextBox.sendKeys(email);
    }

    public void makeSecondEmailPrimaryEmail() {
        emailUpChevron.click();
        waitForAngularRequestsToFinish();
    }

    public void addPhoneNumber() {
        addPhone.click();
        waitForAngularRequestsToFinish();
    }

    public void addFirstPhoneNumber(String phoneNumber, String notes, String phoneType) {
        firstPhoneNumber.sendKeys(phoneNumber);
        firstPhoneNotes.sendKeys(notes);
        Select select = new Select(firstPhoneType);
        select.selectByVisibleText(phoneType);
        waitForAngularRequestsToFinish();
    }

    public void addSecondPhoneNumber(String phoneNumber, String notes, String phoneType) {
        secondPhoneNumber.sendKeys(phoneNumber);
        secondPhoneNotes.sendKeys(notes);
        Select select = new Select(secondPhoneType);
        select.selectByVisibleText(phoneType);
        waitForAngularRequestsToFinish();
    }

    public void makeSecondPhoneNumberPrimaryPhone() {
        phoneUpChevron.click();
        waitForAngularRequestsToFinish();
    }

    public void answerResourceQuestions(String question, String answer) {
        WebElement resourceQuestion = driver.findElement(By.xpath(String.format(RESOURCE_QUESTION_XPATH, question, answer)));
        resourceQuestion.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isWorkingHoursEmpty(String day, int startEnd) {
        return this.isElementPresent(By.xpath(String.format(WORKING_HOURS_EMPTY_XPATH, day, startEnd)));
    }

    public Boolean isPayrollDisplayed() {
        return this.isElementPresent(By.xpath(IS_PAYROLL_DISPLAYED_XPATH));
    }

    public Boolean isSupplierTCodeDisplayed() {
        return sunSupplierTcode.isDisplayed();
    }

    public Boolean isLabelDisplayed(String label) {
        return this.isElementPresent(By.xpath(String.format(IS_LABEL_DISPLAYED_XPATH, label)));
    }

    public Boolean isHeaderDisplayed(String header) {
        return this.isElementPresent(By.xpath(String.format(IS_HEADER_DISPLAYED_XPATH, header)));
    }

    public void enterUsername(String userName) {
        username.sendKeys(userName);
    }

    public void selectUserPermission(String permission) {
        selectOptionFromSelect(userPermissions, permission);
    }

    public void selectRandomUserPermission() {
        selectRandomOptionFromSelect(userPermissions);
    }

    public void cancelChanges() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void saveProgressOfApplication() {
        saveProgress.click();
        waitForAngularRequestsToFinish();
    }

    public void completeResourceSetup() {
        completeResourceSetup.click();
        waitForAngularRequestsToFinish();
    }

    public void selectSiteForConfiguration(String siteName) {
        POHelper.sendKeys(siteConfig, siteName);
        siteConfig.sendKeys(Keys.ARROW_DOWN);
        siteConfig.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public AdminEditSiteConfigurationModal editSiteForConfiguration() {
        editSiteConfig.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditSiteConfigurationModal.class).get();
    }

    public void selectPermanentSite(String siteName) {
        POHelper.sendKeys(permanentSite, siteName);
        permanentSite.sendKeys(Keys.ARROW_DOWN);
        permanentSite.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public List<String> getWorkingHoursShiftSummary() {
        List<String> dailyShifts = new ArrayList<>();
        for (WebElement dailyShift : workingHoursShiftSummaryOptions) {
            dailyShifts.add(dailyShift.getText());
        }
        return dailyShifts;
    }

    public Boolean isShiftSummaryDisplayed() {
        return this.isElementPresent(By.xpath(WORKING_HOURS_SHIFT_SUMMARY_XPATH));
    }

    public void deleteWorkingHoursStartTime(String day) {
        WebElement startTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_START_AT_TIME_XPATH, day)));
        startTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
        startTime.sendKeys(Keys.ARROW_RIGHT);
        waitForAngularRequestsToFinish();
        startTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
        //To remove the AM that appears when run in Jenkins
        startTime.sendKeys(Keys.ARROW_RIGHT);
        waitForAngularRequestsToFinish();
        startTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
    }

    public void deleteWorkingHoursEndTime(String day) {
        WebElement endTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_END_AT_TIME_XPATH, day)));
        endTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
        endTime.sendKeys(Keys.ARROW_RIGHT);
        waitForAngularRequestsToFinish();
        endTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
        //To remove the PM that appears when run in Jenkins
        endTime.sendKeys(Keys.ARROW_RIGHT);
        waitForAngularRequestsToFinish();
        endTime.sendKeys(Keys.BACK_SPACE);
        waitForAngularRequestsToFinish();
    }

    public void enterWorkingHoursStartTime(String day, String timePart) {
        WebElement startTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_START_AT_TIME_XPATH, day)));

        // set element to 'text' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'text');", startTime);

        startTime.clear();
        startTime.sendKeys(timePart);

        // reset element to 'time' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'time');", startTime);

        // re-focus
        new Actions(driver).moveToElement(startTime.findElement(By.xpath("//ph-edit-working-hours"))).click().perform();
    }

    public void enterWorkingHoursEndTime(String day, String timePart) throws InterruptedException {
        WebElement endTime = driver.findElement(By.xpath(String.format(WORKING_HOURS_END_AT_TIME_XPATH, day)));

        // set element to 'text' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'text');", endTime);

        endTime.clear();
        endTime.sendKeys(timePart);

        // reset element to 'time' type
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].setAttribute('type', 'time');", endTime);

        // re-focus
        new Actions(driver).moveToElement(endTime.findElement(By.xpath("//ph-edit-working-hours"))).click().perform();

        //Wait for the Overnight to be populated
        waitForElement(By.xpath(String.format(WORKING_HOURS_OVERNIGHT_XPATH, day)), ELEMENT_IS_VISIBLE);
    }

    public void clickShiftSummary() {
        shiftSummary.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isReplaceBarDisplayed() {
        return this.isElementPresent(By.xpath(SITE_REPLACE_USER_BAR_XPATH));
    }

    public void clickReplaceBar() {
        replaceBar.click();
        waitForAngularRequestsToFinish();
    }

    public void clickResourceRequiresSiteVisitNotificationCheckbox() {
        POHelper.clickJavascript(By.xpath(RESOURCE_REQUIRES_SITE_VISIT_NOTIFICATION_XPATH));
        waitForAngularRequestsToFinish();
    }
}

