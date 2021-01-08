package mercury.pageobject.web.helpdesk.jobs;


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

public class HelpdeskCancelJobChasePanel extends Base_Page<HelpdeskCancelJobChasePanel> {

    public HelpdeskCancelJobChasePanel(WebDriver driver) {
        super(driver);
    }

    private static final Logger logger = LogManager.getLogger();

    // Workspace, panel and form
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String CHASE_PANEL_CONTENT_XPATH = ACTIVE_WORKSPACE_XPATH +  "//div[contains(@class,'job-action-panel') and contains(@class,'job-chase')]//div[@class='job-action-panel__content']";
    private static final String CANCEL_CHASE_FORM_XPATH = CHASE_PANEL_CONTENT_XPATH + "//form[@id='chaseFormUpdate']";
    private static final String CANCEL_CHASE_FORM_DETAILS_XPATH = CANCEL_CHASE_FORM_XPATH + "//div[contains(@class,'job-chase__details')]";
    private static final String CANCEL_CHASE_FORM_FOOTER_XPATH = CANCEL_CHASE_FORM_DETAILS_XPATH + "//div[contains(@class,'footer-button-bar--wide')]";
    private static final String CHASE_FORM_DETAILS_XPATH = CANCEL_CHASE_FORM_XPATH + "//div[contains(@class,'job-chase__details')]";
    //Buttons
    private static final String CHASE_PANEL_SUBMIT_CANCEL_CHASE_BUTTON_XPATH = CANCEL_CHASE_FORM_FOOTER_XPATH + "//button[contains(@class,'btn') and contains(@class,'btn-destructive') and contains(@class,'btn-primary')and contains(text(), 'Cancel Chase')]";

    // Core
    private static final String CANCEL_REASON_DROPDOWN_ARROW_XPATH = CHASE_PANEL_CONTENT_XPATH + "//label[contains(text(), 'Cancellation reason')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String CHASE_CANCEL_NOTES_XPATH = CHASE_PANEL_CONTENT_XPATH + "//textarea[@id='cancelNotes']";


    @FindBy(xpath = CHASE_PANEL_SUBMIT_CANCEL_CHASE_BUTTON_XPATH)
    private WebElement submitCancelChase;

    @FindBy(xpath = CHASE_CANCEL_NOTES_XPATH)
    private WebElement chaseCancelNotes;

    @FindBy(xpath = CANCEL_REASON_DROPDOWN_ARROW_XPATH)
    private WebElement cancellationReasonDropdown;

    @FindBy(xpath = CHASE_FORM_DETAILS_XPATH)
    private WebElement chaseDetails;


    @Override
    protected void isLoaded() throws Error {
        try{
            assertTrue("Chase panel not loaded!", driver.findElement(By.xpath(CANCEL_CHASE_FORM_DETAILS_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public HelpdeskJobChasePanel clickSubmitCancelChase() {
        submitCancelChase.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobChasePanel.class).get();
    }

    public void setCancelChaseNotes(String notes) {
        chaseCancelNotes.sendKeys(notes);
    }

    public void setCancellationReason(String reason) {
        cancellationReasonDropdown.click();
        waitForAngularRequestsToFinish();
        if (!getVisibleDropdownOptions().isEmpty()) {
            selectExactVisibleDropdownOption(reason);
        }
    }

    public String getChaseNumber() {
        String chaseDetailsFull = chaseDetails.getText();
        int newLine = chaseDetailsFull.indexOf("\n");
        String chaseNumber = chaseDetailsFull.substring(0 , newLine);
        return chaseNumber;
    }
}
