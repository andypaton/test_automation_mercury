package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class AdminPpmTypeAssetMappingPage extends Base_Page<AdminPpmTypeAssetMappingPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PPM_TYPE_ASSET_MAPPING_PAGE_XPATH = "//div[@class='admin-action']";

    private static final String DROPDOWN_XPATH = PPM_TYPE_ASSET_MAPPING_PAGE_XPATH + "//label[contains(text(), '%s')]/../..//following-sibling::span";


    public AdminPpmTypeAssetMappingPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(PPM_TYPE_ASSET_MAPPING_PAGE_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectOptionFromDropdown(String questionName, String dropdownOption) {
        WebElement we = waitForElement(By.xpath(String.format(DROPDOWN_XPATH, questionName)), State.ELEMENT_IS_CLICKABLE);
        we.click();
        waitForAngularRequestsToFinish();
        WebElement option = driver.findElement(By.xpath(String.format(DROPDOWN_OPTION_XPATH, dropdownOption)));
        option.click();
    }

}
