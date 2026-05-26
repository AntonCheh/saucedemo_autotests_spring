package selenium.utils;

import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.interfaces.ScrollActions;
import selenium.interfaces.WaitActions;
import selenium.models.FilterSelectionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ShopFilterHelper {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ScrollActions scrollActions;
    private final WaitActions waitActions;
    private final AssertionHelper assertionHelper;

    public ShopFilterHelper(WebDriver driver, WebDriverWait wait,
                            ScrollActions scrollActions, WaitActions waitActions,
                            AssertionHelper assertionHelper) {
        this.driver = driver;
        this.wait = wait;
        this.scrollActions = scrollActions;
        this.waitActions = waitActions;
        this.assertionHelper = assertionHelper;
    }

    // ==================== ОТКРЫТИЕ/ЗАКРЫТИЕ ФИЛЬТРОВ ====================

    /**
     * Открывает фильтр по кнопке
     */
    public void openFilter(By filterButton) {
        waitActions.waitForElementClickable(filterButton);
        driver.findElement(filterButton).click();
        waitActions.sleep(300);
    }

    /**
     * Применяет фильтр (кнопка "Готово"/"Применить")
     */
    public void applyFilter(By applyButton) {
        waitActions.waitForElementClickable(applyButton);
        driver.findElement(applyButton).click();
    }

    // ==================== РАБОТА С ПОЛЯМИ ВВОДА ====================

    /**
     * Заполняет поле ввода значением
     */
    public void typeInField(By locator, String value) {
        waitActions.waitForElementVisible(locator);
        WebElement field = driver.findElement(locator);
        field.clear();
        waitActions.sleep(50);
        assertionHelper.typeWithDelay(field, value, 50);
    }

    /**
     * Заполняет несколько полей ввода
     */
    public void fillInputs(Map<By, String> fieldValues) {
        fieldValues.forEach(this::typeInField);
    }

    // ==================== ВЫБОР ЧЕКБОКСОВ ====================

    /**
     * Выбирает один чекбокс по XPath-шаблону
     */
    public boolean selectSingleCheckbox(By searchInput, String checkboxXpathTemplate, String item) {
        try {
            WebElement search = driver.findElement(searchInput);
            search.clear();
            waitActions.sleep(50);
            assertionHelper.typeWithDelay(search, item, 100);
            waitActions.sleep(500);

            String xpath = String.format(checkboxXpathTemplate, item.toLowerCase());
            List<WebElement> elements = driver.findElements(By.xpath(xpath));

            if (elements.isEmpty()) {
                log.warn("❌ Не найден: {}", item);
                return false;
            }

            WebElement firstElement = elements.get(0);
            scrollActions.scrollToElement(firstElement);
            waitActions.sleep(100);
            new Actions(driver).moveToElement(firstElement).pause(100).click().perform();

            log.debug("✅ Выбран: {}", item);
            waitActions.sleep(200);
            return true;

        } catch (Exception e) {
            log.warn("❌ Не найден: {}", item);
            return false;
        }
    }

    /**
     * Выбирает несколько чекбоксов и возвращает результат
     */
    public FilterSelectionResult selectMultipleCheckboxes(By searchInput, String checkboxXpathTemplate,
                                                          List<String> items, boolean strict) {
        List<String> selected = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (String item : items) {
            if (selectSingleCheckbox(searchInput, checkboxXpathTemplate, item)) {
                selected.add(item);
            } else {
                notFound.add(item);
            }
        }

        FilterSelectionResult result = new FilterSelectionResult(selected, notFound);
        validateCheckboxResult(result, items, strict);

        return result;
    }

    // ==================== ПРОКРУТКА И СБОР ДАННЫХ ====================

    /**
     * Прокручивает страницу и собирает элементы в Set-ы
     */
    public void scrollAndCollect(Set<String> names, Set<String> prices,
                                 By nameLocator, By priceLocator, By showMoreButton) {
        int maxAttempts = 50;

        for (int i = 0; i < maxAttempts; i++) {
            collectCurrentProducts(names, prices, nameLocator, priceLocator);

            log.debug("Прокрутка {}: названий={}, цен={}", i + 1, names.size(), prices.size());

            while (isElementPresent(showMoreButton)) {
                clickElement(showMoreButton);
                waitActions.sleep(1500);
                collectCurrentProducts(names, prices, nameLocator, priceLocator);
            }

            if (scrollActions.isPageEndReached()) {
                waitActions.sleep(1500);
                collectCurrentProducts(names, prices, nameLocator, priceLocator);
                break;
            }

            scrollActions.scrollDown();
            waitActions.sleep(500);
        }
    }

    /**
     * Собирает текущие видимые товары и цены
     */
    public void collectCurrentProducts(Set<String> names, Set<String> prices,
                                       By nameLocator, By priceLocator) {
        for (WebElement product : driver.findElements(nameLocator)) {
            try {
                String text = product.getText();
                if (text != null && !text.isEmpty()) names.add(text);
            } catch (Exception e) {
                // Игнорируем
            }
        }

        for (WebElement price : driver.findElements(priceLocator)) {
            try {
                String text = price.getText();
                if (text != null && !text.isEmpty()) {
                    String cleanPrice = text.replaceAll("[^\\d]", "");
                    if (!cleanPrice.isEmpty()) prices.add(cleanPrice);
                }
            } catch (Exception e) {
                // Игнорируем
            }
        }
    }

    // ==================== ПРОВЕРКИ ====================

    /**
     * Проверяет наличие элемента на странице
     */
    public boolean isElementPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Кликает по элементу с проверкой кликабельности
     */
    public void clickElement(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            scrollActions.scrollToElement(element);
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            log.warn("Не удалось кликнуть по элементу: {}", locator);
        }
    }

    // ==================== ВАЛИДАЦИЯ ====================

    /**
     * Проверяет результат выбора чекбоксов
     */
    private void validateCheckboxResult(FilterSelectionResult result, List<String> items, boolean strict) {
        if (!result.hasSelected()) {
            throw new RuntimeException("Не удалось выбрать ни один элемент из: " + items);
        }

        if (strict && result.hasNotFound()) {
            throw new AssertionError(String.format(
                    "❌ Не все элементы выбраны!\nОжидалось (%d): %s\n✅ Выбрано (%d): %s\n❌ Не найдено (%d): %s",
                    items.size(), items,
                    result.getSelected().size(), result.getSelected(),
                    result.getNotFound().size(), result.getNotFound()
            ));
        }
    }

    // ==================== ОТЧЕТЫ ====================

    @Attachment(value = "Результаты выбора", type = "text/plain")
    public String attachSelectionResult(FilterSelectionResult result) {
        return result.toString();
    }

    @Attachment(value = "📋 Товары и цены", type = "text/plain")
    public String attachProductsReport(List<String> names, List<String> prices) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Товаров: %d | Цен: %d\n\n", names.size(), prices.size()));

        int limit = Math.min(names.size(), prices.size());
        for (int i = 0; i < limit; i++) {
            sb.append(String.format("%d. %s — %s ₽\n", i + 1, names.get(i), prices.get(i)));
        }

        return sb.toString();
    }
}