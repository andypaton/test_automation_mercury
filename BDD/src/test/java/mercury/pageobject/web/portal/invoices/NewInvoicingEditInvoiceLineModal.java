package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;

public class NewInvoicingEditInvoiceLineModal extends Base_Page<NewInvoicingEditInvoiceLineModal>{
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Invoice";

    private static final String EDIT_ORDER_LINE_MODAL_CONTENT_XPATH = "//div[@class='modal-content']";
    private static final String EDIT_ORDER_LINE_MODAL_FOOTER_XPATH = EDIT_ORDER_LINE_MODAL_CONTENT_XPATH + "//div[@class ='modal-footer']";
    private static final String EDIT_ORDER_LINE_MODAL_HEADER_XPATH = EDIT_ORDER_LINE_MODAL_CONTENT_XPATH + "//div[contains(@class,'modal-header')]//h3[contains(text(),'Edit Invoice Line')]";

    //Grid
    private static final String GRID_XPATH = EDIT_ORDER_LINE_MODAL_CONTENT_XPATH + "//div[@class='row table-responsive']";

    //Buttons
    private static final String SAVE_BUTTON_XPATH = EDIT_ORDER_LINE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Save')]";
    private static final String DELETE_BUTTON_XPATH = EDIT_ORDER_LINE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Delete')]";
    
    private static final String CONFIRMATION_MODAL_HEADER_TITLE_XPATH = EDIT_ORDER_LINE_MODAL_CONTENT_XPATH + "//div[contains(@class, 'modal-header')]//h3[contains(text(),'Confirmation')]";

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = DELETE_BUTTON_XPATH)
    private WebElement delete;

    public NewInvoicingEditInvoiceLineModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(EDIT_ORDER_LINE_MODAL_HEADER_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + "isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(EDIT_ORDER_LINE_MODAL_HEADER_XPATH + " Load");
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public void clickSave() {
        save.click();
        waitForAngularRequestsToFinish();
    }
    
    public Boolean isConfirmationMessageDisplayed() {
        return isElementPresent(By.xpath(CONFIRMATION_MODAL_HEADER_TITLE_XPATH));
    }
}
