package mercury.pageobject.web.helpdesk.caller;

import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.POHelper;
import mercury.helpers.State;

public class HelpdeskContactsTab extends Base_Page<HelpdeskContactsTab>{


    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String CONTACTS_TAB_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='view-content']";
    private static final String CONTACT_GRID_XPATH = CONTACTS_TAB_XPATH + "//div[@name='contactGrid']";

    @FindBy(xpath = CONTACTS_TAB_XPATH)
    private WebElement contactsTab;

    public HelpdeskContactsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(CONTACTS_TAB_XPATH));
            logger.info("Page loaded");
        } catch(NoSuchElementException e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        waitForElement(By.xpath(CONTACT_GRID_XPATH),State.ELEMENT_IS_VISIBLE);
        return GridHelper.getGrid(CONTACT_GRID_XPATH);
    }

    public String getGridXpath() {
        return CONTACT_GRID_XPATH;
    }

}
