package selenium.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class BrandValidationHelper {

    private final AssertionHelper assertionHelper;

    public BrandValidationHelper(AssertionHelper assertionHelper) {
        this.assertionHelper = assertionHelper;
    }

    public BrandValidationHelper() {
        this(new AssertionHelper());
    }

    public void validateInput(List<String> productNames, List<String> brands) {
        if (productNames == null) throw new AssertionFailedError("Список товаров не должен быть null");
        if (brands == null) throw new AssertionFailedError("Список брендов не должен быть null");
        if (productNames.isEmpty()) throw new AssertionFailedError("Нет товаров для проверки");
        if (brands.isEmpty()) throw new AssertionFailedError("Не указаны бренды для проверки");

        assertionHelper.validateNoNulls(productNames, "Название товара");
        assertionHelper.validateNoNulls(brands, "Бренд");
    }

    public ValidationResult validateProducts(List<String> productNames, List<String> cleanedBrands) {
        Map<String, Integer> brandHitCount = cleanedBrands.stream()
                .collect(Collectors.toMap(b -> b, b -> 0, (a, b) -> a, LinkedHashMap::new));

        List<String> invalidProducts = new ArrayList<>();
        List<String> productsWithExtraSpaces = assertionHelper.findExtraSpaces(productNames);
        List<String> productsWithMultipleBrands = new ArrayList<>();

        for (String productName : productNames) {
            List<String> foundBrands = new ArrayList<>();

            for (String brand : cleanedBrands) {
                if (assertionHelper.containsWord(assertionHelper.normalizeSpaces(productName), brand)) {
                    brandHitCount.put(brand, brandHitCount.get(brand) + 1);
                    foundBrands.add(brand);
                }
            }

            if (foundBrands.isEmpty()) {
                invalidProducts.add(productName);
            } else if (foundBrands.size() > 1) {
                productsWithMultipleBrands.add(productName);
            }
        }

        List<String> brandsNotFound = brandHitCount.entrySet().stream()
                .filter(e -> e.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> brandsFound = brandHitCount.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new ValidationResult(brandHitCount, invalidProducts, productsWithExtraSpaces,
                productsWithMultipleBrands, brandsNotFound, brandsFound);
    }

    public void logValidationResult(List<String> productNames, List<String> brands,
                                    ValidationResult result, List<String> duplicates) {
        log.info("=== СТАТИСТИКА ПО БРЕНДАМ ===");
        result.brandHitCount().forEach((brand, count) ->
                log.info("  {}: {} товаров {}", brand, count, count == 0 ? "❌ НЕ НАЙДЕН" : "✅ НАЙДЕН"));

        log.info("=== ОБЩАЯ СТАТИСТИКА ===");
        log.info("Всего товаров: {}", productNames.size());
        log.info("Проверяемые бренды: {}", brands);
        log.info("Найденные бренды: {} ({})", result.brandsFound(), result.brandsFound().size());
        log.info("Ненайденные бренды: {} ({})", result.brandsNotFound(), result.brandsNotFound().size());
        log.info("Некорректных товаров: {}", result.invalidProducts().size());

        if (!duplicates.isEmpty()) log.info("Дубликатов товаров: {}", duplicates.size());
        if (!result.productsWithExtraSpaces().isEmpty())
            log.info("Товаров с лишними пробелами: {}", result.productsWithExtraSpaces().size());
        if (!result.productsWithMultipleBrands().isEmpty())
            log.info("Товаров с несколькими брендами: {}", result.productsWithMultipleBrands().size());
    }

    public boolean hasErrors(ValidationResult result, List<String> duplicates) {
        return !result.invalidProducts().isEmpty() || !result.brandsNotFound().isEmpty() ||
                !duplicates.isEmpty() || !result.productsWithMultipleBrands().isEmpty();
    }

    public String buildErrorMessage(List<String> productNames, List<String> brands,
                                    ValidationResult result, List<String> duplicates) {
        StringBuilder sb = new StringBuilder("\n❌ ОШИБКА ВАЛИДАЦИИ\n\n");

        assertionHelper.appendWarningBlock(sb, "Найдены дубликаты товаров", duplicates, "  • %s%n", true);
        assertionHelper.appendWarningBlock(sb, "Найдены товары с несколькими брендами",
                result.productsWithMultipleBrands(), "  • %s%n", true);
        assertionHelper.appendWarningBlock(sb, "Найдены товары с лишними пробелами",
                result.productsWithExtraSpaces(), "  • \"%s\" → \"%s\"%n", false);

        if (!result.brandsNotFound().isEmpty()) {
            sb.append(String.format("⚠️  Бренды, не найденные НИ В ОДНОМ товаре: %s%n%n", result.brandsNotFound()));
        }
        if (!result.brandsFound().isEmpty()) {
            sb.append(String.format("✅ Бренды, найденные в товарах: %s%n%n", result.brandsFound()));
        }
        if (!result.invalidProducts().isEmpty()) {
            sb.append(String.format("❌ %d из %d товаров не содержат ни одного из брендов %s:%n%n",
                    result.invalidProducts().size(), productNames.size(), brands));
            for (int i = 0; i < result.invalidProducts().size(); i++) {
                sb.append(String.format("  %d. %s%n", i + 1, result.invalidProducts().get(i)));
            }
            sb.append("\n");
        }

        sb.append("📊 Детальная статистика по брендам:\n");
        result.brandHitCount().forEach((brand, count) ->
                sb.append(String.format("  %s %s: %d товаров%n", count == 0 ? "❌" : "✅", brand, count)));

        return sb.toString();
    }

    public void validatePricesInRange(List<String> prices, String minPrice, String maxPrice) {
        if (prices == null) throw new AssertionFailedError("Список цен не должен быть null");
        if (prices.isEmpty()) throw new AssertionFailedError("Список цен пуст");

        long min = Long.parseLong(minPrice.replaceAll("[^\\d]", ""));
        long max = Long.parseLong(maxPrice.replaceAll("[^\\d]", ""));

        List<String> invalidPrices = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            String priceStr = prices.get(i);

            if (priceStr == null) {
                invalidPrices.add(String.format("Позиция %d: null", i + 1));
                continue;
            }

            try {
                long price = Long.parseLong(priceStr.replaceAll("[^\\d]", ""));

                if (price < min || price > max) {
                    invalidPrices.add(String.format("Позиция %d: %s ₽ (допустимо: %s - %s)",
                            i + 1, priceStr, minPrice, maxPrice));
                }
            } catch (NumberFormatException e) {
                invalidPrices.add(String.format("Позиция %d: %s (некорректный формат)", i + 1, priceStr));
            }
        }

        if (!invalidPrices.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n❌ ОШИБКА ВАЛИДАЦИИ ЦЕН\n\n");
            sb.append(String.format("⚠️  %d из %d цен не входят в диапазон [%s - %s]:\n\n",
                    invalidPrices.size(), prices.size(), minPrice, maxPrice));

            for (String invalid : invalidPrices) {
                sb.append(String.format("  • %s%n", invalid));
            }

            sb.append(String.format("\n📊 Всего: %d | ✅ Корректных: %d | ❌ Некорректных: %d\n",
                    prices.size(), prices.size() - invalidPrices.size(), invalidPrices.size()));

            throw new AssertionFailedError(sb.toString());
        }
    }
}