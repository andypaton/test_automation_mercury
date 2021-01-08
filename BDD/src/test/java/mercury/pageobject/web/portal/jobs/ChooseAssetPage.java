package mercury.pageobject.web.portal.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class ChooseAssetPage extends Base_Page<ChooseAssetPage>  {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Update Job";

    // Page elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTENT_XPATH + "//h1";

    // Choose Asset form
    private static final String CHANGE_ASSET_FORM_XPATH = PAGE_BODY_CONTAINER_XPATH +"//*[@id='changeAssetForm']";

    private static final String GRID_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@id='AssetsGrid']";
    private static final String ASSETS_GRID_CONTENT_XPATH = GRID_XPATH + "//div[contains(@class, 'k-grid-content')]";

    private static final String TABLE_BODY_XPATH = ASSETS_GRID_CONTENT_XPATH + "//table/tbody";
    private static final String TABLE_ROWS_XPATH = TABLE_BODY_XPATH + "//tr";

    private static final String CHANGE_ASSET_XPATH = CHANGE_ASSET_FORM_XPATH + "//input[contains(@class, 'btn-primary') and contains(@class,'btn') and contains(@value, 'Change Asset')]";


    @FindBy(xpath = PAGE_HEADER_XPATH)
    private WebElement pageHeader;

    @FindBy(xpath = ASSETS_GRID_CONTENT_XPATH)
    private WebElement assetsGrid;

    @FindBy(xpath = CHANGE_ASSET_XPATH)
    private WebElement changeAsset;


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ASSETS_GRID_CONTENT_XPATH));
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
        return pageHeader.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(ASSETS_GRID_CONTENT_XPATH));
    }
    public ChooseAssetPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    // Page interactions
    public UpdateJobPage selectTopAsset() throws Throwable {
        waitForAnimation();
        By by = By.xpath(TABLE_ROWS_XPATH);
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAnimation();
        this.waitForAngularRequestsToFinish();
        changeAsset.click();
        return PageFactory.initElements(driver, UpdateJobPage.class).get();
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(ASSETS_GRID_CONTENT_XPATH);
    }

    public Grid getGrid() {
        return GridHelper.getGrid(ASSETS_GRID_CONTENT_XPATH);
    }


}
