package mercury.pageobject.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.State;

public class PopupAlert extends Base_Page<PopupAlert> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Confirm Alert";
    private static final String SWEET_ALERT = "//div[contains(@class, 'sweet-alert')]";

    private static final String CONTAINS_TEXT_XPATH = "//div[contains(@class, 'sweet-alert') and contains(., '%s')]";
    private static final String CONTAINS_TITLE_AND_DESCRIPTION_XPATH = "//div[contains(@class, 'sweet-alert') and contains(., '%s') and contains(., \"%s\")]";
    private static final String BUTTON_XPATH = SWEET_ALERT + "//button[contains(text(), '%s')]";

    @FindBy(css = ALERT_CONFIRM_CSS)
    private WebElement confirm;

    @FindBy(css = ALERT_CONFIRM_CSS)
    private WebElement ok;

    @FindBy(css = ALERT_CANCEL_CSS)
    private WebElement cancel;


    public PopupAlert(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForAngularRequestsToFinish();
            waitForJavascriptToLoad();
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(ALERT_CSS));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public void click(String button) {
        WebElement we = waitForElement(By.xpath(String.format(BUTTON_XPATH, button)), State.ELEMENT_IS_VISIBLE);
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void confirm() {
        POHelper.clickJavascript(confirm);
    }

    public void ok() {
        POHelper.clickJavascript(ok);
    }

    public void cancel() {
        POHelper.clickJavascript(cancel);
    }

    public boolean containsText(String text) {
        return isElementPresent(By.xpath(String.format(CONTAINS_TEXT_XPATH, text)));
    }

    public boolean containsText(String title, String description) {
        return isElementPresent(By.xpath(String.format(CONTAINS_TITLE_AND_DESCRIPTION_XPATH, title, description)));
    }

}
