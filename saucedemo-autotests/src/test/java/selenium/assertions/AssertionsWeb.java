package selenium.assertions;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import selenium.pages.effective_mobile.LoginPage;
import selenium.pages.effective_mobile.ProductsPage;
import selenium.utils.AssertionHelper;
import selenium.utils.BrandValidationHelper;
import selenium.utils.ValidationResult;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class AssertionsWeb {

    private final BrandValidationHelper brandValidationHelper;

    public AssertionsWeb(BrandValidationHelper brandValidationHelper) {
        this.brandValidationHelper = brandValidationHelper;
    }

    public AssertionsWeb() {
        this(new BrandValidationHelper(new AssertionHelper()));
    }

    @Step("Проверяем, что каждый товар содержит хотя бы один из брендов: {brands}")
    public void assertProductNamesContainBrands(List<String> productNames, List<String> brands) {
        brandValidationHelper.validateInput(productNames, brands);

        List<String> cleanedBrands = brands.stream()
                .map(brand -> brandValidationHelper.getAssertionHelper().normalizeSpaces(brand))
                .collect(Collectors.toList());

        List<String> duplicateProducts = brandValidationHelper.getAssertionHelper().findDuplicates(productNames);

        ValidationResult result = brandValidationHelper.validateProducts(productNames, cleanedBrands);

        brandValidationHelper.logValidationResult(productNames, cleanedBrands, result, duplicateProducts);

        if (brandValidationHelper.hasErrors(result, duplicateProducts)) {
            fail(brandValidationHelper.buildErrorMessage(productNames, cleanedBrands, result, duplicateProducts));
        }

        log.info("✅ УСПЕХ: Все {} товаров содержат хотя бы один из брендов {}",
                productNames.size(), cleanedBrands);
    }

    @Step("Проверяем, что все цены входят в диапазон от {minPrice} до {maxPrice}")
    public void assertPricesInRange(List<String> prices, String minPrice, String maxPrice) {
        brandValidationHelper.validatePricesInRange(prices, minPrice, maxPrice);
        log.info("✅ Все {} цен входят в диапазон [{} - {}]", prices.size(), minPrice, maxPrice);
    }

//    @Step("Проверяем, что каждый товар содержит хотя бы один из брендов: {brands}")
//    public void assertProductNamesContainBrands(List<String> productNames, List<String> brands) {
//        if (productNames == null) {
//            throw new AssertionFailedError("Список товаров не должен быть null");
//        }
//        if (brands == null) {
//            throw new AssertionFailedError("Список брендов не должен быть null");
//        }
//        if (productNames.isEmpty()) {
//            throw new AssertionFailedError("Нет товаров для проверки");
//        }
//        if (brands.isEmpty()) {
//            throw new AssertionFailedError("Не указаны бренды для проверки");
//        }
//
//        // Проверка на null внутри списка товаров
//        for (int i = 0; i < productNames.size(); i++) {
//            if (productNames.get(i) == null) {
//                throw new AssertionFailedError(
//                        String.format("Название товара на позиции %d не должно быть null", i + 1)
//                );
//            }
//        }
//
//        // Проверка на null внутри списка брендов
//        for (int i = 0; i < brands.size(); i++) {
//            if (brands.get(i) == null) {
//                throw new AssertionFailedError(
//                        String.format("Бренд на позиции %d не должен быть null", i + 1)
//                );
//            }
//        }
//
//        // Проверка на дубликаты товаров
//        List<String> duplicateProducts = new ArrayList<>();
//        Set<String> uniqueProducts = new HashSet<>();
//
//        for (String productName : productNames) {
//            String normalizedProduct = normalizeSpaces(productName);
//            if (!uniqueProducts.add(normalizedProduct)) {
//                duplicateProducts.add(productName);
//            }
//        }
//
//        // Очищаем бренды от лишних пробелов
//        List<String> cleanedBrands = new ArrayList<>();
//        for (String brand : brands) {
//            cleanedBrands.add(normalizeSpaces(brand));
//        }
//
//        // Статистика по брендам
//        Map<String, Integer> brandHitCount = new LinkedHashMap<>();
//        for (String brand : cleanedBrands) {
//            brandHitCount.put(brand, 0);
//        }
//
//        List<String> invalidProducts = new ArrayList<>();
//        List<String> productsWithExtraSpaces = new ArrayList<>();
//        List<String> productsWithMultipleBrands = new ArrayList<>();
//
//        for (String productName : productNames) {
//            String normalizedProduct = normalizeSpaces(productName);
//            if (!normalizedProduct.equals(productName)) {
//                productsWithExtraSpaces.add(productName);
//            }
//
//            boolean containsAnyBrand = false;
//            List<String> foundBrands = new ArrayList<>();
//
//            for (String brand : cleanedBrands) {
//                if (containsWord(normalizedProduct, brand)) {
//                    brandHitCount.put(brand, brandHitCount.get(brand) + 1);
//                    containsAnyBrand = true;
//                    foundBrands.add(brand);
//                }
//            }
//
//            // Проверка на множественные бренды
//            if (foundBrands.size() > 1) {
//                log.warn("⚠️ Товар содержит несколько брендов {}: {}", foundBrands, productName);
//                productsWithMultipleBrands.add(productName);
//            }
//
//            if (!containsAnyBrand) {
//                invalidProducts.add(productName);
//                log.warn("❌ Товар не содержит ни одного из брендов {}: {}", cleanedBrands, productName);
//            } else {
//                log.debug("✅ Товар содержит хотя бы один бренд: {}", productName);
//            }
//        }
//
//        // Логируем дубликаты
//        if (!duplicateProducts.isEmpty()) {
//            log.warn("⚠️ Найдены дубликаты товаров: {} товаров", duplicateProducts.size());
//            for (String product : duplicateProducts) {
//                log.warn("  • {}", product);
//            }
//        }
//
//        // Логируем товары с лишними пробелами
//        if (!productsWithExtraSpaces.isEmpty()) {
//            log.warn("⚠️ Найдены товары с лишними пробелами: {} товаров", productsWithExtraSpaces.size());
//            for (String product : productsWithExtraSpaces) {
//                log.warn("  • \"{}\" -> \"{}\"", product, normalizeSpaces(product));
//            }
//        }
//
//        // Определяем, какие бренды не найдены
//        List<String> brandsNotFound = new ArrayList<>();
//        List<String> brandsFound = new ArrayList<>();
//
//        for (Map.Entry<String, Integer> entry : brandHitCount.entrySet()) {
//            if (entry.getValue() == 0) {
//                brandsNotFound.add(entry.getKey());
//            } else {
//                brandsFound.add(entry.getKey());
//            }
//        }
//
//        // Логируем статистику
//        log.info("=== СТАТИСТИКА ПО БРЕНДАМ ===");
//        for (Map.Entry<String, Integer> entry : brandHitCount.entrySet()) {
//            String status = entry.getValue() == 0 ? "❌ НЕ НАЙДЕН" : "✅ НАЙДЕН";
//            log.info("  {}: {} товаров {}", entry.getKey(), entry.getValue(), status);
//        }
//
//        log.info("=== ОБЩАЯ СТАТИСТИКА ===");
//        log.info("Всего товаров: {}", productNames.size());
//        log.info("Уникальных товаров: {}", uniqueProducts.size());
//        log.info("Проверяемые бренды: {}", cleanedBrands);
//        log.info("Найденные бренды: {} ({})", brandsFound, brandsFound.size());
//        log.info("Ненайденные бренды: {} ({})", brandsNotFound, brandsNotFound.size());
//        log.info("Некорректных товаров: {}", invalidProducts.size());
//
//        if (!duplicateProducts.isEmpty()) {
//            log.info("Дубликатов товаров: {}", duplicateProducts.size());
//        }
//        if (!productsWithExtraSpaces.isEmpty()) {
//            log.info("Товаров с лишними пробелами: {}", productsWithExtraSpaces.size());
//        }
//        if (!productsWithMultipleBrands.isEmpty()) {
//            log.info("Товаров с несколькими брендами: {}", productsWithMultipleBrands.size());
//        }
//
//        // Формируем сообщение об ошибке
//        if (!invalidProducts.isEmpty() || !brandsNotFound.isEmpty() ||
//                !duplicateProducts.isEmpty() || !productsWithMultipleBrands.isEmpty()) {
//
//            StringBuilder errorMessage = new StringBuilder();
//            errorMessage.append("\n❌ ОШИБКА ВАЛИДАЦИИ\n\n");
//
//            // Предупреждение о дубликатах
//            if (!duplicateProducts.isEmpty()) {
//                errorMessage.append("⚠️  ВНИМАНИЕ: Найдены дубликаты товаров:\n");
//                for (String product : duplicateProducts) {
//                    errorMessage.append(String.format("  • %s%n", product));
//                }
//                errorMessage.append(String.format("   Всего дубликатов: %d\n\n", duplicateProducts.size()));
//            }
//
//            // Предупреждение о множественных брендах
//            if (!productsWithMultipleBrands.isEmpty()) {
//                errorMessage.append("⚠️  ВНИМАНИЕ: Найдены товары с несколькими брендами:\n");
//                for (String product : productsWithMultipleBrands) {
//                    errorMessage.append(String.format("  • %s%n", product));
//                }
//                errorMessage.append("   Товар должен содержать только один бренд.\n\n");
//            }
//
//            // Предупреждение о лишних пробелах
//            if (!productsWithExtraSpaces.isEmpty()) {
//                errorMessage.append("⚠️  ВНИМАНИЕ: Найдены товары с лишними пробелами:\n");
//                for (String product : productsWithExtraSpaces) {
//                    errorMessage.append(String.format("  • \"%s\" → \"%s\"%n", product, normalizeSpaces(product)));
//                }
//                errorMessage.append("   Пробелы были нормализованы при проверке.\n\n");
//            }
//
//            if (!brandsNotFound.isEmpty()) {
//                errorMessage.append(String.format("⚠️  Бренды, не найденные НИ В ОДНОМ товаре: %s%n%n", brandsNotFound));
//            }
//
//            if (!brandsFound.isEmpty()) {
//                errorMessage.append(String.format("✅ Бренды, найденные в товарах: %s%n%n", brandsFound));
//            }
//
//            if (!invalidProducts.isEmpty()) {
//                errorMessage.append(String.format("❌ %d из %d товаров не содержат ни одного из брендов %s:%n%n",
//                        invalidProducts.size(), productNames.size(), cleanedBrands));
//                for (int i = 0; i < invalidProducts.size(); i++) {
//                    errorMessage.append(String.format("  %d. %s%n", i + 1, invalidProducts.get(i)));
//                }
//                errorMessage.append("\n");
//            }
//
//            errorMessage.append("📊 Детальная статистика по брендам:\n");
//            for (Map.Entry<String, Integer> entry : brandHitCount.entrySet()) {
//                String status = entry.getValue() == 0 ? "❌" : "✅";
//                errorMessage.append(String.format("  %s %s: %d товаров%n",
//                        status, entry.getKey(), entry.getValue()));
//            }
//
//            fail(errorMessage.toString());
//        }
//
//        log.info("✅ УСПЕХ: Все {} товаров содержат хотя бы один из брендов {}",
//                productNames.size(), cleanedBrands);
//    }
//
//    /**
//     * Проверяет, содержит ли текст слово как отдельное слово (по границам слов)
//     */
//    private boolean containsWord(String text, String word) {
//        if (text == null || word == null) {
//            return false;
//        }
//
//        String lowerText = text.toLowerCase();
//        String lowerWord = word.toLowerCase();
//
//        String pattern = ".*\\b" + Pattern.quote(lowerWord) + "\\b.*";
//        return lowerText.matches(pattern);
//    }
//
//    /**
//     * Нормализует пробелы в строке (заменяет множественные пробелы на один)
//     */
//    private String normalizeSpaces(String text) {
//        if (text == null) {
//            return "";
//        }
//        return text.replaceAll("\\s+", " ").trim();
//    }

    /**
     * Формирует сообщение об ошибке с перечислением ненайденных брендов и списком товаров
     */
    private String buildNotFoundErrorMessage(Map<String, List<String>> notFoundBrandsWithProducts, List<String> allProducts) {
        StringBuilder message = new StringBuilder();
        message.append("\n❌ НЕ НАЙДЕНЫ СЛЕДУЮЩИЕ БРЕНДЫ В НАЗВАНИЯХ ТОВАРОВ:\n");
        message.append("=".repeat(60)).append("\n");

        for (Map.Entry<String, List<String>> entry : notFoundBrandsWithProducts.entrySet()) {
            String brand = entry.getKey();
            message.append("\n🔍 Бренд: \"").append(brand).append("\"\n");
            message.append("   Всего проверено товаров: ").append(allProducts.size()).append("\n");
            message.append("   Список всех товаров:\n");

            for (int i = 0; i < allProducts.size(); i++) {
                message.append(String.format("      %d. %s\n", i + 1, allProducts.get(i)));
            }
            message.append("\n");
        }

        message.append("=".repeat(60));
        return message.toString();
    }

    @Step("Проверяем, что на странице более {expectedCount} элементов товаров")
    public void assertCountGreaterThan(int expectedCount, int actualCount) {
        assertThat(actualCount)
                .as("Ожидалось, что количество товаров (%d) будет больше %d, но это не так", actualCount, expectedCount)
                .isGreaterThan(expectedCount);
    }

    @Step("Проверка перехода на страницу {expectedTitle}")
    public void assertPageExist(String expectedTitle, String actualTitle) {
        assertThat(expectedTitle).containsIgnoringCase(actualTitle);
    }

    @Step("Проверка регистрации при вводе корректных данных {user} : {priceMax}")
    public void assertLoginCorrect(WebDriver driver, String user, String password) {
        ProductsPage productsPage = new ProductsPage(driver);
        assertThat(productsPage.isPageOpened())
                .withFailMessage("Логин не удался - страница продуктов не открылась")
                .isTrue();
    }

    @Step("Проверка ошибки: {expectedError}")
    public void assertLoginError(WebDriver driver, String expectedError) {
        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.getErrorMessageText())
                .withFailMessage("Ожидалась ошибка: " + expectedError)
                .contains(expectedError);
    }

    @Step("Проверка логина с неверным паролем, ожидаем ошибку - {message}")
    public void assertLoginWrong(WebDriver driver, String message) {
        assertLoginError(driver, message);
    }

    @Step("Проверка заблокированного пользователя - {user}")
    public void assertBlockedUser(WebDriver driver, String user, String message) {
        assertLoginError(driver, message);
    }

    @Step("Проверка регистрации при оставлении пустых полей, ожидаем ошибку - {message} ")
    public void assertEmptyStrings(WebDriver driver, String message) {
        assertLoginError(driver, message);
    }
}



