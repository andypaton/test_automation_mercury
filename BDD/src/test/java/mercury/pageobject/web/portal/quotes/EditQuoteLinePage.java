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

public class EditQuoteLinePage extends Base_Page<EditQuoteLinePage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Regiserer Quote";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_MODAL_CSS = " #EditLineModal";

    private static final String EDIT_LINE_CSS = PAGE_MODAL_CSS + " #EditLine";
    private static final String CANCEL_EDIT_LINE_CSS = PAGE_MODAL_CSS + " button.btn.btn-secondary.btn-destructive ";


    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = EDIT_LINE_CSS)
    private WebElement deleteLine;

    @FindBy(css = CANCEL_EDIT_LINE_CSS)
    private WebElement cancelEdit;

    public EditQuoteLinePage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.cssSelector(EDIT_LINE_CSS));
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
        return this.isElementClickable(By.cssSelector(EDIT_LINE_CSS));
    }

    // PAge interactions
    public RegisterQuotePage deleteLine() {
        this.deleteLine.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, RegisterQuotePage.class).get();
    }

    public RegisterQuotePage cancelEdit() {
        this.cancelEdit.click();
        waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, RegisterQuotePage.class).get();
    }

}

