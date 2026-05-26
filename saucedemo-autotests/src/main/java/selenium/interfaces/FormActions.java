package selenium.interfaces;

import org.openqa.selenium.By;

public interface FormActions {

    /**
     * Ввод текста и нажатие ENTER
     */
    void typeAndSubmit(By locator, String text);

    /**
     * Ввод текста и нажатие TAB (потеря фокуса)
     */
    void typeAndBlur(By locator, String text);

    /**
     * Нажатие ENTER в поле
     */
    void pressEnter(By locator);

    /**
     * Очистка поля через JavaScript
     */
    void clearField(By locator);

    /**
     * Установка значения через JavaScript (в обход событий)
     */
    void setValueViaJs(By locator, String value);
}
