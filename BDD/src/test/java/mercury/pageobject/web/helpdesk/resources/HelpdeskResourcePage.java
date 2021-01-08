package mercury.pageobject.web.helpdesk.resources;

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
import mercury.pageobject.web.helpdesk.jobs.HelpdeskAddOrOverrideAbsencePanel;

public class HelpdeskResourcePage extends Base_Page<HelpdeskResourcePage> {

    private static final Logger logger = LogManager.getLogger();


    private static final String MAIN_CONTENT_XPATH = "//div[@class='view-main-content']";

    private static final String ACTIVE_WORKSPACE_CSS = "#main-panel > div.tab-content > div.tab-pane.active div.view.resource";
    protected static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[@class='view resource']";

    // HEADERS
    private static final String HEADLINE_CSS = ACTIVE_WORKSPACE_CSS + " div.view-header__headline h1";
    private static final String SUBHEAD_CSS = ACTIVE_WORKSPACE_CSS +" div.view-header__subhead";
    private static final String SUBHEAD_LEFT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__left";
    private static final String SUBHEAD_RIGHT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__right";

    // BUTTONS
    private static final String BUTTONS_BAR_CSS = ACTIVE_WORKSPACE_CSS +" div.view-button-bar__buttons-container";
    private static final String BUTTON_XPATH = ACTIVE_WORKSPACE_XPATH + "//button[text() = '%s']";
    private static final String ACTIONS_BUTTON_XPATH =  ACTIVE_WORKSPACE_XPATH + "//a[contains(.,'Actions ')]";
    private static final String ADD_OR_OVERRIDE_BUTTON_XPATH =  ACTIVE_WORKSPACE_XPATH + "//a[contains(.,'Add or override absence')]";


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

    @FindBy(xpath=ACTIONS_BUTTON_XPATH)
    WebElement actionsButton;

    @FindBy(xpath=ADD_OR_OVERRIDE_BUTTON_XPATH)
    WebElement addOrOverrideButton;

    public HelpdeskResourcePage(WebDriver driver) {
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

    private WebElement getButton(String button) {
        return buttonBar.findElement(By.xpath(String.format(BUTTON_XPATH, button)));
    }

    public HelpdeskResourcePage clickIdentifyCallerButton() {
        getButton("Identify Caller").click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskResourcePage.class).get();
    }

    public HelpdeskAddOrOverrideAbsencePanel selectAddOrOverrideAbsenceAction() {
        actionsButton.click();
        waitForAngularRequestsToFinish();
        addOrOverrideButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAddOrOverrideAbsencePanel.class).get();
    }
}
