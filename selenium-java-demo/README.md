# Selenium Java Demo

UI + API test framework in Java. Runs under TestNG by default, with a
parallel JUnit 5 suite and a Cucumber BDD runner; builds with Maven or
Gradle. Target app: [the-internet.herokuapp.com](https://the-internet.herokuapp.com);
API target: [jsonplaceholder.typicode.com](https://jsonplaceholder.typicode.com).

## Tech stack

- Java 17, records for API DTOs
- Selenium WebDriver 4 (Selenium Manager — no WebDriverManager needed)
- TestNG (primary) + JUnit 5 (secondary suite under `src/test/java/junit`)
- Cucumber-JVM 7 for a small BDD layer
- RestAssured for REST tests, with Jackson + JSON schema validation
- AssertJ for readable assertions
- Lombok on select DTOs (records are used where possible)
- Allure: `@Step`, `@Feature`, `@Severity`, environment + categories
- SLF4J logging
- Maven or Gradle (both build files commit'd; CI runs Maven)

## Architecture

- `DriverFactory` — ThreadLocal WebDriver, supports local + RemoteWebDriver (Selenium Grid)
- `ConfigReader` — singleton properties + `-Dkey=value` overrides
- `BasePage` — explicit/fluent waits, JS helpers, screenshot helpers, `@Step`s
- `RetryAnalyzer` — configurable TestNG retries
- `TestListener` — screenshot-on-failure, log attachment to Allure
- `CdpTest` — Chrome DevTools Protocol example (network log capture)
- `JsonPlaceholderApiTest` — RestAssured + AssertJ + schema validation

## Layout

```
src/main/java/
  config/
    ConfigReader.java
    DriverFactory.java
  pages/                       # shared BasePage + page objects
src/test/java/
  tests/                       # TestNG tests (UI)
  api/                         # RestAssured + schema validation
  junit/                       # JUnit 5 suite (mirrors the core login + dropdown flows)
  bdd/
    CucumberRunner.java        # JUnit-runner for Cucumber
    steps/                     # step definitions
  cdp/
    CdpNetworkTest.java        # Selenium 4 CDP example
  utils/
src/test/resources/
  config.properties
  testng.xml
  junit-platform.properties
  allure.properties
  categories.json
  environment.properties
  features/                    # .feature files
  schemas/                     # JSON schema files
```

## Prerequisites

- Java 17+
- Maven 3.8+ (or Gradle 8+; `gradlew` is included)
- Chrome (for local UI runs)

## Run

### TestNG suite (Maven)

```bash
mvn clean test
mvn clean test -Dheadless=false
mvn clean test -Dgroups=smoke
mvn clean test -Dgroups=api
mvn clean test -Dbrowser=firefox
mvn clean test -Dparallel=methods -DthreadCount=4
```

### Same, via Gradle

```bash
./gradlew test
./gradlew test -Dgroups=smoke
./gradlew bddTest            # Cucumber runner
./gradlew junitTest          # JUnit 5 suite
```

### Selenium Grid (Docker)

```bash
docker compose up -d         # hub + chrome + firefox nodes
mvn clean test -Dselenium.grid.url=http://localhost:4444
```

### Allure report

```bash
mvn allure:serve
# or: ./gradlew allureServe
```

## Config

`src/test/resources/config.properties` — override any value with `-Dkey=value`:

| Property | Default | |
|---|---|---|
| `base.url` | the-internet.herokuapp.com | UI target |
| `api.base.url` | jsonplaceholder.typicode.com | API target |
| `browser` | chrome | chrome / firefox |
| `headless` | true | |
| `explicit.wait` | 10 | seconds |
| `page.load.timeout` | 30 | seconds |
| `retry.count` | 2 | TestNG retries for flaky tests |
| `selenium.grid.url` | — | if set, uses RemoteWebDriver |

## Notes

- `allure-report-demo/` in the repo root holds sample screenshots of the generated report for reviewers who can't run the suite locally.
- If you see `chromedriver` version errors on a very recent Chrome, bump Selenium to `4.28+`; Selenium Manager auto-resolves driver versions once it knows about the new Chrome.
