package mercury.helpers;

import org.openqa.selenium.WebDriver;

import mercury.driverfactory.DriverConfiguration;
import mercury.driverfactory.IDriverFactory;
import mercury.driverfactory.LocalDriverFactory;
import mercury.driverfactory.RemoteDriverFactory;

public class WebDriverHelper {

    public WebDriver getNewWebDriver() {
        DriverConfiguration config = new DriverConfiguration(System.getProperty("web.driver.config"), System.getProperty("web.driver"));
        return setDriver(config);
    }

    private WebDriver setDriver(DriverConfiguration configuration) {

        IDriverFactory lDriverFactory;

        try {
            String target = configuration.Config;
            if (target.equalsIgnoreCase("webdriver.local.conf.json")) {
                lDriverFactory = new LocalDriverFactory();
                return lDriverFactory.getDriver(configuration);

            } else if (target.equalsIgnoreCase("webdriver.remote.conf.json")) {
                lDriverFactory = new RemoteDriverFactory();
                return lDriverFactory.getDriver(configuration);
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;

    }

}
