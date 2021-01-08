package mercury.pageobject.web.admin;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminRulePage extends Base_Page<AdminRulePage>  {

    private static final Logger logger = LogManager.getLogger();


    private static final String RULE_TYPE_DROPDOWN_XPATH = "//select[@id='RuleType']";
    private static final String RULE_DESCRIPTION_XPATH = "//input[@id='RuleName']";

    private static final String ACTIVE_FROM_CLOCK_XPATH = "//input[@id='ActiveFrom']/..//span[@class='k-icon k-i-clock']";
    private static final String ACTIVE_TO_CLOCK_XPATH = "//input[@id='ActiveTo']/..//span[@class='k-icon k-i-clock']";

    private static final String ALL_JOB_QUESTIONS_XPATH = "(//div[@class='form-group'])[last()]//label";
    private static final String JOB_QUESTION_XPATH = "//label[contains(text(),'%s')]";

    private static final String FIELD_DROPDOWN_XPATH = "//select[@name='fieldSelection_0']";
    private static final String COMPARISON_DROPDOWN_XPATH = "//select[@name='comparisonOperator_0']";
    private static final String VALUE_DROPDOWN_XPATH = "//span[contains(@class, 'k-widget') and contains(@class ,'k-dropdown')]";
    private static final String VALUE_DROPDOWN_OPTION_XPATH = "//li[contains(text() ,'%s')]";

    private static final String ADD_NEW_CONDITION_XPATH = "//span[@class='icons__plus']";

    private static final String SAVE_BUTTON_XPATH = "//button[contains(text(),'Save')]";
    private static final String DELETE_BUTTON_XPATH = "//button[contains(text(),'Delete')]";
    private static final String CONFIRM_BUTTON_XPATH = "//button[contains(text(),'Confirm')]";


    @FindBy(xpath = RULE_TYPE_DROPDOWN_XPATH)
    private WebElement ruleTypeDropdown;

    @FindBy(xpath = FIELD_DROPDOWN_XPATH)
    private WebElement fieldDropdown;

    @FindBy(xpath = COMPARISON_DROPDOWN_XPATH)
    private WebElement comparisonDropdown;

    @FindBy(xpath = VALUE_DROPDOWN_XPATH)
    private WebElement valueDropdown;

    @FindBy(xpath = RULE_DESCRIPTION_XPATH)
    private WebElement ruleDescription;

    @FindBy(xpath = ACTIVE_FROM_CLOCK_XPATH)
    private WebElement activeFrom;

    @FindBy(xpath = ACTIVE_TO_CLOCK_XPATH)
    private WebElement activeTo;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = DELETE_BUTTON_XPATH)
    private WebElement deleteButton;

    @FindBy(xpath = CONFIRM_BUTTON_XPATH)
    private WebElement confirmButton;

    @FindBy(xpath = ADD_NEW_CONDITION_XPATH)
    private WebElement addNewConditionButton;

    public AdminRulePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(RULE_DESCRIPTION_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectRuleType(String ruleType) {
        Select select = new Select(ruleTypeDropdown);
        select.selectByVisibleText(ruleType);
        waitForAngularRequestsToFinish();
    }

    public void enterRuleDescription(String rule) {
        ruleDescription.sendKeys(rule);
    }

    public void setActiveFromDate() throws ParseException {
        activeFrom.click();
        waitForAngularRequestsToFinish();
        WebElement dropdownOption = driver.findElement(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH));
        dropdownOption.click();
        waitForAngularRequestsToFinish();
    }

    public void setActiveToDate() throws ParseException {
        activeTo.click();
        waitForAngularRequestsToFinish();
        WebElement dropdownOption = driver.findElement(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH));
        dropdownOption.click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getJobQuestions() {
        List<String> jobQuestions = new ArrayList<>();
        List<WebElement> jobQuestionsList = driver.findElements(By.xpath(ALL_JOB_QUESTIONS_XPATH));
        for (WebElement jobQuestion : jobQuestionsList) {
            jobQuestions.add(jobQuestion.getText());
        }
        return jobQuestions;
    }

    public void selectJobQuestion(String question) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_XPATH, question)),ELEMENT_IS_CLICKABLE);
        jobQuestion.click();
        waitForAngularRequestsToFinish();
    }

    public void selectFieldDropdown(String field) {
        Select select = new Select(fieldDropdown);
        select.selectByVisibleText(field);
        waitForAngularRequestsToFinish();
    }

    public void selectComparisonDropdown(String operator) {
        Select select = new Select(comparisonDropdown);
        select.selectByVisibleText(operator);
        waitForAngularRequestsToFinish();
    }

    public void enterValueDropdown(String valueText) throws InterruptedException {
        valueDropdown.click();
        waitForAngularRequestsToFinish();
        WebElement searchBox = driver.findElement(By.xpath(VISIBLE_DROPDOWN_SEARCHBOX_XPATH));
        searchBox.sendKeys(valueText);
        WebElement visibleOption = driver.findElement(By.xpath(String.format(VALUE_DROPDOWN_OPTION_XPATH, valueText)));
        POHelper.scrollToElement(visibleOption);
        visibleOption.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isAddNewConditionButtonEnabled() {
        return addNewConditionButton.isEnabled();
    }

    public void clickSaveButton() {
        saveButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickDeleteButton() {
        POHelper.scrollToElement(deleteButton);
        deleteButton.click();
    }

    public void confirmDelete() {
        confirmButton.click();
        waitUntilUrlContains("ConfirmDelete");
    }
}