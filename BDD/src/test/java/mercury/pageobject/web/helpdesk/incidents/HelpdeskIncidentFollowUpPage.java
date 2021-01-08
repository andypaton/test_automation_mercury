package mercury.pageobject.web.helpdesk.incidents;

import static mercury.helpers.POHelper.scrollTo;
import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskIncidentFollowUpPage extends Base_Page<HelpdeskIncidentFollowUpPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String INCIDENT_FOLLOW_UP_PAGE_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-incident-follow-up";
    private static final String INCIDENT_FOLLOWUP_QUESTION = INCIDENT_FOLLOW_UP_PAGE_XPATH + "//div[@class='incident-questions__row']//label[contains(text(), '%s')]/following-sibling::span[@class='incident-questions__input-container']//select";
    private static final String UPDATE_NOTES_XPATH = INCIDENT_FOLLOW_UP_PAGE_XPATH + "//label[contains(text(), 'Update Note')]/following-sibling::textarea[@id='updateNote']";

    //Follow Up Questions
    private static final String INCIDENT_FOLLOWUP_QUESTIONS_XPATH = INCIDENT_FOLLOW_UP_PAGE_XPATH + "//div[@class='incident-questions__row']//label[@class='incident-questions__label']";
    private static final String INCIDENT_FOLLOWUP_QUESTION_XPATH = INCIDENT_FOLLOW_UP_PAGE_XPATH + "//div[@class='incident-questions__row']//label[@class='incident-questions__label' and contains(text(),'%s')]";
    private static final String INCIDENT_QUESTION_TYPE_XPATH = INCIDENT_FOLLOWUP_QUESTION_XPATH + "/..//*[@question='vmIncidentQuestionsActive.question']";
    private static final String INCIDENT_QUESTION_DROPDOWN_XPATH = INCIDENT_FOLLOWUP_QUESTION_XPATH + "/following-sibling::span[@class='incident-questions__input-container']//select";
    private static final String INCIDENT_QUESTION_CALENDAR_INPUT_XPATH = INCIDENT_FOLLOWUP_QUESTION_XPATH + "/following-sibling::span//input";
    private static final String INCIDENT_QUESTION_INPUT_TEXT_XPATH = INCIDENT_FOLLOWUP_QUESTION_XPATH + "/..//input[@type='text']";

    //Button
    private static final String UPDATE_FOLLOW_UP_BUTTON = INCIDENT_FOLLOW_UP_PAGE_XPATH + "//div[@class='footer-button-bar']//button[contains(@class,'btn-primary') and text()='Update Follow Up']";

    @FindBy(xpath = INCIDENT_FOLLOW_UP_PAGE_XPATH)
    private WebElement incidentFollowUpPage;

    @FindBy(xpath = UPDATE_NOTES_XPATH)
    private WebElement updateNotes;

    @FindBy(xpath = UPDATE_FOLLOW_UP_BUTTON)
    private WebElement updatefollowUpButton;

    public HelpdeskIncidentFollowUpPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", incidentFollowUpPage.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectQuestionAnswer(String question, String answer) {
        WebElement dropdown = waitForElement(By.xpath(String.format(INCIDENT_FOLLOWUP_QUESTION,question)),State.ELEMENT_IS_CLICKABLE);
        dropdown.click();
        selectOptionFromSelect(dropdown, answer);
    }

    public void enterUpdateNotes(String notes) {
        waitForAngularRequestsToFinish();
        updateNotes.sendKeys(notes);
        updateNotes.sendKeys(Keys.RETURN);
    }

    public HelpdeskIncidentEscalation clickUpdateFollowUpButton() {
        updatefollowUpButton.click();
        return PageFactory.initElements(driver, HelpdeskIncidentEscalation.class).get();
    }

    public List<String> getIncidentQuestions() {
        waitForAngularRequestsToFinish();
        List<String> questions = new ArrayList<>();
        if (isElementPresent(By.xpath(INCIDENT_FOLLOWUP_QUESTIONS_XPATH))) {
            for (WebElement jobQuestion : driver.findElements(By.xpath(INCIDENT_FOLLOWUP_QUESTIONS_XPATH))) {
                questions.add(jobQuestion.getText());
            }
        }
        return questions;
    }

    public String getIncidentQuestionTagName(String question) {
        WebElement element = driver.findElement(By.xpath(String.format(INCIDENT_QUESTION_TYPE_XPATH, question)));
        return element.getTagName();
    }

    public String questionSelectRandomOption(String question) {
        WebElement dropdown = waitForElement(By.xpath(String.format(INCIDENT_QUESTION_DROPDOWN_XPATH, question)), ELEMENT_IS_CLICKABLE);
        int pos = Integer.valueOf(dropdown.getLocation().getY());
        scrollTo(pos);
        dropdown.click();
        String selection = selectRandomOptionFromSelect(dropdown);
        return selection;
    }

    public void questionEnterText(String question, String text) {
        WebElement jobQuestion = waitForElement(By.xpath(String.format(INCIDENT_QUESTION_INPUT_TEXT_XPATH, question)), ELEMENT_IS_VISIBLE);
        jobQuestion.clear();
        jobQuestion.sendKeys(text);
    }

    public void enterDateTime(String question, String dateTime) {
        WebElement cal = waitForElement(By.xpath(String.format(INCIDENT_QUESTION_CALENDAR_INPUT_XPATH, question)), State.ELEMENT_IS_VISIBLE);
        cal.clear();
        cal.sendKeys(dateTime);
    }

    public void selectAnswer(String question, String answer) {
        waitForAngularRequestsToFinish();
        WebElement dropdown = waitForElement(By.xpath(String.format(INCIDENT_QUESTION_DROPDOWN_XPATH, question)), ELEMENT_IS_CLICKABLE);
        dropdown.click();
        selectOptionFromSelect(dropdown,answer);
    }

}
