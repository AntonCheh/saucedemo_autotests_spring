package restApi.properties;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;


/**
 * Базовый интерфейс конфигурации для всех стендов (окружений).
 * Использует библиотеку Owner для загрузки свойств из различных источников.
 *
 * Источники данных (в порядке приоритета):
 * 1. Системные свойства (System.properties)
 * 2. Переменные окружения (Environment variables)
 * 3. Файл config.properties в ресурсах
 */
@Sources({
        "file:src/main/resources/config.properties",
        "system:properties",
        "system:env"
})
public interface BaseStandProperties extends Config {

    @DefaultValue("https://reqres.in")
    @Key("api.base.url")
    String apiBaseUrl();

    @DefaultValue("")
    @Key("api.key")
    String apiKey();

    @DefaultValue("https://restful-booker.herokuapp.com")
    @Key("api.booker.url")
    String apiBookerUrl();
}