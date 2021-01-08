package mercury.pageobject.web.helpdesk.incidents;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskIncidentAnswersTab extends Base_Page<HelpdeskIncidentAnswersTab> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String ANSWERS_TAB_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-incident-answers-tab";
    private static final String QUESTION_AND_ANSWERS_XPATH = ANSWERS_TAB_XPATH + "//span";


    @FindBy(xpath = ANSWERS_TAB_XPATH)
    private WebElement answersTab;

    @FindBy(xpath = QUESTION_AND_ANSWERS_XPATH)
    private List<WebElement> incidentQuestionsAndAnswers;


    public HelpdeskIncidentAnswersTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", answersTab.isDisplayed());
            logger.info("Page loaded");
        } catch(Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public List<String> getQuestionsAndAnswers(){
        List<String> questionsAndAnswers = new ArrayList<String>();
        for (WebElement element : incidentQuestionsAndAnswers) {
            questionsAndAnswers.add(element.getAttribute("innerText"));
        }
        return questionsAndAnswers;
    }

    public String getAnswer(String question) throws Exception {
        for (String questionAndAnswer : getQuestionsAndAnswers()) {
            String[] parts = questionAndAnswer.split(" : ");
            if ((parts[0]).contains(question)) {
                return parts.length == 1 ? "" : parts[1];
            }
        }
        throw new Exception("Question not found: " + question);
    }

}
