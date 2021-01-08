package mercury.helpers.apihelper;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import mercury.api.models.fundingrequest.FundingRequest;
import mercury.api.models.resourceAssignmentFunding.Authorise;

@Component
public class ApiHelperResourceAssignmentFunding extends ApiHelper {

    private static final Logger logger = LogManager.getLogger();

    private static final String RESOURCE_ASSIGNMENT_FUNDING_URL = "/api/ResourceAssignmentFunding/%s/authorise";

    public FundingRequest authorise (Integer fundingRequestId, Authorise authorise) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + RESOURCE_ASSIGNMENT_FUNDING_URL;
        url = String.format(url, fundingRequestId);

        logger.debug("http PUT: " + url + "\nPayload: " + authorise.toJsonString());
        String response = restService.sendPutJsonRequest(url, authorise.toJsonString(), mercuryCookie);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("funding request " + authorise.toJsonString());
            logger.debug("response " + response.substring(0, Math.min(500, response.length())));
            logger.debug("authorise Response code " + restService.getStatusCode());
            throw e;
        }

        Gson gson = new Gson();
        return gson.fromJson(restService.getResponseString(), FundingRequest.class);
    }
}
