package mercury.pageobject.web.portal.resources;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;
import mercury.pageobject.web.Base_Page;
import mercury.helpers.POHelper;
import mercury.helpers.State;

public class ResourceAvailabilityPage extends Base_Page<ResourceAvailabilityPage> {

    private static final Logger logger = LogManager.getLogger();

    // Page title
    private static final String PAGE_TITLE = "Resource Availability";

    // Page elements
    private static final String PAGE_HEADER_CSS = "body > div.body-content > div > h1";
    private static final String PAGE_BODY_CONTENT_CSS = "div.body-content";
    private static final String PAGE_BODY_CONTAINER_CSS = PAGE_BODY_CONTENT_CSS + " div.container.body-container";
    private static final String PAGE_MAIN_CONTENT = PAGE_BODY_CONTAINER_CSS + " div.main-content";

    private static final String PAGE_BODY_CONTENT_XPATH = "//div[contains(@class,'body-content')]";
    private static final String PAGE_BODY_CONTAINER_XPATH = PAGE_BODY_CONTENT_XPATH + "//div[contains(@class,'container') and contains(@class,'body-container')]";
    private static final String PAGE_MAIN_CONTENT_XPATH = PAGE_BODY_CONTAINER_XPATH + "//div[contains(@class,'main-content')]";

    private static final String PAGE_HEADER_XPATH = PAGE_BODY_CONTAINER_XPATH + "//h1[contains(text(),'" + PAGE_TITLE +  "')]";

    private static final String GRID_XPATH = PAGE_MAIN_CONTENT_XPATH + "//div[@id='resource-availability_wrapper']";
    private static final String GRID_CSS = PAGE_MAIN_CONTENT +  " #resource-availability_wrapper";
    private static final String GRID_FILTER_CSS  = GRID_CSS + " #resource-availability_filter";
    private static final String GRID_SEARCH_BOX_CSS = GRID_FILTER_CSS + " input[type='search']";

    private static final String BACK_BUTTON_CSS = PAGE_MAIN_CONTENT + " a.btn.btn-secondary.btn-neutral";

    private static final String RESOURCE_AVAILABILITY_TABLE_XPATH = GRID_XPATH + "//table[@id='resource-availability']/tbody";
    private static final String COVERING_RESOURCE_BUTTON_XPATH = RESOURCE_AVAILABILITY_TABLE_XPATH + "//button//span[contains(text(),'Select a covering resource')]";
    private static final String COVERING_RESOURCE_SEARCHBOX_XPATH = RESOURCE_AVAILABILITY_TABLE_XPATH + "//div[contains(@class, 'search')]/following-sibling::ul[@aria-expanded='true']";
    private static final String COVERING_RESOURCE_SEARCH_INPUT_XPATH = COVERING_RESOURCE_SEARCHBOX_XPATH + "/..//input";
    private static final String COVERING_RESOURCE_SEARCHED_ELEMENTS_XPATH = COVERING_RESOURCE_SEARCHBOX_XPATH + "/li[@class='active']//span[@class='text' and contains(text(), '%s')]";
    private static final String CONFIRM_COVERING_RESOURCE_BUTTON_XPATH = RESOURCE_AVAILABILITY_TABLE_XPATH + "//button[contains(text(),'Confirm')]";
    private static final String COVERING_RESOURCE_LIST = RESOURCE_AVAILABILITY_TABLE_XPATH + "//div[contains(@class, 'search')]/following-sibling::ul[@aria-expanded='true']//li//a[contains(@class, 'opt')]";
    private static final String MY_RESOURCES_XPATH = RESOURCE_AVAILABILITY_TABLE_XPATH + "//td";

    //WebElements
    @FindBy(css = PAGE_HEADER_CSS)
    private WebElement pageHeaderTest;

    @FindBy(css = GRID_SEARCH_BOX_CSS)
    private WebElement searchQuery;

    @FindBy(css = BACK_BUTTON_CSS)
    private WebElement backButton;

    @FindBy(xpath = COVERING_RESOURCE_BUTTON_XPATH)
    private List<WebElement> coveringResourceButtons;

    @FindBy(xpath = COVERING_RESOURCE_SEARCH_INPUT_XPATH) private WebElement coveringResourceSearchInput;

    @FindBy(xpath = CONFIRM_COVERING_RESOURCE_BUTTON_XPATH)
    private List<WebElement> confirmButtons;

    // Page methods
    public ResourceAvailabilityPage(WebDriver driver) {
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

    public void clickCoveringResourceButton(int index) {
        WebElement coveringResourceButton = coveringResourceButtons.get(index);
        coveringResourceButton.click();
        waitForAngularRequestsToFinish();
    }

    public void searchAndSelectCoveringResource(String resourceName) {
        waitForElement(By.xpath(COVERING_RESOURCE_SEARCH_INPUT_XPATH), State.ELEMENT_IS_VISIBLE);
        coveringResourceSearchInput.sendKeys(resourceName);
        WebElement coveringResource = waitForElement(By.xpath(String.format(COVERING_RESOURCE_SEARCHED_ELEMENTS_XPATH, resourceName)), State.ELEMENT_IS_VISIBLE);
        coveringResource.click();
    }

    public String selectFirstCoveringResource() {
        WebElement firstCoveringResource = driver.findElements(By.xpath(COVERING_RESOURCE_LIST)).get(0);
        String name = firstCoveringResource.getText();
        firstCoveringResource.click();
        waitForAngularRequestsToFinish();
        return name;
    }

    public JobsAwaitingReallocationForResourcePage selectFirstResource() {
        WebElement firstCoveringResource = driver.findElements(By.xpath(MY_RESOURCES_XPATH)).get(0);
        firstCoveringResource.click();
        waitForAngularRequestsToFinish();
        return (JobsAwaitingReallocationForResourcePage) new JobsAwaitingReallocationForResourcePage(driver).get();
    }

    public void clickConfirmButton(int rowNumber) {
        WebElement confirmButton = confirmButtons.get(rowNumber);
        POHelper.clickJavascript(confirmButton);
    }

    // Page Interactions
    public ResourceAvailabilityPage searchResources(String search) {
        search = search.trim();
        searchQuery.clear();
        searchQuery.sendKeys(search);
        searchQuery.sendKeys(Keys.RETURN);
        waitUntilElementNotDisplayed(By.xpath(".//*[@class='k-loading-image']"));
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

}
