package mercury.helpers;

import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.Globalisation.setWeightLabel;
import static mercury.helpers.StringHelper.normalize;
import static mercury.helpers.StringHelper.trimZeros;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.PendingException;
import mercury.database.dao.GasApplianceTypeDao;
import mercury.database.dao.SiteVisitCylinderDetailsDao;
import mercury.database.dao.SiteVisitGasDetailsDao;
import mercury.database.dao.SiteVisitGasLeakSiteCheckDao;
import mercury.database.dao.SiteVisitsDao;
import mercury.database.dao.SiteVisitsGasUsageDao;
import mercury.database.models.SiteVisitCylinderDetails;
import mercury.database.models.SiteVisitGasDetails;
import mercury.database.models.SiteVisitGasLeakSiteCheck;
import mercury.database.models.SiteVisits;
import mercury.database.models.SiteVisitsGasUsage;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperSites;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.PopupAlert;
import mercury.pageobject.web.admin.refrigerantGasUsage.AdminLeakSiteInformationModal;
import mercury.pageobject.web.admin.refrigerantGasUsage.AdminRefrigerantSourceModal;
import mercury.pageobject.web.pageHelpers.QuestionHelper;
import mercury.runtime.RuntimeState;

@Component
public class FgasAdminHelper {

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;

    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperGas dbHelperGas;
    @Autowired private DbHelperSites dbHelperSites;
    @Autowired private DbHelperResources dbHelperResources;

    @Autowired private SiteVisitsGasUsageDao siteVisitsGasUsageDao;
    @Autowired private SiteVisitGasDetailsDao siteVisitGasDetailsDao;
    @Autowired private SiteVisitCylinderDetailsDao siteVisitCylinderDetailsDao;
    @Autowired private SiteVisitGasLeakSiteCheckDao siteVisitGasLeakSiteCheckDao;
    @Autowired private GasApplianceTypeDao gasApplianceTypeDao;
    @Autowired private SiteVisitsDao siteVisitsDao;

    public static final String BEFORE = "before_";
    public static final String AFTER = "after_";

    public void assertSiteVisitGasDetails() {
        SiteVisits siteVisit = new SiteVisits();
        siteVisit = (SiteVisits) testData.get("siteVisit");
        SiteVisitGasDetails gasDetails = siteVisitGasDetailsDao.get(siteVisit.getId());
        runtimeState.questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();

        assertAnswer("Appliance Type", gasApplianceTypeDao.get(gasDetails.getGasApplianceTypeId()).getName());

        if ( gasDetails.getAssetId() != null ) {
            runtimeState.scenario.write("Asserting Appliance Identification is displayed");
            assertNotNull(runtimeState.questionHelper.getAnswer("Appliance Identification"));
        }

        if ( gasDetails.getApplianceInformation() != null && !gasDetails.getApplianceInformation().isEmpty() ) {
            assertAnswer("Please provide appliance details", gasDetails.getApplianceInformation());
        }

        if ( gasDetails.isReceiverLevelRecorded() ) {
            assertAnswer("Has receiver level been recorded?", "Yes");
        } else {
            assertAnswer("Has receiver level been recorded?", "No");
        }

        if ( gasDetails.getLevelIndicator() != null ) {
            assertAnswer("Provide Level Indicator %", gasDetails.getLevelIndicator());
        }

        if ( gasDetails.getQuantityOfBallsFloating() != null ) {
            assertAnswer("Quantity of Balls Floating", gasDetails.getQuantityOfBallsFloating());
        }

        if ( gasDetails.getGasTypeId() != null ) {
            assertAnswer("Refrigerant Type Used", dbHelperGas.getGasType(gasDetails.getGasTypeId()));
        }

        if ( gasDetails.getGasLeakCheckStatusId() != null ) {
            assertAnswer("Leak Check Status", dbHelperGas.getGasLeakCheckStatus( gasDetails.getGasLeakCheckStatusId() ));
        }

        if ( gasDetails.getGasLeakCheckMethodId() != null ) {
            assertAnswer("Leak Check Method", dbHelperGas.getGasLeakCheckMethod(gasDetails.getGasLeakCheckMethodId()));
        }

        if ( gasDetails.getGasLeakCheckResultTypeId() != null ) {
            assertAnswer("Leak Check Result Type", dbHelperGas.getGasLeakCheckResultType(gasDetails.getGasLeakCheckResultTypeId()));
        }

        if ( gasDetails.getNewAssetMaximumCharge() != null && gasDetails.getNewAssetMaximumCharge() != 0 ) {
            String expectedMaxCharge = String.valueOf(Math.round(gasDetails.getNewAssetMaximumCharge()));
            assertAnswer("Please enter appliance maximum charge", expectedMaxCharge);
        }

        if ( gasDetails.getReasonForChangingMaximumCharge() != null && !gasDetails.getReasonForChangingMaximumCharge().isEmpty() ) {
            assertAnswer("Please provide notes to explain reason for changing appliance maximum charge", gasDetails.getReasonForChangingMaximumCharge());
        }

    }

    public void assertSiteVisitCylinderDetails() {
        SiteVisits siteVisit = new SiteVisits();
        siteVisit = (SiteVisits) testData.get("siteVisit");
        List<SiteVisitCylinderDetails> cylinderDetails = siteVisitCylinderDetailsDao.getForSiteVisitId(siteVisit.getId());
        runtimeState.questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();

        List<String> serialNumbers = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers();
        List<String> sources = runtimeState.adminRefrigerantGasUsageEditPageV2.getSources();
        List<String> used = runtimeState.adminRefrigerantGasUsageEditPageV2.getFullPartial();
        List<String> types = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderTypes();
        List<String> available = runtimeState.adminRefrigerantGasUsageEditPageV2.getGasAvailable();
        List<String> installed = runtimeState.adminRefrigerantGasUsageEditPageV2.getGasInstalled();
        List<String> surplus = runtimeState.adminRefrigerantGasUsageEditPageV2.getSurplus();
        List<String> destinations = runtimeState.adminRefrigerantGasUsageEditPageV2.getDestinations();
        List<String> surplusTypes = runtimeState.adminRefrigerantGasUsageEditPageV2.getSurplusTypes();

        int index = 0;
        for (SiteVisitCylinderDetails cylinder : cylinderDetails) {
            runtimeState.scenario.write("-------------------------------------");

            String expected = cylinder.getBottleNumber();
            runtimeState.scenario.write("Asserting Bottle Number is : " + expected);
            String actual = serialNumbers.get(index);
            assertEquals("Expected: " + expected + ", Actual: " + actual, expected, actual);

            expected = dbHelperGas.getGasSourceType(cylinder.getGasSourceTypeId());
            runtimeState.scenario.write("Asserting Source is: " + expected);
            actual = sources.get(index);
            assertEquals("Expected: " + expected + ", Actual: " + actual, expected, actual);

            expected = cylinder.isPartialCylinder() ? "Partial" : "Full";
            runtimeState.scenario.write("Asserting Full/Partial is: " + expected);
            actual = used.get(index);
            assertEquals("Expected: " + expected + ", Actual: " + actual, expected, actual);

            expected = dbHelperGas.getGasCylinderType(cylinder.getGasCylinderTypeId());
            runtimeState.scenario.write("Asserting Cylinder Type is: " + expected);
            actual = types.get(index);
            assertEquals("Expected: " + expected + ", Actual: " + actual, expected, actual);

            Double expectedGasAvailable = cylinder.getInitialQuantity();
            runtimeState.scenario.write("Asserting Gas Available is: " + expectedGasAvailable);
            Double actualGasAvailable = Double.valueOf(available.get(index));
            assertTrue("Expected: " + expectedGasAvailable + ", Actual: " + actualGasAvailable, expectedGasAvailable.compareTo(actualGasAvailable) == 0);

            Double expectedGasInstalled = cylinder.getBottleQuantity();
            runtimeState.scenario.write("Asserting Gas Installed is: " + expectedGasInstalled);
            Double actualQty = Double.valueOf(installed.get(index));
            assertTrue("Expected: " + expectedGasInstalled + ", Actual: " + actualQty, expectedGasInstalled.compareTo(actualQty) == 0);

            Double expectedSurplus = expectedGasAvailable > expectedGasInstalled ? expectedGasAvailable - expectedGasInstalled : 0;
            runtimeState.scenario.write("Asserting Surplus is: " + expectedSurplus);
            Double actualSurplus = Double.valueOf(surplus.get(index));
            assertTrue("Expected: " + expectedSurplus + ", Actual: " + actualSurplus, expectedSurplus.compareTo(actualSurplus) == 0);

            if (cylinder.getGasSurplusDestinationId() != null) {
                expected = dbHelperGas.getGasSurplusDestination(cylinder.getGasSurplusDestinationId());
                runtimeState.scenario.write("Asserting Destination is: " + expected);
                actual = destinations.get(index);
                assertEquals("Expected: " + expected + ", Actual: " + actual, expected, actual);
            }

            if (cylinder.getGasSurplusTypeId() != null) {
                expected = dbHelperGas.getGasSurplusType(cylinder.getGasSurplusTypeId());
                runtimeState.scenario.write("Asserting Source Type is: " + expected);
                actual = surplusTypes.get(index);
                assertEquals("Expected: " + expected + ", Actual: " + actual, expected, actual);
            }
            index++;
        }
    }

