package mercury.pageobject.web.admin.companies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
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

public class AdminCompaniesAssignmentRulesPage extends Base_Page<AdminCompaniesAssignmentRulesPage>{

    private static final Logger logger = LogManager.getLogger();

    private static final String ASSIGNMENT_RULES_PAGE_CONTAINER_XPATH = "//div[@ng-app = 'assignmentRuleAdmin']";
    private static final String ASSIGNMENT_RULES_PAGE_CONTENT_CONTAINER_XPATH = ASSIGNMENT_RULES_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__sub-content']";
    private static final String ASSIGNMENT_RULES_PAGE_ANIMATOR_CONTAINER_XPATH = "//div[@class='k-animation-container']";
    private static final String ASSIGNMENT_RULES_PAGE_FILTER_FORM_XPATH = ASSIGNMENT_RULES_PAGE_ANIMATOR_CONTAINER_XPATH + "//form";

    private static final String ASSIGNMENT_RULES_FILTER_INPUT_BOX_XPATH = ASSIGNMENT_RULES_PAGE_FILTER_FORM_XPATH + "//input";
    private static final String ASSIGNMENT_RULES_PAGE_SUBHEADER_XPATH = ASSIGNMENT_RULES_PAGE_CONTAINER_XPATH + "//h2";

    //Grid
    private static final String GRID_XPATH = ASSIGNMENT_RULES_PAGE_CONTENT_CONTAINER_XPATH + "//div[@data-role='grid']";

    //Cells
    private static final String EDIT_CELL_CSS = "div.k-grid-content > table > tbody  > tr > td > a[class='k-button k-button-icontext k-grid-Edit']";
    private static final String DELETE_CELL_CSS = "div.k-grid-content > table > tbody  > tr > td > a[class='k-button k-button-icontext k-grid-Delete']";

    //Buttons
    private static final String ASSIGNMENT_RULES_ADD_RULE_BUTTON_XPATH = ASSIGNMENT_RULES_PAGE_CONTENT_CONTAINER_XPATH + "//button//i[@class='icons__plus']";
    private static final String COMPANY_FILTER_BUTTON_XPATH = GRID_XPATH + "//th[contains(@data-title,'Company')]//span";
    private static final String RULE_DETAILS_FILTER_BUTTON_XPATH = GRID_XPATH + "//th[contains(@data-title,'Rule Detail')]//a//span";
    private static final String ASSIGNMENT_RULES_FILTER_BUTTON_XPATH = ASSIGNMENT_RULES_PAGE_FILTER_FORM_XPATH + "//button[contains(@type,'submit')]";

    @FindBy(xpath = ASSIGNMENT_RULES_ADD_RULE_BUTTON_XPATH)
    private WebElement addRuleButton;

    @FindBy(xpath = COMPANY_FILTER_BUTTON_XPATH)
    private WebElement companyFilterButton;

    @FindBy(xpath = RULE_DETAILS_FILTER_BUTTON_XPATH)
    private WebElement ruleDetailsFilterButton;

    @FindBy(xpath = ASSIGNMENT_RULES_FILTER_BUTTON_XPATH)
    private WebElement filterButton;

    @FindBy(xpath = ASSIGNMENT_RULES_FILTER_INPUT_BOX_XPATH)
    private WebElement filterInputBox;

    public AdminCompaniesAssignmentRulesPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ASSIGNMENT_RULES_PAGE_SUBHEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        Grid grid = GridHelper.getGrid(GRID_XPATH);
        return grid;
    }

    public void searchInCompanyFilter(String filterText) {
        companyFilterButton.click();
        waitForAngularRequestsToFinish();
        filterInputBox.sendKeys(filterText);
        filterButton.click();
        waitForAngularRequestsToFinish();
    }

    public String getEditCell() {
        return EDIT_CELL_CSS;
    }

    public String getDeleteCell() {
        return DELETE_CELL_CSS;
    }

    public void searchInRuleDetailFilter(String filterText) {
        ruleDetailsFilterButton.click();
        waitForJavascriptToLoad();
        //        POHelper.sendKeys(filterInputBox, filterText, 10);
        POHelper.sendKeys(filterInputBox, filterText);
        filterButton.click();
        waitForAngularRequestsToFinish();

    }

    public void clearRuleDetailFilter() {
        ruleDetailsFilterButton.click();
        waitForJavascriptToLoad();
        filterInputBox.clear();
        filterButton.click();
        waitForAngularRequestsToFinish();

    }

    public AdminCompaniesAddAssignmentRulePage clickAddNewRule() {
        addRuleButton.click();
        return PageFactory.initElements(driver, AdminCompaniesAddAssignmentRulePage.class);
    }
}
