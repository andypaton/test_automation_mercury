package mercury.helpers.apihelper;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import mercury.api.models.mercuryportal.api.quoteQueriesSubmission.QuoteQueryResponse;
import mercury.api.models.mercuryportal.api.quoteQueriesSubmission.QuoteQuerySubmission;
import mercury.api.models.mercuryportal.api.quoteQueriesSubmission.SubmitResponseToSeniorManagerQuery;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalRetrieval.JobDetails;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission.QuoteDecision;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission.QuoteRejectionDecision;
import mercury.api.models.portal.quoteCreateEdit.DocumentUpload;
import mercury.api.models.portal.quoteCreateEdit.QuoteHeader;
import mercury.api.models.portal.quoteInvitation.DeclineITQ;
import mercury.api.models.portal.quoteJobEdit.QuoteApprove;
import mercury.api.models.portal.quoteJobEdit.QuoteBypass;
import mercury.api.models.portal.quoteJobEdit.QuoteReject;
import mercury.api.models.portal.quoteLine.QuoteLine;
import mercury.helpers.JsonHelper;

@Component
public class ApiHelperQuotes extends ApiHelper {

    private static final Logger logger = LogManager.getLogger();

    private static final String QUOTE_FILTER_URL = "?jobRef=%s";
    private static final String QUOTE_TYPE_FILTER_URL = "?jobRef=%s&quoteType=1";
    private static final String QUOTE_PROJECT_TYPE_FILTER_URL =  "?jobRef=%s&projectQuoteId=%s&quoteType=%s";
    private static final String QUOTE_UPLOAD_TYPE_FILTER_URL =  "?uploadType=%s&jobRef=%s&quoteType=%s";

    private static final String QUOTE_JOB_EDIT_URL = "/Portal/QuoteJobEdit";
    private static final String QUOTE_JOB_EDIT_EDIT_URL = QUOTE_JOB_EDIT_URL + "/Edit?faultId=%s&scenario=AwaitingQuoteRequestApprover&returnUrl=JobsAwaitingQuoteRequestApproval";
    private static final String QUOTE_JOB_EDIT_SAVE_URL = QUOTE_JOB_EDIT_URL + "/Save";
    private static final String ACCEPT_INVITATION_TO_QUOTE_URL = "/ResourcePortal/api/AcceptInvitationToQuote/";

    private static final String QUOTE_CREATE_EDIT_URL = "/Portal/QuoteCreateEdit";
    private static final String QUOTE_CREATE_EDIT_REGISTER_FILTER_URL = "/Portal/QuoteCreateEdit" + "/Register" + QUOTE_FILTER_URL;
    private static final String QUOTE_CREATE_EDIT_UPLOAD_URL = QUOTE_CREATE_EDIT_URL + "/UploadQuoteDocument";
    private static final String QUOTE_CREATE_EDIT_EDIT_QUOTE_HEADER_URL = QUOTE_CREATE_EDIT_URL + "/EditQuoteHeader";
    private static final String QUOTE_CREATE_EDIT_EDIT_QUOTE_HEADER_FILTER_URL = QUOTE_CREATE_EDIT_URL + "/EditQuoteHeader" + QUOTE_TYPE_FILTER_URL;
    private static final String QUOTE_CREATE_EDIT_UPLOAD_FILTER_URL =  QUOTE_CREATE_EDIT_UPLOAD_URL + QUOTE_UPLOAD_TYPE_FILTER_URL;

    private static final String QUOTE_CREATE_EDIT_QUERY_URL_GET = QUOTE_CREATE_EDIT_URL + "/QUERY/%s?jobRef=%S";
    private static final String QUOTE_CREATE_EDIT_QUERY_URL_POST = QUOTE_CREATE_EDIT_URL + "/QUERY/%s";

    private static final String QUOTE_LINE_URL = "/Portal/QuoteLine";
    private static final String QUOTE_LINE_CREATE_URL = QUOTE_LINE_URL + "/CreateQuoteLine";
    private static final String QUOTE_LINE_GET_URL = QUOTE_LINE_URL + "/GetQuoteLines";
    private static final String QUOTE_LINE_CREATE_LINE_URL = QUOTE_LINE_CREATE_URL + QUOTE_PROJECT_TYPE_FILTER_URL;
    private static final String QUOTE_LINE_GET_LINES_URL = QUOTE_LINE_GET_URL + QUOTE_PROJECT_TYPE_FILTER_URL;

