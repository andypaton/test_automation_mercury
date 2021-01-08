package mercury.pageobject.web.admin.refrigerantGasUsage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.pageHelpers.QuestionHelper;

public class AdminLeakSiteInformationModal extends Base_Page<AdminLeakSiteInformationModal> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Refrigerant Source Modal";

    private static final String PAGE_BODY_XPATH = "//body[contains(@class, 'modal-open')]";
    private static final String PAGE_MODAL_XPATH = PAGE_BODY_XPATH + "//div[contains(@class, 'gas-leak-site__modal')]//div[contains(@class, 'modal-dialog')]//div[contains(@class, 'modal-content')]";
    private static final String PAGE_MODAL_BODY_XPATH = PAGE_MODAL_XPATH + "//div[contains(@class, 'modal-body')]";
    private static final String PAGE_MODAL_FOOTER_XPATH = PAGE_MODAL_XPATH + "//div[contains(@class, 'modal-footer')]";

    private static final String REFRIGERNAT_FORM_XPATH = PAGE_MODAL_BODY_XPATH + "//form[@name='leakSiteInformationForm']";

    private static final String ADD_REFRIGERANT_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Add')]";
    private static final String SAVE_REFRIGERANT_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Save')]";
    private static final String CANCEL_REFRIGERANT_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Cancel')]";

    @FindBy(xpath = ADD_REFRIGERANT_SOURCE)
    private WebElement add;

    @FindBy(xpath = SAVE_REFRIGERANT_SOURCE)
    private WebElement save;

    @FindBy(xpath = CANCEL_REFRIGERANT_SOURCE)
    private WebElement cancel;


    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(REFRIGERNAT_FORM_XPATH));
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

    public AdminLeakSiteInformationModal(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public boolean isPopupVisible() {
        return this.isElementClickable(By.xpath(REFRIGERNAT_FORM_XPATH));
    }

    public String getFormXpath() {
        return REFRIGERNAT_FORM_XPATH;
    }

    public void add() {
        add.click();
        waitForAngularRequestsToFinish();
    }

    public void save() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public QuestionHelper getQuestionHelper() {
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(REFRIGERNAT_FORM_XPATH);
        return questionHelper;
    }

    public void takeScreenshot(OutputHelper outputHelper) throws Exception {
        outputHelper.takeScreenshot(PAGE_BODY_XPATH);
    }
}
