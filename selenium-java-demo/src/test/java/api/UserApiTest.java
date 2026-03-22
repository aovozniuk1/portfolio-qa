package api;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.UserData;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.RetryAnalyzer;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/**
 * API tests targeting <a href="https://jsonplaceholder.typicode.com">jsonplaceholder.typicode.com</a>.
 * <p>
 * Covers CRUD operations, POJO deserialization, JSON schema validation,
 * status-code checks, chained requests, and DataProvider-driven parametrisation.
 */
@Feature("REST API")
@Story("User Endpoints")
public class UserApiTest extends ApiBaseTest {

    // ------------------------------------------------------------------ //
    //  GET -- users list with schema validation
    // ------------------------------------------------------------------ //

    @Test(groups = {"smoke", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("GET /users returns 200 with a non-empty user list and matches JSON schema")
    public void testGetUsersList() {
        given()
                .spec(requestSpec)
        .when()
                .get("/users")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("$", is(notNullValue()))
                .body("size()", equalTo(10))
                .body("[0].id", is(notNullValue()))
                .body("[0].email", is(notNullValue()))
                .body(matchesJsonSchemaInClasspath("schemas/users-list-schema.json"));
    }

    // ------------------------------------------------------------------ //
    //  GET -- single user with POJO deserialization and schema validation
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users/{id} returns correct user details with POJO deserialization")
    public void testGetSingleUser() {
        int userId = 1;

        UserData user = given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get("/users/{id}")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/single-user-schema.json"))
                .extract()
                .as(UserData.class);

        Assert.assertEquals(user.getId(), userId, "User ID should match");
        Assert.assertNotNull(user.getEmail(), "Email should not be null");
        Assert.assertNotNull(user.getName(), "Name should not be null");
        Assert.assertNotNull(user.getUsername(), "Username should not be null");
        Assert.assertNotNull(user.getAddress(), "Address should not be null");
        Assert.assertNotNull(user.getCompany(), "Company should not be null");
    }

    // ------------------------------------------------------------------ //
    //  GET -- non-existent user returns empty object (200)
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users/{id} returns 404 for a non-existent user")
    public void testGetNonExistentUser() {
        given()
                .spec(requestSpec)
                .pathParam("id", 9999)
        .when()
                .get("/users/{id}")
        .then()
                .statusCode(404);
    }

    // ------------------------------------------------------------------ //
    //  POST -- with POJO request/response
    // ------------------------------------------------------------------ //

    @Test(groups = {"smoke", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST /users creates a new user with POJO serialization and returns 201")
    public void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest("John Doe", "johndoe", "john@example.com");

        CreateUserResponse response = given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post("/users")
        .then()
                .spec(responseSpec)
                .statusCode(201)
                .extract()
                .as(CreateUserResponse.class);

        Assert.assertEquals(response.getName(), "John Doe", "Name should match");
        Assert.assertEquals(response.getUsername(), "johndoe", "Username should match");
        Assert.assertEquals(response.getEmail(), "john@example.com", "Email should match");
        Assert.assertTrue(response.getId() > 0, "ID should be a positive integer");
    }

    // ------------------------------------------------------------------ //
    //  PUT
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("PUT /users/{id} updates user data and returns 200")
    public void testUpdateUser() {
        CreateUserRequest request = new CreateUserRequest("Jane Doe", "janedoe", "jane@example.com");

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(request)
        .when()
                .put("/users/{id}")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("name", equalTo("Jane Doe"))
                .body("username", equalTo("janedoe"))
                .body("email", equalTo("jane@example.com"));
    }

    // ------------------------------------------------------------------ //
    //  PATCH
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("PATCH /users/{id} partially updates user data and returns 200")
    public void testPatchUser() {
        String body = "{\"name\":\"Updated Name\"}";

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .body(body)
        .when()
                .patch("/users/{id}")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("name", equalTo("Updated Name"));
    }

    // ------------------------------------------------------------------ //
    //  DELETE
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("DELETE /users/{id} returns 200")
    public void testDeleteUser() {
        given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .delete("/users/{id}")
        .then()
                .statusCode(200);
    }

    // ------------------------------------------------------------------ //
    //  DataProvider -- parametrized user fetching
    // ------------------------------------------------------------------ //

    @DataProvider(name = "userIds")
    public Object[][] userIds() {
        return new Object[][]{
                {1, "Leanne Graham"},
                {2, "Ervin Howell"},
                {3, "Clementine Bauch"},
        };
    }

    @Test(groups = {"regression", "api"}, dataProvider = "userIds",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users/{id} returns the correct user name for each ID")
    public void testUserById(int userId, String expectedName) {
        given()
                .spec(requestSpec)
                .pathParam("id", userId)
        .when()
                .get("/users/{id}")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("name", equalTo(expectedName));
    }

    // ------------------------------------------------------------------ //
    //  GET /posts -- list with schema validation
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /posts returns 200 with 100 posts and matches JSON schema")
    public void testGetPostsList() {
        given()
                .spec(requestSpec)
        .when()
                .get("/posts")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("size()", equalTo(100))
                .body("[0].userId", is(notNullValue()))
                .body("[0].title", is(notNullValue()))
                .body(matchesJsonSchemaInClasspath("schemas/posts-list-schema.json"));
    }

    // ------------------------------------------------------------------ //
    //  GET /comments -- filtered by postId
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /comments?postId=1 returns comments for post 1 with valid email fields")
    public void testGetCommentsForPost() {
        given()
                .spec(requestSpec)
                .queryParam("postId", 1)
        .when()
                .get("/comments")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].postId", equalTo(1))
                .body("[0].email", is(notNullValue()))
                .body("[0].body", is(notNullValue()));
    }
}
