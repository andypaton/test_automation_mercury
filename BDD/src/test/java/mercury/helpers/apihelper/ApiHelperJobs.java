package mercury.helpers.apihelper;

import static mercury.helpers.Constants.City2019;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.PA55W0RD;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mercury.api.models.job.Job;
import mercury.api.models.web.api.ResourceAction.AcceptJob;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.ResourceAssignment;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperTestAutomationUsers;

@Component
public class ApiHelperJobs extends ApiHelper{

    @Autowired private DbHelper dbHelper;
    @Autowired private TestData testData;
    @Autowired private ResourceAssignmentDao resourceAssignmentDao;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private DbHelperTestAutomationUsers dbHelperTestAutomationUsers;
    @Autowired private JobCreationHelper jobCreationHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final int REACTIVE_JOBTYPEID = 1;


    public void triageCallout(int jobId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/api/JobTechTriage/Callout";
        String jsonPayload = String.format("{\"jobId\":%d,\"timeSpent\":\"PT3H2M\",\"notes\":\"Callout from test API\",\"referToHelpdesk\":false}", jobId);
        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        int statusCode = restService.getStatusCode();
        assertTrue(statusCode == 200);
    }

    private Job createDraftJob(int siteId, int jobTypeId, String createdBy) throws Exception {

        Job job = new Job();
        job.setSiteId(siteId);
        job.setJobTypeId(jobTypeId);
        job.setIsDeferred(false);
        job.setJobSourceId(6); // Helpdesk default
        job.setCreatedBy(createdBy);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String jsonPayload = gson.toJson(job);

        String url = propertyHelper.getMercuryUrl() + "/api/jobLogging/CreateDraftJob";

        if (mercuryCookie == null) {
            setUpMercuryCookie();
        }

        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        assertTrue("expected status code 200", restService.getStatusCode() == 200);

        return gson.fromJson(restService.getResponseString(), Job.class);
    }

    public void setUpMercuryCookie() throws Exception {
        WebDriver tempDriver = new ChromeDriver();
        tempDriver.get(propertyHelper.getMercuryUrl());

        List<Map<String, Object>> testAutomationUsers = dbHelperTestAutomationUsers.getITUsers();

        for (Map<String, Object> dbData: testAutomationUsers) {
            String userName = dbData.get("UserName").toString();
            String firstnameLastname = dbData.get("Name").toString().trim();
            String password = userName.equalsIgnoreCase(runtimeState.domainUser) ? City2019 : PA55W0RD;
            try {
                logger.debug("apiUser: " + firstnameLastname + " (username: " + userName + ", password: " + password + ")");
                requestVerificationTokenCookie = "__RequestVerificationToken=" + StringUtils.substringBetween(tempDriver.manage().getCookies().toString(), "__RequestVerificationToken=", ";");
                String url = propertyHelper.getMercuryUrl();
                String response = getLoginPage(url).replace("<!DOCTYPE html>", "");
                String verificationRequestToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");
                postLoginPage(url, userName, password, verificationRequestToken);
                testData.put("apiUser", firstnameLastname);
                break;

            } catch (AssertionError e) {
                logger.debug("Failed to login as: " + firstnameLastname + ", username: " + userName + ", password: " + password);
                // ignore login failure and move on to next attempt
            }
        }
        tempDriver.close();
    }


    public void putContact(int callerId, int jobId) throws ClientProtocolException, IOException {
        String jsonPayload = String.format("{\"active\":true,\"callerId\":%d,\"jobId\":%d}", callerId, jobId);
        String url = propertyHelper.getMercuryUrl() + "/api/jobcontacts/putcontact";
        restService.sendPutJsonRequest(url, jsonPayload, mercuryCookie);
        assertTrue(restService.getStatusCode() == 200);
    }

    public void lockJob(int jobId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + String.format("/api/lock/job/%d", jobId);
        restService.sendPostRequest(url, mercuryCookie);
        assertTrue(restService.getStatusCode() == 200);
    }

    private Job runJob(String url, String jsonPayload) throws ClientProtocolException, IOException, InterruptedException {
        restService.sendPatchJsonRequest(url, jsonPayload, mercuryCookie);

        Gson gson = new Gson();
        Job job = gson.fromJson(restService.getResponseString(), Job.class);

        Integer ref = job.getJobReference() != null ? job.getJobReference() : null;
        dbHelper.insertAudit(url, ref, jsonPayload, String.valueOf(restService.getStatusCode()));

        assertTrue("Unexpected response from end point, expected 200 but got " + restService.getStatusCode() + "\r\n" + restService.getResponseString(), restService.getStatusCode() == 200);

        return gson.fromJson(restService.getResponseString(), Job.class);
    }

