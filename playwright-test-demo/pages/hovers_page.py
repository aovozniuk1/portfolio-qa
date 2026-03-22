"""
HoversPage -- Page Object for the Hovers page.

Target: https://the-internet.herokuapp.com/hovers
"""

import logging

from pages.base_page import BasePage
from playwright.sync_api import Page

logger = logging.getLogger(__name__)


class HoversPage(BasePage):
    """Page object for /hovers."""

    # -- Selectors --------------------------------------------------------- #
    FIGURES: str = ".figure"
    FIGURE_IMAGES: str = ".figure img"
    FIGURE_CAPTIONS: str = ".figcaption"
    FIGURE_CAPTION_HEADERS: str = ".figcaption h5"
    FIGURE_CAPTION_LINKS: str = ".figcaption a"

    def __init__(self, page: Page) -> None:
        super().__init__(page)

    # -- Actions ----------------------------------------------------------- #

    def open(self) -> "HoversPage":
        """Navigate to the hovers page."""
        self.navigate("/hovers")
        return self

    def hover_over_figure(self, index: int) -> "HoversPage":
        """Hover over a figure by its zero-based index.

        Args:
            index: Zero-based index of the figure to hover over.
        """
        logger.info("Hovering over figure at index %d", index)
        self.page.locator(self.FIGURES).nth(index).hover()
        return self

    # -- State readers ----------------------------------------------------- #

    def get_figure_count(self) -> int:
        """Return the number of figures on the page."""
        return self.page.locator(self.FIGURES).count()

    def is_caption_visible(self, index: int) -> bool:
        """Return whether the caption for a figure is visible.

        Args:
            index: Zero-based index of the figure.

        Returns:
            ``True`` if the caption is visible after hovering.
        """
        return self.page.locator(self.FIGURE_CAPTIONS).nth(index).is_visible()

    def get_caption_text(self, index: int) -> str:
        """Return the caption header text for a figure.

        Args:
            index: Zero-based index of the figure.

        Returns:
            The ``h5`` text inside the caption.
        """
        return self.page.locator(self.FIGURE_CAPTION_HEADERS).nth(index).inner_text().strip()

    def get_profile_link(self, index: int) -> str:
        """Return the profile link href for a figure.

        Args:
            index: Zero-based index of the figure.

        Returns:
            The ``href`` attribute value.
        """
        return self.page.locator(self.FIGURE_CAPTION_LINKS).nth(index).get_attribute("href") or ""
