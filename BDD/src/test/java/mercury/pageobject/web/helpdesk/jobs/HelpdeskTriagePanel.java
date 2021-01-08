package mercury.pageobject.web.helpdesk.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class HelpdeskTriagePanel extends Base_Page<HelpdeskTriagePanel>{

    private static final Logger logger = LogManager.getLogger();

    protected static final String JOB_TRIAGE_PANEL_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[contains(@class, 'job-action-panel') and contains(@class, 'job-triage')]";
    protected static final String HEADER_XPATH = JOB_TRIAGE_PANEL_XPATH + "/div[@class='job-action-panel__header']//span";
    protected static final String CONTENT_XPATH = JOB_TRIAGE_PANEL_XPATH + "/div[@class='job-action-panel__content']/div/div";
    protected static final String CANCEL_BUTTON_XPATH = JOB_TRIAGE_PANEL_XPATH + "//button[contains(text(), 'Cancel')]";
    protected static final String SAVE_BUTTON_XPATH = JOB_TRIAGE_PANEL_XPATH + "//button[contains(text(), 'Save')]";
    protected static final String NOTES_XPATH = JOB_TRIAGE_PANEL_XPATH + "//textarea[@name='Notes']";
    protected static final String HOURS_XPATH = JOB_TRIAGE_PANEL_XPATH + "//select[@id='Hours']";
    protected static final String MINUTES_XPATH = JOB_TRIAGE_PANEL_XPATH + "//select[@id='Minutes']";
    protected static final String OUTCOME_XPATH = JOB_TRIAGE_PANEL_XPATH + "//select[@name='Action']";
    protected static final String ROOT_CAUSE_CATEGORY_XPATH = JOB_TRIAGE_PANEL_XPATH + "//select[@name='RootCauseCategory']";
    protected static final String ROOT_CAUSE_XPATH = JOB_TRIAGE_PANEL_XPATH + "//select[@name='RootCause']";


    @FindBy(xpath=HEADER_XPATH)
    WebElement header;

    @FindBy(xpath=CONTENT_XPATH)
    WebElement content;

    @FindBy(xpath=CANCEL_BUTTON_XPATH)
    WebElement cancel;
    @FindBy(xpath=SAVE_BUTTON_XPATH)
    WebElement save;

    @FindBy(xpath=NOTES_XPATH)
    WebElement notes;
    @FindBy(xpath=HOURS_XPATH)
    WebElement hours;
    @FindBy(xpath=MINUTES_XPATH)
    WebElement minutes;
    @FindBy(xpath=OUTCOME_XPATH)
    WebElement outcome;

    @FindBy(xpath=ROOT_CAUSE_CATEGORY_XPATH)
    WebElement rootCauseCategory;
    @FindBy(xpath=ROOT_CAUSE_XPATH)
    WebElement rootCause;


    public HelpdeskTriagePanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(JOB_TRIAGE_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }


    public String getHeaderText() {
        return header.getText();
    }

    public String getContentText() {
        return content.getText();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void save() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public String selectRandomHour() {
        return selectRandomOptionFromSelect(hours);
    }

    public String selectRandomMinute() {
        return selectRandomOptionFromSelect(minutes);
    }

    public String selectRandomRootCauseCategory() {
        return selectRandomOptionFromSelect(rootCauseCategory);
    }

    public String selectRandomRootCause() {
        return selectRandomOptionFromSelect(rootCause);
    }

    public void addNotes(String note) {
        notes.sendKeys(note);
    }

    public void selectOutcome(String option) {
        selectOptionFromSelect(outcome, option);
    }
}
