package selenium.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",           // 1. -D параметры (высший приоритет)
        "system:env",                  // 2. Переменные окружения
        "classpath:test-config-${env}.properties",  // 3. Файл окружения
        "classpath:test-config.properties"          // 4. Файл по умолчанию
})
public interface TestConfig extends Config {

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("timeout")
    @DefaultValue("10")
    int timeout();

    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    @Key("yandex.url")
    @DefaultValue("https://market.yandex.ru/")
    String yandexUrl();

    @Key("mvideo.url")
    @DefaultValue("https://www.mvideo.ru/")
    String mvideoUrl();


}
