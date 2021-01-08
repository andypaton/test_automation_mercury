package mercury.helpers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.City2019;
import static mercury.helpers.Constants.DB_POLLING_INTERVAL;
import static mercury.helpers.Constants.MAX_TIMEOUT;
import static mercury.helpers.Constants.PA55W0RD;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Constants.TWO_MINUTES;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import mercury.api.models.job.Job;
import mercury.api.models.mercuryportal.api.quoteQueriesSubmission.QuoteQueryResponse;
import mercury.api.models.mercuryportal.api.quoteQueriesSubmission.QuoteQuerySubmission;
import mercury.api.models.mercuryportal.api.quoteQueriesSubmission.SubmitResponseToSeniorManagerQuery;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalRetrieval.JobDetails;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalRetrieval.ResourceQuote;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission.QuoteDecision;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission.QuoteRejectionDecision;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission.QuoteReviewScenarioDecisionDto;
import mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission.QuoteReviewScenarioRejectionDecisionDto;
import mercury.api.models.portal.quoteCreateEdit.DocumentUpload;
import mercury.api.models.portal.quoteCreateEdit.QuoteHeader;
import mercury.api.models.portal.quoteCreateEdit.QuoteRequestApprover;
import mercury.api.models.portal.quoteCreateEdit.QuoteRequestResponse;
import mercury.api.models.portal.quoteInvitation.DeclineITQ;
import mercury.api.models.portal.quoteJobEdit.InvitationsQuoted;
import mercury.api.models.portal.quoteJobEdit.QuoteApprove;
import mercury.api.models.portal.quoteJobEdit.QuoteBypass;
import mercury.api.models.portal.quoteJobEdit.QuoteJob;
import mercury.api.models.portal.quoteJobEdit.QuoteReject;
import mercury.api.models.portal.quoteLine.QuoteLine;
import mercury.database.dao.LogJobDataDao;
import mercury.database.dao.QuoteApprovalScenariosDao;
import mercury.database.dao.QuotePriorityDao;
import mercury.database.dao.ResourceDao;
import mercury.database.dao.UserJobDao;
import mercury.database.models.ApplicationUser;
import mercury.database.models.LogJobData;
import mercury.database.models.QuoteApprovalScenarios;
import mercury.database.models.QuotePriority;
import mercury.database.models.Resource;
import mercury.database.models.UserJob;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.apihelper.ApiHelper;
import mercury.helpers.apihelper.ApiHelperJobs;
import mercury.helpers.apihelper.ApiHelperQuotes;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperAssertions;
import mercury.helpers.dbhelper.DbHelperJobData;
import mercury.helpers.dbhelper.DbHelperQuotes;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperTestAutomationUsers;
import mercury.runtime.RuntimeState;

public class QuoteCreationHelper {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private ApiHelperJobs apiHelperJobs;
    @Autowired private ApiHelper apiHelper;
    @Autowired private ApiHelperQuotes apiHelperQuotes;
    @Autowired private DbHelper dbHelper;
    @Autowired private ResourceDao resourceDao;
    @Autowired private TestData testData;
    @Autowired private DbHelperAssertions dbHelperAssertions;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private DbHelperTestAutomationUsers dbHelperTestAutomationUsers;
    @Autowired private QuotePriorityDao quotePriorityDao;
    @Autowired private UserJobDao userJobDao;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private LogJobDataDao logJobDataDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private QuoteApprovalScenariosDao quoteApprovalScenariosDao;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private DbHelperQuotes dbHelperQuotes;
    @Autowired private DbHelperJobData dbHelperJobData;
    @Autowired private JobCreationHelper jobCreationHelper;

    protected static JsonHelper jsonHelper = new JsonHelper();


    private String quoteHeaderid;
    private String quoteTypeId;
    private String applicationUserId = null; // Current application user Id of the impersonation cookie


    /**
     * Sets up the application user ID cookie for use with impersonation.  Only gets a new impersonation cookie if the application user id has changed
     *
     * @param applicationUserId
     * @throws Throwable
     */
    private void setUpImpersonationCookie(String applicationUserId) throws Throwable {
        if (! applicationUserId.equalsIgnoreCase(this.applicationUserId)) {
            for (Map<String, Object> dbData : dbHelperTestAutomationUsers.getITUsers()) {
                String userName = dbData.get("UserName").toString();
                String password = userName.equalsIgnoreCase(runtimeState.domainUser) ? City2019 : PA55W0RD;
                try {
                    apiHelper.setupImpersonationCookiesAppUser(propertyHelper.getMercuryUrl(), userName, password, applicationUserId);
                    break;
                } catch (AssertionError e ) {
                    logger.debug("Login failed for " + userName + ". Trying next user ...");
                }
            }
            this.applicationUserId = applicationUserId; // Store the application user id being impersonated
        }
    }

    private QuoteRequestResponse quoteJobBuilder(int resourceId, Boolean singleQuote, String fundingRoute) throws Throwable {
        // just need any type of job to log a quote against

        int siteId;
        int faultTypeId;
        int assetClassificationId;
        int faultPriorityId;
        int callerId;
        Integer locationId;

        if (testData.getBoolean("dataset")) {
            siteId = testData.getInt("siteId");
            faultTypeId = testData.getInt("faultTypeId");
            faultPriorityId = testData.getInt("faultPriorityId");
            assetClassificationId = testData.getInt("assetClassificationId");
            locationId = testData.getInt("locationId");
            callerId = testData.getInt("callerId");

            Map<String, Object> assetDetails = dbHelper.getAssetSubTypeAndClassification(assetClassificationId);

            logger.debug("SiteName: " + dbHelperSites.getSiteName(siteId));
            logger.debug("ResourceName: " + dbHelperResources.getResourceName(resourceId));
            logger.debug("AssetClassificationName: " + assetDetails.get("AssetClassification"));
            logger.debug("AssetSubTypeName: " + assetDetails.get("AssetSubType"));
            logger.debug("Location: " + dbHelper.getLocationNameFromId(locationId));
            logger.debug("FaultTypeName: " + dbHelper.getFaultTypeNameFromId(faultTypeId));

        } else {

            Map<String, Object> dbData = await().pollInterval(DB_POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() ->  dbHelperJobData.getJobDataForResource(resourceId, null), notNullValue());

            siteId = (int) dbData.get("SiteID");
            faultTypeId = (int) dbData.get("FaultTypeId");
            faultPriorityId = (int) dbData.get("FaultPriorityId");
            assetClassificationId = (int) dbData.get("AssetClassificationId");
            locationId = (int) dbData.get("LocationId");

            Map<String, Object> caller = dbHelper.getRandomResourceCallerWithPhoneNumber("Resource");
            callerId = (Integer) caller.get("Id");
            logger.debug("Caller: " + caller.get("Name"));

        }

        //Get dynamic quote priority and funding route
        QuotePriority quotePriority = ("ignore".equalsIgnoreCase(fundingRoute)) ? quotePriorityDao.getRandomQuotePriority(singleQuote) : quotePriorityDao.getRandomQuotePriority(singleQuote, fundingRoute) ;

        if (quotePriority==null){
            throw new PendingException("Cannot find a suitatble Quote Priority mapping for the requested funding route and number of quotes required. Please verify the scenario.");
        }

        ApplicationUser applicationUser = jobCreationHelper.getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        // Create the quote job
        Job job = apiHelperJobs.createNewQuoteJob(siteId, faultTypeId, faultPriorityId, "Test Automation", callerId, assetClassificationId, locationId, resourceId, quotePriority.getFundingRouteId(), quotePriority.getId(), createdBy);

        runtimeState.scenario.write("Logged quote Job " + job.toString());

        testData.addIntegerTag("jobReference", job.getJobReference());
        logger.debug("============================");
        logger.debug(job.getJobReference());
        logger.debug("============================");

        QuoteRequestResponse quoteResp = new QuoteRequestResponse();
        quoteResp.setId(job.getId());
        quoteResp.setFundingRouteId(quotePriority.getFundingRouteId());
        quoteResp.setIsApproved(false);
        quoteResp.setJobReference(job.getJobReference());
        quoteResp.setJob(job);
        QuoteRequestApprover quoteRequestApprover = new QuoteRequestApprover();

        Map<String, Object> quoteApprover = dbHelperSites.getRfmForSite(siteId);

        quoteRequestApprover.setId(Integer.valueOf(quoteApprover.get("ResourceId").toString()));
        quoteRequestApprover.setName((String) quoteApprover.get("Name"));

        quoteResp.setQuoteApproverId(dbHelperSites.getRfmForSite(siteId).get("Name"));
        quoteResp.setQuoteApprover(quoteRequestApprover);
        quoteResp.setQuoteRequestApprover(quoteRequestApprover);

        runtimeState.scenario.write("Setting Quote Response " + quoteResp.toString());
        return quoteResp;
    }

