package mercury.pageobject.web.helpdesk.caller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.pageobject.web.Base_Page;

public class EditCallerPage extends Base_Page<EditCallerPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_CSS = "#main-panel > div.tab-content > div.tab-pane.active";
    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String EDIT_CALLER_FORM_CSS = ACTIVE_WORKSPACE_CSS + " ph-manage-caller-modal[is-edit-caller='true']";
    private static final String EDIT_CALLER_FORM_XPATH = ACTIVE_WORKSPACE_XPATH + "//ph-manage-caller-modal[@is-edit-caller='true']";

    // FIELDS
    private static final String TITLE_CSS = EDIT_CALLER_FORM_CSS + " .modal-title";
    private static final String NAME_CSS = EDIT_CALLER_FORM_CSS + " #CallerName";
    private static final String JOB_ROLE_CSS = EDIT_CALLER_FORM_CSS + " input[name='role']";
    private static final String DEPARTMENT_CSS = EDIT_CALLER_FORM_CSS + " input[name='department']";
    private static final String TELEPHONE_CSS = EDIT_CALLER_FORM_CSS + " input[name='telephone']";
    private static final String EXTENSION_CSS = EDIT_CALLER_FORM_CSS + " input[name='extension']";

    // BUTTONS
    private static final String CANCEL_XPATH = EDIT_CALLER_FORM_XPATH + "//button/span[contains(text(), 'Cancel')]";
    private static final String SAVE_XPATH = EDIT_CALLER_FORM_XPATH + "//button/span[contains(text(), 'Save and identify as caller')]";


    @FindBy(css=EDIT_CALLER_FORM_CSS)
    private static WebElement editCaller;

    @FindBy(css=TITLE_CSS)
    private static WebElement title;

    @FindBy(css=NAME_CSS)
    private static WebElement name;

    @FindBy(css=JOB_ROLE_CSS)
    private static WebElement jobRole;

    @FindBy(css=DEPARTMENT_CSS)
    private static WebElement department;

    @FindBy(css=TELEPHONE_CSS)
    private static WebElement telephone;

    @FindBy(css=EXTENSION_CSS)
    private static WebElement extension;

    @FindBy(xpath=CANCEL_XPATH)
    private static WebElement cancel;

    @FindBy(xpath=SAVE_XPATH)
    private static WebElement save;

    public EditCallerPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", editCaller.isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getTitle() {
        return title.getText();
    }

    public void enterName(String text) {
        name.sendKeys(text);
    }

    public void enterJobRole(String text) {
        jobRole.sendKeys(text);
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

    public void save() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

}
