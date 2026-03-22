package utils;

import config.DriverFactory;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * Custom TestNG listener for Allure reporting integration.
 * <p>
 * Automatically captures screenshots on test failure and attaches them
 * to the Allure report. Also logs test lifecycle events.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("=== Suite started: {} ===", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("=== Suite finished: {} | Passed: {} | Failed: {} | Skipped: {} ===",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info(">> Test started: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("<< Test PASSED: {} ({}ms)",
                result.getMethod().getMethodName(),
                result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("<< Test FAILED: {} -- {}",
                result.getMethod().getMethodName(),
                result.getThrowable().getMessage());
        captureScreenshot(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("<< Test SKIPPED: {}", result.getMethod().getMethodName());
    }

    /**
     * Capture a screenshot and attach it to the Allure report.
     *
     * @param testName the name of the failed test
     */
    private void captureScreenshot(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(
                    "Failure: " + testName,
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
        } catch (Exception e) {
            log.warn("Could not capture failure screenshot: {}", e.getMessage());
        }
    }
}
