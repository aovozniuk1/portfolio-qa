"""
SecurePage -- Page Object for the secure area after login.

Target: https://the-internet.herokuapp.com/secure
"""

import logging

from pages.base_page import BasePage
from playwright.sync_api import Page

logger = logging.getLogger(__name__)


class SecurePage(BasePage):
    """Page object for the authenticated /secure area."""

    # -- Selectors --------------------------------------------------------- #
    FLASH_MESSAGE: str = "#flash"
    LOGOUT_BUTTON: str = "a[href='/logout']"
    PAGE_HEADING: str = "h2"
    CONTENT_AREA: str = "#content"

    def __init__(self, page: Page) -> None:
        super().__init__(page)

    # -- Actions ----------------------------------------------------------- #

    def open(self) -> "SecurePage":
        """Navigate directly to the secure page (will redirect if not logged in)."""
        self.navigate("/secure")
        return self

    def click_logout(self) -> "SecurePage":
        """Click the Logout button to end the session."""
        logger.info("Clicking logout")
        self.click(self.LOGOUT_BUTTON)
        return self

    # -- State readers ----------------------------------------------------- #

    def get_flash_message(self) -> str:
        """Return the text of the flash notification banner."""
        self.wait_for_element(self.FLASH_MESSAGE)
        return self.get_text(self.FLASH_MESSAGE).strip()

    def get_page_heading(self) -> str:
        """Return the main heading text."""
        return self.get_text(self.PAGE_HEADING).strip()

    def is_logout_visible(self) -> bool:
        """Return ``True`` if the Logout button is displayed."""
        return self.is_element_visible(self.LOGOUT_BUTTON)
