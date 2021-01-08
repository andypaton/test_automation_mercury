package mercury.pageobject.web.helpdesk.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskQuestionsTab extends Base_Page<HelpdeskQuestionsTab> {

    private static final Logger logger = LogManager.getLogger();

    private static String ACTIVE_TAB_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static String QUESTIONS_TAB_XPATH = ACTIVE_TAB_XPATH + "//div[@class='job-detail__questions']";

    private static final String QUESTIONS_XPATH = ACTIVE_TAB_XPATH + "//div[@class='job-detail__questions-label' and contains(text(), '%s')]";

    private static final String ANSWERS_XPATH = QUESTIONS_XPATH + "/..//span[@ng-if='::question.answer']";

    public HelpdeskQuestionsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(QUESTIONS_TAB_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getQuestion(String question) {
        waitForAngularRequestsToFinish();
        WebElement questions = waitForElement(By.xpath(String.format(QUESTIONS_XPATH, question)), State.ELEMENT_IS_VISIBLE);
        return questions.getText();
    }

    public String getAnswer(String question) {
        WebElement answer = waitForElement(By.xpath(String.format(ANSWERS_XPATH, question)), State.ELEMENT_IS_VISIBLE);
        return answer.getText();

    }

}
