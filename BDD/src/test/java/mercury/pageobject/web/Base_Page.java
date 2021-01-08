package mercury.pageobject.web;

import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.MAX_TIMEOUT_MILLIS;
import static mercury.helpers.Globalisation.assertTerminology;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Duration.TWO_SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import mercury.helpers.JSWaiter;
import mercury.helpers.POHelper;
import mercury.helpers.State;
import mercury.pagefactory.CustomLoadableComponent;

public class Base_Page<T extends CustomLoadableComponent<T>> extends CustomLoadableComponent<T> {

    protected static final String BODY_XPATH = "//body";

    protected static final String VISIBLE_DROPDOWN_SEARCHBOX_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]//input";
    protected static final String VISIBLE_DROPDOWN_OPTION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]//*[text()[contains(., \"%s\")]]";
    protected static final String VISIBLE_DROPDOWN_OPTION_EXACT_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]//*[text() = %s]";
    protected static final String VISIBLE_DROPDOWN_OPTIONS_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]//ul[@role='listbox']/li";
    protected static final String VISIBLE_DROPDOWN_CALENDAR_OPTIONS_XPATH = "//div[contains(@class, 'k-animation-container')]//div[contains(@class, 'k-calendar-container')]//div[contains(@class, 'k-calendar')]//td";
    protected static final String VISIBLE_DROPDOWN_CALENDAR_OPTION_XPATH = VISIBLE_DROPDOWN_CALENDAR_OPTIONS_XPATH + "//a[contains(@title, '%s')]";

    protected static final String EXPANDED_LISTBOX_XPATH = "//ul[@role='listbox' and @aria-expanded = 'true']/li";

    protected static final String DROPDOWN_OPTION_XPATH = "//div[@class='k-animation-container']//li[contains(text(), '%s')]";
    protected static final String DROPDOWN_SEARCH_ARROW_XPATH = "//span[contains(@class, 'k-dropdown')]//span[contains(@class, 'k-i-arrow-s')]";
    protected static final String DROPDOWN_SEARCH_XPATH = "//span[contains(@class, 'k-dropdown')]//span[contains(@class, 'k-input')]";
    protected static final String DROPDOWN_CALENDAR_XPATH = "//span[contains(@class, 'k-select')]//span[contains(@class, 'k-i-calendar')]";

    protected static final String DROPDOWN_CLOCK_XPATH = "//span[contains(@class, 'k-select')]//span[contains(@class, 'k-i-clock')]";
    protected static final String DROPDOWN_PICK_WRAPPER_XPATH = "//span[contains(@class, 'k-picker-wrap')]";
    protected static final String DROPDOWN_VALIDATION_XPATH = "//span[contains(@class, 'k-tooltip') and contains(@class, 'k-tooltip-validation')]";
    protected static final String DROPDOWN_INVALID_XPATH = "//span[contains(@class, 'k-tooltip') and contains(@class, 'k-invalid-msg')]";
    protected static final String DROPDOWN_INVALID_MESSAGE_XPATH = "//span[contains(@class, 'k-tooltip') and contains(@class, 'k-invalid-msg') and contains(text(), '%s')]";
    protected static final String DROPDOWN_SELECTED_VALUE_XPATH = "/following-sibling::div//span[contains(@class, 'k-input')]";


    protected static final String LOWERCASE_TEXT = "translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')";
    protected static final String LOWERCASE_TITLE = "translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')";

    private static final String MODAL_XPATH = "//*[contains(@class, 'modal-open') or (contains(@class, 'modal') and contains(@style, 'display: block'))]";
    protected static final String ALERT_CSS = "div.sweet-alert";
    protected static final String ALERT_CONFIRM_CSS = ALERT_CSS + " button.confirm";
    protected static final String ALERT_CANCEL_CSS = ALERT_CSS + " button.cancel";

    private static final String ERROR_XPATH = "//div[contains(@class, 'error') and not(contains(@style,'display: none')) ] | //span[contains(@class, 'error') and not(contains(@style,'display: none'))] | //label[contains(@class, 'error') and not(contains(@style,'display: none'))]";  // All errors in div, span or label tags that are visible
    private static final String ALERT_XPATH = "//*[contains(@class, 'alert')]/span";
    private static final String ALERT2_XPATH = "//*[contains(@role, 'alert') and not(contains(@class,'ng-hide'))]";
    private static final String TOAST_MESSAGE_XPATH = "//div[contains(@class, 'toast-message')]";
    private static final String INFORMATION_MESSAGE_XPATH = "//div[@class='col-xs-12'] | //div[@id='NoData']";
    private static final String NOVALIDATE_CSS = "[novalidate]";

    private static final String VALIDATION_SUMMARY_CONTAINER_XPATH = "//div[contains(@class,'validation-summary-errors') and not(contains(@style,'display: none'))]";
    private static final String VALIDATION_SUMMARY_ERROR_MESSAGES_XPATH = VALIDATION_SUMMARY_CONTAINER_XPATH + "//li";

    private static final String NAV_BAR_CSS = ".navbar.navbar-fixed-top";
    private static final String OPTIONS_LIST = "/..//input";

    public static final int POLL_TIMER = 1;

    private static final Logger logger = LogManager.getLogger();

    protected WebDriver driver;

    @FindBy(xpath = BODY_XPATH)
    private WebElement body;

    @FindBy(xpath = ERROR_XPATH)
    private WebElement error;

    @FindBy(css = NOVALIDATE_CSS)
    private WebElement noValidate;

    public Base_Page(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        JSWaiter.setDriver(driver);

        if ( !this.getClass().getName().contains("partial")  ) {
            String text = body.getAttribute("innerText");
            assertTerminology(text);
        }

    }

    public String getPageTitle(){
        return driver.getTitle();
    }

    @Override
    protected void isLoaded() throws Error {
        logger.info("Base_Page isLoaded");
    }

    @Override
    protected void load() {
        logger.info("Base_Page Load");
    }

    @Override
    protected void load(String xpath) {
        logger.info("Base_Page Load");
    }

    public WebElement find(By locator) {
        return driver.findElement(locator);
    }

    public void click(By locator) {
        find(locator).click();
        waitForAngularRequestsToFinish();
    }

    /**
     * Is element present on page
     * @param by
     * @return
     */
    public boolean isElementPresent(By by) {
        return POHelper.isElementPresent(by);
    }

    /**
     * Is child element present
     * @param we
     * @param by
     * @return
     */
    public boolean isElementPresent(WebElement we, By by) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            we.findElement(by);
            return true;
        } catch(NoSuchElementException e) {
            return false;
        }
        finally {
            driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public boolean isElementClickable(By by) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            new WebDriverWait(getWebDriver(), 0).until(ExpectedConditions.elementToBeClickable(by));
            return true;
        } catch(TimeoutException e) {
            return false;
        }
        finally {
            driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public boolean isElementVisible(By by) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            new WebDriverWait(getWebDriver(), 0).until(ExpectedConditions.visibilityOfElementLocated(by));
            return true;
        } catch(TimeoutException e) {
            return false;
        }
        finally {
            driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void waitUntilUrlContains(String url) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), MAX_TIMEOUT);
        wait.until(ExpectedConditions.urlContains(url));
    }

    public void waitUntilElementNotDisplayed(By locator) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(getWebDriver(), MAX_TIMEOUT);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
    }

    public void waitUntilElementCountToBe(By locator, Integer elementCount) {
        POHelper.waitUntilElementCountToBe(locator, elementCount);
    }

    public WebElement waitForElement(final By locator, State condition) {
        FluentWait<WebDriver> wait = new FluentWait<WebDriver>(getWebDriver())
                .withTimeout(Duration.ofSeconds(MAX_TIMEOUT))
                .pollingEvery(Duration.ofSeconds(POLL_TIMER));

        wait.until(expectedCondition(condition, locator));

        return getWebDriver().findElement(locator);
    }

    public void waitForCertainTimeInMinutes(int maxTime) throws InterruptedException {
        logger.debug("waiting for " + maxTime +" minute");
        Thread.sleep(maxTime*60000);
    }

    public WebElement waitForElement(final By locator, State condition, int timeout) {
        FluentWait<WebDriver> wait = new FluentWait<WebDriver>(getWebDriver())
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofSeconds(POLL_TIMER));

        wait.until(expectedCondition(condition, locator));
        return getWebDriver().findElement(locator);
    }

    private ExpectedCondition<WebElement> expectedCondition(State condition, By locator) {
        // Try and get any Angular application on the page and if found wait for the application to finish processing
        String angularElement = getNgAppName();
        if (angularElement != null) {
            JSWaiter.waitJQueryAngular(getNgAppName());
        }

        switch (condition) {
        case ELEMENT_IS_VISIBLE:
            return ExpectedConditions.visibilityOfElementLocated(locator);
        case ELEMENT_IS_CLICKABLE:
            return ExpectedConditions.elementToBeClickable(locator);
        default:
            throw new IllegalArgumentException("invalid condition");
        }
    }

    public void waitForAnimation() {
        String ANIMATION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]";
        waitUntilElementCountToBe(By.xpath(ANIMATION_XPATH), 0);
    }

    public void waitForAnimation(Integer count) {
        String ANIMATION_XPATH = "//div[@class='k-animation-container' and contains(@style, 'display: block')]";
        waitUntilElementCountToBe(By.xpath(ANIMATION_XPATH), count);
        waitForElement(By.xpath(ANIMATION_XPATH), State.ELEMENT_IS_VISIBLE);
    }

    public List<String> getVisibleDropdownOptions(){
        List<String> options = new ArrayList<>();
        if (isElementVisible(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH))) {
            List<WebElement> weOptions = driver.findElements(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH));
            for (WebElement weOption : weOptions) {
                options.add(weOption.getAttribute("innerText"));
            }
        }
        logger.debug("visibleOptions = " + options.toString());
        return options;
    }

    public List<String> getExpandedDropdownOptions(){
        List<String> options = new ArrayList<>();
        if (isElementVisible(By.xpath(EXPANDED_LISTBOX_XPATH))) {
            List<WebElement> weOptions = driver.findElements(By.xpath(EXPANDED_LISTBOX_XPATH));
            for (WebElement weOption : weOptions) {
                options.add(weOption.getAttribute("innerText"));
            }
        }
        logger.debug("visibleOptions = " + options.toString());
        return options;
    }

    public void searchAndSelectVisibleDropdownOption(String option) {
        WebElement searchBox = driver.findElement(By.xpath(VISIBLE_DROPDOWN_SEARCHBOX_XPATH));

        // in searchbox only enter substring pre '(' and '-'
        String str = option.contains("(") ? option.split("\\(")[0].trim() : option;
        POHelper.sendKeys(searchBox, str);
        logger.debug("visibleOption = " + str.toString());
        waitForAngularRequestsToFinish(); // Need to wait for the drop down options to update after entering text
        waitForElement(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH), State.ELEMENT_IS_VISIBLE); // Do we need this?  Element might not be available due to data issues
        selectVisibleDropdownOption(option);
    }

    public void removeVisibleDropdownOption(String option) {
        WebElement searchBox = driver.findElement(By.xpath(VISIBLE_DROPDOWN_SEARCHBOX_XPATH));
        POHelper.sendKeys(searchBox, option);
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption("None");
    }

    public void searchAndSelectExactVisibleDropdownOption(String option) {
        WebElement searchBox = driver.findElement(By.xpath(VISIBLE_DROPDOWN_SEARCHBOX_XPATH));

        // in searchbox only enter substring pre '(' and '-'
        String str = option.contains("(") ? option.split("\\(")[0].trim() : option;

        POHelper.sendKeys(searchBox, str);
        logger.debug("visibleOption = " + str.toString());
        waitForAngularRequestsToFinish(); // Need to wait for the drop down options to update after entering text
        waitForElement(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH), State.ELEMENT_IS_VISIBLE); // Do we need this?  Element might not be available due to data issues
        selectExactVisibleDropdownOption(option);
    }

    public void searchAndSelectExactVisibleDropdownOption(WebElement we, String option) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                we.click();
                waitForAngularRequestsToFinish();
                waitWhileBusy();
                searchAndSelectExactVisibleDropdownOption(option);
                break;
            } catch (Throwable e) {
                // try again
            }
        }
    }

    public void searchAndSelectExactVisibleDropdownOptionWithoutRemovingBraces(String option) {
        WebElement searchBox = driver.findElement(By.xpath(VISIBLE_DROPDOWN_SEARCHBOX_XPATH));
        searchBox.sendKeys(option);
        logger.debug("visibleOption = " + option.toString());
        waitForAngularRequestsToFinish(); // Need to wait for the drop down options to update after entering text
        waitForElement(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH), State.ELEMENT_IS_VISIBLE); // Do we need this? Element might not be available due to data issues
        selectExactVisibleDropdownOption(option);
    }

    public void selectVisibleDropdownOption(String option) {
        WebElement visibleOption = driver.findElement(By.xpath(String.format(VISIBLE_DROPDOWN_OPTION_XPATH, option)));
        try {
            POHelper.clickJavascript(visibleOption);
        } catch (Exception e) {
            POHelper.clickOnRefreshed(By.xpath(String.format(VISIBLE_DROPDOWN_OPTION_XPATH, option)), 10);
        }
    }

    public void selectExactVisibleDropdownOption(String option) {
        option = option.contains("'") ? String.format("concat('%s', '')", option.replace("'", "', \"'\", '")) : String.format("'%s'", option); // handle single quotes in search option
        String xpath = String.format(VISIBLE_DROPDOWN_OPTION_EXACT_XPATH, option);
        WebElement visibleOption = driver.findElement(By.xpath(xpath));
        try {
            POHelper.clickJavascript(visibleOption);
        } catch (Exception e) {
            POHelper.clickOnRefreshed(By.xpath(String.format(VISIBLE_DROPDOWN_OPTION_EXACT_XPATH, option)), 10);
        }
    }

    public void selectCalendarDate(WebElement kCalendar, String date) {
        POHelper.scrollToElement(kCalendar);

        kCalendar.click();
        waitForAnimation(1);
        WebElement day = getWebDriver().findElement(By.xpath(String.format(VISIBLE_DROPDOWN_CALENDAR_OPTION_XPATH, date)));

        day.click();
        waitForAngularRequestsToFinish();
    }

    public void enterCalendarDate(WebElement kCalendarDatePicker, String date) {
        POHelper.scrollToElement(kCalendarDatePicker);
        kCalendarDatePicker.sendKeys(date);
    }

    /*
     * Starts the selection from the first element in the drop down
     */
    public String selectRandomVisibleDropdownOption() {
        return selectRandomVisibleDropdownOption(0);
    }

    public boolean isDropdownDisplayed(){
        return POHelper.isElementPresent(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH));
    }

    public String selectRandomVisibleDropdownOption(Integer value) {
        if (!isElementPresent(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH))) {
            return null;
        }
        List<WebElement> visibleOptions = driver.findElements(By.xpath(VISIBLE_DROPDOWN_OPTIONS_XPATH));
        int randomSelection = RandomUtils.nextInt(value, visibleOptions.size());
        String selection = visibleOptions.get(randomSelection).getText();
        logger.debug("random selection = " + selection);
        POHelper.clickJavascript(visibleOptions.get(randomSelection));
        waitForAngularRequestsToFinish();
        return selection;
    }

    public String selectRandomVisibleDropdownOption(Integer value, String xpathForOptions) {
        List<WebElement> visibleOptions = driver.findElements(By.xpath(xpathForOptions));
        int randomSelection = RandomUtils.nextInt(value, visibleOptions.size());
        String selection = visibleOptions.get(randomSelection).getText();
        logger.debug("random selection = " + selection);
        POHelper.clickJavascript(visibleOptions.get(randomSelection));
        waitForAngularRequestsToFinish();
        return selection;
    }

    public void waitWhileBusy() {
        POHelper.waitWhileBusy();
    }

    public void waitForLoadingToComplete() {
        POHelper.waitForKendoLoadingToComplete();
        POHelper.waitWhileBusy();
    }

    /*
     * Wait for all possible loading images to be invisible.  Should capture the two Kendo loaders and busy indicator
     * coded in each page.
     */
    public void waitForKendoLoadingToComplete() {
        POHelper.waitForKendoLoadingToComplete();
    }

    /*
     * Wait for the two (so far) modl window fade css
     * div.modal-backdrop.fade
     * div.modal.fade
     * Deprecated use waitForNoModal instead - Required for Portal tests at this moment in time.
     */
    //@Deprecated
    public void waitForModalToFadeOut() {
        String MODAL_FADE_CSS = "div.modal-backdrop.fade";
        waitUntilElementCountToBe(By.cssSelector(MODAL_FADE_CSS), 0);
        MODAL_FADE_CSS = "div.modal.fade[style*='display: block']";
        waitUntilElementCountToBe(By.cssSelector(MODAL_FADE_CSS), 0);
    }

    /*
     * Pass in a Select element and this should wait until the select has values to select from
     */
    public void waitUntilSelectOptionsPopulated(final Select select) {
        new FluentWait<WebDriver>(driver)
        .withTimeout(Duration.ofSeconds(MAX_TIMEOUT))
        .pollingEvery(Duration.ofSeconds(POLL_TIMER))
        .ignoring(NoSuchElementException.class).
        until(new  Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return (select.getOptions().size() > 1);
            }
        });
    }

    public void waitUntilElementTextHas(By locator, String text) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(getWebDriver(), MAX_TIMEOUT);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
    }

    public void waitForJavascriptToLoad() {
        int maxWaitTimeMillis = MAX_TIMEOUT;
        int pollingFrequency = 250;
        double startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + maxWaitTimeMillis) {
            String prevState = getWebDriver().getPageSource();
            try {
                Thread.sleep(POLL_TIMER*1000);
                Thread.sleep(pollingFrequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch(Exception e) {
                // ignore
            }
            if (prevState.equals(getWebDriver().getPageSource())) {
                if (prevState.equals(getWebDriver().getPageSource())) {
                    return;
                }
            }
        }
    }

    // Helper methods for <select> html elements

    /**
     * Selects the required option from the given select WebElement
     * @param select WebElement which is a select
     * @param index index item to be selected
     */
    public void selectIndexFromSelect(WebElement select, int index) {
        Select drop = new Select(select );
        drop.selectByIndex(index);
        waitForAngularRequestsToFinish();
    }


    /**
     * Selects the required option from the given select WebElement
     * @param select WebElement which is a select
     * @param option Option to be selected
     */
    public void selectOptionFromSelect(WebElement select, String option) {
        Select drop = new Select(select );
        drop.selectByVisibleText(option);
        waitForAngularRequestsToFinish();
    }

    /**
     * Selects the required value from the given select WebElement
     * @param select WebElement which is a select
     * @param option Option to be selected
     */
    public void selectValueFromSelect(WebElement select, String value) {
        Select drop = new Select(select);
        drop.selectByValue(value);
        waitForAngularRequestsToFinish();
    }

    /**
     * Selects a random option from the given select WebElement
     * @param select WebElement to have a random value selected from
     */
    public String selectRandomOptionFromSelect(WebElement select) {
        Select drop = new Select(select);
        waitUntilSelectOptionsPopulated(drop);
        List<WebElement> options = drop.getOptions();
        int randomSelection = RandomUtils.nextInt(1, options.size());
        String selection = options.get(randomSelection).getText();
        options.get(randomSelection).click(); // more efficient than calling drop.selectByIndex(randomSelection)
        // waitForAngularRequestsToFinish();
        return selection;
    }

    /**
     * Gets the first selected option for a select WebElement and returns the text
     * @param select
     * @return
     */
    public String getSelectSelectedText(WebElement select) {
        Select drop = new Select(select);
        WebElement option = drop.getFirstSelectedOption();
        return option.getText();
    }

    /**
     * Gets the first selected option for a select WebElement and returns the value
     * @param select
     * @return
     */
    public String getSelectSelectedValue(WebElement select) {
        Select drop = new Select(select);
        WebElement option = drop.getFirstSelectedOption();
        return option.getAttribute("value");
    }

    /**
     * Returns the number of selected options from the select WebElement
     * @param select
     * @return
     */
    public Integer getSelectOptionsCount(WebElement select) {
        Select drop = new Select(select);
        return drop.getAllSelectedOptions().size();
    }

    /**
     * Returns the absolute xpath to a webElement
     * @param element
     * @return
     */
    public String getWebElementXPath(WebElement element) {
        String path = (String)((JavascriptExecutor) driver).executeScript("gPt=function(c){if(c.id!==''){return'id(\"'+c.id+'\")'}if(c===document.body){return c.tagName}var a=0;var e=c.parentNode.childNodes;for(var b=0;b<e.length;b++){var d=e[b];if(d===c){return gPt(c.parentNode)+'/'+c.tagName+'['+(a+1)+']'}if(d.nodeType===1&&d.tagName===c.tagName){a++}}};return gPt(arguments[0]).toLowerCase();", element);
        return path.replaceFirst("id\\(.*?\\)", "/");
    }

    public void waitForAngularRequestsToFinish(int newTimeout) {
        driver.manage().timeouts().implicitlyWait(newTimeout, TimeUnit.SECONDS);
        waitForAngularRequestsToFinish();
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * wait for JQuery, Angular and JS
     */
    public void waitForAngularRequestsToFinish() {
        Instant start = Instant.now();
        try {
            if (getWebDriver().getCurrentUrl().contains("Helpdesk#!/home") || getWebDriver().getCurrentUrl().contains("incident")) {
                sleep(1000);
            } else {
                JSWaiter.waitJQueryAngular(getNgAppName());
            }
        } catch (WebDriverException wde) {
            logger.debug("Caught webdriver error - wait 1 second and continue!");
            sleep(1000);
        }
        Instant end = Instant.now();
        this.telemetry.sendPartialTelemetry( start.toEpochMilli(), end.toEpochMilli(), driver);
        //        logger.debug("waitJQueryAngular elapsed time: " + Duration.between(start, end).toMillis() + "ms");
    }

    public void waitUntilPageSourceUpdated(String pageSource) throws InterruptedException {
        Instant start = Instant.now();
        boolean updated = false;
        do {
            if ( !pageSource.equals(driver.getPageSource()) ) updated = true;
            Thread.sleep(1000);
        } while (!updated && Duration.between(start, Instant.now()).toMinutes() < MAX_TIMEOUT_MILLIS);
    }

    /**
     * Returns the css selector for the ng-app (Angular app) on the current page, if not found then a null string is returned;
     * @return css selector or null
     */
    private String getNgAppName() {
        if ( isElementPresent(By.xpath("//div[@ng-app]")) ) {
            WebElement ngApp = driver.findElement(By.xpath("//div[@ng-app]"));
            return "div[ng-app=" + ngApp.getAttribute("ng-app") + "]";
        } else {
            return null;
        }
    }

    public Callable<Boolean> assertTextContains(WebElement element, String substring) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String source = element.getText();
                try {
                    Assert.assertTrue(source.contains(substring));
                    return true;
                } catch (Throwable t) {
                    // refresh page
                    logger.debug("Refreshing page! Expected '" + source + "' to contain '" + substring +"'");
                    refreshPage();
                    return false;
                }
            }
        };
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }

    public Callable<Boolean> assertTextContainsOneOf(WebElement element, String csvList) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String source = element.getText().trim();
                try {
                    String[] parts = csvList.split(",");
                    for (int i = 0; i < parts.length; i++) {
                        if (source.contains(parts[i].trim())) {
                            return true;
                        }
                    }
                    return false;
                } catch (Throwable t) {
                    // refresh page
                    logger.debug("Refreshing page! Expected '" + source + "' to contain one of '" + csvList +"'");
                    refreshPage();
                    return false;
                }
            }
        };
    }

    public Callable<Boolean> assertTextDoesNotContain(WebElement element, String substring) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String source = element.getText();
                try {
                    Assert.assertTrue(element.getText().contains(substring));
                    return false;
                } catch (Throwable t) {
                    // refresh page
                    logger.debug("Refreshing page! Expected '" + source + "' NOT to contain '" + substring +"'");
                    refreshPage();
                    return true;
                }
            }
        };
    }

    public Callable<Boolean> assertTextEquals(WebElement element, String expected) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String actual = element.getText();
                try {
                    Assert.assertEquals(expected, actual);
                    return true;
                } catch (Throwable t) {
                    // refresh page
                    logger.debug("Refreshing page! Expected '" + expected + "' but was '" + actual +"'");
                    refreshPage();
                    return false;
                }
            }
        };
    }

    public Callable<Boolean> assertTextEqualsIgnoreCase(WebElement element, String expected) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String actual = element.getText();
                try {
                    assertThat(expected, is(equalToIgnoringCase(actual)));
                    return true;
                } catch (Throwable t) {
                    // refresh page
                    logger.debug("Refreshing page! Expected '" + expected + "' but was '" + actual +"'");
                    refreshPage();
                    return false;
                }
            }
        };
    }

    public Boolean isModalDisplayed() {
        return isElementPresent(By.xpath(MODAL_XPATH));
    }

    public String getModalText() {
        return driver.findElement(By.xpath(MODAL_XPATH)).getText();
    }

    public boolean isAlertVisible() {
        return isElementClickable(By.cssSelector(ALERT_CONFIRM_CSS)) || isElementClickable(By.cssSelector(ALERT_CANCEL_CSS));
    }

    public void checkForAndConfirmAlert() {
        if (isAlertVisible()) {
            PopupAlert popupAlert = new PopupAlert(getWebDriver());
            popupAlert.confirm();
            waitForAngularRequestsToFinish();
        }
    }

    public void waitForNoModal() {
        AtomicBoolean isModalDisplayed =  new AtomicBoolean(isModalDisplayed());
        with().pollInterval(TWO_SECONDS).await().atMost(MAX_TIMEOUT, SECONDS).untilFalse(isModalDisplayed);
    }

    public void clickRandomButtonInButtonsContainer(By locator) {
        List<WebElement> buttons = driver.findElements(locator);
        int randomSelection = RandomUtils.nextInt(0, buttons.size());
        WebElement button = buttons.get(randomSelection);
        button.click();
        waitForAngularRequestsToFinish();
    }

    public void setGeoLocation(String latitude, String longitude) {
        String geoLocaitonScript = "window.navigator.geolocation.getCurrentPosition =  function(success){ success({coords : {latitude: %s,longitude: %s}, timestamp: Date.now(),}); }";
        geoLocaitonScript = String.format(geoLocaitonScript, latitude, longitude);
        ((JavascriptExecutor)driver).executeScript(geoLocaitonScript);
    }

    public String getGeolocation() {
        String geoLocationScript = "window.navigator.geolocation.getCurrentPosition(function(pos){positionStr=pos.coords.latitude+\":\"+pos.coords.longitude});return positionStr;";
        return (String) ((JavascriptExecutor)driver).executeScript(geoLocationScript);
    }

    public boolean isQuestionVisible(String question, String labelXpath) {
        return isElementPresent(By.xpath(String.format(labelXpath, question)));
    }

    public String getSelectedRadio(String question, String labelXpath) {
        List<WebElement> radioOptions = getWebDriver().findElements(By.xpath(String.format(labelXpath, question) + OPTIONS_LIST));
        for (WebElement radioOption : radioOptions) {
            if (radioOption.isSelected()) {
                return radioOption.getAttribute("value");
            }
        }
        return null;
    }

    public String selectRandomAnwser(String question, String labelXpath) {
        WebElement questionDropdown = getWebDriver().findElement(By.xpath(String.format(labelXpath + DROPDOWN_SEARCH_ARROW_XPATH, question)));
        questionDropdown.click();
        waitForAngularRequestsToFinish();
        return selectRandomVisibleDropdownOption(1);
    }

    public void selectExactAnwser(String question, String answer, String labelXpath) {
        WebElement questionDropdown = getWebDriver().findElement(By.xpath(String.format(labelXpath + DROPDOWN_SEARCH_ARROW_XPATH, question)));
        questionDropdown.click();
        waitForAngularRequestsToFinish();
        selectVisibleDropdownOption(answer);
    }

    public String getSelectedText(String question, String labelXpath) {
        WebElement dropdownText = getWebDriver().findElement(By.xpath(String.format(labelXpath + DROPDOWN_SELECTED_VALUE_XPATH, question)));
        return dropdownText.getText();
    }

    public void enterFreeText(String question, String text, String labelXpath) {
        WebElement questionFreeText = getWebDriver().findElement(By.xpath(String.format(labelXpath + "/following-sibling::textArea", question)));
        questionFreeText.sendKeys(text);
    }

    public List<String> getWarnings(String xpath){
        List<String> warnings = new ArrayList<>();
        if (isElementPresent(By.xpath(xpath))) {
            for (WebElement we : driver.findElements(By.xpath(xpath))) {
                if (we.isDisplayed()) {
                    if ( !we.getText().isEmpty() ) {
                        warnings.add(we.getText());
                    }
                }
            }
        }
        return warnings;
    }

    public List<String> getValidationSummaryErrors() {
        List<String> errors = new ArrayList<>();

        if (isElementVisible(By.xpath(VALIDATION_SUMMARY_CONTAINER_XPATH))) {
            List<WebElement> visibleErrors = driver.findElements(By.xpath(VALIDATION_SUMMARY_ERROR_MESSAGES_XPATH));
            if (!visibleErrors.isEmpty()) {
                for (WebElement error : visibleErrors) {
                    String optionText = error.getText();
                    if(optionText.length() > 0) {
                        errors.add(optionText);
                    }
                }
            } else {
                // This is needed for some non validation summary that think they are. Bad HTML (and bad english!!!)
                WebElement fullError = driver.findElement(By.xpath(VALIDATION_SUMMARY_CONTAINER_XPATH));
                errors.add(fullError.getText());
            }
        }
        return errors;
    }

    public List<String> getErrors(){
        List<String> errorList = new ArrayList<String>(getWarnings(ERROR_XPATH)); //get individual errors
        errorList.addAll(getValidationSummaryErrors()); // append each error from validation summary
        return errorList;
    }

    public List<String> getAlerts(){
        List<String> alerts = getWarnings(ALERT_XPATH);
        alerts.addAll(getWarnings(ALERT2_XPATH));
        return alerts;
    }

    public String getToastMessage(){
        return driver.findElement(By.xpath(TOAST_MESSAGE_XPATH)).getText();
    }

    public String getInformationMessage(){
        return driver.findElement(By.xpath(INFORMATION_MESSAGE_XPATH)).getText();
    }

    public Boolean isValidationSummaryDisplayed() {
        waitForAngularRequestsToFinish();
        return this.isElementPresent(By.xpath(VALIDATION_SUMMARY_CONTAINER_XPATH));
    }

    public String getNoValidateMessage() {
        try {
            return driver.findElement(By.cssSelector(NOVALIDATE_CSS)).getText();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public void retryingClick(By by) {
        driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
        boolean result = false;
        int attempts = 0;
        while(attempts < 2) {
            try {
                driver.findElement(by).click();
                result = true;
                break;
            } catch(StaleElementReferenceException e) {
                logger.debug("retryingClick : StaleElementReferenceException " + e.getStackTrace());
            } catch(WebDriverException e) {
                logger.debug("retryingClick : WebDriverException " + e.getStackTrace());
            }
            // Wait for angular as the page might be refreshing
            waitForAngularRequestsToFinish();
            attempts++;
        }
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        if (!result) {
            throw new WebDriverException("Unable to click element after 3 retries" + by.toString());
        }
    }

    public void retryingJavaClick(By by) {
        driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
        boolean result = false;
        int attempts = 0;
        while(attempts < 2) {
            try {
                POHelper.clickJavascript(by);
                result = true;
                break;
            } catch(StaleElementReferenceException e) {
                logger.debug("retryingClick : StaleElementReferenceException " + e.getStackTrace());
            } catch(WebDriverException e) {
                logger.debug("retryingClick : WebDriverException " + e.getStackTrace());
            }
            // Wait for angular as the page might be refreshing
            waitForAngularRequestsToFinish();
            attempts++;
        }
        driver.manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        if (!result) {
            throw new WebDriverException("Unable to click element after 3 retries" + by.toString());
        }
    }


    /**
     * Attempts to return the height of the nav bar, if none is found then return zero height.
     * @return
     */
    public int getNavBarHeight() {
        try {
            By by = By.cssSelector(NAV_BAR_CSS);
            return POHelper.getElementHeight(by);
        } catch (Exception e) {
            return 0;
        }
    }
}
