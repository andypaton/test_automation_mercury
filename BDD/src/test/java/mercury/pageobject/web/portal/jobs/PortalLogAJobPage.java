package mercury.pageobject.web.portal.jobs;

import static mercury.helpers.State.ELEMENT_IS_VISIBLE;
import static mercury.helpers.Constants.MAX_ATTEMPTS;
import static mercury.helpers.Globalisation.FULL_DATE;
import static mercury.helpers.Globalisation.SHORT_DATE;

import java.text.ParseException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.DateHelper;
import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.portal.NewSiteContactPage;


public class PortalLogAJobPage extends Base_Page<PortalLogAJobPage>  {
    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Log a Job";

    // Main Body
    private static final String LOG_JOB_BODY_CSS = "div.job-log";
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";

    private static final String LOG_JOB_FORM_XPATH = PAGE_BODY_CONTAINER_XPATH + "//form[contains(@name,'logjobForm')]";

    // Controls
    //    private static final String REQUEST_A_QUOTE_CSS = LOG_JOB_BODY_CSS + " #CreateQuote ~ label";
    private static final String CREATE_QUOTE_XPATH = "//input[@id = 'CreateQuote']";

    // Core
    private static final String SITE_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH +  "//label[contains(text(), 'Site')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ASSET_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH +  "//label[contains(text(), 'Asset (Optional)')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String CLASSIFICATION_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH +  "//label[contains(text(), 'Classification')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String ASSET_LOCATION_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH +  "//label[contains(text(), 'Asset Location')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String FAULT_TYPE_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH +  "//label[contains(text(), 'Fault Type')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String SITE_CONTACT_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH +  "//label[contains(text(), 'Site Contact')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String RESOURCE_PROFILE_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH +  "//label[contains(text(), 'Select Resource Profile')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String ADD_NEW_CONTACT_BUTTON_XPATH = ".//*[@id='AddContact']";
    private static final String CONTACT_LIST_BOX_CSS = "#contactSelect_listbox";
    
    private static final String SITE_CONTACT_ERROR_MESSAGE_XPATH = LOG_JOB_FORM_XPATH + "//label[contains(@class,'job-contact__name-error')]";

    private static final String FAULT_DESCRIPTION_ID = "Description";

    private static final String RESOURCE_QUESTION_YES_XPATH = "//label[contains(text(),'%s')]/..//label[text()='Yes']";
    private static final String RESOURCE_QUESTION_NO_XPATH = "//label[contains(text(),'%s')]/..//label[text()='No']";

    private static final String QUOTE_TYPE_OPEX_XPATH = "//label[contains(text(),'%s')]/..//label[text()='OPEX']";
    private static final String QUOTE_TYPE_CAPEX_XPATH = "//label[contains(text(),'%s')]/..//label[text()='CAPEX']";
    private static final String RADIO_BUTTON_QUESTION_ANSWER_XPATH = "//label[contains(text(),'%s')]/..//label[text()='CAPEX']";

    private static final String QUOTE_PRIORITY_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH +  "//label[contains(text(), 'Select Priority')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String ETA_DATE_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH + "//label[contains(text(), 'ETA')]/.." + DROPDOWN_CALENDAR_XPATH;
    private static final String ETA_DATE_PICKER_CONTAINER_XPATH = "//ph-date[@k-ng-model='ctrl.etaDate']//input[@id = 'datepicker']";

    private static final String ETA_WINDOW_DROPDOWN_XPATH = LOG_JOB_FORM_XPATH + "//label[contains(text(), 'ETA Window')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;

    private static final String ETA_ADVISED_QUESTION_YES_XPATH = LOG_JOB_FORM_XPATH + "//label[contains(text(),'%s')]/..//label[text()='Yes']";
    private static final String ETA_ADVISED_QUESTION_NO_XPATH = LOG_JOB_FORM_XPATH + "//label[contains(text(),'%s')]/..//label[text()='No']";
    private static final String ETA_ADVISED_TO_XPATH = LOG_JOB_FORM_XPATH + "//input[@id='advisedTo']";

    private static final String FORM_CANCEL_BUTTON_CSS = "form > div.form-group.bottom-buttons > a";
    private static final String FORM_SUBMIT_BUTTON_CSS = "form > div.form-group.bottom-buttons > button";

