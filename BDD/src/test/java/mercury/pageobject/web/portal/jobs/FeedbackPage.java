package mercury.pageobject.web.portal.jobs;

import static mercury.helpers.POHelper.scrollTo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.microsoft.applicationinsights.web.dependencies.apachecommons.lang3.StringUtils;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class FeedbackPage extends Base_Page<FeedbackPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Feedback";

    // Page elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE + "')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@class='main-content']";

    // Search Box
    private static final String SEARCH_BOX_XPATH = PAGE_MAIN_CONTENT_XPATH + "//label[contains(text(), 'Search')]/..//input";

    // Button
    private static final String SPLIT_RFMS_BUTTON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//button[contains(text(), 'Split RFMs')]";

    // Grid
    private static final String FEEDBACK_GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//h3[contains(text(), '%s')]/ancestor::fb-feedback-list//div[contains(@class, 'selectable-grid') and contains(@class, 'k-grid k-widget')]";
    private static final String FEEDBACK_GRID_DATA_XPATH = FEEDBACK_GRID_XPATH + "//td/span[contains(text(), '%s')]";
    private static final String COMBINED_FEEDBACK_GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//h3[contains(text(), 'Combined Feedback')]/ancestor::fb-feedback-list//div[contains(@class, 'selectable-grid') and contains(@class, 'k-grid k-widget')]";

    private static final String LABEL_XPATH = COMBINED_FEEDBACK_GRID_XPATH + "//span[contains(@class, 'k-pager-info') and contains(@class, 'k-label')]";

    // Web Elements
    @FindBy(xpath = SEARCH_BOX_XPATH) private WebElement searchBox;

    @FindBy(xpath = SPLIT_RFMS_BUTTON_XPATH) private WebElement splitRFMsButton;

    static int failedCount = 0;

    // Methods
    public FeedbackPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
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

    public boolean isSearchBoxDisplayed() {
        return isElementVisible(By.xpath(SEARCH_BOX_XPATH));
    }

    public boolean isSplitRFMsButtonDisplayed() {
        return isElementVisible(By.xpath(SPLIT_RFMS_BUTTON_XPATH));
    }

    public FeedbackPage searchJobAwaitingFeedback(String searchQuery) {
        this.searchBox.clear();
        POHelper.sendKeys(searchBox, searchQuery);
        POHelper.waitForKendoLoadingToComplete();
        return this;
    }

    public Grid getGrid(String gridName) {
        return GridHelper.getGrid(String.format(FEEDBACK_GRID_XPATH, gridName));
    }

    public Grid getGridFirstRow(String gridName) {
        return GridHelper.getGrid(String.format(FEEDBACK_GRID_XPATH, gridName), 1);
    }

    public Grid getCombinedFeedbackGrid() {
        return GridHelper.getGrid(COMBINED_FEEDBACK_GRID_XPATH);
    }

    public void openJobAwaitingFeedback(String gridName, String jobReference) throws InterruptedException {
        String xpath = String.format(FEEDBACK_GRID_DATA_XPATH, gridName, jobReference);
        for (int attempts = 0; attempts < 10; attempts++) {
            try {
                driver.findElement(By.xpath(xpath)).click();
                waitForAngularRequestsToFinish();
                break;
            } catch (StaleElementReferenceException e) {
                // ignore
            }
        }
    }

    public Integer getNumberOfItemsInTheFeedbackGrid(String gridName) {
        return GridHelper.getNumberOfDisplayedRows(String.format(FEEDBACK_GRID_XPATH, gridName));
    }

    public void clickSplitRFMsButton() {
        splitRFMsButton.click();
        waitForAngularRequestsToFinish();
    }

    public FeedbackPage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        GridHelper.tableSort(COMBINED_FEEDBACK_GRID_XPATH, header, isAscending);
        return this;
    }

    public boolean isAscending(String column) {
        return GridHelper.isAscending(COMBINED_FEEDBACK_GRID_XPATH, column);
    }

    public int getCombinedFeedbackCount() {
        String label = driver.findElement(By.xpath(LABEL_XPATH)).getText();
        return Integer.valueOf(StringUtils.substringBetween(label, "of", "items").trim());
    }

}
