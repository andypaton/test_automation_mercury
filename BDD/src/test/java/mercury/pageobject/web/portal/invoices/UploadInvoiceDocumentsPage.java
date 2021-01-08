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


public class UploadInvoiceDocumentsPage extends Base_Page<UploadInvoiceDocumentsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Upload Invoice Documents";

    //Main Content
    private static final String UPLOAD_INVOICE_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String UPLOAD_INVOICE_DOCUMENTS_TITLE_XPATH = UPLOAD_INVOICE_CONTAINER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";
    private static final String UPLOAD_INVOICE_FILE_HEADER_XPATH = ".//div[contains(@class, 'main-content')]//h2";

    //Form
    private static final String UPLOAD_INVOICE_FORM_XPATH = "//form[contains(@action, 'UploadInvoiceAndJobSheetDocument')]";

    private static final String INVOICE_FILE_HEADER = UPLOAD_INVOICE_FORM_XPATH + "//h3[contains(text(),'Invoice File')]";
    private static final String JOB_SHEET_FILE_HEADER = UPLOAD_INVOICE_FORM_XPATH + "//h3[contains(text(),'Job Sheet File')]";

    //Buttons
    private static final String UPLOAD_INVOICE_FILE_BROWSE_BUTTON_XPATH = "//input[@id='InvoiceFileUpload']/..";
    private static final String UPLOAD_INVOICE_JOB_SHEET_BROWSE_BUTTON_XPATH = "//input[@id='JobSheetFileUpload']";
    private static final String UPLOAD_INVOICE_FILES_BUTTON_XPATH = "//button[contains(text(), 'Upload Files')]";

    //Text Boxes
    private static final String INVOICE_FILE_NAME_CSS = "input#InvoiceFileUpload";
    private static final String JOBSHEET_FILE_NAME_CSS = "input#JobSheetFileUpload";


    @FindBy(xpath = UPLOAD_INVOICE_FILE_HEADER_XPATH)
    private WebElement uploadInvoiceDetailsHeading;

    @FindBy(xpath = INVOICE_FILE_HEADER)
    private WebElement invoiceFileHeader;

    @FindBy(xpath = JOB_SHEET_FILE_HEADER)
    private WebElement jobSheetFile;

    @FindBy(xpath = UPLOAD_INVOICE_DOCUMENTS_TITLE_XPATH)
    private WebElement uploadInvoiceDocumentsTitle;

    @FindBy(xpath = UPLOAD_INVOICE_FILE_BROWSE_BUTTON_XPATH)
    private WebElement uploadInvoiceBrowseButton;

    @FindBy(xpath = UPLOAD_INVOICE_JOB_SHEET_BROWSE_BUTTON_XPATH)
    private WebElement uploadJobSheetBrowseButton;

    @FindBy(xpath = UPLOAD_INVOICE_FILES_BUTTON_XPATH)
    private WebElement uploadFilesButton;

    @FindBy(css = INVOICE_FILE_NAME_CSS)
    private WebElement invoiceFileName;

    @FindBy(css = JOBSHEET_FILE_NAME_CSS)
    private WebElement jobSheetFileName;


    public UploadInvoiceDocumentsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(UPLOAD_INVOICE_DOCUMENTS_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(UPLOAD_INVOICE_DOCUMENTS_TITLE_XPATH));
    }

    public String getUploadInvoiceFileHeading() {
        return uploadInvoiceDetailsHeading.getText();
    }

    public String getInvoiceFileHeader() {
        return invoiceFileHeader.getText();
    }

    public String getJobSheetFile() {
        return jobSheetFile.getText();
    }

    public String getUploadInvoiceDocumentsTitle() {
        return uploadInvoiceDocumentsTitle.getText();
    }

    public void selectUploadInvoiceFile() {
        uploadInvoiceBrowseButton.click();
    }

    public void setInvoiceFileName(String keysToSend) {
        invoiceFileName.sendKeys(keysToSend);
    }

    public void setJobSheetFileName(String keysToSend) {
        jobSheetFileName.sendKeys(keysToSend);
    }

    public InvoiceDetailsPage selectUploadFiles() {
        uploadFilesButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceDetailsPage.class).get();
    }
    public void clickUploadFiles() {
        uploadFilesButton.click();
        waitForAngularRequestsToFinish();
    }
}
