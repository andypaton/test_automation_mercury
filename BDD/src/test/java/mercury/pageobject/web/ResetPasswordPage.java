package mercury.pageobject.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;

public class ResetPasswordPage extends Base_Page<ResetPasswordPage>  {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Reset Password Page";

    private static final String RESET_FORM_XPATH = "//form[@action = '/Account/ResetPassword']";

    private static final String USER_NAME_ID = "UserName";
    private static final String PASSWORD_ID = "Password";
    private static final String CONFIRM_PASSWORD_ID = "ConfirmPassword";
    private static final String RESET_BUTTON_XPATH = RESET_FORM_XPATH + "//input[@value = 'Reset']";


    @FindBy(id = USER_NAME_ID)
    private WebElement userName;

    @FindBy(id = PASSWORD_ID)
    private WebElement password;

    @FindBy(id = CONFIRM_PASSWORD_ID)
    private WebElement confirmPassword;

    @FindBy(xpath = RESET_BUTTON_XPATH)
    private WebElement reset;


    public ResetPasswordPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForAngularRequestsToFinish();
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(RESET_FORM_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");

        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public void enterUserName(String text) {
        userName.sendKeys(text);
    }

    public void enterPassword(String text) {
        password.sendKeys(text);
    }

    public void enterPasswordConfirmation(String text) {
        confirmPassword.sendKeys(text);
    }

    public void reset() {
        reset.click();
        waitForAngularRequestsToFinish();
    }

}
