package mercury.pageobject.web.portal.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class PortalJobsForSitePage extends Base_Page<PortalJobsForSitePage>{

    private static final Logger logger = LogManager.getLogger();
    private static final String PAGE_TITLE = "Jobs for Site:";

    // Page elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//*[contains(@id, 'open-jobs-supplier-technician')]";

    //Buttons
    private static final String START_WORK_BUTTON_XPATH = "//div[@id='open-jobs']//table[@id='open-jobs-supplier-technician']//tbody/tr/td[contains(text(),'%s')]/../td/button[contains(text(),'Start Work')]";
    private static final String STOP_WORK_BUTTON_XPATH = "//div[@id='open-jobs']//table[@id='open-jobs-supplier-technician']//tbody/tr/td[contains(text(),'%s')]/../td/button[contains(text(),'Stop Work')]";

    private static final String JOB_REFERENCE_XPATH = "//div[@id='open-jobs']//table[@id='open-jobs-supplier-technician']//tbody/tr/td[contains(text(),'%s')]";

    private static final String SEARCH_BOX_XPATH = "(//input[@type = 'search'])";

    @FindBy(xpath = PAGE_HEADER_XPATH)
    private WebElement pageHeader;

    @FindBy(xpath = GRID_XPATH)
    private WebElement openJobsGrid;

    @FindBy(xpath = SEARCH_BOX_XPATH)
    private WebElement searchBox;

    public PortalJobsForSitePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_HEADER_XPATH + " isloaded");
        try {

            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
            logger.info(PAGE_HEADER_XPATH + " isloaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_HEADER_XPATH + " isloaded error");
            throw new AssertionError();
        }
    }

    public Boolean isPageTitleDisplayed() {
        return pageHeader.isDisplayed();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Grid getGrid(int i) {
        return GridHelper.getGrid(GRID_XPATH + String.format("[%d]", i));
    }

    public Boolean isStartWorkButtonDisplayed(String jobReference) {
        return this.isElementVisible(By.xpath(String.format(START_WORK_BUTTON_XPATH, jobReference)));
    }

    public void clickStartWorkButton(String jobReference) {
        WebElement startWorkButton = driver.findElement(By.xpath(String.format(START_WORK_BUTTON_XPATH, jobReference)));
        startWorkButton.click();
    }

    public Boolean isStopWorkButtonDisplayed(String jobReference) {
        return this.isElementVisible(By.xpath(String.format(STOP_WORK_BUTTON_XPATH, jobReference)));
    }

    public Boolean isJobReferenceDisplayed(String jobReference) {
        return this.isElementVisible(By.xpath(String.format(JOB_REFERENCE_XPATH, jobReference)));
    }

    public void clickStopWorkButton(String jobReference) {
        WebElement stopWorkButton = driver.findElement(By.xpath(String.format(STOP_WORK_BUTTON_XPATH, jobReference)));
        stopWorkButton.click();
        waitForAngularRequestsToFinish();
    }

    public PortalJobsForSitePage searchJobs(String searchQuery) {
        return searchJobs(searchQuery, 1);
    }

    public PortalJobsForSitePage searchJobs(String searchQuery, int i) {
        WebElement we = driver.findElement(By.xpath(SEARCH_BOX_XPATH + String.format("[%d]", i)));
        we.clear();

        POHelper.sendKeys(we, searchQuery);
        we.sendKeys(Keys.TAB);

        waitForAngularRequestsToFinish();
        POHelper.waitForKendoLoadingToComplete();
        POHelper.waitWhileBusy();
        return this;
    }

    public int getNumberOfSearchBoxes() {
        return driver.findElements(By.xpath(SEARCH_BOX_XPATH)).size();
    }

    public boolean isJobReferenceOnGrid(int jobReference) {
        String xpath = String.format("//table//tr/td[contains(., '%d')]", jobReference);
        return driver.findElements(By.xpath(xpath)).size() > 0;
    }

    public boolean isAnyJobsForSite() {
        return !isElementPresent(By.xpath("//*[contains(text(), 'There are no jobs for this Site')]"));
    }

}
