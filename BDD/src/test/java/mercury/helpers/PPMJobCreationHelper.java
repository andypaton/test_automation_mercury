package mercury.helpers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.POLLING_INTERVAL;
import static mercury.helpers.Constants.MAX_PORTAL_TIMEOUT;
import static mercury.helpers.Globalisation.TAX_RATE;
import static mercury.helpers.StringHelper.normalize;
import static org.awaitility.Awaitility.await;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.api.models.ppm.PPMJob;
import mercury.database.dao.PartCodeDao;
import mercury.database.models.PartCode;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.apihelper.ApiHelperHangfire;
import mercury.helpers.apihelper.ApiHelperPPM;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperPPM;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.dbhelper.DbHelperTimeZone;
import mercury.runtime.RuntimeState;

@Component
public class PPMJobCreationHelper {
    private static final Logger logger = LogManager.getLogger();

    private static final String PPM_JOBTYPE = "PPM";

    @Autowired private ApiHelperHangfire apiHelperHangfire;
    @Autowired private DbHelperPPM dbHelperPPM;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TestData testData;
    @Autowired private JobCreationHelper jobCreationHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private ApiHelperPPM apiHelperPPM;
    @Autowired private DbHelperTimeZone dbHelperTimeZone;
    @Autowired private InvoiceCreationHelper invoiceCreationHelper;
    @Autowired private PartCodeDao partCodeDao;

    /**
     * Creates the PPM Job by inserting the rows into DB.
     * @param profileName
     * @return PPM Job.
     * @throws Exception
     */
    private PPMJob createPPMJob(String profileName) throws Exception {
        PPMJob ppmJob = new PPMJob();
        profileName = testData.getString("profileName").contains("Contractor Admin") ? profileName.replaceAll(" .*", "") : profileName;

        String name = "Test_" + profileName + "_" + System.currentTimeMillis();
        String resourceId = testData.getString("resourceId") != null ? testData.getString("resourceId") : dbHelperResources.getRandomContractorResourceIdWithJobCallouts();
        int resourceProfileId = dbHelperResources.getResourceProfileId(profileName);
        String assetTypeId = dbHelper.getRandomActiveAssetId();
        String ppmType = apiHelperPPM.createPpmType(name, "1", assetTypeId, 1, String.valueOf(resourceProfileId), resourceId, "2", 52, "No Override", "1");
        runtimeState.scenario.write("PPM Type - " + ppmType + " has been created");


        // Create 2 PPM jobs
        float totalInvoiceAmount = 0;
        for (int i = 0; i < 2; i++) {
            int callOutRate = 50;
            String siteId = String.valueOf(dbHelperSites.getRandomSiteId());

            String dueDate = DateHelper.getNowDatePlusOffset(48, "yyyy/MM/dd").replaceAll("/", "-");
            dueDate += "T23:00:00.000Z";

            String ppmJobRef = apiHelperPPM.createPpmJobWithResource(ppmType, callOutRate, siteId, String.valueOf(resourceProfileId), String.valueOf(resourceId), dueDate);
            runtimeState.scenario.write("PPM Job - " + ppmJobRef + " has been created");
            testData.put("callOutRate", callOutRate);
            testData.addToList("createdJobs", Integer.valueOf(ppmJobRef));
            ppmJob.setDateTime(dueDate);
            totalInvoiceAmount = totalInvoiceAmount + callOutRate;
        }

        ppmJob.setResourceId(Integer.valueOf(resourceId));
        ppmJob.setPPMTypeName(name);
        testData.put("ppmTypeName", name);
        testData.put("ppmTypeId", ppmType);
        testData.put("resourceId", resourceId);
        testData.put("totalInvoiceAmount", totalInvoiceAmount);
        return ppmJob;
    }

    /**
     * Creates the PPM Job for submission using the builder methods.
     * @param jobReference
     * @param startDateTime
     * @param etaDateTime
     * @return PPM Job with the required information for submission.
     */
    private PPMJob ppmJobSubmissionBuilder(Integer PPMScheduleRef, Integer jobReference, String dueDate, Integer siteId, String startDateTime, String etaDateTime) {
        PPMJob ppmJobSubmission = new PPMJob().
                withPpmId(String.valueOf(PPMScheduleRef)).
                withJobReference(String.valueOf(jobReference)).
                withDateTime(dueDate).
                withResourceId(0).
                withHoursString("0").
                withHours(0).
                withStartDateTime(startDateTime).
                withAffirmationId("1").
                withSubAffirmationId("2").
                withNote("Entering notes for completing the PPM Job " + DateHelper.dateAsString(new Date())).
                withOverTimeHoursString("02:00").
                withOvertimeHours(2).
                withTravelTimeHoursString("01:30").
                withTravelTimeHours(1.5).
                withAssisted(false).
                withSignalReceived(false).
                withSiteId(testData.getInt("siteId")).
                withPpmJobId(String.valueOf(jobReference)).
                withQuoteRequired(false).
                withRemedialJobRequired(false).
                withEtaDateTime(etaDateTime).
                withGasSafetyAdviceNotices(Collections.<Object>emptyList());
        return ppmJobSubmission;
    }

