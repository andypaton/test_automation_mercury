package mercury.pageobject.web.admin.refrigerantGasUsage;

import static mercury.helpers.POHelper.scrollTo;
import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.PopupAlert;
import mercury.pageobject.web.pageHelpers.QuestionHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.State;

public class AdminRefrigerantGasUsageEditPage extends Base_Page<AdminRefrigerantGasUsageEditPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_GAS_USAGE_XPATH = "//div[@ng-app='editGasUsageAdmin']";

    private static final String CONTROL_LABEL_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = '%s']/..";
    private static final String EDIT_BOTTLE_XPATH = EDIT_GAS_USAGE_XPATH + "//mc-bottle-validation//a[contains(@class, 'k-grid-edit')]";
    private static final String BOTTLE_NUMBER_XPATH = EDIT_GAS_USAGE_XPATH + "//mc-bottle-validation//span[contains(@ng-bind, 'dataItem.bottleNumber')]";
    private static final String BOTTLE_QTY_XPATH = EDIT_GAS_USAGE_XPATH + "//mc-bottle-validation//span[contains(@ng-bind, 'dataItem.bottleQuantity')]";

    private static final String SITE_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Site']/following-sibling::div/span";
    private static final String RESOURCE_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Resource']/following-sibling::div/span";
    private static final String DATE_COMPLETED_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Date completed']/following-sibling::div/span";
    private static final String TOTAL_GAS_USED_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Total gas used']/following-sibling::div/span";
    private static final String REFRIGERANT_CHARGE_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Total gas used']/following-sibling::div/span[2]";

    private static final String GAS_TYPE_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Gas Type']/.." + DROPDOWN_SEARCH_XPATH;
    private static final String LEAK_LOCATION_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Leak Location']/.." + DROPDOWN_SEARCH_XPATH;
    private static final String LEAKAGE_CODE_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Leakage Code']/.." + DROPDOWN_SEARCH_XPATH;
    private static final String LEAK_CHECK_METHOD_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Leak check method']/.." + DROPDOWN_SEARCH_XPATH;
    private static final String ACTION_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Action']/.." + DROPDOWN_SEARCH_XPATH;
    private static final String FAULT_CODE_XPATH = EDIT_GAS_USAGE_XPATH + "//label[contains(@class, 'control-label') and text() = 'Fault Code']/.." + DROPDOWN_SEARCH_XPATH;

    private static final String SAVE_XPATH = EDIT_GAS_USAGE_XPATH + "//button[text() = 'Save']";
    private static final String CANCEL_XPATH = EDIT_GAS_USAGE_XPATH + "//button[text() = 'Save']";

    private static final String POPUP_XPATH = "//div/span[text()='Edit']/ancestor::div[contains(@class, 'k-window') and contains(@class, 'k-widget')]";
    private static final String POPUP_BOTTLE_NUMBER_XPATH = POPUP_XPATH + "//input[@name='bottleNumber']";
    private static final String POPUP_BOTTLE_QTY_XPATH = POPUP_XPATH + "//input[@name='bottleQuantity']/preceding-sibling::input";
    private static final String UPDATE_XPATH = POPUP_XPATH + "//a[contains(@class, 'k-button') and text() = 'Update']";

    private static final String AUDIT_HISTORY_XPATH = "//mc-audit-grid[@table-name = 'EditGasCtrl.tableName']";
    private static final String EXPAND_AUDIT_HISTORY_XPATH = AUDIT_HISTORY_XPATH + "//i[contains(@class, 'fa-plus-circle') and @aria-hidden = 'false']";

    private static final String QUESTION_XPATH = EDIT_GAS_USAGE_XPATH + "//mc-gas-job-editable-data//label[contains(text(),\"%s\")]";
    private static final String QUESTION_TYPE_XPATH = QUESTION_XPATH + "/following-sibling::div//input";
    private static final String QUESTION_DROPDOWN_XPATH = QUESTION_XPATH + "/following-sibling::div" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String EDITABLE_DATA_XPATH = EDIT_GAS_USAGE_XPATH + "//mc-gas-job-editable-data";


    @FindBy(xpath = SITE_XPATH)
    private WebElement site;

    @FindBy(xpath = RESOURCE_XPATH)
    private WebElement resource;

    @FindBy(xpath = DATE_COMPLETED_XPATH)
    private WebElement dateCompleted;

    @FindBy(xpath = TOTAL_GAS_USED_XPATH)
    private WebElement totalGasUsed;

    @FindBy(xpath = REFRIGERANT_CHARGE_XPATH)
    private WebElement refrigerantCharge;

    @FindBy(xpath = GAS_TYPE_XPATH)
    private WebElement gasType;

    @FindBy(xpath = LEAK_LOCATION_XPATH)
    private WebElement leakLocation;

    @FindBy(xpath = LEAKAGE_CODE_XPATH)
    private WebElement leakageCode;

    @FindBy(xpath = LEAK_CHECK_METHOD_XPATH)
    private WebElement leakCheckMethod;

    @FindBy(xpath = ACTION_XPATH)
    private WebElement action;

    @FindBy(xpath = FAULT_CODE_XPATH)
    private WebElement faultCode;

    @FindBy(xpath = EDIT_BOTTLE_XPATH)
    private WebElement editBottle;

    @FindBy(xpath = POPUP_BOTTLE_NUMBER_XPATH)
    private WebElement popupBottleNumber;

    @FindBy(xpath = POPUP_BOTTLE_QTY_XPATH)
    private WebElement popupBottleQty;

    @FindBy(xpath = BOTTLE_NUMBER_XPATH)
    private WebElement bottleNumber;

    @FindBy(xpath = BOTTLE_QTY_XPATH)
    private WebElement bottleQty;

    @FindBy(xpath = UPDATE_XPATH)
    private WebElement update;

    @FindBy(xpath = SAVE_XPATH)
    private WebElement save;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancel;

    @FindBy(xpath = EXPAND_AUDIT_HISTORY_XPATH)
    private WebElement expandAuditHistory;


    public AdminRefrigerantGasUsageEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(EDIT_GAS_USAGE_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getSite() {
        return site.getText();
    }

    public String getResource() {
        return resource.getText();
    }

    public String getDateCompleted() {
        return dateCompleted.getText();
    }

    public String getTotalGasUsed() {
        return totalGasUsed.getText();
    }

    public String getRefrigerantCharge() {
        return refrigerantCharge.getText().replace("/",  "").trim();
    }

    public String getGasType() {
        return gasType.getText();
    }

    public String getLeakLocation() {
        return leakLocation.getText();
    }

    public String getLeakageCode() {
        return leakageCode.getText();
    }

    public String getLeakCheckMethod() {
        return leakCheckMethod.getText();
    }

    public String getAction() {
        return action.getText();
    }

    public String getFaultCode() {
        return faultCode.getText();
    }

    public String selectRandomAssetRack() {
        return selectRandomAnwser("Asset Rack", CONTROL_LABEL_XPATH);
    }

    public String selectRandomGasType() {
        return selectRandomAnwser("Gas Type", CONTROL_LABEL_XPATH);
    }

    public String selectRandomLeakLocation() {
        return selectRandomAnwser("Leak Location", CONTROL_LABEL_XPATH);
    }

    public String selectRandomLeakageCode() {
        return selectRandomAnwser("Leakage Code", CONTROL_LABEL_XPATH);
    }

    public String selectRandomLeakCheckMethod() {
        return selectRandomAnwser("Leak check method", CONTROL_LABEL_XPATH);
    }

    public String selectRandomAction() {
        return selectRandomAnwser("Action", CONTROL_LABEL_XPATH);
    }

    public String selectRandomFaultCode() {
        return selectRandomAnwser("Fault Code", CONTROL_LABEL_XPATH);
    }

    public void enterBottleNumber(String num) {
        popupBottleNumber.clear();
        //        POHelper.sendKeys(popupBottleNumber, num, 10);
        POHelper.sendKeys(popupBottleNumber, num);
        popupBottleNumber.sendKeys(Keys.TAB);
    }

    public void enterAmountUsed(String amount) {
        waitForElement(By.xpath(POPUP_BOTTLE_QTY_XPATH), State.ELEMENT_IS_CLICKABLE);
        popupBottleQty.sendKeys(Keys.chord(Keys.CONTROL, "a"), amount);
    }

    public List<String> getBottleNumbers() {
        List<String> bottleNumbers = new ArrayList<>();
        for (WebElement we : driver.findElements(By.xpath(BOTTLE_NUMBER_XPATH))) {
            bottleNumbers.add(we.getText());
        }
        return bottleNumbers;
    }

    public List<String> getAmountsUsed() {
        List<String> bottleQty = new ArrayList<>();
        for (WebElement we : driver.findElements(By.xpath(BOTTLE_QTY_XPATH))) {
            bottleQty.add(we.getText());
        }
        return bottleQty;
    }

    public void editBottle() {
        editBottle.click();
        waitForAngularRequestsToFinish();
    }

    public void update() {
        update.click();
        waitForAngularRequestsToFinish();
    }

    public PopupAlert save() {
        save.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PopupAlert.class).get();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void takeScreenshotOfPopup(OutputHelper outputHelper) throws Exception {
        outputHelper.takeScreenshot(POPUP_XPATH);
    }

    public void expandAutitHistory(){
        int pos = Integer.valueOf(expandAuditHistory.getLocation().getY());
        expandAuditHistory.click();
        scrollTo(pos);
        waitForAngularRequestsToFinish();
    }

    public Grid getAutitHistory(){
        return GridHelper.getGrid(AUDIT_HISTORY_XPATH);
    }

    public String getGasQuestionTagName(String question) {
        WebElement element = driver.findElement(By.xpath(String.format(QUESTION_TYPE_XPATH, question)));
        return element.getTagName();
    }

    public String questionSelectRandomOption(String question) {
        WebElement dropdown = waitForElement(By.xpath(String.format(QUESTION_DROPDOWN_XPATH, question)), ELEMENT_IS_CLICKABLE);
        int pos = Integer.valueOf(dropdown.getLocation().getY());
        scrollTo(pos);
        dropdown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public QuestionHelper getQuestionHelper() {
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(EDITABLE_DATA_XPATH);
        return questionHelper;
    }

}
