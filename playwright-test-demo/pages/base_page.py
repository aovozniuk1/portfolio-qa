"""Shared utilities every page object can rely on.

Keeps navigation, waiting, and common interactions in one place so
individual page objects stay focused on page-specific behaviour.
"""

import logging
import os
from typing import List

import allure
from playwright.sync_api import Locator, Page, expect

logger = logging.getLogger(__name__)


class BasePage:
    """Base class providing shared utilities for all page objects."""

    def __init__(self, page: Page) -> None:
        """Initialize BasePage with a Playwright Page instance.

        Args:
            page: Playwright Page object from the browser context.
        """
        self.page = page
        self._default_timeout = 10_000

    # ------------------------------------------------------------------ #
    #  Navigation
    # ------------------------------------------------------------------ #

    @allure.step("Navigate to {path}")
    def navigate(self, path: str = "/") -> None:
        """Navigate to a path relative to the base URL.

        Args:
            path: URL path to navigate to (e.g. ``/login``).
        """
        logger.info("Navigating to %s", path)
        self.page.goto(path, wait_until="domcontentloaded")

    def get_current_url(self) -> str:
        """Return the current page URL."""
        return self.page.url

    def get_title(self) -> str:
        """Return the current page title."""
        return self.page.title()

    @allure.step("Reload page")
    def reload(self) -> None:
        """Reload the current page."""
        logger.info("Reloading page")
        self.page.reload(wait_until="domcontentloaded")

    # ------------------------------------------------------------------ #
    #  Element interaction
    # ------------------------------------------------------------------ #

    @allure.step("Click element: {selector}")
    def click(self, selector: str, timeout: float | None = None) -> None:
        """Click an element.

        Args:
            selector: CSS / Playwright selector.
            timeout: Optional custom timeout in ms.
        """
        logger.debug("Clicking '%s'", selector)
        self.page.click(selector, timeout=timeout or self._default_timeout)

    @allure.step("Fill '{selector}' with '{value}'")
    def fill(self, selector: str, value: str) -> None:
        """Clear and fill an input field.

        Args:
            selector: CSS / Playwright selector.
            value: Text to enter.
        """
        logger.debug("Filling '%s' with '%s'", selector, value)
        self.page.fill(selector, value)

    @allure.step("Select option in '{selector}'")
    def select_option(self, selector: str, *, value: str | None = None,
                      label: str | None = None) -> None:
        """Select a dropdown option by value or visible label.

        Args:
            selector: CSS selector of the ``<select>`` element.
            value: Option ``value`` attribute.
            label: Visible text of the option.
        """
        if value is not None:
            logger.debug("Selecting value='%s' in '%s'", value, selector)
            self.page.select_option(selector, value=value)
        elif label is not None:
            logger.debug("Selecting label='%s' in '%s'", label, selector)
            self.page.select_option(selector, label=label)

    @allure.step("Check checkbox: {selector}")
    def check(self, selector: str) -> None:
        """Check a checkbox if it is not already checked.

        Args:
            selector: CSS / Playwright selector.
        """
        self.page.check(selector)

    @allure.step("Uncheck checkbox: {selector}")
    def uncheck(self, selector: str) -> None:
        """Uncheck a checkbox if it is currently checked.

        Args:
            selector: CSS / Playwright selector.
        """
        self.page.uncheck(selector)

    # ------------------------------------------------------------------ #
    #  Reading element state
    # ------------------------------------------------------------------ #

    def get_text(self, selector: str) -> str:
        """Return the inner text of an element.

        Args:
            selector: CSS / Playwright selector.

        Returns:
            Visible text content of the element.
        """
        return self.page.inner_text(selector)

    def get_attribute(self, selector: str, attribute: str) -> str | None:
        """Return the value of an element attribute.

        Args:
            selector: CSS / Playwright selector.
            attribute: HTML attribute name.

        Returns:
            Attribute value, or ``None`` if absent.
        """
        return self.page.get_attribute(selector, attribute)

    def get_input_value(self, selector: str) -> str:
        """Return the current value of an input field.

        Args:
            selector: CSS / Playwright selector.

        Returns:
            Current input value.
        """
        return self.page.input_value(selector)

    def get_elements_text(self, selector: str) -> List[str]:
        """Return the visible text of all elements matching the selector.

        Args:
            selector: CSS / Playwright selector.

        Returns:
            List of text strings for each matched element.
        """
        elements = self.page.locator(selector)
        return elements.all_inner_texts()

    def count_elements(self, selector: str) -> int:
        """Return the number of elements matching the selector.

        Args:
            selector: CSS / Playwright selector.

        Returns:
            Count of matched elements.
        """
        return self.page.locator(selector).count()

    # ------------------------------------------------------------------ #
    #  Visibility & waiting
    # ------------------------------------------------------------------ #

    def is_element_visible(self, selector: str) -> bool:
        """Check whether an element is visible on the page.

        Args:
            selector: CSS / Playwright selector.

        Returns:
            ``True`` if the element is visible.
        """
        return self.page.locator(selector).is_visible()

    def is_element_enabled(self, selector: str) -> bool:
        """Check whether an element is enabled.

        Args:
            selector: CSS / Playwright selector.

        Returns:
            ``True`` if the element is enabled.
        """
        return self.page.locator(selector).is_enabled()

    def is_element_checked(self, selector: str) -> bool:
        """Check whether a checkbox or radio button is checked.

        Args:
            selector: CSS / Playwright selector.

        Returns:
            ``True`` if the element is checked.
        """
        return self.page.locator(selector).is_checked()

    @allure.step("Wait for element '{selector}'")
    def wait_for_element(self, selector: str, *,
                         state: str = "visible",
                         timeout: float | None = None) -> Locator:
        """Wait for an element to reach the given state.

        state: visible | hidden | attached | detached.
        """
        locator = self.page.locator(selector)
        locator.wait_for(state=state, timeout=timeout or self._default_timeout)
        return locator

    @allure.step("Wait for URL matching '{url_pattern}'")
    def wait_for_url(self, url_pattern: str, timeout: float | None = None) -> None:
        """Wait until the page URL matches the pattern.

        Args:
            url_pattern: URL substring or regex pattern.
            timeout: Maximum wait time in ms.
        """
        self.page.wait_for_url(url_pattern, timeout=timeout or self._default_timeout)

    # ------------------------------------------------------------------ #
    #  Scroll helpers
    # ------------------------------------------------------------------ #

    @allure.step("Scroll to element: {selector}")
    def scroll_to(self, selector: str) -> None:
        """Scroll an element into the viewport.

        Args:
            selector: CSS / Playwright selector.
        """
        logger.debug("Scrolling to '%s'", selector)
        self.page.locator(selector).scroll_into_view_if_needed()

    def scroll_to_bottom(self) -> None:
        """Scroll to the bottom of the page."""
        self.page.evaluate("window.scrollTo(0, document.body.scrollHeight)")

    # ------------------------------------------------------------------ #
    #  Screenshot
    # ------------------------------------------------------------------ #

    @allure.step("Take screenshot: {name}")
    def take_screenshot(self, name: str = "screenshot",
                        directory: str = "screenshots") -> str:
        """Capture a full-page screenshot and return the file path.

        Args:
            name: Base file name (without extension).
            directory: Directory to save the screenshot.

        Returns:
            Absolute path to the saved screenshot.
        """
        os.makedirs(directory, exist_ok=True)
        path = os.path.join(directory, f"{name}.png")
        self.page.screenshot(path=path, full_page=True)
        logger.info("Screenshot saved: %s", path)
        return path

    # ------------------------------------------------------------------ #
    #  Assertions (Playwright expect wrappers)
    # ------------------------------------------------------------------ #

    @allure.step("Assert element visible: {selector}")
    def expect_visible(self, selector: str) -> None:
        """Assert that an element is visible.

        Args:
            selector: CSS / Playwright selector.
        """
        expect(self.page.locator(selector)).to_be_visible()

    @allure.step("Assert element hidden: {selector}")
    def expect_hidden(self, selector: str) -> None:
        """Assert that an element is hidden.

        Args:
            selector: CSS / Playwright selector.
        """
        expect(self.page.locator(selector)).to_be_hidden()

    @allure.step("Assert element text contains '{expected}': {selector}")
    def expect_text(self, selector: str, expected: str) -> None:
        """Assert that an element contains expected text.

        Args:
            selector: CSS / Playwright selector.
            expected: Text that should be present.
        """
        expect(self.page.locator(selector)).to_contain_text(expected)

    @allure.step("Assert element count is {count}: {selector}")
    def expect_element_count(self, selector: str, count: int) -> None:
        """Assert the number of elements matching the selector.

        Args:
            selector: CSS / Playwright selector.
            count: Expected element count.
        """
        expect(self.page.locator(selector)).to_have_count(count)
