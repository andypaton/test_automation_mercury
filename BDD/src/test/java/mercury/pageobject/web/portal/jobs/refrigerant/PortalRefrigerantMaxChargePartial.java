package mercury.pageobject.web.portal.jobs.refrigerant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.pageHelpers.QuestionHelper;

public class PortalRefrigerantMaxChargePartial extends Base_Page<PortalRefrigerantMaxChargePartial> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "US Gas Regulations Refrigernat Source Questions Partial";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";

    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@id='resourceAssignmentUpdateUIRoot']";

    private static final String UPDATE_JOB_FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form[@id='updateJobForm']";
    private static final String US_GAS_REGULATIONS_XPATH = UPDATE_JOB_FORM_XPATH + "//us-gas-regulations";
    private static final String US_GAS_QUESTIONS_FORM_XPATH = US_GAS_REGULATIONS_XPATH + "//div[contains(@ng-form,'ctrl.refrigerantInstalledForm')]";//"//us-gas-refrigerant-installed-questions";

    private static final String ADD_REFRIGERANT_INSTALLED_XPATH = US_GAS_QUESTIONS_FORM_XPATH + "//div[contains(@class, 'btn-primary')]//i[contains(@class, 'icons__plus')]";

    @FindBy(xpath = ADD_REFRIGERANT_INSTALLED_XPATH)
    private WebElement addRefrigerantInstalled;

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(US_GAS_QUESTIONS_FORM_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public String getPageTitle() {
        return super.getPageTitle();
    }

    public PortalRefrigerantMaxChargePartial(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public QuestionHelper getQuestionHelper() {
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(US_GAS_QUESTIONS_FORM_XPATH);
        return questionHelper;
    }

}
