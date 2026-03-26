package config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Thread-safe WebDriver factory using ThreadLocal for parallel execution support.
 * <p>
 * Each thread gets its own WebDriver instance, preventing conflicts during
 * parallel test execution with TestNG. Uses Selenium 4's built-in driver
 * management (SeleniumManager) -- no third-party WebDriverManager needed.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverFactory() {
        // utility class
    }

    /**
     * Get the WebDriver instance for the current thread.
     *
     * @return the WebDriver instance
     * @throws IllegalStateException if no driver has been initialised for this thread
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver has not been initialised for this thread. Call initDriver() first.");
        }
        return driver;
    }

    /**
     * Initialise a WebDriver based on configuration and store it in ThreadLocal.
     * <p>
     * Reads browser type, headless mode, and timeout settings from {@link ConfigReader}.
     * When {@code selenium.grid.url} is set, a {@link RemoteWebDriver} is created
     * to run tests against a Selenium Grid (e.g. via Docker Compose).
     * <p>
     * Local drivers rely on Selenium 4's built-in SeleniumManager for automatic
     * browser driver resolution -- no manual setup or third-party libraries required.
     */
    public static void initDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        String gridUrl = ConfigReader.get("selenium.grid.url", "");
        boolean useGrid = !gridUrl.isEmpty();
        WebDriver driver;

        switch (browser) {
            case "firefox":
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (ConfigReader.isHeadless()) {
                    ffOptions.addArguments("--headless");
                }
                if (useGrid) {
                    driver = createRemoteDriver(gridUrl, ffOptions);
                } else {
                    driver = new FirefoxDriver(ffOptions);
                }
                break;
            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--window-size=1280,720");
                if (ConfigReader.isHeadless()) {
                    chromeOptions.addArguments("--headless=new");
                }
                if (useGrid) {
                    driver = createRemoteDriver(gridUrl, chromeOptions);
                } else {
                    driver = new ChromeDriver(chromeOptions);
                }
                break;
        }

        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.manage().window().maximize();

        DRIVER_THREAD_LOCAL.set(driver);
    }

    /**
     * Create a {@link RemoteWebDriver} pointing at the given Selenium Grid URL.
     *
     * @param gridUrl     the grid hub URL (e.g. {@code http://selenium-hub:4444})
     * @param capabilities browser options / capabilities
     * @return a new RemoteWebDriver instance
     */
    private static WebDriver createRemoteDriver(String gridUrl,
                                                 org.openqa.selenium.Capabilities capabilities) {
        try {
            return new RemoteWebDriver(new URL(gridUrl), capabilities);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                    "Invalid selenium.grid.url: " + gridUrl, e);
        }
    }

    /**
     * Quit the WebDriver and remove it from ThreadLocal.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            driver.quit();
            DRIVER_THREAD_LOCAL.remove();
        }
    }
}
