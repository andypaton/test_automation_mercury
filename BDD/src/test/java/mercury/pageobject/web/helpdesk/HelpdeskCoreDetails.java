package mercury.pageobject.web.helpdesk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class HelpdeskCoreDetails extends Base_Page<HelpdeskCoreDetails> {

    private static final Logger logger = LogManager.getLogger();

    protected static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String CORE_DETAILS_XPATH = ACTIVE_WORKSPACE_XPATH + "//*[contains(text(),'Core Details')]";
    protected static final String DESC_TITLE_XPATH = CORE_DETAILS_XPATH + "/..//*[contains(text(),'%s')]";
    protected static final String DESC_VALUE_XPATH = CORE_DETAILS_XPATH + "/..//*[contains(text(),'%s')]/following-sibling::*";


    @FindBy(xpath=CORE_DETAILS_XPATH)
    WebElement coreDetails;


    public HelpdeskCoreDetails(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(CORE_DETAILS_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isCoreTitleDisplayed(String title) {
        return coreDetails.findElement(By.xpath(String.format(DESC_TITLE_XPATH, title))).isDisplayed();
    }

    public String getCoreValue(String title) {
        return coreDetails.findElement(By.xpath(String.format(DESC_VALUE_XPATH, title))).getText();
    }

}
