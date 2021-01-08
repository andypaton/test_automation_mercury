package mercury.pageobject.web.portal.quotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class UploadQuotePage extends Base_Page<UploadQuotePage>{

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Regiserer Quote";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CSS = "div.container.body-container";
    private static final String UPLOAD_FILE_FORM_CSS = PAGE_BODY_CSS + " form";
    private static final String FILE_NAME_CSS = " input#FileToUpload";
    private static final String UPLOAD_FILE_CSS = " input.btn.btn-primary";
    private static final String QUOTE_VALIDATION_SUMMARY_CSS = UPLOAD_FILE_FORM_CSS + " div.validation-summary-errors";

    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = FILE_NAME_CSS)
    private WebElement fileName;

    @FindBy(css = UPLOAD_FILE_CSS)
    private WebElement uploadFile;

    @FindBy(css = QUOTE_VALIDATION_SUMMARY_CSS)
    private WebElement validationSummary;

    public UploadQuotePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(PAGE_HEADER_CSS));
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
        return this.isElementClickable(By.cssSelector(PAGE_HEADER_CSS));
    }

    // Page Interactions
    public void setFileName(String keysToSend) {
        fileName.sendKeys(keysToSend);
    }

    public void uploadFile() {
        this.uploadFile.click();
        waitForAngularRequestsToFinish();
    }

    public String getValidationSummary() {
        return this.validationSummary.getText();
    }
}