    public Job rerunJob(String jsonPayload) throws Exception {
        int siteId = Integer.valueOf(StringUtils.substringBetween(jsonPayload, "\"siteId\":", "}"));
        int jobTypeId = testData.getInt("jobTypeId") == null ? REACTIVE_JOBTYPEID : testData.getInt("jobTypeId");

        ApplicationUser applicationUser = jobCreationHelper.getCurrentUserForJobCreation();

        Job job = createDraftJob(siteId, jobTypeId, applicationUser.getCreatedBy());

        String jobId = jsonPayload.replaceAll("\"id\":.?+,", "\"id\":" + String.valueOf(job.getId()));
        String url = propertyHelper.getMercuryUrl() + String.format("/odata/Jobs(%s)", jobId);

        return runJob(url, jsonPayload);
    }

    public Job createNewJob(int jobTypeId, int siteId, int faultTypeId, int faultPriorityId, String description, int callerId, int assetClassificationId, int locationId, String createdBy) throws Exception {
        Job jobTemplate = newJobTemplate(jobTypeId, siteId, faultTypeId, faultPriorityId, description, callerId, assetClassificationId, locationId, createdBy);
        return createNewJob(jobTemplate);
    }

    public Job createNewJobForResource(int jobTypeId, int siteId, int faultTypeId, int faultPriorityId, String description, int callerId, int assetClassificationId, int locationId, Integer specificResourceRequestId, Integer assetId, String createdBy) throws Exception {
        Job jobTemplate = newJobTemplate(jobTypeId, siteId, faultTypeId, faultPriorityId, description, callerId, assetClassificationId, locationId, createdBy);
        jobTemplate.setSpecificResourceRequestId(specificResourceRequestId);
        if(assetId != null) {
            jobTemplate.setAssetId(assetId);
        }
        return createNewJob(jobTemplate);
    }

    public Job createNewQuoteJob(int siteId, int faultTypeId, int faultPriorityId, String description, int callerId, int assetClassificationId, int locationId, int specificResourceRequestId, int fundingRouteId, int quotePriorityId, String createdBy) throws Exception {
        Job jobTemplate = newJobTemplate(2, siteId, faultTypeId, faultPriorityId, description, callerId, assetClassificationId, locationId, createdBy);
        jobTemplate.setCreateQuote(true);
        jobTemplate.setSpecificResourceRequestId(specificResourceRequestId);
        jobTemplate.setFundingRouteId(fundingRouteId);
        jobTemplate.setQuotePriorityId(quotePriorityId);
        jobTemplate.setIsDeferred(false);
        return createNewJob(jobTemplate);
    }

    public Job createNewJob(Job jobTemplate) throws Exception {

        String url = propertyHelper.getMercuryUrl() + "/api/jobLogging/UpdateJob";

        Job job = createDraftJob(jobTemplate.getSiteId(), jobTemplate.getJobTypeId(), jobTemplate.getCreatedBy());

        jobTemplate.setId(job.getId());
        jobTemplate.setTimestamp(job.getTimestamp());
        jobTemplate.setJobStatusId(1);

        String description = jobTemplate.getDescription();
        String scenarioName = ". Scenario: " + runtimeState.scenario.getName().replaceAll("\\[.*\\]", "").trim();
        description = description.toUpperCase().contains("TEST AUTOMATION") ? description : "Test Automation. " + description ;     // add 'Test Automation', if not already
        description = description.toUpperCase().contains("SCENARIO") ? description : description + scenarioName;    // add the scenario name, if not already
        jobTemplate.setDescription(description);

        // set optional fields
        if (testData.getBoolean("isDeferred")) {
            // deferred job
            jobTemplate.setIsDeferred(true);
            jobTemplate.setDeferralNote("Deferred for automation test");
            jobTemplate.setDeferralTypeId(5);
            Date plusTwoDays = DateHelper.getDatePlusOffsetInHours(new Date(), 48);
            jobTemplate.setDeferralDate(DateHelper.dateAsString(plusTwoDays, "yyyy-MM-dd'T'08:00:00+00:00"));

        } else if (jobTemplate.getIsDeferred() == null || !jobTemplate.getIsDeferred()){
            jobTemplate.setIsDeferred(false);
        }

        if (jobTemplate.getAssetId() == null ) {
            jobTemplate.setReasonNotLoggedAgainstAssetId(10);
        }

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String jsonPayload = gson.toJson(jobTemplate);

        logger.debug("http PATCH: " + url + "\nPayload: " + jsonPayload);
        restService.sendPatchJsonRequest(url, jsonPayload, mercuryCookie);

        assertTrue("Expected status code = 200, but got: " + restService.getStatusCode(), restService.getStatusCode() == 200);

        job = gson.fromJson(restService.getResponseString(), Job.class);
        int ref = job.getJobReference() != null ? job.getJobReference() : job.getId();

        return gson.fromJson(restService.getResponseString(), Job.class);
    }

