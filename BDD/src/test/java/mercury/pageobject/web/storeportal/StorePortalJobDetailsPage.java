package mercury.pageobject.web.storeportal;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

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

public class StorePortalJobDetailsPage extends Base_Page<StorePortalJobDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Mercury Store Portal";

    private static final String PAGE_HEADER_XPATH = "//div[@class='header']";
    private static final String PAGE_BODY_XPATH = "//div[@class='main']";

    private static final String MENU_ITEM_XPATH = PAGE_HEADER_XPATH + "//a[contains(text(), '%s')]";

    private static final String JOB_REFERENCE_XPATH = PAGE_BODY_XPATH + "//div[@class='job-title']";
    private static final String JOB_DETAILS_XPATH = PAGE_BODY_XPATH + "//span[contains(text(), '%s')]/following-sibling::span";

    private static final String WATCH_OR_UNWATCH_JOB_XPATH = PAGE_BODY_XPATH + "//span[normalize-space(text())='%s']";

    private static final String JOB_PROGRESS_STATUS_XPATH = PAGE_BODY_XPATH + "//span[@class='progress-status-highlight']//div";

    private static final String CONTACT_US_XPATH = PAGE_BODY_XPATH + "//div[contains(@class, 'job-contact-us-button')]";
    private static final String VIEW_DETAILS_XPATH = PAGE_BODY_XPATH + "//span[contains(@class, 'mat-expansion-indicator')]";
    private static final String SUBMITTED_BY_TOGGLE_OFF_XPATH = PAGE_BODY_XPATH + "//div[contains(text(), 'Submitted by')]/following-sibling::div[1]";
    private static final String CONTACT_NUMBER_XPATH = PAGE_BODY_XPATH + "//div[contains(text(), 'Contact number')]/following-sibling::div";
    private static final String DESCRIPTION_TOGGLE_OFF_XPATH = PAGE_BODY_XPATH + "//div[@class='contact-message']";

    private static final String DESCRIPTION_TOGGLE_ON_XPATH = PAGE_BODY_XPATH + "//h2[contains(text(), 'Your feedback')]/following-sibling::p[1]";
    private static final String SUBMITTED_BY_TOGGLE_ON_XPATH = PAGE_BODY_XPATH + "//span[contains(text(), 'Submitted')]/following-sibling::span[2]";

    private static final String LEAVE_FEEDBACK_TOGGLE_OFF_XPATH = PAGE_BODY_XPATH + "//div[contains(text(), 'Leave Feedback')]";
    private static final String LEAVE_FEEDBACK_TOGGLE_ON_XPATH = PAGE_BODY_XPATH + "//button[@class='cpl-btn cpl-btn-normal']";

    @FindBy(xpath = JOB_REFERENCE_XPATH)
    private WebElement jobReference;

    @FindBy(xpath = JOB_PROGRESS_STATUS_XPATH)
    private WebElement jobProgressStatus;

    @FindBy(xpath = CONTACT_US_XPATH)
    private WebElement contactUs;

    @FindBy(xpath = VIEW_DETAILS_XPATH)
    private WebElement viewDetails;

    @FindBy(xpath = SUBMITTED_BY_TOGGLE_ON_XPATH)
    private WebElement submittedByToggleOn;

    @FindBy(xpath = SUBMITTED_BY_TOGGLE_OFF_XPATH)
    private WebElement submittedByToggleOff;

    @FindBy(xpath = CONTACT_NUMBER_XPATH)
    private WebElement contactNumber;

    @FindBy(xpath = DESCRIPTION_TOGGLE_OFF_XPATH)
    private WebElement descriptionToggleOff;

    @FindBy(xpath = DESCRIPTION_TOGGLE_ON_XPATH)
    private WebElement descriptionToggleOn;

    @FindBy(xpath = LEAVE_FEEDBACK_TOGGLE_OFF_XPATH)
    private WebElement leaveFeedbackToggleOff;

    @FindBy(xpath = LEAVE_FEEDBACK_TOGGLE_ON_XPATH)
    private WebElement leaveFeedbackToggleOn;


    public StorePortalJobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.waitWhileBusy();
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_BODY_XPATH));
            logger.info(PAGE_TITLE + ": Page loaded");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + ": Page failed to load");
            throw new AssertionError();
        }
    }

    public String getJobReference() {
        POHelper.scrollToElement(jobReference);
        waitForElement(By.xpath(JOB_REFERENCE_XPATH), ELEMENT_IS_VISIBLE);
        return jobReference.getText();
    }

    public String getJobProgressStatus() {
        return jobProgressStatus.getText();
    }

    public String getJobDetail(String jobDetail) {
        WebElement detail = driver.findElement(By.xpath(String.format(JOB_DETAILS_XPATH, jobDetail)));
        return detail.getText();
    }

    public StorePortalLeaveFeedbackModal clickLeaveFeedbackButtonToggleOff() {
        leaveFeedbackToggleOff.click();
        POHelper.waitWhileBusy();
        return PageFactory.initElements(driver, StorePortalLeaveFeedbackModal.class).get();
    }

    public StorePortalLeaveFeedbackModal clickLeaveFeedbackButtonToggleOn() {
        waitForElement(By.xpath(LEAVE_FEEDBACK_TOGGLE_ON_XPATH), ELEMENT_IS_CLICKABLE);
        leaveFeedbackToggleOn.click();
        POHelper.waitWhileBusy();
        return PageFactory.initElements(driver, StorePortalLeaveFeedbackModal.class).get();
    }

    public StorePortalContactUsModal clickContactUsButton() {
        contactUs.click();
        POHelper.waitWhileBusy();
        return PageFactory.initElements(driver, StorePortalContactUsModal.class).get();
    }

    public void viewDetails() {
        POHelper.scrollToElement(viewDetails);
        viewDetails.click();
        POHelper.waitWhileBusy();
    }

    public String getSubmittedByToggleOff() {
        return submittedByToggleOff.getText();
    }

    public String getSubmittedByToggleOn() {
        return submittedByToggleOn.getText();
    }

    public String getContactNumber() {
        return contactNumber.getText();
    }

    public String getDescriptionToggleOff() {
        return descriptionToggleOff.getText();
    }

    public String getDescriptionToggleOn() {
        return descriptionToggleOn.getText();
    }

    public void watchOrUnwatchJob(String watchUnwatch) {
        WebElement button = driver.findElement(By.xpath(String.format(WATCH_OR_UNWATCH_JOB_XPATH, watchUnwatch)));
        button.click();
        POHelper.waitWhileBusy();
    }

    public void selectMenuItem(String menuItem) {
        WebElement button = driver.findElement(By.xpath(String.format(MENU_ITEM_XPATH, menuItem)));
        button.click();
        POHelper.waitWhileBusy();
    }

}
