package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Page object for the Sortable Data Tables page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/tables">/tables</a>
 */
public class TablesPage extends BasePage {

    // -- Selectors -------------------------------------------------------- //
    private static final By TABLE            = By.id("table1");
    private static final By TABLE_HEADERS    = By.cssSelector("#table1 thead th");
    private static final By TABLE_ROWS       = By.cssSelector("#table1 tbody tr");
    private static final By PAGE_HEADING     = By.tagName("h3");

    public TablesPage(WebDriver driver) {
        super(driver);
    }

    // -- Actions ---------------------------------------------------------- //

    @Step("Open Tables page")
    public TablesPage open() {
        navigateTo("/tables");
        return this;
    }

    @Step("Sort table by column: {columnName}")
    public TablesPage sortByColumn(String columnName) {
        List<WebElement> headers = driver.findElements(TABLE_HEADERS);
        for (WebElement header : headers) {
            if (header.getText().trim().equalsIgnoreCase(columnName)) {
                header.click();
                break;
            }
        }
        return this;
    }

    // -- State readers ---------------------------------------------------- //

    /**
     * Get all column header names from table1.
     *
     * @return list of header names
     */
    public List<String> getColumnHeaders() {
        return driver.findElements(TABLE_HEADERS).stream()
                .map(el -> el.getText().trim())
                .collect(Collectors.toList());
    }

    /**
     * Get all values in a given column by column name.
     *
     * @param columnName the header text to look up
     * @return list of cell values for that column
     */
    public List<String> getColumnValues(String columnName) {
        int colIndex = getColumnIndex(columnName);
        if (colIndex == -1) {
            return Collections.emptyList();
        }
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        return rows.stream()
                .map(row -> row.findElements(By.tagName("td")).get(colIndex).getText().trim())
                .collect(Collectors.toList());
    }

    /**
     * Get a specific row as a map keyed by column header.
     *
     * @param lastName the last name to search for
     * @return map of column header to cell value, or empty map if not found
     */
    public Map<String, String> getRowByLastName(String lastName) {
        List<String> headers = getColumnHeaders();
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        int lastNameCol = getColumnIndex("Last Name");

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.get(lastNameCol).getText().trim().equals(lastName)) {
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int i = 0; i < cells.size() && i < headers.size(); i++) {
                    rowData.put(headers.get(i), cells.get(i).getText().trim());
                }
                return rowData;
            }
        }
        return Collections.emptyMap();
    }

    /**
     * Get total number of data rows in the table.
     *
     * @return row count
     */
    public int getRowCount() {
        return driver.findElements(TABLE_ROWS).size();
    }

    /**
     * Get the value of a specific cell by row index and column index (0-based).
     *
     * @param rowIndex row index (0-based)
     * @param colIndex column index (0-based)
     * @return cell text
     */
    public String getCellValue(int rowIndex, int colIndex) {
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        return rows.get(rowIndex)
                .findElements(By.tagName("td")).get(colIndex)
                .getText().trim();
    }

    /**
     * Get all rows as a list of maps keyed by column header.
     *
     * @return list of row maps
     */
    public List<Map<String, String>> getAllRows() {
        List<String> headers = getColumnHeaders();
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        List<Map<String, String>> result = new ArrayList<>();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            Map<String, String> rowData = new LinkedHashMap<>();
            for (int i = 0; i < cells.size() && i < headers.size(); i++) {
                rowData.put(headers.get(i), cells.get(i).getText().trim());
            }
            result.add(rowData);
        }
        return result;
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING).trim();
    }

    // -- Private helpers -------------------------------------------------- //

    private int getColumnIndex(String columnName) {
        List<WebElement> headers = driver.findElements(TABLE_HEADERS);
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
}
