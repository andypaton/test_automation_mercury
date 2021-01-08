package mercury.pageobject.web.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskAddOrOverrideAbsencePanel extends Base_Page<HelpdeskAddOrOverrideAbsencePanel> {

    private static final Logger logger = LogManager.getLogger();

    // Core

    private static final String ACTIVE_PANEL_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[contains(@class, 'resource-action-panel')]";
    private static final String ACTIVE_PANEL_CONTENT_TABLE_HEADER_XPATH = ACTIVE_PANEL_XPATH + "//div[contains(@class, 'resource-action-panel__content')]//table/thead";
    private static final String HEADER_PANEL_XPATH = ACTIVE_PANEL_XPATH + "//div[contains(@class, 'resource-action-panel__header')]";
    private static final String FOOTER_BAR_XPATH = ACTIVE_PANEL_XPATH + "//div[@class='footer-button-bar__buttons-container']";
    private static final String HEADLINE_XPATH = HEADER_PANEL_XPATH + "//h1[contains(text(),'Add or override absence for')]";
    private static final String FROM_LABEL_XPATH = ACTIVE_PANEL_XPATH + "//label[@for='ResourceAvailabilityStartAt']";
    private static final String TO_LABEL_XPATH = ACTIVE_PANEL_XPATH + "//label[@for='ResourceAvailabilityEndAt']";
    private static final String TYPE_LABEL_XPATH = ACTIVE_PANEL_XPATH + "//label[@for='ResourceAvailabilityType']";
    private static final String BACKUP_RESOURCE_LABEL_XPATH = ACTIVE_PANEL_XPATH + "//label[@for='ResourceAvailabilityCoverResource']";
    private static final String OVERLAPPING_ABSENCE_ALERT_XPATH = "//div[contains(@class, 'showSweetAlert') and contains(@class, 'visible')]";
    private static final String ALERT_BUTTON_CONTAINER_XPATH = OVERLAPPING_ABSENCE_ALERT_XPATH + "//div[@class='sa-button-container']";

    // Buttons

    private static final String ADD_ABSENCE_BUTTON_XPATH = FOOTER_BAR_XPATH + "//button[contains(text(),'Add absence')]";
    private static final String SAVE_ABSENCE_BUTTON_XPATH = FOOTER_BAR_XPATH + "//button[contains(text(),'Save')]";
    private static final String CANCEL_ADD_OR_OVERRIDE_XPATH = FOOTER_BAR_XPATH + "//button[contains(text(),'Cancel')]";
    private static final String CLOSE_ADD_OR_OVERRIDE_XPATH = FOOTER_BAR_XPATH + "//button[contains(text(),'Close')]";
    private static final String CONFIRM_OVERLAPPING_ABSENCE_ALERT_XPATH = ALERT_BUTTON_CONTAINER_XPATH + "//button[contains(text(),'OK')]";

    // Input fields

    private static final String FROM_DATE_CALENDAR_XPATH = FROM_LABEL_XPATH + "//following-sibling::ph-date-time" + DROPDOWN_CALENDAR_XPATH;
    private static final String TO_DATE_CALENDAR_XPATH = TO_LABEL_XPATH + "//following-sibling::ph-date-time" + DROPDOWN_CALENDAR_XPATH;
    private static final String FROM_DATE_XPATH = FROM_LABEL_XPATH + "//following-sibling::ph-date-time//input[@name='StartAt']";
    private static final String TO_DATE_XPATH = TO_LABEL_XPATH + "//following-sibling::ph-date-time//input[@name='EndAt']";
    private static final String ABSENCE_TYPE_XPATH = TYPE_LABEL_XPATH + "//following-sibling::select";
    private static final String ABSENCE_NOTES_XPATH = ACTIVE_PANEL_XPATH + "//textarea[@id='ResourceAvailabilityNotes']";
    private static final String BACKUP_RESOURCE_XPATH = BACKUP_RESOURCE_LABEL_XPATH + "//following-sibling::span" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String SELECTED_BACKUP_RESOURCE = BACKUP_RESOURCE_LABEL_XPATH + "//following-sibling::span" + DROPDOWN_SEARCH_XPATH;

    private static final String GRID_XPATH = ACTIVE_PANEL_XPATH + "//table";

    @FindBy(xpath = ADD_ABSENCE_BUTTON_XPATH)
    private WebElement addAbsence;

    @FindBy(xpath = SAVE_ABSENCE_BUTTON_XPATH)
    private WebElement saveAbsence;

    @FindBy(xpath = CANCEL_ADD_OR_OVERRIDE_XPATH)
    private WebElement cancelAddOrOverride;

    @FindBy(xpath = CLOSE_ADD_OR_OVERRIDE_XPATH)
    private WebElement closeAddOrOverride;

    @FindBy(xpath = FROM_DATE_CALENDAR_XPATH)
    private WebElement fromDateCalendar;

    @FindBy(xpath = TO_DATE_CALENDAR_XPATH)
    private WebElement toDateCalendar;

    @FindBy(xpath = FROM_DATE_XPATH)
    private WebElement fromDate;

    @FindBy(xpath = TO_DATE_XPATH)
    private WebElement toDate;

    @FindBy(xpath = ABSENCE_TYPE_XPATH)
    private WebElement typeDropdown;

    @FindBy(xpath = BACKUP_RESOURCE_XPATH)
    private WebElement backupResource;

    @FindBy(xpath = ABSENCE_NOTES_XPATH)
    private WebElement absenceNotes;

    @FindBy(xpath = HEADLINE_XPATH)
    private WebElement panelHeadline;

    @FindBy(xpath = SELECTED_BACKUP_RESOURCE)
    private WebElement selectedBackupResource;

    @FindBy(xpath = CONFIRM_OVERLAPPING_ABSENCE_ALERT_XPATH)
    private WebElement overlappingAbsenceAlert;

    @FindBy(xpath = ACTIVE_PANEL_CONTENT_TABLE_HEADER_XPATH)
    private WebElement absenceTable;

    public HelpdeskAddOrOverrideAbsencePanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            assertTrue("Accept Job panel not loaded!", driver.findElement(By.xpath(HEADLINE_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch (NoSuchElementException ex) {
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean existingAbsencesDisplayed() {
        return isElementPresent(By.xpath(ACTIVE_PANEL_CONTENT_TABLE_HEADER_XPATH));
    }

    public void clickAddAbsence() {
        addAbsence.click();
        waitForAngularRequestsToFinish();
    }

    public void saveAbsence() {
        saveAbsence.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCancelAddOrOverride() {
        cancelAddOrOverride.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCloseAddOrOverride() {
        closeAddOrOverride.click();
        waitForAngularRequestsToFinish();
    }

    public void selectFromDate(String date) throws ParseException {
        selectCalendarDate(fromDateCalendar, date);
    }

    public void selectToDate(String date) throws ParseException {
        selectCalendarDate(toDateCalendar, date);
    }

    public String getFromDate() throws ParseException {
        return fromDate.getAttribute("value");
    }

    public String getToDate() throws ParseException {
        return toDate.getAttribute("value");
    }

    public void selectAbsenceType(String option) {
        typeDropdown.click();
        waitForAngularRequestsToFinish();
        selectOptionFromSelect(typeDropdown, option);
    }

    public void selectRandomAbsenceType() {
        typeDropdown.click();
        waitForAngularRequestsToFinish();
        selectRandomOptionFromSelect(typeDropdown);
    }

    public String getSelectedAbsenceType() {
        return getSelectSelectedText(typeDropdown);
    }

    public void selectBackupResource() {
        backupResource.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
    }

    public String getSelectedBackupResource() {
        return selectedBackupResource.getText();
    }

    public void setAbsenceNotes(String notes) {
        absenceNotes.sendKeys(notes);
    }

    public String getResourceName() {
        return StringUtils.substringAfter(panelHeadline.getText(), ": ");
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public void clickOverlappingAbenceAlert() {
        overlappingAbsenceAlert.click();
        waitForAngularRequestsToFinish();
    }
}
