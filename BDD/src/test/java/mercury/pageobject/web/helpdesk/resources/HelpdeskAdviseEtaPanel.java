package mercury.pageobject.web.helpdesk.resources;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.jobs.HelpdeskCallJobContactModal;

public class HelpdeskAdviseEtaPanel extends Base_Page<HelpdeskAdviseEtaPanel> {

    private static final Logger logger = LogManager.getLogger();

    //Advise ETA Panel
    private static final String ACTIVE_PANEL_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[contains(@class, 'job-action-panel') and contains(@class, 'store-advise-eta')]";
    private static final String ADVISE_ETA_HEADER_XPATH = ACTIVE_PANEL_XPATH +  "//h1[contains(text(),'Advise ETA for Job:')]";
    private static final String STORE_ADVISE_XPATH = ACTIVE_PANEL_XPATH + "//div[contains(@class, 'store-advise-eta__callSite')]";
    private static final String CALL_JOB_CONTACT_XPATH = STORE_ADVISE_XPATH + "//button[contains(@class, 'store-advise-eta__callButton')]";
    private static final String ADVISED_TO_XPATH = ACTIVE_PANEL_XPATH + "//input[@type='text']";
    private static final String ADVISED_XPATH = ACTIVE_PANEL_XPATH + "//input[@type='checkbox']";
    private static final String SAVE_BUTTON_XPATH = ACTIVE_PANEL_XPATH +  "//button[contains(text(),'Save')]";


    @FindBy(xpath = CALL_JOB_CONTACT_XPATH)
    private WebElement callJobContact;

    @FindBy(xpath = ADVISED_TO_XPATH)
    private WebElement advisedTo;

    @FindBy(xpath = ADVISED_XPATH)
    private WebElement advised;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;


    public HelpdeskAdviseEtaPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            assertTrue("Advise ETA Job panel not loaded!", driver.findElement(By.xpath(ADVISE_ETA_HEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public HelpdeskCallJobContactModal clickCallJobContact() {
        callJobContact.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskCallJobContactModal.class).get();
    }

    public void clickAdvisedCheckbox() {
        advised.click();
        waitForAngularRequestsToFinish();
    }

    public void enterAdvisedTo(String to) {
        advisedTo.sendKeys(to);
    }

    public void save() {
        saveButton.click();
        waitForAngularRequestsToFinish();
    }
}
