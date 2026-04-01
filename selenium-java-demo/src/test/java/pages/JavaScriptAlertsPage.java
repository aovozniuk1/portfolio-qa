package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page object for the JavaScript Alerts page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/javascript_alerts">/javascript_alerts</a>
 */
public class JavaScriptAlertsPage extends BasePage {

    // -- Selectors -------------------------------------------------------- //
    private static final By JS_ALERT_BUTTON   = By.cssSelector("button[onclick='jsAlert()']");
    private static final By JS_CONFIRM_BUTTON = By.cssSelector("button[onclick='jsConfirm()']");
    private static final By JS_PROMPT_BUTTON  = By.cssSelector("button[onclick='jsPrompt()']");
    private static final By RESULT_TEXT       = By.id("result");

    public JavaScriptAlertsPage(WebDriver driver) {
        super(driver);
    }

    // -- Actions ---------------------------------------------------------- //

    @Step("Open JavaScript Alerts page")
    public JavaScriptAlertsPage open() {
        navigateTo("/javascript_alerts");
        return this;
    }

    @Step("Click JS Alert button")
    public JavaScriptAlertsPage clickJsAlert() {
        click(JS_ALERT_BUTTON);
        return this;
    }

    @Step("Click JS Confirm button")
    public JavaScriptAlertsPage clickJsConfirm() {
        click(JS_CONFIRM_BUTTON);
        return this;
    }

    @Step("Click JS Prompt button")
    public JavaScriptAlertsPage clickJsPrompt() {
        click(JS_PROMPT_BUTTON);
        return this;
    }

    @Step("Accept alert")
    public JavaScriptAlertsPage acceptAlert() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        return this;
    }

    @Step("Dismiss alert")
    public JavaScriptAlertsPage dismissAlert() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.dismiss();
        return this;
    }

    @Step("Type '{text}' into prompt")
    public JavaScriptAlertsPage typeInPrompt(String text) {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.sendKeys(text);
        return this;
    }

    // -- State readers ---------------------------------------------------- //

    public String getResultText() {
        return getText(RESULT_TEXT).trim();
    }
}
