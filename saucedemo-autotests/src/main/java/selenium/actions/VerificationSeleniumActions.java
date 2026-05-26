package selenium.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.interfaces.VerificationActions;
import selenium.interfaces.WaitActions;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class VerificationSeleniumActions implements VerificationActions {

    private final WebDriver driver;
    private final WaitActions waitActions;

    @Override
    public boolean isTextPresent(By locator, String expectedText) {
        waitActions.waitForElementPresent(locator);

        List<WebElement> elements = driver.findElements(locator);
        String searchText = expectedText.toLowerCase().trim();

        return elements.stream()
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isEmpty())
                .anyMatch(text -> text.toLowerCase().contains(searchText));
    }

    @Override
    public boolean isTextContains(By locator, String partialText) {
        return isTextPresent(locator, partialText);
    }

    @Override
    public boolean isTextExactMatch(By locator, String exactText) {
        waitActions.waitForElementPresent(locator);

        List<WebElement> elements = driver.findElements(locator);

        return elements.stream()
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isEmpty())
                .anyMatch(text -> text.equalsIgnoreCase(exactText));
    }

    @Override
    public List<String> getAllTexts(By locator) {
        waitActions.waitForElementPresent(locator);

        return driver.findElements(locator).stream()
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isNotEmpty(By locator) {
        try {
            waitActions.waitForElementPresent(locator);
            List<WebElement> elements = driver.findElements(locator);
            return !elements.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasMinimumCount(By locator, int minCount) {
        try {
            waitActions.waitForElementPresent(locator);
            return driver.findElements(locator).size() >= minCount;
        } catch (Exception e) {
            return false;
        }
    }
}