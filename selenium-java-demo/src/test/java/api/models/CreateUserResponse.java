package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO for the POST /users response body (jsonplaceholder).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse {

    private String name;
    private String username;
    private String email;
    private int id;

    public CreateUserResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CreateUserResponse{id=" + id + ", name='" + name + "', username='" + username + "'}";
    }
}
