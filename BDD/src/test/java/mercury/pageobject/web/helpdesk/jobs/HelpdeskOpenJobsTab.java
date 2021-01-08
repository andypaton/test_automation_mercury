package mercury.pageobject.web.helpdesk.jobs;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.gridV3.Grid;

public class HelpdeskOpenJobsTab extends Base_Page<HelpdeskOpenJobsTab> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_TAB_CSS = ".tab-content .tab-pane.active .tab-container";
    private static final String ACTIVE_TAB_XPATH = "//div[@class='tab-content']//div[contains(@class,'tab-pane') and contains(@class, 'active')]//div[@class='tab-container']";

    private static final String OPEN_JOBS_TAB_CSS = ACTIVE_TAB_CSS + " ph-site-calls";
    private static final String OPEN_JOBS_TAB_XPATH = ACTIVE_TAB_XPATH + "//ph-site-calls";

    //Table
    private static final String GRID_XPATH = OPEN_JOBS_TAB_XPATH + "//div[@kendo-grid='callGrid']";


    @FindBy(xpath = GRID_XPATH)
    WebElement grid;

    public HelpdeskOpenJobsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.cssSelector(OPEN_JOBS_TAB_CSS)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public HelpdeskOpenJobsTab tableSort(String columnDataField) {
        GridHelper.tableSort(GRID_XPATH, columnDataField);
        return this;
    }

    public int getNumberOfItems() {
        waitForAngularRequestsToFinish();
        String label = GridHelper.getLabel(GRID_XPATH);

        if (label.trim().equals("No items to display")) {
            return 0;
        }else {
            return Integer.valueOf(StringUtils.substringBetween(label, "of", "items").trim());
        }
    }

    public int getNumberOfItemsPerPage() {
        return GridHelper.getNumberOfItemsPerPage(GRID_XPATH);
    }

    public void setNumberOfItemsPerPage(int numItems) {
        GridHelper.setNumberOfItemsPerPage(GRID_XPATH, numItems);
    }
}
