package tests;

import io.qameta.allure.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.WindowsPage;
import utils.RetryAnalyzer;

import java.time.Duration;

/**
 * UI tests for multi-window handling.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/windows">/windows</a>
 */
@Feature("Multi-Window")
@Story("Window Handling")
public class WindowsTest extends BaseTest {

    private WindowsPage windowsPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        windowsPage = new WindowsPage(driver).open();
    }

    /**
     * Wait for a specific number of browser windows/tabs to be open.
     */
    private void waitForWindowCount(int expectedCount) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getWindowHandles().size() == expectedCount);
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Click link to open new window and verify window count becomes 2")
    public void testOpenNewWindow() {
        Assert.assertEquals(windowsPage.getWindowCount(), 1,
                "Initially there should be 1 window");

        windowsPage.clickNewWindowLink();
        waitForWindowCount(2);

        Assert.assertEquals(windowsPage.getWindowCount(), 2,
                "After clicking the link, there should be 2 windows");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Open new window, switch to it, verify heading, switch back, verify original heading")
    public void testSwitchBetweenWindows() {
        String originalHeading = windowsPage.getHeaderText();
        Assert.assertEquals(originalHeading, "Opening a new window",
                "Original page heading should match");

        windowsPage.clickNewWindowLink();
        waitForWindowCount(2);

        windowsPage.switchToNewWindow();
        String newWindowHeading = windowsPage.getHeaderText();
        Assert.assertEquals(newWindowHeading, "New Window",
                "New window heading should be 'New Window'");

        windowsPage.switchToOriginalWindow();
        String backToOriginal = windowsPage.getHeaderText();
        Assert.assertEquals(backToOriginal, "Opening a new window",
                "Should be back to original heading after switching back");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Open new window, switch to it, close it, switch back, verify URL and heading")
    public void testCloseNewWindowAndReturn() {
        String originalUrl = driver.getCurrentUrl();

        windowsPage.clickNewWindowLink();
        waitForWindowCount(2);

        Assert.assertEquals(windowsPage.getWindowCount(), 2,
                "Should have 2 windows open");

        windowsPage.switchToNewWindow();
        Assert.assertEquals(windowsPage.getHeaderText(), "New Window",
                "Should be on the new window");

        windowsPage.closeCurrentWindow();
        windowsPage.switchToOriginalWindow();

        Assert.assertEquals(windowsPage.getWindowCount(), 1,
                "After closing new window, should have 1 window");
        Assert.assertEquals(windowsPage.getHeaderText(), "Opening a new window",
                "Should be back on the original window");
        Assert.assertEquals(driver.getCurrentUrl(), originalUrl,
                "URL should match the original window URL after returning");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Open new window and verify its URL contains /windows/new")
    public void testNewWindowUrl() {
        windowsPage.clickNewWindowLink();
        waitForWindowCount(2);

        windowsPage.switchToNewWindow();

        String newWindowUrl = driver.getCurrentUrl();
        Assert.assertTrue(newWindowUrl.contains("/windows/new"),
                "New window URL should contain '/windows/new', got: " + newWindowUrl);

        windowsPage.switchToOriginalWindow();
        String originalUrl = driver.getCurrentUrl();
        Assert.assertTrue(originalUrl.contains("/windows"),
                "Original window URL should contain '/windows', got: " + originalUrl);
        Assert.assertFalse(originalUrl.contains("/windows/new"),
                "Original window URL should NOT contain '/windows/new'");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Open window, switch back and forth multiple times, verify context each time")
    public void testMultipleSwitchCycles() {
        windowsPage.clickNewWindowLink();
        waitForWindowCount(2);

        for (int cycle = 1; cycle <= 4; cycle++) {
            windowsPage.switchToNewWindow();
            Assert.assertEquals(windowsPage.getHeaderText(), "New Window",
                    "Cycle " + cycle + ": New window heading should be 'New Window'");
            Assert.assertTrue(driver.getCurrentUrl().contains("/windows/new"),
                    "Cycle " + cycle + ": New window URL should contain '/windows/new'");

            windowsPage.switchToOriginalWindow();
            Assert.assertEquals(windowsPage.getHeaderText(), "Opening a new window",
                    "Cycle " + cycle + ": Original window heading should match");
            Assert.assertTrue(driver.getCurrentUrl().contains("/windows"),
                    "Cycle " + cycle + ": Original window URL should contain '/windows'");
        }
    }
}