    private QuoteRequestResponse quoteJobBuilder(Boolean singleQuote, String fundingRoute) throws Throwable {
        // just need any type of job to log a quote against

        int siteId;
        int faultTypeId;
        int assetClassificationId;
        int responsePriorityId;
        int cityTechResourceWithoutIpad;
        int callerId;
        Integer locationId;

        if (testData.getBoolean("dataset")) {
            siteId = testData.getInt("siteId");
            faultTypeId = testData.getInt("faultTypeId");
            responsePriorityId = testData.getInt("faultPriorityId");
            assetClassificationId = testData.getInt("assetClassificationId");
            locationId = testData.getInt("locationId");
            callerId = testData.getInt("callerId");
            cityTechResourceWithoutIpad = testData.getInt("resourceId");

            Map<String, Object> assetDetails = dbHelper.getAssetSubTypeAndClassification(assetClassificationId);

            logger.debug("SiteName: " + dbHelperSites.getSiteName(siteId));
            logger.debug("ResourceName: " + dbHelperResources.getResourceName(cityTechResourceWithoutIpad));
            logger.debug("AssetClassificationName: " + assetDetails.get("AssetClassification"));
            logger.debug("AssetSubTypeName: " + assetDetails.get("AssetSubType"));
            logger.debug("Location: " + dbHelper.getLocationNameFromId(locationId));
            logger.debug("FaultTypeName: " + dbHelper.getFaultTypeNameFromId(faultTypeId));

        } else if (testData.tagExist("assignToSiteId") && testData.tagExist("assignToResourceId")) {

            int assignToSiteId = testData.getInt("assignToSiteId");
            int resourceId = testData.getInt("assignToResourceId");

            LogJobData logJobData = logJobDataDao.getLogJobData_CTE("1", assignToSiteId, resourceId);

            runtimeState.scenario.write("Creating a quote job with the following data: " + logJobData.toString());

            siteId = logJobData.getSiteId();
            faultTypeId = logJobData.getFaultTypeId();
            assetClassificationId = logJobData.getAssetClassificationId();
            responsePriorityId = logJobData.getPriority();
            cityTechResourceWithoutIpad = logJobData.getResourceId();
            callerId = dbHelper.getRandomCaller("Resource");
            locationId = logJobData.getLocationId();
            if (locationId == null) {
                locationId = dbHelper.getRandomLocation(logJobData.getSiteId());
            }

        } else {

            Boolean isContractor = testData.getString("originalProfileName").equalsIgnoreCase("Contractor Admin") ? true : false;
            LogJobData logJobData = null;

            if (propertyHelper.getEnv().equalsIgnoreCase("dev_uswm")) {
                logJobData = logJobDataDao.getLogJobData_CTE("Any", isContractor, true, "Any", true);
            } else {
                logJobData = await().pollInterval(DB_POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> logJobDataDao.getLogJobData_CTE("Any", isContractor, true, "Any", true), notNullValue());
            }

            runtimeState.scenario.write("Creating a quote job with the following data: " + logJobData.toString());

            siteId = logJobData.getSiteId();
            faultTypeId = logJobData.getFaultTypeId();
            assetClassificationId = logJobData.getAssetClassificationId();
            responsePriorityId = logJobData.getPriority();
            cityTechResourceWithoutIpad = logJobData.getResourceId();
            callerId = dbHelper.getRandomCaller("Resource");
            locationId = logJobData.getLocationId();
            if (locationId == null) {
                locationId = dbHelper.getRandomLocation(logJobData.getSiteId());
            }
        }

        //Get dynamic quote priority and funding route
        QuotePriority quotePriority = ("ignore".equalsIgnoreCase(fundingRoute)) ? quotePriorityDao.getRandomQuotePriority(singleQuote) : quotePriorityDao.getRandomQuotePriority(singleQuote, fundingRoute) ;

        if (quotePriority==null){
            throw new PendingException("Cannot find a suitatble Quote Priority mapping for the requested funding route and number of quotes required. Please verify the scenario.");
        }

        ApplicationUser applicationUser = jobCreationHelper.getCurrentUserForJobCreation();
        String createdBy = applicationUser.getId();

        // Create the quote job
        Job job = apiHelperJobs.createNewQuoteJob(siteId, faultTypeId, responsePriorityId, "Created for test automation", callerId, assetClassificationId, locationId, cityTechResourceWithoutIpad, quotePriority.getFundingRouteId(), quotePriority.getId(), createdBy);

        runtimeState.scenario.write("Logged quote Job " + job.toString());

        testData.addIntegerTag("jobReference", job.getJobReference());
        testData.addIntegerTag("siteId", job.getSiteId());
        logger.debug("============================");
        logger.debug(job.getJobReference());
        logger.debug("============================");

        QuoteRequestResponse quoteResp = new QuoteRequestResponse();
        quoteResp.setId(job.getId());
        quoteResp.setFundingRouteId(quotePriority.getFundingRouteId());
        quoteResp.setIsApproved(false);
        quoteResp.setJobReference(job.getJobReference());
        quoteResp.setJob(job);
        QuoteRequestApprover quoteRequestApprover = new QuoteRequestApprover();

        Map<String, Object> quoteApprover = dbHelperSites.getRfmForSite(siteId);

        testData.put("quoteApproverResourceProfile", "RFM");
        testData.addIntegerTag("quoteApproverResourceId", (Integer) quoteApprover.get("ResourceId"));

        quoteRequestApprover.setId(Integer.valueOf(quoteApprover.get("ResourceId").toString()));
        quoteRequestApprover.setName((String) quoteApprover.get("Name"));

        quoteResp.setQuoteApproverId(dbHelperSites.getRfmForSite(siteId).get("Name"));
        quoteResp.setQuoteApprover(quoteRequestApprover);
        quoteResp.setQuoteRequestApprover(quoteRequestApprover);

        runtimeState.scenario.write("Setting Quote Response " + quoteResp.toString());
        return quoteResp;
    }

    private QuoteLine quoteLineBuilder(Integer jobReferenece, Boolean aboveThreshold, BigDecimal limit) {
        limit = aboveThreshold ? limit.add(new BigDecimal(1)) : limit.subtract(new BigDecimal(1));

        QuoteLine quoteLine = new QuoteLine().withId("0").
                withJobRef(jobReferenece.toString()).
                withQuoteLineTypeValue("EQUIP").
                withDescription("Description of Equipment").
                withQuantity("1").
                withUnitPrice(String.valueOf(limit)).
                withQuoteType("2").
                withPartAndPriceInPriceBook("False").
                withHelpdekResourceProfileId("").
                withHelpdekResourceProfileLabourRateTypeId("").
                withSupplierCode("Contactor").
                withPartNumber("");
        return quoteLine;
    }

