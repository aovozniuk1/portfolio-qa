"""Pytest + Playwright configuration.

Defines the session-scoped Playwright instance and browser, per-test
context/page fixtures, tracing hook, failure-screenshot attachment, and a
CLI option to switch between chromium, firefox, and webkit.
"""

import logging
import os
import time
from pathlib import Path
from typing import Generator

import allure
import pytest
from dotenv import load_dotenv
from playwright.sync_api import (
    Browser,
    BrowserContext,
    Page,
    Playwright,
    Request,
    sync_playwright,
)

load_dotenv()

BASE_URL: str = os.getenv("BASE_URL", "https://the-internet.herokuapp.com")
HEADLESS: bool = os.getenv("HEADLESS", "true").lower() == "true"
SLOW_MO: int = int(os.getenv("SLOW_MO", "0"))
DEFAULT_TIMEOUT: int = int(os.getenv("DEFAULT_TIMEOUT", "15000"))
TRACING_ENABLED: bool = os.getenv("TRACING", "false").lower() == "true"

# BrowserStack: set BROWSERSTACK_USERNAME + BROWSERSTACK_ACCESS_KEY to
# route tests through their cloud grid instead of a local browser.
USE_BROWSERSTACK: bool = bool(os.getenv("BROWSERSTACK_USERNAME") and os.getenv("BROWSERSTACK_ACCESS_KEY"))

# Optional HTTP proxy (Charles/mitmproxy/fiddler). Example: http://localhost:8888
HTTP_PROXY: str | None = os.getenv("HTTP_PROXY") or None

SUPPORTED_BROWSERS = ("chromium", "firefox", "webkit")

logger = logging.getLogger(__name__)


# -- CLI options ---------------------------------------------------------- #

def pytest_addoption(parser: pytest.Parser) -> None:
    # Named "--ui-browser" to avoid a clash with pytest-playwright's --browser
    # when that plugin is installed. Falls back to BROWSER env var if unset.
    parser.addoption(
        "--ui-browser",
        action="store",
        default=os.getenv("BROWSER", "chromium"),
        choices=SUPPORTED_BROWSERS,
        help="Browser to run against: chromium (default), firefox, webkit",
    )


# -- Session fixtures ----------------------------------------------------- #

@pytest.fixture(scope="session")
def browser_name(request: pytest.FixtureRequest) -> str:
    return request.config.getoption("--ui-browser")


@pytest.fixture(scope="session")
def playwright_instance() -> Generator[Playwright, None, None]:
    logger.info("Starting Playwright")
    with sync_playwright() as pw:
        yield pw


@pytest.fixture(scope="session")
def browser(playwright_instance: Playwright, browser_name: str) -> Generator[Browser, None, None]:
    """Launch the chosen browser once per session.

    Use --browser=firefox or BROWSER=webkit to switch. When BrowserStack
    credentials are present, we connect to their grid via CDP URL instead.
    """
    if USE_BROWSERSTACK:
        # Minimal BrowserStack connection example. Full capability matrix
        # lives in browserstack.yml; we only wire the connection here.
        bs_url = (
            f"wss://cdp.browserstack.com/playwright?caps="
            f"{os.getenv('BROWSERSTACK_CAPS', '')}"
        )
        logger.info("Connecting to BrowserStack grid")
        bs_browser = playwright_instance.chromium.connect(bs_url)
        yield bs_browser
        bs_browser.close()
        return

    launcher = getattr(playwright_instance, browser_name)
    logger.info("Launching %s (headless=%s, slow_mo=%d)", browser_name, HEADLESS, SLOW_MO)

    launch_kwargs: dict = {"headless": HEADLESS, "slow_mo": SLOW_MO}
    if HTTP_PROXY:
        # Works identically for chromium, firefox, webkit.
        launch_kwargs["proxy"] = {"server": HTTP_PROXY}

    br = launcher.launch(**launch_kwargs)
    yield br
    br.close()
    logger.info("Browser closed")


# -- Function-scoped fixtures -------------------------------------------- #

@pytest.fixture()
def context(
    browser: Browser, request: pytest.FixtureRequest
) -> Generator[BrowserContext, None, None]:
    ctx = browser.new_context(
        base_url=BASE_URL,
        viewport={"width": 1280, "height": 720},
        ignore_https_errors=True,
    )
    ctx.set_default_timeout(DEFAULT_TIMEOUT)

    if TRACING_ENABLED:
        ctx.tracing.start(screenshots=True, snapshots=True, sources=True)

    yield ctx

    if TRACING_ENABLED:
        trace_dir = Path("traces")
        trace_dir.mkdir(exist_ok=True)
        # Per-test trace filename so parallel runs don't overwrite each other.
        safe_name = request.node.name.replace("[", "_").replace("]", "_")
        trace_path = trace_dir / f"{safe_name}_{int(time.time())}.zip"
        ctx.tracing.stop(path=str(trace_path))

    ctx.close()


@pytest.fixture()
def page(context: BrowserContext, request: pytest.FixtureRequest) -> Generator[Page, None, None]:
    pg = context.new_page()
    yield pg

    if hasattr(request.node, "rep_call") and request.node.rep_call.failed:
        _save_failure_screenshot(pg, request.node.name)
        _attach_allure_screenshot(pg, request.node.name)

    pg.close()


# -- Network interception fixtures --------------------------------------- #

@pytest.fixture()
def captured_requests(page: Page) -> list[dict[str, str]]:
    """Collect every network request the page makes.

    Each entry: {url, method, resource_type}. Attach before goto().
    """
    requests: list[dict[str, str]] = []

    def _on_request(request: Request) -> None:
        requests.append(
            {
                "url": request.url,
                "method": request.method,
                "resource_type": request.resource_type,
            }
        )

    page.on("request", _on_request)
    return requests


# -- Hooks ---------------------------------------------------------------- #

@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item: pytest.Item, call: pytest.CallInfo):  # type: ignore[type-arg]
    outcome = yield
    rep = outcome.get_result()
    setattr(item, f"rep_{rep.when}", rep)


# -- Helpers -------------------------------------------------------------- #

def _save_failure_screenshot(pg: Page, test_name: str) -> None:
    screenshot_dir = Path("screenshots") / "failures"
    screenshot_dir.mkdir(parents=True, exist_ok=True)
    safe_name = test_name.replace("[", "_").replace("]", "_")
    path = screenshot_dir / f"{safe_name}.png"
    try:
        pg.screenshot(path=str(path), full_page=True)
        logger.warning("Failure screenshot saved: %s", path)
    except Exception as exc:  # noqa: BLE001
        logger.error("Could not save failure screenshot: %s", exc)


def _attach_allure_screenshot(pg: Page, test_name: str) -> None:
    try:
        screenshot_bytes = pg.screenshot(full_page=True)
        allure.attach(
            screenshot_bytes,
            name=f"Failure: {test_name}",
            attachment_type=allure.attachment_type.PNG,
        )
    except Exception as exc:  # noqa: BLE001
        logger.error("Could not attach Allure screenshot: %s", exc)
