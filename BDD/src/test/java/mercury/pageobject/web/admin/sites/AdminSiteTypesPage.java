package mercury.pageobject.web.admin.sites;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class AdminSiteTypesPage extends Base_Page<AdminSiteTypesPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Site Types";

    private static final String ADMIN_ACTION_XPATH = "//div[@class='admin-action']";

    private static final String ADMIN_ACTION_SUB_HEADER_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__sub-header']//h2[contains(text(),'" + PAGE_TITLE + "')]";
    private static final String ADMIN_ACTION_SUB_CONTENT_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__sub-content']";

    private static final String SITE_TYPES_GRID_XPATH = ADMIN_ACTION_SUB_CONTENT_XPATH + "//div[@data-role='grid']";
    private static final String SITE_TYPES_GRID_SEARCH_FILTER_XPATH = SITE_TYPES_GRID_XPATH + "//span[contains(@class, 'k-filtercell')]//input[@role='textbox']";

    private static final String EDIT_LINK_XPATH = SITE_TYPES_GRID_XPATH + "//td/a[contains(text(), 'Edit')]";


    @FindBy(xpath = SITE_TYPES_GRID_SEARCH_FILTER_XPATH)
    private WebElement searchFilter;

    @FindBy(xpath = EDIT_LINK_XPATH)
    private WebElement edit;

    public AdminSiteTypesPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(SITE_TYPES_GRID_XPATH));
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        return GridHelper.getGrid(SITE_TYPES_GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(SITE_TYPES_GRID_XPATH, 1);
    }

    public void searchForSiteType(String siteType) {
        searchFilter.sendKeys(siteType);
        waitForAngularRequestsToFinish();
        searchFilter.sendKeys(Keys.ENTER);
        waitForAngularRequestsToFinish();
    }

    public AdminEditSiteTypePage clickEditLink() {
        edit.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditSiteTypePage.class).get();
    }

}