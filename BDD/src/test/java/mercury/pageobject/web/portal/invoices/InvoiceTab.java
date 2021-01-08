package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import mercury.helpers.State;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;

public class InvoiceTab extends Base_Page<InvoiceTab>{
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Invoice";

    // Invoice Details
    private static final String INVOICE_APPROVAL_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_DETAILS_XPATH = INVOICE_APPROVAL_CONTAINER_XPATH + "//div[@class='inv-details']";
    private static final String INVOICE_TABLE_XPATH = INVOICE_DETAILS_XPATH + "//table[@id='invoice-table']";
    private static final String ORDER_REF_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Order Ref']/following-sibling::td";
    private static final String ORDER_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Order Value (%s)']/following-sibling::td";
    private static final String JOB_REF_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Job Ref']/following-sibling::td";
    private static final String INV_LOGGED_DATE_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Logged Date']/following-sibling::td";
    private static final String SITE_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Site']/following-sibling::td";
    private static final String INV_NUM_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Inv Num']/following-sibling::td";
    private static final String INV_DATE_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Inv Date']/following-sibling::td";
    private static final String INV_SUPPLIER_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Supplier']/following-sibling::td";
    private static final String INV_NET_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Net (%s)']/following-sibling::td";
    private static final String INV_TAX_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='%s (%s)']/following-sibling::td";
    private static final String INV_GROSS_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Gross (%s)']/following-sibling::td";
    private static final String TOTAL_JOB_COST_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='Total Job Cost (%s)']/following-sibling::td";
    private static final String TABLE_VALUE_XPATH = INVOICE_TABLE_XPATH + "//th[text()='%s']/following-sibling::td";
    private static final String TABLE_VALUE_CONTAINS_XPATH = INVOICE_TABLE_XPATH + "//th[contains(text(), '%s')]/following-sibling::td";

    @FindBy(xpath = ORDER_REF_VALUE_XPATH)
    private WebElement orderRef;

    @FindBy(xpath = JOB_REF_VALUE_XPATH)
    private WebElement jobRef;

    @FindBy(xpath = INV_LOGGED_DATE_VALUE_XPATH)
    private WebElement loggedDate;

    @FindBy(xpath = SITE_VALUE_XPATH)
    private WebElement siteValue;

    @FindBy(xpath = INV_NUM_VALUE_XPATH)
    private WebElement invNum;

    @FindBy(xpath = INV_DATE_VALUE_XPATH)
    private WebElement invDate;

    @FindBy(xpath = INV_SUPPLIER_VALUE_XPATH)
    private WebElement invSupplier;


    public InvoiceTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_TABLE_XPATH));
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

    public Grid getInvoiceGrid() {
        return GridHelper.getGrid(INVOICE_TABLE_XPATH);
    }

    public String getOrderRef() {
        return orderRef.getText();
    }

    public String getOrderValue(String currencySymbol) {
        WebElement element = waitForElement(By.xpath(String.format(ORDER_VALUE_XPATH, currencySymbol)), State.ELEMENT_IS_VISIBLE);
        return element.getText();
    }

    public String getJobRef() {
        return jobRef.getText();
    }

    public String getLoggedDate() {
        return loggedDate.getText();
    }

    public String getSiteValue() {
        return siteValue.getText();
    }

    public String getInvNum() {
        return invNum.getText();
    }

    public String getInvoiceDate() {
        return invDate.getText();
    }

    public String getSupplierValue() {
        return invSupplier.getText();
    }

    public String getNetValue(String currencySymbol) {
        WebElement element = waitForElement(By.xpath(String.format(INV_NET_VALUE_XPATH, currencySymbol)), State.ELEMENT_IS_VISIBLE);
        return element.getText();
    }

    public String getTaxValue(String taxWord, String currencySymbol) {
        WebElement element = waitForElement(By.xpath(String.format(INV_TAX_VALUE_XPATH, taxWord, currencySymbol)), State.ELEMENT_IS_VISIBLE);
        return element.getText();
    }

    public String getGrossValue(String currencySymbol) {
        WebElement element = waitForElement(By.xpath(String.format(INV_GROSS_VALUE_XPATH, currencySymbol)), State.ELEMENT_IS_VISIBLE);
        return element.getText();
    }

    public String getTotalJobCostValue(String currencySymbol) {
        WebElement element = waitForElement(By.xpath(String.format(TOTAL_JOB_COST_VALUE_XPATH, currencySymbol)), State.ELEMENT_IS_VISIBLE);
        return element.getText();
    }

    public String getInvoiceValue(String header) {
        WebElement we = waitForElement(By.xpath(String.format(TABLE_VALUE_XPATH, header)), State.ELEMENT_IS_VISIBLE);
        return we.getText();
    }

    public String getInvoiceValueForHeaderContaining(String header) {
        WebElement we = waitForElement(By.xpath(String.format(TABLE_VALUE_CONTAINS_XPATH, header)), State.ELEMENT_IS_VISIBLE);
        return we.getText();
    }

}
