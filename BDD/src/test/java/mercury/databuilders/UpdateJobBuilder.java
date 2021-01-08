package mercury.databuilders;

import static mercury.helpers.Globalisation.SHORT;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.applicationinsights.core.dependencies.apachecommons.lang3.RandomUtils;

import mercury.api.models.portal.job.updateFGas2019.GasCylinder;
import mercury.api.models.portal.job.updateFGas2019.GasDetails;
import mercury.api.models.portal.job.updateFGas2019.GasLeakSite;
import mercury.api.models.portal.job.updateFGas2019.JobDetails;
import mercury.api.models.portal.job.updateFGas2019.Update;
import mercury.helpers.DateHelper;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.runtime.RuntimeState;

public class UpdateJobBuilder {
    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;

    /**
     * Builds a valid gasCylinder object to be used when creating a site visit.
     * All we want is a site visit with gas used, therefore we are just creating the minimum information required.
     * @param gasCylinderDetails - Map of gas details used to populate the gas cylinder information
     * @return
     */
    private List<GasCylinder> createGasCylinders(Map<String, Object> gasCylinderDetails) {
        List<GasCylinder> gasCylinders = new ArrayList<GasCylinder>();

        GasCylinder gasCylinder = new GasCylinder();
        gasCylinder.setGasSourceTypeId(gasCylinderDetails.get("GasSourceTypeId").toString());
        gasCylinder.setGasCylinderTypeId(gasCylinderDetails.get("GasCylinderTypeId").toString());
        gasCylinder.setRefrigerantSourceLocation("");
        gasCylinder.setCylinderSerialNumber("ACME-".concat(DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0)));
        gasCylinder.setGasCylinderCapacityId(gasCylinderDetails.get("GasCylinderCapacityId").toString());
        gasCylinder.setInitialQuantity(gasCylinderDetails.get("Capacity").toString().split("\\.")[0]);
        gasCylinder.setInstalledQuantity(gasCylinderDetails.get("Capacity").toString().split("\\.")[0]);
        gasCylinder.setIsPartialCylinder("false"); // not sure if this should be true or false but works anyway
        gasCylinder.setSurplusDestinationId("");
        gasCylinder.setSurplusTypeId("");
        gasCylinder.setReturnedTo("");
        gasCylinder.setSurplus("0");

        gasCylinders.add(gasCylinder);

