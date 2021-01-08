package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminHomePage extends Base_Page<AdminHomePage>  {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Admin Home Page";

    private static final String ADMIN_PAGE_CSS = ".admin-action";

    // Admin Page Tiles
    private static final String TILE_XPATH = "//a[@class='site-admin-home-tile']/span[contains(text(), '%s')]";



    public AdminHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(ADMIN_PAGE_CSS));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    private WebElement tile(String tileName) {
        return driver.findElement(By.xpath(String.format(TILE_XPATH, tileName)));
    }

    public void selectTile(String tileName) throws Exception {
        WebElement tile = tile(tileName);
        POHelper.clickJavascript(tile);
        waitForAngularRequestsToFinish();
    }

    public boolean isTileDisplayed(String tile) {
        return isElementVisible(By.xpath(String.format(TILE_XPATH, tile)));
    }

}