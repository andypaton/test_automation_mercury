package mercury.pageobject.web.helpdesk.jobs;

import static mercury.helpers.POHelper.BANNER_HEIGHT;
import static mercury.helpers.POHelper.scrollTo;
import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.caller.HelpdeskContactsTab;
import mercury.pageobject.web.helpdesk.incidents.HelpdeskJobsLinkedIncidentsPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAdviseEtaPanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskManageResourcesPanel;

public class HelpdeskJobPage extends Base_Page<HelpdeskJobPage>{

    private static final Logger logger = LogManager.getLogger();

    protected static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    protected static final String JOB_VIEW_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[contains(@class, 'view job')]";

    // HEADERS
    private static final String JOB_REFERENCE_XPATH = JOB_VIEW_XPATH + "//div[contains(@class, 'view-header__headline')]/h1";
    private static final String JOB_DEFERRAL_XPATH = JOB_VIEW_XPATH + "//div[contains(@class, 'view-header__headline')]/div[contains(@class, 'job-deferral-confirmation__title')]";

    private static final String ACTIONS_BUTTON_XPATH = JOB_VIEW_XPATH + "//a[contains(.,'Actions')]";
    private static final String ACTION_XPATH = ACTIONS_BUTTON_XPATH + "/../ul/li/a[contains(text(), '%s')]";
    private static final String DISABLED_ACTION_XPATH = ACTIONS_BUTTON_XPATH + "/../ul/li/a[contains(text(), '%s') and contains(@class,'disabled')]";
    private static final String ACTIONS_XPATH = ACTIONS_BUTTON_XPATH + "/../ul/li/a[not(contains(@class, 'disabled'))]";
    private static final String ACTIONS_BUTTON_BADGE_COUNT_XPATH = JOB_VIEW_XPATH + "//span[@class='badge active-job-count']";

    private static final String LINKED_JOBS_ICON_XPATH = JOB_VIEW_XPATH + "//i[@ng-class='vmJobDetail.icon.forLinkedJobs']";
    private static final String LINKED_INCIDENTS_ICON_XPATH = JOB_VIEW_XPATH + "//div[@class='view-button-bar__buttons-container']//i[@class='icons__exclamation']";
    private static final String ATTACHMENTS_ICON_XPATH = JOB_VIEW_XPATH + "//div[@class='view-button-bar__buttons-container']//i[@class='icons__paperclip']";
    private static final String ATTACHMENTS_BUTTON_XPATH = JOB_VIEW_XPATH + "//div[@class='view-button-bar__buttons-container']//a[@uib-tooltip='Attachments']";
    private static final String LINKED_JOBS_MODAL_XPATH = JOB_VIEW_XPATH + "//div[@class='modal modal-panel job fade in']//div[@class='modal-dialog modal-xxl']/div[@class='modal-content']";

    private static final String JOB_CLOSEDOWN_XPATH = JOB_VIEW_XPATH + "//*[contains(text(),'At Job Closedown')]";

    protected static final String DESC_TITLE_XPATH = JOB_VIEW_XPATH + "//dt[contains(text(),'%s')]";
    protected static final String DESC_VALUE_XPATH = JOB_VIEW_XPATH + "//dt[contains(text(),'%s')]/following-sibling::dd[1]";

    private static final String CLIENT_STATUS_XPATH = JOB_VIEW_XPATH + "//div[contains(text(), 'Client status')]/span";
    private static final String RESOURCE_STATUS_XPATH = JOB_VIEW_XPATH + "//div[contains(text(), 'Resource status')]";
    private static final String ALERT_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-alert//p";

    private static final String QUESTIONS_TAB_XPATH = "//div[@class='view job']//a[contains(text(), 'Questions')]";
    private static final String CONTACTS_TAB_XPATH = "//div[@class='view job']//a[contains(text(), 'Contacts')]";
    private static final String DOCUMENTS_TAB_XPATH = "//div[@class='view job']//a[contains(text(), 'Documents')]";

    @FindBy(xpath = ACTIONS_BUTTON_XPATH)
    private WebElement actionsButton;

    @FindBy(xpath = ACTIONS_XPATH)
    private List<WebElement> actions;

    @FindBy(xpath = LINKED_JOBS_ICON_XPATH)
    private WebElement linkedJobsIcon;

