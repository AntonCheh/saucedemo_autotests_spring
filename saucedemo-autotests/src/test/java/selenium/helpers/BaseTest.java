package selenium.helpers;

import selenium.config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    protected TestConfig config;

    @BeforeEach
    public void setUp() {
        config = ConfigFactory.create(TestConfig.class);

        // Автоматически включаем headless, если мы в Docker
        boolean isDocker = System.getenv("DOCKER_ENV") != null;
        boolean headless = isDocker || config.headless();

        driver = createDriver(config.browser(), headless);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.timeout()));
    }

    private WebDriver createDriver(String browserType, boolean headless) {
        switch (browserType.toLowerCase()) {
            case "firefox":
                return createFirefoxDriver(headless);
            case "edge":
                return createEdgeDriver(headless);
            case "chrome":
            default:
                return createChromeDriver(headless);
        }
    }

    private ChromeOptions getChromeOptions(boolean headless) {
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

        // Устанавливаем стратегию загрузки
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        // Дополнительные capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, false);

        options.merge(caps);

        return options;
    }

    private WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = getChromeOptions(headless);

        return new ChromeDriver(options);
    }

    private WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--start-maximized");

        return new FirefoxDriver(options);
    }

    private WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--start-maximized");

        return new EdgeDriver(options);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


}