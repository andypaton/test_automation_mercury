package mercury.pageobject.web.portal.creditnotes;

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

public class CreateCreditNotePage extends Base_Page<CreateCreditNotePage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Create Credit Note";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    //Form Elements
    private static final String FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form";
    private static final String DROPDOWN_SUPPLIER_OR_VENDOR_XPATH = FORM_XPATH + "//select[@id='SupplierCode']";
    private static final String BACK_BUTTON_XPATH = FORM_XPATH + "//input[@id='back']";
    private static final String NEXT_BUTTON_XPATH = FORM_XPATH + "//input[@id='save']";


    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = DROPDOWN_SUPPLIER_OR_VENDOR_XPATH)
    private WebElement supplierOrVendorDropdown;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement back;

    @FindBy(xpath = NEXT_BUTTON_XPATH)
    private WebElement next;

    // Page methods

    public CreateCreditNotePage(WebDriver driver) {
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

    public String selectRandomSupplierOrVendor() {
        return selectRandomOptionFromSelect(supplierOrVendorDropdown);
    }

    public CreditNotePage clickNextButton() {
        next.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, CreditNotePage.class).get();
    }
}