    public void assertLeakSiteInformationModal(List<SiteVisitGasLeakSiteCheck> gasLeakChecks) {
        QuestionHelper questionHelper = runtimeState.adminLeakSiteInformationModal.getQuestionHelper();
        String primaryComponent = questionHelper.getAnswer("Primary Component");
        String subComponent = questionHelper.getAnswer("Sub-Component");
        String primaryComponentsInfo = questionHelper.getAnswer("Primary Component Information");
        String leakSiteStatus = questionHelper.getAnswer("Leak Site Status");
        String initialTest = questionHelper.getAnswer("Initial Verification Test");
        String followUpTest = questionHelper.getAnswer("Follow Up Verification Test");

        SiteVisitGasLeakSiteCheck check = gasLeakChecks.get(0);

        String expected = dbHelperGas.getGasLeakLocation(check.getGasLeakLocationId());
        runtimeState.scenario.write("Asserting Primary Component is: " + expected);
        assertEquals(expected, primaryComponent);

        expected = dbHelperGas.getGasLeakSubLocation(check.getGasLeakSubLocationId());
        runtimeState.scenario.write("Asserting Sub-Component is: " + expected);
        assertEquals(expected, subComponent);

        expected = check.getPrimaryComponentInformation();
        runtimeState.scenario.write("Asserting Primary Component Information is: " + expected);
        assertEquals(expected, primaryComponentsInfo);

        expected = dbHelperGas.getGasLeakSiteStatus(check.getGasLeakSiteStatusId());
        runtimeState.scenario.write("Asserting Leak Site Status is: " + expected);
        assertEquals(expected, leakSiteStatus);

        expected = dbHelperGas.getGasLeakInitialTest(check.getGasLeakInitialTestId());
        runtimeState.scenario.write("Asserting Initial Test is: " + expected);
        assertEquals(expected, initialTest);

        expected = dbHelperGas.getGasLeakFollowUpTest(check.getGasLeakFollowUpTestId());
        runtimeState.scenario.write("Asserting Follow Up Test is: " + expected);
        assertEquals(expected, followUpTest);
    }

    public void assertSiteVisitGasLeakSiteCheck() {

        if (testData.get("siteVisit") == null) {
            testData.put("siteVisit", siteVisitsDao.getFirstSiteVisitForJobReference(testData.getInt("jobReference")));
        }
        SiteVisits siteVisit = new SiteVisits();
        siteVisit = (SiteVisits) testData.get("siteVisit");

        List<SiteVisitGasLeakSiteCheck> gasLeakChecks = siteVisitGasLeakSiteCheckDao.getForSiteVisitId(siteVisit.getId());


        if (runtimeState.adminLeakSiteInformationModal != null) {
            assertLeakSiteInformationModal(gasLeakChecks);
        } else {

            List<String> primaryComponents;
            List<String> subComponents;
            List<String> primaryComponentsInfo;
            List<String> leakSiteStatuses;
            List<String> initialTests;
            List<String> followUpTests;
            if (runtimeState.adminRefrigerantGasUsageEditPageV2 != null) {
                primaryComponents = runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponents();
                subComponents = runtimeState.adminRefrigerantGasUsageEditPageV2.getSubComponents();
                primaryComponentsInfo = runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponentsInfo();
                leakSiteStatuses = runtimeState.adminRefrigerantGasUsageEditPageV2.getLeakSiteStatuses();
                initialTests = runtimeState.adminRefrigerantGasUsageEditPageV2.getInitialTests();
                followUpTests = runtimeState.adminRefrigerantGasUsageEditPageV2.getFollowUpTests();

            } else {
                primaryComponents = runtimeState.updateJobPage.getPrimaryComponents();
                subComponents = runtimeState.updateJobPage.getSubComponents();
                primaryComponentsInfo = runtimeState.updateJobPage.getPrimaryComponentsInfo();
                leakSiteStatuses = runtimeState.updateJobPage.getLeakSiteStatuses();
                initialTests = runtimeState.updateJobPage.getInitialTests();
                followUpTests = runtimeState.updateJobPage.getFollowUpTests();
            }

            for (int index = 0; index < gasLeakChecks.size(); index++) {
                SiteVisitGasLeakSiteCheck check = gasLeakChecks.get(index);

                runtimeState.scenario.write("-------------------------------------");
                runtimeState.scenario.write(String.format("Asserting Leak Site Check #%d", index + 1));

                String expected = normalize(dbHelperGas.getGasLeakLocation(check.getGasLeakLocationId()));
                runtimeState.scenario.write("Asserting Primary Component is: " + expected);
                assertEquals(expected, primaryComponents.get(index));

                expected = dbHelperGas.getGasLeakSubLocation(check.getGasLeakSubLocationId());
                runtimeState.scenario.write("Asserting Sub-Component is: " + expected);
                assertEquals(expected, subComponents.get(index));

                expected = normalize(check.getPrimaryComponentInformation());
                runtimeState.scenario.write("Asserting Primary Component Information is: " + expected);
                assertEquals(expected, primaryComponentsInfo.get(index));

                expected = dbHelperGas.getGasLeakSiteStatus(check.getGasLeakSiteStatusId());
                runtimeState.scenario.write("Asserting Leak Site Status is: " + expected);
                assertEquals(expected, leakSiteStatuses.get(index));

                expected = dbHelperGas.getGasLeakInitialTest(check.getGasLeakInitialTestId());
                runtimeState.scenario.write("Asserting Initial Test is: " + expected);
                assertEquals(expected, initialTests.get(index));

                expected = dbHelperGas.getGasLeakFollowUpTest(check.getGasLeakFollowUpTestId());
                runtimeState.scenario.write("Asserting Follow Up Test is: " + expected);
                assertEquals(expected, followUpTests.get(index));
            }
        }
    }

    public void jobDetailsDisplayedForUkRegulations() {
        String expectedSiteName = dbHelperSites.getSiteNameForJobRef(testData.getInt("jobReference"));
        runtimeState.scenario.write("Asserting Site is: " + expectedSiteName);
        String actualSiteName = runtimeState.adminRefrigerantGasUsageEditPage.getSite();
        assertEquals("Expected: " + expectedSiteName + "\nActual: " + actualSiteName, expectedSiteName, actualSiteName);

        String expectedResourceName = dbHelperResources.getResourceNameForResourceAssignmentId(testData.getInt("resourceAssignmentId"));
        expectedResourceName = StringUtils.normalizeSpace(expectedResourceName);
        runtimeState.scenario.write("Asserting Resource is: " + expectedResourceName);
        String actualResourceName = runtimeState.adminRefrigerantGasUsageEditPage.getResource();
        assertEquals("Expected: " + expectedResourceName + "\nActual: " + actualResourceName, expectedResourceName, actualResourceName);

        String expectedWorkEndTime = testData.getString("workEndTime");
        runtimeState.scenario.write("Asserting Date completed is: " + expectedWorkEndTime);
        String actualWorkEndTime = runtimeState.adminRefrigerantGasUsageEditPage.getDateCompleted();
        assertTrue("Expected: " + expectedWorkEndTime + "\nActual: " + actualWorkEndTime, actualWorkEndTime.contains(expectedWorkEndTime));

        float expectedTotalGasUsed = testData.getFloat("totalGasUsed");
        runtimeState.scenario.write("Asserting Total gas used is: " + expectedTotalGasUsed);
        float actualTotalGasUsed = Float.valueOf(runtimeState.adminRefrigerantGasUsageEditPage.getTotalGasUsed());
        assertTrue("Expected: " + expectedTotalGasUsed + "\nActual: " + actualTotalGasUsed, expectedTotalGasUsed == actualTotalGasUsed);

        float expectedRefrigerantCharge = testData.getFloat("maxGasCharge");
        if (expectedRefrigerantCharge > 0) {
            runtimeState.scenario.write("Asserting Total gas refrigerant charge is: " + expectedRefrigerantCharge);
            float actualRefrigerantCharge = Float.valueOf(runtimeState.adminRefrigerantGasUsageEditPage.getRefrigerantCharge());
            assertTrue("Expected: " + expectedRefrigerantCharge + "\nActual: " + actualRefrigerantCharge, expectedRefrigerantCharge == actualRefrigerantCharge);
        }
    }

    public void jobDetailsDisplayedForUsRegulations(){
        String expectedSiteName = dbHelperSites.getSiteNameForJobRef(testData.getInt("jobReference"));
        runtimeState.scenario.write("Asserting Site is: " + expectedSiteName);
        String actualSiteName = runtimeState.adminRefrigerantGasUsageEditPageV2.getSite();
        assertEquals("Expected: " + expectedSiteName + "\nActual: " + actualSiteName, expectedSiteName, actualSiteName);

        String expectedResourceName = dbHelperResources.getResourceNameForResourceAssignmentId(testData.getInt("resourceAssignmentId"));
        expectedResourceName = StringUtils.normalizeSpace(expectedResourceName);
        runtimeState.scenario.write("Asserting Resource is: " + expectedResourceName);
        String actualResourceName = runtimeState.adminRefrigerantGasUsageEditPageV2.getResource();
        assertEquals("Expected: " + expectedResourceName + "\nActual: " + actualResourceName, expectedResourceName, actualResourceName);

        String expectedWorkEndTime = testData.getString("workEndTime");
        runtimeState.scenario.write("Asserting Date completed is: " + expectedWorkEndTime);
        String actualWorkEndTime = runtimeState.adminRefrigerantGasUsageEditPageV2.getDateCompleted();
        assertTrue("Expected: " + expectedWorkEndTime + "\nActual: " + actualWorkEndTime, actualWorkEndTime.contains(expectedWorkEndTime));

        float expectedTotalGasUsed = testData.getFloat("totalGasUsed");
        runtimeState.scenario.write("Asserting Total gas used is: " + expectedTotalGasUsed);
        String str = setWeightLabel(" %s");
        float actualTotalGasUsed = Float.valueOf(runtimeState.adminRefrigerantGasUsageEditPageV2.getTotalGasUsed().replace(str, ""));
        assertTrue("Expected: " + expectedTotalGasUsed + "\nActual: " + actualTotalGasUsed, expectedTotalGasUsed == actualTotalGasUsed);
    }

