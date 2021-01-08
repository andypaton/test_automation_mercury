package mercury.pageobject.web.helpdesk;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

import static mercury.helpers.POHelper.scrollTo;

public class HelpdeskHomePage extends Base_Page<HelpdeskHomePage>  {

    private static final Logger logger = LogManager.getLogger();

    public static final String JOBS = "Jobs";
    public static final String INCIDENTS = "Incidents";
    public static final String ADMIN = "Admin";
    public static final String BUREAU = "Bureau";
    public static final String HEALTH_AND_SAFETY = "Health & Safety";
    public static final String ONE_STOP_SHOP = "One Stop Shop";
    public static final String FOCUS = "Focus";
    public static final String LANDLORD_JOBS = "Landlord Jobs";

    // Page title
    private static final String PAGE_TITLE = "Helpdesk Home Page";

    //Home Page Tiles
    private static final String HOME_PAGE_XPATH = "//div[contains(@class, 'active')]//home-tab/div";
    private static final String TABS_XPATH = "//ul[contains(@class, 'nav-tabs')]//span[contains(@class, 'title')]/ancestor::li";
    protected static final String TAB_XPATH = "//ul[contains(@class, 'nav-tabs')]//span[contains(@class, 'title') and starts-with(text(), '%s')]/ancestor::li";

    private static final String TILES_XPATH = HOME_PAGE_XPATH + "//p[contains(@class,'monitors__tile--title')]";
    private static final String MONITOR_XPATH = HOME_PAGE_XPATH + "//p[contains(@class,'monitors__tile--title') and text() = '%s']";

    private static final String LAST_VIEWED_PANEL_XPATH = "//div[contains(@class, 'last-viewed-panel')]";
    private static final String LAST_VIEWED_JOBS_XPATH = LAST_VIEWED_PANEL_XPATH + "//div[contains(@class, 'last-viewed-menu-tabs')]//li[text() = 'JOBS']";
    private static final String LAST_VIEWED_INCIDENTS_XPATH = LAST_VIEWED_PANEL_XPATH + "//div[contains(@class, 'last-viewed-menu-tabs')]//li[text() = 'INCIDENTS']";
    private static final String LAST_VIEWED_WHEN_XPATH = LAST_VIEWED_PANEL_XPATH + "//div[contains(@class, 'col-xs-11')]";
    private static final String LAST_VIEWED_ITEMS_XPATH = LAST_VIEWED_PANEL_XPATH + "//div[contains(@class, 'col-xs-4')]";
    private static final String LAST_VIEWED_SITES_XPATH = LAST_VIEWED_PANEL_XPATH + "//div[contains(@class, 'col-xs-6')]";
    private static final String LAST_VIEWED_ITEM_XPATH = LAST_VIEWED_PANEL_XPATH + "//div[contains(@class, 'col-xs-4') and text()='%d']";

    //Suffix
    private static final String TO_DO_XPATH = "/following-sibling::div//span[text()='To Do']";
    private static final String FOR_INFO_XPATH = "/following-sibling::div//span[text()='For Info']";

    private static final String MY_TEAM_XPATH = "/following-sibling::span[1]";
    private static final String NATIONAL_XPATH = "/following-sibling::span[2]";

    private static final String CLOSE_ACTIVE_TAB_CSS = ".active .nav__close-button";
    private static final String CONFIRM_ALERT_CSS = ".sweet-alert .confirm";
    private static final String CANCEL_ALERT_CSS = ".sweet-alert .cancel";


    @FindBy(xpath = TABS_XPATH)
    List<WebElement> tabs;

    @FindBy(xpath = TILES_XPATH)
    List<WebElement> tiles;

    // Tabs
    @FindBy(css = CLOSE_ACTIVE_TAB_CSS)
    WebElement closeSearchTab;

    @FindBy(css = CONFIRM_ALERT_CSS)
    WebElement confirmAlert;

    @FindBy(css = CANCEL_ALERT_CSS)
    WebElement cancelAlert;

    @FindBy(xpath = LAST_VIEWED_JOBS_XPATH)
    WebElement lastViewedJobs;

    @FindBy(xpath = LAST_VIEWED_INCIDENTS_XPATH)
    WebElement lastViewedIncidents;

    @FindBy(xpath = LAST_VIEWED_WHEN_XPATH)
    List<WebElement> lastViewedWhen;

    @FindBy(xpath = LAST_VIEWED_ITEMS_XPATH)
    List<WebElement> lastViewedItems;

    @FindBy(xpath = LAST_VIEWED_SITES_XPATH)
    List<WebElement> lastViewedSites;


