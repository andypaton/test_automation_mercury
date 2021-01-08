package mercury.pageobject.web.servicechannel;

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

public class ServiceChannelLoginPage extends Base_Page<ServiceChannelLoginPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Login Page";

    private static final String ANNOUNCEMENT_MODAL_XPATH = "//div[@id='loginAnnouncementModal']";
    private static final String CLOSE_ANNOUNCEMENT_MODAL_XPATH = ANNOUNCEMENT_MODAL_XPATH + "//span[@class='close-modal']//i";

    private static final String USERNAME_XPATH = "//input[@id='UserName']";
    private static final String PASSWORD_XPATH = "//input[@id='Password']";
    private static final String LOGIN_XPATH = "//a[@id='LoginBtn']";


    @FindBy(xpath = CLOSE_ANNOUNCEMENT_MODAL_XPATH)
    private WebElement closeAnnouncementModal;

    @FindBy(xpath = USERNAME_XPATH)
    private WebElement userNameField;

    @FindBy(xpath = PASSWORD_XPATH)
    private WebElement passwordField;

    @FindBy(xpath = LOGIN_XPATH)
    private WebElement loginButton;


    public ServiceChannelLoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForLoadingToComplete();
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(USERNAME_XPATH));
            logger.info(PAGE_TITLE + " isLoaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isLoaded error");
            throw new AssertionError();
        }
    }

    public boolean isAnnouncementModalDisplayed() {
        return isElementPresent(By.xpath(ANNOUNCEMENT_MODAL_XPATH));
    }

    public void closeAnnouncementModal() {
        POHelper.scrollToElement(closeAnnouncementModal);
        POHelper.clickJavascriptWithoutAngular(closeAnnouncementModal);
        waitForLoadingToComplete();
    }

    public ServiceChannelHomePage login(String userName, String password) {
        userNameField.click();
        userNameField.clear();
        userNameField.sendKeys(userName);
        passwordField.click();
        passwordField.clear();
        passwordField.sendKeys(password);

        POHelper.clickJavascriptWithoutAngular(loginButton);
        waitForLoadingToComplete();
        return PageFactory.initElements(driver, ServiceChannelHomePage.class);
    }

}
