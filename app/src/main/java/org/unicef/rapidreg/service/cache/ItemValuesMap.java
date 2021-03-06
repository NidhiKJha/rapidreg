package org.unicef.rapidreg.service.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.utils.TextUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemValuesMap implements Serializable {
    private Map<String, Object> values;

    public ItemValuesMap() {
        values = new HashMap<>();
    }

    public ItemValuesMap(Map<String, Object> values) {
        this.values = values;
    }

    public static ItemValuesMap fromJson(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> values = new Gson().fromJson(json, type);
        return new ItemValuesMap(values);
    }

    public void addItem(String itemKey, Object itemValue) {
        values.put(itemKey, itemValue);
    }

    public void removeItem(String itemKey) {
        values.remove(itemKey);
    }

    public void addBooleanItem(String itemKey, Boolean value) {
        values.put(itemKey, value);
    }

    public void addStringItem(String itemKey, String value) {
        values.put(itemKey, value);
    }

    public void addNumberItem(String itemKey, Number value) {
        values.put(itemKey, value);
    }

    public void addListItem(String itemkey, List value) {
        values.put(itemkey, value);
    }

    public void addLinkedHashMap(String itemKey, LinkedHashMap map) {
        values.put(itemKey, map);
    }

    public Object getAsObject(String key) {
        if (values.get(key) == null) {
            return null;
        }

        return values.get(key);
    }

    public Boolean getAsBoolean(String key) {
        if (values.get(key) == null) {
            return Boolean.valueOf(null);
        }
        return Boolean.valueOf(values.get(key).toString());
    }

    public String getAsString(String key) {
        if (values.get(key) == null) {
            return null;
        }
        return values.get(key).toString();
    }

    public Long getAsLong(String key) {
        if (values.get(key) == null) {
            return null;
        }
        return Double.valueOf(values.get(key).toString()).longValue();
    }

    public Integer getAsInt(String key) {
        if (values.get(key) == null || "".equals(values.get(key).toString().trim())) {
            return null;
        }
        return Double.valueOf(values.get(key).toString()).intValue();
    }

    public List<String> getAsList(String key) {
        Object o = values.get(key);
        if (o == null) {
            return new ArrayList<>();
        }

        return (List<String>) o;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public List<Map<String, Object>> getChildrenAsJsonArray(String childName) {
        if (values.containsKey(childName)) {
            return (List<Map<String, Object>>) values.get(childName);
        }
        return null;
    }

    public <T extends Object> LinkedHashMap<String, T> getChildrenAsLinkedHashMap(String childName) {
        if (values.containsKey(childName)) {
            return (LinkedHashMap<String, T>) values.get(childName);
        }
        return null;
    }

    public void addChild(String childName, Map<String, Object> child) {
        List<Map<String, Object>> children = getChildrenAsJsonArray(childName);
        if (children == null) {
            children = new ArrayList<>();
            addChildren(childName, children);
        }
        children.add(child);
    }

    public int getChildrenSize(String childName) {
        if (getChildrenAsJsonArray(childName) == null) {
            return 0;
        }
        return getChildrenAsJsonArray(childName).size();
    }

    public ItemValuesMap getChildAsItemValues(String childName, int index) {
        List<Map<String, Object>> childrenAsJsonArray = getChildrenAsJsonArray(childName);
        Map<String, Object> child;
        try {
            child = childrenAsJsonArray.get(index);
        } catch (IndexOutOfBoundsException e) {
            child = new HashMap<>();
            childrenAsJsonArray.add(child);
        }
        return new ItemValuesMap(child);
    }

    public void addChildren(String childName, List<Map<String, Object>> children) {
        values.put(childName, children);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public static class RecordProfile {
        public static final String ID_NORMAL_STATE = "_id_normal_state";
        public static final String REGISTRATION_DATE = "_registration_date";
        public static final String ID = "_primary_id";
        //by no means Incident links field should be tampered as it contains server side internal IDs
        public static final String INCIDENT_LINKS = "incident_links";
    }

    public String concatMultiStringsWithBlank(String... keys) {
        StringBuilder result = new StringBuilder();
        for (String key : keys) {
            if (has(key)) {
                result.append(getAsString(key)).append(" ");
            }
        }
        return result.toString().trim();
    }

    public ItemValuesMap copy() {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return new ItemValuesMap(result);
    }

    public boolean hasNoteAlerts() {
        boolean hasNoteAlert = false;

        if (!TextUtils.isEmpty(this.getAsString(RecordModel.ALERT_KEY))) {
            for ( Map alert : this.getChildrenAsJsonArray(RecordModel.ALERT_KEY)) {
                if (alert.get(RecordModel.ALERT_PROP).equals(RecordModel.ALERT_NOTE_TYPE)) {
                    hasNoteAlert = true;
                }

            }
        }

        return hasNoteAlert;
    }
}
