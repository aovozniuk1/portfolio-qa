"""Test-data containers, constants, and Faker-driven factories."""

from dataclasses import dataclass

from faker import Faker

# Seeded so test reports stay reproducible. Drop the seed() call when
# you want fresh data on every run.
_faker = Faker()
Faker.seed(4242)


def random_login_payload() -> "LoginCredentials":
    """Return invalid but plausible login credentials for negative tests."""
    return LoginCredentials(
        username=_faker.user_name(),
        password=_faker.password(length=12, special_chars=True),
        expected_success=False,
        description="Faker-generated invalid user",
    )


def random_user_profile() -> dict:
    """Return a synthetic user profile (for API test bodies, form fills, etc.)."""
    return {
        "name": _faker.name(),
        "email": _faker.safe_email(),
        "phone": _faker.phone_number(),
        "company": _faker.company(),
        "city": _faker.city(),
    }


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
