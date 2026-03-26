package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Record for the POST /users request body (jsonplaceholder).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateUserRequest(String name, String username, String email) {
}
