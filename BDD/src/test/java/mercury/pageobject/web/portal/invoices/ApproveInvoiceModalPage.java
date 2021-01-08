package mercury.pageobject.web.portal.invoices;

import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pageobject.web.Base_Page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ApproveInvoiceModalPage extends Base_Page<ApproveInvoiceModalPage> {
    private static final Logger logger = LogManager.getLogger();
    // Page title
    private static final String PAGE_TITLE = "Invoice Approval";

    //Main Content
    private static final String APPROVE_INVOICE_MODAL_XPATH = "//*[@id='ApproveInvoiceModal']";
    private static final String APPROVE_INVOICE_MODAL_FOOTER = APPROVE_INVOICE_MODAL_XPATH + "//div[contains(@class, 'modal-dialog')]//div[contains(@class, 'modal-content')]//div[contains(@class, 'modal-footer')]";
    private static final String MODAL_TITLE_XPATH = APPROVE_INVOICE_MODAL_XPATH + "//h4";
    private static final String APPROVE_MODAL_FORM_GROUP_XPATH = APPROVE_INVOICE_MODAL_XPATH + "//div[contains(@class, 'form-group')]";
    private static final String FUNDING_ROUTE_CHANGE_CONTAINER_XPATH = APPROVE_MODAL_FORM_GROUP_XPATH + "//div[(@id='FundingRouteChangeNotes')]";

    //Labels
    private static final String SELECTED_BUDGET_LABEL_XPATH = APPROVE_INVOICE_MODAL_XPATH  + "//label[contains(text(), 'Selected Budget')]";
    private static final String RELATED_WEATHER_CONDITION_LABEL_XPATH = APPROVE_INVOICE_MODAL_XPATH  + "//label[contains(text(), 'Does this relate to')]";
    private static final String FUNDING_ROUTE_CHANGE_LABEL_XPATH = FUNDING_ROUTE_CHANGE_CONTAINER_XPATH + "//label[contains(text(), 'Reason for funding route change')]";

    //Buttons
    private static final String CONFIRM_BUTTON_XPATH = APPROVE_INVOICE_MODAL_FOOTER + "//button[(@id='SaveApproval')]";
    private static final String BACK_BUTTON_XPATH = APPROVE_INVOICE_MODAL_FOOTER + "//button[(@type='button') and contains(text(), 'Back')]";

    //List Box
    private static final String BUDGET_LIST_BOX_XPATH = SELECTED_BUDGET_LABEL_XPATH + "/../select[(@id='SelectedBudgetId')]";
    private static final String BUDGET_LIST_VISIBLE_DROPDOWN = BUDGET_LIST_BOX_XPATH + "//option[(@data-note-label) and contains(text(), '%s')]";

    //Radio Buttons
    private static final String RELATED_WEATHER_CONDITION_RADIO_BUTTONS_XPATH = RELATED_WEATHER_CONDITION_LABEL_XPATH + "/../div[contains(@class, 'yesNoRadio')]";
    private static final String RELATED_WEATHER_CONDITION_YES_RADIO_BUTTON_XPATH = RELATED_WEATHER_CONDITION_RADIO_BUTTONS_XPATH + "//label[contains(text(), 'Yes')]/../input";
    private static final String RELATED_WEATHER_CONDITION_NO_RADIO_BUTTON_XPATH = RELATED_WEATHER_CONDITION_RADIO_BUTTONS_XPATH + "//label[contains(text(), 'No')]/../input/following-sibling::input";

    //Text box
    private static final String FUNDING_ROUTE_CHANGE_TEXT_BOX_XPATH = FUNDING_ROUTE_CHANGE_LABEL_XPATH + "/../textarea[@id='fundingroutenotes']";

    private static final String WARNING_ALERT_XPATH = "//div[contains(@class,'sweet-alert')]";
    private static final String WARNING_MESSAGE_XPATH = WARNING_ALERT_XPATH + "//p[contains(text(),'Budget')]";
    private static final String WARNING_MESSAGE_OK_BUTTON_XPATH = WARNING_ALERT_XPATH + "//button[@class='confirm']";


    @FindBy(xpath = SELECTED_BUDGET_LABEL_XPATH)
    private WebElement selectedBudgetLabel;

    @FindBy(xpath = MODAL_TITLE_XPATH)
    private WebElement approveInvoiceHeader;

    @FindBy(xpath = RELATED_WEATHER_CONDITION_LABEL_XPATH)
    private WebElement relatedWeatherConditionLabel;

    @FindBy(xpath = RELATED_WEATHER_CONDITION_YES_RADIO_BUTTON_XPATH)
    private WebElement relatedWeatherConditionYesRadio;

    @FindBy(xpath = RELATED_WEATHER_CONDITION_NO_RADIO_BUTTON_XPATH)
    private WebElement relatedWeatherCondtionNoRadio;

    @FindBy(xpath = CONFIRM_BUTTON_XPATH)
    private WebElement confirmButton;

    @FindBy(xpath = BACK_BUTTON_XPATH)
    private WebElement backButton;

    @FindBy(xpath = BUDGET_LIST_BOX_XPATH)
    private WebElement budgetSelectListBox;

    @FindBy(xpath = FUNDING_ROUTE_CHANGE_TEXT_BOX_XPATH)
    private WebElement fundingRouteChangeTextBox;

    @FindBy(xpath = WARNING_MESSAGE_XPATH)
    private WebElement warningMessage;

    @FindBy(xpath = WARNING_MESSAGE_OK_BUTTON_XPATH)
    private WebElement warningMessageOKButton;

    public ApproveInvoiceModalPage(WebDriver driver){
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(MODAL_TITLE_XPATH + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(MODAL_TITLE_XPATH));
            logger.info(PAGE_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_TITLE + "isloaded error");
            throw new AssertionError();
        }
    }

    @Override
    protected void load() {
        logger.info(PAGE_TITLE + " Load");
    }


    public Boolean isPageLoaded() {
        return this.isElementPresent(By.xpath(MODAL_TITLE_XPATH));
    }

    public String getSelectedBudgetLabel() {
        return selectedBudgetLabel.getText();
    }

    public String getApproveInvoiceModalHeader() {
        return approveInvoiceHeader.getText();
    }

    public String getRelatedWeatherConditionLabel() {
        return relatedWeatherConditionLabel.getText();
    }

    public String getSelectedBudgetRoute() {
        WebElement budgetListDropdown = driver.findElement(By.xpath(String.format(BUDGET_LIST_BOX_XPATH)));
        return getSelectSelectedText(budgetListDropdown).trim();
    }

    public void selectYesRadioRelatedWeather() {
        isElementClickable(By.xpath(RELATED_WEATHER_CONDITION_YES_RADIO_BUTTON_XPATH));
        POHelper.clickJavascript(relatedWeatherConditionYesRadio);
    }

    public void selectNoRadioRelatedWeather() {
        isElementClickable(By.xpath(RELATED_WEATHER_CONDITION_NO_RADIO_BUTTON_XPATH));
        POHelper.clickJavascript(relatedWeatherCondtionNoRadio);
    }

    public void confirm() {
        POHelper.clickJavascript(confirmButton);
        waitForAngularRequestsToFinish();
    }

    public void back() {
        POHelper.clickJavascript(backButton);
        waitForAngularRequestsToFinish();
    }

    public void selectBudgetListBox() {
        budgetSelectListBox.click();
    }

    public void selectBudgetType (String budgetType) {
        waitForElement(By.xpath(BUDGET_LIST_BOX_XPATH), State.ELEMENT_IS_VISIBLE);
        WebElement visibleOption = driver.findElement(By.xpath(String.format(BUDGET_LIST_VISIBLE_DROPDOWN, budgetType)));
        visibleOption.click();
        waitForAngularRequestsToFinish();
    }

    public void enterReason(String note){
        fundingRouteChangeTextBox.sendKeys(note);
    }

    public Boolean isBudgetWarningMessageDisplayed() {
        try {
            return isElementPresent(By.xpath(WARNING_MESSAGE_XPATH));
        } catch (ElementNotVisibleException ex) {
            return false;
        }
    }

    public void confirmBudgetWarningMessage() {
        POHelper.clickJavascript(warningMessageOKButton);
        waitForAngularRequestsToFinish();
    }

    public boolean isDisplayed() {
        return isElementVisible(By.xpath(APPROVE_INVOICE_MODAL_XPATH));
    }
}
