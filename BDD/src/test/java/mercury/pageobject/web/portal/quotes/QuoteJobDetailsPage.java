package mercury.pageobject.web.portal.quotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class QuoteJobDetailsPage extends Base_Page<QuoteJobDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Job Info";

    // Core
    private static final String JOB_DETAILS_XPATH = ".//div[contains(@class,'job-detail__container') and contains(@class,'read-only')]";

    private static final String JOB_INFO_XPATH = JOB_DETAILS_XPATH + "/div[contains(@class,'job-detail__job-info')]";

    private static final String JOB_REF_XPATH = JOB_INFO_XPATH + "/descendant::label[contains(text(),'Job Ref')]/following-sibling::div";
    private static final String SITE_XPATH = JOB_INFO_XPATH + "/descendant::label[contains(text(),'Site')]/following-sibling::div";
    private static final String LOCATION_REF_XPATH = JOB_INFO_XPATH + "/descendant::label[contains(text(),'Location within site')]/following-sibling::div";
    private static final String SUB_TYPE_XPATH = JOB_INFO_XPATH + "/descendant::label[contains(text(),'Subtype')]/following-sibling::div";
    private static final String LOGGED_XPATH = JOB_INFO_XPATH + "/descendant::label[contains(text(),'Logged')]/following-sibling::div";

    @FindBy(css = JOB_INFO_XPATH)
    private WebElement jobInfo;
    
    @FindBy(xpath = JOB_DETAILS_XPATH)
    private WebElement jobDetails;

    @FindBy(xpath = JOB_REF_XPATH)
    private WebElement jobRef;

    @FindBy(xpath = SITE_XPATH)
    private WebElement site;

    @FindBy(xpath = LOCATION_REF_XPATH)
    private WebElement location;

    @FindBy(xpath = SUB_TYPE_XPATH)
    private WebElement subType;

    @FindBy(xpath = LOGGED_XPATH)
    private WebElement logged;


    public QuoteJobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(JOB_DETAILS_XPATH));
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

    public String getJobRef() {
        return jobRef.getText();
    }

    public String getSite() {
        return site.getText();
    }

    public String getLocation() {
        return location.getText();
    }

    public String getSubType() {
        return subType.getText();
    }

    public String getLogged() {
        return logged.getText();
    }

    public boolean isJobDetailViewOnly() {
        return jobDetails.isDisplayed();
    }
}