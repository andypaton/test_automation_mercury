package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class AddOrderLinesToInvoiceConfirmationModal extends Base_Page<AddOrderLinesToInvoiceConfirmationModal> {
    private static final Logger logger = LogManager.getLogger();

    private static final String CONFIRMATION_MODAL_CONTENT_CONTAINER_XPATH = "//div[@class='modal-content']";
    private static final String CONFIRMATION_MODAL_HEADER_XPATH = CONFIRMATION_MODAL_CONTENT_CONTAINER_XPATH + "//div[@class='modal-header']";
    private static final String CONFIRMATION_MODAL_BODY_XPATH = CONFIRMATION_MODAL_CONTENT_CONTAINER_XPATH + "//div[@class='modal-body info']";
    private static final String CONFIRMATION_MODAL_FOOTER_XPATH = CONFIRMATION_MODAL_CONTENT_CONTAINER_XPATH + "//div[@class='modal-footer']";


    private static final String CONFIRMATION_MODAL_HEADER_TITLE_XPATH = CONFIRMATION_MODAL_HEADER_XPATH + "//h4[text()='Confirmation']";

    //buttons
    private static final String CONFIRMATION_MODAL_NO_BUTTON_XPATH = CONFIRMATION_MODAL_FOOTER_XPATH + "//button[@id='btnNo']";
    private static final String CONFIRMATION_MODAL_YES_BUTTON_XPATH = CONFIRMATION_MODAL_FOOTER_XPATH + "//button[@id='btnYes']";

    @FindBy(xpath = CONFIRMATION_MODAL_YES_BUTTON_XPATH)
    private WebElement yesButton;

    @FindBy(xpath = CONFIRMATION_MODAL_NO_BUTTON_XPATH)
    private WebElement noButton;

    @FindBy(xpath = CONFIRMATION_MODAL_BODY_XPATH)
    private WebElement confirmationMessage;

    public AddOrderLinesToInvoiceConfirmationModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(CONFIRMATION_MODAL_HEADER_TITLE_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(CONFIRMATION_MODAL_HEADER_TITLE_XPATH + " Load");
    }

    public void clickYes() {
        yesButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickNo() {
        noButton.click();
        waitForAngularRequestsToFinish();
    }

    public String getConfirmationMessage() {
        return confirmationMessage.getText();
    }



}
