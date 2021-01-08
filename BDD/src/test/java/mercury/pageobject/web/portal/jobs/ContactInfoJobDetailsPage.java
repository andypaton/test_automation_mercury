package mercury.pageobject.web.portal.jobs;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class ContactInfoJobDetailsPage extends Base_Page<ContactInfoJobDetailsPage> {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Contact Info";

    // Page elements
    private static final String CONTACT_INFO_XPATH = ".//div[contains(@class,'job-detail__contact-info')]";
    private static final String CONTACT_NAME_XPATH_SUFFIX = "/descendant::label[contains(text(),'Contact Name')]/following-sibling::div";
    private static final String CONTACT_TYPE_XPATH_SUFFIX = "/descendant::label[contains(text(),'Contact Type')]/following-sibling::div";
    private static final String CONTACT_NUMBER_XPATH_SUFFIX = "/descendant::label[contains(text(),'Contact Number')]/following-sibling::div";
    private static final String CONTACT_INSTANCE_NAME_XPATH_SUFFIX = "/descendant::label[contains(text(),'Contact Name')][%s]/following-sibling::div";
    private static final String CONTACT_INSTANCE_TYPE_XPATH_SUFFIX = "/descendant::label[contains(text(),'Contact Type')][%s]/following-sibling::div";
    private static final String CONTACT_INSTANCE_NUMBER_XPATH_SUFFIX = "/descendant::label[contains(text(),'Contact Number')][%s]/following-sibling::div";

    @FindBy(xpath = CONTACT_INFO_XPATH)
    private WebElement jobInfo;

    @FindBy(xpath = CONTACT_INFO_XPATH + CONTACT_NAME_XPATH_SUFFIX)
    private WebElement contactName;

    @FindBy(xpath = CONTACT_INFO_XPATH + CONTACT_TYPE_XPATH_SUFFIX)
    private WebElement contactType;

    @FindBy(xpath = CONTACT_INFO_XPATH + CONTACT_NUMBER_XPATH_SUFFIX)
    private WebElement contactNumber;

    public ContactInfoJobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded()
            .isFluentElementIsVisible(By.xpath(CONTACT_INFO_XPATH));
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

    public String getContactName(Integer contact) {
        return isElementPresent(By.xpath(String.format(CONTACT_INSTANCE_NAME_XPATH_SUFFIX,contact))) ? driver.findElement(By.xpath(String.format(CONTACT_INSTANCE_NAME_XPATH_SUFFIX,contact))).getText() : null;
    }

    public String getContactType(Integer contact) {
        return isElementPresent(By.xpath(String.format(CONTACT_INSTANCE_TYPE_XPATH_SUFFIX,contact))) ? driver.findElement(By.xpath(String.format(CONTACT_INSTANCE_TYPE_XPATH_SUFFIX,contact))).getText() : null;
    }

    public String getContactNumber(Integer contact) {
        return isElementPresent(By.xpath(String.format(CONTACT_INSTANCE_NUMBER_XPATH_SUFFIX,contact))) ? driver.findElement(By.xpath(String.format(CONTACT_INSTANCE_NUMBER_XPATH_SUFFIX,contact))).getText() : null;
    }

    public ContactInfoJobDetailsPage verifyJobInfo(Object contactInfo){
        // TODO: code to verify the page contains the details in the contactInfo object passed in
        return this;
    }

    public Integer getNumberOfContacts() {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        List<WebElement> headers = driver.findElements(By.xpath(CONTACT_INFO_XPATH + "/descendant::label[contains(text(),'Contact Name')]"));
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        return headers.size();
    }


}
