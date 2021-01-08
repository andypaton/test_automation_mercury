package mercury.runners;

public class RunnerHelper {

    public static void setUpProperties() {

        // select webDriver: Chrome / Chromeheadless
        if (System.getProperty("web.driver") == null){
            System.setProperty("web.driver", "Chrome");
        }

        // when running on Jenkins determine which config to use
        if (System.getProperty("web.driver.config") == null){
            System.setProperty("web.driver.config", "webdriver.local.conf.json");
        }

        // take screenshots - slows performance and can lead to memory issues if used on a large test suite
        if (System.getProperty("screenshots") == null){
            System.setProperty("screenshots", "false");
        }

        // allow working hours to be updated
        if (System.getProperty("updateWorkingHours") == null){
            System.setProperty("updateWorkingHours", "true");
        }

        // allow overriding of system toggles
        if (System.getProperty("setSystemToggles") == null){
            System.setProperty("setSystemToggles", "false");
        }

        // send telemetry to Azure Insights
        if (System.getProperty("telemetry") == null){
            System.setProperty("telemetry", "false");
        }

        // verify web page terminology
        if (System.getProperty("terminology") == null){
            System.setProperty("terminology", "false");
        }

        // when impersonating - either impersonate via gui or api
        if (System.getProperty("impersonationMethod") == null){
            System.setProperty("impersonationMethod", "api");
        }

        // login directly or impersonate as user (direct login can only be done if non-AD user or an automation test user on Walmart)
        if (System.getProperty("impersonation") == null){
            System.setProperty("impersonation", "false");
        }

    }
}
