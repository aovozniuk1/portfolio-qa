package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page object for the WYSIWYG Editor (TinyMCE iframe) page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/iframe">/iframe</a>
 */
public class IframePage extends BasePage {

    // -- Selectors -------------------------------------------------------- //
    private static final By EDITOR_IFRAME  = By.id("mce_0_ifr");
    private static final By EDITOR_BODY    = By.id("tinymce");
    private static final By BOLD_BUTTON    = By.cssSelector("button[aria-label='Bold']");
    private static final By ITALIC_BUTTON  = By.cssSelector("button[aria-label='Italic']");
    private static final By PAGE_HEADING   = By.tagName("h3");

    public IframePage(WebDriver driver) {
        super(driver);
    }

    // -- Actions ---------------------------------------------------------- //

    @Step("Open iframe editor page")
    public IframePage open() {
        navigateTo("/iframe");
        // Wait for TinyMCE to fully initialize before interacting
        wait.until(driver -> ((JavascriptExecutor) driver).executeScript(
                "return typeof tinyMCE !== 'undefined' && tinyMCE.activeEditor != null "
                        + "&& tinyMCE.activeEditor.initialized;"));
        return this;
    }

    @Step("Switch to editor iframe")
    public IframePage switchToEditorIframe() {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(EDITOR_IFRAME));
        return this;
    }

    @Step("Switch back to main frame")
    public IframePage switchToMainFrame() {
        driver.switchTo().defaultContent();
        return this;
    }

    @Step("Type '{text}' in editor")
    public IframePage typeInEditor(String text) {
        // Use TinyMCE API for reliable content setting — sendKeys is flaky with iframes
        driver.switchTo().defaultContent();
        ((JavascriptExecutor) driver).executeScript(
                "tinyMCE.activeEditor.setContent(arguments[0]);", text);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(EDITOR_IFRAME));
        return this;
    }

    @Step("Clear editor text")
    public IframePage clearEditor() {
        WebElement body = waitForVisible(EDITOR_BODY);
        body.clear();
        return this;
    }

    @Step("Select all text and clear editor")
    public IframePage selectAllAndClear() {
        // Use TinyMCE API for reliable clearing — no frame switching needed by caller
        driver.switchTo().defaultContent();
        ((JavascriptExecutor) driver).executeScript(
                "tinyMCE.activeEditor.setContent('');");
        return this;
    }

    @Step("Click Bold toolbar button")
    public IframePage clickBold() {
        switchToMainFrame();
        click(BOLD_BUTTON);
        switchToEditorIframe();
        return this;
    }

    @Step("Click Italic toolbar button")
    public IframePage clickItalic() {
        switchToMainFrame();
        click(ITALIC_BUTTON);
        switchToEditorIframe();
        return this;
    }

    // -- State readers ---------------------------------------------------- //

    /**
     * Get the current text content of the editor.
     * Must be called while inside the iframe context.
     *
     * @return editor text
     */
    public String getEditorText() {
        // Use TinyMCE API for reliable content reading
        driver.switchTo().defaultContent();
        String content = (String) ((JavascriptExecutor) driver).executeScript(
                "return tinyMCE.activeEditor.getContent({format: 'text'});");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(EDITOR_IFRAME));
        return content != null ? content.trim() : "";
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING).trim();
    }
}
