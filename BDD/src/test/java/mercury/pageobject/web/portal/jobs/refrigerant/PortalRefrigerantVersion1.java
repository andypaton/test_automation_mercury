package mercury.pageobject.web.portal.jobs.refrigerant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.pageHelpers.QuestionHelper;

/**
 * @author forbesp This is really the Refrigerant Gas for prior to the FGas
 *         work. It could be US version 1, or Global Version 1, or UK Version 1
 *         depending on who you speak to witin the City group
 *
 */
public class PortalRefrigerantVersion1 extends Base_Page<PortalRefrigerantVersion1> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "UK Gas Regulations Refrigernat Questions Partial";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";

    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[@id='resourceAssignmentUpdateUIRoot']";

    private static final String UPDATE_JOB_FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form[@id='updateJobForm']";
    private static final String UK_GAS_REGULATIONS_XPATH = UPDATE_JOB_FORM_XPATH + "//div[contains(@ng-controller, 'ukFgasRegulations')]";
    private static final String UK_GAS_QUESTIONS_FORM_XPATH = UK_GAS_REGULATIONS_XPATH;// + "//us-gas-refrigerant-source-questions";




    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(UK_GAS_QUESTIONS_FORM_XPATH));
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

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public PortalRefrigerantVersion1(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public QuestionHelper getQuestionHelper() {
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(UK_GAS_QUESTIONS_FORM_XPATH);
        return questionHelper;
    }
}
