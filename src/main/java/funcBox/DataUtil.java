package funcBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized safe-navigation helper for heterogeneous data sources.
 *
 * <p>Accepts structured JSON strings or already-materialized object graphs, tokenizes
 * dotted paths only once via a bounded cache, and traverses nested {@link Map},
 * {@link List}, or array nodes defensively. Callers can supply a fallback value or
 * request the deepest successfully resolved node through {@code returnLastSeen}.</p>
 *
 * <p>This class is stateless aside from its path-token cache and is safe for
 * concurrent use.</p>
 */
public final class DataUtil {

    private static final int PATH_CACHE_MAX = 4096;
    private static final Map<String, List<String>> PATH_CACHE = new ConcurrentHashMap<>();

    private DataUtil() {}

    // ...existing code...

    /**
     * Safely traverse nested data structures sourced from either a JSON string or an already-materialized
     * object graph (maps, lists, arrays). Null and empty sources, malformed JSON, missing keys, and out-of-range
     * indexes are guarded so callers can supply a fallback value or request the last successfully resolved node.
     *
     * @param source JSON text or an object graph (Map/List/array) to traverse.
     * @param path   Dot-delimited string path or explicit key/index sequence (List/Object[]).
     * @return The resolved value, {@code defaultValue}, or the last seen node (when requested).
     */
    public static Object safeGet(Object source, Object path) {
        return safeGet(source, path, null, false);
    }

    /**
     * Variant that defaults the fallback to {@code null} while still exposing the last-seen toggle.
     *
     * @param source         JSON string or object graph to inspect
     * @param path           dotted path or explicit key/index sequence
     * @param returnLastSeen whether to surface the last successfully resolved node when traversal fails
     * @return resolved value, {@code null}, or last-seen object based on {@code returnLastSeen}
     */
    public static Object safeGet(Object source, Object path, boolean returnLastSeen) {
        return safeGet(source, path, null, returnLastSeen);
    }

    /**
     * Core traversal entry point powering every overload. Converts JSON strings to object graphs on demand
     * and walks the resulting structure defensively.
     *
     * @param source         JSON string or object graph
     * @param path           dotted path or explicit key/index sequence
     * @param defaultValue   fallback value when traversal fails
     * @param returnLastSeen whether to return the last successfully resolved node instead of {@code defaultValue}
     * @return resolved value, {@code defaultValue}, or last-seen node
     */
    public static Object safeGet(
            Object source,
            Object path,
            Object defaultValue,
            boolean returnLastSeen
    ) {
        if (source == null) {
            return defaultValue;
        }

        Object data = materializeSource(source, defaultValue);
        if (data == null) {
            return defaultValue;
        }

        return traverse(data, path, defaultValue, returnLastSeen);
    }

    /**
     * Converts JSON strings into object graphs; passes objects through untouched.
     *
     * @param source       raw JSON string or object graph
     * @param defaultValue caller-supplied fallback (unused when conversion succeeds)
     * @return object graph ready for traversal; {@code null} if JSON is empty/invalid
     */
    private static Object materializeSource(Object source, Object defaultValue) {
        if (source instanceof String) {
            String json = (String) source;
            if (json.isEmpty()) {
                return null;
            }
            try {
                return new JSONObject(json).toMap();
            } catch (JSONException e) {
                return null;
            }
        }
        return source;
    }

    /**
     * Iteratively walks maps/lists/arrays following the provided path tokens.
     *
     * @param data           non-null object graph root
     * @param path           dot string or explicit sequence of keys/indexes
     * @param defaultValue   fallback value when traversal fails
     * @param returnLastSeen whether to return {@code defaultValue} or deepest resolved node on failure
     * @return resolved value, {@code defaultValue}, or last-seen node
     */
    private static Object traverse(
            Object data,
            Object path,
            Object defaultValue,
            boolean returnLastSeen
    ) {
        if (path == null) {
            return data;
        }

        List<?> keys = normalizePath(path);
        if (keys == null) {
            return returnLastSeen ? data : defaultValue;
        }
        if (keys.isEmpty()) {
            return data;
        }

        Object current = data;
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
     * Normalizes string/list/array path inputs into a token list for traversal.
     *
     * @param path user-supplied path (string/list/array)
     * @return immutable list of tokens, empty list, or {@code null} when input is unsupported
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
     * Returns a cached immutable token list for the given dot path, clearing the cache when it grows too large.
     *
     * @param path dotted path string
     * @return cached or newly computed token list
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
     * Splits a dotted path into immutable tokens without allocating intermediate arrays.
     *
     * @param path dotted path string
     * @return immutable list of tokens
     */
    private static List<String> splitPath(String path) {
        // Pre-count dots to allocate correct capacity (avoid resize)
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
     * Parses numeric keys (either numbers or numeric strings) into zero-based indexes for list/array access.
     *
     * @param key numeric key candidate
     * @return index or {@code -1} when parsing fails/overflows
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
        if (len == 0 || len > 10) {  // 10 chars = max for Integer
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
