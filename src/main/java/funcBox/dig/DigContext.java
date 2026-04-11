package funcBox.dig;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Immutable dig-style lookup context.
 *
 * <p>Use {@link Dig#of(String)} to parse once and reuse this object for
 * repeated lookups.</p>
 *
 * @since 1.1.1
 */
public final class DigContext {
    /**
     * Empty context singleton used when JSON parsing fails or input is null/blank.
     */
    public static final DigContext EMPTY = new DigContext(null);
    private static final int PATH_CACHE_MAX = 4096;
    private static final Map<String, List<String>> PATH_CACHE = new ConcurrentHashMap<>();
    private static final Object MISSING = new Object();
    private final Object root;
    DigContext(Object root) {
        this.root = root;
    }
    /**
     * Builds a context from a raw JSON string.
     *
     * @param json raw JSON input
     * @return parsed context or {@link #EMPTY} when input is null/blank/invalid
     */
    static DigContext fromJson(String json) {
        if (json == null || json.isBlank()) {
            return EMPTY;
        }
        try {
            Object root = new JSONObject(json).toMap();
            return root == null ? EMPTY : new DigContext(root);
        } catch (JSONException e) {
            try {
                Object root = new JSONArray(json).toList();
                return root == null ? EMPTY : new DigContext(root);
            } catch (JSONException ignored) {
                return EMPTY;
            }
        }
    }
    /**
     * Builds a context from either raw JSON, an existing context, or an object graph.
     *
     * @param source string JSON, context, map/list/array, or null
     * @return normalized context instance
     */
    static DigContext fromSource(Object source) {
        if (source == null) {
            return EMPTY;
        }
        if (source instanceof DigContext) {
            return (DigContext) source;
        }
        if (source instanceof String) {
            return fromJson((String) source);
        }
        return new DigContext(source);
    }
    /**
     * Returns the internal root node.
     *
     * @return internal root object (package-private use)
     */
    Object root() {
        return root;
    }

    /**
     * Indicates whether this context contains usable data.
     *
     * @return {@code true} when this context is empty
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Resolves a value from the context using a dot path, list path, or object-array path.
     *
     * @param path path expression
     * @return resolved value or {@code null}
     */
    public Object get(Object path) {
        return safeGet(root, path, null, false);
    }

    /**
     * Resolves a value from the context with a fallback.
     *
     * @param path path expression
     * @param defaultValue fallback value when traversal fails
     * @return resolved value or {@code defaultValue}
     */
    public Object get(Object path, Object defaultValue) {
        return safeGet(root, path, defaultValue, false);
    }

    /**
     * Checks whether a path exists in the current context.
     *
     * @param path path expression
     * @return {@code true} if the path exists (even when value is null)
     */
    public boolean has(Object path) {
        return safeGet(root, path, MISSING, false) != MISSING;
    }

    /**
     * Re-roots the context to a nested node.
     *
     * @param path path expression
     * @return child context or {@link #EMPTY} when missing
     */
    public DigContext scope(Object path) {
        Object sub = get(path);
        if (sub == null) {
            return EMPTY;
        }
        return new DigContext(sub);
    }

    /**
     * Resolves multiple paths in one call.
     *
     * @param paths one or more path strings
     * @return ordered list of resolved values (same order as input paths)
     */
    public List<Object> getAll(String... paths) {
        List<Object> result = new ArrayList<>();
        if (paths == null) {
            return result;
        }
        for (String path : paths) {
            result.add(get(path));
        }
        return result;
    }
    /**
     * Resolves a path and converts the value to string when present.
     *
     * @param path dot path
     * @return string value or {@code null}
     */
    public String getString(String path) {
        Object value = get(path);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * Resolves a path and converts to {@link Integer} when possible.
     *
     * @param path dot path
     * @return integer value or {@code null}
     */
    public Integer getInt(String path) {
        Object value = get(path);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt(((String) value).trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    /**
     * Resolves a path and converts to {@link Long} when possible.
     *
     * @param path dot path
     * @return long value or {@code null}
     */
    public Long getLong(String path) {
        Object value = get(path);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong(((String) value).trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    /**
     * Resolves a path and converts to {@link Double} when possible.
     *
     * @param path dot path
     * @return double value or {@code null}
     */
    public Double getDouble(String path) {
        Object value = get(path);
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble(((String) value).trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    /**
     * Resolves a path and converts to {@link Boolean} when possible.
     *
     * @param path dot path
     * @return boolean value or {@code null}
     */
    public Boolean getBoolean(String path) {
        Object value = get(path);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0.0d;
        }
        if (value instanceof String) {
            String str = ((String) value).trim();
            if ("true".equalsIgnoreCase(str)) {
                return true;
            }
            if ("false".equalsIgnoreCase(str)) {
                return false;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        if (isEmpty()) {
            return "dig({})";
        }
        if (root instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) root;
            if (map.isEmpty()) {
                return "dig({})";
            }
            List<String> preview = new ArrayList<>();
            int count = 0;
            for (Object key : map.keySet()) {
                if (count++ == 6) {
                    preview.add("...");
                    break;
                }
                preview.add(String.valueOf(key));
            }
            return "dig({" + String.join(", ", preview) + "})";
        }
        if (root instanceof List) {
            return "dig([size=" + ((List<?>) root).size() + "])";
        }
        return "dig(" + String.valueOf(root) + ")";
    }
    /**
     * Internal safe traversal engine.
     */
    private static Object safeGet(Object source, Object path, Object defaultValue, boolean returnLastSeen) {
        if (source == null) return defaultValue;
        if (path == null) return source;

        // Optimization: Fast-path for simple single-level string keys
        if (path instanceof String) {
            String s = (String) path;
            if (s.indexOf('.') == -1) {
                if (source instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) source;
                    Object next = map.get(s);
                    if (next == null && !map.containsKey(s)) {
                        return returnLastSeen ? source : defaultValue;
                    }
                    return next;
                }
                // Handle simple index "0", "1" for arrays/lists even without DOT
                int idx = parseIndex(s);
                if (idx >= 0) {
                    if (source instanceof List) {
                        List<?> list = (List<?>) source;
                        return (idx < list.size()) ? list.get(idx) : (returnLastSeen ? source : defaultValue);
                    }
                    if (source.getClass().isArray()) {
                        int len = Array.getLength(source);
                        return (idx < len) ? Array.get(source, idx) : (returnLastSeen ? source : defaultValue);
                    }
                }
            }
        }

        List<?> keys = normalizePath(path);
        if (keys == null) {
            return returnLastSeen ? source : defaultValue;
        }
        if (keys.isEmpty()) {
            return source;
        }
        Object current = source;
        Object lastSeen = current;
        for (Object key : keys) {
            if (current instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) current;
                Object next = map.get(key);
                if (next == null && !map.containsKey(key)) {
                    return returnLastSeen ? lastSeen : defaultValue;
                }
                current = next;
            } else if (current instanceof List) {
                int index = parseIndex(key);
                List<?> list = (List<?>) current;
                if (index < 0 || index >= list.size()) {
                    return returnLastSeen ? lastSeen : defaultValue;
                }
                current = list.get(index);
            } else if (current != null && current.getClass().isArray()) {
                int index = parseIndex(key);
                int length = Array.getLength(current);
                if (index < 0 || index >= length) {
                    return returnLastSeen ? lastSeen : defaultValue;
                }
                current = Array.get(current, index);
            } else {
                return returnLastSeen ? lastSeen : defaultValue;
            }
            lastSeen = current;
        }
        return current;
    }
    /**
     * Normalizes supported path representations into traversal tokens.
     */
    private static List<?> normalizePath(Object path) {
        if (path instanceof String) {
            String strPath = (String) path;
            if (strPath.isEmpty()) {
                return Collections.emptyList();
            }
            return splitPathCached(strPath);
        }
        if (path instanceof List) {
            return (List<?>) path;
        }
        if (path instanceof Object[]) {
            return Arrays.asList((Object[]) path);
        }
        return null;
    }
    /**
     * Returns cached split tokens for dot-paths.
     */
    private static List<String> splitPathCached(String path) {
        List<String> cached = PATH_CACHE.get(path);
        if (cached != null) {
            return cached;
        }
        if (PATH_CACHE.size() >= PATH_CACHE_MAX) {
            PATH_CACHE.clear();
        }
        List<String> split = splitPath(path);
        PATH_CACHE.put(path, split);
        return split;
    }
    /**
     * Splits dot-path text into immutable token list.
     */
    private static List<String> splitPath(String path) {
        int dotCount = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '.') {
                dotCount++;
            }
        }
        List<String> parts = new ArrayList<>(dotCount + 1);
        int start = 0;
        int idx;
        while ((idx = path.indexOf('.', start)) >= 0) {
            parts.add(path.substring(start, idx));
            start = idx + 1;
        }
        parts.add(path.substring(start));
        return Collections.unmodifiableList(parts);
    }
    /**
     * Parses numeric index tokens.
     */
    private static int parseIndex(Object key) {
        if (key instanceof Number) {
            return ((Number) key).intValue();
        }
        if (!(key instanceof String)) {
            return -1;
        }
        String str = (String) key;
        int len = str.length();
        if (len == 0 || len > 10) {
            return -1;
        }
        int value = 0;
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (ch < '0' || ch > '9') {
                return -1;
            }
            int digit = ch - '0';
            if (value > (Integer.MAX_VALUE - digit) / 10) {
                return -1;
            }
            value = value * 10 + digit;
        }
        return value;
    }
}