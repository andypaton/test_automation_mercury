package mercury.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:LOCAL_environment.properties", "file:${target.env}"}, ignoreResourceNotFound = true)
public class PropertyHelper {

    private static final Logger logger = LogManager.getLogger();

    @Value("${mercury.url}")
    String mercuryUrl;

    @Value("${mercury.notificationUrl:#{null}}")
    String mercuryNotificationUrl;

    @Value("${storePortal.url:#{null}}")
    String storePortalUrl;

    @Value("${serviceChannel.url:#{null}}")
    String serviceChannelUrl;

    @Value("${mobile.url}")
    String mobileUrl;

    @Value("${screenshots:#{null}}")
    private String screenshots;

    @Value("${jsonHelperURL}")
    private String jsonHelperURL;

    @Value("${environment}")
    private String env;

    @Value("${accountsPayableCarousel:#{null}}")
    private String accountsPayableCarousel;


    public boolean showScreenshots(){
        logger.debug("Show ALL screenshots set to : " + screenshots);
        return Boolean.valueOf(screenshots);
    }

    public String getMercuryUrl() {
        return mercuryUrl;
    }

    public String getMercuryNotificationUrl() {
        return mercuryNotificationUrl;
    }

    public String getStorePortalUrl() {
        return storePortalUrl;
    }

    public String getServiceChannelUrl() {
        return serviceChannelUrl;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMercuryUrl(String mercuryUrl) {
        this.mercuryUrl = mercuryUrl;
    }

    public void setMercuryNotificationUrl(String mercuryNotificationUrl) {
        this.mercuryUrl = mercuryNotificationUrl;
    }

    public String getJsonHelperURL() {
        return jsonHelperURL;
    }

    public String getEnv() {
        return env;
    }

    public String getAccountsPayableCarousel() {
        return accountsPayableCarousel;
    }

}
