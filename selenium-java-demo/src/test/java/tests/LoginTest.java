package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
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
                {"tomsmith", "WrongPassword",  "Your password is invalid!"},
                {"invalid_user", "SuperSecretPassword!", "Your username is invalid!"},
                {"", "",                        "Your username is invalid!"},
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
}