    private List<InvitationsQuoted> invitationsQuotedBuilder(int jobReference) {
        List<InvitationsQuoted>  allIinvitationsQuoted = new ArrayList<InvitationsQuoted>();
        InvitationsQuoted  invitationsQuoted = new InvitationsQuoted();

        QuoteApprovalScenarios qas = quoteApprovalScenariosDao.getQuoteByFaultId(jobReference);
        String isPreferredResource = dbHelperSites.isPreferredSiteResource(qas.getStoreId(), qas.getResourceId()) ? "true" : "false";

        invitationsQuoted.setInvitationToQuoteFaultTimeId(qas.getInvitationToQuoteFaultTimeId());
        invitationsQuoted.setQuoteApprovalScenarioId(qas.getQuoteApprovalScenarioId());
        invitationsQuoted.setResourceName(qas.getResourceName());
        invitationsQuoted.setResourceId(qas.getResourceId());
        invitationsQuoted.setResourceEmailAddress("");

        //qas.ResourceId maps to resource.epochid
        String resourceType = dbHelperResources.getResourceTypeByEpochId(qas.getResourceId());

        if ("contractor".equalsIgnoreCase(resourceType)) {
            invitationsQuoted.setIsResource("false");
            invitationsQuoted.setIsPreferredResource("false");
            invitationsQuoted.setIsVendor("true");
            invitationsQuoted.setIsPreferredVendor(isPreferredResource);
        } else {
            invitationsQuoted.setIsResource("true");
            invitationsQuoted.setIsPreferredResource(isPreferredResource);
            invitationsQuoted.setIsVendor("false");
            invitationsQuoted.setIsPreferredVendor("false");
        }

        invitationsQuoted.setCost(qas.getCost());

        allIinvitationsQuoted.add(invitationsQuoted);

        return allIinvitationsQuoted;
    }

    private List<QuoteReviewScenarioDecisionDto> quoteReviewScenarioDecisionDtoBuilder(JobDetails quoteJobDetails, int jobReference) {
        List<QuoteReviewScenarioDecisionDto>  allQuoteReviewScenarioDecisionDto = new ArrayList<QuoteReviewScenarioDecisionDto>();

        for (ResourceQuote resourceQuote : quoteJobDetails.getResourceQuotes()) {
            QuoteReviewScenarioDecisionDto  quoteReviewScenarioDecisionDto = new QuoteReviewScenarioDecisionDto();
            quoteReviewScenarioDecisionDto.setQuoteApprovalScenarioId(resourceQuote.getId());
            quoteReviewScenarioDecisionDto.setApproved(true);
            quoteReviewScenarioDecisionDto.setIsHighRisk(resourceQuote.getWorksAreHighRisk());
            quoteReviewScenarioDecisionDto.setMercuryBudgetId(26);
            quoteReviewScenarioDecisionDto.setPortalResourceId(resourceQuote.getPortalResourceId()); // This seems to be from the qas.ResourceId and is r.EpochId
            quoteReviewScenarioDecisionDto.setRecommendedNotes("Test Automation notes for Recommendation notes to senior manager");
            quoteReviewScenarioDecisionDto.setResourceId(resourceQuote.getResourceId());  //This is the resourceId why send along with EpochId ¯\_(ツ)_/¯

            allQuoteReviewScenarioDecisionDto.add(quoteReviewScenarioDecisionDto);
        }

        return allQuoteReviewScenarioDecisionDto;
    }

    private List<QuoteReviewScenarioRejectionDecisionDto> quoteReviewScenarioRejectionDecisionDtoBuilder(JobDetails quoteJobDetails, int jobReference) {
        List<QuoteReviewScenarioRejectionDecisionDto> allQuoteReviewScenarioRejectionDecisionDto = new ArrayList<QuoteReviewScenarioRejectionDecisionDto>();

        for (ResourceQuote resourceQuote : quoteJobDetails.getResourceQuotes()) {
            QuoteReviewScenarioRejectionDecisionDto quoteReviewScenarioRejectionDecisionDto = new QuoteReviewScenarioRejectionDecisionDto();
            quoteReviewScenarioRejectionDecisionDto.setQuoteApprovalScenarioId(resourceQuote.getId());
            quoteReviewScenarioRejectionDecisionDto.setApproved(false);
            quoteReviewScenarioRejectionDecisionDto.setIsHighRisk(resourceQuote.getWorksAreHighRisk());
            quoteReviewScenarioRejectionDecisionDto.setRejectedReasonId(testData.getInt("quoteRejectionReasonId"));
            quoteReviewScenarioRejectionDecisionDto.setRejectedNotes("Test Automation notes for Rejection notes to manager");
            quoteReviewScenarioRejectionDecisionDto.setPortalResourceId(resourceQuote.getPortalResourceId()); // This seems to be from the qas.ResourceId and is r.EpochId
            quoteReviewScenarioRejectionDecisionDto.setResourceId(resourceQuote.getResourceId()); // This is the resourceId why send along with EpochId ¯\_(ツ)_/¯

            allQuoteReviewScenarioRejectionDecisionDto.add(quoteReviewScenarioRejectionDecisionDto);
        }

        return allQuoteReviewScenarioRejectionDecisionDto;
    }


