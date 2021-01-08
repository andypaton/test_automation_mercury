package mercury.pageobject.web.admin.sites;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

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

public class AdminSitesPage extends Base_Page<AdminSitesPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Sites Admin";

    private static final String ADMIN_ACTION_XPATH = "//div[@class='admin-action']";

    private static final String ADMIN_ACTION_HEADER_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__header']//h1[contains(text(),'" + PAGE_TITLE + "')]";
    private static final String ADMIN_ACTION_CONTENT_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__content']";

    private static final String ADD_NEW_SITE_BUTTON_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//div[contains(text(), 'Add new site')]/..//button";

    private static final String TAB_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//md-tab-title[contains(text(), '%s')]";

    private static final String SITES_GRID_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//md-tab-content[contains(@class, 'active')]";
    private static final String SITES_GRID_NAME_FIELD_SEARCH_FILTER_XPATH = SITES_GRID_XPATH + "//span[@data-field='name' and @class='k-filtercell']//input[@role='textbox']";
    private static final String SITES_GRID_SITECODE_FIELD_SEARCH_FILTER_XPATH = SITES_GRID_XPATH + "//span[@data-field='siteCode' and @class='k-filtercell']//input[@role='textbox']";

    private static final String EDIT_LINK_XPATH = SITES_GRID_XPATH + "//td/a[contains(text(), 'Edit')]";

    private static final String FILTER_LIST_BOX_XPATH = "//ul[@role='listbox']/li[contains(text(), '%s')]";


    @FindBy(xpath = SITES_GRID_NAME_FIELD_SEARCH_FILTER_XPATH)
    private WebElement sitesGridNameFilter;

    @FindBy(xpath = SITES_GRID_SITECODE_FIELD_SEARCH_FILTER_XPATH)
    private WebElement sitesGridSiteCodeFilter;

    @FindBy(xpath = ADD_NEW_SITE_BUTTON_XPATH)
    private WebElement addNewSiteButton;

    @FindBy(xpath = EDIT_LINK_XPATH)
    private WebElement edit;


    public AdminSitesPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(SITES_GRID_XPATH));
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public AdminEditSitePage clickAddNewSiteButton() {
        addNewSiteButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditSitePage.class).get();
    }

    public void clickTab(String tabTitle) {
        waitForAngularRequestsToFinish();
        waitForElement(By.xpath(String.format(TAB_XPATH, tabTitle)), ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(By.xpath(String.format(TAB_XPATH, tabTitle)));
        waitForAngularRequestsToFinish();
        waitForElement(By.xpath(SITES_GRID_NAME_FIELD_SEARCH_FILTER_XPATH), ELEMENT_IS_CLICKABLE);
    }

    public void setSiteNameFilter(String siteName) {
        sitesGridNameFilter.sendKeys(siteName);
        waitForAngularRequestsToFinish();
        sitesGridNameFilter.sendKeys(Keys.ENTER);
        waitForAngularRequestsToFinish();
    }

    public void setSiteCodeFilter(String siteCode) {
        sitesGridSiteCodeFilter.sendKeys(siteCode);
        waitForAngularRequestsToFinish();
        sitesGridNameFilter.sendKeys(Keys.ENTER);
        waitForAngularRequestsToFinish();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(SITES_GRID_XPATH);
    }

    public AdminEditSitePage clickEditSiteLink() {
        edit.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditSitePage.class).get();
    }

}