package mercury.pageobject.web.portal.fundingrequests;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class InitialFundingRequestsApprovalPage extends Base_Page<InitialFundingRequestsApprovalPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Jobs Awaiting Initial Funding Request Approval";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";



    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE + "')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class, 'main-content')]";

    private static final String FUNDING_REQUEST_DETAILS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//h3[contains(text(), 'Funding Request Details')]/..//div[@class='resource-quote-detail__resource-quote-info']";
    private static final String FUNDING_REQUEST_FIELD_DETAIL_XPATH = FUNDING_REQUEST_DETAILS_XPATH + "//label[contains(text(), '%s')]/following-sibling::div";

    private static final String RESOURCE_ASSIGNMENTS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//h3[contains(text(), 'All Resource Assignments')]/..//div[@class='resource-quote-detail__resource-quote-info']";
    private static final String RESOURCE_ASSIGNMENTS_FIELD_DETAIL_XPATH = RESOURCE_ASSIGNMENTS_XPATH + "//label[contains(text(), '%s')]/following-sibling::div";

    private static final String DECISION_CONTAINER_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'resource-quote-detail__resource-quote-input')]";
    private static final String REJECT_RADIO_BUTTON_XPATH = DECISION_CONTAINER_XPATH + "//label[contains(text(), 'Reject')]";
    private static final String APPROVE_RADIO_BUTTON_XPATH = DECISION_CONTAINER_XPATH + "//label[contains(text(), 'Approve')]";

    private static final String REJECT_FUNDING_REQUEST_CONTAINER_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'reject-funding-request')]";
    private static final String REJECTION_REASON_XPATH = REJECT_FUNDING_REQUEST_CONTAINER_XPATH + "//select[@name='rejectionReasons']";
    private static final String REJECTION_NOTES_XPATH = REJECT_FUNDING_REQUEST_CONTAINER_XPATH + "//textarea[@name='rejectionNotes']";
    private static final String ALTERNATIVE_RESOURCE_TYPE_XPATH = REJECT_FUNDING_REQUEST_CONTAINER_XPATH + "//select[@name='quoteResourceTypePicker']";
    private static final String ALTERNATIVE_RESOURCE_XPATH = REJECT_FUNDING_REQUEST_CONTAINER_XPATH + "//select[@id='quoteResourcePicker']";

    private static final String SHOW_ALL_RESOURCES_XPATH = REJECT_FUNDING_REQUEST_CONTAINER_XPATH + "//input[@id='showAllResources']";
    private static final String SHOW_ALL_CITY_TECHNICIANS_LABEL_XPATH = REJECT_FUNDING_REQUEST_CONTAINER_XPATH + "//label[contains(text(),'Show all City Technicians')]";
    private static final String SHOW_ALL_CONTRACTORS_LABEL_XPATH = REJECT_FUNDING_REQUEST_CONTAINER_XPATH + "//label[contains(text(),'Show all Contractors')]";

    private static final String APPROVE_FUNDING_REQUEST_CONTAINER_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'approve-funding-request')]";
    private static final String FUNDING_ROUTE_XPATH = PAGE_MAIN_CONTENT_XPATH + "//select[@name='fundingRouteId']";
    private static final String AMOUNT_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@name='authoriseAmount']";
    private static final String APPROVAL_NOTES_XPATH = PAGE_MAIN_CONTENT_XPATH + "//textarea[@name='authoriseNotes']";


    private static final String QUOTE_REQUEST_CONTAINER_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'reject-funding-request-quote')]";
    private static final String QUOTE_FUNDING_ROUTE_LABELS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'yesNoRadio')]//label[contains(@for, '%s')]";
    private static final String QUOTE_FUNDING_ROUTE_XPATH = QUOTE_REQUEST_CONTAINER_XPATH + "//label[contains(text(), 'Please select Quote Type')]/..//div[contains(@class, 'yesNoRadio')]//input";
    private static final String POTENTIAL_INSURANCE_QUOTE_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@name='isPotentialInsurance']";
    private static final String QUOTE_PRIORITY_SELECT_XPATH = QUOTE_REQUEST_CONTAINER_XPATH +"//label[contains(text(), 'Select Priority')]/.." + DROPDOWN_SEARCH_ARROW_XPATH;
    private static final String SCOPE_OF_WORKS_XPATH = QUOTE_REQUEST_CONTAINER_XPATH + "//textarea[@id='Description']";
    private static final String NUMBER_OF_QUOTES_REQUIRED_SELECT_XPATH = QUOTE_REQUEST_CONTAINER_XPATH + "//select[@id='numberOfQuotesRequired']";
    private static final String RESOURCE_CONTAINER_XPATH = QUOTE_REQUEST_CONTAINER_XPATH + "//descendant::div[contains(@class, 'invitations-to-quote')][%s]";

    private static final String QUOTE_RESOURCE_TYPE_XPATH = RESOURCE_CONTAINER_XPATH + "//select[@id='quoteResourceTypePicker']";
    private static final String QUOTE_SHOW_ALL_CITY_TECHNICIANS_LABEL_XPATH = RESOURCE_CONTAINER_XPATH + "//label[contains(text(),'Show all technicians')]";
    private static final String QUOTE_SHOW_ALL_CONTRACTORS_LABEL_XPATH = RESOURCE_CONTAINER_XPATH + "//label[contains(text(),'Use alternative contractor')]";

    private static final String QUOTE_RESOURCE_XPATH = RESOURCE_CONTAINER_XPATH + "//select[@id='quoteResourcePicker']";

    private static final String BUTTONS_CONTAINER_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class, 'form-buttons')]";
    private static final String SUBMIT_BUTTON_XPATH = BUTTONS_CONTAINER_XPATH + "//input[@id='Submit']";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + " a.btn.btn-secondary.btn-neutral";


    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;

    @FindBy(xpath = APPROVE_RADIO_BUTTON_XPATH)
    private WebElement approve;

    @FindBy(xpath = FUNDING_ROUTE_XPATH)
    private WebElement fundingRoute;

    @FindBy(xpath = AMOUNT_XPATH)
    private WebElement amount;

    @FindBy(xpath = APPROVAL_NOTES_XPATH)
    private WebElement approvalNotes;

    @FindBy(xpath = REJECT_RADIO_BUTTON_XPATH)
    private WebElement reject;

    @FindBy(xpath = REJECTION_REASON_XPATH)
    private WebElement rejectionReason;

    @FindBy(xpath = REJECTION_NOTES_XPATH)
    private WebElement rejectionNotes;

    @FindBy(xpath = ALTERNATIVE_RESOURCE_TYPE_XPATH)
    private WebElement alternativeResourceType;

    @FindBy(xpath = ALTERNATIVE_RESOURCE_XPATH)
    private WebElement alternativeResource;

    @FindBy(xpath = SHOW_ALL_RESOURCES_XPATH)
    private WebElement showAllResources;

    @FindBy(xpath = SHOW_ALL_CITY_TECHNICIANS_LABEL_XPATH)
    private WebElement showAllCityTechnicians;

    @FindBy(xpath = SHOW_ALL_CONTRACTORS_LABEL_XPATH)
    private WebElement showAllContractors;


    @FindBy(xpath = QUOTE_PRIORITY_SELECT_XPATH)
    private WebElement quotePriority;

    @FindBy(xpath = SCOPE_OF_WORKS_XPATH)
    private WebElement scopeOfWorks;

    @FindBy(xpath = NUMBER_OF_QUOTES_REQUIRED_SELECT_XPATH)
    private WebElement numberQuotesRequired;


    @FindBy(xpath = SUBMIT_BUTTON_XPATH)
    private WebElement submit;

    // Page methods
    public InitialFundingRequestsApprovalPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
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

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }


    public void approve() {
        approve.click();
        waitForAngularRequestsToFinish();
    }

    public String selectRandomFundingRoute() {
        return selectRandomOptionFromSelect(fundingRoute);
    }

    public void enterAmount(String value) {
        amount.sendKeys(value);
    }

    public String getAmount() {
        return amount.getAttribute("value");
    }

    public void addApprovalNotes(String approvalNotes) {
        this.approvalNotes.sendKeys(approvalNotes);
    }

    public void reject() {
        reject.click();
        waitForAngularRequestsToFinish();
    }

    public String selectRandomRejectionReason() {
        return selectRandomOptionFromSelect(rejectionReason);
    }

    public void selectExactRejectionReason(String reason) {
        selectOptionFromSelect(rejectionReason, reason);
    }

    public void addRejectionNotes(String rejectionNotes) {
        this.rejectionNotes.sendKeys(rejectionNotes);
    }

    public void submit() {
        this.submit.click();
        waitForAngularRequestsToFinish();
    }


    // Alternative Resource Methods
    public String selectRandomAlternativeResourceType() {
        return selectRandomOptionFromSelect(alternativeResourceType);
    }

    public void selectExactAlternativeResourceType(String resourceType) {
        selectOptionFromSelect(alternativeResourceType, resourceType);
    }

    public void showAllResources() {
        if (POHelper.isElementPresent(By.xpath(SHOW_ALL_CITY_TECHNICIANS_LABEL_XPATH))) {
            showAllCityTechnicians.click();
        } else {
            showAllContractors.click();
        }
    }

    public String selectAlternativeResource() {
        return selectRandomOptionFromSelect(alternativeResource);
    }

    public void selectExactAlternativeResource(String alternativeResourceName) {
        selectOptionFromSelect(alternativeResource, alternativeResourceName);
    }




    // Quote methods

    public void setRandomQuoteFundingRoute() {
        List<WebElement> fundingRoutes = driver.findElements(By.xpath(QUOTE_FUNDING_ROUTE_XPATH));
        int randomSelection = RandomUtils.nextInt(0, fundingRoutes.size());
        String id = fundingRoutes.get(randomSelection).getAttribute("id");
        WebElement fundingRoute = driver.findElement(By.xpath(String.format(QUOTE_FUNDING_ROUTE_LABELS_XPATH, id)));
        fundingRoute.click();
    }

    public void selectRandomAnswerForIsPotentialInsuranceQuote() {
        List<WebElement> potentialInsurance = driver.findElements(By.xpath(POTENTIAL_INSURANCE_QUOTE_XPATH));
        int randomSelection = RandomUtils.nextInt(0, potentialInsurance.size());
        String id = potentialInsurance.get(randomSelection).getAttribute("id");
        WebElement potentialInsuranceQuote = driver.findElement(By.xpath(String.format(QUOTE_FUNDING_ROUTE_LABELS_XPATH, id)));
        potentialInsuranceQuote.click();
    }

    public String getFundintRoute() {
        List<WebElement> fundingRoutes = driver.findElements(By.xpath(QUOTE_FUNDING_ROUTE_XPATH));
        for (WebElement fundingRoute : fundingRoutes)
        {
            if(fundingRoute.isSelected()) {
                return fundingRoute.getAttribute("value");
            }
        }
        return null;
    }

    public void setScopeOFWorks(String keysToSend) {
        scopeOfWorks.sendKeys(keysToSend);
    }

    public String selectRandomQuotePriortyByText() {
        quotePriority.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption();
    }

    public void selectQuotePriortyByValue(String option) {
        selectValueFromSelect(quotePriority, option);
    }

    public void selectNumberOfQuotesByText(String option) {
        selectOptionFromSelect(numberQuotesRequired, option);
    }
    public String getNumberOfQuotesText() {
        return getSelectSelectedText(numberQuotesRequired);
    }

    public void selectNumberOfQuotesByValue(String option) {
        selectValueFromSelect(numberQuotesRequired, option);
    }

    public void selectMinumumNumberOfQuotes() {
        selectIndexFromSelect(numberQuotesRequired, 1);
    }

    public void selectResourceType(Integer resourcePicker, String resource) {
        Select resourceTypeSelect = new Select(driver.findElement(By.xpath(String.format(QUOTE_RESOURCE_TYPE_XPATH, String.valueOf(resourcePicker)))));
        resourceTypeSelect.selectByVisibleText(resource);
    }

    public void selectRandomResourceType(Integer resourcePicker) {
        Select resourceTypeSelect = new Select(driver.findElement(By.xpath(String.format(QUOTE_RESOURCE_TYPE_XPATH, String.valueOf(resourcePicker)))));
        List<WebElement> visibleOptions = resourceTypeSelect.getOptions();

        int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
        resourceTypeSelect.selectByIndex(randomSelection);
    }

    public void selectRandomResource(String resourcePicker) {
        Select resourceSelect = new Select(driver.findElement(By.xpath(String.format(QUOTE_RESOURCE_XPATH, resourcePicker))));
        waitUntilSelectOptionsPopulated(resourceSelect);
        List<WebElement> visibleOptions = resourceSelect.getOptions();

        int randomSelection = RandomUtils.nextInt(1, visibleOptions.size());
        resourceSelect.selectByIndex(randomSelection);

        logger.debug("#visibleOptions = " + visibleOptions.size() + ", index = " + randomSelection);
        logger.debug("#visibleOptions = " + visibleOptions.get(randomSelection).getText());
    }

    public void showAllTechnicians(Integer resourcePicker) {
        WebElement showAllTechnicians = driver.findElement(By.xpath(String.format(QUOTE_SHOW_ALL_CITY_TECHNICIANS_LABEL_XPATH, String.valueOf(resourcePicker))));
        showAllTechnicians.click();
    }

    public void showAllContractors(Integer resourcePicker) {
        WebElement showAllTechnicians = driver.findElement(By.xpath(String.format(QUOTE_SHOW_ALL_CONTRACTORS_LABEL_XPATH, String.valueOf(resourcePicker))));
        showAllTechnicians.click();
    }

    public Integer getNumberOfResources(Integer resourcePicker) {
        Select resourceSelect = new Select(driver.findElement(By.xpath(String.format(QUOTE_RESOURCE_XPATH, String.valueOf(resourcePicker)))));
        List<WebElement> visibleOptions = resourceSelect.getOptions();
        return visibleOptions.size();
    }

    public String getResourceType(Integer resourcePicker) {
        //getSelectSelectedText
        Select drop = new Select(driver.findElement(By.xpath(String.format(QUOTE_RESOURCE_TYPE_XPATH, String.valueOf(resourcePicker)))));
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public String getResource(Integer resourcePicker) {
        //getSelectSelectedText
        Select drop = new Select(driver.findElement(By.xpath(String.format(QUOTE_RESOURCE_XPATH, String.valueOf(resourcePicker)))));
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    public String getFundingRequestFieldDetail(String field) {
        return driver.findElement(By.xpath(String.format(FUNDING_REQUEST_FIELD_DETAIL_XPATH, field))).getText();
    }

    public String getResourceAssignmentsFieldDetail(String field) {
        return driver.findElement(By.xpath(String.format(RESOURCE_ASSIGNMENTS_FIELD_DETAIL_XPATH, field))).getText();
    }
}
