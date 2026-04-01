package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the File Upload page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/upload">/upload</a>
 */
public class FileUploadPage extends BasePage {

    // -- Selectors -------------------------------------------------------- //
    private static final By FILE_INPUT      = By.id("file-upload");
    private static final By UPLOAD_BUTTON   = By.id("file-submit");
    private static final By UPLOADED_FILES  = By.id("uploaded-files");
    private static final By UPLOAD_HEADING  = By.tagName("h3");

    public FileUploadPage(WebDriver driver) {
        super(driver);
    }

    // -- Actions ---------------------------------------------------------- //

    @Step("Open File Upload page")
    public FileUploadPage open() {
        navigateTo("/upload");
        return this;
    }

    @Step("Upload file: {filePath}")
    public FileUploadPage uploadFile(String filePath) {
        driver.findElement(FILE_INPUT).sendKeys(filePath);
        click(UPLOAD_BUTTON);
        return this;
    }

    // -- State readers ---------------------------------------------------- //

    public String getUploadedFileName() {
        return getText(UPLOADED_FILES).trim();
    }

    public boolean isUploadSuccessful() {
        return getText(UPLOAD_HEADING).contains("File Uploaded!");
    }
}
