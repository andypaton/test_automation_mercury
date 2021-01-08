package mercury.pageobject.web.helpdesk.jobs;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskParkJobPanel extends Base_Page<HelpdeskParkJobPanel>{

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String PARK_JOB_PANEL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[contains(@class, 'job-action-panel') and contains(@class, 'job-park')]";
    private static final String PARK_JOB_PANEL_FORM_XPATH = PARK_JOB_PANEL_XPATH + "//form[@name='jobParkForm']";
    private static final String PARK_JOB_PANEL_CONTENT_XPATH = PARK_JOB_PANEL_FORM_XPATH + "//div[contains(@class, 'job-action-panel__content')]";
    private static final String PARK_JOB_PANEL_FOOTER_XPATH = PARK_JOB_PANEL_FORM_XPATH + "//div[@class='footer-button-bar']";

    private static final String DROPDOWN_REASON_ARROW_XPATH = PARK_JOB_PANEL_CONTENT_XPATH + "//label[contains(text(), 'Reason')]/..//span" + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String DROPDOWN_REASON_XPATH = PARK_JOB_PANEL_CONTENT_XPATH + "//label[contains(text(), 'Reason')]/..//span" + DROPDOWN_SEARCH_XPATH;

    private static final String DATE_TO_UNPARK_XPATH = PARK_JOB_PANEL_CONTENT_XPATH + "//label[contains(text(), 'Date to Unpark')]/..";
    private static final String DATE_TO_UNPARK_CALENDAR_XPATH = DATE_TO_UNPARK_XPATH + DROPDOWN_CALENDAR_XPATH;
    private static final String DATE_TO_UNPARK_OPTION_XPATH = "//a[@title='%s']";
    private static final String DATE_TO_UNPARK_CLOCK_XPATH = DATE_TO_UNPARK_XPATH + DROPDOWN_CLOCK_XPATH;
    private static final String DATE_TO_UNPARK_DATE_XPATH = DATE_TO_UNPARK_XPATH + DROPDOWN_PICK_WRAPPER_XPATH + "//input";

    //BUTTONS
    private static final String FOOTER_BUTTONS_CONTAINER_XPATH = PARK_JOB_PANEL_FOOTER_XPATH + "//div[@class='footer-button-bar__buttons-container']";
    private static final String SAVE_BUTTON_XPATH = FOOTER_BUTTONS_CONTAINER_XPATH + "//button[contains(text(), 'Save')]";

    @FindBy(xpath = DROPDOWN_REASON_XPATH)
    private WebElement reasonDropdown;

    @FindBy(xpath = DROPDOWN_REASON_ARROW_XPATH)
    private WebElement reasonDropdownArrow;

    @FindBy(xpath = DATE_TO_UNPARK_CALENDAR_XPATH)
    private WebElement dateToUnparkCalendar;

    @FindBy(xpath = DATE_TO_UNPARK_CLOCK_XPATH)
    private WebElement dateToUnparkClock;

    @FindBy(xpath = DATE_TO_UNPARK_DATE_XPATH)
    private WebElement dateToUnpark;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    public HelpdeskParkJobPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try{
            assertTrue("Park job panel not loaded!", driver.findElement(By.xpath(PARK_JOB_PANEL_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectRandomReason() {
        waitForElement(By.xpath(DROPDOWN_REASON_ARROW_XPATH), ELEMENT_IS_CLICKABLE);
        reasonDropdownArrow.click();
        selectRandomVisibleDropdownOption();
    }

    public void selectDateToUnpark(String date) throws ParseException, InterruptedException {
        waitForElement(By.xpath(DATE_TO_UNPARK_CALENDAR_XPATH), ELEMENT_IS_CLICKABLE);
        dateToUnparkCalendar.click();
        WebElement dateToUnpark = waitForElement(By.xpath(String.format(DATE_TO_UNPARK_OPTION_XPATH, date)), State.ELEMENT_IS_CLICKABLE);
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

    public HelpdeskJobPage save() {
        waitForElement(By.xpath(SAVE_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        saveButton.click();
        return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
    }

}
