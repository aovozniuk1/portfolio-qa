package junit;

import config.ConfigReader;
import config.DriverFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.SecureAreaPage;

/**
 * JUnit 5 mirror of the core TestNG login coverage.
 * <p>
 * Kept intentionally small: the primary suite is TestNG. This exists so
 * a reviewer can see the same framework wired to JUnit 5 + AssertJ and
 * run independently with `mvn -Pjunit test`.
 */
@Feature("Authentication")
@Story("Login (JUnit 5)")
class LoginJUnitTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private SecureAreaPage secureAreaPage;

    @BeforeEach
    void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
        driver.get(ConfigReader.getBaseUrl() + "/login");
        loginPage = new LoginPage(driver);
        secureAreaPage = new SecureAreaPage(driver);
    }

    @AfterEach
    void tearDown() {
        DriverFactory.quitDriver();
    }

    @Test
    @DisplayName("valid credentials land on the secure area")
    @Severity(SeverityLevel.BLOCKER)
    @Description("JUnit 5 + AssertJ: valid login shows logout button and success flash")
    void validLogin() {
        loginPage.login("tomsmith", "SuperSecretPassword!");

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(secureAreaPage.isLogoutVisible())
                .as("logout button visible")
                .isTrue();
        soft.assertThat(secureAreaPage.getFlashMessage())
                .as("success flash")
                .contains("You logged into a secure area");
        soft.assertAll();
    }

    @ParameterizedTest(name = "[{index}] username={0}")
    @CsvSource({
            "tomsmith, WrongPassword,  Your password is invalid",
            "nobody,   SuperSecret!,   Your username is invalid",
            "'',       '',             Your username is invalid",
    })
    @DisplayName("invalid credentials are rejected with the right flash")
    void invalidLogin(String username, String password, String expectedFlash) {
        loginPage.login(username, password);

        org.assertj.core.api.Assertions.assertThat(loginPage.getFlashMessage())
                .as("flash for %s/%s", username, password)
                .contains(expectedFlash);
    }
}
