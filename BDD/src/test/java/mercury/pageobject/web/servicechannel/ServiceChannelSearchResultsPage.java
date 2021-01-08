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

public class ServiceChannelSearchResultsPage extends Base_Page<ServiceChannelSearchResultsPage> {

    private static final Logger logger = LogManager.getLogger();

    private static final String PAGE_TITLE = "ServiceChannel.com";

    private static final String PAGE_BODY_XPATH = "//div[@id='content']";

    private static final String LOADING_ICON_DISPLAYED_XPATH = "//div[@id='loading-progress-lg' and @aria-hidden='false']";

    private static final String RESULTS_LIST_XPATH = PAGE_BODY_XPATH + "//div[@id='result-list']";
    private static final String FIRST_RESULT_DISPLAYED_XPATH = RESULTS_LIST_XPATH + "//a[1]";


    @FindBy(xpath = FIRST_RESULT_DISPLAYED_XPATH)
    private WebElement firstResult;


    public ServiceChannelSearchResultsPage(WebDriver driver) {
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

    public boolean isLoadingIconDisplayed() {
        return isElementPresent(By.xpath(LOADING_ICON_DISPLAYED_XPATH));
    }

    public ServiceChannelJobDetailsPage selectFirstResultFound() {
        waitForElement(By.xpath(FIRST_RESULT_DISPLAYED_XPATH), ELEMENT_IS_CLICKABLE);
        POHelper.scrollToElement(firstResult);
        POHelper.clickJavascriptWithoutAngular(firstResult);
        waitForLoadingToComplete();
        return PageFactory.initElements(driver, ServiceChannelJobDetailsPage.class);
    }

    public boolean hasWorkOrderBeenCreated() {
        waitForElement(By.xpath(FIRST_RESULT_DISPLAYED_XPATH), ELEMENT_IS_CLICKABLE);
        return isElementPresent(By.xpath(FIRST_RESULT_DISPLAYED_XPATH));
    }

}
