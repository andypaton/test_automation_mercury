package mercury.pageobject.web.helpdesk;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class HelpdeskCancelApprovedUpliftPanel extends Base_Page<HelpdeskCancelApprovedUpliftPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String FUNDING_REQUEST_FORM_XPATH = ACTIVE_WORKSPACE_XPATH + "//form[@name='fundingRequestForm']//div[contains(@class, 'resource-action__content')]";

    private static final String CANCEL_APPROVED_UPLIFT_FORM_XPATH = FUNDING_REQUEST_FORM_XPATH + "//form[@name='upliftCancellationForm']";
    private static final String DROPDOWN_AUTHORIZED_BY_XPATH = CANCEL_APPROVED_UPLIFT_FORM_XPATH + "//ph-foreign-key[@name='authorisedBy']" + DROPDOWN_SEARCH_XPATH;
    private static final String DROPDOWN_REASON_XPATH = CANCEL_APPROVED_UPLIFT_FORM_XPATH + "//label[contains(text(), 'Reason')]/../following-sibling::span" + DROPDOWN_SEARCH_XPATH;
    private static final String NOTES_XPATH = CANCEL_APPROVED_UPLIFT_FORM_XPATH + "//label[contains(text(), 'Notes')]/following-sibling::textarea";

    //BUTTONS
    private static final String FOOTER_BUTTON_BAR_XPATH = CANCEL_APPROVED_UPLIFT_FORM_XPATH + "//div[contains(@class, 'footer-button-bar__buttons-container')]";
    private static final String SAVE_BUTTON_XPATH = FOOTER_BUTTON_BAR_XPATH + "//button[contains(text(), 'Save')]";

    @FindBy(xpath = DROPDOWN_AUTHORIZED_BY_XPATH)
    private WebElement authorizedByDropdown;

    @FindBy(xpath = DROPDOWN_REASON_XPATH)
    private WebElement reasonDropdown;

    @FindBy(xpath = NOTES_XPATH)
    private WebElement notes;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;


    public HelpdeskCancelApprovedUpliftPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try{
            waitForAngularRequestsToFinish();
            assertTrue("Funding Requests panel not loaded!", driver.findElement(By.xpath(CANCEL_APPROVED_UPLIFT_FORM_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectReason() {
        reasonDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public String getAuthorizedBy() {
        return authorizedByDropdown.getText();
    }

    public String selectRandomAuthorizedBy() {
        authorizedByDropdown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String getReason() {
        return reasonDropdown.getText();
    }

    public void enterNotes(String text) {
        notes.sendKeys(text);
    }

    public void clickSaveButton() {
        save.click();
        waitForAngularRequestsToFinish();
    }
}