    private static final String QUOTE_SUBMISSION_URL = "/Portal/QuoteSubmission/SubmitQuote" + QUOTE_FILTER_URL;

    private static final String DECLINE_INVITATION_TO_QUOTE_URL =  "/Portal/QuoteInvitation/DeclineInvitation/%s";

    private static final String SUBMIT_MANAGER_QUOTE_QUERY_URL = "/mercuryportal/api/quoteQueriesSubmission/submitManagerQuoteQuery";
    private static final String SUBMIT_SENIOR_MANAGER_QUOTE_QUERY_URL = "/mercuryportal/api/quoteQueriesSubmission/submitSeniorManagerQuoteQuery";
    private static final String SUBMIT_SENIOR_MANAGER_QUOTE_QUERY_RESPONSE_URL = "/mercuryportal/api/quoteQueriesSubmission/submitResponseToSeniorManagerQuery";

    private static final String QUOTES_AWAITING_APPROVAL_RETRIEVAL_URL = "/mercuryportal/api/quotesAwaitingApprovalRetrieval/jobDetails/%s";

    private static final String SUBMIT_QUOTE_DECISION_URL = "/mercuryportal/api/quotesAwaitingApprovalSubmission/submitQuoteDecision";

    private static final String CANCEL_QUOTE_REQUEST_URL = "/mercuryportal/api/QuoteRequestApproval/CancelQuoteRequest";

