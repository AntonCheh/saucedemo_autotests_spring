package restApi.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import restApi.properties.BaseStandProperties;


/**
 * Конфигурация RestAssured для Restful-Booker API.
 * Создает "шаблон" или "настройки" для запросов.
 */
public class BookingApiConfig {

    private static RequestSpecification bookingRequestSpec;

    /**
     * Инициализирует спецификацию запроса для Booking API.
     * Выполняет:
     * - Установку базового URL из конфигурации
     * - Настройку Content-Type: application/json
     * - Добавление фильтров логирования запросов и ответов
     * - Нормализацию URL (удаление trailing slash)
     */
    public static void setup(BaseStandProperties config) {
        String baseUrl = config.apiBookerUrl();

        // Убираем лишний слеш если есть
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        bookingRequestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType("application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    /**
     * Возвращает настроенную спецификацию запроса.
     * Если спецификация еще не инициализирована, вызывает setup().
     *
     * @return спецификация для выполнения запросов к Booking API
     */
    public static RequestSpecification getBookingRequestSpec(BaseStandProperties config) {
        if (bookingRequestSpec == null) {
            setup(config);
        }
        return bookingRequestSpec;
    }
}