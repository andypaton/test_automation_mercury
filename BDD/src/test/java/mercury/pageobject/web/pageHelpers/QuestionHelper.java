package mercury.pageobject.web.pageHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import mercury.databuilders.DataGenerator;
import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class QuestionHelper extends Base_Page<QuestionHelper> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "QUESTION HELPER";
    private String PARENT;
    private static final String QUESTION_LABEL = "//label[normalize-space(text()) = '%s']";
    private static final String LOWERCASE_QUESTION_LABEL = "//label[contains(" + LOWERCASE_TEXT + ",'%s')]";

    private static final String QUESTIONS_XPATH = "//label";
    private static final String BASIC_QUESTION_YES_XPATH = "//label[contains(text(),'%s')]/..//label[text()='Yes']";
    private static final String BASIC_QUESTION_NO_XPATH = "//label[contains(text(),'%s')]/..//label[text()='No']";

    private static final String ANSWER_XPATH = QUESTION_LABEL + "/following-sibling::div";
    private static final String ANSWER_FOR_QUESTION_XPATH = QUESTION_LABEL + "/following-sibling::div//*[contains(@class, 'k-input')]";

    private static final String FORM_CONTROL_DROPDOWN_XPATH = QUESTION_LABEL + "/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String FORM_CONTROL_DROPDOWN_SELECTED_VALUE_XPATH = QUESTION_LABEL + DROPDOWN_SELECTED_VALUE_XPATH;
    private static final String FORM_CONTROL_TEXTAREA_XPATH = LOWERCASE_QUESTION_LABEL + "/following-sibling::textArea";
    private String FORM_CONTROL_PARENT_TEXTAREA_XPATH = "//label[contains(text(),'%s')]" + "/ancestor::div/following-sibling::div" + LOWERCASE_QUESTION_LABEL + "/following-sibling::textArea";
    private String FORM_CONTROL_NUMERIC_XPATH = QUESTION_LABEL + "/following-sibling::input";


    // ANSWERS
    private String ANSWERS_TEXT_XPATH = QUESTION_LABEL + "/following-sibling::input[@type = 'text']";
    private String ANSWERS_TEXT2_XPATH = QUESTION_LABEL + "/following-sibling::div/input[@type = 'text']";
    private String ANSWERS_TEXTBOX_XPATH = QUESTION_LABEL + "/following-sibling::textarea";
    private String ANSWERS_NUMBER_XPATH = QUESTION_LABEL + "/following-sibling::input[@type = 'number']";
    private String ANSWERS_DROPDOWN_XPATH = QUESTION_LABEL + "/following-sibling::div[1]//span[contains(@class, 'k-dropdown')]";
    private String ANSWERS_RADIO_BUTTON_XPATH = QUESTION_LABEL + "/..//input[@type = 'radio']";
    private String ANSWERS_CHECKBOX_XPATH = QUESTION_LABEL + "/preceding-sibling::input[@type='checkbox']";

    // Types
    private static final String TEXT = "text";
    private static final String TEXT2 = "text2";
    private static final String TEXTBOX = "textbox";
    private static final String NUMERIC = "numeric";
    private static final String DROPDOWN = "dropdown";
    private static final String CHECKBOX = "checkbox";
    private static final String RADIO_BUTTON = "radio";

    private static WebElement focus;

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PARENT));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    public QuestionHelper(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void load(String xpath) {
        PARENT = xpath;
    }

    public boolean isQuestionVisible(String question) {
        return isElementVisible(By.xpath(String.format(QUESTION_LABEL, question)));
    }

    public void clickYes(String question) {
        focus = waitForElement(By.xpath(String.format(PARENT + BASIC_QUESTION_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        focus.click();
        waitForAngularRequestsToFinish();
    }

    public void clickNo(String question) {
        focus = waitForElement(By.xpath(String.format(PARENT + BASIC_QUESTION_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        focus.click();
        waitForAngularRequestsToFinish();
    }

    public String selectRandomAnwser(String question) {
        focus = driver.findElement(By.xpath(String.format(PARENT + FORM_CONTROL_DROPDOWN_XPATH, question)));
        focus.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption(0);
    }

    public void selectExactAnwser(String question, String answer) {
        focus = driver.findElement(By.xpath(String.format(PARENT + FORM_CONTROL_DROPDOWN_XPATH, question)));
        focus.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(answer);
    }

    public String getSelectedText(String question) {
        focus = driver.findElement(By.xpath(String.format(PARENT + FORM_CONTROL_DROPDOWN_SELECTED_VALUE_XPATH, question)));
        return focus.getText();
    }

    public void enterText(String question, String text) {
        focus = driver.findElement(By.xpath(String.format(PARENT + ANSWERS_TEXT_XPATH, question)));
        focus.clear();
        focus.sendKeys(text);
    }

    public void enterText2(String question, String text) {
        focus = driver.findElement(By.xpath(String.format(PARENT + ANSWERS_TEXT2_XPATH, question)));
        focus.clear();
        focus.sendKeys(text);
    }

    public void enterFreeText(String question, String text) {
        focus = driver.findElement(By.xpath(String.format(PARENT + FORM_CONTROL_TEXTAREA_XPATH, question.toLowerCase())));
        focus.clear();
        focus.sendKeys(text);
    }

    @Override
    public void enterFreeText(String parentQuestion, String question, String text) {
        focus = driver.findElement(By.xpath(String.format(PARENT + FORM_CONTROL_PARENT_TEXTAREA_XPATH, parentQuestion, question.toLowerCase())));
        focus.clear();
        focus.sendKeys(text);
    }

    public void enterNumeric(String question, String text) {
        focus = driver.findElement(By.xpath(String.format(PARENT + FORM_CONTROL_NUMERIC_XPATH, question)));
        focus.clear();
        focus.sendKeys(text);
    }

    public String clickAnswerButton(String question, String answer) {
        if ("random".equalsIgnoreCase(answer)) {
            List<WebElement> list = driver.findElements(By.xpath(String.format(PARENT + "//label[contains(text(),'%s')]/..//div[contains(@class,'yesNoRadio')]//label", question)));
            Random rand = new Random();
            focus = list.get(rand.nextInt(list.size()));
            answer = focus.getText();
        }

        focus = waitForElement(By.xpath(String.format(PARENT + "//label[contains(text(),'%s')]/..//label[text()='%s']", question, answer)), State.ELEMENT_IS_CLICKABLE);
        focus.click();
        waitForAngularRequestsToFinish();
        return answer;
    }

    public void selectCheckbox(String question) {
        focus = driver.findElement(By.xpath(String.format(PARENT + ANSWERS_CHECKBOX_XPATH, question)));
        if ( !focus.isSelected() ) {
            focus.click();
            waitForAngularRequestsToFinish();
        }
    }

    public List<String> getAllQuestions() {
        List<String> questions = new ArrayList<>();
        if (isElementPresent(By.xpath(PARENT + QUESTIONS_XPATH))) {
            for (WebElement question : driver.findElements(By.xpath(PARENT + QUESTIONS_XPATH))) {
                if ( !isElementPresent(question, By.xpath("./ancestor::div[contains(@class,'yesNoRadio')]")) ) { // not a radio button option
                    if (!question.getText().trim().isEmpty()) {
                        questions.add(question.getText());
                    }
                }
            }
        }
        return questions;
    }

    public String getAnswer(String question) {
        if (isElementPresent(By.xpath(PARENT + QUESTIONS_XPATH))) {

            String type = getAnswerType(question);
            if (type == null) {
                return null;
            } else if (RADIO_BUTTON.equals(type)) {
                List<WebElement> list = driver.findElements(By.xpath(String.format(PARENT + "//label[contains(text(),'%s')]/..//div[contains(@class,'yesNoRadio')]//input", question)));
                for (int i = 0; i < list.size(); i++) {
                    WebElement we = list.get(i);
                    if (we.isSelected()) {
                        return we.findElement(By.xpath("../label[" + (i+1) + "]")).getText();
                    }
                }
                return null;

            } else if (NUMERIC.equals(type)) {
                WebElement we = driver.findElement(By.xpath(String.format(ANSWERS_NUMBER_XPATH, question)));
                return we.getAttribute("value");

            } else if (TEXTBOX.equals(type)) {
                WebElement we = driver.findElement(By.xpath(String.format(ANSWERS_TEXTBOX_XPATH, question)));
                return we.getAttribute("value");

            } else {
                WebElement we = driver.findElement(By.xpath(String.format(ANSWER_FOR_QUESTION_XPATH, question)));
                return we.getAttribute("innerText");
            }

        } else {
            WebElement we = driver.findElement(By.xpath(String.format(ANSWER_XPATH, question)));
            return we.getAttribute("innerText");
        }
    }

    public void updateAnswer(String question, String answer) {
        if (TEXT.equals(getAnswerType(question))) {
            enterText(question, answer);

        } else if (TEXT2.equals(getAnswerType(question))) {
            enterText2(question, answer);

        } else if (TEXTBOX.equals(getAnswerType(question))) {
            enterFreeText(question, answer);

        } else if (NUMERIC.equals(getAnswerType(question))) {
            enterNumeric(question, answer);

        } else if (DROPDOWN.equals(getAnswerType(question))) {
            selectExactAnwser(question, answer);

        } else if (RADIO_BUTTON.equals(getAnswerType(question))) {
            clickAnswerButton(question, answer);

        } else if (CHECKBOX.equals(getAnswerType(question))) {
            if ("checked".equals(answer)) {
                selectCheckbox(question);
            }
        }
    }

    public String updateAnswer(String question) {
        if (TEXT.equals(getAnswerType(question))) {
            String word = DataGenerator.generateRandomWord();
            enterText(question, word);
            return word;

        } else if (TEXTBOX.equals(getAnswerType(question))) {
            String word = DataGenerator.generateRandomWord();
            enterFreeText(question, word);
            return word;

        } else if (NUMERIC.equals(getAnswerType(question))) {
            String randomNum = String.valueOf(RandomUtils.nextInt(1,  100));
            enterNumeric(question, randomNum);
            return randomNum;

        } else if (DROPDOWN.equals(getAnswerType(question))) {
            return selectRandomAnwser(question);

        } else if (RADIO_BUTTON.equals(getAnswerType(question))) {
            return clickAnswerButton(question, "random");

        } else if (CHECKBOX.equals(getAnswerType(question))) {
            if (RandomUtils.nextBoolean()) {
                selectCheckbox(question);
                return "checked";
            }
            return "not checked";
        }
        return null;
    }

    public Boolean isAnswerReadOnly(String question) {
        if (TEXT.equals(getAnswerType(question))) {
            WebElement we = driver.findElement(By.xpath(String.format(ANSWERS_TEXT_XPATH, question)));
            String attr = we.getAttribute("readonly");
            return attr != null ? attr.equals("true") : false;

        } else if (TEXTBOX.equals(getAnswerType(question))) {
            WebElement we = driver.findElement(By.xpath(String.format(ANSWERS_TEXTBOX_XPATH, question)));

            if (we.getAttribute("aria-readonly") != null) {
                return we.getAttribute("aria-readonly").equals("true");

            } else if (we.getAttribute("readonly") != null) {
                return we.getAttribute("readonly").equals("true");

            } else {
                return false;
            }

        } else if (NUMERIC.equals(getAnswerType(question))) {
            WebElement we = driver.findElement(By.xpath(String.format(ANSWERS_NUMBER_XPATH, question)));
            String attr = we.getAttribute("readonly");
            return attr != null ? attr.equals("true") : false;

        } else if (DROPDOWN.equals(getAnswerType(question))) {
            WebElement we = driver.findElement(By.xpath(String.format(ANSWERS_DROPDOWN_XPATH, question)));
            String attr = we.getAttribute("aria-readonly");
            return attr != null ? attr.equals("true") : false;

        } else if (RADIO_BUTTON.equals(getAnswerType(question))) {
            WebElement we = driver.findElement(By.xpath(String.format(ANSWERS_RADIO_BUTTON_XPATH, question)));
            String attr = we.getAttribute("disabled");
            return attr != null ? attr.equals("disabled") || attr.equals("true") : false;

        } else if (CHECKBOX.equals(getAnswerType(question))) {
            WebElement we = driver.findElement(By.xpath(String.format(ANSWERS_CHECKBOX_XPATH, question)));
            String attr = we.getAttribute("disabled");
            return attr != null ? attr.equals("disabled") : false;

        }
        return true; // defaulting to true (eg. if answer not found)
    }

    public String getAnswerType(String question) {

        if (isElementPresent(By.xpath(String.format(PARENT + ANSWERS_CHECKBOX_XPATH, question)))) {
            return CHECKBOX;

        } else if (isElementPresent(By.xpath(String.format(ANSWERS_NUMBER_XPATH, question)))) {
            return NUMERIC;

        } else if (isElementPresent(By.xpath(String.format(ANSWERS_DROPDOWN_XPATH, question)))) {
            return DROPDOWN;

        } else if (isElementPresent(By.xpath(String.format(ANSWERS_RADIO_BUTTON_XPATH, question)))) {
            return RADIO_BUTTON;

        } else if (isElementPresent(By.xpath(String.format(ANSWERS_TEXT_XPATH, question)))) {
            return TEXT;

        } else if (isElementPresent(By.xpath(String.format(ANSWERS_TEXT2_XPATH, question)))) {
            return TEXT2;

        } else if (isElementPresent(By.xpath(String.format(ANSWERS_TEXTBOX_XPATH, question)))) {
            return TEXTBOX;
        }
        return null;
    }

    public List<String> getDropdownOptions(String question){
        WebElement questionDropdown = driver.findElement(By.xpath(String.format(PARENT + FORM_CONTROL_DROPDOWN_XPATH, question)));
        questionDropdown.click();           // expand dropdown
        waitForAngularRequestsToFinish();

        List<String> options = getVisibleDropdownOptions();

        POHelper.clickJavascript(questionDropdown);           // collapse dropdown

        waitForAngularRequestsToFinish();
        return options;
    }

    public void tab() {
        focus.sendKeys(Keys.TAB);
        waitForAngularRequestsToFinish();
    }

}
