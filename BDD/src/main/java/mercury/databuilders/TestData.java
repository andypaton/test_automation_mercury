package mercury.databuilders;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class TestData {

    @Autowired TestDataListener testDataListener;
    private JSONObject testData = new JSONObject();

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.UK);

    public TestData() {
    }

    public boolean tagExist(String tag) {
        return testData.get(tag) == null ? false : true;
    }

    @SuppressWarnings("unchecked")
    public void addStringTag(String tag, String value) {
        testData.put(tag, value);
        testDataListener.listen(tag, value);
    }

    @SuppressWarnings("unchecked")
    public void addIntegerTag(String tag, Integer value) {
        testData.put(tag, Integer.valueOf(value));
        testDataListener.listen(tag, value);
    }

    @SuppressWarnings("unchecked")
    public void addDoubleTag(String tag, Double value) {
        testData.put(tag, Double.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    public void addTimestampTag(String tag, Timestamp value) {
        String dateValue = (value != null ? value.toString() : null);
        testData.put(tag, dateValue);
    }

    @SuppressWarnings("unchecked")
    public void addBooleanTag(String tag, Boolean value) {
        testData.put(tag, Boolean.valueOf(value));
    }

    @SuppressWarnings("rawtypes")
    public Object getTag(String tag) {
        Map.Entry pair = (Map.Entry) testData.get(tag);
        return pair.getValue();
    }

    @SuppressWarnings("unchecked")
    public void put(String tag, Object value) {
        if (value instanceof Date) {
            testData.put(tag, DATE_FORMAT.format(value));
        } else if (value.toString().startsWith("@")) {
            // this looks like a reference to an object
            Gson gson = new Gson();
            testData.put(tag, gson.toJson(value));
        } else {
            testData.put(tag, value);
        }
    }

    public String getString(String tag) {
        String value = String.valueOf(testData.get(tag));
        return "null".equals(value) ? null
                : value;
    }

    public Boolean getBooleanTrueFalseOrNull(String tag) {
        return (Boolean) testData.get(tag);
    }

    public boolean getBoolean(String tag) {
        return testData.get(tag) == null ? false
                : (Boolean) testData.get(tag);
    }

    public Integer getInt(String tag) {
        String value = String.valueOf(testData.get(tag));
        return "null".equals(value) ? null
                : (Integer.parseInt(value));
    }

    public Timestamp getTimestamp(String tag) {
        return testData.get(tag) == null ? null
                : (Timestamp) testData.get(tag);
    }

    /**
     * Get tag as Class object
     * @param tag
     * @param cls
     * @return
     */
    public Object getClass(String tag, Class<?> cls) {
        Gson gson = new Gson();
        return testData.get(tag) == null ? null
                : gson.fromJson((String) testData.get(tag), cls);
    }

    public Float getFloat(String tag) {
        String value = String.valueOf(testData.get(tag));
        return "null".equals(value) ? null
                : Float.valueOf(value);
    }

    public Double getDouble(String tag) {
        return (Double) testData.get(tag);
    }

    public Date getDate(String tag) {
        try {
            return DATE_FORMAT.parse(String.valueOf(testData.get(tag)));
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    public Object get(String tag) {
        return testData.get(tag);
    }

    public void reset() {
        testData = new JSONObject();
    }

    public void set(String testData) throws ParseException {
        JSONParser parser = new JSONParser();
        this.testData = (JSONObject) parser.parse(testData);
    }

    @Override
    public String toString() {
        StringWriter output = new StringWriter();
        try {
            testData.writeJSONString(output);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return output.toString();
    }

    @SuppressWarnings("unchecked")
    public void addArray(String tag, List<String> dataArray) {
        String json = new Gson().toJson(dataArray);
        testData.put(tag, json);
    }

    @SuppressWarnings("unchecked")
    public void putArray(String tag, List<String> dataArray) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put(tag, dataArray);
        testData.putAll(map);
    }

    @SuppressWarnings("unchecked")
    public List<String> getArray(String tag) {
        List<String> tagData = (List<String>) testData.get(tag);
        return tagData;
    }

    public List<Integer> getIntList(String tag) {
        @SuppressWarnings("unchecked")
        List<Integer> tagData = (List<Integer>) testData.get(tag);
        return tagData;
    }

    /**
     * save NEW Map
     *
     * @param tag
     * @param dataMap
     */
    @SuppressWarnings("unchecked")
    public void putMap(String tag, Map<String, Object> dataMap) {
        testData.putAll(dataMap);
    }

    /**
     * Add a value to an existing Map
     *
     * @param tag
     * @param dataMap
     */
    @SuppressWarnings("unchecked")
    public void addMap(String tag, Map<String, Object> dataMap) {
        // Get the existing map update
        List<Map<String, Object>> tagData = new ArrayList<Map<String, Object>>();
        tagData.add(dataMap);
        if (testData.get(tag) != null) {
            tagData.addAll((List<Map<String, Object>>) testData.get(tag));
        }

        // Now save it back
        HashMap<Object, Object> map = new HashMap<>();
        map.put(tag, tagData);
        testData.putAll(map);
    }

    @SuppressWarnings("unchecked")
    public void addAllMap(String tag, List<Map<String, Object>> dataMap) {
        // Get the existing map update
        List<Map<String, Object>> tagData = new ArrayList<Map<String, Object>>();
        tagData.addAll(dataMap);
        if (testData.get(tag) != null) {
            tagData.addAll((List<Map<String, Object>>) testData.get(tag));
        }
        // Now save it back
        HashMap<Object, Object> map = new HashMap<>();
        map.put(tag, tagData);
        testData.putAll(map);
    }

    /**
     * Replaces the existing map with a new one
     *
     * @param tag
     * @param dataMap
     */
    @SuppressWarnings("unchecked")
    public void putListMap(String tag, List<Map<String, Object>> dataMap) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put(tag, dataMap);
        testData.putAll(map);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getListMap(String tag) {
        List<Map<String, Object>> tagData = (List<Map<String, Object>>) testData.get(tag);
        return tagData;
    }

    public void copy(TestData testDataRequirements) {
        this.testData = testDataRequirements.testData;
    }

    @SuppressWarnings("unchecked")
    public void addToMap(String tag, String key, Object value) {
        Map<String, Object> mapping = testData.get(tag) == null ? new HashMap<String, Object>()
                : (Map<String, Object>) testData.get(tag);
        mapping.put(key, value);
        testData.put(tag, mapping);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String tag) {
        return testData.get(tag) == null ? null
                : (Map<String, Object>) testData.get(tag);
    }

    @SuppressWarnings("unchecked")
    public List<LinkedHashMap<String, Object>> getLinkedListMap(String tag) {
        return testData.get(tag) == null ? null
                : (List<LinkedHashMap<String, Object>>) testData.get(tag);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String tag, Class<T> cls) {
        return testData.get(tag) == null ? null
                : (List<T>) testData.get(tag);
    }

    public void addToList(String tag, Object value) {
        List<Object> myList = testData.get(tag) == null ? new ArrayList<Object>() : getList(tag, Object.class);
        myList.add(value);
        put(tag, myList);
    }

    public String getTestData() {
        return this.testData.toJSONString().replaceAll("\"screenshots\":\\[.*\\],", ""); // removing any screenshots from the result
    }

    public BigDecimal getBigDecimal(String tag) {
        return (BigDecimal) testData.get(tag);
    }

    public String getTestDataPretty() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.testData.toJSONString());
    }

    public void removeTag(String tag) {
        Object value = testData.get(tag);
        testData.remove(tag);
        testDataListener.unListen(tag, value);
    }

}
