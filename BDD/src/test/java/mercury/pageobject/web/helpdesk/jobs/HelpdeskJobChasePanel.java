package mercury.pageobject.web.helpdesk.jobs;


import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskJobChasePanel extends Base_Page<HelpdeskJobChasePanel> {


    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String CHASE_PANEL_CONTENT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='job-action-panel__content']";
    private static final String CHASE_LATEST_NOTES_ADDED_XPATH = CHASE_PANEL_CONTENT_XPATH + "//div[@class='job-chase__update-updates'][last()]";
    private static final String ACTIVE_CHASE_XPATH = "//div[@class='job-chase__summary']";
    private static final String MANAGER_CHASE_XPATH = CHASE_PANEL_CONTENT_XPATH + ACTIVE_CHASE_XPATH + "//td[contains(text(),'Manager Chase')]";
    private static final String ETA_CHASE_XPATH = CHASE_PANEL_CONTENT_XPATH + ACTIVE_CHASE_XPATH + "//td[contains(text(),'ETA Chase')]";
    private static final String CHASE_TYPE_XPATH = CHASE_PANEL_CONTENT_XPATH + ACTIVE_CHASE_XPATH + "//td[contains(text(),'%s')]";

    //Buttons
    private static final String CHASE_PANEL_ADD_BUTTON_XPATH = CHASE_PANEL_CONTENT_XPATH + "//button[@class='btn btn-primary btn-compact pull-right']";
    private static final String CHASE_PANEL_VIEW_CHASE_BUTTON_XPATH = "/..//following-sibling::button[contains(text(),'View')]";
    private static final String CHASE_PANEL_VIEW_UPDATE_ETA_CHASE_BUTTON_XPATH = ETA_CHASE_XPATH + "//ancestor::tr//button[contains(text(),'View / Update')]";
    private static final String CHASE_PANEL_VIEW_UPDATE_CHASE_TYPE_BUTTON_XPATH = CHASE_TYPE_XPATH + "//ancestor::tr//button[contains(text(),'View / Update')]";
    private static final String CHASE_PANEL_VIEW_UPDATE_MANAGER_CHASE_BUTTON_XPATH = MANAGER_CHASE_XPATH + "//ancestor::tr//button[contains(text(),'View / Update')]";
    private static final String CHASE_PANEL_CANCEL_CHASE_BUTTON_XPATH = CHASE_PANEL_CONTENT_XPATH + "//button[contains(text(),'Cancel Chase')]";


    //Core
    private static final String GRID_XPATH = CHASE_PANEL_CONTENT_XPATH + ACTIVE_CHASE_XPATH;
    private static final String CHASE_XPATH = CHASE_PANEL_CONTENT_XPATH + "//h2[contains(text(),'%s')]" + CHASE_PANEL_VIEW_CHASE_BUTTON_XPATH;

    @FindBy(xpath = CHASE_PANEL_ADD_BUTTON_XPATH)
    private WebElement addChase;

    @FindBy(xpath = CHASE_PANEL_VIEW_UPDATE_ETA_CHASE_BUTTON_XPATH)
    private WebElement chaseViewUpdateETAButton;

    @FindBy(xpath = CHASE_PANEL_VIEW_UPDATE_MANAGER_CHASE_BUTTON_XPATH)
    private WebElement chaseViewUpdateManagerButton;

    @FindBy(xpath = CHASE_PANEL_VIEW_CHASE_BUTTON_XPATH)
    private WebElement chaseViewButton;

    @FindBy(xpath = CHASE_PANEL_CANCEL_CHASE_BUTTON_XPATH)
    private WebElement chaseCancelButton;

    @FindBy(xpath = CHASE_LATEST_NOTES_ADDED_XPATH)
    private WebElement chaseNotes;

    public HelpdeskJobChasePanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try{
            assertTrue("Chase panel not loaded!", driver.findElement(By.xpath(CHASE_PANEL_ADD_BUTTON_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isNewChaseButtonDisplayed() {
        return isElementPresent(By.xpath(CHASE_PANEL_ADD_BUTTON_XPATH));
    }

    public boolean isViewUpdateButtonDisplayed() {
        return isElementPresent(By.xpath(CHASE_PANEL_VIEW_UPDATE_ETA_CHASE_BUTTON_XPATH));
    }

    public boolean isCancelChaseButtonDisplayed() {
        return isElementPresent(By.xpath(CHASE_PANEL_CANCEL_CHASE_BUTTON_XPATH));
    }

    public HelpdeskCreateJobChasePanel clickAddChase() {
        addChase.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskCreateJobChasePanel.class).get();
    }

    public HelpdeskUpdateJobChasePanel selectETAChaseForUpdate() {
        chaseViewUpdateETAButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskUpdateJobChasePanel.class).get();
    }

    public HelpdeskUpdateJobChasePanel selectChaseTypeForUpdate(String chaseType) {
        WebElement we = driver.findElement(By.xpath(String.format(CHASE_PANEL_VIEW_UPDATE_CHASE_TYPE_BUTTON_XPATH, chaseType)));
        we.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskUpdateJobChasePanel.class).get();
    }

    public HelpdeskUpdateJobChasePanel selectManagerChaseForUpdate() {
        chaseViewUpdateManagerButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskUpdateJobChasePanel.class).get();
    }

    public HelpdeskUpdateJobChasePanel clickViewChase(String chaseNumber) {
        By by = By.xpath(String.format(CHASE_XPATH,chaseNumber));
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskUpdateJobChasePanel.class).get();
    }

    public HelpdeskCancelJobChasePanel clickCancelChase() {
        chaseCancelButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskCancelJobChasePanel.class).get();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public String getChaseNotes() {
        String fullChaseUpdateNotes =  chaseNotes.getText();
        int newLine = fullChaseUpdateNotes.indexOf("\n");
        String chaseUpdateNotes= fullChaseUpdateNotes.substring(0 , newLine);
        if (chaseUpdateNotes.contains("Chase updated by test automation")) {
            return "Chase updated by test automation";
        } else {
            return chaseUpdateNotes;
        }
    }

}
