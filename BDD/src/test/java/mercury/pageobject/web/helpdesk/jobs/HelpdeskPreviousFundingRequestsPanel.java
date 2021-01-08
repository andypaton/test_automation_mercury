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
import org.openqa.selenium.support.PageFactory;

import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.helpdesk.HelpdeskCancelApprovedUpliftPanel;

public class HelpdeskPreviousFundingRequestsPanel extends Base_Page<HelpdeskPreviousFundingRequestsPanel> {

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String FUNDING_REQUEST_FORM_XPATH = ACTIVE_WORKSPACE_XPATH + "//form[@name='fundingRequestForm']//div[contains(@class, 'resource-action__content')]";

    private static final String PREVIOUS_FUNDING_REQUESTS_CONTAINER_XPATH = FUNDING_REQUEST_FORM_XPATH + "//div[@class='historic-funding-requests-container']";
    private static final String PREVIOUS_FUNDING_REQUESTS_TABLE_XPATH = PREVIOUS_FUNDING_REQUESTS_CONTAINER_XPATH + "//div[@class='resource-funding-request__history-table']";
    private static final String FUNDING_REQUEST_HISTORY_ROW_XPATH = PREVIOUS_FUNDING_REQUESTS_TABLE_XPATH + "//div[@class='row resource-funding-request__history-row']";
    private static final String FUNDING_REQUEST_TOTAL_ROW_XPATH = PREVIOUS_FUNDING_REQUESTS_TABLE_XPATH + "//div[@class='row resource-funding-request__history-total-row']";
    private static final String AMOUNT_FUNDING_ROUTE_XPATH = FUNDING_REQUEST_HISTORY_ROW_XPATH + "//div[@class='col-xs-3 text-right']";
    private static final String DESCRIPTION_REASON_XPATH = FUNDING_REQUEST_HISTORY_ROW_XPATH + "//div[@class='col-xs-3 multiline']";
    private static final String STATUS_XPATH = FUNDING_REQUEST_HISTORY_ROW_XPATH + "//div[@class='col-xs-2']";
    private static final String DATE_XPATH = FUNDING_REQUEST_HISTORY_ROW_XPATH + "//div[@class='col-xs-3']";
    private static final String CANCEL_BUTTON_XPATH = FUNDING_REQUEST_HISTORY_ROW_XPATH + "//button[@type='button']";
    private static final String TOTAL_APPROVED_XPATH = FUNDING_REQUEST_TOTAL_ROW_XPATH + "//div[contains(text(), \"%s\")]/following-sibling::div";

    @FindBy(xpath = AMOUNT_FUNDING_ROUTE_XPATH)
    private WebElement amountAndFundingRoute;

    @FindBy (xpath = DESCRIPTION_REASON_XPATH)
    private WebElement descriptionAndReason;

    @FindBy(xpath = STATUS_XPATH)
    private WebElement status;

    @FindBy(xpath = DATE_XPATH)
    private WebElement date;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private List<WebElement> cancelButtons;

    public HelpdeskPreviousFundingRequestsPanel(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        try{
            waitForAngularRequestsToFinish();
            assertTrue("Funding Requests panel not loaded!", driver.findElement(By.xpath(FUNDING_REQUEST_FORM_XPATH)).isDisplayed());
            logger.info("Page loaded");
        } catch(NoSuchElementException ex){
            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public String getAmountAndFundingRoute() {
        return amountAndFundingRoute.getText();
    }

    public String getDescriptionAndReason() {
        return descriptionAndReason.getText();
    }

    public String getDate() {
        return date.getText();
    }

    public String getStatus() {
        return status.getText();
    }

    public String getTotalApproved(String text) {
        WebElement totalApproved = driver.findElement(By.xpath(String.format(TOTAL_APPROVED_XPATH, text)));
        return totalApproved.getText();
    }

    public boolean isCancelButtonClickable() {
        return isElementClickable(By.xpath(CANCEL_BUTTON_XPATH));
    }

    public HelpdeskCancelApprovedUpliftPanel clickCancelButton(int index) {
        waitForAngularRequestsToFinish();
        cancelButtons.get(index).click();
        return PageFactory.initElements(driver, HelpdeskCancelApprovedUpliftPanel.class).get();
    }
}