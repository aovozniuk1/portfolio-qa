package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
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
}
