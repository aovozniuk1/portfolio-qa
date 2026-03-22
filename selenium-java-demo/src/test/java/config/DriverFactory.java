package config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory using ThreadLocal for parallel execution support.
 * <p>
 * Each thread gets its own WebDriver instance, preventing conflicts during
 * parallel test execution with TestNG.
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
     */
    public static void initDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        WebDriver driver;

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (ConfigReader.isHeadless()) {
                    ffOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(ffOptions);
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--window-size=1280,720");
                if (ConfigReader.isHeadless()) {
                    chromeOptions.addArguments("--headless=new");
                }
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.manage().window().maximize();

        DRIVER_THREAD_LOCAL.set(driver);
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
