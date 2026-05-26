package selenium.utils;

import java.util.List;
import java.util.Map;

public record ValidationResult(Map<String, Integer> brandHitCount, List<String> invalidProducts,
                               List<String> productsWithExtraSpaces, List<String> productsWithMultipleBrands,
                               List<String> brandsNotFound, List<String> brandsFound) {
}