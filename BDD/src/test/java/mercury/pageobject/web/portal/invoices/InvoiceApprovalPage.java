package mercury.pageobject.web.portal.invoices;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class InvoiceApprovalPage extends Base_Page<InvoiceApprovalPage>{
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice Approval";

    // Main content
    private static final String INVOICE_APPROVAL_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_APPROVAL_HEADER_XPATH = INVOICE_APPROVAL_CONTAINER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";
    private static final String INVOICE_DOCUMENT_VIEW_XPATH = ".//div[contains(@class, 'doc-view')]";
    private static final String INVOICE_DOCUMENT_CHECK_HEADER_XPATH = INVOICE_APPROVAL_CONTAINER_XPATH + "//h3";

    private static final String INVOICE_INFO_TABLES_XPATH = INVOICE_APPROVAL_CONTAINER_XPATH + "//div[@id='info-tables']//ul//li";
    private static final String INVOICE_TABS_XPATH = INVOICE_INFO_TABLES_XPATH + "//a[contains(text(), '%s')]";

    private static final String INVOICE_DOCUMENTS_CHECK_LIST_XPATH = "//ul[contains(@class,'doc-menu')]//li";
    private static final String INVOICE_DOCUMENTS_CHECKED_XPATH = INVOICE_DOCUMENTS_CHECK_LIST_XPATH + "[contains(@class, 'doc-checked current')]//a";
    private static final String INVOICE_DOCUMENT_LINK_XPATH = INVOICE_DOCUMENTS_CHECK_LIST_XPATH + "//a[contains(text(), 'Invoice')]";
    private static final String JOB_SHEET_LINK_XPATH = INVOICE_DOCUMENTS_CHECK_LIST_XPATH +"//a[contains(text(), 'Job Sheet')]";

    private static final String INVOICE_BUDGET_LABEL_XPATH = ".//label[contains(@id,'budgetLabel')]";

    // Buttons
    private static final String INVOICE_REJECT_BUTTON_XPATH = "//input[contains(@id, 'RejectInvoice')]";
    private static final String INVOICE_VIEW_INVOICE_LINES_BUTTON_XPATH = "//input[contains(@id, 'ViewInvoiceLines')]";
    private static final String INVOICE_APPROVE_BUTTON_XPATH = "//input[contains(@id, 'Approve')]";

    private static final String TABS_XPATH = "//li[contains(@class, 'tab')]/a[contains(text(), '%s')]";
    private static final String ACTIVE_TAB = "//div[contains(@class, 'ui-tabs-panel') and contains(@aria-hidden, 'false')]";
    private static final String TABLE_HEADERS_XPATH = ACTIVE_TAB + "//table//th";

    private static final String INVOICE_XPATH = "//div[@class = 'invoice-approval']//iframe";
    private static final String DOCUMENTS_TO_CHECK_XPATH = "//h3[text() = 'Documents to Check']/following-sibling::ul/li";
    private static final String UNCHECKED_DOCUMENTS_XPATH = "//h3[text() = 'Documents to Check']/following-sibling::ul/li[not(contains(@class, 'doc-checked'))]";

    private static final String NEXT_BUTTON_XPATH = INVOICE_APPROVAL_CONTAINER_XPATH + "//div[@class = 'doc-nav']/a[contains(text(), 'Next >>')]";

    @FindBy(xpath = TABLE_HEADERS_XPATH)
    private List<WebElement> tableHeaders;

    @FindBy(xpath = INVOICE_APPROVAL_HEADER_XPATH)
    private WebElement invoiceApprovalHeader;

    @FindBy(xpath = INVOICE_DOCUMENT_VIEW_XPATH)
    private WebElement invoiceDocument;

    @FindBy(xpath = INVOICE_DOCUMENT_CHECK_HEADER_XPATH)
    private WebElement documentsToCheckHeader;

    @FindBy(xpath = INVOICE_REJECT_BUTTON_XPATH)
    private WebElement invoiceRejectButton;

    @FindBy(xpath = INVOICE_VIEW_INVOICE_LINES_BUTTON_XPATH)
    private WebElement viewInvoiceLinesButton;

    @FindBy(xpath = INVOICE_APPROVE_BUTTON_XPATH)
    private WebElement invoiceApproveButton;

    @FindBy(xpath = INVOICE_BUDGET_LABEL_XPATH)
    private WebElement budgetLabel;

    @FindBy(xpath = INVOICE_DOCUMENT_LINK_XPATH)
    private WebElement invoiceDocumentLink;

    @FindBy(xpath = JOB_SHEET_LINK_XPATH)
    private WebElement jobSheetDocumentLink;

    @FindBy(xpath = INVOICE_DOCUMENTS_CHECKED_XPATH)
    private WebElement invoiceDocumentsChecked;

    @FindBy(xpath = INVOICE_XPATH)
    private WebElement invoice;

    @FindBy(xpath = DOCUMENTS_TO_CHECK_XPATH)
    private List<WebElement> documentsToCheck;

    @FindBy(xpath = UNCHECKED_DOCUMENTS_XPATH)
    private List<WebElement> uncheckedDocuments;

    @FindBy(xpath = NEXT_BUTTON_XPATH)
    private WebElement next;

    public InvoiceApprovalPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_APPROVAL_HEADER_XPATH));
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
        return this.isElementPresent(By.xpath(INVOICE_APPROVAL_HEADER_XPATH));
    }

    public String getInvoiceApprovalHeader() {
        return invoiceApprovalHeader.getText();
    }

    public String getDocumentsToCheckHeader() {
        return documentsToCheckHeader.getText();
    }


    public boolean isDocumentViewVisible() {
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_VIEW_XPATH));
    }

    public void selectReject() {
        invoiceRejectButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectViewInvoiceLines() {
        viewInvoiceLinesButton.click();
        waitForAngularRequestsToFinish();
    }

    public ApproveInvoiceModalPage selectApprove() {
        invoiceApproveButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, ApproveInvoiceModalPage .class).get();

    }

    public String getInvoiceBudget() {
        return budgetLabel.getText();
    }

    public boolean isInvoiceDocumentLinkVisible() {
        return this.isElementPresent(By.xpath(INVOICE_DOCUMENT_LINK_XPATH));
    }

    public boolean isJobSheetDocumentLinkVisible() {
        return  this.isElementPresent(By.xpath(JOB_SHEET_LINK_XPATH));
    }

    public void selectInvoiceDocumentLink() {
        invoiceDocumentLink.click();
        waitForAngularRequestsToFinish();
    }

    public void selectJobSheetDocumentLink() {
        jobSheetDocumentLink.click();
        waitForAngularRequestsToFinish();
    }

    public String getInvoiceDocumentChecked() {
        return invoiceDocumentsChecked.getText();
    }

    public boolean isTabDisplayed(String tab) {
        return isElementVisible(By.xpath(String.format(INVOICE_TABS_XPATH, tab)));
    }

    public boolean isDisplayed(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(INVOICE_TABS_XPATH, option)));
        return we.isDisplayed();
    }

    public void clickNext() {
        POHelper.scrollToElement(next);
        next.click();
        waitForAngularRequestsToFinish();
    }

    public InvoiceTab clickInvoiceTab(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(INVOICE_TABS_XPATH, option)));
        we.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceTab.class).get();
    }

    public JobNotesTab clickJobNotesTab(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(INVOICE_TABS_XPATH, option)));
        POHelper.scrollToElement(we);
        we.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, JobNotesTab.class).get();
    }

    public ClosedownTab clickClosedownTab(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(INVOICE_TABS_XPATH, option)));
        POHelper.scrollToElement(we);
        we.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, ClosedownTab.class).get();
    }

    public UpliftsTab clickUpliftsTab(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(INVOICE_TABS_XPATH, option)));
        POHelper.scrollToElement(we);
        we.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, UpliftsTab.class).get();
    }

    public JobCostsTab clickJobCostsTab(String option) {
        WebElement we = driver.findElement(By.xpath(String.format(INVOICE_TABS_XPATH, option)));
        POHelper.scrollToElement(we);
        we.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, JobCostsTab.class).get();
    }

    public boolean isApprovable() {
        return isElementPresent(By.xpath(INVOICE_APPROVE_BUTTON_XPATH));
    }

    public void clickTab(String tab) {
        WebElement we = driver.findElement(By.xpath(String.format(TABS_XPATH, tab)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getTableHeaders() {
        List<String> result = new ArrayList<>();
        for (WebElement we : tableHeaders) {
            result.add(we.getText());
        }
        return result;
    }

    public String getInvoiceUrl() {
        return invoice.getAttribute("src");
    }

    public List<String> getDocumentsToCheck() {
        List<String> result = new ArrayList<>();
        for (WebElement we : documentsToCheck) {
            result.add(we.getText());
        }
        return result;
    }

    public List<String> getUncheckedDocuments() {
        getWebDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        List<String> result = new ArrayList<>();
        for (WebElement we : uncheckedDocuments) {
            result.add(we.getText());
        }
        getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return result;
    }

    public void selectDoc(String doc) {
        String xpath = DOCUMENTS_TO_CHECK_XPATH + String.format("/a[contains(text(), '%s')]", doc.trim());
        WebElement we = driver.findElement(By.xpath(xpath));
        we.click();
        waitForAngularRequestsToFinish();
    }

}
