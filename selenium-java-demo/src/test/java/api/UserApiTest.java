package api;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.UserData;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.RetryAnalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        Assert.assertEquals(user.id(), userId, "User ID should match");
        Assert.assertNotNull(user.email(), "Email should not be null");
        Assert.assertNotNull(user.name(), "Name should not be null");
        Assert.assertNotNull(user.username(), "Username should not be null");
        Assert.assertNotNull(user.address(), "Address should not be null");
        Assert.assertNotNull(user.company(), "Company should not be null");
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

        Assert.assertEquals(response.name(), "John Doe", "Name should match");
        Assert.assertEquals(response.username(), "johndoe", "Username should match");
        Assert.assertEquals(response.email(), "john@example.com", "Email should match");
        Assert.assertTrue(response.id() > 0, "ID should be a positive integer");
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

    // ------------------------------------------------------------------ //
    //  POST -- create and verify user fields (chained)
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST /users creates a user, then verifies all response fields match the request")
    public void testCreateAndVerifyUser() {
        CreateUserRequest request = new CreateUserRequest("Alice Wonder", "alicew", "alice@example.com");

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

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(response.id() > 0, "ID should be a positive integer");
        softAssert.assertEquals(response.name(), "Alice Wonder", "Name should match request");
        softAssert.assertEquals(response.username(), "alicew", "Username should match request");
        softAssert.assertEquals(response.email(), "alice@example.com", "Email should match request");
        softAssert.assertAll();
    }

    // ------------------------------------------------------------------ //
    //  GET -- verify response headers
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users returns Content-Type application/json header")
    public void testResponseHeaders() {
        given()
                .spec(requestSpec)
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }

    // ------------------------------------------------------------------ //
    //  GET -- nested resource access (user posts)
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users/1/posts returns posts all belonging to userId 1")
    public void testNestedResourceAccess() {
        Response response = given()
                .spec(requestSpec)
        .when()
                .get("/users/1/posts")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("size()", greaterThan(0))
                .extract()
                .response();

        List<Integer> userIds = response.jsonPath().getList("userId", Integer.class);
        SoftAssert softAssert = new SoftAssert();
        for (int i = 0; i < userIds.size(); i++) {
            softAssert.assertEquals(userIds.get(i).intValue(), 1,
                    "Post at index " + i + " should belong to userId 1");
        }
        softAssert.assertAll();
    }

    // ------------------------------------------------------------------ //
    //  GET -- verify nested address fields on user
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users/1 and verify nested address fields (street, city, zipcode, geo)")
    public void testGetUserAddressDetails() {
        UserData user = given()
                .spec(requestSpec)
                .pathParam("id", 1)
        .when()
                .get("/users/{id}")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract()
                .as(UserData.class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(user.address(), "Address should not be null");
        softAssert.assertNotNull(user.address().street(), "Street should not be null");
        softAssert.assertNotNull(user.address().city(), "City should not be null");
        softAssert.assertNotNull(user.address().zipcode(), "Zipcode should not be null");
        softAssert.assertNotNull(user.address().geo(), "Geo should not be null");
        softAssert.assertNotNull(user.address().geo().lat(), "Geo lat should not be null");
        softAssert.assertNotNull(user.address().geo().lng(), "Geo lng should not be null");
        softAssert.assertAll();
    }

    // ------------------------------------------------------------------ //
    //  GET -- bulk user validation with SoftAssert (enhanced with address/company)
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /users returns all users, iterate and verify required fields including address and company nested objects")
    public void testBulkUserValidation() {
        UserData[] users = given()
                .spec(requestSpec)
        .when()
                .get("/users")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract()
                .as(UserData[].class);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(users.length > 0, "Users list should not be empty");

        for (int i = 0; i < users.length; i++) {
            UserData user = users[i];
            softAssert.assertTrue(user.id() > 0,
                    "User at index " + i + " should have a positive ID");
            softAssert.assertNotNull(user.name(),
                    "User at index " + i + " should have a name");
            softAssert.assertNotNull(user.username(),
                    "User at index " + i + " should have a username");
            softAssert.assertNotNull(user.email(),
                    "User at index " + i + " should have an email");
            softAssert.assertTrue(user.email().contains("@"),
                    "User at index " + i + " email should contain '@': " + user.email());

            // Validate address nested object
            softAssert.assertNotNull(user.address(),
                    "User at index " + i + " should have an address");
            if (user.address() != null) {
                softAssert.assertNotNull(user.address().street(),
                        "User at index " + i + " address should have a street");
                softAssert.assertNotNull(user.address().city(),
                        "User at index " + i + " address should have a city");
                softAssert.assertNotNull(user.address().zipcode(),
                        "User at index " + i + " address should have a zipcode");
                softAssert.assertNotNull(user.address().geo(),
                        "User at index " + i + " address should have geo coordinates");
                if (user.address().geo() != null) {
                    softAssert.assertNotNull(user.address().geo().lat(),
                            "User at index " + i + " geo should have lat");
                    softAssert.assertNotNull(user.address().geo().lng(),
                            "User at index " + i + " geo should have lng");
                }
            }

            // Validate company nested object
            softAssert.assertNotNull(user.company(),
                    "User at index " + i + " should have a company");
            if (user.company() != null) {
                softAssert.assertNotNull(user.company().name(),
                        "User at index " + i + " company should have a name");
                softAssert.assertNotNull(user.company().catchPhrase(),
                        "User at index " + i + " company should have a catchPhrase");
                softAssert.assertNotNull(user.company().bs(),
                        "User at index " + i + " company should have a bs");
            }
        }
        softAssert.assertAll();
    }

    // ------------------------------------------------------------------ //
    //  POST then GET -- create user and attempt to fetch it (chained)
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST /users creates a user, then GET /users/{id} attempts to fetch it (jsonplaceholder returns 404 for created resources)")
    public void testCreateThenFetchUser() {
        // Step 1: Create a new user
        CreateUserRequest request = new CreateUserRequest("Chain Test User", "chainuser", "chain@test.com");

        CreateUserResponse createResponse = given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post("/users")
        .then()
                .spec(responseSpec)
                .statusCode(201)
                .extract()
                .as(CreateUserResponse.class);

        Assert.assertTrue(createResponse.id() > 0, "Created user should have a positive ID");
        Assert.assertEquals(createResponse.name(), "Chain Test User", "Created user name should match");

        // Step 2: Attempt to fetch the created user
        // Note: jsonplaceholder is a fake API -- newly created resources are not actually persisted,
        // so the GET will return 404 for any ID > 10
        given()
                .spec(requestSpec)
                .pathParam("id", createResponse.id())
        .when()
                .get("/users/{id}")
        .then()
                .statusCode(anyOf(equalTo(200), equalTo(404)));
    }

    // ------------------------------------------------------------------ //
    //  GET -- verify uniqueness of all user IDs
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users returns all users and verifies all IDs are unique")
    public void testAllUserIdsAreUnique() {
        Response response = given()
                .spec(requestSpec)
        .when()
                .get("/users")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract()
                .response();

        List<Integer> ids = response.jsonPath().getList("id", Integer.class);
        Set<Integer> uniqueIds = new HashSet<>(ids);

        Assert.assertEquals(uniqueIds.size(), ids.size(),
                "All user IDs should be unique. Found " + ids.size()
                        + " total but only " + uniqueIds.size() + " unique");
    }

    // ------------------------------------------------------------------ //
    //  GET -- verify uniqueness of all user emails
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users returns all users and verifies all emails are unique")
    public void testAllUserEmailsAreUnique() {
        Response response = given()
                .spec(requestSpec)
        .when()
                .get("/users")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract()
                .response();

        List<String> emails = response.jsonPath().getList("email", String.class);
        Set<String> uniqueEmails = new HashSet<>(emails);

        Assert.assertEquals(uniqueEmails.size(), emails.size(),
                "All user emails should be unique. Found " + emails.size()
                        + " total but only " + uniqueEmails.size() + " unique");
    }

    // ------------------------------------------------------------------ //
    //  GET /posts -- query parameter filtering by userId
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /posts?userId=1 returns only posts belonging to userId 1")
    public void testPostsFilterByUserId() {
        Response response = given()
                .spec(requestSpec)
                .queryParam("userId", 1)
        .when()
                .get("/posts")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .body("size()", greaterThan(0))
                .extract()
                .response();

        List<Integer> userIds = response.jsonPath().getList("userId", Integer.class);
        SoftAssert softAssert = new SoftAssert();
        for (int i = 0; i < userIds.size(); i++) {
            softAssert.assertEquals(userIds.get(i).intValue(), 1,
                    "Post at index " + i + " should have userId=1, got: " + userIds.get(i));
        }
        softAssert.assertAll();
    }

    // ------------------------------------------------------------------ //
    //  GET -- verify response time is under a threshold
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /users response time should be under 5000ms")
    public void testResponseTimeUnderThreshold() {
        long maxResponseTimeMs = 5000;

        Response response = given()
                .spec(requestSpec)
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .extract()
                .response();

        long responseTime = response.getTime();
        Assert.assertTrue(responseTime < maxResponseTimeMs,
                "Response time should be under " + maxResponseTimeMs
                        + "ms, but was " + responseTime + "ms");
    }

    // ------------------------------------------------------------------ //
    //  POST -- create multiple users and verify each
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("POST /users creates multiple users sequentially and verifies each response")
    public void testCreateMultipleUsers() {
        CreateUserRequest[] requests = {
                new CreateUserRequest("User One", "userone", "one@example.com"),
                new CreateUserRequest("User Two", "usertwo", "two@example.com"),
                new CreateUserRequest("User Three", "userthree", "three@example.com"),
        };

        SoftAssert softAssert = new SoftAssert();

        for (int i = 0; i < requests.length; i++) {
            CreateUserResponse response = given()
                    .spec(requestSpec)
                    .body(requests[i])
            .when()
                    .post("/users")
            .then()
                    .spec(responseSpec)
                    .statusCode(201)
                    .extract()
                    .as(CreateUserResponse.class);

            softAssert.assertTrue(response.id() > 0,
                    "User " + i + " should have a positive ID");
            softAssert.assertEquals(response.name(), requests[i].name(),
                    "User " + i + " name should match request");
            softAssert.assertEquals(response.username(), requests[i].username(),
                    "User " + i + " username should match request");
            softAssert.assertEquals(response.email(), requests[i].email(),
                    "User " + i + " email should match request");
        }
        softAssert.assertAll();
    }

    // ------------------------------------------------------------------ //
    //  GET /posts -- pagination-like behavior with _start and _limit
    // ------------------------------------------------------------------ //

    @Test(groups = {"regression", "api"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /posts with _start and _limit simulates pagination and verifies page sizes")
    public void testPostsPaginationBehavior() {
        SoftAssert softAssert = new SoftAssert();

        int pageSize = 10;

        // Page 1: posts 0-9
        Response page1 = given()
                .spec(requestSpec)
                .queryParam("_start", 0)
                .queryParam("_limit", pageSize)
        .when()
                .get("/posts")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract()
                .response();

        List<Integer> page1Ids = page1.jsonPath().getList("id", Integer.class);
        softAssert.assertEquals(page1Ids.size(), pageSize,
                "Page 1 should return " + pageSize + " posts");

        // Page 2: posts 10-19
        Response page2 = given()
                .spec(requestSpec)
                .queryParam("_start", pageSize)
                .queryParam("_limit", pageSize)
        .when()
                .get("/posts")
        .then()
                .spec(responseSpec)
                .statusCode(200)
                .extract()
                .response();

        List<Integer> page2Ids = page2.jsonPath().getList("id", Integer.class);
        softAssert.assertEquals(page2Ids.size(), pageSize,
                "Page 2 should return " + pageSize + " posts");

        // Verify no overlap between pages
        Set<Integer> page1Set = new HashSet<>(page1Ids);
        for (int id : page2Ids) {
            softAssert.assertFalse(page1Set.contains(id),
                    "Post ID " + id + " from page 2 should not appear in page 1");
        }

        // Verify page 2 IDs are greater than page 1 IDs (ordered)
        int maxPage1 = page1Ids.stream().mapToInt(Integer::intValue).max().orElse(0);
        int minPage2 = page2Ids.stream().mapToInt(Integer::intValue).min().orElse(0);
        softAssert.assertTrue(minPage2 > maxPage1,
                "Page 2 minimum ID (" + minPage2 + ") should be > page 1 maximum ID (" + maxPage1 + ")");

        softAssert.assertAll();
    }
}
