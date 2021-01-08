package mercury.pageobject.web.admin;

import static mercury.helpers.POHelper.clickJavascript;
import static mercury.helpers.POHelper.scrollTo;
import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.runtime.ThreadManager.getWebDriver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class AdminRulesEnginePage extends Base_Page<AdminRulesEnginePage>  {

    private static final Logger logger = LogManager.getLogger();

    private static final String RULES_ENGINE_PAGE_XPATH = "//div[@class='admin-action']";

    private static final String RULES_ENGINE_CONTENT_XPATH = RULES_ENGINE_PAGE_XPATH + "//div[@class='admin-action__content']";

    private static final String QUESTION_LIST_XPATH = RULES_ENGINE_CONTENT_XPATH + "//a[contains(text(), 'Question List')]";

    private static final String ADD_NEW_QUESTION_XPATH = RULES_ENGINE_CONTENT_XPATH + "//a[@type='button']";

    private static final String NAME_SEARCH_BOX_XPATH = RULES_ENGINE_CONTENT_XPATH + "//span[@data-field='name']//input";
    private static final String QUESTION_SEARCH_BOX_XPATH = RULES_ENGINE_CONTENT_XPATH + "//span[@data-field='text']//input";
    private static final String TEMPLATE_SEARCH_BOX_XPATH = RULES_ENGINE_CONTENT_XPATH + "//span[@data-field='rulesEngineTemplateName']//input";
    private static final String ACTIVE_SEARCH_BOX_XPATH = RULES_ENGINE_CONTENT_XPATH + "//span[@data-field='active']//input";

    private static final String CLEAR_ACTIVE_SEARCH_COLUMN_XPATH = RULES_ENGINE_CONTENT_XPATH + "//button[contains(@class, 'k-button-icon') and not (contains(@style, 'none'))]";

    private static final String SEARCH_RESULT_XPATH = "//li[contains(text(), '%s')]";

    private static final String QUESTIONS_GRID_ALL_HEADERS_XPATH = RULES_ENGINE_CONTENT_XPATH + "//tr[@role='row']";
    private static final String QUESTIONS_GRID_HEADER_XPATH = QUESTIONS_GRID_ALL_HEADERS_XPATH + "//a[text()='%s']";
    private static final String QUESTIONS_GRID_HEADER_SORT_XPATH = QUESTIONS_GRID_HEADER_XPATH + "/..";
    private static final String QUESTIONS_GRID_BLANK_HEADER_XPATH = QUESTIONS_GRID_ALL_HEADERS_XPATH + "//a[not(.//text())]";
    private static final String QUESTIONS_GRID_BLANK_HEADER_SORT_XPATH = QUESTIONS_GRID_BLANK_HEADER_XPATH + "/..";
    private static final String QUESTIONS_GRID_XPATH = RULES_ENGINE_CONTENT_XPATH + "//div[@id='rulesQuestionGrid']";
    private static final String QUESTIONS_EDIT_LINK_XPATH = QUESTIONS_GRID_XPATH + "//td[contains(text(), '%s')]/following-sibling::td//a[contains(text(), 'Edit')]";
    private static final String QUESTION_EDIT_LINK_XPATH = QUESTIONS_GRID_XPATH + "//td//a[contains(text(), 'Edit')]";
    private static final String RULES_GRID_XPATH = RULES_ENGINE_CONTENT_XPATH + "//div[@id='rulesEngineGrid']";
    
    private static final String ADD_NEW_RULE_BUTTON_XPATH = RULES_ENGINE_CONTENT_XPATH + "//i[@class='icons__plus']";
    private static final String RULES_EDIT_LINK_XPATH = RULES_GRID_XPATH + "//tr//td[contains(text(), '%s')]/../td[5]/a";

    @FindBy(xpath = ADD_NEW_RULE_BUTTON_XPATH)
    private WebElement addNewRuleButton;

    @FindBy(xpath = QUESTION_LIST_XPATH)
    private WebElement questionListMenuOption;

    @FindBy(xpath = NAME_SEARCH_BOX_XPATH)
    private WebElement searchName;

    @FindBy(xpath = QUESTION_SEARCH_BOX_XPATH)
    private WebElement searchQuestion;

    @FindBy(xpath = QUESTION_EDIT_LINK_XPATH)
    private WebElement editQuestion;

    @FindBy(xpath = TEMPLATE_SEARCH_BOX_XPATH)
    private WebElement searchTemplate;

    @FindBy(xpath = ACTIVE_SEARCH_BOX_XPATH)
    private WebElement searchActive;

    @FindBy(xpath = CLEAR_ACTIVE_SEARCH_COLUMN_XPATH)
    private WebElement clearActiveSearch;

    @FindBy(xpath = ADD_NEW_QUESTION_XPATH)
    private WebElement addNewQuestion;


    public AdminRulesEnginePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(RULES_GRID_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectQuestionListOption() {
        questionListMenuOption.click();
        waitForAngularRequestsToFinish();
    }

    public AdminRulePage editRuleFile(String ruleFile){
        WebElement requiredRuleFile = waitForElement(By.xpath(String.format(RULES_EDIT_LINK_XPATH, ruleFile)),ELEMENT_IS_CLICKABLE);
        requiredRuleFile.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminRulePage.class);
    }

    public AdminQuestionPage editQuestionFile(String question){
        WebElement requiredQuestionFile = waitForElement(By.xpath(String.format(QUESTIONS_EDIT_LINK_XPATH, question)),ELEMENT_IS_CLICKABLE);
        requiredQuestionFile.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminQuestionPage.class);
    }

    public AdminQuestionPage clickEditLink(){
        editQuestion.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminQuestionPage.class);
    }

    public AdminQuestionPage addNewQuestion(){
        addNewQuestion.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminQuestionPage.class);
    }

    public AdminRulePage clickAddNewRuleButton() {
        addNewRuleButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminRulePage.class);
    }

    public Grid getGrid() {
        return GridHelper.getGrid(QUESTIONS_GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(QUESTIONS_GRID_XPATH, 1);
    }

    public AdminRulesEnginePage tableSort(String header, boolean isAscending) {
        scrollTo(0);
        clickJavascript(getWebDriver().findElement(By.xpath(String.format(QUESTIONS_GRID_HEADER_XPATH, header))));
        if (isAscending != isAscending(header)) {
            clickJavascript(getWebDriver().findElement(By.xpath(String.format(QUESTIONS_GRID_HEADER_XPATH, header))));
        }
        return this;
    }

    public Boolean isAscending(String column) {
        if (getWebDriver().findElement(By.xpath(String.format(QUESTIONS_GRID_HEADER_SORT_XPATH, column))).getAttribute("aria-sort").contains("ascending")) {
            return true; // ascending
        } else if (getWebDriver().findElement(By.xpath(String.format(QUESTIONS_GRID_HEADER_SORT_XPATH, column))).getAttribute("aria-sort").contains("descending")) {
            return false; // descending
        } else {
            return null; // not sorted
        }
    }

    public AdminRulesEnginePage tableSortBlank(String header, boolean isAscending) {
        scrollTo(0);
        clickJavascript(getWebDriver().findElement(By.xpath(QUESTIONS_GRID_BLANK_HEADER_XPATH)));
        if (isAscending != isAscendingBlank()) {
            clickJavascript(getWebDriver().findElement(By.xpath(QUESTIONS_GRID_BLANK_HEADER_XPATH)));
        }
        return this;
    }

    public Boolean isAscendingBlank() {
        if (getWebDriver().findElement(By.xpath(QUESTIONS_GRID_BLANK_HEADER_SORT_XPATH)).getAttribute("aria-sort").contains("ascending")) {
            return true; // ascending
        } else if (getWebDriver().findElement(By.xpath(QUESTIONS_GRID_BLANK_HEADER_SORT_XPATH)).getAttribute("aria-sort").contains("descending")) {
            return false; // descending
        } else {
            return null; // not sorted
        }
    }

    public void searchNameColumn(String searchQuery) {
        searchName.clear();
        searchName.sendKeys(searchQuery);
        searchName.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void searchQuestionColumn(String searchQuery) {
        searchQuestion.clear();
        searchQuestion.sendKeys(searchQuery);
        searchName.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void searchTemplateColumn(String searchQuery) {
        searchTemplate.clear();
        searchTemplate.sendKeys(searchQuery);
        searchName.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void searchActiveColumn(String searchQuery) {
        searchActive.clear();
        searchActive.sendKeys(searchQuery);
        searchName.sendKeys(Keys.RETURN);
        waitForAngularRequestsToFinish();
    }

    public void clearActiveSearchColumn() {
        clearActiveSearch.click();
        waitForAngularRequestsToFinish();
    }

}