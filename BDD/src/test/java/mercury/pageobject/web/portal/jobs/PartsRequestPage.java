package mercury.pageobject.web.portal.jobs;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class PartsRequestPage extends Base_Page<PartsRequestPage>{


    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Parts Request";

    // Page elements
    private static final String INCREASE_XPATH = "/..//span[contains(@class, 'k-i-arrow-n')]";
    private static final String DECREASE_XPATH = "/..//span[contains(@class, 'k-i-arrow-s')]";

    // Core
    private static final String PART_REQUEST_FORM_XPATH = "//form[@name='part-request']";

    private static final String PART_DROPDOWN_XPATH = PART_REQUEST_FORM_XPATH + "//label[contains(text(), 'Part')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String SUPPLIER_XPATH = PART_REQUEST_FORM_XPATH + "//select[@id='supplier']";
    private static final String PART_NOT_IN_LIST_XPATH = PART_REQUEST_FORM_XPATH + "//input[@id='RemoteFix']";
    private static final String PART_NOT_IN_LIST_LABEL_XPATH = PART_REQUEST_FORM_XPATH + "//label[contains(text(), 'Part not in list')]";
    private static final String NEW_PART_NUMBER_XPATH = PART_REQUEST_FORM_XPATH + "//input[@id='newPartNo']";
    private static final String PART_NUMBER_XPATH = PART_REQUEST_FORM_XPATH + "//input[@id='partNumber']";
    private static final String NEW_PART_DESCRIPTION_XPATH = PART_REQUEST_FORM_XPATH + "//textarea[@id='newPartDescription']";
    private static final String PART_DESCRIPTION_XPATH = PART_REQUEST_FORM_XPATH + "//textarea[@id='partDescription']";
    private static final String MANUFACTURING_REF_XPATH = PART_REQUEST_FORM_XPATH + "//input[@id='manufacturerRef']";
    private static final String MODEL_XPATH = PART_REQUEST_FORM_XPATH + "//input[@id='modelName']";
    private static final String SERIAL_NUMBER_XPATH = PART_REQUEST_FORM_XPATH + "//input[@id='serialNumber']";
    private static final String UNIT_PRICE_XPATH = PART_REQUEST_FORM_XPATH + "//label[contains(text(), 'Unit Price')]/following-sibling::span//input[contains(@style,'display: inline-block;')]";
    private static final String UNIT_PRICE_INCREASE_XPATH = UNIT_PRICE_XPATH + INCREASE_XPATH;
    private static final String UNIT_PRICE_DECREASE_XPATH = UNIT_PRICE_XPATH + DECREASE_XPATH;
    private static final String PRIOIRTY_XPATH = PART_REQUEST_FORM_XPATH + "//select[@id='priority']";
    private static final String QUANTITY_XPATH  = PART_REQUEST_FORM_XPATH + "//label[contains(text(), 'Quantity')]/following-sibling::span//input[contains(@style,'display: inline-block;')]";
    private static final String QUANTITY_INCREASE_XPATH  = QUANTITY_XPATH + INCREASE_XPATH;
    private static final String QUANTITY_DECREASE_XPATH  = QUANTITY_XPATH + DECREASE_XPATH;
    private static final String DELIVERY_METHOD_XPATH = PART_REQUEST_FORM_XPATH + "//label[contains(text(), 'Delivery Method')]/following-sibling::select";
    private static final String DELIVERY_ADDRESS_XPATH = PART_REQUEST_FORM_XPATH + "//label[contains(text(), 'Delivery Address')]/following-sibling::select";
    private static final String ADD_TO_REQUEST_LIST_XPATH = PART_REQUEST_FORM_XPATH + "//button[contains(text(), 'Add to Request List')]";
    private static final String GRID_XPATH = PART_REQUEST_FORM_XPATH + "//h3[contains(text(), 'New Parts Requests')]/parent::div";
    private static final String SAVE_REQUEST_XPATH = PART_REQUEST_FORM_XPATH +  "//button[contains(text(), 'Save Request')]";

    @FindBy(xpath = PART_REQUEST_FORM_XPATH)
    private WebElement partRequestForm;

    @FindBy(xpath = PART_DROPDOWN_XPATH)
    private WebElement part;

    @FindBy(xpath = PART_NOT_IN_LIST_XPATH)
    private WebElement partNotInList;

    @FindBy(xpath = PART_NOT_IN_LIST_LABEL_XPATH)
    private WebElement partNotInListLabel;

    @FindBy(xpath = SUPPLIER_XPATH)
    private WebElement supplier;

    @FindBy(xpath = NEW_PART_NUMBER_XPATH)
    private WebElement newPartNumber;

    @FindBy(xpath = PART_NUMBER_XPATH)
    private WebElement partNumber;

    @FindBy(xpath = NEW_PART_DESCRIPTION_XPATH)
    private WebElement newPartDescription;

    @FindBy(xpath = PART_DESCRIPTION_XPATH)
    private WebElement partDescription;

    @FindBy(xpath = MANUFACTURING_REF_XPATH)
    private WebElement manufacturingRef;

    @FindBy(xpath = MODEL_XPATH)
    private WebElement model;

    @FindBy(xpath = SERIAL_NUMBER_XPATH)
    private WebElement serialNumber;

    @FindBy(xpath = UNIT_PRICE_XPATH)
    private WebElement unitPrice;

    @FindBy(xpath = UNIT_PRICE_INCREASE_XPATH)
    private WebElement increaseUnitPrice;

    @FindBy(xpath = UNIT_PRICE_DECREASE_XPATH)
    private WebElement decreaseUnitPrice;

    @FindBy(xpath = PRIOIRTY_XPATH)
    private WebElement priority;

    @FindBy(xpath = QUANTITY_XPATH)
    private WebElement quantity;

    @FindBy(xpath = QUANTITY_INCREASE_XPATH)
    private WebElement increaseQuantity;

    @FindBy(xpath = QUANTITY_DECREASE_XPATH)
    private WebElement decreaseQuantity;

    @FindBy(xpath = DELIVERY_METHOD_XPATH)
    private WebElement deliveryMethod;

    @FindBy(xpath = DELIVERY_ADDRESS_XPATH)
    private WebElement deliveryAddress;

    @FindBy(xpath = ADD_TO_REQUEST_LIST_XPATH)
    private WebElement addToAddressList;

    @FindBy(xpath = SAVE_REQUEST_XPATH)
    private WebElement saveRequest;

    public PartsRequestPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PART_REQUEST_FORM_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    public void selectPart(String partDescription) throws InterruptedException {
        part.click();
        WebElement searchBox = waitForElement(By.xpath(VISIBLE_DROPDOWN_SEARCHBOX_XPATH), State.ELEMENT_IS_VISIBLE);
        String searchStr = partDescription.replaceAll("-.*$", "").replaceAll("\\[.*$", "");
        searchBox.sendKeys(searchStr.trim());
        waitForElement(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH), State.ELEMENT_IS_VISIBLE);
        selectExactVisibleDropdownOption(partDescription.toUpperCase());
    }

    // Page interactions
    public void selectPart(String partialPartDescription,  String partDescription) throws InterruptedException {
        part.click();
        waitForAnimation(1); // Review if needed
        this.waitForAngularRequestsToFinish();
        waitForElement(By.xpath(VISIBLE_DROPDOWN_SEARCHBOX_XPATH), State.ELEMENT_IS_VISIBLE); // Test if needed
        WebElement searchBox = driver.findElement(By.xpath(VISIBLE_DROPDOWN_SEARCHBOX_XPATH));
        searchBox.sendKeys(partialPartDescription);
        waitForElement(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH), State.ELEMENT_IS_VISIBLE);
        selectExactVisibleDropdownOption(partDescription.toUpperCase());
    }

    public void setSupplier(String option) {
        selectOptionFromSelect(supplier, option);
    }

    public void selectRandomSupplier() {
        Select drop = new Select(supplier);
        waitUntilSelectOptionsPopulated(drop);
        List<WebElement> visibleOptions = drop.getOptions();

        int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
        drop.selectByIndex(randomSelection);
    }

    public void setSupplierByValue(String value) {
        waitForAngularRequestsToFinish();
        selectValueFromSelect(supplier, value);
        waitForAngularRequestsToFinish();
    }

    public void setPartNotInList() {
        partNotInListLabel.click();
    }
    public void setPartNumber(String value) {
        newPartNumber.sendKeys(value);
    }

    public void setPartDescription(String value) {
        newPartDescription.sendKeys(value);
    }

    public void setManufacturingRef(String value) {
        manufacturingRef.sendKeys(value);
    }

    public void setModel(String value) {
        model.sendKeys(value);
    }

    public void setSerialNumber(String value) {
        serialNumber.sendKeys(value);
    }

    public void setUnitPrice(String value) {
        unitPrice.sendKeys(value);
    }

    public void increaseUnitPrice() {
        increaseUnitPrice.click();
    }

    public void decreaseUnitPrice() {
        decreaseUnitPrice.click();
    }

    public void setPriority(String option) {
        selectOptionFromSelect(priority, option);
    }

    public void setQuantity(String value) {
        quantity.sendKeys(value);
    }

    public void increaseQuantity() {
        increaseQuantity.click();
    }

    public void decreaseQuantity() {
        decreaseQuantity.click();
    }

    public void setDeliveryMethod(String option) {
        Select drop = new Select(deliveryMethod);
        drop.selectByVisibleText(option);
    }

    public void setDeliveryAddress(String option) {
        Select drop = new Select(deliveryAddress);
        drop.selectByVisibleText(option);
    }

    public void addToRequestList() {
        addToAddressList.click();
    }

    public CreatePartRequestModalPage saveRequest() {
        saveRequest.click();
        return PageFactory.initElements(driver, CreatePartRequestModalPage.class).get();
    }



    // Get values
    public String getSupplierValue() {
        Select drop = new Select(supplier);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }

    public String getSupplierText() {
        Select drop = new Select(supplier);
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public Integer getSupplierCount() {
        Select drop = new Select(supplier);
        return drop.getOptions().size();
    }

    public String getPartNumber() {
        if (isElementPresent(By.xpath(PartsRequestPage.NEW_PART_NUMBER_XPATH))) {
            return newPartNumber.getAttribute("value");
        } else {
            return partNumber.getAttribute("value"); //partNumber.getText();
        }
    }

    public String getPartDescription() {
        if (isElementPresent(By.xpath(PartsRequestPage.NEW_PART_DESCRIPTION_XPATH))) {
            return newPartDescription.getAttribute("value");
        } else {
            return partDescription.getAttribute("value"); //partDescription.getText();
        }
    }

    public String getManufactuerRef() {
        return manufacturingRef.getAttribute("value");
    }
    public String getModel() {
        return model.getAttribute("value");
    }

    public String getSerialNumber() {
        return serialNumber.getAttribute("value");
    }

    public Float getUnitPrice() {
        if (isElementPresent(By.xpath(PartsRequestPage.NEW_PART_DESCRIPTION_XPATH))) {
            return Float.valueOf(unitPrice.getAttribute("value"));
        } else {
            return (float) 0.00; // temporary workaround
        }
    }

    public String getPriorityValue() {
        Select drop = new Select(priority);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }
    public String getPriorityText() {
        Select drop = new Select(priority);
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public String getQuantity() {
        //return Math.round((Float.valueOf(quantity.getAttribute("value"))));
        return quantity.getAttribute("value");
    }

    public String getDeliveryMethodValue() {
        Select drop = new Select(deliveryMethod);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }
    public String getDeliveryMethodText() {
        Select drop = new Select(deliveryMethod);
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public String getDeliveryAddressValue() {
        Select drop = new Select(deliveryAddress);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }
    public String getDeliveryAddressText() {
        Select drop = new Select(deliveryAddress);
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public boolean getAddToListEnabled() {
        return this.addToAddressList.isEnabled();
    }
    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }
}
