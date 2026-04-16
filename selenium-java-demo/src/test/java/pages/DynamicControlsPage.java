package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the Dynamic Controls page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/dynamic_controls">/dynamic_controls</a>
 */
public class DynamicControlsPage extends BasePage {

    private static final By CHECKBOX       = By.cssSelector("#checkbox-example input[type='checkbox']");
    private static final By REMOVE_ADD_BTN = By.cssSelector("#checkbox-example button");
    private static final By TEXT_INPUT     = By.cssSelector("#input-example input[type='text']");
    private static final By ENABLE_BTN    = By.cssSelector("#input-example button");
    private static final By MESSAGE        = By.id("message");
    private static final By LOADING        = By.id("loading");

    public DynamicControlsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open Dynamic Controls page")
    public DynamicControlsPage open() {
        navigateTo("/dynamic_controls");
        return this;
    }

    // -- Checkbox section ------------------------------------------------- //

    @Step("Click Remove/Add button")
    public DynamicControlsPage clickRemoveAddButton() {
        click(REMOVE_ADD_BTN);
        return this;
    }

    @Step("Wait for loading to finish")
    public DynamicControlsPage waitForLoading() {
        // The loading spinner blinks briefly; try to catch it with a fluent
        // wait, but fall through if it's already gone.
        try {
            fluentWait(LOADING, 3, 200);
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // loading already completed -- not a failure
        }
        waitForInvisible(LOADING);
        waitForVisible(MESSAGE);
        return this;
    }

    /**
     * Wait for the {@code message} element to contain an expected string.
     * Use this after {@link #waitForLoading()} when the previous operation
     * left its own message on the page (the element is always visible once
     * the first action has run).
     */
    @Step("Wait for message to contain '{expected}'")
    public DynamicControlsPage waitForMessage(String expected) {
        wait.until(d -> getText(MESSAGE).contains(expected));
        return this;
    }

    public boolean isCheckboxPresent() {
        return isDisplayed(CHECKBOX);
    }

    // -- Input section ---------------------------------------------------- //

    @Step("Click Enable/Disable button")
    public DynamicControlsPage clickEnableDisableButton() {
        click(ENABLE_BTN);
        return this;
    }

    @Step("Type '{text}' into text input")
    public DynamicControlsPage typeInInput(String text) {
        type(TEXT_INPUT, text);
        return this;
    }

    public boolean isInputEnabled() {
        return isEnabled(TEXT_INPUT);
    }

    // -- Common ----------------------------------------------------------- //

    public String getMessage() {
        return getText(MESSAGE).trim();
    }

    /**
     * Get the current value of the text input field.
     *
     * @return the input field value
     */
    public String getInputValue() {
        return getAttribute(TEXT_INPUT, "value");
    }
}
