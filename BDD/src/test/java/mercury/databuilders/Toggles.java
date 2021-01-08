package mercury.databuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class Toggles {

    private JSONObject toggles = new JSONObject();

    public Toggles() {
    }

    @SuppressWarnings("unchecked")
    public void put(String tag, Object value) {
        toggles.put(tag, value);
    }

    public boolean getBoolean(String tag) {
        return toggles.get(tag) == null ? false
                : (Boolean) toggles.get(tag);
    }

    public Object get(String tag) {
        return toggles.get(tag);
    }

    @SuppressWarnings("unchecked")
    public void addToMap(String tag, String key, Object value) {
        Map<String, Object> mapping = toggles.get(tag) == null ? new HashMap<String, Object>()
                : (Map<String, Object>) toggles.get(tag);
        mapping.put(key, value);
        toggles.put(tag, mapping);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String tag) {
        return toggles.get(tag) == null ? null
                : (Map<String, Object>) toggles.get(tag);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String tag, Class<T> cls) {
        return toggles.get(tag) == null ? null
                : (List<T>) toggles.get(tag);
    }

    public List<String> getList(String tag) {
        return getList(tag, String.class);
    }

    public void addToList(String tag, Object value) {
        List<Object> myList = toggles.get(tag) == null ? new ArrayList<Object>() : getList(tag, Object.class);
        myList.add(value);
        put(tag, myList);
    }

    public void removeTag(String tag) {
        toggles.remove(tag);
    }

}
