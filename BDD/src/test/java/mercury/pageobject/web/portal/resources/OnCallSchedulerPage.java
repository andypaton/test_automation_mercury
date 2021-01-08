package mercury.pageobject.web.portal.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.microsoft.applicationinsights.core.dependencies.apachecommons.lang3.StringUtils;

import static mercury.helpers.Constants.MAX_TIMEOUT;

import mercury.helpers.POHelper;
import mercury.helpers.StringHelper;
import mercury.pageobject.web.Base_Page;

public class OnCallSchedulerPage extends Base_Page<OnCallSchedulerPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "On Call Scheduler";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_SCHEDULAR_TABLE_HEADER = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'ocs-table') and contains(@class,'ocs-header-table') and contains(@class,'bold')]";
    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String ON_CALL_SCHEDULER_FILTER_XPATH  = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class, 'ocs-header')]";
    private static final String RESOURCE_PROFILE_DROPDOWN_XPATH = ON_CALL_SCHEDULER_FILTER_XPATH + "//input[@name='selectedResourceProfileId']//ancestor::span[contains(@class,'k-dropdown')]";
    private static final String REGION_DROPDOWN_XPATH = ON_CALL_SCHEDULER_FILTER_XPATH + "//input[@name='selectedRegionId']//ancestor::span[contains(@class,'k-dropdown')]";
    private static final String RESOURCE_PROFILES_XPATH = "//div[@id='selectedResourceProfileId-list']//ul/li";
    private static final String ON_CALL_SCHEDULER_WEEK_SELECTOR_XPATH  = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class, 'ocs-week-selector')]";
    private static final String PREVIOUS_WEEK_SECHDULE_XPATH = ON_CALL_SCHEDULER_WEEK_SELECTOR_XPATH + "//i[contains(@class, 'fa-chevron-left')]";
    private static final String FOLLOWING_WEEK_SECHDULE_XPATH = ON_CALL_SCHEDULER_WEEK_SELECTOR_XPATH + "//i[contains(@class, 'fa-chevron-right')]";
    private static final String CURRENT_WEEK_SECHDULE_XPATH = ON_CALL_SCHEDULER_WEEK_SELECTOR_XPATH + "//button[contains(text(), 'This week')]";

    private static final String CURRENTLY_VIEWING_XPATH = "//h2[text() = 'Currently Viewing']/following-sibling::ph-on-call-scheduler//div[@class = 'ocs-header']";
    private static final String START_DATE_XPATH = "//input[@id = 'week-start-date-datepicker']";

    private static final String TABLE_HEADER_XPATH = "//div[contains(@class, 'ocs-header-table')]";

    private static final String TECH_POSITIONS_XPATH = "//ph-on-call-scheduler-header-row//i[contains(@class, 'fa-chevron')]/..";
    private static final String SITE_ROWS_XPATH = "//ph-on-call-scheduler-header-row//div[contains(.,'%s')]/..//ph-on-call-scheduler-site-row";
    private static final String SITES_XPATH = "//ph-on-call-scheduler-header-row//div[contains(.,'%s')]/..//ph-on-call-scheduler-site-row//div[contains(@class, 'site-cell')]";
    private static final String EXPANDED_TECH_POSITIONS_XPATH = "//ph-on-call-scheduler-header-row//i[contains(@class, 'fa-chevron-down')]";
    private static final String COLLAPSED_TECH_POSITIONS_XPATH = "//ph-on-call-scheduler-header-row//i[contains(@class, 'fa-chevron-right')]";
    private static final String COLLAPSED_TECH_POSITION_XPATH = "//ph-on-call-scheduler-header-row//i[contains(@class, 'fa-chevron-right')]/parent::div[contains(.,'%s')]";
    private static final String STATUS_BARS_XPATH = "//ph-on-call-scheduler-header-row[%d]//div[contains(@class, 'bar') and contains(@class, 'grid-row')]";

    private static final String SCHEDULED_ROTA_FOR_DAY_XPATH = SITE_ROWS_XPATH + "//div[%d]/div[contains(@class, 'rota-scheduled')]";
    private static final String EMPTY_ROTA_FOR_DAY_XPATH = SITE_ROWS_XPATH +"//div[%d]//div[contains(@class, 'empty-rota')]";
    private static final String SCHEDULED_ROTA_FOR_SITE_XPATH = "//ph-on-call-scheduler-header-row//div[contains(.,'%s')]/..//div[contains(@class, 'site-cell') and text()='%s']/..//div[contains(@class, 'rota-scheduled')]";
    private static final String EMPTY_ROTA_FOR_SITE_XPATH = "//ph-on-call-scheduler-header-row//div[contains(.,'%s')]/..//div[contains(@class, 'site-cell') and text()='%s']/..//div[contains(@class, 'empty-rota')]";
    private static final String EMPTY_ROTA_FOR_SITE_DAY_XPATH = "//ph-on-call-scheduler-header-row//div[contains(.,'%s')]/..//div[contains(@class, 'site-cell') and text()='%s']/../div[%d]/div[contains(@class, 'empty-rota')]";
    private static final String ROTA_CELL_XPATH = "//ph-on-call-scheduler-header-row//div[contains(.,'%s')]/..//div[contains(@class, 'site-cell') and text()='%s']/../div[%d]/div[contains(@class, 'rota')]";
    private static final String DELETE_ROTA_CELL_XPATH = ROTA_CELL_XPATH + "/i[contains(@class, 'delete-button')]";

    // Add Resource
    private static final String MODAL_SAVE_XPATH = "//div[contains(@class, 'modal-dialog')]//div[text()='Save']";
    private static final String SELECT_RESOURCE_XPATH = "//div[contains(@class, 'modal-dialog')]//div[contains(@class, 'ocs-resource-select')]";


    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = RESOURCE_PROFILE_DROPDOWN_XPATH)
    private WebElement resource;

    @FindBy(xpath = REGION_DROPDOWN_XPATH)
    private WebElement region;

    @FindBy(xpath = PREVIOUS_WEEK_SECHDULE_XPATH)
    private WebElement previousWeek;

    @FindBy(xpath = CURRENT_WEEK_SECHDULE_XPATH)
    private WebElement currentWeek;

    @FindBy(xpath = FOLLOWING_WEEK_SECHDULE_XPATH)
    private WebElement followingWeek;

    @FindBy(xpath = PAGE_SCHEDULAR_TABLE_HEADER)
    private WebElement ocsTableheader;

    @FindBy(xpath = CURRENTLY_VIEWING_XPATH)
    private WebElement currentlyViewing;

    @FindBy(xpath = START_DATE_XPATH)
    private WebElement startDate;

    @FindBy(xpath = MODAL_SAVE_XPATH)
    private WebElement save;

    @FindBy(xpath = SELECT_RESOURCE_XPATH)
    private WebElement selectResource;


    // Page methods
    public OnCallSchedulerPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
            waitForAngularRequestsToFinish();
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public String getPageHeaderText() {
        return pageHeaderTest.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }

    // Page interactions
    public void previousWeek() {
        previousWeek.click();
        waitForAngularRequestsToFinish();
    }

    public void followingWeek() {
        followingWeek.click();
        waitForAngularRequestsToFinish();
    }

    public void thisWeek() {
        currentWeek.click();
        waitForAngularRequestsToFinish();
    }

    public String getSchedularTableDates() {
        return ocsTableheader.getText();
    }

    public List<String> getResourceProfiles(){
        List<String> options = new ArrayList<>();
        for (WebElement we : driver.findElements(By.xpath(RESOURCE_PROFILES_XPATH))) {
            options.add(we.getAttribute("innerText"));
        }
        return options;
    }

    public String getSelectedResourceProfile(){
        return (resource.getText()).split("\\n")[0];
    }

    public String getSelectedRegion(){
        return (region.getText()).split("\\n")[0];
    }

    public String getCurrentlyViewing() {
        String from = driver.findElement(By.xpath("//input[@id = 'week-start-date-datepicker']")).getAttribute("value");
        String to = driver.findElement(By.xpath("//input[@id = 'week-end-date-datepicker']")).getAttribute("value");

        String result = currentlyViewing.getText();
        result = StringHelper.replaceLast(result, "select", to);
        result = StringHelper.replaceLast(result, "select", from);
        result = result.replaceAll("select\n", "");
        return result;
    }

    public void updateViewingFrom(String date) {
        startDate.clear();
        startDate.sendKeys(date);
        startDate.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public int getNumExpandedTechPositions() {
        return getNumberOfElements(EXPANDED_TECH_POSITIONS_XPATH);
    }

    public List<String> getTechPositions() {
        List<String> result = new ArrayList<>();
        List<WebElement> techPositions = driver.findElements(By.xpath(TECH_POSITIONS_XPATH));
        for (WebElement we : techPositions) {
            result.add(we.getText());
        }
        return result;
    }

    public void expandTechPosition(String techPosition) {
        String xpath = String.format(COLLAPSED_TECH_POSITION_XPATH, techPosition);
        WebElement we = driver.findElement(By.xpath(xpath));
        we.click();
        waitForAngularRequestsToFinish();
        waitWhileBusy();
    }

    public void expandAllTechPositions() {
        List<WebElement> wes = driver.findElements(By.xpath(COLLAPSED_TECH_POSITIONS_XPATH));
        for (WebElement we : wes) {
            we.click();
            waitForAngularRequestsToFinish();
        }
    }

    public List<String> getSitesForTechPosition(String techPosition){
        List<String> result = new ArrayList<>();
        String xpath = String.format(SITES_XPATH, techPosition);
        List<WebElement> sites = driver.findElements(By.xpath(xpath));
        for (WebElement we : sites) {
            result.add(we.getText());
        }
        return result;
    }

    public List<String> getTechPositionsStatus(){
        List<String> result = new ArrayList<>();
        String header = driver.findElement(By.xpath(TABLE_HEADER_XPATH)).getText();

        List<WebElement> techPositions = driver.findElements(By.xpath(TECH_POSITIONS_XPATH));
        for (int i = 0; i < techPositions.size(); i++) {
            String[] parts = header.split("\n");

            String techPosition = techPositions.get(i).getText();
            parts[0] = techPosition;

            List<WebElement> statusBars = driver.findElements(By.xpath(String.format(STATUS_BARS_XPATH, i+1)));
            for (WebElement bar : statusBars) {
                String complete = bar.getAttribute("class").contains("incomplete") ? "INCOMPLETE" : "COMPLETE";

                String style = bar.getAttribute("style");
                int start = Integer.valueOf(StringUtils.substringBetween(style, "grid-column:", "/").trim()) - 1;
                int end = Integer.valueOf(StringUtils.substringBetween(style, "/", ";").trim()) - 1;

                for (int index = start; index < end; index++) {
                    parts[index] = parts[index] + " : " + complete;
                }
            }

            result.add(Arrays.toString(parts).replace("[", "").replace("]", ""));
        }
        return result;
    }

    public int getScheduledRotaCountForDay(String techPosition, int day) {
        String xpath = String.format(SCHEDULED_ROTA_FOR_DAY_XPATH, techPosition, day+1);
        return getNumberOfElements(xpath);
    }

    public int getEmptyRotaCountForDay(String techPosition, int day) {
        String xpath = String.format(EMPTY_ROTA_FOR_DAY_XPATH, techPosition, day+1);
        return getNumberOfElements(xpath);
    }

    public int getScheduledRotaCountForSite(String techPosition, String site) {
        String xpath = String.format(SCHEDULED_ROTA_FOR_SITE_XPATH, techPosition, site);
        return getNumberOfElements(xpath);
    }

    public int getEmptyRotaCountForSite(String techPosition, String site) {
        String xpath = String.format(EMPTY_ROTA_FOR_SITE_XPATH, techPosition, site);
        return getNumberOfElements(xpath);
    }

    public List<WebElement> getScheduledRotasForSite(String techPosition, String site) {
        String xpath = String.format(SCHEDULED_ROTA_FOR_SITE_XPATH, techPosition, site);
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        List<WebElement> wes = driver.findElements(By.xpath(xpath));
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return wes;
    }

    public List<WebElement> getEmptyRotasForSite(String techPosition, String site) {
        String xpath = String.format(EMPTY_ROTA_FOR_SITE_XPATH, techPosition, site);
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        List<WebElement> wes = driver.findElements(By.xpath(xpath));
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return wes;
    }

    private int getNumberOfElements(String xpath) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        List<WebElement> wes = driver.findElements(By.xpath(xpath));
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return wes.size();
    }

    public void selectCell(String techPosition, String site, int day) {
        String xpath = String.format(ROTA_CELL_XPATH, techPosition, site, day + 1);
        WebElement we = driver.findElement(By.xpath(xpath));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void selectDelete(String techPosition, String site, int day) throws InterruptedException {
        String xpath = String.format(ROTA_CELL_XPATH, techPosition, site, day + 1);
        WebElement cell = driver.findElement(By.xpath(xpath));
        Actions action = new Actions(driver);
        action.moveToElement(cell).build().perform(); // got to hover over cell for delete button to become active

        String xpathDeleteButton = String.format(DELETE_ROTA_CELL_XPATH, techPosition, site, day + 1);
        WebElement deleteButton = driver.findElement(By.xpath(xpathDeleteButton));
        deleteButton.click();
        waitForAngularRequestsToFinish();
    }

    public void confirmDelete() throws InterruptedException {
        WebElement we = driver.findElement(By.xpath("//div[contains(@class, 'sweet-alert')]//button[text()='Yes']"));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isEmptyRotaEntry(String techPosition, String site, int day) {
        String xpath = String.format(EMPTY_ROTA_FOR_SITE_DAY_XPATH, techPosition, site, day + 1);
        return getNumberOfElements(xpath) > 0;
    }

    public void waitUntilEmptyRotaEntry(String techPosition, String site, int day) {
        String xpath = String.format(EMPTY_ROTA_FOR_SITE_DAY_XPATH, techPosition, site, day + 1);
        driver.findElement(By.xpath(xpath));
    }

    public void selectAddRandomResource(String type) {
        selectResource.click();
        waitForAngularRequestsToFinish();
        String xpath = String.format("(//div[contains(@class, 'section-results')]//*[contains(text(), '%s')])[1]", type);
        WebElement we = driver.findElement(By.xpath(xpath));
        we.click();
        waitForAngularRequestsToFinish();
    }

    public void saveNewResource() {
        save.click();
        waitForAngularRequestsToFinish();
    }

}
