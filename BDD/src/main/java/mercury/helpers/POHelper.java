package mercury.helpers;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.microsoft.applicationinsights.core.dependencies.apachecommons.lang3.StringUtils;


public class POHelper {

    private static final Logger logger = LogManager.getLogger();


    private static final int REFRESH_RATE = 2;
    public static final int BANNER_HEIGHT = 90;
    public static final int SCROLL_OFFSET = 300;


    public static POHelper isLoaded() {
        POHelper loadHelper = new POHelper();
        return loadHelper;
    }

    public POHelper isElementIsClickable(By by) {
        try {
            new WebDriverWait(getWebDriver(), MAX_TIMEOUT).until(ExpectedConditions.elementToBeClickable(by));
            return this;
        } catch (WebDriverException e) {
            throw new Error("Element is not clickable. Timeout: " + MAX_TIMEOUT + " seconds");
        }
    }

    public POHelper isFluentElementIsClickable(By by) {
        try {
            FluentWait<WebDriver> wait = new FluentWait<WebDriver>(getWebDriver())
                    .withTimeout(Duration.ofSeconds(MAX_TIMEOUT))
                    .pollingEvery(Duration.ofSeconds(REFRESH_RATE));
            wait.until(ExpectedConditions.elementToBeClickable(by));
            return this;
        } catch (WebDriverException e) {
            throw new Error("Element is not clickable. Timeout: " + MAX_TIMEOUT + " seconds");
        }
    }

    public POHelper isElementIsVisible(By by) {
        try {
            new WebDriverWait(getWebDriver(), MAX_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(by));
            return this;
        } catch (WebDriverException e) {
            throw new Error("Element is not visible. Timeout: " + MAX_TIMEOUT + " seconds");
        }
    }

    public POHelper isFluentElementIsVisible(By by) {
        try {
            FluentWait<WebDriver> wait = new FluentWait<WebDriver>(getWebDriver())
                    .withTimeout(Duration.ofSeconds(MAX_TIMEOUT))
                    .pollingEvery(Duration.ofSeconds(REFRESH_RATE));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            return this;
        } catch (WebDriverException e) {
            throw new Error("Element is not visible. Timeout: " + MAX_TIMEOUT + " seconds");
        }
    }