        return gasCylinders;
    }


    /**
     * Builds a gas leak site check block to be used when creating a site visit.
     * @param returning - will determine if data generated forces the status of returning or not.
     * @return
     * @throws ParseException
     */
    private List<GasLeakSite> createGasLeakSites(boolean returning) throws ParseException {
        Map<String, Object> leakSiteInformation = dbHelperGas.getValidLeakSiteInformation(returning);
        logger.debug("leakSiteInformation to be used in test data configuration" + leakSiteInformation);


        List<GasLeakSite> gasLeakSites =  new ArrayList<GasLeakSite>();
        GasLeakSite gasLeakSite = new GasLeakSite();

        gasLeakSite.setPrecedingGasLeakSiteCheckId("");
        gasLeakSite.setPrecedingGasLeakSiteCheckUuid("");
        gasLeakSite.setGasLeakLocationId(leakSiteInformation.get("GasLeakLocationId").toString());
        gasLeakSite.setPrimaryComponentInformation(DataGenerator.generateRandomSentence());
        gasLeakSite.setGasLeakSubLocationId(leakSiteInformation.get("GasLeakSubLocationId").toString());
        gasLeakSite.setGasLeakSiteStatusId(leakSiteInformation.get("GasLeakSiteStatusId").toString());
        gasLeakSite.setGasLeakInitialTestId(leakSiteInformation.get("GasLeakInitialTestId").toString());
        gasLeakSite.setGasLeakFollowUpTestId(leakSiteInformation.get("GasLeakFollowUpTestId").toString());

        gasLeakSites.add(gasLeakSite);
        return gasLeakSites;
    }

    /**
     * Builds a valid gasDetails object to be used when creating a site visit.
     * Assuming we can sent certain information to null.  If need be this can be changed.
     * @param gasCylinderDetails
     * @param leakCheckDetails
     * @param withAsset - has an asset to be used in the site visit update only valid when gasUsed or leakCheck
     * @param returningcreateGasDetails
     * @param applianceType - taken from the table GasApplianceType
     * @param siteId - Site Id for the job
     * @return
     * @throws ParseException
     */
    private GasDetails createGasDetails(Map<String, Object> gasCylinderDetails, Map<String, Object> leakCheckDetails,  boolean leakCheck, boolean withAsset, boolean returning, int gasApplianceTypeId, Integer assetID) throws ParseException {
        GasDetails gasDetails = new GasDetails();

        gasDetails.setGasApplianceTypeId(String.valueOf(gasApplianceTypeId));
        if(withAsset) {
            gasDetails.setAssetId(String.valueOf(assetID));
            gasDetails.setApplianceInformation("");
        } else {
            gasDetails.setApplianceInformation("Gas appliance information provided");
        }

        // change this to randomly select values?
        gasDetails.setReceiverLevelRecorded("false"); // not setting receiver level - can be changed
        gasDetails.setQuantityOfBallsFloating("");  // null due to above -
        gasDetails.setLevelIndicator("");  // null due to above

        if (gasCylinderDetails != null) {
            gasDetails.setGasTypeId(gasCylinderDetails.get("GasTypeId").toString());
        }
        gasDetails.setNewAssetMaximumCharge(""); // Should this be set - do a DB query....it works without setting this even though there is no max charge for the appliance
        gasDetails.setReasonForChangingMaximumCharge(""); // Should this be set - do a DB query....it works without setting this even though there is no max charge for the appliance

        // Need to get info that forces returning or complete depending on requirements
        gasDetails.setGasLeakCheckStatusId(leakCheckDetails.get("GasLeakCheckStatusId").toString());
        gasDetails.setGasLeakCheckMethodId(leakCheckDetails.get("GasLeakCheckMethodId").toString());
        gasDetails.setGasLeakCheckResultTypeId(leakCheckDetails.get("GasLeakCheckResultTypeId").toString());

        // Create gas cylinders and leak site info
        if (gasCylinderDetails != null) {
            gasDetails.setGasCylinders(createGasCylinders(gasCylinderDetails));
        }
        if (leakCheck) {
            gasDetails.setGasLeakSite(createGasLeakSites(returning));
        }
        return gasDetails;
    }



    /**
     * Builds a job update object - to be used to set up jobs with a particular status
     *
     * @param jobId - Job.Id of the job being updated with a site visit
     * @param jobReference - job.JobReference for the job being updated with a site visit
     * @param siteId - Site Id for the job
     * @param profile - Profile of the technician e.g. RHVAC Technician, Contractor Technician
     * @param returning - has the site visit status to be returning?  if true set the visit to returning else set the visit to complete.
     * @param gasUsed - has gas to be used in this site visit
     * @param leakCheck - has a leak check to be carried out in the site visit
     * @param assetTypeName - HVAC, Refrigeration, Unknown
     *
     * @return mercury.api.models.portal.job.updateFGas2019.Update
     * @throws ParseException
     */
    public Update createUpdateGasJob(Integer jobId, Integer jobReference,  int siteId, String profile, String statusOnDeparture, boolean gasUsed, boolean leakCheck, String assetTypeName) throws ParseException {
        boolean forceReturning = "Returning".equalsIgnoreCase(statusOnDeparture) ? true : false;

        Integer assetId = null;
        Update update = new Update();

        update.setRemoteFix("false");
        update.setTravelTime("01:30");
        update.setOperationalOnArrival("false");
        update.setStatusOnDeparture("true");

        // Contractor has empty start and end time
        if (profile.contains("Contractor")) {
            update.setWorkStartTime("");
            update.setWorkEndTime("");
        } else {
            int start = RandomUtils.nextInt(10, 71);    // start job between 10 - 71 hours ago (sql lookups filtering on jobs started in last 3 days!)
            int duration = RandomUtils.nextInt(1, 5);   // random number of hours between 1 - 5 hours
            update.setWorkStartTime(DateHelper.getNowDatePlusOffset(0 - start, SHORT));
            update.setTimeSpent(String.format("0%d:00", duration));
            update.setWorkEndTime(DateHelper.getNowDatePlusOffset(duration - start, SHORT));
            update.setOverTime("");
        }

        // Configure asset information
        boolean withAsset;
        Integer gasApplianceTypeId;
        boolean isPlant;

        // Need to hard code this because the devs have
        switch (assetTypeName) {
        case "HVAC":
            // AC/Water Chiller - Remote System Comfort Cooling - Remote System
            gasApplianceTypeId =  dbHelperGas.getApplianceTypeForNameList("'AC/Water Chiller - Remote System', 'Comfort Cooling - Remote System'");
            withAsset = true;
            isPlant = false;
            break;

        case "Refrigeration" :
            // "Commercial Refrigeration - Remote System";
            gasApplianceTypeId = dbHelperGas.getApplianceTypeForName("Commercial Refrigeration - Remote System");
            withAsset = true;
            isPlant = true;
            break;

        case "7-Eleven_Gas" :
            gasApplianceTypeId =  dbHelperGas.getApplianceTypeForNameList("'Comfort Cooling - Remote System', 'Comfort Cooling - Self Contained'");
            withAsset = true;
            isPlant = false;
            break;

        case "Unknown":
            gasApplianceTypeId = dbHelperGas.getApplianceTypeForNameList("'Commercial Refrigeration - Self Contained', 'Comfort Cooling - Self Contained', 'AC/Water Chiller - Self Contained'");
            isPlant = false;
            withAsset = false;
            break;

        case "Non Gas":
            gasApplianceTypeId = null;
            isPlant = false;
            withAsset = false;
            gasUsed = false; //Make sure this is set to false
            leakCheck = false; //Make sure this is set to false
            break;

        default:
            gasApplianceTypeId = null;
            isPlant = false;
            withAsset = false;
            gasUsed = false; //Make sure this is set to false
            leakCheck = false; //Make sure this is set to false
            break;
        }

        runtimeState.scenario.write("gasApplianceType: " + dbHelperGas.getApplianceType(gasApplianceTypeId));

        // Pull back valid gas cylinder details and leak check details
        Map<String, Object> gasCylinderDetails = dbHelperGas.getValidGasCylinderDetails(profile);
        runtimeState.scenario.write("gasCylinderDetails: " + gasCylinderDetails);

        Map<String, Object> leakCheckDetails = dbHelperGas.getValidLeakCheckDetails(forceReturning);
        runtimeState.scenario.write("leakCheckDetails: " + leakCheckDetails);

        if (gasUsed) {
            update.setGsanIssued("false");

            // Need to be true/null or false/trueorfalse
            update.setUsesGas("true");
            //update.setLeakCheckPerformed("false");
            String leakCheckPerformed = testData.getBoolean("leakCheck") ? "true" : "false";
            update.setLeakCheckPerformed(leakCheckPerformed);

            update.setApplianceType(String.valueOf(gasApplianceTypeId));
            if (withAsset) {
                assetId = dbHelperGas.getAssetID(siteId, isPlant, assetTypeName);
                runtimeState.scenario.write("Appliance Type: " + dbHelperGas.getAssetDetails(assetId));
                update.setApplianceIdentification(String.valueOf(assetId));
                update.setApplianceIdentificationGasType("-1");
            } else {
                update.setApplianceInformation("Some appliance information");
            }
            update.setReceiverLevelRecorded("false");
            update.setRefrigerantType(gasCylinderDetails.get("GasTypeId").toString());
            update.setModifiedMaximumCharge(""); // Should this be set - do a DB query....it works without setting this even though there is no max charge for the appliance
            update.setModifiedMaximumChargeNotes(""); // Should this be set - do a DB query....it works without setting this even though there is no max charge for the appliance


            update.setLeakCheckStatus(leakCheckDetails.get("GasLeakCheckStatusId").toString());
            update.setLeakCheckMethod(leakCheckDetails.get("GasLeakCheckMethodId").toString());
            update.setLeakCheckResultType(leakCheckDetails.get("GasLeakCheckResultTypeId").toString());

            update.setGasDetails(createGasDetails(gasCylinderDetails, leakCheckDetails, leakCheck, withAsset, forceReturning, gasApplianceTypeId, assetId));

        } else if (leakCheck) {
            update.setGsanIssued("false");

            // Need to be true/null or false/trueorfalse
            update.setUsesGas("false");
            update.setLeakCheckPerformed("true");

            update.setApplianceType(String.valueOf(gasApplianceTypeId));
            if(withAsset) {
                assetId = dbHelperGas.getAssetID(siteId, isPlant, assetTypeName );
                update.setApplianceIdentification(String.valueOf(assetId));
            } else {
                update.setApplianceInformation("Some appliance information");
            }
            update.setReceiverLevelRecorded("false");
            //            update.setRefrigerantType(gasCylinderDetails.get("GasTypeId").toString());
            update.setModifiedMaximumCharge(""); // Should this be set - do a DB query....it works without setting this even though there is no max charge for the appliance
            update.setModifiedMaximumChargeNotes(""); // Should this be set - do a DB query....it works without setting this even though there is no max charge for the appliance


            update.setLeakCheckStatus(leakCheckDetails.get("GasLeakCheckStatusId").toString());
            update.setLeakCheckMethod(leakCheckDetails.get("GasLeakCheckMethodId").toString());
            update.setLeakCheckResultType(leakCheckDetails.get("GasLeakCheckResultTypeId").toString());

            update.setGasDetails(createGasDetails(null, leakCheckDetails, leakCheck, withAsset, forceReturning, gasApplianceTypeId, assetId));
        }

        // Need to get info that forces returning, complete or awaiting parts depending on requirements
        if ("returning".equalsIgnoreCase(statusOnDeparture)) {
            update.setResourceAssignmentStatusId("21");     // Returning
            update.setReturningToJobReason("21");

            String etaDate = DateHelper.getNowDatePlusOffset(144,  "dd/MM/yyy"); // might need to check the is covered by globalisation
            update.setEtaDate(etaDate);
            update.setFormEtaWindowId("12");
            update.setEta(DateHelper.convert(etaDate, "MM/dd/yyy", "yyyy-MM-dd").concat("T12:00:00.000Z"));
            update.setEtaWindowId("12");

            // Now create the job details section with job information
            JobDetails jobDetails = new JobDetails();
            jobDetails.setJobId(jobId.toString());
            jobDetails.setJobReference(jobReference.toString());
            update.setJobDetails(jobDetails);

        } else if ("complete".equalsIgnoreCase(statusOnDeparture)) {
            update.setResourceAssignmentStatusId("13");     // Complete
            update.setReturningToJobReason("13");

            update.setAssetCondition("2");
            update.setAdditionalResourceRequired("false");
            update.setQuoteRequired("false");

            // Now create the job details section with job and close down information - probably should get this from the DB
            JobDetails jobDetails = new JobDetails();
            jobDetails.setJobId(jobId.toString());
            jobDetails.setJobReference(jobReference.toString());
            jobDetails.setRootCauseCategoryId("5");
            jobDetails.setRootCauseId("17");
            jobDetails.setAdditionalNotes(DataGenerator.generateRandomSentence());
            update.setJobDetails(jobDetails);

        } else {
            update.setResourceAssignmentStatusId("10");   // Awaiting Parts
            update.setReturningToJobReason("10");

            String etaDate = DateHelper.getNowDatePlusOffset(144,  "dd/MM/yyy"); // might need to check the is covered by globalisation
            update.setEtaDate(etaDate);
            update.setFormEtaWindowId("12");
            update.setEta(DateHelper.convert(etaDate, "MM/dd/yyy", "yyyy-MM-dd").concat("T12:00:00.000Z"));
            update.setEtaWindowId("12");

            // Now create the job details section with job information
            JobDetails jobDetails = new JobDetails();
            jobDetails.setJobId(jobId.toString());
            jobDetails.setJobReference(jobReference.toString());
            update.setJobDetails(jobDetails);
        }
        return update;
    }

    public Update createUpdateJob(Integer jobId, Integer jobReference,  int siteId, String profile, String statusOnDeparture) throws ParseException {
        Update update = new Update();

        update.setAsbestosRegisterChecked("true");
        update.setRemoteFix("false");
        update.setTravelTime("01:30");
        update.setOperationalOnArrival("false");
        update.setStatusOnDeparture("true");

        // *** disabled code - but not deleted incase some jobs start failing and it has to be replaced .... can be removed if this doesn't happen!
        //        // Contractor has empty start and end time
        //        if (profile.contains("Contractor")) {
        //            update.setWorkStartTime("");
        //            update.setWorkEndTime("");
        //        } else {
        int start = RandomUtils.nextInt(10, 71);    // start job between 10 - 71 hours ago (sql lookups filtering on jobs started in last 3 days!)
        int duration = RandomUtils.nextInt(1, 5);   // random number of hours between 1 - 5 hours
        update.setWorkStartTime(DateHelper.getNowDatePlusOffset(0 - start, SHORT));
        update.setTimeSpent(String.format("0%d:00", duration));
        update.setWorkEndTime(DateHelper.getNowDatePlusOffset(duration - start, SHORT));
        update.setOverTime("");
        //        }

        // Need to get info that forces returning, complete or awaiting parts depending on requirements
        if ("returning".equalsIgnoreCase(statusOnDeparture)) {
            update.setResourceAssignmentStatusId("21");     // Returning
            update.setReturningToJobReason("21");

            String etaDate = DateHelper.getNowDatePlusOffset(144,  "dd/MM/yyy"); // might need to check the is covered by globalisation
            update.setEtaDate(etaDate);
            update.setFormEtaWindowId("12");
            update.setEta(DateHelper.convert(etaDate, "MM/dd/yyy", "yyyy-MM-dd").concat("T12:00:00.000Z"));
            update.setEtaWindowId("12");

            // Now create the job details section with job information
            JobDetails jobDetails = new JobDetails();
            jobDetails.setJobId(jobId.toString());
            jobDetails.setJobReference(jobReference.toString());
            update.setJobDetails(jobDetails);

        } else if ("complete".equalsIgnoreCase(statusOnDeparture)) {
            logger.debug("Completing the job : " + jobReference.toString());
            update.setResourceAssignmentStatusId("13");     // Complete
            update.setReturningToJobReason("13");

            update.setAssetCondition("3");
            update.setAdditionalResourceRequired("false");
            update.setQuoteRequired("false");

            String etaDate = DateHelper.getNowDatePlusOffset(144,  "dd/MM/yyy");
            update.setEta(DateHelper.convert(etaDate, "MM/dd/yyy", "yyyy-MM-dd").concat("T12:00:00.000Z"));
            update.setEtaWindowId("24");

            // Now create the job details section with job and close down information - probably should get this from the DB
            JobDetails jobDetails = new JobDetails();
            jobDetails.setJobId(jobId.toString());
            jobDetails.setJobReference(jobReference.toString());
            jobDetails.setRootCauseCategoryId("5");
            jobDetails.setRootCauseId("17");
            jobDetails.setAdditionalNotes("completed by automation test");
            update.setJobDetails(jobDetails);

        } else {
            update.setResourceAssignmentStatusId("10");   // Awaiting Parts
            update.setReturningToJobReason("10");

            String etaDate = DateHelper.getNowDatePlusOffset(144,  "dd/MM/yyy"); // might need to check the is covered by globalisation
            update.setEtaDate(etaDate);
            update.setFormEtaWindowId("12");
            update.setEta(DateHelper.convert(etaDate, "MM/dd/yyy", "yyyy-MM-dd").concat("T12:00:00.000Z"));
            update.setEtaWindowId("12");

            // Now create the job details section with job information
            JobDetails jobDetails = new JobDetails();
            jobDetails.setJobId(jobId.toString());
            jobDetails.setJobReference(jobReference.toString());
            update.setJobDetails(jobDetails);
        }
        return update;
    }

}

