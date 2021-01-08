package mercury.pageobject.web.helpdesk.resources;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import static org.junit.Assert.assertTrue;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskRemoveResourcePanel extends Base_Page<HelpdeskRemoveResourcePanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String REMOVE_RESOURCE_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'job-action-panel') and contains(@class, 'confirm-resource')]";

    private static final String REMOVE_RESOURCE_HEADING_XPATH = ACTIVE_WORKSPACE_XPATH + "//h1[contains(text(),'Remove from job')]";

    private static final String ORIGINAL_RESOURCE_PROFILE_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'resource-details')]/span[@ng-if='ctrlResourceAssignment.resource.resourceProfile.alias']";
    private static final String LAST_RESOURCE_PROFILE_XPATH = "(//div[contains(@class, 'resource-details')]/span[@ng-if='ctrlResourceAssignment.resource.resourceProfile.alias'])[last()]";

    private static final String RESOURCE_PROFILE_XPATH = REMOVE_RESOURCE_PANEL_XPATH + "//span[contains(text(), \"%s\")]/following-sibling::span";

    private static final String RESOURCE_ACTION_XPATH = "//div[@class='resource-action__field-container']";
    private static final String REQUESTED_BY_XPATH = ACTIVE_WORKSPACE_XPATH  + RESOURCE_ACTION_XPATH + "//input[@id='RemoveRequestedBy']";
    private static final String REASON_DROPDOWN_XPATH = REMOVE_RESOURCE_HEADING_XPATH + "/ancestor::div[contains(@class, 'resource-action__panel')]//span[contains(@class, 'k-input')]";
    private static final String NOTES_XPATH = ACTIVE_WORKSPACE_XPATH + "//textarea[@id='RemoveResourceNote']";

    private static final String RESOURCE_TO_BE_REMOVED = ACTIVE_WORKSPACE_XPATH + "//h1[contains(text(), 'Remove from job')]/ancestor::div[@class='confirmed']//span[@class='confirm-resource__resourceName']";

    //BUTTONS
    private static final String BUTTONS_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='resource-action__button-container']";
    private static final String SAVE_BUTTON_XPATH = BUTTONS_XPATH + "//button[contains(text(), 'Save')]";
    private static final String CANCEL_BUTTON_XPATH = BUTTONS_XPATH + "//button[contains(text(), 'Cancel')]";

    @FindBy(xpath = ORIGINAL_RESOURCE_PROFILE_XPATH)
    private WebElement originalResourceProfile;

    @FindBy(xpath = LAST_RESOURCE_PROFILE_XPATH)
    private WebElement lastResourceProfile;

    @FindBy(xpath = REQUESTED_BY_XPATH)
    private WebElement requestedBy;

    @FindBy(xpath = REASON_DROPDOWN_XPATH)
    private WebElement reasonDropdown;

    @FindBy(xpath = NOTES_XPATH)
    private WebElement notes;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = RESOURCE_TO_BE_REMOVED)
    WebElement resourceToBeRemoved;

    public HelpdeskRemoveResourcePanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            assertTrue("Remove Resource panel not loaded!", driver.findElement(By.xpath(REMOVE_RESOURCE_HEADING_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getResourceNameToBeRemoved() {
        return resourceToBeRemoved.getText();
    }


    public String getOriginalResourceProfile() {
        return originalResourceProfile.getText();
    }

    public String getLastResourceProfile() {
        return lastResourceProfile.getText();
    }

    public String getResourceProfile(String resourceName) {
        WebElement resourceProfile = driver.findElement(By.xpath(String.format(RESOURCE_PROFILE_XPATH, resourceName)));
        return resourceProfile.getText();
    }

    public void setRequestedBy(String text) {
        waitForLoadingToComplete();
        waitForElement(By.xpath(REQUESTED_BY_XPATH), ELEMENT_IS_VISIBLE);
        //        POHelper.sendKeys(requestedBy, text, 3);
        POHelper.sendKeys(requestedBy, text);
    }

    public String getRequestedBy() {
        waitForElement(By.xpath(REQUESTED_BY_XPATH), ELEMENT_IS_VISIBLE);
        return requestedBy.getText();
    }
    public void setNotes(String text) {
        notes.sendKeys(text);
    }

    public String getNotes() {
        return notes.getText();
    }

    public void selectRandomReason() {
        reasonDropdown.click();
        waitForKendoLoadingToComplete();
        selectRandomVisibleDropdownOption();
    }

    public HelpdeskManageResourcesPanel save() {
        waitForElement(By.xpath(SAVE_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        saveButton.click();
        return PageFactory.initElements(driver, HelpdeskManageResourcesPanel.class).get();
    }

    public void selectExactReason(String reason){
        reasonDropdown.click();
        waitForAngularRequestsToFinish();
        selectExactVisibleDropdownOption(reason);
    }

    public HelpdeskManageResourcesPanel cancel() {
        waitForElement(By.xpath(CANCEL_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        cancelButton.click();
        return PageFactory.initElements(driver, HelpdeskManageResourcesPanel.class).get();
    }
}
