package selenium.utils;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class AssertionHelper {

    /**
     * Посимвольный ввод текста с задержкой (имитация ручного ввода)
     */
    public void typeWithDelay(WebElement element, String text, int delayMs) {
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            sleep(delayMs);
        }
    }

    /**
     * Посимвольный ввод текста с задержкой по умолчанию 100мс
     */
    public void typeWithDelay(WebElement element, String text) {
        typeWithDelay(element, text, 100);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    /**
     * Проверяет элементы списка на null
     */
    public void validateNoNulls(List<String> items, String itemType) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                throw new AssertionError(
                        String.format("%s на позиции %d не должен быть null", itemType, i + 1)
                );
            }
        }
    }

    /**
     * Нормализует пробелы в строке
     */
    public String normalizeSpaces(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("\\s+", " ").trim();
    }

    /**
     * Проверяет, содержит ли текст слово по границам слов
     */
    public boolean containsWord(String text, String word) {
        if (text == null || word == null) {
            return false;
        }
        String lowerText = text.toLowerCase();
        String lowerWord = word.toLowerCase();
        String pattern = ".*\\b" + Pattern.quote(lowerWord) + "\\b.*";
        return lowerText.matches(pattern);
    }

    /**
     * Находит дубликаты в списке
     */
    public List<String> findDuplicates(List<String> items) {
        List<String> duplicates = new ArrayList<>();
        Set<String> unique = new HashSet<>();

        for (String item : items) {
            String normalized = normalizeSpaces(item);
            if (!unique.add(normalized)) {
                duplicates.add(item);
            }
        }
        return duplicates;
    }

    /**
     * Находит элементы с лишними пробелами
     */
    public List<String> findExtraSpaces(List<String> items) {
        List<String> result = new ArrayList<>();
        for (String item : items) {
            String normalized = normalizeSpaces(item);
            if (!normalized.equals(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Строит блок предупреждения для сообщения
     */
    public void appendWarningBlock(StringBuilder sb, String title, List<String> items,
                                          String format, boolean showArrow) {
        if (!items.isEmpty()) {
            sb.append(String.format("⚠️  ВНИМАНИЕ: %s:\n", title));
            for (String item : items) {
                if (showArrow) {
                    sb.append(String.format("  • %s%n", item));
                } else {
                    sb.append(String.format(format, item, normalizeSpaces(item)));
                }
            }
            sb.append("\n");
        }
    }
}