    /**
     * @param jobRef
     * @return Extracts the verificationRequestTokem embedded in the page and returns
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getQuotePage(Integer jobReference) throws ClientProtocolException, IOException {
        try {
            String url = propertyHelper.getMercuryUrl() + QUOTE_JOB_EDIT_EDIT_URL;
            url = String.format(url, jobReference);
            String response = restService.sendGetRequest(url, appUserCookieStore);
            String verificationRequestToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");
            return verificationRequestToken;
        }
        catch (AssertionError e) {
            return "Error";
        }
    }

    /**
     * Approve Quote as a RFM, assumes that the cookie store has a valid RFM cookie setup
     * @param jobRef - Job to be approved
     * @param verificationToken - verification token stripped from html
     * @param quoteApprove - quoteApproval payload - TBC
     * @return response which should be ...
     * @throws IOException
     * @throws JsonProcessingException
     * @throws ClientProtocolException
     * @throws Throwable
     */
    public String approveQuote(Integer jobReference, String verificationToken, QuoteApprove quoteApprove) throws ClientProtocolException, JsonProcessingException, IOException {
        try {
            String url = propertyHelper.getMercuryUrl() + QUOTE_JOB_EDIT_SAVE_URL;
            String formData =  JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), quoteApprove.toJsonString());
            formData = formData.replaceAll("%5B\\d%5D", "%5B%5D"); // For some reason QuoteResourceIds[0]: 109 has now changed to QuoteResourceIds[]: 109 in the payload
            formData = formData + "&__RequestVerificationToken=" + verificationToken;
            logger.debug("Quote approval message: " + formData);
            String response = restService.sendPostRequest(url, formData,  appUserCookieStore);
            return response;
        }
        catch (AssertionError e) {
            return "Error";
        }
    }

    public String rejectQuote(Integer jobReference, String verificationToken, QuoteReject quoteReject) throws ClientProtocolException, JsonProcessingException, IOException {
        try {
            String url = propertyHelper.getMercuryUrl() + CANCEL_QUOTE_REQUEST_URL;
            String formData = JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), quoteReject.toJsonString());
            formData = formData + "&__RequestVerificationToken=" + verificationToken;
            logger.debug("Quote cancel message: " + formData);
            String response = restService.sendPostRequest(url, formData, appUserCookieStore);
            return response;
        } catch (AssertionError e) {
            return "Error";
        }
    }

    public String byPassQuote(Integer jobReference, String verificationToken, QuoteBypass quoteBypass) throws ClientProtocolException, JsonProcessingException, IOException  {
        try {
            String url = propertyHelper.getMercuryUrl() + QUOTE_JOB_EDIT_SAVE_URL;
            String formData =  JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), quoteBypass.toJsonString());
            formData = formData + "&__RequestVerificationToken=" + verificationToken;
            logger.debug("Quote approval message: " + formData);
            String response = restService.sendPostRequest(url, formData,  appUserCookieStore);
            return response;
        }
        catch (AssertionError e) {
            return "Error";
        }
    }

    /**
     * Create a quote as a tect
     * @param jobReference
     * @return full response string - tbc correct type
     * @throws Throwable
     */
    public String createQuote(Integer jobReference) throws Throwable {
        String url = propertyHelper.getMercuryUrl() + QUOTE_CREATE_EDIT_REGISTER_FILTER_URL;
        url = String.format(url, jobReference);
        String response = restService.sendGetRequest(url, appUserCookieStore);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("Create Quote Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Accept an invitation to quote with the resource in the cookie store
     * @param jobReference
     * @return
     * @throws Throwable
     */
    public String acceptITQ(Integer jobReference) throws Throwable {
        String url = propertyHelper.getMercuryUrl() + ACCEPT_INVITATION_TO_QUOTE_URL + jobReference;
        String response = restService.sendPostRequest(url, appUserCookieStore);

        try {
            assertThat("Expected status code 200 or 302, but was " + restService.getStatusCode(), restService.getStatusCode(), anyOf(equalTo(200), equalTo(302)));
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("acceptITQ Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Simulates requesting the create quote header page.
     * @param jobReference
     * @return __RequestVerificationToken embedded in the web page
     * @throws Throwable
     */
    public String getQuoteEditQuoteHeader(Integer jobReference) throws Throwable {
        String url = propertyHelper.getMercuryUrl() + QUOTE_CREATE_EDIT_EDIT_QUOTE_HEADER_FILTER_URL;
        url = String.format(url, jobReference);
        String response = restService.sendGetRequest(url, appUserCookieStore);
        String requestVerificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("createQuoteHeader verificationRequestToken" + requestVerificationToken);
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("createQuoteEditQuoteHeader Response code " + restService.getStatusCode());
            throw e;
        }
        return requestVerificationToken;
    }

    /**
     * Sets the quote header, description and expected work time for the quote
     * @param quoteHeader
     * @return full response from the server
     * @throws ClientProtocolException
     * @throws JsonProcessingException
     * @throws IOException
     */
    public String createQuoteHeader(QuoteHeader quoteHeader) throws ClientProtocolException, JsonProcessingException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_CREATE_EDIT_EDIT_QUOTE_HEADER_URL;
        String formData =  JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), quoteHeader.toJsonString());
        logger.debug("Form data to create quote " + formData);
        String response = restService.sendPostRequestWithRedirection(url, formData,  appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("createQuoteHeader Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Approves a quote in quotes awaiting review - goes to Quote Approved status
     * @param quoteApprovalScenarioId
     * @param resourceId
     * @param portalResourceId
     * @param jobRef
     * @param fundingRouteId
     * @param quoteValue
     * @return full response from the server
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String approveQuoteAwaitingReview(int quoteApprovalScenarioId, int resourceId, int portalResourceId, int jobRef, int fundingRouteId, int quoteValue) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/mercuryportal/api/quotesAwaitingApprovalSubmission/submitQuoteDecision";
        String payload = "{\"quoteReviewScenarioDecisionDtos\":[{\"quoteApprovalScenarioId\":%d,\"resourceId\":%d,\"portalResourceId\":%d,"
                + "\"approved\":true,\"isHighRisk\":false}],\"quoteResourceIds\":[],\"jobRef\":%d,\"currentFundingRouteId\":%d,"
                + "\"overallQuoteValue\":%d,\"jobIsApproved\":true,\"requiresSeniorManagerApproval\":false,\"openQuoteRequest\":false,"
                + "\"seniorManagerSubmitsApprovalDecision\":false}";

        payload = String.format(payload, quoteApprovalScenarioId, resourceId, portalResourceId, jobRef, fundingRouteId, quoteValue);
        logger.debug("Approving quote which is awaiting review with payload: " + payload);
        String response = restService.sendPostRequestWithRedirection(url, payload,  appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("createQuoteHeader Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Simulates getting the edit quote page
     * @param jobReference
     * @return full response from the server
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getCreateEditRegister(Integer jobReference) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_CREATE_EDIT_REGISTER_FILTER_URL;
        url = String.format(url, jobReference);
        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("QuoteCreateEditRegister Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Get all current line items for the given quote
     * @param jobReference
     * @param projectQuoteId
     * @param quoteType
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getQuoteLines(Integer jobReference, Integer projectQuoteId, Integer quoteType) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_LINE_GET_LINES_URL;
        url = String.format(url, jobReference, projectQuoteId, quoteType);
        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("GetQuoteLines Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Simulates getting the add quote line modal, require the __RequestVerificationToken from this page.  Probably should return that instead
     * @param jobReference
     * @param projectQuoteId
     * @param quoteType
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getQuoteLine(Integer jobReference, String projectQuoteId, String quoteType) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_LINE_CREATE_LINE_URL;
        url = String.format(url, jobReference, projectQuoteId, quoteType);
        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("createQuoteLine Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }


    /**
     * Add the item defined by quoteLine to the quote.
     * @param quoteLine
     * @return
     * @throws ClientProtocolException
     * @throws JsonProcessingException
     * @throws IOException
     */
    public String createQuoteLine(QuoteLine quoteLine) throws ClientProtocolException, JsonProcessingException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_LINE_CREATE_URL;
        String formData =  JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), quoteLine.toJsonString());
        logger.debug("Form data to create quote " + formData);
        String response = restService.sendPostRequestWithRedirection(url, formData,  appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("createQuoteLine Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Submit the quote for managers review
     * @param jobReference
     * @param requestVerificationToken
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String SubmitQuote(Integer jobReference, String requestVerificationToken) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_SUBMISSION_URL;
        url = String.format(url, jobReference);
        String formData =  "__RequestVerificationToken=" + requestVerificationToken;
        String response = restService.sendPostRequestWithRedirection(url, formData, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("SubmitQuote Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Simulates getting the upload quote document page.  This is required so a number of form values can be extracted to be used in the actual upload of the form
     * @param jobReference
     * @param uploadType
     * @param quoteType
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getUploadQuoteDocument(Integer jobReference, String uploadType, String quoteType) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_CREATE_EDIT_UPLOAD_FILTER_URL;
        url = String.format(url, uploadType, jobReference, quoteType);
        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("getUploadQuoteDocument Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Upload the a document and attach it to the quote. Only really required for non city resources.
     * @param jobReference
     * @param requestVerificationToken
     * @param documentUpload
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String UploadQuoteDocument(Integer jobReference, String requestVerificationToken, DocumentUpload documentUpload) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_CREATE_EDIT_UPLOAD_URL;// + jobReference;
        String filename = System.getProperty("user.dir")  + "\\src\\test\\resources\\portalfiles\\smallquote.pdf";
        Map<String, Object> formDataMap = JsonHelper.toMap(documentUpload.toJsonString());

        String response = restService.sendPostFileRequestWithRedirection(url, filename, formDataMap, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("UploadQuoteDocument Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Declines an Invitation to Quote for a given job with the decline reason
     * @param jobReference
     * @param declineITQ
     * @return
     * @throws ClientProtocolException
     * @throws JsonProcessingException
     * @throws IOException
     */
    public String DeclineITQ(Integer jobReference, DeclineITQ declineITQ) throws ClientProtocolException, JsonProcessingException, IOException {
        try {
            String url = propertyHelper.getMercuryUrl() + DECLINE_INVITATION_TO_QUOTE_URL;
            url = String.format(url, jobReference);
            String formData =  JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), declineITQ.toJsonString());
            logger.debug("Quote decline message: " + formData);
            String response = restService.sendPostRequest(url, formData,  appUserCookieStore);
            return response;
        }
        catch (AssertionError e) {
            return "Error";
        }
    }

    /**
     * Raises a Query against a Quote. This is usually carried out by the RFM or
     * Operations Manager reviewing the quote
     *
     * @param jobReference
     * @param quoteQuerySubmission
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String QueryQuote(Integer jobReference, QuoteQuerySubmission quoteQuerySubmission) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + SUBMIT_MANAGER_QUOTE_QUERY_URL;
        String formData =  quoteQuerySubmission.toJsonString();
        logger.debug("Quote query message: " + formData);
        String response = restService.sendPostRequest(url, formData,  appUserCookieStore);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("QueryQuote Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Raises a Query against a Quote. This is usually carried out by the RFM or
     * Operations Manager reviewing the quote
     *
     * @param jobReference
     * @param quoteQuerySubmission
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String QueryQuoteSeniorManager(Integer jobReference, QuoteQuerySubmission quoteQuerySubmission) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + SUBMIT_SENIOR_MANAGER_QUOTE_QUERY_URL;
        String formData =  quoteQuerySubmission.toJsonString();
        logger.debug("Quote query message: " + formData);
        String response = restService.sendPostRequest(url, formData,  appUserCookieStore);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("QueryQuote Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Gets the Quote Job details as a RFM.  This does not work if you do not have a RFM cookie
     * @param jobReference
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public JobDetails getQuoteJobDetails(Integer jobReference) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTES_AWAITING_APPROVAL_RETRIEVAL_URL;
        url = String.format(url, jobReference);
        String response = restService.sendGetJSONRequest(url, appUserCookieStore);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("QueryQuote Response code " + restService.getStatusCode());
            throw e;
        }
        Gson gson = new Gson();
        return gson.fromJson(restService.getResponseString(), JobDetails.class);
    }

    /**
     * Simulates getting the edit quote query page as a tech
     * @param jobReference
     * @param quoteApprovalScenarioId
     * @return full response from the server
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getCreateEditQuery(Integer jobReference, Integer quoteApprovalScenarioId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_CREATE_EDIT_QUERY_URL_GET;
        url = String.format(url, quoteApprovalScenarioId, jobReference);
        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("getCreateEditQuery Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Creates a Tech query response to a RFM Query
     * @param quoteQueryResponse
     * @return
     * @throws ClientProtocolException
     * @throws JsonProcessingException
     * @throws IOException
     */
    public String CreateEditQueryResponse(QuoteQueryResponse quoteQueryResponse) throws ClientProtocolException, JsonProcessingException, IOException {
        String url = propertyHelper.getMercuryUrl() + QUOTE_CREATE_EDIT_QUERY_URL_POST;
        url = String.format(url, quoteQueryResponse.getQuoteApprovalScenarioId());
        String formData =  JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), quoteQueryResponse.toJsonString());
        String response = restService.sendPostRequestWithRedirection(url, formData,  appUserCookieStore);

        try {
            assertTrue("Expected status code 302, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("createQuoteLine Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Creates a Tech query response to a RFM Query
     * @param quoteQueryResponse
     * @return
     * @throws ClientProtocolException
     * @throws JsonProcessingException
     * @throws IOException
     */
    public String CreateEditQueryResponseSeniorManager(SubmitResponseToSeniorManagerQuery submitResponseToSeniorManagerQuery) throws ClientProtocolException, JsonProcessingException, IOException {
        String url = propertyHelper.getMercuryUrl() + SUBMIT_SENIOR_MANAGER_QUOTE_QUERY_RESPONSE_URL;

        String formData =  submitResponseToSeniorManagerQuery.toJsonString();
        String response = restService.sendPostRequestWithRedirection(url, formData,  appUserCookieStore);

        try {
            assertTrue("Expected status code 302, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("createQuoteLine Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }


    /**
     * Approve a Quote as a RFM
     *
     * @param jobReference
     * @param quoteDecision
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String SubmitQuoteDecision(Integer jobReference, QuoteDecision quoteDecision) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + SUBMIT_QUOTE_DECISION_URL;
        String formData =  quoteDecision.toJsonString();
        formData = formData.replaceAll("\"jobRef\"", "\"quoteResourceIds\": [],\"jobRef\"");
        logger.debug("Quote decision message: " + formData);
        String response = restService.sendPostRequest(url, formData,  appUserCookieStore);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("QueryQuote Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    /**
     * Reject a Quote as a Senior Manager
     *
     * @param jobReference
     * @param quoteDecision
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String SubmitQuoteRejectionDecision(Integer jobReference, QuoteRejectionDecision quoteRejectionDecision) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + SUBMIT_QUOTE_DECISION_URL;
        String formData = quoteRejectionDecision.toJsonString();
        String[] splitFormData = formData.split(",\"quoteReviewScenarioRejectionDecisionDtos\"");
        formData = "{\"quoteReviewScenarioDecisionDtos\"" + splitFormData[1].replaceAll("]}", "],\"quoteResourceIds\": []") + (splitFormData[0].replaceAll("\\{", ",") + "}");
        logger.debug("Quote rejection decision message2: " + formData);
        String response = restService.sendPostRequest(url, formData, appUserCookieStore);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode(), restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length())));
            logger.debug("QueryQuote Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }
}
