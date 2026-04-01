package tests;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.FileUploadPage;
import utils.RetryAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * UI tests for the File Upload page.
 * <p>
 * Target: <a href="https://the-internet.herokuapp.com/upload">/upload</a>
 */
@Feature("File Operations")
@Story("File Upload")
public class FileUploadTest extends BaseTest {

    private FileUploadPage uploadPage;
    private File tempFile;
    private final List<File> tempFiles = new ArrayList<>();

    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp() {
        super.setUp();
        uploadPage = new FileUploadPage(driver).open();

        // Create a temporary test file for upload
        try {
            tempFile = Files.createTempFile("selenium-test-upload-", ".txt").toFile();
            Files.writeString(tempFile.toPath(), "Test file content for upload verification.");
            tempFiles.add(tempFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file for upload test", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    @Override
    public void tearDown(org.testng.ITestResult result) {
        // Clean up all temp files
        for (File f : tempFiles) {
            if (f != null && f.exists()) {
                f.delete();
            }
        }
        tempFiles.clear();
        super.tearDown(result);
    }

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a temp file, upload it, and verify the filename is displayed")
    public void testFileUpload() {
        Assert.assertTrue(tempFile.exists(), "Temp file should exist before upload");

        uploadPage.uploadFile(tempFile.getAbsolutePath());

        Assert.assertTrue(uploadPage.isUploadSuccessful(),
                "Upload should be successful (heading should say 'File Uploaded!')");
        Assert.assertEquals(uploadPage.getUploadedFileName(), tempFile.getName(),
                "Uploaded file name should match the temp file name");
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Upload a file with special characters in the filename")
    public void testUploadFileWithSpecialCharacters() {
        try {
            File specialFile = Files.createTempFile("test file (1) [copy]", ".txt").toFile();
            Files.writeString(specialFile.toPath(), "Special character filename test content.");
            tempFiles.add(specialFile);

            uploadPage.uploadFile(specialFile.getAbsolutePath());

            Assert.assertTrue(uploadPage.isUploadSuccessful(),
                    "Upload should be successful for file with special characters");
            Assert.assertEquals(uploadPage.getUploadedFileName(), specialFile.getName(),
                    "Uploaded file name should match the special character filename");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file with special chars", e);
        }
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Upload an empty file (0 bytes) and verify the upload succeeds")
    public void testUploadEmptyFile() {
        try {
            File emptyFile = Files.createTempFile("selenium-empty-", ".txt").toFile();
            // Do not write anything — file remains 0 bytes
            tempFiles.add(emptyFile);

            Assert.assertEquals(emptyFile.length(), 0, "Empty file should have 0 bytes");

            uploadPage.uploadFile(emptyFile.getAbsolutePath());

            Assert.assertTrue(uploadPage.isUploadSuccessful(),
                    "Upload should be successful even for an empty file");
            Assert.assertEquals(uploadPage.getUploadedFileName(), emptyFile.getName(),
                    "Uploaded file name should match the empty file name");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create empty temp file", e);
        }
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the upload button is present and enabled before uploading a file")
    public void testUploadButtonPresentAndEnabled() {
        SoftAssert softAssert = new SoftAssert();

        WebElement uploadButton = driver.findElement(By.id("file-submit"));
        softAssert.assertTrue(uploadButton.isDisplayed(),
                "Upload button should be visible on the page");
        softAssert.assertTrue(uploadButton.isEnabled(),
                "Upload button should be enabled");

        WebElement fileInput = driver.findElement(By.id("file-upload"));
        softAssert.assertTrue(fileInput.isEnabled(),
                "File input should be enabled");

        String buttonValue = uploadButton.getAttribute("value");
        softAssert.assertNotNull(buttonValue,
                "Upload button should have a value attribute");
        softAssert.assertFalse(buttonValue.isEmpty(),
                "Upload button value should not be empty");

        softAssert.assertAll();
    }

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.MINOR)
    @Description("Verify drag-and-drop upload area exists on the page")
    public void testDragAndDropAreaExists() {
        SoftAssert softAssert = new SoftAssert();

        WebElement dragDropArea = driver.findElement(By.id("drag-drop-upload"));
        softAssert.assertNotNull(dragDropArea,
                "Drag-and-drop upload area should exist");
        softAssert.assertTrue(dragDropArea.isDisplayed(),
                "Drag-and-drop area should be visible");

        // Verify the drag-drop area has some dimensions (not collapsed)
        int width = dragDropArea.getSize().getWidth();
        int height = dragDropArea.getSize().getHeight();
        softAssert.assertTrue(width > 0,
                "Drag-and-drop area should have width > 0, got: " + width);
        softAssert.assertTrue(height > 0,
                "Drag-and-drop area should have height > 0, got: " + height);

        softAssert.assertAll();
    }
}
