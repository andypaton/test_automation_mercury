package mercury.pageobject.web.admin;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditAssetPage extends Base_Page<AdminEditAssetPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_ASSET_XPATH = "//div[@ng-app='assetRegisterAdmin']";

    //Manufacturer
    private static final String MANUFACTURER_SECTION_XPATH = EDIT_ASSET_XPATH + "//h2[contains(text(), 'Manufacturer')]/..";
    private static final String CLIENT_ASSET_REFERENCE_XPATH = MANUFACTURER_SECTION_XPATH + "//input[@id='clientAssetRef']";

    //Status
    private static final String STATUS_SECTION_XPATH = EDIT_ASSET_XPATH + "//h2[contains(text(), 'Status')]/..";
    private static final String STATUS_XPATH = STATUS_SECTION_XPATH + "//button[contains(text(), '%s')]";
    private static final String CALENDAR_ICON_XPATH = STATUS_SECTION_XPATH + "//span[@class='k-icon k-i-calendar']";
    private static final String STATUS_DATE_XPATH = "//a[@title='%s']";
    private static final String OUT_FOR_REFURBISHMENT_CONTRACTOR_DROPDOWN_XPATH = STATUS_SECTION_XPATH + "//label[@for='outForRefurbishmentResourceId']/..//span[@class='k-select']";
    private static final String OUT_FOR_REPAIR_RESOURCE_DROPDOWN_XPATH = STATUS_SECTION_XPATH + "//label[@for='outForRepairResourceId']/..//span[@class='k-select']";
    private static final String TRANSFER_SITE_DROPDOWN_XPATH = STATUS_SECTION_XPATH + "//label[@for='transferSiteId']/..//span[@class='k-select']";
    private static final String TRANSFER_REASON_DROPDOWN_XPATH = STATUS_SECTION_XPATH + "//label[@for='transferReasonId']/..//span[@class='k-select']";
    private static final String FOR_SALE_AUTHORIZED_BY_TEXTBOX_XPATH = STATUS_SECTION_XPATH + "//input[@id='forSaleAuthorisedBy']";
    private static final String OFF_HIRE_REFERENCE_TEXTBOX_XPATH = STATUS_SECTION_XPATH + "//input[@id='offHireOffHireRef']";
    private static final String IN_STORAGE_REASON_DROPDOWN_XPATH = STATUS_SECTION_XPATH + "//label[@for='inStorageReasonId']/..//span[@class='k-select']";
    private static final String MERGE_REASON_DROPDOWN_XPATH = STATUS_SECTION_XPATH + "//label[@for='mergedReasonId']/..//span[@class='k-select']";
    private static final String MERGE_ASSET_TO_MERGE_DROPDOWN_XPATH = STATUS_SECTION_XPATH + "//label[@for='mergedAssetToMergeId']/..//span[@class='k-select']";
    private static final String MERGE_EVIDENCE_TEXTBOX_XPATH = STATUS_SECTION_XPATH + "//input[@id='mergedEvidence']";
    private static final String DECOMMISSION_REQUESTED_BY_TEXTBOX_XPATH = STATUS_SECTION_XPATH + "//input[@id='decommissionRequestedBy']";
    private static final String DISPOSE_REQUESTED_BY_TEXTBOX_XPATH = STATUS_SECTION_XPATH + "//input[@id='adminDisposeRequestedBy']";
    private static final String DISPOSE_REASON_DROPDOWN_XPATH = STATUS_SECTION_XPATH + "//label[@for='adminDisposeReasonId']/..//span[@class='k-select']";
    private static final String CHANGE_STATUS_BUTTON_XPATH = STATUS_SECTION_XPATH + "//div[@class='asset-status-change-form']//button[contains(text(), '%s')]";

    //Footer
    private static final String FOOTER_SECTION_XPATH = EDIT_ASSET_XPATH + "//div[@class='footer-buttons-bar']";
    private static final String SAVE_BUTTON_XPATH = FOOTER_SECTION_XPATH + "//button[@class='btn btn-primary']";
    private static final String BACK_BUTTON_XPATH = FOOTER_SECTION_XPATH + "//button[@class='btn btn-neutral']";

    //Audit History
    private static final String AUDIT_HISTORY_SECTION_XPATH = EDIT_ASSET_XPATH + "//h2[contains(text(), 'Audit History')]/..";
    private static final String EXPAND_AUDIT_HISTORY_SECTION_XPATH = AUDIT_HISTORY_SECTION_XPATH + "//i[@class='fa fa-plus-circle']";
    private static final String AUDIT_HISTORY_EVENT_XPATH = AUDIT_HISTORY_SECTION_XPATH + "/..//span[contains(text(), 'Asset Client Asset reference was changed')]";


    @FindBy(xpath = CLIENT_ASSET_REFERENCE_XPATH)
    private WebElement clientAssetReference;

    @FindBy(xpath = CALENDAR_ICON_XPATH)
    private WebElement calendarIcon;

    @FindBy(xpath = OUT_FOR_REFURBISHMENT_CONTRACTOR_DROPDOWN_XPATH)
    private WebElement outForRefurbishmentContractorDropdown;

    @FindBy(xpath = OUT_FOR_REPAIR_RESOURCE_DROPDOWN_XPATH)
    private WebElement outForRepairResourceDropdown;

    @FindBy(xpath = TRANSFER_SITE_DROPDOWN_XPATH)
    private WebElement transferSiteDropdown;

    @FindBy(xpath = TRANSFER_REASON_DROPDOWN_XPATH)
    private WebElement transferReasonDropdown;

    @FindBy(xpath = FOR_SALE_AUTHORIZED_BY_TEXTBOX_XPATH)
    private WebElement forSaleAuthorizedBy;

    @FindBy(xpath = OFF_HIRE_REFERENCE_TEXTBOX_XPATH)
    private WebElement offHireReference;

    @FindBy(xpath = IN_STORAGE_REASON_DROPDOWN_XPATH)
    private WebElement inStorageReasonDropdown;

    @FindBy(xpath = MERGE_REASON_DROPDOWN_XPATH)
    private WebElement mergeReasonDropdown;

    @FindBy(xpath = MERGE_ASSET_TO_MERGE_DROPDOWN_XPATH)
    private WebElement mergeAssetToMergeDropdown;

    @FindBy(xpath = MERGE_EVIDENCE_TEXTBOX_XPATH)
    private WebElement mergeEvidence;

    @FindBy(xpath = DECOMMISSION_REQUESTED_BY_TEXTBOX_XPATH)
    private WebElement decommissionRequestedBy;

    @FindBy(xpath = DISPOSE_REQUESTED_BY_TEXTBOX_XPATH)
    private WebElement disposeRequestedBy;

    @FindBy(xpath = DISPOSE_REASON_DROPDOWN_XPATH)
    private WebElement disposeReasonDropdown;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement back;

    @FindBy(xpath = EXPAND_AUDIT_HISTORY_SECTION_XPATH)
    private WebElement expandAuditHistory;

    @FindBy(xpath = AUDIT_HISTORY_EVENT_XPATH)
    private WebElement auditHistoryEvent;


    public AdminEditAssetPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(EDIT_ASSET_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterClientAssetReference(String text) {
        clientAssetReference.sendKeys(text);
    }

    public void selectNewStatus(String status) {
        WebElement we = driver.findElement(By.xpath(String.format(STATUS_XPATH, status)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void changeStatusDate(String date) {
        calendarIcon.click();
        waitForKendoLoadingToComplete();
        WebElement we = waitForElement(By.xpath(String.format(STATUS_DATE_XPATH, date)), ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(we);
    }

    public void clickSaveButton() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public void expandAuditHistorySection() {
        expandAuditHistory.click();
        waitForAngularRequestsToFinish();
    }

    public String getAuditHistoryEvent() {
        return auditHistoryEvent.getText();
    }

}
