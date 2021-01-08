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
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;

public class HelpdeskScheduleCallBackPanel extends Base_Page<HelpdeskScheduleCallBackPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String RESOURCE_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'job-action-panel') and contains(@class, 'confirm-resource')]";
    private static final String RESOURCE_PANEL_CONTENT_XPATH = RESOURCE_PANEL_XPATH + "//div[contains(@class, 'job-action-panel__content')]";

    private static final String SCHEDULE_CALLBACK_PANEL_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[contains(@class, 'resource-action__panel')]";
    private static final String SCHEDULE_CALLBACK_PANEL_CONTENT_XPATH = SCHEDULE_CALLBACK_PANEL_XPATH + "//div[contains(@class, 'resource-action__content')]";
    private static final String SCHEDULE_CALLBACK_PANEL_CONTENT_CONTAINER_XPATH = SCHEDULE_CALLBACK_PANEL_CONTENT_XPATH + "//div[@class='resource-action__field-container']";

    private static final String TIME_CALENDAR_XPATH = SCHEDULE_CALLBACK_PANEL_CONTENT_CONTAINER_XPATH + DROPDOWN_CALENDAR_XPATH;
    private static final String TIME_OPTION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]//a[@title='%s']";
    private static final String TIME_CLOCK_XPATH = SCHEDULE_CALLBACK_PANEL_CONTENT_CONTAINER_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String TIME_VALUE_XPATH = SCHEDULE_CALLBACK_PANEL_CONTENT_CONTAINER_XPATH + DROPDOWN_PICK_WRAPPER_XPATH + "//input";
    private static final String CALLBACK_BUTTONS_CONTAINER_XPATH = SCHEDULE_CALLBACK_PANEL_CONTENT_CONTAINER_XPATH + "//div[@class='resource-action__callback-buttons-container']";
    private static final String CALLBACK_BUTTONS_XPATH = SCHEDULE_CALLBACK_PANEL_CONTENT_CONTAINER_XPATH + "//div[@class='resource-action__callback-buttons-container']" + "//button";
    private static final String CALLBACK_BUTTON_XPATH = CALLBACK_BUTTONS_CONTAINER_XPATH + "//button[contains(text(), '%s')]";

    private static final String NOTES_XPATH = SCHEDULE_CALLBACK_PANEL_CONTENT_CONTAINER_XPATH + "//label[contains(text(), 'Notes')]/following-sibling::textarea";

    private static final String BUTTONS_CONTAINER_XPATH = SCHEDULE_CALLBACK_PANEL_CONTENT_XPATH + "//div[@class='resource-action__button-container']";
    private static final String SAVE_BUTTON_XPATH = BUTTONS_CONTAINER_XPATH + "//button[contains(text(), 'Save')]";

    @FindBy(xpath = TIME_CALENDAR_XPATH)
    private WebElement timeCalendar;

    @FindBy(xpath = TIME_CLOCK_XPATH)
    private WebElement timeClock;

    @FindBy(xpath = TIME_VALUE_XPATH)
    private WebElement time;

    @FindBy(xpath = NOTES_XPATH)
    private WebElement notes;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    public HelpdeskScheduleCallBackPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(SCHEDULE_CALLBACK_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getCallBackTime() {
        waitForElement(By.xpath(TIME_VALUE_XPATH), ELEMENT_IS_VISIBLE);
        return time.getAttribute("value");
    }

    public void clickCallBackTimeButton(String time) {
        WebElement callBackTimeButton = waitForElement(By.xpath(String.format(CALLBACK_BUTTON_XPATH, time)), ELEMENT_IS_CLICKABLE);
        callBackTimeButton.click();
    }

    public void clickRandomCallBackTimeButton() {
        waitForElement(By.xpath(CALLBACK_BUTTONS_XPATH), ELEMENT_IS_CLICKABLE);
        clickRandomButtonInButtonsContainer(By.xpath(CALLBACK_BUTTONS_XPATH));
    }

    public void selectRandomCallBackTime(String time) {
        waitForElement(By.xpath(TIME_CALENDAR_XPATH), ELEMENT_IS_CLICKABLE);
        timeCalendar.click();
        WebElement callBackTime = waitForElement(By.xpath(String.format(TIME_OPTION_XPATH, time)), ELEMENT_IS_CLICKABLE);
        callBackTime.click();
    }

    public void setNotes(String text) {
        waitForElement(By.xpath(NOTES_XPATH), ELEMENT_IS_VISIBLE);
        notes.sendKeys(text);
    }

    public void setTime(String timeValue) {
        waitForElement(By.xpath(TIME_VALUE_XPATH), ELEMENT_IS_VISIBLE);
        time.clear();
        time.sendKeys(timeValue);
    }

    public HelpdeskManageResourcesPanel save() {
        waitForElement(By.xpath(SAVE_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        saveButton.click();
        return PageFactory.initElements(driver, HelpdeskManageResourcesPanel.class).get();
    }
}