    public static ExpectedCondition<Boolean> angularHasFinishedProcessing() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return Boolean.valueOf(((JavascriptExecutor) driver).executeScript("return (window.angular !== undefined) && (angular.element(document.querySelector('ng-app')) != undefined)").toString());
            }
        };
    }

    public POHelper isAngularFinishedProcessing() {
        try {
            new WebDriverWait(getWebDriver(), MAX_TIMEOUT).until(angularHasFinishedProcessing());
            return this;
        } catch (WebDriverException e) {
            throw new Error("Angular has not finished loading. Timeout: " + MAX_TIMEOUT + " seconds");
        }
    }

    public static long getYoffset() {
        return (long) ((JavascriptExecutor) getWebDriver()).executeScript("return window.pageYOffset;");
    }

    public static int getElementHeight(By by) {
        WebElement element = getWebDriver().findElement(by);
        return element.getSize().getHeight();
    }

    public static void scrollTo(long pos) {
        ((JavascriptExecutor) getWebDriver()).executeScript(String.format("window.scrollTo(0, %s);", pos));
    }

    /**
     * if element position is less than SCROLL_OFFSET from top of page then scroll to top of page
     * else scroll to a position position that is SCROLL_OFFSET above the element
     *
     * @param element
     */
    public static void scrollToElement(WebElement element) {
        int pos = Integer.valueOf(element.getLocation().getY());
        pos = pos > SCROLL_OFFSET ? pos - SCROLL_OFFSET : 0;
        scrollTo(pos);
    }

    public static void refreshPage() throws Throwable {
        // refreshing page and expecting it to be updated!
        String pageSource = getWebDriver().getPageSource();
        double startTime = System.currentTimeMillis();
        do {
            Thread.sleep(1000);
            getWebDriver().navigate().refresh();
            waitForAngularRequestsToFinish();
        } while (pageSource.equals(getWebDriver().getPageSource()) && (System.currentTimeMillis()-startTime) < (MAX_TIMEOUT*1000));
    }

    public static String getCurrentUserName() {
        String pageSource = getWebDriver().getPageSource();
        return StringUtils.substringBetween(pageSource, "currentUsername = '", "'");
    }

    /*
     * Wait for all possible loading images to be invisible.  Should capture the two Kendo loaders and busy indicator
     * coded in each page.
     */
    public static void waitForKendoLoadingToComplete() {
        Instant start = Instant.now();
        String KENDO_LOADING = "//*[contains(@class, 'k-loading')]";
        waitUntilElementCountToBe(By.xpath(KENDO_LOADING), 0);

        String KENDO_LOADING_IMAGE = "//*[contains(@class, 'k-loading-image')]";
        waitUntilElementCountToBe(By.xpath(KENDO_LOADING_IMAGE), 0);

        String KENDO_LOADING_MASK = "//*[contains(@class, 'k-loading-mask')]";
        waitUntilElementCountToBe(By.xpath(KENDO_LOADING_MASK), 0);
        logger.debug("waitForKendoLoadingToComplete elapsed time: " + Duration.between(start, Instant.now()).toMillis() + "ms");
    }

    /**
     * Returns the css selector for the ng-app (Angular app) on the current page, if not found then a null string is returned;
     * @return css selector or null
     */
    private static String getNgAppName() {
        if ( isElementPresent(By.xpath("//div[@ng-app]")) ) {
            WebElement ngApp = getWebDriver().findElement(By.xpath("//div[@ng-app]"));
            return "div[ng-app=" + ngApp.getAttribute("ng-app") + "]";
        } else {
            return null;
        }
    }

    public static void waitForAngularRequestsToFinish() {
        JSWaiter.waitJQueryAngular(getNgAppName());
    }

    public static void waitUntilElementCountToBe(By locator, Integer elementCount) {
        getWebDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            WebDriverWait wait = new WebDriverWait(getWebDriver(), MAX_TIMEOUT);
            wait.until(ExpectedConditions.numberOfElementsToBe(locator, elementCount));
        } catch (Throwable e) {
            logger.debug("Ignoring exception waiting for web element : " + e.getMessage());
        }
        getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
    }

    public static void waitUntilElementIsInvisible(By by) {
        if (isElementPresent(by)) {
            new WebDriverWait(getWebDriver(), MAX_TIMEOUT).until(ExpectedConditions.invisibilityOfElementLocated(by));
        }
    }

    public static void waitWhileBusy() {
        String html1, html2;
        double startTime = System.currentTimeMillis();
        int maxWait = MAX_TIMEOUT * 1000;
        String xpath = "//body";

        do {
            html1 = getWebDriver().findElement(By.xpath(xpath)).getAttribute("outerHTML");

            // add any progress indicators here!!!
            waitUntilElementCountToBe(By.xpath(".//div[@class='busy-indicator__wrapper']/div[contains(@aria-hidden,'false')]"), 0);
            waitUntilElementCountToBe(By.xpath("//mat-spinner"), 0);
            waitUntilElementCountToBe(By.xpath("//*[@role='progressbar']"), 0);
            waitUntilElementCountToBe(By.xpath("//*[contains(@class, 'k-loading')]"), 0);

            // wait for the spinning discs, eg. on the outstanding activities table
            waitUntilElementIsInvisible(By.xpath("//*[contains(@class, 'spin')]"));

            html2 = getWebDriver().findElement(By.xpath(xpath)).getAttribute("outerHTML");
        } while (!html1.equals(html2) && (System.currentTimeMillis() - startTime) < maxWait);
    }

    public static void waitForStorePortal() {
        String html1, html2;
        double startTime = System.currentTimeMillis();
        int maxWait = MAX_TIMEOUT * 1000;
        String xpath = "//body";

        do {
            html1 = getWebDriver().findElement(By.xpath(xpath)).getAttribute("outerHTML");

            // add any progress indicators here!!!
            waitUntilElementCountToBe(By.cssSelector("circle"), 0);

            html2 = getWebDriver().findElement(By.xpath(xpath)).getAttribute("outerHTML");
        } while (!html1.equals(html2) && (System.currentTimeMillis() - startTime) < maxWait);
    }

    /**
     * Is element present on page
     * @param by
     * @return
     */
    public static boolean isElementPresent(By by) {
        getWebDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            getWebDriver().findElement(by);
            return true;
        } catch(NoSuchElementException e) {
            return false;
        }
        finally {
            getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public static void clickOnIgnoring(By by, long timeOut) {
        new WebDriverWait(getWebDriver(), timeOut)
        .ignoring(StaleElementReferenceException.class)
        .until((WebDriver d) -> {
            d.findElement(by).click();
            return true;
        });
    }

    public static void clickOnRefreshed(By by, int timeout) {
        final WebDriverWait wait = new WebDriverWait(getWebDriver(), timeout);
        wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(by)));
        getWebDriver().findElement(by).click();
        waitForAngularRequestsToFinish();
    }

    public static void clickJavascript(WebElement element) {
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].click();", element);
        waitForAngularRequestsToFinish();
    }

    public static void clickJavascriptWithoutAngular(WebElement element) {
        ((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].click();", element);
    }

    public static void clickJavascript(By by) {
        WebElement option = getWebDriver().findElement(by);
        clickJavascript(option);
    }

    /**
     * Sometimes clickJavascript is not working & webdriver click() doesn't have a wait so tests are failing,
     * so created this click() method.
     * @param by
     */
    public static void click(By by) {
        getWebDriver().findElement(by).click();
        waitForAngularRequestsToFinish();
    }

    public static void generatePageStats(String pageName) {
        // https://developer.mozilla.org/en-US/docs/Web/API/PerformanceTiming/domComplete
        Long loadtime = (Long)((JavascriptExecutor)getWebDriver()).executeScript("return performance.timing.loadEventEnd - performance.timing.navigationStart;");
        Long loadTime = (Long)((JavascriptExecutor)getWebDriver()).executeScript("return PerformanceEntry.duration;");
        Long domLoad = (Long)((JavascriptExecutor)getWebDriver()).executeScript("return window.performance.timing.domComplete;");
        logger.info("-----------------------------------");
        logger.info("-----------------------------------");
        logger.info("Page " + pageName);
        logger.info("Load time " + loadtime);
        logger.info("Load time " + loadTime);
        logger.info("DOM Load " + domLoad);
        logger.info("-----------------------------------");
        logger.info("-----------------------------------");

        //("returnwindow.performance.timing.navigationStart");
        //("return window.performance.timing.responseStart");
        //("return window.performance.timing.domComplete");
    }

    /**
     * Note : this does not work on Chrome HEADLESS - need to discover method of enabling geolocation on chromeHeadless
     * @param latitude
     * @param longitude
     */
    public static void setGeolocation(double latitude, double longitude) {
        ((LocationContext) getWebDriver()).setLocation(new Location(latitude, longitude, 0));
    }

    /*
     * try up to maxAttempts to populate element with value
     */
    public static void sendKeys(WebElement element, String value, int maxAttempts){
        String done = element.getAttribute("value");
        String toDo = value.replaceFirst(done, "");
        element.sendKeys(toDo);
        if (maxAttempts > 0 && !value.equals(element.getAttribute("value"))) {
            sendKeys(element, value, maxAttempts - 1);
        }
    }

    /**
     * Selenium sendKeys occasionally fails because an angular process runs after every character is entered
     * This is a Selenium workaround!
     * @param element
     * @param value
     */
    public static void sendKeysV1(WebElement element, String value){
        for (int i = 0; i < value.length(); i++) {
            element.sendKeys(String.valueOf(value.charAt(i)));
            waitForAngularRequestsToFinish();
            POHelper.waitForKendoLoadingToComplete();
        }
    }

    /**
     * Selenium sendKeys occasionally fails because an angular process runs after every character is entered
     * This is a Selenium workaround!
     * Original version (V1 above) took too long for lengthy values - this version enters all but last 3 chars in one go, then takes its time for the last 3 chars
     * @param element
     * @param value
     */
    public static void sendKeys(WebElement element, String value){
        int length = value.length();
        int remain = 3;
        int index;
        if (length > remain) {
            element.sendKeys(value.substring(0, length - remain));
            waitForAngularRequestsToFinish();
            POHelper.waitForKendoLoadingToComplete();
            index = length - remain;
        } else {
            index = 0;
        }

        for (int i = index; i < length; i++) {
            element.sendKeys(String.valueOf(value.charAt(i)));
            waitForAngularRequestsToFinish();
            POHelper.waitForKendoLoadingToComplete();
        }
    }

    /**
     * Selenium sendKeys occasionally fails because an angular process runs after every character is entered
     * This is a Selenium workaround!
     * This version removes the wait for Kendo loading which is causing some failures
     * @param element
     * @param value
     */
    public static void sendKeysWithoutWaitForKendo(WebElement element, String value){
        int length = value.length();
        int remain = 3;
        int index;
        if (length > remain) {
            element.sendKeys(value.substring(0, length - remain));
            waitForAngularRequestsToFinish();
            index = length - remain;
        } else {
            index = 0;
        }

        for (int i = index; i < length; i++) {
            element.sendKeys(String.valueOf(value.charAt(i)));
            waitForAngularRequestsToFinish();
        }
    }

    /**
     * Selenium sendKeys occasionally fails because an angular proccess runs after every character is entered
     * This is a Javascript workaround!
     * @param element
     * @param value
     */
    public static void sendKeysJavascript(WebElement element, String value){
        String script = String.format("arguments[0].setAttribute('value','%s')", value);
        ((JavascriptExecutor) getWebDriver()).executeScript(script, element);
        element.sendKeys(Keys.ENTER);
    }

    public static void waitForStability() {
        String html1, html2;
        double startTime = System.currentTimeMillis();
        int maxWait = MAX_TIMEOUT * 1000;
        String xpath = "//body";

        do {
            html1 = getWebDriver().findElement(By.xpath(xpath)).getAttribute("outerHTML");
            try {
                Thread.sleep(500);
                waitForAngularRequestsToFinish();
                waitForKendoLoadingToComplete();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            html2 = getWebDriver().findElement(By.xpath(xpath)).getAttribute("outerHTML");
        } while (!html1.equals(html2) && (System.currentTimeMillis() - startTime) < maxWait);
    }

    public static String getBuildNumber() {
        return StringUtils.substringBetween(getWebDriver().getPageSource(), "\"BuildNumber\":", ",").replaceAll("\"", "");
    }

    public static boolean waitUntilPageSourceContains(String filter) throws InterruptedException {
        int timeoutInMilliSeconds = MAX_TIMEOUT * 1000;
        Instant start = Instant.now();
        String pageSource;
        boolean found = false;
        int loop = 0;
        do {
            if (loop > 0) Thread.sleep(500);
            pageSource = getWebDriver().getPageSource();
            if ( pageSource.contains(filter) ) found = true;
            loop++;
        } while (!found && Duration.between(start, Instant.now()).toMillis() < timeoutInMilliSeconds);
        logger.debug("waited " + getDurationFrom(start) + " for page source to contain: " + filter);
        return found;
    }

    private static String getDurationFrom(Instant start) {
        long milliseconds = Duration.between(start, Instant.now()).toMillis();
        return milliseconds > 1000 ? milliseconds/1000 + " seconds" : milliseconds + " milliseconds";
    }

    public static boolean waitUntilPageSourceDoesNotContain(String filter) throws InterruptedException {
        int timeoutInMilliSeconds = MAX_TIMEOUT * 1000;
        Instant start = Instant.now();
        String pageSource;
        boolean success = false;
        int loop = 0;
        do {
            if (loop > 0) Thread.sleep(500);
            pageSource = getWebDriver().getPageSource();
            if ( !pageSource.contains(filter) ) success = true;
            loop++;
        } while (!success && Duration.between(start, Instant.now()).toMillis() < timeoutInMilliSeconds);
        logger.debug("waited " + getDurationFrom(start) + " for page source NOT to contain: " + filter);
        return success;
    }

    public static boolean waitUntilConsoleLogContains(String filter) throws InterruptedException {
        int timeoutInMilliSeconds = MAX_TIMEOUT * 1000;
        Instant start = Instant.now();
        boolean found = false;
        int loop = 0;
        do {
            if (loop > 0) Thread.sleep(500);
            LogEntries logs = getWebDriver().manage().logs().get("browser");
            for (LogEntry entry : logs) {
                if (entry.toString().contains("console-api")) logger.debug(entry.toString());
                if (entry.toString().contains(filter)) found = true;
            }
            loop++;
        } while (!found && Duration.between(start, Instant.now()).toMillis() < timeoutInMilliSeconds);
        logger.debug("waited " + getDurationFrom(start) + " for console log to contain: " + filter);
        return found;
    }

}