"""
Checkbox Tests -- Validate checkbox toggling behaviour.

Target: https://the-internet.herokuapp.com/checkboxes
"""

import allure
import pytest
from pages.checkbox_page import CheckboxPage
from playwright.sync_api import Page


@allure.feature("Form Controls")
@allure.story("Checkboxes")
class TestCheckboxes:
    """Test suite for the Checkboxes page."""

    @pytest.fixture(autouse=True)
    def _setup(self, page: Page) -> None:
        """Instantiate the page object and navigate to the Checkboxes page."""
        self.checkbox_page = CheckboxPage(page)
        self.checkbox_page.open()

    @allure.title("Page displays exactly two checkboxes")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.smoke
    def test_checkbox_count(self) -> None:
        """The checkboxes page should display exactly two checkboxes."""
        count = self.checkbox_page.get_checkbox_count()
        assert count == 2, f"Expected 2 checkboxes, got {count}"

    @allure.title("Default checkbox states: first unchecked, second checked")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_default_checkbox_states(self) -> None:
        """By default, checkbox 1 is unchecked and checkbox 2 is checked."""
        states = self.checkbox_page.get_checkbox_states()
        assert states == [False, True], f"Expected [False, True], got {states}"

    @allure.title("Check the first checkbox")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_check_first_checkbox(self) -> None:
        """Checking the first checkbox updates its state."""
        self.checkbox_page.check_checkbox(0)
        assert self.checkbox_page.is_checkbox_checked(0), "Checkbox 1 should be checked"

    @allure.title("Uncheck the second checkbox")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_uncheck_second_checkbox(self) -> None:
        """Unchecking the second checkbox updates its state."""
        self.checkbox_page.uncheck_checkbox(1)
        assert not self.checkbox_page.is_checkbox_checked(1), "Checkbox 2 should be unchecked"

    @allure.title("Toggle checkbox changes its state")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_toggle_checkbox(self) -> None:
        """Toggling a checkbox flips its checked state."""
        initial_state = self.checkbox_page.is_checkbox_checked(0)
        self.checkbox_page.toggle_checkbox(0)
        new_state = self.checkbox_page.is_checkbox_checked(0)
        assert new_state != initial_state, "Toggle should change checkbox state"
