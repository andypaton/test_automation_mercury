package mercury.pageobject.web.helpdesk;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;
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

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.resources.HelpdeskManageResourcesPanel;

public class HelpdeskResourceETAPanel extends Base_Page<HelpdeskResourceETAPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_PANEL_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[contains(@class, 'resource-action__panel')]";
    private static final String ADVISED_TO_XPATH = ACTIVE_PANEL_XPATH + "//input[@name='advisedTo']";
    private static final String ETA_ADVISED_TO_SITE_RADIOBUTTON_XPATH = ACTIVE_PANEL_XPATH + "//div[@class='yesNoRadio']//label[@for='%s']";
    private static final String ETA_DATE_CALENDARICON_XPATH = ACTIVE_PANEL_XPATH + DROPDOWN_CALENDAR_XPATH;
    private static final String ETA_DATEPICKER_XPATH = ACTIVE_PANEL_XPATH + "//input[@id='datepicker']";
    private static final String ETA_DATE_XPATH = VISIBLE_DROPDOWN_CALENDAR_OPTIONS_XPATH + "//a[@title='%s']";
    private static final String ETA_TIME_SECTION_XPATH = ACTIVE_PANEL_XPATH + "//div[@class='other-eta-section__time']";
    private static final String ETA_TIME_XPATH = ETA_TIME_SECTION_XPATH + DROPDOWN_SEARCH_XPATH;
    private static final String SAVE_BUTTON_XPATH = ACTIVE_PANEL_XPATH +  "//button[contains(text(),'Save')]";
    private static final String ETA_PANEL_HEADING_XPATH = ACTIVE_PANEL_XPATH +  "//h1[contains(text(),'Provide ETA')]";


    @FindBy(xpath = ADVISED_TO_XPATH)
    private WebElement advisedTo;

    @FindBy(xpath = ETA_DATE_CALENDARICON_XPATH)
    private WebElement etaDateCalenderIcon;

    @FindBy(xpath = ETA_DATEPICKER_XPATH)
    private WebElement etaDatePicker;

    @FindBy(xpath = ETA_TIME_XPATH)
    private WebElement etaTimeWindow;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = ACTIVE_PANEL_XPATH)
    private WebElement activePanel;


    public HelpdeskResourceETAPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            assertTrue("Accept Job panel not loaded!", driver.findElement(By.xpath(ETA_PANEL_HEADING_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void setAdvisedTo(String to) {
        advisedTo.sendKeys(to);
    }

    public void selectEtaAdvisedToSite(String value) {
        WebElement etaAdvisedToSite = waitForElement(By.xpath(String.format(ETA_ADVISED_TO_SITE_RADIOBUTTON_XPATH,value)), ELEMENT_IS_CLICKABLE);
        etaAdvisedToSite.click();
        waitForAngularRequestsToFinish();
    }

    public void selectEtaDate(String date) throws ParseException {
        etaDateCalenderIcon.click();
        waitForAngularRequestsToFinish();
        WebElement etaDate = waitForElement(By.xpath(String.format(ETA_DATE_XPATH, date)), ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(etaDate);
    }

    public void selectEtaTimeWindow() {
        waitForElement(By.xpath(ETA_TIME_XPATH), ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(etaTimeWindow);
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public String getEtaDate() {
        return etaDatePicker.getAttribute("value");
    }

    public String getEtaTimeWindow() {
        return etaTimeWindow.getText();
    }

    public HelpdeskManageResourcesPanel save() {
        saveButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, HelpdeskManageResourcesPanel.class);
    }
}
