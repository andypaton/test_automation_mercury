package mercury.pageobject.web.portal.invoices;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class ManageInvoiceLinesModal extends Base_Page<ManageInvoiceLinesModal> {
    private static final Logger logger = LogManager.getLogger();

    private static final String MANAGE_LINES_MODAL_CONTENT_CONTAINER_XPATH = "//div[@class='modal-content']";
    private static final String MANAGE_LINES_MODAL_HEADER_XPATH = MANAGE_LINES_MODAL_CONTENT_CONTAINER_XPATH + "//div[@class='modal-header']//h4[contains(text(),'Manage Lines')]";
    private static final String MANAGE_LINES_MODAL_BODY_XPATH = MANAGE_LINES_MODAL_CONTENT_CONTAINER_XPATH + "//div[@class='modal-body']";
    private static final String MANAGE_LINES_MODAL_FOOTER_XPATH = MANAGE_LINES_MODAL_CONTENT_CONTAINER_XPATH + "//div[@class='modal-footer']";

    private static final String MANAGE_LINES_MODAL_CONTENT_CONTAINER_CSS = "body > div.modal.fade.centered.manage-line-modal.in > div > div ";
    private static final String MANAGE_LINES_MODAL_BODY_CSS = MANAGE_LINES_MODAL_CONTENT_CONTAINER_CSS + "div > div.modal-body ";

    //Grid
    private static final String MATERIALS_RELATED_INVOICE_LINES_GRID_XPATH = MANAGE_LINES_MODAL_BODY_XPATH + "//table[@id='material-lines']";
    private static final String LABOUR_RELATED_INVOICE_LINES_GRID_XPATH = MANAGE_LINES_MODAL_BODY_XPATH + "//table[@id='labor-lines']";

    private static final String MATERIALS_RELATED_INVOICE_LINES_GRID_CSS = MANAGE_LINES_MODAL_BODY_CSS + "#material-lines ";
    private static final String LABOUR_RELATED_INVOICE_LINES_GRID_CSS = MANAGE_LINES_MODAL_BODY_CSS + "#labor-lines ";

    //Buttons
    private static final String SAVE_BUTTON_XPATH = MANAGE_LINES_MODAL_FOOTER_XPATH + "//button[contains(text(),'Save')]";
    private static final String CANCEL_BUTTON_XPATH = MANAGE_LINES_MODAL_FOOTER_XPATH + "//button[contains(text(),'Cancel')]";
    private static final String ADD_MATERIALS_LINES_BUTTON_XPATH = MATERIALS_RELATED_INVOICE_LINES_GRID_XPATH + "/preceding-sibling::*[1]/descendant::button";
    private static final String ADD_LABOUR_LINES_BUTTON_XPATH = LABOUR_RELATED_INVOICE_LINES_GRID_XPATH + "/preceding-sibling::*[1]/descendant::button";

    //Material Lines fields
    private static final String MATERIALS_LINES_TYPE_LIST_DROPDOWN_CSS = MATERIALS_RELATED_INVOICE_LINES_GRID_CSS + "tbody > tr > td > select";
    private static final String DELETE_MATERIALS_LINES_BUTTON_CSS = MATERIALS_RELATED_INVOICE_LINES_GRID_CSS + "tbody > tr > td > button[class='btn-link']";

    //Labour Lines fields
    private static final String LABOUR_LINES_TYPE_LIST_DROPDOWN_CSS = LABOUR_RELATED_INVOICE_LINES_GRID_CSS + "tbody > tr > td > select";
    private static final String DELETE_LABOUR_LINES_BUTTON_CSS = LABOUR_RELATED_INVOICE_LINES_GRID_CSS + "tbody > tr > td > button[class='btn-link']";

    //Alert
    private static final String CONFIRMATION_ALERT_XPATH = "//div[@class='sweet-alert showSweetAlert visible']";
    private static final String CONFIRM_ALERT_BUTTON_XPATH = CONFIRMATION_ALERT_XPATH + "//button[@class='confirm']";

    private static final String PPM_ORDERS_SEARCHBOX_XPATH = MANAGE_LINES_MODAL_CONTENT_CONTAINER_XPATH + "//input[@id='searchCriteria']";
    private static final String PPM_ORDER_XPATH = MATERIALS_RELATED_INVOICE_LINES_GRID_XPATH + "//td[contains(text(), '%s')]";
    private static final String PPM_ORDER_CHECKBOX_XPATH = PPM_ORDER_XPATH + "//following-sibling::td//input[@type='checkbox']";
    private static final String PPM_ORDER_CHECKBOX_TICKED_XPATH = PPM_ORDER_XPATH + "//following-sibling::td//input[@type='checkbox' and contains(@class, 'ng-not-empty')]";

    @FindBy(xpath = ADD_MATERIALS_LINES_BUTTON_XPATH)
    private WebElement materialsLines;

    @FindBy(css = MATERIALS_LINES_TYPE_LIST_DROPDOWN_CSS)
    private List<WebElement> materialsLinesTypeListBox;

    @FindBy(xpath = ADD_LABOUR_LINES_BUTTON_XPATH)
    private WebElement laborLines;

    @FindBy(css = LABOUR_LINES_TYPE_LIST_DROPDOWN_CSS)
    private List<WebElement> laborLinesTypeListBox;

    @FindBy(xpath = SAVE_BUTTON_XPATH)
    private WebElement save;

    @FindBy(xpath = CANCEL_BUTTON_XPATH)
    private WebElement cancel;

    @FindBy(css = DELETE_MATERIALS_LINES_BUTTON_CSS)
    private WebElement deleteMaterialLines;

    @FindBy(css = DELETE_LABOUR_LINES_BUTTON_CSS)
    private WebElement deleteLabourLines;

    @FindBy(xpath = CONFIRMATION_ALERT_XPATH)
    private WebElement alert;

    @FindBy(xpath = CONFIRM_ALERT_BUTTON_XPATH)
    private WebElement confirmAlert;

    @FindBy(xpath = PPM_ORDERS_SEARCHBOX_XPATH)
    private WebElement ppmOrdersSearchbox;

    public ManageInvoiceLinesModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try {
            Assert.assertTrue("Page is not displayed", driver.findElement(By.xpath(MANAGE_LINES_MODAL_HEADER_XPATH)).isDisplayed());
            logger.info("Page loaded");

        } catch (NoSuchElementException ex) {

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Boolean isPageLoaded() {
        return this.isElementPresent(By.xpath(MANAGE_LINES_MODAL_HEADER_XPATH));
    }

    public Grid getMaterialsRelatedGrid() {
        return GridHelper.getGrid(MATERIALS_RELATED_INVOICE_LINES_GRID_XPATH);
    }

    public Grid getLaborRelatedGrid() {
        return GridHelper.getGrid(LABOUR_RELATED_INVOICE_LINES_GRID_XPATH);
    }

    public void clickMaterialsLines() {
        materialsLines.click();
        waitForAngularRequestsToFinish();
    }

    public String getMaterialsLineTypeListDropDown() {
        return MATERIALS_LINES_TYPE_LIST_DROPDOWN_CSS;
    }

    public void selectMaterialsLinesType(Integer index, String option) {
        selectOptionFromSelect(materialsLinesTypeListBox.get(index), option);
    }

    public String getMaterialsLineType(Integer index) {
        return getSelectSelectedText(materialsLinesTypeListBox.get(index));
    }

    public void clickLaborLines() {
        laborLines.click();
        waitForAngularRequestsToFinish();
    }

    public String getLaborLineTypeListDropDown() {
        return LABOUR_LINES_TYPE_LIST_DROPDOWN_CSS;
    }

    public void selectLaborLinesType(Integer index, String option) {
        selectOptionFromSelect(laborLinesTypeListBox.get(index), option);
    }

    public String getLaborLineType(Integer index) {
        return getSelectSelectedText(laborLinesTypeListBox.get(index));
    }

    public String getMaterialsLineDeleteLink() {
        return DELETE_MATERIALS_LINES_BUTTON_CSS;
    }

    public String getLaborLineDeleteLink() {
        return DELETE_LABOUR_LINES_BUTTON_CSS;
    }

    public void clickSave() {
        save.click();
        waitForAngularRequestsToFinish();
    }

    public void clickCancelButton() {
        cancel.click();
        waitForAngularRequestsToFinish();
    }

    public boolean isSaveButtonEnabled() {
        return isElementClickable(By.xpath(SAVE_BUTTON_XPATH));
    }

    public boolean isMaterialLinePresent() {
        return isElementPresent(By.cssSelector(MATERIALS_LINES_TYPE_LIST_DROPDOWN_CSS));
    }

    public boolean isLabourLinePresent() {
        return isElementPresent(By.cssSelector(LABOUR_LINES_TYPE_LIST_DROPDOWN_CSS));
    }

    public boolean isDeleteMaterialsLinePresent() {
        return isElementPresent(By.cssSelector(DELETE_MATERIALS_LINES_BUTTON_CSS));
    }

    public boolean isDeleteLaboursLinePresent() {
        return isElementPresent(By.cssSelector(DELETE_LABOUR_LINES_BUTTON_CSS));
    }

    public boolean isConfirmationAlertDisplayed() {
        return isElementPresent(By.xpath(CONFIRMATION_ALERT_XPATH));
    }

    public void clickConfirmationAlert() {
        confirmAlert.click();
    }

    public void clickDeleteLabourLines() {
        deleteLabourLines.click();
    }

    public void clickDeleteMaterialsLines() {
        deleteMaterialLines.click();
    }

    public boolean isPpmOrderLineCheckBoxTicked(String ppmId) {
        WebElement we = driver.findElement(By.xpath(String.format(PPM_ORDER_CHECKBOX_TICKED_XPATH, ppmId)));
        return we.isSelected();
    }

    public ManageInvoiceLinesModal searchPpmOrders(String searchQuery) {
        ppmOrdersSearchbox.clear();
        this.ppmOrdersSearchbox.sendKeys(searchQuery);
        waitForAngularRequestsToFinish();
        return this;
    }

    public void clickPpmOrderCheckbox(String ppmId) {
        WebElement we = driver.findElement(By.xpath(String.format(PPM_ORDER_CHECKBOX_XPATH, ppmId)));
        we.click();
        waitForAngularRequestsToFinish();
    }
}
