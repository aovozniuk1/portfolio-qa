# Playwright Test Demo

Enterprise-grade end-to-end test automation framework built with **Python**, **Playwright**, and **Pytest**.

All tests target the public demo site [https://the-internet.herokuapp.com](https://the-internet.herokuapp.com).

## Key Features

- **Page Object Model** with a comprehensive `BasePage` (waits, scrolling, screenshots, assertions)
- **15+ test cases** across 6 test files covering login, dropdowns, dynamic controls, checkboxes, file upload, and hovers
- **Data-driven tests** via `pytest.mark.parametrize` and centralised test data classes
- **Custom markers**: `smoke`, `regression`, `critical`
- **Screenshot on failure** saved automatically under `screenshots/failures/` and attached to Allure
- **Allure reporting** with `@allure.feature`, `@allure.story`, `@allure.title`, `@allure.severity` annotations
- **Playwright tracing** support for debugging (configurable via `TRACING` env var)
- **Environment config** via `.env` with sensible defaults
- **Proper fixture scoping**: session-level browser, function-level context and page
- **Logging** configured through `pytest.ini`

## Project Structure

```
playwright-test-demo/
  pytest.ini                  # Pytest settings, markers, logging
  requirements.txt            # Python dependencies
  pages/
    __init__.py               # Package exports
    base_page.py              # BasePage with reusable methods (wait, click, fill, assert)
    login_page.py             # LoginPage (/login)
    secure_page.py            # SecurePage (/secure)
    dropdown_page.py          # DropdownPage (/dropdown)
    dynamic_controls_page.py  # DynamicControlsPage (/dynamic_controls)
    checkbox_page.py          # CheckboxPage (/checkboxes)
    file_upload_page.py       # FileUploadPage (/upload)
    hovers_page.py            # HoversPage (/hovers)
  tests/
    __init__.py
    conftest.py               # Fixtures, hooks, tracing, screenshot-on-failure
    test_data.py              # Dataclasses and constants for test data
    test_login.py             # Login tests (valid, invalid, logout) -- 5 cases
    test_dropdown.py          # Dropdown selection tests -- 4 cases
    test_dynamic_controls.py  # Dynamic checkbox/input tests -- 3 cases
    test_checkboxes.py        # Checkbox toggling tests -- 5 cases
    test_file_upload.py       # File upload tests -- 3 cases
    test_hovers.py            # Hover interaction tests -- 4 cases
```

## Prerequisites

- Python 3.10+
- pip

## Setup

```bash
python -m venv .venv
source .venv/bin/activate   # Windows: .venv\Scripts\activate
pip install -r requirements.txt
playwright install chromium
```

## Running Tests

```bash
# All tests (verbose)
pytest tests/ -v

# Smoke tests only
pytest tests/ -v -m smoke

# Regression tests only
pytest tests/ -v -m regression

# With Allure reporting
pytest tests/ -v --alluredir=allure-results
allure serve allure-results

# With Playwright tracing enabled
TRACING=true pytest tests/ -v

# Run a specific test file
pytest tests/test_login.py -v

# Run tests matching a keyword
pytest tests/ -v -k "checkbox"
```

## Configuration

Create a `.env` file in the project root or export environment variables:

| Variable          | Default                                    | Description                           |
|-------------------|--------------------------------------------|---------------------------------------|
| `BASE_URL`        | `https://the-internet.herokuapp.com`       | Application under test base URL       |
| `HEADLESS`        | `true`                                     | Run browser in headless mode          |
| `SLOW_MO`         | `0`                                        | Slow down actions (ms)                |
| `DEFAULT_TIMEOUT` | `15000`                                    | Default element timeout (ms)          |
| `TRACING`         | `false`                                    | Enable Playwright tracing (true/false)|

## Test Coverage Summary

| Area              | Tests | Markers          |
|-------------------|-------|------------------|
| Login             | 5     | smoke, critical, regression |
| Dropdown          | 4     | smoke, regression |
| Dynamic Controls  | 3     | smoke, regression |
| Checkboxes        | 5     | smoke, regression |
| File Upload       | 3     | smoke, regression |
| Hovers            | 4     | smoke, regression |
| **Total**         | **24**|                  |
