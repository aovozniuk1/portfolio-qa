package tests;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.HoversPage;
import utils.RetryAnalyzer;

import java.util.List;

/**
 * UI tests for the Hovers page using the Actions class.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/hovers">/hovers</a>
 */
@Feature("Mouse Interactions")
@Story("Hovers")
public class HoversTest extends BaseTest {

    private HoversPage hoversPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        hoversPage = new HoversPage(driver).open();
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Hover over each of the 3 figures and verify username text appears")
    public void testHoverRevealsInfo() {
        SoftAssert softAssert = new SoftAssert();
        int figureCount = hoversPage.getFigureCount();

        softAssert.assertEquals(figureCount, 3, "There should be 3 figures on the page");

        for (int i = 0; i < figureCount; i++) {
            hoversPage.hoverOverFigure(i);

            softAssert.assertTrue(hoversPage.isHoverInfoVisible(i),
                    "Hover info should be visible for figure " + (i + 1));
            String hoverText = hoversPage.getHoverText(i);
            softAssert.assertTrue(hoverText.contains("name: user" + (i + 1)),
                    "Hover text for figure " + (i + 1) + " should contain 'name: user"
                            + (i + 1) + "', got: " + hoverText);
        }

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Hover each figure and verify a profile link exists using SoftAssert")
    public void testAllProfileLinksPresent() {
        SoftAssert softAssert = new SoftAssert();
        int figureCount = hoversPage.getFigureCount();

        for (int i = 0; i < figureCount; i++) {
            hoversPage.hoverOverFigure(i);

            String profileLink = hoversPage.getProfileLink(i);
            softAssert.assertNotNull(profileLink,
                    "Profile link should exist for figure " + (i + 1));
            softAssert.assertTrue(profileLink.contains("/users/" + (i + 1)),
                    "Profile link for figure " + (i + 1) + " should contain '/users/"
                            + (i + 1) + "', got: " + profileLink);
        }

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Hover over a figure, then hover away, and verify info disappears")
    public void testHoverAwayHidesInfo() {
        SoftAssert softAssert = new SoftAssert();

        // Hover over figure 0 to show info
        hoversPage.hoverOverFigure(0);
        softAssert.assertTrue(hoversPage.isHoverInfoVisible(0),
                "Info should be visible after hovering figure 0");

        // Move mouse away from the figure by hovering over the page heading
        WebElement heading = driver.findElement(By.tagName("h3"));
        new Actions(driver).moveToElement(heading).perform();

        // Verify the info for figure 0 is no longer visible
        softAssert.assertFalse(hoversPage.isHoverInfoVisible(0),
                "Info should disappear after hovering away from figure 0");

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Rapidly hover all figures sequentially and verify each shows correct info")
    public void testRapidSequentialHovers() {
        SoftAssert softAssert = new SoftAssert();
        int figureCount = hoversPage.getFigureCount();

        // Perform 3 full passes over all figures
        for (int pass = 0; pass < 3; pass++) {
            for (int i = 0; i < figureCount; i++) {
                hoversPage.hoverOverFigure(i);

                softAssert.assertTrue(hoversPage.isHoverInfoVisible(i),
                        "Pass " + (pass + 1) + ": Info should be visible for figure " + (i + 1));
                String text = hoversPage.getHoverText(i);
                softAssert.assertTrue(text.contains("name: user" + (i + 1)),
                        "Pass " + (pass + 1) + ": Figure " + (i + 1)
                                + " text should contain 'name: user" + (i + 1) + "', got: " + text);
            }
        }

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify all figure images are loaded and not broken (have valid src and dimensions)")
    public void testFigureImagesLoaded() {
        SoftAssert softAssert = new SoftAssert();
        List<WebElement> figures = driver.findElements(By.cssSelector(".figure"));

        softAssert.assertEquals(figures.size(), 3, "Should have 3 figures");

        for (int i = 0; i < figures.size(); i++) {
            WebElement img = figures.get(i).findElement(By.tagName("img"));

            String src = img.getAttribute("src");
            softAssert.assertNotNull(src,
                    "Figure " + (i + 1) + " image src should not be null");
            softAssert.assertFalse(src.isEmpty(),
                    "Figure " + (i + 1) + " image src should not be empty");

            // Check the image has non-zero natural dimensions (indicates it loaded)
            int naturalWidth = Integer.parseInt(
                    img.getAttribute("naturalWidth") != null ? img.getAttribute("naturalWidth") : "0");
            softAssert.assertTrue(naturalWidth > 0,
                    "Figure " + (i + 1) + " image should have naturalWidth > 0 (image loaded), got: "
                            + naturalWidth);
        }

        softAssert.assertAll();
    }
}
