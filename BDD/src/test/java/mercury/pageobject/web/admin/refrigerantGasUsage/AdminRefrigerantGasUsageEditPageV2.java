package mercury.pageobject.web.admin.refrigerantGasUsage;

import static mercury.helpers.POHelper.scrollTo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
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


public class AdminRefrigerantGasUsageEditPageV2 extends Base_Page<AdminRefrigerantGasUsageEditPageV2> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Admin Edit Gas Usage";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    // Admin Edit Gas page
    private static final String EDIT_GAS_FORM_XPATH = "//mc-edit-gas-usage-form-epa-regulations-2019";

    private static final String BASIC_QUESTION_YES_XPATH = "//label[contains(text(),'%s')]/..//label[text()='Yes']";
    private static final String BASIC_QUESTION_NO_XPATH = "//label[contains(text(),'%s')]/..//label[text()='No']";

    // Core
    private static final String QUESTION_LABEL = EDIT_GAS_FORM_XPATH + "//label[contains(text(),'%s')]";
    private static final String UPDATE_JOB_QUESTIONS_YES_XPATH = EDIT_GAS_FORM_XPATH + BASIC_QUESTION_YES_XPATH;
    private static final String UPDATE_JOB_QUESTIONS_NO_XPATH = EDIT_GAS_FORM_XPATH + BASIC_QUESTION_NO_XPATH;

    private static final String SUB_SECTION_XPATH = EDIT_GAS_FORM_XPATH + "//div[contains(@class,'gas-section__sub-header') and contains(text(),'%s')]";

    private static final String SITE_XPATH = EDIT_GAS_FORM_XPATH + "//label[contains(@class, 'control-label') and text() = 'Site']/../following-sibling::div";
    private static final String RESOURCE_XPATH = EDIT_GAS_FORM_XPATH + "//label[contains(@class, 'control-label') and text() = 'Resource']/../following-sibling::div";
    private static final String DATE_COMPLETED_XPATH = EDIT_GAS_FORM_XPATH + "//label[contains(@class, 'control-label') and text() = 'Date completed']/../following-sibling::div";
    private static final String TOTAL_GAS_USED_XPATH = EDIT_GAS_FORM_XPATH + "//label[contains(@class, 'control-label') and text() = 'Total gas used']/../following-sibling::div";

    // Refrigerant Details
    private static final String REFRIGERANT_DETAILS_XPATH = EDIT_GAS_FORM_XPATH + "//us-gas-refrigerant-source-questions";

    private static final String GAS_CYLINDER_SECTION = REFRIGERANT_DETAILS_XPATH + "//div[@class = 'gas-section gas-source-data']";

    private static final String REFRIGERANT_DETAILS_BUTTON_XPATH = REFRIGERANT_DETAILS_XPATH + "//span[contains(@class, 'gas-section__header-part2') and contains(text(), '%s')][1]";
    private static final String DELETE_CYLINDER_XPATH = REFRIGERANT_DETAILS_BUTTON_XPATH + "/../..//div[@role='button' and contains(text(), 'Delete')]";
    private static final String EDIT_XPATH = "//div[@role='button' and contains(text(), 'Edit')]";
    private static final String EDIT_CYLINDER_XPATH = REFRIGERANT_DETAILS_BUTTON_XPATH + "/../.." + EDIT_XPATH;
    private static final String ADD_CYLINDER_XPATH = REFRIGERANT_DETAILS_XPATH + "/../..//div[@role='button' and text()[contains(., 'Add')]]";

    private static final String CYLINDERS_XPATH = "//span[contains(@class, 'gas-section__header-part2')]";
    private static final String GAS_SOURCE_XPATH = "//div[text()='Refrigerant Source']/following-sibling::div[@class='row'][1]/div[@class='col-md-3'][1]";
    private static final String GAS_FULL_PARTIAL_XPATH = "//div[text()='Refrigerant Source']/following-sibling::div[@class='row'][1]/div[@class='col-md-3'][2]";
    private static final String GAS_CYLINDER_TYPE_XPATH = "//div[text()='Refrigerant Source']/following-sibling::div[@class='row'][1]/div[@class='col-md-6']";
    private static final String GAS_LBS_AVAILABLE_XPATH = "//div[text()='Refrigerant Installed']/following-sibling::div[@class='row']/div[@class='col-md-3'][1]";
    private static final String GAS_LBS_INSTALLED_XPATH = "//div[text()='Refrigerant Installed']/following-sibling::div[@class='row']/div[@class='col-md-3'][2]";
    private static final String GAS_SURPLUS_XPATH = "//div[text()='Refrigerant Installed']/following-sibling::div[@class='row']/div[@class='col-md-6']";
    private static final String GAS_DESTINATION_XPATH = "//div[text()='Refrigerant Surplus']/following-sibling::div[@class='row']/div[@class='col-md-3'][1]";
    private static final String GAS_SURPLUS_TYPE_XPATH = "//div[text()='Refrigerant Surplus']/following-sibling::div[@class='row']/div[@class='col-md-3'][2]";

    private static final String GAS_GAS_TYPE_SELECTED_VALUE_XPATH = EDIT_GAS_FORM_XPATH + "//label[text() = 'Refrigerant Type Used']/following-sibling::div//span[contains(@class, 'k-input')]";

    private static final String APPLIANCE_INFO_XPATH = EDIT_GAS_FORM_XPATH +"//div[@ng-form = 'ctrl.applianceInformationForm']";
    private static final String REFRIGERANT_INSTALLED_XPATH = EDIT_GAS_FORM_XPATH +"//div[@ng-form = 'ctrl.refrigerantInstalledForm']";

    // Leak Checks
    private static final String LEAK_CHECKS_XPATH = EDIT_GAS_FORM_XPATH + "//us-gas-leak-check-questions";
    private static final String PRIMARY_COMPONENTS_XPATH =  LEAK_CHECKS_XPATH + "//div[text()='Leak Check Information']/following-sibling::div[@class='row'][1]/div[@class='col-md-6'][1]";
    private static final String SUB_COMPONENTS_XPATH =  LEAK_CHECKS_XPATH + "//div[text()='Leak Check Information']/following-sibling::div[@class='row'][1]/div[@class='col-md-6'][2]";
    private static final String PRIMARY_COMPONENTS_INFO_XPATH =  LEAK_CHECKS_XPATH + "//div[text()='Leak Check Information']/following-sibling::div[@class='row']/div[@class='col-md-12']";
    private static final String LEAK_SITE_STATUSES_XPATH =  LEAK_CHECKS_XPATH + "//div[text()='Leak Check Results']/following-sibling::div[@class='row']/div[@class='col-md-4'][1]";
    private static final String INITIAL_TESTS_XPATH =  LEAK_CHECKS_XPATH + "//div[text()='Leak Check Results']/following-sibling::div[@class='row']/div[@class='col-md-4'][2]";
    private static final String FOLLOW_UP_TESTS_XPATH =  LEAK_CHECKS_XPATH + "//div[text()='Leak Check Results']/following-sibling::div[@class='row']/div[@class='col-md-4'][3]";

    // Leak Site Check buttons
    private static final String LEAK_SITE_CHECK_EDIT_XPATH =  LEAK_CHECKS_XPATH + "//div[contains(@class, 'btn') and contains(text(), 'Edit')]";
    private static final String LEAK_SITE_CHECK_DELETE_XPATH =  LEAK_CHECKS_XPATH + "//div[contains(@class, 'btn') and contains(text(), 'Delete')]";
    private static final String ADD_ADDITIONAL_LEAK_SITE_CHECK_XPATH =  LEAK_CHECKS_XPATH + "//div[@id='addLeakLocation']";
    private static final String ADD_LEAK_SITE_DETAILS_BUTTON_XPATH =  LEAK_CHECKS_XPATH + "//div[contains(., 'Add Leak Site Details')]/div[@role='button']";

    private static final String SAVE_XPATH =  EDIT_GAS_FORM_XPATH + "//button[text()='Save']";

    private static final String AUDIT_HISTORY_XPATH = "//mc-audit-grid";
    private static final String EXPAND_AUDIT_HISTORY_XPATH = AUDIT_HISTORY_XPATH + "//i[contains(@class, 'fa-plus-circle') and @aria-hidden = 'false']";

    private static final String BUTTON_XPATH = "//div[@role = 'button' and text()[contains(., ':name')]] | //button[text()=':name']";

    @FindBy(xpath = EDIT_GAS_FORM_XPATH)
    private WebElement page;

    @FindBy(xpath = SITE_XPATH)
    private WebElement site;

    @FindBy(xpath = RESOURCE_XPATH)
    private WebElement resource;

    @FindBy(xpath = DATE_COMPLETED_XPATH)
    private WebElement dateCompleted;

    @FindBy(xpath = TOTAL_GAS_USED_XPATH)
    private WebElement totalGasUsed;

    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = GAS_CYLINDER_SECTION)
    private List<WebElement> gasCylinderSection;

    // Refrigerant Details
    @FindBy(xpath = CYLINDERS_XPATH)
    private List<WebElement> cylinders;

    @FindBy(xpath = ADD_CYLINDER_XPATH)
    private WebElement addCylinder;

    // Leak Checks
    @FindBy(xpath = PRIMARY_COMPONENTS_XPATH)
    private List<WebElement> primaryComponents;

    @FindBy(xpath = SUB_COMPONENTS_XPATH)
    private List<WebElement> subComponents;

    @FindBy(xpath = PRIMARY_COMPONENTS_INFO_XPATH)
    private List<WebElement> primaryComponentsInfo;

    @FindBy(xpath = LEAK_SITE_STATUSES_XPATH)
    private List<WebElement> leakSiteStatuses;

    @FindBy(xpath = INITIAL_TESTS_XPATH)
    private List<WebElement> initialTests;

    @FindBy(xpath = FOLLOW_UP_TESTS_XPATH)
    private List<WebElement> followUpTests;

    @FindBy(xpath = GAS_GAS_TYPE_SELECTED_VALUE_XPATH)
    private WebElement gasTypeSelectedValue;

    @FindBy(xpath = SAVE_XPATH)
    private WebElement save;

    @FindBy(xpath = LEAK_SITE_CHECK_EDIT_XPATH)
    private List<WebElement> leakSiteCheckEdit;

    @FindBy(xpath = LEAK_SITE_CHECK_DELETE_XPATH)
    private List<WebElement> leakSiteCheckDelete;

    @FindBy(xpath = ADD_ADDITIONAL_LEAK_SITE_CHECK_XPATH)
    private WebElement addAdditionalLeakSiteCheck;

    @FindBy(xpath = EXPAND_AUDIT_HISTORY_XPATH)
    private WebElement expandAuditHistory;

    @FindBy(xpath = ADD_LEAK_SITE_DETAILS_BUTTON_XPATH)
    private WebElement addLeakSiteDetails;


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(EDIT_GAS_FORM_XPATH));
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

    public AdminRefrigerantGasUsageEditPageV2(WebDriver driver) {
        super(driver);
    }

    public boolean isQuestionVisible(String question) {
        return isElementVisible(By.xpath(String.format(QUESTION_LABEL, question)));
    }

    public void clickYes(String question) {
        WebElement yes = waitForElement(By.xpath(String.format(UPDATE_JOB_QUESTIONS_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        yes.click();
        waitForAngularRequestsToFinish();
    }

    public void clickNo(String question) {
        WebElement no = waitForElement(By.xpath(String.format(UPDATE_JOB_QUESTIONS_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        no.click();
        waitForAngularRequestsToFinish();
    }

    public void deleteCylinder(String serialNumber) {
        WebElement delete = waitForElement(By.xpath(String.format(DELETE_CYLINDER_XPATH, serialNumber)), State.ELEMENT_IS_CLICKABLE);
        delete.click();
        waitForAngularRequestsToFinish();
    }

    public AdminRefrigerantSourceModal editCylinder(String serialNumber) {
        WebElement edit = waitForElement(By.xpath(String.format(EDIT_CYLINDER_XPATH, serialNumber)), State.ELEMENT_IS_CLICKABLE);
        edit.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminRefrigerantSourceModal.class).get();
    }

    public AdminRefrigerantSourceModal editCylinder(int position) {
        String xpath = GAS_CYLINDER_SECTION + "[" + position + "]" + EDIT_XPATH;
        WebElement edit = waitForElement(By.xpath(xpath), State.ELEMENT_IS_CLICKABLE);
        edit.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminRefrigerantSourceModal.class).get();
    }

    public AdminRefrigerantSourceModal addCylinder() {
        addCylinder.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminRefrigerantSourceModal.class).get();
    }

    private List<String> getLeakChecks(String xpath, List<WebElement> webElements) {
        List<String> results = new ArrayList<>();
        if (isElementPresent(By.xpath(xpath))) {
            for (WebElement we : webElements) {
                results.add(we.getText().trim());
            }
        }
        return results;
    }

    public int getNumberOfLeakSiteChecks() {
        return getPrimaryComponents().size();
    }

    public List<String> getPrimaryComponents() {
        return getLeakChecks(PRIMARY_COMPONENTS_XPATH, primaryComponents);
    }

    public List<String> getSubComponents() {
        return getLeakChecks(SUB_COMPONENTS_XPATH, subComponents);
    }

    public List<String> getPrimaryComponentsInfo() {
        return getLeakChecks(PRIMARY_COMPONENTS_INFO_XPATH, primaryComponentsInfo);
    }

    public List<String> getLeakSiteStatuses() {
        return getLeakChecks(LEAK_SITE_STATUSES_XPATH, leakSiteStatuses);
    }

    public List<String> getInitialTests() {
        return getLeakChecks(INITIAL_TESTS_XPATH, initialTests);
    }

    public List<String> getFollowUpTests() {
        return getLeakChecks(FOLLOW_UP_TESTS_XPATH, followUpTests);
    }

    public String getGasTypeSelectedValue() {
        if (isElementVisible(By.xpath(GAS_GAS_TYPE_SELECTED_VALUE_XPATH))) {
            return gasTypeSelectedValue.getText();
        } else {
            return null;
        }
    }

    public QuestionHelper getQuestionHelper() {
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(EDIT_GAS_FORM_XPATH);
        return questionHelper;
    }

    public boolean isSubSectionDispayed(String section) {
        return isElementVisible(By.xpath(String.format(SUB_SECTION_XPATH, section)));
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

    public void save() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public AdminLeakSiteInformationModal editLeakSiteCheck(int checkNumber) {
        leakSiteCheckEdit.get(checkNumber - 1).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminLeakSiteInformationModal.class).get();
    }

    public PopupAlert deleteLastLeakSiteCheck() {
        leakSiteCheckDelete.get(leakSiteCheckDelete.size() - 1).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PopupAlert.class).get();
    }

    public AdminLeakSiteInformationModal addAdditionalLeakSiteDetails() {
        addAdditionalLeakSiteCheck.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminLeakSiteInformationModal.class).get();
    }

    public boolean isDisplayed() {
        return page.isDisplayed();
    }

    public boolean isLeakSiteCheckEditDisplayed() {
        return isElementVisible(By.xpath(LEAK_SITE_CHECK_EDIT_XPATH));
    }

    public boolean isLeakSiteCheckDeleteDisplayed() {
        return isElementVisible(By.xpath(LEAK_SITE_CHECK_DELETE_XPATH));
    }

    public List<String> getGasSectionDetails(String target) {
        List<String> results = new ArrayList<>();
        if (isElementVisible(By.xpath(GAS_CYLINDER_SECTION))) {
            for (int i = 1; i <= gasCylinderSection.size(); i++) {
                String xpath = String.format(GAS_CYLINDER_SECTION + "[%d]" + target, i);
                if (isElementPresent(By.xpath(xpath))) {
                    results.add(driver.findElement(By.xpath(xpath)).getText());
                } else {
                    results.add("");
                }
            }
        }
        return results;
    }

    public List<String> getCylinderSerialNumbers() {
        List<String> serialNumbers = new ArrayList<>();
        for (String serialNumber : getGasSectionDetails(CYLINDERS_XPATH)) {
            serialNumbers.add(serialNumber.replaceAll("^- ", "").trim());
        }
        return serialNumbers;
    }

    public List<String> getSources() {
        return getGasSectionDetails(GAS_SOURCE_XPATH);
    }

    public List<String> getFullPartial() {
        return getGasSectionDetails(GAS_FULL_PARTIAL_XPATH);
    }

    public List<String> getCylinderTypes() {
        return getGasSectionDetails(GAS_CYLINDER_TYPE_XPATH);
    }

    public List<String> getGasAvailable() {
        return getGasSectionDetails(GAS_LBS_AVAILABLE_XPATH);
    }

    public List<String> getGasInstalled() {
        return getGasSectionDetails(GAS_LBS_INSTALLED_XPATH);
    }

    public List<String> getSurplus() {
        return getGasSectionDetails(GAS_SURPLUS_XPATH);
    }

    public List<String> getDestinations() {
        return getGasSectionDetails(GAS_DESTINATION_XPATH);
    }

    public List<String> getSurplusTypes() {
        return getGasSectionDetails(GAS_SURPLUS_TYPE_XPATH);
    }

    public void getUpdatedOn(){
        int pos = Integer.valueOf(expandAuditHistory.getLocation().getY());
        expandAuditHistory.click();
        scrollTo(pos);
        waitForAngularRequestsToFinish();
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

    public void takeScreenshotApplianceInformationForm(OutputHelper outputHelper) {
        outputHelper.takeScreenshot(APPLIANCE_INFO_XPATH);
    }

    public void takeScreenshotRefrigerantInstalledForm(OutputHelper outputHelper) {
        outputHelper.takeScreenshot(REFRIGERANT_INSTALLED_XPATH);
    }

    public Boolean isButtonDisplayed(String name) {
        String xpath = BUTTON_XPATH.replaceAll(":name", name);
        return isElementPresent(By.xpath(xpath));
    }

    public AdminLeakSiteInformationModal addLeakSiteDetails() {
        addLeakSiteDetails.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminLeakSiteInformationModal.class).get();
    }

}
