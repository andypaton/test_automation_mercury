package mercury.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import mercury.rest.RestService;

public class JsonHelper {

    protected static RestService restService = new RestService();



    /**
     * @param serviceURL - endpoint which hosts the conversion tool
     * @param json - json tring to be converted
     * @return x-www-form-urlencoded version
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String convertJsonToHttpQueryString(String serviceURL, String json) throws ClientProtocolException, IOException {
        String payload = "inputText=" + json + "&inputFormat=json&outputFormat=http";
        String response = restService.sendPostRequest(serviceURL, payload, "");
        return response;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = StringUtils.substringBetween(value.toString(), "[", "]");

            } else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            map.put(key, value);
        }
        return map;
    }

    public static  Map<String, Object> toMap(String object) throws JSONException {
        JSONObject obj = new JSONObject(object);
        return toMap(obj);
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


    @SuppressWarnings("unused")
    public static  Map<String, Object> toMap(String key, JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        Map<String, Object> map = new HashMap<String, Object>();

        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key+"["+ i + "]", value);
        }
        return map;
    }


    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String prettyJson(String uglyJson) throws JsonParseException, JsonMappingException, IOException {
        try {
            JSONObject json = new JSONObject(uglyJson); // Convert text to object
            return json.toString(4);

        } catch (JSONException e) {
            System.err.println("Exception: " + e + "\n\n" + uglyJson);
            return uglyJson;
        }
    }
}
