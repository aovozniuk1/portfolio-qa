"""Page Object classes for the test framework."""

from pages.base_page import BasePage
from pages.checkbox_page import CheckboxPage
from pages.dropdown_page import DropdownPage
from pages.dynamic_controls_page import DynamicControlsPage
from pages.file_upload_page import FileUploadPage
from pages.hovers_page import HoversPage
from pages.login_page import LoginPage
from pages.secure_page import SecurePage

__all__ = [
    "BasePage",
    "CheckboxPage",
    "DropdownPage",
    "DynamicControlsPage",
    "FileUploadPage",
    "HoversPage",
    "LoginPage",
    "SecurePage",
]
