package mercury.pageobject.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class AdminEditIncidentCriteriaPage extends Base_Page<AdminEditIncidentCriteriaPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String INCIDENT_CRITERIA_PAGE_XPATH = "//div[@class='admin-action']";
    private static final String INCIDENT_CRITERIA_CONTENT_XPATH = INCIDENT_CRITERIA_PAGE_XPATH + "//div[@class='admin-action__content']";

    private static final String DROPDOWN_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//label[contains(text(),'%s')]/following::select";

    private static final String ACTIVE_INACTIVE_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//label[contains(text(), '%s')]";
    private static final String SAVE_BUTTON_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//button[@type='submit']";
    private static final String CANCEL_BUTTON_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//button[contains(text(), 'Cancel')]";

    private static final String AUDIT_HISTORY_EXPAND_EXPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//i[contains(@class, 'fa-plus-circle')]";
    private static final String AUDIT_UPDATED_ON_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//tbody[contains(@ng-repeat, 'ctrl.auditItems')]//td[@class='ng-binding'][1]";
    private static final String AUDIT_UPDATED_BY_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//tbody[contains(@ng-repeat, 'ctrl.auditItems')]//td[@class='ng-binding'][2]";
    private static final String AUDIT_DESCRIPTION_XPATH = INCIDENT_CRITERIA_CONTENT_XPATH + "//tbody[contains(@ng-repeat, 'ctrl.auditItems')]//td/span";

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    @FindBy(xpath = AUDIT_HISTORY_EXPAND_EXPATH)
    private WebElement auditHistoryExpand;

    @FindBy(xpath = AUDIT_UPDATED_ON_XPATH)
    private WebElement updatedOn;

    @FindBy(xpath = AUDIT_UPDATED_BY_XPATH)
    private WebElement updatedBy;


    public AdminEditIncidentCriteriaPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INCIDENT_CRITERIA_PAGE_XPATH));
            logger.info("Page loaded");
        } catch (Exception e){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectDropdownOption(String dropdownName, String option) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)));
        selectOptionFromSelect(dropdown, option);
        waitForAngularRequestsToFinish();
        dropdown.sendKeys(Keys.TAB); //Added as error messages do not show for all fields without this
    }

    public String selectRandomDropdownOption(String dropdownName) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)));
        String option = selectRandomOptionFromSelect(dropdown);
        waitForAngularRequestsToFinish();
        dropdown.sendKeys(Keys.TAB);
        return option;
    }

    public void selectActiveOrInactive(String isCriteriaActive) {
        WebElement activeInactive = driver.findElement(By.xpath(String.format(ACTIVE_INACTIVE_XPATH, isCriteriaActive)));
        activeInactive.click();
        waitForAngularRequestsToFinish();
    }

    public void save() {
        POHelper.scrollToElement(save);
        save.click();
        waitForAngularRequestsToFinish();
    }

    public void cancel() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void expandAuditHistory() {
        auditHistoryExpand.click();
        waitForAngularRequestsToFinish();
    }

    public String getUpdatedOn() {
        return updatedOn.getText();
    }

    public String getUpdatedBy() {
        return updatedBy.getText();
    }

    public List<String> getDescriptions() {
        List<String> descriptions = new ArrayList<>();
        List<WebElement> descriptionValues = driver.findElements(By.xpath(AUDIT_DESCRIPTION_XPATH));
        for (WebElement description : descriptionValues) {
            descriptions.add(description.getAttribute("innerText"));
        }
        return descriptions;
    }
}
