package mercury.helpers.apihelper;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.databuilders.TestData;
import mercury.rest.RestService;
import mercury.runtime.RuntimeState;

@Component
public class ApiHelperServiceChannel {

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;

    private static final Logger logger = LogManager.getLogger();

    protected static RestService restService = new RestService();

    public void getBearerToken() throws ClientProtocolException, IOException {
        String url = "https://mercuryserviceprovidersp20190508043218.azurewebsites.net/connect/token";

        String payload = "grant_type=client_credentials&client_id=sc_etl_pipeline_Walmart_API&client_secret=c36898d3-f2ee-2917-d4d9-67ba75d9df8a";
        String response = restService.sendPostRequestWithRedirectionStorePortal(url, payload);
        assertTrue(restService.getStatusCode() == 200);
        String accessToken = StringUtils.substringBetween(response, "{\"access_token\":\"", "\"");
        testData.put("accessToken", accessToken);
    }

    public void runJobLoggedEventInjectionAPI(int jobReference) throws ClientProtocolException, IOException {
        String url = "https://web-test-city-extract.azurewebsites.net/api/eventinjection/joblogged/";
        url += jobReference;

        String response = restService.sendGetRequestBearerToken(url, testData.getString("accessToken"));
        int statusCode = restService.getStatusCode();

        if (statusCode != 200) {
            runtimeState.scenario.write("ERROR - STATUS CODE IS: " + statusCode);
            runtimeState.scenario.write("API ERROR: " + response);
        }
    }

    public void runPPMJobLoggedEventInjectionAPI(int jobReference) throws ClientProtocolException, IOException {
        String url = "https://web-test-city-extract.azurewebsites.net/api/eventinjection/ppmlogged/";
        url += jobReference;

        String response = restService.sendGetRequestBearerToken(url, testData.getString("accessToken"));
        int statusCode = restService.getStatusCode();

        if (statusCode != 200) {
            runtimeState.scenario.write("ERROR - STATUS CODE IS: " + statusCode);
            runtimeState.scenario.write("API ERROR: " + response);
        }
    }
}