    @FindBy(xpath = LINKED_JOBS_MODAL_XPATH)
    private WebElement linkedJobsModal;

    @FindBy(xpath = LINKED_INCIDENTS_ICON_XPATH)
    private WebElement linkedIncidentsIcon;

    @FindBy(xpath = ATTACHMENTS_ICON_XPATH)
    private WebElement attachmentsIcon;

    @FindBy(xpath = ATTACHMENTS_BUTTON_XPATH)
    private WebElement attachmentsButton;

    @FindBy(xpath = CLIENT_STATUS_XPATH)
    private WebElement clientStatus;

    @FindBy(xpath = RESOURCE_STATUS_XPATH)
    private WebElement resourceStatus;

    @FindBy(xpath = ALERT_XPATH)
    private WebElement alert;

    @FindBy(xpath = ACTIONS_BUTTON_BADGE_COUNT_XPATH)
    private WebElement actionsBadgeCount;

    @FindBy(xpath = QUESTIONS_TAB_XPATH)
    private WebElement questionsTab;

    @FindBy(xpath = DOCUMENTS_TAB_XPATH)
    private WebElement documentsTab;

    @FindBy(xpath = CONTACTS_TAB_XPATH)
    private WebElement contactsTab;

    @FindBy(xpath = JOB_REFERENCE_XPATH)
    private WebElement jobReference;

    @FindBy(xpath = JOB_DEFERRAL_XPATH)
    private WebElement deferralDate;

    public HelpdeskJobPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForAngularRequestsToFinish();
            //            WebElement jobPanel = waitForElement(By.xpath(ACTIVE_WORKSPACE_XPATH), State.ELEMENT_IS_VISIBLE);
            WebElement jobPanel = waitForElement(By.xpath(JOB_VIEW_XPATH), State.ELEMENT_IS_VISIBLE);
            Assert.assertTrue("Page is not displayed", jobPanel != null);

            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectActions() {
        int pos = Integer.valueOf(actionsButton.getLocation().getY());
        pos = pos > BANNER_HEIGHT ? pos - BANNER_HEIGHT : pos;
        scrollTo(pos);
        actionsButton.click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getAvailableActions() {
        if (actionsButton.getAttribute("aria-expanded").equals("false")) {
            actionsButton.click();
            waitForAngularRequestsToFinish();
        }
        List<String> result = new ArrayList<>();
        for (WebElement we : actions) {
            result.add(we.getText());
        }
        actionsButton.click();
        waitForAngularRequestsToFinish();
        return result;
    }

    public HelpdeskJobCancelPanel selectCancelJobAction() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Cancel Job")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobCancelPanel.class).get();
    }

