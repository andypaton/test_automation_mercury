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

import mercury.pageobject.web.Base_Page;

public class HelpdeskAdviseRemovalPanel extends Base_Page<HelpdeskAdviseRemovalPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String ADVISE_REMOVAL_HEADING_XPATH = "//h4[contains(text(),'Advise removal')]";

    //ADVISE REMOVAL FIELDS
    private static final String REMOVAL_CONFIRMED_RADIOBUTTON_XPATH = "//div[@class='yesNoRadio']//label[contains(text(),'%s')]";
    private static final String REASON_DROPDOWN_XPATH = ACTIVE_WORKSPACE_XPATH + "//span[contains(@class, 'k-input')]";
    private static final String NOTES_XPATH = ACTIVE_WORKSPACE_XPATH + "//textarea[@id='AdviseRemoveResourceNote']";

    //BUTTONS
    private static final String BUTTONS_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='resource-action__button-container']";
    private static final String SAVE_BUTTON_XPATH = BUTTONS_XPATH + "//button[contains(text(), 'Save')]";
    private static final String CANCEL_BUTTON_XPATH = BUTTONS_XPATH + "//button[contains(text(), 'Cancel')]";

    @FindBy(xpath = ADVISE_REMOVAL_HEADING_XPATH)
    private WebElement header;

    @FindBy(xpath = REASON_DROPDOWN_XPATH)
    private WebElement reasonDropdown;

    @FindBy(xpath = NOTES_XPATH)
    private WebElement notes;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    public HelpdeskAdviseRemovalPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            assertTrue("Advise Removal panel not loaded!", driver.findElement(By.xpath(ADVISE_REMOVAL_HEADING_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Boolean isPageDisplayed() {
        return header.isDisplayed();
    }

    public void selectRemovalConfirmed(String option) {
        WebElement action = waitForElement(By.xpath(String.format(REMOVAL_CONFIRMED_RADIOBUTTON_XPATH, option)), ELEMENT_IS_VISIBLE);
        action.click();
    }

    public void setNotes(String note) {
        notes.sendKeys(note);
    }

    public void selectRandomReason() {
        waitForElement(By.xpath(REASON_DROPDOWN_XPATH), ELEMENT_IS_CLICKABLE);
        reasonDropdown.click();
        waitForKendoLoadingToComplete();
        selectRandomVisibleDropdownOption();
    }

    public HelpdeskManageResourcesPanel save() {
        waitForElement(By.xpath(SAVE_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        saveButton.click();
        return PageFactory.initElements(driver, HelpdeskManageResourcesPanel.class).get();
    }

}
