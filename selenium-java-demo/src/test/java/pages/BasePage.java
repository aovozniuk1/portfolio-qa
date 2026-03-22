package pages;

import config.ConfigReader;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base page object providing shared browser interaction utilities.
 * <p>
 * Includes explicit waits, fluent waits, JavaScript helpers, and
 * common element operations. All page objects extend this class.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    private static final Duration DEFAULT_TIMEOUT =
            Duration.ofSeconds(ConfigReader.getExplicitWait());

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    // ------------------------------------------------------------------ //
    //  Navigation
    // ------------------------------------------------------------------ //

    @Step("Open URL: {url}")
    protected void openUrl(String url) {
        driver.get(url);
    }

    @Step("Navigate to path: {path}")
    protected void navigateTo(String path) {
        driver.get(ConfigReader.getBaseUrl() + path);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    // ------------------------------------------------------------------ //
    //  Explicit wait helpers
    // ------------------------------------------------------------------ //

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected boolean waitForTextPresent(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    // ------------------------------------------------------------------ //
    //  Fluent wait helper
    // ------------------------------------------------------------------ //

    /**
     * Wait for an element with custom polling interval and ignored exceptions.
     *
     * @param locator          element locator
     * @param timeoutSeconds   max wait time
     * @param pollingMillis    polling interval
     * @return the found element
     */
    protected WebElement fluentWait(By locator, int timeoutSeconds, int pollingMillis) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        return fluentWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ------------------------------------------------------------------ //
    //  Element interactions
    // ------------------------------------------------------------------ //

    @Step("Click element: {locator}")
    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    @Step("Type '{text}' into element: {locator}")
    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getAttribute(attribute);
    }

    protected List<String> getTexts(By locator) {
        List<WebElement> elements = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        return elements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ //
    //  JavaScript helpers
    // ------------------------------------------------------------------ //

    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", element);
    }

    protected void jsClick(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void highlightElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.border='3px solid red';", element);
    }

    // ------------------------------------------------------------------ //
    //  Screenshot
    // ------------------------------------------------------------------ //

    /**
     * Capture a screenshot and return it as a byte array.
     *
     * @return PNG screenshot bytes
     */
    public byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
