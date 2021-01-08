package mercury.pageobject.web.helpdesk.incidents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskIncidentActionsDropdown extends Base_Page<HelpdeskIncidentActionsDropdown> {

    private static final Logger logger = LogManager.getLogger();

    //WORKSPACE
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String VIEW_INCIDENT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='view incident']";

    // ACTIONS
    private static final String INCIDENT_BUTTONS_CONTAINER_XPATH = VIEW_INCIDENT_XPATH + "//div[contains(@class,'view-button-bar') and contains(@class,'buttons-container')]";
    private static final String ACTIONS_BUTTON_XPATH = INCIDENT_BUTTONS_CONTAINER_XPATH + "//a[contains(@class,'btn-primary') and contains(@class,'dropdown-toggle')]";
    private static final String ACTIONS_XPATH = ACTIONS_BUTTON_XPATH + "/..//li//a[contains(text(),'%s')]";

    @FindBy(xpath = ACTIONS_BUTTON_XPATH)
    private WebElement actions;

    public HelpdeskIncidentActionsDropdown(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", actions.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void clickActions() {
        actions.click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskCancelIncidentPage selectCancelIncident() throws Throwable {
        clickActions();
        WebElement element = waitForElement(By.xpath(String.format(ACTIONS_XPATH, "Cancel Incident")), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskCancelIncidentPage.class).get();
    }

    public HelpdeskLogAnIncidentPage selectEditIncident() throws Throwable {
        clickActions();
        WebElement element = waitForElement(By.xpath(String.format(ACTIONS_XPATH, "Edit")), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogAnIncidentPage.class).get();
    }

    public HelpdeskLogAnIncidentPage reviewIncident() throws Throwable {
        clickActions();
        WebElement element = waitForElement(By.xpath(String.format(ACTIONS_XPATH, "Review")), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogAnIncidentPage.class).get();
    }

    public HelpdeskIncidentFollowUpPage selectFollowUp() throws Throwable {
        clickActions();
        WebElement element = waitForElement(By.xpath(String.format(ACTIONS_XPATH, "Follow Up")), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskIncidentFollowUpPage.class).get();
    }

    public HelpdeskIncidentEscalation selectEscalate() {
        clickActions();
        WebElement element = waitForElement(By.xpath(String.format(ACTIONS_XPATH, "Escalate")), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskIncidentEscalation.class).get();
    }

    public HelpdeskIncidentSendEscalation selectEscalationCalls() {
        clickActions();
        WebElement element = waitForElement(By.xpath(String.format(ACTIONS_XPATH, "Escalation Calls")), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskIncidentSendEscalation.class).get();
    }

}
