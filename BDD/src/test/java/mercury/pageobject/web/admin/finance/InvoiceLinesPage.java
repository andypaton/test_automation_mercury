package mercury.pageobject.web.admin.finance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class InvoiceLinesPage extends Base_Page<InvoiceLinesPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String INVOICE_LINE_TYPE_ADMIN_XPATH = "//div[@ng-app='invoiceLineTypeAdmin']";

    private static final String INVOICE_LINE_TYPE_ADMIN_HEADER_XPATH = INVOICE_LINE_TYPE_ADMIN_XPATH + "//h2[contains(text(),'Invoice Line Type Admin')]";
    private static final String INVOICE_LINE_TYPE_GRID_XPATH = INVOICE_LINE_TYPE_ADMIN_XPATH + "//div[@class='k-grid-content']";

    //Buttons
    private static final String INVOICE_LINE_TYPE_CANCEL_BUTTON_XPATH = INVOICE_LINE_TYPE_ADMIN_XPATH + "//button[contains(text(),'Cancel')]";
    private static final String INVOICE_LINE_TYPE_SAVE_BUTTON_XPATH = INVOICE_LINE_TYPE_ADMIN_XPATH + "//button[contains(text(),'Save')]";


    @FindBy(xpath = INVOICE_LINE_TYPE_ADMIN_HEADER_XPATH)
    private WebElement invoiceLineTypeAdminHeader;

    @FindBy(xpath = INVOICE_LINE_TYPE_GRID_XPATH)
    private WebElement invoiceLineTypeGrid;

    @FindBy(xpath = INVOICE_LINE_TYPE_CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = INVOICE_LINE_TYPE_SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    public InvoiceLinesPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(INVOICE_LINE_TYPE_ADMIN_HEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectCancel() {
        cancelButton.click();
    }

    public void selectSave() {
        saveButton.click();
    }

}
