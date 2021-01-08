package mercury.pageobject.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;

public class PasswordResetRequestPage extends Base_Page<PasswordResetRequestPage>  {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Password Reset Request.";

    private static final String PASSWORD_RESET_REQUEST_XPATH = "//form[@action = '/Account/PasswordResetRequest']";

    private static final String USER_NAME_ID = "UserName";
    private static final String EMAIL_LINK_XPATH = PASSWORD_RESET_REQUEST_XPATH + "//input[@value = 'Email Link']";
    private static final String PAGE_TEXT_XPATH ="(//div[@class = 'container'])[2]";


    @FindBy(id = USER_NAME_ID)
    private WebElement userName;

    @FindBy(xpath = EMAIL_LINK_XPATH)
    private WebElement emailLink;


    public PasswordResetRequestPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForAngularRequestsToFinish();
            POHelper.isLoaded().isFluentElementIsVisible(By.id(USER_NAME_ID));
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

    public void enterUserName(String name) {
        userName.sendKeys(name);
    }

    public void emailLink() {
        emailLink.click();
        waitForAngularRequestsToFinish();
    }

    // this could be a separate page object - but seems overkill for a page with no actions on it
    public boolean isPasswordResetRequestConfirmationDisplayed() {
        WebElement we = driver.findElement(By.xpath(PAGE_TEXT_XPATH));
        String text = we.getText();
        return text.equals("Password Reset Request Confirmation\nA password reset email has been sent. Please check your email and click on the provided link. This link will be valid for 24 hours.");
    }

}
