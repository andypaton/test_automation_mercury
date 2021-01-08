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

public class JobLoggedConfigmationPage  extends Base_Page<JobLoggedConfigmationPage>{
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Job Logged";
    private static final String PAGE_TITLE_XPATH = "//div//h1[text()='Job Logged']";

    private static final String STATUS_MESSAGE_CSS = " div.panel-body.bg-success";
    private static final String JOB_REFERENCE_CSS = STATUS_MESSAGE_CSS + " > strong";


    @FindBy(css = JOB_REFERENCE_CSS)
    private WebElement jobReference;

    @FindBy(css = STATUS_MESSAGE_CSS)
    private WebElement statusPanel;


    public JobLoggedConfigmationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_TITLE_XPATH));
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


    public String getJobReference() {
        return jobReference.getText();
    }

}


