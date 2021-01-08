package mercury.pageobject.web.admin.companies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class AdminEditCompanySaveModal extends Base_Page<AdminEditCompanySaveModal>{

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_COMPANY_SAVE_MODAL_CONTENT_CONTAINER_XPATH = "//div[@class='modal-content']";
    private static final String EDIT_COMPANY_SAVE_MODAL_HEADER_XPATH = EDIT_COMPANY_SAVE_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class,'modal-header')]";
    private static final String EDIT_COMPANY_SAVE_MODAL_BODY_XPATH = EDIT_COMPANY_SAVE_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class,'modal-body')]";

    private static final String EDIT_COMPANY_SAVE_MODAL_TITLE_XPATH = EDIT_COMPANY_SAVE_MODAL_HEADER_XPATH + "//h3[contains(text(),'Save Company')]";
    private static final String EDIT_COMPANY_SAVE_MODAL_BODY_PARAGRAPH_XPATH = EDIT_COMPANY_SAVE_MODAL_BODY_XPATH + "//p";

    //buttons
    private static final String EDIT_COMPANY_SAVE_MODAL_CONFIRM_BUTTON_XPATH = EDIT_COMPANY_SAVE_MODAL_CONTENT_CONTAINER_XPATH + "//button[contains(text(),'Confirm')]";
    private static final String EDIT_COMPANY_SAVE_MODAL_CANCEL_BUTTON_XPATH = EDIT_COMPANY_SAVE_MODAL_CONTENT_CONTAINER_XPATH + "//button[contains(text(),'Cancel')]";

    @FindBy(xpath = EDIT_COMPANY_SAVE_MODAL_BODY_PARAGRAPH_XPATH)
    private WebElement saveModalParagraph;

    @FindBy(xpath = EDIT_COMPANY_SAVE_MODAL_CONFIRM_BUTTON_XPATH)
    private WebElement confirmButton;

    @FindBy(xpath = EDIT_COMPANY_SAVE_MODAL_CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;


    public AdminEditCompanySaveModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(EDIT_COMPANY_SAVE_MODAL_TITLE_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getParagraph() {
        waitForAnimation();
        return saveModalParagraph.getText();
    }

    public void clickConfirm() {
        confirmButton.click();
        waitForAngularRequestsToFinish();
    }

}
