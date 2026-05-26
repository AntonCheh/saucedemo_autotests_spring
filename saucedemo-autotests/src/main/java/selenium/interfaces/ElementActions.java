package selenium.interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Optional;

/**
 * Действия с элементами
 */
public interface ElementActions {
    void click(By locator);
    void click(WebElement element);
    void type(By locator, String text);
    void type(WebElement element, String text);

    void clearAndType(By locator, String text);
    void clearAndType(WebElement element, String text);

    String getText(By locator);
    boolean isDisplayed(By locator);
    boolean isEnabled(By locator);

    /**
     * Получить первый видимый элемент с непустым текстом
     */
    Optional<String> getFirstNonEmptyText(By locator);

    /**
     * Получить текст первого элемента
     */
    String getFirstElementText(By locator);

    void clearField(By locator);  // ← Добавить
    void clearField(WebElement element);  // ← Добавить
}
