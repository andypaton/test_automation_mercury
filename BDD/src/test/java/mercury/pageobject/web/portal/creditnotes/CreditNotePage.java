package mercury.pageobject.web.portal.creditnotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class CreditNotePage extends Base_Page<CreditNotePage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Credit Note";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    //Form Elements
    private static final String FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form";
    private static final String CREDIT_NOTE_NUMBER_XPATH = FORM_XPATH + "//input[@id='CreditNoteNumber']";
    private static final String CREDIT_NOTE_DATE_XPATH = FORM_XPATH + "//input[@id='CreditNoteDate']";
    private static final String NET_AMOUNT_XPATH = FORM_XPATH + "//input[@id='NetAmount']";
    private static final String TAX_AMOUNT_XPATH = FORM_XPATH + "//input[@id='TaxAmount']";
    private static final String GROSS_AMOUNT_XPATH = FORM_XPATH + "//span[@id='GrossAmount']";
    private static final String SUN_REFERENCE_XPATH = FORM_XPATH + "//input[@id='SunReference']";
    private static final String SUN_CONTRACT_CODE_XPATH = FORM_XPATH + "//select[@id='SelectedSunContractCode']";
    private static final String SUN_STORE_CODE_XPATH = FORM_XPATH + "//select[@id='SelectedSunStoreCode']";
    private static final String SUN_NOMINAL_CODE_XPATH = FORM_XPATH + "//select[@id='SelectedSunNominalCode']";
    private static final String SUN_DISCIPLINE_CODE_XPATH = FORM_XPATH + "//select[@id='SelectedSunDisciplineCode']";
    private static final String SUN_TAX_CODE_XPATH = FORM_XPATH + "//select[@id='SelectedSunTaxCode']";
    private static final String SUN_AREA_CODE_XPATH = FORM_XPATH + "//select[@id='SelectedSunAreaCode']";
    private static final String SUN_JOB_NUMBER_XPATH = FORM_XPATH + "//input[@id='SunJobNo']";

    private static final String BUTTONS_XPATH = FORM_XPATH + "//p[@class='buttons']";
    private static final String BACK_BUTTON_XPATH = BUTTONS_XPATH + "//input[@value='Back']";
    private static final String NEXT_BUTTON_XPATH = BUTTONS_XPATH + "//input[@value='Next']";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = CREDIT_NOTE_NUMBER_XPATH)
    private WebElement creditNoteNumber;

    @FindBy(xpath = CREDIT_NOTE_DATE_XPATH)
    private WebElement creditNoteDate;

    @FindBy(xpath = NET_AMOUNT_XPATH)
    private WebElement netAmount;

    @FindBy(xpath = TAX_AMOUNT_XPATH)
    private WebElement taxAmount;

    @FindBy(xpath = GROSS_AMOUNT_XPATH)
    private WebElement grossAmount;

    @FindBy(xpath = SUN_REFERENCE_XPATH)
    private WebElement sunReference;

    @FindBy(xpath = SUN_CONTRACT_CODE_XPATH)
    private WebElement sunContractCode;

    @FindBy(xpath = SUN_STORE_CODE_XPATH)
    private WebElement sunStoreCode;

    @FindBy(xpath = SUN_NOMINAL_CODE_XPATH)
    private WebElement sunNominalCode;

    @FindBy(xpath = SUN_DISCIPLINE_CODE_XPATH)
    private WebElement sunDisciplineCode;

    @FindBy(xpath = SUN_TAX_CODE_XPATH)
    private WebElement sunTaxCode;

    @FindBy(xpath = SUN_AREA_CODE_XPATH)
    private WebElement sunAreaCode;

    @FindBy(xpath = SUN_JOB_NUMBER_XPATH)
    private WebElement sunJobNumber;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement back;

    @FindBy(xpath = NEXT_BUTTON_XPATH)
    private WebElement next;

    // Page methods

    public CreditNotePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public String getPageHeaderText() {
        return pageHeaderTest.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }

    public void enterCreditNoteNumber(String text) {
        creditNoteNumber.sendKeys(text);
    }

    public String getCreditNoteDate() {
        return creditNoteDate.getAttribute("value");
    }

    public void enterNetAmount(String amount) {
        netAmount.clear();
        netAmount.sendKeys(amount);
    }

    public void enterTaxAmount(String amount) {
        taxAmount.clear();
        taxAmount.sendKeys(amount);
    }

    public String getGrossAmount() {
        return grossAmount.getText();
    }

    public void enterSunReference(String text) {
        sunReference.sendKeys(text);
    }

    public String selectSunContractCode() {
        return selectRandomOptionFromSelect(sunContractCode);
    }

    public String selectSunStoreCode() {
        return selectRandomOptionFromSelect(sunStoreCode);
    }

    public String selectSunNominalCode() {
        return selectRandomOptionFromSelect(sunNominalCode);
    }

    public String selectSunDisciplineCode() {
        return selectRandomOptionFromSelect(sunDisciplineCode);
    }

    public String selectSunTaxCode() {
        return selectRandomOptionFromSelect(sunTaxCode);
    }

    public String selectSunAreaCode() {
        return selectRandomOptionFromSelect(sunAreaCode);
    }

    public void clickNextButton() {
        next.click();
        waitForAngularRequestsToFinish();
    }
}