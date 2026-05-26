package selenium.actions;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.interfaces.AdvancedActions;

@Slf4j
public class AdvancedSeleniumActions implements AdvancedActions {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;

    public AdvancedSeleniumActions(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        this.actions = new Actions(driver);
    }


    @Override
    public void hoverOver(By locator) {
        log.debug("Наведение курсора на элемент: {}", locator);

        // Ждем видимости элемента
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        // Наводим курсор
        actions.moveToElement(element).perform();

        log.debug("Курсор наведен на элемент: {}", locator);
    }

    @Override
    public void hoverOver(WebElement element) {
        log.debug("Наведение курсора на элемент: {}", element);
        actions.moveToElement(element).perform();
        log.debug("Курсор наведен");
    }
}
