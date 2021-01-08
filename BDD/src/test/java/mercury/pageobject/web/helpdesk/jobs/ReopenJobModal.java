package mercury.pageobject.web.helpdesk.jobs;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class ReopenJobModal extends Base_Page<HelpdeskFundingRequestsPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String REOPEN_JOB_MODAL_XPATH = "//body[@class='modal-open']//div[@modal-render='true']//form[@name='ReOpenJobForm']";

    private static final String REASON_XPATH = REOPEN_JOB_MODAL_XPATH + "//input[@id='reasonDropDown']/preceding-sibling::span/span[contains(@class, 'k-input')]";
    private static final String NOTES_XPATH = REOPEN_JOB_MODAL_XPATH + "//label[@for='notes']/following-sibling::textarea";
    private static final String EXISTING_RESOURCE_XPATH = REOPEN_JOB_MODAL_XPATH + "//input[@id='existingResourceDropDown']/preceding-sibling::span/span[contains(@class, 'k-input')]";

    private static final String REOPEN_BUTTON_XPATH = REOPEN_JOB_MODAL_XPATH + "//button[contains(text(), 'Re-Open')]";
    private static final String CANCEL_BUTTON_XPATH = REOPEN_JOB_MODAL_XPATH + "//button[contains(text(), 'Cancel')]";


    @FindBy(xpath = REASON_XPATH)
    private WebElement reason;

    @FindBy(xpath = NOTES_XPATH)
    private WebElement notes;

    @FindBy(xpath = EXISTING_RESOURCE_XPATH)
    private WebElement existingResource;

    @FindBy(xpath = REOPEN_BUTTON_XPATH)
    private WebElement reopen;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    public ReopenJobModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            assertTrue("Reopen Job modal not loaded!", driver.findElement(By.xpath(REOPEN_JOB_MODAL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String selectRandomReason() {
        waitForElement(By.xpath(REASON_XPATH), ELEMENT_IS_VISIBLE);
        reason.click();
        return selectRandomVisibleDropdownOption();
    }

    public void selectReason(String reopenReason) {
        waitForElement(By.xpath(REASON_XPATH), ELEMENT_IS_VISIBLE);
        reason.click();
        selectExactVisibleDropdownOption(reopenReason);
    }

    public String selectRandomExistingResource() {
        waitForElement(By.xpath(EXISTING_RESOURCE_XPATH), ELEMENT_IS_VISIBLE);
        existingResource.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public void enterNotes(String note) {
        notes.sendKeys(note);
    }

    public void reopen() {
        waitForAngularRequestsToFinish();
        reopen.click();
        waitForAngularRequestsToFinish();
        waitUntilElementNotDisplayed(By.xpath(REOPEN_JOB_MODAL_XPATH));
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
        waitUntilElementNotDisplayed(By.xpath(REOPEN_JOB_MODAL_XPATH));
    }
}
