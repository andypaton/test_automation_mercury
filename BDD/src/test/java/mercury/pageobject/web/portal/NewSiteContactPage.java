package mercury.pageobject.web.portal;

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
import mercury.pageobject.web.portal.jobs.PortalLogAJobPage;

public class NewSiteContactPage extends Base_Page<NewSiteContactPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "New Site Contact";

    private static final String ACTIVE_WORKSPACE_CSS = "div.modal-panel div.modal-content";
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[@class='modal-content']";

    private static final String NEW_SITE_CONTACT_FORM_CSS = ACTIVE_WORKSPACE_CSS + " #newCallerForm";
    private static final String NEW_SITE_CONTACT_FORM_XPATH = ACTIVE_WORKSPACE_XPATH + "//*[@id='newCallerForm']";

    // FIELDS
    private static final String NAME_CSS = NEW_SITE_CONTACT_FORM_CSS + " input[name='name']";
    private static final String JOB_TITLE_CSS = NEW_SITE_CONTACT_FORM_CSS + " input[name='jobTitle']";
    private static final String SITE_CSS = NEW_SITE_CONTACT_FORM_CSS + " select[name='site']";
    private static final String DEPARTMENT_CSS = NEW_SITE_CONTACT_FORM_CSS + " input[name='department']";
    private static final String TELEPHONE_CSS = NEW_SITE_CONTACT_FORM_CSS + " input[name='phoneNumber']";
    private static final String EXTENSION_CSS = NEW_SITE_CONTACT_FORM_CSS + " input[name='extension']";

    // BUTTONS
    private static final String CANCEL_XPATH = NEW_SITE_CONTACT_FORM_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String SAVE_XPATH = NEW_SITE_CONTACT_FORM_XPATH + "//button[contains(text(), 'Add')]";


    @FindBy(css = NEW_SITE_CONTACT_FORM_CSS)
    private static WebElement newContact;

    @FindBy(css = NAME_CSS)
    private static WebElement name;

    @FindBy(css = JOB_TITLE_CSS)
    private static WebElement jobTitle;

    @FindBy(css = SITE_CSS)
    private static WebElement site;

    @FindBy(css = DEPARTMENT_CSS)
    private static WebElement department;

    @FindBy(css=  TELEPHONE_CSS)
    private static WebElement telephone;

    @FindBy(css = EXTENSION_CSS)
    private static WebElement extension;

    @FindBy(xpath = CANCEL_XPATH)
    private static WebElement cancel;

    @FindBy(xpath = SAVE_XPATH)
    private static WebElement save;


    public NewSiteContactPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(NEW_SITE_CONTACT_FORM_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }


    public void enterName(String text) {
        name.sendKeys(text);
    }

    public void enterJobTitle(String text) {
        jobTitle.sendKeys(text);
    }

    public void enterDepartment(String text) {
        department.sendKeys(text);
    }

    public void enterTelephone(String text) {
        telephone.sendKeys(text);
    }

    public void enterExtension(String text) {
        extension.sendKeys(text);
    }

    public PortalLogAJobPage save() throws InterruptedException {
        save.click();
        waitUntilElementCountToBe(By.cssSelector(ACTIVE_WORKSPACE_CSS), 0);
        return PageFactory.initElements(driver, PortalLogAJobPage.class).get();
    }

    public PortalLogAJobPage cancel() {
        cancel.click();
        return PageFactory.initElements(driver, PortalLogAJobPage.class).get();
    }

}
