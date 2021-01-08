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

public class EditInvoiceLineConfirmationModal extends Base_Page<EditInvoiceLineConfirmationModal>{
    private static final Logger logger = LogManager.getLogger();

    private static final String CONFIRMATION_MODAL_CONTENT_CONTAINER_XPATH = "//div[@class='modal-content']";
    private static final String CONFIRMATION_MODAL_HEADER_XPATH = CONFIRMATION_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class, 'modal-header')]";
    private static final String CONFIRMATION_MODAL_BODY_XPATH = CONFIRMATION_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class, 'modal-body info')]";
    private static final String CONFIRMATION_MODAL_FOOTER_XPATH = CONFIRMATION_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class, 'modal-footer')]";

    private static final String CONFIRMATION_MODAL_HEADER_TITLE_XPATH = CONFIRMATION_MODAL_HEADER_XPATH + "//h3[contains(text(),'Confirmation')]";

    //buttons
    private static final String CONFIRMATION_MODAL_CONFIRM_BUTTON_XPATH = CONFIRMATION_MODAL_FOOTER_XPATH + "//button[text()='Confirm']";
    private static final String CONFIRMATION_MODAL_CANCEL_BUTTON_XPATH = CONFIRMATION_MODAL_FOOTER_XPATH + "//button[text()='Cancel']";

    @FindBy(xpath = CONFIRMATION_MODAL_CONFIRM_BUTTON_XPATH)
    private WebElement confirmButton;

    @FindBy(xpath = CONFIRMATION_MODAL_CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = CONFIRMATION_MODAL_BODY_XPATH)
    private WebElement confirmationMessage;

    public EditInvoiceLineConfirmationModal(WebDriver driver) {
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

    public void clickConfirm() {
        confirmButton.click();
    }

    public void clickCancel() {
        cancelButton.click();
    }

    public String getConfirmationMessage() {
        return confirmationMessage.getText();
    }

}
