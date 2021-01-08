package mercury.pageobject.web.helpdesk.incidents;

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

public class HelpdeskCancelIncidentPage extends Base_Page<HelpdeskCancelIncidentPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String CANCEL_INCIDENT_XPATH = "//div[@class='sweet-overlay']/following-sibling::div[contains(@class,'sweet-alert') and contains(@class,'incident--cancel')]";
    private static final String CANCEL_INCIDENT_HEADER_XPATH = CANCEL_INCIDENT_XPATH + "//h2[text()='Cancel Incident']";
    private static final String CONFIRM_BUTTON_XPATH = CANCEL_INCIDENT_XPATH + "//div[@class='sa-button-container']//button[@class='confirm']";
    private static final String NO_BUTTON_XPATH = CANCEL_INCIDENT_XPATH + "//div[@class='sa-button-container']//button[@class='cancel']";

    @FindBy(xpath = CANCEL_INCIDENT_HEADER_XPATH)
    private WebElement cancelIncident;

    @FindBy(xpath = CONFIRM_BUTTON_XPATH)
    private WebElement cancelIncidentConfirmButton;

    @FindBy(xpath = NO_BUTTON_XPATH)
    private WebElement cancelIncidentNoButton;

    public HelpdeskCancelIncidentPage(WebDriver driver) {
        super(driver);

    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForElement(By.xpath(CANCEL_INCIDENT_HEADER_XPATH),State.ELEMENT_IS_VISIBLE);
            Assert.assertTrue("Page is not displayed", cancelIncident.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public HelpdeskIncidentTimelineTab clickCancelIncidentConfirmButton() throws Throwable {
        waitForElement(By.xpath(CONFIRM_BUTTON_XPATH),State.ELEMENT_IS_CLICKABLE);
        cancelIncidentConfirmButton.click();
        return PageFactory.initElements(driver, HelpdeskIncidentTimelineTab.class).get();
    }

}
