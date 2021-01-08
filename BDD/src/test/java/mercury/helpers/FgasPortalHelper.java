package mercury.helpers;

import static mercury.helpers.Globalisation.localize;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperGas;
import mercury.pageobject.web.pageHelpers.QuestionHelper;
import mercury.runtime.RuntimeState;

import static mercury.helpers.StringHelper.trimZeros;

@Component
public class FgasPortalHelper {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelper dbHelper;
    @Autowired private DbHelperGas dbHelperGas;

    private LinkedHashMap<String, Object> makeKeyMap(String title, Object value, Integer type) {
        LinkedHashMap<String, Object> details2Map =  new LinkedHashMap<String, Object>();
        if (value instanceof Integer) {
            BigDecimal bottleQuantity = new BigDecimal(String.valueOf(value));
            value = bottleQuantity.setScale(1, RoundingMode.CEILING);
        }
        details2Map.put("Title", title);
        details2Map.put("Value", value);
        details2Map.put("Type", type);
        return details2Map;
    }

    public String buildApplianceMap() throws Throwable {
        // Get appliance information from testData
        Map<String, Object> testDataMap = testData.getMap("FGAS Appliance");

        // Create ordered map to store values
        LinkedHashMap<String, Object> applianceDetailsMap =  new LinkedHashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();

        String gasTypeName = ("Incorrect refrigerant type displayed".equalsIgnoreCase(testData.getString("gasTypeId"))) ? testData.getString("gasTypeOtherId") : testData.getString("gasTypeId");
        applianceDetailsMap.put("Gas Type", gasTypeName);
        applianceDetailsMap.put("Gas Appliance Type", testDataMap.get("applianceTypeId"));
        if (testDataMap.get("applianceIdentificationId") != null) {
            applianceDetailsMap.put("Appliance Information", testDataMap.get("applianceIdentificationId"));
        }

        if (testDataMap.get("ApplianceInformation") != null) {
            applianceDetailsMap.put("Appliance Information", testDataMap.get("ApplianceInformation"));
        }

        if (testDataMap.get("ReceiverLevelRecorded") != null) {
            String ReceiverLevelRecorded = testDataMap.get("ReceiverLevelRecorded") == "Yes" ? "Yes" : "No" ;
            applianceDetailsMap.put("Receiver Level Recorded", ReceiverLevelRecorded);
        }

        if (testDataMap.get("QuantityOfBallsFloating") != null) {
            applianceDetailsMap.put("Quantity of Balls Floating", Integer.valueOf((String) testDataMap.get("QuantityOfBallsFloating")));
        }
        if (testDataMap.get("LevelIndicator") != null) {
            applianceDetailsMap.put("Level Indicator Percentage", Integer.valueOf((String)testDataMap.get("LevelIndicator")));
        }

        testDataMap = testData.getMap("Leak Check Questions");
        if (testDataMap.get("GasLeakCheckStatusId") != null) {
            applianceDetailsMap.put("Gas Leak Check Status", testDataMap.get("GasLeakCheckStatusId"));
        }
        if (testDataMap.get("GasLeakCheckMethodId") != null) {
            applianceDetailsMap.put("Gas Leak Check Method", testDataMap.get("GasLeakCheckMethodId"));
        }
        if (testDataMap.get("GasLeakCheckResultTypeId") != null) {
            applianceDetailsMap.put("Gas Leak Check Result Type", testDataMap.get("GasLeakCheckResultTypeId"));
        }

        // Output as string
        String mapAsString = mapper.writeValueAsString(applianceDetailsMap);
        mapAsString = mapAsString.substring(1, mapAsString.length()-1);
        mapAsString = "{".concat(mapAsString).concat(",");

        runtimeState.scenario.write("Appliance map: " + mapAsString);

        return mapAsString;
    }

