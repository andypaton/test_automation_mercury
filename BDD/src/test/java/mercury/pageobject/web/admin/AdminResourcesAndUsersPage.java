package mercury.pageobject.web.admin;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.PopupAlert;

public class AdminResourcesAndUsersPage extends Base_Page<AdminResourcesAndUsersPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String RESOURCES_AND_USERS_XPATH = "//div[@class='admin-action__header']//h1[text()='Resources & Users admin']//ancestor::div[@class='admin-action']";

    private static final String LEFT_MENU_XPATH = RESOURCES_AND_USERS_XPATH + "//a[contains(text(), '%s')]";
    private static final String SELECTED_MENU_ITEM = RESOURCES_AND_USERS_XPATH + "//a[@class='admin-highlighted-link']";

    private static final String RESOURCES_XPATH = RESOURCES_AND_USERS_XPATH + "//div[@ng-app='resourceAdmin']";
    private static final String ADD_NEW_RESOURCE_BUTTON_XPATH = RESOURCES_XPATH + "//button[contains(@ui-sref, 'draft')]";
    private static final String RESOURCE_SEARCH_XPATH = RESOURCES_XPATH + "//input[@data-text-field='name']";
    private static final String RESOURCE_PROFILE_SEARCH_XPATH = RESOURCES_XPATH + "//input[@data-text-field='resourceProfile.alias']";
    private static final String RESOURCES_ACTIVE_TAB_XPATH = RESOURCES_XPATH + "//md-tab-title[contains(text(), 'Active')]/..";
    private static final String RESOURCES_INACTIVE_TAB_XPATH = RESOURCES_XPATH + "//md-tab-title[contains(text(), 'Inactive')]/..";
    private static final String RESOURCES_INCOMPLETE_TAB_XPATH = RESOURCES_XPATH + "//md-tab-title[contains(text(), 'Incomplete')]/..";

    private static final String RESOURCES_ACTIVE_INACTIVE_RESOURCE_FILTER_XPATH = RESOURCES_XPATH + "//md-tab-content[contains(@class, 'md-active')]//input[@data-text-field='name']";
    private static final String RESOURCES_ACTIVE_INACTIVE_RESOURCE_PROFILE_FILTER_XPATH = RESOURCES_XPATH + "//md-tab-content[contains(@class, 'md-active')]//input[@data-text-field='resourceProfile.alias']";
    private static final String RESOURCES_INCOMPLETE_RESOURCE_FILTER_XPATH = RESOURCES_XPATH + "//input[@data-text-field='resource.name']";
    private static final String RESOURCES_INCOMPLETE_RESOURCE_PROFILE_FILTER_XPATH = RESOURCES_XPATH + "//input[@data-text-field='resource.resourceProfileAlias']";
    private static final String RESOURCES_SELECTED_TAB_XPATH = RESOURCES_XPATH + "//md-tab-item[@aria-selected='true']//md-tab-title";

    private static final String RESOURCE_PROFILES_XPATH = RESOURCES_AND_USERS_XPATH + "//div[@ng-app='resourceProfileAdmin']";
    private static final String ADD_NEW_RESOURCE_PROFILE_BUTTON_XPATH = RESOURCE_PROFILES_XPATH + "//button[contains(@ui-sref, 'resourceProfile')]";
    private static final String RESOURCE_PROFILES_SELECTED_TAB_XPATH = RESOURCE_PROFILES_XPATH + "//md-tab-item[@aria-selected='true']//md-tab-title";
    private static final String RESOURCE_PROFILES_ACTIVE_TAB_XPATH = RESOURCE_PROFILES_XPATH + "//md-tab-title[contains(text(), 'Active')]/..";
    private static final String RESOURCE_PROFILES_INACTIVE_TAB_XPATH = RESOURCE_PROFILES_XPATH + "//md-tab-title[contains(text(), 'Inactive')]/..";
    private static final String RESOURCE_PROFILES_INCOMPLETE_TAB_XPATH = RESOURCE_PROFILES_XPATH + "//md-tab-title[contains(text(), 'Incomplete')]/..";
    private static final String RESOURCE_PROFILE_FILTER_XPATH = RESOURCE_PROFILES_XPATH + "//md-tab-content[contains(@class, 'md-active')]//input[@data-text-field='name']";
    private static final String RESOURCE_TYPE_FILTER_XPATH = RESOURCE_PROFILES_XPATH + "//md-tab-content[contains(@class, 'md-active')]//input[@data-text-field='resourceType.name']";
    private static final String EDIT_RESOURCE_PROFILE_XPATH = RESOURCE_PROFILES_XPATH + "//td[contains(text(), '%s')]//a";

    private static final String USER_PROFILES_XPATH = RESOURCES_AND_USERS_XPATH + "//div[@ng-app='userProfileAdmin']";
    private static final String ADD_NEW_USER_PROFILE_BUTTON_XPATH = USER_PROFILES_XPATH + "//button[contains(@ui-sref, 'userProfile')]";
    private static final String USER_PROFILES_ACTIVE_TAB_XPATH = USER_PROFILES_XPATH + "//md-pagination-wrapper//md-tab-title[contains(text(), 'Active')]/..";
    private static final String USER_PROFILES_INACTIVE_TAB_XPATH = USER_PROFILES_XPATH + "//md-pagination-wrapper//md-tab-title[contains(text(), 'Inactive')]/..";
    private static final String USER_PROFILES_SELECTED_TAB_XPATH = USER_PROFILES_XPATH + "//md-tab-item[@aria-selected='true']//md-tab-title";
    private static final String USER_PROFILES_FILTER_SEARCHBOX_XPATH = USER_PROFILES_XPATH + "//md-tab-content[contains(@class, 'md-active')]//input[@class='k-input']";
    private static final String EDIT_USER_PROFILE_XPATH = USER_PROFILES_XPATH + "//td[contains(text(), '%s')]//a";

    private static final String USERS_XPATH = RESOURCES_AND_USERS_XPATH + "//div[@ng-app='userAdmin']";
    private static final String ADD_NEW_USER_BUTTON_XPATH = USERS_XPATH + "//button[contains(@ui-sref, 'user')]";
    private static final String USER_ACTIVE_TAB_XPATH = USERS_XPATH + "//md-pagination-wrapper//md-tab-title[contains(text(), 'Active')]/..";
    private static final String USER_INACTIVE_TAB_XPATH = USERS_XPATH + "//md-pagination-wrapper//md-tab-title[contains(text(), 'Inactive')]/..";
    private static final String USER_SELECTED_TAB_XPATH = USERS_XPATH + "//md-tab-item[@aria-selected='true']//md-tab-title";
    private static final String USER_NAME_FILTER_XPATH = USERS_XPATH + "//md-tab-content[contains(@class, 'md-active')]//input[@data-text-field='fullName']";
    private static final String USER_USERNAME_FILTER_XPATH = USERS_XPATH + "//md-tab-content[contains(@class, 'md-active')]//input[@data-text-field='userName']";
    private static final String USER_RESOURCE_PROFILE_FILTER_XPATH = USERS_XPATH + "//md-tab-content[contains(@class, 'md-active')]//input[@data-text-field='resourceProfileName']";

    private static final String IMPERSONATE_USER_GRID_XPATH = RESOURCES_AND_USERS_XPATH + "//div[@class='js-grid k-grid k-widget']";
    private static final String IMPERSONATE_USER_GRID_FILTER_XPATH = RESOURCES_AND_USERS_XPATH + "//a[text()='%s']/..//span[@class='k-icon k-filter']";
    private static final String IMPERSONATE_USER_GRID_FILTER_DROPDOWN_XPATH = "(//form[contains(@class, 'k-state-border-up')]//span[@class='k-widget k-dropdown k-header']//span[@class='k-input'])[%d]";

    private static final String IMPERSONATE_USER_XPATH = RESOURCES_AND_USERS_XPATH + "//ul[contains(@class, 'nav-pills')]//li/a[contains(text(), 'Impersonate user')]";

    private static final String GRID_ACTIVE_USER_XPATH = RESOURCES_AND_USERS_XPATH + "//md-tab-content[contains(@class, 'md-active')]//div[@data-role='grid']";
    private static final String GRID_IMPERSONATE_USER_XPATH = RESOURCES_AND_USERS_XPATH + "//div[@data-role='grid']";

    private static final String GRID_PAGES_XPATH = GRID_ACTIVE_USER_XPATH + "//a[@data-page='%d']";
    private static final String GRID_CURRENT_PAGE_XPATH = GRID_ACTIVE_USER_XPATH + "//li[@class='k-current-page']//span";

    private static final String EDIT_RESOURCE_XPATH = GRID_ACTIVE_USER_XPATH + "//td[contains(text(), '%s')]//a";
    private static final String EDIT_USER_XPATH = GRID_ACTIVE_USER_XPATH + "//td[contains(text(), \"%s\")]//a";


    @FindBy(xpath = IMPERSONATE_USER_XPATH)
    private WebElement impersonateUser;

    @FindBy(xpath = SELECTED_MENU_ITEM)
    private WebElement selectedMenuItem;

    @FindBy(xpath = ADD_NEW_RESOURCE_BUTTON_XPATH)
    private WebElement newResource;

    @FindBy(xpath = ADD_NEW_RESOURCE_PROFILE_BUTTON_XPATH)
    private WebElement newResourceProfile;

    @FindBy(xpath = ADD_NEW_USER_PROFILE_BUTTON_XPATH)
    private WebElement newUserProfile;

    @FindBy(xpath = ADD_NEW_USER_BUTTON_XPATH)
    private WebElement newUser;

    @FindBy(xpath = USER_PROFILES_SELECTED_TAB_XPATH)
    private WebElement userProfileSelectedTab;

    @FindBy(xpath = RESOURCE_PROFILES_SELECTED_TAB_XPATH)
    private WebElement resourceProfileSelectedTab;

    @FindBy(xpath = RESOURCES_SELECTED_TAB_XPATH)
    private WebElement resourcesSelectedTab;

    @FindBy(xpath = USER_SELECTED_TAB_XPATH)
    private WebElement userSelectedTab;

    @FindBy(xpath = GRID_CURRENT_PAGE_XPATH)
    private WebElement currentPage;

    @FindBy(xpath = RESOURCE_SEARCH_XPATH)
    private WebElement resourceSearch;

    @FindBy(xpath = RESOURCE_PROFILE_SEARCH_XPATH)
    private WebElement resourceProfileSearch;

    @FindBy(xpath = USER_NAME_FILTER_XPATH)
    private WebElement userNameFilter;

    @FindBy(xpath = USER_USERNAME_FILTER_XPATH)
    private WebElement userUsernameFilter;

    @FindBy(xpath = USER_RESOURCE_PROFILE_FILTER_XPATH)
    private WebElement userResourceProfileFilter;


    public AdminResourcesAndUsersPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(IMPERSONATE_USER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getSelectedMenuItem() {
        return selectedMenuItem.getText();
    }

    public String getSelectedTabNameUserProfiles() {
        return userProfileSelectedTab.getText();
    }

    public String getSelectedTabNameResourceProfiles() {
        return resourceProfileSelectedTab.getText();
    }

    public String getSelectedTabNameResources() {
        return resourcesSelectedTab.getText();
    }

    public String getSelectedTabNameUsers() {
        return userSelectedTab.getText();
    }

    public void selectMenuItem(String menuName) {
        WebElement menuItem = driver.findElement(By.xpath(String.format(LEFT_MENU_XPATH, menuName)));
        menuItem.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isMenuItemDisplayed(String menuName) {
        WebElement menuItem = driver.findElement(By.xpath(String.format(LEFT_MENU_XPATH, menuName)));
        return menuItem.isDisplayed();
    }

    public AdminAddNewUserPage addNewUser() {
        POHelper.scrollToElement(newUser);
        newUser.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminAddNewUserPage.class).get();
    }

    public AdminAddNewUserProfilePage addNewUserProfile() {
        newUserProfile.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminAddNewUserProfilePage.class).get();
    }

    public AdminAddNewResourceProfilePage addNewResourceProfile() {
        newResourceProfile.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminAddNewResourceProfilePage.class).get();
    }

    public AdminAddNewResourcePage clickAddNewResource() {
        newResource.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminAddNewResourcePage.class).get();
    }

    public boolean isNamefilterDisplayedUsers() {
        return userNameFilter.isDisplayed();
    }

    public boolean isUsernameFilterDisplayedUsers() {
        return userUsernameFilter.isDisplayed();
    }

    public boolean isResourceProfileDisplayedUsers() {
        return userResourceProfileFilter.isDisplayed();
    }

    public boolean isAddNewResourceButtonDisplayed() {
        return newResource.isDisplayed();
    }

    public boolean isAddNewResourceProfileButtonDisplayed() {
        return newResourceProfile.isDisplayed();
    }

    public boolean isAddNewUserProfileButtonDisplayed() {
        return newUserProfile.isDisplayed();
    }

    public boolean isAddNewUserButtonDisplayed() {
        return newUser.isDisplayed();
    }

    public void selectActiveTabUsers() {
        waitForElement(By.xpath(USER_ACTIVE_TAB_XPATH), ELEMENT_IS_CLICKABLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(USER_ACTIVE_TAB_XPATH));
            waitForAngularRequestsToFinish();
            POHelper.clickJavascript(By.xpath(USER_ACTIVE_TAB_XPATH));
        } while (getSelectedTabNameUsers().equalsIgnoreCase("Inactive") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void selectInactiveTabUsers() {
        waitForElement(By.xpath(USER_INACTIVE_TAB_XPATH), ELEMENT_IS_CLICKABLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(USER_INACTIVE_TAB_XPATH));
            waitForAngularRequestsToFinish();
            POHelper.clickJavascript(By.xpath(USER_INACTIVE_TAB_XPATH));
        } while (getSelectedTabNameUsers().equalsIgnoreCase("Active") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void selectActiveTabUserProfiles() {
        waitForElement(By.xpath(USER_PROFILES_ACTIVE_TAB_XPATH), ELEMENT_IS_CLICKABLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(USER_PROFILES_ACTIVE_TAB_XPATH));
            waitForAngularRequestsToFinish();
            POHelper.clickJavascript(By.xpath(USER_PROFILES_ACTIVE_TAB_XPATH));
        } while (getSelectedTabNameUserProfiles().equalsIgnoreCase("Inactive") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void selectInactiveTabUserProfiles() {
        waitForElement(By.xpath(USER_PROFILES_INACTIVE_TAB_XPATH), ELEMENT_IS_CLICKABLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(USER_PROFILES_INACTIVE_TAB_XPATH));
            waitForAngularRequestsToFinish();
            POHelper.clickJavascript(By.xpath(USER_PROFILES_INACTIVE_TAB_XPATH));
        } while (getSelectedTabNameUserProfiles().equalsIgnoreCase("Active") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void selectActiveTabResourceProfiles() {
        waitForElement(By.xpath(RESOURCE_PROFILES_ACTIVE_TAB_XPATH), ELEMENT_IS_CLICKABLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(RESOURCE_PROFILES_ACTIVE_TAB_XPATH));
            waitForAngularRequestsToFinish();
            POHelper.clickJavascript(By.xpath(RESOURCE_PROFILES_ACTIVE_TAB_XPATH));
        } while (!getSelectedTabNameResourceProfiles().equalsIgnoreCase("Active") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void selectInactiveTabResourceProfiles() {
        waitForElement(By.xpath(RESOURCE_PROFILES_INACTIVE_TAB_XPATH), ELEMENT_IS_CLICKABLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(RESOURCE_PROFILES_INACTIVE_TAB_XPATH));
            waitForAngularRequestsToFinish();
            POHelper.clickJavascript(By.xpath(RESOURCE_PROFILES_INACTIVE_TAB_XPATH));
        } while (!getSelectedTabNameResourceProfiles().equalsIgnoreCase("Inactive") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void selectIncompleteTabResourceProfiles() {
        waitForElement(By.xpath(RESOURCE_PROFILES_INCOMPLETE_TAB_XPATH), ELEMENT_IS_CLICKABLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(RESOURCE_PROFILES_INCOMPLETE_TAB_XPATH));
            waitForAngularRequestsToFinish();
            POHelper.clickJavascript(By.xpath(RESOURCE_PROFILES_INCOMPLETE_TAB_XPATH));
        } while (!getSelectedTabNameResourceProfiles().equalsIgnoreCase("Incomplete") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void searchForResourceProfile(String searchQuery) {
        WebElement searchBox = waitForElement(By.xpath(RESOURCE_PROFILE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.sendKeysWithoutWaitForKendo(searchBox, searchQuery);
        searchBox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void searchForResourceType(String searchQuery) {
        WebElement searchBox = waitForElement(By.xpath(RESOURCE_TYPE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.sendKeysWithoutWaitForKendo(searchBox, searchQuery);
        searchBox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void searchForUserProfile(String searchQuery) {
        WebElement searchBox = waitForElement(By.xpath(USER_PROFILES_FILTER_SEARCHBOX_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.sendKeysWithoutWaitForKendo(searchBox, searchQuery);
        searchBox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void searchForUserName(String searchQuery) {
        WebElement searchBox = waitForElement(By.xpath(USER_NAME_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.sendKeysWithoutWaitForKendo(searchBox, searchQuery);
        searchBox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void searchForUserUsername(String searchQuery) {
        WebElement searchBox = waitForElement(By.xpath(USER_USERNAME_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.sendKeysWithoutWaitForKendo(searchBox, searchQuery);
        searchBox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void searchForUserResourceProfile(String searchQuery) {
        WebElement searchBox = waitForElement(By.xpath(USER_RESOURCE_PROFILE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.sendKeysWithoutWaitForKendo(searchBox, searchQuery);
        searchBox.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public AdminEditResourceProfilePage editResourceProfile(String resourceProfileName) {
        WebElement editResourceProfile = driver.findElement(By.xpath(String.format(EDIT_RESOURCE_PROFILE_XPATH, resourceProfileName)));
        editResourceProfile.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditResourceProfilePage.class).get();
    }

    public AdminEditUserProfilePage editUserProfile(String userProfileName) {
        WebElement editUserProfile = driver.findElement(By.xpath(String.format(EDIT_USER_PROFILE_XPATH, userProfileName)));
        editUserProfile.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditUserProfilePage.class).get();
    }

    public void selectImpersonateUser(String username) {
        impersonateUser.click();
        waitForAngularRequestsToFinish();
        GridHelper.filterIsEqualTo(GRID_IMPERSONATE_USER_XPATH, "User Name", username);
        Grid grid = GridHelper.getGrid(GRID_IMPERSONATE_USER_XPATH);
        assertTrue("Filtering on username: " + username + ". Expected 1 row. Got " + grid.getRows().size(), grid.getRows().size() == 1);
        GridHelper.selectFirstRow(GRID_IMPERSONATE_USER_XPATH);
    }

    public void selectFirstRowToImpersonate() {
        GridHelper.selectFirstRow(GRID_IMPERSONATE_USER_XPATH);
    }

    public void confirmImpersonateUser() {
        PopupAlert popupAlert = new PopupAlert(getWebDriver()).get();
        popupAlert.click("Yes, Proceed!");
        POHelper.waitForAngularRequestsToFinish();
        POHelper.waitWhileBusy();
    }

    public Grid getActiveUserGrid() {
        return GridHelper.getGrid(GRID_ACTIVE_USER_XPATH);
    }

    public Grid getImpersonateUserGrid() {
        return GridHelper.getGrid(IMPERSONATE_USER_GRID_XPATH);
    }

    public Grid getImpersonateUserGridFirstRow() {
        return GridHelper.getGrid(IMPERSONATE_USER_GRID_XPATH, 1);
    }

    public List<String> getImpersonateUserGridAsString() {
        return GridHelper.getRowsAsString(IMPERSONATE_USER_GRID_XPATH);
    }

    public void selectPage(int pageNumber) {
        WebElement page = driver.findElement(By.xpath(String.format(GRID_PAGES_XPATH, pageNumber)));
        page.click();
        waitForAngularRequestsToFinish();
    }

    public Integer getCurrentPageNumber() {
        return Integer.valueOf(currentPage.getAttribute("innerHTML"));
    }

    public void selectResourcesActiveTab() {
        waitForElement(By.xpath(RESOURCES_ACTIVE_TAB_XPATH), ELEMENT_IS_VISIBLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(RESOURCES_ACTIVE_TAB_XPATH));
            waitForAngularRequestsToFinish();
        } while (!getSelectedTabNameResources().equalsIgnoreCase("Active") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void selectResourcesInactiveTab() {
        waitForElement(By.xpath(RESOURCES_INACTIVE_TAB_XPATH), ELEMENT_IS_VISIBLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(RESOURCES_INACTIVE_TAB_XPATH));
            waitForAngularRequestsToFinish();
        } while (!getSelectedTabNameResources().equalsIgnoreCase("Inactive") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void selectResourcesIncompleteTab() {
        waitForElement(By.xpath(RESOURCES_INCOMPLETE_TAB_XPATH), ELEMENT_IS_VISIBLE);
        Instant start = Instant.now();
        do {
            POHelper.clickJavascript(By.xpath(RESOURCES_INCOMPLETE_TAB_XPATH));
            waitForAngularRequestsToFinish();
        } while (!getSelectedTabNameResources().equalsIgnoreCase("Incomplete") && Duration.between(start, Instant.now()).toMinutes() < 2);
    }

    public void setActiveInactiveResourceFilter(String option) {
        WebElement resourceActiveInactiveResourceFilter = waitForElement(By.xpath(RESOURCES_ACTIVE_INACTIVE_RESOURCE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        resourceActiveInactiveResourceFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        resourceActiveInactiveResourceFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setActiveInactiveResourceProfileFilter(String option) {
        WebElement resourceActiveInactiveResourceProfileFilter = waitForElement(By.xpath(RESOURCES_ACTIVE_INACTIVE_RESOURCE_PROFILE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        resourceActiveInactiveResourceProfileFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        resourceActiveInactiveResourceProfileFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setIncompleteResourceFilter(String option) {
        WebElement resourceIncompleteResourceFilter = waitForElement(By.xpath(RESOURCES_INCOMPLETE_RESOURCE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        resourceIncompleteResourceFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        resourceIncompleteResourceFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setIncompleteResourceProfileFilter(String option) {
        WebElement resourceIncompleteResourceProfileFilter = waitForElement(By.xpath(RESOURCES_INCOMPLETE_RESOURCE_PROFILE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        resourceIncompleteResourceProfileFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        resourceIncompleteResourceProfileFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public AdminEditResourcePage editResource(String resourceName) {
        WebElement editResource = driver.findElement(By.xpath(String.format(EDIT_RESOURCE_XPATH, resourceName)));
        editResource.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditResourcePage.class).get();
    }

    public AdminEditUserPage editUser(String userName) {
        WebElement editUser = driver.findElement(By.xpath(String.format(EDIT_USER_XPATH, userName)));
        editUser.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditUserPage.class).get();
    }

    public void selectImpersonateGridFilter(String columnName) {
        WebElement filter = driver.findElement(By.xpath(String.format(IMPERSONATE_USER_GRID_FILTER_XPATH, columnName)));
        filter.click();
        waitForAngularRequestsToFinish();
    }

    public String getTextOfFilterDropdown(int number) {
        WebElement filterDropdown = driver.findElement(By.xpath(String.format(IMPERSONATE_USER_GRID_FILTER_DROPDOWN_XPATH, number)));
        return filterDropdown.getText();
    }

    public void filterImpersonatorGridContains(String column, String filter) {
        GridHelper.filterContains(GRID_IMPERSONATE_USER_XPATH, column, filter.trim());
    }

    public void filterImpersonatorGridIsEqualTo(String column, String filter) {
        GridHelper.filterIsEqualTo(GRID_IMPERSONATE_USER_XPATH, column, filter.trim());
    }

    public void filterImpersonatorGridDoesNotContain(String column, String filter) {
        GridHelper.filterDoesNotContain(GRID_IMPERSONATE_USER_XPATH, column, filter.trim());
    }

    public void clearFilterOnImpersonatorGrid(String column) throws InterruptedException {
        GridHelper.filterClear(GRID_IMPERSONATE_USER_XPATH, column);
    }

}
