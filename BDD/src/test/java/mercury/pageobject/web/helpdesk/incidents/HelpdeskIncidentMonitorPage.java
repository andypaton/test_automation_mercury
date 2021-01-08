package mercury.pageobject.web.helpdesk.incidents;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class HelpdeskIncidentMonitorPage extends Base_Page<HelpdeskIncidentMonitorPage>{

    private static final Logger logger = LogManager.getLogger();

    private static final String MONITOR_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[@class='tab-container monitors']";
    private static final String INCIDENTS_MONITOR_PAGE = MONITOR_XPATH + "//p[@class='monitors__subheading' and contains(text(),'Showing all')]";
    private static final String INCIDENTS_LINK_XPATH = MONITOR_XPATH + "//div[@class='monitors__menu']//li//div[text()='%s']";
    private static final String INCIDENT_REFERENCE_NUMBER_XPATH = MONITOR_XPATH + "//div[@id='monitorGrid']//table//tr[1]//td[2]//*[text()='%d']";
    private static final String FILTER_TEXTBOX_XPATH = VISIBLE_DROPDOWN_SEARCHBOX_XPATH + "[1]";


    @FindBy(xpath=INCIDENTS_MONITOR_PAGE)
    private WebElement incidentsMonitorPage;

    @FindBy(xpath=INCIDENTS_LINK_XPATH)
    private WebElement incidentLink;

    @FindBy(xpath=INCIDENT_REFERENCE_NUMBER_XPATH)
    private WebElement incidentReferenceNumber;

    @FindBy(xpath=FILTER_TEXTBOX_XPATH)
    private WebElement filterTextbox;

    public HelpdeskIncidentMonitorPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", incidentsMonitorPage.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void clickFollowUpLink() {
        WebElement incidentFollowUpLink = waitForElement(By.xpath(String.format(INCIDENTS_LINK_XPATH, "Follow ups")), ELEMENT_IS_CLICKABLE);
        incidentFollowUpLink.click();
        waitForAngularRequestsToFinish();
    }

    public void clickReviewsLink() {
        WebElement incidentReviewsLink = waitForElement(By.xpath(String.format(INCIDENTS_LINK_XPATH, "Reviews")), ELEMENT_IS_CLICKABLE);
        incidentReviewsLink.click();
        waitForAngularRequestsToFinish();
    }

    public void clickPendingToDoLink() {
        WebElement incidentPendingToDoLink = waitForElement(By.xpath(String.format(INCIDENTS_LINK_XPATH, "Pending To Do")), ELEMENT_IS_CLICKABLE);
        incidentPendingToDoLink.click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskViewIncidentPage clickIncidentReference(int referenceNumber) {
        WebElement incidentReferenceNumber = driver.findElement(By.xpath(String.format(INCIDENT_REFERENCE_NUMBER_XPATH, referenceNumber)));
        incidentReferenceNumber.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskViewIncidentPage.class).get();
    }

    public void enterReferenceNumberIntoFilterTextbox(int refereneceNumber) {
        filterTextbox.sendKeys(String.valueOf(refereneceNumber));
    }

}
