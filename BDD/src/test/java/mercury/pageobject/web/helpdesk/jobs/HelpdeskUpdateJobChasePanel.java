package mercury.pageobject.web.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class HelpdeskUpdateJobChasePanel extends Base_Page<HelpdeskUpdateJobChasePanel>{

    public HelpdeskUpdateJobChasePanel(WebDriver driver) {
        super(driver);
    }


    private static final Logger logger = LogManager.getLogger();

    // Workspace, panel and form
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String CHASE_PANEL_CONTENT_XPATH = ACTIVE_WORKSPACE_XPATH +  "//div[contains(@class,'job-action-panel') and contains(@class,'job-chase')]//div[@class='job-action-panel__content']";
    private static final String CHASE_LATEST_NOTES_ALREADY_ADDED_XPATH = CHASE_PANEL_CONTENT_XPATH + "//div[@class='job-chase__update-updates'][last()]";
    private static final String UPDATE_CHASE_FORM_XPATH = CHASE_PANEL_CONTENT_XPATH + "//form[@id='chaseFormUpdate']";
    private static final String UPDATE_CHASE_FORM_DETAILS_XPATH = UPDATE_CHASE_FORM_XPATH + "//div[contains(@class,'job-chase__details')]";
    private static final String UPDATE_CHASE_FORM_FOOTER_XPATH = UPDATE_CHASE_FORM_DETAILS_XPATH + "//div[contains(@class,'footer-button-bar--wide')]";

    //Buttons
    private static final String CHASE_PANEL_UPDATE_CHASE_BUTTON_XPATH = CHASE_PANEL_CONTENT_XPATH + "//label[contains(text(), 'Update')]";
    private static final String CHASE_PANEL_RESOLVE_BUTTON_XPATH = CHASE_PANEL_CONTENT_XPATH + "//label[contains(text(), 'Resolve')]";
    private static final String CHASE_PANEL_SUBMIT_UPDATE_CHASE_BUTTON_XPATH = UPDATE_CHASE_FORM_FOOTER_XPATH + "//button[contains(@class,'btn btn-primary') and contains(text(), 'Update Chase')]";
    private static final String CHASE_PANEL_SUBMIT_RESOLVE_CHASE_BUTTON_XPATH = UPDATE_CHASE_FORM_FOOTER_XPATH + "//button[contains(@class,'btn btn-primary') and contains(text(), 'Resolve Chase')]";

    //Core
    private static final String CHASE_NOTES_TO_ADD_XPATH = CHASE_PANEL_CONTENT_XPATH + "//textarea[@id='updateChaseInformation']";
    private static final String CHASE_STORE_RESOLUTION_FEEDBACK_XPATH = CHASE_PANEL_CONTENT_XPATH + "//textarea[@id='updateStoreChaseInformation']";

    private static final String CHASE_UPDATES_XPATH = CHASE_PANEL_CONTENT_XPATH + "//div[@class='job-chase__updates']//div[@class='job-chase__update-updates']";


    @FindBy(xpath = CHASE_UPDATES_XPATH)
    private List<WebElement> updates;

    @FindBy(xpath = CHASE_PANEL_SUBMIT_UPDATE_CHASE_BUTTON_XPATH)
    private WebElement submitUpdateChase;

    @FindBy(xpath = CHASE_PANEL_SUBMIT_RESOLVE_CHASE_BUTTON_XPATH)
    private WebElement submitResolveChase;


    @FindBy(xpath = CHASE_PANEL_UPDATE_CHASE_BUTTON_XPATH)
    private WebElement updateChaseButton;

    @FindBy(xpath = CHASE_PANEL_RESOLVE_BUTTON_XPATH)
    private WebElement resolveChaseButton;

    @FindBy(xpath = CHASE_NOTES_TO_ADD_XPATH)
    private WebElement chaseNotesToAdd;


    @FindBy(xpath = CHASE_LATEST_NOTES_ALREADY_ADDED_XPATH)
    private WebElement chaseNotesAlreadyAdded;

    @FindBy(xpath = UPDATE_CHASE_FORM_DETAILS_XPATH)
    private WebElement chaseDetails;

    @FindBy(xpath = CHASE_STORE_RESOLUTION_FEEDBACK_XPATH)
    private WebElement chaseStoreResolutionFeedback;


    @Override
    protected void isLoaded() throws Error {
        try{
            assertTrue("Chase panel not loaded!", driver.findElement(By.xpath(UPDATE_CHASE_FORM_DETAILS_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public List<String> getAvailableUpdates() {
        try {
            List<String> result = new ArrayList<>();
            for (WebElement we : updates) {
                result.add(we.getText());
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isUpdateRadioButtonDisplayed() {
        return isElementPresent(By.xpath(CHASE_PANEL_UPDATE_CHASE_BUTTON_XPATH));
    }

    public boolean isResolveRadioButtonDisplayed() {
        return isElementPresent(By.xpath(CHASE_PANEL_RESOLVE_BUTTON_XPATH));
    }

    public Boolean isUpdateChaseButtonClickable() {
        try {
            return isElementClickable(By.xpath(CHASE_PANEL_SUBMIT_UPDATE_CHASE_BUTTON_XPATH));
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isResolveChaseButtonClickable() {
        try {
            return isElementClickable(By.xpath(CHASE_PANEL_SUBMIT_RESOLVE_CHASE_BUTTON_XPATH));
        } catch (Exception e) {
            return false;
        }
    }

    public void clickUpdateChase() {
        updateChaseButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickResolveChase() {
        resolveChaseButton.click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskJobChasePanel clickSubmitUpdateChase() {
        submitUpdateChase.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobChasePanel.class).get();
    }

    public HelpdeskJobChasePanel clickSubmitResolveChase() {
        submitResolveChase.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobChasePanel.class).get();
    }

    public void setUpdateChaseNotes(String notes) {
        chaseNotesToAdd.sendKeys(notes);
    }

    public void clearUpdateChaseNotes() {
        chaseNotesToAdd.clear();
    }

    public String getChaseNotes() {
        String fullChaseUpdateNotes =  chaseNotesAlreadyAdded.getText();
        int newLine = fullChaseUpdateNotes.indexOf("\n");
        String chaseUpdateNotes= fullChaseUpdateNotes.substring(0 , newLine);
        return chaseUpdateNotes;
    }

    public String getChaseNumber() {
        String chaseDetailsFull = chaseDetails.getText();
        int newLine = chaseDetailsFull.indexOf("\n");
        String chaseNumber = chaseDetailsFull.substring(0 , newLine);
        return chaseNumber;
    }

    public void setChaseStoreResolutionFeedback(String notes) {
        chaseStoreResolutionFeedback.sendKeys(notes);
    }

}
