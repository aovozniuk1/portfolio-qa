package tests;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.IframePage;
import utils.RetryAnalyzer;

/**
 * UI tests for the TinyMCE WYSIWYG Editor iframe.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/iframe">/iframe</a>
 */
@Feature("Iframes")
@Story("TinyMCE Editor")
public class IframeTest extends BaseTest {

    private IframePage iframePage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        iframePage = new IframePage(driver).open();
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Clear default text, set new text via TinyMCE API, and verify content")
    public void testTypeInIframe() {
        String testText = "Hello from Selenium!";

        iframePage.selectAllAndClear()
                .typeInEditor(testText);

        String actualText = iframePage.getEditorText();
        Assert.assertEquals(actualText, testText,
                "Editor text should match the typed text");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Set text, read it multiple times, verify it persists")
    public void testIframeContentPersistence() {
        String testText = "Persistent content test";

        iframePage.selectAllAndClear()
                .typeInEditor(testText);

        // Read text multiple times to verify persistence
        String text1 = iframePage.getEditorText();
        Assert.assertEquals(text1, testText, "First read should match");

        String text2 = iframePage.getEditorText();
        Assert.assertEquals(text2, testText, "Second read should still match");

        String text3 = iframePage.getEditorText();
        Assert.assertEquals(text3, testText, "Third read should still match");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Set a long text string into the editor and verify full content is preserved")
    public void testTypeLongText() {
        String longText = "This is a longer piece of text designed to test the TinyMCE editor's " +
                "ability to handle substantial input. It includes multiple sentences and should be " +
                "fully preserved when read back from the editor content area.";

        iframePage.selectAllAndClear()
                .typeInEditor(longText);

        String actualText = iframePage.getEditorText();
        Assert.assertEquals(actualText, longText,
                "Long text should be fully preserved in the editor");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Set text, clear it, set new text, and verify only the new text remains")
    public void testTypeClearAndRetype() {
        String firstText = "First input";
        String secondText = "Second input after clearing";

        // Set first text
        iframePage.selectAllAndClear()
                .typeInEditor(firstText);

        String afterFirst = iframePage.getEditorText();
        Assert.assertEquals(afterFirst, firstText,
                "First text should be present after setting");

        // Clear and set second text
        iframePage.selectAllAndClear()
                .typeInEditor(secondText);

        String afterSecond = iframePage.getEditorText();
        Assert.assertEquals(afterSecond, secondText,
                "Only the second text should remain after clear and retype");
        Assert.assertFalse(afterSecond.contains(firstText),
                "First text should not be present after clearing and retyping");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify the editor iframe element attributes (id and src)")
    public void testEditorFrameAttributes() {
        SoftAssert softAssert = new SoftAssert();

        WebElement iframe = driver.findElement(By.id("mce_0_ifr"));

        String id = iframe.getAttribute("id");
        softAssert.assertEquals(id, "mce_0_ifr",
                "Iframe ID should be 'mce_0_ifr'");

        String src = iframe.getAttribute("src");
        softAssert.assertNotNull(src,
                "Iframe src attribute should not be null");

        String tagName = iframe.getTagName().toLowerCase();
        softAssert.assertEquals(tagName, "iframe",
                "Element should be an iframe tag, got: " + tagName);

        // Verify the iframe has dimensions (is rendered)
        int width = iframe.getSize().getWidth();
        int height = iframe.getSize().getHeight();
        softAssert.assertTrue(width > 0,
                "Iframe width should be greater than 0, got: " + width);
        softAssert.assertTrue(height > 0,
                "Iframe height should be greater than 0, got: " + height);

        softAssert.assertAll();
    }
}
