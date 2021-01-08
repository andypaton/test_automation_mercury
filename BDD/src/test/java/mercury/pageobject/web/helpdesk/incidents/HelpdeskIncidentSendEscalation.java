package mercury.pageobject.web.helpdesk.incidents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class HelpdeskIncidentSendEscalation extends Base_Page<HelpdeskIncidentSendEscalation>{

    private static final Logger logger = LogManager.getLogger();

    //Workspace
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String INCIDENT_SEND_ESCALATION_PAGE_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-incident-action//div[contains(@class,'incident-action')]//h1[@role='button' and contains(text(),'Send Escalations for incident')]";
    private static final String INCIDENT_FOLLOW_UP_MESSAGE = INCIDENT_SEND_ESCALATION_PAGE_XPATH + "/../..//div[@class='row']//h2[@class='pull-right']";
    private static final String SEND_ESCALATIONS_BUTTON = ACTIVE_WORKSPACE_XPATH + "//div[@class='footer-button-bar']//button[text()='Send Escalations']";
    private static final String INFORMATION_MESSAGE = ACTIVE_WORKSPACE_XPATH + "//div[@class='incident-action__content']//div[contains(@class,'escalation__summary--sms')]";
    private static final String CALLED_CHECKBOX = ACTIVE_WORKSPACE_XPATH + "//div[@class='incident-action__content']//div[contains(@class,'callrecord')]//input[@type='checkbox']";
    private static final String INFORMATION_THAT_WILL_BE_SENT_XPATH = "//div[@class='incident-action__content']//div[contains(@class,'escalation__summarynote--current')]";

    //Date Picker
    private static final String NEXT_FOLLOW_UP_AT_CLOCK_XPATH = ACTIVE_WORKSPACE_XPATH + "//form[@name='nextFollowupForm']" + DROPDOWN_CLOCK_XPATH ;
    private static final String NEXT_FOLLOW_UP_AT_WRAPPER_XPATH = ACTIVE_WORKSPACE_XPATH + DROPDOWN_PICK_WRAPPER_XPATH + "//input";

    @FindBy(xpath = INCIDENT_SEND_ESCALATION_PAGE_XPATH)
    private WebElement incidentSendEscalationPage;

    @FindBy(xpath = INCIDENT_FOLLOW_UP_MESSAGE)
    private WebElement incidentFollowUpMessage;

    @FindBy(xpath = SEND_ESCALATIONS_BUTTON)
    private WebElement sendEscalationsbutton;

    @FindBy(xpath = INFORMATION_MESSAGE)
    private WebElement informationMessage;

    @FindBy(xpath = CALLED_CHECKBOX)
    private WebElement calledCheckbox;

    @FindBy(xpath = NEXT_FOLLOW_UP_AT_CLOCK_XPATH)
    private WebElement nextFollowUpClock;

    @FindBy(xpath = NEXT_FOLLOW_UP_AT_WRAPPER_XPATH)
    private WebElement nextFollowUpDate;

    @FindBy(xpath = INFORMATION_THAT_WILL_BE_SENT_XPATH)
    private WebElement informationEmail;

    public HelpdeskIncidentSendEscalation(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", incidentSendEscalationPage.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getFollowUpText() {
        return incidentFollowUpMessage.getText();
    }

    public HelpdeskViewIncidentPage clickSendEscalationsButton() {
        waitForAngularRequestsToFinish();
        sendEscalationsbutton.click();
        return PageFactory.initElements(driver, HelpdeskViewIncidentPage.class).get();
    }

    public String getInformationMessageText() {
        waitForAngularRequestsToFinish();
        return informationMessage.getText();
    }

    public void clickCalledCheckbox() {
        calledCheckbox.click();
    }

    public boolean isNextFollowUpTimeDisplayed() {
        return nextFollowUpDate.isDisplayed();
    }

    public String getNextFollowUpPrepopulatedTime() {
        return nextFollowUpDate.getAttribute("value");
    }

    public String selectNextFollowUpDate() {
        nextFollowUpClock.click();
        return selectRandomVisibleDropdownOption(1);
    }

    public String getInformationEmailText() {
        return informationEmail.getText();
    }

    public void enterFollowUpTime(String time) {
        nextFollowUpDate.clear();
        nextFollowUpDate.sendKeys(time);
    }
}
