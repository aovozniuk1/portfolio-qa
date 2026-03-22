"""
DynamicControlsPage -- Page Object for the Dynamic Controls page.

Target: https://the-internet.herokuapp.com/dynamic_controls
Demonstrates waiting for elements that are added/removed dynamically.
"""

import logging

from pages.base_page import BasePage
from playwright.sync_api import Page

logger = logging.getLogger(__name__)


class DynamicControlsPage(BasePage):
    """Page object for /dynamic_controls."""

    # -- Selectors --------------------------------------------------------- #
    CHECKBOX: str = "#checkbox-example input[type='checkbox']"
    REMOVE_ADD_BUTTON: str = "#checkbox-example button"
    CHECKBOX_MESSAGE: str = "#message"

    TEXT_INPUT: str = "#input-example input[type='text']"
    ENABLE_DISABLE_BUTTON: str = "#input-example button"
    INPUT_MESSAGE: str = "#message"

    LOADING_INDICATOR: str = "#loading"
    PAGE_HEADING: str = "h4"

    def __init__(self, page: Page) -> None:
        super().__init__(page)

    # -- Actions ----------------------------------------------------------- #

    def open(self) -> "DynamicControlsPage":
        """Navigate to the dynamic controls page."""
        self.navigate("/dynamic_controls")
        return self

    def click_remove_add_button(self) -> "DynamicControlsPage":
        """Click the Remove / Add button in the checkbox section."""
        logger.info("Clicking Remove/Add button")
        self.click(self.REMOVE_ADD_BUTTON)
        return self

    def click_enable_disable_button(self) -> "DynamicControlsPage":
        """Click the Enable / Disable button in the input section."""
        logger.info("Clicking Enable/Disable button")
        self.click(self.ENABLE_DISABLE_BUTTON)
        return self

    def wait_for_checkbox_operation(self) -> None:
        """Wait for the checkbox add/remove operation to complete.

        Waits for the Remove/Add button to become enabled again, which
        signals the async operation has finished.
        """
        # The button is disabled during loading; wait for re-enable
        self.page.locator(self.REMOVE_ADD_BUTTON).wait_for(state="visible")
        self.page.wait_for_function(
            "() => !document.querySelector('#checkbox-example button').disabled",
            timeout=15_000,
        )

    def wait_for_input_operation(self) -> None:
        """Wait for the input enable/disable operation to complete.

        Waits for the Enable/Disable button to become enabled again.
        """
        self.page.wait_for_function(
            "() => !document.querySelector('#input-example button').disabled",
            timeout=15_000,
        )

    def type_in_input(self, text: str) -> "DynamicControlsPage":
        """Type text into the text input field.

        Args:
            text: Value to enter.
        """
        self.fill(self.TEXT_INPUT, text)
        return self

    # -- State readers ----------------------------------------------------- #

    def is_checkbox_present(self) -> bool:
        """Return ``True`` if the checkbox is present on the page."""
        return self.is_element_visible(self.CHECKBOX)

    def is_input_enabled(self) -> bool:
        """Return ``True`` if the text input field is enabled."""
        return self.is_element_enabled(self.TEXT_INPUT)

    def get_message(self) -> str:
        """Return the text of the status message."""
        self.wait_for_element(self.CHECKBOX_MESSAGE)
        return self.get_text(self.CHECKBOX_MESSAGE).strip()

    def get_page_heading(self) -> str:
        """Return the page heading text."""
        return self.get_text(self.PAGE_HEADING).strip()
