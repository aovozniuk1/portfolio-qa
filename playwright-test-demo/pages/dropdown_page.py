"""
DropdownPage -- Page Object for the Dropdown page.

Target: https://the-internet.herokuapp.com/dropdown
"""

import logging
from typing import List

from pages.base_page import BasePage
from playwright.sync_api import Page

logger = logging.getLogger(__name__)


class DropdownPage(BasePage):
    """Page object for /dropdown."""

    # -- Selectors --------------------------------------------------------- #
    DROPDOWN: str = "#dropdown"
    DROPDOWN_OPTIONS: str = "#dropdown option"
    PAGE_HEADING: str = "h3"

    def __init__(self, page: Page) -> None:
        super().__init__(page)

    # -- Actions ----------------------------------------------------------- #

    def open(self) -> "DropdownPage":
        """Navigate to the dropdown page."""
        self.navigate("/dropdown")
        return self

    def select_option_by_value(self, value: str) -> "DropdownPage":
        """Select a dropdown option by its ``value`` attribute.

        Args:
            value: The ``value`` attribute of the option to select.
        """
        logger.info("Selecting dropdown value='%s'", value)
        self.select_option(self.DROPDOWN, value=value)
        return self

    def select_option_by_visible_text(self, text: str) -> "DropdownPage":
        """Select a dropdown option by its visible text.

        Args:
            text: The visible text of the option to select.
        """
        logger.info("Selecting dropdown label='%s'", text)
        self.select_option(self.DROPDOWN, label=text)
        return self

    # -- State readers ----------------------------------------------------- #

    def get_selected_option_text(self) -> str:
        """Return the visible text of the currently selected option."""
        selected = self.page.locator(f"{self.DROPDOWN} option:checked")
        return selected.inner_text().strip()

    def get_all_option_texts(self) -> List[str]:
        """Return all option texts (including the placeholder)."""
        return self.get_elements_text(self.DROPDOWN_OPTIONS)

    def get_selectable_option_texts(self) -> List[str]:
        """Return option texts excluding disabled placeholder."""
        options = self.page.locator(f"{self.DROPDOWN} option:not([disabled])")
        return options.all_inner_texts()

    def get_page_heading(self) -> str:
        """Return the page heading text."""
        return self.get_text(self.PAGE_HEADING).strip()
