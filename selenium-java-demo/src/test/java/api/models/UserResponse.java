package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Record representing a wrapper for single-user API responses.
 * <p>
 * Can be used when the API wraps user data in a container object
 * (e.g. {@code {"data": {...}, "support": {...}}}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserResponse(UserData data, Support support) {

    /**
     * Nested support record in the API response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Support(String url, String text) {
    }
}
