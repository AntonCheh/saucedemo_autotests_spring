package selenide.helpers;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeOptions;

public class BaseTestSelenide {

    @BeforeEach
    public void setUp() {
        // Автоматически загружаем драйвер
        WebDriverManager.chromedriver().setup();

        // Размер окна
        Configuration.browserSize = "1920x1080";

        // Таймауты
        Configuration.timeout = 10000;
        Configuration.pageLoadTimeout = 30000;

        // Headless
        Configuration.headless = false;

        // Настройки Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        Configuration.browserCapabilities = options;

        // Allure
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
        );
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }
}