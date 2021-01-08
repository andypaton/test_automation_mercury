package mercury.pageobject.web.portal.invoices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;


public class InvoiceTemplatePage extends Base_Page<InvoiceTemplatePage> {
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice Template";

    //Main content
    private static final String INVOICE_TEMPLATE_CONTAINER_XPATH = ".//div[contains(@class,'body-content')]";
    private static final String INVOICE_TEMPLATE_TITLE_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//h1[contains(text(), '" + PAGE_TITLE + "')]";

    //Page elements
    private static final String INVOICE_TEMPLATE_HEADER_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//h2[contains(text(), 'Invoice Template')]";
    private static final String INVOICE_TEMPLATE_TOAST_CONTAINER_XPATH = ".//div[(@id='toast-container')]";
    private static final String INVOICE_TEMPLATE_SAVED_TOAST_TITLE_XPATH = INVOICE_TEMPLATE_TOAST_CONTAINER_XPATH + "//div[contains(@class, 'toast-title')]";

    private static final String INVOICE_TEMPLATE_INVOICE_DETAIlS_CONTAINER_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//div[contains(@class, 'bhoechie-tab-container')]";
    private static final String INVOICE_DETAILS_LIST_XPATH = INVOICE_TEMPLATE_INVOICE_DETAIlS_CONTAINER_XPATH + "//div[contains(@class, 'list-group')]";
    private static final String INVOICE_DETAILS_LIST_CONTENT_XPATH = INVOICE_TEMPLATE_INVOICE_DETAIlS_CONTAINER_XPATH + "//div[contains(@class, 'bhoechie-tab')]";
    private static final String INVOICE_DETAILS_LIST_FIELD_ITEMS_XPATH = INVOICE_DETAILS_LIST_XPATH + "//h4/..";
    private static final String INVOICE_DETAILS_TAX_AMOUNT_ITEM_XPATH = INVOICE_DETAILS_LIST_FIELD_ITEMS_XPATH + "/text()[preceding-sibling::br[contains(..,' %s Amount')]]";
    private static final String INVOICE_DETAILS_EXCLUDE_TAX_CHECKBOX_XPATH = INVOICE_DETAILS_LIST_CONTENT_XPATH + "/div[contains(@class, 'well-sm')]/div[contains(@class, 'radio')]//input[@value= 'exempt']";

    private static final String INVOICE_TEMPLATE_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//canvas";


    //Buttons
    private static final String INVOICE_TEMPLATE_DELETE_BUTTON_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//button[contains(text(), 'Delete Template')]";
    private static final String INVOICE_TEMPLATE_SAVE_BUTTON_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//button[(@id='saveTemplate')]";

    private static final String LEGAL_ENTITY_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//div[contains(@class, 'col-md-3 bhoechie-tab-menu')]//a[contains(., 'Legal Entity')]";
    private static final String INVOICE_NUMBER_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//div[contains(@class, 'col-md-3 bhoechie-tab-menu')]//a[contains(., 'Invoice Number')]";
    private static final String INVOICE_DATE_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//div[contains(@class, 'col-md-3 bhoechie-tab-menu')]//a[contains(., 'Invoice Date')]";
    private static final String NET_AMOUNT_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//div[contains(@class, 'col-md-3 bhoechie-tab-menu')]//a[contains(., 'Net Amount')]";
    private static final String GROSS_AMOUNT_XPATH = INVOICE_TEMPLATE_CONTAINER_XPATH + "//div[contains(@class, 'col-md-3 bhoechie-tab-menu')]//a[contains(., 'Gross Amount')]";


    @FindBy(xpath = INVOICE_TEMPLATE_TITLE_XPATH)
    private WebElement invoiceTemplateTitle;

    @FindBy(xpath = INVOICE_TEMPLATE_HEADER_XPATH )
    private WebElement invoiceTemplateHeader;

    @FindBy(xpath = INVOICE_TEMPLATE_DELETE_BUTTON_XPATH)
    private WebElement invoiceTemplateDeleteButton;

    @FindBy(xpath = INVOICE_TEMPLATE_SAVE_BUTTON_XPATH)
    private WebElement invoiceTemplateSaveButton;

    @FindBy(xpath = INVOICE_TEMPLATE_SAVED_TOAST_TITLE_XPATH)
    private WebElement invoiceTemplateSavedToastAlert;

    @FindBy(xpath = LEGAL_ENTITY_XPATH)
    private WebElement legalEntity;

