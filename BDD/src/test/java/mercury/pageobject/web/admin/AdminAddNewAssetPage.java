package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class AdminAddNewAssetPage extends Base_Page<AdminAddNewAssetPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADD_NEW_ASSET_XPATH = "//div[@ng-app='assetRegisterAdmin']";

    //Asset Info
    private static final String ASSET_INFO_SECTION_XPATH = ADD_NEW_ASSET_XPATH + "//h2[contains(text(), 'Asset Info')]/..";
    private static final String ASSET_TAG_XPATH = ASSET_INFO_SECTION_XPATH + "//input[@id='assetTag']";
    private static final String ASSET_TYPE_DROPDOWN_XPATH = ASSET_INFO_SECTION_XPATH + "//label[@for='assetTypeId']/..//span[@class='k-select']";
    private static final String ASSET_SUBTYPE_DROPDOWN_XPATH = ASSET_INFO_SECTION_XPATH + "//label[@for='assetSubTypeId']/..//span[@class='k-select']";
    private static final String ASSET_CLASSIFICATION_DROPDOWN_XPATH = ASSET_INFO_SECTION_XPATH + "//label[@for='assetClassificationId']/..//span[@class='k-select']";

    //Location
    private static final String LOCATION_SECTION_XPATH = ADD_NEW_ASSET_XPATH + "//h2[contains(text(), 'Location')]/..";
    private static final String SITE_DROPDOWN_XPATH = LOCATION_SECTION_XPATH + "//label[@for='siteId']/..//span[@class='k-select']";
    private static final String AREA_DROPDOWN_XPATH = LOCATION_SECTION_XPATH + "//label[@for='areaId']/..//span[@class='k-select']";
    private static final String LOCATION_DROPDOWN_XPATH = LOCATION_SECTION_XPATH + "//label[@for='midLocationId']/..//span[@class='k-select']";
    private static final String SUB_LOCATION_DROPDOWN_XPATH = LOCATION_SECTION_XPATH + "//label[@for='subLocationId']/..//span[@class='k-select']";

    //Power
    private static final String POWER_SECTION_XPATH = ADD_NEW_ASSET_XPATH + "//h2[contains(text(), 'Power')]/..";
    private static final String POWERED_BY_ELECTRICITY_CHECKBOX_XPATH = POWER_SECTION_XPATH + "//input[@id='electricity']";
    private static final String POWERED_BY_GAS_CHECKBOX_XPATH = POWER_SECTION_XPATH + "//input[@id='gas']";
    private static final String POWERED_BY_OIL_CHECKBOX_XPATH = POWER_SECTION_XPATH + "//input[@id='oil']";

    //Manufacturer
    private static final String MANUFACTURER_SECTION_XPATH = ADD_NEW_ASSET_XPATH + "//h2[contains(text(), 'Manufacturer')]/..";
    private static final String MANUFACTURER_DROPDOWN_XPATH = MANUFACTURER_SECTION_XPATH + "//label[@for='manufacturerId']/..//span[@class='k-select']";
    private static final String MODEL_NAME_XPATH = MANUFACTURER_SECTION_XPATH + "//input[@id='modelName']";
    private static final String MODEL_NUMBER_XPATH = MANUFACTURER_SECTION_XPATH + "//input[@id='modelNumber']";
    private static final String SERIAL_NUMBER_XPATH = MANUFACTURER_SECTION_XPATH + "//input[@id='serialNo']";

    //Condition / Warranty
    private static final String CONDITION_WARRANTY_SECTION_XPATH = ADD_NEW_ASSET_XPATH + "//h2[contains(text(), 'Condition / Warranty')]/..";
    private static final String LIFE_CYCLE_COSTING_XPATH = CONDITION_WARRANTY_SECTION_XPATH + "//input[@id='lifeCycleCosting']";

    //Notes
    private static final String NOTES_SECTION_XPATH = ADD_NEW_ASSET_XPATH + "//h2[contains(text(), 'Notes')]/..";
    private static final String NEW_NOTE_XPATH = NOTES_SECTION_XPATH + "//textarea[@id='notes']";
    private static final String SAVE_NOTE_BUTTON_XPATH = NOTES_SECTION_XPATH + "//button[@ng-click='AssetForm.saveNote()']";

    //Footer
    private static final String FOOTER_SECTION_XPATH = ADD_NEW_ASSET_XPATH + "//div[@class='footer-buttons-bar']";
    private static final String SAVE_BUTTON_XPATH = FOOTER_SECTION_XPATH + "//button[@class='btn btn-primary']";
    private static final String BACK_BUTTON_XPATH = FOOTER_SECTION_XPATH + "//button[@class='btn btn-neutral']";


    @FindBy(xpath = ASSET_TAG_XPATH)
    private WebElement assetTag;

    @FindBy(xpath = ASSET_TYPE_DROPDOWN_XPATH)
    private WebElement assetTypeDropdown;

    @FindBy(xpath = ASSET_SUBTYPE_DROPDOWN_XPATH)
    private WebElement assetSubtypeDropdown;

    @FindBy(xpath = ASSET_CLASSIFICATION_DROPDOWN_XPATH)
    private WebElement assetClassificationDropdown;

    @FindBy(xpath = SITE_DROPDOWN_XPATH)
    private WebElement siteDropdown;

    @FindBy(xpath = AREA_DROPDOWN_XPATH)
    private WebElement areaDropdown;

    @FindBy(xpath = LOCATION_DROPDOWN_XPATH)
    private WebElement locationDropdown;

    @FindBy(xpath = SUB_LOCATION_DROPDOWN_XPATH)
    private WebElement subLocationDropdown;

    @FindBy(xpath = POWERED_BY_ELECTRICITY_CHECKBOX_XPATH)
    private WebElement poweredByElectricity;

    @FindBy(xpath = POWERED_BY_GAS_CHECKBOX_XPATH)
    private WebElement poweredByGas;

    @FindBy(xpath = POWERED_BY_OIL_CHECKBOX_XPATH)
    private WebElement poweredByOil;

    @FindBy(xpath = MANUFACTURER_DROPDOWN_XPATH)
    private WebElement manufacturerDropdown;

    @FindBy(xpath = MODEL_NAME_XPATH)
    private WebElement modelName;

    @FindBy(xpath = MODEL_NUMBER_XPATH)
    private WebElement modelNumber;

    @FindBy(xpath = SERIAL_NUMBER_XPATH)
    private WebElement serialNumber;

    @FindBy(xpath = LIFE_CYCLE_COSTING_XPATH)
    private WebElement lifeCycleCosting;

    @FindBy(xpath = NEW_NOTE_XPATH)
    private WebElement newNote;

    @FindBy(xpath = SAVE_NOTE_BUTTON_XPATH)
    private WebElement saveNote;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement back;


    public AdminAddNewAssetPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ADD_NEW_ASSET_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterAssetTag(String text) {
        assetTag.sendKeys(text);
    }

    public String selectRandomAssetType() {
        assetTypeDropdown.click();
        waitForAngularRequestsToFinish();
        String assetType = selectRandomVisibleDropdownOption();
        return assetType;
    }

    public void selectAssetType(String assetType) {
        assetTypeDropdown.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(assetType);
    }

    public String selectRandomAssetSubtype() {
        assetSubtypeDropdown.click();
        waitForAngularRequestsToFinish();
        String assetSubtype = selectRandomVisibleDropdownOption();
        return assetSubtype;
    }

    public void selectAssetSubtype(String assetSubtype) {
        assetSubtypeDropdown.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(assetSubtype);
    }

    public String selectRandomAssetClassification() {
        assetClassificationDropdown.click();
        waitForAngularRequestsToFinish();
        String assetClassification = selectRandomVisibleDropdownOption();
        return assetClassification;
    }

    public void selectAssetClassification(String assetClassification) {
        assetClassificationDropdown.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(assetClassification);
    }

    public String selectRandomSite() {
        siteDropdown.click();
        waitForAngularRequestsToFinish();
        String site = selectRandomVisibleDropdownOption();
        return site;
    }

    public void selectRandomArea() {
        areaDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public String selectRandomLocation() {
        locationDropdown.click();
        waitForAngularRequestsToFinish();
        String location = selectRandomVisibleDropdownOption();
        return location;
    }

    public void selectRandomSubLocation() {
        subLocationDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void selectPoweredByElectricity() {
        poweredByElectricity.click();
        waitForAngularRequestsToFinish();
    }

    public void selectPoweredByGas() {
        poweredByGas.click();
        waitForAngularRequestsToFinish();
    }

    public void selectPoweredByOil() {
        poweredByOil.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRandomManufacturer() {
        manufacturerDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public void enterModelName(String text) {
        modelName.sendKeys(text);
    }

    public void enterModelNumber(String text) {
        modelNumber.sendKeys(text);
    }

    public void enterSerialNumber(String text) {
        serialNumber.sendKeys(text);
    }

    public void enterLifeCycleCosting(String text) {
        lifeCycleCosting.sendKeys(text);
    }

    public void enterNote(String text) {
        newNote.sendKeys(text);
    }

    public void clickSaveNoteButton() {
        saveNote.click();
        waitForAngularRequestsToFinish();
    }

    public void clickSaveButton() {
        save.click();
        waitForAngularRequestsToFinish();
    }

}
