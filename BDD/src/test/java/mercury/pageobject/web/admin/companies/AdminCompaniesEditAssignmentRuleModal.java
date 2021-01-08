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

public class AdminCompaniesEditAssignmentRuleModal extends Base_Page<AdminCompaniesEditAssignmentRuleModal>{

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_ASSIGNMENT_RULE_MODAL_CONTENT_CONTAINER_XPATH = "//div[@class='modal-content']";
    private static final String EDIT_ASSIGNMENT_RULE_MODAL_HEADER_XPATH = EDIT_ASSIGNMENT_RULE_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class,'modal-header')]";
    private static final String EDIT_ASSIGNMENT_RULE_MODAL_BODY_XPATH = EDIT_ASSIGNMENT_RULE_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class,'modal-body')]";

    private static final String EDIT_ASSIGNMENT_RULE_MODAL_TITLE_XPATH = EDIT_ASSIGNMENT_RULE_MODAL_HEADER_XPATH + "//h3[contains(text(),'Edit Assignment Rule')]";
    private static final String EDIT_ASSIGNMENT_RULE_MODAL_BODY_PARAGRAPH_XPATH = EDIT_ASSIGNMENT_RULE_MODAL_BODY_XPATH + "//span";

    //Buttons
    private static final String EDIT_ASSIGNMENT_RULE_MODAL_CONFIRM_BUTTON_XPATH = EDIT_ASSIGNMENT_RULE_MODAL_CONTENT_CONTAINER_XPATH + "//button[contains(text(),'Save')]";
    private static final String EDIT_ASSIGNMENT_RULE_MODAL_CANCEL_BUTTON_XPATH = EDIT_ASSIGNMENT_RULE_MODAL_CONTENT_CONTAINER_XPATH + "//button[contains(text(),'Cancel')]";

    @FindBy(xpath = EDIT_ASSIGNMENT_RULE_MODAL_BODY_PARAGRAPH_XPATH)
    private WebElement editModalParagraph;

    @FindBy(xpath = EDIT_ASSIGNMENT_RULE_MODAL_CONFIRM_BUTTON_XPATH)
    private WebElement confirmButton;

    @FindBy(xpath = EDIT_ASSIGNMENT_RULE_MODAL_CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;


    public AdminCompaniesEditAssignmentRuleModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(EDIT_ASSIGNMENT_RULE_MODAL_TITLE_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getParagraph() {
        return editModalParagraph.getText();
    }

    public void clickSave() {
        confirmButton.click();
        waitForAngularRequestsToFinish();
    }

}
