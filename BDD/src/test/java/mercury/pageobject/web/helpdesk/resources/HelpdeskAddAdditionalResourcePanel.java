package mercury.pageobject.web.helpdesk.resources;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

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

public class HelpdeskAddAdditionalResourcePanel extends Base_Page<HelpdeskAddAdditionalResourcePanel> {

    private static final Logger logger = LogManager.getLogger();
    private static final String ACTIVE_PANEL_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String ADD_ADDITIONAL_RESOURCE_PANEL_XPATH = ACTIVE_PANEL_XPATH + "//ph-job-add-additional-resource";
    private static final String ADDITIONAL_RESOURCE_CONTAINER_XPATH = ADD_ADDITIONAL_RESOURCE_PANEL_XPATH + "//div[contains(@class, 'arr__new-resource-container')]";
    private static final String CREATION_REASON_DROPDOWN_XPATH = ADDITIONAL_RESOURCE_CONTAINER_XPATH + "//label[contains(text(), 'Creation Reason')]/.."
            + DROPDOWN_SEARCH_XPATH;

    private static final String CONFIGURED_RESOURCES_XPATH = ADDITIONAL_RESOURCE_CONTAINER_XPATH + "//div[contains(@class, 'resource-picker-container')]//span[contains(@class, 'k-input')]";

    private static final String ADDITIONAL_RESOURCE_NAME_XPATH = ADDITIONAL_RESOURCE_CONTAINER_XPATH + "//span[contains(@class, 'confirm-resource__resourceName')]";
    private static final String RESOURCE_PROFILE_DROPDOWN_XPATH = ADDITIONAL_RESOURCE_CONTAINER_XPATH + "//input[@kendo-dropdownlist='resourceProfileDropdown']/.."
            + DROPDOWN_SEARCH_XPATH;
    private static final String RESOURCE_PROFILE_ADDITIONAL_RESOURCE_XPATH
    = ACTIVE_PANEL_XPATH + "//ph-job-additional-resource//div[contains(@class, 'resource-picker-container')]//span[contains(@class, 'confirm-resource__resourceName')]";

    private static final String INCREASE_CALLOUT_CHECKBOX_XPATH = ADD_ADDITIONAL_RESOURCE_PANEL_XPATH + "//label[contains(text(), 'Increase call out')]";
    private static final String INCREASE_CALLOUT_SECTION_XPATH = ADD_ADDITIONAL_RESOURCE_PANEL_XPATH + "//div[@ng-if='ctrl.showIncreaseCalloutSection']";
    private static final String INCREASE_CALLOUT_AMOUNT_XPATH = INCREASE_CALLOUT_SECTION_XPATH + "//input[@name='calloutFee']";
    private static final String INCREASE_CALLOUT_REASON_XPATH = INCREASE_CALLOUT_SECTION_XPATH + "//label[text()='Reason']/following-sibling::span//span[contains(@class, 'k-input')]";
    private static final String INCREASE_CALLOUT_NOTES_CSS = "textarea[ng-model='ctrl.resourceModel.increaseCalloutFeeNotes']";

    // BUTTONS
    private static final String ADDITIONAL_REQUEST_FORM_XPATH = "//form[@name='arrForm']";
    private static final String ADDITIONAL_REQUEST_DESC__XPATH = ADDITIONAL_REQUEST_FORM_XPATH + "//textarea[@id='arrDescription']";
    private static final String INDIVIDUAL_RESOURCE_RADIO_BUTTON_XPATH = "//div[@class='yesNoRadio']//input[contains(@id,'individualResource')]";
    private static final String RESOURCE_PROFILE_RADIO_BUTTON_XPATH = ADDITIONAL_REQUEST_FORM_XPATH + "//div[@class='yesNoRadio']//input[@id='resourceProfile']";

    @FindBy(xpath = CREATION_REASON_DROPDOWN_XPATH)
    WebElement creationReason;

