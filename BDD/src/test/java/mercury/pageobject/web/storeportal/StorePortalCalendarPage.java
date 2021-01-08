package mercury.pageobject.web.storeportal;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class StorePortalCalendarPage extends Base_Page<StorePortalCalendarPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "Mercury Store Portal";

    private static final String PAGE_BODY_XPATH = "//div[@class='main']";

    private static final String FULL_CALENDAR_XPATH = PAGE_BODY_XPATH + "//div[@class='calendar']";

    private static final String CALENDAR_SECTION_XPATH = FULL_CALENDAR_XPATH + "//div[@class='calendar-section']";
    private static final String CALENDAR_MONTH_XPATH = CALENDAR_SECTION_XPATH + "//span[@class='calendar-header-month']";
    private static final String CURRENT_CALENDAR_DAY_XPATH = CALENDAR_SECTION_XPATH + "//mwl-calendar-month-cell[contains(@class, 'selected')]//span";
    private static final String CALENDAR_DAY_XPATH = CALENDAR_SECTION_XPATH + "//mwl-calendar-month-cell[contains(@class, 'cal-in-month')]//span[text()='%s']";
    private static final String CALENDAR_DAYS_WITH_NO_EVENTS_XPATH = CALENDAR_SECTION_XPATH + "//mwl-calendar-month-cell[contains(@class, 'cal-in-month') and not(contains(@class, 'cal-has-events'))]//span";

    private static final String JOB_DETAILS_SECTION_XPATH = FULL_CALENDAR_XPATH + "//div[@class='job-detail-section']";
    private static final String JOB_DETAILS_DAY_XPATH = JOB_DETAILS_SECTION_XPATH + "//div[@class='job-detail-header']";
    private static final String JOB_DETAILS_DATE_XPATH = JOB_DETAILS_SECTION_XPATH + "//div[@class='job-detail-date']";
    private static final String JOB_DETAILS_XPATH = JOB_DETAILS_SECTION_XPATH + "//div[contains(text(), '%s')]";
    private static final String NO_VISITS_SCHEDULED_MESSAGE_XPATH = JOB_DETAILS_SECTION_XPATH + "//div[contains(@class, 'job-item')]";


    @FindBy(xpath = CALENDAR_MONTH_XPATH)
    private WebElement calendarMonth;

    @FindBy(xpath = CURRENT_CALENDAR_DAY_XPATH)
    private WebElement currentCalendarDay;

    @FindBy(xpath = JOB_DETAILS_DAY_XPATH)
    private WebElement jobDetailsDay;

    @FindBy(xpath = JOB_DETAILS_DATE_XPATH)
    private WebElement jobDetailsDate;

    @FindBy(xpath = CALENDAR_DAYS_WITH_NO_EVENTS_XPATH)
    private List<WebElement> daysWithNoEvents;

    @FindBy(xpath = NO_VISITS_SCHEDULED_MESSAGE_XPATH)
    private WebElement noVisitsScheduledMessage;


    public StorePortalCalendarPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            POHelper.waitWhileBusy();
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_BODY_XPATH));
            logger.info(PAGE_TITLE + ": Page loaded");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + ": Page failed to load");
            throw new AssertionError();
        }
    }

    public String getCalendarMonth() {
        return calendarMonth.getText();
    }

    public String getCurrentCalendarDay() {
        return currentCalendarDay.getText();
    }

    public String getJobDetailsDay() {
        return jobDetailsDay.getText();
    }

    public String getJobDetailsDate() {
        return jobDetailsDate.getText();
    }

    public void selectCalendarDay(String dayOfMonth) {
        WebElement day = driver.findElement(By.xpath(String.format(CALENDAR_DAY_XPATH, dayOfMonth)));
        day.click();
        waitForAngularRequestsToFinish();
    }

    public Boolean isJobDetailsDisplayed(String description) {
        WebElement details = waitForElement(By.xpath(String.format(JOB_DETAILS_XPATH, description)), ELEMENT_IS_VISIBLE);
        return details.isDisplayed();
    }

    public List<String> getDaysWithNoEvents() {
        List<String> days = new ArrayList<>();
        for (WebElement day : daysWithNoEvents) {
            days.add(day.getText());
        }
        return days;
    }

    public String getNoVisitsScheduledMessage() {
        return noVisitsScheduledMessage.getText();
    }

}
