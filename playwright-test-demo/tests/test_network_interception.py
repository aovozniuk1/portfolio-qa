"""
Network interception and API mocking tests.

Demonstrates advanced Playwright capabilities:
- Request interception and modification
- API response mocking
- Network condition simulation
- Request/response logging
"""

import allure
import pytest
from playwright.sync_api import Page, Route


@allure.feature("Network Interception")
class TestNetworkInterception:
    """Test suite for network interception and API mocking."""

    # -- Mock API response ------------------------------------------------- #

    @allure.story("API Mocking")
    @allure.title("Mock API response and verify page uses mocked data")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.smoke
    @pytest.mark.regression
    def test_mock_api_response(self, page: Page) -> None:
        """Intercept the status_codes page response and inject custom HTML content."""

        mocked_body = """
        <html>
        <head><title>Mocked Page</title></head>
        <body>
            <h1>Mocked Heading</h1>
            <p id="mocked-content">This response was mocked by Playwright</p>
        </body>
        </html>
        """

        def handle_route(route: Route) -> None:
            route.fulfill(
                status=200,
                content_type="text/html",
                body=mocked_body,
            )

        page.route("**/status_codes", handle_route)
        page.goto("/status_codes")

        heading = page.locator("h1")
        assert heading.text_content() == "Mocked Heading"

        content = page.locator("#mocked-content")
        assert content.text_content() == "This response was mocked by Playwright"

    # -- Block images ------------------------------------------------------ #

    @allure.story("Request Blocking")
    @allure.title("Block all image requests and verify page loads without images")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_block_images(self, page: Page) -> None:
        """Block image resources and verify that images fail to render on /hovers."""

        page.route(
            "**/*.{png,jpg,jpeg,gif,svg}",
            lambda route: route.abort(),
        )
        page.goto("/hovers")

        images = page.locator(".figure img")
        count = images.count()
        assert count > 0, "Page should contain image elements"

        for i in range(count):
            img = images.nth(i)
            is_broken = page.evaluate(
                "(el) => !el.complete || el.naturalWidth === 0",
                img.element_handle(),
            )
            assert is_broken, f"Image {i} should be broken when requests are blocked"

    # -- Intercept and modify request headers ------------------------------ #

    @allure.story("Request Modification")
    @allure.title("Intercept requests and add custom headers")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_intercept_and_modify_request(self, page: Page) -> None:
        """Add a custom header to outgoing requests and verify it was sent."""

        intercepted_headers: dict[str, str] = {}

        def handle_route(route: Route) -> None:
            headers = route.request.headers.copy()
            headers["X-Custom-Test-Header"] = "playwright-intercepted"
            intercepted_headers.update(headers)
            route.continue_(headers=headers)

        page.route("**/login", handle_route)
        page.goto("/login")

        # Playwright preserves header casing as-is
        assert intercepted_headers.get("X-Custom-Test-Header") == "playwright-intercepted", (
            "Custom header should have been added to the intercepted request"
        )

    # -- Capture network requests ------------------------------------------ #

    @allure.story("Network Logging")
    @allure.title("Capture and log all network requests during page load")
    @allure.severity(allure.severity_level.NORMAL)
    @pytest.mark.regression
    def test_capture_network_requests(
        self,
        page: Page,
        captured_requests: list[dict[str, str]],
    ) -> None:
        """Navigate to a page and verify that expected network requests were captured."""

        page.goto("/login")
        page.wait_for_load_state("networkidle")

        assert len(captured_requests) > 0, "At least one network request should be captured"

        urls = [req["url"] for req in captured_requests]
        has_document_request = any("/login" in url for url in urls)
        assert has_document_request, (
            f"A request to /login should be captured. Got: {urls}"
        )

        allure.attach(
            "\n".join(
                f"{req['method']} {req['url']} ({req['resource_type']})"
                for req in captured_requests
            ),
            name="Captured Network Requests",
            attachment_type=allure.attachment_type.TEXT,
        )

    # -- Mock failed request (500 error) ----------------------------------- #

    @allure.story("Error Handling")
    @allure.title("Mock a 500 server error and verify error state")
    @allure.severity(allure.severity_level.CRITICAL)
    @pytest.mark.regression
    def test_mock_failed_request(self, page: Page) -> None:
        """Route a page request to return HTTP 500 and verify the response status."""

        error_body = """
        <html>
        <head><title>Internal Server Error</title></head>
        <body>
            <h1>500 Internal Server Error</h1>
            <p id="error-message">Something went wrong on the server</p>
        </body>
        </html>
        """

        def handle_route(route: Route) -> None:
            route.fulfill(
                status=500,
                content_type="text/html",
                body=error_body,
            )

        page.route("**/dynamic_loading", handle_route)
        response = page.goto("/dynamic_loading")

        assert response is not None
        assert response.status == 500, (
            f"Expected 500 status, got {response.status}"
        )

        heading = page.locator("h1")
        assert heading.text_content() == "500 Internal Server Error"

        error_msg = page.locator("#error-message")
        assert error_msg.text_content() == "Something went wrong on the server"
