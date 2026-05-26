package selenium.interfaces;

import org.openqa.selenium.By;

public interface SearchActions {

    /**
     * Выполняет поиск по заданному запросу
     */
    void performSearch(By searchField, By searchButton, String query);

    /**
     * Выполняет поиск и ждет результатов
     */
    void performSearchAndWait(By searchField, By searchButton, By resultsLocator, String query);

    /**
     * Проверяет, что поиск выполнился
     */
    boolean isSearchExecuted(String expectedUrlPart);

    /**
     * Очищает поле поиска
     */
    void clearSearchField(By searchField);

    /**
     * Получает текущий поисковый запрос из поля
     */
    String getSearchQuery(By searchField);
}