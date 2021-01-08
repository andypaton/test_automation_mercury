package mercury.pageobject.web.admin.companies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditCompanyAliasesPartial extends Base_Page<AdminEditCompanyAliasesPartial> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_COMPANY_PAGE_CONTAINER_XPATH = "//div[@class = 'container']";
    private static final String EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH = EDIT_COMPANY_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__content']";

    private static final String ALIASES_SUBHEADER_XPATH = EDIT_COMPANY_PAGE_CONTENT_CONTAINER_XPATH + "//h3[contains(.,'Aliases')]";
    private static final String ALIASES_SECTION_XPATH = ALIASES_SUBHEADER_XPATH + "//parent::div//parent::div[@class = 'admin-action__section ng-scope']";
    private static final String ALIASES_SECTION_ANIMATOR_CONTAINER_XPATH = "//div[@class='k-animation-container']";
    private static final String ALIASES_SECTION_FILTER_FORM_XPATH = ALIASES_SECTION_ANIMATOR_CONTAINER_XPATH + "//form";

    private static final String ALIASES_SECTION_FILTER_INPUT_BOX_XPATH = ALIASES_SECTION_FILTER_FORM_XPATH + "//input";

    //Grid
    private static final String GRID_XPATH = ALIASES_SECTION_XPATH + "//div[@id='company-alias-grid']";
    private static final String GRID_HEADERS_CSS = "#company-alias-grid > div.k-grid-header > div > table > thead > tr";


    //Buttons
    private static final String ADD_COMPANY_ALIAS_BUTTON_XPATH = ALIASES_SECTION_XPATH + "//a[contains(text(),'Add Company Alias')]";
    private static final String ALIASES_NAME_FILTER_BUTTON_XPATH = ALIASES_SECTION_XPATH + "//a[contains(text(),'Name')]/preceding-sibling::a//span[contains(@class,'k-icon k-filter')]";
    private static final String ALIASES_FILTER_BUTTON_XPATH = ALIASES_SECTION_FILTER_FORM_XPATH + "//button[contains(@type,'submit')]";

    @FindBy(xpath = ADD_COMPANY_ALIAS_BUTTON_XPATH)
    private WebElement addCompanyAliasButton;

    @FindBy(xpath = ALIASES_NAME_FILTER_BUTTON_XPATH)
    private WebElement selectNameFilter;

    @FindBy(xpath = ALIASES_SECTION_FILTER_INPUT_BOX_XPATH)
    private WebElement filterInputBox;

    @FindBy(xpath = ALIASES_FILTER_BUTTON_XPATH)
    private WebElement filterButton;


    public AdminEditCompanyAliasesPartial(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ALIASES_SUBHEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void clickAddCompanyAlias() {
        addCompanyAliasButton.click();
        waitForAngularRequestsToFinish();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public void searchInNameFilter(String filterText) {
        selectNameFilter.click();
        waitForAngularRequestsToFinish();
        filterInputBox.sendKeys(filterText);
        filterButton.click();
        waitForAngularRequestsToFinish();
    }

}
