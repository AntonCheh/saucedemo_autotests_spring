package selenium.helpers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import selenium.config.AppConfig;
import selenium.config.TestConfig;

import java.time.Duration;

// Эта аннотация говорит JUnit 5 использовать Spring для управления тестами.
// Она автоматически загрузит ApplicationContext из AppConfig.
@SpringJUnitConfig(classes = {AppConfig.class})
public class BaseSpringTest {

    // Spring автоматически найдет подходящий бин типа WebDriver и внедрит его сюда.
    @Autowired
    protected WebDriver driver;

    @Autowired
    protected TestConfig config;

    @BeforeEach
    public void setUp() {
        // Теперь не нужно создавать драйвер вручную.
        // Spring уже сделал это за нас.
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.timeout()));
    }

    @AfterEach
    public void tearDown() {
        // Закрываем браузер после каждого теста.
        if (driver != null) {
            driver.quit();
        }
    }
}