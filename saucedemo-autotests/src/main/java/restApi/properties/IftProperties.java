package restApi.properties;

import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

/**
 * Конфигурация для IFT стенда.
 * Расширяет базовую конфигурацию специфичными для IFT параметрами.
 *
 * @LoadPolicy(LoadType.MERGE) - объединяет свойства из всех источников
 * Приоритет: последний источник имеет высший приоритет
 */
@LoadPolicy(LoadType.MERGE)
@Sources({
        "file:src/main/resources/config.properties",      // Общая конфигурация
        "file:src/main/resources/ift.properties",         // IFT-специфичная конфигурация
        "system:properties",                               // Системные свойства
        "system:env"                                       // Переменные окружения
})
public interface IftProperties extends BaseStandProperties {

    /**
     * Возвращает специальный эндпоинт для IFT стенда.
     * Используется для тестирования дополнительных функций.
     *
     * @return путь к специальному эндпоинту (например: /api/v2/users)
     */
    @Key("ift.special.endpoint")
    String iftSpecialEndpoint();

    /**
     * Проверяет, включена ли специфичная для IFT функциональность.
     *
     * @return true - если функция включена, false - если отключена
     */
    @DefaultValue("true")
    @Key("ift.feature.enabled")
    boolean isFeatureEnabled();
}