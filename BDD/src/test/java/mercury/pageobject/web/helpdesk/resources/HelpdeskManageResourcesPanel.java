package mercury.pageobject.web.helpdesk.resources;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;

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
import mercury.pageobject.web.helpdesk.HelpdeskResourceETAPanel;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskAcceptJobPanel;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskCallJobContactModal;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskDeclineJobPanel;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskFundingRequestsPanel;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskTransferWorkPanel;

public class HelpdeskManageResourcesPanel extends Base_Page<HelpdeskManageResourcesPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_CSS = ".active";
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String RESOURCE_MANAGEMENT_PANEL_CSS = ACTIVE_WORKSPACE_CSS + " ph-resource-management .job-action-panel";
    private static final String RESOURCE_MANAGEMENT_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-resource-management/div[contains(@class, 'job-action-panel')]";

    // ADDITIONAL RESOURCE
    private static final String OUTSTANDING_PO_LABEL_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//h1[contains(text(),'Outstanding POs')]";

    // RESOURCES FOR JOB
    private static final String INITIAL_RESOURCE_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//div[contains(@class, 'thread-resource first')]/ancestor::div[contains(@class, 'thread-container')]";
    private static final String RESOURCE_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//span[contains(text(), '%s')]//ancestor::div[contains(@class, 'thread-row')]";

    private static final String RESOURCE_NAME_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'resource-details')]/span[@class='confirm-resource__resourceName']";
    private static final String RESOURCE_NAME_WITH_ACTIONS_XPATH = ACTIVE_WORKSPACE_XPATH + "//button[@ng-click='ctrlResource.updateActionsMenuItems()']/ancestor::div[@class='confirmed']//span[@class='confirm-resource__resourceName']";
    private static final String RESOURCE_NAME_ACKNOWLEDGE_ETA_XPATH = ACTIVE_WORKSPACE_XPATH + "//a[contains(text(), 'Acknowledge ETA') and contains(@ng-click, 'ctrlResource')]/ancestor::div[@class='confirmed']//div[contains(@class, 'resource-details')]/span[@class='confirm-resource__resourceName']";
    private static final String RESOURCE_PROFILE_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'resource-details')]/span[@ng-if='ctrlResourceAssignment.resource.resourceProfile.name']";
    private static final String CONFIGURED_RESOURCES_LISTBOX_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//span[contains(@role, 'listbox')]";
    private static final String CONFIGURED_RESOURCE_PICKER_XPATH = CONFIGURED_RESOURCES_LISTBOX_XPATH + "//div[@name='resourcePickerRow']";
    private static final String RESOURCE_STATUS_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'resource-details')]/span[contains(@class, 'confirm-resource__resourcestatus')]";
    private static final String LAST_RESOURCE_STATUS_XPATH = "(//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[contains(@class, 'resource-details')]/span[contains(@class, 'confirm-resource__resourcestatus')])[last()]";

    private static final String RESOURCE_CALLBACK_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//span[contains(@class, 'confirm-resource__resourcecallback')]";
    private static final String RESOURCE_CALLBACK_ICON_XPATH = RESOURCE_CALLBACK_XPATH + "//i[@class='icons__phone confirm-resource__resourcecallbackicon']";

    private static final String CALL_BUTTON_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//div[@class='confirm-resource__actions']//button//span[contains(text(), 'Call')]";

    private static final String CALL_RESOURCE_BUTTON_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//span[contains(text(), '%s')]/ancestor::div[contains(@class,'confirmed')]//div[@class='confirm-resource__actions']//button//span[contains(text(), 'Call')]";

    // HEADERS
    private static final String HEADLINE_CSS = ACTIVE_WORKSPACE_CSS + " div.view-header__headline h1";
    private static final String HEADLINE_COMMENT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-header__headline div";
    private static final String SUBHEAD_CSS = ACTIVE_WORKSPACE_CSS + " div.view-header__subhead";
    private static final String SUBHEAD_LEFT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__left";
    private static final String SUBHEAD_RIGHT_CSS = ACTIVE_WORKSPACE_CSS + " div.view-subheader__right";
    private static final String IMMEDIATE_CALLOUT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='alert-text' and text()='Immediate callout required']";

    // BUTTONS
    private static final String BUTTONS_BAR_CSS = ACTIVE_WORKSPACE_CSS + " div.view-button-bar__buttons-container";
    private static final String BUTTON_XPATH = "//button[contains(text(), '%s')]";
    private static final String BUTTON_ADDITIONAL_RESOURCE = "//div[@class='resource-buttons-container']//button[@ng-click = 'ctrlResources.addAdditionalResourceRequirement()']";
    private static final String BUTTON_CLOSE_RESOURCES = RESOURCE_MANAGEMENT_PANEL_XPATH + "//button[contains(text(), 'Close resources')]";
    private static final String SAVE_BUTTON_XPATH = INITIAL_RESOURCE_XPATH + "//button[contains(text(), 'Save')]";
    private static final String ACTIONS_BUTTON_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//button[contains(text(), 'Actions')]";
    private static final String ACTIONS_BUTTON_FOR_RESOURCE_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//span[contains(text(), '%s')]//ancestor::div[@ng-if='row.resourceAssignment']//button[contains(text(), 'Actions')]";
    private static final String UNAVAILABLE_ACTION_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'%s') and @ng-if='!menuItem.hasAccess']";
    private static final String EXPANDED_ACTIONS_XPATH = RESOURCE_XPATH + "//button[contains(text(), 'Actions')]";
    private static final String ETA_ACTION_XPATH = EXPANDED_ACTIONS_XPATH + "/following-sibling::ul[contains(@class, 'dropdown-menu')]/li/span/a[text()='ETA']";
    private static final String ACCEPT_JOB_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Accept job')]";
    private static final String FUNDING_REQUESTS_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Funding requests')]";
    private static final String ACKNOWLEDGE_ETA_XPATH = EXPANDED_ACTIONS_XPATH + "/following-sibling::ul[contains(@class, 'dropdown-menu')]/li/span/a[text()='Acknowledge ETA']";
    private static final String ACKNOWLEDGE_ETA_LINK_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//a[contains(text(),'Acknowledge ETA')]";
    private static final String REMOVE_RESOURCE_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Remove Resource')]";
    private static final String REMOVE_RESOURCE_FOR_RESOURCE_XPATH = ACTIONS_BUTTON_FOR_RESOURCE_XPATH + "/following-sibling::ul//*[contains(text(),'Remove Resource')]";
    private static final String ADVISE_REMOVAL_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Advise Removal')]";
    private static final String DECLINE_JOB_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Decline job')]";
    private static final String SCHEDULE_CALLBACK_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Schedule Callback')]";
    private static final String TRANSFER_WORK_XPATH = ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Transfer Work')]";

    private static final String ADDITIONAL_RESOURCE_THREAD_XPATH = "//span[contains(text(), \"%s\")]//ancestor::div[contains(@class, 'thread-container')]";
    private static final String ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH = ADDITIONAL_RESOURCE_THREAD_XPATH + "//button[contains(text(), 'Actions')]";
    private static final String ADDITIONAL_RESOURCE_ACCEPT_JOB_XPATH = ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Accept job')]";
    private static final String ADDITIONAL_RESOURCE_FUNDING_REQUESTS_XPATH = ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Funding requests')]";
    private static final String ADDITIONAL_RESOURCE_REMOVE_RESOURCE_XPATH = ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Remove Resource')]";
    private static final String ADDITIONAL_RESOURCE_DECLINE_JOB_XPATH = ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Decline job')]";
    private static final String ADDITIONAL_RESOURCE_STATUS_XPATH = ADDITIONAL_RESOURCE_THREAD_XPATH + "//div[contains(@class, 'resource-details')]/span[contains(@class, 'confirm-resource__resourcestatus')]";
    private static final String ADDITIONAL_RESOURCE_ADVISE_REMOVAL_XPATH = ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH + "/following-sibling::ul//*[contains(text(),'Advise Removal')]";

    // OVERRIDE RESOURCE
    private static final String OVERRIDE_RECOMMENDED_RESOURCE_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//div[contains(@class, 'resource-action__header')]/*[contains(text(), 'Override Recommended Resource')]";
    private static final String OVERRIDE_RECOMMENDED_RESOURCE_REQUESTED_BY_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//input[@name='overrideRequester']";
    private static final String OVERRIDE_RECOMMENDED_RESOURCE_REASON_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//label[@for='reason']//following-sibling::span[@role='listbox']";
    private static final String OVERRIDE_RECOMMENDED_RESOURCE_NOTE_XPATH = RESOURCE_MANAGEMENT_PANEL_XPATH + "//textarea[@name='override-suggested-resource-notes']";

    @FindBy(css = ACTIVE_WORKSPACE_CSS)
    WebElement activeWorkspace;

    @FindBy(css = RESOURCE_MANAGEMENT_PANEL_CSS)
    WebElement resourceManagementPanel;

    @FindBy(css = HEADLINE_CSS)
    WebElement headline;

    @FindBy(css = HEADLINE_COMMENT_CSS)
    WebElement headlineComment;

    @FindBy(css = SUBHEAD_CSS)
    WebElement subHeadline;

    @FindBy(css = SUBHEAD_LEFT_CSS)
    WebElement subHeadline_left;

    @FindBy(css = SUBHEAD_RIGHT_CSS)
    WebElement subHeadline_right;

    @FindBy(css = BUTTONS_BAR_CSS)
    WebElement buttonBar;

    @FindBy(xpath = IMMEDIATE_CALLOUT_XPATH)
    WebElement immediateCallout;

    @FindBy(xpath = RESOURCE_NAME_XPATH)
    WebElement resourceName;

    @FindBy(xpath = RESOURCE_NAME_WITH_ACTIONS_XPATH)
    WebElement resourceNameWithActions;

    @FindBy(xpath = RESOURCE_NAME_ACKNOWLEDGE_ETA_XPATH)
    WebElement resourceNameAcknowledgeEta;

    @FindBy(xpath = RESOURCE_PROFILE_XPATH)
    WebElement resourceProfile;

    @FindBy(xpath = RESOURCE_STATUS_XPATH)
    WebElement resourceStatus;

    @FindBy(xpath = LAST_RESOURCE_STATUS_XPATH)
    WebElement lastResourceStatus;

    @FindBy(xpath = CALL_BUTTON_XPATH)
    private WebElement callButton;

    @FindBy(xpath = CONFIGURED_RESOURCE_PICKER_XPATH)
    WebElement configuredResourcePicker;

    @FindBy(xpath = BUTTON_ADDITIONAL_RESOURCE)
    WebElement addAdditionalResource;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = ACTIONS_BUTTON_XPATH)
    private WebElement actionsButton;

    @FindBy(xpath = ACCEPT_JOB_XPATH)
    private WebElement acceptJob;

    @FindBy(xpath = FUNDING_REQUESTS_XPATH)
    private WebElement fundingRequests;

    @FindBy(xpath = REMOVE_RESOURCE_XPATH)
    private WebElement removeResource;

    @FindBy(xpath = ADVISE_REMOVAL_XPATH)
    private WebElement adviseRemoval;

    @FindBy(xpath = DECLINE_JOB_XPATH)
    private WebElement declineJob;

    @FindBy(xpath = SCHEDULE_CALLBACK_XPATH)
    private WebElement scheduleCallBack;

    @FindBy(xpath = RESOURCE_CALLBACK_XPATH)
    private WebElement resourceCallBack;

    @FindBy(xpath = RESOURCE_CALLBACK_ICON_XPATH)
    private WebElement callBackIcon;

    @FindBy(xpath = OVERRIDE_RECOMMENDED_RESOURCE_XPATH)
    WebElement overrideRecommendedResource;

    @FindBy(xpath = OVERRIDE_RECOMMENDED_RESOURCE_REQUESTED_BY_XPATH)
    WebElement overrideRecommendedResourceRequestedBy;

    @FindBy(xpath = OVERRIDE_RECOMMENDED_RESOURCE_REASON_XPATH)
    WebElement overrideRecommendedResourceReason;

    @FindBy(xpath = OVERRIDE_RECOMMENDED_RESOURCE_NOTE_XPATH)
    WebElement overrideRecommendedResourceNote;

    @FindBy(xpath = RESOURCE_PROFILE_XPATH)
    private List<WebElement> resourceProfiles;

    @FindBy(xpath = ADDITIONAL_RESOURCE_STATUS_XPATH)
    WebElement additionalResourceStatus;

    @FindBy(xpath = ACKNOWLEDGE_ETA_XPATH)
    private WebElement acknowledgeETA;

    @FindBy(xpath = ETA_ACTION_XPATH)
    private WebElement etaAction;

    @FindBy(xpath = BUTTON_CLOSE_RESOURCES)
    WebElement closeResources;

    @FindBy(xpath = TRANSFER_WORK_XPATH) private WebElement transferWork;

    @FindBy(xpath = OUTSTANDING_PO_LABEL_XPATH)
    WebElement outstandingPOLabel;

    WebElement cardSite;
    WebElement cardJob;



    public HelpdeskManageResourcesPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitUntilUrlContains("manageResources");
            assertTrue(driver.getCurrentUrl().contains("manageResources"));
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isOutstandingPOLabelDisplayed() {
        return isElementVisible(By.xpath(OUTSTANDING_PO_LABEL_XPATH));
    }

    private WebElement getButton(String button) {
        return buttonBar.findElement(By.xpath(String.format(BUTTON_XPATH, button)));
    }

    public String getHeadline() {
        return headline.getText();
    }

    public boolean isDisplayed() {
        return isElementPresent(By.cssSelector(RESOURCE_MANAGEMENT_PANEL_CSS));
    }

    public boolean actionsButtonIsDisplayed() {
        waitForAngularRequestsToFinish();
        try {
            return isElementPresent(By.xpath(ACTIONS_BUTTON_XPATH));
        } catch (org.openqa.selenium.NoSuchElementException ex) {
            return false;
        }
    }

    public boolean isImmediateCallout() {
        return isElementPresent(By.xpath(IMMEDIATE_CALLOUT_XPATH));
    }

    public boolean isNotImmediateCallout() {
        return !isElementPresent(By.xpath(IMMEDIATE_CALLOUT_XPATH));
    }

    public String getResourceName() {
        return resourceName.getText();
    }

    public String getResourceNameWithActions() {
        return resourceNameWithActions.getText();
    }

    public String getResourceNameWithAcknowledgeEta() {
        return resourceNameAcknowledgeEta.getText();
    }

    public boolean isResourceNamePresent() {
        return isElementPresent(By.xpath(RESOURCE_NAME_XPATH));
    }

    public String getResourceProfile() {
        if (isElementPresent(By.xpath(RESOURCE_PROFILE_XPATH))) {
            return resourceProfile.getText();
        } else {
            return null;
        }
    }

    public String getResourceStatus() {
        waitForAngularRequestsToFinish();
        return resourceStatus.getText();
    }

    public String getLastResourceStatus() {
        waitForAngularRequestsToFinish();
        return lastResourceStatus.getText();
    }

    public Callable<Boolean> assertResourceStatusContains(String status) {
        return assertTextContains(resourceStatus, status);
    }

    public Callable<Boolean> assertResourceStatusContainsOneOf(String status) {
        return assertTextContainsOneOf(resourceStatus, status);
    }

    public String getHeadlineComment() {
        return headlineComment.getText();
    }

    public String getConfiguredResource() {
        return configuredResourcePicker.getText();
    }

    public void save() {
        getButton("Save").click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskAddAdditionalResourcePanel clickAddAdditionalResource() {
        POHelper.scrollToElement(addAdditionalResource);
        addAdditionalResource.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAddAdditionalResourcePanel.class).get();
    }

    public String selectContractor() {
        configuredResourcePicker.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption("Contractor");
        return resourceName.getText();
    }

    public String selectResource(String resource) {
        POHelper.scrollToElement(configuredResourcePicker);
        configuredResourcePicker.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(resource);
        return resourceName.getText();
    }

    public Boolean isOverrideRecommendedResourceDisplayed() {
        return isElementPresent(By.xpath(OVERRIDE_RECOMMENDED_RESOURCE_XPATH));
    }

    public void enterOverrideRecommendedResourceRequestedBy(String value) {
        overrideRecommendedResourceRequestedBy.sendKeys(value);
    }

    public void enterOverrideRecommendedResourceNote(String value) {
        overrideRecommendedResourceNote.sendKeys(value);
    }

    public String selectRandomOverrideRecommendedResourceReason() {
        overrideRecommendedResourceReason.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public HelpdeskAcceptJobPanel selectAcceptJobAction() {
        POHelper.clickJavascript(actionsButton);
        waitForAngularRequestsToFinish();
        acceptJob.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAcceptJobPanel.class).get();
    }

    public HelpdeskFundingRequestsPanel selectFundingRequestAction() throws InterruptedException {
        POHelper.scrollToElement(actionsButton);
        POHelper.clickJavascript(actionsButton);
        waitForAngularRequestsToFinish();
        fundingRequests.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskFundingRequestsPanel.class).get();
    }

    public HelpdeskRemoveResourcePanel selectRemoveResourceAction() throws InterruptedException {
        POHelper.clickJavascript(actionsButton);
        waitForAngularRequestsToFinish();
        removeResource.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskRemoveResourcePanel.class).get();
    }

    public HelpdeskRemoveResourcePanel selectRemoveResourceActionForResource(String resourceName) throws InterruptedException {
        WebElement actions = driver.findElement(By.xpath(String.format(ACTIONS_BUTTON_FOR_RESOURCE_XPATH, resourceName)));
        POHelper.clickJavascript(actions);
        waitForAngularRequestsToFinish();
        WebElement remove = driver.findElement(By.xpath(String.format(REMOVE_RESOURCE_FOR_RESOURCE_XPATH, resourceName)));
        POHelper.clickJavascript(remove);
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskRemoveResourcePanel.class).get();
    }

    public HelpdeskAdviseRemovalPanel selectAdviseRemovalAction() throws InterruptedException {
        POHelper.clickJavascript(actionsButton);
        waitForAngularRequestsToFinish();
        adviseRemoval.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAdviseRemovalPanel.class).get();
    }

    public HelpdeskDeclineJobPanel selectDeclineJobAction() throws InterruptedException {
        actionsButton.click();
        waitForAngularRequestsToFinish();
        declineJob.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskDeclineJobPanel.class).get();
    }

    public HelpdeskScheduleCallBackPanel selectScheduleCallBackAction() throws InterruptedException {
        actionsButton.click();
        waitForAngularRequestsToFinish();
        scheduleCallBack.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskScheduleCallBackPanel.class).get();
    }

    public boolean isCallBackIconDisplayed() {
        waitForAngularRequestsToFinish();
        return callBackIcon.isDisplayed();
    }

    public String getResourceCallBackText() {
        return resourceCallBack.getText();
    }

    public HelpdeskCallJobContactModal callResource() {
        callButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskCallJobContactModal.class).get();
    }

    public HelpdeskAcceptJobPanel additionalResourceSelectAcceptJobAction(String additionalResourceName) {
        WebElement additionaResourceActionsButton = driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH, additionalResourceName)));
        POHelper.scrollToElement(additionaResourceActionsButton);
        additionaResourceActionsButton.click();
        waitForAngularRequestsToFinish();

        WebElement additionaResourceAcceptButton = driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_ACCEPT_JOB_XPATH, additionalResourceName)));
        additionaResourceAcceptButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAcceptJobPanel.class).get();
    }

    public HelpdeskRemoveResourcePanel selectRemoveAdditionalResourceAction(String additionalResourceName) throws InterruptedException {
        driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH, additionalResourceName))).click();
        waitForAngularRequestsToFinish();
        driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_REMOVE_RESOURCE_XPATH, additionalResourceName))).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskRemoveResourcePanel.class).get();
    }

    public HelpdeskAdviseRemovalPanel additionalResourceSelectAdviseRemovalAction(String additionalResourceName) throws InterruptedException {
        WebElement we = driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH, additionalResourceName)));
        POHelper.scrollToElement(we);
        we.click();
        waitForAngularRequestsToFinish();
        WebElement we1 = driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_ADVISE_REMOVAL_XPATH, additionalResourceName)));
        POHelper.scrollToElement(we1);
        we1.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAdviseRemovalPanel.class).get();
    }

    public HelpdeskFundingRequestsPanel selectAdditionalResourceFundingRequestAction(String additionalResourceName) throws InterruptedException {
        driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH, additionalResourceName))).click();
        waitForAngularRequestsToFinish();
        driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_FUNDING_REQUESTS_XPATH, additionalResourceName))).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskFundingRequestsPanel.class).get();
    }

    public String getAdditionalResourceStatus(String addResourceName) {
        return driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_STATUS_XPATH, addResourceName))).getText();
    }

    public void selectAcknowledgeEtaAction(String resourceName) {
        WebElement acknowledgeEta = driver.findElement(By.xpath(String.format(ACKNOWLEDGE_ETA_XPATH, resourceName)));
        actionsButton.click();
        waitForAngularRequestsToFinish();
        acknowledgeEta.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isETAAcknowledgedButtonEnabled() {
        actionsButton.click();
        waitForAngularRequestsToFinish();
        return isElementPresent(By.xpath(ACKNOWLEDGE_ETA_LINK_XPATH));
    }

    public boolean isRemoveResourceButtonEnabled(String addResourceName) {
        driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH, addResourceName))).click();
        waitForAngularRequestsToFinish();
        return isElementPresent(By.xpath(ADDITIONAL_RESOURCE_REMOVE_RESOURCE_XPATH));
    }

    public HelpdeskResourceETAPanel selectEtaAction(String resourceName) {
        WebElement eta = driver.findElement(By.xpath(String.format(ETA_ACTION_XPATH, resourceName)));
        POHelper.scrollToElement(actionsButton);
        actionsButton.click();
        waitForAngularRequestsToFinish();
        eta.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskResourceETAPanel.class).get();
    }

    public void clickCloseResourcesPanel() {
        closeResources.click();
    }

    public HelpdeskCallJobContactModal clickCallResourceButton(String resource) {
        WebElement callButton = waitForElement(By.xpath(String.format(CALL_RESOURCE_BUTTON_XPATH, resource)), ELEMENT_IS_CLICKABLE);
        callButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskCallJobContactModal.class).get();
    }

    public HelpdeskTransferWorkPanel selectTransferWorkAction() throws InterruptedException {
        actionsButton.click();
        waitForAngularRequestsToFinish();
        transferWork.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskTransferWorkPanel.class).get();
    }

    public HelpdeskDeclineJobPanel additionalResourceSelectDeclineJobAction(String additionalResourceName) throws InterruptedException {
        driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_ACTIONS_BUTTON_XPATH, additionalResourceName))).click();
        waitForAngularRequestsToFinish();
        driver.findElement(By.xpath(String.format(ADDITIONAL_RESOURCE_DECLINE_JOB_XPATH, additionalResourceName))).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskDeclineJobPanel.class).get();
    }

    public boolean isActionUnavailable(String action) {
        actionsButton.click();
        waitForAngularRequestsToFinish();
        return isElementPresent(By.xpath(String.format(UNAVAILABLE_ACTION_XPATH, action)));
    }
}