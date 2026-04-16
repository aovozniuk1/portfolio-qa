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
        // The loading indicator blinks for ~2s. It may already be gone by the
        // time we look, so we only log a timeout here and fall through to the
        // disappearance wait + final message wait.
        try {
            fluentWait(LOADING, 3, 200);
        } catch (org.openqa.selenium.TimeoutException e) {
            // loading appears and disappears quickly on fast machines
        }
        waitForInvisible(LOADING);
        waitForVisible(MESSAGE);
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
