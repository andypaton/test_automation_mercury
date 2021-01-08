package mercury.telemetry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.microsoft.applicationinsights.TelemetryClient;

@Component
@PropertySource(value = {"classpath:LOCAL_environment.properties", "file:${target.env}"},
ignoreResourceNotFound = true)
public class AppInsightConfig {

    @Value("${instrumentationKey}")
    private String instrumentationKey;

    private TelemetryClient telemetryClient = new TelemetryClient();

    public TelemetryClient getTelemetryClient() {
        if (telemetryClient.getContext().getInstrumentationKey() == null) {
            telemetryClient.getContext().setInstrumentationKey(instrumentationKey);
        }
        return telemetryClient;
    }
}
