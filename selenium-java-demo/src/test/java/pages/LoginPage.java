package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the Herokuapp login page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/login">/login</a>
 */
public class LoginPage extends BasePage {

    // -- Selectors -------------------------------------------------------- //
    private static final By USERNAME_FIELD = By.id("username");
    private static final By PASSWORD_FIELD = By.id("password");
    private static final By LOGIN_BUTTON   = By.cssSelector("button[type='submit']");
    private static final By FLASH_MESSAGE  = By.id("flash");
    private static final By LOGOUT_BUTTON  = By.cssSelector("a[href='/logout']");
    private static final By PAGE_HEADING   = By.tagName("h2");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // -- Actions ---------------------------------------------------------- //

    @Step("Open login page")
    public LoginPage open() {
        navigateTo("/login");
        return this;
    }

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        type(USERNAME_FIELD, username);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(PASSWORD_FIELD, password);
        return this;
    }

    @Step("Click Login button")
    public LoginPage clickLogin() {
        click(LOGIN_BUTTON);
        return this;
    }

    @Step("Login as '{username}'")
    public LoginPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        // Wait for the form submission to settle. The flash banner is present
        // on both success and failure, so this works for positive + negative
        // paths without forcing callers to add their own wait.
        waitForVisible(FLASH_MESSAGE);
        return this;
    }

    @Step("Click Logout button")
    public LoginPage clickLogout() {
        click(LOGOUT_BUTTON);
        return this;
    }

    // -- State readers ---------------------------------------------------- //

    public String getFlashMessage() {
        return getText(FLASH_MESSAGE).trim();
    }

    public boolean isLoggedIn() {
        return isDisplayed(LOGOUT_BUTTON);
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING).trim();
    }

    public boolean isUsernameFieldDisplayed() {
        return isDisplayed(USERNAME_FIELD);
    }

    public boolean isPasswordFieldDisplayed() {
        return isDisplayed(PASSWORD_FIELD);
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(LOGIN_BUTTON);
    }
}