    public void assertUkRefrigerantGasQuestions() {
        if (runtimeState.questionHelper == null) {
            runtimeState.questionHelper = runtimeState.adminRefrigerantGasUsageEditPage.getQuestionHelper();
        }

        SiteVisits siteVisit = new SiteVisits();
        siteVisit = (SiteVisits) testData.get("siteVisit");

        if (siteVisit.getGasTypeId() != null) {
            String expectedGasType = dbHelperGas.getGasType(siteVisit.getGasTypeId());
            assertGasAnswers("Gas Type", expectedGasType);
        }

        if (siteVisit.getGasLeakLocationId() != null) {
            String expectedGasLeakLocation = dbHelperGas.getGasLeakLocation(siteVisit.getGasLeakLocationId());
            assertGasAnswers("Leak Location", expectedGasLeakLocation);
        }

        if (siteVisit.getGasLeakageCodeId() != null) {
            String expectedGasLeakageCode = dbHelperGas.getGasLeakageCode(siteVisit.getGasLeakageCodeId());
            assertGasAnswers("Leakage Code", expectedGasLeakageCode);
        }

        if (siteVisit.getGasLeakageCheckMethodId() != null) {
            String expectedGasLeakageCheckMethod = dbHelperGas.getGasLeakageCheckMethod(siteVisit.getGasLeakageCheckMethodId());
            assertGasAnswers("Leak check method", expectedGasLeakageCheckMethod);
        }

        if (siteVisit.getGasActionReasonId() != null) {
            String expectedReason = dbHelper.getReason(siteVisit.getGasActionReasonId());
            assertGasAnswers("Action", expectedReason);
        }

        if (siteVisit.getGasFaultCodeReasonId() != null) {
            String expectedReason = dbHelper.getReason(siteVisit.getGasFaultCodeReasonId());
            assertGasAnswers("Fault Code", expectedReason);
        }

        List<SiteVisitsGasUsage> siteVisitsGasUsages = siteVisitsGasUsageDao.getForSiteVisit(siteVisit.getId());
        List<String> bottleNumbers = runtimeState.adminRefrigerantGasUsageEditPage.getBottleNumbers();
        List<String> amountsUsed = runtimeState.adminRefrigerantGasUsageEditPage.getAmountsUsed();
        for (SiteVisitsGasUsage siteVisitsGasUsage : siteVisitsGasUsages) {
            String num = siteVisitsGasUsage.getBottleNumber();
            float qty = siteVisitsGasUsage.getBottleQuantity();
            runtimeState.scenario.write("Asserting bottle number is: " + num);
            runtimeState.scenario.write("Asserting amount used is: " + qty);

            assertTrue("Expected bottle number not listed: " + num, bottleNumbers.contains(num));

            boolean found = false;
            for (String amountUsed : amountsUsed) {
                if (Float.valueOf(amountUsed) == qty) {
                    found = true;
                    break;
                }
            }
            assertTrue("Expected bottle amount not listed: " + qty, found);
        }
    }

    private void assertGasAnswers(String label, String expected) {
        String actual = runtimeState.questionHelper.getAnswer(label);
        runtimeState.scenario.write("Asserting " + label + " is " + expected);
        assertEquals("Unexpected " + label, normalize(expected), actual);
    }

    public String getRandomCylinderType() {
        Map<String, Object> questions = testData.getMap("questions");
        String gasType = questions.get("Refrigerant Type Used") == null || questions.get("Refrigerant Type Used").equals("Incorrect refrigerant type displayed") ? (String) questions.get("New Refrigerant Type Used") : (String) questions.get("Refrigerant Type Used");
        List<String> configuredCylinderTypes = dbHelperGas.getConfiguredCylinderTypes(gasType);
        return configuredCylinderTypes.get(RandomUtils.nextInt(0, configuredCylinderTypes.size()-1));
    }

    private String getGasTypeName() {
        Map<String, Object> map = testData.getMap("questions");
        if (map.get("New Refrigerant Type Used") != null && !((String) map.get("New Refrigerant Type Used")).isEmpty() ) {
            return (String) map.get("New Refrigerant Type Used");
        } else {
            return (String) map.get("Refrigerant Type Used");
        }
    }

    public void updateCylinder(QuestionHelper questionHelper) throws ParseException {
        List<String> questions = questionHelper.getAllQuestions();

        testData.addToMap("Refrigerant Source", "Full or Partial Cylinder", "Full"); // defaulted to Full

        String labelGasInCylinder = setWeightLabel("Gas in Cylinder (%s)");
        String labelCylinderCapacity = setWeightLabel("Cylinder Capacity (%s)");
        String labelGasInstalled = setWeightLabel("Gas Installed (%s)");

        String answer;
        for (int index = 0; index < questions.size(); index++) {
            String question = questions.get(index);

            if ("Fully Used".equals(question)) {
                updateRefrigerantInstallationDetails();
                questions = questionHelper.getAllQuestions();
                index = questions.indexOf(labelGasInstalled) + 1;

            } else {
                if ( !questionHelper.isAnswerReadOnly(question) ) {
                    if ("Refrigerant Source".equals(question)) {
                        String gasTypeName = getGasTypeName();
                        SiteVisits siteVisit = new SiteVisits();
                        siteVisit = (SiteVisits) testData.get("siteVisit");
                        Integer resourceAssignmentId = siteVisit.getResourceAssignmentId();
                        List<String> configuredRefrigerantSources = dbHelperGas.getConfiguredRefrigerantSources(gasTypeName, resourceAssignmentId);
                        if (configuredRefrigerantSources == null || configuredRefrigerantSources.isEmpty()) throw new PendingException("No Refrigerant Sources configured for: " + gasTypeName);
                        answer = configuredRefrigerantSources.get(RandomUtils.nextInt(0, configuredRefrigerantSources.size() - 1));
                        questionHelper.updateAnswer(question, answer);
                        testData.put("refrigerantSourceID", answer);

                    } else if ("Cylinder Serial No".equals(question)) {
                        answer = DataGenerator.generateRandomWord();
                        questionHelper.updateAnswer(question, answer);

                    } else if ("Type of Cylinder".equals(question)) {
                        answer = getRandomCylinderType();
                        questionHelper.updateAnswer(question, answer);

                    } else if (labelCylinderCapacity.equals(question)) {
                        List<String> capacities = questionHelper.getDropdownOptions(labelCylinderCapacity);
                        answer = capacities.get(capacities.size() - 1);
                        questionHelper.updateAnswer(question, answer);

                    } else if (labelGasInCylinder.equals(question)) {
                        Double maxCapacity = Double.valueOf(questionHelper.getAnswer(labelCylinderCapacity));
                        testData.put("maxCapacity", maxCapacity);
                        answer = trimZeros(String.valueOf(maxCapacity)).equals(String.valueOf(1)) ? trimZeros(String.valueOf(maxCapacity)) : trimZeros(String.valueOf(maxCapacity - 1));
                        questionHelper.enterNumeric(question, answer);

                    } else if ("Returned To".equals(question)) {
                        answer = DataGenerator.generateRandomSentence();
                        questionHelper.updateAnswer("Returned To", answer);

                    } else {
                        answer = questionHelper.updateAnswer(question);
                    }
                } else {
                    answer = questionHelper.getAnswer(question);
                }

                runtimeState.scenario.write(question + " : " + answer);
                testData.addToMap("Refrigerant Source", question, answer);

                questions = questionHelper.getAllQuestions();
            }
        }
    }

    public void captureQuestion(QuestionHelper questionHelper, String question) {
        if (questionHelper.isQuestionVisible(question)) {
            String answer = questionHelper.getAnswer(question);
            testData.addToMap("questions", question, answer);
        }
    }

    public void addCylinder() throws ParseException {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        captureQuestion(questionHelper, "Refrigerant Type Used");
        captureQuestion(questionHelper, "New Refrigerant Type Used");

        runtimeState.adminRefrigerantSourceModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addCylinder();
        runtimeState.scenario.write("*** Adding cylinder ");
        updateCylinder(runtimeState.adminRefrigerantSourceModal.getQuestionHelper());
        outputHelper.takeScreenshots();
        runtimeState.adminRefrigerantSourceModal.addSource();
    }

