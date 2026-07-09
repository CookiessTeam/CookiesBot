package ru.devprizrakk.voidbot.config;

import java.util.Map;

public record Config(Map<String, Object> properties) {

    // ------------------------ String ------------------------

    public String getString(String key) {
        Object value = getNested(key);
        return value != null ? value.toString() : null;
    }

    public String getString(String key, String def) {
        String v = getString(key);
        return v != null ? v : def;
    }

    // ------------------------ Int ------------------------

    public Integer getInt(String key) {
        Object value = getNested(key);
        if (value == null) return null;

        if (value instanceof Number) return ((Number) value).intValue();

        try {
            return Integer.parseInt(value.toString());
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public int getInt(String key, int def) {
        Integer v = getInt(key);
        return v != null ? v : def;
    }

    // ------------------------ Long ------------------------

    public Long getLong(String key) {
        Object value = getNested(key);
        if (value == null) return null;

        if (value instanceof Number) return ((Number) value).longValue();

        try {
            return Long.parseLong(value.toString());
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public long getLong(String key, long def) {
        Long v = getLong(key);
        return v != null ? v : def;
    }

    // ------------------------ Double ------------------------

    public Double getDouble(String key) {
        Object value = getNested(key);
        if (value == null) return null;

        if (value instanceof Number) return ((Number) value).doubleValue();

        try {
            return Double.parseDouble(value.toString());
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public double getDouble(String key, double def) {
        Double v = getDouble(key);
        return v != null ? v : def;
    }

    // ------------------------ Boolean ------------------------

    public Boolean getBoolean(String key) {
        Object value = getNested(key);
        if (value == null) return null;

        if (value instanceof Boolean) return (Boolean) value;

        return Boolean.parseBoolean(value.toString());
    }

    public boolean getBoolean(String key, boolean def) {
        Boolean v = getBoolean(key);
        return v != null ? v : def;
    }

    // ------------------------ Float ------------------------

    public Float getFloat(String key) {
        Object value = getNested(key);
        if (value == null) return null;

        if (value instanceof Number) return ((Number) value).floatValue();

        try {
            return Float.parseFloat(value.toString());
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public float getFloat(String key, float def) {
        Float v = getFloat(key);
        return v != null ? v : def;
    }

    // ------------------------ Set Property ------------------------

    public void set(String key, Object value) {
        String[] keys = key.split("\\.");
        Map<String, Object> temp = properties;

        for (int i = 0; i < keys.length - 1; i++) {
            temp = (Map<String, Object>) temp.get(keys[i]);
        }

        temp.put(keys[keys.length - 1], value);
    }

    // ------------------------ Get Nested ------------------------

    private Object getNested(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> temp = properties;

        for (int i = 0; i < keys.length - 1; i++) {
            Object next = temp.get(keys[i]);
            if (!(next instanceof Map)) return null;
            temp = (Map<String, Object>) next;
        }

        return temp.get(keys[keys.length - 1]);
    }
}
