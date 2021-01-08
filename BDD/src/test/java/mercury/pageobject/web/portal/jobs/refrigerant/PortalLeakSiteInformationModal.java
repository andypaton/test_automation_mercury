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

public class PortalLeakSiteInformationModal extends Base_Page<PortalLeakSiteInformationModal> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Leak Site Information";

    private static final String PAGE_BODY_XPATH = "//body[contains(@class, 'modal-open')]";
    private static final String PAGE_MODAL_XPATH = PAGE_BODY_XPATH + "//div[contains(@class, 'gas-leak-site__modal')]//div[contains(@class, 'modal-dialog')]//div[contains(@class, 'modal-content')]";
    private static final String PAGE_MODAL_BODY_XPATH = PAGE_MODAL_XPATH + "//div[contains(@class, 'modal-body')]";
    private static final String PAGE_MODAL_FOOTER_XPATH = PAGE_MODAL_XPATH + "//div[contains(@class, 'modal-footer')]";

    private static final String REFRIGERNAT_FORM_XPATH = PAGE_MODAL_BODY_XPATH + "//form[@name='leakSiteInformationForm']";

    private static final String ADD_LEAK_SITE_INFORMATION_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(@class,'btn-primary')]";
    private static final String CANCEL_ADD_LEAK_SITE_INFORMATION_SOURCE = PAGE_MODAL_FOOTER_XPATH + "//button[contains(@class,'btn-destructive')]";

    @FindBy(xpath = ADD_LEAK_SITE_INFORMATION_SOURCE)
    private WebElement addLeakSite;

    @FindBy(xpath = CANCEL_ADD_LEAK_SITE_INFORMATION_SOURCE)
    private WebElement cancelAdd;

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

    public PortalLeakSiteInformationModal(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public boolean isPopupVisible() {
        this.waitForAngularRequestsToFinish();
        return this.isElementClickable(By.xpath(REFRIGERNAT_FORM_XPATH));
    }

    public String getFormXpath() {
        return REFRIGERNAT_FORM_XPATH;
    }

    public void addLeakSiteInformation() {
        addLeakSite.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isAddButtonDisplayed() {
        return isElementVisible(By.xpath(ADD_LEAK_SITE_INFORMATION_SOURCE));
    }

    public void cancelSavingForm() {
        cancelAdd.click();
        waitForAngularRequestsToFinish();
    }

    public QuestionHelper getQuestionHelper() {
        QuestionHelper questionHelper = PageFactory.initElements(driver, QuestionHelper.class);
        questionHelper.load(REFRIGERNAT_FORM_XPATH);
        return questionHelper;
    }

}
