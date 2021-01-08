package mercury.helpers;

import java.util.Arrays;
import java.util.List;

public class FgasQuestions {

    private static String[] FGAS_APPLIANCE_QUESTIONS = {
            "Appliance Type"
            , "Appliance Identification"
            , "Please provide appliance details"
            , "Has receiver level been recorded?"
            , "Quantity of Balls Floating"
            , "Provide Level Indicator %"
            , "Refrigerant Type Used"
            , "New Refrigerant Type Used"
    };

    private static String[] IN_PLANT_WITH_ASSET_QUESTIONS = {
            "Refrigerant Gas Used"
            , "Gas Type"
            , "Other Gas Type"
            , "Notes"
            , "Gas Leakage Code"
            , "Gas Leakage Check Method"
            , "Gas Leak Location"
            , "Action"
            , "Fault Code"
            , "Bottle Number"
            , "Quantity (kg)"
    };

    private static String[] IN_PLANT_WITHOUT_ASSET_QUESTIONS = {
            "Refrigerant Gas Used"
            , "Select a rack"
            , "Notes"
            , "Gas Type"
            , "Other Gas Type"
            , "Notes"
            , "Gas Leakage Code"
            , "Gas Leakage Check Method"
            , "Gas Leak Location"
            , "Action"
            , "Fault Code"
            , "Bottle Number"
            , "Quantity (kg)"
    };

    private static String[] LEAK_CHECK_QUESTIONS = {
            "Refrigerant Source"
            , "Refrigerant Source Location"
            , "Full or Partial Cylinder"
            , "Type of Cylinder"
            , "Cylinder Serial No"
            , "Cylinder Capacity (kg)"
            , "Gas in Cylinder (kg)"
            , "Fully Used"
            , "Gas Installed (kg)"
            , "Destination"
            , "Surplus Type"
            , "Returned To"
    };

    private static String[] MAXIMUM_CHARGE_QUESTIONS = {
            "Leak Check Status"
            , "Leak Check Method"
            , "Leak Check Result Type"
    };

    private static String[] NOT_IN_PLANT_WITH_ASSET_QUESTIONS = {
            "Refrigerant Gas Used"
            , "Select an Asset"
            , "Please enter Asset details (minimum 20 characters)"
            , "Gas Type"
            , "Other Gas Type"
            , "Notes"
            , "Gas Leakage Code"
            , "Gas Leakage Check Method"
            , "Gas Leak Location"
            , "Action"
            , "Fault Code"
            , "Bottle Number"
            , "Quantity (kg)"
    };

    private static String[] NOT_IN_PLANT_WITHOUT_ASSET_QUESTIONS = {
            "Refrigerant Gas Used"
            , "Was Gas Added to a Rack?"
            , "Select an Asset"
            , "Please enter Asset details (minimum 20 characters)"
            , "Select a rack"
            , "Notes"
            , "Gas Type"
            , "Other Gas Type"
            , "Notes"
            , "Gas Leakage Code"
            , "Gas Leakage Check Method"
            , "Gas Leak Location"
            , "Action"
            , "Fault Code"
            , "Bottle Number"
            , "Quantity (kg)"
    };

    private static String[] REFRIGERANT_SOURCE_QUESTIONS = { };     // not in Pauls db ... needs to be populated!

    private static String[] LEAK_SITE_INFORMATION_QUESTIONS = { };     // not in Pauls db ... needs to be populated!


    public static List<String> getGasQuestionsForSection(String section) {
        switch(section) {
        case "Not in Plant with Asset" :
            return Arrays.asList(NOT_IN_PLANT_WITH_ASSET_QUESTIONS);

        case "In Plant with Asset" :
            return Arrays.asList(IN_PLANT_WITH_ASSET_QUESTIONS);

        case "In Plant without Asset" :
            return Arrays.asList(IN_PLANT_WITHOUT_ASSET_QUESTIONS);

        case "Not in Plant without Asset" :
            return Arrays.asList(NOT_IN_PLANT_WITHOUT_ASSET_QUESTIONS);

        case "FGAS Appliance" :
            return Arrays.asList(FGAS_APPLIANCE_QUESTIONS);

        case "Refrigerant Source" :
            return Arrays.asList(REFRIGERANT_SOURCE_QUESTIONS);

        case "Leak Check Questions" :
            return Arrays.asList(LEAK_CHECK_QUESTIONS);

        case "Leak Site Information" :
            return Arrays.asList(LEAK_SITE_INFORMATION_QUESTIONS);

        case "Maximum Charge" :
            return Arrays.asList(MAXIMUM_CHARGE_QUESTIONS);
        }
        return null;
    }

}
