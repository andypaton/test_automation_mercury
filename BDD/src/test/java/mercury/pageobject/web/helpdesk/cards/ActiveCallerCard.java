package mercury.pageobject.web.helpdesk.cards;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class ActiveCallerCard extends Base_Page<ActiveCallerCard> {

    private static final Logger logger = LogManager.getLogger();


    private static final String CALLER_ON_CALL_XPATH = "//div[@id='side-panel']/div[@caller='app.activeCaller']";

    private static final String CARD_HEADER_XPATH = CALLER_ON_CALL_XPATH + "//div[@class='card-header']";
    private static final String CARD_HEADER_ICON_XPATH = CALLER_ON_CALL_XPATH + "//div[@class='card-header__icon-container']";

    private static final String CARD_SUBHEADER_LEFT_XPATH =CALLER_ON_CALL_XPATH +  "//div[@class='card-subheader__left']";
    private static final String CARD_SUBHEADER_RIGHT_XPATH = CALLER_ON_CALL_XPATH + "//div[@class='card-subheader__right']";
    private static final String CARD_CONTENT_XPATH = CALLER_ON_CALL_XPATH + "//div[@class='card-content']";

    private static final String WRENCH_XPATH = CALLER_ON_CALL_XPATH + "//i[@class='icons__wrench']/..";
    private static final String EXCLAMATION_TRIANGLE_XPATH = CALLER_ON_CALL_XPATH + "//i[@class='icons__exclamation-triangle']/..";

    private static final String TIMER_XPATH = CALLER_ON_CALL_XPATH + "//span[contains(@class, '__timer')]";
    private static final String END_CALL_XPATH = CALLER_ON_CALL_XPATH + "//button[text() = 'End call']";

    @FindBy(xpath=CALLER_ON_CALL_XPATH)
    private static WebElement callerCard;

    @FindBy(xpath=CARD_HEADER_XPATH)
    private static WebElement header;

    @FindBy(xpath=CARD_HEADER_ICON_XPATH)
    private static WebElement headerIcon;

    @FindBy(xpath=CARD_SUBHEADER_LEFT_XPATH)
    private static WebElement subHeader_left;

    @FindBy(xpath=CARD_SUBHEADER_RIGHT_XPATH)
    private static WebElement subHeader_right;

    @FindBy(xpath=CARD_CONTENT_XPATH)
    private static WebElement content;

    @FindBy(xpath=WRENCH_XPATH)
    private static WebElement wrench;

    @FindBy(xpath=EXCLAMATION_TRIANGLE_XPATH)
    private static WebElement exclamationTriangle;

    @FindBy(xpath=TIMER_XPATH)
    private static WebElement timer;


    @FindBy(xpath=END_CALL_XPATH)
    private static WebElement endCall;

    public ActiveCallerCard(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(CALLER_ON_CALL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isCardDisplayed() throws Exception {
        return isElementPresent(By.xpath(CALLER_ON_CALL_XPATH));
    }

    public String getHeader() throws Exception {
        return header.getText();
    }

    public String getSubHeaderLeft() throws Exception {
        return subHeader_left.getText();
    }

    public String getSubHeaderRight() throws Exception {
        return subHeader_right.getText();
    }

    public String getContent() throws Exception {
        return content.getText();
    }

    public Integer getWrenchCount() throws Exception {
        String countAsString = wrench.getText();
        return countAsString.isEmpty() ? 0 : Integer.valueOf(countAsString);
    }

    public Integer getExclamationTriangleCount() throws Exception {
        String countAsString = exclamationTriangle.getText();
        return countAsString.isEmpty() ? 0 : Integer.valueOf(countAsString);
    }

    public boolean isTimerDisplayed() throws Exception {
        return isElementPresent(By.xpath(TIMER_XPATH));
    }

    public String getTimer() throws Exception {
        return timer.getText();
    }

    public void endCall() throws Exception {
        endCall.click();
        waitForAngularRequestsToFinish();
    }
}
