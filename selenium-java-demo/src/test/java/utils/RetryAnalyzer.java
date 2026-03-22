package utils;

import config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyser for flaky tests.
 * <p>
 * Re-runs a failed test up to {@code retry.count} times (configurable via
 * {@code config.properties} or the {@code -Dretry.count} system property).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetries = ConfigReader.getRetryCount();
        if (attempt < maxRetries) {
            attempt++;
            return true;
        }
        return false;
    }
}
