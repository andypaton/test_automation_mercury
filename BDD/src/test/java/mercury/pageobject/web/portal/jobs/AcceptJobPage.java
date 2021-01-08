package mercury.pageobject.web.portal.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

public class AcceptJobPage extends Base_Page<AcceptJobPage>  {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Job Details";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";


    // Main content
    private static final String ACCEPT_FORM_XPATH = PAGE_BODY_CONTAINER_XPATH + "//form[@id='acceptOrDeclineForm']";
    private static final String ETA_DROPDOWN_XPATH = ACCEPT_FORM_XPATH + "//label[contains(text(), 'ETA')]/.." + DROPDOWN_CALENDAR_XPATH;
    private static final String ETA_WINDOW_XPATH = ACCEPT_FORM_XPATH + "//label[contains(text(), 'ETA')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String ETA_ADVISED_QUESTION_YES_XPATH = ACCEPT_FORM_XPATH + "//label[contains(text(),'%s')]/..//label[text()='Yes']";
    private static final String ETA_ADVISED_QUESTION_NO_XPATH = ACCEPT_FORM_XPATH + "//label[contains(text(),'%s')]/..//label[text()='No']";

    private static final String CONTACTOR_REFERENCE_XPATH = ACCEPT_FORM_XPATH + "//*[@id='ContractorReference']";

    private static final String ADVISED_TO_XPATH = ACCEPT_FORM_XPATH + "//*[@id='advisedTo']";

    private static final String SAVE_BUTTON_XPATH = ACCEPT_FORM_XPATH + "//input[contains(@type,'submit') and contains(@class,'btn-primary') and contains(@class,'btn-primary')]";

    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = ACCEPT_FORM_XPATH)
    private WebElement acceptJobForm;

    @FindBy(xpath = ETA_DROPDOWN_XPATH)
    private WebElement etaDate;

    @FindBy(xpath = ETA_WINDOW_XPATH)
    private WebElement etaWindow;

    @FindBy(xpath = ADVISED_TO_XPATH)
    private WebElement advisedTo;

    @FindBy(xpath = CONTACTOR_REFERENCE_XPATH)
    private WebElement contractorReference;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement saveButton;

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(ETA_DROPDOWN_XPATH));
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

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public AcceptJobPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    public void enterETADate(String etaDate) throws InterruptedException {
        selectCalendarDate(this.etaDate, etaDate);
    }

    public String selectRandomETAWindow() {
        etaWindow.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public void clickAdviseYes(String question) {
        WebElement yes = waitForElement(By.xpath(String.format(ETA_ADVISED_QUESTION_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        yes.click();
        waitForAngularRequestsToFinish();
    }

    public void clickAdviseNo(String question) {
        WebElement no = waitForElement(By.xpath(String.format(ETA_ADVISED_QUESTION_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        no.click();
        waitForAngularRequestsToFinish();
    }

    public void enterAdvisedTo(String advisedTo) {
        this.advisedTo.sendKeys(advisedTo);
    }

    public void  setContractorReference(String contractorReferenceNumber) throws InterruptedException{
        this.contractorReference.sendKeys(contractorReferenceNumber);
    }

    public void save() {
        saveButton.click();
        this.waitForAngularRequestsToFinish();
    }
}