    /**
     * Starts the PPM Job(s)
     * @param ppmJob
     * @return PPM Schedule References
     * @throws Exception
     */
    private List<Integer> startPPMJobs(PPMJob ppmJob) throws Exception {

        // run Hangfire ppm job to create the JobRefs
        apiHelperHangfire.processPPMJobs();

        List<Integer> PPMScheduleRefs = testData.getIntList("createdJobs");

        for (Integer PPMScheduleRef : PPMScheduleRefs) {
            // wait for JobRefs to appear in the Database
            logger.debug("wait for JobRefs to appear in the Database...");
            await().pollInterval(POLLING_INTERVAL, MILLISECONDS).atMost(MAX_PORTAL_TIMEOUT, SECONDS).until(() -> dbHelperPPM.getJobReferenceCount(PPMScheduleRef) != 0);
            int jobReference = dbHelperPPM.getJobReferenceForPpm(PPMScheduleRef);
            int siteId = dbHelperSites.getSiteIdForJobRef(jobReference);
            testData.put("siteId", siteId);

            jobCreationHelper.startContractorJob(jobReference, testData.getInt("resourceId"), PPM_JOBTYPE);
            ppmJob.setStartDateTime(testData.getString("Start time"));
        }
        return PPMScheduleRefs;
    }

    /**
     * Completes the PPM Job(s)
     * @param ppmJob
     * @return PPM Schedule References
     * @throws Exception
     */
    private List<Integer> completePPMJobs(PPMJob ppmJob) throws Exception {
        List<Integer> PPMScheduleRefs = testData.getIntList("createdJobs");
        for (Integer PPMScheduleRef : PPMScheduleRefs) {
            int jobReference = dbHelperPPM.getJobReferenceForPpm(PPMScheduleRef);
            int siteId = dbHelperSites.getSiteIdForJobRef(jobReference);
            testData.put("siteId", siteId);

            apiHelperPPM.getPPMJobDetailsPage(jobReference);

            String ianaCode = dbHelperTimeZone.getIanaCodeForSite(siteId);
            String dueDate = ppmJob.getDateTime();
            String startDateTime = ppmJob.getStartDateTime();
            String etaDateTime = DateHelper.getDateInFormat(Timestamp.valueOf(LocalDateTime.of(LocalDate.now(ZoneId.of(ianaCode)), LocalTime.MIDNIGHT).minusHours(1)), "yyyy-MM-dd'T'HH:mm:ss.000'Z'");
            PPMJob ppmJobDetails =  ppmJobSubmissionBuilder(PPMScheduleRef, jobReference, dueDate, siteId, startDateTime, etaDateTime);

            apiHelperPPM.completePPMJob(PPMScheduleRef, ppmJobDetails);
            runtimeState.scenario.write("The PPM job created is: " + PPMScheduleRef + ", Job Reference: " + jobReference);
        }
        return PPMScheduleRefs;
    }

