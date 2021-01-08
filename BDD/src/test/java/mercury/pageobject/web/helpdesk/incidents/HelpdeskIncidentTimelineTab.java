package mercury.pageobject.web.helpdesk.incidents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.gridV3.Grid;


public class HelpdeskIncidentTimelineTab extends Base_Page<HelpdeskIncidentTimelineTab> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String VIEW_INCIDENT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='view incident']";
    private static final String TIMELINE_TAB_XPATH =  VIEW_INCIDENT_XPATH + "//ph-incident-timeline-tab";
    private static final String TIMELINE_GRID_XPATH = TIMELINE_TAB_XPATH + "//div[@id='timelineEventGrid']";
    private static final String TIMELINE_REFRESH_XPATH = TIMELINE_TAB_XPATH + "//div[@class='pull-right']//a[text()='Refresh']";
    private static final String TIMELINE_VIEW_BUTTON_LINKTEXT = "View";
    private static final String TIMELINE_ADD_NOTE_BUTTON = TIMELINE_TAB_XPATH + "//div[@class='pull-right']//button[contains(@class,'btn-primary') and contains(text(),'Add note')]";

    @FindBy(xpath = TIMELINE_TAB_XPATH)
    private WebElement timelineTab;

    @FindBy(xpath = TIMELINE_REFRESH_XPATH)
    private WebElement refresh;

    @FindBy(linkText = TIMELINE_VIEW_BUTTON_LINKTEXT)
    private WebElement viewButton;

    @FindBy(xpath = TIMELINE_ADD_NOTE_BUTTON)
    private WebElement addNote;

    public HelpdeskIncidentTimelineTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", timelineTab.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        return GridHelper.getGrid(TIMELINE_GRID_XPATH);
    }

    public void tableSort(String column) {
        GridHelper.tableSort( TIMELINE_GRID_XPATH , column);
    }

    public void clickRefresh() {
        refresh.click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskIncidentUpdate clickViewButton() {
        viewButton.click();
        return PageFactory.initElements(driver, HelpdeskIncidentUpdate.class).get();
    }

    public String getViewButtonText() {
        return viewButton.getText();
    }

    public HelpdeskIncidentUpdate clickAddNote() {
        addNote.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskIncidentUpdate.class).get();
    }

}


