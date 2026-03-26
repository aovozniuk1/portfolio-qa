package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Record representing a single user object from the jsonplaceholder API.
 * <p>
 * Maps to a user in {@code GET /users} or {@code GET /users/{id}}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserData(
        int id,
        String name,
        String username,
        String email,
        String phone,
        String website,
        Address address,
        Company company
) {

    /**
     * Nested address record in the user response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Address(String street, String suite, String city, String zipcode, Geo geo) {
    }

    /**
     * Nested geo record inside address.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geo(String lat, String lng) {
    }

    /**
     * Nested company record in the user response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Company(String name, String catchPhrase, String bs) {
    }
}
