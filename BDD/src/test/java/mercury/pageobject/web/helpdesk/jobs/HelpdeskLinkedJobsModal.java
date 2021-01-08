package mercury.pageobject.web.helpdesk.jobs;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class HelpdeskLinkedJobsModal extends Base_Page<HelpdeskLinkedJobsModal>{

    private static final Logger logger = LogManager.getLogger();

    private static final String ACTIVE_WORKSPACE_XPATH = "//div[contains(@class,'tab-pane') and contains(@class,'active')]";

    private static final String MODAL_XPATH = ACTIVE_WORKSPACE_XPATH + "//div[@class='modal modal-panel job fade in']//div[@class='modal-content']";
    private static final String MODAL_BODY_XPATH = MODAL_XPATH + "//div[@class='modal-body']";
    private static final String MODAL_FOOTER_XPATH = MODAL_XPATH + "//div[@class='modal-footer']";

    //BUTTONS
    private static final String SEARCH_BUTTON_XPATH = MODAL_BODY_XPATH + "//button[contains(text(), 'Search')]";
    private static final String CLOSE_BUTTON_XPATH = MODAL_FOOTER_XPATH + "//button[contains(text(), 'Close')]";

    //DROPDOWNS
    private static final String DROPDOWN_XPATH = MODAL_BODY_XPATH + "//label[contains(text(), '%s')]/..//select";
    private static final String OPTION_XPATH = DROPDOWN_XPATH + "//option[contains(text(), '%s')]";

    //GRID
    private static final String GRID_XPATH = MODAL_BODY_XPATH + "//div[@class='k-grid k-widget']";
    private static final String FIRST_JOB_ON_GRID_XPATH = GRID_XPATH + "//tbody/tr";
    private static final String SECOND_JOB_ON_GRID_XPATH = "(//div[contains(@class,'tab-pane') and contains(@class,'active')]//div[@class='modal modal-panel job fade in']//div[@class='modal-content']//div[@class='modal-body']//div[@class='k-grid k-widget']//tbody/tr)[2]";

    private static final String LINK_BUTTON_XPATH = MODAL_BODY_XPATH + "//button[contains(text(), 'Link')]";
    private static final String UNLINK_BUTTON_XPATH = MODAL_BODY_XPATH + "//button[contains(text(), 'Unlink')]";

    @FindBy(xpath = CLOSE_BUTTON_XPATH)
    private WebElement closeButton;

    @FindBy(xpath = SEARCH_BUTTON_XPATH)
    private WebElement searchButton;

    @FindBy(xpath = FIRST_JOB_ON_GRID_XPATH)
    private WebElement firstJob;

    @FindBy(xpath = SECOND_JOB_ON_GRID_XPATH)
    private WebElement secondJob;

    @FindBy(xpath = LINK_BUTTON_XPATH)
    private WebElement linkJob;

    @FindBy(xpath = UNLINK_BUTTON_XPATH)
    private WebElement unlinkJob;


    public HelpdeskLinkedJobsModal(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {

        try{
            waitForLoadingToComplete();
            Assert.assertTrue("Modal is not displayed", driver.findElement(By.xpath(MODAL_XPATH)).isDisplayed());
            logger.info("Page loaded");

        }catch(NoSuchElementException ex){

            logger.info("Page failed to load");
            throw new AssertionError();
        }
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public void selectOptionFromDropdown(String dropdownName, String optionName) {
        WebElement dropdown = driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)));
        dropdown.click();
        waitForAngularRequestsToFinish();
        WebElement option = driver.findElement(By.xpath(String.format(OPTION_XPATH, dropdownName, optionName)));
        option.click();
        waitForAngularRequestsToFinish();
    }

    public void selectBlankOptionFromDropdown(String dropdownName) {
        new Select(driver.findElement(By.xpath(String.format(DROPDOWN_XPATH, dropdownName)))).selectByIndex(0);
        waitForAngularRequestsToFinish();
    }

    public void searchForLinkedJob() {
        waitForElement(By.xpath(SEARCH_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        searchButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectFirstJobOnGrid() {
        waitForElement(By.xpath(FIRST_JOB_ON_GRID_XPATH), ELEMENT_IS_CLICKABLE);
        firstJob.click();
        waitForAngularRequestsToFinish();
    }

    public void selectSecondJobOnGrid() {
        waitForElement(By.xpath(SECOND_JOB_ON_GRID_XPATH), ELEMENT_IS_CLICKABLE);
        secondJob.click();
        waitForAngularRequestsToFinish();
    }

    public void linkJob() {
        waitForElement(By.xpath(LINK_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        linkJob.click();
        waitForAngularRequestsToFinish();
    }

    public void unlinkJob() {
        waitForElement(By.xpath(UNLINK_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        unlinkJob.click();
        waitForAngularRequestsToFinish();
    }

    public HelpdeskJobPage closeLinkedJobsModal() {
        waitForElement(By.xpath(CLOSE_BUTTON_XPATH), ELEMENT_IS_CLICKABLE);
        closeButton.click();
        return PageFactory.initElements(driver, HelpdeskJobPage.class).get();
    }
}
