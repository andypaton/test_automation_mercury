package mercury.pageobject.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminSystemFeatureTogglePage extends Base_Page<AdminSystemFeatureTogglePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String SYSTEM_FEATURE_TOGGLE_FORM_XPATH = "//mc-system-feature-toggle-form";

    private static final String FEATURE_TOGGLE = SYSTEM_FEATURE_TOGGLE_FORM_XPATH + "//h2[contains(@class, 'admin-action__section-header') and text() = '%s']/..//following-sibling::div//input[@type = 'checkbox']";
    private static final String SUB_FEATURE_TOGGLE = SYSTEM_FEATURE_TOGGLE_FORM_XPATH + "//div[contains(@class, 'feature-toggle__sub-feature') and contains(@class, 'col-md-5')]/label[text() = '%s']/..//following-sibling::div//input[@type = 'checkbox']";

    private static final String SUB_FEATURES_XPATH = SYSTEM_FEATURE_TOGGLE_FORM_XPATH + "//h2[contains(@class, 'admin-action__section-header') and text() = '%s']/ancestor::div[contains(@class, 'feature-toggle__feature')]//div[contains(@class, 'feature-toggle__sub-feature') and contains(@class, 'col-md-5')]";
    private static final String PARENT_TOGGLE_XPATH = SYSTEM_FEATURE_TOGGLE_FORM_XPATH + "//div[contains(@class, 'feature-toggle__sub-feature') and contains(@class, 'col-md-5')]/label[text() = '%s']/ancestor::div[contains(@class, 'feature-toggle__feature')]//h2";

    private static final String SAVE_XPATH = SYSTEM_FEATURE_TOGGLE_FORM_XPATH + "//button/span[text() = 'Save']";
    private static final String CANCEL_XPATH = SYSTEM_FEATURE_TOGGLE_FORM_XPATH + "//div[contains(@class, ' footer-button-bar__buttons-container')]/a[text() = 'Cancel']";


    @FindBy(xpath = SAVE_XPATH)
    private WebElement save;

    @FindBy(xpath = CANCEL_XPATH)
    private WebElement cancel;


    public AdminSystemFeatureTogglePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(SYSTEM_FEATURE_TOGGLE_FORM_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void toggleOnFeature(String feature) {
        WebElement checkbox = driver.findElement(By.xpath(String.format(FEATURE_TOGGLE, feature)));
        if ( !checkbox.isSelected() ) {
            WebElement we = driver.findElement(By.xpath(String.format(FEATURE_TOGGLE + "/..", feature)));
            POHelper.scrollToElement(we);
            we.click();
            waitForAngularRequestsToFinish();
        }
    }

    public void toggleOffFeature(String feature) {
        WebElement checkbox = driver.findElement(By.xpath(String.format(FEATURE_TOGGLE, feature)));
        if ( checkbox.isSelected() ) {
            WebElement we = driver.findElement(By.xpath(String.format(FEATURE_TOGGLE + "/..", feature)));
            POHelper.scrollToElement(we);
            we.click();
            waitForAngularRequestsToFinish();
        }
    }

    public void toggleOnSubFeature(String subFeature) {
        WebElement checkbox = driver.findElement(By.xpath(String.format(SUB_FEATURE_TOGGLE, subFeature)));
        if ( !checkbox.isSelected() ) {
            WebElement we = driver.findElement(By.xpath(String.format(SUB_FEATURE_TOGGLE + "/..", subFeature)));
            POHelper.scrollToElement(we);
            we.click();
            waitForAngularRequestsToFinish();
        }
    }

    public void toggleOffSubFeature(String subFeature) {
        WebElement checkbox = driver.findElement(By.xpath(String.format(SUB_FEATURE_TOGGLE, subFeature)));
        if ( checkbox.isSelected() ) {
            WebElement we = driver.findElement(By.xpath(String.format(SUB_FEATURE_TOGGLE + "/..", subFeature)));
            POHelper.scrollToElement(we);
            we.click();
            waitForAngularRequestsToFinish();
        }
    }

    public Boolean isToggleOnFeature(String feature) {
        WebElement checkbox = driver.findElement(By.xpath(String.format(FEATURE_TOGGLE, feature)));
        return checkbox.isSelected();
    }

    public Boolean isToggleOnSubFeature(String subFeature) {
        WebElement checkbox = driver.findElement(By.xpath(String.format(SUB_FEATURE_TOGGLE, subFeature)));
        return checkbox.isSelected();
    }

    public List<String> getSubFeatures(String feature) {
        List<String> subFeatures = new ArrayList<>();
        if ( isElementPresent(By.xpath(String.format(SUB_FEATURES_XPATH, feature))) ) {
            for (WebElement we : driver.findElements(By.xpath(String.format(SUB_FEATURES_XPATH, feature)))) {
                subFeatures.add(we.getText());
            }
        }
        return subFeatures;
    }

    public String getParentToggle(String subFeature) {
        if ( isElementPresent(By.xpath(String.format(PARENT_TOGGLE_XPATH, subFeature))) ) {
            return driver.findElement(By.xpath(String.format(PARENT_TOGGLE_XPATH, subFeature))).getText();
        }
        return null;
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
