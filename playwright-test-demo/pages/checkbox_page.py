"""
CheckboxPage -- Page Object for the Checkboxes page.

Target: https://the-internet.herokuapp.com/checkboxes
"""

import logging
from typing import List

from pages.base_page import BasePage
from playwright.sync_api import Page

logger = logging.getLogger(__name__)


class CheckboxPage(BasePage):
    """Page object for /checkboxes."""

    # -- Selectors --------------------------------------------------------- #
    CHECKBOXES: str = "#checkboxes input[type='checkbox']"
    PAGE_HEADING: str = "h3"

    def __init__(self, page: Page) -> None:
        super().__init__(page)

    # -- Actions ----------------------------------------------------------- #

    def open(self) -> "CheckboxPage":
        """Navigate to the checkboxes page."""
        self.navigate("/checkboxes")
        return self

    def toggle_checkbox(self, index: int) -> "CheckboxPage":
        """Toggle a checkbox by its zero-based index.

        Args:
            index: Zero-based index of the checkbox.
        """
        logger.info("Toggling checkbox at index %d", index)
        self.page.locator(self.CHECKBOXES).nth(index).click()
        return self

    def check_checkbox(self, index: int) -> "CheckboxPage":
        """Ensure a checkbox is checked.

        Args:
            index: Zero-based index of the checkbox.
        """
        checkbox = self.page.locator(self.CHECKBOXES).nth(index)
        if not checkbox.is_checked():
            checkbox.check()
        return self

    def uncheck_checkbox(self, index: int) -> "CheckboxPage":
        """Ensure a checkbox is unchecked.

        Args:
            index: Zero-based index of the checkbox.
        """
        checkbox = self.page.locator(self.CHECKBOXES).nth(index)
        if checkbox.is_checked():
            checkbox.uncheck()
        return self

    # -- State readers ----------------------------------------------------- #

    def is_checkbox_checked(self, index: int) -> bool:
        """Return whether a checkbox is checked.

        Args:
            index: Zero-based index of the checkbox.

        Returns:
            ``True`` if the checkbox is checked.
        """
        return self.page.locator(self.CHECKBOXES).nth(index).is_checked()

    def get_checkbox_states(self) -> List[bool]:
        """Return a list of checked states for all checkboxes."""
        count = self.page.locator(self.CHECKBOXES).count()
        return [
            self.page.locator(self.CHECKBOXES).nth(i).is_checked()
            for i in range(count)
        ]

    def get_checkbox_count(self) -> int:
        """Return the total number of checkboxes on the page."""
        return self.page.locator(self.CHECKBOXES).count()

    def get_page_heading(self) -> str:
        """Return the page heading text."""
        return self.get_text(self.PAGE_HEADING).strip()
