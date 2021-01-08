package mercury.helpers.apihelper;

import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.restassured.response.Response;
import mercury.helpers.FileHelper;
import mercury.helpers.POHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.URLHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperIncidents;
import mercury.rest.RestAssuredHelper;
import mercury.rest.RestService;
import mercury.runtime.RuntimeState;

@Component
public class ApiHelper {

    @Autowired protected PropertyHelper propertyHelper;
    @Autowired protected RuntimeState runtimeState;
    @Autowired protected DbHelper dbHelper;
    @Autowired protected DbHelperIncidents dbHelperIncidents;

    private static final Logger logger = LogManager.getLogger();

    protected static String requestVerificationTokenCookie;
    protected static String mercuryCookie;
    protected static String appUserRequestVerificationTokenCookie;
    protected static String appUserMercuryCookie;
    protected static CookieStore appUserCookieStore;
    protected static RestService restService = new RestService();
    protected static URLHelper urlHelper = new URLHelper();

    public void setupCookies(String url, String username, String password) throws Exception {
        if ( !getWebDriver().getCurrentUrl().contains(propertyHelper.getMercuryUrl()) || !getWebDriver().getCurrentUrl().contains(propertyHelper.getStorePortalUrl())) {
            if (runtimeState.scenario.getSourceTagNames().contains("@store_portal")) {
                getWebDriver().get(propertyHelper.getStorePortalUrl());
                POHelper.waitForStorePortal();
            } else {
                getWebDriver().get(propertyHelper.getMercuryUrl());
                POHelper.waitWhileBusy();
            }
        }

        if (getWebDriver().getCurrentUrl().contains(propertyHelper.getMercuryUrl())) {
            if (requestVerificationTokenCookie == null) {
                requestVerificationTokenCookie = "__RequestVerificationToken=" + StringUtils.substringBetween(getWebDriver().manage().getCookies().toString(), "__RequestVerificationToken=", ";");
            }

            String response = getLoginPage(url).replace("<!DOCTYPE html>", "");
            String verificationRequestToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");
            postLoginPage(url, username, password, verificationRequestToken);
        }
    }

    public String getLoginPage(String url) throws ClientProtocolException, IOException {
        String body;
        if (url.contains("/Account/Login")) {
            body = restService.sendGetRequest(url, requestVerificationTokenCookie);
        } else {
            body = restService.sendGetRequest(url + "/Account/Login", requestVerificationTokenCookie);
        }
        return body;
    }

    private String getLoginPage(String url, String cookie) throws ClientProtocolException, IOException {
        String body = restService.sendGetRequest(url + "/Account/Login", cookie);
        return body;
    }

    public void postLoginPage(String url, String username, String password, String verificationToken) throws ClientProtocolException, IOException {
        String body = "__RequestVerificationToken=" + verificationToken + "&UserName=" + username + "&Password=" + password + "&BrowserTimeZoneOlsonName=Europe%2FLondon";
        String response;
        if (url.contains("/Account/Login")) {
            response = restService.sendPostRequestWithRedirection(url, body, requestVerificationTokenCookie);
        } else {
            response = restService.sendPostRequestWithRedirection(url + "/Account/Login", body, requestVerificationTokenCookie);
        }

        assertTrue(restService.getStatusCode() == 200);
        assertFalse("Incorrect username [" + username + "] or password [" + password + "]", response.contains("Incorrect username or password"));
        if  (restService.getMercuryCookie() == null) {
            logger.debug(" ***** WARNING: all tests making an API call will fail because no MERCURY COOKIE has been returned!!! *****");
        }
        mercuryCookie = restService.getMercuryCookie();
    }

    private String getVerificationTokenCookieViaApi(String url) throws Exception {
        HttpResponse httpResponse = restService.sendGetRequest(url);

        appUserRequestVerificationTokenCookie = "__RequestVerificationToken=" + StringUtils.substringBetween(httpResponse.getHeaders("Set-Cookie")[0].getValue().toString(), "__RequestVerificationToken=", ";");

        String response = getLoginPage(url, appUserRequestVerificationTokenCookie).replace("<!DOCTYPE html>", "");
        String verificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");
        return verificationToken;
    }

    private String getMercuryCookieViaApi(String verificationToken, String username, String password ) throws ClientProtocolException, IOException {
        String body = "__RequestVerificationToken=" + verificationToken + "&UserName=" + username + "&Password=" + password + "&BrowserTimeZoneOlsonName=Europe%2FLondon";
        restService.sendPostRequestWithRedirection(propertyHelper.getMercuryUrl() + "/Account/Login", body, appUserRequestVerificationTokenCookie);
        assertTrue(restService.getStatusCode() == 200);
        if  (restService.getMercuryCookie() == null) {
            logger.debug(" ***** WARNING: all tests making an API call will fail becaulse no MERCURY COOKIE has been returned!!! *****");
        }

        return restService.getMercuryCookie();
    }

