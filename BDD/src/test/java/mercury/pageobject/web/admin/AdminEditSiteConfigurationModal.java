package mercury.pageobject.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditSiteConfigurationModal extends Base_Page<AdminEditSiteConfigurationModal> {

    private static final Logger logger = LogManager.getLogger();

    private static final String SITE_CONFIG_MODAL_XPATH = "//div[contains(@class, 'modal-dialog') and contains(@class, 'modal-lg')]";
    private static final String SITE_CONFIG_MODAL_CONTENT_XPATH = SITE_CONFIG_MODAL_XPATH + "//div[@class='modal-content']";

    private static final String COPY_FROM_XPATH = SITE_CONFIG_MODAL_CONTENT_XPATH + "//select[@ng-model='$ctrl.copyFromSite']";

    private static final String SITE_CLASSIFICATIONS_XPATH = "(//label[contains(@class, 'button-style-checkbox')])[2]";
    private static final String SITE_CLASSIFICATION_PRIORITY_XPATH = SITE_CONFIG_MODAL_CONTENT_XPATH + "//select[@ng-model='cls.priority']";
    private static final String SITE_CLASSIFICATIONS_LIST_XPATH = SITE_CONFIG_MODAL_CONTENT_XPATH + "//div[contains(@class, 'contractor-site__modal-body')]//label";
    private static final String SELECTED_CLASSIFICATION_XPATH = SITE_CONFIG_MODAL_CONTENT_XPATH + "//label[contains(@class, 'button-style-checkbox--selected')]";
    private static final String FILTER_XPATH = SITE_CONFIG_MODAL_CONTENT_XPATH + "//input[@ng-model='$ctrl.searchText']";
    private static final String ONLY_SHOW_SELECTED_FILTER_XPATH = SITE_CONFIG_MODAL_CONTENT_XPATH + "//label[contains(text(), 'Only show selected')]";
    private static final String PRIORITY_DROPDOWN_SELECTED_CLASSIFICATION = SITE_CONFIG_MODAL_CONTENT_XPATH + "//label[contains(@class, 'selected')]//../following-sibling::div//select";
    private static final String SELECTED_CLASSIFICATION = SITE_CONFIG_MODAL_CONTENT_XPATH + "//label[contains(@class, 'selected')]";

    private static final String BUTTON_XPATH = SITE_CONFIG_MODAL_CONTENT_XPATH + "//button[contains(text(), '%s')]";


    @FindBy(xpath = SITE_CLASSIFICATIONS_XPATH)
    private WebElement siteClassification;

    @FindBy(xpath = SITE_CLASSIFICATION_PRIORITY_XPATH)
    private WebElement siteClassificationPriority;

    @FindBy(xpath = COPY_FROM_XPATH)
    private WebElement copyFrom;

    @FindBy(xpath = SELECTED_CLASSIFICATION)
    private WebElement selectedClassification;

    @FindBy(xpath = SITE_CLASSIFICATIONS_LIST_XPATH)
    private List<WebElement> siteClassificationsList;


    public AdminEditSiteConfigurationModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(SITE_CONFIG_MODAL_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectClassification() {
        siteClassification.click();
        waitForAngularRequestsToFinish();
    }

    public void selectClassificationPriority(String priority) {
        Select select = new Select(siteClassificationPriority);
        select.selectByVisibleText(priority);
        waitForAngularRequestsToFinish();
    }

    public void clickButton(String buttonName) {
        WebElement button = driver.findElement(By.xpath(String.format(BUTTON_XPATH, buttonName)));
        button.click();
        waitForAngularRequestsToFinish();
    }

    public void copyFromPreviousClassification(String siteName) {
        Select select = new Select(copyFrom);
        select.selectByVisibleText(siteName);
        waitForAngularRequestsToFinish();
    }

    public List<String> getSiteClassifications() {
        List<String> siteClassifications = new ArrayList<>();
        for (WebElement siteClassification : siteClassificationsList) {
            siteClassifications.add(siteClassification.getText().trim());
        }
        return siteClassifications;
    }

    public Boolean isClassificationSelected() {
        return this.isElementPresent(By.xpath(SELECTED_CLASSIFICATION_XPATH));
    }

    public Boolean isFilterBoxDisplayed() {
        return this.isElementPresent(By.xpath(FILTER_XPATH));
    }

    public Boolean isOnlyShowSelectedFilterDisplayed() {
        return this.isElementPresent(By.xpath(ONLY_SHOW_SELECTED_FILTER_XPATH));
    }

    public Boolean isPriorityDropdownDisplayedForSelectedClassification() {
        return this.isElementPresent(By.xpath(PRIORITY_DROPDOWN_SELECTED_CLASSIFICATION));
    }

    public void removeSelectedClassification() {
        selectedClassification.click();
        waitForAngularRequestsToFinish();
    }
}
