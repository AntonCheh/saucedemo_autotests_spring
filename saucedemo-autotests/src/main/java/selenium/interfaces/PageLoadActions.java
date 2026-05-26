package selenium.interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.function.Predicate;

public interface PageLoadActions {

    /**
     * Собирает все элементы с бесконечной прокруткой
     */
    List<String> collectAllItems(By itemLocator, int maxScrollAttempts);

    /**
     * Собирает все элементы с бесконечной прокруткой и настраиваемой паузой
     */
    List<String> collectAllItemsWithScrollPause(By itemLocator, int maxScrollAttempts, int pauseBetweenScrolls);

    /**
     * Ожидание загрузки контента (появление хотя бы одного элемента)
     */
    void waitForContentLoad(By contentLocator);

    /**
     * Ожидание обновления контента
     */
    void waitForContentUpdate(By contentLocator);

    /**
     * Ожидание исчезновения стандартного лоадера
     */
    void waitForLoaderDisappear();

    /**
     * Ожидание выполнения условия
     */
    void waitForCondition(Predicate<WebDriver> condition, String message);

    /**
     * Ожидание завершения AJAX-запросов
     */
    void waitForAjaxComplete();
}