package selenium.interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Действия с прокруткой
 */
public interface ScrollActions {
    void scrollToElement(WebElement element);
    void scrollToElement(By locator);
    void scrollDown();
    void scrollDown(int pixels);
    void scrollToTop();
    void scrollToBottom();
    boolean isPageEndReached();
}