package mercury.pageobject.web.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class ConfirmWarrantyPanel extends Base_Page<ConfirmWarrantyPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String CONFIRM_WARRANTY_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class,'warranty')]//h1[contains(text(),'Confirm Warranty')]";
    private static final String REASON_DROPDOWN_XPATH = CONFIRM_WARRANTY_PANEL_XPATH + "//..//..//form//span[contains(@class,'k-icon k-i-arrow-s')]";
    private static final String NOTES_XPATH = CONFIRM_WARRANTY_PANEL_XPATH + "//..//..//form//div//textarea[@name='Notes']";
    private static final String SAVE_BUTTON_XPATH = CONFIRM_WARRANTY_PANEL_XPATH + "//..//..//form//button[contains(text(),'Save')]";
      
    @FindBy(xpath=REASON_DROPDOWN_XPATH)
    private WebElement reasonDropdown;
    
    @FindBy(xpath=NOTES_XPATH)
    private WebElement notes;
    
    @FindBy(xpath=SAVE_BUTTON_XPATH)
    private WebElement saveButton;
    
    public ConfirmWarrantyPanel(WebDriver driver) {
        super(driver);
       
    }
    
    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            waitForElement(By.xpath(CONFIRM_WARRANTY_PANEL_XPATH), State.ELEMENT_IS_VISIBLE);
            assertTrue("Confirm Warranty panel is not loaded!", driver.findElement(By.xpath(CONFIRM_WARRANTY_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectRandomReason() {
        reasonDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }
    
    public void enterNotes(String text) {
        notes.sendKeys(text);
    }
    
    public void clickSaveButton() {
        saveButton.click();
    }
}
