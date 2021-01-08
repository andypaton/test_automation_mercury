package mercury.pageobject.web.helpdesk.resources;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskAdditionalResourcesRequiredPanel extends Base_Page<HelpdeskAdditionalResourcesRequiredPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";
    private static final String RESOURCE_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'job-action-panel') and contains(@class, 'confirm-resource')]";
    private static final String RESOURCE_PANEL_CONTENT_XPATH = RESOURCE_PANEL_XPATH + "//div[contains(@class, 'job-action-panel__content')]";

    private static final String ADDITIONAL_RESOURCE_PANEL_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@class='arr-resource-picker']";

    private static final String CLOSE_ICON_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@ng-hide='ctrl.removing' and @aria-hidden='false']//i[@class='icons__times']";

    private static final String DETAIL_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@ng-show='ctrl.removing' and @aria-hidden='false']//textarea";
    private static final String CANCEL_CHECKBOX_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@ng-show='ctrl.removing' and @aria-hidden='false']//label[@for='rdoJobActionCancel']";
    private static final String PARK_CHECKBOX_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@ng-show='ctrl.removing' and @aria-hidden='false']//label[@for='rdoJobActionPark']";
    private static final String PARK_REASON_DROPDOWN_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@ng-show='ctrl.removing' and @aria-hidden='false']//span[contains(@class, 'k-input')]";
    private static final String DATE_TO_UNPARK_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@ng-show='ctrl.removing' and @aria-hidden='false']" + DROPDOWN_CLOCK_XPATH;
    private static final String DATE_TO_UNPARK_TEXT_XPATH = RESOURCE_PANEL_CONTENT_XPATH + "//div[@ng-show='ctrl.removing' and @aria-hidden='false']//input[@type='text' and @class='k-input']";

    //When there is more than one additional resource
    private static final String NEW_DETAIL_XPATH = "(" + DETAIL_XPATH + ")[%s]";
    private static final String NEW_CANCEL_CHECKBOX_XPATH = "(" + CANCEL_CHECKBOX_XPATH + ")[%s]";
    private static final String NEW_PARK_CHECKBOX_XPATH = "(" + PARK_CHECKBOX_XPATH + ")[%s]";
    private static final String NEW_PARK_REASON_DROPDOWN_XPATH = "(" + PARK_REASON_DROPDOWN_XPATH + ")[%s]";
    private static final String NEW_DATE_TO_UNPARK_XPATH = "(" + DATE_TO_UNPARK_XPATH + ")[%s]";
    private static final String NEW_DATE_TO_UNPARK_TEXT_XPATH = "(" + DATE_TO_UNPARK_TEXT_XPATH + ")[%s]";

    private static final String SAVE_XPATH = RESOURCE_PANEL_XPATH + "//button[contains(text(), 'Save')]";

    @FindBy(xpath = CLOSE_ICON_XPATH)
    private WebElement closeIcon;

    @FindBy(xpath = RESOURCE_PANEL_XPATH)
    private WebElement panel;

    @FindBy(xpath = DETAIL_XPATH)
    private WebElement detail;

    @FindBy(xpath = SAVE_XPATH)
    private WebElement save;

    @FindBy(xpath = PARK_CHECKBOX_XPATH)
    private WebElement park;

    @FindBy(xpath = PARK_REASON_DROPDOWN_XPATH)
    private WebElement parkReason;

    @FindBy(xpath = DATE_TO_UNPARK_XPATH)
    private WebElement dateToUnpark;

    @FindBy(xpath = DATE_TO_UNPARK_TEXT_XPATH)
    private WebElement dateToUnparkText;

    @FindBy(xpath = CANCEL_CHECKBOX_XPATH)
    private WebElement cancel;

    public HelpdeskAdditionalResourcesRequiredPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForLoadingToComplete();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(ADDITIONAL_RESOURCE_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean isDisplayed() {
        return panel.isDisplayed();
    }

    public boolean closeAdditionalResourceButtonIsDisplayed() {
        waitForAngularRequestsToFinish();
        try {
            return isElementPresent(By.xpath(CLOSE_ICON_XPATH));
        } catch (org.openqa.selenium.NoSuchElementException ex) {
            return false;
        }
    }

    public HelpdeskAdditionalResourcesNotRequiredPanel closeAdditionalResource() {
        waitForElement(By.xpath(CLOSE_ICON_XPATH), ELEMENT_IS_CLICKABLE);
        closeIcon.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskAdditionalResourcesNotRequiredPanel.class).get();
    }

    public String selectRandomParkReason() {
        parkReason.click();
        waitForKendoLoadingToComplete();
        return selectRandomVisibleDropdownOption();
    }

    public String selectNextRandomParkReason(int number) {
        WebElement newParkReason = waitForElement(By.xpath(String.format(NEW_PARK_REASON_DROPDOWN_XPATH, number)),State.ELEMENT_IS_CLICKABLE);
        newParkReason.click();
        waitForKendoLoadingToComplete();
        return selectRandomVisibleDropdownOption();
    }

    public String selectParkClock() {
        POHelper.scrollToElement(dateToUnpark);
        dateToUnpark.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String selectNextParkClock(int number) {
        WebElement newDateToUnpark = waitForElement(By.xpath(String.format(NEW_DATE_TO_UNPARK_XPATH, number)),State.ELEMENT_IS_CLICKABLE);
        POHelper.scrollToElement(newDateToUnpark);
        newDateToUnpark.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public String getParkDateAndTime() {
        return dateToUnparkText.getAttribute("value");
    }

    public String getNextParkDateAndTime(int number) {
        WebElement newDateToUnparkText = waitForElement(By.xpath(String.format(NEW_DATE_TO_UNPARK_TEXT_XPATH, number)),State.ELEMENT_IS_VISIBLE);
        return newDateToUnparkText.getAttribute("value");
    }

    public void removeAdditionalResourceAndPark(String text) {
        detail.sendKeys(text);
        park.click();
        waitForAngularRequestsToFinish();
    }

    public void removeNextAdditionalResourceAndPark(String text, int number) {
        WebElement newDetail = waitForElement(By.xpath(String.format(NEW_DETAIL_XPATH, number)),State.ELEMENT_IS_CLICKABLE);
        newDetail.sendKeys(text);
        WebElement newPark = waitForElement(By.xpath(String.format(NEW_PARK_CHECKBOX_XPATH, number)),State.ELEMENT_IS_CLICKABLE);
        newPark.click();
        waitForAngularRequestsToFinish();
    }

    public void removeAdditionalResourceAndCancel(String text) {
        detail.sendKeys(text);
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public void removeNextAdditionalResourceAndCancel(String text, int number) {
        WebElement newDetail = waitForElement(By.xpath(String.format(NEW_DETAIL_XPATH, number)),State.ELEMENT_IS_CLICKABLE);
        newDetail.sendKeys(text);
        WebElement newCancel = waitForElement(By.xpath(String.format(NEW_CANCEL_CHECKBOX_XPATH, number)),State.ELEMENT_IS_CLICKABLE);
        newCancel.click();
        waitForAngularRequestsToFinish();
    }

    public void save() {
        save.click();
    }

}