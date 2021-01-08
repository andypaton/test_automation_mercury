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

public class AdminCompaniesAssignmentRuleDeleteModal extends Base_Page<AdminCompaniesAssignmentRuleDeleteModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ASSIGNMENT_RULE_DELETE_MODAL_CONTENT_CONTAINER_XPATH = "//div[@class='modal-content']";
    private static final String ASSIGNMENT_RULE_DELETE_MODAL_HEADER_XPATH = ASSIGNMENT_RULE_DELETE_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class,'modal-header')]";
    private static final String ASSIGNMENT_RULE_DELETE_MODAL_BODY_XPATH = ASSIGNMENT_RULE_DELETE_MODAL_CONTENT_CONTAINER_XPATH + "//div[contains(@class,'modal-body')]";
    private static final String ASSIGNMENT_RULE_DELETE_MODAL_SUBHEADER_XPATH = ASSIGNMENT_RULE_DELETE_MODAL_HEADER_XPATH + "//h3[contains(text(),'Delete Assignment Rule')]";

    //buttons
    private static final String ASSIGNMENT_RULE_DELTE_MODAL_DELETE_BUTTON_XPATH = ASSIGNMENT_RULE_DELETE_MODAL_CONTENT_CONTAINER_XPATH + "//button[contains(text(),'Delete')]";
    private static final String ASSIGNMENT_RULE_DELTE_MODAL_CANCEL_BUTTON_XPATH = ASSIGNMENT_RULE_DELETE_MODAL_CONTENT_CONTAINER_XPATH + "//button[contains(text(),'Cancel')]";

    @FindBy(xpath = ASSIGNMENT_RULE_DELETE_MODAL_BODY_XPATH)
    private WebElement deleteModalBody;

    @FindBy(xpath = ASSIGNMENT_RULE_DELTE_MODAL_DELETE_BUTTON_XPATH)
    private WebElement deleteButton;

    @FindBy(xpath = ASSIGNMENT_RULE_DELTE_MODAL_CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;


    public AdminCompaniesAssignmentRuleDeleteModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ASSIGNMENT_RULE_DELETE_MODAL_SUBHEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getBody() {
        waitForAnimation();
        return deleteModalBody.getText();
    }

    public void clickDelete() {
        waitForAnimation();
        deleteButton.click();
    }

    public void clickCancel() {
        waitForAnimation();
        cancelButton.click();
    }

}
