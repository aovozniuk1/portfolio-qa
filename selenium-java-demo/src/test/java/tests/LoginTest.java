package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.LoginPage;
import pages.SecureAreaPage;
import utils.RetryAnalyzer;

/**
 * UI tests for the login functionality.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/login">/login</a>
 */
@Feature("Authentication")
@Story("Login")
public class LoginTest extends BaseTest {

    private LoginPage loginPage;
    private SecureAreaPage secureAreaPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        loginPage = new LoginPage(driver).open();
        secureAreaPage = new SecureAreaPage(driver);
    }

    @Test(groups = {"smoke", "critical"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify successful login with valid credentials redirects to the secure area")
    public void testSuccessfulLogin() {
        loginPage.login("tomsmith", "SuperSecretPassword!");

        Assert.assertTrue(secureAreaPage.isLogoutVisible(),
                "Logout button should be visible after login");
        Assert.assertTrue(secureAreaPage.getFlashMessage().contains("You logged into a secure area!"),
                "Success flash message should be displayed");
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a logged-in user can log out and return to the login page")
    public void testLogout() {
        loginPage.login("tomsmith", "SuperSecretPassword!");
        Assert.assertTrue(secureAreaPage.isLogoutVisible());

        secureAreaPage.clickLogout();

        Assert.assertFalse(loginPage.isLoggedIn(),
                "Logout button should not be visible");
        Assert.assertTrue(loginPage.getFlashMessage().contains("You logged out of the secure area!"),
                "Logout flash message should be displayed");
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {"tomsmith", "WrongPassword", "Your password is invalid!"},
                {"invalid_user", "SuperSecretPassword!", "Your username is invalid!"},
                {"", "", "Your username is invalid!"},
                {"admin' OR '1'='1", "password", "Your username is invalid!"},
                {"<script>alert('xss')</script>", "password", "Your username is invalid!"},
                {"a".repeat(100), "b".repeat(100), "Your username is invalid!"},
                {"user@#$%^&*()", "pass!@#$%^", "Your username is invalid!"},
                {" tomsmith ", "SuperSecretPassword!", "Your username is invalid!"},
                {"TOMSMITH", "SuperSecretPassword!", "Your username is invalid!"},
        };
    }

    @Test(groups = {"regression"}, dataProvider = "invalidCredentials",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify login fails with invalid credentials and shows error")
    public void testInvalidCredentials(String username, String password, String expectedError) {
        loginPage.login(username, password);

        Assert.assertFalse(loginPage.isLoggedIn(),
                "User should NOT be logged in");
        String flash = loginPage.getFlashMessage();
        Assert.assertTrue(flash.contains(expectedError),
                "Expected '" + expectedError + "' in flash message, got: " + flash);
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the URL changes to /secure after a successful login")
    public void testSuccessfulLoginRedirectUrl() {
        loginPage.login("tomsmith", "SuperSecretPassword!");

        String currentUrl = loginPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/secure"),
                "URL should contain '/secure' after login, got: " + currentUrl);
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Full login-logout-login cycle: login, logout, then login again successfully")
    public void testLoginLogoutLoginAgain() {
        // First login
        loginPage.login("tomsmith", "SuperSecretPassword!");
        Assert.assertTrue(secureAreaPage.isLogoutVisible(),
                "Should be logged in after first login");

        // Logout
        secureAreaPage.clickLogout();
        Assert.assertEquals(loginPage.getPageHeading(), "Login Page",
                "Should be on login page after logout");
        Assert.assertFalse(loginPage.isLoggedIn(),
                "Should be logged out");

        // Login again. Re-open the page first so we bind to fresh form
        // elements -- depending on browser/driver the post-logout redirect
        // can leave stale references to the previous page's inputs.
        loginPage.open();
        loginPage.login("tomsmith", "SuperSecretPassword!");
        Assert.assertTrue(secureAreaPage.isLogoutVisible(),
                "Should be logged in after second login");
        Assert.assertTrue(secureAreaPage.getFlashMessage().contains("You logged into a secure area!"),
                "Success flash message should be displayed after second login");
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify all login form elements are present using SoftAssert")
    public void testLoginFormElementsPresent() {
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertTrue(loginPage.isUsernameFieldDisplayed(),
                "Username field should be visible");
        softAssert.assertTrue(loginPage.isPasswordFieldDisplayed(),
                "Password field should be visible");
        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button should be visible");
        softAssert.assertEquals(loginPage.getPageHeading(), "Login Page",
                "Page heading should be 'Login Page'");

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify URL does not change on failed login attempt")
    public void testUrlUnchangedOnFailedLogin() {
        String urlBefore = loginPage.getCurrentUrl();

        loginPage.login("invalid_user", "invalid_password");

        String urlAfter = loginPage.getCurrentUrl();
        Assert.assertEquals(urlAfter, urlBefore,
                "URL should not change after a failed login attempt");
        Assert.assertTrue(urlAfter.contains("/login"),
                "URL should still contain '/login' after failed attempt, got: " + urlAfter);
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify SQL injection in username field does not bypass authentication")
    public void testSqlInjectionInUsername() {
        String[] sqlPayloads = {
                "' OR '1'='1' --",
                "admin'--",
                "' UNION SELECT * FROM users --",
                "1; DROP TABLE users --"
        };

        SoftAssert softAssert = new SoftAssert();
        for (String payload : sqlPayloads) {
            loginPage.open();
            loginPage.login(payload, "password");

            softAssert.assertFalse(loginPage.isLoggedIn(),
                    "SQL injection payload should not grant access: " + payload);
            softAssert.assertTrue(loginPage.getCurrentUrl().contains("/login"),
                    "URL should remain on /login for payload: " + payload);
        }
        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the page title is correct on the login page")
    public void testLoginPageTitle() {
        String title = loginPage.getPageTitle();
        Assert.assertNotNull(title, "Page title should not be null");
        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");
        Assert.assertTrue(title.contains("The Internet"),
                "Page title should contain 'The Internet', got: " + title);
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Type username, clear it, type again to verify field interaction works correctly")
    public void testUsernameFieldClearAndRetype() {
        // Type initial text
        loginPage.enterUsername("initial_text");

        // Clear and type the correct username by using login which calls type() (which clears first)
        loginPage.enterUsername("tomsmith");
        loginPage.enterPassword("SuperSecretPassword!");
        loginPage.clickLogin();

        Assert.assertTrue(secureAreaPage.isLogoutVisible(),
                "Should be able to login after clearing and retyping username");
        Assert.assertTrue(secureAreaPage.getFlashMessage().contains("You logged into a secure area!"),
                "Success flash message should be displayed");
    }
}
