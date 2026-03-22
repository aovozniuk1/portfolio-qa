# Selenium Java Demo

Enterprise-grade test automation framework built with **Selenium WebDriver 4**, **TestNG**, **RestAssured**, and **Allure**.

## Tech Stack

- **Selenium WebDriver 4** -- browser automation with thread-safe DriverFactory
- **TestNG** -- test framework with groups, DataProvider, parallel execution, and custom listeners
- **RestAssured** -- fluent REST API testing with POJO deserialization and JSON schema validation
- **Allure** -- rich HTML test reporting with @Step, @Severity, @Feature annotations
- **WebDriverManager** -- automatic browser driver management
- **Jackson** -- JSON serialisation / deserialisation for API request/response POJOs

## Architecture Highlights

- **DriverFactory** -- ThreadLocal-based WebDriver management for parallel-safe execution
- **ConfigManager** -- Singleton properties reader with system-property overrides (`-Dkey=value`)
- **RetryAnalyzer** -- Configurable retry mechanism for flaky test handling
- **TestListener** -- Custom TestNG listener with automatic screenshot capture on failure
- **BasePage** -- Abstract base with explicit waits, fluent waits, JS helpers, and @Step annotations
- **POJO Models** -- Type-safe API response deserialization (UserData, UserResponse, CreateUserRequest/Response)
- **JSON Schema Validation** -- API response structure validation against JSON schema files

## Project Structure

```
src/test/java/
  config/
    ConfigReader.java          # Singleton properties reader with system-property overrides
    DriverFactory.java         # ThreadLocal WebDriver factory for parallel execution
  pages/
    BasePage.java              # Abstract base: waits, JS helpers, screenshots, @Step
    LoginPage.java             # /login page object
    SecureAreaPage.java        # /secure page object
    DynamicControlsPage.java   # /dynamic_controls page object
    DropdownPage.java          # /dropdown page object with Select support
  tests/
    BaseTest.java              # DriverFactory setup/teardown, screenshot on failure
    LoginTest.java             # Login tests (valid, logout, DataProvider negatives)
    DynamicControlsTest.java   # Checkbox remove/add, input enable tests
    DropdownTest.java          # Dropdown selection tests with DataProvider
  api/
    ApiBaseTest.java           # RestAssured specs with Allure filter
    UserApiTest.java           # CRUD + POJO deserialization + schema validation + register
    models/
      UserData.java            # POJO for user data object
      UserResponse.java        # POJO for single-user API response
      CreateUserRequest.java   # POJO for POST /users request body
      CreateUserResponse.java  # POJO for POST /users response body
  utils/
    RetryAnalyzer.java         # TestNG retry for flaky tests (configurable count)
    TestListener.java          # Custom TestNG listener with Allure screenshot on failure
    AllureUtils.java           # Screenshot & text attachment helpers
src/test/resources/
  config.properties            # Base URL, browser, timeouts, retry config
  testng.xml                   # Suite with smoke/regression groups and parallel support
  allure.properties            # Allure results directory and link patterns
  categories.json              # Allure failure categorization
  schemas/
    single-user-schema.json    # JSON schema for GET /users/{id}
    users-list-schema.json     # JSON schema for GET /users?page={n}
```

## Prerequisites

- Java 11+
- Maven 3.6+
- Chrome browser (for UI tests)

## Running Tests

```bash
# All tests via testng.xml
mvn clean test

# Headed mode
mvn clean test -Dheadless=false

# Smoke tests only
mvn clean test -Dgroups=smoke

# API tests only
mvn clean test -Dgroups=api

# Regression tests only
mvn clean test -Dgroups=regression

# Run with Firefox
mvn clean test -Dbrowser=firefox

# Parallel execution (configured in testng.xml)
mvn clean test -Dparallel=methods -DthreadCount=3

# Generate Allure report
mvn allure:serve
```

## Configuration

Properties in `src/test/resources/config.properties` can be overridden via system properties (`-Dkey=value`):

| Property             | Default                                    | Description               |
|----------------------|--------------------------------------------|---------------------------|
| `base.url`           | `https://the-internet.herokuapp.com`       | UI test target URL        |
| `api.base.url`       | `https://reqres.in/api`                    | API test base URI         |
| `browser`            | `chrome`                                   | Browser (chrome, firefox) |
| `headless`           | `true`                                     | Headless mode             |
| `explicit.wait`      | `10`                                       | Explicit wait (seconds)   |
| `page.load.timeout`  | `30`                                       | Page load timeout (s)     |
| `retry.count`        | `1`                                        | Retry failed tests        |

## Allure Report Categories

The `categories.json` file classifies test failures into:

- **Product Defects** -- assertion failures indicating real bugs
- **Test Environment Issues** -- timeouts, connectivity, WebDriver problems
- **Known Issues** -- skipped tests due to known limitations
- **Flaky Tests** -- intermittent failures
