package mercury.pageobject.web.helpdesk.caller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class HelpdeskCallerPage extends Base_Page<HelpdeskCallerPage> {

    private static final Logger logger = LogManager.getLogger();


    private static final String MAIN_CONTENT_XPATH = "//div[@class='view-main-content']";

    private static final String ACTIVE_WORKSPACE_CSS = "#main-panel > div.tab-content > div.tab-pane.active div.view.caller";
    protected static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[@class='view caller']";

    // HEADERS
    private static final String HEADLINE_CSS = ACTIVE_WORKSPACE_CSS + " div.view-header__headline";
    private static final String SUBHEAD_CSS = ACTIVE_WORKSPACE_CSS +" div.view-header__subhead";
    private static final String SUBHEAD_LEFT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__left";
    private static final String SUBHEAD_RIGHT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__right";

    // BUTTONS
    private static final String BUTTONS_BAR_CSS = ACTIVE_WORKSPACE_CSS +" div.view-button-bar__buttons-container";
    private static final String BUTTON_XPATH = "//button[text() = '%s']";

    // CONTENT
    protected static final String DESC_TITLE_XPATH = "//dt[contains(text(),'%s')]";
    protected static final String DESC_VALUE_XPATH = "//dt[contains(text(),'%s')]/following-sibling::dd[1]";


    @FindBy(xpath = ACTIVE_WORKSPACE_XPATH + MAIN_CONTENT_XPATH)
    WebElement mainContent;

    @FindBy(css=HEADLINE_CSS)
    WebElement headline;

    @FindBy(css=SUBHEAD_CSS)
    WebElement subHeadline;

    @FindBy(css=SUBHEAD_LEFT_CSS)
    WebElement subHeadline_left;

    @FindBy(css=SUBHEAD_RIGHT_CSS)
    WebElement subHeadline_right;

    @FindBy(css=BUTTONS_BAR_CSS)
    WebElement buttonBar;

    public HelpdeskCallerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.cssSelector(HEADLINE_CSS)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getHeadline() {
        return headline.getText();
    }

    public String getSubHeadline() {
        return subHeadline.getText();
    }

    public String getSubHeadlineLeft() {
        return subHeadline_left.getText();
    }

    public String getSubHeadlineRight() {
        return subHeadline_right.getText();
    }

    public boolean isContentTitleDisplayed(String title) {
        return mainContent.findElement(By.xpath(String.format(DESC_TITLE_XPATH, title))).isDisplayed();
    }

    public String getContentValue(String title) {
        return mainContent.findElement(By.xpath(String.format(DESC_VALUE_XPATH, title))).getText();
    }

    public boolean isButtonDisplayed(String button) {
        return getButton(button).isDisplayed();
    }

    public boolean isButtonEnabled(String button) {
        return getButton(button).isEnabled();
    }

    public boolean isButtonDisabled(String button) {
        return "true".equals(getButton(button).getAttribute("disabled"));
    }

    private WebElement getButton(String button) {
        return buttonBar.findElement(By.xpath(String.format(BUTTON_XPATH, button)));
    }

    public void clickIdentifyCallerButton() {
        getButton("Identify Caller").click();
        waitForAngularRequestsToFinish();
    }

}
