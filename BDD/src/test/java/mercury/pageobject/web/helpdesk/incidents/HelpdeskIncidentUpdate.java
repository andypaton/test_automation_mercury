package mercury.pageobject.web.helpdesk.incidents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskIncidentUpdate extends Base_Page<HelpdeskIncidentUpdate> {

    private static final Logger logger = LogManager.getLogger();

    private static final String INCIDENT_UPDATE_XPATH = "//div[@class='modal-dialog ']//div[@class='modal-content']//form[@name='updateNoteForm']";
    private static final String INCIDENT_UPDATE_HEADER = INCIDENT_UPDATE_XPATH + "//div[@class='modal-header']//h3";
    private static final String INCIDENT_UPDATE_NOTE = INCIDENT_UPDATE_XPATH + "//div[@class='modal-body']//label[@title='Note']/following-sibling::textarea";
    private static final String INCIDENT_UPDATE_CANCEL_BUTTON =  INCIDENT_UPDATE_XPATH + "//div[@class='modal-footer']//button[contains(text(),'Cancel')]";
    private static final String INCIDENT_UPDATE_SAVE_BUTTON =  INCIDENT_UPDATE_XPATH + "//div[@class='modal-footer']//button[contains(text(),'Save')]";


    @FindBy(xpath = INCIDENT_UPDATE_HEADER)
    private WebElement incidentHeader;

    @FindBy(xpath = INCIDENT_UPDATE_NOTE)
    private WebElement incidentNote;

    @FindBy(xpath = INCIDENT_UPDATE_CANCEL_BUTTON)
    private WebElement cancelButton;

    @FindBy(xpath = INCIDENT_UPDATE_SAVE_BUTTON)
    private WebElement saveButton;

    public HelpdeskIncidentUpdate(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", incidentHeader.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void clickSaveButton() throws Throwable {
        POHelper.clickJavascript(saveButton);
    }

    public String getHeader() throws Throwable{
        return incidentHeader.getText();
    }

    public void addnotes(String notes) {
        incidentNote.sendKeys(notes);
    }

    public Boolean isPageDisplayed() {
        return incidentHeader.isDisplayed();
    }
}
