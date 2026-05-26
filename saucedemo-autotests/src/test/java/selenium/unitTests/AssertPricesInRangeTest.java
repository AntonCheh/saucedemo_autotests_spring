package selenium.unitTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import selenium.assertions.AssertionsWeb;
import selenium.utils.AssertionHelper;
import selenium.utils.BrandValidationHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@DisplayName("Юнит-тесты для assertPricesInRange")
class AssertPricesInRangeTest {

    private final AssertionsWeb assertionsWeb = new AssertionsWeb(
            new BrandValidationHelper(new AssertionHelper())
    );

    @Nested
    @DisplayName("✅ Успешные сценарии")
    class SuccessScenarios {

        @Test
        @DisplayName("Все цены входят в диапазон")
        void allPricesInRange() {
            List<String> prices = Arrays.asList("10000", "20000", "30000", "40000");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatCode(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Цены равны границам диапазона")
        void pricesEqualToBoundaries() {
            List<String> prices = Arrays.asList("10000", "50000");
            String minPrice = "10000";
            String maxPrice = "50000";

            assertThatCode(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Одна цена в диапазоне")
        void singlePriceInRange() {
            List<String> prices = Arrays.asList("25000");
            String minPrice = "10000";
            String maxPrice = "50000";

            assertThatCode(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Цены с пробелами и неразрывными пробелами")
        void pricesWithSpaces() {
            List<String> prices = Arrays.asList("10 000", "20 000", "30000");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatCode(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("❌ Провальные сценарии")
    class FailureScenarios {

        @Test
        @DisplayName("Цена ниже минимальной")
        void priceBelowMinimum() {
            List<String> prices = Arrays.asList("1000", "20000", "30000");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("1 из 3 цен не входят в диапазон")
                    .hasMessageContaining("1000")
                    .hasMessageContaining("5000 - 50000");
        }

        @Test
        @DisplayName("Цена выше максимальной")
        void priceAboveMaximum() {
            List<String> prices = Arrays.asList("20000", "60000", "30000");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("1 из 3 цен не входят в диапазон")
                    .hasMessageContaining("60000");
        }

        @Test
        @DisplayName("Несколько цен вне диапазона")
        void multiplePricesOutOfRange() {
            List<String> prices = Arrays.asList("1000", "20000", "60000", "500");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("3 из 4 цен не входят в диапазон")
                    .hasMessageContaining("1000")
                    .hasMessageContaining("60000")
                    .hasMessageContaining("500");
        }

        @Test
        @DisplayName("Все цены вне диапазона")
        void allPricesOutOfRange() {
            List<String> prices = Arrays.asList("100", "200", "60000", "70000");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("4 из 4 цен не входят в диапазон");
        }

        @Test
        @DisplayName("Некорректный формат цены")
        void invalidPriceFormat() {
            List<String> prices = Arrays.asList("abc", "20000");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("некорректный формат")
                    .hasMessageContaining("abc");
        }

        @Test
        @DisplayName("Null значение в списке цен")
        void nullPriceInList() {
            List<String> prices = Arrays.asList("20000", null, "30000");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("⚠️ Провальные сценарии - валидация входных данных")
    class InputValidationScenarios {

        @Test
        @DisplayName("Список цен равен null")
        void pricesListIsNull() {
            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(null, "10000", "50000"))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("Список цен не должен быть null");
        }

        @Test
        @DisplayName("Список цен пуст")
        void pricesListIsEmpty() {
            List<String> prices = Collections.emptyList();

            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(prices, "10000", "50000"))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("Список цен пуст");
        }
    }

    @Nested
    @DisplayName("📊 Проверка формата сообщения об ошибке")
    class ErrorMessageFormatScenarios {

        @Test
        @DisplayName("Точный формат сообщения об ошибке")
        void exactErrorMessageFormat() {
            List<String> prices = Arrays.asList("1000", "60000", "abc");
            String minPrice = "5000";
            String maxPrice = "50000";

            assertThatThrownBy(() -> assertionsWeb.assertPricesInRange(prices, minPrice, maxPrice))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("ОШИБКА ВАЛИДАЦИИ ЦЕН")
                    .hasMessageContaining("3 из 3 цен не входят в диапазон")
                    .hasMessageContaining("📊 Всего: 3")
                    .hasMessageContaining("✅ Корректных: 0")
                    .hasMessageContaining("❌ Некорректных: 3");
        }
    }
}