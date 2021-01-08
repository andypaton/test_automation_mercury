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

public class InvoiceTemplatesPage extends Base_Page<InvoiceTemplatesPage> {
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice Templates";

    //Main content
    private static final String INVOICE_TEMPLATES_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_TEMPLATES_TITLE_XPATH = INVOICE_TEMPLATES_CONTAINER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";

    //Form
    private static final String INVOICE_TEMPLATES_FORM_XPATH = INVOICE_TEMPLATES_CONTAINER_XPATH + "//form[(@class = 'upload-form')]";

    //Page elements
    private static final String INVOICE_UPLOADS_TEMPLATES_HEADER = INVOICE_TEMPLATES_CONTAINER_XPATH + "//h3[contains(text(), 'Upload Invoice Templates')]";

    //Input boxes
    private static final String INVOICE_UPlOAD_INPUT_BOX_CSS = "input#TemplateFileUpload";

    //Buttons
    private static final String INVOICE_TEMPLATES_UPLOAD_TEMPLATE_XPATH = INVOICE_TEMPLATES_FORM_XPATH + "//button[(@id = 'submitFileTemplate')]";

    @FindBy(xpath = INVOICE_TEMPLATES_TITLE_XPATH)
    private WebElement invoiceTemplatesTitle;

    @FindBy(xpath = INVOICE_UPLOADS_TEMPLATES_HEADER)
    private WebElement invoiceTemplateHeader;

    @FindBy(css = INVOICE_UPlOAD_INPUT_BOX_CSS)
    private WebElement invoiceTemplateInputBox;

    @FindBy(xpath = INVOICE_TEMPLATES_UPLOAD_TEMPLATE_XPATH)
    private WebElement uploadInvoiceTemplateButton;

    public InvoiceTemplatesPage(WebDriver driver) {
        super(driver);
    }


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_TEMPLATES_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(INVOICE_TEMPLATES_TITLE_XPATH));
    }

    public String getInvoiceTemplatesTitle() {
        return invoiceTemplatesTitle.getText();
    }

    public String getInvoiceUploadTemplateHeader() {
        return invoiceTemplateHeader.getText();
    }

    public  void setInvoiceTemplateFileName(String keysToSend) {
        invoiceTemplateInputBox.sendKeys(keysToSend);
    }

    public InvoiceTemplatePage uploadInvoiceTemplate() {
        uploadInvoiceTemplateButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceTemplatePage.class).get();
    }

}
