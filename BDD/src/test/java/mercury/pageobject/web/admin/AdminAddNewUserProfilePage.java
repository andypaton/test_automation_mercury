package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminAddNewUserProfilePage extends Base_Page<AdminAddNewUserProfilePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADD_NEW_USER_PROFILE_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String ADD_NEW_USER_PROFILE_CONTENT_XPATH = ADD_NEW_USER_PROFILE_PAGE_XPATH + "//div[@class='admin-action__sub-content']";
    private static final String ADD_NEW_USER_PROFILE_FOOTER_XPATH = ADD_NEW_USER_PROFILE_PAGE_XPATH + "//div[@class='footer-button-bar']";

    private static final String USER_PROFILE_NAME_XPATH = ADD_NEW_USER_PROFILE_CONTENT_XPATH + "//input[@id='name']";
    private static final String DEFAULT_PORTAL_DROPDOWN_XPATH = ADD_NEW_USER_PROFILE_CONTENT_XPATH + "//select[@name='defaultPortal']";
    private static final String PERMISSIONS_XPATH = ADD_NEW_USER_PROFILE_CONTENT_XPATH + "//div[@class='permissionsTableData']//label[contains(text(), '%s')]";
    private static final String PERMISSIONS_NAME_XPATH = ADD_NEW_USER_PROFILE_CONTENT_XPATH + "//h2[contains(text(), 'Permissions')]/../../following-sibling::div//div[@class='permissionsTableData']//label[contains(text(), '%s')]";
    private static final String MONITOR_TILES_XPATH = ADD_NEW_USER_PROFILE_CONTENT_XPATH + "//h2[contains(text(), 'Monitor Tiles')]/../../following-sibling::div//div[@class='permissionsTableData']//label[contains(text(), '%s')]";
    private static final String ACTIVE_YES_XPATH = ADD_NEW_USER_PROFILE_CONTENT_XPATH + "//label[@for='active_Yes']";
    private static final String ACTIVE_NO_XPATH = ADD_NEW_USER_PROFILE_CONTENT_XPATH + "//label[@for='active_No']";

    private static final String CANCEL_XPATH = ADD_NEW_USER_PROFILE_FOOTER_XPATH + "//button[@ng-click='$ctrl.cancel()']";
    private static final String CREATE_PROFILE_XPATH = ADD_NEW_USER_PROFILE_FOOTER_XPATH + "//button[@ng-click='$ctrl.save()']";


    @FindBy(xpath = USER_PROFILE_NAME_XPATH)
    private WebElement userProfileName;

    @FindBy(xpath = ACTIVE_YES_XPATH)
    private WebElement active;

    @FindBy(xpath = ACTIVE_NO_XPATH)
    private WebElement inactive;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancel;

    @FindBy(xpath = CREATE_PROFILE_XPATH)
    private WebElement createProfile;


    public AdminAddNewUserProfilePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ADD_NEW_USER_PROFILE_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterUserProfileName(String userProfile) {
        userProfileName.sendKeys(userProfile);
    }

    public void selectDefaultPortal(String option) {
        WebElement dropdown = driver.findElement(By.xpath(DEFAULT_PORTAL_DROPDOWN_XPATH));
        selectOptionFromSelect(dropdown, option);
    }

    public void selectPermission(String permission) {
        WebElement permissionSelection = driver.findElement(By.xpath(String.format(PERMISSIONS_XPATH, permission)));
        POHelper.scrollToElement(permissionSelection);
        permissionSelection.click();
        waitForAngularRequestsToFinish();
    }
    
    public boolean permissionIsChecked(String Permission_name) {
        Boolean isChecked = driver.findElement(By.xpath(String.format(PERMISSIONS_NAME_XPATH, Permission_name))).isSelected();
        return isChecked;
    }

    public void selectMonitorTile(String monitorTile) {
        WebElement montiorSelection = driver.findElement(By.xpath(String.format(MONITOR_TILES_XPATH, monitorTile)));
        POHelper.scrollToElement(montiorSelection);
        montiorSelection.click();
        waitForAngularRequestsToFinish();
    }

    public void makeProfileActive() {
        POHelper.scrollToElement(active);
        active.click();
        waitForAngularRequestsToFinish();
    }

    public void makeProfileInactive() {
        POHelper.scrollToElement(inactive);
        inactive.click();
        waitForAngularRequestsToFinish();
    }

    public AdminResourcesAndUsersPage cancelChanges() {
        cancel.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminResourcesAndUsersPage.class).get();
    }

    public AdminResourcesAndUsersPage createProfile() {
        POHelper.scrollToElement(createProfile);
        POHelper.clickJavascript(createProfile);
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminResourcesAndUsersPage.class).get();
    }
}