    public void cancelJob(int jobId, String requestedBy) throws ClientProtocolException, IOException {
        String jsonPayload = "{\"requestedBy\":\"" + requestedBy + "\",\"reasonId\":4}";
        String url = propertyHelper.getMercuryUrl() + "/api/Job/" + jobId + "/Cancel";
        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        assertTrue(restService.getStatusCode() == 200);
    }

    public void acceptJob(AcceptJob acceptjob) throws ClientProtocolException, IOException, InterruptedException {
        String url = propertyHelper.getMercuryUrl() + "/web/api/ResourceAction/acceptjob";
        try {
            restService.sendPostJsonRequest(url, acceptjob.toJsonString(), mercuryCookie);
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (Exception e) {
            logger.debug("Failed to accept job ..... attempting one more time!");
            Thread.sleep(1000);
            restService.sendPostJsonRequest(url, acceptjob.toJsonString(), mercuryCookie);
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        }
    }

    public void acceptOrDeclineJob(AcceptJob acceptjob) throws ClientProtocolException, JsonProcessingException, IOException {
        int resourceAssignmentId = acceptjob.getResourceAssignmentId();
        int jobId = acceptjob.getJobId();
        int resourceId = acceptjob.getResourceId();
        String eta = acceptjob.getEta().replaceAll(":", "%3A");
        String ref = acceptjob.getContractorReference().replaceAll(" ", "+");

        String url = propertyHelper.getMercuryUrl() + "/Portal/Job/AcceptOrDecline/" + resourceAssignmentId;

        String payload = "%s&JobId=%d&ResourceId=%d&SelfAssigned=False&ResourceAssignmentId=%d&Accepted=true&etaDate=%s&FormEtaWindowId=3&ContractorReference=%s";
        payload = String.format(payload, requestVerificationTokenCookie, jobId, resourceId, resourceAssignmentId, eta, ref);

        restService.sendPostJsonRequest(url, payload, mercuryCookie);
        assertTrue("Expected status code 302, but was " + restService.getStatusCode(), restService.getStatusCode() == 302);
    }

    private void startJob(int jobReference, String jobType, Instant start) throws Exception {
        String jobRef = String.valueOf(jobReference);
        String url = propertyHelper.getMercuryUrl() + "/Portal/SupplierTechnician/StartWork";

        String postBody = "jobRef=".concat(jobRef);
        postBody = postBody.concat(jobType.contains("PPM") ? "&jobType=PPM" : "&jobType=Reactive");
        postBody = postBody.concat("&asbestosRegisterChecked=true");
        String response = null;

        response = restService.sendPostRequest(url, postBody, appUserCookieStore);
        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode(), restService.getStatusCode() == 200);
            logger.debug("Start Work success");

        } catch (AssertionError e) {
            // Start Job sometimes fails if (possibly) the Works Order has not been issued after Contractor accepted the job. Try again!

            logger.debug("Start Work failed");
            if (Duration.between(start, Instant.now()).toMillis() < (MAX_TIMEOUT * 1000)) {
                Thread.sleep(2000);
                startJob(jobReference, jobType, start);

            } else {
                logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
                logger.debug("StartWork expected status code 200, but was " + restService.getStatusCode());
                throw e;
            }
        }
    }

    public void startJob(int jobReference, String jobType) throws Exception {
        startJob(jobReference, jobType, Instant.now());
    }

    public String getUpdateJobPage(Integer jobId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/Portal/Job/Update/%d";
        url = String.format(url, jobId);
        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("Get job update page: Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    public void updateJob(Integer resourceAssignmentId, String payload) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/Portal/Job/Update/%d";
        url = String.format(url, resourceAssignmentId);
        String postBody = payload;
        String response = null;
        response = restService.sendPostRequest(url, postBody, appUserCookieStore);
        try {
            assertTrue("Expected status code 302, but was " + restService.getStatusCode(), restService.getStatusCode() == 302);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("updateJob Response code " + restService.getStatusCode());
            throw e;
        }
    }

    //    public void updateFgasJob(Integer jobReference, Integer jobId, Integer resourceAssignmentId, String payload)  throws Exception {
    public void updateFgasJob(Integer resourceAssignmentId, String payload)  throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/Portal/Job/UpdateFGas2019/%d";
        url = String.format(url, resourceAssignmentId);
        String postBody = payload;
        String response = null;
        response = restService.sendPostRequest(url, postBody, appUserCookieStore);
        try {
            assertTrue("Expected status code 302, but was " + restService.getStatusCode(), restService.getStatusCode() == 302);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("updateJob Response code " + restService.getStatusCode());
            throw e;
        }
    }

    public Job newJobTemplate(int jobTypeId, int siteId, int faultTypeId, int faultPriorityId, String description, int callerId, int assetClassificationId, int locationId, String createdBy) {
        Job job = new Job();
        job.setAssetClassificationId(assetClassificationId);
        job.setCallerId(callerId);
        job.setDescription(description);
        job.setFaultPriorityId(faultPriorityId);
        job.setFaultTypeId(faultTypeId);
        job.setJobTypeId(jobTypeId);
        job.setLocationId(locationId);
        job.setSiteId(siteId);
        job.setCreatedBy(createdBy);
        return job;
    }

    public void declineJob(int jobId, int resourceId, int resourceAssignmentId) throws ClientProtocolException, IOException {
        String payload = "jobId=%d&resourceId=%d&resourceAssignmentId=%d&declineReasonId=124&reasonNotes=Declined+by+Test+Automation";
        payload = String.format(payload, jobId, resourceId, resourceAssignmentId);
        String url = propertyHelper.getMercuryUrl() + "/web/api/ResourceAction/declinejob";
        restService.sendPostRequest(url, payload, mercuryCookie);
        assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
    }

    /**
     * Decline jobReference for ALL assigned resources
     * @param jobReference
     * @throws ClientProtocolException
     * @throws IOException
     */
    public void declineJob(int jobReference) throws ClientProtocolException, IOException {
        List<ResourceAssignment> resourceAssignments = resourceAssignmentDao.getByJobReference(jobReference);
        int jobId = resourceAssignments.get(0).getJobId();
        int resourceId = resourceAssignments.get(0).getResourceId();
        int resourceAssignmentId = resourceAssignments.get(0).getId();

        declineJob(jobId, resourceId, resourceAssignmentId);
    }

    public void updateResourceProfile(int additionalResourceRequirementId, int profileId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/api/additionalresource/%d/updateresourceprofile";
        url = String.format(url, additionalResourceRequirementId);
        restService.sendPutJsonRequest(url, String.valueOf(profileId), mercuryCookie);
        assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
    }

    public void reopenJob(int jobId) throws ClientProtocolException, IOException {
        String payload = "{\"jobId\":%d,\"reasonId\":42,\"notes\":\"Reopen by Test Automation\"}";
        payload = String.format(payload, jobId);
        String url = propertyHelper.getMercuryUrl() + "/api/JobReopen";
        restService.sendPostJsonRequest(url, payload, mercuryCookie);
        assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
    }

    public void parkJob(Integer jobId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + String.format("/api/Job/%d/Park", jobId);
        String unparkDate = DateHelper.getNowDatePlusOffset(24, "yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        String payload = String.format("{\"unParkDate\":\"%s\",\"reasonId\":48,\"notes\":\"Unparked by Test Automation\",\"isReadOnly\":false}", unparkDate);
        restService.sendPostJsonRequest(url, payload, mercuryCookie);
        assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
    }

    public void unParkJob(Integer jobId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + String.format("/api/Job/%d/UnPark", jobId);
        restService.sendPostRequest(url, mercuryCookie);
        assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
    }
    public void removeResource(Integer resourceAssignmentId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/api/ResourceAssignment/removeresource";

        String payload = "resourceAssignmentId=%d&requestedBy=TestAutomation&reason=34&notes=Remove+requested+by+Test+Automation&isApprovalRequired=true&RequestedStatus=14";
        restService.sendPostRequest(url, String.format(payload, resourceAssignmentId), mercuryCookie);

        int statusCode = restService.getStatusCode();
        assertTrue("Failed to remove resource: " + resourceAssignmentId, statusCode == 200);
    }

    public void removeResourceAdvise(Integer resourceAssignmentId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/web/api/ResourceAction/removeresourceadvise";

        String payload = "resourceAssignmentId=%d&reason=&notes=Removal+advised+by+Test+Automation&isRemovedSelected=true";
        restService.sendPostRequest(url, String.format(payload, resourceAssignmentId), mercuryCookie);

        int statusCode = restService.getStatusCode();
        assertTrue("Failed to remove resource: " + resourceAssignmentId, statusCode == 200);
    }

    public void cancelAdditionalResource(Integer arrId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/api/additionalresource/%d/close";

        String payload = "{\"closeDescription\":\"Details for no ARR\",\"action\":\"\"}";
        restService.sendPutJsonRequest(String.format(url, arrId), payload, mercuryCookie);

        int statusCode = restService.getStatusCode();
        assertTrue("Failed to cancel ARR: " + arrId, statusCode == 200);
    }

    public void assignAdditionalResource(int jobId, int resourceId, int additionalResourceRequirementId) throws ClientProtocolException, IOException {
        String payload = "{\"JobId\":%d,\"resourceId\":%d,\"deferralDate\":null,\"deferralNote\":null,\"deferralTypeId\":null,\"resourceAssignmentRank\":1,\"slaEtaRequests\":[],\"resourceAssignmentCategoryId\":0,\"warrantyResourceAnswers\":null}";
        payload = String.format(payload, jobId, resourceId);
        String url = propertyHelper.getMercuryUrl() + String.format("/api/additionalresource/%d/assignresource", additionalResourceRequirementId);
        restService.sendPostJsonRequest(url, payload, mercuryCookie);
        assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
    }

    public void assignAdditionalResource(int jobReference, int resourceId) throws ClientProtocolException, IOException {
        List<ResourceAssignment> resourceAssignments = resourceAssignmentDao.getByJobReference(jobReference);
        int jobId = resourceAssignments.get(0).getJobId();
        int additionalResourceRequirementId = dbHelper.getMaxAdditionalResourceRequirementId(jobReference);

        assignAdditionalResource(jobId, resourceId, additionalResourceRequirementId);
    }

    public void createHelpdeskChase(int jobId, String description, String telephone, int chaseTypeId, Boolean raisedByHelpdesk, String caller) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/odata/JobChases";
        String payload = "{\"jobId\":%d,\"problemDescription\":\"%s\",\"problemFixDescription\":null,\"chaseTypeId\":\"%s\","
                + "\"resourceId\":null,\"jobChaseStatusId\":0,\"callerName\":\"%s\",\"callerTelephone\":\"%s\","
                + "\"callerExtension\":null,\"raisedByHelpdesk\":%s}";
        payload = String.format(payload, jobId, description, String.valueOf(chaseTypeId), caller, telephone, raisedByHelpdesk);
        restService.sendPostJsonRequest(url, payload, mercuryCookie);
        assertTrue("Expected status code 201, but was " + restService.getStatusCode() , restService.getStatusCode() == 201);
    }

    public void cancelChase(int chaseId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + String.format("/api/JobChase/%d/Update", chaseId);

        String payload = "{\"jobChaseId\":%d,\"updateText\":\"cancelled by test automation\",\"newStatusId\":2,\"cancellationReasonId\":90}";
        payload = String.format(payload, chaseId);

        restService.sendPostJsonRequest(url, payload, mercuryCookie);

        assertTrue("Expected status code 201, but was " + restService.getStatusCode() , restService.getStatusCode() == 201);
    }

    public void fixJob(int jobReference) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/Jobs/JobStatus/Edit";
        String payload = String.format("JobReference=%d&JobStatusId=9&submit=save", jobReference);
        String response = restService.sendPostRequest(url, payload, mercuryCookie);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("acceptJob Response code " + restService.getStatusCode());
            throw e;
        }
    }

    public void cancelJob(int jobReference) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/Jobs/JobStatus/Edit";
        String payload = String.format("JobReference=%d&JobStatusId=10&submit=save", jobReference);

        String response = restService.sendPostRequest(url, payload, mercuryCookie);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("acceptJob Response code " + restService.getStatusCode());
            throw e;
        }
    }
}