    public HelpdeskLogJobPage selectEditAction() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Edit")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogJobPage.class).get();
    }

    public HelpdeskJobActionsPanel selectQuotesAction() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Quotes")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobActionsPanel.class).get();
    }

    public HelpdeskManageResourcesPanel selectManageResourcesAction() {
        POHelper.clickJavascript(actionsButton);
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Manage Resources")), ELEMENT_IS_CLICKABLE);
        POHelper.scrollToElement(choice);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskManageResourcesPanel.class).get();
    }

    public void selectManageResourcesFromAction() {
        actionsButton.click();
        WebElement choice = null;

        try {
            choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Manage Resources")), ELEMENT_IS_CLICKABLE);
        } catch (Exception e) {
            POHelper.clickJavascript(actionsButton);
            choice = driver.findElement(By.xpath(String.format(ACTION_XPATH, "Manage Resources")));
        }
        choice.click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskJobChasePanel selectChaseAction() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Chase")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobChasePanel.class).get();
    }

    public HelpdeskAdviseEtaPanel selectAdviseEtaAction() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Advise Eta")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAdviseEtaPanel.class).get();
    }

    public HelpdeskTriagePanel selectTriageAction() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Triage")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskTriagePanel.class).get();
    }

    public boolean isJobCloseDownSectionDisplayed() {
        return isElementPresent(By.xpath(JOB_CLOSEDOWN_XPATH));
    }

    public boolean isJobCloseDownTitleDisplayed(String title) {
        WebElement jobClosedown = driver.findElement(By.xpath(JOB_CLOSEDOWN_XPATH));
        return jobClosedown.findElement(By.xpath(String.format(DESC_TITLE_XPATH, title))).isDisplayed();
    }

    public String getJobCloseDownValue(String title) {
        WebElement jobClosedown = driver.findElement(By.xpath(JOB_CLOSEDOWN_XPATH));
        return jobClosedown.findElement(By.xpath(String.format(DESC_VALUE_XPATH, title))).getText();
    }

    public String getClientStatus() {
        return clientStatus.getText();
    }

    public String getResourceStatus() {
        return resourceStatus.getText().trim();
    }

    public String getAlertText() {
        return alert.getText();
    }

    public int getActionsBadgeCount() {
        int badgeCount = Integer.parseInt(actionsBadgeCount.getText());
        return badgeCount;
    }

    public boolean badgeCountCleared() {
        waitUntilElementNotDisplayed(By.xpath(ACTIONS_BUTTON_BADGE_COUNT_XPATH));
        return true;
    }

    public int getJobReference() {
        int jobRef = Integer.parseInt(jobReference.getText());
        return jobRef;
    }

    public Callable<Boolean> assertClientStatusEquals(String status) {
        return assertTextEquals(clientStatus, status);
    }

    public Callable<Boolean> assertResourceStatusContains(String status) {
        return assertTextContains(resourceStatus, status);
    }

    public Callable<Boolean> assertResourceStatusContainsOneOf(String status) {
        return assertTextContainsOneOf(resourceStatus, status);
    }

    public ReopenJobModal selectReopenJob() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Reopen Job")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, ReopenJobModal.class);
    }

    public HelpdeskQuestionsTab selectQuestionsTab() {
        questionsTab.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskQuestionsTab.class);
    }

    public HelpdeskDocumentsTab selectDocumentsTab() {
        documentsTab.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskDocumentsTab.class);
    }

    public HelpdeskContactsTab selectContactsTab() {
        POHelper.scrollToElement(contactsTab);
        contactsTab.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskContactsTab.class);
    }

    public HelpdeskLinkedJobsModal openLinkedJobsModal() {
        linkedJobsIcon.click();
        waitForElement(By.xpath(LINKED_JOBS_MODAL_XPATH), State.ELEMENT_IS_VISIBLE);
        linkedJobsModal.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLinkedJobsModal.class).get();
    }

    public void selectUnParkJobAction() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "UnPark Job")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
    }

    public ConfirmWarrantyPanel selectConfirmWarranty() {
        actionsButton.click();
        WebElement choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Confirm Warranty")), ELEMENT_IS_CLICKABLE);
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, ConfirmWarrantyPanel.class);
    }

    public HelpdeskParkJobPanel selectParkJobAction() {
        actionsButton.click();
        WebElement choice = null;

        try {
            choice = waitForElement(By.xpath(String.format(ACTION_XPATH, "Park Job")), ELEMENT_IS_CLICKABLE);
        } catch (Exception e) {
            POHelper.clickJavascript(actionsButton);
            choice = driver.findElement(By.xpath(String.format(ACTION_XPATH, "Park Job")));
        }
        choice.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskParkJobPanel.class).get();
    }

    public Boolean isPageDisplayed() {
        return isElementPresent(By.xpath(JOB_VIEW_XPATH));
    }

    public String getDeferralDate() {
        return deferralDate.getText();
    }

    public Callable<Boolean> assertDeferralDateDoesNotContain(String deferralDate) {
        return assertTextDoesNotContain(resourceStatus, deferralDate);
    }

    public HelpdeskJobsLinkedIncidentsPanel clickLinkedIncidentsIcon() {
        linkedIncidentsIcon.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskJobsLinkedIncidentsPanel.class).get();
    }

    public HelpdeskAddAttachmentsModal clickAttachmentsIcon() {
        attachmentsIcon.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAddAttachmentsModal.class).get();
    }

    public HelpdeskLinkedJobsModal clickLinkedJobsIcon() {
        linkedJobsIcon.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLinkedJobsModal.class).get();
    }

    public boolean isJobActionDisabled(String action) {
        waitForAngularRequestsToFinish();
        actionsButton.click();
        boolean element = isElementPresent(By.xpath(String.format(DISABLED_ACTION_XPATH, action)));
        actionsButton.click();
        return element;
    }

    public int getAttachmentCount() {
        int badgeCount = Integer.parseInt(driver.findElement(By.xpath(ATTACHMENTS_BUTTON_XPATH)).getText());
        return badgeCount;
    }

}