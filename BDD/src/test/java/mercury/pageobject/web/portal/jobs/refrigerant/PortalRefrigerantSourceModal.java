package mercury.pageobject.web.portal.jobs.refrigerant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class PortalRefrigerantSourceModal extends Base_Page<PortalRefrigerantSourceModal> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Refrigerant Source Modal";

    private static final String PAGE_BODY_XPATH = "//body[contains(@class, 'modal-open')]";
    private static final String PAGE_MODAL_XPATH = PAGE_BODY_XPATH + "//div[contains(@class, 'refrigerant-source__modal')]//div[contains(@class, 'modal-dialog')]//div[contains(@class, 'modal-content')]";
    private static final String PAGE_MODAL_BODY_XPATH = PAGE_MODAL_XPATH + "//div[contains(@class, 'modal-body')]";
    private static final String PAGE_MODAL_FOOTER_XPATH = PAGE_MODAL_XPATH + "//div[contains(@class, 'modal-footer')]";

    private static final String REFRIGERANT_FORM_XPATH = PAGE_MODAL_BODY_XPATH + "//form[@name='refrigerantForm']";

    private static final String REFRIGERANT_SOURCE_TOTAL_XPATH = REFRIGERANT_FORM_XPATH + "//label[contains(text(),'Total (lbs)')]";

    private static final String ADD_REFRIGERANT_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(@class,'btn-primary')]";
    private static final String CANCEL_REFRIGERANT_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(text(),'Cancel')]";

    @FindBy(xpath = PAGE_MODAL_BODY_XPATH)
    private WebElement modalBody;

    @FindBy(xpath = ADD_REFRIGERANT_SOURCE)
    private WebElement addSource;

    @FindBy(xpath = CANCEL_REFRIGERANT_SOURCE)
    private WebElement cancel;

    @FindBy(xpath = REFRIGERANT_SOURCE_TOTAL_XPATH)
    private WebElement total;

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(REFRIGERANT_FORM_XPATH));
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

    public PortalRefrigerantSourceModal(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public boolean isDisplayed() {
        return modalBody.isDisplayed();
    }

    public String getFormXpath() {
        return REFRIGERANT_FORM_XPATH;
    }

    public void addSource() {
        addSource.click();
        waitForAngularRequestsToFinish();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void save() {
        addSource.click();
        waitForAngularRequestsToFinish();
    }

    public QuestionHelper getQuestionHelper() {
        waitForAngularRequestsToFinish();
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(REFRIGERANT_FORM_XPATH);
        return questionHelper;
    }

    public String getTotal() {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(total.getText());
        if (m.find()) {
            return m.group();
        } else {
            return null;
        }
    }

}
