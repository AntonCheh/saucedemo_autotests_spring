package restApi.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

/**
 * Билдер для создания спецификаций RestAssured.
 */
public class ApiSpecBuilder {
    private String baseUrl;
    private String contentType = "application/json";
    private Map<String, String> headers = new HashMap<>();
    private boolean enableLogging = true;
    private boolean relaxedHTTPS = false;

    public ApiSpecBuilder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ApiSpecBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ApiSpecBuilder header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public ApiSpecBuilder headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public ApiSpecBuilder enableLogging(boolean enable) {
        this.enableLogging = enable;
        return this;
    }

    public ApiSpecBuilder relaxedHTTPS(boolean relaxed) {
        this.relaxedHTTPS = relaxed;
        return this;
    }

    public RequestSpecification build() {
        if (baseUrl == null) {
            throw new IllegalStateException("baseUrl is required");
        }

        // Нормализация URL
        String normalizedUrl = baseUrl.endsWith("/") ?
                baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(normalizedUrl)
                .setContentType(contentType);

        // Добавляем заголовки
        headers.forEach(builder::addHeader);

        // Добавляем логирование
        if (enableLogging) {
            builder.addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter());
        }

        // Настройка HTTPS
        if (relaxedHTTPS) {
            builder.setRelaxedHTTPSValidation();
        }

        return builder.build();
    }
}