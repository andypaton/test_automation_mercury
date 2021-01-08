package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditUserProfilePage extends Base_Page<AdminEditUserProfilePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String EDIT_RESOURCE_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String EDIT_RESOURCE_CONTENT_XPATH = EDIT_RESOURCE_PAGE_XPATH + "//div[@class='admin-action__content']";

    private static final String ACTIVE_YES_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//label[@for='active_Yes']";
    private static final String ACTIVE_NO_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//label[@for='active_No']";

    private static final String DEACTIVATE_PROFILE_MODAL_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//div[contains(@ng-show, '$ctrl.userProfile.usersWithProfile.length')]";
    private static final String ASSIGNED_RESOURCE_XPATH = DEACTIVATE_PROFILE_MODAL_XPATH + "//a[contains(text(), '%s')]";
    private static final String CHANGE_PROFILE_DROPDOWN_XPATH = DEACTIVATE_PROFILE_MODAL_XPATH + "//select[contains(@name, 'userProfile')]";
    private static final String CHANGE_PROFILE_BUTTON_XPATH = DEACTIVATE_PROFILE_MODAL_XPATH + "//span[contains(text(), 'Change Profile')]/..";

    private static final String CANCEL_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String SAVE_PROFILE_XPATH = EDIT_RESOURCE_CONTENT_XPATH + "//span[contains(text(), 'Save profile')]/..";


    @FindBy(xpath = ACTIVE_YES_XPATH)
    private WebElement activeYes;

    @FindBy(xpath = ACTIVE_NO_XPATH)
    private WebElement activeNo;

    @FindBy(xpath = DEACTIVATE_PROFILE_MODAL_XPATH)
    private WebElement deactivateProfileModal;

    @FindBy(xpath = CHANGE_PROFILE_DROPDOWN_XPATH)
    private WebElement changeUserProfileDropdown;

    @FindBy(xpath = CHANGE_PROFILE_BUTTON_XPATH)
    private WebElement changeProfileButton;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancel;

    @FindBy(xpath = SAVE_PROFILE_XPATH)
    private WebElement saveProfile;


    public AdminEditUserProfilePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(EDIT_RESOURCE_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void makeProfileActive() {
        POHelper.scrollToElement(activeYes);
        activeYes.click();
        waitForAngularRequestsToFinish();
    }

    public void makeProfileInactive() {
        POHelper.scrollToElement(activeNo);
        activeNo.click();
        waitForAngularRequestsToFinish();
    }

    public String getDeactivateProfileModalText() {
        return deactivateProfileModal.getText();
    }

    public String getAssignedResource(String resourceName) {
        WebElement assignedresource = driver.findElement(By.xpath(String.format(ASSIGNED_RESOURCE_XPATH, resourceName)));
        return assignedresource.getText();
    }

    public void selectRandomProfile() {
        selectRandomOptionFromSelect(changeUserProfileDropdown);
    }

    public void clickChangeProfile() {
        changeProfileButton.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isChangeProfileButtonEnabled() {
        return changeProfileButton.isEnabled();
    }

    public void cancelChanges() {
        POHelper.scrollToElement(cancel);
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void saveProfile() {
        POHelper.scrollToElement(saveProfile);
        saveProfile.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isSaveProfileButtonEnabled() {
        return saveProfile.isEnabled();
    }
}
