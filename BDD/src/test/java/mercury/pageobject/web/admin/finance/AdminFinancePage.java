package mercury.pageobject.web.admin.finance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.pageobject.web.PopupAlert;
import mercury.helpers.POHelper;

public class AdminFinancePage extends Base_Page<AdminFinancePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String FINANCE_PAGE_TITLE_XPATH = "//div[h1='Finance admin']";
    private static final String FINANCE_PAGE_CONTAINER_XPATH = "//div[@class = 'container']";
    private static final String FINANCE_PAGE_CONTENT_CONTIANER_XPATH = FINANCE_PAGE_CONTAINER_XPATH + "//div[@class = 'admin-action__content']";

    //Grid
    private static final String GRID_XPATH = FINANCE_PAGE_CONTENT_CONTIANER_XPATH + "//div[@id='InvoiceLineConfigTable']";
    private static final String TABLE_ROW_TYPE_XPATH_SUFFIX =  "//td//span[contains(text(),'%s')]";

    //Cell CheckBox
    private static final String CELL_CHECKBOX_XPATH = "//parent::td//parent::tr//input[contains(@class,'%s') and @ng-model='%s']";


    //Buttons
    private static final String ADMIN_HOME_BUTTON_XPATH = FINANCE_PAGE_CONTAINER_XPATH + "//a[contains(text(),'Admin home')]";
    private static final String INVOICE_LINE_TYPE_CANCEL_BUTTON_XPATH = FINANCE_PAGE_CONTAINER_XPATH + "//Button[contains(text(),'Cancel')]";
    private static final String INVOICE_LINE_TYPE_SAVE_BUTTON_XPATH = FINANCE_PAGE_CONTAINER_XPATH + "//Button[contains(text(),'Save')]";

    @FindBy(xpath = ADMIN_HOME_BUTTON_XPATH)
    private WebElement backToAdminHomeButton;

    @FindBy(xpath = INVOICE_LINE_TYPE_CANCEL_BUTTON_XPATH)
    private WebElement cancelButton;

    @FindBy(xpath = INVOICE_LINE_TYPE_SAVE_BUTTON_XPATH)
    private WebElement saveButton;


    public AdminFinancePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(FINANCE_PAGE_TITLE_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public void selectBackToAdminHome() {
        backToAdminHomeButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectCancel() {
        cancelButton.click();
        waitForAngularRequestsToFinish();
    }

    public PopupAlert selectSave() {
        saveButton.click();
        this.waitForAngularRequestsToFinish();
        return PageFactory.initElements(driver, PopupAlert.class);
    }

    public Grid getGrid() {
        Grid grid = GridHelper.getGrid(GRID_XPATH);
        return grid;
    }

    public void selectMandatoryCheckBoxInCell(String type, String status) throws InterruptedException {
        By by = By.xpath(GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, type ) + String.format(CELL_CHECKBOX_XPATH, status, "dataItem.isMandatory"));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
    }

    public void selectLaborCheckBoxInCell(String type, String status) throws InterruptedException {
        By by = By.xpath(GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, type ) + String.format(CELL_CHECKBOX_XPATH, status, "dataItem.isLabor"));
        POHelper.isLoaded().isAngularFinishedProcessing().isFluentElementIsVisible(by);
        driver.findElement(by).click();
        waitForAngularRequestsToFinish();
    }

    public boolean isMandatoryCheckBoxChecked(String type) {
        return this.isElementPresent(By.xpath(GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, type ) + String.format(CELL_CHECKBOX_XPATH, "ng-not-empty", "dataItem.isMandatory")));
    }

    public boolean isLaborCheckBoxChecked(String type) {
        return this.isElementPresent(By.xpath(GRID_XPATH + String.format(TABLE_ROW_TYPE_XPATH_SUFFIX, type ) + String.format(CELL_CHECKBOX_XPATH, "ng-not-empty", "dataItem.isLabor")));
    }

    public boolean isSaveButtonEnabled() {
        return driver.findElement(By.xpath(INVOICE_LINE_TYPE_SAVE_BUTTON_XPATH)).isEnabled();
    }


}
