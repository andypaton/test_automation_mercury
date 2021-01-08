package mercury.pageobject.web.portal.quotes;

import static mercury.helpers.StringHelper.normalize;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;
import mercury.helpers.State;

public class QuoteJobEdit extends Base_Page<QuoteJobEdit>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Update Job";

    // Page elements
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    // Update Job form
    private static final String EDIT_JOB_FORM_ID = "EditQuoteJobForm";
    private static final String EDIT_JOB_FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//*[@id='EditQuoteJobForm']";

    // CORE
    private static final String ALERT_XPATH = "/parent::div/preceding::div[contains(@class,'alert') and contains(@class,'alert-info')]";

    private static final String SCOPE_OF_WORKS_XPATH = EDIT_JOB_FORM_XPATH + "//*[@id='ScopeOfWorks']";
    private static final String SCOPE_OF_WORKS_ALERT_XPATH = SCOPE_OF_WORKS_XPATH + ALERT_XPATH;

    private static final String QUOTE_TYPE_OPEX_XPATH = EDIT_JOB_FORM_XPATH +  "//label[contains(text(),'Please select Quote Type')]/..//label[text()='%s']";;
    private static final String QUOTE_TYPE_CAPEX_XPATH = EDIT_JOB_FORM_XPATH + "//label[contains(text(),'Please select Quote Type')]/..//label[text()='CAPEX']";;
    private static final String QUOTE_PRIORITY_SELECT_XPATH = EDIT_JOB_FORM_XPATH + "//select[@id='QuotePriorityId']";
    private static final String QUOTE_PRIORITY_ALERT_XPATH = QUOTE_PRIORITY_SELECT_XPATH + ALERT_XPATH;

    private static final String NUMBER_OF_QUOTES_REQUIRED_SELECT_XPATH =  EDIT_JOB_FORM_XPATH + "//select[@name='NumberOfQuotesRequired']";

    private static final String RESOURCE_PICKER_XPATH =  EDIT_JOB_FORM_XPATH + "//div[@id='resource-picker-%s']";
    private static final String RESOURCE_TYPE_XPATH = RESOURCE_PICKER_XPATH + "/descendant::label[contains(text(),'Technician or Contractor?')]/following-sibling::select";
    private static final String SHOW_ALL_TECHNICIANS_XPATH = RESOURCE_PICKER_XPATH + "//input[@id='show-city-resources-%s']";
    private static final String SHOW_ALL_TECHNICIANS_LABEL_XPATH = RESOURCE_PICKER_XPATH + "//label[contains(text(),'Show all technicians')]";
    private static final String SHOW_ALL_CONTRACTORS_XPATH = RESOURCE_PICKER_XPATH + "//input[@id='show-alternative-vendors-%s']";
    private static final String SHOW_ALL_CONTRACTORS_LABEL_XPATH = RESOURCE_PICKER_XPATH + "//label[contains(text(),'Show all contractors')]";
    private static final String RESOURCE_SELECT_XPATH = RESOURCE_PICKER_XPATH + "//select[@name='QuoteResourceIds']";

    private static final String QUOTE_TYPE_OPEX_VALUE_XPATH = QUOTE_TYPE_OPEX_XPATH + "//preceding-sibling::input[1]";
    private static final String QUOTE_TYPE_CAPEX_VALUE_XPATH = QUOTE_TYPE_CAPEX_XPATH + "//preceding-sibling::input[1]";
    private static final String FUNDING_ROUTE_CONTAINER_XPATH = EDIT_JOB_FORM_XPATH + "//div[@id='funding-routes-container']";
    private static final String FUNDING_ROUTE_OPTIONS_XPATH = FUNDING_ROUTE_CONTAINER_XPATH + "//input";

    private static final String SAVE_CHANGES_XPATH = EDIT_JOB_FORM_XPATH + "//input[@id='Save']";

    private static final String MULTI_QUOTE_BYPASS_XPATH = EDIT_JOB_FORM_XPATH + "//input[@id='MultiQuoteBypassStart']";
    private static final String MULTI_QUOTE_BYPASS_QUESTONS_XPATH = EDIT_JOB_FORM_XPATH + "//div[@id='MultiQuoteBypassQuestions']";
    private static final String MULTI_QUOTE_BYPASS_ALERT_XPATH = MULTI_QUOTE_BYPASS_QUESTONS_XPATH + "//div[contains(@class,'alert') and contains(@class,'alert-info')]";
    private static final String MULTI_QUOTE_BYPASS_REASON_XPATH = MULTI_QUOTE_BYPASS_QUESTONS_XPATH +  "//select[@id='MultiQuoteBypassReason']";
    private static final String MULTI_QUOTE_BYPASS_NOTE_XPATH = MULTI_QUOTE_BYPASS_QUESTONS_XPATH +  "//textarea[@id='MultiQuoteBypassNote']";

    private static final String MULTI_QUOTE_BYPASS_RECOMMENDATION_QUESTONS_XPATH  = EDIT_JOB_FORM_XPATH + "//div[@id='MultiQuoteBypassRecommendationQuestions']";

    private static final String CONFIRM_QUOTE_RECOMMENDATION_FOR_APPROVAL_XPATH = MULTI_QUOTE_BYPASS_RECOMMENDATION_QUESTONS_XPATH + "//input[@id='IsBypassQuoteRecommendationConfirmed']";
    private static final String CONFIRM_QUOTE_RECOMMENDATION_FOR_APPROVAL_LABEL_XPATH = MULTI_QUOTE_BYPASS_RECOMMENDATION_QUESTONS_XPATH + "//label[@for='IsBypassQuoteRecommendationConfirmed']";
    private static final String CONFIRM_QUOTE_RECOMMENDATION_FOR_APPROVAL_NOTES_XPATH = MULTI_QUOTE_BYPASS_RECOMMENDATION_QUESTONS_XPATH + "//textarea[@id='MultiQuoteBypassQuoteRecommendationNote']";

    //Select dropdown = new Select(driver.findElement(By.id("identifier")))

    private static final String DECLINE_INVITATION_HISTORY_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='query-history']";
    private static final String JOB_DETAILS_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'job-detail__container') and contains(@class,'read-only')]";

    private static final String WARNING_ALERT_XPATH = "//div[contains(@class,'sweet-alert')]";
    private static final String WARNING_MESSAGE_XPATH = WARNING_ALERT_XPATH + "//p[contains(text(),'Changing the Budget')]";
    private static final String WARNING_MESSAGE_OK_BUTTON_XPATH = WARNING_ALERT_XPATH + "//button[@class='confirm']";

    @FindBy(id = EDIT_JOB_FORM_ID)
    private WebElement updateJobForm;

    @FindBy(xpath = SCOPE_OF_WORKS_XPATH)
    private WebElement scopeOfWorks;

    @FindBy (xpath = SCOPE_OF_WORKS_ALERT_XPATH)
    private WebElement scopeOFWorksAlert;

    @FindBy(xpath = QUOTE_TYPE_OPEX_XPATH)
    private WebElement opex;

    @FindBy(xpath = QUOTE_TYPE_OPEX_VALUE_XPATH)
    private WebElement opexChecked;

    @FindBy(xpath = QUOTE_TYPE_CAPEX_XPATH)
    private WebElement capex;

    @FindBy(xpath = QUOTE_TYPE_CAPEX_VALUE_XPATH)
    private WebElement capexChecked;

    @FindBy(xpath = QUOTE_PRIORITY_ALERT_XPATH)
    private WebElement quotePriorityAlert;

    @FindBy(xpath = QUOTE_PRIORITY_SELECT_XPATH)
    private WebElement quotePriority;

    @FindBy(xpath = NUMBER_OF_QUOTES_REQUIRED_SELECT_XPATH)
    private WebElement numberQuotesRequired;

    @FindBy(xpath = MULTI_QUOTE_BYPASS_XPATH)
    private WebElement multiQuoteBypass;

    @FindBy(xpath = MULTI_QUOTE_BYPASS_REASON_XPATH)
    private WebElement multiQuoteBypassReason;

    @FindBy(xpath = MULTI_QUOTE_BYPASS_NOTE_XPATH)
    private WebElement multiQuoteBypassNote;

    @FindBy(xpath = MULTI_QUOTE_BYPASS_ALERT_XPATH)
    private WebElement multiQuoteBypassAlert;

    @FindBy(xpath = CONFIRM_QUOTE_RECOMMENDATION_FOR_APPROVAL_XPATH)
    private WebElement confirmQuoteRecommendationForApproval;

    @FindBy(xpath = CONFIRM_QUOTE_RECOMMENDATION_FOR_APPROVAL_LABEL_XPATH)
    private WebElement confirmQuoteRecommendationForApprovalLabel;

    @FindBy(xpath = CONFIRM_QUOTE_RECOMMENDATION_FOR_APPROVAL_NOTES_XPATH)
    private WebElement confirmQuoteRecommendationForApprovalNotes;

    @FindBy(xpath = SAVE_CHANGES_XPATH)
    private WebElement saveButton;

    @FindBy(xpath = JOB_DETAILS_XPATH)
    private WebElement jobDetails;

    @FindBy(xpath = WARNING_MESSAGE_OK_BUTTON_XPATH)
    private WebElement warningMessageOKButton;

    public QuoteJobEdit(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(EDIT_JOB_FORM_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(EDIT_JOB_FORM_XPATH));
    }

    // Page Interactions
    public void setScopeOFWorks(String keysToSend) {
        scopeOfWorks.sendKeys(keysToSend);
    }

    public boolean getScopeOfWorksEditState() {
        return scopeOfWorks.isEnabled();
    }

    public void clickOpex(String quoteType) {
        WebElement element = waitForElement(By.xpath(String.format(QUOTE_TYPE_OPEX_XPATH, quoteType)), State.ELEMENT_IS_CLICKABLE);
        element.click();
        this.waitForAngularRequestsToFinish();
    }

    public void clickCapex() {
        capex.click();
        this.waitForAngularRequestsToFinish();
    }

    public void selectQuotePriortyByText(String option) {
        Select drop = new Select(quotePriority);
        drop.selectByVisibleText(option);
    }

    public void selectRandomQuotePriortyByText() {
        Select drop = new Select(quotePriority );
        waitUntilSelectOptionsPopulated(drop);
        List<WebElement> visibleOptions = drop.getOptions();

        int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
        drop.selectByIndex(randomSelection);
    }

    public void selectQuotePriortyByValue(String option) {
        Select drop = new Select(quotePriority );
        drop.selectByValue(option);
    }

    public void selectNumberOfQuotesByText(String option) {
        Select drop = new Select(numberQuotesRequired );
        drop.selectByVisibleText(option);
    }

    public void selectNumberOfQuotesByValue(String option) {
        Select drop = new Select(numberQuotesRequired );
        drop.selectByValue(option);
        waitForAngularRequestsToFinish();
    }

    public void selectResourceType(Integer resourcePicker, String resource) {
        Select resourceTypeSelect = new Select(driver.findElement(By.xpath(String.format(RESOURCE_TYPE_XPATH, String.valueOf(resourcePicker)))));
        resourceTypeSelect.selectByVisibleText(resource);
    }

    public void selectRandomResourceType(Integer resourcePicker) {
        Select resourceTypeSelect = new Select(driver.findElement(By.xpath(String.format(RESOURCE_TYPE_XPATH, String.valueOf(resourcePicker)))));
        List<WebElement> visibleOptions = resourceTypeSelect.getOptions();

        int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
        resourceTypeSelect.selectByIndex(randomSelection);
    }

    public void selectRandomResource(String resourcePicker) {
        this.waitForAngularRequestsToFinish();
        Select resourceSelect = new Select(driver.findElement(By.xpath(String.format(RESOURCE_SELECT_XPATH, resourcePicker))));
        waitUntilSelectOptionsPopulated(resourceSelect);
        List<WebElement> visibleOptions = resourceSelect.getOptions();

        int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
        resourceSelect.selectByIndex(randomSelection);

        logger.debug("#visibleOptions = " + visibleOptions.size() + ", index = " + randomSelection);
        logger.debug("#visibleOptions = " + visibleOptions.get(randomSelection).getText());
    }

    public void showAllTechnicians(Integer resourcePicker) {
        this.waitForAngularRequestsToFinish();
        WebElement showAllTechnicians = driver.findElement(By.xpath(String.format(SHOW_ALL_TECHNICIANS_LABEL_XPATH, String.valueOf(resourcePicker))));
        showAllTechnicians.click();
    }

    public void showAllContractors(Integer resourcePicker) {
        this.waitForAngularRequestsToFinish();
        WebElement showAllTechnicians = driver.findElement(By.xpath(String.format(SHOW_ALL_CONTRACTORS_LABEL_XPATH, String.valueOf(resourcePicker))));
        showAllTechnicians.click();
    }

    public void submitForm() {
        saveButton.click();
        waitForAngularRequestsToFinish();
    }


    // Get values
    public String getScopeOfWorks() {
        return scopeOfWorks.getText();
    }

    public boolean getOpexChecked(String quoteType) {
        WebElement element = waitForElement(By.xpath(String.format(QUOTE_TYPE_OPEX_VALUE_XPATH, quoteType)), State.ELEMENT_IS_VISIBLE);
        return element.isSelected();
    }

    public String getOpexValue(String quoteType) {
        By by = By.xpath(String.format(QUOTE_TYPE_OPEX_VALUE_XPATH,quoteType));
        return driver.findElement(by).getAttribute("value");
    }

    public boolean getCapexChecked() {
        return capexChecked.isSelected();
    }

    public String getCapexValue() {
        return capexChecked.getAttribute("value");
    }

    public String getFundintRoute() {
        List<WebElement> fundingRoutes = driver.findElements(By.xpath(FUNDING_ROUTE_OPTIONS_XPATH));
        for (WebElement fundingRoute : fundingRoutes)
        {
            if(fundingRoute.isSelected()) {
                return fundingRoute.getAttribute("value");
            }
        }
        return null;
    }


    public String getQuotePriorityText() {
        return getSelectSelectedText(quotePriority);
    }

    public String getQuotePriorityValue() {
        return getSelectSelectedValue(quotePriority);
    }

    public boolean getNumberOfQuotesEditState() {
        return numberQuotesRequired.isEnabled();
    }

    public String getNumberOfQuotesText() {
        return getSelectSelectedText(numberQuotesRequired);
    }

    public String getNumberOfQuotesValue() {
        return getSelectSelectedValue(numberQuotesRequired);
    }

    public List<Integer> getNumberOfQuotesRequiredOptions() {
        List<Integer> allOptions = new ArrayList<>();
        Select drop = new Select(numberQuotesRequired);
        List<WebElement> visibleOptions = drop.getOptions();
        for (WebElement option : visibleOptions)
        {
            String optionText = option.getText();
            if(optionText.length()>0) {
                allOptions.add(Integer.valueOf(option.getText().trim()));
            }
        }
        return allOptions;
    }

    public Integer getNumberOfResources(Integer resourcePicker) {
        Select resourceSelect = new Select(driver.findElement(By.xpath(String.format(RESOURCE_SELECT_XPATH, String.valueOf(resourcePicker)))));
        List<WebElement> visibleOptions = resourceSelect.getOptions();
        return visibleOptions.size();
    }

    public String getResourceType(Integer resourcePicker) {
        Select drop = new Select(driver.findElement(By.xpath(String.format(RESOURCE_TYPE_XPATH, String.valueOf(resourcePicker)))));
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public String getResource(Integer resourcePicker) {
        this.waitForAngularRequestsToFinish();
        Select drop = new Select(driver.findElement(By.xpath(String.format(RESOURCE_SELECT_XPATH, String.valueOf(resourcePicker)))));
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public boolean getAllContactorsChecked(Integer resourcePicker) {
        WebElement showAllContractors = driver.findElement(By.xpath(String.format(SHOW_ALL_CONTRACTORS_XPATH, String.valueOf(resourcePicker), String.valueOf(resourcePicker))));
        return showAllContractors.isSelected();
    }

    public boolean getAllTechniciansChecked(Integer resourcePicker) {
        WebElement showAllTechnicians = driver.findElement(By.xpath(String.format(SHOW_ALL_TECHNICIANS_XPATH, String.valueOf(resourcePicker), String.valueOf(resourcePicker))));
        return showAllTechnicians.isSelected();
    }

    public List<String> getAllResources(Integer resourcePicker) {
        List<String> allResources = new ArrayList<>();
        Select resourceSelect = new Select(driver.findElement(By.xpath(String.format(RESOURCE_SELECT_XPATH, String.valueOf(resourcePicker)))));
        List<WebElement> visibleOptions = resourceSelect.getOptions();
        for (WebElement option : visibleOptions)
        {
            String optionText = option.getText();
            if(optionText.length()>0) {
                //                allResources.add(normalize(option.getText().trim().replaceAll(" \\(.*\\)","").replaceAll(" \\(.*\\)","")));
                allResources.add(normalize(option.getText().trim()));
                //                allResources.add(normalize(option.getText().trim().replaceAll(" \\(MST.*\\)","").replaceAll(" \\(RHVAC.*\\)","")));
            }
        }
        return allResources;
    }

    public boolean isScopeOfWorksAlertDisplayed() {
        return isElementVisible(By.xpath(SCOPE_OF_WORKS_ALERT_XPATH));
    }

    public boolean isQuotePriorityAlertDisplayed() {
        return isElementVisible(By.xpath(QUOTE_PRIORITY_ALERT_XPATH));
    }

    public boolean isBypassMultiQuoteButtonDisplayed() {
        return isElementVisible(By.xpath(MULTI_QUOTE_BYPASS_XPATH));
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(DECLINE_INVITATION_HISTORY_XPATH);
    }

    public Grid getGrid() {
        return GridHelper.getGrid(DECLINE_INVITATION_HISTORY_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(DECLINE_INVITATION_HISTORY_XPATH, 1);
    }

    public void byPassMultiQuote() {
        multiQuoteBypass.click();
    }

    public void selectRandomMultiQuoteByPassReason() {
        selectRandomOptionFromSelect(multiQuoteBypassReason);
    }

    public boolean getByPassReasonEditState() {
        return multiQuoteBypassReason.isEnabled();
    }

    public void enterMultiQuoteByPassNotes(String notes) {
        this.multiQuoteBypassNote.sendKeys(notes);
    }

    public boolean getMultiQuoteByPassNotesEditState() {
        return multiQuoteBypassNote.isEnabled();
    }

    public void confirmRecommendQuoteForApproval() {
        confirmQuoteRecommendationForApprovalLabel.click();
    }

    public boolean getConfirmRecommendQuoteForApprovalEditState() {
        return confirmQuoteRecommendationForApproval.isEnabled();
    }

    public void enterConfirmRecommendationNotes(String notes) {
        confirmQuoteRecommendationForApprovalNotes.sendKeys(notes);
    }

    public boolean getConfirmRecommendationNotesEditState() {
        return confirmQuoteRecommendationForApprovalNotes.isEnabled();
    }

    public boolean isJobDetailViewOnly() {
        return jobDetails.isDisplayed();
    }

    public Boolean isBudgetWarningMessageDisplayed() {
        try {
            return isElementPresent(By.xpath(WARNING_MESSAGE_XPATH));
        } catch (ElementNotVisibleException ex) {
            return false;
        }
    }

    public void confirmBudgetWarningMessage() {
        warningMessageOKButton.click();
        waitForAngularRequestsToFinish();
    }
}
