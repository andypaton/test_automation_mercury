package mercury.helpers.apihelper;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.PendingException;
import mercury.database.dao.SystemFeatureToggleDao;
import mercury.database.dao.SystemSubFeatureToggleDao;
import mercury.database.models.SystemFeatureToggle;
import mercury.database.models.SystemSubFeatureToggle;
import mercury.databuilders.TestData;
import mercury.databuilders.Toggles;
import mercury.runtime.RuntimeState;

@Component
public class ApiHelperAdmin extends ApiHelper{

    private static final Logger logger = LogManager.getLogger();

    @Autowired private TestData testData;
    @Autowired private SystemFeatureToggleDao systemFeatureToggleDao;
    @Autowired private RuntimeState runtimeState;
    @Autowired private SystemSubFeatureToggleDao systemSubFeatureToggleDao;
    @Autowired private Toggles toggles;

    private static final String SYSTEM_TOGGLES_PAYLOAD = "{\"systemFeatureToggles\":[%s],\"systemSubFeatureToggles\":[%s]}";
    private static final String TOGGLE_PAYLOAD = "{\"id\":%d,\"active\":%b}";
    private static final String MONITOR_PAYLOAD = "[{\"id\":%d,\"areaDisplayName\":\"%s\",\"displayName\":\"%s\",\"active\":%s,\"categoryDisplayName\":\"%s\",\"exportDisplayName\":\"%s\"}]";


    private void assertTagsExist() {

        if (!Boolean.valueOf(System.getProperty("setSystemToggles"))) {
            throw new PendingException("System configured not to allow toggle changes, please re-run enabling setting of System Toggles");
        }

        if ( !runtimeState.scenario.getSourceTagNames().contains("@toggles") ) {
            throw new PendingException("Scenario must include tag: @toggles");
        }

        if ( !runtimeState.scenario.getSourceTagNames().contains("@admin") && !toggles.getBoolean("resetToggles") ) {
            Map<String, Object> featureToggles = toggles.getMap("systemFeatureToggles");
            if (featureToggles != null) {
                for (String feature : featureToggles.keySet()) {
                    String tag = "@" + feature.replaceAll(" ", "");
                    if ( !runtimeState.scenario.getSourceTagNames().contains(tag) ) {
                        throw new PendingException("Scenario must include tag: " + tag);
                    }
                }
            }

            Map<String, Object> subFeatureToggles = toggles.getMap("systemSubFeatureToggles");
            if (subFeatureToggles != null) {
                for (String subFeature : subFeatureToggles.keySet()) {
                    String tag = "@" + subFeature.replaceAll(" ", "");
                    if ( !runtimeState.scenario.getSourceTagNames().contains(tag) ) {
                        throw new PendingException("Scenario must include tag: " + tag);
                    }
                }
            }
        }
    }

    public void setSystemFeatureToggles() throws Exception {
        assertTagsExist();
        String url = propertyHelper.getMercuryUrl() + "/api/systemfeaturetoggle/save";

        Map<String, Object> featureToggles = toggles.getMap("systemFeatureToggles");
        String systemFeatureToggles = "";
        if (featureToggles != null) {
            for (String feature : featureToggles.keySet()) {

                try {
                    int id = getSystemFeatureToggleId(feature);

                    int toggleVal = (Integer) featureToggles.get(feature);
                    boolean active = toggleVal == 0 ? false : true;

                    String featureToggle = String.format(TOGGLE_PAYLOAD, id, active);
                    systemFeatureToggles += systemFeatureToggles.isEmpty() ? featureToggle : "," + featureToggle;

                    toggles.addToMap("updatedFeatureToggles", String.valueOf(id), active);
                } catch (Exception e) {
                    logger.debug("Ignored exception thrown while attempting to retrieve id for: " + feature);
                }
            }
        }

        Map<String, Object> subFeatureToggles = toggles.getMap("systemSubFeatureToggles");
        String systemSubFeatureToggles = "";
        if (subFeatureToggles != null) {
            for (String feature : subFeatureToggles.keySet()) {

                try {
                    int id = getSystemSubFeatureToggleId(feature);

                    int toggleVal = (Integer) subFeatureToggles.get(feature);
                    boolean active = toggleVal == 0 ? false : true;

                    String subFeatureToggle = String.format(TOGGLE_PAYLOAD, id, active);
                    systemSubFeatureToggles += systemSubFeatureToggles.isEmpty() ? subFeatureToggle : "," + subFeatureToggle;

                    toggles.addToMap("updatedSubFeatureToggles", String.valueOf(id), active);
                } catch (Exception e) {
                    logger.debug("Ignored exception thrown while attempting to retrieve id for: " + feature);
                }
            }
        }

        if (featureToggles != null || subFeatureToggles != null) {
            String jsonPayload = String.format(SYSTEM_TOGGLES_PAYLOAD, systemFeatureToggles, systemSubFeatureToggles);
            toggles.put("haveTogglesBeenUpdated", true);

            restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
            int statusCode = restService.getStatusCode();
            logger.debug("setSystemFeatureToggles response status code: " + statusCode);
            assertTrue("Expected status code 200 but got: " + statusCode, statusCode == 200);
        }
    }

    private int getSystemFeatureToggleId(String feature) throws Exception {
        List<SystemFeatureToggle> toggles = systemFeatureToggleDao.getAll();
        for (SystemFeatureToggle toggle : toggles) {
            if (feature.equalsIgnoreCase(toggle.getFeature())) {
                return toggle.getId();
            }
        }
        throw new Exception("Unexpected feature: " + feature);
    }

    private int getSystemSubFeatureToggleId(String feature) throws Exception {
        List<SystemSubFeatureToggle> toggles = systemSubFeatureToggleDao.getAll();
        for (SystemSubFeatureToggle toggle : toggles) {
            if (feature.equalsIgnoreCase(toggle.getSubFeature())) {
                return toggle.getId();
            }
        }
        throw new Exception("Unexpected sub feature: " + feature);
    }

    public void updateMonitors(List<Map<String, Object>> dbData, String activeFlag) throws Exception {

        String url = propertyHelper.getMercuryUrl() + "/api/monitors/updatemonitors";
        String monitorValue = testData.getString("monitorToUpdate");
        String jsonPayload = null;

        try {
            int monitorId = Integer.valueOf(dbData.get(0).get("Id").toString());
            String areaDisplayName = dbData.get(0).get("AreaDisplayName").toString();
            String categoryDisplayName = dbData.get(0).get("CategoryDisplayName").toString();
            testData.addStringTag("monitorOriginalActiveflag", activeFlag);
            String displayName = dbData.get(0).get("DisplayName").toString();
            String exportName = dbData.get(0).get("ExportName").toString();

            jsonPayload = String.format(MONITOR_PAYLOAD, monitorId, areaDisplayName, categoryDisplayName, activeFlag, displayName, exportName);
            runtimeState.scenario.write("Resetting monitors : " + jsonPayload);
            testData.put("haveMonitorBeenUpdated", true);

            restService.sendPostRequestWithRedirection(url, jsonPayload, appUserMercuryCookie);
            int statusCode = restService.getStatusCode();
            logger.debug("setMonitors response status code: " + statusCode);
            assertTrue("Expected status code 200 but got: " + statusCode, statusCode == 200);

        } catch (Exception e) {
            logger.debug("Ignored exception thrown while attempting to retrieve id for: " + monitorValue);
        }
    }
}