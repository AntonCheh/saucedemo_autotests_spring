package selenium.interfaces;

import org.openqa.selenium.By;

import java.util.List;

public interface VerificationActions {

    /**
     * Проверяет наличие текста в элементах
     */
    boolean isTextPresent(By locator, String expectedText);

    /**
     * Проверяет частичное совпадение текста
     */
    boolean isTextContains(By locator, String partialText);

    /**
     * Проверяет точное совпадение текста
     */
    boolean isTextExactMatch(By locator, String exactText);

    /**
     * Получает список всех текстов
     */
    List<String> getAllTexts(By locator);

    /**
     * Проверяет, что список не пуст
     */
    boolean isNotEmpty(By locator);

    /**
     * Проверяет минимальное количество элементов
     */
    boolean hasMinimumCount(By locator, int minCount);
}