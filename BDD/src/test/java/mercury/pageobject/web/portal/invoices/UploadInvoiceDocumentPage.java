package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class UploadInvoiceDocumentPage extends Base_Page<UploadInvoiceDocumentPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Upload Invoice Document";

    //Main Content
    private static final String UPLOAD_INVOICE_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String UPLOAD_INVOICE_MAIN_CONTENT_XPATH = "//div[@class='main-content']";
    private static final String UPLOAD_INVOICE_DOCUMENT_TITLE_XPATH = UPLOAD_INVOICE_CONTAINER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";
    private static final String UPLOAD_INVOICE_FILE_HEADER_XPATH = UPLOAD_INVOICE_CONTAINER_XPATH +"//h2";

    //Form
    private static final String UPLOAD_INVOICE_DOCUMENT_FORM_XPATH = UPLOAD_INVOICE_MAIN_CONTENT_XPATH + "//form[contains(@action, 'UploadInvoiceDocument')]";
    private static final String UPLOAD_INVOICE_DOCUMENT_FILE_NAME_LABEL_XPATH = UPLOAD_INVOICE_DOCUMENT_FORM_XPATH + "//label";

    //Buttons
    private static final String UPLOAD_INVOICE_DOCUMENT_UPLOAD_FILE_BUTTON_XPATH = UPLOAD_INVOICE_DOCUMENT_FORM_XPATH + "//input[@value='Upload File']";

    //Text Boxes
    private static final String FILENAME_XPATH = UPLOAD_INVOICE_MAIN_CONTENT_XPATH + "//fieldset//input[(@id='FileToUpload')]";

    @FindBy(xpath = UPLOAD_INVOICE_FILE_HEADER_XPATH)
    private WebElement uploadInvoiceFileHeader;

    @FindBy(xpath = UPLOAD_INVOICE_DOCUMENT_FILE_NAME_LABEL_XPATH)
    private WebElement uploadDocumentFileNameLabel;

    @FindBy(xpath = UPLOAD_INVOICE_DOCUMENT_TITLE_XPATH)
    private WebElement uploadInvoiceDocumentTitle;

    @FindBy(xpath = UPLOAD_INVOICE_DOCUMENT_UPLOAD_FILE_BUTTON_XPATH)
    private WebElement uploadDocumentButton;

    @FindBy(xpath = FILENAME_XPATH)
    private WebElement documentFileName;

    public UploadInvoiceDocumentPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(UPLOAD_INVOICE_DOCUMENT_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(UPLOAD_INVOICE_DOCUMENT_TITLE_XPATH));
    }

    public InvoiceDetailsPage selectUploadDocument() {
        uploadDocumentButton.click();
        return PageFactory.initElements(driver, InvoiceDetailsPage.class).get();
    }

    public void clickUploadFile() {
        uploadDocumentButton.click();
    }

    public void setDocumentFileName(String keysToSend) {
        documentFileName.sendKeys(keysToSend);
    }


}