    private static final String NUMBER_OF_QUOTES_REQUIRED_XPATH = "//select[@name = 'NumberOfQuotesRequired']";
    private static final String INVITATIONS_TO_QUOTE_SECTION_XPATH = "//div[contains(@class, 'invitations-to-quote')]";
    private static final String QUOTE_RESOURCE_TYPE_PICKER_XPATH = INVITATIONS_TO_QUOTE_SECTION_XPATH + "[%d]//select[@id = 'quoteResourceTypePicker']";
    private static final String QUOTE_RESOURCE_PICKER_XPATH = INVITATIONS_TO_QUOTE_SECTION_XPATH + "[%d]//select[@id = 'quoteResourcePicker']";
    private static final String SHOW_ALL_XPATH = INVITATIONS_TO_QUOTE_SECTION_XPATH + "[%d]//input[@type = 'checkbox']";

    private static final String CANCEL_CHANGES_ALERT_XPATH = "//div[contains(@class, 'sweet-alert') and contains(@class, 'visible')]//h2[text() = 'Leaving page']";
    private static final String QUESTION_XPATH = "//label[text() = '%s']";

    //Table
    private static final String GRID_XPATH = "//div[@id='duplicateJobs']";


    @FindBy(css = LOG_JOB_BODY_CSS)
    private WebElement mainBody;

    @FindBy(xpath = LOG_JOB_FORM_XPATH)
    private WebElement logJobForm;

    @FindBy(xpath = SITE_DROPDOWN_XPATH)
    private WebElement site;

    @FindBy(xpath = ASSET_DROPDOWN_XPATH)
    private WebElement asset;

    @FindBy(xpath = CLASSIFICATION_DROPDOWN_XPATH)
    private WebElement classification;

    @FindBy(xpath = ASSET_LOCATION_DROPDOWN_XPATH)
    private WebElement assetLocation;

    @FindBy(xpath = FAULT_TYPE_DROPDOWN_XPATH)
    private WebElement faultType;

    @FindBy(xpath = SITE_CONTACT_DROPDOWN_XPATH)
    private WebElement siteContact;

    @FindBy(id = FAULT_DESCRIPTION_ID)
    private WebElement faultDescription;

    @FindBy(xpath = QUOTE_PRIORITY_DROPDOWN_XPATH)
    private WebElement quotePriority;

    @FindBy(xpath = ADD_NEW_CONTACT_BUTTON_XPATH)
    private WebElement newContactButton;

    @FindBy(xpath = ETA_DATE_PICKER_CONTAINER_XPATH)
    private WebElement eTADatePickerContainer;

    @FindBy(xpath = ETA_DATE_DROPDOWN_XPATH)
    private WebElement etaDate;

    @FindBy(xpath = ETA_WINDOW_DROPDOWN_XPATH)
    private WebElement eTAWindow;

    @FindBy(xpath = ETA_ADVISED_QUESTION_YES_XPATH)
    private WebElement eTAAdvisedYes;

    @FindBy(xpath = ETA_ADVISED_QUESTION_NO_XPATH)
    private WebElement eTAAdvisedNo;

    @FindBy(xpath = ETA_ADVISED_TO_XPATH)
    private WebElement advisedTo;

    @FindBy(css = FORM_CANCEL_BUTTON_CSS)
    private WebElement formCancel;

    @FindBy(css = FORM_SUBMIT_BUTTON_CSS)
    private WebElement formSubmit;

    @FindBy(xpath = NUMBER_OF_QUOTES_REQUIRED_XPATH)
    private WebElement numberOfQuotesRequired;
    
    @FindBy(xpath = SITE_CONTACT_ERROR_MESSAGE_XPATH)
    private WebElement siteContactErrorMessage;


