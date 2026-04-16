package cdp;

import config.ConfigReader;
import config.DriverFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.remote.http.Filter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Chrome DevTools Protocol example using Selenium 4's version-agnostic
 * {@link NetworkInterceptor}. Captures the URLs of every request made
 * during a page load, including the main document.
 * <p>
 * Unlike pinning {@code devtools.v131.Network} imports (which break on
 * every Chrome major bump), NetworkInterceptor survives browser upgrades.
 */
@Feature("Chrome DevTools Protocol")
public class CdpNetworkTest {

    private WebDriver driver;
    private NetworkInterceptor interceptor;
    private final List<String> observedUrls = new CopyOnWriteArrayList<>();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        if (!"chrome".equalsIgnoreCase(ConfigReader.getBrowser())) {
            throw new org.testng.SkipException(
                    "CDP path is Chrome-only; skipping on " + ConfigReader.getBrowser());
        }
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();

        Filter observer = next -> req -> {
            observedUrls.add(req.getUri());
            return next.execute(req);
        };
        interceptor = new NetworkInterceptor(driver, observer);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (interceptor != null) {
            interceptor.close();
        }
        DriverFactory.quitDriver();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("NetworkInterceptor records the main document URL")
    public void capturesMainDocumentRequest() {
        driver.get(ConfigReader.getBaseUrl() + "/login");

        assertThat(observedUrls)
                .as("captured network requests")
                .isNotEmpty()
                .anyMatch(url -> url.contains("/login"));
    }
}
