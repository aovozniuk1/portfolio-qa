package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page object for the Dropdown page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/dropdown">/dropdown</a>
 */
public class DropdownPage extends BasePage {

    private static final By DROPDOWN     = By.id("dropdown");
    private static final By PAGE_HEADING = By.tagName("h3");

    public DropdownPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open Dropdown page")
    public DropdownPage open() {
        navigateTo("/dropdown");
        return this;
    }

    @Step("Select dropdown option by value: {value}")
    public DropdownPage selectByValue(String value) {
        Select select = new Select(waitForVisible(DROPDOWN));
        select.selectByValue(value);
        return this;
    }

    @Step("Select dropdown option by visible text: {text}")
    public DropdownPage selectByVisibleText(String text) {
        Select select = new Select(waitForVisible(DROPDOWN));
        select.selectByVisibleText(text);
        return this;
    }

    public String getSelectedOptionText() {
        Select select = new Select(waitForVisible(DROPDOWN));
        return select.getFirstSelectedOption().getText().trim();
    }

    public List<String> getAllOptionTexts() {
        Select select = new Select(waitForVisible(DROPDOWN));
        return select.getOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getSelectableOptionTexts() {
        Select select = new Select(waitForVisible(DROPDOWN));
        return select.getOptions().stream()
                .filter(WebElement::isEnabled)
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING).trim();
    }
}
