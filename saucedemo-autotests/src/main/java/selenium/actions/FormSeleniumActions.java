package selenium.actions;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.interfaces.FormActions;

@Slf4j
@RequiredArgsConstructor
public class FormSeleniumActions implements FormActions {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @Override
    public void typeAndSubmit(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
        element.sendKeys(Keys.ENTER);
        log.debug("Введен текст '{}' и нажат ENTER в элемент: {}", text, locator);
    }

    @Override
    public void typeAndBlur(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
        element.sendKeys(Keys.TAB);
        log.debug("Введен текст '{}' и нажат TAB в элемент: {}", text, locator);
    }

    @Override
    public void pressEnter(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.sendKeys(Keys.ENTER);
        log.debug("Нажат ENTER в элемент: {}", locator);
    }

    @Override
    public void clearField(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", element);
        log.debug("Поле очищено через JS: {}", locator);
    }

    @Override
    public void setValueViaJs(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];", element, value
        );
        log.debug("Значение '{}' установлено через JS в элемент: {}", value, locator);
    }
}
