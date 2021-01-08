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


public class FeedbackDetailsModal extends Base_Page<FeedbackDetailsModal>{

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Feedback Details";

    private static final String MODAL_XPATH = "//div[@class='modal-dialog']";
    private static final String MODAL_CONTENT_XPATH = MODAL_XPATH + "//div[@class='modal-content']";
    private static final String MODAL_BODY_XPATH = MODAL_CONTENT_XPATH + "//div[@class='modal-body']";
    private static final String MODAL_HEADER_XPATH = MODAL_BODY_XPATH + "//h3[contains(text(), '" + PAGE_TITLE + "')]";

    private static final String SUPPORTING_MESSAGE_XPATH = MODAL_BODY_XPATH + "//label[contains(text(), 'Supporting message')]/following-sibling::p";

    private static final String CLOSE_BUTTON_XPATH = MODAL_BODY_XPATH + "//button//span[contains(text(), 'Close')]";

    @FindBy(xpath = SUPPORTING_MESSAGE_XPATH) private WebElement supportingMessage;

    @FindBy(xpath = CLOSE_BUTTON_XPATH) private WebElement close;

    public FeedbackDetailsModal(WebDriver driver) {
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

    public String getSupportingMessage() {
        waitForAngularRequestsToFinish();
        return supportingMessage.getText();
    }

    public FeedbackResponsePage closeFeedbackDetails() {
        close.click();
        return PageFactory.initElements(driver, FeedbackResponsePage.class).get();
    }


}
