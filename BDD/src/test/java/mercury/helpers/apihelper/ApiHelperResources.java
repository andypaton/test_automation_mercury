package mercury.helpers.apihelper;

import static mercury.helpers.Constants.City2019;
import static mercury.helpers.Constants.PA55W0RD;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import mercury.api.models.resource.Absence;
import mercury.api.models.resource.Classification;
import mercury.api.models.resource.ContractorSite;
import mercury.api.models.resource.EmailAddress;
import mercury.api.models.resource.PhoneNumber;
import mercury.api.models.resource.Resource;
import mercury.database.dao.SiteViewDao;
import mercury.database.models.SiteView;
import mercury.databuilders.DataGenerator;
import mercury.helpers.FileHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperTestAutomationUsers;

@Component
public class ApiHelperResources extends ApiHelper{

    private static final Logger logger = LogManager.getLogger();
    @Autowired private DbHelperTestAutomationUsers dbHelperTestAutomationUsers;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private SiteViewDao siteViewDao;

    public Resource getResource(int resourceId) throws ClientProtocolException, IOException, JSONException, TransformerException, ParseException {
        String url = propertyHelper.getMercuryUrl() + "/ConfigureResources/api/GetResource/" + resourceId;
        restService.sendGetRequest(url, mercuryCookie);

        int statusCode = restService.getStatusCode();
        logger.debug("getResource response status code: " + statusCode);
        assertTrue("Failed to GET resourceId " + resourceId, statusCode == 200);

        String jsonString = restService.getResponseString();
        Gson gson = new Gson();

        try {
            return gson.fromJson(jsonString, Resource.class);
        } catch (JsonSyntaxException jse) {
            logger.debug("Syntax error with returned resource (update api.model): " + jsonString);
            throw jse;
        }
    }

