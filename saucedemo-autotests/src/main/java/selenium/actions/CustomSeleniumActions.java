package selenium.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.interfaces.ElementActions;
import selenium.interfaces.JavaScriptActions;
import selenium.interfaces.ScrollActions;
import selenium.interfaces.WaitActions;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomSeleniumActions implements ElementActions, ScrollActions, WaitActions, JavaScriptActions {

    private final WebDriver driver;
    private final WebDriverWait wait;
    // ============ ElementActions ============

    @Override
    public Optional<String> getFirstNonEmptyText(By locator) {
        log.debug("Поиск первого непустого текста: {}", locator);

        try {
            // Ждем появления хотя бы одного элемента
            waitForElementPresent(locator);

            List<WebElement> elements = driver.findElements(locator);

            for (int i = 0; i < elements.size(); i++) {
                WebElement element = elements.get(i);

                // Скроллим к элементу если нужно
                if (!element.isDisplayed()) {
                    scrollToElement(element);
                    sleep(300);
                }

                String text = element.getText();
                if (text != null && !text.trim().isEmpty()) {
                    log.debug("Найден текст в элементе {}: {}", i, text);
                    return Optional.of(text);
                }
            }

            log.debug("Не найден непустой текст среди {} элементов", elements.size());
            return Optional.empty();

        } catch (Exception e) {
            log.error("Ошибка при получении первого текста: {}", locator, e);
            return Optional.empty();
        }
    }

    @Override
    public String getFirstElementText(By locator) {
        return getFirstNonEmptyText(locator)
                .orElseThrow(() -> new RuntimeException("Не найден текст в элементе: " + locator));
    }

    @Override
    public void clearAndType(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        clearAndType(element, text);
    }

    @Override
    public void clearAndType(WebElement element, String text) {
        try {
            // Очищаем через JavaScript (надежнее, чем element.clear())
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", element);

            // Вводим текст
            element.sendKeys(text);

            log.debug("Очищено и введено '{}' в элемент: {}", text, element);

        } catch (Exception e) {
            log.error("Не удалось очистить и ввести текст в элемент", e);
            throw new RuntimeException("Clear and type failed", e);
        }
    }

    @Override
    public void clearField(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        clearField(element);
    }

    @Override
    public void clearField(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", element);
        log.debug("Поле очищено: {}", element);
    }

    @Override
    public void click(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            log.debug("Клик по элементу: {}", locator);
        } catch (Exception e) {
            log.error("Не удалось кликнуть по элементу: {}", locator, e);
            throw new RuntimeException("Click failed: " + locator, e);
        }
    }

    @Override
    public void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
            log.debug("Клик по элементу: {}", element);
        } catch (Exception e) {
            log.error("Не удалось кликнуть по элементу", e);
            throw new RuntimeException("Click failed", e);
        }
    }

    @Override
    public void type(By locator, String text) {
        // Обычный ввод с предварительным clear()
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
        log.debug("Введен текст '{}' в элемент: {}", text, locator);
    }

    @Override
    public void type(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
        log.debug("Введен текст '{}' в элемент", text);
    }

    @Override
    public String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    @Override
    public boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public boolean isEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ============ ScrollActions ============
    @Override
    public void scrollToElement(WebElement element) {
        executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        sleep(300);
        log.debug("Прокрутка к элементу: {}", element);
    }

    @Override
    public void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        scrollToElement(element);
    }

    @Override
    public void scrollDown() {
        scrollDown(800);
    }

    @Override
    public void scrollDown(int pixels) {
        executeScript("window.scrollBy(0, " + pixels + ");");
        log.debug("Прокрутка вниз на {} пикселей", pixels);
    }

    @Override
    public void scrollToTop() {
        executeScript("window.scrollTo(0, 0);");
        log.debug("Прокрутка в начало страницы");
    }

    @Override
    public void scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
        log.debug("Прокрутка в конец страницы");
    }

    @Override
    public boolean isPageEndReached() {
        // Получаем как Number и конвертируем в long
        Number scrollPosition = (Number) ((JavascriptExecutor) driver)
                .executeScript("return window.pageYOffset + window.innerHeight;");
        Number pageHeight = (Number) ((JavascriptExecutor) driver)
                .executeScript("return document.body.scrollHeight;");

        return scrollPosition.longValue() >= pageHeight.longValue() - 100;
    }

    // ============ WaitActions ============
    @Override
    public void waitForElementVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        log.debug("Элемент видим: {}", locator);
    }

    @Override
    public void waitForElementClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        log.debug("Элемент доступен для клика: {}", locator);
    }

    @Override
    public void waitForElementPresent(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        log.debug("Элемент присутствует в DOM: {}", locator);
    }

    @Override
    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
            log.trace("Ожидание {} мс", millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Ожидание прервано", e);
        }
    }

    // ============ JavaScriptActions ============
    @Override
    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    @Override
    public void highlightElement(WebElement element) {
        String originalStyle = element.getAttribute("style");
        executeScript(
                "arguments[0].setAttribute('style', 'border: 3px solid red; background: yellow;');",
                element
        );
        sleep(500);
        executeScript("arguments[0].setAttribute('style', arguments[1]);", element, originalStyle);
    }

    @Override
    public void removeAttribute(WebElement element, String attribute) {
        executeScript("arguments[0].removeAttribute(arguments[1]);", element, attribute);
    }
}