    public String buildApplianceMapV2() throws Throwable {
        // Get appliance information from testData
        Map<String, Object> testDataMap = testData.getMap("FGAS Appliance");

        //If no map then gas not used
        if (null == testDataMap) {
            return null;
        }

        // Create ordered map to store values
        LinkedHashMap<String, Object> applianceDetailsMap =  new LinkedHashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();

        boolean type1 = testDataMap.get("applianceTypeId") != null ? true : false;

        Object applianceTypeId = type1 ? testDataMap.get("applianceTypeId") : testDataMap.get("Appliance Type");
        Object applianceInformation = type1 ? testDataMap.get("ApplianceInformation") : testDataMap.get("Please provide appliance details");
        Object applianceIdentificationId = type1 ? testDataMap.get("applianceIdentificationId") : testDataMap.get("Appliance Identification");
        Object receiverLevelRecorded = type1 ? testDataMap.get("ReceiverLevelRecorded") : testDataMap.get("Has receiver level been recorded?");
        Object quantityOfBallsFloating = type1 ? testDataMap.get("QuantityOfBallsFloating") : testDataMap.get("Quantity of Balls Floating");
        Object levelIndicator = type1 ? testDataMap.get("LevelIndicator") : testDataMap.get("Provide Level Indicator %");
        String gasTypeId = type1 ? "gasTypeId" : "Refrigerant Type Used";
        String gasTypeOtherId = type1 ? "gasTypeOtherId" : "New Refrigerant Type Used";


        if (applianceTypeId != null) {
            applianceDetailsMap.put("GasApplianceType", makeKeyMap("Appliance Type", applianceTypeId.toString(), 0));
        }

        if (applianceInformation != null) {
            applianceDetailsMap.put("ApplianceInformation", makeKeyMap("Appliance Information", applianceInformation.toString(), 0));
        }

        if ((applianceIdentificationId != null) && !("Appliance not on list".equalsIgnoreCase(applianceIdentificationId.toString()))) {
            // Need to get the classification and subtype for the asset
            String classificationSubtype = dbHelper.getAssetClassificationSubtypeName(testData.getString("siteName"), applianceIdentificationId.toString());
            String appId = applianceIdentificationId.toString().concat(" ").concat(classificationSubtype.replace(" > )", ")"));
            applianceDetailsMap.put("ApplianceIdentification", makeKeyMap("Appliance Identification", appId, 0));
        }

        if (receiverLevelRecorded != null) {
            applianceDetailsMap.put("ReceiverLevelRecorded", makeKeyMap("Receiver Level Recorded", receiverLevelRecorded.toString(), 0));
        }

        if (quantityOfBallsFloating != null) {
            applianceDetailsMap.put("QuantityOfBallsFloating", makeKeyMap("Quantity of Balls Floating", Integer.valueOf((String) quantityOfBallsFloating).toString(), 0));
        }

        if (levelIndicator != null) {
            applianceDetailsMap.put("LevelIndicator", makeKeyMap("Provided Level Indicator %", Integer.valueOf((String) levelIndicator).toString(), 0));
        }

        String gasTypeName = ("Incorrect refrigerant type displayed".equalsIgnoreCase(testDataMap.get(gasTypeId).toString())) ? testDataMap.get(gasTypeOtherId).toString() : testDataMap.get(gasTypeId).toString();
        if (gasTypeName != null) {
            applianceDetailsMap.put("GasType", makeKeyMap("Refrigerant Type Used", gasTypeName, 0));
        }

        testDataMap = testData.getMap("Leak Check Questions");
        if (testDataMap.get("GasLeakCheckStatusId") != null) {
            applianceDetailsMap.put("GasLeakCheckStatus", makeKeyMap("Leak Check Status", testDataMap.get("GasLeakCheckStatusId").toString(), 0));
        }
        if (testDataMap.get("GasLeakCheckMethodId") != null) {
            applianceDetailsMap.put("GasLeakCheckMethod", makeKeyMap("Leak Check Method", testDataMap.get("GasLeakCheckMethodId").toString(), 0));
        }
        if (testDataMap.get("GasLeakCheckResultTypeId") != null) {
            applianceDetailsMap.put("GasLeakCheckResultType", makeKeyMap("Leak Check Result Type", testDataMap.get("GasLeakCheckResultTypeId").toString(), 0));
        }

        testDataMap = testData.getMap("Maximum Charge");
        if (testDataMap != null) {
            if (testDataMap.get("NewAssetMaximumCharge") != null) {
                applianceDetailsMap.put("NewAssetMaximumCharge", makeKeyMap("New Appliance Maximum Charge", testDataMap.get("NewAssetMaximumCharge").toString(), 0));
            }

            if (testDataMap.get("ReasonForChangingMaximumCharge") != null) {
                applianceDetailsMap.put("ReasonForChangingMaximumCharge", makeKeyMap("Reason For Changing Maximum Charge", testDataMap.get("ReasonForChangingMaximumCharge").toString(), 0));
            }
        }

        testData.addMap("Gas Details", applianceDetailsMap);

        String mapAsString1 = "\"Gas Details\":" + mapper.writeValueAsString(testData.getLinkedListMap("Gas Details"));

        // Output as string
        String mapAsString = mapper.writeValueAsString(applianceDetailsMap);
        mapAsString = mapAsString.substring(1, mapAsString.length()-1);

        runtimeState.scenario.write("Appliance map: " + mapAsString);

        return mapAsString1;
    }

