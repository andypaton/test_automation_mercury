package mercury.pageobject.web.admin.sites;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditSiteTypePage extends Base_Page<AdminEditSiteTypePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADMIN_ACTION_XPATH = "//div[@class='admin-action']";

    private static final String ADMIN_ACTION_SUB_CONTENT_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__sub-content']";

    private static final String SITE_TYPE_GRID_XPATH = ADMIN_ACTION_SUB_CONTENT_XPATH + "//div[@data-role='grid']";
    private static final String SITE_TYPE_GRID_COLUMN_HEADER_FILTER_XPATH = SITE_TYPE_GRID_XPATH + "//th//a[contains(text(), '%s')]/preceding-sibling::a[contains(@class,'k-grid-filter')]";
    private static final String FILTER_FORM_XPATH = "//form[contains(@class,'k-filter')]";
    private static final String FILTER_DROPDOWN_XPATH = FILTER_FORM_XPATH + "//span[contains(@class, 'k-dropdown') and @aria-expanded='false'][1]";
    private static final String FILTER_TEXTBOX_XPATH = FILTER_FORM_XPATH + "//input[1]";
    private static final String FILTER_BUTTON_XPATH = FILTER_FORM_XPATH + "//button[contains(text(), 'Filter')]";
    private static final String ICON_IMAGE_XPATH = SITE_TYPE_GRID_XPATH + "//img[@src='%s']";

    private static final String SAVE_BUTTON_XPATH = ADMIN_ACTION_SUB_CONTENT_XPATH + "//button[contains(text(), 'Save')]";
    private static final String CANCEL_BUTTON_XPATH = ADMIN_ACTION_SUB_CONTENT_XPATH + "//button[contains(text(), 'Cancel')]";

    @FindBy(xpath = FILTER_TEXTBOX_XPATH)
    private WebElement filterTextbox;

    @FindBy(xpath = FILTER_DROPDOWN_XPATH)
    private WebElement filterDropdown;

    @FindBy(xpath = FILTER_BUTTON_XPATH)
    private WebElement filterButton;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    public AdminEditSiteTypePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(SITE_TYPE_GRID_XPATH));
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        return GridHelper.getGrid(SITE_TYPE_GRID_XPATH);
    }

    public void selectFilterDropdownOption(String option) {
        filterDropdown.click();
        selectExactVisibleDropdownOption(option);
    }

    public void setColumnHeaderFilter(String column, String option, String text) {
        WebElement columnFilter = driver.findElement(By.xpath(String.format(SITE_TYPE_GRID_COLUMN_HEADER_FILTER_XPATH, column)));
        columnFilter.click();
        waitForAngularRequestsToFinish();
        selectFilterDropdownOption(option);
        filterTextbox.clear();
        filterTextbox.sendKeys(text);
        filterButton.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isIconPictureDisplayed(String iconPath) {
        return isElementPresent(By.xpath(String.format(ICON_IMAGE_XPATH, iconPath)));
    }

    public void clickSaveButton() {
        POHelper.clickJavascript(save);
        waitForAngularRequestsToFinish();
    }

    public void clickCancelButton() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

}