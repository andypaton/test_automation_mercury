package mercury.pageobject.web.admin.jobs;

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

public class AdminJobsPage extends Base_Page<AdminJobsPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Jobs Admin";

    private static final String ADMIN_ACTION_XPATH = "//div[@class='admin-action']";

    private static final String ADMIN_ACTION_HEADER_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__header']//h1[contains(text(),'" + PAGE_TITLE + "')]";
    private static final String ADMIN_ACTION_CONTENT_XPATH = ADMIN_ACTION_XPATH + "//div[@class='admin-action__content']";

    private static final String SEARCH_FORM_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//form[@id='searchForm']";

    private static final String SEARCHBOX_XPATH = SEARCH_FORM_XPATH + "//input[@id='userSearch']";
    private static final String SEARCH_BUTTON_XPATH = SEARCH_FORM_XPATH + "//button[text() = 'Search']";
    private static final String SEARCH_NOTE_XPATH = SEARCH_FORM_XPATH + "//div[contains(text(), '%s')]";

    private static final String GRID_XPATH = ADMIN_ACTION_CONTENT_XPATH + "//table";

    private static final String TABLE_DATA_ROW_XPATH = GRID_XPATH + "/tbody//td[contains(text(), '%s')]";


    @FindBy(xpath = SEARCHBOX_XPATH)
    private WebElement searchBox;

    @FindBy(xpath = SEARCH_BUTTON_XPATH)
    private WebElement searchButton;



    public AdminJobsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(SEARCHBOX_XPATH));
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void enterJobReference(Integer jobReference) {
        searchBox.sendKeys(String.valueOf(jobReference));
    }

    public AdminJobsPage search() {
        searchButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminJobsPage.class).get();
    }

    public AdminJobStatusEditPage OpenJob(String jobReference) throws InterruptedException {
        waitForAnimation();
        By by = By.xpath(String.format(TABLE_DATA_ROW_XPATH, jobReference));
        driver.findElement(by).click();
        waitForAnimation();
        return PageFactory.initElements(driver, AdminJobStatusEditPage.class).get();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(GRID_XPATH, 1);
    }

    public Boolean isSearchBoxDisplayed() {
        return isElementPresent(By.xpath(SEARCHBOX_XPATH));
    }

    public Boolean isSearchNoteDisplayed(String note) {
        return isElementPresent(By.xpath(String.format(SEARCH_NOTE_XPATH, note)));
    }

}