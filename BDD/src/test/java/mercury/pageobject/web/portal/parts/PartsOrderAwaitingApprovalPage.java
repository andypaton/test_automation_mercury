package mercury.pageobject.web.portal.parts;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;



public class PartsOrderAwaitingApprovalPage extends Base_Page<PartsOrderAwaitingApprovalPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Parts Order Awaiting Approval";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = " div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT_CSS = PAGE_BODY_CONTAINER_CSS + " div.main-content";
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";
    private static final String JOB_DETAILS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//h3[contains(text(), 'Job details')]/..//div[@class='job-detail__job-info']";
    private static final String JOB_DETAILS_FIELD_XPATH = JOB_DETAILS_XPATH + "//label[contains(text(), '%s')]/following-sibling::div";
    private static final String PARTS_ORDER_DETAILS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//h3[contains(text(), 'Parts Order Details')]/..//div[@class='part-detail__info']";
    private static final String PARTS_ORDER_DETAILS_FIELD_XPATH = PARTS_ORDER_DETAILS_XPATH + "//label[contains(text(), '%s')]/following-sibling::div";
    private static final String PAGE_PARTS_REQUEST_DETAIL_CSS = PAGE_MAIN_CONTENT_CSS + " div.parts-request-detail";
    private static final String PAGE_PARTS_REQUEST_DETAILS_CSS = PAGE_MAIN_CONTENT_CSS + " div.parts-request-details";
    private static final String PAGE_PARTS_REQUEST_DETAILS_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[@class='parts-request-details']";
    private static final String PAGE_PARTS_ORDER__CSS = PAGE_PARTS_REQUEST_DETAIL_CSS + " div.parts-order-approval-decision-panel";
    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[contains(@class,'parts-request-details')]";
    private static final String POTENTIAL_INSURANCE_JOB_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@name='IsPotentialInsuranceJob']";
    private static final String YES_OR_NO_LABELS_XPATH = PAGE_MAIN_CONTENT_XPATH + "//input[@name='IsPotentialInsuranceJob' and @value='%s']";

    //Forms elements
    private static final String FORM_PARTS_ORDER_REJECT_CSS = PAGE_PARTS_ORDER__CSS + " #RejectPartsOrderRequestForm";
    private static final String FORM_PARTS_ORDER_APPROVAL_CSS = PAGE_PARTS_ORDER__CSS + " #ApprovalForm";
    private static final String FORM_PARTS_ORDER_REJECT_REASON_CSS = FORM_PARTS_ORDER_REJECT_CSS + " #RejectionReasonId";
    private static final String FORM_PARTS_ORDER_BUDGET_CSS = FORM_PARTS_ORDER_APPROVAL_CSS + " #BudgetId";
    private static final String FORM_PARTS_ORDER_BUDGET_ALERT_MESSAGE_CSS = FORM_PARTS_ORDER_APPROVAL_CSS + " #BudgetAlertText";
    private static final String FORM_PARTS_ORDER_REJECT_NOTES_CSS = FORM_PARTS_ORDER_REJECT_CSS + " #Notes";
    private static final String FORM_PARTS_ORDER_LINES_CSS = PAGE_PARTS_REQUEST_DETAILS_CSS + " #OrderLines";
    private static final String FORM_REDUCE_FIRST_PARTS_ORDER_QUANTITY_FIELD_CSS = FORM_PARTS_ORDER_LINES_CSS + " #ApprovedPartsOrderLines_0__UnitQuantity";
    private static final String TOTAL_COST_XPATH = PAGE_PARTS_REQUEST_DETAILS_XPATH + "//td[contains(@class, 'TotalCost')]";

    //Buttons
    private static final String FORM_PARTS_ORDER_REJECT_BUTTON_CSS = FORM_PARTS_ORDER_REJECT_CSS + " input.btn.btn-primary.Reject-Order";
    private static final String FORM_PARTS_ORDER_APPROVAL_BUTTON_CSS = FORM_PARTS_ORDER_APPROVAL_CSS + " input.btn.btn-primary.Approve-Order";
    private static final String FORM_PARTS_ORDER_DELETE_BUTTON_CSS = FORM_PARTS_ORDER_LINES_CSS + " button.btn.btn-xs.btn-primary.Delete-Order-Line";
    private static final String FORM_PARTS_ORDER_DELETE_BUTTON_XPATH = "//button[contains(text(), 'Delete')]";

    private static final String APPROVAL_CHECKBOX_CSS = FORM_PARTS_ORDER_APPROVAL_CSS + " label.checkbox-inline";


    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeader;

    @FindBy(css = FORM_PARTS_ORDER_REJECT_CSS)
    private WebElement rejectOrderForm;

    @FindBy(css = FORM_PARTS_ORDER_REJECT_REASON_CSS)
    private WebElement rejectReason;

    @FindBy(css = FORM_PARTS_ORDER_REJECT_NOTES_CSS)
    private WebElement rejectNotes;

    @FindBy(css = FORM_PARTS_ORDER_REJECT_BUTTON_CSS)
    private WebElement rejectButton;

    @FindBy(css = FORM_PARTS_ORDER_BUDGET_CSS)
    private WebElement budget;

    @FindBy(css = FORM_PARTS_ORDER_APPROVAL_BUTTON_CSS)
    private WebElement approveButton;

    @FindBy(css = FORM_PARTS_ORDER_DELETE_BUTTON_CSS)
    private WebElement deleteButton;

    @FindBy(xpath = FORM_PARTS_ORDER_DELETE_BUTTON_XPATH)
    private WebElement delete;

    @FindBy(css = FORM_REDUCE_FIRST_PARTS_ORDER_QUANTITY_FIELD_CSS)
    private WebElement unitQuantityField;

    @FindBy(css = FORM_PARTS_ORDER_BUDGET_ALERT_MESSAGE_CSS)
    private WebElement budgetAlertMessage;

    @FindBy(xpath = TOTAL_COST_XPATH)
    private WebElement totalCost;

    @FindBy(css = APPROVAL_CHECKBOX_CSS)
    private WebElement approvalCheckbox;


    // Page methods
    public PartsOrderAwaitingApprovalPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(PAGE_PARTS_ORDER__CSS));
            logger.info(PAGE_PARTS_ORDER__CSS + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_PARTS_ORDER__CSS + " isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }

    public String getPageHeaderText() {
        return pageHeader.getText();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public Boolean isPageLoaded() {
        return this.isElementClickable(By.cssSelector(PAGE_HEADER_CSS));
    }

    // Page Interactions
    public PartsOrderAwaitingApprovalPage deletePartRequest() {
        POHelper.scrollToElement(delete);
        delete.click();
        waitForAngularRequestsToFinish();
        return this;
    }

    public void reject() {
        rejectButton.click();
        waitForAngularRequestsToFinish();
    }

    public void setRejectNotes(String notes) {
        rejectNotes.sendKeys(notes);
    }

    public void setRandomRejectReason() {
        selectRandomOptionFromSelect(rejectReason);
    }

    public void setBudget() {
        selectRandomOptionFromSelect(budget);
        waitForAngularRequestsToFinish();
        checkForAndConfirmAlert();
    }

    public void approve() {
        approveButton.click();
        waitForAngularRequestsToFinish();
    }

    public void delete() {
        deleteButton.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isDeleteButtonEnabled() {
        return deleteButton.isEnabled();
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(GRID_XPATH, 1);
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public void reduceUnitQuantity() {
        unitQuantityField.sendKeys(Keys.DOWN);
    }

    public int getUnitQuantityReduced() {
        return Integer.parseInt(unitQuantityField.getAttribute("value"));
    }

    public boolean getBudgetDropdownEditState() {
        return this.isElementClickable(By.cssSelector(FORM_PARTS_ORDER_BUDGET_CSS));
    }

    public String getBudgetAlertMessage() {
        return this.budgetAlertMessage.getText();
    }

    public boolean isApprovalEnabled() {
        return approveButton.isEnabled();
    }

    public boolean isRejectButtonEnabled() {
        return rejectButton.isEnabled();
    }

    public void clickVerbalApproval() {
        approvalCheckbox.click();
        waitForAngularRequestsToFinish();
    }

    public String getTotalCost() {
        return totalCost.getText();
    }

    public String getJobDetailsFieldData(String field) {
        WebElement jobDetailsField = driver.findElement(By.xpath(String.format(JOB_DETAILS_FIELD_XPATH, field)));
        return jobDetailsField.getText();
    }

    public String getPartsOrdersDetailsFieldData(String field) {
        WebElement partsOrderDetailsField = driver.findElement(By.xpath(String.format(PARTS_ORDER_DETAILS_FIELD_XPATH, field)));
        return partsOrderDetailsField.getText();
    }

    public void selectRandomAnswerForIsPotentialInsuranceJob() {
        List<WebElement> potentialInsurance = driver.findElements(By.xpath(POTENTIAL_INSURANCE_JOB_XPATH));
        int randomSelection = RandomUtils.nextInt(0, potentialInsurance.size());
        String value = potentialInsurance.get(randomSelection).getAttribute("value");
        WebElement potentialInsuranceJob = driver.findElement(By.xpath(String.format(YES_OR_NO_LABELS_XPATH, value)));
        POHelper.clickJavascript(potentialInsuranceJob);
    }
}
