package selenium.interfaces;

import org.openqa.selenium.By;
import selenium.models.FilterSelectionResult;

import java.util.List;

/**
 * Действия с фильтрами
 */
public interface FilterActions {

    /**
     * Выбор чекбоксов с возвратом полной информации
     */
    FilterSelectionResult selectCheckboxesWithResult(By searchField, String checkboxTemplate, List<String> items);

    /**
     * Выбор чекбоксов из списка
     */
    List<String> selectCheckboxes(By searchField, String checkboxTemplate, List<String> items);

    /**
     * Выбор радио-кнопок
     */
    void selectRadio(By radioLocator);

    /**
     * Выбор из выпадающего списка
     */
    void selectFromDropdown(By dropdown, String option);
}
