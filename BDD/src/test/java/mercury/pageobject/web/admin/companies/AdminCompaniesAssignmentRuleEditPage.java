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

public class AdminCompaniesAssignmentRuleEditPage extends Base_Page<AdminCompaniesAssignmentRuleEditPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_ASSIGNMENT_RULE_PAGE_CONTAINER_XPATH = "//div[@ng-app = 'assignmentRuleAdmin']";
    private static final String EDIT_ASSIGNMENT_RULE_PAGE_HEADER_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_CONTAINER_XPATH + "//h2";
    private static final String EDIT_ASSIGNMENT_RULE_PAGE_CONTENT_CONTAINER_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__sub-content']";

    //Labels
    private static final String EDIT_ASSIGNMENT_RULE_PAGE_COMPANY_LABEL_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_CONTENT_CONTAINER_XPATH + "//label[contains(text(),'Company')]";
    private static final String EDIT_ASSIGNMENT_RULE_PAGE_RULE_LABEL_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_CONTENT_CONTAINER_XPATH + "//label[contains(text(),'Rule')]";
    private static final String EDIT_ASSIGNMENT_RULE_PAGE_RULE_DETAIL_LABEL_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_CONTENT_CONTAINER_XPATH + "//label[contains(text(),'Rule Detail')]";

    //DropDowns
    private static final String EDIT_ASSIGNMENT_RULE_COMPANY_DROPDOWN_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_COMPANY_LABEL_XPATH + "//parent::div" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String EDIT_ASSIGNMENT_RULE_RULE_DROPDOWN_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_RULE_LABEL_XPATH + "//parent::div" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String EDIT_ASSIGNMENT_RULE_RULE_DETAIL_DROPDOWN_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_RULE_DETAIL_LABEL_XPATH + "//parent::div" + DROPDOWN_SEARCH_ARROW_XPATH;

    //Buttons
    private static final String ADD_ASSIGNMENT_RULES_CREATE_BUTTON_XPATH = EDIT_ASSIGNMENT_RULE_PAGE_CONTAINER_XPATH + "//span[contains(text(),'Save')]";

    @FindBy(xpath = EDIT_ASSIGNMENT_RULE_COMPANY_DROPDOWN_XPATH)
    private WebElement companyDropDown;

    @FindBy(xpath = EDIT_ASSIGNMENT_RULE_RULE_DROPDOWN_XPATH)
    private WebElement ruleDropDown;

    @FindBy(xpath = EDIT_ASSIGNMENT_RULE_RULE_DETAIL_DROPDOWN_XPATH)
    private WebElement ruleDetailDropDown;

    @FindBy(xpath = ADD_ASSIGNMENT_RULES_CREATE_BUTTON_XPATH)
    private WebElement saveButton;


    public AdminCompaniesAssignmentRuleEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(EDIT_ASSIGNMENT_RULE_PAGE_HEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void clickCompany(String companyName) {
        companyDropDown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectExactVisibleDropdownOption(companyName);
    }

    public void clickRule(String Rule) {
        ruleDropDown.click();
        waitForAngularRequestsToFinish();
        selectExactVisibleDropdownOption(Rule);
    }

    public void clickRuleDetail(String ruleDetail) {
        ruleDetailDropDown.click();
        waitForAngularRequestsToFinish();
        searchAndSelectExactVisibleDropdownOption(ruleDetail);
    }

    public void clickSave() {
        saveButton.click();
        waitForAngularRequestsToFinish();
    }

}
