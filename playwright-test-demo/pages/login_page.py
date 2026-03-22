"""
LoginPage -- Page Object for the Herokuapp login form.

Target: https://the-internet.herokuapp.com/login
Valid credentials: tomsmith / SuperSecretPassword!
"""

import logging

from pages.base_page import BasePage
from playwright.sync_api import Page

logger = logging.getLogger(__name__)


class LoginPage(BasePage):
    """Page object encapsulating selectors and actions for /login."""

    # -- Selectors --------------------------------------------------------- #
    USERNAME_INPUT: str = "#username"
    PASSWORD_INPUT: str = "#password"
    LOGIN_BUTTON: str = "button[type='submit']"
    FLASH_MESSAGE: str = "#flash"
    LOGOUT_BUTTON: str = "a[href='/logout']"
    PAGE_HEADING: str = "h2"

    def __init__(self, page: Page) -> None:
        super().__init__(page)

    # -- Actions ----------------------------------------------------------- #

    def open(self) -> "LoginPage":
        """Navigate to the login page."""
        self.navigate("/login")
        return self

    def enter_username(self, username: str) -> "LoginPage":
        """Fill in the username field.

        Args:
            username: Value to type.
        """
        self.fill(self.USERNAME_INPUT, username)
        return self

    def enter_password(self, password: str) -> "LoginPage":
        """Fill in the password field.

        Args:
            password: Value to type.
        """
        self.fill(self.PASSWORD_INPUT, password)
        return self

    def click_login(self) -> "LoginPage":
        """Click the Login button."""
        self.click(self.LOGIN_BUTTON)
        return self

    def login(self, username: str, password: str) -> "LoginPage":
        """Perform a complete login.

        Args:
            username: Username to enter.
            password: Password to enter.
        """
        logger.info("Logging in as '%s'", username)
        self.enter_username(username)
        self.enter_password(password)
        self.click_login()
        return self

    # -- State readers ----------------------------------------------------- #

    def get_flash_message(self) -> str:
        """Return the text of the flash notification banner."""
        self.wait_for_element(self.FLASH_MESSAGE)
        return self.get_text(self.FLASH_MESSAGE).strip()

    def is_logged_in(self) -> bool:
        """Return ``True`` if the logout button is visible."""
        return self.is_element_visible(self.LOGOUT_BUTTON)

    def get_page_heading(self) -> str:
        """Return the main ``<h2>`` heading text."""
        return self.get_text(self.PAGE_HEADING)

    def click_logout(self) -> "LoginPage":
        """Click the Logout button."""
        logger.info("Logging out")
        self.click(self.LOGOUT_BUTTON)
        return self
