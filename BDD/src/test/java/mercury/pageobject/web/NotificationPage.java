package mercury.pageobject.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.State;

public class NotificationPage extends Base_Page<NotificationPage> {
 
    private static final Logger logger = LogManager.getLogger();

// Page title
    private static final String PAGE_TITLE = "Notification Page";

    private static final String SCREEN_XPATH = "//div[@class = 'jumbotron']";

    private static final String HEADING_XPATH = SCREEN_XPATH + "//h3[text()='Phoenix Notifications']";
    private static final String ACTIVITY_MONITOR_XPATH = "//div[@class = 'container']//a[@href='/Home/Monitor']";

    @FindBy(xpath = HEADING_XPATH)
    private WebElement heading;
    
    @FindBy(xpath = ACTIVITY_MONITOR_XPATH)
    private WebElement activityMonitor;


    public NotificationPage(WebDriver driver) {
        super(driver);
    }
    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForAngularRequestsToFinish();
            waitForElement(By.xpath(HEADING_XPATH),State.ELEMENT_IS_VISIBLE);
            Assert.assertTrue("Page is not displayed", heading.isDisplayed());
            logger.info(PAGE_TITLE + " isloaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }
    
    public boolean isActivityMonitorDisplayed() {
        waitForAngularRequestsToFinish();
        return isElementPresent(By.xpath(ACTIVITY_MONITOR_XPATH));
    }
}