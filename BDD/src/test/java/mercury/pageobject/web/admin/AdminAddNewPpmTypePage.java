package mercury.pageobject.web.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class AdminAddNewPpmTypePage extends Base_Page<AdminAddNewPpmTypePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ADD_NEW_PPM_TYPE_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String ADD_NEW_PPM_TYPE_CONTENT_XPATH = ADD_NEW_PPM_TYPE_PAGE_XPATH + "//div[@class='admin-action__content']";
    private static final String ADD_NEW_PPM_TYPE_FOOTER_XPATH = ADD_NEW_PPM_TYPE_PAGE_XPATH + "//div[@class='footer-button-bar']";

    private static final String FORM_INSTRUCTIONS_XPATH = ADD_NEW_PPM_TYPE_CONTENT_XPATH + "//div[@class='form-instructions']";
    private static final String QUESTION_LABEL_XPATH = ADD_NEW_PPM_TYPE_CONTENT_XPATH + "//label[contains(text(), '%s')]/../following-sibling::div";
    private static final String QUESTION_TEXTBOX_XPATH = QUESTION_LABEL_XPATH + "/input";
    private static final String QUESTION_DROPDOWN_XPATH = QUESTION_LABEL_XPATH + "/span";
    private static final String DROPDOWN_OPTION_XPATH = "//li[contains(text(), '%s')]";

    private static final String CANCEL_BUTTON_XPATH = ADD_NEW_PPM_TYPE_FOOTER_XPATH + "//button[contains(text(), 'Cancel')]";
    private static final String SAVE_BUTTON_XPATH = ADD_NEW_PPM_TYPE_FOOTER_XPATH + "//button[@ng-click='$ctrl.save()']";

    private static final String ASSET_LEVEL_ALERT_XPATH = "//div[contains(@class, 'sweet-alert')]";
    private static final String ASSET_LEVEL_NO_XPATH = ASSET_LEVEL_ALERT_XPATH + "//button[@class='cancel']";
    private static final String ASSET_LEVEL_YES_XPATH = ASSET_LEVEL_ALERT_XPATH + "//button[@class='confirm']";


    @FindBy(xpath = FORM_INSTRUCTIONS_XPATH)
    private WebElement formInstructions;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = ASSET_LEVEL_NO_XPATH)
    private WebElement assetLevelNo;

    @FindBy(xpath = ASSET_LEVEL_YES_XPATH)
    private WebElement assetLevelYes;


    public AdminAddNewPpmTypePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ADD_NEW_PPM_TYPE_PAGE_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getFormInstructionsText() {
        return formInstructions.getText();
    }

    public boolean isQuestionTextboxDisplayed(String questionName) {
        return isElementPresent(By.xpath(String.format(QUESTION_TEXTBOX_XPATH, questionName)));
    }

    public boolean isQuestionDropdownDisplayed(String questionName) {
        return isElementPresent(By.xpath(String.format(QUESTION_DROPDOWN_XPATH, questionName)));
    }

    public void enterTextIntoField(String questionName, String text) {
        WebElement we = driver.findElement(By.xpath(String.format(QUESTION_TEXTBOX_XPATH, questionName)));
        we.clear();
        we.sendKeys(text);
    }

    public void selectOptionFromDropdown(String questionName, String dropdownOption) {
        WebElement we = waitForElement(By.xpath(String.format(QUESTION_DROPDOWN_XPATH, questionName)), State.ELEMENT_IS_CLICKABLE);
        we.click();
        waitForAngularRequestsToFinish();
        WebElement option = driver.findElement(By.xpath(String.format(DROPDOWN_OPTION_XPATH, dropdownOption)));
        option.click();
    }

    public void save() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public AdminPpmTypeAssetPage ppmTypeAtAssetLevel() {
        assetLevelYes.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, AdminPpmTypeAssetPage.class).get();
    }

    public void ppmTypeNotAtAssetLevel() {
        assetLevelNo.click();
        waitForAngularRequestsToFinish();
    }

}