    @FindBy(xpath = ADDITIONAL_REQUEST_DESC__XPATH)
    private WebElement additionalRequestDescription;

    @FindBy(xpath = CONFIGURED_RESOURCES_XPATH)
    WebElement configuredResources;

    @FindBy(xpath = INCREASE_CALLOUT_REASON_XPATH)
    WebElement increaseCalloutReason;

    @FindBy(xpath = ADDITIONAL_RESOURCE_NAME_XPATH)
    WebElement additionalResourceName;

    @FindBy(xpath = INDIVIDUAL_RESOURCE_RADIO_BUTTON_XPATH)
    WebElement individualResourceRadio;

    @FindBy(xpath = RESOURCE_PROFILE_RADIO_BUTTON_XPATH)
    WebElement resourceProfileRadio;

    @FindBy(xpath = RESOURCE_PROFILE_DROPDOWN_XPATH)
    WebElement resourceProfileDropdown;

    @FindBy(xpath = RESOURCE_PROFILE_ADDITIONAL_RESOURCE_XPATH)
    WebElement resourceProfileAdditionalResource;

    public HelpdeskAddAdditionalResourcePanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ADD_ADDITIONAL_RESOURCE_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectCreationReason(String reason) {
        creationReason.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(reason);
    }

    public void selectRandomCreationReason() {
        creationReason.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public String getCreationReason() {
        return creationReason.getText();
    }

    public void sendAdditionalRequestDescription(String desc) {
        additionalRequestDescription.sendKeys(desc);
    }

    public void selectRandomConfiguredContractorResource() {
        waitForLoadingToComplete();
        waitForElement(By.xpath(CONFIGURED_RESOURCES_XPATH), ELEMENT_IS_VISIBLE);
        configuredResources.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption("(Contractor)");
    }

    public void selectConfiguredResource(String resource) {
        configuredResources.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(resource);
    }

    public void searchAndSelectRandomConfiguredResource(String resource) {
        configuredResources.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(resource);
    }

    public String getConfiguredResource() {
        waitForElement(By.xpath(CONFIGURED_RESOURCES_XPATH), ELEMENT_IS_VISIBLE);
        return configuredResources.getText();
    }

    public void clickIncreaseCallOut() {
        WebElement increaseCallOut = waitForElement(By.xpath(INCREASE_CALLOUT_CHECKBOX_XPATH), ELEMENT_IS_CLICKABLE);
        increaseCallOut.click();
        waitForAngularRequestsToFinish();
    }

    public void sendIncreasedCalloutAmount(String amount) {
        WebElement calloutIncrease = waitForElement(By.xpath(INCREASE_CALLOUT_AMOUNT_XPATH), ELEMENT_IS_VISIBLE);
        calloutIncrease.sendKeys(amount);
    }

    public void selectIncreaseCalloutReason(String reason) {
        increaseCalloutReason.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(reason);
    }

    public void sendIncreaseCalloutNotes(String notes) {
        WebElement calloutNotes = driver.findElement(By.cssSelector(String.format(INCREASE_CALLOUT_NOTES_CSS)));
        calloutNotes.sendKeys(notes);
    }

    public void selectRandomConfiguredResource() {
        configuredResources.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
    }

    public String getAdditionalResourceName() {
        return additionalResourceName.getText();
    }

    public String getResourceProfile() {
        return resourceProfileAdditionalResource.getText();
    }

    public void clickResourceProfileRadioButton() {
        POHelper.clickJavascript(resourceProfileRadio);
    }

    public void clickIndividualResourceRadioButton() {
        POHelper.clickJavascript(individualResourceRadio);
    }

    public void selectResourceProfile(String option) {
        resourceProfileDropdown.click();
        waitForAngularRequestsToFinish();
        selectExactVisibleDropdownOption(option);
    }

    public void selectRandomResourceProfile() {
        resourceProfileDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }
}
