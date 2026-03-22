# QA Automation Portfolio

Senior-level test automation frameworks demonstrating enterprise QA engineering practices across multiple languages, tools, and testing paradigms.

## Projects

| Project | Stack | Description |
|---------|-------|-------------|
| [playwright-test-demo](playwright-test-demo/) | Python, Playwright, Pytest | E2E framework with Page Object Model, 15+ data-driven tests, Allure reporting, tracing support |
| [selenium-java-demo](selenium-java-demo/) | Java, Selenium 4, TestNG, RestAssured | UI + API framework with DriverFactory, POJO deserialization, JSON schema validation, parallel execution |
| [allure-report-demo](allure-report-demo/) | Allure | Report configuration, categories, environment setup, and sample screenshots |

## Skills Demonstrated

- **Page Object Model** -- BasePage with reusable methods, clean separation of concerns, fluent API
- **Thread-safe WebDriver management** -- DriverFactory with ThreadLocal for parallel test execution
- **Data-driven testing** -- pytest parametrize (Python), TestNG DataProvider (Java), external test data
- **API testing** -- RestAssured with POJO deserialization, JSON schema validation, chained requests
- **Reporting** -- Allure integration with @Step, @Feature, @Severity annotations, failure categorization
- **Configuration management** -- .env / config.properties with environment overrides
- **Test reliability** -- RetryAnalyzer for flaky tests, explicit/fluent waits, proper fixture scoping
- **Custom listeners** -- TestNG TestListener with automatic screenshot capture and logging
- **Tracing & debugging** -- Playwright tracing support for post-mortem test analysis
- **CI-ready** -- structured for Jenkins / GitHub Actions integration with parallel execution support

## Test Targets

All tests run against public demo sites -- no infrastructure setup required:

- **UI**: [https://the-internet.herokuapp.com](https://the-internet.herokuapp.com)
- **API**: [https://reqres.in](https://reqres.in)

## Quick Start

Each project has its own README with detailed setup and execution instructions.

### Playwright (Python)

```bash
cd playwright-test-demo
python -m venv .venv
source .venv/bin/activate   # Windows: .venv\Scripts\activate
pip install -r requirements.txt
playwright install chromium
pytest tests/ -v
```

### Selenium (Java)

```bash
cd selenium-java-demo
mvn clean test
mvn allure:serve
```

## Test Coverage

| Framework  | UI Tests | API Tests | Total |
|------------|----------|-----------|-------|
| Playwright | 24       | --        | 24    |
| Selenium   | 10       | 12        | 22    |
| **Total**  | **34**   | **12**    | **46**|
