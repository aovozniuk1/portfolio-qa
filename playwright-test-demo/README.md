# Playwright Test Demo

End-to-end UI and API tests written in Python with Playwright + Pytest.
Target application: [the-internet.herokuapp.com](https://the-internet.herokuapp.com)
(public demo site, no auth or setup required).

## What's in here

- Page objects with a shared `BasePage` (waits, clicks, assertions, screenshots)
- Login, dropdowns, dynamic controls, checkboxes, hovers, file upload, and network-interception tests
- Data-driven tests via `pytest.mark.parametrize` and a BDD feature file via `pytest-bdd`
- API tests using `httpx` with Pydantic response models
- Parallel execution via `pytest-xdist` (`-n auto`)
- Cross-browser: Chromium, Firefox, WebKit (selectable via `--browser=...`)
- BrowserStack-ready: set `BROWSERSTACK_USERNAME` / `BROWSERSTACK_ACCESS_KEY`
- Proxy support (Charles, mitmproxy, Fiddler) via `HTTP_PROXY` env var
- Screenshot on failure (attached to Allure report)
- Playwright tracing (`TRACING=true`) saved per-test under `traces/`
- Allure reporting with `@allure.feature/story/title/severity`
- `Faker` for realistic test data

## Quick start

```bash
python -m venv .venv
source .venv/bin/activate          # Windows: .venv\Scripts\activate
pip install -r requirements.txt
python -m playwright install chromium
pytest tests/ -v
```

### Run against a specific browser

```bash
pytest tests/ --browser=firefox
pytest tests/ --browser=webkit
# or
BROWSER=firefox pytest tests/
```

### Run in parallel

```bash
pytest tests/ -n auto              # one worker per CPU
pytest tests/ -n 4
```

### API-only run

```bash
pytest tests/api/ -v
```

### BDD feature

```bash
pytest tests/bdd -v
```

### Allure report

```bash
pytest tests/ --alluredir=allure-results
allure serve allure-results
```

### Tracing

```bash
TRACING=true pytest tests/test_login.py
# traces land in ./traces/*.zip; open with `playwright show-trace <file>`
```

## Layout

```
playwright-test-demo/
  pytest.ini
  pyproject.toml
  requirements.txt
  pages/
    base_page.py              # shared interactions
    login_page.py
    secure_page.py
    dropdown_page.py
    dynamic_controls_page.py
    checkbox_page.py
    file_upload_page.py
    hovers_page.py
  tests/
    conftest.py               # fixtures, CLI, tracing, screenshots, proxy/BS hookup
    test_data.py              # data classes + Faker factories
    test_login.py
    test_dropdown.py
    test_dynamic_controls.py
    test_checkboxes.py
    test_file_upload.py
    test_hovers.py
    test_network_interception.py
    api/
      test_jsonplaceholder.py # httpx + Pydantic response model validation
    bdd/
      features/login.feature
      step_defs/test_login_steps.py
```

## Environment variables

| Variable | Default | Purpose |
|---|---|---|
| `BASE_URL` | `https://the-internet.herokuapp.com` | App under test |
| `BROWSER` | `chromium` | Browser to launch (chromium / firefox / webkit) |
| `HEADLESS` | `true` | Headless mode |
| `SLOW_MO` | `0` | Slow down actions (ms) |
| `DEFAULT_TIMEOUT` | `15000` | Default element timeout (ms) |
| `TRACING` | `false` | Enable Playwright trace per test |
| `HTTP_PROXY` | — | Route traffic through a proxy (e.g. `http://localhost:8888`) |
| `BROWSERSTACK_USERNAME` / `BROWSERSTACK_ACCESS_KEY` | — | Run on BrowserStack |

## Notes

- Trace files are written per test so parallel workers don't overwrite each other.
- Default timeout is 15s; dynamic-controls tests occasionally need more — pass `--timeout=...` if you see flakes on slow networks.
- BrowserStack capabilities are expected in `browserstack.yml` at the repo root when you enable that path.
