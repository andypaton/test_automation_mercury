package mercury.pageobject.web.portal;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.MAX_PORTAL_TIMEOUT;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class PortalNavBar extends Base_Page<PortalNavBar> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Portal Main Navbar";

    // Main Menu
    private static final String DESKTOP_MAINMENU_XPATH = ".//ul[contains(@class,'main-menu desktop')]";
    private static final String MENU_XPATH = ".//ul[contains(@class,'main-menu desktop')]/li/a[contains(text(),'%s')]/..";
    private static final String EXPAND_XPATH = ".//ul[contains(@class,'main-menu desktop')]/li/a[contains(text(),'%s')]/b[contains(@class, 'icons__chevron-down')]";
    private static final String MERCURY_LOGO_XPATH = "//img[@alt = 'Mercury - Service Management System']";
    private static final String INVOICES_MENU_XPATH = ".//ul[contains(@class,'main-menu desktop')]/li/a[text()='%s ']";

    // Sub Menu
    private static final String SUB_MENU_DROPDOWN_XPATH = ".//li[contains(@class,'dropdown') and contains(@class,'open')]//ul[contains(@class,'dropdown-menu')]";
    private static final String SUB_MENU_XPATH = SUB_MENU_DROPDOWN_XPATH + "/li/a[contains(text(),'%s')]";
    private static final String SENIOR_MANAGER_RESOURCE_AVAILABILITY_SUB_MENU_XPATH = SUB_MENU_DROPDOWN_XPATH + "/li/a[contains(@href, 'SeniorManager') and contains(text(), '%s')]";

    // Jobs Menu
    private static final String JOBS_XPATH_SUFFIX = "/li/a[contains(text(),'Jobs')]";
    private static final String OPEN_JOBS_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Open Jobs')]";
    private static final String AWAITING_JOBS_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Jobs Awaiting Acceptance')]";
    private static final String SITE_JOBS_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Open Jobs By Site')]";
    private static final String ALL_JOBS_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'All Jobs')]";

    // Quotes Menu
    private static final String QUOTES_XPATH = "/li/a[contains(text(),'Quotes')]";
    private static final String AWAITING_QUOTES_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Jobs Awaiting Quote')]";
    private static final String QUERY_QUOTES_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Quotes with Query Pending')]";
    private static final String REVIEW_QUOTES_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Quotes Awaiting Review')]";

    // Invoices and Credits Menu
    private static final String INV_AND_CREDITS_XPATH = "/li/a[contains(text(),'Invoices and Credits')]";
    private static final String ALL_ORDERS_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'All Orders')]";
    private static final String AWAITING_INVOICE_ORDERS_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Orders Awaiting Invoice')]";
    private static final String NOT_INVOICED_ORDERS_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Orders Not Invoiced')]";
    private static final String SUBMITTED_INVOICE_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Submitted Invoices and Credits')]";
    private static final String INVOICE_TEMPLATE_XPATH_SUFFIX = "/following-sibling::ul/li/a[contains(text(),'Invoices Template')]";

    // Logout menu
    private static final String USER_MENU_XPATH = ".//ul[contains(@class,'user-menu')]";
    private static final String LOGOUT_XPATH = "/descendant::a[contains(text(),'Log Out')]";


    @FindBy(xpath = DESKTOP_MAINMENU_XPATH)
    private WebElement navBar_Menu;

    // Jobs Menu
    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + JOBS_XPATH_SUFFIX)
    private WebElement jobs_NavBar_Menu;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + JOBS_XPATH_SUFFIX + OPEN_JOBS_XPATH_SUFFIX)
    private WebElement jobs_Open_NavBar_Menu;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + JOBS_XPATH_SUFFIX + AWAITING_JOBS_XPATH_SUFFIX)
    private WebElement jobs_Awaiting_NavBar_Menu;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + JOBS_XPATH_SUFFIX + SITE_JOBS_XPATH_SUFFIX)
    private WebElement jobs_Site_NavBar_Menu;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + JOBS_XPATH_SUFFIX + ALL_JOBS_XPATH_SUFFIX)
    private WebElement jobs_All_NavBar_Menu;


    // Quotes Menu
    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + QUOTES_XPATH)
    private WebElement quotes_NavBar_Home;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + QUOTES_XPATH + AWAITING_QUOTES_XPATH_SUFFIX)
    private WebElement quotes_Awaiting_NavBar_Home;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + QUOTES_XPATH + QUERY_QUOTES_XPATH_SUFFIX)
    private WebElement quotes_Query_NavBar_Home;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + QUOTES_XPATH + REVIEW_QUOTES_XPATH_SUFFIX)
    private WebElement quotes_Review_NavBar_Home;


    // Invoice and Credits Menu
    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + INV_AND_CREDITS_XPATH)
    private WebElement inv_Credits_NavBar_Home;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + INV_AND_CREDITS_XPATH + ALL_ORDERS_XPATH_SUFFIX)
    private WebElement inv_Credits_Open_NavBar_Menu;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + INV_AND_CREDITS_XPATH + AWAITING_INVOICE_ORDERS_XPATH_SUFFIX)
    private WebElement inv_Credits_Awaiting_NavBar_Menu;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + INV_AND_CREDITS_XPATH + NOT_INVOICED_ORDERS_XPATH_SUFFIX)
    private WebElement inv_Credits_Not_Invoiced_NavBar_Menu;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + INV_AND_CREDITS_XPATH + SUBMITTED_INVOICE_XPATH_SUFFIX)
    private WebElement inv_Credits_Submitted_NavBar_Menu;

    @FindBy(xpath = DESKTOP_MAINMENU_XPATH + INV_AND_CREDITS_XPATH + INVOICE_TEMPLATE_XPATH_SUFFIX)
    private WebElement inv_Credits_Templates_NavBar_Menu;


    // Logout Menu
    @FindBy(xpath = USER_MENU_XPATH)
    private WebElement userMenu;

    @FindBy(xpath = USER_MENU_XPATH + LOGOUT_XPATH)
    private WebElement logOut;

    public PortalNavBar(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        System.out.print(PAGE_TITLE + ": Waiting for page to load ... ");
        try {
            findElement(By.xpath(DESKTOP_MAINMENU_XPATH), 5);
            System.out.println("done");
        } catch (WebDriverException ex) {
            System.err.println("load error!");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public PortalNavBar LogOut() {
        try {
            userMenu.click();
            logOut.click();
            return this;
        } catch (NoSuchElementException ex) {
            throw new AssertionError();
        }
    }

    // Portal nav bars are slow to respond - be prepared to wait a little longer than usual!
    private WebElement findElement(By by, int wait) {
        driver.manage().timeouts().implicitlyWait(wait, TimeUnit.SECONDS);
        WebElement we = driver.findElement(by);
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return we;
    }

    public boolean isMenuDisplayed(String monitor) {
        return findElement(By.xpath(String.format(MENU_XPATH, monitor)), MAX_PORTAL_TIMEOUT).isDisplayed();
    }

    public PortalNavBar clickTopLevelMenu(String topMenu) {
        WebElement we;
        if ("HOME".equalsIgnoreCase(topMenu) && !isElementPresent(By.xpath(String.format(MENU_XPATH, topMenu)))) {
            we = findElement(By.xpath(MERCURY_LOGO_XPATH), MAX_PORTAL_TIMEOUT);
        } else {
            waitForElement(By.xpath(String.format(MENU_XPATH, topMenu)), State.ELEMENT_IS_VISIBLE);
            we = findElement(By.xpath(String.format(MENU_XPATH, topMenu)), MAX_PORTAL_TIMEOUT);
        }
        boolean withDropdownList = isElementVisible(By.xpath(String.format(EXPAND_XPATH, topMenu)));
        we.click();
        if (withDropdownList) {
            waitForElement(By.xpath("//li[contains(@class,'dropdown') and contains(@class, 'open')]"), State.ELEMENT_IS_VISIBLE);
        } else {
            sleep(500);
        }
        return this;
    }

    public PortalNavBar clickInvoicesMenu(String invoiceMenu) {
        findElement(By.xpath(String.format(INVOICES_MENU_XPATH, invoiceMenu)), MAX_PORTAL_TIMEOUT).click();
        waitForAngularRequestsToFinish();
        return this;
    }

    public PortalNavBar expandTopLevelMenu(String topMenu) {
        WebElement we = findElement(By.xpath(String.format(MENU_XPATH, topMenu)), MAX_PORTAL_TIMEOUT);
        if ( !we.getAttribute("class").contains("open") ) {
            boolean withDropdownList = isElementVisible(By.xpath(String.format(EXPAND_XPATH, topMenu)));
            we.click();
            if (withDropdownList) {
                waitForElement(By.xpath("//li[contains(@class,'dropdown') and contains(@class, 'open')]"), State.ELEMENT_IS_VISIBLE);
            } else {
                sleep(500);
            }
        }
        return this;
    }

    public PortalNavBar clickSubLevelMenu(String subMenu) {
        findElement(By.xpath(String.format(SUB_MENU_XPATH, subMenu)), MAX_PORTAL_TIMEOUT).click();
        waitForAngularRequestsToFinish();
        return this;
    }

    public PortalNavBar clickSeniorManagerResourceAvailabilitySubMenu(String subMenu) {
        findElement(By.xpath(String.format(SENIOR_MANAGER_RESOURCE_AVAILABILITY_SUB_MENU_XPATH, subMenu)), MAX_PORTAL_TIMEOUT).click();
        waitForAngularRequestsToFinish();
        return this;
    }

    public Boolean isSubMenuDisplayed(String subMenu) {
        return findElement(By.xpath(String.format(SUB_MENU_XPATH, subMenu)), MAX_PORTAL_TIMEOUT).isDisplayed();
    }

    public List<String> getUserMenuOptions(){
        userMenu.click();
        return Arrays.asList(userMenu.getText().split("\n"));
    }
}
