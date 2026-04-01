package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Page object for the Multiple Windows page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/windows">/windows</a>
 */
public class WindowsPage extends BasePage {

    // -- Selectors -------------------------------------------------------- //
    private static final By NEW_WINDOW_LINK = By.cssSelector("a[href='/windows/new']");
    private static final By PAGE_HEADING    = By.tagName("h3");

    private String originalWindowHandle;

    public WindowsPage(WebDriver driver) {
        super(driver);
    }

    // -- Actions ---------------------------------------------------------- //

    @Step("Open Windows page")
    public WindowsPage open() {
        navigateTo("/windows");
        originalWindowHandle = driver.getWindowHandle();
        return this;
    }

    @Step("Click 'Click Here' to open new window")
    public WindowsPage clickNewWindowLink() {
        click(NEW_WINDOW_LINK);
        return this;
    }

    @Step("Switch to new window")
    public WindowsPage switchToNewWindow() {
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(originalWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
        return this;
    }

    @Step("Switch back to original window")
    public WindowsPage switchToOriginalWindow() {
        driver.switchTo().window(originalWindowHandle);
        return this;
    }

    @Step("Close current window")
    public WindowsPage closeCurrentWindow() {
        driver.close();
        return this;
    }

    // -- State readers ---------------------------------------------------- //

    public String getHeaderText() {
        return getText(PAGE_HEADING).trim();
    }

    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    public int getWindowCount() {
        return driver.getWindowHandles().size();
    }
}
