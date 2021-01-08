package mercury.pageobject.web.helpdesk.resources;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;

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

public class HelpdeskAdditionalResourcesNotRequiredPanel extends Base_Page<HelpdeskAdditionalResourcesNotRequiredPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String RESOURCE_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'job-action-panel') and contains(@class, 'confirm-resource')]";
    private static final String RESOURCE_PANEL_CONTENT_XPATH = RESOURCE_PANEL_XPATH + "//div[contains(@class, 'job-action-panel__content')]";

    private static final String ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@aria-hidden='false']/div[@class='arr__removing-container']";

    private static final String ALERT_XPATH = ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH + "//div[@class='alert-text']";

    private static final String DETAIL_WHY_NO_ADDITIONAL_RESOURCE_REQUIRED_XPATH = ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH + "//label[contains(text(), 'Please detail why no additional resource is required')]/following-sibling::textarea";

    private static final String RADIOBUTTON_PARK_XPATH = ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH + "//div[@class='yesNoRadio']//input[@value='Park']";
    private static final String RADIOBUTTON_CANCEL_XPATH = ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH + "//div[@class='yesNoRadio']//input[@value='Cancel']";

    private static final String DROPDOWN_REASON_ARROW_XPATH = ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH + "//label[contains(text(), 'Reason')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String DROPDOWN_REASON_XPATH = ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH + "//label[contains(text(), 'Reason')]/.." + DROPDOWN_SEARCH_XPATH;

    private static final String DATE_TO_UNPARK_XPATH = ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH + "//label[contains(text(),'Date to Unpark')]/..";
    private static final String DATE_TO_UNPARK_CALENDAR_XPATH = DATE_TO_UNPARK_XPATH + DROPDOWN_CALENDAR_XPATH;
    private static final String DATE_TO_UNPARK_OPTION_XPATH = "//a[@title='%s']";
    private static final String DATE_TO_UNPARK_CLOCK_XPATH = DATE_TO_UNPARK_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String DATE_TO_UNPARK_DATE_XPATH = DATE_TO_UNPARK_XPATH + DROPDOWN_PICK_WRAPPER_XPATH + "//input";


    @FindBy(xpath = ALERT_XPATH)
    private WebElement alert;

    @FindBy(xpath = DETAIL_WHY_NO_ADDITIONAL_RESOURCE_REQUIRED_XPATH)
    private WebElement detailWhyNoAdditionalResourceRequired;

    @FindBy(xpath = RADIOBUTTON_PARK_XPATH)
    private WebElement park;

    @FindBy(xpath = RADIOBUTTON_CANCEL_XPATH)
    private WebElement cancel;

    @FindBy(xpath = DROPDOWN_REASON_ARROW_XPATH)
    private WebElement reasonDropdownArrow;

    @FindBy(xpath = DROPDOWN_REASON_XPATH)
    private WebElement reasonDropdown;

    @FindBy(xpath = DATE_TO_UNPARK_CALENDAR_XPATH)
    private WebElement dateToUnparkCalendar;

    @FindBy(xpath = DATE_TO_UNPARK_CLOCK_XPATH)
    private WebElement dateToUnparkClock;

    @FindBy(xpath = DATE_TO_UNPARK_DATE_XPATH)
    private WebElement dateToUnpark;

    public HelpdeskAdditionalResourcesNotRequiredPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ADDITIONAL_RESOURCE_REMOVING_CONTAINER_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }


    public void setDetailWhyNoAdditionalResourceRequired(String details) {
        waitForElement(By.xpath(DETAIL_WHY_NO_ADDITIONAL_RESOURCE_REQUIRED_XPATH), ELEMENT_IS_VISIBLE);
        detailWhyNoAdditionalResourceRequired.sendKeys(details);
    }

    public void parkJob() {
        isElementClickable(By.xpath(RADIOBUTTON_PARK_XPATH));
        POHelper.clickJavascript(park);
    }

    public void cancelJob() {
        isElementClickable(By.xpath(RADIOBUTTON_CANCEL_XPATH));
        POHelper.clickJavascript(cancel);
    }

    public void selectRandomReason() {
        waitForElement(By.xpath(DROPDOWN_REASON_ARROW_XPATH), ELEMENT_IS_CLICKABLE);
        reasonDropdownArrow.click();
        selectRandomVisibleDropdownOption();
    }

    public void selectDateToUnpark(String date) {
        waitForElement(By.xpath(DATE_TO_UNPARK_CLOCK_XPATH), ELEMENT_IS_CLICKABLE);
        dateToUnparkClock.click();
        waitForElement(By.xpath(DATE_TO_UNPARK_CALENDAR_XPATH), ELEMENT_IS_CLICKABLE);
        dateToUnparkCalendar.click();
        WebElement dateToUnpark = waitForElement(By.xpath(String.format(DATE_TO_UNPARK_OPTION_XPATH, date)), ELEMENT_IS_CLICKABLE);
        dateToUnpark.click();
    }

    public String getReason() {
        waitForElement(By.xpath(DROPDOWN_REASON_XPATH), ELEMENT_IS_CLICKABLE);
        return reasonDropdown.getText();
    }

    public String getDateToUnpark() {
        waitForElement(By.xpath(DATE_TO_UNPARK_DATE_XPATH), ELEMENT_IS_VISIBLE);
        return dateToUnpark.getAttribute("value");
    }

    public String getAlertText() {
        return alert.getText();
    }
}
