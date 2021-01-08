package mercury.pageobject.web.portal.quotes;

import static mercury.helpers.Globalisation.localize;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class AddQuoteLinePage extends Base_Page<AddQuoteLinePage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Regiserer Quote";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_MODAL_CSS = " #NewLineModal";

    private static final String NEW_LINE_MODAL_FORM_CSS = " #QuoteLineForm";
    private static final String NEW_LINE_MODL_FORM_XPATH = ".//*[@id='QuoteLineForm']";

    private static final String LINE_TYPE_CSS = NEW_LINE_MODAL_FORM_CSS + " #QuoteLineTypeValue";
    private static final String LINE_PART_NUMBER_CSS = NEW_LINE_MODAL_FORM_CSS + " #PartNumber";
    private static final String LINE_PART_CODE_CSS = NEW_LINE_MODAL_FORM_CSS + " input[name=PartCode_input]";
    private static final String LINE_PART_NOT_IN_LIST_LABEL_CSS = NEW_LINE_MODAL_FORM_CSS + " label[for='PartCodeNotInListCheckBox']";
    private static final String LINE_RESOURCE_PROFILE_SEARCH_ARROW_XPATH = NEW_LINE_MODL_FORM_XPATH + "//label[contains(text(), 'Resource Profile')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String LINE_SUPPLIER_SEARCH_ARROW_XPATH = NEW_LINE_MODL_FORM_XPATH + "//label[contains(text(), 'Supplier')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String LINE_ITEM_SEARCH_ARROW_XPATH = NEW_LINE_MODL_FORM_XPATH + "//label[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '%s')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String LINE_DESCRIPTION_CSS = NEW_LINE_MODAL_FORM_CSS +  " #Description";
    private static final String LINE_QUANTITY_CSS  = NEW_LINE_MODAL_FORM_CSS +  " #Quantity";
    private static final String LINE_UNIT_PRICE_CSS = NEW_LINE_MODAL_FORM_CSS + " #UnitPrice";
    private static final String LINE_VALUE_CSS = NEW_LINE_MODAL_FORM_CSS + " #LineValue";

    private static final String ADD_NEW_LINE_CSS = PAGE_MODAL_CSS + " #AddNewLine";
    private static final String CANCEL_NEW_LINE_CSS = PAGE_MODAL_CSS + " button.btn.btn-secondary.btn-destructive ";

    private static final String LINE_TYPE_ERROR_CSS = NEW_LINE_MODAL_FORM_CSS + " #QuoteLineTypeValue-error";
    private static final String LINE_PART_NUMBER_ERROR_CSS = NEW_LINE_MODAL_FORM_CSS + " #PartNumber-error";
    private static final String LINE_DESCRIPTION_ERROR_CSS = NEW_LINE_MODAL_FORM_CSS + " #Description-error";
    private static final String LINE_QUANTITY_ERROR_CSS = NEW_LINE_MODAL_FORM_CSS + " #Quantity-error";
    private static final String LINE_UNIT_PRICE_ERROR_CSS = NEW_LINE_MODAL_FORM_CSS + " #UnitPrice-error";


    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = LINE_TYPE_CSS)
    private WebElement lineType;

    @FindBy(css = LINE_PART_NUMBER_CSS)
    private WebElement linePartNumber;

    @FindBy(css = LINE_PART_CODE_CSS)
    private WebElement linePartCode;

    @FindBy(css = LINE_PART_NOT_IN_LIST_LABEL_CSS)
    private WebElement partNotInListLabel;

    @FindBy(xpath = LINE_RESOURCE_PROFILE_SEARCH_ARROW_XPATH)
    private WebElement resourceProfileSearchArrow;

    @FindBy(xpath = LINE_SUPPLIER_SEARCH_ARROW_XPATH)
    private WebElement supplierSearchArrow;

    @FindBy(css = LINE_DESCRIPTION_CSS)
    private WebElement lineDescription;

    @FindBy(css = LINE_QUANTITY_CSS)
    private WebElement lineQuantity;

    @FindBy(css = LINE_UNIT_PRICE_CSS)
    private WebElement lineUnitPrice;

    @FindBy(css = LINE_VALUE_CSS)
    private WebElement lineValue;

    @FindBy(css = LINE_TYPE_ERROR_CSS)
    private WebElement lineTypeError;

    @FindBy(css = LINE_PART_NUMBER_ERROR_CSS)
    private WebElement linePartNumberError;

    @FindBy(css = LINE_DESCRIPTION_ERROR_CSS)
    private WebElement lineDescriptionError;

    @FindBy(css = LINE_QUANTITY_ERROR_CSS)
    private WebElement lineQuantityError;

    @FindBy(css = LINE_UNIT_PRICE_ERROR_CSS)
    private WebElement lineUnitPriceError;

    @FindBy(xpath = NEW_LINE_MODAL_FORM_CSS)
    private WebElement lineSupplierError;

    @FindBy(css = ADD_NEW_LINE_CSS)
    private WebElement addNewLine;

    @FindBy(css = CANCEL_NEW_LINE_CSS)
    private WebElement cancelNewLine;

    public AddQuoteLinePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            isElementPresent(By.cssSelector(ADD_NEW_LINE_CSS));
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
        return this.isElementClickable(By.cssSelector(ADD_NEW_LINE_CSS));
    }

    // Page Interactions

    public void setLineTypeByText(String option) {
        selectOptionFromSelect(lineType, option);
    }

    public void setPartNumber(String keysToSend) {
        linePartNumber.sendKeys(keysToSend);
    }

    public void setPartCode(String partCode,  String partDescription) throws InterruptedException {
        linePartCode.sendKeys(partCode);
        waitForAnimation(1);
        waitForElement(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH), State.ELEMENT_IS_VISIBLE);
        selectExactVisibleDropdownOption(partCode + " - " + partDescription.toUpperCase());
        waitForAngularRequestsToFinish();
    }

    public void setPartNotInList() {
        By by = By.cssSelector(LINE_PART_NOT_IN_LIST_LABEL_CSS);
        waitForElement(by, State.ELEMENT_IS_VISIBLE);
        waitForElement(by, State.ELEMENT_IS_CLICKABLE);
        WebElement partNotInListLabel = driver.findElement(by);
        partNotInListLabel.click();
        waitForAngularRequestsToFinish();
    }

    public void selectResourceType(String option) {
        waitForElement(By.xpath(LINE_RESOURCE_PROFILE_SEARCH_ARROW_XPATH), State.ELEMENT_IS_VISIBLE);
        waitForElement(By.xpath(LINE_RESOURCE_PROFILE_SEARCH_ARROW_XPATH), State.ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(resourceProfileSearchArrow);
        waitForAngularRequestsToFinish();
        selectExactVisibleDropdownOption(option);
        waitForAngularRequestsToFinish();
    }

    public String selectRandomResourceType() {
        waitForElement(By.xpath(LINE_RESOURCE_PROFILE_SEARCH_ARROW_XPATH), State.ELEMENT_IS_VISIBLE);
        waitForElement(By.xpath(LINE_RESOURCE_PROFILE_SEARCH_ARROW_XPATH), State.ELEMENT_IS_CLICKABLE);
        POHelper.scrollToElement(resourceProfileSearchArrow);
        resourceProfileSearchArrow.click();
        String resource = selectRandomVisibleDropdownOption(1);
        waitForAngularRequestsToFinish();
        return resource;
    }

    public void selectLabourType(String option) {
        By by = By.xpath(String.format(LINE_ITEM_SEARCH_ARROW_XPATH, localize("Labour Type").toLowerCase()));
        waitForElement(by, State.ELEMENT_IS_VISIBLE);
        waitForElement(by, State.ELEMENT_IS_CLICKABLE);
        WebElement labourTypeSearchArrow = driver.findElement(by);
        labourTypeSearchArrow.click();
        waitForAngularRequestsToFinish();
        selectExactVisibleDropdownOption(option);
        waitForAngularRequestsToFinish();
    }

    public String selectRandomLabourType() {
        By by = By.xpath(String.format(LINE_ITEM_SEARCH_ARROW_XPATH, localize("Labour Type").toLowerCase()));
        waitForElement(by, State.ELEMENT_IS_VISIBLE);
        waitForElement(by, State.ELEMENT_IS_CLICKABLE);
        WebElement labourTypeSearchArrow = driver.findElement(by);
        labourTypeSearchArrow.click();
        String labourType = selectRandomVisibleDropdownOption();
        return labourType;
    }

    public String selectRandomSupplier() {
        By by = By.xpath(LINE_SUPPLIER_SEARCH_ARROW_XPATH);
        waitForElement(by, State.ELEMENT_IS_VISIBLE);
        waitForElement(by, State.ELEMENT_IS_CLICKABLE);
        WebElement supplierSearchArrow = driver.findElement(by);
        supplierSearchArrow.click();
        String supplier = selectRandomVisibleDropdownOption();
        return supplier;
    }

    public String setLineDescription(String keysToSend) {
        lineDescription.sendKeys(keysToSend);
        return lineDescription.getText();
    }

    public void setQuantity(String keysToSend) {
        lineQuantity.sendKeys(keysToSend);
    }

    public void setQuantity(Integer keysToSend) {
        lineQuantity.sendKeys(keysToSend.toString());
    }

    public void setUnitPrice(Float keysToSend) {
        lineUnitPrice.sendKeys(keysToSend.toString());
        lineUnitPrice.sendKeys(Keys.TAB);
    }

    public void setUnitPrice(String keysToSend) {
        lineUnitPrice.sendKeys(keysToSend);
    }

    public void addLine() {
        POHelper.clickJavascript(addNewLine);
        waitForAngularRequestsToFinish();
    }

    public void cancelLine() {
        cancelNewLine.click();
        waitForAngularRequestsToFinish();
    }

    // Get page values
    public String getLineType() {
        return this.lineType.getAttribute("value");
    }

    public String getLinePartCode() {
        return this.linePartCode.getAttribute("value");
    }

    public String getLinePartNumber() {
        return this.linePartNumber.getAttribute("value");
    }

    public String getLineDescription() {
        return this.lineDescription.getAttribute("value");
    }

    public String getLineQuantity() {
        return this.lineQuantity.getAttribute("value");
    }

    public String getLineUnitPrice() {
        return this.lineUnitPrice.getAttribute("value");
    }

    public String getLineValue() {
        return this.lineValue.getText();
    }

    public Boolean isLineTypeErrorDisplayed() {
        return this.lineTypeError.isDisplayed();
    }
    public Boolean isPartNumberErrorDisplayed() {
        return this.linePartNumberError.isDisplayed();
    }
    public Boolean isLineDescriptionErrorDisplayed() {
        return this.lineDescriptionError.isDisplayed();
    }
    public Boolean isLineQuantityErrorDisplayed() {
        return this.lineQuantityError.isDisplayed();
    }
    public Boolean isLineUnitPriceErrorDisplayed() {
        return this.lineUnitPriceError.isDisplayed();
    }
    public Boolean isLineSupplierErrorDisplayed() {
        return this.lineSupplierError.isDisplayed();
    }

    public String getLineTypeErrorMessage() {
        return this.lineTypeError.getText();
    }
    public String getLinePartNumberErrorMessage() {
        return this.linePartNumberError.getText();
    }
    public String getLineDescriptionErrorMessage() {
        return this.lineDescriptionError.getText();
    }
    public String getLineQuantityErrorMessage() {
        return this.lineQuantityError.getText();
    }
    public String getLineUnitPriceErrorMessage() {
        return this.lineUnitPriceError.getText();
    }
    public String getLineSupplierErrorMessage() {
        return this.lineSupplierError.getText();
    }

}