    public HelpdeskHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            //            waitUntilTileIsClickable(1);
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(HOME_PAGE_XPATH));
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public void selectJobs(){
        selectTile(JOBS);
    }

    public void selectIncidents(){
        selectTile(INCIDENTS);
    }

    public void selectAdmin(){
        selectTile(ADMIN);
    }

    public void selectBureau(){
        selectTile(BUREAU);
    }

    public void selectOneStopShop(){
        selectTile(ONE_STOP_SHOP);
    }

    public void selectFocus(){
        selectTile(FOCUS);
    }

    public void selectHealthAndSafety() {
        selectTile(HEALTH_AND_SAFETY);
    }

    public void selectLandlordJobs() {
        selectTile(LANDLORD_JOBS);
    }

    public int getNumberOfMonitorTiles() {
        return tiles.size();
    }

    public void waitUntilTileIsClickable(int i) {
        waitForElement(By.xpath("(" +TILES_XPATH + ")[" + i + "]"), State.ELEMENT_IS_CLICKABLE);
    }

    public void waitUntilAllTilesClickable() {
        for  (int i = 1; i <= tiles.size(); i++) {
            waitForElement(By.xpath("(" +TILES_XPATH + ")[" + i + "]"), State.ELEMENT_IS_CLICKABLE);
        }
    }

    public boolean isTileDisplayed(String monitor) {
        return isElementPresent(By.xpath(String.format(MONITOR_XPATH, monitor)));
    }

    public void closeActiveTab() {
        if (isElementPresent(By.cssSelector(CLOSE_ACTIVE_TAB_CSS))) {
            closeSearchTab.click();
        }
        if (isElementClickable(By.cssSelector(CONFIRM_ALERT_CSS))) {
            confirmAlert.click();
        }
        if (isElementClickable(By.cssSelector(CANCEL_ALERT_CSS))) {
            cancelAlert.click();
        }
        waitForAngularRequestsToFinish();
    }

    public void closeAllTabs() {
        while (isElementPresent(By.cssSelector(CLOSE_ACTIVE_TAB_CSS))) {
            closeActiveTab();
        }
    }

    public int getMyTeamToDoCount(String tile) {
        String xpath = String.format(MONITOR_XPATH, tile) + TO_DO_XPATH + MY_TEAM_XPATH;
        WebElement we = driver.findElement(By.xpath(xpath));
        return Integer.valueOf(we.getText());
    }

    public int getNationalToDoCount(String tile) {
        String xpath = String.format(MONITOR_XPATH, tile) + TO_DO_XPATH + NATIONAL_XPATH;
        WebElement we = driver.findElement(By.xpath(xpath));
        return Integer.valueOf(we.getText());
    }

    public int getMyTeamForInfoCount(String tile) {
        String xpath = String.format(MONITOR_XPATH, tile) + FOR_INFO_XPATH + MY_TEAM_XPATH;
        WebElement we = driver.findElement(By.xpath(xpath));
        return Integer.valueOf(we.getText());
    }

    public int getNationalForInfoCount(String tile) {
        String xpath = String.format(MONITOR_XPATH, tile) + FOR_INFO_XPATH + NATIONAL_XPATH;
        WebElement we = driver.findElement(By.xpath(xpath));
        return Integer.valueOf(we.getText());
    }

    public void selectHomeTab() {
        selectTab("Home");
    }

    public List<String> getTabs(){
        List<String> openTabs = new ArrayList<>();
        for (WebElement we : tabs) {
            openTabs.add(we.getText());
        }
        return openTabs;
    }

    public void selectTab(String tab){
        scrollTo(0);
        String xpath = String.format(TAB_XPATH, tab);
        waitForElement(By.xpath(xpath), State.ELEMENT_IS_CLICKABLE);
        driver.findElement(By.xpath(xpath)).click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getTiles(){
        List<String> result = new ArrayList<>();
        for (WebElement we : tiles) {
            result.add(we.getText());
        }
        return result;
    }

    public void selectTile(String tile){
        String xpath = String.format(MONITOR_XPATH, tile);
        driver.findElement(By.xpath(xpath)).click();
        waitForAngularRequestsToFinish();
    }

    public boolean isTabActive(String tab) {
        String xpath = String.format(TAB_XPATH, tab);
        WebElement we = waitForElement(By.xpath(xpath), ELEMENT_IS_VISIBLE);
        return we.getAttribute("class").equals("active");
    }

    public void selectLastViewedJobs(){
        lastViewedJobs.click();
        waitForAngularRequestsToFinish();
    }

    public void selectLastViewedIncidents(){
        lastViewedIncidents.click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getLastViewedWhen(){
        List<String> result = new ArrayList<>();
        for (WebElement we : lastViewedWhen) {
            result.add(we.getText());
        }
        return result;
    }

    public List<Integer> getLastViewedJobs(){
        selectLastViewedJobs();
        List<Integer> result = new ArrayList<>();
        for (WebElement we : lastViewedItems) {
            result.add(Integer.valueOf(we.getText()));
        }
        return result;
    }

    public List<Integer> getLastViewedIncidents(){
        selectLastViewedIncidents();
        List<Integer> result = new ArrayList<>();
        for (WebElement we : lastViewedItems) {
            result.add(Integer.valueOf(we.getText()));
        }
        return result;
    }

    public List<String> getLastViewedSites(){
        List<String> result = new ArrayList<>();
        for (WebElement we : lastViewedSites) {
            result.add(we.getText());
        }
        return result;
    }

    public void selectLastViewedItem(int item) {
        WebElement we = driver.findElement(By.xpath(String.format(LAST_VIEWED_ITEM_XPATH, item)));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void waitForCurrentUserName(String username) {
        String xpath = String.format("//script[contains(text(), \"currentUsername = '%s'\")]", username);
        driver.findElement(By.xpath(xpath));
    }

}
