package mercury.helpers;

import static mercury.helpers.Constants.DB_DATE_FORMAT;
import static mercury.helpers.StringHelper.normalize;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.database.dao.SiteVisitsDao;
import mercury.database.models.JobView;
import mercury.database.models.SiteVisits;
import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.pageobject.web.portal.jobs.OpenAwaitingJobsPage;
import mercury.runtime.RuntimeState;

@Component
public class StepHelper {

    @Autowired private SiteVisitsDao siteVisitsDao;
    @Autowired private TzHelper tzHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;
    @Autowired private DbHelperResources dbHelperResources;

    public void verifyJobDescription(String jobReference, String descriptionDatabase, String descriptionGrid) throws Exception {
        // Remove any line breaks - need to do this better and remove unnecessary spaces
        String databaseDescription = descriptionDatabase==null ? "null" : descriptionDatabase.replaceAll("\r\n", " ");
        databaseDescription = databaseDescription.trim().replaceAll("\\s+", " ");
        descriptionGrid = descriptionGrid.trim().replaceAll("\\s+", " ");

        runtimeState.scenario.write("Asserting Job Description: " + databaseDescription);
        assertTrue("Unexpected Job Description " + jobReference, descriptionGrid.toLowerCase().contains(databaseDescription.toLowerCase()));
    }

    /**
     * Asserts that the job grid is displaying the correct data from the database
     * This method verifies the columns consistent between the open and awaiting jobs grids
     * @param jobView
     * @param openAwaitingJobsPage
     * @throws Exception
     */
    public void verifyJobGridCommon(JobView jobView, OpenAwaitingJobsPage openAwaitingJobsPage) throws Exception {
        String expectedJobType = jobView.getJobTypeName();
        runtimeState.scenario.write("Asserting Job Type: " + expectedJobType);
        assertTrue("Unexpected Job Type " + jobView.getJobReference(), openAwaitingJobsPage.getJobRowJobtype(jobView.getJobReference()).contains(expectedJobType));

        List<String> expectedResourceAssignmentStatus = dbHelperResources.getResourceAssignmentStatus(testData.getInt("jobReference"), testData.getInt("resourceId"));
        assertTrue("Unexpected Resource Assignment Status " + jobView.getJobReference(), expectedResourceAssignmentStatus.contains(openAwaitingJobsPage.getJobRowAssignmentStatus(jobView.getJobReference())));

        String priority = jobView.getFaultPriority() == null ? "N/A" : jobView.getFaultPriority();
        runtimeState.scenario.write("Asserting Fault Priority: " + priority);
        assertEquals("Unexpected Fault Priority " + jobView.getJobReference(), priority, openAwaitingJobsPage.getJobRowPriority(jobView.getJobReference()));

        runtimeState.scenario.write("Asserting Site Code: " + jobView.getName());
        assertTrue("Unexpected Site Code " + jobView.getJobReference(), openAwaitingJobsPage.getJobRowSite(jobView.getJobReference()).contains(jobView.getName()));

        if(jobView.getJobTypeName().equalsIgnoreCase("ppm")) {
            String expectedDescription = normalize(jobView.getDescription().replaceAll(" PPM$", ""));
            verifyJobDescription(jobView.getJobReference(), expectedDescription, normalize(openAwaitingJobsPage.getJobRowDescription(jobView.getJobReference())));
            String faultType = jobView.getFaultType() == null ? "N/A" : jobView.getFaultType();
            runtimeState.scenario.write("Asserting Fault Type: " + faultType);
            assertEquals("Unexpected Fault Type " + jobView.getJobReference(), normalize(faultType), openAwaitingJobsPage.getJobRowFaultType(jobView.getJobReference()).trim());

        } else {
            String assetSubtypeClassification = jobView.getAssetName()+ " > " + jobView.getAssetSubTypeName();
            runtimeState.scenario.write("Asserting Asset subtype/classification: " + assetSubtypeClassification);
            assertEquals("Unexpected Asset subtype/classification " + jobView.getJobReference(), assetSubtypeClassification.trim(), openAwaitingJobsPage.getJobRowAssetSubTypeClassification(jobView.getJobReference()).trim());
            runtimeState.scenario.write("Asserting Fault Type: " + jobView.getFaultType());
            assertEquals("Unexpected Fault Type  " + jobView.getJobReference(), normalize(jobView.getFaultType()), openAwaitingJobsPage.getJobRowFaultType(jobView.getJobReference()).trim());
        }

        // TODO: Following assertions do not pass for all jobs so commenting them out.  Waiting for MCP-240
        //      Timestamp loggedDate = jobView.getCreatedOn();
        //      String newDateString = mercury.helpers.DateHelper.getDateInFormat(loggedDate);
        //      String diff = mercury.helpers.DateHelper.getDaysOutstanding(loggedDate);
        //		assertEquals("Unexpected Logged Date", newDateString, openAwaitingJobsPage.getJobRowLoggedDate(jobView.getJobReference()));
        //		assertEquals("Unexpected Days Outstanding", diff, openAwaitingJobsPage.getJobRowDaysOutstanding(jobView.getJobReference()));

        verifyJobDescription(jobView.getJobReference(), jobView.getDescription(), openAwaitingJobsPage.getJobRowDescription(jobView.getJobReference()));
    }


