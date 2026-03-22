"""
Login Tests -- Validate authentication flows on the-internet.herokuapp.com.

Covers valid login, invalid credentials, empty fields, and logout.
Uses ``pytest.mark.parametrize`` for data-driven negative scenarios.
"""

import allure
import pytest
from pages.login_page import LoginPage
from pages.secure_page import SecurePage
from playwright.sync_api import Page

from tests.test_data import (
    EMPTY_CREDENTIALS,
    INVALID_PASSWORD_USER,
    INVALID_USERNAME_USER,
    VALID_USER,
)


@allure.feature("Authentication")
@allure.story("Login")
class TestLogin:
    """Test suite for the login functionality."""

    @pytest.fixture(autouse=True)
    def _setup(self, page: Page) -> None:
        """Instantiate page objects and navigate to the login page."""
        self.login_page = LoginPage(page)
        self.secure_page = SecurePage(page)
        self.login_page.open()

    # -- Positive ---------------------------------------------------------- #

    @allure.title("Successful login with valid credentials")
    @allure.severity(allure.severity_level.BLOCKER)
    @pytest.mark.smoke
    @pytest.mark.critical
    def test_successful_login(self) -> None:
        """Valid credentials redirect to the secure area with a success banner."""
        self.login_page.login(VALID_USER.username, VALID_USER.password)

        assert self.login_page.is_logged_in(), "Logout button should be visible"
        assert "You logged into a secure area!" in self.login_page.get_flash_message()

    @allure.title("User can log out after a successful login")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_logout(self) -> None:
        """After logging in, clicking Logout returns to the login page."""
        self.login_page.login(VALID_USER.username, VALID_USER.password)
        assert self.login_page.is_logged_in()

        self.login_page.click_logout()

        assert not self.login_page.is_logged_in(), "User should be logged out"
        assert "You logged out of the secure area!" in self.login_page.get_flash_message()

    # -- Negative (parametrized) ------------------------------------------- #

    @allure.title("Login fails with invalid credentials: {credentials.description}")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.regression
    @pytest.mark.parametrize(
        "credentials, expected_error",
        [
            pytest.param(
                INVALID_PASSWORD_USER,
                "Your password is invalid!",
                id="wrong-password",
            ),
            pytest.param(
                INVALID_USERNAME_USER,
                "Your username is invalid!",
                id="wrong-username",
            ),
            pytest.param(
                EMPTY_CREDENTIALS,
                "Your username is invalid!",
                id="empty-fields",
            ),
        ],
    )
    def test_login_with_invalid_credentials(
        self,
        credentials,  # type: ignore[no-untyped-def]
        expected_error: str,
    ) -> None:
        """Invalid or empty credentials show an appropriate error banner."""
        self.login_page.login(credentials.username, credentials.password)

        assert not self.login_page.is_logged_in(), "User should NOT be logged in"
        flash = self.login_page.get_flash_message()
        assert expected_error in flash, f"Expected '{expected_error}' in '{flash}'"
