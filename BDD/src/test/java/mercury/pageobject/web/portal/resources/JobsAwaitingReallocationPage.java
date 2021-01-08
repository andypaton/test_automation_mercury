package mercury.pageobject.web.portal.resources;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import mercury.helpers.POHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;

public class JobsAwaitingReallocationPage extends Base_Page<JobsAwaitingReallocationPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Jobs Awaiting Reallocation In Your Region.";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    protected static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='resource-availability_wrapper']";
    private static final String GRID_CSS = PAGE_MAIN_CONTENT + " #resource-availability_wrapper";
    private static final String GRID_FILTER_CSS  = GRID_CSS + " #resource-availability_filter";
    private static final String GRID_SEARCH_BOX_CSS = GRID_FILTER_CSS + " input[type='search']";

    //Table rows
    private static final String TABLE_BODY_XPATH = GRID_XPATH + "//table/tbody";
    protected static final String TABLE_DATA_XPATH = GRID_XPATH + "//td[contains(text(), '%s')]";

    private static final String COVERING_RESOURCE_DROPDOWN_XPATH = "(" + TABLE_BODY_XPATH + "//td//button[contains(@class, 'dropdown')]//span)[1]";
    private static final String COVERING_RESOURCE_SEARCHBOX_XPATH = COVERING_RESOURCE_DROPDOWN_XPATH + "/ancestor::button[@aria-expanded='true']/..//div[@class='bs-searchbox']/input";
    private static final String COVERING_RESOURCE_DROPDOWN_OPTIONS_XPATH = TABLE_BODY_XPATH + "//div[contains(@class, 'dropdown-menu') and contains(@class, 'open')]//ul[@role='listbox' and @aria-expanded='true']/li/a[contains(@class, 'opt')]";
    private static final String COVERING_RESOURCE_VISIBLE_DROPDOWN_OPTION_XPATH = COVERING_RESOURCE_DROPDOWN_OPTIONS_XPATH + "/span[@class='text' and contains(text(), '%s')]";
    private static final String EXPANDED_COVERING_RESOURCES = "//button/following-sibling::div/ul[@aria-expanded = 'true']";

    private static final String COVERING_RESOURCES_XPATH = "(//button[contains(., 'Select a covering resource')])[1]/../div/ul/li[@data-optgroup]/a/span[@class = 'text']";
    private static final String COVERING_RESOURCE_HEADERS_XPATH = "(//button[contains(., 'Select a covering resource')])[1]/../div/ul/li[contains(@class, 'dropdown-header')]/span[@class = 'text']";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + " a.btn.btn-secondary.btn-neutral";
    private static final String REALLOCATE_BUTTON_XPATH = TABLE_BODY_XPATH + "//td/button[contains(text(), 'Reallocate') and not(@disabled)]";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    protected WebElement pageHeaderTest;

    @FindBy(css = GRID_SEARCH_BOX_CSS)
    private WebElement searchBox;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;

    @FindBy(xpath = REALLOCATE_BUTTON_XPATH)
    private WebElement reallocateButton;

    @FindBy(xpath = COVERING_RESOURCE_DROPDOWN_XPATH)
    private WebElement coveringResourceDropdown;

    @FindBy(xpath = COVERING_RESOURCES_XPATH)
    private List<WebElement> coveringResources;

    @FindBy(xpath = COVERING_RESOURCE_HEADERS_XPATH)
    private List<WebElement> coveringResourceHeaders;

    // Page methods
    public JobsAwaitingReallocationPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_HEADER_XPATH));
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
        return this.isElementClickable(By.xpath(PAGE_HEADER_XPATH));
    }

    // Page Interactions
    public JobsAwaitingReallocationPage searchJobs(String searchQuery) {
        searchBox.clear();
        POHelper.sendKeys(searchBox, searchQuery);
        POHelper.waitForStability();
        return this;
    }

    public Integer getNumberOfDisplayedRows() {
        return GridHelper.getNumberOfDisplayedRows(GRID_XPATH);
    }

    public Grid getGrid() {
        return GridHelper.getGrid(GRID_XPATH);
    }

    public Grid getGridFirstRow() {
        return GridHelper.getGrid(GRID_XPATH, 1);
    }

    public Grid getGridFirstRow(int gridNumber) {
        String xpath = String.format("(%s)[%d]", GRID_XPATH, gridNumber);
        return GridHelper.getGrid(xpath, 1);
    }

    public List<String> getGridAsString(){
        return GridHelper.getRowsAsString(GRID_XPATH);
    }

    public List<String> getGridAsString(int gridNumber) {
        String xpath = String.format("(%s)[%d]", GRID_XPATH, gridNumber);
        return GridHelper.getRowsAsString(xpath);
    }

    public boolean isSearchJobDisplayed() {
        return isElementPresent(By.cssSelector(GRID_SEARCH_BOX_CSS));
    }

    public boolean isReAllocateButtonEnabled() {
        return isElementClickable(By.xpath(REALLOCATE_BUTTON_XPATH));
    }

    public void clickReallocateButton() {
        reallocateButton.click();
        waitForAngularRequestsToFinish();
    }

    public void selectACoveringResource(String coveringResource) {
        coveringResourceDropdown.click();
        waitForAngularRequestsToFinish();
        WebElement searchBox = driver.findElement(By.xpath(COVERING_RESOURCE_SEARCHBOX_XPATH));

        // in searchbox only enter substring pre '(' and '-'
        String str = coveringResource.contains("(") ? coveringResource.split("\\(")[0].trim() : coveringResource;
        POHelper.sendKeys(searchBox, str);
        logger.debug("visibleCoveringResource = " + str.toString());
        waitForAngularRequestsToFinish(); // Need to wait for the drop down coveringResources to update after entering text
        WebElement visibleOption = driver.findElement(By.xpath(String.format(COVERING_RESOURCE_VISIBLE_DROPDOWN_OPTION_XPATH, coveringResource)));
        POHelper.clickJavascript(visibleOption);
    }

    public void selectRandomCoveringResource() {
        if (!isElementVisible(By.xpath(EXPANDED_COVERING_RESOURCES))) {
            coveringResourceDropdown.click();
        }
        driver.findElements(By.xpath(COVERING_RESOURCE_DROPDOWN_OPTIONS_XPATH)).get(0).click();
        waitForAngularRequestsToFinish();
    }

    public List<String> getCoveringResources() {
        List<String> result = new ArrayList<>();
        for (WebElement we : coveringResources) {
            result.add(we.getAttribute("textContent"));
        }
        return result;
    }

    public List<String> getCoveringResourceHeaders() {
        List<String> result = new ArrayList<>();
        for (WebElement we : coveringResourceHeaders) {
            result.add(we.getAttribute("textContent"));
        }
        return result;
    }

    public boolean isJobDisplayed(String jobReference) {
        return isElementPresent(By.xpath(String.format(TABLE_DATA_XPATH, jobReference)));
    }

    /**
     * click Show Contractors checkbox in first row
     * @throws Throwable
     */
    public void showContractors() throws Throwable {
        // selenium we.click(), selenium actions, and javascript click on checkbox don't update the dropdown resource list, so everything done in javascript!

        String id = driver.findElement(By.xpath("//tr[@class='odd'][1]//input[@data-show-contractors-checkbox]/following-sibling::label")).getAttribute("data-show-contractors");

        String js = "var checkbox = $('[data-show-contractors-checkbox=' + %s + ']'); "
                + "var checked = checkbox.prop('checked'); "
                + "checkbox.prop(\"checked\", !checked); "
                + "var resourcePicker = $('[data-Resource-picker=' + %s + ']'); "
                + "var contractorsInAreaOption = $('[data-show-contractor-area-option=' + %s + ']'); "
                + "var enabled = contractorsInAreaOption.prop(\"disabled\"); "
                + "contractorsInAreaOption.attr(\"disabled\", !enabled); "
                + "resourcePicker.selectpicker('refresh');";
        js = String.format(js, id, id, id);

        ((JavascriptExecutor) getWebDriver()).executeScript(js);
        waitForAngularRequestsToFinish();
    }

}
