package mercury.pageobject.web.portal.quotes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class JobAwaitingResourceAssignmentPage extends Base_Page<JobAwaitingResourceAssignmentPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Update Job";

    // Page elements

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    // Update Job form
    private static final String UPDATE_JOB_FORM_ID = "EditQuoteJobForm";
    private static final String UPDATE_JOB_FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//*[@id='EditQuoteJobForm']";

    // CORE
    private static final String SCOEP_OF_WORKS_XPATH = UPDATE_JOB_FORM_XPATH + "//*[@id='ScopeOfWorks']";

    private static final String QUOTE_TYPE_OPEX_XPATH = UPDATE_JOB_FORM_XPATH +  "//label[contains(text(),'Please select Quote Type')]/..//label[text()='OPEX']";;
    private static final String QUOTE_TYPE_CAPEX_XPATH = UPDATE_JOB_FORM_XPATH + "//label[contains(text(),'Please select Quote Type')]/..//label[text()='CAPEX']";;
    private static final String QUOTE_PRIORITY_SELECT_XPATH = UPDATE_JOB_FORM_XPATH + "//select[@id='QuotePriorityId']";
    private static final String NUMBER_OF_QUOTES_REQUIRED_SELECT_XPATH =  UPDATE_JOB_FORM_XPATH + "//select[@name='NumberOfQuotesRequired']";

    private static final String RESOURCE_PICKER_XPATH =  UPDATE_JOB_FORM_XPATH + "//div[@id='resource-picker-%s']";
    private static final String RESOURCE_TYPE_XPATH = RESOURCE_PICKER_XPATH + "/descendant::label[contains(text(),'Technician or Contractor?')]/following-sibling::select";
    private static final String SHOW_ALL_TECHNICIANS_LABEL_XPATH = RESOURCE_PICKER_XPATH + "//label[contains(text(),'Show all technicians')]";
    private static final String SHOW_ALL_CONTRACTORS_LABEL_XPATH = RESOURCE_PICKER_XPATH + "//label[contains(text(),'Show all contractors')]";
    private static final String RESOURCE_SELECT_XPATH = RESOURCE_PICKER_XPATH + "//select[@name='QuoteResourceIds']";

    private static final String QUOTE_TYPE_OPEX_VALUE_XPATH = QUOTE_TYPE_OPEX_XPATH + "//preceding-sibling::input[1]";
    private static final String QUOTE_TYPE_CAPEX_VALUE_XPATH = QUOTE_TYPE_CAPEX_XPATH + "//preceding-sibling::input[1]";

    private static final String SAVE_CHANGES_XPATH = UPDATE_JOB_FORM_XPATH + "//input[@id='Save']";


    private static final String DECLINE_INVITATION_HISTORY_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='query-history']";



    @FindBy(id = UPDATE_JOB_FORM_ID)
    private WebElement updateJobForm;

    @FindBy(xpath = SCOEP_OF_WORKS_XPATH)
    private WebElement scopeOFWorks;

    @FindBy(xpath = QUOTE_TYPE_OPEX_XPATH)
    private WebElement opex;

    @FindBy(xpath = QUOTE_TYPE_OPEX_VALUE_XPATH)
    private WebElement opexChecked;

    @FindBy(xpath = QUOTE_TYPE_CAPEX_XPATH)
    private WebElement capex;

    @FindBy(xpath = QUOTE_TYPE_CAPEX_VALUE_XPATH)
    private WebElement capexChecked;

    @FindBy(xpath = QUOTE_PRIORITY_SELECT_XPATH)
    private WebElement quotePriority;

    @FindBy(xpath = NUMBER_OF_QUOTES_REQUIRED_SELECT_XPATH)
    private WebElement numberQuotesRequired;

    @FindBy(xpath = SAVE_CHANGES_XPATH)
    private WebElement saveButton;

    public JobAwaitingResourceAssignmentPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(UPDATE_JOB_FORM_XPATH));
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
        return this.isElementClickable(By.xpath(UPDATE_JOB_FORM_XPATH));
    }

    // Page Interactions
    public void setScopeOFWorks(String keysToSend) {
        scopeOFWorks.sendKeys(keysToSend);
    }

    public void clickOpex() {
        opex.click();
    }

    public void clickCapex() {
        capex.click();
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
        Select resourceSelect = new Select(driver.findElement(By.xpath(String.format(RESOURCE_SELECT_XPATH, resourcePicker))));
        List<WebElement> visibleOptions = resourceSelect.getOptions();

        int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
        resourceSelect.selectByIndex(randomSelection);

        logger.debug("#visibleOptions = " + visibleOptions.size() + ", index = " + randomSelection);
        logger.debug("#visibleOptions = " + visibleOptions.get(randomSelection).getText());
    }

    public void showAllTechnicians(Integer resourcePicker) {
        WebElement showAllTechnicians = driver.findElement(By.xpath(String.format(SHOW_ALL_TECHNICIANS_LABEL_XPATH, String.valueOf(resourcePicker))));
        showAllTechnicians.click();
    }

    public void showAllContractors(Integer resourcePicker) {
        WebElement showAllTechnicians = driver.findElement(By.xpath(String.format(SHOW_ALL_CONTRACTORS_LABEL_XPATH, String.valueOf(resourcePicker))));
        showAllTechnicians.click();
    }

    public JobsAwaitingResourceSelectionPage submitForm() {
        saveButton.click();
        return PageFactory.initElements(driver, JobsAwaitingResourceSelectionPage.class).get();
    }


    // Get values
    public String getScopeOfWorks() {
        return scopeOFWorks.getText();
    }

    public boolean getOpexChecked() {
        return opexChecked.isSelected();
    }

    public String getOpexValue() {
        return opexChecked.getAttribute("value");
    }

    public boolean getCapexChecked() {
        return capexChecked.isSelected();
    }

    public String getCapexValue() {
        return capexChecked.getAttribute("value");
    }

    public String getQuotePriorityText() {
        Select drop = new Select(quotePriority);
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public String getQuotePriorityValue() {
        Select drop = new Select(quotePriority);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }

    public String getNumberOfQuotesText() {
        Select drop = new Select(numberQuotesRequired);
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
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
        Select drop = new Select(driver.findElement(By.xpath(String.format(RESOURCE_SELECT_XPATH, String.valueOf(resourcePicker)))));
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public List<String> getAllResources(Integer resourcePicker) {
        List<String> allResources = new ArrayList<>();
        Select resourceSelect = new Select(driver.findElement(By.xpath(String.format(RESOURCE_SELECT_XPATH, String.valueOf(resourcePicker)))));
        List<WebElement> visibleOptions = resourceSelect.getOptions();
        for (WebElement option : visibleOptions)
        {
            String optionText = option.getText();
            if(optionText.length()>0) {
                allResources.add(option.getText().trim());
            }
        }
        return allResources;
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(DECLINE_INVITATION_HISTORY_XPATH);
    }

    public Grid getGrid() {
        return GridHelper.getGrid(DECLINE_INVITATION_HISTORY_XPATH);
    }

}
