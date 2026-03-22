"""
Dropdown Tests -- Validate dropdown selection behaviour.

Target: https://the-internet.herokuapp.com/dropdown
Demonstrates ``pytest.mark.parametrize`` for data-driven option selection.
"""

import allure
import pytest
from pages.dropdown_page import DropdownPage
from playwright.sync_api import Page

from tests.test_data import DROPDOWN_OPTIONS, DROPDOWN_VALUES


@allure.feature("Form Controls")
@allure.story("Dropdown")
class TestDropdown:
    """Test suite for the Dropdown page."""

    @pytest.fixture(autouse=True)
    def _setup(self, page: Page) -> None:
        """Instantiate the page object and navigate to the Dropdown page."""
        self.dropdown_page = DropdownPage(page)
        self.dropdown_page.open()

    @allure.title("Dropdown contains the expected selectable options")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.smoke
    def test_dropdown_options_present(self) -> None:
        """The dropdown should list Option 1 and Option 2."""
        options = self.dropdown_page.get_selectable_option_texts()
        assert options == DROPDOWN_OPTIONS, f"Expected {DROPDOWN_OPTIONS}, got {options}"

    @allure.title("Select dropdown option by value: {value} -> {expected_text}")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    @pytest.mark.parametrize(
        "value, expected_text",
        [
            pytest.param("1", "Option 1", id="option-1"),
            pytest.param("2", "Option 2", id="option-2"),
        ],
    )
    def test_select_option_by_value(self, value: str, expected_text: str) -> None:
        """Selecting by value updates the visible selection text."""
        self.dropdown_page.select_option_by_value(value)
        selected = self.dropdown_page.get_selected_option_text()
        assert selected == expected_text, f"Expected '{expected_text}', got '{selected}'"

    @allure.title("Select dropdown option by visible text")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_select_option_by_text(self) -> None:
        """Selecting by visible text correctly updates the selected value."""
        self.dropdown_page.select_option_by_visible_text("Option 2")
        selected = self.dropdown_page.get_selected_option_text()
        assert selected == "Option 2"

        self.dropdown_page.select_option_by_visible_text("Option 1")
        selected = self.dropdown_page.get_selected_option_text()
        assert selected == "Option 1"