    public PortalLogAJobPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForAngularRequestsToFinish();
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(LOG_JOB_BODY_CSS));
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

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.cssSelector(LOG_JOB_BODY_CSS));
    }

    public PortalLogAJobPage selectSite(String option) throws InterruptedException {
        site.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(option);
        return this;
    }
    
    public PortalLogAJobPage selectRandomSite() throws InterruptedException {
        site.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
        return this;
    }

    public PortalLogAJobPage selectAsset(String option) throws InterruptedException {
        asset.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(option);
        return this;
    }
    
    public PortalLogAJobPage selectRandomAsset() throws InterruptedException {
        asset.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
        return this;
    }

    public PortalLogAJobPage selectAsset_Alt(String option) throws InterruptedException {
        asset.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(option);
        return this;
    }

    public PortalLogAJobPage selectClassification(String option) throws InterruptedException {
        classification.click();
        waitForAngularRequestsToFinish();
        searchAndSelectExactVisibleDropdownOption(option);
        return this;
    }
    
    public PortalLogAJobPage selectRandomClassification() throws InterruptedException {
        classification.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
        return this;
    }
    

    public PortalLogAJobPage selectAssetLocation(String option) throws InterruptedException {
        assetLocation.click();
        waitForAngularRequestsToFinish();
        searchAndSelectExactVisibleDropdownOption(option);
        return this;
    }

    public PortalLogAJobPage selectRandomAssetLocation() throws InterruptedException {
        assetLocation.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
        return this;
    }

    public PortalLogAJobPage selectFaultType(String option) throws InterruptedException {
        faultType.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(option);
        return this;
    }
    
    public PortalLogAJobPage selectRandomFaultType() throws InterruptedException {
        faultType.click();
        waitForAngularRequestsToFinish();
        this.selectRandomVisibleDropdownOption();
        return this;
    }
    

    public PortalLogAJobPage enterFaultDescription(String faultDescription) {
        this.faultDescription.sendKeys(faultDescription);
        return this;
    }

    public PortalLogAJobPage selectRandomSiteContact() {
        POHelper.scrollToElement(siteContact);
        siteContact.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption(1);
        return this;
    }

    public PortalLogAJobPage selectSiteContact(String option) {
        siteContact.click();
        waitForAngularRequestsToFinish();
        searchAndSelectVisibleDropdownOption(option);
        return this;
    }

    /**
     * flaky web element - this is a hack to retry upto MAX_ATEMPTS
     * @param option
     * @param attempt
     * @return
     */
    private PortalLogAJobPage selectResource(String option, int attempt) {
        try {
            WebElement we = driver.findElement(By.xpath(RESOURCE_PROFILE_DROPDOWN_XPATH));
            POHelper.scrollToElement(we);
            we.click();
            waitForAngularRequestsToFinish();
            searchAndSelectVisibleDropdownOption(option);
        } catch (NoSuchElementException e) {
            if (attempt < MAX_ATTEMPTS) return selectResource(option, attempt + 1);
        }
        return this;
    }

    public PortalLogAJobPage selectResource(String option) {
        return selectResource(option, 0);
    }

    public void clickYes(String question) {
        this.waitForKendoLoadingToComplete();
        WebElement yes = waitForElement(By.xpath(String.format(RESOURCE_QUESTION_YES_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        yes.click();
        waitForAngularRequestsToFinish();
    }

    public void clickNo(String question) {
        this.waitForKendoLoadingToComplete();
        WebElement no = waitForElement(By.xpath(String.format(RESOURCE_QUESTION_NO_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        no.click();
        waitForAngularRequestsToFinish();
    }

    public PortalLogAJobPage requestQuote() {
        // use Actions on stubborn checkbox declared inside an input element!
        WebElement we = driver.findElement(By.xpath(CREATE_QUOTE_XPATH));
        POHelper.scrollToElement(we);
        new Actions(driver).click(we).build().perform();
        waitForAngularRequestsToFinish();
        return this;
    }

    public void clickOpex(String question) {
        WebElement yes = waitForElement(By.xpath(String.format(QUOTE_TYPE_OPEX_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        yes.click();
    }

    public void clickCapex(String question) {
        WebElement no = waitForElement(By.xpath(String.format(QUOTE_TYPE_CAPEX_XPATH, question)), State.ELEMENT_IS_CLICKABLE);
        no.click();
    }

    public void clickAnswer(String question, String answer) {
        WebElement element = waitForElement(By.xpath(String.format(RADIO_BUTTON_QUESTION_ANSWER_XPATH, question, answer)), State.ELEMENT_IS_CLICKABLE);
        element.click();
    }

    public PortalLogAJobPage selectQuotePriority(String quotePriority) throws InterruptedException {
        this.quotePriority.click();
        waitForAngularRequestsToFinish();

        WebElement visibleOption = driver.findElement(By.xpath(String.format(DROPDOWN_OPTION_XPATH, quotePriority)));
        POHelper.clickJavascript(visibleOption);
        return this;
    }


    public void selectRandomQuotePriority() {
        quotePriority.click();
        waitForAngularRequestsToFinish();
        selectRandomVisibleDropdownOption();
    }

    public JobLoggedConfigmationPage submitFormSuccess() throws InterruptedException {
        formSubmit.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, JobLoggedConfigmationPage.class).get();
    }

    public void submitFormCancel() throws InterruptedException {
        formCancel.click();
        waitForAngularRequestsToFinish();
        waitForElement(By.xpath(CANCEL_CHANGES_ALERT_XPATH), ELEMENT_IS_VISIBLE);
    }

    public NewSiteContactPage addNewContact() {
        POHelper.scrollToElement(newContactButton);
        this.waitForElement(By.xpath(".//*[@id='AddContact']"), ELEMENT_IS_VISIBLE);
        POHelper.isLoaded().isElementIsClickable(By.xpath(".//*[@id='AddContact']"));
        newContactButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, NewSiteContactPage.class).get();
    }

    public Boolean SiteHasContacts() {
        List<WebElement> contacts = driver.findElement(By.cssSelector(CONTACT_LIST_BOX_CSS)).findElements(By.tagName("li"));
        return (contacts.size()>0);
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public PortalLogAJobPage tableSort(String columnDataField) {
        GridHelper.tableSort(GRID_XPATH, columnDataField);
        return this;
    }


    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    // update ETA
    public void selectETADate(String etaDate) throws InterruptedException, ParseException {
        // leaving commented section as alternative way to select ETA date ...
        //        waitForAngularRequestsToFinish();
        //        waitForElement(By.xpath(ETA_DATE_DROPDOWN_XPATH), State.ELEMENT_IS_VISIBLE);
        //        selectCalendarDate(this.etaDate, etaDate);
        //        waitForAngularRequestsToFinish();

        enterCalendarDate(eTADatePickerContainer, DateHelper.convert(etaDate, FULL_DATE, SHORT_DATE));
    }

    public void selectRandomETAWindow() {
        waitForKendoLoadingToComplete();
        eTAWindow.click();
        waitForAnimation(1);
        selectRandomVisibleDropdownOption();
    }

    public void enterAdvisedTo(String advisedTo) {
        this.advisedTo.sendKeys(advisedTo);
    }

    public boolean isInvitationsToQuoteDisplayed() {
        return isElementPresent(By.xpath(INVITATIONS_TO_QUOTE_SECTION_XPATH));
    }

    public int getNumberOfQuotesRequired() {
        return Integer.valueOf(numberOfQuotesRequired.getAttribute("value"));
    }

    public void selectNumberOfQuotesRequired(int i) {
        WebElement we = driver.findElement(By.xpath(NUMBER_OF_QUOTES_REQUIRED_XPATH));
        selectOptionFromSelect(we, String.valueOf(i)); 
    }

    public String selectRandomResourceTypeToQuote(int i) {
        String xpath = String.format(QUOTE_RESOURCE_TYPE_PICKER_XPATH, i);
        WebElement we = driver.findElement(By.xpath(xpath));
        return selectRandomOptionFromSelect(we);
    }

    public String selectRandomResourceToQuote(int i) {
        String xpath = String.format(QUOTE_RESOURCE_PICKER_XPATH, i);
        WebElement we = driver.findElement(By.xpath(xpath));
        return selectRandomOptionFromSelect(we);
    }

    public void showAllResourcesToQuote(int i) {
        String xpath = String.format(SHOW_ALL_XPATH, i);
        WebElement we = driver.findElement(By.xpath(xpath));
        POHelper.clickJavascript(we);
    }

    public boolean isLeavingPageDisplayed() {
        return isElementVisible(By.xpath(CANCEL_CHANGES_ALERT_XPATH));
    }
    
    public boolean isSiteContactErrorMessageDisplayed() {
        return isElementVisible(By.xpath(SITE_CONTACT_ERROR_MESSAGE_XPATH));
    }

    public boolean isQuestionDisplayed(String question) {
        return isElementVisible(By.xpath(String.format(QUESTION_XPATH, question)));
    }

    public void selectPotentialInsuranceQuote(boolean isPotentialInsuranceQuote) {
        String id = isPotentialInsuranceQuote ? "isPotentialInsuranceYes" : "isPotentialInsuranceNo";
        WebElement we = driver.findElement(By.id(id));
        POHelper.clickJavascript(we);
    }
}