    public String buildApplianceMapV3(Map<String, Object> testDataMap) throws Throwable {
        // Get appliance information from testData
        //        Map<String, Object> testDataMap = testData.getMap("FGAS Appliance");

        //If no map then gas not used
        if (null == testDataMap) {
            return null;
        }
        // Create ordered map to store values
        LinkedHashMap<String, Object> applianceDetailsMap =  new LinkedHashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();
        if (testDataMap.get("applianceTypeId") != null) {
            applianceDetailsMap.put("GasApplianceType", makeKeyMap("Appliance Type", testDataMap.get("applianceTypeId").toString(), 0));
        }

        if (testDataMap.get("ApplianceInformation") != null) {
            applianceDetailsMap.put("ApplianceInformation", makeKeyMap("Appliance Information", testDataMap.get("ApplianceInformation").toString(), 0));
        }

        if ((testDataMap.get("applianceIdentificationId") != null) && !("Appliance not on list".equalsIgnoreCase(testDataMap.get("applianceIdentificationId").toString()))) {
            // Need to get the classification and subtype for the asset
            String classificationSubtype = dbHelper.getAssetClassificationSubtypeName(testData.getString("siteName"), testDataMap.get("applianceIdentificationId").toString());
            String appId = testDataMap.get("applianceIdentificationId").toString().concat(" ").concat(classificationSubtype);
            applianceDetailsMap.put("ApplianceIdentification", makeKeyMap("Appliance Identification", appId, 0));
        }

        if (testDataMap.get("ReceiverLevelRecorded") != null) {
            applianceDetailsMap.put("ReceiverLevelRecorded", makeKeyMap("Receiver Level Recorded", testDataMap.get("ReceiverLevelRecorded").toString(), 0));
        }

        if (testDataMap.get("QuantityOfBallsFloating") != null) {
            applianceDetailsMap.put("QuantityOfBallsFloating", makeKeyMap("Quantity of Balls Floating", Integer.valueOf((String) testDataMap.get("QuantityOfBallsFloating")).toString(), 0));
        }

        if (testDataMap.get("LevelIndicator") != null) {
            applianceDetailsMap.put("LevelIndicator", makeKeyMap("Provided Level Indicator %", Integer.valueOf((String) testDataMap.get("LevelIndicator")).toString(), 0));
        }

        String gasTypeName = ("Incorrect refrigerant type displayed".equalsIgnoreCase(testData.getString("gasTypeId"))) ? testData.getString("gasTypeOtherId") : testData.getString("gasTypeId");
        if (gasTypeName != null) {
            applianceDetailsMap.put("GasType", makeKeyMap("Refrigerant Type Used", gasTypeName, 0));
        }

        testDataMap = testData.getMap("Leak Check Questions");
        if (testDataMap.get("GasLeakCheckStatusId") != null) {
            applianceDetailsMap.put("GasLeakCheckStatus", makeKeyMap("Leak Check Status", testDataMap.get("GasLeakCheckStatusId").toString(), 0));
        }
        if (testDataMap.get("GasLeakCheckMethodId") != null) {
            applianceDetailsMap.put("GasLeakCheckMethod", makeKeyMap("Leak Check Method", testDataMap.get("GasLeakCheckMethodId").toString(), 0));
        }
        if (testDataMap.get("GasLeakCheckResultTypeId") != null) {
            applianceDetailsMap.put("GasLeakCheckResultType", makeKeyMap("Leak Check Result Type", testDataMap.get("GasLeakCheckResultTypeId").toString(), 0));
        }

        testDataMap = testData.getMap("Maximum Charge");
        if (testDataMap != null) {
            if (testDataMap.get("NewAssetMaximumCharge") != null) {
                applianceDetailsMap.put("NewAssetMaximumCharge", makeKeyMap("New Appliance Maximum Charge", testDataMap.get("NewAssetMaximumCharge").toString(), 0));
            }

            if (testDataMap.get("ReasonForChangingMaximumCharge") != null) {
                applianceDetailsMap.put("ReasonForChangingMaximumCharge", makeKeyMap("Reason For Changing Maximum Charge", testDataMap.get("ReasonForChangingMaximumCharge").toString(), 0));
            }
        }

        testData.addMap("Gas Details", applianceDetailsMap);

        // Output as string
        String mapAsString = mapper.writeValueAsString(applianceDetailsMap);
        mapAsString = mapAsString.substring(1, mapAsString.length()-1);

        runtimeState.scenario.write("Appliance map: " + mapAsString);

        return mapAsString;
    }

