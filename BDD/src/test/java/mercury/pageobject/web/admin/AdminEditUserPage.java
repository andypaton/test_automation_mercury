package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditUserPage extends Base_Page<AdminEditUserPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_USER_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String EDIT_USER_CONTENT_XPATH = EDIT_USER_PAGE_XPATH + "//div[@class='admin-action__sub-content']";
    private static final String EDIT_USER_FOOTER_XPATH = EDIT_USER_PAGE_XPATH + "//div[@class='footer-button-bar']";

    private static final String ACTIVE_YES_XPATH = EDIT_USER_CONTENT_XPATH + "//label[@for='active_Yes']";
    private static final String ACTIVE_NO_XPATH = EDIT_USER_CONTENT_XPATH + "//label[@for='active_No']";

    private static final String CANCEL_CHANGES_XPATH = EDIT_USER_FOOTER_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String SAVE_CHANGES_XPATH = EDIT_USER_FOOTER_XPATH + "//span[contains(text(), 'Save')]/..";


    @FindBy(xpath = ACTIVE_YES_XPATH)
    private WebElement activeYes;

    @FindBy(xpath = ACTIVE_NO_XPATH)
    private WebElement activeNo;

    @FindBy(xpath = CANCEL_CHANGES_XPATH)
    private WebElement cancelChanges;

    @FindBy(xpath = SAVE_CHANGES_XPATH)
    private WebElement saveChanges;


    public AdminEditUserPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(EDIT_USER_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void makeUserActive() {
        activeYes.click();
        waitForAngularRequestsToFinish();
    }

    public void makeUserInactive() {
        activeNo.click();
        waitForAngularRequestsToFinish();
    }

    public void cancelChanges() {
        cancelChanges.click();
        waitForAngularRequestsToFinish();
    }

    public void saveChanges() {
        saveChanges.click();
        waitForAngularRequestsToFinish();
    }

}
