package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class AdminComplianceConfigPage extends Base_Page<AdminComplianceConfigPage> {

    private static final Logger logger = LogManager.getLogger();
    
    private static final String CERTIFICATE_CONFIGURATION_HOME_XPATH = "//div[@class='admin-action__header']//h1[text()='Certificate Configuration admin']//ancestor::div[@class='admin-action']";
    
    private static final String LEFT_MENU_XPATH = CERTIFICATE_CONFIGURATION_HOME_XPATH + "//a[contains(text(), '%s')]";
    
    private static final String ADD_NEW_CERTIFICATE_TYPE_BUTTON_XPATH = CERTIFICATE_CONFIGURATION_HOME_XPATH + "//button[@ui-sref='draft({ id: undefined })']";
    
    @FindBy(xpath = ADD_NEW_CERTIFICATE_TYPE_BUTTON_XPATH)
    private WebElement newCertificateType;
    
    
    public AdminComplianceConfigPage(WebDriver driver) {
        super(driver);
    }
    
    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(CERTIFICATE_CONFIGURATION_HOME_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }
    
    public boolean isMenuItemDisplayed(String menuName) {
        return isElementPresent(By.xpath(String.format(LEFT_MENU_XPATH, menuName)));
    }
 
    public boolean isAddNewCertificateTypeButtonDisplayed() {
        return newCertificateType.isDisplayed();
    }
}