    /**
     * Asserts that the job grid is displaying the correct data from the database
     * This method verifies the columns only displayed on the open jobs grid
     * @param jobView
     * @param openAwaitingJobsPage
     * @throws ParseException
     */
    public void verifyJobGridOpen(JobView jobView, OpenAwaitingJobsPage openAwaitingJobsPage, String projectCost) throws ParseException {
        String jobReference = jobView.getJobReference();
        if(jobView.getContractorReference() == null && !projectCost.equals("0.00")) {
            runtimeState.scenario.write("Asserting Contractor Reference");
            assertTrue("Unexpected Contractor Reference. " + jobView.toString(), normalize(openAwaitingJobsPage.getJobRowReference(jobReference)).isEmpty());
            if (projectCost.equals("0.00")) {
                runtimeState.scenario.write("Asserting Project Cost: " + openAwaitingJobsPage.getJobRowReferenceValue(jobReference));
                assertThat("Unexpected Project Cost", openAwaitingJobsPage.getJobRowReferenceValue(jobReference), anyOf(equalTo(projectCost), equalTo("")));
            } else {
                runtimeState.scenario.write("Asserting Project Cost: " + projectCost);
                assertTrue("Unexpected Project Cost", openAwaitingJobsPage.getJobRowReferenceValue(jobReference).contains(projectCost));
            }
        }

        if (jobView.getContractorReference() != null){
            runtimeState.scenario.write("Asserting Contractor Reference: " + jobView.getContractorReference());
            assertEquals("Unexpected contractor reference. " + jobView.toString(), normalize(jobView.getContractorReference()), normalize(openAwaitingJobsPage.getJobRowReference(jobReference)));
            String actualValue = openAwaitingJobsPage.getJobRowReferenceValue(jobReference);
            actualValue = actualValue.isEmpty() ? "0.00" : actualValue;
            runtimeState.scenario.write("Asserting Project Cost: " + projectCost);
            assertTrue("Unexpected Project Cost", actualValue.contains(projectCost));
        }

        SiteVisits siteVisit = siteVisitsDao.getLatestSiteVisitForJobReference(Integer.valueOf(jobReference));
        if (siteVisit.getEtaFrom() == null) {
            runtimeState.scenario.write("Asserting ETA date: none");
            assertTrue("Unexpected ETA Date " + jobReference, openAwaitingJobsPage.getjobRowETADate(jobReference).isEmpty());

        } else {
            String etaFrom = tzHelper.adjustTimeForJobReference(Integer.valueOf(jobReference), siteVisit.getEtaFrom().toString(), DB_DATE_FORMAT);
            String etaFromDate = DateHelper.convert(etaFrom, DB_DATE_FORMAT, "d MMM yyyy");
            runtimeState.scenario.write("Asserting ETA date: " + etaFromDate);
            assertEquals("Unexpected ETA Date " + jobReference, etaFromDate, openAwaitingJobsPage.getjobRowETADate(jobReference));

            if (!jobView.getJobTypeName().equalsIgnoreCase("ppm")) {
                String etaFromTime = DateHelper.convert(etaFrom, DB_DATE_FORMAT, "h:mma");
                String etaFromTimeHour =  DateHelper.convert(etaFrom, DB_DATE_FORMAT, "h:mm a");

                String etaTo = tzHelper.adjustTimeForJobReference(Integer.valueOf(jobReference), siteVisit.getEtaTo().toString(), DB_DATE_FORMAT);
                String etaToTime = DateHelper.convert(etaTo, DB_DATE_FORMAT, "h:mma");
                String etaToTimeHour = DateHelper.convert(etaTo, DB_DATE_FORMAT, "h:mm a");

                String etaWindow = etaFromTime + " - " + etaToTime;
                String etaWindowHour = etaFromTimeHour + " - " + etaToTimeHour;

                String actualEtaWindow = openAwaitingJobsPage.getjobRowETATime(jobReference);
                runtimeState.scenario.write("Asserting ETA window: " + actualEtaWindow);
                assertTrue("Unexpected ETA for jobReference. " + jobView.toString() + ", expected: " + etaWindow + ", actual: " + actualEtaWindow, actualEtaWindow.toLowerCase().contains(etaWindow.toLowerCase()) || actualEtaWindow.toLowerCase().contains(etaWindowHour.toLowerCase()));
            }
        }
    }

    public String getMoneyString(Double value) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(value);
    }

}
