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

import mercury.pageobject.web.Base_Page;

public class AdminPpmTypeAssetPage extends Base_Page<AdminPpmTypeAssetPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PPM_TYPE_ASSET_XPATH = "//div[@class='admin-action__header']//h1[text()='PPMs admin']//ancestor::div[@class='admin-action']";

    private static final String ADD_NEW_PPM_TYPE_ASSET_BUTTON_XPATH = PPM_TYPE_ASSET_XPATH + "//div[contains(text(), 'Add new PPM Type Asset')]/..//button";


    @FindBy(xpath = ADD_NEW_PPM_TYPE_ASSET_BUTTON_XPATH)
    private WebElement addNewPpmTypeAsset;


    public AdminPpmTypeAssetPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(PPM_TYPE_ASSET_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public AdminPpmTypeAssetMappingPage addNewPpmTypeAsset() {
        addNewPpmTypeAsset.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminPpmTypeAssetMappingPage.class).get();
    }

}
