# Allure Report Demo

Sample Allure report configuration and screenshots demonstrating QA reporting capabilities across both the Playwright (Python) and Selenium (Java) frameworks in this portfolio.

## What is Allure?

Allure is an open-source test reporting framework that produces rich, interactive HTML reports. It integrates with major test frameworks (pytest, TestNG, JUnit, etc.) and provides:

- Step-by-step execution logs with @Step annotations
- Feature/Story grouping via @Feature and @Story annotations
- Severity classification (@Severity)
- Automatic screenshot attachment on test failure
- API request/response body logging (RestAssured filter)
- Failure categorization (product defects vs. environment issues)
- Timeline view for parallel execution analysis

## Generating Reports

### Prerequisites

Install the Allure command-line tool:

```bash
# macOS
brew install allure

# Windows (Scoop)
scoop install allure

# Linux (via npm)
npm install -g allure-commandline
```

### From Playwright (Python)

```bash
cd playwright-test-demo
source .venv/bin/activate   # Windows: .venv\Scripts\activate

# Run tests and generate Allure results
pytest tests/ -v --alluredir=allure-results

# Serve the report locally
allure serve allure-results

# Or generate a static report
allure generate allure-results -o allure-report --clean
allure open allure-report
```

### From Selenium (Java)

```bash
cd selenium-java-demo

# Run tests (results go to target/allure-results)
mvn clean test

# Serve the report via Maven plugin
mvn allure:serve

# Or generate a static report
mvn allure:report
```

## Configuration Files

### Playwright (pytest)

- **`pytest.ini`** -- Pytest markers and logging configuration
- **`conftest.py`** -- Allure screenshot attachment on failure

### Selenium (Java)

- **`allure.properties`** -- Results directory and link patterns
- **`categories.json`** -- Failure categorization rules:
  - *Product Defects* -- assertion failures
  - *Test Environment Issues* -- timeouts, connectivity
  - *Known Issues* -- skipped tests
  - *Flaky Tests* -- intermittent failures

## Report Sections

- **Overview Dashboard** -- pass/fail/skip breakdown with pie charts and trend lines
- **Suites View** -- tests grouped by @Feature and @Story annotations
- **Timeline** -- execution order and duration for parallel-run analysis
- **Test Details** -- step-by-step logs, attached screenshots, API request/response bodies
- **Categories** -- failure classification (product defects vs. test environment issues)
- **Graphs** -- severity distribution, duration trends, retry statistics

## Sample screenshots

`screenshots/` contains report captures from a recent run so reviewers
can see what the report looks like without running the suite locally:

| File | What it shows |
|---|---|
| `01-overview-dashboard.png` | Top-level pass/fail breakdown with pie charts |
| `02-test-suites.png` | Tests grouped by `@Feature` and `@Story` |
| `03-timeline.png` | Parallel-execution timeline for all workers |
| `04-graphs.png` | Severity, duration, and retry distributions |
| `05-categories.png` | Failures split by category (product bugs vs. env) |
| `06-behaviors.png` | BDD-style view (Feature → Story → Scenario) |
| `07-test-detail.png` | A single test with its `@Step` log + attachment |
