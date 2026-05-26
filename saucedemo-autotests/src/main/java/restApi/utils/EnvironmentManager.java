package restApi.utils;

import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigCache;
import restApi.properties.BaseStandProperties;
import restApi.properties.IftProperties;

/**
 * Единый менеджер для управления окружениями.
 * Окружение выбирается через системное свойство "env".
 *
 * Использование:
 * -Denv=dev  (по умолчанию)
 * -Denv=ift
 * -Denv=prod
 */

@Slf4j
public class EnvironmentManager {

    private static final String ENV_PROPERTY = "env";  // ← единый параметр
    private static final String DEFAULT_ENV = "dev";

    private static BaseStandProperties currentConfig;
    private static String currentEnv;

    /**
     * Возвращает конфигурацию для текущего окружения
     */
    public static BaseStandProperties getConfig() {
        if (currentConfig == null) {
            currentEnv = System.getProperty(ENV_PROPERTY, DEFAULT_ENV);
            currentConfig = loadConfig(currentEnv);

            printConfigInfo();
        }
        return currentConfig;
    }

    /**
     * Загружает конфигурацию для конкретного окружения
     */
    private static BaseStandProperties loadConfig(String environment) {
        switch (environment.toLowerCase()) {
            case "ift":
                return ConfigCache.getOrCreate(IftProperties.class);
            case "dev":
            default:
                return ConfigCache.getOrCreate(BaseStandProperties.class);
        }
    }

    /**
     * Получить имя текущего окружения
     */
    public static String getCurrentEnvironment() {
        if (currentEnv == null) {
            getConfig();
        }
        return currentEnv;
    }

    /**
     * Проверить текущее окружение
     */
    public static boolean isDev() {
        return getCurrentEnvironment().equalsIgnoreCase("dev");
    }

    public static boolean isIft() {
        return getCurrentEnvironment().equalsIgnoreCase("ift");
    }

    /**
     * Получить IFT специфичные настройки
     */
    public static IftProperties getIftConfig() {
        BaseStandProperties config = getConfig();
        if (config instanceof IftProperties) {
            return (IftProperties) config;
        }
        throw new IllegalStateException("Current environment is not IFT");
    }

    /**
     * Принудительно переключить окружение
     */
    public static void switchTo(String environment) {
        System.setProperty(ENV_PROPERTY, environment);
        currentConfig = loadConfig(environment);
        currentEnv = environment;
        printConfigInfo();
    }

    private static void printConfigInfo() {
        log.info("=== Active Environment: {} ===", currentEnv.toUpperCase());
        log.info("URL: {}", currentConfig.apiBookerUrl());

        if (currentConfig instanceof IftProperties) {
            IftProperties ift = (IftProperties) currentConfig;
            log.info("IFT Special Endpoint: {}", ift.iftSpecialEndpoint());
            log.info("IFT Feature Enabled: {}", ift.isFeatureEnabled());
        }
        log.info("==========================================");
    }
}