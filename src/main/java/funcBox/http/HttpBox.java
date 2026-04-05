package funcBox.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
 * Simplified HTTP client inspired by JavaScript's fetch().
 *
 * <p>Provides easy-to-use methods for GET, POST, PUT, and DELETE requests
 * without the boilerplate of Java's native HttpClient.</p>
 *
 * <p>Defensive coding:</p>
 * <ul>
 *   <li>Null URLs throw {@link IllegalArgumentException}</li>
 *   <li>Non-2xx responses return null (safe default)</li>
 *   <li>Network errors are wrapped as {@link HttpBoxException}</li>
 * </ul>
 *
 * @author Kishore P
 * @since 1.1.1
 */
public final class HttpBox {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final int TIMEOUT_SECONDS = 10;

    private HttpBox() {
    }

    /**
     * Sends a GET request and returns the response body as a String.
     *
     * @param url the URL to fetch
     * @return response body, or null if status is not 2xx
     * @throws IllegalArgumentException if url is null or blank
     * @throws HttpBoxException           if a network error occurs
     */
    public static String get(String url) {
        return request("GET", url, null, null);
    }

    /**
     * Sends a GET request and parses the response as JSON.
     *
     * @param url the URL to fetch
     * @return parsed JSONObject, or null if status is not 2xx
     * @throws IllegalArgumentException if url is null or blank
     * @throws HttpBoxException           if a network error occurs or JSON parsing fails
     */
    public static JSONObject getJson(String url) {
        String body = get(url);
        if (body == null) {
            return null;
        }
        try {
            return new JSONObject(body);
        } catch (Exception ex) {
            throw new HttpBoxException("Failed to parse JSON response", ex);
        }
    }

    /**
     * Sends a POST request with a String body and returns the response.
     *
     * @param url  the URL to post to
     * @param body the request body (can be null)
     * @return response body, or null if status is not 2xx
     * @throws IllegalArgumentException if url is null or blank
     * @throws HttpBoxException           if a network error occurs
     */
    public static String post(String url, String body) {
        return request("POST", url, body, "application/json");
    }

    /**
     * Sends a POST request with a JSON object and returns the response.
     *
     * @param url     the URL to post to
     * @param jsonObj the JSON object to send
     * @return response body, or null if status is not 2xx
     * @throws IllegalArgumentException if url or jsonObj is null
     * @throws HttpBoxException           if a network error occurs
     */
    public static String postJson(String url, JSONObject jsonObj) {
        if (jsonObj == null) {
            throw new IllegalArgumentException("jsonObj cannot be null");
        }
        return post(url, jsonObj.toString());
    }

    /**
     * Sends a PUT request with a String body and returns the response.
     *
     * @param url  the URL to put to
     * @param body the request body (can be null)
     * @return response body, or null if status is not 2xx
     * @throws IllegalArgumentException if url is null or blank
     * @throws HttpBoxException           if a network error occurs
     */
    public static String put(String url, String body) {
        return request("PUT", url, body, "application/json");
    }

    /**
     * Sends a DELETE request and returns the response.
     *
     * @param url the URL to delete
     * @return response body, or null if status is not 2xx
     * @throws IllegalArgumentException if url is null or blank
     * @throws HttpBoxException           if a network error occurs
     */
    public static String delete(String url) {
        return request("DELETE", url, null, null);
    }

    /**
     * Generic request method supporting custom HTTP methods.
     *
     * @param method  HTTP method (GET, POST, PUT, DELETE, etc.)
     * @param url     the URL
     * @param body    request body (null if none)
     * @param contentType content type header (null for none)
     * @return response body, or null if status is not 2xx
     * @throws IllegalArgumentException if url is null or blank
     * @throws HttpBoxException           if a network error occurs
     */
    public static String request(String method, String url, String body, String contentType) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or blank");
        }

        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(java.time.Duration.ofSeconds(TIMEOUT_SECONDS))
                    .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));

            if (contentType != null && !contentType.isEmpty()) {
                requestBuilder.header("Content-Type", contentType);
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Return null for non-2xx responses (defensive)
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }

            return response.body();
        } catch (IOException | InterruptedException ex) {
            throw new HttpBoxException("Failed to complete HTTP request", ex);
        }
    }

    /**
     * Exception thrown when an HTTP operation fails.
     */
    public static class HttpBoxException extends RuntimeException {
        public HttpBoxException(String message) {
            super(message);
        }

        public HttpBoxException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}