    private CookieStore getImpersonationCookie(String impUserId) throws ClientProtocolException, IOException, URISyntaxException {
        String url = propertyHelper.getMercuryUrl() + String.format("/Resources/Api/ImpersonateUserApi/Impersonate/%s", impUserId);
        logger.debug("impersonateUser: " + impUserId);

        String mercuryCookieName = appUserMercuryCookie.replaceAll("=.*", "");

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Accept" , "text/xml");
        httpPost.addHeader("origin" , propertyHelper.getMercuryUrl());
        httpPost.addHeader("referer" , propertyHelper.getMercuryUrl() + "/Resources/ImpersonateUser");

        restService.sendPostRequest(httpPost, restService.getCookieStore());
        int statusCode = restService.getStatusCode();
        assertTrue("Bad status returned from request: " + statusCode, statusCode == 200);

        String impersonatingCookieValue = restService.getMercuryCookie().replaceAll("(.*)MERCURY_.*_COOKIE=", "");

        appUserMercuryCookie = mercuryCookieName + "=" + impersonatingCookieValue;
        if ( restService.getRequestVerificationToken() != null) {
            appUserRequestVerificationTokenCookie = restService.getRequestVerificationToken();
        }

        restService.addCookie("__RequestVerificationToken", appUserRequestVerificationTokenCookie.replaceAll("__RequestVerificationToken=", ""), URLHelper.getDomainName(propertyHelper.getMercuryUrl()) );
        return restService.getCookieStore();
    }


    /**
     * Set up the cookie store
     * @param url - Endpoint of the system
     * @param username - username to log onto the system as
     * @param password - password of the user
     * @param impUserId - the applicationuser.id of the user who is to be impersonated
     * @throws Exception
     */
    public void setupImpersonationCookiesAppUser(String url, String username, String password, String impersonateUserId) throws Exception {
        logger.debug("setupImpersonationCookiesAppUser:\nURL: " + url + "\nusername: " + username + "\npassword: " + password + "\nimpersonateUserId: " + impersonateUserId);
        String verificationToken = getVerificationTokenCookieViaApi(url);
        appUserMercuryCookie = getMercuryCookieViaApi(verificationToken, username, password);
        appUserCookieStore = getImpersonationCookie(impersonateUserId);
    }

    public String getDocumentAsUser(String url) throws ClientProtocolException, IOException {
        String response = restService.sendGetRequestNoRedirect(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("QuoteCreateEditRegister Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    public void createLinkedIncidentCriterion() throws Exception {
        String jsonPayload = FileHelper.getResourceFile("apiTemplates/linkedIncidentCriterion/", "newLinkenIncidentCriterion.json");

        int faultTypeId = dbHelper.getFaultTypeId("Non Operational");
        int incidentTypeId = dbHelperIncidents.getIncidentTypeId("Refrigeration Outage");
        int assetSubTypeId = dbHelper.getAssetSubTypeId("Total Refrigeration Outage");

        jsonPayload = jsonPayload.replace("137", String.valueOf(assetSubTypeId)).replace("1006", String.valueOf(incidentTypeId)).replace("85", String.valueOf(faultTypeId));

        String url = propertyHelper.getMercuryUrl() + "/api/LinkedIncidentCriterion/Update";
        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        int statusCode = restService.getStatusCode();
        logger.debug("linkedIncidentCriterion update response status code: " + statusCode);
        assertTrue("Failed to create Linked Incident Criterion", statusCode == 200);
    }

    public void createRuleJobQuestion(String question, int siteId) throws Exception {
        Map<String, Object> dbData = dbHelper.getQuestion(question);
        int questionId = (Integer) dbData.get("Id");
        int rulesEngineRuleTemplateId = (Integer) dbData.get("RulesEngineRuleTemplateId");
        String ruleName = "automationTestRule_" + RandomStringUtils.randomAlphabetic(3);

        String jsonPayload = FileHelper.getResourceFile("apiTemplates/rules/", "createRule.json")
                .replaceAll("RULE_NAME", ruleName)
                .replaceAll("TEMPLATE_ID", String.valueOf(rulesEngineRuleTemplateId))
                .replaceAll("SITE_ID", String.valueOf(siteId))
                .replaceAll("\\bQUESTION\\b", question)
                .replaceAll("QUESTION_ID", String.valueOf(questionId));

        String url = propertyHelper.getMercuryUrl() + "/RulesEngine/api/ConfigureRulesApi/Rule/0";
        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        int statusCode = restService.getStatusCode();
        logger.debug("ConfigureRulesApi response status code: " + statusCode);
        assertTrue("Failed to create Linked Incident Criterion", statusCode == 200);
        runtimeState.scenario.write("New Rule created: " + ruleName);
    }

    public void cancelIncidents(List<Integer> incidentIds) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/api/Incident/%d/CancelRequest";

        for (int id : incidentIds) {
            restService.sendPostJsonRequest(String.format(url, id), "{}", mercuryCookie);
            int statusCode = restService.getStatusCode();
            assertTrue("Failed to cancel Incident: " + String.format(url, id), statusCode == 200);
        }

        logger.debug("Cancelled " + incidentIds.size() + " incidents");
    }

    public void rejectFundingRequest(Integer fundingRequestId, Integer initialRequestApproverId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/api/ResourceAssignmentFunding/%d/reject";

        String payload = "{\"id\":%d,\"rejectedByResourceId\":%d,\"rejectionReasonId\":\"103\",\"notes\":\"Rejection Notes\"}";
        restService.sendPutJsonRequest(String.format(url, fundingRequestId), String.format(payload, fundingRequestId, initialRequestApproverId), mercuryCookie);

        int statusCode = restService.getStatusCode();
        assertTrue("Failed to reject function request: " + fundingRequestId, statusCode == 200);
    }

    public String getMonitorAreaCategoryCounts(int areaId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + String.format("/api/monitors/areas/%d/categorycounts", areaId);
        Response response = RestAssuredHelper.getJson(url);
        return response.getBody().asString();
    }

    public String getMonitorAreaCounts(int areaId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + String.format("/api/monitors/areas/%d/counts", areaId);
        Response response = RestAssuredHelper.getJson(url);
        return response.getBody().asString();
    }

}