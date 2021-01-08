package mercury.helpers;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JSWaiter {

    private static final Logger logger = LogManager.getLogger();

    private static WebDriver jsWaitDriver;
    private static WebDriverWait jsWait;
    private static JavascriptExecutor jsExec;


    private static Integer SLEEP_BUFFER = 40;  // sleep 40 milliseconds for stability


    private static String getPageTitle() {
        String page = getWebDriver().getTitle();
        page = page.isEmpty() ? getWebDriver().getCurrentUrl() : page;
        return page + " page: ";
    }


    //Get the driver
    public static void setDriver (WebDriver driver) {
        jsWaitDriver = driver;
        jsWait = new WebDriverWait(jsWaitDriver, MAX_TIMEOUT);
        jsExec = (JavascriptExecutor) jsWaitDriver;
    }


    //Sleep as workaround to increase stability
    public static void sleep (Integer milliseconds) {
        long millisecondsLong = milliseconds;
        try {
            Thread.sleep(millisecondsLong);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Wait for JQuery Load
    public static void waitForJQueryLoad() {
        //Wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = driver -> ((Long) ((JavascriptExecutor) jsWaitDriver).executeScript("return jQuery.active") == 0);

        //Get JQuery is Ready
        boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");

        //Wait JQuery until it is Ready!
        if (!jqueryReady) {
            //Wait for jQuery to load
            try {
                jsWait.until(jQueryLoad);
            } catch (TimeoutException te) {
                te.printStackTrace();
            }
        }
    }

    //Wait for Angular Load
    public static void waitForAngularLoad(String id) {
        WebDriverWait wait = new WebDriverWait(jsWaitDriver, MAX_TIMEOUT);
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;

        String angularReadyScript = String.format("return angular.element('%s').injector().get('$http').pendingRequests.length === 0", id);

        //Wait for ANGULAR to load
        ExpectedCondition<Boolean> angularLoad = driver -> Boolean.valueOf(((JavascriptExecutor) driver).executeScript(angularReadyScript).toString());

        //Get Angular is Ready
        boolean angularReady = Boolean.valueOf(jsExec.executeScript(angularReadyScript).toString());

        //Wait ANGULAR until it is Ready!
        if (!angularReady) {
            //Wait for Angular to load
            wait.until(angularLoad);
        }
    }

    //Wait Until JS Ready
    public static void waitUntilJSReady() {
        WebDriverWait wait = new WebDriverWait(jsWaitDriver, MAX_TIMEOUT);
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;

        //Wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) jsWaitDriver).executeScript("return document.readyState").toString().equals("complete");

        //Get JS is Ready
        boolean jsReady =  jsExec.executeScript("return document.readyState").toString().equals("complete");

        //Wait Javascript until it is Ready!
        if (!jsReady) {
            //Wait for Javascript to load
            wait.until(jsLoad);
        }
    }

    //Wait Until JQuery and JS Ready
    public static void waitUntilJQueryReady()  {
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;

        //First check that JQuery is defined on the page. If it is, then wait AJAX
        Boolean jQueryDefined = (Boolean) jsExec.executeScript("return typeof jQuery != 'undefined'");
        if (jQueryDefined == true) {
            sleep(SLEEP_BUFFER);
            //Wait JQuery Load
            waitForJQueryLoad();
            sleep(SLEEP_BUFFER);
            //Wait JS Load
            waitUntilJSReady();

        }  else {
            logger.debug(getPageTitle() + "jQuery is not defined on this page!");
        }
    }

    //Wait Until Angular and JS Ready
    public static void waitUntilAngularReady(String id)  {
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;

        //First check that ANGULAR is defined on the page. If it is, then wait ANGULAR
        Boolean angularUndefined = (Boolean) jsExec.executeScript("return window.angular === undefined");
        if (!angularUndefined) {
            Boolean angularInjectorUndefined = (Boolean) jsExec.executeScript(String.format("return angular.element('%s').injector() === undefined", id));
            if (!angularInjectorUndefined) {
                sleep(SLEEP_BUFFER);
                //Wait Angular Load
                waitForAngularLoad(id);
                sleep(SLEEP_BUFFER);
                //Wait JS Load
                waitUntilJSReady();

            } else {
                logger.debug(getPageTitle() + "Angular injector is not defined on this page!");
            }
        }  else {
            logger.debug(getPageTitle() + "Angular is not defined on this page!");
        }
    }

    // Wait Until JQuery Angular and JS is ready
    public static void waitJQueryAngular(String id) {
        try {
            Instant start = Instant.now();
            waitUntilJQueryReady();
            waitUntilAngularReady(id);
            logger.debug(getPageTitle() + "Elapsed time waiting: " + Duration.between(start, Instant.now()).toMillis() + "ms");
        } catch (TimeoutException e) {
            logger.debug("Timeout exception thrown after: " + MAX_TIMEOUT + " seconds");
        }
    }

}

