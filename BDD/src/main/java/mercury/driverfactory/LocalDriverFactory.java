package mercury.driverfactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class LocalDriverFactory implements IDriverFactory {

    private static final Logger logger = LogManager.getLogger();

    private static final int IMPLICIT_WAIT = 10;
    private static final int PAGELOAD_TIMEOUT = 30;
    private static final int SCRIPT_TIMEOUT = 10;

    // DesiredCapabilites is used to add specific browser requirements to
    // RemoteWebDriver, such as version, OS.
    private DesiredCapabilities _capabilities;
    // Declare an instance of an WebDriver to assign a browser driver to.
    // We use WebDriver as all browsers use this interface, so all compatible.
    private WebDriver _driver;

    @SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
    @Override
    public WebDriver getDriver(DriverConfiguration configuration) throws FileNotFoundException, IOException, ParseException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        LoggingPreferences logPrefs = new LoggingPreferences();

        logger.debug("config : " + configuration.Config);
        logger.debug("env : " + configuration.Environment);

        String environment = configuration.Environment;
        String configfile = "src/test/resources/" + configuration.Config;

        logger.debug("configfile : " + configfile);
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader(configfile));
        JSONObject envs = (JSONObject) config.get("environments");

        JSONObject envConfig = (JSONObject) envs.get(environment);

        Map<String, String> envCapabilities = (Map<String, String>) envConfig.get("capabilities");
        Iterator it = envCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
        }
        logger.debug(capabilities);

        Map prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.geolocation", 1); // 1:allow 2:block

        System.setProperty("scaleScreenshot", "false");

        // capabilities.setCapability("elementScrollBehanviour",0);
        // A simple switch statement to determine which driver/service to create.
        switch (configuration.Environment) {
        case "Chrome":
            logger.debug("Setting capabilities for Chrome");
            String chromeDriverPath = System.getProperty("user.dir") + "//src//test//resources//BrowserDrivers//chromedriver.exe";
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);

            logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
            // create chrome instance
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.merge(capabilities);

            chromeOptions.setExperimentalOption("prefs", prefs);
            chromeOptions.addArguments("--window-size=" + envConfig.get("resolution"));

            if (System.getProperty("user.name").toLowerCase().contains("jenkins")) {
                // when Jenkins launches chromedriver it doesnt allow you to maximise or resize the window to larger than the systems screen resolution
                // workaround for small screen resolution - which prevents browser window being set to large enough size
                // reduce font size
                logger.debug("Re-scaling font to 75%");
                chromeOptions.addArguments("force-device-scale-factor=0.75");
                chromeOptions.addArguments("high-dpi-support=0.75");
                chromeOptions.addArguments("--font-render-hinting=slight");
                System.setProperty("scaleScreenshot", "true");
            }

            _driver = new ChromeDriver(chromeOptions);

            break;
        case "Chromeheadless":
            logger.debug("Setting capabilities for Chromeheadless");
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "//src//test//resources//BrowserDrivers//chromedriver.exe");

            logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
            // create chrome instance
            ChromeOptions chromeOptionsHeadless = new ChromeOptions();
            chromeOptionsHeadless.merge(capabilities);
            chromeOptionsHeadless.addArguments("--headless");
            chromeOptionsHeadless.addArguments("--window-size=" + envConfig.get("resolution"));

            chromeOptionsHeadless.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
            chromeOptionsHeadless.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
            chromeOptionsHeadless.addArguments("--disable-infobars"); //https://stackoverflow.com/a/43840128/1689770
            chromeOptionsHeadless.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/50725918/1689770
            chromeOptionsHeadless.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
            chromeOptionsHeadless.addArguments("--disable-gpu");

            chromeOptionsHeadless.setExperimentalOption("prefs", prefs);

            _driver = new ChromeDriver(chromeOptionsHeadless);
            break;
        case "IE":
            logger.debug("Setting capabilities for IE");
            String path = System.getProperty("user.dir") + "//src//test//resources//BrowserDrivers//IEDriverServer.exe";
            System.setProperty("webdriver.ie.driver", path);

            DesiredCapabilities dc = DesiredCapabilities.internetExplorer();
            dc.setCapability(CapabilityType.BROWSER_NAME, "IE");
            dc.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
            dc.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            dc.setCapability(CapabilityType.SUPPORTS_LOCATION_CONTEXT, true);
            dc.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
            dc.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
            dc.setCapability(InternetExplorerDriver.NATIVE_EVENTS, true);
            dc.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            dc.setCapability("ignoreProtectedModeSettings", true);

            InternetExplorerOptions ieOptions = new InternetExplorerOptions();
            ieOptions.merge(dc);

            _driver = new InternetExplorerDriver(ieOptions);
            break;

        case "Firefox":
            logger.debug("Setting capabilities for Firefox");
            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "//src//test//resources//BrowserDrivers//geckodriver.exe");
            // create Firefox instance
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.merge(capabilities);

            _driver = new FirefoxDriver(firefoxOptions);
            break;
            // If a string isn't matched, it will default to FireFoxDriver
        default:
            _driver = new FirefoxDriver(_capabilities);
            break;
        }

        _driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.SECONDS);
        _driver.manage().timeouts().pageLoadTimeout(PAGELOAD_TIMEOUT, TimeUnit.SECONDS);
        _driver.manage().timeouts().setScriptTimeout(SCRIPT_TIMEOUT, TimeUnit.SECONDS);
        _driver.manage().window().maximize();
        // Return the driver instance to the calling class.
        return _driver;
    }

    @Override
    public void DestroyWebDriver() {
        try {
            System.out.println("DestroyWebDriver");
            _driver.close();
            _driver.quit();
            _driver = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
