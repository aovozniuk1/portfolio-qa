package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the authenticated Secure Area.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/secure">/secure</a>
 */
public class SecureAreaPage extends BasePage {

    private static final By FLASH_MESSAGE  = By.id("flash");
    private static final By LOGOUT_BUTTON  = By.cssSelector("a[href='/logout']");
    private static final By PAGE_HEADING   = By.tagName("h2");

    public SecureAreaPage(WebDriver driver) {
        super(driver);
    }

    @Step("Click Logout")
    public void clickLogout() {
        click(LOGOUT_BUTTON);
    }

    public String getFlashMessage() {
        return getText(FLASH_MESSAGE).trim();
    }

    public String getHeading() {
        return getText(PAGE_HEADING).trim();
    }

    public boolean isLogoutVisible() {
        return isDisplayed(LOGOUT_BUTTON);
    }
}
