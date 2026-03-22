package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

/**
 * Utility methods for Allure report attachments.
 */
public final class AllureUtils {

    private AllureUtils() {
        // utility class
    }

    /**
     * Attach a full-page screenshot to the current Allure step.
     *
     * @param driver active WebDriver instance
     * @param name   attachment display name
     */
    public static void attachScreenshot(WebDriver driver, String name) {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
    }

    /**
     * Attach a plain-text body (e.g. API response) to the current Allure step.
     *
     * @param name attachment display name
     * @param body text content to attach
     */
    public static void attachText(String name, String body) {
        Allure.addAttachment(name, "text/plain", body);
    }
}
