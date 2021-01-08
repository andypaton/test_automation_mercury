package mercury.pageobject.web.portal;

import static mercury.helpers.Constants.MAX_ATTEMPTS;
import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class JobQuestions extends Base_Page<JobQuestions> {

    private static final Logger logger = LogManager.getLogger();

    private static final String JOB_QUESTIONS_HEADER_XPATH = "//h4[contains(text(), 'Job Questions')]";
    private static final String JOB_QUESTIONS_XPATH =  JOB_QUESTIONS_HEADER_XPATH + "/..//label[contains(@ng-class, 'question.isRequired')]";
    private static final String JOB_QUESTION_XPATH =  JOB_QUESTIONS_HEADER_XPATH + "/..//label[contains(@ng-class, 'question.isRequired') and contains(text(),'%s')]";
    private static final String JOB_QUESTION_TYPE_XPATH = JOB_QUESTION_XPATH + "/../child::*[3]";
    private static final String JOB_QUESTION_YES_XPATH = JOB_QUESTION_XPATH + "/..//label[contains(@for, 'Yes')]";
    private static final String JOB_QUESTION_NO_XPATH = JOB_QUESTION_XPATH + "/..//label[contains(@for, 'No')]";
    private static final String JOB_QUESTION_INPUT_XPATH = JOB_QUESTION_XPATH + "/..//input[contains(@ng-if, 'ctrl.questionTypes')]";
    private static final String JOB_QUESTION_NONDECIMAL_XPATH = JOB_QUESTION_XPATH + "/..//span/input";
    private static final String JOB_QUESTION_MULTIPLELINETEXT_XPATH = JOB_QUESTION_XPATH + "/..//textarea";
    private static final String JOB_QUESTION_DROPDOWN_XPATH = JOB_QUESTION_XPATH + "/..//select";
    private static final String JOB_QUESTION_CALENDAR_XPATH = JOB_QUESTION_XPATH + "/.." + DROPDOWN_CALENDAR_XPATH;
    private static final String JOB_QUESTION_CALENDAR_OPTION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]//a[@title='%s']";
    private static final String JOB_QUESTION_CLOCK_XPATH = JOB_QUESTION_XPATH + "/.." + DROPDOWN_CLOCK_XPATH;


    public JobQuestions(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(JOB_QUESTIONS_HEADER_XPATH + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(JOB_QUESTIONS_HEADER_XPATH));
            logger.info(JOB_QUESTIONS_HEADER_XPATH + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(JOB_QUESTIONS_HEADER_XPATH + " isloaded error");
            throw new AssertionError();
        }
    }

    public Boolean isPageLoaded() {
        return this.isElementVisible(By.xpath(JOB_QUESTIONS_HEADER_XPATH));
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

    public String getJobQuestionAttribute(String question, String attributeValue) {
        WebElement element = driver.findElement(By.xpath(String.format(JOB_QUESTION_TYPE_XPATH, question)));
        return element.getAttribute(attributeValue);
    }


    public void jobQuestionSelectYes(String question) {
        WebElement yes = waitForElement(By.xpath(String.format(JOB_QUESTION_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        yes.click();
        waitForAngularRequestsToFinish();
    }

    public void jobQuestionSelectNo(String question) {
        WebElement no = waitForElement(By.xpath(String.format(JOB_QUESTION_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        POHelper.scrollToElement(no);
        no.click();
        waitForAngularRequestsToFinish();
    }

    public void jobQuestionEnterText(String question, String text) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_INPUT_XPATH, question)), ELEMENT_IS_VISIBLE);
        jobQuestion.sendKeys(text);
    }

    public void jobQuestionEnterMultipleLineText(String question, String text) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_MULTIPLELINETEXT_XPATH, question)), ELEMENT_IS_VISIBLE);
        jobQuestion.sendKeys(text);
    }

    public void jobQuestionEnterNonDecimal(String question, int text) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_NONDECIMAL_XPATH, question)), ELEMENT_IS_VISIBLE);
        jobQuestion.sendKeys(Integer.toString(text));
    }

    private String jobQuestionSelectRandomOption(String question, int attempt) {
        String selection = "";
        try {
            WebElement jobQuestion = waitForElement(By.xpath(String.format(JOB_QUESTION_DROPDOWN_XPATH, question)), ELEMENT_IS_CLICKABLE);
            selection = selectRandomOptionFromSelect(jobQuestion);
        } catch (StaleElementReferenceException e) {
            if (attempt < MAX_ATTEMPTS) return jobQuestionSelectRandomOption(question, attempt + 1);
        }
        return selection;
    }

    public String jobQuestionSelectRandomOption(String question) {
        return jobQuestionSelectRandomOption(question, 0);
    }

    public void jobQuestionSelectRandomDate(String question, String date) {
        WebElement jobQuestionCalendarIcon = waitForElement(By.xpath(String.format(JOB_QUESTION_CALENDAR_XPATH, question)), ELEMENT_IS_CLICKABLE);
        jobQuestionCalendarIcon.click();
        waitForAngularRequestsToFinish();
        POHelper.clickJavascript(jobQuestionCalendarIcon);
        WebElement jobQuestionCalendarOption =  waitForElement(By.xpath(String.format(JOB_QUESTION_CALENDAR_OPTION_XPATH, date)), ELEMENT_IS_CLICKABLE);
        jobQuestionCalendarOption.click();
        waitForAngularRequestsToFinish();
    }

    public String jobQuestionSelectRandomTime(String question) {
        WebElement jobQuestionClockIcon = waitForElement(By.xpath(String.format(JOB_QUESTION_CLOCK_XPATH, question)), ELEMENT_IS_CLICKABLE);
        jobQuestionClockIcon.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

}
