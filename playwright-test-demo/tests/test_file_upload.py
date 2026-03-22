"""
File Upload Tests -- Validate file upload functionality.

Target: https://the-internet.herokuapp.com/upload
"""

import os
import tempfile

import allure
import pytest
from pages.file_upload_page import FileUploadPage
from playwright.sync_api import Page


@allure.feature("File Management")
@allure.story("File Upload")
class TestFileUpload:
    """Test suite for the File Upload page."""

    @pytest.fixture(autouse=True)
    def _setup(self, page: Page) -> None:
        """Instantiate the page object and navigate to the Upload page."""
        self.upload_page = FileUploadPage(page)
        self.upload_page.open()

    @pytest.fixture()
    def temp_file(self) -> str:
        """Create a temporary text file for upload testing.

        Yields:
            Absolute path to the temporary file.
        """
        fd, path = tempfile.mkstemp(suffix=".txt", prefix="test_upload_")
        with os.fdopen(fd, "w") as f:
            f.write("Test file content for upload validation")
        yield path
        if os.path.exists(path):
            os.unlink(path)

    @allure.title("Upload form is visible on page load")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.smoke
    def test_upload_form_visible(self) -> None:
        """The upload form should be visible when the page loads."""
        assert self.upload_page.is_upload_form_visible(), "Upload form should be visible"

    @allure.title("Successfully upload a text file")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    def test_upload_file(self, temp_file: str) -> None:
        """Uploading a file displays the uploaded filename on the result page."""
        expected_name = os.path.basename(temp_file)

        self.upload_page.upload_and_submit(temp_file)

        uploaded = self.upload_page.get_uploaded_filename()
        assert uploaded == expected_name, f"Expected '{expected_name}', got '{uploaded}'"

    @allure.title("Page heading displays 'File Uploader'")
    @allure.severity(allure.severity_level.MINOR)
    @pytest.mark.regression
    def test_page_heading(self) -> None:
        """The page heading should read 'File Uploader'."""
        heading = self.upload_page.get_page_heading()
        assert heading == "File Uploader", f"Expected 'File Uploader', got '{heading}'"
