package api;

import api.models.Post;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Smaller, AssertJ-first counterpart to UserApiTest. Uses the Lombok
 * {@code @Builder} on {@link Post} to build request bodies and AssertJ
 * for the actual assertions.
 */
@Feature("REST API")
@Story("Posts endpoint")
public class PostApiTest extends ApiBaseTest {

    @Test(groups = {"smoke", "api"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /posts returns 100 posts with non-empty titles")
    public void listReturns100Posts() {
        Post[] posts = given()
                .spec(requestSpec)
                .when()
                .get("/posts")
                .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract()
                .as(Post[].class);

        assertThat(posts)
                .hasSize(100)
                .allSatisfy(p -> {
                    assertThat(p.getId()).isPositive();
                    assertThat(p.getUserId()).isBetween(1, 10);
                    assertThat(p.getTitle()).isNotBlank();
                });
    }

    @Test(groups = {"regression", "api"})
    @Severity(SeverityLevel.NORMAL)
    @Description("POST /posts echoes the body built via Lombok @Builder")
    public void createPostEchoesBody() {
        Post payload = Post.builder()
                .userId(1)
                .title("portfolio smoke")
                .body("created through RestAssured + Lombok builder")
                .build();

        Post created = given()
                .spec(requestSpec)
                .contentType("application/json")
                .body(payload)
                .when()
                .post("/posts")
                .then()
                .spec(responseSpec)
                .statusCode(201)
                .extract()
                .as(Post.class);

        assertThat(created)
                .extracting(Post::getTitle, Post::getBody, Post::getUserId)
                .containsExactly(payload.getTitle(), payload.getBody(), payload.getUserId());
        assertThat(created.getId()).isPositive();
    }

    @Test(groups = {"regression", "api"})
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users/{id}/posts is consistent with /posts?userId={id}")
    public void postsForUserAreConsistentAcrossEndpoints() {
        int userId = 2;

        Response nested = given().spec(requestSpec).when().get("/users/" + userId + "/posts");
        Response queryParam = given().spec(requestSpec)
                .queryParam("userId", userId)
                .when().get("/posts");

        List<Post> nestedPosts = List.of(nested.as(Post[].class));
        List<Post> queryPosts = List.of(queryParam.as(Post[].class));

        assertThat(nestedPosts)
                .as("both endpoints return the same set of posts")
                .extracting(Post::getId)
                .containsExactlyInAnyOrderElementsOf(
                        queryPosts.stream().map(Post::getId).toList()
                );
    }
}
