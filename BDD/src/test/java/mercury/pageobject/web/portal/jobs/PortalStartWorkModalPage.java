package mercury.pageobject.web.portal.jobs;

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
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class PortalStartWorkModalPage extends Base_Page<PortalStartWorkModalPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page Elements
    private static final String PAGE_MODAL_XPATH = "//div[@id = 'StartWorkModal']//div[@class='modal-content']";
    private static final String PAGE_MODAL_HEADER_XPATH = PAGE_MODAL_XPATH + "//div[@class='modal-header']";

    // Core
    private static final String BUTTON_XPATH = "//div[@class='modal-footer']//button[contains(text(),'%s')]";
    private static final String START_WORK_MESSAGE_XPATH = "//div[@class='modal-content']//div[@class='modal-body']";
    private static final String CONFIRMATION_MESSAGE_XPATH = "//div[@class='modal-content']//div[@class='modal-body']/p[contains(text(),'%s')]";

    //WebElements
    @FindBy(xpath = PAGE_MODAL_HEADER_XPATH)
    private WebElement pageHeader;

    @FindBy(xpath = START_WORK_MESSAGE_XPATH)
    private WebElement startWorkMessage;

    @FindBy(xpath = CONFIRMATION_MESSAGE_XPATH)
    private WebElement confirmationMessage;

    public PortalStartWorkModalPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_MODAL_HEADER_XPATH + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_MODAL_HEADER_XPATH));
            logger.info(PAGE_MODAL_HEADER_XPATH + " isloaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_MODAL_HEADER_XPATH + " isloaded error");
            throw new AssertionError();
        }
    }

    public PortalJobsForSitePage clickCancelButton() {
        WebElement element = waitForElement((By.xpath(String.format(BUTTON_XPATH, "Cancel"))),ELEMENT_IS_CLICKABLE);
        element.click();
        return PageFactory.initElements(driver, PortalJobsForSitePage.class).get();
    }

    public void clickConfirmButton() {
        WebElement element = waitForElement((By.xpath(String.format(BUTTON_XPATH, "Confirm"))),ELEMENT_IS_CLICKABLE);
        element.click();
    }

    public String getStartWorkMessagetext() {
        return startWorkMessage.getText();
    }

    public String getStartConfirmationMessagetext() {
        waitForAngularRequestsToFinish();
        return startWorkMessage.getText();
    }

    public Boolean IsConfirmationMessageDisplayed(String message) {
        waitForElement(By.xpath(String.format(CONFIRMATION_MESSAGE_XPATH, message)), State.ELEMENT_IS_VISIBLE);
        return isElementVisible(By.xpath(String.format(CONFIRMATION_MESSAGE_XPATH, message)));
    }
}
