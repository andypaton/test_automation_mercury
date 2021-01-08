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

public class AdminAssetsPage extends Base_Page<AdminAssetsPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ASSETS_XPATH = "//div[@class='admin-action__header']//h1[text()='Assets admin']//ancestor::div[@class='admin-action']";

    private static final String LEFT_MENU_XPATH = ASSETS_XPATH + "//a[contains(text(), '%s')]";
    private static final String SELECTED_MENU_ITEM = ASSETS_XPATH + "//a[@class='admin-highlighted-link']";

    //Asset Register
    private static final String ASSET_REGISTER_XPATH = ASSETS_XPATH + "//div[@ng-app='assetRegisterAdmin']";
    private static final String ADD_NEW_ASSET_BUTTON_XPATH = ASSET_REGISTER_XPATH + "//div[contains(text(),'Add new asset')]/..//button";
    private static final String ASSET_REGISTER_GRID_XPATH = ASSET_REGISTER_XPATH + "//div[@data-role='grid']";
    private static final String ASSET_TAG_FILTER_XPATH = ASSET_REGISTER_XPATH + "//th[@data-field='assetTag']//a";
    private static final String ASSET_TAG_FILTER_TEXTBOX_XPATH = "//form[contains(@class, 'k-filter-menu')]//input[@class='k-textbox']";
    private static final String ASSET_TAG_FILTER_BUTTON_XPATH = "//form[contains(@class, 'k-filter-menu')]//button[@type='submit']";
    private static final String EDIT_ASSET_XPATH = ASSET_REGISTER_XPATH + "//div[@class='k-grid-content']//a";


    @FindBy(xpath = SELECTED_MENU_ITEM)
    private WebElement selectedMenuItem;

    @FindBy(xpath = ADD_NEW_ASSET_BUTTON_XPATH)
    private WebElement addNewAsset;

    @FindBy(xpath = ASSET_TAG_FILTER_XPATH)
    private WebElement assetTagFilter;

    @FindBy(xpath = ASSET_TAG_FILTER_TEXTBOX_XPATH)
    private WebElement assetTagFilterTextbox;

    @FindBy(xpath = ASSET_TAG_FILTER_BUTTON_XPATH)
    private WebElement assetTagFilterButton;

    @FindBy(xpath = EDIT_ASSET_XPATH)
    private WebElement editAsset;


    public AdminAssetsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ASSETS_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isMenuItemDisplayed(String menuName) {
        WebElement menuItem = driver.findElement(By.xpath(String.format(LEFT_MENU_XPATH, menuName)));
        return menuItem.isDisplayed();
    }

    public String getSelectedMenuItem() {
        return selectedMenuItem.getText();
    }

    public boolean isAddNewAssetButtonDisplayed() {
        return addNewAsset.isDisplayed();
    }

    public Grid getAssetRegisterGrid() {
        return GridHelper.getGrid(ASSET_REGISTER_GRID_XPATH);
    }

    public AdminAddNewAssetPage addNewAsset() {
        addNewAsset.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminAddNewAssetPage.class).get();
    }

    public void filterOnAssetTag(String assetTag) {
        assetTagFilter.click();
        waitForAngularRequestsToFinish();
        assetTagFilterTextbox.sendKeys(assetTag);
        assetTagFilterButton.click();
        waitForAngularRequestsToFinish();
    }

    public AdminEditAssetPage editAsset() {
        editAsset.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditAssetPage.class).get();
    }

}
