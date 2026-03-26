package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Record for the POST /users response body (jsonplaceholder).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateUserResponse(String name, String username, String email, int id) {
}
