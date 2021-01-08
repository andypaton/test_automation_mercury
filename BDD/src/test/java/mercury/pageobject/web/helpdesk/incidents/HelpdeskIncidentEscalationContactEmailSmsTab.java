package mercury.pageobject.web.helpdesk.incidents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import java.util.ArrayList;
import java.util.List;

public class HelpdeskIncidentEscalationContactEmailSmsTab extends Base_Page<HelpdeskIncidentEscalationContactEmailSmsTab>{

    private static final Logger logger = LogManager.getLogger();

    // Core
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String INCIDENT_ESCALATION_PAGE_XPATH
    = ACTIVE_WORKSPACE_XPATH + "//ph-incident-escalation//form[@name='incidentEscalationForm']//div[contains(@class,'incident-action__content')]";
    private static final String EMAIL_SMS_TAB_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//ph-incident-notifications[contains(@notifications, 'nonCallList')]";
    private static final String TABLE_XPATH = EMAIL_SMS_TAB_XPATH + "//table";

    // Icons
    private static final String ESCALATION_CONTACT_HEADER_XPATH = TABLE_XPATH + "//tr//th[contains(text(),'Name')]";
    private static final String ESCALATION_CONTACT_ICON_XPATH = TABLE_XPATH + "//tr//th[contains(@uib-tooltip,'%s')]";

    // Escalation Contacts
    private static final String ESCLATION_CONTACTS_XPATH = TABLE_XPATH + "//tbody//tr/td//label";

    // Radio Button
    private static final String RADIO_BUTTON_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//ph-incident-notifications//table/tbody//td//input[contains(@name,'%s') and @uib-tooltip = '%s']";

    @FindBy(xpath = EMAIL_SMS_TAB_XPATH)
    private WebElement emailSmsTab;

    @FindBy(xpath = ESCALATION_CONTACT_HEADER_XPATH)
    private WebElement escalationContactHeader;

    @FindBy(xpath = ESCLATION_CONTACTS_XPATH)
    private List<WebElement> escalationContacts;

    public HelpdeskIncidentEscalationContactEmailSmsTab(WebDriver driver) {
        super(driver);

    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", emailSmsTab.isDisplayed());
            logger.info("Email/SMS Tab on incident Escalation loaded");
        } catch(Exception e){
            logger.info("Email/SMS Tab on incident Escalation failed to load");
            throw new AssertionError();
        }
    }

    public void clickRadioButton(String name, String button) {
        waitForAngularRequestsToFinish();
        WebElement element = waitForElement(By.xpath(String.format(RADIO_BUTTON_XPATH, name, button)), ELEMENT_IS_CLICKABLE);
        element.click();
    }

    public Boolean IsIconDisplayed(String icon) {
        WebElement element = waitForElement(By.xpath(String.format(ESCALATION_CONTACT_ICON_XPATH, icon)), ELEMENT_IS_CLICKABLE);
        return element.isDisplayed();
    }

    public Boolean IsEscalationContactHeaderDisplayed() {
        return escalationContactHeader.isDisplayed();
    }

    public List<String> getEscalationContacts() {
        List<String> contacts = new ArrayList<String>();
        for (WebElement e : escalationContacts) {
            contacts.add(e.getAttribute("innerText"));
        }
        return contacts;
    }
}
