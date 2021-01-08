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

public class HelpdeskIncidentEscalation extends Base_Page<HelpdeskIncidentEscalation> {

    private static final Logger logger = LogManager.getLogger();

    //Workspace
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String HEADER_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='incident-action__header']";
    private static final String INCIDENT_ESCALATION_PAGE_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-incident-action//div[contains(@class,'incident-action')]";
    private static final String INCIDENT_ACTION_CONTENT_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//div[@class='incident-action__content']";
    private static final String TEXT_MESSAGE = INCIDENT_ACTION_CONTENT_XPATH + "//label[text()='Text Message' ]/..//textarea[@name ='SMSText']";

    private static final String WHAT_WILL_BE_SENT_EMAILS_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//h2[text()='What Will Be Sent']/following-sibling::div[@class='row']//div[@class='escalation__summarynotes']";
    private static final String PREVIOUSLY_SENT_EMAILS_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//h2[text()='Previously Sent']/following-sibling::div[@class='row']//div[contains(@class,'escalation__summarynote--current')]";
    private static final String ESCALATION_EMAIL_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-incident-notifications//table/tbody//label[contains(text(),'%s')] //following-sibling::p";

    // Escalation Contacts
    private static final String ESCALATION_CONTACTS_TABS_XPATH
    = INCIDENT_ESCALATION_PAGE_XPATH + "//form[@name='incidentEscalationForm']//div[@class='incident-action__content']//ul[contains(@class,'nav-pills') and @data-tabs='tabs']";
    private static final String ESCALATION_CONTACTS_TAB_XPATH = ESCALATION_CONTACTS_TABS_XPATH + "//li//a[text()='%s']";

    //Button
    private static final String NEXT_BUTTON_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//div[@class='footer-button-bar']//button[text()='Next']";

    @FindBy(xpath = INCIDENT_ESCALATION_PAGE_XPATH)
    private WebElement incidentEscalationPage;

    @FindBy(xpath = HEADER_XPATH)
    private WebElement incidentHeader;

    @FindBy(xpath = NEXT_BUTTON_XPATH)
    private WebElement nextButton;

    @FindBy(xpath = TEXT_MESSAGE)
    private WebElement textMessageInput;

    @FindBy(xpath = PREVIOUSLY_SENT_EMAILS_XPATH)
    private WebElement previouslySentEmailsText;

    @FindBy(xpath = WHAT_WILL_BE_SENT_EMAILS_XPATH)
    private WebElement whatWillBeSentEmailsText;

    public HelpdeskIncidentEscalation(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", incidentEscalationPage.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public HelpdeskIncidentSendEscalation clickNextButton() {
        nextButton.click();
        return PageFactory.initElements(driver, HelpdeskIncidentSendEscalation.class).get();
    }

    public void enterTextMessage(String message) {
        waitForAngularRequestsToFinish();
        textMessageInput.clear();
        textMessageInput.sendKeys(message);
    }

    public void clickContactTab(String tab) {
        WebElement element = waitForElement(By.xpath(String.format(ESCALATION_CONTACTS_TAB_XPATH, tab)), State.ELEMENT_IS_CLICKABLE);
        element.click();
        waitForAngularRequestsToFinish();
    }

    public String getPreviouslySentEmailsText() {
        return previouslySentEmailsText.getText();
    }

    public String getWhatWillBeSentEmailsText() {
        return whatWillBeSentEmailsText.getText();
    }

    public String getIncidentHeader() {
        return incidentHeader.getText();
    }

    public String getEscalationEmail(String name) {
        WebElement escalationEmail = driver.findElement(By.xpath(String.format(ESCALATION_EMAIL_XPATH, name)));
        return escalationEmail.getText();
    }

}
