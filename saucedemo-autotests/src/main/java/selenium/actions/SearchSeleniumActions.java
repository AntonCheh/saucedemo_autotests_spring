package selenium.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.interfaces.ElementActions;
import selenium.interfaces.PageLoadActions;
import selenium.interfaces.SearchActions;
import selenium.interfaces.WaitActions;

@Slf4j
@RequiredArgsConstructor
public class SearchSeleniumActions implements SearchActions {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ElementActions elementActions;
    private final WaitActions waitActions;
    private final PageLoadActions pageLoadActions;

    @Override
    public void performSearch(By searchField, By searchButton, String query) {
        log.debug("Выполнение поиска: {}", query);

        // Вводим запрос
        elementActions.clearAndType(searchField, query);
        waitActions.sleep(300);

        // Нажимаем поиск
        elementActions.click(searchButton);

        log.debug("Поиск '{}' выполнен", query);
    }

    @Override
    public void performSearchAndWait(By searchField, By searchButton, By resultsLocator, String query) {
        performSearch(searchField, searchButton, query);

        // Ждем результаты
        waitActions.waitForElementPresent(resultsLocator);
        pageLoadActions.waitForLoaderDisappear();

        log.debug("Результаты поиска '{}' загружены", query);
    }

    @Override
    public boolean isSearchExecuted(String expectedUrlPart) {
        String currentUrl = driver.getCurrentUrl();
        boolean contains = currentUrl.contains(expectedUrlPart);

        log.debug("Проверка URL: содержит '{}' = {}", expectedUrlPart, contains);
        return contains;
    }

    @Override
    public void clearSearchField(By searchField) {
        log.debug("Очистка поля поиска: {}", searchField);
        elementActions.clearField(searchField);
    }

    @Override
    public String getSearchQuery(By searchField) {
        WebElement field = driver.findElement(searchField);
        String value = field.getAttribute("value");
        log.debug("Текущий поисковый запрос: {}", value);
        return value;
    }

    /**
     * Выполняет поиск нажатием ENTER (альтернативный способ)
     */
    public void performSearchWithEnter(By searchField, String query) {
        log.debug("Выполнение поиска через ENTER: {}", query);

        WebElement field = wait.until(d -> d.findElement(searchField));
        elementActions.clearAndType(searchField, query);
        field.sendKeys(Keys.ENTER);

        log.debug("Поиск '{}' выполнен через ENTER", query);
    }


}