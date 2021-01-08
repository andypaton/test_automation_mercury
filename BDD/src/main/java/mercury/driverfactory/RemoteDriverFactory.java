package mercury.driverfactory;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteDriverFactory implements IDriverFactory {

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

    private static String buildRemoteServerURI(String remoteServer, String remoteServerPort) {
        return String.format("http://%s:%s/wd/hub", remoteServer, remoteServerPort);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public WebDriver getDriver(DriverConfiguration configuration) throws Exception, IOException, ParseException {
        System.out.println("config : " + configuration.Config);
        System.out.println("env : " + configuration.Environment);

        String environment = configuration.Environment;
        String configfile = "src/test/resources/" + configuration.Config;

        System.out.println("configfile : " + configfile);
        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader(configfile));
        JSONObject envs = (JSONObject) config.get("environments");

        JSONObject envConfig = (JSONObject) envs.get(environment);

        DesiredCapabilities capabilities = new DesiredCapabilities();

        Map<String, String> envCapabilities = (Map<String, String>) envs.get(environment);
        if (envCapabilities != null) {
            Iterator it = envCapabilities.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
            }

            Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
            it = commonCapabilities.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (capabilities.getCapability(pair.getKey().toString()) == null) {
                    capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
                }
            }
        }
        String username;
        username = (String) config.get("user");
        String accessKey;
        accessKey = (String) config.get("key");

        ChromeOptions chromeOptions = new ChromeOptions();
        switch (configuration.Environment) {
        case "Chrome":
            chromeOptions.merge(capabilities);
            logger.debug("Resolution: " + envConfig.get("resolution"));
            chromeOptions.addArguments("--window-size=" + envConfig.get("resolution"));
            break;
        case "Chromeheadless":
            chromeOptions.merge(capabilities);
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--window-size=" + envConfig.get("resolution"));
            break;
        }

        // We then create a new RemoteWebDriver with the URL created earlier and the
        // desired capabilities object.
        // This would then call your GRID instance and find a match and start the
        // browser on the matching node.
        try {
            String driverURI = buildRemoteServerURI(config.get("server").toString(), config.get("port").toString());
            System.out.println(driverURI);

            _driver = new RemoteWebDriver(new URL(driverURI), chromeOptions);
        } catch (MalformedURLException e) {
            System.out.println("Failed to start webdriver : MalformedURLException : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Failed to start webdriver : " + e.getMessage());
            e.printStackTrace();
        }

        _driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.SECONDS);
        _driver.manage().timeouts().pageLoadTimeout(PAGELOAD_TIMEOUT, TimeUnit.SECONDS);
        _driver.manage().timeouts().setScriptTimeout(SCRIPT_TIMEOUT, TimeUnit.SECONDS);

        // Return the driver to the calling class.
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
