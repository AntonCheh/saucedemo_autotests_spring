package selenium.interfaces;

import org.openqa.selenium.WebElement;

/**
 * JavaScript действия
 */
public interface JavaScriptActions {
    Object executeScript(String script, Object... args);
    void highlightElement(WebElement element);
    void removeAttribute(WebElement element, String attribute);
}
