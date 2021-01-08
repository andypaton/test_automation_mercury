package mercury.pageobject.web.helpdesk.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskResourcesTab extends Base_Page<HelpdeskResourcesTab> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_TAB_CSS = ".tab-content .tab-pane.fade.active.in";
    private static final String ACTIVE_TAB_XPATH = "//div[@class='tab-content']//div[contains(@class,'tab-pane') and contains(@class, 'active')]";

    private static final String RESOURCES_TAB_CSS = ACTIVE_TAB_CSS + " ph-job-detail-resources-tab";
    private static final String RESOURCES_TAB_XPATH = ACTIVE_TAB_XPATH + "/ph-job-detail-resources-tab";

    //Table
    private static final String GRID_XPATH = RESOURCES_TAB_XPATH + "//div[@name='resourceGrid']";

    //Table rows
    private static final String TABLE_BODY_XPATH = GRID_XPATH + "//table/tbody";

    //Table pager
    private static final String NEXT_PAGE_CSS = RESOURCES_TAB_CSS + " div.k-pager-wrap.k-grid-pager.k-widget.k-floatwrap > a:nth-child(4) > span";

    @FindBy(css = RESOURCES_TAB_CSS)
    WebElement activeTab;

    @FindBy(xpath = TABLE_BODY_XPATH)
    private WebElement tableBody;

    @FindBy(css = NEXT_PAGE_CSS)
    private WebElement nextPageButton;


    public HelpdeskResourcesTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.cssSelector(RESOURCES_TAB_CSS)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public HelpdeskResourcesTab tableSort(String columnDataField) {
        GridHelper.tableSort(GRID_XPATH, columnDataField);
        return this;
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

}