    /**
     * Adding the Materials or Labour line to the Order
     * @param PPMScheduleRef
     * @param isMandatoryLine
     * @param jobStatus
     */
    private void addMaterialsLabourLine(Integer PPMScheduleRef, boolean isMandatoryLine, String jobStatus) {
        Integer lineType;
        Float quantity = null, unitPrice = null, unitTaxAmount = null;
        String description = null, partCode = null;

        String orderReference = dbHelperPPM.getPPMOrderReference(PPMScheduleRef);
        Integer supplierInvoiceHeaderId = dbHelperPPM.getSupplierInvoiceHeaderId(orderReference);

        if (jobStatus.contains("Materials")) {
            lineType = 1;
            PartCode partDetails = partCodeDao.getRandomPartCode();
            description = normalize(partDetails.getDescription());
            quantity = (float) DataGenerator.randBetween(2, 10);
            unitPrice = BigDecimal.valueOf(partDetails.getUnitPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            partCode = partDetails.getPartCode();
        } else {
            lineType = 2;
            description = "Description for labour work done on site " + DateHelper.dateAsString(new Date());
            Float hours = BigDecimal.valueOf(DataGenerator.GenerateRandomDouble(0.00, 24.00)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            Float minutes = BigDecimal.valueOf(DataGenerator.GenerateRandomDouble(0.00, 59.00)).setScale(2, BigDecimal.ROUND_DOWN).floatValue();
            quantity = hours + minutes;
            unitPrice = BigDecimal.valueOf(DataGenerator.GenerateRandomDouble(1.00, 1000.00)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        unitTaxAmount = BigDecimal.valueOf(unitPrice * TAX_RATE / 100).setScale(2, BigDecimal.ROUND_DOWN).floatValue();
        Float lineValue = unitPrice + unitTaxAmount;

        dbHelperPPM.insertInToSupplierInvoiceLine(supplierInvoiceHeaderId, lineType, description, quantity, unitPrice, unitTaxAmount, lineValue, partCode, isMandatoryLine, orderReference);
        String mandatory = isMandatoryLine == true ? "Mandatory" : "";
        String invoiceLineType = jobStatus.contains("Materials") ? "Materials" : "Labour";
        runtimeState.scenario.write("The "+ mandatory +" "+ invoiceLineType +" line added to the job is: " + PPMScheduleRef);
    }

    /**
     * Adding the required lines to Orders
     * @param PPMScheduleRefs
     * @param isMandatoryLine
     * @param jobStatus
     */
    public void addLines(List<Integer> PPMScheduleRefs, boolean isMandatoryLine, String jobStatus) {
        for (Integer PPMScheduleRef : PPMScheduleRefs) {
            String orderReference = dbHelperPPM.getPPMOrderReference(PPMScheduleRef);
            if (orderReference != null) {
                addMaterialsLabourLine(PPMScheduleRef, isMandatoryLine, jobStatus);
            }
        }
    }

    /**
     * Creates the PPM Job with required data.
     * @param jobStatus
     * @return PPM Job
     * @throws Exception
     */
    public PPMJob createPPMJobData(String jobStatus) throws Exception {
        PPMJob ppmJob = null;
        List<Integer> PPMScheduleRefs;
        boolean isMandatoryLine;

        switch (jobStatus) {
        case "PPM Orders" :
            try {
                ppmJob = createPPMJob(testData.getString("profileName"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;

        case "Start / PPM Orders Awaiting Invoice With No Invoice" :
            ppmJob = createPPMJobData("PPM Orders");
            PPMScheduleRefs = startPPMJobs(ppmJob);
            ppmJob.setPPMScheduleRefs(PPMScheduleRefs);
            break;

        case "Complete / PPM Orders Awaiting Invoice With No Invoice" :
            ppmJob = createPPMJobData("Start / PPM Orders Awaiting Invoice With No Invoice");
            PPMScheduleRefs = completePPMJobs(ppmJob);
            ppmJob.setPPMScheduleRefs(PPMScheduleRefs);
            break;

        case "Complete / PPM Orders With No Invoice In Progress" :
            // Get a contractor with no consolidated invoices in progress
            String resourceId = dbHelperPPM.getRandomContractorResourceIdWithNoConsolidatedInvoicesInProgress();
            testData.put("resourceId", resourceId);
            ppmJob = createPPMJobData("Complete / PPM Orders Awaiting Invoice With No Invoice");
            break;

        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice":
        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice and Documents":
            ppmJob = createPPMJobData("Complete / PPM Orders Awaiting Invoice With No Invoice");
            invoiceCreationHelper.addConsolidatedInvoiceDocument(testData.getIntList("createdJobs"), ppmJob.getResourceId(), testData.getString("profileName"));
            break;

        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Materials Line":
        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Mandatory Materials Line":
        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Labour Line":
        case "Complete / PPM Orders Awaiting Invoice With Existing Invoice and has a Mandatory Labour Line":
            ppmJob = createPPMJobData("Complete / PPM Orders Awaiting Invoice With Existing Invoice");
            isMandatoryLine = jobStatus.contains("Mandatory") ? true : false;
            addLines(ppmJob.getPPMScheduleRefs(), isMandatoryLine, jobStatus);
            break;

        default:
            throw new Exception("Unexpected status: " + jobStatus);
        }

        return ppmJob;
    }
}
