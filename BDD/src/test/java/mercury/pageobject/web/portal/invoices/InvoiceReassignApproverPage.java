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

public class InvoiceReassignApproverPage extends Base_Page<InvoiceReassignApproverPage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Re-assign Invoice to Another Approver";

    //Main content
    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";

    private static final String INVOICE_REASSIGN_APPROVER_PAGE_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String INVOICE_PENDING_REVIEW_PAGE_HEADER_TITLE_XPATH = INVOICE_REASSIGN_APPROVER_PAGE_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String ASSIGN_BUTTON_XPATH = INVOICE_REASSIGN_APPROVER_PAGE_XPATH + "//input[@id = 'accept']";
    private static final String ASSIGN_TO_XPATH = INVOICE_REASSIGN_APPROVER_PAGE_XPATH + "//select[@id = 'SelectedResourceIdToAssignTo']";

    @FindBy(xpath = ASSIGN_BUTTON_XPATH)
    private WebElement assign;

    @FindBy(xpath = ASSIGN_TO_XPATH)
    private WebElement assignTo;


    public InvoiceReassignApproverPage(WebDriver driver) {
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

    public void assign() {
        assign.click();
        waitForAngularRequestsToFinish();
    }

    public String selectRandomApprover() {
        return selectRandomOptionFromSelect(assignTo);
    }

}
