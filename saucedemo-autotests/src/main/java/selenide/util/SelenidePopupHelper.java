package selenide.util;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

@Slf4j
public class SelenidePopupHelper {

    /**
     * Закрывает все известные всплывающие окна
     */
    public void closeAllPopups() {
        closeCookieBanner();
        closeCityPopup();
        closeByEsc();
        sleep(500);
    }

    /**
     * Закрывает куки-баннер
     */
    public void closeCookieBanner() {
        try {
            By[] cookieButtons = {
                    By.xpath("//button[contains(@class, 'notification__button')]"),
                    By.xpath("//button[contains(text(), 'Понятно')]"),
                    By.xpath("//button[contains(text(), 'Принять')]"),
                    By.xpath("//button[contains(text(), 'Согласен')]"),
            };

            for (By locator : cookieButtons) {
                SelenideElement button = $(locator);
                if (button.isDisplayed()) {
                    button.click();
                    log.info("✅ Куки-баннер закрыт");
                    sleep(500);
                    return;
                }
            }
        } catch (Exception e) {
            log.debug("Куки-баннер не найден");
        }
    }

    /**
     * Закрывает окно выбора города
     */
    public void closeCityPopup() {
        try {
            By[] cityButtons = {
                    By.xpath("//button[contains(@class, 'location-confirm')]"),
                    By.xpath("//button[contains(text(), 'Все верно')]"),
                    By.xpath("//button[contains(text(), 'Да, верно')]"),
                    By.xpath("//button[contains(text(), 'Продолжить')]"),
            };

            for (By locator : cityButtons) {
                SelenideElement button = $(locator);
                if (button.isDisplayed()) {
                    button.click();
                    log.info("✅ Окно выбора города закрыто");
                    sleep(500);
                    return;
                }
            }
        } catch (Exception e) {
            log.debug("Окно выбора города не найдено");
        }
    }

    /**
     * Закрывает окно нажатием Escape
     */
    public void closeByEsc() {
        try {
            Selenide.actions().sendKeys(Keys.ESCAPE).perform();
            sleep(300);
            log.debug("Нажат Escape");
        } catch (Exception e) {
            // Игнорируем
        }
    }

    /**
     * Умное закрытие с повторными попытками
     */
    public void smartClosePopups() {
        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            closeAllPopups();
            sleep(500);

            if (!hasOpenPopups()) {
                log.info("✅ Все всплывающие окна закрыты");
                return;
            }

            log.warn("Попытка {}: окна остались", i + 1);
        }

        log.warn("⚠️ Не удалось закрыть все окна после {} попыток", maxRetries);
    }

    /**
     * Проверяет наличие открытых окон
     */
    private boolean hasOpenPopups() {
        try {
            return $(By.xpath("//div[contains(@class, 'notification')]")).isDisplayed() ||
                    $(By.xpath("//div[contains(@class, 'location-tooltip')]")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
