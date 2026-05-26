package selenium.actions;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.interfaces.PageLoadActions;
import selenium.interfaces.ScrollActions;
import selenium.interfaces.WaitActions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
public class PageLoadSeleniumActions implements PageLoadActions {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ScrollActions scrollActions;
    private final WaitActions waitActions;

    private static final By DEFAULT_LOADER = By.cssSelector(
            "[data-auto='preloader'], .spin2, .loader, [data-zone-name='snippet-loader']"
    );

    public PageLoadSeleniumActions(WebDriver driver, WebDriverWait wait,
                                   ScrollActions scrollActions, WaitActions waitActions) {
        this.driver = driver;
        this.wait = wait;
        this.scrollActions = scrollActions;
        this.waitActions = waitActions;
    }

    // ==================== Методы сбора элементов ====================

    @Override
    public List<String> collectAllItems(By itemLocator, int maxScrollAttempts) {
        return collectAllItemsWithScrollPause(itemLocator, maxScrollAttempts, 500);
    }

    @Override
    public List<String> collectAllItemsWithScrollPause(By itemLocator, int maxScrollAttempts, int pauseBetweenScrolls) {
        log.info("Начало сбора элементов: {}", itemLocator);

        Set<String> uniqueItems = new LinkedHashSet<>();
        int previousSize = 0;
        int noNewItemsCount = 0;
        int lastScrollPosition = 0;

        for (int attempt = 1; attempt <= maxScrollAttempts; attempt++) {
            // 1. Собираем текущие элементы
            List<WebElement> currentItems = driver.findElements(itemLocator);
            int beforeAdd = uniqueItems.size();

            currentItems.stream()
                    .map(WebElement::getText)
                    .filter(text -> text != null && !text.trim().isEmpty())
                    .forEach(uniqueItems::add);

            int newItems = uniqueItems.size() - beforeAdd;
            log.debug("Попытка {}: добавлено {} новых элементов, всего: {}",
                    attempt, newItems, uniqueItems.size());

            // 2. Проверяем, достигнут ли конец страницы
            if (scrollActions.isPageEndReached()) {
                log.info("Достигнут физический конец страницы на попытке {}", attempt);

                // Ждем возможной подгрузки и делаем финальный сбор
                waitActions.sleep(1500);

                List<WebElement> finalItems = driver.findElements(itemLocator);
                finalItems.stream()
                        .map(WebElement::getText)
                        .filter(text -> text != null && !text.trim().isEmpty())
                        .forEach(uniqueItems::add);

                log.info("После финального сбора: {} элементов", uniqueItems.size());
                break;
            }

            // 3. Проверяем, не застряла ли прокрутка
            int currentScrollPosition = getCurrentScrollPosition();
            if (currentScrollPosition == lastScrollPosition && attempt > 3) {
                log.warn("Прокрутка застряла на позиции {}", currentScrollPosition);
                noNewItemsCount++;
                if (noNewItemsCount >= 3) {
                    log.info("Прокрутка не двигается. Останавливаем сбор.");
                    break;
                }
            } else {
                lastScrollPosition = currentScrollPosition;
            }

            // 4. Проверяем, добавляются ли новые элементы
            if (uniqueItems.size() == previousSize) {
                noNewItemsCount++;
                if (noNewItemsCount >= 5) {
                    log.info("Новые элементы не добавляются {} попыток подряд. Останавливаем сбор.",
                            noNewItemsCount);
                    break;
                }
            } else {
                noNewItemsCount = 0;
            }

            previousSize = uniqueItems.size();

            // 5. Прокручиваем страницу
            scrollActions.scrollDown(600);

            // 6. Ждем загрузки нового контента
            waitForLoaderDisappear();

            // 7. Пауза между прокрутками
            waitActions.sleep(pauseBetweenScrolls);
        }

        List<String> result = new ArrayList<>(uniqueItems);
        log.info("Сбор завершен. Всего собрано {} элементов", result.size());

        return result;
    }

    // ==================== Методы ожидания ====================

    @Override
    public void waitForContentLoad(By contentLocator) {
        log.debug("Ожидание загрузки контента: {}", contentLocator);

        wait.until(driver -> {
            List<WebElement> elements = driver.findElements(contentLocator);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        });

        log.debug("Контент загружен: {}", contentLocator);
    }

    @Override
    public void waitForContentUpdate(By contentLocator) {
        log.debug("Ожидание обновления контента: {}", contentLocator);

        // Запоминаем текущие элементы
        List<WebElement> oldElements = driver.findElements(contentLocator);
        int oldCount = oldElements.size();
        String oldText = oldElements.isEmpty() ? "" : oldElements.get(0).getText();

        // Ждем изменения
        wait.until(driver -> {
            List<WebElement> newElements = driver.findElements(contentLocator);
            if (newElements.isEmpty()) return false;

            boolean countChanged = newElements.size() != oldCount;
            boolean textChanged = !newElements.get(0).getText().equals(oldText);

            return countChanged || textChanged;
        });

        log.debug("Контент обновлен: {}", contentLocator);
    }

    @Override
    public void waitForLoaderDisappear() {
        waitForLoaderDisappear(DEFAULT_LOADER);
    }

    public void waitForLoaderDisappear(By loaderLocator) {
        log.debug("Ожидание исчезновения лоадера");

        try {
            // Сначала ждем появления (если его нет - ок)
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(loaderLocator),
                    driver -> true
            ));

            // Потом ждем исчезновения
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderLocator));

            log.debug("Лоадер исчез");
        } catch (TimeoutException e) {
            log.debug("Лоадер не появился или уже исчез");
        }
    }

    @Override
    public void waitForCondition(Predicate<WebDriver> condition, String message) {
        log.debug("Ожидание условия: {}", message);
        wait.until(driver -> condition.test(driver));
        log.debug("Условие выполнено: {}", message);
    }

    @Override
    public void waitForAjaxComplete() {
        log.debug("Ожидание завершения AJAX-запросов");

        wait.until(driver -> {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;

                // Проверяем jQuery.active
                Object jQueryActive = js.executeScript("return jQuery.active");
                if (jQueryActive instanceof Number && ((Number) jQueryActive).longValue() > 0) {
                    return false;
                }

                // Проверяем document.readyState
                Object readyState = js.executeScript("return document.readyState");
                return "complete".equals(readyState);

            } catch (Exception e) {
                // jQuery может быть не загружен
                return true;
            }
        });

        log.debug("AJAX-запросы завершены");
    }

    // ==================== Вспомогательные методы ====================

    /**
     * Получает текущую позицию прокрутки
     */
    private int getCurrentScrollPosition() {
        try {
            Number scrollY = (Number) ((JavascriptExecutor) driver)
                    .executeScript("return window.pageYOffset;");
            return scrollY.intValue();
        } catch (Exception e) {
            log.warn("Не удалось получить позицию прокрутки: {}", e.getMessage());
            return 0;
        }
    }
}