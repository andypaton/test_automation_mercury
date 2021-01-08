package mercury.pageobject.web.portal.jobs;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class PortalUpdateJobETA extends Base_Page<PortalUpdateJobETA> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Update ETA";

    // Main content

    private static final String BODY_CONTENT_XPATH =  ".//div[contains(@class,'body-content')]";
    private static final String BODY_CONTAINER_XPATH =  BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class, 'body-container')]";
    private static final String PAGE_HEADER_XPATH = BODY_CONTAINER_XPATH + "//h1";
    private static final String RESOURCE_ASSIGNMENT_CONTAINER_XPATH =  BODY_CONTAINER_XPATH + "//*[@id='resourceAssignmentUpdateUIRoot']";
    private static final String UPDATE_ETA_FORM_XPATH = RESOURCE_ASSIGNMENT_CONTAINER_XPATH + "//form[@id='updateEtaForm']";

    // Core components
    private static final String CONTROL_TOOLTIP_ERROR_XPATH = UPDATE_ETA_FORM_XPATH + "//label[contains(text(), '%s')]/..//span[contains(@class, 'k-tooltip')]";

    private static final String ETA_DATE_DROPDOWN_XPATH = UPDATE_ETA_FORM_XPATH + "//label[contains(text(), 'ETA')]/.." + DROPDOWN_CALENDAR_XPATH;
    private static final String ETA_DATE_PICKER_CONTAINER_XPATH = "//div[@class='k-animation-container']//div[contains(@class, 'k-calendar')]";
    private static final String ETA_WINDOW_DROPDOWN_XPATH = UPDATE_ETA_FORM_XPATH + "//label[contains(text(), 'ETA Window')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String ETA_ADVISED_QUESTION_YES_XPATH = UPDATE_ETA_FORM_XPATH + "//label[contains(text(),'%s')]/..//label[text()='Yes']";
    private static final String ETA_ADVISED_QUESTION_NO_XPATH = UPDATE_ETA_FORM_XPATH + "//label[contains(text(),'%s')]/..//label[text()='No']";
    private static final String ETA_ADVISED_TO_XPATH = UPDATE_ETA_FORM_XPATH + "//input[@id='advisedTo']";

    private static final String UPDATE_ETA_BUTTON_XPATH = UPDATE_ETA_FORM_XPATH + "//input[contains(@class, 'btn') and contains(@class, 'btn-primary') and contains(@value, 'Update ETA')]";

    @FindBy(xpath = PAGE_HEADER_XPATH)
    private WebElement pageHeaderTest;

    @FindBy(xpath = UPDATE_ETA_FORM_XPATH)
    private WebElement updateETAForm;

    @FindBy(xpath = ETA_DATE_DROPDOWN_XPATH)
    private WebElement eTADatePicker;

    @FindBy(xpath = ETA_DATE_PICKER_CONTAINER_XPATH)
    private WebElement eTADatePickerContainer;

    @FindBy(xpath = ETA_WINDOW_DROPDOWN_XPATH)
    private WebElement eTAWindow;

    @FindBy(xpath = ETA_ADVISED_QUESTION_YES_XPATH)
    private WebElement eTAAdvisedYes;

    @FindBy(xpath = ETA_ADVISED_QUESTION_NO_XPATH)
    private WebElement eTAAdvisedNo;

    @FindBy(xpath = ETA_ADVISED_TO_XPATH)
    private WebElement advisedTo;

    @FindBy(xpath = UPDATE_ETA_BUTTON_XPATH)
    private WebElement updateETA;

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(UPDATE_ETA_FORM_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public String getPageHeaderText() {
        return pageHeaderTest.getText();
    }

    public String getPageTitle() {
        return super.getPageTitle();
    }

    public PortalUpdateJobETA(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public void selectETADate(String etaDate) throws InterruptedException {
        waitForElement(By.xpath(ETA_DATE_DROPDOWN_XPATH), State.ELEMENT_IS_VISIBLE);
        this.eTADatePicker.click();
        waitForAnimation(1);
        List<WebElement> days = eTADatePickerContainer.findElements(By.tagName("td"));
        for (WebElement day : days)
        {
            if (day.getText().trim().length()>0) {
                List<WebElement> links = day.findElements(By.tagName("a"));
                for (WebElement alink : links)
                {
                    if ( alink.getAttribute("title").equalsIgnoreCase(etaDate)){
                        alink.click();
                        break;
                    }
                }
            }
        }
        waitForAngularRequestsToFinish();
    }

    public String selectRandomETAWindow() {
        waitForKendoLoadingToComplete();
        eTAWindow.click();
        waitForAnimation(1);
        return selectRandomVisibleDropdownOption(); //(eTAWindow);
    }

    public void clickYes(String question) {
        waitForAnimation();
        WebElement adviceETAYes = waitForElement(By.xpath(String.format(ETA_ADVISED_QUESTION_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        adviceETAYes.click();
    }

    public void clickNo(String question) {
        waitForAnimation();
        WebElement adviceETANo = waitForElement(By.xpath(String.format(ETA_ADVISED_QUESTION_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        adviceETANo.click();
    }

    public void enterAdvisedTo(String advisedTo) {
        this.advisedTo.sendKeys(advisedTo);
    }

    public UpdateSavedPage submitForm() {
        waitForAngularRequestsToFinish();
        waitForElement(By.xpath(UPDATE_ETA_BUTTON_XPATH), State.ELEMENT_IS_CLICKABLE);
        updateETA.click();
        return PageFactory.initElements(driver, UpdateSavedPage.class).get();
    }

    public boolean controlToolTipIsDisplayed(String question) {
        return isElementVisible(By.xpath(String.format(CONTROL_TOOLTIP_ERROR_XPATH, question)));
    }

}
