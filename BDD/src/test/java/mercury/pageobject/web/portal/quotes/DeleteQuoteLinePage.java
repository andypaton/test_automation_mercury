package mercury.pageobject.web.portal.quotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.POHelper;
import mercury.pageobject.web.Base_Page;

public class DeleteQuoteLinePage extends Base_Page<DeleteQuoteLinePage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Regiserer Quote";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_MODAL_CSS = " #DeleteLineModal";
    private static final String PAGE_MODAL_FOOTER_CSS = PAGE_MODAL_CSS + " div.modal-footer";

    private static final String DELETE_LINE_CSS = PAGE_MODAL_FOOTER_CSS + " #DeleteLine";
    private static final String CANCEL_DELETE_LINE_CSS = PAGE_MODAL_FOOTER_CSS + " button.btn.btn-secondary.btn-destructive ";


    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = DELETE_LINE_CSS)
    private WebElement deleteLine;

    @FindBy(css = CANCEL_DELETE_LINE_CSS)
    private WebElement cancelDelete;

    public DeleteQuoteLinePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(DELETE_LINE_CSS));
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
        return this.isElementClickable(By.cssSelector(DELETE_LINE_CSS));
    }

    // PAge interactions
    public RegisterQuotePage deleteLine() {
        this.deleteLine.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, RegisterQuotePage.class).get();
    }

    public RegisterQuotePage cancelDelete() {
        this.cancelDelete.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, RegisterQuotePage.class).get();
    }

}

