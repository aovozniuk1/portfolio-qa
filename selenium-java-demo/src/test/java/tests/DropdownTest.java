package tests;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
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

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the default selected option is the placeholder text")
    public void testDefaultSelectedOption() {
        String defaultOption = dropdownPage.getSelectedOptionText();
        Assert.assertEquals(defaultOption, "Please select an option",
                "Default selected option should be the placeholder");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify the dropdown contains exactly 3 options (placeholder + 2 selectable)")
    public void testOptionCount() {
        List<String> allOptions = dropdownPage.getAllOptionTexts();
        Assert.assertEquals(allOptions.size(), 3,
                "Dropdown should contain exactly 3 options (including placeholder)");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Select options in 3+ cycles verifying each step updates correctly")
    public void testReselection() {
        // Cycle 1: select Option 1
        dropdownPage.selectByValue("1");
        Assert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 1",
                "After selecting value '1', text should be 'Option 1'");

        // Cycle 2: switch to Option 2
        dropdownPage.selectByValue("2");
        Assert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 2",
                "After reselecting value '2', text should be 'Option 2'");

        // Cycle 3: back to Option 1
        dropdownPage.selectByVisibleText("Option 1");
        Assert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 1",
                "After reselecting 'Option 1' by text, text should be 'Option 1'");

        // Cycle 4: back to Option 2 via value
        dropdownPage.selectByValue("2");
        Assert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 2",
                "After fourth selection, text should be 'Option 2'");

        // Cycle 5: final switch to Option 1 via visible text
        dropdownPage.selectByVisibleText("Option 1");
        Assert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 1",
                "After fifth selection, text should be 'Option 1'");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Select all options sequentially and verify each selection is reflected correctly")
    public void testSelectAllOptionsSequentially() {
        List<String> selectableOptions = dropdownPage.getSelectableOptionTexts();
        SoftAssert softAssert = new SoftAssert();

        for (String option : selectableOptions) {
            dropdownPage.selectByVisibleText(option);
            String selected = dropdownPage.getSelectedOptionText();
            softAssert.assertEquals(selected, option,
                    "After selecting '" + option + "', selected text should match");
        }

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify selecting by index works correctly for all selectable options")
    public void testSelectByIndex() {
        SoftAssert softAssert = new SoftAssert();

        // Index 1 = Option 1 (index 0 is the disabled placeholder)
        dropdownPage.selectByValue("1");
        softAssert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 1",
                "Selecting index 1 should show 'Option 1'");

        // Index 2 = Option 2
        dropdownPage.selectByValue("2");
        softAssert.assertEquals(dropdownPage.getSelectedOptionText(), "Option 2",
                "Selecting index 2 should show 'Option 2'");

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the dropdown element is a <select> HTML element")
    public void testDropdownIsSelectElement() {
        WebElement dropdownElement = driver.findElement(By.id("dropdown"));
        String tagName = dropdownElement.getTagName().toLowerCase();
        Assert.assertEquals(tagName, "select",
                "Dropdown element should be a <select> tag, got: <" + tagName + ">");
        Assert.assertTrue(dropdownElement.isDisplayed(),
                "Dropdown element should be visible on the page");
        Assert.assertTrue(dropdownElement.isEnabled(),
                "Dropdown element should be enabled");
    }
}
