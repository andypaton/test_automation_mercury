package mercury.pageobject.web.storeportal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class StorePortalLoginPage extends Base_Page<StorePortalLoginPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "STORE PORTAL";

    private static final String USERNAME_CSS = "input#mat-input-0";
    private static final String PASSWORD_CSS = "input#mat-input-1";
    private static final String LOGIN_CSS = "button[name='login']";

    @FindBy(css = USERNAME_CSS)
    private WebElement userNameField;

    @FindBy(css = PASSWORD_CSS)
    private WebElement passwordField;

    @FindBy(css = LOGIN_CSS)
    private WebElement loginButton;


    public StorePortalLoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.waitForStorePortal();
            logger.info(PAGE_TITLE + ": Page loaded");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + ": Page failed to load");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public StorePortalHomePage login(String userName, String password) {
        userNameField.click();
        userNameField.clear();
        userNameField.sendKeys(userName);
        passwordField.click();
        passwordField.clear();
        passwordField.sendKeys(password);

        POHelper.clickJavascript(loginButton);
        POHelper.waitForStorePortal();
        return PageFactory.initElements(driver, StorePortalHomePage.class);
    }

}
