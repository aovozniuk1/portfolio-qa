"""
Dynamic Controls Tests -- Validate dynamic UI behaviour.

Target: https://the-internet.herokuapp.com/dynamic_controls
Tests cover checkbox add/remove toggling and input enable/disable transitions.
"""

import allure
import pytest
from pages.dynamic_controls_page import DynamicControlsPage
from playwright.sync_api import Page


@allure.feature("Dynamic UI")
@allure.story("Dynamic Controls")
class TestDynamicControls:
    """Test suite for the Dynamic Controls page."""

    @pytest.fixture(autouse=True)
    def _setup(self, page: Page) -> None:
        """Instantiate the page object and navigate to Dynamic Controls."""
        self.dc_page = DynamicControlsPage(page)
        self.dc_page.open()

    @allure.title("Remove checkbox and verify it disappears")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_remove_checkbox(self) -> None:
        """Clicking Remove hides the checkbox and shows a confirmation message."""
        assert self.dc_page.is_checkbox_present(), "Checkbox should be visible initially"

        self.dc_page.click_remove_add_button()
        self.dc_page.wait_for_checkbox_operation()

        assert not self.dc_page.is_checkbox_present(), "Checkbox should be removed"
        assert "It's gone!" in self.dc_page.get_message()

    @allure.title("Add checkbox back after removal")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_add_checkbox_back(self) -> None:
        """After removing the checkbox, clicking Add restores it."""
        self.dc_page.click_remove_add_button()
        self.dc_page.wait_for_checkbox_operation()
        assert not self.dc_page.is_checkbox_present()

        # Button text toggles to "Add"
        self.dc_page.click_remove_add_button()
        self.dc_page.wait_for_checkbox_operation()

        assert self.dc_page.is_checkbox_present(), "Checkbox should be back"
        assert "It's back!" in self.dc_page.get_message()

    @allure.title("Enable the text input and type text")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.smoke
    def test_enable_input_and_type(self) -> None:
        """Clicking Enable activates the disabled input so text can be entered."""
        assert not self.dc_page.is_input_enabled(), "Input should be disabled initially"

        self.dc_page.click_enable_disable_button()
        self.dc_page.wait_for_input_operation()

        assert self.dc_page.is_input_enabled(), "Input should be enabled"
        assert "It's enabled!" in self.dc_page.get_message()

        self.dc_page.type_in_input("Hello, World!")
