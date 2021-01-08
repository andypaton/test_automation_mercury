package mercury.pageobject.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.State;

import mercury.pageobject.web.Base_Page;

public class AdminOrganisationPage extends Base_Page<AdminOrganisationPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Admin Organisation Structure Page";

    private static final String PAGE_BODY_XPATH = "//html//body[@ng-app='phAdmin']";
    private static final String PAGE_CONTAINER_XPATH = PAGE_BODY_XPATH + "//div[@class='container-fluid']";
    private static final String PAGE_ORGANISATION_ADMIN_XPATH = PAGE_CONTAINER_XPATH + "//div//mc-organisation-admin";
    private static final String PAGE_ROW_ORGANISATION_ADMIN_XPATH = PAGE_ORGANISATION_ADMIN_XPATH + "//div[@class='row organisation-admin']//div[@class='col-sm-3']";
    private static final String MANAGERS_AND_SITES_CONTAINER_XPATH = PAGE_ORGANISATION_ADMIN_XPATH + "//div[@class='row organisation-admin']//div[@class='col-sm-9 admin-action']";
    private static final String MANAGERS_AND_SITES_ROW_XPATH = MANAGERS_AND_SITES_CONTAINER_XPATH + "//div[3][@class='row']";

    //Dialog box
    private static final String DIALOG_BOX_XPATH = PAGE_BODY_XPATH + "//div[contains(@role, 'dialog')]//div[contains(@class, 'modal-dialog modal-sm')]//div[contains(@class, 'modal-content')]";

    // Colour key
    private static final String COLOUR_KEY_XPATH = PAGE_ROW_ORGANISATION_ADMIN_XPATH  + "//div[@class='col-sm-12 color-key']//ul";

    //Hierarchies
    private static final String ACTIVE_HIERARCHIES_XPATH = PAGE_ROW_ORGANISATION_ADMIN_XPATH + "//div[@class='col-sm-12']//ol[@class='row']";
    private static final String HIERARCHY_OPTION_XPATH = ACTIVE_HIERARCHIES_XPATH + "//li//div[contains(., '%s')]";
    private static final String HIERARCHY_OPTION_ARROW_XPATH = "//span[2]";
    private static final String HIERARCHY_OPTION_DIVISIONS_XPATH = ACTIVE_HIERARCHIES_XPATH + "//li//ol[@ng-if='team.expanded' and @class='child-list']";
    private static final String OPTION_LEVEL_XPATH = "//ol//li//div[contains(., '%s')]";

    //Managers
    private static final String MANAGERS_ADMIN_XPATH = MANAGERS_AND_SITES_ROW_XPATH + "//div[contains(@class, 'manage-managers')]//div[contains(@class, 'sub-content')]";
    private static final String ADD_MANAGER_XPATH = MANAGERS_ADMIN_XPATH + "//div[contains(@class, 'add-manager')]";
    private static final String AVAILABLE_MANAGERS_XPATH = MANAGERS_ADMIN_XPATH + "//div[contains(@class, 'view-managers')]";
    private static final String VIEW_MANAGERS_XPATH = AVAILABLE_MANAGERS_XPATH + "//div[contains(@class, 'manager--name')]";
    private static final String ADD_REMOVE_MANAGER_BUTTONS_XPATH = "//ph-foreign-key-list[contains(@id, 'newManagers')]//div[contains(@class, 'row')]//div//button[contains(@ng-click, '%s')]";
    private static final String ALERT_MESSAGE_XPATH = MANAGERS_ADMIN_XPATH + "//div[contains(@class, 'alert')]";
    private static final String MANAGERS_DROPDOWN_XPATH = ADD_MANAGER_XPATH + "//ph-foreign-key-list[contains(@id, 'newManagers')]//div[contains(@class, 'row')]//div[contains(@class, 'col-md-10')]//ph-foreign-key//span//input";

    //Sites
    private static final String ADD_MOVE_SITES_XPATH = MANAGERS_AND_SITES_ROW_XPATH + "//mc-organisation-admin-add-move-sites";
    private static final String MANAGE_SITES_XPATH = ADD_MOVE_SITES_XPATH + "//div[contains(@class, 'manage-sites admin-action')]";
    private static final String MANAGE_SITES_CONTENT_XPATH = MANAGE_SITES_XPATH + "//div[contains(@class, 'manage-sites__block')]";
    private static final String ADD_SITE_XPATH = MANAGE_SITES_CONTENT_XPATH + "//div[contains(@class, 'manage-sites__add')]";
    private static final String MOVE_SITE_XPATH = MANAGE_SITES_CONTENT_XPATH + "//div[contains(@class, 'manage-sites__move')]";
    private static final String SITE_NAMES_XPATH = MOVE_SITE_XPATH + "//div//div[contains(@class, 'site-name')]";
    private static final String SITE_DROPDOWN_XPATH = "//div[contains(., '%s')]//div[contains(@class, 'site-select')]//select";
    private static final String SAVE_CANCEL_BUTTONS_XPATH = "//button[contains(., '%s')]";

    @FindBy(xpath = HIERARCHY_OPTION_DIVISIONS_XPATH)
    private WebElement hierarchyDivisions;

    @FindBy(xpath = SITE_DROPDOWN_XPATH)
    private WebElement siteDropdown;

    public AdminOrganisationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_BODY_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Boolean isColourKeyDisplayed() {
        waitForElement(By.xpath(COLOUR_KEY_XPATH), State.ELEMENT_IS_VISIBLE);
        return this.isElementPresent(By.xpath(COLOUR_KEY_XPATH));
    }

    public Boolean isActiveHierarchiesDisplayed() throws InterruptedException {
        Thread.sleep(3000);
        waitForElement(By.xpath(ACTIVE_HIERARCHIES_XPATH), State.ELEMENT_IS_VISIBLE);
        return this.isElementPresent(By.xpath(ACTIVE_HIERARCHIES_XPATH));
    }

    public Boolean isHierarchyDivisionsDisplayed() {
        return this.isElementPresent(By.xpath(HIERARCHY_OPTION_DIVISIONS_XPATH));
    }

    public Boolean isAlertMessageDisplayed() {
        return this.isElementPresent(By.xpath(ALERT_MESSAGE_XPATH));
    }

    public String getAlertMessage() {
        WebElement we = driver.findElement(By.xpath(String.format(ALERT_MESSAGE_XPATH)));
        return we.getText();
    }

    public void selectHierarchyOption(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(HIERARCHY_OPTION_XPATH + HIERARCHY_OPTION_ARROW_XPATH, option)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void selectOption(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(OPTION_LEVEL_XPATH + HIERARCHY_OPTION_ARROW_XPATH, option)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void selectTechPosition(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(OPTION_LEVEL_XPATH, option)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void clickSaveOrCancelButton(String buttonName) {
        WebElement we = driver.findElement(By.xpath(String.format(SAVE_CANCEL_BUTTONS_XPATH, buttonName)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void clickAddOrRemoveButton(String buttonName) {
        WebElement we = driver.findElement(By.xpath(String.format(ADD_REMOVE_MANAGER_BUTTONS_XPATH, buttonName)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public String getSiteName() {
        List<String> siteNames = getAvailableSites();
        int randomSelection = RandomUtils.nextInt(1, siteNames.size());
        String selection = siteNames.get(randomSelection);
        return selection;
    }

    public String getManagerName() {
        List<String> managerNames = getAvailableManagers();
        int randomSelection = RandomUtils.nextInt(1, managerNames.size());
        String selection = managerNames.get(randomSelection);
        return selection;
    }

    public String selectRandomManager() {
        WebElement we = driver.findElement(By.xpath(String.format(MANAGERS_DROPDOWN_XPATH)));
        we.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String selectRandomLocation(String siteLocation) {
        WebElement we = driver.findElement(By.xpath(String.format(MOVE_SITE_XPATH + SITE_DROPDOWN_XPATH, siteLocation)));
        we.click();
        return  selectRandomOptionFromSelect(we);
    }

    public List<String> getHierarchyOptions() {
        List<String> hierarchyOptions = new ArrayList<>();
        List<WebElement> hierarchyOptionsList = driver.findElements(By.xpath(ACTIVE_HIERARCHIES_XPATH));
        for (WebElement hierarchyOption : hierarchyOptionsList) {
            hierarchyOptions.add(hierarchyOption.getText());
        }
        return hierarchyOptions;
    }

    public List<String> getHierarchyDivisions() {
        List<String> hierarchyDivisions = new ArrayList<>();
        List<WebElement> hierarchyDivisionsList = driver.findElements(By.xpath(HIERARCHY_OPTION_DIVISIONS_XPATH));
        for (WebElement hierarchyDivision : hierarchyDivisionsList) {
            hierarchyDivisions.add(hierarchyDivision.getText());
        }
        return hierarchyDivisions;
    }

    public List<String> getAvailableSites() {
        List<String> availableSites = new ArrayList<>();
        try {
            List<WebElement> availableSitesList = driver.findElements(By.xpath(SITE_NAMES_XPATH));
            for (WebElement availableSite : availableSitesList) {
                availableSites.add(availableSite.getText());
            }
        } catch (StaleElementReferenceException e) {
            List<WebElement> availableSitesList = driver.findElements(By.xpath(SITE_NAMES_XPATH));
            for (WebElement availableSite : availableSitesList) {
                availableSites.add(availableSite.getText());
            }
        }
        return availableSites;
    }

    public List<String> getAvailableManagers() {
        List<String> availableManagers = new ArrayList<>();
        List<WebElement> availableManagersList = driver.findElements(By.xpath(VIEW_MANAGERS_XPATH));
        for (WebElement manager : availableManagersList) {
            availableManagers.add(manager.getText());
        }
        return availableManagers;
    }
}
