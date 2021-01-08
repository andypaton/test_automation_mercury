package mercury.helpers;

import static mercury.helpers.StringHelper.splitCamelCase;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mercury.databuilders.Toggles;
import mercury.helpers.apihelper.ApiHelperAdmin;
import mercury.helpers.dbhelper.DbHelperSystemToggles;
import mercury.runtime.RuntimeState;

public class ToggleHelper {

    @Autowired private PropertyHelper propertyHelper;
    @Autowired private ApiHelperAdmin apiHelperAdmin;
    @Autowired private Toggles toggles;
    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperSystemToggles dbHelperSystemToggles;


    private static final Logger logger = LogManager.getLogger();

    public void enableSystemFeatureToggle(String feature) throws Exception {
        toggles.addToMap("systemFeatureToggles", feature, 1);
        apiHelperAdmin.setSystemFeatureToggles();
    }

    public void disableSystemFeatureToggle(String feature) throws Exception {
        toggles.addToMap("systemFeatureToggles", feature, 0);
        apiHelperAdmin.setSystemFeatureToggles();
    }

    public void enableSystemSubFeatureToggle(String feature) throws Exception {
        toggles.addToMap("systemSubFeatureToggles", feature, 1);
        apiHelperAdmin.setSystemFeatureToggles();
    }

    public void disableSystemSubFeatureToggle(String feature) throws Exception {
        toggles.addToMap("systemSubFeatureToggles", feature, 0);
        apiHelperAdmin.setSystemFeatureToggles();
    }

    public void resetTogglesViaUI() throws Exception {
        String client = propertyHelper.getEnv().split("_")[1];
        logger.debug("Reseting System Feature Toggles to: " + client);

        // read default feature toggle values
        List<String> featureDefaults = FileHelper.getLines("systemFeatureToggles/" + client.toLowerCase(), "SystemFeatureToggle.txt");
        runtimeState.scenario.write("Setting " + client + " feature toggles to defaults: " + featureDefaults.toString());

        for (String featureValue : featureDefaults) {
            featureValue = featureValue.trim();
            String feature = featureValue.substring(0, featureValue.length() - 2).trim();
            feature = splitCamelCase(feature);
            Boolean active = featureValue.substring(featureValue.length() - 1).equals("1");

            if (active) {
                runtimeState.adminSystemFeatureTogglePage.toggleOnFeature(feature);
            } else {
                runtimeState.adminSystemFeatureTogglePage.toggleOffFeature(feature);
            }
        }

        // read default sub feature toggle values
        List<String> subFeatureDefaults = FileHelper.getLines("systemFeatureToggles/" + client.toLowerCase(), "SystemSubFeatureToggle.txt");
        runtimeState.scenario.write("Setting " + client + " sub feature toggles to defaults: " + subFeatureDefaults.toString());

        for (String subFeatureValue : subFeatureDefaults) {
            subFeatureValue = subFeatureValue.trim();
            String subFeature = subFeatureValue.substring(0, subFeatureValue.length() - 2).trim();
            subFeature = splitCamelCase(subFeature);
            Boolean active = subFeatureValue.substring(subFeatureValue.length() - 1).equals("1");

            if (active) {
                String parent = runtimeState.adminSystemFeatureTogglePage.getParentToggle(subFeature);
                if ( ! runtimeState.adminSystemFeatureTogglePage.isToggleOnFeature(parent) ) {
                    // the sub features parent toggle must be set to ON
                    runtimeState.adminSystemFeatureTogglePage.toggleOnFeature(parent);
                }
                runtimeState.adminSystemFeatureTogglePage.toggleOnSubFeature(subFeature);
            } else {
                runtimeState.adminSystemFeatureTogglePage.toggleOffSubFeature(subFeature);
            }
        }
    }

    public void resetTogglesViaAPI() throws Exception {
        String client = propertyHelper.getEnv().split("_")[1];
        logger.debug("Reseting System Feature Toggles to: " + client);

        // read default feature toggle values
        List<String> featureDefaults = FileHelper.getLines("systemFeatureToggles/" + client.toLowerCase(), "SystemFeatureToggle.txt");
        runtimeState.scenario.write("Setting " + client + " feature toggles to defaults: " + featureDefaults.toString());

        for (String featureValue : featureDefaults) {
            featureValue = featureValue.trim();
            String feature = featureValue.substring(0, featureValue.length() - 2).trim();
            int active = Integer.valueOf(featureValue.substring(featureValue.length() - 1));

            if (active != dbHelperSystemToggles.getSystemFeatureToggle(feature)) {
                toggles.addToMap("systemFeatureToggles", feature, active);
            }
        }

        // read default sub feature toggle values
        List<String> subFeatureDefaults = FileHelper.getLines("systemFeatureToggles/" + client.toLowerCase(), "SystemSubFeatureToggle.txt");
        runtimeState.scenario.write("Setting " + client + " sub feature toggles to defaults: " + subFeatureDefaults.toString());

        for (String subFeatureValue : subFeatureDefaults) {
            subFeatureValue = subFeatureValue.trim();
            String subFeature = subFeatureValue.substring(0, subFeatureValue.length() - 2).trim();
            int active = Integer.valueOf(subFeatureValue.substring(subFeatureValue.length() - 1));

            if (active != dbHelperSystemToggles.getSystemSubFeatureToggle(subFeature)) {
                toggles.addToMap("systemSubFeatureToggles", subFeature, active);
            }
        }

        apiHelperAdmin.setSystemFeatureToggles();
    }

    public void assertDefaultToggles() throws Exception {
        String client = propertyHelper.getEnv().split("_")[1];
        logger.debug("Reseting System Feature Toggles to: " + client);

        List<String> featureDefaults = FileHelper.getLines("systemFeatureToggles/" + client.toLowerCase(), "SystemFeatureToggle.txt");
        for (String featureValue : featureDefaults) {
            featureValue = featureValue.trim();
            String feature = featureValue.substring(0, featureValue.length() - 2).trim();
            Boolean active = featureValue.substring(featureValue.length() - 1).equals("1");

            if (active) {
                assertTrue("Expected " + feature + " to equal 1", dbHelperSystemToggles.getSystemFeatureToggle(feature) == 1);
            } else {
                assertTrue("Expected " + feature + " to equal 0", dbHelperSystemToggles.getSystemFeatureToggle(feature) == 0);
            }
        }

        List<String> subFeatureDefaults = FileHelper.getLines("systemFeatureToggles/" + client.toLowerCase(), "SystemSubFeatureToggle.txt");
        for (String subFeatureValue : subFeatureDefaults) {
            subFeatureValue = subFeatureValue.trim();
            String subFeature = subFeatureValue.substring(0, subFeatureValue.length() - 2).trim();
            Boolean active = subFeatureValue.substring(subFeatureValue.length() - 1).equals("1");

            if (active) {
                assertTrue("Expected " + subFeatureValue + " to equal 1", dbHelperSystemToggles.getSystemSubFeatureToggle(subFeature) == 1);
            } else {
                assertTrue("Expected " + subFeatureValue + " to equal 0", dbHelperSystemToggles.getSystemSubFeatureToggle(subFeature) == 0);
            }
        }
    }

}
