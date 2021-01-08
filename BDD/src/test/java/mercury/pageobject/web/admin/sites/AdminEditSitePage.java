package mercury.pageobject.web.admin.sites;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import java.util.List;

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

public class AdminEditSitePage extends Base_Page<AdminEditSitePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Edit Site";

    private static final String ADMIN_ACTION_CONTENT_XPATH = "//div[@class='admin-action__content']";

    private static final String ADMIN_ACTION_HEADER_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[@class='admin-action__sub-header']//h2[contains(text(),'" + PAGE_TITLE + "')]";

    private static final String CORE_DETAILS_FORM_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[@ng-form='CoreDetailsForm']";
    private static final String CORE_DETAILS_FORM_HEADER_XPATH = CORE_DETAILS_FORM_XPATH + "/../../..//h3";
    private static final String CORE_DETAILS_FORM_TEXTBOX_XPATH = CORE_DETAILS_FORM_XPATH + "//label[contains(text(), '%s')]/following-sibling::input";
    private static final String CORE_DETAILS_FORM_DROPDOWN_XPATH = CORE_DETAILS_FORM_XPATH + "//label[contains(text(), '%s')]/following-sibling::span[contains(@class, 'k-dropdown')]/span";
    private static final String CORE_DETAILS_FORM_DATE_PICKERS_XPATH = CORE_DETAILS_FORM_XPATH + "//span[contains(@class, 'k-datepicker')]//input";

    private static final String ADDRESS_FORM_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[@ng-form='AddressForm']";
    private static final String ADDRESS_FORM_HEADER_XPATH = ADDRESS_FORM_XPATH + "/../../..//h3";
    private static final String ADDRESS_FORM_COLLAPSED_XPATH = ADDRESS_FORM_XPATH + "/../../..//div[@ng-show='$ctrl.addressDisplayed' and contains(@class, 'ng-hide')]";
    private static final String ADDRESS_FORM_TEXTBOX_XPATH = ADDRESS_FORM_XPATH + "//label[contains(text(), '%s')]/following-sibling::input";
    private static final String ADDRESS_FORM_DROPDOWN_XPATH = ADDRESS_FORM_XPATH + "//label[contains(text(), '%s')]/following-sibling::span[contains(@class, 'k-dropdown')]/span";
    private static final String PHONE_NUMBER_ERROR_XPATH = ADDRESS_FORM_XPATH + "//div[ contains(@class, 'error') and contains(text(), 'Phone numbers ')]";
    private static final String REMOVE_CONTACT_INFO_XPATH = ADDRESS_FORM_XPATH + "//div/i[contains(@class, 'contact-info__remove')]";

    private static final String GEOGRAPHICAL_INFORMATION_FORM_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[@ng-form='LocationForm']";
    private static final String GEOGRAPHICAL_INFORMATION_FORM_HEADER_XPATH = GEOGRAPHICAL_INFORMATION_FORM_XPATH + "/../../..//h3";
    private static final String GEOGRAPHICAL_INFORMATION_FORM_TEXTBOX_XPATH = GEOGRAPHICAL_INFORMATION_FORM_XPATH + "//label[contains(text(), '%s')]/following-sibling::input";
    private static final String GEOGRAPHICAL_INFORMATION_FORM_DROPDOWN_XPATH = GEOGRAPHICAL_INFORMATION_FORM_XPATH + "//label[contains(text(), '%s')]/following-sibling::span[contains(@class, 'k-dropdown')]/span";

    private static final String OPENING_HOURS_FORM_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[@ng-form='OpeningHoursForm']";
    private static final String OPENING_HOURS_FORM_HEADER_XPATH = OPENING_HOURS_FORM_XPATH + "/../../../..//h3";
    private static final String OPENING_HOURS_FORM_24_HRS_CHECKBOX_XPATH = OPENING_HOURS_FORM_XPATH + "//md-checkbox[contains(@aria-label, '24 Hrs')]";


    private static final String FOOTER_BUTTON_BAR_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[@class='footer-button-bar']";
    private static final String CANCEL_BUTTON_XPATH = FOOTER_BUTTON_BAR_XPATH + "//button[contains(text(),'Cancel')]";
    private static final String SAVE_BUTTON_XPATH = FOOTER_BUTTON_BAR_XPATH + "//button[contains(text(),'Save')]";


    @FindBy(xpath = ADDRESS_FORM_HEADER_XPATH)
    private WebElement addressFormHeader;

    @FindBy(xpath = GEOGRAPHICAL_INFORMATION_FORM_HEADER_XPATH)
    private WebElement geographicalInformationFormHeader;

    @FindBy(xpath = OPENING_HOURS_FORM_HEADER_XPATH)
    private WebElement openingHoursFormHeader;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    @FindBy(xpath = REMOVE_CONTACT_INFO_XPATH) private WebElement removeContactInfo;

    public AdminEditSitePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ADMIN_ACTION_HEADER_XPATH));
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterValueInCoreDetailsFormTextBox(String textboxName, String text) {
        WebElement textbox = driver.findElement(By.xpath(String.format(CORE_DETAILS_FORM_TEXTBOX_XPATH, textboxName)));
        textbox.clear();
        textbox.sendKeys(text);
    }

    public String selectRandomOptionFromCoreDetailsFormDropdown(String dropDownName) {
        WebElement dropDown = driver.findElement(By.xpath(String.format(CORE_DETAILS_FORM_DROPDOWN_XPATH, dropDownName)));
        dropDown.click();
        return selectRandomVisibleDropdownOption();
    }

    public void selectOptionFromCoreDetailsFormDropdown(String dropDownName, String option) {
        WebElement dropDown = driver.findElement(By.xpath(String.format(CORE_DETAILS_FORM_DROPDOWN_XPATH, dropDownName)));
        POHelper.scrollToElement(dropDown);
        POHelper.clickJavascript(dropDown);
        selectExactVisibleDropdownOption(option);
    }

    public void clearCoreDetailsFormDates() {
        List<WebElement> datePickers = driver.findElements(By.xpath(CORE_DETAILS_FORM_DATE_PICKERS_XPATH));
        for (WebElement datePicker : datePickers) {
            datePicker.clear();
            waitForAngularRequestsToFinish();
        }
    }

    public boolean isAddressFormCollapsed() {
        return isElementPresent(By.xpath(ADDRESS_FORM_COLLAPSED_XPATH));
    }

    public void openAddressForm() {
        addressFormHeader.click();
        waitForAngularRequestsToFinish();
    }

    public void enterValueInAddressFormTextBox(String textboxName, String text) {
        WebElement textbox = driver.findElement(By.xpath(String.format(ADDRESS_FORM_TEXTBOX_XPATH, textboxName)));
        textbox.clear();
        textbox.sendKeys(text);
        waitForAngularRequestsToFinish();
    }

    public String selectRandomOptionFromAddressFormDropdown(String dropDownName) {
        WebElement dropDown = driver.findElement(By.xpath(String.format(ADDRESS_FORM_DROPDOWN_XPATH, dropDownName)));
        dropDown.click();
        return selectRandomVisibleDropdownOption();
    }

    public void selectOptionFromAddressFormDropdown(String dropDownName, String option) {
        WebElement dropDown = driver.findElement(By.xpath(String.format(ADDRESS_FORM_DROPDOWN_XPATH, dropDownName)));
        dropDown.click();
        selectExactVisibleDropdownOption(option);
    }

    public void openGeographicalInformationForm() {
        POHelper.scrollToElement(geographicalInformationFormHeader);
        POHelper.clickJavascript(geographicalInformationFormHeader);
        waitForAngularRequestsToFinish();
    }

    public void enterValueInGeographicalInformationFormTextBox(String textboxName, String text) {
        WebElement textbox = driver.findElement(By.xpath(String.format(GEOGRAPHICAL_INFORMATION_FORM_TEXTBOX_XPATH, textboxName)));
        textbox.clear();
        textbox.sendKeys(text);
    }

    public String selectRandomOptionFromGeographicalInformationFormDropdown(String dropDownName) {
        WebElement dropDown = driver.findElement(By.xpath(String.format(GEOGRAPHICAL_INFORMATION_FORM_DROPDOWN_XPATH, dropDownName)));
        dropDown.click();
        return selectRandomVisibleDropdownOption();
    }

    public void openOpeningHoursForm() {
        openingHoursFormHeader.click();
        waitForAngularRequestsToFinish();
    }

    public void selectAll24HrsCheckboxesInOpeningHoursForm() {
        List<WebElement> checkboxes = driver.findElements(By.xpath(OPENING_HOURS_FORM_24_HRS_CHECKBOX_XPATH));
        for (WebElement checkbox : checkboxes) {
            checkbox.click();
        }
    }

    public void clickSaveButton() {
        waitForElement(By.xpath(SAVE_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(save);
        waitForJavascriptToLoad();
    }

    public AdminSitesPage clickCancelButton() {
        cancel.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminSitesPage.class).get();
    }

    public boolean isSaveButtonEnabled() {
        return isElementClickable(By.xpath(SAVE_BUTTON_XPATH));
    }

    public boolean isPhoneNumberErrorDisplayed() {
        return isElementPresent(By.xpath(PHONE_NUMBER_ERROR_XPATH));
    }

    public void removeContactInfo() {
        POHelper.clickJavascript(removeContactInfo);
        waitForAngularRequestsToFinish();
    }

    public void checkAndConfirmAlert() {
        checkForAndConfirmAlert();
    }


}