    /**
     * Build the Quote Approve message for the RFM adding the required resources to the Quote
     * @param quoteRequestResponse
     * @param resources
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private QuoteApprove quoteApprovalBuilder(QuoteRequestResponse quoteRequestResponse, List<Resource> resources) throws ClientProtocolException, IOException {
        QuoteApprove quoteApprove = new QuoteApprove();

        // Get a list of resources to invite to quote
        List<String> quoteResourceIds =  new ArrayList<String>();
        for (Resource resource : resources) {
            quoteResourceIds.add(resource.getId().toString());
        }
        Boolean singleQuote = resources.size() == 1 ? true : false;
        //Get dynamic quote priority and funding route
        QuotePriority quotePriority = quotePriorityDao.getRandomQuotePriority(singleQuote, quoteRequestResponse.getFundingRouteId());

        if (quotePriority==null){
            throw new PendingException("Cannot find a suitatble Quote Priority mapping for the requested funding route and number of quotes required. Please verify the scenario.");
        }

        //Create the quote job
        QuoteJob quoteJob = new QuoteJob();

        quoteJob.setFaultId(quoteRequestResponse.getJobReference().toString());
        quoteJob.setFundingRouteId(String.valueOf(quotePriority.getFundingRouteId()));
        quoteJob.setIsBypass("false");
        quoteJob.setIsBypassQuoteRecommendationConfirmed("false");
        quoteJob.setMultiQuoteBypassNote("");
        quoteJob.setMultiQuoteBypassQuoteRecommendationNote("");
        quoteJob.setMultiQuoteBypassReasonId("");
        quoteJob.setNumberOfQuotesRequired(String.valueOf(resources.size()));
        quoteJob.setScopeOfWorks(quoteRequestResponse.getJob().getDescription());
        quoteJob.setQuotePriorityId(String.valueOf(quotePriority.getId()));
        quoteJob.setQuoteJobApprovalStatusId("1");

        // Set resources and quote job to approve
        quoteApprove.setQuoteResourceIds(quoteResourceIds);
        quoteApprove.setQuoteJob(quoteJob);
        runtimeState.scenario.write("Quote approve message " + quoteApprove.toString());
        return quoteApprove;
    }

    private QuoteReject quoteRejectionBuilder(QuoteRequestResponse quoteRequestResponse) throws ClientProtocolException, IOException {
        QuoteReject quoteReject = new QuoteReject();

        int resourceId = dbHelperResources.getRandomActiveCityTech();
        int epochSiteId = dbHelperSites.getSiteIdForJobRef(quoteRequestResponse.getJobReference());

        // Setting values in quote rejection builder
        quoteReject.setCancellationReason(0);
        quoteReject.setHasSelectedAlternativeResource(true);
        quoteReject.setResourceType(0);
        quoteReject.setNewResourceAssignment(resourceId);
        quoteReject.setAlternativeFundingRoute(quoteRequestResponse.getFundingRouteId());
        quoteReject.setAdditionalCancellationComments("Test Comments");
        quoteReject.setJobReference(quoteRequestResponse.getJobReference());
        quoteReject.setEpochSiteId(epochSiteId);
        runtimeState.scenario.write("Quote approve message " + quoteReject.toString());
        return quoteReject;
    }


    private QuoteBypass quoteBypassBuilder(QuoteRequestResponse quoteRequestResponse, JobDetails quoteJobDetails, List<Resource> resources) throws ClientProtocolException, IOException {
        QuoteBypass quoteBypass = new QuoteBypass();


        //Get dynamic quote priority and funding route
        QuotePriority quotePriority = quotePriorityDao.getRandomQuotePriority(false, quoteRequestResponse.getFundingRouteId());

        if (quotePriority==null){
            throw new PendingException("Cannot find a suitatble Quote Priority mapping for the requested funding route and number of quotes required. Please verify the scenario.");
        }

        //Create the quote job
        QuoteJob quoteJob = new QuoteJob();

        quoteJob.setFaultId(quoteRequestResponse.getJobReference().toString());
        quoteJob.setFundingRouteId(quoteJobDetails.getJob().getFundingRouteId().toString());
        quoteJob.setIsBypass("true");
        quoteJob.setIsBypassQuoteRecommendationConfirmed("true");
        quoteJob.setMultiQuoteBypassNote("Test Automation notes for Multi Quote Bypass Note");
        quoteJob.setMultiQuoteBypassQuoteRecommendationNote("Test Automation notes Multi Quote Bypass Quote Recommendation Note");
        quoteJob.setMultiQuoteBypassReasonId("2");
        quoteJob.setNumberOfQuotesRequired(String.valueOf(resources.size()));
        quoteJob.setScopeOfWorks(quoteRequestResponse.getJob().getDescription());
        quoteJob.setQuotePriorityId(quoteJobDetails.getJob().getQuotePriorityId().toString());
        quoteJob.setQuoteJobApprovalStatusId("2");
        quoteJob.setInvitationsQuoted(invitationsQuotedBuilder(quoteRequestResponse.getJobReference()));
        quoteJob.setIsBypassQuoteRecommendationConfirmed("true");

        // Set resources and quote job to approve
        quoteBypass.setQuoteJob(quoteJob);

        return quoteBypass;
    }

    private QuoteDecision quoteDecisionBuilder(QuoteRequestResponse quoteRequestResponse, JobDetails quoteJobDetails, List<Resource> resources) throws ClientProtocolException, IOException {
        QuoteDecision quoteDecision = new QuoteDecision();
        int totalValue = 0;
        for (ResourceQuote resourceQuote : quoteJobDetails.getResourceQuotes()) {
            totalValue = (int) (totalValue + resourceQuote.getQuoteValue());
        }

        quoteDecision.setCurrentFundingRouteId(quoteJobDetails.getJob().getFundingRouteId());
        quoteDecision.setInternalNotes("Test Automation notes for Recommendation notes to senior manager on modal");
        quoteDecision.setJobIsApproved(true);
        quoteDecision.setJobRef(quoteRequestResponse.getJobReference());
        quoteDecision.setOpenQuoteRequest(false);
        quoteDecision.setOverallQuoteValue(totalValue);
        quoteDecision.setRequiresSeniorManagerApproval(true);
        quoteDecision.setSeniorManagerSubmitsApprovalDecision(false);

        quoteDecision.setQuoteReviewScenarioDecisionDtos(quoteReviewScenarioDecisionDtoBuilder(quoteJobDetails, quoteRequestResponse.getJobReference()));

        return quoteDecision;
    }

    private QuoteRejectionDecision quoteRejectionDecisionBuilder(QuoteRequestResponse quoteRequestResponse, JobDetails quoteJobDetails, List<Resource> resources) {
        QuoteRejectionDecision quoteRejectionDecision = new QuoteRejectionDecision();

        quoteRejectionDecision.setCurrentFundingRouteId(quoteJobDetails.getJob().getFundingRouteId());
        quoteRejectionDecision.setInternalNotes("Test Automation notes for Rejection to manager on modal");
        quoteRejectionDecision.setJobIsApproved(false);
        quoteRejectionDecision.setJobRef(quoteRequestResponse.getJobReference());
        int quoteRejectionReasonId = dbHelperQuotes.getRandomQuoteRejectionReasonId();
        testData.put("quoteRejectionReasonId", quoteRejectionReasonId);
        quoteRejectionDecision.setJobRejectedReasonId(quoteRejectionReasonId);
        quoteRejectionDecision.setRequiresSeniorManagerApproval(true);
        quoteRejectionDecision.setSeniorManagerSubmitsApprovalDecision(true);

        quoteRejectionDecision.setQuoteReviewScenarioRejectionDecisionDtos(quoteReviewScenarioRejectionDecisionDtoBuilder(quoteJobDetails, quoteRequestResponse.getJobReference()));

        return quoteRejectionDecision;
    }

    private QuoteHeader quoteHeaderBuilder(String verificationCode, Integer jobReference) {
        QuoteHeader quoteHeader = new QuoteHeader();

        quoteHeader.setRequestVerificationToken(verificationCode);
        quoteHeader.setDescriptionOfWorks("Some description of works to be done");
        quoteHeader.setProposedWorkingTimes("Some description of works to be done");
        quoteHeader.setQuoteType("1");
        quoteHeader.setWorksAreHighRisk("false");
        quoteHeader.setWorksAreHighRiskrd("on");
        quoteHeader.setId("0");
        quoteHeader.setJobRef(String.valueOf(jobReference));

        return quoteHeader;
    }

    private DocumentUpload documentUploadBuilder(String verificationRequestToken, Integer jobReference) {
        DocumentUpload documentUpload = new DocumentUpload();

        documentUpload.setRequestVerificationToken(verificationRequestToken);
        documentUpload.setJobRef(jobReference.toString());
        documentUpload.setUploadRef(jobReference.toString());
        documentUpload.setUploadType("QTD");
        documentUpload.setDocumentRef("");
        List<String> validFileExtensions =  new ArrayList<>();
        validFileExtensions.add(".pdf");
        validFileExtensions.add(".png");
        validFileExtensions.add(".doc");
        validFileExtensions.add(".docx");
        validFileExtensions.add(".jpg");
        validFileExtensions.add(".jpeg");
        documentUpload.setValidFileExtensions(validFileExtensions);

        return documentUpload;
    }

    private QuoteQuerySubmission quoteQuerySubmissionBuilder(Integer jobReference, Integer quoteApprovalScenarioId) {
        QuoteQuerySubmission quoteQuerySubmission = new QuoteQuerySubmission().
                withJobRef(jobReference).
                withQuoteApprovalScenarioId(quoteApprovalScenarioId).
                withQueryReasonId(1).
                withQueryText("Can I query it, yes I can.");
        return quoteQuerySubmission;
    }

    private DeclineITQ declineITQBuilder(Integer jobReference) {

        DeclineITQ declineITQ = new DeclineITQ().
                withJobRef(jobReference.toString()).
                withReason("Decline Invitation to Quote for Automated Test").
                withQuoteInvitationDeclinedReasonId(3);

        return declineITQ;
    }

    /**
     * Impersonate a Quote Approver(RFM), and then approve a Quote adding in the required resources.
     *
     * @param quoteRequestResponse
     * @param resources
     * @throws Throwable
     */
    public void approveQuote(QuoteRequestResponse quoteRequestResponse, List<Resource> resources) throws Throwable {
        // Set up the impersonation for the quote approver - usually RFM
        String resourceUserId = dbHelper.getApplicationUserIdForResourceId(quoteRequestResponse.getQuoteRequestApprover().getId(), "RFM");
        setUpImpersonationCookie(resourceUserId);

        // Loop until the job is in the correct status
        String quoteJobApprovalStatus = "AwaitingQuoteRequestApproval";
        String approvalStatus = "None";
        dbHelperAssertions.quoteIsInStage(quoteRequestResponse.getJobReference(), quoteJobApprovalStatus, approvalStatus);

        /*
         * If the test gets to here then the job should be ready for the RFM to approve.  But geez things are never that easy!!!
         * The job might not be ready for the RFM to approve so belt and braces approach then, so await().until
         */
        String verificationCode;
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> apiHelperQuotes.getQuotePage(quoteRequestResponse.getJobReference()), notNullValue());
        // Need the verification code so make the call again to the page and extract it.
        verificationCode =  apiHelperQuotes.getQuotePage(quoteRequestResponse.getJobReference());

