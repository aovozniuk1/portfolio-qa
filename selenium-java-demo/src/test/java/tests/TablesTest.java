package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.TablesPage;
import utils.RetryAnalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * UI tests for the Sortable Data Tables page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/tables">/tables</a>
 */
@Feature("Data Tables")
@Story("Sortable Tables")
public class TablesTest extends BaseTest {

    private TablesPage tablesPage;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern URL_PATTERN =
            Pattern.compile("^https?://[\\w.-]+(/[\\w./-]*)?$");

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        tablesPage = new TablesPage(driver).open();
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Extract all rows from the table, verify row count and specific cell data")
    public void testTableDataExtraction() {
        List<Map<String, String>> allRows = tablesPage.getAllRows();

        Assert.assertEquals(allRows.size(), 4, "Table should have 4 data rows");
        Assert.assertEquals(tablesPage.getRowCount(), 4, "getRowCount should return 4");

        String firstCellValue = tablesPage.getCellValue(0, 0);
        Assert.assertNotNull(firstCellValue, "First cell value should not be null");
        Assert.assertFalse(firstCellValue.isEmpty(), "First cell value should not be empty");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Click Last Name header to sort, then verify ascending sort order")
    public void testSortByLastName() {
        tablesPage.sortByColumn("Last Name");

        List<String> lastNames = tablesPage.getColumnValues("Last Name");
        Assert.assertFalse(lastNames.isEmpty(), "Last Name column should have values");

        for (int i = 0; i < lastNames.size() - 1; i++) {
            Assert.assertTrue(
                    lastNames.get(i).compareToIgnoreCase(lastNames.get(i + 1)) <= 0,
                    "Table should be sorted ascending by Last Name: '"
                            + lastNames.get(i) + "' should come before '" + lastNames.get(i + 1) + "'");
        }
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Find a row by last name and verify all fields using SoftAssert")
    public void testFilterRowByData() {
        Map<String, String> row = tablesPage.getRowByLastName("Smith");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(row.isEmpty(), "Row for 'Smith' should exist");
        softAssert.assertEquals(row.get("Last Name"), "Smith", "Last Name should be Smith");
        softAssert.assertEquals(row.get("First Name"), "John", "First Name should be John");
        softAssert.assertEquals(row.get("Email"), "jsmith@gmail.com", "Email should match");
        softAssert.assertEquals(row.get("Due"), "$50.00", "Due should be $50.00");
        softAssert.assertEquals(row.get("Web Site"), "http://www.jsmith.com", "Web Site should match");
        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify data integrity: no empty cells, emails contain @, dues start with $, URLs are valid")
    public void testTableDataIntegrity() {
        SoftAssert softAssert = new SoftAssert();

        List<Map<String, String>> allRows = tablesPage.getAllRows();
        for (int i = 0; i < allRows.size(); i++) {
            Map<String, String> row = allRows.get(i);
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (!"Action".equals(entry.getKey())) {
                    softAssert.assertFalse(entry.getValue().isEmpty(),
                            "Row " + i + ", column '" + entry.getKey() + "' should not be empty");
                }
            }
        }

        List<String> emails = tablesPage.getColumnValues("Email");
        for (int i = 0; i < emails.size(); i++) {
            softAssert.assertTrue(emails.get(i).contains("@"),
                    "Email in row " + i + " should contain '@': " + emails.get(i));
            softAssert.assertTrue(EMAIL_PATTERN.matcher(emails.get(i)).matches(),
                    "Email in row " + i + " should match valid email format: " + emails.get(i));
        }

        List<String> dues = tablesPage.getColumnValues("Due");
        for (int i = 0; i < dues.size(); i++) {
            softAssert.assertTrue(dues.get(i).startsWith("$"),
                    "Due in row " + i + " should start with '$': " + dues.get(i));
        }

        // Validate Web Site column has valid URL format
        List<String> websites = tablesPage.getColumnValues("Web Site");
        for (int i = 0; i < websites.size(); i++) {
            softAssert.assertTrue(URL_PATTERN.matcher(websites.get(i)).matches(),
                    "Web Site in row " + i + " should be a valid URL: " + websites.get(i));
        }

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Sort by multiple columns sequentially and verify each sort is applied")
    public void testSortByMultipleColumnsSequentially() {
        SoftAssert softAssert = new SoftAssert();

        // Sort by Last Name
        tablesPage.sortByColumn("Last Name");
        List<String> lastNames = tablesPage.getColumnValues("Last Name");
        for (int i = 0; i < lastNames.size() - 1; i++) {
            softAssert.assertTrue(
                    lastNames.get(i).compareToIgnoreCase(lastNames.get(i + 1)) <= 0,
                    "After sorting by Last Name: '" + lastNames.get(i)
                            + "' should come before '" + lastNames.get(i + 1) + "'");
        }

        // Sort by First Name
        tablesPage.sortByColumn("First Name");
        List<String> firstNames = tablesPage.getColumnValues("First Name");
        for (int i = 0; i < firstNames.size() - 1; i++) {
            softAssert.assertTrue(
                    firstNames.get(i).compareToIgnoreCase(firstNames.get(i + 1)) <= 0,
                    "After sorting by First Name: '" + firstNames.get(i)
                            + "' should come before '" + firstNames.get(i + 1) + "'");
        }

        // Sort by Email
        tablesPage.sortByColumn("Email");
        List<String> emailsSorted = tablesPage.getColumnValues("Email");
        for (int i = 0; i < emailsSorted.size() - 1; i++) {
            softAssert.assertTrue(
                    emailsSorted.get(i).compareToIgnoreCase(emailsSorted.get(i + 1)) <= 0,
                    "After sorting by Email: '" + emailsSorted.get(i)
                            + "' should come before '" + emailsSorted.get(i + 1) + "'");
        }

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify sort is togglable: first click ascending, second click descending")
    public void testSortToggleAscendingDescending() {
        SoftAssert softAssert = new SoftAssert();

        // First click: ascending
        tablesPage.sortByColumn("Last Name");
        List<String> ascending = tablesPage.getColumnValues("Last Name");
        for (int i = 0; i < ascending.size() - 1; i++) {
            softAssert.assertTrue(
                    ascending.get(i).compareToIgnoreCase(ascending.get(i + 1)) <= 0,
                    "First sort should be ascending: '" + ascending.get(i)
                            + "' should come before '" + ascending.get(i + 1) + "'");
        }

        // Second click: descending
        tablesPage.sortByColumn("Last Name");
        List<String> descending = tablesPage.getColumnValues("Last Name");
        for (int i = 0; i < descending.size() - 1; i++) {
            softAssert.assertTrue(
                    descending.get(i).compareToIgnoreCase(descending.get(i + 1)) >= 0,
                    "Second sort should be descending: '" + descending.get(i)
                            + "' should come after '" + descending.get(i + 1) + "'");
        }

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Cross-validate email format against names: email should plausibly relate to the name")
    public void testCrossValidateEmailAgainstName() {
        SoftAssert softAssert = new SoftAssert();

        List<Map<String, String>> allRows = tablesPage.getAllRows();
        for (int i = 0; i < allRows.size(); i++) {
            Map<String, String> row = allRows.get(i);
            String firstName = row.get("First Name").toLowerCase();
            String lastName = row.get("Last Name").toLowerCase();
            String email = row.get("Email").toLowerCase();
            String emailLocal = email.split("@")[0];

            // Verify email local part contains at least part of the first name or last name
            boolean containsNamePart = emailLocal.contains(firstName.substring(0, 1))
                    || emailLocal.contains(lastName.toLowerCase());
            softAssert.assertTrue(containsNamePart,
                    "Row " + i + ": email local part '" + emailLocal
                            + "' should relate to name '" + firstName + " " + lastName + "'");
        }

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify all emails in the table are unique")
    public void testEmailUniqueness() {
        List<String> emails = tablesPage.getColumnValues("Email");
        Set<String> uniqueEmails = new HashSet<>(emails);
        Assert.assertEquals(uniqueEmails.size(), emails.size(),
                "All emails in the table should be unique");
    }
}
