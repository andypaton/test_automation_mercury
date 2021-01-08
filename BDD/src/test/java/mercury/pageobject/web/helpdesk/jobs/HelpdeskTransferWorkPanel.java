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


public class HelpdeskTransferWorkPanel extends Base_Page<HelpdeskTransferWorkPanel> {

    private static final Logger logger = LogManager.getLogger();

    // Page Title
    private static final String PAGE_TITLE = "Transfer Work";

    // Active Workspace
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    // Resource Action Panel
    private static final String RESOURCE_ACTION_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'resource-action__panel')]";
    private static final String RESOURCE_ACTION_PANEL_HEADER_XPATH = RESOURCE_ACTION_PANEL_XPATH + "//div[contains(@class, 'resource-action__header')]";
    private static final String TRANSFER_WORK_PANEL_TITLE_XPATH = RESOURCE_ACTION_PANEL_HEADER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";

    // Transfer Work Form
    private static final String TRANSFER_WORK_FORM_XPATH = RESOURCE_ACTION_PANEL_XPATH + "//form[@name='TransferResourceForm']";

    // Form Elements
    private static final String ADDITIONAL_RESOURCE_DROPDOWN_XPATH = TRANSFER_WORK_FORM_XPATH + "//label[contains(text(), 'Additional Resource')]/.." + DROPDOWN_SEARCH_XPATH;
    private static final String REASON_DROPDOWN_XPATH = TRANSFER_WORK_FORM_XPATH + "//label[contains(text(), 'Reason')]/.." + DROPDOWN_SEARCH_XPATH;
    private static final String NOTES_XPATH = TRANSFER_WORK_FORM_XPATH + "//textarea";

    // Button Container
    private static final String BUTTON_CONTAINER_XPATH = TRANSFER_WORK_FORM_XPATH + "//div[@class='resource-action__button-container']";
    private static final String SAVE_BUTTON_XPATH = BUTTON_CONTAINER_XPATH + "//button[@type='submit']";
    private static final String CANCEL_BUTTON_XPATH = BUTTON_CONTAINER_XPATH + "//button[contains(text(), 'Cancel')]";


    @FindBy(xpath = ADDITIONAL_RESOURCE_DROPDOWN_XPATH) private WebElement additionalResourceDropdown;

    @FindBy(xpath = REASON_DROPDOWN_XPATH) private WebElement reasonDropdown;

    @FindBy(xpath = NOTES_XPATH) private WebElement notes;

    @FindBy(xpath = SAVE_BUTTON_XPATH) private WebElement save;

    @FindBy(xpath = CANCEL_BUTTON_XPATH) private WebElement cancel;



    public HelpdeskTransferWorkPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try{
            waitForAngularRequestsToFinish();
            assertTrue("Transfer work panel not loaded!", driver.findElement(By.xpath(TRANSFER_WORK_PANEL_TITLE_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void searchAndSelectAdditionalResource(String additionalResource) {
        additionalResourceDropdown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectExactVisibleDropdownOptionWithoutRemovingBraces(additionalResource);
        waitForAngularRequestsToFinish();
    }

    public void selectReason() {
        reasonDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
        waitForAngularRequestsToFinish();
    }

    public String getSelectedReason() {
        return reasonDropdown.getText();
    }

    public void enterNotes(String text) {
        notes.clear();
        notes.sendKeys(text);
    }

    public HelpdeskJobPage clickSaveButton() {
        save.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
    }
}
