package mercury.pageobject.web.portal;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class PortalHomePage extends Base_Page<PortalHomePage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Portal Home Page";

    private static final String HOME_TITLE_XPATH = "//body//h1[text() = 'Home']";

    public static final String SUMMARY_TABLE_XPATH = "//table[@id = 'summary']";

    // Dashboard Area
    private static final String DASHBOARD_TILES_XPATH =  "//div[@class='dashboard-area']//a//span";
    private static final String DASHBOARD_TILE_HEADERS_XPATH =  DASHBOARD_TILES_XPATH + "[contains(@class, 'title')]";
    private static final String DASHBOARD_TILE_HEADER_LINK_XPATH = DASHBOARD_TILES_XPATH + "[contains(text(),'%s')]";
    private static final String DASHBOARD_TILE_COUNT_XPATH = DASHBOARD_TILE_HEADER_LINK_XPATH + "//preceding-sibling::span";

    @FindBy(xpath = DASHBOARD_TILE_HEADERS_XPATH)
    private List<WebElement> dashboardTileHeaders;


    public PortalHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            // Note: not waiting for all counts to be refreshed!!!
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(HOME_TITLE_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (WebDriverException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public Grid getGrid() {
        return GridHelper.getGrid(SUMMARY_TABLE_XPATH);
    }

    public List<String> getGridAsString(){
        return GridHelper.getRowsAsString(SUMMARY_TABLE_XPATH);
    }

    public List<String> getTiles() {
        List<String> rows = new ArrayList<String>();
        for (WebElement row : dashboardTileHeaders) {
            rows.add(row.getAttribute("innerText"));
        }
        return rows;
    }

    public String getCountsForTile(String tile) {
        WebElement tileCount = driver.findElement(By.xpath(String.format(DASHBOARD_TILE_COUNT_XPATH, tile)));
        String count = tileCount.getAttribute("innerText");
        return count;
    }

    public void clickDashboardLinkFromTile(String type) {
        WebElement element = driver.findElement(By.xpath(String.format(DASHBOARD_TILE_HEADER_LINK_XPATH, type)));
        element.click();
        waitForAngularRequestsToFinish();
    }

}
