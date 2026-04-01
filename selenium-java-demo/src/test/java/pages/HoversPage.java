package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * Page object for the Hovers page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/hovers">/hovers</a>
 */
public class HoversPage extends BasePage {

    // -- Selectors -------------------------------------------------------- //
    private static final By FIGURES        = By.cssSelector(".figure");
    private static final By FIGURE_CAPTION = By.cssSelector(".figcaption");
    private static final By CAPTION_HEADER = By.tagName("h5");
    private static final By PROFILE_LINK   = By.cssSelector("a[href^='/users/']");
    private static final By PAGE_HEADING   = By.tagName("h3");

    public HoversPage(WebDriver driver) {
        super(driver);
    }

    // -- Actions ---------------------------------------------------------- //

    @Step("Open Hovers page")
    public HoversPage open() {
        navigateTo("/hovers");
        return this;
    }

    @Step("Hover over figure at index: {index}")
    public HoversPage hoverOverFigure(int index) {
        List<WebElement> figures = driver.findElements(FIGURES);
        WebElement figure = figures.get(index);
        new Actions(driver).moveToElement(figure).perform();
        return this;
    }

    // -- State readers ---------------------------------------------------- //

    /**
     * Get the hover info text displayed over a figure after hovering.
     *
     * @param index 0-based figure index
     * @return caption header text (e.g. "name: user1")
     */
    public String getHoverText(int index) {
        List<WebElement> figures = driver.findElements(FIGURES);
        WebElement caption = figures.get(index).findElement(CAPTION_HEADER);
        return caption.getText().trim();
    }

    /**
     * Check if the hover info (figcaption) is visible for a given figure.
     *
     * @param index 0-based figure index
     * @return true if the caption is displayed
     */
    public boolean isHoverInfoVisible(int index) {
        List<WebElement> figures = driver.findElements(FIGURES);
        WebElement caption = figures.get(index).findElement(FIGURE_CAPTION);
        return caption.isDisplayed();
    }

    /**
     * Get the profile link href for a given figure (must be hovered first).
     *
     * @param index 0-based figure index
     * @return href value (e.g. "/users/1")
     */
    public String getProfileLink(int index) {
        List<WebElement> figures = driver.findElements(FIGURES);
        WebElement link = figures.get(index).findElement(PROFILE_LINK);
        return link.getAttribute("href");
    }

    public int getFigureCount() {
        return driver.findElements(FIGURES).size();
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING).trim();
    }
}
