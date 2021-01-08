package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminAddNewUserPage extends Base_Page<AdminAddNewUserPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADD_NEW_USER_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String ADD_NEW_USER_CONTENT_XPATH = ADD_NEW_USER_PAGE_XPATH + "//div[@class='admin-action__sub-content']";
    private static final String ADD_NEW_USER_FOOTER_XPATH = ADD_NEW_USER_PAGE_XPATH + "//div[@class='footer-button-bar']";

    private static final String RESOUCE_DROPDOWN_XPATH = ADD_NEW_USER_CONTENT_XPATH + "//select[@ng-model='$ctrl.selectedResource']";
    private static final String USERNAME_XPATH = ADD_NEW_USER_CONTENT_XPATH + "//input[@id='username']";
    private static final String EMAIL_XPATH = ADD_NEW_USER_CONTENT_XPATH + "//input[@id='email']";
    private static final String PHONE_NUMBER_XPATH = ADD_NEW_USER_CONTENT_XPATH + "//input[@id='phone']";
    private static final String USER_PROFILE_DROPDOWN_XPATH = ADD_NEW_USER_CONTENT_XPATH + "//select[@id='userPermissions']";
    private static final String ACTIVE_YES_XPATH = ADD_NEW_USER_CONTENT_XPATH + "//label[@for='active_Yes']";
    private static final String ACTIVE_NO_XPATH = ADD_NEW_USER_CONTENT_XPATH + "//label[@for='active_No']";

    private static final String CREATE_USER_ERROR_TITLE_XPATH = "//div[@class='toast-title']";
    private static final String CREATE_USER_ERROR_TEXT_XPATH = "//div[@class='toast-message']";

    private static final String CANCEL_CHANGES_XPATH = ADD_NEW_USER_FOOTER_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String CREATE_USER_XPATH = ADD_NEW_USER_FOOTER_XPATH + "//span[contains(text(), 'Create user')]/..";


    @FindBy(xpath = USERNAME_XPATH)
    private WebElement userName;

    @FindBy(xpath = EMAIL_XPATH)
    private WebElement email;

    @FindBy(xpath = PHONE_NUMBER_XPATH)
    private WebElement phone;

    @FindBy(xpath = ACTIVE_YES_XPATH)
    private WebElement activeYes;

    @FindBy(xpath = ACTIVE_NO_XPATH)
    private WebElement activeNo;

    @FindBy(xpath = CREATE_USER_ERROR_TITLE_XPATH)
    private WebElement errorTitle;

    @FindBy(xpath = CREATE_USER_ERROR_TEXT_XPATH)
    private WebElement errorText;

    @FindBy(xpath = CANCEL_CHANGES_XPATH)
    private WebElement cancelChanges;

    @FindBy(xpath = CREATE_USER_XPATH)
    private WebElement createUser;


    public AdminAddNewUserPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ADD_NEW_USER_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectResource(String resource) {
        WebElement dropdown = driver.findElement(By.xpath(RESOUCE_DROPDOWN_XPATH));
        selectOptionFromSelect(dropdown, resource);
    }

    public void enterUsername(String username) {
        userName.sendKeys(username);
    }

    public void enterEmailAddress(String emailAddress) {
        email.sendKeys(emailAddress);
    }

    public void enterPhoneNumber(String phoneNumber) {
        phone.sendKeys(phoneNumber);
    }

    public void selectUserProfile(String userProfile) {
        WebElement dropdown = driver.findElement(By.xpath(USER_PROFILE_DROPDOWN_XPATH));
        selectOptionFromSelect(dropdown, userProfile);
    }

    public void makeUserActive() {
        activeYes.click();
        waitForAngularRequestsToFinish();
    }

    public void makeUserInactive() {
        activeNo.click();
        waitForAngularRequestsToFinish();
    }

    public String getCreateUserErrorTitle() {
        return errorTitle.getText();
    }

    public String getCreateUserErrorMessage() {
        return errorText.getText();
    }

    public void cancelChanges() {
        cancelChanges.click();
        waitForAngularRequestsToFinish();
    }

    public void createUser() {
        createUser.click();
        waitForAngularRequestsToFinish();
    }

}
