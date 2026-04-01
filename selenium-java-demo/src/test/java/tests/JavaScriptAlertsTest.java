package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.JavaScriptAlertsPage;
import utils.RetryAnalyzer;

/**
 * UI tests for JavaScript Alerts, Confirms, and Prompts.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/javascript_alerts">/javascript_alerts</a>
 */
@Feature("JavaScript Alerts")
@Story("Alert Handling")
public class JavaScriptAlertsTest extends BaseTest {

    private JavaScriptAlertsPage alertsPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        alertsPage = new JavaScriptAlertsPage(driver).open();
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Trigger a JS Alert, accept it, and verify the result text")
    public void testAcceptJsAlert() {
        alertsPage.clickJsAlert()
                .acceptAlert();

        Assert.assertEquals(alertsPage.getResultText(),
                "You successfully clicked an alert",
                "Result text should confirm alert was accepted");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Trigger a JS Confirm, dismiss it, and verify Cancel result")
    public void testDismissJsConfirm() {
        alertsPage.clickJsConfirm()
                .dismissAlert();

        Assert.assertEquals(alertsPage.getResultText(),
                "You clicked: Cancel",
                "Result text should show Cancel");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Trigger a JS Confirm, accept it, and verify OK result")
    public void testAcceptJsConfirm() {
        alertsPage.clickJsConfirm()
                .acceptAlert();

        Assert.assertEquals(alertsPage.getResultText(),
                "You clicked: Ok",
                "Result text should show Ok");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Trigger a JS Prompt, type text, accept, and verify typed text in result")
    public void testJsPromptWithInput() {
        String inputText = "Selenium Test Input";

        alertsPage.clickJsPrompt()
                .typeInPrompt(inputText)
                .acceptAlert();

        Assert.assertEquals(alertsPage.getResultText(),
                "You entered: " + inputText,
                "Result text should contain the typed input");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Trigger a JS Prompt, dismiss it, and verify null result")
    public void testJsPromptDismiss() {
        alertsPage.clickJsPrompt()
                .dismissAlert();

        Assert.assertEquals(alertsPage.getResultText(),
                "You entered: null",
                "Result text should show null when prompt is dismissed");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Trigger a JS Prompt with empty input, accept, and verify empty result")
    public void testJsPromptWithEmptyInput() {
        alertsPage.clickJsPrompt()
                .acceptAlert();

        Assert.assertEquals(alertsPage.getResultText(),
                "You entered:",
                "Result text should show empty value when prompt accepted without input");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Trigger a JS Prompt with special characters and verify they are preserved in result")
    public void testJsPromptWithSpecialCharacters() {
        String specialInput = "!@#$%^&*()_+-=<>?";

        alertsPage.clickJsPrompt()
                .typeInPrompt(specialInput)
                .acceptAlert();

        Assert.assertEquals(alertsPage.getResultText(),
                "You entered: " + specialInput,
                "Result text should preserve special characters");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Full flow: trigger alert, confirm, and prompt sequentially, verifying each result")
    public void testFullAlertConfirmPromptFlow() {
        // Step 1: JS Alert
        alertsPage.clickJsAlert()
                .acceptAlert();
        Assert.assertEquals(alertsPage.getResultText(),
                "You successfully clicked an alert",
                "Step 1: Alert result should confirm acceptance");

        // Step 2: JS Confirm (accept)
        alertsPage.clickJsConfirm()
                .acceptAlert();
        Assert.assertEquals(alertsPage.getResultText(),
                "You clicked: Ok",
                "Step 2: Confirm result should show Ok");

        // Step 3: JS Confirm (dismiss)
        alertsPage.clickJsConfirm()
                .dismissAlert();
        Assert.assertEquals(alertsPage.getResultText(),
                "You clicked: Cancel",
                "Step 3: Confirm dismiss should show Cancel");

        // Step 4: JS Prompt with text
        String promptText = "End-to-end flow test";
        alertsPage.clickJsPrompt()
                .typeInPrompt(promptText)
                .acceptAlert();
        Assert.assertEquals(alertsPage.getResultText(),
                "You entered: " + promptText,
                "Step 4: Prompt result should match typed text");

        // Step 5: JS Prompt dismissed
        alertsPage.clickJsPrompt()
                .dismissAlert();
        Assert.assertEquals(alertsPage.getResultText(),
                "You entered: null",
                "Step 5: Dismissed prompt should show null");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify the initial result text area is empty or has default content before any alert")
    public void testInitialResultTextIsDefault() {
        // The result element may not exist at all before any alert is triggered
        try {
            String resultText = alertsPage.getResultText();
            Assert.assertFalse(resultText.contains("You successfully clicked"),
                    "Initial result should not contain alert result text");
            Assert.assertFalse(resultText.contains("You clicked:"),
                    "Initial result should not contain confirm result text");
            Assert.assertFalse(resultText.contains("You entered:"),
                    "Initial result should not contain prompt result text");
        } catch (org.openqa.selenium.TimeoutException e) {
            // Result element not present before interaction — expected behavior
        }
    }
}
