package mercury.pagefactory;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;

import mercury.telemetry.Telemetry;

public abstract class CustomLoadableComponent<T extends CustomLoadableComponent<T>> {
    private static final int LOAD_TIMEOUT = 30;
    private static final int REFRESH_RATE = 2;
    protected Telemetry telemetry = new Telemetry();
    private static String previousEvent = null;

    /**
     * Generate a log file with performance information for page load times
     * @throws
     */
    private void logStats() {
        // https://developer.mozilla.org/en-US/docs/Web/API/PerformanceTiming/domComplete
        String pageTitle = getWebDriver().getTitle().replaceAll("\\d+", "").trim();
        String pageURL = getWebDriver().getCurrentUrl();
        Long loadTime = (Long) ((JavascriptExecutor) getWebDriver()).executeScript("return performance.timing.loadEventEnd - performance.timing.navigationStart;");
        // According to the URL above this is the correct stat but doesnt work
        // Long loadtime = (Long)((JavascriptExecutor)getWebDriver()).executeScript("return window.PerformanceEntry.duration;");
        Long timestamp = (Long) ((JavascriptExecutor) getWebDriver()).executeScript("return window.performance.timing.domComplete;");
        previousEvent = telemetry.sendTelemetry(pageTitle, pageURL, loadTime, timestamp, previousEvent, getWebDriver());
    }

    @SuppressWarnings("unchecked")
    public T get() {
        try {
            isLoaded();
            logStats();
            return (T) this;
        } catch (Error e) {
            System.out.println("Error encountered during page load: " + e.getMessage());
            load();
        }

        //        isLoaded();

        return (T) this;
    }

    protected abstract void load();

    protected abstract void isLoaded() throws Error;

    protected void waitForPageToLoad(ExpectedCondition<?> pageLoadCondition) {
        FluentWait<WebDriver> wait = new FluentWait<WebDriver>(getWebDriver())
                .withTimeout(Duration.ofSeconds(LOAD_TIMEOUT))
                .pollingEvery(Duration.ofSeconds(REFRESH_RATE));
        wait.until(pageLoadCondition);
    }

    protected abstract void load(String xpath);
}
