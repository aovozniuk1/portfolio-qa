"""
Test data classes and constants used across test modules.

Centralises test data so changes propagate to all tests automatically.
"""

from dataclasses import dataclass


@dataclass(frozen=True)
class LoginCredentials:
    """Represents a set of login credentials with an expected outcome."""

    username: str
    password: str
    expected_success: bool
    description: str


# -- Login credentials ----------------------------------------------------- #

VALID_USER = LoginCredentials(
    username="tomsmith",
    password="SuperSecretPassword!",
    expected_success=True,
    description="valid credentials",
)

INVALID_PASSWORD_USER = LoginCredentials(
    username="tomsmith",
    password="WrongPassword123",
    expected_success=False,
    description="invalid password",
)

INVALID_USERNAME_USER = LoginCredentials(
    username="nonexistent_user",
    password="SuperSecretPassword!",
    expected_success=False,
    description="invalid username",
)

EMPTY_CREDENTIALS = LoginCredentials(
    username="",
    password="",
    expected_success=False,
    description="empty fields",
)


# -- Dropdown data --------------------------------------------------------- #

DROPDOWN_OPTIONS = ["Option 1", "Option 2"]

DROPDOWN_VALUES = [
    ("1", "Option 1"),
    ("2", "Option 2"),
]


# -- Hovers data ----------------------------------------------------------- #

HOVER_USERS = [
    {"index": 0, "name": "name: user1", "link": "/users/1"},
    {"index": 1, "name": "name: user2", "link": "/users/2"},
    {"index": 2, "name": "name: user3", "link": "/users/3"},
]
