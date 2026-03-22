package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.DropdownPage;
import utils.RetryAnalyzer;

import java.util.List;

/**
 * UI tests for the Dropdown page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/dropdown">/dropdown</a>
 */
@Feature("Form Controls")
@Story("Dropdown")
public class DropdownTest extends BaseTest {

    private DropdownPage dropdownPage;

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        dropdownPage = new DropdownPage(driver).open();
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify dropdown displays selectable options")
    public void testDropdownOptionsPresent() {
        List<String> options = dropdownPage.getSelectableOptionTexts();
        Assert.assertTrue(options.contains("Option 1"), "Should contain Option 1");
        Assert.assertTrue(options.contains("Option 2"), "Should contain Option 2");
    }

    @DataProvider(name = "dropdownValues")
    public Object[][] dropdownValues() {
        return new Object[][]{
                {"1", "Option 1"},
                {"2", "Option 2"},
        };
    }

    @Test(groups = {"regression"}, dataProvider = "dropdownValues",
            retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify selecting by value updates the displayed selection")
    public void testSelectByValue(String value, String expectedText) {
        dropdownPage.selectByValue(value);
        String selected = dropdownPage.getSelectedOptionText();
        Assert.assertEquals(selected, expectedText,
                "Selected text should match: " + expectedText);
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify selecting by visible text updates the dropdown correctly")
    public void testSelectByVisibleText() {
        dropdownPage.selectByVisibleText("Option 2");
        Assert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 2");

        dropdownPage.selectByVisibleText("Option 1");
        Assert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 1");
    }
}
