# QA Automation Portfolio

Two test frameworks and a small Allure sample. Built as a showcase of
everything I reach for on a normal QA automation project — page objects,
API + UI, cross-browser, BDD, parallel execution, CI, Docker Grid,
reporting — without the padding.

## Projects

| Project | Stack | |
|---|---|---|
| [playwright-test-demo](playwright-test-demo/) | Python, Playwright, Pytest, httpx, Pydantic, pytest-bdd, pytest-xdist, Faker | UI + API + BDD. Runs on chromium/firefox/webkit. |
| [selenium-java-demo](selenium-java-demo/) | Java 17, Selenium 4, TestNG + JUnit 5, Cucumber, RestAssured, AssertJ, Lombok | UI + API + BDD + CDP. Maven or Gradle. |
| [allure-report-demo](allure-report-demo/) | Allure | Sample report screenshots for quick review without running the suite. |

## Skills demonstrated

- Page Object Model with a shared `BasePage` (waits, JS helpers, screenshots)
- Thread-safe WebDriver via `ThreadLocal` `DriverFactory`; RemoteWebDriver for Selenium Grid
- Parallel execution: TestNG (`parallel=methods`), JUnit 5, `pytest-xdist -n auto`
- Cross-browser Playwright (chromium / firefox / webkit); Chrome + Firefox nodes in the Selenium Grid
- Data-driven: `@DataProvider`, `@ParameterizedTest`, `pytest.mark.parametrize`, Faker-driven fixtures
- BDD: Cucumber-JVM (Java) and pytest-bdd (Python) — feature files + step defs
- API: RestAssured + AssertJ + JSON-schema validation (Java); httpx + Pydantic models (Python)
- Chrome DevTools Protocol: Selenium 4 CDP example capturing network events
- Reporting: Allure with `@Step / @Feature / @Severity`, environment + categories, screenshots on failure
- Retry of flaky tests: `RetryAnalyzer` (TestNG), `flaky` pytest marker
- Docker: `docker-compose` for Selenium Grid (hub + chrome + firefox nodes)
- BrowserStack hookup: Playwright CDP connection via env vars
- HTTP proxy support: `HTTP_PROXY` routes traffic through Charles / mitmproxy / Fiddler
- CI: GitHub Actions — matrix on JDK 17/21, matrix on chromium/firefox/webkit, Allure artifacts

## Test targets

Everything points at free public endpoints so a reviewer can clone and run with no setup:

- UI: [the-internet.herokuapp.com](https://the-internet.herokuapp.com)
- API: [jsonplaceholder.typicode.com](https://jsonplaceholder.typicode.com)

## Quick start

### Playwright (Python)

```bash
cd playwright-test-demo
python -m venv .venv
source .venv/bin/activate           # Windows: .venv\Scripts\activate
pip install -r requirements.txt
python -m playwright install chromium
pytest tests/ -v                    # or: pytest tests/ -n auto --browser=firefox
```

### Selenium (Java)

```bash
cd selenium-java-demo
mvn clean test                      # TestNG
mvn allure:serve
# or via Gradle:
./gradlew test
```

## Notes for reviewers

- `playwright-test-demo/tests/api/` runs without a browser — useful for a
  "does anything work?" smoke run on a fresh clone.
- `allure-report-demo/screenshots/` has four Allure report screens (suite
  overview, timeline, a test detail with step log, categories) if you'd
  rather not build locally.
- Selenium Grid is optional; pass `-Dselenium.grid.url=...` to switch.
