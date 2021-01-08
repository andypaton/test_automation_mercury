package mercury.pageobject.web.admin.companies;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.gridV3.GridHelper;

public class AdminCompaniesPage extends Base_Page<AdminCompaniesPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String COMPANIES_PAGE_TITLE_XPATH = "//div[h1='Companies admin']";
    private static final String COMPANIES_PAGE_CONTAINER_XPATH = "//div[@class = 'container']";
    private static final String COMPANIES_PAGE_CONTENT_CONTAINER_XPATH = COMPANIES_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__content']";
    private static final String COMPANIES_PAGE_ANIMATOR_CONTAINER_XPATH = "//div[@class='k-animation-container']";
    private static final String COMPANIES_PAGE_FILTER_FORM_XPATH = COMPANIES_PAGE_ANIMATOR_CONTAINER_XPATH + "//form";

    private static final String COMPANIES_FILTER_INPUT_BOX_XPATH = COMPANIES_PAGE_FILTER_FORM_XPATH + "//input";
    private static final String COMPANIES_FILTER_BUTTON_XPATH = COMPANIES_PAGE_FILTER_FORM_XPATH + "//button[contains(@type,'submit')]";

    //Grid
    private static final String GRID_XPATH = COMPANIES_PAGE_CONTENT_CONTAINER_XPATH + "//div[@data-role='grid']";

    //Buttons
    private static final String ADMIN_HOME_BUTTON_XPATH = COMPANIES_PAGE_CONTENT_CONTAINER_XPATH + "//a[contains(text(),'Admin home')]";
    private static final String COMPANIES_ADD_COMPANY_BUTTON_XPATH = COMPANIES_PAGE_CONTENT_CONTAINER_XPATH + "//button//i[@class='icons__plus']";
    private static final String NAME_FILTER_BUTTON_XPATH = GRID_XPATH + "//th[contains(@data-title,'Name')]//span";

    @FindBy(xpath = COMPANIES_ADD_COMPANY_BUTTON_XPATH)
    private WebElement addCompanyButton;

    @FindBy(xpath = ADMIN_HOME_BUTTON_XPATH)
    private WebElement backToAdminHomeButton;

    @FindBy(xpath = NAME_FILTER_BUTTON_XPATH)
    private WebElement selectNameFilter;

    @FindBy(xpath = COMPANIES_FILTER_INPUT_BOX_XPATH)
    private WebElement filterInputBox;

    @FindBy(xpath = COMPANIES_FILTER_BUTTON_XPATH)
    private WebElement filterButton;

    public AdminCompaniesPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(COMPANIES_PAGE_TITLE_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectBackToAdminHome() {
        backToAdminHomeButton.click();
    }

    public Grid getGrid() {
        Grid grid = GridHelper.getGrid(GRID_XPATH);
        return grid;
    }

    public void searchInFilter(String filterText) {
        selectNameFilter.click();
        waitForAngularRequestsToFinish();
        filterInputBox.sendKeys(filterText);
        filterButton.click();
        waitForAngularRequestsToFinish();
    }

    public AdminEditCompanyPage clickEditButton(String company) {
        List<Row> rows = getGrid().getRows();
        rows.get(0).getCell(3).clickButton("Edit");
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminEditCompanyPage.class);
    }

}
