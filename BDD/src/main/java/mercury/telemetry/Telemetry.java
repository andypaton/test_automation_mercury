package mercury.telemetry;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.PageViewTelemetry;

import mercury.spring.ApplicationContextProvider;


public class Telemetry {

    private static final Logger loggerPerf = LogManager.getLogger("webPerformanceLog");

    /**
     * Log statistics to Azure Application Insights and log file
     * @param driver
     * https://developer.mozilla.org/en-US/docs/Web/API/PerformanceTiming/domComplete
     */
    public String sendTelemetry(String pageTitle, String pageURL, Long loadTime, Long timeStamp, String previousEvent, WebDriver driver) {
        // Log to local file
        String event = pageTitle.concat(pageURL).concat(loadTime.toString()).concat(timeStamp.toString());
        if (!event.equals(previousEvent)) {
            previousEvent = event;
            loggerPerf.info("PerfStats", pageTitle, pageURL, loadTime, timeStamp);
            try {
                // Check to see if app Insighs Telemetry set to on
                if (System.getProperty("telemetry").equalsIgnoreCase("false")) {
                    return null;
                }

                // Couldnt autowire the beans in so doing this
                AppInsightConfig appInsightConfig  = ApplicationContextProvider.getApplicationContext().getBean("appInsightConfig", AppInsightConfig.class);
                TelemetryClient telemetryClient = appInsightConfig.getTelemetryClient();

                pageTitle =  (pageTitle.equalsIgnoreCase("")) ? "unknown" : pageTitle.replaceAll("\\d+", "").trim();

                // This might be split out into a separate method depending on how many concants build up.
                pageTitle = (pageTitle.toLowerCase().contains("monitors")) ? pageTitle.concat(" - Monitors") : pageTitle;

                Timestamp stamp = new Timestamp(timeStamp);
                Date date = new Date(stamp.getTime());

                URI uri = new URI (pageURL.replaceAll("#", "/").trim());

                PageViewTelemetry telemetry = new PageViewTelemetry();
                telemetry.setUrl(uri);
                telemetry.setDuration(loadTime);
                telemetry.setTimestamp(date);

                telemetryClient.trackPageView(telemetry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return previousEvent;
    }

    /**
     * Used to record partial page loads - i.e. wait for angular loads
     * @param startTime
     * @param endTime
     * @param driver
     */
    public void sendPartialTelemetry(Long startTime, Long endTime, WebDriver driver) {
        if (System.getProperty("telemetry").equalsIgnoreCase("false")) {
            return;
        }
        // https://developer.mozilla.org/en-US/docs/Web/API/PerformanceTiming/domComplete
        Long loadTime = endTime - startTime;
        if (loadTime > 2500) {
            String pageTitle = "Partial " + driver.getTitle().replaceAll("\\d+", "").trim();
            String pageURL = driver.getCurrentUrl();

            Long timestamp = endTime;
            sendTelemetry(pageTitle, pageURL, loadTime, timestamp, "", driver);
        }
    }



}
