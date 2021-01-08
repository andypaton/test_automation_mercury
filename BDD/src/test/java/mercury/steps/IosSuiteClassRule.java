package mercury.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.ExternalResource;
import org.springframework.test.context.ContextConfiguration;

import mercury.config.CucumberConfig;

@ContextConfiguration(classes=CucumberConfig.class)
public class IosSuiteClassRule extends ExternalResource {

    private static final Logger logger = LogManager.getLogger();

    @Override
    protected void before() throws Throwable {
        //        WebDriverHelper webDriverHelper = new WebDriverHelper();
        //        ThreadManager.webDriver.set(webDriverHelper.getNewWebDriver());
        //
        //        logger.debug("WebDriver instance created : " + getWebDriver().hashCode() + " [JVM process name : " + ManagementFactory.getRuntimeMXBean().getName() + "]");
    }

    @Override
    protected void after() {
        //        try {
        //            getWebDriver().close();
        //            getWebDriver().quit();
        //            logger.debug("WebDriver instance destroyed : " + getWebDriver().hashCode());
        //        } catch (Exception e) {
        //            // ignore
        //        }
    }

}

