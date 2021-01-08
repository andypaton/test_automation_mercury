package mercury.runtime;

import org.openqa.selenium.WebDriver;

public class ThreadManager {

    // Threads for running in parallel
    public static ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();

    public static WebDriver getWebDriver() {
        return webDriver.get();
    }

}

