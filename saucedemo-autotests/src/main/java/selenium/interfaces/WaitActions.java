package selenium.interfaces;

import org.openqa.selenium.By;

/**
 * Действия ожидания
 */
public interface WaitActions {
    void waitForElementVisible(By locator);
    void waitForElementClickable(By locator);
    void waitForElementPresent(By locator);
    void sleep(long millis);
}
