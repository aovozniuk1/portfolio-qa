package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO representing the full single-user API response from reqres.in.
 * <p>
 * Maps the response of {@code GET /api/users/{id}}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    private UserData data;
    private Support support;

    public UserResponse() {
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public Support getSupport() {
        return support;
    }

    public void setSupport(Support support) {
        this.support = support;
    }

    @Override
    public String toString() {
        return "UserResponse{data=" + data + "}";
    }

    /**
     * Nested support object in the API response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Support {
        private String url;
        private String text;

        public Support() {
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