    public LinkedHashMap<String, Object> buildGasCylinderMap( Map<String, Object> map) throws Throwable {

        LinkedHashMap<String, Object> gasCylinderMap =  new LinkedHashMap<String, Object>();

        boolean type1 = map.get("gasCylinderSerialNo") != null ? true : false;

        Object refrigerantSourceID = type1 ? map.get("refrigerantSourceID") : map.get("Refrigerant Source");
        Object lbsInstalled = type1 ? map.get("lbsInstalled") : map.get(localize("Gas Installed (lbs)"));
        Object gasCylinderTypeId = type1 ? map.get("gasCylinderTypeId") : map.get("Type of Cylinder");
        Object refrigerantSourceLocation = type1 ? map.get("RefrigerantSourceLocation") : map.get("Refrigerant Source Location");
        Object fullOrPartialCylinder = type1 ? map.get("IsPartialCylinder") : map.get("Full or Partial Cylinder");
        Object gasCylinderSerialNo = type1 ? map.get("gasCylinderSerialNo") : map.get("Cylinder Serial No");
        Object gasPoundsInCylinderID = type1 ? map.get("gasPoundsInCylinderID") : map.get(localize("Gas Available (lbs)"));

        if (refrigerantSourceID != null) {
            gasCylinderMap.put("GasSourceType", makeKeyMap("Refrigerant Source", refrigerantSourceID.toString(), 0));
        }

        if (refrigerantSourceLocation != null) {
            gasCylinderMap.put("RefrigerantSourceLocation", makeKeyMap("Refrigerant Source Location", refrigerantSourceLocation.toString(), 0));
        }

        if (fullOrPartialCylinder != null) {
            gasCylinderMap.put("FullOrPartialCylinder", makeKeyMap("Full or Partial Cylinder", fullOrPartialCylinder.toString(), 0));
        }

        if (gasCylinderTypeId != null) {
            gasCylinderMap.put("GasCylinderType",  makeKeyMap("Type of Cylinder", gasCylinderTypeId, 0));
        }

        if (gasCylinderSerialNo != null) {
            gasCylinderMap.put("BottleNumber", makeKeyMap("Cylinder Serial No", gasCylinderSerialNo.toString(), 0));
        }

        if (gasPoundsInCylinderID != null) {
            String initQty = trimZeros(((Double) gasPoundsInCylinderID).toString());
            gasCylinderMap.put("InitialQuantity", makeKeyMap(localize("Gas Available (lbs)"), initQty, 0));
        }

        if (map.get(localize("Total (lbs)")) != null) {
            gasCylinderMap.put("BottleQuantity", makeKeyMap(localize("Gas Installed (lbs)"), Integer.valueOf((String) map.get(localize("Total (lbs)"))).toString(), 0));
        }

        if (lbsInstalled != null) {
            String val = trimZeros(((Double) lbsInstalled).toString());
            gasCylinderMap.put("BottleQuantity", makeKeyMap(localize("Gas Installed (lbs)"), val, 0));
        }

        boolean isSurplus = map.get("GasSurplusDestinationId") != null || map.get("Surplus Type") != null ? true : false;
        if (isSurplus) {
            Object gasSurplusDestinationId = type1 ? map.get("GasSurplusDestinationId") : map.get("Destination");
            Object gasSurplusTypeId = type1 ? map.get("GasSurplusTypeId") : map.get("Surplus Type");
            Object returnedTo = type1 ? map.get("ReturnedTo") : map.get("Returned To");

            Double surplus = (Double) gasPoundsInCylinderID - (Double) lbsInstalled;
            gasCylinderMap.put("Surplus", makeKeyMap("Surplus", trimZeros(surplus.toString()), 0));

            if (gasSurplusDestinationId != null) {
                gasCylinderMap.put("SurplusDestination", makeKeyMap("Surplus Destination", gasSurplusDestinationId.toString(), 0));
            }

            if (gasSurplusTypeId != null) {
                gasCylinderMap.put("SurplusType", makeKeyMap("Surplus Type", gasSurplusTypeId.toString(), 0));
            }

            if (returnedTo != null) {
                gasCylinderMap.put("SurplusReturnedTo", makeKeyMap("Returned To", returnedTo.toString(), 0));
            }

        } else {
            gasCylinderMap.put("Surplus", makeKeyMap("Surplus", "0", 0));
        }

        return gasCylinderMap;
    }

    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    public String buildGasUsageMap() throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String, Object>> refrigerantGasUsage = testData.getLinkedListMap("refrigerantGasUsage");
        LinkedHashMap<String, Object> gasUsage = new LinkedHashMap<>();
        if ( refrigerantGasUsage != null) {
            Iterator it = refrigerantGasUsage.iterator();

            while (it.hasNext()) {
                Map<String, Object> map = (Map<String, Object>) it.next();

                testData.addMap("Gas Usage", buildGasCylinderMap(map));
            }
        }

