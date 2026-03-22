package tests;

import config.ConfigReader;
import config.DriverFactory;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

/**
 * Base class for all UI tests.
 * <p>
 * Uses {@link DriverFactory} for thread-safe WebDriver management,
 * supporting parallel test execution. Captures screenshots on failure
 * and attaches them to Allure reports.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                captureScreenshotOnFailure(result.getName());
            }
        }
        DriverFactory.quitDriver();
    }

    /**
     * Attach a screenshot to Allure when a test fails.
     *
     * @param testName the name of the failed test
     */
    private void captureScreenshotOnFailure(String testName) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(
                    "Failure: " + testName,
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
        } catch (Exception e) {
            // screenshot capture should never break the test
        }
    }
}
