"""
Pytest + Playwright Configuration

Provides browser fixtures with proper scoping, base URL configuration,
automatic screenshot capture on test failure, Playwright tracing support,
Allure screenshot attachment, and logging setup.
"""

import logging
import os
import sys
from typing import Generator

# Add project root to path so 'pages' package is importable from tests/
sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

import allure
import pytest
from dotenv import load_dotenv
from playwright.sync_api import Browser, BrowserContext, Page, Playwright, sync_playwright

load_dotenv()

# -- Environment configuration -------------------------------------------- #

BASE_URL: str = os.getenv("BASE_URL", "https://the-internet.herokuapp.com")
HEADLESS: bool = os.getenv("HEADLESS", "true").lower() == "true"
SLOW_MO: int = int(os.getenv("SLOW_MO", "0"))
DEFAULT_TIMEOUT: int = int(os.getenv("DEFAULT_TIMEOUT", "15000"))
TRACING_ENABLED: bool = os.getenv("TRACING", "false").lower() == "true"

logger = logging.getLogger(__name__)


# -- Session-scoped fixtures ---------------------------------------------- #

@pytest.fixture(scope="session")
def playwright_instance() -> Generator[Playwright, None, None]:
    """Provide a Playwright instance for the entire test session."""
    logger.info("Starting Playwright")
    with sync_playwright() as pw:
        yield pw


@pytest.fixture(scope="session")
def browser(playwright_instance: Playwright) -> Generator[Browser, None, None]:
    """Launch a Chromium browser once for the test session.

    The browser is shared across all tests to reduce startup overhead.
    Each test gets its own context and page (see below).
    """
    logger.info(
        "Launching Chromium (headless=%s, slow_mo=%d)", HEADLESS, SLOW_MO
    )
    browser = playwright_instance.chromium.launch(
        headless=HEADLESS,
        slow_mo=SLOW_MO,
    )
    yield browser
    browser.close()
    logger.info("Browser closed")


# -- Function-scoped fixtures --------------------------------------------- #

@pytest.fixture()
def context(browser: Browser) -> Generator[BrowserContext, None, None]:
    """Create an isolated browser context for each test.

    Context isolation ensures cookies and storage do not leak between tests.
    Optionally starts Playwright tracing when TRACING=true.
    """
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
        trace_dir = os.path.join("traces")
        os.makedirs(trace_dir, exist_ok=True)
        ctx.tracing.stop(path=os.path.join(trace_dir, "trace.zip"))

    ctx.close()


@pytest.fixture()
def page(context: BrowserContext, request: pytest.FixtureRequest) -> Generator[Page, None, None]:
    """Create a new page with automatic screenshot on failure.

    On test failure the screenshot is saved under ``screenshots/failures/``
    and attached to the Allure report.
    """
    pg = context.new_page()
    yield pg

    # Capture screenshot when the test call phase failed
    if hasattr(request.node, "rep_call") and request.node.rep_call.failed:
        _save_failure_screenshot(pg, request.node.name)
        _attach_allure_screenshot(pg, request.node.name)

    pg.close()


# -- Hooks ---------------------------------------------------------------- #

@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item: pytest.Item, call: pytest.CallInfo) -> None:  # type: ignore[type-arg]
    """Store test result on the item so the ``page`` fixture can read it."""
    import pluggy

    outcome: pluggy.Result = yield  # type: ignore[assignment]
    rep = outcome.get_result()
    setattr(item, f"rep_{rep.when}", rep)


# -- Helpers --------------------------------------------------------------- #

def _save_failure_screenshot(pg: Page, test_name: str) -> None:
    """Save a screenshot for a failed test.

    Args:
        pg: Playwright Page to capture.
        test_name: Name of the failed test (used as file name).
    """
    screenshot_dir = os.path.join("screenshots", "failures")
    os.makedirs(screenshot_dir, exist_ok=True)
    safe_name = test_name.replace("[", "_").replace("]", "_")
    path = os.path.join(screenshot_dir, f"{safe_name}.png")
    try:
        pg.screenshot(path=path, full_page=True)
        logger.warning("Failure screenshot saved: %s", path)
    except Exception as exc:  # noqa: BLE001
        logger.error("Could not save failure screenshot: %s", exc)


def _attach_allure_screenshot(pg: Page, test_name: str) -> None:
    """Attach a screenshot to the Allure report for a failed test.

    Args:
        pg: Playwright Page to capture.
        test_name: Name of the failed test.
    """
    try:
        screenshot_bytes = pg.screenshot(full_page=True)
        allure.attach(
            screenshot_bytes,
            name=f"Failure: {test_name}",
            attachment_type=allure.attachment_type.PNG,
        )
    except Exception as exc:  # noqa: BLE001
        logger.error("Could not attach Allure screenshot: %s", exc)
