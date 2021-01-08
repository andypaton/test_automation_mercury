package mercury.helpers;

import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.runtime.ThreadManager.getWebDriver;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TimeoutHelper {

    private static final Logger logger = LogManager.getLogger();

    public static void resetTimeout() {
        mercury.helpers.Constants.resetTimeout();
        logger.debug("Setting MAX_TIMEOUT to " + MAX_TIMEOUT + " seconds");
        getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
    }

    public static void setTimeout(int seconds) {
        mercury.helpers.Constants.setTimeout(seconds);
        logger.debug("Setting MAX_TIMEOUT to " + seconds + " seconds");
        getWebDriver().manage().timeouts().implicitlyWait(MAX_TIMEOUT, TimeUnit.SECONDS);
    }

}
