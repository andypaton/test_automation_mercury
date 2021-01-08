package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class InvoiceLinesModal extends Base_Page<InvoiceLinesModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String MODAL_TITLE = "Invoice Lines modal";

    private static final String INVOICE_LINES_MODAL_XPATH = "//div[@id = 'InvoiceLinesModal']";
    private static final String INVOICE_GRID_XPATH = INVOICE_LINES_MODAL_XPATH + "//table";
    private static final String CLOSE_BUTTON_XPATH = INVOICE_LINES_MODAL_XPATH + "//button[text() = 'Close']";


    @FindBy(css = CLOSE_BUTTON_XPATH)
    private WebElement close;


    public InvoiceLinesModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(MODAL_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_LINES_MODAL_XPATH));
            logger.info(MODAL_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(MODAL_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(MODAL_TITLE + " Load");
    }

    public Grid getGrid() {
        return GridHelper.getGrid(INVOICE_GRID_XPATH);
    }

    public void close() {
        close.click();
        waitForAngularRequestsToFinish();
    }

}
