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

public class AdminIncidentConfigurationPage extends Base_Page<AdminIncidentConfigurationPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String INCIDENT_CONFIG_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String INCIDENT_CONFIG_CONTENT_XPATH = INCIDENT_CONFIG_PAGE_XPATH + "//div[@class='admin-action__content']";

    private static final String ADD_NEW_CRITERIA_XPATH = INCIDENT_CONFIG_CONTENT_XPATH + "//i[@class='icons__plus']";

    private static final String ACTIVE_CRITERIA_XPATH = INCIDENT_CONFIG_CONTENT_XPATH + "//md-tab-title[contains(text(), 'Active')]/..";
    private static final String INACTIVE_CRITERIA_XPATH = INCIDENT_CONFIG_CONTENT_XPATH + "//md-tab-title[contains(text(), 'Inactive')]/..";

    private static final String INCIDENT_CONFIG_GRID_XPATH = INCIDENT_CONFIG_CONTENT_XPATH + "//md-tab-content[contains(@class, 'md-active')]//div[@data-role='grid']";

    private static final String POTENTIAL_FORCED_FILTER_XPATH = INCIDENT_CONFIG_GRID_XPATH + "//input[@data-text-field='linkedIncidentCriterionType.name']";
    private static final String SITE_BRAND_FILTER_XPATH = INCIDENT_CONFIG_GRID_XPATH + "//input[@data-text-field='brand.name']";
    private static final String SITE_TYPE_FILTER_XPATH = INCIDENT_CONFIG_GRID_XPATH + "//input[@data-text-field='siteType.name']";
    private static final String ASSET_SUBTYPE_FILTER_XPATH = INCIDENT_CONFIG_GRID_XPATH + "//input[@data-text-field='assetSubType.name']";
    private static final String CLASSIFICATION_FILTER_XPATH = INCIDENT_CONFIG_GRID_XPATH + "//input[@data-text-field='assetClassification.name']";
    private static final String FAULT_TYPE_FILTER_XPATH = INCIDENT_CONFIG_GRID_XPATH + "//input[@data-text-field='faultType.name']";
    private static final String INCIDENT_TYPE_FILTER_XPATH = INCIDENT_CONFIG_GRID_XPATH + "//input[@data-text-field='incidentType.name']";

    private static final String EDIT_INCIDENT_CRITERIA_XPATH = INCIDENT_CONFIG_GRID_XPATH + "//a[contains(text(), 'Edit')]";


    @FindBy(xpath = ADD_NEW_CRITERIA_XPATH)
    private WebElement addNewCriteriaButton;

    @FindBy(xpath = ACTIVE_CRITERIA_XPATH)
    private WebElement activeTab;

    @FindBy(xpath = INACTIVE_CRITERIA_XPATH)
    private WebElement inactiveTab;

    @FindBy(xpath = EDIT_INCIDENT_CRITERIA_XPATH)
    private WebElement edit;


    public AdminIncidentConfigurationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INCIDENT_CONFIG_GRID_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        return GridHelper.getGrid(INCIDENT_CONFIG_GRID_XPATH);
    }

    public void selectActiveTab() {
        waitForElement(By.xpath(ACTIVE_CRITERIA_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.clickJavascript(By.xpath(ACTIVE_CRITERIA_XPATH));
        waitForAngularRequestsToFinish();
    }

    public void selectInactiveTab() {
        waitForElement(By.xpath(INACTIVE_CRITERIA_XPATH), ELEMENT_IS_VISIBLE);
        POHelper.clickJavascript(By.xpath(INACTIVE_CRITERIA_XPATH));
        waitForAngularRequestsToFinish();
    }

    public void addNewCriteria() {
        addNewCriteriaButton.click();
        waitForAngularRequestsToFinish();
    }

    public AdminEditIncidentCriteriaPage editIncidentCriteria() {
        waitForElement(By.xpath(EDIT_INCIDENT_CRITERIA_XPATH), ELEMENT_IS_CLICKABLE);
        edit.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditIncidentCriteriaPage.class).get();
    }

    public void setPotentialForcedFilter(String option) {
        WebElement potentialForcedFilter = waitForElement(By.xpath(POTENTIAL_FORCED_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        potentialForcedFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        potentialForcedFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setSiteBrandFilter(String option) {
        WebElement siteBrandFilter = waitForElement(By.xpath(SITE_BRAND_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        siteBrandFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        siteBrandFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setSiteTypeFilter(String option) {
        WebElement siteTypeFilter = waitForElement(By.xpath(SITE_TYPE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        siteTypeFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        siteTypeFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setAssetSubTypeFilter(String option) {
        WebElement assetSubTypeFilter = waitForElement(By.xpath(ASSET_SUBTYPE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        assetSubTypeFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        assetSubTypeFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setClassificationFilter(String option) {
        WebElement classificationFilter = waitForElement(By.xpath(CLASSIFICATION_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        classificationFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        classificationFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setFaultTypeFilter(String option) {
        WebElement faultTypeFilter = waitForElement(By.xpath(FAULT_TYPE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        faultTypeFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        faultTypeFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void setIncidentTypeFilter(String option) {
        WebElement incidentTypeFilter = waitForElement(By.xpath(INCIDENT_TYPE_FILTER_XPATH), ELEMENT_IS_VISIBLE);
        incidentTypeFilter.sendKeys(option);
        waitForAngularRequestsToFinish();
        incidentTypeFilter.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

}
