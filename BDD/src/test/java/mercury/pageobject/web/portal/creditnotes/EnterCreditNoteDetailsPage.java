package mercury.pageobject.web.portal.creditnotes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;
import mercury.helpers.State;

public class EnterCreditNoteDetailsPage extends Base_Page<EnterCreditNoteDetailsPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Credit Note";
    private static final String PAGE_SUB_TITLE = "Enter Credit Note Details";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";
    private static final String PAGE_SUB_HEADER_XPATH = PAGE_MAIN_CONTENT_XPATH + "//h3[contains(text(),'" + PAGE_SUB_TITLE +  "')]";

    //Tables
    private static final String CREDIT_NOTE_DETAILS_TABLE_XPATH = PAGE_MAIN_CONTENT_XPATH + "//table[contains(@class, 'second-table')]";
    private static final String CREDIT_NOTE_LINE_DETAILS_TABLE_XPATH = PAGE_MAIN_CONTENT_XPATH + "//table[@id='detail-lines']";
    private static final String CREDIT_NOTE_DOCUMENT_TABLE_XPATH = PAGE_MAIN_CONTENT_XPATH + "//table[@id='upload-docs']";

    //Links
    private static final String EDIT_XPATH = CREDIT_NOTE_DETAILS_TABLE_XPATH + "//a[contains(text(), 'Edit')]";
    private static final String ADD_NEW_LINE_TO_CREDIT_NOTE_XPATH = CREDIT_NOTE_LINE_DETAILS_TABLE_XPATH + "//a[contains(text(), 'Add New Line to Credit Note')]";
    private static final String UPLOAD_XPATH = CREDIT_NOTE_DOCUMENT_TABLE_XPATH + "//a[contains(text(), 'Upload')]";

    //Form Elements
    private static final String FORM_XPATH = PAGE_MAIN_CONTENT_XPATH + "//form";
    private static final String SUBMIT_CREDIT_NOTE_BUTTON_XPATH = FORM_XPATH + "//input[@class='btn btn-primary']";

    private static final String ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_XPATH = "//div[@id='NewLineModal']";



    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(xpath = SUBMIT_CREDIT_NOTE_BUTTON_XPATH)
    private WebElement submitCreditNote;

    @FindBy(xpath = EDIT_XPATH)
    private WebElement edit;

    @FindBy(xpath = ADD_NEW_LINE_TO_CREDIT_NOTE_XPATH)
    private WebElement addNewLineToCreditNote;

    @FindBy(xpath = UPLOAD_XPATH)
    private WebElement upload;

    // Page methods

    public EnterCreditNoteDetailsPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_SUB_HEADER_XPATH));
            logger.info(PAGE_SUB_TITLE + " isloaded success");
        } catch (NoSuchElementException ex) {
            logger.info(PAGE_SUB_TITLE + " isloaded error");
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

    public Grid getCreditNoteDetailsTable() {
        return GridHelper.getGrid(CREDIT_NOTE_DETAILS_TABLE_XPATH);
    }

    public Grid getCreditNoteLineDetailsTable() {
        return GridHelper.getGrid(CREDIT_NOTE_LINE_DETAILS_TABLE_XPATH);
    }

    public Grid getCreditNoteDocumentTable() {
        return GridHelper.getGrid(CREDIT_NOTE_DOCUMENT_TABLE_XPATH);
    }

    public void clickSubmitCreditNoteButton() {
        submitCreditNote.click();
        waitForAngularRequestsToFinish();
    }

    public AddNewCreditNoteLineModal addNewLineToCreditNote() {
        addNewLineToCreditNote.click();
        return PageFactory.initElements(driver, AddNewCreditNoteLineModal.class).get();
    }

    public Boolean isAddNewCreditLineModalVisible() {
        return isElementVisible(By.xpath(ADD_NEW_LINE_TO_CREDIT_NOTE_MODAL_XPATH));
    }

    public UploadCreditNoteDocumentPage clickUploadLink() {
        waitForElement(By.xpath(UPLOAD_XPATH), State.ELEMENT_IS_CLICKABLE);
        POHelper.clickJavascript(upload);
        return PageFactory.initElements(driver, UploadCreditNoteDocumentPage.class).get();
    }
}