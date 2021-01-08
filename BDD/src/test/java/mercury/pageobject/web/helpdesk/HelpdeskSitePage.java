package mercury.pageobject.web.helpdesk;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.caller.HelpdeskAddCallerPage;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskLogAnIncidentPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskLogJobPage;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskOpenJobsTab;
import mercury.pageobject.web.helpdesk.resources.HelpdeskResourcesTab;

public class HelpdeskSitePage extends Base_Page<HelpdeskSitePage>{

    private static final Logger logger = LogManager.getLogger();

    private static final String MAIN_CONTENT_XPATH = "//div[@class='view-main-content']";

    private static final String ACTIVE_WORKSPACE_CSS = "#main-panel > div.tab-content > div.tab-pane.active div.view.site";
    protected static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[@class='view site']";

    private static final String ADD_NEW_CALLER_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//button[text() = 'Add new caller']";

    // HEADERS
    private static final String HEADLINE_CSS = ACTIVE_WORKSPACE_CSS + " div.view-header__headline";
    private static final String SUBHEAD_CSS = ACTIVE_WORKSPACE_CSS +" div.view-header__subhead";
    private static final String SUBHEAD_LEFT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__left";
    private static final String SUBHEAD_RIGHT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__right";

    // BUTTONS
    private static final String BUTTONS_BAR_CSS = ACTIVE_WORKSPACE_CSS +" div.view-button-bar__buttons-container";
    private static final String BUTTON_XPATH = "//button[text() = '%s']";

    // TABS
    private static final String TAB_XPATH = ACTIVE_WORKSPACE_XPATH + "//ul[@class='nav nav-tabs']/li/a[text()='%s']";

    @FindBy(css=ACTIVE_WORKSPACE_CSS)
    private WebElement activeWorkspace;

    @FindBy(css=HEADLINE_CSS)
    private WebElement headline;

    @FindBy(css=SUBHEAD_CSS)
    private WebElement subHeadline;

    @FindBy(css=SUBHEAD_LEFT_CSS)
    private WebElement subHeadline_left;

    @FindBy(css=SUBHEAD_RIGHT_CSS)
    private WebElement subHeadline_right;

    @FindBy(css=BUTTONS_BAR_CSS)
    private WebElement buttonBar;

    @FindBy(xpath = ACTIVE_WORKSPACE_XPATH + MAIN_CONTENT_XPATH)
    private WebElement mainContent;


    public HelpdeskSitePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.cssSelector(HEADLINE_CSS)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    private WebElement getButton(String button) {
        return buttonBar.findElement(By.xpath(String.format(BUTTON_XPATH, button)));
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

    public HelpdeskLogJobPage clickLogAJobButton() {
        getButton("Log a job").click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
    }

    public HelpdeskLogAnIncidentPage clickLogAnIncidentButton() {
        getButton("Log an incident").click();
        return PageFactory.initElements(driver, HelpdeskLogAnIncidentPage.class).get();
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

    public HelpdeskResourcesTab selectResourcesTab() {
        WebElement tab = waitForElement(By.xpath(String.format(TAB_XPATH, "Resources")), ELEMENT_IS_CLICKABLE);
        tab.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskResourcesTab.class).get();
    }

    public HelpdeskOpenJobsTab selectOpenJobsTab() {
        WebElement tab = waitForElement(By.xpath(String.format(TAB_XPATH, "Open Jobs")), ELEMENT_IS_CLICKABLE);
        tab.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskOpenJobsTab.class).get();
    }

    public HelpdeskOpenJobsTab getOpenJobs() {
        return PageFactory.initElements(driver, HelpdeskOpenJobsTab.class).get();
    }

    public HelpdeskAddCallerPage clickAddNewCaller() {
        WebElement addNewCaller = waitForElement(By.xpath(ADD_NEW_CALLER_XPATH), ELEMENT_IS_CLICKABLE);
        POHelper.scrollToElement(addNewCaller);
        POHelper.clickJavascript(addNewCaller);
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAddCallerPage.class).get();
    }

    public Boolean isIncidentsTabDisplayed() {
        return isElementVisible(By.xpath(String.format(TAB_XPATH, "Incidents")));
    }

    public void hoverOverIcon(String icon) {
        WebElement we = driver.findElement(By.xpath(String.format("//img[@uib-tooltip = '%s']", icon)));
        Actions builder = new Actions(driver);
        builder.moveToElement(we).build().perform();
    }

}