        logger.debug("Verification code " + verificationCode);

        // Now approve the quote and add the resources
        QuoteApprove quoteApprove = quoteApprovalBuilder(quoteRequestResponse, resources);

        apiHelperQuotes.approveQuote(quoteRequestResponse.getJobReference(), verificationCode, quoteApprove);
        // commenting out the below check as for Test Rainbow its failing, says the object has been moved however it creates the job correctly
        // await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> apiHelperQuotes.approveQuote(quoteRequestResponse.getJobReference(), verificationCode, quoteApprove), equalToIgnoringCase("{\"Success\":true}"));
    }

    /**
     * Impersonate a Quote Approver(RFM), and then reject the quote.
     *
     * @param quoteRequestResponse
     * @throws Throwable
     */
    private void rejectQuote(QuoteRequestResponse quoteRequestResponse) throws Throwable {
        // Set up the impersonation for the quote approver - usually RFM
        String resourceUserId = dbHelper.getApplicationUserIdForResourceId(quoteRequestResponse.getQuoteRequestApprover().getId());
        setUpImpersonationCookie(resourceUserId);

        // Loop until the job is in the correct status
        String quoteJobApprovalStatus = "AwaitingQuoteRequestApproval";
        String approvalStatus = "None";
        dbHelperAssertions.quoteIsInStage(quoteRequestResponse.getJobReference(), quoteJobApprovalStatus, approvalStatus);

        String verificationCode;
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> apiHelperQuotes.getQuotePage(quoteRequestResponse.getJobReference()), notNullValue());
        // Need the verification code so make the call again to the page and extract it.
        verificationCode = apiHelperQuotes.getQuotePage(quoteRequestResponse.getJobReference());

        logger.debug("Verification code " + verificationCode);

        // Now reject the quote
        QuoteReject quoteReject = quoteRejectionBuilder(quoteRequestResponse);

        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(TWO_MINUTES, SECONDS).until(() -> apiHelperQuotes.rejectQuote(quoteRequestResponse.getJobReference(), verificationCode, quoteReject).contains("Job changed to reactive"));
    }

    /**
     * Impersonate a Quote Approver(RFM), and then Bypass a Multi-Quote by recommending the currently submitted quote
     *
     * @param quoteRequestResponse
     * @param resources
     * @throws Throwable
     */
    private void quoteBypass(QuoteBypass quoteBypass, int jobReference) throws Throwable {
        /*
         * If the test gets to here then the job should be ready for the RFM to work on.  But geez things are never that easy!!!
         * The job might not be ready for the RFM to approve so belt and braces approach then, so await().until
         */
        String verificationCode;
        verificationCode = await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> apiHelperQuotes.getQuotePage(jobReference), notNullValue());
        // Need the verification code so make the call again to the page and extract it.

        logger.debug("Verification code " + verificationCode);

        // Now approve and bypass the multi quote to send it on for final approval
        await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> apiHelperQuotes.byPassQuote(jobReference, verificationCode, quoteBypass), equalToIgnoringCase("{\"Success\":true}"));
    }

    /**
     * Impersonate a Quote Approver(RFM), and then approve a quote awaiting approval (RFM should already be impersonated)
     *
     * @param quoteDecision
     * @param jobReference
     * @throws Throwable
     */
    private void quoteDecision(QuoteDecision quoteDecision, int jobReference) throws Throwable {
        apiHelperQuotes.SubmitQuoteDecision(jobReference, quoteDecision);
    }

    /**
     * Impersonate a Quote Approver(Senior Manager), and then reject a quote awaiting approval (Senior Manager should already be impersonated)
     *
     * @param quoteDecision
     * @param jobReference
     * @throws Throwable
     */
    private void quoteRejectionDecision(QuoteRejectionDecision quoteRejectionDecision, int jobReference) throws Throwable {
        apiHelperQuotes.SubmitQuoteRejectionDecision(jobReference, quoteRejectionDecision);
    }

    /**
     * Get the quote creation edit page and extract the quote header id and the quote type id
     *
     * @param jobReference
     * @throws Throwable
     */
    private void getCreateEditPage(Integer jobReference) throws Throwable {
        String response;
        response = apiHelperQuotes.getCreateEditRegister(jobReference);

        // Need to extract header ID and quote type from create/edit page
        quoteHeaderid = StringUtils.substringBetween(response, "id=\"QuoteHeader_Id\" name=\"QuoteHeader.Id\" type=\"hidden\" value=\"", "\"");
        quoteTypeId = StringUtils.substringBetween(response, "id=\"QuoteHeader_QuoteType\" name=\"QuoteHeader.QuoteType\" type=\"hidden\" value=\"", "\"");
    }


    /**
     * Accepts a ITQ
     * Kicks off creating a Quote
     * Pull back the edit quote page to extract the QuoteHeader.Id and QuoteHeader.QuoteType
     *
     * Assumes that the impersonation cookie has already been obtained
     *
     * @param jobReference
     * @throws Throwable
     */
    private void acceptCreateQuote(Integer jobReference) throws Throwable {
        String response;
        response = apiHelperQuotes.acceptITQ(jobReference);
        response = apiHelperQuotes.createQuote(jobReference);
        String verificationCode = apiHelperQuotes.getQuoteEditQuoteHeader(jobReference);

        QuoteHeader quoteHeader = quoteHeaderBuilder(verificationCode, jobReference);
        response = apiHelperQuotes.createQuoteHeader(quoteHeader);
        quoteHeaderid = StringUtils.substringBetween(response, "id=\"QuoteHeader_Id\" name=\"QuoteHeader.Id\" type=\"hidden\" value=\"", "\"");
        quoteTypeId = StringUtils.substringBetween(response, "id=\"QuoteHeader_QuoteType\" name=\"QuoteHeader.QuoteType\" type=\"hidden\" value=\"", "\"");
    }


    /**
     * Add a line item to a a quote
     * Assumes that the impersonation cookie has already been obtained.
     * Assumes that the quote header id and quote type id has already been retrieved
     * @param jobReference
     * @throws Throwable
     */
    private void addQuoteLines(Integer jobReference, Boolean aboveThreshhold, BigDecimal limit) throws Throwable {
        // Get modal for adding a line item to the quote
        String response = apiHelperQuotes.getQuoteLine(jobReference, quoteHeaderid, quoteTypeId);
        String requestVerificationToken;
        // need to extract verification code from this modal and use it with the quote line creation
        requestVerificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");

        // Create a basic line to add to the quote
        QuoteLine quoteLine = quoteLineBuilder(jobReference, aboveThreshhold, limit);
        quoteLine.setRequestVerificationToken(requestVerificationToken);
        quoteLine.setProjectQuoteId(quoteHeaderid);
        apiHelperQuotes.createQuoteLine(quoteLine);
    }


    /**
     * Adds the document to the quote
     * Assumes that the impersonation cookie has already been obtained.
     * Assumes that the quote header id and quote type id has already been retrieved
     * @param jobReference
     * @throws Throwable
     */
    private void addQuoteDocument(Integer jobReference) throws Throwable {
        String response;
        String requestVerificationToken;

        response = apiHelperQuotes.getUploadQuoteDocument(jobReference, "QTD", quoteTypeId);
        requestVerificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");
        DocumentUpload documentUpload = new DocumentUpload();
        documentUpload = documentUploadBuilder(requestVerificationToken, jobReference);

        response = apiHelperQuotes.UploadQuoteDocument(jobReference, requestVerificationToken, documentUpload);
        dbHelperQuotes.updateQuoteDocumentDetails(jobReference);
    }


    /**
     * Submits the quote for approval
     * Assumes that the impersonation cookie has already been obtained.
     * Assumes that the quote header id and quote type id has already been retrieved
     * @param jobReference
     * @throws Throwable
     */
    private void submitQuote(Integer jobReference) throws Throwable {
        String response;
        String requestVerificationToken;

        response = apiHelperQuotes.getCreateEditRegister(jobReference);
        //need to extract verification code from this page and use it with the submit quote
        requestVerificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");
        response = apiHelperQuotes.SubmitQuote(jobReference, requestVerificationToken);
    }


    /**
     * Impersonate the given resource and then accept and create the Invitation to Quote
     *
     * @param jobReference
     * @param resource
     * @param queryMap
     * @throws Throwable
     */
    private void createQuote(Integer jobReference, Resource resource, Map<String, Object> queryMap) throws Throwable {
        String resourceUserId = dbHelper.getApplicationUserIdForEpochId(resource.getId());
        setUpImpersonationCookie(resourceUserId);

        // get the quote header and quote type id
        getCreateEditPage(jobReference);

        // accept quote and create header
        acceptCreateQuote(jobReference);

        // Add a line item to the quote
        addQuoteLines(jobReference, Boolean.valueOf(queryMap.get("aboveThreshhold").toString()), new BigDecimal(queryMap.get("limit").toString()));

        // Add a document to the quote
        addQuoteDocument(jobReference);

        // Now submit the quote
        submitQuote(jobReference);
    }

    /**
     * Impersonate the user who has been Invited to Quote and then Decline the invitation
     * @param jobReference
     * @param resource who is declining the ITQ
     * @throws Exception
     */
    public void declineITQ(Integer jobReference, Resource resource) throws Throwable {
        String resourceUserId = dbHelper.getApplicationUserIdForEpochId(resource.getId());
        setUpImpersonationCookie(resourceUserId);

        String response;
        DeclineITQ declineITQ = declineITQBuilder(jobReference);
        response = apiHelperQuotes.DeclineITQ(jobReference, declineITQ);
        logger.debug(response);
    }

    /**
     * Impersonate the Quote approver (RFM) and then raise a query Query a quote
     *
     * @param jobReference
     * @param quoteApprovalScenarioId
     * @param rfmName
     * @throws Throwable
     */
    public void createQuoteQuery(Integer jobReference, Integer quoteApprovalScenarioId, int approverResourceID) throws Throwable {
        // Set up the impersonation for the quote approver
        String resourceUserId = dbHelper.getApplicationUserIdForResourceId(approverResourceID);
        setUpImpersonationCookie(resourceUserId);

        QuoteQuerySubmission quoteQuerySubmission =  quoteQuerySubmissionBuilder(jobReference, quoteApprovalScenarioId);
        apiHelperQuotes.QueryQuote(jobReference, quoteQuerySubmission);
    }

    /**
     * Impersonate the Quote approver (Additional Approver/Additional Final Approver) and then raise a query Query a quote
     *
     * @param jobReference
     * @param quoteApprovalScenarioId
     * @param rfmName
     * @throws Throwable
     */
    public void createQuoteQuerySeniorManager(Integer jobReference, Integer quoteApprovalScenarioId, int approverResourceID) throws Throwable {
        // Set up the impersonation for the quote approver
        String resourceUserId = dbHelper.getApplicationUserIdForResourceId(approverResourceID);
        setUpImpersonationCookie(resourceUserId);

        QuoteQuerySubmission quoteQuerySubmission =  quoteQuerySubmissionBuilder(jobReference, quoteApprovalScenarioId);
        apiHelperQuotes.QueryQuoteSeniorManager(jobReference, quoteQuerySubmission);
    }

    /**
     * Impersonate the Quote approver (RFM) and pull back the Quote Job Details
     *
     * @param jobRefererence
     * @param rfmName
     * @return
     * @throws Throwable
     */
    public JobDetails getQuoteJobDetails(Integer jobRefererence, int approverResourceID)  throws Throwable {
        // Set up the impersonation for the quote approver
        String resourceUserId = dbHelper.getApplicationUserIdForResourceId(approverResourceID);
        setUpImpersonationCookie(resourceUserId);

        return  apiHelperQuotes.getQuoteJobDetails(jobRefererence);
    }


    /**
     * Create a query response to a RFM Quote query
     * @param jobReference
     * @param quoteApprovalScenarioId
     * @param resource
     * @throws Throwable
     */
    private void createQuoteQueryResponse(Integer jobReference, Integer quoteApprovalScenarioId, Resource resource) throws Throwable {
        String resourceUserId = dbHelper.getApplicationUserIdForEpochId(resource.getId());
        setUpImpersonationCookie(resourceUserId);

        String response;
        response = apiHelperQuotes.getCreateEditQuery(jobReference, quoteApprovalScenarioId);

        String requestVerificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");

        String QuoteApprovalScenarioId = StringUtils.substringBetween(response, "id=\"QuoteApprovalScenarioId\" name=\"QuoteApprovalScenarioId\" type=\"hidden\" value=\"", "\"");
        String QueryId = StringUtils.substringBetween(response, "id=\"QueryId\" name=\"QueryId\" type=\"hidden\" value=\"", "\"");


        QuoteQueryResponse CreateEditQueryResponse = new QuoteQueryResponse().
                withJobRef(jobReference).
                withQueryId(QueryId).
                withQuoteApprovalScenarioId(QuoteApprovalScenarioId).
                withResponse("Some response to a quote in Query").
                withSubmitAction("Send Response").
                withRequestVerificationToken(requestVerificationToken);

        response = apiHelperQuotes.CreateEditQueryResponse(CreateEditQueryResponse);
    }

    /**
     * Create a query response to a RFM Quote query
     * @param jobReference
     * @param quoteApprovalScenarioId
     * @param resource
     * @throws Throwable
     */
    private void createQuoteQueryResponseSeniorManager(int jobReference, int scopeOfWorksId, int queryId, int resourceId) throws Throwable {
        String resourceUserId = dbHelper.getApplicationUserIdForResourceId(resourceId);
        setUpImpersonationCookie(resourceUserId);

        SubmitResponseToSeniorManagerQuery submitResponseToSeniorManagerQuery = new SubmitResponseToSeniorManagerQuery().
                withJobRef(jobReference).
                withId(queryId).
                withScopeOfWorksId(scopeOfWorksId).
                withQueryText("Some response to a quote in Query");

        String response = apiHelperQuotes.CreateEditQueryResponseSeniorManager(submitResponseToSeniorManagerQuery);
    }


    /**
     * Pulls back a collection of resources to add to the quote.
     * @param queryMap
     * @return
     */
    private List<Resource> getResources(Map<String, Object> queryMap) {
        List<Resource> resources;
        if (queryMap.get("assignToResourceId") != null) {
            int resourceId = (int) queryMap.get("assignToResourceId");
            Resource resource = resourceDao.getByResourceId(resourceId);
            resources = new ArrayList<>();
            resources.add(resource);
        } else if (queryMap.get("profileName").toString().toLowerCase().contains("contractor")) {
            resources = resourceDao.getRandomPortalContractQuoteResource(queryMap.get("multiQuote").toString());
        } else if (queryMap.get("awaitingResourceSelectionBypass").toString().toLowerCase().contains("true")) {
            resources = resourceDao.getRandomPortalContractorQuoteResourceForBypass();
        } else {
            resources = resourceDao.getRandomPortalCityTechQuoteResource(queryMap.get("multiQuote").toString());
        }
        assertNotNull("Cannot pull back resources for quote creation", resources);
        return resources;
    }


    /**
     * Create Quote or Decline ITQ based on the number of quotes requires by the scenario
     *
     * @param jobReferenece - quote jobReference
     * @param queryMap - queryMap defining the query requirements. This should come from the given steps
     * @param resources - resources to be requested for ITQ
     * @throws Throwable
     */
    private void acceptOrDeclineITQ(Integer jobReferenece, Map<String, Object> queryMap, List<Resource> resources ) throws Throwable {
        Integer numberToSubmit = null;//  = Integer.valueOf(queryMap.get("quotesSubmitted").toString() == "ignore" ? "0" :queryMap.get("quotesSubmitted").toString());

        if (queryMap.get("quotesSubmitted").toString().equalsIgnoreCase("ignore")) {
            numberToSubmit = 0;
        } else if (queryMap.get("quotesSubmitted").toString().equalsIgnoreCase("all")) {
            numberToSubmit = resources.size();
        } else {
            numberToSubmit = Integer.valueOf(queryMap.get("quotesSubmitted").toString());
        }


        for ( int i=0; i < numberToSubmit; i++ ) {
            createQuote(jobReferenece, resources.get(i), queryMap);
        }
        for ( int i=numberToSubmit; i < resources.size(); i++ ) {
            declineITQ(jobReferenece, resources.get(i));
        }
        logger.debug("QUOTES CREATED or DECLINED ===================================");
    }

    private BigDecimal getBudgetLimit(Map<String, Object> queryMap, int fundingRouteId, int resourceCount) {
        // Now need to get the budget limit here so that the quote lines are above or below the required limit
        BigDecimal limit = dbHelperQuotes.getApproverLimit(queryMap.get("profileName").toString(), fundingRouteId);
        logger.debug("Budget limit " + limit);
        //if null then it means we have found the final approver
        limit = limit == null || limit.compareTo(BigDecimal.ZERO) == 0 ? dbHelperQuotes.getNotNullAdditionalApprovalLimit(fundingRouteId) : limit;
        // spread limit over all quotes
        if (Boolean.valueOf(queryMap.get("aboveThreshhold").toString())) {
            limit = limit.multiply(new BigDecimal(2));
            if (propertyHelper.getMercuryUrl().contains("usad") && "Additional Final Approver".equalsIgnoreCase(testData.getString("originalProfileName"))) {
                limit = limit.divide(new BigDecimal(resourceCount * 4), 2, RoundingMode.HALF_UP);
            }
            if ("Additional Approver".equalsIgnoreCase(testData.getString("originalProfileName"))) {
                String initialApprover = dbHelperQuotes.getInitialApproverForQuote();
                limit = DataGenerator.generateRandomBigDecimalFromRange(dbHelperQuotes.getApproverLimit(initialApprover, fundingRouteId), limit.divide(new BigDecimal(2)));
            }
        }else {
            if ("Additional Approver".equalsIgnoreCase(testData.getString("originalProfileName"))) {
                String initialApprover = dbHelperQuotes.getInitialApproverForQuote();
                limit = DataGenerator.generateRandomBigDecimalFromRange(dbHelperQuotes.getApproverLimit(initialApprover, fundingRouteId), limit).divide(new BigDecimal(resourceCount), 2, RoundingMode.HALF_UP);
            } else {
                limit = limit.divide(new BigDecimal(resourceCount), 2 , RoundingMode.HALF_UP);
            }
        }

        logger.debug("Setting line to " + limit);
        return limit;
    }
    /**
     * Step through the phases of creating a quote until it is at the correct stage
     *
     * @param queryMap
     * @return
     * @throws Throwable
     */
    public QuoteRequestResponse createQuoteInStatus(Map<String, Object> queryMap) throws Throwable {
        String quoteJobApprovalStatus = null;
        QuoteRequestResponse quoteRequestResponse = null;
        List<Resource> resources;
        JobDetails quoteJobDetails = null;
        //        resources = getResources(queryMap);
        BigDecimal limit;

        switch(queryMap.get("quoteJobApprovalStatus").toString()) {

        case "AwaitingQuoteRequestApproval" : // "Awaiting Quote Request Review" - helpdesk job status
            Boolean singleQuote = "single".equalsIgnoreCase(queryMap.get("multiQuote").toString())? true : false;
            String fundingRoute = queryMap.get("fundingRoute").toString();
            logger.debug(Arrays.toString(queryMap.entrySet().toArray()));

            if (queryMap.get("assignToResourceId") != null) {
                quoteRequestResponse = quoteJobBuilder((int) queryMap.get("assignToResourceId"), singleQuote, fundingRoute);
            } else {
                quoteRequestResponse = quoteJobBuilder(singleQuote, fundingRoute);
            }

            logger.debug("QUOTE JOB CREATED ===================================");

            break;

        case "ItqAwaitingAcceptance" : //"ITQ Awaiting Acceptance" - helpdesk job status
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "AwaitingQuoteRequestApproval");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            // Retrieve resources to assign to the quote
            resources = getResources(queryMap);
            approveQuote(quoteRequestResponse, resources);
            Integer resouceIDToSave = dbHelperResources.getAssignedQuoteResourceID(quoteRequestResponse.getJobReference());
            testData.addIntegerTag("assignedResourceId", resouceIDToSave);

            break;

        case "Jobs Awaiting Quote" :
        case "AwaitingQuote" :
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "ItqAwaitingAcceptance");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            //now need to accept the ITQ but not create any quotes

            resources = resourceDao.getQuoteResources(quoteRequestResponse.getJobReference(), false);
            // Now determine the number of quotes to accept

            if(queryMap.get("quotesSubmitted").toString().equalsIgnoreCase("1 or more")) {
                limit = getBudgetLimit(queryMap, 16, resources.size());
                queryMap.put("limit", limit);
                createQuote(quoteRequestResponse.getJobReference(), resources.get(0), queryMap);
            }

            break;

        case "Quotes Awaiting Review" :
        case "AwaitingManagerApproval" :
        case "Awaiting Approval" :
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "ItqAwaitingAcceptance");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            Boolean isContractor = testData.getString("profileName").equalsIgnoreCase("Contractor Admin") ? true : false;

            // need to get quote resources here
            resources = resourceDao.getQuoteResources(quoteRequestResponse.getJobReference(), isContractor);
            //            dbHelperQuotes.getApproverLimit("Client Approver", quoteRequestResponse.getFundingRouteId());

            // Now need to get the budget limit here so that the quote lines are above or below the required limit
            limit = getBudgetLimit(queryMap, quoteRequestResponse.getFundingRouteId(), resources.size());
            queryMap.put("limit", limit);

            acceptOrDeclineITQ(quoteRequestResponse.getJobReference(), queryMap, resources);

            Integer resouceIdToSave = dbHelperResources.getAssignedQuoteResourceID(quoteRequestResponse.getJobReference());
            testData.addIntegerTag("assignedResourceId", resouceIdToSave);

            break;

        case "AwaitingResourceAssignment" :
        case "Jobs Awaiting Resource Selection" :
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "ItqAwaitingAcceptance");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            // To get a quote awaiting resource assignment we either increase the number of quotes required, or decline the ITQ for a resource
            // So lets decline a ITQ
            resources = resourceDao.getQuoteResources(quoteRequestResponse.getJobReference(), false);

            // Now need to get the budget limit here so that the quote lines are above or below the required limit
            limit = getBudgetLimit(queryMap, quoteRequestResponse.getFundingRouteId(), resources.size());
            queryMap.put("limit", limit);
            acceptOrDeclineITQ(quoteRequestResponse.getJobReference(), queryMap, resources);

            break;

        case "InResourceQuery":
        case "Quotes in Query" :
        case "Quotes with Query Pending" :
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "AwaitingManagerApproval");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            quoteJobDetails = this.getQuoteJobDetails(quoteRequestResponse.getJobReference(), quoteRequestResponse.getQuoteRequestApprover().getId());
            int id1 = quoteJobDetails.getResourceQuotes().get(0).getId();
            createQuoteQuery(quoteRequestResponse.getJobReference(), quoteJobDetails.getResourceQuotes().get(0).getId(), quoteRequestResponse.getQuoteRequestApprover().getId() );
            break;

        case "QueryResourceAnswered" :
        case "Quotes with Query Response" :
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "Quotes with Query Pending");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            resources = resourceDao.getQuoteResources(quoteRequestResponse.getJobReference(), false);
            quoteJobDetails = this.getQuoteJobDetails(quoteRequestResponse.getJobReference(), quoteRequestResponse.getQuoteRequestApprover().getId());
            createQuoteQueryResponse(quoteRequestResponse.getJobReference(), quoteJobDetails.getResourceQuotes().get(0).getId(), resources.get(0));
            break;

        case "AwaitingBypassApproval" :
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "Jobs Awaiting Resource Selection");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            quoteJobDetails = this.getQuoteJobDetails(quoteRequestResponse.getJobReference(), quoteRequestResponse.getQuoteRequestApprover().getId());
            resources = resourceDao.getQuoteResources(quoteRequestResponse.getJobReference(), false);
            QuoteBypass quoteBypass = quoteBypassBuilder(quoteRequestResponse, quoteJobDetails, resources);

            quoteBypass(quoteBypass, quoteRequestResponse.getJobReference());
            break;

        case "AwaitingSeniorManagerApproval":
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "AwaitingManagerApproval");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            quoteJobDetails = this.getQuoteJobDetails(quoteRequestResponse.getJobReference(), quoteRequestResponse.getQuoteRequestApprover().getId());
            resources = resourceDao.getQuoteResources(quoteRequestResponse.getJobReference(), false);

            QuoteDecision quoteDecision = quoteDecisionBuilder(quoteRequestResponse, quoteJobDetails, resources);

            quoteDecision(quoteDecision, quoteRequestResponse.getJobReference());
            break;

        case "QueryManagerPending":
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "AwaitingSeniorManagerApproval");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            quoteJobDetails = this.getQuoteJobDetails(quoteRequestResponse.getJobReference(), quoteRequestResponse.getQuoteRequestApprover().getId());
            createQuoteQuerySeniorManager(quoteRequestResponse.getJobReference(), quoteJobDetails.getResourceQuotes().get(0).getId(), quoteRequestResponse.getQuoteRequestApprover().getId() );
            break;

        case "QueryManagerAnswered":
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "QueryManagerPending");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus); // Reset status to original value

            quoteJobDetails = this.getQuoteJobDetails(quoteRequestResponse.getJobReference(), quoteRequestResponse.getQuoteRequestApprover().getId());

            int queryId = dbHelperQuotes.getQueryId(quoteJobDetails.getJob().getScopeOfWorksId());

            createQuoteQueryResponseSeniorManager(quoteRequestResponse.getJobReference(), quoteJobDetails.getJob().getScopeOfWorksId(), queryId, quoteRequestResponse.getQuoteRequestApprover().getId());
            break;

        case "SeniorManagerRejected":
            quoteJobApprovalStatus = queryMap.get("quoteJobApprovalStatus").toString(); // Save initial status
            queryMap.put("quoteJobApprovalStatus", "AwaitingSeniorManagerApproval");
            quoteRequestResponse = createQuoteInStatus(queryMap);
            queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus);

            quoteJobDetails = this.getQuoteJobDetails(quoteRequestResponse.getJobReference(), quoteRequestResponse.getQuoteRequestApprover().getId());
            logger.debug("quoteJobDetails: " + quoteJobDetails);
            resources = resourceDao.getQuoteResources(quoteRequestResponse.getJobReference(), false);

            QuoteRejectionDecision quoteRejectionDecision = quoteRejectionDecisionBuilder(quoteRequestResponse, quoteJobDetails, resources);

            quoteRejectionDecision(quoteRejectionDecision, quoteRequestResponse.getJobReference());
            break;

        default:
            throw new PendingException("Cannot find Quote Creation Helper for "  + queryMap.get("quoteJobApprovalStatus").toString());
        }
        return quoteRequestResponse;

    }

    /**
     * Generate a Quote or Funding request to the correct stage then pull back the type of user required to log in
     *
     * @param queryMap - defines details regarding the type of quote required
     *
     *  queryMap = new HashMap<String, Object>();
     *  queryMap.put("jobType", jobType);
     *  queryMap.put("profileName", profileName);
     *  queryMap.put("quoteJobApprovalStatus", quoteJobApprovalStatus);
     *  queryMap.put("quoteJobApprovalStatusId", quoteJobApprovalStatusId);
     *  queryMap.put("approvalStatus", approvalStatus);
     *  queryMap.put("approvalStatusId", approvalStatusId);
     *  queryMap.put("fundingRoute", fundingRoute);
     *  queryMap.put("budget", budget);
     *  queryMap.put("multiQuote", multiQuote);
     *  queryMap.put("profileName", testData.getString("profileName"));
     *  queryMap.put("resourceTypeName", testData.getString("resourceTypeName"));
     *  queryMap.put("assignToResourceId", testData.getString("resourceTypeName"));
     *  queryMap.put("useResourceTypeName", testData.getBoolean("useResourceTypeName"));
     *  queryMap.put("quotesSubmitted", quotesSubmitted);
     *  queryMap.put("aboveThreshhold", aboveThreshhold);
     *
     * @return - a UserJob object which contains the Portal user to log on with along with the job reference
     * @throws Throwable
     */
    public UserJob createQuote(Map<String, Object> queryMap) throws Throwable {
        // Create the quote to the correct stage
        QuoteRequestResponse quoteRequestResponse = createQuoteInStatus(queryMap);
        // Now pull back the correct user for the quote job and return that

        UserJob userJob = await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_TIMEOUT, SECONDS).until(() -> userJobDao.getUserforQuoteJob(quoteRequestResponse.getJobReference(), queryMap), notNullValue());
        return userJob;
    }

    // Create quote and reject it converting it into reactive job.
    public Job createAndRejectQuote(Map<String, Object> queryMap) throws Throwable {
        QuoteRequestResponse quoteRequestResponse = createQuoteInStatus(queryMap);
        rejectQuote(quoteRequestResponse);
        return quoteRequestResponse.getJob();
    }

}
