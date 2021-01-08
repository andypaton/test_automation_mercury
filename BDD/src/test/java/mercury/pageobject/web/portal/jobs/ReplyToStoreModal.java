package mercury.pageobject.web.portal.jobs;

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


public class ReplyToStoreModal extends Base_Page<ReplyToStoreModal>{

    private static final Logger logger = LogManager.getLogger();

    // Page Title
    private static final String PAGE_TITLE = "Reply to Store";

    // Modal
    private static final String MODAL_XPATH = "//div[@id='replyToStoreDialog']";
    private static final String MODAL_CONTENT_XPATH = MODAL_XPATH + "//div[@class='modal-content']";
    private static final String MODAL_BODY_XPATH = MODAL_CONTENT_XPATH + "//div[@class='modal-body']";
    private static final String MODAL_HEADER_XPATH = MODAL_BODY_XPATH + "//h3[contains(text(), '" + PAGE_TITLE + "')]";

    // Form
    private static final String REPLY_TO_STORE_FORM_XPATH = MODAL_BODY_XPATH + "//form";
    private static final String FORM_GROUP_XPATH = REPLY_TO_STORE_FORM_XPATH + "//div[@class='form-group']";
    private static final String RESPONSE_TEXT_XPATH = FORM_GROUP_XPATH + "//textarea[@id='responseText']";

    // Form Buttons bar
    private static final String FORM_GROUP_BUTTONS_BAR_XPATH = REPLY_TO_STORE_FORM_XPATH + "//div[@class='form-group button-bar']";
    private static final String CLOSE_BUTTON_XPATH = FORM_GROUP_BUTTONS_BAR_XPATH + "//button//span[contains(text(), 'Close')]";
    private static final String SEND_BUTTON_XPATH = FORM_GROUP_BUTTONS_BAR_XPATH + "//button[contains(text(), 'Send')]";

    // Web Elements
    @FindBy(xpath = RESPONSE_TEXT_XPATH) private WebElement responseText;

    @FindBy(xpath = SEND_BUTTON_XPATH) private WebElement send;

    @FindBy(xpath = CLOSE_BUTTON_XPATH) private WebElement close;

    // Methods
    public ReplyToStoreModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try{
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Modal is not displayed", driver.findElement(By.xpath(MODAL_HEADER_XPATH)).isDisplayed());
            logger.info(PAGE_TITLE + " Page loaded");
        }catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public void enterResponseText(String text) {
        responseText.sendKeys(text);
        waitForAngularRequestsToFinish();
    }

    public Boolean isSendButtonEnabled() {
        return isElementClickable(By.xpath(SEND_BUTTON_XPATH));
    }

    public FeedbackResponsePage send() {
        send.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, FeedbackResponsePage.class).get();
    }

    public FeedbackResponsePage close() {
        close.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, FeedbackResponsePage.class).get();
    }

}
