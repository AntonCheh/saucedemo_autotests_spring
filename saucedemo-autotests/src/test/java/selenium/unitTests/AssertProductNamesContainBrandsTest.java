package selenium.unitTests;

import selenium.assertions.AssertionsWeb;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@DisplayName("Юнит-тесты для assertProductNamesContainBrands")
class AssertProductNamesContainBrandsTest {

    private final AssertionsWeb assertionsWeb = new AssertionsWeb();

    @Nested
    @DisplayName("Успешные сценарии")
    class SuccessScenarios {

        @Test
        @DisplayName("Все товары содержат бренды HP или Lenovo")
        void allProductsContainBrands() {
            List<String> productNames = Arrays.asList(
                    "1. Ноутбук Lenovo V15 82QY00RGRU (Intel Celeron, 8 ГБ, 256 ГБ SSD, Full HD, Win 11 + MS Office) Черный",
            "2. Ноутбук Lenovo IP Slim 3 15AMN8 15.6\"/Ryzen 3 7320U/8Гб/512Гб/noOS/Серый(82XQ00XLSA)",
            "3. 14\" Ноутбук HP 14-am012ur (HD) i3 5005U(2.0)/8Gb/256SSD/AMD R5 M430 2Gb/BT/Win10",
            "4. Lenovo Ноутбук 15.6\", Intel Core i5-6200U, RAM 16 ГБ, 512 ГБ SSD, Intel HD Graphics 520, Windows Pro, черный",
            "5. 14\" Ноутбук HP EliteBook 840 G6. Intel Core i5-8365U. ОЗУ 16Gb. SSD 256Gb. Windows 10 Pro. Для работы и учебы.",
            "6. Lenovo Ноутбук 15.6\", Intel Core i7-6500U, RAM 16 ГБ, 512 ГБ SSD, 500 ГБ HDD, Intel HD Graphics 520, Windows Pro, черный",
            "7. Ноутбук HP 15s-fr2504TU,15.6\", IPS, Intel i5 1135G7, DDR4 16ГБ, SSD 512ГБ, Windows 11 Pro + Office 365",
            "8. Ноутбук Lenovo Intel Celeron 2,8ГГц 2 ядер. 15,6' 1920x1080 Intel UHD Graphics Windows 11 Pro Русская раскладка");
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatCode(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Товар с очень длинным названием")
        void veryLongProductName() {
            String longName = "Ноутбук HP " + "Pavilion ".repeat(50) + "Edition";
            List<String> productNames = Arrays.asList(longName);
            List<String> brands = Arrays.asList("HP");

            assertThatCode(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Регистр брендов не влияет на поиск")
        void caseInsensitiveSearch() {
            List<String> productNames = Arrays.asList(
                    "ноутбук hp pavilion",
                    "НОУТБУК LENOVO THINKPAD",
                    "Ноутбук Hp EliteBook"
            );
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatCode(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Проверка с кириллическими брендами")
        void cyrillicBrands() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук IRU Офис 101",
                    "Ноутбук IRU Game 202"
            );
            List<String> brands = Arrays.asList("IRU");

            assertThatCode(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Проверка с брендами, содержащими пробелы")
        void brandsWithSpaces() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук Packard  Bell EasyNote", // Двойной пробел
                    "Ноутбук HP Pavilion"
            );
            List<String> brands = Arrays.asList("Packard Bell", "HP");

            // Проверяем, что Packard Bell найден (пробелы нормализованы)
            assertThatCode(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Проверка с лишними пробелами в брендах")
        void brandsWithExtraSpaces() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук Packard Bell EasyNote"
            );
            List<String> brands = Arrays.asList("Packard   Bell"); // Тройной пробел

            // Проверяем, что бренд найден (пробелы нормализованы)
            assertThatCode(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Проверка с пробелами в начале и конце")
        void brandsWithLeadingTrailingSpaces() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук HP Pavilion",
                    "Ноутбук Lenovo master"
            );
            List<String> brands = Arrays.asList("  HP  ", "  Lenovo  ");

            // Проверяем, что бренд найден (пробелы обрезаны)
            assertThatCode(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Провальные сценарии")
    class FailureScenarios {

        @Test
        @DisplayName("Все товары содержат бренды HP или Lenovo, но Витязь отсутствует")
        void allProductsContainBrands() {
            List<String> productNames = Arrays.asList(
                    "1. Ноутбук Lenovo V15 82QY00RGRU (Intel Celeron, 8 ГБ, 256 ГБ SSD, Full HD, Win 11 + MS Office) Черный",
                    "2. Ноутбук Lenovo IP Slim 3 15AMN8 15.6\"/Ryzen 3 7320U/8Гб/512Гб/noOS/Серый(82XQ00XLSA)",
                    "3. 14\" Ноутбук HP 14-am012ur (HD) i3 5005U(2.0)/8Gb/256SSD/AMD R5 M430 2Gb/BT/Win10",
                    "4. Lenovo Ноутбук 15.6\", Intel Core i5-6200U, RAM 16 ГБ, 512 ГБ SSD, Intel HD Graphics 520, Windows Pro, черный",
                    "5. 14\" Ноутбук HP EliteBook 840 G6. Intel Core i5-8365U. ОЗУ 16Gb. SSD 256Gb. Windows 10 Pro. Для работы и учебы.",
                    "6. Lenovo Ноутбук 15.6\", Intel Core i7-6500U, RAM 16 ГБ, 512 ГБ SSD, 500 ГБ HDD, Intel HD Graphics 520, Windows Pro, черный",
                    "7. Ноутбук HP 15s-fr2504TU,15.6\", IPS, Intel i5 1135G7, DDR4 16ГБ, SSD 512ГБ, Windows 11 Pro + Office 365",
                    "8. Ноутбук Lenovo Intel Celeron 2,8ГГц 2 ядер. 15,6' 1920x1080 Intel UHD Graphics Windows 11 Pro Русская раскладка"
            );
            List<String> brands = Arrays.asList("HP", "Витязь", "Lenovo");

            // ТЕПЕРЬ ТЕСТ ДОЛЖЕН ПРОВАЛИТЬСЯ С ИНФОРМАЦИЕЙ О ТОМ, ЧТО ВИТЯЗЬ НЕ НАЙДЕН
            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("Бренды, не найденные НИ В ОДНОМ товаре: [Витязь]")
                    .hasMessageContaining("Бренды, найденные в товарах: [HP, Lenovo]");
        }

        @Test
        @DisplayName("Товар с брендом Dell должен вызвать ошибку")
        void productWithDellShouldFail() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук Lenovo V15",
                    "Ноутбук Dell Inspiron", // Этот товар вызовет ошибку
                    "Ноутбук HP Pavilion",
                    "Ноутбук HP Pavilion");
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("Dell")
                    .hasMessageContaining("1 из 4 товаров");
        }

        @Test
        @DisplayName("Пустой список товаров")
        void emptyProductList() {
            List<String> productNames = Collections.emptyList();
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("Нет товаров для проверки");
        }

        @Test
        @DisplayName("Товар не содержит ни одного бренда")
        void productWithoutAnyBrand() {
            List<String> productNames = Collections.singletonList("Ноутбук Asus Rog");
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("1 из 1 товаров");
        }

        @Test
        @DisplayName("Товар с null значением")
        void productWithNullValue() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук HP Pavilion",
                    null,
                    "Ноутбук Lenovo ThinkPad"
            );
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("не должен быть null");
        }

        @Test
        @DisplayName("Список брендов с null значением")
        void brandWithNullValue() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук HP Pavilion",
                    "Ноутбук Lenovo ThinkPad"
            );
            List<String> brands = Arrays.asList("HP", null, "Lenovo");

            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("не должен быть null");
        }

        @Test
        @DisplayName("Пустой список брендов")
        void emptyBrandsList() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук HP Pavilion",
                    "Ноутбук Lenovo ThinkPad"
            );
            List<String> brands = Collections.emptyList();

