package cn.huamantang.health;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class HealthRecordStore {
    private static final String PREFS = "health_records";
    private static final String KEY_ITEMS = "items";
    private final SharedPreferences preferences;

    public HealthRecordStore(Context context) {
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<HealthRecord> getAll() {
        List<HealthRecord> records = new ArrayList<>();
        String raw = preferences.getString(KEY_ITEMS, "[]");
        try {
            JSONArray array = new JSONArray(raw == null ? "[]" : raw);
            for (int index = 0; index < array.length(); index++) {
                JSONObject item = array.getJSONObject(index);
                records.add(new HealthRecord(
                        item.getLong("id"),
                        item.getLong("timestamp"),
                        item.getString("type"),
                        (float) item.getDouble("value1"),
                        (float) item.optDouble("value2", 0),
                        item.optString("note", "")
                ));
            }
        } catch (JSONException ignored) {
            return new ArrayList<>();
        }
        records.sort((left, right) -> Long.compare(right.timestamp, left.timestamp));
        return records;
    }

    public HealthRecord latest(String type) {
        for (HealthRecord record : getAll()) {
            if (record.type.equals(type)) return record;
        }
        return null;
    }

    public void add(String type, float value1, float value2, String note) {
        long now = System.currentTimeMillis();
        HealthRecord record = new HealthRecord(now, now, type, value1, value2, note);
        List<HealthRecord> records = getAll();
        records.add(record);
        save(records);
    }

    public void delete(long id) {
        List<HealthRecord> records = getAll();
        List<HealthRecord> remaining = new ArrayList<>();
        for (HealthRecord record : records) {
            if (record.id != id) remaining.add(record);
        }
        save(remaining);
    }

    public void clear() {
        preferences.edit().remove(KEY_ITEMS).apply();
    }

    private void save(List<HealthRecord> records) {
        JSONArray array = new JSONArray();
        for (HealthRecord record : records) {
            JSONObject item = new JSONObject();
            try {
                item.put("id", record.id);
                item.put("timestamp", record.timestamp);
                item.put("type", record.type);
                item.put("value1", record.value1);
                item.put("value2", record.value2);
                item.put("note", record.note);
                array.put(item);
            } catch (JSONException ignored) {
                // All fields are primitive or non-null strings, so this cannot occur in normal use.
            }
        }
        preferences.edit().putString(KEY_ITEMS, array.toString()).apply();
    }
}
