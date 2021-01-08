package mercury.pageobject.web.helpdesk.jobs;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class AddJobNotesModal extends Base_Page<AddJobNotesModal> {

    private static final Logger logger = LogManager.getLogger();

    //Page
    private static final String PAGE_BODY_XPATH = "//body[@class='modal-open']";

    //Main panel
    private static final String NOTES_MODAL_MAIN_PANEL_XPATH = PAGE_BODY_XPATH + "//div[@id='main-panel']";

    //Modal Dialog
    private static final String NOTES_MODAL_DIALOG_XPATH = NOTES_MODAL_MAIN_PANEL_XPATH + "//div[@class='modal-dialog']";

    //Timeline
    private static final String NOTES_MODAL_JOB_TIMELINE_XPATH = NOTES_MODAL_MAIN_PANEL_XPATH + "//div[contains(@class,'modal-body') and contains(@class, 'job__timeline')]";
    private static final String NOTES_MODAL_TIMELINE_CONTENT_XPATH = NOTES_MODAL_JOB_TIMELINE_XPATH + "//div[contains(@class, 'timeline-content') and contains(@class, 'col-xs-12')]";
    private static final String NOTES_MODAL_TIMELINE_LIST_XPATH = NOTES_MODAL_TIMELINE_CONTENT_XPATH + "//div[@class='timeline-list']";
    private static final String NOTES_MODAL_TIMELINE_ADDEDNOTE_HEADER_XPATH = NOTES_MODAL_TIMELINE_LIST_XPATH + "//div[@class='timeline-template-item__header-text']//*[contains(text(), 'note added')][1]";
    private static final String TIMELINE_NOTE_TEXT_XPATH = NOTES_MODAL_TIMELINE_ADDEDNOTE_HEADER_XPATH + "/ancestor::li//div[@class='note-text']";
    private static final String TIMELINE_NOTE_CHEVRON_XPATH = NOTES_MODAL_TIMELINE_ADDEDNOTE_HEADER_XPATH + "/ancestor::li//i[contains(@class, 'glyphicon-chevron-right')]";
    private static final String TIMELINE_NOTE_DATE_XPATH = NOTES_MODAL_TIMELINE_ADDEDNOTE_HEADER_XPATH + "/ancestor::li//div[contains(@class, 'timeline-row-item') and contains(@class, 'timeline-date')]//span";

    //Notes
    private static final String NOTES_MODAL_NOTE_CONTROLS_XPATH = NOTES_MODAL_TIMELINE_CONTENT_XPATH + "//div[@class='timeline__note']";
    private static final String NOTES_MODAL_TEXTAREA_XPATH = NOTES_MODAL_NOTE_CONTROLS_XPATH + "//textarea[@id='timelinenote']";
    private static final String NOTES_MODAL_BUTTONS_XPATH = NOTES_MODAL_NOTE_CONTROLS_XPATH + "//div[contains(@class, 'btn-toolbar') and contains(@class, 'pull-right')]";
    private static final String NOTES_MODAL_SAVE_NOTE_BUTTON_XPATH = NOTES_MODAL_BUTTONS_XPATH + "//button[contains(text(), 'Save')]";
    private static final String NOTES_MODAL_PRIVATE_CHECKBOX_XPATH = NOTES_MODAL_NOTE_CONTROLS_XPATH + "//input[@type='checkbox']";

    //Footer
    private static final String NOTES_MODAL_FOOTER_XPATH =  NOTES_MODAL_DIALOG_XPATH + "//div[@class='modal-footer']";
    private static final String NOTES_MODAL_CLOSE_BUTTON_XPATH = NOTES_MODAL_FOOTER_XPATH + "//button[contains(text(), 'Close')]";

    @FindBy(xpath = NOTES_MODAL_DIALOG_XPATH)
    private WebElement modalDialog;

    @FindBy(xpath = NOTES_MODAL_TEXTAREA_XPATH)
    private WebElement addJobNote;

    @FindBy(xpath = NOTES_MODAL_SAVE_NOTE_BUTTON_XPATH)
    private WebElement saveNoteButton;

    @FindBy(xpath = NOTES_MODAL_TIMELINE_LIST_XPATH)
    private WebElement timelineItems;

    @FindBy(xpath = NOTES_MODAL_CLOSE_BUTTON_XPATH)
    private WebElement closeButton;

    @FindBy(xpath = NOTES_MODAL_PRIVATE_CHECKBOX_XPATH)
    private WebElement privateCheckbox;

    public AddJobNotesModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            waitForAngularRequestsToFinish();
            waitForElement(By.xpath(PAGE_BODY_XPATH), State.ELEMENT_IS_VISIBLE);
            assertTrue("Notes modal not loaded!", driver.findElement(By.xpath(PAGE_BODY_XPATH)).isDisplayed());
            logger.info("Notes Modal Dialog loaded");
        } catch(NoSuchElementException ex){
            logger.info("Notes Model Dialog failed to load");
            throw new AssertionError();
        }
    }

    public boolean isDisplayed() {
        return modalDialog.isDisplayed();
    }

    public void addJobNotes(String notes) {
        addJobNote.click();
        waitForAngularRequestsToFinish();
        addJobNote.sendKeys(notes);
    }

    public void clickSaveButton() {
        saveNoteButton.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCloseButton() {
        closeButton.click();
        waitForAngularRequestsToFinish();
    }

    public String getAddedNoteText(String event) {
        WebElement noteText = driver.findElement(By.xpath(String.format(TIMELINE_NOTE_TEXT_XPATH, event)));
        return noteText.getAttribute("innerHTML");
    }

    public String getAddedNoteDate(String event) {
        WebElement noteDate = driver.findElement(By.xpath(String.format(TIMELINE_NOTE_DATE_XPATH, event)));
        return noteDate.getAttribute("innerHTML");
    }

    public void openTimelineItem(String event) {
        WebElement noteChevron = driver.findElement(By.xpath(String.format(TIMELINE_NOTE_CHEVRON_XPATH, event)));
        noteChevron.click();
        waitForAngularRequestsToFinish();
    }

    public int getBadgeCount() {
        String headerXpath = NOTES_MODAL_TIMELINE_ADDEDNOTE_HEADER_XPATH.substring(0, NOTES_MODAL_TIMELINE_ADDEDNOTE_HEADER_XPATH.length() - 3);
        List<WebElement> notesInTimeline = driver.findElements(By.xpath(headerXpath));
        return notesInTimeline.size();
    }

    public void clickPrivateCheckbox() {
        privateCheckbox.click();
        waitForAngularRequestsToFinish();
    }
}
