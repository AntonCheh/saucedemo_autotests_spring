package selenium.config;

import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.*;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.Objects;

@Configuration // Говорит, что это класс конфигурации Spring.
public class AppConfig {

    // Создаем бин конфигурации. Он будет синглтоном (один на всё приложение).
    @Bean
    @Scope("singleton")
    public TestConfig testConfig() {
        return ConfigFactory.create(TestConfig.class);
    }

    // Создаем бин WebDriver. Для каждого теста будет создаваться новый экземпляр (Prototype).
    @Bean
    @Scope("prototype")
    public WebDriver webDriver(TestConfig config) {
        // Настраиваем драйвер так же, как  в BaseTest.
        boolean isDocker = Objects.equals(System.getenv("DOCKER_ENV"), "true");
        boolean headless = isDocker || config.headless();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = getChromeOptions(headless);

        // Устанавливаем стратегию загрузки
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        // Дополнительные capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, false);

        options.merge(caps);

        return new ChromeDriver(options);
    }

    private static ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        }

        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");

        options.addArguments("--disable-notifications");  // Блокируем уведомления
        options.addArguments("--disable-popup-blocking");  // Блокируем попапы
        options.addArguments("--disable-infobars");  // Убираем инфобары

        // Блокируем запрос геолокации
        options.addArguments("--deny-permission-prompts");
        return options;
    }
}