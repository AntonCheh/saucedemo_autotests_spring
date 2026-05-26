package selenium.models;

import lombok.Getter;

import java.util.List;

@Getter
public class FilterSelectionResult {

    private final List<String> selected;
    private final List<String> notFound;
    private final int totalRequested;

    public FilterSelectionResult(List<String> selected, List<String> notFound) {
        this.selected = selected;
        this.notFound = notFound;
        this.totalRequested = selected.size() + notFound.size();
    }

    public boolean hasSelected() {
        return !selected.isEmpty();
    }

    public boolean hasNotFound() {
        return !notFound.isEmpty();
    }

    public boolean allSelected() {
        return notFound.isEmpty();
    }

    public String getSummary() {
        return String.format("Выбрано: %d/%d (не найдено: %d)",
                selected.size(), totalRequested, notFound.size());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Результаты выбора ===\n");
        sb.append(String.format("Всего запрошено: %d\n", totalRequested));
        sb.append(String.format("✅ Успешно выбрано (%d):\n", selected.size()));
        selected.forEach(item -> sb.append("   ").append(item).append("\n"));
        if (!notFound.isEmpty()) {
            sb.append(String.format("❌ Не найдено (%d):\n", notFound.size()));
            notFound.forEach(item -> sb.append("   ").append(item).append("\n"));
        }
        return sb.toString();
    }
}
