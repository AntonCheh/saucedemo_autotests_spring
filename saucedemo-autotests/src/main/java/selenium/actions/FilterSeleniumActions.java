package selenium.actions;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.interfaces.FilterActions;
import selenium.interfaces.ScrollActions;
import selenium.interfaces.WaitActions;
import selenium.models.FilterSelectionResult;
import selenium.utils.AssertionHelper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FilterSeleniumActions implements FilterActions {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ScrollActions scrollActions;
    private final WaitActions waitActions;
    private final AssertionHelper assertionHelper;

    public FilterSeleniumActions(WebDriver driver, WebDriverWait wait,
                                 ScrollActions scrollActions, WaitActions waitActions,
                                 AssertionHelper assertionHelper) {
        this.driver = driver;
        this.wait = wait;
        this.scrollActions = scrollActions;
        this.waitActions = waitActions;
        this.assertionHelper = assertionHelper;
    }

    @Override
    public FilterSelectionResult selectCheckboxesWithResult(By searchField, String checkboxTemplate, List<String> items) {
        List<String> selected = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        log.info("Начало выбора чекбоксов. Запрошено элементов: {}", items.size());

        for (String item : items) {
            try {
                WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));

                // 1. Очищаем поле
                search.clear();
                waitActions.sleep(300);  // Ждем очистку

                // 2. Еще раз очищаем через JavaScript для надежности
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", search);
                waitActions.sleep(200);  // Ждем JavaScript очистку

                // 3. Кликаем на поле для фокуса
                search.click();
                waitActions.sleep(200);

                // 4. Вводим посимвольно с увеличенной задержкой
                assertionHelper.typeWithDelay(search, item, 150);  // Увеличиваем до 150мс
                waitActions.sleep(1000);  // Ждем появления результатов поиска

                By checkbox = By.xpath(String.format(checkboxTemplate, item));
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(checkbox));

                scrollActions.scrollToElement(element);
//                element.click();


                Actions actions = new Actions(driver);
                actions.moveToElement(element)  // Наводим мышь
                        .pause(200)              // Пауза
                        .click()                 // Клик
                        .perform();

                selected.add(item);

                log.debug("✅ Выбран: {}", item);
                waitActions.sleep(500);

            } catch (Exception e) {
                log.warn("❌ Не найден: {}", item);
                notFound.add(item);
            }
        }

        FilterSelectionResult result = new FilterSelectionResult(selected, notFound);
        log.info(result.getSummary());

        return result;
    }

    @Override
    public List<String> selectCheckboxes(By searchField, String checkboxTemplate, List<String> items) {
        // Делегируем вызов selectCheckboxesWithResult
        FilterSelectionResult result = selectCheckboxesWithResult(searchField, checkboxTemplate, items);
        return result.getSelected();
    }

    @Override
    public void selectRadio(By radioLocator) {
        WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(radioLocator));
        scrollActions.scrollToElement(radio);
        radio.click();
    }

    @Override
    public void selectFromDropdown(By dropdown, String option) {
        WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(dropdown));
        dropdownElement.click();

        By optionLocator = By.xpath(String.format("//option[text()='%s']", option));
        WebElement optionElement = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
        optionElement.click();
    }

    /**
     * Проверяет наличие кнопки "Показать ещё"
     */
    public boolean isShowMoreButtonPresent(By showExtra) {
        try {
            List<WebElement> buttons = driver.findElements(showExtra);
            return !buttons.isEmpty() && buttons.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Кликает по кнопке "Показать ещё"
     */
    public void clickShowMoreButton(By showExtra) {
        try {
            WebElement showMoreButton = driver.findElement(showExtra);
            scrollActions.scrollToElement(showMoreButton);
            waitActions.sleep(300);

            wait.until(ExpectedConditions.elementToBeClickable(showMoreButton));
            showMoreButton.click();

            log.debug("Клик по кнопке 'Показать ещё' выполнен");
        } catch (Exception e) {
            log.warn("Не удалось кликнуть по кнопке 'Показать ещё': {}", e.getMessage());
        }
    }
}