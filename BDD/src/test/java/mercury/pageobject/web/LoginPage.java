package mercury.pageobject.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.databuilders.User;
import mercury.helpers.POHelper;
import mercury.pageobject.web.helpdesk.HelpdeskHomePage;
import mercury.pageobject.web.portal.PortalNavBar;

public class LoginPage extends Base_Page<LoginPage>  {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Login Page";

    private static final String LOGIN_SCREEN_XPATH = "//div[@class = 'loginscreen']";

    private static final String USER_NAME_ID = "UserName";
    private static final String PASSWORD_ID = "Password";
    private static final String LOGIN_BTN_ID = "btn--login";
    private static final String RESET_LINK_CLASSNAME = "reset-link";

    private static final String SCRIPT_DEPLOY_INFO_XPATH = "//script[contains(text(), 'deployInfo')]";
    private static final String INCORRECT_USERNAME_OR_PASSWORD_XPATH = LOGIN_SCREEN_XPATH + "//li[contains(text(), 'Incorrect username or password')]";


    @FindBy(id = USER_NAME_ID)
    private WebElement userName;

    @FindBy(id = PASSWORD_ID)
    private WebElement password;

    @FindBy(id = LOGIN_BTN_ID)
    private WebElement btnLogin;

    @FindBy(className = RESET_LINK_CLASSNAME)
    private WebElement resetLink;

    @FindBy(xpath = SCRIPT_DEPLOY_INFO_XPATH)
    private WebElement deployInfo;


    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForAngularRequestsToFinish();
            POHelper.isLoaded().isFluentElementIsVisible(By.id(USER_NAME_ID));
            logger.info(PAGE_TITLE + " isLoaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isLoaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public HelpdeskHomePage login(User user) {
        logger.info("Populating Form");
        logger.info("Username: " + user.getUsername());
        logger.info("Password: " + user.getPassword());
        this.userName.sendKeys(user.getUsername());
        this.password.sendKeys(user.getPassword());

        POHelper.clickJavascript(btnLogin);
        return PageFactory.initElements(driver, HelpdeskHomePage.class);
    }

    public PortalNavBar loginPortal(User user) {
        logger.info("Populating Form");
        this.userName.sendKeys(user.getUsername());
        this.password.sendKeys(user.getPassword());

        btnLogin.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PortalNavBar.class).get();
    }

    public String getDeployedVersion() {
        String script = deployInfo.getAttribute("textContent");
        return StringUtils.substringBetween(script, "BuildNumber\":\"", "\"");
    }

    public boolean isDisplayed() {
        waitForAngularRequestsToFinish();
        return isElementPresent(By.id(LOGIN_BTN_ID));
    }

    public boolean isIncorrectUsernameOrPassword() {
        waitForAngularRequestsToFinish();
        return isElementPresent(By.xpath(INCORRECT_USERNAME_OR_PASSWORD_XPATH));
    }

    public PasswordResetRequestPage selectForgottenPassword() {
        resetLink.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PasswordResetRequestPage.class);
    }

}
