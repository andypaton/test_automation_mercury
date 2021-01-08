package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class AdminPpmsPage extends Base_Page<AdminPpmsPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PPM_HOME_XPATH = "//div[@class='admin-action__header']//h1[text()='PPMs admin']//ancestor::div[@class='admin-action']";

    private static final String LEFT_MENU_XPATH = PPM_HOME_XPATH + "//a[contains(text(), '%s')]";
    private static final String SELECTED_MENU_ITEM = PPM_HOME_XPATH + "//a[@class='admin-highlighted-link']";

    private static final String PPM_TYPES_XPATH = PPM_HOME_XPATH + "//div[@ng-app='ppmTypesAdmin']";
    private static final String FILTER_XPATH = PPM_TYPES_XPATH + "//div[contains(@class, 'filter-option-wrap')]";
    private static final String FILTER_NAME_XPATH = FILTER_XPATH + "//label[contains(text(), 'Name')]/following-sibling::input";
    private static final String FILTER_CLASSIFICATION_XPATH = FILTER_XPATH + "//label[contains(text(), 'Classification')]/following-sibling::span";
    private static final String FILTER_ASSET_TYPE_XPATH = FILTER_XPATH + "//label[contains(text(), 'Asset Type')]/following-sibling::span";
    private static final String FILTER_CALLOUT_ADVANCE_WEEKS_XPATH = FILTER_XPATH + "//label[contains(text(), 'Callout Advance Weeks')]/following-sibling::span";
    private static final String CLEAR_FILTER_BUTTON_XPATH = FILTER_XPATH + "//button[@ng-click='$ctrl.clearFilter()']";
    private static final String SEARCH_BUTTON_XPATH = FILTER_XPATH + "//button[@ng-click='$ctrl.filter()']";
    private static final String GRID_XPATH = PPM_TYPES_XPATH + "//div[@data-role='grid']";
    private static final String RECORDS_PER_PAGE_XPATH = PPM_TYPES_XPATH + "//select[@ng-model='$ctrl.pageSize']//option[@selected='selected']";
    private static final String ADD_NEW_PPM_TYPE_XPATH = PPM_TYPES_XPATH + "//button[@ui-sref='createPpmType']";


    @FindBy(xpath = SELECTED_MENU_ITEM)
    private WebElement selectedMenuItem;

    @FindBy(xpath = RECORDS_PER_PAGE_XPATH)
    private WebElement recordsPerPage;

    @FindBy(xpath = ADD_NEW_PPM_TYPE_XPATH)
    private WebElement addNewPpmType;


    public AdminPpmsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(PPM_HOME_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isMenuItemDisplayed(String menuName) {
        return isElementPresent(By.xpath(String.format(LEFT_MENU_XPATH, menuName)));
    }

    public String getSelectedMenuItem() {
        return selectedMenuItem.getText();
    }

    public boolean isNameFilterDisplayed() {
        return isElementPresent(By.xpath(FILTER_NAME_XPATH));
    }

    public boolean isClassificationFilterDisplayed() {
        return isElementPresent(By.xpath(FILTER_CLASSIFICATION_XPATH));
    }

    public boolean isAssetTypeFilterDisplayed() {
        return isElementPresent(By.xpath(FILTER_ASSET_TYPE_XPATH));
    }

    public boolean isCalloutAdvanceWeeksFilterDisplayed() {
        return isElementPresent(By.xpath(FILTER_CALLOUT_ADVANCE_WEEKS_XPATH));
    }

    public boolean isClearFilterButtonDisplayed() {
        return isElementPresent(By.xpath(CLEAR_FILTER_BUTTON_XPATH));
    }

    public boolean isSearchButtonDisplayed() {
        return isElementPresent(By.xpath(SEARCH_BUTTON_XPATH));
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public String getRecordsPerPage() {
        return recordsPerPage.getText();
    }

    public AdminAddNewPpmTypePage addNewPpmType() {
        addNewPpmType.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminAddNewPpmTypePage.class).get();
    }

}
