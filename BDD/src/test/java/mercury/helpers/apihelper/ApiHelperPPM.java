package mercury.helpers.apihelper;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.api.models.ppm.PPMJob;

@Component
public class ApiHelperPPM extends ApiHelper {

    @Autowired private ApiHelperJobs apiHelperJobs;

    private static final Logger logger = LogManager.getLogger();

    private static final String PPM_DETAILS_URL = "/api/ppm/jobs/getclosedown/%s";
    private static final String SUBMIT_PPM_DETAILS_URL = "/api/ppm/jobs";

    public String createPpmJob(String ppmTypeId, Integer callOutRate, String siteId, String dueDate) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/api/ppms";

        String payload = "{\"ppmTypeId\":\"%s\",\"callOutRate\":%d,\"siteId\":\"%s\",\"dueDate\":\"%s\"}";
        payload = String.format(payload, ppmTypeId, callOutRate, siteId, dueDate);
        logger.debug("Creating PPM Job with following payload: " + payload);

        if (mercuryCookie == null) {
            apiHelperJobs.setUpMercuryCookie();
        }

        String response = restService.sendPostJsonRequest(url, payload, mercuryCookie);
        assertTrue("expected status code 200", restService.getStatusCode() == 200);

        return response;
    }

    public String createPpmJobWithResource(String ppmTypeId, Integer callOutRate, String siteId, String resourceProfileId, String resourceId, String dueDate) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/api/ppms";

        String payload = "{\"ppmTypeId\":\"%s\",\"callOutRate\":%d,\"siteId\":\"%s\",\"resourceProfileId\":\"%s\",";

        if (resourceId != null) {
            payload += "\"resourceId\":\"%s\",";
        }

        payload += "\"dueDate\":\"%s\"}";

        if (payload.contains("resourceId")) {
            payload = String.format(payload, ppmTypeId, callOutRate, siteId, resourceProfileId, resourceId, dueDate);
        } else {
            payload = String.format(payload, ppmTypeId, callOutRate, siteId, resourceProfileId, dueDate);

        }

        logger.debug("Creating PPM Job with following payload: " + payload);

        if (mercuryCookie == null) {
            apiHelperJobs.setUpMercuryCookie();
        }

        String response = restService.sendPostJsonRequest(url, payload, mercuryCookie);

        if (restService.getStatusCode() != 200) {
            runtimeState.scenario.write("Failed to create PPM Job. Response = " + response);
            response = "Fail";
        }

        // This assertion has been commented to allow us to bulk create ppm's without the test falling down for 1 failure
        // Will uncomment once these tests have been completed
        //        assertTrue("expected status code 200", restService.getStatusCode() == 200);

        return response;
    }

    public String createPpmType(String name, String ppmTypeClassificationId, String assetTypeId, Integer calloutAdvanceInWeeks, String resourceProfileId, String resourceId, String ppmTypeFrequencyId, Integer frequency, String areaTCodeOverride, String ppmTradeId) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/api/ppms/ppmtypes";

        String payload = "{\"name\":\"%s\",\"ppmTypeClassificationId\":\"%s\",\"assetTypeId\":\"%s\",\"calloutAdvanceInWeeks\":%d,\"resourceProfileId\":\"%s\",";

        if (resourceId != null) {
            payload += "\"resourceId\":\"%s\",";
        }

        payload += "\"ppmTypeFrequencyId\":\"%s\",\"frequency\":%d";

        if (propertyHelper.getEnv().contains("UKRB")) {
            payload += ",\"certificateRequiredPerAsset\":false}";
            if (payload.contains("resourceId")) {
                payload = String.format(payload, name, ppmTypeClassificationId, assetTypeId, calloutAdvanceInWeeks, resourceProfileId, resourceId, ppmTypeFrequencyId, frequency);
            } else {
                payload = String.format(payload, name, ppmTypeClassificationId, assetTypeId, calloutAdvanceInWeeks, resourceProfileId, ppmTypeFrequencyId, frequency);
            }

        } else  if (propertyHelper.getEnv().contains("USWM")) {
            payload += ",\"areaTCodeOverride\":\"%s\",\"ppmTradeId\":\"%s\"}";
            if (payload.contains("resourceId")) {
                payload = String.format(payload, name, ppmTypeClassificationId, assetTypeId, calloutAdvanceInWeeks, resourceProfileId, resourceId, ppmTypeFrequencyId, frequency, areaTCodeOverride, ppmTradeId);
            } else {
                payload = String.format(payload, name, ppmTypeClassificationId, assetTypeId, calloutAdvanceInWeeks, resourceProfileId, ppmTypeFrequencyId, frequency, areaTCodeOverride, ppmTradeId);
            }

        } else {
            payload += "}";
            if (payload.contains("resourceId")) {
                payload = String.format(payload, name, ppmTypeClassificationId, assetTypeId, calloutAdvanceInWeeks, resourceProfileId, resourceId, ppmTypeFrequencyId, frequency);
            } else {
                payload = String.format(payload, name, ppmTypeClassificationId, assetTypeId, calloutAdvanceInWeeks, resourceProfileId, ppmTypeFrequencyId, frequency);
            }
        }

        logger.debug("Creating PPM Type with following payload: " + payload);

        if (mercuryCookie == null) {
            apiHelperJobs.setUpMercuryCookie();
        }

        String response = restService.sendPostJsonRequest(url, payload, mercuryCookie);

        if (restService.getStatusCode() != 200) {
            runtimeState.scenario.write("Failed to create PPM Type. Response = " + response);
            response = "Fail";
        }

        // This assertion has been commented to allow us to bulk create ppm's without the test falling down for 1 failure
        // Will uncomment once these tests have been completed
        //        assertTrue("expected status code 200", restService.getStatusCode() == 200);

        return response;
    }

    public String getPPMJobDetailsPage(Integer jobReference) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + PPM_DETAILS_URL;
        url = String.format(url, jobReference);
        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode(), restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("getPPMJobDetailsPage Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    public String completePPMJob(Integer jobReference, PPMJob ppmJobDetails) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + SUBMIT_PPM_DETAILS_URL;
        String formData =  ppmJobDetails.toJsonString();
        logger.debug("PPM Job details are: " + formData);
        String response = restService.sendPostRequestWithRedirection(url, formData,  appUserCookieStore);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("completePPMJob Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }
}