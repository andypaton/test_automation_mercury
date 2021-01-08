package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class InvoiceSubmittedPage extends Base_Page<InvoiceSubmittedPage>{
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Invoice Submitted";

    //Main Content
    private static final String INVOICE_SUBMITTED_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_SUBMITTED_TITLE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//h1[contains(text(), 'Invoice Submitted')]";
    private static final String INVOICE_SUBMITTED_HEADER_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//h3[contains(text(),'Invoice Submitted Successfully')]";
    private static final String ORDER_REF_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Order Ref']/following-sibling::td";
    private static final String JOB_REF_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Job Ref']/following-sibling::td";
    private static final String SITE_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Site']/following-sibling::td";
    private static final String JOB_DATE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Job Date']/following-sibling::td";
    private static final String INVOICE_NUMBER_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Invoice Number']/following-sibling::td";
    private static final String INVOICE_DATE_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Invoice Date']/following-sibling::td";
    private static final String NET_AMOUNT_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Net Amount (%s)']/following-sibling::td";
    private static final String TAX_AMOUNT_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='%s Amount (%s)']/following-sibling::td";
    private static final String GROSS_AMOUNT_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Gross Amount (%s)']/following-sibling::td";
    private static final String DATE_SUBMITTED_VALUE_XPATH = INVOICE_SUBMITTED_CONTAINER_XPATH + "//table//th[text()='Date Submitted']/following-sibling::td";

    @FindBy(xpath = INVOICE_SUBMITTED_HEADER_XPATH)
    private WebElement invoiceSubmittedMessage;

    @FindBy(xpath = ORDER_REF_VALUE_XPATH)
    private WebElement orderRef;

    @FindBy(xpath = JOB_REF_VALUE_XPATH)
    private WebElement jobRef;

    @FindBy(xpath = SITE_VALUE_XPATH)
    private WebElement siteValue;

    @FindBy(xpath = JOB_DATE_XPATH)
    private WebElement jobDate;

    @FindBy(xpath = INVOICE_NUMBER_VALUE_XPATH)
    private WebElement invoiceNumber;

    @FindBy(xpath = INVOICE_DATE_VALUE_XPATH)
    private WebElement invoiceDate;

    @FindBy(xpath = DATE_SUBMITTED_VALUE_XPATH)
    private WebElement dateSubmitted;

    public InvoiceSubmittedPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_SUBMITTED_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(INVOICE_SUBMITTED_TITLE_XPATH));
    }

    public Boolean isInvoiceSubmittedHeaderDisplayed() {
        return this.isElementPresent(By.xpath(INVOICE_SUBMITTED_HEADER_XPATH));
    }

    public String getInvoiceSubmittedMessage() {
        return invoiceSubmittedMessage.getText();
    }

    public String getOrderRef() {
        return orderRef.getText();
    }

    public String getJobRef() {
        return jobRef.getText();
    }

    public String getSiteValue() {
        return siteValue.getText();
    }

    public String getJobDate() {
        return jobDate.getText();
    }

    public String getInvoiceNumber() {
        return invoiceNumber.getText();
    }

    public String getInvoiceDate() {
        return invoiceDate.getText();
    }

    public String getNetAmount(String currencySymbol) {
        WebElement element = waitForElement(By.xpath(String.format(NET_AMOUNT_VALUE_XPATH, currencySymbol)), State.ELEMENT_IS_VISIBLE);
        return element.getText();
    }

    public String getTaxAmount(String taxWord, String currencySymbol) {
        WebElement element = waitForElement(By.xpath(String.format(TAX_AMOUNT_VALUE_XPATH, taxWord, currencySymbol)), State.ELEMENT_IS_VISIBLE);
        return element.getText();
    }

    public String getGrossAmount(String currencySymbol) {
        WebElement element = waitForElement(By.xpath(String.format(GROSS_AMOUNT_VALUE_XPATH, currencySymbol)), State.ELEMENT_IS_VISIBLE);
        return element.getText();
    }

    public String getDateSubmitted() {
        return dateSubmitted.getText();
    }

}
