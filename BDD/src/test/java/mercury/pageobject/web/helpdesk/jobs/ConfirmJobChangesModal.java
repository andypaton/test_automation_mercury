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

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class ConfirmJobChangesModal extends Base_Page<ConfirmJobChangesModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String CONFIRM_JOB_CHANGES_MODAL_XPATH = "//body[@class='modal-open']//div[@modal-render='true']//div[contains(@class, 'job-edit__reason-modal')]/..";

    private static final String REASON_XPATH = CONFIRM_JOB_CHANGES_MODAL_XPATH + "//div[text()='Please enter a reason for these changes:']/following-sibling::div//input";
    private static final String CHANGES_XPATH = CONFIRM_JOB_CHANGES_MODAL_XPATH + "//div[text()='The following changes will be made:']/following-sibling::div//li";

    private static final String BACK_BUTTON_XPATH = CONFIRM_JOB_CHANGES_MODAL_XPATH + "//button[contains(text(), 'Back')]";
    private static final String CONFIRM_BUTTON_XPATH = CONFIRM_JOB_CHANGES_MODAL_XPATH + "//button[contains(text(), 'Confirm')]";

    @FindBy(xpath=CONFIRM_JOB_CHANGES_MODAL_XPATH)
    private WebElement page;

    @FindBy(xpath = REASON_XPATH)
    private WebElement reason;

    @FindBy(xpath = CHANGES_XPATH)
    private WebElement changes;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement back;

    @FindBy(xpath = CONFIRM_BUTTON_XPATH)
    private WebElement confirm;

    public ConfirmJobChangesModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            waitForElement(By.xpath(CONFIRM_JOB_CHANGES_MODAL_XPATH), State.ELEMENT_IS_VISIBLE);
            assertTrue("Confirm Job Changes modal not loaded!", driver.findElement(By.xpath(CONFIRM_JOB_CHANGES_MODAL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isDisplayed() {
        return page.isDisplayed();
    }

    public void enterReason(String desc) {
        reason.sendKeys(desc);
    }

    public String getChanges() {
        return changes.getText();
    }

    public HelpdeskLogJobPage back() {
        back.click();
        waitForAngularRequestsToFinish();
        waitUntilElementNotDisplayed(By.xpath(CONFIRM_JOB_CHANGES_MODAL_XPATH));
        return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
    }

    public HelpdeskJobPage confirm() {
        confirm.click();
        waitForAngularRequestsToFinish();
        waitUntilElementNotDisplayed(By.xpath(CONFIRM_JOB_CHANGES_MODAL_XPATH));
        return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
    }

    public boolean isBackButtonDisplayed() {
        return back.isDisplayed();
    }

    public boolean isConfirmButtonDisplayed() {
        return confirm.isDisplayed();
    }
}
