package mercury.pageobject.web.helpdesk.incidents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.State;

public class HelpdeskIncidentEscalationAdditionalContact extends Base_Page<HelpdeskIncidentEscalationAdditionalContact> {

    private static final Logger logger = LogManager.getLogger();
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String INCIDENT_ESCALATION_PAGE_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-incident-action//div[contains(@class,'incident-action')]";
    private static final String ADDITIONAL_CONTACT_PAGE_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//div[contains(@class,'tab-pane') and contains(@class,'active')]//h2[text()='Add Additional Contact']/../..";

    // Additional Contact Link
    private static final String ADD_ADDITIONAL_CONTACT_LABEL_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//ph-incident-notifications/..//h2[text()='Add Additional Contact']/following-sibling::div//div";
    private static final String NAME_INPUT_XPATH = ADD_ADDITIONAL_CONTACT_LABEL_XPATH + "//label[text()='Name']/following-sibling::input";
    private static final String EMAIL_INPUT_XPATH = ADD_ADDITIONAL_CONTACT_LABEL_XPATH + "//label[text()='Email']/following-sibling::input";
    private static final String TELEPHONE_INPUT_XPATH = ADD_ADDITIONAL_CONTACT_LABEL_XPATH + "//label[text()='Telephone']/following-sibling::input";
    private static final String CONTACT_BY_RADIO_BUTTON_XPATH = ADD_ADDITIONAL_CONTACT_LABEL_XPATH + "/label[@class='control-label']/following-sibling::input[@uib-tooltip='%s']";
    private static final String ADD_BUTTON_XPATH = ADD_ADDITIONAL_CONTACT_LABEL_XPATH + "//button[text()='Add']";
    private static final String ADDED_ADDITIONAL_CONTACT_GRID_XPATH = INCIDENT_ESCALATION_PAGE_XPATH + "//ph-incident-notifications[contains(@notifications, 'additionalList')]";

    @FindBy(xpath = ADDITIONAL_CONTACT_PAGE_XPATH)
    private WebElement additionalContactPage;

    @FindBy(xpath = NAME_INPUT_XPATH)
    private WebElement nameInput;

    @FindBy(xpath = EMAIL_INPUT_XPATH)
    private WebElement emailInput;

    @FindBy(xpath = TELEPHONE_INPUT_XPATH)
    private WebElement telephoneInput;

    @FindBy(xpath = ADD_BUTTON_XPATH)
    private WebElement addButton;

    public HelpdeskIncidentEscalationAdditionalContact(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", additionalContactPage.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterName(String name) {
        waitForAngularRequestsToFinish();
        nameInput.sendKeys(name);
        nameInput.sendKeys(Keys.RETURN);
    }

    public void enterEmail(String email) {
        waitForAngularRequestsToFinish();
        emailInput.sendKeys(email);
        emailInput.sendKeys(Keys.RETURN);
    }

    public void enterTelephone(String telephone) {
        waitForAngularRequestsToFinish();
        telephoneInput.sendKeys(telephone);
        telephoneInput.sendKeys(Keys.RETURN);
    }

    public void clickContactByRadioButton(String button) {
        WebElement element = waitForElement(By.xpath(String.format(CONTACT_BY_RADIO_BUTTON_XPATH, button)), State.ELEMENT_IS_CLICKABLE);
        element.click();
    }

    public void clickAddButton() {
        addButton.click();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(ADDED_ADDITIONAL_CONTACT_GRID_XPATH);
    }
}
