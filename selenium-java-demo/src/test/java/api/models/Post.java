package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for /posts on jsonplaceholder.
 * <p>
 * Using Lombok here rather than a record so we can demo @Builder for
 * request bodies and keep a mutable shape that Jackson can populate.
 * The existing user/address models stay on records; this is intentional
 * — both styles live side by side.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
    private Integer id;
    private Integer userId;
    private String title;
    private String body;
}
