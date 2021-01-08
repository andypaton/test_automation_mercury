package mercury.pageobject.web.admin.companies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class AdminCompaniesAddAssignmentRulePage extends Base_Page<AdminCompaniesAddAssignmentRulePage>{

    private static final Logger logger = LogManager.getLogger();

    private static final String ADD_ASSIGNMENT_RULES_PAGE_CONTAINER_XPATH = "//div[@ng-app = 'assignmentRuleAdmin']";
    private static final String ADD_ASSIGNMENT_RULES_PAGE_CONTENT_CONTAINER_XPATH = ADD_ASSIGNMENT_RULES_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__sub-content']";

    private static final String ADD_ASSIGNMENT_RULES_PAGE_SUBHEADER_XPATH = ADD_ASSIGNMENT_RULES_PAGE_CONTAINER_XPATH + "//h2";

    //Labels
    private static final String ADD_ASSIGNMENT_RULES_PAGE_COMPANY_LABEL_XPATH = ADD_ASSIGNMENT_RULES_PAGE_CONTENT_CONTAINER_XPATH + "//label[contains(text(),'Company')]";
    private static final String ADD_ASSIGNMENT_RULES_PAGE_RULE_LABEL_XPATH = ADD_ASSIGNMENT_RULES_PAGE_CONTENT_CONTAINER_XPATH + "//label[contains(text(),'Rule')]";
    private static final String ADD_ASSIGNMENT_RULES_PAGE_RULE_DETAIL_LABEL_XPATH = ADD_ASSIGNMENT_RULES_PAGE_CONTENT_CONTAINER_XPATH + "//label[contains(text(),'Rule Detail')]";

    //DropDowns
    private static final String ADD_ASSIGNMENT_RULES_COMPANY_DROPDOWN_XPATH = ADD_ASSIGNMENT_RULES_PAGE_COMPANY_LABEL_XPATH + "//parent::div" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ADD_ASSIGNMENT_RULES_RULE_DROPDOWN_XPATH = ADD_ASSIGNMENT_RULES_PAGE_RULE_LABEL_XPATH + "//parent::div" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ADD_ASSIGNMENT_RULES_RULE_DETAIL_DROPDOWN_XPATH = ADD_ASSIGNMENT_RULES_PAGE_RULE_DETAIL_LABEL_XPATH + "//parent::div" + DROPDOWN_SEARCH_ARROW_XPATH;

    //Buttons
    private static final String ADD_ASSIGNMENT_RULES_CREATE_BUTTON_XPATH = ADD_ASSIGNMENT_RULES_PAGE_CONTAINER_XPATH + "//span[contains(text(),'Create')]";

    @FindBy(xpath = ADD_ASSIGNMENT_RULES_COMPANY_DROPDOWN_XPATH)
    private WebElement companyDropDown;

    @FindBy(xpath = ADD_ASSIGNMENT_RULES_RULE_DROPDOWN_XPATH)
    private WebElement ruleDropDown;

    @FindBy(xpath = ADD_ASSIGNMENT_RULES_RULE_DETAIL_DROPDOWN_XPATH)
    private WebElement ruleDetailDropDown;

    @FindBy(xpath = ADD_ASSIGNMENT_RULES_CREATE_BUTTON_XPATH)
    private WebElement createButton;


    public AdminCompaniesAddAssignmentRulePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ADD_ASSIGNMENT_RULES_PAGE_SUBHEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void searchAndSelectCompany(String companyName) {
        companyDropDown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectExactVisibleDropdownOption(companyName);
    }

    public void selectRule(String Rule) {
        ruleDropDown.click();
        selectExactVisibleDropdownOption(Rule);
    }

    public void selectRuleDetail(String ruleDetail) {
        ruleDetailDropDown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectExactVisibleDropdownOption(ruleDetail);
    }

    public void selectCreate() {
        createButton.click();
        waitForAngularRequestsToFinish();
    }


}
