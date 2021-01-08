package mercury.pageobject.web.helpdesk.incidents;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskJobsLinkedIncidentsPanel extends Base_Page<HelpdeskJobsLinkedIncidentsPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class,'job-action-panel') and contains(@class,'job-linked-incident')]";
    private static final String PANEL_CONTENT_XPATH = PANEL_XPATH + "//div[@class='job-action-panel__content']";
    private static final String PANEL_FOOTER_XPATH = PANEL_XPATH + "//div[@class='footer-button-bar']";
    private static final String JOB_LINKED_INCIDENT_MESSAGE_XPATH = PANEL_CONTENT_XPATH + "//div[@class='job-linked-incident__message']";

    // GRID
    private static final String GRID_XPATH = PANEL_CONTENT_XPATH + "//table[@class='job-linked-incident__table']";

    //BUTTONS
    private static final String LINK_TO_JOB_BUTTONS_IN_GRID_XPATH = GRID_XPATH + "//button";
    private static final String UNLINK_BUTTON_XPATH = LINK_TO_JOB_BUTTONS_IN_GRID_XPATH + "[contains(text(),'Unlink')]";
    private static final String CLOSE_BUTTON_XPATH = PANEL_FOOTER_XPATH + "//button[contains(text(), 'Close')]";
    private static final String CREATE_NEW_INCIDENT_ICON_XPATH = "//div[contains(@class, 'tab-container')]//div[contains(@class,'job-action-panel')]//button//i[contains(@class,'plus')]";

    @FindBy(xpath = JOB_LINKED_INCIDENT_MESSAGE_XPATH)
    private WebElement linkedMessage;
    
    @FindBy(xpath = CREATE_NEW_INCIDENT_ICON_XPATH)
    private WebElement createNewIncidentIcon;

    @FindBy(xpath = CLOSE_BUTTON_XPATH)
    private WebElement closeButton;
    
    @FindBy(xpath = UNLINK_BUTTON_XPATH)
    private WebElement unlinkButton;
    

    public HelpdeskJobsLinkedIncidentsPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try{
            waitForAngularRequestsToFinish();
            Assert.assertTrue("PANEL is not displayed", driver.findElement(By.xpath(PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");

        }catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    // Returning the text of first button in the panel
    public String getButtonName() {
        List<WebElement> buttons = driver.findElements(By.xpath(LINK_TO_JOB_BUTTONS_IN_GRID_XPATH));
        return buttons.get(0).getText();
    }

    public String getLinkedMessageText() {
        return linkedMessage.getText();
    }
    
    public HelpdeskLogAnIncidentPage clickCreateNewIncidentIcon() {
        createNewIncidentIcon.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskLogAnIncidentPage.class);
    }
    
    public void clickUnlinkButton() {
        unlinkButton.click();
        waitForAngularRequestsToFinish();
    }
}
