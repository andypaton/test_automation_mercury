package mercury.pageobject.web.storeportal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class StorePortalLeaveFeedbackModal extends Base_Page<StorePortalLeaveFeedbackModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String LEAVE_FEEDBACK_MODAL_XPATH = "//div[@class='cdk-overlay-pane']";
    private static final String LEAVE_FEEDBACK_MODAL_CONTENT_XPATH = LEAVE_FEEDBACK_MODAL_XPATH + "//mat-dialog-container[contains(@class, 'mat-dialog-container')]";

    private static final String HAPPY_FEEDBACK_XPATH = LEAVE_FEEDBACK_MODAL_CONTENT_XPATH + "//div[contains(text(), 'HAPPY')]";
    private static final String SAD_FEEDBACK_XPATH = LEAVE_FEEDBACK_MODAL_CONTENT_XPATH + "//div[contains(text(), 'SAD')]";
    private static final String NAME_XPATH = LEAVE_FEEDBACK_MODAL_CONTENT_XPATH + "//input[@name='Name']";
    private static final String ADDITIONAL_INFORMATION_XPATH = LEAVE_FEEDBACK_MODAL_CONTENT_XPATH + "//textarea[@name='Additional information']";
    private static final String ADDITIONAL_FEEDBACK_XPATH = LEAVE_FEEDBACK_MODAL_CONTENT_XPATH + "//textarea[@name='Additional feedback']";
    private static final String CANCEL_XPATH = LEAVE_FEEDBACK_MODAL_CONTENT_XPATH + "//button[@name='cancelFeedback']";
    private static final String SEND_XPATH = LEAVE_FEEDBACK_MODAL_CONTENT_XPATH + "//button[@name='submitFeedback']";

    private static final String FEEDBACK_SUBMITTED_POPUP_BUTTON_XPATH = "//button/span[text()='OK']";


    @FindBy(xpath = HAPPY_FEEDBACK_XPATH)
    private WebElement happyFeedback;

    @FindBy(xpath = SAD_FEEDBACK_XPATH)
    private WebElement sadFeedback;

    @FindBy(xpath = NAME_XPATH)
    private WebElement nameTextBox;

    @FindBy(xpath = ADDITIONAL_INFORMATION_XPATH)
    private WebElement additionalInformationTextBox;

    @FindBy(xpath = ADDITIONAL_FEEDBACK_XPATH)
    private WebElement additionalFeedbackTextBox;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = SEND_XPATH)
    private WebElement sendButton;

    @FindBy(xpath = FEEDBACK_SUBMITTED_POPUP_BUTTON_XPATH)
    private WebElement okPopupButton;


    public StorePortalLeaveFeedbackModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(LEAVE_FEEDBACK_MODAL_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void clickHappyFeedback() {
        happyFeedback.click();
        POHelper.waitWhileBusy();
    }

    public void clickSadFeedback() {
        sadFeedback.click();
        POHelper.waitWhileBusy();
    }

    public void enterName(String name) {
        nameTextBox.sendKeys(name);
    }

    public void enterAdditionalInformation(String additionalInformation) {
        additionalInformationTextBox.sendKeys(additionalInformation);
    }

    public void enterAdditionalFeedback(String additionalInformation) {
        additionalFeedbackTextBox.sendKeys(additionalInformation);
    }

    public void clickCancelButton() {
        cancelButton.click();
        POHelper.waitWhileBusy();
    }

    public void clickSendButton() {
        POHelper.scrollToElement(sendButton);
        sendButton.click();
        POHelper.waitWhileBusy();
    }

    public Boolean isFeedbackPopupDisplayed() {
        return this.isElementPresent(By.xpath(FEEDBACK_SUBMITTED_POPUP_BUTTON_XPATH));
    }

    public void clickOkOnPopup() {
        okPopupButton.click();
        POHelper.waitWhileBusy();
    }

}
