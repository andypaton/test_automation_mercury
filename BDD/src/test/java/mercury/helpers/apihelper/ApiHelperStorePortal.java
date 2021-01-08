package mercury.helpers.apihelper;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.api.models.job.Job;
import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.rest.RestService;

@Component
public class ApiHelperStorePortal {

    @Autowired private TestData testData;
    @Autowired private DbHelperSites dbHelperSites;

    private static final Logger logger = LogManager.getLogger();

    protected static String mercuryCookie;
    protected static RestService restService = new RestService();

    public void logInToStorePortal() throws Exception {
        String storePortalUrl = "https://uswm-jpmobility-test-api.azurewebsites.net";

        String userName = dbHelperSites.getRandomSiteCode();
        testData.put("storePortalUserName", userName);
        String password = "Password1";
        testData.put("storePortalPassword", password);
        try {
            logger.debug("username: " + userName + ", password: " + password);
            postLoginPageStorePortal(storePortalUrl, userName, password);
            testData.put("apiUser", userName);
        } catch (AssertionError e) {
            logger.debug("Failed to login as username: " + userName + ", password: " + password);
        }

        testData.copy(testData);
    }

    public void postLoginPageStorePortal(String url, String username, String password) throws ClientProtocolException, IOException {
        String browserId = UUID.randomUUID().toString().toUpperCase();
        testData.put("browserId", browserId);
        String payload = "grant_type=password&clientId=1&username=" + username + "&password=" + password + "&browserId=" + browserId;
        String response = restService.sendPostRequestWithRedirectionStorePortal(url + "/token", payload);
        assertTrue(restService.getStatusCode() == 200);
        String accessToken = StringUtils.substringBetween(response, "{\"access_token\":\"", "\"");
        testData.put("accessToken", accessToken);
        if  (restService.getMercuryCookie() == null) {
            logger.debug(" ***** WARNING: all tests making an API call will fail because no MERCURY COOKIE has been returned!!! *****");
        }
        mercuryCookie = restService.getMercuryCookie();
    }

    public void createNonHelpdeskManagerChase(int jobRef, String description, String contactName, String contactNumber) throws ClientProtocolException, IOException {
        String url = "https://uswm-jpmobility-test-api.azurewebsites.net";
        String payload = "{\"JobReference\":%d,\"ChaseDescription\":\"%s\",\"ContactName\":\"%s\",\"ContactNumber\":\"%s\","
                + "\"RequiresCall\":false,\"ChaseTypeId\":1,\"ResolutionDescription\":null,\"CreatorId\":null}";
        payload = String.format(payload, jobRef, description, contactName, contactNumber);
        logger.debug("Creating Chase from the Store Portal: " + payload);
        String response = restService.sendPostRequestWithRedirectionStorePortalBearerToken(url + "/portal/portaljobchase/add", payload, testData.getString("accessToken"));
        assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
    }

    public Job createJobInStorePortal(int assetClassificationId, int assetId, int faultTypeId, String description, String jobTitle, int locationId, String telephone, String siteContact, int siteId) throws Exception {

        String url = "https://uswm-jpmobility-test-api.azurewebsites.net";
        String payload = "{\"assetClassificationId\":%d,\"assetId\":%d,\"customQuestions\":[{\"questionId\":118,\"answer\":\"No\",\"displayOrder\":1}],"
                + "\"faultTypeId\":%d,\"isDeferred\":false,\"jobDescription\":\"%s\",\"jobSourceEnum\":8,\"jobTitle\":\"%s\",\"locationId\":%d,"
                + "\"phoneNumber\":\"%s\",\"siteContact\":\"%s\",\"siteId\":%d}";
        payload = String.format(payload, assetClassificationId, assetId, faultTypeId, description, jobTitle, locationId, telephone, siteContact, siteId);
        logger.debug("Creating Job from the Store Portal: " + payload);
        String response = restService.sendPostRequestWithRedirectionStorePortalBearerToken(url + "/portal/portaljob/newjob", payload, testData.getString("accessToken"));
        assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        String jobReference = StringUtils.substringBetween(response, "\"jobReference\":", ",");
        logger.debug("Job created from the Store Portal: " + jobReference);
        testData.put("jobReference", jobReference);

        Job job = new Job();
        job.setSiteId(siteId);
        job.setJobTypeId(1);
        job.setIsDeferred(false);
        job.setJobSourceId(8);
        job.setAssetClassificationId(assetClassificationId);
        job.setAssetId(assetId);
        job.setFaultTypeId(faultTypeId);
        job.setDescription(description);
        job.setLocationId(locationId);
        job.setSpecificResourceRequestId(null);
        job.setJobReference(Integer.valueOf(jobReference));

        return job;
    }

}
