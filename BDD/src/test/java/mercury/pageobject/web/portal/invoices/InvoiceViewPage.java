package mercury.pageobject.web.portal.invoices;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class InvoiceViewPage extends Base_Page<InvoiceViewPage> {

    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice View";

    //Main content
    private static final String INVOICE_VIEW_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_VIEW_TITLE_XPATH = INVOICE_VIEW_CONTAINER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";

    private static final String INVOICE_PDF_XPATH = ".//div[contains(@class,'body-content')]//div[@class='invoice-approval']//div//iframe";
    private static final String INVOICE_PDF_TAB_XPATH = ".//body//embed[@type='application/pdf']";

    @FindBy(xpath = INVOICE_VIEW_TITLE_XPATH)
    private WebElement invoiceViewTitle;

    @FindBy(xpath = INVOICE_PDF_XPATH)
    private WebElement invoicePdf;
    
    @FindBy(xpath = INVOICE_PDF_TAB_XPATH)
    private WebElement invoiceTabPdf;

    public InvoiceViewPage(WebDriver driver) {
        super(driver);
    }


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_VIEW_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(INVOICE_VIEW_TITLE_XPATH));
    }


    public boolean isDisplayed() {
        WebElement shadowDom = expandRootElement(driver.findElement(By.id("download")));
        WebElement ele =  shadowDom.findElement(By.cssSelector("#download"));
        return ele.isDisplayed();
    }
    
    public WebElement expandRootElement(WebElement element) {
        WebElement ele = (WebElement) ((JavascriptExecutor)driver).executeScript("return arguments[0].shadowRoot", element);
        return ele;
    }

    public boolean isInvoicePdfDisplayed() {
        
        WebElement root1 = driver.findElement(By.tagName("viewer-pdf-toolbar"));
      
        //Get shadow root element
        WebElement shadowRoot1 = expandRootElement(root1);

        WebElement root2 = shadowRoot1.findElement(By.cssSelector("cr-icon-button"));
        WebElement shadowRoot2 = expandRootElement(root2);
        
        return shadowRoot2.findElement(By.cssSelector("#download")).isDisplayed();  
    }
    
    public boolean isPDFDisplayed() {
        waitForAngularRequestsToFinish();
        return invoicePdf.isDisplayed();
    }
    
    public String getPdfLinkAddress() {
        return invoicePdf.getAttribute("src");
    }
    
    public void openPdfInNewTab(String link) {
         driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"t");
        ((JavascriptExecutor)driver).executeScript("window.open();");
        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1)); 
        driver.get(link);
        waitForAngularRequestsToFinish();
    }
    
    public boolean isPDFDisplayedInTab() {
        return invoiceTabPdf.isDisplayed();    
    }
}
