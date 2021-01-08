package mercury.pageobject.web.admin;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditResourcePage extends Base_Page<AdminEditResourcePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_RESOURCE_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String EDIT_RESOURCE_CONTENT_XPATH = EDIT_RESOURCE_PAGE_XPATH + "//div[@class='admin-action__content']";

    private static final String RESOURCE_STATUS_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//label[contains(text(), 'Make resource active?')]/..//label[contains(text(), '%s')]";
    private static final String DEACTIVATE_CHECKBOX_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//label[@for='ConfirmRemoved']";
    private static final String PERMANENT_SITE_ALLOCATIONS_MESSAGE_XPATH = "//p[contains(@ng-if, 'isContractor')]";
    private static final String DEACTIVATE_RESOURCE_CONFIRMATION_MESSAGE_XPATH = "//span[contains(@class, 'confirm-text')]";

    private static final String CANCEL_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String DEACTIVATE_RESOURCE_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//span[contains(text(), 'De-Activate Resource')]/..";
    private static final String SAVE_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//span[contains(text(), 'Save')]/..";

    private static final String ERROR_MESSAGE_XPATH = "//h2[contains(text(), 'Error')]//following-sibling::p";

    private static final String SITE_CONFIG_TABLE_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//table[contains(@class, 'contractor-sites')]";
    private static final String PERMANENT_SITES_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//label[contains(text(), 'Permanent sites')]//following-sibling::div//input";
    private static final String PERMANENT_SITES_TABLE_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//table[contains(@class, 'admin-form__question-row')]";
    private static final String SITE_CONFIGURATION_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//label[contains(text(), 'Site configuration')]//following-sibling::div//input";
    private static final String EDIT_SELECTED_SITE_CONFIG_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//button[contains(text(), 'Edit')]";
    private static final String REPLACE_RESOURCE_SITE_BAR_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//label[contains(@class, 'admin-form__confirmation-required')]";
    private static final String NUMBER_OF_CLASSIFICATIONS_TO_SITE_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//td[contains(text(), '%s')]//following-sibling::td";
    private static final String REMOVE_SITE_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//td[contains(text(), '%s')]//..//following-sibling::td//button[contains(text(), 'Remove')]";

    private static final String REMOVE_SITE_ALERT = "//div[contains(@class, 'sweet-alert') and contains(@class, 'showSweetAlert')]";

    private static final String SHIFT_DAY_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//div[contains(text(), '%s')]";
    private static final String SHIFT_OVERNIGHT_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//td[contains(text(), '%s')]//following-sibling::td//span[@ng-if='day.overnight!==null']";

    private static final String CALLOUT_RATES_TABLE_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//table[@id='call-out-table']";
    private static final String ADD_EXCEPTION_XPATH = CALLOUT_RATES_TABLE_XPATH + "//div[@class='add-exception__link']";
    private static final String EXCEPTION_ROW_XPATH = CALLOUT_RATES_TABLE_XPATH + "//tr[contains(@ng-repeat, 'calloutRateExceptionsDictionary')]";
    private static final String EXCEPTION_SITE_XPATH = EXCEPTION_ROW_XPATH + "//td[@class='column-header']";
    private static final String EXCEPTION_STANDARD_XPATH = EXCEPTION_ROW_XPATH + "//td[contains(@ng-class, 'standardCallout') and contains(@class, 'unavailable-background')]";
    private static final String EXCEPTION_RECALL_XPATH = EXCEPTION_ROW_XPATH + "//td[contains(@ng-class, 'recallJobRate') and contains(@class, 'unavailable-background')]";
    private static final String EXCEPTION_OUT_OF_HOURS_XPATH = EXCEPTION_ROW_XPATH + "//td[contains(@ng-class, 'oOHCallout') and contains(@class, 'unavailable-background')]";
    private static final String EXCEPTION_SUBSEQUENT_XPATH = EXCEPTION_ROW_XPATH + "//td[contains(@ng-class, 'subsequentJobRate') and contains(@class, 'unavailable-background')]";


    @FindBy(xpath = DEACTIVATE_CHECKBOX_XPATH)
    private WebElement deactivateCheckbox;

    @FindBy(xpath = DEACTIVATE_RESOURCE_CONFIRMATION_MESSAGE_XPATH)
    private WebElement deactivateConfirmationMessage;

    @FindBy(xpath = PERMANENT_SITE_ALLOCATIONS_MESSAGE_XPATH)
    private WebElement permanentSiteAllocationsMessage;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancelChanges;

    @FindBy(xpath = DEACTIVATE_RESOURCE_XPATH)
    private WebElement deactivateResource;

    @FindBy(xpath = SAVE_XPATH)
    private WebElement saveChanges;

    @FindBy(xpath = ERROR_MESSAGE_XPATH)
    private WebElement error;

    @FindBy(xpath = SITE_CONFIGURATION_XPATH)
    private WebElement siteConfig;

    @FindBy(xpath = PERMANENT_SITES_XPATH)
    private WebElement permanentSites;

    @FindBy(xpath = REPLACE_RESOURCE_SITE_BAR_XPATH)
    private WebElement replaceResourceFromSite;

    @FindBy(xpath = ADD_EXCEPTION_XPATH)
    private WebElement addException;

    @FindBy(xpath = EXCEPTION_SITE_XPATH)
    private WebElement exceptionSite;


    public AdminEditResourcePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(EDIT_RESOURCE_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void changeResourceStatus(String status) {
        WebElement newResourceStatus = driver.findElement(By.xpath(String.format(RESOURCE_STATUS_XPATH, status)));
        newResourceStatus.click();
        waitForAngularRequestsToFinish();
    }

    public void deactivateResourceConfirmation() {
        deactivateCheckbox.click();
        waitForAngularRequestsToFinish();
    }

    public void cancelChanges() {
        cancelChanges.click();
        waitForAngularRequestsToFinish();
    }

    public void deactivateResource() {
        deactivateResource.click();
        waitForAngularRequestsToFinish();
    }

    public void saveChanges() {
        saveChanges.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isSaveEnabled() {
        return this.isElementClickable(By.xpath(SAVE_XPATH));
    }

    public Boolean isReplaceResourceSiteBarDisplayed() {
        return this.isElementClickable(By.xpath(REPLACE_RESOURCE_SITE_BAR_XPATH));
    }

    public void clickReplaceResourceSiteBar() {
        replaceResourceFromSite.click();
        waitForAngularRequestsToFinish();
    }

    public String getErrorMessage() {
        return error.getText();
    }

    public String getConfirmationMessage() {
        return deactivateConfirmationMessage.getText();
    }

    public String getPermSiteAllocationsMessage() {
        return permanentSiteAllocationsMessage.getText();
    }

    public String getReplaceResourceText() {
        return replaceResourceFromSite.getText();
    }

    public Boolean isSiteConfigTableDisplayed() {
        return this.isElementPresent(By.xpath(SITE_CONFIG_TABLE_XPATH));
    }

    public Boolean isPermSiteTableDisplayed() {
        return this.isElementPresent(By.xpath(PERMANENT_SITES_TABLE_XPATH));
    }

    public Grid getPermSiteTableGrid() {
        return GridHelper.getGrid(PERMANENT_SITES_TABLE_XPATH);
    }

    public Grid getSiteConfigTableGrid() {
        return GridHelper.getGrid(SITE_CONFIG_TABLE_XPATH);
    }

    public Boolean isShiftDisplayed(String shift) {
        return this.isElementPresent(By.xpath(String.format(SHIFT_DAY_XPATH, shift)));
    }

    public Boolean isShiftOvernightDisplayed(String shift) {
        return this.isElementPresent(By.xpath(String.format(SHIFT_OVERNIGHT_XPATH, shift)));
    }

    public String getShiftSummaryText(String shift) {
        WebElement shiftSummary = driver.findElement(By.xpath(String.format(SHIFT_DAY_XPATH, shift)));
        return shiftSummary.getText();
    }

    public String getShiftOvernightText(String shift) {
        WebElement shiftOvernight = driver.findElement(By.xpath(String.format(SHIFT_OVERNIGHT_XPATH, shift)));
        return shiftOvernight.getAttribute("innerHTML");
    }

    public void selectSiteForConfiguration(String siteName) {
        waitForElement(By.xpath(SITE_CONFIGURATION_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.sendKeys(siteConfig, siteName);
        siteConfig.sendKeys(Keys.ARROW_DOWN);
        siteConfig.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public Boolean isSiteConfigurationDisplayed() {
        return this.isElementPresent(By.xpath(SITE_CONFIGURATION_XPATH));
    }

    public void selectPermanentSiteForConfiguration(String siteName) {
        waitForElement(By.xpath(PERMANENT_SITES_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.sendKeys(permanentSites, siteName);
        permanentSites.sendKeys(Keys.ARROW_DOWN);
        permanentSites.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public Boolean isPermanentSiteDisplayed() {
        return this.isElementPresent(By.xpath(PERMANENT_SITES_XPATH));
    }

    public void editSiteConfig(String siteName) {
        WebElement editSite = driver.findElement(By.xpath(String.format(EDIT_SELECTED_SITE_CONFIG_XPATH, siteName)));
        editSite.click();
        waitForAngularRequestsToFinish();
    }

    public String getNumberOfClassificationsForSite(String siteName) {
        WebElement numberOfClassifications = driver.findElement(By.xpath(String.format(NUMBER_OF_CLASSIFICATIONS_TO_SITE_XPATH, siteName)));
        return numberOfClassifications.getText().trim();
    }

    public void removeSite(String siteName) {
        WebElement removeSite = driver.findElement(By.xpath(String.format(REMOVE_SITE_XPATH, siteName)));
        removeSite.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isRemoveSiteAlertDisplayed() {
        return this.isElementPresent(By.xpath(REMOVE_SITE_ALERT));
    }

    public AdminConfigureExceptionModal addCalloutException() {
        waitForElement(By.xpath(ADD_EXCEPTION_XPATH), ELEMENT_IS_CLICKABLE);
        addException.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminConfigureExceptionModal.class).get();
    }

    public String getExceptionSite() {
        return exceptionSite.getText();
    }

    public Boolean isStandardRateBoxDisplayed() {
        return this.isElementPresent(By.xpath(EXCEPTION_STANDARD_XPATH));
    }

    public Boolean isRecallRateBoxDisplayed() {
        return this.isElementPresent(By.xpath(EXCEPTION_RECALL_XPATH));
    }

    public Boolean isOutOfHoursRateBoxDisplayed() {
        return this.isElementPresent(By.xpath(EXCEPTION_OUT_OF_HOURS_XPATH));
    }

    public Boolean isSubsequentRateBoxDisplayed() {
        return this.isElementPresent(By.xpath(EXCEPTION_SUBSEQUENT_XPATH));
    }
}
