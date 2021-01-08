package mercury.pageobject.web.storeportal;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class StorePortalLogAJobPage extends Base_Page<StorePortalLogAJobPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Mercury Store Portal";

    private static final String PAGE_BODY_XPATH = "//div[@class='main']";

    //Labels
    private static final String LABEL_XPATH = PAGE_BODY_XPATH + "//label[contains(text(), '%s')]";
    private static final String RADIO_LABEL_XPATH = PAGE_BODY_XPATH + "//div[contains(text(), '%s')]";

    //Dropdown Fields
    private static final String DROPDOWN_XPATH = LABEL_XPATH + "/ancestor::div[@class='sp-form-component']//input";
    private static final String DROPDOWN_OPTIONS_XPATH = PAGE_BODY_XPATH + "//ng-dropdown-panel[contains(@class, 'ng-dropdown-panel')]//div[contains(@class, 'ng-option')]";
    private static final String DROPDOWN_VALUE_XPATH = LABEL_XPATH + "/ancestor::div[@class='sp-form-component']//span[@class='ng-value-label ng-star-inserted']";
    private static final String DROPDOWN_SEARCHED_OPTION_XPATH = PAGE_BODY_XPATH + "//div[contains(@class, 'ng-option')and contains(@class, 'ng-option-marked')and contains(@class, 'ng-star-inserted')]//span";

    //Job Questions Section
    private static final String JOB_QUESTIONS_HEADER_XPATH = PAGE_BODY_XPATH + "//div[contains(text(), 'Job Questions')]";
    private static final String JOB_QUESTIONS_XPATH = PAGE_BODY_XPATH + "//cfm-custom-questions//div[@class='sp-form-label-container']/child::*[1]";
    private static final String JOB_QUESTION_XPATH = JOB_QUESTIONS_XPATH + "[contains(text(), '%s')]";
    private static final String JOB_QUESTION_TYPE_XPATH = JOB_QUESTION_XPATH + "/ancestor::div[contains(@class, 'ng-trigger')]/child::*[1]/child::*[2]";
    private static final String JOB_QUESTION_RADIO_ANSWER_XPATH = RADIO_LABEL_XPATH + "/following::div[contains(text(), '%s')]";
    private static final String JOB_QUESTION_DROPDOWN_XPATH = LABEL_XPATH + "/following::div[contains(@class, 'ng-input')]";
    private static final String JOB_QUESTION_TEXTBOX_XPATH = LABEL_XPATH + "/following::div[1]//input";
    private static final String JOB_QUESTION_TEXTAREA_XPATH = LABEL_XPATH + "/following::div[1]//textarea";

    //Text Fields
    private static final String TEXTAREA_XPATH = LABEL_XPATH + "/ancestor::div[@class='ng-star-inserted']//textarea";
    private static final String TEXTBOX_XPATH = LABEL_XPATH + "/ancestor::div[@class='ng-star-inserted']//input";

    //Potential Duplicate Jobs
    private static final String POTENTIAL_DUPLICATE_XPATH = "//cfm-potential-duplicate-jobs";
    private static final String POTENTIAL_DUPLICATE_JOBREF_XPATH = POTENTIAL_DUPLICATE_XPATH + "//div[@class='job-title']";
    private static final String POTENTIAL_DUPLICATE_ITEM_XPATH = POTENTIAL_DUPLICATE_XPATH + "//span[contains(text(), '%s')]/following-sibling::span";

    //Save Button
    private static final String SAVE_BUTTON_XPATH = PAGE_BODY_XPATH + "//button[@name='submitJob']";

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = POTENTIAL_DUPLICATE_JOBREF_XPATH)
    private WebElement potentialDuplicateJobRef;


    public StorePortalLogAJobPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.waitWhileBusy();
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_BODY_XPATH));
            logger.info(PAGE_TITLE + ": Page loaded");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + ": Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isLabelDisplayed(String labelName) {
        return this.isElementClickable(By.xpath(String.format(LABEL_XPATH, labelName)));
    }

    public boolean isRadioButtonLabelDisplayed(String labelName) {
        return this.isElementClickable(By.xpath(String.format(RADIO_LABEL_XPATH, labelName)));
    }

    public void selectOptionFromDropdown(String dropdownName, String option) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)));
        POHelper.sendKeysWithoutWaitForKendo(dropdown, option);
        WebElement searchedOption = driver.findElement(By.xpath(DROPDOWN_SEARCHED_OPTION_XPATH));
        searchedOption.click();
        POHelper.waitWhileBusy();
    }

    public String selectRandomOptionFromDropdown(String dropdownName) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)));
        dropdown.click();
        return selectRandomVisibleDropdownOption(0, DROPDOWN_OPTIONS_XPATH);
    }

    public String getSelectedDropdownValue(String dropdownName) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_VALUE_XPATH, dropdownName)));
        return dropdown.getText();
    }

    public void enterTextIntoTextArea(String textAreaName, String text) {
        WebElement textBox = driver.findElement(By.xpath(String.format(TEXTAREA_XPATH, textAreaName)));
        textBox.sendKeys(text);
    }

    public void enterTextIntoTextBox(String textBoxName, String text) {
        WebElement textBox = driver.findElement(By.xpath(String.format(TEXTBOX_XPATH, textBoxName)));
        textBox.sendKeys(text);
    }

    public String selectRandomOptionFromJobQuestionDropdown(String dropdownName) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(JOB_QUESTION_DROPDOWN_XPATH, dropdownName)));
        dropdown.click();
        return selectRandomVisibleDropdownOption(0, DROPDOWN_OPTIONS_XPATH);
    }

    public void enterTextIntoJobQuestionTextBox(String textBoxName, String text) {
        WebElement textBox = driver.findElement(By.xpath(String.format(JOB_QUESTION_TEXTBOX_XPATH, textBoxName)));
        textBox.sendKeys(text);
    }

    public void enterTextIntoJobQuestionTextArea(String textAreaName, String text) {
        WebElement textBox = driver.findElement(By.xpath(String.format(JOB_QUESTION_TEXTAREA_XPATH, textAreaName)));
        textBox.sendKeys(text);
    }

    public Boolean isJobQuestionsDisplayed() {
        WebElement we = driver.findElement(By.xpath(JOB_QUESTIONS_HEADER_XPATH));
        return we.isDisplayed();
    }

    public void waitForJobQuestionsToBeDisplayed() {
        waitForElement(By.xpath(JOB_QUESTIONS_HEADER_XPATH), ELEMENT_IS_VISIBLE);
    }

    public void clickJobQuestionRadioAnswerButton(String radioName, String answer) {
        WebElement radio = driver.findElement(By.xpath(String.format(JOB_QUESTION_RADIO_ANSWER_XPATH, radioName, answer)));
        POHelper.clickJavascript(radio);
    }

    public void clickSaveButton() {
        POHelper.scrollToElement(save);
        POHelper.clickJavascript(save);
        POHelper.waitWhileBusy();
    }

    public String getPotentialDuplicateJobReference() {
        return potentialDuplicateJobRef.getText();
    }

    public String getPotentialDuplicateItem(String itemName) {
        WebElement item = driver.findElement(By.xpath(String.format(POTENTIAL_DUPLICATE_ITEM_XPATH, itemName)));
        return item.getText();
    }

    public List<String> getJobQuestions(){
        List<String> questions = new ArrayList<>();
        if (isElementPresent(By.xpath(JOB_QUESTIONS_XPATH))) {
            for (WebElement jobQuestion : driver.findElements(By.xpath(JOB_QUESTIONS_XPATH))){
                questions.add(jobQuestion.getAttribute("textContent"));
            }
        }
        return questions;
    }

    public String getJobQuestionTagName(String question) {
        WebElement element = driver.findElement(By.xpath(String.format(JOB_QUESTION_TYPE_XPATH, question)));
        return element.getTagName();
    }
}
