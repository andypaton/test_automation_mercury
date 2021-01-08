package mercury.pageobject.web.portal.jobs;

import static mercury.helpers.Constants.MAX_TIMEOUT;
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

public class SiteInfoJobDetailsPage extends Base_Page<SiteInfoJobDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Site Info";

    // Page elements
    private static final String SITE_INFO_XPATH = ".//div[contains(@class,'job-detail__site-info')]";
    private static final String SITE_XPATH_SUFFIX = "/descendant::label[contains(text(),'Site')]/following-sibling::div";
    private static final String SITE_ADDRESS_XPATH_SUFFIX = "/descendant::label[contains(text(),'Site Address')]/following-sibling::div";


    @FindBy(xpath = SITE_INFO_XPATH)
    private WebElement siteInfo;

    @FindBy(xpath = SITE_INFO_XPATH + SITE_XPATH_SUFFIX)
    private WebElement site;

    @FindBy(xpath = SITE_INFO_XPATH + SITE_ADDRESS_XPATH_SUFFIX)
    private WebElement siteAddress;

    public SiteInfoJobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded()
            .isFluentElementIsVisible(By.xpath(SITE_INFO_XPATH));
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

    public String getSiteName() {
        return site.getText();
    }

    public String getAddress1() {
        logger.info(siteAddress.getText());
        return siteAddress.getText();
    }

    public String getAddressLine(Integer lineNumber) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            By by = By.xpath(SITE_INFO_XPATH + SITE_ADDRESS_XPATH_SUFFIX);
            POHelper.isLoaded().isFluentElementIsVisible(by);
            return this.find(by).getText().split("\n")[lineNumber];
        } catch(NoSuchElementException e) {
            return null;
        }
        finally {
            driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }
}
