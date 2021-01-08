package mercury.pageobject.web.portal.fundingrequests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class JobDetailsPartial  extends Base_Page<JobDetailsPartial> {

    private static final Logger logger = LogManager.getLogger();

    //Page Elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class, 'main-content')]";

    private static final String QUOTE_DETAIL_CONTAINER_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'resource-quote-detail__container')]";

    // Partial Page container
    private static final String JOB_DETAILS_XPATH = QUOTE_DETAIL_CONTAINER_XPATH + "//h3[contains(text(), 'Job Details')]/..//div[@class='resource-quote-detail__resource-quote-info']";
    private static final String JOB_DETAILS_FIELDS_XPATH = JOB_DETAILS_XPATH + "//label[contains(text(), '%s')]/following-sibling::div";

    // Methods
    public JobDetailsPartial(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(QUOTE_DETAIL_CONTAINER_XPATH + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(JOB_DETAILS_XPATH));
            logger.info(QUOTE_DETAIL_CONTAINER_XPATH + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(QUOTE_DETAIL_CONTAINER_XPATH + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(QUOTE_DETAIL_CONTAINER_XPATH + " Load");
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(JOB_DETAILS_XPATH));
    }

    public String getJobDetailsFieldData(String field) {
        WebElement jobDetailsField = driver.findElement(By.xpath(String.format(JOB_DETAILS_FIELDS_XPATH, field)));
        return jobDetailsField.getText();
    }

}
