package mercury.pageobject.web.helpdesk.incidents;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskViewIncidentPage extends Base_Page<HelpdeskViewIncidentPage> {

    private static final Logger logger = LogManager.getLogger();

    //WORKSPACE
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String VIEW_INCIDENT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='view incident']";

    // HEADERS
    private static final String HEADER_HEADLINE_XPATH = VIEW_INCIDENT_XPATH + "//div[contains(@class, 'view-header__headline')]";
    private static final String SUBHEAD_LEFT_XPATH = VIEW_INCIDENT_XPATH + "//div[contains(@class, 'view-subheader__left')]";
    private static final String SUBHEAD_RIGHT_XPATH = VIEW_INCIDENT_XPATH + "//div[contains(@class, 'view-subheader__right')]";
    private static final String INCIDENT_TYPE_XPATH = SUBHEAD_LEFT_XPATH + "//span[@class='text']";
    private static final String INCIDENT_STATUS = SUBHEAD_RIGHT_XPATH + "/h3/span[contains(@class,'text')]";

    private static final String INCIDENT_BUTTONS_CONTAINER_XPATH = VIEW_INCIDENT_XPATH + "//div[contains(@class,'view-button-bar') and contains(@class,'buttons-container')]";
    private static final String LINKED_JOBS_XPATH = INCIDENT_BUTTONS_CONTAINER_XPATH + "//a[contains(@class,'btn-primary') and @uib-tooltip='Linked Jobs']";

    //CONTENT
    private static final String CORE_DETAILS_XPATH = VIEW_INCIDENT_XPATH + "//div[@class='view-main-content']";
    private static final String SITE_VALUE_XPATH  = CORE_DETAILS_XPATH + "//dt[text()='Site']/following-sibling::dd";
    private static final String CALLER_VALUE_XPATH = CORE_DETAILS_XPATH + "//dt[text()='Caller']/following-sibling::dd";
    private static final String SITE_CLOSED_VALUE_XPATH = CORE_DETAILS_XPATH + "//dt[text()='Site closed']/following-sibling::dd";
    private static final String SITE_REOPNED_VALUE_XPATH = CORE_DETAILS_XPATH + "//dt[text()='Site re-opened']/following-sibling::dd";
    private static final String DEPARTMENT_CLOSED_VALUE_XPATH = CORE_DETAILS_XPATH + "//dt[text()='Dept closed' or text()='Location closed']/following-sibling::dd";
    private static final String DEPARTMENT_REOPNED_VALUE_XPATH = CORE_DETAILS_XPATH + "//dt[text()='Dept re-opened' or text()='Location re-opened']/following-sibling::dd";
    private static final String DESCRIPTION_VALUE_XPATH = CORE_DETAILS_XPATH + "//dt[text()='Description']/following-sibling::dd";

    // TABS
    private static final String ANSWERS_TAB_XPATH = VIEW_INCIDENT_XPATH + "//li//a[text()='Answers']";
    private static final String ESCALATIONS_TAB_XPATH = VIEW_INCIDENT_XPATH + "//li//a[text()='Escalations']";

    @FindBy(xpath = ACTIVE_WORKSPACE_XPATH)
    private WebElement activeWorkspace;

    @FindBy(xpath = VIEW_INCIDENT_XPATH)
    private WebElement viewIncident;

    @FindBy(xpath = HEADER_HEADLINE_XPATH)
    private WebElement incidentReference;

    @FindBy(xpath = SUBHEAD_LEFT_XPATH)
    private WebElement subheadLeft;

    @FindBy(xpath = INCIDENT_TYPE_XPATH)
    private WebElement incidentType;

    @FindBy(xpath = SUBHEAD_RIGHT_XPATH)
    private WebElement subheadRight;

    @FindBy(xpath = INCIDENT_STATUS)
    private WebElement incidentStatus;

    @FindBy(xpath = LINKED_JOBS_XPATH)
    private WebElement linkedJobs;

    @FindBy(xpath = CORE_DETAILS_XPATH)
    private WebElement coreDetails;

    @FindBy(xpath = CALLER_VALUE_XPATH)
    private WebElement callerValue;

    @FindBy(xpath = SITE_VALUE_XPATH)
    private WebElement siteValue;

    @FindBy(xpath = SITE_CLOSED_VALUE_XPATH)
    private WebElement siteClosedValue;

    @FindBy(xpath = SITE_REOPNED_VALUE_XPATH)
    private WebElement siteReopenedValue;

    @FindBy(xpath = DEPARTMENT_CLOSED_VALUE_XPATH)
    private WebElement departmentClosedValue;

    @FindBy(xpath = DEPARTMENT_REOPNED_VALUE_XPATH)
    private WebElement departmentReopenedValue;

    @FindBy(xpath = DESCRIPTION_VALUE_XPATH)
    private WebElement descriptionValue;

    @FindBy(xpath = ANSWERS_TAB_XPATH)
    private WebElement answersTab;

    @FindBy(xpath = ESCALATIONS_TAB_XPATH)
    private WebElement escalationTab;

    public HelpdeskViewIncidentPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", viewIncident.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isViewIncidentPageLoaded() {
        return isElementPresent(By.xpath(VIEW_INCIDENT_XPATH));
    }

    public boolean isViewIncidentLeftHeaderDisplayed() {
        return isElementPresent(By.xpath(SUBHEAD_LEFT_XPATH));
    }

    public void isIncidentInitialStatusDisplayed() throws Throwable{
        assertTextContains(subheadRight, "Incident Initial Review");
    }

    public String getIncidentInitialStatus() throws Throwable{
        waitForAngularRequestsToFinish();
        String[] status = incidentStatus.getText().split(",");
        return status[0];
    }

    public String getSiteValue() {
        return siteValue.getText();
    }

    public String getIncidentType() {
        return incidentType.getText();
    }

    public String getCallerName() {
        String caller[] = callerValue.getText().split(" ");
        return caller[0];
    }

    public String getSiteClosedValue() {
        return siteClosedValue.getText();
    }

    public String getSiteReopenedValue() {
        return siteReopenedValue.getText();
    }

    public String getDepartmentClosedValue() {
        return departmentClosedValue.getText();
    }

    public String getDepartmentReopenedValue() {
        return departmentReopenedValue.getText();
    }

    public String getDescriptionValue() {
        return descriptionValue.getText();
    }

    public HelpdeskIncidentAnswersTab selectAnswersTab() {
        answersTab.click();
        return PageFactory.initElements(driver, HelpdeskIncidentAnswersTab.class).get();
    }

    public Callable<Boolean> assertStatusEqualsIgnoreCase(String status) {
        return assertTextEqualsIgnoreCase(incidentStatus, status);
    }

    public Boolean isPageDisplayed() {
        return isElementPresent(By.xpath(VIEW_INCIDENT_XPATH));
    }

    public int getIncidenceReference() {
        return Integer.parseInt(incidentReference.getText());
    }

    public HelpdeskIncidentsLinkedJobsModal clickLinkedJob() throws Throwable {
        waitForElement(By.xpath(LINKED_JOBS_XPATH), State.ELEMENT_IS_CLICKABLE);
        linkedJobs.click();
        return PageFactory.initElements(driver, HelpdeskIncidentsLinkedJobsModal.class).get();
    }

    public HelpdeskIncidentEscalationTab clickEscalationsTab() {
        waitForElement(By.xpath(ESCALATIONS_TAB_XPATH), State.ELEMENT_IS_CLICKABLE);
        escalationTab.click();
        return PageFactory.initElements(driver, HelpdeskIncidentEscalationTab.class).get();
    }
}