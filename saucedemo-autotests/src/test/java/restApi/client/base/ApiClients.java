package restApi.client.base;

import io.restassured.specification.RequestSpecification;
import restApi.client.BookingApiClient;
import restApi.client.UserApiClient;
import restApi.config.ApiSpecBuilder;
import restApi.properties.BaseStandProperties;
import restApi.utils.EnvironmentManager;

import java.util.function.Consumer;

/**
 * Утилитарный класс для быстрого создания клиентов
 */
public class ApiClients {

    private static final BaseStandProperties config = EnvironmentManager.getConfig();

    public static BookingApiClient booking() {
        RequestSpecification spec = new ApiSpecBuilder()
                .baseUrl(config.apiBookerUrl())
                .build();
        return new BookingApiClient(spec);
    }

    public static UserApiClient reqres() {
        RequestSpecification spec = new ApiSpecBuilder()
                .baseUrl(config.apiBaseUrl())
                .header("x-api-key", config.apiKey())
                .relaxedHTTPS(true)
                .build();
        return new UserApiClient(spec);
    }

    public static BookingApiClient bookingWithCustomSpec(Consumer<ApiSpecBuilder> customizer) {
        ApiSpecBuilder builder = new ApiSpecBuilder().baseUrl(config.apiBookerUrl());
        customizer.accept(builder);
        return new BookingApiClient(builder.build());
    }
}