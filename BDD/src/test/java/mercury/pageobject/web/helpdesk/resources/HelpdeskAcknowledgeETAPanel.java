package mercury.pageobject.web.helpdesk.resources;


import static org.junit.Assert.assertTrue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class HelpdeskAcknowledgeETAPanel extends Base_Page<HelpdeskAcknowledgeETAPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String ACKNOWLEDGE_ETA_HEADING_XPATH = ACTIVE_WORKSPACE_XPATH + "//h4[contains(text(),'Acknowledge ETA')]";
    private static final String ACKNOWLEDGE_ETA_FORM_XPATH = ACTIVE_WORKSPACE_XPATH +  "//form[@name='acknowledgeEtaForm']";
    private static final String ACKNOWLEDGED_BY_XPATH = ACKNOWLEDGE_ETA_FORM_XPATH + "//input[@name='requestedBy']";
    private static final String NOTES_XPATH = ACKNOWLEDGE_ETA_FORM_XPATH + "//textarea[@name='notes']";

    //BUTTONS
    private static final String BUTTONS_XPATH = ACKNOWLEDGE_ETA_FORM_XPATH + "//div[contains(@class, 'resource-action__button-container')]";
    private static final String SAVE_BUTTON_XPATH = BUTTONS_XPATH + "//button[contains(text(), 'Save')]";
    private static final String CANCEL_BUTTON_XPATH = BUTTONS_XPATH + "//button[contains(text(), 'Cancel')]";


    @FindBy(xpath = ACKNOWLEDGED_BY_XPATH)
    private WebElement acknowledgedBy;

    @FindBy(xpath = NOTES_XPATH)
    private WebElement notes;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    public HelpdeskAcknowledgeETAPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            assertTrue("Acknowledge ETA panel not loaded!", driver.findElement(By.xpath(ACKNOWLEDGE_ETA_HEADING_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void setAcknowledgedBy(String notes) {
        acknowledgedBy.sendKeys(notes);
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy.getText();
    }

    public void setNotes(String text) {
        notes.sendKeys(text);
    }

    public String getNotes() {
        return notes.getText();
    }

    public HelpdeskManageResourcesPanel save() {
        saveButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskManageResourcesPanel.class).get();
    }

}
