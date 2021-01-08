package mercury.helpers.apihelper;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.databuilders.TestData;

import mercury.helpers.FileHelper;

@Component
public class ApiHelperParts extends ApiHelper {

    @Autowired private TestData testData;

    private static final Logger logger = LogManager.getLogger();

    /**
     * Create a parts request using Portal (old method).
     * If this breaks, use the partsRequestUsingMobile() method instead as techs use mobile to create parts orders.
     *
     * To get to this page manually:
     * Log a job and assign to city tech and start job > Login to portal as the tech and update job with 'Awaiting Parts' status.
     * You should then be taken to create a parts request page.
     *
     * @throws Throwable
     */
    public void partsRequest(int jobReference, String partCode, String supplierCode, String supplierName, int quantity) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/ResourcePortal/api/Parts/PartsRequest";

        String jsonPayload = FileHelper.getResourceFile("apiTemplates/parts/", "PartsRequest.json");
        jsonPayload = jsonPayload.replace(":jobReference", String.valueOf(jobReference));
        jsonPayload = jsonPayload.replace(":partCode", String.valueOf(partCode));
        jsonPayload = jsonPayload.replace(":supplierCode", String.valueOf(supplierCode));
        jsonPayload = jsonPayload.replace(":supplierName", String.valueOf(supplierName));
        jsonPayload = jsonPayload.replace(":quantity", String.valueOf(quantity));

        restService.sendPostRequest(url, jsonPayload, appUserCookieStore);
        int statusCode = restService.getStatusCode();
        logger.debug("PartsRequest response status code: " + statusCode);
        assertTrue("Failed to POST PartsRequest: " + jsonPayload, statusCode == 200);
    }

    /**
     * Create a parts request using mobile
     * @return full response string
     * @throws Throwable
     */
    public void partsRequestUsingMobile(int jobReference, String partCode, String description, String supplierCode, int quantity, float unitPrice, int epochId, int siteId, String geolocation, String date) throws Exception {
        String url = propertyHelper.getMobileUrl() + "/CityWS/syncNewRequests";

        String jsonPayload;
        if (testData.getString("originalJobStatus").equalsIgnoreCase("Parts Requested Offsite")) {
            jsonPayload = FileHelper.getResourceFile("apiTemplates/parts/", "MobileOffsitePartsRequest.json");
        } else {
            jsonPayload = FileHelper.getResourceFile("apiTemplates/parts/", "NewMobilePartsRequest.json");
            jsonPayload = jsonPayload.replaceAll(":siteId", String.valueOf(siteId));
            jsonPayload = jsonPayload.replaceAll(":geolocation", String.valueOf(geolocation));
        }
        jsonPayload = jsonPayload.replaceAll(":jobReference", String.valueOf(jobReference));
        jsonPayload = jsonPayload.replaceAll(":partCode", String.valueOf(partCode));
        jsonPayload = jsonPayload.replaceAll(":description", String.valueOf(description));
        jsonPayload = jsonPayload.replaceAll(":supplierCode", String.valueOf(supplierCode));
        jsonPayload = jsonPayload.replaceAll(":quantity", String.valueOf(quantity));
        jsonPayload = jsonPayload.replaceAll(":unitPrice", String.valueOf(unitPrice));
        jsonPayload = jsonPayload.replaceAll(":date", String.valueOf(date));
        jsonPayload = jsonPayload.replaceAll(":epochId", String.valueOf(epochId));

        restService.sendPostRequestForMobileWithBearerToken(url, jsonPayload, testData.getString("accessToken"));
        int statusCode = restService.getStatusCode();
        logger.debug("PartsRequest response status code: " + statusCode);
        assertTrue("Failed to POST PartsRequestWithMobile: " + jsonPayload, statusCode == 200);
    }

    /**
     * Get mobile authentication token
     * @return full response string containing the access token
     * @throws Throwable
     */
    public String getMobileAuthenticationToken(String username) throws Exception {
        String url = propertyHelper.getMobileUrl() + "/token";

        String payload = "username=:username%3D%3D&password=z5CfgC%2FccT2zFWvpnh8%2FJQ%3D%3D&clientid=1&grant_type=password";
        payload = payload.replace(":username", username);

        String response = restService.sendPostRequestForMobileAuthentication(url, payload);
        String accessToken = StringUtils.substringBetween(response, "{\"access_token\":\"", "\"");
        logger.debug("Mobile Login response string: " + response);
        testData.put("accessToken", accessToken);
        int statusCode = restService.getStatusCode();
        logger.debug("Mobile Login response status code: " + statusCode);
        assertTrue("Failed to POST MobileAuthenticationToken: " + payload, statusCode == 200);
        return response;
    }
}
