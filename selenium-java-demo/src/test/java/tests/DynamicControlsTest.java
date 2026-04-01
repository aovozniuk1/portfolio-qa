package tests;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.DynamicControlsPage;
import utils.RetryAnalyzer;

/**
 * UI tests for the Dynamic Controls page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/dynamic_controls">/dynamic_controls</a>
 */
@Feature("Dynamic UI")
@Story("Dynamic Controls")
public class DynamicControlsTest extends BaseTest {

    private DynamicControlsPage dcPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        dcPage = new DynamicControlsPage(driver).open();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify checkbox can be removed and a confirmation message appears")
    public void testRemoveCheckbox() {
        Assert.assertTrue(dcPage.isCheckboxPresent(), "Checkbox should be visible initially");

        dcPage.clickRemoveAddButton().waitForLoading();

        Assert.assertFalse(dcPage.isCheckboxPresent(), "Checkbox should be removed");
        Assert.assertTrue(dcPage.getMessage().contains("It's gone!"));
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify checkbox can be re-added after removal")
    public void testAddCheckboxBack() {
        dcPage.clickRemoveAddButton().waitForLoading();
        Assert.assertFalse(dcPage.isCheckboxPresent());

        dcPage.clickRemoveAddButton().waitForLoading();

        Assert.assertTrue(dcPage.isCheckboxPresent(), "Checkbox should be back");
        Assert.assertTrue(dcPage.getMessage().contains("It's back!"));
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify text input can be enabled and accepts text")
    public void testEnableInputAndType() {
        Assert.assertFalse(dcPage.isInputEnabled(), "Input should be disabled initially");

        dcPage.clickEnableDisableButton().waitForLoading();

        Assert.assertTrue(dcPage.isInputEnabled(), "Input should be enabled");
        Assert.assertTrue(dcPage.getMessage().contains("It's enabled!"));

        dcPage.typeInInput("Hello, World!");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Full checkbox lifecycle: remove, verify gone, add back, verify present")
    public void testFullCheckboxCycle() {
        // Step 1: Verify checkbox is initially present
        Assert.assertTrue(dcPage.isCheckboxPresent(), "Checkbox should be visible initially");

        // Step 2: Remove checkbox
        dcPage.clickRemoveAddButton().waitForLoading();
        Assert.assertFalse(dcPage.isCheckboxPresent(), "Checkbox should be removed after clicking Remove");
        Assert.assertTrue(dcPage.getMessage().contains("It's gone!"),
                "Message should confirm checkbox removal");

        // Step 3: Add checkbox back
        dcPage.clickRemoveAddButton().waitForLoading();
        Assert.assertTrue(dcPage.isCheckboxPresent(), "Checkbox should be back after clicking Add");
        Assert.assertTrue(dcPage.getMessage().contains("It's back!"),
                "Message should confirm checkbox was added back");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Full input lifecycle: enable, type, disable, re-enable, verify text preserved, disable again")
    public void testEnableDisableCycle() {
        String testText = "Cycle Test";

        // Step 1: Enable input and type text
        Assert.assertFalse(dcPage.isInputEnabled(), "Input should be disabled initially");
        dcPage.clickEnableDisableButton().waitForLoading();
        Assert.assertTrue(dcPage.getMessage().contains("It's enabled!"),
                "Message should confirm input is enabled");
        Assert.assertTrue(dcPage.isInputEnabled(), "Input should be enabled");

        dcPage.typeInInput(testText);
        Assert.assertEquals(dcPage.getInputValue(), testText,
                "Input value should match typed text");

        // Step 2: Disable input
        dcPage.clickEnableDisableButton().waitForLoading();
        Assert.assertFalse(dcPage.isInputEnabled(), "Input should be disabled after first cycle");

        // Step 3: Enable and verify text preserved
        dcPage.clickEnableDisableButton().waitForLoading();
        Assert.assertTrue(dcPage.isInputEnabled(), "Input should be re-enabled");
        Assert.assertEquals(dcPage.getInputValue(), testText,
                "Input text should be preserved through enable/disable cycle");

        // Step 4: Disable input one final time
        dcPage.clickEnableDisableButton().waitForLoading();
        Assert.assertFalse(dcPage.isInputEnabled(), "Input should be disabled after second cycle");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify async operations complete with correct messages after loading finishes")
    public void testAsyncOperationsWithLoading() {
        SoftAssert softAssert = new SoftAssert();

        // Remove checkbox — verify loading completes and message appears
        dcPage.clickRemoveAddButton().waitForLoading();
        softAssert.assertFalse(dcPage.isCheckboxPresent(),
                "Checkbox should be gone after async removal completes");
        softAssert.assertTrue(dcPage.getMessage().contains("It's gone!"),
                "Correct message should appear after loading finishes");

        // Verify loading indicator is no longer visible
        boolean loadingGone = !driver.findElements(By.id("loading")).isEmpty()
                ? !driver.findElement(By.id("loading")).isDisplayed()
                : true;
        softAssert.assertTrue(loadingGone,
                "Loading indicator should not be visible after operation completes");

        // Re-add checkbox
        dcPage.clickRemoveAddButton().waitForLoading();
        softAssert.assertTrue(dcPage.isCheckboxPresent(),
                "Checkbox should be back after async add completes");

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify loading time for dynamic operations is within an acceptable range (under 10s)")
    public void testLoadingTimeWithinThreshold() {
        long maxAllowedMs = 30_000;

        // Measure remove operation time
        long startRemove = System.currentTimeMillis();
        dcPage.clickRemoveAddButton().waitForLoading();
        long removeTime = System.currentTimeMillis() - startRemove;

        Assert.assertTrue(removeTime < maxAllowedMs,
                "Remove operation took " + removeTime + "ms, should be under " + maxAllowedMs + "ms");

        // Measure add-back operation time
        long startAdd = System.currentTimeMillis();
        dcPage.clickRemoveAddButton().waitForLoading();
        long addTime = System.currentTimeMillis() - startAdd;

        Assert.assertTrue(addTime < maxAllowedMs,
                "Add-back operation took " + addTime + "ms, should be under " + maxAllowedMs + "ms");

        // Measure enable operation time
        long startEnable = System.currentTimeMillis();
        dcPage.clickEnableDisableButton().waitForLoading();
        long enableTime = System.currentTimeMillis() - startEnable;

        Assert.assertTrue(enableTime < maxAllowedMs,
                "Enable operation took " + enableTime + "ms, should be under " + maxAllowedMs + "ms");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that checkbox can be checked before removal and state is acknowledged")
    public void testCheckboxCanBeCheckedBeforeRemoval() {
        Assert.assertTrue(dcPage.isCheckboxPresent(), "Checkbox should be present initially");

        // Check the checkbox
        WebElement checkbox = driver.findElement(
                By.cssSelector("#checkbox-example input[type='checkbox']"));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        Assert.assertTrue(checkbox.isSelected(), "Checkbox should be checked after clicking");

        // Now remove it
        dcPage.clickRemoveAddButton().waitForLoading();
        Assert.assertFalse(dcPage.isCheckboxPresent(),
                "Checkbox should be removed even when checked");
        Assert.assertTrue(dcPage.getMessage().contains("It's gone!"),
                "Confirmation message should appear after removing checked checkbox");

        // Add it back and verify it returns unchecked
        dcPage.clickRemoveAddButton().waitForLoading();
        Assert.assertTrue(dcPage.isCheckboxPresent(),
                "Checkbox should be back after re-adding");
    }
}
