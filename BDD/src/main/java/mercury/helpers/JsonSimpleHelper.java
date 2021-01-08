package mercury.helpers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonSimpleHelper {

    public static String extractElement(String json, String element) throws Exception {
        JSONParser parser = new JSONParser();
        Object obj =  parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;

        JSONArray msg = (JSONArray) jsonObject.get(element);

        return msg.toString();

    }

    public static JSONArray extractElementArray(String json, String element) throws Exception {
        JSONParser parser = new JSONParser();
        Object obj =  parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;

        JSONArray msg = (JSONArray) jsonObject.get(element);

        return msg;

    }
}
