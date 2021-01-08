package mercury.pageobject.web.portal.invoices;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class InvoiceDeleteInvoiceTemplateModal extends Base_Page<InvoiceDeleteInvoiceTemplateModal> {

    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice Template";

    //Main content
    private static final String INVOICE_TEMPLATE_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String DELETE_INVOICE_TEMPLATE_MODAL_CONTAINER = INVOICE_TEMPLATE_CONTAINER_XPATH + "//div[contains(@class, 'modal-content')]";

    //Modal Title
    private static final String DELETE_INVOICE_TEMPLATE_MODAL_TITLE_XPATH = DELETE_INVOICE_TEMPLATE_MODAL_CONTAINER + "//h4[(@id='myModalLabel')]";

    //Buttons
    private static final String DELETE_INVOICE_TEMPLATE_MODAL_DELETE_BUTTON_XPATH = DELETE_INVOICE_TEMPLATE_MODAL_CONTAINER + "//button[contains(text(), 'Delete')]";

    @FindBy(xpath = DELETE_INVOICE_TEMPLATE_MODAL_DELETE_BUTTON_XPATH)
    private WebElement deleteInvoiceTemplateButton;



    public InvoiceDeleteInvoiceTemplateModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(DELETE_INVOICE_TEMPLATE_MODAL_TITLE_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + "isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public Boolean isPageLoaded() {
        return this.isElementPresent(By.xpath(DELETE_INVOICE_TEMPLATE_MODAL_TITLE_XPATH));
    }

    public InvoiceTemplatesPage delete() {
        waitForAngularRequestsToFinish();
        deleteInvoiceTemplateButton.click();
        return PageFactory.initElements(driver, InvoiceTemplatesPage.class).get();
    }
}
