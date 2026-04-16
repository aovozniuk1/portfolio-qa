package bdd.steps;

import config.ConfigReader;
import config.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import pages.SecureAreaPage;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginSteps {

    private WebDriver driver;
    private LoginPage loginPage;
    private SecureAreaPage secureAreaPage;

    @Before
    public void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
        loginPage = new LoginPage(driver);
        secureAreaPage = new SecureAreaPage(driver);
    }

    @After
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        driver.get(ConfigReader.getBaseUrl() + "/login");
    }

    @When("I log in with {string} and {string}")
    public void iLogInWith(String username, String password) {
        loginPage.login(username, password);
    }

    @Then("I should be on the secure area")
    public void iShouldBeOnTheSecureArea() {
        assertThat(driver.getCurrentUrl()).contains("/secure");
        assertThat(secureAreaPage.isLogoutVisible()).isTrue();
    }

    @Then("I should still be on the login page")
    public void iShouldStillBeOnTheLoginPage() {
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Then("the flash message should contain {string}")
    public void theFlashMessageShouldContain(String expected) {
        String actual = driver.getCurrentUrl().contains("/secure")
                ? secureAreaPage.getFlashMessage()
                : loginPage.getFlashMessage();
        assertThat(actual).contains(expected);
    }
}
