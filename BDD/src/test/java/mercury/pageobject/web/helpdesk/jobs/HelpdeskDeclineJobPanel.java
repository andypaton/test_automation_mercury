package mercury.pageobject.web.helpdesk.jobs;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAdditionalResourcesRequiredPanel;

public class HelpdeskDeclineJobPanel extends Base_Page<HelpdeskDeclineJobPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String RESOURCE_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'job-action-panel') and contains(@class, 'confirm-resource')]";
    private static final String RESOURCE_PANEL_CONTENT_XPATH = RESOURCE_PANEL_XPATH + "//div[contains(@class, 'job-action-panel__content')]";

    private static final String DECLINE_JOB_PANEL_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[contains(@class, 'resource-action__panel')]";
    private static final String DECLINE_JOB_PANEL_CONTENT_XPATH = DECLINE_JOB_PANEL_XPATH + "//div[contains(@class, 'resource-action__content')]";
    private static final String DECLINE_JOB_PANEL_CONTENT_CONTAINER_XPATH = DECLINE_JOB_PANEL_CONTENT_XPATH + "//div[@class='resource-action__field-container']";

    private static final String DROPDOWN_REASON_ARROW_XPATH = DECLINE_JOB_PANEL_CONTENT_CONTAINER_XPATH + "//label[contains(text(), 'Reason')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String DROPDOWN_REASON_XPATH = DECLINE_JOB_PANEL_CONTENT_CONTAINER_XPATH + "//label[contains(text(), 'Reason')]/.." + DROPDOWN_SEARCH_XPATH;
    private static final String NOTES_XPATH = DECLINE_JOB_PANEL_CONTENT_CONTAINER_XPATH + "//label[contains(text(), 'Notes')]/following-sibling::textarea";

    private static final String DECLINE_JOB_BUTTON_CONTAINER_XPATH = DECLINE_JOB_PANEL_CONTENT_XPATH + "//div[@class='resource-action__button-container']";
    private static final String SAVE_BUTTON_XPATH = DECLINE_JOB_BUTTON_CONTAINER_XPATH + "//button[contains(text(), 'Save')]";

    @FindBy(xpath = DROPDOWN_REASON_ARROW_XPATH)
    private WebElement reasonDropdownArrow;

    @FindBy(xpath = DROPDOWN_REASON_XPATH)
    private WebElement reasonDropdown;

    @FindBy(xpath = NOTES_XPATH)
    private WebElement notes;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    public HelpdeskDeclineJobPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(DECLINE_JOB_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectRandomReason() {
        waitForElement(By.xpath(DROPDOWN_REASON_ARROW_XPATH), ELEMENT_IS_CLICKABLE);
        reasonDropdownArrow.click();
        selectRandomVisibleDropdownOption();
    }

    public String getReason() {
        waitForElement(By.xpath(DROPDOWN_REASON_XPATH), ELEMENT_IS_CLICKABLE);
        return reasonDropdown.getText();
    }

    public void setNotes(String text) {
        notes.sendKeys(text);
    }

    public String getNotes() {
        return notes.getAttribute("value");
    }

    public HelpdeskAdditionalResourcesRequiredPanel save() {
        waitForElement(By.xpath(SAVE_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        saveButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAdditionalResourcesRequiredPanel.class).get();
    }
}
