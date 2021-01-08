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

public class AssetInfoJobDetailsPage extends Base_Page<AssetInfoJobDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Asset Info";

    // Page elements
    private static final String ASSET_INFO_XPATH = ".//div[contains(@class,'job-detail__asset-info')]";
    private static final String ASSET_XPATH_SUFFIX = "/descendant::label[contains(text(),'Asset')]/following-sibling::div";

    @FindBy(css = ASSET_INFO_XPATH)
    private WebElement jobInfo;

    @FindBy(css = ASSET_INFO_XPATH + ASSET_XPATH_SUFFIX)
    private WebElement asset;


    public AssetInfoJobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded()
            .isFluentElementIsVisible(By.xpath(ASSET_INFO_XPATH));
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


    public AssetInfoJobDetailsPage verifyJobInfo(Object assetInfo){
        // TODO: code to verify the page contains the details in the assetInfo object passed in
        return this;
    }




}