    @FindBy(xpath = INVOICE_NUMBER_XPATH)
    private WebElement invouceNumber;

    @FindBy(xpath = INVOICE_DATE_XPATH)
    private WebElement invoiceDate;

    @FindBy(xpath = NET_AMOUNT_XPATH)
    private WebElement netAmount;

    @FindBy(xpath = GROSS_AMOUNT_XPATH)
    private WebElement grossAmount;

    @FindBy(xpath = INVOICE_DETAILS_TAX_AMOUNT_ITEM_XPATH + "/..")
    private WebElement invoiceTemplateTaxAmountTab;

    @FindBy(xpath = INVOICE_DETAILS_EXCLUDE_TAX_CHECKBOX_XPATH)
    private WebElement invoiceTemplateExemptTaxCheckbox;

    @FindBy(xpath = INVOICE_TEMPLATE_XPATH)
    private WebElement templateCanvas;


    public InvoiceTemplatePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_TEMPLATE_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(INVOICE_TEMPLATE_TITLE_XPATH));
    }

    @Override
    public String getPageTitle() {
        return invoiceTemplateTitle.getText();
    }

    public String getInvoiceTemplateHeader() {
        return invoiceTemplateHeader.getText();
    }

    public InvoiceDeleteInvoiceTemplateModal deleteTemplate() {
        invoiceTemplateDeleteButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, InvoiceDeleteInvoiceTemplateModal.class);
    }

    public void selectSaveTemplate(Boolean isTaxAmountSpecified) {
        //refresh page to reload template, to view the save button(due to sending coordinates via dbhelper)
        if (isTaxAmountSpecified) {
            refreshPage();
        }
        waitForAngularRequestsToFinish();
        //select save template
        invoiceTemplateSaveButton.click();
        waitForAngularRequestsToFinish();
    }

    public void saveTemplate() {
        invoiceTemplateSaveButton.click();
        waitForAngularRequestsToFinish();
    }

    public String getSavedTemplateAlert() {
        return invoiceTemplateSavedToastAlert.getText();
    }

    public Boolean isSaveButtonDisplayed() {
        return this.isElementVisible(By.xpath(INVOICE_TEMPLATE_SAVE_BUTTON_XPATH));
    }

    public void excludeTaxAmount(String tabValue) {
        refreshPage();
        WebElement element = waitForElement(By.xpath(String.format(INVOICE_DETAILS_TAX_AMOUNT_ITEM_XPATH + "/..", tabValue)), State.ELEMENT_IS_CLICKABLE);
        element.click();
        invoiceTemplateExemptTaxCheckbox.click();
    }

    public void excludeTaxAmount() {
        invoiceTemplateExemptTaxCheckbox.click();
        waitForAngularRequestsToFinish();
    }

    public void selectLegalEntity() {
        legalEntity.click();
        waitForAngularRequestsToFinish();
    }

    public void selectInvoiceNumber() {
        invouceNumber.click();
        waitForAngularRequestsToFinish();
    }

    public void selectInvoiceDate() {
        invoiceDate.click();
        waitForAngularRequestsToFinish();
    }

    public void selectNetAmount() {
        netAmount.click();
        waitForAngularRequestsToFinish();
    }

    public void selectTaxAmount(String val) {
        WebElement we = waitForElement(By.xpath(String.format(INVOICE_DETAILS_TAX_AMOUNT_ITEM_XPATH + "/..", val)), State.ELEMENT_IS_CLICKABLE);
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void selectGrossAmount() {
        grossAmount.click();
        waitForAngularRequestsToFinish();
    }

    private void highlightArea(int startX, int startY, int endX, int endY) {
        Actions actions = new Actions(driver);
        actions.moveToElement(templateCanvas, startX, startY).clickAndHold().moveByOffset(endX, endY).release().perform();
        waitForAngularRequestsToFinish();
    }

    public void highlightLegalEntity() {
        highlightArea(27, 125, 80, 40);
    }

    public void highlightInvoiceNumber() {
        highlightArea(205, 60, 50, 30);
    }

    public void highlightInvoiceDate() {
        highlightArea(25, 60, 80, 30);
    }

    public void highlightNetAmount() {
        highlightArea(415, 462, 55, 20);
    }

    public void highlightTaxAmount() {
        highlightArea(415, 484, 55, 20);
    }

    public void highlightGrossAmount() {
        highlightArea(415, 532, 55, 20);
    }
}
