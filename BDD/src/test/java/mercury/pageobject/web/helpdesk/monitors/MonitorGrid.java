package mercury.pageobject.web.helpdesk.monitors;

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
import mercury.helpers.State;
import mercury.helpers.gridV3.Grid;

public class MonitorGrid extends Base_Page<MonitorGrid> {

    private static final Logger logger = LogManager.getLogger();

    private static final String MONITOR_GRID_PANE_XPATH = "//div[contains(@class, 'tab-pane') and contains(@class, 'active')]//monitor-grid";
    private static final String HEADER_XPATH = MONITOR_GRID_PANE_XPATH + "//h3[contains(@class, 'monitors__header')]";
    private static final String MONITOR_KEY_XPATH = "//div[contains(@class, 'monitors__key')]";
    private static final String MONITOR_GRID_XPATH = MONITOR_GRID_PANE_XPATH + "//div[@id='monitorGrid']";

    private static final String ACTIVE_MONITOR_XPATH = "//div[contains(@class, 'tab-pane') and contains(@class, 'active')]//li[contains(@class, 'monitors__menu-row--active')]//div[contains(@class, 'monitors__display-name')]";
    private static final String ACTIVE_CATEGORY_XPATH = ACTIVE_MONITOR_XPATH + "/ancestor::div[contains(@class, 'monitors__menu')]//div[contains(@class, 'monitors__category-name')]";
    private static final String ACTIVE_TEAM_LIST_XPATH = ACTIVE_MONITOR_XPATH + "/following-sibling::div[contains(@class, 'monitors__value-group')]/div[1]";
    private static final String ACTIVE_MY_LIST_XPATH = ACTIVE_MONITOR_XPATH + "/following-sibling::div[contains(@class, 'monitors__value-group')]/div[2]";
    private static final String REFERECE_NUMBER_XPATH = MONITOR_GRID_XPATH + "//table[@role='grid']/tbody//span[text()='%s']";
    private static final String WATCHED_JOBS_ICON_XPATH = REFERECE_NUMBER_XPATH + "/ancestor::tr//td//i[contains(@class,'glyphicon-eye-open')]";

    protected static final String MONITOR_FILTER_ICON_XPATH = "//th[@data-title='%s']//a[contains(@class, 'k-grid-filter')]//span[contains(@class,'k-icon') and contains(@class,'k-filter')]";
    protected static final String MONITOR_FILTER_BUTTON_XPATH = "//div[@class='k-animation-container' and contains(@style,'visible')]//div//button[text()='%s']";

    // Filter
    private static final String FILTER_ICON_XPATH = MONITOR_GRID_XPATH + MONITOR_FILTER_ICON_XPATH;
    private static final String FILTER_TEXTBOX_XPATH = VISIBLE_DROPDOWN_SEARCHBOX_XPATH + "[1]";

    @FindBy(xpath = HEADER_XPATH)
    WebElement header;

    @FindBy(xpath = ACTIVE_MONITOR_XPATH)
    WebElement activeMonitor;

    @FindBy(xpath = ACTIVE_CATEGORY_XPATH)
    WebElement activeMonitorCategory;

    @FindBy(xpath = ACTIVE_MY_LIST_XPATH)
    WebElement activeMonitorMyList;

    @FindBy(xpath = ACTIVE_TEAM_LIST_XPATH)
    WebElement activeMonitorTeamList;

    @FindBy(xpath = FILTER_TEXTBOX_XPATH)
    private WebElement filterTextbox;
    
    
    public MonitorGrid(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(MONITOR_GRID_PANE_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getMonitorName() {
        return header.getText();
    }

    public String getActiveMonitor() {
        return activeMonitor.getText();
    }

    public String getActiveMonitorCategory() {
        return activeMonitorCategory.getText();
    }

    public Integer getActiveMonitorMyListCount() {
        return Integer.valueOf(activeMonitorMyList.getText());
    }

    public Integer getActiveMonitorTeamListCount() {
        return Integer.valueOf(activeMonitorTeamList.getText());
    }

    public Grid getGrid() {
        return GridHelper.getGrid(MONITOR_GRID_XPATH);
    }

    public String getGridXpath() {
        return MONITOR_GRID_XPATH;
    }

    public void enterTextInFilterbox(String value) {
        filterTextbox.clear();
        filterTextbox.sendKeys(value);
        waitForAngularRequestsToFinish();
    }

    public void clickFilterButton() {
        WebElement filterButton = waitForElement(By.xpath(String.format(MONITOR_FILTER_BUTTON_XPATH, "Filter")), State.ELEMENT_IS_CLICKABLE);
        filterButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clearFilter() {
        WebElement clearButton = driver.findElement(By.xpath(String.format(MONITOR_FILTER_BUTTON_XPATH, "Clear")));
        clearButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickFilterIcon(String header) {
        WebElement filterIcon = driver.findElement(By.xpath(String.format(FILTER_ICON_XPATH, header)));
        filterIcon.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isReferenceNumberDisplayed(Integer referenceNumber) {
        return isElementPresent(By.xpath(String.format(REFERECE_NUMBER_XPATH, referenceNumber)));
    }

    public Boolean isWatchedIconDisplayed(Integer referenceNumber) {
        return isElementPresent(By.xpath(String.format(WATCHED_JOBS_ICON_XPATH, referenceNumber)));
    }

    public boolean isAscending(String header) {
        return GridHelper.isAscending(MONITOR_GRID_XPATH, header);
    }

    public void tableSort(String header) {
        GridHelper.tableSort(MONITOR_GRID_XPATH, header);
        waitForAngularRequestsToFinish();
    }
    
    public Boolean isMonitorKeyDisplayed() {
        return isElementPresent(By.xpath(MONITOR_KEY_XPATH));
    }
}
