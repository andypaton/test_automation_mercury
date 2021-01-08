package mercury.steps;

import static mercury.runtime.ThreadManager.getWebDriver;

import java.lang.management.ManagementFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.ExternalResource;
import org.springframework.test.context.ContextConfiguration;

import mercury.config.CucumberConfig;
import mercury.helpers.WebDriverHelper;
import mercury.runtime.ThreadManager;


@ContextConfiguration(classes=CucumberConfig.class)
public class WebSuiteClassRule extends ExternalResource {

    private static final Logger logger = LogManager.getLogger();


    @Override
    protected void before() throws Throwable {

        WebDriverHelper webDriverHelper = new WebDriverHelper();
        ThreadManager.webDriver.set(webDriverHelper.getNewWebDriver());

        logger.debug("WebDriver instance created : " + getWebDriver().hashCode() + " [JVM process name : " + ManagementFactory.getRuntimeMXBean().getName() + "]");
    }

    @Override
    protected void after() {
        try {
            getWebDriver().close();
            getWebDriver().quit();
            logger.debug("WebDriver instance destroyed : " + getWebDriver().hashCode());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}

