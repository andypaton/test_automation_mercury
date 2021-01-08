package mercury.pageobject.web.servicechannel;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class ServiceChannelJobDetailsPage extends Base_Page<ServiceChannelJobDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "ServiceChannel.com";

    private static final String PAGE_BODY_XPATH = "//div[@id='content']";

    private static final String STATUS_XPATH = PAGE_BODY_XPATH + "//div[@class='header-btn-text text-lg']";
    private static final String PRIMARY_STATUS_XPATH = STATUS_XPATH + "//span[contains(@data-bind, 'primaryStatusText')]";
    private static final String EXTENDED_STATUS_XPATH = STATUS_XPATH + "//span[contains(@data-bind, 'extendedStatus')]";

    private static final String HEADER_INFO_XPATH = PAGE_BODY_XPATH + "//div[@class='wo-detail-header-description']";
    private static final String FAULT_TYPE_XPATH = HEADER_INFO_XPATH + "//div[@class='header-description-1']";
    private static final String DESCRIPTION_XPATH = HEADER_INFO_XPATH + "//div[@class='header-description-2']";
    private static final String TRADE_XPATH = HEADER_INFO_XPATH + "//div[contains(@data-bind, 'trade')]";
    private static final String PRIORITY_XPATH = HEADER_INFO_XPATH + "//div[contains(@data-bind, 'priority')]";
    private static final String CATEGORY_XPATH = HEADER_INFO_XPATH + "//div[contains(@data-bind, 'category')]";

    private static final String WORK_ORDER_INFO_XPATH = PAGE_BODY_XPATH + "//div[@class='row LocationNotesRow work-orders']";
    private static final String TRACKING_NUMBER_XPATH = WORK_ORDER_INFO_XPATH + "//strong[contains(@data-bind, 'trackingNumber')]";
    private static final String WORK_ORDER_NUMBER_XPATH = WORK_ORDER_INFO_XPATH + "//div[@data-bind='text: workOrderNumber']";
    private static final String PURCHASE_ORDER_NUMBER_XPATH = WORK_ORDER_INFO_XPATH + "//div[@data-bind='attr: { title: purchaseNumber}']//span";
    private static final String SCHEDULED_DATE_XPATH = WORK_ORDER_INFO_XPATH + "//span[contains(@data-bind, 'scheduledDate')]";
    private static final String SCHEDULED_TIME_XPATH = WORK_ORDER_INFO_XPATH + "//span[contains(@data-bind, 'scheduledTime')]";
    private static final String SITE_CODE_XPATH = WORK_ORDER_INFO_XPATH + "//strong[contains(@data-bind, 'storeId')]";
    private static final String SITE_ADDRESS1_XPATH = WORK_ORDER_INFO_XPATH + "//div[contains(@data-bind, 'address')]";
    private static final String SITE_ADDRESS2_XPATH = WORK_ORDER_INFO_XPATH + "//div[contains(@data-bind, 'city')]";
    private static final String SITE_PHONE_NUMBER_XPATH = WORK_ORDER_INFO_XPATH + "//a[contains(@data-bind, 'phone')]";


    @FindBy(xpath = PRIMARY_STATUS_XPATH)
    private WebElement primaryStatus;

    @FindBy(xpath = EXTENDED_STATUS_XPATH)
    private WebElement extendedStatus;

    @FindBy(xpath = FAULT_TYPE_XPATH)
    private WebElement faultType;

    @FindBy(xpath = DESCRIPTION_XPATH)
    private WebElement description;

    @FindBy(xpath = TRADE_XPATH)
    private WebElement trade;

    @FindBy(xpath = PRIORITY_XPATH)
    private WebElement priority;

    @FindBy(xpath = CATEGORY_XPATH)
    private WebElement category;

    @FindBy(xpath = TRACKING_NUMBER_XPATH)
    private WebElement trackingNumber;

    @FindBy(xpath = WORK_ORDER_NUMBER_XPATH)
    private WebElement workOrderNumber;

    @FindBy(xpath = PURCHASE_ORDER_NUMBER_XPATH)
    private WebElement purchaseOrderNumber;

    @FindBy(xpath = SCHEDULED_DATE_XPATH)
    private WebElement scheduledDate;

    @FindBy(xpath = SCHEDULED_TIME_XPATH)
    private WebElement scheduledTime;

    @FindBy(xpath = SITE_CODE_XPATH)
    private WebElement siteCode;

    @FindBy(xpath = SITE_ADDRESS1_XPATH)
    private WebElement siteAddress1;

    @FindBy(xpath = SITE_ADDRESS2_XPATH)
    private WebElement siteAddress2;

    @FindBy(xpath = SITE_PHONE_NUMBER_XPATH)
    private WebElement sitePhoneNumber;


    public ServiceChannelJobDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForLoadingToComplete();
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_BODY_XPATH));
            logger.info(PAGE_TITLE + " isLoaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isLoaded error");
            throw new AssertionError();
        }
    }

    public String getPrimaryStatus() {
        waitForElement(By.xpath(PRIMARY_STATUS_XPATH), ELEMENT_IS_CLICKABLE);
        return primaryStatus.getAttribute("innerText");
    }

    public String getExtendedStatus() {
        return extendedStatus.getAttribute("innerText");
    }

    public String getFaultType() {
        return faultType.getText();
    }

    public String getDescription() {
        return description.getText();
    }

    public String getTrade() {
        return trade.getText();
    }

    public String getPriority() {
        return priority.getText();
    }

    public String getCategory() {
        return category.getText();
    }

    public String getTrackingNumber() {
        return trackingNumber.getText();
    }

    public String getWorkOrderNumber() {
        return workOrderNumber.getText();
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber.getText();
    }

    public String getScheduledDate() {
        return scheduledDate.getText();
    }

    public String getScheduledTime() {
        return scheduledTime.getText();
    }

    public String getSiteCode() {
        return siteCode.getText();
    }

    public String getSiteAddress1() {
        return siteAddress1.getText();
    }

    public String getSiteAddress2() {
        return siteAddress2.getText();
    }

    public String getSitePhoneNumber() {
        return sitePhoneNumber.getText();
    }
}
