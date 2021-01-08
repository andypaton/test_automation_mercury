package mercury.pageobject.web.helpdesk;

import static mercury.helpers.POHelper.scrollTo;
import static mercury.helpers.StringHelper.normalize;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.OutputHelper;
import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class HelpdeskTimelineTab extends Base_Page<HelpdeskTimelineTab> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_TAB_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String TIMELINE_TAB_XPATH = ACTIVE_TAB_XPATH + "//div[contains(@class, 'view') and contains(@class, 'job')]//div[contains(@class, 'timeline-container')]";

    private static final String TIMELINE_EVENT_NAME_XPATH = ACTIVE_TAB_XPATH + "//div[@class='timeline-template-item__header-text']//*[contains(normalize-space(text()), '%s')]";
    private static final String TIMELINE_EVENT_XPATH = ACTIVE_TAB_XPATH + "//div[@class='timeline-template-item__header-text']//*[contains(normalize-space(text()), '%s')]/ancestor::li";
    private static final String TIMELINE_FIRST_EVENT_XPATH = ACTIVE_TAB_XPATH + "//div[@class='timeline-template-item__header-text']//*[contains(normalize-space(text()), \"%s\")]/ancestor::li[1]";
    private static final String LOWERCASE_EVENT_NAME_XPATH = ACTIVE_TAB_XPATH + "//div[@class='timeline-template-item__header-text']//*[contains(normalize-space(" + LOWERCASE_TEXT + "), '%s')]";
    private static final String LOWERCASE_EVENT_XPATH = ACTIVE_TAB_XPATH + "//div[@class='timeline-template-item__header-text']//*[contains(normalize-space(" + LOWERCASE_TEXT + "), '%s')]/ancestor::li[1]";
    private static final String LOGGED_BY_XPATH = TIMELINE_FIRST_EVENT_XPATH + "//p[@class='logged-by']";
    private static final String ICON_XPATH = LOWERCASE_EVENT_XPATH + "//div[contains(@class, 'timeline-icon') and contains(@class, '%s')]";
    private static final String DATE_XPATH = TIMELINE_FIRST_EVENT_XPATH + "//div[contains(@class, 'timeline-date')]/span";
    private static final String TIME_XPATH = TIMELINE_FIRST_EVENT_XPATH + "//div[@class='timeline-template-item__header-time']/p";
    private static final String HOME_OFFICE_TIME_XPATH = TIME_XPATH + "[1]";
    private static final String STORE_TIME_XPATH = TIME_XPATH + "[2]";
    private static final String RIGHT_CHEVRON_XPATH = TIMELINE_FIRST_EVENT_XPATH + "//i[contains(@class, 'glyphicon-chevron-right')][1]";
    private static final String VALUE_ON_RIGHT_XPATH = "//div[contains(@class, 'timeline-expanded')]//div[contains(@class, 'key') and contains(text(), '%s')]/following-sibling::div";
    private static final String VALUE_BELOW_XPATH = "//div[contains(@class, 'timeline-expanded')]//div[contains(@class, 'key')]/div[contains(text(), '%s')]/../following-sibling::div";
    private static final String NOTES_VALUE = "//div[contains(@class, 'timeline-expanded')]//div[text()='%s']/..//div[@class='note-text']";
    private static final String SUB_HEADINGS_XPATH = TIMELINE_FIRST_EVENT_XPATH + VALUE_BELOW_XPATH + "//div[contains(@class, 'timeline-subheading')]";
    private static final String SUB_VALUES_XPATH = TIMELINE_FIRST_EVENT_XPATH + VALUE_BELOW_XPATH + "//div[contains(@class, 'timeline-subvalue')]";

    private static final String SUB_HEADING_VALUES_XPATH = TIMELINE_FIRST_EVENT_XPATH + VALUE_BELOW_XPATH + "//div[contains(@class, 'timeline-subheading') and contains(text(), '%s')]/following-sibling::div[contains(@class, 'timeline-subvalue') and contains(text(), '%s')]/..";

    private static final String EXPAND_ALL_XPATH = TIMELINE_TAB_XPATH + "//div[contains(text(), 'Expand All')]/i";
    private static final String ADD_NOTE_BUTTON_XPATH = TIMELINE_TAB_XPATH + "//button[contains(@class, 'btn') and contains(@class, 'btn-primary') and contains(@class, 'btn-compact')]";
    private static final String NOTE_TEXTAREA_XPATH = TIMELINE_TAB_XPATH + "//textarea[@id='timelinenote']";
    private static final String SAVE_NOTE_BUTTON_XPATH = TIMELINE_TAB_XPATH + "//button[contains(text(), 'Save')]";
    private static final String TIMELINE_PRIVATE_NOTE_CHECKBOX_XPATH = TIMELINE_TAB_XPATH + "//input[@type='checkbox'and @id='private-note']";
    private static final String SEARCH_XPATH = TIMELINE_TAB_XPATH + "//input[@type='text' and @placeholder='Search']";


    @FindBy(xpath = TIMELINE_TAB_XPATH)
    private WebElement timeline;

    @FindBy(xpath = EXPAND_ALL_XPATH)
    WebElement expandAll;

    @FindBy(xpath = ADD_NOTE_BUTTON_XPATH)
    private WebElement addNoteButton;

    @FindBy(xpath = SAVE_NOTE_BUTTON_XPATH)
    private WebElement saveNoteButton;

    @FindBy(xpath = NOTE_TEXTAREA_XPATH)
    private WebElement noteTextArea;

    @FindBy(xpath = TIMELINE_PRIVATE_NOTE_CHECKBOX_XPATH)
    private WebElement privateCheckbox;

    @FindBy(xpath = SEARCH_XPATH)
    private WebElement search;

    public HelpdeskTimelineTab(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            waitForAngularRequestsToFinish();
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(TIMELINE_TAB_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public boolean timelineDoesNotContain(String entry) {
        String entry_xpath = String.format(TIMELINE_FIRST_EVENT_XPATH, entry);
        return !isElementPresent(By.xpath(entry_xpath));
    }

    public boolean timelineDoesNotContainLowercaseText(String entry) {
        String entry_xpath = String.format(LOWERCASE_EVENT_XPATH, entry.toLowerCase());
        return !isElementPresent(By.xpath(entry_xpath));
    }

    public boolean timelineContains(String entry) {
        String entry_xpath = String.format(TIMELINE_FIRST_EVENT_XPATH, entry);
        WebElement event = waitForElement(By.xpath(entry_xpath), State.ELEMENT_IS_VISIBLE);
        return event != null;
    }

    public boolean timelineContainsLowercaseText(String entry) {
        String entry_xpath = String.format(LOWERCASE_EVENT_XPATH, entry.toLowerCase());
        WebElement event = waitForElement(By.xpath(entry_xpath), State.ELEMENT_IS_VISIBLE);
        return event != null;
    }

    public String getLoggedBy(String event) {
        String xpath = String.format(LOGGED_BY_XPATH, event);
        return driver.findElement(By.xpath(xpath)).getText();
    }

    public String getDate(String event) {
        String xpath = String.format(DATE_XPATH, event);
        return driver.findElement(By.xpath(xpath)).getText();
    }

    public String getHomeOfficeTime(String event) {
        String xpath = String.format(HOME_OFFICE_TIME_XPATH, event);
        return driver.findElement(By.xpath(xpath)).getText();
    }

    public String getStoreTime(String event) {
        String xpath = String.format(STORE_TIME_XPATH, event);
        return driver.findElement(By.xpath(xpath)).getText();
    }

    public Boolean isStoreTimeDisplayed(String event) {
        String xpath = String.format(STORE_TIME_XPATH, event);
        return isElementPresent(By.xpath(xpath));
    }

    public boolean isWrenchIcon(String entry) {
        return isIconDisplayed(entry, "wrench");
    }

    public boolean isPhoneIcon(String entry) {
        return isIconDisplayed(entry, "phone");
    }

    public boolean isUserIcon(String entry) {
        return isIconDisplayed(entry, "user");
    }

    public boolean isFileIcon(String entry) {
        return isIconDisplayed(entry, "file-text-o");
    }

    public boolean isBookIcon(String entry) {
        return isIconDisplayed(entry, "book");
    }

    public boolean isTimesIcon(String entry) {
        return isIconDisplayed(entry, "times");

    }

    public boolean isTabletIcon(String entry) {
        return isIconDisplayed(entry, "tablet");

    }

    public boolean isCommentIcon(String entry) {
        return isIconDisplayed(entry, "comment");
    }

    public void expandTimelineEvent(String event) throws Exception {
        String xpath = String.format(RIGHT_CHEVRON_XPATH, event);
        if (isElementVisible(By.xpath(xpath))) {
            WebElement eventChevron = waitForElement(By.xpath(xpath), State.ELEMENT_IS_CLICKABLE);
            POHelper.clickJavascript(eventChevron);
        }
    }

    public void expandAll() throws Exception {
        if (isElementClickable(By.xpath(EXPAND_ALL_XPATH))) {
            expandAll.click();
            waitForAngularRequestsToFinish();
        }
    }

    public int getNumberOfMatchingEvents(String event) {
        int count = 0;
        if (isElementPresent(By.xpath(String.format(TIMELINE_EVENT_XPATH, event.trim() + " - ")))) {
            count = driver.findElements(By.xpath(  String.format(TIMELINE_EVENT_XPATH, event.trim() + " - "))).size();
        } else {
            count = driver.findElements(By.xpath(String.format(TIMELINE_EVENT_XPATH, event.trim()))).size();
        }
        return count;
    }

    public String getEventDetails(String event, String key) throws Exception {

        event = getClosestMatch(TIMELINE_EVENT_NAME_XPATH, event);
        event = normalize(event);
        WebElement timelineEvent = driver.findElement(By.xpath(String.format(TIMELINE_FIRST_EVENT_XPATH, event)));

        int pos = Integer.valueOf(timelineEvent.getLocation().getY());
        scrollTo(pos);

        if (isElementPresent(By.xpath(String.format(RIGHT_CHEVRON_XPATH, event)))) {
            expandTimelineEvent(event);
        }

        String xpath = null;
        if (key.equalsIgnoreCase("Notes")) {
            xpath = String.format(TIMELINE_FIRST_EVENT_XPATH + NOTES_VALUE, event, key);
            if (!isElementVisible(By.xpath(xpath))) {
                xpath = String.format(TIMELINE_FIRST_EVENT_XPATH + VALUE_BELOW_XPATH, event, key);
            }
        } else {
            xpath = String.format(TIMELINE_FIRST_EVENT_XPATH + VALUE_BELOW_XPATH, event, key);
            if ( !isElementVisible(By.xpath(xpath))) {
                xpath = String.format(TIMELINE_FIRST_EVENT_XPATH + VALUE_ON_RIGHT_XPATH, event, key);
            }
        }

        return driver.findElement(By.xpath(xpath)).getAttribute("innerText");
    }

    public String getEventDetails(String event, int eventNumber, String key) throws Exception {

        expandAll();

        String eventsXpath = String.format(TIMELINE_EVENT_XPATH, event);
        String eventXpath = String.format("(%s)[%d]", eventsXpath, eventNumber + 1 );

        String xpath = eventXpath + String.format(VALUE_BELOW_XPATH, key);
        if ( !isElementPresent(By.xpath(xpath)) ) {
            xpath = eventXpath + String.format(VALUE_ON_RIGHT_XPATH, key);
        }
        if ( !isElementPresent(By.xpath(xpath)) ) {
            xpath = eventXpath + String.format(VALUE_ON_RIGHT_XPATH, StringUtils.capitalize(key));
        }

        return driver.findElement(By.xpath(xpath)).getAttribute("innerText");
    }

    public boolean isEventDetailListed(String event, String key) throws Exception {
        event = normalize(getClosestMatch(TIMELINE_EVENT_NAME_XPATH, event));
        WebElement timelineEvent = driver.findElement(By.xpath(String.format(TIMELINE_FIRST_EVENT_XPATH, event)));

        int pos = Integer.valueOf(timelineEvent.getLocation().getY());
        scrollTo(pos);

        if (isElementPresent(By.xpath(String.format(RIGHT_CHEVRON_XPATH, event)))) {
            expandTimelineEvent(event);
        }

        String xpath = String.format(VALUE_BELOW_XPATH, key);
        return isElementPresent(timelineEvent, By.xpath(xpath));
    }

    private String getClosestMatch(String xpath, String event) {
        if (isElementPresent(By.xpath(String.format(xpath, event.trim() + " - ")))) {
            return driver.findElement(By.xpath(String.format(xpath, event.trim() + " - "))).getText();
        }
        return driver.findElement(By.xpath(String.format(xpath, event.trim()))).getAttribute("textContent");
    }

    private boolean isIconDisplayed(String entry, String icon) {
        String icon_xpath = String.format(ICON_XPATH, entry.toLowerCase(), icon);
        WebElement event = waitForElement(By.xpath(icon_xpath), State.ELEMENT_IS_VISIBLE);
        return event != null;
    }

    public String getLastEventLike(String event) {
        if ("Notification".equalsIgnoreCase(event)) {
            return getClosestMatch(LOWERCASE_EVENT_NAME_XPATH, event.toLowerCase());
        } else {
            return getClosestMatch(TIMELINE_EVENT_NAME_XPATH, event);
        }
    }

    public void takeScreenshotOfEvent(OutputHelper outputHelper, String event) throws Exception {
        if (isElementPresent(By.xpath(String.format(TIMELINE_FIRST_EVENT_XPATH, event)))) {
            expandAll();
            outputHelper.takeScreenshot(String.format(TIMELINE_FIRST_EVENT_XPATH, event));
        } else {
            // event not found - take screenshot of page
            outputHelper.takeScreenshots();
        }
    }

    public List<String> getMatchingEvents(String event) {
        List<WebElement> elements = driver.findElements(By.xpath(String.format(TIMELINE_FIRST_EVENT_XPATH, event)));
        List<String> texts = elements.stream().map(WebElement::getText).collect(Collectors.toList());
        return texts;
    }

    public void clickAddNotes() {
        addNoteButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickSaveButton() {
        saveNoteButton.click();
        waitForAngularRequestsToFinish();
    }

    public void addJobNotes(String notes) {
        noteTextArea.sendKeys(notes);
    }

    public void clickPrivateCheckbox() {
        privateCheckbox.click();
        waitForAngularRequestsToFinish();
    }

    public void search(String str) {
        int pos = Integer.valueOf(timeline.getLocation().getY());
        scrollTo(pos);
        search.clear();
        search.sendKeys(str);
        waitForAngularRequestsToFinish();
    }

    public void clearSearch() {
        int pos = Integer.valueOf(timeline.getLocation().getY());
        scrollTo(pos);
        search.clear();
        waitForAngularRequestsToFinish();
    }

    private List<String> getEventSubDetails(String event, String key, String xpath) throws Exception {

        event = getClosestMatch(TIMELINE_EVENT_NAME_XPATH, event);
        WebElement timelineEvent = driver.findElement(By.xpath(String.format(TIMELINE_FIRST_EVENT_XPATH, event)));

        int pos = Integer.valueOf(timelineEvent.getLocation().getY());
        scrollTo(pos);

        if (isElementPresent(By.xpath(String.format(RIGHT_CHEVRON_XPATH, event)))) {
            expandTimelineEvent(event);
        }

        waitForElement(By.xpath(xpath), State.ELEMENT_IS_VISIBLE);

        List<String> subHeadings = new ArrayList<>();
        for (WebElement we : driver.findElements(By.xpath(xpath))) {
            subHeadings.add(we.getText());
        }
        return subHeadings;
    }

    public List<String> getEventSubHeadings(String event, String key) throws Exception {
        String xpath = String.format(SUB_HEADINGS_XPATH, event, key);
        return getEventSubDetails(event, key, xpath);
    }

    public List<String> getEventSubValues(String event, String key) throws Exception {
        String xpath = String.format(SUB_VALUES_XPATH, event, key);
        return getEventSubDetails(event, key, xpath);
    }

    public boolean isBold(String event, String key, String header, String value) throws Exception {
        String xpath = String.format(SUB_HEADING_VALUES_XPATH, event, key, header, value);
        List<WebElement> webElements = driver.findElements(By.xpath(xpath));
        JavascriptExecutor executor = (JavascriptExecutor) driver;

        for (WebElement we : webElements) {
            String fontWeight = (String) executor.executeScript("return getComputedStyle(arguments[0]).getPropertyValue('font-weight');",  we);
            if (fontWeight.trim().equals("bold") || Integer.parseInt(fontWeight) >= 700) {
                return true;
            }
        }
        return false;
    }

}