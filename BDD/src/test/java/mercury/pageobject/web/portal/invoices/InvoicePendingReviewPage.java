package mercury.pageobject.web.portal.invoices;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class InvoicePendingReviewPage extends Base_Page<InvoicePendingReviewPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Invoice Pending Review";

    //Main content
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";

    private static final String INVOICE_PENDING_REVIEW_PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String INVOICE_PENDING_REVIEW_PAGE_HEADER_TITLE_XPATH = INVOICE_PENDING_REVIEW_PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    //Buttons
    private static final String INVOICE_PENDING_REVIEW_APPROVE_BUTTON_XPATH = INVOICE_PENDING_REVIEW_PAGE_BODY_CONTAINER_XPATH + "//button[contains(@class,'btn btn-success')]";
    private static final String INVOICE_PENDING_REVIEW_REJECT_BUTTON_XPATH = INVOICE_PENDING_REVIEW_PAGE_BODY_CONTAINER_XPATH + "//button[contains(@class,'btn btn-danger')]";

    @FindBy(xpath = INVOICE_PENDING_REVIEW_APPROVE_BUTTON_XPATH)
    private WebElement approveButton;

    @FindBy(xpath = INVOICE_PENDING_REVIEW_REJECT_BUTTON_XPATH)
    private WebElement rejectButton;


    public InvoicePendingReviewPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(INVOICE_PENDING_REVIEW_PAGE_HEADER_TITLE_XPATH));
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
        return this.isElementPresent(By.xpath(INVOICE_PENDING_REVIEW_PAGE_HEADER_TITLE_XPATH));
    }

    public void clickApprove() {
        approveButton.click();
        waitForAngularRequestsToFinish();
    }

    public RejectInvoicePendingReviewModal clickReject() {
        rejectButton.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, RejectInvoicePendingReviewModal.class);
    }
}