    public void updateRefrigerantInstallationDetails() {
        Double gasAvailable, surplus;
        Double gasInstalled = null;

        if (runtimeState.adminRefrigerantSourceModal == null ) runtimeState.adminRefrigerantSourceModal = new AdminRefrigerantSourceModal(getWebDriver()).get();

        if (RandomUtils.nextBoolean() || testData.getBoolean("fullyUsed")) {
            runtimeState.adminRefrigerantSourceModal.setFullyUsed(true);
            runtimeState.scenario.write("Fully Used : Full");

            gasAvailable = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasAvailable());
            gasInstalled = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasAvailable());
            surplus = 0.0;

        } else {
            runtimeState.adminRefrigerantSourceModal.setFullyUsed(false);
            runtimeState.scenario.write("Fully Used : Partial");

            gasAvailable = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasAvailable());
            gasInstalled = gasAvailable == 1 ? 1 : gasAvailable - 1;

            runtimeState.adminRefrigerantSourceModal.enterGasInstalled(gasInstalled);
            surplus = gasAvailable - gasInstalled;
            surplus = surplus > 0 ? surplus : 0.0;
        }
        runtimeState.scenario.write("Gas Available : " + gasAvailable);
        testData.addToMap("Refrigerant Source", setWeightLabel("Gas Available (%s)"), gasAvailable);

        runtimeState.scenario.write("Gas Installed : " + gasInstalled);
        testData.addToMap("Refrigerant Source", setWeightLabel("Gas Installed (%s)"), gasInstalled);

        runtimeState.scenario.write("Surplus : " + surplus);
        testData.addToMap("Refrigerant Source", "Surplus", surplus);
    }

    public void assertAnswer(String question, String expected, String actual) {
        runtimeState.scenario.write("Asserting " + question + " = " + expected);
        assertTrue("Expected " + question + " = " + expected, actual.equals(expected));
    }

    public void assertAnswerDouble(String question, String expected, String actual) {
        runtimeState.scenario.write("Asserting " + question + " = " + expected);
        Double expectedVal = Double.valueOf(expected);
        Double actualVal = Double.valueOf(actual);
        assertTrue("Expected " + question + " = " + expected, expectedVal.compareTo(actualVal) == 0);
    }

    public void assertAnswer(String question, String expected) {
        String actual = runtimeState.questionHelper.getAnswer(question);

        if (actual == null && question.equals("Please provide appliance details")) {
            actual = runtimeState.questionHelper.getAnswer("No appliances found, please provide appliance details");
        }

        runtimeState.scenario.write("Asserting " + question + " = " + expected);
        assertTrue("Expected " + question + " = " + expected, actual.equals(expected));
    }

    public void updateUkFgasQuestions(List<String> answers) throws Exception {

        // may need to be moved to CommonSteps in future if/when required

        outputHelper.takeScreenshots();
        String answer = "";
        for (String question : answers) {

            switch (question) {
            case "Gas Type":

                // note : for UK regulations, refrigerant charge is held against the asset.
                // Since the asset cannot be changed on the Admin screen we would be able to select any gas type!

                String origGasType = runtimeState.adminRefrigerantGasUsageEditPage.getGasType();
                runtimeState.scenario.write("Original question / answer : " + question + " = " + origGasType);
                testData.put("origGasType", origGasType);

                answer = runtimeState.adminRefrigerantGasUsageEditPage.selectRandomGasType();
                testData.put("gasType", answer);
                break;

            case "Leak Location":
                String origGasLeakLocation = runtimeState.adminRefrigerantGasUsageEditPage.getLeakLocation();
                runtimeState.scenario.write("Original question / answer : " + question + " = " + origGasLeakLocation);
                testData.put("origGasLeakLocation", origGasLeakLocation);

                answer = runtimeState.adminRefrigerantGasUsageEditPage.selectRandomLeakLocation();
                testData.put("gasLeakLocation", answer);
                break;

            case "Leakage Code":
                String origGasLeakageCode = runtimeState.adminRefrigerantGasUsageEditPage.getLeakageCode();
                runtimeState.scenario.write("Original question / answer : " + question + " = " + origGasLeakageCode);
                testData.put("origGasLeakageCode", origGasLeakageCode);

                answer = runtimeState.adminRefrigerantGasUsageEditPage.selectRandomLeakageCode();
                testData.put("gasLeakageCode", answer);
                break;

            case "Leak check method":
                String origGasLeakageCheckMethod = runtimeState.adminRefrigerantGasUsageEditPage.getLeakCheckMethod();
                runtimeState.scenario.write("Original question / answer : " + question + " = " + origGasLeakageCheckMethod);
                testData.put("origGasLeakageCheckMethod", origGasLeakageCheckMethod);

                answer = runtimeState.adminRefrigerantGasUsageEditPage.selectRandomLeakCheckMethod();
                testData.put("gasLeakageCheckMethod", answer);
                break;

            case "Action":
                String origGasAction = runtimeState.adminRefrigerantGasUsageEditPage.getAction();
                runtimeState.scenario.write("Original question / answer : " + question + " = " + origGasAction);
                testData.put("origGasAction", origGasAction);

                answer = runtimeState.adminRefrigerantGasUsageEditPage.selectRandomAction();
                testData.put("gasAction", answer);
                break;

            case "Fault Code":
                String origGasFaultCode = runtimeState.adminRefrigerantGasUsageEditPage.getFaultCode();
                runtimeState.scenario.write("Original question / answer : " + question + " = " + origGasFaultCode);
                testData.put("origGasFaultCode", origGasFaultCode);

                answer = runtimeState.adminRefrigerantGasUsageEditPage.selectRandomFaultCode();
                testData.put("gasFaultCode", answer);
                break;

            case "Bottle/Serial Number":
                String bottleNumber = runtimeState.adminRefrigerantGasUsageEditPage.getBottleNumbers().get(0);
                float qty = Float.valueOf(runtimeState.adminRefrigerantGasUsageEditPage.getAmountsUsed().get(0).replaceAll(",", ""));
                runtimeState.scenario.write("Original question / answer : " + question + " = bottleNumber (" + bottleNumber + ") bottleQuantity (" + String.valueOf(qty) + ")");
                testData.put("origGasBottleNumber", bottleNumber);

                runtimeState.adminRefrigerantGasUsageEditPage.editBottle();

                bottleNumber = RandomStringUtils.randomAlphanumeric(6, 9);
                runtimeState.adminRefrigerantGasUsageEditPage.enterBottleNumber(bottleNumber);

                runtimeState.adminRefrigerantGasUsageEditPage.takeScreenshotOfPopup(outputHelper);
                runtimeState.adminRefrigerantGasUsageEditPage.update();

                qty = Float.valueOf(runtimeState.adminRefrigerantGasUsageEditPage.getAmountsUsed().get(0).replaceAll(",", ""));
                testData.addToMap("gasAmountUsed", bottleNumber, qty);

                String newGasBottleNumber = runtimeState.adminRefrigerantGasUsageEditPage.getBottleNumbers().get(0);
                testData.put("gasBottleNumber", newGasBottleNumber);

                answer = "bottleNumber (" + bottleNumber + ") bottleQuantity (" + String.valueOf(qty) + ")";

                break;

            case "Amount Used":
                String amountUsed = runtimeState.adminRefrigerantGasUsageEditPage.getAmountsUsed().get(0);
                bottleNumber = runtimeState.adminRefrigerantGasUsageEditPage.getBottleNumbers().get(0);
                runtimeState.scenario.write("Original question / answer : " + question + " = bottleNumber (" + bottleNumber + ") bottleQuantity (" + amountUsed + ")");
                testData.put("origGasBottleQty", amountUsed);

                runtimeState.adminRefrigerantGasUsageEditPage.editBottle();

                qty = RandomUtils.nextFloat(1, 10);
                runtimeState.adminRefrigerantGasUsageEditPage.enterAmountUsed(String.valueOf(qty));

                runtimeState.adminRefrigerantGasUsageEditPage.takeScreenshotOfPopup(outputHelper);
                runtimeState.adminRefrigerantGasUsageEditPage.update();

                testData.addToMap("gasAmountUsed", bottleNumber, qty);

                amountUsed = runtimeState.adminRefrigerantGasUsageEditPage.getAmountsUsed().get(0);
                testData.put("gasBottleQty", amountUsed);

                answer = "bottleNumber (" + bottleNumber + ") bottleQuantity (" + amountUsed + ")";

                break;

            default:
                throw new Exception("Unknown question: " + question);
            }

            runtimeState.scenario.write("Updated question / answer : " + question + " = " + answer);
        }
    }

    public void updateAllFgasUkQuestions() throws Throwable {

        outputHelper.takeScreenshots();

        runtimeState.questionHelper = runtimeState.adminRefrigerantGasUsageEditPage.getQuestionHelper();
        List<String> questions = runtimeState.questionHelper.getAllQuestions();

        // get original questions & answers
        for (String question : questions) {
            String answer = runtimeState.questionHelper.getAnswer(question);
            runtimeState.scenario.write("Original question / answer : " + question + " = " + answer);
            testData.addToMap("originalQuestions", question, answer);
        }

        // update questions & answers
        int index = 0;
        while (questions.size() > index) {
            String question = questions.get(index);
            String answer = runtimeState.questionHelper.selectRandomAnwser(question);
            testData.addToMap("questions", question, answer);
            runtimeState.scenario.write("Updated question / answer : " + question + " = " + answer);

            index++;
            // the subsequent questions may change dependent on previous choice - so get them again!
            questions = runtimeState.questionHelper.getAllQuestions();
        }

        updateUkFgasQuestions(Arrays.asList("Bottle/Serial Number", "Amount Used"));
    }

    public void enterLeakDetails() throws Exception {

        if (runtimeState.adminLeakSiteInformationModal == null) runtimeState.adminLeakSiteInformationModal = new AdminLeakSiteInformationModal(getWebDriver()).get();

        QuestionHelper questionHelper = runtimeState.adminLeakSiteInformationModal.getQuestionHelper();
        List<String> questions = questionHelper.getAllQuestions();

        // update questions & answers
        int index = 0;
        while (questions.size() > index) {
            String question = questions.get(index);
            String answer;
            if (question.contains("Primary Component Information")) {
                question = question.substring(0, question.indexOf("\n"));
                answer = DataGenerator.generateRandomSentence();
                questionHelper.updateAnswer(question, answer);

            } else if (question.contains("Leak Site Status") && testData.getString("leakSiteStatusAnswer") != null) {
                answer = testData.getString("leakSiteStatusAnswer");
                questionHelper.updateAnswer(question, answer);

            } else if (question.contains("Follow Up Verification Test") && testData.getString("followUpVerificationTestAnswer") != null) {
                answer = testData.getString("followUpVerificationTestAnswer");
                questionHelper.updateAnswer(question, answer);

            } else {
                answer = questionHelper.updateAnswer(question);
            }

            // this bit gets messy - to support the original Portal FGAS code
            String destColumnId = "";
            switch (question) {
            case "Primary Component" : destColumnId = "gasLeakLocationId"; break;
            case "Primary Component Information" : destColumnId = "primaryComponentInformation"; break;
            case "Sub-Component" : destColumnId = "gasLeakSubLocationId"; break;
            case "Leak Site Status" : destColumnId = "gasLeakSiteStatusId"; break;
            case "Initial Verification Test" : destColumnId = "gasLeakInitialTestId"; break;
            case "Follow Up Verification Test" : destColumnId = "gasLeakFollowUpTestId"; break;
            }
            testData.put(destColumnId, answer);
            testData.addToMap("Leak Site Information", destColumnId, answer);

            testData.addToMap("questions", question, answer);
            runtimeState.scenario.write("Updated question / answer : " + question + " = " + answer);

            index++;
            // the subsequent questions may change dependent on previous choice - so get them again!
            questions = questionHelper.getAllQuestions();
        }

        runtimeState.adminLeakSiteInformationModal.takeScreenshot(outputHelper);
        runtimeState.adminLeakSiteInformationModal.add();
    }

    private void captureQuestion(String question, QuestionHelper questionHelper, String mapName) {
        String answer = questionHelper.getAnswer(question);
        runtimeState.scenario.write(mapName + " : " + question + " = " + answer);
        testData.addToMap(mapName, question, answer);
    }

    private void captureApplianceInformation(QuestionHelper questionHelper, String prefix) {
        List<String> sectionQuestions = FgasQuestions.getGasQuestionsForSection("FGAS Appliance");
        List<String> questionsDisplayed = questionHelper.getAllQuestions();

        for (String question : sectionQuestions) {
            if (questionsDisplayed.contains(question)) {
                captureQuestion(question, questionHelper, prefix + "questions");

            } else {
                runtimeState.scenario.write(prefix + "questions : "  + question + " = null");
                testData.addToMap(prefix + "questions", question, null);
            }
        }
    }

    public void captureRefrigerantDetails(QuestionHelper questionHelper, String prefix) {
        testData.put(prefix + "cylinderSerialNumbers", runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers());
        testData.put(prefix + "cylinderSources", runtimeState.adminRefrigerantGasUsageEditPageV2.getSources());
        testData.put(prefix + "cylinderFullPartial", runtimeState.adminRefrigerantGasUsageEditPageV2.getFullPartial());
        testData.put(prefix + "cylinderTypes", runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderTypes());
        testData.put(prefix + "cylinderGasAvailable", runtimeState.adminRefrigerantGasUsageEditPageV2.getGasAvailable());
        testData.put(prefix + "cylinderGasInstalled", runtimeState.adminRefrigerantGasUsageEditPageV2.getGasInstalled());
        testData.put(prefix + "cylinderSurplus", runtimeState.adminRefrigerantGasUsageEditPageV2.getSurplus());
        testData.put(prefix + "cylinderDestinations", runtimeState.adminRefrigerantGasUsageEditPageV2.getDestinations());
        testData.put(prefix + "cylinderSurplusTypes", runtimeState.adminRefrigerantGasUsageEditPageV2.getSurplusTypes());

        runtimeState.scenario.write(prefix + "questions : cylinderSerialNumbers : " + testData.getArray(prefix + "cylinderSerialNumbers").toString());
        runtimeState.scenario.write(prefix + "questions : cylinderSources : " + testData.getArray(prefix + "cylinderSources").toString());
        runtimeState.scenario.write(prefix + "questions : cylinderFullPartial : " + testData.getArray(prefix + "cylinderFullPartial").toString());
        runtimeState.scenario.write(prefix + "questions : cylinderTypes : " + testData.getArray(prefix + "cylinderTypes").toString());
        runtimeState.scenario.write(prefix + "questions : cylinderGasAvailable : " + testData.getArray(prefix + "cylinderGasAvailable").toString());
        runtimeState.scenario.write(prefix + "questions : cylinderGasInstalled : " + testData.getArray(prefix + "cylinderGasInstalled").toString());
        runtimeState.scenario.write(prefix + "questions : cylinderSurplus : " + testData.getArray(prefix + "cylinderSurplus").toString());
        runtimeState.scenario.write(prefix + "questions : cylinderDestinations : " + testData.getArray(prefix + "cylinderDestinations").toString());
        runtimeState.scenario.write(prefix + "questions : cylinderSurplusTypes : " + testData.getArray(prefix + "cylinderSurplusTypes").toString());

        captureQuestion("Please enter appliance maximum charge", questionHelper, prefix + "questions");
        captureQuestion("Please provide notes to explain reason for changing appliance maximum charge", questionHelper, prefix + "questions");
    }

    public void captureLeakInspectionsAndRepairs(QuestionHelper questionHelper, String prefix) {
        captureQuestion("Leak Check Status", questionHelper, prefix + "questions");
        captureQuestion("Leak Check Method", questionHelper, prefix + "questions");
        captureQuestion("Leak Check Result Type", questionHelper, prefix + "questions");

        testData.put(prefix + "primaryComponents", runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponents());
        testData.put(prefix + "primaryComponentsInfo", runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponentsInfo());
        testData.put(prefix + "subComponents", runtimeState.adminRefrigerantGasUsageEditPageV2.getSubComponents());
        testData.put(prefix + "leakSiteStatuses", runtimeState.adminRefrigerantGasUsageEditPageV2.getLeakSiteStatuses());
        testData.put(prefix + "initialTests", runtimeState.adminRefrigerantGasUsageEditPageV2.getInitialTests());
        testData.put(prefix + "followUpTests", runtimeState.adminRefrigerantGasUsageEditPageV2.getFollowUpTests());

        runtimeState.scenario.write(prefix + "questions : primaryComponents : " + testData.getArray(prefix + "primaryComponents").toString());
        runtimeState.scenario.write(prefix + "questions : primaryComponentsInfo : " + testData.getArray(prefix + "primaryComponentsInfo").toString());
        runtimeState.scenario.write(prefix + "questions : subComponents : " + testData.getArray(prefix + "subComponents").toString());
        runtimeState.scenario.write(prefix + "questions : leakSiteStatuses : " + testData.getArray(prefix + "leakSiteStatuses").toString());
        runtimeState.scenario.write(prefix + "questions : initialTests : " + testData.getArray(prefix + "initialTests").toString());
        runtimeState.scenario.write(prefix + "questions : followUpTests : " + testData.getArray(prefix + "followUpTests").toString());
    }

    public void captureAllFgasUsQuestions(String prefix) throws Throwable {
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        captureApplianceInformation(questionHelper, prefix);
        captureRefrigerantDetails(questionHelper, prefix);
        captureLeakInspectionsAndRepairs(questionHelper, prefix);
    }

    public void answerFgasApplianceQuestions(QuestionHelper questionHelper){
        List<String> questions = questionHelper.getAllQuestions();

        // update questions & answers
        int index = 0;
        while (questions.size() > index) {
            String question = questions.get(index);
            String answer = "";

            if ("Quantity of Balls Floating".equals(question)) {
                answer = String.valueOf(RandomUtils.nextInt(1,4));
                questionHelper.updateAnswer(question, answer);

            } else if (question.contains("Refrigerant Type Used")) {
                List<String> options = questionHelper.getDropdownOptions(question);
                if (options.get(0).equals("Incorrect refrigerant type displayed")) {
                    answer = "Incorrect refrigerant type displayed";

                } else {
                    List<String> gasTypes = dbHelperGas.getActiveGasTypes();
                    answer = gasTypes.get(RandomUtils.nextInt(0, gasTypes.size()-1));
                }
                questionHelper.updateAnswer(question, answer);

            } else {
                answer = questionHelper.updateAnswer(question);
            }
            runtimeState.scenario.write("Updated question / answer : " + question + " = " + answer);
            testData.addToMap("questions", question, answer);
            testData.addToMap("FGAS Appliance", question, answer);

            index++;
            // the subsequent questions may change dependent on previous choice - so get them again!
            questions = runtimeState.questionHelper.getAllQuestions();
        }
    }

    public void updateApplianceInformationSection(QuestionHelper questionHelper) {
        List<String> sectionQuestions = FgasQuestions.getGasQuestionsForSection("FGAS Appliance");
        List<String> questionsDisplayed = questionHelper.getAllQuestions();

        sectionQuestions.remove("Appliance Type"); // remove read only question

        String answer;
        for (String question : sectionQuestions) {
            if (questionsDisplayed.contains(question)) {

                if ("Quantity of Balls Floating".equals(question)) {
                    answer = String.valueOf(RandomUtils.nextInt(1,4));
                    questionHelper.updateAnswer(question, answer);

                }else if (question.contains("Refrigerant Type Used")) {
                    // updating Refrigerant Type will delete all cylinders - so only randomly update
                    if (new Random().nextBoolean() || question.equals("New Refrigerant Type Used") || questionHelper.getAnswer(question).equals("Please select a gas type")) {
                        do {
                            answer = questionHelper.updateAnswer(question);
                        } while ("Other".equals(answer));
                    } else {
                        answer = questionHelper.getAnswer(question);
                    }
                } else {
                    answer = questionHelper.updateAnswer(question);
                }
                runtimeState.scenario.write("Updated question / answer : " + question + " = " + answer);
                testData.addToMap("questions", question, answer);

                if (runtimeState.adminRefrigerantGasUsageEditPageV2.isAlertVisible()) {     // alert displayed when changing gas type
                    runtimeState.scenario.write("*** Gas Type changed - deleting all cylinders");
                    testData.put("gasTypeChanged", true);
                    runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
                    runtimeState.popupAlert.ok();
                }
            } else {
                runtimeState.scenario.write("Updated question / answer : " + question + " = null");
                testData.addToMap("questions", question, "");
            }

            questionsDisplayed = questionHelper.getAllQuestions();
        }
    }

    public void updateRefrigerantDetails(QuestionHelper questionHelper) throws ParseException {
        if ("Yes".equals(questionHelper.getAnswer("Refrigerant Gas used during this visit"))) {
            testData.put("gasType", runtimeState.adminRefrigerantGasUsageEditPageV2.getGasTypeSelectedValue());

            List<String> serialNumbers = runtimeState.adminRefrigerantGasUsageEditPageV2.getCylinderSerialNumbers();

            if (serialNumbers.size() > 0) {
                // edit first cylinder
                String serialNumber = serialNumbers.get(0);
                runtimeState.scenario.write("*** Editing cylinder: " + serialNumber);
                runtimeState.adminRefrigerantSourceModal = runtimeState.adminRefrigerantGasUsageEditPageV2.editCylinder(serialNumber);
                QuestionHelper editCylinderQuestionHelper = runtimeState.adminRefrigerantSourceModal.getQuestionHelper();
                boolean fullyUsed = runtimeState.adminRefrigerantSourceModal.isFullyUsed();
                runtimeState.adminRefrigerantSourceModal.setFullyUsed(!fullyUsed);
                if (fullyUsed) {
                    Double gasAvailable = Double.valueOf(runtimeState.adminRefrigerantSourceModal.getGasAvailable());
                    Double gasInstalled = gasAvailable - 1;
                    runtimeState.adminRefrigerantSourceModal.enterGasInstalled(gasInstalled);

                    if (editCylinderQuestionHelper.isQuestionVisible("Destination")) {
                        editCylinderQuestionHelper.updateAnswer("Destination");
                        editCylinderQuestionHelper.updateAnswer("Surplus Type");
                    }
                    if (editCylinderQuestionHelper.isQuestionVisible("Returned To")) {
                        editCylinderQuestionHelper.updateAnswer("Returned To", RandomStringUtils.randomAlphabetic(20, 30));
                    }
                }
                runtimeState.adminRefrigerantSourceModal.save();
            }

            if (serialNumbers.size() > 1) {
                // delete last cylinder
                String serialNumber = serialNumbers.get(serialNumbers.size() - 1);
                runtimeState.scenario.write("*** Deleting cylinder: " + serialNumber);
                runtimeState.adminRefrigerantGasUsageEditPageV2.deleteCylinder(serialNumber);
                runtimeState.popupAlert = new PopupAlert(getWebDriver()).get();
                runtimeState.popupAlert.ok();
            }

            // add cylinder
            addCylinder();

            if (questionHelper.isQuestionVisible("Please enter appliance maximum charge")) {
                questionHelper.updateAnswer("Please enter appliance maximum charge");
            }
            if (questionHelper.isQuestionVisible("Please provide notes to explain reason for changing appliance maximum charge")) {
                questionHelper.updateAnswer("Please provide notes to explain reason for changing appliance maximum charge", DataGenerator.generateRandomSentence());
            }
        }
    }

    public void updateLeakSiteCheck() throws ParseException {
        QuestionHelper questionHelper = runtimeState.adminLeakSiteInformationModal.getQuestionHelper();

        if ( ! questionHelper.isAnswerReadOnly("Primary Component") ) {
            String answer = questionHelper.updateAnswer("Primary Component");
            runtimeState.scenario.write("Updating Primary Component to: " + answer);

            answer = DataGenerator.generateRandomSentence();
            runtimeState.scenario.write("Updating Primary Component Information to: " + answer);
            questionHelper.updateAnswer("Primary Component Information", answer);

            answer = questionHelper.updateAnswer("Sub-Component");
            runtimeState.scenario.write("Updating Sub-Component to: " + answer);
        }

        String status = dbHelperGas.getRandomLeakSiteStatus(false);
        runtimeState.scenario.write("Updating Leak Site Status to: " + status);
        questionHelper.updateAnswer("Leak Site Status", status);

        String answer = questionHelper.updateAnswer("Initial Verification Test");
        runtimeState.scenario.write("Updating Initial Verification Test to: " + answer);

        status = dbHelperGas.getRandomGasLeakFollowUpTest(false);
        runtimeState.scenario.write("Updating Follow Up Verification Test to: " + status);
        questionHelper.updateAnswer("Follow Up Verification Test", status);
    }

    public void updateLeakInspectionsAndRepairs(QuestionHelper questionHelper) throws Exception {
        String status = dbHelperGas.getRandomLeakCheckStatus(false);
        questionHelper.updateAnswer("Leak Check Status", status);

        questionHelper.updateAnswer("Leak Check Method");

        String type = dbHelperGas.getRandomLeakCheckResultType(false);
        questionHelper.updateAnswer("Leak Check Result Type", type);

        List<String> locations = runtimeState.adminRefrigerantGasUsageEditPageV2.getPrimaryComponents();

        if (locations.size() > 0) {
            // edit first Leak Site Check
            runtimeState.scenario.write("Editing Leak Site Check #1");
            if (runtimeState.adminRefrigerantGasUsageEditPageV2.isLeakSiteCheckEditDisplayed()) {
                runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.editLeakSiteCheck(1);
            }

            updateLeakSiteCheck();

            runtimeState.adminLeakSiteInformationModal.save();
        }

        if (locations.size() > 1) {
            // delete last Leak Site Check
            runtimeState.scenario.write("Deleting Leak Site Check #" + locations.size());
            runtimeState.popupAlert = runtimeState.adminRefrigerantGasUsageEditPageV2.deleteLastLeakSiteCheck();
            runtimeState.popupAlert.ok();
        }

        // add Leak Site Check
        runtimeState.scenario.write("Adding Leak Site Check");
        if (runtimeState.adminRefrigerantGasUsageEditPageV2.isButtonDisplayed("Add Additional Leak Site Details")) {
            runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addAdditionalLeakSiteDetails();
        } else {
            runtimeState.adminLeakSiteInformationModal = runtimeState.adminRefrigerantGasUsageEditPageV2.addLeakSiteDetails();
        }
        enterLeakDetails();
    }

    public void updateAllFgasUsQuestions() throws Throwable {
        outputHelper.takeScreenshots();
        QuestionHelper questionHelper = runtimeState.adminRefrigerantGasUsageEditPageV2.getQuestionHelper();
        updateApplianceInformationSection(questionHelper);
        updateRefrigerantDetails(questionHelper);
        updateLeakInspectionsAndRepairs(questionHelper);
    }

    private String updatedTo(String origVal, String newVal) {
        if (origVal.trim().isEmpty()) {
            return "added with value '" +  newVal +"'";
        } else {
            return "changed from '" + origVal + "' to '" + newVal +"'";
        }
    }

    private void assertAuditHistory(String description, String auditString) {
        auditString = auditString.replaceAll("\\s+", " ");       // normalise spaces
        runtimeState.scenario.write("Asserting Audit History contains: " + auditString);
        if ( !description.contains(auditString) ) {
            String auditStringWithSpaces = ".*" + auditString.replace("'", " ?'") + ".*";
            assertTrue("Expected: " + auditString, description.matches(auditStringWithSpaces));
            runtimeState.scenario.write("Warning: assertion passed, but audit history includes unnessessary spaces. Refer to low priority bug MCP-8819");
        }
    }

    public void assertAuditHistoryUkGas() throws Exception {
        runtimeState.adminRefrigerantGasUsageEditPage.expandAutitHistory();
        Grid grid = runtimeState.adminRefrigerantGasUsageEditPage.getAutitHistory();
        Row row = grid.getRows().get(0);
        String description = row.getCell("Description").getText();
        String auditString;

        if ( testData.getString("gasType") != null && !testData.getString("origGasType").equals(testData.getString("gasType")) ) {
            auditString = "Gas Type was " + updatedTo(testData.getString("origGasType"), testData.getString("gasType"));
            assertAuditHistory(description, auditString);
        }

        if ( testData.getString("gasLeakageCode") != null && !testData.getString("origGasLeakageCode").equals(testData.getString("gasLeakageCode")) ) {
            auditString = "Gas Leakage Code was " + updatedTo(testData.getString("origGasLeakageCode"), testData.getString("gasLeakageCode"));
            assertAuditHistory(description, auditString);
        }

        if ( testData.getString("gasLeakageCheckMethod") != null && !testData.getString("origGasLeakageCheckMethod").equals(testData.getString("gasLeakageCheckMethod")) ) {
            auditString = "Gas Leak Check Method was " + updatedTo(testData.getString("origGasLeakageCheckMethod"), testData.getString("gasLeakageCheckMethod"));
            assertAuditHistory(description, auditString);
        }

        if ( testData.getString("gasLeakLocation") != null && !testData.getString("origGasLeakLocation").equals(testData.getString("gasLeakLocation")) ) {
            auditString = "Gas Leak Location was " + updatedTo(testData.getString("origGasLeakLocation"), testData.getString("gasLeakLocation"));
            assertAuditHistory(description, auditString);
        }

        if ( testData.getString("gasAction") != null && !testData.getString("origGasAction").equals(testData.getString("gasAction")) ) {
            auditString = "Gas Action Reason was " + updatedTo(testData.getString("origGasAction"), testData.getString("gasAction"));
            assertAuditHistory(description, auditString);
        }

        if ( testData.getString("gasFaultCode") != null && !testData.getString("origGasFaultCode").equals(testData.getString("gasFaultCode"))) {
            auditString = "Gas Fault Code Reason was " + updatedTo(testData.getString("origGasFaultCode"), testData.getString("gasFaultCode"));
            assertAuditHistory(description, auditString);
        }

        if ( testData.getString("gasBottleNumber") != null && !testData.getString("origGasBottleNumber").equals(testData.getString("gasBottleNumber")) ) {
            auditString = "Gas Bottle Number was " + updatedTo(testData.getString("origGasBottleNumber"), testData.getString("gasBottleNumber"));
            assertAuditHistory(description, auditString);
        }

        if ( testData.getString("gasBottleQty") != null && !testData.getString("origGasBottleQty").equals(testData.getString("gasBottleQty")) ) {
            auditString = "Gas Bottle Quantity was changed from '" + testData.getString("origGasBottleQty") + "' to '" + testData.getString("gasBottleQty") + "'";
            runtimeState.scenario.write("Asserting Audit History contains: " + auditString);

            float qtyFrom = Float.valueOf(StringUtils.substringBetween(description, "Gas Bottle Quantity was changed from '", "' to"));
            float qtyTo = Float.valueOf(description.substring(description.lastIndexOf(' ') + 1).replaceAll("\\.$", "").replaceAll("'", ""));

            assertTrue("Expected Gas Bottle Quantity was changed from: " + testData.getFloat("origBottleQty"), qtyFrom == testData.getFloat("origGasBottleQty"));
            assertTrue("Expected Gas Bottle Quantity was changed to: " + testData.getFloat("bottleQty"), qtyTo == testData.getFloat("gasBottleQty"));
        }
    }

    private void assertChangesToApplianceInformationAndLeakChecks(String description) {
        runtimeState.scenario.write("CHANGES TO APPLIANCE INFORMATION AND LEAK CHECKS:");
        Map<String, Object> beforeMap = testData.getMap(BEFORE + "questions");
        Map<String, Object> afterMap = testData.getMap(AFTER + "questions");

        String auditString;
        for (String question : beforeMap.keySet()) {                // assert changed answers
            String before = (String) beforeMap.get(question);
            String after = (String) afterMap.get(question);
            before = before == null ? "null" : before;
            after = after == null ? "null" : after;

            if ( !before.equals("null") && !after.equals(before) ) {
                switch (question) {
                case "Appliance Identification":
                    before = "Appliance not on list".equals(before) ? "null" : before + " \\(.*?\\)";
                    after = "Appliance not on list".equals(after) ? "null" : after + " \\(.*\\)";
                    auditString = question + " was changed from '" + before + "' to '" + after + "'";
                    runtimeState.scenario.write("Asserting Audit History contains: " + auditString);
                    assertTrue("Expected: " + auditString, description.matches(".*" + auditString.replaceAll("\\s+", " ") + ".*"));
                    break;

                case "Please provide appliance details":
                    auditString = "Appliance Information was changed from '" + before + "' to '" + after +"'";
                    assertAuditHistory(description, auditString);
                    break;

                case "Has receiver level been recorded?":
                    before = "Yes".equals(before) ? "True" : "False";
                    after = "Yes".equals(after) ? "True" : "False";
                    auditString = "Receiver Level Recorded was changed from '" + before + "' to '" + after +"'";
                    assertAuditHistory(description, auditString);
                    break;

                case "Please provide notes to explain reason for changing appliance maximum charge":
                    auditString = "Reason For Changing Maximum Charge was changed from '" + before + "' to '" + after +"'";
                    assertAuditHistory(description, auditString);
                    break;

                case "Please enter appliance maximum charge":
                    auditString = "New Appliance Maximum Charge was changed from '" + before + "' to '" + after +"'";
                    assertAuditHistory(description, auditString);
                    break;

                case "Refrigerant Type Used":
                    after = "Incorrect refrigerant type displayed".equals(after) ? (String) afterMap.get("New Refrigerant Type Used") : after;
                    if (!after.equals(before)) {
                        auditString = "Refrigerant Type Used was changed from '" + before + "' to '" + after +"'";
                        assertAuditHistory(description, auditString);
                    }
                    break;

                default:
                    auditString = question + " was changed from '" + before + "' to '" + after +"'";
                    assertAuditHistory(description, auditString);
                }

            }
        }
    }

    private void assertAdditionsToApplianceInformationAndLeakChecks(String description) {
        // assert questions that previously were not answered
        Map<String, Object> beforeMap = testData.getMap(BEFORE + "questions");
        Map<String, Object> afterMap = testData.getMap(AFTER + "questions");

        String auditString;
        for (String question : afterMap.keySet()) {                 // assert added answers
            String before = (String) beforeMap.get(question);
            String after = (String) afterMap.get(question);

            if ( before == null && after != null ) {
                switch (question) {
                case "New Refrigerant Type Used":
                case "Please provide appliance details":
                    // skip - already tested as a changed answer
                    break;

                case "Please enter appliance maximum charge":
                    auditString = "New Appliance Maximum Charge was changed from 'null' to '" + after + "'";
                    assertAuditHistory(description, auditString);
                    break;

                case "Please provide notes to explain reason for changing appliance maximum charge":
                    auditString = "Reason For Changing Maximum Charge was changed from 'null' to '" + after +"'";
                    assertAuditHistory(description, auditString);
                    break;

                default:
                    auditString = question + " was changed from 'null' to '" + after +"'";
                    assertAuditHistory(description, auditString);
                }
            }
        }
    }

    private void changedAudit(String auditEntry, String title, String before, String after) {
        before = before == null || before.isEmpty() ? "null" : before;
        after = after == null || after.isEmpty() ? "null" : after;
        if ( ! before.equals(after) ) {
            assertAuditHistory(auditEntry, title + " was changed from '" + before + "' to '" + after + "'");
        }
    }

    private void changedNumericAudit(String auditEntry, String title, String before, String after) {
        before = before == null || before.isEmpty() ? "0" : before;
        after = after == null || after.isEmpty() ? "0" : after;
        if ( Float.valueOf(before).compareTo(Float.valueOf(after)) != 0 ) {
            assertAuditHistory(auditEntry, title + " was changed from '" + before + "' to '" + after +"'");
        }
    }

    private void assertChangesToCylinders(String description) {
        runtimeState.scenario.write("CHANGES TO CYLINDERS:");
        List<String> beforeCylinderSerialNumbers = testData.getList(BEFORE + "cylinderSerialNumbers", String.class);
        List<String> beforeCylinderSources = testData.getList(BEFORE + "cylinderSources", String.class);
        List<String> beforeCylinderFullPartial = testData.getList(BEFORE + "cylinderFullPartial", String.class);
        List<String> beforeCylinderTypes = testData.getList(BEFORE + "cylinderTypes", String.class);
        List<String> beforeCylinderGasAvailable = testData.getList(BEFORE + "cylinderGasAvailable", String.class);
        List<String> beforeCylinderGasInstalled = testData.getList(BEFORE + "cylinderGasInstalled", String.class);
        List<String> beforeCylinderSurplus = testData.getList(BEFORE + "cylinderSurplus", String.class);
        List<String> beforeCylinderDestinations = testData.getList(BEFORE + "cylinderDestinations", String.class);
        List<String> beforeCylinderSurplusTypes = testData.getList(BEFORE + "cylinderSurplusTypes", String.class);

        List<String> afterCylinderSerialNumbers = testData.getList(AFTER + "cylinderSerialNumbers", String.class);
        List<String> afterCylinderSources = testData.getList(AFTER + "cylinderSources", String.class);
        List<String> afterCylinderFullPartial = testData.getList(AFTER + "cylinderFullPartial", String.class);
        List<String> afterCylinderTypes = testData.getList(AFTER + "cylinderTypes", String.class);
        List<String> afterCylinderGasAvailable = testData.getList(AFTER + "cylinderGasAvailable", String.class);
        List<String> afterCylinderGasInstalled = testData.getList(AFTER + "cylinderGasInstalled", String.class);
        List<String> afterCylinderSurplus = testData.getList(AFTER + "cylinderSurplus", String.class);
        List<String> afterCylinderDestinations = testData.getList(AFTER + "cylinderDestinations", String.class);
        List<String> afterCylinderSurplusTypes = testData.getList(AFTER + "cylinderSurplusTypes", String.class);

        if (beforeCylinderSerialNumbers.size() > 0) {
            if ( testData.getBoolean("gasTypeChanged")) {           // gas type changed so assert cylinder deleted
                assertAuditHistory(description, "Cylinder Serial No with value '" + beforeCylinderSerialNumbers.get(0) + "' was removed");
                assertAuditHistory(description, "Refrigerant Source with value '" + beforeCylinderSources.get(0) + "' was removed");
                String partial = beforeCylinderFullPartial.get(0).equals("Partial") ? "True" : "False";
                assertAuditHistory(description, "Is Partial Cylinder with value '" + partial + "' was removed");
                assertAuditHistory(description, "Type of Cylinder with value '" + beforeCylinderTypes.get(0) + "' was removed");
                assertAuditHistory(description, localize("Gas Available (lbs)") + " with value '" + beforeCylinderGasAvailable.get(0) + "' was removed");
                assertAuditHistory(description, localize("Gas Installed (lbs)") + " with value '" + beforeCylinderGasInstalled.get(0) + "' was removed");
                assertAuditHistory(description, "Surplus with value '" + beforeCylinderSurplus.get(0) + "' was removed");

            } else {       // assert first cylinder changed
                changedAudit(description, "Refrigerant Source", beforeCylinderSources.get(0), afterCylinderSources.get(0));
                changedAudit(description, "Is Partial Cylinder", beforeCylinderFullPartial.get(0), afterCylinderFullPartial.get(0));
                changedAudit(description, "Type of Cylinder", beforeCylinderTypes.get(0), afterCylinderTypes.get(0));
                changedNumericAudit(description, localize("Gas Available (lbs)"), beforeCylinderGasAvailable.get(0), afterCylinderGasAvailable.get(0));
                changedNumericAudit(description, localize("Gas Installed (lbs)"), beforeCylinderGasInstalled.get(0), afterCylinderGasInstalled.get(0));
                changedNumericAudit(description, "Surplus", beforeCylinderSurplus.get(0), afterCylinderSurplus.get(0));
                changedAudit(description, "Surplus Destination", beforeCylinderDestinations.get(0), afterCylinderDestinations.get(0));
                changedAudit(description, "Surplus Type", beforeCylinderSurplusTypes.get(0), afterCylinderSurplusTypes.get(0));
            }
        }

        if (beforeCylinderSerialNumbers.size() > 1) {       // assert last cylinder deleted
            int index = beforeCylinderSerialNumbers.size() - 1;
            assertAuditHistory(description, "Cylinder Serial No with value '" + beforeCylinderSerialNumbers.get(index) + "' was removed");
            assertAuditHistory(description, "Refrigerant Source with value '" + beforeCylinderSources.get(index) + "' was removed");
            String partial = beforeCylinderFullPartial.get(index).equals("Partial") ? "True" : "False";
            assertAuditHistory(description, "Is Partial Cylinder with value '" + partial + "' was removed");
            assertAuditHistory(description, "Type of Cylinder with value '" + beforeCylinderTypes.get(index) + "' was removed");
            assertAuditHistory(description, localize("Gas Available (lbs)") + " with value '" + beforeCylinderGasAvailable.get(index) + "' was removed");
            assertAuditHistory(description, localize("Gas Installed (lbs)") + " with value '" + beforeCylinderGasInstalled.get(index) + "' was removed");
            assertAuditHistory(description, "Surplus with value '" + beforeCylinderSurplus.get(index) + "' was removed");
            if ( !beforeCylinderDestinations.get(index).isEmpty() ) {
                assertAuditHistory(description, "Surplus Destination with value '" + beforeCylinderDestinations.get(index) + "' was removed");
            }
            if ( !beforeCylinderSurplusTypes.get(index).isEmpty() ) {
                assertAuditHistory(description, "Surplus Type with value '" + beforeCylinderSurplusTypes.get(index) + "' was removed");
            }
        }


        int index = afterCylinderSerialNumbers.size() -1;     // assert cylinder added
        assertAuditHistory(description, "Cylinder Serial No was added with value '" + afterCylinderSerialNumbers.get(index) + "'");
        assertAuditHistory(description, "Refrigerant Source was added with value '" + afterCylinderSources.get(index) + "'");
        String partial = afterCylinderFullPartial.get(index).equals("Partial") ? "True" : "False";
        assertAuditHistory(description, "Is Partial Cylinder was added with value '" + partial + "'");
        assertAuditHistory(description, "Type of Cylinder was added with value '" + afterCylinderTypes.get(index) + "'");
        assertAuditHistory(description, localize("Gas Available (lbs)") + " was added with value '" + afterCylinderGasAvailable.get(index) + "'");
        assertAuditHistory(description, localize("Gas Installed (lbs)") + " was added with value '" + afterCylinderGasInstalled.get(index) + "'");
        assertAuditHistory(description, "Surplus was added with value '" + afterCylinderSurplus.get(index) + "'");
        if ( !afterCylinderDestinations.get(index).isEmpty() ) {
            assertAuditHistory(description, "Surplus Destination was added with value '" + afterCylinderDestinations.get(index) + "'");
        }
        if ( !afterCylinderSurplusTypes.get(index).isEmpty() ) {
            assertAuditHistory(description, "Surplus Type was added with value '" + afterCylinderSurplusTypes.get(index) + "'");
        }
    }

    private void assertChangesToLeakSiteChecks(String description) {
        runtimeState.scenario.write("CHANGES TO LEAK SITE CHECKS:");
        List<String> beforePrimaryComponents =  testData.getList(BEFORE + "primaryComponents", String.class);
        List<String> beforeSubComponents = testData.getList(BEFORE + "subComponents", String.class);
        List<String> beforePrimaryComponentsInfo = testData.getList(BEFORE + "primaryComponentsInfo", String.class);
        List<String> beforeLeakSiteStatuses = testData.getList(BEFORE + "leakSiteStatuses", String.class);
        List<String> beforeInitialTests = testData.getList(BEFORE + "initialTests", String.class);
        List<String> beforeFollowUpTests = testData.getList(BEFORE + "followUpTests", String.class);

        List<String> afterPrimaryComponents =  testData.getList(AFTER + "primaryComponents", String.class);
        List<String> afterSubComponents = testData.getList(AFTER + "subComponents", String.class);
        List<String> afterPrimaryComponentsInfo = testData.getList(AFTER + "primaryComponentsInfo", String.class);
        List<String> afterLeakSiteStatuses = testData.getList(AFTER + "leakSiteStatuses", String.class);
        List<String> afterInitialTests = testData.getList(AFTER + "initialTests", String.class);
        List<String> afterFollowUpTests = testData.getList(AFTER + "followUpTests", String.class);

        if (beforePrimaryComponents.size() > 0) {                   // assert first leak site check changed
            changedAudit(description, "Primary Component", beforePrimaryComponents.get(0), afterPrimaryComponents.get(0));
            changedAudit(description, "Sub-Component", beforeSubComponents.get(0), afterSubComponents.get(0));
            changedAudit(description, "Primary Component Information", beforePrimaryComponentsInfo.get(0), afterPrimaryComponentsInfo.get(0));
            changedAudit(description, "Leak Site Status", beforeLeakSiteStatuses.get(0), afterLeakSiteStatuses.get(0));
            changedAudit(description, "Initial Verification Test", beforeInitialTests.get(0), afterInitialTests.get(0));
            changedAudit(description, "Follow Up Verification Test", beforeFollowUpTests.get(0), afterFollowUpTests.get(0));
        }

        if (beforePrimaryComponents.size() > 1) {                   // assert last leak site check removed
            int index = beforePrimaryComponents.size() - 1;
            assertAuditHistory(description, "Primary Component with value '" + beforePrimaryComponents.get(index) + "' was removed");
            assertAuditHistory(description, "Primary Component Information with value '" + beforePrimaryComponentsInfo.get(index) + "' was removed");
            assertAuditHistory(description, "Sub-Component with value '" + beforeSubComponents.get(index) + "' was removed");
            assertAuditHistory(description, "Leak Site Status with value '" + beforeLeakSiteStatuses.get(index) + "' was removed");
            assertAuditHistory(description, "Initial Verification Test with value '" + beforeInitialTests.get(index) + "' was removed");
            assertAuditHistory(description, "Follow Up Verification Test with value '" + beforeFollowUpTests.get(index) + "' was removed");
        }

        int index = afterPrimaryComponents.size() - 1;              // assert leak site check added
        assertAuditHistory(description, "Primary Component was added with value '" + afterPrimaryComponents.get(index) + "'");
        assertAuditHistory(description, "Primary Component Information was added with value '" + afterPrimaryComponentsInfo.get(index) + "'");
        assertAuditHistory(description, "Sub-Component was added with value '" + afterSubComponents.get(index) + "'");
        assertAuditHistory(description, "Leak Site Status was added with value '" + afterLeakSiteStatuses.get(index) + "'");
        assertAuditHistory(description, "Initial Verification Test was added with value '" + afterInitialTests.get(index) + "'");
        assertAuditHistory(description, "Follow Up Verification Test was added with value '" + afterFollowUpTests.get(index) + "'");
    }

    public void assertAuditHistoryUsGas() throws Exception {
        runtimeState.adminRefrigerantGasUsageEditPageV2.expandAutitHistory();
        Grid grid = runtimeState.adminRefrigerantGasUsageEditPageV2.getAutitHistory();

        String timeZone = TimeZone.getDefault().getDisplayName();
        Long scenarioStartTimestamp = DateHelper.stringToTimestamp(runtimeState.timestamp, timeZone, "yyyy-MM-dd HH:mm:ss.SSS");

        String expectedUser = testData.getString("impersonatedUserName");

        String description = "";
        int found = 0;
        for (Row row : grid.getRows()) {
            String updatedOn = row.getCell("Updated On").getText();
            Long updatedOnTimestamp = DateHelper.stringToTimestamp(updatedOn, timeZone, MEDIUM);

            if ((updatedOnTimestamp > scenarioStartTimestamp) && expectedUser.equals(row.getCell("Updated By").getText())) {
                description = description.concat(row.getCell("Description").getText());
                found++;
            }
        }

        runtimeState.scenario.write(found + " audits found after " + runtimeState.timestamp + " updated by " + expectedUser);
        assertTrue("No audits found after " + runtimeState.timestamp + " updated by " + expectedUser, found > 0);

        // assert gas changes
        assertChangesToApplianceInformationAndLeakChecks(description);
        assertAdditionsToApplianceInformationAndLeakChecks(description);
        assertChangesToCylinders(description);
        assertChangesToLeakSiteChecks(description);
    }

}