            // Должен либо упасть, либо сообщить что бренды не указаны
            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class);
        }

        @Test
        @DisplayName("Проверка точного формата сообщения об ошибке")
        void exactErrorMessageFormat() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук Dell Inspiron",
                    "Ноутбук HP Pavilion",
                    "Ноутбук Apple MacBook"
            );
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("ОШИБКА ВАЛИДАЦИИ")
                    .hasMessageContaining("Бренды, найденные в товарах: [HP]")
                    .hasMessageContaining("2 из 3 товаров")
                    .hasMessageContaining("HP: 1 товаров");
        }

        @Test
        @DisplayName("Бренд является частью другого слова")
        void brandIsPartOfAnotherWord() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук HP Developer Edition",  // PHP содержит HP, но это не бренд HP
                    "Ноутбук LenovoPAD ThinkPad"
            );
            List<String> brands = Arrays.asList("HP", "Lenovo");

            // Метод НЕ должен считать "PHP" как содержащий бренд "HP"
            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("LenovoPAD");
        }

        @Test
        @DisplayName("Один товар содержит несколько брендов - должно быть предупреждение")
        void productContainsMultipleBrands() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук HP Lenovo Comparison"  // Содержит оба бренда
            );
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("товары с несколькими брендами")
                    .hasMessageContaining("HP Lenovo Comparison");
        }

        @Test
        @DisplayName("Дубликаты товаров - должно быть предупреждение")
        void duplicateProducts() {
            List<String> productNames = Arrays.asList(
                    "Ноутбук HP Pavilion",
                    "Ноутбук HP Pavilion",  // Дубликат
                    "Ноутбук Lenovo ThinkPad"
            );
            List<String> brands = Arrays.asList("HP", "Lenovo");

            assertThatThrownBy(() -> assertionsWeb.assertProductNamesContainBrands(productNames, brands))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("дубликаты товаров")
                    .hasMessageContaining("HP Pavilion");
        }

    }
}
