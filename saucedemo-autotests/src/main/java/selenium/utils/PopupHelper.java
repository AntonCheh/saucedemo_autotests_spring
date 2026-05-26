package selenium.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@Slf4j
public class PopupHelper {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public PopupHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    /**
     * Закрывает все известные всплывающие окна
     */
    public void closeAllPopups() {
        // Сначала пробуем закрыть окно города (самое важное)
        closeCityPopup();

        // Затем куки
        closeCookieBanner();

        // Затем остальные
        closeByEsc();
        closeByOverlayClick();
        closeByCloseButton();
    }

    /**
     * Закрывает окно выбора города
     */
    private void closeCityPopup() {
        try {
            // Специфичные для Mvideo селекторы
            By[] cityButtons = {
                    By.xpath("//button[contains(@class, 'location-confirm')]"),
                    By.xpath("//button[contains(text(), 'Все верно')]"),
                    By.xpath("//div[contains(@class, 'location-tooltip')]//button[contains(@class, 'location-confirm')]"),
                    By.xpath("//mvid-icon[@type='close' and contains(@class, 'location-close-icon')]"),
                    By.xpath("//*[contains(@class, 'location-close-icon')]")
            };

            for (By locator : cityButtons) {
                try {
                    List<WebElement> elements = driver.findElements(locator);
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(elements.get(0)));
                        button.click();
                        log.info("✅ Окно выбора города закрыто");
                        sleep(500);
                        return;
                    }
                } catch (Exception e) {
                    // Пробуем следующий локатор
                }
            }
        } catch (Exception e) {
            log.debug("Окно выбора города не найдено");
        }
    }

    /**
     * Закрывает куки-баннер
     */
    private void closeCookieBanner() {
        try {
            // Специфичные для Mvideo селекторы
            By[] cookieButtons = {
                    By.xpath("//button[contains(@class, 'notification__button')]"),
                    By.xpath("//button[contains(text(), 'Понятно')]"),
                    By.xpath("//div[contains(@class, 'notification')]//button[contains(@class, 'notification__button')]"),
                    By.xpath("//div[contains(@class, 'notification__text-cookie')]/following-sibling::button")
            };

            for (By locator : cookieButtons) {
                try {
                    List<WebElement> elements = driver.findElements(locator);
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(elements.get(0)));
                        button.click();
                        log.info("✅ Куки-баннер закрыт");
                        sleep(500);
                        return;
                    }
                } catch (Exception e) {
                    // Пробуем следующий локатор
                }
            }
        } catch (Exception e) {
            log.debug("Куки-баннер не найден");
        }
    }

    /**
     * Закрывает окно нажатием Escape
     */
    private void closeByEsc() {
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
            sleep(300);
            log.debug("Нажат Escape");
        } catch (Exception e) {
            // Игнорируем
        }
    }

    /**
     * Закрывает окно кликом по оверлею
     */
    private void closeByOverlayClick() {
        try {
            List<WebElement> overlays = driver.findElements(By.cssSelector(
                    "[class*='overlay'], [class*='modal-backdrop'], [class*='cdk-overlay-backdrop']"
            ));
            for (WebElement overlay : overlays) {
                if (overlay.isDisplayed()) {
                    overlay.click();
                    sleep(300);
                    break;
                }
            }
        } catch (Exception e) {
            // Игнорируем
        }
    }

    /**
     * Закрывает окно кликом по кнопке закрытия
     */
    private void closeByCloseButton() {
        try {
            By[] closeButtons = {
                    By.cssSelector("[aria-label='Закрыть']"),
                    By.cssSelector("[class*='close']"),
                    By.cssSelector("mvid-icon[type='close']"),
                    By.cssSelector("[data-auto='close-button']"),
                    By.xpath("//*[contains(@class, 'close-icon')]")
            };

            for (By locator : closeButtons) {
                try {
                    List<WebElement> buttons = driver.findElements(locator);
                    for (WebElement button : buttons) {
                        if (button.isDisplayed()) {
                            button.click();
                            sleep(300);
                            log.debug("Окно закрыто через кнопку закрытия");
                            return;
                        }
                    }
                } catch (Exception e) {
                    // Пробуем следующий локатор
                }
            }
        } catch (Exception e) {
            // Игнорируем
        }
    }

    /**
     * Закрывает все всплывающие окна через JavaScript
     */
    public void closeAllPopupsAggressively() {
        String script = """
            // Закрываем окно города
            var cityConfirm = document.querySelector('button.location-confirm');
            if (cityConfirm) cityConfirm.click();
            
            var cityClose = document.querySelector('.location-close-icon');
            if (cityClose) cityClose.click();
            
            // Закрываем куки
            var cookieBtn = document.querySelector('button.notification__button');
            if (cookieBtn) cookieBtn.click();
            
            // Закрываем все модальные окна
            var modals = document.querySelectorAll('[class*="modal"], [class*="popup"], [class*="overlay"], [class*="notification"], [class*="tooltip"]');
            modals.forEach(function(modal) {
                var closeBtn = modal.querySelector('[class*="close"], [aria-label="Закрыть"], mvid-icon[type="close"]');
                if (closeBtn) closeBtn.click();
            });
            
            // Нажимаем Escape
            document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', code: 'Escape', keyCode: 27 }));
        """;

        try {
            ((JavascriptExecutor) driver).executeScript(script);
            sleep(500);
            log.info("✅ Все окна закрыты через JavaScript");
        } catch (Exception e) {
            log.warn("Не удалось закрыть окна через JavaScript: {}", e.getMessage());
        }
    }

    /**
     * Проверяет, есть ли открытые всплывающие окна
     */
    public boolean hasOpenPopups() {
        try {
            List<WebElement> popups = driver.findElements(By.cssSelector(
                    "[class*='location-tooltip'], [class*='notification'], [class*='modal']"
            ));
            return popups.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Умная очистка: пробует стандартные методы, затем JavaScript
     */
    public void smartClosePopups() {
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            closeAllPopups();
            sleep(1000);

            if (!hasOpenPopups()) {
                log.info("✅ Все всплывающие окна закрыты");
                return;
            }

            log.warn("Попытка {}: окна остались, пробуем агрессивный метод", i + 1);
            closeAllPopupsAggressively();
            sleep(1000);
        }

        if (hasOpenPopups()) {
            log.warn("⚠️ Не удалось закрыть все всплывающие окна после {} попыток", maxRetries);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}