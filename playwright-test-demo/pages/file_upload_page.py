"""
FileUploadPage -- Page Object for the File Upload page.

Target: https://the-internet.herokuapp.com/upload
"""

import logging
from pathlib import Path

from pages.base_page import BasePage
from playwright.sync_api import Page

logger = logging.getLogger(__name__)


class FileUploadPage(BasePage):
    """Page object for /upload."""

    # -- Selectors --------------------------------------------------------- #
    FILE_INPUT: str = "#file-upload"
    UPLOAD_BUTTON: str = "#file-submit"
    UPLOADED_FILES: str = "#uploaded-files"
    PAGE_HEADING: str = "h3"
    DRAG_DROP_AREA: str = "#drag-drop-upload"

    def __init__(self, page: Page) -> None:
        super().__init__(page)

    # -- Actions ----------------------------------------------------------- #

    def open(self) -> "FileUploadPage":
        """Navigate to the file upload page."""
        self.navigate("/upload")
        return self

    def upload_file(self, file_path: str) -> "FileUploadPage":
        """Select a file for upload using the file input.

        Args:
            file_path: Absolute or relative path to the file.
        """
        logger.info("Uploading file: %s", file_path)
        self.page.set_input_files(self.FILE_INPUT, file_path)
        return self

    def click_upload(self) -> "FileUploadPage":
        """Click the Upload button to submit the file."""
        logger.info("Clicking Upload button")
        self.click(self.UPLOAD_BUTTON)
        return self

    def upload_and_submit(self, file_path: str) -> "FileUploadPage":
        """Upload a file and submit the form.

        Args:
            file_path: Path to the file to upload.
        """
        self.upload_file(file_path)
        self.click_upload()
        return self

    # -- State readers ----------------------------------------------------- #

    def get_uploaded_filename(self) -> str:
        """Return the name of the uploaded file displayed on the result page."""
        self.wait_for_element(self.UPLOADED_FILES)
        return self.get_text(self.UPLOADED_FILES).strip()

    def get_page_heading(self) -> str:
        """Return the page heading text."""
        return self.get_text(self.PAGE_HEADING).strip()

    def is_upload_form_visible(self) -> bool:
        """Return ``True`` if the upload form is visible."""
        return self.is_element_visible(self.FILE_INPUT)
