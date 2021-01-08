package mercury.pageobject.web.storeportal;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class StorePortalHomePage extends Base_Page<StorePortalHomePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Mercury Store Portal";

    private static final String PAGE_HEADER_XPATH = "//div[@class='header']";
    private static final String PAGE_BODY_XPATH = "//div[@class='main']";

    private static final String ALL_MENU_ITEMS_XPATH = PAGE_HEADER_XPATH + "//div[@class='navigation-items']";
    private static final String MENU_ITEM_NAMES_XPATH = ALL_MENU_ITEMS_XPATH + "//a[contains(@class, 'item')]";
    private static final String MENU_ITEM_XPATH = ALL_MENU_ITEMS_XPATH + "//a[contains(text(), '%s')]";
    private static final String LOGOUT_XPATH = PAGE_HEADER_XPATH + "//a[contains(text(), 'Logout')]";

    private static final String ALL_TILES_XPATH = PAGE_BODY_XPATH + "//div[contains(@class, 'tiles-container')]";
    private static final String TILE_NAMES_XPATH = ALL_TILES_XPATH + "//div[@class='title']";
    private static final String TILE_COUNTS_XPATH = ALL_TILES_XPATH + "//div[@class='number']";
    private static final String TILE_COUNT_FOR_NAME_XPATH = ALL_TILES_XPATH + "//div[contains(text(), '%s')]/preceding-sibling::div";
    private static final String VIEW_TILE_FOR_NAME_XPATH = ALL_TILES_XPATH + "//div[contains(text(), '%s')]/following-sibling::div";

    private static final String SEARCH_FOR_JOB_XPATH = PAGE_BODY_XPATH + "//input[contains(@placeholder, 'Search')]";
    private static final String FIRST_AVAILABLE_JOB_XPATH = PAGE_BODY_XPATH + "//div[contains(@class, 'ag-body-viewport')]//div[@class='ag-center-cols-clipper']//div[@role='row'][1]";
    private static final String FIRST_AVAILABLE_JOB_JOBTYPE_XPATH = FIRST_AVAILABLE_JOB_XPATH + "//div[3]";
    private static final String FIRST_NOT_WATCHED_JOB_XPATH = "(//span[normalize-space(text())='Watch']/ancestor::div[@role='row'])[1]";

    private static final String GRID_ROWS_XPATH = PAGE_BODY_XPATH + "//div[@ref='gridPanel']//div[@class='ag-center-cols-container']//div[@role='row']";
    private static final String JOB_ID_XPATH = GRID_ROWS_XPATH + "//div[@col-id='jobId']";
    private static final String LOGGED_DATE_XPATH = GRID_ROWS_XPATH + "//div[@col-id='loggedDate']";
    private static final String JOB_TYPE_XPATH = GRID_ROWS_XPATH + "//div[@col-id='jobType']";
    private static final String STATUS_XPATH = GRID_ROWS_XPATH + "//div[@col-id='clientStatus']";
    private static final String ASSET_TYPE_XPATH = GRID_ROWS_XPATH + "//div[@col-id='asset']";
    private static final String ON_SITE_RESOURCE_XPATH = GRID_ROWS_XPATH + "//div[@col-id='onSiteResource']";
    private static final String PRIORITY_XPATH = GRID_ROWS_XPATH + "//div[@col-id='priority']";
    private static final String CLOSED_DATE_XPATH = GRID_ROWS_XPATH + "//div[@col-id='closedDate']";
    private static final String ETA_XPATH = GRID_ROWS_XPATH + "//div[@col-id='etaDisplayModel']";
    private static final String RAISED_BY_XPATH = GRID_ROWS_XPATH + "//div[@col-id='raisedBy']";
    private static final String FEEDBACK_RATING_XPATH = GRID_ROWS_XPATH + "//div[@col-id='feedbackRating']";
    private static final String COMMENTS_XPATH = GRID_ROWS_XPATH + "//div[@col-id='comment']";


    @FindBy(xpath = TILE_NAMES_XPATH)
    private List<WebElement> tileNameOptions;

    @FindBy(xpath = TILE_COUNTS_XPATH)
    private List<WebElement> tileCountOptions;

    @FindBy(xpath = MENU_ITEM_NAMES_XPATH)
    private List<WebElement> menuNameOptions;

    @FindBy(xpath = SEARCH_FOR_JOB_XPATH)
    private WebElement searchForJob;

    @FindBy(xpath = FIRST_AVAILABLE_JOB_XPATH)
    private WebElement firstAvailableJob;

    @FindBy(xpath = FIRST_AVAILABLE_JOB_JOBTYPE_XPATH)
    private WebElement firstAvailableJobType;

    @FindBy(xpath = FIRST_NOT_WATCHED_JOB_XPATH)
    private WebElement firstNotWatchedJob;

    @FindBy(xpath = GRID_ROWS_XPATH)
    private List<WebElement> gridRowsFound;

    @FindBy(xpath = JOB_ID_XPATH)
    private WebElement jobId;

    @FindBy(xpath = LOGGED_DATE_XPATH)
    private WebElement loggedDate;

    @FindBy(xpath = JOB_TYPE_XPATH)
    private WebElement jobType;

    @FindBy(xpath = STATUS_XPATH)
    private WebElement status;

    @FindBy(xpath = ASSET_TYPE_XPATH)
    private WebElement assetType;

    @FindBy(xpath = ON_SITE_RESOURCE_XPATH)
    private WebElement onSiteResource;

    @FindBy(xpath = PRIORITY_XPATH)
    private WebElement priority;

    @FindBy(xpath = CLOSED_DATE_XPATH)
    private WebElement closedDate;

    @FindBy(xpath = ETA_XPATH)
    private WebElement eta;

    @FindBy(xpath = RAISED_BY_XPATH)
    private WebElement raisedBy;

    @FindBy(xpath = FEEDBACK_RATING_XPATH)
    private WebElement feedbackRating;

    @FindBy(xpath = COMMENTS_XPATH)
    private WebElement comments;


    public StorePortalHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.waitForStorePortal();
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_BODY_XPATH));
            logger.info(PAGE_TITLE + ": Page loaded");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + ": Page failed to load");
            throw new AssertionError();
        }
    }

    public List<String> getTileNames() {
        List<String> tileNames = new ArrayList<>();
        for (WebElement tileName : tileNameOptions) {
            tileNames.add(tileName.getText());
        }
        return tileNames;
    }

    public List<String> getTileCounts() {
        List<String> tileCounts = new ArrayList<>();
        for (WebElement tileCount : tileCountOptions) {
            tileCounts.add(tileCount.getText());
        }
        return tileCounts;
    }

    public List<String> getMenuNames() {
        List<String> menuNames = new ArrayList<>();
        for (WebElement menuName : menuNameOptions) {
            menuNames.add(menuName.getAttribute("innerText"));
        }
        return menuNames;
    }

    public void selectMenuItem(String menuItemName) {
        WebElement menuItem = driver.findElement(By.xpath(String.format(MENU_ITEM_XPATH, menuItemName)));
        menuItem.click();
        POHelper.waitForStorePortal();
    }

    public String getCountForTile(String tileName) {
        POHelper.waitWhileBusy();
        WebElement count = driver.findElement(By.xpath(String.format(TILE_COUNT_FOR_NAME_XPATH, tileName)));
        return count.getText();
    }

    public void viewJobsForTile(String tileName) {
        WebElement view = driver.findElement(By.xpath(String.format(VIEW_TILE_FOR_NAME_XPATH, tileName)));
        POHelper.scrollToElement(view);
        POHelper.clickJavascript(view);
        POHelper.waitForStorePortal();
    }

    public void searchForJob(String searchQuery) {
        searchForJob.sendKeys(searchQuery);
    }

    public StorePortalJobDetailsPage selectFirstAvailableJob() {
        firstAvailableJob.click();
        POHelper.waitForStorePortal();
        return PageFactory.initElements(driver, StorePortalJobDetailsPage.class).get();
    }

    public StorePortalJobDetailsPage selectFirstNotWatchedJob() {
        firstNotWatchedJob.click();
        POHelper.waitForStorePortal();
        return PageFactory.initElements(driver, StorePortalJobDetailsPage.class).get();
    }

    public String getFirstAvailableJobType() {
        return firstAvailableJobType.getText();
    }

    public List<String> getGridRows() {
        List<String> gridRows = new ArrayList<>();
        for (WebElement gridRow : gridRowsFound) {
            gridRows.add(gridRow.getText());
        }
        return gridRows;
    }

    public String getJobId() {
        return jobId.getText();
    }

    public String getLoggedDate() {
        return loggedDate.getText();
    }

    public String getJobType() {
        return jobType.getText();
    }

    public String getStatus() {
        return status.getText();
    }

    public String getAssetType() {
        return assetType.getText();
    }

    public String getOnSiteResource() {
        return onSiteResource.getText();
    }

    public String getPriority() {
        return priority.getText();
    }

    public String getClosedDate() {
        return closedDate.getText();
    }

    public String getEta() {
        return eta.getText();
    }

    public String getRaisedBy() {
        return raisedBy.getText();
    }

    public String getFeedbackRating() {
        return feedbackRating.getText();
    }

    public String getComments() {
        return comments.getText();
    }

    public void logout() {
        WebElement logout = driver.findElement(By.xpath(LOGOUT_XPATH));
        POHelper.clickJavascript(logout);
        POHelper.waitForStorePortal();
    }
}
