"""API tests against jsonplaceholder.typicode.com using httpx + Pydantic.

Complements the Playwright UI suite. These run much faster (no browser)
and exercise the same endpoints as the Java RestAssured tests so the
two halves of the portfolio stay in sync.
"""

from __future__ import annotations

import httpx
import pytest
from pydantic import BaseModel, EmailStr, Field, ValidationError


BASE_URL = "https://jsonplaceholder.typicode.com"
TIMEOUT = httpx.Timeout(10.0, connect=5.0)


# -- Pydantic models ------------------------------------------------------ #

class Geo(BaseModel):
    lat: str
    lng: str


class Address(BaseModel):
    street: str
    suite: str
    city: str
    zipcode: str
    geo: Geo


class Company(BaseModel):
    name: str
    catchPhrase: str
    bs: str


class User(BaseModel):
    id: int
    name: str
    username: str
    email: EmailStr
    phone: str
    website: str
    address: Address
    company: Company


class Post(BaseModel):
    userId: int
    id: int
    title: str = Field(min_length=1)
    body: str = Field(min_length=1)


# -- Fixtures ------------------------------------------------------------ #

@pytest.fixture(scope="module")
def client() -> httpx.Client:
    with httpx.Client(base_url=BASE_URL, timeout=TIMEOUT) as c:
        yield c


# -- Tests --------------------------------------------------------------- #

class TestUsersEndpoint:
    def test_list_parses_into_user_models(self, client: httpx.Client) -> None:
        resp = client.get("/users")
        assert resp.status_code == 200
        users = [User.model_validate(u) for u in resp.json()]
        assert len(users) == 10
        assert all(u.id > 0 for u in users)

    @pytest.mark.parametrize("user_id", [1, 3, 7])
    def test_single_user_by_id(self, client: httpx.Client, user_id: int) -> None:
        resp = client.get(f"/users/{user_id}")
        assert resp.status_code == 200
        user = User.model_validate(resp.json())
        assert user.id == user_id
        assert user.email  # EmailStr already validates format

    def test_missing_user_is_404(self, client: httpx.Client) -> None:
        resp = client.get("/users/9999")
        assert resp.status_code == 404

    def test_invalid_payload_is_rejected_by_model(self) -> None:
        # The API never returns malformed data, so we synthesize one.
        with pytest.raises(ValidationError):
            User.model_validate({"id": 1, "name": "x"})  # missing required fields


class TestPostsEndpoint:
    def test_create_post_returns_echoed_payload(self, client: httpx.Client) -> None:
        payload = {"userId": 1, "title": "portfolio smoke", "body": "created via httpx"}
        resp = client.post("/posts", json=payload)
        assert resp.status_code == 201
        created = Post.model_validate(resp.json())
        assert created.title == payload["title"]
        assert created.body == payload["body"]
        assert created.userId == payload["userId"]

    def test_posts_for_user_chain(self, client: httpx.Client) -> None:
        # Get user 2, then fetch their posts and assert consistency.
        user = User.model_validate(client.get("/users/2").json())
        posts = [Post.model_validate(p) for p in client.get(f"/users/{user.id}/posts").json()]
        assert posts, "expected at least one post for user 2"
        assert all(p.userId == user.id for p in posts)
