package mercury.pageobject.web.admin;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminQuestionPage extends Base_Page<AdminQuestionPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String QUESTION_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String QUESTION_CONTENT_XPATH = QUESTION_PAGE_XPATH + "//div[@class='admin-action__content']";
    private static final String QUESTION_FOOTER_XPATH = QUESTION_PAGE_XPATH + "//div[@class='footer-button-bar']";

    private static final String RULES_ENGINE_RULE_TEMPLATE_DROPDOWN_XPATH = QUESTION_CONTENT_XPATH + "//select[@id='RulesEngineRuleTemplate']";
    private static final String RULES_ENGINE_RULE_TEMPLATE_OPTION_XPATH = "//option[contains(text(),'%s')]";
    private static final String QUESTION_TYPE_DROPDOWN_XPATH = QUESTION_CONTENT_XPATH + "//select[@id='QuestionType']";
    private static final String QUESTION_TYPE_OPTION_XPATH = "//option[contains(text(),'%s')]";
    private static final String QUESTION_NAME_XPATH = QUESTION_CONTENT_XPATH + "//input[@id='RuleName']";
    private static final String QUESTION_TEXT_XPATH = QUESTION_CONTENT_XPATH + "//input[@id='RuleText']";

    private static final String QUESTION_TYPE_XPATH = QUESTION_CONTENT_XPATH + "//select[@id='QuestionType']/option[@selected='selected'][2]";

    private static final String REQUIRED_YES_XPATH = QUESTION_CONTENT_XPATH + "//label[@for='required_active_Yes']";
    private static final String REQUIRED_NO_XPATH = QUESTION_CONTENT_XPATH + "//label[@for='required_active_No']";
    private static final String ACTIVE_YES_XPATH = QUESTION_CONTENT_XPATH + "//label[@for='active_yes']";
    private static final String ACTIVE_NO_XPATH = QUESTION_CONTENT_XPATH + "//label[@for='active_no']";

    private static final String WARNING_ADD_OUTCOME_BUTTON_XPATH = QUESTION_CONTENT_XPATH + "//button[@class='btn btn-warning' and contains(text(), 'Add Outcome')]";
    private static final String PRIMARY_ADD_OUTCOME_BUTTON_XPATH = QUESTION_CONTENT_XPATH + "//button[@class='btn btn-primary' and contains(text(), 'Add Outcome')]";

    private static final String ADD_OUTCOME_TEXTBOX_XPATH = QUESTION_CONTENT_XPATH + "//input[contains(@ng-model, 'outcomes')]";

    private static final String BUTTON_XPATH = QUESTION_FOOTER_XPATH + "//button[text() = '%s']";


    @FindBy(xpath = RULES_ENGINE_RULE_TEMPLATE_DROPDOWN_XPATH)
    private WebElement templateDropdown;

    @FindBy(xpath = QUESTION_TYPE_DROPDOWN_XPATH)
    private WebElement questionTypeDropdown;

    @FindBy(xpath = QUESTION_NAME_XPATH)
    private WebElement questionName;

    @FindBy(xpath = QUESTION_TEXT_XPATH)
    private WebElement questionText;

    @FindBy(xpath = QUESTION_TYPE_XPATH)
    private WebElement questionType;

    @FindBy(xpath = REQUIRED_YES_XPATH)
    private WebElement requiredYes;

    @FindBy(xpath = REQUIRED_NO_XPATH)
    private WebElement requiredNo;

    @FindBy(xpath = ACTIVE_YES_XPATH)
    private WebElement activeYes;

    @FindBy(xpath = ACTIVE_NO_XPATH)
    private WebElement activeNo;

    @FindBy(xpath = WARNING_ADD_OUTCOME_BUTTON_XPATH)
    private WebElement warningAddOutcomeButton;

    @FindBy(xpath = PRIMARY_ADD_OUTCOME_BUTTON_XPATH)
    private WebElement primaryAddOutcomeButton;

    @FindBy(xpath = ADD_OUTCOME_TEXTBOX_XPATH)
    private List<WebElement> addOutcomes;


    public AdminQuestionPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(QUESTION_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectTemplateDropdownOption(String option) {
        templateDropdown.click();
        waitForAngularRequestsToFinish();
        WebElement dropdownOption = driver.findElement(By.xpath(String.format(RULES_ENGINE_RULE_TEMPLATE_OPTION_XPATH, option)));
        dropdownOption.click();
        templateDropdown.click(); //Dropdown is not closing when dropdownOption is clicked so this line is a hack to close the dropdown
    }

    public void selectQuestionTypeDropdownOption(String option) {
        questionTypeDropdown.click();
        waitForAngularRequestsToFinish();
        WebElement dropdownOption = driver.findElement(By.xpath(String.format(QUESTION_TYPE_OPTION_XPATH, option)));
        dropdownOption.click();
        questionTypeDropdown.click(); //Dropdown is not closing when dropdownOption is clicked so this line is a hack to close the dropdown
    }

    public void enterQuestionName(String option) {
        questionName.sendKeys(option);
    }

    public void enterQuestionText(String option) {
        questionText.sendKeys(option);
    }

    public String getQuestionType() {
        return questionType.getAttribute("innerText");
    }

    private WebElement getButton(String button) {
        WebElement we = driver.findElement(By.xpath(String.format(BUTTON_XPATH, button)));
        return we;
    }

    public void buttonIsClicked(String button) {
        POHelper.scrollToElement(getButton(button));
        getButton(button).click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isButtonEnabled(String button) {
        return getButton(button).isEnabled();
    }

    public void selectRequiredYes() {
        requiredYes.click();
        waitForAngularRequestsToFinish();
    }

    public void selectRequiredNo() {
        requiredNo.click();
        waitForAngularRequestsToFinish();
    }

    public void selectActiveYes() {
        POHelper.scrollToElement(activeYes);
        activeYes.click();
        waitForAngularRequestsToFinish();
    }

    public void selectActiveNo() {
        activeNo.click();
        waitForAngularRequestsToFinish();
    }

    public void clickWarningAddOutcomeButton() {
        warningAddOutcomeButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickPrimaryAddOutcomeButton() {
        primaryAddOutcomeButton.click();
        waitForAngularRequestsToFinish();
    }

    public void addOutcomeText(Integer index, String text) {
        addOutcomes.get(index).sendKeys(text);
    }
}