    public void createOrUpdateResource(Resource resource) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/ConfigureResources/api/CreateOrUpdateResource";
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(resource);
        logger.debug("createOrUpdateResource: " + url + "\n" + jsonPayload);
        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        int statusCode = restService.getStatusCode();
        logger.debug("createOrUpdateResource response status code: " + statusCode);
        assertTrue("Failed to update resource", statusCode == 200);
    }

    public void createOrUpdateContractorResource(String resourceName) throws Exception {
        String resourceTemplate = getResourceTemplateAsString("contractorResource.json");
        resourceTemplate = resourceTemplate.replace("automationContractor", resourceName);
        SiteView siteView = siteViewDao.getSiteByState("Occupied");
        resourceTemplate = resourceTemplate.replace("493", String.valueOf(siteView.getId()));
        resourceTemplate = resourceTemplate.replace("TestAutomationSite", siteView.getName());
        createOrUpdateResource(resourceTemplate);
    }

    public void createOrUpdateResource(String jsonPayload) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/ConfigureResources/api/CreateOrUpdateResource";
        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        int statusCode = restService.getStatusCode();
        logger.debug("createOrUpdateResource response status code: " + statusCode);
        assertTrue("Failed to update resource", statusCode == 200);
    }

    /**
     * Add resource to ON CALL rota for siteId
     * @param resourceId
     * @param resourceProfileId
     * @param siteId
     * @throws Exception
     */
    public void createOnCallSchedule(int resourceId, int resourceProfileId, int siteId) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/Portal/api/Scheduler/CreateSchedule";
        //        String jsonPayload = "{\"resourceId\":%d,\"resourceProfileId\":\"%d\",\"startDate\":\"2018-05-19T06:00:00.000Z\",\"endDate\":\"2038-05-19T06:00:00.000Z\",\"siteIds\":[%d]}";
        //        String jsonPayload = "{\"resourceId\":%d,\"resourceProfileId\":\"%d\",\"startDate\":\"2018-05-19T06:00:00.000Z\",\"endDate\":null,\"siteIds\":[%d]}";

        String jsonPayload = "{\"resourceId\":%d,\"resourceProfileId\":\"%d\",\"startDate\":\"2018-05-19T06:00:00.000\",\"endDate\":\"2038-05-19T06:00:00.000\",\"siteIds\":[%d]}";
        jsonPayload = String.format(jsonPayload, resourceId, resourceProfileId, siteId);

        // Set up the impersonation for the resource ()
        String expectedApprover = dbHelperSites.getRfmForSite(siteId).get("Name").toString();
        String resourceUserId = dbHelper.getApplicationUserId(expectedApprover);

        Map<String, Object> dbData = dbHelperTestAutomationUsers.getITUser();
        String userName = dbData.get("UserName").toString();
        String password = userName.equalsIgnoreCase(runtimeState.domainUser) ? City2019 : PA55W0RD;
        setupImpersonationCookiesAppUser(propertyHelper.getMercuryUrl(), userName, password, resourceUserId);

        restService.sendPostJsonRequestWithRedirection(url, jsonPayload, appUserCookieStore);
        int statusCode = restService.getStatusCode();
        logger.debug("createOrUpdateResource response status code: " + statusCode);
        assertTrue("Failed to update resource", statusCode == 200);
    }

    /**
     * update a resource and restore to pre-test state in scenario teardown
     * @param resource
     * @throws Exception
     */
    public void createOrUpdateResourceAndRollbackAtEndOfScenario(Resource resource) throws Exception {
        runtimeState.originalResources.add(getResource(resource.getId()));
        createOrUpdateResource(resource);
    }

    public void removeResourceAdvise(int resourceAssignmentId, String requesteBy) throws ClientProtocolException, IOException {
        String payload = "resourceAssignmentId=" + resourceAssignmentId + "&reason=&notes=&isRemovedSelected=true";
        String url = propertyHelper.getMercuryUrl() + "/web/api/ResourceAction/removeresourceadvise";
        restService.sendPostRequest(url, payload, mercuryCookie);
    }

    public void addContractorResourceToSite(int resourceId, int siteId, String siteName) throws Exception {
        Resource resource = getResource(resourceId);

        List<ContractorSite> sites = resource.getContractorSites();
        List<Classification> classifications = sites.get(0).getClassifications();

        ContractorSite newSite = new ContractorSite();
        newSite.setSiteId(siteId);
        newSite.setClassifications(classifications); // copy classifications from first Site
        newSite.setSiteName(siteName);

        sites.add(newSite);
        logger.debug("addContractorResourceToSite: adding site " + siteName + " to resource " + resource.getName());
        createOrUpdateResource(resource);
    }

    public void addCityTechResourceToSite(int resourceId, int siteId, String siteName) throws Exception {
        Resource resource = getResource(resourceId);

        List<Integer> sites = resource.getPermanentSiteIds();
        sites.add(siteId);

        logger.debug("addCityTechResourceToSite: adding site " + siteName + " to resource " + resource.getName());
        createOrUpdateResource(resource);
    }

    public void createContractorResource(String siteId) throws Exception {
        String resourceTemplate = getResourceTemplateAsString("contractorResource.json");
        resourceTemplate = resourceTemplate.replace("493", siteId);
        createOrUpdateResource(resourceTemplate);
    }

    public void createPermieResource(String name, int resourceProfileId, int siteId, String payrollCode) throws Exception {
        String resourceTemplate = getResourceTemplateAsString("cityTechResource.json");
        resourceTemplate = resourceTemplate.replace("\"homeStoreId\":493", "\"homeStoreId\":" + siteId);
        resourceTemplate = resourceTemplate.replace("\"resourceProfileId\":134", "\"resourceProfileId\":" + resourceProfileId);
        resourceTemplate = resourceTemplate.replace("\"permanentSiteIds\":[]", "\"permanentSiteIds\":[" + siteId + "]");
        resourceTemplate= resourceTemplate.replace("\"payrollTCode\":\"1\"", "\"payrollTCode\":\"" + payrollCode + "\"");
        resourceTemplate = resourceTemplate.replace("\"username\":\"testMst\"", "\"username\":\"" + name + "\"");
        resourceTemplate = resourceTemplate.replace("\"name\":\"testMst\"", "\"name\":\"" + name + "\"");
        createOrUpdateResource(resourceTemplate);
    }

    public void createOnCallResource(String name, int resourceProfileId, int siteId, String payrollCode) throws Exception {
        String resourceTemplate = getResourceTemplateAsString("cityTechResource.json");
        resourceTemplate = resourceTemplate.replace("\"homeStoreId\":493", "\"homeStoreId\":" + siteId);
        resourceTemplate = resourceTemplate.replace("\"resourceProfileId\":134", "\"resourceProfileId\":" + resourceProfileId);
        resourceTemplate= resourceTemplate.replace("\"payrollTCode\":\"1\"", "\"payrollTCode\":\"" + payrollCode + "\"");
        resourceTemplate = resourceTemplate.replace("\"username\":\"testMst\"", "\"username\":\"" + name + "\"");
        resourceTemplate = resourceTemplate.replace("\"name\":\"testMst\"", "\"name\":\"" + name + "\"");
        createOrUpdateResource(resourceTemplate);
    }

    private String getResourceTemplateAsString(String filename) throws Exception {
        return FileHelper.getResourceFile("apiTemplates/resources/", filename);
    }

    private Absence createAbsence(int resourceId, Integer coveringResource) {
        String startAt = ZonedDateTime.now( ZoneOffset.UTC ).plusDays(-1).toString();
        String endAt = ZonedDateTime.now( ZoneOffset.UTC ).plusDays(5).toString();
        Absence newAbsence = new Absence();
        newAbsence.setResourceAvailabilityTypeId(7);
        newAbsence.setCoveringResourceId(coveringResource);
        newAbsence.setResourceId(resourceId);
        newAbsence.setStartAt(startAt);
        newAbsence.setEndAt(endAt);
        newAbsence.setActive(true);
        newAbsence.setNotes("Absence added by test automation. Scenario: " + runtimeState.scenario.getName());
        newAbsence.setAddedViaHelpdesk(true);
        return newAbsence;
    }

    public Absence createAbsenceForResource(int resourceId, Integer coveringResource) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/odata/ResourceAvailability";
        Absence newAbsence = createAbsence(resourceId, coveringResource);
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(newAbsence);
        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        int statusCode = restService.getStatusCode();
        logger.debug("createOrUpdateResource response status code: " + statusCode);
        assertTrue("Failed to update resource", statusCode == 200);
        return newAbsence;
    }

    public Integer configureRandomActiveResource(String resourceType, String phone, String email, String ipad) throws Exception {
        Integer resourceId = "CITY RESOURCE".equalsIgnoreCase(resourceType) ? dbHelperResources.getRandomActiveCityTech() : dbHelperResources.getContractorWithConfiguredRate();
        configureResource(resourceId, phone, email, ipad);
        return resourceId;
    }

    public void configureResource(int resourceId, String phone, String email, String ipad) throws Exception {
        Resource resource = getResource(resourceId);

        if ("no".equals(phone)) {
            List<PhoneNumber> phoneNumbers = new ArrayList<>();
            resource.setPhoneNumbers(phoneNumbers);
        } else {
            List<PhoneNumber> phoneNumbers = resource.getPhoneNumbers();
            int phoneNumberTypeId = "MOBILE".equalsIgnoreCase(phone) ? 1 : 2;
            if (phoneNumbers.isEmpty()) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setPhoneNumber(DataGenerator.generatePhoneNumber());
                phoneNumber.setPhoneNumberTypeId(phoneNumberTypeId);
                phoneNumbers = new ArrayList<>();
                phoneNumbers.add(phoneNumber);
                resource.setPhoneNumbers(phoneNumbers);
            } else if (phoneNumbers.get(0).getPhoneNumber().isEmpty()) {
                phoneNumbers.get(0).setPhoneNumber(DataGenerator.generatePhoneNumber());
                phoneNumbers.get(0).setPhoneNumberTypeId(phoneNumberTypeId);
            } else {
                phoneNumbers.get(0).setPhoneNumberTypeId(phoneNumberTypeId);
            }
        }

        if (!"WITH".equalsIgnoreCase(email)) {
            resource.setEmailAddresses(null);
        } else {
            List<EmailAddress> emailAddresses = resource.getEmailAddresses();
            if (emailAddresses.isEmpty()) {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmailAddress("testAutomation@city-holdings.co.uk");
                emailAddresses = new ArrayList<>();
                emailAddresses.add(emailAddress);
                resource.setEmailAddresses(emailAddresses);
            }
        }

        if ("NO".equalsIgnoreCase(ipad)) {
            resource.setHasIPad(false);
        } else if ("WITH".equalsIgnoreCase(ipad)) {
            resource.setHasIPad(true);
            if (!dbHelperResources.ipadUserHasLogin(resourceId)) {
                dbHelperResources.insertNewRowApplicationUserMobileApplication(resourceId);
            }
        }

        createOrUpdateResource(resource);
    }

    public void addAdditionalResource(String jsonPayload) throws Exception {
        String url = propertyHelper.getMercuryUrl() + "/api/additionalresource";
        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        int statusCode = restService.getStatusCode();
        logger.debug("additionalResource response status code: " + statusCode);
        assertTrue("Failed to add additional resource", statusCode == 200);
    }

    public void addAdditionalResource_resourceId(int jobId, int resourceId) throws Exception {
        String resourceTemplate = getResourceTemplateAsString("additionalResource_resourceId.json");
        resourceTemplate = resourceTemplate.replaceAll("<JOB_ID>", String.valueOf(jobId));
        resourceTemplate = resourceTemplate.replace("<RESOURCE_ID>", String.valueOf(resourceId));
        addAdditionalResource(resourceTemplate);
    }

    public void addAdditionalResource_resourceProfileId(int jobId, int resourceProfileId) throws Exception {
        String resourceTemplate = getResourceTemplateAsString("additionalResource_resourceProfileId.json");
        resourceTemplate = resourceTemplate.replaceAll("<JOB_ID>", String.valueOf(jobId));
        resourceTemplate = resourceTemplate.replace("<PROFILE_ID>", String.valueOf(resourceProfileId));
        addAdditionalResource(resourceTemplate);
    }

    public void removeResource(Integer resourceAssignmentId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/api/ResourceAssignment/removeresource";

        String payload = "resourceAssignmentId=%d&requestedBy=TestAutomation&reason=34&notes=ta&isApprovalRequired=false&RequestedStatus=14";
        restService.sendPostRequest(url, String.format(payload, resourceAssignmentId), mercuryCookie);

        int statusCode = restService.getStatusCode();
        assertTrue("Failed to remove resource: " + resourceAssignmentId, statusCode == 200);
    }

    public void removeResourceAdvise(Integer resourceAssignmentId) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + "/web/api/ResourceAction/removeresourceadvise";

        String payload = "resourceAssignmentId=%d&reason=&notes=Removal+notes&isRemovedSelected=true";
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

    public void transferResource(int resourceAssignmentId, int newResourceId) throws Exception {
        String payload = getResourceTemplateAsString("transferResource.json");
        payload = payload.replace("RESOURCE_ASSIGNMENT_ID", String.valueOf(resourceAssignmentId)).replace("NEW_RESOURCE_ID", String.valueOf(newResourceId));

        String url = propertyHelper.getMercuryUrl() + "/api/ResourceAssignment/transferresource";
        restService.sendPostRequest(url, String.format(payload, resourceAssignmentId), mercuryCookie);

        int statusCode = restService.getStatusCode();
        assertTrue("Failed to transfer resource assignment id " + resourceAssignmentId + "to resource " + newResourceId, statusCode == 200);
    }
}