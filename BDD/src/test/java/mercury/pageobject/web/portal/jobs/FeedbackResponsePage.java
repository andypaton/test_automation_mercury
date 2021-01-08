package mercury.pageobject.web.portal.jobs;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;

public class FeedbackResponsePage extends Base_Page<FeedbackResponsePage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Feedback Response";

    // Page elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE + "')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@class='main-content']";

    // Job Details
    private static final String JOB_DETAILS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'job-detail')]";
    private static final String JOB_DETAILS_FIELDS_XPATH = JOB_DETAILS_XPATH + "//dt[contains(text(), '%s')]/following-sibling::dd";

    // Feedback Details
    private static final String FEEDBACK_DETAILS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'feedback-details')]";
    private static final String FEEDBACK_DETAILS_FIELDS_XPATH = FEEDBACK_DETAILS_XPATH + "//dt[contains(text(), '%s')]/following-sibling::dd";
    private static final String VIEW_FULL_DETAILS_BUTTON_XPATH = FEEDBACK_DETAILS_XPATH + "//button//span[contains(text(), 'View full details')]";
    private static final String FEEDBACK_FACE_XPATH = FEEDBACK_DETAILS_XPATH + "//div[contains(@class, 'feedback-face')]//p";
    private static final String FEEDBACK_IMAGE_XPATH = FEEDBACK_DETAILS_XPATH + "//div[contains(@class, 'feedback-face')]//img";
    private static final String BUTTON_XPATH = FEEDBACK_DETAILS_XPATH + "//button[contains(text(), '%s')]";
    private static final String TICK_SYMBOL_ON_BUTTON_XPATH = BUTTON_XPATH + "//i[contains(@class, 'tick')]";

    // Grid
    private static final String JOB_TIMELINE_GRID_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class, 'selectable-grid') and contains(@class, 'k-grid k-widget')]";

    // Back Button
    private static final String BACK_BUTTON_XPATH = PAGE_MAIN_CONTENT_XPATH + "//button[contains(text(), 'Back')]";

    // Web Elements
    @FindBy(xpath = VIEW_FULL_DETAILS_BUTTON_XPATH)
    private WebElement viewFullDetails;

    @FindBy(xpath = FEEDBACK_FACE_XPATH)
    private WebElement feedbackFace;

    @FindBy(xpath = FEEDBACK_IMAGE_XPATH)
    private WebElement feedbackImage;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement back;

    @FindBy(xpath = JOB_TIMELINE_GRID_XPATH)
    private WebElement jobTimeline;

    // Methods
    public FeedbackResponsePage(WebDriver driver) {
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

    public String getJobDetailsFieldData(String field) {
        WebElement jobDetailsField = driver.findElement(By.xpath(String.format(JOB_DETAILS_FIELDS_XPATH, field)));
        return jobDetailsField.getText();
    }

    public String getFeedbackDetailsFieldData(String field) {
        WebElement feedbackDetailsField = driver.findElement(By.xpath(String.format(FEEDBACK_DETAILS_FIELDS_XPATH, field)));
        return feedbackDetailsField.getText();
    }

    public FeedbackDetailsModal clickViewFullDetails() {
        viewFullDetails.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, FeedbackDetailsModal.class).get();
    }

    public String getFeedbackFace() {
        return feedbackFace.getText();
    }

    public String getFeedbackImageSource() {
        return feedbackImage.getAttribute("src");
    }

    public Grid getJobTimeLineGrid() throws Throwable {
        Grid grid = GridHelper.getGrid(JOB_TIMELINE_GRID_XPATH);

        // Job Timeline doesn't always display, so refresh page until it does
        for (int i = 0; i < 10; i++) {
            if (grid.getRows().size() > 0) break;
            POHelper.refreshPage();
            grid = GridHelper.getGrid(JOB_TIMELINE_GRID_XPATH);
        }
        return grid;
    }

    public Grid getJobTimeLineGridFirstRow() throws Throwable {
        Grid grid = GridHelper.getGrid(JOB_TIMELINE_GRID_XPATH, 1);

        // Job Timeline doesn't always display, so refresh page until it does
        for (int i = 0; i < 10; i++) {
            if (grid.getRows().size() > 0) break;
            POHelper.refreshPage();
            grid = GridHelper.getGrid(JOB_TIMELINE_GRID_XPATH, 1);
        }
        return grid;    }

    public ReplyToStoreModal clickReplyToStoreButton(String button) {
        WebElement replyToStore = driver.findElement(By.xpath(String.format(BUTTON_XPATH, button)));
        replyToStore.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, ReplyToStoreModal.class).get();
    }

    public SpokeToStoreModal clickSpokeToStoreButton(String button) {
        WebElement spokeToStore = driver.findElement(By.xpath(String.format(BUTTON_XPATH, button)));
        spokeToStore.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, SpokeToStoreModal.class).get();
    }

    public Boolean isButtonEnabled(String button) {
        return isElementClickable(By.xpath(String.format(BUTTON_XPATH, button)));
    }

    public Boolean isTickSymbolDisplayedOnButton(String button) {
        return isElementVisible(By.xpath(String.format(TICK_SYMBOL_ON_BUTTON_XPATH, button)));
    }

    public FeedbackPage clickBackButton() {
        back.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, FeedbackPage.class).get();
    }

    public boolean isJobTimelineDisplayed() {
        return isElementVisible(By.xpath(JOB_TIMELINE_GRID_XPATH));
    }
}
