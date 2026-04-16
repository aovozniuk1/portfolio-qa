"""Step definitions for login.feature."""

import re
from pathlib import Path

import pytest
from playwright.sync_api import expect
from pytest_bdd import given, parsers, scenarios, then, when

from pages.login_page import LoginPage
from pages.secure_page import SecurePage


FEATURES_DIR = Path(__file__).resolve().parent.parent / "features"
scenarios(str(FEATURES_DIR / "login.feature"))


@pytest.fixture()
def login_page(page):
    return LoginPage(page)


@pytest.fixture()
def secure_page(page):
    return SecurePage(page)


@given("I am on the login page")
def _open_login(login_page: LoginPage) -> None:
    login_page.open()


@when(parsers.parse('I submit username "{username}" and password "{password}"'))
def _submit_credentials(login_page: LoginPage, username: str, password: str) -> None:
    login_page.login(username, password)


@then("I should see the secure area")
def _assert_secure_area(secure_page: SecurePage) -> None:
    expect(secure_page.page).to_have_url(re.compile(r"/secure"))


@then("I should remain on the login page")
def _assert_still_on_login(login_page: LoginPage) -> None:
    expect(login_page.page).to_have_url(re.compile(r"/login"))


@then(parsers.parse('the flash message should contain "{expected}"'))
def _assert_flash_contains(login_page: LoginPage, expected: str) -> None:
    assert expected in login_page.get_flash_message()
