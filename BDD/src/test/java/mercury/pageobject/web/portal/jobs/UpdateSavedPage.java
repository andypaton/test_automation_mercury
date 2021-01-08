package mercury.pageobject.web.portal.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class UpdateSavedPage extends Base_Page<UpdateSavedPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Update Saved";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";


    private static final String JOB_LOG_CSS = "div .job-log" ;
    private static final String JOB_LOG_CONTENTS_CSS = JOB_LOG_CSS + " > div > div" ;

    private static final String JOB_UPDATE_SAVED_MESSAGE_CSS = JOB_LOG_CONTENTS_CSS + " > h3:nth-child(1)";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";

    private static final String MESSAGE_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h3[contains(text(),'%s')]";

    private static final String VIEW_JOB_DETAILS_LINK_XPATH = PAGE_BODY_CONTAINER_XPATH + "//a[text()='View job details']";
    private static final String VIEW_OPEN_JOBS_LINK_XPATH = PAGE_BODY_CONTAINER_XPATH + "//a[text()='View open jobs']";
    private static final String VIEW_ALL_JOBS_LINK_XPATH = PAGE_BODY_CONTAINER_XPATH + "//a[text()='View all jobs']";

    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = VIEW_JOB_DETAILS_LINK_XPATH)
    private WebElement viewJobDetails;

    @FindBy(xpath = VIEW_OPEN_JOBS_LINK_XPATH)
    private WebElement viewOpenJobs;

    @FindBy(xpath = VIEW_ALL_JOBS_LINK_XPATH)
    private WebElement viewAllJobs;

    @FindBy(css =  JOB_UPDATE_SAVED_MESSAGE_CSS)
    private WebElement savedJobMessage;

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(VIEW_OPEN_JOBS_LINK_XPATH));
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

    public UpdateSavedPage(WebDriver driver) {
        super(driver);
    }

    public boolean viewJobDetailsIsDisplayed() {
        return viewJobDetails.isDisplayed();
    }

    public boolean viewOpenJobsIsDisplayed() {
        return viewOpenJobs.isDisplayed();
    }

    public boolean viewAllJobsUsDisplayed() {
        return viewAllJobs.isDisplayed();
    }

    public String getJobSavedMessage() {
        return savedJobMessage.getText();
    }

    public String getJobReference(String expectedMessage) {
        WebElement message = driver.findElement(By.xpath(String.format(MESSAGE_XPATH, expectedMessage)));
        return message.getText().split(":")[1].trim();
    }

}