        String mapAsString = "\"Gas Usage\":" + mapper.writeValueAsString(testData.getLinkedListMap("Gas Usage"));

        runtimeState.scenario.write("Gas Usage: " + mapAsString);
        return mapAsString;
    }

    public LinkedHashMap<String, Object> buildLeakSiteInfoMap( Map<String, Object> map) throws Throwable {

        LinkedHashMap<String, Object> leakSiteInfoMap =  new LinkedHashMap<String, Object>();

        if (map.get("Leak Location") != null) {
            leakSiteInfoMap.put("LeakLocation", makeKeyMap("Primary Component", map.get("Leak Location").toString(), 0));
        }

        if (map.get("primaryComponentInformation") != null) {
            leakSiteInfoMap.put("PrimaryComponentInformation", makeKeyMap("Primary Component Information", map.get("primaryComponentInformation"), 0));
        }

        if (map.get("Leak Sub Location") != null) {
            leakSiteInfoMap.put("LeakSubLocation",  makeKeyMap("Sub-Component", map.get("Leak Sub Location"), 0));
        }

        if (map.get("Leak Site Status") != null) {
            leakSiteInfoMap.put("LeakSiteStatus", makeKeyMap("Leak Site Status", map.get("Leak Site Status").toString(), 0));
        }

        if (map.get("Initial Verification Test") != null) {
            leakSiteInfoMap.put("InitialVerificationTest", makeKeyMap("Initial Verification Test", map.get("Initial Verification Test").toString(), 0));
        }

        if (map.get("gasLeakFollowUpTestId") != null) {
            leakSiteInfoMap.put("FollowUpVerificationTest", makeKeyMap("Follow Up Verification Test", map.get("gasLeakFollowUpTestId").toString(), 0));
        }

        return leakSiteInfoMap;
    }

    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    public String buildLeakSitesMap() throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        List<LinkedHashMap<String, Object>> leakSiteInformation = testData.getLinkedListMap("leakSiteInformation");

        LinkedHashMap<String, Object> details1Map =  new LinkedHashMap<String, Object>();
        if ( leakSiteInformation != null) {
            Iterator it = leakSiteInformation.iterator();
            while (it.hasNext()) {
                Map<String, Object> map = (Map<String, Object>) it.next();
                testData.addMap("Leak Site Checks", buildLeakSiteInfoMap(map));
            }
        }

        String mapAsString = "\"Leak Site Checks\":" + mapper.writeValueAsString(testData.getLinkedListMap("Leak Site Checks"));

        runtimeState.scenario.write("Leak Site Checks: " + mapAsString);
        return mapAsString;
    }


    public String getGasDetails(String detail, String section) {
        String re1=".*?";  // Non-greedy match on filler
        String re2="(\"" + section + "\")";
        String re3="(\\[.*?\\])";   // Square Braces 1
        Pattern p = Pattern.compile(re1+re2+re1+re3,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(detail);
        if (m.find()) {
            String extract = m.group(1).concat(":").concat(m.group(2));
            return extract;
        }

        return "";
    }

    public String getLatestGasSectionDetails(String section) {
        String details1 = dbHelperGas.getLatestGasDetails(testData.getInt("jobReference"));
        logger.debug("================================================");
        logger.debug(details1);
        logger.debug("================================================");
        if (null != details1) {
            details1 = getGasDetails(details1, section);
        }
        return details1;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Integer getTotalInstalled() throws Throwable {
        List<LinkedHashMap<String, Object>> refrigerantGasUsage = testData.getLinkedListMap("refrigerantGasUsage");
        Integer totalInstalled = 0;
        if ( refrigerantGasUsage != null) {
            Iterator it = refrigerantGasUsage.iterator();
            while (it.hasNext()) {
                Map<String, Object> map = (Map<String, Object>) it.next();

                totalInstalled += Integer.valueOf((String) map.get("lbsInstalled"));
            }
        }

        return totalInstalled;
    }



    public String getFGASDetails1() throws Throwable {
        String details1 = "{\"Overtime\":\"00:00:00\",\"Travel time\":\"" + testData.getString("Travel time") + "\"";

        String gasApplianceDetails = buildApplianceMapV2();
        String gasUsage = buildGasUsageMap();
        String leakSite = buildLeakSitesMap();


        if (null != gasApplianceDetails ) {
            details1 = details1.concat(",").concat(gasApplianceDetails);
        } else {
            details1 = details1.concat(",").concat(getLatestGasSectionDetails("Gas Details"));
        }

        if (!gasUsage.equalsIgnoreCase("\"Gas Usage\":null")) {
            details1 = details1.concat(",").concat(gasUsage);
        }

        if (!leakSite.equalsIgnoreCase("\"Leak Site Checks\":null")) {
            details1 = details1.concat(",").concat(leakSite);
        } else {
            leakSite = getLatestGasSectionDetails("Leak Site Checks");
            if (null !=leakSite) {
                details1 = details1.concat(",").concat(leakSite);
            }
        }

        details1= details1.concat("}");
        return details1;
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
        testData.put(prefix + "cylinderSerialNumbers", runtimeState.updateJobPage.getCylinderSerialNumbers());
        testData.put(prefix + "cylinderSources", runtimeState.updateJobPage.getSources());
        testData.put(prefix + "cylinderFullPartial", runtimeState.updateJobPage.getFullPartial());
        testData.put(prefix + "cylinderTypes", runtimeState.updateJobPage.getCylinderTypes());
        testData.put(prefix + "cylinderGasAvailable", runtimeState.updateJobPage.getGasAvailable());
        testData.put(prefix + "cylinderGasInstalled", runtimeState.updateJobPage.getGasInstalled());
        testData.put(prefix + "cylinderSurplus", runtimeState.updateJobPage.getSurplus());
        testData.put(prefix + "cylinderDestinations", runtimeState.updateJobPage.getDestinations());
        testData.put(prefix + "cylinderSurplusTypes", runtimeState.updateJobPage.getSurplusTypes());

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

        testData.put(prefix + "primaryComponents", runtimeState.updateJobPage.getPrimaryComponents());
        testData.put(prefix + "primaryComponentsInfo", runtimeState.updateJobPage.getPrimaryComponentsInfo());
        testData.put(prefix + "subComponents", runtimeState.updateJobPage.getSubComponents());
        testData.put(prefix + "leakSiteStatuses", runtimeState.updateJobPage.getLeakSiteStatuses());
        testData.put(prefix + "initialTests", runtimeState.updateJobPage.getInitialTests());
        testData.put(prefix + "followUpTests", runtimeState.updateJobPage.getFollowUpTests());

        runtimeState.scenario.write(prefix + "questions : primaryComponents : " + testData.getArray(prefix + "primaryComponents").toString());
        runtimeState.scenario.write(prefix + "questions : primaryComponentsInfo : " + testData.getArray(prefix + "primaryComponentsInfo").toString());
        runtimeState.scenario.write(prefix + "questions : subComponents : " + testData.getArray(prefix + "subComponents").toString());
        runtimeState.scenario.write(prefix + "questions : leakSiteStatuses : " + testData.getArray(prefix + "leakSiteStatuses").toString());
        runtimeState.scenario.write(prefix + "questions : initialTests : " + testData.getArray(prefix + "initialTests").toString());
        runtimeState.scenario.write(prefix + "questions : followUpTests : " + testData.getArray(prefix + "followUpTests").toString());
    }

    public void captureAllFgasUsQuestions() throws Throwable {
        String prefix = "FGAS_";
        QuestionHelper questionHelper = runtimeState.updateJobPage.getQuestionHelper();

        if (questionHelper.isQuestionVisible("Refrigerant Gas used during this visit") && questionHelper.getAnswer("Refrigerant Gas used during this visit").equals("Yes")) {
            captureApplianceInformation(questionHelper, prefix);
            captureRefrigerantDetails(questionHelper, prefix);
            captureLeakInspectionsAndRepairs(questionHelper, prefix);
        }
    }

    public void updateLeakInspectionAndRepair() {
        QuestionHelper questionHelper = runtimeState.updateJobPage.getQuestionHelper();

        //        String[] questions = { "Leak Check Status", "Leak Check Method", "Leak Check Result Type", "Status on Departure" };
        String[] questions = { "Leak Check Status", "Leak Check Method", "Leak Check Result Type" };

        for (String question : questions) {
            String answer;
            if (questionHelper.isQuestionVisible(question)) {
                if (testData.getString(question) != null) {
                    if (question.equals("Leak Check Status")) {
                        if (testData.getString(question).equals("Complete")) {
                            answer = dbHelperGas.getRandomLeakCheckStatus(false);
                        } else if (testData.getString(question).equals("Not Complete")) {
                            answer = dbHelperGas.getRandomLeakCheckStatus(true);
                        } else {
                            answer = dbHelperGas.getRandomLeakCheckStatus(null);
                        }
                    } else{
                        answer = testData.getString(question);
                    }
                    questionHelper.updateAnswer(question, answer);
                } else {
                    questionHelper.updateAnswer(question);
                }
            }
        }
    }

}
