"""
Hovers Tests -- Validate hover behaviour over user profile images.

Target: https://the-internet.herokuapp.com/hovers
"""

import allure
import pytest
from pages.hovers_page import HoversPage
from playwright.sync_api import Page

from tests.test_data import HOVER_USERS


@allure.feature("User Interaction")
@allure.story("Hovers")
class TestHovers:
    """Test suite for the Hovers page."""

    @pytest.fixture(autouse=True)
    def _setup(self, page: Page) -> None:
        """Instantiate the page object and navigate to the Hovers page."""
        self.hovers_page = HoversPage(page)
        self.hovers_page.open()

    @allure.title("Page displays three user figures")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.smoke
    def test_figure_count(self) -> None:
        """The hovers page should display exactly three figures."""
        count = self.hovers_page.get_figure_count()
        assert count == 3, f"Expected 3 figures, got {count}"

    @allure.title("Hovering over figure {index} reveals caption '{expected_name}'")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    @pytest.mark.parametrize(
        "index, expected_name, expected_link",
        [
            pytest.param(u["index"], u["name"], u["link"], id=u["name"])
            for u in HOVER_USERS
        ],
    )
    def test_hover_reveals_user_info(
        self, index: int, expected_name: str, expected_link: str
    ) -> None:
        """Hovering over a figure reveals the user caption with name and profile link."""
        self.hovers_page.hover_over_figure(index)

        assert self.hovers_page.is_caption_visible(index), (
            f"Caption should be visible for figure {index}"
        )
        caption = self.hovers_page.get_caption_text(index)
        assert expected_name in caption, f"Expected '{expected_name}' in '{caption}'"

        link = self.hovers_page.get_profile_link(index)
        assert link.endswith(expected_link), f"Expected link ending with '{expected_link}', got '{link}'"
