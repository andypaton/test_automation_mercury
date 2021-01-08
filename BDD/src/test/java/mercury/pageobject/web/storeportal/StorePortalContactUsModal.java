package mercury.pageobject.web.storeportal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class StorePortalContactUsModal extends Base_Page<StorePortalContactUsModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String CONTACT_US_MODAL_XPATH = "//div[@class='cdk-overlay-pane']";
    private static final String CONTACT_US_MODAL_CONTENT_XPATH = CONTACT_US_MODAL_XPATH + "//mat-dialog-container[contains(@class, 'mat-dialog-container')]";

    private static final String NAME_XPATH = CONTACT_US_MODAL_CONTENT_XPATH + "//input[@name='Name']";
    private static final String CONTACT_NUMBER_XPATH = CONTACT_US_MODAL_CONTENT_XPATH + "//input[@name='Contact number']";
    private static final String RADIO_YES_XPATH = "(//label[@class='mat-radio-label'])[1]";
    private static final String RADIO_NO_XPATH = "(//label[@class='mat-radio-label'])[2]";
    private static final String MESSAGE_XPATH = CONTACT_US_MODAL_CONTENT_XPATH + "//textarea[@name='Message']";
    private static final String CANCEL_XPATH = CONTACT_US_MODAL_CONTENT_XPATH + "//button[@name='cancelChase']";
    private static final String SEND_XPATH = CONTACT_US_MODAL_CONTENT_XPATH + "//button[@name='submitChase']";


    @FindBy(xpath = NAME_XPATH)
    private WebElement nameTextBox;

    @FindBy(xpath = CONTACT_NUMBER_XPATH)
    private WebElement contactNumberTextBox;

    @FindBy(xpath = RADIO_YES_XPATH)
    private WebElement radioYesButton;

    @FindBy(xpath = RADIO_NO_XPATH)
    private WebElement radioNoButton;

    @FindBy(xpath = MESSAGE_XPATH)
    private WebElement messageTextBox;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = SEND_XPATH)
    private WebElement sendButton;


    public StorePortalContactUsModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(CONTACT_US_MODAL_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterName(String name) {
        nameTextBox.sendKeys(name);
    }

    public void enterContactNumber(String contactNumber) {
        contactNumberTextBox.sendKeys(contactNumber);
    }

    public void clickRadioYesButton() {
        POHelper.scrollToElement(radioYesButton);
        radioYesButton.click();
        POHelper.waitWhileBusy();
    }

    public void clickRadioNoButton() {
        POHelper.scrollToElement(radioNoButton);
        radioNoButton.click();
        POHelper.waitWhileBusy();
    }

    public void enterMessage(String message) {
        messageTextBox.sendKeys(message);
    }

    public void clickCancelButton() {
        cancelButton.click();
        POHelper.waitWhileBusy();
    }

    public void clickSendButton() {
        sendButton.click();
        POHelper.waitWhileBusy();
    }

}
