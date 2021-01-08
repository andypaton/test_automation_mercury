package mercury.pageobject.web.servicechannel;

import static mercury.helpers.State.ELEMENT_IS_CLICKABLE;

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

public class ServiceChannelHomePage extends Base_Page<ServiceChannelHomePage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "ServiceChannel.com - Home";

    private static final String PAGE_BODY_XPATH = "//div[@id='content']";

    private static final String LOADING_ICON_DISPLAYED_XPATH = "//div[@id='loading-progress-lg' and @aria-hidden='false']";

    private static final String NAVIGATION_POPUP_XPATH = "//div[@class='joyride-tip-guide joyride-tip']";
    private static final String CLOSE_NAVIGATION_POPUP_XPATH = NAVIGATION_POPUP_XPATH + "//button[contains(text(), 'Close')]";

    private static final String SEARCH_CONTAINER_XPATH = PAGE_BODY_XPATH + "//div[@class='search-container']";
    private static final String FILTER_DROPDOWN_XPATH = SEARCH_CONTAINER_XPATH + "//select[@name='findby']";
    private static final String FILTER_DROPDOWN_OPTION_XPATH = "//option[contains(text(), '%s')]";
    private static final String FILTER_TEXTBOX_XPATH = SEARCH_CONTAINER_XPATH + "//textarea[@name='searchby']";
    private static final String FILTER_SEARCH_XPATH = SEARCH_CONTAINER_XPATH + "//button[contains(text(), 'SEARCH')]";


    @FindBy(xpath = CLOSE_NAVIGATION_POPUP_XPATH)
    private WebElement closeNavigationPopup;

    @FindBy(xpath = FILTER_DROPDOWN_XPATH)
    private WebElement filterDropdown;

    @FindBy(xpath = FILTER_TEXTBOX_XPATH)
    private WebElement filterTextbox;

    @FindBy(xpath = FILTER_SEARCH_XPATH)
    private WebElement filterSearch;


    public ServiceChannelHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info(PAGE_TITLE + " isloaded");
        try {
            waitForLoadingToComplete();
            POHelper.isLoaded().isFluentElementIsVisible(By.xpath(PAGE_BODY_XPATH));
            logger.info(PAGE_TITLE + " isLoaded success");
        } catch(NoSuchElementException ex){
            logger.info(PAGE_TITLE + " isLoaded error");
            throw new AssertionError();
        }
    }

    public boolean isNavigationPopupDisplayed() throws InterruptedException {
        Thread.sleep(5000);
        return isElementPresent(By.xpath(NAVIGATION_POPUP_XPATH));
    }

    public void closeNavigationPopup() {
        waitForElement(By.xpath(CLOSE_NAVIGATION_POPUP_XPATH), ELEMENT_IS_CLICKABLE);
        POHelper.scrollToElement(closeNavigationPopup);
        POHelper.clickJavascriptWithoutAngular(closeNavigationPopup);
        waitForLoadingToComplete();
    }

    public boolean isLoadingIconDisplayed() {
        return isElementPresent(By.xpath(LOADING_ICON_DISPLAYED_XPATH));
    }

    public void selectSearchTypeDropdownOption(String searchType) {
        POHelper.scrollToElement(filterDropdown);
        filterDropdown.click();
        waitForLoadingToComplete();
        WebElement option = driver.findElement(By.xpath(String.format(FILTER_DROPDOWN_OPTION_XPATH, searchType)));
        option.click();
        waitForLoadingToComplete();
    }

    public void enterFilterText(String filterText) {
        filterTextbox.sendKeys(filterText);
    }

    public ServiceChannelSearchResultsPage clickSearchButton() {
        filterSearch.click();
        waitForLoadingToComplete();
        return PageFactory.initElements(driver, ServiceChannelSearchResultsPage.class);
    }

}
