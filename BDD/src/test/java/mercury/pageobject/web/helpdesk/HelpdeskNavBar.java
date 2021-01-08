package mercury.pageobject.web.helpdesk;

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
import mercury.pageobject.web.admin.AdminHomePage;
import mercury.pageobject.web.portal.PortalNavBar;

public class HelpdeskNavBar  extends Base_Page<HelpdeskNavBar> {

    private static final Logger logger = LogManager.getLogger();


    // Page title
    private static final String PAGE_TITLE = "Helpdesk Main Navbar";

    // Nav Bar
    private static final String NAVBAR_CONTAINER_ID = "mainnav";

    // App Menu
    private static final String NAVBAR_XPATH = ".//div[contains(@class,'nav__menu-dropdown')]";
    private static final String NAVBAR_MENU_XPATH = ".//div[contains(@class,'nav__menu-dropdown')]/descendant::ul[contains(@role,'menu')]/parent::div";
    private static final String ADMIN_MENU_XPATH_SUFFIX = "/descendant::a[contains(text(),'Admin')]";
    private static final String REPORTING_MENU_XPATH_SUFFIX = "/descendant::a[contains(text(),'Reporting')]";
    private static final String PORTAL_MENU_XPATH_SUFFIX = "/descendant::a[contains(text(),'Portal')]";
    private static final String HELPDESK_MENU_XPATH_SUFFIX = "/descendant::a[contains(text(),'Helpdesk')]";

    // Logout menu
    private static final String USER_MENU_XPATH = "(//*[contains(@data-toggle, 'dropdown')]/*[contains(@class, 'user')])[last()]/..";
    private static final String LOGOUT_XPATH = "/descendant::a[contains(text(),'Log Out')]";

    // System Version
    private static final String VERSION_INFO_XPATH = ".//div[contains(@class,'nav__versionNo')]";


    @FindBy(id=NAVBAR_CONTAINER_ID)
    private WebElement navBarContainer;

    @FindBy(xpath = NAVBAR_XPATH)
    private WebElement navBar;

    @FindBy(xpath = NAVBAR_MENU_XPATH)
    private WebElement navBarMenu;

    @FindBy(xpath = NAVBAR_MENU_XPATH + ADMIN_MENU_XPATH_SUFFIX)
    private WebElement adminApp;

    @FindBy(xpath = NAVBAR_MENU_XPATH + REPORTING_MENU_XPATH_SUFFIX)
    private WebElement reportingApp;

    @FindBy(xpath = NAVBAR_MENU_XPATH + PORTAL_MENU_XPATH_SUFFIX)
    private WebElement portalApp;

    @FindBy(xpath = NAVBAR_MENU_XPATH + HELPDESK_MENU_XPATH_SUFFIX)
    private WebElement helpdeskApp;


    @FindBy(xpath = USER_MENU_XPATH)
    private WebElement userMenu;

    @FindBy(xpath = USER_MENU_XPATH + LOGOUT_XPATH)
    private WebElement logOut;

    @FindBy(xpath = VERSION_INFO_XPATH)
    private WebElement versionInfo;


    public HelpdeskNavBar(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            // TODO: implement waitForPageToLoad
            //Assert.assertTrue("Page is not displayed", driver.findElement(By.id(NAVBAR_CONTAINER_ID)).isDisplayed());
            POHelper.isLoaded()
            .isFluentElementIsVisible(By.id(NAVBAR_CONTAINER_ID));
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }


    public AdminHomePage OpenAdminApp() {
        try {
            navBarMenu.click();
            waitForAngularRequestsToFinish();
            adminApp.click();
            waitForAngularRequestsToFinish();
            return PageFactory.initElements(driver, AdminHomePage.class).get();
        } catch (NoSuchElementException ex) {
            // TODO: change this error
            throw new AssertionError();
        }
    }

    public HelpdeskNavBar OpenHelpdesk() {
        try {
            navBarMenu.click();
            waitForAngularRequestsToFinish();
            helpdeskApp.click();
            waitForAngularRequestsToFinish();
            return this;
        } catch (NoSuchElementException ex) {
            throw new AssertionError();
        }
    }

    public PortalNavBar OpenPortal() {
        try {
            navBarMenu.click();
            waitForAngularRequestsToFinish();
            portalApp.click();
            waitForAngularRequestsToFinish();
            return PageFactory.initElements(driver, PortalNavBar.class).get();
        } catch (NoSuchElementException ex) {
            throw new AssertionError();
        }
    }

    public HelpdeskNavBar OpenReporting() {
        try {
            navBarMenu.click();
            waitForAngularRequestsToFinish();
            reportingApp.click();
            waitForAngularRequestsToFinish();
            return this;
        } catch (NoSuchElementException ex) {
            throw new AssertionError();
        }
    }

    public HelpdeskNavBar LogOut() {
        try {
            userMenu.click();
            waitForAngularRequestsToFinish();
            logOut.click();
            waitForAngularRequestsToFinish();
            return this;
        } catch (NoSuchElementException ex) {
            throw new AssertionError();
        }
    }

    public String getUserName() {
        return userMenu.getText();
    }

    public String getNavBarText() {
        return navBar.getText();
    }

}

