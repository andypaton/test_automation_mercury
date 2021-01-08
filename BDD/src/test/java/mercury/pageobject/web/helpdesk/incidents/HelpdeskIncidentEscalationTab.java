package mercury.pageobject.web.helpdesk.incidents;

import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;
import mercury.helpers.State;

public class HelpdeskIncidentEscalationTab extends Base_Page<HelpdeskIncidentEscalationTab>{

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String VIEW_INCIDENT_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='view incident']";
    private static final String ESCALATION_TAB_XPATH =  VIEW_INCIDENT_XPATH + "//ph-incident-escalation-detail-tab";
    private static final String ESCALATION_GRID_XPATH = ESCALATION_TAB_XPATH + "//div[@name='escalationsGrid']";

    @FindBy(xpath = ESCALATION_TAB_XPATH)
    private WebElement escalationTab;

    public HelpdeskIncidentEscalationTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ESCALATION_TAB_XPATH));
            logger.info("Page loaded");
        } catch(NoSuchElementException e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        waitForElement(By.xpath(ESCALATION_GRID_XPATH),State.ELEMENT_IS_VISIBLE);
        return GridHelper.getGrid(ESCALATION_GRID_XPATH);
    }

    public String getGridXpath() {
        return ESCALATION_GRID_XPATH;
    }

    public void tableSort(String column) {
        GridHelper.tableSort( ESCALATION_GRID_XPATH , column);
    }

}
