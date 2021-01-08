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

public class JobInfoJobDetailsPage extends Base_Page<JobInfoJobDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Job Info";
    // Page elements
    private static final String JOB_INFO_XPATH = ".//div[contains(@class,'job-detail__job-info')]";
    private static final String LOGGED_DATE_XPATH_SUFFIX = "/descendant::label[contains(text(),'Logged Date')]/following-sibling::div";
    private static final String JOB_DESCRIPTION_XPATH_SUFFIX = "/descendant::label[contains(text(),'Job description')]/following-sibling::div";
    private static final String JOB_TYPE_XPATH_SUFFIX = "/descendant::label[contains(text(),'Job Type')]/following-sibling::div";
    private static final String LOCATION_WITHIN_SITE_XPATH_SUFFIX = "/descendant::label[contains(text(),'Location within site')]/following-sibling::div";
    private static final String SUBTYPE_CLASSIFICATION_XPATH_SUFFIX = "/descendant::label[contains(text(),'Subtype')]/following-sibling::div";
    private static final String RESPONSE_PRIORITY_XPATH_SUFFIX = "/descendant::label[contains(text(),'Response Priority')]/following-sibling::div";
    private static final String REPAIR_PRIORITY_XPATH_SUFFIX = "/descendant::label[contains(text(),'Repair Priority')]/following-sibling::div";
    private static final String FAULT_TYPE_XPATH_SUFFIX = "/descendant::label[contains(text(),'Fault Type')]/following-sibling::div";
    private static final String JOB_INFO_GENERIC_XPATH_SUFFIX = "/descendant::label[contains(text(),'%s')]/following-sibling::div";

    @FindBy(css = JOB_INFO_XPATH)
    private WebElement jobInfo;

    @FindBy(xpath = JOB_INFO_XPATH + LOGGED_DATE_XPATH_SUFFIX)
    private WebElement loggedDate;

    @FindBy(xpath = JOB_INFO_XPATH + JOB_DESCRIPTION_XPATH_SUFFIX)
    private WebElement jobDescription;

    @FindBy(xpath = JOB_INFO_XPATH + JOB_TYPE_XPATH_SUFFIX)
    private WebElement jobType;

    @FindBy(xpath = JOB_INFO_XPATH + LOCATION_WITHIN_SITE_XPATH_SUFFIX)
    private WebElement locationWithinSite;

    @FindBy(xpath = JOB_INFO_XPATH + SUBTYPE_CLASSIFICATION_XPATH_SUFFIX)
    private WebElement subtypeClassification;

    @FindBy(xpath = JOB_INFO_XPATH + RESPONSE_PRIORITY_XPATH_SUFFIX)
    private WebElement responsePriority;

    @FindBy(xpath = JOB_INFO_XPATH + REPAIR_PRIORITY_XPATH_SUFFIX)
    private WebElement repairPriority;

    @FindBy(xpath = JOB_INFO_XPATH + FAULT_TYPE_XPATH_SUFFIX)
    private WebElement faultType;


    public JobInfoJobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded()
            .isFluentElementIsVisible(By.xpath(JOB_INFO_XPATH));
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

    public String getLoggedDate() {
        return loggedDate.getText();
    }

    public String getJobDescription() {
        return  jobDescription.getText();
    }

    public String getJobType() {
        return jobType.getText();
    }

    public String getLocation() {
        return locationWithinSite.getText();
    }

    public String getSubtypeClassification() {
        return subtypeClassification.getText();
    }

    public String getResponsePriority() {
        return responsePriority.getText();
    }

    public String getRepairPriority() {
        return responsePriority.getText();
    }

    public String getFaultType() {
        return faultType.getText();
    }

    public String getDetail(String label) {
        By by = By.xpath(String.format(JOB_INFO_GENERIC_XPATH_SUFFIX, label));
        WebElement element = driver.findElement(by);
        return element.getText();
    }
}
