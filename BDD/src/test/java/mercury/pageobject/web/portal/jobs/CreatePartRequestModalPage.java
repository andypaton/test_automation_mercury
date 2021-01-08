package mercury.pageobject.web.portal.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class CreatePartRequestModalPage extends Base_Page<CreatePartRequestModalPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Create Request";

    //Core
    private static final String CREATE_REQUEST_MODAL_CSS = "div.modal-content";
    private static final String CREATE_REQUEST_MODAL_FOOTER_CSS = CREATE_REQUEST_MODAL_CSS + " div.modal-footer";
    private static final String SAVE_BUTTON_CSS = CREATE_REQUEST_MODAL_FOOTER_CSS + " button.btn-primary";


    @FindBy(css = SAVE_BUTTON_CSS)
    private WebElement saveButton;

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(SAVE_BUTTON_CSS));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }


    public CreatePartRequestModalPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }


    // Page Interactions

    public void createRequest() {
        saveButton.click();
    }

}
